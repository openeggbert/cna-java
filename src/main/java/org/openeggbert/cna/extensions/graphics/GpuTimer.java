package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Measures how long the GPU spent on a range of work.
 *
 * <p>A CNA extension. XNA has no way to ask this at all: a game could time the CPU around a draw
 * and learn nothing, because the call returns long before the GPU has finished. This wraps the
 * renderer's timer query, which measures the other side.
 *
 * <p><strong>A timer that the renderer cannot supply still exists.</strong> That is deliberate on
 * CNA's part and preserved here: {@link #create} succeeds, {@link #isSupported()} answers
 * {@code false}, and {@link #getUnsupportedReason()} says why in the renderer's own words. A
 * profiling overlay can then report "no GPU timer: <em>reason</em>" rather than disappearing, and
 * a game does not need a build-time switch for it. {@link #begin()} and {@link #end()} do nothing
 * on such a timer rather than failing, so the calls can stay in the frame unconditionally.
 *
 * <p><strong>Reading a result does not block.</strong> A GPU timer query is answered whenever the
 * GPU gets to it, which is typically one or more frames later. {@link #poll()} collects a
 * finished result and answers whether it collected one; {@link #getLastMilliseconds()} returns
 * the most recent collected value, and zero before the first. A game polls each frame and shows
 * the last number it got.
 *
 * <p><strong>There is deliberately no {@code try}-with-resources range.</strong> CNA's
 * {@link #begin()} does nothing when a range is already open, so a nested scope object would
 * silently measure the wrong thing rather than refuse. {@link #isOpen()} is how a game checks
 * instead.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class GpuTimer implements AutoCloseable {

    /** CNA's own result for a buffer that could not hold the answer. */
    private static final int RESULT_BUFFER_TOO_SMALL = 14;

    private final long handle;
    private boolean closed;

    private GpuTimer(long handle) {
        this.handle = handle;
    }

    /**
     * Creates a timer on a device.
     *
     * <p>Succeeds even where the renderer has no timer query. Ask {@link #isSupported()} rather
     * than reading a successful creation as one.
     *
     * @param graphicsDevice the device to measure on
     * @return the timer, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static GpuTimer create(GraphicsDevice graphicsDevice) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] timer = new long[1];
        GraphicsExtension.check("GpuTimer.create", NativeEngineLayerRoutes.gpuTimerCreate(
                NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), timer));
        return new GpuTimer(timer[0]);
    }

    /**
     * Reports whether the renderer supplied a timer query.
     *
     * @return whether this timer measures anything
     */
    public boolean isSupported() {
        return flag("GpuTimer.isSupported", NativeEngineLayerRoutes::gpuTimerIsSupported);
    }

    /**
     * Returns why the timer is unsupported, in the renderer's own words.
     *
     * @return the reason, or an empty string when the timer is supported
     */
    public String getUnsupportedReason() {
        long timer = open();
        long[] bytes = new long[1];
        // A zero-capacity probe reports the byte count and writes nothing, and CNA writes no
        // partial string, so BUFFER_TOO_SMALL is an expected answer rather than a failure.
        int probe = NativeEngineLayerRoutes
                .gpuTimerCopyUnsupportedReason(timer, new byte[0], bytes);
        if (probe != RESULT_BUFFER_TOO_SMALL) {
            GraphicsExtension.check("GpuTimer.getUnsupportedReason", probe);
        }
        int length = Math.toIntExact(bytes[0]);
        if (length == 0) {
            return "";
        }
        byte[] destination = new byte[length];
        GraphicsExtension.check("GpuTimer.getUnsupportedReason", NativeEngineLayerRoutes
                .gpuTimerCopyUnsupportedReason(timer, destination, bytes));
        return new String(destination, 0, Math.toIntExact(bytes[0]), StandardCharsets.UTF_8);
    }

    /**
     * Opens the timed range.
     *
     * <p>Does nothing when the timer is unsupported or a range is already open, which is what
     * lets the call stay in a frame unconditionally.
     */
    public void begin() {
        GraphicsExtension.check("GpuTimer.begin",
                NativeEngineLayerRoutes.gpuTimerBegin(open()));
    }

    /**
     * Closes the timed range.
     *
     * <p>Does nothing when the timer is unsupported or no range is open.
     */
    public void end() {
        GraphicsExtension.check("GpuTimer.end", NativeEngineLayerRoutes.gpuTimerEnd(open()));
    }

    /**
     * Reports whether a timed range is currently open.
     *
     * @return whether {@link #begin()} has been called without a matching {@link #end()}
     */
    public boolean isOpen() {
        return flag("GpuTimer.isOpen", NativeEngineLayerRoutes::gpuTimerIsOpen);
    }

    /**
     * Reports whether the last closed range can be collected without blocking.
     *
     * @return whether {@link #poll()} would collect something
     */
    public boolean isResultAvailable() {
        return flag("GpuTimer.isResultAvailable",
                NativeEngineLayerRoutes::gpuTimerIsResultAvailable);
    }

    /**
     * Collects a finished result, without blocking.
     *
     * @return whether a new result was collected
     */
    public boolean poll() {
        return flag("GpuTimer.poll", NativeEngineLayerRoutes::gpuTimerPoll);
    }

    /**
     * Returns the most recently collected GPU time.
     *
     * @return the elapsed milliseconds, or zero before the first result
     */
    public double getLastMilliseconds() {
        double[] milliseconds = new double[1];
        GraphicsExtension.check("GpuTimer.getLastMilliseconds",
                NativeEngineLayerRoutes.gpuTimerGetLastMilliseconds(open(), milliseconds));
        return milliseconds[0];
    }

    /**
     * Returns how many results have been collected.
     *
     * @return the sample count
     */
    public int getSampleCount() {
        int[] samples = new int[1];
        GraphicsExtension.check("GpuTimer.getSampleCount",
                NativeEngineLayerRoutes.gpuTimerGetSampleCount(open(), samples));
        return samples[0];
    }

    /** Releases the timer and its query object. Closing twice is a no-op. */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        GraphicsExtension.check("GpuTimer.close",
                NativeEngineLayerRoutes.gpuTimerDestroy(handle));
    }

    /** A boolean CNA answers about one timer. */
    @FunctionalInterface
    private interface FlagRoute {
        int call(long timer, boolean[] answer);
    }

    private boolean flag(String operation, FlagRoute route) {
        boolean[] answer = new boolean[1];
        GraphicsExtension.check(operation, route.call(open(), answer));
        return answer[0];
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This GpuTimer is closed");
            }
        }
        return handle;
    }
}
