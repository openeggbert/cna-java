package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

/**
 * Blurs what the lens is not focused on.
 *
 * <p>A CNA extension, and a physical one: focus distance, focal length and f-number are the
 * camera's own numbers rather than an artistic blur radius, and
 * {@link #getCircleOfConfusionMillimetres} is the optics that turns them into a blur.
 *
 * <p>Created on a device, configured here, and run by a {@link RenderPipeline}. Creation
 * succeeds on a renderer that cannot run the pass; ask
 * {@link PostProcessPass#isSupported(GraphicsDevice)}.
 */
public final class DepthOfFieldPass extends PostProcessPass {

    private DepthOfFieldPass(long handle) {
        super(handle);
    }

    /**
     * Creates the pass on a device.
     *
     * @param graphicsDevice the device to compile on
     * @return the pass, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static DepthOfFieldPass create(GraphicsDevice graphicsDevice) {
        return new DepthOfFieldPass(createOn(graphicsDevice, "DepthOfFieldPass.create",
                NativeEngineLayerRoutes::depthOfFieldPassCreate));
    }

    /**
     * Returns the pass's FNumber.
     *
     * @return the value
     */
    public float getFNumber() {
        return readFloat("DepthOfFieldPass.getFNumber",
                NativeEngineLayerRoutes::depthOfFieldPassGetFNumber);
    }

    /**
     * Sets the pass's FNumber.
     *
     * <p>The value, ignored when not positive -- a guarded assignment; the aperture divides by it.
     *
     * @param value the value
     */
    public void setFNumber(float value) {
        GraphicsExtension.check("DepthOfFieldPass.setFNumber",
                NativeEngineLayerRoutes.depthOfFieldPassSetFNumber(handle(), value));
    }

    /**
     * Returns the pass's FocalLength.
     *
     * @return the value
     */
    public float getFocalLength() {
        return readFloat("DepthOfFieldPass.getFocalLength",
                NativeEngineLayerRoutes::depthOfFieldPassGetFocalLength);
    }

    /**
     * Sets the pass's FocalLength.
     *
     * <p>The value, ignored when not positive -- a guarded assignment.
     *
     * @param value the value
     */
    public void setFocalLength(float value) {
        GraphicsExtension.check("DepthOfFieldPass.setFocalLength",
                NativeEngineLayerRoutes.depthOfFieldPassSetFocalLength(handle(), value));
    }

    /**
     * Returns the pass's FocusDistance.
     *
     * @return the value
     */
    public float getFocusDistance() {
        return readFloat("DepthOfFieldPass.getFocusDistance",
                NativeEngineLayerRoutes::depthOfFieldPassGetFocusDistance);
    }

    /**
     * Sets the pass's FocusDistance.
     *
     * <p>The value, ignored when not positive -- a guarded assignment; focusing at zero distance has no meaning.
     *
     * @param value the value
     */
    public void setFocusDistance(float value) {
        GraphicsExtension.check("DepthOfFieldPass.setFocusDistance",
                NativeEngineLayerRoutes.depthOfFieldPassSetFocusDistance(handle(), value));
    }

    /**
     * Returns the pass's MaxRadius.
     *
     * @return the value
     */
    public float getMaxRadius() {
        return readFloat("DepthOfFieldPass.getMaxRadius",
                NativeEngineLayerRoutes::depthOfFieldPassGetMaxRadius);
    }

    /**
     * Sets the pass's MaxRadius.
     *
     * <p>The value, clamped to zero through 0.25.
     *
     * @param value the value
     */
    public void setMaxRadius(float value) {
        GraphicsExtension.check("DepthOfFieldPass.setMaxRadius",
                NativeEngineLayerRoutes.depthOfFieldPassSetMaxRadius(handle(), value));
    }

    /**
     * Returns how large a point at a depth blurs to, in millimetres on the sensor.
     *
     * <p>The optics the pass implements, so a game can pick a focal length and an f-number
     * against a number rather than by eye.
     *
     * @param depth the depth of the point
     * @param focusDistance where the lens is focused
     * @param focalLength the lens's focal length
     * @param fNumber the aperture
     * @return the circle of confusion in millimetres
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static float getCircleOfConfusionMillimetres(float depth, float focusDistance,
            float focalLength, float fNumber) {
        GraphicsExtension.requireBackend();
        float[] millimetres = new float[1];
        GraphicsExtension.check("DepthOfFieldPass.getCircleOfConfusionMillimetres",
                NativeEngineLayerRoutes.depthOfFieldPassCircleOfConfusionMillimetres(depth,
                        focusDistance, focalLength, fNumber, millimetres));
        return millimetres[0];
    }
}
