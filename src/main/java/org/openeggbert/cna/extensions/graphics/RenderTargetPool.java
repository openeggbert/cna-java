package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.DepthFormat;
import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.SurfaceFormat;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.Objects;

/**
 * Reuses render targets across a frame instead of allocating them.
 *
 * <p>A CNA extension. A post-process chain wants a scratch target per step and they mostly have
 * the same shape, so allocating one per pass per frame is the difference between a steady
 * frame time and a stuttering one. A pool hands out a target for a shape and a slot, and
 * {@link #reset()} makes them all available again for the next frame.
 *
 * <p><strong>The slot is what makes two targets of the same shape different.</strong> Two passes
 * that both want 1280 by 720 in the same format need <em>two</em> targets if one reads while the
 * other writes, and the slot is how a caller says so.
 *
 * <p><strong>An acquired target is a counted borrow.</strong> Disposing the returned
 * {@link Texture2D} gives it back; the pool refuses {@link #reset()} and {@link #close()} until
 * every one has been. That is what stops a frame reusing a target another pass is still reading.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class RenderTargetPool implements AutoCloseable {

    private final long handle;
    private boolean closed;

    private RenderTargetPool(long handle) {
        this.handle = handle;
    }

    /**
     * Creates a pool on a device.
     *
     * @param graphicsDevice the device the targets belong to
     * @return the pool, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static RenderTargetPool create(GraphicsDevice graphicsDevice) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] pool = new long[1];
        GraphicsExtension.check("RenderTargetPool.create",
                NativeEngineLayerRoutes.renderTargetPoolCreate(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), pool));
        return new RenderTargetPool(pool[0]);
    }

    /**
     * Acquires a target of a shape, creating one only if the pool has none free.
     *
     * @param graphicsDevice the device the pool belongs to
     * @param width the width in pixels; must be positive
     * @param height the height in pixels; must be positive
     * @param format the surface format
     * @param depthFormat the depth format
     * @param slot which of several targets of this shape is wanted
     * @return the target, which the caller disposes to give it back
     */
    public Texture2D acquire(GraphicsDevice graphicsDevice, int width, int height,
            SurfaceFormat format, DepthFormat depthFormat, int slot) {
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        Objects.requireNonNull(format, "format");
        Objects.requireNonNull(depthFormat, "depthFormat");
        long[] target = new long[1];
        GraphicsExtension.check("RenderTargetPool.acquire",
                NativeEngineLayerRoutes.renderTargetPoolAcquire(open(), width, height,
                        format.ordinal(), depthFormat.ordinal(), slot, target));
        if (target[0] == 0L) {
            return null;
        }
        return NativeBindings.createBorrowedRenderTarget(graphicsDevice, target[0]);
    }

    /**
     * Makes every target available again.
     *
     * @throws org.openeggbert.cna.internal.CnaNativeException when a target borrowed from this
     *         pool has not been disposed
     */
    public void reset() {
        GraphicsExtension.check("RenderTargetPool.reset",
                NativeEngineLayerRoutes.renderTargetPoolReset(open()));
    }

    /** @return how many targets the pool holds */
    public long getTargetCount() {
        long[] count = new long[1];
        GraphicsExtension.check("RenderTargetPool.getTargetCount",
                NativeEngineLayerRoutes.renderTargetPoolGetTargetCount(open(), count));
        return count[0];
    }

    /** @return roughly how many bytes of GPU memory the pool's targets hold */
    public long getEstimatedBytes() {
        long[] bytes = new long[1];
        GraphicsExtension.check("RenderTargetPool.getEstimatedBytes",
                NativeEngineLayerRoutes.renderTargetPoolGetEstimatedBytes(open(), bytes));
        return bytes[0];
    }

    /**
     * Releases the pool and its targets. Closing twice is a no-op.
     *
     * <p>Refused while a target borrowed from it has not been disposed, and a refusal leaves the
     * pool exactly as it was.
     */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
        }
        GraphicsExtension.check("RenderTargetPool.close",
                NativeEngineLayerRoutes.renderTargetPoolDestroy(handle));
        synchronized (this) {
            closed = true;
        }
    }

    /**
     * Adopts a pool handle the engine layer lent.
     *
     * <p>A {@link PostProcessChain} lends its own pool as a <em>counted</em> borrow: destroying
     * the chain is refused while the borrow is outstanding, and {@link #close()} is what returns
     * it. The facade is disposed exactly as an owned pool is, and the same call means "give the
     * borrow back" here and "destroy the pool" there -- which is CNA's own arrangement, not a
     * conflation invented in Java.
     *
     * @param handle the handle CNA just returned
     * @return the facade, which the caller closes
     */
    static RenderTargetPool adopt(long handle) {
        return new RenderTargetPool(handle);
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This RenderTargetPool is closed");
            }
        }
        return handle;
    }
}
