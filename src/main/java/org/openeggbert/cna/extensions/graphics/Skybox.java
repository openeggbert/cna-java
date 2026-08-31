package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.TextureCube;
import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.Objects;

/**
 * Draws a cube map as the sky behind everything else.
 *
 * <p>A CNA extension. XNA games drew a skybox as a cube mesh with a custom effect and depth
 * writing turned off; this is that, plus the three controls a scene actually wants:
 * {@link #setYaw} to turn the sky without rotating the world, {@link #setIntensity} to match it
 * to the rest of the lighting, and {@link #setTint} to shift its colour.
 *
 * <p><strong>Two ways to attach an environment, and they differ in who owns it.</strong>
 * {@link #setEnvironment} borrows -- the caller keeps the cube map and must keep it alive --
 * while {@link #takeEnvironment} <em>consumes</em> it: on success the skybox owns the cube map
 * and the Java facade stops being its owner, so disposing that facade afterwards does nothing
 * rather than freeing something twice. A failed transfer leaves the caller owning what it always
 * owned.
 *
 * <p>{@link #computeViewRay} is the direction one screen pixel looks along, which is how a game
 * samples the same sky in its own shader or picks what the player is looking at.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class Skybox implements AutoCloseable {

    private final long handle;
    // What the skybox was given and does not own. Retained so it cannot be collected while the
    // skybox names it; cleared when ownership is handed over, because then it is not ours.
    private TextureCube borrowedEnvironment;
    private boolean closed;

    private Skybox(long handle, TextureCube environment) {
        this.handle = handle;
        this.borrowedEnvironment = environment;
    }

    /**
     * Creates a skybox over a borrowed environment.
     *
     * @param graphicsDevice the device to draw with
     * @param environment the cube map to draw, or {@code null} for none yet; borrowed and
     *        retained here
     * @return the skybox, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static Skybox create(GraphicsDevice graphicsDevice, TextureCube environment) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] skybox = new long[1];
        GraphicsExtension.check("Skybox.create",
                NativeEngineLayerRoutes.skyboxCreate(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice),
                        environment == null ? 0L
                                : NativeBindings.nativeResourceHandle(environment), skybox));
        return new Skybox(skybox[0], environment);
    }

    /**
     * Returns the direction one screen point looks along.
     *
     * <p>Pure, and it takes the yaw as an argument rather than reading a skybox's, so a game can
     * ask about a sky it has not built yet.
     *
     * @param view the camera's view matrix
     * @param projection the camera's projection matrix
     * @param ndcX the horizontal device coordinate, from minus one to one
     * @param ndcY the vertical device coordinate, from minus one to one
     * @param yaw how far the sky is turned, in radians
     * @return the direction
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static Vector3 computeViewRay(Matrix view, Matrix projection, float ndcX, float ndcY,
            float yaw) {
        GraphicsExtension.requireBackend();
        float[] direction = new float[3];
        GraphicsExtension.check("Skybox.computeViewRay",
                NativeEngineLayerRoutes.skyboxComputeViewRay(
                        EngineValues.floats(view, "view"),
                        EngineValues.floats(projection, "projection"), ndcX, ndcY, yaw,
                        direction));
        return new Vector3(direction[0], direction[1], direction[2]);
    }

    /**
     * Reports whether this renderer can draw the sky.
     *
     * @return whether the shader exists and links
     */
    public boolean isSupported() {
        boolean[] supported = new boolean[1];
        GraphicsExtension.check("Skybox.isSupported",
                NativeEngineLayerRoutes.skyboxIsSupported(open(), supported));
        return supported[0];
    }

    /**
     * Draws the sky over the whole viewport.
     *
     * @param view the camera's view matrix
     * @param projection the camera's projection matrix
     * @param width the viewport width in pixels
     * @param height the viewport height in pixels
     */
    public void draw(Matrix view, Matrix projection, int width, int height) {
        GraphicsExtension.check("Skybox.draw",
                NativeEngineLayerRoutes.skyboxDraw(open(),
                        EngineValues.floats(view, "view"),
                        EngineValues.floats(projection, "projection"), width, height));
    }

    /**
     * Attaches an environment the caller keeps.
     *
     * <p>Borrowed: the caller disposes the cube map, and must not do so while the skybox still
     * names it.
     *
     * @param environment the cube map, or {@code null} for none
     */
    public void setEnvironment(TextureCube environment) {
        GraphicsExtension.check("Skybox.setEnvironment",
                NativeEngineLayerRoutes.skyboxSetEnvironment(open(),
                        environment == null ? 0L
                                : NativeBindings.nativeResourceHandle(environment)));
        synchronized (this) {
            borrowedEnvironment = environment;
        }
    }

    /**
     * Attaches an environment and takes ownership of it.
     *
     * <p><strong>The cube map is consumed.</strong> On success the skybox owns it, the Java
     * facade stops being its owner, and disposing that facade afterwards does nothing -- which
     * is what stops a second release becoming a double free. A failed transfer leaves the caller
     * owning what it always owned, so the facade is still usable and still has to be disposed.
     *
     * @param environment the cube map to hand over
     */
    public void takeEnvironment(TextureCube environment) {
        Objects.requireNonNull(environment, "environment");
        GraphicsExtension.check("Skybox.takeEnvironment",
                NativeEngineLayerRoutes.skyboxSetOwnedEnvironment(open(),
                        NativeBindings.nativeResourceHandle(environment)));
        // Only after CNA agreed to take it. A transfer that threw above leaves the caller's
        // facade owning its handle, which is the whole reason this line is here and not before.
        NativeBindings.surrenderResource(environment);
        synchronized (this) {
            borrowedEnvironment = null;
        }
    }

    /**
     * Returns the environment the skybox was lent.
     *
     * <p>The cube map handed to {@link #setEnvironment} or to {@link #create}, which this object
     * retained. {@code null} after {@link #takeEnvironment}, because the skybox owns that one and
     * there is no Java facade for it any more.
     *
     * @return the borrowed cube map, or {@code null}
     */
    public synchronized TextureCube getEnvironment() {
        open();
        return borrowedEnvironment;
    }

    /** @return how far the sky is turned, in radians */
    public float getYaw() {
        float[] radians = new float[1];
        GraphicsExtension.check("Skybox.getYaw",
                NativeEngineLayerRoutes.skyboxGetYaw(open(), radians));
        return radians[0];
    }

    /**
     * Turns the sky without rotating the world.
     *
     * @param radians how far to turn it
     */
    public void setYaw(float radians) {
        GraphicsExtension.check("Skybox.setYaw",
                NativeEngineLayerRoutes.skyboxSetYaw(open(), radians));
    }

    /** @return the multiplier on the sky's radiance */
    public float getIntensity() {
        float[] intensity = new float[1];
        GraphicsExtension.check("Skybox.getIntensity",
                NativeEngineLayerRoutes.skyboxGetIntensity(open(), intensity));
        return intensity[0];
    }

    /**
     * Sets the multiplier on the sky's radiance.
     *
     * @param intensity the multiplier
     */
    public void setIntensity(float intensity) {
        GraphicsExtension.check("Skybox.setIntensity",
                NativeEngineLayerRoutes.skyboxSetIntensity(open(), intensity));
    }

    /** @return the colour the sky is multiplied by */
    public Vector3 getTint() {
        float[] tint = new float[3];
        GraphicsExtension.check("Skybox.getTint",
                NativeEngineLayerRoutes.skyboxGetTint(open(), tint));
        return new Vector3(tint[0], tint[1], tint[2]);
    }

    /**
     * Shifts the sky's colour.
     *
     * @param tint the colour to multiply it by
     */
    public void setTint(Vector3 tint) {
        GraphicsExtension.check("Skybox.setTint",
                NativeEngineLayerRoutes.skyboxSetTint(open(),
                        EngineValues.floats(tint, "tint")));
    }

    /**
     * Releases the skybox, and the environment only if it was handed over.
     *
     * <p>Closing twice is a no-op.
     */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
            borrowedEnvironment = null;
        }
        GraphicsExtension.check("Skybox.close",
                NativeEngineLayerRoutes.skyboxDestroy(handle));
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This Skybox is closed");
            }
        }
        return handle;
    }
}
