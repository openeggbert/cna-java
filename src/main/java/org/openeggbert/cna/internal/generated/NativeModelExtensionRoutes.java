package org.openeggbert.cna.internal.generated;

/**
 * Generated CNA C ABI declarations for NativeModelExtensionRoutes.
 *
 * <p>Produced by {@code tools/native-abi/generate_jni.py} from the live CNA C headers.
 * Do not edit: every signature here is the header's own declaration, and regenerating
 * is how a change upstream reaches Java. This class is not application API.
 */
public final class NativeModelExtensionRoutes {

    private NativeModelExtensionRoutes() {
    }

    /**
     * cna_effect_copy_type_name (effects.h).
     */
    public static native int effectCopyTypeName(long effect, byte[] destination, long[] outByteCount);

    /**
     * cna_effect_get_type_name_byte_count (effects.h).
     */
    public static native int effectGetTypeNameByteCount(long effect, long[] outByteCount);

    /**
     * cna_model_bone_add_child (models.h).
     */
    public static native int modelBoneAddChild(long bone, long child);

    /**
     * cna_model_bone_collection_destroy (models.h).
     */
    public static native int modelBoneCollectionDestroy(long collection);

    /**
     * cna_model_bone_collection_get_at (models.h).
     */
    public static native int modelBoneCollectionGetAt(long collection, long index, long[] outBone);

    /**
     * cna_model_bone_collection_get_count (models.h).
     */
    public static native int modelBoneCollectionGetCount(long collection, long[] outCount);

    /**
     * cna_model_bone_copy_name (models.h).
     */
    public static native int modelBoneCopyName(long bone, byte[] destination, long[] outByteCount);

    /**
     * cna_model_bone_create (models.h).
     */
    public static native int modelBoneCreate(int index, byte[] name, long[] outBone);

    /**
     * cna_model_bone_destroy (models.h).
     */
    public static native int modelBoneDestroy(long bone);

    /**
     * cna_model_bone_get_children (models.h).
     */
    public static native int modelBoneGetChildren(long bone, long[] outChildren);

    /**
     * cna_model_bone_get_index (models.h).
     */
    public static native int modelBoneGetIndex(long bone, int[] outIndex);

    /**
     * cna_model_bone_get_name_byte_count (models.h).
     */
    public static native int modelBoneGetNameByteCount(long bone, long[] outByteCount);

    /**
     * cna_model_bone_get_parent (models.h).
     */
    public static native int modelBoneGetParent(long bone, boolean[] outHasParent, long[] outParent);

    /**
     * cna_model_bone_get_transform (models.h).
     *
     * <p>outTransformFloating carries CNA_Matrix in this order:
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
    public static native int modelBoneGetTransform(long bone, float[] outTransformFloating);

    /**
     * cna_model_bone_set_transform (models.h).
     */
    public static native int modelBoneSetTransform(long bone, float[] transformFloating);

    /**
     * cna_model_copy_absolute_bone_transforms (models.h).
     */
    public static native int modelCopyAbsoluteBoneTransforms(long model, float[] destinationFloating, long[] outCount);

    /**
     * cna_model_copy_bone_transforms (models.h).
     */
    public static native int modelCopyBoneTransforms(long model, float[] destinationFloating, long[] outCount);

    /**
     * cna_model_copy_camera_name_ext (models.h).
     */
    public static native int modelCopyCameraNameExt(long model, long index, byte[] destination, long[] outByteCount);

    /**
     * cna_model_copy_material_variant_name_ext (models.h).
     */
    public static native int modelCopyMaterialVariantNameExt(long model, long index, byte[] destination, long[] outByteCount);

    /**
     * cna_model_copy_skin_name_ext (models.h).
     */
    public static native int modelCopySkinNameExt(long model, long index, byte[] destination, long[] outByteCount);

    /**
     * cna_model_create_with_parents (models.h).
     */
    public static native int modelCreateWithParents(long graphicsDevice, long[] bones, long[] meshes, long[] meshParents, long rootBoneIndex, long[] outModel);

    /**
     * cna_model_destroy (models.h).
     */
    public static native int modelDestroy(long model);

    /**
     * cna_model_draw (models.h).
     */
    public static native int modelDraw(long model, float[] worldFloating, float[] viewFloating, float[] projectionFloating);

    /**
     * cna_model_effect_collection_destroy (models.h).
     */
    public static native int modelEffectCollectionDestroy(long collection);

    /**
     * cna_model_effect_collection_get_at (models.h).
     */
    public static native int modelEffectCollectionGetAt(long collection, long index, long[] outEffect);

    /**
     * cna_model_effect_collection_get_count (models.h).
     */
    public static native int modelEffectCollectionGetCount(long collection, long[] outCount);

    /**
     * cna_model_get_bone_transform_count (models.h).
     */
    public static native int modelGetBoneTransformCount(long model, long[] outCount);

    /**
     * cna_model_get_bones (models.h).
     */
    public static native int modelGetBones(long model, long[] outBones);

    /**
     * cna_model_get_bounding_sphere_ext (models.h).
     *
     * <p>outSphereFloating carries CNA_BoundingSphere in this order:
     * <ol start="0">
     *   <li>{@code center.x} (float)</li>
     *   <li>{@code center.y} (float)</li>
     *   <li>{@code center.z} (float)</li>
     *   <li>{@code radius} (float)</li>
     * </ol>
     */
    public static native int modelGetBoundingSphereExt(long model, boolean[] outHasValue, float[] outSphereFloating);

    /**
     * cna_model_get_camera_count_ext (models.h).
     */
    public static native int modelGetCameraCountExt(long model, long[] outCount);

    /**
     * cna_model_get_camera_ext (models.h).
     *
     * <p>outCameraIntegral carries CNA_ModelCameraEXT in this order:
     * <ol start="0">
     *   <li>{@code scene_node_index} (int32_t)</li>
     *   <li>{@code is_perspective} (CNA_Bool)</li>
     *   <li>{@code has_infinite_far_plane} (CNA_Bool)</li>
     *   <li>{@code has_authored_aspect_ratio} (CNA_Bool)</li>
     * </ol>
     *
     * <p>outCameraFloating carries CNA_ModelCameraEXT in this order:
     * <ol start="0">
     *   <li>{@code projection.m11} (float)</li>
     *   <li>{@code projection.m12} (float)</li>
     *   <li>{@code projection.m13} (float)</li>
     *   <li>{@code projection.m14} (float)</li>
     *   <li>{@code projection.m21} (float)</li>
     *   <li>{@code projection.m22} (float)</li>
     *   <li>{@code projection.m23} (float)</li>
     *   <li>{@code projection.m24} (float)</li>
     *   <li>{@code projection.m31} (float)</li>
     *   <li>{@code projection.m32} (float)</li>
     *   <li>{@code projection.m33} (float)</li>
     *   <li>{@code projection.m34} (float)</li>
     *   <li>{@code projection.m41} (float)</li>
     *   <li>{@code projection.m42} (float)</li>
     *   <li>{@code projection.m43} (float)</li>
     *   <li>{@code projection.m44} (float)</li>
     *   <li>{@code world_transform.m11} (float)</li>
     *   <li>{@code world_transform.m12} (float)</li>
     *   <li>{@code world_transform.m13} (float)</li>
     *   <li>{@code world_transform.m14} (float)</li>
     *   <li>{@code world_transform.m21} (float)</li>
     *   <li>{@code world_transform.m22} (float)</li>
     *   <li>{@code world_transform.m23} (float)</li>
     *   <li>{@code world_transform.m24} (float)</li>
     *   <li>{@code world_transform.m31} (float)</li>
     *   <li>{@code world_transform.m32} (float)</li>
     *   <li>{@code world_transform.m33} (float)</li>
     *   <li>{@code world_transform.m34} (float)</li>
     *   <li>{@code world_transform.m41} (float)</li>
     *   <li>{@code world_transform.m42} (float)</li>
     *   <li>{@code world_transform.m43} (float)</li>
     *   <li>{@code world_transform.m44} (float)</li>
     *   <li>{@code aspect_ratio} (float)</li>
     *   <li>{@code field_of_view} (float)</li>
     *   <li>{@code near_plane_distance} (float)</li>
     *   <li>{@code far_plane_distance} (float)</li>
     * </ol>
     */
    public static native int modelGetCameraExt(long model, long index, long[] outCameraIntegral, float[] outCameraFloating);

    /**
     * cna_model_get_camera_name_byte_count_ext (models.h).
     */
    public static native int modelGetCameraNameByteCountExt(long model, long index, long[] outByteCount);

    /**
     * cna_model_get_material_variant_count_ext (models.h).
     */
    public static native int modelGetMaterialVariantCountExt(long model, long[] outCount);

    /**
     * cna_model_get_material_variant_ext (models.h).
     */
    public static native int modelGetMaterialVariantExt(long model, int[] outValue);

    /**
     * cna_model_get_material_variant_name_byte_count_ext (models.h).
     */
    public static native int modelGetMaterialVariantNameByteCountExt(long model, long index, long[] outByteCount);

    /**
     * cna_model_get_meshes (models.h).
     */
    public static native int modelGetMeshes(long model, long[] outMeshes);

    /**
     * cna_model_get_root (models.h).
     */
    public static native int modelGetRoot(long model, boolean[] outHasRoot, long[] outRoot);

    /**
     * cna_model_get_skin_count_ext (models.h).
     */
    public static native int modelGetSkinCountExt(long model, long[] outCount);

    /**
     * cna_model_get_skin_ext (models.h).
     */
    public static native int modelGetSkinExt(long model, long index, boolean[] outHasData, long[] outMeshCount);

    /**
     * cna_model_get_skin_mesh_index_ext (models.h).
     */
    public static native int modelGetSkinMeshIndexExt(long model, long index, long meshIndex, long[] outModelMeshIndex);

    /**
     * cna_model_get_skin_name_byte_count_ext (models.h).
     */
    public static native int modelGetSkinNameByteCountExt(long model, long index, long[] outByteCount);

    /**
     * cna_model_mesh_collection_destroy (models.h).
     */
    public static native int modelMeshCollectionDestroy(long collection);

    /**
     * cna_model_mesh_collection_get_at (models.h).
     */
    public static native int modelMeshCollectionGetAt(long collection, long index, long[] outMesh);

    /**
     * cna_model_mesh_collection_get_count (models.h).
     */
    public static native int modelMeshCollectionGetCount(long collection, long[] outCount);

    /**
     * cna_model_mesh_copy_name (models.h).
     */
    public static native int modelMeshCopyName(long mesh, byte[] destination, long[] outByteCount);

    /**
     * cna_model_mesh_create_named (models.h).
     */
    public static native int modelMeshCreateNamed(long graphicsDevice, byte[] name, long[] parts, long[] outMesh);

    /**
     * cna_model_mesh_destroy (models.h).
     */
    public static native int modelMeshDestroy(long mesh);

    /**
     * cna_model_mesh_get_bounding_sphere (models.h).
     *
     * <p>outValueFloating carries CNA_BoundingSphere in this order:
     * <ol start="0">
     *   <li>{@code center.x} (float)</li>
     *   <li>{@code center.y} (float)</li>
     *   <li>{@code center.z} (float)</li>
     *   <li>{@code radius} (float)</li>
     * </ol>
     */
    public static native int modelMeshGetBoundingSphere(long mesh, float[] outValueFloating);

    /**
     * cna_model_mesh_get_effects (models.h).
     */
    public static native int modelMeshGetEffects(long mesh, long[] outEffects);

    /**
     * cna_model_mesh_get_mesh_parts (models.h).
     */
    public static native int modelMeshGetMeshParts(long mesh, long[] outParts);

    /**
     * cna_model_mesh_get_name_byte_count (models.h).
     */
    public static native int modelMeshGetNameByteCount(long mesh, long[] outByteCount);

    /**
     * cna_model_mesh_get_parent_bone (models.h).
     */
    public static native int modelMeshGetParentBone(long mesh, boolean[] outHasParent, long[] outParent);

    /**
     * cna_model_mesh_part_collection_destroy (models.h).
     */
    public static native int modelMeshPartCollectionDestroy(long collection);

    /**
     * cna_model_mesh_part_collection_get_at (models.h).
     */
    public static native int modelMeshPartCollectionGetAt(long collection, long index, long[] outPart);

    /**
     * cna_model_mesh_part_collection_get_count (models.h).
     */
    public static native int modelMeshPartCollectionGetCount(long collection, long[] outCount);

    /**
     * cna_model_mesh_part_create (models.h).
     */
    public static native int modelMeshPartCreate(long vertexBuffer, long indexBuffer, int numVertices, int primitiveCount, int startIndex, int vertexOffset, long[] outPart);

    /**
     * cna_model_mesh_part_destroy (models.h).
     */
    public static native int modelMeshPartDestroy(long part);

    /**
     * cna_model_mesh_part_get_effect (models.h).
     */
    public static native int modelMeshPartGetEffect(long part, boolean[] outHasEffect, long[] outEffect);

    /**
     * cna_model_mesh_part_get_index_buffer (models.h).
     */
    public static native int modelMeshPartGetIndexBuffer(long part, boolean[] outHasBuffer, long[] outBuffer);

    /**
     * cna_model_mesh_part_get_num_vertices (models.h).
     */
    public static native int modelMeshPartGetNumVertices(long part, int[] outValue);

    /**
     * cna_model_mesh_part_get_primitive_count (models.h).
     */
    public static native int modelMeshPartGetPrimitiveCount(long part, int[] outValue);

    /**
     * cna_model_mesh_part_get_start_index (models.h).
     */
    public static native int modelMeshPartGetStartIndex(long part, int[] outValue);

    /**
     * cna_model_mesh_part_get_vertex_buffer (models.h).
     */
    public static native int modelMeshPartGetVertexBuffer(long part, boolean[] outHasBuffer, long[] outBuffer);

    /**
     * cna_model_mesh_part_get_vertex_offset (models.h).
     */
    public static native int modelMeshPartGetVertexOffset(long part, int[] outValue);

    /**
     * cna_model_mesh_part_set_effect (models.h).
     */
    public static native int modelMeshPartSetEffect(long part, long effect);

    /**
     * cna_model_mesh_set_bounding_sphere (models.h).
     */
    public static native int modelMeshSetBoundingSphere(long mesh, float[] valueFloating);

    /**
     * cna_model_mesh_set_parent_bone (models.h).
     */
    public static native int modelMeshSetParentBone(long mesh, long parent);

    /**
     * cna_model_set_bone_transforms (models.h).
     */
    public static native int modelSetBoneTransforms(long model, float[] sourceFloating);

    /**
     * cna_model_set_material_variant_ext (models.h).
     */
    public static native int modelSetMaterialVariantExt(long model, int value);
}
