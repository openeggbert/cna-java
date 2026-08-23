# CNA-Java continuation handoff

**Updated:** 2026-08-23

The selected XNA 4.0 Windows runtime projection is structurally strict-complete in the current
uncommitted worktree. Graphics/XNB/Model, Audio/XACT, Media/Video, Storage, all thirteen Design
converters, and the selected `GamerServicesComponent` are finished. Do not revisit a completed
family without a concrete regression. Preserve the pre-existing untracked `out` entry.

Read `plan.md`, this file, `README.md`, `docs/xna-java-mapping.md`, `docs/architecture.md`,
`docs/design-evidence.md`, `docs/gamerservices-evidence.md`, and
`docs/runtime-capabilities.json` before new work. Media/Video and Storage details remain in their
focused evidence files.

## Repository and provenance

Writable: this repository and `../cna-java-template`. CNA, CNA-C#, CNA-TS, and CNA-Rust siblings
are read-only references. No read-only repository was modified. The sibling template source was
not changed.

Continue native qualification with:

```text
/tmp/cna-java-native-working-070/modules/c-api/libcna_c_api.so
ABI=0.7.0
Linux x86-64
HEADLESS renderer
NULL audio
```

The seven authoritative reference assemblies are SHA-256-pinned by
`tools/api-compat/profiles/xna40-windows-runtime.json`; the current local reference directory used
for qualification was `/tmp/xna-probe-out`.

## Exact strict state

```text
REFERENCE_TYPES=257
REFERENCE_MEMBERS=2964
EXPECTED_JAVA_TYPES=265
EXPECTED_JAVA_MEMBERS=3206
TARGET_TYPES=265
TARGET_MEMBERS=3206
TOTAL_DIAGNOSTICS=0
MISSING_TYPE=0
MISSING_MEMBER=0

ACCESSIBILITY_MISMATCH=0
BASE_TYPE_MISMATCH=0
CNA_INTERNAL_LEAK=0
ENUM_VALUE_MISMATCH=0
FIELD_TYPE_MISMATCH=0
GENERIC_MISMATCH=0
INTERFACE_MISMATCH=0
MEMBER_MODIFIER_MISMATCH=0
PARAMETER_MISMATCH=0
PARAMETER_NAME_MISMATCH=0
RETURN_TYPE_MISMATCH=0
TYPE_KIND_MISMATCH=0
TYPE_MODIFIER_MISMATCH=0
UNEXPECTED_MEMBER=0
UNEXPECTED_TYPE=0
XNA_MAPPING_MISMATCH=0
ALLOWLIST_ENTRIES=0
```

Transition from the milestone baseline:

```text
TARGET_TYPES:       251 -> 264 after Design -> 265 final
TARGET_MEMBERS:    3150 -> 3203 after Design -> 3206 final
TOTAL_DIAGNOSTICS:   14 ->    1 after Design ->    0 final
MISSING_TYPE:         14 ->    1 after Design ->    0 final
MISSING_MEMBER:        0 ->    0 -> 0
```

Final family distribution:

```text
Graphics=0
Audio/XACT=0
Media/Video=0
Storage=0
Design=0
GamerServices=0
```

Both `apiCompatReport` and `apiCompatCheck` pass. This is strict zero only for the selected Windows
runtime mapping, not all historical XNA profiles or all runtime capabilities.

## Design converter state

All thirteen types exist under `src/main/java/Microsoft/Xna/Framework/Design/` and must remain a
coherent family:

```text
MathTypeConverter
PointConverter RectangleConverter
Vector2Converter Vector3Converter Vector4Converter QuaternionConverter
ColorConverter MatrixConverter PlaneConverter RayConverter
BoundingBoxConverter BoundingSphereConverter
```

The formal mapping uses `Class<?>`, `Locale`, ordered `LinkedHashMap` metadata/value decomposition,
`Expression`, and `Map<String,Object>`. It omits the non-equivalent CLR context and attribute-array
parameters. Do not add a fake ComponentModel namespace. The exact projection lives in both the
mapping document and JSON rules, backed by 23 verifier regression tests.

Only Point, vectors, Quaternion, and Color accept component strings. Rectangle, Matrix, bounds,
Plane, and Ray disable string input and use base `toString()` fallback. Formatting/parsing is
culture-aware and follows CLR Single general formatting, including seven significant digits,
NaN/infinity, scientific notation, and signed-zero output. Property order is explicit; Matrix is
`Translation,M11..M44`, while creation ignores Translation and consumes all sixteen scalar keys.
Nested components are snapshots. Create maps require exact named boxed values and ignore extras.

Focused tests and the forty-observation Design corpus are under
`src/test/java/Microsoft/Xna/Framework/Design/`. Full reasoning and per-type order are in
`docs/design-evidence.md`.

## GamerServicesComponent state

The selected type is implemented at
`src/main/java/Microsoft/Xna/Framework/GamerServices/GamerServicesComponent.java`. It extends
`GameComponent`; its constructor delegates to the base, `Initialize` sets the window token and
initializes the canonical dispatcher, and `Update` pumps that dispatcher before the inherited
null check. It owns no native handle and requires no teardown.

Three added canonical ABI 0.7 functions bring the manifest from 720 to 723:

```text
cna_gamer_services_dispatcher_set_window_handle(uint64_t)
cna_gamer_services_dispatcher_initialize(CNA_Handle)
cna_gamer_services_dispatcher_update(void)
```

The focused native test covers three Game recreation cycles. Do not add the separate Gamer,
SignedInGamer, Guide, Achievement, Avatar, Network, or leaderboard profiles. XNA's private
InstallingTitleUpdate→Game.Exit subscription has no current CNA selected-profile event route, and
real GamerServices facilities remain backend-blocked. Strict shape and available lifecycle are
real; runtime ecosystem completeness is not claimed.

Read-only CNA HEAD is still `1bb2145d99ed572dd4eb15009c34e2e5f410fcf0`. Fresh remeasurement:

- networking off C API build fails on missing `GameUpdateRequiredException.hpp`;
- networking on C API build fails the renderer identity assertion at `49 == 50`.

Do not repair or modify that read-only checkout. The qualified ABI 0.7 artifact independently
exports all 723 bound symbols.

## Native, tests, behavior, ownership

```text
BOUND_FUNCTIONS=723
HEADER_ABI=0.7.0
MANIFEST_JNI_BINDING_CHECK=PASS
LAYOUT_SIGNATURE_PROBE=PASS
LIBRARY_ABI=0.7.0
LIBRARY_SYMBOL_CHECK=PASS (723/723)

TESTS=156
SUITES=32
FAILURES=0
ERRORS=0
SKIPPED=0

PRIOR_OBSERVATIONS=127
DESIGN_OBSERVATIONS=40
TOTAL_OBSERVATIONS=167
CORPUS_FAILURES=0
```

Existing stress evidence is unchanged: core Game recreations 25; graphics Texture/SpriteBatch
ownership 200; audio ownership 100; standalone audio lifecycles 25; draw routes 25 iterations/150
calls; vertex/index buffers 25; Media 40; Video 40; Storage 40; Media callbacks 100; and Storage
DeviceChanged dispatches 4. GamerServices adds three Game recreation cycles. There were zero
crashes, observed UAF, or double-free. No sanitizer-compatible CNA runtime was available.

Preserve all existing ownership invariants: transactional destroy and wrong-thread retry; reverse
Game/resource/content teardown; Effect parent-child ownership; Video transient parent-owned frame
aliases; Media queue generation; Storage stream→container→device order and managed path
containment; bound vertex/index safety; and callback exception containment.

## Final qualification evidence

The current worktree passed:

```text
native-enabled clean check: PASS (156/156)
apiCompatReport: PASS, zero diagnostics
apiCompatCheck: PASS, zero diagnostics
verifier regression tests: PASS (23/23)
javadoc sourcesJar: PASS
native ABI/library exports: PASS (723/723)
temporary Maven publication: PASS
sibling template build/test/install: PASS
fresh standalone generated consumer build/test/install: PASS
60 frames: PASS
600 frames: PASS
global Maven repository used: NO
template source changed: NO
git diff --check, both writable repositories: PASS
```

The template remains the Game/PNG/managed-XNB/SpriteBatch/input/cleanup canary. Do not add Design
or GamerServices solely as a showcase.

## Honest next work

There is no remaining strict diagnostic in this profile. Choose future work from
`docs/runtime-capabilities.json` or formally inventory a separate profile first. Current important
boundaries are XACT authored assets, microphone hardware, Video decode/frame identity, populated
Media catalogs/pictures, native Storage containment, real OS DeviceChanged delivery, real
GamerServices facilities, and the deliberate Java-vs-CLR designer-host mapping limitation.

Any follow-up must preserve strict zero, ABI 0.7, the empty allowlist, and the distinction between
structural completeness and runtime evidence.
