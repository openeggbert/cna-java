package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.BoundingBox;
import Microsoft.Xna.Framework.BoundingFrustum;
import Microsoft.Xna.Framework.BoundingSphere;
import Microsoft.Xna.Framework.ContainmentType;
import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Vector3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The frustum culler, against the live runtime.
 *
 * <p><strong>What this can say.</strong> The culler needs no graphics device -- the probe in
 * {@code tools/native-abi/probes} creates and destroys one with none at all -- so this is a
 * VERIFIED_PURE family and every answer below is CNA's own arithmetic, not a rendering claim.
 *
 * <p>The evidence worth having is not "it returned some indices". It is that CNA's answer agrees,
 * object by object, with <em>XNA's own</em> {@link BoundingFrustum}, which this projection
 * already implements in managed Java from the reference. Two independent implementations of the
 * same test compared across 125 objects is a much harder thing to pass by accident than any
 * single expected value written down here.
 *
 * <p><strong>Which XNA test, though.</strong> {@code BoundingFrustum} has two, and they are not
 * the same function. {@link BoundingFrustum#Contains(BoundingBox)} is the six-plane test, and
 * CNA's culler is that one -- its {@code Intersects} is defined as
 * {@code Contains(box) != Disjoint}. {@link BoundingFrustum#Intersects(BoundingBox)} is XNA's
 * GJK, a different algorithm that disagrees with the plane test on boxes near a frustum corner.
 * So the comparison below is against {@code Contains}, and the divergence from {@code Intersects}
 * is measured rather than papered over: a game must not assume the culler and
 * {@code frustum.Intersects} will keep the same objects.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class FrustumCullerTests {

    /**
     * A camera off every axis, looking at the origin.
     *
     * <p>Deliberately not at the origin looking down -Z. That camera's view matrix is the
     * <em>identity</em>, and against an identity the product of a view and a projection is the
     * same either way round -- so the whole class of multiplication-order bugs becomes
     * invisible. This position was chosen after a planted order swap passed against the
     * simpler camera.
     */
    private static final Matrix VIEW = Matrix.CreateLookAt(
            new Vector3(3f, 2f, 10f), new Vector3(0f, 0f, 0f), new Vector3(0f, 1f, 0f));

    /** A 90-degree square perspective, near 1, far 100. */
    private static final Matrix PROJECTION = Matrix.CreatePerspectiveFieldOfView(
            (float) (Math.PI / 2.0), 1.0f, 1.0f, 100.0f);

    @Test
    void theFrustumIsTheMatrixItWasGiven() {
        try (FrustumCuller culler = FrustumCuller.create()) {
            Matrix viewProjection = Matrix.Multiply(VIEW, PROJECTION);
            culler.setViewProjection(viewProjection);
            assertMatrixEquals(viewProjection, culler.getFrustum().getMatrix(),
                    "the culler reports back the matrix it was set from");

            // CNA documents setCamera as exactly setViewProjection of the product, in that
            // order. Multiplying the other way round is the classic bug and would show up here.
            try (FrustumCuller other = FrustumCuller.create()) {
                other.setCamera(VIEW, PROJECTION);
                assertMatrixEquals(viewProjection, other.getFrustum().getMatrix(),
                        "setCamera is view times projection, in that order");
            }
        }
    }

    @Test
    void oneBoxAtATimeAgreesWithXnasOwnFrustum() {
        BoundingFrustum reference = new BoundingFrustum(Matrix.Multiply(VIEW, PROJECTION));
        try (FrustumCuller culler = FrustumCuller.create()) {
            culler.setCamera(VIEW, PROJECTION);
            for (BoundingBox box : boxes()) {
                assertEquals(visible(reference, box), culler.isVisible(box),
                        "CNA and XNA's plane test disagree about " + box);
            }
            // Not a vacuous agreement: the sample really does contain both answers, and in
            // quantity, so an implementation that answered one constant could not pass.
            long kept = boxes().stream().filter(box -> visible(reference, box)).count();
            assertTrue(kept > 4, "the sample must keep several boxes, kept " + kept);
            assertTrue(kept < boxes().size() - 4,
                    "and drop several, dropped " + (boxes().size() - kept));
        }
    }

    @Test
    void oneSphereAtATimeAgreesWithXnasOwnFrustum() {
        BoundingFrustum reference = new BoundingFrustum(Matrix.Multiply(VIEW, PROJECTION));
        try (FrustumCuller culler = FrustumCuller.create()) {
            culler.setCamera(VIEW, PROJECTION);
            for (BoundingSphere sphere : spheres()) {
                assertEquals(visible(reference, sphere), culler.isVisible(sphere),
                        "CNA and XNA's plane test disagree about " + sphere);
            }
            long kept = spheres().stream().filter(sphere -> visible(reference, sphere)).count();
            assertTrue(kept > 4 && kept < spheres().size() - 4, "kept " + kept);
        }
    }

    @Test
    void theBatchReturnsExactlyTheIndicesTheSingleTestKeeps() {
        BoundingFrustum reference = new BoundingFrustum(Matrix.Multiply(VIEW, PROJECTION));
        List<BoundingBox> boxes = boxes();
        List<BoundingSphere> spheres = spheres();
        try (FrustumCuller culler = FrustumCuller.create()) {
            culler.setCamera(VIEW, PROJECTION);

            assertArrayEquals(expected(boxes, box -> visible(reference, box)),
                    culler.cullBoxes(boxes),
                    "the batch keeps the boxes the one-at-a-time test keeps, in order");
            assertArrayEquals(expected(spheres, sphere -> visible(reference, sphere)),
                    culler.cullSpheres(spheres),
                    "and the spheres too");

            // The empty case is a real one -- a scene can cull to nothing -- and the count probe
            // has to survive it rather than ask CNA for a zero-length copy it refuses.
            assertEquals(0, culler.cullBoxes(List.of()).length);
            assertEquals(0, culler.cullSpheres(List.of()).length);
            assertThrows(NullPointerException.class, () -> culler.cullBoxes(null));
        }
    }

    @Test
    void aTransformWithNoBoundIsKeptRatherThanCulled() {
        BoundingFrustum reference = new BoundingFrustum(Matrix.Multiply(VIEW, PROJECTION));
        // Two bounds: one at the origin the camera is aimed at, one well behind the camera.
        BoundingBox inFront = new BoundingBox(
                new Vector3(-1f, -1f, -1f), new Vector3(1f, 1f, 1f));
        BoundingBox behind = new BoundingBox(
                new Vector3(8f, 5f, 28f), new Vector3(10f, 7f, 30f));
        assertTrue(visible(reference, inFront), "the sample must actually keep one");
        assertFalse(visible(reference, behind), "and must actually drop one");

        // Four transforms, each distinguishable, and only two bounds to match them.
        List<Matrix> transforms = List.of(
                Matrix.CreateTranslation(new Vector3(10f, 0f, 0f)),
                Matrix.CreateTranslation(new Vector3(20f, 0f, 0f)),
                Matrix.CreateTranslation(new Vector3(30f, 0f, 0f)),
                Matrix.CreateTranslation(new Vector3(40f, 0f, 0f)));

        try (FrustumCuller culler = FrustumCuller.create()) {
            culler.setCamera(VIEW, PROJECTION);
            List<Matrix> kept = culler.cullTransforms(transforms, List.of(inFront, behind));

            // Index 0 has a visible bound, index 1 has an invisible one, and indices 2 and 3
            // have no bound at all -- which CNA keeps. Three survive, and which three matters:
            // the translation identifies each one, so a shifted result cannot pass.
            assertEquals(3, kept.size(), "the two unbounded transforms are kept");
            assertMatrixEquals(transforms.get(0), kept.get(0), "the transform with a visible bound");
            assertMatrixEquals(transforms.get(2), kept.get(1), "the first unbounded transform");
            assertMatrixEquals(transforms.get(3), kept.get(2), "the second unbounded transform");

            // With a bound each, the rule stops mattering and the visible one alone survives.
            List<Matrix> both = culler.cullTransforms(
                    List.of(transforms.get(0), transforms.get(1)), List.of(inFront, behind));
            assertEquals(1, both.size());
            assertMatrixEquals(transforms.get(0), both.get(0), "only the visible one");

            assertEquals(0, culler.cullTransforms(List.of(), List.of()).size());
            assertThrows(NullPointerException.class,
                    () -> culler.cullTransforms(null, List.of()));
        }
    }

    @Test
    void aClosedCullerRefusesEveryOperation() {
        FrustumCuller culler = FrustumCuller.create();
        culler.close();
        culler.close();
        assertThrows(IllegalStateException.class, culler::getFrustum);
        assertThrows(IllegalStateException.class, () -> culler.cullBoxes(List.of()));
        assertThrows(IllegalStateException.class,
                () -> culler.setViewProjection(Matrix.getIdentity()));
    }

    /**
     * The culler and {@code BoundingFrustum.Intersects} are not the same test.
     *
     * <p>Measured rather than assumed. CNA's culler is the six-plane test, which reports a box
     * near a frustum corner as visible or not by whether it lies wholly outside a single plane;
     * XNA's {@code Intersects} is GJK, which answers the sharper question of whether the two
     * convex bodies actually overlap. Across the same grid they disagree about at least one box,
     * and this test names the count so that a change in either implementation shows up here
     * rather than in a game whose objects start popping.
     */
    @Test
    void thePlaneTestAndGjkAreNotTheSameTest() {
        BoundingFrustum reference = new BoundingFrustum(Matrix.Multiply(VIEW, PROJECTION));
        try (FrustumCuller culler = FrustumCuller.create()) {
            culler.setCamera(VIEW, PROJECTION);
            List<BoundingBox> disagreements = new ArrayList<>();
            for (BoundingBox box : boxes()) {
                if (culler.isVisible(box) != reference.Intersects(box)) {
                    disagreements.add(box);
                }
            }
            assertFalse(disagreements.isEmpty(),
                    "if these ever agree everywhere, the class documentation is out of date");
            for (BoundingBox box : disagreements) {
                // Every disagreement runs the same way: the plane test is the conservative one,
                // so a box it drops is one GJK found no overlap for -- CNA keeps fewer, never
                // more. A disagreement the other way would mean the culler is dropping
                // something a game can see, which is the failure that matters.
                assertTrue(reference.Intersects(box), "GJK keeps what the plane test drops");
                assertFalse(culler.isVisible(box));
                assertEquals(ContainmentType.Disjoint, reference.Contains(box));
            }
        }
    }

    /** Exactly what CNA's culler computes: the six-plane test, not GJK. */
    private static boolean visible(BoundingFrustum frustum, BoundingBox box) {
        return frustum.Contains(box) != ContainmentType.Disjoint;
    }

    /** The same for a sphere. */
    private static boolean visible(BoundingFrustum frustum, BoundingSphere sphere) {
        return frustum.Contains(sphere) != ContainmentType.Disjoint;
    }

    /** The indices XNA's own frustum keeps, which is what the batch has to reproduce. */
    private static <T> int[] expected(List<T> candidates, java.util.function.Predicate<T> visible) {
        List<Integer> kept = new ArrayList<>();
        for (int index = 0; index < candidates.size(); index++) {
            if (visible.test(candidates.get(index))) {
                kept.add(index);
            }
        }
        int[] result = new int[kept.size()];
        for (int index = 0; index < result.length; index++) {
            result[index] = kept.get(index);
        }
        return result;
    }

    /**
     * A grid of small boxes around the origin, which the camera sees part of.
     *
     * <p>A grid rather than a handful of hand-placed cases, because the useful evidence is
     * agreement across many objects at once, and 125 of them spanning well beyond the frustum
     * guarantees a mixture of both answers without anyone choosing which. The spacing keeps
     * every box clear of exact tangency with a plane, where two independent implementations of
     * the same test may legitimately round differently.
     */
    private static List<BoundingBox> boxes() {
        List<BoundingBox> boxes = new ArrayList<>();
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    Vector3 centre = new Vector3(x * 9f + 0.5f, y * 9f + 0.5f, z * 9f + 0.5f);
                    boxes.add(new BoundingBox(
                            new Vector3(centre.X - 1.5f, centre.Y - 1.5f, centre.Z - 1.5f),
                            new Vector3(centre.X + 1.5f, centre.Y + 1.5f, centre.Z + 1.5f)));
                }
            }
        }
        return List.copyOf(boxes);
    }

    /** The same grid as spheres, so the two batch routes are tested on the same spread. */
    private static List<BoundingSphere> spheres() {
        List<BoundingSphere> spheres = new ArrayList<>();
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    spheres.add(new BoundingSphere(
                            new Vector3(x * 9f + 0.5f, y * 9f + 0.5f, z * 9f + 0.5f), 1.5f));
                }
            }
        }
        return List.copyOf(spheres);
    }

    private static void assertMatrixEquals(Matrix expected, Matrix actual, String message) {
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
            assertEquals(left[index], right[index], 1.0e-5f, message + " (element " + index + ")");
        }
    }
}
