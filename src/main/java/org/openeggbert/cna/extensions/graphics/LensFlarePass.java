package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

/**
 * Ghost images of bright spots, mirrored through the centre of the frame.
 *
 * <p>A CNA extension. {@link #getDispersal()} is how far apart the ghosts sit, and the threshold
 * decides what is bright enough to make one.
 *
 * <p>Created on a device, configured here, and run by a {@link RenderPipeline}. Creation
 * succeeds on a renderer that cannot run the pass; ask
 * {@link PostProcessPass#isSupported(GraphicsDevice)}.
 */
public final class LensFlarePass extends PostProcessPass {

    private LensFlarePass(long handle) {
        super(handle);
    }

    /**
     * Creates the pass on a device.
     *
     * @param graphicsDevice the device to compile on
     * @return the pass, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static LensFlarePass create(GraphicsDevice graphicsDevice) {
        return new LensFlarePass(createOn(graphicsDevice, "LensFlarePass.create",
                NativeEngineLayerRoutes::lensFlarePassCreate));
    }

    /**
     * Returns the pass's Dispersal.
     *
     * @return the value
     */
    public float getDispersal() {
        return readFloat("LensFlarePass.getDispersal",
                NativeEngineLayerRoutes::lensFlarePassGetDispersal);
    }

    /**
     * Sets the pass's Dispersal.
     *
     * <p>The value, clamped to zero through one.
     *
     * @param value the value
     */
    public void setDispersal(float value) {
        GraphicsExtension.check("LensFlarePass.setDispersal",
                NativeEngineLayerRoutes.lensFlarePassSetDispersal(handle(), value));
    }

    /**
     * Returns the pass's Intensity.
     *
     * @return the value
     */
    public float getIntensity() {
        return readFloat("LensFlarePass.getIntensity",
                NativeEngineLayerRoutes::lensFlarePassGetIntensity);
    }

    /**
     * Sets the pass's Intensity.
     *
     * <p>The value, ignored when negative, zero accepted.
     *
     * @param value the value
     */
    public void setIntensity(float value) {
        GraphicsExtension.check("LensFlarePass.setIntensity",
                NativeEngineLayerRoutes.lensFlarePassSetIntensity(handle(), value));
    }

    /**
     * Returns the pass's Threshold.
     *
     * @return the value
     */
    public float getThreshold() {
        return readFloat("LensFlarePass.getThreshold",
                NativeEngineLayerRoutes::lensFlarePassGetThreshold);
    }

    /**
     * Sets the pass's Threshold.
     *
     * <p>The value, ignored when negative, zero accepted.
     *
     * @param value the value
     */
    public void setThreshold(float value) {
        GraphicsExtension.check("LensFlarePass.setThreshold",
                NativeEngineLayerRoutes.lensFlarePassSetThreshold(handle(), value));
    }

}
