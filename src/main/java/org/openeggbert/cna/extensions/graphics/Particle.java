package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.Objects;

/**
 * One particle, as both CNA's compute shader and its CPU simulation hold it.
 *
 * <p>A CNA extension. The native layout is three {@code Vector4}s whose fourth components are
 * {@code std430} padding rather than anything a shader reads, so this projection carries the
 * three-component vectors that are actually meaningful and the four numbers packed into the
 * third.
 *
 * <p>{@link #step} is the CPU simulation, exposed as a pure function: a game can advance one
 * particle and see exactly what the emitter's settings do to it, without a device, a system or a
 * frame.
 *
 * <p><strong>There is deliberately no {@code isAlive}.</strong> Comparing {@link #age()} against
 * {@link #lifetime()} looks like the answer and is not the one CNA uses: a slot is respawned by
 * the simulation rather than removed, and {@link ParticleSystem#getActiveCount()} is a different
 * quantity again -- the steady-state occupancy the emitter implies. Inventing a predicate here
 * would have made a Java-only rule look like CNA's.
 *
 * @param position where the particle is, in world space
 * @param velocity how fast it is going, in world space
 * @param age how long it has been alive, in seconds
 * @param lifetime how long it lives, in seconds
 * @param seed the seed its last spawn used
 * @param respawnCount how many times its slot has been reused
 */
public record Particle(Vector3 position, Vector3 velocity, float age, float lifetime,
        float seed, float respawnCount) {

    /** Copies both vectors, because XNA's are mutable and a record's components are not. */
    public Particle {
        position = new Vector3(Objects.requireNonNull(position, "position"));
        velocity = new Vector3(Objects.requireNonNull(velocity, "velocity"));
    }

    /**
     * Returns the particle CNA itself starts a slot at.
     *
     * @return the default particle
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static Particle createDefault() {
        GraphicsExtension.requireBackend();
        float[] leaves = new float[12];
        GraphicsExtension.check("Particle.createDefault",
                NativeEngineLayerRoutes.particleInit(leaves));
        return read(leaves, 0);
    }

    /**
     * Advances one particle by a time step, as CNA's own simulation does.
     *
     * <p>A pure function of its arguments: no device, no system, no frame. The index is the
     * particle's slot, which is what the respawn seed is derived from, so two particles at
     * different indices spawn differently from the same settings.
     *
     * @param particle the particle to advance
     * @param index the particle's slot in the system
     * @param settings the emitter's settings
     * @param elapsedSeconds how far to advance
     * @return the advanced particle
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static Particle step(Particle particle, int index, ParticleEmitterSettings settings,
            float elapsedSeconds) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(particle, "particle");
        Objects.requireNonNull(settings, "settings");
        float[] leaves = particle.leaves();
        GraphicsExtension.check("Particle.step", NativeEngineLayerRoutes.particleSystemStep(
                leaves, index, settings.floating(), elapsedSeconds));
        return read(leaves, 0);
    }

    /**
     * Returns a pseudo-random value from a seed, using CNA's own generator.
     *
     * <p>The same sequence the simulation uses, so a game reproducing a spawn on the CPU gets
     * the particle the GPU would have produced rather than a different one.
     *
     * @param seed the seed
     * @return the value
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static float random(int seed) {
        GraphicsExtension.requireBackend();
        float[] value = new float[1];
        GraphicsExtension.check("Particle.random",
                NativeEngineLayerRoutes.particleSystemRandom(seed, value));
        return value[0];
    }

    /** Reads one particle out of a packed array. */
    static Particle read(float[] packed, int base) {
        return new Particle(
                new Vector3(packed[base], packed[base + 1], packed[base + 2]),
                new Vector3(packed[base + 4], packed[base + 5], packed[base + 6]),
                packed[base + 8], packed[base + 9], packed[base + 10], packed[base + 11]);
    }

    /** The twelve floats CNA's structure declares, padding included. */
    float[] leaves() {
        return new float[] {
            position.X, position.Y, position.Z, 0.0f,
            velocity.X, velocity.Y, velocity.Z, 0.0f,
            age, lifetime, seed, respawnCount,
        };
    }
}
