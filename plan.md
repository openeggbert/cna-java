# CNA-Java measured engineering plan

**Status:** functional foundational slice; strict XNA projection measured incomplete

**Updated:** 2026-08-22

**Primary target:** XNA 4.0 Windows runtime projected to Java 17

## Current verified state

The audited starting snapshot had eight Java source files (three were
`package-info.java`), no tests, no wrapper, and no native implementation. Direct
`javac --release 17` failed with two unresolved types: `ContentManager` and
`GraphicsDevice`. The sibling template did not compile against the binding,
used a different Maven group, mixed XNA and Java casing, faked renderer
capabilities, had no wrapper, and advertised unsupported Android/iOS/Web paths.

The current repository has a reproducible Gradle 8.12/JDK 17 build, 62
production Java sources, managed and conditional native tests, a real JNI
backend, class-metadata verification, and a functional desktop/headless
template canary. Gradle is the single canonical build; the stale Maven build
was removed instead of maintaining two divergent lifecycles. This is not an
XNA-completeness claim.

## Compatibility definition

Three independent contracts are tracked:

1. **XNA CLR reference** — actual legally available XNA 4.0 assembly metadata.
2. **XNA Java projection** — the deterministic transformation specified by
   `docs/xna-java-mapping.md` and `tools/api-compat/mapping-rules.json`.
3. **CNA implementation coverage** — whether a mapped API has verified behavior
   through the CNA C ABI.

Coverage statuses are: mapped and implemented, mapped but temporarily
unsupported, mapped with Java adaptation, native CNA blocker, and outside the
selected profile. API shape never implies behavioral support.

## XNA-to-Java mapping definition

The normative mapping is versioned in `docs/xna-java-mapping.md`.

- XNA packages, types, methods, fields, enum members, and casing are preserved
  where Java syntax permits (`Run`, `Clear`, `Vector2.X`, `Color.White`).
- property `Foo` maps to `getFoo()` and, when writable, `setFoo(...)`; boolean
  `IsFoo` maps to `getIsFoo()` / `setIsFoo(...)`.
- generic `Load<T>(name)` maps to `Load(Class<T>, name)`.
- named XNA methods project operators; exceptional operator-only cases require
  explicit mapping rules.
- mutable CLR structs become value-oriented Java classes with copy constructors,
  equality/hash semantics, and snapshots across API/native boundaries. Java
  reference assignment cannot reproduce CLR struct assignment.
- `TimeSpan` maps to `Duration`, `IDisposable` to `AutoCloseable`, and enumerable
  contracts to carefully selected Java collection interfaces.
- delegates map to dedicated functional interfaces; events map to ordered
  add/remove listener methods.
- `ref`/`out` uses documented result/holder transformations; overload collisions
  are explicit verifier rules, not blanket exceptions.

## Definition of done

For the selected Windows runtime profile, completion requires zero unreviewed
mapped-contract differences, zero implementation leaks, a passing compile-probe
corpus, differential value/math behavior, meaningful lifecycle/content/
graphics/input/audio/media tests, verified ABI use and lifetime stress,
real-rendering template evidence, independently generated project builds,
reproducible publication, and runtime evidence for every claimed platform.

The README must not use “XNA complete” before all of these gates pass.

## Strict API verifier baseline

Profile assemblies:

- Microsoft.Xna.Framework.dll
- Microsoft.Xna.Framework.Game.dll
- Microsoft.Xna.Framework.Graphics.dll
- Microsoft.Xna.Framework.Storage.dll
- Microsoft.Xna.Framework.Video.dll
- Microsoft.Xna.Framework.Input.Touch.dll
- Microsoft.Xna.Framework.Xact.dll

Measured 2026-08-22 from compiled metadata:

```text
reference types:                   257
reference members:               2964
expected mapped Java types:        261
expected mapped Java members:     3086
mapped Java target types:           51
mapped Java target members:        770
unreviewed projection differences: 604

INTERFACE_MISMATCH                   2
MISSING_MEMBER                     392
MISSING_TYPE                       210

CNA_INTERNAL_LEAK                    0
allowlist entries                    0
```

The initial 730-diagnostic JSON evidence remains
`tools/api-compat/baselines/xna40-windows-runtime-initial.json`; the current
window/orientation checkpoint is recorded here and in `NEXT.md`. Report-only mode
is green for measurement. `apiCompatCheck` is deliberately red until the count
reaches zero; it is not weakened or attached to the ordinary partial-build gate.
The profile SHA-256-pins every reference assembly and the verifier rejects a
different byte identity before metadata extraction. CLR flags enums are now
detected from metadata and projected as composable immutable value classes.
CLR non-virtual methods are checked for effective Java non-overridability: an
instance method in a final class need not redundantly carry method-level
`final`; all nine genuine modifier mismatches in extensible classes were fixed.
Overload comparison now reserves exact matches before pairing mismatch
candidates, so a diagnostic can no longer steal a correct overload. The
unsigned CLR `System.Byte` maps to Java `int` with a 0..255 contract rather than
Java's signed `byte`. Parameter names retained in class metadata now match XNA
for every implemented member. `ContentLoadException`'s CLR-serialization-only
constructor is an explicit mapping exclusion, while direct `IDisposable`
implementations project a final public `close()` even when CLR `Dispose` is an
explicit interface method.
Missing CLR parameter names map to stable `argN` names; inaccessible CLR
interfaces are omitted from the public Java projection. Full-signature stream
rules map `Texture2D.FromStream` to `InputStream` and PNG/JPEG save operations
to `OutputStream`. Adding the first graphics-resource hierarchy removed seven
missing-type findings but exposed ten previously hidden missing-member findings,
so the honest total rose by three. A present but incomplete type is never
misreported as progress.

Next verifier work:

- add relevant annotation and nested generic-bound comparisons;
- expand compile probes by coherent API group;
- review every non-missing diagnostic against actual metadata before changing
  either code or mapping.

## Native bridge

JNI is selected because Java 17 and future Android compatibility are retained;
stable FFM is unavailable on Java 17. The adapter dynamically resolves only the
stable C ABI and binds 53 functions:

```text
cna_get_abi_version
cna_error_get_last_message_size
cna_error_copy_last_message
cna_game_create
cna_game_set_frame_hooks_ext
cna_game_run
cna_game_run_one_frame
cna_game_request_exit
cna_game_reset_elapsed_time
cna_game_suppress_draw
cna_game_tick
cna_game_destroy
cna_game_clear
cna_game_set_is_mouse_visible
cna_game_get_is_mouse_visible
cna_game_get_is_active
cna_game_set_is_fixed_time_step
cna_game_get_is_fixed_time_step
cna_game_set_target_elapsed_time_ticks
cna_game_get_target_elapsed_time_ticks
cna_game_set_inactive_sleep_time_ticks
cna_game_get_inactive_sleep_time_ticks
cna_game_window_get_allow_user_resizing
cna_game_window_set_allow_user_resizing
cna_game_window_get_client_bounds
cna_game_window_get_current_orientation
cna_game_window_get_native_handle_ext
cna_game_window_get_screen_device_name_size
cna_game_window_copy_screen_device_name
cna_game_set_window_title
cna_game_window_begin_screen_device_change
cna_game_window_end_screen_device_change
cna_keyboard_get_state
cna_keyboard_get_state_for_player
cna_mouse_get_state
cna_mouse_set_position
cna_mouse_get_window_handle
cna_mouse_set_window_handle
cna_game_get_graphics_device
cna_texture2d_create
cna_texture2d_create_from_encoded_memory
cna_texture2d_get_info
cna_texture2d_set_data_rgba8
cna_texture2d_get_data_rgba8
cna_texture2d_get_encoded_byte_count
cna_texture2d_copy_encoded
cna_texture2d_destroy
cna_sprite_batch_create
cna_sprite_batch_begin
cna_sprite_batch_submit_many
cna_sprite_batch_submit_scaled_many
cna_sprite_batch_end
cna_sprite_batch_destroy
```

It provides ABI-version rejection, UTF-8 conversion, callback rooting,
JVM-thread attachment, exception/result conversion, and portable configuration
through Java properties plus `CNA_JNI_LIBRARY`, `CNA_NATIVE_LIBRARY`,
`CNA_NATIVE_DIR`, and `CNA_ROOT`.

Linux x86-64 evidence: header ABI 0.7.0, all 53 symbols present, manifest/JNI
identity and layout/signature probes passing, runtime ABI 0.7.0, three-frame and
one-frame/tick callback lifecycles passing, and ten repeated create/run/destroy
cycles passing. Texture encode/decode/readback and two-frame SpriteBatch drawing
also pass against HEADLESS. This is not Windows/macOS ABI evidence.

## Ownership

Internal wrappers distinguish owned, borrowed, parent-owned, and adopted
handles. Owned/adopted values release exactly once; borrowed/parent-owned values
never destroy their target. Failed release leaves the handle live for explicit
retry. `Game.close()` unloads content and
closes registered texture/SpriteBatch children in reverse creation order before
destroying the native parent. No raw handles appear in strict public APIs and
deprecated finalization is not used.

Remaining lifetime work includes callback-failure teardown, JVM shutdown hooks,
resource graphs below Game, cross-thread misuse, long stress runs, and sanitizer
evidence.

## Core and value API

Implemented behavior/tests currently cover `Game`, `GameTime`, `IGameComponent`,
`IUpdateable`, `IDrawable`, `GameComponent`, `DrawableGameComponent`,
`GameComponentCollection`, `GameServiceContainer`, the managed
`LaunchParameters` map container (native population pending),
`GameWindow`, composable `DisplayOrientation`, opaque `WindowHandle`,
`GraphicsDeviceManager`, `ContentManager` validation/cache boundary,
`MathHelper`, `Vector2/3/4`, `Matrix`, `Quaternion`, `Color`, `Point`,
`Rectangle`, `Plane`, `Ray`, `BoundingBox`, and `BoundingSphere`.

The new lifecycle/window types and `Game` match their mapped contract without
local diagnostics. CNA backs window title, resize permission, client bounds,
orientation, opaque handle, screen-device name, and screen-device-change calls.
The public native address is deliberately represented only as opaque
`WindowHandle`; no raw address leaks. Window listener ordering is implemented
on the managed facade, but native event delivery and supported-orientation
mutation remain binding work. Next work is missing math/geometry members and
differential fixtures, driven by metadata rather than ad-hoc generation.

## Graphics

`GraphicsResource`, `Texture`, `Texture2D`, `SpriteBatch`, `SurfaceFormat`,
`SpriteSortMode`, and composable `SpriteEffects` now preserve their mapped
hierarchy without local verifier mismatches. CNA-backed behavior includes
resource metadata/events/disposal, texture creation, mutable-Color snapshot
upload and readback, raw encoded-image `FromStream`, PNG/JPEG output, all seven
texture-based Draw overloads, and Begin/End state validation. Only `Color[]`
generic transfers and full mip-level-zero regions work in this slice; other
mapped generic transfers fail explicitly. SpriteBatch state overloads and
DrawString await the coherent state/effect/font types.

`GraphicsDeviceManager` still lacks most of its contract. Render targets,
buffers/states, effects, vertices, resize/device reset, and broad draw APIs
remain Java binding work. Empty facade generation is not a substitute for
behavior.

## Input

`PlayerIndex`, all 160 measured `Keys` identities, `KeyState`, `KeyboardState`,
both `Keyboard.GetState` overloads, `ButtonState`, `MouseState`, and the full
mapped `Mouse` surface match their local contracts. Keyboard and mouse snapshots
are copied from CNA's versioned PODs. Managed tests cover value semantics and
the normalized XNA mouse string/hash corpus; native HEADLESS tests cover
ordinary/per-player keyboard capture, mouse capture/position, and opaque window
round trips. Gamepad and touch remain coherent future groups; synthetic input
injection is not claimed.

## Content

`ContentManager` currently supplies RootDirectory, validation, deterministic
unload, and the normative class-token `Load` signature. XNB parsing and custom
readers are not implemented. The future architecture must include reader tables,
versioning, shared resources, caching, streams, built-in readers, and user
readers. Raw PNG must use a projected `Texture2D.FromStream`; it must never be
reported as XNB.

## Models, audio/XACT, media, storage, and GamerServices

Models, XACT, media/video, and storage in the selected profile are not
implemented in Java. `GamerServicesComponent` appears in the selected Game
assembly, but the separate GamerServices and networking assemblies are outside
this first profile. Extinct services may eventually expose deterministic
unsupported behavior after their compile-time contract is projected; profile
boundaries must remain explicit.

## XNA profile inventory

The first strict target is the seven-assembly Windows runtime profile above:
Framework, Game, Graphics, Storage, Video, Input.Touch, and Xact. The separate
GamerServices and Net assemblies, Xbox-only, Windows Phone, and the build-time
Content Pipeline are separately inventoried future profiles and cannot inherit
the Windows-runtime completion status.

## CNA extensions

No public CNA-specific extension API is shipped yet. Future renderer identity,
capability, diagnostics, and modern-format access belongs under
`org.openeggbert.cna.extensions.*` and must query CNA rather than return guessed
values. The strict XNA package tree stays clean.

## Template

`../cna-java-template` is now a desktop-only functional canary. It uses the
normative names, exact `org.openeggbert:cna-java:0.1.0-SNAPSHOT` coordinate,
temporary Maven repositories, a pinned wrapper, and deterministic 60/600-frame
modes. Linux HEADLESS runtime has executed both counts with clean shutdown.

It intentionally demonstrates lifecycle, `GameTime`, graphics manager,
pre-run `GameWindow` title configuration, CNA-backed keyboard/mouse snapshots
with Escape/left-click exit, mouse visibility, `Clear`, real raw-PNG decoding,
and moving SpriteBatch drawing. The Base64 file is only text-safe transport for
valid PNG bytes and is not called XNB. The fake renderer name/capability banner,
unimplemented cube/XNB path, and non-running Android/GWT/TeaVM launchers were
removed. A configurable generator creates a standalone project and its fresh
build is verified.

## Packaging

The canonical coordinate is `org.openeggbert:cna-java:0.1.0-SNAPSHOT` during
development. Gradle publishes binary, sources, Javadoc, metadata, and POM. Native
libraries are not yet packaged in the artifact; consumers currently configure
the JNI adapter and CNA library explicitly.

Before release: settle repository/version policy, sign publications, include
license/notice data, define classifier or runtime artifacts per OS/architecture,
verify reproducibility, and add consumer installation tests. Do not split core,
native, and extensions until independent release/lifecycle needs justify it.

## Platform matrix

| Platform | Build | Runtime | Status |
| --- | --- | --- | --- |
| Linux x86-64 HEADLESS | verified | 60/600 frames | implemented foundational slice |
| Linux windowed renderer | not verified | not verified | planned |
| Windows x64 | not verified | not verified | planned |
| macOS x64/arm64 | not verified | not verified | planned |
| Android | no module/backend package | not verified | planned |
| iOS | no module | not verified | unsupported |
| Browser/WASM | desktop JNI inapplicable | not verified | unsupported |

## CI and local quality gates

Green now:

1. Java/JNI build with strict warnings;
2. JUnit managed tests (native tests conditional on a supplied library);
3. strict internal/native-leak guard;
4. fourteen verifier/mapping regression tests and a graphics-aware compile probe;
5. C header width/layout/signature checks;
6. optional native symbol/version/integration tests;
7. template build/tests;
8. fresh generated-project build;
9. optional 60-frame smoke and 600-frame stability runs.

Intentionally red: strict XNA completeness (`apiCompatCheck`, 604 differences).
Future platform CI must record evidence separately per OS/architecture.

## Upstream CNA blockers

At CNA HEAD `1bb2145d9` (2026-08-20), a HEADLESS C API build currently fails:

- with `CNA_ENABLE_NET=OFF`, C API detail headers still unconditionally require a
  GamerServices header;
- with networking enabled, `CnaCApiCoreExt.cpp` asserts that 50 native renderer
  identities exist while the C table contains 49 after NanoVG was added.

No upstream file was modified. Runtime evidence used the immediately preceding
working ABI-0.7.0 commit `a09196a64` in an isolated `/tmp` checkout. Repairing
current CNA HEAD is a **native CNA blocker**, not authorization for this binding
task to patch the upstream repository.

## Ordered next priorities

1. Resolve/consume the upstream C API HEAD build blockers and rerun Linux native
   evidence against HEAD.
2. Complete native `GameWindow` event/orientation behavior and math members
   against the strict diagnostics; add XNA differential fixtures.
3. Bind gamepad/touch and native device/window resize lifecycle.
4. Add graphics states/effects/fonts for the remaining SpriteBatch overloads,
   then renderer-backed resize/device lifecycle behavior.
5. Expand content/XNB, remaining graphics resources/models, audio/XACT, and
   media/storage in dependency-coherent groups, recording baseline deltas after
   every group.
6. Package/test native runtime artifacts and add platform-specific CI before
   promoting any platform claim.
