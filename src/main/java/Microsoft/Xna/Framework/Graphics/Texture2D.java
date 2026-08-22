package Microsoft.Xna.Framework.Graphics;

import Microsoft.Xna.Framework.Color;
import Microsoft.Xna.Framework.Content.ContentLoadException;
import Microsoft.Xna.Framework.Rectangle;
import org.openeggbert.cna.internal.NativeBindings;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Objects;

/** Device-owned two-dimensional texture backed by CNA's stable C resource ABI. */
@SuppressWarnings("this-escape")
public class Texture2D extends Texture {

    private int width;
    private int height;

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
        validateArrayWindow(data.length, startIndex, elementCount);
        requireWholeColorTransfer(level, rect, elementCount);
        if (!(data instanceof Color[] colors)) {
            throw new UnsupportedOperationException(
                    "This CNA-Java slice currently supports Texture2D Color[] transfers only");
        }
        NativeBindings.setTexture2DData(
                this, Arrays.copyOfRange(colors, startIndex, startIndex + elementCount));
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
        validateArrayWindow(data.length, startIndex, elementCount);
        requireWholeColorTransfer(level, rect, elementCount);
        if (!(data instanceof Color[] colors)) {
            throw new UnsupportedOperationException(
                    "This CNA-Java slice currently supports Texture2D Color[] transfers only");
        }
        Color[] snapshot = NativeBindings.getTexture2DData(this, elementCount);
        System.arraycopy(snapshot, 0, colors, startIndex, elementCount);
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

    private void initialize(int[] info) {
        if (info.length != 4 || info[0] <= 0 || info[1] <= 0 || info[2] <= 0
                || info[3] < 0 || info[3] >= SurfaceFormat.values().length) {
            NativeBindings.closeGraphicsResource(this);
            throw new IllegalStateException("CNA returned invalid Texture2D metadata");
        }
        width = info[0];
        height = info[1];
        setTextureInfo(SurfaceFormat.values()[info[3]], info[2]);
    }

    private void requireWholeColorTransfer(int level, Rectangle rect, int elementCount) {
        if (level != 0 || rect != null && !rect.equals(getBounds())) {
            throw new UnsupportedOperationException(
                    "This CNA-Java slice currently supports full level-zero transfers only");
        }
        if (elementCount != Math.multiplyExact(width, height)) {
            throw new IllegalArgumentException("Color transfer must contain exactly width * height elements");
        }
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
        if (startIndex < 0 || elementCount < 0 || startIndex > length - elementCount) {
            throw new IndexOutOfBoundsException("Texture data array window is outside the source array");
        }
    }
}
