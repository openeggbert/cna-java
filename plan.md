# CNA-Java measured engineering plan

**Status:** selected XNA 4.0 Windows runtime projection is structurally strict-complete

**Updated:** 2026-08-23

**Selected profile:** XNA 4.0 Windows runtime projected to Java 17

**Runtime-qualified platform:** Linux x86-64, CNA HEADLESS renderer, NULL audio

## Authority and normative boundaries

Microsoft XNA 4.0 metadata, IL, and reference behavior are authoritative. CNA-C# is the strongest
behavioral engineering comparison; CNA-TS/Rust, FNA, and MonoGame remain comparisons only. CNA C
headers are authoritative only at the native boundary. The project binds only stable CNA C ABI
0.7.x and rejects ABI 0.8.

Strict API completeness and runtime capability are separate. Strict zero below applies only to the
selected seven-assembly Windows runtime profile. It is not completion of historical XNA build-time,
GamerServices/Avatar, networking, or other platform profiles, and it is not universal runtime
qualification. `docs/runtime-capabilities.json` is the machine-readable capability/blocker source.

Do not weaken the verifier, add an allowlist, expose CNA implementation types in strict packages,
or add unrelated profile types. Preserve transactional native destruction, owner-thread retry,
callback containment, reverse Game/content teardown, bound vertex/index safety, Media queue
generation invalidation, Video frame `PARENT_OWNED` aliases, and Storage
stream→container→device teardown and path containment.

## Exact strict result

The user-provided pre-milestone baseline, the Design-zero boundary, and the final selected-profile
state all use the same SHA-256-pinned seven XNA reference assemblies:

| Metric | Before | After all 13 Design types | Final |
|---|---:|---:|---:|
| REFERENCE_TYPES | 257 | 257 | 257 |
| REFERENCE_MEMBERS | 2,964 | 2,964 | 2,964 |
| EXPECTED_JAVA_TYPES | 265 | 265 | 265 |
| EXPECTED_JAVA_MEMBERS | 3,206 | 3,206 | 3,206 |
| TARGET_TYPES | 251 | 264 | 265 |
| TARGET_MEMBERS | 3,150 | 3,203 | 3,206 |
| TOTAL_DIAGNOSTICS | 14 | 1 | 0 |
| MISSING_TYPE | 14 | 1 | 0 |
| MISSING_MEMBER | 0 | 0 | 0 |
| ALLOWLIST_ENTRIES | 0 | 0 | 0 |

The final `apiCompatReport` and `apiCompatCheck` both pass. Every structural category is exactly
zero:

```text
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

Family distribution moved from:

```text
Graphics=0          -> 0
Audio/XACT=0        -> 0
Media/Video=0       -> 0
Storage=0           -> 0
Design=13           -> 0
GamerServices=1     -> 0
```

No completed family was reopened without a regression.

## Completed Design milestone

The coherent family consists of public concrete `MathTypeConverter` plus `Point`, `Rectangle`,
`Vector2`, `Vector3`, `Vector4`, `Quaternion`, `Color`, `Matrix`, `Plane`, `Ray`, `BoundingBox`, and
`BoundingSphere` converters. All thirteen are implemented and tested; no constructor-only or
signature shell remains.

The formal compact Java mapping is:

```text
TypeConverter/ExpandableObjectConverter -> Object
System.Type                              -> Class<?>
CultureInfo                              -> Locale
ITypeDescriptorContext                  -> omitted
PropertyDescriptorCollection metadata   -> LinkedHashMap<String, Class<?>>
GetProperties decomposition             -> LinkedHashMap<String, Object>
InstanceDescriptor                      -> java.beans.Expression
IDictionary                             -> Map<String, Object>
Attribute[] property filter             -> omitted
```

This mapping is encoded in `docs/xna-java-mapping.md` and
`tools/api-compat/mapping-rules.json`, with 23 passing verifier regression tests. No fake
`System.ComponentModel` namespace or synthetic public type was added.

XNA runtime probes establish that only Point, vectors, Quaternion, and Color parse/format component
strings. Rectangle, Matrix, bounds, Plane, and Ray disable string input and use base value
`toString()` fallback for output. Locale-aware decimal/list separators, seven-significant-digit
CLR Single formatting, NaN/infinity, signed zero, exponent forms, overflow, whitespace, malformed
counts, exact ordered decomposition, constructor expressions, nested value snapshots, and map
reconstruction are covered. Matrix exposes `Translation` before `M11..M44` but reconstructs from
the sixteen scalars; Color uses byte-domain RGBA.

The exact per-type and authority ledger is `docs/design-evidence.md`.

## GamerServicesComponent reconciliation

GamerServices was assessed only after Design reached local diagnostic zero. XNA metadata shows one
public extensible `GameComponent` subclass with constructor, `Initialize`, and `Update`. Exact IL
sets the Game window token, subscribes a private title-update exit handler, initializes the
dispatcher with `Game.Services`, then pumps the dispatcher per update.

The result is outcome B: strict shape and the available component lifecycle are real and native-
verified, while an actual GamerServices ecosystem remains backend-blocked. CNA-Java uses the three
canonical routes already exported by the qualified ABI 0.7 artifact:

```text
cna_gamer_services_dispatcher_set_window_handle(uint64_t)
cna_gamer_services_dispatcher_initialize(CNA_Handle)
cna_gamer_services_dispatcher_update(void)
```

The component allocates no native handle. Three successive Game lifetimes verify one initialization
and two updates each. The wider Gamer/Guide/Achievement/Avatar/Network/leaderboard profile is absent
by design. CNA exposes no selected-profile equivalent for XNA's private InstallingTitleUpdate event
and its current dispatcher provides only stub service behavior; Java fabricates neither event nor
signed-in gamers. See `docs/gamerservices-evidence.md`.

Read-only CNA HEAD was remeasured at
`1bb2145d99ed572dd4eb15009c34e2e5f410fcf0`. Networking-off C API build fails on the missing
`GameUpdateRequiredException.hpp` include; networking-on fails the renderer identity static
assertion at `49 == 50`. Neither blocker nor read-only repository was modified. The qualified ABI
0.7 artifact remains usable independently and contains all three routes.

## Native ABI

Design added no native function. GamerServices added only its three canonical non-owning routes:

```text
BEFORE_BOUND_FUNCTIONS=720
DESIGN_BOUND_FUNCTIONS=720
FINAL_BOUND_FUNCTIONS=723
HEADER_ABI=0.7.0
MANIFEST_JNI_BINDING_CHECK=PASS
LAYOUT_SIGNATURE_PROBE=PASS
LIBRARY_ABI=0.7.0
LIBRARY_SYMBOL_CHECK=PASS (723/723)
```

The probe verifies the exact `uint64_t`, `CNA_Handle`, void-parameter, and `CNA_Result` signatures.
JNI compiles as C11 with `-Wall -Wextra -Werror`. No C++ ABI, ABI 0.8, GamerServices callback, or
native component owner was introduced.

## Behavior, tests, ownership, and stress

The normalized reference-backed corpus is now:

```text
PRIOR_OBSERVATIONS=127
MATH_GEOMETRY_OBSERVATIONS=94
INPUT_OBSERVATIONS=23
MEDIA_OBSERVATIONS=10
DESIGN_OBSERVATIONS=40
TOTAL_OBSERVATIONS=167
FAILURES=0
```

The native-enabled clean suite is:

```text
TESTS=156
SUITES=32
FAILURES=0
ERRORS=0
SKIPPED=0

MEDIA_STRESS_CYCLES=40
VIDEO_STRESS_CYCLES=40
STORAGE_STRESS_CYCLES=40
MEDIA_CALLBACK_CYCLES=100
STORAGE_DEVICE_EVENT_DISPATCHES=4
GAMERSERVICES_GAME_RECREATION_CYCLES=3
CORE_GAME_RECREATION_CYCLES=25
GRAPHICS_TEXTURE_SPRITEBATCH_OWNERSHIP_CYCLES=200
AUDIO_OWNERSHIP_CYCLES=100
AUDIO_STANDALONE_LIFECYCLE_CYCLES=25
DRAW_ROUTE_ITERATIONS=25 (150 routed calls)
VERTEX_INDEX_BUFFER_CYCLES=25
NATIVE_CRASHES=0
OBSERVED_UAF=0
DOUBLE_FREE=0
SANITIZER=NOT_RUN
```

Design is managed-only. GamerServices adds no owner, callback lifetime, or disposal ordering.
Existing ownership and stress counts remain unchanged. No compatible sanitizer-built CNA runtime
was available, so allocator leak freedom is not claimed.

## Qualification and template

The final source passes:

- native-enabled `./gradlew --no-daemon clean check` with all 156 tests;
- `apiCompatReport` and `apiCompatCheck` at exact structural zero;
- all 23 verifier regression tests;
- `javadoc` and `sourcesJar` generation;
- 723/723 manifest/JNI/signature/layout/export verification;
- temporary Maven publication outside the global repository;
- unchanged sibling template build/test/install;
- freshly generated standalone consumer build/test/install without sibling/developer paths;
- 60-frame smoke and 600-frame stability runs under HEADLESS/NULL audio;
- `git diff --check` in both writable repositories.

The template source is unchanged. Design converters and GamerServices do not belong in the starter.
The pre-existing untracked `out` entry remains untouched.

## Runtime capability boundaries

Strict zero does not erase these measured boundaries:

- XACT authored-bank playback: `ASSET_PENDING`;
- real microphone capture: `HARDWARE_PENDING`;
- VideoPlayer stable two-buffer frame identity: `BACKEND_BLOCKED`;
- video decode on the qualified runtime: `BACKEND_BLOCKED` and `ASSET_PENDING`;
- populated Media catalog/pictures: `PLATFORM_PENDING`;
- native Storage path containment: `UPSTREAM_CNA_BLOCKED` while Java containment is
  `MANAGED_VERIFIED`;
- real OS-originated Storage DeviceChanged transition: `PLATFORM_PENDING`;
- GamerServices sign-in/Guide/network/title-update facilities: `BACKEND_BLOCKED`, with current CNA
  HEAD C API builds `UPSTREAM_CNA_BLOCKED`;
- CLR ComponentModel designer-host services: `LANGUAGE_MAPPING_LIMITATION`, while mapped Design
  value conversion is `MANAGED_VERIFIED`.

Future work must start from the machine-readable `docs/runtime-capabilities.json`, preserve strict
zero, and target an independently evidenced runtime capability or separate formally inventoried
profile—not add surface merely to increase a count.
