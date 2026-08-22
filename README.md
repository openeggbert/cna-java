# CNA-Java

CNA-Java is an early, measured Java 17 projection of Microsoft XNA Framework
4.0 backed by CNA C++ through CNA's stable C ABI and a JNI adapter.

```text
Java game
    ↓
Microsoft.Xna.Framework.*       strict XNA Java projection
    ↓
org.openeggbert.cna.internal.*  private implementation/JNI
    ↓
CNA stable C ABI 0.7.0
    ↓
CNA C++
```

## Honest status

The project is functional for a small Linux/headless lifecycle-and-clear slice;
it is not XNA-complete and does not yet provide a playable SpriteBatch/content/
input path.

The first strict Windows-runtime measurement is intentionally red:

```text
XNA reference:        257 types / 2,964 members
Mapped Java contract: 261 types / 3,086 members
Java target:           44 strict types / 693 members
Differences:          677 unreviewed diagnostics
Allowlist:              0
Strict leaks:           0
```

The exact current diagnostic breakdown is recorded in [plan.md](plan.md) and
[NEXT.md](NEXT.md). No completeness claim is inferred from source counts.

Implemented now:

- normative CLR-to-Java mapping, including naming and language adaptations;
- foundational value types plus `Game`, component hierarchy/collection,
  services, a launch-parameter container (native population pending), events,
  `GameTime`, `GameWindow`, composable display orientation, and an opaque window
  handle (native window event delivery remains pending);
- exact `PlayerIndex`, `Keys`, `KeyState`, `KeyboardState`, and both native
  `Keyboard.GetState` overloads;
- exact `ButtonState`, immutable `MouseState`, and native-backed `Mouse`
  state/position/opaque-window operations;
- Java 17 JNI adapter for 38 CNA game/error/window/keyboard/mouse functions;
- explicit native ownership modes and idempotent cleanup;
- class-metadata XNA contract verifier and strict implementation-leak guard;
- header/signature/layout/native-symbol ABI verifier;
- managed numerical, validation, lifecycle, content, and ownership tests;
- desktop template canary with 60-frame smoke and 600-frame stability modes.

Not implemented yet includes SpriteBatch, textures, gamepad/touch, XNB
loading, models, audio/XACT, media/storage, and most of the selected XNA profile.

## Build and verify

Use the pinned Gradle 8.12 Wrapper with JDK 17 or newer. CNA headers are found
through `CNA_ROOT`, then the known sibling checkout layouts.

```bash
./gradlew clean check
```

The normal build compiles with `--release 17`, retained parameter names,
`-Xlint:all`, and `-Werror`. Native integration tests are enabled when
`CNA_NATIVE_LIBRARY` points at a compatible CNA C ABI library. The JNI adapter
can be overridden with `CNA_JNI_LIBRARY`.

```bash
CNA_NATIVE_LIBRARY=/path/to/libcna_c_api.so ./gradlew clean check
```

Run the report-only API measurement with the legally available seven XNA 4.0
Windows runtime assemblies:

```bash
XNA_REFERENCE_DIR=/path/to/xna4/windows ./gradlew apiCompatReport
```

`apiCompatCheck` is the strict completeness gate and currently fails by design.
The leak guard and native ABI shape checks remain part of the green `check` gate.

To verify this binding, the sibling template, and a freshly generated external
project without touching the global Maven repository:

```bash
scripts/verify-template.sh
```

Set `CNA_NATIVE_LIBRARY` to add the 60-frame runtime smoke. Also set
`CNA_RUN_STABILITY_TEST=1` to add the 600-frame run.

## Public API policy

`Microsoft.Xna.Framework.*` is reserved for the strict XNA Java projection.
Its public/protected signatures may not expose native handles or
`org.openeggbert.cna.internal.*`. Future opt-in CNA-specific functionality
belongs under `org.openeggbert.cna.extensions.*` (or a real native CNA concept),
not in the strict packages.

Read [the normative mapping](docs/xna-java-mapping.md), [the architecture](docs/architecture.md),
and [the measured engineering plan](plan.md) before expanding the surface.

## Platform evidence

Only Linux x86-64 with CNA HEADLESS/NULL-audio has runtime evidence in this
checkout. Windows and macOS desktop are planned. Android is planned but has no
backend/package. iOS and browser targets are unsupported. Java source
portability is not native runtime evidence.

CNA-Java is licensed under the [Microsoft Public License](LICENSE).
