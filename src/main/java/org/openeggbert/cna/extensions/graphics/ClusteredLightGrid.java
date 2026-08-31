package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.BoundingBox;
import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.Objects;

/**
 * How the view frustum is cut into clusters.
 *
 * <p>A CNA extension, and the second of the four objects clustered forward lighting is made of.
 * The grid is a tile count across the screen and a count of depth slices into it; the slices are
 * spaced logarithmically between the near and far planes, so a cluster covers roughly the same
 * proportion of depth wherever it sits, which is what keeps a light near the camera from
 * landing in every slice at once.
 *
 * <p><strong>A grid has no shape until it has a projection.</strong> {@link #setProjection} is
 * what gives it one; before that {@link #getClusterBounds} refuses and an assignment cannot sort
 * into it. {@link #hasProjection()} is how a game asks.
 *
 * <p><strong>There is one more slice boundary than slice.</strong> {@link #getSliceDistance}
 * accepts the slice count itself and answers the far edge of the last slice -- which is the far
 * plane -- because a caller that rejected it would have no way to name it.
 *
 * <p>Like {@link ClusteredLightSet}, the parameter CNA's header calls a game is in fact a
 * graphics device; see {@code JAVA-UPSTREAM-005}.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class ClusteredLightGrid implements AutoCloseable {

    /** {@code CNA_CLUSTER_GRID_MAX_TILES_PER_AXIS_EXT}. */
    public static final int MaxTilesPerAxis = 128;

    /** {@code CNA_CLUSTER_GRID_MAX_SLICE_COUNT_EXT}. */
    public static final int MaxSliceCount = 256;

    private final long handle;
    private boolean closed;

    private ClusteredLightGrid(long handle) {
        this.handle = handle;
    }

    /**
     * Creates a grid of a given shape.
     *
     * <p>Every dimension is refused rather than clamped when it is out of range: the cluster
     * count is what the light-index list is sized from, so a silently corrected grid would be a
     * different grid from the one a shader was compiled against.
     *
     * @param graphicsDevice the device the grid is parented to
     * @param tilesX tiles along X, from one to {@link #MaxTilesPerAxis}
     * @param tilesY tiles along Y, in the same range
     * @param sliceCount depth slices, from one to {@link #MaxSliceCount}
     * @return the grid, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static ClusteredLightGrid create(GraphicsDevice graphicsDevice, int tilesX, int tilesY,
            int sliceCount) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] grid = new long[1];
        GraphicsExtension.check("ClusteredLightGrid.create",
                NativeEngineLayerRoutes.clusteredLightGridCreate(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice),
                        tilesX, tilesY, sliceCount, grid));
        return new ClusteredLightGrid(grid[0]);
    }

    /** @return the tile count along X */
    public int getTilesX() {
        return count("ClusteredLightGrid.getTilesX",
                NativeEngineLayerRoutes::clusteredLightGridGetTilesX);
    }

    /** @return the tile count along Y */
    public int getTilesY() {
        return count("ClusteredLightGrid.getTilesY",
                NativeEngineLayerRoutes::clusteredLightGridGetTilesY);
    }

    /** @return the depth-slice count */
    public int getSliceCount() {
        return count("ClusteredLightGrid.getSliceCount",
                NativeEngineLayerRoutes::clusteredLightGridGetSliceCount);
    }

    /** @return how many clusters the grid holds */
    public int getClusterCount() {
        return count("ClusteredLightGrid.getClusterCount",
                NativeEngineLayerRoutes::clusteredLightGridGetClusterCount);
    }

    /**
     * Returns the flat index of a cluster coordinate.
     *
     * <p>The index a shader looks a cluster up by, and the one an assignment's arrays are in.
     *
     * @param x tile along X
     * @param y tile along Y
     * @param slice depth slice
     * @return the flat index
     */
    public int getClusterIndex(int x, int y, int slice) {
        int[] index = new int[1];
        GraphicsExtension.check("ClusteredLightGrid.getClusterIndex",
                NativeEngineLayerRoutes.clusteredLightGridClusterIndex(open(), x, y, slice, index));
        return index[0];
    }

    /**
     * Gives the grid its shape from a camera projection.
     *
     * @param projection the camera's projection matrix; it must be invertible
     * @param nearPlane the near distance; must be positive
     * @param farPlane the far distance; must exceed the near
     */
    public void setProjection(Matrix projection, float nearPlane, float farPlane) {
        GraphicsExtension.check("ClusteredLightGrid.setProjection",
                NativeEngineLayerRoutes.clusteredLightGridSetProjection(open(),
                        EngineValues.floats(projection, "projection"), nearPlane, farPlane));
    }

    /**
     * Reports whether the grid has been given a projection.
     *
     * @return whether it has a shape yet
     */
    public boolean hasProjection() {
        boolean[] has = new boolean[1];
        GraphicsExtension.check("ClusteredLightGrid.hasProjection",
                NativeEngineLayerRoutes.clusteredLightGridHasProjection(open(), has));
        return has[0];
    }

    /** @return the near distance the grid was given */
    public float getNearPlane() {
        return distance("ClusteredLightGrid.getNearPlane",
                NativeEngineLayerRoutes::clusteredLightGridGetNearPlane);
    }

    /** @return the far distance the grid was given */
    public float getFarPlane() {
        return distance("ClusteredLightGrid.getFarPlane",
                NativeEngineLayerRoutes::clusteredLightGridGetFarPlane);
    }

    /**
     * Returns the inverse of the projection the grid was given.
     *
     * @return the inverse matrix
     */
    public Matrix getInverseProjection() {
        float[] matrix = new float[EngineValues.MATRIX_LEAVES];
        GraphicsExtension.check("ClusteredLightGrid.getInverseProjection",
                NativeEngineLayerRoutes.clusteredLightGridGetInverseProjection(open(), matrix));
        return EngineValues.matrix(matrix, 0);
    }

    /**
     * Returns the view distance at which a depth slice begins.
     *
     * @param slice the slice boundary, from zero to the slice count <em>inclusive</em>; the count
     *        itself names the far edge of the last slice
     * @return the distance, or zero when no projection has been set
     */
    public float getSliceDistance(int slice) {
        float[] distance = new float[1];
        GraphicsExtension.check("ClusteredLightGrid.getSliceDistance",
                NativeEngineLayerRoutes.clusteredLightGridSliceDistance(open(), slice, distance));
        return distance[0];
    }

    /**
     * Returns which slice covers a view distance.
     *
     * <p><strong>Clamped rather than refused</strong>, exactly as CNA clamps it: a point in front
     * of the near plane belongs to the first slice and one beyond the far plane to the last,
     * which is what a renderer wants when a light straddles the frustum edge.
     *
     * @param viewDistance the distance to place
     * @return the slice index
     */
    public int getSliceForViewDistance(float viewDistance) {
        int[] slice = new int[1];
        GraphicsExtension.check("ClusteredLightGrid.getSliceForViewDistance",
                NativeEngineLayerRoutes.clusteredLightGridSliceForViewDistance(
                        open(), viewDistance, slice));
        return slice[0];
    }

    /**
     * Returns the view-space bounds of one cluster.
     *
     * @param x tile along X
     * @param y tile along Y
     * @param slice depth slice
     * @return the bounds, in view space
     * @throws IllegalStateException when the grid has no projection yet
     */
    public BoundingBox getClusterBounds(int x, int y, int slice) {
        float[] bounds = new float[EngineValues.BOX_LEAVES];
        GraphicsExtension.check("ClusteredLightGrid.getClusterBounds",
                NativeEngineLayerRoutes.clusteredLightGridClusterBounds(
                        open(), x, y, slice, bounds));
        return new BoundingBox(
                new Vector3(bounds[0], bounds[1], bounds[2]),
                new Vector3(bounds[3], bounds[4], bounds[5]));
    }

    /** Releases the grid. Closing twice is a no-op. */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        GraphicsExtension.check("ClusteredLightGrid.close",
                NativeEngineLayerRoutes.clusteredLightGridDestroy(handle));
    }

    /** The native handle, for the objects in this package that consume a grid. */
    long handle() {
        return open();
    }

    /** A count CNA answers about one grid. */
    @FunctionalInterface
    private interface CountRoute {
        int call(long grid, int[] answer);
    }

    /** A distance CNA answers about one grid. */
    @FunctionalInterface
    private interface DistanceRoute {
        int call(long grid, float[] answer);
    }

    private int count(String operation, CountRoute route) {
        int[] answer = new int[1];
        GraphicsExtension.check(operation, route.call(open(), answer));
        return answer[0];
    }

    private float distance(String operation, DistanceRoute route) {
        float[] answer = new float[1];
        GraphicsExtension.check(operation, route.call(open(), answer));
        return answer[0];
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This ClusteredLightGrid is closed");
            }
        }
        return handle;
    }
}
