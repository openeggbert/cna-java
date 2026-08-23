package Microsoft.Xna.Framework.Graphics;

import Microsoft.Xna.Framework.Color;
import Microsoft.Xna.Framework.Content.ContentLoadException;
import Microsoft.Xna.Framework.Rectangle;
import org.openeggbert.cna.internal.NativeBindings;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/** Device-owned two-dimensional texture backed by CNA's stable C resource ABI. */
@SuppressWarnings("this-escape")
public class Texture2D extends Texture {

    private int width;
    private int height;

    Texture2D(GraphicsDevice graphicsDevice) {
        super(Objects.requireNonNull(graphicsDevice, "graphicsDevice"));
    }

    public Texture2D(GraphicsDevice graphicsDevice, int width, int height) {
        this(graphicsDevice, width, height, false, SurfaceFormat.Color);
    }

    public Texture2D(
            GraphicsDevice graphicsDevice,
            int width,
            int height,
            boolean mipMap,
            SurfaceFormat format) {
        super(Objects.requireNonNull(graphicsDevice, "graphicsDevice"));
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Texture dimensions must be positive");
        }
        initialize(NativeBindings.createTexture2D(
                this, graphicsDevice, width, height, mipMap,
                Objects.requireNonNull(format, "format").ordinal()));
    }

    private Texture2D(
            GraphicsDevice graphicsDevice,
            byte[] encoded,
            int width,
            int height,
            boolean zoom,
            boolean resize) {
        super(Objects.requireNonNull(graphicsDevice, "graphicsDevice"));
        initialize(NativeBindings.createTexture2DFromEncoded(
                this, graphicsDevice, encoded, width, height, zoom, resize));
    }

    public static Texture2D FromStream(GraphicsDevice graphicsDevice, InputStream stream) {
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        return new Texture2D(graphicsDevice, readEncoded(stream), 0, 0, false, false);
    }

    public static Texture2D FromStream(
            GraphicsDevice graphicsDevice,
            InputStream stream,
            int width,
            int height,
            boolean zoom) {
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Decoded texture dimensions must be positive");
        }
        return new Texture2D(graphicsDevice, readEncoded(stream), width, height, zoom, true);
    }

    public final <T> void SetData(T[] data) {
        Objects.requireNonNull(data, "data");
        SetData(0, null, data, 0, data.length);
    }

    public final <T> void SetData(T[] data, int startIndex, int elementCount) {
        SetData(0, null, data, startIndex, elementCount);
    }

    public final <T> void SetData(
            int level,
            Rectangle rect,
            T[] data,
            int startIndex,
            int elementCount) {
        ensureNotDisposed();
        Objects.requireNonNull(data, "data");
        Rectangle region = rect == null ? null : new Rectangle(rect);
        TextureDataCodec codec = validateTransfer(
                level, region, data, startIndex, elementCount);
        byte[] snapshot = codec.encode(data, startIndex, elementCount);
        NativeBindings.setTexture2DData(
                this, codec.dataType(), level, region,
                startIndex, elementCount, snapshot);
    }

    public final <T> void GetData(T[] data) {
        Objects.requireNonNull(data, "data");
        GetData(0, null, data, 0, data.length);
    }

    public final <T> void GetData(T[] data, int startIndex, int elementCount) {
        GetData(0, null, data, startIndex, elementCount);
    }

    public final <T> void GetData(
            int level,
            Rectangle rect,
            T[] data,
            int startIndex,
            int elementCount) {
        ensureNotDisposed();
        Objects.requireNonNull(data, "data");
        Rectangle region = rect == null ? null : new Rectangle(rect);
        TextureDataCodec codec = validateTransfer(
                level, region, data, startIndex, elementCount);
        int payloadBytes = Math.multiplyExact(data.length, codec.elementSize());
        byte[] snapshot = NativeBindings.getTexture2DData(
                this, codec.dataType(), level, region,
                startIndex, elementCount, payloadBytes);
        codec.decodeInto(snapshot, data, startIndex, elementCount);
    }

    public final void SaveAsPng(OutputStream stream, int width, int height) {
        saveAs(stream, 0, width, height, "SaveAsPng");
    }

    public final void SaveAsJpeg(OutputStream stream, int width, int height) {
        saveAs(stream, 1, width, height, "SaveAsJpeg");
    }

    public final Rectangle getBounds() {
        ensureNotDisposed();
        return new Rectangle(0, 0, width, height);
    }

    public final int getHeight() {
        ensureNotDisposed();
        return height;
    }

    public final int getWidth() {
        ensureNotDisposed();
        return width;
    }

    @Override
    protected void Dispose(boolean arg0) {
        if (arg0 && !getIsDisposed()) {
            NativeBindings.closeGraphicsResource(this);
        }
        super.Dispose(arg0);
    }

    final void initialize(int[] info) {
        if (info.length != 4 || info[0] <= 0 || info[1] <= 0 || info[2] <= 0
                || info[3] < 0 || info[3] >= SurfaceFormat.values().length) {
            NativeBindings.closeGraphicsResource(this);
            throw new IllegalStateException("CNA returned invalid Texture2D metadata");
        }
        width = info[0];
        height = info[1];
        setTextureInfo(SurfaceFormat.values()[info[3]], info[2]);
    }

    private <T> TextureDataCodec validateTransfer(
            int level,
            Rectangle rect,
            T[] data,
            int startIndex,
            int elementCount) {
        if (data.length == 0) {
            throw new IllegalArgumentException("Texture data array must not be empty");
        }
        if (level < 0 || level >= getLevelCount()) {
            throw new IllegalStateException("Texture mip level is outside the allocated chain");
        }
        validateArrayWindow(data.length, startIndex, elementCount);

        int levelWidth = Math.max(1, width >> level);
        int levelHeight = Math.max(1, height >> level);
        int transferWidth = levelWidth;
        int transferHeight = levelHeight;
        if (rect != null) {
            if (rect.X < 0 || rect.Y < 0 || rect.Width <= 0 || rect.Height <= 0
                    || (long)rect.X + rect.Width > levelWidth
                    || (long)rect.Y + rect.Height > levelHeight) {
                throw new IllegalArgumentException("Texture rectangle is outside the selected mip level");
            }
            transferWidth = rect.Width;
            transferHeight = rect.Height;
        }

        TextureDataCodec codec = TextureDataCodec.select(
                data.getClass().getComponentType(), getFormat());
        long expected = expectedElementCount(getFormat(), transferWidth, transferHeight);
        if (elementCount != expected) {
            throw new IllegalArgumentException(
                    "Texture transfer element count must be exactly " + expected);
        }
        return codec;
    }

    private static long expectedElementCount(SurfaceFormat format, int width, int height) {
        return switch (format) {
            case Dxt1 -> (long)((width + 3) >> 2) * ((height + 3) >> 2) * 8L;
            case Dxt3, Dxt5 -> (long)((width + 3) >> 2) * ((height + 3) >> 2) * 16L;
            default -> (long)width * height;
        };
    }

    private void saveAs(OutputStream stream, int format, int width, int height, String operation) {
        ensureNotDisposed();
        Objects.requireNonNull(stream, "stream");
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Encoded texture dimensions must be positive");
        }
        try {
            stream.write(NativeBindings.encodeTexture2D(this, format, width, height));
        } catch (IOException exception) {
            throw new ContentLoadException(
                    operation + " could not write the Java output stream", new RuntimeException(exception));
        }
    }

    private static byte[] readEncoded(InputStream stream) {
        try {
            byte[] encoded = Objects.requireNonNull(stream, "stream").readAllBytes();
            if (encoded.length == 0) {
                throw new IllegalArgumentException("Encoded image stream must not be empty");
            }
            return encoded;
        } catch (IOException exception) {
            throw new ContentLoadException(
                    "Texture2D.FromStream could not read the Java input stream",
                    new RuntimeException(exception));
        }
    }

    private static void validateArrayWindow(int length, int startIndex, int elementCount) {
        if (startIndex < 0 || startIndex > length) {
            throw new IndexOutOfBoundsException("Texture data start index is outside the array");
        }
        if (elementCount <= 0 || elementCount > length - startIndex) {
            throw new IndexOutOfBoundsException("Texture data array window is outside the source array");
        }
    }
}
