package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.Objects;

/**
 * Colour and intensity arriving from one direction, everywhere.
 *
 * <p>A CNA extension. XNA's {@code BasicEffect} has three directional lights with a fixed shape
 * and no shadow; this is the engine layer's own, and it is the currency of the families built on
 * it -- a shadow map computes its light view from one, a cascaded shadow map updates from one,
 * the render pipeline takes one as the scene's key light, and the debug renderer draws one.
 *
 * <p><strong>Immutable.</strong> A light is a value: {@link #withDirection} and its siblings
 * return a new one rather than changing this. Nothing here holds a native handle, so nothing has
 * to be closed.
 *
 * <p><strong>The colour is a {@link Vector3}, not a {@code Color}.</strong> It is linear RGB and
 * an intensity above one is meaningful, which is exactly what XNA's 8-bit sRGB {@code Color}
 * cannot carry. That is CNA's choice and converting it here would lose the range that makes an
 * HDR pipeline work.
 */
public final class DirectionalLight {

    private final Vector3 direction;
    private final Vector3 color;
    private final float intensity;
    private final boolean castsShadows;

    /**
     * Creates a light from its parts.
     *
     * @param direction the direction the light travels
     * @param color linear RGB colour
     * @param intensity scalar multiplier on the colour
     * @param castsShadows whether this light should be given a shadow map
     */
    public DirectionalLight(Vector3 direction, Vector3 color, float intensity,
            boolean castsShadows) {
        this.direction = new Vector3(Objects.requireNonNull(direction, "direction"));
        this.color = new Vector3(Objects.requireNonNull(color, "color"));
        this.intensity = intensity;
        this.castsShadows = castsShadows;
    }

    /**
     * Returns the light CNA itself defaults to.
     *
     * <p>Asked of CNA rather than written down here, so a default that moves upstream moves here
     * too instead of quietly disagreeing.
     *
     * @return the default light
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static DirectionalLight createDefault() {
        GraphicsExtension.requireBackend();
        long[] integral = new long[1];
        float[] floating = new float[7];
        GraphicsExtension.check("DirectionalLight.createDefault", NativeEngineLayerRoutes
                .directionalLightExtInit(new byte[3], integral, floating));
        return new DirectionalLight(
                new Vector3(floating[0], floating[1], floating[2]),
                new Vector3(floating[3], floating[4], floating[5]),
                floating[6], integral[0] != 0L);
    }

    /** @return the direction the light travels */
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

    /** @return whether this light should be given a shadow map */
    public boolean getCastsShadows() {
        return castsShadows;
    }

    /**
     * @param value the new direction
     * @return a light like this one with that direction
     */
    public DirectionalLight withDirection(Vector3 value) {
        return new DirectionalLight(value, color, intensity, castsShadows);
    }

    /**
     * @param value the new linear RGB colour
     * @return a light like this one with that colour
     */
    public DirectionalLight withColor(Vector3 value) {
        return new DirectionalLight(direction, value, intensity, castsShadows);
    }

    /**
     * @param value the new intensity
     * @return a light like this one with that intensity
     */
    public DirectionalLight withIntensity(float value) {
        return new DirectionalLight(direction, color, value, castsShadows);
    }

    /**
     * @param value whether the light should be given a shadow map
     * @return a light like this one with that setting
     */
    public DirectionalLight withCastsShadows(boolean value) {
        return new DirectionalLight(direction, color, intensity, value);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DirectionalLight light)) {
            return false;
        }
        return direction.equals(light.direction) && color.equals(light.color)
                && Float.compare(intensity, light.intensity) == 0
                && castsShadows == light.castsShadows;
    }

    @Override
    public int hashCode() {
        return Objects.hash(direction, color, intensity, castsShadows);
    }

    @Override
    public String toString() {
        return "{Direction:" + direction + " Color:" + color + " Intensity:" + intensity
                + " CastsShadows:" + castsShadows + "}";
    }

    /** The integral leaves CNA's structure declares, in declaration order. */
    long[] integral() {
        return new long[] {castsShadows ? 1 : 0};
    }

    /** The floating leaves CNA's structure declares, in declaration order. */
    float[] floating() {
        return new float[] {
            direction.X, direction.Y, direction.Z,
            color.X, color.Y, color.Z,
            intensity,
        };
    }
}
