package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.BasicEffect;
import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.RenderTarget2D;
import Microsoft.Xna.Framework.Graphics.SamplerState;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import Microsoft.Xna.Framework.Graphics.TextureAddressMode;
import Microsoft.Xna.Framework.Graphics.TextureFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The screen-filling draw, and the ASCII pass, against the live runtime.
 *
 * <p><strong>What this file checks.</strong> Which draws CNA accepts and which it refuses, and
 * that XNA's sampler enums and CNA's identities agree ordinal for ordinal -- two independently
 * written declarations that this projection sends straight across, so a reordering on either side
 * would quietly become a different filter with nothing to say so.
 *
 * <p><strong>What used to be missing is now next door.</strong> The sampler crosses into CNA and
 * never comes back, and on a renderer that draws nothing a carrier packed in the wrong order is
 * invisible -- a planted swap of the address modes passed every test in this file. It no longer
 * does: {@link PixelReadbackTests} draws through the sampler on a renderer that renders, reads the
 * result back, and fails on exactly that swap. The layout gate in the generator tool tests is
 * still there, and is now a second line rather than the only one.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class FullscreenPassTests {

    @Test
    void xnasSamplerEnumsAreCnasIdentitiesInTheSameOrder() {
        // Not a tautology: these are two independently written declarations, and this projection
        // sends the ordinal straight across. CNA's graphics_state.h declares wrap, clamp, mirror
        // and its nine filters in exactly this order, and if either side reorders them a
        // filtered draw would quietly become a different filter with nothing to say so. The
        // renderer here cannot show that, which is why it is checked at the declaration.
        assertEquals(0, TextureAddressMode.Wrap.ordinal());
        assertEquals(1, TextureAddressMode.Clamp.ordinal());
        assertEquals(2, TextureAddressMode.Mirror.ordinal());
        assertEquals(0, TextureFilter.Linear.ordinal());
        assertEquals(1, TextureFilter.Point.ordinal());
        assertEquals(2, TextureFilter.Anisotropic.ordinal());
        assertEquals(3, TextureFilter.LinearMipPoint.ordinal());
        assertEquals(4, TextureFilter.PointMipLinear.ordinal());
        assertEquals(8, TextureFilter.MinPointMagLinearMipPoint.ordinal());
        assertEquals(9, TextureFilter.values().length, "and there are nine of them");
    }

    @Test
    void aDrawGoesToATargetOrToTheBackBuffer() {
        GameProbe.run(probe -> {
            GraphicsDevice device = probe.device();
            try (FullscreenPass pass = FullscreenPass.create(device);
                 Texture2D source = new Texture2D(device, 32, 32);
                 RenderTarget2D destination = new RenderTarget2D(device, 32, 32)) {
                pass.draw(source, destination, null, 32, 32, null);
                // Null destination is the back buffer, which is a different code path in CNA
                // rather than a convenience here.
                pass.draw(source, null, null, 32, 32, null);
                // And over whatever is bound, which is what a game inside its own render-target
                // scope uses.
                pass.drawOverCurrentTarget(source, null, 32, 32, null);
            }
        });
    }

    @Test
    void aSamplerAndAnEffectAreBothOptionalAndBothAccepted() {
        GameProbe.run(probe -> {
            GraphicsDevice device = probe.device();
            try (FullscreenPass pass = FullscreenPass.create(device);
                 Texture2D source = new Texture2D(device, 16, 16);
                 RenderTarget2D destination = new RenderTarget2D(device, 16, 16);
                 BasicEffect effect = new BasicEffect(device)) {
                // No sampler at all, which reaches CNA as a null pointer rather than a zeroed
                // structure -- the difference between "use your default" and "wrap-addressed
                // linear filtering", which is a real setting that looks like an absence.
                pass.draw(source, destination, null, 16, 16, null);

                pass.draw(source, destination, null, 16, 16, SamplerState.PointClamp);
                pass.draw(source, destination, null, 16, 16, SamplerState.AnisotropicWrap);
                pass.draw(source, destination, effect, 16, 16, SamplerState.LinearWrap);
                pass.drawOverCurrentTarget(source, effect, 16, 16, SamplerState.PointWrap);
            }
        });
    }

    @Test
    void whatCnaRefusesIsRefused() {
        GameProbe.run(probe -> {
            GraphicsDevice device = probe.device();
            try (FullscreenPass pass = FullscreenPass.create(device);
                 Texture2D source = new Texture2D(device, 8, 8);
                 RenderTarget2D destination = new RenderTarget2D(device, 8, 8)) {
                // A draw with no size covers no pixels, and CNA says so rather than drawing
                // nothing quietly.
                assertThrows(IllegalArgumentException.class,
                        () -> pass.draw(source, destination, null, 0, 0, null));
                assertThrows(IllegalArgumentException.class,
                        () -> pass.draw(source, destination, null, -4, 8, null));
                assertThrows(NullPointerException.class,
                        () -> pass.draw(null, destination, null, 8, 8, null));
                assertThrows(NullPointerException.class,
                        () -> pass.drawOverCurrentTarget(null, null, 8, 8, null));
                assertThrows(NullPointerException.class, () -> FullscreenPass.create(null));
            }
        });
    }

    @Test
    void aClosedPassIsClosedAndSaysSo() {
        GameProbe.run(probe -> {
            GraphicsDevice device = probe.device();
            try (Texture2D source = new Texture2D(device, 8, 8)) {
                FullscreenPass pass = FullscreenPass.create(device);
                pass.close();
                pass.close();
                assertThrows(IllegalStateException.class,
                        () -> pass.draw(source, null, null, 8, 8, null));
                assertThrows(IllegalStateException.class,
                        () -> pass.drawOverCurrentTarget(source, null, 8, 8, null));
            }
        });
    }

    @Test
    void theAsciiPassIsAPassLikeAnyOther() {
        GameProbe.run(probe -> {
            try (AsciiPass pass = AsciiPass.create(probe.device())) {
                // It is a PostProcessPass, so it has a name, it answers whether this renderer can
                // run it, and it goes in a chain -- which is the whole reason it is a subclass
                // rather than a type of its own.
                assertNotNull(pass.getName());
                assertEquals(false, pass.getName().isBlank(), "it names itself: " + pass.getName());
                pass.isSupported(probe.device());

                try (PostProcessChain chain = PostProcessChain.create(probe.device())) {
                    chain.addPass(pass);
                    assertEquals(1, chain.getPassCount());
                }
                assertThrows(NullPointerException.class, () -> AsciiPass.create(null));
            }
        });
    }
}
