package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.Objects;

/**
 * Decides which of a scene's lights get a shadow this frame.
 *
 * <p>A CNA extension. Clustered lighting makes hundreds of lights affordable and shadows do not
 * scale the same way: each one is a render pass. A policy takes a budget -- how many shadows the
 * frame can pay for -- scores every light in a {@link ClusteredLightSet} against the camera, and
 * selects the best of them.
 *
 * <p><strong>The hysteresis is the reason this is an object rather than a sort.</strong> A light
 * on the edge of the budget would otherwise gain and lose its shadow every frame as the camera
 * drifts, which reads as flicker; the margin is the multiplier a contender must beat before it
 * takes a shadow from the light that currently has one. It also makes a selection depend on the
 * one before it, so {@link #reset()} is how a game gets a repeatable answer.
 *
 * <p>{@link #getRequestCount()} and {@link #getRefusedCount()} are what a game shows when it
 * wants to explain why a particular light is unshadowed.
 *
 * <p>Like {@link ClusteredLightSet}, the parameter CNA's header calls a game is in fact a
 * graphics device; see {@code JAVA-UPSTREAM-005}.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class ClusteredShadowPolicy implements AutoCloseable {

    /** CNA's own result for a buffer that could not hold the answer. */
    private static final int RESULT_BUFFER_TOO_SMALL = 14;

    private final long handle;
    private boolean closed;

    private ClusteredShadowPolicy(long handle) {
        this.handle = handle;
    }

    /**
     * Creates a policy with a shadow budget.
     *
     * @param graphicsDevice the device the policy is parented to
     * @param budget how many lights may cast shadows at once
     * @return the policy, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static ClusteredShadowPolicy create(GraphicsDevice graphicsDevice, int budget) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] policy = new long[1];
        GraphicsExtension.check("ClusteredShadowPolicy.create",
                NativeEngineLayerRoutes.clusteredShadowPolicyCreate(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), budget,
                        policy));
        return new ClusteredShadowPolicy(policy[0]);
    }

    /** @return how many lights may cast shadows at once */
    public int getBudget() {
        int[] budget = new int[1];
        GraphicsExtension.check("ClusteredShadowPolicy.getBudget",
                NativeEngineLayerRoutes.clusteredShadowPolicyGetBudget(open(), budget));
        return budget[0];
    }

    /**
     * Sets how many lights may cast shadows at once.
     *
     * @param budget the budget
     */
    public void setBudget(int budget) {
        GraphicsExtension.check("ClusteredShadowPolicy.setBudget",
                NativeEngineLayerRoutes.clusteredShadowPolicySetBudget(open(), budget));
    }

    /**
     * Returns the multiplier a contender must beat to take a shadow from an incumbent.
     *
     * @return the margin, always above one
     */
    public float getHysteresis() {
        float[] hysteresis = new float[1];
        GraphicsExtension.check("ClusteredShadowPolicy.getHysteresis",
                NativeEngineLayerRoutes.clusteredShadowPolicyGetHysteresis(open(), hysteresis));
        return hysteresis[0];
    }

    /**
     * Sets the multiplier a contender must beat to take a shadow from an incumbent.
     *
     * <p><strong>Ignored below one</strong>, and the previous margin stands -- a guarded setter
     * rather than a clamp. A margin under one would let a light displace an incumbent it scores
     * <em>worse</em> than, which is the swap this number exists to prevent; exactly one is
     * accepted and means a contender need only match.
     *
     * @param hysteresis the margin, which must be at least one to take effect
     */
    public void setHysteresis(float hysteresis) {
        GraphicsExtension.check("ClusteredShadowPolicy.setHysteresis",
                NativeEngineLayerRoutes.clusteredShadowPolicySetHysteresis(open(), hysteresis));
    }

    /**
     * Scores a light set and selects which of its lights may cast shadows.
     *
     * @param lights the light set to score
     * @param view the camera's view matrix
     * @param projection the camera's projection matrix
     * @param cameraPosition the camera's world-space position
     */
    public void select(ClusteredLightSet lights, Matrix view, Matrix projection,
            Vector3 cameraPosition) {
        Objects.requireNonNull(lights, "lights");
        GraphicsExtension.check("ClusteredShadowPolicy.select",
                NativeEngineLayerRoutes.clusteredShadowPolicySelect(open(), lights.handle(),
                        EngineValues.floats(view, "view"),
                        EngineValues.floats(projection, "projection"),
                        EngineValues.floats(cameraPosition, "cameraPosition")));
    }

    /**
     * Returns the indices of the lights that were selected.
     *
     * @return the light indices, at most {@link #getBudget()} of them
     */
    public int[] getSelected() {
        long policy = open();
        long[] count = new long[1];
        // A zero-capacity probe reports the count and writes nothing, so BUFFER_TOO_SMALL is the
        // expected answer to the first call rather than a failure.
        int probe = NativeEngineLayerRoutes
                .clusteredShadowPolicyCopySelected(policy, new int[0], count);
        if (probe != RESULT_BUFFER_TOO_SMALL) {
            GraphicsExtension.check("ClusteredShadowPolicy.getSelected", probe);
        }
        int selected = Math.toIntExact(count[0]);
        if (selected == 0) {
            return new int[0];
        }
        int[] destination = new int[selected];
        GraphicsExtension.check("ClusteredShadowPolicy.getSelected", NativeEngineLayerRoutes
                .clusteredShadowPolicyCopySelected(policy, destination, count));
        return destination;
    }

    /**
     * Reports whether one light was selected.
     *
     * @param lightIndex the light's index in the set
     * @return whether it may cast a shadow this frame
     */
    public boolean isSelected(int lightIndex) {
        boolean[] selected = new boolean[1];
        GraphicsExtension.check("ClusteredShadowPolicy.isSelected",
                NativeEngineLayerRoutes.clusteredShadowPolicyIsSelected(open(), lightIndex,
                        selected));
        return selected[0];
    }

    /**
     * Returns the score one light was given.
     *
     * <p>What the selection sorted on, exposed so a game can explain why a light lost.
     *
     * @param lightIndex the light's index in the set
     * @return the score
     */
    public float getScore(int lightIndex) {
        float[] score = new float[1];
        GraphicsExtension.check("ClusteredShadowPolicy.getScore",
                NativeEngineLayerRoutes.clusteredShadowPolicyGetScore(open(), lightIndex,
                        score));
        return score[0];
    }

    /** @return how many lights asked for a shadow */
    public int getRequestCount() {
        int[] count = new int[1];
        GraphicsExtension.check("ClusteredShadowPolicy.getRequestCount",
                NativeEngineLayerRoutes.clusteredShadowPolicyGetRequestCount(open(), count));
        return count[0];
    }

    /** @return how many were refused because the budget was full */
    public int getRefusedCount() {
        int[] count = new int[1];
        GraphicsExtension.check("ClusteredShadowPolicy.getRefusedCount",
                NativeEngineLayerRoutes.clusteredShadowPolicyGetRefusedCount(open(), count));
        return count[0];
    }

    /** Forgets every selection, so the next one is unaffected by hysteresis. */
    public void reset() {
        GraphicsExtension.check("ClusteredShadowPolicy.reset",
                NativeEngineLayerRoutes.clusteredShadowPolicyReset(open()));
    }

    /** Releases the policy. Closing twice is a no-op. */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        GraphicsExtension.check("ClusteredShadowPolicy.close",
                NativeEngineLayerRoutes.clusteredShadowPolicyDestroy(handle));
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This ClusteredShadowPolicy is closed");
            }
        }
        return handle;
    }
}
