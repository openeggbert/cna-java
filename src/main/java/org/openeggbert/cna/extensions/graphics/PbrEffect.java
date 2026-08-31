package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Color;
import Microsoft.Xna.Framework.Graphics.Effect;
import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.FacadeFactory;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativePbrEffectRoutes;

import java.util.Objects;

/**
 * A physically-based effect, the way glTF describes a surface.
 *
 * <p>A CNA extension with no XNA counterpart. {@code BasicEffect} lights a surface with a diffuse
 * colour, a specular power and one texture, which is the lighting model of 2004; this one takes
 * base colour, metalness and roughness, an index of refraction, and up to seven maps each with its
 * own coordinate set and placement -- so a glTF asset can be drawn as it was authored rather than
 * approximated.
 *
 * <p>Every value can be set one at a time, or the whole surface at once with a
 * {@link PbrMaterialExt}: {@link #applyMaterial} writes all of it and {@link #extractMaterial()}
 * reads all of it back, which is what makes a material a thing a game can store, compare and
 * hand around.
 *
 * <p><strong>Ownership.</strong> The effect is OWNED and released by {@link #close()}. Textures
 * are RETAINED by CNA when assigned -- it says so, and it means an assigned texture stays alive --
 * and retained here as well, so {@link #getTexture} can answer with the Java object a game handed
 * over rather than with a second facade over the same native texture.
 */
public class PbrEffect implements AutoCloseable {

    private final Effect effect;
    private final Texture2D[] textures = new Texture2D[PbrTextureSlot.COUNT];

    /**
     * Creates the effect on one device.
     *
     * @param graphicsDevice the device to compile on
     * @throws ExtensionNotSupportedException when this build has no extended graphics layer
     */
    public PbrEffect(GraphicsDevice graphicsDevice) {
        this(graphicsDevice, false);
    }

    /** Creates either this effect or the skinned one, which differ only in which route builds it. */
    PbrEffect(GraphicsDevice graphicsDevice, boolean skinned) {
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        GraphicsExtension.requireBackend();
        long device = NativeBindings.nativeGraphicsDeviceValue(graphicsDevice);
        long[] created = new long[1];
        GraphicsExtension.check(skinned ? "SkinnedPbrEffect" : "PbrEffect",
                skinned ? NativePbrEffectRoutes.skinnedPbrEffectCreate(device, created)
                        : NativePbrEffectRoutes.pbrEffectCreate(device, created));
        effect = FacadeFactory.createBorrowedEffect(graphicsDevice, created[0]);
    }

    /**
     * Returns the XNA effect, for the ordinary effect surface and for drawing with.
     *
     * @return the effect
     */
    public final Effect getEffect() {
        return effect;
    }

    /**
     * Writes a whole material into the effect.
     *
     * @param material the material; its textures are retained by the effect
     */
    public final void applyMaterial(PbrMaterialExt material) {
        Objects.requireNonNull(material, "material");
        GraphicsExtension.check("PbrEffect.applyMaterial", applyMaterial(handle(),
                material.bytes(), material.integral(), material.floating()));
        // And then again, slot by slot -- because CNA's material route sets the effect's texture
        // pointers without telling the C API's handle registry, so a texture applied that way is
        // invisible to cna_pbr_effect_get_texture and comes back invalid from
        // cna_pbr_effect_extract_material. JAVA-UPSTREAM-010, measured in
        // tools/native-abi/probes/pbr_effect_material.c. Setting each slot the other way round
        // makes the registry agree with the effect, so a game's read-modify-write of a material
        // does not silently unbind every map. When CNA closes the gap this is redundant rather
        // than wrong.
        Texture2D[] applied = material.retainedTextures();
        for (PbrTextureSlot slot : PbrTextureSlot.values()) {
            setTexture(slot, applied[slot.ordinal()]);
        }
    }

    /**
     * Reads the whole material back out of the effect.
     *
     * <p>The textures come from what this object retained rather than from CNA's handles: CNA
     * gives back the handle it was given, and a second Java facade over an already-owned texture
     * would be a double release waiting to happen.
     *
     * @return the material
     */
    public final PbrMaterialExt extractMaterial() {
        byte[] bytes = new byte[3];
        long[] integral = new long[24];
        float[] floating = new float[48];
        GraphicsExtension.check("PbrEffect.extractMaterial",
                extractMaterial(handle(), bytes, integral, floating));
        return PbrMaterialExt.fromLeaves(integral, floating, textures);
    }

    /** Overridden by the skinned effect, which has its own pair of routes for the same job. */
    int applyMaterial(long effectHandle, byte[] bytes, long[] integral, float[] floating) {
        return org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes
                .pbrEffectApplyMaterial(effectHandle, bytes, integral, floating);
    }

    /** Overridden by the skinned effect. */
    int extractMaterial(long effectHandle, byte[] bytes, long[] integral, float[] floating) {
        return org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes
                .pbrEffectExtractMaterial(effectHandle, bytes, integral, floating);
    }

    /**
     * Returns the texture in one slot.
     *
     * @param slot the slot
     * @return the texture, or {@code null} when the slot is empty
     */
    public final Texture2D getTexture(PbrTextureSlot slot) {
        Objects.requireNonNull(slot, "slot");
        boolean[] present = new boolean[1];
        long[] texture = new long[1];
        GraphicsExtension.check("PbrEffect.getTexture",
                NativePbrEffectRoutes.pbrEffectGetTexture(handle(), slot.ordinal(), present,
                        texture));
        // CNA's own answer decides whether the slot is empty; the object comes from what was
        // handed over, because CNA returns the handle it retained and wrapping it a second time
        // would give one native texture two Java owners.
        return present[0] ? textures[slot.ordinal()] : null;
    }

    /**
     * Assigns a texture to one slot, or clears it.
     *
     * @param slot the slot
     * @param texture the texture, which must belong to the same device, or {@code null} to clear
     */
    public final void setTexture(PbrTextureSlot slot, Texture2D texture) {
        Objects.requireNonNull(slot, "slot");
        GraphicsExtension.check("PbrEffect.setTexture",
                NativePbrEffectRoutes.pbrEffectSetTexture(handle(), slot.ordinal(),
                        texture == null ? 0L : NativeBindings.nativeResourceHandle(texture)));
        textures[slot.ordinal()] = texture;
    }

    /**
     * Returns which set of texture coordinates one slot samples with.
     *
     * @param slot the slot
     * @return the coordinate set index
     */
    public final int getTextureCoordinateSet(PbrTextureSlot slot) {
        Objects.requireNonNull(slot, "slot");
        int[] value = new int[1];
        GraphicsExtension.check("PbrEffect.getTextureCoordinateSet",
                NativePbrEffectRoutes.pbrEffectGetTextureCoordinateSetExt(handle(),
                        slot.ordinal(), value));
        return value[0];
    }

    /**
     * Sets which set of texture coordinates one slot samples with.
     *
     * @param slot the slot
     * @param value the coordinate set index
     */
    public final void setTextureCoordinateSet(PbrTextureSlot slot, int value) {
        Objects.requireNonNull(slot, "slot");
        GraphicsExtension.check("PbrEffect.setTextureCoordinateSet",
                NativePbrEffectRoutes.pbrEffectSetTextureCoordinateSetExt(handle(),
                        slot.ordinal(), value));
    }

    /**
     * Reports whether one slot's texture is sRGB encoded.
     *
     * @param slot the slot; CNA accepts this question only for the colour-carrying slots
     * @return whether the texture is sRGB encoded
     */
    public final boolean isTextureSrgb(PbrTextureSlot slot) {
        Objects.requireNonNull(slot, "slot");
        boolean[] value = new boolean[1];
        GraphicsExtension.check("PbrEffect.isTextureSrgb",
                NativePbrEffectRoutes.pbrEffectGetTextureIsSrgbExt(handle(), slot.ordinal(),
                        value));
        return value[0];
    }

    /**
     * Sets whether one slot's texture is sRGB encoded.
     *
     * @param slot the slot
     * @param value whether the texture is sRGB encoded
     */
    public final void setTextureSrgb(PbrTextureSlot slot, boolean value) {
        Objects.requireNonNull(slot, "slot");
        GraphicsExtension.check("PbrEffect.setTextureSrgb",
                NativePbrEffectRoutes.pbrEffectSetTextureIsSrgbExt(handle(), slot.ordinal(),
                        value));
    }

    /**
     * Returns how one slot's texture is placed on the surface.
     *
     * @param slot the slot
     * @return the transform
     */
    public final TextureTransform getTextureTransform(PbrTextureSlot slot) {
        Objects.requireNonNull(slot, "slot");
        float[] leaves = new float[TextureTransform.LEAVES];
        GraphicsExtension.check("PbrEffect.getTextureTransform",
                NativePbrEffectRoutes.pbrEffectGetTextureTransformExt(handle(), slot.ordinal(),
                        leaves));
        return TextureTransform.fromLeaves(leaves, 0);
    }

    /**
     * Sets how one slot's texture is placed on the surface.
     *
     * @param slot the slot
     * @param value the transform
     */
    public final void setTextureTransform(PbrTextureSlot slot, TextureTransform value) {
        Objects.requireNonNull(slot, "slot");
        Objects.requireNonNull(value, "value");
        GraphicsExtension.check("PbrEffect.setTextureTransform",
                NativePbrEffectRoutes.pbrEffectSetTextureTransformExt(handle(), slot.ordinal(),
                        value.floating()));
    }

    /** @return the base colour */
    public final Color getDiffuseColor() {
        float[] value = new float[3];
        GraphicsExtension.check("PbrEffect.getDiffuseColor",
                NativePbrEffectRoutes.pbrEffectGetDiffuseColor(handle(), value));
        return new Color(value[0], value[1], value[2]);
    }

    /** @param value the base colour */
    public final void setDiffuseColor(Vector3 value) {
        GraphicsExtension.check("PbrEffect.setDiffuseColor",
                NativePbrEffectRoutes.pbrEffectSetDiffuseColor(handle(),
                        EngineValues.floats(value, "value")));
    }

    /** @return the linear emissive colour */
    public final Vector3 getEmissiveFactor() {
        float[] value = new float[3];
        GraphicsExtension.check("PbrEffect.getEmissiveFactor",
                NativePbrEffectRoutes.pbrEffectGetEmissiveFactor(handle(), value));
        return new Vector3(value[0], value[1], value[2]);
    }

    /** @param value the linear emissive colour */
    public final void setEmissiveFactor(Vector3 value) {
        GraphicsExtension.check("PbrEffect.setEmissiveFactor",
                NativePbrEffectRoutes.pbrEffectSetEmissiveFactor(handle(),
                        EngineValues.floats(value, "value")));
    }

    /** @return {@code KHR_materials_specular}'s colour factor */
    public final Vector3 getSpecularColorFactor() {
        float[] value = new float[3];
        GraphicsExtension.check("PbrEffect.getSpecularColorFactor",
                NativePbrEffectRoutes.pbrEffectGetSpecularColorFactorExt(handle(), value));
        return new Vector3(value[0], value[1], value[2]);
    }

    /** @param value the specular colour factor */
    public final void setSpecularColorFactor(Vector3 value) {
        GraphicsExtension.check("PbrEffect.setSpecularColorFactor",
                NativePbrEffectRoutes.pbrEffectSetSpecularColorFactorExt(handle(),
                        EngineValues.floats(value, "value")));
    }

    /** @return the material opacity */
    public final float getAlpha() {
        return scalar(NativePbrEffectRoutes::pbrEffectGetAlpha, "getAlpha");
    }

    /** @param value the material opacity, which CNA does not clamp */
    public final void setAlpha(float value) {
        GraphicsExtension.check("PbrEffect.setAlpha",
                NativePbrEffectRoutes.pbrEffectSetAlpha(handle(), value));
    }

    /** @return the alpha threshold, meaningful only in {@link AlphaMode#Mask} */
    public final float getAlphaCutoff() {
        return scalar(NativePbrEffectRoutes::pbrEffectGetAlphaCutoffExt, "getAlphaCutoff");
    }

    /** @param value the alpha threshold */
    public final void setAlphaCutoff(float value) {
        GraphicsExtension.check("PbrEffect.setAlphaCutoff",
                NativePbrEffectRoutes.pbrEffectSetAlphaCutoffExt(handle(), value));
    }

    /** @return how metallic the surface is */
    public final float getMetallicFactor() {
        return scalar(NativePbrEffectRoutes::pbrEffectGetMetallicFactor, "getMetallicFactor");
    }

    /** @param value how metallic the surface is */
    public final void setMetallicFactor(float value) {
        GraphicsExtension.check("PbrEffect.setMetallicFactor",
                NativePbrEffectRoutes.pbrEffectSetMetallicFactor(handle(), value));
    }

    /** @return how rough the surface is */
    public final float getRoughnessFactor() {
        return scalar(NativePbrEffectRoutes::pbrEffectGetRoughnessFactor, "getRoughnessFactor");
    }

    /** @param value how rough the surface is */
    public final void setRoughnessFactor(float value) {
        GraphicsExtension.check("PbrEffect.setRoughnessFactor",
                NativePbrEffectRoutes.pbrEffectSetRoughnessFactor(handle(), value));
    }

    /** @return how strongly the normal map is applied */
    public final float getNormalScale() {
        return scalar(NativePbrEffectRoutes::pbrEffectGetNormalScaleExt, "getNormalScale");
    }

    /** @param value how strongly the normal map is applied */
    public final void setNormalScale(float value) {
        GraphicsExtension.check("PbrEffect.setNormalScale",
                NativePbrEffectRoutes.pbrEffectSetNormalScaleExt(handle(), value));
    }

    /** @return how strongly the occlusion map is applied */
    public final float getOcclusionStrength() {
        return scalar(NativePbrEffectRoutes::pbrEffectGetOcclusionStrengthExt,
                "getOcclusionStrength");
    }

    /** @param value how strongly the occlusion map is applied */
    public final void setOcclusionStrength(float value) {
        GraphicsExtension.check("PbrEffect.setOcclusionStrength",
                NativePbrEffectRoutes.pbrEffectSetOcclusionStrengthExt(handle(), value));
    }

    /** @return the index of refraction */
    public final float getIor() {
        return scalar(NativePbrEffectRoutes::pbrEffectGetIorExt, "getIor");
    }

    /** @param value the index of refraction */
    public final void setIor(float value) {
        GraphicsExtension.check("PbrEffect.setIor",
                NativePbrEffectRoutes.pbrEffectSetIorExt(handle(), value));
    }

    /** @return {@code KHR_materials_specular}'s scalar strength */
    public final float getSpecularFactor() {
        return scalar(NativePbrEffectRoutes::pbrEffectGetSpecularFactorExt, "getSpecularFactor");
    }

    /** @param value the specular strength */
    public final void setSpecularFactor(float value) {
        GraphicsExtension.check("PbrEffect.setSpecularFactor",
                NativePbrEffectRoutes.pbrEffectSetSpecularFactorExt(handle(), value));
    }

    /** @return how the material's alpha is interpreted */
    public final AlphaMode getAlphaMode() {
        int[] value = new int[1];
        GraphicsExtension.check("PbrEffect.getAlphaMode",
                NativePbrEffectRoutes.pbrEffectGetAlphaModeExt(handle(), value));
        return AlphaMode.fromValue(value[0]);
    }

    /** @param value how the material's alpha is interpreted */
    public final void setAlphaMode(AlphaMode value) {
        Objects.requireNonNull(value, "value");
        GraphicsExtension.check("PbrEffect.setAlphaMode",
                NativePbrEffectRoutes.pbrEffectSetAlphaModeExt(handle(), value.ordinal()));
    }

    /** @return whether both faces are drawn */
    public final boolean isDoubleSided() {
        return flag(NativePbrEffectRoutes::pbrEffectGetDoubleSidedExt, "isDoubleSided");
    }

    /** @param value whether both faces are drawn */
    public final void setDoubleSided(boolean value) {
        GraphicsExtension.check("PbrEffect.setDoubleSided",
                NativePbrEffectRoutes.pbrEffectSetDoubleSidedExt(handle(), value));
    }

    /** @return whether the shader encodes its output to sRGB */
    public final boolean isOutputEncodedToSrgb() {
        return flag(NativePbrEffectRoutes::pbrEffectGetEncodeOutputToSrgbExt,
                "isOutputEncodedToSrgb");
    }

    /** @param value whether the shader encodes its output to sRGB */
    public final void setOutputEncodedToSrgb(boolean value) {
        GraphicsExtension.check("PbrEffect.setOutputEncodedToSrgb",
                NativePbrEffectRoutes.pbrEffectSetEncodeOutputToSrgbExt(handle(), value));
    }

    /** @return whether per-vertex colours modulate the base colour */
    public final boolean isVertexColorEnabled() {
        return flag(NativePbrEffectRoutes::pbrEffectGetVertexColorEnabledExt,
                "isVertexColorEnabled");
    }

    /** @param value whether per-vertex colours modulate the base colour */
    public final void setVertexColorEnabled(boolean value) {
        GraphicsExtension.check("PbrEffect.setVertexColorEnabled",
                NativePbrEffectRoutes.pbrEffectSetVertexColorEnabledExt(handle(), value));
    }

    /** Releases the effect. Closing twice is a no-op, as disposing an XNA resource is. */
    @Override
    public void close() {
        effect.Dispose();
    }

    /** The live native handle, refusing a disposed effect the way every other route does. */
    final long handle() {
        return NativeBindings.nativeResourceHandle(effect);
    }

    private float scalar(ScalarRoute route, String operation) {
        float[] value = new float[1];
        GraphicsExtension.check("PbrEffect." + operation, route.call(handle(), value));
        return value[0];
    }

    private boolean flag(FlagRoute route, String operation) {
        boolean[] value = new boolean[1];
        GraphicsExtension.check("PbrEffect." + operation, route.call(handle(), value));
        return value[0];
    }

    private interface ScalarRoute {
        int call(long effect, float[] value);
    }

    private interface FlagRoute {
        int call(long effect, boolean[] value);
    }
}
