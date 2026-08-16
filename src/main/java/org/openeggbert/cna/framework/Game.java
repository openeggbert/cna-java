package org.openeggbert.cna.framework;

/**
 * Base class for a CNA game. The lifecycle shape is present in the scaffold;
 * native execution starts after CNA publishes its stable ABI.
 */
public abstract class Game implements AutoCloseable {

    private boolean closed;

    /** Runs the native CNA game loop. */
    public final void run() {
        ensureOpen();
        throw new CnaException("CNA native C ABI is not available yet");
    }

    /** Requests normal game-loop termination. */
    public final void exit() {
        ensureOpen();
    }

    /** Called once before content is loaded. */
    protected void initialize() {
    }

    /** Called once to load game content. */
    protected void loadContent() {
    }

    /** Called for every logical game update. */
    protected void update(GameTime gameTime) {
    }

    /** Called for every rendered frame. */
    protected void draw(GameTime gameTime) {
    }

    /** Called during shutdown to release game-owned content. */
    protected void unloadContent() {
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
