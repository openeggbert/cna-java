package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.Objects;

/**
 * Measures how bright a frame is and moves the exposure toward what it asks for.
 *
 * <p>A CNA extension. XNA has no tonemapping at all, so it has nothing to expose <em>for</em>;
 * this is the automatic half of CNA's HDR pipeline -- the thing that makes a dark cellar
 * gradually become visible when a player walks into it, and a bright doorway gradually stop being
 * white when they walk out.
 *
 * <p><strong>It is a compute problem, which is why the renderer decides whether it exists.</strong>
 * Measuring a frame's brightness means reducing every pixel to one number, and CNA does that with
 * a compute shader over a storage buffer. On a renderer without
 * {@link GraphicsCapability#ComputeShaders} there is nothing to construct, and {@link #create}
 * raises {@link ExtensionNotSupportedException} rather than producing a meter that answers
 * nothing. {@link #isSupported} is how to ask first.
 *
 * <p><strong>Log-average, not a plain mean.</strong> A few very bright pixels would otherwise drag
 * the whole frame dark, which is exactly the artefact automatic exposure gets blamed for.
 *
 * <p><strong>Adaptation is deliberately asymmetric, and the speeds are named for the
 * scene.</strong> An eye adjusts to darkness far more slowly than to light. A brighter scene needs
 * a <em>lower</em> exposure, so {@code brighteningPerSecond} is the speed at which the exposure
 * comes <em>down</em> -- the fast direction. Getting that backwards is the one confusion this
 * naming exists to prevent, and it is CNA's naming rather than this projection's.
 *
 * <p><strong>Two of the setters correct what they are given, and say so.</strong>
 * {@link #setExposure} clamps into the current range, so a value inside its own contract can
 * still come back different; {@link #setExposureRange} re-clamps the current exposure, so setting
 * a range can change an exposure that was just set. Both are CNA's documented behaviour and both
 * are asserted rather than hidden.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class AutoExposure implements AutoCloseable {

    private final long handle;
    private boolean closed;

    private AutoExposure(long handle) {
        this.handle = handle;
    }

    /**
     * Reports whether a device's renderer can measure a frame at all.
     *
     * @param graphicsDevice the device to ask about
     * @return whether {@link #create} can succeed
     * @throws ExtensionNotSupportedException when this build has no extended graphics layer
     */
    public static boolean isSupported(GraphicsDevice graphicsDevice) {
        return RendererCapabilities.supports(graphicsDevice, GraphicsCapability.ComputeShaders);
    }

    /**
     * Creates a meter and compiles its reduction shader.
     *
     * @param graphicsDevice the device to measure on
     * @return the meter, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer, or the renderer
     *         has no compute shaders
     */
    public static AutoExposure create(GraphicsDevice graphicsDevice) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] meter = new long[1];
        GraphicsExtension.check("AutoExposure.create",
                NativeEngineLayerRoutes.autoExposureExtCreate(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), meter));
        return new AutoExposure(meter[0]);
    }

    /**
     * Measures a frame's log-average luminance without adapting to it.
     *
     * @param scene the frame to measure
     * @return the log-average luminance, in the texture's own units
     */
    public float measureAverageLuminance(Texture2D scene) {
        Objects.requireNonNull(scene, "scene");
        float[] luminance = new float[1];
        GraphicsExtension.check("AutoExposure.measureAverageLuminance",
                NativeEngineLayerRoutes.autoExposureExtMeasureAverageLuminance(open(),
                        NativeBindings.nativeResourceHandle(scene), luminance));
        return luminance[0];
    }

    /**
     * Measures the frame and moves the exposure toward what it asks for.
     *
     * @param scene the frame to measure
     * @param deltaSeconds time since the last call; zero or less snaps straight to the target
     * @return the exposure to render the next frame with
     */
    public float update(Texture2D scene, float deltaSeconds) {
        Objects.requireNonNull(scene, "scene");
        float[] exposure = new float[1];
        GraphicsExtension.check("AutoExposure.update",
                NativeEngineLayerRoutes.autoExposureExtUpdate(open(),
                        NativeBindings.nativeResourceHandle(scene), deltaSeconds, exposure));
        return exposure[0];
    }

    /**
     * Writes the current exposure into a settings value.
     *
     * <p>The whole integration with the pipeline: a tonemap pass reads
     * {@code RenderPipelineSettingsExt}'s exposure every frame, so nothing else has to change.
     * Only the exposure is written; every other field is left as it was, which is why the
     * settings cross the boundary in both directions.
     *
     * @param settings the settings to update in place
     */
    public void applyTo(RenderPipelineSettingsExt settings) {
        Objects.requireNonNull(settings, "settings");
        byte[] bytes = new byte[4];
        long[] integral = settings.integral();
        float[] floating = settings.floating();
        GraphicsExtension.check("AutoExposure.applyTo", NativeEngineLayerRoutes
                .autoExposureExtApplyTo(open(), bytes, integral, floating));
        settings.read(integral, floating);
    }

    /**
     * Returns the exposure as it stands.
     *
     * @return the exposure
     */
    public float getExposure() {
        return value("AutoExposure.getExposure",
                NativeEngineLayerRoutes::autoExposureExtGetExposure);
    }

    /**
     * Sets the exposure directly, skipping adaptation.
     *
     * <p><strong>Clamped into the current range</strong>, so a positive value can still come back
     * different. {@link #getExposure()} is what it became.
     *
     * @param value the exposure; must be positive
     * @throws IllegalArgumentException when the value is not positive
     */
    public void setExposure(float value) {
        GraphicsExtension.check("AutoExposure.setExposure",
                NativeEngineLayerRoutes.autoExposureExtSetExposure(open(), value));
    }

    /**
     * Returns the middle grey the exposure aims to put the frame's log-average at.
     *
     * @return the key value, photography's 0.18 by default
     */
    public float getKeyValue() {
        return value("AutoExposure.getKeyValue",
                NativeEngineLayerRoutes::autoExposureExtGetKeyValue);
    }

    /**
     * Sets the middle grey the exposure aims for.
     *
     * @param value the key value; must be positive
     * @throws IllegalArgumentException when the value is not positive
     */
    public void setKeyValue(float value) {
        GraphicsExtension.check("AutoExposure.setKeyValue",
                NativeEngineLayerRoutes.autoExposureExtSetKeyValue(open(), value));
    }

    /**
     * Returns how fast the exposure adapts when the scene has become brighter.
     *
     * @return e-foldings per second; the direction in which the exposure comes down
     */
    public float getBrighteningSpeed() {
        return value("AutoExposure.getBrighteningSpeed",
                NativeEngineLayerRoutes::autoExposureExtGetBrighteningSpeed);
    }

    /**
     * Returns how fast the exposure adapts when the scene has become darker.
     *
     * @return e-foldings per second; the direction in which the exposure comes up
     */
    public float getDarkeningSpeed() {
        return value("AutoExposure.getDarkeningSpeed",
                NativeEngineLayerRoutes::autoExposureExtGetDarkeningSpeed);
    }

    /**
     * Sets both adaptation speeds.
     *
     * <p><strong>Validated as a pair</strong>: if either is not positive the call is refused and
     * neither is written, so one good value and one bad changes nothing.
     *
     * @param brighteningPerSecond speed when the scene has become brighter; must be positive
     * @param darkeningPerSecond speed when it has become darker; must be positive
     * @throws IllegalArgumentException when either speed is not positive
     */
    public void setAdaptationSpeeds(float brighteningPerSecond, float darkeningPerSecond) {
        GraphicsExtension.check("AutoExposure.setAdaptationSpeeds",
                NativeEngineLayerRoutes.autoExposureExtSetAdaptationSpeeds(open(),
                        brighteningPerSecond, darkeningPerSecond));
    }

    /**
     * Sets the range the exposure is kept within.
     *
     * <p>Validated as a pair, and <strong>the current exposure is re-clamped into the new
     * range</strong> -- so setting a range can change an exposure a caller just set.
     *
     * @param minimum the lowest exposure; must be positive
     * @param maximum the highest exposure; must not be below the minimum
     * @throws IllegalArgumentException when the minimum is not positive or the maximum is below it
     */
    public void setExposureRange(float minimum, float maximum) {
        GraphicsExtension.check("AutoExposure.setExposureRange",
                NativeEngineLayerRoutes.autoExposureExtSetExposureRange(open(), minimum, maximum));
    }

    /** Releases the meter and its GPU resources. Closing twice is a no-op. */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        GraphicsExtension.check("AutoExposure.close",
                NativeEngineLayerRoutes.autoExposureExtDestroy(handle));
    }

    /** A float CNA answers about one meter. */
    @FunctionalInterface
    private interface FloatRoute {
        int call(long meter, float[] answer);
    }

    private float value(String operation, FloatRoute route) {
        float[] answer = new float[1];
        GraphicsExtension.check(operation, route.call(open(), answer));
        return answer[0];
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This AutoExposure is closed");
            }
        }
        return handle;
    }
}
