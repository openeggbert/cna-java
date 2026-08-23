package Microsoft.Xna.Framework.GamerServices;

import Microsoft.Xna.Framework.Game;
import Microsoft.Xna.Framework.GameComponent;
import Microsoft.Xna.Framework.GameTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class GamerServicesComponentTests {

    @Test
    void constructorAndInheritedComponentStateRemainManaged() {
        try (Game game = new Game()) {
            GamerServicesComponent component = new GamerServicesComponent(game);
            AtomicInteger enabledEvents = new AtomicInteger();
            AtomicInteger orderEvents = new AtomicInteger();
            component.addEnabledChangedListener((sender, args) -> enabledEvents.incrementAndGet());
            component.addUpdateOrderChangedListener(
                    (sender, args) -> orderEvents.incrementAndGet());

            assertSame(game, component.getGame());
            component.setEnabled(false);
            component.setUpdateOrder(-7);

            assertFalse(component.getEnabled());
            assertEquals(-7, component.getUpdateOrder());
            assertEquals(1, enabledEvents.get());
            assertEquals(1, orderEvents.get());
        }
        assertThrows(NullPointerException.class, () -> new GamerServicesComponent(null));
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
    void canonicalDispatcherRoutesParticipateInGameLifecycleAndRepeatedGames() {
        for (int iteration = 0; iteration < 3; iteration++) {
            try (Game game = new Game()) {
                List<String> updateOrder = new ArrayList<>();
                TrackingComponent component = new TrackingComponent(game, updateOrder);
                OrderProbe probe = new OrderProbe(game, updateOrder);
                component.setUpdateOrder(10);
                probe.setUpdateOrder(-10);
                component.setEnabled(false);
                game.getComponents().add(component);
                game.getComponents().add(probe);

                game.RunOneFrame();
                assertEquals(1, component.initializeCount);
                assertEquals(0, component.updateCount);
                assertEquals(List.of("probe"), updateOrder);

                updateOrder.clear();
                component.setEnabled(true);
                game.RunOneFrame();
                game.RunOneFrame();

                assertEquals(1, component.initializeCount);
                assertEquals(2, component.updateCount);
                assertEquals(List.of("probe", "gamer", "probe", "gamer"), updateOrder);
            }
        }
    }

    private static final class TrackingComponent extends GamerServicesComponent {
        private int initializeCount;
        private int updateCount;
        private final List<String> updateOrder;

        private TrackingComponent(Game game, List<String> updateOrder) {
            super(game);
            this.updateOrder = updateOrder;
        }

        @Override
        public void Initialize() {
            super.Initialize();
            initializeCount++;
        }

        @Override
        public void Update(GameTime gameTime) {
            super.Update(gameTime);
            updateCount++;
            updateOrder.add("gamer");
        }
    }

    private static final class OrderProbe extends GameComponent {
        private final List<String> updateOrder;

        private OrderProbe(Game game, List<String> updateOrder) {
            super(game);
            this.updateOrder = updateOrder;
        }

        @Override
        public void Update(GameTime gameTime) {
            super.Update(gameTime);
            updateOrder.add("probe");
        }
    }
}
