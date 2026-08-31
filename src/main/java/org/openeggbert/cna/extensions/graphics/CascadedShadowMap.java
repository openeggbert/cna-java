package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.BoundingSphere;
import Microsoft.Xna.Framework.Graphics.Effect;
import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.FacadeFactory;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A directional light's shadow, split across the camera's depth range.
 *
 * <p>A CNA extension, and the answer to the problem {@link ShadowMap} has: one map stretched over
 * a whole outdoor scene is too coarse near the camera and wasted far away. A cascaded map is
 * several, each covering a slice of the view frustum, packed into one atlas -- and the
 * fiddly parts, which every game that tried this got wrong at least once, are named here:
 *
 * <ul>
 *   <li>{@link #getSplitLambda()} blends uniform against logarithmic split placement, which is
 *       the one number that decides whether the near cascade is sharp;</li>
 *   <li>{@link #getBlendBand()} is how wide the cross-fade between neighbouring cascades is,
 *       without which the seam is visible as a hard line;</li>
 *   <li>{@link #snapToTexelGrid} stops a cascade shimmering as the camera moves, because
 *       otherwise the same world position lands on a slightly different texel each frame;</li>
 *   <li>{@link #setDebugTintEnabled} tints each cascade differently, which is how the placement
 *       is diagnosed at all.</li>
 * </ul>
 *
 * <p>A frame is {@link #update} once with the light and the camera, then
 * {@link #begin(int)}/draw/{@link #end()} per cascade, then {@link #applyToReceiver} on the
 * effect that shades the scene -- which moves the atlas, the transforms, the splits and the
 * blend band across together, so a caller cannot set half of them.
 *
 * <p>Like {@link ShadowMap}, a map on a renderer that cannot cast still exists, and its atlas is
 * a counted borrow the caller disposes.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class CascadedShadowMap implements AutoCloseable {

    /** {@code CNA_SHADOW_CASCADE_MAX_EXT}. */
    public static final int MaxCascades = 4;

    /** {@code CNA_FRUSTUM_CORNER_COUNT_EXT}: a frustum has eight corners and always will. */
    public static final int FrustumCornerCount = 8;

    /** CNA's own result for a buffer that could not hold the answer. */
    private static final int RESULT_BUFFER_TOO_SMALL = 14;

    private final long handle;
    private boolean closed;

    private CascadedShadowMap(long handle) {
        this.handle = handle;
    }

    /**
     * Creates a cascaded map.
     *
     * @param graphicsDevice the device to render on
     * @param quality the preset, which selects each cascade's size
     * @param cascadeCount how many cascades, from one to {@link #MaxCascades}
     * @return the map, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static CascadedShadowMap create(GraphicsDevice graphicsDevice, ShadowQuality quality,
            int cascadeCount) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        Objects.requireNonNull(quality, "quality");
        long[] map = new long[1];
        GraphicsExtension.check("CascadedShadowMap.create",
                NativeEngineLayerRoutes.cascadedShadowMapCreate(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice),
                        quality.ordinal(), cascadeCount, map));
        return new CascadedShadowMap(map[0]);
    }

    /**
     * Computes where the splits fall, without a map.
     *
     * @param nearPlane the camera's near plane
     * @param farPlane the camera's far plane
     * @param cascadeCount how many cascades to place
     * @param lambda zero for uniform splits, one for logarithmic
     * @return the split distances
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static float[] computeSplitDistances(float nearPlane, float farPlane,
            int cascadeCount, float lambda) {
        GraphicsExtension.requireBackend();
        long[] count = new long[1];
        // A zero-capacity probe reports the count and writes nothing, so BUFFER_TOO_SMALL is the
        // expected answer to the first call rather than a failure.
        int probe = NativeEngineLayerRoutes.cascadedShadowMapComputeSplitDistances(
                nearPlane, farPlane, cascadeCount, lambda, new float[0], count);
        if (probe != RESULT_BUFFER_TOO_SMALL) {
            GraphicsExtension.check("CascadedShadowMap.computeSplitDistances", probe);
        }
        float[] distances = new float[Math.toIntExact(count[0])];
        if (distances.length == 0) {
            return distances;
        }
        GraphicsExtension.check("CascadedShadowMap.computeSplitDistances",
                NativeEngineLayerRoutes.cascadedShadowMapComputeSplitDistances(
                        nearPlane, farPlane, cascadeCount, lambda, distances, count));
        return distances;
    }

    /**
     * Returns the eight world-space corners of a view-projection frustum.
     *
     * @param view the view matrix
     * @param projection the projection matrix
     * @return the corners, always {@link #FrustumCornerCount} of them
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static List<Vector3> computeFrustumCorners(Matrix view, Matrix projection) {
        GraphicsExtension.requireBackend();
        float[] corners = new float[FrustumCornerCount * 3];
        GraphicsExtension.check("CascadedShadowMap.computeFrustumCorners",
                NativeEngineLayerRoutes.cascadedShadowMapComputeFrustumCorners(
                        EngineValues.floats(view, "view"),
                        EngineValues.floats(projection, "projection"), corners));
        List<Vector3> read = new ArrayList<>(FrustumCornerCount);
        for (int index = 0; index < FrustumCornerCount; index++) {
            read.add(new Vector3(corners[index * 3], corners[index * 3 + 1],
                    corners[index * 3 + 2]));
        }
        return List.copyOf(read);
    }

    /**
     * Returns the sphere that bounds a frustum's corners.
     *
     * <p>A sphere rather than a box on purpose: a cascade fitted to a rotating box changes size
     * as the camera turns, and one fitted to a sphere does not.
     *
     * @param corners the eight corners, as {@link #computeFrustumCorners} returns them
     * @return the bounding sphere
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static BoundingSphere computeBoundingSphere(List<Vector3> corners) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(corners, "corners");
        if (corners.size() != FrustumCornerCount) {
            throw new IllegalArgumentException("a frustum has " + FrustumCornerCount
                    + " corners, not " + corners.size());
        }
        float[] packed = new float[FrustumCornerCount * 3];
        for (int index = 0; index < FrustumCornerCount; index++) {
            float[] corner = EngineValues.floats(corners.get(index), "corners");
            System.arraycopy(corner, 0, packed, index * 3, 3);
        }
        float[] centre = new float[3];
        float[] radius = new float[1];
        GraphicsExtension.check("CascadedShadowMap.computeBoundingSphere",
                NativeEngineLayerRoutes.cascadedShadowMapComputeBoundingSphere(packed, centre,
                        radius));
        return new BoundingSphere(new Vector3(centre[0], centre[1], centre[2]), radius[0]);
    }

    /**
     * Snaps a cascade centre to the shadow map's texel grid.
     *
     * <p>Without this a cascade's contents shimmer as the camera moves, because the same world
     * position lands on a slightly different texel each frame.
     *
     * @param centre the cascade centre
     * @param radius the cascade's bounding radius
     * @param cascadeSize the cascade's edge length in texels
     * @return the snapped centre
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static Vector3 snapToTexelGrid(Vector3 centre, float radius, int cascadeSize) {
        GraphicsExtension.requireBackend();
        float[] snapped = new float[3];
        GraphicsExtension.check("CascadedShadowMap.snapToTexelGrid",
                NativeEngineLayerRoutes.cascadedShadowMapSnapToTexelGrid(
                        EngineValues.floats(centre, "centre"), radius, cascadeSize, snapped));
        return new Vector3(snapped[0], snapped[1], snapped[2]);
    }

    /**
     * Reports whether this renderer can cast into the cascades.
     *
     * @return whether the caster shader exists and links
     */
    public boolean isSupported() {
        boolean[] supported = new boolean[1];
        GraphicsExtension.check("CascadedShadowMap.isSupported",
                NativeEngineLayerRoutes.cascadedShadowMapIsSupported(open(), supported));
        return supported[0];
    }

    /**
     * Recomputes every cascade's transform for a light and a camera.
     *
     * <p>Once per frame, before any cascade is opened.
     *
     * @param light the directional light to cast from
     * @param cameraView the camera's view matrix
     * @param cameraProjection the camera's projection matrix
     */
    public void update(DirectionalLight light, Matrix cameraView, Matrix cameraProjection) {
        Objects.requireNonNull(light, "light");
        GraphicsExtension.check("CascadedShadowMap.update",
                NativeEngineLayerRoutes.cascadedShadowMapUpdate(open(), new byte[3],
                        light.integral(), light.floating(),
                        EngineValues.floats(cameraView, "cameraView"),
                        EngineValues.floats(cameraProjection, "cameraProjection")));
    }

    /**
     * Opens the pass for one cascade.
     *
     * @param cascadeIndex which cascade
     */
    public void begin(int cascadeIndex) {
        GraphicsExtension.check("CascadedShadowMap.begin",
                NativeEngineLayerRoutes.cascadedShadowMapBegin(open(), cascadeIndex));
    }

    /** Closes the cascade pass. */
    public void end() {
        GraphicsExtension.check("CascadedShadowMap.end",
                NativeEngineLayerRoutes.cascadedShadowMapEnd(open()));
    }

    /**
     * Moves the whole shadow state across to a receiving effect.
     *
     * <p>The atlas, the cascade transforms, the splits and the blend band in one call, which is
     * why this exists rather than four setters: a caller cannot set half of them.
     *
     * @param effect an effect that implements the shadow-receiver contract
     */
    public void applyToReceiver(Effect effect) {
        Objects.requireNonNull(effect, "effect");
        GraphicsExtension.check("CascadedShadowMap.applyToReceiver",
                NativeEngineLayerRoutes.cascadedShadowMapApplyToReceiver(open(),
                        NativeBindings.nativeResourceHandle(effect)));
    }

    /**
     * Borrows the atlas every cascade is packed into.
     *
     * @param graphicsDevice the device the map renders on
     * @return the texture, which the caller disposes, or {@code null} when this renderer cannot
     *         cast shadows
     */
    public Texture2D getShadowTexture(GraphicsDevice graphicsDevice) {
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] texture = new long[1];
        GraphicsExtension.check("CascadedShadowMap.getShadowTexture",
                NativeEngineLayerRoutes.cascadedShadowMapGetShadowTexture(open(), texture));
        if (texture[0] == 0L) {
            return null;
        }
        return NativeBindings.createBorrowedRenderTarget(graphicsDevice, texture[0]);
    }

    /** @return how many cascades the map holds */
    public int getCascadeCount() {
        int[] count = new int[1];
        GraphicsExtension.check("CascadedShadowMap.getCascadeCount",
                NativeEngineLayerRoutes.cascadedShadowMapGetCascadeCount(open(), count));
        return count[0];
    }

    /** @return one cascade's edge length in texels */
    public int getCascadeSize() {
        int[] size = new int[1];
        GraphicsExtension.check("CascadedShadowMap.getCascadeSize",
                NativeEngineLayerRoutes.cascadedShadowMapGetCascadeSize(open(), size));
        return size[0];
    }

    /**
     * Returns the transform from world space into one cascade's atlas region.
     *
     * @param cascadeIndex which cascade
     * @return the transform, as of the last {@link #update}
     */
    public Matrix getCascadeMatrix(int cascadeIndex) {
        float[] matrix = new float[EngineValues.MATRIX_LEAVES];
        GraphicsExtension.check("CascadedShadowMap.getCascadeMatrix",
                NativeEngineLayerRoutes.cascadedShadowMapGetCascadeMatrix(open(), cascadeIndex,
                        matrix));
        return EngineValues.matrix(matrix, 0);
    }

    /**
     * Returns the view-space distance at which one cascade ends.
     *
     * @param cascadeIndex which cascade
     * @return the distance
     */
    public float getSplitDistance(int cascadeIndex) {
        float[] distance = new float[1];
        GraphicsExtension.check("CascadedShadowMap.getSplitDistance",
                NativeEngineLayerRoutes.cascadedShadowMapGetSplitDistance(open(), cascadeIndex,
                        distance));
        return distance[0];
    }

    /**
     * Returns which cascade covers a view-space depth.
     *
     * @param viewDepth the depth to place
     * @return the cascade index
     */
    public int selectCascade(float viewDepth) {
        int[] index = new int[1];
        GraphicsExtension.check("CascadedShadowMap.selectCascade",
                NativeEngineLayerRoutes.cascadedShadowMapSelectCascade(open(), viewDepth, index));
        return index[0];
    }

    /** @return the width in world units over which neighbouring cascades cross-fade */
    public float getBlendBand() {
        float[] band = new float[1];
        GraphicsExtension.check("CascadedShadowMap.getBlendBand",
                NativeEngineLayerRoutes.cascadedShadowMapGetBlendBand(open(), band));
        return band[0];
    }

    /**
     * Sets the width over which neighbouring cascades cross-fade.
     *
     * @param band the width in world units
     */
    public void setBlendBand(float band) {
        GraphicsExtension.check("CascadedShadowMap.setBlendBand",
                NativeEngineLayerRoutes.cascadedShadowMapSetBlendBand(open(), band));
    }

    /** @return the blend between uniform and logarithmic split placement */
    public float getSplitLambda() {
        float[] lambda = new float[1];
        GraphicsExtension.check("CascadedShadowMap.getSplitLambda",
                NativeEngineLayerRoutes.cascadedShadowMapGetSplitLambda(open(), lambda));
        return lambda[0];
    }

    /**
     * Sets the blend between uniform and logarithmic split placement.
     *
     * <p>Zero places the splits evenly through the frustum, which wastes the near cascade; one
     * places them logarithmically, which is right for a perspective camera and is why this
     * number exists.
     *
     * @param lambda zero for uniform, one for logarithmic
     */
    public void setSplitLambda(float lambda) {
        GraphicsExtension.check("CascadedShadowMap.setSplitLambda",
                NativeEngineLayerRoutes.cascadedShadowMapSetSplitLambda(open(), lambda));
    }

    /** @return whether each cascade is tinted differently, for diagnosing split placement */
    public boolean isDebugTintEnabled() {
        boolean[] enabled = new boolean[1];
        GraphicsExtension.check("CascadedShadowMap.isDebugTintEnabled",
                NativeEngineLayerRoutes.cascadedShadowMapIsDebugTintEnabled(open(), enabled));
        return enabled[0];
    }

    /**
     * Tints each cascade differently, so its extent is visible in the frame.
     *
     * @param enabled whether to tint
     */
    public void setDebugTintEnabled(boolean enabled) {
        GraphicsExtension.check("CascadedShadowMap.setDebugTintEnabled",
                NativeEngineLayerRoutes.cascadedShadowMapSetDebugTintEnabled(open(), enabled));
    }

    /**
     * Returns the effect this map casts shadows with, borrowed.
     *
     * <p>A CNA extension with no XNA shape: the shader is CNA's own, and this is the only way to
     * reach it -- to give it a parameter, or to read what it has. The effect belongs to the
     * lender; what comes back is a <em>view</em> of it, and disposing the view gives the borrow
     * back rather than destroying the effect.
     *
     * <p><strong>A fresh view every call.</strong> Two calls are two objects to dispose and one
     * effect underneath.
     *
     * <p><strong>The lender refuses to be destroyed while a view is outstanding, so disposing the
     * view is not housekeeping -- it is what makes closing the lender possible, and the
     * refusal is recoverable rather than fatal.</strong>
     *
     * <p>Answers {@code null} on a renderer with no shader compiler, which has no effect to
     * lend -- and that is not a failure but the renderer's answer.
     *
     * @param graphicsDevice the device the effect belongs to
     * @return the effect, which the caller disposes, or {@code null}
     */
    public Effect getCasterEffect(GraphicsDevice graphicsDevice) {
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] effect = new long[1];
        GraphicsExtension.check("CascadedShadowMap.getCasterEffect", NativeEngineLayerRoutes
                .cascadedShadowMapGetCasterEffect(open(), effect));
        return effect[0] == 0L ? null
                : FacadeFactory.createBorrowedEffect(graphicsDevice, effect[0]);
    }

    /**
     * Releases the map and its atlas. Closing twice is a no-op.
     *
     * <p>Refused while an atlas borrow is outstanding, and a refusal leaves the map exactly as
     * it was; see {@link ShadowMap#close()}.
     */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
        }
        GraphicsExtension.check("CascadedShadowMap.close",
                NativeEngineLayerRoutes.cascadedShadowMapDestroy(handle));
        synchronized (this) {
            closed = true;
        }
    }

    /** The native handle, for the debug renderer's cascade gizmo. */
    long handle() {
        return open();
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This CascadedShadowMap is closed");
            }
        }
        return handle;
    }
}
