package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The light arriving at one point from every direction, as nine numbers per channel.
 *
 * <p>A CNA extension, and one XNA has nothing like: XNA's only indirect lighting is
 * {@code BasicEffect.AmbientLightColor}, one colour for the whole world. A light probe stores the
 * incoming light as second-order spherical harmonics -- {@link #CoefficientCount} coefficients,
 * each an RGB vector -- which is enough to reconstruct a smooth directional ambient: a surface
 * facing the sky is lit differently from one facing a red wall, from the same probe.
 *
 * <p>{@link #getIrradiance} is the reconstruction, and it is <strong>never negative</strong>:
 * spherical harmonics can ring below zero and CNA floors it, because negative light is not a
 * look.
 *
 * <p><strong>Visibility is optional and separate.</strong> A probe may also carry the mean and
 * mean-squared distance to an occluder in each of {@link #VisibilityDirectionCount} directions,
 * which {@link #getVisibilityWeight} turns into how much of the probe's light actually reaches a
 * point. A probe with no visibility data answers one -- "nothing is known to be in the way" --
 * rather than refusing, so a game can ship probes with and without it.
 *
 * <p>Needs no graphics device: a probe is arithmetic and can be built, stored and evaluated on a
 * loading thread.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class LightProbe implements AutoCloseable {

    /** {@code CNA_LIGHT_PROBE_COEFFICIENT_COUNT_EXT}: second-order spherical harmonics. */
    public static final int CoefficientCount = 9;

    /** {@code CNA_LIGHT_PROBE_VISIBILITY_DIRECTIONS_EXT}: the six axis directions. */
    public static final int VisibilityDirectionCount = 6;

    /** CNA's own result for a buffer that could not hold the answer. */
    private static final int RESULT_BUFFER_TOO_SMALL = 14;

    private final long handle;
    private boolean closed;

    private LightProbe(long handle) {
        this.handle = handle;
    }

    /**
     * Creates a probe at the origin with no light in it.
     *
     * @return the probe, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static LightProbe create() {
        GraphicsExtension.requireBackend();
        long[] probe = new long[1];
        GraphicsExtension.check("LightProbe.create",
                NativeEngineLayerRoutes.lightProbeExtCreate(probe));
        return new LightProbe(probe[0]);
    }

    /**
     * Creates a probe at a position with no light in it.
     *
     * @param position where the probe sits
     * @return the probe, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static LightProbe createAt(Vector3 position) {
        GraphicsExtension.requireBackend();
        long[] probe = new long[1];
        GraphicsExtension.check("LightProbe.createAt",
                NativeEngineLayerRoutes.lightProbeExtCreateAt(
                        EngineValues.floats(position, "position"), probe));
        return new LightProbe(probe[0]);
    }

    /**
     * Returns the GLSL that evaluates a probe in a shader.
     *
     * <p>Exposed for the same reason {@link ClusteredLightBuffer#getLightLookupGlsl()} is: a game
     * whose shader reconstructs irradiance itself has to reconstruct it the way CNA does, and
     * reading CNA's own source is how it can.
     *
     * @return the GLSL
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static String getEvaluationGlsl() {
        GraphicsExtension.requireBackend();
        long[] bytes = new long[1];
        int probe = NativeEngineLayerRoutes
                .lightProbeExtCopyEvaluationGlsl(new byte[0], bytes);
        if (probe != RESULT_BUFFER_TOO_SMALL) {
            GraphicsExtension.check("LightProbe.getEvaluationGlsl", probe);
        }
        int length = Math.toIntExact(bytes[0]);
        if (length == 0) {
            return "";
        }
        byte[] destination = new byte[length];
        GraphicsExtension.check("LightProbe.getEvaluationGlsl", NativeEngineLayerRoutes
                .lightProbeExtCopyEvaluationGlsl(destination, bytes));
        return new String(destination, 0, Math.toIntExact(bytes[0]), StandardCharsets.UTF_8);
    }

    /** @return where the probe sits */
    public Vector3 getPosition() {
        float[] position = new float[3];
        GraphicsExtension.check("LightProbe.getPosition",
                NativeEngineLayerRoutes.lightProbeExtGetPosition(open(), position));
        return new Vector3(position[0], position[1], position[2]);
    }

    /**
     * Moves the probe.
     *
     * @param position where it sits
     */
    public void setPosition(Vector3 position) {
        GraphicsExtension.check("LightProbe.setPosition",
                NativeEngineLayerRoutes.lightProbeExtSetPosition(open(),
                        EngineValues.floats(position, "position")));
    }

    /**
     * Returns one spherical-harmonic coefficient.
     *
     * @param index which coefficient, from zero to {@link #CoefficientCount} minus one
     * @return the coefficient, per channel
     */
    public Vector3 getCoefficient(int index) {
        float[] value = new float[3];
        GraphicsExtension.check("LightProbe.getCoefficient",
                NativeEngineLayerRoutes.lightProbeExtGetCoefficient(open(), index, value));
        return new Vector3(value[0], value[1], value[2]);
    }

    /**
     * Sets one spherical-harmonic coefficient.
     *
     * @param index which coefficient, from zero to {@link #CoefficientCount} minus one
     * @param value the coefficient, per channel
     */
    public void setCoefficient(int index, Vector3 value) {
        GraphicsExtension.check("LightProbe.setCoefficient",
                NativeEngineLayerRoutes.lightProbeExtSetCoefficient(open(), index,
                        EngineValues.floats(value, "value")));
    }

    /**
     * Returns every coefficient, in order.
     *
     * @return the coefficients
     */
    public List<Vector3> getCoefficients() {
        long probe = open();
        long[] count = new long[1];
        int size = NativeEngineLayerRoutes
                .lightProbeExtCopyCoefficients(probe, new float[0], count);
        if (size != RESULT_BUFFER_TOO_SMALL) {
            GraphicsExtension.check("LightProbe.getCoefficients", size);
        }
        int coefficients = Math.toIntExact(count[0]);
        float[] destination = new float[Math.multiplyExact(coefficients, 3)];
        GraphicsExtension.check("LightProbe.getCoefficients",
                NativeEngineLayerRoutes.lightProbeExtCopyCoefficients(probe, destination, count));
        List<Vector3> read = new ArrayList<>(coefficients);
        for (int index = 0; index < coefficients; index++) {
            read.add(new Vector3(destination[index * 3], destination[index * 3 + 1],
                    destination[index * 3 + 2]));
        }
        return List.copyOf(read);
    }

    /**
     * Returns the irradiance arriving on a surface with a given normal.
     *
     * <p>Irradiance, not outgoing radiance, and never negative.
     *
     * @param normal the surface normal
     * @return the irradiance per channel
     */
    public Vector3 getIrradiance(Vector3 normal) {
        float[] irradiance = new float[3];
        GraphicsExtension.check("LightProbe.getIrradiance",
                NativeEngineLayerRoutes.lightProbeExtIrradiance(open(),
                        EngineValues.floats(normal, "normal"), irradiance));
        return new Vector3(irradiance[0], irradiance[1], irradiance[2]);
    }

    /**
     * Stores how far away an occluder is in one direction.
     *
     * <p>Both distances are floored at zero.
     *
     * @param direction which direction, from zero to {@link #VisibilityDirectionCount} minus one
     * @param meanDistance the mean occluder distance
     * @param meanSquaredDistance the mean squared occluder distance
     */
    public void setVisibility(int direction, float meanDistance, float meanSquaredDistance) {
        GraphicsExtension.check("LightProbe.setVisibility",
                NativeEngineLayerRoutes.lightProbeExtSetVisibility(open(), direction,
                        meanDistance, meanSquaredDistance));
    }

    /**
     * Returns the mean occluder distance in one direction.
     *
     * @param direction which direction
     * @return the mean distance
     */
    public float getVisibilityMean(int direction) {
        float[] value = new float[1];
        GraphicsExtension.check("LightProbe.getVisibilityMean",
                NativeEngineLayerRoutes.lightProbeExtGetVisibilityMean(open(), direction, value));
        return value[0];
    }

    /**
     * Returns the mean squared occluder distance in one direction.
     *
     * @param direction which direction
     * @return the mean squared distance
     */
    public float getVisibilityMeanSquared(int direction) {
        float[] value = new float[1];
        GraphicsExtension.check("LightProbe.getVisibilityMeanSquared",
                NativeEngineLayerRoutes.lightProbeExtGetVisibilityMeanSquared(open(), direction,
                        value));
        return value[0];
    }

    /**
     * Reports whether the probe carries visibility data at all.
     *
     * @return whether any direction has been given a distance
     */
    public boolean hasVisibility() {
        boolean[] has = new boolean[1];
        GraphicsExtension.check("LightProbe.hasVisibility",
                NativeEngineLayerRoutes.lightProbeExtHasVisibility(open(), has));
        return has[0];
    }

    /**
     * Returns how much of the probe's light reaches a point in a direction.
     *
     * <p><strong>Answers one when the probe has no visibility data, and one when the distance is
     * not positive.</strong> Both mean "nothing is known to be in the way", which is the safe
     * answer rather than an error, and is why neither is a refusal.
     *
     * @param direction the direction from the probe
     * @param distance how far away the shaded point is
     * @return the weight, between zero and one
     */
    public float getVisibilityWeight(Vector3 direction, float distance) {
        float[] weight = new float[1];
        GraphicsExtension.check("LightProbe.getVisibilityWeight",
                NativeEngineLayerRoutes.lightProbeExtVisibilityWeight(open(),
                        EngineValues.floats(direction, "direction"), distance, weight));
        return weight[0];
    }

    /**
     * Reports whether every coefficient is zero.
     *
     * @return whether the probe holds no light
     */
    public boolean isZero() {
        boolean[] zero = new boolean[1];
        GraphicsExtension.check("LightProbe.isZero",
                NativeEngineLayerRoutes.lightProbeExtIsZero(open(), zero));
        return zero[0];
    }

    /**
     * Multiplies every coefficient by a factor.
     *
     * @param factor the multiplier
     */
    public void scale(float factor) {
        GraphicsExtension.check("LightProbe.scale",
                NativeEngineLayerRoutes.lightProbeExtScale(open(), factor));
    }

    /**
     * Overwrites this probe with another's value.
     *
     * @param source the probe to copy
     */
    public void copyFrom(LightProbe source) {
        Objects.requireNonNull(source, "source");
        GraphicsExtension.check("LightProbe.copyFrom",
                NativeEngineLayerRoutes.lightProbeExtCopyFrom(open(), source.open()));
    }

    /**
     * Reports whether two probes hold the same value.
     *
     * <p>Deliberately not {@code equals}: this object owns a native handle and has identity, so
     * two Java probes are the same object or they are not. This is the separate question of
     * whether their <em>contents</em> match, which is CNA's own comparison.
     *
     * @param other the probe to compare with
     * @return whether they hold the same value
     */
    public boolean matches(LightProbe other) {
        Objects.requireNonNull(other, "other");
        boolean[] equal = new boolean[1];
        GraphicsExtension.check("LightProbe.matches",
                NativeEngineLayerRoutes.lightProbeExtEquals(open(), other.open(), equal));
        return equal[0];
    }

    /** Releases the probe. Closing twice is a no-op. */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        GraphicsExtension.check("LightProbe.close",
                NativeEngineLayerRoutes.lightProbeExtDestroy(handle));
    }

    /** Adopts a probe the engine layer created and handed over. */
    static LightProbe adopt(long handle) {
        return new LightProbe(handle);
    }

    /** The native handle, for the volume that copies into or out of a probe. */
    long handle() {
        return open();
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This LightProbe is closed");
            }
        }
        return handle;
    }
}
