package org.openeggbert.cna.internal;

import Microsoft.Xna.Framework.DisplayOrientation;
import Microsoft.Xna.Framework.Game;
import Microsoft.Xna.Framework.GameComponent;
import Microsoft.Xna.Framework.GameTime;
import Microsoft.Xna.Framework.Rectangle;
import Microsoft.Xna.Framework.Input.Keyboard;
import Microsoft.Xna.Framework.Input.KeyboardState;
import Microsoft.Xna.Framework.Input.Keys;
import Microsoft.Xna.Framework.Input.ButtonState;
import Microsoft.Xna.Framework.Input.Mouse;
import Microsoft.Xna.Framework.Input.MouseState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class NativeIntegrationTests {

    @Test
    void LoadsCompatibleCnaAbi() {
        int version = NativeBindings.requireAvailable();
        assertEquals(0, version >>> 16);
        assertTrue(version >= NativeBindings.COMPILED_ABI_VERSION);
    }

    @Test
    void NativeGameRunsThreeFramesAndShutsDownInOrder() {
        ProbeGame game = new ProbeGame(3);
        game.Run();
        game.close();
        assertEquals(3, game.frames);
        assertEquals(List.of("Initialize", "LoadContent", "BeginRun"), game.events.subList(0, 3));
        assertTrue(game.events.indexOf("EndRun") > game.events.lastIndexOf("Draw"));
        assertTrue(game.events.indexOf("UnloadContent") > game.events.indexOf("EndRun"));
    }

    @Test
    void RepeatedNativeCreateRunDestroyDoesNotRetainParentHandles() {
        for (int iteration = 0; iteration < 10; iteration++) {
            try (ProbeGame game = new ProbeGame(1)) {
                game.Run();
                assertEquals(1, game.frames);
            }
        }
    }

    @Test
    void NativeGameSupportsOneFrameTimingAndSuppressedDrawOperations() {
        StepGame game = new StepGame();
        game.setIsFixedTimeStep(false);
        game.setTargetElapsedTime(Duration.ofMillis(20));
        game.setInactiveSleepTime(Duration.ofMillis(7));
        game.RunOneFrame();

        assertFalse(game.getIsFixedTimeStep());
        assertEquals(Duration.ofMillis(20), game.getTargetElapsedTime());
        assertEquals(Duration.ofMillis(7), game.getInactiveSleepTime());
        assertEquals(1, game.updates);
        assertEquals(1, game.draws);
        assertTrue(game.lateComponent.initialized);

        game.SuppressDraw();
        game.Tick();
        game.ResetElapsedTime();
        assertEquals(2, game.updates);
        assertEquals(1, game.draws);
        game.close();
    }

    @Test
    void NativeGameWindowQueriesHeadlessStateAndUsesOpaqueHandle() {
        try (Game game = new Game()) {
            var window = game.getWindow();
            assertSame(window, game.getWindow());
            window.setTitle("CNA Java window probe");
            assertEquals("CNA Java window probe", window.getTitle());
            assertEquals(new Rectangle(), window.getClientBounds());
            assertEquals(DisplayOrientation.Default, window.getCurrentOrientation());
            assertTrue(window.getHandle().getIsZero());
            assertNotNull(window.getScreenDeviceName());
            window.getAllowUserResizing();
            window.BeginScreenDeviceChange(false);
            game.RunOneFrame();
        }
    }

    @Test
    void NativeKeyboardCapturesIndependentHeadlessSnapshots() {
        try (KeyboardGame game = new KeyboardGame()) {
            assertThrows(IllegalStateException.class, Keyboard::GetState);
            game.RunOneFrame();
            assertNotNull(game.first);
            assertNotSame(game.first, game.second);
            assertEquals(game.first, game.second);
            assertTrue(game.first.IsKeyUp(Keys.Escape));
            assertArrayEquals(new Keys[0], game.first.GetPressedKeys());
        }
        assertThrows(IllegalStateException.class, Keyboard::GetState);
    }

    @Test
    void NativeMouseCapturesHeadlessSnapshotAndOpaqueWindowBinding() {
        try (MouseGame game = new MouseGame()) {
            assertThrows(IllegalStateException.class, Mouse::GetState);
            game.RunOneFrame();
            assertEquals(new MouseState(), game.state);
            assertEquals(ButtonState.Released, game.state.getLeftButton());
            assertTrue(Mouse.getWindowHandle().getIsZero());
            Mouse.setWindowHandle(game.getWindow().getHandle());
            Mouse.SetPosition(17, 23);
        }
        assertThrows(IllegalStateException.class, Mouse::GetState);
    }

    private static final class ProbeGame extends Game {
        private final List<String> events = new ArrayList<>();
        private final int frameLimit;
        private int frames;

        private ProbeGame(int frameLimit) {
            this.frameLimit = frameLimit;
        }

        @Override protected void Initialize() { events.add("Initialize"); }
        @Override protected void LoadContent() { events.add("LoadContent"); }
        @Override protected void BeginRun() { events.add("BeginRun"); }
        @Override protected void Update(GameTime gameTime) { events.add("Update"); }
        @Override protected void Draw(GameTime gameTime) {
            events.add("Draw");
            if (++frames == frameLimit) Exit();
        }
        @Override protected void EndRun() { events.add("EndRun"); }
        @Override protected void UnloadContent() { events.add("UnloadContent"); }
    }

    private static final class StepGame extends Game {
        private int updates;
        private int draws;
        private LateComponent lateComponent;

        @Override
        protected void Update(GameTime gameTime) {
            updates++;
            if (lateComponent == null) {
                lateComponent = new LateComponent(this);
                getComponents().add(lateComponent);
            }
        }

        @Override
        protected void Draw(GameTime gameTime) {
            draws++;
        }
    }

    private static final class LateComponent extends GameComponent {
        private boolean initialized;

        private LateComponent(Game game) {
            super(game);
        }

        @Override
        public void Initialize() {
            initialized = true;
        }
    }

    private static final class KeyboardGame extends Game {
        private KeyboardState first;
        private KeyboardState second;

        @Override
        protected void Update(GameTime gameTime) {
            first = Keyboard.GetState();
            second = Keyboard.GetState(Microsoft.Xna.Framework.PlayerIndex.One);
        }
    }

    private static final class MouseGame extends Game {
        private MouseState state;

        @Override
        protected void Update(GameTime gameTime) {
            state = Mouse.GetState();
        }
    }
}
