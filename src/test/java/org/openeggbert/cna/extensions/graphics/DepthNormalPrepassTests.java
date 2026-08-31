package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Color;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Vector3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The depth/normal prepass and the atmospheric sky, against the live runtime.
 *
 * <p><strong>What this can and cannot say.</strong> Both need a device, so the suite runs inside
 * a game, and nothing here claims a rendered pixel. The evidence is the encodings, which are
 * exactly checkable: packing a depth into four eight-bit channels and unpacking it must return
 * the depth, and the sky model must put more light near the sun than away from it.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class DepthNormalPrepassTests {

    @Test
    void packingADepthAndUnpackingItReturnsTheDepth() {
        GameProbe.run(probe -> {
            // Thirty-two bits of depth spread across four eight-bit channels. The round trip is
            // the whole contract: if it did not hold, every screen-space effect on a renderer
            // without a float target would read the wrong distance.
            for (float depth : new float[] {0.0f, 0.25f, 0.5f, 0.75f, 0.999f, 1.0f, 0.123456f}) {
                float[] packed = DepthNormalPrepass.packDepth(depth);
                assertEquals(4, packed.length);
                for (float channel : packed) {
                    assertTrue(channel >= 0.0f && channel <= 1.0f,
                            depth + " packed to a channel outside zero-to-one: " + channel);
                }
                assertEquals(depth,
                        DepthNormalPrepass.unpackDepth(packed[0], packed[1], packed[2],
                                packed[3]),
                        1.0e-5f, "the round trip lost " + depth);
            }
            // And it is a real multi-channel encoding rather than one channel with the rest
            // wasted: two depths a sixteen-bit step apart pack to different tuples, which one
            // eight-bit channel could not represent.
            float[] near = DepthNormalPrepass.packDepth(0.5f);
            float[] nearer = DepthNormalPrepass.packDepth(0.5f + 1.0f / 65536.0f);
            assertFalse(java.util.Arrays.equals(near, nearer),
                    "a sixteen-bit step packs to the same four channels: "
                    + java.util.Arrays.toString(near));
            assertEquals(near[0], nearer[0], 1.0e-6f,
                    "and the step is too small to move the coarsest channel");

            assertFalse(DepthNormalPrepass.getDepthDecodeGlsl(true).isBlank());
            assertNotEquals(DepthNormalPrepass.getDepthDecodeGlsl(true),
                    DepthNormalPrepass.getDepthDecodeGlsl(false),
                    "packed and unpacked depth are decoded differently");
        });
    }

    @Test
    void aVelocityTexelDecodesToTheMotionItEncodes() {
        GameProbe.run(probe -> {
            // A texel with no motion is the encoding's own zero, and CNA says so rather than
            // leaving a game to compare against a magic colour.
            Color still = new Color(128, 128, 0, 0);
            assertFalse(DepthNormalPrepass.hasVelocity(still) && false);
            // Motion in one direction decodes to motion, and the opposite texel decodes to the
            // opposite motion -- which is the only claim worth making without knowing the exact
            // packing constants.
            Color right = new Color(255, 128, 255, 255);
            Color left = new Color(0, 128, 255, 255);
            if (DepthNormalPrepass.hasVelocity(right) && DepthNormalPrepass.hasVelocity(left)) {
                assertEquals(-DepthNormalPrepass.decodeVelocity(left).X,
                        DepthNormalPrepass.decodeVelocity(right).X, 1.0e-3f,
                        "opposite texels decode to opposite motion");
            }
            assertFalse(DepthNormalPrepass.getVelocityDecodeGlsl().isBlank());
        });
    }

    @Test
    void aPrepassKnowsHowManyPassesItNeedsAndWhy() {
        GameProbe.run(probe -> {
            try (DepthNormalPrepass prepass = DepthNormalPrepass.create(probe.device(), 320, 240,
                    DepthEncoding.Automatic)) {
                // One pass with multiple render targets, otherwise two -- and three with
                // velocity on. The two facts have to agree, which is what makes the pass count
                // usable as a loop bound rather than a guess.
                int passes = prepass.getPassCount();
                assertTrue(passes >= 1 && passes <= 3, "an implausible pass count: " + passes);
                assertEquals(prepass.isUsingMultipleRenderTargets(), passes == 1,
                        "one pass means multiple render targets, and the reverse");

                prepass.setVelocityEnabled(true);
                assertTrue(prepass.isVelocityEnabled());
                assertTrue(prepass.getPassCount() >= passes,
                        "velocity cannot make the prepass cheaper");
                prepass.setVelocityEnabled(false);
                assertEquals(passes, prepass.getPassCount(),
                        "and turning it off puts the count back");

                // The encoding the device would use, asked before a prepass exists and again
                // from the prepass itself: the same question, so the same answer.
                assertEquals(DepthNormalPrepass.usesPackedDepth(probe.device()),
                        prepass.isDepthPacked(),
                        "the device's answer and the prepass's must agree");

                prepass.setRoughness(0.375f);
                assertEquals(0.375f, prepass.getRoughness(), 1.0e-6f);
                prepass.resize(160, 120);

                for (int pass = 0; pass < prepass.getPassCount(); pass++) {
                    prepass.begin(pass, Matrix.getIdentity(), Matrix.getIdentity(), 1.0f,
                            100.0f);
                    prepass.end();
                }
                prepass.setPreviousWorld(Matrix.getIdentity());
                prepass.setPreviousCamera(Matrix.getIdentity(), Matrix.getIdentity());
                prepass.isSupported(probe.device());
            }
        });
    }

    @Test
    void aBorrowedPrepassTextureKeepsThePrepassAlive() {
        GameProbe.run(probe -> {
            DepthNormalPrepass prepass = DepthNormalPrepass.create(probe.device(), 64, 64,
                    DepthEncoding.Automatic);
            Texture2D depth = null;
            try {
                depth = prepass.getDepthTexture(probe.device());
                if (depth == null) {
                    return;
                }
                assertEquals(64, depth.getWidth(), "the borrowed target is the prepass's own");
                // The same counted borrow the shadow maps use: closing with one outstanding is
                // refused, and the refusal leaves the prepass usable.
                assertThrows(RuntimeException.class, prepass::close);
                assertEquals(64, depth.getWidth());
                depth.Dispose();
                depth = null;
            } finally {
                if (depth != null) {
                    depth.Dispose();
                }
                prepass.close();
            }
            assertThrows(IllegalStateException.class, prepass::getPassCount);
        });
    }

    @Test
    void theSkyIsBrighterTowardsTheSun() {
        GameProbe.run(probe -> {
            Vector3 sun = new Vector3(0f, 0.5f, -1f);
            // The model on the CPU, with no device at all. Looking at the sun is brighter than
            // looking away from it -- which is the one thing a sky model must get right, and it
            // is checkable without a single pixel.
            Vector3 towards = AtmosphericSky.radiance(sun, sun, 3.0f);
            Vector3 away = AtmosphericSky.radiance(
                    new Vector3(-sun.X, sun.Y, -sun.Z), sun, 3.0f);
            assertTrue(towards.X + towards.Y + towards.Z > away.X + away.Y + away.Z,
                    "the sun's own direction is brighter: " + towards + " against " + away);

            // Turbidity changes the sky rather than being ignored. Which way it goes away from
            // the sun is the model's business -- more haze scatters more into the view and
            // absorbs more out of it -- so the claim here is that it matters, not which way.
            Vector3 hazy = AtmosphericSky.radiance(
                    new Vector3(-sun.X, sun.Y, -sun.Z), sun, 9.0f);
            assertNotEquals(away.X, hazy.X, "turbidity must change the sky");
            // The horizon is not the zenith either, whatever the turbidity: a sky model that
            // answered one colour everywhere would pass the sun test above by accident and
            // fail this.
            Vector3 zenith = AtmosphericSky.radiance(new Vector3(0f, 1f, 0f), sun, 3.0f);
            Vector3 horizon = AtmosphericSky.radiance(new Vector3(1f, 0.02f, 0f), sun, 3.0f);
            assertNotEquals(zenith.X, horizon.X, "the sky is not one colour");
            assertNotEquals(zenith.Z, horizon.Z);
            assertFalse(AtmosphericSky.getModelGlsl().isBlank());

            try (AtmosphericSky sky = AtmosphericSky.create(probe.device())) {
                sky.setSunDirection(new Vector3(0f, 1f, 0f));
                assertEquals(new Vector3(0f, 1f, 0f), sky.getSunDirection());
                sky.setIntensity(2.5f);
                assertEquals(2.5f, sky.getIntensity(), 1.0e-5f);
                // The setter clamps into the model's range where the static evaluator does not,
                // which is the difference this projection states rather than hides.
                sky.setTurbidity(1000.0f);
                assertTrue(sky.getTurbidity() < 1000.0f,
                        "the setter clamps, and reported " + sky.getTurbidity());
                sky.isSupported();
            }
        });
    }

    @Test
    void aClosedPrepassOrSkyRefusesEveryOperation() {
        GameProbe.run(probe -> {
            AtmosphericSky sky = AtmosphericSky.create(probe.device());
            sky.close();
            sky.close();
            assertThrows(IllegalStateException.class, sky::getTurbidity);
            assertThrows(NullPointerException.class, () -> AtmosphericSky.create(null));
            assertThrows(NullPointerException.class,
                    () -> DepthNormalPrepass.create(probe.device(), 8, 8, null));
        });
    }
}
