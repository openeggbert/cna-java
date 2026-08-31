package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.BoundingSphere;
import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Matrix;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.List;
import java.util.Objects;

/**
 * Which lights reach which clusters.
 *
 * <p>A CNA extension, and the third of the four objects clustered forward lighting is made of.
 * The assignment takes a {@link ClusteredLightGrid} and every light's bounding sphere, and
 * produces the compressed-row structure a shader reads: one flat array of light indices, and one
 * array of offsets saying where each cluster's run of them begins. There is one more offset than
 * cluster, so a cluster's run is {@code offsets[i]} through {@code offsets[i + 1]}.
 *
 * <p><strong>A pure CPU object.</strong> It needs no device to do its work and its arrays are
 * read by copy, so nothing it returns keeps it alive. {@link ClusteredLightCompute} is the same
 * job on the GPU, filling one of these.
 *
 * <p>Like {@link ClusteredLightSet}, the parameter CNA's header calls a game is in fact a
 * graphics device; see {@code JAVA-UPSTREAM-005}.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class ClusteredLightAssignment implements AutoCloseable {

    /** {@code CNA_CLUSTERED_ASSIGNMENT_MAX_LIGHTS_EXT}: more than this wants a second grid. */
    public static final int MaxLights = 1024;

    /** CNA's own result for a buffer that could not hold the answer. */
    private static final int RESULT_BUFFER_TOO_SMALL = 14;

    private final long handle;
    private boolean closed;

    private ClusteredLightAssignment(long handle) {
        this.handle = handle;
    }

    /**
     * Creates an empty assignment.
     *
     * @param graphicsDevice the device the assignment is parented to
     * @return the assignment, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static ClusteredLightAssignment create(GraphicsDevice graphicsDevice) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] assignment = new long[1];
        GraphicsExtension.check("ClusteredLightAssignment.create",
                NativeEngineLayerRoutes.clusteredLightAssignmentCreate(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), assignment));
        return new ClusteredLightAssignment(assignment[0]);
    }

    /**
     * Sorts a set of light bounds into a grid's clusters.
     *
     * <p>The bounds are what {@link ClusteredLightSet#getBounds()} produces, so a game sorts the
     * set it already built rather than describing every light twice.
     *
     * @param grid the grid to sort into; it must have a projection
     * @param view the camera's view matrix
     * @param bounds every light's bounding sphere, in light-index order; at most
     *        {@link #MaxLights} of them
     * @throws IllegalStateException when the grid has no projection
     */
    public void assign(ClusteredLightGrid grid, Matrix view, List<BoundingSphere> bounds) {
        Objects.requireNonNull(grid, "grid");
        GraphicsExtension.check("ClusteredLightAssignment.assign",
                NativeEngineLayerRoutes.clusteredLightAssignmentAssign(open(), grid.handle(),
                        EngineValues.floats(view, "view"),
                        EngineValues.spheres(bounds, "bounds")));
    }

    /** Forgets every assignment. */
    public void clear() {
        GraphicsExtension.check("ClusteredLightAssignment.clear",
                NativeEngineLayerRoutes.clusteredLightAssignmentClear(open()));
    }

    /**
     * Takes an assignment computed elsewhere.
     *
     * <p>For a game that sorts lights itself, or reads the result back from its own compute pass.
     * CNA validates the shape rather than trusting it: the offsets must begin at zero, never go
     * backwards, and end at the index count, and every index must name a light below
     * {@code lightCount}.
     *
     * @param lightCount how many lights the indices may name
     * @param offsets where each cluster's run of indices begins; one more than the cluster count
     * @param indices the light indices, cluster by cluster
     */
    public void adopt(int lightCount, int[] offsets, int[] indices) {
        Objects.requireNonNull(offsets, "offsets");
        Objects.requireNonNull(indices, "indices");
        GraphicsExtension.check("ClusteredLightAssignment.adopt", NativeEngineLayerRoutes
                .clusteredLightAssignmentAdopt(open(), lightCount, offsets.clone(),
                        indices.clone()));
    }

    /** @return how many lights the assignment describes */
    public int getLightCount() {
        return count("ClusteredLightAssignment.getLightCount",
                NativeEngineLayerRoutes::clusteredLightAssignmentGetLightCount);
    }

    /** @return how many clusters the assignment describes */
    public int getClusterCount() {
        return count("ClusteredLightAssignment.getClusterCount",
                NativeEngineLayerRoutes::clusteredLightAssignmentGetClusterCount);
    }

    /**
     * Returns how many light references there are across every cluster.
     *
     * <p>The length of {@link #getIndices()}, and the number that says how expensive the
     * assignment was: one light touching a hundred clusters costs a hundred references.
     *
     * @return the total
     */
    public int getTotalReferenceCount() {
        return count("ClusteredLightAssignment.getTotalReferenceCount",
                NativeEngineLayerRoutes::clusteredLightAssignmentGetTotalReferenceCount);
    }

    /**
     * Returns the largest number of lights in any one cluster.
     *
     * <p>The number a shader's per-cluster loop has to be able to run, and the one a game watches
     * when it tunes the grid.
     *
     * @return the maximum
     */
    public int getMaxLightsPerCluster() {
        return count("ClusteredLightAssignment.getMaxLightsPerCluster",
                NativeEngineLayerRoutes::clusteredLightAssignmentGetMaxLightsPerCluster);
    }

    /**
     * Returns the light indices assigned to one cluster.
     *
     * @param clusterIndex which cluster, as {@link ClusteredLightGrid#getClusterIndex} numbers
     *        them
     * @return the indices into the light set
     */
    public int[] getLightsInCluster(int clusterIndex) {
        long assignment = open();
        return copy("ClusteredLightAssignment.getLightsInCluster",
                (destination, count) -> NativeEngineLayerRoutes
                        .clusteredLightAssignmentCopyLightsInCluster(
                                assignment, clusterIndex, destination, count));
    }

    /**
     * Returns the whole index array.
     *
     * @return every light index, cluster by cluster
     */
    public int[] getIndices() {
        long assignment = open();
        return copy("ClusteredLightAssignment.getIndices",
                (destination, count) -> NativeEngineLayerRoutes
                        .clusteredLightAssignmentCopyIndices(assignment, destination, count));
    }

    /**
     * Returns the whole offset array.
     *
     * @return where each cluster's run of indices begins; one more than the cluster count
     */
    public int[] getOffsets() {
        long assignment = open();
        return copy("ClusteredLightAssignment.getOffsets",
                (destination, count) -> NativeEngineLayerRoutes
                        .clusteredLightAssignmentCopyOffsets(assignment, destination, count));
    }

    /** Releases the assignment. Closing twice is a no-op. */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        GraphicsExtension.check("ClusteredLightAssignment.close",
                NativeEngineLayerRoutes.clusteredLightAssignmentDestroy(handle));
    }

    /** The native handle, for the objects in this package that fill or read an assignment. */
    long handle() {
        return open();
    }

    /** A count CNA answers about one assignment. */
    @FunctionalInterface
    private interface CountRoute {
        int call(long assignment, int[] answer);
    }

    /** One of the assignment's copy-out arrays. */
    @FunctionalInterface
    private interface CopyRoute {
        int call(int[] destination, long[] count);
    }

    private int count(String operation, CountRoute route) {
        int[] answer = new int[1];
        GraphicsExtension.check(operation, route.call(open(), answer));
        return answer[0];
    }

    private static int[] copy(String operation, CopyRoute route) {
        long[] count = new long[1];
        // A zero-capacity probe reports the count and writes nothing, so BUFFER_TOO_SMALL is the
        // expected answer to the first call rather than a failure.
        int probe = route.call(new int[0], count);
        if (probe != RESULT_BUFFER_TOO_SMALL) {
            GraphicsExtension.check(operation, probe);
        }
        int length = Math.toIntExact(count[0]);
        if (length == 0) {
            return new int[0];
        }
        int[] destination = new int[length];
        GraphicsExtension.check(operation, route.call(destination, count));
        return destination;
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This ClusteredLightAssignment is closed");
            }
        }
        return handle;
    }
}
