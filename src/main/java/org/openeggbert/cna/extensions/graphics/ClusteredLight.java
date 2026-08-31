package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.Objects;

/**
 * One light in a {@link ClusteredLightSet}.
 *
 * <p>A CNA extension. A clustered light is a point light or a spot light in one shape, because a
 * clustered set holds both and sorts them into the same grid: {@link #getType()} says which the
 * cone fields mean anything for.
 *
 * <p><strong>Immutable</strong>, and its colour is linear RGB in a {@link Vector3} for the reason
 * {@link DirectionalLight} states. {@link #isUsable()} asks CNA whether the set would accept it,
 * which is worth asking before adding a thousand of them.
 */
public final class ClusteredLight {

    private final ClusteredLightType type;
    private final boolean castsShadows;
    private final Vector3 position;
    private final Vector3 direction;
    private final Vector3 color;
    private final float intensity;
    private final float range;
    private final float innerAngle;
    private final float outerAngle;

    /**
     * Creates a light from its parts.
     *
     * @param type whether this is a point light or a spot light
     * @param position world-space position
     * @param direction the direction a spot light points
     * @param color linear RGB colour
     * @param intensity scalar multiplier on the colour; must not be negative
     * @param range distance at which the light stops contributing; must be positive
     * @param innerAngle half-angle in radians inside which a spot light is at full strength
     * @param outerAngle half-angle in radians at which a spot light has fallen to nothing
     * @param castsShadows whether this light should be given a shadow
     */
    public ClusteredLight(ClusteredLightType type, Vector3 position, Vector3 direction,
            Vector3 color, float intensity, float range, float innerAngle, float outerAngle,
            boolean castsShadows) {
        this.type = Objects.requireNonNull(type, "type");
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
    public static ClusteredLight createDefault() {
        GraphicsExtension.requireBackend();
        long[] integral = new long[2];
        float[] floating = new float[13];
        GraphicsExtension.check("ClusteredLight.createDefault",
                NativeEngineLayerRoutes.clusteredLightExtInit(new byte[3], integral, floating));
        return read(integral, floating);
    }

    /**
     * Reports whether a set would accept this light.
     *
     * <p>CNA's own answer, not a rule restated here: a range that is not positive or an intensity
     * below zero makes a light that contributes nothing and would take a slot in every cluster it
     * touches.
     *
     * @return whether the light is usable
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public boolean isUsable() {
        GraphicsExtension.requireBackend();
        boolean[] usable = new boolean[1];
        GraphicsExtension.check("ClusteredLight.isUsable", NativeEngineLayerRoutes
                .clusteredLightSetIsUsable(new byte[3], integral(), floating(), usable));
        return usable[0];
    }

    /** @return whether this is a point light or a spot light */
    public ClusteredLightType getType() {
        return type;
    }

    /** @return the world-space position */
    public Vector3 getPosition() {
        return new Vector3(position);
    }

    /** @return the direction a spot light points */
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

    /** @return the half-angle in radians inside which a spot light is at full strength */
    public float getInnerAngle() {
        return innerAngle;
    }

    /** @return the half-angle in radians at which a spot light has fallen to nothing */
    public float getOuterAngle() {
        return outerAngle;
    }

    /** @return whether this light should be given a shadow */
    public boolean getCastsShadows() {
        return castsShadows;
    }

    /**
     * @param value the new type
     * @return a light like this one of that type
     */
    public ClusteredLight withType(ClusteredLightType value) {
        return new ClusteredLight(value, position, direction, color, intensity, range,
                innerAngle, outerAngle, castsShadows);
    }

    /**
     * @param value the new position
     * @return a light like this one at that position
     */
    public ClusteredLight withPosition(Vector3 value) {
        return new ClusteredLight(type, value, direction, color, intensity, range, innerAngle,
                outerAngle, castsShadows);
    }

    /**
     * @param value the new direction
     * @return a light like this one pointing there
     */
    public ClusteredLight withDirection(Vector3 value) {
        return new ClusteredLight(type, position, value, color, intensity, range, innerAngle,
                outerAngle, castsShadows);
    }

    /**
     * @param value the new linear RGB colour
     * @return a light like this one with that colour
     */
    public ClusteredLight withColor(Vector3 value) {
        return new ClusteredLight(type, position, direction, value, intensity, range, innerAngle,
                outerAngle, castsShadows);
    }

    /**
     * @param value the new intensity
     * @return a light like this one with that intensity
     */
    public ClusteredLight withIntensity(float value) {
        return new ClusteredLight(type, position, direction, color, value, range, innerAngle,
                outerAngle, castsShadows);
    }

    /**
     * @param value the new range
     * @return a light like this one with that range
     */
    public ClusteredLight withRange(float value) {
        return new ClusteredLight(type, position, direction, color, intensity, value, innerAngle,
                outerAngle, castsShadows);
    }

    /**
     * Returns a light like this one with a different cone.
     *
     * @param inner the half-angle in radians inside which a spot light is at full strength
     * @param outer the half-angle in radians at which a spot light has fallen to nothing
     * @return a light like this one with that cone
     */
    public ClusteredLight withCone(float inner, float outer) {
        return new ClusteredLight(type, position, direction, color, intensity, range, inner,
                outer, castsShadows);
    }

    /**
     * @param value whether the light should be given a shadow
     * @return a light like this one with that setting
     */
    public ClusteredLight withCastsShadows(boolean value) {
        return new ClusteredLight(type, position, direction, color, intensity, range, innerAngle,
                outerAngle, value);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ClusteredLight light)) {
            return false;
        }
        return type == light.type && castsShadows == light.castsShadows
                && position.equals(light.position) && direction.equals(light.direction)
                && color.equals(light.color)
                && Float.compare(intensity, light.intensity) == 0
                && Float.compare(range, light.range) == 0
                && Float.compare(innerAngle, light.innerAngle) == 0
                && Float.compare(outerAngle, light.outerAngle) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, castsShadows, position, direction, color, intensity, range,
                innerAngle, outerAngle);
    }

    @Override
    public String toString() {
        return "{Type:" + type + " Position:" + position + " Direction:" + direction
                + " Color:" + color + " Intensity:" + intensity + " Range:" + range
                + " InnerAngle:" + innerAngle + " OuterAngle:" + outerAngle
                + " CastsShadows:" + castsShadows + "}";
    }

    /** Reads one light out of the leaves CNA's structure declares. */
    static ClusteredLight read(long[] integral, float[] floating) {
        return read(integral, 0, floating, 0);
    }

    /** Reads the light at one position of a packed array. */
    static ClusteredLight read(long[] integral, int integralBase, float[] floating,
            int floatingBase) {
        return new ClusteredLight(
                ClusteredLightType.fromValue(integral[integralBase]),
                new Vector3(floating[floatingBase], floating[floatingBase + 1],
                        floating[floatingBase + 2]),
                new Vector3(floating[floatingBase + 3], floating[floatingBase + 4],
                        floating[floatingBase + 5]),
                new Vector3(floating[floatingBase + 6], floating[floatingBase + 7],
                        floating[floatingBase + 8]),
                floating[floatingBase + 9], floating[floatingBase + 10],
                floating[floatingBase + 11], floating[floatingBase + 12],
                integral[integralBase + 1] != 0L);
    }

    /** The integral leaves CNA's structure declares, in declaration order. */
    long[] integral() {
        return new long[] {type.ordinal(), castsShadows ? 1 : 0};
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
