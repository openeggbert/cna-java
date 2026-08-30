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
cnanext HEAD            72262a33ed5ae7657024c7f1251338748a3feee5 (branch next)
sharp-runtimenext HEAD  df1b42abfcdefda030d63e97f16d2f7ea883837f (branch next)
native artifact         cnanext/cmake-build-javanext/modules/c-api/libcna_c_api.so
ABI                     0.20.0
platform/renderer/audio HEADLESS / HEADLESS / NULL
XNA reference corpus    /rv/data/development/github.com/openeggbert/xna4-decomp/dlls
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
CANONICAL_FUNCTIONS=4051   BOUND_FUNCTIONS=1130
XNA_BACKING=954  JAVA_INTERNAL_ONLY=176  CNA_EXTENSION_CANDIDATE=1733
DEFERRED_RUNTIME=692  NOT_USEFUL_IN_JAVA=496  UNEXPLAINED=0

TESTS=171 SUITES=35 FAILURES=0 ERRORS=0 SKIPPED=0
```

The selected profile is now a **subset gate**: a type the wider profile declares is not an
unexpected type in the narrower one, so its zero still means what it always meant.

## What changed this session

1. The native boundary moved from ABI 0.7.0 to the live 0.20.0. Thirteen minor versions moved
   and every bound route kept its signature; three documented ABI 0.9.0 behaviour contracts did
   change, and in each case CNA moved closer to the XNA reference, so the Java expectation was
   corrected rather than the behaviour worked around. See `docs/cna-abi-migration-evidence.md`.
2. Stale JNI declarations are a compile error. All 1,130 dispatch-table slots are declared
   `CNA_JNI_ROUTE(symbol)`, whose type is the header's own declaration.
3. `tools/native-abi/generate_jni.py` generates the mechanical half of the boundary from the
   headers. It understands values, out-parameters, string views, count/copy triples, arrays,
   arrays of structs, by-value structs, flat POD structs and an opted-in null callback, and it
   refuses anything else with a diagnostic rather than guessing.
4. `Dispose()` keeps its XNA name; `close()` is the delegating AutoCloseable bridge.
5. GamerServices, Avatar and Net are projected in full.

## Honest boundaries

- **Net and GamerServices events have no native producer.** The members exist and accept
  listeners, but the eleven CNA subscribe routes take real callbacks, which the generator
  refuses by design. `JAVA-XNA-005` owns that work; nothing fabricates an event today.
- **`Guide.IsScreenSaverEnabled` does not round-trip on HEADLESS.** CNA accepts the request and
  the platform does not honour it. The projection reports what CNA reports.
- **CNA publishes a local signed-in roster** on this runtime rather than an empty one, and
  sorts a `PropertyDictionary` by key. Both are asserted as CNA's own answers.
- Everything in `docs/runtime-capabilities.json` still holds for the previously measured
  families.

## Next work, in dependency order

`docs/backlog.json` is the machine-readable source. The highest-value ready tasks are:

1. `JAVA-EXT-001` — the extended graphics layer. 1,733 routes are classified as extension
   candidates and none is reachable from Java yet; this is the largest single gap left.
2. `JAVA-XNA-005` — give the eleven session and sign-in events a real native producer.
3. `JAVA-NATIVE-011` — bind the native Model routes behind the managed XNB model graph.
4. `JAVA-TEMPLATE-001` — the extensions canary, once the first extension family exists.

Do not weaken either profile's zero, do not add an allowlist, and do not put non-XNA API inside
`Microsoft.Xna.Framework.*`.
