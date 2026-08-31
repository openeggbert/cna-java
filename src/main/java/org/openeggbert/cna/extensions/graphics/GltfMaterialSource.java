package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.Texture2D;
import Microsoft.Xna.Framework.Vector3;
import Microsoft.Xna.Framework.Vector4;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.Arrays;
import java.util.Objects;

/**
 * One glTF material as the file declares it, before it becomes something a renderer can use.
 *
 * <p>A CNA extension, and the input half of the glTF bridge: a loader fills this in with the
 * numbers straight out of the JSON -- {@code pbrMetallicRoughness}, {@code emissiveFactor},
 * {@code KHR_materials_ior}, {@code KHR_materials_specular}, {@code KHR_texture_transform} -- and
 * {@link #build()} turns it into the {@link PbrMaterialExt} a {@link PbrEffect} draws with.
 *
 * <p>The point of the separation is that these are two different things. A glTF material is a
 * description in a file: its base colour is four floats, its textures are indices into the
 * document. A CNA material is what a shader binds: its base colour is a {@code Color} and its
 * textures are real {@code Texture2D} objects a loader has already resolved. The bridge is the one
 * place that conversion happens, so every loader gets the same answer.
 *
 * <p><strong>One value is not carried exactly, and that is CNA's behaviour rather than a limit of
 * this binding:</strong> glTF's {@code baseColorFactor} is four floats and a material's albedo is
 * eight bits a channel, so it is quantised. Everything else round-trips, and the tests say so.
 *
 * <p>The textures are BORROWED: the built material records them, and whoever loaded them still
 * owns them.
 */
public final class GltfMaterialSource {

    private static final int BYTE_LEAVES = 3;
    private static final int INTEGRAL_LEAVES = 9;
    private static final int FLOATING_LEAVES = 52;
    private static final int COORDINATE_SETS_AT = 2;
    private static final int TRANSFORMS_AT = 17;

    private final Texture2D[] textures = new Texture2D[PbrTextureSlot.COUNT];
    private final int[] coordinateSets = new int[PbrTextureSlot.COUNT];
    private final TextureTransform[] transforms = new TextureTransform[PbrTextureSlot.COUNT];

    private Vector4 baseColorFactor;
    private Vector3 emissiveFactor;
    private Vector3 specularColorFactor;
    private float metallicFactor;
    private float roughnessFactor;
    private float normalScale;
    private float occlusionStrength;
    private float ior;
    private float specularFactor;
    private float alphaCutoff;
    private AlphaMode alphaMode;
    private boolean doubleSided;

    /**
     * Creates a source carrying the glTF specification's own default factors.
     *
     * <p>Read from CNA rather than restated here, which matters more than usual: these are the
     * values a material takes for every field the file leaves out, so a wrong default is a
     * material that is quietly not what the file says.
     *
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public GltfMaterialSource() {
        GraphicsExtension.requireBackend();
        long[] integral = new long[INTEGRAL_LEAVES];
        float[] floating = new float[FLOATING_LEAVES];
        GraphicsExtension.check("GltfMaterialSource",
                NativeEngineLayerRoutes.gltfMaterialSourceExtInit(new byte[BYTE_LEAVES], integral,
                        floating));
        baseColorFactor = new Vector4(floating[0], floating[1], floating[2], floating[3]);
        metallicFactor = floating[4];
        roughnessFactor = floating[5];
        emissiveFactor = new Vector3(floating[6], floating[7], floating[8]);
        normalScale = floating[9];
        occlusionStrength = floating[10];
        ior = floating[11];
        specularFactor = floating[12];
        specularColorFactor = new Vector3(floating[13], floating[14], floating[15]);
        alphaCutoff = floating[16];
        alphaMode = AlphaMode.fromValue(integral[0]);
        doubleSided = integral[1] != 0L;
        for (int slot = 0; slot < PbrTextureSlot.COUNT; slot++) {
            coordinateSets[slot] = Math.toIntExact(integral[COORDINATE_SETS_AT + slot]);
            transforms[slot] = TextureTransform.fromLeaves(floating,
                    TRANSFORMS_AT + slot * TextureTransform.LEAVES);
        }
    }

    /**
     * Builds the material a renderer can draw with.
     *
     * @return the material
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public PbrMaterialExt build() {
        byte[] bytes = new byte[3];
        long[] integral = new long[24];
        float[] floating = new float[48];
        GraphicsExtension.check("GltfMaterialSource.build",
                NativeEngineLayerRoutes.gltfMaterialBridgeBuildMaterial(bytes(), integral(),
                        floating(), textureHandles(), bytes, integral, floating));
        return PbrMaterialExt.fromLeaves(integral, floating, textures.clone());
    }

    /**
     * Returns the texture one slot resolved to.
     *
     * @param slot the slot
     * @return the texture, or {@code null}
     */
    public Texture2D getTexture(PbrTextureSlot slot) {
        return textures[Objects.requireNonNull(slot, "slot").ordinal()];
    }

    /**
     * Records the texture one slot resolved to, borrowing it.
     *
     * @param slot the slot
     * @param texture the texture, or {@code null} for a slot the file does not name
     */
    public void setTexture(PbrTextureSlot slot, Texture2D texture) {
        textures[Objects.requireNonNull(slot, "slot").ordinal()] = texture;
    }

    /**
     * Returns {@code KHR_texture_transform}'s coordinate set for one slot.
     *
     * @param slot the slot
     * @return the coordinate set index
     */
    public int getTextureCoordinateSet(PbrTextureSlot slot) {
        return coordinateSets[Objects.requireNonNull(slot, "slot").ordinal()];
    }

    /**
     * Sets {@code KHR_texture_transform}'s coordinate set for one slot.
     *
     * @param slot the slot
     * @param value the coordinate set index
     */
    public void setTextureCoordinateSet(PbrTextureSlot slot, int value) {
        coordinateSets[Objects.requireNonNull(slot, "slot").ordinal()] = value;
    }

    /**
     * Returns {@code KHR_texture_transform}'s transform for one slot.
     *
     * @param slot the slot
     * @return the transform
     */
    public TextureTransform getTextureTransform(PbrTextureSlot slot) {
        return transforms[Objects.requireNonNull(slot, "slot").ordinal()];
    }

    /**
     * Sets {@code KHR_texture_transform}'s transform for one slot.
     *
     * @param slot the slot
     * @param value the transform
     */
    public void setTextureTransform(PbrTextureSlot slot, TextureTransform value) {
        transforms[Objects.requireNonNull(slot, "slot").ordinal()] =
                Objects.requireNonNull(value, "value");
    }

    /** @return {@code pbrMetallicRoughness.baseColorFactor} */
    public Vector4 getBaseColorFactor() {
        return new Vector4(baseColorFactor);
    }

    /** @param value the base colour factor */
    public void setBaseColorFactor(Vector4 value) {
        baseColorFactor = new Vector4(Objects.requireNonNull(value, "value"));
    }

    /** @return {@code emissiveFactor} */
    public Vector3 getEmissiveFactor() {
        return new Vector3(emissiveFactor);
    }

    /** @param value the emissive factor */
    public void setEmissiveFactor(Vector3 value) {
        emissiveFactor = new Vector3(Objects.requireNonNull(value, "value"));
    }

    /** @return {@code KHR_materials_specular.specularColorFactor} */
    public Vector3 getSpecularColorFactor() {
        return new Vector3(specularColorFactor);
    }

    /** @param value the specular colour factor */
    public void setSpecularColorFactor(Vector3 value) {
        specularColorFactor = new Vector3(Objects.requireNonNull(value, "value"));
    }

    /** @return {@code pbrMetallicRoughness.metallicFactor} */
    public float getMetallicFactor() {
        return metallicFactor;
    }

    /** @param value the metallic factor */
    public void setMetallicFactor(float value) {
        metallicFactor = value;
    }

    /** @return {@code pbrMetallicRoughness.roughnessFactor} */
    public float getRoughnessFactor() {
        return roughnessFactor;
    }

    /** @param value the roughness factor */
    public void setRoughnessFactor(float value) {
        roughnessFactor = value;
    }

    /** @return {@code normalTexture.scale} */
    public float getNormalScale() {
        return normalScale;
    }

    /** @param value the normal scale */
    public void setNormalScale(float value) {
        normalScale = value;
    }

    /** @return {@code occlusionTexture.strength} */
    public float getOcclusionStrength() {
        return occlusionStrength;
    }

    /** @param value the occlusion strength */
    public void setOcclusionStrength(float value) {
        occlusionStrength = value;
    }

    /** @return {@code KHR_materials_ior.ior} */
    public float getIor() {
        return ior;
    }

    /** @param value the index of refraction */
    public void setIor(float value) {
        ior = value;
    }

    /** @return {@code KHR_materials_specular.specularFactor} */
    public float getSpecularFactor() {
        return specularFactor;
    }

    /** @param value the specular factor */
    public void setSpecularFactor(float value) {
        specularFactor = value;
    }

    /** @return {@code alphaCutoff} */
    public float getAlphaCutoff() {
        return alphaCutoff;
    }

    /** @param value the alpha cutoff */
    public void setAlphaCutoff(float value) {
        alphaCutoff = value;
    }

    /** @return {@code alphaMode} */
    public AlphaMode getAlphaMode() {
        return alphaMode;
    }

    /** @param value the alpha mode */
    public void setAlphaMode(AlphaMode value) {
        alphaMode = Objects.requireNonNull(value, "value");
    }

    /** @return {@code doubleSided} */
    public boolean isDoubleSided() {
        return doubleSided;
    }

    /** @param value whether the material is double sided */
    public void setDoubleSided(boolean value) {
        doubleSided = value;
    }

    private byte[] bytes() {
        return new byte[BYTE_LEAVES];
    }

    private long[] integral() {
        long[] leaves = new long[INTEGRAL_LEAVES];
        leaves[0] = alphaMode.ordinal();
        leaves[1] = doubleSided ? 1L : 0L;
        for (int slot = 0; slot < PbrTextureSlot.COUNT; slot++) {
            leaves[COORDINATE_SETS_AT + slot] = coordinateSets[slot];
        }
        return leaves;
    }

    private float[] floating() {
        float[] leaves = new float[FLOATING_LEAVES];
        leaves[0] = baseColorFactor.X;
        leaves[1] = baseColorFactor.Y;
        leaves[2] = baseColorFactor.Z;
        leaves[3] = baseColorFactor.W;
        leaves[4] = metallicFactor;
        leaves[5] = roughnessFactor;
        leaves[6] = emissiveFactor.X;
        leaves[7] = emissiveFactor.Y;
        leaves[8] = emissiveFactor.Z;
        leaves[9] = normalScale;
        leaves[10] = occlusionStrength;
        leaves[11] = ior;
        leaves[12] = specularFactor;
        leaves[13] = specularColorFactor.X;
        leaves[14] = specularColorFactor.Y;
        leaves[15] = specularColorFactor.Z;
        leaves[16] = alphaCutoff;
        for (int slot = 0; slot < PbrTextureSlot.COUNT; slot++) {
            transforms[slot].writeTo(leaves, TRANSFORMS_AT + slot * TextureTransform.LEAVES);
        }
        return leaves;
    }

    /**
     * The texture set CNA takes beside the factors, in slot order.
     *
     * <p>{@code cna_gltf_material_textures_ext_init} is deliberately unbound: all it does is fill
     * a structure with invalid handles, and a fresh Java array is already that. Binding it would
     * make the library demand a symbol from libcna_c_api to write the zeroes Java has anyway.
     */
    private long[] textureHandles() {
        long[] handles = new long[PbrTextureSlot.COUNT];
        Arrays.setAll(handles, slot -> textures[slot] == null ? 0L
                : NativeBindings.nativeResourceHandle(textures[slot]));
        return handles;
    }
}
