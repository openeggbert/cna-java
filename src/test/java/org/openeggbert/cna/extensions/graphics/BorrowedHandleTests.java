package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Color;
import Microsoft.Xna.Framework.Graphics.Effect;
import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The handles the engine layer lends, and the terms it lends them on.
 *
 * <p>A dozen routes hand back an effect or a texture belonging to something else, and their
 * documentation says only "borrowed". That word covers two opposite contracts, and which one a
 * route has decides the Java facade: a <strong>counted</strong> borrow must be given back before
 * its lender can be closed, and a <strong>retaining</strong> one may outlive its lender entirely.
 * Nothing in a declaration distinguishes them; {@code tools/native-abi/probes/lent_effect_lifetime.c}
 * and {@code lent_handle_use_after_lender.c} measured each one, and these are the same facts
 * asserted from Java.
 *
 * <p><strong>What this can say depends on the renderer, and it says which.</strong> A renderer
 * with no shader compiler has no effect to lend and answers {@code null}, which is an answer
 * rather than a failure; where an effect exists, the ownership rules are asserted in full.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class BorrowedHandleTests {

    @Test
    void aShadowMapsCasterEffectIsACountedBorrowThatBlocksItsLender() {
        GameProbe.run(probe -> {
            GraphicsDevice device = probe.device();
            ShadowMap map = ShadowMap.create(device, ShadowQuality.Low);
            Effect first = map.getCasterEffect(device);
            if (first == null) {
                // The renderer compiles no shaders, so there is no effect to lend. Asserted
                // rather than skipped: a renderer that gains one shows up as a failing test.
                assertNull(map.getSkinnedCasterEffect(device),
                        "a map with no caster effect has no skinned one either");
                map.close();
                return;
            }
            Effect second = map.getCasterEffect(device);
            // Every call mints its own view, so two calls are two objects to dispose and one
            // effect underneath. A facade that assumed one object would double-release.
            assertNotSame(first, second, "each call lends its own view");
            Effect skinned = map.getSkinnedCasterEffect(device);
            assertNotNull(skinned, "a map that lends a caster lends a skinned one too");

            // The counted half of the contract, and the reason disposing a view is not
            // housekeeping: three views are out, so the map refuses to close.
            assertThrows(RuntimeException.class, map::close,
                    "a map still lending an effect refuses to be destroyed");
            first.Dispose();
            assertThrows(RuntimeException.class, map::close,
                    "and one released view is not enough while two are out");
            second.Dispose();
            skinned.Dispose();
            // Every borrow back, so the map closes -- which is what makes the refusal above a
            // recoverable state rather than a leak.
            map.close();
        });
    }

    @Test
    void aSpotShadowMapDoesNotBlockAndItsViewOutlivesIt() {
        GameProbe.run(probe -> {
            GraphicsDevice device = probe.device();
            SpotShadowMap map = SpotShadowMap.create(device, ShadowQuality.Low);
            Effect caster = map.getCasterEffect(device);
            if (caster == null) {
                map.close();
                return;
            }
            {
                // JAVA-UPSTREAM-013. Three sibling maps count their borrows and refuse; this one
                // does not, and closing it while a view is out succeeds. Measured in C first,
                // because a Java test that merely passed would not have said which of the two
                // contracts it was passing under.
                map.close();
                // The view kept the effect alive rather than dangling, which is what makes the
                // difference safe rather than a use-after-free waiting to happen.
                assertNotNull(caster.getCurrentTechnique(),
                        "a retained view still names a live effect after its lender is gone");
                caster.Dispose();
            }
        });
    }

    @Test
    void theDepthNormalPrepassLendsTwoEffectsOnTheSameTerms() {
        GameProbe.run(probe -> {
            GraphicsDevice device = probe.device();
            DepthNormalPrepass prepass = DepthNormalPrepass.create(device, 64, 64, DepthEncoding.Automatic);
            Effect rigid = prepass.getPrepassEffect(device);
            if (rigid == null) {
                assertNull(prepass.getSkinnedPrepassEffect(device));
                prepass.close();
                return;
            }
            Effect skinned = prepass.getSkinnedPrepassEffect(device);
            assertNotNull(skinned);
            assertNotSame(rigid, skinned, "the rigid and skinned effects are different views");
            assertThrows(RuntimeException.class, prepass::close);
            rigid.Dispose();
            skinned.Dispose();
            // Both back, so the prepass closes.
            prepass.close();
        });
    }

    @Test
    void theClusteredForwardEffectLendsItsShaderCountedAndItsMaterialRetaining() {
        GameProbe.run(probe -> {
            GraphicsDevice device = probe.device();
            ClusteredForwardEffect effect = ClusteredForwardEffect.create(device);
            PbrMaterialExtensions borrowed = effect.getEffectMaterialExtensions();
            // The retaining half: the effect's own extensions, which the view keeps alive.
            assertNotNull(borrowed);

            Effect shader = effect.getEffect(device);
            if (shader == null) {
                borrowed.close();
                effect.close();
                return;
            }
            // The counted half.
            assertThrows(RuntimeException.class, effect::close,
                    "an effect still lending its shader refuses to be destroyed");
            shader.Dispose();
            // And the material view does not block, which is the difference being asserted:
            // two routes on one object, two contracts.
            effect.close();
            assertEquals(0.0f, borrowed.getClearcoatFactor(), 1.0e-6f,
                    "the retained view still answers after its lender is gone");
            borrowed.close();
        });
    }

    @Test
    void aBorrowedMaterialAnswersWhatIsBoundEvenThoughItHasNoJavaSideRecord() {
        GameProbe.run(probe -> {
            GraphicsDevice device = probe.device();
            try (PbrMaterialExtensions material = PbrMaterialExtensions.create();
                    Texture2D texture = new Texture2D(device, 4, 4)) {
                assertFalse(material.hasClearcoatTexture(), "nothing is bound to start with");
                assertNull(material.getClearcoatTexture());

                material.setClearcoatTexture(texture);
                // Both answers agree for a material this Java object configured: the retained
                // reference and CNA's own record.
                assertTrue(material.hasClearcoatTexture());
                assertEquals(texture, material.getClearcoatTexture());

                // And the eight siblings are all still empty, so the question is about one slot
                // rather than about the material having any texture at all.
                assertFalse(material.hasSheenColorTexture());
                assertFalse(material.hasThicknessTexture());
                assertFalse(material.hasTransmissionTexture());
                assertFalse(material.hasIridescenceTexture());
                assertFalse(material.hasIridescenceThicknessTexture());
                assertFalse(material.hasClearcoatRoughnessTexture());
                assertFalse(material.hasClearcoatNormalTexture());
                assertFalse(material.hasSheenRoughnessTexture());

                material.setClearcoatTexture(null);
                assertFalse(material.hasClearcoatTexture(), "unbinding is visible to CNA too");
            }
        });
    }

    @Test
    void theTransparencyResolveLendsBothTargetsRetaining() {
        GameProbe.run(probe -> {
            GraphicsDevice device = probe.device();
            WeightedBlendedTransparency resolve =
                    WeightedBlendedTransparency.create(device, 64, 64);
            Texture2D accumulation = resolve.getAccumulationTexture(device);
            Texture2D revealage = resolve.getRevealageTexture(device);
            if (accumulation == null) {
                // A renderer with no float target has nothing to accumulate into, and says so
                // through the same question the resolve answers about itself.
                assertFalse(resolve.isSupported());
                assertNull(revealage);
                resolve.close();
                return;
            }
            assertTrue(resolve.isSupported());
            assertNotNull(revealage);
            assertNotSame(accumulation, revealage, "the two targets are different views");
            // And that is as far as Java can honestly go here. The two targets are the same size
            // and the same surface format -- measured, not assumed -- so nothing observable from
            // this side distinguishes a correct pair from two views of one target. What checks
            // the two routes independently is lent_effect_lifetime.c, which asks CNA directly.
            assertEquals(accumulation.getFormat(), revealage.getFormat(),
                    "the two targets really are the same shape, which is why this test cannot "
                            + "tell them apart");

            // Retaining: the resolve closes while both views are out, and they stay usable.
            resolve.close();
            assertEquals(64, accumulation.getWidth(),
                    "a retained target still knows its size after its lender is gone");
            assertEquals(64, revealage.getWidth());
            accumulation.Dispose();
            revealage.Dispose();
        });
    }

    @Test
    void aPipelineLendsItsSceneTargetOnlyWhileAFrameIsOpen() {
        GameProbe.run(probe -> {
            GraphicsDevice device = probe.device();
            try (RenderPipeline pipeline = RenderPipeline.create(device)) {
                pipeline.resize(64, 64);
                // Nothing enabled means no offscreen target at all, which is the pipeline's own
                // short circuit rather than an absence of anything to lend.
                assertNull(pipeline.getSceneTarget(device),
                        "a pipeline with nothing enabled renders straight to the back buffer");

                RenderPipelineSettingsExt settings = pipeline.getSettings();
                settings.setHdrEnabled(true);
                pipeline.setSettings(settings);
                assertNull(pipeline.getSceneTarget(device),
                        "and the target does not exist before a frame opens");

                pipeline.begin(Color.Black);
                Texture2D inside = pipeline.getSceneTarget(device);
                assertNotNull(inside, "inside a frame the target exists");
                Texture2D again = pipeline.getSceneTarget(device);
                assertNotSame(inside, again, "each call lends its own view");
                assertEquals(64, inside.getWidth(), "and it is the size the pipeline was given");
                inside.Dispose();
                again.Dispose();
                pipeline.end();

                assertTrue(pipeline.isUsingSceneTarget(),
                        "the frame used one, which is a different question");
                assertNull(pipeline.getSceneTarget(device),
                        "but it is gone once the frame closes");
            }
        });
    }
}
