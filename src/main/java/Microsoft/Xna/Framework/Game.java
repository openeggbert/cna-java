package Microsoft.Xna.Framework;

import org.openeggbert.cna.internal.NativeBindings;

/** XNA 4.0-compatible game lifecycle facade over the native CNA engine. */
public abstract class Game implements AutoCloseable {

    private boolean closed;

    /** Runs the CNA-backed XNA game loop. */
    public final void Run() {
        ensureOpen();
        NativeBindings.requireAvailable();
    }

    /** Requests normal game-loop termination. */
    public final void Exit() {
        ensureOpen();
    }

    protected void Initialize() {
    }

    protected void LoadContent() {
    }

    protected void Update(GameTime gameTime) {
    }

    protected void Draw(GameTime gameTime) {
    }

    protected void UnloadContent() {
    }

    /** Releases the future native game handle; repeated calls are harmless. */
    @Override
    public final void close() {
        closed = true;
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException("Game is already closed");
        }
    }
}
