package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.Objects;

/**
 * Colour radiating from a position, falling off to a range.
 *
 * <p>A CNA extension, and one XNA has no shape for at all: {@code BasicEffect} has three
 * directional lights and nothing else. This is what a clustered light set takes, what a cube
 * shadow map updates from, and what the debug renderer draws as a gizmo.
 *
 * <p><strong>Immutable</strong>, and its colour is linear RGB in a {@link Vector3} rather than an
 * 8-bit {@code Color}, for the reason {@link DirectionalLight} states.
 */
public final class PointLight {

    private final Vector3 position;
    private final Vector3 color;
    private final float intensity;
    private final float range;
    private final boolean castsShadows;

    /**
     * Creates a light from its parts.
     *
     * @param position world-space position
     * @param color linear RGB colour
     * @param intensity scalar multiplier on the colour
     * @param range distance at which the light stops contributing
     * @param castsShadows whether this light should be given a shadow cube
     */
    public PointLight(Vector3 position, Vector3 color, float intensity, float range,
            boolean castsShadows) {
        this.position = new Vector3(Objects.requireNonNull(position, "position"));
        this.color = new Vector3(Objects.requireNonNull(color, "color"));
        this.intensity = intensity;
        this.range = range;
        this.castsShadows = castsShadows;
    }

    /**
     * Returns the light CNA itself defaults to.
     *
     * @return the default light
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static PointLight createDefault() {
        GraphicsExtension.requireBackend();
        long[] integral = new long[1];
        float[] floating = new float[8];
        GraphicsExtension.check("PointLight.createDefault",
                NativeEngineLayerRoutes.pointLightExtInit(new byte[3], integral, floating));
        return new PointLight(
                new Vector3(floating[0], floating[1], floating[2]),
                new Vector3(floating[3], floating[4], floating[5]),
                floating[6], floating[7], integral[0] != 0L);
    }

    /** @return the world-space position */
    public Vector3 getPosition() {
        return new Vector3(position);
    }

    /** @return the linear RGB colour */
    public Vector3 getColor() {
        return new Vector3(color);
    }

    /** @return the scalar multiplier on the colour */
    public float getIntensity() {
        return intensity;
    }

    /** @return the distance at which the light stops contributing */
    public float getRange() {
        return range;
    }

    /** @return whether this light should be given a shadow cube */
    public boolean getCastsShadows() {
        return castsShadows;
    }

    /**
     * @param value the new position
     * @return a light like this one at that position
     */
    public PointLight withPosition(Vector3 value) {
        return new PointLight(value, color, intensity, range, castsShadows);
    }

    /**
     * @param value the new linear RGB colour
     * @return a light like this one with that colour
     */
    public PointLight withColor(Vector3 value) {
        return new PointLight(position, value, intensity, range, castsShadows);
    }

    /**
     * @param value the new intensity
     * @return a light like this one with that intensity
     */
    public PointLight withIntensity(float value) {
        return new PointLight(position, color, value, range, castsShadows);
    }

    /**
     * @param value the new range
     * @return a light like this one with that range
     */
    public PointLight withRange(float value) {
        return new PointLight(position, color, intensity, value, castsShadows);
    }

    /**
     * @param value whether the light should be given a shadow cube
     * @return a light like this one with that setting
     */
    public PointLight withCastsShadows(boolean value) {
        return new PointLight(position, color, intensity, range, value);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PointLight light)) {
            return false;
        }
        return position.equals(light.position) && color.equals(light.color)
                && Float.compare(intensity, light.intensity) == 0
                && Float.compare(range, light.range) == 0
                && castsShadows == light.castsShadows;
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, color, intensity, range, castsShadows);
    }

    @Override
    public String toString() {
        return "{Position:" + position + " Color:" + color + " Intensity:" + intensity
                + " Range:" + range + " CastsShadows:" + castsShadows + "}";
    }

    /** The integral leaves CNA's structure declares, in declaration order. */
    long[] integral() {
        return new long[] {castsShadows ? 1 : 0};
    }

    /** The floating leaves CNA's structure declares, in declaration order. */
    float[] floating() {
        return new float[] {
            position.X, position.Y, position.Z,
            color.X, color.Y, color.Z,
            intensity, range,
        };
    }
}
