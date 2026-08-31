package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.Texture2D;
import Microsoft.Xna.Framework.Graphics.TextureCube;
import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.Objects;

/**
 * One point or spot light as a lit effect receives it, with its shadow attached.
 *
 * <p>A CNA extension, and the shape CNA's stock effects read. It carries <em>both</em> a shadow
 * cube and a shadow map, because a point light shadows into the cube and a spot light into the
 * map, and {@link #getKind()} says which one is meaningful. That is why it is one type and not
 * two: an effect has a fixed slot and the slot has to hold either.
 *
 * <p>{@link PointLight} and {@link SpotLight} are what a game <em>authors</em>; this is what an
 * effect is <em>given</em>, after a shadow pass has produced something to sample.
 *
 * <p><strong>Immutable, and the shadow textures are retained rather than owned.</strong> CNA's
 * structure records them and never owns them, so this holds Java references to keep them alive
 * and disposes neither.
 */
public final class PunctualLight {

    private final PunctualLightKind kind;
    private final Vector3 position;
    private final Vector3 direction;
    private final Vector3 diffuseColor;
    private final float range;
    private final float innerAngle;
    private final float outerAngle;
    private final float shadowDepthBias;
    private final TextureCube shadowCube;
    private final Texture2D shadowMap;
    private final Matrix shadowViewProjection;

    /**
     * Creates a light from its parts.
     *
     * @param kind which kind of light this is; {@link PunctualLightKind#None} leaves the slot
     *        unused
     * @param position world-space position
     * @param direction the direction a spot light points
     * @param diffuseColor linear RGB diffuse colour
     * @param range distance at which the light stops contributing
     * @param innerAngle half-angle in radians inside which a spot light is at full strength
     * @param outerAngle half-angle in radians at which a spot light has fallen to nothing
     * @param shadowDepthBias depth bias applied when sampling this light's shadow
     * @param shadowCube a point light's shadow cube, or {@code null}
     * @param shadowMap a spot light's shadow map, or {@code null}
     * @param shadowViewProjection the transform that takes world space into the light's shadow
     *        space
     */
    public PunctualLight(PunctualLightKind kind, Vector3 position, Vector3 direction,
            Vector3 diffuseColor, float range, float innerAngle, float outerAngle,
            float shadowDepthBias, TextureCube shadowCube, Texture2D shadowMap,
            Matrix shadowViewProjection) {
        this.kind = Objects.requireNonNull(kind, "kind");
        this.position = new Vector3(Objects.requireNonNull(position, "position"));
        this.direction = new Vector3(Objects.requireNonNull(direction, "direction"));
        this.diffuseColor = new Vector3(Objects.requireNonNull(diffuseColor, "diffuseColor"));
        this.range = range;
        this.innerAngle = innerAngle;
        this.outerAngle = outerAngle;
        this.shadowDepthBias = shadowDepthBias;
        this.shadowCube = shadowCube;
        this.shadowMap = shadowMap;
        this.shadowViewProjection = new Matrix(
                Objects.requireNonNull(shadowViewProjection, "shadowViewProjection"));
    }

    /**
     * Returns the light CNA itself defaults to, which is an unused slot.
     *
     * @return the default light
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static PunctualLight createDefault() {
        GraphicsExtension.requireBackend();
        long[] integral = new long[4];
        float[] floating = new float[29];
        GraphicsExtension.check("PunctualLight.createDefault",
                NativeEngineLayerRoutes.punctualLightExtInit(integral, floating));
        return fromLeaves(integral, floating);
    }

    /** @return which kind of light this is */
    public PunctualLightKind getKind() {
        return kind;
    }

    /** @return the world-space position */
    public Vector3 getPosition() {
        return new Vector3(position);
    }

    /** @return the direction a spot light points */
    public Vector3 getDirection() {
        return new Vector3(direction);
    }

    /** @return the linear RGB diffuse colour */
    public Vector3 getDiffuseColor() {
        return new Vector3(diffuseColor);
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

    /** @return the depth bias applied when sampling this light's shadow */
    public float getShadowDepthBias() {
        return shadowDepthBias;
    }

    /** @return a point light's shadow cube, retained rather than owned, or {@code null} */
    public TextureCube getShadowCube() {
        return shadowCube;
    }

    /** @return a spot light's shadow map, retained rather than owned, or {@code null} */
    public Texture2D getShadowMap() {
        return shadowMap;
    }

    /** @return the transform that takes world space into this light's shadow space */
    public Matrix getShadowViewProjection() {
        return new Matrix(shadowViewProjection);
    }

    /**
     * Returns a light like this one with a point light's shadow cube attached.
     *
     * @param cube the shadow cube, borrowed and retained
     * @param viewProjection the transform into the light's shadow space
     * @return the light
     */
    public PunctualLight withShadowCube(TextureCube cube, Matrix viewProjection) {
        return new PunctualLight(kind, position, direction, diffuseColor, range, innerAngle,
                outerAngle, shadowDepthBias, cube, null, viewProjection);
    }

    /**
     * Returns a light like this one with a spot light's shadow map attached.
     *
     * @param map the shadow map, borrowed and retained
     * @param viewProjection the transform into the light's shadow space
     * @return the light
     */
    public PunctualLight withShadowMap(Texture2D map, Matrix viewProjection) {
        return new PunctualLight(kind, position, direction, diffuseColor, range, innerAngle,
                outerAngle, shadowDepthBias, null, map, viewProjection);
    }

    /**
     * @param value the new kind
     * @return a light like this one of that kind
     */
    public PunctualLight withKind(PunctualLightKind value) {
        return new PunctualLight(value, position, direction, diffuseColor, range, innerAngle,
                outerAngle, shadowDepthBias, shadowCube, shadowMap, shadowViewProjection);
    }

    /**
     * @param value the new position
     * @return a light like this one there
     */
    public PunctualLight withPosition(Vector3 value) {
        return new PunctualLight(kind, value, direction, diffuseColor, range, innerAngle,
                outerAngle, shadowDepthBias, shadowCube, shadowMap, shadowViewProjection);
    }

    /**
     * @param value the new range
     * @return a light like this one with that range
     */
    public PunctualLight withRange(float value) {
        return new PunctualLight(kind, position, direction, diffuseColor, value, innerAngle,
                outerAngle, shadowDepthBias, shadowCube, shadowMap, shadowViewProjection);
    }

    /**
     * @param value the new diffuse colour
     * @return a light like this one with that colour
     */
    public PunctualLight withDiffuseColor(Vector3 value) {
        return new PunctualLight(kind, position, direction, value, range, innerAngle,
                outerAngle, shadowDepthBias, shadowCube, shadowMap, shadowViewProjection);
    }

    /**
     * Rebuilds a light from CNA's flat leaves, with no textures.
     *
     * <p>Only ever used for a light read back <em>out</em> of an effect, and CNA's header is
     * explicit that both shadow handles come back invalid there: the canonical structure holds raw
     * pointers and this ABI will not invent a name for a texture it does not track.
     */
    static PunctualLight fromLeaves(long[] integral, float[] floating) {
        return new PunctualLight(
                PunctualLightKind.fromValue(integral[0]),
                new Vector3(floating[0], floating[1], floating[2]),
                new Vector3(floating[3], floating[4], floating[5]),
                new Vector3(floating[6], floating[7], floating[8]),
                floating[9], floating[10], floating[11], floating[12],
                null, null, matrixAt(floating, 13));
    }

    /** Reads the shadow transform out of the flat leaves, which start at a fixed offset. */
    private static Matrix matrixAt(float[] leaves, int base) {
        return new Matrix(
                leaves[base], leaves[base + 1], leaves[base + 2], leaves[base + 3],
                leaves[base + 4], leaves[base + 5], leaves[base + 6], leaves[base + 7],
                leaves[base + 8], leaves[base + 9], leaves[base + 10], leaves[base + 11],
                leaves[base + 12], leaves[base + 13], leaves[base + 14], leaves[base + 15]);
    }

    /** The integral leaves CNA's structure declares, in declaration order. */
    long[] integral() {
        return new long[] {
            kind.ordinal(), 0L,
            shadowCube == null ? 0L : NativeBindings.nativeResourceHandle(shadowCube),
            shadowMap == null ? 0L : NativeBindings.nativeResourceHandle(shadowMap),
        };
    }

    /** The floating leaves CNA's structure declares, in declaration order. */
    float[] floating() {
        float[] leaves = new float[29];
        leaves[0] = position.X;
        leaves[1] = position.Y;
        leaves[2] = position.Z;
        leaves[3] = direction.X;
        leaves[4] = direction.Y;
        leaves[5] = direction.Z;
        leaves[6] = diffuseColor.X;
        leaves[7] = diffuseColor.Y;
        leaves[8] = diffuseColor.Z;
        leaves[9] = range;
        leaves[10] = innerAngle;
        leaves[11] = outerAngle;
        leaves[12] = shadowDepthBias;
        System.arraycopy(EngineValues.floats(shadowViewProjection, "shadowViewProjection"), 0,
                leaves, 13, EngineValues.MATRIX_LEAVES);
        return leaves;
    }
}
