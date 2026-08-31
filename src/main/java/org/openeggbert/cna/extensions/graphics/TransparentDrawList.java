package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.BoundingBox;
import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Draws transparent geometry back to front.
 *
 * <p>A CNA extension, and the one piece of a renderer that cannot be fixed by a depth buffer:
 * transparent surfaces have to be drawn in an order that depends on where the camera is, so the
 * order has to be worked out every frame. A game submits each transparent thing with the bounds
 * that place it, then asks for the whole set to be drawn.
 *
 * <p>Pure CPU. It touches no graphics device -- {@link #getSortedOrder(Matrix)} and the two static
 * helpers are answers, not draws -- so a game can sort on a machine that cannot render at all.
 *
 * <p><strong>Ownership.</strong> The native list is OWNED and released by {@link #close()}. Each
 * submitted callback is RETAINED here: CNA holds an index rather than a reference to it, and the
 * callbacks reach CNA only for the duration of {@link #drawSorted(Matrix)}, which is the only time
 * CNA runs them. Clearing the list drops them.
 */
public final class TransparentDrawList implements AutoCloseable {

    private final List<Runnable> callbacks = new ArrayList<>();
    private long handle;
    private boolean closed;

    private TransparentDrawList(long handle) {
        this.handle = handle;
    }

    /**
     * Creates an empty draw list.
     *
     * @return the list, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static TransparentDrawList create() {
        GraphicsExtension.requireBackend();
        long[] created = new long[1];
        GraphicsExtension.check("TransparentDrawList.create",
                NativeEngineLayerRoutes.transparentDrawListCreate(created));
        return new TransparentDrawList(created[0]);
    }

    /**
     * Adds one thing to draw, with the bounds that decide where in the order it goes.
     *
     * @param bounds the entry's world-space bounds
     * @param draw what to run when the entry's turn comes; an exception it throws stops the draw
     *        and reaches the caller of {@link #drawSorted(Matrix)}
     * @throws IllegalArgumentException when CNA refuses the entry
     */
    public void submit(BoundingBox bounds, Runnable draw) {
        Objects.requireNonNull(bounds, "bounds");
        Objects.requireNonNull(draw, "draw");
        // The index CNA carries is a position in this list, so the two sides must never disagree
        // about how many entries there are. Appending only after CNA accepts is what keeps that
        // true. On ABI 0.21.0 it cannot actually be observed: CNA refuses a submit only for a
        // null callback, null bounds or a dead list, and all three are refused above first --
        // so this ordering is a guard against a CNA that grows a new refusal, not something the
        // tests can make fail. What they do check is the invariant itself, that every entry runs
        // its own callback.
        GraphicsExtension.check("TransparentDrawList.submit",
                NativeBindings.transparentDrawListSubmit(alive(),
                        EngineValues.floats(bounds, "bounds"), callbacks.size()));
        callbacks.add(draw);
    }

    /**
     * Removes every entry.
     */
    public void clear() {
        GraphicsExtension.check("TransparentDrawList.clear",
                NativeEngineLayerRoutes.transparentDrawListClear(alive()));
        callbacks.clear();
    }

    /**
     * Returns how many entries the list holds.
     *
     * @return the count
     */
    public int getCount() {
        long[] count = new long[1];
        GraphicsExtension.check("TransparentDrawList.getCount",
                NativeEngineLayerRoutes.transparentDrawListGetCount(alive(), count));
        return Math.toIntExact(count[0]);
    }

    /**
     * Runs every entry's callback, farthest from the camera first.
     *
     * <p>A callback that throws stops the draw where it threw, and the exception reaches here
     * rather than being turned into a result code -- so a game learns which draw failed instead of
     * finding a partly drawn frame and no reason for it. The entries after the failing one do not
     * run.
     *
     * @param view the camera's view matrix; the camera position is derived from it
     */
    public void drawSorted(Matrix view) {
        Objects.requireNonNull(view, "view");
        GraphicsExtension.check("TransparentDrawList.drawSorted",
                NativeBindings.transparentDrawListDrawSorted(alive(),
                        EngineValues.floats(view, "view"),
                        callbacks.toArray(new Runnable[0])));
    }

    /**
     * Returns the order {@link #drawSorted(Matrix)} would use, as indices into submission order.
     *
     * <p>The way to check an order without drawing anything, and the way to drive a draw a game
     * would rather run itself.
     *
     * @param view the camera's view matrix
     * @return the indices, farthest first
     */
    public int[] getSortedOrder(Matrix view) {
        Objects.requireNonNull(view, "view");
        float[] leaves = EngineValues.floats(view, "view");
        long[] count = new long[1];
        int probe = NativeEngineLayerRoutes.transparentDrawListCopySortedOrderExt(
                alive(), leaves, new int[0], count);
        if (probe != 14) {
            GraphicsExtension.check("TransparentDrawList.getSortedOrder", probe);
        }
        int[] order = new int[Math.toIntExact(count[0])];
        if (order.length == 0) {
            return order;
        }
        GraphicsExtension.check("TransparentDrawList.getSortedOrder",
                NativeEngineLayerRoutes.transparentDrawListCopySortedOrderExt(
                        alive(), leaves, order, count));
        return order;
    }

    /**
     * Returns the key an entry with these bounds would sort by, which is its distance from the
     * camera.
     *
     * <p>Measured to the <em>nearest point of the box</em>, so a camera inside the box sorts at
     * zero rather than at the box's centre. A pure function of its arguments, and public because a
     * game that keeps its own order still wants CNA's definition of the distance rather than a
     * second one that disagrees near a large object.
     *
     * @param bounds the bounds
     * @param cameraPosition the camera's world-space position
     * @return the key
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static float sortKey(BoundingBox bounds, Vector3 cameraPosition) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(bounds, "bounds");
        Objects.requireNonNull(cameraPosition, "cameraPosition");
        float[] key = new float[1];
        GraphicsExtension.check("TransparentDrawList.sortKey",
                NativeEngineLayerRoutes.transparentDrawListSortKey(
                        EngineValues.floats(bounds, "bounds"),
                        EngineValues.floats(cameraPosition, "cameraPosition"), key));
        return key[0];
    }

    /**
     * Returns the camera position a view matrix implies.
     *
     * <p>A pure function of its argument, and the same derivation {@link #drawSorted(Matrix)} does
     * internally -- so a game sorting its own entries against {@link #sortKey} gets the position
     * CNA would have used rather than one it inverted itself.
     *
     * @param view the view matrix
     * @return the camera's world-space position
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static Vector3 cameraPositionOf(Matrix view) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(view, "view");
        float[] position = new float[3];
        GraphicsExtension.check("TransparentDrawList.cameraPositionOf",
                NativeEngineLayerRoutes.transparentDrawListCameraPositionOf(
                        EngineValues.floats(view, "view"), position));
        return new Vector3(position[0], position[1], position[2]);
    }

    /**
     * Releases the native list.
     *
     * <p>Marked closed only after CNA agrees, so a refused release leaves a usable list rather
     * than an unusable one that also leaked.
     */
    @Override
    public void close() {
        if (closed) {
            return;
        }
        GraphicsExtension.check("TransparentDrawList.close",
                NativeEngineLayerRoutes.transparentDrawListDestroy(handle));
        closed = true;
        handle = 0L;
        callbacks.clear();
    }

    private long alive() {
        if (closed) {
            throw new IllegalStateException("TransparentDrawList is closed");
        }
        return handle;
    }
}
