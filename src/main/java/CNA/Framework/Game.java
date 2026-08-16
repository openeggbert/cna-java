package CNA.Framework;

/** Base class for games targeting CNA's Java-native public API. */
public abstract class Game implements AutoCloseable {

    private boolean closed;

    /** Runs the CNA game loop once the canonical C ABI is available. */
    public final void Run() {
        ensureOpen();
        throw new CnaException("CNA native C ABI is not available yet");
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
