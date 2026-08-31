package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.Effect;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import Microsoft.Xna.Framework.Matrix;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.Objects;

/**
 * The lighting parameters CNA's stock effects take beyond anything XNA has.
 *
 * <p>A CNA extension over XNA's own {@link Effect}: an effect from the extended layer understands
 * a shadow map and its transform, a punctual light with its shadow attached, and an image-based
 * light -- none of which XNA's {@code EffectParameter} surface has a name for. These reach them.
 *
 * <p>Static rather than an object, because the parameters belong to the effect and not to a
 * wrapper: two wrappers over one effect would disagree about what was set.
 *
 * <p><strong>The getters that would return a texture are deliberately absent.</strong> CNA lends
 * those handles without saying how a borrow is given back, so a Java facade over one would be a
 * leak or a dangling reference depending on which guess is right. A game that sets a shadow map
 * already has it; the setters are what it needs.
 *
 * <p>{@code set_shadow_cascades} and its getter are unbound for a different reason: their
 * structure carries a fixed array of matrices, a shape the generated boundary refuses rather than
 * guesses at.
 */
public final class EffectLighting {

    private EffectLighting() {
    }

    /**
     * Gives an effect the shadow map to sample.
     *
     * <p>Borrowed and not retained here, because a static has nowhere to retain it: whoever
     * calls this keeps the texture alive for as long as the effect names it.
     *
     * @param effect the effect
     * @param shadowMap the shadow texture, or {@code null} to unbind
     */
    public static void setShadowMap(Effect effect, Texture2D shadowMap) {
        GraphicsExtension.check("EffectLighting.setShadowMap",
                NativeEngineLayerRoutes.effectSetShadowMapExt(handle(effect),
                        shadowMap == null ? 0L
                                : NativeBindings.nativeResourceHandle(shadowMap)));
    }

    /**
     * Gives an effect the transform that takes world space into the shadow map.
     *
     * @param effect the effect
     * @param lightViewProjection the transform
     */
    public static void setLightViewProjection(Effect effect, Matrix lightViewProjection) {
        GraphicsExtension.check("EffectLighting.setLightViewProjection",
                NativeEngineLayerRoutes.effectSetLightViewProjectionExt(handle(effect),
                        EngineValues.floats(lightViewProjection, "lightViewProjection")));
    }

    /**
     * Returns the transform an effect was given.
     *
     * @param effect the effect
     * @return the transform
     */
    public static Matrix getLightViewProjection(Effect effect) {
        float[] matrix = new float[EngineValues.MATRIX_LEAVES];
        GraphicsExtension.check("EffectLighting.getLightViewProjection",
                NativeEngineLayerRoutes.effectGetLightViewProjectionExt(handle(effect), matrix));
        return EngineValues.matrix(matrix, 0);
    }

    /**
     * Turns shadow sampling on or off for an effect.
     *
     * @param effect the effect
     * @param enabled whether to sample shadows
     */
    public static void setShadowsEnabled(Effect effect, boolean enabled) {
        GraphicsExtension.check("EffectLighting.setShadowsEnabled",
                NativeEngineLayerRoutes.effectSetShadowsEnabledExt(handle(effect), enabled));
    }

    /**
     * Reports whether an effect samples shadows.
     *
     * @param effect the effect
     * @return whether shadow sampling is on
     */
    public static boolean isShadowsEnabled(Effect effect) {
        boolean[] enabled = new boolean[1];
        GraphicsExtension.check("EffectLighting.isShadowsEnabled",
                NativeEngineLayerRoutes.effectIsShadowsEnabledExt(handle(effect), enabled));
        return enabled[0];
    }

    /**
     * Sets the depth bias an effect applies when sampling its shadow map.
     *
     * @param effect the effect
     * @param bias the bias
     */
    public static void setShadowDepthBias(Effect effect, float bias) {
        GraphicsExtension.check("EffectLighting.setShadowDepthBias",
                NativeEngineLayerRoutes.effectSetShadowDepthBiasExt(handle(effect), bias));
    }

    /**
     * Returns the depth bias an effect applies when sampling its shadow map.
     *
     * @param effect the effect
     * @return the bias
     */
    public static float getShadowDepthBias(Effect effect) {
        float[] bias = new float[1];
        GraphicsExtension.check("EffectLighting.getShadowDepthBias",
                NativeEngineLayerRoutes.effectGetShadowDepthBiasExt(handle(effect), bias));
        return bias[0];
    }

    /**
     * Sets how wide an effect filters its shadow map, in texels.
     *
     * @param effect the effect
     * @param radius the filter radius
     */
    public static void setShadowFilterRadius(Effect effect, int radius) {
        GraphicsExtension.check("EffectLighting.setShadowFilterRadius",
                NativeEngineLayerRoutes.effectSetShadowFilterRadiusExt(handle(effect), radius));
    }

    /**
     * Returns how wide an effect filters its shadow map, in texels.
     *
     * @param effect the effect
     * @return the filter radius
     */
    public static int getShadowFilterRadius(Effect effect) {
        int[] radius = new int[1];
        GraphicsExtension.check("EffectLighting.getShadowFilterRadius",
                NativeEngineLayerRoutes.effectGetShadowFilterRadiusExt(handle(effect), radius));
        return radius[0];
    }

    /**
     * Gives an effect one punctual light and its shadow.
     *
     * @param effect the effect
     * @param light the light; its textures are borrowed and not retained here
     */
    public static void setPunctualLight(Effect effect, PunctualLight light) {
        Objects.requireNonNull(light, "light");
        GraphicsExtension.check("EffectLighting.setPunctualLight",
                NativeEngineLayerRoutes.effectSetPunctualLightExt(handle(effect),
                        light.integral(), light.floating()));
    }

    /**
     * Returns the punctual light an effect shades with.
     *
     * <p><strong>Its two shadow textures always come back {@code null}</strong>, whatever was set.
     * That is CNA's own behaviour and not a gap here: the canonical structure holds raw pointers,
     * and the ABI declines to invent a handle for a texture it does not track. Every numeric field
     * round-trips, so this is the way to read back what an effect was given, and a game that needs
     * the textures keeps the ones it set.
     *
     * @param effect the effect
     * @return the light
     */
    public static PunctualLight getPunctualLight(Effect effect) {
        long[] integral = new long[4];
        float[] floating = new float[29];
        GraphicsExtension.check("EffectLighting.getPunctualLight",
                NativeEngineLayerRoutes.effectGetPunctualLightExt(handle(effect), integral,
                        floating));
        return PunctualLight.fromLeaves(integral, floating);
    }

    /**
     * Reports whether an effect currently has a shadow map bound.
     *
     * <p>A boolean rather than the texture, deliberately. CNA answers with a <em>fresh name</em>
     * for the texture, not the handle that was set, and that name keeps nothing alive -- so a
     * Java facade over it would claim an ownership that does not exist and would dangle the moment
     * whoever really owns the texture disposed it. The name is given straight back here, and what
     * a caller can honestly learn from the question is whether something is bound.
     *
     * @param effect the effect
     * @return whether a shadow map is bound
     */
    public static boolean hasShadowMap(Effect effect) {
        long[] shadowMap = new long[1];
        GraphicsExtension.check("EffectLighting.hasShadowMap",
                NativeEngineLayerRoutes.effectGetShadowMapExt(handle(effect), shadowMap));
        NativeBindings.releaseBorrowedTextureName(shadowMap[0]);
        return shadowMap[0] != 0L;
    }

    /**
     * Gives an effect an environment to light from.
     *
     * @param effect the effect
     * @param light the light; its textures are borrowed and not retained here
     */
    public static void setImageBasedLight(Effect effect, ImageBasedLight light) {
        Objects.requireNonNull(light, "light");
        GraphicsExtension.check("EffectLighting.setImageBasedLight",
                NativeEngineLayerRoutes.effectSetImageBasedLightExt(handle(effect),
                        light.integral(), light.floating()));
    }

    private static long handle(Effect effect) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(effect, "effect");
        return NativeBindings.nativeResourceHandle(effect);
    }
}
