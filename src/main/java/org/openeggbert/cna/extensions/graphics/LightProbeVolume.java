package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.BoundingBox;
import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.Objects;

/**
 * A grid of {@link LightProbe}s over a box, interpolated between.
 *
 * <p>A CNA extension. One probe lights one point; a volume lights a room. The probes sit on a
 * regular grid across the box, and {@link #getIrradiance} interpolates the eight surrounding ones
 * so a moving object's ambient light changes smoothly rather than jumping between probes.
 *
 * <p><strong>Probes are stored by value.</strong> {@link #setProbe} copies into the grid and
 * {@link #copyProbeInto} copies out, so a probe handed in can be closed straight away and one
 * read out stays correct after the volume changes. That is why both take a probe to write into
 * rather than returning a new one: a volume sampled per object per frame would otherwise
 * allocate and release a native probe every time.
 *
 * <p><strong>Sampling clamps rather than refusing.</strong> A position outside the box is an
 * ordinary thing during rendering -- an object half out of a room -- and the nearest
 * interpolation is what a caller wants there. {@link #contains} is how to ask instead.
 *
 * <p>Needs no graphics device.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class LightProbeVolume implements AutoCloseable {

    /** {@code CNA_LIGHT_PROBE_VOLUME_MAX_PROBES_EXT}: the largest grid CNA will build. */
    public static final int MaxProbes = 32768;

    private final long handle;
    private boolean closed;

    private LightProbeVolume(long handle) {
        this.handle = handle;
    }

    /**
     * Creates a volume over a box.
     *
     * @param bounds the box the grid spans
     * @param countX probes along X; at least one
     * @param countY probes along Y; at least one
     * @param countZ probes along Z; at least one
     * @return the volume, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static LightProbeVolume create(BoundingBox bounds, int countX, int countY,
            int countZ) {
        GraphicsExtension.requireBackend();
        long[] volume = new long[1];
        GraphicsExtension.check("LightProbeVolume.create",
                NativeEngineLayerRoutes.lightProbeVolumeExtCreate(
                        EngineValues.floats(bounds, "bounds"), countX, countY, countZ, volume));
        return new LightProbeVolume(volume[0]);
    }

    /** @return the box the grid spans */
    public BoundingBox getBounds() {
        float[] bounds = new float[EngineValues.BOX_LEAVES];
        GraphicsExtension.check("LightProbeVolume.getBounds",
                NativeEngineLayerRoutes.lightProbeVolumeExtGetBounds(open(), bounds));
        return new BoundingBox(new Vector3(bounds[0], bounds[1], bounds[2]),
                new Vector3(bounds[3], bounds[4], bounds[5]));
    }

    /** @return how many probes there are along X */
    public int getCountX() {
        return count("LightProbeVolume.getCountX",
                NativeEngineLayerRoutes::lightProbeVolumeExtGetCountX);
    }

    /** @return how many probes there are along Y */
    public int getCountY() {
        return count("LightProbeVolume.getCountY",
                NativeEngineLayerRoutes::lightProbeVolumeExtGetCountY);
    }

    /** @return how many probes there are along Z */
    public int getCountZ() {
        return count("LightProbeVolume.getCountZ",
                NativeEngineLayerRoutes::lightProbeVolumeExtGetCountZ);
    }

    /** @return how many probes the grid holds altogether */
    public int getProbeCount() {
        return count("LightProbeVolume.getProbeCount",
                NativeEngineLayerRoutes::lightProbeVolumeExtGetProbeCount);
    }

    /**
     * Returns where one grid probe sits.
     *
     * @param x the X index, from zero
     * @param y the Y index, from zero
     * @param z the Z index, from zero
     * @return the world-space position
     */
    public Vector3 getProbePosition(int x, int y, int z) {
        float[] position = new float[3];
        GraphicsExtension.check("LightProbeVolume.getProbePosition",
                NativeEngineLayerRoutes.lightProbeVolumeExtGetProbePosition(open(), x, y, z,
                        position));
        return new Vector3(position[0], position[1], position[2]);
    }

    /**
     * Copies one grid probe into a caller's probe.
     *
     * @param x the X index, from zero
     * @param y the Y index, from zero
     * @param z the Z index, from zero
     * @param destination the probe to overwrite
     */
    public void copyProbeInto(int x, int y, int z, LightProbe destination) {
        Objects.requireNonNull(destination, "destination");
        GraphicsExtension.check("LightProbeVolume.copyProbeInto",
                NativeEngineLayerRoutes.lightProbeVolumeExtGetProbe(open(), x, y, z,
                        destination.handle()));
    }

    /**
     * Copies a probe into the grid.
     *
     * @param x the X index, from zero
     * @param y the Y index, from zero
     * @param z the Z index, from zero
     * @param probe the probe to copy in
     */
    public void setProbe(int x, int y, int z, LightProbe probe) {
        Objects.requireNonNull(probe, "probe");
        GraphicsExtension.check("LightProbeVolume.setProbe",
                NativeEngineLayerRoutes.lightProbeVolumeExtSetProbe(open(), x, y, z,
                        probe.handle()));
    }

    /**
     * Reports whether a position is inside the box.
     *
     * @param position the world-space position
     * @return whether the volume covers it
     */
    public boolean contains(Vector3 position) {
        boolean[] contains = new boolean[1];
        GraphicsExtension.check("LightProbeVolume.contains",
                NativeEngineLayerRoutes.lightProbeVolumeExtContains(open(),
                        EngineValues.floats(position, "position"), contains));
        return contains[0];
    }

    /**
     * Interpolates the eight surrounding probes into a caller's probe.
     *
     * <p>The position is clamped into the box rather than refused.
     *
     * @param position the world-space position
     * @param destination the probe to overwrite
     */
    public void sampleInto(Vector3 position, LightProbe destination) {
        Objects.requireNonNull(destination, "destination");
        GraphicsExtension.check("LightProbeVolume.sampleInto",
                NativeEngineLayerRoutes.lightProbeVolumeExtSampleProbe(open(),
                        EngineValues.floats(position, "position"), destination.handle()));
    }

    /**
     * Returns the irradiance a surface receives at a position.
     *
     * <p>The common case, and the one that needs no probe of the caller's own: interpolate and
     * evaluate in one call.
     *
     * @param position the world-space position
     * @param normal the surface normal
     * @return the irradiance per channel
     */
    public Vector3 getIrradiance(Vector3 position, Vector3 normal) {
        float[] irradiance = new float[3];
        GraphicsExtension.check("LightProbeVolume.getIrradiance",
                NativeEngineLayerRoutes.lightProbeVolumeExtIrradiance(open(),
                        EngineValues.floats(position, "position"),
                        EngineValues.floats(normal, "normal"), irradiance));
        return new Vector3(irradiance[0], irradiance[1], irradiance[2]);
    }

    /**
     * Reports whether every probe in the grid holds no light.
     *
     * @return whether the volume is empty of light
     */
    public boolean isZero() {
        boolean[] zero = new boolean[1];
        GraphicsExtension.check("LightProbeVolume.isZero",
                NativeEngineLayerRoutes.lightProbeVolumeExtIsZero(open(), zero));
        return zero[0];
    }

    /** Releases the volume and every probe in it. Closing twice is a no-op. */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        GraphicsExtension.check("LightProbeVolume.close",
                NativeEngineLayerRoutes.lightProbeVolumeExtDestroy(handle));
    }

    /** The native handle, for the debug renderer's volume gizmo. */
    long handle() {
        return open();
    }

    /** A count CNA answers about one volume. */
    @FunctionalInterface
    private interface CountRoute {
        int call(long volume, int[] answer);
    }

    private int count(String operation, CountRoute route) {
        int[] answer = new int[1];
        GraphicsExtension.check(operation, route.call(open(), answer));
        return answer[0];
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This LightProbeVolume is closed");
            }
        }
        return handle;
    }
}
