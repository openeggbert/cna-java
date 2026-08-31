package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.PrimitiveType;
import Microsoft.Xna.Framework.Matrix;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * Culls instances on the GPU and draws what survives, without the CPU ever seeing the answer.
 *
 * <p>A CNA extension, and the thing {@link FrustumCuller} cannot be: a compute shader tests every
 * instance, writes an indirect draw command, and the draw reads that command from GPU memory. The
 * CPU issues one call and never learns how many instances it drew -- which is the point, because
 * reading the count back would stall the pipeline exactly where the technique exists to avoid
 * stalling it. {@link #readVisibleCount()} does read it, deliberately, for a test or a tool.
 *
 * <p><strong>This renderer cannot do it</strong>, and the object says so rather than being
 * absent: {@link #isSupported()} answers {@code false} and {@link #getUnsupportedReason()}
 * explains -- "this renderer has no compute shaders" -- so a game can fall back to
 * {@link FrustumCuller} and log why.
 *
 * <p><strong>{@link #draw} before {@link #cull} is refused.</strong> CNA's own note says the
 * canonical body throws there and that reporting it as an internal failure would send a caller
 * looking in the wrong place entirely; the order is the contract.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class GpuInstanceCuller implements AutoCloseable {

    /** CNA's own result for a buffer that could not hold the answer. */
    private static final int RESULT_BUFFER_TOO_SMALL = 14;

    /** {@code CNA_GPU_INSTANCE_BINDING}: where a culled-instance shader reads its buffer. */
    public static final int StorageBufferBinding = 6;

    private final long handle;
    private boolean closed;

    private GpuInstanceCuller(long handle) {
        this.handle = handle;
    }

    /**
     * Creates a culler on a device.
     *
     * <p>Succeeds on a renderer with no compute shaders; ask {@link #isSupported()}.
     *
     * @param graphicsDevice the device to cull on
     * @return the culler, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static GpuInstanceCuller create(GraphicsDevice graphicsDevice) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] culler = new long[1];
        GraphicsExtension.check("GpuInstanceCuller.create",
                NativeEngineLayerRoutes.gpuInstanceCullerCreate(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), culler));
        return new GpuInstanceCuller(culler[0]);
    }

    /**
     * Returns the GLSL a shader reads a surviving instance's transform with.
     *
     * @return the GLSL
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static String getInstanceLookupGlsl() {
        GraphicsExtension.requireBackend();
        return text("GpuInstanceCuller.getInstanceLookupGlsl",
                NativeEngineLayerRoutes::gpuInstanceCullerCopyInstanceLookupGlsl);
    }

    /**
     * Reports whether this renderer can cull on the GPU.
     *
     * @return whether the compute shader exists and links
     */
    public boolean isSupported() {
        boolean[] supported = new boolean[1];
        GraphicsExtension.check("GpuInstanceCuller.isSupported",
                NativeEngineLayerRoutes.gpuInstanceCullerIsSupported(open(), supported));
        return supported[0];
    }

    /**
     * Returns why GPU culling is unavailable, in the renderer's own words.
     *
     * @return the reason, or an empty string when it is available
     */
    public String getUnsupportedReason() {
        long culler = open();
        return text("GpuInstanceCuller.getUnsupportedReason",
                (destination, bytes) -> NativeEngineLayerRoutes
                        .gpuInstanceCullerCopyUnsupportedReason(culler, destination, bytes));
    }

    /**
     * Uploads the instances to test.
     *
     * @param instances the instances, each a transform and its bounds
     */
    public void setInstances(List<GpuCullableInstance> instances) {
        Objects.requireNonNull(instances, "instances");
        int leaves = EngineValues.MATRIX_LEAVES + EngineValues.BOX_LEAVES;
        float[] packed = new float[Math.multiplyExact(instances.size(), leaves)];
        for (int index = 0; index < instances.size(); index++) {
            GpuCullableInstance instance = Objects.requireNonNull(instances.get(index),
                    "instances[" + index + "]");
            System.arraycopy(instance.floating(), 0, packed, index * leaves, leaves);
        }
        GraphicsExtension.check("GpuInstanceCuller.setInstances",
                NativeEngineLayerRoutes.gpuInstanceCullerSetInstances(open(), packed));
    }

    /**
     * Returns how many instances are uploaded.
     *
     * @return the count, which is what was uploaded rather than what survived
     */
    public int getInstanceCount() {
        int[] count = new int[1];
        GraphicsExtension.check("GpuInstanceCuller.getInstanceCount",
                NativeEngineLayerRoutes.gpuInstanceCullerGetInstanceCount(open(), count));
        return count[0];
    }

    /**
     * Culls the uploaded instances and builds the indirect draw command.
     *
     * <p>The command is written from the CPU with an instance count of zero and the shader adds
     * to it, which is why the count means nothing until this has run.
     *
     * @param view the camera's view matrix
     * @param projection the camera's projection matrix
     * @param indexCount how many indices the drawn mesh has; must be positive
     * @param firstIndex the first index
     * @param baseVertex the base vertex
     * @throws ExtensionNotSupportedException when this renderer cannot cull
     */
    public void cull(Matrix view, Matrix projection, int indexCount, int firstIndex,
            int baseVertex) {
        GraphicsExtension.check("GpuInstanceCuller.cull",
                NativeEngineLayerRoutes.gpuInstanceCullerCull(open(),
                        EngineValues.floats(view, "view"),
                        EngineValues.floats(projection, "projection"), indexCount, firstIndex,
                        baseVertex));
    }

    /**
     * Draws the instances the last cull kept.
     *
     * @param primitiveType what the mesh's indices describe
     * @throws IllegalStateException when nothing has been culled yet
     */
    public void draw(PrimitiveType primitiveType) {
        Objects.requireNonNull(primitiveType, "primitiveType");
        GraphicsExtension.check("GpuInstanceCuller.draw",
                NativeEngineLayerRoutes.gpuInstanceCullerDraw(open(), primitiveType.ordinal()));
    }

    /**
     * Reads back how many instances the last cull kept.
     *
     * <p><strong>Stalls the pipeline</strong>, which is the one thing GPU culling exists to
     * avoid, so this is for a test or a tool rather than a frame.
     *
     * @return the surviving count
     */
    public int readVisibleCount() {
        int[] count = new int[1];
        GraphicsExtension.check("GpuInstanceCuller.readVisibleCount",
                NativeEngineLayerRoutes.gpuInstanceCullerReadVisibleCountExt(open(), count));
        return count[0];
    }

    /** Releases the culler and its buffers. Closing twice is a no-op. */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        GraphicsExtension.check("GpuInstanceCuller.close",
                NativeEngineLayerRoutes.gpuInstanceCullerDestroy(handle));
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

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This GpuInstanceCuller is closed");
            }
        }
        return handle;
    }
}
