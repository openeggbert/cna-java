package Microsoft.Xna.Framework.Content;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/** XNA content facade; XNB decoding is intentionally not yet implemented. */
public class ContentManager implements AutoCloseable {

    private final Map<String, Object> assets = new LinkedHashMap<>();
    private String rootDirectory = "";
    private boolean closed;

    public final String getRootDirectory() {
        ensureOpen();
        return rootDirectory;
    }

    public final void setRootDirectory(String value) {
        ensureOpen();
        rootDirectory = Objects.requireNonNull(value, "value");
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
                    throw new ContentLoadException("Failed to unload content", exception);
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
        Unload();
        closed = true;
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException("ContentManager is already closed");
        }
    }
}
