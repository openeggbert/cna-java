package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Color;
import Microsoft.Xna.Framework.Graphics.CubeMapFace;
import Microsoft.Xna.Framework.Graphics.SurfaceFormat;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import Microsoft.Xna.Framework.Graphics.TextureCube;
import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Vector2;
import Microsoft.Xna.Framework.Vector3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Image-based lighting: the environment processor and the skybox.
 *
 * <p>The sampling arithmetic is pure and exactly checkable -- a low-discrepancy sequence, a
 * roughness-to-mip mapping and its inverse, a cube face direction and the panorama coordinate it
 * falls at. The generated textures need a device and are checked for the thing that matters
 * about them: who owns them.
 *
 * <p>The skybox carries the one <em>consumed</em> ownership transfer in the whole engine layer,
 * so it gets a test of its own.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class EnvironmentTests {

    @Test
    void roughnessAndMipAreEachOthersInverse() {
        GameProbe.run(probe -> {
            // The prefilter walks the mip levels and the shader walks back, so the two have to
            // agree or a rough surface samples the wrong blur.
            for (int mips : new int[] {4, 6, 9}) {
                for (float roughness : new float[] {0.0f, 0.25f, 0.5f, 0.75f, 1.0f}) {
                    float mip = EnvironmentProcessor.mipForRoughness(roughness, mips);
                    assertTrue(mip >= 0.0f && mip <= mips,
                            roughness + " maps outside the mip chain: " + mip);
                    assertEquals(roughness, EnvironmentProcessor.roughnessForMip(mip, mips),
                            1.0e-4f, "the round trip lost " + roughness + " at " + mips
                            + " mips");
                }
            }
            // And it is monotonic: rougher is blurrier, which is what a mip chain means.
            assertTrue(EnvironmentProcessor.mipForRoughness(0.9f, 6)
                    > EnvironmentProcessor.mipForRoughness(0.1f, 6));
        });
    }

    @Test
    void theSamplingSequenceCoversItsSquareAndFollowsRoughness() {
        GameProbe.run(probe -> {
            // A low-discrepancy sequence: every point inside the unit square, none repeated, and
            // spread rather than clustered -- which is the property that makes a few samples
            // approximate the whole hemisphere.
            float lowest = 1.0f;
            float highest = 0.0f;
            for (int index = 0; index < 16; index++) {
                Vector2 point = EnvironmentProcessor.hammersley(index, 16);
                assertTrue(point.X >= 0.0f && point.X <= 1.0f, "x left the square: " + point);
                assertTrue(point.Y >= 0.0f && point.Y <= 1.0f, "y left the square: " + point);
                lowest = Math.min(lowest, point.Y);
                highest = Math.max(highest, point.Y);
            }
            assertTrue(highest - lowest > 0.8f,
                    "sixteen points must spread across the square, not cluster: "
                    + lowest + " to " + highest);
            assertNotEquals(EnvironmentProcessor.hammersley(3, 16),
                    EnvironmentProcessor.hammersley(4, 16));

            // Importance sampling: a smooth surface's samples hug the normal and a rough one's
            // spread out, which is the whole reason the mip levels mean roughness.
            Vector3 normal = new Vector3(0f, 1f, 0f);
            Vector2 point = EnvironmentProcessor.hammersley(7, 16);
            Vector3 smooth = EnvironmentProcessor.importanceSampleGgx(point, normal, 0.02f);
            Vector3 rough = EnvironmentProcessor.importanceSampleGgx(point, normal, 0.95f);
            assertTrue(Vector3.Dot(smooth, normal) > Vector3.Dot(rough, normal),
                    "a smooth lobe stays nearer the normal: " + smooth + " against " + rough);
            assertEquals(1.0f, smooth.Length(), 1.0e-3f, "a sampled direction is a direction");
        });
    }

    @Test
    void aCubeFaceLooksWhereItsPanoramaCoordinateSays() {
        GameProbe.run(probe -> {
            // Every face's centre looks a different way, and the six of them tile the sphere --
            // the same claim the cube shadow map's views make, from the other end of the layer.
            Vector3[] centres = new Vector3[6];
            for (CubeMapFace face : CubeMapFace.values()) {
                Vector3 direction = EnvironmentProcessor.faceDirection(face, 0.5f, 0.5f);
                assertEquals(1.0f, direction.Length(), 1.0e-3f);
                centres[face.ordinal()] = direction;
            }
            for (int first = 0; first < centres.length; first++) {
                for (int second = first + 1; second < centres.length; second++) {
                    assertTrue(Vector3.Dot(centres[first], centres[second]) < 0.5f,
                            "faces " + first + " and " + second + " look the same way");
                }
            }

            // A direction and its panorama coordinate are the two halves of the conversion, so
            // opposite directions must land opposite sides of the panorama.
            Vector2 forward = EnvironmentProcessor.directionToEquirectangular(
                    new Vector3(0f, 0f, -1f));
            Vector2 backward = EnvironmentProcessor.directionToEquirectangular(
                    new Vector3(0f, 0f, 1f));
            assertTrue(forward.X >= 0f && forward.X <= 1f, "u is a texture coordinate");
            assertTrue(Math.abs(forward.X - backward.X) > 0.4f,
                    "opposite directions are half a panorama apart: " + forward + " against "
                    + backward);
            // Up and down are the poles, top and bottom of the image.
            assertNotEquals(
                    EnvironmentProcessor.directionToEquirectangular(new Vector3(0f, 1f, 0f)).Y,
                    EnvironmentProcessor.directionToEquirectangular(new Vector3(0f, -1f, 0f)).Y);
        });
    }

    @Test
    void theGeneratorsRefuseOnARendererThatCannotRunThem() {
        GameProbe.run(probe -> {
            try (EnvironmentProcessor processor = EnvironmentProcessor.create(probe.device());
                 Texture2D panorama = new Texture2D(probe.device(), 8, 4, false,
                         SurfaceFormat.Color)) {
                Color[] sky = new Color[8 * 4];
                java.util.Arrays.fill(sky, Color.CornflowerBlue);
                panorama.SetData(sky);

                // The processor constructs on this renderer and then refuses to do the work,
                // which is NOT_SUPPORTED_BY_RENDERER rather than a missing layer -- and the two
                // are worth telling apart, because a build without the layer would not have got
                // this far. Asserted rather than skipped, so a renderer that gains the
                // capability shows up here.
                ExtensionNotSupportedException refusal = assertThrows(
                        ExtensionNotSupportedException.class,
                        () -> processor.convertEquirectangular(probe.device(), panorama, 8));
                assertTrue(refusal.getMessage().contains("renderer"),
                        "the refusal names both possibilities: " + refusal.getMessage());
                // Not everything refuses: the BRDF lookup is integrated rather than sampled
                // from a cube map, so it generates here -- and what it generates belongs to the
                // caller, which is the ownership rule this whole family rests on. The processor
                // can be closed and the texture stays.
                Texture2D lut = processor.generateBrdfLut(probe.device(), 16, 16);
                try {
                    assertEquals(16, lut.getWidth(), "the table is the size asked for");
                    assertEquals(16, lut.getHeight());
                } finally {
                    lut.Dispose();
                }

                // The arithmetic that needs no GPU is unaffected, which is why it is exposed
                // separately at all.
                assertEquals(0.5f, EnvironmentProcessor.roughnessForMip(
                        EnvironmentProcessor.mipForRoughness(0.5f, 6), 6), 1.0e-4f);
                assertThrows(NullPointerException.class,
                        () -> processor.convertEquirectangular(probe.device(), null, 8));
            }
        });
    }

    @Test
    void handingASkyboxAnEnvironmentTransfersOwnershipOnlyOnSuccess() {
        GameProbe.run(probe -> {
            // Borrowed: the caller keeps it, and the skybox says so.
            TextureCube borrowed = new TextureCube(probe.device(), 8, false,
                    SurfaceFormat.Color);
            try (Skybox skybox = Skybox.create(probe.device(), borrowed)) {
                assertSame(borrowed, skybox.getEnvironment());
                skybox.setEnvironment(null);
                assertNull(skybox.getEnvironment());
                skybox.setEnvironment(borrowed);
                assertSame(borrowed, skybox.getEnvironment());
            }
            // The skybox is gone and the cube map is still the caller's.
            assertEquals(8, borrowed.getSize());
            borrowed.Dispose();

            // Consumed: after a successful transfer the skybox owns it, the Java facade stops
            // being its owner, and disposing that facade does nothing rather than freeing
            // something twice. That last part is the whole point -- the frame below ends
            // cleanly either way only because the second release is a no-op.
            TextureCube given = new TextureCube(probe.device(), 8, false, SurfaceFormat.Color);
            try (Skybox skybox = Skybox.create(probe.device(), null)) {
                skybox.takeEnvironment(given);
                assertNull(skybox.getEnvironment(),
                        "a handed-over environment has no Java owner left");
                given.Dispose();
                given.Dispose();
            }

            // A transfer that never happened leaves the caller owning what it always owned.
            try (Skybox skybox = Skybox.create(probe.device(), null)) {
                TextureCube keeper = new TextureCube(probe.device(), 8, false,
                        SurfaceFormat.Color);
                try {
                    assertThrows(NullPointerException.class,
                            () -> skybox.takeEnvironment(null));
                    assertEquals(8, keeper.getSize(), "a refused transfer took nothing");
                } finally {
                    keeper.Dispose();
                }
            }
        });
    }

    @Test
    void aSkyboxTurnsAndTintsAndKnowsWhereAPixelLooks() {
        GameProbe.run(probe -> {
            try (Skybox skybox = Skybox.create(probe.device(), null)) {
                skybox.setYaw(1.5f);
                assertEquals(1.5f, skybox.getYaw(), 1.0e-5f);
                skybox.setIntensity(0.5f);
                assertEquals(0.5f, skybox.getIntensity(), 1.0e-5f);
                skybox.setTint(new Vector3(1f, 0.5f, 0.25f));
                assertEquals(new Vector3(1f, 0.5f, 0.25f), skybox.getTint());
                skybox.isSupported();
                skybox.draw(Matrix.getIdentity(), Matrix.getIdentity(), 64, 64);
            }

            // The view ray is pure and takes the yaw as an argument, so it can be asked about a
            // sky that does not exist yet. The centre of the screen looks where the camera does,
            // and turning the sky turns what the same pixel sees.
            Matrix view = Matrix.CreateLookAt(new Vector3(0f, 0f, 0f), new Vector3(0f, 0f, -1f),
                    new Vector3(0f, 1f, 0f));
            Matrix projection = Matrix.CreatePerspectiveFieldOfView(1.0f, 1.0f, 1.0f, 100.0f);
            Vector3 centre = Skybox.computeViewRay(view, projection, 0f, 0f, 0f);
            assertEquals(1.0f, centre.Length(), 1.0e-3f, "a view ray is a direction");
            assertTrue(centre.Z < -0.9f, "the centre of the screen looks forward: " + centre);
            assertNotEquals(centre.X,
                    Skybox.computeViewRay(view, projection, 0.8f, 0f, 0f).X,
                    "the edge of the screen looks somewhere else");
            assertNotEquals(centre.X,
                    Skybox.computeViewRay(view, projection, 0f, 0f, 1.5f).X,
                    "and turning the sky turns what the same pixel sees");
        });
    }
}
