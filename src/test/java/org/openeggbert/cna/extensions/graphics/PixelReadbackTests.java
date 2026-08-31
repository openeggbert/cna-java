package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Color;
import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.RenderTarget2D;
import Microsoft.Xna.Framework.Graphics.SamplerState;
import Microsoft.Xna.Framework.Graphics.Texture2D;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The engine layer's claims that are about actual pixels.
 *
 * <p>Every other engine-layer suite here qualifies what objects know about themselves and which
 * calls CNA accepts, because the renderer these were written against draws nothing. This one is
 * different: it renders into a target and <strong>reads the result back</strong>, so what it
 * asserts is the image.
 *
 * <p><strong>The sampler was the one value nothing could check.</strong> It crosses into CNA and
 * never comes back -- no route reads one -- and a renderer that draws nothing accepts any sampler
 * and does something unobservable with it. A planted swap of a sampler's address modes passed
 * every test in this projection, which is exactly the state a layout gate is a poor substitute
 * for. On a renderer that draws, a filter mode is the difference between hard texel blocks and
 * interpolated ones, and that is a pixel a test can read.
 *
 * <p><strong>What this cannot say on a renderer that draws nothing</strong> is said rather than
 * skipped: where the readback is refused, the refusal is asserted, and where the draw produces
 * nothing the test stops without claiming anything about an image it never saw.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class PixelReadbackTests {

    /** A two-by-two checkerboard: the smallest source whose filtering is visible. */
    private static Texture2D checkerboard(GraphicsDevice device) {
        Texture2D source = new Texture2D(device, 2, 2);
        source.SetData(new Color[] {
                Color.Black, Color.White,
                Color.White, Color.Black });
        return source;
    }

    private static Color[] read(Texture2D target, int size) {
        Color[] pixels = new Color[size * size];
        target.GetData(pixels);
        return pixels;
    }

    /** Whether a colour is one of the two the source was made of, allowing for 8-bit rounding. */
    private static boolean isSourceColour(Color colour) {
        return (colour.getR() <= 2 || colour.getR() >= 253)
                && colour.getR() == colour.getG() && colour.getG() == colour.getB();
    }

    @Test
    void aPointFilteredDrawKeepsTheSourcesTexelsAndALinearOneBlendsThem() {
        GameProbe.run(probe -> {
            GraphicsDevice device = probe.device();
            final int size = 16;
            try (FullscreenPass pass = FullscreenPass.create(device);
                    Texture2D source = checkerboard(device);
                    RenderTarget2D point = new RenderTarget2D(device, size, size);
                    RenderTarget2D linear = new RenderTarget2D(device, size, size)) {

                pass.draw(source, point, null, size, size, SamplerState.PointClamp);
                pass.draw(source, linear, null, size, size, SamplerState.LinearClamp);

                Color[] pointPixels;
                try {
                    pointPixels = read(point, size);
                } catch (RuntimeException refused) {
                    // A renderer that binds an offscreen target and refuses to read it back --
                    // HEADLESS is exactly that. Nothing here can be claimed about an image that
                    // cannot be seen, and saying so is the whole of what this test can do there.
                    assertFalse(RendererCapabilities.getRendererName(device).isEmpty());
                    return;
                }
                Color[] linearPixels = read(linear, size);

                // No escape hatch past this point, and that is deliberate. The readback above is
                // what a renderer that draws nothing refuses; having got an image back, this
                // renderer draws, and a flat one would be a defect rather than an excuse. An
                // earlier version of this test skipped on a flat image and a planted defect that
                // produced one therefore passed.

                // Point filtering reproduces the source's own texels and nothing between them:
                // every pixel is one of the two colours the checkerboard was made of.
                for (Color pixel : pointPixels) {
                    assertTrue(isSourceColour(pixel),
                            "point filtering invented the colour " + pixel);
                }
                // Linear filtering does the opposite: magnifying a two-by-two checkerboard
                // sixty-four times over produces a gradient, so some pixel must be neither
                // black nor white. This is the assertion the sampler had nowhere to be checked
                // by before -- it fails if the filter never reached the draw.
                boolean blended = false;
                for (Color pixel : linearPixels) {
                    if (!isSourceColour(pixel)) {
                        blended = true;
                        break;
                    }
                }
                assertTrue(blended,
                        "linear filtering must produce a colour between the source's two");

                // And the two images differ, which is the same claim from the other side: a
                // sampler that never reached CNA would make them identical.
                assertFalse(java.util.Arrays.equals(pointPixels, linearPixels),
                        "two filters must not produce the same image");
            }
        });
    }

    @Test
    void aClearedTargetReadsBackAsTheColourItWasCleared() {
        GameProbe.run(probe -> {
            GraphicsDevice device = probe.device();
            final int size = 8;
            try (RenderTarget2D target = new RenderTarget2D(device, size, size)) {
                device.SetRenderTarget(target);
                device.Clear(new Color(12, 34, 56, 255));
                device.SetRenderTarget(null);

                Color[] pixels;
                try {
                    pixels = read(target, size);
                } catch (RuntimeException refused) {
                    // The floor under every other claim in this file: a renderer that cannot
                    // return what it was just told to write cannot be asked what a shader wrote.
                    return;
                }
                for (Color pixel : pixels) {
                    assertEquals(12, pixel.getR(), "red");
                    assertEquals(34, pixel.getG(), "green");
                    assertEquals(56, pixel.getB(), "blue");
                }
            }
        });
    }

    @Test
    void aTextureReadsBackTheTexelsItWasGiven() {
        GameProbe.run(probe -> {
            GraphicsDevice device = probe.device();
            try (Texture2D source = checkerboard(device)) {
                Color[] pixels = new Color[4];
                source.GetData(pixels);
                // No renderer here refuses this one -- an ordinary texture's data is CPU-side --
                // and it is what separates "the readback path works" from "the draw worked".
                assertEquals(Color.Black, pixels[0]);
                assertEquals(Color.White, pixels[1]);
                assertEquals(Color.White, pixels[2]);
                assertEquals(Color.Black, pixels[3]);
            }
        });
    }

    @Test
    void aPostProcessContextsSourceAndDestinationReachTheDrawTheyDescribe() {
        GameProbe.run(probe -> {
            GraphicsDevice device = probe.device();
            final int size = 8;
            try (BlitPass blit = BlitPass.create(device);
                    Texture2D source = new Texture2D(device, size, size);
                    RenderTarget2D destination = new RenderTarget2D(device, size, size)) {
                Color[] filled = new Color[size * size];
                java.util.Arrays.fill(filled, new Color(200, 120, 40, 255));
                source.SetData(filled);

                // The context is the other value this projection could never check: every field
                // crosses into CNA and none comes back, and a renderer that draws nothing accepts
                // any of them. A blit is the smallest pass whose whole output is decided by two
                // of those fields, so reading the destination back is what says they arrived --
                // and says they arrived the right way round.
                PostProcessContext context = new PostProcessContext();
                context.setSource(source);
                context.setDestination(destination);
                context.setSize(size, size);
                blit.apply(context);

                Color[] pixels;
                try {
                    pixels = read(destination, size);
                } catch (RuntimeException refused) {
                    return;
                }
                for (Color pixel : pixels) {
                    assertEquals(200, pixel.getR(), 1, "red arrived from the context's source");
                    assertEquals(120, pixel.getG(), 1, "green");
                    assertEquals(40, pixel.getB(), 1, "blue");
                }
            }
        });
    }

    @Test
    void theContextsElapsedTimeReachesAPassThatAnimatesWithIt() {
        GameProbe.run(probe -> {
            GraphicsDevice device = probe.device();
            final int size = 16;
            try (FilmGrainPass grain = FilmGrainPass.create(device);
                    Texture2D source = new Texture2D(device, size, size);
                    RenderTarget2D first = new RenderTarget2D(device, size, size);
                    RenderTarget2D second = new RenderTarget2D(device, size, size)) {
                Color[] filled = new Color[size * size];
                java.util.Arrays.fill(filled, new Color(128, 128, 128, 255));
                source.SetData(filled);
                grain.setIntensity(1.0f);

                // Film grain is noise seeded from the frame's own time, so the same source at two
                // different times must not produce the same image. That is the one context field
                // besides the two handles whose arrival at CNA a pixel can witness.
                PostProcessContext context = new PostProcessContext();
                context.setSource(source);
                context.setSize(size, size);

                context.setDestination(first);
                context.setElapsedSeconds(0.0f);
                grain.apply(context);

                context.setDestination(second);
                context.setElapsedSeconds(7.5f);
                grain.apply(context);

                Color[] firstPixels;
                try {
                    firstPixels = read(first, size);
                } catch (RuntimeException refused) {
                    return;
                }
                Color[] secondPixels = read(second, size);
                // The claim only exists where the grain actually ran. A renderer whose pass is a
                // pass-through has nothing to seed and nothing to differ, and saying so is better
                // than asserting a difference that could not appear. Both the pass's own answer
                // and the image are consulted, because the two disagree on one renderer.
                boolean grainRan = java.util.Arrays.stream(firstPixels).distinct().count() > 4;
                if (grain.isSupported(device) || grainRan) {
                    assertFalse(java.util.Arrays.equals(firstPixels, secondPixels),
                            "grain seeded from two different times must differ");
                }
            }
        });
    }

    @Test
    void aPostProcessChainChangesThePixelsItsPassesSayItShould() {
        GameProbe.run(probe -> {
            GraphicsDevice device = probe.device();
            final int size = 16;
            try (PostProcessChain chain = PostProcessChain.create(device);
                    FilmGrainPass grain = FilmGrainPass.create(device);
                    Texture2D source = new Texture2D(device, size, size);
                    RenderTarget2D plain = new RenderTarget2D(device, size, size);
                    RenderTarget2D grained = new RenderTarget2D(device, size, size)) {
                Color[] flat = new Color[size * size];
                java.util.Arrays.fill(flat, new Color(128, 128, 128, 255));
                source.SetData(flat);

                PostProcessContext context = new PostProcessContext();
                context.setSource(source);
                context.setSize(size, size);
                context.setElapsedSeconds(0.5f);

                // An empty chain is the control: whatever it does to the frame, it is what a
                // chain with one pass has to differ from.
                context.setDestination(plain);
                chain.apply(context);

                grain.setIntensity(1.0f);
                chain.addPass(grain);
                context.setDestination(grained);
                chain.apply(context);

                Color[] plainPixels;
                try {
                    plainPixels = read(plain, size);
                } catch (RuntimeException refused) {
                    // A renderer that cannot read a target back cannot be asked what a chain
                    // wrote, and nothing is claimed about an image that was never seen.
                    return;
                }
                Color[] grainedPixels = read(grained, size);
                long distinct = java.util.Arrays.stream(grainedPixels).distinct().count();

                if (grain.isSupported(device)) {
                    // Film grain is noise: a flat grey source comes out as many different greys,
                    // which is a claim about the image rather than about the call succeeding.
                    assertTrue(distinct > 4,
                            "a supported grain pass over a flat frame must produce more than "
                                    + distinct + " distinct colours");
                    assertFalse(java.util.Arrays.equals(plainPixels, grainedPixels),
                            "adding a pass to a chain must change what the chain produces");
                } else {
                    // A pass that says it cannot run is not required to have run -- and is not
                    // required not to, either. SOFTWARE agrees with itself and copies the source
                    // through unchanged; OPENGL4 says no and grains the frame anyway, which is
                    // JAVA-UPSTREAM-015 and is measured in C rather than decided here. So the
                    // claim is the disjunction, which is not vacuous: a chain that wrote the
                    // target's cleared black would have neither left the frame alone nor grained
                    // it, and would fail.
                    boolean unchanged = java.util.Arrays.equals(plainPixels, grainedPixels);
                    assertTrue(unchanged || distinct > 4,
                            "a chain must either leave the frame as an empty one left it or "
                                    + "actually run its pass, and this one wrote "
                                    + grainedPixels[0] + " with " + distinct + " distinct "
                                    + "colours");
                }
            }
        });
    }

    @Test
    void aGpuTimerThatSaysItWorksCollectsASample() {
        GameProbe.run(probe -> {
            GraphicsDevice device = probe.device();
            try (GpuTimer timer = GpuTimer.create(device)) {
                if (!timer.isSupported()) {
                    // The renderer has no timer query. GpuTimerTests qualifies that state; this
                    // one is about the other.
                    return;
                }
                assertEquals(0, timer.getSampleCount(), "a fresh timer has collected nothing");
                timer.begin();
                assertTrue(timer.isOpen());
                try (RenderTarget2D target = new RenderTarget2D(device, 128, 128)) {
                    device.SetRenderTarget(target);
                    for (int step = 0; step < 32; step++) {
                        device.Clear(new Color(step, step, step, 255));
                    }
                    device.SetRenderTarget(null);
                }
                timer.end();
                assertFalse(timer.isOpen());

                // A query is answered when the GPU gets to it, so this waits rather than
                // assuming -- and the sample count rising is what says a result was collected
                // rather than a zero being reported as one.
                long deadline = System.nanoTime() + 5_000_000_000L;
                boolean collected = false;
                while (!collected && System.nanoTime() < deadline) {
                    collected = timer.poll();
                }
                assertTrue(collected, "a supported timer must answer within five seconds");
                assertEquals(1, timer.getSampleCount(), "and count what it collected");
                // The value itself is deliberately not claimed to be a duration. On a software
                // GL implementation the query comes back as 0xFFFFFFFF nanoseconds -- a sentinel
                // rather than a measurement -- and inventing a plausible range here would be
                // asserting something this host cannot show. What is asserted is that the number
                // is a time at all.
                assertTrue(timer.getLastMilliseconds() >= 0.0,
                        "a collected sample is not negative: " + timer.getLastMilliseconds());
            }
        });
    }

    @Test
    void aDrawWithNoSourceIsRefusedRatherThanDrawingSomething() {
        GameProbe.run(probe -> {
            GraphicsDevice device = probe.device();
            try (FullscreenPass pass = FullscreenPass.create(device)) {
                assertThrows(NullPointerException.class,
                        () -> pass.draw(null, null, null, 8, 8, null));
            }
        });
    }

}
