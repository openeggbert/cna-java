package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Color;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Automatic exposure, against the live runtime.
 *
 * <p><strong>What this can say depends on the renderer, and it says which.</strong> The meter
 * reduces a whole frame to one number with a compute shader, so on a renderer without compute
 * there is nothing to construct and that refusal is what gets qualified. Where compute exists,
 * these tests hand the meter frames whose brightness is known in advance and check the numbers it
 * returns against that -- not that a call succeeded.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class AutoExposureTests {

    /** A texture whose every texel is one known grey. */
    private static Texture2D grey(GameProbe probe, int level) {
        Texture2D texture = new Texture2D(probe.device(), 16, 16);
        Color[] pixels = new Color[16 * 16];
        Arrays.fill(pixels, new Color(level, level, level, 255));
        texture.SetData(pixels);
        return texture;
    }

    @Test
    void aRendererWithoutComputeRefusesTheMeterOutright() {
        GameProbe.run(probe -> {
            if (AutoExposure.isSupported(probe.device())) {
                return;
            }
            // Not the GPU timer's shape. That family constructs and reports itself unsupported,
            // so its refusal is observable; this one has no object to observe, because the
            // constructor builds a compute program and a storage buffer.
            assertThrows(ExtensionNotSupportedException.class,
                    () -> AutoExposure.create(probe.device()),
                    "a renderer with no compute must refuse the meter at construction");
        });
    }

    @Test
    void theDefaultsAreCnasOwn() {
        GameProbe.run(probe -> {
            if (!AutoExposure.isSupported(probe.device())) {
                return;
            }
            try (AutoExposure meter = AutoExposure.create(probe.device())) {
                assertEquals(1.0F, meter.getExposure(), 1.0e-6F,
                        "a fresh meter starts at an exposure of one");
                assertEquals(0.18F, meter.getKeyValue(), 1.0e-6F,
                        "and at photography's middle grey");
                // The asymmetry is the design: an eye adjusts to light far faster than to dark.
                assertTrue(meter.getBrighteningSpeed() > meter.getDarkeningSpeed(),
                        "adaptation to a brighter scene must be the faster direction: "
                                + meter.getBrighteningSpeed() + " vs "
                                + meter.getDarkeningSpeed());
            }
        });
    }

    @Test
    void aBrighterFrameMeasuresBrighter() {
        GameProbe.run(probe -> {
            if (!AutoExposure.isSupported(probe.device())) {
                return;
            }
            try (AutoExposure meter = AutoExposure.create(probe.device());
                    Texture2D dark = grey(probe, 8);
                    Texture2D bright = grey(probe, 240)) {
                float darkLuminance = meter.measureAverageLuminance(dark);
                float brightLuminance = meter.measureAverageLuminance(bright);
                // The reduction really read the frame it was handed: 8/255 and 240/255 are the
                // luminances of these two greys, and the meter returns them rather than a
                // constant. A tolerance rather than an equality because the reduction runs in
                // floating point on the GPU.
                assertEquals(8.0F / 255.0F, darkLuminance, 1.0e-3F,
                        "the dark frame's own luminance");
                assertEquals(240.0F / 255.0F, brightLuminance, 1.0e-3F,
                        "the bright frame's own luminance");
                assertTrue(brightLuminance > darkLuminance,
                        "and the brighter frame must measure brighter");
            }
        });
    }

    @Test
    void adaptationMovesTheExposureInTheDirectionTheSceneAsksFor() {
        GameProbe.run(probe -> {
            if (!AutoExposure.isSupported(probe.device())) {
                return;
            }
            try (AutoExposure meter = AutoExposure.create(probe.device());
                    Texture2D dark = grey(probe, 8);
                    Texture2D bright = grey(probe, 240)) {
                // A long step so the exponential has converged and the direction is unambiguous;
                // nothing here asserts a rate, only a direction and an ordering, because CNA
                // promises the shape of the curve rather than a value at a time.
                meter.setExposure(1.0F);
                float towardBright = meter.update(bright, 1000.0F);
                meter.setExposure(1.0F);
                float towardDark = meter.update(dark, 1000.0F);

                assertTrue(towardBright < 1.0F,
                        "a brighter scene needs a lower exposure, got " + towardBright);
                assertTrue(towardDark > 1.0F,
                        "a darker scene needs a higher exposure, got " + towardDark);
                assertTrue(towardDark > towardBright,
                        "and the dark scene's exposure must be the higher of the two");
                assertEquals(towardDark, meter.getExposure(), 1.0e-6F,
                        "the meter keeps what update returned");
            }
        });
    }

    @Test
    void aHigherKeyValueAsksForABrighterPicture() {
        GameProbe.run(probe -> {
            if (!AutoExposure.isSupported(probe.device())) {
                return;
            }
            try (AutoExposure meter = AutoExposure.create(probe.device());
                    Texture2D scene = grey(probe, 64)) {
                meter.setKeyValue(0.18F);
                meter.setExposure(1.0F);
                float atMiddleGrey = meter.update(scene, 1000.0F);

                meter.setKeyValue(0.36F);
                assertEquals(0.36F, meter.getKeyValue(), 1.0e-6F);
                meter.setExposure(1.0F);
                float atTwiceMiddleGrey = meter.update(scene, 1000.0F);

                // The key value is what the exposure is aiming the frame's log-average at, so
                // doubling it asks for twice as much exposure from the same scene. That is the
                // one relation between the two numbers a game can rely on.
                assertTrue(atTwiceMiddleGrey > atMiddleGrey,
                        "a higher key value must ask for more exposure: " + atTwiceMiddleGrey
                                + " vs " + atMiddleGrey);
            }
        });
    }

    @Test
    void theRangeClampsTheExposureAndTheSpeedsAreValidatedAsAPair() {
        GameProbe.run(probe -> {
            if (!AutoExposure.isSupported(probe.device())) {
                return;
            }
            try (AutoExposure meter = AutoExposure.create(probe.device())) {
                meter.setExposureRange(0.5F, 2.0F);
                meter.setExposure(8.0F);
                // Documented: a value inside its own contract still comes back clamped.
                assertEquals(2.0F, meter.getExposure(), 1.0e-6F,
                        "an exposure above the range is clamped into it");

                meter.setExposure(1.0F);
                meter.setExposureRange(4.0F, 8.0F);
                // And the other direction: setting a range re-clamps an exposure already set.
                assertEquals(4.0F, meter.getExposure(), 1.0e-6F,
                        "setting a range re-clamps the current exposure");

                float brightening = meter.getBrighteningSpeed();
                float darkening = meter.getDarkeningSpeed();
                assertThrows(IllegalArgumentException.class,
                        () -> meter.setAdaptationSpeeds(3.0F, 0.0F),
                        "a non-positive speed is refused");
                assertEquals(brightening, meter.getBrighteningSpeed(), 1.0e-6F,
                        "and the pair is validated as a pair: the good half is not written");
                assertEquals(darkening, meter.getDarkeningSpeed(), 1.0e-6F);

                assertThrows(IllegalArgumentException.class, () -> meter.setExposure(0.0F));
                assertThrows(IllegalArgumentException.class, () -> meter.setKeyValue(-1.0F));
                assertThrows(IllegalArgumentException.class,
                        () -> meter.setExposureRange(2.0F, 1.0F),
                        "a maximum below the minimum is refused");
            }
        });
    }

    @Test
    void applyToWritesTheExposureAndLeavesEveryOtherSettingAlone() {
        GameProbe.run(probe -> {
            if (!AutoExposure.isSupported(probe.device())) {
                return;
            }
            try (AutoExposure meter = AutoExposure.create(probe.device())) {
                RenderPipelineSettingsExt settings = new RenderPipelineSettingsExt();
                settings.setBloomEnabled(true);
                settings.setBloomIntensity(0.75F);
                settings.setTonemappingMode(TonemappingMode.Aces);
                float exposureBefore = settings.getExposure();

                meter.setExposureRange(0.25F, 4.0F);
                meter.setExposure(2.5F);
                meter.applyTo(settings);

                assertEquals(2.5F, settings.getExposure(), 1.0e-6F,
                        "the meter's exposure reaches the settings");
                assertNotEquals(exposureBefore, settings.getExposure(),
                        "and it really changed something");
                // The whole reason this crosses the boundary in both directions: an output-only
                // marshalling would have zeroed every other field on the way in.
                assertTrue(settings.getBloomEnabled(), "bloom stays enabled");
                assertEquals(0.75F, settings.getBloomIntensity(), 1.0e-6F,
                        "and its intensity is untouched");
                assertEquals(TonemappingMode.Aces, settings.getTonemappingMode(),
                        "and so is the tonemapping mode");
            }
        });
    }

    @Test
    void aClosedMeterRefusesFurtherUse() {
        GameProbe.run(probe -> {
            if (!AutoExposure.isSupported(probe.device())) {
                return;
            }
            AutoExposure meter = AutoExposure.create(probe.device());
            meter.close();
            meter.close();
            assertThrows(IllegalStateException.class, meter::getExposure);
        });
    }
}
