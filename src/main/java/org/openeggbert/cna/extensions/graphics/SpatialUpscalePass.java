package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.Objects;

/**
 * Scales a smaller frame up to the display, sharply.
 *
 * <p>A CNA extension. Rendering at less than the display's resolution and stretching the result
 * is how a game buys frame rate, and a plain bilinear stretch looks soft; this is an edge-aware
 * filter that keeps the edges crisp.
 *
 * <p><strong>Not a {@link PostProcessPass}.</strong> CNA gives it its own handle kind, its own
 * release route and a {@link #draw} that takes both sizes rather than one -- because upscaling is
 * the one pass whose input and output are deliberately different sizes.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class SpatialUpscalePass implements AutoCloseable {

    private final long handle;
    private boolean closed;

    private SpatialUpscalePass(long handle) {
        this.handle = handle;
    }

    /**
     * Creates the pass on a device.
     *
     * @param graphicsDevice the device to compile on
     * @return the pass, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static SpatialUpscalePass create(GraphicsDevice graphicsDevice) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] pass = new long[1];
        GraphicsExtension.check("SpatialUpscalePass.create",
                NativeEngineLayerRoutes.spatialUpscalePassCreate(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), pass));
        return new SpatialUpscalePass(pass[0]);
    }

    /**
     * Reports whether two sizes are the same, so upscaling would do nothing.
     *
     * <p>Worth asking: a game that renders at the display's own size should skip the pass rather
     * than pay for a filter that cannot change anything.
     *
     * @param sourceWidth the source width in pixels
     * @param sourceHeight the source height in pixels
     * @param targetWidth the target width in pixels
     * @param targetHeight the target height in pixels
     * @return whether the scale is one to one
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static boolean isIdentityScale(int sourceWidth, int sourceHeight, int targetWidth,
            int targetHeight) {
        GraphicsExtension.requireBackend();
        boolean[] identity = new boolean[1];
        GraphicsExtension.check("SpatialUpscalePass.isIdentityScale",
                NativeEngineLayerRoutes.spatialUpscalePassIsIdentityScale(sourceWidth,
                        sourceHeight, targetWidth, targetHeight, identity));
        return identity[0];
    }

    /** @return how hard the filter sharpens, from zero to one */
    public float getSharpness() {
        float[] sharpness = new float[1];
        GraphicsExtension.check("SpatialUpscalePass.getSharpness",
                NativeEngineLayerRoutes.spatialUpscalePassGetSharpness(open(), sharpness));
        return sharpness[0];
    }

    /**
     * Sets how hard the filter sharpens.
     *
     * <p>Clamped to zero through one.
     *
     * @param sharpness the sharpening amount
     */
    public void setSharpness(float sharpness) {
        GraphicsExtension.check("SpatialUpscalePass.setSharpness",
                NativeEngineLayerRoutes.spatialUpscalePassSetSharpness(open(), sharpness));
    }

    /** @return whether the filter follows edges rather than sharpening everything alike */
    public boolean isEdgeAdaptive() {
        boolean[] adaptive = new boolean[1];
        GraphicsExtension.check("SpatialUpscalePass.isEdgeAdaptive",
                NativeEngineLayerRoutes.spatialUpscalePassGetEdgeAdaptive(open(), adaptive));
        return adaptive[0];
    }

    /**
     * Turns edge-aware filtering on or off.
     *
     * @param adaptive whether to follow edges
     */
    public void setEdgeAdaptive(boolean adaptive) {
        GraphicsExtension.check("SpatialUpscalePass.setEdgeAdaptive",
                NativeEngineLayerRoutes.spatialUpscalePassSetEdgeAdaptive(open(), adaptive));
    }

    /**
     * Scales a source frame onto the current target.
     *
     * @param source the frame to scale; borrowed for the call
     * @param sourceWidth the source width in pixels
     * @param sourceHeight the source height in pixels
     * @param targetWidth the target width in pixels
     * @param targetHeight the target height in pixels
     */
    public void draw(Texture2D source, int sourceWidth, int sourceHeight, int targetWidth,
            int targetHeight) {
        Objects.requireNonNull(source, "source");
        GraphicsExtension.check("SpatialUpscalePass.draw",
                NativeEngineLayerRoutes.spatialUpscalePassDraw(open(),
                        NativeBindings.nativeResourceHandle(source), sourceWidth, sourceHeight,
                        targetWidth, targetHeight));
    }

    /** Releases the pass. Closing twice is a no-op. */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        GraphicsExtension.check("SpatialUpscalePass.close",
                NativeEngineLayerRoutes.spatialUpscalePassDestroy(handle));
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This SpatialUpscalePass is closed");
            }
        }
        return handle;
    }
}
