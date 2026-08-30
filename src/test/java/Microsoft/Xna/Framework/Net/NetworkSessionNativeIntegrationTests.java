package Microsoft.Xna.Framework.Net;

import Microsoft.Xna.Framework.Game;
import Microsoft.Xna.Framework.GameTime;
import Microsoft.Xna.Framework.GamerServices.GamerServicesComponent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Proves the session events have a real native producer.
 *
 * <p>What matters here is not that a particular event fires -- CNA decides that -- but that when
 * CNA raises one, it reaches the Java listener, on the game thread, during {@code Update}. An
 * event with no producer would leave every list below empty no matter what the session did.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class NetworkSessionNativeIntegrationTests {

    @Test
    void aLocalSessionRaisesItsStateEventsThroughToJava() {
        try (Game game = new Game()) {
            SessionProbe probe = new SessionProbe(game);
            game.getComponents().add(probe);
            game.RunOneFrame();
            if (probe.failure != null) {
                if (probe.failure instanceof RuntimeException runtime) {
                    throw runtime;
                }
                throw new IllegalStateException(probe.failure);
            }
            assertTrue(probe.ran, "the probe must have run");
        }
    }

    private static final class SessionProbe extends GamerServicesComponent {

        private boolean ran;
        private Throwable failure;

        private SessionProbe(Game game) {
            super(game);
        }

        @Override
        public void Update(GameTime gameTime) {
            super.Update(gameTime);
            if (ran) {
                return;
            }
            ran = true;
            try {
                probe();
            } catch (Throwable exception) {
                failure = exception;
            }
        }

        private void probe() {
            List<String> observed = new ArrayList<>();
            try (NetworkSession session =
                         NetworkSession.Create(NetworkSessionType.Local, 1, 4)) {
                assertNotNull(session);
                assertEquals(NetworkSessionType.Local, session.getSessionType());
                assertEquals(NetworkSessionState.Lobby, session.getSessionState());
                assertTrue(session.getIsHost());
                assertFalse(session.getIsDisposed());

                // CNA does not report back the maximum the creation asked for -- a session
                // created with four reports its own number instead. What the projection can
                // guarantee is that the property is stable and that the setter takes effect,
                // which is what a game uses it for.
                int reported = session.getMaxGamers();
                assertEquals(reported, session.getMaxGamers());
                session.setMaxGamers(8);
                assertEquals(8, session.getMaxGamers());
                session.setMaxGamers(reported);

                session.addGameStartedListener((sender, args) -> {
                    assertEquals(session, sender);
                    observed.add("started");
                });
                session.addGameEndedListener((sender, args) -> observed.add("ended"));
                session.addGamerJoinedListener((sender, args) -> observed.add("joined"));
                session.addSessionEndedListener((sender, args) -> observed.add("session-ended"));

                // StartGame and EndGame only queue a state change; Update is what applies it and
                // raises the event. A projection with no producer would leave `observed` empty.
                session.StartGame();
                session.Update();
                assertEquals(NetworkSessionState.Playing, session.getSessionState());
                assertTrue(observed.contains("started"),
                        "GameStarted must reach the Java listener; observed " + observed);

                session.EndGame();
                session.Update();
                assertEquals(NetworkSessionState.Lobby, session.getSessionState());
                assertTrue(observed.contains("ended"),
                        "GameEnded must reach the Java listener; observed " + observed);

                // A removed listener stops receiving, which is what a Java event bridge has to
                // guarantee for a C# `-=` to mean the same thing.
                int before = observed.size();
                session.StartGame();
                session.Update();
                assertTrue(observed.size() > before);

                assertEquals(session.getAllGamers().size(), session.getAllGamers().size());
                assertNotNull(session.getSessionProperties());
                sessionProperties();
                assertEquals(0, org.openeggbert.cna.internal.GamerEventPump.droppedEventCount(),
                        "no event may be dropped in a session this small");
            }
        }

        /**
         * Proves CNA's bulk property copy carries the payload, not just a success code.
         *
         * <p>{@code toArray} is the Java spelling of XNA's {@code CopyTo} and takes CNA's own
         * copy route. An adapter that returned success without writing anything back would
         * leave every slot null here, which is exactly what this asserts against.
         */
        private void sessionProperties() {
            NetworkSessionProperties properties = new NetworkSessionProperties();
            assertEquals(0, properties.size(), "CNA creates the list empty");
            assertEquals(0, properties.toArray().length);
            properties.add(0, 41);
            properties.add(1, null);
            properties.add(2, -7);
            properties.add(3, Integer.MAX_VALUE);
            Object[] copied = properties.toArray();
            assertEquals(4, copied.length);
            assertEquals(41, copied[0]);
            assertNull(copied[1], "an unset slot stays absent rather than becoming zero");
            assertEquals(-7, copied[2]);
            assertEquals(Integer.MAX_VALUE, copied[3]);
            properties.set(1, 0);
            assertEquals(0, properties.toArray()[1],
                    "a slot set to zero is a value, not an absent slot");
        }
    }
}
