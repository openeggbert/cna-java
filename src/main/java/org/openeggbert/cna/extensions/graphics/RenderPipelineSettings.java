package org.openeggbert.cna.extensions.graphics;

import org.openeggbert.cna.internal.generated.NativeGraphicsExtensionRoutes;

import java.util.Objects;

/**
 * The frame-wide settings CNA's render pipeline reads: exposure, gamma, bloom, tonemapping and
 * the two quality presets.
 *
 * <p>A CNA extension with no XNA 4.0 counterpart. A new instance carries CNA's own defaults,
 * read from the runtime rather than restated here, so a default constructed in Java and one
 * constructed in C++ agree.
 */
public final class RenderPipelineSettings {

    private float exposure;
    private float gamma;
    private float bloomIntensity;
    private TonemappingMode tonemappingMode;
    private RenderQuality renderQuality;
    private ShadowQuality shadowQuality;
    private boolean hdrEnabled;
    private boolean bloomEnabled;
    private boolean ssaoEnabled;
    private boolean shadowsEnabled;

    /** Creates the settings CNA itself defaults to. */
    public RenderPipelineSettings() {
        GraphicsExtension.requireBackend();
        long[] integers = new long[7];
        float[] floats = new float[3];
        GraphicsExtension.check("RenderPipelineSettings",
                NativeGraphicsExtensionRoutes.renderPipelineSettingsInit(integers, floats));
        tonemappingMode = TonemappingMode.values()[(int) integers[0]];
        renderQuality = RenderQuality.values()[(int) integers[1]];
        shadowQuality = ShadowQuality.values()[(int) integers[2]];
        hdrEnabled = integers[3] != 0L;
        bloomEnabled = integers[4] != 0L;
        ssaoEnabled = integers[5] != 0L;
        shadowsEnabled = integers[6] != 0L;
        exposure = floats[0];
        gamma = floats[1];
        bloomIntensity = floats[2];
    }

    /** Copies another settings value. */
    public RenderPipelineSettings(RenderPipelineSettings value) {
        RenderPipelineSettings source = Objects.requireNonNull(value, "value");
        exposure = source.exposure;
        gamma = source.gamma;
        bloomIntensity = source.bloomIntensity;
        tonemappingMode = source.tonemappingMode;
        renderQuality = source.renderQuality;
        shadowQuality = source.shadowQuality;
        hdrEnabled = source.hdrEnabled;
        bloomEnabled = source.bloomEnabled;
        ssaoEnabled = source.ssaoEnabled;
        shadowsEnabled = source.shadowsEnabled;
    }

    public float getExposure() {
        return exposure;
    }

    public void setExposure(float value) {
        exposure = value;
    }

    public float getGamma() {
        return gamma;
    }

    public void setGamma(float value) {
        gamma = value;
    }

    public float getBloomIntensity() {
        return bloomIntensity;
    }

    public void setBloomIntensity(float value) {
        bloomIntensity = value;
    }

    public TonemappingMode getTonemappingMode() {
        return tonemappingMode;
    }

    public void setTonemappingMode(TonemappingMode value) {
        tonemappingMode = Objects.requireNonNull(value, "value");
    }

    public RenderQuality getRenderQuality() {
        return renderQuality;
    }

    public void setRenderQuality(RenderQuality value) {
        renderQuality = Objects.requireNonNull(value, "value");
    }

    public ShadowQuality getShadowQuality() {
        return shadowQuality;
    }

    public void setShadowQuality(ShadowQuality value) {
        shadowQuality = Objects.requireNonNull(value, "value");
    }

    public boolean getHdrEnabled() {
        return hdrEnabled;
    }

    public void setHdrEnabled(boolean value) {
        hdrEnabled = value;
    }

    public boolean getBloomEnabled() {
        return bloomEnabled;
    }

    public void setBloomEnabled(boolean value) {
        bloomEnabled = value;
    }

    public boolean getSsaoEnabled() {
        return ssaoEnabled;
    }

    public void setSsaoEnabled(boolean value) {
        ssaoEnabled = value;
    }

    public boolean getShadowsEnabled() {
        return shadowsEnabled;
    }

    public void setShadowsEnabled(boolean value) {
        shadowsEnabled = value;
    }
}
