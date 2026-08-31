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
  -L"$CNA/cmake-build-javanext/modules/c-api" -lcna_c_api \
  -Wl,-rpath,"$CNA/cmake-build-javanext/modules/c-api"
CNA_PLATFORM=HEADLESS CNA_RENDERER=HEADLESS CNA_AUDIO=NULL ./build-probe/cnb_model_roundtrip
```

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
