package org.openeggbert.cna.internal.generated;

/**
 * Generated CNA C ABI declarations for NativeEngineLayerRoutes.
 *
 * <p>Produced by {@code tools/native-abi/generate_jni.py} from the live CNA C headers.
 * Do not edit: every signature here is the header's own declaration, and regenerating
 * is how a change upstream reaches Java. This class is not application API.
 */
public final class NativeEngineLayerRoutes {

    private NativeEngineLayerRoutes() {
    }

    /**
     * cna_debug_draw_add_bounding_sphere (engine_layer.h).
     *
     * <p>sphereFloating carries CNA_BoundingSphere in this order:
     * <ol start="0">
     *   <li>{@code center.x} (float)</li>
     *   <li>{@code center.y} (float)</li>
     *   <li>{@code center.z} (float)</li>
     *   <li>{@code radius} (float)</li>
     * </ol>
     */
    public static native int debugDrawAddBoundingSphere(long debug, float[] sphereFloating, long[] colourIntegral, int segments);

    /**
     * cna_debug_draw_add_box (engine_layer.h).
     *
     * <p>boundsFloating carries CNA_BoundingBox in this order:
     * <ol start="0">
     *   <li>{@code min.x} (float)</li>
     *   <li>{@code min.y} (float)</li>
     *   <li>{@code min.z} (float)</li>
     *   <li>{@code max.x} (float)</li>
     *   <li>{@code max.y} (float)</li>
     *   <li>{@code max.z} (float)</li>
     * </ol>
     */
    public static native int debugDrawAddBox(long debug, float[] boundsFloating, long[] colourIntegral);

    /**
     * cna_debug_draw_add_cross (engine_layer.h).
     *
     * <p>positionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int debugDrawAddCross(long debug, float[] positionFloating, float size, long[] colourIntegral);

    /**
     * cna_debug_draw_add_frustum (engine_layer.h).
     */
    public static native int debugDrawAddFrustum(long debug, float[] frustumFloating, long[] colourIntegral);

    /**
     * cna_debug_draw_add_line (engine_layer.h).
     *
     * <p>fromFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     *
     * <p>toFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int debugDrawAddLine(long debug, float[] fromFloating, float[] toFloating, long[] colourIntegral);

    /**
     * cna_debug_draw_add_sphere (engine_layer.h).
     *
     * <p>centreFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int debugDrawAddSphere(long debug, float[] centreFloating, float radius, long[] colourIntegral, int segments);

    /**
     * cna_debug_draw_begin (engine_layer.h).
     *
     * <p>viewFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     *
     * <p>projectionFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     */
    public static native int debugDrawBegin(long debug, float[] viewFloating, float[] projectionFloating);

    /**
     * cna_debug_draw_clear (engine_layer.h).
     */
    public static native int debugDrawClear(long debug);

    /**
     * cna_debug_draw_copy_vertices (engine_layer.h).
     */
    public static native int debugDrawCopyVertices(long debug, boolean depthTested, long[] destinationIntegral, float[] destinationFloating, long[] outCount);

    /**
     * cna_debug_draw_create (engine_layer.h).
     */
    public static native int debugDrawCreate(long graphicsDevice, long[] outDebug);

    /**
     * cna_debug_draw_destroy (engine_layer.h).
     */
    public static native int debugDrawDestroy(long debug);

    /**
     * cna_debug_draw_end (engine_layer.h).
     */
    public static native int debugDrawEnd(long debug);

    /**
     * cna_debug_draw_get_line_count (engine_layer.h).
     */
    public static native int debugDrawGetLineCount(long debug, int[] outCount);

    /**
     * cna_debug_draw_is_depth_tested (engine_layer.h).
     */
    public static native int debugDrawIsDepthTested(long debug, boolean[] outDepthTested);

    /**
     * cna_debug_draw_set_depth_tested (engine_layer.h).
     */
    public static native int debugDrawSetDepthTested(long debug, boolean depthTested);

    /**
     * cna_engine_layer_get_version (engine_layer.h).
     */
    public static native int engineLayerGetVersion(int[] outVersion);

    /**
     * cna_lod_group_ext_add_level (engine_layer.h).
     */
    public static native int lodGroupExtAddLevel(long group, float maxDistance, long part);

    /**
     * cna_lod_group_ext_clear (engine_layer.h).
     */
    public static native int lodGroupExtClear(long group);

    /**
     * cna_lod_group_ext_copy_levels (engine_layer.h).
     */
    public static native int lodGroupExtCopyLevels(long group, long[] destinationIntegral, float[] destinationFloating, long[] outCount);

    /**
     * cna_lod_group_ext_create (engine_layer.h).
     */
    public static native int lodGroupExtCreate(long[] outGroup);

    /**
     * cna_lod_group_ext_destroy (engine_layer.h).
     */
    public static native int lodGroupExtDestroy(long group);

    /**
     * cna_lod_group_ext_get_hysteresis (engine_layer.h).
     */
    public static native int lodGroupExtGetHysteresis(long group, float[] outMargin);

    /**
     * cna_lod_group_ext_get_selection_mode (engine_layer.h).
     */
    public static native int lodGroupExtGetSelectionMode(long group, int[] outMode);

    /**
     * cna_lod_group_ext_projected_radius_pixels (engine_layer.h).
     */
    public static native int lodGroupExtProjectedRadiusPixels(long group, float distance, float[] outPixels);

    /**
     * cna_lod_group_ext_reset_hysteresis (engine_layer.h).
     */
    public static native int lodGroupExtResetHysteresis(long group);

    /**
     * cna_lod_group_ext_select_index (engine_layer.h).
     */
    public static native int lodGroupExtSelectIndex(long group, float distance, int[] outIndex);

    /**
     * cna_lod_group_ext_set_hysteresis (engine_layer.h).
     */
    public static native int lodGroupExtSetHysteresis(long group, float margin);

    /**
     * cna_lod_group_ext_set_screen_space_parameters (engine_layer.h).
     */
    public static native int lodGroupExtSetScreenSpaceParameters(long group, float radius, float verticalFov, float viewportHeight);

    /**
     * cna_lod_group_ext_set_selection_mode (engine_layer.h).
     */
    public static native int lodGroupExtSetSelectionMode(long group, int mode);
}
