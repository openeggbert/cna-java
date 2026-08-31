package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.Effect;
import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * What the shader actually reads: the lights, the grid and the assignment, on the GPU.
 *
 * <p>A CNA extension, and the last of the four objects clustered forward lighting is made of.
 * {@link #upload} packs a {@link ClusteredLightSet}, a {@link ClusteredLightGrid} and a
 * {@link ClusteredLightAssignment} into textures, and {@link #bind} points an effect's samplers
 * at them. {@link #getLightLookupGlsl()} is the GLSL that reads them back out, which is what a
 * game's own shader includes so its lookup and CNA's packing cannot disagree.
 *
 * <p><strong>Nothing here retains the three objects it uploads.</strong> {@link #upload} copies
 * what it needs into GPU memory and the buffer does not depend on them afterwards -- so a game
 * may rebuild its set every frame without invalidating a buffer, and closing the set does not
 * close the buffer. The counts below are the buffer's own record of what it last took.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class ClusteredLightBuffer implements AutoCloseable {

    /** CNA's own result for a buffer that could not hold the answer. */
    private static final int RESULT_BUFFER_TOO_SMALL = 14;

    private final long handle;
    private boolean closed;

    private ClusteredLightBuffer(long handle) {
        this.handle = handle;
    }

    /**
     * Creates an empty buffer on a device.
     *
     * @param graphicsDevice the device to upload to
     * @return the buffer, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static ClusteredLightBuffer create(GraphicsDevice graphicsDevice) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] buffer = new long[1];
        GraphicsExtension.check("ClusteredLightBuffer.create",
                NativeEngineLayerRoutes.clusteredLightBufferCreate(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), buffer));
        return new ClusteredLightBuffer(buffer[0]);
    }

    /**
     * Returns the GLSL a shader includes to read a bound buffer.
     *
     * <p>Static, because it describes the packing rather than any one buffer. Exposed for the
     * same reason {@link InstancedRenderer#getInstanceDeclaration()} is: a game whose shader
     * unpacks these textures itself has to unpack them the way CNA packed them, and reading
     * CNA's own source is how it can.
     *
     * @return the GLSL
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static String getLightLookupGlsl() {
        GraphicsExtension.requireBackend();
        long[] bytes = new long[1];
        int probe = NativeEngineLayerRoutes
                .clusteredLightBufferCopyLightLookupGlsl(new byte[0], bytes);
        if (probe != RESULT_BUFFER_TOO_SMALL) {
            GraphicsExtension.check("ClusteredLightBuffer.getLightLookupGlsl", probe);
        }
        int length = Math.toIntExact(bytes[0]);
        if (length == 0) {
            return "";
        }
        byte[] destination = new byte[length];
        GraphicsExtension.check("ClusteredLightBuffer.getLightLookupGlsl", NativeEngineLayerRoutes
                .clusteredLightBufferCopyLightLookupGlsl(destination, bytes));
        return new String(destination, 0, Math.toIntExact(bytes[0]), StandardCharsets.UTF_8);
    }

    /**
     * Packs the lights, the grid and the assignment into GPU memory.
     *
     * @param lights the light set
     * @param grid the grid the assignment was sorted into
     * @param assignment which lights reach which clusters
     */
    public void upload(ClusteredLightSet lights, ClusteredLightGrid grid,
            ClusteredLightAssignment assignment) {
        Objects.requireNonNull(lights, "lights");
        Objects.requireNonNull(grid, "grid");
        Objects.requireNonNull(assignment, "assignment");
        GraphicsExtension.check("ClusteredLightBuffer.upload",
                NativeEngineLayerRoutes.clusteredLightBufferUpload(open(), lights.handle(),
                        grid.handle(), assignment.handle()));
    }

    /**
     * Points an effect's samplers at this buffer.
     *
     * @param effect the effect to bind to
     * @param firstUnit the first texture unit to use
     */
    public void bind(Effect effect, int firstUnit) {
        Objects.requireNonNull(effect, "effect");
        GraphicsExtension.check("ClusteredLightBuffer.bind",
                NativeEngineLayerRoutes.clusteredLightBufferBind(open(),
                        NativeBindings.nativeResourceHandle(effect), firstUnit));
    }

    /**
     * Reports whether anything has been uploaded.
     *
     * @return whether the buffer holds a scene
     */
    public boolean isUploaded() {
        boolean[] uploaded = new boolean[1];
        GraphicsExtension.check("ClusteredLightBuffer.isUploaded",
                NativeEngineLayerRoutes.clusteredLightBufferIsUploaded(open(), uploaded));
        return uploaded[0];
    }

    /** @return how many lights the last upload carried */
    public int getLightCount() {
        return count("ClusteredLightBuffer.getLightCount",
                NativeEngineLayerRoutes::clusteredLightBufferGetLightCount);
    }

    /** @return how many clusters the last upload carried */
    public int getClusterCount() {
        return count("ClusteredLightBuffer.getClusterCount",
                NativeEngineLayerRoutes::clusteredLightBufferGetClusterCount);
    }

    /** @return how many light references the last upload carried */
    public int getReferenceCount() {
        return count("ClusteredLightBuffer.getReferenceCount",
                NativeEngineLayerRoutes::clusteredLightBufferGetReferenceCount);
    }

    /** Releases the buffer. Closing twice is a no-op. */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        GraphicsExtension.check("ClusteredLightBuffer.close",
                NativeEngineLayerRoutes.clusteredLightBufferDestroy(handle));
    }

    /** A count CNA answers about one buffer. */
    @FunctionalInterface
    private interface CountRoute {
        int call(long buffer, int[] answer);
    }

    private int count(String operation, CountRoute route) {
        int[] answer = new int[1];
        GraphicsExtension.check(operation, route.call(open(), answer));
        return answer[0];
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This ClusteredLightBuffer is closed");
            }
        }
        return handle;
    }
}
