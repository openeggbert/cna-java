package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.Objects;

/**
 * A point light restricted to a cone.
 *
 * <p>A CNA extension. The cone is two half-angles: everything inside {@link #getInnerAngle()} is
 * at full strength and everything outside {@link #getOuterAngle()} is dark, with the falloff in
 * between. Both are in radians, and both are half-angles from the axis rather than full cone
 * widths -- which is the number a spot light is usually specified with and the one it is usually
 * got wrong by.
 *
 * <p><strong>Immutable</strong>, and its colour is linear RGB in a {@link Vector3} rather than an
 * 8-bit {@code Color}, for the reason {@link DirectionalLight} states.
 */
public final class SpotLight {

    private final Vector3 position;
    private final Vector3 direction;
    private final Vector3 color;
    private final float intensity;
    private final float range;
    private final float innerAngle;
    private final float outerAngle;
    private final boolean castsShadows;

    /**
     * Creates a light from its parts.
     *
     * @param position world-space position
     * @param direction the direction the cone points
     * @param color linear RGB colour
     * @param intensity scalar multiplier on the colour
     * @param range distance at which the light stops contributing
     * @param innerAngle half-angle in radians inside which the light is at full strength
     * @param outerAngle half-angle in radians at which the light has fallen to nothing
     * @param castsShadows whether this light should be given a shadow map
     */
    public SpotLight(Vector3 position, Vector3 direction, Vector3 color, float intensity,
            float range, float innerAngle, float outerAngle, boolean castsShadows) {
        this.position = new Vector3(Objects.requireNonNull(position, "position"));
        this.direction = new Vector3(Objects.requireNonNull(direction, "direction"));
        this.color = new Vector3(Objects.requireNonNull(color, "color"));
        this.intensity = intensity;
        this.range = range;
        this.innerAngle = innerAngle;
        this.outerAngle = outerAngle;
        this.castsShadows = castsShadows;
    }

    /**
     * Returns the light CNA itself defaults to.
     *
     * @return the default light
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static SpotLight createDefault() {
        GraphicsExtension.requireBackend();
        long[] integral = new long[1];
        float[] floating = new float[13];
        GraphicsExtension.check("SpotLight.createDefault",
                NativeEngineLayerRoutes.spotLightExtInit(new byte[3], integral, floating));
        return new SpotLight(
                new Vector3(floating[0], floating[1], floating[2]),
                new Vector3(floating[3], floating[4], floating[5]),
                new Vector3(floating[6], floating[7], floating[8]),
                floating[9], floating[10], floating[11], floating[12], integral[0] != 0L);
    }

    /** @return the world-space position */
    public Vector3 getPosition() {
        return new Vector3(position);
    }

    /** @return the direction the cone points */
    public Vector3 getDirection() {
        return new Vector3(direction);
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

    /** @return the half-angle in radians inside which the light is at full strength */
    public float getInnerAngle() {
        return innerAngle;
    }

    /** @return the half-angle in radians at which the light has fallen to nothing */
    public float getOuterAngle() {
        return outerAngle;
    }

    /** @return whether this light should be given a shadow map */
    public boolean getCastsShadows() {
        return castsShadows;
    }

    /**
     * @param value the new position
     * @return a light like this one at that position
     */
    public SpotLight withPosition(Vector3 value) {
        return new SpotLight(value, direction, color, intensity, range, innerAngle, outerAngle,
                castsShadows);
    }

    /**
     * @param value the new cone direction
     * @return a light like this one pointing there
     */
    public SpotLight withDirection(Vector3 value) {
        return new SpotLight(position, value, color, intensity, range, innerAngle, outerAngle,
                castsShadows);
    }

    /**
     * @param value the new linear RGB colour
     * @return a light like this one with that colour
     */
    public SpotLight withColor(Vector3 value) {
        return new SpotLight(position, direction, value, intensity, range, innerAngle, outerAngle,
                castsShadows);
    }

    /**
     * @param value the new intensity
     * @return a light like this one with that intensity
     */
    public SpotLight withIntensity(float value) {
        return new SpotLight(position, direction, color, value, range, innerAngle, outerAngle,
                castsShadows);
    }

    /**
     * @param value the new range
     * @return a light like this one with that range
     */
    public SpotLight withRange(float value) {
        return new SpotLight(position, direction, color, intensity, value, innerAngle, outerAngle,
                castsShadows);
    }

    /**
     * Returns a light like this one with a different cone.
     *
     * @param inner the half-angle in radians inside which the light is at full strength
     * @param outer the half-angle in radians at which the light has fallen to nothing
     * @return a light like this one with that cone
     */
    public SpotLight withCone(float inner, float outer) {
        return new SpotLight(position, direction, color, intensity, range, inner, outer,
                castsShadows);
    }

    /**
     * @param value whether the light should be given a shadow map
     * @return a light like this one with that setting
     */
    public SpotLight withCastsShadows(boolean value) {
        return new SpotLight(position, direction, color, intensity, range, innerAngle, outerAngle,
                value);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SpotLight light)) {
            return false;
        }
        return position.equals(light.position) && direction.equals(light.direction)
                && color.equals(light.color)
                && Float.compare(intensity, light.intensity) == 0
                && Float.compare(range, light.range) == 0
                && Float.compare(innerAngle, light.innerAngle) == 0
                && Float.compare(outerAngle, light.outerAngle) == 0
                && castsShadows == light.castsShadows;
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, direction, color, intensity, range, innerAngle, outerAngle,
                castsShadows);
    }

    @Override
    public String toString() {
        return "{Position:" + position + " Direction:" + direction + " Color:" + color
                + " Intensity:" + intensity + " Range:" + range + " InnerAngle:" + innerAngle
                + " OuterAngle:" + outerAngle + " CastsShadows:" + castsShadows + "}";
    }

    /** The integral leaves CNA's structure declares, in declaration order. */
    long[] integral() {
        return new long[] {castsShadows ? 1 : 0};
    }

    /** The floating leaves CNA's structure declares, in declaration order. */
    float[] floating() {
        return new float[] {
            position.X, position.Y, position.Z,
            direction.X, direction.Y, direction.Z,
            color.X, color.Y, color.Z,
            intensity, range, innerAngle, outerAngle,
        };
    }
}
