package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

/**
 * Copies the frame from one target to the next, unchanged.
 *
 * <p>A CNA extension, and the pass with no settings at all: it exists so a chain can end
 * somewhere definite, and so a game can put a known-cheap step between two passes it is timing.
 *
 * <p>Created on a device, and run by a {@link RenderPipeline}. Creation succeeds on a renderer
 * that cannot run the pass; ask {@link PostProcessPass#isSupported(GraphicsDevice)}.
 */
public final class BlitPass extends PostProcessPass {

    private BlitPass(long handle) {
        super(handle);
    }

    /**
     * Creates the pass on a device.
     *
     * @param graphicsDevice the device to compile on
     * @return the pass, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static BlitPass create(GraphicsDevice graphicsDevice) {
        return new BlitPass(createOn(graphicsDevice, "BlitPass.create",
                NativeEngineLayerRoutes::blitPassCreate));
    }
}
