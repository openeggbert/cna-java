package org.openeggbert.cna.extensions.avatars;

import Microsoft.Xna.Framework.Color;
import Microsoft.Xna.Framework.Game;
import Microsoft.Xna.Framework.GameTime;
import Microsoft.Xna.Framework.GamerServices.AvatarAnimation;
import Microsoft.Xna.Framework.GamerServices.AvatarAnimationPreset;
import Microsoft.Xna.Framework.GamerServices.AvatarBodyType;
import Microsoft.Xna.Framework.GamerServices.AvatarDescription;
import Microsoft.Xna.Framework.GamerServices.AvatarRenderer;
import Microsoft.Xna.Framework.GamerServices.GamerServicesComponent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** CNA's own answers about an avatar, which XNA's types have no member for. */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class AvatarExtensionTests {

    @Test
    void everyPresetAndBodyTypeNamesItsContent() {
        run(() -> {
            // XNA's enumerations say nothing about what they play or load. A title supplying its
            // own avatar content needs the names to author against, and every one has to exist:
            // a preset with no clip is a preset that cannot be authored for.
            Set<String> clips = new HashSet<>();
            for (AvatarAnimationPreset preset : AvatarAnimationPreset.values()) {
                String clip = AvatarExtensions.GetClipName(preset);
                assertFalse(clip.isEmpty(), preset + " maps to no clip");
                clips.add(clip);
            }
            assertEquals(AvatarAnimationPreset.values().length, clips.size(),
                    "two presets share a clip name, so one of them cannot be authored for");

            Set<String> assets = new HashSet<>();
            for (AvatarBodyType bodyType : AvatarBodyType.values()) {
                String asset = AvatarExtensions.GetContentName(bodyType);
                assertFalse(asset.isEmpty(), bodyType + " maps to no content");
                assets.add(asset);
            }
            assertEquals(AvatarBodyType.values().length, assets.size(),
                    "two body types share a content name");

            assertThrows(NullPointerException.class,
                    () -> AvatarExtensions.GetClipName(null));
            assertThrows(NullPointerException.class,
                    () -> AvatarExtensions.GetContentName(null));
        });
    }

    @Test
    void anAnimationCarriesTheClipItPlays() {
        run(() -> {
            try (AvatarAnimation animation =
                         new AvatarAnimation(AvatarAnimationPreset.Stand0)) {
                // The preset's own clip is what a fresh animation plays, so the two agree.
                assertEquals(AvatarExtensions.GetClipName(AvatarAnimationPreset.Stand0),
                        AvatarExtensions.GetRealClipName(animation));

                AvatarExtensions.SetRealClipName(animation, "title/idle-breathe");
                assertEquals("title/idle-breathe",
                        AvatarExtensions.GetRealClipName(animation));

                // An empty name is a legal one: it means no real clip is assigned.
                AvatarExtensions.SetRealClipName(animation, "");
                assertEquals("", AvatarExtensions.GetRealClipName(animation));

                assertThrows(NullPointerException.class,
                        () -> AvatarExtensions.SetRealClipName(animation, null));
            }
        });
    }

    @Test
    void aRendererTakesTheColoursItIsGiven() {
        run(() -> {
            AvatarAppearance defaults = AvatarAppearance.Default();
            // CNA's defaults, not this projection's: writing five colours down here would be a
            // guess that drifted the first time CNA changed one.
            assertNotNull(defaults.SkinColor());
            assertEquals(255, defaults.SkinColor().getA(),
                    "an avatar's colours are opaque");

            AvatarDescription description = AvatarDescription.CreateRandom(AvatarBodyType.Male);
            try (AvatarRenderer renderer = new AvatarRenderer(description, false)) {
                AvatarExtensions.SetAppearance(renderer, defaults);
                AvatarExtensions.SetAppearance(renderer, new AvatarAppearance(
                        new Color(210, 180, 140, 255), new Color(40, 30, 20, 255),
                        new Color(0, 120, 200, 255), new Color(30, 30, 40, 255),
                        new Color(90, 60, 40, 255)));
                assertThrows(NullPointerException.class,
                        () -> AvatarExtensions.SetAppearance(renderer, null));
                assertThrows(NullPointerException.class,
                        () -> AvatarExtensions.SetAppearance(null, defaults));
            }
        });
    }

    private static void run(Runnable body) {
        try (Game game = new Game()) {
            Probe probe = new Probe(game, body);
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

    /** Runs one body inside a frame, because CNA's avatar layer needs a live game. */
    private static final class Probe extends GamerServicesComponent {

        private final Runnable body;
        private boolean ran;
        private Throwable failure;

        private Probe(Game game, Runnable body) {
            super(game);
            this.body = body;
        }

        @Override
        public void Update(GameTime gameTime) {
            super.Update(gameTime);
            if (ran) {
                return;
            }
            ran = true;
            try {
                body.run();
            } catch (Throwable exception) {
                failure = exception;
            }
        }
    }
}
