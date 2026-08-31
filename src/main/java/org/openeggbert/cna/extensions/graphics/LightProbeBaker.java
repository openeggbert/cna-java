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
 * <p><strong>The three bake routes take a callback CNA runs once per face.</strong>
 * {@link #bakeProbe}, {@link #bakeLight} and {@link #bakeVisibility} run it six times per probe,
 * with the view and projection the baker chose, and they run it only inside the call -- so the
 * Java callback is passed in for that call's duration and nothing outlives it.
 *
 * <p><strong>Draw the scene and nothing else inside the callback.</strong> The baker owns the
 * render target for the duration, and binding another one loses the face being captured. That is
 * CNA's instruction, repeated here because it is not recoverable: the capture silently comes back
 * wrong rather than failing.
 *
 * <p><strong>An exception thrown by the callback cannot stop the bake.</strong> CNA's callback
 * returns nothing and has no way to refuse, so the remaining faces are skipped instead and the
 * exception surfaces at the bake call. The probe CNA produces in that case is whatever the faces
 * that did run captured, and a caller that let an exception escape should discard it.
 *
 * <p>Everything a game needs to capture the six faces <em>itself</em> is here too, for the
 * renderers CNA cannot bake on: {@link #isSupported()} to know which case it is,
 * {@link #getFaceSize()} for the target to render into, {@link #faceView(int, Vector3)} for each
 * face's camera, and {@link #faceProjection()} for the ninety-degree square frustum.
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
     * Draws one cube face of a capture.
     *
     * <p>The one callback CNA's bake routes take. It is called six times per probe, in face
     * order, with the camera the baker chose for that face.
     */
    @FunctionalInterface
    public interface SceneDraw {

        /**
         * Draws the scene for one face.
         *
         * @param view the view matrix for this face
         * @param projection the projection matrix for this face
         */
        void draw(Matrix view, Matrix projection);
    }

    /**
     * Captures one probe at a position, drawing the scene six times.
     *
     * @param position where to capture from
     * @param draw the per-face scene callback
     * @return a new probe carrying what the six faces saw, which the caller closes
     * @throws IllegalStateException when this renderer cannot capture; {@link #isSupported()} is
     *         how to ask first
     */
    public LightProbe bakeProbe(Vector3 position, SceneDraw draw) {
        Objects.requireNonNull(position, "position");
        Objects.requireNonNull(draw, "draw");
        long[] probe = new long[1];
        int[] faces = new int[1];
        GraphicsExtension.check("LightProbeBaker.bakeProbe",
                NativeBindings.lightProbeBakerBakeProbe(alive(),
                        EngineValues.floats(position, "position"), adapt(draw), probe, faces));
        return LightProbe.adopt(probe[0]);
    }

    /**
     * Captures every probe of a volume's lighting.
     *
     * <p>Six faces per probe, so a two-by-two-by-two volume draws the scene forty-eight times.
     * Whatever visibility each probe already carried is kept: light and visibility are separate
     * bakes and either may be run without the other.
     *
     * @param volume the volume whose probes to capture
     * @param draw the per-face scene callback
     * @return how many faces were drawn, which is six times the volume's probe count
     * @throws IllegalStateException when this renderer cannot capture
     */
    public int bakeLight(LightProbeVolume volume, SceneDraw draw) {
        Objects.requireNonNull(volume, "volume");
        Objects.requireNonNull(draw, "draw");
        int[] faces = new int[1];
        GraphicsExtension.check("LightProbeBaker.bakeLight",
                NativeBindings.lightProbeBakerBakeLight(alive(), volume.handle(), adapt(draw),
                        faces));
        return faces[0];
    }

    /**
     * Captures every probe of a volume's visibility.
     *
     * <p>The other half of {@link #bakeLight}: what each probe can see rather than how bright it
     * is, which is what keeps light from leaking through a wall. Whatever lighting each probe
     * already carried is kept.
     *
     * @param volume the volume whose probes to capture
     * @param draw the per-face scene callback
     * @return how many faces were drawn
     * @throws IllegalStateException when this renderer cannot capture
     */
    public int bakeVisibility(LightProbeVolume volume, SceneDraw draw) {
        Objects.requireNonNull(volume, "volume");
        Objects.requireNonNull(draw, "draw");
        int[] faces = new int[1];
        GraphicsExtension.check("LightProbeBaker.bakeVisibility",
                NativeBindings.lightProbeBakerBakeVisibility(alive(), volume.handle(),
                        adapt(draw), faces));
        return faces[0];
    }

    /**
     * Wraps a scene callback in the shape the native boundary calls.
     *
     * <p>Two sixteen-float arrays rather than two matrices, because the trampoline builds them in
     * C and a Java type would have to be constructed there. The conversion happens here, on the
     * Java side of the boundary, where it is ordinary code.
     */
    private static java.util.function.BiConsumer<float[], float[]> adapt(SceneDraw draw) {
        return (view, projection) ->
                draw.draw(EngineValues.matrix(view, 0), EngineValues.matrix(projection, 0));
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
