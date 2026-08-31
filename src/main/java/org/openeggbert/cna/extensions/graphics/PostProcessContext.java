package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.Texture2D;
import Microsoft.Xna.Framework.Matrix;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.Objects;

/**
 * One frame's inputs and destination, as a post-process pass reads them.
 *
 * <p>A CNA extension, and the argument every pass takes: the colour to read, the depth and normals
 * and velocity a screen-space effect needs, where to write, how big it is, and the camera
 * transforms that let a pass reconstruct a world position from a depth.
 *
 * <p>All five textures are BORROWED for the length of the call, and this object retains Java
 * references to them so a context held across a frame keeps its inputs alive.
 *
 * <p><strong>The pipeline settings a pass could read are deliberately not here.</strong> CNA's
 * structure ends with a borrowed {@code const CNA_RenderPipelineSettingsEXT*}, and this projection
 * fills in the structure as CNA's own version 1 -- the size CNA documents as the mandatory prefix,
 * which every route accepts and past which CNA never reads. A pass driven from here therefore sees
 * no settings and uses its own. {@link RenderPipeline} is the path that does supply them, because
 * it owns the settings object; this one is for a game running a chain of its own.
 */
public final class PostProcessContext {

    static final int BYTE_LEAVES = 3;
    static final int INTEGRAL_LEAVES = 8;
    static final int FLOATING_LEAVES = 67;

    // The offsets of the four matrices and the size pair in CNA's flattened structure. Nothing at
    // runtime can check these: the context is write-only, and CNA has no route that reads one
    // back, so a constant naming the wrong leaf would send a pass the wrong transform silently.
    // They are pinned instead against the live header, by the generator tool tests in
    // tools/native-abi/test_verify.py, which is the only place the check can honestly live.
    private static final int WIDTH_AT = 5;
    private static final int PROJECTION_AT = 3;
    private static final int INVERSE_PROJECTION_AT = 19;
    private static final int INVERSE_VIEW_AT = 35;
    private static final int PREVIOUS_VIEW_PROJECTION_AT = 51;

    private Texture2D source;
    private Texture2D sourceDepth;
    private Texture2D sourceNormals;
    private Texture2D sourceVelocity;
    private Texture2D destination;
    private int width;
    private int height;
    private float elapsedSeconds;
    private float nearPlane;
    private float farPlane;
    private boolean hasPreviousFrame;
    private Matrix projection;
    private Matrix inverseProjection;
    private Matrix inverseView;
    private Matrix previousViewProjection;

    /**
     * Creates a context carrying CNA's own defaults: no textures, zero size, identity matrices and
     * no previous frame.
     *
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public PostProcessContext() {
        GraphicsExtension.requireBackend();
        long[] integral = new long[INTEGRAL_LEAVES];
        float[] floating = new float[FLOATING_LEAVES];
        GraphicsExtension.check("PostProcessContext",
                NativeEngineLayerRoutes.postProcessContextInit(new byte[BYTE_LEAVES], integral,
                        floating));
        width = Math.toIntExact(integral[WIDTH_AT]);
        height = Math.toIntExact(integral[WIDTH_AT + 1]);
        hasPreviousFrame = integral[WIDTH_AT + 2] != 0L;
        elapsedSeconds = floating[0];
        nearPlane = floating[1];
        farPlane = floating[2];
        projection = EngineValues.matrixAt(floating, PROJECTION_AT);
        inverseProjection = EngineValues.matrixAt(floating, INVERSE_PROJECTION_AT);
        inverseView = EngineValues.matrixAt(floating, INVERSE_VIEW_AT);
        previousViewProjection = EngineValues.matrixAt(floating, PREVIOUS_VIEW_PROJECTION_AT);
    }

    /** @return the colour input, or {@code null} */
    public Texture2D getSource() {
        return source;
    }

    /** @param value the colour input, or {@code null} for none */
    public void setSource(Texture2D value) {
        source = value;
    }

    /** @return the linear-depth input, or {@code null} */
    public Texture2D getSourceDepth() {
        return sourceDepth;
    }

    /** @param value the linear-depth input, or {@code null} when no pass reads depth */
    public void setSourceDepth(Texture2D value) {
        sourceDepth = value;
    }

    /** @return the normals input, or {@code null} */
    public Texture2D getSourceNormals() {
        return sourceNormals;
    }

    /** @param value the normals input, or {@code null} when no pass reads normals */
    public void setSourceNormals(Texture2D value) {
        sourceNormals = value;
    }

    /** @return the velocity input, or {@code null} */
    public Texture2D getSourceVelocity() {
        return sourceVelocity;
    }

    /** @param value the velocity input, or {@code null} when no pass reads velocity */
    public void setSourceVelocity(Texture2D value) {
        sourceVelocity = value;
    }

    /** @return the destination, or {@code null} for the back buffer */
    public Texture2D getDestination() {
        return destination;
    }

    /** @param value the destination render target, or {@code null} for the back buffer */
    public void setDestination(Texture2D value) {
        destination = value;
    }

    /** @return the destination width in pixels */
    public int getWidth() {
        return width;
    }

    /** @return the destination height in pixels */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the destination size in pixels.
     *
     * @param newWidth the width
     * @param newHeight the height
     */
    public void setSize(int newWidth, int newHeight) {
        width = newWidth;
        height = newHeight;
    }

    /** @return seconds elapsed since the previous frame */
    public float getElapsedSeconds() {
        return elapsedSeconds;
    }

    /** @param value seconds elapsed since the previous frame */
    public void setElapsedSeconds(float value) {
        elapsedSeconds = value;
    }

    /** @return the camera's near plane distance */
    public float getNearPlane() {
        return nearPlane;
    }

    /** @return the camera's far plane distance */
    public float getFarPlane() {
        return farPlane;
    }

    /**
     * Sets the camera's clip distances.
     *
     * @param near the near plane
     * @param far the far plane
     */
    public void setPlanes(float near, float far) {
        nearPlane = near;
        farPlane = far;
    }

    /** @return whether {@link #getPreviousViewProjection()} describes a real previous frame */
    public boolean hasPreviousFrame() {
        return hasPreviousFrame;
    }

    /** @return the camera's projection */
    public Matrix getProjection() {
        return new Matrix(projection);
    }

    /** @return the inverse of the projection */
    public Matrix getInverseProjection() {
        return new Matrix(inverseProjection);
    }

    /** @return the inverse of the camera's view */
    public Matrix getInverseView() {
        return new Matrix(inverseView);
    }

    /** @return the previous frame's view-projection, for reprojection */
    public Matrix getPreviousViewProjection() {
        return new Matrix(previousViewProjection);
    }

    /**
     * Sets the camera transforms this frame.
     *
     * <p>The inverses are taken here rather than asked for, because a caller that computed them
     * separately could hand over an inverse that does not match the projection -- and a pass
     * reconstructing a world position from a depth would then be wrong in a way no assertion
     * anywhere would catch.
     *
     * @param view the camera's view matrix
     * @param cameraProjection the camera's projection matrix
     */
    public void setCamera(Matrix view, Matrix cameraProjection) {
        Objects.requireNonNull(view, "view");
        Objects.requireNonNull(cameraProjection, "cameraProjection");
        projection = new Matrix(cameraProjection);
        inverseProjection = Matrix.Invert(cameraProjection);
        inverseView = Matrix.Invert(view);
    }

    /**
     * Records the previous frame's view-projection, so reprojecting passes can use it.
     *
     * @param value the previous frame's view-projection, or {@code null} to say there was none
     */
    public void setPreviousViewProjection(Matrix value) {
        hasPreviousFrame = value != null;
        previousViewProjection = value == null ? Matrix.getIdentity() : new Matrix(value);
    }

    /** The byte leaves CNA's structure declares, which are padding. */
    byte[] bytes() {
        return new byte[BYTE_LEAVES];
    }

    /** The integral leaves CNA's structure declares, in declaration order. */
    long[] integral() {
        return new long[] {
            handleOf(source), handleOf(sourceDepth), handleOf(sourceNormals),
            handleOf(sourceVelocity), handleOf(destination),
            width, height, hasPreviousFrame ? 1L : 0L,
        };
    }

    /** The floating leaves CNA's structure declares, in declaration order. */
    float[] floating() {
        float[] leaves = new float[FLOATING_LEAVES];
        leaves[0] = elapsedSeconds;
        leaves[1] = nearPlane;
        leaves[2] = farPlane;
        System.arraycopy(EngineValues.floats(projection, "projection"), 0, leaves,
                PROJECTION_AT, EngineValues.MATRIX_LEAVES);
        System.arraycopy(EngineValues.floats(inverseProjection, "inverseProjection"), 0, leaves,
                INVERSE_PROJECTION_AT, EngineValues.MATRIX_LEAVES);
        System.arraycopy(EngineValues.floats(inverseView, "inverseView"), 0, leaves,
                INVERSE_VIEW_AT, EngineValues.MATRIX_LEAVES);
        System.arraycopy(EngineValues.floats(previousViewProjection, "previousViewProjection"), 0,
                leaves, PREVIOUS_VIEW_PROJECTION_AT, EngineValues.MATRIX_LEAVES);
        return leaves;
    }

    private static long handleOf(Texture2D texture) {
        return texture == null ? 0L : NativeBindings.nativeResourceHandle(texture);
    }
}
