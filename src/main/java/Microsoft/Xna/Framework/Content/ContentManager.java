package Microsoft.Xna.Framework.Content;

import Microsoft.Xna.Framework.ServiceProvider;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/** XNA content facade; XNB decoding is intentionally not yet implemented. */
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
        rootDirectory = Objects.requireNonNull(value, "value");
    }

    public final ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    /** Normative Java projection of XNA {@code Load<T>(String)} using a class token. */
    public <T> T Load(Class<T> assetType, String assetName) {
        ensureOpen();
        Objects.requireNonNull(assetType, "assetType");
        if (assetName == null || assetName.isBlank()) {
            throw new IllegalArgumentException("assetName must not be blank");
        }
        Object cached = assets.get(assetName);
        if (cached != null) {
            if (!assetType.isInstance(cached)) {
                throw new ContentLoadException("Asset was cached with a different Java type: " + assetName);
            }
            return assetType.cast(cached);
        }
        throw new ContentLoadException("XNB loading is not implemented yet: " + assetName);
    }

    public void Unload() {
        ensureOpen();
        for (Object asset : assets.values()) {
            if (asset instanceof AutoCloseable closeable) {
                try {
                    closeable.close();
                } catch (Exception exception) {
                    RuntimeException cause = exception instanceof RuntimeException runtime
                            ? runtime : new RuntimeException(exception);
                    throw new ContentLoadException("Failed to unload content", cause);
                }
            }
        }
        assets.clear();
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
        }
        closed = true;
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException("ContentManager is already closed");
        }
    }
}
