package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import Microsoft.Xna.Framework.Vector2;
import Microsoft.Xna.Framework.Vector3;
import Microsoft.Xna.Framework.Vector4;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The bridge from a glTF material to one a renderer can draw with, against the live runtime.
 *
 * <p><strong>What this can and cannot say.</strong> VERIFIED_PURE for the core material -- the
 * bridge is arithmetic over two structures and touches no device -- and VERIFIED_HEADLESS_GAME
 * only where a real texture is needed to prove a slot carried it. Nothing here renders.
 *
 * <p>What is checked is the conversion: that every factor arrives where it belongs, that the one
 * value CNA does not carry exactly is the one CNA says, and that the defaults are the glTF
 * specification's rather than zero.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class GltfMaterialBridgeTests {

    @Test
    void theDefaultsAreTheSpecificationsRatherThanZero() {
        // The values a material takes for every field the file leaves out. A source that
        // defaulted to zero would give a black, fully rough, fully metallic surface for a glTF
        // material that only names a texture -- which is the failure this checks for.
        GltfMaterialSource source = new GltfMaterialSource();
        assertEquals(1f, source.getBaseColorFactor().X, 1.0e-6f, "base colour defaults to white");
        assertEquals(1f, source.getBaseColorFactor().W, 1.0e-6f, "and opaque");
        assertEquals(1f, source.getMetallicFactor(), 1.0e-6f);
        assertEquals(1f, source.getRoughnessFactor(), 1.0e-6f);
        assertEquals(0f, source.getEmissiveFactor().X, 1.0e-6f, "and emits nothing");
        assertEquals(1f, source.getNormalScale(), 1.0e-6f);
        assertEquals(1f, source.getOcclusionStrength(), 1.0e-6f);
        assertEquals(1.5f, source.getIor(), 1.0e-6f, "glTF's default index of refraction");
        assertEquals(AlphaMode.Opaque, source.getAlphaMode());
        assertEquals(0.5f, source.getAlphaCutoff(), 1.0e-6f);
        assertFalse(source.isDoubleSided());
        for (PbrTextureSlot slot : PbrTextureSlot.values()) {
            assertNull(source.getTexture(slot));
            assertEquals(0, source.getTextureCoordinateSet(slot));
            assertEquals(TextureTransform.identity(), source.getTextureTransform(slot),
                    slot + " starts untransformed");
        }
    }

    @Test
    void everyFactorArrivesWhereItBelongs() {
        GltfMaterialSource source = new GltfMaterialSource();
        // Each value distinct, so a bridge that crossed two fields fails rather than agreeing.
        source.setBaseColorFactor(new Vector4(0.2f, 0.4f, 0.6f, 0.8f));
        source.setMetallicFactor(0.125f);
        source.setRoughnessFactor(0.25f);
        source.setEmissiveFactor(new Vector3(0.3f, 0.5f, 0.7f));
        source.setNormalScale(0.375f);
        source.setOcclusionStrength(0.625f);
        source.setIor(1.75f);
        source.setSpecularFactor(0.875f);
        source.setSpecularColorFactor(new Vector3(0.1f, 0.9f, 0.05f));
        source.setAlphaMode(AlphaMode.Mask);
        source.setAlphaCutoff(0.3f);
        source.setDoubleSided(true);

        PbrMaterialExt material = source.build();
        assertEquals(0.125f, material.getMetallicFactor(), 1.0e-5f);
        assertEquals(0.25f, material.getRoughnessFactor(), 1.0e-5f);
        assertEquals(0.375f, material.getNormalScale(), 1.0e-5f);
        assertEquals(0.625f, material.getOcclusionStrength(), 1.0e-5f);
        assertEquals(1.75f, material.getIor(), 1.0e-5f);
        assertEquals(0.875f, material.getSpecularFactor(), 1.0e-5f);
        assertEquals(0.3f, material.getAlphaCutoff(), 1.0e-5f);
        assertEquals(AlphaMode.Mask, material.getAlphaMode());
        assertTrue(material.isDoubleSided());
        assertEquals(0.3f, material.getEmissiveFactor().X, 1.0e-5f);
        assertEquals(0.5f, material.getEmissiveFactor().Y, 1.0e-5f);
        assertEquals(0.7f, material.getEmissiveFactor().Z, 1.0e-5f);
        assertEquals(0.9f, material.getSpecularColorFactor().Y, 1.0e-5f);
    }

    @Test
    void theBaseColourIsQuantisedAndCnaSaysSo() {
        GltfMaterialSource source = new GltfMaterialSource();
        // The one value the bridge does not carry exactly, documented by CNA and asserted here
        // rather than glossed over: four floats become eight bits a channel.
        source.setBaseColorFactor(new Vector4(0.2f, 0.4f, 0.6f, 0.8f));
        PbrMaterialExt material = source.build();
        assertEquals(Math.round(0.2f * 255f), material.getAlbedoColor().getR(), 1);
        assertEquals(Math.round(0.4f * 255f), material.getAlbedoColor().getG(), 1);
        assertEquals(Math.round(0.6f * 255f), material.getAlbedoColor().getB(), 1);
        assertEquals(Math.round(0.8f * 255f), material.getAlbedoColor().getA(), 1);

        // And the quantisation is real rather than a rounding tolerance: two colours a
        // two-hundred-and-fifty-sixth apart land on the same byte.
        source.setBaseColorFactor(new Vector4(0.5f, 0.5f, 0.5f, 1f));
        int first = source.build().getAlbedoColor().getR();
        source.setBaseColorFactor(new Vector4(0.5f + 1f / 1024f, 0.5f, 0.5f, 1f));
        assertEquals(first, source.build().getAlbedoColor().getR(),
                "a difference finer than a byte does not survive");
        source.setBaseColorFactor(new Vector4(0.75f, 0.5f, 0.5f, 1f));
        assertNotEquals(first, source.build().getAlbedoColor().getR(),
                "a difference coarser than a byte does");
    }

    @Test
    void eachSlotKeepsItsOwnCoordinateSetTransformAndTexture() {
        GameProbe.run(probe -> {
            GraphicsDevice device = probe.device();
            try (Texture2D albedo = new Texture2D(device, 4, 4);
                 Texture2D normal = new Texture2D(device, 4, 4)) {
                GltfMaterialSource source = new GltfMaterialSource();
                source.setTexture(PbrTextureSlot.BaseColor, albedo);
                source.setTexture(PbrTextureSlot.Normal, normal);
                for (PbrTextureSlot slot : PbrTextureSlot.values()) {
                    source.setTextureCoordinateSet(slot, slot.ordinal() % 2);
                    source.setTextureTransform(slot, new TextureTransform(
                            new Vector2(slot.ordinal() * 0.25f, slot.ordinal() * 0.5f),
                            new Vector2(slot.ordinal() + 2f, slot.ordinal() + 3f),
                            slot.ordinal() * 0.2f));
                }

                PbrMaterialExt material = source.build();
                // Seven slots crossing as one structure with two fixed arrays inside it. A
                // flattening that started the transforms at the wrong leaf, or that wrote every
                // element to the first, fails on at least six of these.
                for (PbrTextureSlot slot : PbrTextureSlot.values()) {
                    assertEquals(slot.ordinal() % 2, material.getTextureCoordinateSet(slot),
                            slot + " coordinate set");
                    TextureTransform transform = material.getTextureTransform(slot);
                    assertEquals(slot.ordinal() * 0.25f, transform.offset().X, 1.0e-5f,
                            slot + " offset");
                    assertEquals(slot.ordinal() + 2f, transform.scale().X, 1.0e-5f,
                            slot + " scale");
                    assertEquals(slot.ordinal() * 0.2f, transform.rotation(), 1.0e-5f,
                            slot + " rotation");
                }

                // And the textures came across as the objects a loader resolved, not as copies.
                assertSame(albedo, material.getTexture(PbrTextureSlot.BaseColor));
                assertSame(normal, material.getTexture(PbrTextureSlot.Normal));
                assertNull(material.getTexture(PbrTextureSlot.Emissive));
            }
        });
    }

    @Test
    void theExtensionSourceFillsAnExtensionsObjectInOneCall() {
        GameProbe.run(probe -> {
            GraphicsDevice device = probe.device();
            try (PbrMaterialExtensions extensions = PbrMaterialExtensions.create();
                 Texture2D clearcoat = new Texture2D(device, 4, 4);
                 Texture2D sheen = new Texture2D(device, 4, 4)) {
                assertTrue(extensions.isNeutral(), "a fresh extensions set is neutral");

                GltfMaterialExtensionSource source = new GltfMaterialExtensionSource();
                source.setClearcoatFactor(0.25f);
                source.setClearcoatRoughnessFactor(0.375f);
                source.setSheenColorFactor(new Vector3(0.1f, 0.2f, 0.3f));
                source.setSheenRoughnessFactor(0.5f);
                source.setTransmissionFactor(0.625f);
                source.setThicknessFactor(0.75f);
                source.setAttenuationDistance(12.5f);
                source.setAttenuationColor(new Vector3(0.4f, 0.5f, 0.6f));
                source.setIridescenceFactor(0.875f);
                source.setIridescenceIor(1.8f);
                source.setIridescenceThicknessMinimum(120f);
                source.setIridescenceThicknessMaximum(480f);
                source.setTexture(0, clearcoat);
                source.setTexture(3, sheen);

                source.buildInto(extensions);

                // Twelve factors and nine slots written by one call, and each read back through
                // the getter that names it -- so a source that packed two factors into one leaf
                // fails rather than agreeing on the total.
                assertEquals(0.25f, extensions.getClearcoatFactor(), 1.0e-5f);
                assertEquals(0.375f, extensions.getClearcoatRoughness(), 1.0e-5f);
                assertEquals(0.2f, extensions.getSheenColorFactor().Y, 1.0e-5f);
                assertEquals(0.5f, extensions.getSheenRoughness(), 1.0e-5f);
                assertEquals(0.625f, extensions.getTransmissionFactor(), 1.0e-5f);
                assertEquals(0.75f, extensions.getThicknessFactor(), 1.0e-5f);
                assertEquals(12.5f, extensions.getAttenuationDistance(), 1.0e-5f);
                assertEquals(0.5f, extensions.getAttenuationColor().Y, 1.0e-5f);
                assertEquals(0.875f, extensions.getIridescenceFactor(), 1.0e-5f);
                assertEquals(1.8f, extensions.getIridescenceIor(), 1.0e-5f);
                assertEquals(120f, extensions.getIridescenceThicknessMinimum(), 1.0e-4f);
                assertEquals(480f, extensions.getIridescenceThicknessMaximum(), 1.0e-4f);

                // The retained references follow the value, or the getters would answer with
                // what the object held before the write.
                assertSame(clearcoat, extensions.getClearcoatTexture());
                assertSame(sheen, extensions.getSheenColorTexture());
                assertNull(extensions.getTransmissionTexture());

                // And CNA agrees it is no longer neutral, which is the question a renderer asks
                // before spending a shader variant on it.
                assertFalse(extensions.isNeutral());
                assertTrue(extensions.isSheenEnabled());
                assertTrue(extensions.isTransmissionEnabled());

                assertThrows(NullPointerException.class, () -> source.buildInto(null));
            }
        });
    }

    @Test
    void theExtensionDefaultsAreNeutral() {
        GameProbe.run(probe -> {
            try (PbrMaterialExtensions extensions = PbrMaterialExtensions.create()) {
                // A file that names no extension must produce a material that behaves as if it
                // had none. Building the untouched defaults into a set and finding it still
                // neutral is what says the defaults are the specification's.
                new GltfMaterialExtensionSource().buildInto(extensions);
                assertTrue(extensions.isNeutral(),
                        "the specification's defaults are the neutral material");
                assertFalse(extensions.isSheenEnabled());
                assertFalse(extensions.isTransmissionEnabled());
                assertFalse(extensions.isIridescenceEnabled());
            }
        });
    }

    @Test
    void nullsAreRefusedBeforeAnythingNativeHappens() {
        GltfMaterialSource source = new GltfMaterialSource();
        assertThrows(NullPointerException.class, () -> source.setBaseColorFactor(null));
        assertThrows(NullPointerException.class, () -> source.setAlphaMode(null));
        assertThrows(NullPointerException.class, () -> source.getTexture(null));
        assertThrows(NullPointerException.class,
                () -> source.setTextureTransform(PbrTextureSlot.Normal, null));
        GltfMaterialExtensionSource extension = new GltfMaterialExtensionSource();
        assertThrows(NullPointerException.class, () -> extension.setSheenColorFactor(null));
        assertThrows(NullPointerException.class, () -> extension.setAttenuationColor(null));
    }
}
