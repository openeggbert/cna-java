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
