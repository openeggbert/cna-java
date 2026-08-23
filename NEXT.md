# CNA-Java final evidence and next handoff

**Updated:** 2026-08-23

This file is the tactical continuation point for the current uncommitted CNA-Java worktree. The
measured repositories and verifier output are authoritative; do not reconstruct intent from commit
history.

## Repository boundaries and worktree state

Writable:

- this repository, `cna-java`;
- `../cna-java-template`.

Read-only references:

- `../../cna`;
- `../cna-cs`, `../cna-cs-template`;
- `../cna-ts`, `../cna-ts-template`.

No template source change was needed in this phase; `../cna-java-template` is clean. The
CNA-Java worktree intentionally contains the implementation described below. Do not discard or
overwrite it. The untracked `out` path was already present during this continuation and was
not deleted. The read-only CNA worktree has a pre-existing untracked
`cmake_test_discovery_e3b0c44298.json`; leave it untouched.

## Exact strict verifier state

Starting state:

```text
REFERENCE_TYPES=257
REFERENCE_MEMBERS=2964
EXPECTED_JAVA_TYPES=261
EXPECTED_JAVA_MEMBERS=3086
TARGET_TYPES=51
TARGET_MEMBERS=912
TOTAL_DIAGNOSTICS=462
MISSING_TYPE=210
MISSING_MEMBER=250
INTERFACE_MISMATCH=2
CNA_INTERNAL_LEAK=0
ALLOWLIST_ENTRIES=0
```

Current state:

```text
REFERENCE_TYPES=257
REFERENCE_MEMBERS=2964
EXPECTED_JAVA_TYPES=265
EXPECTED_JAVA_MEMBERS=3178
TARGET_TYPES=149
TARGET_MEMBERS=2171
TOTAL_DIAGNOSTICS=119
MISSING_TYPE=116
MISSING_MEMBER=3
INTERFACE_MISMATCH=0

ACCESSIBILITY_MISMATCH=0
BASE_TYPE_MISMATCH=0
CNA_INTERNAL_LEAK=0
ENUM_VALUE_MISMATCH=0
FIELD_TYPE_MISMATCH=0
GENERIC_MISMATCH=0
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

`apiCompatCheck` was explicitly run and exits 1 on exactly these 119 findings. That is the
correct state until strict compatibility reaches zero.

### Only three remaining members

```text
Microsoft.Xna.Framework.Content.ContentManager
  ReadAsset(String, System.Action<AutoCloseable>) -> T

Microsoft.Xna.Framework.Graphics.SpriteBatch
  Begin(SpriteSortMode, BlendState, SamplerState, DepthStencilState,
        RasterizerState, Effect) -> void
  Begin(SpriteSortMode, BlendState, SamplerState, DepthStencilState,
        RasterizerState, Effect, Matrix) -> void
```

Do not add these as nominal methods. `ReadAsset` needs the ContentReader/shared-resource
architecture. The SpriteBatch members need a real Effect foundation and CNA routing.

### Initially incomplete types now at zero

The following 15 of the original 51 target types were brought from local diagnostics to zero:

`BoundingBox`, `BoundingSphere`, `Color`, `GameTime`,
`GraphicsDevice`, `GraphicsDeviceManager`, `Matrix`, `Plane`,
`Point`, `Quaternion`, `Ray`, `Rectangle`, `Vector2`,
`Vector3`, and `Vector4`.

Only `ContentManager` and `SpriteBatch` remain nonzero among the initially
incomplete types. The exact ranked starting inventory is preserved in `plan.md`.

## Behavior corpus

Start: 106 observations, comprising 83 math/geometry and 23 input.

Current: 117 observations, comprising 94 math/geometry and 23 input.

Covered groups:

- MathHelper, Vector2/3/4, Matrix, Quaternion;
- Color, Point, Rectangle, Plane, Ray;
- BoundingBox, BoundingSphere, BoundingFrustum, GJK;
- Curve, CurveKey, CurveKeyCollection, loop/continuity/tangent behavior;
- all 17 packed-vector formats;
- keyboard, mouse, GamePad state/value filtering, and touch value/collection behavior.

The original 106 expected outputs are independently XNA-adjudicated neutral observations shared
with CNA-C#. The Java build does not depend on CNA-C# implementation code. The 11 additions are
packed-vector observations transcribed from XNA 4.0 IL, including nearest-even rounding, channel
order, half saturation, and signed minima.

Fixed CNA-Java behavior includes binary32 grouping for scalar vector division, Viewport
Project/Unproject edges, matrix decomposition/degeneracy, frustum/GJK relationships, curve
boundaries, packed rounding/saturation, and GamePad button filtering.

The frozen 106-line comparison outputs report:

- FNA: 49 differences from the XNA-adjudicated snapshot;
- MonoGame: 52 differences.

The 11 new packed observations have not yet been executed by those two harnesses.

## Implemented coherent groups

### Core/value

The existing math/geometry families have exact mapped public shape, value snapshots,
equals/hash/string behavior where mapped, and real differential edge tests. BoundingFrustum, GJK,
Curve and the packed-vector families are substantive implementations, not shells.

### GameWindow and lifecycle

ClientSizeChanged, OrientationChanged, and ScreenDeviceNameChanged use CNA event registrations.
Callbacks root Java objects, attach/detach native threads safely, contain Java exceptions, tolerate
listener removal during dispatch, and unsubscribe before Game destruction. Supported orientations
route through GraphicsDeviceManager. Opaque window tokens remain non-numeric publicly.

### GraphicsDeviceManager and GraphicsDevice

Both types now have zero local diagnostics. Managed preference validation and snapshot behavior are
separate from real CNA adapter/device routes. Device creation/change/reset events, mutable
PreparingDeviceSettings, adapter modes, presentation parameters, viewport, state, clear/present,
back-buffer readback, resources, render targets, buffers, and draw routes are covered.

Backend limitations remain visible as CNA result codes or explicit unsupported operations. No
native-dependent property is populated with a guessed default.

### Graphics states, textures, SpriteBatch, and SpriteFont

The coherent state/resource dependency groups are implemented: blend, depth/stencil, rasterizer,
sampler and texture collections, render targets/cube textures, vertex declarations/values,
vertex/index buffers, and packed vectors.

Texture2D transfer supports array windows, partial rectangles, mip levels, validation, stream
decoding, encoded output, association, and deterministic disposal through real CNA routes.

SpriteBatch has all texture Draw overloads, all six DrawString overloads, state-bearing Begin
overloads that do not require Effect, real CNA SpriteFont drawing, and measured Begin/End/Draw
validation. The only omitted overloads are the two Effect-dependent Begin members listed above.

### Input

Buttons/flags, GamePadButtons/DPad/Triggers/ThumbSticks/State/Capabilities, dead zones, player index,
packets, filtering, equality/hash/string behavior, native polling, and vibration are implemented.
Touch value state and collections are present; TouchPanel/gesture polling is not.

### Content

ContentManager has exact constructors, protected OpenStream-to-InputStream mapping, path cleaning,
case-insensitive caching, root validation, Unload, and deterministic resource lifetime. It really
loads loose PNG/JPEG Texture2D assets and CNA-Java SpriteFont descriptors.

This is not XNB support. Even after explicit C-API builtin-loader registration, a real XNB fails:

```text
white-1 references an unregistered .xnb content type reader
'Microsoft.Xna.Framework.Content.Texture2DReader'
```

CNA's C-API `RegisterBuiltinLoaders()` registers loose loaders, while the global
`RegisterAllBuiltInXnbReaders` has no C-API route. Preserve this blocker until a real reader
pipeline exists.

## JNI and ABI evidence

Starting bound symbol count: 53.

Current bound symbol count: 194.

```text
HEADER_ABI=0.7.0
BOUND_FUNCTIONS=194
MANIFEST_JNI_BINDING_CHECK=PASS
LAYOUT_SIGNATURE_PROBE=PASS
LIBRARY_ABI=0.7.0
LIBRARY_SYMBOL_CHECK=PASS (194/194)
HEADER_MISSING_SYMBOLS=0
LIBRARY_MISSING_SYMBOLS=0
```

JNI compiles with `-std=c11 -Wall -Wextra -Werror`. Manifest/header/signature/layout/library
identity all pass.

### CNA HEAD status

Read-only HEAD:

```text
1bb2145d99ed572dd4eb15009c34e2e5f410fcf0
```

It remains source-build blocked:

- `CNA_ENABLE_NET=OFF`: `modules/c-api/src/CnaCApiDetail.hpp:26` includes missing
  `Microsoft/Xna/Framework/GamerServices/GameUpdateRequiredException.hpp`;
- networking enabled: `CnaCApiCoreExt.cpp` observes 49 renderer identities against
  `CanonicalRendererCount == 50`.

Do not repair this in `../../cna`. Working native evidence used the ABI-0.7.0 checkout:

```text
revision=a09196a6477f69a7a57c8364f990658d31531a5b
library=/tmp/cna-java-native-working-070/modules/c-api/libcna_c_api.so
```

## Ownership evidence

Passing native stress:

- 25 Game create/run/destroy cycles;
- 200 Texture2D/SpriteBatch create/draw/destroy cycles;
- double-close after successful release;
- failed creation followed by successful creation;
- retained-resource release refusal and retry;
- wrong-thread release refusal and owner-thread retry;
- 25 bound vertex/index buffer auto-unbind cycles;
- release refusal for bindings outside the lifecycle callback, followed by unbind/retry;
- Game teardown with live unbound vertex/index buffer children;
- callback listener removal, throwing callback containment, and registration teardown;
- 150 draw calls across six routes, all returning the stable HEADLESS result 12.

No crash, observed leak, or use-after-free occurred. A potential upstream dangling-binding
use-after-free was found by inspection and blocked in Java. CNA should make buffer destruction
aware of current device bindings, or expose an unbind route that accepts a Game/device lifetime
handle outside callbacks.

Not yet covered: JVM-shutdown-specific behavior, sanitizers, and an isolated subprocess crash
runner.

## Final verification

The final implementation checkpoint passed:

```bash
env CNA_NATIVE_LIBRARY=/tmp/cna-java-native-working-070/modules/c-api/libcna_c_api.so \
    XNA_REFERENCE_DIR=/path/to/pinned/xna40/windows \
    ./gradlew --no-daemon check apiCompatReport javadoc sourcesJar
```

Evidence:

- 87/87 JUnit tests;
- 20/20 native integration tests within that suite;
- 22/22 verifier tests;
- compile probe pass;
- JNI warning-clean pass;
- 194/194 ABI symbols;
- Javadoc and sources JAR pass.

The strict red gate was checked separately:

```bash
./gradlew --no-daemon apiCompatCheck
```

Expected result: exit 1, exactly 119 diagnostics.

## Template and generated-project evidence

The exact-artifact gate ran with stability enabled:

```bash
env CNA_NATIVE_LIBRARY=/tmp/cna-java-native-working-070/modules/c-api/libcna_c_api.so \
    CNA_RUN_STABILITY_TEST=1 \
    scripts/verify-template.sh
```

It rebuilt CNA-Java, published to a unique temporary Maven repository, rebuilt the checked-in
template, generated a separate project, and used only those fresh artifacts.

Passed:

- 4 template tests;
- checked-in template build/installDist;
- generated-project build/test/installDist;
- 60 frames;
- 600 frames.

The canary remains truthful and unchanged: Game lifecycle, GameTime, title, Escape, left click,
Clear, raw PNG FromStream, moving SpriteBatch draw, and deterministic cleanup. No XNB, Effect,
GamePad, or resize showcase was added.

## Platform status

Runtime-qualified:

- Linux x86-64;
- HEADLESS renderer;
- NULL audio.

Not runtime-qualified: Windows, macOS, Android, iOS, Browser/WASM. JNI selection and compile-time
portability do not promote those platforms.

## Immediate next work

1. Re-run the cheap managed baseline and inspect the three member findings before editing.
2. Implement the full Effect dependency group from XNA metadata and actual CNA routes, then close
   both SpriteBatch Begin findings with differential validation.
3. Implement ContentReader/ContentTypeReader/readers/shared resources and only then ReadAsset.
   Coordinate the missing CNA XNB-reader registration route upstream.
4. Add subprocess, JVM shutdown, and sanitizer ownership evidence; do not merely increase loop
   counts.
5. Carry the 11 new packed observations through XNA/FNA/MonoGame comparison harnesses.
6. Address upstream CNA's two HEAD build blockers and bound-buffer safety outside this read-only
   task.
7. Continue missing types only as complete dependency groups. Preserve every zero category,
   `CNA_INTERNAL_LEAK=0`, and `ALLOWLIST_ENTRIES=0`.

At every checkpoint run `check`, `apiCompatReport`, Javadoc/sources, ABI validation,
native integration when available, and `git diff --check`. Run the fresh 60/600-frame
template gate before claiming a final artifact.
