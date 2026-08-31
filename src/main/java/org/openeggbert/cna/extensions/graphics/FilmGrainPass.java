package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

/**
 * Animated noise over the frame.
 *
 * <p>A CNA extension: a look rather than a correction, and the cheapest one in the chain.
 *
 * <p>Created on a device, configured here, and run by a {@link RenderPipeline}. Creation
 * succeeds on a renderer that cannot run the pass; ask
 * {@link PostProcessPass#isSupported(GraphicsDevice)}.
 */
public final class FilmGrainPass extends PostProcessPass {

    private FilmGrainPass(long handle) {
        super(handle);
    }

    /**
     * Creates the pass on a device.
     *
     * @param graphicsDevice the device to compile on
     * @return the pass, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static FilmGrainPass create(GraphicsDevice graphicsDevice) {
        return new FilmGrainPass(createOn(graphicsDevice, "FilmGrainPass.create",
                NativeEngineLayerRoutes::filmGrainPassCreate));
    }

    /**
     * Returns the pass's Intensity.
     *
     * @return the value
     */
    public float getIntensity() {
        return readFloat("FilmGrainPass.getIntensity",
                NativeEngineLayerRoutes::filmGrainPassGetIntensity);
    }

    /**
     * Sets the pass's Intensity.
     *
     * <p>The value, clamped to zero through one.
     *
     * @param value the value
     */
    public void setIntensity(float value) {
        GraphicsExtension.check("FilmGrainPass.setIntensity",
                NativeEngineLayerRoutes.filmGrainPassSetIntensity(handle(), value));
    }

}
