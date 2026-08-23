# CNA-Java measured engineering plan

**Status:** Graphics type-complete; member-complete, structurally strict XNA 4.0 Java projection with 60 dependency-group types remaining

**Updated:** 2026-08-23

**Selected profile:** XNA 4.0 Windows runtime projected to Java 17

**Runtime-qualified platform:** Linux x86-64, CNA HEADLESS renderer, NULL audio

## Authority and invariants

Microsoft XNA 4.0 metadata and IL remain authoritative. CNA-C# is an engineering reference and
FNA/MonoGame are comparison implementations. CNA's ABI is the native implementation boundary; it
does not define the public Java contract.

This milestone did not weaken a mapping rule, add an allowlist, expose an internal/JNI type, or add
a partial public XNA type. `MISSING_MEMBER=0` remains a hard invariant. The new expected Java
members are reviewed inherited `ReadOnlyCollection<T>` operations and Java iterator bridges for
the Model collections; these are deterministic mapping obligations, not verifier exceptions.

## Exact milestone measurement

The run began at the user's hard baseline and finished against the same hash-pinned seven XNA
reference assemblies:

| Metric | Before | Current | Change |
|---|---:|---:|---:|
| REFERENCE_TYPES | 257 | 257 | 0 |
| REFERENCE_MEMBERS | 2,964 | 2,964 | 0 |
| EXPECTED_JAVA_TYPES | 265 | 265 | 0 |
| EXPECTED_JAVA_MEMBERS | 3,182 | 3,200 | +18 |
| TARGET_TYPES | 184 | 205 | +21 |
| TARGET_MEMBERS | 2,492 | 2,730 | +238 |
| TOTAL_DIAGNOSTICS | 81 | 60 | -21 |
| MISSING_TYPE | 81 | 60 | -21 |
| MISSING_MEMBER | 0 | 0 | 0 |
| CNA_INTERNAL_LEAK | 0 | 0 | 0 |
| ALLOWLIST_ENTRIES | 0 | 0 | 0 |

Every strict mismatch category is exactly zero:

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

`apiCompatReport` succeeds with exactly 60 `MISSING_TYPE` diagnostics. `apiCompatCheck` exits 1
solely because those 60 complete types are absent.

## Graphics result

Graphics moved from 21 missing types to zero. All added types are complete against their mapped
public/protected XNA contracts.

The four remaining stock effects are real CNA effects. Constructors, default state, device
association, stable current-technique/pass views, pass application, clone, disposal, idempotent
double close, disposed use, and foreign-device texture rejection are covered. Texture and cube-map
references retain stable Java identity. EnvironmentMapEffect and SkinnedEffect expose three stable
DirectionalLight views. The CNA implementation cannot disable lighting on those two effects, so a
false setter is rejected explicitly instead of fabricating renderer state. SkinnedEffect enforces
`MaxBones=72`, copies caller transforms, copies results into the caller array, validates array
length/count, and accepts only 1, 2, or 4 weights per vertex.

`EffectMaterial` creates a real same-device CNA effect-material clone. `OcclusionQuery` uses CNA's
real create/begin/end/completion/pixel-count route; query results are not fabricated. The three XNA
graphics exceptions preserve their exact mapped inheritance and public constructor set.

The complete Model surface is present: Model, ModelBone, ModelMesh, ModelMeshPart, the four
collections, ModelEffectCollection, and all four enumerators. Collections are read-only
`AbstractList` facades, use stable repeated identity, preserve source order, and implement the
reviewed iterator bridge. Bone parent/child/root relationships, mesh parent bones, mesh-part
buffers/effects/draw metadata, effect deduplication, name/index lookup, absolute bone transforms,
and real indexed Model.Draw are tested. Model is an owned content graph but is not made disposable,
matching XNA; native buffers/effects retain their existing resource ownership rather than being
rewrapped as duplicate owners.

## Managed graphics XNB result

The reader table now activates internal readers for Rectangle and selected List families,
Texture2D, SpriteFont, VertexDeclaration, VertexBuffer, IndexBuffer, Effect, BasicEffect, and Model.
No public reader types were invented and no asset name is special-cased.

Texture2DReader parses SurfaceFormat, dimensions, mip count, declared payload lengths, and every
mip payload. It accepts one level or the exact complete mip chain, verifies byte counts, uploads
through the existing CNA texture create/data route, and closes a created texture if later parsing
fails. Format status is explicit:

- `SurfaceFormat.Color`: supported through the existing CNA route, including multiple mips;
- every other selected SurfaceFormat: requires exact CNA surface-format creation/upload support;
- DXT1/DXT3/DXT5 additionally require compressed-format preservation (no managed RGBA
  reinterpretation or fake decode is used);
- unsupported formats fail deterministically before native resource creation.

Generated fixtures cover a one-level Color texture, multiple mips, invalid dimensions, bad mip
count, truncated data, payload mismatch, unsupported format, wrong requested type, cache identity,
Unload, Dispose, and partial failure after native creation. ContentManager owns managed-reader
resources, records a shared native resource once, unloads in reverse creation order, and preserves
cache identity.

SpriteFontReader parses the atlas, glyph/cropping rectangles, character map, line spacing, spacing,
kerning vectors, and optional default character. A new CNA `cna_sprite_font_create` route builds a
real font from a managed uncompressed Color atlas. Tests load through ContentManager, measure,
exercise default and missing-glyph behavior, draw through SpriteBatch, and verify that Unload first
destroys the font then its atlas.

ModelReader deserializes the normal XNA reader table and shared-resource fixups. Its verified
synthetic model contains a root bone, one mesh/part, exact raw vertex bytes, 16-bit indices, and a
shared BasicEffect; it loads through `ContentManager.Load(Model.class, ...)`, preserves identity and
buffer contents, executes real indexed Model.Draw on HEADLESS, and invalidates the owned resources
on Unload. EffectMaterialReader and other reader families not required by this fixture remain
explicitly unavailable; the Model path is not claimed for arbitrary compiled assets.

LZX-compressed XNB was not attempted. The uncompressed graphics path remains separate and stable;
compressed framing still fails explicitly.

## Behavior corpus

The normalized deterministic text corpus remains 117 observations: 94 math/geometry and 23 input.
No new text group was added. Stock effects and graphics XNB gained deterministic unit/native
assertions instead; their native object identities and cleanup events are not forced into a
cross-runtime text format. No XNA Windows, FNA, or MonoGame behavior probe was executed in this
milestone, so no new comparator result is claimed. Existing XNA-derived corpus expectations were
not changed.

## JNI and native ABI

Bound functions grew from 338 to 399. The 61 reviewed additions cover EffectMaterial, all four
stock effects, OcclusionQuery, and SpriteFont construction. Managed Texture2D/Model XNB parsing and
existing buffer transfers required no new generic loader route.

```text
HEADER_ABI=0.7.0
BOUND_FUNCTIONS=399
MANIFEST_JNI_BINDING_CHECK=PASS
LAYOUT_SIGNATURE_PROBE=PASS
LIBRARY_ABI=0.7.0
LIBRARY_SYMBOL_CHECK=PASS (399/399)
```

JNI compiles as C11 with `-Wall -Wextra -Werror`; the canonical headers, manifest, generated layout
probe, JNI declarations, and loaded library exports agree.

The read-only CNA HEAD was checked exactly once and remains
`1bb2145d99ed572dd4eb15009c34e2e5f410fcf0`. Networking-off still fails on the absent GamerServices
detail header and networking-on still reports renderer inventory 49 against canonical 50. No CNA
source was changed. Runtime evidence therefore uses the compatible ABI-0.7 library at
`/tmp/cna-java-native-working-070/modules/c-api/libcna_c_api.so`.

## Ownership, stress, and runtime evidence

All 109 Java tests passed with no failures, errors, or skips under HEADLESS. The existing stress
coverage remains green: 25 Game cycles, 200 Texture2D/SpriteBatch cycles, 25 bound-buffer cycles,
and 150 draw calls. New tests add stock-effect parent/child views and clones, wrong-device texture
paths, idempotent close and use-after-close, a real OcclusionQuery, pure and native Model graphs,
and content-loaded Texture2D/SpriteFont/Model load/cache/failure/Unload lifetimes.

The existing isolated JVM shutdown case remains green. No native crash or observed use-after-free
occurred. No sanitizer-built compatible CNA library was available, so allocator-level leak freedom
is not claimed. The Java guard around CNA's bound vertex/index raw-pointer lifetime defect remains
unchanged; tests explicitly unbind loaded Model buffers before content teardown.

## Verification and template

The following passed against the final source and compatible native library:

- `./gradlew --no-daemon check` (using the pinned Gradle distribution and populated offline cache);
- `./gradlew --no-daemon apiCompatReport`;
- `./gradlew --no-daemon javadoc sourcesJar`;
- native header/manifest/layout/export verification, 399/399;
- `scripts/verify-template.sh` with a fresh temporary Maven repository;
- sibling template and generated standalone consumer build/tests;
- 60-frame smoke and 600-frame stability runs on Linux x86-64 HEADLESS/NULL audio.

The template retains its raw PNG `Texture2D.FromStream` path and separately decodes a deterministic
135-byte Windows XNB fixture, loads it through `ContentManager.Load(Texture2D.class, ...)`, verifies
cache identity, draws both textures, and unloads the XNB resource through ContentManager. It does
not exercise SpriteFont, 3D, or Model; real Model.Draw evidence lives in the binding integration
test rather than being presented as a template showcase.

## Remaining dependency groups

The 60 remaining types are exactly:

```text
Graphics=0
Audio/XACT=19
Media/Video=24
Storage=3
Design=13
GamerServices=1
```

There are no missing Content, Touch, ordinary non-Design Framework/core, or Graphics types.

Continue in coherent groups:

1. implement and test LZX framing without changing the stable uncompressed reader path;
2. implement Audio/XACT as a complete ownership graph, starting with SoundEffect and its instances
   rather than isolated enums;
3. implement Media/Video after Audio, then Storage;
4. finish Design converters and GamerServices last.

At every checkpoint preserve `MISSING_MEMBER=0`, all strict mismatch categories at zero, the empty
allowlist, and the bound-buffer guard.
