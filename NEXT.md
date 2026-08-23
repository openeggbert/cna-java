# CNA-Java continuation handoff

**Updated:** 2026-08-23

This is the tactical continuation point for the current uncommitted worktree. Read `plan.md`, this
file, `README.md`, `docs/xna-java-mapping.md`, `docs/architecture.md`, the verifier/native manifests,
and both writable worktrees before changing code. Preserve the pre-existing untracked `out` file.

## Repository boundaries

Writable:

- this repository, `cna-java`;
- `../cna-java-template`.

Read-only references:

- `../cna`;
- `../cna-cs`, `../cna-cs-template`;
- `../cna-ts`, `../cna-ts-template`;
- `../cna-rust`, `../cna-rust-template`.

No read-only sibling was modified. The template now intentionally contains the separate generated
Texture2D XNB canary described below.

## Exact strict state

```text
REFERENCE_TYPES=257
REFERENCE_MEMBERS=2964
EXPECTED_JAVA_TYPES=265
EXPECTED_JAVA_MEMBERS=3200
TARGET_TYPES=205
TARGET_MEMBERS=2730
TOTAL_DIAGNOSTICS=60
MISSING_TYPE=60
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

The exact remaining distribution is:

```text
Graphics=0
Audio/XACT=19
Media/Video=24
Storage=3
Design=13
GamerServices=1
```

There are no missing Graphics, Content, Touch, or ordinary non-Design core types.

## Completed graphics milestone

- AlphaTestEffect, DualTextureEffect, EnvironmentMapEffect, and SkinnedEffect have exact contracts,
  real CNA objects/passes/clones, validation, wrong-device checks, and ownership tests.
- EffectMaterial and OcclusionQuery are strict-complete and CNA-native verified. The three graphics
  device exceptions are strict-complete.
- Model, ModelBone, ModelMesh, ModelMeshPart, all four collections/enumerators, and
  ModelEffectCollection are strict-complete. Identity/read-only graph behavior and a real indexed
  draw are verified.
- Internal Texture2DReader supports exact uncompressed Color payloads and mip chains. Other formats
  are rejected without fidelity loss until CNA exposes matching surface-format routes.
- Internal SpriteFontReader creates a real CNA font over its content-owned Color atlas; measure,
  fallback, DrawString, and teardown are verified.
- Internal VertexDeclaration/VertexBuffer/IndexBuffer/Effect/BasicEffect/Model readers load a
  synthetic legal model through the normal reader table and shared-resource mechanism.

Do not redo these families without evidence of a defect. In particular, do not weaken the bound
vertex/index guard, make Model disposable, add public reader types, or fall back to loose-asset
loading for XNB resources.

## Remaining XNB boundary

Uncompressed Windows XNB v5 is stable. LZX-compressed framing remains unimplemented and fails
explicitly. Texture formats other than Color require CNA format-preserving creation/upload;
DXT payloads must not be decoded or relabeled silently. The verified Model fixture uses
VertexDeclaration, vertex/index buffers, and BasicEffect. EffectMaterialReader and additional
compiled-model dependency readers remain asset-family gaps, so do not claim arbitrary Model XNB
compatibility.

The next coherent XNB task is a complete managed XNA LZX framing implementation with valid,
truncated, bad-length, wrong-size, multi-block, and post-decompression reader-failure fixtures.

## Native evidence

```text
starting bound functions=338
current bound functions=399
HEADER_ABI=0.7.0
MANIFEST_JNI_BINDING_CHECK=PASS
LAYOUT_SIGNATURE_PROBE=PASS
LIBRARY_ABI=0.7.0
LIBRARY_SYMBOL_CHECK=PASS (399/399)
```

The 61 new functions cover EffectMaterial, four stock effects, OcclusionQuery, and SpriteFont
construction. Read-only CNA HEAD remains `1bb2145d99ed572dd4eb15009c34e2e5f410fcf0`; its
networking-off missing GamerServices detail header and networking-on renderer 49/50 blockers are
unchanged. Use the compatible ABI-0.7 library:

```text
/tmp/cna-java-native-working-070/modules/c-api/libcna_c_api.so
```

Do not modify CNA or silently accept a different ABI.

## Behavior, ownership, and verification evidence

The normalized text corpus remains 117 observations (94 math/geometry, 23 input); no new XNA, FNA,
or MonoGame corpus run was performed. The graphics milestone instead added deterministic native and
managed tests. Final test evidence is 109 tests, 0 failures, 0 errors, 0 skipped on Linux x86-64
HEADLESS/NULL audio.

Existing stress remains green: 25 Game cycles, 200 Texture2D/SpriteBatch cycles, 25 bound-buffer
cycles, 150 draw calls, and the isolated live-graph JVM shutdown test. New tests cover stock-effect
clone/child/double-close/wrong-device paths and content-loaded Texture2D/SpriteFont/Model cache,
partial-failure, reverse unload, and parent-child lifetimes. No crash or observed use-after-free
occurred. No sanitizer run was performed.

Verified commands/evidence:

```text
./gradlew --no-daemon check
./gradlew --no-daemon apiCompatReport
./gradlew --no-daemon javadoc sourcesJar
native ABI/library export check: 399/399
scripts/verify-template.sh
template: 60 frames
template: 600 frames
```

`apiCompatCheck` was run and exits 1 only on the 60 missing types.

The sibling template consumes a freshly published temporary artifact, retains raw PNG loading, and
separately loads/draws a deterministic 135-byte Texture2D XNB through ContentManager. It does not
exercise SpriteFont, 3D, or Model.

## Status ledger

Use these meanings literally: strict completeness is API shape; managed verification is Java
behavior; CNA verification executes the native route.

| Family/type | Status |
|---|---|
| AlphaTestEffect | strict complete; managed behavior verified; CNA native verified |
| DualTextureEffect | strict complete; managed behavior verified; CNA native verified |
| EnvironmentMapEffect | strict complete; managed behavior verified; CNA native verified |
| SkinnedEffect | strict complete; managed behavior verified; CNA native verified |
| EffectMaterial | strict complete; CNA native verified |
| OcclusionQuery | strict complete; managed behavior verified; CNA native verified |
| Texture2D XNB | managed behavior verified; CNA native verified (Color only) |
| SpriteFont XNB | managed behavior verified; CNA native verified (Color atlas) |
| Model public graph | strict complete; managed behavior verified; CNA native verified |
| Model XNB | managed behavior verified; CNA native verified for the documented reader graph |
| LZX compressed XNB | not attempted |
| Audio/XACT | not attempted |
| Media/Video | not attempted |
| Storage | not attempted |

## Recommended next work

1. Complete LZX without disturbing uncompressed graphics XNB.
2. Implement all 19 Audio/XACT types as a dependency-coherent resource graph. Start with
   SoundEffect helpers/resources/instances and reuse FrameworkDispatcher; do not land enum-only
   progress.
3. Move to Media/Video only after Audio ownership and callbacks are stable, then Storage.
4. Leave Design converters and GamerServices to the end.

Preserve `MISSING_MEMBER=0`, every zero mismatch category, the empty allowlist, exact ABI checks,
and reverse-order content ownership at every handoff.
