# CNA-Java continuation handoff

**Updated:** 2026-08-30

The **complete XNA 4.0 runtime superset** is structurally at zero diagnostics, and the native
boundary is qualified against the live sibling dependencies. Read `plan.md`, this file,
`docs/backlog.json`, `docs/xna-java-mapping.md`, `docs/cna-abi-migration-evidence.md` and
`docs/cna-c-api-coverage-summary.json` before new work.

## Repository and dependencies

Writable: this repository and `../cna-java-template`. `../../cnanext` and
`../../sharp-runtimenext` are dependencies and were not modified; `git status` in `cnanext` is
clean. The pre-existing untracked `out` entry is untouched.

```text
cnanext HEAD            17b5a90a0878f3f44c23bc8e3197d5d30373dc72 (branch next)
sharp-runtimenext HEAD  eebebd862121953538e3b84d43384d70a8a1728d (branch next)
native artifact         cnanext/cmake-build-javanext/modules/c-api/libcna_c_api.so
ABI                     0.20.0
C API inventory SHA-256 e9e0be892dbdce49dedf195dac35f604a9263565a74473195d878ee9e580696d
platform/renderer/audio HEADLESS / HEADLESS / NULL
build options           CNA_BUILD_C_API=ON, CNA_CNAEXT=ON, CNA_DEVICES=ON,
                        CNA_ENABLE_NET=ON, CNA_ENABLE_VIDEO=AUTO
XNA reference corpus    /rv/data/development/github.com/openeggbert/xna4-decomp/dlls

Both dependency HEADs advanced during this session. The C API did not: 4,051 declarations,
4,051 exported symbols, ABI 0.20.0, and the same inventory hash before and after. The hash is
the identity worth checking, because it is what the binding actually depends on.
```

`build.gradle` resolves CNA from `../../cnanext` and fails clearly when it is absent. There is
deliberately no fallback to `../cna` or `../../cna`: qualifying against an unrelated checkout
would make every ABI, symbol and layout result describe a library nobody ships.

## Exact state

```text
SELECTED PROFILE (7 Windows runtime assemblies)
REFERENCE_TYPES=257  REFERENCE_MEMBERS=2964
EXPECTED_JAVA_TYPES=265  EXPECTED_JAVA_MEMBERS=3242
TOTAL_DIAGNOSTICS=0  ALLOWLIST_ENTRIES=0

FULL PROFILE (10 runtime assemblies, adding GamerServices, Net and Avatar)
REFERENCE_TYPES=331  REFERENCE_MEMBERS=3640
EXPECTED_JAVA_TYPES=340  EXPECTED_JAVA_MEMBERS=4022
TARGET_TYPES=340     TARGET_MEMBERS=4022
TOTAL_DIAGNOSTICS=0  ALLOWLIST_ENTRIES=0

NATIVE
CANONICAL_FUNCTIONS=4051   BOUND_FUNCTIONS=1225
XNA_BACKING=975  JAVA_INTERNAL_ONLY=171  CNA_EXTENSION_CANDIDATE=1730
DEFERRED_RUNTIME=679  NOT_USEFUL_IN_JAVA=496  UNEXPLAINED=0
BOUND_BUT_UNREACHED=0

CNA EXTENSIONS
org.openeggbert.cna.extensions.graphics   pipeline settings, PBR material, ASCII/CRT/depth effects
org.openeggbert.cna.extensions.runtime    platform, renderer, backend category and maturity, logger
org.openeggbert.cna.extensions.devices    system info, power, display, locales, clipboard, URL, vibration
org.openeggbert.cna.extensions.input      typed characters, text input control, mouse cursors

TESTS=191 SUITES=42 FAILURES=0 ERRORS=0 SKIPPED=0
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

## Next work, in dependency order

`docs/backlog.json` is the machine-readable source. The highest-value ready tasks are:

1. `JAVA-EXT-002` — the rest of the device and input extensions: sensors, haptics, raw
   joysticks and device enumeration. About 1,700 routes are still classified as extension
   candidates, which is the largest single gap left.
2. `JAVA-EXT-003` — the `.cnb` content format, 272 routes.
3. `JAVA-NATIVE-011` — bind the native Model routes behind the managed XNB model graph, so
   `Load<Model>` stops being managed-only.
4. `JAVA-XNA-006` — measure the Content Pipeline build-time profile and decide whether a Java
   content pipeline belongs in this binding.

Do not weaken either profile's zero, do not add an allowlist, and do not put non-XNA API inside
`Microsoft.Xna.Framework.*`.
