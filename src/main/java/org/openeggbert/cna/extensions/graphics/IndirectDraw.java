package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.PrimitiveType;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.Objects;

/**
 * Draws whose counts the GPU reads out of a buffer instead of the CPU passing them.
 *
 * <p>A CNA extension, and the other end of {@link ComputeShader}: a shader writes an
 * {@link IndirectDrawArguments} into a {@link StorageBuffer}, and the draw reads it there. The
 * CPU never learns how much was drawn, which is the point -- a cull or a compaction that had to
 * report its result back would cost a pipeline stall bigger than the work it saved.
 *
 * <p><strong>The vertex and index buffers are the ones already bound.</strong> These routes carry
 * the <em>command</em>, not the geometry, so a game sets its streams the way it would for an
 * ordinary draw and only the counts come from elsewhere.
 *
 * <p><strong>A barrier is required between the write and the draw.</strong> A dispatch that filled
 * the argument buffer is not finished when it returns; {@link MemoryBarrier#IndirectCommand} is
 * what orders the command processor's read of it against the shader's write.
 *
 * <p>Needs {@link GraphicsCapability#IndirectDraw}, which is a separate question from having
 * compute: indirect drawing arrived three API versions earlier than compute shaders did, so a
 * renderer really can have one without the other.
 */
public final class IndirectDraw {

    private IndirectDraw() {
    }

    /**
     * Reports whether a device's renderer can draw indirectly.
     *
     * @param graphicsDevice the device to ask about
     * @return whether {@link #draw} can work
     * @throws ExtensionNotSupportedException when this build has no extended graphics layer
     */
    public static boolean isSupported(GraphicsDevice graphicsDevice) {
        return RendererCapabilities.supports(graphicsDevice, GraphicsCapability.IndirectDraw);
    }

    /**
     * Draws non-indexed primitives whose arguments are in a buffer.
     *
     * @param graphicsDevice the device to draw on
     * @param primitiveType what the bound vertices describe
     * @param argumentBuffer the buffer holding an {@link IndirectDrawArguments}
     * @param argumentByteOffset where in it the structure starts
     * @throws ExtensionNotSupportedException when the renderer cannot draw indirectly
     */
    public static void draw(GraphicsDevice graphicsDevice, PrimitiveType primitiveType,
            StorageBuffer argumentBuffer, int argumentByteOffset) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        Objects.requireNonNull(primitiveType, "primitiveType");
        Objects.requireNonNull(argumentBuffer, "argumentBuffer");
        GraphicsExtension.check("IndirectDraw.draw",
                NativeEngineLayerRoutes.graphicsDeviceDrawPrimitivesIndirectExt(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice),
                        primitiveType.ordinal(), argumentBuffer.nativeHandle(),
                        argumentByteOffset));
    }

    /**
     * Draws indexed primitives whose arguments are in a buffer.
     *
     * @param graphicsDevice the device to draw on
     * @param primitiveType what the bound indices describe
     * @param argumentBuffer the buffer holding an {@link IndirectDrawIndexedArguments}
     * @param argumentByteOffset where in it the structure starts
     * @throws ExtensionNotSupportedException when the renderer cannot draw indirectly
     */
    public static void drawIndexed(GraphicsDevice graphicsDevice, PrimitiveType primitiveType,
            StorageBuffer argumentBuffer, int argumentByteOffset) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        Objects.requireNonNull(primitiveType, "primitiveType");
        Objects.requireNonNull(argumentBuffer, "argumentBuffer");
        GraphicsExtension.check("IndirectDraw.drawIndexed",
                NativeEngineLayerRoutes.graphicsDeviceDrawIndexedPrimitivesIndirectExt(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice),
                        primitiveType.ordinal(), argumentBuffer.nativeHandle(),
                        argumentByteOffset));
    }
}
