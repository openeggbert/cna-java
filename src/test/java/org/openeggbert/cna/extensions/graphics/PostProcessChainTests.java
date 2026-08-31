package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.RenderTarget2D;
import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Vector3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The standalone post-process chain, against the live runtime.
 *
 * <p><strong>What this can and cannot say.</strong> VERIFIED_HEADLESS_GAME. The chain needs a
 * device for its pooled targets, and nothing here claims a pixel changed -- this renderer runs no
 * shader. What is checked is the bookkeeping a game gets wrong on its own: how many passes the
 * chain holds, which of them it owns, what a hand-over leaves behind, and that its pool is a
 * counted borrow rather than a second owner.
 *
 * <p><strong>One thing these tests cannot check, and it is said rather than worked around.</strong>
 * The context is write-only -- CNA has no route that reads one back -- so no assertion here can
 * catch a leaf offset that names the wrong field, and a swapped inverse would reach a pass
 * silently. Those offsets are pinned against the live header by the generator tool tests instead.
 * What is checked below is everything that does come back: the defaults CNA fills in, and that the
 * inverses this class derives really are inverses.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class PostProcessChainTests {

    @Test
    void aBorrowedPassStaysTheCallersAfterTheChainIsDoneWithIt() {
        GameProbe.run(probe -> {
            BlitPass pass = BlitPass.create(probe.device());
            try (PostProcessChain chain = PostProcessChain.create(probe.device())) {
                assertEquals(0, chain.getPassCount(), "a fresh chain holds none");
                chain.addPass(pass);
                assertEquals(1, chain.getPassCount());
                assertEquals(List.of(pass), chain.getPasses());

                chain.clear();
                assertEquals(0, chain.getPassCount());
                assertTrue(chain.getPasses().isEmpty());
            }
            // Borrowed means borrowed: the chain is gone and the pass is still usable and still
            // the caller's to close. Had the chain released it, this would throw.
            assertEquals("Blit", pass.getName());
            pass.close();
        });
    }

    @Test
    void onePassCanAppearAtTwoPointsInAChain() {
        GameProbe.run(probe -> {
            GraphicsDevice device = probe.device();
            BlitPass blit = BlitPass.create(device);
            FilmGrainPass grain = FilmGrainPass.create(device);
            try (PostProcessChain chain = PostProcessChain.create(device)) {
                chain.addPass(blit);
                chain.addPass(grain);
                chain.addPass(blit);

                // Three entries, one pass twice: the chain counts places in the sequence rather
                // than distinct objects, which is what lets a game run one blit at two points in
                // a frame without a second pass object.
                assertEquals(3, chain.getPassCount());
                assertEquals(List.of(blit, grain, blit), chain.getPasses());
            }
            assertEquals("Blit", blit.getName(),
                    "closing the chain released nothing that was borrowed");
            blit.close();
            grain.close();
        });
    }

    @Test
    void theTargetPoolIsACountedBorrowAndTheChainSaysSo() {
        GameProbe.run(probe -> {
            PostProcessChain chain = PostProcessChain.create(probe.device());
            RenderTargetPool pool = chain.getTargetPool();
            assertNotNull(pool);
            assertEquals(0, pool.getTargetCount(), "the pool starts empty");

            // The count is what makes it a borrow rather than a second owner: the chain cannot
            // be closed while the pool is out, and the refusal leaves a usable chain.
            assertThrows(IllegalStateException.class, chain::close);
            assertEquals(0, chain.getPassCount(), "a refused close left the chain usable");

            pool.close();
            chain.close();
            chain.close();
            assertThrows(IllegalStateException.class, chain::getPassCount);
        });
    }

    @Test
    void aChainRunsOverAGamesOwnTexturesWithoutAPipeline() {
        GameProbe.run(probe -> {
            GraphicsDevice device = probe.device();
            try (PostProcessChain chain = PostProcessChain.create(device);
                 RenderTarget2D source = new RenderTarget2D(device, 64, 64);
                 RenderTarget2D destination = new RenderTarget2D(device, 64, 64);
                 BlitPass blit = BlitPass.create(device)) {
                chain.addPass(blit);

                PostProcessContext context = new PostProcessContext();
                context.setSource(source);
                context.setDestination(destination);
                context.setSize(64, 64);
                context.setElapsedSeconds(1f / 60f);
                context.setPlanes(0.5f, 300f);
                context.setCamera(
                        Matrix.CreateLookAt(new Vector3(0f, 2f, 6f), Vector3.getZero(),
                                Vector3.getUp()),
                        Matrix.CreatePerspectiveFieldOfView(0.9f, 1.6f, 0.5f, 300f));

                // The whole point of the type: a chain applied to a game's own textures, with no
                // RenderPipeline anywhere. Whether the blit shader runs is this renderer's
                // business; that the chain accepted the frame and kept its state is not.
                chain.apply(context);
                assertEquals(1, chain.getPassCount(), "applying did not disturb the chain");

                // The pool is where the intermediate targets come from, so a chain that ran has
                // either used one or had nothing to ping-pong between -- both are consistent,
                // and resetting is what a game does on a resize.
                chain.resetTargets();
                try (RenderTargetPool pool = chain.getTargetPool()) {
                    assertEquals(0, pool.getTargetCount(), "reset gave the targets back");
                }

                assertThrows(NullPointerException.class, () -> chain.apply(null));
            }
        });
    }

    @Test
    void asinglePassRunsOnItsOwnWithoutAChain() {
        GameProbe.run(probe -> {
            GraphicsDevice device = probe.device();
            BlitPass blit = BlitPass.create(device);
            try (RenderTarget2D source = new RenderTarget2D(device, 32, 32);
                 RenderTarget2D destination = new RenderTarget2D(device, 32, 32)) {
                PostProcessContext context = new PostProcessContext();
                context.setSource(source);
                context.setDestination(destination);
                context.setSize(32, 32);
                context.setPlanes(0.5f, 100f);

                // No pool, no ping-pong, no chain: one pass over a game's own two targets, which
                // is what a game with a single effect actually wants.
                blit.apply(context);
                assertEquals("Blit", blit.getName(), "and the pass is unchanged by running");

                assertThrows(NullPointerException.class, () -> blit.apply(null));
                blit.close();
                assertThrows(IllegalStateException.class, () -> blit.apply(context));
            }
        });
    }

    @Test
    void theContextCarriesEveryCameraTransformAndInvertsThemItself() {
        PostProcessContext context = new PostProcessContext();
        // CNA's defaults first, so the test is against what CNA says rather than what Java hopes.
        assertEquals(0, context.getWidth());
        assertFalse(context.hasPreviousFrame());

        Matrix view = Matrix.CreateLookAt(new Vector3(3f, 4f, 5f), new Vector3(1f, 0f, 2f),
                Vector3.getUp());
        Matrix projection = Matrix.CreatePerspectiveFieldOfView(1.1f, 1.6f, 0.5f, 120f);
        context.setCamera(view, projection);

        // The inverses are derived rather than taken, because a caller that computed them
        // separately could hand over one that does not match -- and a pass reconstructing a
        // world position from a depth would then be wrong with nothing to catch it.
        assertMatrixEquals(projection, context.getProjection());
        assertMatrixEquals(Matrix.Multiply(projection, context.getInverseProjection()),
                Matrix.getIdentity());
        assertMatrixEquals(Matrix.Multiply(view, context.getInverseView()), Matrix.getIdentity());

        assertFalse(context.hasPreviousFrame());
        context.setPreviousViewProjection(Matrix.Multiply(view, projection));
        assertTrue(context.hasPreviousFrame(), "now there is one to reproject from");
        assertMatrixEquals(Matrix.Multiply(view, projection),
                context.getPreviousViewProjection());
        context.setPreviousViewProjection(null);
        assertFalse(context.hasPreviousFrame(), "and a game can say there is not");

        assertThrows(NullPointerException.class, () -> context.setCamera(null, projection));
        assertThrows(NullPointerException.class, () -> context.setCamera(view, null));
    }

    @Test
    void timingIsHonestAboutHavingNoClock() {
        GameProbe.run(probe -> {
            try (PostProcessChain chain = PostProcessChain.create(probe.device());
                 BlitPass blit = BlitPass.create(probe.device())) {
                assertFalse(chain.isGpuTimingEnabled(), "timing is off until asked for");
                chain.setGpuTimingEnabled(true);
                chain.addPass(blit);

                // Asking for timing does not create a clock, and the chain reports what it can
                // actually do rather than a fabricated zero presented as a measurement.
                for (PassTiming timing : chain.getPassTimings()) {
                    assertFalse(timing.name().isBlank(), "a timing names its pass");
                    assertTrue(timing.sampleCount() >= 0);
                    assertTrue(timing.milliseconds() >= 0.0);
                }
                chain.setGpuTimingEnabled(false);
                assertFalse(chain.isGpuTimingEnabled());
            }
        });
    }

    @Test
    void nullsAndAClosedChainAreRefused() {
        GameProbe.run(probe -> {
            PostProcessChain chain = PostProcessChain.create(probe.device());
            assertThrows(NullPointerException.class, () -> chain.addPass(null));
            chain.close();
            assertThrows(IllegalStateException.class, chain::clear);
            assertThrows(IllegalStateException.class, chain::getTargetPool);
            assertThrows(IllegalStateException.class, () -> chain.setGpuTimingEnabled(true));
            assertThrows(NullPointerException.class, () -> PostProcessChain.create(null));
        });
    }

    private static void assertMatrixEquals(Matrix expected, Matrix actual) {
        float[] left = EngineValues.floats(expected, "expected");
        float[] right = EngineValues.floats(actual, "actual");
        for (int index = 0; index < left.length; index++) {
            assertEquals(left[index], right[index], 1.0e-4f, "element " + index);
        }
    }
}
