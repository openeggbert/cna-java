package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A sequence of post-process passes, run over a game's own textures.
 *
 * <p>A CNA extension, and the machinery {@link RenderPipeline} uses internally -- exposed on its
 * own because a game with a renderer of its own still wants the ping-pong: the chain owns a pool of
 * intermediate targets, runs each pass into one and reads it back for the next, and hands the last
 * result to the destination. Doing that by hand is where the target leaks and the extra copy come
 * from.
 *
 * <p><strong>Every pass is borrowed.</strong> The caller keeps each one alive for as long as it
 * is in the chain and closes it afterwards. CNA has a second route that hands a pass over
 * outright -- {@code cna_post_process_chain_add_owned_pass} -- and it is deliberately not
 * projected: it releases the pass handle without decrementing the game's count of owned children,
 * so a game that ever uses it can never be destroyed. Reproduced in pure C by
 * {@code tools/native-abi/probes/chain_owned_pass.c} and recorded as JAVA-UPSTREAM-011.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op. Closing is
 * refused while {@link #getTargetPool()} has lent its pool.
 */
public final class PostProcessChain implements AutoCloseable {

    private static final int RESULT_BUFFER_TOO_SMALL = 14;

    private final List<PostProcessPass> borrowed = new ArrayList<>();
    private final long handle;
    private boolean closed;

    private PostProcessChain(long handle) {
        this.handle = handle;
    }

    /**
     * Creates an empty chain.
     *
     * @param graphicsDevice the device its intermediate targets come from
     * @return the chain, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static PostProcessChain create(GraphicsDevice graphicsDevice) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] chain = new long[1];
        GraphicsExtension.check("PostProcessChain.create",
                NativeEngineLayerRoutes.postProcessChainCreate(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), chain));
        return new PostProcessChain(chain[0]);
    }

    /**
     * Appends a pass the caller keeps owning.
     *
     * @param pass the pass, which must outlive its place in the chain
     */
    public void addPass(PostProcessPass pass) {
        Objects.requireNonNull(pass, "pass");
        GraphicsExtension.check("PostProcessChain.addPass",
                NativeEngineLayerRoutes.postProcessChainAddPass(open(), pass.nativeHandle()));
        borrowed.add(pass);
    }

    /**
     * Returns how many passes the chain holds.
     *
     * @return the count, owned and borrowed together
     */
    public int getPassCount() {
        int[] count = new int[1];
        GraphicsExtension.check("PostProcessChain.getPassCount",
                NativeEngineLayerRoutes.postProcessChainGetPassCount(open(), count));
        return count[0];
    }

    /**
     * Removes every pass.
     *
     * <p>They are only dropped: each was the caller's before and still is.
     */
    public void clear() {
        GraphicsExtension.check("PostProcessChain.clear",
                NativeEngineLayerRoutes.postProcessChainClear(open()));
        borrowed.clear();
    }

    /**
     * Runs every pass in order, ping-ponging between pooled targets.
     *
     * @param context the frame's inputs and destination
     */
    public void apply(PostProcessContext context) {
        Objects.requireNonNull(context, "context");
        GraphicsExtension.check("PostProcessChain.apply",
                NativeEngineLayerRoutes.postProcessChainApply(open(), context.bytes(),
                        context.integral(), context.floating()));
    }

    /**
     * Releases the chain's pooled intermediate targets.
     *
     * <p>What a game calls when the window resizes: the pooled targets are the old size and
     * keeping them would cost the memory twice.
     */
    public void resetTargets() {
        GraphicsExtension.check("PostProcessChain.resetTargets",
                NativeEngineLayerRoutes.postProcessChainResetTargets(open()));
    }

    /**
     * Returns the chain's pool of intermediate targets.
     *
     * <p>A counted borrow: closing the chain is refused until the returned pool is closed, and
     * closing the pool is what gives the borrow back rather than destroying the chain's targets.
     *
     * @return the pool, which the caller closes
     */
    public RenderTargetPool getTargetPool() {
        long[] pool = new long[1];
        GraphicsExtension.check("PostProcessChain.getTargetPool",
                NativeEngineLayerRoutes.postProcessChainGetTargetPool(open(), pool));
        return RenderTargetPool.adopt(pool[0]);
    }

    /**
     * Reports whether the chain times its passes on the GPU.
     *
     * @return whether timing is on
     */
    public boolean isGpuTimingEnabled() {
        boolean[] enabled = new boolean[1];
        GraphicsExtension.check("PostProcessChain.isGpuTimingEnabled",
                NativeEngineLayerRoutes.postProcessChainIsGpuTimingEnabled(open(), enabled));
        return enabled[0];
    }

    /**
     * Turns GPU timing on or off.
     *
     * <p>Asking for it does not create a clock: on a renderer with no timer query the chain
     * reports what it can actually do, which {@link #getPassTimings()} then says.
     *
     * @param value whether to time each pass
     */
    public void setGpuTimingEnabled(boolean value) {
        GraphicsExtension.check("PostProcessChain.setGpuTimingEnabled",
                NativeEngineLayerRoutes.postProcessChainSetGpuTimingEnabled(open(), value));
    }

    /**
     * Returns how long each pass took, by name.
     *
     * @return one timing per pass the chain recorded
     */
    public List<PassTiming> getPassTimings() {
        long chain = open();
        long[] count = new long[1];
        GraphicsExtension.check("PostProcessChain.getPassTimings",
                NativeEngineLayerRoutes.postProcessChainGetPassTimingCount(chain, count));
        int passes = Math.toIntExact(count[0]);
        List<PassTiming> timings = new ArrayList<>(passes);
        for (int index = 0; index < passes; index++) {
            long[] integral = new long[1];
            double[] doubles = new double[1];
            GraphicsExtension.check("PostProcessChain.getPassTimings",
                    NativeEngineLayerRoutes.postProcessChainGetPassTiming(chain, index,
                            new byte[4], integral, doubles));
            timings.add(new PassTiming(timingName(chain, index),
                    Math.toIntExact(integral[0]), doubles[0]));
        }
        return List.copyOf(timings);
    }

    /**
     * Returns the passes in the chain, in the order they were added.
     *
     * @return the passes
     */
    public List<PostProcessPass> getPasses() {
        return List.copyOf(borrowed);
    }

    /**
     * Releases the chain.
     *
     * <p>Marked closed only after CNA agrees, so a close refused because the target pool is still
     * borrowed leaves a usable chain rather than an unusable one that also leaked.
     */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
        }
        GraphicsExtension.check("PostProcessChain.close",
                NativeEngineLayerRoutes.postProcessChainDestroy(handle));
        synchronized (this) {
            closed = true;
        }
        borrowed.clear();
    }

    private String timingName(long chain, int index) {
        long[] bytes = new long[1];
        int probe = NativeEngineLayerRoutes.postProcessChainCopyPassTimingName(chain, index,
                new byte[0], bytes);
        if (probe != RESULT_BUFFER_TOO_SMALL) {
            GraphicsExtension.check("PostProcessChain.getPassTimings", probe);
        }
        int length = Math.toIntExact(bytes[0]);
        if (length == 0) {
            return "";
        }
        byte[] destination = new byte[length];
        GraphicsExtension.check("PostProcessChain.getPassTimings",
                NativeEngineLayerRoutes.postProcessChainCopyPassTimingName(chain, index,
                        destination, bytes));
        return new String(destination, 0, Math.toIntExact(bytes[0]), StandardCharsets.UTF_8);
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This PostProcessChain is closed");
            }
        }
        return handle;
    }
}
