package org.openeggbert.cna.extensions.graphics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Level-of-detail selection, against the live runtime.
 *
 * <p>Needs no graphics device, which is what makes it testable here: a LOD group is arithmetic
 * over thresholds and a remembered selection. That is also why it was the first engine-layer
 * family bound -- the probe in {@code build-probe} found that {@code cna_lod_group_ext_create}
 * succeeds with no device while the debug renderer and the particle system both refuse one.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class LodGroupTests {

    @Test
    void aGroupSortsItsLevelsAndPicksOneForADistance() {
        try (LodGroup group = LodGroup.create()) {
            assertEquals(LodSelectionMode.Distance, group.getSelectionMode());
            // An empty group has no answer, and says so with an index rather than a failure.
            assertEquals(-1, group.selectIndex(10.0f));

            // Added out of order on purpose: the group sorts, so a caller does not have to.
            group.addLevel(100.0f);
            group.addLevel(10.0f);
            group.addLevel(50.0f);
            assertEquals(List.of(10.0f, 50.0f, 100.0f), group.getThresholds());

            assertEquals(0, group.selectIndex(1.0f), "close by, the finest level");
            group.resetHysteresis();
            assertEquals(2, group.selectIndex(90.0f), "far away, the coarsest");

            group.clear();
            assertEquals(List.of(), group.getThresholds());
            assertEquals(-1, group.selectIndex(1.0f));

            assertThrows(IllegalArgumentException.class, () -> group.addLevel(0.0f));
            assertThrows(IllegalArgumentException.class, () -> group.addLevel(-1.0f));
            assertThrows(IllegalArgumentException.class, () -> group.addLevel(Float.NaN));
        }
    }

    @Test
    void hysteresisStopsAnObjectOnAThresholdFromFlickering() {
        try (LodGroup group = LodGroup.create()) {
            group.addLevel(10.0f);
            group.addLevel(20.0f);

            group.setHysteresis(0.0f);
            assertEquals(0.0f, group.getHysteresis());
            group.resetHysteresis();
            int atNine = group.selectIndex(9.0f);
            int justOver = group.selectIndex(10.5f);
            assertNotEquals(atNine, justOver,
                    "with no margin, crossing the threshold changes the level immediately");

            // With a margin, the same crossing does not: the object has to move further before
            // the group changes its mind, which is the whole point.
            group.setHysteresis(5.0f);
            assertEquals(5.0f, group.getHysteresis());
            group.resetHysteresis();
            assertEquals(atNine, group.selectIndex(9.0f));
            assertEquals(atNine, group.selectIndex(10.5f),
                    "a margin of five holds the level half a unit past the threshold");

            // And forgetting the last selection makes the next one unconditioned again.
            group.resetHysteresis();
            assertEquals(justOver, group.selectIndex(10.5f));
        }
    }

    @Test
    void screenSpaceSelectionAccountsForTheCameraAndTheViewport() {
        try (LodGroup group = LodGroup.create()) {
            group.setSelectionMode(LodSelectionMode.ScreenSpaceError);
            assertEquals(LodSelectionMode.ScreenSpaceError, group.getSelectionMode());
            group.setScreenSpaceParameters(1.0f, (float) (Math.PI / 4.0), 1080.0f);

            // The projected radius falls as the object recedes, which is the quantity the mode
            // compares against and the reason it is not the same question as distance.
            float near = group.getProjectedRadiusPixels(5.0f);
            float far = group.getProjectedRadiusPixels(50.0f);
            assertTrue(near > far, "an object covers fewer pixels further away: "
                    + near + " then " + far);
            assertTrue(far > 0.0f);

            // A taller viewport puts the same object across more pixels at the same distance --
            // the fact a distance-only scheme cannot express.
            group.setScreenSpaceParameters(1.0f, (float) (Math.PI / 4.0), 2160.0f);
            assertTrue(group.getProjectedRadiusPixels(5.0f) > near,
                    "doubling the viewport height must not leave the projection unchanged");

            assertThrows(IllegalArgumentException.class,
                    () -> group.setScreenSpaceParameters(0.0f, 1.0f, 1080.0f));
            assertThrows(IllegalArgumentException.class,
                    () -> group.setScreenSpaceParameters(1.0f, (float) Math.PI, 1080.0f));
            assertThrows(IllegalArgumentException.class,
                    () -> group.setScreenSpaceParameters(1.0f, 1.0f, 0.0f));
            assertThrows(NullPointerException.class, () -> group.setSelectionMode(null));
        }
    }

    @Test
    void theEngineLayerReportsTheRevisionItWasBuiltWith() {
        // A revision marker, not an ABI promise. It is worth a game putting in a crash log:
        // when it disagrees with the revision the headers declare, a header and a library from
        // different builds have been mixed, which explains a whole class of confusing failures.
        assertTrue(GraphicsExtension.getEngineLayerVersion() > 0,
                "the engine layer reports a revision in every build");
    }

    @Test
    void aClosedGroupRefusesEveryOperation() {
        LodGroup group = LodGroup.create();
        group.close();
        group.close();
        assertThrows(IllegalStateException.class, () -> group.selectIndex(1.0f));
        assertThrows(IllegalStateException.class, group::getThresholds);
    }
}
