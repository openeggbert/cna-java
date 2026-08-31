package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

/**
 * Darkens creases and contact points, from the depth buffer.
 *
 * <p>A CNA extension. Screen-space ambient occlusion needs the depth and normal buffers --
 * {@link RenderPipeline#setDepthNormalInputs} is where those come from -- and samples a
 * hemisphere around each pixel. {@link #getKernel()} is that hemisphere, exposed because a game
 * whose own shader samples it has to sample the same points.
 *
 * <p>Created on a device, configured here, and run by a {@link RenderPipeline}. Creation
 * succeeds on a renderer that cannot run the pass; ask
 * {@link PostProcessPass#isSupported(GraphicsDevice)}.
 */
public final class SsaoPass extends PostProcessPass {

    /** CNA's own result for a buffer that could not hold the answer. */
    private static final int RESULT_BUFFER_TOO_SMALL_LOCAL = 14;

    private SsaoPass(long handle) {
        super(handle);
    }

    /**
     * Creates the pass on a device.
     *
     * @param graphicsDevice the device to compile on
     * @return the pass, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static SsaoPass create(GraphicsDevice graphicsDevice) {
        return new SsaoPass(createOn(graphicsDevice, "SsaoPass.create",
                NativeEngineLayerRoutes::ssaoPassCreate));
    }

    /**
     * Returns the pass's HalfResolution.
     *
     * @return the value
     */
    public boolean isHalfResolution() {
        return readFlag("SsaoPass.isHalfResolution",
                NativeEngineLayerRoutes::ssaoPassGetHalfResolution);
    }

    /**
     * Sets the pass's HalfResolution.
     *
     * <p>The value, stored as given.
     *
     * @param value the value
     */
    public void setHalfResolution(boolean value) {
        GraphicsExtension.check("SsaoPass.setHalfResolution",
                NativeEngineLayerRoutes.ssaoPassSetHalfResolution(handle(), value));
    }

    /**
     * Returns the pass's Intensity.
     *
     * @return the value
     */
    public float getIntensity() {
        return readFloat("SsaoPass.getIntensity",
                NativeEngineLayerRoutes::ssaoPassGetIntensity);
    }

    /**
     * Sets the pass's Intensity.
     *
     * <p>The value, stored as given.
     *
     * @param value the value
     */
    public void setIntensity(float value) {
        GraphicsExtension.check("SsaoPass.setIntensity",
                NativeEngineLayerRoutes.ssaoPassSetIntensity(handle(), value));
    }

    /**
     * Returns the pass's Radius.
     *
     * @return the value
     */
    public float getRadius() {
        return readFloat("SsaoPass.getRadius",
                NativeEngineLayerRoutes::ssaoPassGetRadius);
    }

    /**
     * Sets the pass's Radius.
     *
     * <p>The value, stored as given.
     *
     * @param value the value
     */
    public void setRadius(float value) {
        GraphicsExtension.check("SsaoPass.setRadius",
                NativeEngineLayerRoutes.ssaoPassSetRadius(handle(), value));
    }

    /**
     * Returns the pass's SampleCount.
     *
     * @return the value
     */
    public int getSampleCount() {
        return readInt("SsaoPass.getSampleCount",
                NativeEngineLayerRoutes::ssaoPassGetSampleCount);
    }

    /**
     * Sets the pass's SampleCount.
     *
     * <p>The value, stored as given.
     *
     * @param value the value
     */
    public void setSampleCount(int value) {
        GraphicsExtension.check("SsaoPass.setSampleCount",
                NativeEngineLayerRoutes.ssaoPassSetSampleCount(handle(), value));
    }

    /**
     * Resets the pass's own targets, so the next frame rebuilds them.
     */
    public void resetTargets() {
        GraphicsExtension.check("SsaoPass.resetTargets",
                NativeEngineLayerRoutes.ssaoPassResetTargets(handle()));
    }

    /**
     * Returns the hemisphere of sample offsets the pass uses.
     *
     * @return the kernel, one offset per sample
     */
    public java.util.List<Vector3> getKernel() {
        long pass = handle();
        long[] count = new long[1];
        int probe = NativeEngineLayerRoutes.ssaoPassCopyKernel(pass, new float[0], count);
        if (probe != RESULT_BUFFER_TOO_SMALL_LOCAL) {
            GraphicsExtension.check("SsaoPass.getKernel", probe);
        }
        int samples = Math.toIntExact(count[0]);
        if (samples == 0) {
            return java.util.List.of();
        }
        float[] destination = new float[Math.multiplyExact(samples, 3)];
        GraphicsExtension.check("SsaoPass.getKernel",
                NativeEngineLayerRoutes.ssaoPassCopyKernel(pass, destination, count));
        java.util.List<Vector3> kernel = new java.util.ArrayList<>(samples);
        for (int index = 0; index < samples; index++) {
            kernel.add(new Vector3(destination[index * 3], destination[index * 3 + 1],
                    destination[index * 3 + 2]));
        }
        return java.util.List.copyOf(kernel);
    }

    /**
     * Returns the sample count a quality preset selects.
     *
     * @param quality the preset
     * @return the sample count
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static int getSampleCountForQuality(RenderQuality quality) {
        GraphicsExtension.requireBackend();
        java.util.Objects.requireNonNull(quality, "quality");
        int[] count = new int[1];
        GraphicsExtension.check("SsaoPass.getSampleCountForQuality",
                NativeEngineLayerRoutes.ssaoPassSampleCountForQuality(quality.ordinal(), count));
        return count[0];
    }

    /**
     * Returns the occlusion shader CNA runs.
     *
     * @param packed whether to return the packed-depth variant
     * @return the GLSL
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static String getOcclusionGlsl(boolean packed) {
        GraphicsExtension.requireBackend();
        return readText("SsaoPass.getOcclusionGlsl",
                (destination, bytes) -> NativeEngineLayerRoutes
                        .ssaoPassCopyOcclusionGlsl(packed, destination, bytes));
    }
}
