package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * One step of CNA's post-process chain.
 *
 * <p>A CNA extension. Every pass is the same object to the pipeline -- it has a name, it may or
 * may not be supported on the renderer, and it runs -- and each subclass adds only the settings
 * that pass has. That is why this is one type with several subclasses rather than one class per
 * pass with everything repeated.
 *
 * <p><strong>Creation succeeds on a renderer that cannot run the pass.</strong> Ask
 * {@link #isSupported(GraphicsDevice)}, which is a question about the renderer and therefore
 * takes the device rather than being a property of the pass.
 *
 * <p><strong>Where a pass runs.</strong> {@link RenderPipeline#addUserPass} appends it after the
 * built-in ones. Running one by hand needs {@code cna_post_process_pass_apply} and its context,
 * which carries a borrowed pointer to another structure the generated boundary refuses rather
 * than guesses at -- so the pipeline is the path, and it is the one a game wants anyway.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public abstract class PostProcessPass implements AutoCloseable {

    /** CNA's own result for a buffer that could not hold the answer. */
    private static final int RESULT_BUFFER_TOO_SMALL = 14;

    private final long handle;
    private boolean closed;

    /**
     * Adopts a native pass handle.
     *
     * @param handle the owned handle CNA just returned
     */
    PostProcessPass(long handle) {
        this.handle = handle;
    }

    /**
     * Returns the pass's own name, as CNA names it.
     *
     * <p>The name a pass timing is reported under, so a game that logs
     * {@link RenderPipeline#getPassTimings()} can match a number to the object that produced it.
     *
     * @return the name
     */
    public final String getName() {
        long pass = open();
        long[] bytes = new long[1];
        // A zero-capacity probe reports the byte count and writes nothing, so BUFFER_TOO_SMALL
        // is the expected answer to the first call rather than a failure.
        int probe = NativeEngineLayerRoutes.postProcessPassCopyName(pass, new byte[0], bytes);
        if (probe != RESULT_BUFFER_TOO_SMALL) {
            GraphicsExtension.check("PostProcessPass.getName", probe);
        }
        int length = Math.toIntExact(bytes[0]);
        if (length == 0) {
            return "";
        }
        byte[] destination = new byte[length];
        GraphicsExtension.check("PostProcessPass.getName",
                NativeEngineLayerRoutes.postProcessPassCopyName(pass, destination, bytes));
        return new String(destination, 0, Math.toIntExact(bytes[0]), StandardCharsets.UTF_8);
    }

    /**
     * Reports whether a renderer can run this pass.
     *
     * @param graphicsDevice the device to ask about
     * @return whether the pass's shader exists and links there
     */
    public final boolean isSupported(GraphicsDevice graphicsDevice) {
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        boolean[] supported = new boolean[1];
        GraphicsExtension.check("PostProcessPass.isSupported",
                NativeEngineLayerRoutes.postProcessPassIsSupported(open(),
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), supported));
        return supported[0];
    }

    /** Releases the pass. Closing twice is a no-op. */
    @Override
    public final void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        GraphicsExtension.check("PostProcessPass.close", destroy(handle));
    }

    /**
     * Releases the native pass.
     *
     * <p>Overridable because not every pass answers to the common release route, however the
     * header reads: a {@link DecalPass} carries its own handle kind and
     * {@code cna_post_process_pass_destroy} refuses it. See {@code JAVA-UPSTREAM-008}.
     *
     * @param pass the handle to release
     * @return CNA's own result
     */
    int destroy(long pass) {
        return NativeEngineLayerRoutes.postProcessPassDestroy(pass);
    }

    /** The native handle, for the pipeline and for each subclass's own settings. */
    final long handle() {
        return open();
    }

    /** Creates a pass on a device with one of CNA's per-pass constructors. */
    static long createOn(GraphicsDevice graphicsDevice, String operation, PassFactory factory) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] pass = new long[1];
        GraphicsExtension.check(operation,
                factory.call(NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), pass));
        return pass[0];
    }

    /** One of CNA's per-pass constructors. */
    @FunctionalInterface
    interface PassFactory {
        int call(long graphicsDevice, long[] outPass);
    }

    /** A float CNA answers about one pass. */
    @FunctionalInterface
    interface FloatRoute {
        int call(long pass, float[] answer);
    }

    /** An int CNA answers about one pass. */
    @FunctionalInterface
    interface IntRoute {
        int call(long pass, int[] answer);
    }

    /** A boolean CNA answers about one pass. */
    @FunctionalInterface
    interface FlagRoute {
        int call(long pass, boolean[] answer);
    }

    /** A vector CNA answers about one pass. */
    @FunctionalInterface
    interface VectorRoute {
        int call(long pass, float[] answer);
    }

    final float readFloat(String operation, FloatRoute route) {
        float[] answer = new float[1];
        GraphicsExtension.check(operation, route.call(open(), answer));
        return answer[0];
    }

    final int readInt(String operation, IntRoute route) {
        int[] answer = new int[1];
        GraphicsExtension.check(operation, route.call(open(), answer));
        return answer[0];
    }

    final boolean readFlag(String operation, FlagRoute route) {
        boolean[] answer = new boolean[1];
        GraphicsExtension.check(operation, route.call(open(), answer));
        return answer[0];
    }

    final float[] readVector(String operation, int leaves, VectorRoute route) {
        float[] answer = new float[leaves];
        GraphicsExtension.check(operation, route.call(open(), answer));
        return answer;
    }

    /** Copies out one of CNA's UTF-8 diagnostics for a pass. */
    static String readText(String operation, TextRoute route) {
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

    /** A copy-out of UTF-8 bytes CNA sizes first. */
    @FunctionalInterface
    interface TextRoute {
        int call(byte[] destination, long[] bytes);
    }

    /**
     * Returns the live handle, refusing a closed pass.
     *
     * <p>Package-private rather than private so a subclass with routes of its own -- an
     * {@link EffectPass} and its effect -- refuses a closed pass the same way this does, instead
     * of each one inventing its own check.
     */
    long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException(
                        "This " + getClass().getSimpleName() + " is closed");
            }
        }
        return handle;
    }
}
