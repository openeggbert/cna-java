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

The selected XNA 4.0 Windows runtime projection is structurally strict-complete. This means every
formally mapped type and member matches; it does not mean every historical XNA profile or every
platform-dependent runtime capability is complete.

The current strict measurement is:

```text
XNA reference:        257 types / 2,964 members
Mapped Java contract: 265 types / 3,206 members
Java target:          265 strict types / 3,206 members
Missing types:          0
Missing members:        0
Total diagnostics:      0
Mapping drift:          0
Allowlist:               0
Strict leaks:            0
```

The exact diagnostic breakdown and remaining family distribution are recorded
in [plan.md](plan.md) and [NEXT.md](NEXT.md). No completeness claim is inferred
from source counts.

Implemented now:

- normative CLR-to-Java mapping, foundational math/framework types, the `Game`
  lifecycle/component/service/window surface, and deterministic ownership;
- native keyboard, mouse, gamepad, raw touch, and gesture paths, with
  `FrameworkDispatcher.Update` pumping CNA framework services;
- the graphics device, state, texture/render-target, vertex/index declaration
  and buffer families, including dynamic uploads, ContentLost callbacks, draw
  routes, and the safety guard around CNA's bound-buffer lifetime defect;
- texture-based and string-based `SpriteBatch` contracts, including complete
  Begin state/effect overloads, plus native SpriteFont measurement/drawing for
  fonts obtained from the existing CNA loader;
- real Effect bytecode/reflection, stable technique/pass/parameter/annotation
  views, real pass application, and all five executable native stock effects:
  `BasicEffect`, `AlphaTestEffect`, `DualTextureEffect`,
  `EnvironmentMapEffect`, and `SkinnedEffect`;
- a managed Windows XNB version-5 reader foundation with reader tables, custom
  and primitive/value readers, shared resources, external references, cache
  identity, existing-instance handling, partial-failure cleanup, and real
  Texture2D, SpriteFont, vertex/index buffer, Effect/BasicEffect, and Model
  built-in readers;
- exact managed XNA LZX compressed-XNB framing with stateful multi-frame
  decoding, strict length/trailing-data checks, failure cleanup, cache identity,
  and the existing Texture2D reader verified through both compressed and
  uncompressed framing;
- the complete selected-profile Model object graph, including stable read-only
  collection identities and real indexed drawing through loaded CNA resources;
- the complete 19-type Audio/XACT contract, including native SoundEffect,
  SoundEffectInstance, dynamic streaming/callbacks, honest NULL-microphone
  behavior, and an ownership-correct XACT graph whose authored-bank playback is
  explicitly asset-pending;
- the complete 24-type Media/Video contract, including native platform-library
  collections, owned URI songs, process-global MediaPlayer/queue/event routes,
  Video XNB metadata, native VideoPlayer controls, and an explicitly borrowed
  transient frame facade whose stronger XNA identity remains backend-blocked;
- the complete three-type Storage contract, including authoritative completed-result
  `Begin`/`End` behavior, native container/stream CRUD, process-global device events,
  reverse Game ownership, and explicit Java containment for a documented CNA path gap;
- all 13 Design converters as a managed Java conversion subsystem, with an explicit compact
  TypeConverter language mapping, XNA-ordered property decomposition, constructor reconstruction,
  and culture-aware integer, byte, and binary32 text behavior;
- the selected `GamerServicesComponent` lifecycle through three canonical ABI 0.7 dispatcher
  routes, without projecting or fabricating the separate Gamer/Guide/Avatar/Network profiles;
- a Java 17 JNI adapter for 723 reviewed CNA ABI 0.7.0 functions, with header,
  manifest, layout/signature, and native-symbol verification;
- managed and native integration/ownership tests plus a desktop template canary
  verified for 60-frame smoke and 600-frame stability runs using both raw PNG
  and managed Texture2D XNB paths.

The current managed XNB implementation supports uncompressed and XNA LZX-
compressed Windows framing.
Texture2D upload is fidelity-preserving for `SurfaceFormat.Color`; all other
surface formats are rejected until CNA can create/upload the exact format, and
compressed data is never reinterpreted as RGBA. SpriteFont is consequently
verified with an uncompressed Color atlas. The Model path is verified for a
synthetic graph using VertexDeclaration, VertexBuffer, IndexBuffer, and
BasicEffect readers; reader families not in that graph remain explicit load
errors. Graphics, Audio/XACT, Media/Video, Storage, Design, and the selected GamerServices
component are all at zero missing types. Remaining runtime boundaries are kept separately in the
machine-readable capability inventory rather than hidden by the strict score.

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

`apiCompatCheck` is the strict completeness gate and now passes at zero diagnostics.
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
[the Audio/XACT evidence](docs/audio-xact-evidence.md),
[the Media/Video evidence](docs/media-video-evidence.md),
[the Storage evidence](docs/storage-evidence.md),
[the Design evidence](docs/design-evidence.md),
[the GamerServices component evidence](docs/gamerservices-evidence.md),
[the runtime capability inventory](docs/runtime-capabilities.json), and
[the measured engineering plan](plan.md) before expanding the surface.

## Platform evidence

Only Linux x86-64 with CNA HEADLESS/NULL-audio has runtime evidence in this
checkout. Windows and macOS desktop are planned. Android is planned but has no
backend/package. iOS and browser targets are unsupported. Java source
portability is not native runtime evidence.

CNA-Java is licensed under the [Microsoft Public License](LICENSE).
