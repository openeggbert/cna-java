package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Transparency that does not need sorting.
 *
 * <p>A CNA extension. Sorting transparent geometry back to front is the classic answer and it is
 * wrong whenever two transparent surfaces interpenetrate -- a window frame through smoke, a leaf
 * through water -- because there is no single correct order. Weighted-blended order-independent
 * transparency accumulates every fragment with a depth-derived weight instead, and resolves once.
 *
 * <p>A frame is {@link #begin(float)}, every transparent draw, {@link #end()}, then
 * {@link #resolve}. {@link #weight} is the same curve the shader uses, exposed so a game can see
 * why a distant fragment contributes less than a near one.
 *
 * <p><strong>The accumulation and revealage targets are not exposed.</strong> CNA documents them
 * as borrowed and does not say how the borrow is given back; a facade over a borrow with no
 * stated release is a leak or a dangling reference depending on which guess is right, so this
 * projection makes neither.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class WeightedBlendedTransparency implements AutoCloseable {

    /** CNA's own result for a buffer that could not hold the answer. */
    private static final int RESULT_BUFFER_TOO_SMALL = 14;

    private final long handle;
    private boolean closed;

    private WeightedBlendedTransparency(long handle) {
        this.handle = handle;
    }

    /**
     * Creates a resolve at a target size.
     *
     * @param graphicsDevice the device to render on
     * @param width the target width in pixels
     * @param height the target height in pixels
     * @return the resolve, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static WeightedBlendedTransparency create(GraphicsDevice graphicsDevice, int width,
            int height) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] transparency = new long[1];
        GraphicsExtension.check("WeightedBlendedTransparency.create",
                NativeEngineLayerRoutes.weightedBlendedTransparencyCreate(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), width, height,
                        transparency));
        return new WeightedBlendedTransparency(transparency[0]);
    }

    /**
     * Returns the blending weight for one fragment's depth and coverage.
     *
     * <p>The curve the whole technique rests on: a near fragment weighs more than a far one, so
     * the accumulation approximates the sorted answer without sorting. The depth ratio is
     * clamped to zero-to-one and the weight itself to a finite range, because the curve is
     * unbounded near zero depth and one overflowing weight would poison the whole buffer rather
     * than one fragment.
     *
     * @param viewDepth the fragment's view-space depth
     * @param alpha the fragment's coverage
     * @param farPlane the camera's far plane
     * @return the weight
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static float weight(float viewDepth, float alpha, float farPlane) {
        GraphicsExtension.requireBackend();
        float[] weight = new float[1];
        GraphicsExtension.check("WeightedBlendedTransparency.weight",
                NativeEngineLayerRoutes.weightedBlendedTransparencyWeight(viewDepth, alpha,
                        farPlane, weight));
        return weight[0];
    }

    /**
     * Returns the GLSL a shader accumulates with.
     *
     * @return the GLSL
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static String getAccumulationGlsl() {
        GraphicsExtension.requireBackend();
        long[] bytes = new long[1];
        int probe = NativeEngineLayerRoutes
                .weightedBlendedTransparencyCopyAccumulationGlsl(new byte[0], bytes);
        if (probe != RESULT_BUFFER_TOO_SMALL) {
            GraphicsExtension.check("WeightedBlendedTransparency.getAccumulationGlsl", probe);
        }
        int length = Math.toIntExact(bytes[0]);
        if (length == 0) {
            return "";
        }
        byte[] destination = new byte[length];
        GraphicsExtension.check("WeightedBlendedTransparency.getAccumulationGlsl",
                NativeEngineLayerRoutes.weightedBlendedTransparencyCopyAccumulationGlsl(
                        destination, bytes));
        return new String(destination, 0, Math.toIntExact(bytes[0]), StandardCharsets.UTF_8);
    }

    /**
     * Reports whether this renderer can run the resolve.
     *
     * @return whether the shaders and targets exist
     */
    public boolean isSupported() {
        boolean[] supported = new boolean[1];
        GraphicsExtension.check("WeightedBlendedTransparency.isSupported",
                NativeEngineLayerRoutes.weightedBlendedTransparencyIsSupported(open(),
                        supported));
        return supported[0];
    }

    /**
     * Returns why the resolve is unavailable, in the renderer's own words.
     *
     * @return the reason, or an empty string when it is available
     */
    public String getUnsupportedReason() {
        long transparency = open();
        long[] bytes = new long[1];
        int probe = NativeEngineLayerRoutes
                .weightedBlendedTransparencyCopyUnsupportedReason(transparency, new byte[0],
                        bytes);
        if (probe != RESULT_BUFFER_TOO_SMALL) {
            GraphicsExtension.check("WeightedBlendedTransparency.getUnsupportedReason", probe);
        }
        int length = Math.toIntExact(bytes[0]);
        if (length == 0) {
            return "";
        }
        byte[] destination = new byte[length];
        GraphicsExtension.check("WeightedBlendedTransparency.getUnsupportedReason",
                NativeEngineLayerRoutes.weightedBlendedTransparencyCopyUnsupportedReason(
                        transparency, destination, bytes));
        return new String(destination, 0, Math.toIntExact(bytes[0]), StandardCharsets.UTF_8);
    }

    /**
     * Resizes the accumulation targets.
     *
     * @param width the target width in pixels
     * @param height the target height in pixels
     */
    public void resize(int width, int height) {
        GraphicsExtension.check("WeightedBlendedTransparency.resize",
                NativeEngineLayerRoutes.weightedBlendedTransparencyResize(open(), width, height));
    }

    /**
     * Opens the accumulation, binding both targets and clearing them.
     *
     * <p>Opens on every renderer, supported or not, so a frame that always runs its transparency
     * pass stays symmetric rather than leaving an accumulation half-open where the resolve
     * cannot run.
     *
     * @param farPlane the camera's far plane, which the weight curve is measured against
     */
    public void begin(float farPlane) {
        GraphicsExtension.check("WeightedBlendedTransparency.begin",
                NativeEngineLayerRoutes.weightedBlendedTransparencyBegin(open(), farPlane));
    }

    /** Closes the accumulation. */
    public void end() {
        GraphicsExtension.check("WeightedBlendedTransparency.end",
                NativeEngineLayerRoutes.weightedBlendedTransparencyEnd(open()));
    }

    /**
     * Composites the accumulated transparency over the current target.
     *
     * @param width the destination width in pixels
     * @param height the destination height in pixels
     */
    public void resolve(int width, int height) {
        GraphicsExtension.check("WeightedBlendedTransparency.resolve",
                NativeEngineLayerRoutes.weightedBlendedTransparencyResolve(open(), width,
                        height));
    }

    /**
     * Reports whether an accumulation is currently open.
     *
     * @return whether {@link #begin(float)} has been called without a matching {@link #end()}
     */
    public boolean isAccumulating() {
        boolean[] accumulating = new boolean[1];
        GraphicsExtension.check("WeightedBlendedTransparency.isAccumulating",
                NativeEngineLayerRoutes.weightedBlendedTransparencyIsAccumulating(open(),
                        accumulating));
        return accumulating[0];
    }

    /**
     * Returns the accumulation target, borrowed.
     *
     * <p>A CNA extension. Order-independent transparency works by summing weighted colour into
     * one target and coverage into another, then dividing; this is the first of the two, and
     * reaching it is how a game debugs a transparency that looks wrong -- or builds a pass of its
     * own over the same intermediate.
     *
     * <p><strong>A retaining borrow, fresh every call.</strong> Two calls are two objects to
     * dispose and one target underneath, this object may be closed while a view is out, and the
     * view keeps what it names alive.
     *
     * <p>Answers {@code null} on a renderer that has no float target to accumulate into, which is
     * the same renderer {@link #isSupported()} answers {@code false} for.
     *
     * @param graphicsDevice the device the target belongs to
     * @return the target, which the caller disposes, or {@code null}
     */
    public Texture2D getAccumulationTexture(GraphicsDevice graphicsDevice) {
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] texture = new long[1];
        GraphicsExtension.check("WeightedBlendedTransparency.getAccumulationTexture",
                NativeEngineLayerRoutes.weightedBlendedTransparencyGetAccumulationTextureExt(
                        open(), texture));
        return texture[0] == 0L ? null
                : NativeBindings.createBorrowedRenderTarget(graphicsDevice, texture[0]);
    }

    /**
     * Returns the revealage target, borrowed.
     *
     * <p>The other half of {@link #getAccumulationTexture}: the coverage the accumulated colour is
     * divided by. Same ownership in every respect, and a different surface format -- the
     * accumulation target carries colour and this one carries one channel of coverage, which is
     * the cheapest way to tell the two apart.
     *
     * @param graphicsDevice the device the target belongs to
     * @return the target, which the caller disposes, or {@code null}
     */
    public Texture2D getRevealageTexture(GraphicsDevice graphicsDevice) {
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] texture = new long[1];
        GraphicsExtension.check("WeightedBlendedTransparency.getRevealageTexture",
                NativeEngineLayerRoutes.weightedBlendedTransparencyGetRevealageTextureExt(
                        open(), texture));
        return texture[0] == 0L ? null
                : NativeBindings.createBorrowedRenderTarget(graphicsDevice, texture[0]);
    }

    /** Releases the resolve and its targets. Closing twice is a no-op. */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        GraphicsExtension.check("WeightedBlendedTransparency.close",
                NativeEngineLayerRoutes.weightedBlendedTransparencyDestroy(handle));
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This WeightedBlendedTransparency is closed");
            }
        }
        return handle;
    }
}
