package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Color;
import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

/**
 * Everything a glTF surface is, in one value.
 *
 * <p>A CNA extension, and the fuller sibling of {@link PbrMaterial}: that one is CNA's original
 * five-map material, and this is the one {@link PbrEffect} actually carries -- seven maps, each
 * with its own texture-coordinate set and {@link TextureTransform}, plus an index of refraction,
 * {@code KHR_materials_specular}'s two factors, an explicit {@link AlphaMode} and the sRGB
 * questions a colour map has to answer. They are separate CNA structures, not two names for one,
 * which is why they are separate here.
 *
 * <p>A value rather than a handle: it owns nothing, {@link PbrEffect#applyMaterial} copies it whole
 * into an effect and {@link PbrEffect#extractMaterial()} copies it back out. The seven textures are
 * BORROWED and retained here only so a material that names them keeps them alive; applying it makes
 * the effect retain them too.
 *
 * <p>Mutable, like CNA's own POD and like the other settings values in this package: a material
 * with two dozen fields built by two dozen wither calls would be worse to use and no safer.
 * {@link #equals} and {@link #hashCode} are CNA's own answers, so two materials are equal when they
 * name the same textures rather than equal ones.
 */
public final class PbrMaterialExt {

    private static final int BYTE_LEAVES = 3;
    private static final int INTEGRAL_LEAVES = 24;
    private static final int FLOATING_LEAVES = 48;
    private static final int ALBEDO_COLOR_AT = 7;
    private static final int ALPHA_MODE_AT = 11;
    private static final int COORDINATE_SETS_AT = 17;
    private static final int TRANSFORMS_AT = 13;
    private static final int RESULT_BUFFER_TOO_SMALL = 14;

    private final Texture2D[] textures = new Texture2D[PbrTextureSlot.COUNT];
    private final int[] coordinateSets = new int[PbrTextureSlot.COUNT];
    private final TextureTransform[] transforms = new TextureTransform[PbrTextureSlot.COUNT];

    private Color albedoColor = Color.White;
    private Vector3 emissiveFactor = new Vector3(0f, 0f, 0f);
    private Vector3 specularColorFactor = new Vector3(1f, 1f, 1f);
    private float metallicFactor = 1f;
    private float roughnessFactor = 1f;
    private float normalScale = 1f;
    private float occlusionStrength = 1f;
    private float ior = 1.5f;
    private float specularFactor = 1f;
    private float alphaCutoff = 0.5f;
    private AlphaMode alphaMode = AlphaMode.Opaque;
    private boolean doubleSided;
    private boolean baseColorTextureSrgb;
    private boolean emissiveTextureSrgb;
    private boolean specularColorTextureSrgb;
    private boolean outputEncodedToSrgb;

    /**
     * Creates a material with the values a fresh PBR effect carries.
     *
     * <p>CNA has no defaults route for this structure, so these are written out rather than read.
     * They are not a guess: the tests compare a new material against what
     * {@link PbrEffect#extractMaterial()} answers on a new effect, so CNA is still what decides
     * whether they are right.
     */
    public PbrMaterialExt() {
        Arrays.fill(transforms, TextureTransform.identity());
    }

    /**
     * Copies another material.
     *
     * @param value the material to copy
     */
    public PbrMaterialExt(PbrMaterialExt value) {
        Objects.requireNonNull(value, "value");
        System.arraycopy(value.textures, 0, textures, 0, PbrTextureSlot.COUNT);
        System.arraycopy(value.coordinateSets, 0, coordinateSets, 0, PbrTextureSlot.COUNT);
        System.arraycopy(value.transforms, 0, transforms, 0, PbrTextureSlot.COUNT);
        albedoColor = value.albedoColor;
        emissiveFactor = new Vector3(value.emissiveFactor);
        specularColorFactor = new Vector3(value.specularColorFactor);
        metallicFactor = value.metallicFactor;
        roughnessFactor = value.roughnessFactor;
        normalScale = value.normalScale;
        occlusionStrength = value.occlusionStrength;
        ior = value.ior;
        specularFactor = value.specularFactor;
        alphaCutoff = value.alphaCutoff;
        alphaMode = value.alphaMode;
        doubleSided = value.doubleSided;
        baseColorTextureSrgb = value.baseColorTextureSrgb;
        emissiveTextureSrgb = value.emissiveTextureSrgb;
        specularColorTextureSrgb = value.specularColorTextureSrgb;
        outputEncodedToSrgb = value.outputEncodedToSrgb;
    }

    /**
     * Returns the texture in one slot.
     *
     * @param slot the slot
     * @return the texture, or {@code null}
     */
    public Texture2D getTexture(PbrTextureSlot slot) {
        return textures[Objects.requireNonNull(slot, "slot").ordinal()];
    }

    /**
     * Puts a texture in one slot, borrowing it.
     *
     * @param slot the slot
     * @param texture the texture, or {@code null} to clear the slot
     */
    public void setTexture(PbrTextureSlot slot, Texture2D texture) {
        textures[Objects.requireNonNull(slot, "slot").ordinal()] = texture;
    }

    /**
     * Returns which set of texture coordinates one slot samples with.
     *
     * @param slot the slot
     * @return the coordinate set index
     */
    public int getTextureCoordinateSet(PbrTextureSlot slot) {
        return coordinateSets[Objects.requireNonNull(slot, "slot").ordinal()];
    }

    /**
     * Sets which set of texture coordinates one slot samples with.
     *
     * @param slot the slot
     * @param value the coordinate set index
     */
    public void setTextureCoordinateSet(PbrTextureSlot slot, int value) {
        coordinateSets[Objects.requireNonNull(slot, "slot").ordinal()] = value;
    }

    /**
     * Returns how one slot's texture is placed on the surface.
     *
     * @param slot the slot
     * @return the transform
     */
    public TextureTransform getTextureTransform(PbrTextureSlot slot) {
        return transforms[Objects.requireNonNull(slot, "slot").ordinal()];
    }

    /**
     * Sets how one slot's texture is placed on the surface.
     *
     * @param slot the slot
     * @param value the transform
     */
    public void setTextureTransform(PbrTextureSlot slot, TextureTransform value) {
        transforms[Objects.requireNonNull(slot, "slot").ordinal()] =
                Objects.requireNonNull(value, "value");
    }

    /** @return the base colour */
    public Color getAlbedoColor() {
        return albedoColor;
    }

    /** @param value the base colour */
    public void setAlbedoColor(Color value) {
        albedoColor = Objects.requireNonNull(value, "value");
    }

    /** @return the linear emissive colour */
    public Vector3 getEmissiveFactor() {
        return new Vector3(emissiveFactor);
    }

    /** @param value the linear emissive colour */
    public void setEmissiveFactor(Vector3 value) {
        emissiveFactor = new Vector3(Objects.requireNonNull(value, "value"));
    }

    /** @return {@code KHR_materials_specular}'s colour factor */
    public Vector3 getSpecularColorFactor() {
        return new Vector3(specularColorFactor);
    }

    /** @param value the specular colour factor */
    public void setSpecularColorFactor(Vector3 value) {
        specularColorFactor = new Vector3(Objects.requireNonNull(value, "value"));
    }

    /** @return how metallic the surface is, from zero to one */
    public float getMetallicFactor() {
        return metallicFactor;
    }

    /** @param value how metallic the surface is */
    public void setMetallicFactor(float value) {
        metallicFactor = value;
    }

    /** @return how rough the surface is, from zero to one */
    public float getRoughnessFactor() {
        return roughnessFactor;
    }

    /** @param value how rough the surface is */
    public void setRoughnessFactor(float value) {
        roughnessFactor = value;
    }

    /** @return how strongly the normal map is applied */
    public float getNormalScale() {
        return normalScale;
    }

    /** @param value how strongly the normal map is applied */
    public void setNormalScale(float value) {
        normalScale = value;
    }

    /** @return how strongly the occlusion map is applied */
    public float getOcclusionStrength() {
        return occlusionStrength;
    }

    /** @param value how strongly the occlusion map is applied */
    public void setOcclusionStrength(float value) {
        occlusionStrength = value;
    }

    /** @return the index of refraction */
    public float getIor() {
        return ior;
    }

    /** @param value the index of refraction */
    public void setIor(float value) {
        ior = value;
    }

    /** @return {@code KHR_materials_specular}'s scalar strength */
    public float getSpecularFactor() {
        return specularFactor;
    }

    /** @param value the specular strength */
    public void setSpecularFactor(float value) {
        specularFactor = value;
    }

    /** @return the alpha threshold, meaningful only in {@link AlphaMode#Mask} */
    public float getAlphaCutoff() {
        return alphaCutoff;
    }

    /** @param value the alpha threshold */
    public void setAlphaCutoff(float value) {
        alphaCutoff = value;
    }

    /** @return how the material's alpha is interpreted */
    public AlphaMode getAlphaMode() {
        return alphaMode;
    }

    /** @param value how the material's alpha is interpreted */
    public void setAlphaMode(AlphaMode value) {
        alphaMode = Objects.requireNonNull(value, "value");
    }

    /** @return whether both faces are drawn */
    public boolean isDoubleSided() {
        return doubleSided;
    }

    /** @param value whether both faces are drawn */
    public void setDoubleSided(boolean value) {
        doubleSided = value;
    }

    /** @return whether the base-colour texture is sRGB encoded */
    public boolean isBaseColorTextureSrgb() {
        return baseColorTextureSrgb;
    }

    /** @param value whether the base-colour texture is sRGB encoded */
    public void setBaseColorTextureSrgb(boolean value) {
        baseColorTextureSrgb = value;
    }

    /** @return whether the emissive texture is sRGB encoded */
    public boolean isEmissiveTextureSrgb() {
        return emissiveTextureSrgb;
    }

    /** @param value whether the emissive texture is sRGB encoded */
    public void setEmissiveTextureSrgb(boolean value) {
        emissiveTextureSrgb = value;
    }

    /** @return whether the specular colour texture is sRGB encoded */
    public boolean isSpecularColorTextureSrgb() {
        return specularColorTextureSrgb;
    }

    /** @param value whether the specular colour texture is sRGB encoded */
    public void setSpecularColorTextureSrgb(boolean value) {
        specularColorTextureSrgb = value;
    }

    /** @return whether the shader encodes its output to sRGB */
    public boolean isOutputEncodedToSrgb() {
        return outputEncodedToSrgb;
    }

    /** @param value whether the shader encodes its output to sRGB */
    public void setOutputEncodedToSrgb(boolean value) {
        outputEncodedToSrgb = value;
    }

    /**
     * Applies the device state this material implies -- blending, depth write and culling.
     *
     * <p>The part of a material that is not a shader parameter: a blended material needs alpha
     * blending on and depth writes off, and a double-sided one needs culling off. Having CNA do it
     * from the material is what stops a transparent surface being drawn as an opaque one that
     * happens to have an alpha channel.
     *
     * @param graphicsDevice the device to set state on
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public void applyState(GraphicsDevice graphicsDevice) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        GraphicsExtension.check("PbrMaterialExt.applyState",
                NativeEngineLayerRoutes.pbrMaterialApplyState(bytes(), integral(), floating(),
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice)));
    }

    /**
     * Compares two materials by value across every field, as CNA does.
     *
     * <p>CNA's own rule, asked of CNA rather than reimplemented here: textures compare by handle
     * identity, so two materials are equal when they name the same textures and not merely equal
     * ones. Reimplementing it field by field in Java would make equality answerable in a build
     * where the hash consistent with it is not.
     */
    @Override
    public boolean equals(Object value) {
        if (this == value) {
            return true;
        }
        if (!(value instanceof PbrMaterialExt other)) {
            return false;
        }
        GraphicsExtension.requireBackend();
        boolean[] equal = new boolean[1];
        GraphicsExtension.check("PbrMaterialExt.equals",
                NativeEngineLayerRoutes.pbrMaterialExtEquals(bytes(), integral(), floating(),
                        other.bytes(), other.integral(), other.floating(), equal));
        return equal[0];
    }

    /**
     * Returns CNA's own hash code, which is consistent with {@link #equals}.
     *
     * <p>Narrowed from CNA's {@code uint64_t} to Java's {@code int} by folding the halves, which
     * keeps equal materials hashing equally -- the only property a hash owes.
     */
    @Override
    public int hashCode() {
        GraphicsExtension.requireBackend();
        long[] hash = new long[1];
        GraphicsExtension.check("PbrMaterialExt.hashCode",
                NativeEngineLayerRoutes.pbrMaterialExtGetHashCode(bytes(), integral(), floating(),
                        hash));
        return (int) (hash[0] ^ (hash[0] >>> 32));
    }

    /**
     * Returns CNA's own description of the material.
     *
     * @return the text
     */
    @Override
    public String toString() {
        GraphicsExtension.requireBackend();
        long[] bytes = new long[1];
        int probe = NativeEngineLayerRoutes.pbrMaterialExtCopyToString(bytes(), integral(),
                floating(), new byte[0], bytes);
        if (probe != RESULT_BUFFER_TOO_SMALL) {
            GraphicsExtension.check("PbrMaterialExt.toString", probe);
        }
        int length = Math.toIntExact(bytes[0]);
        if (length == 0) {
            return "";
        }
        byte[] destination = new byte[length];
        GraphicsExtension.check("PbrMaterialExt.toString",
                NativeEngineLayerRoutes.pbrMaterialExtCopyToString(bytes(), integral(), floating(),
                        destination, bytes));
        return new String(destination, 0, Math.toIntExact(bytes[0]), StandardCharsets.UTF_8);
    }

    /** Rebuilds a material from CNA's flat leaves, keeping the textures a caller already has. */
    static PbrMaterialExt fromLeaves(long[] integral, float[] floating, Texture2D[] retained) {
        PbrMaterialExt material = new PbrMaterialExt();
        System.arraycopy(retained, 0, material.textures, 0, PbrTextureSlot.COUNT);
        material.albedoColor = new Color((int) integral[ALBEDO_COLOR_AT],
                (int) integral[ALBEDO_COLOR_AT + 1], (int) integral[ALBEDO_COLOR_AT + 2],
                (int) integral[ALBEDO_COLOR_AT + 3]);
        material.alphaMode = AlphaMode.fromValue(integral[ALPHA_MODE_AT]);
        material.doubleSided = integral[ALPHA_MODE_AT + 1] != 0L;
        material.baseColorTextureSrgb = integral[ALPHA_MODE_AT + 2] != 0L;
        material.emissiveTextureSrgb = integral[ALPHA_MODE_AT + 3] != 0L;
        material.specularColorTextureSrgb = integral[ALPHA_MODE_AT + 4] != 0L;
        material.outputEncodedToSrgb = integral[ALPHA_MODE_AT + 5] != 0L;
        for (int slot = 0; slot < PbrTextureSlot.COUNT; slot++) {
            material.coordinateSets[slot] = Math.toIntExact(integral[COORDINATE_SETS_AT + slot]);
            material.transforms[slot] = TextureTransform.fromLeaves(floating,
                    TRANSFORMS_AT + slot * TextureTransform.LEAVES);
        }
        material.emissiveFactor = new Vector3(floating[0], floating[1], floating[2]);
        material.specularColorFactor = new Vector3(floating[3], floating[4], floating[5]);
        material.metallicFactor = floating[6];
        material.roughnessFactor = floating[7];
        material.normalScale = floating[8];
        material.occlusionStrength = floating[9];
        material.ior = floating[10];
        material.specularFactor = floating[11];
        material.alphaCutoff = floating[12];
        return material;
    }

    /** The textures this material holds, for a reader that has to retain them too. */
    Texture2D[] retainedTextures() {
        return textures.clone();
    }

    /** The byte leaves CNA's structure declares, which are padding. */
    byte[] bytes() {
        return new byte[BYTE_LEAVES];
    }

    /** The integral leaves CNA's structure declares, in declaration order. */
    long[] integral() {
        long[] leaves = new long[INTEGRAL_LEAVES];
        for (int slot = 0; slot < PbrTextureSlot.COUNT; slot++) {
            leaves[slot] = textures[slot] == null ? 0L
                    : NativeBindings.nativeResourceHandle(textures[slot]);
            leaves[COORDINATE_SETS_AT + slot] = coordinateSets[slot];
        }
        leaves[ALBEDO_COLOR_AT] = albedoColor.getR();
        leaves[ALBEDO_COLOR_AT + 1] = albedoColor.getG();
        leaves[ALBEDO_COLOR_AT + 2] = albedoColor.getB();
        leaves[ALBEDO_COLOR_AT + 3] = albedoColor.getA();
        leaves[ALPHA_MODE_AT] = alphaMode.ordinal();
        leaves[ALPHA_MODE_AT + 1] = doubleSided ? 1L : 0L;
        leaves[ALPHA_MODE_AT + 2] = baseColorTextureSrgb ? 1L : 0L;
        leaves[ALPHA_MODE_AT + 3] = emissiveTextureSrgb ? 1L : 0L;
        leaves[ALPHA_MODE_AT + 4] = specularColorTextureSrgb ? 1L : 0L;
        leaves[ALPHA_MODE_AT + 5] = outputEncodedToSrgb ? 1L : 0L;
        return leaves;
    }

    /** The floating leaves CNA's structure declares, in declaration order. */
    float[] floating() {
        float[] leaves = new float[FLOATING_LEAVES];
        leaves[0] = emissiveFactor.X;
        leaves[1] = emissiveFactor.Y;
        leaves[2] = emissiveFactor.Z;
        leaves[3] = specularColorFactor.X;
        leaves[4] = specularColorFactor.Y;
        leaves[5] = specularColorFactor.Z;
        leaves[6] = metallicFactor;
        leaves[7] = roughnessFactor;
        leaves[8] = normalScale;
        leaves[9] = occlusionStrength;
        leaves[10] = ior;
        leaves[11] = specularFactor;
        leaves[12] = alphaCutoff;
        for (int slot = 0; slot < PbrTextureSlot.COUNT; slot++) {
            transforms[slot].writeTo(leaves, TRANSFORMS_AT + slot * TextureTransform.LEAVES);
        }
        return leaves;
    }
}
