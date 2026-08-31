package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

/**
 * The blue haze distance puts over everything outdoors.
 *
 * <p>A CNA extension, and a physical one: {@link #getTurbidity()} is how much is in the air and
 * {@link #getScaleHeight()} is how fast the atmosphere thins with altitude.
 * {@link #getAirMassForDistance} and {@link #getTransmittance} are the same two functions the
 * shader evaluates.
 *
 * <p>Created on a device, configured here, and run by a {@link RenderPipeline}. Creation
 * succeeds on a renderer that cannot run the pass; ask
 * {@link PostProcessPass#isSupported(GraphicsDevice)}.
 */
public final class AerialPerspectivePass extends PostProcessPass {

    private AerialPerspectivePass(long handle) {
        super(handle);
    }

    /**
     * Creates the pass on a device.
     *
     * @param graphicsDevice the device to compile on
     * @return the pass, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static AerialPerspectivePass create(GraphicsDevice graphicsDevice) {
        return new AerialPerspectivePass(createOn(graphicsDevice, "AerialPerspectivePass.create",
                NativeEngineLayerRoutes::aerialPerspectivePassCreate));
    }

    /**
     * Returns the pass's Intensity.
     *
     * @return the value
     */
    public float getIntensity() {
        return readFloat("AerialPerspectivePass.getIntensity",
                NativeEngineLayerRoutes::aerialPerspectivePassGetIntensity);
    }

    /**
     * Sets the pass's Intensity.
     *
     * <p>The value, ignored when negative, but zero is accepted -- a guarded assignment whose bound admits zero, because no aerial perspective at all is a legitimate setting.
     *
     * @param value the value
     */
    public void setIntensity(float value) {
        GraphicsExtension.check("AerialPerspectivePass.setIntensity",
                NativeEngineLayerRoutes.aerialPerspectivePassSetIntensity(handle(), value));
    }

    /**
     * Returns the pass's ScaleHeight.
     *
     * @return the value
     */
    public float getScaleHeight() {
        return readFloat("AerialPerspectivePass.getScaleHeight",
                NativeEngineLayerRoutes::aerialPerspectivePassGetScaleHeight);
    }

    /**
     * Sets the pass's ScaleHeight.
     *
     * <p>The value, floored at 0.001 -- the air-mass integral divides by it, so zero is a division by zero rather than a thin atmosphere.
     *
     * @param value the value
     */
    public void setScaleHeight(float value) {
        GraphicsExtension.check("AerialPerspectivePass.setScaleHeight",
                NativeEngineLayerRoutes.aerialPerspectivePassSetScaleHeight(handle(), value));
    }

    /**
     * Returns the pass's SunDirection.
     *
     * @return the value
     */
    public Vector3 getSunDirection() {
        float[] value = readVector("AerialPerspectivePass.getSunDirection", 3,
                NativeEngineLayerRoutes::aerialPerspectivePassGetSunDirection);
        return new Vector3(value[0], value[1], value[2]);
    }

    /**
     * Sets the pass's SunDirection.
     *
     * <p>The value, stored as given -- the canonical setter corrects nothing here.
     *
     * @param value the value
     */
    public void setSunDirection(Vector3 value) {
        GraphicsExtension.check("AerialPerspectivePass.setSunDirection",
                NativeEngineLayerRoutes.aerialPerspectivePassSetSunDirection(handle(),
                        EngineValues.floats(value, "value")));
    }

    /**
     * Returns the pass's Turbidity.
     *
     * @return the value
     */
    public float getTurbidity() {
        return readFloat("AerialPerspectivePass.getTurbidity",
                NativeEngineLayerRoutes::aerialPerspectivePassGetTurbidity);
    }

    /**
     * Sets the pass's Turbidity.
     *
     * <p>The value, floored at one, not at zero: turbidity is a ratio against a perfectly clear atmosphere, so a value below one describes air clearer than vacuum.
     *
     * @param value the value
     */
    public void setTurbidity(float value) {
        GraphicsExtension.check("AerialPerspectivePass.setTurbidity",
                NativeEngineLayerRoutes.aerialPerspectivePassSetTurbidity(handle(), value));
    }

    /**
     * Returns why the pass fell back, when it did.
     *
     * @return the reason, or an empty string when it did not
     */
    public String getFallbackReason() {
        long pass = handle();
        return readText("AerialPerspectivePass.getFallbackReason",
                (destination, bytes) -> NativeEngineLayerRoutes
                        .aerialPerspectivePassCopyFallbackReason(pass, destination, bytes));
    }

    /**
     * Returns how much atmosphere a view ray passes through.
     *
     * @param viewDirection the direction being looked along
     * @param distance how far
     * @param scaleHeight how fast the atmosphere thins with altitude
     * @return the air mass
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static float getAirMassForDistance(Vector3 viewDirection, float distance,
            float scaleHeight) {
        GraphicsExtension.requireBackend();
        float[] airMass = new float[1];
        GraphicsExtension.check("AerialPerspectivePass.getAirMassForDistance",
                NativeEngineLayerRoutes.aerialPerspectivePassAirMassForDistance(
                        EngineValues.floats(viewDirection, "viewDirection"), distance,
                        scaleHeight, airMass));
        return airMass[0];
    }

    /**
     * Returns how much light survives that much atmosphere, per channel.
     *
     * @param turbidity how much is in the air
     * @param airMass the air mass, as {@link #getAirMassForDistance} computes it
     * @return the transmittance per channel
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static Vector3 getTransmittance(float turbidity, float airMass) {
        GraphicsExtension.requireBackend();
        float[] transmittance = new float[3];
        GraphicsExtension.check("AerialPerspectivePass.getTransmittance",
                NativeEngineLayerRoutes.aerialPerspectivePassTransmittance(turbidity, airMass,
                        transmittance));
        return new Vector3(transmittance[0], transmittance[1], transmittance[2]);
    }
}
