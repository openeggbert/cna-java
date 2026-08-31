package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

/**
 * A post-process pass that redraws the frame as ASCII characters.
 *
 * <p>A CNA extension, and the one pass in the layer that is a look rather than a correction:
 * bloom, tonemapping and the rest exist to make an image right, and this one exists to make it a
 * different image on purpose.
 *
 * <p>Creation succeeds on a renderer that cannot run the pass; ask
 * {@link PostProcessPass#isSupported(GraphicsDevice)}.
 *
 * <p><strong>{@code cna_ascii_pass_get_effect} is deliberately not bound.</strong> It lends the
 * pass's own effect on terms that make a Java facade unsafe: the header says the handle does not
 * keep the pass alive and that the pass must outlive it, so an object holding one would dangle the
 * moment the pass was closed, with nothing to notice. A game that wants its own shader has
 * {@link EffectPass} and {@link ShaderEffectFactory}.
 */
public final class AsciiPass extends PostProcessPass {

    private AsciiPass(long handle) {
        super(handle);
    }

    /**
     * Creates the pass on a device.
     *
     * @param graphicsDevice the device to compile on
     * @return the pass, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static AsciiPass create(GraphicsDevice graphicsDevice) {
        return new AsciiPass(createOn(graphicsDevice, "AsciiPass.create",
                NativeEngineLayerRoutes::asciiPassCreate));
    }
}
