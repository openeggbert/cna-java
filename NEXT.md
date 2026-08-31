# CNA-Java continuation handoff

**Updated:** 2026-09-01

The **complete XNA 4.0 runtime superset** is structurally at zero diagnostics, the native boundary
is qualified against the live sibling dependencies **on five renderers**, and the extension census
is at **`ACTIONABLE_LOCAL = 0`**: every unbound CNA route now says why it exists *and*, separately,
what stops it being bound -- and no remaining route is stopped by anything inside this repository.

Read `plan.md`, this file, `docs/backlog.json`, `docs/runtime-capabilities.json`,
`tools/native-abi/probes/README.md` and `docs/cna-c-api-coverage-summary.json` before new work.

## Repository and dependencies

Writable: this repository and `../cna-java-template`. `../../cnanext` and `../../sharp-runtimenext`
are dependencies and were **not** modified: both working trees are clean and neither has a commit
from this session. The pre-existing untracked `out` entry in this repository's root -- an ILDASM
dump from 2026-08-23, not this session's -- is untouched.

```text
cnanext HEAD            96b56b0e4c281fe53db92264f7bedf665de59625 (branch next)
sharp-runtimenext HEAD  4a49afb0cfe6a41e6e0af0bb62dc5175976731bb (branch next)
ABI                     0.21.0
C API inventory SHA-256 b485d17b1df9a86c6099700baedcd435c8a3f07487af8741b01d8c471956bfe7
```

**`cnanext` moved during this session** -- three commits, all XNB content readers (`SAMPLE-077`,
`SAMPLE-060`, `SAMPLE-073`) -- so `cmake-build-javagl` was rebuilt from that HEAD and everything
below was re-measured against it. The C contract itself did not move: same ABI, same 4,054
declarations, same inventory hash. So nothing here is explained by the headers having changed.

## The native library this qualifies against

`cnanext/cmake-build-javagl` is a **multi-renderer** build of the same sources -- CNA compiles
several renderers in and one is chosen before the first `GraphicsDevice`:

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

`HEADLESS` is the default, so a run that names nothing behaves as the earliest qualifications did.
`CNA_GRAPHICS_RENDERER` picks another for one run, and the build forwards it, `DISPLAY`,
`SDL_VIDEODRIVER` and `WAYLAND_DISPLAY` into the test JVM -- a test worker inherits the Gradle
daemon's environment rather than the shell's, so without that a run redirected to a virtual display
would still open real windows on someone's desktop.

```sh
CNA_NATIVE_LIBRARY=../../cnanext/cmake-build-javagl/modules/c-api/libcna_c_api.so \
CNA_GRAPHICS_RENDERER=OPENGL33 DISPLAY=:91 SDL_VIDEODRIVER=x11 ./gradlew check
```

## Exact state

```text
SELECTED PROFILE   TOTAL_DIAGNOSTICS=0  ALLOWLIST_ENTRIES=0  REFERENCE 257 types / 2964 members
FULL PROFILE       TOTAL_DIAGNOSTICS=0  ALLOWLIST_ENTRIES=0  REFERENCE 331 types / 3640 members
                   TARGET_TYPES=340     TARGET_MEMBERS=4022

NATIVE (purpose axis)
CANONICAL_FUNCTIONS=4054   BOUND_FUNCTIONS=2774
XNA_BACKING=986  JAVA_INTERNAL_ONLY=11  CNA_EXTENSION_CANDIDATE=1850
DEFERRED_RUNTIME=321  NOT_USEFUL_IN_JAVA=886  UNMAPPED_REQUIRES_REVIEW=0

NATIVE (binding axis)
BOUND=2774  DEFERRED_TRACKED=320  DELIBERATE_NON_BINDING=938  BLOCKED_UPSTREAM=22
ACTIONABLE_LOCAL=0  UNREVIEWED=0
BOUND_BUT_UNREACHED=0  BOUND_WITHOUT_JAVA_CALL_SITE=0  RULE_PROBLEMS=0
EXTENSION_CENSUS=73

LIBRARY_SYMBOL_CHECK=PASS (2774/2774)   NATIVE_TOOL_TESTS=179

TESTS=593 SUITES=106 FAILURES=0 ERRORS=0 SKIPPED=0
  -- on each of HEADLESS, SOFTWARE, OPENGL4, OPENGLES3 and OPENGL33
  -- and clean under -Xcheck:jni on all five (./gradlew test -PcheckJni)
TEMPLATE  60-frame smoke, --extensions-smoke, 600-frame stability: all pass
```

## The census, and the mistake it exists to stop

The census is `EXTENSION_CENSUS`: unbound routes whose **purpose** is a CNA capability outside XNA
4.0. It went **394 -> 73** this session. `ACTIONABLE_LOCAL` -- routes nothing outside this
repository blocks -- is **0**, and `nativeCensusCheck` fails the build at any other value.

What made that number movable was splitting one field into two. Until this session a rule carried
a single `reason`, and a hundred and forty-three routes carried text explaining *why the route is a
CNA extension rather than XNA* -- which reads like an explanation and answers a question nobody
asked about binding. `classification`/`purposeReason` now answer why a route exists;
`bindingStatus`/`bindingReason`/`evidence` answer, separately, why it is not bound.

**Five times this session a recorded reason turned out to be about something other than the route
it was attached to, and each one had been true when it was written.** They are worth reading before
the next census is trusted:

1. `JAVA-EXT-007`'s eighty skinning routes: "a clip enters a skinned model only through a
   descriptor pointer graph the generator refuses". True about the generator. A shape a generator
   cannot derive is not a lifetime nobody knows -- the marshaller was written by hand.
2. `JAVA-NATIVE-011`'s fourteen: the same shape one level up.
3. `JAVA-UPSTREAM-004`'s twelve glTF import-report routes: "the diagnostics belong to a model CNA
   imported, and the loader is blocked". Wrong about direction -- every one takes a
   `CNA_ModelHandle`, which `CnaModel.From` produces, so writing a report on a Java-built model is
   meaningful and reading it back then round-trips.
4. `cna_lod_group_ext_select`: "nothing to return by construction, because the Java
   `ModelMeshPart` is a managed object with no native handle". That stopped being true earlier in
   this same session, when `CnaModelMeshPartHandle` was added.
5. `cna_model_get_content_tag_dictionary_ext`: grouped with two routes that really do hand back a
   `void*`, and it does not -- it lends an owned `CNA_ObjectDictionary`. Correcting it did not make
   it bindable; it moved it from a decision to a block, and the census went **up**.

Two of those are now mechanised, and both checks are in `tools/native-abi/coverage.py` with
mutation tests behind them:

- **`STALE_BLOCKER_RULE_DECIDES_NOTHING`** -- a `BLOCKED_*` rule that is the first match for no
  unbound route decides nothing while still reading as a live reason. The pre-existing check only
  looked at rules that name symbols; both glTF rules matched by prefix and by substring, which is
  exactly why they survived.
- **`HALF_BOUND_PAIR` / `PAIR_CLASSIFIED_APART` / `PAIR_DECIDED_APART`** -- CNA's two-call
  size-then-copy protocol is one operation, so its halves are one decision. Eight
  `_get_type_name_byte_count` routes had no rule at all (only the older `_size` spelling did) and
  were classified by whatever header rule caught them; one inherited a lifted blocker. Exactly one
  pair is honestly decided apart -- the renderer count route is broken upstream where its copy
  route is not -- and that is written down in `coverage-rules.json` as a `pairExceptions` entry
  rather than tolerated.

**The third instance is not mechanised and probably cannot be.** A stale `DELIBERATE_NON_BINDING`
reads exactly like a live decision; instances 4 and 5 were found only by reading all fifty-six of
them against today's Java surface. Do that again next session: the question to ask each one is
*"does this reason rest on something Java lacks, and does Java still lack it?"*

## What each renderer can do

Measured by `tools/native-abi/probes/gpu_renderer_qualification.c`, on Mesa 25.0.7 -- an AMD Radeon
780M through the host session, llvmpipe through Xvfb, and identical answers from both.

| | HEADLESS | SOFTWARE | OPENGL4 | OPENGLES3 | OPENGL33 |
|---|---|---|---|---|---|
| GL context | none | none | desktop GL 4.x | **OpenGL ES 3.2** | **OpenGL 4.6 / 4.5 core** |
| compute shaders | no | no | no | **yes** | **yes** |
| storage buffers, indirect draw | no | no | no | yes | yes |
| compute image binding | -- | -- | -- | no | **yes** |
| GPU timer, GPU instance culler | no | no | no | yes | yes |
| render-target readback | **no** | yes | yes | yes | yes |
| runs a custom fragment shader | no | no | **yes** | yes | yes |
| automatic exposure | no | no | no | **yes** | **yes** |

**`OPENGL4` is not the compute-capable renderer and `OPENGL33` is** -- CNA's compute lives in the
EasyGL family, reached through the five profile names, and `OPENGL4` is a separate renderer that
implements no compute at all. The dialect CNA's own engine-layer shaders are written in is GLSL ES.

## What changed this session

1. **The census went 394 -> 73**, and the schema behind it was rebuilt so purpose and binding
   status are two fields rather than one overloaded one. Schema version 2.
2. **Skinning, morph targets and animation are projected** -- `CnaSkeleton`, `CnaSkinningData`,
   `CnaAnimationPlayer`, `CnaSkinnedModel`, `CnaModelMeshPartHandle`, `CnaMorphTargetData`,
   `CnaMorphWeightTrack`, `CnaModelAnimations` -- through hand-written descriptor-graph
   marshallers, because the generator refuses a pointer graph and the lifetimes are one call long.
3. **`.cnb` can now be written, not only read**: `CnbByteWriter`, `CnbAnimationClip`, `CnbClip`,
   `CnbBoneTrack`, `CnbKeyframes`.
4. **The three host dialogs and the tray** (`MessageBox`, `FileDialog`, `SystemTray`), measured
   against CNA's own test backends first, with one-shot callbacks that delete their own global
   reference.
5. **Sensors are readable on a machine with none**, through CNA's injection backends
   (`SensorSubscription`, `SensorTestBackends`).
6. **A model carries its own provenance** -- `GltfImportReport`, `GltfImportSourceCounts`,
   `GltfImportDiagnostic` -- and CNA's split between the twelve counts it stores and the five it
   derives is kept rather than flattened, because CNA refuses a report that carries the derived
   five.
7. **`LodGroup` levels can carry the mesh part they draw**, and `select` hands back the caller's
   own object rather than a second view of it.
8. **CNA's log can be routed into a Java sink** (`CnaLogger.setSink`). Its global reference is not
   merely cleared before deletion: clearing an atomic pointer does not recall a thread already
   inside the callback, so the adapter counts readers and waits for them to leave.
9. **Two new upstream findings** (`JAVA-UPSTREAM-022`, `-023`) and four existing ones retaken
   against the rebuilt library.
10. **146 -> 179 native tool tests**, including the two new census checks and their mutations.

## Honest boundaries

- **Survived mutations are recorded, not hidden.** Four stand: a filter's name/pattern swap and a
  message box's label content (CNA's test backends record neither); the uniform-block offsets (no
  SPIR-V renderer in this build); the post-process context's normals and velocity slots (nothing
  reachable here reads them); and skipping `cna_logger_reset_sink_ext` entirely, because clearing
  the sink reference already makes the trampoline return without calling Java, so the native
  uninstall has no Java-visible effect.
- **The GPU timer's value is not a duration here.** On this software GL implementation the number
  is `0xFFFFFFFF` nanoseconds -- a sentinel. The test asserts the protocol and says so.
- **`SOFTWARE` accepts any shader source and runs none of it.** Every shader claim establishes
  first, with a literal-colour control, whether the renderer draws at all.
- **No claim here is about the host's own GPU.** The qualification runs on a virtual display, so
  the GL implementation is llvmpipe; the same probes were run against the AMD Radeon 780M through
  the host session and gave the same answers, bar the GL version.
- **A mistyped renderer name is fatal and nothing in Java can soften it.** `CNA_GRAPHICS_RENDERER`
  is read while `libcna_c_api.so` loads, and a name this build does not have aborts the process
  before `System.loadLibrary` returns (`JAVA-UPSTREAM-017`).
- **Four renderer-selection getters are not projected because they answer about something else.**
  Three are reset by creating a `GraphicsDevice`; `get_current_type` reports the renderer the build
  was configured with. `GraphicsRenderer.getActive()` is the one that is right.
- Everything in `docs/runtime-capabilities.json` still holds for the previously measured families.

## Next work, in dependency order

`docs/backlog.json` is the machine-readable source. **Nothing in it is local work.**

1. **Four upstream blockers, all retaken on 2026-09-01 against cnanext `96b56b0e4`** -- three
   commits past the build every earlier measurement in this repository used. All four still
   reproduce, and each has a pure-C reproducer in `tools/native-abi/probes/`:
   - `JAVA-UPSTREAM-004` (2 routes): `cna_content_manager_load_model` returns SUCCESS and the
     process then dies with SIGSEGV on the destroy. Blocks `CnaModel.Load` and, with it,
     `cna_model_get_content_tag_dictionary_ext` -- no `cna_model_set_content_tag_*` declaration
     exists at 0.21.0, so the loader is the only writer of a content tag.
   - `JAVA-UPSTREAM-011` (1): one owned pass makes `cna_game_destroy` answer `INVALID_STATE`.
   - `JAVA-UPSTREAM-018` (4): after one device the available count is 0 against
     `copy_available_ext`'s 5, the selection is `UNKNOWN`, the latch stays false; and
     `get_current_type` answers `HEADLESS` while `OPENGL33` runs.
   - `JAVA-UPSTREAM-019` (15): `PROBE_CASE=0` exits 139, `PROBE_SKIP=camera` exits 0.
2. **Nine further upstream findings** (`-012` through `-023`), each reproduced in C before it was
   claimed. Four share one shape: a capability query that does not predict the behaviour.
3. **320 deferred XNA-backing routes**, each owned by a named `JAVA-NATIVE-*` or `JAVA-XNA-*` task.
   Their Java members are projected and behave; what is deferred is moving the implementation onto
   the native route. Both profiles' zeros are what says the members behave.
4. **`cna_content_manager_load_effect`** is `ASSET_PENDING`, not blocked: no `.xnb` effect and no
   `.cnj` describing one exists in the checkout this qualifies against.
5. **51 deliberate non-bindings inside the census**, every one with its exact reason in
   `tools/native-abi/coverage-rules.json`. Re-read them against the Java surface each session; two
   of this session's five stale reasons were hiding there.

Do not weaken either profile's zero, do not add an allowlist, and do not put non-XNA API inside
`Microsoft.Xna.Framework.*`.
