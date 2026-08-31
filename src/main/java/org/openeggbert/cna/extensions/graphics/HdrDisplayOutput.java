package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.Objects;

/**
 * Presents a frame to a display brighter than sRGB.
 *
 * <p>A CNA extension. XNA presents eight-bit sRGB and nothing else, so an HDR pipeline had
 * nowhere to put the range it computed. This encodes a scene-linear frame for the space a display
 * actually takes, with the two numbers that decide how it looks: {@link #getPaperWhiteNits()},
 * the luminance of a white page -- which is what the whole user interface is measured against --
 * and {@link #getPeakNits()}, the brightest the display can go.
 *
 * <p><strong>The transfer functions are pure and exposed.</strong> {@link #encodePq},
 * {@link #decodePq}, {@link #rec709ToRec2020}, {@link #rollOff} and {@link #encode} run without a
 * device, so a game can build a calibration screen, check its own shader against CNA's, or work
 * out what a value will look like before presenting it.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class HdrDisplayOutput implements AutoCloseable {

    private final long handle;
    private boolean closed;

    private HdrDisplayOutput(long handle) {
        this.handle = handle;
    }

    /**
     * Creates an output on a device.
     *
     * @param graphicsDevice the device to present on
     * @return the output, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static HdrDisplayOutput create(GraphicsDevice graphicsDevice) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] output = new long[1];
        GraphicsExtension.check("HdrDisplayOutput.create",
                NativeEngineLayerRoutes.hdrDisplayOutputCreate(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), output));
        return new HdrDisplayOutput(output[0]);
    }

    /**
     * Encodes a luminance with the PQ transfer function.
     *
     * <p>PQ is absolute: a code value means a number of nits rather than a fraction of whatever
     * the display can do, which is what makes an HDR10 frame look the same on two televisions.
     *
     * @param nits the luminance
     * @return the encoded value
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static float encodePq(float nits) {
        GraphicsExtension.requireBackend();
        float[] encoded = new float[1];
        GraphicsExtension.check("HdrDisplayOutput.encodePq",
                NativeEngineLayerRoutes.hdrDisplayOutputEncodePq(nits, encoded));
        return encoded[0];
    }

    /**
     * Decodes a PQ value back to a luminance.
     *
     * @param encoded the encoded value
     * @return the luminance in nits
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static float decodePq(float encoded) {
        GraphicsExtension.requireBackend();
        float[] nits = new float[1];
        GraphicsExtension.check("HdrDisplayOutput.decodePq",
                NativeEngineLayerRoutes.hdrDisplayOutputDecodePq(encoded, nits));
        return nits[0];
    }

    /**
     * Converts a colour from Rec.709 primaries to Rec.2020.
     *
     * @param color the colour in Rec.709
     * @return the same colour in Rec.2020
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static Vector3 rec709ToRec2020(Vector3 color) {
        GraphicsExtension.requireBackend();
        float[] converted = new float[3];
        GraphicsExtension.check("HdrDisplayOutput.rec709ToRec2020",
                NativeEngineLayerRoutes.hdrDisplayOutputRec709ToRec2020(
                        EngineValues.floats(color, "color"), converted));
        return new Vector3(converted[0], converted[1], converted[2]);
    }

    /**
     * Rolls a luminance off towards a peak, so highlights compress rather than clip.
     *
     * @param nits the luminance
     * @param peakNits the brightest the display can show
     * @return the rolled-off luminance
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static float rollOff(float nits, float peakNits) {
        GraphicsExtension.requireBackend();
        float[] rolled = new float[1];
        GraphicsExtension.check("HdrDisplayOutput.rollOff",
                NativeEngineLayerRoutes.hdrDisplayOutputRollOff(nits, peakNits, rolled));
        return rolled[0];
    }

    /**
     * Encodes one scene-linear colour for a colour space.
     *
     * <p>The whole chain in one call: primaries, paper white, roll-off and transfer function.
     *
     * @param space the colour space to encode for
     * @param sceneLinear the scene-linear colour
     * @param paperWhiteNits the luminance of diffuse white
     * @param peakNits the brightest the display can show
     * @return the encoded colour
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static Vector3 encode(DisplayColorSpace space, Vector3 sceneLinear,
            float paperWhiteNits, float peakNits) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(space, "space");
        float[] encoded = new float[3];
        GraphicsExtension.check("HdrDisplayOutput.encode",
                NativeEngineLayerRoutes.hdrDisplayOutputEncode(space.ordinal(),
                        EngineValues.floats(sceneLinear, "sceneLinear"), paperWhiteNits,
                        peakNits, encoded));
        return new Vector3(encoded[0], encoded[1], encoded[2]);
    }

    /**
     * Reports whether this renderer can present an HDR frame.
     *
     * @return whether the encode shader exists and links
     */
    public boolean isSupported() {
        boolean[] supported = new boolean[1];
        GraphicsExtension.check("HdrDisplayOutput.isSupported",
                NativeEngineLayerRoutes.hdrDisplayOutputIsSupported(open(), supported));
        return supported[0];
    }

    /** @return the colour space frames are encoded for */
    public DisplayColorSpace getColorSpace() {
        int[] space = new int[1];
        GraphicsExtension.check("HdrDisplayOutput.getColorSpace",
                NativeEngineLayerRoutes.hdrDisplayOutputGetColorSpace(open(), space));
        return DisplayColorSpace.fromValue(space[0]);
    }

    /**
     * Sets the colour space frames are encoded for.
     *
     * @param space the space the display takes
     */
    public void setColorSpace(DisplayColorSpace space) {
        Objects.requireNonNull(space, "space");
        GraphicsExtension.check("HdrDisplayOutput.setColorSpace",
                NativeEngineLayerRoutes.hdrDisplayOutputSetColorSpace(open(), space.ordinal()));
    }

    /** @return the luminance diffuse white is presented at */
    public float getPaperWhiteNits() {
        float[] nits = new float[1];
        GraphicsExtension.check("HdrDisplayOutput.getPaperWhiteNits",
                NativeEngineLayerRoutes.hdrDisplayOutputGetPaperWhiteNits(open(), nits));
        return nits[0];
    }

    /**
     * Sets the luminance diffuse white is presented at.
     *
     * <p>The number a user interface is measured against: too high and menus glare, too low and
     * the whole frame looks dim.
     *
     * @param nits the luminance
     */
    public void setPaperWhiteNits(float nits) {
        GraphicsExtension.check("HdrDisplayOutput.setPaperWhiteNits",
                NativeEngineLayerRoutes.hdrDisplayOutputSetPaperWhiteNits(open(), nits));
    }

    /** @return the brightest luminance the display can show */
    public float getPeakNits() {
        float[] nits = new float[1];
        GraphicsExtension.check("HdrDisplayOutput.getPeakNits",
                NativeEngineLayerRoutes.hdrDisplayOutputGetPeakNits(open(), nits));
        return nits[0];
    }

    /**
     * Sets the brightest luminance the display can show.
     *
     * @param nits the peak luminance
     */
    public void setPeakNits(float nits) {
        GraphicsExtension.check("HdrDisplayOutput.setPeakNits",
                NativeEngineLayerRoutes.hdrDisplayOutputSetPeakNits(open(), nits));
    }

    /**
     * Encodes a scene-linear frame into a destination.
     *
     * @param source the scene-linear frame; borrowed for the call
     * @param destination the target to encode into, or {@code null} for the back buffer
     * @param width the destination width in pixels
     * @param height the destination height in pixels
     */
    public void draw(Texture2D source, Texture2D destination, int width, int height) {
        Objects.requireNonNull(source, "source");
        GraphicsExtension.check("HdrDisplayOutput.draw",
                NativeEngineLayerRoutes.hdrDisplayOutputDraw(open(),
                        NativeBindings.nativeResourceHandle(source),
                        destination == null ? 0L
                                : NativeBindings.nativeResourceHandle(destination),
                        width, height));
    }

    /** Releases the output. Closing twice is a no-op. */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        GraphicsExtension.check("HdrDisplayOutput.close",
                NativeEngineLayerRoutes.hdrDisplayOutputDestroy(handle));
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This HdrDisplayOutput is closed");
            }
        }
        return handle;
    }
}
