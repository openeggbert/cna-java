package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Vector3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Area lights and the clustered forward effect, against the live runtime.
 *
 * <p>Both carry their shading as pure functions, which is where the evidence is: a light that is
 * a surface has to light a surface facing it more than one facing away, a bigger surface has to
 * light more, and a light past its range has to light nothing. Those are checkable exactly, with
 * no device and no frame.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class AreaLightTests {

    @Test
    void anAreaLightsQuadFollowsItsShape() {
        AreaLight rectangle = AreaLight.createDefault();
        assertEquals(AreaLightShape.Rectangle, rectangle.getShape(), "CNA's own default");
        assertTrue(rectangle.isValid());

        // The quad is the shape integrated: four corners around the light's centre, and its
        // extent is the axes' lengths -- which is what makes right_axis a *half*-axis.
        AreaLight window = rectangle
                .withPosition(new Vector3(0f, 3f, 0f))
                .withAxes(new Vector3(2f, 0f, 0f), new Vector3(0f, 1f, 0f));
        List<Vector3> quad = window.getQuad(new Vector3(0f, 0f, 5f));
        assertEquals(AreaLight.QuadCornerCount, quad.size());
        float widest = 0.0f;
        float tallest = 0.0f;
        for (Vector3 corner : quad) {
            widest = Math.max(widest, Math.abs(corner.X));
            tallest = Math.max(tallest, Math.abs(corner.Y - 3.0f));
        }
        assertEquals(2.0f, widest, 1.0e-4f, "the right axis is half the width");
        assertEquals(1.0f, tallest, 1.0e-4f, "and the up axis half the height");

        // A disc scales the same axes so a polygon matches the disc's area, so its quad is not
        // the rectangle's -- which is the whole reason the shape is a field rather than a
        // second type.
        List<Vector3> disc = window.withShape(AreaLightShape.Disc)
                .getQuad(new Vector3(0f, 0f, 5f));
        assertNotEquals(quad.get(0), disc.get(0), "a disc is not integrated as its rectangle");

        // A tube is billboarded: its quad depends on where it is seen from, and the others do
        // not.
        AreaLight tube = window.withShape(AreaLightShape.Tube);
        assertNotEquals(tube.getQuad(new Vector3(0f, 0f, 5f)).get(0),
                tube.getQuad(new Vector3(5f, 0f, 0f)).get(0),
                "a tube turns to face the surface");
        assertEquals(quad.get(0), window.getQuad(new Vector3(5f, 0f, 0f)).get(0),
                "and a rectangle does not");
    }

    @Test
    void anAreaLightLightsWhatFacesItAndNothingBeyondItsRange() {
        AreaLight light = AreaLight.createDefault()
                .withPosition(new Vector3(0f, 5f, 0f))
                .withAxes(new Vector3(1f, 0f, 0f), new Vector3(0f, 0f, 1f))
                .withColor(new Vector3(1f, 1f, 1f))
                .withIntensity(10.0f)
                .withRange(20.0f);
        Vector3 surface = new Vector3(0f, 0f, 0f);
        Vector3 camera = new Vector3(0f, 1f, 5f);
        Vector3 albedo = new Vector3(0.8f, 0.8f, 0.8f);

        Vector3 facing = light.getContribution(surface, new Vector3(0f, 1f, 0f), camera, albedo,
                0.0f, 0.5f);
        Vector3 away = light.getContribution(surface, new Vector3(0f, -1f, 0f), camera, albedo,
                0.0f, 0.5f);
        assertTrue(facing.X > 0.0f, "a surface under a ceiling light is lit: " + facing);
        assertTrue(facing.X > away.X, "and one facing away is not: " + away);

        // Past the range there is nothing, which is what range means.
        Vector3 far = light.getContribution(new Vector3(0f, -100f, 0f), new Vector3(0f, 1f, 0f),
                camera, albedo, 0.0f, 0.5f);
        assertEquals(0.0f, far.X, 1.0e-5f, "a hundred units under a twenty-unit light is dark");

        // A bigger emitter puts out more light from the same intensity, because it is a
        // surface: doubling the axes doubles the area.
        Vector3 bigger = light.withAxes(new Vector3(2f, 0f, 0f), new Vector3(0f, 0f, 2f))
                .getContribution(surface, new Vector3(0f, 1f, 0f), camera, albedo, 0.0f, 0.5f);
        assertTrue(bigger.X > facing.X,
                "a larger surface emits more: " + bigger.X + " against " + facing.X);

        // The coverage helper is the same integration exposed on its own, and a lobe pointed at
        // the light covers more of it than one pointed away.
        float scale = AreaLight.getLobeScaleFor(0.5f);
        List<Vector3> quad = light.getQuad(surface);
        float towards = AreaLight.getCoverage(quad, surface, new Vector3(0f, 1f, 0f), scale,
                false);
        float sideways = AreaLight.getCoverage(quad, surface, new Vector3(1f, 0f, 0f), scale,
                false);
        assertTrue(towards > sideways, "a lobe pointed at the light covers more of it");
        assertFalse(AreaLight.getShadingGlsl().isBlank());
        assertThrows(IllegalArgumentException.class,
                () -> AreaLight.getCoverage(List.of(new Vector3()), surface,
                        new Vector3(0f, 1f, 0f), scale, false));
    }

    @Test
    void theBrdfTableIsBuiltAndItsEntriesAreTheDirectEvaluation() {
        GameProbe.run(probe -> {
            // Evaluated directly, with no table at all: a rougher surface spreads its lobe, so
            // the terms differ, and the same arguments always agree with themselves.
            AreaLightBrdfTerms smooth = AreaLightBrdfTable.evaluate(0.05f, 1.0f, 64);
            AreaLightBrdfTerms rough = AreaLightBrdfTable.evaluate(0.95f, 1.0f, 64);
            assertNotEquals(smooth.magnitude(), rough.magnitude(),
                    "roughness must change the lobe");
            assertEquals(smooth.magnitude(), AreaLightBrdfTable.evaluate(0.05f, 1.0f, 64)
                    .magnitude(), "the same arguments give the same answer");
            assertTrue(smooth.magnitude() >= 0.0f && smooth.magnitude() <= 1.0f,
                    "energy is a fraction: " + smooth.magnitude());

            try (AreaLightBrdfTable table = AreaLightBrdfTable.create(probe.device(), 16, 32)) {
                assertEquals(16, table.getSize());
                assertEquals(32, table.getSampleCount());
                assertTrue(table.getGenerationMilliseconds() >= 0.0,
                        "building it took some time, or none, but never a negative one");
                assertFalse(AreaLightBrdfTable.getLookupGlsl().isBlank());
            }
            try (AreaLightBrdfTable table = AreaLightBrdfTable.create(probe.device())) {
                assertTrue(table.getSize() > 0, "the default table has a size");
            }
        });
    }

    @Test
    void theClusteredEffectShadesOnTheCpuTheWayItsShaderWould() {
        GameProbe.run(probe -> {
            ClusteredLight light = ClusteredLight.createDefault()
                    .withPosition(new Vector3(0f, 4f, 0f))
                    .withRange(20.0f)
                    .withIntensity(8.0f);
            Vector3 surface = new Vector3(0f, 0f, 0f);
            Vector3 camera = new Vector3(0f, 1f, 4f);
            Vector3 albedo = new Vector3(0.8f, 0.1f, 0.1f);

            Vector3 lit = ClusteredForwardEffect.contribution(light, surface,
                    new Vector3(0f, 1f, 0f), camera, albedo, 0f, 0.5f, 0f, 0f,
                    new Vector3(), 0f, 0f, 1.5f, 0f, new Vector3(), 0f);
            Vector3 unlit = ClusteredForwardEffect.contribution(light, surface,
                    new Vector3(0f, -1f, 0f), camera, albedo, 0f, 0.5f, 0f, 0f,
                    new Vector3(), 0f, 0f, 1.5f, 0f, new Vector3(), 0f);
            assertTrue(lit.X > unlit.X, "a surface facing the light is lit more");
            assertTrue(lit.X > lit.Y, "and a red surface reflects red");

            // The same shading through a material's extensions, which is the shape a game
            // actually has.
            try (PbrMaterialExtensions material = PbrMaterialExtensions.create()) {
                Vector3 plain = ClusteredForwardEffect.contribution(light, surface,
                        new Vector3(0f, 1f, 0f), camera, albedo, 0f, 0.5f, material);
                assertTrue(plain.X > 0f);
                material.setClearcoatFactor(1.0f);
                material.setClearcoatRoughness(0.0f);
                Vector3 lacquered = ClusteredForwardEffect.contribution(light, surface,
                        new Vector3(0f, 1f, 0f), camera, albedo, 0f, 0.5f, material);
                assertNotEquals(plain.X, lacquered.X,
                        "a clearcoat must change what the surface reflects");
            }

            // Beer's law: thicker glass is greener, and no thickness absorbs nothing.
            Vector3 clear = ClusteredForwardEffect.volumeAttenuation(
                    new Vector3(0.5f, 1f, 0.5f), 1.0f, 0.0f);
            Vector3 thick = ClusteredForwardEffect.volumeAttenuation(
                    new Vector3(0.5f, 1f, 0.5f), 1.0f, 4.0f);
            assertEquals(1.0f, clear.X, 1.0e-4f, "no thickness absorbs nothing");
            assertTrue(thick.X < clear.X, "thicker absorbs more");
            assertTrue(thick.Y > thick.X, "and the colour that survives is the one it is quoted at");
        });
    }

    @Test
    void theEffectRetainsWhatItNamesAndSaysWhatItHas() {
        GameProbe.run(probe -> {
            try (ClusteredForwardEffect effect = ClusteredForwardEffect.create(probe.device());
                 AreaLightBrdfTable table = AreaLightBrdfTable.create(probe.device(), 8, 8);
                 LightProbe probeLight = LightProbe.create();
                 PbrMaterialExtensions material = PbrMaterialExtensions.create()) {
                assertFalse(effect.hasAreaLight());
                assertFalse(effect.hasLightProbe());
                assertNull(effect.getAreaLight());
                assertNull(effect.getLightProbe());

                AreaLight light = AreaLight.createDefault();
                effect.setAreaLight(light, table);
                assertTrue(effect.hasAreaLight(), "CNA agrees the effect has one");
                assertEquals(light, effect.getAreaLight());
                effect.clearAreaLight();
                assertFalse(effect.hasAreaLight());
                assertNull(effect.getAreaLight());

                effect.setLightProbe(probeLight);
                assertTrue(effect.hasLightProbe());
                assertSame(probeLight, effect.getLightProbe(),
                        "the effect hands back the probe it was given");
                assertNull(effect.getLightProbeVolume(), "a probe is not a volume");
                effect.clearLightProbe();
                assertFalse(effect.hasLightProbe());

                effect.setMaterialExtensions(material);
                assertSame(material, effect.getMaterialExtensions());
                // There is no "none": CNA refuses an invalid handle here, so a material with no
                // extensions is a neutral one rather than an absent one.
                assertThrows(NullPointerException.class,
                        () -> effect.setMaterialExtensions(null));

                effect.setBaseColor(new Vector3(0.2f, 0.4f, 0.6f));
                assertEquals(new Vector3(0.2f, 0.4f, 0.6f), effect.getBaseColor());
                effect.setMetallic(0.75f);
                assertEquals(0.75f, effect.getMetallic(), 1.0e-5f);
                effect.setRoughness(0.25f);
                assertEquals(0.25f, effect.getRoughness(), 1.0e-5f);
                effect.setIor(1.45f);
                assertEquals(1.45f, effect.getIor(), 1.0e-5f);
                effect.setAmbient(new Vector3(0.1f, 0.1f, 0.1f));
                assertEquals(new Vector3(0.1f, 0.1f, 0.1f), effect.getAmbient());
                effect.isSupported();

                assertThrows(NullPointerException.class,
                        () -> effect.setAreaLight(null, table));
            }
        });
    }

    @Test
    void aClosedEffectOrTableRefusesEveryOperation() {
        GameProbe.run(probe -> {
            ClusteredForwardEffect effect = ClusteredForwardEffect.create(probe.device());
            AreaLightBrdfTable table = AreaLightBrdfTable.create(probe.device(), 8, 8);
            effect.close();
            effect.close();
            table.close();
            table.close();
            assertThrows(IllegalStateException.class, effect::getMetallic);
            assertThrows(IllegalStateException.class, table::getSize);
            assertThrows(IllegalStateException.class, table::getTexture);
            assertThrows(NullPointerException.class, () -> ClusteredForwardEffect.create(null));
        });
    }

    @Test
    void theTableHandsOutTheTextureItsShaderSamples() {
        GameProbe.run(probe -> {
            try (AreaLightBrdfTable table = AreaLightBrdfTable.create(probe.device(), 16, 8)) {
                Microsoft.Xna.Framework.Graphics.Texture2D texture = table.getTexture();
                if (texture == null) {
                    // The header allows it: a renderer that cannot store the table answers with
                    // nothing. Stated rather than asserted around, because a game must branch on
                    // it before it spends a shader variant on area lights.
                    return;
                }
                // The texture is the table: its edges are the size the table was built at, which
                // is what the shader's lookup code indexes by.
                assertEquals(table.getSize(), texture.getWidth(), "the table's own edge length");
                assertEquals(table.getSize(), texture.getHeight());

                // A fresh handle each call, each keeping the table alive, each to be disposed --
                // so two asks are two objects over one texture, and disposing one leaves the
                // other usable. A facade that released the table's texture would fail here.
                Microsoft.Xna.Framework.Graphics.Texture2D second = table.getTexture();
                assertNotSame(texture, second, "each ask is its own handle");
                texture.Dispose();
                assertEquals(table.getSize(), second.getWidth(),
                        "disposing one handle left the texture alone");
                assertEquals(table.getSize(), table.getSize(),
                        "and left the table alone too");
                second.Dispose();
            }
        });
    }
}
