# CNA-Java measured engineering plan

**Status:** Graphics, Audio/XACT, all 24 Media/Video types, and all three Storage types are
structurally complete; 14 whole types remain

**Updated:** 2026-08-23

**Selected profile:** XNA 4.0 Windows runtime projected to Java 17

**Runtime-qualified platform:** Linux x86-64, CNA HEADLESS renderer, NULL audio

## Authority and invariants

Microsoft XNA 4.0 metadata and IL remain authoritative. CNA-C# is the strongest existing
Media/Video and Storage ownership/behavior reference; FNA, MonoGame, CNA-TS, and CNA-Rust are
comparison implementations. CNA's stable C ABI is the native implementation boundary, not the
authority for the Java public contract.

The completed work adds no allowlist, partial public type shell, CNA-internal public signature,
fake media catalog, fake video frame, or duplicate native owner. Existing Graphics/XNB/Model,
Audio/XACT, and Media/Video behavior is unchanged except for the shared Game teardown and dispatcher
integration needed by Storage. Preserve transactional native release, owner-thread retry after
refusal, callback exception containment, reverse Game/content ownership, the bound vertex/index
guard, and exact ABI 0.7.

## Exact strict measurement

The requested Media baseline, completed Media state, and current post-Storage state use the same
seven XNA reference assemblies:

| Metric | Before Media | Post-Media | Current | Total change |
|---|---:|---:|---:|---:|
| REFERENCE_TYPES | 257 | 257 | 257 | 0 |
| REFERENCE_MEMBERS | 2,964 | 2,964 | 2,964 | 0 |
| EXPECTED_JAVA_TYPES | 265 | 265 | 265 | 0 |
| EXPECTED_JAVA_MEMBERS | 3,200 | 3,207 | 3,206 | +6 |
| TARGET_TYPES | 224 | 248 | 251 | +27 |
| TARGET_MEMBERS | 2,906 | 3,115 | 3,150 | +244 |
| TOTAL_DIAGNOSTICS | 41 | 17 | 14 | -27 |
| MISSING_TYPE | 41 | 17 | 14 | -27 |
| MISSING_MEMBER | 0 | 0 | 0 | 0 |
| ALLOWLIST_ENTRIES | 0 | 0 | 0 | 0 |

The seven Media collection `iterator()` methods are formal Java `Iterable<T>` bridge obligations.
The one expected-member reduction at Storage is the exact exclusion of the protected CLR
`StorageDeviceNotConnectedException(SerializationInfo, StreamingContext)` constructor, for which
Java has no equivalent serialization protocol. This is a mapping rule, not an allowlist.

Every strict mismatch, unexpected, leak, and mapping-drift category is exactly zero:

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

`apiCompatReport` succeeds with 14 whole-type diagnostics. `apiCompatCheck` exits 1 only for those
types; it has no missing-member or structural diagnostic.

Current family distribution:

```text
Graphics=0
Audio/XACT=0
Media/Video=0
Storage=0
Design=13
GamerServices=1
```

## Completed Media/Video milestone

All 24 mapped Media/Video types have their complete selected-profile public contract. The managed
graph provides stable read-only collections and stable relationship identity without fabricating
platform records. MediaLibrary exposes only the canonical platform result. Owned URI songs,
process-global MediaPlayer state, its per-Game queue generation, visualization, native callbacks,
and Game recreation are directly tested.

VideoReader follows the authoritative boxed XNB layout. VideoPlayer executes the CNA control path,
preserves XNA cached loop/mute/volume behavior after disposal, and deliberately accepts NaN volume.
`GetTexture` can only create a parent-owned, non-destroying transient Texture2D facade; CNA ABI 0.7
lacks stable/two-buffer identity and a generation token, and HEADLESS produced no decoded frame.
No frame pixels or stronger identity are claimed. The exact 24-type ledger and upstream boundaries
remain in `docs/media-video-evidence.md`.

## Completed Storage milestone

All three Storage types are complete together:

```text
StorageContainer
StorageDevice
StorageDeviceNotConnectedException
```

Authoritative XNA IL establishes a fake-async contract: every `Begin` result is already completed,
invokes its callback synchronously, retains state, and defers device/container construction until
the corresponding one-shot `End`. Foreign results and double End fail. Negative `sizeInBytes` is
rejected at Begin; negative `directoryCount` is accepted as XNA does and normalized only for CNA's
stricter native input. All four selector routes execute.

StorageDevice is a non-disposable public facade over an owned Game-scoped native handle. It owns
containers, and containers retain the device and own every native stream. Teardown walks streams,
containers, then devices in reverse order before the native Game. Explicit and shutdown close are
idempotent after success and retryable after result-8 wrong-thread refusal.

Container CRUD, enumeration patterns, display/device identity, file modes/access/share, stream
read/write/seek/position/length/capabilities/flush/close, disposal, child-before-parent,
parent-before-child, and use-after-close are native-verified. The ABI 0.7 native `Disposing`
callback is observed exactly once, correcting the uncertainty from older CNA-C# qualification.
JNI records that callback but user handlers run only after the native frame returns; duplicate,
self-removing, recursive-close, throwing, and later handlers are covered.

`StorageDevice.DeviceChanged` uses a single process-owned CNA registration and the existing
FrameworkDispatcher owner-thread queue. Worker-thread enqueue, duplicate removal, throwing-handler
containment, later handlers, shutdown discard, and Game recreation are verified. No real host
device transition occurred, so backend-originated callback delivery remains platform-pending.

The qualified CNA 0.7 path routes accepted parent traversal that XNA's canonicalized containment
check rejects. Java therefore rejects null/empty, Unix-rooted, Windows-rooted/drive, and normalized
escaping paths before JNI. This is documented as a native semantic gap rather than credited to the
C route. The full Storage ledger is in `docs/storage-evidence.md`.

## JNI and native ABI

The project began this work with 487 reviewed functions, reached 679 after Media/Video, and now
binds 720 after 41 canonical Storage additions.

```text
PROJECT_STARTING_BOUND_FUNCTIONS=487
MEDIA_FINAL_BOUND_FUNCTIONS=679
STORAGE_STARTING_BOUND_FUNCTIONS=679
FINAL_BOUND_FUNCTIONS=720
HEADER_ABI=0.7.0
MANIFEST_JNI_BINDING_CHECK=PASS
LAYOUT_SIGNATURE_PROBE=PASS
LIBRARY_ABI=0.7.0
LIBRARY_SYMBOL_CHECK=PASS (720/720)
```

The signature/layout probe covers every bound function, Media PODs, and Storage's 32-bit
FileMode/FileAccess/FileShare/SeekOrigin values and 64-bit device/container/stream handles. JNI
compiles as C11 with `-Wall -Wextra -Werror`. The loader still requires ABI 0.7.x and rejects ABI
0.8; no C++ ABI is bound.

Read-only CNA HEAD was rechecked once and remains
`1bb2145d99ed572dd4eb15009c34e2e5f410fcf0`. Its unrelated networking-off GamerServices header and
networking-on renderer-identity blockers remain out of scope. Runtime evidence continues to use
the previously qualified artifact at
`/tmp/cna-java-native-working-070/modules/c-api/libcna_c_api.so`.

## Behavior, ownership, and stress

The native-enabled suite is 141 tests across 29 suites, with zero failures, errors, or skips. The
normalized reference-backed corpus remains:

```text
observations=127
math/geometry=94
input=23
Media=10
```

Storage filesystem/capacity results remain direct assertions because they depend on the isolated
qualified host root. Nine Storage tests add authoritative managed values plus native fake-async,
CRUD, event, containment, ownership, wrong-thread, and 40-lifetime stress coverage.

```text
MEDIA_STRESS_CYCLES=40
VIDEO_STRESS_CYCLES=40
STORAGE_STRESS_CYCLES=40
MEDIA_CALLBACK_CYCLES=100
STORAGE_DEVICE_EVENT_DISPATCHES=4
NATIVE_CRASHES=0
OBSERVED_UAF=0
DOUBLE_FREE=0
```

No sanitizer-built compatible CNA runtime was available, so allocator leak freedom is not claimed.

## Verification and template

The final source passes:

- native-enabled `clean check` with 141/141 tests;
- report-only compatibility at exactly 14 missing whole types;
- expected-failing strict completeness check with exit 1 only for those 14 types;
- Javadoc and source JAR generation;
- 720/720 manifest/JNI/signature/layout/export verification;
- temporary Maven publication;
- unchanged sibling template build/test/install;
- freshly generated standalone consumer build/test/install without sibling/developer paths;
- 60-frame smoke and 600-frame stability runs under HEADLESS/NULL audio;
- `git diff --check` in both writable repositories.

The template source was not changed. It remains the deliberately small Game/PNG/managed-XNB/
SpriteBatch/input/cleanup canary rather than a Media or Storage showcase.

## Remaining dependency groups

The 14 remaining whole types are the thirteen `Microsoft.Xna.Framework.Design` converters and
`Microsoft.Xna.Framework.GamerServices.GamerServicesComponent`. Do not create converter shells or
revisit completed families without a concrete regression. The next coherent implementation group
is all 13 Design types; the single GamerServices type remains entangled with the already documented
upstream networking-off detail-header problem and must not be papered over in Java.

Preserve `MISSING_MEMBER=0`, every zero mismatch category, the empty allowlist, exact ABI 0.7,
transactional destruction/retry, callback containment, reverse Game/content teardown, Media queue
generation invalidation, the borrowed Video frame rule, Storage's explicit containment boundary,
and the bound vertex/index guard.
