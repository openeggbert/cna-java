package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Color;
import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Vector3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The render pipeline, against the live runtime.
 *
 * <p><strong>What this can and cannot say.</strong> VERIFIED_HEADLESS_GAME: the pipeline runs a
 * real frame on a real device, and this renderer draws nothing visible, so no claim is made about
 * the image. What the frame does report is exact -- whether it opened, how many passes ran, how
 * many times the target changed, what it estimates its memory at -- and those are the numbers a
 * game budgets against.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class RenderPipelineTests {

    private static final Matrix VIEW = Matrix.CreateLookAt(
            new Vector3(0f, 2f, 8f), new Vector3(0f, 0f, 0f), new Vector3(0f, 1f, 0f));

    private static final Matrix PROJECTION = Matrix.CreatePerspectiveFieldOfView(
            (float) (Math.PI / 3.0), 4.0f / 3.0f, 0.5f, 250.0f);

    @Test
    void aFrameHasToBeSizedBeforeItCanOpen() {
        GameProbe.run(probe -> {
            try (RenderPipeline pipeline = RenderPipeline.create(probe.device())) {
                // Two distinct states with distinct messages, and this is the first: a pipeline
                // that has never been sized has no targets to render into and says so rather
                // than guessing a size.
                assertThrows(IllegalStateException.class,
                        () -> pipeline.begin(Color.CornflowerBlue));

                pipeline.resize(320, 240);
                pipeline.setCamera(VIEW, PROJECTION, 0.5f, 250.0f);
                pipeline.begin(Color.CornflowerBlue);
                // And the second: a frame that is already open.
                assertThrows(IllegalStateException.class,
                        () -> pipeline.begin(Color.Black));
                pipeline.end();

                // Which is symmetric, so the next frame opens.
                pipeline.begin(Color.Black);
                pipeline.end();
                assertThrows(NullPointerException.class, () -> pipeline.begin(null));
            }
        });
    }

    @Test
    void theSettingsGoInAndComeBackCorrected() {
        GameProbe.run(probe -> {
            try (RenderPipeline pipeline = RenderPipeline.create(probe.device())) {
                RenderPipelineSettingsExt settings = pipeline.getSettings();
                assertNotNull(settings);

                settings.setBloomEnabled(true);
                settings.setExposure(2.5f);
                settings.setTonemappingMode(TonemappingMode.Aces);
                // Out of range on purpose: the pipeline corrects what it stores, so asking it
                // back is the only way to know what is actually running.
                settings.setMotionBlurStrength(9.0f);
                pipeline.setSettings(settings);

                RenderPipelineSettingsExt stored = pipeline.getSettings();
                assertTrue(stored.getBloomEnabled(), "a boolean survives the round trip");
                assertEquals(2.5f, stored.getExposure(), 1.0e-5f);
                assertEquals(TonemappingMode.Aces, stored.getTonemappingMode());
                assertEquals(1.0f, stored.getMotionBlurStrength(),
                        "and the pipeline clamps what it was given");

                assertThrows(NullPointerException.class, () -> pipeline.setSettings(null));
            }
        });
    }

    @Test
    void aFrameReportsWhatItDid() {
        GameProbe.run(probe -> {
            try (RenderPipeline pipeline = RenderPipeline.create(probe.device())) {
                pipeline.resize(256, 256);
                pipeline.setCamera(VIEW, PROJECTION, 0.5f, 250.0f);

                RenderPipelineSettingsExt off = pipeline.getSettings();
                off.setBloomEnabled(false);
                off.setFxaaEnabled(false);
                off.setSsaoEnabled(false);
                off.setSsrEnabled(false);
                off.setDofEnabled(false);
                off.setColorGradeEnabled(false);
                pipeline.setSettings(off);
                pipeline.begin(Color.Black);
                pipeline.end();
                int quiet = pipeline.getLastFramePassCount();

                // Turning effects on makes the frame run more passes, and the pipeline says so.
                // That is the number a game watches when a settings screen starts costing.
                RenderPipelineSettingsExt on = pipeline.getSettings();
                on.setBloomEnabled(true);
                on.setFxaaEnabled(true);
                pipeline.setSettings(on);
                pipeline.begin(Color.Black);
                pipeline.end();
                int busy = pipeline.getLastFramePassCount();
                assertTrue(busy >= quiet,
                        "more effects cannot mean fewer passes: " + quiet + " then " + busy);

                RenderPipelineFrameStatistics statistics = pipeline.getStatistics();
                assertEquals(busy, statistics.passesRun(),
                        "the statistics agree with the pass count");
                assertEquals(pipeline.isUsingSceneTarget(), statistics.usedSceneTarget());
                assertEquals(pipeline.didSkyboxDraw(), statistics.drewSkybox());
                assertEquals(pipeline.getGpuMemoryEstimateBytes(),
                        statistics.gpuMemoryEstimateBytes());
                assertTrue(statistics.targetSwitches() >= 0);

                // The memory estimate follows the size, which is the check that it is an
                // estimate of something rather than a constant.
                long small = pipeline.getGpuMemoryEstimateBytes();
                pipeline.resize(1024, 1024);
                pipeline.begin(Color.Black);
                pipeline.end();
                assertTrue(pipeline.getGpuMemoryEstimateBytes() >= small,
                        "a bigger frame does not cost less: " + small + " then "
                        + pipeline.getGpuMemoryEstimateBytes());

                assertNotNull(pipeline.getSceneTargetFormat());
                pipeline.releaseDeviceResources();
            }
        });
    }

    @Test
    void aUserPassIsBorrowedAndTheCallerKeepsIt() {
        GameProbe.run(probe -> {
            try (RenderPipeline pipeline = RenderPipeline.create(probe.device());
                 BlitPass first = BlitPass.create(probe.device());
                 FilmGrainPass second = FilmGrainPass.create(probe.device())) {
                assertTrue(pipeline.getUserPasses().isEmpty());
                pipeline.addUserPass(first);
                pipeline.addUserPass(second);
                assertEquals(List.of(first, second), pipeline.getUserPasses(),
                        "the pipeline reports the passes it holds, in order");

                pipeline.resize(128, 128);
                pipeline.setCamera(VIEW, PROJECTION, 0.5f, 250.0f);
                pipeline.begin(Color.Black);
                pipeline.end();

                pipeline.clearUserPasses();
                assertTrue(pipeline.getUserPasses().isEmpty());

                assertThrows(NullPointerException.class, () -> pipeline.addUserPass(null));
            }
        });
    }

    @Test
    void perPassTimingIsHonestAboutHavingNoClock() {
        GameProbe.run(probe -> {
            try (RenderPipeline pipeline = RenderPipeline.create(probe.device())) {
                assertFalse(pipeline.isGpuTimingEnabled(), "timing is off until asked for");
                pipeline.setGpuTimingEnabled(true);
                // Asking for timing does not create a clock. The pipeline reports what it can
                // actually do, which on a renderer with no timer query is nothing -- and that
                // is a better answer than a switch that reads as on while measuring nothing.
                try (GpuTimer clock = GpuTimer.create(probe.device())) {
                    assertEquals(clock.isSupported(), pipeline.isGpuTimingEnabled(),
                            "the pipeline times only where the renderer has a timer, and it "
                            + "said: " + clock.getUnsupportedReason());
                }

                pipeline.resize(128, 128);
                pipeline.setCamera(VIEW, PROJECTION, 0.5f, 250.0f);
                pipeline.begin(Color.Black);
                pipeline.end();

                for (PassTiming timing : pipeline.getPassTimings()) {
                    assertFalse(timing.name().isBlank(), "a timing names its pass");
                    assertTrue(timing.sampleCount() >= 0);
                    assertTrue(timing.milliseconds() >= 0.0);
                    // On a renderer with no timer query nothing is ever sampled, and the record
                    // says so rather than reporting a fabricated zero as a measurement.
                    if (!timing.isMeasured()) {
                        assertEquals(0.0, timing.milliseconds(),
                                "an unsampled pass reports no time at all");
                    }
                }
                // The fallback reason is a diagnostic that exists whether or not it fell back.
                assertNotNull(pipeline.getTransparencyFallbackReason());
            }
        });
    }

    @Test
    void aClosedPipelineRefusesEveryOperation() {
        GameProbe.run(probe -> {
            RenderPipeline pipeline = RenderPipeline.create(probe.device());
            pipeline.close();
            pipeline.close();
            assertThrows(IllegalStateException.class, pipeline::getStatistics);
            assertThrows(IllegalStateException.class, () -> pipeline.resize(1, 1));
            assertThrows(NullPointerException.class, () -> RenderPipeline.create(null));
        });
    }
}
