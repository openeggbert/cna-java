package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.Texture2D;
import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.Arrays;
import java.util.Objects;

/**
 * The glTF material extensions a file declares, before they become something a renderer can use.
 *
 * <p>The sibling of {@link GltfMaterialSource} for the {@code KHR_materials_*} extensions that go
 * beyond the core model -- clearcoat, sheen, transmission, volume and iridescence -- and the input
 * half of {@link #buildInto}, which fills a {@link PbrMaterialExtensions} in one call rather than
 * leaving a loader to make nine setter calls in the right order.
 *
 * <p>Nine textures, nine slots, and each is BORROWED: the extensions object records them and
 * whoever loaded them still owns them.
 */
public final class GltfMaterialExtensionSource {

    /** How many texture slots the extension set has. */
    public static final int TEXTURE_SLOTS = 9;

    private static final int FLOATING_LEAVES = 16;

    private final Texture2D[] textures = new Texture2D[TEXTURE_SLOTS];

    private float clearcoatFactor;
    private float clearcoatRoughnessFactor;
    private Vector3 sheenColorFactor;
    private float sheenRoughnessFactor;
    private float transmissionFactor;
    private float thicknessFactor;
    private float attenuationDistance;
    private Vector3 attenuationColor;
    private float iridescenceFactor;
    private float iridescenceIor;
    private float iridescenceThicknessMinimum;
    private float iridescenceThicknessMaximum;

    /**
     * Creates a source carrying the glTF specification's own default factors.
     *
     * <p>Read from CNA rather than restated, because these are the values every extension takes
     * when a file mentions it without giving a number -- and an attenuation distance defaulted
     * wrong is a volume that absorbs light at the wrong rate with nothing to say so.
     *
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public GltfMaterialExtensionSource() {
        GraphicsExtension.requireBackend();
        float[] floating = new float[FLOATING_LEAVES];
        GraphicsExtension.check("GltfMaterialExtensionSource",
                NativeEngineLayerRoutes.gltfMaterialExtensionSourceExtInit(floating));
        clearcoatFactor = floating[0];
        clearcoatRoughnessFactor = floating[1];
        sheenColorFactor = new Vector3(floating[2], floating[3], floating[4]);
        sheenRoughnessFactor = floating[5];
        transmissionFactor = floating[6];
        thicknessFactor = floating[7];
        attenuationDistance = floating[8];
        attenuationColor = new Vector3(floating[9], floating[10], floating[11]);
        iridescenceFactor = floating[12];
        iridescenceIor = floating[13];
        iridescenceThicknessMinimum = floating[14];
        iridescenceThicknessMaximum = floating[15];
    }

    /**
     * Writes these extensions into an existing extensions object.
     *
     * <p>Into rather than returning a new one, because CNA's route takes an existing handle: an
     * extensions object owns native memory and a loader that builds one per material wants to
     * reuse it.
     *
     * @param destination the extensions to fill
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public void buildInto(PbrMaterialExtensions destination) {
        Objects.requireNonNull(destination, "destination");
        GraphicsExtension.check("GltfMaterialExtensionSource.buildInto",
                NativeEngineLayerRoutes.gltfMaterialBridgeBuildExtensions(floating(),
                        textureHandles(), destination.handle()));
        destination.rememberSlots(textures.clone());
    }

    /**
     * Returns the texture one extension slot resolved to.
     *
     * @param slot the slot index, in CNA's own order: clearcoat, clearcoat roughness, clearcoat
     *        normal, sheen colour, sheen roughness, transmission, thickness, iridescence,
     *        iridescence thickness
     * @return the texture, or {@code null}
     */
    public Texture2D getTexture(int slot) {
        return textures[slot];
    }

    /**
     * Records the texture one extension slot resolved to, borrowing it.
     *
     * @param slot the slot index
     * @param texture the texture, or {@code null} for a slot the file does not name
     */
    public void setTexture(int slot, Texture2D texture) {
        textures[slot] = texture;
    }

    /** @return {@code KHR_materials_clearcoat.clearcoatFactor} */
    public float getClearcoatFactor() {
        return clearcoatFactor;
    }

    /** @param value the clearcoat factor */
    public void setClearcoatFactor(float value) {
        clearcoatFactor = value;
    }

    /** @return {@code KHR_materials_clearcoat.clearcoatRoughnessFactor} */
    public float getClearcoatRoughnessFactor() {
        return clearcoatRoughnessFactor;
    }

    /** @param value the clearcoat roughness factor */
    public void setClearcoatRoughnessFactor(float value) {
        clearcoatRoughnessFactor = value;
    }

    /** @return {@code KHR_materials_sheen.sheenColorFactor} */
    public Vector3 getSheenColorFactor() {
        return new Vector3(sheenColorFactor);
    }

    /** @param value the sheen colour factor */
    public void setSheenColorFactor(Vector3 value) {
        sheenColorFactor = new Vector3(Objects.requireNonNull(value, "value"));
    }

    /** @return {@code KHR_materials_sheen.sheenRoughnessFactor} */
    public float getSheenRoughnessFactor() {
        return sheenRoughnessFactor;
    }

    /** @param value the sheen roughness factor */
    public void setSheenRoughnessFactor(float value) {
        sheenRoughnessFactor = value;
    }

    /** @return {@code KHR_materials_transmission.transmissionFactor} */
    public float getTransmissionFactor() {
        return transmissionFactor;
    }

    /** @param value the transmission factor */
    public void setTransmissionFactor(float value) {
        transmissionFactor = value;
    }

    /** @return {@code KHR_materials_volume.thicknessFactor} */
    public float getThicknessFactor() {
        return thicknessFactor;
    }

    /** @param value the thickness factor */
    public void setThicknessFactor(float value) {
        thicknessFactor = value;
    }

    /** @return {@code KHR_materials_volume.attenuationDistance} */
    public float getAttenuationDistance() {
        return attenuationDistance;
    }

    /** @param value the attenuation distance */
    public void setAttenuationDistance(float value) {
        attenuationDistance = value;
    }

    /** @return {@code KHR_materials_volume.attenuationColor} */
    public Vector3 getAttenuationColor() {
        return new Vector3(attenuationColor);
    }

    /** @param value the attenuation colour */
    public void setAttenuationColor(Vector3 value) {
        attenuationColor = new Vector3(Objects.requireNonNull(value, "value"));
    }

    /** @return {@code KHR_materials_iridescence.iridescenceFactor} */
    public float getIridescenceFactor() {
        return iridescenceFactor;
    }

    /** @param value the iridescence factor */
    public void setIridescenceFactor(float value) {
        iridescenceFactor = value;
    }

    /** @return {@code KHR_materials_iridescence.iridescenceIor} */
    public float getIridescenceIor() {
        return iridescenceIor;
    }

    /** @param value the iridescence index of refraction */
    public void setIridescenceIor(float value) {
        iridescenceIor = value;
    }

    /** @return {@code KHR_materials_iridescence.iridescenceThicknessMinimum} */
    public float getIridescenceThicknessMinimum() {
        return iridescenceThicknessMinimum;
    }

    /** @param value the minimum iridescent film thickness */
    public void setIridescenceThicknessMinimum(float value) {
        iridescenceThicknessMinimum = value;
    }

    /** @return {@code KHR_materials_iridescence.iridescenceThicknessMaximum} */
    public float getIridescenceThicknessMaximum() {
        return iridescenceThicknessMaximum;
    }

    /** @param value the maximum iridescent film thickness */
    public void setIridescenceThicknessMaximum(float value) {
        iridescenceThicknessMaximum = value;
    }

    private float[] floating() {
        return new float[] {
            clearcoatFactor, clearcoatRoughnessFactor,
            sheenColorFactor.X, sheenColorFactor.Y, sheenColorFactor.Z,
            sheenRoughnessFactor, transmissionFactor, thicknessFactor, attenuationDistance,
            attenuationColor.X, attenuationColor.Y, attenuationColor.Z,
            iridescenceFactor, iridescenceIor, iridescenceThicknessMinimum,
            iridescenceThicknessMaximum,
        };
    }

    /**
     * The nine slots CNA takes beside the factors.
     *
     * <p>As with the core set, {@code cna_gltf_material_extension_textures_ext_init} is unbound:
     * it fills a structure with invalid handles, which a fresh Java array already is.
     */
    private long[] textureHandles() {
        long[] handles = new long[TEXTURE_SLOTS];
        Arrays.setAll(handles, slot -> textures[slot] == null ? 0L
                : NativeBindings.nativeResourceHandle(textures[slot]));
        return handles;
    }
}
