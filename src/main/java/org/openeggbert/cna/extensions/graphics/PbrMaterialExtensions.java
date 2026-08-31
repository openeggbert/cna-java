package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.Texture2D;
import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The glTF material extensions a modern PBR shader reads beyond base colour and roughness.
 *
 * <p>A CNA extension, and a long way past anything XNA has: {@code BasicEffect} has a diffuse
 * colour, a specular power and one texture. This carries the {@code KHR_materials_*} set --
 * clearcoat, sheen, transmission, volume, iridescence -- which is what makes a car's lacquer,
 * a velvet cushion, a glass bottle and a soap bubble different materials rather than different
 * colours.
 *
 * <p><strong>Every texture slot is non-owning in CNA and retained here.</strong> A material names
 * a texture; it does not own it. This object holds a Java reference to each texture it was given
 * so nothing collects one while a material still names it, and {@link #close()} disposes none of
 * them. The getters return the retained texture and check its handle against the one CNA holds,
 * so the two cannot drift apart unnoticed.
 *
 * <p><strong>The corrections are three different shapes</strong>, which is worth reading the
 * per-field documentation for: some clamp, some are ignored when out of range and leave the
 * previous value standing, and {@link #setAttenuationDistance} floors -- writing zero rather than
 * keeping what was there.
 *
 * <p>Needs no graphics device to exist. The handle is owned; {@link #close()} releases it and
 * closing twice is a no-op.
 */
public final class PbrMaterialExtensions implements AutoCloseable {

    /** CNA's own result for a buffer that could not hold the answer. */
    private static final int RESULT_BUFFER_TOO_SMALL = 14;

    private final long handle;
    // The Java side of each non-owning slot. CNA stores a handle; this stores the object that
    // handle belongs to, which is what keeps it alive and what a getter can honestly return.
    private final Map<String, Texture2D> slots = new HashMap<>();
    private boolean closed;

    private PbrMaterialExtensions(long handle) {
        this.handle = handle;
    }

    /**
     * Creates a neutral set of extensions.
     *
     * @return the material, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static PbrMaterialExtensions create() {
        GraphicsExtension.requireBackend();
        long[] extensions = new long[1];
        GraphicsExtension.check("PbrMaterialExtensions.create",
                NativeEngineLayerRoutes.pbrMaterialExtensionsCreate(extensions));
        return new PbrMaterialExtensions(extensions[0]);
    }

    /**
     * Returns the material's AttenuationDistance.
     *
     * @return the value
     */
    public float getAttenuationDistance() {
        return readFloat("PbrMaterialExtensions.getAttenuationDistance",
                NativeEngineLayerRoutes::pbrMaterialExtensionsGetAttenuationDistance);
    }

    /**
     * Sets the material's AttenuationDistance.
     *
     * <p>The value, floored at zero -- unlike the guarded setters beside it this one writes zero rather than keeping the previous value, which is a third correction shape in the same class.
     *
     * @param value the value
     */
    public void setAttenuationDistance(float value) {
        GraphicsExtension.check("PbrMaterialExtensions.setAttenuationDistance",
                NativeEngineLayerRoutes.pbrMaterialExtensionsSetAttenuationDistance(open(), value));
    }

    /**
     * Returns the material's ClearcoatFactor.
     *
     * @return the value
     */
    public float getClearcoatFactor() {
        return readFloat("PbrMaterialExtensions.getClearcoatFactor",
                NativeEngineLayerRoutes::pbrMaterialExtensionsGetClearcoatFactor);
    }

    /**
     * Sets the material's ClearcoatFactor.
     *
     * <p>The value, clamped to zero-to-one.
     *
     * @param value the value
     */
    public void setClearcoatFactor(float value) {
        GraphicsExtension.check("PbrMaterialExtensions.setClearcoatFactor",
                NativeEngineLayerRoutes.pbrMaterialExtensionsSetClearcoatFactor(open(), value));
    }

    /**
     * Returns the material's ClearcoatNormalScale.
     *
     * @return the value
     */
    public float getClearcoatNormalScale() {
        return readFloat("PbrMaterialExtensions.getClearcoatNormalScale",
                NativeEngineLayerRoutes::pbrMaterialExtensionsGetClearcoatNormalScale);
    }

    /**
     * Sets the material's ClearcoatNormalScale.
     *
     * <p>The value, ignored when negative -- the canonical setter guards the assignment rather than clamping, so a negative write leaves the previous value in place instead of forcing it to zero.
     *
     * @param value the value
     */
    public void setClearcoatNormalScale(float value) {
        GraphicsExtension.check("PbrMaterialExtensions.setClearcoatNormalScale",
                NativeEngineLayerRoutes.pbrMaterialExtensionsSetClearcoatNormalScale(open(), value));
    }

    /**
     * Returns the material's ClearcoatRoughness.
     *
     * @return the value
     */
    public float getClearcoatRoughness() {
        return readFloat("PbrMaterialExtensions.getClearcoatRoughness",
                NativeEngineLayerRoutes::pbrMaterialExtensionsGetClearcoatRoughness);
    }

    /**
     * Sets the material's ClearcoatRoughness.
     *
     * <p>The value, clamped to zero-to-one.
     *
     * @param value the value
     */
    public void setClearcoatRoughness(float value) {
        GraphicsExtension.check("PbrMaterialExtensions.setClearcoatRoughness",
                NativeEngineLayerRoutes.pbrMaterialExtensionsSetClearcoatRoughness(open(), value));
    }

    /**
     * Returns the material's IridescenceFactor.
     *
     * @return the value
     */
    public float getIridescenceFactor() {
        return readFloat("PbrMaterialExtensions.getIridescenceFactor",
                NativeEngineLayerRoutes::pbrMaterialExtensionsGetIridescenceFactor);
    }

    /**
     * Sets the material's IridescenceFactor.
     *
     * <p>The value, clamped to zero-to-one.
     *
     * @param value the value
     */
    public void setIridescenceFactor(float value) {
        GraphicsExtension.check("PbrMaterialExtensions.setIridescenceFactor",
                NativeEngineLayerRoutes.pbrMaterialExtensionsSetIridescenceFactor(open(), value));
    }

    /**
     * Returns the material's IridescenceIor.
     *
     * @return the value
     */
    public float getIridescenceIor() {
        return readFloat("PbrMaterialExtensions.getIridescenceIor",
                NativeEngineLayerRoutes::pbrMaterialExtensionsGetIridescenceIor);
    }

    /**
     * Sets the material's IridescenceIor.
     *
     * <p>The value, ignored when below one, not below zero -- an index of refraction under one describes a medium light speeds up in, which this film cannot be; a guarded assignment, so such a write leaves the previous value in place.
     *
     * @param value the value
     */
    public void setIridescenceIor(float value) {
        GraphicsExtension.check("PbrMaterialExtensions.setIridescenceIor",
                NativeEngineLayerRoutes.pbrMaterialExtensionsSetIridescenceIor(open(), value));
    }

    /**
     * Returns the material's IridescenceThicknessMaximum.
     *
     * @return the value
     */
    public float getIridescenceThicknessMaximum() {
        return readFloat("PbrMaterialExtensions.getIridescenceThicknessMaximum",
                NativeEngineLayerRoutes::pbrMaterialExtensionsGetIridescenceThicknessMaximum);
    }

    /**
     * Sets the material's IridescenceThicknessMaximum.
     *
     * <p>The value, ignored when negative -- a guarded assignment; the value is a film thickness in nanometres and has no upper bound.
     *
     * @param value the value
     */
    public void setIridescenceThicknessMaximum(float value) {
        GraphicsExtension.check("PbrMaterialExtensions.setIridescenceThicknessMaximum",
                NativeEngineLayerRoutes.pbrMaterialExtensionsSetIridescenceThicknessMaximum(open(), value));
    }

    /**
     * Returns the material's IridescenceThicknessMinimum.
     *
     * @return the value
     */
    public float getIridescenceThicknessMinimum() {
        return readFloat("PbrMaterialExtensions.getIridescenceThicknessMinimum",
                NativeEngineLayerRoutes::pbrMaterialExtensionsGetIridescenceThicknessMinimum);
    }

    /**
     * Sets the material's IridescenceThicknessMinimum.
     *
     * <p>The value, ignored when negative -- a guarded assignment; the value is a film thickness in nanometres and has no upper bound.
     *
     * @param value the value
     */
    public void setIridescenceThicknessMinimum(float value) {
        GraphicsExtension.check("PbrMaterialExtensions.setIridescenceThicknessMinimum",
                NativeEngineLayerRoutes.pbrMaterialExtensionsSetIridescenceThicknessMinimum(open(), value));
    }

    /**
     * Returns the material's SheenRoughness.
     *
     * @return the value
     */
    public float getSheenRoughness() {
        return readFloat("PbrMaterialExtensions.getSheenRoughness",
                NativeEngineLayerRoutes::pbrMaterialExtensionsGetSheenRoughness);
    }

    /**
     * Sets the material's SheenRoughness.
     *
     * <p>The value, clamped to zero-to-one.
     *
     * @param value the value
     */
    public void setSheenRoughness(float value) {
        GraphicsExtension.check("PbrMaterialExtensions.setSheenRoughness",
                NativeEngineLayerRoutes.pbrMaterialExtensionsSetSheenRoughness(open(), value));
    }

    /**
     * Returns the material's SubsurfaceWrap.
     *
     * @return the value
     */
    public float getSubsurfaceWrap() {
        return readFloat("PbrMaterialExtensions.getSubsurfaceWrap",
                NativeEngineLayerRoutes::pbrMaterialExtensionsGetSubsurfaceWrap);
    }

    /**
     * Sets the material's SubsurfaceWrap.
     *
     * <p>The value, clamped to zero-to-one.
     *
     * @param value the value
     */
    public void setSubsurfaceWrap(float value) {
        GraphicsExtension.check("PbrMaterialExtensions.setSubsurfaceWrap",
                NativeEngineLayerRoutes.pbrMaterialExtensionsSetSubsurfaceWrap(open(), value));
    }

    /**
     * Returns the material's ThicknessFactor.
     *
     * @return the value
     */
    public float getThicknessFactor() {
        return readFloat("PbrMaterialExtensions.getThicknessFactor",
                NativeEngineLayerRoutes::pbrMaterialExtensionsGetThicknessFactor);
    }

    /**
     * Sets the material's ThicknessFactor.
     *
     * <p>The value, ignored when negative -- a guarded assignment, so a negative write leaves the previous thickness in place; the value is a distance and has no upper bound.
     *
     * @param value the value
     */
    public void setThicknessFactor(float value) {
        GraphicsExtension.check("PbrMaterialExtensions.setThicknessFactor",
                NativeEngineLayerRoutes.pbrMaterialExtensionsSetThicknessFactor(open(), value));
    }

    /**
     * Returns the material's TransmissionFactor.
     *
     * @return the value
     */
    public float getTransmissionFactor() {
        return readFloat("PbrMaterialExtensions.getTransmissionFactor",
                NativeEngineLayerRoutes::pbrMaterialExtensionsGetTransmissionFactor);
    }

    /**
     * Sets the material's TransmissionFactor.
     *
     * <p>The value, clamped to zero-to-one.
     *
     * @param value the value
     */
    public void setTransmissionFactor(float value) {
        GraphicsExtension.check("PbrMaterialExtensions.setTransmissionFactor",
                NativeEngineLayerRoutes.pbrMaterialExtensionsSetTransmissionFactor(open(), value));
    }

    /**
     * Returns the material's AttenuationColor.
     *
     * @return the value
     */
    public Vector3 getAttenuationColor() {
        float[] value = new float[3];
        GraphicsExtension.check("PbrMaterialExtensions.getAttenuationColor",
                NativeEngineLayerRoutes.pbrMaterialExtensionsGetAttenuationColor(open(), value));
        return new Vector3(value[0], value[1], value[2]);
    }

    /**
     * Sets the material's AttenuationColor.
     *
     * <p>The value; each channel is clamped rather than the value refused.
     *
     * @param value the value
     */
    public void setAttenuationColor(Vector3 value) {
        GraphicsExtension.check("PbrMaterialExtensions.setAttenuationColor",
                NativeEngineLayerRoutes.pbrMaterialExtensionsSetAttenuationColor(open(),
                        EngineValues.floats(value, "value")));
    }

    /**
     * Returns the material's SheenColorFactor.
     *
     * @return the value
     */
    public Vector3 getSheenColorFactor() {
        float[] value = new float[3];
        GraphicsExtension.check("PbrMaterialExtensions.getSheenColorFactor",
                NativeEngineLayerRoutes.pbrMaterialExtensionsGetSheenColorFactor(open(), value));
        return new Vector3(value[0], value[1], value[2]);
    }

    /**
     * Sets the material's SheenColorFactor.
     *
     * <p>The value; each channel is clamped rather than the value refused.
     *
     * @param value the value
     */
    public void setSheenColorFactor(Vector3 value) {
        GraphicsExtension.check("PbrMaterialExtensions.setSheenColorFactor",
                NativeEngineLayerRoutes.pbrMaterialExtensionsSetSheenColorFactor(open(),
                        EngineValues.floats(value, "value")));
    }

    /**
     * Returns the material's SubsurfaceColor.
     *
     * @return the value
     */
    public Vector3 getSubsurfaceColor() {
        float[] value = new float[3];
        GraphicsExtension.check("PbrMaterialExtensions.getSubsurfaceColor",
                NativeEngineLayerRoutes.pbrMaterialExtensionsGetSubsurfaceColor(open(), value));
        return new Vector3(value[0], value[1], value[2]);
    }

    /**
     * Sets the material's SubsurfaceColor.
     *
     * <p>The value; each channel is clamped rather than the value refused.
     *
     * @param value the value
     */
    public void setSubsurfaceColor(Vector3 value) {
        GraphicsExtension.check("PbrMaterialExtensions.setSubsurfaceColor",
                NativeEngineLayerRoutes.pbrMaterialExtensionsSetSubsurfaceColor(open(),
                        EngineValues.floats(value, "value")));
    }

    /**
     * Returns the material's ClearcoatNormalTexture.
     *
PLACEHOLDER
     *
     * @return the texture, or {@code null} when the slot is empty
     */
    public Texture2D getClearcoatNormalTexture() {
        return slot("clearcoat_normal_texture");
    }

    /**
     * Sets the material's ClearcoatNormalTexture.
     *
     * <p>The slot is non-owning in CNA and retained here; closing this material does not dispose
     * the texture.
     *
     * @param value the texture, or {@code null} to empty the slot
     */
    public void setClearcoatNormalTexture(Texture2D value) {
        setSlot("clearcoat_normal_texture", value, NativeEngineLayerRoutes::pbrMaterialExtensionsSetClearcoatNormalTexture);
    }

    /**
     * Returns the material's ClearcoatRoughnessTexture.
     *
     * <p>The texture handed to {@link #setClearcoatRoughnessTexture}, which this object retained. CNA's slot is
     * non-owning, so the retention here is what keeps a texture alive while a material names it,
     * and the retained reference is the answer -- see the note on the private slot reader.
     *
     * @return the texture, or {@code null} when the slot is empty
     */
    public Texture2D getClearcoatRoughnessTexture() {
        return slot("clearcoat_roughness_texture");
    }

    /**
     * Sets the material's ClearcoatRoughnessTexture.
     *
     * <p>The slot is non-owning in CNA and retained here; closing this material does not dispose
     * the texture.
     *
     * @param value the texture, or {@code null} to empty the slot
     */
    public void setClearcoatRoughnessTexture(Texture2D value) {
        setSlot("clearcoat_roughness_texture", value, NativeEngineLayerRoutes::pbrMaterialExtensionsSetClearcoatRoughnessTexture);
    }

    /**
     * Returns the material's ClearcoatTexture.
     *
X
     *
     * @return the texture, or {@code null} when the slot is empty
     */
    public Texture2D getClearcoatTexture() {
        return slot("clearcoat_texture");
    }

    /**
     * Sets the material's ClearcoatTexture.
     *
     * <p>The slot is non-owning in CNA and retained here; closing this material does not dispose
     * the texture.
     *
     * @param value the texture, or {@code null} to empty the slot
     */
    public void setClearcoatTexture(Texture2D value) {
        setSlot("clearcoat_texture", value, NativeEngineLayerRoutes::pbrMaterialExtensionsSetClearcoatTexture);
    }

    /**
     * Returns the material's IridescenceTexture.
     *
     * <p>The texture handed to {@link #setIridescenceTexture}, which this object retained. CNA's slot is
     * non-owning, so the retention here is what keeps a texture alive while a material names it,
     * and the retained reference is the answer -- see the note on the private slot reader.
     *
     * @return the texture, or {@code null} when the slot is empty
     */
    public Texture2D getIridescenceTexture() {
        return slot("iridescence_texture");
    }

    /**
     * Sets the material's IridescenceTexture.
     *
     * <p>The slot is non-owning in CNA and retained here; closing this material does not dispose
     * the texture.
     *
     * @param value the texture, or {@code null} to empty the slot
     */
    public void setIridescenceTexture(Texture2D value) {
        setSlot("iridescence_texture", value, NativeEngineLayerRoutes::pbrMaterialExtensionsSetIridescenceTexture);
    }

    /**
     * Returns the material's IridescenceThicknessTexture.
     *
     * <p>The texture handed to {@link #setIridescenceThicknessTexture}, which this object retained. CNA's slot is
     * non-owning, so the retention here is what keeps a texture alive while a material names it,
     * and the retained reference is the answer -- see the note on the private slot reader.
     *
     * @return the texture, or {@code null} when the slot is empty
     */
    public Texture2D getIridescenceThicknessTexture() {
        return slot("iridescence_thickness_texture");
    }

    /**
     * Sets the material's IridescenceThicknessTexture.
     *
     * <p>The slot is non-owning in CNA and retained here; closing this material does not dispose
     * the texture.
     *
     * @param value the texture, or {@code null} to empty the slot
     */
    public void setIridescenceThicknessTexture(Texture2D value) {
        setSlot("iridescence_thickness_texture", value, NativeEngineLayerRoutes::pbrMaterialExtensionsSetIridescenceThicknessTexture);
    }

    /**
     * Returns the material's SheenColorTexture.
     *
     * <p>The texture handed to {@link #setSheenColorTexture}, which this object retained. CNA's slot is
     * non-owning, so the retention here is what keeps a texture alive while a material names it,
     * and the retained reference is the answer -- see the note on the private slot reader.
     *
     * @return the texture, or {@code null} when the slot is empty
     */
    public Texture2D getSheenColorTexture() {
        return slot("sheen_color_texture");
    }

    /**
     * Sets the material's SheenColorTexture.
     *
     * <p>The slot is non-owning in CNA and retained here; closing this material does not dispose
     * the texture.
     *
     * @param value the texture, or {@code null} to empty the slot
     */
    public void setSheenColorTexture(Texture2D value) {
        setSlot("sheen_color_texture", value, NativeEngineLayerRoutes::pbrMaterialExtensionsSetSheenColorTexture);
    }

    /**
     * Returns the material's SheenRoughnessTexture.
     *
     * <p>The texture handed to {@link #setSheenRoughnessTexture}, which this object retained. CNA's slot is
     * non-owning, so the retention here is what keeps a texture alive while a material names it,
     * and the retained reference is the answer -- see the note on the private slot reader.
     *
     * @return the texture, or {@code null} when the slot is empty
     */
    public Texture2D getSheenRoughnessTexture() {
        return slot("sheen_roughness_texture");
    }

    /**
     * Sets the material's SheenRoughnessTexture.
     *
     * <p>The slot is non-owning in CNA and retained here; closing this material does not dispose
     * the texture.
     *
     * @param value the texture, or {@code null} to empty the slot
     */
    public void setSheenRoughnessTexture(Texture2D value) {
        setSlot("sheen_roughness_texture", value, NativeEngineLayerRoutes::pbrMaterialExtensionsSetSheenRoughnessTexture);
    }

    /**
     * Returns the material's ThicknessTexture.
     *
     * <p>The texture handed to {@link #setThicknessTexture}, which this object retained. CNA's slot is
     * non-owning, so the retention here is what keeps a texture alive while a material names it,
     * and the retained reference is the answer -- see the note on the private slot reader.
     *
     * @return the texture, or {@code null} when the slot is empty
     */
    public Texture2D getThicknessTexture() {
        return slot("thickness_texture");
    }

    /**
     * Sets the material's ThicknessTexture.
     *
     * <p>The slot is non-owning in CNA and retained here; closing this material does not dispose
     * the texture.
     *
     * @param value the texture, or {@code null} to empty the slot
     */
    public void setThicknessTexture(Texture2D value) {
        setSlot("thickness_texture", value, NativeEngineLayerRoutes::pbrMaterialExtensionsSetThicknessTexture);
    }

    /**
     * Returns the material's TransmissionTexture.
     *
     * <p>The texture handed to {@link #setTransmissionTexture}, which this object retained. CNA's slot is
     * non-owning, so the retention here is what keeps a texture alive while a material names it,
     * and the retained reference is the answer -- see the note on the private slot reader.
     *
     * @return the texture, or {@code null} when the slot is empty
     */
    public Texture2D getTransmissionTexture() {
        return slot("transmission_texture");
    }

    /**
     * Sets the material's TransmissionTexture.
     *
     * <p>The slot is non-owning in CNA and retained here; closing this material does not dispose
     * the texture.
     *
     * @param value the texture, or {@code null} to empty the slot
     */
    public void setTransmissionTexture(Texture2D value) {
        setSlot("transmission_texture", value, NativeEngineLayerRoutes::pbrMaterialExtensionsSetTransmissionTexture);
    }

    /**
     * Reports whether the material uses iridescence.
     *
     * <p>Receives the answer.
     *
     * @return CNA's own answer
     */
    public boolean isIridescenceEnabled() {
        return readFlag("PbrMaterialExtensions.isIridescenceEnabled",
                NativeEngineLayerRoutes::pbrMaterialExtensionsIsIridescenceEnabled);
    }

    /**
     * Reports whether the material is neutral.
     *
     * <p>Receives the answer.
     *
     * @return CNA's own answer
     */
    public boolean isNeutral() {
        return readFlag("PbrMaterialExtensions.isNeutral",
                NativeEngineLayerRoutes::pbrMaterialExtensionsIsNeutral);
    }

    /**
     * Reports whether the material uses sheen.
     *
     * <p>Receives the answer.
     *
     * @return CNA's own answer
     */
    public boolean isSheenEnabled() {
        return readFlag("PbrMaterialExtensions.isSheenEnabled",
                NativeEngineLayerRoutes::pbrMaterialExtensionsIsSheenEnabled);
    }

    /**
     * Reports whether the material uses subsurface.
     *
     * <p>Receives the answer.
     *
     * @return CNA's own answer
     */
    public boolean isSubsurfaceEnabled() {
        return readFlag("PbrMaterialExtensions.isSubsurfaceEnabled",
                NativeEngineLayerRoutes::pbrMaterialExtensionsIsSubsurfaceEnabled);
    }

    /**
     * Reports whether the material uses transmission.
     *
     * <p>Receives the answer.
     *
     * @return CNA's own answer
     */
    public boolean isTransmissionEnabled() {
        return readFlag("PbrMaterialExtensions.isTransmissionEnabled",
                NativeEngineLayerRoutes::pbrMaterialExtensionsIsTransmissionEnabled);
    }

    /**
     * Overwrites this material with another's value, texture slots included.
     *
     * @param source the material to copy
     */
    public void copyFrom(PbrMaterialExtensions source) {
        Objects.requireNonNull(source, "source");
        GraphicsExtension.check("PbrMaterialExtensions.copyFrom",
                NativeEngineLayerRoutes.pbrMaterialExtensionsCopyFrom(open(), source.open()));
        synchronized (slots) {
            slots.clear();
            synchronized (source.slots) {
                slots.putAll(source.slots);
            }
        }
    }

    /**
     * Reports whether two materials hold the same value.
     *
     * <p>Deliberately not {@code equals}: this object owns a native handle and has identity.
     * This is the separate question of whether their contents match, which is CNA's own
     * comparison.
     *
     * @param other the material to compare with
     * @return whether they hold the same value
     */
    public boolean matches(PbrMaterialExtensions other) {
        Objects.requireNonNull(other, "other");
        boolean[] equal = new boolean[1];
        GraphicsExtension.check("PbrMaterialExtensions.matches",
                NativeEngineLayerRoutes.pbrMaterialExtensionsEquals(open(), other.open(),
                        equal));
        return equal[0];
    }

    /**
     * Returns CNA's own hash of the material's value.
     *
     * <p>Not this object's {@code hashCode}, which is identity: this is the value hash a
     * material cache keys on, and two materials that {@link #matches} share it.
     *
     * @return the hash
     */
    public long getValueHashCode() {
        long[] hash = new long[1];
        GraphicsExtension.check("PbrMaterialExtensions.getValueHashCode",
                NativeEngineLayerRoutes.pbrMaterialExtensionsGetHashCode(open(), hash));
        return hash[0];
    }

    /**
     * Returns CNA's own description of the material.
     *
     * @return the description
     */
    @Override
    public String toString() {
        long extensions;
        synchronized (this) {
            if (closed) {
                return "PbrMaterialExtensions[closed]";
            }
            extensions = handle;
        }
        long[] bytes = new long[1];
        int probe = NativeEngineLayerRoutes
                .pbrMaterialExtensionsCopyToString(extensions, new byte[0], bytes);
        if (probe != RESULT_BUFFER_TOO_SMALL) {
            GraphicsExtension.check("PbrMaterialExtensions.toString", probe);
        }
        int length = Math.toIntExact(bytes[0]);
        if (length == 0) {
            return "";
        }
        byte[] destination = new byte[length];
        GraphicsExtension.check("PbrMaterialExtensions.toString", NativeEngineLayerRoutes
                .pbrMaterialExtensionsCopyToString(extensions, destination, bytes));
        return new String(destination, 0, Math.toIntExact(bytes[0]), StandardCharsets.UTF_8);
    }

    /** Releases the material. The textures it named are untouched. Closing twice is a no-op. */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        synchronized (slots) {
            slots.clear();
        }
        GraphicsExtension.check("PbrMaterialExtensions.close",
                NativeEngineLayerRoutes.pbrMaterialExtensionsDestroy(handle));
    }

    /** A float CNA answers about one material. */
    @FunctionalInterface
    private interface FloatRoute {
        int call(long extensions, float[] answer);
    }

    /** A boolean CNA answers about one material. */
    @FunctionalInterface
    private interface FlagRoute {
        int call(long extensions, boolean[] answer);
    }

    /** A texture handle CNA takes for one material. */
    @FunctionalInterface
    private interface SetTextureRoute {
        int call(long extensions, long texture);
    }

    private float readFloat(String operation, FloatRoute route) {
        float[] answer = new float[1];
        GraphicsExtension.check(operation, route.call(open(), answer));
        return answer[0];
    }

    private boolean readFlag(String operation, FlagRoute route) {
        boolean[] answer = new boolean[1];
        GraphicsExtension.check(operation, route.call(open(), answer));
        return answer[0];
    }

    /**
     * Returns what this material put in a slot.
     *
     * <p>Answered from the retained reference rather than from CNA, and deliberately: CNA's own
     * slot getters mint a <em>fresh borrowed handle</em> on every call, which the caller then has
     * to release, so asking it a question this object already knows the answer to would allocate
     * and free a native handle on every read and hand back a texture facade nobody owns. Nothing
     * outside this class can write a slot, and {@code copyFrom} copies the retained references
     * with the value, so the two cannot disagree.
     */
    private Texture2D slot(String name) {
        open();
        synchronized (slots) {
            return slots.get(name);
        }
    }

    private void setSlot(String name, Texture2D value, SetTextureRoute route) {
        GraphicsExtension.check("PbrMaterialExtensions." + name, route.call(open(),
                value == null ? 0L : NativeBindings.nativeResourceHandle(value)));
        synchronized (slots) {
            if (value == null) {
                slots.remove(name);
            } else {
                slots.put(name, value);
            }
        }
    }

    /** The native handle, for the effect that shades through a material. */
    long handle() {
        return open();
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This PbrMaterialExtensions is closed");
            }
        }
        return handle;
    }
}
