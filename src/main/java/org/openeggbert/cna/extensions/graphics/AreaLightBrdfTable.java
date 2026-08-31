package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * The precomputed table an {@link AreaLight} is shaded through.
 *
 * <p>A CNA extension. Integrating a rough surface's response over a light's whole surface is too
 * expensive to do per pixel, so it is precomputed once into a texture the shader samples: for a
 * roughness and a viewing angle, how much energy the lobe carries and which way it points. That
 * costs a moment to build, which {@link #getGenerationMilliseconds()} reports.
 *
 * <p>{@link #evaluate} computes one entry directly, with no table and no device, so a game can
 * check the table's own resolution against the exact answer.
 *
 * <p><strong>The table's texture is not exposed.</strong> CNA lends it as a borrow and does not
 * say how the borrow is given back; see {@link WeightedBlendedTransparency} for the same
 * reasoning. {@link ClusteredForwardEffect#setAreaLight} takes the table itself, which is what a
 * game actually needs.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class AreaLightBrdfTable implements AutoCloseable {

    /** CNA's own result for a buffer that could not hold the answer. */
    private static final int RESULT_BUFFER_TOO_SMALL = 14;

    private final long handle;
    private boolean closed;

    // Kept because the table's texture is adopted onto it: a facade over a native texture needs
    // the device it belongs to, and the table's own creator is the only thing that knows it.
    private final GraphicsDevice graphicsDevice;

    private AreaLightBrdfTable(long handle, GraphicsDevice device) {
        this.graphicsDevice = device;
        this.handle = handle;
    }

    /**
     * Builds a table at CNA's own resolution.
     *
     * @param graphicsDevice the device to upload to
     * @return the table, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static AreaLightBrdfTable create(GraphicsDevice graphicsDevice) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] table = new long[1];
        GraphicsExtension.check("AreaLightBrdfTable.create",
                NativeEngineLayerRoutes.areaLightBrdfTableCreate(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), table));
        return new AreaLightBrdfTable(table[0], graphicsDevice);
    }

    /**
     * Builds a table at a chosen resolution and sample count.
     *
     * <p>Both cost build time: the size squares and the sample count multiplies, so a game that
     * builds this at load time can trade a moment for accuracy and see what it bought through
     * {@link #getGenerationMilliseconds()}.
     *
     * @param graphicsDevice the device to upload to
     * @param size the table's edge length in texels
     * @param sampleCount how many samples each entry integrates over
     * @return the table, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static AreaLightBrdfTable create(GraphicsDevice graphicsDevice, int size,
            int sampleCount) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] table = new long[1];
        GraphicsExtension.check("AreaLightBrdfTable.create",
                NativeEngineLayerRoutes.areaLightBrdfTableCreateWithSize(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), size,
                        sampleCount, table));
        return new AreaLightBrdfTable(table[0], graphicsDevice);
    }

    /**
     * Computes one table entry directly, without a table.
     *
     * @param roughness the surface roughness
     * @param cosTheta the cosine of the viewing angle
     * @param sampleCount how many samples to integrate over
     * @return the four terms
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static AreaLightBrdfTerms evaluate(float roughness, float cosTheta, int sampleCount) {
        GraphicsExtension.requireBackend();
        float[] terms = new float[4];
        GraphicsExtension.check("AreaLightBrdfTable.evaluate",
                NativeEngineLayerRoutes.areaLightBrdfTableEvaluate(roughness, cosTheta,
                        sampleCount, terms));
        return new AreaLightBrdfTerms(terms[0], terms[1], terms[2], terms[3]);
    }

    /**
     * Returns the GLSL a shader looks the table up with.
     *
     * @return the GLSL
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static String getLookupGlsl() {
        GraphicsExtension.requireBackend();
        long[] bytes = new long[1];
        int probe = NativeEngineLayerRoutes.areaLightBrdfTableCopyLookupGlsl(new byte[0], bytes);
        if (probe != RESULT_BUFFER_TOO_SMALL) {
            GraphicsExtension.check("AreaLightBrdfTable.getLookupGlsl", probe);
        }
        int length = Math.toIntExact(bytes[0]);
        if (length == 0) {
            return "";
        }
        byte[] destination = new byte[length];
        GraphicsExtension.check("AreaLightBrdfTable.getLookupGlsl",
                NativeEngineLayerRoutes.areaLightBrdfTableCopyLookupGlsl(destination, bytes));
        return new String(destination, 0, Math.toIntExact(bytes[0]), StandardCharsets.UTF_8);
    }

    /**
     * Returns the table a shader samples.
     *
     * <p>The whole reason the table exists: {@link #getLookupGlsl()} is the shader code and this
     * is the texture that code reads. Without it the table computes something no draw can reach.
     *
     * <p><strong>A fresh facade on every call, and each one must be disposed.</strong> CNA hands
     * back a new handle each time -- measured, in {@code tools/native-abi/probes/lent_handles.c}
     * -- and each one keeps the table alive while it exists. Disposing the returned texture
     * releases the handle and not the table's texture, so a game that asks twice has two objects
     * to dispose and one texture underneath.
     *
     * @return the texture, or {@code null} when the renderer could not store one
     */
    public Texture2D getTexture() {
        long[] texture = new long[1];
        GraphicsExtension.check("AreaLightBrdfTable.getTexture",
                NativeEngineLayerRoutes.areaLightBrdfTableGetTexture(open(), texture));
        return texture[0] == 0L ? null
                : NativeBindings.adoptTexture2D(graphicsDevice, texture[0]);
    }

    /** @return the table's edge length in texels */
    public int getSize() {
        int[] size = new int[1];
        GraphicsExtension.check("AreaLightBrdfTable.getSize",
                NativeEngineLayerRoutes.areaLightBrdfTableGetSize(open(), size));
        return size[0];
    }

    /** @return how many samples each entry was integrated over */
    public int getSampleCount() {
        int[] count = new int[1];
        GraphicsExtension.check("AreaLightBrdfTable.getSampleCount",
                NativeEngineLayerRoutes.areaLightBrdfTableGetSampleCount(open(), count));
        return count[0];
    }

    /**
     * Returns how long the table took to build.
     *
     * @return the build time in milliseconds
     */
    public double getGenerationMilliseconds() {
        double[] milliseconds = new double[1];
        GraphicsExtension.check("AreaLightBrdfTable.getGenerationMilliseconds",
                NativeEngineLayerRoutes.areaLightBrdfTableGetGenerationMilliseconds(open(),
                        milliseconds));
        return milliseconds[0];
    }

    /** Releases the table and its texture. Closing twice is a no-op. */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        GraphicsExtension.check("AreaLightBrdfTable.close",
                NativeEngineLayerRoutes.areaLightBrdfTableDestroy(handle));
    }

    /** The native handle, for the effect that shades an area light through this table. */
    long handle() {
        return open();
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This AreaLightBrdfTable is closed");
            }
        }
        return handle;
    }
}
