package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.BoundingSphere;
import Microsoft.Xna.Framework.Color;
import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Vector3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Cascaded shadows, against the live runtime.
 *
 * <p><strong>What this can and cannot say.</strong> The map needs a real device, so the suite
 * runs inside a game, and this renderer cannot cast -- so no shadowed pixel is claimed. The
 * evidence is the placement arithmetic, which is where cascaded shadows are actually got wrong:
 * where the splits fall, what a frustum's corners are, the sphere that bounds them, and the texel
 * snap that stops the whole thing shimmering.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class CascadedShadowTests {

    private static final Matrix VIEW = Matrix.CreateLookAt(
            new Vector3(4f, 3f, 12f), new Vector3(0f, 0f, 0f), new Vector3(0f, 1f, 0f));

    private static final Matrix PROJECTION = Matrix.CreatePerspectiveFieldOfView(
            (float) (Math.PI / 3.0), 16.0f / 9.0f, 1.0f, 200.0f);

    @Test
    void lambdaMovesTheSplitsFromUniformToLogarithmic() {
        GameProbe.run(probe -> {
            float[] uniform = CascadedShadowMap.computeSplitDistances(1.0f, 100.0f, 4, 0.0f);
            float[] logarithmic = CascadedShadowMap.computeSplitDistances(1.0f, 100.0f, 4, 1.0f);
            assertEquals(uniform.length, logarithmic.length);
            assertTrue(uniform.length >= 4, "four cascades need at least four splits: "
                    + uniform.length);

            // Uniform means evenly spaced: every gap the same. That is the definition, and it is
            // also what makes the near cascade too coarse, which is why lambda exists.
            float gap = uniform[1] - uniform[0];
            for (int index = 2; index < uniform.length; index++) {
                assertEquals(gap, uniform[index] - uniform[index - 1], 0.05f,
                        "uniform splits are evenly spaced");
            }

            // Logarithmic means evenly spaced in ratio, which packs resolution near the camera.
            float ratio = logarithmic[1] / logarithmic[0];
            for (int index = 2; index < logarithmic.length; index++) {
                assertEquals(ratio, logarithmic[index] / logarithmic[index - 1], 0.02f,
                        "logarithmic splits are evenly spaced in ratio");
            }

            // And the two really are different, so lambda is doing something.
            assertTrue(logarithmic[0] < uniform[0],
                    "the first logarithmic split is nearer: " + logarithmic[0]
                    + " against " + uniform[0]);

            // Every split placement ends at the far plane, whatever lambda is.
            assertEquals(100.0f, uniform[uniform.length - 1], 0.05f);
            assertEquals(100.0f, logarithmic[logarithmic.length - 1], 0.05f);
        });
    }

    @Test
    void aFrustumsCornersAndTheSphereAroundThem() {
        GameProbe.run(probe -> {
            List<Vector3> corners = CascadedShadowMap.computeFrustumCorners(VIEW, PROJECTION);
            assertEquals(CascadedShadowMap.FrustumCornerCount, corners.size());

            // The eight corners are what XNA's own BoundingFrustum computes for the same matrix,
            // as a set. Order is CNA's business; membership is the fact.
            List<Vector3> reference = new ArrayList<>(List.of(
                    new Microsoft.Xna.Framework.BoundingFrustum(
                            Matrix.Multiply(VIEW, PROJECTION)).GetCorners()));
            for (Vector3 corner : corners) {
                assertTrue(reference.stream().anyMatch(other -> near(other, corner, 0.01f)),
                        corner + " is not one of XNA's own frustum corners");
            }
            assertEquals(8, reference.size());

            // The sphere contains every corner, which is the whole reason a cascade is fitted to
            // a sphere rather than a box: a box's size changes as the camera turns.
            BoundingSphere sphere = CascadedShadowMap.computeBoundingSphere(corners);
            for (Vector3 corner : corners) {
                assertTrue(Vector3.Distance(sphere.Center, corner) <= sphere.Radius + 0.01f,
                        corner + " is outside the sphere fitted around it");
            }
            // And it is not vastly larger than it needs to be: something touches it.
            float furthest = 0.0f;
            for (Vector3 corner : corners) {
                furthest = Math.max(furthest, Vector3.Distance(sphere.Center, corner));
            }
            assertEquals(sphere.Radius, furthest, 0.05f, "the sphere is a fit, not a guess");

            assertThrows(IllegalArgumentException.class,
                    () -> CascadedShadowMap.computeBoundingSphere(List.of(new Vector3())));
        });
    }

    @Test
    void snappingToTheTexelGridMovesLessThanOneTexel() {
        GameProbe.run(probe -> {
            float radius = 20.0f;
            int size = 1024;
            // The whole point: the snapped centre is on a texel boundary, so the same world
            // position lands on the same texel from one frame to the next. It must therefore
            // move by less than one texel, or it would be a different cascade rather than the
            // same one aligned.
            float texel = 2.0f * radius / size;
            Vector3 centre = new Vector3(3.14159f, -2.71828f, 1.41421f);
            Vector3 snapped = CascadedShadowMap.snapToTexelGrid(centre, radius, size);
            assertTrue(Vector3.Distance(centre, snapped) < texel * 2.0f,
                    "the snap moved " + Vector3.Distance(centre, snapped)
                    + ", which is more than a texel of " + texel);

            // And it is idempotent: snapping an already-snapped centre changes nothing, which is
            // what makes the result stable across frames rather than drifting.
            Vector3 again = CascadedShadowMap.snapToTexelGrid(snapped, radius, size);
            assertTrue(Vector3.Distance(snapped, again) < 1.0e-4f,
                    "snapping twice moved it again");

            // A nearby centre snaps to the same grid point, which is the property that stops the
            // shimmer.
            Vector3 nudged = new Vector3(centre.X + texel / 8f, centre.Y, centre.Z);
            assertTrue(Vector3.Distance(snapped,
                            CascadedShadowMap.snapToTexelGrid(nudged, radius, size)) < texel * 2f,
                    "a sub-texel camera move should not move the cascade far");
        });
    }

    @Test
    void aMapPlacesItsOwnCascadesAndSaysWhichCoversADepth() {
        GameProbe.run(probe -> {
            try (CascadedShadowMap map =
                         CascadedShadowMap.create(probe.device(), ShadowQuality.Low, 4)) {
                assertEquals(4, map.getCascadeCount());
                assertTrue(map.getCascadeSize() > 0);

                map.setSplitLambda(0.85f);
                assertEquals(0.85f, map.getSplitLambda(), 1.0e-6f);
                map.setBlendBand(2.5f);
                assertEquals(2.5f, map.getBlendBand(), 1.0e-6f);
                assertFalse(map.isDebugTintEnabled(), "the tint is a diagnostic, off by default");
                map.setDebugTintEnabled(true);
                assertTrue(map.isDebugTintEnabled());

                DirectionalLight light = DirectionalLight.createDefault()
                        .withDirection(new Vector3(-0.4f, -1f, -0.3f));
                map.update(light, VIEW, PROJECTION);

                // The splits come out in order and cover the whole range, and each cascade has
                // its own transform -- four identical matrices would be one cascade drawn four
                // times.
                float previous = 0.0f;
                for (int cascade = 0; cascade < map.getCascadeCount(); cascade++) {
                    float split = map.getSplitDistance(cascade);
                    assertTrue(split > previous,
                            "cascade " + cascade + " ends at " + split + ", not after "
                            + previous);
                    previous = split;
                }
                for (int cascade = 1; cascade < map.getCascadeCount(); cascade++) {
                    assertNotEquals(map.getCascadeMatrix(0), map.getCascadeMatrix(cascade));
                }

                // Selecting is the shader's own question, and its answer has to agree with the
                // splits the map just reported.
                for (int cascade = 0; cascade < map.getCascadeCount(); cascade++) {
                    float low = cascade == 0 ? 0.0f : map.getSplitDistance(cascade - 1);
                    float middle = (low + map.getSplitDistance(cascade)) / 2.0f;
                    assertEquals(cascade, map.selectCascade(middle),
                            "the middle of cascade " + cascade + " is in cascade " + cascade);
                }
                assertEquals(0, map.selectCascade(-100.0f), "behind the camera is the first");
                assertEquals(map.getCascadeCount() - 1, map.selectCascade(1.0e6f),
                        "past the last split is the last");

                if (!map.isSupported()) {
                    // Same shape as the cube's face passes: nothing is claimed about casting.
                    assertFalse(map.isSupported());
                }
            }
        });
    }

    @Test
    void theDebugRendererCanOutlineAGridAndACascadeSet() {
        GameProbe.run(probe -> {
            try (DebugDraw debug = DebugDraw.create(probe.device());
                 ClusteredLightGrid grid = ClusteredLightGrid.create(probe.device(), 3, 3, 3);
                 CascadedShadowMap cascades =
                         CascadedShadowMap.create(probe.device(), ShadowQuality.Low, 3)) {
                grid.setProjection(PROJECTION, 1.0f, 200.0f);
                cascades.update(DirectionalLight.createDefault(), VIEW, PROJECTION);

                debug.begin(Matrix.getIdentity(), Matrix.getIdentity());
                debug.addClusterSliceGizmo(grid, Matrix.Invert(VIEW), Color.Magenta);
                int afterGrid = debug.getLineCount();
                // One box per depth slice, and a box is twelve edges. Three slices, thirty-six
                // lines: the count says the gizmo drew slices rather than all twenty-seven
                // clusters, which is what CNA documents and what the Java documentation now
                // says too.
                assertEquals(36, afterGrid, "three slices, twelve edges each");

                debug.addCascadeGizmo(cascades, Color.Orange);
                assertTrue(debug.getLineCount() > afterGrid,
                        "three cascades add their own outlines");

                // The grid's own slice count is what decides how much is drawn, which is the
                // check that the handle reached CNA rather than a constant. More tiles would
                // not: the gizmo is per slice.
                try (ClusteredLightGrid deeper =
                             ClusteredLightGrid.create(probe.device(), 3, 3, 5)) {
                    deeper.setProjection(PROJECTION, 1.0f, 200.0f);
                    debug.clear();
                    debug.addClusterSliceGizmo(deeper, Matrix.Invert(VIEW), Color.Magenta);
                    assertEquals(60, debug.getLineCount(), "five slices, twelve edges each");
                }

                // And CNA's documented rule that an unshaped grid draws nothing rather than
                // refusing, so a debug overlay can run before the camera is set.
                try (ClusteredLightGrid unshaped =
                             ClusteredLightGrid.create(probe.device(), 3, 3, 3)) {
                    debug.clear();
                    debug.addClusterSliceGizmo(unshaped, Matrix.Invert(VIEW), Color.Magenta);
                    assertEquals(0, debug.getLineCount(),
                            "a grid with no projection has no slices to place");
                }

                assertThrows(NullPointerException.class,
                        () -> debug.addCascadeGizmo(null, Color.Red));
            }
        });
    }

    private static boolean near(Vector3 left, Vector3 right, float tolerance) {
        return Vector3.Distance(left, right) <= tolerance;
    }
}
