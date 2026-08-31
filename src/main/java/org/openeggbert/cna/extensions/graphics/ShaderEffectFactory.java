package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.Effect;
import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import org.openeggbert.cna.internal.FacadeFactory;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * A cache of shader effects, compiled once and looked up by name.
 *
 * <p>A CNA extension. XNA has no way to build an effect from shader source at all -- an
 * {@code Effect} comes from a compiled {@code .xnb}, produced by a content pipeline that no longer
 * exists on most machines -- so a game that wants its own shader has nowhere to put one. This is
 * that place, and the caching is the point: a post-process pass that acquires its effect every
 * frame compiles it once.
 *
 * <p><strong>The name is the key, not the source.</strong> Two names with identical source compile
 * twice, and a second acquire of one name does not recompile, whatever source is passed with it.
 * That is CNA's behaviour, measured rather than assumed, and it means a game changing a shader has
 * to change the name or {@link #clear()} the cache.
 *
 * <p><strong>Ownership.</strong> The factory is OWNED and released by {@link #close()}. Every
 * effect it hands out is a BORROWED view of a cached effect the factory keeps: disposing the view
 * gives the borrow back and does not destroy the cached effect. The factory refuses to clear or
 * close while any view is outstanding, so disposing them is not housekeeping -- it is what makes
 * closing possible at all, and the refusal is recoverable rather than fatal.
 */
public final class ShaderEffectFactory implements AutoCloseable {

    private final GraphicsDevice graphicsDevice;
    private long handle;
    private boolean closed;

    private ShaderEffectFactory(GraphicsDevice graphicsDevice, long handle) {
        this.graphicsDevice = graphicsDevice;
        this.handle = handle;
    }

    /**
     * Creates a factory for one device.
     *
     * @param graphicsDevice the device every cached effect is compiled on
     * @return the factory, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static ShaderEffectFactory create(GraphicsDevice graphicsDevice) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] created = new long[1];
        GraphicsExtension.check("ShaderEffectFactory.create",
                NativeEngineLayerRoutes.shaderEffectFactoryCreate(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), created));
        return new ShaderEffectFactory(graphicsDevice, created[0]);
    }

    /**
     * Returns the named effect, compiling it on the first request.
     *
     * <p>Each call hands back a fresh view of the same cached effect, so two acquires of one name
     * are two objects that must each be disposed -- and neither disposal destroys the effect the
     * factory holds.
     *
     * @param name a non-empty stable cache key
     * @param vertexSource the vertex shader source, used only on the first request for this name
     * @param fragmentSource the fragment shader source, likewise
     * @return the effect, which the caller disposes
     * @throws IllegalArgumentException for an empty name or source CNA refuses
     */
    public Effect acquire(String name, String vertexSource, String fragmentSource) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(vertexSource, "vertexSource");
        Objects.requireNonNull(fragmentSource, "fragmentSource");
        long[] acquired = new long[1];
        GraphicsExtension.check("ShaderEffectFactory.acquire",
                NativeEngineLayerRoutes.shaderEffectFactoryAcquire(alive(), utf8(name),
                        utf8(vertexSource), utf8(fragmentSource), acquired));
        return FacadeFactory.createBorrowedEffect(graphicsDevice, acquired[0]);
    }

    /**
     * Reports whether a name is in the cache.
     *
     * @param name the key to look for
     * @return whether it is cached
     */
    public boolean contains(String name) {
        Objects.requireNonNull(name, "name");
        boolean[] contains = new boolean[1];
        GraphicsExtension.check("ShaderEffectFactory.contains",
                NativeEngineLayerRoutes.shaderEffectFactoryContains(alive(), utf8(name),
                        contains));
        return contains[0];
    }

    /**
     * Returns how many distinct shaders this factory has compiled since it was created.
     *
     * <p>Not the cache size: {@link #clear()} empties the cache and leaves this number alone, so
     * it is a measure of work done rather than of what is held. That is what makes it worth
     * exposing -- a game whose frame time is fine but whose compile count keeps climbing is
     * recompiling something every frame, which nothing else here would reveal.
     *
     * @return the compile count
     */
    public long getCompileCount() {
        long[] count = new long[1];
        GraphicsExtension.check("ShaderEffectFactory.getCompileCount",
                NativeEngineLayerRoutes.shaderEffectFactoryGetCompileCount(alive(), count));
        return count[0];
    }

    /**
     * Releases every cached effect.
     *
     * @throws IllegalStateException while any acquired effect is undisposed; the cache is
     *         unchanged and the call can be repeated once they are
     */
    public void clear() {
        GraphicsExtension.check("ShaderEffectFactory.clear",
                NativeEngineLayerRoutes.shaderEffectFactoryClear(alive()));
    }

    /**
     * Releases the factory and every effect it cached.
     *
     * <p>Marked closed only after CNA agrees. A close refused because a view is still out leaves a
     * usable factory -- dispose the views and close again -- rather than an unusable one that also
     * leaked, which is the difference between a recoverable mistake and a lost handle.
     *
     * @throws IllegalStateException while any acquired effect is undisposed
     */
    @Override
    public void close() {
        if (closed) {
            return;
        }
        GraphicsExtension.check("ShaderEffectFactory.close",
                NativeEngineLayerRoutes.shaderEffectFactoryDestroy(handle));
        closed = true;
        handle = 0L;
    }

    private static byte[] utf8(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }

    private long alive() {
        if (closed) {
            throw new IllegalStateException("ShaderEffectFactory is closed");
        }
        return handle;
    }
}
