package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.BufferUsage;
import Microsoft.Xna.Framework.Graphics.IndexBuffer;
import Microsoft.Xna.Framework.Graphics.IndexElementSize;
import Microsoft.Xna.Framework.Graphics.VertexBuffer;
import Microsoft.Xna.Framework.Graphics.VertexPositionColor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.openeggbert.cna.extensions.content.CnaModelMeshPartHandle;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
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
    void aLevelCanCarryTheMeshPartItDrawsAndTheGroupHandsBackTheCallersOwn() {
        // Needs a device only because a mesh part is made of buffers; the selection itself is
        // still arithmetic. This is the measurement that says the group can hold the mapping,
        // which is what the threshold-only form used to leave to the caller.
        GameProbe.run(probe -> {
            try (VertexBuffer vertices = new VertexBuffer(probe.device(),
                            VertexPositionColor.VertexDeclaration, 3, BufferUsage.None);
                    IndexBuffer indices = new IndexBuffer(probe.device(),
                            IndexElementSize.SixteenBits, 3, BufferUsage.None);
                    CnaModelMeshPartHandle near = CnaModelMeshPartHandle.create(
                            vertices, indices, 3, 1, 0, 0);
                    CnaModelMeshPartHandle far = CnaModelMeshPartHandle.create(
                            vertices, indices, 3, 1, 0, 0);
                    LodGroup group = LodGroup.create()) {
                assertNull(group.select(10.0f), "an empty group draws nothing");

                group.setHysteresis(0.0f);
                group.addLevel(10.0f, near);
                group.addLevel(20.0f, far);
                group.addLevel(30.0f, null);

                assertSame(near, group.select(5.0f),
                        "the group hands back the object that was added, not a second view of it");
                assertSame(far, group.select(15.0f));
                assertNull(group.select(25.0f),
                        "a level added with no part deliberately draws nothing");
                assertEquals(2, group.selectIndex(25.0f),
                        "which selectIndex tells apart from an empty group's -1");

                // The two selections are one selection: they share the remembered level that
                // hysteresis damps, so they cannot disagree about which level applies.
                assertEquals(0, group.selectIndex(5.0f));
                assertSame(near, group.select(5.0f));

                assertEquals(2, group.retainedPartCount(),
                        "the level with no part holds nothing");

                group.clear();
                assertNull(group.select(5.0f));
                assertEquals(-1, group.selectIndex(5.0f));
                // Clearing must let the caller's parts go. Nothing in a selection can show this
                // -- a cleared group answers "no part" whether or not it kept them -- so it is
                // asserted directly, which is what makes forgetting it a failing test.
                assertEquals(0, group.retainedPartCount(),
                        "a cleared group holds none of the caller's parts");
            }
        });
    }

    @Test
    void aPartAddedToAGroupStaysTheCallersToClose() {
        GameProbe.run(probe -> {
            try (VertexBuffer vertices = new VertexBuffer(probe.device(),
                            VertexPositionColor.VertexDeclaration, 3, BufferUsage.None);
                    IndexBuffer indices = new IndexBuffer(probe.device(),
                            IndexElementSize.SixteenBits, 3, BufferUsage.None);
                    CnaModelMeshPartHandle part = CnaModelMeshPartHandle.create(
                            vertices, indices, 3, 1, 0, 0)) {
                LodGroup group = LodGroup.create();
                group.addLevel(10.0f, part);
                assertSame(part, group.select(5.0f));
                // Closing the group borrowed the part and does not close it: the part answers
                // afterwards, which a released handle would not.
                group.close();
                assertEquals(0, group.retainedPartCount(),
                        "a closed group lets the caller's parts go too");
                assertEquals(Microsoft.Xna.Framework.Graphics.PrimitiveType.TriangleList,
                        part.getPrimitiveType());
            }
        });
    }

    @Test
    void aThresholdIsStillRefusedWhenALevelCarriesAPart() {
        GameProbe.run(probe -> {
            try (VertexBuffer vertices = new VertexBuffer(probe.device(),
                            VertexPositionColor.VertexDeclaration, 3, BufferUsage.None);
                    IndexBuffer indices = new IndexBuffer(probe.device(),
                            IndexElementSize.SixteenBits, 3, BufferUsage.None);
                    CnaModelMeshPartHandle part = CnaModelMeshPartHandle.create(
                            vertices, indices, 3, 1, 0, 0);
                    LodGroup group = LodGroup.create()) {
                assertThrows(IllegalArgumentException.class, () -> group.addLevel(0.0f, part));
                assertThrows(IllegalArgumentException.class,
                        () -> group.addLevel(Float.NaN, part));
                assertEquals(List.of(), group.getThresholds(),
                        "a refused level was never added");
            }
        });
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
