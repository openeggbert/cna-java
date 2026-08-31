package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.CubeMapFace;
import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.TextureCube;
import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.Objects;

/**
 * A point light's shadow cube.
 *
 * <p>A CNA extension. A point light shines in every direction, so its shadow is six maps rather
 * than one: a frame is {@link #update} once with the light, then {@link #begin(CubeMapFace)},
 * draw, {@link #end()} six times, once per face.
 *
 * <p><strong>{@link #update} first, every frame.</strong> It recomputes all six face transforms
 * from the light, and a face opened without it casts from wherever the light was last time. CNA
 * documents that order and this projection restates it because nothing enforces it.
 *
 * <p>Like {@link ShadowMap}, a cube on a renderer that cannot cast still exists, and its texture
 * is a counted borrow the caller disposes.
 *
 * <p><strong>Unlike {@link ShadowMap}, its passes do not open on such a renderer.</strong> CNA
 * says a cube's face passes still open and close where it cannot cast -- its own startup log
 * says so -- and on the HEADLESS renderer they do not: binding a cube face fails where binding
 * the 2D map's target succeeds. Measured in C by
 * {@code tools/native-abi/probes/engine_layer_families.c} and recorded as
 * {@code JAVA-UPSTREAM-007}. A game that runs its point-shadow pass unconditionally must
 * therefore check {@link #isSupported()} first, which the directional map does not require.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class CubeShadowMap implements AutoCloseable {

    /** {@code CNA_CUBE_SHADOW_FACE_COUNT_EXT}: six, and it is not going to change. */
    public static final int FaceCount = 6;

    private final long handle;
    private boolean closed;

    private CubeShadowMap(long handle) {
        this.handle = handle;
    }

    /**
     * Creates a cube at a quality preset.
     *
     * @param graphicsDevice the device to render on
     * @param quality the preset, which selects each face's size
     * @return the cube, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static CubeShadowMap create(GraphicsDevice graphicsDevice, ShadowQuality quality) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        Objects.requireNonNull(quality, "quality");
        long[] map = new long[1];
        GraphicsExtension.check("CubeShadowMap.create",
                NativeEngineLayerRoutes.cubeShadowMapCreate(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice),
                        quality.ordinal(), map));
        return new CubeShadowMap(map[0]);
    }

    /**
     * Returns the face size a quality preset selects, without creating a cube.
     *
     * <p>Its own route rather than {@link ShadowMap#getSizeForQuality}'s: a cube is six faces and
     * costs six times the memory, so CNA sizes it differently.
     *
     * @param quality the preset
     * @return each face's edge length in texels
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static int getSizeForQuality(ShadowQuality quality) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(quality, "quality");
        int[] size = new int[1];
        GraphicsExtension.check("CubeShadowMap.getSizeForQuality",
                NativeEngineLayerRoutes.cubeShadowMapSizeForQuality(quality.ordinal(), size));
        return size[0];
    }

    /**
     * Computes one face's view transform, without a cube.
     *
     * @param face which face
     * @param position the light's world-space position
     * @return the view transform
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static Matrix computeFaceView(CubeMapFace face, Vector3 position) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(face, "face");
        float[] matrix = new float[EngineValues.MATRIX_LEAVES];
        GraphicsExtension.check("CubeShadowMap.computeFaceView",
                NativeEngineLayerRoutes.cubeShadowMapComputeFaceView(face.ordinal(),
                        EngineValues.floats(position, "position"), matrix));
        return EngineValues.matrix(matrix, 0);
    }

    /**
     * Computes the projection every face shares, without a cube.
     *
     * <p>Ninety degrees square, so the six of them tile the sphere; the range is the far plane.
     *
     * @param range the light's range
     * @return the projection
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static Matrix computeFaceProjection(float range) {
        GraphicsExtension.requireBackend();
        float[] matrix = new float[EngineValues.MATRIX_LEAVES];
        GraphicsExtension.check("CubeShadowMap.computeFaceProjection",
                NativeEngineLayerRoutes.cubeShadowMapComputeFaceProjection(range, matrix));
        return EngineValues.matrix(matrix, 0);
    }

    /**
     * Reports whether this renderer can cast into the cube.
     *
     * @return whether the caster shader exists and links
     */
    public boolean isSupported() {
        boolean[] supported = new boolean[1];
        GraphicsExtension.check("CubeShadowMap.isSupported",
                NativeEngineLayerRoutes.cubeShadowMapIsSupported(open(), supported));
        return supported[0];
    }

    /**
     * Recomputes all six face transforms for a point light.
     *
     * <p>Once per frame, before any face is opened.
     *
     * @param light the point light to cast from
     */
    public void update(PointLight light) {
        Objects.requireNonNull(light, "light");
        GraphicsExtension.check("CubeShadowMap.update",
                NativeEngineLayerRoutes.cubeShadowMapUpdate(open(), new byte[3],
                        light.integral(), light.floating()));
    }

    /**
     * Opens the pass for one face.
     *
     * @param face which face
     */
    public void begin(CubeMapFace face) {
        Objects.requireNonNull(face, "face");
        GraphicsExtension.check("CubeShadowMap.begin",
                NativeEngineLayerRoutes.cubeShadowMapBegin(open(), face.ordinal()));
    }

    /** Closes the face pass. */
    public void end() {
        GraphicsExtension.check("CubeShadowMap.end",
                NativeEngineLayerRoutes.cubeShadowMapEnd(open()));
    }

    /**
     * Borrows the cube's depth texture.
     *
     * @param graphicsDevice the device the cube renders on
     * @return the texture, which the caller disposes, or {@code null} when this renderer cannot
     *         cast shadows
     */
    public TextureCube getShadowTexture(GraphicsDevice graphicsDevice) {
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] texture = new long[1];
        GraphicsExtension.check("CubeShadowMap.getShadowTexture",
                NativeEngineLayerRoutes.cubeShadowMapGetShadowTexture(open(), texture));
        if (texture[0] == 0L) {
            return null;
        }
        return NativeBindings.createBorrowedTextureCube(graphicsDevice, texture[0]);
    }

    /**
     * Returns the position of the light the cube last updated for.
     *
     * @return the position
     */
    public Vector3 getLightPosition() {
        float[] position = new float[3];
        GraphicsExtension.check("CubeShadowMap.getLightPosition",
                NativeEngineLayerRoutes.cubeShadowMapGetLightPosition(open(), position));
        return new Vector3(position[0], position[1], position[2]);
    }

    /**
     * Returns the range of the light the cube last updated for.
     *
     * @return the range, which is every face's far plane
     */
    public float getLightRange() {
        float[] range = new float[1];
        GraphicsExtension.check("CubeShadowMap.getLightRange",
                NativeEngineLayerRoutes.cubeShadowMapGetLightRange(open(), range));
        return range[0];
    }

    /** @return each face's edge length in texels */
    public int getSize() {
        int[] size = new int[1];
        GraphicsExtension.check("CubeShadowMap.getSize",
                NativeEngineLayerRoutes.cubeShadowMapGetSize(open(), size));
        return size[0];
    }

    /** @return the quality preset the cube was created with */
    public ShadowQuality getQuality() {
        int[] quality = new int[1];
        GraphicsExtension.check("CubeShadowMap.getQuality",
                NativeEngineLayerRoutes.cubeShadowMapGetQuality(open(), quality));
        return ShadowQuality.fromValue(quality[0]);
    }

    /** @return the depth bias applied when casting */
    public float getDepthBias() {
        float[] bias = new float[1];
        GraphicsExtension.check("CubeShadowMap.getDepthBias",
                NativeEngineLayerRoutes.cubeShadowMapGetDepthBias(open(), bias));
        return bias[0];
    }

    /**
     * Sets the depth bias applied when casting.
     *
     * @param bias the bias
     */
    public void setDepthBias(float bias) {
        GraphicsExtension.check("CubeShadowMap.setDepthBias",
                NativeEngineLayerRoutes.cubeShadowMapSetDepthBias(open(), bias));
    }

    /** Releases the cube and its texture. Closing twice is a no-op. */
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
        GraphicsExtension.check("CubeShadowMap.close",
                NativeEngineLayerRoutes.cubeShadowMapDestroy(handle));
        synchronized (this) {
            closed = true;
        }
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This CubeShadowMap is closed");
            }
        }
        return handle;
    }
}
