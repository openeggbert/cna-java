package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.RenderTarget2D;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.Objects;

/**
 * Binds a render target and puts the previous one back.
 *
 * <p>A CNA extension. XNA's {@code SetRenderTarget} has no counterpart to it, so every pass that
 * borrows the device has to remember what was bound and restore it -- and the one that forgets is
 * the one whose bug shows up three passes later. This records the binding, replaces it, and puts
 * it back when the scope ends.
 *
 * <p><strong>Scopes nest and must close in reverse order.</strong> An out-of-order close is
 * refused and changes neither the scope nor the binding, so a mistake is diagnosable rather than
 * silently leaving the wrong target bound. Java's try-with-resources closes in reverse order by
 * construction, which is why this is an {@link AutoCloseable} rather than a pair of calls.
 *
 * <p>The handle is owned; {@link #close()} ends the scope and closing twice is a no-op.
 */
public final class ScopedRenderTarget implements AutoCloseable {

    private final long handle;
    private boolean closed;

    private ScopedRenderTarget(long handle) {
        this.handle = handle;
    }

    /**
     * Records the current binding and binds a destination.
     *
     * @param graphicsDevice the device to bind on
     * @param destination the target to bind, which must be one this game owns, or {@code null}
     *        for the back buffer
     * @return the scope, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static ScopedRenderTarget begin(GraphicsDevice graphicsDevice,
            RenderTarget2D destination) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] scope = new long[1];
        GraphicsExtension.check("ScopedRenderTarget.begin",
                NativeEngineLayerRoutes.scopedRenderTargetBegin(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice),
                        destination == null ? 0L
                                : NativeBindings.nativeResourceHandle(destination), scope));
        return new ScopedRenderTarget(scope[0]);
    }

    /**
     * Reports whether the scope recorded a previous binding to restore.
     *
     * <p>A scope opened when nothing was bound has nothing to put back, and says so rather than
     * restoring a target that was never there.
     *
     * @return whether there is a binding to restore
     */
    public boolean hasRecordedPrevious() {
        boolean[] recorded = new boolean[1];
        GraphicsExtension.check("ScopedRenderTarget.hasRecordedPrevious",
                NativeEngineLayerRoutes.scopedRenderTargetGetHasRecordedPrevious(open(),
                        recorded));
        return recorded[0];
    }

    /**
     * Ends the scope and restores the previous binding. Closing twice is a no-op.
     *
     * @throws IllegalStateException when a scope opened after this one is still open
     */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
        }
        // Marked closed only after CNA agrees: an out-of-order end is refused and changes
        // nothing, so the scope is still open and still has to be closed in the right order.
        GraphicsExtension.check("ScopedRenderTarget.close",
                NativeEngineLayerRoutes.scopedRenderTargetEnd(handle));
        synchronized (this) {
            closed = true;
        }
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This ScopedRenderTarget is closed");
            }
        }
        return handle;
    }
}
