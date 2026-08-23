# CNA-Java continuation handoff

**Updated:** 2026-08-23

This is the tactical continuation point for the current uncommitted worktree. Read `plan.md`, this
file, `README.md`, `docs/xna-java-mapping.md`, `docs/architecture.md`, the verifier/native manifests,
and the focused LZX/Audio tests before changing code. Preserve the pre-existing untracked `out`
entry.

## Repository boundaries

Writable:

- this repository, `cna-java`;
- `../cna-java-template`.

Read-only references:

- `../../cna` in this checkout layout;
- `../cna-cs`, `../cna-cs-template`;
- `../cna-ts`, `../cna-ts-template`;
- `../cna-rust`, `../cna-rust-template`.

No read-only sibling was modified. CNA remains at
`1bb2145d99ed572dd4eb15009c34e2e5f410fcf0`; the existing untracked
`cmake_test_discovery_e3b0c44298.json` in that read-only worktree was observed and left untouched.
The writable template remains source-clean and deliberately has no Audio showcase.

## Exact strict state

```text
REFERENCE_TYPES=257
REFERENCE_MEMBERS=2964
EXPECTED_JAVA_TYPES=265
EXPECTED_JAVA_MEMBERS=3200
TARGET_TYPES=224
TARGET_MEMBERS=2906
TOTAL_DIAGNOSTICS=41
MISSING_TYPE=41
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

This is the exact baseline change:

```text
TARGET_TYPES: 205 -> 224
TARGET_MEMBERS: 2730 -> 2906
TOTAL_DIAGNOSTICS: 60 -> 41
MISSING_TYPE: 60 -> 41
MISSING_MEMBER: 0 -> 0
```

The exact remaining distribution is:

```text
Graphics=0
Audio/XACT=0
Media/Video=24
Storage=3
Design=13
GamerServices=1
```

The remaining types are:

```text
Microsoft.Xna.Framework.Design.BoundingBoxConverter
Microsoft.Xna.Framework.Design.BoundingSphereConverter
Microsoft.Xna.Framework.Design.ColorConverter
Microsoft.Xna.Framework.Design.MathTypeConverter
Microsoft.Xna.Framework.Design.MatrixConverter
Microsoft.Xna.Framework.Design.PlaneConverter
Microsoft.Xna.Framework.Design.PointConverter
Microsoft.Xna.Framework.Design.QuaternionConverter
Microsoft.Xna.Framework.Design.RayConverter
Microsoft.Xna.Framework.Design.RectangleConverter
Microsoft.Xna.Framework.Design.Vector2Converter
Microsoft.Xna.Framework.Design.Vector3Converter
Microsoft.Xna.Framework.Design.Vector4Converter
Microsoft.Xna.Framework.GamerServices.GamerServicesComponent
Microsoft.Xna.Framework.Media.Album
Microsoft.Xna.Framework.Media.AlbumCollection
Microsoft.Xna.Framework.Media.Artist
Microsoft.Xna.Framework.Media.ArtistCollection
Microsoft.Xna.Framework.Media.Genre
Microsoft.Xna.Framework.Media.GenreCollection
Microsoft.Xna.Framework.Media.MediaLibrary
Microsoft.Xna.Framework.Media.MediaPlayer
Microsoft.Xna.Framework.Media.MediaQueue
Microsoft.Xna.Framework.Media.MediaSource
Microsoft.Xna.Framework.Media.MediaSourceType
Microsoft.Xna.Framework.Media.MediaState
Microsoft.Xna.Framework.Media.Picture
Microsoft.Xna.Framework.Media.PictureAlbum
Microsoft.Xna.Framework.Media.PictureAlbumCollection
Microsoft.Xna.Framework.Media.PictureCollection
Microsoft.Xna.Framework.Media.Playlist
Microsoft.Xna.Framework.Media.PlaylistCollection
Microsoft.Xna.Framework.Media.Song
Microsoft.Xna.Framework.Media.SongCollection
Microsoft.Xna.Framework.Media.Video
Microsoft.Xna.Framework.Media.VideoPlayer
Microsoft.Xna.Framework.Media.VideoSoundtrackType
Microsoft.Xna.Framework.Media.VisualizationData
Microsoft.Xna.Framework.Storage.StorageContainer
Microsoft.Xna.Framework.Storage.StorageDevice
Microsoft.Xna.Framework.Storage.StorageDeviceNotConnectedException
```

## Completed managed LZX milestone

`ContentReader.create` now handles XNA compressed Windows XNB v5 through
`XnbLzxDecompression` and the stateful `LzxDecoder`. The uncompressed code path is unchanged. XNA
short and extended frame headers, default 32-KiB output frames, a persistent 64-KiB window, exact
compressed/frame/decompressed lengths, canonical zero termination, and verbatim/aligned/compressed
LZX blocks are implemented.

Deterministic legal generated fixtures prove single/multi-frame XNB, Texture2D through both framing
paths, exact output size, truncated headers/blocks, malformed lengths, trailing data rejection,
decoder failure, post-decompression reader failure, cache identity, cleanup, and `Unload`. Two
existing read-only compressed fixtures also matched their known 16,561- and 44,032-byte payloads;
none was committed. LZ4 and non-Color texture fidelity gaps remain explicit failures.

## Completed 19-type Audio/XACT milestone

The complete public contracts now exist for:

```text
AudioCategory
AudioChannels
AudioEmitter
AudioEngine
AudioListener
AudioStopOptions
Cue
DynamicSoundEffectInstance
InstancePlayLimitException
Microphone
MicrophoneState
NoAudioHardwareException
NoMicrophoneConnectedException
RendererDetail
SoundBank
SoundEffect
SoundEffectInstance
SoundState
WaveBank
```

Key evidence:

- XNA enum/default/value/copy/range/sample arithmetic is covered; 44.1-kHz mono one-second sizing
  intentionally yields 88,198 bytes.
- SoundEffect PCM and deterministic WAV construction, both Play overloads, properties, native
  instances, Apply3D, transport/state, multiple instances, close ordering, wrong-thread retry,
  failed-create recovery, and Game shutdown execute on NULL audio.
- Dynamic streaming executes native buffer submission/pending-count/transport and native event
  registration. Throwing listeners are captured inside Java, close-during-callback is safe, native
  registration is retryable, and no callback targets a closed object.
- Microphone enumeration/default executes and honestly reports no NULL devices. Real capture and
  BufferReady remain hardware-pending; no samples or devices are fabricated.
- XACT ownership, events, validation, JNI routes, and transactional release are implemented. No
  legal redistributable XGS/XSB/XWB fixture exists, so authored engine/bank/cue success remains
  asset-pending rather than fabricated.
- CNA ignores AudioEngine renderer/look-ahead parameters. CNA rejects true multi-listener 3D with
  NOT_SUPPORTED; the Java route is atomic and does not apply just one listener.

The exact per-type strict/managed/native/asset status is in `plan.md` and
`docs/audio-xact-evidence.md`.

## Native evidence

```text
starting bound functions=399
current bound functions=487
HEADER_ABI=0.7.0
MANIFEST_JNI_BINDING_CHECK=PASS
LAYOUT_SIGNATURE_PROBE=PASS
LIBRARY_ABI=0.7.0
LIBRARY_SYMBOL_CHECK=PASS (487/487)
```

The 88 additions come only from canonical `audio.h`/`xact.h` routes. Callback JNI owns global refs
until successful unsubscribe; failed unsubscribe leaves the registration retryable. Resource
wrappers mark handles disposed only after CNA accepts destruction. Continue using the qualified
ABI-0.7 runtime:

```text
/tmp/cna-java-native-working-070/modules/c-api/libcna_c_api.so
```

Do not modify CNA, bind its C++ ABI, or silently accept a different ABI.

## Test, stress, sanitizer, and template evidence

Final native-enabled test evidence:

```text
tests=118
suites=24
failures=0
errors=0
skipped=0
```

Stress is green for 25 Game cycles, 200 Texture2D/SpriteBatch cycles, 100 Audio ownership/dynamic-
registration cycles, an additional 25 SoundEffect/instance pairs, 25 bound-buffer cycles, and 150
draw calls. Failed native Audio creation recovers. Wrong-thread effect/dynamic close returns CNA
result 8 without losing the handle, then succeeds on the owner thread. No sanitizer-compatible CNA
runtime was available, so allocator-level leak freedom is not claimed.

Verified commands/evidence:

```text
CNA_NATIVE_LIBRARY=... ./gradlew --no-daemon check
XNA_REFERENCE_DIR=/tmp/xna-probe-out ./gradlew --no-daemon apiCompatReport
XNA_REFERENCE_DIR=/tmp/xna-probe-out ./gradlew --no-daemon apiCompatCheck
./gradlew --no-daemon javadoc sourcesJar
native ABI/library export check: 487/487
CNA_NATIVE_LIBRARY=... CNA_RUN_STABILITY_TEST=1 scripts/verify-template.sh
template: sibling build/test/install PASS
template: generated standalone build/test/install PASS
template: 60 frames PASS
template: 600 frames PASS
git diff --check: both writable repositories PASS
```

`apiCompatCheck` exits 1 only for the 41 missing whole types. The fresh consumer resolved only the
temporary Maven publication and contains no sibling/developer path.

## Recommended next work

The next coherent dependency group is all 24 Media/Video types. Use XNA metadata/IL as authority
and CNA-C# as evidence, preserve Media queue/library identity and Video texture ownership honesty,
and do not land enum-only progress. After Media/Video, complete Storage, then the 13 Design
converters and GamerServices.

Do not revisit completed Graphics, stock effects, Model, existing readers, or Audio without a
concrete regression. Preserve `MISSING_MEMBER=0`, every zero category, the empty allowlist,
transactional Audio release, callback containment, reverse-order content ownership, and the bound
vertex/index guard.
