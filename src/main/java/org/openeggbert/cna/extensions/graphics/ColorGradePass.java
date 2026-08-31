package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import Microsoft.Xna.Framework.Graphics.Texture3D;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.Objects;

/**
 * Applies a colourist's grade to the finished frame.
 *
 * <p>A CNA extension, and the consumer of {@link CubeLut}: a {@code .cube} table exported from a
 * grading tool, sampled per pixel. {@link #setStrength} blends between the graded frame and the
 * original, which is how a game fades a look in rather than switching it on.
 *
 * <p><strong>Two shapes of table, and the pass refuses a malformed one.</strong>
 * {@link #setLut} takes a strip -- N slices of N by N, so its width must be the square of its
 * height -- and {@link #setVolumeLut} takes a cubical {@link Texture3D}: equal width, height and
 * depth, with an edge between two and {@value #MAX_VOLUME_LUT_EDGE}. CNA checks the shape rather
 * than sampling it, because a strip read at the wrong slice count would grade the frame into
 * colours nothing in the table names.
 *
 * <p>The volume slot took a {@code TextureCube} until it was measured. CNA's declaration calls it
 * "a cube", which reads both ways, and a cube <em>map</em> is a different object from a cubical
 * volume: CNA refuses one with {@code INVALID_HANDLE} and accepts the other. Nothing caught it
 * because nothing had ever bound a volume table -- the signature was wrong and untested at the
 * same time, which is how a wrong signature survives.
 *
 * <p>Both slots are borrowed and retained here, so nothing collects a table while the pass names
 * it; closing the pass disposes neither.
 *
 * <p>Created on a device, configured here, and run by a {@link RenderPipeline}. Creation succeeds
 * on a renderer that cannot run the pass; ask
 * {@link PostProcessPass#isSupported(GraphicsDevice)}.
 */
public final class ColorGradePass extends PostProcessPass {

    /** CNA's own ceiling on a volume table's edge, from CNA_COLOR_GRADE_MAX_LUT_SIZE_EXT. */
    public static final int MAX_VOLUME_LUT_EDGE = 64;

    /** CNA's own floor: a one-texel table has no two entries to interpolate between. */
    public static final int MIN_VOLUME_LUT_EDGE = 2;

    private Texture2D strip;
    private Texture3D volume;

    private ColorGradePass(long handle) {
        super(handle);
    }

    /**
     * Creates the pass on a device.
     *
     * @param graphicsDevice the device to compile on
     * @return the pass, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static ColorGradePass create(GraphicsDevice graphicsDevice) {
        return new ColorGradePass(createOn(graphicsDevice, "ColorGradePass.create",
                NativeEngineLayerRoutes::colorGradePassCreate));
    }

    /**
     * Creates an identity strip table, which grades nothing.
     *
     * <p>The starting point for a game that builds a grade at runtime, and the thing to compare
     * a loaded one against.
     *
     * @param graphicsDevice the device to allocate on
     * @param size the slice count; at least two
     * @return a new texture, which the caller disposes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static Texture2D createIdentityLut(GraphicsDevice graphicsDevice, int size) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] lut = new long[1];
        GraphicsExtension.check("ColorGradePass.createIdentityLut",
                NativeEngineLayerRoutes.colorGradePassCreateIdentityLut(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), size, lut));
        return NativeBindings.adoptTexture2D(graphicsDevice, lut[0]);
    }

    /**
     * Returns the slice count a strip of a given pixel size describes.
     *
     * <p>Zero when the size describes no valid strip, which is how a game validates a texture an
     * artist supplied before handing it over.
     *
     * @param width the strip width in pixels
     * @param height the strip height in pixels
     * @return the slice count, or zero
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static int lutSizeForStrip(int width, int height) {
        GraphicsExtension.requireBackend();
        int[] size = new int[1];
        GraphicsExtension.check("ColorGradePass.lutSizeForStrip",
                NativeEngineLayerRoutes.colorGradePassLutSizeForStrip(width, height, size));
        return size[0];
    }

    /** @return how far the grade is applied, from nothing to fully */
    public float getStrength() {
        return readFloat("ColorGradePass.getStrength",
                NativeEngineLayerRoutes::colorGradePassGetStrength);
    }

    /**
     * Sets how far the grade is applied.
     *
     * @param value the blend between the original frame and the graded one
     */
    public void setStrength(float value) {
        GraphicsExtension.check("ColorGradePass.setStrength",
                NativeEngineLayerRoutes.colorGradePassSetStrength(handle(), value));
    }

    /** @return how the table is interpolated between its entries */
    public LutInterpolation getInterpolation() {
        return LutInterpolation.fromValue(readInt("ColorGradePass.getInterpolation",
                NativeEngineLayerRoutes::colorGradePassGetInterpolation));
    }

    /**
     * Sets how the table is interpolated between its entries.
     *
     * @param value the interpolation
     */
    public void setInterpolation(LutInterpolation value) {
        Objects.requireNonNull(value, "value");
        GraphicsExtension.check("ColorGradePass.setInterpolation",
                NativeEngineLayerRoutes.colorGradePassSetInterpolation(handle(),
                        value.ordinal()));
    }

    /**
     * Binds a strip table, or unbinds the current one.
     *
     * @param lut the strip texture, or {@code null} to unbind
     * @throws IllegalArgumentException when the texture is not a valid strip
     */
    public void setLut(Texture2D lut) {
        GraphicsExtension.check("ColorGradePass.setLut",
                NativeEngineLayerRoutes.colorGradePassSetLut(handle(),
                        lut == null ? 0L : NativeBindings.nativeResourceHandle(lut)));
        synchronized (this) {
            strip = lut;
        }
    }

    /**
     * Returns the strip table the pass was given.
     *
     * @return the texture, or {@code null} when none is bound
     */
    public synchronized Texture2D getLut() {
        handle();
        return strip;
    }

    /**
     * Binds a volume table, or unbinds the current one.
     *
     * <p>The table must be cubical -- equal width, height and depth -- with an edge between
     * {@value #MIN_VOLUME_LUT_EDGE} and {@value #MAX_VOLUME_LUT_EDGE}. Those bounds are checked
     * here as well as by CNA, so a wrong table is named rather than returned as a code.
     *
     * @param lut the volume texture, or {@code null} to unbind
     * @throws IllegalArgumentException when the texture is not a cube of a size CNA grades with
     */
    public void setVolumeLut(Texture3D lut) {
        if (lut != null) {
            int edge = lut.getWidth();
            if (lut.getHeight() != edge || lut.getDepth() != edge) {
                throw new IllegalArgumentException("a volume table must be cubical, not "
                        + lut.getWidth() + "x" + lut.getHeight() + "x" + lut.getDepth());
            }
            if (edge < MIN_VOLUME_LUT_EDGE || edge > MAX_VOLUME_LUT_EDGE) {
                throw new IllegalArgumentException("a volume table's edge must be between "
                        + MIN_VOLUME_LUT_EDGE + " and " + MAX_VOLUME_LUT_EDGE + ", not " + edge);
            }
        }
        GraphicsExtension.check("ColorGradePass.setVolumeLut",
                NativeEngineLayerRoutes.colorGradePassSetVolumeLut(handle(),
                        lut == null ? 0L : NativeBindings.nativeResourceHandle(lut)));
        synchronized (this) {
            volume = lut;
        }
    }

    /**
     * Returns the volume table the pass was given.
     *
     * @return the texture, or {@code null} when none is bound
     */
    public synchronized Texture3D getVolumeLut() {
        handle();
        return volume;
    }
}
