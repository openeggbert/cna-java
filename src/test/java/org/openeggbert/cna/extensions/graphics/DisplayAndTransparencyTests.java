package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.DepthFormat;
import Microsoft.Xna.Framework.Graphics.SurfaceFormat;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import Microsoft.Xna.Framework.Vector3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * HDR presentation, order-independent transparency, upscaling and the target pool.
 *
 * <p>Four families that end a frame. Each carries pure arithmetic with exact properties -- a PQ
 * round trip, a blending weight that falls with depth, a scale that is or is not the identity --
 * and each has a device-backed object whose state is readable. Nothing here claims a presented
 * pixel.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class DisplayAndTransparencyTests {

    @Test
    void thePqCurveGoesThereAndBack() {
        GameProbe.run(probe -> {
            // PQ is absolute: a code value means a number of nits rather than a fraction of
            // whatever the display can do, which is what makes an HDR10 frame look the same on
            // two televisions. The round trip is the contract.
            for (float nits : new float[] {0.0f, 0.1f, 1.0f, 100.0f, 203.0f, 1000.0f, 4000.0f}) {
                float encoded = HdrDisplayOutput.encodePq(nits);
                assertTrue(encoded >= 0.0f && encoded <= 1.0f,
                        nits + " nits encoded outside zero-to-one: " + encoded);
                assertEquals(nits, HdrDisplayOutput.decodePq(encoded),
                        Math.max(0.01f, nits * 0.001f), "the round trip lost " + nits);
            }
            // And it is a curve rather than a line: the first hundred nits take far more of the
            // code range than the last hundred, which is the whole point of a perceptual
            // quantiser.
            float low = HdrDisplayOutput.encodePq(100.0f) - HdrDisplayOutput.encodePq(0.0f);
            float high = HdrDisplayOutput.encodePq(4000.0f)
                    - HdrDisplayOutput.encodePq(3900.0f);
            assertTrue(low > high * 10.0f,
                    "PQ must spend its precision on the dark end: " + low + " against " + high);

            // Rolling off compresses towards the peak rather than clipping at it.
            assertTrue(HdrDisplayOutput.rollOff(2000.0f, 1000.0f) <= 1000.0f,
                    "nothing rolls off above the peak");
            assertTrue(HdrDisplayOutput.rollOff(100.0f, 1000.0f) > 90.0f,
                    "and well below the peak almost nothing changes");

            // Rec.2020 is a wider gamut, so a saturated Rec.709 primary needs less of it.
            Vector3 red = HdrDisplayOutput.rec709ToRec2020(new Vector3(1f, 0f, 0f));
            assertTrue(red.X < 1.0f && red.X > 0.5f,
                    "a Rec.709 red is less than a Rec.2020 red: " + red);
            assertTrue(red.Y > 0.0f, "and picks up a little green in the wider space");

            // White stays white: the two spaces share a white point, which is the one colour
            // that must not move.
            Vector3 white = HdrDisplayOutput.rec709ToRec2020(new Vector3(1f, 1f, 1f));
            assertEquals(1.0f, white.X, 1.0e-3f);
            assertEquals(1.0f, white.Y, 1.0e-3f);
            assertEquals(1.0f, white.Z, 1.0e-3f);

            // The whole chain in one call, and sRGB is not HDR10.
            Vector3 sceneLinear = new Vector3(0.5f, 0.5f, 0.5f);
            assertNotEquals(
                    HdrDisplayOutput.encode(DisplayColorSpace.Srgb, sceneLinear, 203f, 1000f).X,
                    HdrDisplayOutput.encode(DisplayColorSpace.Hdr10, sceneLinear, 203f, 1000f).X,
                    "two colour spaces cannot encode the same value the same way");
            assertThrows(NullPointerException.class,
                    () -> HdrDisplayOutput.encode(null, sceneLinear, 203f, 1000f));
        });
    }

    @Test
    void anHdrOutputHoldsTheTwoNumbersThatDecideHowItLooks() {
        GameProbe.run(probe -> {
            try (HdrDisplayOutput output = HdrDisplayOutput.create(probe.device())) {
                assertEquals(DisplayColorSpace.Srgb, output.getColorSpace(),
                        "an output starts where XNA always was");
                output.setColorSpace(DisplayColorSpace.Hdr10);
                assertEquals(DisplayColorSpace.Hdr10, output.getColorSpace());

                assertTrue(output.getPaperWhiteNits() > 0f, "white has a luminance");
                assertTrue(output.getPeakNits() >= output.getPaperWhiteNits(),
                        "and the display cannot peak below its own white");
                output.setPaperWhiteNits(250.0f);
                assertEquals(250.0f, output.getPaperWhiteNits(), 1.0e-4f);
                output.setPeakNits(1500.0f);
                assertEquals(1500.0f, output.getPeakNits(), 1.0e-4f);
                output.isSupported();
                assertThrows(NullPointerException.class, () -> output.setColorSpace(null));
            }
        });
    }

    @Test
    void theBlendingWeightFallsWithDepthAndRisesWithCoverage() {
        GameProbe.run(probe -> {
            // The curve the whole technique rests on. A near fragment weighs more than a far
            // one, which is what lets the accumulation approximate the sorted answer without
            // sorting anything.
            float near = WeightedBlendedTransparency.weight(1.0f, 1.0f, 100.0f);
            float middle = WeightedBlendedTransparency.weight(50.0f, 1.0f, 100.0f);
            float far = WeightedBlendedTransparency.weight(99.0f, 1.0f, 100.0f);
            assertTrue(near > middle && middle > far,
                    "weight must fall with depth: " + near + ", " + middle + ", " + far);
            assertTrue(far > 0.0f, "and never reach zero, or a far fragment would vanish");

            // More coverage weighs more at the same depth.
            assertTrue(WeightedBlendedTransparency.weight(50.0f, 1.0f, 100.0f)
                    > WeightedBlendedTransparency.weight(50.0f, 0.1f, 100.0f));

            // Clamped rather than unbounded: the curve runs away near zero depth, and one
            // overflowing weight would poison the whole buffer rather than one fragment.
            float atZero = WeightedBlendedTransparency.weight(0.0f, 1.0f, 100.0f);
            assertTrue(Float.isFinite(atZero) && atZero > 0f,
                    "zero depth must be finite: " + atZero);
            float beyond = WeightedBlendedTransparency.weight(1000.0f, 1.0f, 100.0f);
            assertTrue(Float.isFinite(beyond) && beyond > 0f,
                    "and so must past the far plane: " + beyond);
            assertFalse(WeightedBlendedTransparency.getAccumulationGlsl().isBlank());
        });
    }

    @Test
    void theAccumulationBracketIsSymmetric() {
        GameProbe.run(probe -> {
            try (WeightedBlendedTransparency transparency =
                         WeightedBlendedTransparency.create(probe.device(), 128, 128)) {
                assertFalse(transparency.isAccumulating(), "nothing is open yet");
                boolean supported = transparency.isSupported();
                if (!supported) {
                    assertFalse(transparency.getUnsupportedReason().isBlank(),
                            "an unsupported resolve says why");
                }
                // The bracket opens on every renderer, supported or not, so a frame that
                // always runs its transparency pass stays symmetric rather than leaving an
                // accumulation half-open on a renderer that cannot resolve it.
                transparency.begin(100.0f);
                assertTrue(transparency.isAccumulating(),
                        "begin opens the accumulation whatever the renderer can do");
                transparency.end();
                assertFalse(transparency.isAccumulating(), "end closes what begin opened");
                transparency.resize(64, 64);
                transparency.resolve(64, 64);
            }
        });
    }

    @Test
    void upscalingKnowsWhenItWouldDoNothing() {
        GameProbe.run(probe -> {
            assertTrue(SpatialUpscalePass.isIdentityScale(1920, 1080, 1920, 1080),
                    "the same size is the identity");
            assertFalse(SpatialUpscalePass.isIdentityScale(1280, 720, 1920, 1080));
            assertFalse(SpatialUpscalePass.isIdentityScale(1920, 1080, 1920, 1081),
                    "one pixel taller is not the identity either");

            try (SpatialUpscalePass upscale = SpatialUpscalePass.create(probe.device())) {
                upscale.setSharpness(5.0f);
                assertEquals(1.0f, upscale.getSharpness(), "sharpness clamps at one");
                upscale.setSharpness(0.4f);
                assertEquals(0.4f, upscale.getSharpness(), 1.0e-5f);
                upscale.setEdgeAdaptive(false);
                assertFalse(upscale.isEdgeAdaptive());
                upscale.setEdgeAdaptive(true);
                assertTrue(upscale.isEdgeAdaptive());
                assertThrows(NullPointerException.class,
                        () -> upscale.draw(null, 1, 1, 2, 2));
            }
        });
    }

    @Test
    void aPoolReusesATargetAndRefusesToResetWhileOneIsOut() {
        GameProbe.run(probe -> {
            RenderTargetPool pool = RenderTargetPool.create(probe.device());
            Texture2D first = null;
            try {
                assertEquals(0L, pool.getTargetCount(), "a new pool holds nothing");
                assertEquals(0L, pool.getEstimatedBytes());

                first = pool.acquire(probe.device(), 64, 64, SurfaceFormat.Color,
                        DepthFormat.None, 0);
                assertEquals(64, first.getWidth());
                assertEquals(1L, pool.getTargetCount(), "the pool made one");
                assertTrue(pool.getEstimatedBytes() > 0L, "and it costs memory");

                // The rule the pool exists to enforce: a target still lent out cannot be reused,
                // so resetting while one is outstanding is refused rather than silently handing
                // the same target to two passes.
                assertThrows(RuntimeException.class, pool::reset,
                        "a pool with a target out must not reset");
                assertThrows(RuntimeException.class, pool::close);

                first.Dispose();
                first = null;
                pool.reset();

                // After a reset the same shape comes back out of the pool rather than being
                // allocated again, which is the whole point of pooling.
                Texture2D second = pool.acquire(probe.device(), 64, 64, SurfaceFormat.Color,
                        DepthFormat.None, 0);
                assertEquals(1L, pool.getTargetCount(), "the pool reused what it had");
                second.Dispose();

                // A different slot is a different target, because two passes of the same shape
                // may need to read one while writing the other.
                Texture2D third = pool.acquire(probe.device(), 64, 64, SurfaceFormat.Color,
                        DepthFormat.None, 1);
                assertEquals(2L, pool.getTargetCount(), "a second slot is a second target");
                third.Dispose();
            } finally {
                if (first != null) {
                    first.Dispose();
                }
                pool.close();
            }
            pool.close();
            assertThrows(IllegalStateException.class, pool::getTargetCount);
        });
    }
}
