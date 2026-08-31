package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.SurfaceFormat;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import Microsoft.Xna.Framework.Graphics.Texture3D;
import Microsoft.Xna.Framework.Vector3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The {@code .cube} lookup table and the pass that applies it.
 *
 * <p>Parsing needs no device, so most of this is VERIFIED_PURE: a table written here by hand is
 * read back entry by entry, which is the strongest thing that can be said about a parser. The
 * pass's strip validation is checked from both sides -- a size that describes a strip and one
 * that does not.
 *
 * <p>The volume slot is checked the same way, and needed to be: it took a {@code TextureCube}
 * until a probe bound a real table to it, and CNA refuses a cube map there. Nothing here had ever
 * bound one, so the wrong signature had nothing to fail against.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class ColorGradeTests {

    /** A two-entry-per-axis table that swaps red and blue, written the way a tool exports one. */
    private static final String SWAP_RED_AND_BLUE = """
            TITLE "Swap Red And Blue"
            LUT_3D_SIZE 2
            DOMAIN_MIN 0.0 0.0 0.0
            DOMAIN_MAX 1.0 1.0 1.0
            0.0 0.0 0.0
            0.0 0.0 1.0
            0.0 1.0 0.0
            0.0 1.0 1.0
            1.0 0.0 0.0
            1.0 0.0 1.0
            1.0 1.0 0.0
            1.0 1.0 1.0
            """;

    @Test
    void aTableIsReadBackEntryByEntry() {
        GameProbe.run(probe -> {
            try (CubeLut lut = CubeLut.parse(SWAP_RED_AND_BLUE)) {
                assertEquals(2, lut.getSize(), "two entries per axis");
                assertEquals("Swap Red And Blue", lut.getTitle(),
                        "the title the file declared");
                assertTrue(lut.isUnitDomain(), "the declared domain is the unit cube");
                assertEquals(new Vector3(0f, 0f, 0f), lut.getDomainMinimum());
                assertEquals(new Vector3(1f, 1f, 1f), lut.getDomainMaximum());

                // The entries come back in the order a .cube file writes them -- red fastest --
                // so the corner that was written fifth is the one at red index one. Getting the
                // index order wrong is the classic .cube bug and this is what catches it.
                assertEquals(new Vector3(0f, 0f, 0f), lut.getEntry(0, 0, 0));
                assertEquals(new Vector3(0f, 0f, 1f), lut.getEntry(1, 0, 0),
                        "red varies fastest in a .cube file");
                assertEquals(new Vector3(0f, 1f, 0f), lut.getEntry(0, 1, 0));
                assertEquals(new Vector3(1f, 0f, 0f), lut.getEntry(0, 0, 1));
                assertEquals(new Vector3(1f, 1f, 1f), lut.getEntry(1, 1, 1));

                // An index outside the table is refused rather than clamped, because a clamped
                // index would silently read a different colour.
                assertThrows(IllegalArgumentException.class, () -> lut.getEntry(2, 0, 0));
                assertThrows(IllegalArgumentException.class, () -> lut.getEntry(-1, 0, 0));
            }
        });
    }

    @Test
    void theParserRefusesWhatItCannotReadButNamesTheWrongReason() {
        GameProbe.run(probe -> {
            // Every one of these is a different way a .cube file goes wrong, and CNA refuses
            // each rather than filling the gaps with zeroes -- which would be a grade nobody
            // authored. What it gets wrong is the *reason*: the header documents
            // INVALID_ARGUMENT and the parser's own exception escapes the route's catch into
            // the barrier's capability arm, so a typo arrives as "this renderer cannot grade".
            // JAVA-UPSTREAM-009, reproduced in C by tools/native-abi/probes/cube_lut_refusal.c.
            // Asserted as it is rather than as it should be, so an upstream fix fails here.
            for (String malformed : new String[] {
                "",
                "DOMAIN_MIN 0 0 0\n",
                "LUT_3D_SIZE 2\n0.0 0.0 0.0\n",
                "LUT_3D_SIZE 2\nDOMAIN_MIN nonsense\n"}) {
                assertThrows(ExtensionNotSupportedException.class,
                        () -> CubeLut.parse(malformed),
                        "a malformed table must be refused: " + malformed);
            }
            assertThrows(RuntimeException.class,
                    () -> CubeLut.load(Path.of("/nonexistent/grade.cube")),
                    "a file that is not there");
            assertThrows(NullPointerException.class, () -> CubeLut.parse(null));
        });
    }

    @Test
    void aStripIsSlicesSideBySideAndTheShapeIsChecked() {
        GameProbe.run(probe -> {
            // A strip is N slices of N by N, so its width is the square of its height. The pass
            // works that out from the pixel size, and answers zero for a size that describes no
            // strip -- which is how a game validates a texture an artist supplied.
            assertEquals(16, ColorGradePass.lutSizeForStrip(256, 16));
            assertEquals(32, ColorGradePass.lutSizeForStrip(1024, 32));
            assertEquals(0, ColorGradePass.lutSizeForStrip(255, 16),
                    "a width that is not the square of the height is no strip");
            assertEquals(0, ColorGradePass.lutSizeForStrip(0, 0));

            try (CubeLut lut = CubeLut.parse(SWAP_RED_AND_BLUE);
                 Texture2D strip = lut.createStripTexture(probe.device())) {
                // The table's own texture has exactly the shape the pass expects.
                assertEquals(lut.getSize() * lut.getSize(), strip.getWidth());
                assertEquals(lut.getSize(), strip.getHeight());
                assertEquals(lut.getSize(),
                        ColorGradePass.lutSizeForStrip(strip.getWidth(), strip.getHeight()),
                        "the texture the table built is a strip the pass recognises");
            }
        });
    }

    @Test
    void theVolumeSlotTakesACubicalVolumeAndRefusesEverythingElse() {
        GameProbe.run(probe -> {
            // A volume table is a Texture3D, and not every renderer has real 3D texture storage:
            // HEADLESS refuses to allocate one at all. Both sides are qualified rather than one
            // skipped -- where the storage exists the slot is exercised, and where it does not the
            // refusal is checked to be the exact one CNA documents.
            if (!RendererCapabilities.supports(probe.device(), GraphicsCapability.Texture3D)) {
                RuntimeException refused = assertThrows(RuntimeException.class,
                        () -> new Texture3D(probe.device(), 8, 8, 8, false, SurfaceFormat.Color));
                assertTrue(refused.getMessage().contains("volume"),
                        "the refusal says what is missing: " + refused.getMessage());
                // And the bounds are still the pass's own, whatever the renderer can allocate.
                assertEquals(2, ColorGradePass.MIN_VOLUME_LUT_EDGE);
                assertEquals(64, ColorGradePass.MAX_VOLUME_LUT_EDGE);
                return;
            }
            try (ColorGradePass pass = ColorGradePass.create(probe.device());
                 Texture3D cubical = new Texture3D(probe.device(), 8, 8, 8, false,
                         SurfaceFormat.Color);
                 Texture3D slab = new Texture3D(probe.device(), 8, 8, 4, false,
                         SurfaceFormat.Color);
                 Texture3D single = new Texture3D(probe.device(), 1, 1, 1, false,
                         SurfaceFormat.Color)) {
                assertNull(pass.getVolumeLut(), "nothing is bound yet");

                pass.setVolumeLut(cubical);
                assertSame(cubical, pass.getVolumeLut(),
                        "the pass hands back the table it was given");

                // Not cubical, so there is no edge length to read a slice count from. The
                // message matters as much as the type here: CNA refuses the same table with the
                // same exception type, and what the Java check adds is a sentence that names the
                // shape the caller actually passed. Asserting only the type passed with the
                // check deleted, which is how this assertion came to read the message.
                IllegalArgumentException notCubical = assertThrows(IllegalArgumentException.class,
                        () -> pass.setVolumeLut(slab));
                assertTrue(notCubical.getMessage().contains("8x8x4"),
                        "the refusal names the shape it was given: " + notCubical.getMessage());
                // One texel has no two entries to interpolate between, which is why CNA's floor
                // is two rather than one.
                IllegalArgumentException tooSmall = assertThrows(IllegalArgumentException.class,
                        () -> pass.setVolumeLut(single));
                assertTrue(tooSmall.getMessage().contains("between 2 and 64"),
                        "the refusal states the bounds: " + tooSmall.getMessage());
                assertSame(cubical, pass.getVolumeLut(),
                        "a refused table does not displace the one that was working");

                // The bounds are CNA's, so they are worth stating rather than assuming.
                assertEquals(2, ColorGradePass.MIN_VOLUME_LUT_EDGE);
                assertEquals(64, ColorGradePass.MAX_VOLUME_LUT_EDGE);

                pass.setVolumeLut(null);
                assertNull(pass.getVolumeLut(), "null unbinds");
            }
        });
    }

    @Test
    void thePassHoldsTheTableItWasGivenAndRefusesOneItCannotUse() {
        GameProbe.run(probe -> {
            try (ColorGradePass pass = ColorGradePass.create(probe.device());
                 Texture2D identity = ColorGradePass.createIdentityLut(probe.device(), 8);
                 Texture2D notAStrip = new Texture2D(probe.device(), 8, 8, false,
                         Microsoft.Xna.Framework.Graphics.SurfaceFormat.Color)) {
                assertEquals(64, identity.getWidth(), "an eight-slice strip is 64 by 8");
                assertEquals(8, identity.getHeight());

                assertNull(pass.getLut(), "nothing is bound yet");
                pass.setLut(identity);
                assertSame(identity, pass.getLut(),
                        "the pass hands back the texture it was given");

                // Eight by eight is not a strip -- its width is not the square of its height --
                // and the pass refuses it rather than sampling the wrong slice count, which
                // would grade the frame into colours nothing in the table names.
                assertThrows(IllegalArgumentException.class, () -> pass.setLut(notAStrip));
                assertSame(identity, pass.getLut(), "a refused table did not replace the bound one");

                pass.setLut(null);
                assertNull(pass.getLut(), "an invalid handle unbinds");

                pass.setStrength(0.25f);
                assertEquals(0.25f, pass.getStrength(), 1.0e-5f);
                pass.setInterpolation(LutInterpolation.Tetrahedral);
                assertEquals(LutInterpolation.Tetrahedral, pass.getInterpolation());
                assertThrows(NullPointerException.class, () -> pass.setInterpolation(null));

                assertFalse(pass.getName().isBlank());
            }
        });
    }
}
