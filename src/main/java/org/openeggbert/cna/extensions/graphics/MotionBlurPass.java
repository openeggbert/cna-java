package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

/**
 * Smears the frame along whatever moved.
 *
 * <p>A CNA extension, and one that needs a velocity buffer:
 * {@link RenderPipeline#setVelocityInput} is where that comes from. Without it the pass has
 * nothing to smear along.
 *
 * <p>Created on a device, configured here, and run by a {@link RenderPipeline}. Creation
 * succeeds on a renderer that cannot run the pass; ask
 * {@link PostProcessPass#isSupported(GraphicsDevice)}.
 */
public final class MotionBlurPass extends PostProcessPass {

    private MotionBlurPass(long handle) {
        super(handle);
    }

    /**
     * Creates the pass on a device.
     *
     * @param graphicsDevice the device to compile on
     * @return the pass, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static MotionBlurPass create(GraphicsDevice graphicsDevice) {
        return new MotionBlurPass(createOn(graphicsDevice, "MotionBlurPass.create",
                NativeEngineLayerRoutes::motionBlurPassCreate));
    }

    /**
     * Returns the pass's MaxDistance.
     *
     * @return the value
     */
    public float getMaxDistance() {
        return readFloat("MotionBlurPass.getMaxDistance",
                NativeEngineLayerRoutes::motionBlurPassGetMaxDistance);
    }

    /**
     * Sets the pass's MaxDistance.
     *
     * <p>The value, clamped to zero through 0.25 -- a different bound from the strength beside it.
     *
     * @param value the value
     */
    public void setMaxDistance(float value) {
        GraphicsExtension.check("MotionBlurPass.setMaxDistance",
                NativeEngineLayerRoutes.motionBlurPassSetMaxDistance(handle(), value));
    }

    /**
     * Returns the pass's Strength.
     *
     * @return the value
     */
    public float getStrength() {
        return readFloat("MotionBlurPass.getStrength",
                NativeEngineLayerRoutes::motionBlurPassGetStrength);
    }

    /**
     * Sets the pass's Strength.
     *
     * <p>The value, clamped to zero through one.
     *
     * @param value the value
     */
    public void setStrength(float value) {
        GraphicsExtension.check("MotionBlurPass.setStrength",
                NativeEngineLayerRoutes.motionBlurPassSetStrength(handle(), value));
    }

}
