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

### `cna-java-template`

- `64dc930 feat: make desktop template a real CNA canary`
  - real desktop lifecycle/clear canary with 60/600-frame modes;
  - pinned wrapper, exact coordinate, tests, independent generator;
  - removed fake capability/cube/content behavior and non-running Android,
    GWT, and TeaVM launchers;
  - pushed to `origin/develop` after build and native evidence.

The final documentation/lifetime/workflow changes after `aadce19` are intended
for the next binding checkpoint commit in this same session.

## Final measured API baseline

Command:

```bash
XNA_REFERENCE_DIR=/rv/data/development/github.com/openeggbert/xna4-decomp/reference/xna4/original/windows \
./gradlew --no-daemon apiCompatReport
```

Result (`exit 0`, report-only):

```text
REFERENCE_TYPES=257
REFERENCE_MEMBERS=2964
TARGET_TYPES=21
TARGET_MEMBERS=351
TOTAL_DIAGNOSTICS=730
ALLOWLIST_ENTRIES=0
INTERFACE_MISMATCH=2
MISSING_MEMBER=405
MISSING_TYPE=236
PARAMETER_MISMATCH=21
PARAMETER_NAME_MISMATCH=59
RETURN_TYPE_MISMATCH=4
UNEXPECTED_MEMBER=3
```

`apiCompatCheck` reported the same numbers and exited 1 as designed. Leak-only
inspection reported `CNA_INTERNAL_LEAK=0` (total leak diagnostics 0).

## Managed/native binding gate

Command:

```bash
CNA_NATIVE_LIBRARY=/tmp/cna-java-native-working-070/modules/c-api/libcna_c_api.so \
./gradlew --no-daemon clean check --warning-mode all
```

Result: `BUILD SUCCESSFUL`, 8 tasks executed, no compiler/deprecation warnings.

- JUnit: 22 tests, 0 failures, 0 errors, 0 skipped.
- Suites: 12 value/math, 3 lifecycle/content, 4 ownership, 3 native integration.
- Native stress: one ordered three-frame lifecycle plus ten repeated
  create/run/destroy lifecycles.
- Compile probe: passed.
- Strict leak guard: 21 target types / 351 members, 0 findings.
- ABI: header 0.7.0, 11 bound functions, C width/layout/function-signature probe
  passed, native library ABI 0.7.0, symbols 11/11.
- JNI compiled with `-std=c11 -Wall -Wextra -Werror -fPIC`.

Without `CNA_NATIVE_LIBRARY`, the same `check` passes and conditionally skips the
three native integration tests plus native symbol/runtime-version inspection.

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
GraphicsDeviceManager attachment, mouse visibility, `GraphicsDevice.Clear`,
frame-limited exit, and deterministic cleanup. SpriteBatch, texture/raw PNG,
keyboard/mouse state, resize, and 3D are not claimed.

## Immediate next work

1. Consume an upstream CNA fix for both HEAD C API build failures and repeat all
   native evidence against current HEAD.
2. Review the 2 interface, 21 parameter, 59 parameter-name, 4 return-type, and 3
   unexpected-member diagnostics before adding more surface.
3. Complete the foundational lifecycle hierarchy and math/geometry contract;
   add normalized XNA differential fixtures.
4. Bind keyboard/mouse plus window/device resize.
5. Implement Texture2D FromStream and SpriteBatch, then upgrade the clear-only
   template to the requested moving raw-PNG playable slice.
6. Add platform-specific native packaging and CI; do not promote Windows,
   macOS, Android, iOS, or Web status without runtime evidence.
