package org.openeggbert.cna.extensions.content;

import Microsoft.Xna.Framework.Graphics.IndexBuffer;
import Microsoft.Xna.Framework.Graphics.ModelMeshPart;
import Microsoft.Xna.Framework.Graphics.VertexBuffer;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeModelExtensionRoutes;

import java.util.Objects;

/**
 * CNA's own view of one mesh part, which a skinned model draws.
 *
 * <p>A CNA extension. The XNA {@link ModelMeshPart} a Java game has is a managed object with no
 * native side -- the managed content reader built it, and CNA never saw it -- so a skinned model
 * that draws it needs CNA's own. This is that, made from the same four numbers and two buffers
 * XNA's part carries.
 *
 * <p><strong>The part is always the caller's, and the close order is a chain.</strong> Measured
 * rather than assumed, because the wording invites the other reading:
 * {@link CnaSkinnedModel#addPart} <em>retains</em> the part, it does not take it. A model that has
 * released everything it owns still leaves the part alive, and the part still retains the vertex
 * and index buffers it was made from -- so destroying a buffer first is refused with
 * {@code INVALID_STATE}, naming the part that holds it.
 *
 * <p>The order is therefore: close the model, then the part, then the buffers. In a
 * try-with-resources that means declaring them the other way round, and getting it wrong is what
 * found the rule.
 */
public final class CnaModelMeshPartHandle implements AutoCloseable {

    private final long handle;

    /** Whether closing this frees the part, which an alias a model lent back does not. */
    private final boolean owned;
    private boolean closed;

    CnaModelMeshPartHandle(long handle, boolean owned) {
        this.handle = handle;
        this.owned = owned;
    }

    /**
     * Creates CNA's own mesh part from the same description an XNA one carries.
     *
     * @param vertexBuffer the part's vertices
     * @param indexBuffer the part's indices
     * @param numVertices how many vertices the part draws
     * @param primitiveCount how many primitives it draws
     * @param startIndex where in the index buffer it starts
     * @param vertexOffset what to add to every index
     * @return the part, which the caller owns and closes after any model that retains it
     */
    public static CnaModelMeshPartHandle create(VertexBuffer vertexBuffer,
            IndexBuffer indexBuffer, int numVertices, int primitiveCount, int startIndex,
            int vertexOffset) {
        CnbExtension.requireAvailable();
        Objects.requireNonNull(vertexBuffer, "vertexBuffer");
        Objects.requireNonNull(indexBuffer, "indexBuffer");
        long[] created = new long[1];
        CnbExtension.check("CnaModelMeshPartHandle.create",
                NativeModelExtensionRoutes.modelMeshPartCreate(
                        NativeBindings.nativeResourceHandle(vertexBuffer),
                        NativeBindings.nativeResourceHandle(indexBuffer),
                        numVertices, primitiveCount, startIndex, vertexOffset, created));
        return new CnaModelMeshPartHandle(created[0], true);
    }

    /**
     * Creates CNA's own mesh part from an XNA one.
     *
     * @param part the managed part to mirror
     * @return the part, which the caller owns and closes after any model that retains it
     */
    public static CnaModelMeshPartHandle of(ModelMeshPart part) {
        Objects.requireNonNull(part, "part");
        return create(part.getVertexBuffer(), part.getIndexBuffer(), part.getNumVertices(),
                part.getPrimitiveCount(), part.getStartIndex(), part.getVertexOffset());
    }

    /** The native handle, for the model that takes it. */
    long value() {
        if (closed) {
            throw new IllegalStateException("this CnaModelMeshPartHandle is closed");
        }
        return handle;
    }

    /**
     * Releases the part, when this handle is the owning one rather than a model's alias.
     *
     * <p>Closing twice is a no-op. Close it <strong>after</strong> every model that retains it
     * and <strong>before</strong> the buffers it was made from.
     */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        if (owned) {
            CnbExtension.check("CnaModelMeshPartHandle.close",
                    NativeModelExtensionRoutes.modelMeshPartDestroy(handle));
        }
    }
}
