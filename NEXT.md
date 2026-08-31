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
CANONICAL_FUNCTIONS=4054   BOUND_FUNCTIONS=1570
XNA_BACKING=976  JAVA_INTERNAL_ONLY=170  CNA_EXTENSION_CANDIDATE=1871
DEFERRED_RUNTIME=417  NOT_USEFUL_IN_JAVA=620  UNEXPLAINED=0
BOUND_BUT_UNREACHED=0  BOUND_WITHOUT_JAVA_CALL_SITE=161

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
org.openeggbert.cna.extensions.content    CNA's model over an XNA one, and the .cnb container:
                                          document, checked reader, texture data, writer

TESTS=219 SUITES=50 FAILURES=0 ERRORS=0 SKIPPED=0
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
- **`NetworkSession.MaxGamers` does not report the maximum a session was created with.** A
  session created with four reports 69; the setter does work. `JAVA-UPSTREAM-002` records it,
  and Java does not invent the creation value.
- **CNA publishes a local signed-in roster** on this runtime rather than an empty one, and
  sorts a `PropertyDictionary` by key. Both are asserted as CNA's own answers.
- **The headless platform does not report every host fact.** A zero from
  `SystemInformation` is the host saying it does not know, not a measurement.
- Everything in `docs/runtime-capabilities.json` still holds for the previously measured
  families.

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

## Next work, in dependency order

`docs/backlog.json` is the machine-readable source. The highest-value ready tasks are:

1. `JAVA-NATIVE-023` — reach or unbind the 161 routes no Java call site touches, concentrated
   in the GamerServices and Net families. PacketReader and PacketWriter are the largest slice:
   28 routes CNA implements that the Java projection currently does managed.
2. `JAVA-EXT-003` — the remaining `.cnb` asset families: model, sprite font, sound, song, video,
   curve, animation clip and `.cnj`. The loader registry needs a design decision rather than
   more binding, because `cna_cnb_loader_invoke` hands back a `void*`.
3. `JAVA-UPSTREAM-004` — revalidate the CNA model loader when it is fixed, and add
   `CnaModel.Load` on top of it.

Do not weaken either profile's zero, do not add an allowlist, and do not put non-XNA API inside
`Microsoft.Xna.Framework.*`.
