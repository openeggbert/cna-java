package org.openeggbert.cna.extensions.graphics;

/**
 * What one frame of a {@link RenderPipeline} actually did.
 *
 * <p>A CNA extension, and the reason to expose it is that every number here is one a game
 * budgets against and cannot otherwise see: how many post-process passes ran, how many times the
 * render target changed, whether the frame went through an offscreen target at all, and roughly
 * how much GPU memory the pipeline's targets hold.
 *
 * @param passesRun how many post-process passes ran
 * @param targetSwitches how many times the render target changed
 * @param usedSceneTarget whether the frame rendered through an offscreen scene target
 * @param drewSkybox whether the skybox drew
 * @param gpuMemoryEstimateBytes estimated bytes of GPU memory the pipeline's targets hold
 */
public record RenderPipelineFrameStatistics(
        int passesRun,
        int targetSwitches,
        boolean usedSceneTarget,
        boolean drewSkybox,
        long gpuMemoryEstimateBytes) {
}
