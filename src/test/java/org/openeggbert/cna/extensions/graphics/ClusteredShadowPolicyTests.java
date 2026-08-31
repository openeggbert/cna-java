package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Vector3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The shadow budget policy, against the live runtime.
 *
 * <p>VERIFIED_HEADLESS_GAME, and the arithmetic is the point: clustered lighting makes hundreds
 * of lights affordable and shadows do not scale the same way, so something has to choose. What is
 * checkable exactly is that the choice respects the budget, that it prefers the lights a camera
 * would notice, and that the counts explain the refusals.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class ClusteredShadowPolicyTests {

    private static final Matrix VIEW = Matrix.CreateLookAt(
            new Vector3(0f, 0f, 0f), new Vector3(0f, 0f, -1f), new Vector3(0f, 1f, 0f));

    private static final Matrix PROJECTION = Matrix.CreatePerspectiveFieldOfView(
            (float) (Math.PI / 2.0), 1.0f, 1.0f, 500.0f);

    @Test
    void theBudgetIsAHardLimitAndTheRefusalsAreCounted() {
        GameProbe.run(probe -> {
            try (ClusteredLightSet lights = ClusteredLightSet.create(probe.device());
                 ClusteredShadowPolicy policy =
                         ClusteredShadowPolicy.create(probe.device(), 2)) {
                assertEquals(2, policy.getBudget());

                // Six lights all asking for a shadow, at increasing distances from a camera at
                // the origin looking down -Z.
                ClusteredLight base = ClusteredLight.createDefault()
                        .withRange(50.0f).withIntensity(5.0f).withCastsShadows(true);
                for (int index = 0; index < 6; index++) {
                    lights.add(base.withPosition(new Vector3(0f, 0f, -10f * (index + 1))));
                }

                policy.select(lights, VIEW, PROJECTION, new Vector3(0f, 0f, 0f));

                int[] selected = policy.getSelected();
                assertEquals(2, selected.length,
                        "the budget is a limit, and it was " + Arrays.toString(selected));
                assertEquals(6, policy.getRequestCount(), "all six asked");
                assertEquals(4, policy.getRefusedCount(), "and four were refused");
                assertEquals(policy.getRequestCount(),
                        selected.length + policy.getRefusedCount(),
                        "every request is selected or refused");

                // The two selected are the two the policy actually chose, and asking each light
                // one at a time agrees with the list.
                for (int index = 0; index < 6; index++) {
                    final int light = index;
                    boolean inList = Arrays.stream(selected).anyMatch(chosen -> chosen == light);
                    assertEquals(inList, policy.isSelected(index),
                            "light " + index + " disagrees with the selected list");
                }

                // The selection is by score, so the chosen lights score at least as well as the
                // rejected ones -- which is what makes it a policy rather than a prefix.
                float worstChosen = Float.MAX_VALUE;
                float bestRejected = -Float.MAX_VALUE;
                for (int index = 0; index < 6; index++) {
                    float score = policy.getScore(index);
                    if (policy.isSelected(index)) {
                        worstChosen = Math.min(worstChosen, score);
                    } else {
                        bestRejected = Math.max(bestRejected, score);
                    }
                }
                assertTrue(worstChosen >= bestRejected,
                        "a rejected light outscored a chosen one: " + bestRejected
                        + " against " + worstChosen);

                // A nearer light is worth more than a distant one, which is the scoring's whole
                // job and is checkable because the lights were placed in a line.
                assertTrue(policy.getScore(0) > policy.getScore(5),
                        "ten units away must score above fifty: " + policy.getScore(0)
                        + " against " + policy.getScore(5));

                // Raising the budget takes more of them, and a budget of zero takes none. Each
                // selection is reset first, because the margin above deliberately makes a
                // second selection depend on the first.
                policy.setBudget(5);
                policy.reset();
                policy.select(lights, VIEW, PROJECTION, new Vector3(0f, 0f, 0f));
                int wider = policy.getSelected().length;
                assertTrue(wider > 2 && wider <= 5,
                        "a wider budget takes more, up to its limit: " + wider);
                // The invariant that holds whatever the scoring does: everything that asked was
                // either selected or refused, and nothing was both.
                assertEquals(policy.getRequestCount(), wider + policy.getRefusedCount(),
                        "every request is selected or refused");

                policy.setBudget(0);
                policy.reset();
                policy.select(lights, VIEW, PROJECTION, new Vector3(0f, 0f, 0f));
                assertEquals(0, policy.getSelected().length, "a budget of nothing chooses none");
                assertEquals(6, policy.getRefusedCount(), "so every request is refused");
            }
        });
    }

    @Test
    void aLightThatCastsNoShadowDoesNotAskForOne() {
        GameProbe.run(probe -> {
            try (ClusteredLightSet lights = ClusteredLightSet.create(probe.device());
                 ClusteredShadowPolicy policy =
                         ClusteredShadowPolicy.create(probe.device(), 4)) {
                ClusteredLight base = ClusteredLight.createDefault().withRange(50.0f);
                lights.add(base.withPosition(new Vector3(0f, 0f, -5f)).withCastsShadows(true));
                lights.add(base.withPosition(new Vector3(0f, 0f, -6f)).withCastsShadows(false));
                lights.add(base.withPosition(new Vector3(0f, 0f, -7f)).withCastsShadows(true));

                policy.select(lights, VIEW, PROJECTION, new Vector3(0f, 0f, 0f));
                assertEquals(2, policy.getRequestCount(),
                        "only the lights that ask for a shadow are counted");
                assertEquals(0, policy.getRefusedCount(),
                        "and a budget of four refuses none of two");
                assertFalse(policy.isSelected(1), "the light that did not ask did not get one");
            }
        });
    }

    @Test
    void hysteresisIsTheMarginThatStopsAShadowFlickering() {
        GameProbe.run(probe -> {
            try (ClusteredShadowPolicy policy =
                         ClusteredShadowPolicy.create(probe.device(), 1)) {
                // The margin is a multiplier a contender must beat, so a value at or below one
                // would let a light displace an incumbent it merely ties -- and CNA ignores
                // such a write rather than storing it. A guarded setter, not a clamp: the
                // previous margin stands.
                float standing = policy.getHysteresis();
                assertTrue(standing > 1.0f, "the default margin is above one: " + standing);
                policy.setHysteresis(0.5f);
                assertEquals(standing, policy.getHysteresis(), 1.0e-6f,
                        "a margin at or below one is ignored");
                policy.setHysteresis(1.0f);
                assertEquals(1.0f, policy.getHysteresis(), 1.0e-6f,
                        "exactly one is accepted: a contender must merely match, not beat");
                policy.setHysteresis(2.5f);
                assertEquals(2.5f, policy.getHysteresis(), 1.0e-6f,
                        "a real margin is stored as written");
                // Reset forgets the incumbent, which is what makes the next selection
                // unaffected by the margin -- and the only way to make one repeatable.
                policy.reset();
                assertEquals(0, policy.getSelected().length,
                        "a reset policy has selected nothing");
                assertEquals(0, policy.getRequestCount());
            }
        });
    }

    @Test
    void aClosedPolicyRefusesEveryOperation() {
        GameProbe.run(probe -> {
            ClusteredShadowPolicy policy = ClusteredShadowPolicy.create(probe.device(), 1);
            policy.close();
            policy.close();
            assertThrows(IllegalStateException.class, policy::getBudget);
            assertThrows(NullPointerException.class,
                    () -> ClusteredShadowPolicy.create(null, 1));
        });
    }
}
