package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Color;
import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.SurfaceFormat;
import Microsoft.Xna.Framework.Matrix;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The frame CNA's engine layer draws, from clear to present.
 *
 * <p>A CNA extension, and the object the rest of the layer hangs off. XNA has
 * {@code GraphicsDevice.Clear} and {@code Draw*}, and everything between them is the game's own
 * problem; a pipeline owns the offscreen targets, runs the post-process passes the
 * {@link RenderPipelineSettingsExt} asks for, and afterwards says what it did.
 *
 * <p>A frame is {@link #resize} once when the window changes, then {@link #setCamera}, then
 * {@link #begin(Color)}, the game's own drawing, {@link #end()}. {@link #getStatistics()} reports
 * what that frame cost.
 *
 * <p><strong>Passes are borrowed, never owned.</strong> {@link #addUserPass} records a pass and
 * the caller keeps it alive for as long as it is registered; closing a pass the pipeline still
 * holds is the caller's mistake to avoid. {@link #clearUserPasses()} is how to stop.
 *
 * <p><strong>What is deliberately not here.</strong> {@code set_transparent_scene} and
 * {@code set_shadow_scene} each take a draw callback the generator refuses, so the pipeline's
 * transparent and shadow passes cannot be driven from Java yet; {@code get_scene_target} returns
 * a borrow with no release route and no documented lifetime, so a Java facade over it would be a
 * dangling texture waiting to happen -- {@link #isUsingSceneTarget()} and
 * {@link #getSceneTargetFormat()} are the safe half of that question, and a game that needs the
 * pixels should render to a target of its own.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class RenderPipeline implements AutoCloseable {

    /** CNA's own result for a buffer that could not hold the answer. */
    private static final int RESULT_BUFFER_TOO_SMALL = 14;

    private final long handle;
    // Retained so a pass cannot be collected while the pipeline still names it. CNA borrows
    // rather than owns, so this list is what makes "the caller keeps it alive" true by default.
    private final List<PostProcessPass> userPasses = new ArrayList<>();
    private boolean closed;

    private RenderPipeline(long handle) {
        this.handle = handle;
    }

    /**
     * Creates a pipeline on a device.
     *
     * @param graphicsDevice the device to render on
     * @return the pipeline, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static RenderPipeline create(GraphicsDevice graphicsDevice) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] pipeline = new long[1];
        GraphicsExtension.check("RenderPipeline.create",
                NativeEngineLayerRoutes.renderPipelineCreate(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), pipeline));
        return new RenderPipeline(pipeline[0]);
    }

    /**
     * Returns the settings the pipeline is running with.
     *
     * @return a copy of the settings
     */
    public RenderPipelineSettingsExt getSettings() {
        RenderPipelineSettingsExt settings = new RenderPipelineSettingsExt();
        long[] integral = new long[15];
        float[] floating = new float[32];
        GraphicsExtension.check("RenderPipeline.getSettings",
                NativeEngineLayerRoutes.renderPipelineGetSettings(open(), new byte[4], integral,
                        floating));
        settings.read(integral, floating);
        return settings;
    }

    /**
     * Gives the pipeline new settings.
     *
     * <p>The pipeline corrects what it stores; ask {@link #getSettings()} back, or run
     * {@link RenderPipelineSettingsExt#normalize()} first, rather than assuming the value went in
     * unchanged.
     *
     * @param settings the settings to run with
     */
    public void setSettings(RenderPipelineSettingsExt settings) {
        Objects.requireNonNull(settings, "settings");
        GraphicsExtension.check("RenderPipeline.setSettings",
                NativeEngineLayerRoutes.renderPipelineSetSettings(open(), new byte[4],
                        settings.integral(), settings.floating()));
    }

    /**
     * Sizes the pipeline's targets.
     *
     * <p>Must happen before the first {@link #begin(Color)}: an unsized pipeline has no targets
     * to render into and says so rather than guessing a size.
     *
     * @param width the width in pixels
     * @param height the height in pixels
     */
    public void resize(int width, int height) {
        GraphicsExtension.check("RenderPipeline.resize",
                NativeEngineLayerRoutes.renderPipelineResize(open(), width, height));
    }

    /**
     * Opens a frame, clearing to a colour.
     *
     * @param clearColor the colour to clear to
     * @throws IllegalStateException when a frame is already open, or the pipeline has never been
     *         sized
     */
    public void begin(Color clearColor) {
        GraphicsExtension.check("RenderPipeline.begin",
                NativeEngineLayerRoutes.renderPipelineBegin(open(),
                        EngineValues.channels(clearColor, "clearColor")));
    }

    /** Closes the frame, running the post-process passes. */
    public void end() {
        GraphicsExtension.check("RenderPipeline.end",
                NativeEngineLayerRoutes.renderPipelineEnd(open()));
    }

    /**
     * Sets the camera the pipeline renders from.
     *
     * @param view the view matrix
     * @param projection the projection matrix
     * @param nearPlane the near plane distance
     * @param farPlane the far plane distance
     */
    public void setCamera(Matrix view, Matrix projection, float nearPlane, float farPlane) {
        GraphicsExtension.check("RenderPipeline.setCamera",
                NativeEngineLayerRoutes.renderPipelineSetCamera(open(),
                        EngineValues.floats(view, "view"),
                        EngineValues.floats(projection, "projection"), nearPlane, farPlane));
    }

    /**
     * Sets the camera the skybox is drawn with, which is usually the same rotation and no
     * translation.
     *
     * @param view the view matrix
     * @param projection the projection matrix
     */
    public void setSkyboxCamera(Matrix view, Matrix projection) {
        GraphicsExtension.check("RenderPipeline.setSkyboxCamera",
                NativeEngineLayerRoutes.renderPipelineSetSkyboxCamera(open(),
                        EngineValues.floats(view, "view"),
                        EngineValues.floats(projection, "projection")));
    }

    /**
     * Appends a pass to run after the built-in ones.
     *
     * <p>The pipeline borrows it; this object holds a reference so the pass cannot be collected
     * while the pipeline still names it, but closing the pass yourself while it is registered is
     * still a mistake.
     *
     * @param pass the pass to append
     */
    public void addUserPass(PostProcessPass pass) {
        Objects.requireNonNull(pass, "pass");
        GraphicsExtension.check("RenderPipeline.addUserPass",
                NativeEngineLayerRoutes.renderPipelineAddUserPass(open(), pass.handle()));
        synchronized (userPasses) {
            userPasses.add(pass);
        }
    }

    /** Forgets every user pass. */
    public void clearUserPasses() {
        GraphicsExtension.check("RenderPipeline.clearUserPasses",
                NativeEngineLayerRoutes.renderPipelineClearUserPasses(open()));
        synchronized (userPasses) {
            userPasses.clear();
        }
    }

    /**
     * Returns the user passes this pipeline is holding.
     *
     * @return the passes, in the order they were added
     */
    public List<PostProcessPass> getUserPasses() {
        synchronized (userPasses) {
            return List.copyOf(userPasses);
        }
    }

    /**
     * Gives the pipeline the depth and normal buffers its passes read.
     *
     * <p>Both are borrowed and neither is retained here, because a game that hands over a target
     * it then disposes has made a mistake this projection cannot see.
     *
     * @param depth the depth texture, or {@code null} for none
     * @param normals the normal texture, or {@code null} for none
     */
    public void setDepthNormalInputs(Microsoft.Xna.Framework.Graphics.Texture2D depth,
            Microsoft.Xna.Framework.Graphics.Texture2D normals) {
        GraphicsExtension.check("RenderPipeline.setDepthNormalInputs",
                NativeEngineLayerRoutes.renderPipelineSetDepthNormalInputs(open(),
                        handleOrNone(depth), handleOrNone(normals)));
    }

    /**
     * Gives the pipeline the velocity buffer motion blur reads.
     *
     * @param velocity the velocity texture, or {@code null} for none
     */
    public void setVelocityInput(Microsoft.Xna.Framework.Graphics.Texture2D velocity) {
        GraphicsExtension.check("RenderPipeline.setVelocityInput",
                NativeEngineLayerRoutes.renderPipelineSetVelocityInputExt(open(),
                        handleOrNone(velocity)));
    }

    /**
     * Reports whether the pipeline renders through an offscreen target rather than straight to
     * the back buffer.
     *
     * @return whether a scene target is in use
     */
    public boolean isUsingSceneTarget() {
        boolean[] using = new boolean[1];
        GraphicsExtension.check("RenderPipeline.isUsingSceneTarget",
                NativeEngineLayerRoutes.renderPipelineIsUsingSceneTarget(open(), using));
        return using[0];
    }

    /**
     * Returns the format of the offscreen scene target.
     *
     * @return the format, whatever the pipeline would use if it needed one
     */
    public SurfaceFormat getSceneTargetFormat() {
        int[] format = new int[1];
        GraphicsExtension.check("RenderPipeline.getSceneTargetFormat",
                NativeEngineLayerRoutes.renderPipelineGetSceneTargetFormat(open(), format));
        SurfaceFormat[] values = SurfaceFormat.values();
        if (format[0] < 0 || format[0] >= values.length) {
            throw new IllegalStateException("CNA reported surface format " + format[0]
                    + ", which this build has no constant for");
        }
        return values[format[0]];
    }

    /**
     * Reports whether the skybox drew in the last frame.
     *
     * @return whether it drew
     */
    public boolean didSkyboxDraw() {
        boolean[] drew = new boolean[1];
        GraphicsExtension.check("RenderPipeline.didSkyboxDraw",
                NativeEngineLayerRoutes.renderPipelineDidSkyboxDraw(open(), drew));
        return drew[0];
    }

    /**
     * Reports whether the shadow pass ran in the last frame.
     *
     * @return whether it ran
     */
    public boolean didShadowPassRun() {
        boolean[] ran = new boolean[1];
        GraphicsExtension.check("RenderPipeline.didShadowPassRun",
                NativeEngineLayerRoutes.renderPipelineDidShadowPassRun(open(), ran));
        return ran[0];
    }

    /**
     * Returns how many post-process passes ran in the last frame.
     *
     * @return the count
     */
    public int getLastFramePassCount() {
        int[] count = new int[1];
        GraphicsExtension.check("RenderPipeline.getLastFramePassCount",
                NativeEngineLayerRoutes.renderPipelineGetLastFramePassCount(open(), count));
        return count[0];
    }

    /**
     * Returns roughly how much GPU memory the pipeline's targets hold.
     *
     * @return the estimate in bytes
     */
    public long getGpuMemoryEstimateBytes() {
        long[] bytes = new long[1];
        GraphicsExtension.check("RenderPipeline.getGpuMemoryEstimateBytes",
                NativeEngineLayerRoutes.renderPipelineGetGpuMemoryEstimateBytes(open(), bytes));
        return bytes[0];
    }

    /**
     * Returns what the last frame did, in one value.
     *
     * @return the statistics
     */
    public RenderPipelineFrameStatistics getStatistics() {
        long[] integral = new long[5];
        GraphicsExtension.check("RenderPipeline.getStatistics",
                NativeEngineLayerRoutes.renderPipelineGetStatistics(open(), new byte[2],
                        integral));
        return new RenderPipelineFrameStatistics(
                Math.toIntExact(integral[0]), Math.toIntExact(integral[1]),
                integral[2] != 0L, integral[3] != 0L, integral[4]);
    }

    /**
     * Reports whether the pipeline times each pass on the GPU.
     *
     * @return whether timing is on
     */
    public boolean isGpuTimingEnabled() {
        boolean[] enabled = new boolean[1];
        GraphicsExtension.check("RenderPipeline.isGpuTimingEnabled",
                NativeEngineLayerRoutes.renderPipelineIsGpuTimingEnabledExt(open(), enabled));
        return enabled[0];
    }

    /**
     * Turns per-pass GPU timing on or off.
     *
     * <p>On a renderer with no timer query the timings exist but are never sampled; see
     * {@link PassTiming#isMeasured()}.
     *
     * @param enabled whether to time
     */
    public void setGpuTimingEnabled(boolean enabled) {
        GraphicsExtension.check("RenderPipeline.setGpuTimingEnabled",
                NativeEngineLayerRoutes.renderPipelineSetGpuTimingEnabledExt(open(), enabled));
    }

    /**
     * Returns how long each pass took, by name.
     *
     * @return one timing per pass the pipeline knows about
     */
    public List<PassTiming> getPassTimings() {
        long pipeline = open();
        long[] count = new long[1];
        GraphicsExtension.check("RenderPipeline.getPassTimings",
                NativeEngineLayerRoutes.renderPipelineGetPassTimingCountExt(pipeline, count));
        int passes = Math.toIntExact(count[0]);
        List<PassTiming> timings = new ArrayList<>(passes);
        for (int index = 0; index < passes; index++) {
            long[] integral = new long[1];
            double[] doubles = new double[1];
            GraphicsExtension.check("RenderPipeline.getPassTimings",
                    NativeEngineLayerRoutes.renderPipelineGetPassTimingExt(pipeline, index,
                            new byte[4], integral, doubles));
            timings.add(new PassTiming(
                    timingName(pipeline, index), Math.toIntExact(integral[0]), doubles[0]));
        }
        return List.copyOf(timings);
    }

    /**
     * Returns why the transparency pass fell back, when it did.
     *
     * @return the reason, or an empty string when it did not
     */
    public String getTransparencyFallbackReason() {
        return text("RenderPipeline.getTransparencyFallbackReason",
                (destination, bytes) -> NativeEngineLayerRoutes
                        .renderPipelineCopyTransparencyFallbackReasonExt(open(), destination,
                                bytes));
    }

    /**
     * Releases the device resources the pipeline holds, keeping the pipeline itself.
     *
     * <p>For a game that wants its memory back while a level is unloaded; the next
     * {@link #resize} builds them again.
     */
    public void releaseDeviceResources() {
        GraphicsExtension.check("RenderPipeline.releaseDeviceResources",
                NativeEngineLayerRoutes.renderPipelineReleaseDeviceResourcesExt(open()));
    }

    /** Releases the pipeline and its targets. Closing twice is a no-op. */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        synchronized (userPasses) {
            userPasses.clear();
        }
        GraphicsExtension.check("RenderPipeline.close",
                NativeEngineLayerRoutes.renderPipelineDestroy(handle));
    }

    /** A copy-out of UTF-8 bytes CNA sizes first. */
    @FunctionalInterface
    private interface TextRoute {
        int call(byte[] destination, long[] bytes);
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

    private static String timingName(long pipeline, int index) {
        return text("RenderPipeline.getPassTimings",
                (destination, bytes) -> NativeEngineLayerRoutes
                        .renderPipelineCopyPassTimingNameExt(pipeline, index, destination,
                                bytes));
    }

    private static long handleOrNone(Microsoft.Xna.Framework.Graphics.Texture2D texture) {
        return texture == null ? 0L : NativeBindings.nativeResourceHandle(texture);
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This RenderPipeline is closed");
            }
        }
        return handle;
    }
}
