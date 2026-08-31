# C probes

Small standalone C programs that answer a question about CNA's own behaviour before Java is
written against it. They exist because some questions cannot honestly be answered from Java: a
segfault inside CNA is not a Java exception, and a JNI layer in between only makes the answer
harder to read.

Build one into the shared `build-probe/` directory, which is not version controlled:

```sh
CNA=../../cnanext
ccache cc -std=c11 -Wall -Wextra -g -O0 \
  -I"$CNA/modules/c-api/include" \
  -o build-probe/cnb_model_roundtrip tools/native-abi/probes/cnb_model_roundtrip.c \
  -L"$CNA/cmake-build-javagl/modules/c-api" -lcna_c_api \
  -Wl,-rpath,"$CNA/cmake-build-javagl/modules/c-api"
CNA_GRAPHICS_RENDERER=HEADLESS ./build-probe/cnb_model_roundtrip
```

## Which library, and which renderer

There are two CNA builds under `../../cnanext` that this repository uses, and they answer
different questions.

`cmake-build-javanext` is the pinned single-renderer HEADLESS build every measurement before this
session was taken on. `cmake-build-javagl` is a **multi-renderer** build of the same sources --
CNA compiles several renderers in and one is chosen at runtime -- configured as:

```sh
cmake -S . -B cmake-build-javagl -G Ninja \
  -DCMAKE_BUILD_TYPE=Debug \
  -DCNA_GRAPHICS_RENDERER=HEADLESS \
  -DCNA_GRAPHICS_RENDERERS="HEADLESS;OPENGLES3;OPENGL33;OPENGL4;SOFTWARE" \
  -DCNA_PLATFORM=SDL3 -DCNA_AUDIO_PLATFORM=NULL \
  -DCNA_BUILD_C_API=ON -DCNA_CNAEXT=ON -DCNA_DEVICES=ON \
  -DCNA_ENABLE_NET=ON -DCNA_ENABLE_VIDEO=AUTO \
  -DCNA_BUILD_TESTS=OFF -DCNA_BUILD_EXAMPLES=OFF \
  -DCNA_SHARP_RUNTIME_ROOT=../sharp-runtimenext \
  -DCMAKE_C_COMPILER_LAUNCHER=ccache -DCMAKE_CXX_COMPILER_LAUNCHER=ccache
cmake --build cmake-build-javagl --target cna_c_api -j$(nproc)
```

`HEADLESS` is the default so the existing qualification behaves exactly as it did; the
`CNA_GRAPHICS_RENDERER` **environment variable** picks another one for a single run.

**Name one this build does not have and the process dies.** Not a refusal, a `SIGABRT`: the
selection is resolved while `libcna_c_api.so` is loading and throws a C++ exception there, so
nothing runs -- not `main`, not a constructor, and in a JVM not a line of Java, because
`System.loadLibrary` never returns. `renderer_selection.c` reproduces it and JAVA-UPSTREAM-017
records it. The list above is not decoration: check a renderer name against it before exporting
one, or ask `cna_graphics_renderer_copy_available_ext` from a process that already loaded
successfully.

The platform is `SDL3` rather than `HEADLESS`, because a GPU renderer needs a native window and
the headless platform has none. That means these probes open a window for the fraction of a second
they run.

**Every renderer-sensitive probe below now carries two answers**: the HEADLESS one it was written
against, and the one the GPU renderers give. Where they differ, the difference is the measurement.

## What the five renderers can do

Taken by `gpu_renderer_qualification.c` on this host -- Linux x86-64, Mesa 25.0.7, AMD Radeon 780M
(radeonsi) reached through SDL3 on Wayland.

| | HEADLESS | SOFTWARE | OPENGL4 | OPENGLES3 | OPENGL33 |
|---|---|---|---|---|---|
| GL context | none | none | desktop GL 4.x | **OpenGL ES 3.2** | **OpenGL 4.6 core** |
| compute shaders | no | no | no | **yes** | **yes** |
| indirect draw | no | no | no | yes | yes |
| storage buffers | no | no | no | yes | yes |
| compute image binding | -- | -- | -- | no | **yes** |
| GPU timer | no | no | no | yes | yes |
| GPU instance culler | no | no | no | yes | yes |
| float render targets | no | no | no | yes | yes |
| texture readback | yes | yes | yes | yes | yes |
| **render-target readback** | **no** | yes | yes | yes | yes |
| automatic exposure | no | no | no | **yes** | **yes** |
| lent caster/prepass effects | invalid | -- | -- | **valid** | **valid** |

Three things in that table are worth stating outright, because each of them contradicts an
assumption the previous qualification was written under.

**`OPENGL4` is not the compute-capable renderer, and `OPENGL33` is.** The name is the opposite of
the answer. CNA's compute, storage-buffer and indirect-draw support lives in one implementation
family, EasyGL, which is reached through the five profile names `OPENGLES2`, `OPENGLES3`,
`OPENGL33`, `WEBGL1` and `WEBGL2`; `OPENGL4` is a separate hand-rolled desktop renderer that
implements no `IComputeShaderRenderer` at all. `OPENGL33` asks SDL for a 3.3 core profile and Mesa
hands back **4.6 core**, and EasyGL asks the *runtime* context rather than the compile-time
profile, so compute is available on it.

**The dialect is GLSL ES, not desktop GLSL.** Every shader inside CNA's own engine layer opens
with `#version 300 es` or `#version 310 es`, including the compute programs behind automatic
exposure, GPU instance culling and clustered lighting. `#version 310 es` compiles on both capable
renderers; `#version 430 core` compiles only on `OPENGL33`, whose context really is desktop GL.
A projection that generated desktop GLSL would work on one of the two renderers CNA supports here
and fail on the other.

**Render-target readback is not a compute question.** `SOFTWARE` and `OPENGL4` read a cleared
render target back correctly with no compute at all, so pixel-level qualification is available
much more widely than the compute family is. Only HEADLESS refuses it.

## cnb_model_roundtrip.c

Builds a model with two bones, one part with real vertex and index bytes, and one mesh, encodes
it, parses it back and decodes it, then releases everything.

Written because JAVA-UPSTREAM-004 found `cna_content_manager_load_model` segfaulting during
teardown for any asset with a mesh part. The `.cnb` model family is a different code path, but
that was worth measuring rather than assuming before binding 33 routes against it. It prints
`PROBE OK` and exits zero on ABI 0.21.0.

It also earned its keep twice over: the first two runs failed, and taught the Java layer two
things CNA requires that no header comment states outright -- a part's declared
`vertex_stride * vertex_count` has to equal the bytes it holds, and the primitive topology of a
triangle list is 4.

## engine_layer_families.c

Asks which engine-layer families can be created in this configuration, because "the layer reports
itself available", "this family does anything without a GPU" and "this family does anything on a
HEADLESS renderer" are three different questions, and binding a family that answers
`NOT_SUPPORTED` to everything would be shipping an API nobody can call.

It runs two passes. The first asks every family with no device at all. The second creates a real
game with a graphics device manager, borrows the device inside the update callback -- the only
scope the C API lends one in -- and asks the rest there, which is the answer that decides whether
a family is worth projecting. Each family is created *and destroyed*, because "create returned
SUCCESS" alone is the weakest possible evidence and a family that cannot be released is not
usable either.

Its answer on ABI 0.21.0, HEADLESS platform and renderer, engine layer revision 2:

| Needs | Families |
|---|---|
| nothing at all | `lod_group_ext`, `frustum_culler_ext`, `light_probe_ext`, `light_probe_volume_ext`, `transparent_draw_list`, `pbr_material_extensions` |
| a real device, and works | `debug_draw`, `particle_system`, `decal_pass`, `atmospheric_sky`, all four shadow maps, `clustered_light_buffer`, `render_pipeline`, `post_process_chain`, `bloom_pass`, `tonemap_pass`, `light_probe_baker`, `environment_processor`, `hdr_display_output`, `area_light_brdf_table`, `clustered_forward_effect`, `depth_normal_prepass`, `gpu_timer` |
| more than this renderer has | `storage_buffer`, `compute_shader`, `auto_exposure_ext` answer `NOT_SUPPORTED`; `gpu_timer` and `gpu_instance_culler` create but report themselves unsupported, the culler saying why: *"this renderer has no compute shaders"* |

Three results are worth keeping in mind when reading it.

`particle_system` and `debug_draw` refuse an **invalid** device with `INVALID_HANDLE`, which is
not evidence of an absent feature -- it is invalid input, and both work fine with a real one.

`gpu_timer` and `gpu_instance_culler` are the honest shape of a family this renderer cannot do:
they construct, they answer `is_supported` with `false`, and each carries a human-readable reason
-- *"the HEADLESS renderer has no GPU timer query (GL ES needs GL_EXT_disjoint_timer_query,
desktop GL needs 3.3 or ARB_timer_query)"* and *"this renderer has no compute shaders"*. That is a
family to project with its refusal intact, not one to leave out: a profiling overlay that can say
why it has no numbers is more useful than one that vanishes.

### The same census on OPENGLES3 and OPENGL33

Re-run against the multi-renderer library, both capable renderers answer **identically**, and six
lines of the HEADLESS table change:

```text
gpu_timer supported?          no  -> yes
storage_buffer                NOT_SUPPORTED -> SUCCESS
compute_shader                NOT_SUPPORTED -> INTERNAL   (see compute_compile_contract.c)
auto_exposure_ext             NOT_SUPPORTED -> SUCCESS
cube_shadow_map supported?    no  -> yes
cube_shadow_map_begin(face 0) INTERNAL -> SUCCESS
gpu_instance_culler supported? no -> yes
```

Nothing else in the census moves at all: every family that worked on HEADLESS still works, and no
family that worked stops. That the two capable renderers agree line for line is worth as much as
the deltas -- it is what makes "this is CNA's answer" a fairer reading than "this is one driver's
answer".

`cube_shadow_map` is the one that closes an upstream finding rather than a hardware one.
`JAVA-UPSTREAM-007` recorded that its face passes refuse to open while `is_supported` answered
true; here `is_supported` answers **true** and `begin`/`end` both **succeed**, so the pair is
consistent and the finding was a renderer boundary reported through the wrong result code rather
than a contradiction in CNA.

The four clustered create routes -- `clustered_light_set`, `clustered_light_grid`,
`clustered_light_assignment` and `clustered_shadow_policy` -- name their first parameter `game`
and document it as *"the owning game"*, and **a game handle is not what they take**. Asked with
the owned game handle and again with the callback-borrowed one, all four answer `INVALID_HANDLE`;
asked with the game's **graphics device**, all four succeed. The C API resolves the parameter
through `GetBorrowedGraphicsDevice`, so the header's name and prose are wrong and the probe is
what says so. Recorded as `JAVA-UPSTREAM-005`.

## instanced_draw_refusal.c

`JAVA-UPSTREAM-006`, reproduced with no Java anywhere in the picture.

`cna_instanced_renderer_ext_draw` documents `CNA_RESULT_INVALID_STATE` for a renderer that cannot
instance with the per-instance fallback disabled, and the implementation's own comment says the
exception barrier maps the `std::logic_error` it throws to that result. The barrier
(`modules/c-api/src/CnaCApiDetail.hpp`) has no `std::logic_error` arm; the throw reaches
`catch (const std::exception&)` and the caller is told `CNA_RESULT_INTERNAL` -- which a game
cannot tell from a defect inside CNA, and which is the one refusal here a game can actually act
on.

The probe builds the whole thing in C: a game, its device, a filled three-vertex buffer, a filled
three-index buffer, a mesh part over them, an instanced renderer, a `BasicEffect`, four instances,
and one draw. Its output on ABI 0.21.0, HEADLESS:

```text
instancing supported     no
fallback enabled         no
draw with fallback off   INTERNAL (12)
header documents         INVALID_STATE (3)
draw with fallback on    SUCCESS, 4 call(s), instanced=no
```

It earned its keep on the way, too: the first version left both buffers declared but never
uploaded, and the draw was refused with *"The requested primitive range exceeds the bound index
buffer"*. The headless renderer validates a draw's range against what a buffer actually holds,
not against the count it was created with, so a probe that skips `set_data` is measuring the
wrong refusal.

`CnaCApiEngineLayer.cpp` is not the only place that throws `std::logic_error`, so this is likely
to be wider than one route; the probe pins the one CNA-Java depends on.

## cube_lut_refusal.c

`JAVA-UPSTREAM-009`, and the same shape as the one above: an exception escaping into the wrong arm
of the same barrier.

`cna_cube_lut_parse` documents `CNA_RESULT_INVALID_ARGUMENT` for text the parser refuses, and
`ParseCubeLutText` catches `CNA::CNAException` to return exactly that. `CubeLut::parse` throws
`CNA::Graphics::EngineException`, which that catch does not name and which
`CallWithExceptionBarrier` maps to `CNA_RESULT_NOT_SUPPORTED`.

Needs no device -- parsing is text -- and its output on ABI 0.21.0:

```text
well formed            SUCCESS
no LUT_3D_SIZE         NOT_SUPPORTED (6)
too few entries        NOT_SUPPORTED (6)
malformed domain line  NOT_SUPPORTED (6)
header documents       INVALID_ARGUMENT (1)
```

The consequence is worth spelling out: a typo in an artist's `.cube` file arrives at a game as
"this renderer cannot do colour grading", so a game that catches the capability refusal in order
to fall back will fall back for a file it could have rejected and told someone about.

## transparent_draw_order.c

What order the transparent draw list actually runs its callbacks in, and what its sort key
measures. Asked before any Java existed because the answer decides the shape of the projection: the
list's entries are C function pointers, so it needs a JNI trampoline, and building the hard part on
top of a guess about the ordering would be the wrong way round.

Needs no device -- the header calls it *"a pure CPU object"* and the probe confirms it -- and its
output on ABI 0.21.0:

```text
create                 0
camera position of     0  (-0.00 0.00 -0.00)
count                  0  3
submit null callback   1
submit null bounds     1
order probe            14  needs 3
order                  0  [2 1 0]
draw sorted            0  [2 1 0]
key, camera inside     0  0.0000
key, camera at origin  0  9.5000
failing draw           3  [7 8]
clear                  0  count 0
destroy                0
destroy again          2
```

Five answers came out of it, all of which the Java tests now assert:

The three boxes are submitted **nearest first** on purpose, and come back `[2 1 0]` -- farthest
first, as documented. Submitted the other way round the test would have passed against a list that
ignored the camera entirely.

The sort key is the distance to the **nearest point of the box**, not to its centre: a camera
inside a box ten units away answers `0.0000`, and one at the origin answers `9.5000` for a box
whose centre is at ten and whose half-width is a half.

A failing callback stops the draw, **its own result is what `draw_sorted` returns** -- `3`, the
`INVALID_STATE` the callback chose, not a generic failure -- and the entries after it do not run.
That is what lets the Java trampoline leave a thrown exception pending and have it surface at the
call that caused it.

`submit` refuses a null callback and null bounds with `INVALID_ARGUMENT`, and nothing else: reading
the implementation confirms there is no third refusal on this ABI, which is why the Java projection
says its ordering guard is for a future CNA rather than claiming a test can make it fail.

`destroy` twice answers `INVALID_HANDLE` rather than succeeding quietly, which is the behaviour the
Java `close()` is built to be idempotent over.

## light_probe_bake.c

Can this renderer bake a light probe, and what happens to the scene callback when it cannot?

The header says the baker measures its own capability at construction -- it captures one probe and
sees whether the readback worked -- and that the headless renderer is exactly the case that binds
an offscreen target happily and then refuses to read it. Both halves of that needed measuring
before any Java was written, because the second decides whether a JNI trampoline for the bake
routes could ever be entered here.

Runs inside a game, because the baker needs a device. Its output on ABI 0.21.0, HEADLESS:

```text
is supported            0  0
defaults                face 32, count 6, planes 0.0500..500.0000
set planes reversed     1
set planes negative     1
planes after refusals   0.5000..200.0000
face view 0             0  m11=0.000 m31=1.000 m41=-3.000
face view 6             1
bake probe              3  faces drawn 0  probe invalid
bake null callback      1
bake light              3  faces drawn 0
bake visibility         3  faces drawn 0
face size zero          1
```

The answer that shaped the projection is the fourth line from the bottom: **all three bake routes
refuse with `INVALID_STATE` and draw zero faces.** The callback is never entered on this renderer,
so a JNI trampoline for it would be code no test here could execute. `LightProbeBaker` therefore
projects the other ten routes and says why the three are missing, rather than shipping a
trampoline nobody can exercise -- while `cna_transparent_draw_list_draw_sorted`, whose callback
*does* run here, gets one.

### The same probe on OPENGLES3 and OPENGL33

Both capable renderers answer identically, and the four lines that mattered all move:

```text
is supported            0  1        (was 0)
bake probe              0  faces drawn 6   probe valid    (was INVALID_STATE, 0 faces, invalid)
bake light              0  faces drawn 48  (was INVALID_STATE, 0 faces)
bake visibility         0  faces drawn 48  (was INVALID_STATE, 0 faces)
```

**The callback is entered.** Six faces for one probe, and forty-eight for the eight probes of a
2x2x2 volume -- six per probe, which is the arithmetic a cube capture should produce and is
stronger evidence than a success code, because a baker that returned SUCCESS without capturing
would have drawn zero. The three bake routes and the scene callback they take are therefore
reachable and testable here, which is what `JAVA-EXT-008` left them unbound for.

Every refusal in the HEADLESS list survives unchanged: a reversed or negative near/far pair, a
seventh face, a zero face size and a null callback are all still refused, and `set_planes` still
leaves the previously accepted pair intact.

The rest is what the Java tests now assert. `set_planes` refuses a reversed and a negative pair
with `INVALID_ARGUMENT` and **leaves the previously accepted pair intact** -- a setter that wrote
the near plane before checking would have left `0.5000..200.0000` crossed. The six face views are
six different matrices, the seventh face is refused, and a face size of zero is refused. A null
callback answers `INVALID_ARGUMENT` rather than `INVALID_STATE`, so CNA checks its arguments before
it checks whether it can capture at all.

## shader_effect_cache.c

Can the headless renderer compile a shader at all, and does this runtime actually enforce the
shader-effect factory's borrow discipline?

The factory's whole contract is that discipline -- an acquired effect is a borrowed view, and clear
and destroy are refused while one is outstanding -- and a contract stated in a header is not
evidence that a library implements it. Its output on ABI 0.21.0, HEADLESS:

```text
compile count, fresh   0
contains before        0
acquire                0  effect valid
contains after         1
compile count after    1
clear while borrowed   3
destroy while borrowed 3
acquire again          0  same handle 0  compile count 1
acquire other name     0  compile count 2
dispose borrowed       0
clear after release    0
compile count, cleared 2
acquire empty name     1
acquire bad source     0
destroy                3
```

Six answers, all of which the Java tests now assert:

The discipline is real: `clear` and `destroy` both answer `INVALID_STATE` while a view is out, and
`clear` succeeds once it is back.

**The key is the name, not the source.** A second acquire of `tint` does not recompile -- the count
stays at one -- while the same source under the name `other` compiles again. A game editing a
shader has to change the name or clear the cache, which is the sort of thing that is obvious in
retrospect and expensive to discover in a frame budget.

Each acquire returns a **different handle** for the same cached effect (`same handle 0`), so each
is a view to dispose separately, and disposing one does not destroy the effect the factory holds.

The compile count survives `clear` -- two, not zero -- which is what makes it a measure of work
done rather than of cache size, and the one number that reveals a game recompiling every frame.

An empty name is refused with `INVALID_ARGUMENT`.

`acquire bad source` **succeeds**: this renderer has no compiler and takes any text at all. That is
why the Java test asserting it is called `thisRendererAcceptsSourceItCouldNeverRun` -- a game must
not read a successful acquire as a compiled shader.

The last line is the discipline catching the probe itself: that final `destroy` is refused because
the effect from `acquire bad source` was never released.

## effect_pass_ownership.c

Does the owning effect-pass constructor really consume its effect, and is there a failure branch
that leaves it alive?

`cna_post_process_effect_pass_create_owning` is the only consumed-ownership transfer in this part
of the engine layer, and the Java rule for one is that the owner stops owning on success and keeps
owning on failure. Both branches have to be real before either can be projected: a success that did
not actually consume would make the Java side a leak, and a failure nobody can provoke would make
the other half untestable. Its output on ABI 0.21.0:

```text
create borrowing        0
get effect              0  same as set 0  stable across calls 0
set effect to none      0
get effect after none   invalid
destroy borrowing pass  0
effect alive after      0
create owning           0
destroy consumed handle 2
destroy owning pass     0
owning, bad device      2
effect after refusal    0
get effect of blit      1
set effect of blit      1
fullscreen create       0
fullscreen draw         1
fullscreen destroy      0
```

Both branches are real. After a successful `create_owning`, destroying the effect handle answers
`INVALID_HANDLE` -- it was consumed. After one refused for a bad device, destroying the effect
answers `SUCCESS` -- the caller still owned it. The borrowing constructor is the other side of the
same coin: the effect outlives the pass that drew through it.

`get_effect` is the reason that route is **not** bound. Its second line says it: the handle is
neither the one that was set nor stable across calls, so every call mints a new one -- and the
header says the handle must not be destroyed. Calling it twice therefore leaks twice with no way to
give either back. `EffectPass` answers from the effect it retained instead, which is the same
answer for nothing.

A blit pass refuses both effect-pass questions with `INVALID_ARGUMENT`, so "is an effect pass" is
CNA's own check rather than a Java type test.

The last three lines are why the full-screen pass is not projected yet: it creates, but both draws
refuse a null source with `INVALID_ARGUMENT`, and its sampler parameter is a `CNA_SamplerState`
this binding has no value type for. It needs its own measurement before it earns a Java type.

## pbr_effect_material.c

Does the PBR effect exist on this renderer, and does a material really round-trip through it?

The PBR effect and the material it carries were the largest unbound family left, and everything
depended on the round trip: without it every value would only be checkable against its own getter,
and the material structure -- ninety-one leaves, seven texture transforms among them -- could not be
checked at all. Its output on ABI 0.21.0, HEADLESS:

```text
pbr effect create       0
skinned create          0
metallic / ior          0.250 1.750
coordinate set 3        0  1
coordinate set 7        1
texture transform 2     0 0  offset 0.50 scale 2.00,3.00 rot 0.25
extract                 0  metallic 0.250 ior 1.750 sided 1 mode 2
extract slots           coord[3]=1 transform[2] scale 2.00,3.00
apply                   0  roughness 0.125 coord[5] 1
texture create          0
set_texture then get    present 1  same handle 1
apply_material then get present 0  same handle 0
extract after apply     albedo handle invalid
equals / hash / string  1  0  14 needs 133
apply to wrong effect   1
```

Both effects construct, every value round-trips, the seventh texture slot is refused (there are
seven, indexed nought to six), and applying a material to the wrong kind of effect is refused with
`INVALID_ARGUMENT` rather than silently doing nothing.

**`JAVA-UPSTREAM-010` is the three lines in the middle.** A texture set through
`cna_pbr_effect_set_texture` comes back from `cna_pbr_effect_get_texture` as the *same handle* --
genuinely retained, not minted. A texture applied through `cna_pbr_effect_apply_material` does not
come back at all: `get_texture` says the slot is empty and `extract_material` returns an invalid
handle. The native effect does hold the texture -- `MaterialBinding.cpp`'s `ApplyTo` sets every map
-- but the C API's handle registry never learns about it, so there is no handle to hand back.

The consequence for a game is the read-modify-write: extract a material, change one number, apply
it, and every map is silently unbound. `PbrEffect.applyMaterial` therefore sets each slot a second
time through `set_texture`, which makes the registry agree with the effect; when CNA closes the gap
that becomes redundant rather than wrong.

The probe also earned its keep by being wrong first. Its initial version left the `struct_size` of
the out-parameters at zero, and `get_texture_transform_ext` refused a structure that said it was
zero bytes long -- which looked exactly like a broken getter. A versioned out-parameter is still the
caller's structure and has to be stamped before the call. The generated adapter does that on every
route, which is the whole reason this class of mistake does not reach Java.

## compute_and_storage.c

Does the compute family get anywhere on a renderer with no compute?

Worth asking twice, because the family census asked with no device and because the header says
something that sounded like an opening: *"Creation succeeds even when the source does not compile:
ask `cna_compute_shader_is_valid` and read `cna_compute_shader_copy_compile_error`."* That is the
GPU timer's shape -- a family that constructs, reports itself unsupported and says why, which is
worth projecting with its refusal intact. Asked with a real device, on ABI 0.21.0:

```text
barrier has, present    0  1
barrier has, absent     0  0
indirect draw init      0  count 0 instances 0
indirect indexed init   0  count 0
storage buffer create   6
storage buffer typed    6
compute shader create   6
```

It is not the timer's shape. `cna_compute_shader_create` and both storage-buffer constructors
answer `NOT_SUPPORTED` outright, so there is no object to ask anything of and the compile-error
route is unreachable here. The sentence in the header describes a renderer that *has* compute and
rejects the source; this one refuses before that.

### The same probe on OPENGLES3 and OPENGL33

```text
storage buffer create   0            (was NOT_SUPPORTED)
storage buffer size     256
storage buffer typed    0            (was NOT_SUPPORTED)
compute shader create   0            (was NOT_SUPPORTED)
is valid                0  1
compile error           0  0 bytes
dispatch                0
set uniform int         0
set uniform float       0
image binding supported 0  0         OPENGLES3   |   0  1  OPENGL33
barrier                 0
destroy                 0
```

Every route in the family is reachable, and the one difference between the two renderers is image
binding: GL ES 3.1 requires an immutable texture allocation this renderer does not make, and the
desktop 4.6 core context does. That is exactly the distinction the header draws when it says
having compute and being able to bind an image are different questions, and it is why the Java
projection asks rather than assumes.

This probe also earned its keep by being **wrong first**, in a way HEADLESS could never have
exposed. It read `is_valid`'s output parameter inside the same `printf` that filled it:

```c
printf("is valid %d  %d\n", (int)cna_compute_shader_is_valid(shader, &valid), (int)valid);
```

C leaves the order of a call's arguments unspecified, and this compiler evaluates them
right-to-left, so `valid` was read *before* the call that wrote it and the probe reported a
compiled shader as invalid. On HEADLESS the answer was `NOT_SUPPORTED` and the line never ran, so
the defect sat undetected for a whole qualification. Both such lines are sequenced explicitly now.

## chain_owned_pass.c

What does `cna_post_process_chain_add_owned_pass` leave behind?

It is the one route in the engine layer that invalidates a handle a caller still holds, so the Java
side had to match it exactly. What needed measuring was not the handle -- the header is clear about
that -- but the game's count of owned children, which every engine object is registered in and
which `cna_game_destroy` refuses on. Its output on ABI 0.21.0:

```text
chain create            0
pass create             0
add owned               0
pass count              1
destroy consumed handle 2
chain clear             0
pass count after clear  0
chain destroy           0
game destroy            3
```

The handover works exactly as documented: the pass is in the chain, the old handle is
`INVALID_HANDLE`, `clear` empties the chain and the chain destroys cleanly. And then the game
**cannot be destroyed** -- `INVALID_STATE`, *"All owned C child resources must be destroyed before
the game."* The route releases the handle through `GetRuntimeHandles().Release` without
decrementing the count `cna_blit_pass_create` incremented, so one hand-over makes the game
undestroyable for the rest of its life.

Recorded as `JAVA-UPSTREAM-011`, and the reason the route is **not bound**. A chain is fully usable
without it -- `add_pass` borrows, and the caller closing its own passes is an ordinary thing to
ask -- so binding a method that quietly makes shutdown impossible would be worse than the absence.
The Java suite could only ever have seen this as a teardown failure three tests later, which is why
it was worth asking in C.

## lent_handles.c

On what terms does the engine layer lend the handles it calls "borrowed"?

A dozen routes hand back an effect or a texture that belongs to something else, and their
documentation divides into two shapes. Some say exactly what the borrow is worth -- *"it keeps the
table alive while it exists, and releasing it releases only the handle"* -- and some say only
"borrowed from the map", with no release route and nothing about the lender's lifetime. Three
questions decide whether the second shape can be projected at all, and none can be read off the
declaration. Its output on ABI 0.21.0:

```text
caster effect              0  invalid, stable across calls
prepass effect             0  invalid, stable across calls
brdf texture               0  valid, fresh each call
destroy table while lent   0
release brdf texture       0
destroy table              2
skybox environment         0  valid, fresh each call
destroy skybox while lent  0
release environment        0
game destroy               0
```

Two answers, and the first corrected a reading of the header rather than the header itself. "It
keeps the table alive while it exists" is a **retaining** borrow, not a blocking one: destroying
the table while a texture handle is out succeeds, the handle stays valid because it retains the
table, and releasing it afterwards succeeds too. That is safe to project, and
`AreaLightBrdfTable.getTexture` does -- a fresh facade per call, each disposed by its caller. The
skybox's environment behaves identically, and is *not* projected only because `Skybox` already
answers that question from the reference a game gave it, for nothing.

The caster and prepass effects come back **invalid** here. The header allows it -- *"or
`CNA_INVALID_HANDLE` when unsupported"* -- and this renderer compiles no shaders, so there is no
effect to lend. There is nothing to project and nothing a test could say beyond the absence.

### The same probe on OPENGLES3 and OPENGL33

Both capable renderers answer identically, and the absent half of the measurement is now present:

```text
caster effect              0  valid, fresh each call     (was invalid)
release caster             0
destroy map while lent     3                              <- INVALID_STATE
prepass effect             0  valid, fresh each call     (was invalid)
release prepass effect     0
destroy prepass            3                              <- INVALID_STATE
brdf texture               0  valid, fresh each call     (unchanged)
destroy table while lent   0                              <- SUCCESS, unchanged
```

The two shapes are now measured side by side, and **they are opposites**. The BRDF table's texture
is a *retaining* borrow: the table can be destroyed while a handle is out, the handle keeps it
alive, and releasing the handle afterwards succeeds. A shadow map's caster effect is a *blocking*
borrow: while any lent handle is outstanding the lender's `destroy` is refused with
`INVALID_STATE`. Both lines above come from a probe that minted the handle **twice** -- once to
report it and once to ask whether it was stable -- and released only one, so the refusal is the
lender counting what it has lent rather than a one-shot flag.

That difference is the whole reason this group could not be projected from the declaration. Two
routes both documented as "borrowed" require opposite Java facades: one may outlive its lender and
one must not, and nothing but the measurement distinguishes them. `lent_effect_lifetime.c` takes
the rest of the questions -- release order, use after the lender goes, and how many handles one
lender will lend.

## pipeline_scene_callbacks.c

Does the render pipeline ever enter the scene callbacks a game registers?

The last open question in the engine layer. Two routes register a callback CNA runs inside the
frame -- one for transparent geometry, one for shadow casters -- and the transparent draw list had
already shown that a callback invoked during a call can be projected with a trampoline that leaks
nothing. These are the harder shape, registered once and invoked later, and that only matters if
they run at all. On ABI 0.21.0, HEADLESS:

```text
set transparent scene  0
set shadow scene       0
begin                  0
end                    0
transparent callback   0 call(s)
shadow callback        0 call(s)
clear transparent      0
clear shadow           0
```

Both register, both clear, and **neither is entered** by a whole frame. The reading at the time was
that this followed from the renderer: no compiled shaders, so no transparent pass and no shadow
pass to call anyone from.

### The same probe on OPENGLES3 and OPENGL33

That reading was wrong, and the GPU renderers are what say so:

```text
begin                  0
end                    0
transparent callback   0 call(s)
shadow callback        0 call(s)
```

**Identical.** A renderer that compiles shaders, runs the light-probe baker's six face captures and
reads pixels back still enters neither callback across a whole `begin`/`end`. So the blocker is not
the renderer, and "a stronger renderer will reach it" is no longer an available explanation. What
the probe drives is an empty pipeline: no geometry is submitted, no light casts, and the passes
whose bodies would call out have nothing to call about. Whether a populated scene enters them is a
different question, and one this probe does not answer -- which is why the two routes stay unbound
with a *measured* reason rather than an assumed one. See `pipeline_scene_callback_scene.c`, which
asks the populated question.

## gpu_renderer_qualification.c

What can a renderer actually do, asked of every renderer this build compiles in?

Every engine-layer measurement before this session was taken on HEADLESS, which compiles no shader
and reads back no pixel, and three families were recorded as blocked on that fact rather than on
CNA. This probe is what re-opens them: it asks the questions in the order they depend on each
other -- which renderer is really active by its own name, what it claims through
`cna_graphics_device_supports_capability`, whether a compute shader compiles and in which dialect,
whether a dispatch over a storage buffer produces the arithmetic it was asked for, whether a frame
can be rendered into a target and read back as pixels, and whether automatic exposure constructs
and measures.

Its structured lines carry a `GPUQ ` prefix and are also written to a file named on the command
line, because a real renderer prints banners on stdout and a machine-readable probe must not have
to be told apart from them.

The capability table it produces is at the top of this file. The line that matters most is not a
capability at all:

```text
compute.result   in [3 5 11 19] out [13 17 29 45] expected [13 17 29 45]
compute.semantic PASS
```

Four known integers uploaded to a storage buffer, doubled and offset by a uniform inside a compute
shader, read back and compared against the same arithmetic done in C. **Nothing short of the GPU
having run the program produces that.** A dispatch that returned `SUCCESS` without executing would
leave the output buffer at its uninitialised contents, and every earlier `SUCCESS` in the sequence
-- create, upload, bind, dispatch, barrier, read -- is individually satisfiable by a renderer that
does nothing.

The same discipline is applied to the other two families. A render target is cleared to a specific
colour and read back as pixels (`first=[12 34 56 255]`), and automatic exposure is asked to measure
a deliberately dark texture and a deliberately bright one:

```text
auto_exposure.measure         dark=SUCCESS 0.031373 bright=SUCCESS 0.941176
auto_exposure.adapt           bright=SUCCESS 0.191250 dark=SUCCESS 5.737502
```

8/255 is 0.031373 and 240/255 is 0.941176, so the meter is reporting the luminance of the frame it
was handed rather than a constant, and the adaptation moves the exposure **down** for the brighter
scene and **up** for the darker one, which is the direction the header documents. Both renderers
produce those four numbers bit for bit.

## compute_compile_contract.c

`JAVA-UPSTREAM-012`, and a question no renderer without a shader compiler could have been asked.

`cna_compute_shader_create` documents this exactly: *"Creation succeeds even when the source does
not compile: ask `cna_compute_shader_is_valid` and read `cna_compute_shader_copy_compile_error`.
That mirrors the canonical class, which records the failure rather than throwing."* That sentence
is the entire basis on which a Java `ComputeShader` would be built: a constructor that always
produces an object, and a compile log a caller reads off it.

It is not what happens. Five sources, on both capable renderers, three runs each, identical every
time:

```text
compiles               create SUCCESS   valid yes  error 0 bytes
not glsl at all        create INTERNAL  handle invalid
no version directive   create INTERNAL  handle invalid
compiles, cannot link  create INTERNAL  handle invalid
empty source           create INTERNAL  handle invalid
```

The canonical `ComputeShader` constructor (`modules/graphics-ext/src/ComputeShader.cpp`) **throws
`std::runtime_error`** when the program does not compile or does not link. `CallWithExceptionBarrier`
has no arm for it, so the throw reaches `catch (const std::exception&)` and the caller is told
`CNA_RESULT_INTERNAL` -- which a game cannot tell from a defect inside CNA, and which is the one
refusal here a game can actually act on. It is the same barrier shape as `JAVA-UPSTREAM-006` and
`JAVA-UPSTREAM-009`, on a third route.

Two consequences follow, and the second is the reason this probe was worth writing before any Java
existed.

**`cna_compute_shader_is_valid` and `cna_compute_shader_copy_compile_error` cannot answer the
question they exist for.** A shader that failed to compile has no handle, so there is nothing to
ask; a shader that has a handle always answers `valid yes` and `0 bytes`.

**The compiler's diagnostics are still reachable, through the barrier's own last message.** That is
what makes a Java projection possible at all:

```text
not glsl at all       CNA::Graphics::ComputeShader: the program did not compile:
                      CS: 0:2(1): error: illegal use of reserved word `this' ...
no version directive  ... CS: 0:0(0): error: Compute shaders require GLSL 4.30 or GLSL ES 3.10
compiles, cannot link ... Link: error: unresolved reference to function `missing'
```

So `ComputeShader.compile` surfaces the GLSL log in the exception it throws, and **still** asks
`isValid()` and reads the compile error on the success path -- not because either can fail today,
but because the day CNA makes its implementation match its header, a Java layer that trusted only
the result code would hand a game a shader that never compiled.

## pipeline_scene_callback_scene.c

Does a render pipeline enter its scene callbacks when there is actually a scene?

`pipeline_scene_callbacks.c` showed that an empty pipeline enters neither, on HEADLESS and on both
capable renderers alike, which removed the renderer explanation without supplying another one.
This probe supplies the other half: the same two callbacks, with the pipeline given the things a
transparent pass and a shadow pass need before either could have anything to do.

The answer is that the earlier probe was measuring a pipeline with both passes **switched off**,
and it is the same on all three renderers:

```text
default settings   shadows=off transparency=NONE

defaults                       shadow=0(at 0) transparent=0(at 0) shadow_pass_ran=no
shadows on                     shadow=1(at 1) transparent=0(at 0) shadow_pass_ran=yes
shadows on, sorted             shadow=1(at 1) transparent=1(at 2) shadow_pass_ran=yes
shadows on, order independent  shadow=1(at 1) transparent=1(at 2) shadow_pass_ran=yes
transparent callback fails     begin=SUCCESS  end=INVALID_STATE
second frame                   shadow=1(at 1) transparent=1(at 2)
transparent cleared            shadow=1(at 1) transparent=0(at 0)
both cleared                   shadow=0(at 0) transparent=0(at 0)
no shadow map                  shadow=0(at 0) transparent=0(at 0)
```

`RenderPipeline::begin` runs the shadow pass only `if (settings_.isShadowsEnabled() && shadowMap_
!= nullptr && drawCasters_)`, and `drawTransparentPhase` returns immediately unless the
transparency mode is something other than `None`. The default settings have shadows off and
transparency `None`, so the earlier probe's frame had nothing to call from -- **on any renderer**.
The renderer was never the gate, and "a shader-capable renderer will reach it" would have been the
wrong prediction as well as the wrong reason.

Six answers, and together they are the contract a JNI trampoline needs:

The **shadow callback runs first**, inside `begin`, and the **transparent callback second**, inside
`end`. The sequence numbers say so rather than the ordering of the source: each callback stamps a
shared counter, and it reads 1 and 2 every time.

**Once per frame each**, over two consecutive frames, so that is a rate rather than a coincidence.

**A failing callback's result reaches the caller.** The transparent callback returning
`INVALID_STATE` leaves `begin` at `SUCCESS` and makes **`end`** return `INVALID_STATE` -- the
callback's own code, not a generic failure -- which is exactly the shape
`cna_transparent_draw_list_draw_sorted` already has and which lets a Java trampoline leave a thrown
exception pending and surface it at the call that caused it.

**Both clear**, independently, and clearing the shadow *map* (passing `CNA_INVALID_HANDLE`) also
stops the shadow callback even with a callback still registered.

**Order-independent transparency really runs on the capable renderers.** On HEADLESS it falls back
and names the reason -- *"this renderer has no half-float render target, and the accumulation sums
values far outside 0..1"* -- and on both GL renderers the fallback reason is empty.

`get_last_frame_pass_count` answers **0** in every configuration, on every renderer, because no
post-process pass is enabled; it is a count of chain passes rather than of scene passes.

## lent_effect_lifetime.c

On a renderer that compiles shaders, what exactly is each of the engine layer's borrowed handles
worth?

`lent_handles.c` could only half-answer this: the caster and prepass effects came back
`CNA_INVALID_HANDLE` on HEADLESS, so there was no lifetime to measure. This probe asks four
questions of every lender, and the fourth is the one that decides the Java facade -- **is the
lender's own `destroy` refused while a lent handle is outstanding?**

Identical on OPENGLES3 and OPENGL33:

| Lent handle | valid | stable | release | lender destroy while lent | borrow |
|---|---|---|---|---|---|
| `shadow_map` caster | yes | fresh each call | SUCCESS | INVALID_STATE | blocking |
| `shadow_map` skinned caster | yes | fresh each call | SUCCESS | INVALID_STATE | blocking |
| `cascaded_shadow_map` caster | yes | fresh each call | SUCCESS | INVALID_STATE | blocking |
| `cube_shadow_map` caster | yes | fresh each call | SUCCESS | INVALID_STATE | blocking |
| `spot_shadow_map` caster | yes | fresh each call | SUCCESS | **SUCCESS** | retaining |
| `depth_normal_prepass` effect | yes | fresh each call | SUCCESS | INVALID_STATE | blocking |
| `depth_normal_prepass` skinned | yes | fresh each call | SUCCESS | INVALID_STATE | blocking |
| `clustered_forward` shader effect | yes *(also on HEADLESS)* | fresh each call | SUCCESS | INVALID_STATE | blocking |
| `clustered_forward` extensions | yes *(also on HEADLESS)* | fresh each call | SUCCESS | SUCCESS | retaining |
| `weighted_blended` accumulation | yes | fresh each call | SUCCESS | SUCCESS | retaining |
| `weighted_blended` revealage | yes | fresh each call | SUCCESS | SUCCESS | retaining |
| `ascii_pass` effect | yes *(every renderer)* | fresh each call | SUCCESS | SUCCESS | retaining |
| `color_grade_pass` LUT / volume LUT | invalid with none bound | -- | -- | -- | -- |
| `render_pipeline` shadow map | yes, and **not** the handle that was given | fresh each call | -- | -- | -- |
| `render_pipeline` scene target | invalid even while `is_using_scene_target` is true | -- | -- | -- | -- |
| `render_pipeline` skybox, none set | invalid | -- | -- | -- | -- |

Three things follow, and none of them could have been read off a declaration that says only
"borrowed".

**Two routes with the same one-line documentation need opposite Java facades.** A blocking borrow
must be given back before its lender can be closed, so its Java object has to be closed first or
the parent's `close()` fails; a retaining borrow may outlive its lender entirely.

**The four shadow maps do not agree with each other.** `cna_shadow_map_destroy`,
`cna_cascaded_shadow_map_destroy` and `cna_cube_shadow_map_destroy` all check an
`activeBorrowCount` and refuse; `cna_spot_shadow_map_destroy` has no such check and releases the
handle. Recorded as `JAVA-UPSTREAM-013`. It is an inconsistency rather than a memory-safety fault
-- see the next probe.

**The ASCII pass's effect was read wrong the first time, and the probe is what corrected it.**
It released with `cna_effect_destroy` and got `INVALID_HANDLE`, which reads exactly like a lender
refusing to take its borrow back, and the row above used to say "non-owning view" on that basis.
The route was simply the wrong one: `graphics_ext.h` says in as many words that an ASCII effect
"is not a shader `Effect` and is not accepted by the `cna_effect_*` routes", and
`cna_ascii_post_process_effect_destroy` releases it with `SUCCESS`. A refusal that comes from
asking the wrong question looks identical to one that comes from a real constraint, and the only
defence is reading the declaration of the type you actually hold.

### Five getters asked while the lender is holding something

Every case above asks a lender that holds nothing, or holds something it made itself. Five getters
lend a thing the *caller* gave the lender, and there the question that decides the Java facade is
a different one: **is the handle that comes back the same name the caller handed in, or a fresh
name for the same object?** Identical on OPENGLES3 and OPENGL33:

| Lent handle | valid | same name the caller gave? | release | lender destroy while lent |
|---|---|---|---|---|
| `skybox` environment | yes | **no**, fresh each call | SUCCESS | SUCCESS |
| `render_pipeline` skybox, one set | yes | **yes**, the same handle | -- | SUCCESS |
| `color_grade_pass` strip LUT, one set | yes | no, fresh each call | SUCCESS | SUCCESS |
| `color_grade_pass` volume LUT, one set | yes | no, fresh each call | SUCCESS | SUCCESS |
| `post_process_effect_pass` effect | yes | no, fresh each call | SUCCESS | SUCCESS |
| `clustered_forward` opaque frame | yes | no, fresh each call | SUCCESS | SUCCESS |

**A fresh name is a game child, and forgetting one makes the game undestroyable.** The first
version of this section leaked the second handle of each pair -- the one taken only to see whether
the getter is stable -- and `cna_game_destroy` answered `INVALID_STATE` at the end of the run.
Releasing every fresh name turns the same run's answer to `SUCCESS`. That is the whole argument
against a Java facade over these six: a game that reads `getEnvironment()` in a frame loop
allocates and must free a native handle per read, and the Java object already holds the thing it
was given. `cna_render_pipeline_get_skybox` is the exception that proves it -- it hands back the
identical handle, so there is nothing to release and nothing to gain.

### The volume LUT is a Texture3D, not a TextureCube

`cna_color_grade_pass_set_volume_lut` documents its argument as *"a cube with an edge between two
and `CNA_COLOR_GRADE_MAX_LUT_SIZE_EXT`"*, which reads two ways. Measured, it means a **cubical
`Texture3D`**:

| Table | result |
|---|---|
| `TextureCube`, size 8 | `INVALID_HANDLE` |
| `Texture3D` 8x8x8 | SUCCESS |
| `Texture3D` 8x8x4 | `INVALID_ARGUMENT` |
| `Texture3D` 1x1x1 | `INVALID_ARGUMENT` |
| `Texture3D` 2x2x2 | SUCCESS |
| `Texture3D` 64x64x64 | SUCCESS |

CNA-Java's `ColorGradePass.setVolumeLut` took a `TextureCube`, which can never have worked. It
survived because no test had ever bound a volume table -- the signature and its coverage were
missing together, which is the only way a signature this wrong stays wrong.

## lent_handle_use_after_lender.c

A lender that lets go could be *retaining* -- the handle keeps the object alive -- or simply
*dangling*. Only using the handle afterwards tells the two apart, and one of those answers is a
crash, so this probe does exactly **one case per process**, named on the command line, and a
non-zero exit is part of the measurement.

`brdf_texture` is the control: its header states the retaining contract outright and an earlier
probe already measured it holding, so a run where the control crashes means the probe is wrong
rather than CNA. Identical on OPENGLES3 and OPENGL33:

```text
brdf_texture          destroy table while lent SUCCESS  use after SUCCESS  release SUCCESS
spot_caster           destroy map   while lent SUCCESS  use after SUCCESS  release SUCCESS
clustered_extensions  destroy       while lent SUCCESS  use after SUCCESS  release SUCCESS
oit_accumulation      destroy       while lent SUCCESS  use after SUCCESS  release SUCCESS
ascii_effect          destroy pass  while lent SUCCESS  use after INVALID_HANDLE
```

**Nothing dangles.** Every lender that permits its own destruction really is retained by the
handle it lent, and the one that is not -- the ASCII pass's effect, which is a non-owning view --
answers `INVALID_HANDLE` afterwards rather than dereferencing freed memory. So `JAVA-UPSTREAM-013`
is a *consistency* finding: the spot shadow map is as safe as its three siblings, it simply does
not enforce the same discipline, and a caller who relies on the refusal to catch a bug will not be
caught out by that one.

That result is what makes the whole group projectable. A Java facade over a blocking borrow keeps
its parent alive and gives the handle back on `close()`; a facade over a retaining borrow may be
closed in any order. Both are safe, and the measurement says which is which per route rather than
one guess applied to twenty.

The answer is the earlier probe's, in every configuration:

```text
default settings               shadows=off transparency=NONE
defaults                       shadow=0 transparent=0
shadows on                     shadow=1(at 1) transparent=0
shadows on, sorted             shadow=1(at 1) transparent=1(at 2)
transparent callback fails     begin=SUCCESS  end=INVALID_STATE
```

## content_manager_model_teardown.c

`JAVA-UPSTREAM-004`, and the reason `CnaModel.Load` does not exist.

Loading a Model through CNA's own content manager and destroying it segfaults inside
`PartResource::~PartResource` for any asset whose meshes have parts -- which is every real model.
cnanext's own content fixtures are models with one bone and no meshes, which is why the path is
uncovered upstream.

It is a source probe rather than a note because *"still broken"* is a measurement that has to be
retaken against each CNA this repository qualifies against, and a segfault is not something a Java
test can report. Retaken on 2026-08-31 against ABI 0.21.0, on HEADLESS and on OPENGL33:

```text
game_create=0  get_graphics_device=0  content_manager_create=0
load_model=0 model=4294967303
destroying
<SIGABRT>
```

The model loads. `cna_model_destroy` does not return.

## exit_with_live_graph.c

`JAVA-UPSTREAM-014`. What happens when a process exits with a CNA game still alive?

A game that is never destroyed is not a hypothetical: a JVM exiting on an unhandled exception, a
`System.exit`, or simply a program that lets the operating system reclaim everything all leave the
native graph standing. CNA-Java has had a subprocess test for exactly that since the ownership
graph existed, and on HEADLESS the process exits zero.

On the EasyGL renderers it aborts -- `terminate called without an active exception`, SIGABRT --
and this reproduces it with no Java anywhere, one case per process because the interesting
outcomes are crashes:

| case | HEADLESS | SOFTWARE | OPENGL4 | OPENGLES3 | OPENGL33 |
|---|---|---|---|---|---|
| a live game | 0 | 0 | 0 | 0 | 0 |
| a live device manager | 0 | 0 | 0 | 0 | 0 |
| a frame run | 0 | 0 | 0 | 0 | 0 |
| a live static or dynamic vertex buffer | 0 | 0 | 0 | 0 | 0 |
| all of it on a thread that ends first | 0 | 0 | 0 | **134** | **134** |
| that thread with **no** buffer | 0 | 0 | 0 | 0 | 0 |

**Both conditions are necessary and neither alone does it.** A buffer alive at exit is fine; a
thread that ends before the process is fine; the two together abort.

The second condition is what makes this reach every Java program rather than an unusual one. The
`java` launcher runs `main` on a thread it creates, not on the process's initial thread, so the
thread that made the game and its GL context has already ended by the time the process exits --
without anyone choosing that. Narrowing from the Java side agreed exactly: a plain game with a
device and a frame exits cleanly, and adding one `VertexBuffer` -- static or dynamic, bound or
not, with a listener or without -- aborts.

## pass_support_versus_behaviour.c

`JAVA-UPSTREAM-015`. Does a post-process pass that reports itself unsupported actually decline to
run?

`cna_post_process_pass_is_supported` exists so a game can ask before it spends a frame, and the
whole value of the question is that the answer predicts the behaviour. A film-grain pass over a
flat grey frame, read back and its distinct colours counted, says whether it did:

| renderer | `is_supported` | the image | verdict |
|---|---|---|---|
| HEADLESS | no | readback refused | nothing can be said |
| SOFTWARE | no | 128,128,128, one colour | agrees with itself |
| OPENGL4 | **no** | 153,153,153, **168 colours** | **grains the frame anyway** |
| OPENGLES3 | yes | 153,153,153, 168 colours | agrees with itself |
| OPENGL33 | yes | 153,153,153, 168 colours | agrees with itself |

That is the third instance of one shape, after `JAVA-UPSTREAM-007` -- a cube shadow map that
answers `is_supported=no` and opens its face passes exactly as documented -- and
`JAVA-UPSTREAM-005`, whose routes do not take the handle their documentation names. A capability
query is only worth asking if its answer predicts something.

It is in C rather than only in Java because a Java test can show it on the renderers it happens to
be run against, and the finding is the disagreement rather than one renderer's answer.

## gpu_deep behaviours worth knowing

Three things the GPU renderers do that HEADLESS could not, measured while deciding what was worth
asserting:

**A GPU timer collects a sample and the value is not a duration.** `poll` returns true, the sample
count rises to one, and `getLastMilliseconds` answers **4294.967295** -- which is 0xFFFFFFFF
nanoseconds, a sentinel rather than a measurement, on this software GL implementation. The Java
test therefore asserts the protocol (a sample is collected and counted, and the value is not
negative) and explicitly does not claim the duration means anything. Qualifying a real duration
needs hardware whose timer query answers.

**The render pipeline runs three passes** with bloom, FXAA and HDR enabled -- on HEADLESS as well
as on the GL renderers, and with five target switches. `getLastFramePassCount` was never blocked;
the zero the scene-callback probe reported was a pipeline with nothing turned on.

**A shadow map lends its texture on every renderer**, and answers `is_supported` false only where
it cannot cast into it.

## renderer_selection.c

Which renderers does this build have, and what happens when a caller asks for one it does not?

The second half of that question was answered by accident: a qualification sweep named
`OPENGLES2`, a renderer this library was configured without, and the JVM printed
`terminate called after throwing an instance of 'System::InvalidOperationException'` and died with
signal 6. The message was a good one. The delivery was a process abort across a C ABI whose whole
contract is that failures come back as a `CNA_Result`.

**The abort is at library load, not at first call.** Run this probe with
`CNA_GRAPHICS_RENDERER=OPENGLES2` and nothing of it runs -- not `main`, not a
`__attribute__((constructor))` of the program itself. A trivial program that links the library but
references no symbol from it survives, because `--as-needed` drops the `DT_NEEDED` entry and the
library is never loaded at all; add one reference and it dies. So there is no point at which a
caller could guard it, and in a JVM `System.loadLibrary` never returns. Recorded as
`JAVA-UPSTREAM-017`.

**The API path is safe, and is the whole reason to bind this family.** Every setter refuses
cleanly:

| Call | result |
|---|---|
| `set_preferred_ext(OPENGLES2)` -- defined, not in this build | `INVALID_STATE` |
| `set_preferred_by_name_ext("OPENGLES2")` | `INVALID_STATE` |
| `set_preferred_by_name_ext("NOT_A_RENDERER")` | `INVALID_ARGUMENT` |
| `set_preferred_ext(9999)` -- outside the table | `INVALID_ARGUMENT` |
| `set_preferred_by_name_ext("HEADLESS")` | SUCCESS |
| `set_fallback_chain_ext(chain, 1)` with an undefined identity | `INVALID_ARGUMENT` |
| `set_fallback_chain_ext(NULL, 1)` | `INVALID_ARGUMENT` |
| `set_fallback_chain_ext(NULL, 0)` | SUCCESS |
| any setter once a renderer exists | `INVALID_STATE` |

`try_parse_name_ext` is case-insensitive, treats an unrecognised name as an answer rather than a
failure, and parses names for renderers this build does not have -- which is right: parsing a name
and having the renderer are different questions.

**Creating a device resets three query routes.** `JAVA-UPSTREAM-018`. `identity` mode asks them
on a process that touches the selection not at all, before and after one `GraphicsDevice`:

| Route | before a device | after a device |
|---|---|---|
| `get_available_count_ext` | SUCCESS, **5** | SUCCESS, **0** -- while `copy_available_ext` still says 5 |
| `get_selected_ext` | SUCCESS, **the renderer the run asked for** | SUCCESS, **`UNKNOWN`** |
| `get_is_latched_ext` | SUCCESS, not latched | SUCCESS, **still not latched** -- it never reports the state it exists to report |
| `get_active_ext` | `INVALID_STATE`, correctly | SUCCESS, **the running renderer** -- correct |
| `copy_available_ext` | 5 identities | 5 identities -- correct |

**This corrects an earlier reading of the same family, and the correction is the more useful
finding.** Asked from `main`, which calls `set_preferred_by_name` and
`reset_selection_for_tests_ext` before it gets there, every one of these looked broken and the
conclusion drawn was "write-only". They were being asked after the probe had rearranged the state
they report. Measured without that, they are correct until a device exists and three of them are
reset by creating one -- a narrower defect with a specific trigger, rather than five unrelated
ones. A probe that mutates what it is about to measure will always find something.

**Three routes named `current` report the compile-time default, not the running renderer.** On a
build configured `CNA_GRAPHICS_RENDERER=HEADLESS` with five renderers compiled in, running under
`CNA_GRAPHICS_RENDERER=OPENGL33`:

| Route | answers |
|---|---|
| `cna_graphics_renderer_copy_current_name` | `"HEADLESS"` -- and its declaration does say it matches the build option, so this one is honest |
| `cna_graphics_renderer_get_current_type` | `HEADLESS` |
| `cna_graphics_backend_get_current_category` | `Diagnostic`, HEADLESS's category |
| `cna_graphics_backend_get_current_maturity` | `Supported`, HEADLESS's maturity |
| `cna_graphics_renderer_get_active_ext` | **`OPENGL33`** |
| `cna_graphics_device_copy_renderer_name` | **`"OPENGL33"`** |

The two that are right are the two that ask about something real -- the selection that happened,
and the device in hand. The word "current" in the other three means "the one this build was
configured with", which on a single-renderer build is the same thing and on this one is not.

**Per-identity classification works for every identity, compiled in or not:**

| Identity | category | maturity |
|---|---|---|
| HEADLESS | Diagnostic | Supported |
| SOFTWARE | Software | Experimental |
| OPENGL33, OPENGLES3, OPENGLES2, VULKAN | Native | Production |
| OPENGL4 | Native | Supported |
| STUB | Diagnostic | Supported |
| UNKNOWN | `INVALID_ARGUMENT` | `INVALID_ARGUMENT` |

The enumeration itself is sound: `copy_available_ext` supports the zero-capacity probe, writes no
partial result when the buffer is one element short, and lists `HEADLESS OPENGLES3 OPENGL33
OPENGL4 SOFTWARE` -- which is what the `CNA_GRAPHICS_RENDERERS` cache entry says, read at runtime
rather than out of `CMakeCache.txt`. `get_is_available_ext` agrees with it identity by identity
and refuses `UNKNOWN` and out-of-range values. The four fallback reasons name themselves
`NotCompiledIn`, `ProbeUnavailable`, `InitializationFailed` and `WindowKindConflict`.

So CNA-Java binds eighteen of the family's twenty-two routes, including `get_active_ext`, and
`GraphicsRenderer.available()` sizes its buffer with the zero-capacity probe rather than the count
route -- which is what keeps it working after a device exists.

## shader_effect_uniform_binding.c

`JAVA-UPSTREAM-016`. When does a uniform set on a `ShaderEffect` actually reach the shader?

`cna_shader_effect_set_uniform_*` answers `SUCCESS` whatever else is going on, and on CNA's EasyGL
renderer the value is silently discarded unless the effect's own GL program happens to be the
current one. `EasyGLEffectRenderer::SetUniformFloat` and its eight siblings ask for a uniform
location and write to it without binding first, and `glUniform*` writes to whichever program is
current.

A fragment shader that writes nothing but a uniform, drawn into a render target and read back:

| renderer | uniform then apply | apply then uniform |
|---|---|---|
| HEADLESS | readback refused | readback refused |
| SOFTWARE | 0,255,0,255 (the source, unshaded) | 0,255,0,255 |
| OPENGL4 | **0,0,0,255** | **255,0,0,255** |
| OPENGLES3 | **0,0,0,0** | **255,0,0,255** |
| OPENGL33 | **0,0,0,0** | **255,0,0,255** |

The same renderer does it correctly one file over: `EasyGLComputeShaderRenderer::SetUniformInt`
opens with `program_.use()`. Two uniform setters in one renderer, one of which works from a cold
start and one of which does not -- which is what makes this a defect rather than a contract. CNA's
own passes are unaffected because they apply their effect as part of drawing; a game reaching the
routes directly is not, and `ShaderEffect.apply()` is where that is written down.

It also settles what a custom full-screen shader has to look like. The vertex program must match
what the pass feeds it -- `aPos`, `aTexCoord` and `aColor` at locations nought, one and two, plus a
`projection` uniform -- which is the eight lines every lens pass inside CNA shares. A shader that
names its attributes anything else compiles, reports itself valid, and draws nothing.
