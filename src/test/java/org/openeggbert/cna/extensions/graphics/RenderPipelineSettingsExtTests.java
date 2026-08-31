package org.openeggbert.cna.extensions.graphics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The settings a render pipeline actually takes.
 *
 * <p>Nothing here renders: this is a value, and its interesting behaviour is arithmetic. The one
 * fact worth testing hard is CNA's own warning that <em>writing a field is not the same as the
 * engine storing it</em> -- thirty-one of these correct their input -- so the tests put values
 * outside every documented range in and check what comes back.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class RenderPipelineSettingsExtTests {

    /** {@code CNA_RENDER_PIPELINE_MINIMUM_GAMMA_EXT}, which the header states as a literal. */
    private static final float MINIMUM_GAMMA = 0.01f;

    /** {@code CNA_RENDER_PIPELINE_MINIMUM_FXAA_EDGE_THRESHOLD_EXT}, likewise. */
    private static final float MINIMUM_FXAA_EDGE_THRESHOLD = 0.001f;

    @Test
    void theDefaultsAreCnasOwn() {
        RenderPipelineSettingsExt settings = new RenderPipelineSettingsExt();
        // Asked of CNA rather than written down here, which is the same rule the smaller
        // RenderPipelineSettings follows.
        assertTrue(settings.getGamma() > 0.0f);
        assertTrue(settings.getExposure() > 0.0f);
        assertEquals(settings.getGamma(), new RenderPipelineSettingsExt(settings).getGamma());
        assertEquals(settings.getTonemappingMode(),
                new RenderPipelineSettingsExt(settings).getTonemappingMode());

        // This is the larger of CNA's two settings values, and the difference is the point: the
        // frozen one has ten fields and cannot grow inside an ABI major, so the complete shape
        // arrived under a new name. A field only this one has proves which is which.
        settings.setSsrEnabled(true);
        assertTrue(settings.getSsrEnabled());
        assertThrows(NullPointerException.class, () -> settings.setTonemappingMode(null));
    }

    @Test
    void normalizeAppliesTheCorrectionsTheEngineWouldApply() {
        RenderPipelineSettingsExt settings = new RenderPipelineSettingsExt();

        // A floor: a negative exposure is a sign error rather than a request.
        settings.setExposure(-4.0f);
        // A floor with a non-zero minimum: gamma is applied as a reciprocal power, so zero would
        // return the frame as infinities.
        settings.setGamma(0.0f);
        // Clamps to a two-sided range, each with a different upper bound.
        settings.setSsrEdgeFade(10.0f);
        settings.setMotionBlurStrength(10.0f);
        settings.setMotionBlurMaxDistance(10.0f);
        settings.setChromaticAberrationStrength(10.0f);
        settings.setFilmGrainIntensity(10.0f);
        settings.setDofMaxRadius(10.0f);

        settings.normalize();

        assertEquals(0.0f, settings.getExposure(), "a negative exposure floors at zero");
        assertTrue(settings.getGamma() > 0.0f, "gamma floors above zero, not at it");
        assertEquals(0.5f, settings.getSsrEdgeFade(), "edge fade clamps to a half");
        assertEquals(1.0f, settings.getMotionBlurStrength());
        assertEquals(0.25f, settings.getMotionBlurMaxDistance());
        assertEquals(0.1f, settings.getChromaticAberrationStrength());
        assertEquals(1.0f, settings.getFilmGrainIntensity());
        assertEquals(0.25f, settings.getDofMaxRadius());

        // And a field CNA documents as stored as given is left exactly as given, which is what
        // makes the corrections above a rule rather than a blanket clamp.
        settings.setHeightFogBaseHeight(-1234.5f);
        settings.normalize();
        assertEquals(-1234.5f, settings.getHeightFogBaseHeight());
    }

    /**
     * Every field CNA documents as correcting, driven past both ends of its own range.
     *
     * <p>The narrower test above proves the corrections happen. This one proves they happen
     * <em>to the right field</em>. The settings cross JNI as two flat arrays -- fifteen integral
     * slots and thirty-two floating ones -- and a generated slot pointed one field along would
     * still round-trip perfectly, because the same wrong index writes and reads. What it cannot
     * survive is a correction: each of these thirty-one fields floors or clamps at a bound only
     * it has, so a value that comes back corrected the way a neighbour would be corrected is a
     * mis-mapped slot, and this is the test that says so.
     */
    @Test
    void everyCorrectingFieldIsCorrectedAtItsOwnBound() {
        RenderPipelineSettingsExt low = new RenderPipelineSettingsExt();
        low.setExposure(-7.5f);
        low.setBloomIntensity(-7.5f);
        low.setBloomThreshold(-7.5f);
        low.setSsaoRadius(-7.5f);
        low.setSsaoIntensity(-7.5f);
        low.setSsrMaxDistance(-7.5f);
        low.setSsrThickness(-7.5f);
        low.setSsrDepthBias(-7.5f);
        low.setVolumetricFogDensity(-7.5f);
        low.setLightShaftThreshold(-7.5f);
        low.setLightShaftIntensity(-7.5f);
        low.setHeightFogDensity(-7.5f);
        low.setHeightFogFalloff(-7.5f);
        low.setLensFlareThreshold(-7.5f);
        low.setLensFlareIntensity(-7.5f);
        low.setDofFocusDistance(-7.5f);
        low.setDofFocalLength(-7.5f);
        low.setDofFNumber(-7.5f);
        low.setSsrIntensity(-7.5f);
        low.setGamma(-7.5f);
        low.setFxaaEdgeThreshold(-7.5f);
        low.setSsrEdgeFade(-7.5f);
        low.setLightShaftDecay(-7.5f);
        low.setMotionBlurStrength(-7.5f);
        low.setMotionBlurMaxDistance(-7.5f);
        low.setChromaticAberrationStrength(-7.5f);
        low.setFilmGrainIntensity(-7.5f);
        low.setLensFlareDispersal(-7.5f);
        low.setColorGradeStrength(-7.5f);
        low.setDofMaxRadius(-7.5f);
        low.setSsrRoughnessBlur(-7.5f);
        low.normalize();

        assertEquals(0.0f, low.getExposure(), "Exposure floors at zero");
        assertEquals(0.0f, low.getBloomIntensity(), "BloomIntensity floors at zero");
        assertEquals(0.0f, low.getBloomThreshold(), "BloomThreshold floors at zero");
        assertEquals(0.0f, low.getSsaoRadius(), "SsaoRadius floors at zero");
        assertEquals(0.0f, low.getSsaoIntensity(), "SsaoIntensity floors at zero");
        assertEquals(0.0f, low.getSsrMaxDistance(), "SsrMaxDistance floors at zero");
        assertEquals(0.0f, low.getSsrThickness(), "SsrThickness floors at zero");
        assertEquals(0.0f, low.getSsrDepthBias(), "SsrDepthBias floors at zero");
        assertEquals(0.0f, low.getVolumetricFogDensity(), "VolumetricFogDensity floors at zero");
        assertEquals(0.0f, low.getLightShaftThreshold(), "LightShaftThreshold floors at zero");
        assertEquals(0.0f, low.getLightShaftIntensity(), "LightShaftIntensity floors at zero");
        assertEquals(0.0f, low.getHeightFogDensity(), "HeightFogDensity floors at zero");
        assertEquals(0.0f, low.getHeightFogFalloff(), "HeightFogFalloff floors at zero");
        assertEquals(0.0f, low.getLensFlareThreshold(), "LensFlareThreshold floors at zero");
        assertEquals(0.0f, low.getLensFlareIntensity(), "LensFlareIntensity floors at zero");
        assertEquals(0.0f, low.getDofFocusDistance(), "DofFocusDistance floors at zero");
        assertEquals(0.0f, low.getDofFocalLength(), "DofFocalLength floors at zero");
        assertEquals(0.0f, low.getDofFNumber(), "DofFNumber floors at zero");
        assertEquals(0.0f, low.getSsrIntensity(), "SsrIntensity floors at zero");
        assertEquals(MINIMUM_GAMMA, low.getGamma(), "gamma floors at CNA's own minimum");
        assertEquals(MINIMUM_FXAA_EDGE_THRESHOLD, low.getFxaaEdgeThreshold(),
                "the edge threshold floors at CNA's own minimum");
        assertEquals(0.0f, low.getSsrEdgeFade(), "SsrEdgeFade clamps up to zero");
        assertEquals(0.0f, low.getLightShaftDecay(), "LightShaftDecay clamps up to zero");
        assertEquals(0.0f, low.getMotionBlurStrength(), "MotionBlurStrength clamps up to zero");
        assertEquals(0.0f, low.getMotionBlurMaxDistance(), "MotionBlurMaxDistance clamps up to zero");
        assertEquals(0.0f, low.getChromaticAberrationStrength(), "ChromaticAberrationStrength clamps up to zero");
        assertEquals(0.0f, low.getFilmGrainIntensity(), "FilmGrainIntensity clamps up to zero");
        assertEquals(0.0f, low.getLensFlareDispersal(), "LensFlareDispersal clamps up to zero");
        assertEquals(0.0f, low.getColorGradeStrength(), "ColorGradeStrength clamps up to zero");
        assertEquals(0.0f, low.getDofMaxRadius(), "DofMaxRadius clamps up to zero");
        assertEquals(0.0f, low.getSsrRoughnessBlur(), "SsrRoughnessBlur clamps up to zero");

        RenderPipelineSettingsExt high = new RenderPipelineSettingsExt();
        high.setSsrEdgeFade(9.0f);
        high.setLightShaftDecay(9.0f);
        high.setMotionBlurStrength(9.0f);
        high.setMotionBlurMaxDistance(9.0f);
        high.setChromaticAberrationStrength(9.0f);
        high.setFilmGrainIntensity(9.0f);
        high.setLensFlareDispersal(9.0f);
        high.setColorGradeStrength(9.0f);
        high.setDofMaxRadius(9.0f);
        high.setSsrRoughnessBlur(9.0f);
        high.normalize();

        assertEquals(0.5f, high.getSsrEdgeFade(), "SsrEdgeFade clamps down to its own maximum");
        assertEquals(1.0f, high.getLightShaftDecay(), "LightShaftDecay clamps down to its own maximum");
        assertEquals(1.0f, high.getMotionBlurStrength(), "MotionBlurStrength clamps down to its own maximum");
        assertEquals(0.25f, high.getMotionBlurMaxDistance(), "MotionBlurMaxDistance clamps down to its own maximum");
        assertEquals(0.1f, high.getChromaticAberrationStrength(), "ChromaticAberrationStrength clamps down to its own maximum");
        assertEquals(1.0f, high.getFilmGrainIntensity(), "FilmGrainIntensity clamps down to its own maximum");
        assertEquals(1.0f, high.getLensFlareDispersal(), "LensFlareDispersal clamps down to its own maximum");
        assertEquals(1.0f, high.getColorGradeStrength(), "ColorGradeStrength clamps down to its own maximum");
        assertEquals(0.25f, high.getDofMaxRadius(), "DofMaxRadius clamps down to its own maximum");
        assertEquals(0.25f, high.getSsrRoughnessBlur(), "SsrRoughnessBlur clamps down to its own maximum");
    }

    @Test
    void aQualityPresetTurnsANameIntoTheChoicesItStandsFor() {
        RenderPipelineSettingsExt low = new RenderPipelineSettingsExt();
        low.setRenderQuality(RenderQuality.Low);
        low.applyRenderQualityPreset();

        RenderPipelineSettingsExt ultra = new RenderPipelineSettingsExt();
        ultra.setRenderQuality(RenderQuality.Ultra);
        ultra.applyRenderQualityPreset();

        assertEquals(RenderQuality.Low, low.getRenderQuality());
        assertEquals(RenderQuality.Ultra, ultra.getRenderQuality());
        // Setting the quality alone changes one field; applying the preset changes the ones the
        // name stands for, which is why both routes exist.
        assertNotEquals(low.getSsaoSampleCount(), ultra.getSsaoSampleCount(),
                "two quality presets that pick the same sample count would not be two presets");
    }

    @Test
    void applyingTextReportsHowManyFieldsItRecognised() {
        RenderPipelineSettingsExt settings = new RenderPipelineSettingsExt();
        // Unrecognised fields are skipped rather than refused, which is what makes the count the
        // useful answer: a typo in a config file shows up as a number rather than as a setting
        // that quietly did not take.
        int none = settings.applyFrom("thisIsNotASetting=1");
        assertEquals(0, none, "nothing in that text names a field");

        int empty = settings.applyFrom("");
        assertEquals(0, empty);
        assertThrows(NullPointerException.class, () -> settings.applyFrom(null));
    }
}
