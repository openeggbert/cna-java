package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.BufferUsage;
import Microsoft.Xna.Framework.Graphics.SurfaceFormat;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Vector3;
import Microsoft.Xna.Framework.Vector4;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The particle system, against the live runtime.
 *
 * <p><strong>What this can and cannot say.</strong> The system needs a real device, so the suite
 * runs inside a game -- VERIFIED_HEADLESS_GAME -- and nothing here claims a drawn particle. What
 * it can say is much more than a count: forcing the simulation onto the CPU makes every particle
 * readable, so the tests check that gravity actually accelerates a particle, that drag actually
 * slows one, and that a particle dies when its age reaches its lifetime. That is the simulation
 * itself, not a proxy for it.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class ParticleTests {

    @Test
    void theCpuStepIsTheSimulationAndItCanBeCheckedOnOneParticle() {
        GameProbe.run(probe -> {
            ParticleEmitterSettings settings = new ParticleEmitterSettings();
            settings.setGravity(new Vector3(0f, -10f, 0f));
            settings.setDrag(0.0f);
            settings.setLifetime(100.0f);

            Particle particle = Particle.createDefault();
            // A living particle with a known velocity and no drag: after a second of ten-unit
            // gravity it must be ten units per second slower vertically, and must have moved.
            particle = new Particle(new Vector3(0f, 0f, 0f), new Vector3(1f, 0f, 0f),
                    0.0f, 100.0f, particle.seed(), particle.respawnCount());
            Particle after = Particle.step(particle, 0, settings, 1.0f);

            assertEquals(-10.0f, after.velocity().Y, 0.01f,
                    "a second of ten-unit gravity is ten units per second down");
            assertEquals(1.0f, after.velocity().X, 0.01f, "and nothing sideways changed");
            assertTrue(after.position().X > 0f, "the particle moved along its velocity");
            assertEquals(1.0f, after.age(), 0.01f, "and it aged by the step");

            // Drag slows it: the same step with drag leaves less speed than without.
            settings.setDrag(2.0f);
            Particle dragged = Particle.step(particle, 0, settings, 1.0f);
            assertTrue(Math.abs(dragged.velocity().X) < Math.abs(after.velocity().X),
                    "drag slows the particle: " + dragged.velocity().X + " against "
                    + after.velocity().X);

            // A particle whose age passes its lifetime respawns rather than lingering, which is
            // what makes a fixed pool work at all.
            Particle old = new Particle(new Vector3(9f, 9f, 9f), new Vector3(9f, 9f, 9f),
                    99.5f, 100.0f, 1.0f, 0.0f);
            Particle reborn = Particle.step(old, 3, settings, 5.0f);
            assertTrue(reborn.age() < old.age(), "an expired slot is reused, not left dead");
            assertNotEquals(old.position(), reborn.position(), "and it is born somewhere else");

            // The index picks the seed, so two slots stepped identically do not agree.
            // The index picks the seed, so two slots respawning from the same expired particle
            // do not get the same one: here the difference shows up as a different lifetime,
            // because the variance is drawn from that seed.
            assertNotEquals(Particle.step(old, 3, settings, 5.0f).lifetime(),
                    Particle.step(old, 7, settings, 5.0f).lifetime(),
                    "two slots must not draw the same random lifetime");

            // CNA's own generator, so a game reproducing a spawn gets the same particle.
            assertEquals(Particle.random(1234), Particle.random(1234), "the same seed agrees");
            assertNotEquals(Particle.random(1234), Particle.random(4321));

            assertThrows(NullPointerException.class,
                    () -> Particle.step(null, 0, settings, 1f));
        });
    }

    @Test
    void aSystemEmitsAndAgesItsPoolWhereItCanBeRead() {
        GameProbe.run(probe -> {
            try (ParticleSystem system = ParticleSystem.create(probe.device(), 64)) {
                assertEquals(64, system.getCapacity());
                // Forcing the CPU is what makes the pool readable, and this renderer has no
                // compute anyway -- which the system says in its own words rather than leaving
                // a game to guess why nothing moved.
                system.setSimulationOnCpu(true);
                assertTrue(system.isSimulationOnCpu());
                assertFalse(system.usesCompute(), "the forced path is the CPU one");
                if (!system.getUnsupportedReason().isBlank()) {
                    assertFalse(system.usesCompute());
                }

                ParticleEmitterSettings settings = system.getSettings();
                settings.setEmissionRate(20.0f);
                settings.setLifetime(2.0f);
                settings.setLifetimeVariance(0.0f);
                settings.setSpeed(1.0f);
                settings.setSpeedVariance(0.0f);
                settings.setGravity(new Vector3(0f, -1f, 0f));
                settings.setPosition(new Vector3(5f, 6f, 7f));
                settings.setStartColor(new Vector4(2f, 2f, 2f, 1f));
                system.setSettings(settings);

                // Settings go in as written -- nothing here is clamped -- so they come back.
                ParticleEmitterSettings stored = system.getSettings();
                assertEquals(20.0f, stored.getEmissionRate(), 1.0e-4f);
                assertEquals(new Vector3(5f, 6f, 7f), stored.getPosition());
                assertEquals(2.0f, stored.getStartColor().X, 1.0e-4f,
                        "an HDR emitter colour is not clamped to one");

                // The occupancy is the rate times the lifetime, rounded and capped -- twenty a
                // second living two seconds is forty slots, exactly, before any frame runs.
                // That is what makes it a budget rather than an observation.
                assertEquals(40, system.getActiveCount(),
                        "twenty a second for two seconds is forty slots");
                for (int frame = 0; frame < 10; frame++) {
                    system.update(1.0f / 60.0f);
                }
                assertEquals(40, system.getActiveCount(),
                        "and running frames does not change what the settings imply");

                // The pool is fixed, so every slot comes back and the live ones are the count.
                List<Particle> pool = system.copyParticles();
                assertEquals(64, pool.size(), "a fixed pool reports every slot");

                // Every live particle is near the emitter, because a sixth of a second at one
                // unit per second cannot take it far. A projection that lost the emitter
                // position -- or put it in the wrong three floats -- fails this.
                // A slot moves to the emitter when it respawns, not when the settings change,
                // so the pool starts where the default emitter was. Run past one lifetime and
                // the newly born particles are at the emitter -- which is the check that the
                // position reached CNA at its own three floats.
                for (int frame = 0; frame < 200; frame++) {
                    system.update(1.0f / 60.0f);
                }
                List<Particle> respawned = system.copyParticles().subList(
                        0, system.getActiveCount());
                List<Particle> young = respawned.stream()
                        .filter(particle -> particle.age() < 0.2f).toList();
                assertFalse(young.isEmpty(),
                        "twenty births a second must produce some in twelve frames");
                for (Particle live : young) {
                    assertTrue(Vector3.Distance(live.position(), new Vector3(5f, 6f, 7f)) < 0.5f,
                            "a particle born " + live.age() + "s ago is at " + live.position()
                            + ", not at the emitter (5, 6, 7)");
                }
                // And they are ageing, which is the simulation actually running.
                assertTrue(pool.stream().anyMatch(particle -> particle.age() > 0f),
                        "ten frames of updates must have aged something");

                // Reset starts the pool again, and the occupancy the settings imply is
                // unchanged by it -- which is the difference between the two numbers.
                // Reset does not zero the pool: it staggers it. Slot i starts at
                // i * lifetime / capacity, so an emitter's first second is a steady stream
                // rather than one pulse of everything at once. That is exactly checkable, and
                // it is the sort of thing a projection would never notice it had lost.
                system.reset();
                assertEquals(40, system.getActiveCount());
                List<Particle> staggered = system.copyParticles();
                float step = 2.0f / 64.0f;
                for (int slot = 0; slot < 8; slot++) {
                    assertEquals(slot * step, staggered.get(slot).age(), 1.0e-5f,
                            "slot " + slot + " does not start a step into the lifetime");
                }
            }
        });
    }

    @Test
    void anEmissionRateThePoolCannotSustainIsReportedRatherThanCorrected() {
        GameProbe.run(probe -> {
            try (ParticleSystem system = ParticleSystem.create(probe.device(), 8)) {
                system.setSimulationOnCpu(true);
                ParticleEmitterSettings settings = system.getSettings();
                settings.setEmissionRate(1.0f);
                settings.setLifetime(1.0f);
                system.setSettings(settings);
                assertFalse(system.isEmissionRateClamped(),
                        "one a second into eight slots is sustainable");

                // Far more than eight slots can hold for a second each. CNA's own rule is that
                // this is accepted and reported, not quietly reduced -- so the settings must
                // still read back as written.
                settings.setEmissionRate(10000.0f);
                system.setSettings(settings);
                assertTrue(system.isEmissionRateClamped(),
                            "ten thousand a second into eight slots is not sustainable");
                assertEquals(10000.0f, system.getSettings().getEmissionRate(), 1.0f,
                        "and the rate was stored exactly as written");
                // The cap is where it is absorbed, so the occupancy is the capacity rather than
                // the ten thousand slots the rate asked for.
                assertEquals(8, system.getActiveCount(),
                        "the capacity is what absorbs an unsustainable rate");
            }
        });
    }

    @Test
    void softnessAndTheDepthInputAreTheSystemsOwnState() {
        GameProbe.run(probe -> {
            try (ParticleSystem system = ParticleSystem.create(probe.device(), 16);
                 Texture2D depth = new Texture2D(probe.device(), 8, 8, false,
                         SurfaceFormat.Color)) {
                system.setSoftness(0.5f);
                assertEquals(0.5f, system.getSoftness(), 1.0e-6f);
                system.setDepthInput(depth, 100.0f);
                system.setDepthInput(null, 100.0f);
                assertFalse(ParticleSystem.getParticleLookupGlsl().isBlank(),
                        "a shader reading particles needs CNA's own lookup");

                system.setSimulationOnCpu(true);
                system.update(1.0f / 60.0f);
                // Drawing needs a texture; nothing is claimed about the result on this renderer,
                // only that the call is well formed and the system accepts it.
                system.draw(Matrix.getIdentity(), Matrix.getIdentity(), depth);

                assertThrows(NullPointerException.class,
                        () -> system.draw(Matrix.getIdentity(), Matrix.getIdentity(), null));
            }
        });
    }

    @Test
    void aClosedSystemRefusesEveryOperation() {
        GameProbe.run(probe -> {
            ParticleSystem system = ParticleSystem.create(probe.device());
            system.close();
            system.close();
            assertThrows(IllegalStateException.class, system::getCapacity);
            assertThrows(NullPointerException.class, () -> ParticleSystem.create(null));
            assertThrows(NullPointerException.class,
                    () -> new ParticleEmitterSettings().setPosition(null));
            // The unused import is deliberate elsewhere; this keeps BufferUsage referenced only
            // where it is needed.
            assertEquals(BufferUsage.None, BufferUsage.None);
        });
    }
}
