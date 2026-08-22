package Microsoft.Xna.Framework.Graphics;

import Microsoft.Xna.Framework.Color;
import Microsoft.Xna.Framework.Game;
import org.openeggbert.cna.internal.NativeBindings;

import java.util.Objects;

/** Strict XNA graphics facade with no native handle in its public contract. */
public class GraphicsDevice implements AutoCloseable {

    private final Game game;
    private boolean closed;

    public GraphicsDevice(Game game) {
        this.game = Objects.requireNonNull(game, "game");
    }

    /** Clears the current CNA render target, snapshotting the mutable color value. */
    public void Clear(Color color) {
        ensureOpen();
        Color snapshot = new Color(Objects.requireNonNull(color, "color"));
        NativeBindings.clear(game, snapshot.getR(), snapshot.getG(), snapshot.getB(), snapshot.getA());
    }

    @Override
    public void close() {
        closed = true;
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException("GraphicsDevice is already closed");
        }
    }
}
