# CNA-Java continuation handoff

**Updated:** 2026-08-23

The complete Graphics/XNB/Model, Audio/XACT, 24-type Media/Video, and three-type Storage milestones
are finished in the current uncommitted worktree. Do not redo them without a concrete regression.
Preserve the pre-existing untracked `out` entry.

Read `plan.md`, this file, `README.md`, `docs/xna-java-mapping.md`,
`docs/architecture.md`, `docs/media-video-evidence.md`, `docs/storage-evidence.md`, the strict/native
manifests, and the focused Media and Storage tests before changing shared lifecycle or callbacks.

## Repository and provenance

Writable: this repository and `../cna-java-template`. The CNA, CNA-C#, CNA-TS, CNA-Rust, and their
template siblings are read-only references. No read-only sibling was modified. CNA HEAD was
rechecked at `1bb2145d99ed572dd4eb15009c34e2e5f410fcf0`; its unrelated networking-off
GamerServices detail-header and networking-on renderer-identity blockers were not repaired.

Continue using the qualified artifact:

```text
/tmp/cna-java-native-working-070/modules/c-api/libcna_c_api.so
ABI=0.7.0
Linux x86-64
HEADLESS renderer
NULL audio
```

## Exact strict state

```text
REFERENCE_TYPES=257
REFERENCE_MEMBERS=2964
EXPECTED_JAVA_TYPES=265
EXPECTED_JAVA_MEMBERS=3206
TARGET_TYPES=251
TARGET_MEMBERS=3150
TOTAL_DIAGNOSTICS=14
MISSING_TYPE=14
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

Transition from the requested baseline:

```text
EXPECTED_JAVA_MEMBERS: 3200 -> 3207 after Media -> 3206 after Storage serialization exclusion
TARGET_TYPES: 224 -> 248 after Media -> 251 after Storage
TARGET_MEMBERS: 2906 -> 3115 after Media -> 3150 after Storage
TOTAL_DIAGNOSTICS: 41 -> 17 after Media -> 14 after Storage
MISSING_TYPE: 41 -> 17 after Media -> 14 after Storage
MISSING_MEMBER: 0 -> 0
```

Current distribution:

```text
Graphics=0
Audio/XACT=0
Media/Video=0
Storage=0
Design=13
GamerServices=1
```

The 14 missing whole types are the thirteen
`Microsoft.Xna.Framework.Design` converters (`MathTypeConverter` plus the BoundingBox,
BoundingSphere, Color, Matrix, Plane, Point, Quaternion, Ray, Rectangle, Vector2, Vector3, and
Vector4 converters) and
`Microsoft.Xna.Framework.GamerServices.GamerServicesComponent`.

## Completed Media/Video state

All 24 types are strict complete. MediaLibrary uses only platform routes and may be empty on the
qualified runtime. URI Song, static MediaPlayer, stable per-Game MediaQueue generation, native
events through the existing FrameworkDispatcher pump, and Game recreation are verified.

Video XNB metadata and native player controls are implemented. `VideoPlayer.GetTexture` never owns
the CNA frame: a nonzero handle becomes a parent-owned facade invalidated before the next player
operation. CNA lacks XNA stable/two-buffer identity and a generation token; HEADLESS produced no
frame. Do not fabricate pixels or strengthen the identity claim. See `docs/media-video-evidence.md`.

## Completed Storage state

The coherent group is `StorageContainer`, `StorageDevice`, and
`StorageDeviceNotConnectedException`. Public CLR support carriers were added under `System` and
`System.IO`: `IAsyncResult`, `AsyncCallback`, `FileMode`, `FileAccess`, `FileShare`, `SeekOrigin`,
and read/write/seek extensions on `Stream`. The protected exception serialization constructor is
excluded by exact signature; ordinary public constructors remain strict.

Important behavior:

- XNA `Begin` returns an already-completed carrier, invokes the callback synchronously, and creates
  the device/container only on one-shot `End`. Foreign and repeated End fail.
- All four selectors execute. XNA validates negative size but not negative directory count; Java
  preserves that and normalizes the latter only for CNA's stricter native route.
- StorageDevice has no public close. The current Game owns its native handle; devices own
  containers and containers own streams. Game teardown closes streams/containers/devices in
  reverse order before native Game destruction.
- Container CRUD, enumeration patterns, display/device identity, stream read/write/seek, and all
  selected file mode/access/share routes execute natively.
- Wrong-thread release returns result 8 without clearing Java ownership and then succeeds on the
  owner thread.
- ABI 0.7 native `Disposing` is observed exactly once. JNI records only; Java invokes user handlers
  after the native frame. Duplicate, self-removal, recursive close, throwing/later handlers, and
  double close pass.
- `StorageDevice.DeviceChanged` has one process-global native registration and the existing owner-
  thread dispatcher queue. Worker enqueue, duplicate removal, throwing/later handlers, shutdown
  discard, and recreation pass. An actual OS-originated event is platform-pending.

Exact CNA gap: ABI 0.7 accepts `..` paths outside a container, while XNA canonicalizes and rejects
them. Java rejects null/empty, absolute Unix, Windows drive/root, and normalized escaping paths
before JNI. Keep this visible in `docs/storage-evidence.md`; do not credit containment to CNA until
the native route supplies it.

Primary Storage implementation files:

```text
src/main/java/Microsoft/Xna/Framework/Storage/*
src/main/java/System/IAsyncResult.java
src/main/java/System/AsyncCallback.java
src/main/java/System/IO/{Stream,FileMode,FileAccess,FileShare,SeekOrigin}.java
src/main/java/org/openeggbert/cna/internal/NativeStorage.java
src/main/java/org/openeggbert/cna/internal/NativeStorageStream.java
src/main/c/cna_java_jni.c
src/test/java/Microsoft/Xna/Framework/Storage/*
tools/native-abi/{bindings.json,probe.c}
```

## Native evidence

```text
PROJECT_STARTING_BOUND_FUNCTIONS=487
MEDIA_FINAL_BOUND_FUNCTIONS=679
STORAGE_ADDITIONS=41
FINAL_BOUND_FUNCTIONS=720
HEADER_ABI=0.7.0
MANIFEST_JNI_BINDING_CHECK=PASS
LAYOUT_SIGNATURE_PROBE=PASS
LIBRARY_ABI=0.7.0
LIBRARY_SYMBOL_CHECK=PASS (720/720)
```

The Storage additions are canonical selectors, device properties/delete/event/destroy, container
open/display/dispose/event/CRUD/names/destroy, and owned stream operations. The probe covers every
signature plus 32-bit Storage option values and 64-bit handle aliases. JNI compiles as C11 with
`-Wall -Wextra -Werror`. Do not bind the C++ ABI or accept ABI 0.8.

## Tests, behavior, stress, and template

```text
tests=141
suites=29
failures=0
errors=0
skipped=0

behavior observations=127
math/geometry=94
input=23
Media=10

MEDIA_STRESS_CYCLES=40
VIDEO_STRESS_CYCLES=40
STORAGE_STRESS_CYCLES=40
MEDIA_CALLBACK_CYCLES=100
STORAGE_DEVICE_EVENT_DISPATCHES=4
NATIVE_CRASHES=0
OBSERVED_UAF=0
DOUBLE_FREE=0
sanitizer=NOT_RUN
```

Verified gates:

```text
native-enabled clean check: PASS
apiCompatReport: PASS, exactly 14 missing whole types
apiCompatCheck: expected exit 1, solely those 14 types
javadoc sourcesJar: PASS
native ABI/library exports: PASS 720/720
temporary Maven publication: PASS
sibling template build/test/install: PASS
fresh generated consumer build/test/install: PASS
60 frames: PASS
600 frames: PASS
global Maven repository used: NO
template source changed: NO
git diff --check, both writable repositories: PASS
```

No sanitizer-compatible CNA runtime was used, so allocator leak freedom is not claimed. The
template remains the small Game/PNG/managed-XNB/SpriteBatch/input canary and was not turned into a
Media or Storage showcase.

## Recommended next work

The next coherent group is all 13 Design converters. Do not add signature shells or implement only
a subset of the shared converter graph. Recheck XNA metadata/IL and the formal Java TypeConverter
mapping before adding any public Design type. GamerServices is separate and still has the upstream
networking-off header blocker noted above.

Preserve the empty allowlist, `MISSING_MEMBER=0`, every zero mismatch category, exact ABI 0.7,
transactional release/retry, callback containment, reverse ownership, the Media queue generation,
borrowed Video frame rule, explicit Storage path boundary, and bound-buffer safety guard.
