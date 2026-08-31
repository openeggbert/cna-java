# CNA-Java continuation handoff

**Updated:** 2026-08-31

The **complete XNA 4.0 runtime superset** is structurally at zero diagnostics, and the native
boundary is qualified against the live sibling dependencies. Read `plan.md`, this file,
`docs/backlog.json`, `docs/xna-java-mapping.md`, `docs/cna-abi-migration-evidence.md` and
`docs/cna-c-api-coverage-summary.json` before new work.

## Repository and dependencies

Writable: this repository and `../cna-java-template`. `../../cnanext` and
`../../sharp-runtimenext` are dependencies and were not modified; `git status` in `cnanext` is
clean. The pre-existing untracked `out` entry is untouched.

```text
cnanext HEAD            599d14e54e073b566d77b3d6fb30ac52d3d810b7 (branch next)
sharp-runtimenext HEAD  4a49afb0cfe6a41e6e0af0bb62dc5175976731bb (branch next)
native artifact         cnanext/cmake-build-javanext/modules/c-api/libcna_c_api.so
ABI                     0.21.0
C API inventory SHA-256 be9a2bf818318002adc100ad1db22949b164726d2d124352f3b29aa3fdc48ea2
platform/renderer/audio HEADLESS / HEADLESS / NULL
build options           CNA_BUILD_C_API=ON, CNA_CNAEXT=ON, CNA_DEVICES=ON,
                        CNA_ENABLE_NET=ON, CNA_ENABLE_VIDEO=AUTO
XNA reference corpus    /rv/data/development/github.com/openeggbert/xna4-decomp/dlls

Both dependency HEADs advanced during this session, and CNA's C API advanced with them: ABI
0.20.0 to 0.21.0, 4,051 declarations to 4,054. Every one of the 1,570 bound routes kept its
exact signature, so the migration was three edits and a rebuild;
`docs/cna-abi-migration-evidence.md` carries the addendum. The library was rebuilt from the
clean checkout before any of it was claimed.
```

`build.gradle` resolves CNA from `../../cnanext` and fails clearly when it is absent. There is
deliberately no fallback to `../cna` or `../../cna`: qualifying against an unrelated checkout
would make every ABI, symbol and layout result describe a library nobody ships.

## Exact state

```text
SELECTED PROFILE (7 Windows runtime assemblies)
REFERENCE_TYPES=257  REFERENCE_MEMBERS=2964
EXPECTED_JAVA_TYPES=266  EXPECTED_JAVA_MEMBERS=3242
TOTAL_DIAGNOSTICS=0  ALLOWLIST_ENTRIES=0

FULL PROFILE (10 runtime assemblies, adding GamerServices, Net and Avatar)
REFERENCE_TYPES=331  REFERENCE_MEMBERS=3640
EXPECTED_JAVA_TYPES=340  EXPECTED_JAVA_MEMBERS=4022
TARGET_TYPES=340     TARGET_MEMBERS=4022
TOTAL_DIAGNOSTICS=0  ALLOWLIST_ENTRIES=0

NATIVE
CANONICAL_FUNCTIONS=4054   BOUND_FUNCTIONS=2410
XNA_BACKING=986  JAVA_INTERNAL_ONLY=9  CNA_EXTENSION_CANDIDATE=1916
DEFERRED_RUNTIME=416  NOT_USEFUL_IN_JAVA=727  UNEXPLAINED=0
BOUND_BUT_UNREACHED=0  BOUND_WITHOUT_JAVA_CALL_SITE=0
LIBRARY_SYMBOL_CHECK=PASS (2410/2410)   NATIVE_TOOL_TESTS=125
ENGINE LAYER (engine_layer.h)   778 of 857 bound and reached
EFFECTS (effects.h)             212 of 290 bound and reached

CNA EXTENSIONS
org.openeggbert.cna.extensions.graphics   pipeline settings, PBR material, ASCII/CRT/depth effects
org.openeggbert.cna.extensions.runtime    platform, renderer, backend category and maturity, logger
org.openeggbert.cna.extensions.devices    system info, power, display, locales, clipboard, URL,
                                          vibration, input-device enumeration and hot plug
org.openeggbert.cna.extensions.input      typed characters, composition drafts and candidate
                                          lists, text input control, mouse cursors, raw
                                          joysticks, force feedback, the modern game pad,
                                          keyboard naming and scancodes, mouse capture and
                                          relative mode, touch emulation
org.openeggbert.cna.extensions.sensors    accelerometer, gyroscope, compass, fused motion,
                                          and the host's sensor enumeration
org.openeggbert.cna.extensions.content    CNA's model over an XNA one, the .cnb container, and
                                          the ingest half of a content pipeline: textures in all
                                          three shapes, sounds, sprite fonts, curves, songs,
                                          videos, whole models with materials, and PNG/JPEG/WAV/
                                          DDS import
org.openeggbert.cna.extensions.net        where a discovered session is, and describing one at
                                          an address so NetworkSession.Join can take it
org.openeggbert.cna.extensions.gamerservices  CNA's application-rendered Guide
org.openeggbert.cna.extensions.avatars    real-avatar colours, an animation's clip, and the
                                          content behind XNA's canonical presets
org.openeggbert.cna.extensions.graphics   also CNA's engine layer, 778 of its 857 routes: the
                                          render pipeline and its sixteen post-process passes
                                          and a standalone chain to run them in, LOD groups,
                                          frustum culling, instancing, the debug renderer, GPU
                                          timing, the three light types and clustered forward
                                          lighting, all four shadow maps with the cascade state
                                          an effect reads, light probes and their baker's
                                          cameras, decals, particles, the depth/normal prepass,
                                          atmospheric sky, HDR presentation, order-independent
                                          transparency, spatial upscaling, the render-target
                                          pool, area lights with the table their shader samples,
                                          image-based lighting, GPU instance culling, colour
                                          grading, the back-to-front transparent draw list, the
                                          physically-based effect and its skinned form with the
                                          glTF material they carry and the bridge that builds
                                          one, the named shader-effect cache, the screen-filling
                                          draw, and the lighting parameters CNA's stock effects
                                          take beyond XNA's

TESTS=455 SUITES=88 FAILURES=0 ERRORS=0 SKIPPED=0
```

The selected profile is now a **subset gate**: a type the wider profile declares is not an
unexpected type in the narrower one, so its zero still means what it always meant.

## What changed this session

1. The native boundary moved from ABI 0.7.0 to the live 0.20.0. Thirteen minor versions moved
   and every bound route kept its signature; three documented ABI 0.9.0 behaviour contracts did
   change, and in each case CNA moved closer to the XNA reference, so the Java expectation was
   corrected rather than the behaviour worked around. See `docs/cna-abi-migration-evidence.md`.
2. Stale JNI declarations are a compile error. All 1,225 dispatch-table slots are declared
   `CNA_JNI_ROUTE(symbol)`, whose type is the header's own declaration.
3. `tools/native-abi/generate_jni.py` generates the mechanical half of the boundary from the
   headers. It understands values, out-parameters, string views, count/copy triples, arrays,
   arrays of structs, by-value structs, flat POD structs and an opted-in null callback, and it
   refuses anything else with a diagnostic rather than guessing.
4. `Dispose()` keeps its XNA name; `close()` is the delegating AutoCloseable bridge.
5. GamerServices, Avatar and Net are projected in full, and their eleven events have a real
   native producer: the JNI callbacks record each event and Java drains it right after pumping,
   so an event arrives on the game thread during `Update`.
6. Four CNA extension families exist outside the strict packages, and the template has an
   opt-in `--extensions-smoke` that proves an external consumer can reach them.

## Honest boundaries

- **The events have a producer, but not every event was observed.** `GameStarted` and
  `GameEnded` are verified end to end on a local session. The other nine share the same
  producer and the same drain, but a real `GamerJoined`, `HostChanged` or `SignedIn` needs a
  second machine or a live sign-in, and neither took part in this qualification.
- **`Guide.IsScreenSaverEnabled` does not round-trip on HEADLESS.** CNA accepts the request and
  the platform does not honour it. The projection reports what CNA reports.
- **`NetworkSession.MaxGamers` was wrong and is fixed.** A session created with four once
  reported 69. Revalidated against the live 0.21.0 library by re-running the exact case: it
  reports four. The test asserts the creation value now rather than only that the property is
  stable.
- **CNA publishes a local signed-in roster** on this runtime rather than an empty one, and
  sorts a `PropertyDictionary` by key. Both are asserted as CNA's own answers.
- **The headless platform does not report every host fact.** A zero from
  `SystemInformation` is the host saying it does not know, not a measurement.
- Everything in `docs/runtime-capabilities.json` still holds for the previously measured
  families.
- **Two engine-layer values are write-only and cannot be checked at runtime.** The post-process
  context and the full-screen pass's sampler both cross into CNA and never come back: no route
  reads either one, and this renderer accepts any sampler without doing anything observable with
  it. A planted swap of the sampler's address modes passed every Java test. Their leaf offsets
  are pinned against the live header by the generator tool tests instead, which is the only place
  that check can honestly live.
- **No engine-layer claim here is about a rendered pixel.** The HEADLESS renderer runs no shader,
  so every pass, every effect and every draw is qualified by what the objects know about
  themselves and by which calls CNA accepts or refuses. The families whose whole answer would be
  an image -- compute, automatic exposure, probe baking -- are recorded as HARDWARE_PENDING with
  the measurement that says so, not projected and hoped for.

## What changed after that

7. `JAVA-EXT-002` and `JAVA-NATIVE-022` are done, and no input route is deferred. The audit
   found that none of the 132 deferred input routes was an XNA-shaped route lacking backing;
   39 were CNA capabilities XNA has no shape for, and the other 92 are value operations on
   XNA's input structs, managed in XNA and managed here.
8. `JAVA-EXT-005` is done: a second event transport carries the string-bearing composition and
   candidate events, and a shared sequence keeps one order out of two queues.
9. Three native-boundary defects were fixed and are covered by new tool tests: an output array
   of structs was never copied back, a C `double` was narrowed to `jfloat`, and a nested
   versioned structure was left unstamped. The reachability question was also unanswerable
   because a `native` declaration counted as its own call site; with that fixed,
   `boundWithoutJavaCallSite` is 161 and `JAVA-NATIVE-023` owns getting it to zero.

10. `JAVA-NATIVE-011` is done, and its audit found more than it bound: CNA's own model loader
    segfaults during teardown for any asset with a mesh part, reproduced in C as
    `JAVA-UPSTREAM-004`.
11. `JAVA-EXT-003`'s first vertical slice reads and writes `.cnb`, with every fixture produced
    by CNA's own encoder.
12. `JAVA-XNA-006` is measured and answered in `docs/content-pipeline-decision.md`:
    partial/interop, consume compiled content rather than reimplement Microsoft's build system.

13. `JAVA-NATIVE-023` is done. Every bound route reaches a Java call site: seven were reached
    and 155 unbound with a stated reason, and both reachability facts are hard gates now. Three
    leaks turned up on the way -- `NetworkSessionProperties`, `AvatarDescription` and every
    `LeaderboardEntry` owned a handle nothing released -- and are fixed.

## What changed after that

14. `JAVA-EXT-003` grew from one vertical slice to eight. Five asset families cross to the XNA
    type a game uses -- a sound becomes a `SoundEffect`, a sprite font a `SpriteFont` that
    `DrawString` draws with, a curve a `Curve` that evaluates the same on both sides, and a song
    and a video media objects over the reference the file records. The model family reads and
    writes the whole graph including materials. And `CnbImport` reads what artists actually hand
    over: PNG, JPEG, WAV and DDS.
15. `JAVA-EXT-006` is done in all three slices. The net one found a leak on the way: every
    `AvailableNetworkSession` owns an independent native handle -- CNA's collection accessor is
    documented to return a copy that outlives the collection -- and nothing released it.
16. The JNI generator learned three shapes, each with tool tests and refusals: `CNA_StringView`
    fields inside a structure, input arrays whose length CNA states in prose rather than in a
    count parameter, and declared in/out structures. The last was a silent wrong guess rather
    than a refusal -- an in/out structure was treated as an output and started zeroed, so the
    caller's values were discarded -- and an audit of every bound route found no other case.
17. `JAVA-EXT-008` opened CNA's engine layer, 857 routes of renderer XNA has no counterpart for.
    Three families are in, each chosen by measuring what can be honestly qualified here rather
    than by guessing: LOD groups need no device, the debug renderer needs a real one, and the
    render-pipeline settings are arithmetic.

## What changed after that

18. `JAVA-EXT-008` went from three families to most of the layer: 778 of the 857 routes are bound
    and reached, and every one of the remaining 79 has a written reason. Twelve more families
    landed this session -- the effect lighting parameters, the transparent draw list, the
    light-probe baker's cameras, the shader-effect cache, the user effect pass, the cascade
    state, the physically-based effect and its glTF material, the glTF bridge, the standalone
    post-process chain, the screen-filling draw and the ASCII pass -- and each was chosen by
    probing what this runtime can actually do before any Java was written.
19. The generator learned five shapes, each with tool tests and each still refusing what it
    cannot derive: fixed arrays inside a structure, whether the extent is a literal or one of
    CNA's own macros and whether the elements are scalars or structures; version stamping for
    every element of an array of versioned structures, which was silently missing; a declared
    version prefix, so a structure that grew a pointer field can still be filled in as the
    version CNA documents as its mandatory prefix; a refusal for a null struct carrier, which
    would otherwise have been read as uninitialised stack; and an optional structure, so "none"
    reaches CNA as a null pointer rather than an all-zero value. 70 tool tests became 125.
20. The transparent draw list is the first family whose entries are C callbacks, and it has the
    first hand-written JNI trampoline. It takes no global references and has nothing to leak:
    the callbacks only run inside one call, so they are passed in for its duration and the
    context is an index. A thrown exception is left pending and surfaces at the Java call that
    caused it. The whole suite runs clean under `-Xcheck:jni`.
21. Three upstream findings, each reproduced in pure C before it was claimed.
    `JAVA-UPSTREAM-010`: a texture applied through a PBR material never reaches the C API's
    handle registry, so a read-modify-write of a material silently unbinds every map.
    `JAVA-UPSTREAM-011`: handing a pass to a post-process chain leaves the game undestroyable,
    which is why that route is not bound. The third correction went the other way -- a
    "borrowed" handle the header says keeps its lender alive really does, and the probe is what
    established it.
22. `check` now runs javadoc. A `{@link}` to a method that does not exist survived a session of
    green checks and surfaced only when the template was next built, because javadoc ran only
    for the documentation jar.

## Next work, in dependency order

`docs/backlog.json` is the machine-readable source. What is left:

1. `JAVA-EXT-008` — the engine layer, 79 routes still unbound and every one with a reason.
   Three groups, and none of them is a matter of writing more Java. Twenty-five are the compute
   family and thirteen are automatic exposure: both refuse at construction on a renderer with no
   compute and no luminance readback, so there is no object to configure or observe
   (`JAVA-EXT-009`, `JAVA-EXT-011`). About a dozen lend a handle whose lifetime the declaration
   does not state (`JAVA-EXT-010`). The rest are named individually in `docs/backlog.json`: three
   bake routes whose callback this renderer never enters, nine texture getters that mint a fresh
   handle per call, two scene callbacks, two no-op initialisers, and the one route that would
   make a game undestroyable.

2. `JAVA-EXT-007` — blocked with evidence. 23 of 26 skinned-model routes plan; the three that do
   not include `set_clip`, whose descriptor is a pointer graph, and no route takes a clip handle
   instead. Twenty-three plannable routes that do not add up to a usable feature is not a slice
   worth shipping, and it is what keeps the two real-avatar-rendering routes unbound.
3. `JAVA-UPSTREAM-004` — revalidate the CNA content-manager model loader when it is fixed, and
   add `CnaModel.Load` on top of it. The `.cnb` model path is a different one and was probed
   clean in C before this session bound it.
4. `JAVA-UPSTREAM-003` — five of cnanext's own C API tests still fail in this configuration.
   Not a CNA-Java dependency, and the number is only meaningful after rebuilding the test
   executables, which is written down because it caught this session out once.

Do not weaken either profile's zero, do not add an allowlist, and do not put non-XNA API inside
`Microsoft.Xna.Framework.*`.
