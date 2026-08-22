# CNA-Java session evidence and next handoff

**Session:** 2026-08-22

**Binding branch:** `develop`

**Template branch:** `develop`

## Repository baseline observed at session start

- `cna-java` was clean and contained exactly eight `.java` files, including
  three `package-info.java` files.
- `mvn -q test` could not start because Maven was not installed (`exit 127`).
- Direct `javac --release 17` over all eight sources failed with two errors:
  `Game` referenced missing `Microsoft.Xna.Framework.Content.ContentManager`
  and `Microsoft.Xna.Framework.Graphics.GraphicsDevice`.
- There was no JUnit suite, Gradle/Maven wrapper, native implementation, API
  verifier, ABI verifier, or compile-probe corpus.
- The binding POM used `org.openeggbert:cna-java:0.0.0-SNAPSHOT`; the template
  requested nonexistent `com.openeggbert.cna:cna-java:1.0.0-preview`.
- The template had no wrapper and could not compile against the binding. It
  mixed `Run` with `begin`, `createScale`, lowercase vector fields and
  screaming-snake constants; it referenced absent classes throughout.
- Template renderer identity and 3D/depth capabilities were hardcoded, not
  queried. Android did not run CNA, GWT/TeaVM could not reach desktop JNI, iOS
  had no module, while README advertised all as supported.
- `plan.md` incorrectly deferred selection of JNI/FFM and waited for a
  “canonical” ABI although CNA already exposed a broad stable C API.

## Reference and ABI truth established

- XNA source of truth: the seven legally available XNA 4.0 Windows runtime
  assemblies listed in `tools/api-compat/profiles/xna40-windows-runtime.json`.
  The profile now SHA-256-pins all seven inputs and rejects mismatched bits.
- Current CNA headers: C ABI 0.7.0, `uint64_t` opaque handles, `uint8_t` booleans,
  `uint32_t` results/enums, versioned structs, UTF-8 buffers, callbacks, and
  explicit create/destroy ownership.
- Java 17 is retained. JNI was selected and implemented because stable FFM is
  not a Java 17 API and JNI preserves a future Android route.
- No direct C++ ABI symbol is used.
- Gradle 8.12 is now the single canonical build and publication path; the stale
  Maven build was removed, so a second Maven wrapper/configuration cannot drift.

## Commits already pushed during this session

### `cna-java`

- `aadce19 feat: establish XNA Java compatibility foundation`
  - Gradle 8.12 wrapper/build, canonical coordinate, mapping document;
  - verifier/leak/ABI tooling and first baseline;
  - JNI backend and ownership wrappers;
  - foundational lifecycle/value/geometry implementations and tests.
- `10e0daa chore: harden verification and record measured state`
  - strengthened native lifetime/error handling and template verification;
  - recorded complete build, ABI, runtime, template, and CNA-HEAD blocker evidence;
  - removed the stale Maven build so Gradle is the single source of build truth.
- `0e7293c feat: implement foundational game components`
  - added the mapped component hierarchy, collection/events, services, launch
    parameters, ordered update/draw, and resilient teardown;
  - reduced the measured contract from 730 to 691 diagnostics.
- `29ce9c9 feat: bind XNA game window through CNA`
  - added strict `GameWindow`, composable `[Flags]` orientation, and opaque
    `WindowHandle` projections plus ten CNA window ABI bindings;
  - made verifier modifier checks rigorous and reduced the measured contract to
    685 diagnostics with zero leaks and no allowlist.
- `e28dd6d fix: defer native window title until startup`
  - kept pre-run title configuration managed and passed it into native creation;
  - reran binding/template/generated-project plus 60/600-frame verification.
- `58209ac feat: bind XNA keyboard snapshots through CNA`
  - added exact keyboard types, 160 key identities, two CNA snapshot routes,
    managed/native behavior tests, and template Escape input;
  - reduced the measured contract to 680 diagnostics with 34/34 ABI symbols.
- `50eb603 feat: bind XNA mouse snapshots through CNA`
  - added exact mouse types, four CNA state/position/window routes, normalized
    behavior evidence, and opaque strict window tokens;
  - reduced the then-current measured contract to 677 diagnostics with 38/38
    ABI symbols.
- `b235862 fix: eliminate mapped member drift`
  - corrected overload pairing and unsigned-byte mapping, made ContentManager
    and disposal shapes exact, hid a non-XNA constructor, and eliminated every
    implemented parameter/return/unexpected-member mismatch;
  - reduced the measured contract to 601 diagnostics with zero leaks.

### `cna-java-template`

- `64dc930 feat: make desktop template a real CNA canary`
  - real desktop lifecycle/clear canary with 60/600-frame modes;
  - pinned wrapper, exact coordinate, tests, independent generator;
  - removed fake capability/cube/content behavior and non-running Android,
    GWT, and TeaVM launchers;
  - pushed to `origin/develop` after build and native evidence.
- `df38a41 feat: exercise mapped game window title`
  - configures the mapped title before native startup and tests that path.
- `7a5c97d feat: exercise CNA keyboard input`
  - captures native keyboard state per update and exits on Escape.
- `e12039f feat: exercise CNA mouse input`
  - captures native mouse state per update and exits on left click.

## API measurements

The immutable initial measurement at the first foundation checkpoint was 257
reference types / 2,964 members versus 21 target types / 351 members, with 730
diagnostics, zero leaks, and an empty allowlist. Its full JSON remains at
`tools/api-compat/baselines/xna40-windows-runtime-initial.json`.

After the first coherent texture/SpriteBatch group, the current measurement is:

Command:

```bash
XNA_REFERENCE_DIR=/rv/data/development/github.com/openeggbert/xna4-decomp/reference/xna4/original/windows \
./gradlew --no-daemon apiCompatReport
```

Result (`exit 0`, report-only):

```text
REFERENCE_TYPES=257
REFERENCE_MEMBERS=2964
EXPECTED_JAVA_TYPES=261
EXPECTED_JAVA_MEMBERS=3086
TARGET_TYPES=51
TARGET_MEMBERS=770
TOTAL_DIAGNOSTICS=604
ALLOWLIST_ENTRIES=0
INTERFACE_MISMATCH=2
MISSING_MEMBER=392
MISSING_TYPE=210
```

This is a 126-diagnostic reduction from the immutable initial baseline, with 30
additional strict target types and 419 additional target members.
`apiCompatCheck` still exits 1 as designed. Leak-only inspection reports
`CNA_INTERNAL_LEAK=0` (total leak diagnostics 0).

The graphics group intentionally raised the total from 601 to 604: seven
formerly missing types are now present, but their compiled metadata exposes ten
additional missing members that one-per-type diagnostics previously hid. All
implemented graphics members match their mapped signatures; no parameter,
return, modifier, unexpected-member, or parameter-name finding was introduced.

The latest contract audit fixed overload matching so exact overloads cannot be
reused as mismatch candidates, maps unsigned CLR `System.Byte` to Java `int`,
and eliminated every implemented parameter-name mismatch. It also made
`ContentManager` constructors/service-provider/disposal shape exact, explicitly
excluded the CLR-serialization-only `ContentLoadException` constructor, and hid
the implementation-only `GraphicsDevice` constructor behind a narrow internal
facade factory. The remaining diagnostics contain only genuinely absent types,
members, and two interfaces; there are no parameter, return, unexpected-member,
or parameter-name findings.

The newest group added exact `GameWindow`, `[Flags]`-aware
`DisplayOrientation`, and opaque `WindowHandle` projections. `Game` now has all
currently mapped local members, including `getWindow()` and deterministic
`ShowMissingRequirementMessage`. The JNI path queries or updates resize
permission, client bounds, orientation, opaque handle identity, screen-device
name, title, and begin/end screen-device changes. Managed window listeners are
ordered, but CNA event delivery and supported-orientation mutation remain
explicit behavior work. The verifier now extracts CLR virtual/final state and
compares effective Java overridability; it exposed nine real mismatches, all of
which were corrected without an allowlist.

The input group adds exact `PlayerIndex`, `KeyState`, all 160 metadata-derived
`Keys` constants and numbers, immutable `KeyboardState`, and both static
`Keyboard.GetState` overloads. Its five types have no local verifier findings;
the diagnostic delta is exactly five removed `MISSING_TYPE` findings. Two new
C ABI routes copy versioned keyboard snapshots, including the per-player route.

The adjacent mouse group adds exact `ButtonState`, `MouseState`, and `Mouse`
contracts. Four CNA routes cover snapshot capture, cursor positioning, and an
opaque window-token round trip. The normalized XNA behavior fixture pins the
sample hash (`-120`) and string format. No raw address enters a strict public
signature; internally registered CNA-issued tokens are the only accepted
nonzero setter values. Its diagnostic delta is exactly three removed
`MISSING_TYPE` findings.

The graphics group adds the exact `GraphicsResource` / `Texture` / `Texture2D`
hierarchy, `SpriteBatch`, `SurfaceFormat`, `SpriteSortMode`, and composable
`SpriteEffects`. Fifteen new C ABI routes provide callback-scoped device access,
owned texture/batch creation and destruction, RGBA snapshot transfer,
PNG/JPEG decode/encode, and both rectangle/scaled sprite commands. The native
test uploads mutable colors, mutates the Java source afterward, reads back the
original snapshot, round-trips a PNG through `FromStream`, draws two frames, and
verifies reverse child-before-game cleanup.

## Managed/native binding gate

Command:

```bash
CNA_NATIVE_LIBRARY=/tmp/cna-java-native-working-070/modules/c-api/libcna_c_api.so \
XNA_REFERENCE_DIR=/rv/data/development/github.com/openeggbert/xna4-decomp/reference/xna4/original/windows \
./gradlew --no-daemon clean check apiCompatReport --warning-mode all
```

Result: `BUILD SUCCESSFUL in 19s`, 12 actionable tasks executed, no
compiler/deprecation warnings.

- JUnit: 49 tests, 0 failures, 0 errors, 0 skipped.
- Verifier regression suite: 14 tests, all passing.
- Suites: 12 value/math, 4 lifecycle/content, 10 component/service/window,
  3 graphics-resource, 4 keyboard-state, 4 mouse-state, 4 ownership,
  8 native integration.
- Native stress: one ordered three-frame lifecycle plus ten repeated
  create/run/destroy lifecycles, and one-frame/tick/suppressed-draw timing.
- Compile probe: passed.
- Strict leak guard: 51 target types / 770 members, 0 findings.
- ABI: header 0.7.0, 53 bound functions, manifest/JNI identity and C
  width/layout/function-signature probes passed, native library ABI 0.7.0,
  symbols 53/53.
- JNI compiled with `-std=c11 -Wall -Wextra -Werror -fPIC`.

Without `CNA_NATIVE_LIBRARY`, the same `check` passes and conditionally skips the
eight native integration tests plus native symbol/runtime-version inspection.

## CNA native build evidence and blocker

The upstream CNA checkout was read-only and was not modified. Its starting
status already contained one unrelated untracked file:
`cmake_test_discovery_e3b0c44298.json`.

Current HEAD `1bb2145d9` was configured in `/tmp` with HEADLESS renderer,
HEADLESS platform, NULL audio, C API on, tests/examples off.

Observed failures:

1. `CNA_ENABLE_NET=OFF`: `CnaCApiDetail.hpp` requires
   `GameUpdateRequiredException.hpp`, which is unavailable when GamerServices is
   omitted.
2. Networking on: `CnaCApiCoreExt.cpp` fails its strict assertion because
   `RendererIdentities.size()` is 49 while `CanonicalRendererCount()` is 50
   after NanoVG entered the native enum.

For binding verification only, a local shared clone at commit `a09196a64`
(working ABI 0.7.0 immediately before the problematic merge) was created under
`/tmp`, configured HEADLESS/NULL audio, and built successfully. The resulting
Linux x86-64 `libcna_c_api.so` supplied all integration evidence. This does not
erase the current-HEAD upstream blocker.

## Template and generated-project gate

Final command:

```bash
CNA_NATIVE_LIBRARY=/tmp/cna-java-native-working-070/modules/c-api/libcna_c_api.so \
CNA_RUN_STABILITY_TEST=1 \
scripts/verify-template.sh
```

Result: passed end-to-end without using the global Maven repository.

1. Binding clean/check/Javadoc/sources and publication to a unique temporary
   Maven repository: passed.
2. Sibling template clean/test/installDist against that exact artifact: passed
   (4 tests, no warnings).
3. Generated standalone project with custom project name, package, application
   ID, game class, group, and artifact ID: clean/test/installDist passed outside
   both repositories.
4. Native template smoke: exactly 60 frames, clean shutdown.
5. Native template stability: exactly 600 frames, clean shutdown.

The demonstrated runtime feature set is lifecycle callbacks, `GameTime`,
GraphicsDeviceManager attachment, pre-run mapped `GameWindow` title,
CNA-backed `KeyboardState`/Escape and `MouseState`/left-click input, mouse
visibility, `GraphicsDevice.Clear`, a real Base64-transported raw PNG decoded by
`Texture2D.FromStream`, moving SpriteBatch drawing, frame-limited exit, and
deterministic cleanup. XNB, gamepad/touch state, resize events, and 3D are not
claimed.

## Immediate next work

1. Consume an upstream CNA fix for both HEAD C API build failures and repeat all
   native evidence against current HEAD.
2. Implement the 392 measured missing members in coherent dependency groups;
   the only non-missing diagnostics are two known missing public interfaces.
3. Connect native window events and supported-orientation behavior, then add
   normalized XNA differential fixtures for the math/geometry contract.
4. Bind gamepad/touch plus native device resize.
5. Continue the graphics state/effect/font types needed by the remaining
   SpriteBatch Begin and DrawString overloads.
6. Add platform-specific native packaging and CI; do not promote Windows,
   macOS, Android, iOS, or Web status without runtime evidence.
