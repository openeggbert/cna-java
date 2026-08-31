package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Color;
import Microsoft.Xna.Framework.Graphics.BasicEffect;
import Microsoft.Xna.Framework.Graphics.BufferUsage;
import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.IndexBuffer;
import Microsoft.Xna.Framework.Graphics.IndexElementSize;
import Microsoft.Xna.Framework.Graphics.ModelMeshPart;
import Microsoft.Xna.Framework.Graphics.VertexBuffer;
import Microsoft.Xna.Framework.Graphics.VertexDeclaration;
import Microsoft.Xna.Framework.Graphics.VertexElement;
import Microsoft.Xna.Framework.Graphics.VertexElementFormat;
import Microsoft.Xna.Framework.Graphics.VertexElementUsage;
import Microsoft.Xna.Framework.Graphics.VertexPositionColor;
import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Vector3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The instanced renderer, against the live runtime.
 *
 * <p><strong>What this can and cannot say.</strong> The renderer needs a real graphics device, so
 * it runs inside a game -- VERIFIED_HEADLESS_GAME. Nothing here claims a triangle appeared: this
 * qualification has a HEADLESS renderer and no pixels. What it does check is everything the
 * renderer knows about itself, which is a lot and is exactly what a game's own logic branches on:
 * the instance count, the capacity that never shrinks, whether the last draw instanced or fell
 * back to one call per instance, and how many calls that took.
 *
 * <p>The fallback is the part worth testing hardest, because on this renderer it is the path that
 * actually runs.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class InstancedRendererTests {

    @Test
    void theDeclarationsAreCnasOwnAndNotWrittenDownHere() {
        GameProbe.run(probe -> {
            VertexDeclaration instances = InstancedRenderer.getInstanceDeclaration();
            // CNA's header states the shape: four Vector4 elements at TextureCoordinate usage
            // indices one through four, sixty-four bytes. A game building its own instance
            // buffer has to match it exactly, so the exact values are the point.
            assertEquals(64, instances.getVertexStride());
            VertexElement[] elements = instances.GetVertexElements();
            assertEquals(4, elements.length);
            for (int index = 0; index < elements.length; index++) {
                assertEquals(VertexElementFormat.Vector4,
                        elements[index].getVertexElementFormat());
                assertEquals(VertexElementUsage.TextureCoordinate,
                        elements[index].getVertexElementUsage());
                assertEquals(index + 1, elements[index].getUsageIndex(),
                        "the four rows take usage indices one through four");
                assertEquals(index * 16, elements[index].getOffset(),
                        "each row is sixteen bytes after the last");
            }

            VertexDeclaration tints = InstancedRenderer.getTintDeclaration();
            VertexElement[] tintElements = tints.GetVertexElements();
            assertEquals(1, tintElements.length);
            assertEquals(VertexElementFormat.Color,
                    tintElements[0].getVertexElementFormat());
            assertEquals(VertexElementUsage.Color, tintElements[0].getVertexElementUsage());
            assertEquals(1, tintElements[0].getUsageIndex());
        });
    }

    @Test
    void theBufferGrowsAndNeverShrinks() {
        GameProbe.run(probe -> {
            try (Geometry geometry = new Geometry(probe.device());
                 InstancedRenderer renderer = geometry.renderer()) {
                assertEquals(0, renderer.getInstanceCount(), "nothing is uploaded yet");

                renderer.setInstances(transforms(5));
                assertEquals(5, renderer.getInstanceCount());
                int grown = renderer.getInstanceCapacity();
                assertTrue(grown >= 5, "the buffer holds what was put in it, capacity " + grown);

                // The whole point of the capacity: a smaller frame does not give the memory
                // back, so a varying instance count allocates nothing after the largest one.
                renderer.setInstances(transforms(2));
                assertEquals(2, renderer.getInstanceCount());
                assertEquals(grown, renderer.getInstanceCapacity(),
                        "capacity never shrinks");

                // And a larger frame does grow it.
                renderer.setInstances(transforms(grown + 16));
                assertEquals(grown + 16, renderer.getInstanceCount());
                assertTrue(renderer.getInstanceCapacity() >= grown + 16);

                // Uploading none is how a game stops drawing without destroying the renderer.
                renderer.setInstances(List.of());
                assertEquals(0, renderer.getInstanceCount());
                assertThrows(NullPointerException.class, () -> renderer.setInstances(null));
            }
        });
    }

    @Test
    void theTintStreamIsBoundSeparatelyFromBeingUploaded() {
        GameProbe.run(probe -> {
            try (Geometry geometry = new Geometry(probe.device());
                 InstancedRenderer renderer = geometry.renderer()) {
                assertFalse(renderer.isTintsEnabled(), "the stream starts unbound");

                // CNA documents these as independent: tints can be uploaded while unbound and
                // are simply not read, which is what lets a game keep them across a toggle.
                renderer.setInstances(transforms(3));
                renderer.setInstanceTints(List.of(Color.Red, Color.Lime, Color.Blue));
                assertFalse(renderer.isTintsEnabled(), "uploading does not bind");

                renderer.setTintsEnabled(true);
                assertTrue(renderer.isTintsEnabled());
                renderer.setTintsEnabled(true);
                assertTrue(renderer.isTintsEnabled(), "setting the value it has does nothing");
                renderer.setTintsEnabled(false);
                assertFalse(renderer.isTintsEnabled());
            }
        });
    }

    @Test
    void aDrawSaysAfterwardsWhetherItInstancedOrFellBack() {
        GameProbe.run(probe -> {
            try (Geometry geometry = new Geometry(probe.device());
                 InstancedRenderer renderer = geometry.renderer();
                 BasicEffect effect = new BasicEffect(probe.device())) {
                // CNA's own default, asked rather than written down here: it is off, which
                // means a renderer that cannot instance refuses the draw until a game says it
                // would rather have the calls than nothing.
                assertFalse(renderer.isFallbackEnabled(),
                        "the fallback is off until a game asks for it");
                boolean instances = renderer.isInstancingSupported();
                renderer.setInstances(transforms(4));

                if (instances) {
                    renderer.draw(effect);
                    assertTrue(renderer.didLastDrawInstance());
                    assertEquals(1, renderer.getLastDrawCallCount(),
                            "an instanced draw is one call");
                } else {
                    // The path this renderer actually takes. With the fallback off it refuses
                    // rather than quietly costing one call per instance.
                    // CNA's header documents this refusal as INVALID_STATE and the library
                    // returns INTERNAL -- JAVA-UPSTREAM-006 -- so the projection recognises it
                    // from the renderer's own two answers rather than from a message, and the
                    // native failure is still reachable as the cause with its real result.
                    IllegalStateException refusal = assertThrows(IllegalStateException.class,
                            () -> renderer.draw(effect));
                    assertTrue(refusal.getMessage().contains("setFallbackEnabled"),
                            "the refusal names the way out of it: " + refusal.getMessage());
                    assertNotNull(refusal.getCause(), "CNA's own failure is kept as the cause");

                    renderer.setFallbackEnabled(true);
                    assertTrue(renderer.isFallbackEnabled());
                    renderer.draw(effect);

                    // Four instances become four draw calls, and the renderer says so -- which
                    // is the fact a game logs, or refuses to ship a scene over.
                    assertFalse(renderer.didLastDrawInstance());
                    assertEquals(4, renderer.getLastDrawCallCount(),
                            "the fallback is one call per instance");

                    // And the count follows the instance count rather than being a constant.
                    renderer.setInstances(transforms(7));
                    renderer.draw(effect);
                    assertEquals(7, renderer.getLastDrawCallCount());

                    renderer.setFallbackEnabled(false);
                    assertThrows(IllegalStateException.class, () -> renderer.draw(effect));
                }

                assertThrows(NullPointerException.class, () -> renderer.draw(null));
            }
        });
    }

    @Test
    void theBuffersAreRetainedAndNotOwned() {
        GameProbe.run(probe -> {
            GraphicsDevice device = probe.device();
            Geometry geometry = new Geometry(device);
            InstancedRenderer renderer = geometry.renderer();
            assertSame(geometry.vertices, renderer.getVertexBuffer());
            assertSame(geometry.indices, renderer.getIndexBuffer());

            renderer.close();
            renderer.close();
            // Closing the renderer released CNA's mesh part and the renderer over it, and left
            // the buffers alone -- they were retained, never owned, and whoever made them still
            // has to. If close had disposed them this would throw.
            assertEquals(3, geometry.vertices.getVertexCount());
            assertEquals(3, geometry.indices.getIndexCount());
            geometry.close();

            assertThrows(IllegalStateException.class, renderer::getInstanceCount);
        });
    }

    @Test
    void geometryThatCannotBeInstancedIsRefused() {
        GameProbe.run(probe -> {
            GraphicsDevice device = probe.device();
            try (Geometry geometry = new Geometry(device)) {
                // CNA documents a part that draws no primitives as invalid, and refusing it at
                // creation is better than a renderer that can never draw.
                assertThrows(IllegalArgumentException.class,
                        () -> InstancedRenderer.create(device, geometry.vertices,
                                geometry.indices, 0, 3, 0, 0));
                assertThrows(NullPointerException.class,
                        () -> InstancedRenderer.create(device, null, geometry.indices,
                                0, 3, 0, 1));
                assertThrows(NullPointerException.class,
                        () -> InstancedRenderer.create(device, (ModelMeshPart) null));
            }
        });
    }

    /** One triangle, and the renderer over it. */
    private static final class Geometry implements AutoCloseable {

        private final VertexBuffer vertices;
        private final IndexBuffer indices;

        private Geometry(GraphicsDevice device) {
            vertices = new VertexBuffer(device, VertexPositionColor.VertexDeclaration, 3,
                    BufferUsage.None);
            vertices.SetData(new VertexPositionColor[] {
                new VertexPositionColor(new Vector3(0f, 0f, 0f), Color.White),
                new VertexPositionColor(new Vector3(1f, 0f, 0f), Color.White),
                new VertexPositionColor(new Vector3(0f, 1f, 0f), Color.White)});
            indices = new IndexBuffer(device, IndexElementSize.SixteenBits, 3, BufferUsage.None);
            indices.SetData(new Short[] {0, 1, 2});
        }

        private InstancedRenderer renderer() {
            InstancedRenderer renderer = InstancedRenderer.create(
                    vertices.getGraphicsDevice(), vertices, indices, 0, 3, 0, 1);
            assertNotNull(renderer);
            return renderer;
        }

        @Override
        public void close() {
            indices.Dispose();
            vertices.Dispose();
        }
    }

    /** Distinguishable transforms, so a count is never confused with a constant. */
    private static List<Matrix> transforms(int count) {
        List<Matrix> transforms = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            transforms.add(Matrix.CreateTranslation(new Vector3(index, 0f, 0f)));
        }
        return List.copyOf(transforms);
    }

}
