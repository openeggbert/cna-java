package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import org.openeggbert.cna.internal.CnaNativeException;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * A program the GPU runs over a grid of work groups, outside the drawing pipeline.
 *
 * <p>A CNA extension, and one of the few here with no XNA shape whatsoever: XNA 4.0's pipeline
 * ends at the pixel shader, and a game that wanted to do arithmetic on the GPU had to disguise it
 * as a full-screen draw into a render target. This is the real thing -- a program, a set of
 * {@link StorageBuffer}s bound to numbered points, and a dispatch.
 *
 * <p><strong>The source dialect is the renderer's, and CNA's is GLSL ES.</strong> Every compute
 * program inside CNA's own engine layer opens with {@code #version 310 es}, which compiles on
 * every renderer here that has compute at all -- including the ones whose context is desktop
 * OpenGL, because a desktop GL 4.3+ compiler accepts it. Desktop GLSL ({@code #version 430 core})
 * compiles only where the context really is desktop GL, so a game that wants to run on both
 * writes GLSL ES.
 *
 * <p><strong>A source that does not compile raises
 * {@link ShaderCompilationException}, carrying the compiler's log.</strong> CNA's header describes
 * a different shape -- creation succeeding and the failure being read off the object afterwards --
 * and its implementation throws instead, which the C exception barrier reports as a generic
 * internal failure. That is measured in {@code tools/native-abi/probes/compute_compile_contract.c}
 * and recorded as {@code JAVA-UPSTREAM-012}. This class presents the shape CNA actually has, and
 * <em>also</em> checks {@link #isValid()} on the success path, so that the day CNA honours its own
 * header a caller here still gets the same exception rather than a shader that never compiled.
 *
 * <p><strong>Binding borrows.</strong> A bound {@link StorageBuffer} or {@link Texture2D} is not
 * retained: it must outlive every dispatch that reads it. Java keeps a strong reference to each
 * bound buffer so that garbage collection alone cannot pull one out from under a dispatch, but
 * closing one explicitly while it is still bound is the caller's mistake to avoid.
 *
 * <p><strong>A dispatch is not finished when it returns.</strong> {@link #dispatch} queues work;
 * {@link #barrier} is what orders a later read against it. Reading a storage buffer back without
 * one is a race that will usually appear to work.
 *
 * <p><strong>The renderer decides whether this can exist.</strong> Compiling needs
 * {@link GraphicsCapability#ComputeShaders}; without it CNA refuses and this raises
 * {@link ExtensionNotSupportedException}. {@link #isSupported} is how to ask first.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class ComputeShader implements AutoCloseable {

    /** CNA's own result for a buffer that could not hold the answer. */
    private static final int RESULT_BUFFER_TOO_SMALL = 14;

    /**
     * CNA's own result for a failure it could not classify. Measured to be the one and only
     * answer {@code cna_compute_shader_create} gives for source that does not compile.
     */
    private static final int RESULT_INTERNAL = 12;

    private final long handle;

    /**
     * Every buffer and texture currently bound, held strongly.
     *
     * <p>CNA borrows a bound resource and states that it must outlive the dispatch. Java's
     * collector has no way to know that, so a buffer whose last reference was the local variable
     * that bound it could be collected -- and its {@code close} run by whatever finaliser a
     * future version of this binding grows -- between the bind and the dispatch. Holding it here
     * costs one reference and removes the whole class of problem; it deliberately does not
     * <em>own</em> anything, so closing the shader releases the references without closing the
     * buffers.
     */
    private final java.util.Map<Integer, AutoCloseable> boundBuffers = new java.util.HashMap<>();
    private final java.util.Map<Integer, Object> boundTextures = new java.util.HashMap<>();

    private boolean closed;

    private ComputeShader(long handle) {
        this.handle = handle;
    }

    /**
     * Reports whether a device's renderer can run compute shaders at all.
     *
     * @param graphicsDevice the device to ask about
     * @return whether {@link #compile} can succeed
     * @throws ExtensionNotSupportedException when this build has no extended graphics layer
     */
    public static boolean isSupported(GraphicsDevice graphicsDevice) {
        return RendererCapabilities.supports(graphicsDevice, GraphicsCapability.ComputeShaders);
    }

    /**
     * Compiles a compute program.
     *
     * @param graphicsDevice the device to compile and run on
     * @param source the shader source, in the renderer's own dialect; CNA's is GLSL ES, so
     *        {@code #version 310 es} is the portable choice
     * @return the shader, which the caller closes
     * @throws ShaderCompilationException when the renderer's compiler refuses the source; the
     *         message carries its log
     * @throws ExtensionNotSupportedException when this build has no engine layer, or the renderer
     *         has no compute shaders
     */
    public static ComputeShader compile(GraphicsDevice graphicsDevice, String source) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        Objects.requireNonNull(source, "source");
        long[] shader = new long[1];
        int result = NativeEngineLayerRoutes.computeShaderCreate(
                NativeBindings.nativeGraphicsDeviceValue(graphicsDevice),
                source.getBytes(StandardCharsets.UTF_8), shader);
        if (result == RESULT_INTERNAL) {
            // The measured shape of a refused source. cna_compute_shader_create answers
            // NOT_SUPPORTED without compute, INVALID_HANDLE for a bad device, INVALID_ARGUMENT
            // for a null output and SUCCESS for source that compiles; INTERNAL is what a
            // std::runtime_error from the canonical constructor becomes, and the constructor
            // throws that for exactly one reason. The native diagnostic is the compiler's log.
            CnaNativeException failure = NativeBindings.failure("ComputeShader.compile", result);
            throw new ShaderCompilationException(failure.getMessage(), failure);
        }
        GraphicsExtension.check("ComputeShader.compile", result);
        ComputeShader compiled = new ComputeShader(shader[0]);
        if (!compiled.isValid()) {
            // Unreachable on the CNA measured here, and deliberately kept: this is the branch the
            // header documents, and the day the implementation matches it a caller must still be
            // told rather than handed a shader that never compiled.
            String reason = compiled.getCompileError();
            compiled.close();
            throw new ShaderCompilationException(reason, null);
        }
        return compiled;
    }

    /**
     * Reports whether the program compiled.
     *
     * <p>Always {@code true} for a shader that exists on the CNA measured here, because a source
     * that did not compile never produces one. Asked anyway, for the reason {@link #compile}
     * gives.
     *
     * @return whether the shader is usable
     */
    public boolean isValid() {
        boolean[] valid = new boolean[1];
        GraphicsExtension.check("ComputeShader.isValid",
                NativeEngineLayerRoutes.computeShaderIsValid(open(), valid));
        return valid[0];
    }

    /**
     * Returns the compiler's log for a shader that did not compile.
     *
     * @return the log, or an empty string when the shader compiled
     */
    public String getCompileError() {
        long shader = open();
        long[] bytes = new long[1];
        // A zero-capacity probe reports the byte count and writes nothing, and CNA writes no
        // partial string, so BUFFER_TOO_SMALL is an expected answer rather than a failure.
        int probe = NativeEngineLayerRoutes
                .computeShaderCopyCompileError(shader, new byte[0], bytes);
        if (probe != RESULT_BUFFER_TOO_SMALL) {
            GraphicsExtension.check("ComputeShader.getCompileError", probe);
        }
        int length = Math.toIntExact(bytes[0]);
        if (length == 0) {
            return "";
        }
        byte[] destination = new byte[length];
        GraphicsExtension.check("ComputeShader.getCompileError", NativeEngineLayerRoutes
                .computeShaderCopyCompileError(shader, destination, bytes));
        return new String(destination, 0, Math.toIntExact(bytes[0]), StandardCharsets.UTF_8);
    }

    /**
     * Sets a signed-integer uniform.
     *
     * <p>A name the program does not declare is accepted and does nothing, which is the
     * renderer's behaviour rather than a check this could add: a uniform the compiler removed
     * because nothing read it is indistinguishable from one that was never declared.
     *
     * @param name the uniform's name
     * @param value the value
     */
    public void setUniform(String name, int value) {
        Objects.requireNonNull(name, "name");
        GraphicsExtension.check("ComputeShader.setUniform",
                NativeEngineLayerRoutes.computeShaderSetUniformInt(open(),
                        name.getBytes(StandardCharsets.UTF_8), value));
    }

    /**
     * Sets a floating-point uniform.
     *
     * @param name the uniform's name
     * @param value the value
     */
    public void setUniform(String name, float value) {
        Objects.requireNonNull(name, "name");
        GraphicsExtension.check("ComputeShader.setUniform",
                NativeEngineLayerRoutes.computeShaderSetUniformFloat(open(),
                        name.getBytes(StandardCharsets.UTF_8), value));
    }

    /**
     * Binds a storage buffer to a numbered binding point.
     *
     * <p>The binding index is the one the shader's {@code layout(std430, binding = N)} names.
     *
     * @param binding the binding index
     * @param buffer the buffer, borrowed rather than owned
     */
    public void bindStorageBuffer(int binding, StorageBuffer buffer) {
        Objects.requireNonNull(buffer, "buffer");
        GraphicsExtension.check("ComputeShader.bindStorageBuffer", NativeEngineLayerRoutes
                .computeShaderBindStorageBuffer(open(), binding, buffer.nativeHandle()));
        boundBuffers.put(binding, buffer);
    }

    /**
     * Binds a texture to a numbered sampler unit.
     *
     * @param unit the texture unit
     * @param samplerName the sampler uniform's name in the shader
     * @param texture the texture, borrowed rather than owned
     */
    public void bindTexture(int unit, String samplerName, Texture2D texture) {
        Objects.requireNonNull(samplerName, "samplerName");
        Objects.requireNonNull(texture, "texture");
        GraphicsExtension.check("ComputeShader.bindTexture", NativeEngineLayerRoutes
                .computeShaderBindTexture(open(), unit,
                        samplerName.getBytes(StandardCharsets.UTF_8),
                        NativeBindings.nativeResourceHandle(texture)));
        boundTextures.put(unit, texture);
    }

    /**
     * Reports whether this renderer can bind a texture as a read/write image at all.
     *
     * <p>A different question from having compute, and the two really do come apart: an OpenGL
     * ES 3.1 context has compute and requires an immutable texture allocation this renderer does
     * not make, so it answers {@code false} here while a desktop GL 4.6 context answers
     * {@code true}. Where it is false, route a compute shader's output through a
     * {@link StorageBuffer} instead.
     *
     * @return whether {@link #bindImage} can work
     */
    public boolean isImageBindingSupported() {
        boolean[] supported = new boolean[1];
        GraphicsExtension.check("ComputeShader.isImageBindingSupported",
                NativeEngineLayerRoutes.computeShaderIsImageBindingSupported(open(), supported));
        return supported[0];
    }

    /**
     * Binds a texture as a read/write image.
     *
     * @param unit the image unit
     * @param texture the texture, borrowed rather than owned
     * @param access how the shader will touch it
     * @throws ExtensionNotSupportedException where image binding is unavailable; ask
     *         {@link #isImageBindingSupported()} first
     */
    public void bindImage(int unit, Texture2D texture, ImageAccess access) {
        Objects.requireNonNull(texture, "texture");
        Objects.requireNonNull(access, "access");
        GraphicsExtension.check("ComputeShader.bindImage", NativeEngineLayerRoutes
                .computeShaderBindImage(open(), unit, NativeBindings.nativeResourceHandle(texture),
                        access.toValue()));
        boundTextures.put(unit, texture);
    }

    /**
     * Dispatches the program over a grid of work groups.
     *
     * <p>The grid is in <em>groups</em>, not invocations: a shader declaring
     * {@code layout(local_size_x = 64)} dispatched with {@code groupsX = 2} runs 128 invocations.
     *
     * @param groupsX work groups along X
     * @param groupsY work groups along Y
     * @param groupsZ work groups along Z
     */
    public void dispatch(int groupsX, int groupsY, int groupsZ) {
        GraphicsExtension.check("ComputeShader.dispatch",
                NativeEngineLayerRoutes.computeShaderDispatch(open(), groupsX, groupsY, groupsZ));
    }

    /**
     * Orders the given kinds of access against later commands.
     *
     * @param bits which later accesses have to wait for this shader's writes
     */
    public void barrier(Set<MemoryBarrier> bits) {
        Objects.requireNonNull(bits, "bits");
        GraphicsExtension.check("ComputeShader.barrier",
                NativeEngineLayerRoutes.computeShaderBarrier(open(), MemoryBarrier.maskOf(bits)));
    }

    /**
     * Orders the given kinds of access against later commands.
     *
     * @param first one kind to order
     * @param rest any further kinds
     */
    public void barrier(MemoryBarrier first, MemoryBarrier... rest) {
        Objects.requireNonNull(first, "first");
        EnumSet<MemoryBarrier> bits = EnumSet.of(first);
        for (MemoryBarrier bit : rest) {
            bits.add(Objects.requireNonNull(bit, "rest"));
        }
        barrier(bits);
    }

    /** Releases the program. Closing twice is a no-op, and no bound buffer or texture is closed. */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        boundBuffers.clear();
        boundTextures.clear();
        GraphicsExtension.check("ComputeShader.close",
                NativeEngineLayerRoutes.computeShaderDestroy(handle));
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This ComputeShader is closed");
            }
        }
        return handle;
    }
}
