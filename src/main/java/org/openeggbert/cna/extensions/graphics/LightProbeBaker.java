package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.Objects;

/**
 * Captures what a light probe sees, by rendering the scene once per cube face.
 *
 * <p>A CNA extension, and the thing that <em>fills</em> a {@link LightProbe} or a
 * {@link LightProbeVolume}: a probe holds irradiance, and irradiance has to come from somewhere.
 *
 * <p><strong>Whether a renderer can capture at all is measured, not asked.</strong> No renderer
 * publishes "can bind an offscreen target and read it back", and the two do not come together, so
 * CNA captures one probe when the baker is created and remembers whether it worked. That
 * measurement is {@link #isSupported()}, and it is {@code false} on the headless renderer, which
 * binds happily and then refuses the readback.
 *
 * <p><strong>CNA's own bake routes are deliberately not projected.</strong> They take a C callback
 * that CNA runs once per face, and on a renderer that cannot capture the callback is never entered
 * -- measured, in {@code tools/native-abi/probes/light_probe_bake.c}: all three bake routes refuse
 * with {@code INVALID_STATE} and draw zero faces. A JNI trampoline for them would therefore be
 * code no test here could execute, which is a worse thing to ship than an absence. What is here
 * instead is everything a game needs to capture the six faces itself: {@link #isSupported()} to
 * know whether CNA's own bake would have worked, {@link #getFaceSize()} for the target to render
 * into, {@link #faceView(int, Vector3)} for each face's camera, and the two planes that go with a
 * ninety-degree square projection.
 *
 * <p><strong>Ownership.</strong> The native baker is OWNED and released by {@link #close()}. The
 * device is BORROWED and outlives it.
 */
public final class LightProbeBaker implements AutoCloseable {

    private long handle;
    private boolean closed;

    private LightProbeBaker(long handle) {
        this.handle = handle;
    }

    /**
     * Creates a baker at CNA's default face size.
     *
     * @param graphicsDevice the device to capture with
     * @return the baker, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static LightProbeBaker create(GraphicsDevice graphicsDevice) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] created = new long[1];
        GraphicsExtension.check("LightProbeBaker.create",
                NativeEngineLayerRoutes.lightProbeBakerCreate(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), created));
        return new LightProbeBaker(created[0]);
    }

    /**
     * Creates a baker at a chosen cube-face resolution.
     *
     * @param graphicsDevice the device to capture with
     * @param faceSize the cube-face resolution; must be positive
     * @return the baker, which the caller closes
     * @throws IllegalArgumentException for a face size below one
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static LightProbeBaker create(GraphicsDevice graphicsDevice, int faceSize) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] created = new long[1];
        GraphicsExtension.check("LightProbeBaker.create",
                NativeEngineLayerRoutes.lightProbeBakerCreateWithFaceSize(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), faceSize,
                        created));
        return new LightProbeBaker(created[0]);
    }

    /**
     * Returns how many faces one capture renders.
     *
     * <p>Six, on every baker. Asked of CNA rather than written down here, because a game sizing an
     * array by it should be sizing it by CNA's number.
     *
     * @return the face count
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static int getFaceCount() {
        GraphicsExtension.requireBackend();
        int[] count = new int[1];
        GraphicsExtension.check("LightProbeBaker.getFaceCount",
                NativeEngineLayerRoutes.lightProbeBakerFaceCount(count));
        return count[0];
    }

    /**
     * Reports whether this renderer can actually capture probes.
     *
     * <p>The measurement CNA took when this baker was created, not a guess from the renderer's
     * name. A game that bakes probes at load time asks this first and falls back to authored
     * lighting when it is {@code false}.
     *
     * @return whether capture works here
     */
    public boolean isSupported() {
        boolean[] supported = new boolean[1];
        GraphicsExtension.check("LightProbeBaker.isSupported",
                NativeEngineLayerRoutes.lightProbeBakerIsSupported(alive(), supported));
        return supported[0];
    }

    /**
     * Returns the cube-face resolution this baker captures at.
     *
     * @return the face size in texels
     */
    public int getFaceSize() {
        int[] size = new int[1];
        GraphicsExtension.check("LightProbeBaker.getFaceSize",
                NativeEngineLayerRoutes.lightProbeBakerGetFaceSize(alive(), size));
        return size[0];
    }

    /**
     * Returns the near capture distance.
     *
     * @return the near plane
     */
    public float getNearPlane() {
        float[] near = new float[1];
        GraphicsExtension.check("LightProbeBaker.getNearPlane",
                NativeEngineLayerRoutes.lightProbeBakerGetNearPlane(alive(), near));
        return near[0];
    }

    /**
     * Returns the far capture distance.
     *
     * @return the far plane
     */
    public float getFarPlane() {
        float[] far = new float[1];
        GraphicsExtension.check("LightProbeBaker.getFarPlane",
                NativeEngineLayerRoutes.lightProbeBakerGetFarPlane(alive(), far));
        return far[0];
    }

    /**
     * Sets both capture distances at once.
     *
     * <p>Both together, because they are only valid as a pair: a near plane set past the current
     * far one would have to be refused or silently reordered, and setting them in one call means
     * neither happens. A refused pair leaves both distances as they were.
     *
     * @param nearPlane the near distance; must be positive
     * @param farPlane the far distance; must exceed the near one
     * @throws IllegalArgumentException when the pair is not ordered
     */
    public void setPlanes(float nearPlane, float farPlane) {
        GraphicsExtension.check("LightProbeBaker.setPlanes",
                NativeEngineLayerRoutes.lightProbeBakerSetPlanes(alive(), nearPlane, farPlane));
    }

    /**
     * Returns the view matrix one cube face is captured with.
     *
     * <p>With {@link #getNearPlane()} and {@link #getFarPlane()} and a ninety-degree square
     * perspective, this is the whole camera for a face -- which is what lets a game capture a
     * probe itself on a renderer where CNA's own bake is refused.
     *
     * @param face the face index, from zero to {@link #getFaceCount()} minus one
     * @param position where to capture from
     * @return the face's view matrix
     * @throws IllegalArgumentException for a face outside the six
     */
    public Matrix faceView(int face, Vector3 position) {
        Objects.requireNonNull(position, "position");
        float[] view = new float[EngineValues.MATRIX_LEAVES];
        GraphicsExtension.check("LightProbeBaker.faceView",
                NativeEngineLayerRoutes.lightProbeBakerFaceView(alive(), face,
                        EngineValues.floats(position, "position"), view));
        return EngineValues.matrix(view, 0);
    }

    /**
     * Returns the projection every face is captured with.
     *
     * <p>Derived here rather than asked of CNA, which has no route for it: a cube face is a
     * ninety-degree square frustum by construction, and the two distances are the baker's own. It
     * is on this type so a game capturing faces itself does not have to rediscover the convention.
     *
     * @return the face projection
     */
    public Matrix faceProjection() {
        return Matrix.CreatePerspectiveFieldOfView((float) (Math.PI / 2.0), 1f,
                getNearPlane(), getFarPlane());
    }

    /**
     * Releases the native baker.
     *
     * <p>Marked closed only after CNA agrees, so a refused release leaves a usable baker rather
     * than an unusable one that also leaked.
     */
    @Override
    public void close() {
        if (closed) {
            return;
        }
        GraphicsExtension.check("LightProbeBaker.close",
                NativeEngineLayerRoutes.lightProbeBakerDestroy(handle));
        closed = true;
        handle = 0L;
    }

    private long alive() {
        if (closed) {
            throw new IllegalStateException("LightProbeBaker is closed");
        }
        return handle;
    }
}
