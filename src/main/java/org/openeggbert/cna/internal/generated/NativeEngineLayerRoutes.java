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
