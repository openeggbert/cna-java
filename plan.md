# CNA-Java measured engineering plan

**Status:** member-complete, structurally strict XNA 4.0 Java projection with 81 dependency-group types remaining

**Updated:** 2026-08-23

**Selected profile:** XNA 4.0 Windows runtime projected to Java 17

**Runtime-qualified platform:** Linux x86-64, CNA HEADLESS renderer, NULL audio

## Authority and invariants

Microsoft XNA 4.0 metadata, IL, and measured behavior remain authoritative. CNA-C# is an
engineering reference and FNA/MonoGame are comparison implementations. CNA's ABI is the native
implementation boundary; it does not define the public Java contract.

Mapping rules were not weakened, no allowlist was introduced, no raw CNA handle entered public or
protected API, and missing API was not hidden through broad Java adaptations. The verifier's four
new expected members are the explicit Java `Iterable.iterator()` bridges on the four Effect
reflection collections.

## Exact run checkpoint

This run began at the user's hard baseline and finished against the same pinned XNA profile:

| Metric | Before | Current | Change |
|---|---:|---:|---:|
| REFERENCE_TYPES | 257 | 257 | 0 |
| REFERENCE_MEMBERS | 2,964 | 2,964 | 0 |
| EXPECTED_JAVA_TYPES | 265 | 265 | 0 |
| EXPECTED_JAVA_MEMBERS | 3,178 | 3,182 | +4 |
| TARGET_TYPES | 149 | 184 | +35 |
| TARGET_MEMBERS | 2,171 | 2,492 | +321 |
| TOTAL_DIAGNOSTICS | 119 | 81 | -38 |
| MISSING_TYPE | 116 | 81 | -35 |
| MISSING_MEMBER | 3 | 0 | -3 |
| CNA_INTERNAL_LEAK | 0 | 0 | 0 |
| ALLOWLIST_ENTRIES | 0 | 0 | 0 |

Every other strict category is exactly zero:

```text
ACCESSIBILITY_MISMATCH=0
BASE_TYPE_MISMATCH=0
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
```

`apiCompatReport` is green as a measurement task. `apiCompatCheck` was run and exits 1 solely on
the 81 missing types. The central goal was achieved: **MISSING_MEMBER reached 0**.

Local diagnostics are zero for `Effect`, `EffectTechnique`, `EffectPass`, `EffectParameter`,
`ContentManager`, `ContentReader`, `ContentTypeReader`, `SpriteBatch`, `DynamicVertexBuffer`,
`DynamicIndexBuffer`, and `TouchPanel`.

## Effect, SpriteBatch, and stock-effect result

The full base reflection family is substantive: `Effect`, techniques, passes, parameters,
annotations, all four collections, both parameter enums, and required `Texture3D` support. Typed
parameter getters/setters cover booleans, integers, floats, strings, vectors, quaternions,
matrices, their typed arrays, and texture families through real CNA calls. Compiled bytecode,
cloning, technique selection, reflection, and `EffectPass.Apply` use native routes.

Effect owns its native resource. Reflection nodes and collection objects are stable Java views;
their separately retained CNA view handles are registered under the parent and released in reverse
order before the Effect. Repeated current-technique, collection, index, and name lookup returns the
same wrapper. A foreign technique is rejected. Parent close invalidates live children without a
child ever destroying its parent, and successful double close is idempotent.

Both missing Effect-bearing `SpriteBatch.Begin` overloads now snapshot all state and the optional
matrix and pass the actual Effect to `cna_sprite_batch_begin_with_effect`. Null Effect, disposed or
foreign-device Effect, invalid sort mode, nested Begin, End-before-Begin, failed-Begin recovery,
and mutable matrix snapshot behavior are tested. SpriteBatch now has zero local diagnostics.

The coherent stock group `IEffectFog`, `IEffectLights`, `IEffectMatrices`, `DirectionalLight`, and
`BasicEffect` is implemented. BasicEffect is created and executed by CNA, exposes real matrices,
fog, material, texture, lighting, and three stable directional-light views, supports native clone
and default lighting, and applies a real pass on HEADLESS. Directional-light view destruction only
releases its view handle; parent Effect state remains parent-owned.

## Managed ContentReader/XNB result

`ContentReader`, `ContentTypeReader`, `ContentTypeReaderOfT`, `ContentTypeReaderManager`, all five
serializer attributes, `ResourceContentManager`, and the required Java `System` projections are
implemented as a managed XNB runtime. `ContentManager.ReadAsset` now participates in real reader
dispatch, cache/type checks, shared resources, disposable recording, partial-failure cleanup,
Unload, and Dispose; it is not an alias for the loose-file loader.

The parser validates Windows XNB version 5 framing, declared size, reader count/table, reader
activation and version, object reader indexes, shared-resource indexes/fixups, external references,
existing-instance identity, requested Java type, duplicate disposable occurrences, and reader
failures. A registry-activated user reader loads a synthetic custom asset through ordinary
`ContentManager.Load`; no test type is special-cased in ContentManager.

Built-in managed readers currently cover Boolean, signed/unsigned integer widths, byte/character,
float/double, String, Vector2/3/4, Quaternion, Matrix, and Color. Generated fixtures contain no
Microsoft proprietary content.

Truthful remaining XNB limits:

- compressed XNB/LZX framing is not implemented;
- graphics/resource readers such as Texture2DReader are not yet implemented;
- consequently Texture2D XNB is not supported;
- CNA's global `RegisterAllBuiltInXnbReaders` still has no C API route, but the managed reader
  architecture no longer waits on it;
- a future Texture2DReader can parse in Java and use the already-bound CNA texture create/upload
  routes; no additional generic loose-loader dependency is needed.

## Core runtime, dynamic buffers, and touch

`FrameworkDispatcher.Update` pumps real CNA framework services and is not empty.
`TitleContainer.OpenStream` provides the mapped portable readable-stream/path behavior with tests.

`DynamicVertexBuffer` and `DynamicIndexBuffer` preserve base relationships, route full-buffer
None/Discard/NoOverwrite uploads through CNA, expose real ContentLost callback registrations, and
unsubscribe before destruction. CNA's offset/raw transfer ABI cannot carry a SetDataOptions value,
so offset uploads with Discard/NoOverwrite fail explicitly rather than silently behaving as None.
The dangerous native bound-buffer destruction path remains guarded by Java auto-unbind/refusal and
retry behavior.

`TouchPanel`, `TouchPanelCapabilities`, `GestureType`, and `GestureSample` are complete for the
selected profile. Raw touch capabilities/state, display properties, opaque window token, gesture
mask, queue availability/read, and CNA's real gesture-recognition/update route are bound. Tests use
the ABI's deterministic extension inputs only to drive the real queue and recognizer; the public
API fabricates no gesture.

The working ABI-0.7 library exposes immediate raw-state updates and retains a released slot longer
than current CNA HEAD's pure-test documentation. Assertions are limited to shared XNA-visible
semantics rather than treating that revision detail as authoritative.

## Behavior corpus

The repository still contains 117 deterministic observations: 94 math/geometry and 23 input. No
new Effect, Content, dynamic-buffer, or TouchPanel text observations were added in this run; those
groups gained native/unit integration tests instead.

The 11 previously added packed-vector cases were carried through a temporary neutral FNA probe.
FNA matched 9/11 XNA-adjudicated lines and differed on:

```text
packed.half4.saturation
packed.nshort4.minimum
```

Microsoft XNA cannot execute on this Linux host; the expected 11 lines remain derived from its IL.
MonoGame's local probe assembly requires a .NET 6+ runtime, which is absent here, so the new 11
could not be executed there. The prior frozen 106-line comparison remains FNA 49 differences and
MonoGame 52 differences; those expectations were not revised.

## JNI and native ABI

Bound functions grew from 194 to 338:

- 113 Effect/BasicEffect/SpriteBatch symbols;
- 5 Texture3D symbols required by Effect parameters;
- 1 FrameworkDispatcher symbol;
- 5 dynamic-buffer upload/callback symbols;
- 20 touch/gesture symbols;
- 0 new content symbols because XNB framing and dispatch are managed Java.

Final ABI evidence:

```text
HEADER_ABI=0.7.0
BOUND_FUNCTIONS=338
MANIFEST_JNI_BINDING_CHECK=PASS
LAYOUT_SIGNATURE_PROBE=PASS
LIBRARY_ABI=0.7.0
LIBRARY_SYMBOL_CHECK=PASS (338/338)
```

JNI compiles as C11 with `-Wall -Wextra -Werror`. Header, manifest, layout/signature, and loaded
library identity all pass.

The read-only CNA HEAD was checked once and remains
`1bb2145d99ed572dd4eb15009c34e2e5f410fcf0`. Its known source blockers remain: networking-off
includes an absent GamerServices detail header, while networking-on asserts renderer inventory 49
against canonical count 50. No CNA source was changed. Integration evidence therefore uses the
documented compatible ABI-0.7 library at
`/tmp/cna-java-native-working-070/modules/c-api/libcna_c_api.so`.

## Ownership and failure evidence

Existing native stress remains green: 25 Game cycles, 200 Texture2D/SpriteBatch cycles, 25 bound
vertex/index cycles, and 150 draw calls. New tests cover Effect parents with live technique/pass/
collection/light children, ContentReader duplicate and partial-load disposables, dynamic callback
teardown, failed-operation recovery, foreign ownership, and parent close with live children.

An isolated child JVM now reaches natural process termination with a live Game, Effect/light/pass
graph, bound dynamic vertex/index buffers, and registered ContentLost callbacks. It exits 0 without
a crash or hang. This is process-shutdown evidence, not allocator-level leak proof. No sanitizer
run was performed because no sanitizer-built compatible CNA library is available; no claim of
allocator leak freedom is made.

No native crash, observed use-after-free, or Java-side ownership duplication occurred. The known
upstream binding defect remains: CNA stores raw bound vertex/index pointers without a destruction
contract. Desired upstream behavior is destroy-time refusal/clearing, or a stable C unbind route
usable outside lifecycle callbacks. CNA-Java's safety guard must not be weakened.

## Verification and template

Passing final commands/evidence:

- `./gradlew --no-daemon check`
- `./gradlew --no-daemon apiCompatReport`
- `./gradlew --no-daemon javadoc sourcesJar`
- native ABI and all JNI integration tests against ABI 0.7/HEADLESS
- `scripts/verify-template.sh` with an exact temporary Maven artifact
- sibling template build/tests and generated-project build/tests
- unchanged template runtime: 60 frames and 600 frames on Linux x86-64 HEADLESS/NULL

`apiCompatCheck` was also run and failed exactly on 81 missing types, as expected. The template was
not expanded: no Effect/3D or XNB path was added because the present canary already exercises its
truthful runtime path and Texture2D XNB is not implemented.

## Remaining dependency groups

The 81 missing types are now exactly: Graphics 21, Audio/XACT 19, Media/Video 24, Storage 3,
Design 13, and GamerServices 1. Content, Touch, and non-Design core have no missing selected-profile
types.

Continue in coherent groups:

1. finish executable stock effects (`AlphaTestEffect`, `DualTextureEffect`,
   `EnvironmentMapEffect`, `SkinnedEffect`) and then model/remaining graphics dependencies;
2. add managed graphics XNB readers, beginning with Texture2DReader and then model dependencies;
3. implement Audio/XACT, using FrameworkDispatcher for pump-sensitive behavior;
4. implement Media/Video, then Storage;
5. leave Design converters until runtime families are healthy.

At every checkpoint preserve `MISSING_MEMBER=0` and every strict mismatch category at zero.
