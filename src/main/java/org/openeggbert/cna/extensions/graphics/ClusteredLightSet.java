package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.BoundingSphere;
import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The lights a clustered forward pass will sort into a grid.
 *
 * <p>A CNA extension. Clustered forward lighting is how a modern renderer draws a scene with
 * hundreds of lights without a deferred pass: the view frustum is cut into clusters, each light
 * is sorted into the clusters it touches, and a shader looks up only the handful that reach the
 * pixel it is shading. This is the first of the four objects that does -- the set is the lights,
 * {@link ClusteredLightGrid} is the cut, {@link ClusteredLightAssignment} is the sorting, and
 * {@link ClusteredLightBuffer} is what the shader reads.
 *
 * <p>The set keeps each light's bounding sphere beside it, which is what the assignment sorts, so
 * a game builds the scene once here rather than describing every light twice.
 *
 * <p><strong>The parameter CNA calls a game is a graphics device.</strong> CNA's header names it
 * {@code game} and documents it as "the owning game"; the library resolves it as a graphics
 * device and refuses an actual game handle. Measured in
 * {@code tools/native-abi/probes/engine_layer_families.c} and recorded as
 * {@code JAVA-UPSTREAM-005}. This projection takes the device, because that is what works.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class ClusteredLightSet implements AutoCloseable {

    /** CNA's own result for a buffer that could not hold the answer. */
    private static final int RESULT_BUFFER_TOO_SMALL = 14;

    /** A clustered light's leaves, per element, in each of the three carriers. */
    private static final int LIGHT_BYTES = 3;
    private static final int LIGHT_INTEGRAL = 2;
    private static final int LIGHT_FLOATING = 13;

    /** A bounding sphere is its centre and its radius. */
    private static final int SPHERE_LEAVES = 4;

    private final long handle;
    private boolean closed;

    private ClusteredLightSet(long handle) {
        this.handle = handle;
    }

    /**
     * Creates an empty set.
     *
     * @param graphicsDevice the device the set is parented to
     * @return the set, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static ClusteredLightSet create(GraphicsDevice graphicsDevice) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] set = new long[1];
        GraphicsExtension.check("ClusteredLightSet.create",
                NativeEngineLayerRoutes.clusteredLightSetCreate(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), set));
        return new ClusteredLightSet(set[0]);
    }

    /**
     * Adds a light and returns its index.
     *
     * @param light the light to add; it must be usable
     * @return the light's index in the set
     */
    public int add(ClusteredLight light) {
        Objects.requireNonNull(light, "light");
        int[] index = new int[1];
        GraphicsExtension.check("ClusteredLightSet.add", NativeEngineLayerRoutes
                .clusteredLightSetAdd(open(), new byte[LIGHT_BYTES], light.integral(),
                        light.floating(), index));
        return index[0];
    }

    /**
     * Adds a point light, which CNA converts to a clustered one.
     *
     * @param light the light to add
     * @return the light's index in the set
     */
    public int add(PointLight light) {
        Objects.requireNonNull(light, "light");
        int[] index = new int[1];
        GraphicsExtension.check("ClusteredLightSet.add", NativeEngineLayerRoutes
                .clusteredLightSetAddPoint(open(), new byte[3], light.integral(),
                        light.floating(), index));
        return index[0];
    }

    /**
     * Adds a spot light, which CNA converts to a clustered one.
     *
     * @param light the light to add
     * @return the light's index in the set
     */
    public int add(SpotLight light) {
        Objects.requireNonNull(light, "light");
        int[] index = new int[1];
        GraphicsExtension.check("ClusteredLightSet.add", NativeEngineLayerRoutes
                .clusteredLightSetAddSpot(open(), new byte[3], light.integral(),
                        light.floating(), index));
        return index[0];
    }

    /**
     * Replaces the light at an index.
     *
     * @param index which light
     * @param light the light to put there
     */
    public void replaceAt(int index, ClusteredLight light) {
        Objects.requireNonNull(light, "light");
        GraphicsExtension.check("ClusteredLightSet.replaceAt", NativeEngineLayerRoutes
                .clusteredLightSetReplaceAt(open(), index, new byte[LIGHT_BYTES],
                        light.integral(), light.floating()));
    }

    /**
     * Removes the light at an index.
     *
     * <p>Every later light moves down one, so an index taken before a removal names a different
     * light after it.
     *
     * @param index which light
     */
    public void removeAt(int index) {
        GraphicsExtension.check("ClusteredLightSet.removeAt",
                NativeEngineLayerRoutes.clusteredLightSetRemoveAt(open(), index));
    }

    /** Removes every light. */
    public void clear() {
        GraphicsExtension.check("ClusteredLightSet.clear",
                NativeEngineLayerRoutes.clusteredLightSetClear(open()));
    }

    /**
     * Returns how many lights the set holds.
     *
     * @return the count
     */
    public int getCount() {
        int[] count = new int[1];
        GraphicsExtension.check("ClusteredLightSet.getCount",
                NativeEngineLayerRoutes.clusteredLightSetGetCount(open(), count));
        return count[0];
    }

    /**
     * Reports whether the set holds no lights.
     *
     * @return whether it is empty
     */
    public boolean isEmpty() {
        boolean[] empty = new boolean[1];
        GraphicsExtension.check("ClusteredLightSet.isEmpty",
                NativeEngineLayerRoutes.clusteredLightSetIsEmpty(open(), empty));
        return empty[0];
    }

    /**
     * Returns the light at an index.
     *
     * @param index which light
     * @return the light, as CNA holds it
     */
    public ClusteredLight getAt(int index) {
        long[] integral = new long[LIGHT_INTEGRAL];
        float[] floating = new float[LIGHT_FLOATING];
        GraphicsExtension.check("ClusteredLightSet.getAt", NativeEngineLayerRoutes
                .clusteredLightSetGetAt(open(), index, new byte[LIGHT_BYTES], integral, floating));
        return ClusteredLight.read(integral, floating);
    }

    /**
     * Returns every light, in the set's own order.
     *
     * @return the lights
     */
    public List<ClusteredLight> getLights() {
        long set = open();
        long[] count = new long[1];
        // A zero-capacity probe reports the count and writes nothing, so BUFFER_TOO_SMALL is the
        // expected answer to the first call rather than a failure.
        int probe = NativeEngineLayerRoutes.clusteredLightSetCopyLights(
                set, new byte[0], new long[0], new float[0], count);
        if (probe != RESULT_BUFFER_TOO_SMALL) {
            GraphicsExtension.check("ClusteredLightSet.getLights", probe);
        }
        int lights = Math.toIntExact(count[0]);
        if (lights == 0) {
            return List.of();
        }
        // Every carrier is sized for the same number of elements: the adapter derives the count
        // from one of them and refuses a set that disagrees.
        long[] integral = new long[Math.multiplyExact(lights, LIGHT_INTEGRAL)];
        float[] floating = new float[Math.multiplyExact(lights, LIGHT_FLOATING)];
        GraphicsExtension.check("ClusteredLightSet.getLights", NativeEngineLayerRoutes
                .clusteredLightSetCopyLights(set, new byte[Math.multiplyExact(lights, LIGHT_BYTES)],
                        integral, floating, count));
        List<ClusteredLight> copied = new ArrayList<>(lights);
        for (int index = 0; index < lights; index++) {
            copied.add(ClusteredLight.read(integral, index * LIGHT_INTEGRAL,
                    floating, index * LIGHT_FLOATING));
        }
        return List.copyOf(copied);
    }

    /**
     * Returns the bounding sphere CNA keeps for the light at an index.
     *
     * <p>This is what an assignment sorts, and it is CNA's own -- derived from the light's
     * position and range rather than supplied.
     *
     * @param index which light
     * @return the sphere
     */
    public BoundingSphere getBoundsAt(int index) {
        float[] sphere = new float[SPHERE_LEAVES];
        GraphicsExtension.check("ClusteredLightSet.getBoundsAt",
                NativeEngineLayerRoutes.clusteredLightSetGetBoundsAt(open(), index, sphere));
        return sphere(sphere, 0);
    }

    /**
     * Returns every light's bounding sphere, in light-index order.
     *
     * <p>The list an assignment takes: {@link ClusteredLightAssignment#assign} sorts exactly
     * this, so a game builds the set once and hands the result straight over.
     *
     * @return the spheres
     */
    public List<BoundingSphere> getBounds() {
        long set = open();
        long[] count = new long[1];
        int probe = NativeEngineLayerRoutes
                .clusteredLightSetCopyBounds(set, new float[0], count);
        if (probe != RESULT_BUFFER_TOO_SMALL) {
            GraphicsExtension.check("ClusteredLightSet.getBounds", probe);
        }
        int lights = Math.toIntExact(count[0]);
        if (lights == 0) {
            return List.of();
        }
        float[] spheres = new float[Math.multiplyExact(lights, SPHERE_LEAVES)];
        GraphicsExtension.check("ClusteredLightSet.getBounds",
                NativeEngineLayerRoutes.clusteredLightSetCopyBounds(set, spheres, count));
        List<BoundingSphere> copied = new ArrayList<>(lights);
        for (int index = 0; index < lights; index++) {
            copied.add(sphere(spheres, index * SPHERE_LEAVES));
        }
        return List.copyOf(copied);
    }

    /** Releases the set. Closing twice is a no-op. */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        GraphicsExtension.check("ClusteredLightSet.close",
                NativeEngineLayerRoutes.clusteredLightSetDestroy(handle));
    }

    /** The native handle, for the objects in this package that consume a set. */
    long handle() {
        return open();
    }

    private static BoundingSphere sphere(float[] packed, int base) {
        return new BoundingSphere(
                new Vector3(packed[base], packed[base + 1], packed[base + 2]), packed[base + 3]);
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This ClusteredLightSet is closed");
            }
        }
        return handle;
    }
}
