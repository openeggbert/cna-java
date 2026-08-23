# CNA-Java continuation handoff

**Updated:** 2026-08-23

This is the tactical continuation point for the current uncommitted worktree. Read `plan.md`, this
file, `docs/xna-java-mapping.md`, the verifier/native manifests, and both writable worktrees before
changing code. Preserve the pre-existing untracked `out` file.

## Repository boundaries

Writable:

- this repository, `cna-java`;
- `../cna-java-template`.

Read-only references:

- `../cna`;
- `../cna-cs`, `../cna-cs-template`;
- `../cna-ts`, `../cna-ts-template`;
- `../cna-rust`, `../cna-rust-template`.

No read-only sibling was modified. The template source is unchanged and clean apart from generated
build output ignored by Git.

## Exact strict state

```text
REFERENCE_TYPES=257
REFERENCE_MEMBERS=2964
EXPECTED_JAVA_TYPES=265
EXPECTED_JAVA_MEMBERS=3182
TARGET_TYPES=184
TARGET_MEMBERS=2492
TOTAL_DIAGNOSTICS=81
MISSING_TYPE=81
MISSING_MEMBER=0

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

`MISSING_MEMBER=0` is now a permanent invariant. Local diagnostics are zero for Effect,
EffectTechnique, EffectPass, EffectParameter, ContentManager, ContentReader, ContentTypeReader,
SpriteBatch, DynamicVertexBuffer, DynamicIndexBuffer, and TouchPanel.

The remaining exact type distribution is:

```text
Graphics=21
Audio/XACT=19
Media/Video=24
Storage=3
Design=13
GamerServices=1
```

There are no remaining missing Content, Touch, or non-Design core types.

## What is complete in this worktree

- Full native Effect reflection/ownership family and typed parameter APIs.
- Both real Effect-bearing SpriteBatch.Begin overloads.
- Managed XNB ContentReader type system, custom reader registry, reader table, shared resources,
  existing instances, external references, disposable ownership, and real ContentManager.ReadAsset.
- Serializer attributes and ResourceContentManager.
- FrameworkDispatcher and TitleContainer.
- DynamicVertexBuffer/DynamicIndexBuffer with real option-aware full uploads and ContentLost
  subscriptions; unrepresentable offset+Discard/NoOverwrite is explicitly rejected.
- TouchPanel/capabilities/gesture types and real CNA polling/gesture routes.
- IEffectFog/Lights/Matrices, DirectionalLight, and executable BasicEffect.
- Isolated live-native-graph JVM shutdown test.

Do not redo completed math, device, buffer, SpriteFont, texture, Effect, ContentReader, core runtime,
dynamic buffer, TouchPanel, or BasicEffect work.

## XNB continuation boundary

Custom readers and shared resources pass through ordinary ContentManager load. Primitive/String and
Vector/Quaternion/Matrix/Color built-in readers are real. Windows uncompressed XNB version 5 is
supported with strict framing validation.

Still missing:

- LZX-compressed XNB;
- Texture2DReader and other graphics/resource built-ins;
- Texture2D XNB upload;
- model XNB readers.

Do not route these through CNA's loose-loader registry or wait blindly for
`RegisterAllBuiltInXnbReaders`. Add managed readers and call existing native create/upload APIs only
where native resources are actually required.

## Native evidence

```text
starting bound functions=194
current bound functions=338
HEADER_ABI=0.7.0
MANIFEST_JNI_BINDING_CHECK=PASS
LAYOUT_SIGNATURE_PROBE=PASS
LIBRARY_ABI=0.7.0
LIBRARY_SYMBOL_CHECK=PASS (338/338)
```

This run added 113 Effect/stock/SpriteBatch, 5 Texture3D, 1 FrameworkDispatcher, 5 dynamic-buffer,
and 20 touch symbols. Content XNB added no JNI dependency.

Read-only CNA HEAD remains `1bb2145d99ed572dd4eb15009c34e2e5f410fcf0` and still has the
documented networking-off missing detail header and networking-on 49/50 renderer inventory
blockers. Continue integration with the compatible ABI-0.7 library:

```text
/tmp/cna-java-native-working-070/modules/c-api/libcna_c_api.so
```

Never silently load an ABI-incompatible library and do not modify `../cna`.

## Behavior and ownership evidence

The corpus remains 117 observations: 94 math/geometry and 23 input. A temporary FNA probe ran the
11 new packed cases: 9 matched XNA IL expectations and 2 differed (`packed.half4.saturation`,
`packed.nshort4.minimum`). XNA execution is unavailable on Linux. The local MonoGame assembly
cannot run without a missing .NET 6+ runtime, so no new MonoGame claim was made. Existing frozen
106-line results remain FNA 49 and MonoGame 52 differences.

Content tests cover duplicate disposables and partial-failure cleanup. Effect tests cover stable
identity, foreign ownership, parent disposal with live children, and double close. Dynamic tests
cover callbacks, invalid options/recovery, binding guards, and parent teardown. The isolated child
JVM exits 0 with a live native ownership graph. No sanitizer run was performed and no allocator leak
claim is valid.

Preserve the Java guard around CNA's potentially dangling bound vertex/index buffer pointers. CNA
needs destroy-time unbind/refusal or a stable out-of-callback unbind C route.

## Verified commands

The following passed on Linux x86-64 with `CNA_RENDERER=HEADLESS` and NULL audio:

```text
./gradlew --no-daemon check
./gradlew --no-daemon apiCompatReport
./gradlew --no-daemon javadoc sourcesJar
nativeAbiCheck: 338/338
scripts/verify-template.sh
template: 60 frames
template: 600 frames
```

`apiCompatCheck` was run and deliberately exits 1 on exactly the 81 missing types. Both writable
repositories passed `git diff --check` at the measured checkpoint. The template did not gain 3D or
XNB because Texture2D XNB is not yet real and the existing canary remains truthful.

## Recommended next work

1. Implement the remaining stock-effect family on the already exported CNA ABI, starting with
   AlphaTestEffect and DualTextureEffect, then EnvironmentMapEffect and SkinnedEffect.
2. Implement Texture2DReader in managed Java and upload parsed data through the existing texture
   routes; add only generated or openly licensed fixtures.
3. Build the model dependency group after stock effects and graphics readers are stable.
4. Implement Audio/XACT, then Media/Video, then Storage.
5. Finish Design converters last.

Use the live verifier after every coherent group. Preserve every zero category and keep the
allowlist empty.
