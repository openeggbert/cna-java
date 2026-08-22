package Microsoft.Xna.Framework.Graphics;

import Microsoft.Xna.Framework.Game;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

final class GraphicsResourceTests {

    @Test
    void SurfaceAndSpriteEnumsPreserveXnaIdentityAndValues() {
        assertEquals(0, SurfaceFormat.Color.ordinal());
        assertEquals(19, SurfaceFormat.HdrBlendable.ordinal());
        assertEquals(4, SpriteSortMode.FrontToBack.ordinal());
        assertEquals(3, SpriteEffects.FlipHorizontally.Or(SpriteEffects.FlipVertically).getValue());
        assertTrue(SpriteEffects.FromValue(3).Contains(SpriteEffects.FlipHorizontally));
        assertEquals(SpriteEffects.None, SpriteEffects.FromValue(0));
    }

    @Test
    void GraphicsResourcePropertiesEventsAndCloseAreDeterministic() {
        try (Game game = new Game()) {
            DummyResource resource = new DummyResource(game.getGraphicsDevice());
            List<String> events = new ArrayList<>();
            resource.setName("probe");
            resource.setTag(17);
            resource.addDisposingListener((sender, args) -> events.add(sender.toString()));

            assertSame(game.getGraphicsDevice(), resource.getGraphicsDevice());
            assertEquals("probe", resource.getName());
            assertEquals(17, resource.getTag());
            assertFalse(resource.getIsDisposed());
            resource.close();
            resource.close();

            assertTrue(resource.getIsDisposed());
            assertEquals(1, resource.disposeCalls);
            assertEquals(List.of("probe"), events);
        }
    }

    @Test
    void NativeGraphicsConstructionRequiresAnActiveLifecycleCallback() {
        try (Game game = new Game()) {
            assertThrows(IllegalStateException.class,
                    () -> new Texture2D(game.getGraphicsDevice(), 1, 1));
            assertThrows(IllegalStateException.class,
                    () -> new SpriteBatch(game.getGraphicsDevice()));
        }
    }

    private static final class DummyResource extends GraphicsResource {
        private int disposeCalls;

        private DummyResource(GraphicsDevice graphicsDevice) {
            super(graphicsDevice);
        }

        @Override
        protected void Dispose(boolean arg0) {
            disposeCalls++;
            super.Dispose(arg0);
        }
    }
}
