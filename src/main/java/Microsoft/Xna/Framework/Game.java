package Microsoft.Xna.Framework;

import Microsoft.Xna.Framework.Content.ContentManager;
import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.NativeGameHandle;

import java.time.Duration;

/** XNA 4.0 game lifecycle facade backed by CNA's stable C ABI through JNI. */
public class Game implements AutoCloseable {

    private static final long DEFAULT_TARGET_TICKS = 166_667L;

    private final ContentManager content;
    private final GraphicsDevice graphicsDevice;
    private NativeGameHandle nativeGame;
    private GraphicsDeviceManager graphicsManager;
    private boolean closed;
    private boolean hasRun;
    private boolean exitRequested;
    private boolean mouseVisible;

    @SuppressWarnings("this-escape")
    protected Game() {
        content = new ContentManager();
        graphicsDevice = new GraphicsDevice(this);
    }

    /** Runs the native CNA loop until {@link #Exit()} is requested. */
    public final void Run() {
        ensureOpen();
        if (hasRun) {
            throw new IllegalStateException("Game.Run may only be called once");
        }
        hasRun = true;
        nativeGame = NativeBindings.createGame(this, getClass().getSimpleName(), true, DEFAULT_TARGET_TICKS);
        NativeBindings.setMouseVisible(nativeGame, mouseVisible);
        if (exitRequested) {
            NativeBindings.requestExit(nativeGame);
        }
        NativeBindings.run(nativeGame);
    }

    /** Requests normal game-loop termination at CNA's next safe point. */
    public final void Exit() {
        ensureOpen();
        exitRequested = true;
        if (nativeGame != null && !nativeGame.isClosed()) {
            NativeBindings.requestExit(nativeGame);
        }
    }

    public final ContentManager getContent() {
        return content;
    }

    public final GraphicsDevice getGraphicsDevice() {
        return graphicsDevice;
    }

    public final boolean getIsMouseVisible() {
        if (nativeGame != null && !nativeGame.isClosed()) {
            mouseVisible = NativeBindings.getMouseVisible(nativeGame);
        }
        return mouseVisible;
    }

    public final void setIsMouseVisible(boolean visible) {
        ensureOpen();
        mouseVisible = visible;
        if (nativeGame != null && !nativeGame.isClosed()) {
            NativeBindings.setMouseVisible(nativeGame, visible);
        }
    }

    protected void Initialize() {
    }

    protected void LoadContent() {
    }

    protected void BeginRun() {
    }

    protected void Update(GameTime gameTime) {
    }

    protected boolean BeginDraw() {
        return true;
    }

    protected void Draw(GameTime gameTime) {
    }

    protected void EndDraw() {
    }

    protected void EndRun() {
    }

    protected void UnloadContent() {
    }

    /** Releases content before the owned native game. Repeated calls are harmless. */
    @Override
    public final void close() {
        if (closed) {
            return;
        }
        closed = true;
        if (nativeGame != null) {
            nativeGame.close();
        }
        content.close();
        if (graphicsManager != null) {
            graphicsManager.closeFromGame();
        }
    }

    final void attachGraphicsManager(GraphicsDeviceManager manager) {
        ensureOpen();
        if (graphicsManager != null && graphicsManager != manager) {
            throw new IllegalStateException("A Game already has a GraphicsDeviceManager");
        }
        graphicsManager = manager;
    }

    @SuppressWarnings("unused")
    private void nativeInitialize() {
        Initialize();
    }

    @SuppressWarnings("unused")
    private void nativeLoadContent() {
        LoadContent();
    }

    @SuppressWarnings("unused")
    private void nativeBeginRun() {
        BeginRun();
    }

    @SuppressWarnings("unused")
    private void nativeUpdate(long totalTicks, long elapsedTicks, boolean slowly) {
        Update(gameTime(totalTicks, elapsedTicks, slowly));
    }

    @SuppressWarnings("unused")
    private boolean nativeBeginDraw() {
        return BeginDraw();
    }

    @SuppressWarnings("unused")
    private void nativeDraw(long totalTicks, long elapsedTicks, boolean slowly) {
        Draw(gameTime(totalTicks, elapsedTicks, slowly));
    }

    @SuppressWarnings("unused")
    private void nativeEndDraw() {
        EndDraw();
    }

    @SuppressWarnings("unused")
    private void nativeEndRun() {
        EndRun();
    }

    @SuppressWarnings("unused")
    private void nativeUnloadContent() {
        UnloadContent();
    }

    @SuppressWarnings("unused")
    private void nativeExiting() {
        exitRequested = true;
    }

    private static GameTime gameTime(long totalTicks, long elapsedTicks, boolean slowly) {
        return new GameTime(durationFromTicks(totalTicks), durationFromTicks(elapsedTicks), slowly);
    }

    private static Duration durationFromTicks(long ticks) {
        long seconds = ticks / 10_000_000L;
        long remainingTicks = ticks % 10_000_000L;
        return Duration.ofSeconds(seconds, remainingTicks * 100L);
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException("Game is already closed");
        }
    }
}
