# CNA-Java continuation handoff

**Updated:** 2026-08-31

The **complete XNA 4.0 runtime superset** is structurally at zero diagnostics, and the native
boundary is qualified against the live sibling dependencies **on five renderers**. Read `plan.md`,
this file, `docs/backlog.json`, `docs/runtime-capabilities.json`,
`tools/native-abi/probes/README.md` and `docs/cna-c-api-coverage-summary.json` before new work.

## Repository and dependencies

Writable: this repository and `../cna-java-template`. `../../cnanext` and `../../sharp-runtimenext`
are dependencies and were not modified. The pre-existing untracked `out` entry is untouched.

```text
cnanext HEAD            0fd4d4e39e3adcd7531e04eff857defa9233518e (branch next)
sharp-runtimenext HEAD  4a49afb0cfe6a41e6e0af0bb62dc5175976731bb (branch next)
ABI                     0.21.0
C API inventory SHA-256 b485d17b1df9a86c6099700baedcd435c8a3f07487af8741b01d8c471956bfe7
```

Neither dependency moved during this session, and the C contract is byte-identical to the one the
previous qualification ran against: same ABI, same 4,054 declarations, same inventory hash. So
nothing here is explained by CNA having changed.

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

`HEADLESS` is the default, so a run that names nothing behaves exactly as the previous
qualification did. `CNA_GRAPHICS_RENDERER` picks another for one run, and the build forwards it,
`DISPLAY`, `SDL_VIDEODRIVER` and `WAYLAND_DISPLAY` into the test JVM -- a test worker inherits the
Gradle daemon's environment rather than the shell's, so without that a run redirected to a virtual
display would still open real windows on someone's desktop.

```sh
CNA_NATIVE_LIBRARY=../../cnanext/cmake-build-javagl/modules/c-api/libcna_c_api.so \
CNA_GRAPHICS_RENDERER=OPENGL33 DISPLAY=:91 SDL_VIDEODRIVER=x11 ./gradlew check
```

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

Three of those contradict what this projection assumed before. **`OPENGL4` is not the
compute-capable renderer and `OPENGL33` is** -- CNA's compute lives in the EasyGL family, reached
through the five profile names, and `OPENGL4` is a separate renderer that implements no compute at
all. **The dialect is GLSL ES**, because every shader inside CNA's own engine layer is written in
it. And **render-target readback is not a compute question**: only HEADLESS refuses it.

## Exact state

```text
SELECTED PROFILE   TOTAL_DIAGNOSTICS=0  ALLOWLIST_ENTRIES=0
FULL PROFILE       TOTAL_DIAGNOSTICS=0  ALLOWLIST_ENTRIES=0
                   TARGET_TYPES=340     TARGET_MEMBERS=4022

NATIVE
CANONICAL_FUNCTIONS=4054   BOUND_FUNCTIONS=2528
XNA_BACKING=986  JAVA_INTERNAL_ONLY=9  CNA_EXTENSION_CANDIDATE=1927
DEFERRED_RUNTIME=405  NOT_USEFUL_IN_JAVA=727  UNEXPLAINED=0
BOUND_BUT_UNREACHED=0  BOUND_WITHOUT_JAVA_CALL_SITE=0
LIBRARY_SYMBOL_CHECK=PASS (2528/2528)   NATIVE_TOOL_TESTS=146
ENGINE LAYER (engine_layer.h)   844 of 857 bound and reached
EFFECTS (effects.h)             241 of 290 bound and reached

TESTS=516 SUITES=94 FAILURES=0 ERRORS=0 SKIPPED=0
  -- on each of HEADLESS, SOFTWARE, OPENGL4, OPENGLES3 and OPENGL33
  -- and clean under -Xcheck:jni on all five (./gradlew test -PcheckJni)
```

## What changed this session

1. **The engine layer went from 778 to 844 of its 857 routes**, and none of the sixty-six was
   waiting on Java. Compute and storage buffers, automatic exposure, the light-probe baker's bake
   routes, the render pipeline's scene callbacks and twenty-two lent handles each needed a
   renderer or a measurement.
2. **Compute is qualified by arithmetic, not by a result code.** Four known integers into a
   storage buffer, a dispatch that doubles and offsets them, a readback compared against the same
   arithmetic in Java. Five of six planted mutations registered; the sixth is recorded.
3. **Automatic exposure is qualified by luminance.** A grey of 8/255 measures 0.031373 and one of
   240/255 measures 0.941176, and adaptation moves the exposure the way the header says.
4. **The borrowed handles are sorted by measurement rather than by wording.** Seven counted
   borrows whose lender refuses to die, seven retaining ones that outlive theirs, one non-owning
   view. Nothing dangles.
5. **The shader effect family exists**, which is what makes `ShaderEffectFactory` and
   `FullscreenPass` useful: a custom shader can now be given a value. Qualified by a pixel.
6. **Two write-only values became pixels.** A planted swap of the full-screen sampler's address
   modes used to pass every test; it fails now.
7. **Five upstream findings opened and one widened**, each reproduced in pure C first.
8. **The generator learned two shapes** -- an opaque `void*` whose byte extent is declared, and a
   counted array whose count is not its length -- each with tool tests including the refusals.
   125 tool tests became 146.
9. **The renderer selection family is projected**, which is what this session needed and did not
   have: it was reading the renderer inventory out of `CMakeCache.txt` while
   `cna_graphics_renderer_copy_available_ext` was sitting there unbound. `GraphicsRenderer`
   answers what this build has, classifies any identity CNA defines, parses a name, prefers one,
   and names the renderer actually running -- eighteen routes, and pointedly not the four that
   answer about something else.
10. **Two more upstream findings, one of them found by being hit and one corrected by being
   tested.** A sweep named a renderer this library was configured without and the JVM died with
   `SIGABRT` inside `System.loadLibrary` (`JAVA-UPSTREAM-017`). Measuring the family around it
   first produced a wrong finding -- five write-only query routes -- because the probe asked them
   after rearranging the state they report. Asked cleanly, three are correct until a
   `GraphicsDevice` exists and are reset by creating one, and three more named `current` describe
   the compile-time default rather than the running renderer (`JAVA-UPSTREAM-018`).
11. **`ColorGradePass.setVolumeLut` took a `TextureCube` and could never have worked.** CNA wants a
   cubical `Texture3D` and refuses a cube map with `INVALID_HANDLE`. Nothing caught it because no
   test had ever bound a volume table; both are fixed together.

## Honest boundaries

- **The GPU timer's value is not a duration here.** It collects a sample and counts it, and on
  this software GL implementation the number is 0xFFFFFFFF nanoseconds -- a sentinel. The test
  asserts the protocol and says explicitly that it does not assert the duration.
- **The post-process context is verified in three fields, not all of them.** A blit's output is
  decided by its source and destination handles and film grain by the elapsed time, so those three
  are pixels. Swapping the normals and velocity slots still passes, because nothing reachable here
  reads them; that mutation is recorded rather than hidden, and those leaves stay pinned by the
  layout gate alone.
- **`SOFTWARE` accepts any shader source and runs none of it.** CNA documents that state, and
  every shader claim here establishes first, with a literal-colour control, whether the renderer
  draws at all -- rather than reading a black image as permission to skip.
- **No claim here is about the host's own GPU.** The qualification runs on a virtual display, so
  the GL implementation is llvmpipe; the same probes were also run against the AMD Radeon 780M
  through the host session and gave the same answers, bar the GL version.
- **A mistyped renderer name is fatal and nothing in Java can soften it.** `CNA_GRAPHICS_RENDERER`
  is read while `libcna_c_api.so` loads, and a name this build does not have aborts the process
  there -- before `main`, before `System.loadLibrary` returns. `GraphicsRenderer.available()` is a
  mitigation for the next run, not a guard for this one.
- **Four renderer-selection getters are not projected because they answer about something else**,
  not because they were missed. Three are reset by creating a `GraphicsDevice` -- correct before,
  wrong after -- and `get_current_type` reports the renderer the build was configured with rather
  than the one running. `GraphicsRenderer.getActive()` and
  `RendererCapabilities.getRendererName` are the two that are right, and they are right for
  different reasons: one asks the selection that happened, the other asks the device in hand.
- **`CnaRuntime.getBackendCategory()` and `getBackendMaturity()` describe the compile-time
  default**, not the running renderer, and now say so. On a single-renderer build those coincide;
  this is a multi-renderer build and they do not.
- Everything in `docs/runtime-capabilities.json` still holds for the previously measured families.

## Next work, in dependency order

`docs/backlog.json` is the machine-readable source. Nothing in it is local work.

1. **Thirteen upstream findings**, `JAVA-UPSTREAM-004` through `-021`, each with a pure-C
   reproducer in `tools/native-abi/probes/`. Four share one shape -- a capability query that does
   not predict the behaviour -- two more are the exception barrier flattening a refusal a game
   could act on, `-017` is that barrier missing entirely at library load, and `-019` is a
   segfault: destroying a camera after a longer session kills the process, on every renderer.
2. **`JAVA-EXT-007`**, blocked with evidence and rechecked against the live headers: a clip enters
   a skinned model only through a descriptor pointer graph, and `cna_skinning_data_create` takes
   one of its own. No route takes a clip handle.
3. **Thirteen engine-layer routes and 405 deferred ones**, each with a written reason -- and the
   thirteen now carry their own, in `tools/native-abi/coverage-rules.json`, rather than sharing
   the header's. Seven lend a fresh name for something the caller already holds, and a fresh name
   is a game child: taking two and releasing neither makes `cna_game_destroy` answer
   `INVALID_STATE`, which is the measurement that decided it. One lends back the identical handle
   it was given. Two are struct initialisers the generated adapter already writes at compile time,
   one is a bitmask test, one has nothing to return by construction, and one makes a game
   undestroyable.
4. **`cna_content_manager_load_effect`** is `ASSET_PENDING`, not blocked: no `.xnb` effect and no
   `.cnj` describing one exists in the checkout this qualifies against.
5. **143 extension routes are classified but not explained**, and this is the one piece of local
   work this session leaves behind rather than finishing. The census is reproducible: of 394
   unbound `CNA_EXTENSION_CANDIDATE` routes, 125 have a shape that speaks for itself (a callback
   the generator refuses, a struct-field accessor whose struct already arrives flattened, half of
   a count/copy pair, a test-injection route), and of the 269 that do not, 126 now carry a
   measured reason or a blocking task. The remaining 143 carry a reason that explains why the
   route is a CNA extension rather than XNA -- which is a different question from why it is not
   bound:

   ```text
   35 cnb.h   20 devices.h   15 content_readers.h   10 sensors.h   10 effects.h
    8 input_devices.h   7 input_haptics.h   7 core_ext.h   7 models.h
    5 input_joystick.h   3 input_text.h   1 graphics_ext.h
   ```

   The camera family was the first of these taken apart, and it is worth reading before the next
   one is. It looked like BLOCKED_HARDWARE and was not: CNA ships a test backend that makes every
   route exercisable and its pixels checkable on a machine with no camera. Qualifying it found a
   segfault (`JAVA-UPSTREAM-019`), so those fifteen routes are unbound for a measured reason now
   instead of an assumed one. **The assumption about why a family is absent is usually wrong in a
   more interesting way than expected**, in both directions.

   Some will turn out to be blocked -- the model remainder almost certainly behind
   `JAVA-UPSTREAM-004` and `JAVA-EXT-007`, as the rest of that family already is. Some will turn
   out to be bindable. **Measure before writing the reason**: this session filed one upstream
   finding from a probe that had rearranged the state it then measured and had to withdraw four
   fifths of it, and twice reported behaviour that was only the probe reading back its own
   initialiser. Poison every out-parameter before the call; "untouched" is a third answer and it
   is sometimes the finding.

Do not weaken either profile's zero, do not add an allowlist, and do not put non-XNA API inside
`Microsoft.Xna.Framework.*`.
