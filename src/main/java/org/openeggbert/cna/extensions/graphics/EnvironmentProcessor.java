package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.CubeMapFace;
import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import Microsoft.Xna.Framework.Graphics.TextureCube;
import Microsoft.Xna.Framework.Vector2;
import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.Objects;

/**
 * Turns a photograph of a place into the textures that light a scene from it.
 *
 * <p>A CNA extension, and the whole of image-based lighting's preparation. An artist hands over
 * an equirectangular panorama; a renderer needs a cube map to draw as a sky, an irradiance cube
 * for diffuse light, a prefiltered specular cube whose mip levels are roughness levels, and a
 * BRDF lookup table. This produces all four, and {@link #generateProbe} reduces a whole
 * environment to one {@link LightProbe}.
 *
 * <p><strong>Everything it generates is owned by the caller.</strong> These are new textures, not
 * views of the processor's own, so each one is disposed by whoever asked for it and the processor
 * can be closed without touching them.
 *
 * <p>The sampling arithmetic is exposed too -- {@link #hammersley},
 * {@link #importanceSampleGgx}, {@link #faceDirection},
 * {@link #directionToEquirectangular}, {@link #mipForRoughness} and
 * {@link #roughnessForMip} -- because a game whose own shader samples an environment has to
 * sample it the way this prefiltered it.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class EnvironmentProcessor implements AutoCloseable {

    private final long handle;
    private boolean closed;

    private EnvironmentProcessor(long handle) {
        this.handle = handle;
    }

    /**
     * Creates a processor on a device.
     *
     * @param graphicsDevice the device to generate on
     * @return the processor, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static EnvironmentProcessor create(GraphicsDevice graphicsDevice) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] processor = new long[1];
        GraphicsExtension.check("EnvironmentProcessor.create",
                NativeEngineLayerRoutes.environmentProcessorCreate(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), processor));
        return new EnvironmentProcessor(processor[0]);
    }

    /**
     * Returns the mip level a roughness samples the prefiltered specular cube at.
     *
     * @param roughness the surface roughness
     * @param mipCount how many mip levels the cube has
     * @return the mip level, which need not be a whole number
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static float mipForRoughness(float roughness, int mipCount) {
        GraphicsExtension.requireBackend();
        float[] mip = new float[1];
        GraphicsExtension.check("EnvironmentProcessor.mipForRoughness",
                NativeEngineLayerRoutes.environmentProcessorMipForRoughness(roughness, mipCount,
                        mip));
        return mip[0];
    }

    /**
     * Returns the roughness a mip level of the prefiltered specular cube was filtered at.
     *
     * <p>The inverse of {@link #mipForRoughness}, and the reason both exist: the prefilter walks
     * the levels and the shader walks back.
     *
     * @param mip the mip level
     * @param mipCount how many mip levels the cube has
     * @return the roughness
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static float roughnessForMip(float mip, int mipCount) {
        GraphicsExtension.requireBackend();
        float[] roughness = new float[1];
        GraphicsExtension.check("EnvironmentProcessor.roughnessForMip",
                NativeEngineLayerRoutes.environmentProcessorRoughnessForMip(mip, mipCount,
                        roughness));
        return roughness[0];
    }

    /**
     * Returns one point of the low-discrepancy sequence the prefilter samples with.
     *
     * @param index which point
     * @param count how many points there are
     * @return the point, both components in zero-to-one
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static Vector2 hammersley(int index, int count) {
        GraphicsExtension.requireBackend();
        float[] x = new float[1];
        float[] y = new float[1];
        GraphicsExtension.check("EnvironmentProcessor.hammersley",
                NativeEngineLayerRoutes.environmentProcessorHammersley(index, count, x, y));
        return new Vector2(x[0], y[0]);
    }

    /**
     * Turns one sequence point into a direction around a normal, weighted by roughness.
     *
     * <p>The importance sampling the prefilter is built on: a smooth surface's samples cluster
     * around the normal and a rough one's spread out, which is what makes the mip levels mean
     * roughness at all.
     *
     * @param point a point from {@link #hammersley}
     * @param normal the surface normal
     * @param roughness the surface roughness
     * @return the sampled direction
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static Vector3 importanceSampleGgx(Vector2 point, Vector3 normal, float roughness) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(point, "point");
        float[] direction = new float[3];
        GraphicsExtension.check("EnvironmentProcessor.importanceSampleGgx",
                NativeEngineLayerRoutes.environmentProcessorImportanceSampleGgx(point.X, point.Y,
                        EngineValues.floats(normal, "normal"), roughness, direction));
        return new Vector3(direction[0], direction[1], direction[2]);
    }

    /**
     * Returns the direction one point of one cube face looks along.
     *
     * @param face which face
     * @param u the horizontal coordinate across the face, in zero-to-one
     * @param v the vertical coordinate, in zero-to-one
     * @return the direction
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static Vector3 faceDirection(CubeMapFace face, float u, float v) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(face, "face");
        float[] direction = new float[3];
        GraphicsExtension.check("EnvironmentProcessor.faceDirection",
                NativeEngineLayerRoutes.environmentProcessorFaceDirection(face.ordinal(), u, v,
                        direction));
        return new Vector3(direction[0], direction[1], direction[2]);
    }

    /**
     * Returns where a direction falls in an equirectangular panorama.
     *
     * <p>The inverse of {@link #faceDirection} composed with the projection, and what
     * {@link #convertEquirectangular} walks.
     *
     * @param direction the direction
     * @return the panorama coordinates, both in zero-to-one
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static Vector2 directionToEquirectangular(Vector3 direction) {
        GraphicsExtension.requireBackend();
        float[] u = new float[1];
        float[] v = new float[1];
        GraphicsExtension.check("EnvironmentProcessor.directionToEquirectangular",
                NativeEngineLayerRoutes.environmentProcessorDirectionToEquirectangular(
                        EngineValues.floats(direction, "direction"), u, v));
        return new Vector2(u[0], v[0]);
    }

    /**
     * Converts an equirectangular panorama into a cube map.
     *
     * @param graphicsDevice the device the result belongs to
     * @param panorama the panorama; it must have pixels
     * @param faceSize the cube-face resolution; must be positive
     * @return a new cube map, which the caller disposes
     */
    public TextureCube convertEquirectangular(GraphicsDevice graphicsDevice, Texture2D panorama,
            int faceSize) {
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        Objects.requireNonNull(panorama, "panorama");
        long[] environment = new long[1];
        GraphicsExtension.check("EnvironmentProcessor.convertEquirectangular",
                NativeEngineLayerRoutes.environmentProcessorConvertEquirectangular(open(),
                        NativeBindings.nativeResourceHandle(panorama), faceSize, environment));
        return NativeBindings.adoptTextureCube(graphicsDevice, environment[0]);
    }

    /**
     * Generates the irradiance cube a diffuse surface reads.
     *
     * @param graphicsDevice the device the result belongs to
     * @param environment the environment cube map
     * @param size the result's face resolution
     * @param sampleCount how many samples each texel integrates over
     * @return a new cube map, which the caller disposes
     */
    public TextureCube generateIrradiance(GraphicsDevice graphicsDevice, TextureCube environment,
            int size, int sampleCount) {
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        Objects.requireNonNull(environment, "environment");
        long[] irradiance = new long[1];
        GraphicsExtension.check("EnvironmentProcessor.generateIrradiance",
                NativeEngineLayerRoutes.environmentProcessorGenerateIrradiance(open(),
                        NativeBindings.nativeResourceHandle(environment), size, sampleCount,
                        irradiance));
        return NativeBindings.adoptTextureCube(graphicsDevice, irradiance[0]);
    }

    /**
     * Generates the specular cube whose mip levels are roughness levels.
     *
     * @param graphicsDevice the device the result belongs to
     * @param environment the environment cube map
     * @param baseSize the sharpest level's face resolution
     * @param mipCount how many roughness levels to prefilter
     * @param sampleCount how many samples each texel integrates over
     * @return a new cube map, which the caller disposes
     */
    public TextureCube generatePrefilteredSpecular(GraphicsDevice graphicsDevice,
            TextureCube environment, int baseSize, int mipCount, int sampleCount) {
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        Objects.requireNonNull(environment, "environment");
        long[] specular = new long[1];
        GraphicsExtension.check("EnvironmentProcessor.generatePrefilteredSpecular",
                NativeEngineLayerRoutes.environmentProcessorGeneratePrefilteredSpecular(open(),
                        NativeBindings.nativeResourceHandle(environment), baseSize, mipCount,
                        sampleCount, specular));
        return NativeBindings.adoptTextureCube(graphicsDevice, specular[0]);
    }

    /**
     * Generates the BRDF lookup a shader combines the specular cube with.
     *
     * @param graphicsDevice the device the result belongs to
     * @param size the table's edge length in texels
     * @param sampleCount how many samples each texel integrates over
     * @return a new texture, which the caller disposes
     */
    public Texture2D generateBrdfLut(GraphicsDevice graphicsDevice, int size, int sampleCount) {
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] lut = new long[1];
        GraphicsExtension.check("EnvironmentProcessor.generateBrdfLut",
                NativeEngineLayerRoutes.environmentProcessorGenerateBrdfLut(open(), size,
                        sampleCount, lut));
        return NativeBindings.adoptTexture2D(graphicsDevice, lut[0]);
    }

    /**
     * Reduces a whole environment to one light probe at a position.
     *
     * <p>The bridge between image-based lighting and {@link LightProbeVolume}: a room lit by a
     * photographed sky becomes nine coefficients that cost nothing to evaluate.
     *
     * @param environment the environment cube map
     * @param position where the probe sits
     * @return a new probe, which the caller closes
     */
    public LightProbe generateProbe(TextureCube environment, Vector3 position) {
        Objects.requireNonNull(environment, "environment");
        long[] probe = new long[1];
        GraphicsExtension.check("EnvironmentProcessor.generateProbe",
                NativeEngineLayerRoutes.environmentProcessorGenerateProbe(open(),
                        NativeBindings.nativeResourceHandle(environment),
                        EngineValues.floats(position, "position"), probe));
        return LightProbe.adopt(probe[0]);
    }

    /** Releases the processor. What it generated is untouched. Closing twice is a no-op. */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        GraphicsExtension.check("EnvironmentProcessor.close",
                NativeEngineLayerRoutes.environmentProcessorDestroy(handle));
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This EnvironmentProcessor is closed");
            }
        }
        return handle;
    }
}
