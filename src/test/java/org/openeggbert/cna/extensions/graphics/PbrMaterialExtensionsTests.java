package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.SurfaceFormat;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import Microsoft.Xna.Framework.Vector3;
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
 * The glTF material extensions, against the live runtime.
 *
 * <p>VERIFIED_PURE for everything but the texture slots, which need a device only because a
 * texture does. The evidence worth having is the corrections: CNA uses <em>three</em> different
 * shapes in this one class -- clamp, guard, and floor -- and which field uses which is exactly
 * what a projection loses silently.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class PbrMaterialExtensionsTests {

    @Test
    void theThreeCorrectionShapesAreThreeDifferentBehaviours() {
        try (PbrMaterialExtensions material = PbrMaterialExtensions.create()) {
            // Clamped: the value is corrected into range and stored.
            material.setClearcoatFactor(5.0f);
            assertEquals(1.0f, material.getClearcoatFactor(), "a clamped factor stops at one");
            material.setClearcoatFactor(-5.0f);
            assertEquals(0.0f, material.getClearcoatFactor(), "and at zero");

            // Guarded: an out-of-range value is ignored and the previous one stands. That is a
            // different observable behaviour from clamping and the two are one line apart in
            // the same class.
            material.setClearcoatNormalScale(2.5f);
            assertEquals(2.5f, material.getClearcoatNormalScale(), 1.0e-5f);
            material.setClearcoatNormalScale(-1.0f);
            assertEquals(2.5f, material.getClearcoatNormalScale(), 1.0e-5f,
                    "a guarded setter leaves the previous value standing");

            // Floored: the third shape, which writes zero rather than keeping what was there.
            material.setAttenuationDistance(4.0f);
            material.setAttenuationDistance(-1.0f);
            assertEquals(0.0f, material.getAttenuationDistance(),
                    "a floored setter writes the bound rather than keeping the old value");

            // And the guard whose bound is not zero: an index of refraction below one describes
            // a medium light speeds up in, which is not a material.
            material.setIridescenceIor(1.8f);
            material.setIridescenceIor(0.5f);
            assertEquals(1.8f, material.getIridescenceIor(), 1.0e-5f,
                    "an index of refraction below one is ignored, not clamped to zero");

            // A colour clamps per channel rather than being refused as a whole.
            material.setSheenColorFactor(new Vector3(2.0f, 0.5f, -1.0f));
            assertEquals(new Vector3(1.0f, 0.5f, 0.0f), material.getSheenColorFactor(),
                    "each channel is clamped on its own");
        }
    }

    @Test
    void aNeutralMaterialSaysSoAndStopsSayingSo() {
        try (PbrMaterialExtensions material = PbrMaterialExtensions.create()) {
            // A fresh material asks the shader for nothing, which is what makes it free.
            assertTrue(material.isNeutral(), "a new material is neutral");
            assertFalse(material.isSheenEnabled());
            assertFalse(material.isTransmissionEnabled());
            assertFalse(material.isIridescenceEnabled());
            assertFalse(material.isSubsurfaceEnabled());

            // Turning one feature on turns exactly that feature on, and stops the material
            // being neutral. A projection that wrote the wrong field would still round-trip and
            // would fail this.
            material.setSheenRoughness(0.5f);
            material.setSheenColorFactor(new Vector3(1f, 1f, 1f));
            assertTrue(material.isSheenEnabled(), "sheen with a colour is sheen");
            assertFalse(material.isNeutral(), "and a material with sheen is not neutral");
            assertFalse(material.isTransmissionEnabled(), "and nothing else came on with it");

            material.setTransmissionFactor(0.75f);
            assertTrue(material.isTransmissionEnabled());
            assertEquals(0.75f, material.getTransmissionFactor(), 1.0e-5f);
        }
    }

    @Test
    void copyingTakesTheValueAndTheSlotsWithIt() {
        GameProbe.run(probe -> {
            try (PbrMaterialExtensions source = PbrMaterialExtensions.create();
                 PbrMaterialExtensions copy = PbrMaterialExtensions.create();
                 Texture2D sheen = new Texture2D(probe.device(), 4, 4, false,
                         SurfaceFormat.Color)) {
                assertTrue(source.matches(copy), "two neutral materials match");
                assertEquals(source.getValueHashCode(), copy.getValueHashCode(),
                        "and share CNA's value hash");

                source.setClearcoatFactor(0.5f);
                source.setSheenColorTexture(sheen);
                assertSame(sheen, source.getSheenColorTexture(),
                        "the slot hands back the texture it was given, not a facade");
                assertFalse(source.matches(copy));

                copy.copyFrom(source);
                assertTrue(source.matches(copy), "a copy holds the same value");
                assertEquals(source.getValueHashCode(), copy.getValueHashCode());
                assertSame(sheen, copy.getSheenColorTexture(),
                        "and the same texture, still the caller's object");

                // Changing the source must not change the copy.
                source.setClearcoatFactor(0.25f);
                assertFalse(source.matches(copy));

                // Emptying a slot empties it, and the texture is untouched.
                copy.setSheenColorTexture(null);
                assertNull(copy.getSheenColorTexture());
                assertEquals(4, sheen.getWidth(), "the material never owned the texture");

                assertFalse(source.toString().isBlank(), "CNA describes the material");
                assertNotEquals(source.toString(), copy.toString());
                assertThrows(NullPointerException.class, () -> copy.copyFrom(null));
            }
        });
    }

    @Test
    void aClosedMaterialRefusesEveryOperation() {
        PbrMaterialExtensions material = PbrMaterialExtensions.create();
        material.close();
        material.close();
        assertThrows(IllegalStateException.class, material::isNeutral);
        assertEquals("PbrMaterialExtensions[closed]", material.toString(),
                "a closed material describes itself rather than reaching a released handle");
    }
}
