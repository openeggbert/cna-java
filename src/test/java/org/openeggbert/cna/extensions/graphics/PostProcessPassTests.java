package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Vector2;
import Microsoft.Xna.Framework.Vector3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The post-process passes, against the live runtime.
 *
 * <p><strong>What this can and cannot say.</strong> Every pass needs a real device to compile on,
 * so the suite runs inside a game -- and this renderer compiles none of them, which is a fact the
 * tests report rather than assert around. What is fully checkable is everything a game's own
 * settings screen depends on: which values a pass keeps, which it corrects, and which it refuses
 * outright. CNA documents a different rule for almost every one, and the difference between
 * <em>clamped</em>, <em>floored</em> and <em>ignored</em> is exactly the thing a projection can
 * get wrong invisibly.
 *
 * <p>The pure helpers each pass exposes -- the tonemapping curve, the bloom extraction, the
 * circle of confusion, the contact-shadow ray test -- are checked as arithmetic, because that is
 * what they are.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class PostProcessPassTests {

    @Test
    void everyPassCreatesNamesItselfAndClosesTwice() {
        GameProbe.run(probe -> {
            List<PostProcessPass> passes = List.of(
                    BloomPass.create(probe.device()),
                    TonemapPass.create(probe.device()),
                    FxaaPass.create(probe.device()),
                    FilmGrainPass.create(probe.device()),
                    ChromaticAberrationPass.create(probe.device()),
                    LensFlarePass.create(probe.device()),
                    MotionBlurPass.create(probe.device()),
                    LightShaftPass.create(probe.device()),
                    HeightFogPass.create(probe.device()),
                    VolumetricFogPass.create(probe.device()),
                    DepthOfFieldPass.create(probe.device()),
                    SsaoPass.create(probe.device()),
                    SsrPass.create(probe.device()),
                    ContactShadowPass.create(probe.device()),
                    AerialPerspectivePass.create(probe.device()),
                    BlitPass.create(probe.device()));
            try {
                for (PostProcessPass pass : passes) {
                    // The name is what a pass timing is reported under, so a game can match a
                    // number to the object that produced it. Every pass must have one.
                    assertFalse(pass.getName().isBlank(),
                            pass.getClass().getSimpleName() + " has no name");
                    // Support is a question about the renderer, so it takes the device. Asking
                    // must not fail whatever the answer is.
                    pass.isSupported(probe.device());
                }
                // And the names are distinct, or a timing list would be unreadable.
                assertEquals(passes.size(),
                        passes.stream().map(PostProcessPass::getName).distinct().count(),
                        "two passes share a name");
            } finally {
                passes.forEach(PostProcessPass::close);
                passes.forEach(PostProcessPass::close);
            }
            PostProcessPass closed = BlitPass.create(probe.device());
            closed.close();
            assertThrows(IllegalStateException.class, closed::getName);
            assertThrows(NullPointerException.class, () -> BloomPass.create(null));
        });
    }

    @Test
    void aClampedSettingIsClampedAndAGuardedOneIsIgnored() {
        GameProbe.run(probe -> {
            // Three different rules, three different observable behaviours, and CNA states which
            // is which per field. A projection that treated them all the same would pass a
            // round-trip test and fail every one of these.
            try (MotionBlurPass blur = MotionBlurPass.create(probe.device())) {
                blur.setStrength(5.0f);
                assertEquals(1.0f, blur.getStrength(), "strength clamps to one");
                blur.setStrength(-5.0f);
                assertEquals(0.0f, blur.getStrength(), "and up to zero");
                blur.setMaxDistance(5.0f);
                assertEquals(0.25f, blur.getMaxDistance(),
                        "the distance beside it has a different bound");
            }
            try (ChromaticAberrationPass aberration =
                         ChromaticAberrationPass.create(probe.device())) {
                aberration.setStrength(1.0f);
                assertEquals(0.1f, aberration.getStrength(), 1.0e-6f);
            }
            try (SsrPass ssr = SsrPass.create(probe.device())) {
                ssr.setEdgeFade(1.0f);
                assertEquals(0.5f, ssr.getEdgeFade(), 1.0e-6f, "edge fade clamps at a half");
                ssr.setRoughnessBlur(1.0f);
                assertEquals(0.25f, ssr.getRoughnessBlur(), 1.0e-6f,
                        "and the roughness blur beside it at a quarter");

                // A guarded setter leaves the previous value in place rather than clamping.
                float distance = ssr.getMaxDistance();
                assertTrue(distance > 0f, "the default trace has a distance");
                ssr.setMaxDistance(0.0f);
                assertEquals(distance, ssr.getMaxDistance(),
                        "a zero distance is ignored, not stored");
                ssr.setMaxDistance(12.5f);
                assertEquals(12.5f, ssr.getMaxDistance(), 1.0e-5f, "a good value is stored");
            }
            try (VolumetricFogPass fog = VolumetricFogPass.create(probe.device())) {
                // The one two-sided clamp with a negative lower bound: scattering runs from
                // fully backward to fully forward and the poles are singular.
                fog.setAnisotropy(5.0f);
                assertEquals(0.95f, fog.getAnisotropy(), 1.0e-6f);
                fog.setAnisotropy(-5.0f);
                assertEquals(-0.95f, fog.getAnisotropy(), 1.0e-6f);

                // Zero density is a legitimate setting; a negative one is not, and is ignored.
                fog.setDensity(0.0f);
                assertEquals(0.0f, fog.getDensity());
                fog.setDensity(-1.0f);
                assertEquals(0.0f, fog.getDensity(), "a negative density is ignored");

                // The range beside it rejects zero as well, because a zero range has no volume.
                float range = fog.getRange();
                fog.setRange(0.0f);
                assertEquals(range, fog.getRange(), "a zero range is ignored");
            }
            try (HeightFogPass fog = HeightFogPass.create(probe.device())) {
                // Stored as given, deliberately: a fog base below the origin is legitimate.
                fog.setBaseHeight(-40.0f);
                assertEquals(-40.0f, fog.getBaseHeight());
                fog.setColor(new Vector3(0.2f, 0.3f, 0.4f));
                assertEquals(new Vector3(0.2f, 0.3f, 0.4f), fog.getColor());
            }
            try (AerialPerspectivePass air = AerialPerspectivePass.create(probe.device())) {
                // Floored at one rather than zero: turbidity is a ratio against a perfectly
                // clear atmosphere, so below one would be clearer than vacuum.
                air.setTurbidity(0.0f);
                assertEquals(1.0f, air.getTurbidity(), 1.0e-6f);
                air.setScaleHeight(0.0f);
                assertEquals(0.001f, air.getScaleHeight(), 1.0e-7f,
                        "the air-mass integral divides by it");
            }
            try (DecalPass decal = DecalPass.create(probe.device())) {
                decal.setOpacity(4.0f);
                assertEquals(1.0f, decal.getOpacity());
                decal.setMaxSlopeAngle(10.0f);
                assertEquals((float) (Math.PI / 2.0), decal.getMaxSlopeAngle(), 1.0e-5f,
                        "a decal cannot project past perpendicular");
            }
        });
    }

    @Test
    void aQualityPresetPicksMoreOfEverythingAsItRises() {
        GameProbe.run(probe -> {
            assertTrue(BloomPass.getIterationsForQuality(RenderQuality.Ultra)
                            >= BloomPass.getIterationsForQuality(RenderQuality.Low),
                    "a higher preset blurs at least as deep");
            assertTrue(SsaoPass.getSampleCountForQuality(RenderQuality.Ultra)
                            > SsaoPass.getSampleCountForQuality(RenderQuality.Low),
                    "and samples more");
            // FXAA is the other way round: a lower threshold touches more edges, so quality
            // means a *smaller* number, which is the kind of thing that gets inverted.
            assertTrue(FxaaPass.getEdgeThresholdForQuality(RenderQuality.Ultra)
                            < FxaaPass.getEdgeThresholdForQuality(RenderQuality.Low),
                    "a higher preset anti-aliases a finer edge");
            assertFalse(FxaaPass.getFragmentGlsl().isBlank());
            assertFalse(SsaoPass.getOcclusionGlsl(false).isBlank());
            assertNotEquals(SsaoPass.getOcclusionGlsl(false), SsaoPass.getOcclusionGlsl(true),
                    "the packed-depth variant is a different shader");
        });
    }

    @Test
    void theTonemapCurveDoesWhatEachOperatorIsFor() {
        GameProbe.run(probe -> {
            // None clips: everything above one is one, which is the whole reason to tonemap.
            assertEquals(1.0f,
                    TonemapPass.tonemapChannel(TonemappingMode.None, 8.0f, 1.0f, 1.0f), 1.0e-5f);
            // Reinhard does not: it compresses, so a bright value stays below one and stays
            // distinguishable from a brighter one.
            float reinhardFour = TonemapPass.tonemapChannel(
                    TonemappingMode.Reinhard, 4.0f, 1.0f, 1.0f);
            float reinhardEight = TonemapPass.tonemapChannel(
                    TonemappingMode.Reinhard, 8.0f, 1.0f, 1.0f);
            assertTrue(reinhardFour < 1.0f && reinhardEight < 1.0f, "Reinhard never clips");
            assertTrue(reinhardEight > reinhardFour, "and stays monotonic");

            // Exposure multiplies before the curve, so more exposure is never less light.
            assertTrue(TonemapPass.tonemapChannel(TonemappingMode.Aces, 1.0f, 2.0f, 1.0f)
                    > TonemapPass.tonemapChannel(TonemappingMode.Aces, 1.0f, 0.5f, 1.0f));
            // And zero in is zero out for every operator, or a black frame would not be black.
            for (TonemappingMode mode : TonemappingMode.values()) {
                assertEquals(0.0f, TonemapPass.tonemapChannel(mode, 0.0f, 1.0f, 1.0f), 1.0e-5f,
                        mode + " turns black into something else");
            }
            assertThrows(NullPointerException.class,
                    () -> TonemapPass.tonemapChannel(null, 1f, 1f, 1f));
        });
    }

    @Test
    void thePureHelpersAreTheArithmeticTheyClaimToBe() {
        GameProbe.run(probe -> {
            // Bloom's bright pass: nothing below the threshold contributes, and above it the
            // contribution rises.
            assertEquals(0.0f, BloomPass.extractChannel(0.5f, 1.0f), 1.0e-6f);
            assertTrue(BloomPass.extractChannel(2.0f, 1.0f)
                    > BloomPass.extractChannel(1.5f, 1.0f));

            // Depth of field: nothing at the focus distance blurs, and further away blurs more.
            assertEquals(0.0f,
                    DepthOfFieldPass.getCircleOfConfusionMillimetres(10f, 10f, 0.05f, 2.8f),
                    1.0e-5f, "what is in focus is not blurred");
            float near = DepthOfFieldPass.getCircleOfConfusionMillimetres(12f, 10f, 0.05f, 2.8f);
            float far = DepthOfFieldPass.getCircleOfConfusionMillimetres(40f, 10f, 0.05f, 2.8f);
            assertTrue(far > near && near > 0f,
                    "further out of focus is blurrier: " + near + " then " + far);
            // A wider aperture -- a smaller f-number -- blurs more, which is the photography.
            assertTrue(DepthOfFieldPass.getCircleOfConfusionMillimetres(40f, 10f, 0.05f, 1.4f)
                    > far, "a wider aperture has less depth of field");

            // Contact shadows: the ray test, and the combination that keeps two shadows from
            // double-darkening.
            // A ray just behind the surface is occluded; one in front is not; and one far
            // behind a thin surface is not either, because it has passed through and out the
            // back. That last case is what `thickness` is for and it is the one a naive
            // implementation gets wrong.
            assertTrue(ContactShadowPass.isOccluded(6.0f, 5.0f, 0.01f, 2.0f),
                    "a ray just behind a surface is occluded");
            assertFalse(ContactShadowPass.isOccluded(4.0f, 5.0f, 0.01f, 2.0f),
                    "and one in front of it is not");
            assertFalse(ContactShadowPass.isOccluded(50.0f, 5.0f, 0.01f, 2.0f),
                    "nor is one far behind a two-unit surface");
            assertEquals(1.0f, ContactShadowPass.combineVisibility(1.0f, 1.0f), 1.0e-6f,
                    "two clear visibilities stay clear");
            assertEquals(0.0f, ContactShadowPass.combineVisibility(0.0f, 1.0f), 1.0e-6f,
                    "and either one alone can shadow");
            assertEquals(0.0f, ContactShadowPass.combineVisibility(1.0f, 0.0f), 1.0e-6f);
            assertFalse(ContactShadowPass.getOcclusionTestGlsl().isBlank());

            // Height fog: more distance and more density both mean more fog.
            float shallow = HeightFogPass.getOpticalDepth(0f, 0f, 10f, 0.02f, 0.1f, 0f);
            float deep = HeightFogPass.getOpticalDepth(0f, 0f, 100f, 0.02f, 0.1f, 0f);
            assertTrue(deep > shallow && shallow > 0f,
                    "further through fog is more fog: " + shallow + " then " + deep);
            assertTrue(HeightFogPass.getOpticalDepth(0f, 0f, 10f, 0.2f, 0.1f, 0f) > shallow,
                    "denser fog is more fog");
            // Climbing out of it is less fog than staying in it, which is what "height" fog is.
            assertTrue(HeightFogPass.getOpticalDepth(0f, 1f, 10f, 0.02f, 0.1f, 0f) < shallow,
                    "a ray that climbs leaves the fog behind");

            // Aerial perspective: more distance is more air, and more air is less light through.
            Vector3 along = new Vector3(1f, 0f, 0f);
            float nearAir = AerialPerspectivePass.getAirMassForDistance(along, 100f, 1000f);
            float farAir = AerialPerspectivePass.getAirMassForDistance(along, 10000f, 1000f);
            assertTrue(farAir > nearAir && nearAir > 0f);
            Vector3 clear = AerialPerspectivePass.getTransmittance(1.0f, nearAir);
            Vector3 hazy = AerialPerspectivePass.getTransmittance(1.0f, farAir);
            assertTrue(hazy.X < clear.X, "more air lets less light through");
            assertTrue(clear.X <= 1.0f && hazy.X >= 0.0f, "transmittance is a fraction");
            // Blue scatters most, so red survives further -- which is why distance looks blue.
            assertTrue(hazy.X > hazy.Z,
                    "red survives the haze better than blue: " + hazy);
        });
    }

    @Test
    void theSettingsThatAreNotNumbersRoundTripToo() {
        GameProbe.run(probe -> {
            try (TonemapPass tonemap = TonemapPass.create(probe.device())) {
                tonemap.setMode(TonemappingMode.Uncharted2);
                assertEquals(TonemappingMode.Uncharted2, tonemap.getMode());
                tonemap.setDebandEnabled(true);
                assertTrue(tonemap.isDebandEnabled());
                assertThrows(NullPointerException.class, () -> tonemap.setMode(null));
            }
            try (SsaoPass ssao = SsaoPass.create(probe.device())) {
                ssao.setHalfResolution(true);
                assertTrue(ssao.isHalfResolution());
                ssao.setSampleCount(24);
                assertEquals(24, ssao.getSampleCount());
                // The kernel is the hemisphere the shader samples, and every offset is inside
                // the unit sphere -- which is what makes it a hemisphere rather than noise.
                List<Vector3> kernel = ssao.getKernel();
                assertFalse(kernel.isEmpty());
                for (Vector3 offset : kernel) {
                    assertTrue(offset.Length() <= 1.0001f, offset + " leaves the unit sphere");
                }
                ssao.resetTargets();
            }
            try (LightShaftPass shafts = LightShaftPass.create(probe.device())) {
                shafts.setLightScreenPosition(new Vector2(0.25f, 0.75f));
                assertEquals(new Vector2(0.25f, 0.75f), shafts.getLightScreenPosition());
                shafts.setDecay(4.0f);
                assertEquals(1.0f, shafts.getDecay());
            }
            try (ContactShadowPass contact = ContactShadowPass.create(probe.device())) {
                contact.setLightDirection(new Vector3(0f, -1f, 0f));
                assertEquals(new Vector3(0f, -1f, 0f), contact.getLightDirection());
                contact.setStepCount(12);
                assertEquals(12, contact.getStepCount());
                // The fallback reason exists whether or not it fell back; an empty string is
                // the honest answer for a pass that did not.
                contact.getFallbackReason();
            }
        });
    }
}
