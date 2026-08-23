# CNA-Java measured engineering plan

**Status:** deep, coherent partial XNA 4.0 projection; strict compatibility remains incomplete

**Updated:** 2026-08-23

**Selected profile:** XNA 4.0 Windows runtime projected to Java 17

**Runtime-qualified platform:** Linux x86-64, CNA HEADLESS renderer, NULL audio

## Authority and guardrails

The compatibility authority is, in order:

1. actual Microsoft XNA 4.0 behavior, IL, and reference metadata;
2. an independent reference snapshot where available;
3. FNA and MonoGame as comparison implementations only.

`../cna-cs` supplies neutral inputs and independently adjudicated expected observations; its
implementation is not the Java specification and is not a Java build dependency. CNA's C API is
the implementation boundary for native behavior. Public API shape is determined by
`docs/xna-java-mapping.md` and `tools/api-compat/mapping-rules.json`, never by what is
convenient to bind.

Do not reduce diagnostics by weakening the verifier, mapping a real API away, introducing an
allowlist, or exposing CNA internals. Pure value/math behavior stays in Java. Native-dependent
values are not filled with guessed defaults.

## Exact measured checkpoint

The starting snapshot and the final 2026-08-23 checkpoint use the same pinned XNA profile.

| Metric | Start | Current | Change |
|---|---:|---:|---:|
| REFERENCE_TYPES | 257 | 257 | 0 |
| REFERENCE_MEMBERS | 2,964 | 2,964 | 0 |
| EXPECTED_JAVA_TYPES | 261 | 265 | +4 |
| EXPECTED_JAVA_MEMBERS | 3,086 | 3,178 | +92 |
| TARGET_TYPES | 51 | 149 | +98 |
| TARGET_MEMBERS | 912 | 2,171 | +1,259 |
| TOTAL_DIAGNOSTICS | 462 | 119 | -343 |
| MISSING_TYPE | 210 | 116 | -94 |
| MISSING_MEMBER | 250 | 3 | -247 |
| INTERFACE_MISMATCH | 2 | 0 | -2 |
| CNA_INTERNAL_LEAK | 0 | 0 | 0 |
| ALLOWLIST_ENTRIES | 0 | 0 | 0 |

Every other implemented-contract category is zero:

```text
ACCESSIBILITY_MISMATCH=0
BASE_TYPE_MISMATCH=0
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
```

`apiCompatReport` is a passing measurement task. `apiCompatCheck` remains
deliberately red with exactly 119 diagnostics until strict compatibility reaches zero.

The three remaining member findings are intentional dependency boundaries:

- `ContentManager.ReadAsset(String, System.Action<AutoCloseable>)`, which requires the real
  ContentReader/shared-resource pipeline;
- the two `SpriteBatch.Begin` overloads that accept `Effect`, one of which also
  accepts a transform `Matrix`.

The other 116 findings are missing types, not hidden incomplete members.

## Starting local-diagnostic inventory

At the start, all 250 missing members and both interface mismatches belonged to only 17 of the 51
present target types. The descriptor-based classification below records the work that was selected
before expanding dependency groups. “Property” and “event” counts are Java accessor counts.

| Existing type | Total | Ctors | Properties | Methods | Events/fields | Interface | Implementation split |
|---|---:|---:|---:|---:|---:|---:|---|
| GraphicsDevice | 69 | 1 | 28 | 28 | 12 | 0 | managed validation + CNA |
| GraphicsDeviceManager | 44 | 0 | 18 | 11 | 14 | 1 | managed preferences + CNA |
| Matrix | 34 | 0 | 12 | 22 | 0 | 0 | pure Java |
| Vector4 | 23 | 0 | 0 | 23 | 0 | 0 | pure Java |
| Vector3 | 17 | 0 | 0 | 17 | 0 | 0 | pure Java |
| Vector2 | 15 | 0 | 0 | 15 | 0 | 0 | pure Java |
| SpriteBatch | 10 | 0 | 0 | 10 | 0 | 0 | validation + CNA |
| BoundingSphere | 9 | 2 | 0 | 7 | 0 | 0 | pure Java |
| BoundingBox | 8 | 2 | 0 | 6 | 0 | 0 | pure Java |
| Plane | 6 | 2 | 0 | 4 | 0 | 0 | pure Java |
| Ray | 4 | 2 | 0 | 2 | 0 | 0 | pure Java |
| GameTime | 3 | 0 | 3 | 0 | 0 | 0 | value snapshots |
| Quaternion | 3 | 0 | 0 | 3 | 0 | 0 | pure Java |
| Rectangle | 3 | 0 | 2 | 1 | 0 | 0 | pure Java |
| ContentManager | 2 | 0 | 0 | 2 | 0 | 0 | cache/validation + CNA |
| Color | 1 | 0 | 0 | 0 | 0 | 1 | pure Java |
| Point | 1 | 0 | 0 | 1 | 0 | 0 | pure Java |

The two interface findings were the missing packed-vector contract on `Color` and the
missing graphics manager/service interfaces on `GraphicsDeviceManager`. Both are resolved,
and `INTERFACE_MISMATCH=0` is now a hard invariant.

## Existing types completed

Fifteen initially incomplete types now have zero local strict diagnostics:

- `BoundingBox`, `BoundingSphere`, `Color`, `GameTime`;
- `GraphicsDevice`, `GraphicsDeviceManager`;
- `Matrix`, `Plane`, `Point`, `Quaternion`, `Ray`,
  `Rectangle`;
- `Vector2`, `Vector3`, `Vector4`.

Of the initially incomplete types, only `ContentManager` and `SpriteBatch` retain
local findings, precisely the three dependency-gated members listed above.

## Behavior corpus

The shared neutral corpus grew from 106 observations to 117:

- math/geometry: 83 to 94;
- input: 23 to 23.

It covers `MathHelper`, vectors, `Matrix`, `Quaternion`, `Color`,
`Point`, `Rectangle`, `Plane`, `Ray`, bounding box/sphere/frustum,
curve evaluation, all 17 packed-vector formats, keyboard, mouse, GamePad value state, and touch
value state.

The original 106 expected lines are independently XNA-adjudicated observations shared with the
CNA-C# corpus. The 11 new Java observations are packed-vector edges transcribed from XNA 4.0 IL:
channel order, half saturation, signed minima, and nearest-even midpoint rounding. CNA-Java fixes
made during this phase include binary32 scalar-division grouping, matrix/viewport edge behavior,
frustum/GJK relationships, curve boundaries, packed rounding/saturation, and GamePad button
filtering.

Against the same 106-line XNA-adjudicated snapshot, FNA differs on 49 observations and MonoGame on
52. The additional 11 Java packed observations have not yet been run through those comparison
harnesses, so no cross-framework claim is made for them.

## Coherent implementation groups

### Core math and geometry

The vector, matrix, quaternion, plane, ray, rectangle, and bounding families now have exact mapped
shape and differential edge behavior. `BoundingFrustum`, the GJK helper, and the complete
curve family are real pure-Java implementations. Mutable value inputs and retained values are
snapshotted.

### Window, device lifecycle, and graphics foundation

`GameWindow` routes native client-size, orientation, and screen-device-name events. JNI
roots callbacks, attaches native threads when needed, contains Java exceptions, preserves ordered
copy-on-write listener dispatch, and unsubscribes before Game destruction. Supported orientations
route through `GraphicsDeviceManager`; activation/deactivation continue through Game
lifecycle callbacks.

`GraphicsDeviceManager` and `GraphicsDevice` now have zero local shape diagnostics.
Their preference/state/event behavior is divided between Java validation/snapshotting and real CNA
routes. Adapter/display modes, presentation parameters, viewport, clear/present/reset, resource
events, render targets, state collections, back-buffer readback, vertex/index buffers, and six draw
routes are represented without public native handles. A HEADLESS backend rejection remains a real
backend result, not a fabricated success.

### Graphics state, SpriteBatch, fonts, and texture transfer

Blend, depth/stencil, rasterizer, sampler, texture-collection, render-target, vertex declaration,
vertex value, index/vertex buffer, and packed-vector dependency groups are implemented coherently.
State descriptors become immutable while bound where XNA requires it.

`SpriteBatch` performs real CNA-backed texture draws and all six `DrawString`
projections through real CNA-backed `SpriteFont`. Begin/End/Draw ordering, nulls, disposed
resources, retained texture release, and recovery are tested. Only the two Effect-bearing Begin
overloads remain absent.

`Texture2D` supports real encoded stream loading, PNG/JPEG output routes, device
association/disposal, array windows, rectangles, and mip levels through the CNA raw transfer API.
Element codecs and validation reject unsupported representations rather than reinterpreting them
incorrectly.

### Input

Buttons/flags, GamePad value types, dead-zone rules, packet/button filtering, equality/hash/string
behavior, capabilities, native polling, and vibration routing are present. Touch value types and
collections are covered; the static `TouchPanel`/gesture group remains among the missing
types. Native CI validates polling reachability under HEADLESS, not synthetic controller input.

### Content

`ContentManager` has exact constructors, protected `OpenStream(String)` mapped to
`InputStream`, cleaned/case-insensitive cache keys, root-directory validation, unload, and
deterministic resource cleanup. It loads real loose PNG/JPEG textures and the CNA-Java SpriteFont
descriptor format. It does not claim XNB support.

Registering CNA's C-API builtin loaders is necessary but insufficient for XNB: the current C++
`RegisterBuiltinLoaders()` registers loose loaders only, and the C API exposes no route to
the global `RegisterAllBuiltInXnbReaders`. A real texture XNB therefore fails with an
unregistered `Microsoft.Xna.Framework.Content.Texture2DReader`, which is preserved as an
explicit blocker.

## JNI and ABI

The JNI manifest grew from 53 to 194 bound CNA ABI functions. Final evidence against the working
ABI-0.7.0 library is:

```text
HEADER_ABI=0.7.0
BOUND_FUNCTIONS=194
MANIFEST_JNI_BINDING_CHECK=PASS
LAYOUT_SIGNATURE_PROBE=PASS
LIBRARY_ABI=0.7.0
LIBRARY_SYMBOL_CHECK=PASS (194/194)
```

Header missing symbols: 0. Library missing symbols: 0. JNI compiles as C11 with
`-Wall -Wextra -Werror`. Raw resource addresses remain internal.

The current read-only CNA HEAD is
`1bb2145d99ed572dd4eb15009c34e2e5f410fcf0`. It is not usable as final native evidence:

- with networking disabled, `CnaCApiDetail.hpp` includes the absent
  `Microsoft/Xna/Framework/GamerServices/GameUpdateRequiredException.hpp`;
- with networking enabled, the native renderer inventory asserts 49 entries against a canonical
  count of 50.

The working fallback is revision
`a09196a6477f69a7a57c8364f990658d31531a5b`, ABI 0.7.0, built at
`/tmp/cna-java-native-working-070/modules/c-api/libcna_c_api.so`. This path is evidence for
the current workspace, not a packaging solution.

## Ownership and stress

Owned, borrowed, parent-owned, and adopted handles release deterministically. Failed native release
keeps the Java handle live for retry; double-close is idempotent after successful release. Game
closes registered children in reverse creation order before destroying its native parent. No Java
finalizers were introduced.

Final native stress evidence includes:

- 25 Game create/run/destroy cycles;
- 200 Texture2D/SpriteBatch create/draw/destroy cycles with double-close;
- failed encoded-texture creation followed by successful creation;
- retained Texture2D release refusal followed by End/retry success;
- wrong-thread release refusal followed by owner-thread retry success;
- 25 vertex/index buffer bind/close cycles with Java-side auto-unbind;
- bound-buffer release refusal outside a lifecycle callback, then native unbind and retry;
- live unbound buffer children released during Game teardown;
- listener removal during dispatch, listener exceptions contained and rethrown at a Java boundary,
  and callback registrations detached before Game destruction;
- 150 calls across the six draw routes with stable HEADLESS result 12.

No native crash, observed leak, or use-after-free occurred. Inspection did find a potential upstream
use-after-free: CNA device binding state retains raw vertex/index buffer pointers, while buffer
destruction does not check those bindings. CNA-Java now prevents that route. The preferred upstream
fix is for destroy to refuse or clear current bindings internally, or for the C API to expose a
game-handle unbind route valid outside lifecycle callbacks.

JVM-shutdown-specific, sanitizer, and isolated-subprocess crash tests are still missing and must not
be claimed.

## Verification evidence

The final clean checkpoint passed:

- `./gradlew --no-daemon check apiCompatReport javadoc sourcesJar`;
- 87 JUnit tests, including 20 native integration tests;
- 22 verifier tests;
- compile probe;
- JNI warning-clean compilation;
- 194/194 ABI manifest/header/library validation;
- `git diff --check`.

`apiCompatCheck` was also run and failed exactly as expected on the remaining 119 strict
diagnostics.

The template gate rebuilt CNA-Java, published a unique temporary artifact, rebuilt the checked-in
template, generated and rebuilt an independent project, and passed:

- 4 template tests;
- install distributions for the template and generated project;
- 60-frame smoke;
- 600-frame stability.

The canary continues to exercise only real features: lifecycle/GameTime, window title,
keyboard Escape, mouse left click, GraphicsDevice.Clear, raw PNG
Texture2D.FromStream, moving SpriteBatch drawing, and deterministic cleanup. No showcase-only
feature was added.

## Platform and packaging truth

Only Linux x86-64 with HEADLESS rendering and NULL audio is runtime-qualified. Windows, macOS,
Android, iOS, and Browser/WASM remain unqualified. Choosing JNI for Java 17 and possible future
Android work is not Android evidence.

Temporary Maven publication and the generated-project workflow are reproducible. Native
OS/architecture artifact publication, signing, repository staging, and release automation remain
unfinished. `CNA_NATIVE_LIBRARY` must continue to override native discovery explicitly.

## Ordered next work

1. Implement the complete Effect dependency family from authoritative metadata and real CNA
   routes, then add the two remaining SpriteBatch Begin overloads and their validation corpus.
2. Design and implement ContentReader, ContentTypeReader, reader tables/versions, shared resources,
   caching, and custom/builtin readers; only then add ReadAsset. Obtain a real C-API path for XNB
   reader registration instead of treating loose files as XNB.
3. Extend the neutral corpus with the 11 new packed observations on XNA/FNA/MonoGame and add
   subprocess/JVM-shutdown/sanitizer ownership evidence.
4. Ask upstream CNA to fix both HEAD build configurations and bound-buffer destruction safety.
5. Expand the remaining 116 types only in behaviorally complete dependency groups; likely next
   candidates after Effect/Content are dynamic buffers, TouchPanel/gestures, and the smallest
   coherent graphics effect family.
6. Defer broad platform and publication claims until each has actual build and runtime evidence.
