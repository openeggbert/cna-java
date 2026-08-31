package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A light that is a <em>surface</em>, which is what almost every real light is.
 *
 * <p>A CNA extension, and the third kind of light after XNA's directional one and CNA's point and
 * spot. The difference is not brightness but the shape of what the light does: a window is a
 * bright rectangle in a polished floor, and that is not something a point light can be tuned into
 * producing.
 *
 * <p><strong>The shape is a centre and two half-axes</strong>, not four corners, so all three
 * shapes share one description and none can be given a non-planar or self-intersecting outline.
 * {@link #getRightAxis()} and {@link #getUpAxis()} are half-extents -- their lengths are half the
 * rectangle's width and height -- and the emitting side is the one they cross towards.
 *
 * <p><strong>There are no area-light shadows.</strong> A soft-edged shadow needs many samples of
 * the light's surface or a ray query, and this layer has neither, so an area light lights what
 * faces it whether or not anything stands in the way. Stated here because the failure looks like
 * a shadow bug.
 *
 * <p>Immutable, and needs no graphics device. {@link #getContribution} is the shading itself,
 * evaluated on the CPU exactly as the shader does.
 */
public final class AreaLight {

    /** {@code CNA_AREA_LIGHT_QUAD_CORNER_COUNT}: the quad every shape is integrated as. */
    public static final int QuadCornerCount = 4;

    /** CNA's own result for a buffer that could not hold the answer. */
    private static final int RESULT_BUFFER_TOO_SMALL = 14;

    private final AreaLightShape shape;
    private final boolean twoSided;
    private final Vector3 position;
    private final Vector3 rightAxis;
    private final Vector3 upAxis;
    private final Vector3 color;
    private final float intensity;
    private final float range;

    /**
     * Creates a light from its parts.
     *
     * @param shape which shape the light emits from
     * @param position world-space centre of the emitting surface
     * @param rightAxis half-axis across the surface; its length is half the width
     * @param upAxis half-axis up the surface; half the height, or the tube's radius
     * @param color emitted colour, linear and unbounded
     * @param intensity multiplier applied to the colour
     * @param range distance past which the light contributes nothing
     * @param twoSided whether the light emits from both faces of its surface
     */
    public AreaLight(AreaLightShape shape, Vector3 position, Vector3 rightAxis, Vector3 upAxis,
            Vector3 color, float intensity, float range, boolean twoSided) {
        this.shape = Objects.requireNonNull(shape, "shape");
        this.position = new Vector3(Objects.requireNonNull(position, "position"));
        this.rightAxis = new Vector3(Objects.requireNonNull(rightAxis, "rightAxis"));
        this.upAxis = new Vector3(Objects.requireNonNull(upAxis, "upAxis"));
        this.color = new Vector3(Objects.requireNonNull(color, "color"));
        this.intensity = intensity;
        this.range = range;
        this.twoSided = twoSided;
    }

    /**
     * Returns the light CNA itself defaults to.
     *
     * @return the default light
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static AreaLight createDefault() {
        GraphicsExtension.requireBackend();
        long[] integral = new long[2];
        float[] floating = new float[14];
        GraphicsExtension.check("AreaLight.createDefault",
                NativeEngineLayerRoutes.areaLightExtInit(new byte[3], integral, floating));
        return read(integral, floating);
    }

    /**
     * Returns the shading lobe's width for a roughness.
     *
     * @param roughness the surface roughness
     * @return the lobe scale {@link #getCoverage} takes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static float getLobeScaleFor(float roughness) {
        GraphicsExtension.requireBackend();
        float[] scale = new float[1];
        GraphicsExtension.check("AreaLight.getLobeScaleFor",
                NativeEngineLayerRoutes.areaLightShadingLobeScaleFor(roughness, scale));
        return scale[0];
    }

    /**
     * Returns how much of a shading lobe a quad covers.
     *
     * @param quad the four corners, as {@link #getQuad} produces them
     * @param surface the world-space point being lit
     * @param lobeAxis the direction the lobe points
     * @param lobeScale the lobe's width, from {@link #getLobeScaleFor}
     * @param twoSided whether the light emits from both faces
     * @return the coverage; zero when the quad is behind the surface
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static float getCoverage(List<Vector3> quad, Vector3 surface, Vector3 lobeAxis,
            float lobeScale, boolean twoSided) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(quad, "quad");
        if (quad.size() != QuadCornerCount) {
            throw new IllegalArgumentException("a quad has " + QuadCornerCount
                    + " corners, not " + quad.size());
        }
        float[] packed = new float[QuadCornerCount * 3];
        for (int corner = 0; corner < QuadCornerCount; corner++) {
            System.arraycopy(EngineValues.floats(quad.get(corner), "quad"), 0, packed,
                    corner * 3, 3);
        }
        float[] coverage = new float[1];
        GraphicsExtension.check("AreaLight.getCoverage",
                NativeEngineLayerRoutes.areaLightShadingCoverage(packed,
                        EngineValues.floats(surface, "surface"),
                        EngineValues.floats(lobeAxis, "lobeAxis"), lobeScale, twoSided,
                        coverage));
        return coverage[0];
    }

    /**
     * Returns the GLSL a shader shades an area light with.
     *
     * @return the GLSL
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static String getShadingGlsl() {
        GraphicsExtension.requireBackend();
        long[] bytes = new long[1];
        int probe = NativeEngineLayerRoutes.areaLightShadingCopyShadingGlsl(new byte[0], bytes);
        if (probe != RESULT_BUFFER_TOO_SMALL) {
            GraphicsExtension.check("AreaLight.getShadingGlsl", probe);
        }
        int length = Math.toIntExact(bytes[0]);
        if (length == 0) {
            return "";
        }
        byte[] destination = new byte[length];
        GraphicsExtension.check("AreaLight.getShadingGlsl",
                NativeEngineLayerRoutes.areaLightShadingCopyShadingGlsl(destination, bytes));
        return new String(destination, 0, Math.toIntExact(bytes[0]), StandardCharsets.UTF_8);
    }

    /**
     * Reports whether the light is usable.
     *
     * @return CNA's own answer
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public boolean isValid() {
        GraphicsExtension.requireBackend();
        boolean[] valid = new boolean[1];
        GraphicsExtension.check("AreaLight.isValid", NativeEngineLayerRoutes
                .areaLightExtIsValid(new byte[3], integral(), floating(), valid));
        return valid[0];
    }

    /**
     * Returns the four corners of the quad this light is integrated as.
     *
     * <p><strong>Shape-dependent, and that is the point</strong>: a rectangle uses its axes as
     * they are, a disc scales them so a polygon matches the disc's area, and a tube is
     * <em>billboarded</em> -- turned so its face points at the surface, because a cylinder looks
     * like a rectangle from wherever it is seen. One quad therefore serves all three shapes.
     *
     * @param surface the world-space point being lit; only a tube's quad depends on it
     * @return the corners, counter-clockwise from the lower left
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public List<Vector3> getQuad(Vector3 surface) {
        GraphicsExtension.requireBackend();
        float[] quad = new float[QuadCornerCount * 3];
        GraphicsExtension.check("AreaLight.getQuad", NativeEngineLayerRoutes
                .areaLightShadingQuadOf(new byte[3], integral(), floating(),
                        EngineValues.floats(surface, "surface"), quad));
        List<Vector3> corners = new ArrayList<>(QuadCornerCount);
        for (int corner = 0; corner < QuadCornerCount; corner++) {
            corners.add(new Vector3(quad[corner * 3], quad[corner * 3 + 1],
                    quad[corner * 3 + 2]));
        }
        return List.copyOf(corners);
    }

    /**
     * Returns what this light contributes to a surface, as the shader computes it.
     *
     * @param surface the world-space point being lit
     * @param normal the surface normal
     * @param cameraPosition where the camera is
     * @param baseColor the surface's base colour
     * @param metallic how metallic the surface is
     * @param roughness how rough it is
     * @return the contribution per channel
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public Vector3 getContribution(Vector3 surface, Vector3 normal, Vector3 cameraPosition,
            Vector3 baseColor, float metallic, float roughness) {
        GraphicsExtension.requireBackend();
        float[] contribution = new float[3];
        GraphicsExtension.check("AreaLight.getContribution", NativeEngineLayerRoutes
                .areaLightShadingContribution(new byte[3], integral(), floating(),
                        EngineValues.floats(surface, "surface"),
                        EngineValues.floats(normal, "normal"),
                        EngineValues.floats(cameraPosition, "cameraPosition"),
                        EngineValues.floats(baseColor, "baseColor"), metallic, roughness,
                        contribution));
        return new Vector3(contribution[0], contribution[1], contribution[2]);
    }

    /** @return which shape the light emits from */
    public AreaLightShape getShape() {
        return shape;
    }

    /** @return whether the light emits from both faces of its surface */
    public boolean getTwoSided() {
        return twoSided;
    }

    /** @return the world-space centre of the emitting surface */
    public Vector3 getPosition() {
        return new Vector3(position);
    }

    /** @return the half-axis across the surface */
    public Vector3 getRightAxis() {
        return new Vector3(rightAxis);
    }

    /** @return the half-axis up the surface */
    public Vector3 getUpAxis() {
        return new Vector3(upAxis);
    }

    /** @return the emitted colour, linear and unbounded */
    public Vector3 getColor() {
        return new Vector3(color);
    }

    /** @return the multiplier applied to the colour */
    public float getIntensity() {
        return intensity;
    }

    /** @return the distance past which the light contributes nothing */
    public float getRange() {
        return range;
    }

    /**
     * @param value the new shape
     * @return a light like this one of that shape
     */
    public AreaLight withShape(AreaLightShape value) {
        return new AreaLight(value, position, rightAxis, upAxis, color, intensity, range,
                twoSided);
    }

    /**
     * @param value the new centre
     * @return a light like this one there
     */
    public AreaLight withPosition(Vector3 value) {
        return new AreaLight(shape, value, rightAxis, upAxis, color, intensity, range, twoSided);
    }

    /**
     * Returns a light like this one with a different surface.
     *
     * @param right the half-axis across the surface
     * @param up the half-axis up the surface
     * @return a light like this one with that surface
     */
    public AreaLight withAxes(Vector3 right, Vector3 up) {
        return new AreaLight(shape, position, right, up, color, intensity, range, twoSided);
    }

    /**
     * @param value the new colour
     * @return a light like this one with that colour
     */
    public AreaLight withColor(Vector3 value) {
        return new AreaLight(shape, position, rightAxis, upAxis, value, intensity, range,
                twoSided);
    }

    /**
     * @param value the new intensity
     * @return a light like this one with that intensity
     */
    public AreaLight withIntensity(float value) {
        return new AreaLight(shape, position, rightAxis, upAxis, color, value, range, twoSided);
    }

    /**
     * @param value the new range
     * @return a light like this one with that range
     */
    public AreaLight withRange(float value) {
        return new AreaLight(shape, position, rightAxis, upAxis, color, intensity, value,
                twoSided);
    }

    /**
     * @param value whether the light emits from both faces
     * @return a light like this one with that setting
     */
    public AreaLight withTwoSided(boolean value) {
        return new AreaLight(shape, position, rightAxis, upAxis, color, intensity, range, value);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AreaLight light)) {
            return false;
        }
        return shape == light.shape && twoSided == light.twoSided
                && position.equals(light.position) && rightAxis.equals(light.rightAxis)
                && upAxis.equals(light.upAxis) && color.equals(light.color)
                && Float.compare(intensity, light.intensity) == 0
                && Float.compare(range, light.range) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(shape, twoSided, position, rightAxis, upAxis, color, intensity,
                range);
    }

    @Override
    public String toString() {
        return "{Shape:" + shape + " Position:" + position + " RightAxis:" + rightAxis
                + " UpAxis:" + upAxis + " Color:" + color + " Intensity:" + intensity
                + " Range:" + range + " TwoSided:" + twoSided + "}";
    }

    private static AreaLight read(long[] integral, float[] floating) {
        return new AreaLight(
                AreaLightShape.fromValue(integral[0]),
                new Vector3(floating[0], floating[1], floating[2]),
                new Vector3(floating[3], floating[4], floating[5]),
                new Vector3(floating[6], floating[7], floating[8]),
                new Vector3(floating[9], floating[10], floating[11]),
                floating[12], floating[13], integral[1] != 0L);
    }

    /** The integral leaves CNA's structure declares, in declaration order. */
    long[] integral() {
        return new long[] {shape.ordinal(), twoSided ? 1 : 0};
    }

    /** The floating leaves CNA's structure declares, in declaration order. */
    float[] floating() {
        return new float[] {
            position.X, position.Y, position.Z,
            rightAxis.X, rightAxis.Y, rightAxis.Z,
            upAxis.X, upAxis.Y, upAxis.Z,
            color.X, color.Y, color.Z,
            intensity, range,
        };
    }
}
