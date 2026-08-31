package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.BoundingBox;
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
 * <p><strong>What this can say depends on the renderer, and it says which.</strong> Whether a
 * capture can be read back is measured by CNA at construction rather than published as a
 * capability, and the two outcomes are qualified separately. On {@code HEADLESS} the target binds
 * happily, the readback is refused, and all three bake routes refuse with {@code INVALID_STATE}
 * having entered the callback zero times. On {@code OPENGLES3} and {@code OPENGL33} the bake runs
 * and the callback is entered once per cube face, which is what the face counts here assert.
 *
 * <p>The six face cameras are the substance. They are checked for being six <em>different</em>
 * cameras that between them look along all six axes and all sit at the capture position, which is
 * what a cube capture means; a baker that returned the same matrix six times, or that ignored the
 * position, would pass an existence check and fail every one of these.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class LightProbeBakerTests {

    @Test
    void whetherThisRendererCanCaptureIsMeasuredAtConstruction() {
        GameProbe.run(probe -> {
            try (LightProbeBaker baker = LightProbeBaker.create(probe.device())) {
                // The measurement CNA took at construction, whichever way it came out. A
                // renderer that binds an offscreen target and then refuses the readback -- which
                // HEADLESS does -- answers false, and a game that bakes at load time has to know
                // before it spends the time. Nothing is asserted about which answer this
                // renderer gives; what is asserted is that the answer decides what the bake
                // routes do, in the two tests below.
                boolean supported = baker.isSupported();
                try (LightProbeBaker second = LightProbeBaker.create(probe.device())) {
                    assertEquals(supported, second.isSupported(),
                            "the measurement is a property of the renderer, not of one baker");
                }
            }
        });
    }

    @Test
    void aBakeDrawsSixFacesPerProbeOrRefusesOutright() {
        GameProbe.run(probe -> {
            try (LightProbeBaker baker = LightProbeBaker.create(probe.device())) {
                List<Matrix> views = new ArrayList<>();
                List<Matrix> projections = new ArrayList<>();
                LightProbeBaker.SceneDraw record = (view, projection) -> {
                    views.add(view);
                    projections.add(projection);
                };
                if (!baker.isSupported()) {
                    // Documented, and measured in light_probe_bake.c: a renderer that cannot
                    // read a capture back refuses with INVALID_STATE and enters the callback
                    // zero times. Asserted rather than skipped, so a renderer that gains the
                    // capability shows up here.
                    assertThrows(IllegalStateException.class,
                            () -> baker.bakeProbe(new Vector3(0f, 1f, 0f), record));
                    assertEquals(0, views.size(),
                            "a refused bake must not have entered the callback");
                    return;
                }

                try (LightProbe baked = baker.bakeProbe(new Vector3(0f, 1f, 0f), record)) {
                    // Six faces, once each. A baker that returned success without capturing
                    // would have drawn none, and that is the difference this counts.
                    assertEquals(LightProbeBaker.getFaceCount(), views.size(),
                            "one callback per cube face");
                    assertEquals(views.size(), projections.size());
                    // A probe CNA produced from a real capture, and its position is where it
                    // was told to capture from -- which a probe that was merely allocated and
                    // handed back would not have.
                    assertEquals(1f, baked.getPosition().Y, 1.0e-4f,
                            "the probe is at the capture position");

                    // The six cameras are six different ones, and each is the camera the baker
                    // would have handed out for that face. A bake that passed the same matrix
                    // six times, or the faces in a different order, fails here.
                    for (int face = 0; face < views.size(); face++) {
                        Matrix expected = baker.faceView(face, new Vector3(0f, 1f, 0f));
                        assertEquals(expected.M31, views.get(face).M31, 1.0e-4f,
                                "face " + face + " was drawn with its own view");
                        assertEquals(expected.M41, views.get(face).M41, 1.0e-4f,
                                "face " + face + " was drawn from the capture position");
                    }
                    // And the projection is the square ninety-degree frustum a cube face needs,
                    // the same one faceProjection() derives.
                    assertEquals(baker.faceProjection().M11, projections.get(0).M11, 1.0e-4f);
                }
            }
        });
    }

    @Test
    void bakingAVolumeDrawsSixFacesForEveryProbeInIt() {
        GameProbe.run(probe -> {
            try (LightProbeBaker baker = LightProbeBaker.create(probe.device());
                    LightProbeVolume volume = LightProbeVolume.create(
                            new BoundingBox(new Vector3(-1f, -1f, -1f), new Vector3(1f, 1f, 1f)),
                            2, 2, 2)) {
                int[] calls = new int[1];
                LightProbeBaker.SceneDraw count = (view, projection) -> calls[0]++;
                if (!baker.isSupported()) {
                    assertThrows(IllegalStateException.class,
                            () -> baker.bakeLight(volume, count));
                    assertEquals(0, calls[0]);
                    return;
                }
                int lightFaces = baker.bakeLight(volume, count);
                // Eight probes, six faces each. The arithmetic is what says the whole volume was
                // walked rather than one probe of it.
                assertEquals(8 * LightProbeBaker.getFaceCount(), lightFaces,
                        "six faces for each of the volume's eight probes");
                assertEquals(lightFaces, calls[0],
                        "and the callback ran once for each of them");

                calls[0] = 0;
                int visibilityFaces = baker.bakeVisibility(volume, count);
                assertEquals(lightFaces, visibilityFaces,
                        "a visibility bake walks the same probes");
                assertEquals(visibilityFaces, calls[0]);
            }
        });
    }

    @Test
    void anExceptionFromTheCallbackSurfacesAtTheBakeAndStopsTheRemainingFaces() {
        GameProbe.run(probe -> {
            try (LightProbeBaker baker = LightProbeBaker.create(probe.device())) {
                if (!baker.isSupported()) {
                    return;
                }
                int[] calls = new int[1];
                IllegalStateException thrown = assertThrows(IllegalStateException.class,
                        () -> baker.bakeProbe(new Vector3(0f, 0f, 0f), (view, projection) -> {
                            calls[0]++;
                            throw new IllegalStateException("the scene refused");
                        }));
                assertEquals("the scene refused", thrown.getMessage(),
                        "the callback's own exception surfaces, not a translated result");
                // CNA's callback returns nothing and cannot refuse, so the trampoline skips the
                // remaining faces rather than calling into Java with an exception pending --
                // which is undefined in JNI and is what -Xcheck:jni fails a suite for.
                assertEquals(1, calls[0],
                        "the faces after the failing one must not have been entered");
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
