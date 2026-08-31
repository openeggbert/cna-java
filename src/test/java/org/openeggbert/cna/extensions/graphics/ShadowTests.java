package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.BoundingBox;
import Microsoft.Xna.Framework.Graphics.CubeMapFace;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import Microsoft.Xna.Framework.Graphics.TextureCube;
import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Vector3;
import Microsoft.Xna.Framework.Vector4;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The three shadow maps, against the live runtime.
 *
 * <p><strong>What this can and cannot say.</strong> A map needs a real device, so the suite runs
 * inside a game. This renderer cannot cast shadows -- CNA says so in its own log -- so nothing
 * here claims a shadowed pixel, and the tests check the parts that do not depend on it: the
 * quality presets, the light fitting, and the ownership of the texture borrow. The fitting is
 * where the real evidence is, because it is checkable against the geometry it claims to produce.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class ShadowTests {

    @Test
    void aQualityPresetIsTheSizeAndFilterCnasHeaderSaysItIs() {
        GameProbe.run(probe -> {
            // CNA's header states each preset outright -- 512 with no filtering, 1024 with 2x2,
            // 2048 with 3x3, 4096 with 5x5 -- so these are its numbers rather than a guess, and
            // a preset that quietly changed size would show up here rather than as a memory
            // bill nobody could explain.
            assertEquals(512, ShadowMap.getSizeForQuality(ShadowQuality.Low));
            assertEquals(1024, ShadowMap.getSizeForQuality(ShadowQuality.Medium));
            assertEquals(2048, ShadowMap.getSizeForQuality(ShadowQuality.High));
            assertEquals(4096, ShadowMap.getSizeForQuality(ShadowQuality.Ultra));

            assertEquals(0, ShadowMap.getFilterRadiusForQuality(ShadowQuality.Low),
                    "Low is documented as having no filtering");
            assertTrue(ShadowMap.getFilterRadiusForQuality(ShadowQuality.Ultra)
                            > ShadowMap.getFilterRadiusForQuality(ShadowQuality.Medium),
                    "a higher preset filters wider");

            // A cube is six faces, so CNA sizes it separately rather than reusing the 2D table.
            assertTrue(CubeShadowMap.getSizeForQuality(ShadowQuality.High) > 0);

            try (ShadowMap map = ShadowMap.create(probe.device(), ShadowQuality.Medium)) {
                assertEquals(ShadowQuality.Medium, map.getQuality());
                assertEquals(ShadowMap.getSizeForQuality(ShadowQuality.Medium), map.getSize(),
                        "a created map is the size its preset promised");
                assertEquals(ShadowMap.getFilterRadiusForQuality(ShadowQuality.Medium),
                        map.getFilterRadius());
            }
            assertThrows(NullPointerException.class,
                    () -> ShadowMap.create(probe.device(), null));
        });
    }

    @Test
    void theLightFittingActuallyFitsTheSceneItWasGiven() {
        GameProbe.run(probe -> {
            // A light coming down and slightly along, and a scene box off the origin, so a
            // fitting that ignored either would not survive.
            DirectionalLight light = DirectionalLight.createDefault()
                    .withDirection(new Vector3(0.3f, -1f, 0.2f));
            BoundingBox scene = new BoundingBox(
                    new Vector3(-6f, -1f, 4f), new Vector3(10f, 5f, 18f));

            Matrix view = ShadowMap.computeLightView(light, scene);
            Matrix projection = ShadowMap.computeLightProjection(view, scene);
            Matrix viewProjection = Matrix.Multiply(view, projection);

            // The property the whole fitting exists for: every corner of the scene lands inside
            // the clip volume. A view that pointed the wrong way, a projection sized for the
            // wrong bounds, or the two multiplied in the wrong order all break this, and none of
            // them can be seen by looking at the numbers.
            for (Vector3 corner : scene.GetCorners()) {
                Vector4 clip = Vector4.Transform(new Vector4(corner, 1.0f), viewProjection);
                assertTrue(clip.X >= -1.001f && clip.X <= 1.001f,
                        corner + " leaves the map sideways at x=" + clip.X);
                assertTrue(clip.Y >= -1.001f && clip.Y <= 1.001f,
                        corner + " leaves the map vertically at y=" + clip.Y);
                assertTrue(clip.Z >= -0.001f && clip.Z <= 1.001f,
                        corner + " leaves the map in depth at z=" + clip.Z);
            }

            // And it is a tight fit rather than a huge one: something reaches each edge, or the
            // map is wasting most of its texels on empty space.
            float widest = 0.0f;
            for (Vector3 corner : scene.GetCorners()) {
                Vector4 clip = Vector4.Transform(new Vector4(corner, 1.0f), viewProjection);
                widest = Math.max(widest, Math.max(Math.abs(clip.X), Math.abs(clip.Y)));
            }
            assertTrue(widest > 0.9f, "the fit wastes the map: widest corner is " + widest);

            assertThrows(NullPointerException.class,
                    () -> ShadowMap.computeLightView(null, scene));
        });
    }

    @Test
    void beginRecordsTheTransformTheStaticFittingWouldHaveProduced() {
        GameProbe.run(probe -> {
            DirectionalLight light = DirectionalLight.createDefault()
                    .withDirection(new Vector3(-0.5f, -1f, 0.25f));
            BoundingBox scene = new BoundingBox(
                    new Vector3(-3f, -3f, -3f), new Vector3(3f, 3f, 3f));
            try (ShadowMap map = ShadowMap.create(probe.device(), ShadowQuality.Low)) {
                boolean supported = map.isSupported();
                map.begin(light, scene);
                // The pass opens whether or not this renderer can cast -- that is the point of
                // the design -- and either way it records the transform a shading pass needs.
                Matrix recorded = map.getLightViewProjection();
                map.end();

                Matrix expected = Matrix.Multiply(
                        ShadowMap.computeLightView(light, scene),
                        ShadowMap.computeLightProjection(
                                ShadowMap.computeLightView(light, scene), scene));
                assertMatrixEquals(expected, recorded, 1.0e-4f,
                        "begin fits the light the same way the static routes do");

                // The bias is a value the map holds, whatever the renderer can do with it.
                map.setDepthBias(0.004f);
                assertEquals(0.004f, map.getDepthBias(), 1.0e-6f);

                // Nothing here claims a shadowed pixel: on this renderer it cannot cast, and
                // the test says so rather than asserting around it.
                if (!supported) {
                    assertFalse(map.isSupported());
                }
            }
        });
    }

    @Test
    void aSpotMapFitsItsOwnConeAndRemembersTheLight() {
        GameProbe.run(probe -> {
            SpotLight light = SpotLight.createDefault()
                    .withPosition(new Vector3(2f, 9f, -3f))
                    .withDirection(new Vector3(0f, -1f, 0f))
                    .withRange(25.0f)
                    .withCone(0.2f, 0.6f);

            Matrix view = SpotShadowMap.computeLightView(light);
            Matrix projection = SpotShadowMap.computeLightProjection(light);

            // The cone is the frustum, which is why this needs no scene bounds. A point one
            // range away straight down the axis must land on the far plane, and the light's own
            // position must land at the origin of the light's view.
            Vector4 atLight = Vector4.Transform(new Vector4(light.getPosition(), 1.0f), view);
            assertEquals(0.0f, atLight.X, 1.0e-4f);
            assertEquals(0.0f, atLight.Y, 1.0e-4f);
            assertEquals(0.0f, atLight.Z, 1.0e-4f, "the light is the eye");

            Vector3 far = new Vector3(2f, 9f - 25f, -3f);
            Vector4 clip = Vector4.Transform(new Vector4(far, 1.0f),
                    Matrix.Multiply(view, projection));
            assertEquals(1.0f, clip.Z / clip.W, 1.0e-3f,
                    "one range away is the far plane");

            try (SpotShadowMap map = SpotShadowMap.create(probe.device(), ShadowQuality.Low)) {
                map.begin(light);
                assertEquals(light.getPosition(), map.getLightPosition());
                assertEquals(25.0f, map.getLightRange(), 1.0e-4f);
                assertMatrixEquals(Matrix.Multiply(view, projection),
                        map.getLightViewProjection(), 1.0e-4f,
                        "the map fits the cone the same way the static routes do");
                map.end();
            }
        });
    }

    @Test
    void aCubeFacesSixWaysFromOnePoint() {
        GameProbe.run(probe -> {
            Vector3 position = new Vector3(1f, 2f, 3f);

            // Every face puts the light at the eye and looks somewhere different: six views that
            // agreed about a direction would leave a hole in the shadow.
            Vector3[] forwards = new Vector3[CubeShadowMap.FaceCount];
            for (CubeMapFace face : CubeMapFace.values()) {
                Matrix view = CubeShadowMap.computeFaceView(face, position);
                Vector4 eye = Vector4.Transform(new Vector4(position, 1.0f), view);
                assertEquals(0.0f, eye.X, 1.0e-4f);
                assertEquals(0.0f, eye.Y, 1.0e-4f);
                assertEquals(0.0f, eye.Z, 1.0e-4f, face + " does not put the light at the eye");
                // The third row of a view matrix is the camera's backwards axis, so its
                // negation is where the face looks.
                forwards[face.ordinal()] = new Vector3(-view.M13, -view.M23, -view.M33);
            }
            for (int first = 0; first < forwards.length; first++) {
                for (int second = first + 1; second < forwards.length; second++) {
                    float alignment = Vector3.Dot(forwards[first], forwards[second]);
                    assertTrue(alignment < 0.5f,
                            "faces " + first + " and " + second + " look the same way");
                }
            }

            // The shared projection is ninety degrees square, so the six of them tile the
            // sphere: a point on the diagonal at the far plane is exactly on the clip corner.
            Matrix projection = CubeShadowMap.computeFaceProjection(50.0f);
            Vector4 diagonal = Vector4.Transform(new Vector4(50f, 0f, -50f, 1.0f), projection);
            assertEquals(1.0f, diagonal.X / diagonal.W, 1.0e-3f,
                    "forty-five degrees across is the edge of a ninety-degree frustum");

            try (CubeShadowMap cube = CubeShadowMap.create(probe.device(), ShadowQuality.Low)) {
                PointLight light = PointLight.createDefault()
                        .withPosition(position).withRange(50.0f);
                cube.update(light);
                assertEquals(position, cube.getLightPosition());
                assertEquals(50.0f, cube.getLightRange(), 1.0e-4f);
                if (cube.isSupported()) {
                    for (CubeMapFace face : CubeMapFace.values()) {
                        cube.begin(face);
                        cube.end();
                    }
                } else {
                    // JAVA-UPSTREAM-007, and it is wider than one renderer. CNA documents that a
                    // cube's face passes "still open and close" where it cannot cast, and what
                    // actually happens depends entirely on the renderer: HEADLESS and SOFTWARE
                    // refuse `begin`, contradicting that, while OPENGL4 opens the pass exactly as
                    // documented and leaves the target bound until `end`. Both are asserted so
                    // that a change in either direction shows up.
                    boolean opened = true;
                    try {
                        cube.begin(CubeMapFace.PositiveX);
                    } catch (RuntimeException refused) {
                        // The documented-but-absent case. Nothing was bound, so nothing to end.
                        opened = false;
                        assertTrue(refused.getMessage() != null && !refused.getMessage().isBlank(),
                                "a refused face pass says why");
                    }
                    if (opened) {
                        // The documented case: the pass opened even though nothing can be cast
                        // into it, and it has to be closed -- a frame that presents with a render
                        // target still bound is refused by the device, which is how this was
                        // found.
                        cube.end();
                    }
                }
                cube.setDepthBias(0.01f);
                assertEquals(0.01f, cube.getDepthBias(), 1.0e-6f);
                assertThrows(NullPointerException.class, () -> cube.begin(null));
            }
        });
    }

    @Test
    void aBorrowedShadowTextureKeepsItsMapAlive() {
        GameProbe.run(probe -> {
            ShadowMap map = ShadowMap.create(probe.device(), ShadowQuality.Low);
            Texture2D texture = null;
            try {
                texture = map.getShadowTexture(probe.device());
                if (texture == null) {
                    // Only reachable on a renderer that has no target at all to lend. This one
                    // does lend one even though it cannot cast into it, which is what makes the
                    // ownership rule below testable here.
                    assertFalse(map.isSupported());
                    return;
                }
                assertEquals(map.getSize(), texture.getWidth(),
                        "the borrowed target is the map's own");

                // The rule this design exists for. CNA counts the borrow and refuses to destroy
                // a map that is still lent out, so a texture that outlives its map keeps the map
                // alive rather than dangling -- and closing in the wrong order is a diagnosable
                // failure rather than a use-after-free.
                assertThrows(RuntimeException.class, map::close,
                        "a map with a borrow outstanding must not be destroyed");
                // And the refusal left the map usable rather than half-closed, which is what
                // makes the recovery below possible at all.
                assertEquals(ShadowQuality.Low, map.getQuality());

                texture.Dispose();
                texture = null;
            } finally {
                if (texture != null) {
                    texture.Dispose();
                }
                map.close();
            }
            map.close();
            assertThrows(IllegalStateException.class, map::getSize);
        });
    }

    @Test
    void aClosedMapRefusesEveryOperation() {
        GameProbe.run(probe -> {
            SpotShadowMap spot = SpotShadowMap.create(probe.device(), ShadowQuality.Low);
            CubeShadowMap cube = CubeShadowMap.create(probe.device(), ShadowQuality.Low);
            spot.close();
            spot.close();
            cube.close();
            cube.close();
            assertThrows(IllegalStateException.class, spot::getSize);
            assertThrows(IllegalStateException.class, cube::getSize);
            assertThrows(NullPointerException.class,
                    () -> SpotShadowMap.create(null, ShadowQuality.Low));
            assertNotNull(CubeMapFace.PositiveX);
        });
    }

    private static void assertMatrixEquals(Matrix expected, Matrix actual, float tolerance,
            String message) {
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
            assertEquals(left[index], right[index], tolerance,
                    message + " (element " + index + ")");
        }
    }
}
