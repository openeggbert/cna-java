package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

/**
 * A block of GPU memory a compute shader reads and writes, and the CPU can fill and read back.
 *
 * <p>A CNA extension. XNA has no shape for this at all: its only GPU-side buffers are vertex and
 * index buffers, which the fixed pipeline consumes and no shader may write. A storage buffer is
 * the other half of {@link ComputeShader} -- the shader is the program and this is the data.
 *
 * <p><strong>Two shapes, and the difference is what CNA will check for you.</strong>
 * {@link #ofBytes} is a size in bytes and nothing else. {@link #ofElements} additionally
 * remembers a count and an element size, and {@link #setElements}/{@link #getElements} then
 * refuse a transfer whose element size disagrees with the one the buffer was created for, rather
 * than silently reinterpreting the bytes. Prefer the second where the data really is an array of
 * something.
 *
 * <p><strong>Bytes are the currency, and they are little-endian on every GPU CNA targets.</strong>
 * Java has no way to hand a struct to C, so the transfer routes take {@code byte[]}; the
 * {@code putInt}/{@code putFloat} helpers on {@link ByteBuffer} are the ordinary way to fill one,
 * and {@link #allocate} returns a buffer already ordered the way the GPU reads.
 *
 * <p><strong>Binding does not retain.</strong> {@link ComputeShader#bindStorageBuffer} borrows:
 * this object must outlive every dispatch that reads it, and closing it while a shader is still
 * bound to it is the caller's mistake to avoid. CNA states that and does not enforce it.
 *
 * <p><strong>The renderer decides whether this can exist.</strong> Creation needs
 * {@link GraphicsCapability#ComputeShaders}; without it CNA refuses and this raises
 * {@link ExtensionNotSupportedException}. Ask {@link RendererCapabilities#supports} first.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class StorageBuffer implements AutoCloseable {

    private final long handle;
    private boolean closed;

    private StorageBuffer(long handle) {
        this.handle = handle;
    }

    /**
     * Returns a little-endian byte buffer of the requested size.
     *
     * <p>A convenience with one real purpose: every GPU CNA targets reads little-endian words,
     * and {@link ByteBuffer}'s default order is big-endian, so a buffer filled with
     * {@link ByteBuffer#putInt(int)} at the default order reaches the shader byte-reversed. This
     * is the ordering mistake that produces a shader reading plausible-looking nonsense.
     *
     * @param byteSize the size in bytes
     * @return an array-backed buffer, already little-endian
     */
    public static ByteBuffer allocate(int byteSize) {
        return ByteBuffer.allocate(byteSize).order(ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * Creates a buffer of a size in bytes.
     *
     * @param graphicsDevice the device to allocate on
     * @param byteSize the size in bytes; must be positive
     * @return the buffer, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer, or the renderer
     *         has no compute shaders
     */
    public static StorageBuffer ofBytes(GraphicsDevice graphicsDevice, long byteSize) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] buffer = new long[1];
        GraphicsExtension.check("StorageBuffer.ofBytes", NativeEngineLayerRoutes.storageBufferCreate(
                NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), byteSize, buffer));
        return new StorageBuffer(buffer[0]);
    }

    /**
     * Creates a buffer of a count of fixed-size elements.
     *
     * <p>The buffer remembers both numbers, which is what lets {@link #setElements} refuse a
     * transfer that does not match rather than reinterpreting the bytes.
     *
     * @param graphicsDevice the device to allocate on
     * @param elementCount how many elements it holds
     * @param elementByteSize the size of one element in bytes; must not be zero
     * @return the buffer, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer, or the renderer
     *         has no compute shaders
     */
    public static StorageBuffer ofElements(GraphicsDevice graphicsDevice, long elementCount,
            long elementByteSize) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] buffer = new long[1];
        GraphicsExtension.check("StorageBuffer.ofElements",
                NativeEngineLayerRoutes.storageBufferCreateTyped(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), elementCount,
                        elementByteSize, buffer));
        return new StorageBuffer(buffer[0]);
    }

    /**
     * Returns the buffer's size in bytes.
     *
     * @return the size
     */
    public long getByteSize() {
        long[] size = new long[1];
        GraphicsExtension.check("StorageBuffer.getByteSize",
                NativeEngineLayerRoutes.storageBufferGetByteSize(open(), size));
        return size[0];
    }

    /**
     * Returns how many elements the buffer was created to hold.
     *
     * @return the count, or zero for a buffer created by byte size
     */
    public long getElementCount() {
        long[] count = new long[1];
        GraphicsExtension.check("StorageBuffer.getElementCount",
                NativeEngineLayerRoutes.storageBufferGetElementCount(open(), count));
        return count[0];
    }

    /**
     * Returns the element size the buffer was created with.
     *
     * @return the size in bytes, or zero for a buffer created by byte size
     */
    public long getElementByteSize() {
        long[] size = new long[1];
        GraphicsExtension.check("StorageBuffer.getElementByteSize",
                NativeEngineLayerRoutes.storageBufferGetElementByteSize(open(), size));
        return size[0];
    }

    /**
     * Uploads bytes into the buffer.
     *
     * @param data the bytes to upload; every byte of the array is sent
     * @throws IllegalArgumentException when the array is larger than the buffer
     */
    public void setBytes(byte[] data) {
        Objects.requireNonNull(data, "data");
        GraphicsExtension.check("StorageBuffer.setBytes",
                NativeEngineLayerRoutes.storageBufferSetBytes(open(), data, data.length));
    }

    /**
     * Reads bytes back from the buffer.
     *
     * @param destination filled with as many bytes as it holds
     * @throws IllegalArgumentException when the array is larger than the buffer
     */
    public void getBytes(byte[] destination) {
        Objects.requireNonNull(destination, "destination");
        GraphicsExtension.check("StorageBuffer.getBytes", NativeEngineLayerRoutes
                .storageBufferGetBytes(open(), destination, destination.length));
    }

    /**
     * Uploads a count of elements.
     *
     * <p>CNA refuses a count larger than the buffer holds, and an element size that disagrees
     * with the one it was created with, rather than reinterpreting the bytes.
     *
     * @param data the packed elements, {@code elementCount * elementByteSize} bytes of them
     * @param elementCount how many elements to upload
     * @param elementByteSize the size of one element; must equal the creation value
     * @throws IllegalArgumentException when the array is too small for the extent, when the count
     *         exceeds the buffer, or when the element size disagrees
     */
    public void setElements(byte[] data, long elementCount, long elementByteSize) {
        Objects.requireNonNull(data, "data");
        GraphicsExtension.check("StorageBuffer.setElements", NativeEngineLayerRoutes
                .storageBufferSetElements(open(), data, elementCount, elementByteSize));
    }

    /**
     * Reads the buffer's whole element range back.
     *
     * @param destination filled with {@code elementCount * elementByteSize} bytes
     * @param elementCount must equal the buffer's element count
     * @param elementByteSize must equal the buffer's element size
     * @throws IllegalArgumentException when the array is too small for the extent, or either
     *         number disagrees with the buffer
     */
    public void getElements(byte[] destination, long elementCount, long elementByteSize) {
        Objects.requireNonNull(destination, "destination");
        GraphicsExtension.check("StorageBuffer.getElements", NativeEngineLayerRoutes
                .storageBufferGetElements(open(), destination, elementCount, elementByteSize));
    }

    /** Releases the buffer's GPU memory. Closing twice is a no-op. */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        GraphicsExtension.check("StorageBuffer.close",
                NativeEngineLayerRoutes.storageBufferDestroy(handle));
    }

    /** The native handle, for the routes that bind this buffer to a shader. */
    long nativeHandle() {
        return open();
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This StorageBuffer is closed");
            }
        }
        return handle;
    }
}
