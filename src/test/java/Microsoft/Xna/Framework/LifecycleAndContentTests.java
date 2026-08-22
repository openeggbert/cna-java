package Microsoft.Xna.Framework;

import Microsoft.Xna.Framework.Content.ContentLoadException;
import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class LifecycleAndContentTests {

    @Test
    void Game_PropertiesFollowNormativeMappingAndCloseIsIdempotent() {
        TestGame game = new TestGame();
        GraphicsDeviceManager manager = new GraphicsDeviceManager(game);
        assertSame(game.getGraphicsDevice(), manager.getGraphicsDevice());
        assertNotNull(game.getContent());
        game.setIsMouseVisible(true);
        assertTrue(game.getIsMouseVisible());
        game.close();
        game.close();
        assertThrows(IllegalStateException.class, game::Run);
        assertThrows(IllegalStateException.class, manager::getGraphicsDevice);
    }

    @Test
    void ContentManager_ValidatesNamesAndDisposedState() {
        GameServiceContainer services = new GameServiceContainer();
        var content = new Microsoft.Xna.Framework.Content.ContentManager(services);
        assertSame(services, content.getServiceProvider());
        content.setRootDirectory("Content");
        assertEquals("Content", content.getRootDirectory());
        assertThrows(IllegalArgumentException.class, () -> content.Load(String.class, ""));
        assertThrows(ContentLoadException.class, () -> content.Load(String.class, "missing"));
        content.close();
        assertThrows(IllegalStateException.class, content::getRootDirectory);
    }

    @Test
    void ContentManager_ExactConstructorsValidateAndSetRootDirectory() {
        GameServiceContainer services = new GameServiceContainer();
        var content = new Microsoft.Xna.Framework.Content.ContentManager(services, "Assets");
        assertEquals("Assets", content.getRootDirectory());
        assertThrows(NullPointerException.class,
                () -> new Microsoft.Xna.Framework.Content.ContentManager(null));
        assertThrows(NullPointerException.class,
                () -> new Microsoft.Xna.Framework.Content.ContentManager(services, null));
        content.close();
    }

    @Test
    void GraphicsDevice_RejectsUseBeforeNativeRun() {
        try (TestGame game = new TestGame()) {
            GraphicsDevice device = game.getGraphicsDevice();
            assertThrows(IllegalStateException.class, () -> device.Clear(Color.CornflowerBlue));
            assertThrows(NullPointerException.class, () -> device.Clear(null));
        }
    }

    private static final class TestGame extends Game {
    }
}
