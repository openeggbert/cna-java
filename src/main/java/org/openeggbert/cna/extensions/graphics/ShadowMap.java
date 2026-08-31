package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.BoundingBox;
import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import Microsoft.Xna.Framework.Matrix;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.List;
import java.util.Objects;

/**
 * A directional light's shadow map.
 *
 * <p>A CNA extension. XNA has render targets and nothing else: every XNA game that wanted shadows
 * chose a map size, fitted an orthographic frustum around the scene from the light's direction,
 * rendered depth into it with a shader of its own, and sampled it back with a bias it tuned by
 * hand. This is that, with the fiddly parts done and named.
 *
 * <p>A frame looks like {@link #begin} with the light and the scene's bounds, one
 * {@link #applyCaster()} and draw per rigid mesh, {@link #end()}, and then
 * {@link #getLightViewProjection()} handed to the shading pass so it can look each pixel up in
 * {@link #getShadowTexture(GraphicsDevice)}.
 *
 * <p><strong>A map on a renderer that cannot cast still exists.</strong> Like {@link GpuTimer},
 * that is deliberate: {@link #create} succeeds, {@link #isSupported()} answers {@code false}, and
 * the passes open and close without doing anything, so a frame that always runs the shadow pass
 * renders unshadowed rather than failing. CNA says as much in its own log on this renderer.
 *
 * <p><strong>The shadow texture is a counted borrow.</strong> {@link #getShadowTexture} hands
 * back a {@link Texture2D} facade that <em>owns the borrow</em>, not the texture: disposing it
 * gives the borrow back and leaves the map's own target intact, and CNA refuses to destroy the
 * map while a borrow is outstanding. So a texture that outlives its {@link #close()} keeps the
 * map alive rather than dangling, which is the failure this design exists to make impossible.
 *
 * <p>The map's handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class ShadowMap implements AutoCloseable {

    private final long handle;
    private boolean closed;

    private ShadowMap(long handle) {
        this.handle = handle;
    }

    /**
     * Creates a map at a quality preset.
     *
     * <p>Succeeds on a renderer that cannot cast shadows; ask {@link #isSupported()}.
     *
     * @param graphicsDevice the device to render on
     * @param quality the preset, which selects the map's size and filter radius
     * @return the map, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static ShadowMap create(GraphicsDevice graphicsDevice, ShadowQuality quality) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        Objects.requireNonNull(quality, "quality");
        long[] map = new long[1];
        GraphicsExtension.check("ShadowMap.create", NativeEngineLayerRoutes.shadowMapCreate(
                NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), quality.ordinal(), map));
        return new ShadowMap(map[0]);
    }

    /**
     * Returns the map size a quality preset selects, without creating one.
     *
     * @param quality the preset
     * @return the edge length in texels
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static int getSizeForQuality(ShadowQuality quality) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(quality, "quality");
        int[] size = new int[1];
        GraphicsExtension.check("ShadowMap.getSizeForQuality",
                NativeEngineLayerRoutes.shadowMapSizeForQuality(quality.ordinal(), size));
        return size[0];
    }

    /**
     * Returns the filter radius a quality preset selects, without creating one.
     *
     * @param quality the preset
     * @return the radius in texels
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static int getFilterRadiusForQuality(ShadowQuality quality) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(quality, "quality");
        int[] radius = new int[1];
        GraphicsExtension.check("ShadowMap.getFilterRadiusForQuality",
                NativeEngineLayerRoutes.shadowMapFilterRadiusForQuality(quality.ordinal(),
                        radius));
        return radius[0];
    }

    /**
     * Computes a directional light's view transform for a scene, without a map.
     *
     * <p>A pure function of its arguments, so it needs no device and no map: a game can fit a
     * light to a scene on a loading thread, or check what the fitting will do before it commits
     * to a quality preset.
     *
     * @param light the directional light
     * @param sceneBounds the world-space bounds the shadow must cover
     * @return the view transform
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static Matrix computeLightView(DirectionalLight light, BoundingBox sceneBounds) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(light, "light");
        float[] matrix = new float[EngineValues.MATRIX_LEAVES];
        GraphicsExtension.check("ShadowMap.computeLightView",
                NativeEngineLayerRoutes.shadowMapComputeLightView(new byte[3], light.integral(),
                        light.floating(), EngineValues.floats(sceneBounds, "sceneBounds"),
                        matrix));
        return EngineValues.matrix(matrix, 0);
    }

    /**
     * Computes the projection that fits a scene into a light's view.
     *
     * @param lightView the view transform, from {@link #computeLightView}
     * @param sceneBounds the world-space bounds the shadow must cover
     * @return the projection
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static Matrix computeLightProjection(Matrix lightView, BoundingBox sceneBounds) {
        GraphicsExtension.requireBackend();
        float[] matrix = new float[EngineValues.MATRIX_LEAVES];
        GraphicsExtension.check("ShadowMap.computeLightProjection",
                NativeEngineLayerRoutes.shadowMapComputeLightProjection(
                        EngineValues.floats(lightView, "lightView"),
                        EngineValues.floats(sceneBounds, "sceneBounds"), matrix));
        return EngineValues.matrix(matrix, 0);
    }

    /**
     * Reports whether this renderer can cast into the map.
     *
     * @return whether the caster shader exists and links
     */
    public boolean isSupported() {
        boolean[] supported = new boolean[1];
        GraphicsExtension.check("ShadowMap.isSupported",
                NativeEngineLayerRoutes.shadowMapIsSupported(open(), supported));
        return supported[0];
    }

    /**
     * Opens the shadow pass, binding the map and computing the light's transform.
     *
     * @param light the directional light to cast from
     * @param sceneBounds the world-space bounds the shadow must cover
     */
    public void begin(DirectionalLight light, BoundingBox sceneBounds) {
        Objects.requireNonNull(light, "light");
        GraphicsExtension.check("ShadowMap.begin", NativeEngineLayerRoutes.shadowMapBegin(open(),
                new byte[3], light.integral(), light.floating(),
                EngineValues.floats(sceneBounds, "sceneBounds")));
    }

    /** Closes the shadow pass. */
    public void end() {
        GraphicsExtension.check("ShadowMap.end",
                NativeEngineLayerRoutes.shadowMapEnd(open()));
    }

    /** Applies the caster effect for a rigid draw inside the pass. */
    public void applyCaster() {
        GraphicsExtension.check("ShadowMap.applyCaster",
                NativeEngineLayerRoutes.shadowMapApplyCaster(open()));
    }

    /**
     * Applies the skinned caster effect with a bone palette.
     *
     * @param boneTransforms the bone palette
     * @param weightsPerVertex how many bone weights each vertex carries
     */
    public void applySkinnedCaster(List<Matrix> boneTransforms, int weightsPerVertex) {
        GraphicsExtension.check("ShadowMap.applySkinnedCaster",
                NativeEngineLayerRoutes.shadowMapApplySkinnedCaster(open(),
                        EngineValues.matrices(boneTransforms, "boneTransforms"),
                        weightsPerVertex));
    }

    /**
     * Borrows the map's depth texture.
     *
     * <p>The returned facade owns a <em>borrow</em>: disposing it gives the borrow back and
     * leaves the map's target alone, and the map refuses to close while a borrow is outstanding.
     *
     * @param graphicsDevice the device the map renders on
     * @return the texture, which the caller disposes, or {@code null} when this renderer cannot
     *         cast shadows and the map therefore has none
     */
    public Texture2D getShadowTexture(GraphicsDevice graphicsDevice) {
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] texture = new long[1];
        GraphicsExtension.check("ShadowMap.getShadowTexture",
                NativeEngineLayerRoutes.shadowMapGetShadowTexture(open(), texture));
        if (texture[0] == 0L) {
            return null;
        }
        return NativeBindings.createBorrowedRenderTarget(graphicsDevice, texture[0]);
    }

    /**
     * Returns the transform from world space into the map, as of the last {@link #begin}.
     *
     * @return the transform
     */
    public Matrix getLightViewProjection() {
        float[] matrix = new float[EngineValues.MATRIX_LEAVES];
        GraphicsExtension.check("ShadowMap.getLightViewProjection",
                NativeEngineLayerRoutes.shadowMapGetLightViewProjection(open(), matrix));
        return EngineValues.matrix(matrix, 0);
    }

    /** @return the map's edge length in texels */
    public int getSize() {
        int[] size = new int[1];
        GraphicsExtension.check("ShadowMap.getSize",
                NativeEngineLayerRoutes.shadowMapGetSize(open(), size));
        return size[0];
    }

    /** @return the quality preset the map was created with */
    public ShadowQuality getQuality() {
        int[] quality = new int[1];
        GraphicsExtension.check("ShadowMap.getQuality",
                NativeEngineLayerRoutes.shadowMapGetQuality(open(), quality));
        return ShadowQuality.fromValue(quality[0]);
    }

    /** @return the filter radius in texels the map's quality selects */
    public int getFilterRadius() {
        int[] radius = new int[1];
        GraphicsExtension.check("ShadowMap.getFilterRadius",
                NativeEngineLayerRoutes.shadowMapGetFilterRadius(open(), radius));
        return radius[0];
    }

    /** @return the depth bias applied when casting */
    public float getDepthBias() {
        float[] bias = new float[1];
        GraphicsExtension.check("ShadowMap.getDepthBias",
                NativeEngineLayerRoutes.shadowMapGetDepthBias(open(), bias));
        return bias[0];
    }

    /**
     * Sets the depth bias applied when casting.
     *
     * @param bias the bias
     */
    public void setDepthBias(float bias) {
        GraphicsExtension.check("ShadowMap.setDepthBias",
                NativeEngineLayerRoutes.shadowMapSetDepthBias(open(), bias));
    }

    /**
     * Releases the map, its texture and its effects. Closing twice is a no-op.
     *
     * @throws org.openeggbert.cna.internal.CnaNativeException when a shadow texture borrowed
     *         from this map has not been disposed; CNA refuses to destroy a map that is still
     *         lent out
     */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
        }
        // Marked closed only after CNA agrees. This is the one family whose release has a
        // documented refusal -- a map still lending its texture out -- and an object that
        // recorded itself closed on the way to a refusal would be unusable and undestroyable at
        // once, with the native map leaked. A refused close leaves it exactly as it was, so the
        // caller can dispose the borrow and close again.
        GraphicsExtension.check("ShadowMap.close",
                NativeEngineLayerRoutes.shadowMapDestroy(handle));
        synchronized (this) {
            closed = true;
        }
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This ShadowMap is closed");
            }
        }
        return handle;
    }
}
