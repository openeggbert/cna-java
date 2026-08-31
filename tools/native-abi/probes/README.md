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
they construct, they answer `is_supported` with `false`, and one of them carries a human-readable
reason. That is a family to project with its refusal intact, not one to leave out.

The four clustered create routes -- `clustered_light_set`, `clustered_light_grid`,
`clustered_light_assignment` and `clustered_shadow_policy` -- name their first parameter `game`
and document it as *"the owning game"*, and **a game handle is not what they take**. Asked with
the owned game handle and again with the callback-borrowed one, all four answer `INVALID_HANDLE`;
asked with the game's **graphics device**, all four succeed. The C API resolves the parameter
through `GetBorrowedGraphicsDevice`, so the header's name and prose are wrong and the probe is
what says so. Recorded as `JAVA-UPSTREAM-005`.
