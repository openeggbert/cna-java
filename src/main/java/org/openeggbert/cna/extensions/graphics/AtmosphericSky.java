package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * A sky computed from where the sun is and how much is in the air.
 *
 * <p>A CNA extension. XNA's answer to a sky was a textured cube a game authored by hand, which
 * cannot change with the time of day; this is the scattering model, so moving
 * {@link #setSunDirection} moves sunset across the sky.
 *
 * <p><strong>The model runs on the CPU too.</strong> {@link #radiance} evaluates the same
 * function the shader does, with no device at all -- for a CPU-side ambient term, for a
 * lighting tool, or simply to see what the sky will look like before drawing it. It takes the
 * turbidity <em>as given</em> where {@link #setTurbidity} clamps it into the model's range, which
 * is a difference worth knowing rather than discovering.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class AtmosphericSky implements AutoCloseable {

    /** CNA's own result for a buffer that could not hold the answer. */
    private static final int RESULT_BUFFER_TOO_SMALL = 14;

    private final long handle;
    private boolean closed;

    private AtmosphericSky(long handle) {
        this.handle = handle;
    }

    /**
     * Creates a sky on a device.
     *
     * @param graphicsDevice the device to draw on
     * @return the sky, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static AtmosphericSky create(GraphicsDevice graphicsDevice) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] sky = new long[1];
        GraphicsExtension.check("AtmosphericSky.create",
                NativeEngineLayerRoutes.atmosphericSkyCreate(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), sky));
        return new AtmosphericSky(sky[0]);
    }

    /**
     * Evaluates the sky model for one view direction, on the CPU.
     *
     * <p>A degenerate direction falls back to straight up rather than refusing, and the turbidity
     * is used exactly as given -- unlike {@link #setTurbidity}, this does not clamp it into the
     * model's range.
     *
     * @param viewDirection the direction being looked along
     * @param sunDirection the direction the sun is in
     * @param turbidity how much is in the air; not clamped
     * @return the radiance per channel
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static Vector3 radiance(Vector3 viewDirection, Vector3 sunDirection, float turbidity) {
        GraphicsExtension.requireBackend();
        float[] radiance = new float[3];
        GraphicsExtension.check("AtmosphericSky.radiance",
                NativeEngineLayerRoutes.atmosphericSkyRadiance(
                        EngineValues.floats(viewDirection, "viewDirection"),
                        EngineValues.floats(sunDirection, "sunDirection"), turbidity, radiance));
        return new Vector3(radiance[0], radiance[1], radiance[2]);
    }

    /**
     * Returns the GLSL the sky's shader evaluates.
     *
     * @return the GLSL
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static String getModelGlsl() {
        GraphicsExtension.requireBackend();
        long[] bytes = new long[1];
        int probe = NativeEngineLayerRoutes.atmosphericSkyCopyModelGlsl(new byte[0], bytes);
        if (probe != RESULT_BUFFER_TOO_SMALL) {
            GraphicsExtension.check("AtmosphericSky.getModelGlsl", probe);
        }
        int length = Math.toIntExact(bytes[0]);
        if (length == 0) {
            return "";
        }
        byte[] destination = new byte[length];
        GraphicsExtension.check("AtmosphericSky.getModelGlsl",
                NativeEngineLayerRoutes.atmosphericSkyCopyModelGlsl(destination, bytes));
        return new String(destination, 0, Math.toIntExact(bytes[0]), StandardCharsets.UTF_8);
    }

    /**
     * Reports whether this renderer can draw the sky.
     *
     * @return whether the shader exists and links
     */
    public boolean isSupported() {
        boolean[] supported = new boolean[1];
        GraphicsExtension.check("AtmosphericSky.isSupported",
                NativeEngineLayerRoutes.atmosphericSkyIsSupported(open(), supported));
        return supported[0];
    }

    /**
     * Draws the sky over the whole viewport.
     *
     * @param view the camera's view matrix
     * @param projection the camera's projection matrix
     * @param width the viewport width in pixels
     * @param height the viewport height in pixels
     */
    public void draw(Matrix view, Matrix projection, int width, int height) {
        GraphicsExtension.check("AtmosphericSky.draw",
                NativeEngineLayerRoutes.atmosphericSkyDraw(open(),
                        EngineValues.floats(view, "view"),
                        EngineValues.floats(projection, "projection"), width, height));
    }

    /** @return the direction the sun is in */
    public Vector3 getSunDirection() {
        float[] direction = new float[3];
        GraphicsExtension.check("AtmosphericSky.getSunDirection",
                NativeEngineLayerRoutes.atmosphericSkyGetSunDirection(open(), direction));
        return new Vector3(direction[0], direction[1], direction[2]);
    }

    /**
     * Moves the sun.
     *
     * @param direction the direction the sun is in
     */
    public void setSunDirection(Vector3 direction) {
        GraphicsExtension.check("AtmosphericSky.setSunDirection",
                NativeEngineLayerRoutes.atmosphericSkySetSunDirection(open(),
                        EngineValues.floats(direction, "direction")));
    }

    /** @return how much is in the air */
    public float getTurbidity() {
        float[] turbidity = new float[1];
        GraphicsExtension.check("AtmosphericSky.getTurbidity",
                NativeEngineLayerRoutes.atmosphericSkyGetTurbidity(open(), turbidity));
        return turbidity[0];
    }

    /**
     * Sets how much is in the air.
     *
     * <p>Clamped into the model's range, unlike {@link #radiance}.
     *
     * @param turbidity the turbidity
     */
    public void setTurbidity(float turbidity) {
        GraphicsExtension.check("AtmosphericSky.setTurbidity",
                NativeEngineLayerRoutes.atmosphericSkySetTurbidity(open(), turbidity));
    }

    /** @return the multiplier on the model's radiance */
    public float getIntensity() {
        float[] intensity = new float[1];
        GraphicsExtension.check("AtmosphericSky.getIntensity",
                NativeEngineLayerRoutes.atmosphericSkyGetIntensity(open(), intensity));
        return intensity[0];
    }

    /**
     * Sets the multiplier on the model's radiance.
     *
     * @param intensity the multiplier
     */
    public void setIntensity(float intensity) {
        GraphicsExtension.check("AtmosphericSky.setIntensity",
                NativeEngineLayerRoutes.atmosphericSkySetIntensity(open(), intensity));
    }

    /** Releases the sky. Closing twice is a no-op. */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        GraphicsExtension.check("AtmosphericSky.close",
                NativeEngineLayerRoutes.atmosphericSkyDestroy(handle));
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This AtmosphericSky is closed");
            }
        }
        return handle;
    }
}
