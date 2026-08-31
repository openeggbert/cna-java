package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

/**
 * Fast approximate anti-aliasing, over the finished frame.
 *
 * <p>A CNA extension. XNA anti-aliases by asking the device for a multisampled back buffer, which
 * costs memory and bandwidth on every draw; this smooths edges afterwards, from the colour buffer
 * alone. {@link #getEdgeThreshold()} is what decides which edges it touches, and
 * {@link #getEdgeThresholdForQuality} is CNA's own table.
 *
 * <p>Created on a device, configured here, and run by a {@link RenderPipeline}. Creation
 * succeeds on a renderer that cannot run the pass; ask
 * {@link PostProcessPass#isSupported(GraphicsDevice)}.
 */
public final class FxaaPass extends PostProcessPass {

    private FxaaPass(long handle) {
        super(handle);
    }

    /**
     * Creates the pass on a device.
     *
     * @param graphicsDevice the device to compile on
     * @return the pass, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static FxaaPass create(GraphicsDevice graphicsDevice) {
        return new FxaaPass(createOn(graphicsDevice, "FxaaPass.create",
                NativeEngineLayerRoutes::fxaaPassCreate));
    }

    /**
     * Returns the pass's EdgeThreshold.
     *
     * @return the value
     */
    public float getEdgeThreshold() {
        return readFloat("FxaaPass.getEdgeThreshold",
                NativeEngineLayerRoutes::fxaaPassGetEdgeThreshold);
    }

    /**
     * Sets the pass's EdgeThreshold.
     *
     * <p>The value, stored as given -- the pass corrects nothing, though the settings bag that can drive it floors the same value at `CNA_RENDER_PIPELINE_MINIMUM_FXAA_EDGE_THRESHOLD_EXT`; the two are different surfaces and only one of them corrects.
     *
     * @param value the value
     */
    public void setEdgeThreshold(float value) {
        GraphicsExtension.check("FxaaPass.setEdgeThreshold",
                NativeEngineLayerRoutes.fxaaPassSetEdgeThreshold(handle(), value));
    }

    /**
     * Returns the edge threshold a quality preset selects.
     *
     * @param quality the preset
     * @return the threshold
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static float getEdgeThresholdForQuality(RenderQuality quality) {
        GraphicsExtension.requireBackend();
        java.util.Objects.requireNonNull(quality, "quality");
        float[] threshold = new float[1];
        GraphicsExtension.check("FxaaPass.getEdgeThresholdForQuality",
                NativeEngineLayerRoutes.fxaaPassEdgeThresholdForQuality(quality.ordinal(),
                        threshold));
        return threshold[0];
    }

    /**
     * Returns the fragment shader CNA runs.
     *
     * @return the GLSL
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static String getFragmentGlsl() {
        GraphicsExtension.requireBackend();
        return readText("FxaaPass.getFragmentGlsl",
                NativeEngineLayerRoutes::fxaaPassCopyFragmentGlsl);
    }
}
