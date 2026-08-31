package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.BoundingBox;
import Microsoft.Xna.Framework.BoundingFrustum;
import Microsoft.Xna.Framework.BoundingSphere;
import Microsoft.Xna.Framework.Matrix;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.ArrayList;
import java.util.List;

/**
 * Drops what the camera cannot see, before anything is drawn.
 *
 * <p>A CNA extension. XNA has the shapes -- {@link BoundingFrustum}, {@link BoundingBox},
 * {@link BoundingSphere} -- and the intersection tests between them, but nothing that takes a
 * scene's worth of bounds and answers which of them survive. Every XNA game that cared wrote
 * that loop itself.
 *
 * <p>The reason to use this one rather than write the loop again is the batch: one call tests
 * every bound against the six planes CNA derived once, instead of a per-object round trip that
 * rebuilds the frustum each time. {@link #cullBoxes} and {@link #cullSpheres} return the
 * <em>indices</em> that survived, so a game keeps its own array of objects and indexes into it;
 * {@link #cullTransforms} returns the surviving transforms themselves, which is the shape an
 * instanced draw wants next.
 *
 * <p><strong>A transform with no bound is kept.</strong> {@link #cullTransforms} tests
 * {@code index >= bounds.size() || visible(bounds.get(index))}, so a bounds list shorter than the
 * transform list means "these last ones are always visible" rather than "cull them". That is
 * CNA's own documented behaviour and it is the opposite of what passing a short list by accident
 * looks like, so this projection states it rather than hiding it.
 *
 * <p>Needs no graphics device: the culler is arithmetic on matrices and bounds, and can run on a
 * loading thread or in a test with no window at all.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class FrustumCuller implements AutoCloseable {

    /** CNA's own result for a buffer that could not hold the answer. */
    private static final int RESULT_BUFFER_TOO_SMALL = 14;

    private final long handle;
    private boolean closed;

    private FrustumCuller(long handle) {
        this.handle = handle;
    }

    /**
     * Creates a culler whose frustum is whatever CNA starts one at.
     *
     * <p>Set a camera before asking it anything: an unset frustum is not a documented value, so
     * this projection does not claim one.
     *
     * @return the culler, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static FrustumCuller create() {
        GraphicsExtension.requireBackend();
        long[] culler = new long[1];
        GraphicsExtension.check("FrustumCuller.create",
                NativeEngineLayerRoutes.frustumCullerExtCreate(culler));
        return new FrustumCuller(culler[0]);
    }

    /**
     * Sets the frustum from a combined view-projection matrix.
     *
     * @param viewProjection the combined matrix
     */
    public void setViewProjection(Matrix viewProjection) {
        GraphicsExtension.check("FrustumCuller.setViewProjection",
                NativeEngineLayerRoutes.frustumCullerExtSetViewProjection(open(),
                        EngineValues.floats(viewProjection, "viewProjection")));
    }

    /**
     * Sets the frustum from a view and a projection.
     *
     * <p>Exactly {@link #setViewProjection} of their product, in that order, which is stated
     * because the order is the mistake this overload exists to prevent.
     *
     * @param view the view matrix
     * @param projection the projection matrix
     */
    public void setCamera(Matrix view, Matrix projection) {
        GraphicsExtension.check("FrustumCuller.setCamera",
                NativeEngineLayerRoutes.frustumCullerExtSetCamera(open(),
                        EngineValues.floats(view, "view"),
                        EngineValues.floats(projection, "projection")));
    }

    /**
     * Returns the frustum the culler is testing against.
     *
     * @return the frustum, as XNA's own type
     */
    public BoundingFrustum getFrustum() {
        float[] matrix = new float[EngineValues.MATRIX_LEAVES];
        GraphicsExtension.check("FrustumCuller.getFrustum",
                NativeEngineLayerRoutes.frustumCullerExtGetFrustum(open(), matrix));
        return new BoundingFrustum(EngineValues.matrix(matrix, 0));
    }

    /**
     * Tests one box against the frustum.
     *
     * @param box the box
     * @return whether any part of it is inside
     */
    public boolean isVisible(BoundingBox box) {
        boolean[] visible = new boolean[1];
        GraphicsExtension.check("FrustumCuller.isVisible",
                NativeEngineLayerRoutes.frustumCullerExtIsBoxVisible(open(),
                        EngineValues.floats(box, "box"), visible));
        return visible[0];
    }

    /**
     * Tests one sphere against the frustum.
     *
     * @param sphere the sphere
     * @return whether any part of it is inside
     */
    public boolean isVisible(BoundingSphere sphere) {
        boolean[] visible = new boolean[1];
        GraphicsExtension.check("FrustumCuller.isVisible",
                NativeEngineLayerRoutes.frustumCullerExtIsSphereVisible(open(),
                        EngineValues.floats(sphere, "sphere"), visible));
        return visible[0];
    }

    /**
     * Returns the indices of the boxes that are visible.
     *
     * @param bounds the boxes to test, in the caller's own order
     * @return the indices into {@code bounds} that survived, ascending
     */
    public int[] cullBoxes(List<BoundingBox> bounds) {
        long culler = open();
        float[] packed = EngineValues.boxes(bounds, "bounds");
        return indices("FrustumCuller.cullBoxes", bounds.size(),
                (destination, count) -> NativeEngineLayerRoutes
                        .frustumCullerExtCullBoxes(culler, packed, destination, count));
    }

    /**
     * Returns the indices of the spheres that are visible.
     *
     * @param bounds the spheres to test, in the caller's own order
     * @return the indices into {@code bounds} that survived, ascending
     */
    public int[] cullSpheres(List<BoundingSphere> bounds) {
        long culler = open();
        float[] packed = EngineValues.spheres(bounds, "bounds");
        return indices("FrustumCuller.cullSpheres", bounds.size(),
                (destination, count) -> NativeEngineLayerRoutes
                        .frustumCullerExtCullSpheres(culler, packed, destination, count));
    }

    /**
     * Returns the transforms whose bounds are visible.
     *
     * <p>A transform at an index the bounds list does not reach is <strong>kept</strong>. See the
     * class documentation: that is CNA's own rule and it is deliberately not corrected here.
     *
     * @param transforms the transforms
     * @param bounds the bounds, which may be shorter than the transforms
     * @return the surviving transforms, in the order they were given
     */
    public List<Matrix> cullTransforms(List<Matrix> transforms, List<BoundingBox> bounds) {
        long culler = open();
        float[] packedTransforms = EngineValues.matrices(transforms, "transforms");
        float[] packedBounds = EngineValues.boxes(bounds, "bounds");
        long[] count = new long[1];
        // A zero-capacity probe reports how many survive and writes nothing, so
        // BUFFER_TOO_SMALL is the expected answer to the first call rather than a failure.
        int probe = NativeEngineLayerRoutes.frustumCullerExtCullTransforms(
                culler, packedTransforms, packedBounds, new float[0], count);
        if (probe != RESULT_BUFFER_TOO_SMALL) {
            GraphicsExtension.check("FrustumCuller.cullTransforms", probe);
        }
        int visible = Math.toIntExact(count[0]);
        if (visible == 0) {
            return List.of();
        }
        float[] destination = new float[Math.multiplyExact(visible, EngineValues.MATRIX_LEAVES)];
        GraphicsExtension.check("FrustumCuller.cullTransforms",
                NativeEngineLayerRoutes.frustumCullerExtCullTransforms(
                        culler, packedTransforms, packedBounds, destination, count));
        List<Matrix> kept = new ArrayList<>(visible);
        for (int index = 0; index < visible; index++) {
            kept.add(EngineValues.matrix(destination, index));
        }
        return List.copyOf(kept);
    }

    /** Releases the culler. Closing twice is a no-op. */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        GraphicsExtension.check("FrustumCuller.close",
                NativeEngineLayerRoutes.frustumCullerExtDestroy(handle));
    }

    /** One of CNA's two index-returning cull routes, which differ only in what they test. */
    @FunctionalInterface
    private interface IndexCull {
        int call(long[] destination, long[] count);
    }

    /**
     * Runs the count-then-copy pair CNA's index culls use.
     *
     * <p>The count call is not skipped even when nothing can be culled: asking for the count
     * first is what makes the second call's capacity exact, and a buffer sized to the input
     * would allocate the worst case on every frame.
     */
    private int[] indices(String operation, int candidates, IndexCull cull) {
        long[] count = new long[1];
        int probe = cull.call(new long[0], count);
        if (probe != RESULT_BUFFER_TOO_SMALL) {
            GraphicsExtension.check(operation, probe);
        }
        int visible = Math.toIntExact(count[0]);
        if (visible == 0) {
            return new int[0];
        }
        long[] destination = new long[visible];
        GraphicsExtension.check(operation, cull.call(destination, count));
        int[] result = new int[visible];
        for (int index = 0; index < visible; index++) {
            long candidate = destination[index];
            if (candidate < 0 || candidate >= candidates) {
                throw new IllegalStateException("CNA reported visible index " + candidate
                        + " for " + candidates + " candidates");
            }
            result[index] = (int) candidate;
        }
        return result;
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This FrustumCuller is closed");
            }
        }
        return handle;
    }
}
