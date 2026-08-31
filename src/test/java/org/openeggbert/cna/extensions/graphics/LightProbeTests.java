package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.BoundingBox;
import Microsoft.Xna.Framework.Color;
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
 * Light probes and probe volumes, against the live runtime.
 *
 * <p>VERIFIED_PURE: neither needs a graphics device, and every answer below is CNA's own
 * arithmetic. The interesting facts are checkable exactly -- a constant probe lights every normal
 * the same, a directional one does not, the reconstruction never goes negative, and a volume
 * interpolates rather than picking a nearest neighbour.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class LightProbeTests {

    @Test
    void aProbeWithOnlyItsConstantTermLightsEveryDirectionAlike() {
        try (LightProbe probe = LightProbe.create()) {
            assertTrue(probe.isZero(), "a new probe holds no light");
            assertEquals(Vector3.getZero(), probe.getIrradiance(new Vector3(0f, 1f, 0f)));

            // Coefficient zero is the DC term of a spherical-harmonic expansion: the part with no
            // direction at all. A probe holding only that must light every normal identically,
            // which is the sharpest statement available about the reconstruction being right.
            probe.setCoefficient(0, new Vector3(1f, 0.5f, 0.25f));
            assertFalse(probe.isZero());
            Vector3 up = probe.getIrradiance(new Vector3(0f, 1f, 0f));
            Vector3 down = probe.getIrradiance(new Vector3(0f, -1f, 0f));
            Vector3 side = probe.getIrradiance(new Vector3(1f, 0f, 0f));
            assertVectorEquals(up, down, 1.0e-5f, "the constant term has no direction");
            assertVectorEquals(up, side, 1.0e-5f, "nor along any other axis");
            assertTrue(up.X > 0f, "and it is light rather than nothing");
            // Its colour survives: twice as much red as green, four times as much as blue.
            assertEquals(2.0f, up.X / up.Y, 1.0e-4f);
            assertEquals(4.0f, up.X / up.Z, 1.0e-4f);

            // A directional term must then break that symmetry, or the other eight coefficients
            // are doing nothing. Which axis each of the three linear coefficients belongs to is
            // CNA's convention rather than this projection's, so the assertion is that the six
            // axis normals stop agreeing -- which holds whichever way round they are.
            probe.setCoefficient(1, new Vector3(1f, 1f, 1f));
            List<Vector3> axes = List.of(
                    new Vector3(0f, 1f, 0f), new Vector3(0f, -1f, 0f),
                    new Vector3(1f, 0f, 0f), new Vector3(-1f, 0f, 0f),
                    new Vector3(0f, 0f, 1f), new Vector3(0f, 0f, -1f));
            float first = probe.getIrradiance(axes.get(0)).X;
            assertTrue(axes.stream().anyMatch(
                            axis -> Math.abs(probe.getIrradiance(axis).X - first) > 1.0e-4f),
                    "a linear coefficient must light some direction differently");
            // And opposite normals along that axis differ, which is what "linear" means.
            assertNotEquals(probe.getIrradiance(axes.get(0)).X + probe.getIrradiance(axes.get(2)).X
                            + probe.getIrradiance(axes.get(4)).X,
                    probe.getIrradiance(axes.get(1)).X + probe.getIrradiance(axes.get(3)).X
                            + probe.getIrradiance(axes.get(5)).X,
                    "one hemisphere is brighter than the other");

            assertThrows(IllegalArgumentException.class,
                    () -> probe.setCoefficient(LightProbe.CoefficientCount, new Vector3()));
            assertThrows(IllegalArgumentException.class, () -> probe.getCoefficient(-1));
        }
    }

    @Test
    void theReconstructionNeverGoesNegative() {
        try (LightProbe probe = LightProbe.create()) {
            // Spherical harmonics ring: a strong directional term with a small constant one
            // reconstructs below zero on the far side. CNA floors it, because negative light is
            // not a look -- and this is the case that would otherwise produce black rims.
            probe.setCoefficient(0, new Vector3(0.05f, 0.05f, 0.05f));
            probe.setCoefficient(1, new Vector3(5f, 5f, 5f));
            probe.setCoefficient(2, new Vector3(5f, 5f, 5f));
            probe.setCoefficient(3, new Vector3(5f, 5f, 5f));
            for (Vector3 normal : List.of(
                    new Vector3(0f, 1f, 0f), new Vector3(0f, -1f, 0f),
                    new Vector3(1f, 0f, 0f), new Vector3(-1f, 0f, 0f),
                    new Vector3(0f, 0f, 1f), new Vector3(0f, 0f, -1f),
                    new Vector3(-0.577f, -0.577f, -0.577f))) {
                Vector3 irradiance = probe.getIrradiance(normal);
                assertTrue(irradiance.X >= 0f && irradiance.Y >= 0f && irradiance.Z >= 0f,
                        normal + " reconstructed to " + irradiance);
            }
            // The sample really does include a direction that would have gone negative, or the
            // floor was never exercised.
            assertEquals(0.0f, probe.getIrradiance(new Vector3(-0.577f, -0.577f, -0.577f)).X,
                    1.0e-5f, "the far side of a strong lobe floors at zero");
        }
    }

    @Test
    void visibilityIsOptionalAndAbsenceMeansNothingIsInTheWay() {
        try (LightProbe probe = LightProbe.create()) {
            assertFalse(probe.hasVisibility(), "a new probe knows nothing about occluders");
            // The documented safe answer, and the reason a game can ship probes without it.
            assertEquals(1.0f, probe.getVisibilityWeight(new Vector3(1f, 0f, 0f), 5.0f),
                    "an unoccluded probe passes all its light");

            probe.setVisibility(0, 2.0f, 6.0f);
            assertTrue(probe.hasVisibility());
            assertEquals(2.0f, probe.getVisibilityMean(0), 1.0e-5f);
            assertEquals(6.0f, probe.getVisibilityMeanSquared(0), 1.0e-5f);

            // Floored at zero rather than refused, which is CNA's own rule.
            probe.setVisibility(1, -3.0f, -9.0f);
            assertEquals(0.0f, probe.getVisibilityMean(1));
            assertEquals(0.0f, probe.getVisibilityMeanSquared(1));

            // A non-positive distance also means "nothing known", not "fully occluded".
            assertEquals(1.0f, probe.getVisibilityWeight(new Vector3(1f, 0f, 0f), 0.0f));

            // And a point well behind the recorded occluder gets less than all of the light,
            // which is the only thing worth asserting about the shadowing itself.
            float near = probe.getVisibilityWeight(new Vector3(1f, 0f, 0f), 0.5f);
            float far = probe.getVisibilityWeight(new Vector3(1f, 0f, 0f), 20.0f);
            assertTrue(far < near, "further past the occluder is darker: " + far + " against "
                    + near);

            assertThrows(IllegalArgumentException.class,
                    () -> probe.setVisibility(LightProbe.VisibilityDirectionCount, 1f, 1f));
        }
    }

    @Test
    void probesCopyByValueRatherThanByReference() {
        try (LightProbe source = LightProbe.createAt(new Vector3(1f, 2f, 3f));
             LightProbe copy = LightProbe.create()) {
            source.setCoefficient(0, new Vector3(1f, 1f, 1f));
            assertFalse(source.matches(copy));

            copy.copyFrom(source);
            assertTrue(source.matches(copy), "a copy holds the same value");
            assertEquals(new Vector3(1f, 2f, 3f), copy.getPosition(),
                    "including where the probe sits");

            // Changing the source must not change the copy, which is what by-value means and
            // what a shared handle would break.
            source.scale(2.0f);
            assertFalse(source.matches(copy));
            assertEquals(2.0f, source.getCoefficient(0).X, 1.0e-5f);
            assertEquals(1.0f, copy.getCoefficient(0).X, 1.0e-5f);

            assertEquals(LightProbe.CoefficientCount, copy.getCoefficients().size());
            assertEquals(copy.getCoefficient(0), copy.getCoefficients().get(0));

            // The shader source exists, because a game whose own shader evaluates a probe has to
            // evaluate it the way CNA does.
            assertFalse(LightProbe.getEvaluationGlsl().isBlank());
        }
    }

    @Test
    void aVolumeInterpolatesBetweenItsProbesRatherThanPickingOne() {
        BoundingBox room = new BoundingBox(new Vector3(0f, 0f, 0f), new Vector3(10f, 10f, 10f));
        try (LightProbeVolume volume = LightProbeVolume.create(room, 2, 2, 2);
             LightProbe corner = LightProbe.create();
             LightProbe sampled = LightProbe.create()) {
            assertEquals(2 * 2 * 2, volume.getProbeCount());
            assertEquals(2, volume.getCountX());
            assertEquals(room.Min, volume.getBounds().Min);
            assertTrue(volume.isZero(), "a new volume holds no light");

            // A two-by-two-by-two grid puts its probes on the box's corners.
            assertEquals(new Vector3(0f, 0f, 0f), volume.getProbePosition(0, 0, 0));
            assertEquals(new Vector3(10f, 10f, 10f), volume.getProbePosition(1, 1, 1));

            // Light one corner white and leave the rest dark. Halfway along that edge the
            // interpolation must be half -- a nearest-neighbour lookup would give one or zero,
            // and this is the assertion that tells the two apart.
            corner.setCoefficient(0, new Vector3(1f, 1f, 1f));
            volume.setProbe(0, 0, 0, corner);
            assertFalse(volume.isZero());

            volume.sampleInto(new Vector3(0f, 0f, 0f), sampled);
            float lit = sampled.getCoefficient(0).X;
            assertEquals(1.0f, lit, 1.0e-5f, "at the probe, the probe's own value");

            volume.sampleInto(new Vector3(5f, 0f, 0f), sampled);
            assertEquals(0.5f, sampled.getCoefficient(0).X, 1.0e-4f,
                    "halfway along one edge is half");

            volume.sampleInto(new Vector3(5f, 5f, 5f), sampled);
            assertEquals(0.125f, sampled.getCoefficient(0).X, 1.0e-4f,
                    "the centre of the box is an eighth: trilinear, in all three axes");

            // Outside the box, sampling clamps rather than refusing.
            assertFalse(volume.contains(new Vector3(-5f, 0f, 0f)));
            assertTrue(volume.contains(new Vector3(5f, 5f, 5f)));
            volume.sampleInto(new Vector3(-5f, -5f, -5f), sampled);
            assertEquals(1.0f, sampled.getCoefficient(0).X, 1.0e-5f,
                    "outside the box clamps to the nearest corner rather than refusing");

            // And the one-call form agrees with sampling then evaluating.
            volume.sampleInto(new Vector3(5f, 5f, 5f), sampled);
            assertVectorEquals(sampled.getIrradiance(new Vector3(0f, 1f, 0f)),
                    volume.getIrradiance(new Vector3(5f, 5f, 5f), new Vector3(0f, 1f, 0f)),
                    1.0e-5f, "the shortcut is the two steps");

            // Reading a probe back out is a copy: closing it does not touch the volume.
            volume.copyProbeInto(0, 0, 0, sampled);
            assertEquals(1.0f, sampled.getCoefficient(0).X, 1.0e-5f);
            sampled.scale(0.0f);
            volume.copyProbeInto(0, 0, 0, sampled);
            assertEquals(1.0f, sampled.getCoefficient(0).X, 1.0e-5f,
                    "the volume still holds what it held");

            assertThrows(IllegalArgumentException.class, () -> volume.getProbePosition(2, 0, 0));
            assertThrows(NullPointerException.class, () -> volume.setProbe(0, 0, 0, null));
        }
    }

    @Test
    void aVolumeRefusesAGridItCannotBuild() {
        BoundingBox room = new BoundingBox(new Vector3(0f, 0f, 0f), new Vector3(1f, 1f, 1f));
        // CNA gives each of these its own refusal, because a caller fixes each differently.
        assertThrows(IllegalArgumentException.class,
                () -> LightProbeVolume.create(room, 0, 1, 1));
        assertThrows(IllegalArgumentException.class,
                () -> LightProbeVolume.create(room, 64, 64, 64));
        assertThrows(IllegalArgumentException.class,
                () -> LightProbeVolume.create(
                        new BoundingBox(new Vector3(1f, 1f, 1f), new Vector3(0f, 0f, 0f)),
                        2, 2, 2));
    }

    @Test
    void theDebugRendererCanShowWhereTheProbesAre() {
        GameProbe.run(probe -> {
            BoundingBox room = new BoundingBox(new Vector3(0f, 0f, 0f), new Vector3(4f, 4f, 4f));
            try (DebugDraw debug = DebugDraw.create(probe.device());
                 LightProbeVolume small = LightProbeVolume.create(room, 2, 2, 2);
                 LightProbeVolume large = LightProbeVolume.create(room, 3, 3, 3)) {
                debug.begin(Matrix.getIdentity(), Matrix.getIdentity());
                debug.addProbeVolumeGizmo(small, Color.White, 0.25f);
                int eight = debug.getLineCount();
                assertTrue(eight > 0);

                debug.clear();
                debug.addProbeVolumeGizmo(large, Color.White, 0.25f);
                // Twenty-seven probes draw more crosses than eight, which is how the volume's
                // own size is shown to have reached CNA rather than a constant.
                assertTrue(debug.getLineCount() > eight,
                        "27 probes draw more than 8: " + eight + " then "
                        + debug.getLineCount());
                assertEquals(eight + (27 - 8) * 3, debug.getLineCount(),
                        "each extra probe is a three-segment cross");

                assertThrows(NullPointerException.class,
                        () -> debug.addProbeVolumeGizmo(null, Color.White, 1f));
            }
        });
    }

    @Test
    void aClosedProbeRefusesEveryOperation() {
        LightProbe probe = LightProbe.create();
        LightProbeVolume volume = LightProbeVolume.create(
                new BoundingBox(new Vector3(), new Vector3(1f, 1f, 1f)), 2, 2, 2);
        probe.close();
        probe.close();
        volume.close();
        volume.close();
        assertThrows(IllegalStateException.class, probe::isZero);
        assertThrows(IllegalStateException.class, volume::getProbeCount);
    }

    private static void assertVectorEquals(Vector3 expected, Vector3 actual, float tolerance,
            String message) {
        assertEquals(expected.X, actual.X, tolerance, message + " (x)");
        assertEquals(expected.Y, actual.Y, tolerance, message + " (y)");
        assertEquals(expected.Z, actual.Z, tolerance, message + " (z)");
    }
}
