package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Color;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import org.openeggbert.cna.internal.generated.NativeGraphicsExtensionRoutes;

import java.util.Objects;

/**
 * A physically based material: the five texture maps and the scalar factors CNA shades with.
 *
 * <p>A CNA extension with no XNA 4.0 counterpart -- XNA has five stock effects and no material
 * model. A new instance carries CNA's own defaults, read from the runtime rather than restated
 * here.
 *
 * <p>The texture maps are ordinary XNA {@link Texture2D} objects: a material is a description of
 * how to shade, not a second resource system.
 */
public final class PbrMaterial {

    private Texture2D albedoTexture;
    private Texture2D normalTexture;
    private Texture2D metallicRoughnessTexture;
    private Texture2D ambientOcclusionTexture;
    private Texture2D emissiveTexture;
    private Color albedoColor;
    private Color emissiveColor;
    private float metallicFactor;
    private float roughnessFactor;
    private float normalScale;
    private float occlusionStrength;
    private float alphaCutoff;
    private boolean alphaBlendEnabled;

    /** Creates the material CNA itself defaults to, with no textures assigned. */
    public PbrMaterial() {
        long[] integers = new long[14];
        float[] floats = new float[5];
        GraphicsExtension.check("PbrMaterial",
                NativeGraphicsExtensionRoutes.pbrMaterialInit(new byte[3], integers, floats));
        albedoColor = new Color((int) integers[5], (int) integers[6],
                (int) integers[7], (int) integers[8]);
        emissiveColor = new Color((int) integers[9], (int) integers[10],
                (int) integers[11], (int) integers[12]);
        alphaBlendEnabled = integers[13] != 0L;
        metallicFactor = floats[0];
        roughnessFactor = floats[1];
        normalScale = floats[2];
        occlusionStrength = floats[3];
        alphaCutoff = floats[4];
    }

    /** Copies another material, including which textures it names. */
    public PbrMaterial(PbrMaterial value) {
        PbrMaterial source = Objects.requireNonNull(value, "value");
        albedoTexture = source.albedoTexture;
        normalTexture = source.normalTexture;
        metallicRoughnessTexture = source.metallicRoughnessTexture;
        ambientOcclusionTexture = source.ambientOcclusionTexture;
        emissiveTexture = source.emissiveTexture;
        albedoColor = new Color(source.albedoColor);
        emissiveColor = new Color(source.emissiveColor);
        metallicFactor = source.metallicFactor;
        roughnessFactor = source.roughnessFactor;
        normalScale = source.normalScale;
        occlusionStrength = source.occlusionStrength;
        alphaCutoff = source.alphaCutoff;
        alphaBlendEnabled = source.alphaBlendEnabled;
    }

    public Texture2D getAlbedoTexture() {
        return albedoTexture;
    }

    public void setAlbedoTexture(Texture2D value) {
        albedoTexture = value;
    }

    public Texture2D getNormalTexture() {
        return normalTexture;
    }

    public void setNormalTexture(Texture2D value) {
        normalTexture = value;
    }

    public Texture2D getMetallicRoughnessTexture() {
        return metallicRoughnessTexture;
    }

    public void setMetallicRoughnessTexture(Texture2D value) {
        metallicRoughnessTexture = value;
    }

    public Texture2D getAmbientOcclusionTexture() {
        return ambientOcclusionTexture;
    }

    public void setAmbientOcclusionTexture(Texture2D value) {
        ambientOcclusionTexture = value;
    }

    public Texture2D getEmissiveTexture() {
        return emissiveTexture;
    }

    public void setEmissiveTexture(Texture2D value) {
        emissiveTexture = value;
    }

    /** Returns a copy: XNA's Color is a struct, so this must not hand out an alias. */
    public Color getAlbedoColor() {
        return new Color(albedoColor);
    }

    public void setAlbedoColor(Color value) {
        albedoColor = new Color(Objects.requireNonNull(value, "value"));
    }

    /** Returns a copy: XNA's Color is a struct, so this must not hand out an alias. */
    public Color getEmissiveColor() {
        return new Color(emissiveColor);
    }

    public void setEmissiveColor(Color value) {
        emissiveColor = new Color(Objects.requireNonNull(value, "value"));
    }

    public float getMetallicFactor() {
        return metallicFactor;
    }

    public void setMetallicFactor(float value) {
        metallicFactor = value;
    }

    public float getRoughnessFactor() {
        return roughnessFactor;
    }

    public void setRoughnessFactor(float value) {
        roughnessFactor = value;
    }

    public float getNormalScale() {
        return normalScale;
    }

    public void setNormalScale(float value) {
        normalScale = value;
    }

    public float getOcclusionStrength() {
        return occlusionStrength;
    }

    public void setOcclusionStrength(float value) {
        occlusionStrength = value;
    }

    public float getAlphaCutoff() {
        return alphaCutoff;
    }

    public void setAlphaCutoff(float value) {
        alphaCutoff = value;
    }

    public boolean getAlphaBlendEnabled() {
        return alphaBlendEnabled;
    }

    public void setAlphaBlendEnabled(boolean value) {
        alphaBlendEnabled = value;
    }
}
