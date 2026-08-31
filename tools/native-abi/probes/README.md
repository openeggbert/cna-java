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
