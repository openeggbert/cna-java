package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.BasicEffect;
import Microsoft.Xna.Framework.Graphics.Effect;
import Microsoft.Xna.Framework.Color;
import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Matrix;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The post-process pass a game supplies the effect for, against the live runtime.
 *
 * <p><strong>What this can and cannot say.</strong> VERIFIED_HEADLESS_GAME. Nothing here claims a
 * pixel changed: the pass runs inside {@link RenderPipeline}, and this renderer draws nothing. What
 * is checked is the part a game gets wrong on its own -- which of the two constructors owns the
 * effect afterwards, and what each leaves behind.
 *
 * <p>The consumed transfer is the substance. Both branches are exercised: a successful transfer
 * must leave the Java effect owning nothing, and a refused one must leave it owning exactly what
 * it did before.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class EffectPassTests {

    @Test
    void aBorrowedEffectOutlivesThePassThatDrewThroughIt() {
        GameProbe.run(probe -> {
            BasicEffect effect = new BasicEffect(probe.device());
            EffectPass pass = EffectPass.create(probe.device(), effect, "tint");
            assertEquals("tint", pass.getName(), "the pass carries the name it was given");
            assertSame(effect, pass.getEffect());
            assertFalse(pass.isOwningItsEffect(), "create borrows");

            pass.close();
            pass.close();

            // The whole meaning of borrowing: closing the pass did not take the effect with it.
            // Were it consumed, this would throw rather than answer.
            assertNotNull(effect.getCurrentTechnique());
            effect.Dispose();
        });
    }

    @Test
    void anOwningPassConsumesItsEffectAndReleasesItAtTheEnd() {
        GameProbe.run(probe -> {
            BasicEffect effect = new BasicEffect(probe.device());
            EffectPass pass = EffectPass.createOwning(probe.device(), effect, "owned");
            assertTrue(pass.isOwningItsEffect());
            assertNull(pass.getEffect(),
                    "an owning pass hands back nothing, because the effect is no longer the "
                            + "caller's to use");

            // The transfer really happened: the Java object owns no native effect any more, so
            // every route through it is refused rather than reaching a handle the pass owns.
            assertThrows(IllegalStateException.class, effect::getCurrentTechnique);
            // And disposing it is a no-op rather than a double release -- which is the failure
            // this ownership model exists to prevent.
            effect.Dispose();
            effect.Dispose();

            pass.close();
        });
    }

    @Test
    void aRefusedTransferLeavesTheCallerOwningTheEffect() {
        GameProbe.run(probe -> {
            BasicEffect effect = new BasicEffect(probe.device());
            // A disposed device is a transfer CNA refuses. The point is not the refusal but what
            // survives it: had the projection surrendered the effect before asking, this effect
            // would now belong to nobody and could never be released.
            GraphicsDevice device = probe.device();
            assertThrows(RuntimeException.class,
                    () -> EffectPass.createOwning(device, effect, ""));

            // Still the caller's, still usable, and still disposable.
            assertNotNull(effect.getCurrentTechnique(),
                    "a refused transfer left the effect where it was");
            EffectPass recovered = EffectPass.create(device, effect, "recovered");
            assertSame(effect, recovered.getEffect(),
                    "and it can still be handed to a pass that borrows");
            recovered.close();
            effect.Dispose();
        });
    }

    @Test
    void aPassWithNoEffectIsAllowedAndCanBeGivenOneLater() {
        GameProbe.run(probe -> {
            try (EffectPass pass = EffectPass.create(probe.device(), null, "empty");
                 BasicEffect effect = new BasicEffect(probe.device())) {
                assertNull(pass.getEffect(), "no effect is a pass that draws nothing");

                pass.setEffect(effect);
                assertSame(effect, pass.getEffect());

                // And back to none, which is how a game turns its own pass off without removing
                // it from the pipeline.
                pass.setEffect(null);
                assertNull(pass.getEffect());
            }
        });
    }

    @Test
    void theNameIsTheOneTheTimingIsReportedUnder() {
        GameProbe.run(probe -> {
            try (BasicEffect effect = new BasicEffect(probe.device());
                 EffectPass first = EffectPass.create(probe.device(), effect, "outline");
                 EffectPass second = EffectPass.create(probe.device(), effect, "posterize")) {
                // Two passes over one effect, each with its own name: a projection that stored
                // the name on the effect, or handed CNA a constant, would fail here.
                assertEquals("outline", first.getName());
                assertEquals("posterize", second.getName());
                assertSame(effect, first.getEffect());
                assertSame(effect, second.getEffect());
            }
        });
    }

    @Test
    void aClosedPassIsClosedAndNullsAreRefused() {
        GameProbe.run(probe -> {
            BasicEffect effect = new BasicEffect(probe.device());
            EffectPass pass = EffectPass.create(probe.device(), effect, "closing");
            pass.close();
            assertThrows(IllegalStateException.class, () -> pass.setEffect(null));
            assertThrows(IllegalStateException.class, pass::getName);

            GraphicsDevice device = probe.device();
            assertThrows(NullPointerException.class,
                    () -> EffectPass.create(null, effect, "name"));
            assertThrows(NullPointerException.class,
                    () -> EffectPass.create(device, effect, null));
            assertThrows(NullPointerException.class,
                    () -> EffectPass.createOwning(device, null, "name"));
            effect.Dispose();
        });
    }

    @Test
    void aUserPassJoinsTheChainUnderItsOwnName() {
        GameProbe.run(probe -> {
            try (RenderPipeline pipeline = RenderPipeline.create(probe.device());
                 BasicEffect effect = new BasicEffect(probe.device());
                 EffectPass pass = EffectPass.create(probe.device(), effect, "user")) {
                // The reason this type exists: a game's own pass joins CNA's chain, alongside
                // the sixteen CNA ships. The pipeline borrows it, so it is closed here.
                pipeline.addUserPass(pass);
                assertEquals(List.of(pass), pipeline.getUserPasses());

                pipeline.resize(64, 64);
                pipeline.setCamera(Matrix.getIdentity(), Matrix.getIdentity(), 0.5f, 250f);
                pipeline.begin(Color.Black);
                pipeline.end();

                // Running a frame with a user pass in it must leave the pass registered and the
                // effect untouched -- a pipeline that consumed either would be a leak or a
                // double release, and the frame above is where that would show.
                assertEquals(List.of(pass), pipeline.getUserPasses());
                assertSame(effect, pass.getEffect());
                assertNotNull(effect.getCurrentTechnique());

                pipeline.clearUserPasses();
                assertTrue(pipeline.getUserPasses().isEmpty());
            }
        });
    }
}
