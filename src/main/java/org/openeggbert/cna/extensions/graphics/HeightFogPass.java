package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

/**
 * Fog that thins with altitude.
 *
 * <p>A CNA extension. XNA's fog is a single distance ramp in {@code BasicEffect}; this is the
 * exponential-with-height model an outdoor scene wants, where a valley fills and a hilltop does
 * not. {@link #getOpticalDepth} evaluates the same integral the shader does.
 *
 * <p>Created on a device, configured here, and run by a {@link RenderPipeline}. Creation
 * succeeds on a renderer that cannot run the pass; ask
 * {@link PostProcessPass#isSupported(GraphicsDevice)}.
 */
public final class HeightFogPass extends PostProcessPass {

    private HeightFogPass(long handle) {
        super(handle);
    }

    /**
     * Creates the pass on a device.
     *
     * @param graphicsDevice the device to compile on
     * @return the pass, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static HeightFogPass create(GraphicsDevice graphicsDevice) {
        return new HeightFogPass(createOn(graphicsDevice, "HeightFogPass.create",
                NativeEngineLayerRoutes::heightFogPassCreate));
    }

    /**
     * Returns the pass's BaseHeight.
     *
     * @return the value
     */
    public float getBaseHeight() {
        return readFloat("HeightFogPass.getBaseHeight",
                NativeEngineLayerRoutes::heightFogPassGetBaseHeight);
    }

    /**
     * Sets the pass's BaseHeight.
     *
     * <p>The value, stored as given -- a fog base below the origin is legitimate, so this one is not floored.
     *
     * @param value the value
     */
    public void setBaseHeight(float value) {
        GraphicsExtension.check("HeightFogPass.setBaseHeight",
                NativeEngineLayerRoutes.heightFogPassSetBaseHeight(handle(), value));
    }

    /**
     * Returns the pass's Color.
     *
     * @return the value
     */
    public Vector3 getColor() {
        float[] value = readVector("HeightFogPass.getColor", 3,
                NativeEngineLayerRoutes::heightFogPassGetColor);
        return new Vector3(value[0], value[1], value[2]);
    }

    /**
     * Sets the pass's Color.
     *
     * <p>The value, stored as given.
     *
     * @param value the value
     */
    public void setColor(Vector3 value) {
        GraphicsExtension.check("HeightFogPass.setColor",
                NativeEngineLayerRoutes.heightFogPassSetColor(handle(),
                        EngineValues.floats(value, "value")));
    }

    /**
     * Returns the pass's Density.
     *
     * @return the value
     */
    public float getDensity() {
        return readFloat("HeightFogPass.getDensity",
                NativeEngineLayerRoutes::heightFogPassGetDensity);
    }

    /**
     * Sets the pass's Density.
     *
     * <p>The value, ignored when negative, but zero is accepted -- no fog is a legitimate setting.
     *
     * @param value the value
     */
    public void setDensity(float value) {
        GraphicsExtension.check("HeightFogPass.setDensity",
                NativeEngineLayerRoutes.heightFogPassSetDensity(handle(), value));
    }

    /**
     * Returns the pass's Falloff.
     *
     * @return the value
     */
    public float getFalloff() {
        return readFloat("HeightFogPass.getFalloff",
                NativeEngineLayerRoutes::heightFogPassGetFalloff);
    }

    /**
     * Sets the pass's Falloff.
     *
     * <p>The value, ignored when not positive -- the exponential divides by it, so zero is rejected as well as negatives.
     *
     * @param value the value
     */
    public void setFalloff(float value) {
        GraphicsExtension.check("HeightFogPass.setFalloff",
                NativeEngineLayerRoutes.heightFogPassSetFalloff(handle(), value));
    }

    /**
     * Returns the optical depth along a ray through the fog.
     *
     * <p>The integral the shader evaluates: how much fog a ray actually passes through, given
     * that the density falls off with height.
     *
     * @param cameraHeight where the ray starts
     * @param rayHeightStep how fast the ray climbs
     * @param distance how far the ray travels
     * @param density the fog density at the base height
     * @param falloff how fast the density falls with height
     * @param baseHeight the height the density is quoted at
     * @return the optical depth
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static float getOpticalDepth(float cameraHeight, float rayHeightStep, float distance,
            float density, float falloff, float baseHeight) {
        GraphicsExtension.requireBackend();
        float[] depth = new float[1];
        GraphicsExtension.check("HeightFogPass.getOpticalDepth",
                NativeEngineLayerRoutes.heightFogPassOpticalDepth(cameraHeight, rayHeightStep,
                        distance, density, falloff, baseHeight, depth));
        return depth[0];
    }
}
