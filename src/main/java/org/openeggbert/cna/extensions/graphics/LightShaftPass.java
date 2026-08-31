package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Vector2;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

/**
 * God rays radiating from a bright point on screen.
 *
 * <p>A CNA extension. The pass works in screen space, so it takes the light's <em>screen</em>
 * position rather than its world one -- a game projects the sun itself and hands the result over,
 * and hides the effect when that position is off screen.
 *
 * <p>Created on a device, configured here, and run by a {@link RenderPipeline}. Creation
 * succeeds on a renderer that cannot run the pass; ask
 * {@link PostProcessPass#isSupported(GraphicsDevice)}.
 */
public final class LightShaftPass extends PostProcessPass {

    private LightShaftPass(long handle) {
        super(handle);
    }

    /**
     * Creates the pass on a device.
     *
     * @param graphicsDevice the device to compile on
     * @return the pass, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static LightShaftPass create(GraphicsDevice graphicsDevice) {
        return new LightShaftPass(createOn(graphicsDevice, "LightShaftPass.create",
                NativeEngineLayerRoutes::lightShaftPassCreate));
    }

    /**
     * Returns the pass's Decay.
     *
     * @return the value
     */
    public float getDecay() {
        return readFloat("LightShaftPass.getDecay",
                NativeEngineLayerRoutes::lightShaftPassGetDecay);
    }

    /**
     * Sets the pass's Decay.
     *
     * <p>The value, clamped to zero through one.
     *
     * @param value the value
     */
    public void setDecay(float value) {
        GraphicsExtension.check("LightShaftPass.setDecay",
                NativeEngineLayerRoutes.lightShaftPassSetDecay(handle(), value));
    }

    /**
     * Returns the pass's Intensity.
     *
     * @return the value
     */
    public float getIntensity() {
        return readFloat("LightShaftPass.getIntensity",
                NativeEngineLayerRoutes::lightShaftPassGetIntensity);
    }

    /**
     * Sets the pass's Intensity.
     *
     * <p>The value, ignored when negative, but zero is accepted.
     *
     * @param value the value
     */
    public void setIntensity(float value) {
        GraphicsExtension.check("LightShaftPass.setIntensity",
                NativeEngineLayerRoutes.lightShaftPassSetIntensity(handle(), value));
    }

    /**
     * Returns the pass's LightScreenPosition.
     *
     * @return the value
     */
    public Vector2 getLightScreenPosition() {
        float[] value = readVector("LightShaftPass.getLightScreenPosition", 2,
                NativeEngineLayerRoutes::lightShaftPassGetLightScreenPosition);
        return new Vector2(value[0], value[1]);
    }

    /**
     * Sets the pass's LightScreenPosition.
     *
     * <p>The value, stored as given -- a light off the edge of the screen still casts shafts across it.
     *
     * @param value the value
     */
    public void setLightScreenPosition(Vector2 value) {
        java.util.Objects.requireNonNull(value, "value");
        GraphicsExtension.check("LightShaftPass.setLightScreenPosition",
                NativeEngineLayerRoutes.lightShaftPassSetLightScreenPosition(handle(),
                        new float[] {value.X, value.Y}));
    }

    /**
     * Returns the pass's Threshold.
     *
     * @return the value
     */
    public float getThreshold() {
        return readFloat("LightShaftPass.getThreshold",
                NativeEngineLayerRoutes::lightShaftPassGetThreshold);
    }

    /**
     * Sets the pass's Threshold.
     *
     * <p>The value, ignored when negative, but zero is accepted.
     *
     * @param value the value
     */
    public void setThreshold(float value) {
        GraphicsExtension.check("LightShaftPass.setThreshold",
                NativeEngineLayerRoutes.lightShaftPassSetThreshold(handle(), value));
    }

}
