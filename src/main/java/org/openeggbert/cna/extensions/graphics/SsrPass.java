package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

/**
 * Reflections traced through the depth buffer.
 *
 * <p>A CNA extension. Screen-space reflection marches a ray through depth until it hits
 * something, so it can only reflect what is already on screen -- which is why
 * {@link #getEdgeFade()} exists: a reflection that runs off the edge of the frame has to fade
 * rather than end.
 *
 * <p>Created on a device, configured here, and run by a {@link RenderPipeline}. Creation
 * succeeds on a renderer that cannot run the pass; ask
 * {@link PostProcessPass#isSupported(GraphicsDevice)}.
 */
public final class SsrPass extends PostProcessPass {

    private SsrPass(long handle) {
        super(handle);
    }

    /**
     * Creates the pass on a device.
     *
     * @param graphicsDevice the device to compile on
     * @return the pass, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static SsrPass create(GraphicsDevice graphicsDevice) {
        return new SsrPass(createOn(graphicsDevice, "SsrPass.create",
                NativeEngineLayerRoutes::ssrPassCreate));
    }

    /**
     * Returns the pass's DepthBias.
     *
     * @return the value
     */
    public float getDepthBias() {
        return readFloat("SsrPass.getDepthBias",
                NativeEngineLayerRoutes::ssrPassGetDepthBias);
    }

    /**
     * Sets the pass's DepthBias.
     *
     * <p>The value, ignored when not positive -- a guarded assignment.
     *
     * @param value the value
     */
    public void setDepthBias(float value) {
        GraphicsExtension.check("SsrPass.setDepthBias",
                NativeEngineLayerRoutes.ssrPassSetDepthBias(handle(), value));
    }

    /**
     * Returns the pass's EdgeFade.
     *
     * @return the value
     */
    public float getEdgeFade() {
        return readFloat("SsrPass.getEdgeFade",
                NativeEngineLayerRoutes::ssrPassGetEdgeFade);
    }

    /**
     * Sets the pass's EdgeFade.
     *
     * <p>The value, clamped to zero through 0.5 -- a different bound from the roughness blur beside it.
     *
     * @param value the value
     */
    public void setEdgeFade(float value) {
        GraphicsExtension.check("SsrPass.setEdgeFade",
                NativeEngineLayerRoutes.ssrPassSetEdgeFade(handle(), value));
    }

    /**
     * Returns the pass's Intensity.
     *
     * @return the value
     */
    public float getIntensity() {
        return readFloat("SsrPass.getIntensity",
                NativeEngineLayerRoutes::ssrPassGetIntensity);
    }

    /**
     * Sets the pass's Intensity.
     *
     * <p>The value, stored as given -- the canonical setter corrects nothing here.
     *
     * @param value the value
     */
    public void setIntensity(float value) {
        GraphicsExtension.check("SsrPass.setIntensity",
                NativeEngineLayerRoutes.ssrPassSetIntensity(handle(), value));
    }

    /**
     * Returns the pass's MaxDistance.
     *
     * @return the value
     */
    public float getMaxDistance() {
        return readFloat("SsrPass.getMaxDistance",
                NativeEngineLayerRoutes::ssrPassGetMaxDistance);
    }

    /**
     * Sets the pass's MaxDistance.
     *
     * <p>The value, ignored when not positive -- the canonical setter guards the assignment, so a zero or negative write leaves the previous distance in place rather than disabling the trace.
     *
     * @param value the value
     */
    public void setMaxDistance(float value) {
        GraphicsExtension.check("SsrPass.setMaxDistance",
                NativeEngineLayerRoutes.ssrPassSetMaxDistance(handle(), value));
    }

    /**
     * Returns the pass's RoughnessBlur.
     *
     * @return the value
     */
    public float getRoughnessBlur() {
        return readFloat("SsrPass.getRoughnessBlur",
                NativeEngineLayerRoutes::ssrPassGetRoughnessBlur);
    }

    /**
     * Sets the pass's RoughnessBlur.
     *
     * <p>The value, clamped to zero through 0.25.
     *
     * @param value the value
     */
    public void setRoughnessBlur(float value) {
        GraphicsExtension.check("SsrPass.setRoughnessBlur",
                NativeEngineLayerRoutes.ssrPassSetRoughnessBlur(handle(), value));
    }

    /**
     * Returns the pass's StepCount.
     *
     * @return the value
     */
    public int getStepCount() {
        return readInt("SsrPass.getStepCount",
                NativeEngineLayerRoutes::ssrPassGetStepCount);
    }

    /**
     * Sets the pass's StepCount.
     *
     * <p>The value, stored as given; the march clamps it to `CNA_SSR_PASS_MIN_STEP_COUNT_EXT`..`CNA_SSR_PASS_MAX_STEP_COUNT_EXT` when it applies, not when it is set, so a caller reads back what it wrote.
     *
     * @param value the value
     */
    public void setStepCount(int value) {
        GraphicsExtension.check("SsrPass.setStepCount",
                NativeEngineLayerRoutes.ssrPassSetStepCount(handle(), value));
    }

    /**
     * Returns the pass's Thickness.
     *
     * @return the value
     */
    public float getThickness() {
        return readFloat("SsrPass.getThickness",
                NativeEngineLayerRoutes::ssrPassGetThickness);
    }

    /**
     * Sets the pass's Thickness.
     *
     * <p>The value, ignored when not positive -- a guarded assignment; a zero-thickness depth test matches nothing.
     *
     * @param value the value
     */
    public void setThickness(float value) {
        GraphicsExtension.check("SsrPass.setThickness",
                NativeEngineLayerRoutes.ssrPassSetThickness(handle(), value));
    }

}
