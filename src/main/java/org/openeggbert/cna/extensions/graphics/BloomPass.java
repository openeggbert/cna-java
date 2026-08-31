package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

/**
 * The glow around things brighter than the frame can show.
 *
 * <p>A CNA extension. Bloom is the one post-process effect almost every XNA game wrote by hand:
 * threshold the bright pixels, blur them down a pyramid, add them back. The threshold is where it
 * is usually got wrong -- too low and the whole frame hazes, too high and nothing glows -- and
 * {@link #extractChannel} is CNA's own answer to what a given value contributes, so a game can
 * plot the curve rather than guess at it.
 *
 * <p>Created on a device, configured here, and run by a {@link RenderPipeline}. Creation
 * succeeds on a renderer that cannot run the pass; ask
 * {@link PostProcessPass#isSupported(GraphicsDevice)}.
 */
public final class BloomPass extends PostProcessPass {

    private BloomPass(long handle) {
        super(handle);
    }

    /**
     * Creates the pass on a device.
     *
     * @param graphicsDevice the device to compile on
     * @return the pass, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static BloomPass create(GraphicsDevice graphicsDevice) {
        return new BloomPass(createOn(graphicsDevice, "BloomPass.create",
                NativeEngineLayerRoutes::bloomPassCreate));
    }

    /**
     * Returns the pass's Intensity.
     *
     * @return the value
     */
    public float getIntensity() {
        return readFloat("BloomPass.getIntensity",
                NativeEngineLayerRoutes::bloomPassGetIntensity);
    }

    /**
     * Sets the pass's Intensity.
     *
     * <p>The value, stored as given.
     *
     * @param value the value
     */
    public void setIntensity(float value) {
        GraphicsExtension.check("BloomPass.setIntensity",
                NativeEngineLayerRoutes.bloomPassSetIntensity(handle(), value));
    }

    /**
     * Returns the pass's Iterations.
     *
     * @return the value
     */
    public int getIterations() {
        return readInt("BloomPass.getIterations",
                NativeEngineLayerRoutes::bloomPassGetIterations);
    }

    /**
     * Sets the pass's Iterations.
     *
     * <p>The value, stored as given; the pyramid clamps the count where it builds it.
     *
     * @param value the value
     */
    public void setIterations(int value) {
        GraphicsExtension.check("BloomPass.setIterations",
                NativeEngineLayerRoutes.bloomPassSetIterations(handle(), value));
    }

    /**
     * Returns the pass's Threshold.
     *
     * @return the value
     */
    public float getThreshold() {
        return readFloat("BloomPass.getThreshold",
                NativeEngineLayerRoutes::bloomPassGetThreshold);
    }

    /**
     * Sets the pass's Threshold.
     *
     * <p>The value, stored as given.
     *
     * @param value the value
     */
    public void setThreshold(float value) {
        GraphicsExtension.check("BloomPass.setThreshold",
                NativeEngineLayerRoutes.bloomPassSetThreshold(handle(), value));
    }

    /**
     * Resets the pass's own downsample targets, so the next frame rebuilds them.
     */
    public void resetTargets() {
        GraphicsExtension.check("BloomPass.resetTargets",
                NativeEngineLayerRoutes.bloomPassResetTargets(handle()));
    }

    /**
     * Returns the pyramid depth a quality preset selects.
     *
     * @param quality the preset
     * @return how many blur iterations it asks for
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static int getIterationsForQuality(RenderQuality quality) {
        GraphicsExtension.requireBackend();
        java.util.Objects.requireNonNull(quality, "quality");
        int[] iterations = new int[1];
        GraphicsExtension.check("BloomPass.getIterationsForQuality",
                NativeEngineLayerRoutes.bloomPassIterationsForQuality(quality.ordinal(),
                        iterations));
        return iterations[0];
    }

    /**
     * Returns what one channel value contributes to the bloom at a threshold.
     *
     * <p>CNA's own extraction curve, so a game can plot the threshold's effect rather than tune
     * it blind.
     *
     * @param value the channel value
     * @param threshold the bright-pass threshold
     * @return the extracted value
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static float extractChannel(float value, float threshold) {
        GraphicsExtension.requireBackend();
        float[] extracted = new float[1];
        GraphicsExtension.check("BloomPass.extractChannel",
                NativeEngineLayerRoutes.bloomPassExtractChannel(value, threshold, extracted));
        return extracted[0];
    }
}
