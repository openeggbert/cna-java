package org.openeggbert.cna.internal;

import Microsoft.Xna.Framework.Game;
import Microsoft.Xna.Framework.GameTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

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
        ProbeGame game = new ProbeGame();
        game.Run();
        game.close();
        assertEquals(3, game.frames);
        assertEquals(List.of("Initialize", "LoadContent", "BeginRun"), game.events.subList(0, 3));
        assertTrue(game.events.indexOf("EndRun") > game.events.lastIndexOf("Draw"));
        assertTrue(game.events.indexOf("UnloadContent") > game.events.indexOf("EndRun"));
    }

    private static final class ProbeGame extends Game {
        private final List<String> events = new ArrayList<>();
        private int frames;

        @Override protected void Initialize() { events.add("Initialize"); }
        @Override protected void LoadContent() { events.add("LoadContent"); }
        @Override protected void BeginRun() { events.add("BeginRun"); }
        @Override protected void Update(GameTime gameTime) { events.add("Update"); }
        @Override protected void Draw(GameTime gameTime) {
            events.add("Draw");
            if (++frames == 3) Exit();
        }
        @Override protected void EndRun() { events.add("EndRun"); }
        @Override protected void UnloadContent() { events.add("UnloadContent"); }
    }
}

