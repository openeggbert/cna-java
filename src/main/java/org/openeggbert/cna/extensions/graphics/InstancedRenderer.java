package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Color;
import Microsoft.Xna.Framework.Graphics.Effect;
import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.IndexBuffer;
import Microsoft.Xna.Framework.Graphics.ModelMeshPart;
import Microsoft.Xna.Framework.Graphics.VertexBuffer;
import Microsoft.Xna.Framework.Graphics.VertexDeclaration;
import Microsoft.Xna.Framework.Graphics.VertexElement;
import Microsoft.Xna.Framework.Graphics.VertexElementFormat;
import Microsoft.Xna.Framework.Graphics.VertexElementUsage;
import Microsoft.Xna.Framework.Matrix;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;
import org.openeggbert.cna.internal.generated.NativeModelExtensionRoutes;

import java.util.List;
import java.util.Objects;

/**
 * Draws one piece of geometry many times, in one call where the renderer can.
 *
 * <p>A CNA extension. XNA 4.0 has the pieces -- a second vertex stream, {@code
 * DrawInstancedPrimitives} on Windows -- but no object that owns the instance buffer, grows it,
 * describes it, falls back when the renderer cannot instance, and tells you afterwards which of
 * those happened. This is that object.
 *
 * <p><strong>The fallback is the interesting part.</strong> A renderer that cannot draw instances
 * in one call can still draw them one at a time, and CNA will do that rather than fail -- but
 * only if the fallback is allowed and only with an effect that can carry a per-instance
 * transform. {@link #getLastDrawCallCount()} and {@link #didLastDrawInstance()} say which
 * happened, so a game can log it or refuse to ship a scene that silently became a thousand draw
 * calls. Turn the fallback off with {@link #setFallbackEnabled} and a renderer that cannot
 * instance refuses the draw instead.
 *
 * <p><strong>Capacity never shrinks.</strong> Uploading fewer instances than the largest frame so
 * far leaves the buffer where it was, which is what makes a varying instance count allocate
 * nothing after that frame. {@link #getInstanceCapacity()} exposes it because a game budgeting
 * memory needs the high-water mark, not the current count.
 *
 * <p><strong>Ownership.</strong> This owns two native objects: CNA's own mesh part, built over
 * the buffers handed in, and the renderer over it. Both are released by {@link #close()}, the
 * renderer first, because CNA borrows the part and documents that it must outlive the renderer.
 * The {@link VertexBuffer} and {@link IndexBuffer} are <em>retained, not owned</em> -- this holds
 * a reference so nothing collects them, and closing it does not dispose them. Whoever created
 * them still does.
 */
public final class InstancedRenderer implements AutoCloseable {

    /** CNA's own result for a buffer that could not hold the answer. */
    private static final int RESULT_BUFFER_TOO_SMALL = 14;

    /** A vertex element is its offset, format, usage and usage index. */
    private static final int ELEMENT_LEAVES = 4;

    private final long handle;
    private final long part;
    // Retained so the geometry cannot be collected while CNA is drawing from it. Never disposed
    // here: this object did not create them.
    private final VertexBuffer vertices;
    private final IndexBuffer indices;
    private boolean closed;

    private InstancedRenderer(long handle, long part, VertexBuffer vertices, IndexBuffer indices) {
        this.handle = handle;
        this.part = part;
        this.vertices = vertices;
        this.indices = indices;
    }

    /**
     * Creates a renderer over one part of a loaded model.
     *
     * @param graphicsDevice the device to draw with
     * @param part the mesh part to instance; it must have both buffers and draw at least one
     *        primitive
     * @return the renderer, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static InstancedRenderer create(GraphicsDevice graphicsDevice, ModelMeshPart part) {
        Objects.requireNonNull(part, "part");
        return create(graphicsDevice, part.getVertexBuffer(), part.getIndexBuffer(),
                part.getVertexOffset(), part.getNumVertices(),
                part.getStartIndex(), part.getPrimitiveCount());
    }

    /**
     * Creates a renderer over geometry a game built itself.
     *
     * <p>The same object as the {@link ModelMeshPart} overload, for a game whose geometry never
     * came from a model: XNA only ever hands a {@code ModelMeshPart} out of a loaded
     * {@code Model}, and a procedurally built mesh has the same six numbers without one.
     *
     * @param graphicsDevice the device to draw with
     * @param vertices the vertex buffer, retained rather than owned
     * @param indices the index buffer, retained rather than owned
     * @param vertexOffset the first vertex, added to every decoded index
     * @param numVertices how many vertices the draw range spans
     * @param startIndex the first index
     * @param primitiveCount how many triangles to draw; must be at least one
     * @return the renderer, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static InstancedRenderer create(GraphicsDevice graphicsDevice, VertexBuffer vertices,
            IndexBuffer indices, int vertexOffset, int numVertices,
            int startIndex, int primitiveCount) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        Objects.requireNonNull(vertices, "vertices");
        Objects.requireNonNull(indices, "indices");
        long[] created = new long[1];
        GraphicsExtension.check("InstancedRenderer.create",
                NativeModelExtensionRoutes.modelMeshPartCreate(
                        NativeBindings.nativeResourceHandle(vertices),
                        NativeBindings.nativeResourceHandle(indices),
                        numVertices, primitiveCount, startIndex, vertexOffset, created));
        long part = created[0];
        long[] renderer = new long[1];
        try {
            GraphicsExtension.check("InstancedRenderer.create",
                    NativeEngineLayerRoutes.instancedRendererExtCreate(
                            NativeBindings.nativeGraphicsDeviceValue(graphicsDevice),
                            part, renderer));
        } catch (RuntimeException failure) {
            // The part is this object's alone and nothing else can reach it, so a renderer that
            // never existed must not leave it behind.
            NativeModelExtensionRoutes.modelMeshPartDestroy(part);
            throw failure;
        }
        return new InstancedRenderer(renderer[0], part, vertices, indices);
    }

    /**
     * Returns the declaration a per-instance transform stream has to have.
     *
     * <p>Four {@code Vector4} elements at {@code TextureCoordinate} usage indices one through
     * four, sixty-four bytes. Exposed because a game building its own instance buffer must
     * describe it <em>identically</em>, and reading CNA's answer is how it can check that rather
     * than write the same four lines and hope.
     *
     * @return the declaration, with CNA's own stride
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static VertexDeclaration getInstanceDeclaration() {
        GraphicsExtension.requireBackend();
        int[] stride = new int[1];
        GraphicsExtension.check("InstancedRenderer.getInstanceDeclaration",
                NativeEngineLayerRoutes.instancedRendererExtGetInstanceStride(stride));
        return declaration(stride[0], NativeEngineLayerRoutes::instancedRendererExtCopyInstanceElements,
                "InstancedRenderer.getInstanceDeclaration");
    }

    /**
     * Returns the declaration the optional per-instance tint stream has.
     *
     * @return the declaration, with CNA's own stride
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static VertexDeclaration getTintDeclaration() {
        GraphicsExtension.requireBackend();
        int[] stride = new int[1];
        GraphicsExtension.check("InstancedRenderer.getTintDeclaration",
                NativeEngineLayerRoutes.instancedRendererExtGetTintStride(stride));
        return declaration(stride[0], NativeEngineLayerRoutes::instancedRendererExtCopyTintElements,
                "InstancedRenderer.getTintDeclaration");
    }

    /**
     * Returns the vertex buffer this renderer draws from.
     *
     * <p>Retained rather than owned: closing this renderer does not dispose it.
     *
     * @return the buffer handed to {@code create}
     */
    public VertexBuffer getVertexBuffer() {
        return vertices;
    }

    /**
     * Returns the index buffer this renderer draws from.
     *
     * <p>Retained rather than owned: closing this renderer does not dispose it.
     *
     * @return the buffer handed to {@code create}
     */
    public IndexBuffer getIndexBuffer() {
        return indices;
    }

    /**
     * Uploads the per-instance world transforms, replacing whatever was there.
     *
     * <p>Uploading none is not an error: it is how a game stops drawing without destroying the
     * renderer.
     *
     * @param transforms the world matrices, one per instance
     */
    public void setInstances(List<Matrix> transforms) {
        GraphicsExtension.check("InstancedRenderer.setInstances",
                NativeEngineLayerRoutes.instancedRendererExtSetInstances(open(),
                        EngineValues.matrices(transforms, "transforms")));
    }

    /**
     * Uploads the per-instance tints, replacing whatever was there.
     *
     * <p>Independent of {@link #setTintsEnabled}: tints can be uploaded while the stream is
     * unbound and are simply not read.
     *
     * @param tints the tints, one per instance
     */
    public void setInstanceTints(List<Color> tints) {
        Objects.requireNonNull(tints, "tints");
        long[] packed = new long[Math.multiplyExact(tints.size(), 4)];
        for (int index = 0; index < tints.size(); index++) {
            long[] channels = EngineValues.channels(tints.get(index), "tints[" + index + "]");
            System.arraycopy(channels, 0, packed, index * 4, 4);
        }
        GraphicsExtension.check("InstancedRenderer.setInstanceTints",
                NativeEngineLayerRoutes.instancedRendererExtSetInstanceTints(open(), packed));
    }

    /**
     * Reports whether the tint stream is bound.
     *
     * @return whether it is bound
     */
    public boolean isTintsEnabled() {
        return flag("InstancedRenderer.isTintsEnabled",
                NativeEngineLayerRoutes::instancedRendererExtIsTintsEnabled);
    }

    /**
     * Binds or unbinds the tint stream.
     *
     * @param enabled whether to bind it
     */
    public void setTintsEnabled(boolean enabled) {
        GraphicsExtension.check("InstancedRenderer.setTintsEnabled",
                NativeEngineLayerRoutes.instancedRendererExtSetTintsEnabled(open(), enabled));
    }

    /**
     * Draws every uploaded instance.
     *
     * <p><strong>The refusal a game can act on is raised as {@link IllegalStateException}</strong>
     * even though CNA does not currently report it as one. A renderer that cannot instance, with
     * the fallback off, is a configuration the caller chose and can change; CNA's header
     * documents that as {@code CNA_RESULT_INVALID_STATE} and the library in fact returns
     * {@code CNA_RESULT_INTERNAL}, because its exception barrier has no arm for the
     * {@code std::logic_error} the engine throws. Recorded as {@code JAVA-UPSTREAM-006}. Rather
     * than match on the message, this asks the renderer the two questions that define the
     * refusal, and only on the failure path so an ordinary draw costs nothing extra.
     *
     * <p>The other documented refusal -- the fallback is on and the effect cannot carry a
     * per-instance transform -- cannot be distinguished from Java and arrives as the native
     * failure it is.
     *
     * @param effect the effect to draw with; the per-instance fallback additionally needs one
     *        that can carry a transform
     * @throws IllegalStateException when this renderer cannot instance and the fallback is off
     */
    public void draw(Effect effect) {
        Objects.requireNonNull(effect, "effect");
        int result = NativeEngineLayerRoutes.instancedRendererExtDraw(open(),
                NativeBindings.nativeResourceHandle(effect));
        if (result != 0 && !isInstancingSupported() && !isFallbackEnabled()) {
            throw new IllegalStateException(
                    "This renderer cannot draw instances in one call and the per-instance "
                    + "fallback is not enabled; call setFallbackEnabled(true) to draw one call "
                    + "per instance instead",
                    NativeBindings.failure("InstancedRenderer.draw", result));
        }
        GraphicsExtension.check("InstancedRenderer.draw", result);
    }

    /**
     * Reports whether this renderer can draw every instance in one call.
     *
     * @return whether the renderer instances
     */
    public boolean isInstancingSupported() {
        return flag("InstancedRenderer.isInstancingSupported",
                NativeEngineLayerRoutes::instancedRendererExtIsInstancingSupported);
    }

    /**
     * Reports whether the one-draw-per-instance fallback is allowed.
     *
     * @return whether it is allowed
     */
    public boolean isFallbackEnabled() {
        return flag("InstancedRenderer.isFallbackEnabled",
                NativeEngineLayerRoutes::instancedRendererExtIsFallbackEnabled);
    }

    /**
     * Allows or forbids the one-draw-per-instance fallback.
     *
     * @param enabled whether to allow it
     */
    public void setFallbackEnabled(boolean enabled) {
        GraphicsExtension.check("InstancedRenderer.setFallbackEnabled",
                NativeEngineLayerRoutes.instancedRendererExtSetFallbackEnabled(open(), enabled));
    }

    /**
     * Returns how many instances are uploaded.
     *
     * @return the count
     */
    public int getInstanceCount() {
        return count("InstancedRenderer.getInstanceCount",
                NativeEngineLayerRoutes::instancedRendererExtGetInstanceCount);
    }

    /**
     * Returns how many instances the buffer holds without growing.
     *
     * @return the capacity, which never shrinks
     */
    public int getInstanceCapacity() {
        return count("InstancedRenderer.getInstanceCapacity",
                NativeEngineLayerRoutes::instancedRendererExtGetInstanceCapacity);
    }

    /**
     * Returns how many draw calls the last draw issued.
     *
     * <p>One when it instanced, one per instance when it fell back. Means nothing before the
     * first draw.
     *
     * @return the count
     */
    public int getLastDrawCallCount() {
        return count("InstancedRenderer.getLastDrawCallCount",
                NativeEngineLayerRoutes::instancedRendererExtGetLastDrawCallCount);
    }

    /**
     * Reports whether the last draw instanced rather than falling back.
     *
     * <p>A record of what happened, not a capability: it is written by {@link #draw} and means
     * nothing before the first one. {@link #isInstancingSupported()} is the capability.
     *
     * @return whether the last draw instanced
     */
    public boolean didLastDrawInstance() {
        return flag("InstancedRenderer.didLastDrawInstance",
                NativeEngineLayerRoutes::instancedRendererExtDidLastDrawInstance);
    }

    /**
     * Releases the renderer and the mesh part built for it, in that order.
     *
     * <p>The order is CNA's requirement, not a preference: the part is borrowed by the renderer
     * and documented as having to outlive it. The vertex and index buffers are untouched --
     * they were retained, never owned.
     */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        try {
            GraphicsExtension.check("InstancedRenderer.close",
                    NativeEngineLayerRoutes.instancedRendererExtDestroy(handle));
        } finally {
            GraphicsExtension.check("InstancedRenderer.close",
                    NativeModelExtensionRoutes.modelMeshPartDestroy(part));
        }
    }

    /** A boolean CNA answers about one renderer. */
    @FunctionalInterface
    private interface FlagRoute {
        int call(long renderer, boolean[] answer);
    }

    /** A count CNA answers about one renderer. */
    @FunctionalInterface
    private interface CountRoute {
        int call(long renderer, int[] answer);
    }

    /** A copy-out of vertex elements, which the two declarations share. */
    @FunctionalInterface
    private interface ElementRoute {
        int call(long[] destination, long[] count);
    }

    private boolean flag(String operation, FlagRoute route) {
        boolean[] answer = new boolean[1];
        GraphicsExtension.check(operation, route.call(open(), answer));
        return answer[0];
    }

    private int count(String operation, CountRoute route) {
        int[] answer = new int[1];
        GraphicsExtension.check(operation, route.call(open(), answer));
        return answer[0];
    }

    private static VertexDeclaration declaration(int stride, ElementRoute route, String operation) {
        long[] count = new long[1];
        // A zero-capacity probe reports the count and writes nothing, so BUFFER_TOO_SMALL is the
        // expected answer to the first call rather than a failure.
        int probe = route.call(new long[0], count);
        if (probe != RESULT_BUFFER_TOO_SMALL) {
            GraphicsExtension.check(operation, probe);
        }
        int elements = Math.toIntExact(count[0]);
        long[] packed = new long[Math.multiplyExact(elements, ELEMENT_LEAVES)];
        GraphicsExtension.check(operation, route.call(packed, count));
        VertexElement[] declared = new VertexElement[elements];
        for (int index = 0; index < elements; index++) {
            int base = index * ELEMENT_LEAVES;
            declared[index] = new VertexElement(
                    Math.toIntExact(packed[base]),
                    format(packed[base + 1]),
                    usage(packed[base + 2]),
                    Math.toIntExact(packed[base + 3]));
        }
        return new VertexDeclaration(stride, declared);
    }

    private static VertexElementFormat format(long value) {
        VertexElementFormat[] values = VertexElementFormat.values();
        if (value < 0 || value >= values.length) {
            throw new IllegalStateException("CNA reported vertex element format " + value
                    + ", which this build has no constant for");
        }
        return values[(int) value];
    }

    private static VertexElementUsage usage(long value) {
        VertexElementUsage[] values = VertexElementUsage.values();
        if (value < 0 || value >= values.length) {
            throw new IllegalStateException("CNA reported vertex element usage " + value
                    + ", which this build has no constant for");
        }
        return values[(int) value];
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This InstancedRenderer is closed");
            }
        }
        return handle;
    }
}
