package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

/**
 * Maps an HDR frame onto what a display can actually show.
 *
 * <p>A CNA extension. Without it an HDR pipeline clips: everything above one is white. The
 * operator is the choice that decides how the frame looks -- Reinhard washes out, filmic and ACES
 * hold contrast -- and {@link #tonemapChannel} evaluates the same curve CNA's shader does, so a
 * game can show the operator's response rather than describe it.
 *
 * <p>Debanding is the other half: an eight-bit destination shows visible steps in a smooth
 * gradient, and a little noise hides them.
 *
 * <p>Created on a device, configured here, and run by a {@link RenderPipeline}. Creation
 * succeeds on a renderer that cannot run the pass; ask
 * {@link PostProcessPass#isSupported(GraphicsDevice)}.
 */
public final class TonemapPass extends PostProcessPass {

    private TonemapPass(long handle) {
        super(handle);
    }

    /**
     * Creates the pass on a device.
     *
     * @param graphicsDevice the device to compile on
     * @return the pass, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static TonemapPass create(GraphicsDevice graphicsDevice) {
        return new TonemapPass(createOn(graphicsDevice, "TonemapPass.create",
                NativeEngineLayerRoutes::tonemapPassCreate));
    }

    /**
     * Returns the pass's DebandStrength.
     *
     * @return the value
     */
    public float getDebandStrength() {
        return readFloat("TonemapPass.getDebandStrength",
                NativeEngineLayerRoutes::tonemapPassGetDebandStrength);
    }

    /**
     * Sets the pass's DebandStrength.
     *
     * <p>The value.
     *
     * @param value the value
     */
    public void setDebandStrength(float value) {
        GraphicsExtension.check("TonemapPass.setDebandStrength",
                NativeEngineLayerRoutes.tonemapPassSetDebandStrength(handle(), value));
    }

    /**
     * Returns the pass's Exposure.
     *
     * @return the value
     */
    public float getExposure() {
        return readFloat("TonemapPass.getExposure",
                NativeEngineLayerRoutes::tonemapPassGetExposure);
    }

    /**
     * Sets the pass's Exposure.
     *
     * <p>The value.
     *
     * @param value the value
     */
    public void setExposure(float value) {
        GraphicsExtension.check("TonemapPass.setExposure",
                NativeEngineLayerRoutes.tonemapPassSetExposure(handle(), value));
    }

    /**
     * Returns the pass's Gamma.
     *
     * @return the value
     */
    public float getGamma() {
        return readFloat("TonemapPass.getGamma",
                NativeEngineLayerRoutes::tonemapPassGetGamma);
    }

    /**
     * Sets the pass's Gamma.
     *
     * <p>The value.
     *
     * @param value the value
     */
    public void setGamma(float value) {
        GraphicsExtension.check("TonemapPass.setGamma",
                NativeEngineLayerRoutes.tonemapPassSetGamma(handle(), value));
    }

    /**
     * Returns the operator the pass applies.
     *
     * @return the mode
     */
    public TonemappingMode getMode() {
        return TonemappingMode.fromValue(
                readInt("TonemapPass.getMode", NativeEngineLayerRoutes::tonemapPassGetMode));
    }

    /**
     * Sets the operator the pass applies.
     *
     * @param value the mode
     */
    public void setMode(TonemappingMode value) {
        java.util.Objects.requireNonNull(value, "value");
        GraphicsExtension.check("TonemapPass.setMode",
                NativeEngineLayerRoutes.tonemapPassSetMode(handle(), value.ordinal()));
    }

    /**
     * Returns the pass's DebandEnabled.
     *
     * @return the value
     */
    public boolean isDebandEnabled() {
        return readFlag("TonemapPass.isDebandEnabled",
                NativeEngineLayerRoutes::tonemapPassIsDebandEnabled);
    }

    /**
     * Sets the pass's DebandEnabled.
     *
     * <p>The value.
     *
     * @param value the value
     */
    public void setDebandEnabled(boolean value) {
        GraphicsExtension.check("TonemapPass.setDebandEnabled",
                NativeEngineLayerRoutes.tonemapPassSetDebandEnabled(handle(), value));
    }

    /**
     * Applies an operator to one channel value.
     *
     * <p>The same curve CNA's shader evaluates, so a game can draw the operator's response.
     *
     * @param mode the operator
     * @param value the channel value
     * @param exposure the exposure multiplier
     * @param gamma the gamma to encode with
     * @return the tonemapped value
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static float tonemapChannel(TonemappingMode mode, float value, float exposure,
            float gamma) {
        GraphicsExtension.requireBackend();
        java.util.Objects.requireNonNull(mode, "mode");
        float[] result = new float[1];
        GraphicsExtension.check("TonemapPass.tonemapChannel",
                NativeEngineLayerRoutes.tonemapPassTonemapChannel(mode.ordinal(), value,
                        exposure, gamma, result));
        return result[0];
    }
}
