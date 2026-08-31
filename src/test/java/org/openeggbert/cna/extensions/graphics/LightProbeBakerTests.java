package org.openeggbert.cna.extensions.graphics;

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
 * The light-probe baker, against the live runtime.
 *
 * <p><strong>What this can and cannot say.</strong> VERIFIED_HEADLESS_GAME for the baker's
 * configuration and its face cameras, which are real matrices with real content.
 * NOT_SUPPORTED_BY_RENDERER for capture itself: {@link LightProbeBaker#isSupported()} is
 * {@code false} here, because the headless renderer binds an offscreen target happily and then
 * refuses to read it back -- and that refusal is asserted rather than skipped, because a game
 * branches on it.
 *
 * <p>The six face cameras are the substance. They are checked for being six <em>different</em>
 * cameras that between them look along all six axes and all sit at the capture position, which is
 * what a cube capture means; a baker that returned the same matrix six times, or that ignored the
 * position, would pass an existence check and fail every one of these.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class LightProbeBakerTests {

    @Test
    void thisRendererCannotCaptureAndTheBakerSaysSoRatherThanFailingLater() {
        GameProbe.run(probe -> {
            try (LightProbeBaker baker = LightProbeBaker.create(probe.device())) {
                // The measurement CNA took at construction. Asserted as what it is: a HEADLESS
                // renderer binds the target and refuses the readback, so a game that bakes at
                // load time has to know before it spends the time.
                assertFalse(baker.isSupported(),
                        "the headless renderer cannot read a capture back");
            }
        });
    }

    @Test
    void theFaceSizeIsTheOneAskedForAndTheCountIsSix() {
        GameProbe.run(probe -> {
            try (LightProbeBaker byDefault = LightProbeBaker.create(probe.device());
                 LightProbeBaker chosen = LightProbeBaker.create(probe.device(), 64)) {
                assertEquals(6, LightProbeBaker.getFaceCount(), "a cube has six faces");
                assertTrue(byDefault.getFaceSize() > 0);
                assertEquals(64, chosen.getFaceSize(), "the size asked for is the size used");
                assertNotEquals(byDefault.getFaceSize(), chosen.getFaceSize(),
                        "and it is not the default in disguise");

                assertThrows(IllegalArgumentException.class,
                        () -> LightProbeBaker.create(probe.device(), 0));
                assertThrows(IllegalArgumentException.class,
                        () -> LightProbeBaker.create(probe.device(), -8));
                assertThrows(NullPointerException.class, () -> LightProbeBaker.create(null));
            }
        });
    }

    @Test
    void thePlanesMoveTogetherAndARefusedPairChangesNeither() {
        GameProbe.run(probe -> {
            try (LightProbeBaker baker = LightProbeBaker.create(probe.device())) {
                float near = baker.getNearPlane();
                float far = baker.getFarPlane();
                assertTrue(near > 0f && far > near,
                        "CNA's defaults are already an ordered pair: " + near + ".." + far);

                baker.setPlanes(0.5f, 200f);
                assertEquals(0.5f, baker.getNearPlane(), 1.0e-6f);
                assertEquals(200f, baker.getFarPlane(), 1.0e-6f);

                // The two refusals, and -- the part worth testing -- that neither of them moved
                // anything. A setter that wrote the near plane before checking the pair would
                // leave a baker whose planes were crossed.
                assertThrows(IllegalArgumentException.class, () -> baker.setPlanes(10f, 1f));
                assertThrows(IllegalArgumentException.class, () -> baker.setPlanes(-1f, 100f));
                assertThrows(IllegalArgumentException.class, () -> baker.setPlanes(5f, 5f));
                assertEquals(0.5f, baker.getNearPlane(), 1.0e-6f,
                        "a refused pair left the near plane alone");
                assertEquals(200f, baker.getFarPlane(), 1.0e-6f,
                        "and the far one");
            }
        });
    }

    @Test
    void theSixFaceCamerasLookAlongTheSixAxesFromTheCapturePosition() {
        GameProbe.run(probe -> {
            try (LightProbeBaker baker = LightProbeBaker.create(probe.device())) {
                Vector3 position = new Vector3(1f, 2f, 3f);
                List<Vector3> forwards = new ArrayList<>();
                for (int face = 0; face < LightProbeBaker.getFaceCount(); face++) {
                    Matrix view = baker.faceView(face, position);

                    // A view matrix is the inverse of the camera's transform, so inverting it
                    // gives the camera back. Every face must be at the capture position -- a
                    // baker that ignored the position would put them all at the origin.
                    Matrix camera = Matrix.Invert(view);
                    assertEquals(position.X, camera.M41, 1.0e-4f, "face " + face + " x");
                    assertEquals(position.Y, camera.M42, 1.0e-4f, "face " + face + " y");
                    assertEquals(position.Z, camera.M43, 1.0e-4f, "face " + face + " z");

                    // The third row of the camera transform is where it looks.
                    forwards.add(new Vector3(camera.M31, camera.M32, camera.M33));
                }

                // Six different directions, each a unit vector, and between them one along each
                // axis in both directions -- which is what makes the capture a cube rather than
                // six views of the same thing.
                for (Vector3 forward : forwards) {
                    assertEquals(1f, forward.Length(), 1.0e-4f, "each face looks somewhere");
                }
                for (int left = 0; left < forwards.size(); left++) {
                    for (int right = left + 1; right < forwards.size(); right++) {
                        float dot = Vector3.Dot(forwards.get(left), forwards.get(right));
                        assertTrue(Math.abs(dot) < 1.0e-3f || Math.abs(dot + 1f) < 1.0e-3f,
                                "faces " + left + " and " + right
                                        + " are perpendicular or opposite, not " + dot);
                    }
                }
                for (int axis = 0; axis < 3; axis++) {
                    for (float sign : new float[] {1f, -1f}) {
                        Vector3 wanted = new Vector3(axis == 0 ? sign : 0f,
                                axis == 1 ? sign : 0f, axis == 2 ? sign : 0f);
                        boolean found = false;
                        for (Vector3 forward : forwards) {
                            found |= Vector3.Distance(forward, wanted) < 1.0e-3f;
                        }
                        assertTrue(found, "no face looks along " + wanted);
                    }
                }

                // And the position really is read: capturing somewhere else moves every camera.
                Matrix elsewhere = Matrix.Invert(baker.faceView(0, new Vector3(-5f, 0f, 7f)));
                assertEquals(-5f, elsewhere.M41, 1.0e-4f);
                assertEquals(7f, elsewhere.M43, 1.0e-4f);

                assertThrows(IllegalArgumentException.class, () -> baker.faceView(6, position));
                assertThrows(IllegalArgumentException.class, () -> baker.faceView(-1, position));
                assertThrows(NullPointerException.class, () -> baker.faceView(0, null));
            }
        });
    }

    @Test
    void theFaceProjectionIsASquareRightAngleFrustumOverTheBakersOwnPlanes() {
        GameProbe.run(probe -> {
            try (LightProbeBaker baker = LightProbeBaker.create(probe.device())) {
                baker.setPlanes(0.25f, 400f);
                Matrix projection = baker.faceProjection();

                // Ninety degrees over a square face makes both scales one: anything else would
                // capture the wrong solid angle and leave seams between the faces.
                assertEquals(1f, projection.M11, 1.0e-5f, "horizontal scale");
                assertEquals(1f, projection.M22, 1.0e-5f, "vertical scale");

                // And it follows the baker's planes rather than being a constant: a point at the
                // near plane lands on the near clip and one at the far plane on the far clip.
                assertEquals(0f, depthOf(projection, 0.25f), 1.0e-4f, "the near plane clips at 0");
                assertEquals(1f, depthOf(projection, 400f), 1.0e-4f, "the far plane clips at 1");

                baker.setPlanes(1f, 10f);
                assertEquals(0f, depthOf(baker.faceProjection(), 1f), 1.0e-4f,
                        "and it moves when the planes move");
            }
        });
    }

    @Test
    void aClosedBakerIsClosedAndSaysSo() {
        GameProbe.run(probe -> {
            LightProbeBaker baker = LightProbeBaker.create(probe.device());
            baker.close();
            baker.close();
            assertThrows(IllegalStateException.class, baker::isSupported);
            assertThrows(IllegalStateException.class, baker::getFaceSize);
            assertThrows(IllegalStateException.class, () -> baker.setPlanes(1f, 2f));
            assertThrows(IllegalStateException.class,
                    () -> baker.faceView(0, Vector3.getZero()));
        });
    }

    /** Where a point that far down -Z lands in clip depth, which is what a projection decides. */
    private static float depthOf(Matrix projection, float distance) {
        float z = -distance;
        float clipZ = projection.M33 * z + projection.M43;
        float clipW = projection.M34 * z + projection.M44;
        return clipZ / clipW;
    }
}
