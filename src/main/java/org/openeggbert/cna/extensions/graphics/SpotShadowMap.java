package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Vector3;
import Microsoft.Xna.Framework.Graphics.Effect;
import org.openeggbert.cna.internal.FacadeFactory;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.Objects;

/**
 * A spot light's shadow map.
 *
 * <p>A CNA extension, and the simplest of the three: a spot light is already a perspective
 * camera, so the map needs no scene bounds to fit -- {@link #begin} takes the light alone and the
 * cone's outer angle and range are the frustum.
 *
 * <p>Like {@link ShadowMap}, a map on a renderer that cannot cast still exists and its passes
 * still open and close, and its texture is a counted borrow the caller disposes.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class SpotShadowMap implements AutoCloseable {

    private final long handle;
    private boolean closed;

    private SpotShadowMap(long handle) {
        this.handle = handle;
    }

    /**
     * Creates a map at a quality preset.
     *
     * @param graphicsDevice the device to render on
     * @param quality the preset, which selects the map's size
     * @return the map, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static SpotShadowMap create(GraphicsDevice graphicsDevice, ShadowQuality quality) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        Objects.requireNonNull(quality, "quality");
        long[] map = new long[1];
        GraphicsExtension.check("SpotShadowMap.create",
                NativeEngineLayerRoutes.spotShadowMapCreate(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice),
                        quality.ordinal(), map));
        return new SpotShadowMap(map[0]);
    }

    /**
     * Computes a spot light's view transform, without a map.
     *
     * @param light the spot light
     * @return the view transform
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static Matrix computeLightView(SpotLight light) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(light, "light");
        float[] matrix = new float[EngineValues.MATRIX_LEAVES];
        GraphicsExtension.check("SpotShadowMap.computeLightView",
                NativeEngineLayerRoutes.spotShadowMapComputeLightView(new byte[3],
                        light.integral(), light.floating(), matrix));
        return EngineValues.matrix(matrix, 0);
    }

    /**
     * Computes a spot light's projection, without a map.
     *
     * <p>The cone is the frustum: the outer angle is the field of view and the range is the far
     * plane, which is why this needs no scene bounds where {@link ShadowMap} does.
     *
     * @param light the spot light
     * @return the projection
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static Matrix computeLightProjection(SpotLight light) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(light, "light");
        float[] matrix = new float[EngineValues.MATRIX_LEAVES];
        GraphicsExtension.check("SpotShadowMap.computeLightProjection",
                NativeEngineLayerRoutes.spotShadowMapComputeLightProjection(new byte[3],
                        light.integral(), light.floating(), matrix));
        return EngineValues.matrix(matrix, 0);
    }

    /**
     * Reports whether this renderer can cast into the map.
     *
     * @return whether the caster shader exists and links
     */
    public boolean isSupported() {
        boolean[] supported = new boolean[1];
        GraphicsExtension.check("SpotShadowMap.isSupported",
                NativeEngineLayerRoutes.spotShadowMapIsSupported(open(), supported));
        return supported[0];
    }

    /**
     * Opens the shadow pass for one spot light.
     *
     * @param light the spot light to cast from
     */
    public void begin(SpotLight light) {
        Objects.requireNonNull(light, "light");
        GraphicsExtension.check("SpotShadowMap.begin",
                NativeEngineLayerRoutes.spotShadowMapBegin(open(), new byte[3], light.integral(),
                        light.floating()));
    }

    /** Closes the shadow pass. */
    public void end() {
        GraphicsExtension.check("SpotShadowMap.end",
                NativeEngineLayerRoutes.spotShadowMapEnd(open()));
    }

    /**
     * Borrows the map's depth texture.
     *
     * @param graphicsDevice the device the map renders on
     * @return the texture, which the caller disposes, or {@code null} when this renderer cannot
     *         cast shadows
     */
    public Texture2D getShadowTexture(GraphicsDevice graphicsDevice) {
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] texture = new long[1];
        GraphicsExtension.check("SpotShadowMap.getShadowTexture",
                NativeEngineLayerRoutes.spotShadowMapGetShadowTexture(open(), texture));
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
        GraphicsExtension.check("SpotShadowMap.getLightViewProjection",
                NativeEngineLayerRoutes.spotShadowMapGetLightViewProjection(open(), matrix));
        return EngineValues.matrix(matrix, 0);
    }

    /**
     * Returns the position of the light the map last opened for.
     *
     * @return the position
     */
    public Vector3 getLightPosition() {
        float[] position = new float[3];
        GraphicsExtension.check("SpotShadowMap.getLightPosition",
                NativeEngineLayerRoutes.spotShadowMapGetLightPosition(open(), position));
        return new Vector3(position[0], position[1], position[2]);
    }

    /**
     * Returns the range of the light the map last opened for.
     *
     * @return the range, which is the map's far plane
     */
    public float getLightRange() {
        float[] range = new float[1];
        GraphicsExtension.check("SpotShadowMap.getLightRange",
                NativeEngineLayerRoutes.spotShadowMapGetLightRange(open(), range));
        return range[0];
    }

    /** @return the map's edge length in texels */
    public int getSize() {
        int[] size = new int[1];
        GraphicsExtension.check("SpotShadowMap.getSize",
                NativeEngineLayerRoutes.spotShadowMapGetSize(open(), size));
        return size[0];
    }

    /** @return the quality preset the map was created with */
    public ShadowQuality getQuality() {
        int[] quality = new int[1];
        GraphicsExtension.check("SpotShadowMap.getQuality",
                NativeEngineLayerRoutes.spotShadowMapGetQuality(open(), quality));
        return ShadowQuality.fromValue(quality[0]);
    }

    /** @return the depth bias applied when casting */
    public float getDepthBias() {
        float[] bias = new float[1];
        GraphicsExtension.check("SpotShadowMap.getDepthBias",
                NativeEngineLayerRoutes.spotShadowMapGetDepthBias(open(), bias));
        return bias[0];
    }

    /**
     * Sets the depth bias applied when casting.
     *
     * @param bias the bias
     */
    public void setDepthBias(float bias) {
        GraphicsExtension.check("SpotShadowMap.setDepthBias",
                NativeEngineLayerRoutes.spotShadowMapSetDepthBias(open(), bias));
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
     * <p><strong>This lender, unlike its three siblings, does NOT refuse to be destroyed while a view
     * is outstanding: measured, and recorded as JAVA-UPSTREAM-013. The view keeps the effect
     * alive and may be disposed afterwards, so it is safe -- but a caller relying on the
     * refusal to catch a leak will not be caught out by this one.</strong>
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
        GraphicsExtension.check("SpotShadowMap.getCasterEffect", NativeEngineLayerRoutes
                .spotShadowMapGetCasterEffect(open(), effect));
        return effect[0] == 0L ? null
                : FacadeFactory.createBorrowedEffect(graphicsDevice, effect[0]);
    }

    /** Releases the map and its texture. Closing twice is a no-op. */
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
        GraphicsExtension.check("SpotShadowMap.close",
                NativeEngineLayerRoutes.spotShadowMapDestroy(handle));
        synchronized (this) {
            closed = true;
        }
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This SpotShadowMap is closed");
            }
        }
        return handle;
    }
}
