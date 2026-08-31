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

Asks which engine-layer families can be created in this HEADLESS/HEADLESS configuration, because
"the layer reports itself available" and "this family does anything without a GPU" are different
questions, and binding a family that answers NOT_SUPPORTED to everything would be shipping an API
nobody can call.

Its answer on ABI 0.21.0: the layer is present at revision 2, `cna_lod_group_ext_create` succeeds
with no device at all, and both `cna_debug_draw_create` and `cna_particle_system_create` refuse an
invalid device -- so those two want a real one and belong in a test that runs inside a `Game`.
That is why the LOD group was the first engine-layer family projected.
