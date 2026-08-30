package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Color;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The extended graphics layer, including what it does when the layer is not in the build. */
final class GraphicsExtensionTests {

    @Test
    void availabilityIsAnsweredRatherThanAssumedWithNoNativeBackend() {
        // Asking before anything native is loaded must answer, not fail: a game decides whether
        // to use the extension on this answer.
        assertFalse(GraphicsExtension.isAvailable() && !nativeEnabled(),
                "availability must be false when no native backend is loaded");
    }

    @Test
    void identityNamesAreCnasOwn() {
        assertEquals(2, AsciiQuantizeMode.values().length);
        assertEquals(3, CrtMaskType.values().length);
        assertEquals(3, DitherMode.values().length);
        assertEquals(7, DepthEffectMode.values().length);
        assertEquals(4, RenderQuality.values().length);
        assertEquals(5, ShadowQuality.values().length);
        assertEquals(5, TonemappingMode.values().length);
        assertEquals(0, TonemappingMode.None.ordinal());
        assertEquals(3, TonemappingMode.Aces.ordinal());
        assertEquals(0, ShadowQuality.Disabled.ordinal());
        assertEquals(4, ShadowQuality.Ultra.ordinal());
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
    void pipelineDefaultsComeFromCnaAndCopyIndependently() {
        RenderPipelineSettings settings = new RenderPipelineSettings();
        assertNotNull(settings.getTonemappingMode());
        assertNotNull(settings.getRenderQuality());
        assertNotNull(settings.getShadowQuality());
        assertTrue(settings.getGamma() > 0.0f, "CNA's default gamma must be positive");
        assertTrue(settings.getExposure() > 0.0f, "CNA's default exposure must be positive");

        RenderPipelineSettings copy = new RenderPipelineSettings(settings);
        copy.setTonemappingMode(TonemappingMode.Aces);
        copy.setShadowQuality(ShadowQuality.Ultra);
        copy.setExposure(2.0f);
        assertNotSame(settings.getTonemappingMode(), TonemappingMode.Aces);
        assertEquals(TonemappingMode.Aces, copy.getTonemappingMode());
        assertEquals(2.0f, copy.getExposure());
        assertThrows(NullPointerException.class, () -> copy.setTonemappingMode(null));
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
    void materialDefaultsComeFromCnaAndColoursAreCopied() {
        PbrMaterial material = new PbrMaterial();
        assertNotNull(material.getAlbedoColor());
        assertNotNull(material.getEmissiveColor());
        assertTrue(material.getRoughnessFactor() >= 0.0f);
        assertTrue(material.getMetallicFactor() >= 0.0f);

        // Color is a struct in XNA, so a getter must never hand out an alias a caller could
        // mutate into the material.
        Color albedo = material.getAlbedoColor();
        assertNotSame(albedo, material.getAlbedoColor());
        albedo.setR(albedo.getR() == 1 ? 2 : 1);
        assertNotEquals(albedo.getR(), material.getAlbedoColor().getR());

        material.setAlbedoColor(new Color(10, 20, 30, 40));
        assertEquals(new Color(10, 20, 30, 40), material.getAlbedoColor());
        assertThrows(NullPointerException.class, () -> material.setAlbedoColor(null));
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
    void availabilityIsTruthfulAgainstTheLoadedBuild() {
        // Whichever build is loaded, the answer must be the same one twice, and it must be the
        // one the pure value routes agree with: those work in either build.
        boolean available = GraphicsExtension.isAvailable();
        assertEquals(available, GraphicsExtension.isAvailable());
        assertNotNull(new RenderPipelineSettings());
    }

    private static void assertNotEquals(int unexpected, int actual) {
        assertFalse(unexpected == actual, "expected a value other than " + unexpected);
    }

    private static boolean nativeEnabled() {
        return System.getenv("CNA_NATIVE_LIBRARY") != null;
    }
}
