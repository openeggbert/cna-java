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

### `cna-java-template`

- `64dc930 feat: make desktop template a real CNA canary`
  - real desktop lifecycle/clear canary with 60/600-frame modes;
  - pinned wrapper, exact coordinate, tests, independent generator;
  - removed fake capability/cube/content behavior and non-running Android,
    GWT, and TeaVM launchers;
  - pushed to `origin/develop` after build and native evidence.

## API measurements

The immutable initial measurement at the first foundation checkpoint was 257
reference types / 2,964 members versus 21 target types / 351 members, with 730
diagnostics, zero leaks, and an empty allowlist. Its full JSON remains at
`tools/api-compat/baselines/xna40-windows-runtime-initial.json`.

After the coherent window/orientation group, the current measurement is:

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
TARGET_TYPES=36
TARGET_MEMBERS=495
TOTAL_DIAGNOSTICS=685
ALLOWLIST_ENTRIES=0
INTERFACE_MISMATCH=2
MISSING_MEMBER=372
MISSING_TYPE=225
PARAMETER_MISMATCH=21
PARAMETER_NAME_MISMATCH=58
RETURN_TYPE_MISMATCH=4
UNEXPECTED_MEMBER=3
```

This is a 45-diagnostic reduction from the immutable initial baseline, with 15
additional strict target types and 144 additional target members.
`apiCompatCheck` still exits 1 as designed. Leak-only inspection reports
`CNA_INTERNAL_LEAK=0` (total leak diagnostics 0).

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

## Managed/native binding gate

Command:

```bash
CNA_NATIVE_LIBRARY=/tmp/cna-java-native-working-070/modules/c-api/libcna_c_api.so \
XNA_REFERENCE_DIR=/rv/data/development/github.com/openeggbert/xna4-decomp/reference/xna4/original/windows \
./gradlew --no-daemon clean check apiCompatReport --warning-mode all
```

Result: `BUILD SUCCESSFUL`, 10 tasks executed, no compiler/deprecation warnings.

- JUnit: 34 tests, 0 failures, 0 errors, 0 skipped.
- Verifier regression suite: 8 tests, all passing.
- Suites: 12 value/math, 3 lifecycle/content, 9 component/service/window,
  4 ownership, 5 native integration.
- Native stress: one ordered three-frame lifecycle plus ten repeated
  create/run/destroy lifecycles, and one-frame/tick/suppressed-draw timing.
- Compile probe: passed.
- Strict leak guard: 36 target types / 495 members, 0 findings.
- ABI: header 0.7.0, 32 bound functions, manifest/JNI identity and C
  width/layout/function-signature probes passed, native library ABI 0.7.0,
  symbols 22/22.
- JNI compiled with `-std=c11 -Wall -Wextra -Werror -fPIC`.

Without `CNA_NATIVE_LIBRARY`, the same `check` passes and conditionally skips the
four native integration tests plus native symbol/runtime-version inspection.

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
   (2 tests, no warnings).
3. Generated standalone project with custom project name, package, application
   ID, game class, group, and artifact ID: clean/test/installDist passed outside
   both repositories.
4. Native template smoke: exactly 60 frames, clean shutdown.
5. Native template stability: exactly 600 frames, clean shutdown.

The demonstrated runtime feature set is lifecycle callbacks, `GameTime`,
GraphicsDeviceManager attachment, pre-run mapped `GameWindow` title,
mouse visibility, `GraphicsDevice.Clear`, frame-limited exit, and deterministic
cleanup. SpriteBatch, texture/raw PNG, keyboard/mouse state, resize events, and
3D are not claimed.

## Immediate next work

1. Consume an upstream CNA fix for both HEAD C API build failures and repeat all
   native evidence against current HEAD.
2. Review the 2 interface, 21 parameter, 58 parameter-name, 4 return-type, and 3
   unexpected-member diagnostics before adding more surface.
3. Connect native window events and supported-orientation behavior, then add
   normalized XNA differential fixtures for the math/geometry contract.
4. Bind keyboard/mouse plus native device resize.
5. Implement Texture2D FromStream and SpriteBatch, then upgrade the clear-only
   template to the requested moving raw-PNG playable slice.
6. Add platform-specific native packaging and CI; do not promote Windows,
   macOS, Android, iOS, or Web status without runtime evidence.
