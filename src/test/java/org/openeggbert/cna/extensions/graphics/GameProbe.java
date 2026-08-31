package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Game;
import Microsoft.Xna.Framework.GameTime;
import Microsoft.Xna.Framework.GraphicsDeviceManager;
import Microsoft.Xna.Framework.Graphics.GraphicsDevice;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Runs one test body inside a real game frame.
 *
 * <p>Most of the engine layer needs a real graphics device, and the C API only lends one out
 * during a lifecycle callback -- so a test for any of those families has to be a game. This is
 * that game, shared by every engine-layer suite that needs one rather than copied into each.
 *
 * <p>A failure inside the body is captured rather than thrown through CNA's native frame, which
 * would cross a JNI boundary with an exception in flight, and is rethrown afterwards.
 */
final class GameProbe extends Game implements AutoCloseable {

    private final Consumer<GameProbe> body;
    private boolean ran;
    private Throwable failure;

    private GameProbe(Consumer<GameProbe> body) {
        this.body = body;
        new GraphicsDeviceManager(this);
    }

    /** Runs one body inside a single frame and rethrows whatever it threw. */
    static void run(Consumer<GameProbe> body) {
        try (GameProbe probe = new GameProbe(body)) {
            probe.RunOneFrame();
            if (probe.failure != null) {
                if (probe.failure instanceof RuntimeException runtime) {
                    throw runtime;
                }
                if (probe.failure instanceof Error error) {
                    throw error;
                }
                throw new IllegalStateException(probe.failure);
            }
            assertTrue(probe.ran, "the probe must have run");
        }
    }

    /** The game's own device, borrowed for the frame. */
    GraphicsDevice device() {
        return getGraphicsDevice();
    }

    @Override
    protected void Update(GameTime gameTime) {
        super.Update(gameTime);
        if (ran) {
            return;
        }
        ran = true;
        try {
            body.accept(this);
        } catch (Throwable exception) {
            failure = exception;
        }
    }
}
