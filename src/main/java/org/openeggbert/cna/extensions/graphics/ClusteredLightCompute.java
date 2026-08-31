package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.BoundingSphere;
import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Matrix;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * Sorts lights into clusters on the GPU, and on the CPU where it cannot.
 *
 * <p>A CNA extension: the same job {@link ClusteredLightAssignment#assign} does, moved to a
 * compute shader. It fills an assignment either way, so a game writes one code path and asks
 * afterwards which one ran.
 *
 * <p><strong>A program the renderer cannot compile still works.</strong> Like {@link GpuTimer},
 * this constructs on a renderer with no compute support, reports {@link #isSupported()} as
 * {@code false}, says why in {@link #getUnsupportedReason()}, and then does the work on the CPU
 * rather than failing. {@link #didUseCompute()} says which happened for the last assignment, so
 * a profiling overlay can report the fallback rather than a game silently paying for it.
 *
 * <p><strong>The stride is a budget, not a hint.</strong> It is the per-cluster light capacity the
 * GPU path allocates, and a scene that needs more than it overflows -- {@link #hasOverflowed()}
 * says so, and the assignment is then missing lights in the busiest clusters rather than being
 * wrong everywhere.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class ClusteredLightCompute implements AutoCloseable {

    /** CNA's own result for a buffer that could not hold the answer. */
    private static final int RESULT_BUFFER_TOO_SMALL = 14;

    private final long handle;
    private boolean closed;

    private ClusteredLightCompute(long handle) {
        this.handle = handle;
    }

    /**
     * Creates a program on a device.
     *
     * <p>Succeeds even where the renderer has no compute shaders. Ask {@link #isSupported()}
     * rather than reading a successful creation as one.
     *
     * @param graphicsDevice the device to compile on
     * @param stride the per-cluster light capacity; must be positive
     * @return the program, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static ClusteredLightCompute create(GraphicsDevice graphicsDevice, int stride) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] compute = new long[1];
        GraphicsExtension.check("ClusteredLightCompute.create",
                NativeEngineLayerRoutes.clusteredLightComputeCreate(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), stride,
                        compute));
        return new ClusteredLightCompute(compute[0]);
    }

    /**
     * Reports whether the renderer compiled the compute program.
     *
     * @return whether the GPU path is available
     */
    public boolean isSupported() {
        return flag("ClusteredLightCompute.isSupported",
                NativeEngineLayerRoutes::clusteredLightComputeIsSupported);
    }

    /**
     * Returns why the program is unsupported, in the renderer's own words.
     *
     * @return the reason, or an empty string when it is supported
     */
    public String getUnsupportedReason() {
        long compute = open();
        long[] bytes = new long[1];
        int probe = NativeEngineLayerRoutes
                .clusteredLightComputeCopyUnsupportedReason(compute, new byte[0], bytes);
        if (probe != RESULT_BUFFER_TOO_SMALL) {
            GraphicsExtension.check("ClusteredLightCompute.getUnsupportedReason", probe);
        }
        int length = Math.toIntExact(bytes[0]);
        if (length == 0) {
            return "";
        }
        byte[] destination = new byte[length];
        GraphicsExtension.check("ClusteredLightCompute.getUnsupportedReason",
                NativeEngineLayerRoutes.clusteredLightComputeCopyUnsupportedReason(
                        compute, destination, bytes));
        return new String(destination, 0, Math.toIntExact(bytes[0]), StandardCharsets.UTF_8);
    }

    /**
     * Returns the per-cluster light capacity this program was created with.
     *
     * @return the stride
     */
    public int getStride() {
        int[] stride = new int[1];
        GraphicsExtension.check("ClusteredLightCompute.getStride",
                NativeEngineLayerRoutes.clusteredLightComputeGetStride(open(), stride));
        return stride[0];
    }

    /**
     * Sorts lights into clusters, filling an assignment.
     *
     * @param grid the grid to sort into; it must have a projection
     * @param view the camera's view matrix
     * @param bounds every light's bounding sphere, in light-index order
     * @param assignment the assignment to fill
     * @throws IllegalStateException when the grid has no projection
     */
    public void assign(ClusteredLightGrid grid, Matrix view, List<BoundingSphere> bounds,
            ClusteredLightAssignment assignment) {
        Objects.requireNonNull(grid, "grid");
        Objects.requireNonNull(assignment, "assignment");
        GraphicsExtension.check("ClusteredLightCompute.assign",
                NativeEngineLayerRoutes.clusteredLightComputeAssign(open(), grid.handle(),
                        EngineValues.floats(view, "view"),
                        EngineValues.spheres(bounds, "bounds"), assignment.handle()));
    }

    /**
     * Reports whether the last assignment ran on the GPU.
     *
     * <p>A record of what happened rather than a capability: {@link #isSupported()} is the
     * capability, and this means nothing before the first {@link #assign}.
     *
     * @return whether compute was used
     */
    public boolean didUseCompute() {
        return flag("ClusteredLightCompute.didUseCompute",
                NativeEngineLayerRoutes::clusteredLightComputeUsedCompute);
    }

    /**
     * Reports whether a cluster wanted more lights than the stride allows.
     *
     * @return whether the last assignment overflowed
     */
    public boolean hasOverflowed() {
        return flag("ClusteredLightCompute.hasOverflowed",
                NativeEngineLayerRoutes::clusteredLightComputeHasOverflowed);
    }

    /** Releases the program. Closing twice is a no-op. */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        GraphicsExtension.check("ClusteredLightCompute.close",
                NativeEngineLayerRoutes.clusteredLightComputeDestroy(handle));
    }

    /** A boolean CNA answers about one program. */
    @FunctionalInterface
    private interface FlagRoute {
        int call(long compute, boolean[] answer);
    }

    private boolean flag(String operation, FlagRoute route) {
        boolean[] answer = new boolean[1];
        GraphicsExtension.check(operation, route.call(open(), answer));
        return answer[0];
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This ClusteredLightCompute is closed");
            }
        }
        return handle;
    }
}
