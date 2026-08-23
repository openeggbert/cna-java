# CNA-Java measured engineering plan

**Status:** Graphics and Audio/XACT type-complete; member-complete, structurally strict XNA 4.0 Java projection with 41 dependency-group types remaining

**Updated:** 2026-08-23

**Selected profile:** XNA 4.0 Windows runtime projected to Java 17

**Runtime-qualified platform:** Linux x86-64, CNA HEADLESS renderer, NULL audio

## Authority and invariants

Microsoft XNA 4.0 metadata and IL remain authoritative. CNA-C# is a strong engineering and
behavioral reference; FNA and MonoGame are comparison implementations. CNA's stable C ABI is the
native implementation boundary, not the authority for the public Java contract.

This milestone completed managed LZX XNB framing and the whole selected 19-type Audio/XACT group.
It added no allowlist entry, public reader type, public JNI/native type, or partial mapped XNA type.
The mapping of `System.Runtime.InteropServices.ExternalException` to unchecked
`java.lang.RuntimeException` is a real deterministic CLR-to-Java rule, documented in
`docs/xna-java-mapping.md`; it is not an exception or allowlist.

## Exact milestone measurement

The run began at the requested hard baseline and finished against the same seven XNA reference
assemblies:

| Metric | Before | Current | Change |
|---|---:|---:|---:|
| REFERENCE_TYPES | 257 | 257 | 0 |
| REFERENCE_MEMBERS | 2,964 | 2,964 | 0 |
| EXPECTED_JAVA_TYPES | 265 | 265 | 0 |
| EXPECTED_JAVA_MEMBERS | 3,200 | 3,200 | 0 |
| TARGET_TYPES | 205 | 224 | +19 |
| TARGET_MEMBERS | 2,730 | 2,906 | +176 |
| TOTAL_DIAGNOSTICS | 60 | 41 | -19 |
| MISSING_TYPE | 60 | 41 | -19 |
| MISSING_MEMBER | 0 | 0 | 0 |
| CNA_INTERNAL_LEAK | 0 | 0 | 0 |
| ALLOWLIST_ENTRIES | 0 | 0 | 0 |

Every strict mismatch/unexpected category is exactly zero:

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

`apiCompatReport` succeeds with exactly 41 whole-type diagnostics. `apiCompatCheck` exits 1 only
because those complete types remain absent.

## Managed LZX XNB framing result

The existing uncompressed Windows XNB v5 path is unchanged. For the XNA compressed flag, the
reader now consumes the declared decompressed size and XNA's LZX frame stream, including the
ordinary 2-byte big-endian block length and the `0xFF` extended frame/block header. A stateful
64-KiB LZX window is retained across frames; verbatim, aligned, and uncompressed LZX blocks are
decoded. Each frame is capped at XNA's 32-KiB output size, declared compressed/frame lengths are
checked before reads, and the final byte count must exactly equal the XNB decompressed-size field.
Only the canonical zero end marker/padding is accepted after the declared output is complete.

Deterministic generated legal fixtures cover single- and multi-frame assets, the default 32-KiB
frame size, truncated short/extended headers, truncated compressed blocks, malformed block/frame
lengths, declared output too short or too long, non-zero trailing framing, decoder failure, and a
reader failure after successful decompression. Cache identity, `Unload`, and partial-failure cleanup
are exercised through the normal `ContentManager` path. The same Texture2D reader is tested through
both uncompressed and compressed XNB framing; there is no parallel fake content route.

As an independent decoder check, two existing read-only legal compressed XNB fixtures decompressed
byte-for-byte to their known payloads (16,561 and 44,032 bytes). No fixture was copied or committed.
LZ4 and unsupported texture formats remain explicit failures; compressed texture bytes are never
reinterpreted as RGBA.

## Audio/XACT result

All 19 mapped types have their full selected-profile public contract. The pure types follow XNA
enum values, constructor/default behavior, defensive copies of mutable vectors, value equality,
hashing, and null/range behavior. `AudioCategory` uses CNA's canonical name/equality/hash routes so
independently obtained category values compare by XNA category identity rather than owned C-handle
address.

`SoundEffect` owns one native effect. Every `SoundEffectInstance` owns its own voice and strongly
retains its parent; parent close walks live children first. Java state changes only after CNA accepts
destruction, so a wrong-thread release is refused with the wrapper still live and retryable.
Construction from PCM and a deterministic generated PCM WAV, both Play overloads, sample
arithmetic, duration, name, global settings, multiple instances, properties, transport, state,
single-listener/array Apply3D, idempotent close, disposed use, parent-first/child-first close,
failed native creation, and game shutdown are covered. The observed XNA float arithmetic remains
intentional: one second of 44.1-kHz mono maps to 88,198 bytes.

`DynamicSoundEffectInstance` uses CNA's real streaming instance and event registration. Buffers and
slices, pending count, properties, transport, register/remove/re-register lifetime, listener
exception capture, close during callback, no callback after close, wrong-thread retry, repeated
creation, and game shutdown are tested. Java listener failures never unwind through JNI; the next
owner call or close surfaces the captured failure after cleanup. FrameworkDispatcher remains the
owner-thread pump.

`Microphone` exposes the exact selected contract and stable per-game device wrappers. The qualified
NULL runtime honestly reports no devices and no default; it does not fabricate samples or device
presence. Device enumeration/default JNI routes execute, while Start/Stop/GetData/BufferReady with
real capture hardware remain hardware-pending. Wrappers from a destroyed Game are generation-
invalidated, and shutdown unsubscribes native event registrations.

The XACT graph is explicit: `AudioEngine` owns native engine/category handles, `WaveBank` and
`SoundBank` strongly retain and register with the engine, and `Cue` retains the engine and bank
bookkeeping required for safe destruction. Engine close walks dependants and categories before the
native root. Double close, disposed access, one-shot post-disposal events, event-handler exception
cleanup, constructor validation, transactional release, and all ABI routes are implemented.

No legal redistributable authored XGS/XSB/XWB fixture is present. Authored engine/bank/cue creation,
lookup, playback, variables, state transitions, and bank ordering therefore remain asset-pending;
they are not reported as runtime-qualified. CNA currently accepts but ignores the AudioEngine
look-ahead and renderer arguments. NULL audio reports zero renderer details. True multi-listener
SoundEffectInstance mixing is explicitly unsupported by CNA; the atomic multi-listener route fails
rather than applying listeners sequentially. Empty listener arrays also fail explicitly.

### Per-type evidence ledger

| Type | Evidence status |
|---|---|
| AudioCategory | strict complete; managed default/copy behavior; canonical CNA name/equality/hash ABI; authored engine asset-pending |
| AudioChannels | strict complete; managed enum values verified |
| AudioEmitter | strict complete; managed defaults, validation, defensive vector copies; CNA single-listener 3D verified |
| AudioEngine | strict complete; managed validation/ownership/events; CNA ABI/export verified; authored creation asset-pending; renderer/look-ahead ignored by CNA |
| AudioListener | strict complete; managed defaults and defensive vector copies; CNA single-listener 3D verified |
| AudioStopOptions | strict complete; managed enum values verified |
| Cue | strict complete; managed ownership/disposal; CNA ABI/export verified; authored cue behavior asset-pending |
| DynamicSoundEffectInstance | strict complete; managed behavior verified; CNA native streaming/callback route verified |
| InstancePlayLimitException | strict complete; managed constructors/inheritance verified |
| Microphone | strict complete; managed validation/cache behavior; CNA NULL enumeration/default verified; capture hardware-pending |
| MicrophoneState | strict complete; managed enum values verified |
| NoAudioHardwareException | strict complete; managed constructors/inheritance verified |
| NoMicrophoneConnectedException | strict complete; managed constructors/inheritance verified |
| RendererDetail | strict complete; managed value/default/copy behavior verified; NULL renderer list verified empty |
| SoundBank | strict complete; managed ownership/disposal; CNA ABI/export verified; authored bank behavior asset-pending |
| SoundEffect | strict complete; managed arithmetic/validation/ownership; CNA PCM/WAV/Play/global settings verified |
| SoundEffectInstance | strict complete; managed ownership/properties; CNA transport/state/Apply3D/release verified |
| SoundState | strict complete; managed enum values verified |
| WaveBank | strict complete; managed ownership/disposal; CNA ABI/export verified; authored bank behavior asset-pending |

## JNI and native ABI

Bound functions grew from 399 to 487. The 88 reviewed additions cover SoundEffect, instance,
dynamic streaming/callback, microphone, AudioEngine/category, WaveBank, SoundBank, and Cue routes.
All functions come from the canonical CNA C headers; JNI compiles as C11 with
`-Wall -Wextra -Werror`.

```text
HEADER_ABI=0.7.0
BOUND_FUNCTIONS=487
MANIFEST_JNI_BINDING_CHECK=PASS
LAYOUT_SIGNATURE_PROBE=PASS
LIBRARY_ABI=0.7.0
LIBRARY_SYMBOL_CHECK=PASS (487/487)
```

Callback global references are created only after successful subscription and freed only after CNA
accepts unsubscription. A failed unsubscription re-enables the context and leaves both the Java and
native registration retryable. Native handle identity/ownership is never exposed publicly and no
C++ symbol is bound.

Read-only CNA HEAD remains `1bb2145d99ed572dd4eb15009c34e2e5f410fcf0`; its known unrelated
networking-off missing GamerServices detail header and networking-on renderer 49/50 blockers are
unchanged. No CNA source was modified. Runtime evidence uses the qualified compatible ABI-0.7
library at `/tmp/cna-java-native-working-070/modules/c-api/libcna_c_api.so`.

## Behavior, ownership, stress, and sanitizer status

The complete native-enabled suite is 118 tests across 24 suites: zero failures, errors, or skips.
The normalized differential text corpus remains 117 observations (94 math/geometry, 23 input);
Audio uses direct XNA-derived deterministic assertions instead of forcing hardware/native identity
into the text corpus.

Stress evidence is green: 25 repeated Game create/run/destroy cycles, 200 Texture2D/SpriteBatch
cycles, 100 Audio cycles alternating parent-first and child-first release with dynamic callback
registration churn, an additional 25 repeated SoundEffect/instance pairs, 25 bound-buffer cycles,
and 150 routed draw calls. Failed native Audio creation recovers in the same Game. Wrong-thread
effect and registered-dynamic release refuse without losing the live handle, then succeed on the
owner thread. A live SoundEffect is closed before Game native destruction. No crash or observed
use-after-free occurred.

No sanitizer-built compatible CNA runtime was available, so no allocator-level leak-freedom claim
is made. The existing bound vertex/index lifetime guard is unchanged.

## Verification and template

The final source passes:

- `./gradlew --no-daemon check` with the qualified native library;
- `./gradlew --no-daemon apiCompatReport`;
- manifest/JNI consistency, layout/signature probe, and 487/487 library export checks;
- `./gradlew --no-daemon javadoc sourcesJar`;
- fresh temporary Maven publication;
- sibling template and freshly generated standalone consumer tests/build/install;
- 60-frame smoke and 600-frame stability runs under HEADLESS/NULL audio;
- `git diff --check` in both writable repositories.

The template remains deliberately small: real Game lifecycle, PNG `Texture2D.FromStream`, managed
Texture2D XNB canary/cache, SpriteBatch, input, and deterministic cleanup. No Audio canary was added:
NULL playback honestly returns false and the binding integration suite provides materially stronger
Audio construction/ownership coverage without making the starter noisy.

## Remaining dependency groups

The 41 remaining types are exactly:

```text
Graphics=0
Audio/XACT=0
Media/Video=24
Storage=3
Design=13
GamerServices=1
```

There are no missing Content, Touch, ordinary non-Design Framework/core, Graphics, or Audio types.
The next dependency-coherent milestone is Media/Video; do not split it merely to reduce counts.
After Media/Video, complete Storage, then Design converters and GamerServices. At every checkpoint
preserve `MISSING_MEMBER=0`, every mismatch/unexpected/leak category at zero, the empty allowlist,
transactional ownership, exact ABI probes, and the bound-buffer guard.
