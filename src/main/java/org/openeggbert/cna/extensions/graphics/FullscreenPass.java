package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.Effect;
import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.SamplerState;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.Objects;

/**
 * Draws one texture over the whole of a target, optionally through an effect.
 *
 * <p>A CNA extension, and the smallest useful thing in the engine layer: the screen-filling
 * triangle every post-process effect is drawn with. XNA's nearest equivalent is a
 * {@code SpriteBatch} draw, which is a different pipeline with its own state and its own
 * assumptions about blending -- this one sets none of that up and exists to be the bottom of a
 * game's own effect stack.
 *
 * <p>With {@link ShaderEffectFactory} for the shader this is a complete screen-space effect in two
 * objects, without a {@link PostProcessChain} or a {@link RenderPipeline} anywhere.
 *
 * <p><strong>Ownership.</strong> The pass is OWNED and released by {@link #close()}. Every texture
 * and effect a draw names is BORROWED for the length of that call and nothing is retained, because
 * a fullscreen pass has no state between draws -- which is what makes one pass enough for a whole
 * frame's worth of different draws.
 */
public final class FullscreenPass implements AutoCloseable {

    private final long handle;
    private boolean closed;

    private FullscreenPass(long handle) {
        this.handle = handle;
    }

    /**
     * Creates the pass on a device.
     *
     * @param graphicsDevice the device to draw on
     * @return the pass, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static FullscreenPass create(GraphicsDevice graphicsDevice) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] pass = new long[1];
        GraphicsExtension.check("FullscreenPass.create",
                NativeEngineLayerRoutes.fullscreenPassCreate(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), pass));
        return new FullscreenPass(pass[0]);
    }

    /**
     * Draws a source texture over a destination target.
     *
     * @param source the texture to read
     * @param destination the target to write, or {@code null} for the back buffer
     * @param effect the effect to draw through, or {@code null} for a straight copy
     * @param width the destination width in pixels; must be positive
     * @param height the destination height in pixels; must be positive
     * @param sampler how to sample the source, or {@code null} for the pass's own default
     */
    public void draw(Texture2D source, Texture2D destination, Effect effect, int width,
            int height, SamplerState sampler) {
        Objects.requireNonNull(source, "source");
        GraphicsExtension.check("FullscreenPass.draw",
                NativeEngineLayerRoutes.fullscreenPassDraw(open(),
                        NativeBindings.nativeResourceHandle(source), handleOf(destination),
                        handleOf(effect), width, height,
                        samplerIntegral(sampler), samplerFloating(sampler)));
    }

    /**
     * Draws a source texture over whatever target is already bound.
     *
     * <p>What a game uses inside a render-target scope it opened itself: the pass writes where the
     * device is already pointing rather than binding anything of its own.
     *
     * @param source the texture to read
     * @param effect the effect to draw through, or {@code null} for a straight copy
     * @param width the destination width in pixels; must be positive
     * @param height the destination height in pixels; must be positive
     * @param sampler how to sample the source, or {@code null} for the pass's own default
     */
    public void drawOverCurrentTarget(Texture2D source, Effect effect, int width, int height,
            SamplerState sampler) {
        Objects.requireNonNull(source, "source");
        GraphicsExtension.check("FullscreenPass.drawOverCurrentTarget",
                NativeEngineLayerRoutes.fullscreenPassDrawOverCurrentTarget(open(),
                        NativeBindings.nativeResourceHandle(source), handleOf(effect),
                        width, height, samplerIntegral(sampler), samplerFloating(sampler)));
    }

    /** Releases the pass. Closing twice is a no-op. */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
        }
        GraphicsExtension.check("FullscreenPass.close",
                NativeEngineLayerRoutes.fullscreenPassDestroy(handle));
        synchronized (this) {
            closed = true;
        }
    }

    /**
     * The sampler's integral leaves, or CNA's own "no sampler" when there is none.
     *
     * <p>The enum ordinals are the identities CNA declares, in the same order -- not a mapping
     * invented here, and {@code FullscreenPassTests} checks that rather than trusting it. A null
     * sampler is passed as a null pointer by the adapter, which is how CNA is told to use the
     * pass's default; an all-zero structure would mean wrap-addressed linear filtering instead,
     * which is a different thing that happens to look plausible.
     *
     * <p>The order the leaves are written in cannot be checked at runtime: this renderer accepts
     * any sampler and CNA has no route that reads one back. It is pinned against the live header
     * by the generator tool tests, which is the only place the check can honestly live.
     */
    private static long[] samplerIntegral(SamplerState sampler) {
        if (sampler == null) {
            return null;
        }
        return new long[] {
            sampler.getAddressU().ordinal(), sampler.getAddressV().ordinal(),
            sampler.getAddressW().ordinal(), sampler.getFilter().ordinal(),
            sampler.getMaxAnisotropy(), sampler.getMaxMipLevel(), 0L,
        };
    }

    private static float[] samplerFloating(SamplerState sampler) {
        return sampler == null ? null
                : new float[] {sampler.getMipMapLevelOfDetailBias()};
    }

    private static long handleOf(Object resource) {
        if (resource == null) {
            return 0L;
        }
        return NativeBindings.nativeResourceHandle(
                (Microsoft.Xna.Framework.Graphics.GraphicsResource) resource);
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This FullscreenPass is closed");
            }
        }
        return handle;
    }
}
