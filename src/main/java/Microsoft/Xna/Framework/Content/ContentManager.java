package Microsoft.Xna.Framework.Content;

import Microsoft.Xna.Framework.ServiceProvider;
import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.IGraphicsDeviceService;
import Microsoft.Xna.Framework.Graphics.SpriteFont;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import org.openeggbert.cna.internal.NativeBindings;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/** XNA content facade with CNA-backed Texture2D and SpriteFont loading. */
public class ContentManager implements AutoCloseable {

    private final Map<String, Object> assets = new LinkedHashMap<>();
    private final ServiceProvider serviceProvider;
    private String rootDirectory = "";
    private boolean closed;

    public ContentManager(ServiceProvider serviceProvider) {
        this.serviceProvider = Objects.requireNonNull(serviceProvider, "serviceProvider");
    }

    public ContentManager(ServiceProvider serviceProvider, String rootDirectory) {
        this(serviceProvider);
        this.rootDirectory = Objects.requireNonNull(rootDirectory, "rootDirectory");
    }

    public final String getRootDirectory() {
        ensureOpen();
        return rootDirectory;
    }

    public final void setRootDirectory(String value) {
        ensureOpen();
        Objects.requireNonNull(value, "value");
        if (!assets.isEmpty()) {
            throw new IllegalStateException(
                    "The content root directory cannot be changed after an asset has been loaded");
        }
        rootDirectory = value;
    }

    public final ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    /** Normative Java projection of XNA {@code Load<T>(String)} using a class token. */
    public <T> T Load(Class<T> assetType, String assetName) {
        ensureOpen();
        Objects.requireNonNull(assetType, "assetType");
        Objects.requireNonNull(assetName, "assetName");
        if (assetName.isEmpty()) {
            throw new IllegalArgumentException("assetName must not be empty");
        }
        String assetKey = assetKey(assetName);
        Object cached = assets.get(assetKey);
        if (cached != null) {
            if (!assetType.isInstance(cached)) {
                throw new ContentLoadException("Asset was cached with a different Java type: " + assetName);
            }
            return assetType.cast(cached);
        }
        Object loaded;
        try {
            GraphicsDevice graphicsDevice = graphicsDevice();
            if (assetType == Texture2D.class) {
                loaded = NativeBindings.loadContentTexture2D(
                        this, graphicsDevice, rootDirectory, assetName);
            } else if (assetType == SpriteFont.class) {
                loaded = NativeBindings.loadContentSpriteFont(
                        this, graphicsDevice, rootDirectory, assetName);
            } else {
                throw new ContentLoadException(
                        "No CNA-Java content reader is mapped for " + assetType.getName());
            }
        } catch (ContentLoadException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new ContentLoadException(
                    "Could not load content asset " + assetName + ": "
                            + exception.getMessage(), exception);
        }
        assets.put(assetKey, loaded);
        return assetType.cast(loaded);
    }

    /** Normative readable-stream projection of XNA's protected virtual OpenStream member. */
    protected InputStream OpenStream(String assetName) {
        String requestedName = assetName == null ? "" : assetName;
        Path path;
        try {
            String fileName = cleanAssetName(requestedName + ".xnb");
            path = Path.of(rootDirectory).resolve(fileName).normalize();
            return Files.newInputStream(path);
        } catch (NoSuchFileException | NotDirectoryException exception) {
            throw new ContentLoadException(
                    "Could not find content asset '" + requestedName + "'",
                    new UncheckedIOException(exception));
        } catch (IOException exception) {
            throw new ContentLoadException(
                    "Could not open content asset '" + requestedName + "'",
                    new UncheckedIOException(exception));
        } catch (RuntimeException exception) {
            throw new ContentLoadException(
                    "Could not open content asset '" + requestedName + "'", exception);
        }
    }

    public void Unload() {
        ensureOpen();
        RuntimeException failure = null;
        for (Object asset : assets.values()) {
            try {
                if (asset instanceof SpriteFont spriteFont) {
                    NativeBindings.closeSpriteFont(spriteFont);
                } else if (asset instanceof AutoCloseable closeable) {
                    closeable.close();
                }
            } catch (Exception exception) {
                RuntimeException cause = exception instanceof RuntimeException runtime
                        ? runtime : new RuntimeException(exception);
                if (failure == null) {
                    failure = cause;
                } else {
                    failure.addSuppressed(cause);
                }
            }
        }
        if (failure == null) {
            assets.clear();
            try {
                NativeBindings.unloadContentManager(this);
            } catch (RuntimeException exception) {
                failure = exception;
            }
        }
        if (failure != null) {
            throw new ContentLoadException("Failed to unload content", failure);
        }
    }

    @Override
    public final void close() {
        if (closed) {
            return;
        }
        Dispose(true);
    }

    protected void Dispose(boolean disposing) {
        if (closed) {
            return;
        }
        if (disposing) {
            Unload();
            NativeBindings.closeContentManager(this);
        }
        closed = true;
    }

    private GraphicsDevice graphicsDevice() {
        Object service = serviceProvider.GetService(IGraphicsDeviceService.class);
        if (!(service instanceof IGraphicsDeviceService graphicsService)) {
            throw new ContentLoadException(
                    "ContentManager requires an IGraphicsDeviceService for native assets");
        }
        GraphicsDevice graphicsDevice = graphicsService.getGraphicsDevice();
        if (graphicsDevice == null) {
            throw new ContentLoadException("The graphics-device service returned no device");
        }
        return graphicsDevice;
    }

    private static String assetKey(String assetName) {
        return cleanAssetName(assetName).toLowerCase(Locale.ROOT);
    }

    private static String cleanAssetName(String assetName) {
        return Path.of(assetName.replace('\\', '/')).normalize().toString().replace('\\', '/');
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException("ContentManager is already closed");
        }
    }
}
