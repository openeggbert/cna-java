package org.openeggbert.cna.extensions.graphics;

import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Every setting CNA's extended render pipeline actually takes.
 *
 * <p>A CNA extension, and the one a pipeline consumes. {@link RenderPipelineSettings} is the
 * frozen ten-field value CNA cannot grow inside an ABI major; this is what its header calls
 * "the canonical RenderPipelineSettings in full", and every engine-layer route that takes
 * settings takes this one. A game configuring a pipeline wants this type.
 *
 * <p><strong>Writing a field is not the same as the engine storing it.</strong> Thirty-one of
 * these correct their input -- ten clamp to a range and twenty-one floor at a minimum -- and a
 * value written here holds whatever was put in it. {@link #normalize()} runs every field
 * through the same correction the engine applies, so a game can ask what will actually be
 * stored rather than assume. That is why it exists and why it is worth calling before showing
 * a settings screen back to a player.
 *
 * <p>Each field's documentation below is CNA's own, including which of them correct and how.
 */
public final class RenderPipelineSettingsExt {

    private boolean hdrEnabled;
    private TonemappingMode tonemappingMode;
    private boolean bloomEnabled;
    private int bloomIterations;
    private boolean ssaoEnabled;
    private TransparencyMode transparencyMode;
    private int ssaoSampleCount;
    private boolean ssrEnabled;
    private int ssrStepCount;
    private boolean colorGradeEnabled;
    private boolean dofEnabled;
    private boolean fxaaEnabled;
    private RenderQuality renderQuality;
    private ShadowQuality shadowQuality;
    private boolean shadowsEnabled;
    private float exposure;
    private float gamma;
    private float bloomIntensity;
    private float bloomThreshold;
    private float ssaoRadius;
    private float ssaoIntensity;
    private float ssrMaxDistance;
    private float ssrThickness;
    private float ssrDepthBias;
    private float ssrEdgeFade;
    private float volumetricFogDensity;
    private float lightShaftThreshold;
    private float lightShaftIntensity;
    private float lightShaftDecay;
    private float heightFogDensity;
    private float heightFogFalloff;
    private float heightFogBaseHeight;
    private float motionBlurStrength;
    private float motionBlurMaxDistance;
    private float chromaticAberrationStrength;
    private float filmGrainIntensity;
    private float lensFlareThreshold;
    private float lensFlareIntensity;
    private float lensFlareDispersal;
    private float colorGradeStrength;
    private float dofFocusDistance;
    private float dofFocalLength;
    private float dofFNumber;
    private float dofMaxRadius;
    private float ssrRoughnessBlur;
    private float ssrIntensity;
    private float fxaaEdgeThreshold;

    /** Creates the settings CNA itself defaults to. */
    public RenderPipelineSettingsExt() {
        GraphicsExtension.requireBackend();
        long[] integral = new long[15];
        float[] floating = new float[32];
        GraphicsExtension.check("RenderPipelineSettingsExt", NativeEngineLayerRoutes
                .renderPipelineSettingsExtInit(new byte[4], integral, floating));
        read(integral, floating);
    }

    /** Copies another value. */
    public RenderPipelineSettingsExt(RenderPipelineSettingsExt value) {
        Objects.requireNonNull(value, "value");
        hdrEnabled = value.hdrEnabled;
        tonemappingMode = value.tonemappingMode;
        bloomEnabled = value.bloomEnabled;
        bloomIterations = value.bloomIterations;
        ssaoEnabled = value.ssaoEnabled;
        transparencyMode = value.transparencyMode;
        ssaoSampleCount = value.ssaoSampleCount;
        ssrEnabled = value.ssrEnabled;
        ssrStepCount = value.ssrStepCount;
        colorGradeEnabled = value.colorGradeEnabled;
        dofEnabled = value.dofEnabled;
        fxaaEnabled = value.fxaaEnabled;
        renderQuality = value.renderQuality;
        shadowQuality = value.shadowQuality;
        shadowsEnabled = value.shadowsEnabled;
        exposure = value.exposure;
        gamma = value.gamma;
        bloomIntensity = value.bloomIntensity;
        bloomThreshold = value.bloomThreshold;
        ssaoRadius = value.ssaoRadius;
        ssaoIntensity = value.ssaoIntensity;
        ssrMaxDistance = value.ssrMaxDistance;
        ssrThickness = value.ssrThickness;
        ssrDepthBias = value.ssrDepthBias;
        ssrEdgeFade = value.ssrEdgeFade;
        volumetricFogDensity = value.volumetricFogDensity;
        lightShaftThreshold = value.lightShaftThreshold;
        lightShaftIntensity = value.lightShaftIntensity;
        lightShaftDecay = value.lightShaftDecay;
        heightFogDensity = value.heightFogDensity;
        heightFogFalloff = value.heightFogFalloff;
        heightFogBaseHeight = value.heightFogBaseHeight;
        motionBlurStrength = value.motionBlurStrength;
        motionBlurMaxDistance = value.motionBlurMaxDistance;
        chromaticAberrationStrength = value.chromaticAberrationStrength;
        filmGrainIntensity = value.filmGrainIntensity;
        lensFlareThreshold = value.lensFlareThreshold;
        lensFlareIntensity = value.lensFlareIntensity;
        lensFlareDispersal = value.lensFlareDispersal;
        colorGradeStrength = value.colorGradeStrength;
        dofFocusDistance = value.dofFocusDistance;
        dofFocalLength = value.dofFocalLength;
        dofFNumber = value.dofFNumber;
        dofMaxRadius = value.dofMaxRadius;
        ssrRoughnessBlur = value.ssrRoughnessBlur;
        ssrIntensity = value.ssrIntensity;
        fxaaEdgeThreshold = value.fxaaEdgeThreshold;
    }

    /**
     * HDREnabled. Stored as given -- the canonical setter corrects nothing here.
     *
     * @return the stored value
     */
    public boolean getHdrEnabled() {
        return hdrEnabled;
    }

    /**
     * Sets HDREnabled. Stored as given -- the canonical setter corrects nothing here.
     *
     * @param value the value to store here, uncorrected
     */
    public void setHdrEnabled(boolean value) {
        hdrEnabled = value;
    }

    /**
     * TonemappingMode. Stored as given -- the canonical setter corrects nothing here.
     *
     * @return the stored value
     */
    public TonemappingMode getTonemappingMode() {
        return tonemappingMode;
    }

    /**
     * Sets TonemappingMode. Stored as given -- the canonical setter corrects nothing here.
     *
     * @param value the value to store here, uncorrected
     */
    public void setTonemappingMode(TonemappingMode value) {
        tonemappingMode = Objects.requireNonNull(value, "value");
    }

    /**
     * BloomEnabled. Stored as given -- the canonical setter corrects nothing here.
     *
     * @return the stored value
     */
    public boolean getBloomEnabled() {
        return bloomEnabled;
    }

    /**
     * Sets BloomEnabled. Stored as given -- the canonical setter corrects nothing here.
     *
     * @param value the value to store here, uncorrected
     */
    public void setBloomEnabled(boolean value) {
        bloomEnabled = value;
    }

    /**
     * BloomIterations. Stored as given -- the canonical setter corrects nothing here.
     *
     * @return the stored value
     */
    public int getBloomIterations() {
        return bloomIterations;
    }

    /**
     * Sets BloomIterations. Stored as given -- the canonical setter corrects nothing here.
     *
     * @param value the value to store here, uncorrected
     */
    public void setBloomIterations(int value) {
        bloomIterations = value;
    }

    /**
     * SSAOEnabled. Stored as given -- the canonical setter corrects nothing here.
     *
     * @return the stored value
     */
    public boolean getSsaoEnabled() {
        return ssaoEnabled;
    }

    /**
     * Sets SSAOEnabled. Stored as given -- the canonical setter corrects nothing here.
     *
     * @param value the value to store here, uncorrected
     */
    public void setSsaoEnabled(boolean value) {
        ssaoEnabled = value;
    }

    /**
     * TransparencyMode. Stored as given -- the canonical setter corrects nothing here.
     *
     * @return the stored value
     */
    public TransparencyMode getTransparencyMode() {
        return transparencyMode;
    }

    /**
     * Sets TransparencyMode. Stored as given -- the canonical setter corrects nothing here.
     *
     * @param value the value to store here, uncorrected
     */
    public void setTransparencyMode(TransparencyMode value) {
        transparencyMode = Objects.requireNonNull(value, "value");
    }

    /**
     * SSAOSampleCount. Stored as given -- the canonical setter corrects nothing here.
     *
     * @return the stored value
     */
    public int getSsaoSampleCount() {
        return ssaoSampleCount;
    }

    /**
     * Sets SSAOSampleCount. Stored as given -- the canonical setter corrects nothing here.
     *
     * @param value the value to store here, uncorrected
     */
    public void setSsaoSampleCount(int value) {
        ssaoSampleCount = value;
    }

    /**
     * SSREnabled. Stored as given -- the canonical setter corrects nothing here.
     *
     * @return the stored value
     */
    public boolean getSsrEnabled() {
        return ssrEnabled;
    }

    /**
     * Sets SSREnabled. Stored as given -- the canonical setter corrects nothing here.
     *
     * @param value the value to store here, uncorrected
     */
    public void setSsrEnabled(boolean value) {
        ssrEnabled = value;
    }

    /**
     * SSRStepCount. Stored as given -- the canonical setter corrects nothing here.
     *
     * @return the stored value
     */
    public int getSsrStepCount() {
        return ssrStepCount;
    }

    /**
     * Sets SSRStepCount. Stored as given -- the canonical setter corrects nothing here.
     *
     * @param value the value to store here, uncorrected
     */
    public void setSsrStepCount(int value) {
        ssrStepCount = value;
    }

    /**
     * ColorGradeEnabled. Stored as given -- the canonical setter corrects nothing here.
     *
     * @return the stored value
     */
    public boolean getColorGradeEnabled() {
        return colorGradeEnabled;
    }

    /**
     * Sets ColorGradeEnabled. Stored as given -- the canonical setter corrects nothing here.
     *
     * @param value the value to store here, uncorrected
     */
    public void setColorGradeEnabled(boolean value) {
        colorGradeEnabled = value;
    }

    /**
     * DOFEnabled. Stored as given -- the canonical setter corrects nothing here.
     *
     * @return the stored value
     */
    public boolean getDofEnabled() {
        return dofEnabled;
    }

    /**
     * Sets DOFEnabled. Stored as given -- the canonical setter corrects nothing here.
     *
     * @param value the value to store here, uncorrected
     */
    public void setDofEnabled(boolean value) {
        dofEnabled = value;
    }

    /**
     * FXAAEnabled. Stored as given -- the canonical setter corrects nothing here.
     *
     * @return the stored value
     */
    public boolean getFxaaEnabled() {
        return fxaaEnabled;
    }

    /**
     * Sets FXAAEnabled. Stored as given -- the canonical setter corrects nothing here.
     *
     * @param value the value to store here, uncorrected
     */
    public void setFxaaEnabled(boolean value) {
        fxaaEnabled = value;
    }

    /**
     * RenderQuality. Stored as given -- the canonical setter corrects nothing here.
     *
     * @return the stored value
     */
    public RenderQuality getRenderQuality() {
        return renderQuality;
    }

    /**
     * Sets RenderQuality. Stored as given -- the canonical setter corrects nothing here.
     *
     * @param value the value to store here, uncorrected
     */
    public void setRenderQuality(RenderQuality value) {
        renderQuality = Objects.requireNonNull(value, "value");
    }

    /**
     * ShadowQuality. Stored as given -- the canonical setter corrects nothing here.
     *
     * @return the stored value
     */
    public ShadowQuality getShadowQuality() {
        return shadowQuality;
    }

    /**
     * Sets ShadowQuality. Stored as given -- the canonical setter corrects nothing here.
     *
     * @param value the value to store here, uncorrected
     */
    public void setShadowQuality(ShadowQuality value) {
        shadowQuality = Objects.requireNonNull(value, "value");
    }

    /**
     * ShadowsEnabled. Stored as given -- the canonical setter corrects nothing here.
     *
     * @return the stored value
     */
    public boolean getShadowsEnabled() {
        return shadowsEnabled;
    }

    /**
     * Sets ShadowsEnabled. Stored as given -- the canonical setter corrects nothing here.
     *
     * @param value the value to store here, uncorrected
     */
    public void setShadowsEnabled(boolean value) {
        shadowsEnabled = value;
    }

    /**
     * Exposure. Floored at 0.0 when it reaches the engine; a negative value here is a sign
     * error rather than a look.
     *
     * @return the stored value
     */
    public float getExposure() {
        return exposure;
    }

    /**
     * Sets Exposure. Floored at 0.0 when it reaches the engine; a negative value here is a sign
     * error rather than a look.
     *
     * @param value the value to store here, uncorrected
     */
    public void setExposure(float value) {
        exposure = value;
    }

    /**
     * Gamma. Floored at `CNA_RENDER_PIPELINE_MINIMUM_GAMMA_EXT`; gamma is applied as a
     * reciprocal power, so zero is a division by zero.
     *
     * @return the stored value
     */
    public float getGamma() {
        return gamma;
    }

    /**
     * Sets Gamma. Floored at `CNA_RENDER_PIPELINE_MINIMUM_GAMMA_EXT`; gamma is applied as a
     * reciprocal power, so zero is a division by zero.
     *
     * @param value the value to store here, uncorrected
     */
    public void setGamma(float value) {
        gamma = value;
    }

    /**
     * BloomIntensity. Floored at 0.0 when it reaches the engine; a negative value here is a
     * sign error rather than a look.
     *
     * @return the stored value
     */
    public float getBloomIntensity() {
        return bloomIntensity;
    }

    /**
     * Sets BloomIntensity. Floored at 0.0 when it reaches the engine; a negative value here is
     * a sign error rather than a look.
     *
     * @param value the value to store here, uncorrected
     */
    public void setBloomIntensity(float value) {
        bloomIntensity = value;
    }

    /**
     * BloomThreshold. Floored at 0.0 when it reaches the engine; a negative value here is a
     * sign error rather than a look.
     *
     * @return the stored value
     */
    public float getBloomThreshold() {
        return bloomThreshold;
    }

    /**
     * Sets BloomThreshold. Floored at 0.0 when it reaches the engine; a negative value here is
     * a sign error rather than a look.
     *
     * @param value the value to store here, uncorrected
     */
    public void setBloomThreshold(float value) {
        bloomThreshold = value;
    }

    /**
     * SSAORadius. Floored at 0.0 when it reaches the engine; a negative value here is a sign
     * error rather than a look.
     *
     * @return the stored value
     */
    public float getSsaoRadius() {
        return ssaoRadius;
    }

    /**
     * Sets SSAORadius. Floored at 0.0 when it reaches the engine; a negative value here is a
     * sign error rather than a look.
     *
     * @param value the value to store here, uncorrected
     */
    public void setSsaoRadius(float value) {
        ssaoRadius = value;
    }

    /**
     * SSAOIntensity. Floored at 0.0 when it reaches the engine; a negative value here is a sign
     * error rather than a look.
     *
     * @return the stored value
     */
    public float getSsaoIntensity() {
        return ssaoIntensity;
    }

    /**
     * Sets SSAOIntensity. Floored at 0.0 when it reaches the engine; a negative value here is a
     * sign error rather than a look.
     *
     * @param value the value to store here, uncorrected
     */
    public void setSsaoIntensity(float value) {
        ssaoIntensity = value;
    }

    /**
     * SSRMaxDistance. Floored at 0.0 when it reaches the engine; a negative value here is a
     * sign error rather than a look.
     *
     * @return the stored value
     */
    public float getSsrMaxDistance() {
        return ssrMaxDistance;
    }

    /**
     * Sets SSRMaxDistance. Floored at 0.0 when it reaches the engine; a negative value here is
     * a sign error rather than a look.
     *
     * @param value the value to store here, uncorrected
     */
    public void setSsrMaxDistance(float value) {
        ssrMaxDistance = value;
    }

    /**
     * SSRThickness. Floored at 0.0 when it reaches the engine; a negative value here is a sign
     * error rather than a look.
     *
     * @return the stored value
     */
    public float getSsrThickness() {
        return ssrThickness;
    }

    /**
     * Sets SSRThickness. Floored at 0.0 when it reaches the engine; a negative value here is a
     * sign error rather than a look.
     *
     * @param value the value to store here, uncorrected
     */
    public void setSsrThickness(float value) {
        ssrThickness = value;
    }

    /**
     * SSRDepthBias. Floored at 0.0 when it reaches the engine; a negative value here is a sign
     * error rather than a look.
     *
     * @return the stored value
     */
    public float getSsrDepthBias() {
        return ssrDepthBias;
    }

    /**
     * Sets SSRDepthBias. Floored at 0.0 when it reaches the engine; a negative value here is a
     * sign error rather than a look.
     *
     * @param value the value to store here, uncorrected
     */
    public void setSsrDepthBias(float value) {
        ssrDepthBias = value;
    }

    /**
     * SSREdgeFade. Clamped to 0.0 through 0.5 when it reaches the engine.
     *
     * @return the stored value
     */
    public float getSsrEdgeFade() {
        return ssrEdgeFade;
    }

    /**
     * Sets SSREdgeFade. Clamped to 0.0 through 0.5 when it reaches the engine.
     *
     * @param value the value to store here, uncorrected
     */
    public void setSsrEdgeFade(float value) {
        ssrEdgeFade = value;
    }

    /**
     * VolumetricFogDensity. Floored at 0.0 when it reaches the engine; a negative value here is
     * a sign error rather than a look.
     *
     * @return the stored value
     */
    public float getVolumetricFogDensity() {
        return volumetricFogDensity;
    }

    /**
     * Sets VolumetricFogDensity. Floored at 0.0 when it reaches the engine; a negative value
     * here is a sign error rather than a look.
     *
     * @param value the value to store here, uncorrected
     */
    public void setVolumetricFogDensity(float value) {
        volumetricFogDensity = value;
    }

    /**
     * LightShaftThreshold. Floored at 0.0 when it reaches the engine; a negative value here is
     * a sign error rather than a look.
     *
     * @return the stored value
     */
    public float getLightShaftThreshold() {
        return lightShaftThreshold;
    }

    /**
     * Sets LightShaftThreshold. Floored at 0.0 when it reaches the engine; a negative value
     * here is a sign error rather than a look.
     *
     * @param value the value to store here, uncorrected
     */
    public void setLightShaftThreshold(float value) {
        lightShaftThreshold = value;
    }

    /**
     * LightShaftIntensity. Floored at 0.0 when it reaches the engine; a negative value here is
     * a sign error rather than a look.
     *
     * @return the stored value
     */
    public float getLightShaftIntensity() {
        return lightShaftIntensity;
    }

    /**
     * Sets LightShaftIntensity. Floored at 0.0 when it reaches the engine; a negative value
     * here is a sign error rather than a look.
     *
     * @param value the value to store here, uncorrected
     */
    public void setLightShaftIntensity(float value) {
        lightShaftIntensity = value;
    }

    /**
     * LightShaftDecay. Clamped to 0.0 through 1.0 when it reaches the engine.
     *
     * @return the stored value
     */
    public float getLightShaftDecay() {
        return lightShaftDecay;
    }

    /**
     * Sets LightShaftDecay. Clamped to 0.0 through 1.0 when it reaches the engine.
     *
     * @param value the value to store here, uncorrected
     */
    public void setLightShaftDecay(float value) {
        lightShaftDecay = value;
    }

    /**
     * HeightFogDensity. Floored at 0.0 when it reaches the engine; a negative value here is a
     * sign error rather than a look.
     *
     * @return the stored value
     */
    public float getHeightFogDensity() {
        return heightFogDensity;
    }

    /**
     * Sets HeightFogDensity. Floored at 0.0 when it reaches the engine; a negative value here
     * is a sign error rather than a look.
     *
     * @param value the value to store here, uncorrected
     */
    public void setHeightFogDensity(float value) {
        heightFogDensity = value;
    }

    /**
     * HeightFogFalloff. Floored at 0.0 when it reaches the engine; a negative value here is a
     * sign error rather than a look.
     *
     * @return the stored value
     */
    public float getHeightFogFalloff() {
        return heightFogFalloff;
    }

    /**
     * Sets HeightFogFalloff. Floored at 0.0 when it reaches the engine; a negative value here
     * is a sign error rather than a look.
     *
     * @param value the value to store here, uncorrected
     */
    public void setHeightFogFalloff(float value) {
        heightFogFalloff = value;
    }

    /**
     * HeightFogBaseHeight. Stored as given -- the canonical setter corrects nothing here.
     *
     * @return the stored value
     */
    public float getHeightFogBaseHeight() {
        return heightFogBaseHeight;
    }

    /**
     * Sets HeightFogBaseHeight. Stored as given -- the canonical setter corrects nothing here.
     *
     * @param value the value to store here, uncorrected
     */
    public void setHeightFogBaseHeight(float value) {
        heightFogBaseHeight = value;
    }

    /**
     * MotionBlurStrength. Clamped to 0.0 through 1.0 when it reaches the engine.
     *
     * @return the stored value
     */
    public float getMotionBlurStrength() {
        return motionBlurStrength;
    }

    /**
     * Sets MotionBlurStrength. Clamped to 0.0 through 1.0 when it reaches the engine.
     *
     * @param value the value to store here, uncorrected
     */
    public void setMotionBlurStrength(float value) {
        motionBlurStrength = value;
    }

    /**
     * MotionBlurMaxDistance. Clamped to 0.0 through 0.25 when it reaches the engine.
     *
     * @return the stored value
     */
    public float getMotionBlurMaxDistance() {
        return motionBlurMaxDistance;
    }

    /**
     * Sets MotionBlurMaxDistance. Clamped to 0.0 through 0.25 when it reaches the engine.
     *
     * @param value the value to store here, uncorrected
     */
    public void setMotionBlurMaxDistance(float value) {
        motionBlurMaxDistance = value;
    }

    /**
     * ChromaticAberrationStrength. Clamped to 0.0 through 0.1 when it reaches the engine.
     *
     * @return the stored value
     */
    public float getChromaticAberrationStrength() {
        return chromaticAberrationStrength;
    }

    /**
     * Sets ChromaticAberrationStrength. Clamped to 0.0 through 0.1 when it reaches the engine.
     *
     * @param value the value to store here, uncorrected
     */
    public void setChromaticAberrationStrength(float value) {
        chromaticAberrationStrength = value;
    }

    /**
     * FilmGrainIntensity. Clamped to 0.0 through 1.0 when it reaches the engine.
     *
     * @return the stored value
     */
    public float getFilmGrainIntensity() {
        return filmGrainIntensity;
    }

    /**
     * Sets FilmGrainIntensity. Clamped to 0.0 through 1.0 when it reaches the engine.
     *
     * @param value the value to store here, uncorrected
     */
    public void setFilmGrainIntensity(float value) {
        filmGrainIntensity = value;
    }

    /**
     * LensFlareThreshold. Floored at 0.0 when it reaches the engine; a negative value here is a
     * sign error rather than a look.
     *
     * @return the stored value
     */
    public float getLensFlareThreshold() {
        return lensFlareThreshold;
    }

    /**
     * Sets LensFlareThreshold. Floored at 0.0 when it reaches the engine; a negative value here
     * is a sign error rather than a look.
     *
     * @param value the value to store here, uncorrected
     */
    public void setLensFlareThreshold(float value) {
        lensFlareThreshold = value;
    }

    /**
     * LensFlareIntensity. Floored at 0.0 when it reaches the engine; a negative value here is a
     * sign error rather than a look.
     *
     * @return the stored value
     */
    public float getLensFlareIntensity() {
        return lensFlareIntensity;
    }

    /**
     * Sets LensFlareIntensity. Floored at 0.0 when it reaches the engine; a negative value here
     * is a sign error rather than a look.
     *
     * @param value the value to store here, uncorrected
     */
    public void setLensFlareIntensity(float value) {
        lensFlareIntensity = value;
    }

    /**
     * LensFlareDispersal. Clamped to 0.0 through 1.0 when it reaches the engine.
     *
     * @return the stored value
     */
    public float getLensFlareDispersal() {
        return lensFlareDispersal;
    }

    /**
     * Sets LensFlareDispersal. Clamped to 0.0 through 1.0 when it reaches the engine.
     *
     * @param value the value to store here, uncorrected
     */
    public void setLensFlareDispersal(float value) {
        lensFlareDispersal = value;
    }

    /**
     * ColorGradeStrength. Clamped to 0.0 through 1.0 when it reaches the engine.
     *
     * @return the stored value
     */
    public float getColorGradeStrength() {
        return colorGradeStrength;
    }

    /**
     * Sets ColorGradeStrength. Clamped to 0.0 through 1.0 when it reaches the engine.
     *
     * @param value the value to store here, uncorrected
     */
    public void setColorGradeStrength(float value) {
        colorGradeStrength = value;
    }

    /**
     * DOFFocusDistance. Floored at 0.0 when it reaches the engine; a negative value here is a
     * sign error rather than a look.
     *
     * @return the stored value
     */
    public float getDofFocusDistance() {
        return dofFocusDistance;
    }

    /**
     * Sets DOFFocusDistance. Floored at 0.0 when it reaches the engine; a negative value here
     * is a sign error rather than a look.
     *
     * @param value the value to store here, uncorrected
     */
    public void setDofFocusDistance(float value) {
        dofFocusDistance = value;
    }

    /**
     * DOFFocalLength. Floored at 0.0 when it reaches the engine; a negative value here is a
     * sign error rather than a look.
     *
     * @return the stored value
     */
    public float getDofFocalLength() {
        return dofFocalLength;
    }

    /**
     * Sets DOFFocalLength. Floored at 0.0 when it reaches the engine; a negative value here is
     * a sign error rather than a look.
     *
     * @param value the value to store here, uncorrected
     */
    public void setDofFocalLength(float value) {
        dofFocalLength = value;
    }

    /**
     * DOFFNumber. Floored at 0.0 when it reaches the engine; a negative value here is a sign
     * error rather than a look.
     *
     * @return the stored value
     */
    public float getDofFNumber() {
        return dofFNumber;
    }

    /**
     * Sets DOFFNumber. Floored at 0.0 when it reaches the engine; a negative value here is a
     * sign error rather than a look.
     *
     * @param value the value to store here, uncorrected
     */
    public void setDofFNumber(float value) {
        dofFNumber = value;
    }

    /**
     * DOFMaxRadius. Clamped to 0.0 through 0.25 when it reaches the engine.
     *
     * @return the stored value
     */
    public float getDofMaxRadius() {
        return dofMaxRadius;
    }

    /**
     * Sets DOFMaxRadius. Clamped to 0.0 through 0.25 when it reaches the engine.
     *
     * @param value the value to store here, uncorrected
     */
    public void setDofMaxRadius(float value) {
        dofMaxRadius = value;
    }

    /**
     * SSRRoughnessBlur. Clamped to 0.0 through 0.25 when it reaches the engine.
     *
     * @return the stored value
     */
    public float getSsrRoughnessBlur() {
        return ssrRoughnessBlur;
    }

    /**
     * Sets SSRRoughnessBlur. Clamped to 0.0 through 0.25 when it reaches the engine.
     *
     * @param value the value to store here, uncorrected
     */
    public void setSsrRoughnessBlur(float value) {
        ssrRoughnessBlur = value;
    }

    /**
     * SSRIntensity. Floored at 0.0 when it reaches the engine; a negative value here is a sign
     * error rather than a look.
     *
     * @return the stored value
     */
    public float getSsrIntensity() {
        return ssrIntensity;
    }

    /**
     * Sets SSRIntensity. Floored at 0.0 when it reaches the engine; a negative value here is a
     * sign error rather than a look.
     *
     * @param value the value to store here, uncorrected
     */
    public void setSsrIntensity(float value) {
        ssrIntensity = value;
    }

    /**
     * FXAAEdgeThresholdEXT. Floored at `CNA_RENDER_PIPELINE_MINIMUM_FXAA_EDGE_THRESHOLD_EXT`.
     *
     * @return the stored value
     */
    public float getFxaaEdgeThreshold() {
        return fxaaEdgeThreshold;
    }

    /**
     * Sets FXAAEdgeThresholdEXT. Floored at
     * `CNA_RENDER_PIPELINE_MINIMUM_FXAA_EDGE_THRESHOLD_EXT`.
     *
     * @param value the value to store here, uncorrected
     */
    public void setFxaaEdgeThreshold(float value) {
        fxaaEdgeThreshold = value;
    }

    /**
     * Corrects every field the way the engine would, in place.
     *
     * <p>Thirty-one of these fields correct their input on the way in, and a value written by
     * hand holds whatever it was given. This asks CNA what it would actually store.
     */
    public void normalize() {
        byte[] bytes = new byte[4];
        long[] integral = integral();
        float[] floating = floating();
        GraphicsExtension.check("RenderPipelineSettingsExt.normalize",
                NativeEngineLayerRoutes.renderPipelineSettingsExtNormalize(
                        bytes, integral, floating));
        read(integral, floating);
    }

    /**
     * Applies the preset that goes with this value's render quality.
     *
     * <p>A quality level is a name for a set of choices, and this is where the name becomes
     * the choices: setting the quality alone changes one field, and this changes the ones it
     * stands for.
     */
    public void applyRenderQualityPreset() {
        byte[] bytes = new byte[4];
        long[] integral = integral();
        float[] floating = floating();
        GraphicsExtension.check("RenderPipelineSettingsExt.applyRenderQualityPreset",
                NativeEngineLayerRoutes.renderPipelineSettingsExtApplyRenderQualityPreset(
                        bytes, integral, floating));
        read(integral, floating);
    }

    /**
     * Applies serialized settings text, reporting how many fields were recognised.
     *
     * <p>What a console command or a config file wants. Unrecognised fields are skipped
     * rather than refused, which is what makes the count meaningful: a caller compares it
     * against what it expected to set, and a typo shows up as a number rather than as a
     * setting that quietly did not take.
     *
     * @param text the serialized settings
     * @return how many fields were recognised and applied
     */
    public int applyFrom(String text) {
        Objects.requireNonNull(text, "text");
        byte[] bytes = new byte[4];
        long[] integral = integral();
        float[] floating = floating();
        int[] applied = new int[1];
        GraphicsExtension.check("RenderPipelineSettingsExt.applyFrom",
                NativeEngineLayerRoutes.renderPipelineSettingsExtApplyFromString(
                        bytes, integral, floating,
                        text.getBytes(StandardCharsets.UTF_8), applied));
        read(integral, floating);
        return applied[0];
    }

    long[] integral() {
        return new long[] {
            hdrEnabled ? 1 : 0,
            tonemappingMode.ordinal(),
            bloomEnabled ? 1 : 0,
            bloomIterations,
            ssaoEnabled ? 1 : 0,
            transparencyMode.ordinal(),
            ssaoSampleCount,
            ssrEnabled ? 1 : 0,
            ssrStepCount,
            colorGradeEnabled ? 1 : 0,
            dofEnabled ? 1 : 0,
            fxaaEnabled ? 1 : 0,
            renderQuality.ordinal(),
            shadowQuality.ordinal(),
            shadowsEnabled ? 1 : 0,
        };
    }

    float[] floating() {
        return new float[] {
            exposure,
            gamma,
            bloomIntensity,
            bloomThreshold,
            ssaoRadius,
            ssaoIntensity,
            ssrMaxDistance,
            ssrThickness,
            ssrDepthBias,
            ssrEdgeFade,
            volumetricFogDensity,
            lightShaftThreshold,
            lightShaftIntensity,
            lightShaftDecay,
            heightFogDensity,
            heightFogFalloff,
            heightFogBaseHeight,
            motionBlurStrength,
            motionBlurMaxDistance,
            chromaticAberrationStrength,
            filmGrainIntensity,
            lensFlareThreshold,
            lensFlareIntensity,
            lensFlareDispersal,
            colorGradeStrength,
            dofFocusDistance,
            dofFocalLength,
            dofFNumber,
            dofMaxRadius,
            ssrRoughnessBlur,
            ssrIntensity,
            fxaaEdgeThreshold,
        };
    }

    void read(long[] integral, float[] floating) {
        hdrEnabled = integral[0] != 0L;
        tonemappingMode = TonemappingMode.fromValue(integral[1]);
        bloomEnabled = integral[2] != 0L;
        bloomIterations = (int) integral[3];
        ssaoEnabled = integral[4] != 0L;
        transparencyMode = TransparencyMode.fromValue(integral[5]);
        ssaoSampleCount = (int) integral[6];
        ssrEnabled = integral[7] != 0L;
        ssrStepCount = (int) integral[8];
        colorGradeEnabled = integral[9] != 0L;
        dofEnabled = integral[10] != 0L;
        fxaaEnabled = integral[11] != 0L;
        renderQuality = RenderQuality.fromValue(integral[12]);
        shadowQuality = ShadowQuality.fromValue(integral[13]);
        shadowsEnabled = integral[14] != 0L;
        exposure = floating[0];
        gamma = floating[1];
        bloomIntensity = floating[2];
        bloomThreshold = floating[3];
        ssaoRadius = floating[4];
        ssaoIntensity = floating[5];
        ssrMaxDistance = floating[6];
        ssrThickness = floating[7];
        ssrDepthBias = floating[8];
        ssrEdgeFade = floating[9];
        volumetricFogDensity = floating[10];
        lightShaftThreshold = floating[11];
        lightShaftIntensity = floating[12];
        lightShaftDecay = floating[13];
        heightFogDensity = floating[14];
        heightFogFalloff = floating[15];
        heightFogBaseHeight = floating[16];
        motionBlurStrength = floating[17];
        motionBlurMaxDistance = floating[18];
        chromaticAberrationStrength = floating[19];
        filmGrainIntensity = floating[20];
        lensFlareThreshold = floating[21];
        lensFlareIntensity = floating[22];
        lensFlareDispersal = floating[23];
        colorGradeStrength = floating[24];
        dofFocusDistance = floating[25];
        dofFocalLength = floating[26];
        dofFNumber = floating[27];
        dofMaxRadius = floating[28];
        ssrRoughnessBlur = floating[29];
        ssrIntensity = floating[30];
        fxaaEdgeThreshold = floating[31];
    }
}
