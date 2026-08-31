package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.Objects;

/**
 * Projects a texture onto whatever the depth buffer says is there.
 *
 * <p>A CNA extension. A bullet hole, a puddle, a painted marking: the decal is a box in the
 * world, and every pixel whose reconstructed position falls inside it takes the decal's texture.
 * That needs the depth and normal buffers -- {@link #setPrepassInputs} -- and the camera the
 * depth was rendered with, because the reconstruction is the whole technique.
 *
 * <p>{@link #getMaxSlopeAngle()} is what stops a decal smearing across a wall it was meant to sit
 * on the floor beside.
 *
 * <p><strong>Not a {@link PostProcessPass}, however CNA's header reads.</strong> The header says
 * to ask {@code cna_post_process_pass_is_supported} about a decal pass and to release it with
 * {@code cna_post_process_pass_destroy}; measured in
 * {@code tools/native-abi/probes/engine_layer_families.c}, a {@code CNA_DecalPassHandle} is
 * refused by both with {@code INVALID_HANDLE}, and by {@code cna_post_process_pass_copy_name}
 * too. Only its own routes accept it, so this is its own type and it draws itself rather than
 * being appended to a pipeline. Recorded as {@code JAVA-UPSTREAM-008}; a caller following the
 * header leaks the pass.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class DecalPass implements AutoCloseable {

    private final long handle;
    private boolean closed;

    private DecalPass(long handle) {
        this.handle = handle;
    }

    /**
     * Creates the pass on a device.
     *
     * <p>Succeeds on a renderer that cannot run it; unlike every other pass there is no route
     * that will say so, for the reason above.
     *
     * @param graphicsDevice the device to compile on
     * @return the pass, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static DecalPass create(GraphicsDevice graphicsDevice) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] pass = new long[1];
        GraphicsExtension.check("DecalPass.create",
                NativeEngineLayerRoutes.decalPassCreate(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), pass));
        return new DecalPass(pass[0]);
    }

    /**
     * Returns the pass's MaxSlopeAngle.
     *
     * @return the value
     */
    public float getMaxSlopeAngle() {
        return readFloat("DecalPass.getMaxSlopeAngle",
                NativeEngineLayerRoutes::decalPassGetMaxSlopeAngle);
    }

    /**
     * Sets the pass's MaxSlopeAngle.
     *
     * <p>The value, clamped to zero through a right angle in radians -- a decal cannot project onto a surface facing further away than perpendicular.
     *
     * @param value the value
     */
    public void setMaxSlopeAngle(float value) {
        GraphicsExtension.check("DecalPass.setMaxSlopeAngle",
                NativeEngineLayerRoutes.decalPassSetMaxSlopeAngle(open(), value));
    }

    /**
     * Returns the pass's Opacity.
     *
     * @return the value
     */
    public float getOpacity() {
        return readFloat("DecalPass.getOpacity",
                NativeEngineLayerRoutes::decalPassGetOpacity);
    }

    /**
     * Sets the pass's Opacity.
     *
     * <p>The value, clamped to zero through one.
     *
     * @param value the value
     */
    public void setOpacity(float value) {
        GraphicsExtension.check("DecalPass.setOpacity",
                NativeEngineLayerRoutes.decalPassSetOpacity(open(), value));
    }

    /**
     * Returns the pass's Tint.
     *
     * @return the value
     */
    public Vector3 getTint() {
        float[] value = readVector("DecalPass.getTint", 3,
                NativeEngineLayerRoutes::decalPassGetTint);
        return new Vector3(value[0], value[1], value[2]);
    }

    /**
     * Sets the pass's Tint.
     *
     * <p>The value, stored as given.
     *
     * @param value the value
     */
    public void setTint(Vector3 value) {
        GraphicsExtension.check("DecalPass.setTint",
                NativeEngineLayerRoutes.decalPassSetTint(open(),
                        EngineValues.floats(value, "value")));
    }

    /**
     * Gives the pass the depth and normal buffers it reconstructs positions from.
     *
     * <p>Both are borrowed and neither is retained here.
     *
     * @param depth the depth texture, or {@code null} for none
     * @param normals the normal texture, or {@code null} for none
     */
    public void setPrepassInputs(Texture2D depth, Texture2D normals) {
        GraphicsExtension.check("DecalPass.setPrepassInputs",
                NativeEngineLayerRoutes.decalPassSetPrepassInputs(open(),
                        handleOrNone(depth), handleOrNone(normals)));
    }

    /**
     * Tells the pass which camera the depth buffer was rendered with.
     *
     * <p>Not a preference: the reconstruction from depth to world position is only correct for
     * the camera that produced the depth. A far plane that is not positive is ignored, because
     * the unprojection divides by it -- so a bad value leaves the previous camera in place
     * rather than breaking the pass.
     *
     * @param view the view matrix
     * @param projection the projection matrix
     * @param farPlane the far plane distance
     */
    public void setCamera(Matrix view, Matrix projection, float farPlane) {
        GraphicsExtension.check("DecalPass.setCamera",
                NativeEngineLayerRoutes.decalPassSetCamera(open(),
                        EngineValues.floats(view, "view"),
                        EngineValues.floats(projection, "projection"), farPlane));
    }

    /**
     * Projects one decal.
     *
     * @param decal the texture to project; borrowed for the call
     * @param decalWorld the decal box's world transform
     * @param width the destination width in pixels
     * @param height the destination height in pixels
     */
    public void draw(Texture2D decal, Matrix decalWorld, int width, int height) {
        Objects.requireNonNull(decal, "decal");
        GraphicsExtension.check("DecalPass.draw",
                NativeEngineLayerRoutes.decalPassDraw(open(), handleOrNone(decal),
                        EngineValues.floats(decalWorld, "decalWorld"), width, height));
    }

    /**
     * Reports whether a position in the decal's own space falls inside its box.
     *
     * <p>The test the shader performs per pixel, exposed so a game can place a decal against a
     * number rather than by looking at it.
     *
     * @param decalLocalPosition the position, in the decal's own space
     * @return whether the decal covers it
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static boolean isInsideDecalBox(Vector3 decalLocalPosition) {
        GraphicsExtension.requireBackend();
        boolean[] inside = new boolean[1];
        GraphicsExtension.check("DecalPass.isInsideDecalBox",
                NativeEngineLayerRoutes.decalPassIsInsideDecalBox(
                        EngineValues.floats(decalLocalPosition, "decalLocalPosition"), inside));
        return inside[0];
    }

    /** Releases the pass. Closing twice is a no-op. */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        GraphicsExtension.check("DecalPass.close",
                NativeEngineLayerRoutes.decalPassDestroy(handle));
    }

    /** A float CNA answers about the pass. */
    @FunctionalInterface
    private interface FloatRoute {
        int call(long pass, float[] answer);
    }

    private float readFloat(String operation, FloatRoute route) {
        float[] answer = new float[1];
        GraphicsExtension.check(operation, route.call(open(), answer));
        return answer[0];
    }

    private float[] readVector(String operation, int leaves, FloatRoute route) {
        float[] answer = new float[leaves];
        GraphicsExtension.check(operation, route.call(open(), answer));
        return answer;
    }

    private static long handleOrNone(Texture2D texture) {
        return texture == null ? 0L : NativeBindings.nativeResourceHandle(texture);
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This DecalPass is closed");
            }
        }
        return handle;
    }
}
