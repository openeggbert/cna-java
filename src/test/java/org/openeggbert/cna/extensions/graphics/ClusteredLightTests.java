package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.BoundingSphere;
import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Vector3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Clustered forward lighting, against the live runtime.
 *
 * <p><strong>What this can and cannot say.</strong> The four objects are parented to a graphics
 * device, so the suite runs inside a game -- VERIFIED_HEADLESS_GAME. Nothing here claims a lit
 * pixel. What it does check is the arithmetic the whole technique rests on, which is fully
 * readable: the grid's cluster numbering and its logarithmic slice spacing, the set's derived
 * bounds, and the assignment's compressed-row structure. Those are the parts a game's own shader
 * and its own budgeting depend on, and getting any of them wrong produces lights in the wrong
 * place rather than a crash.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class ClusteredLightTests {

    private static final Matrix PROJECTION = Matrix.CreatePerspectiveFieldOfView(
            (float) (Math.PI / 2.0), 1.0f, 1.0f, 100.0f);

    private static final float NEAR = 1.0f;
    private static final float FAR = 100.0f;

    @Test
    void theGridNumbersItsClustersOnceEach() {
        GameProbe.run(probe -> {
            try (ClusteredLightGrid grid = ClusteredLightGrid.create(probe.device(), 4, 3, 5)) {
                assertEquals(4, grid.getTilesX());
                assertEquals(3, grid.getTilesY());
                assertEquals(5, grid.getSliceCount());
                assertEquals(4 * 3 * 5, grid.getClusterCount(),
                        "a grid is its tiles times its slices");

                // Every coordinate maps to a distinct index inside the range, which is what
                // makes an assignment's flat arrays addressable at all.
                Set<Integer> seen = new HashSet<>();
                for (int x = 0; x < grid.getTilesX(); x++) {
                    for (int y = 0; y < grid.getTilesY(); y++) {
                        for (int slice = 0; slice < grid.getSliceCount(); slice++) {
                            int index = grid.getClusterIndex(x, y, slice);
                            assertTrue(index >= 0 && index < grid.getClusterCount(),
                                    "index " + index + " is outside the grid");
                            assertTrue(seen.add(index),
                                    "two coordinates share index " + index);
                        }
                    }
                }
                assertEquals(grid.getClusterCount(), seen.size(),
                        "every cluster is named exactly once");

                assertThrows(IllegalArgumentException.class,
                        () -> grid.getClusterIndex(4, 0, 0));
                assertThrows(IllegalArgumentException.class,
                        () -> grid.getClusterIndex(0, 0, 5));
                assertThrows(IllegalArgumentException.class,
                        () -> ClusteredLightGrid.create(probe.device(), 0, 1, 1));
                assertThrows(IllegalArgumentException.class,
                        () -> ClusteredLightGrid.create(probe.device(),
                                ClusteredLightGrid.MaxTilesPerAxis + 1, 1, 1));
            }
        });
    }

    @Test
    void aGridHasNoShapeUntilItHasAProjection() {
        GameProbe.run(probe -> {
            try (ClusteredLightGrid grid = ClusteredLightGrid.create(probe.device(), 2, 2, 4)) {
                assertFalse(grid.hasProjection());
                assertEquals(0.0f, grid.getSliceDistance(0),
                        "an unshaped grid reports zero rather than guessing");
                assertThrows(IllegalStateException.class, () -> grid.getClusterBounds(0, 0, 0));

                grid.setProjection(PROJECTION, NEAR, FAR);
                assertTrue(grid.hasProjection());
                assertEquals(NEAR, grid.getNearPlane());
                assertEquals(FAR, grid.getFarPlane());

                // The inverse really is the projection's inverse, which is the one thing a
                // caller cannot check for itself without redoing the work.
                Matrix round = Matrix.Multiply(PROJECTION, grid.getInverseProjection());
                assertMatrixEquals(Matrix.getIdentity(), round, 1.0e-4f);

                // The planes have to be able to space the slices: the spacing is a ratio, so a
                // zero near plane has no logarithm and an inverted pair has no grid.
                assertThrows(IllegalArgumentException.class,
                        () -> grid.setProjection(PROJECTION, 0.0f, FAR));
                assertThrows(IllegalArgumentException.class,
                        () -> grid.setProjection(PROJECTION, FAR, NEAR));
            }
        });
    }

    @Test
    void theSlicesAreSpacedLogarithmicallyBetweenThePlanes() {
        GameProbe.run(probe -> {
            try (ClusteredLightGrid grid = ClusteredLightGrid.create(probe.device(), 2, 2, 8)) {
                grid.setProjection(PROJECTION, NEAR, FAR);

                // One more boundary than slice: the count itself names the far plane.
                assertEquals(NEAR, grid.getSliceDistance(0), 1.0e-3f);
                assertEquals(FAR, grid.getSliceDistance(8), 1.0e-2f);
                assertThrows(IllegalArgumentException.class, () -> grid.getSliceDistance(9));

                float previous = grid.getSliceDistance(0);
                float previousRatio = 0.0f;
                for (int slice = 1; slice <= 8; slice++) {
                    float distance = grid.getSliceDistance(slice);
                    assertTrue(distance > previous,
                            "slice " + slice + " begins at " + distance
                            + ", not after " + previous);
                    // Logarithmic rather than linear is the whole point: each slice covers the
                    // same *ratio* of depth, so a light near the camera does not land in every
                    // slice. Equal ratios is a much stronger statement than "increasing".
                    float ratio = distance / previous;
                    if (slice > 1) {
                        assertEquals(previousRatio, ratio, 1.0e-3f,
                                "slice " + slice + " is not the same ratio as the last");
                    }
                    previousRatio = ratio;
                    previous = distance;
                }

                // Placing a distance is clamped into the grid rather than refused, which is what
                // a renderer wants when a light straddles the frustum edge.
                assertEquals(0, grid.getSliceForViewDistance(-50.0f),
                        "a point behind the camera belongs to the first slice");
                assertEquals(7, grid.getSliceForViewDistance(FAR * 10.0f),
                        "and one past the far plane to the last");

                // And inside the grid it agrees with the boundaries it reported.
                for (int slice = 0; slice < 8; slice++) {
                    float low = grid.getSliceDistance(slice);
                    float high = grid.getSliceDistance(slice + 1);
                    float middle = (float) Math.sqrt((double) low * high);
                    assertEquals(slice, grid.getSliceForViewDistance(middle),
                            "the middle of slice " + slice + " is in slice " + slice);
                }
            }
        });
    }

    @Test
    void aLightsBoundsAreCnasOwnDerivationFromItsPositionAndRange() {
        GameProbe.run(probe -> {
            try (ClusteredLightSet set = ClusteredLightSet.create(probe.device())) {
                assertTrue(set.isEmpty());
                assertEquals(0, set.getCount());

                ClusteredLight light = ClusteredLight.createDefault()
                        .withPosition(new Vector3(3f, -4f, 5f))
                        .withRange(7.0f)
                        .withIntensity(2.5f)
                        .withColor(new Vector3(0.25f, 0.5f, 0.75f));
                assertTrue(light.isUsable());
                assertEquals(0, set.add(light));
                assertEquals(1, set.getCount());
                assertFalse(set.isEmpty());

                // Every field survives the round trip through CNA's own storage, at its own
                // offset -- and this is the check that the leaf order is right.
                assertEquals(light, set.getAt(0));

                // The bounds are derived rather than supplied, and the derivation is the light's
                // reach: an assignment sorting these is sorting where the light gets to.
                BoundingSphere bounds = set.getBoundsAt(0);
                assertEquals(new Vector3(3f, -4f, 5f), bounds.Center);
                assertEquals(7.0f, bounds.Radius, 1.0e-4f);
                assertEquals(List.of(bounds), set.getBounds());

                // A range of zero reaches nothing, and CNA says so rather than taking it.
                assertFalse(light.withRange(0.0f).isUsable());
                assertFalse(light.withIntensity(-1.0f).isUsable());
            }
        });
    }

    @Test
    void theSetHoldsItsLightsInOrderAndClosesTheGapWhenOneGoes() {
        GameProbe.run(probe -> {
            try (ClusteredLightSet set = ClusteredLightSet.create(probe.device())) {
                ClusteredLight base = ClusteredLight.createDefault().withRange(2.0f);
                for (int index = 0; index < 4; index++) {
                    assertEquals(index,
                            set.add(base.withPosition(new Vector3(index, 0f, 0f))),
                            "add returns the index it went in at");
                }
                assertEquals(4, set.getCount());
                assertEquals(4, set.getLights().size());
                for (int index = 0; index < 4; index++) {
                    assertEquals((float) index, set.getLights().get(index).getPosition().X);
                }

                // Removing shifts everything after it down, which is exactly why an index taken
                // before a removal names a different light after it.
                set.removeAt(1);
                assertEquals(3, set.getCount());
                assertEquals(0.0f, set.getAt(0).getPosition().X);
                assertEquals(2.0f, set.getAt(1).getPosition().X, "index 2 moved down to 1");
                assertEquals(3.0f, set.getAt(2).getPosition().X);

                set.replaceAt(1, base.withPosition(new Vector3(99f, 0f, 0f)));
                assertEquals(99.0f, set.getAt(1).getPosition().X);
                assertEquals(3, set.getCount(), "replacing does not add");

                // A point light and a spot light become clustered lights of the right kind, so a
                // game can build a scene from the simpler types it already has.
                set.clear();
                assertTrue(set.isEmpty());
                assertEquals(0, set.add(PointLight.createDefault()
                        .withPosition(new Vector3(1f, 2f, 3f)).withRange(5.0f)));
                assertEquals(ClusteredLightType.Point, set.getAt(0).getType());
                assertEquals(new Vector3(1f, 2f, 3f), set.getAt(0).getPosition());
                assertEquals(5.0f, set.getAt(0).getRange(), 1.0e-4f);

                assertEquals(1, set.add(SpotLight.createDefault()
                        .withPosition(new Vector3(4f, 5f, 6f)).withRange(8.0f)
                        .withCone(0.25f, 0.75f)));
                assertEquals(ClusteredLightType.Spot, set.getAt(1).getType());
                assertEquals(0.25f, set.getAt(1).getInnerAngle(), 1.0e-4f);
                assertEquals(0.75f, set.getAt(1).getOuterAngle(), 1.0e-4f);

                assertThrows(IllegalArgumentException.class, () -> set.getAt(9));
                assertThrows(IllegalArgumentException.class, () -> set.removeAt(9));
                assertThrows(NullPointerException.class, () -> set.add((ClusteredLight) null));
            }
        });
    }

    @Test
    void anAssignmentIsAConsistentCompressedRowOfLightsPerCluster() {
        GameProbe.run(probe -> {
            try (ClusteredLightGrid grid = ClusteredLightGrid.create(probe.device(), 4, 4, 6);
                 ClusteredLightAssignment assignment =
                         ClusteredLightAssignment.create(probe.device())) {
                grid.setProjection(PROJECTION, NEAR, FAR);

                // Three lights: one in front of the camera, one behind it, one enormous.
                List<BoundingSphere> bounds = List.of(
                        new BoundingSphere(new Vector3(0f, 0f, -10f), 2.0f),
                        new BoundingSphere(new Vector3(0f, 0f, 40f), 2.0f),
                        new BoundingSphere(new Vector3(0f, 0f, -30f), 60.0f));
                assignment.assign(grid, Matrix.getIdentity(), bounds);

                assertEquals(3, assignment.getLightCount());
                assertEquals(grid.getClusterCount(), assignment.getClusterCount());

                int[] offsets = assignment.getOffsets();
                int[] indices = assignment.getIndices();
                assertEquals(grid.getClusterCount() + 1, offsets.length,
                        "there is one more offset than cluster");
                assertEquals(0, offsets[0], "the first run begins at zero");
                assertEquals(indices.length, offsets[offsets.length - 1],
                        "the last offset is the end of the index array");
                assertEquals(indices.length, assignment.getTotalReferenceCount());

                int longest = 0;
                for (int cluster = 0; cluster < grid.getClusterCount(); cluster++) {
                    assertTrue(offsets[cluster + 1] >= offsets[cluster],
                            "offsets never go backwards");
                    int[] run = new int[offsets[cluster + 1] - offsets[cluster]];
                    System.arraycopy(indices, offsets[cluster], run, 0, run.length);
                    // Reading a cluster directly and reading it out of the flat arrays are two
                    // routes to the same answer, and a shader uses the second.
                    assertArrayEquals(run, assignment.getLightsInCluster(cluster),
                            "cluster " + cluster + " disagrees with its own run");
                    for (int light : run) {
                        assertTrue(light >= 0 && light < 3,
                                "cluster " + cluster + " names light " + light);
                    }
                    longest = Math.max(longest, run.length);
                }
                assertEquals(longest, assignment.getMaxLightsPerCluster(),
                        "the maximum is the longest run there actually is");

                // The light behind the camera reaches nothing, and the enormous one reaches
                // more clusters than the small one. That is what the sorting is for, and
                // neither statement can hold by accident.
                int small = countClustersHolding(assignment, grid.getClusterCount(), 0);
                int behind = countClustersHolding(assignment, grid.getClusterCount(), 1);
                int huge = countClustersHolding(assignment, grid.getClusterCount(), 2);
                assertEquals(0, behind, "a light behind the camera is in no cluster");
                assertTrue(small > 0, "a light in front of the camera is in some");
                assertTrue(huge > small,
                        "a sixty-unit light reaches more than a two-unit one: "
                        + huge + " against " + small);

                assignment.clear();
                assertEquals(0, assignment.getTotalReferenceCount());
                assertEquals(0, assignment.getIndices().length);
            }
        });
    }

    @Test
    void anAssignmentCanBeGivenOneComputedElsewhere() {
        GameProbe.run(probe -> {
            try (ClusteredLightAssignment assignment =
                         ClusteredLightAssignment.create(probe.device())) {
                // Two clusters: the first holds lights 1 and 0, the second holds light 1.
                int[] offsets = {0, 2, 3};
                int[] indices = {1, 0, 1};
                assignment.adopt(2, offsets, indices);

                assertEquals(2, assignment.getLightCount());
                assertEquals(2, assignment.getClusterCount());
                assertArrayEquals(indices, assignment.getIndices());
                assertArrayEquals(offsets, assignment.getOffsets());
                assertArrayEquals(new int[] {1, 0}, assignment.getLightsInCluster(0));
                assertArrayEquals(new int[] {1}, assignment.getLightsInCluster(1));
                assertEquals(3, assignment.getTotalReferenceCount());
                assertEquals(2, assignment.getMaxLightsPerCluster());

                // The adopted arrays are copied, so editing the caller's does not edit the
                // assignment -- which is the difference between a value and a view.
                offsets[1] = 999;
                indices[0] = 999;
                assertArrayEquals(new int[] {0, 2, 3}, assignment.getOffsets());
                assertArrayEquals(new int[] {1, 0, 1}, assignment.getIndices());

                // And CNA validates the shape rather than trusting it.
                assertThrows(IllegalArgumentException.class,
                        () -> assignment.adopt(2, new int[] {1, 2, 3}, new int[] {0, 0, 0}));
                assertThrows(IllegalArgumentException.class,
                        () -> assignment.adopt(2, new int[] {0, 2, 1}, new int[] {0, 0}));
                assertThrows(IllegalArgumentException.class,
                        () -> assignment.adopt(1, new int[] {0, 1}, new int[] {5}));
            }
        });
    }

    @Test
    void theBufferCarriesWhatTheAssignmentSaidAndTheGlslToReadIt() {
        GameProbe.run(probe -> {
            try (ClusteredLightSet set = ClusteredLightSet.create(probe.device());
                 ClusteredLightGrid grid = ClusteredLightGrid.create(probe.device(), 4, 4, 4);
                 ClusteredLightAssignment assignment =
                         ClusteredLightAssignment.create(probe.device());
                 ClusteredLightBuffer buffer = ClusteredLightBuffer.create(probe.device())) {
                grid.setProjection(PROJECTION, NEAR, FAR);
                set.add(ClusteredLight.createDefault()
                        .withPosition(new Vector3(0f, 0f, -10f)).withRange(5.0f));
                set.add(ClusteredLight.createDefault()
                        .withPosition(new Vector3(2f, 1f, -20f)).withRange(8.0f));
                assignment.assign(grid, Matrix.getIdentity(), set.getBounds());

                assertFalse(buffer.isUploaded(), "an empty buffer holds no scene");
                buffer.upload(set, grid, assignment);
                assertTrue(buffer.isUploaded());
                assertEquals(set.getCount(), buffer.getLightCount());
                assertEquals(grid.getClusterCount(), buffer.getClusterCount());
                assertEquals(assignment.getTotalReferenceCount(), buffer.getReferenceCount());

                // The GLSL is what a game's own shader includes so its unpacking and CNA's
                // packing cannot disagree. It is CNA's own text, so the only thing worth
                // asserting is that there is some and that it is GLSL.
                String glsl = ClusteredLightBuffer.getLightLookupGlsl();
                assertFalse(glsl.isBlank(), "the lookup source must exist to be included");
                assertTrue(glsl.contains("("), "it is source rather than a name: " + glsl);
            }
        });
    }

    @Test
    void theComputePathSaysWhetherItRanOnTheGpu() {
        GameProbe.run(probe -> {
            try (ClusteredLightGrid grid = ClusteredLightGrid.create(probe.device(), 4, 4, 4);
                 ClusteredLightAssignment assignment =
                         ClusteredLightAssignment.create(probe.device());
                 ClusteredLightCompute compute =
                         ClusteredLightCompute.create(probe.device(), 32)) {
                assertEquals(32, compute.getStride());
                boolean supported = compute.isSupported();
                if (!supported) {
                    assertFalse(compute.getUnsupportedReason().isBlank(),
                            "an unsupported program says why");
                }

                grid.setProjection(PROJECTION, NEAR, FAR);
                List<BoundingSphere> bounds = List.of(
                        new BoundingSphere(new Vector3(0f, 0f, -10f), 4.0f),
                        new BoundingSphere(new Vector3(0f, 0f, -60f), 10.0f));
                compute.assign(grid, Matrix.getIdentity(), bounds, assignment);

                // The work happened either way -- that is the point of the fallback -- and the
                // program says which path it took rather than leaving a game to guess.
                assertEquals(supported, compute.didUseCompute());
                assertEquals(2, assignment.getLightCount());
                assertTrue(assignment.getTotalReferenceCount() > 0,
                        "two lights inside the frustum reach some clusters");

                // The CPU fallback must agree with the CPU sorter, or one of them is wrong.
                try (ClusteredLightAssignment direct =
                             ClusteredLightAssignment.create(probe.device())) {
                    direct.assign(grid, Matrix.getIdentity(), bounds);
                    assertArrayEquals(direct.getOffsets(), assignment.getOffsets(),
                            "the two sorters put the lights in the same clusters");
                    assertArrayEquals(direct.getIndices(), assignment.getIndices());
                }

                assertThrows(IllegalArgumentException.class,
                        () -> ClusteredLightCompute.create(probe.device(), 0));
            }
        });
    }

    @Test
    void aClosedObjectRefusesEveryOperation() {
        GameProbe.run(probe -> {
            ClusteredLightSet set = ClusteredLightSet.create(probe.device());
            ClusteredLightGrid grid = ClusteredLightGrid.create(probe.device(), 2, 2, 2);
            ClusteredLightAssignment assignment =
                    ClusteredLightAssignment.create(probe.device());
            ClusteredLightBuffer buffer = ClusteredLightBuffer.create(probe.device());
            ClusteredLightCompute compute = ClusteredLightCompute.create(probe.device(), 8);
            // Closing twice is a no-op for every one of them, which is the rule the whole
            // package follows and the one a try-with-resources beside an explicit close needs.
            for (int pass = 0; pass < 2; pass++) {
                set.close();
                grid.close();
                assignment.close();
                buffer.close();
                compute.close();
            }
            assertThrows(IllegalStateException.class, set::getCount);
            assertThrows(IllegalStateException.class, grid::getTilesX);
            assertThrows(IllegalStateException.class, assignment::getIndices);
            assertThrows(IllegalStateException.class, buffer::isUploaded);
            assertThrows(IllegalStateException.class, compute::getStride);
            assertThrows(NullPointerException.class, () -> ClusteredLightSet.create(null));
        });
    }

    private static int countClustersHolding(ClusteredLightAssignment assignment, int clusters,
            int light) {
        int held = 0;
        for (int cluster = 0; cluster < clusters; cluster++) {
            for (int index : assignment.getLightsInCluster(cluster)) {
                if (index == light) {
                    held++;
                    break;
                }
            }
        }
        return held;
    }

    private static void assertMatrixEquals(Matrix expected, Matrix actual, float tolerance) {
        float[] left = {
            expected.M11, expected.M12, expected.M13, expected.M14,
            expected.M21, expected.M22, expected.M23, expected.M24,
            expected.M31, expected.M32, expected.M33, expected.M34,
            expected.M41, expected.M42, expected.M43, expected.M44};
        float[] right = {
            actual.M11, actual.M12, actual.M13, actual.M14,
            actual.M21, actual.M22, actual.M23, actual.M24,
            actual.M31, actual.M32, actual.M33, actual.M34,
            actual.M41, actual.M42, actual.M43, actual.M44};
        for (int index = 0; index < left.length; index++) {
            assertEquals(left[index], right[index], tolerance, "element " + index);
        }
    }
}
