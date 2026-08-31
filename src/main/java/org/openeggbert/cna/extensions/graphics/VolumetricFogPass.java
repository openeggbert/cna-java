package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

/**
 * Fog that light actually travels through.
 *
 * <p>A CNA extension. Unlike {@link HeightFogPass} this one is lit: {@link #setLight} gives it a
 * direction, a colour and a shadow map, so the fog is darker where the shadow falls, which is
 * what makes a light shaft look like a light shaft.
 *
 * <p>{@link #getAnisotropy()} is the phase function's forward bias: zero scatters evenly, and
 * towards one the fog brightens sharply when looking into the light.
 *
 * <p>Created on a device, configured here, and run by a {@link RenderPipeline}. Creation
 * succeeds on a renderer that cannot run the pass; ask
 * {@link PostProcessPass#isSupported(GraphicsDevice)}.
 */
public final class VolumetricFogPass extends PostProcessPass {

    private VolumetricFogPass(long handle) {
        super(handle);
    }

    /**
     * Creates the pass on a device.
     *
     * @param graphicsDevice the device to compile on
     * @return the pass, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static VolumetricFogPass create(GraphicsDevice graphicsDevice) {
        return new VolumetricFogPass(createOn(graphicsDevice, "VolumetricFogPass.create",
                NativeEngineLayerRoutes::volumetricFogPassCreate));
    }

    /**
     * Returns the pass's Anisotropy.
     *
     * @return the value
     */
    public float getAnisotropy() {
        return readFloat("VolumetricFogPass.getAnisotropy",
                NativeEngineLayerRoutes::volumetricFogPassGetAnisotropy);
    }

    /**
     * Sets the pass's Anisotropy.
     *
     * <p>The value, clamped to -0.95 through 0.95 -- the only two-sided clamp in this phase with a negative lower bound, because scattering runs from fully backward to fully forward and the poles are singular.
     *
     * @param value the value
     */
    public void setAnisotropy(float value) {
        GraphicsExtension.check("VolumetricFogPass.setAnisotropy",
                NativeEngineLayerRoutes.volumetricFogPassSetAnisotropy(handle(), value));
    }

    /**
     * Returns the pass's Density.
     *
     * @return the value
     */
    public float getDensity() {
        return readFloat("VolumetricFogPass.getDensity",
                NativeEngineLayerRoutes::volumetricFogPassGetDensity);
    }

    /**
     * Sets the pass's Density.
     *
     * <p>The value, ignored when negative, but zero is accepted -- no fog is a legitimate setting.
     *
     * @param value the value
     */
    public void setDensity(float value) {
        GraphicsExtension.check("VolumetricFogPass.setDensity",
                NativeEngineLayerRoutes.volumetricFogPassSetDensity(handle(), value));
    }

    /**
     * Returns the pass's Range.
     *
     * @return the value
     */
    public float getRange() {
        return readFloat("VolumetricFogPass.getRange",
                NativeEngineLayerRoutes::volumetricFogPassGetRange);
    }

    /**
     * Sets the pass's Range.
     *
     * <p>The value, ignored when not positive -- a zero range has no volume to march through, so unlike the density beside it this guard rejects zero as well as negatives.
     *
     * @param value the value
     */
    public void setRange(float value) {
        GraphicsExtension.check("VolumetricFogPass.setRange",
                NativeEngineLayerRoutes.volumetricFogPassSetRange(handle(), value));
    }

    /**
     * Gives the fog a light to scatter, and the shadow that shapes it.
     *
     * <p>The shadow map is borrowed and is not retained here: a game that disposes a map the fog
     * still names has made a mistake this projection cannot see.
     *
     * @param shadowMap the shadow the fog is darkened by, or {@code null} for none
     * @param lightDirection the direction the light travels
     * @param lightColor the light's linear RGB colour
     */
    public void setLight(ShadowMap shadowMap, Vector3 lightDirection, Vector3 lightColor) {
        GraphicsExtension.check("VolumetricFogPass.setLight",
                NativeEngineLayerRoutes.volumetricFogPassSetLight(handle(),
                        shadowMap == null ? 0L : shadowMap.handleForBorrow(),
                        EngineValues.floats(lightDirection, "lightDirection"),
                        EngineValues.floats(lightColor, "lightColor")));
    }
}
