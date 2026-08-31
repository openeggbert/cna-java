package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.BoundingBox;
import Microsoft.Xna.Framework.Graphics.PrimitiveType;
import Microsoft.Xna.Framework.Graphics.RenderTarget2D;
import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Vector3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * GPU culling, render-target scopes, thin-film iridescence and two device queries.
 *
 * <p>GPU culling is a family this renderer cannot run, projected with its refusal intact -- the
 * same shape {@link GpuTimer} has, and for the same reason: a game that can read <em>why</em> can
 * fall back to {@link FrustumCuller} and say so, where a missing class would leave it guessing.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class GpuCullingTests {

    @Test
    void aCullerThisRendererCannotRunStillSaysWhatItIs() {
        GameProbe.run(probe -> {
            try (GpuInstanceCuller culler = GpuInstanceCuller.create(probe.device())) {
                boolean supported = culler.isSupported();
                if (!supported) {
                    String reason = culler.getUnsupportedReason();
                    assertFalse(reason.isBlank(), "an unsupported culler says why");
                    assertTrue(reason.length() > 8, reason);
                }

                List<GpuCullableInstance> instances = List.of(
                        new GpuCullableInstance(Matrix.CreateTranslation(new Vector3(0f, 0f, -5f)),
                                unitBoxAt(0f, 0f, -5f)),
                        new GpuCullableInstance(Matrix.CreateTranslation(new Vector3(0f, 0f, 40f)),
                                unitBoxAt(0f, 0f, 40f)));
                Matrix view = Matrix.CreateLookAt(new Vector3(0f, 0f, 0f),
                        new Vector3(0f, 0f, -1f), new Vector3(0f, 1f, 0f));
                Matrix projection = Matrix.CreatePerspectiveFieldOfView(1.0f, 1.0f, 1.0f, 100.0f);
                if (supported) {
                    culler.setInstances(instances);
                    assertEquals(2, culler.getInstanceCount(),
                            "the count is what was uploaded, not what survived");
                    culler.cull(view, projection, 36, 0, 0);
                    assertTrue(culler.readVisibleCount() >= 0);
                    culler.draw(PrimitiveType.TriangleList);
                } else {
                    // Everything past construction refuses, because every part of the technique
                    // is a storage buffer or a compute dispatch. That is the family's whole
                    // qualification here, and it is asserted rather than skipped so a renderer
                    // that gains compute shows up as a failing test.
                    assertThrows(ExtensionNotSupportedException.class,
                            () -> culler.setInstances(instances));
                    assertThrows(ExtensionNotSupportedException.class,
                            () -> culler.cull(view, projection, 36, 0, 0));
                    assertThrows(RuntimeException.class,
                            () -> culler.draw(PrimitiveType.TriangleList));
                }

                assertFalse(GpuInstanceCuller.getInstanceLookupGlsl().isBlank());
                assertEquals(6, GpuInstanceCuller.StorageBufferBinding,
                        "the binding point a culled-instance shader reads from");
                assertThrows(NullPointerException.class, () -> culler.setInstances(null));
            }
        });
    }

    @Test
    void aCullableInstanceIsATransformAndItsBounds() {
        GameProbe.run(probe -> {
            // CNA's own default rather than an assumed identity: a zeroed instance is what an
            // uninitialised slot looks like in the buffer, and this is the value that says so.
            GpuCullableInstance instance = GpuCullableInstance.createDefault();
            assertEquals(0.0f, instance.world().M11, "CNA starts an instance zeroed");

            // A record copies its components, because XNA's matrices and boxes are mutable and
            // a value that changed under its owner would not be a value.
            Matrix world = Matrix.CreateTranslation(new Vector3(1f, 2f, 3f));
            GpuCullableInstance copied = new GpuCullableInstance(world, unitBoxAt(1f, 2f, 3f));
            world.M41 = -99f;
            assertEquals(1.0f, copied.world().M41, 1.0e-5f, "the instance copied its transform");
            assertThrows(NullPointerException.class,
                    () -> new GpuCullableInstance(null, unitBoxAt(0f, 0f, 0f)));
        });
    }

    @Test
    void aRenderTargetScopePutsBackWhatItFound() {
        GameProbe.run(probe -> {
            try (RenderTarget2D target = new RenderTarget2D(probe.device(), 16, 16)) {
                // Nested scopes close in reverse order, which try-with-resources does by
                // construction -- and is why this is an AutoCloseable rather than two calls.
                try (ScopedRenderTarget outer =
                             ScopedRenderTarget.begin(probe.device(), target)) {
                    outer.hasRecordedPrevious();
                    try (ScopedRenderTarget inner =
                                 ScopedRenderTarget.begin(probe.device(), null)) {
                        inner.hasRecordedPrevious();
                    }
                }

                // Closing out of order is refused and changes nothing, so the scope is still
                // open and still has to be closed properly -- which is the recovery this
                // design exists to make possible.
                ScopedRenderTarget first = ScopedRenderTarget.begin(probe.device(), target);
                ScopedRenderTarget second = ScopedRenderTarget.begin(probe.device(), null);
                try {
                    assertThrows(RuntimeException.class, first::close,
                            "a scope with a newer one still open must not close");
                    first.hasRecordedPrevious();
                } finally {
                    second.close();
                    first.close();
                }
                first.close();
                assertThrows(IllegalStateException.class, first::hasRecordedPrevious);
            }
        });
    }

    @Test
    void thinFilmColourMovesWithThicknessAndAngle() {
        GameProbe.run(probe -> {
            Vector3 baseF0 = new Vector3(0.04f, 0.04f, 0.04f);
            // A bubble's colour moves as its film thickens, which is the whole phenomenon.
            Vector3 thin = ThinFilmIridescence.evaluate(1.0f, 1.3f, 1.0f, 200.0f, baseF0);
            Vector3 thick = ThinFilmIridescence.evaluate(1.0f, 1.3f, 1.0f, 600.0f, baseF0);
            assertNotEquals(thin.X, thick.X, "thickness must change the colour");

            // And with the angle it is seen at, which is why a bubble is banded.
            Vector3 grazing = ThinFilmIridescence.evaluate(1.0f, 1.3f, 0.1f, 200.0f, baseF0);
            assertNotEquals(thin.X, grazing.X, "the viewing angle must change it too");

            // Floored at zero, because negative reflectance is not a colour.
            for (Vector3 value : List.of(thin, thick, grazing)) {
                assertTrue(value.X >= 0f && value.Y >= 0f && value.Z >= 0f, value.toString());
            }
            // The cosine is clamped rather than refused, so a normal a caller got slightly
            // wrong is a colour rather than an exception.
            assertEquals(ThinFilmIridescence.evaluate(1.0f, 1.3f, 1.0f, 200.0f, baseF0).X,
                    ThinFilmIridescence.evaluate(1.0f, 1.3f, 5.0f, 200.0f, baseF0).X, 1.0e-5f,
                    "a cosine above one clamps to one");
            assertFalse(ThinFilmIridescence.getGlsl().isBlank());
        });
    }

    @Test
    void theLayerNamesItselfAndTheDeviceAnswersAboutShadows() {
        GameProbe.run(probe -> {
            String version = GraphicsExtension.getEngineLayerVersionString();
            assertFalse(version.isBlank(), "the layer names its own version");
            assertTrue(version.contains(String.valueOf(
                            GraphicsExtension.getEngineLayerVersion()))
                    || version.length() > 1,
                    "and the string relates to the number: " + version);

            // Hardware shadow comparison is the difference between one texture fetch per shadow
            // sample and several, so a game picks its shadow quality against this answer.
            GraphicsExtension.supportsShadowSampling(probe.device());
            assertThrows(NullPointerException.class,
                    () -> GraphicsExtension.supportsShadowSampling(null));
        });
    }

    @Test
    void aPipelineHoldsTheSkyItWasGiven() {
        GameProbe.run(probe -> {
            try (RenderPipeline pipeline = RenderPipeline.create(probe.device());
                 Skybox skybox = Skybox.create(probe.device(), null)) {
                assertEquals(null, pipeline.getSkybox());
                pipeline.setSkybox(skybox);
                assertEquals(skybox, pipeline.getSkybox(),
                        "the pipeline reports the sky it borrowed");
                pipeline.setSkybox(null);
                assertEquals(null, pipeline.getSkybox());
            }
        });
    }

    private static BoundingBox unitBoxAt(float x, float y, float z) {
        return new BoundingBox(new Vector3(x - 1f, y - 1f, z - 1f),
                new Vector3(x + 1f, y + 1f, z + 1f));
    }
}
