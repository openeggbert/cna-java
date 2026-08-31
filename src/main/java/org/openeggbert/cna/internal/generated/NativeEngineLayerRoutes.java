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
     * cna_clustered_light_assignment_adopt (engine_layer.h).
     */
    public static native int clusteredLightAssignmentAdopt(long assignment, int lightCount, int[] offsets, int[] indices);

    /**
     * cna_clustered_light_assignment_assign (engine_layer.h).
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
     */
    public static native int clusteredLightAssignmentAssign(long assignment, long grid, float[] viewFloating, float[] boundsFloating);

    /**
     * cna_clustered_light_assignment_clear (engine_layer.h).
     */
    public static native int clusteredLightAssignmentClear(long assignment);

    /**
     * cna_clustered_light_assignment_copy_indices (engine_layer.h).
     */
    public static native int clusteredLightAssignmentCopyIndices(long assignment, int[] destination, long[] outCount);

    /**
     * cna_clustered_light_assignment_copy_lights_in_cluster (engine_layer.h).
     */
    public static native int clusteredLightAssignmentCopyLightsInCluster(long assignment, int clusterIndex, int[] destination, long[] outCount);

    /**
     * cna_clustered_light_assignment_copy_offsets (engine_layer.h).
     */
    public static native int clusteredLightAssignmentCopyOffsets(long assignment, int[] destination, long[] outCount);

    /**
     * cna_clustered_light_assignment_create (engine_layer.h).
     */
    public static native int clusteredLightAssignmentCreate(long game, long[] outAssignment);

    /**
     * cna_clustered_light_assignment_destroy (engine_layer.h).
     */
    public static native int clusteredLightAssignmentDestroy(long assignment);

    /**
     * cna_clustered_light_assignment_get_cluster_count (engine_layer.h).
     */
    public static native int clusteredLightAssignmentGetClusterCount(long assignment, int[] outCount);

    /**
     * cna_clustered_light_assignment_get_light_count (engine_layer.h).
     */
    public static native int clusteredLightAssignmentGetLightCount(long assignment, int[] outCount);

    /**
     * cna_clustered_light_assignment_get_max_lights_per_cluster (engine_layer.h).
     */
    public static native int clusteredLightAssignmentGetMaxLightsPerCluster(long assignment, int[] outCount);

    /**
     * cna_clustered_light_assignment_get_total_reference_count (engine_layer.h).
     */
    public static native int clusteredLightAssignmentGetTotalReferenceCount(long assignment, int[] outCount);

    /**
     * cna_clustered_light_buffer_bind (engine_layer.h).
     */
    public static native int clusteredLightBufferBind(long buffer, long effect, int firstUnit);

    /**
     * cna_clustered_light_buffer_copy_light_lookup_glsl (engine_layer.h).
     */
    public static native int clusteredLightBufferCopyLightLookupGlsl(byte[] destination, long[] outBytes);

    /**
     * cna_clustered_light_buffer_create (engine_layer.h).
     */
    public static native int clusteredLightBufferCreate(long graphicsDevice, long[] outBuffer);

    /**
     * cna_clustered_light_buffer_destroy (engine_layer.h).
     */
    public static native int clusteredLightBufferDestroy(long buffer);

    /**
     * cna_clustered_light_buffer_get_cluster_count (engine_layer.h).
     */
    public static native int clusteredLightBufferGetClusterCount(long buffer, int[] outCount);

    /**
     * cna_clustered_light_buffer_get_light_count (engine_layer.h).
     */
    public static native int clusteredLightBufferGetLightCount(long buffer, int[] outCount);

    /**
     * cna_clustered_light_buffer_get_reference_count (engine_layer.h).
     */
    public static native int clusteredLightBufferGetReferenceCount(long buffer, int[] outCount);

    /**
     * cna_clustered_light_buffer_is_uploaded (engine_layer.h).
     */
    public static native int clusteredLightBufferIsUploaded(long buffer, boolean[] outUploaded);

    /**
     * cna_clustered_light_buffer_upload (engine_layer.h).
     */
    public static native int clusteredLightBufferUpload(long buffer, long lights, long grid, long assignment);

    /**
     * cna_clustered_light_compute_assign (engine_layer.h).
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
     */
    public static native int clusteredLightComputeAssign(long compute, long grid, float[] viewFloating, float[] boundsFloating, long outAssignment);

    /**
     * cna_clustered_light_compute_copy_unsupported_reason (engine_layer.h).
     */
    public static native int clusteredLightComputeCopyUnsupportedReason(long compute, byte[] destination, long[] outBytes);

    /**
     * cna_clustered_light_compute_create (engine_layer.h).
     */
    public static native int clusteredLightComputeCreate(long graphicsDevice, int stride, long[] outCompute);

    /**
     * cna_clustered_light_compute_destroy (engine_layer.h).
     */
    public static native int clusteredLightComputeDestroy(long compute);

    /**
     * cna_clustered_light_compute_get_stride (engine_layer.h).
     */
    public static native int clusteredLightComputeGetStride(long compute, int[] outStride);

    /**
     * cna_clustered_light_compute_has_overflowed (engine_layer.h).
     */
    public static native int clusteredLightComputeHasOverflowed(long compute, boolean[] outOverflowed);

    /**
     * cna_clustered_light_compute_is_supported (engine_layer.h).
     */
    public static native int clusteredLightComputeIsSupported(long compute, boolean[] outSupported);

    /**
     * cna_clustered_light_compute_used_compute (engine_layer.h).
     */
    public static native int clusteredLightComputeUsedCompute(long compute, boolean[] outUsed);

    /**
     * cna_clustered_light_ext_init (engine_layer.h).
     *
     * <p>outLightBytes carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>outLightIntegral carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code type} (CNA_ClusteredLightType)</li>
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>outLightFloating carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code direction.x} (float)</li>
     *   <li>{@code direction.y} (float)</li>
     *   <li>{@code direction.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     *   <li>{@code inner_angle} (float)</li>
     *   <li>{@code outer_angle} (float)</li>
     * </ol>
     */
    public static native int clusteredLightExtInit(byte[] outLightBytes, long[] outLightIntegral, float[] outLightFloating);

    /**
     * cna_clustered_light_grid_cluster_bounds (engine_layer.h).
     *
     * <p>outBoundsFloating carries CNA_BoundingBox in this order:
     * <ol start="0">
     *   <li>{@code min.x} (float)</li>
     *   <li>{@code min.y} (float)</li>
     *   <li>{@code min.z} (float)</li>
     *   <li>{@code max.x} (float)</li>
     *   <li>{@code max.y} (float)</li>
     *   <li>{@code max.z} (float)</li>
     * </ol>
     */
    public static native int clusteredLightGridClusterBounds(long grid, int x, int y, int slice, float[] outBoundsFloating);

    /**
     * cna_clustered_light_grid_cluster_index (engine_layer.h).
     */
    public static native int clusteredLightGridClusterIndex(long grid, int x, int y, int slice, int[] outIndex);

    /**
     * cna_clustered_light_grid_create (engine_layer.h).
     */
    public static native int clusteredLightGridCreate(long game, int tilesX, int tilesY, int sliceCount, long[] outGrid);

    /**
     * cna_clustered_light_grid_destroy (engine_layer.h).
     */
    public static native int clusteredLightGridDestroy(long grid);

    /**
     * cna_clustered_light_grid_get_cluster_count (engine_layer.h).
     */
    public static native int clusteredLightGridGetClusterCount(long grid, int[] outCount);

    /**
     * cna_clustered_light_grid_get_far_plane (engine_layer.h).
     */
    public static native int clusteredLightGridGetFarPlane(long grid, float[] outFar);

    /**
     * cna_clustered_light_grid_get_inverse_projection (engine_layer.h).
     *
     * <p>outMatrixFloating carries CNA_Matrix in this order:
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
    public static native int clusteredLightGridGetInverseProjection(long grid, float[] outMatrixFloating);

    /**
     * cna_clustered_light_grid_get_near_plane (engine_layer.h).
     */
    public static native int clusteredLightGridGetNearPlane(long grid, float[] outNear);

    /**
     * cna_clustered_light_grid_get_slice_count (engine_layer.h).
     */
    public static native int clusteredLightGridGetSliceCount(long grid, int[] outSlices);

    /**
     * cna_clustered_light_grid_get_tiles_x (engine_layer.h).
     */
    public static native int clusteredLightGridGetTilesX(long grid, int[] outTiles);

    /**
     * cna_clustered_light_grid_get_tiles_y (engine_layer.h).
     */
    public static native int clusteredLightGridGetTilesY(long grid, int[] outTiles);

    /**
     * cna_clustered_light_grid_has_projection (engine_layer.h).
     */
    public static native int clusteredLightGridHasProjection(long grid, boolean[] outHas);

    /**
     * cna_clustered_light_grid_set_projection (engine_layer.h).
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
    public static native int clusteredLightGridSetProjection(long grid, float[] projectionFloating, float nearPlane, float farPlane);

    /**
     * cna_clustered_light_grid_slice_distance (engine_layer.h).
     */
    public static native int clusteredLightGridSliceDistance(long grid, int slice, float[] outDistance);

    /**
     * cna_clustered_light_grid_slice_for_view_distance (engine_layer.h).
     */
    public static native int clusteredLightGridSliceForViewDistance(long grid, float viewDistance, int[] outSlice);

    /**
     * cna_clustered_light_set_add (engine_layer.h).
     *
     * <p>lightBytes carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>lightIntegral carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code type} (CNA_ClusteredLightType)</li>
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>lightFloating carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code direction.x} (float)</li>
     *   <li>{@code direction.y} (float)</li>
     *   <li>{@code direction.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     *   <li>{@code inner_angle} (float)</li>
     *   <li>{@code outer_angle} (float)</li>
     * </ol>
     */
    public static native int clusteredLightSetAdd(long set, byte[] lightBytes, long[] lightIntegral, float[] lightFloating, int[] outIndex);

    /**
     * cna_clustered_light_set_add_point (engine_layer.h).
     *
     * <p>lightBytes carries CNA_PointLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>lightIntegral carries CNA_PointLightEXT in this order:
     * <ol start="0">
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>lightFloating carries CNA_PointLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     * </ol>
     */
    public static native int clusteredLightSetAddPoint(long set, byte[] lightBytes, long[] lightIntegral, float[] lightFloating, int[] outIndex);

    /**
     * cna_clustered_light_set_add_spot (engine_layer.h).
     *
     * <p>lightBytes carries CNA_SpotLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>lightIntegral carries CNA_SpotLightEXT in this order:
     * <ol start="0">
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>lightFloating carries CNA_SpotLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code direction.x} (float)</li>
     *   <li>{@code direction.y} (float)</li>
     *   <li>{@code direction.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     *   <li>{@code inner_angle} (float)</li>
     *   <li>{@code outer_angle} (float)</li>
     * </ol>
     */
    public static native int clusteredLightSetAddSpot(long set, byte[] lightBytes, long[] lightIntegral, float[] lightFloating, int[] outIndex);

    /**
     * cna_clustered_light_set_clear (engine_layer.h).
     */
    public static native int clusteredLightSetClear(long set);

    /**
     * cna_clustered_light_set_copy_bounds (engine_layer.h).
     */
    public static native int clusteredLightSetCopyBounds(long set, float[] destinationFloating, long[] outCount);

    /**
     * cna_clustered_light_set_copy_lights (engine_layer.h).
     */
    public static native int clusteredLightSetCopyLights(long set, byte[] destinationBytes, long[] destinationIntegral, float[] destinationFloating, long[] outCount);

    /**
     * cna_clustered_light_set_create (engine_layer.h).
     */
    public static native int clusteredLightSetCreate(long game, long[] outSet);

    /**
     * cna_clustered_light_set_destroy (engine_layer.h).
     */
    public static native int clusteredLightSetDestroy(long set);

    /**
     * cna_clustered_light_set_get_at (engine_layer.h).
     *
     * <p>outLightBytes carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>outLightIntegral carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code type} (CNA_ClusteredLightType)</li>
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>outLightFloating carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code direction.x} (float)</li>
     *   <li>{@code direction.y} (float)</li>
     *   <li>{@code direction.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     *   <li>{@code inner_angle} (float)</li>
     *   <li>{@code outer_angle} (float)</li>
     * </ol>
     */
    public static native int clusteredLightSetGetAt(long set, int index, byte[] outLightBytes, long[] outLightIntegral, float[] outLightFloating);

    /**
     * cna_clustered_light_set_get_bounds_at (engine_layer.h).
     *
     * <p>outBoundsFloating carries CNA_BoundingSphere in this order:
     * <ol start="0">
     *   <li>{@code center.x} (float)</li>
     *   <li>{@code center.y} (float)</li>
     *   <li>{@code center.z} (float)</li>
     *   <li>{@code radius} (float)</li>
     * </ol>
     */
    public static native int clusteredLightSetGetBoundsAt(long set, int index, float[] outBoundsFloating);

    /**
     * cna_clustered_light_set_get_count (engine_layer.h).
     */
    public static native int clusteredLightSetGetCount(long set, int[] outCount);

    /**
     * cna_clustered_light_set_is_empty (engine_layer.h).
     */
    public static native int clusteredLightSetIsEmpty(long set, boolean[] outEmpty);

    /**
     * cna_clustered_light_set_is_usable (engine_layer.h).
     *
     * <p>lightBytes carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>lightIntegral carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code type} (CNA_ClusteredLightType)</li>
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>lightFloating carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code direction.x} (float)</li>
     *   <li>{@code direction.y} (float)</li>
     *   <li>{@code direction.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     *   <li>{@code inner_angle} (float)</li>
     *   <li>{@code outer_angle} (float)</li>
     * </ol>
     */
    public static native int clusteredLightSetIsUsable(byte[] lightBytes, long[] lightIntegral, float[] lightFloating, boolean[] outUsable);

    /**
     * cna_clustered_light_set_remove_at (engine_layer.h).
     */
    public static native int clusteredLightSetRemoveAt(long set, int index);

    /**
     * cna_clustered_light_set_replace_at (engine_layer.h).
     *
     * <p>lightBytes carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>lightIntegral carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code type} (CNA_ClusteredLightType)</li>
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>lightFloating carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code direction.x} (float)</li>
     *   <li>{@code direction.y} (float)</li>
     *   <li>{@code direction.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     *   <li>{@code inner_angle} (float)</li>
     *   <li>{@code outer_angle} (float)</li>
     * </ol>
     */
    public static native int clusteredLightSetReplaceAt(long set, int index, byte[] lightBytes, long[] lightIntegral, float[] lightFloating);

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
     * cna_debug_draw_add_directional_light_gizmo (engine_layer.h).
     *
     * <p>lightBytes carries CNA_DirectionalLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>lightIntegral carries CNA_DirectionalLightEXT in this order:
     * <ol start="0">
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>lightFloating carries CNA_DirectionalLightEXT in this order:
     * <ol start="0">
     *   <li>{@code direction.x} (float)</li>
     *   <li>{@code direction.y} (float)</li>
     *   <li>{@code direction.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     * </ol>
     *
     * <p>atFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int debugDrawAddDirectionalLightGizmo(long debug, byte[] lightBytes, long[] lightIntegral, float[] lightFloating, float[] atFloating, float length, long[] colourIntegral);

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
     * cna_debug_draw_add_point_light_gizmo (engine_layer.h).
     *
     * <p>lightBytes carries CNA_PointLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>lightIntegral carries CNA_PointLightEXT in this order:
     * <ol start="0">
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>lightFloating carries CNA_PointLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     * </ol>
     */
    public static native int debugDrawAddPointLightGizmo(long debug, byte[] lightBytes, long[] lightIntegral, float[] lightFloating, long[] colourIntegral);

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
     * cna_debug_draw_add_spot_light_gizmo (engine_layer.h).
     *
     * <p>lightBytes carries CNA_SpotLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>lightIntegral carries CNA_SpotLightEXT in this order:
     * <ol start="0">
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>lightFloating carries CNA_SpotLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code direction.x} (float)</li>
     *   <li>{@code direction.y} (float)</li>
     *   <li>{@code direction.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     *   <li>{@code inner_angle} (float)</li>
     *   <li>{@code outer_angle} (float)</li>
     * </ol>
     */
    public static native int debugDrawAddSpotLightGizmo(long debug, byte[] lightBytes, long[] lightIntegral, float[] lightFloating, long[] colourIntegral, int segments);

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
     * cna_directional_light_ext_init (engine_layer.h).
     *
     * <p>outLightBytes carries CNA_DirectionalLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>outLightIntegral carries CNA_DirectionalLightEXT in this order:
     * <ol start="0">
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>outLightFloating carries CNA_DirectionalLightEXT in this order:
     * <ol start="0">
     *   <li>{@code direction.x} (float)</li>
     *   <li>{@code direction.y} (float)</li>
     *   <li>{@code direction.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     * </ol>
     */
    public static native int directionalLightExtInit(byte[] outLightBytes, long[] outLightIntegral, float[] outLightFloating);

    /**
     * cna_engine_layer_get_version (engine_layer.h).
     */
    public static native int engineLayerGetVersion(int[] outVersion);

    /**
     * cna_frustum_culler_ext_create (engine_layer.h).
     */
    public static native int frustumCullerExtCreate(long[] outCuller);

    /**
     * cna_frustum_culler_ext_cull_boxes (engine_layer.h).
     */
    public static native int frustumCullerExtCullBoxes(long culler, float[] boundsFloating, long[] destination, long[] outCount);

    /**
     * cna_frustum_culler_ext_cull_spheres (engine_layer.h).
     */
    public static native int frustumCullerExtCullSpheres(long culler, float[] boundsFloating, long[] destination, long[] outCount);

    /**
     * cna_frustum_culler_ext_cull_transforms (engine_layer.h).
     */
    public static native int frustumCullerExtCullTransforms(long culler, float[] transformsFloating, float[] boundsFloating, float[] destinationFloating, long[] outCount);

    /**
     * cna_frustum_culler_ext_destroy (engine_layer.h).
     */
    public static native int frustumCullerExtDestroy(long culler);

    /**
     * cna_frustum_culler_ext_get_frustum (engine_layer.h).
     *
     * <p>outFrustumFloating carries CNA_BoundingFrustum in this order:
     * <ol start="0">
     *   <li>{@code matrix.m11} (float)</li>
     *   <li>{@code matrix.m12} (float)</li>
     *   <li>{@code matrix.m13} (float)</li>
     *   <li>{@code matrix.m14} (float)</li>
     *   <li>{@code matrix.m21} (float)</li>
     *   <li>{@code matrix.m22} (float)</li>
     *   <li>{@code matrix.m23} (float)</li>
     *   <li>{@code matrix.m24} (float)</li>
     *   <li>{@code matrix.m31} (float)</li>
     *   <li>{@code matrix.m32} (float)</li>
     *   <li>{@code matrix.m33} (float)</li>
     *   <li>{@code matrix.m34} (float)</li>
     *   <li>{@code matrix.m41} (float)</li>
     *   <li>{@code matrix.m42} (float)</li>
     *   <li>{@code matrix.m43} (float)</li>
     *   <li>{@code matrix.m44} (float)</li>
     * </ol>
     */
    public static native int frustumCullerExtGetFrustum(long culler, float[] outFrustumFloating);

    /**
     * cna_frustum_culler_ext_is_box_visible (engine_layer.h).
     *
     * <p>boxFloating carries CNA_BoundingBox in this order:
     * <ol start="0">
     *   <li>{@code min.x} (float)</li>
     *   <li>{@code min.y} (float)</li>
     *   <li>{@code min.z} (float)</li>
     *   <li>{@code max.x} (float)</li>
     *   <li>{@code max.y} (float)</li>
     *   <li>{@code max.z} (float)</li>
     * </ol>
     */
    public static native int frustumCullerExtIsBoxVisible(long culler, float[] boxFloating, boolean[] outVisible);

    /**
     * cna_frustum_culler_ext_is_sphere_visible (engine_layer.h).
     *
     * <p>sphereFloating carries CNA_BoundingSphere in this order:
     * <ol start="0">
     *   <li>{@code center.x} (float)</li>
     *   <li>{@code center.y} (float)</li>
     *   <li>{@code center.z} (float)</li>
     *   <li>{@code radius} (float)</li>
     * </ol>
     */
    public static native int frustumCullerExtIsSphereVisible(long culler, float[] sphereFloating, boolean[] outVisible);

    /**
     * cna_frustum_culler_ext_set_camera (engine_layer.h).
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
    public static native int frustumCullerExtSetCamera(long culler, float[] viewFloating, float[] projectionFloating);

    /**
     * cna_frustum_culler_ext_set_view_projection (engine_layer.h).
     *
     * <p>viewProjectionFloating carries CNA_Matrix in this order:
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
    public static native int frustumCullerExtSetViewProjection(long culler, float[] viewProjectionFloating);

    /**
     * cna_gpu_timer_begin (engine_layer.h).
     */
    public static native int gpuTimerBegin(long timer);

    /**
     * cna_gpu_timer_copy_unsupported_reason (engine_layer.h).
     */
    public static native int gpuTimerCopyUnsupportedReason(long timer, byte[] destination, long[] outBytes);

    /**
     * cna_gpu_timer_create (engine_layer.h).
     */
    public static native int gpuTimerCreate(long graphicsDevice, long[] outTimer);

    /**
     * cna_gpu_timer_destroy (engine_layer.h).
     */
    public static native int gpuTimerDestroy(long timer);

    /**
     * cna_gpu_timer_end (engine_layer.h).
     */
    public static native int gpuTimerEnd(long timer);

    /**
     * cna_gpu_timer_get_last_milliseconds (engine_layer.h).
     */
    public static native int gpuTimerGetLastMilliseconds(long timer, double[] outMilliseconds);

    /**
     * cna_gpu_timer_get_sample_count (engine_layer.h).
     */
    public static native int gpuTimerGetSampleCount(long timer, int[] outSampleCount);

    /**
     * cna_gpu_timer_is_open (engine_layer.h).
     */
    public static native int gpuTimerIsOpen(long timer, boolean[] outOpen);

    /**
     * cna_gpu_timer_is_result_available (engine_layer.h).
     */
    public static native int gpuTimerIsResultAvailable(long timer, boolean[] outAvailable);

    /**
     * cna_gpu_timer_is_supported (engine_layer.h).
     */
    public static native int gpuTimerIsSupported(long timer, boolean[] outSupported);

    /**
     * cna_gpu_timer_poll (engine_layer.h).
     */
    public static native int gpuTimerPoll(long timer, boolean[] outCollected);

    /**
     * cna_instanced_renderer_ext_copy_instance_elements (engine_layer.h).
     */
    public static native int instancedRendererExtCopyInstanceElements(long[] destinationIntegral, long[] outElementCount);

    /**
     * cna_instanced_renderer_ext_copy_tint_elements (engine_layer.h).
     */
    public static native int instancedRendererExtCopyTintElements(long[] destinationIntegral, long[] outElementCount);

    /**
     * cna_instanced_renderer_ext_create (engine_layer.h).
     */
    public static native int instancedRendererExtCreate(long graphicsDevice, long part, long[] outRenderer);

    /**
     * cna_instanced_renderer_ext_destroy (engine_layer.h).
     */
    public static native int instancedRendererExtDestroy(long renderer);

    /**
     * cna_instanced_renderer_ext_did_last_draw_instance (engine_layer.h).
     */
    public static native int instancedRendererExtDidLastDrawInstance(long renderer, boolean[] outInstanced);

    /**
     * cna_instanced_renderer_ext_draw (engine_layer.h).
     */
    public static native int instancedRendererExtDraw(long renderer, long effect);

    /**
     * cna_instanced_renderer_ext_get_instance_capacity (engine_layer.h).
     */
    public static native int instancedRendererExtGetInstanceCapacity(long renderer, int[] outCapacity);

    /**
     * cna_instanced_renderer_ext_get_instance_count (engine_layer.h).
     */
    public static native int instancedRendererExtGetInstanceCount(long renderer, int[] outCount);

    /**
     * cna_instanced_renderer_ext_get_instance_stride (engine_layer.h).
     */
    public static native int instancedRendererExtGetInstanceStride(int[] outStride);

    /**
     * cna_instanced_renderer_ext_get_last_draw_call_count (engine_layer.h).
     */
    public static native int instancedRendererExtGetLastDrawCallCount(long renderer, int[] outCount);

    /**
     * cna_instanced_renderer_ext_get_tint_stride (engine_layer.h).
     */
    public static native int instancedRendererExtGetTintStride(int[] outStride);

    /**
     * cna_instanced_renderer_ext_is_fallback_enabled (engine_layer.h).
     */
    public static native int instancedRendererExtIsFallbackEnabled(long renderer, boolean[] outEnabled);

    /**
     * cna_instanced_renderer_ext_is_instancing_supported (engine_layer.h).
     */
    public static native int instancedRendererExtIsInstancingSupported(long renderer, boolean[] outSupported);

    /**
     * cna_instanced_renderer_ext_is_tints_enabled (engine_layer.h).
     */
    public static native int instancedRendererExtIsTintsEnabled(long renderer, boolean[] outEnabled);

    /**
     * cna_instanced_renderer_ext_set_fallback_enabled (engine_layer.h).
     */
    public static native int instancedRendererExtSetFallbackEnabled(long renderer, boolean enabled);

    /**
     * cna_instanced_renderer_ext_set_instance_tints (engine_layer.h).
     */
    public static native int instancedRendererExtSetInstanceTints(long renderer, long[] tintsIntegral);

    /**
     * cna_instanced_renderer_ext_set_instances (engine_layer.h).
     */
    public static native int instancedRendererExtSetInstances(long renderer, float[] transformsFloating);

    /**
     * cna_instanced_renderer_ext_set_tints_enabled (engine_layer.h).
     */
    public static native int instancedRendererExtSetTintsEnabled(long renderer, boolean enabled);

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

    /**
     * cna_point_light_ext_init (engine_layer.h).
     *
     * <p>outLightBytes carries CNA_PointLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>outLightIntegral carries CNA_PointLightEXT in this order:
     * <ol start="0">
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>outLightFloating carries CNA_PointLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     * </ol>
     */
    public static native int pointLightExtInit(byte[] outLightBytes, long[] outLightIntegral, float[] outLightFloating);

    /**
     * cna_render_pipeline_settings_ext_apply_from_string (engine_layer.h).
     *
     * <p>settingsBytes carries CNA_RenderPipelineSettingsEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     *   <li>{@code reserved[3]} (uint8_t)</li>
     * </ol>
     *
     * <p>settingsIntegral carries CNA_RenderPipelineSettingsEXT in this order:
     * <ol start="0">
     *   <li>{@code hdr_enabled} (CNA_Bool)</li>
     *   <li>{@code tonemapping_mode} (CNA_TonemappingMode)</li>
     *   <li>{@code bloom_enabled} (CNA_Bool)</li>
     *   <li>{@code bloom_iterations} (int32_t)</li>
     *   <li>{@code ssao_enabled} (CNA_Bool)</li>
     *   <li>{@code transparency_mode} (CNA_TransparencyMode)</li>
     *   <li>{@code ssao_sample_count} (int32_t)</li>
     *   <li>{@code ssr_enabled} (CNA_Bool)</li>
     *   <li>{@code ssr_step_count} (int32_t)</li>
     *   <li>{@code color_grade_enabled} (CNA_Bool)</li>
     *   <li>{@code dof_enabled} (CNA_Bool)</li>
     *   <li>{@code fxaa_enabled} (CNA_Bool)</li>
     *   <li>{@code render_quality} (CNA_RenderQuality)</li>
     *   <li>{@code shadow_quality} (CNA_ShadowQuality)</li>
     *   <li>{@code shadows_enabled} (CNA_Bool)</li>
     * </ol>
     *
     * <p>settingsFloating carries CNA_RenderPipelineSettingsEXT in this order:
     * <ol start="0">
     *   <li>{@code exposure} (float)</li>
     *   <li>{@code gamma} (float)</li>
     *   <li>{@code bloom_intensity} (float)</li>
     *   <li>{@code bloom_threshold} (float)</li>
     *   <li>{@code ssao_radius} (float)</li>
     *   <li>{@code ssao_intensity} (float)</li>
     *   <li>{@code ssr_max_distance} (float)</li>
     *   <li>{@code ssr_thickness} (float)</li>
     *   <li>{@code ssr_depth_bias} (float)</li>
     *   <li>{@code ssr_edge_fade} (float)</li>
     *   <li>{@code volumetric_fog_density} (float)</li>
     *   <li>{@code light_shaft_threshold} (float)</li>
     *   <li>{@code light_shaft_intensity} (float)</li>
     *   <li>{@code light_shaft_decay} (float)</li>
     *   <li>{@code height_fog_density} (float)</li>
     *   <li>{@code height_fog_falloff} (float)</li>
     *   <li>{@code height_fog_base_height} (float)</li>
     *   <li>{@code motion_blur_strength} (float)</li>
     *   <li>{@code motion_blur_max_distance} (float)</li>
     *   <li>{@code chromatic_aberration_strength} (float)</li>
     *   <li>{@code film_grain_intensity} (float)</li>
     *   <li>{@code lens_flare_threshold} (float)</li>
     *   <li>{@code lens_flare_intensity} (float)</li>
     *   <li>{@code lens_flare_dispersal} (float)</li>
     *   <li>{@code color_grade_strength} (float)</li>
     *   <li>{@code dof_focus_distance} (float)</li>
     *   <li>{@code dof_focal_length} (float)</li>
     *   <li>{@code doff_number} (float)</li>
     *   <li>{@code dof_max_radius} (float)</li>
     *   <li>{@code ssr_roughness_blur} (float)</li>
     *   <li>{@code ssr_intensity} (float)</li>
     *   <li>{@code fxaa_edge_threshold_ext} (float)</li>
     * </ol>
     */
    public static native int renderPipelineSettingsExtApplyFromString(byte[] settingsBytes, long[] settingsIntegral, float[] settingsFloating, byte[] text, int[] outApplied);

    /**
     * cna_render_pipeline_settings_ext_apply_render_quality_preset (engine_layer.h).
     *
     * <p>settingsBytes carries CNA_RenderPipelineSettingsEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     *   <li>{@code reserved[3]} (uint8_t)</li>
     * </ol>
     *
     * <p>settingsIntegral carries CNA_RenderPipelineSettingsEXT in this order:
     * <ol start="0">
     *   <li>{@code hdr_enabled} (CNA_Bool)</li>
     *   <li>{@code tonemapping_mode} (CNA_TonemappingMode)</li>
     *   <li>{@code bloom_enabled} (CNA_Bool)</li>
     *   <li>{@code bloom_iterations} (int32_t)</li>
     *   <li>{@code ssao_enabled} (CNA_Bool)</li>
     *   <li>{@code transparency_mode} (CNA_TransparencyMode)</li>
     *   <li>{@code ssao_sample_count} (int32_t)</li>
     *   <li>{@code ssr_enabled} (CNA_Bool)</li>
     *   <li>{@code ssr_step_count} (int32_t)</li>
     *   <li>{@code color_grade_enabled} (CNA_Bool)</li>
     *   <li>{@code dof_enabled} (CNA_Bool)</li>
     *   <li>{@code fxaa_enabled} (CNA_Bool)</li>
     *   <li>{@code render_quality} (CNA_RenderQuality)</li>
     *   <li>{@code shadow_quality} (CNA_ShadowQuality)</li>
     *   <li>{@code shadows_enabled} (CNA_Bool)</li>
     * </ol>
     *
     * <p>settingsFloating carries CNA_RenderPipelineSettingsEXT in this order:
     * <ol start="0">
     *   <li>{@code exposure} (float)</li>
     *   <li>{@code gamma} (float)</li>
     *   <li>{@code bloom_intensity} (float)</li>
     *   <li>{@code bloom_threshold} (float)</li>
     *   <li>{@code ssao_radius} (float)</li>
     *   <li>{@code ssao_intensity} (float)</li>
     *   <li>{@code ssr_max_distance} (float)</li>
     *   <li>{@code ssr_thickness} (float)</li>
     *   <li>{@code ssr_depth_bias} (float)</li>
     *   <li>{@code ssr_edge_fade} (float)</li>
     *   <li>{@code volumetric_fog_density} (float)</li>
     *   <li>{@code light_shaft_threshold} (float)</li>
     *   <li>{@code light_shaft_intensity} (float)</li>
     *   <li>{@code light_shaft_decay} (float)</li>
     *   <li>{@code height_fog_density} (float)</li>
     *   <li>{@code height_fog_falloff} (float)</li>
     *   <li>{@code height_fog_base_height} (float)</li>
     *   <li>{@code motion_blur_strength} (float)</li>
     *   <li>{@code motion_blur_max_distance} (float)</li>
     *   <li>{@code chromatic_aberration_strength} (float)</li>
     *   <li>{@code film_grain_intensity} (float)</li>
     *   <li>{@code lens_flare_threshold} (float)</li>
     *   <li>{@code lens_flare_intensity} (float)</li>
     *   <li>{@code lens_flare_dispersal} (float)</li>
     *   <li>{@code color_grade_strength} (float)</li>
     *   <li>{@code dof_focus_distance} (float)</li>
     *   <li>{@code dof_focal_length} (float)</li>
     *   <li>{@code doff_number} (float)</li>
     *   <li>{@code dof_max_radius} (float)</li>
     *   <li>{@code ssr_roughness_blur} (float)</li>
     *   <li>{@code ssr_intensity} (float)</li>
     *   <li>{@code fxaa_edge_threshold_ext} (float)</li>
     * </ol>
     */
    public static native int renderPipelineSettingsExtApplyRenderQualityPreset(byte[] settingsBytes, long[] settingsIntegral, float[] settingsFloating);

    /**
     * cna_render_pipeline_settings_ext_init (engine_layer.h).
     *
     * <p>outSettingsBytes carries CNA_RenderPipelineSettingsEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     *   <li>{@code reserved[3]} (uint8_t)</li>
     * </ol>
     *
     * <p>outSettingsIntegral carries CNA_RenderPipelineSettingsEXT in this order:
     * <ol start="0">
     *   <li>{@code hdr_enabled} (CNA_Bool)</li>
     *   <li>{@code tonemapping_mode} (CNA_TonemappingMode)</li>
     *   <li>{@code bloom_enabled} (CNA_Bool)</li>
     *   <li>{@code bloom_iterations} (int32_t)</li>
     *   <li>{@code ssao_enabled} (CNA_Bool)</li>
     *   <li>{@code transparency_mode} (CNA_TransparencyMode)</li>
     *   <li>{@code ssao_sample_count} (int32_t)</li>
     *   <li>{@code ssr_enabled} (CNA_Bool)</li>
     *   <li>{@code ssr_step_count} (int32_t)</li>
     *   <li>{@code color_grade_enabled} (CNA_Bool)</li>
     *   <li>{@code dof_enabled} (CNA_Bool)</li>
     *   <li>{@code fxaa_enabled} (CNA_Bool)</li>
     *   <li>{@code render_quality} (CNA_RenderQuality)</li>
     *   <li>{@code shadow_quality} (CNA_ShadowQuality)</li>
     *   <li>{@code shadows_enabled} (CNA_Bool)</li>
     * </ol>
     *
     * <p>outSettingsFloating carries CNA_RenderPipelineSettingsEXT in this order:
     * <ol start="0">
     *   <li>{@code exposure} (float)</li>
     *   <li>{@code gamma} (float)</li>
     *   <li>{@code bloom_intensity} (float)</li>
     *   <li>{@code bloom_threshold} (float)</li>
     *   <li>{@code ssao_radius} (float)</li>
     *   <li>{@code ssao_intensity} (float)</li>
     *   <li>{@code ssr_max_distance} (float)</li>
     *   <li>{@code ssr_thickness} (float)</li>
     *   <li>{@code ssr_depth_bias} (float)</li>
     *   <li>{@code ssr_edge_fade} (float)</li>
     *   <li>{@code volumetric_fog_density} (float)</li>
     *   <li>{@code light_shaft_threshold} (float)</li>
     *   <li>{@code light_shaft_intensity} (float)</li>
     *   <li>{@code light_shaft_decay} (float)</li>
     *   <li>{@code height_fog_density} (float)</li>
     *   <li>{@code height_fog_falloff} (float)</li>
     *   <li>{@code height_fog_base_height} (float)</li>
     *   <li>{@code motion_blur_strength} (float)</li>
     *   <li>{@code motion_blur_max_distance} (float)</li>
     *   <li>{@code chromatic_aberration_strength} (float)</li>
     *   <li>{@code film_grain_intensity} (float)</li>
     *   <li>{@code lens_flare_threshold} (float)</li>
     *   <li>{@code lens_flare_intensity} (float)</li>
     *   <li>{@code lens_flare_dispersal} (float)</li>
     *   <li>{@code color_grade_strength} (float)</li>
     *   <li>{@code dof_focus_distance} (float)</li>
     *   <li>{@code dof_focal_length} (float)</li>
     *   <li>{@code doff_number} (float)</li>
     *   <li>{@code dof_max_radius} (float)</li>
     *   <li>{@code ssr_roughness_blur} (float)</li>
     *   <li>{@code ssr_intensity} (float)</li>
     *   <li>{@code fxaa_edge_threshold_ext} (float)</li>
     * </ol>
     */
    public static native int renderPipelineSettingsExtInit(byte[] outSettingsBytes, long[] outSettingsIntegral, float[] outSettingsFloating);

    /**
     * cna_render_pipeline_settings_ext_normalize (engine_layer.h).
     *
     * <p>settingsBytes carries CNA_RenderPipelineSettingsEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     *   <li>{@code reserved[3]} (uint8_t)</li>
     * </ol>
     *
     * <p>settingsIntegral carries CNA_RenderPipelineSettingsEXT in this order:
     * <ol start="0">
     *   <li>{@code hdr_enabled} (CNA_Bool)</li>
     *   <li>{@code tonemapping_mode} (CNA_TonemappingMode)</li>
     *   <li>{@code bloom_enabled} (CNA_Bool)</li>
     *   <li>{@code bloom_iterations} (int32_t)</li>
     *   <li>{@code ssao_enabled} (CNA_Bool)</li>
     *   <li>{@code transparency_mode} (CNA_TransparencyMode)</li>
     *   <li>{@code ssao_sample_count} (int32_t)</li>
     *   <li>{@code ssr_enabled} (CNA_Bool)</li>
     *   <li>{@code ssr_step_count} (int32_t)</li>
     *   <li>{@code color_grade_enabled} (CNA_Bool)</li>
     *   <li>{@code dof_enabled} (CNA_Bool)</li>
     *   <li>{@code fxaa_enabled} (CNA_Bool)</li>
     *   <li>{@code render_quality} (CNA_RenderQuality)</li>
     *   <li>{@code shadow_quality} (CNA_ShadowQuality)</li>
     *   <li>{@code shadows_enabled} (CNA_Bool)</li>
     * </ol>
     *
     * <p>settingsFloating carries CNA_RenderPipelineSettingsEXT in this order:
     * <ol start="0">
     *   <li>{@code exposure} (float)</li>
     *   <li>{@code gamma} (float)</li>
     *   <li>{@code bloom_intensity} (float)</li>
     *   <li>{@code bloom_threshold} (float)</li>
     *   <li>{@code ssao_radius} (float)</li>
     *   <li>{@code ssao_intensity} (float)</li>
     *   <li>{@code ssr_max_distance} (float)</li>
     *   <li>{@code ssr_thickness} (float)</li>
     *   <li>{@code ssr_depth_bias} (float)</li>
     *   <li>{@code ssr_edge_fade} (float)</li>
     *   <li>{@code volumetric_fog_density} (float)</li>
     *   <li>{@code light_shaft_threshold} (float)</li>
     *   <li>{@code light_shaft_intensity} (float)</li>
     *   <li>{@code light_shaft_decay} (float)</li>
     *   <li>{@code height_fog_density} (float)</li>
     *   <li>{@code height_fog_falloff} (float)</li>
     *   <li>{@code height_fog_base_height} (float)</li>
     *   <li>{@code motion_blur_strength} (float)</li>
     *   <li>{@code motion_blur_max_distance} (float)</li>
     *   <li>{@code chromatic_aberration_strength} (float)</li>
     *   <li>{@code film_grain_intensity} (float)</li>
     *   <li>{@code lens_flare_threshold} (float)</li>
     *   <li>{@code lens_flare_intensity} (float)</li>
     *   <li>{@code lens_flare_dispersal} (float)</li>
     *   <li>{@code color_grade_strength} (float)</li>
     *   <li>{@code dof_focus_distance} (float)</li>
     *   <li>{@code dof_focal_length} (float)</li>
     *   <li>{@code doff_number} (float)</li>
     *   <li>{@code dof_max_radius} (float)</li>
     *   <li>{@code ssr_roughness_blur} (float)</li>
     *   <li>{@code ssr_intensity} (float)</li>
     *   <li>{@code fxaa_edge_threshold_ext} (float)</li>
     * </ol>
     */
    public static native int renderPipelineSettingsExtNormalize(byte[] settingsBytes, long[] settingsIntegral, float[] settingsFloating);

    /**
     * cna_spot_light_ext_init (engine_layer.h).
     *
     * <p>outLightBytes carries CNA_SpotLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>outLightIntegral carries CNA_SpotLightEXT in this order:
     * <ol start="0">
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>outLightFloating carries CNA_SpotLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code direction.x} (float)</li>
     *   <li>{@code direction.y} (float)</li>
     *   <li>{@code direction.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     *   <li>{@code inner_angle} (float)</li>
     *   <li>{@code outer_angle} (float)</li>
     * </ol>
     */
    public static native int spotLightExtInit(byte[] outLightBytes, long[] outLightIntegral, float[] outLightFloating);
}
