package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.BoundingBox;
import Microsoft.Xna.Framework.BoundingFrustum;
import Microsoft.Xna.Framework.BoundingSphere;
import Microsoft.Xna.Framework.Color;
import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Vector3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The debug renderer, against the live runtime.
 *
 * <p><strong>What this can and cannot say.</strong> The renderer needs a real graphics device, so
 * it runs inside a game -- the probe in {@code tools/native-abi/probes} found it refuses an
 * invalid one. Nothing here claims a line was drawn: this qualification has a HEADLESS renderer
 * and no pixels to look at. What it does check is the queue, which is the part a game's own logic
 * depends on and which is fully readable: a box really is twelve edges, the two lists really are
 * separate, and the vertices come back in submission order with the colours they were queued
 * with.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class DebugDrawTests {

    @Test
    void theQueueHoldsTheEdgesEachShapeIsMadeOf() {
        GameProbe.run(probe -> {
            try (DebugDraw debug = DebugDraw.create(probe.device())) {
                debug.begin(Matrix.getIdentity(), Matrix.getIdentity());
                assertEquals(0, debug.getLineCount(), "begin clears both lists");
                assertTrue(debug.isDepthTested(), "begin restores depth testing");

                debug.addLine(new Vector3(0f, 0f, 0f), new Vector3(1f, 2f, 3f), Color.Red);
                assertEquals(1, debug.getLineCount());

                List<DebugVertex> vertices = debug.readVertices(true);
                assertEquals(2, vertices.size(), "a line is two vertices");
                assertEquals(new Vector3(0f, 0f, 0f), vertices.get(0).Position());
                assertEquals(new Vector3(1f, 2f, 3f), vertices.get(1).Position());
                assertEquals(Color.Red, vertices.get(0).Color());

                // A box is twelve edges, which is the fiddly count this exists to get right.
                debug.clear();
                debug.addBox(new BoundingBox(new Vector3(-1f, -1f, -1f), new Vector3(1f, 1f, 1f)),
                        Color.Lime);
                assertEquals(12, debug.getLineCount());
                assertEquals(24, debug.readVertices(true).size());

                // A cross is three segments, one per axis.
                debug.clear();
                debug.addCross(new Vector3(5f, 0f, 0f), 1.0f, Color.White);
                assertEquals(3, debug.getLineCount());

                // A sphere is three rings, so its edge count follows the segment count.
                debug.clear();
                debug.addSphere(new Vector3(0f, 0f, 0f), 2.0f, Color.Blue, 8);
                int coarse = debug.getLineCount();
                debug.clear();
                debug.addSphere(new Vector3(0f, 0f, 0f), 2.0f, Color.Blue, 16);
                assertTrue(debug.getLineCount() > coarse,
                        "more segments must mean more edges: " + coarse
                        + " then " + debug.getLineCount());

                debug.clear();
                debug.addBoundingSphere(new BoundingSphere(new Vector3(0f, 0f, 0f), 2.0f),
                        Color.Blue, 8);
                assertEquals(coarse, debug.getLineCount(),
                        "a bounding sphere is the same three rings");

                // A frustum is twelve edges too, and takes XNA's own type.
                debug.clear();
                debug.addFrustum(new BoundingFrustum(Matrix.CreatePerspectiveFieldOfView(
                        1.0f, 1.0f, 1.0f, 100.0f)), Color.Yellow);
                assertEquals(12, debug.getLineCount());

                assertThrows(NullPointerException.class,
                        () -> debug.addLine(null, new Vector3(), Color.Red));
                assertThrows(NullPointerException.class,
                        () -> debug.addBox(new BoundingBox(), null));
            }
        });
    }

    @Test
    void aLightGizmoIsDrawnWhereTheLightIsAndAsBigAsItReaches() {
        GameProbe.run(probe -> {
            try (DebugDraw debug = DebugDraw.create(probe.device())) {
                debug.begin(Matrix.getIdentity(), Matrix.getIdentity());

                // A point light is drawn as a sphere at its range, so the geometry CNA queues
                // says whether the position and the range arrived at their own offsets in the
                // native structure. Nothing about this can pass if the two are swapped, or if
                // either lands in a neighbouring field.
                PointLight point = PointLight.createDefault()
                        .withPosition(new Vector3(10f, -4f, 7f))
                        .withRange(3.0f);
                debug.addPointLightGizmo(point, Color.Yellow);
                Bounds sphere = Bounds.of(debug.readVertices(true));
                assertTrue(debug.getLineCount() > 0, "a gizmo is some lines");
                assertVectorEquals(new Vector3(10f, -4f, 7f), sphere.centre(), 0.05f,
                        "the sphere is centred on the light");
                assertEquals(3.0f, sphere.radius(), 0.05f,
                        "and reaches exactly as far as the light does");

                // Doubling the range doubles the sphere, which is the check that the value is
                // read rather than a constant.
                debug.clear();
                debug.addPointLightGizmo(point.withRange(6.0f), Color.Yellow);
                assertEquals(6.0f, Bounds.of(debug.readVertices(true)).radius(), 0.05f);

                // A directional light has no position, so the arrow is drawn where the caller
                // says, as long as the caller says, pointing where the light travels.
                debug.clear();
                DirectionalLight directional = DirectionalLight.createDefault()
                        .withDirection(new Vector3(1f, 0f, 0f));
                debug.addDirectionalLightGizmo(directional, new Vector3(0f, 20f, 0f), 5.0f,
                        Color.White);
                Bounds arrow = Bounds.of(debug.readVertices(true));
                assertTrue(arrow.minimum().Y > 15f && arrow.maximum().Y < 25f,
                        "the arrow is drawn around the point it was given, not the origin: "
                        + arrow.minimum() + " to " + arrow.maximum());
                assertTrue(arrow.maximum().X - arrow.minimum().X >= 5.0f - 0.05f,
                        "and it is as long along the light's direction as it was asked to be: "
                        + (arrow.maximum().X - arrow.minimum().X));

                // A spot light is drawn as a cone, so its apex is the light and its far end is
                // one range away along the direction it points.
                debug.clear();
                SpotLight spot = SpotLight.createDefault()
                        .withPosition(new Vector3(0f, 0f, 0f))
                        .withDirection(new Vector3(0f, 0f, -1f))
                        .withRange(8.0f)
                        .withCone(0.1f, 0.5f);
                debug.addSpotLightGizmo(spot, Color.Cyan, 12);
                Bounds cone = Bounds.of(debug.readVertices(true));
                assertEquals(0.0f, cone.maximum().Z, 0.05f, "the apex is at the light");
                assertEquals(-8.0f, cone.minimum().Z, 0.05f,
                        "and the cone closes one range away along the direction it points");

                // More segments means more edges, the same way a sphere's do -- which is how
                // the segment count is shown to reach CNA rather than being ignored.
                int coarse = debug.getLineCount();
                debug.clear();
                debug.addSpotLightGizmo(spot, Color.Cyan, 24);
                assertTrue(debug.getLineCount() > coarse,
                        "24 segments must draw more than 12: " + coarse + " then "
                        + debug.getLineCount());

                assertThrows(NullPointerException.class,
                        () -> debug.addPointLightGizmo(null, Color.Red));
                assertThrows(NullPointerException.class,
                        () -> debug.addSpotLightGizmo(spot, null, 8));
            }
        });
    }

    @Test
    void theTwoListsAreSeparate() {
        GameProbe.run(probe -> {
            try (DebugDraw debug = DebugDraw.create(probe.device())) {
                debug.begin(Matrix.getIdentity(), Matrix.getIdentity());

                // A collision volume you want hidden behind geometry, and a marker you must not
                // lose behind a wall: two different lists, and the count spans both.
                debug.addLine(new Vector3(0f, 0f, 0f), new Vector3(1f, 0f, 0f), Color.Red);
                debug.setDepthTested(false);
                assertFalse(debug.isDepthTested());
                debug.addLine(new Vector3(0f, 0f, 0f), new Vector3(0f, 1f, 0f), Color.Lime);
                debug.addLine(new Vector3(0f, 0f, 0f), new Vector3(0f, 0f, 1f), Color.Blue);

                assertEquals(3, debug.getLineCount(), "the count spans both lists");
                assertEquals(2, debug.readVertices(true).size(), "one depth-tested line");
                assertEquals(4, debug.readVertices(false).size(), "two overlay lines");
                assertEquals(Color.Lime, debug.readVertices(false).get(0).Color());

                debug.begin(Matrix.getIdentity(), Matrix.getIdentity());
                assertEquals(0, debug.getLineCount(), "begin clears both");
                assertTrue(debug.isDepthTested(), "and restores depth testing");
            }
        });
    }

    @Test
    void aClosedRendererRefusesEveryOperation() {
        GameProbe.run(probe -> {
            DebugDraw debug = DebugDraw.create(probe.device());
            debug.close();
            debug.close();
            assertThrows(IllegalStateException.class, debug::getLineCount);
            assertThrows(NullPointerException.class, () -> DebugDraw.create(null));
        });
    }


    /** The axis-aligned extent of a queued shape, which is what a gizmo's numbers show up in. */
    private record Bounds(Vector3 minimum, Vector3 maximum) {

        static Bounds of(List<DebugVertex> vertices) {
            assertTrue(!vertices.isEmpty(), "a gizmo must queue something");
            Vector3 low = new Vector3(Float.MAX_VALUE);
            Vector3 high = new Vector3(-Float.MAX_VALUE);
            for (DebugVertex vertex : vertices) {
                Vector3 position = vertex.Position();
                low.X = Math.min(low.X, position.X);
                low.Y = Math.min(low.Y, position.Y);
                low.Z = Math.min(low.Z, position.Z);
                high.X = Math.max(high.X, position.X);
                high.Y = Math.max(high.Y, position.Y);
                high.Z = Math.max(high.Z, position.Z);
            }
            return new Bounds(low, high);
        }

        Vector3 centre() {
            return new Vector3((minimum.X + maximum.X) / 2f, (minimum.Y + maximum.Y) / 2f,
                    (minimum.Z + maximum.Z) / 2f);
        }

        float radius() {
            return Math.max(Math.max(maximum.X - minimum.X, maximum.Y - minimum.Y),
                    maximum.Z - minimum.Z) / 2f;
        }
    }

    private static void assertVectorEquals(Vector3 expected, Vector3 actual, float tolerance,
            String message) {
        assertEquals(expected.X, actual.X, tolerance, message + " (x)");
        assertEquals(expected.Y, actual.Y, tolerance, message + " (y)");
        assertEquals(expected.Z, actual.Z, tolerance, message + " (z)");
    }
}
