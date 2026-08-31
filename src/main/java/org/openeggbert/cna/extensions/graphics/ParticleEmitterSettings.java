package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Vector3;
import Microsoft.Xna.Framework.Vector4;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.Objects;

/**
 * What an emitter throws, how fast, and what happens to it afterwards.
 *
 * <p>A CNA extension. XNA has no particle system at all, so every field here is an addition.
 *
 * <p><strong>Nothing is corrected on the way in.</strong> That is CNA's own rule and it is worth
 * stating: an emission rate the system's capacity cannot sustain is <em>accepted</em> and then
 * reported by {@link ParticleSystem#isEmissionRateClamped()}, rather than being quietly reduced.
 * The settings are a description; the system says what it could actually do with it.
 *
 * <p>Colours are {@link Vector4} rather than {@code Color} and are deliberately unclamped, so an
 * emitter can be an HDR source that blooms.
 */
public final class ParticleEmitterSettings {

    private Vector3 position;
    private Vector3 direction;
    private Vector3 gravity;
    private Vector4 startColor;
    private Vector4 endColor;
    private float coneAngle;
    private float speed;
    private float speedVariance;
    private float lifetime;
    private float lifetimeVariance;
    private float drag;
    private float emissionRate;
    private float startSize;
    private float endSize;

    /**
     * Creates the settings CNA itself defaults to.
     *
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public ParticleEmitterSettings() {
        GraphicsExtension.requireBackend();
        float[] floating = new float[26];
        GraphicsExtension.check("ParticleEmitterSettings",
                NativeEngineLayerRoutes.particleEmitterSettingsInit(floating));
        read(floating);
    }

    /**
     * Copies another value.
     *
     * @param value the settings to copy
     */
    public ParticleEmitterSettings(ParticleEmitterSettings value) {
        Objects.requireNonNull(value, "value");
        position = new Vector3(value.position);
        direction = new Vector3(value.direction);
        gravity = new Vector3(value.gravity);
        startColor = new Vector4(value.startColor);
        endColor = new Vector4(value.endColor);
        coneAngle = value.coneAngle;
        speed = value.speed;
        speedVariance = value.speedVariance;
        lifetime = value.lifetime;
        lifetimeVariance = value.lifetimeVariance;
        drag = value.drag;
        emissionRate = value.emissionRate;
        startSize = value.startSize;
        endSize = value.endSize;
    }

    /** @return where particles are born, in world space */
    public Vector3 getPosition() {
        return new Vector3(position);
    }

    /**
     * @param value where particles are born, in world space
     */
    public void setPosition(Vector3 value) {
        position = new Vector3(Objects.requireNonNull(value, "value"));
    }

    /** @return the centre of the emission cone, which CNA normalises for itself */
    public Vector3 getDirection() {
        return new Vector3(direction);
    }

    /**
     * @param value the centre of the emission cone; it need not be normalised
     */
    public void setDirection(Vector3 value) {
        direction = new Vector3(Objects.requireNonNull(value, "value"));
    }

    /** @return the constant acceleration, in units per second squared */
    public Vector3 getGravity() {
        return new Vector3(gravity);
    }

    /**
     * @param value the constant acceleration, in units per second squared
     */
    public void setGravity(Vector3 value) {
        gravity = new Vector3(Objects.requireNonNull(value, "value"));
    }

    /** @return the colour at birth, unclamped so an emitter can be an HDR source */
    public Vector4 getStartColor() {
        return new Vector4(startColor);
    }

    /**
     * @param value the colour at birth
     */
    public void setStartColor(Vector4 value) {
        startColor = new Vector4(Objects.requireNonNull(value, "value"));
    }

    /** @return the colour at death */
    public Vector4 getEndColor() {
        return new Vector4(endColor);
    }

    /**
     * @param value the colour at death
     */
    public void setEndColor(Vector4 value) {
        endColor = new Vector4(Objects.requireNonNull(value, "value"));
    }

    /** @return the cone's half angle in radians; zero emits a line, pi a full sphere */
    public float getConeAngle() {
        return coneAngle;
    }

    /**
     * @param value the cone's half angle in radians
     */
    public void setConeAngle(float value) {
        coneAngle = value;
    }

    /** @return how fast a particle leaves, in units per second */
    public float getSpeed() {
        return speed;
    }

    /**
     * @param value how fast a particle leaves, in units per second
     */
    public void setSpeed(float value) {
        speed = value;
    }

    /** @return how much that speed varies, as a fraction of it */
    public float getSpeedVariance() {
        return speedVariance;
    }

    /**
     * @param value how much the speed varies, as a fraction of it
     */
    public void setSpeedVariance(float value) {
        speedVariance = value;
    }

    /** @return how long a particle lives, in seconds */
    public float getLifetime() {
        return lifetime;
    }

    /**
     * @param value how long a particle lives, in seconds
     */
    public void setLifetime(float value) {
        lifetime = value;
    }

    /** @return how much that lifetime varies, as a fraction of it */
    public float getLifetimeVariance() {
        return lifetimeVariance;
    }

    /**
     * @param value how much the lifetime varies, as a fraction of it
     */
    public void setLifetimeVariance(float value) {
        lifetimeVariance = value;
    }

    /** @return the linear drag per second; zero is a vacuum */
    public float getDrag() {
        return drag;
    }

    /**
     * @param value the linear drag per second
     */
    public void setDrag(float value) {
        drag = value;
    }

    /** @return how many particles are born per second */
    public float getEmissionRate() {
        return emissionRate;
    }

    /**
     * Sets how many particles are born per second.
     *
     * <p>Not clamped here and not clamped by the system either: a rate the capacity cannot
     * sustain is accepted and reported by {@link ParticleSystem#isEmissionRateClamped()}.
     *
     * @param value how many particles are born per second
     */
    public void setEmissionRate(float value) {
        emissionRate = value;
    }

    /** @return a particle's size at birth, in world units */
    public float getStartSize() {
        return startSize;
    }

    /**
     * @param value a particle's size at birth, in world units
     */
    public void setStartSize(float value) {
        startSize = value;
    }

    /** @return a particle's size at death, in world units */
    public float getEndSize() {
        return endSize;
    }

    /**
     * @param value a particle's size at death, in world units
     */
    public void setEndSize(float value) {
        endSize = value;
    }

    /** The floating leaves CNA's structure declares, in declaration order. */
    float[] floating() {
        return new float[] {
            position.X, position.Y, position.Z,
            direction.X, direction.Y, direction.Z,
            gravity.X, gravity.Y, gravity.Z,
            startColor.X, startColor.Y, startColor.Z, startColor.W,
            endColor.X, endColor.Y, endColor.Z, endColor.W,
            coneAngle, speed, speedVariance, lifetime, lifetimeVariance, drag,
            emissionRate, startSize, endSize,
        };
    }

    /** Reads the leaves back in the same order. */
    void read(float[] floating) {
        position = new Vector3(floating[0], floating[1], floating[2]);
        direction = new Vector3(floating[3], floating[4], floating[5]);
        gravity = new Vector3(floating[6], floating[7], floating[8]);
        startColor = new Vector4(floating[9], floating[10], floating[11], floating[12]);
        endColor = new Vector4(floating[13], floating[14], floating[15], floating[16]);
        coneAngle = floating[17];
        speed = floating[18];
        speedVariance = floating[19];
        lifetime = floating[20];
        lifetimeVariance = floating[21];
        drag = floating[22];
        emissionRate = floating[23];
        startSize = floating[24];
        endSize = floating[25];
    }
}
