package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Color;
import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Vector2;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * The depth, normal and velocity buffers every screen-space effect reads.
 *
 * <p>A CNA extension, and the thing SSAO, SSR, contact shadows, decals, soft particles and motion
 * blur all need before any of them can run. XNA gives a game a depth buffer it cannot sample and
 * nothing else, so each of those effects was out of reach.
 *
 * <p><strong>It may take more than one pass.</strong> A renderer with multiple render targets
 * fills depth and normals together; one without has to draw the scene twice, or three times with
 * velocity on. {@link #getPassCount()} says how many, and a game loops
 * {@link #begin(int, Matrix, Matrix, float, float)} / draw / {@link #end()} that many times --
 * which is why the pass index is a parameter rather than something the prepass counts for itself.
 *
 * <p><strong>Depth may be packed.</strong> Where the renderer has no float target, thirty-two
 * bits of depth are spread across an eight-bit RGBA one. {@link #isDepthPacked()} says which, and
 * {@link #packDepth}/{@link #unpackDepth} are the same arithmetic the shader uses, so a game can
 * read a depth texel on the CPU and get the number the GPU would have got.
 *
 * <p>The three textures are counted borrows: each one keeps the prepass alive and is given back
 * by disposing it, the same terms {@link ShadowMap#getShadowTexture} works on.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class DepthNormalPrepass implements AutoCloseable {

    /** CNA's own result for a buffer that could not hold the answer. */
    private static final int RESULT_BUFFER_TOO_SMALL = 14;

    private final long handle;
    private boolean closed;

    private DepthNormalPrepass(long handle) {
        this.handle = handle;
    }

    /**
     * Creates a prepass at a target size.
     *
     * @param graphicsDevice the device to render on
     * @param width the target width in pixels
     * @param height the target height in pixels
     * @param encoding how to store linear depth
     * @return the prepass, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static DepthNormalPrepass create(GraphicsDevice graphicsDevice, int width, int height,
            DepthEncoding encoding) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        Objects.requireNonNull(encoding, "encoding");
        long[] prepass = new long[1];
        GraphicsExtension.check("DepthNormalPrepass.create",
                NativeEngineLayerRoutes.depthNormalPrepassCreate(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), width, height,
                        encoding.ordinal(), prepass));
        return new DepthNormalPrepass(prepass[0]);
    }

    /**
     * Reports whether a device would store depth packed rather than as a float.
     *
     * <p>Answerable before a prepass exists, so a game can size its own buffers to match.
     *
     * @param graphicsDevice the device to ask about
     * @return whether depth would be packed across four channels
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static boolean usesPackedDepth(GraphicsDevice graphicsDevice) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        boolean[] packed = new boolean[1];
        GraphicsExtension.check("DepthNormalPrepass.usesPackedDepth",
                NativeEngineLayerRoutes.depthNormalPrepassUsesPackedDepthExt(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), packed));
        return packed[0];
    }

    /**
     * Packs a linear depth into four channel values.
     *
     * @param value the depth to pack
     * @return the four channels, red first
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static float[] packDepth(float value) {
        GraphicsExtension.requireBackend();
        float[] red = new float[1];
        float[] green = new float[1];
        float[] blue = new float[1];
        float[] alpha = new float[1];
        GraphicsExtension.check("DepthNormalPrepass.packDepth",
                NativeEngineLayerRoutes.depthNormalPrepassPackDepth(value, red, green, blue,
                        alpha));
        return new float[] {red[0], green[0], blue[0], alpha[0]};
    }

    /**
     * Unpacks four channel values back into a linear depth.
     *
     * @param red the red channel
     * @param green the green channel
     * @param blue the blue channel
     * @param alpha the alpha channel
     * @return the depth
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static float unpackDepth(float red, float green, float blue, float alpha) {
        GraphicsExtension.requireBackend();
        float[] value = new float[1];
        GraphicsExtension.check("DepthNormalPrepass.unpackDepth",
                NativeEngineLayerRoutes.depthNormalPrepassUnpackDepth(red, green, blue, alpha,
                        value));
        return value[0];
    }

    /**
     * Reports whether a velocity texel carries motion at all.
     *
     * @param texel the texel read from the velocity texture
     * @return whether it encodes motion
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static boolean hasVelocity(Color texel) {
        GraphicsExtension.requireBackend();
        boolean[] has = new boolean[1];
        GraphicsExtension.check("DepthNormalPrepass.hasVelocity",
                NativeEngineLayerRoutes.depthNormalPrepassHasVelocityExt(
                        EngineValues.channels(texel, "texel"), has));
        return has[0];
    }

    /**
     * Decodes a velocity texel into screen-space motion.
     *
     * @param texel the texel read from the velocity texture
     * @return the motion
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static Vector2 decodeVelocity(Color texel) {
        GraphicsExtension.requireBackend();
        float[] velocity = new float[2];
        GraphicsExtension.check("DepthNormalPrepass.decodeVelocity",
                NativeEngineLayerRoutes.depthNormalPrepassDecodeVelocityExt(
                        EngineValues.channels(texel, "texel"), velocity));
        return new Vector2(velocity[0], velocity[1]);
    }

    /**
     * Returns the GLSL a shader decodes depth with.
     *
     * @param packed whether to return the packed-depth variant
     * @return the GLSL
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static String getDepthDecodeGlsl(boolean packed) {
        GraphicsExtension.requireBackend();
        return text("DepthNormalPrepass.getDepthDecodeGlsl",
                (destination, bytes) -> NativeEngineLayerRoutes
                        .depthNormalPrepassCopyDepthDecodeGlsl(packed, destination, bytes));
    }

    /**
     * Returns the GLSL a shader decodes velocity with.
     *
     * @return the GLSL
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static String getVelocityDecodeGlsl() {
        GraphicsExtension.requireBackend();
        return text("DepthNormalPrepass.getVelocityDecodeGlsl",
                NativeEngineLayerRoutes::depthNormalPrepassCopyVelocityDecodeGlsl);
    }

    /**
     * Reports whether this renderer can fill the prepass at all.
     *
     * @param graphicsDevice the device to ask about
     * @return whether the prepass shaders exist and link there
     */
    public boolean isSupported(GraphicsDevice graphicsDevice) {
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        boolean[] supported = new boolean[1];
        GraphicsExtension.check("DepthNormalPrepass.isSupported",
                NativeEngineLayerRoutes.depthNormalPrepassIsSupported(open(),
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), supported));
        return supported[0];
    }

    /**
     * Resizes the prepass's targets.
     *
     * @param width the target width in pixels
     * @param height the target height in pixels
     */
    public void resize(int width, int height) {
        GraphicsExtension.check("DepthNormalPrepass.resize",
                NativeEngineLayerRoutes.depthNormalPrepassResize(open(), width, height));
    }

    /**
     * Returns how many passes this renderer needs to fill the prepass.
     *
     * <p>One where multiple render targets are available, otherwise two -- or three with
     * velocity on.
     *
     * @return the pass count
     */
    public int getPassCount() {
        int[] count = new int[1];
        GraphicsExtension.check("DepthNormalPrepass.getPassCount",
                NativeEngineLayerRoutes.depthNormalPrepassGetPassCount(open(), count));
        return count[0];
    }

    /**
     * Opens one of the prepass's passes.
     *
     * @param passIndex which pass, from zero to {@link #getPassCount()} minus one
     * @param view the camera's view matrix
     * @param projection the camera's projection matrix
     * @param nearPlane the near plane distance
     * @param farPlane the far plane distance
     */
    public void begin(int passIndex, Matrix view, Matrix projection, float nearPlane,
            float farPlane) {
        GraphicsExtension.check("DepthNormalPrepass.begin",
                NativeEngineLayerRoutes.depthNormalPrepassBegin(open(), passIndex,
                        EngineValues.floats(view, "view"),
                        EngineValues.floats(projection, "projection"), nearPlane, farPlane));
    }

    /** Closes the open pass. */
    public void end() {
        GraphicsExtension.check("DepthNormalPrepass.end",
                NativeEngineLayerRoutes.depthNormalPrepassEnd(open()));
    }

    /**
     * Borrows the linear-depth texture.
     *
     * @param graphicsDevice the device the prepass renders on
     * @return the texture, which the caller disposes, or {@code null} when there is none
     */
    public Texture2D getDepthTexture(GraphicsDevice graphicsDevice) {
        return borrow(graphicsDevice, "DepthNormalPrepass.getDepthTexture",
                NativeEngineLayerRoutes::depthNormalPrepassGetDepthTexture);
    }

    /**
     * Borrows the normal texture.
     *
     * @param graphicsDevice the device the prepass renders on
     * @return the texture, which the caller disposes, or {@code null} when there is none
     */
    public Texture2D getNormalTexture(GraphicsDevice graphicsDevice) {
        return borrow(graphicsDevice, "DepthNormalPrepass.getNormalTexture",
                NativeEngineLayerRoutes::depthNormalPrepassGetNormalTexture);
    }

    /**
     * Borrows the velocity texture.
     *
     * @param graphicsDevice the device the prepass renders on
     * @return the texture, which the caller disposes, or {@code null} when velocity is off
     */
    public Texture2D getVelocityTexture(GraphicsDevice graphicsDevice) {
        return borrow(graphicsDevice, "DepthNormalPrepass.getVelocityTexture",
                NativeEngineLayerRoutes::depthNormalPrepassGetVelocityTextureExt);
    }

    /**
     * Reports whether the renderer fills depth and normals in one pass.
     *
     * @return whether multiple render targets are in use
     */
    public boolean isUsingMultipleRenderTargets() {
        return flag("DepthNormalPrepass.isUsingMultipleRenderTargets",
                NativeEngineLayerRoutes::depthNormalPrepassIsUsingMultipleRenderTargets);
    }

    /**
     * Reports whether depth is stored packed across four channels.
     *
     * @return whether the depth texture is packed
     */
    public boolean isDepthPacked() {
        return flag("DepthNormalPrepass.isDepthPacked",
                NativeEngineLayerRoutes::depthNormalPrepassIsDepthPacked);
    }

    /** @return the roughness written into the normal buffer's spare channel */
    public float getRoughness() {
        float[] roughness = new float[1];
        GraphicsExtension.check("DepthNormalPrepass.getRoughness",
                NativeEngineLayerRoutes.depthNormalPrepassGetRoughness(open(), roughness));
        return roughness[0];
    }

    /**
     * Sets the roughness written into the normal buffer's spare channel.
     *
     * @param roughness the roughness
     */
    public void setRoughness(float roughness) {
        GraphicsExtension.check("DepthNormalPrepass.setRoughness",
                NativeEngineLayerRoutes.depthNormalPrepassSetRoughness(open(), roughness));
    }

    /**
     * Reports whether the prepass also writes screen-space velocity.
     *
     * @return whether velocity is on
     */
    public boolean isVelocityEnabled() {
        return flag("DepthNormalPrepass.isVelocityEnabled",
                NativeEngineLayerRoutes::depthNormalPrepassIsVelocityEnabledExt);
    }

    /**
     * Turns the velocity buffer on or off.
     *
     * <p>It costs a target and, on a renderer without multiple render targets, a whole extra
     * pass -- which {@link #getPassCount()} then reports.
     *
     * @param enabled whether to write velocity
     */
    public void setVelocityEnabled(boolean enabled) {
        GraphicsExtension.check("DepthNormalPrepass.setVelocityEnabled",
                NativeEngineLayerRoutes.depthNormalPrepassSetVelocityEnabledExt(open(), enabled));
    }

    /**
     * Gives the prepass the object's previous world transform, for velocity.
     *
     * <p>Velocity is the difference between where a vertex was and where it is, so a moving
     * object needs both.
     *
     * @param previousWorld the world transform the object had last frame
     */
    public void setPreviousWorld(Matrix previousWorld) {
        GraphicsExtension.check("DepthNormalPrepass.setPreviousWorld",
                NativeEngineLayerRoutes.depthNormalPrepassSetPreviousWorldExt(open(),
                        EngineValues.floats(previousWorld, "previousWorld")));
    }

    /**
     * Gives the prepass the camera's previous transform, for velocity.
     *
     * @param previousView the view matrix the camera had last frame
     * @param previousProjection the projection matrix it had last frame
     */
    public void setPreviousCamera(Matrix previousView, Matrix previousProjection) {
        GraphicsExtension.check("DepthNormalPrepass.setPreviousCamera",
                NativeEngineLayerRoutes.depthNormalPrepassSetPreviousCameraExt(open(),
                        EngineValues.floats(previousView, "previousView"),
                        EngineValues.floats(previousProjection, "previousProjection")));
    }

    /**
     * Releases the prepass and its targets. Closing twice is a no-op.
     *
     * <p>Refused while a texture borrow is outstanding, like {@link ShadowMap#close()}.
     */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
        }
        GraphicsExtension.check("DepthNormalPrepass.close",
                NativeEngineLayerRoutes.depthNormalPrepassDestroy(handle));
        synchronized (this) {
            closed = true;
        }
    }

    /** A boolean CNA answers about one prepass. */
    @FunctionalInterface
    private interface FlagRoute {
        int call(long prepass, boolean[] answer);
    }

    /** A borrowed texture CNA lends from one prepass. */
    @FunctionalInterface
    private interface TextureRoute {
        int call(long prepass, long[] answer);
    }

    /** A copy-out of UTF-8 bytes CNA sizes first. */
    @FunctionalInterface
    private interface TextRoute {
        int call(byte[] destination, long[] bytes);
    }

    private boolean flag(String operation, FlagRoute route) {
        boolean[] answer = new boolean[1];
        GraphicsExtension.check(operation, route.call(open(), answer));
        return answer[0];
    }

    private Texture2D borrow(GraphicsDevice graphicsDevice, String operation,
            TextureRoute route) {
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] texture = new long[1];
        GraphicsExtension.check(operation, route.call(open(), texture));
        if (texture[0] == 0L) {
            return null;
        }
        return NativeBindings.createBorrowedRenderTarget(graphicsDevice, texture[0]);
    }

    private static String text(String operation, TextRoute route) {
        long[] bytes = new long[1];
        int probe = route.call(new byte[0], bytes);
        if (probe != RESULT_BUFFER_TOO_SMALL) {
            GraphicsExtension.check(operation, probe);
        }
        int length = Math.toIntExact(bytes[0]);
        if (length == 0) {
            return "";
        }
        byte[] destination = new byte[length];
        GraphicsExtension.check(operation, route.call(destination, bytes));
        return new String(destination, 0, Math.toIntExact(bytes[0]), StandardCharsets.UTF_8);
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This DepthNormalPrepass is closed");
            }
        }
        return handle;
    }
}
