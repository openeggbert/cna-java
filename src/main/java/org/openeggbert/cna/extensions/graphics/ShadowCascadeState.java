package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Matrix;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The cascaded-shadow state an effect reads for one frame.
 *
 * <p>A CNA extension, and the piece that connects a {@link CascadedShadowMap} to an effect that
 * samples it: the map knows where each cascade's atlas region is and how far it reaches, and the
 * effect needs both to pick a cascade per pixel. {@link #of} takes one from the other, which is
 * the whole reason this type exists rather than a game copying four transforms by hand every
 * frame.
 *
 * <p><strong>Fixed at four cascades, always.</strong> CNA's structure carries four transforms and
 * four splits whatever {@link #getCount()} says; the unused entries are left at their defaults
 * rather than removed, so the layout never changes. That is why {@link #getCount()} rather than
 * the list length is what says how many are meaningful, and why a count of zero -- which disables
 * cascaded shadows -- still carries four identity transforms.
 *
 * <p>Immutable, and owns nothing: it is a value that crosses to CNA and back.
 */
public final class ShadowCascadeState {

    /** The greatest number of cascades this state can describe, which is CNA's own constant. */
    public static final int MAX_CASCADES = 4;

    private static final int INTEGRAL_LEAVES = 2;
    private static final int FLOATING_LEAVES = 85;
    private static final int BYTE_LEAVES = 3;
    private static final int TRANSFORMS_AT = 1;
    private static final int SPLITS_AT = TRANSFORMS_AT + MAX_CASCADES * EngineValues.MATRIX_LEAVES;
    private static final int CAMERA_VIEW_AT = SPLITS_AT + MAX_CASCADES;

    private final int count;
    private final float blendBand;
    private final List<Matrix> worldToAtlas;
    private final float[] splitDistance;
    private final Matrix cameraView;
    private final boolean debugTint;

    private ShadowCascadeState(int count, float blendBand, List<Matrix> worldToAtlas,
            float[] splitDistance, Matrix cameraView, boolean debugTint) {
        this.count = count;
        this.blendBand = blendBand;
        this.worldToAtlas = List.copyOf(worldToAtlas);
        this.splitDistance = splitDistance.clone();
        this.cameraView = new Matrix(cameraView);
        this.debugTint = debugTint;
    }

    /**
     * Returns CNA's own defaults, which are the state that disables cascaded shadows.
     *
     * @return the default state
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static ShadowCascadeState createDefault() {
        GraphicsExtension.requireBackend();
        long[] integral = new long[INTEGRAL_LEAVES];
        float[] floating = new float[FLOATING_LEAVES];
        GraphicsExtension.check("ShadowCascadeState.createDefault",
                NativeEngineLayerRoutes.shadowCascadeStateExtInit(new byte[BYTE_LEAVES], integral,
                        floating));
        return fromLeaves(integral, floating);
    }

    /**
     * Builds a state from cascades a game computed itself.
     *
     * <p>{@link #of} is the usual route, and this is for a game that does its own cascade fitting
     * rather than using CNA's {@link CascadedShadowMap} -- which is a reasonable thing to do, and
     * would otherwise leave this type unreachable.
     *
     * @param count how many cascades are in use, from zero to {@link #MAX_CASCADES}; zero
     *        disables cascaded shadows
     * @param blendBand the width in world units over which neighbouring cascades cross-fade
     * @param worldToAtlas exactly {@link #MAX_CASCADES} transforms, whatever the count is,
     *        because CNA's layout is fixed
     * @param splitDistance exactly {@link #MAX_CASCADES} distances, likewise
     * @param cameraView the view the splits were computed against
     * @param debugTint whether to tint each cascade differently
     * @return the state
     * @throws IllegalArgumentException when the count or either array is the wrong size
     */
    public static ShadowCascadeState create(int count, float blendBand, List<Matrix> worldToAtlas,
            float[] splitDistance, Matrix cameraView, boolean debugTint) {
        Objects.requireNonNull(worldToAtlas, "worldToAtlas");
        Objects.requireNonNull(splitDistance, "splitDistance");
        Objects.requireNonNull(cameraView, "cameraView");
        if (count < 0 || count > MAX_CASCADES) {
            throw new IllegalArgumentException(
                    "count is " + count + ", which is outside zero to " + MAX_CASCADES);
        }
        // The arrays are the structure's own fixed length rather than the count, because the
        // unused entries are part of the layout: accepting a shorter list would mean inventing
        // values for the rest, which is the kind of guess that shows up as an unlit cascade.
        if (worldToAtlas.size() != MAX_CASCADES || splitDistance.length != MAX_CASCADES) {
            throw new IllegalArgumentException(
                    "a cascade state carries exactly " + MAX_CASCADES + " transforms and splits");
        }
        return new ShadowCascadeState(count, blendBand, worldToAtlas, splitDistance, cameraView,
                debugTint);
    }

    /**
     * Reads the state out of a cascaded shadow map.
     *
     * <p>Every field comes from the map's own answers rather than from anything remembered here,
     * so a state built after an {@link CascadedShadowMap#update} describes that update.
     *
     * @param shadowMap the map to read
     * @param cameraView the view the splits were computed against
     * @return the state
     */
    public static ShadowCascadeState of(CascadedShadowMap shadowMap, Matrix cameraView) {
        Objects.requireNonNull(shadowMap, "shadowMap");
        Objects.requireNonNull(cameraView, "cameraView");
        int cascades = shadowMap.getCascadeCount();
        List<Matrix> transforms = new ArrayList<>(MAX_CASCADES);
        float[] splits = new float[MAX_CASCADES];
        for (int cascade = 0; cascade < MAX_CASCADES; cascade++) {
            // Only the cascades the map actually has are read from it; the rest keep the
            // defaults CNA's own structure carries, because the layout is fixed at four whatever
            // the count is.
            if (cascade < cascades) {
                transforms.add(shadowMap.getCascadeMatrix(cascade));
                splits[cascade] = shadowMap.getSplitDistance(cascade);
            } else {
                transforms.add(Matrix.getIdentity());
            }
        }
        return new ShadowCascadeState(cascades, shadowMap.getBlendBand(), transforms, splits,
                cameraView, shadowMap.isDebugTintEnabled());
    }

    /** @return how many cascades are in use; zero disables cascaded shadows */
    public int getCount() {
        return count;
    }

    /** @return the width in world units over which neighbouring cascades cross-fade */
    public float getBlendBand() {
        return blendBand;
    }

    /**
     * Returns the transform from world space into one cascade's atlas region.
     *
     * @param cascade the index, from zero to {@link #MAX_CASCADES} minus one
     * @return the transform
     */
    public Matrix getWorldToAtlas(int cascade) {
        return new Matrix(worldToAtlas.get(cascade));
    }

    /**
     * Returns the view-space distance at which one cascade ends.
     *
     * @param cascade the index, from zero to {@link #MAX_CASCADES} minus one
     * @return the distance
     */
    public float getSplitDistance(int cascade) {
        return splitDistance[cascade];
    }

    /** @return the camera view the splits were computed against */
    public Matrix getCameraView() {
        return new Matrix(cameraView);
    }

    /** @return whether each cascade is tinted differently, for diagnosing split placement */
    public boolean isDebugTint() {
        return debugTint;
    }

    /**
     * Returns a state like this one, tinted or not.
     *
     * @param value whether to tint each cascade differently
     * @return the state
     */
    public ShadowCascadeState withDebugTint(boolean value) {
        return new ShadowCascadeState(count, blendBand, worldToAtlas, splitDistance, cameraView,
                value);
    }

    /**
     * Returns a state like this one with a different blend band.
     *
     * @param value the width in world units over which cascades cross-fade
     * @return the state
     */
    public ShadowCascadeState withBlendBand(float value) {
        return new ShadowCascadeState(count, value, worldToAtlas, splitDistance, cameraView,
                debugTint);
    }

    /** Rebuilds a state from CNA's flat leaves. */
    static ShadowCascadeState fromLeaves(long[] integral, float[] floating) {
        List<Matrix> transforms = new ArrayList<>(MAX_CASCADES);
        float[] splits = new float[MAX_CASCADES];
        for (int cascade = 0; cascade < MAX_CASCADES; cascade++) {
            transforms.add(EngineValues.matrixAt(floating,
                    TRANSFORMS_AT + cascade * EngineValues.MATRIX_LEAVES));
            splits[cascade] = floating[SPLITS_AT + cascade];
        }
        return new ShadowCascadeState(Math.toIntExact(integral[0]), floating[0], transforms,
                splits, EngineValues.matrixAt(floating, CAMERA_VIEW_AT), integral[1] != 0L);
    }

    /** The byte leaves CNA's structure declares, which are padding. */
    byte[] bytes() {
        return new byte[BYTE_LEAVES];
    }

    /** The integral leaves CNA's structure declares, in declaration order. */
    long[] integral() {
        return new long[] {count, debugTint ? 1L : 0L};
    }

    /** The floating leaves CNA's structure declares, in declaration order. */
    float[] floating() {
        float[] leaves = new float[FLOATING_LEAVES];
        leaves[0] = blendBand;
        for (int cascade = 0; cascade < MAX_CASCADES; cascade++) {
            System.arraycopy(EngineValues.floats(worldToAtlas.get(cascade), "worldToAtlas"), 0,
                    leaves, TRANSFORMS_AT + cascade * EngineValues.MATRIX_LEAVES,
                    EngineValues.MATRIX_LEAVES);
            leaves[SPLITS_AT + cascade] = splitDistance[cascade];
        }
        System.arraycopy(EngineValues.floats(cameraView, "cameraView"), 0, leaves,
                CAMERA_VIEW_AT, EngineValues.MATRIX_LEAVES);
        return leaves;
    }
}
