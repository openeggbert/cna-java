package Microsoft.Xna.Framework.Graphics;

import org.openeggbert.cna.internal.NativeBindings;

import java.util.Objects;

/** Device-owned vertex buffer with deterministic CNA lifetime and atomic typed transfers. */
@SuppressWarnings("this-escape")
public class VertexBuffer extends GraphicsResource {

    private final BufferUsage bufferUsage;
    private final int vertexCount;
    private final VertexDeclaration vertexDeclaration;

    public VertexBuffer(
            GraphicsDevice graphicsDevice,
            VertexDeclaration vertexDeclaration,
            int vertexCount,
            BufferUsage usage) {
        super(Objects.requireNonNull(graphicsDevice, "graphicsDevice"));
        VertexDeclaration declaration = Objects.requireNonNull(
                vertexDeclaration, "vertexDeclaration");
        if (vertexCount <= 0) {
            throw new IllegalArgumentException("vertexCount must be positive");
        }
        BufferUsage selectedUsage = Objects.requireNonNull(usage, "usage");
        int[] info = NativeBindings.createVertexBuffer(
                this, graphicsDevice, declaration.getVertexStride(),
                declaration.descriptorForUse(graphicsDevice),
                vertexCount, selectedUsage.getValue());
        if (info.length != 4 || info[0] != vertexCount
                || info[1] != selectedUsage.getValue()
                || info[2] != declaration.getVertexStride()
                || info[3] != declaration.GetVertexElements().length) {
            NativeBindings.closeGraphicsResource(this);
            throw new IllegalStateException("CNA returned inconsistent VertexBuffer metadata");
        }
        this.vertexDeclaration = declaration;
        this.vertexCount = vertexCount;
        bufferUsage = selectedUsage;
    }

    public VertexBuffer(
            GraphicsDevice graphicsDevice,
            Class<?> vertexType,
            int vertexCount,
            BufferUsage usage) {
        this(graphicsDevice, VertexDeclaration.fromType(vertexType), vertexCount, usage);
    }

    public final <T> void SetData(T[] data) {
        Objects.requireNonNull(data, "data");
        SetData(data, 0, data.length);
    }

    public final <T> void SetData(T[] data, int startIndex, int elementCount) {
        ensureNotDisposed();
        validateArrayWindow(Objects.requireNonNull(data, "data").length, startIndex, elementCount);
        if (elementCount > vertexCount) {
            throw new IllegalArgumentException("Vertex upload exceeds the buffer capacity");
        }
        VertexDataCodec codec = VertexDataCodec.select(data);
        if (codec.stride() != vertexDeclaration.getVertexStride()) {
            throw new IllegalArgumentException(
                    "Vertex value size does not match the buffer declaration stride");
        }
        NativeBindings.setVertexBufferData(
                this, -1, codec.encode(data, startIndex, elementCount),
                elementCount, codec.stride());
    }

    public final <T> void SetData(
            int offsetInBytes,
            T[] data,
            int startIndex,
            int elementCount,
            int vertexStride) {
        ensureNotDisposed();
        validateArrayWindow(Objects.requireNonNull(data, "data").length, startIndex, elementCount);
        VertexDataCodec codec = VertexDataCodec.select(data);
        validateRawWindow(offsetInBytes, elementCount, vertexStride, codec);
        NativeBindings.setVertexBufferData(
                this, offsetInBytes, codec.encode(data, startIndex, elementCount),
                elementCount, vertexStride);
    }

    public final <T> void GetData(T[] data) {
        Objects.requireNonNull(data, "data");
        GetData(data, 0, data.length);
    }

    public final <T> void GetData(T[] data, int startIndex, int elementCount) {
        GetData(0, data, startIndex, elementCount, vertexDeclaration.getVertexStride());
    }

    public final <T> void GetData(
            int offsetInBytes,
            T[] data,
            int startIndex,
            int elementCount,
            int vertexStride) {
        ensureNotDisposed();
        validateArrayWindow(Objects.requireNonNull(data, "data").length, startIndex, elementCount);
        VertexDataCodec codec = VertexDataCodec.select(data);
        validateRawWindow(offsetInBytes, elementCount, vertexStride, codec);
        byte[] payload = NativeBindings.getVertexBufferData(
                this, offsetInBytes, elementCount, vertexStride);
        T[] snapshot = codec.decode(payload, elementCount);
        System.arraycopy(snapshot, 0, data, startIndex, elementCount);
    }

    public final BufferUsage getBufferUsage() {
        ensureNotDisposed();
        return bufferUsage;
    }

    public final int getVertexCount() {
        ensureNotDisposed();
        return vertexCount;
    }

    public final VertexDeclaration getVertexDeclaration() {
        ensureNotDisposed();
        return vertexDeclaration;
    }

    @Override
    protected void Dispose(boolean arg0) {
        if (arg0 && !getIsDisposed()) {
            NativeBindings.closeGraphicsResource(this);
        }
        super.Dispose(arg0);
    }

    private void validateRawWindow(
            int offsetInBytes,
            int elementCount,
            int stride,
            VertexDataCodec codec) {
        if (offsetInBytes < 0) {
            throw new IllegalArgumentException("offsetInBytes must not be negative");
        }
        if (stride != codec.stride() || stride != vertexDeclaration.getVertexStride()) {
            throw new UnsupportedOperationException(
                    "CNA's raw buffer ABI cannot represent XNA scatter/gather vertex strides");
        }
        if (offsetInBytes % stride != 0
                || (long)offsetInBytes + (long)elementCount * stride
                > (long)vertexCount * vertexDeclaration.getVertexStride()) {
            throw new IndexOutOfBoundsException("Vertex transfer window is outside the buffer");
        }
    }

    private static void validateArrayWindow(int length, int startIndex, int elementCount) {
        if (startIndex < 0 || startIndex > length) {
            throw new IndexOutOfBoundsException("Vertex data start index is outside the array");
        }
        if (elementCount <= 0 || elementCount > length - startIndex) {
            throw new IndexOutOfBoundsException("Vertex data window is outside the array");
        }
    }
}
