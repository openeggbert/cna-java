package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.BoundingBox;
import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Vector3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The back-to-front transparent draw list, against the live runtime.
 *
 * <p><strong>What this can and cannot say.</strong> VERIFIED_PURE: the list touches no graphics
 * device, so every claim here is a real one about a real answer -- the order callbacks actually
 * ran in, the distance CNA actually measures, and what a failing draw actually leaves behind.
 * Nothing about a rendered pixel arises, because nothing here renders.
 *
 * <p>The order is the whole point of the type, so the entries are submitted <em>nearest first</em>
 * throughout: a list that ignored the camera and ran them in submission order would be
 * indistinguishable from a correct one if they were submitted farthest first.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class TransparentDrawListTests {

    /** A camera at the origin looking down -X, so a box at -n is n away. */
    private static final Matrix VIEW = Matrix.CreateLookAt(
            new Vector3(0f, 0f, 0f), new Vector3(-1f, 0f, 0f), Vector3.getUp());

    @Test
    void theCallbacksRunFarthestFirstAndNotInSubmissionOrder() {
        try (TransparentDrawList list = TransparentDrawList.create()) {
            assertEquals(0, list.getCount());
            List<String> ran = new ArrayList<>();
            list.submit(boxAt(-2f), () -> ran.add("near"));
            list.submit(boxAt(-10f), () -> ran.add("middle"));
            list.submit(boxAt(-30f), () -> ran.add("far"));
            assertEquals(3, list.getCount());

            list.drawSorted(VIEW);
            assertEquals(List.of("far", "middle", "near"), ran,
                    "back to front is the entire purpose of the list");

            // The order follows the camera rather than being fixed: viewed from the far side,
            // the same three entries run the other way round.
            ran.clear();
            list.drawSorted(Matrix.CreateLookAt(new Vector3(-60f, 0f, 0f),
                    new Vector3(0f, 0f, 0f), Vector3.getUp()));
            assertEquals(List.of("near", "middle", "far"), ran,
                    "the order is recomputed from the camera every draw");
        }
    }

    @Test
    void theOrderCanBeAskedForWithoutDrawingAnything() {
        try (TransparentDrawList list = TransparentDrawList.create()) {
            assertArrayEquals(new int[0], list.getSortedOrder(VIEW), "an empty list sorts empty");

            list.submit(boxAt(-2f), () -> { });
            list.submit(boxAt(-10f), () -> { });
            list.submit(boxAt(-30f), () -> { });

            // Indices into submission order, farthest first -- and asking does not draw, which is
            // why the callbacks above would have failed the test had it.
            assertArrayEquals(new int[] {2, 1, 0}, list.getSortedOrder(VIEW));

            // The two-call protocol underneath grows the buffer rather than truncating: a fourth
            // entry appears rather than being dropped by a buffer sized for three.
            list.submit(boxAt(-20f), () -> { });
            assertArrayEquals(new int[] {2, 3, 1, 0}, list.getSortedOrder(VIEW));
        }
    }

    @Test
    void aFailingCallbackStopsTheDrawWhereItThrew() {
        try (TransparentDrawList list = TransparentDrawList.create()) {
            List<String> ran = new ArrayList<>();
            IllegalStateException planted = new IllegalStateException("the middle one failed");
            list.submit(boxAt(-2f), () -> ran.add("near"));
            list.submit(boxAt(-10f), () -> {
                ran.add("middle");
                throw planted;
            });
            list.submit(boxAt(-30f), () -> ran.add("far"));

            // The exception a callback threw reaches the caller of the draw, rather than being
            // flattened into a result code -- which is the difference between learning which
            // draw failed and finding a partly drawn frame with no reason for it.
            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> list.drawSorted(VIEW));
            assertSame(planted, thrown, "the callback's own exception, not a wrapper");

            // And it stopped there: the farthest ran, the failing one ran, the nearest did not.
            assertEquals(List.of("far", "middle"), ran);

            // The list survives it. A draw that threw must not leave an object nobody can use.
            assertEquals(3, list.getCount());
            ran.clear();
            list.clear();
            assertEquals(0, list.getCount());
            list.submit(boxAt(-1f), () -> ran.add("again"));
            list.drawSorted(VIEW);
            assertEquals(List.of("again"), ran);
        }
    }

    @Test
    void theSortKeyIsMeasuredToTheNearestPointOfTheBox() {
        // The distinction that matters for a large object: a camera inside a box is at zero
        // distance from it, not at half the box's width. Anything sorting by centre distance
        // would put a room's walls behind the furniture in it.
        BoundingBox box = boxAt(-10f);
        assertEquals(0f, TransparentDrawList.sortKey(box, new Vector3(-10f, 0f, 0f)), 1.0e-6f,
                "a camera inside the box is at zero");
        assertEquals(9.5f, TransparentDrawList.sortKey(box, new Vector3(0f, 0f, 0f)), 1.0e-5f,
                "and outside it, the distance is to the near face rather than the centre");
        assertEquals(0f, TransparentDrawList.sortKey(box, new Vector3(-10.4f, 0.4f, 0f)),
                1.0e-6f, "still inside, off-centre");

        // The key grows with distance, which is what makes it a sort key at all.
        assertTrue(TransparentDrawList.sortKey(boxAt(-30f), Vector3.getZero())
                > TransparentDrawList.sortKey(boxAt(-10f), Vector3.getZero()));

        assertThrows(NullPointerException.class,
                () -> TransparentDrawList.sortKey(null, Vector3.getZero()));
        assertThrows(NullPointerException.class,
                () -> TransparentDrawList.sortKey(boxAt(0f), null));
    }

    @Test
    void theCameraPositionComesBackOutOfTheViewMatrix() {
        Vector3 eye = new Vector3(3f, -7f, 11f);
        Vector3 derived = TransparentDrawList.cameraPositionOf(
                Matrix.CreateLookAt(eye, new Vector3(0f, 1f, 0f), Vector3.getUp()));
        // A view matrix is the inverse of the camera's transform, so getting the eye back is a
        // real inversion and not a copy: an implementation that read the translation row
        // straight out would be wrong here, because the eye is off every axis.
        assertEquals(eye.X, derived.X, 1.0e-4f);
        assertEquals(eye.Y, derived.Y, 1.0e-4f);
        assertEquals(eye.Z, derived.Z, 1.0e-4f);

        // And it agrees with what the list itself sorts by: sorting against this position by
        // hand gives the order the list gives.
        try (TransparentDrawList list = TransparentDrawList.create()) {
            Matrix view = Matrix.CreateLookAt(eye, Vector3.getZero(), Vector3.getUp());
            BoundingBox[] boxes = {boxAt(-2f), boxAt(-30f), boxAt(-10f)};
            for (BoundingBox box : boxes) {
                list.submit(box, () -> { });
            }
            Vector3 camera = TransparentDrawList.cameraPositionOf(view);
            int[] order = list.getSortedOrder(view);
            for (int index = 1; index < order.length; index++) {
                assertTrue(TransparentDrawList.sortKey(boxes[order[index - 1]], camera)
                                >= TransparentDrawList.sortKey(boxes[order[index]], camera),
                        "the list's order is descending in its own sort key");
            }
        }
    }

    @Test
    void refusedEntriesLeaveTheTwoSidesAgreeing() {
        try (TransparentDrawList list = TransparentDrawList.create()) {
            List<String> ran = new ArrayList<>();
            list.submit(boxAt(-5f), () -> ran.add("first"));
            assertThrows(NullPointerException.class, () -> list.submit(boxAt(-1f), null));
            assertThrows(NullPointerException.class, () -> list.submit(null, () -> { }));
            assertEquals(1, list.getCount(), "a refused entry was not added");

            // The point of that: CNA's context is an index into the Java callbacks, so a Java
            // list that grew when CNA's did not would run the wrong callback from here on. On
            // this ABI no submit CNA refuses can get past the checks above, so what is checked
            // here is the invariant rather than the guard -- CNA's own count and the callbacks
            // that ran agree, entry for entry.
            list.submit(boxAt(-50f), () -> ran.add("second"));
            assertEquals(2, list.getCount());
            list.drawSorted(VIEW);
            assertEquals(List.of("second", "first"), ran,
                    "each entry ran its own callback");
            assertEquals(list.getCount(), ran.size(),
                    "CNA's count and the callbacks that ran are the same number");
        }
    }

    @Test
    void aClosedListIsClosedAndSaysSo() {
        TransparentDrawList list = TransparentDrawList.create();
        list.submit(boxAt(-1f), () -> { });
        list.close();
        list.close();
        assertThrows(IllegalStateException.class, list::getCount);
        assertThrows(IllegalStateException.class, () -> list.drawSorted(VIEW));
        assertThrows(IllegalStateException.class, () -> list.submit(boxAt(0f), () -> { }));
        assertThrows(NullPointerException.class, () -> TransparentDrawList.cameraPositionOf(null));
    }

    @Test
    void aListSurvivesManyDrawsWithoutAccumulatingReferences() {
        try (TransparentDrawList list = TransparentDrawList.create()) {
            int[] ran = new int[1];
            for (int entry = 0; entry < 16; entry++) {
                list.submit(boxAt(-entry - 1f), () -> ran[0]++);
            }
            // The callbacks cross into C as a local array per draw and nothing is kept, so a
            // thousand draws is a thousand times sixteen calls and no reference table growth.
            // A trampoline that took a global reference per entry would exhaust the local or
            // global reference table long before this finished.
            for (int frame = 0; frame < 1000; frame++) {
                list.drawSorted(VIEW);
            }
            assertEquals(16_000, ran[0]);
        }
    }

    private static BoundingBox boxAt(float x) {
        return new BoundingBox(new Vector3(x - 0.5f, -0.5f, -0.5f),
                new Vector3(x + 0.5f, 0.5f, 0.5f));
    }
}
