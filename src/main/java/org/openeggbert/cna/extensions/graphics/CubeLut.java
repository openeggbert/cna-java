package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import Microsoft.Xna.Framework.Graphics.TextureCube;
import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Objects;

/**
 * A colourist's {@code .cube} lookup table, read.
 *
 * <p>A CNA extension. A {@code .cube} file is what a colour grade is delivered as: a cube of
 * output colours indexed by input colour, exported by every grading tool there is. XNA had no
 * notion of one, so a game that wanted a look either baked it into its shader or did without.
 *
 * <p>{@link #parse} and {@link #load} read the file; {@link #createStripTexture} and
 * {@link #createVolumeTexture} turn it into something a shader can sample, and both hand the
 * caller a texture it owns.
 *
 * <p><strong>The domain matters.</strong> Most tables map the unit cube, and
 * {@link #isUnitDomain()} says whether this one does. A table with a wider domain expects input
 * outside zero-to-one -- an HDR grade -- and applying it as if it were a unit table would grade
 * the wrong range.
 *
 * <p>Parsing needs no graphics device: a build step can read a grade and produce its texture
 * without a window.
 *
 * <p><strong>A malformed file is reported as an unsupported capability.</strong> CNA's header
 * documents {@code CNA_RESULT_INVALID_ARGUMENT} for text the parser refuses, and the parser's own
 * exception escapes the route's catch into the exception barrier's capability arm instead -- so a
 * typo in an artist's file raises {@link ExtensionNotSupportedException} rather than
 * {@link IllegalArgumentException}. Recorded as {@code JAVA-UPSTREAM-009} and reproduced in C by
 * {@code tools/native-abi/probes/cube_lut_refusal.c}. A game that catches the capability refusal
 * to fall back will fall back for a file it could have rejected, so catch both until it is
 * fixed.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class CubeLut implements AutoCloseable {

    /** CNA's own result for a buffer that could not hold the answer. */
    private static final int RESULT_BUFFER_TOO_SMALL = 14;

    private final long handle;
    private boolean closed;

    private CubeLut(long handle) {
        this.handle = handle;
    }

    /**
     * Parses a {@code .cube} table from text.
     *
     * @param text the file's contents
     * @return the table, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer, and -- see the
     *         class documentation -- when the parser refuses the text
     */
    public static CubeLut parse(String text) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(text, "text");
        long[] lut = new long[1];
        GraphicsExtension.check("CubeLut.parse", NativeEngineLayerRoutes.cubeLutParse(
                text.getBytes(StandardCharsets.UTF_8), lut));
        return new CubeLut(lut[0]);
    }

    /**
     * Loads and parses a {@code .cube} table from a file.
     *
     * @param path the file to read
     * @return the table, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer, and -- see the
     *         class documentation -- when the parser refuses the file's contents
     */
    public static CubeLut load(Path path) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(path, "path");
        long[] lut = new long[1];
        GraphicsExtension.check("CubeLut.load", NativeEngineLayerRoutes.cubeLutLoadFromFile(
                path.toString().getBytes(StandardCharsets.UTF_8), lut));
        return new CubeLut(lut[0]);
    }

    /** @return the table's edge length, so it holds this cubed entries */
    public int getSize() {
        int[] size = new int[1];
        GraphicsExtension.check("CubeLut.getSize",
                NativeEngineLayerRoutes.cubeLutGetSize(open(), size));
        return size[0];
    }

    /**
     * Returns one entry of the table.
     *
     * <p>An index outside the table is refused rather than clamped, because a clamped index would
     * silently read a different colour.
     *
     * @param red the red index, from zero
     * @param green the green index, from zero
     * @param blue the blue index, from zero
     * @return the graded colour
     */
    public Vector3 getEntry(int red, int green, int blue) {
        float[] color = new float[3];
        GraphicsExtension.check("CubeLut.getEntry",
                NativeEngineLayerRoutes.cubeLutGetEntry(open(), red, green, blue, color));
        return new Vector3(color[0], color[1], color[2]);
    }

    /** @return the lowest input colour the table maps */
    public Vector3 getDomainMinimum() {
        return domain("CubeLut.getDomainMinimum",
                NativeEngineLayerRoutes::cubeLutGetDomainMin);
    }

    /** @return the highest input colour the table maps */
    public Vector3 getDomainMaximum() {
        return domain("CubeLut.getDomainMaximum",
                NativeEngineLayerRoutes::cubeLutGetDomainMax);
    }

    /**
     * Reports whether the table maps the unit cube.
     *
     * @return whether its domain is zero-to-one in every channel
     */
    public boolean isUnitDomain() {
        boolean[] unit = new boolean[1];
        GraphicsExtension.check("CubeLut.isUnitDomain",
                NativeEngineLayerRoutes.cubeLutIsUnitDomain(open(), unit));
        return unit[0];
    }

    /**
     * Returns the title the file declared.
     *
     * @return the title, or an empty string when the file names none
     */
    public String getTitle() {
        long lut = open();
        long[] bytes = new long[1];
        int probe = NativeEngineLayerRoutes.cubeLutCopyTitle(lut, new byte[0], bytes);
        if (probe != RESULT_BUFFER_TOO_SMALL) {
            GraphicsExtension.check("CubeLut.getTitle", probe);
        }
        int length = Math.toIntExact(bytes[0]);
        if (length == 0) {
            return "";
        }
        byte[] destination = new byte[length];
        GraphicsExtension.check("CubeLut.getTitle",
                NativeEngineLayerRoutes.cubeLutCopyTitle(lut, destination, bytes));
        return new String(destination, 0, Math.toIntExact(bytes[0]), StandardCharsets.UTF_8);
    }

    /**
     * Builds the strip texture a two-dimensional grading shader samples.
     *
     * <p>A strip is the cube's slices laid side by side: N slices of N by N, so the texture is
     * N-squared wide and N tall.
     *
     * @param graphicsDevice the device the result belongs to
     * @return a new texture, which the caller disposes
     */
    public Texture2D createStripTexture(GraphicsDevice graphicsDevice) {
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] texture = new long[1];
        GraphicsExtension.check("CubeLut.createStripTexture",
                NativeEngineLayerRoutes.cubeLutCreateStripTexture(open(),
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), texture));
        return NativeBindings.adoptTexture2D(graphicsDevice, texture[0]);
    }

    /**
     * Builds the volume texture a three-dimensional grading shader samples.
     *
     * <p>Sharper than a strip and cheaper to sample, where the renderer has volume textures.
     *
     * @param graphicsDevice the device the result belongs to
     * @return a new texture, which the caller disposes
     */
    public TextureCube createVolumeTexture(GraphicsDevice graphicsDevice) {
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] texture = new long[1];
        GraphicsExtension.check("CubeLut.createVolumeTexture",
                NativeEngineLayerRoutes.cubeLutCreateVolumeTexture(open(),
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), texture));
        return NativeBindings.adoptTextureCube(graphicsDevice, texture[0]);
    }

    /** Releases the table. What it built is untouched. Closing twice is a no-op. */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        GraphicsExtension.check("CubeLut.close",
                NativeEngineLayerRoutes.cubeLutDestroy(handle));
    }

    /** A colour CNA answers about one table. */
    @FunctionalInterface
    private interface ColorRoute {
        int call(long lut, float[] answer);
    }

    private Vector3 domain(String operation, ColorRoute route) {
        float[] answer = new float[3];
        GraphicsExtension.check(operation, route.call(open(), answer));
        return new Vector3(answer[0], answer[1], answer[2]);
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This CubeLut is closed");
            }
        }
        return handle;
    }
}
