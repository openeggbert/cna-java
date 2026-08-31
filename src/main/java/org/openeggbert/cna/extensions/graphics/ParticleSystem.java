package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import Microsoft.Xna.Framework.Matrix;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A pool of particles, emitted, simulated and drawn.
 *
 * <p>A CNA extension. XNA has none of this: the canonical particle sample was a vertex buffer, a
 * custom effect and several hundred lines of the game's own. A system here is a fixed pool of
 * slots, an emitter's {@link ParticleEmitterSettings}, and a simulation that runs on the GPU
 * where it can and the CPU where it cannot.
 *
 * <p><strong>Which one ran is a fact, not a promise.</strong> {@link #usesCompute()} says
 * whether the simulation is on the GPU and {@link #getUnsupportedReason()} says why not, so a
 * game can log the fallback rather than pay for it silently. {@link #setSimulationOnCpu} forces
 * the CPU path, which is what makes {@link #copyParticles()} reproducible in a test.
 *
 * <p><strong>The capacity is a hard bound and the emission rate is not.</strong> A rate the pool
 * cannot sustain is accepted and then reported by {@link #isEmissionRateClamped()} -- CNA's own
 * rule, and the reason a settings screen can show "this is more than the pool can do" rather than
 * silently rounding it.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class ParticleSystem implements AutoCloseable {

    /** CNA's own result for a buffer that could not hold the answer. */
    private static final int RESULT_BUFFER_TOO_SMALL = 14;

    /** A particle is three four-component vectors, padding included. */
    private static final int PARTICLE_LEAVES = 12;

    private final long handle;
    private boolean closed;

    private ParticleSystem(long handle) {
        this.handle = handle;
    }

    /**
     * Creates a system with CNA's own default capacity.
     *
     * @param graphicsDevice the device to simulate and draw on
     * @return the system, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static ParticleSystem create(GraphicsDevice graphicsDevice) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] system = new long[1];
        GraphicsExtension.check("ParticleSystem.create",
                NativeEngineLayerRoutes.particleSystemCreate(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), system));
        return new ParticleSystem(system[0]);
    }

    /**
     * Creates a system with a chosen capacity.
     *
     * @param graphicsDevice the device to simulate and draw on
     * @param capacity how many particle slots the pool holds
     * @return the system, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static ParticleSystem create(GraphicsDevice graphicsDevice, int capacity) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] system = new long[1];
        GraphicsExtension.check("ParticleSystem.create",
                NativeEngineLayerRoutes.particleSystemCreateWithCapacity(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), capacity,
                        system));
        return new ParticleSystem(system[0]);
    }

    /**
     * Returns the GLSL a shader reads a particle with.
     *
     * @return the GLSL
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static String getParticleLookupGlsl() {
        GraphicsExtension.requireBackend();
        return text("ParticleSystem.getParticleLookupGlsl",
                NativeEngineLayerRoutes::particleSystemCopyParticleLookupGlsl);
    }

    /** @return how many particle slots the pool holds */
    public int getCapacity() {
        int[] capacity = new int[1];
        GraphicsExtension.check("ParticleSystem.getCapacity",
                NativeEngineLayerRoutes.particleSystemGetCapacity(open(), capacity));
        return capacity[0];
    }

    /**
     * Returns the emitter's settings.
     *
     * @return a copy of the settings
     */
    public ParticleEmitterSettings getSettings() {
        ParticleEmitterSettings settings = new ParticleEmitterSettings();
        float[] floating = new float[26];
        GraphicsExtension.check("ParticleSystem.getSettings",
                NativeEngineLayerRoutes.particleSystemGetSettings(open(), floating));
        settings.read(floating);
        return settings;
    }

    /**
     * Gives the emitter new settings, exactly as written.
     *
     * @param settings the settings to emit with
     */
    public void setSettings(ParticleEmitterSettings settings) {
        Objects.requireNonNull(settings, "settings");
        GraphicsExtension.check("ParticleSystem.setSettings",
                NativeEngineLayerRoutes.particleSystemSetSettings(open(), settings.floating()));
    }

    /**
     * Starts the pool again, staggered.
     *
     * <p>Not a clear: slot <em>i</em> is put back at {@code i * lifetime / capacity}, so the
     * emitter's first second is a steady stream rather than one pulse of everything at once.
     * That is CNA's own behaviour and it is worth knowing, because a game that resets a system
     * expecting an empty pool gets a full one at staggered ages.
     */
    public void reset() {
        GraphicsExtension.check("ParticleSystem.reset",
                NativeEngineLayerRoutes.particleSystemReset(open()));
    }

    /**
     * Advances the simulation.
     *
     * @param elapsedSeconds how far to advance
     */
    public void update(float elapsedSeconds) {
        GraphicsExtension.check("ParticleSystem.update",
                NativeEngineLayerRoutes.particleSystemUpdate(open(), elapsedSeconds));
    }

    /**
     * Draws every live particle as a camera-facing quad.
     *
     * @param view the camera's view matrix
     * @param projection the camera's projection matrix
     * @param texture the texture each particle is drawn with; borrowed for the call
     */
    public void draw(Matrix view, Matrix projection, Texture2D texture) {
        Objects.requireNonNull(texture, "texture");
        GraphicsExtension.check("ParticleSystem.draw",
                NativeEngineLayerRoutes.particleSystemDraw(open(),
                        EngineValues.floats(view, "view"),
                        EngineValues.floats(projection, "projection"),
                        NativeBindings.nativeResourceHandle(texture)));
    }

    /**
     * Gives the system a depth buffer so particles fade where they meet geometry.
     *
     * <p>Without it a particle quad cuts a hard line through whatever it intersects, which is
     * the single most recognisable artefact a particle system has. The depth texture is borrowed
     * and is not retained here.
     *
     * @param depth the linear-depth texture, or {@code null} for none
     * @param farPlane the far plane the depth was rendered with
     */
    public void setDepthInput(Texture2D depth, float farPlane) {
        GraphicsExtension.check("ParticleSystem.setDepthInput",
                NativeEngineLayerRoutes.particleSystemSetDepthInputExt(open(),
                        depth == null ? 0L : NativeBindings.nativeResourceHandle(depth),
                        farPlane));
    }

    /** @return how far from geometry a particle starts fading, in world units */
    public float getSoftness() {
        float[] softness = new float[1];
        GraphicsExtension.check("ParticleSystem.getSoftness",
                NativeEngineLayerRoutes.particleSystemGetSoftnessExt(open(), softness));
        return softness[0];
    }

    /**
     * Sets how far from geometry a particle starts fading.
     *
     * @param softness the distance in world units
     */
    public void setSoftness(float softness) {
        GraphicsExtension.check("ParticleSystem.setSoftness",
                NativeEngineLayerRoutes.particleSystemSetSoftnessExt(open(), softness));
    }

    /**
     * Reports whether the simulation runs on the GPU.
     *
     * @return whether compute is being used
     */
    public boolean usesCompute() {
        boolean[] uses = new boolean[1];
        GraphicsExtension.check("ParticleSystem.usesCompute",
                NativeEngineLayerRoutes.particleSystemUsesCompute(open(), uses));
        return uses[0];
    }

    /**
     * Returns why the GPU simulation is unavailable, in the renderer's own words.
     *
     * @return the reason, or an empty string when it is available
     */
    public String getUnsupportedReason() {
        long system = open();
        return text("ParticleSystem.getUnsupportedReason",
                (destination, bytes) -> NativeEngineLayerRoutes
                        .particleSystemCopyUnsupportedReason(system, destination, bytes));
    }

    /**
     * Reports whether the CPU simulation has been forced.
     *
     * @return whether the GPU path is being refused deliberately
     */
    public boolean isSimulationOnCpu() {
        boolean[] forced = new boolean[1];
        GraphicsExtension.check("ParticleSystem.isSimulationOnCpu",
                NativeEngineLayerRoutes.particleSystemIsSimulationOnCpuExt(open(), forced));
        return forced[0];
    }

    /**
     * Forces the simulation onto the CPU, or lets it use the GPU again.
     *
     * <p>The CPU path is the one a game can read particles back from, so this is what makes a
     * particle system testable and debuggable rather than a black box.
     *
     * @param forced whether to force the CPU path
     */
    public void setSimulationOnCpu(boolean forced) {
        GraphicsExtension.check("ParticleSystem.setSimulationOnCpu",
                NativeEngineLayerRoutes.particleSystemSetSimulationOnCpuExt(open(), forced));
    }

    /**
     * Returns how many slots the emitter's settings actually keep in use.
     *
     * <p><strong>Not a count of living particles.</strong> It is the emission rate multiplied by
     * the lifetime, rounded, and capped at the capacity -- the steady-state occupancy the
     * settings imply, which is what the simulation dispatches over and what the draw submits.
     * That cap is where an unsustainable rate is absorbed, and
     * {@link #isEmissionRateClamped()} is how a game finds out it happened. Zero when the
     * product is not positive.
     *
     * @return the occupancy
     */
    public int getActiveCount() {
        int[] count = new int[1];
        GraphicsExtension.check("ParticleSystem.getActiveCount",
                NativeEngineLayerRoutes.particleSystemGetActiveCount(open(), count));
        return count[0];
    }

    /**
     * Reports whether the emission rate is more than the pool can sustain.
     *
     * <p>Reported rather than corrected: the settings went in as written, and this is how a game
     * finds out they asked for more particles than there are slots.
     *
     * @return whether the rate is being clamped by the capacity
     */
    public boolean isEmissionRateClamped() {
        boolean[] clamped = new boolean[1];
        GraphicsExtension.check("ParticleSystem.isEmissionRateClamped",
                NativeEngineLayerRoutes.particleSystemIsEmissionRateClamped(open(), clamped));
        return clamped[0];
    }

    /**
     * Copies every particle slot out of the pool.
     *
     * <p>Every slot comes back, always {@link #getCapacity()} of them: the pool is fixed and a
     * slot is respawned rather than removed. CNA documents this route as existing for tests and
     * tools rather than for the frame -- ordinary drawing never copies particles out, because
     * the vertex shader reads the same buffer the simulation wrote.
     *
     * @return the pool, one entry per slot
     */
    public List<Particle> copyParticles() {
        long system = open();
        long[] count = new long[1];
        // A zero-capacity probe reports the count and writes nothing, so BUFFER_TOO_SMALL is the
        // expected answer to the first call rather than a failure.
        int probe = NativeEngineLayerRoutes
                .particleSystemCopyParticlesExt(system, new float[0], count);
        if (probe != RESULT_BUFFER_TOO_SMALL) {
            GraphicsExtension.check("ParticleSystem.copyParticles", probe);
        }
        int particles = Math.toIntExact(count[0]);
        if (particles == 0) {
            return List.of();
        }
        float[] destination = new float[Math.multiplyExact(particles, PARTICLE_LEAVES)];
        GraphicsExtension.check("ParticleSystem.copyParticles",
                NativeEngineLayerRoutes.particleSystemCopyParticlesExt(system, destination,
                        count));
        List<Particle> pool = new ArrayList<>(particles);
        for (int index = 0; index < particles; index++) {
            pool.add(Particle.read(destination, index * PARTICLE_LEAVES));
        }
        return List.copyOf(pool);
    }

    /** Releases the system and its buffers. Closing twice is a no-op. */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        GraphicsExtension.check("ParticleSystem.close",
                NativeEngineLayerRoutes.particleSystemDestroy(handle));
    }

    /** A copy-out of UTF-8 bytes CNA sizes first. */
    @FunctionalInterface
    private interface TextRoute {
        int call(byte[] destination, long[] bytes);
    }

    private static String text(String operation, TextRoute route) {
        long[] bytes = new long[1];
        int probe = route.call(new byte[0], bytes);
        if (probe != RESULT_BUFFER_TOO_SMALL) {
            GraphicsExtension.check(operation, probe);
        }
        int length = Math.toIntExact(bytes[0]);
        if (length == 0) {
            return "";
        }
        byte[] destination = new byte[length];
        GraphicsExtension.check(operation, route.call(destination, bytes));
        return new String(destination, 0, Math.toIntExact(bytes[0]), StandardCharsets.UTF_8);
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This ParticleSystem is closed");
            }
        }
        return handle;
    }
}
