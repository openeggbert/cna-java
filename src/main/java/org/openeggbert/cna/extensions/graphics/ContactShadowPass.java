package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

/**
 * The short, sharp shadow where two surfaces meet.
 *
 * <p>A CNA extension, and the companion to {@link ShadowMap}: a shadow map at any practical
 * resolution loses the contact point, and this traces it in screen space instead.
 * {@link #combineVisibility} is how the two are put together, and
 * {@link #isOccluded} is the ray test itself, exposed so a game can reason about the
 * thickness and bias it chose.
 *
 * <p>Created on a device, configured here, and run by a {@link RenderPipeline}. Creation
 * succeeds on a renderer that cannot run the pass; ask
 * {@link PostProcessPass#isSupported(GraphicsDevice)}.
 */
public final class ContactShadowPass extends PostProcessPass {

    private ContactShadowPass(long handle) {
        super(handle);
    }

    /**
     * Creates the pass on a device.
     *
     * @param graphicsDevice the device to compile on
     * @return the pass, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static ContactShadowPass create(GraphicsDevice graphicsDevice) {
        return new ContactShadowPass(createOn(graphicsDevice, "ContactShadowPass.create",
                NativeEngineLayerRoutes::contactShadowPassCreate));
    }

    /**
     * Returns the pass's Bias.
     *
     * @return the value
     */
    public float getBias() {
        return readFloat("ContactShadowPass.getBias",
                NativeEngineLayerRoutes::contactShadowPassGetBias);
    }

    /**
     * Sets the pass's Bias.
     *
     * <p>The bias.
     *
     * @param value the value
     */
    public void setBias(float value) {
        GraphicsExtension.check("ContactShadowPass.setBias",
                NativeEngineLayerRoutes.contactShadowPassSetBias(handle(), value));
    }

    /**
     * Returns the pass's Intensity.
     *
     * @return the value
     */
    public float getIntensity() {
        return readFloat("ContactShadowPass.getIntensity",
                NativeEngineLayerRoutes::contactShadowPassGetIntensity);
    }

    /**
     * Sets the pass's Intensity.
     *
     * <p>The intensity.
     *
     * @param value the value
     */
    public void setIntensity(float value) {
        GraphicsExtension.check("ContactShadowPass.setIntensity",
                NativeEngineLayerRoutes.contactShadowPassSetIntensity(handle(), value));
    }

    /**
     * Returns the pass's LightDirection.
     *
     * @return the value
     */
    public Vector3 getLightDirection() {
        float[] value = readVector("ContactShadowPass.getLightDirection", 3,
                NativeEngineLayerRoutes::contactShadowPassGetLightDirection);
        return new Vector3(value[0], value[1], value[2]);
    }

    /**
     * Sets the pass's LightDirection.
     *
     * <p>The direction.
     *
     * @param value the value
     */
    public void setLightDirection(Vector3 value) {
        GraphicsExtension.check("ContactShadowPass.setLightDirection",
                NativeEngineLayerRoutes.contactShadowPassSetLightDirection(handle(),
                        EngineValues.floats(value, "value")));
    }

    /**
     * Returns the pass's MaxDistance.
     *
     * @return the value
     */
    public float getMaxDistance() {
        return readFloat("ContactShadowPass.getMaxDistance",
                NativeEngineLayerRoutes::contactShadowPassGetMaxDistance);
    }

    /**
     * Sets the pass's MaxDistance.
     *
     * <p>The distance.
     *
     * @param value the value
     */
    public void setMaxDistance(float value) {
        GraphicsExtension.check("ContactShadowPass.setMaxDistance",
                NativeEngineLayerRoutes.contactShadowPassSetMaxDistance(handle(), value));
    }

    /**
     * Returns the pass's StepCount.
     *
     * @return the value
     */
    public int getStepCount() {
        return readInt("ContactShadowPass.getStepCount",
                NativeEngineLayerRoutes::contactShadowPassGetStepCount);
    }

    /**
     * Sets the pass's StepCount.
     *
     * <p>The step count.
     *
     * @param value the value
     */
    public void setStepCount(int value) {
        GraphicsExtension.check("ContactShadowPass.setStepCount",
                NativeEngineLayerRoutes.contactShadowPassSetStepCount(handle(), value));
    }

    /**
     * Returns the pass's Thickness.
     *
     * @return the value
     */
    public float getThickness() {
        return readFloat("ContactShadowPass.getThickness",
                NativeEngineLayerRoutes::contactShadowPassGetThickness);
    }

    /**
     * Sets the pass's Thickness.
     *
     * <p>The thickness.
     *
     * @param value the value
     */
    public void setThickness(float value) {
        GraphicsExtension.check("ContactShadowPass.setThickness",
                NativeEngineLayerRoutes.contactShadowPassSetThickness(handle(), value));
    }

    /**
     * Returns why the pass fell back, when it did.
     *
     * @return the reason, or an empty string when it did not
     */
    public String getFallbackReason() {
        long pass = handle();
        return readText("ContactShadowPass.getFallbackReason",
                (destination, bytes) -> NativeEngineLayerRoutes
                        .contactShadowPassCopyFallbackReason(pass, destination, bytes));
    }

    /**
     * Runs the pass's own occlusion test on one pair of depths.
     *
     * <p>The ray test the shader performs, exposed so a game can reason about the thickness and
     * bias it chose rather than tuning them by eye.
     *
     * @param rayViewDepth the depth the ray has marched to
     * @param sceneViewDepth the depth the scene holds there
     * @param bias the depth bias
     * @param thickness the assumed thickness of what the scene holds
     * @return whether the ray is occluded
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static boolean isOccluded(float rayViewDepth, float sceneViewDepth, float bias,
            float thickness) {
        GraphicsExtension.requireBackend();
        boolean[] occluded = new boolean[1];
        GraphicsExtension.check("ContactShadowPass.isOccluded",
                NativeEngineLayerRoutes.contactShadowPassIsOccluded(rayViewDepth, sceneViewDepth,
                        bias, thickness, occluded));
        return occluded[0];
    }

    /**
     * Combines a shadow map's visibility with a contact shadow's.
     *
     * <p>CNA's own combination, which is what keeps the two from double-darkening where they
     * agree.
     *
     * @param shadowMapVisibility what the shadow map says
     * @param contactVisibility what this pass says
     * @return the combined visibility
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static float combineVisibility(float shadowMapVisibility, float contactVisibility) {
        GraphicsExtension.requireBackend();
        float[] visibility = new float[1];
        GraphicsExtension.check("ContactShadowPass.combineVisibility",
                NativeEngineLayerRoutes.contactShadowPassCombineVisibility(shadowMapVisibility,
                        contactVisibility, visibility));
        return visibility[0];
    }

    /**
     * Returns the occlusion test CNA's shader runs.
     *
     * @return the GLSL
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static String getOcclusionTestGlsl() {
        GraphicsExtension.requireBackend();
        return readText("ContactShadowPass.getOcclusionTestGlsl",
                NativeEngineLayerRoutes::contactShadowPassCopyOcclusionTestGlsl);
    }
}
