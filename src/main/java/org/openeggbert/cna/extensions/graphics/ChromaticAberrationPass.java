package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

/**
 * Splits the colour channels towards the edges of the frame, as a lens does.
 *
 * <p>A CNA extension. The strength is clamped hard on purpose -- past a fraction of a percent it
 * stops reading as a lens and starts reading as a fault.
 *
 * <p>Created on a device, configured here, and run by a {@link RenderPipeline}. Creation
 * succeeds on a renderer that cannot run the pass; ask
 * {@link PostProcessPass#isSupported(GraphicsDevice)}.
 */
public final class ChromaticAberrationPass extends PostProcessPass {

    private ChromaticAberrationPass(long handle) {
        super(handle);
    }

    /**
     * Creates the pass on a device.
     *
     * @param graphicsDevice the device to compile on
     * @return the pass, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static ChromaticAberrationPass create(GraphicsDevice graphicsDevice) {
        return new ChromaticAberrationPass(createOn(graphicsDevice, "ChromaticAberrationPass.create",
                NativeEngineLayerRoutes::chromaticAberrationPassCreate));
    }

    /**
     * Returns the pass's Strength.
     *
     * @return the value
     */
    public float getStrength() {
        return readFloat("ChromaticAberrationPass.getStrength",
                NativeEngineLayerRoutes::chromaticAberrationPassGetStrength);
    }

    /**
     * Sets the pass's Strength.
     *
     * <p>The value, clamped to zero through 0.1.
     *
     * @param value the value
     */
    public void setStrength(float value) {
        GraphicsExtension.check("ChromaticAberrationPass.setStrength",
                NativeEngineLayerRoutes.chromaticAberrationPassSetStrength(handle(), value));
    }

}
