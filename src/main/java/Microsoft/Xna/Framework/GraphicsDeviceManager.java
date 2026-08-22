package Microsoft.Xna.Framework;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;

import java.util.Objects;

/** Minimal XNA graphics-device manager facade for the first CNA lifecycle slice. */
@SuppressWarnings("this-escape")
public class GraphicsDeviceManager implements AutoCloseable {

    private final Game game;
    private boolean closed;

    public GraphicsDeviceManager(Game game) {
        this.game = Objects.requireNonNull(game, "game");
        game.attachGraphicsManager(this);
    }

    public final GraphicsDevice getGraphicsDevice() {
        ensureOpen();
        return game.getGraphicsDevice();
    }

    @Override
    public void close() {
        closed = true;
    }

    void closeFromGame() {
        closed = true;
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException("GraphicsDeviceManager is already closed");
        }
    }
}
