package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.BasicEffect;
import Microsoft.Xna.Framework.Graphics.Effect;
import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.RenderTarget2D;
import Microsoft.Xna.Framework.Graphics.SkinnedEffect;
import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Vector3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The lighting parameters CNA's effects take beyond XNA's, against the live runtime.
 *
 * <p><strong>What this can and cannot say.</strong> VERIFIED_HEADLESS_GAME: an effect needs a real
 * device, and no claim here is about a rendered pixel. What is checked is what the effect knows
 * about itself -- and unusually for a setter family, most of it round-trips, so these are real
 * equalities and not existence checks.
 *
 * <p>The exception is the punctual light's two shadow textures, which CNA documents as coming back
 * invalid whatever was set. That is asserted as the behaviour it is, so a future CNA that starts
 * returning them fails here and is looked at rather than silently changing what a game sees.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class EffectLightingTests {

    @Test
    void theShadowSwitchAndItsTwoNumbersRoundTrip() {
        GameProbe.run(probe -> {
            try (BasicEffect effect = new BasicEffect(probe.device())) {
                // CNA's own defaults, asked rather than written down here.
                assertFalse(EffectLighting.isShadowsEnabled(effect),
                        "an effect does not sample shadows until it is told to");

                EffectLighting.setShadowsEnabled(effect, true);
                assertTrue(EffectLighting.isShadowsEnabled(effect));
                EffectLighting.setShadowsEnabled(effect, false);
                assertFalse(EffectLighting.isShadowsEnabled(effect));

                // Distinguishable values, so a getter that returned a constant or read the
                // neighbouring parameter fails.
                EffectLighting.setShadowDepthBias(effect, 0.0035f);
                assertEquals(0.0035f, EffectLighting.getShadowDepthBias(effect), 1.0e-7f);
                EffectLighting.setShadowFilterRadius(effect, 3);
                assertEquals(3, EffectLighting.getShadowFilterRadius(effect));

                // And the two do not alias each other: setting one leaves the other alone.
                EffectLighting.setShadowDepthBias(effect, 0.25f);
                assertEquals(3, EffectLighting.getShadowFilterRadius(effect),
                        "the bias and the radius are separate parameters");
                EffectLighting.setShadowFilterRadius(effect, 7);
                assertEquals(0.25f, EffectLighting.getShadowDepthBias(effect), 1.0e-7f);
            }
        });
    }

    @Test
    void theLightTransformRoundTripsElementForElement() {
        GameProbe.run(probe -> {
            try (BasicEffect effect = new BasicEffect(probe.device())) {
                // A transform with sixteen different elements: a marshaller that transposed the
                // matrix, or wrote the same row four times, would pass against an identity and
                // fails against this.
                Matrix light = Matrix.Multiply(
                        Matrix.CreateLookAt(new Vector3(4f, 9f, -3f),
                                new Vector3(1f, 0f, 2f), Vector3.getUp()),
                        Matrix.CreatePerspectiveFieldOfView(1.1f, 1.6f, 0.5f, 120f));
                EffectLighting.setLightViewProjection(effect, light);

                Matrix returned = EffectLighting.getLightViewProjection(effect);
                assertMatrixEquals(light, returned);

                // Not a constant: a second, different transform comes back as itself.
                Matrix other = Matrix.CreateTranslation(new Vector3(-2f, 5f, 11f));
                EffectLighting.setLightViewProjection(effect, other);
                assertMatrixEquals(other, EffectLighting.getLightViewProjection(effect));
            }
        });
    }

    @Test
    void aShadowMapIsBoundAndUnboundAndTheEffectSaysWhich() {
        GameProbe.run(probe -> {
            GraphicsDevice device = probe.device();
            try (BasicEffect effect = new BasicEffect(device);
                 RenderTarget2D map = new RenderTarget2D(device, 32, 32)) {
                assertFalse(EffectLighting.hasShadowMap(effect), "nothing is bound yet");

                EffectLighting.setShadowMap(effect, map);
                assertTrue(EffectLighting.hasShadowMap(effect));

                // Null is how a game unbinds, and CNA agrees that it did.
                EffectLighting.setShadowMap(effect, null);
                assertFalse(EffectLighting.hasShadowMap(effect));

                // Asking repeatedly is safe: each answer is a fresh name for the texture that
                // this releases, and a leak or a double release would show up here first.
                EffectLighting.setShadowMap(effect, map);
                for (int attempt = 0; attempt < 64; attempt++) {
                    assertTrue(EffectLighting.hasShadowMap(effect));
                }

                // The effect only borrows it. Unbinding first, because leaving a disposed
                // texture bound is a different question than this test is asking.
                EffectLighting.setShadowMap(effect, null);
            }
        });
    }

    @Test
    void aPunctualLightRoundTripsEveryNumberItCarries() {
        GameProbe.run(probe -> {
            try (BasicEffect effect = new BasicEffect(probe.device())) {
                PunctualLight light = new PunctualLight(PunctualLightKind.Spot,
                        new Vector3(3f, -4f, 5f), new Vector3(0f, -1f, 0f),
                        new Vector3(0.2f, 0.5f, 0.9f), 42.5f, 0.3f, 0.7f, 0.002f,
                        null, null, Matrix.CreateScale(2f));
                EffectLighting.setPunctualLight(effect, light);

                PunctualLight returned = EffectLighting.getPunctualLight(effect);
                // Every field distinct, so a structure whose leaves were laid out in the wrong
                // order -- position where direction goes, inner angle where outer goes -- fails
                // rather than passing on symmetry.
                assertEquals(PunctualLightKind.Spot, returned.getKind());
                assertVectorEquals(new Vector3(3f, -4f, 5f), returned.getPosition());
                assertVectorEquals(new Vector3(0f, -1f, 0f), returned.getDirection());
                assertVectorEquals(new Vector3(0.2f, 0.5f, 0.9f), returned.getDiffuseColor());
                assertEquals(42.5f, returned.getRange(), 1.0e-6f);
                assertEquals(0.3f, returned.getInnerAngle(), 1.0e-6f);
                assertEquals(0.7f, returned.getOuterAngle(), 1.0e-6f);
                assertEquals(0.002f, returned.getShadowDepthBias(), 1.0e-7f);
                assertMatrixEquals(Matrix.CreateScale(2f),
                        returned.getShadowViewProjection());

                // CNA's documented gap, asserted as what it is.
                assertNull(returned.getShadowCube(), "CNA returns no handle for the cube");
                assertNull(returned.getShadowMap(), "CNA returns no handle for the map");

                // A point light is a different kind and the effect keeps that too.
                EffectLighting.setPunctualLight(effect,
                        light.withKind(PunctualLightKind.Point).withRange(8f));
                PunctualLight point = EffectLighting.getPunctualLight(effect);
                assertEquals(PunctualLightKind.Point, point.getKind());
                assertEquals(8f, point.getRange(), 1.0e-6f);
            }
        });
    }

    @Test
    void theLightTexturesReachCnaEvenThoughTheyNeverComeBack() {
        GameProbe.run(probe -> {
            GraphicsDevice device = probe.device();
            try (BasicEffect effect = new BasicEffect(device);
                 RenderTarget2D map = new RenderTarget2D(device, 16, 16)) {
                PunctualLight light = PunctualLight.createDefault()
                        .withKind(PunctualLightKind.Spot)
                        .withShadowMap(map, Matrix.getIdentity());
                // The Java side does retain it, which is the whole reason this shape exists.
                assertNotNull(light.getShadowMap());

                // And CNA accepts a light carrying a real texture handle. Were the handle
                // marshalled into the wrong leaf, CNA would refuse it as a bad handle.
                EffectLighting.setPunctualLight(effect, light);
                assertEquals(PunctualLightKind.Spot,
                        EffectLighting.getPunctualLight(effect).getKind());
            }
        });
    }

    @Test
    void skinnedEffectsAreShadowReceiversToo() {
        GameProbe.run(probe -> {
            try (SkinnedEffect effect = new SkinnedEffect(probe.device())) {
                // Not a duplicate of the BasicEffect test: CNA resolves the shadow-receiver
                // contract by dynamic cast, so "which effect classes are receivers" is a real
                // question with a real answer, and a game picking a skinned effect for a
                // character needs this one to be yes.
                EffectLighting.setShadowsEnabled(effect, true);
                assertTrue(EffectLighting.isShadowsEnabled(effect));
                EffectLighting.setShadowFilterRadius(effect, 5);
                assertEquals(5, EffectLighting.getShadowFilterRadius(effect));
            }
        });
    }

    @Test
    void theCascadeStateRoundTripsAllFourTransformsAndSplits() {
        GameProbe.run(probe -> {
            try (BasicEffect effect = new BasicEffect(probe.device())) {
                ShadowCascadeState defaults = ShadowCascadeState.createDefault();
                // CNA's own defaults, and what they mean: no cascades is how cascaded shadows
                // are turned off, and the four slots still exist because the layout is fixed.
                assertEquals(0, defaults.getCount(), "the default state disables cascades");
                assertEquals(0f, defaults.getBlendBand(), 1.0e-6f);
                assertFalse(defaults.isDebugTint());

                // Four distinguishable transforms and four distinguishable splits. A structure
                // whose fixed arrays were flattened in the wrong order, or whose fourth element
                // overlapped the camera view, fails here rather than passing on symmetry.
                ShadowCascadeState state = buildState();
                EffectLighting.setShadowCascades(effect, state);

                ShadowCascadeState returned = EffectLighting.getShadowCascades(effect);
                assertEquals(3, returned.getCount());
                assertEquals(2.5f, returned.getBlendBand(), 1.0e-6f);
                assertTrue(returned.isDebugTint());
                for (int cascade = 0; cascade < ShadowCascadeState.MAX_CASCADES; cascade++) {
                    assertEquals((cascade + 1) * 10f, returned.getSplitDistance(cascade),
                            1.0e-5f, "split " + cascade);
                    assertMatrixEquals(Matrix.CreateTranslation(
                                    new Vector3(cascade, cascade * 2f, cascade * 3f)),
                            returned.getWorldToAtlas(cascade));
                }
                assertMatrixEquals(Matrix.CreateScale(7f), returned.getCameraView());

                // Not a constant: turning the tint off comes back off.
                EffectLighting.setShadowCascades(effect, state.withDebugTint(false)
                        .withBlendBand(0.75f));
                ShadowCascadeState changed = EffectLighting.getShadowCascades(effect);
                assertFalse(changed.isDebugTint());
                assertEquals(0.75f, changed.getBlendBand(), 1.0e-6f);
                assertEquals(3, changed.getCount(), "and the rest is unchanged");

                assertThrows(NullPointerException.class,
                        () -> EffectLighting.setShadowCascades(effect, null));
            }
        });
    }

    @Test
    void aCascadeStateReadsAMapRatherThanBeingCopiedByHand() {
        GameProbe.run(probe -> {
            try (CascadedShadowMap map =
                         CascadedShadowMap.create(probe.device(), ShadowQuality.Low, 3);
                 BasicEffect effect = new BasicEffect(probe.device())) {
                Matrix cameraView = Matrix.CreateLookAt(new Vector3(0f, 12f, 30f),
                        Vector3.getZero(), Vector3.getUp());
                map.setBlendBand(1.5f);
                map.setDebugTintEnabled(true);
                map.update(DirectionalLight.createDefault(), cameraView,
                        Matrix.CreatePerspectiveFieldOfView(0.9f, 1.6f, 1f, 300f));
                ShadowCascadeState state = ShadowCascadeState.of(map, cameraView);

                // Every field comes from the map's own answers, so this is a real comparison and
                // not a copy of what the test just set: a state that invented its own transforms
                // would disagree with the map that computed them.
                assertEquals(map.getCascadeCount(), state.getCount());
                assertEquals(map.getBlendBand(), state.getBlendBand(), 1.0e-6f);
                assertEquals(map.isDebugTintEnabled(), state.isDebugTint());
                for (int cascade = 0; cascade < map.getCascadeCount(); cascade++) {
                    assertMatrixEquals(map.getCascadeMatrix(cascade),
                            state.getWorldToAtlas(cascade));
                    assertEquals(map.getSplitDistance(cascade),
                            state.getSplitDistance(cascade), 1.0e-5f);
                }
                assertMatrixEquals(cameraView, state.getCameraView());

                // The splits must be an increasing sequence -- each cascade ends further away
                // than the last -- or the effect could never pick one from a depth.
                for (int cascade = 1; cascade < map.getCascadeCount(); cascade++) {
                    assertTrue(state.getSplitDistance(cascade)
                                    > state.getSplitDistance(cascade - 1),
                            "cascade " + cascade + " ends beyond cascade " + (cascade - 1));
                }

                // And it survives the round trip through the effect unchanged.
                EffectLighting.setShadowCascades(effect, state);
                ShadowCascadeState returned = EffectLighting.getShadowCascades(effect);
                for (int cascade = 0; cascade < map.getCascadeCount(); cascade++) {
                    assertMatrixEquals(state.getWorldToAtlas(cascade),
                            returned.getWorldToAtlas(cascade));
                }

                assertThrows(NullPointerException.class,
                        () -> ShadowCascadeState.of(null, cameraView));
                assertThrows(NullPointerException.class,
                        () -> ShadowCascadeState.of(map, null));
            }
        });
    }

    @Test
    void anImageBasedLightKnowsWhenItIsIncomplete() {
        GameProbe.run(probe -> {
            ImageBasedLight defaults = ImageBasedLight.createDefault();
            // CNA's defaults name no textures, and its own answer is that such a light cannot
            // be shaded with. That is the value of the question: a light missing one texture
            // does not look like a mismatch, it looks like a scene lit slightly wrong.
            assertNull(defaults.irradiance());
            assertNull(defaults.prefilteredSpecular());
            assertNull(defaults.brdfLut());
            assertFalse(defaults.isValid(), "a light with no textures is not shadeable");

            GraphicsDevice device = probe.device();
            try (Microsoft.Xna.Framework.Graphics.TextureCube irradiance =
                         new Microsoft.Xna.Framework.Graphics.TextureCube(device, 4, false,
                                 Microsoft.Xna.Framework.Graphics.SurfaceFormat.Color);
                 Microsoft.Xna.Framework.Graphics.TextureCube specular =
                         new Microsoft.Xna.Framework.Graphics.TextureCube(device, 4, true,
                                 Microsoft.Xna.Framework.Graphics.SurfaceFormat.Color);
                 Microsoft.Xna.Framework.Graphics.Texture2D lut =
                         new Microsoft.Xna.Framework.Graphics.Texture2D(device, 8, 8)) {
                // Each of the three missing in turn is still invalid -- so the answer depends on
                // all three and not just on the first one CNA happens to look at.
                assertFalse(new ImageBasedLight(null, specular, lut, 3, 1f).isValid());
                assertFalse(new ImageBasedLight(irradiance, null, lut, 3, 1f).isValid());
                assertFalse(new ImageBasedLight(irradiance, specular, null, 3, 1f).isValid());
                // Present but claiming no mip levels is the other half of CNA's rule.
                assertFalse(new ImageBasedLight(irradiance, specular, lut, 0, 1f).isValid());

                assertTrue(new ImageBasedLight(irradiance, specular, lut, 3, 1f).isValid(),
                        "all three present and at least one mip level");
            }
        });
    }

    @Test
    void anImageBasedLightIsRefusedByAnEffectThatCannotShadeWithOne() {
        GameProbe.run(probe -> {
            try (BasicEffect effect = new BasicEffect(probe.device())) {
                // CNA's header restricts this route to a PbrEffect or SkinnedPbrEffect, neither
                // of which this binding can create yet. Asserting the refusal is what keeps the
                // route honest: it says the projection reaches CNA and that CNA, not Java,
                // decides which effects take an environment.
                assertThrows(IllegalArgumentException.class,
                        () -> EffectLighting.setImageBasedLight(effect,
                                ImageBasedLight.createDefault()));
            }
        });
    }

    @Test
    void anEffectSaysWhetherItHasAnImageBasedLightBound() {
        GameProbe.run(probe -> {
            GraphicsDevice device = probe.device();
            try (PbrEffect pbr = new PbrEffect(device);
                    Microsoft.Xna.Framework.Graphics.TextureCube irradiance =
                            new Microsoft.Xna.Framework.Graphics.TextureCube(device, 4, false,
                                    Microsoft.Xna.Framework.Graphics.SurfaceFormat.Color);
                    Microsoft.Xna.Framework.Graphics.TextureCube specular =
                            new Microsoft.Xna.Framework.Graphics.TextureCube(device, 4, true,
                                    Microsoft.Xna.Framework.Graphics.SurfaceFormat.Color);
                    Microsoft.Xna.Framework.Graphics.Texture2D lut =
                            new Microsoft.Xna.Framework.Graphics.Texture2D(device, 8, 8)) {
                Effect effect = pbr.getEffect();
                assertFalse(EffectLighting.hasImageBasedLight(effect), "nothing is bound yet");

                EffectLighting.setImageBasedLight(effect,
                        new ImageBasedLight(irradiance, specular, lut, 3, 1f));
                assertTrue(EffectLighting.hasImageBasedLight(effect),
                        "the effect knows what it was given");

                // Nothing on the Java side retains what was set -- setImageBasedLight is a static
                // call over textures the caller owns -- so this question is the only way to ask,
                // which is what earns it a route of its own.
                //
                // Asking repeatedly is safe: each answer is three fresh names for the effect's
                // own textures and this gives all three back, so a leak or a double release
                // would show up here first.
                for (int attempt = 0; attempt < 64; attempt++) {
                    assertTrue(EffectLighting.hasImageBasedLight(effect));
                }
            }
        });
    }

    @Test
    void nullsAreRefusedBeforeAnythingNativeHappens() {
        GameProbe.run(probe -> {
            try (BasicEffect effect = new BasicEffect(probe.device())) {
                assertThrows(NullPointerException.class,
                        () -> EffectLighting.setShadowsEnabled(null, true));
                assertThrows(NullPointerException.class,
                        () -> EffectLighting.setShadowMap(null, null));
                assertThrows(NullPointerException.class,
                        () -> EffectLighting.getLightViewProjection(null));
                assertThrows(NullPointerException.class,
                        () -> EffectLighting.setLightViewProjection(effect, null));
                assertThrows(NullPointerException.class,
                        () -> EffectLighting.setPunctualLight(effect, null));
                assertThrows(NullPointerException.class,
                        () -> EffectLighting.setImageBasedLight(effect, null));
                assertThrows(NullPointerException.class,
                        () -> PunctualLight.createDefault().withPosition(null));
            }
        });
    }

    /** Four distinguishable transforms and four distinguishable splits. */
    private static ShadowCascadeState buildState() {
        java.util.List<Matrix> transforms = new java.util.ArrayList<>();
        float[] splits = new float[ShadowCascadeState.MAX_CASCADES];
        for (int cascade = 0; cascade < ShadowCascadeState.MAX_CASCADES; cascade++) {
            transforms.add(Matrix.CreateTranslation(
                    new Vector3(cascade, cascade * 2f, cascade * 3f)));
            splits[cascade] = (cascade + 1) * 10f;
        }
        return ShadowCascadeState.create(3, 2.5f, transforms, splits, Matrix.CreateScale(7f),
                true);
    }

    private static void assertVectorEquals(Vector3 expected, Vector3 actual) {
        assertEquals(expected.X, actual.X, 1.0e-6f, "x");
        assertEquals(expected.Y, actual.Y, 1.0e-6f, "y");
        assertEquals(expected.Z, actual.Z, 1.0e-6f, "z");
    }

    private static void assertMatrixEquals(Matrix expected, Matrix actual) {
        float[] left = EngineValues.floats(expected, "expected");
        float[] right = EngineValues.floats(actual, "actual");
        for (int index = 0; index < left.length; index++) {
            assertEquals(left[index], right[index], 1.0e-5f, "element " + index);
        }
    }
}
