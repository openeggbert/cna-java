/* SPDX-License-Identifier: MS-PL */
/*
 * Generated JNI adapter for NativeModelExtensionRoutes.
 *
 * Produced by tools/native-abi/generate_jni.py from the live CNA C headers, and
 * included by cna_java_jni.c so it shares the one dispatch table. Do not edit.
 */

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_effectCopyTypeName(
    JNIEnv* environment, jclass declaring_class, jlong effect, jbyteArray destination, jlongArray out_byte_count)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t destination_written = 0;
    CNA_Result call_result = cna.effect_copy_type_name((CNA_EffectHandle)effect, (char*)destination_bytes, (uint64_t)destination_size, &destination_written);
    (*environment)->ReleaseByteArrayElements(environment, destination, destination_bytes, 0);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong destination_element = (jlong)destination_written;
        (*environment)->SetLongArrayRegion(
            environment, out_byte_count, 0, 1, &destination_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_effectGetTypeNameByteCount(
    JNIEnv* environment, jclass declaring_class, jlong effect, jlongArray out_byte_count)
{
    (void)environment;
    (void)declaring_class;
    uint64_t out_byte_count_value = 0;
    CNA_Result call_result = cna.effect_get_type_name_byte_count((CNA_EffectHandle)effect, &out_byte_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_byte_count_element = (jlong)out_byte_count_value;
        (*environment)->SetLongArrayRegion(
            environment, out_byte_count, 0, 1, &out_byte_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelBoneAddChild(
    JNIEnv* environment, jclass declaring_class, jlong bone, jlong child)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.model_bone_add_child((CNA_ModelBoneHandle)bone, (CNA_ModelBoneHandle)child);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelBoneCollectionDestroy(
    JNIEnv* environment, jclass declaring_class, jlong collection)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.model_bone_collection_destroy((CNA_ModelBoneCollectionHandle)collection);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelBoneCollectionGetAt(
    JNIEnv* environment, jclass declaring_class, jlong collection, jlong index, jlongArray out_bone)
{
    (void)environment;
    (void)declaring_class;
    CNA_ModelBoneHandle out_bone_value = 0;
    CNA_Result call_result = cna.model_bone_collection_get_at((CNA_ModelBoneCollectionHandle)collection, (uint64_t)index, &out_bone_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_bone_element = (jlong)out_bone_value;
        (*environment)->SetLongArrayRegion(
            environment, out_bone, 0, 1, &out_bone_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelBoneCollectionGetCount(
    JNIEnv* environment, jclass declaring_class, jlong collection, jlongArray out_count)
{
    (void)environment;
    (void)declaring_class;
    uint64_t out_count_value = 0;
    CNA_Result call_result = cna.model_bone_collection_get_count((CNA_ModelBoneCollectionHandle)collection, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_count_element = (jlong)out_count_value;
        (*environment)->SetLongArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelBoneCopyName(
    JNIEnv* environment, jclass declaring_class, jlong bone, jbyteArray destination, jlongArray out_byte_count)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t destination_written = 0;
    CNA_Result call_result = cna.model_bone_copy_name((CNA_ModelBoneHandle)bone, (char*)destination_bytes, (uint64_t)destination_size, &destination_written);
    (*environment)->ReleaseByteArrayElements(environment, destination, destination_bytes, 0);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong destination_element = (jlong)destination_written;
        (*environment)->SetLongArrayRegion(
            environment, out_byte_count, 0, 1, &destination_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelBoneCreate(
    JNIEnv* environment, jclass declaring_class, jint index, jbyteArray name, jlongArray out_bone)
{
    (void)environment;
    (void)declaring_class;
    jsize name_size = (*environment)->GetArrayLength(environment, name);
    jbyte* name_bytes = (*environment)->GetByteArrayElements(environment, name, NULL);
    if (name_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    CNA_StringView name_view = {(const char*)name_bytes, (uint64_t)name_size};
    CNA_ModelBoneHandle out_bone_value = 0;
    CNA_Result call_result = cna.model_bone_create((int32_t)index, name_view, &out_bone_value);
    (*environment)->ReleaseByteArrayElements(environment, name, name_bytes, JNI_ABORT);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_bone_element = (jlong)out_bone_value;
        (*environment)->SetLongArrayRegion(
            environment, out_bone, 0, 1, &out_bone_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelBoneDestroy(
    JNIEnv* environment, jclass declaring_class, jlong bone)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.model_bone_destroy((CNA_ModelBoneHandle)bone);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelBoneGetChildren(
    JNIEnv* environment, jclass declaring_class, jlong bone, jlongArray out_children)
{
    (void)environment;
    (void)declaring_class;
    CNA_ModelBoneCollectionHandle out_children_value = 0;
    CNA_Result call_result = cna.model_bone_get_children((CNA_ModelBoneHandle)bone, &out_children_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_children_element = (jlong)out_children_value;
        (*environment)->SetLongArrayRegion(
            environment, out_children, 0, 1, &out_children_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelBoneGetIndex(
    JNIEnv* environment, jclass declaring_class, jlong bone, jintArray out_index)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_index_value = 0;
    CNA_Result call_result = cna.model_bone_get_index((CNA_ModelBoneHandle)bone, &out_index_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_index_element = (jint)out_index_value;
        (*environment)->SetIntArrayRegion(
            environment, out_index, 0, 1, &out_index_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelBoneGetNameByteCount(
    JNIEnv* environment, jclass declaring_class, jlong bone, jlongArray out_byte_count)
{
    (void)environment;
    (void)declaring_class;
    uint64_t out_byte_count_value = 0;
    CNA_Result call_result = cna.model_bone_get_name_byte_count((CNA_ModelBoneHandle)bone, &out_byte_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_byte_count_element = (jlong)out_byte_count_value;
        (*environment)->SetLongArrayRegion(
            environment, out_byte_count, 0, 1, &out_byte_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelBoneGetParent(
    JNIEnv* environment, jclass declaring_class, jlong bone, jbooleanArray out_has_parent, jlongArray out_parent)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_has_parent_value = 0;
    CNA_ModelBoneHandle out_parent_value = 0;
    CNA_Result call_result = cna.model_bone_get_parent((CNA_ModelBoneHandle)bone, &out_has_parent_value, &out_parent_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_has_parent_element = (jboolean)out_has_parent_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_has_parent, 0, 1, &out_has_parent_element);
        jlong out_parent_element = (jlong)out_parent_value;
        (*environment)->SetLongArrayRegion(
            environment, out_parent, 0, 1, &out_parent_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelBoneGetTransform(
    JNIEnv* environment, jclass declaring_class, jlong bone, jfloatArray out_transformFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Matrix out_transform_value;
    memset(&out_transform_value, 0, sizeof out_transform_value);
    CNA_Result call_result = cna.model_bone_get_transform((CNA_ModelBoneHandle)bone, &out_transform_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_transformFloating_values[] = {
            (jfloat)out_transform_value.m11,
            (jfloat)out_transform_value.m12,
            (jfloat)out_transform_value.m13,
            (jfloat)out_transform_value.m14,
            (jfloat)out_transform_value.m21,
            (jfloat)out_transform_value.m22,
            (jfloat)out_transform_value.m23,
            (jfloat)out_transform_value.m24,
            (jfloat)out_transform_value.m31,
            (jfloat)out_transform_value.m32,
            (jfloat)out_transform_value.m33,
            (jfloat)out_transform_value.m34,
            (jfloat)out_transform_value.m41,
            (jfloat)out_transform_value.m42,
            (jfloat)out_transform_value.m43,
            (jfloat)out_transform_value.m44
        };
        (*environment)->SetFloatArrayRegion(environment, out_transformFloating, 0,
            (jsize)(sizeof out_transformFloating_values / sizeof out_transformFloating_values[0]),
            out_transformFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelBoneSetTransform(
    JNIEnv* environment, jclass declaring_class, jlong bone, jfloatArray transformFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Matrix transform_value;
    memset(&transform_value, 0, sizeof transform_value);
    {
        jfloat transformFloating_values[16];
        (*environment)->GetFloatArrayRegion(environment, transformFloating, 0, 16, transformFloating_values);
        transform_value.m11 = (float)transformFloating_values[0];
        transform_value.m12 = (float)transformFloating_values[1];
        transform_value.m13 = (float)transformFloating_values[2];
        transform_value.m14 = (float)transformFloating_values[3];
        transform_value.m21 = (float)transformFloating_values[4];
        transform_value.m22 = (float)transformFloating_values[5];
        transform_value.m23 = (float)transformFloating_values[6];
        transform_value.m24 = (float)transformFloating_values[7];
        transform_value.m31 = (float)transformFloating_values[8];
        transform_value.m32 = (float)transformFloating_values[9];
        transform_value.m33 = (float)transformFloating_values[10];
        transform_value.m34 = (float)transformFloating_values[11];
        transform_value.m41 = (float)transformFloating_values[12];
        transform_value.m42 = (float)transformFloating_values[13];
        transform_value.m43 = (float)transformFloating_values[14];
        transform_value.m44 = (float)transformFloating_values[15];
    }
    CNA_Result call_result = cna.model_bone_set_transform((CNA_ModelBoneHandle)bone, transform_value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelCopyAbsoluteBoneTransforms(
    JNIEnv* environment, jclass declaring_class, jlong model, jfloatArray destinationFloating, jlongArray out_count)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_count = (*environment)->GetArrayLength(environment, destinationFloating) / 16;
    CNA_Matrix* destination_values = (CNA_Matrix*)calloc(
        (size_t)destination_count + 1U, sizeof(*destination_values));
    if (destination_values == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    {
        jsize destinationFloating_length = (*environment)->GetArrayLength(environment, destinationFloating);
        jfloat* destinationFloating_values = (jfloat*)malloc(
            ((size_t)destinationFloating_length + 1U) * sizeof(*destinationFloating_values));
        if (destinationFloating_values == NULL) {
            free(destination_values);
            return (jint)CNA_RESULT_OUT_OF_MEMORY;
        }
        (*environment)->GetFloatArrayRegion(environment, destinationFloating, 0, destinationFloating_length, destinationFloating_values);
        for (jsize element = 0; element < destination_count; ++element) {
            destination_values[element].m11 = (float)destinationFloating_values[element * 16 + 0];
            destination_values[element].m12 = (float)destinationFloating_values[element * 16 + 1];
            destination_values[element].m13 = (float)destinationFloating_values[element * 16 + 2];
            destination_values[element].m14 = (float)destinationFloating_values[element * 16 + 3];
            destination_values[element].m21 = (float)destinationFloating_values[element * 16 + 4];
            destination_values[element].m22 = (float)destinationFloating_values[element * 16 + 5];
            destination_values[element].m23 = (float)destinationFloating_values[element * 16 + 6];
            destination_values[element].m24 = (float)destinationFloating_values[element * 16 + 7];
            destination_values[element].m31 = (float)destinationFloating_values[element * 16 + 8];
            destination_values[element].m32 = (float)destinationFloating_values[element * 16 + 9];
            destination_values[element].m33 = (float)destinationFloating_values[element * 16 + 10];
            destination_values[element].m34 = (float)destinationFloating_values[element * 16 + 11];
            destination_values[element].m41 = (float)destinationFloating_values[element * 16 + 12];
            destination_values[element].m42 = (float)destinationFloating_values[element * 16 + 13];
            destination_values[element].m43 = (float)destinationFloating_values[element * 16 + 14];
            destination_values[element].m44 = (float)destinationFloating_values[element * 16 + 15];
        }
        free(destinationFloating_values);
    }
    uint64_t out_count_value = 0;
    CNA_Result call_result = cna.model_copy_absolute_bone_transforms((CNA_ModelHandle)model, destination_values, (uint64_t)destination_count, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS) {
        {
            jfloat* destinationFloating_out = (jfloat*)malloc(
                ((size_t)destination_count * 16 + 1U) * sizeof(*destinationFloating_out));
            if (destinationFloating_out != NULL) {
                for (jsize element = 0; element < destination_count; ++element) {
                    destinationFloating_out[element * 16 + 0] = (jfloat)destination_values[element].m11;
                    destinationFloating_out[element * 16 + 1] = (jfloat)destination_values[element].m12;
                    destinationFloating_out[element * 16 + 2] = (jfloat)destination_values[element].m13;
                    destinationFloating_out[element * 16 + 3] = (jfloat)destination_values[element].m14;
                    destinationFloating_out[element * 16 + 4] = (jfloat)destination_values[element].m21;
                    destinationFloating_out[element * 16 + 5] = (jfloat)destination_values[element].m22;
                    destinationFloating_out[element * 16 + 6] = (jfloat)destination_values[element].m23;
                    destinationFloating_out[element * 16 + 7] = (jfloat)destination_values[element].m24;
                    destinationFloating_out[element * 16 + 8] = (jfloat)destination_values[element].m31;
                    destinationFloating_out[element * 16 + 9] = (jfloat)destination_values[element].m32;
                    destinationFloating_out[element * 16 + 10] = (jfloat)destination_values[element].m33;
                    destinationFloating_out[element * 16 + 11] = (jfloat)destination_values[element].m34;
                    destinationFloating_out[element * 16 + 12] = (jfloat)destination_values[element].m41;
                    destinationFloating_out[element * 16 + 13] = (jfloat)destination_values[element].m42;
                    destinationFloating_out[element * 16 + 14] = (jfloat)destination_values[element].m43;
                    destinationFloating_out[element * 16 + 15] = (jfloat)destination_values[element].m44;
                }
                (*environment)->SetFloatArrayRegion(
                    environment, destinationFloating, 0,
                    (jsize)(destination_count * 16), destinationFloating_out);
                free(destinationFloating_out);
            }
        }
    }
    free(destination_values);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_count_element = (jlong)out_count_value;
        (*environment)->SetLongArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelCopyBoneTransforms(
    JNIEnv* environment, jclass declaring_class, jlong model, jfloatArray destinationFloating, jlongArray out_count)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_count = (*environment)->GetArrayLength(environment, destinationFloating) / 16;
    CNA_Matrix* destination_values = (CNA_Matrix*)calloc(
        (size_t)destination_count + 1U, sizeof(*destination_values));
    if (destination_values == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    {
        jsize destinationFloating_length = (*environment)->GetArrayLength(environment, destinationFloating);
        jfloat* destinationFloating_values = (jfloat*)malloc(
            ((size_t)destinationFloating_length + 1U) * sizeof(*destinationFloating_values));
        if (destinationFloating_values == NULL) {
            free(destination_values);
            return (jint)CNA_RESULT_OUT_OF_MEMORY;
        }
        (*environment)->GetFloatArrayRegion(environment, destinationFloating, 0, destinationFloating_length, destinationFloating_values);
        for (jsize element = 0; element < destination_count; ++element) {
            destination_values[element].m11 = (float)destinationFloating_values[element * 16 + 0];
            destination_values[element].m12 = (float)destinationFloating_values[element * 16 + 1];
            destination_values[element].m13 = (float)destinationFloating_values[element * 16 + 2];
            destination_values[element].m14 = (float)destinationFloating_values[element * 16 + 3];
            destination_values[element].m21 = (float)destinationFloating_values[element * 16 + 4];
            destination_values[element].m22 = (float)destinationFloating_values[element * 16 + 5];
            destination_values[element].m23 = (float)destinationFloating_values[element * 16 + 6];
            destination_values[element].m24 = (float)destinationFloating_values[element * 16 + 7];
            destination_values[element].m31 = (float)destinationFloating_values[element * 16 + 8];
            destination_values[element].m32 = (float)destinationFloating_values[element * 16 + 9];
            destination_values[element].m33 = (float)destinationFloating_values[element * 16 + 10];
            destination_values[element].m34 = (float)destinationFloating_values[element * 16 + 11];
            destination_values[element].m41 = (float)destinationFloating_values[element * 16 + 12];
            destination_values[element].m42 = (float)destinationFloating_values[element * 16 + 13];
            destination_values[element].m43 = (float)destinationFloating_values[element * 16 + 14];
            destination_values[element].m44 = (float)destinationFloating_values[element * 16 + 15];
        }
        free(destinationFloating_values);
    }
    uint64_t out_count_value = 0;
    CNA_Result call_result = cna.model_copy_bone_transforms((CNA_ModelHandle)model, destination_values, (uint64_t)destination_count, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS) {
        {
            jfloat* destinationFloating_out = (jfloat*)malloc(
                ((size_t)destination_count * 16 + 1U) * sizeof(*destinationFloating_out));
            if (destinationFloating_out != NULL) {
                for (jsize element = 0; element < destination_count; ++element) {
                    destinationFloating_out[element * 16 + 0] = (jfloat)destination_values[element].m11;
                    destinationFloating_out[element * 16 + 1] = (jfloat)destination_values[element].m12;
                    destinationFloating_out[element * 16 + 2] = (jfloat)destination_values[element].m13;
                    destinationFloating_out[element * 16 + 3] = (jfloat)destination_values[element].m14;
                    destinationFloating_out[element * 16 + 4] = (jfloat)destination_values[element].m21;
                    destinationFloating_out[element * 16 + 5] = (jfloat)destination_values[element].m22;
                    destinationFloating_out[element * 16 + 6] = (jfloat)destination_values[element].m23;
                    destinationFloating_out[element * 16 + 7] = (jfloat)destination_values[element].m24;
                    destinationFloating_out[element * 16 + 8] = (jfloat)destination_values[element].m31;
                    destinationFloating_out[element * 16 + 9] = (jfloat)destination_values[element].m32;
                    destinationFloating_out[element * 16 + 10] = (jfloat)destination_values[element].m33;
                    destinationFloating_out[element * 16 + 11] = (jfloat)destination_values[element].m34;
                    destinationFloating_out[element * 16 + 12] = (jfloat)destination_values[element].m41;
                    destinationFloating_out[element * 16 + 13] = (jfloat)destination_values[element].m42;
                    destinationFloating_out[element * 16 + 14] = (jfloat)destination_values[element].m43;
                    destinationFloating_out[element * 16 + 15] = (jfloat)destination_values[element].m44;
                }
                (*environment)->SetFloatArrayRegion(
                    environment, destinationFloating, 0,
                    (jsize)(destination_count * 16), destinationFloating_out);
                free(destinationFloating_out);
            }
        }
    }
    free(destination_values);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_count_element = (jlong)out_count_value;
        (*environment)->SetLongArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelCopyCameraNameExt(
    JNIEnv* environment, jclass declaring_class, jlong model, jlong index, jbyteArray destination, jlongArray out_byte_count)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t destination_written = 0;
    CNA_Result call_result = cna.model_copy_camera_name_ext((CNA_ModelHandle)model, (uint64_t)index, (char*)destination_bytes, (uint64_t)destination_size, &destination_written);
    (*environment)->ReleaseByteArrayElements(environment, destination, destination_bytes, 0);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong destination_element = (jlong)destination_written;
        (*environment)->SetLongArrayRegion(
            environment, out_byte_count, 0, 1, &destination_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelCopyMaterialVariantNameExt(
    JNIEnv* environment, jclass declaring_class, jlong model, jlong index, jbyteArray destination, jlongArray out_byte_count)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t destination_written = 0;
    CNA_Result call_result = cna.model_copy_material_variant_name_ext((CNA_ModelHandle)model, (uint64_t)index, (char*)destination_bytes, (uint64_t)destination_size, &destination_written);
    (*environment)->ReleaseByteArrayElements(environment, destination, destination_bytes, 0);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong destination_element = (jlong)destination_written;
        (*environment)->SetLongArrayRegion(
            environment, out_byte_count, 0, 1, &destination_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelCopySkinNameExt(
    JNIEnv* environment, jclass declaring_class, jlong model, jlong index, jbyteArray destination, jlongArray out_byte_count)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t destination_written = 0;
    CNA_Result call_result = cna.model_copy_skin_name_ext((CNA_ModelHandle)model, (uint64_t)index, (char*)destination_bytes, (uint64_t)destination_size, &destination_written);
    (*environment)->ReleaseByteArrayElements(environment, destination, destination_bytes, 0);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong destination_element = (jlong)destination_written;
        (*environment)->SetLongArrayRegion(
            environment, out_byte_count, 0, 1, &destination_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelCreateWithParents(
    JNIEnv* environment, jclass declaring_class, jlong graphics_device, jlongArray bones, jlongArray meshes, jlongArray mesh_parents, jlong root_bone_index, jlongArray out_model)
{
    (void)environment;
    (void)declaring_class;
    jsize bones_size = (*environment)->GetArrayLength(environment, bones);
    jlong* bones_elements = (*environment)->GetLongArrayElements(environment, bones, NULL);
    if (bones_elements == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    CNA_ModelBoneHandle* bones_values = (CNA_ModelBoneHandle*)malloc(
        ((size_t)bones_size + 1U) * sizeof(*bones_values));
    if (bones_values == NULL) {
        (*environment)->ReleaseLongArrayElements(
            environment, bones, bones_elements, JNI_ABORT);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    for (jsize index = 0; index < bones_size; ++index) {
        bones_values[index] = (CNA_ModelBoneHandle)bones_elements[index];
    }
    jsize meshes_size = (*environment)->GetArrayLength(environment, meshes);
    jlong* meshes_elements = (*environment)->GetLongArrayElements(environment, meshes, NULL);
    if (meshes_elements == NULL) {
    free(bones_values);
    (*environment)->ReleaseLongArrayElements(
        environment, bones, bones_elements, JNI_ABORT);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    CNA_ModelMeshHandle* meshes_values = (CNA_ModelMeshHandle*)malloc(
        ((size_t)meshes_size + 1U) * sizeof(*meshes_values));
    if (meshes_values == NULL) {
        (*environment)->ReleaseLongArrayElements(
            environment, meshes, meshes_elements, JNI_ABORT);
    free(bones_values);
    (*environment)->ReleaseLongArrayElements(
        environment, bones, bones_elements, JNI_ABORT);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    for (jsize index = 0; index < meshes_size; ++index) {
        meshes_values[index] = (CNA_ModelMeshHandle)meshes_elements[index];
    }
    jsize mesh_parents_size = (*environment)->GetArrayLength(environment, mesh_parents);
    jlong* mesh_parents_elements = (*environment)->GetLongArrayElements(environment, mesh_parents, NULL);
    if (mesh_parents_elements == NULL) {
    free(meshes_values);
    (*environment)->ReleaseLongArrayElements(
        environment, meshes, meshes_elements, JNI_ABORT);
    free(bones_values);
    (*environment)->ReleaseLongArrayElements(
        environment, bones, bones_elements, JNI_ABORT);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    CNA_ModelBoneHandle* mesh_parents_values = (CNA_ModelBoneHandle*)malloc(
        ((size_t)mesh_parents_size + 1U) * sizeof(*mesh_parents_values));
    if (mesh_parents_values == NULL) {
        (*environment)->ReleaseLongArrayElements(
            environment, mesh_parents, mesh_parents_elements, JNI_ABORT);
    free(meshes_values);
    (*environment)->ReleaseLongArrayElements(
        environment, meshes, meshes_elements, JNI_ABORT);
    free(bones_values);
    (*environment)->ReleaseLongArrayElements(
        environment, bones, bones_elements, JNI_ABORT);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    for (jsize index = 0; index < mesh_parents_size; ++index) {
        mesh_parents_values[index] = (CNA_ModelBoneHandle)mesh_parents_elements[index];
    }
    CNA_ModelHandle out_model_value = 0;
    CNA_Result call_result = cna.model_create_with_parents((CNA_Handle)graphics_device, bones_values, (uint64_t)bones_size, meshes_values, (uint64_t)meshes_size, mesh_parents_values, (uint64_t)mesh_parents_size, (uint64_t)root_bone_index, &out_model_value);
    free(bones_values);
    (*environment)->ReleaseLongArrayElements(
        environment, bones, bones_elements, JNI_ABORT);
    free(meshes_values);
    (*environment)->ReleaseLongArrayElements(
        environment, meshes, meshes_elements, JNI_ABORT);
    free(mesh_parents_values);
    (*environment)->ReleaseLongArrayElements(
        environment, mesh_parents, mesh_parents_elements, JNI_ABORT);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_model_element = (jlong)out_model_value;
        (*environment)->SetLongArrayRegion(
            environment, out_model, 0, 1, &out_model_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelDestroy(
    JNIEnv* environment, jclass declaring_class, jlong model)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.model_destroy((CNA_ModelHandle)model);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelDraw(
    JNIEnv* environment, jclass declaring_class, jlong model, jfloatArray worldFloating, jfloatArray viewFloating, jfloatArray projectionFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Matrix world_value;
    memset(&world_value, 0, sizeof world_value);
    {
        jfloat worldFloating_values[16];
        (*environment)->GetFloatArrayRegion(environment, worldFloating, 0, 16, worldFloating_values);
        world_value.m11 = (float)worldFloating_values[0];
        world_value.m12 = (float)worldFloating_values[1];
        world_value.m13 = (float)worldFloating_values[2];
        world_value.m14 = (float)worldFloating_values[3];
        world_value.m21 = (float)worldFloating_values[4];
        world_value.m22 = (float)worldFloating_values[5];
        world_value.m23 = (float)worldFloating_values[6];
        world_value.m24 = (float)worldFloating_values[7];
        world_value.m31 = (float)worldFloating_values[8];
        world_value.m32 = (float)worldFloating_values[9];
        world_value.m33 = (float)worldFloating_values[10];
        world_value.m34 = (float)worldFloating_values[11];
        world_value.m41 = (float)worldFloating_values[12];
        world_value.m42 = (float)worldFloating_values[13];
        world_value.m43 = (float)worldFloating_values[14];
        world_value.m44 = (float)worldFloating_values[15];
    }
    CNA_Matrix view_value;
    memset(&view_value, 0, sizeof view_value);
    {
        jfloat viewFloating_values[16];
        (*environment)->GetFloatArrayRegion(environment, viewFloating, 0, 16, viewFloating_values);
        view_value.m11 = (float)viewFloating_values[0];
        view_value.m12 = (float)viewFloating_values[1];
        view_value.m13 = (float)viewFloating_values[2];
        view_value.m14 = (float)viewFloating_values[3];
        view_value.m21 = (float)viewFloating_values[4];
        view_value.m22 = (float)viewFloating_values[5];
        view_value.m23 = (float)viewFloating_values[6];
        view_value.m24 = (float)viewFloating_values[7];
        view_value.m31 = (float)viewFloating_values[8];
        view_value.m32 = (float)viewFloating_values[9];
        view_value.m33 = (float)viewFloating_values[10];
        view_value.m34 = (float)viewFloating_values[11];
        view_value.m41 = (float)viewFloating_values[12];
        view_value.m42 = (float)viewFloating_values[13];
        view_value.m43 = (float)viewFloating_values[14];
        view_value.m44 = (float)viewFloating_values[15];
    }
    CNA_Matrix projection_value;
    memset(&projection_value, 0, sizeof projection_value);
    {
        jfloat projectionFloating_values[16];
        (*environment)->GetFloatArrayRegion(environment, projectionFloating, 0, 16, projectionFloating_values);
        projection_value.m11 = (float)projectionFloating_values[0];
        projection_value.m12 = (float)projectionFloating_values[1];
        projection_value.m13 = (float)projectionFloating_values[2];
        projection_value.m14 = (float)projectionFloating_values[3];
        projection_value.m21 = (float)projectionFloating_values[4];
        projection_value.m22 = (float)projectionFloating_values[5];
        projection_value.m23 = (float)projectionFloating_values[6];
        projection_value.m24 = (float)projectionFloating_values[7];
        projection_value.m31 = (float)projectionFloating_values[8];
        projection_value.m32 = (float)projectionFloating_values[9];
        projection_value.m33 = (float)projectionFloating_values[10];
        projection_value.m34 = (float)projectionFloating_values[11];
        projection_value.m41 = (float)projectionFloating_values[12];
        projection_value.m42 = (float)projectionFloating_values[13];
        projection_value.m43 = (float)projectionFloating_values[14];
        projection_value.m44 = (float)projectionFloating_values[15];
    }
    CNA_Result call_result = cna.model_draw((CNA_ModelHandle)model, world_value, view_value, projection_value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelEffectCollectionDestroy(
    JNIEnv* environment, jclass declaring_class, jlong collection)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.model_effect_collection_destroy((CNA_ModelEffectCollectionHandle)collection);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelEffectCollectionGetAt(
    JNIEnv* environment, jclass declaring_class, jlong collection, jlong index, jlongArray out_effect)
{
    (void)environment;
    (void)declaring_class;
    CNA_EffectHandle out_effect_value = 0;
    CNA_Result call_result = cna.model_effect_collection_get_at((CNA_ModelEffectCollectionHandle)collection, (uint64_t)index, &out_effect_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_effect_element = (jlong)out_effect_value;
        (*environment)->SetLongArrayRegion(
            environment, out_effect, 0, 1, &out_effect_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelEffectCollectionGetCount(
    JNIEnv* environment, jclass declaring_class, jlong collection, jlongArray out_count)
{
    (void)environment;
    (void)declaring_class;
    uint64_t out_count_value = 0;
    CNA_Result call_result = cna.model_effect_collection_get_count((CNA_ModelEffectCollectionHandle)collection, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_count_element = (jlong)out_count_value;
        (*environment)->SetLongArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelGetBoneTransformCount(
    JNIEnv* environment, jclass declaring_class, jlong model, jlongArray out_count)
{
    (void)environment;
    (void)declaring_class;
    uint64_t out_count_value = 0;
    CNA_Result call_result = cna.model_get_bone_transform_count((CNA_ModelHandle)model, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_count_element = (jlong)out_count_value;
        (*environment)->SetLongArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelGetBones(
    JNIEnv* environment, jclass declaring_class, jlong model, jlongArray out_bones)
{
    (void)environment;
    (void)declaring_class;
    CNA_ModelBoneCollectionHandle out_bones_value = 0;
    CNA_Result call_result = cna.model_get_bones((CNA_ModelHandle)model, &out_bones_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_bones_element = (jlong)out_bones_value;
        (*environment)->SetLongArrayRegion(
            environment, out_bones, 0, 1, &out_bones_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelGetBoundingSphereExt(
    JNIEnv* environment, jclass declaring_class, jlong model, jbooleanArray out_has_value, jfloatArray out_sphereFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_has_value_value = 0;
    CNA_BoundingSphere out_sphere_value;
    memset(&out_sphere_value, 0, sizeof out_sphere_value);
    CNA_Result call_result = cna.model_get_bounding_sphere_ext((CNA_ModelHandle)model, &out_has_value_value, &out_sphere_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_has_value_element = (jboolean)out_has_value_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_has_value, 0, 1, &out_has_value_element);
        jfloat out_sphereFloating_values[] = {
            (jfloat)out_sphere_value.center.x,
            (jfloat)out_sphere_value.center.y,
            (jfloat)out_sphere_value.center.z,
            (jfloat)out_sphere_value.radius
        };
        (*environment)->SetFloatArrayRegion(environment, out_sphereFloating, 0,
            (jsize)(sizeof out_sphereFloating_values / sizeof out_sphereFloating_values[0]),
            out_sphereFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelGetCameraCountExt(
    JNIEnv* environment, jclass declaring_class, jlong model, jlongArray out_count)
{
    (void)environment;
    (void)declaring_class;
    uint64_t out_count_value = 0;
    CNA_Result call_result = cna.model_get_camera_count_ext((CNA_ModelHandle)model, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_count_element = (jlong)out_count_value;
        (*environment)->SetLongArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelGetCameraExt(
    JNIEnv* environment, jclass declaring_class, jlong model, jlong index, jlongArray out_cameraIntegral, jfloatArray out_cameraFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_ModelCameraEXT out_camera_value;
    memset(&out_camera_value, 0, sizeof out_camera_value);
    out_camera_value.struct_size = (uint32_t)(sizeof out_camera_value);
    out_camera_value.struct_version = UINT32_C(1);
    CNA_Result call_result = cna.model_get_camera_ext((CNA_ModelHandle)model, (uint64_t)index, &out_camera_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_cameraIntegral_values[] = {
            (jlong)out_camera_value.scene_node_index,
            (jlong)out_camera_value.is_perspective,
            (jlong)out_camera_value.has_infinite_far_plane,
            (jlong)out_camera_value.has_authored_aspect_ratio
        };
        (*environment)->SetLongArrayRegion(environment, out_cameraIntegral, 0,
            (jsize)(sizeof out_cameraIntegral_values / sizeof out_cameraIntegral_values[0]),
            out_cameraIntegral_values);
        jfloat out_cameraFloating_values[] = {
            (jfloat)out_camera_value.projection.m11,
            (jfloat)out_camera_value.projection.m12,
            (jfloat)out_camera_value.projection.m13,
            (jfloat)out_camera_value.projection.m14,
            (jfloat)out_camera_value.projection.m21,
            (jfloat)out_camera_value.projection.m22,
            (jfloat)out_camera_value.projection.m23,
            (jfloat)out_camera_value.projection.m24,
            (jfloat)out_camera_value.projection.m31,
            (jfloat)out_camera_value.projection.m32,
            (jfloat)out_camera_value.projection.m33,
            (jfloat)out_camera_value.projection.m34,
            (jfloat)out_camera_value.projection.m41,
            (jfloat)out_camera_value.projection.m42,
            (jfloat)out_camera_value.projection.m43,
            (jfloat)out_camera_value.projection.m44,
            (jfloat)out_camera_value.world_transform.m11,
            (jfloat)out_camera_value.world_transform.m12,
            (jfloat)out_camera_value.world_transform.m13,
            (jfloat)out_camera_value.world_transform.m14,
            (jfloat)out_camera_value.world_transform.m21,
            (jfloat)out_camera_value.world_transform.m22,
            (jfloat)out_camera_value.world_transform.m23,
            (jfloat)out_camera_value.world_transform.m24,
            (jfloat)out_camera_value.world_transform.m31,
            (jfloat)out_camera_value.world_transform.m32,
            (jfloat)out_camera_value.world_transform.m33,
            (jfloat)out_camera_value.world_transform.m34,
            (jfloat)out_camera_value.world_transform.m41,
            (jfloat)out_camera_value.world_transform.m42,
            (jfloat)out_camera_value.world_transform.m43,
            (jfloat)out_camera_value.world_transform.m44,
            (jfloat)out_camera_value.aspect_ratio,
            (jfloat)out_camera_value.field_of_view,
            (jfloat)out_camera_value.near_plane_distance,
            (jfloat)out_camera_value.far_plane_distance
        };
        (*environment)->SetFloatArrayRegion(environment, out_cameraFloating, 0,
            (jsize)(sizeof out_cameraFloating_values / sizeof out_cameraFloating_values[0]),
            out_cameraFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelGetCameraNameByteCountExt(
    JNIEnv* environment, jclass declaring_class, jlong model, jlong index, jlongArray out_byte_count)
{
    (void)environment;
    (void)declaring_class;
    uint64_t out_byte_count_value = 0;
    CNA_Result call_result = cna.model_get_camera_name_byte_count_ext((CNA_ModelHandle)model, (uint64_t)index, &out_byte_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_byte_count_element = (jlong)out_byte_count_value;
        (*environment)->SetLongArrayRegion(
            environment, out_byte_count, 0, 1, &out_byte_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelGetMaterialVariantCountExt(
    JNIEnv* environment, jclass declaring_class, jlong model, jlongArray out_count)
{
    (void)environment;
    (void)declaring_class;
    uint64_t out_count_value = 0;
    CNA_Result call_result = cna.model_get_material_variant_count_ext((CNA_ModelHandle)model, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_count_element = (jlong)out_count_value;
        (*environment)->SetLongArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelGetMaterialVariantExt(
    JNIEnv* environment, jclass declaring_class, jlong model, jintArray out_value)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_value_value = 0;
    CNA_Result call_result = cna.model_get_material_variant_ext((CNA_ModelHandle)model, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_value_element = (jint)out_value_value;
        (*environment)->SetIntArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelGetMaterialVariantNameByteCountExt(
    JNIEnv* environment, jclass declaring_class, jlong model, jlong index, jlongArray out_byte_count)
{
    (void)environment;
    (void)declaring_class;
    uint64_t out_byte_count_value = 0;
    CNA_Result call_result = cna.model_get_material_variant_name_byte_count_ext((CNA_ModelHandle)model, (uint64_t)index, &out_byte_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_byte_count_element = (jlong)out_byte_count_value;
        (*environment)->SetLongArrayRegion(
            environment, out_byte_count, 0, 1, &out_byte_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelGetMeshes(
    JNIEnv* environment, jclass declaring_class, jlong model, jlongArray out_meshes)
{
    (void)environment;
    (void)declaring_class;
    CNA_ModelMeshCollectionHandle out_meshes_value = 0;
    CNA_Result call_result = cna.model_get_meshes((CNA_ModelHandle)model, &out_meshes_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_meshes_element = (jlong)out_meshes_value;
        (*environment)->SetLongArrayRegion(
            environment, out_meshes, 0, 1, &out_meshes_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelGetRoot(
    JNIEnv* environment, jclass declaring_class, jlong model, jbooleanArray out_has_root, jlongArray out_root)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_has_root_value = 0;
    CNA_ModelBoneHandle out_root_value = 0;
    CNA_Result call_result = cna.model_get_root((CNA_ModelHandle)model, &out_has_root_value, &out_root_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_has_root_element = (jboolean)out_has_root_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_has_root, 0, 1, &out_has_root_element);
        jlong out_root_element = (jlong)out_root_value;
        (*environment)->SetLongArrayRegion(
            environment, out_root, 0, 1, &out_root_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelGetSkinCountExt(
    JNIEnv* environment, jclass declaring_class, jlong model, jlongArray out_count)
{
    (void)environment;
    (void)declaring_class;
    uint64_t out_count_value = 0;
    CNA_Result call_result = cna.model_get_skin_count_ext((CNA_ModelHandle)model, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_count_element = (jlong)out_count_value;
        (*environment)->SetLongArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelGetSkinExt(
    JNIEnv* environment, jclass declaring_class, jlong model, jlong index, jbooleanArray out_has_data, jlongArray out_mesh_count)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_has_data_value = 0;
    uint64_t out_mesh_count_value = 0;
    CNA_Result call_result = cna.model_get_skin_ext((CNA_ModelHandle)model, (uint64_t)index, &out_has_data_value, &out_mesh_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_has_data_element = (jboolean)out_has_data_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_has_data, 0, 1, &out_has_data_element);
        jlong out_mesh_count_element = (jlong)out_mesh_count_value;
        (*environment)->SetLongArrayRegion(
            environment, out_mesh_count, 0, 1, &out_mesh_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelGetSkinMeshIndexExt(
    JNIEnv* environment, jclass declaring_class, jlong model, jlong index, jlong mesh_index, jlongArray out_model_mesh_index)
{
    (void)environment;
    (void)declaring_class;
    uint64_t out_model_mesh_index_value = 0;
    CNA_Result call_result = cna.model_get_skin_mesh_index_ext((CNA_ModelHandle)model, (uint64_t)index, (uint64_t)mesh_index, &out_model_mesh_index_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_model_mesh_index_element = (jlong)out_model_mesh_index_value;
        (*environment)->SetLongArrayRegion(
            environment, out_model_mesh_index, 0, 1, &out_model_mesh_index_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelGetSkinNameByteCountExt(
    JNIEnv* environment, jclass declaring_class, jlong model, jlong index, jlongArray out_byte_count)
{
    (void)environment;
    (void)declaring_class;
    uint64_t out_byte_count_value = 0;
    CNA_Result call_result = cna.model_get_skin_name_byte_count_ext((CNA_ModelHandle)model, (uint64_t)index, &out_byte_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_byte_count_element = (jlong)out_byte_count_value;
        (*environment)->SetLongArrayRegion(
            environment, out_byte_count, 0, 1, &out_byte_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelMeshCollectionDestroy(
    JNIEnv* environment, jclass declaring_class, jlong collection)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.model_mesh_collection_destroy((CNA_ModelMeshCollectionHandle)collection);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelMeshCollectionGetAt(
    JNIEnv* environment, jclass declaring_class, jlong collection, jlong index, jlongArray out_mesh)
{
    (void)environment;
    (void)declaring_class;
    CNA_ModelMeshHandle out_mesh_value = 0;
    CNA_Result call_result = cna.model_mesh_collection_get_at((CNA_ModelMeshCollectionHandle)collection, (uint64_t)index, &out_mesh_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_mesh_element = (jlong)out_mesh_value;
        (*environment)->SetLongArrayRegion(
            environment, out_mesh, 0, 1, &out_mesh_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelMeshCollectionGetCount(
    JNIEnv* environment, jclass declaring_class, jlong collection, jlongArray out_count)
{
    (void)environment;
    (void)declaring_class;
    uint64_t out_count_value = 0;
    CNA_Result call_result = cna.model_mesh_collection_get_count((CNA_ModelMeshCollectionHandle)collection, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_count_element = (jlong)out_count_value;
        (*environment)->SetLongArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelMeshCopyName(
    JNIEnv* environment, jclass declaring_class, jlong mesh, jbyteArray destination, jlongArray out_byte_count)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t destination_written = 0;
    CNA_Result call_result = cna.model_mesh_copy_name((CNA_ModelMeshHandle)mesh, (char*)destination_bytes, (uint64_t)destination_size, &destination_written);
    (*environment)->ReleaseByteArrayElements(environment, destination, destination_bytes, 0);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong destination_element = (jlong)destination_written;
        (*environment)->SetLongArrayRegion(
            environment, out_byte_count, 0, 1, &destination_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelMeshCreateNamed(
    JNIEnv* environment, jclass declaring_class, jlong graphics_device, jbyteArray name, jlongArray parts, jlongArray out_mesh)
{
    (void)environment;
    (void)declaring_class;
    jsize name_size = (*environment)->GetArrayLength(environment, name);
    jbyte* name_bytes = (*environment)->GetByteArrayElements(environment, name, NULL);
    if (name_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    CNA_StringView name_view = {(const char*)name_bytes, (uint64_t)name_size};
    jsize parts_size = (*environment)->GetArrayLength(environment, parts);
    jlong* parts_elements = (*environment)->GetLongArrayElements(environment, parts, NULL);
    if (parts_elements == NULL) {
    (*environment)->ReleaseByteArrayElements(environment, name, name_bytes, JNI_ABORT);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    CNA_ModelMeshPartHandle* parts_values = (CNA_ModelMeshPartHandle*)malloc(
        ((size_t)parts_size + 1U) * sizeof(*parts_values));
    if (parts_values == NULL) {
        (*environment)->ReleaseLongArrayElements(
            environment, parts, parts_elements, JNI_ABORT);
    (*environment)->ReleaseByteArrayElements(environment, name, name_bytes, JNI_ABORT);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    for (jsize index = 0; index < parts_size; ++index) {
        parts_values[index] = (CNA_ModelMeshPartHandle)parts_elements[index];
    }
    CNA_ModelMeshHandle out_mesh_value = 0;
    CNA_Result call_result = cna.model_mesh_create_named((CNA_Handle)graphics_device, name_view, parts_values, (uint64_t)parts_size, &out_mesh_value);
    (*environment)->ReleaseByteArrayElements(environment, name, name_bytes, JNI_ABORT);
    free(parts_values);
    (*environment)->ReleaseLongArrayElements(
        environment, parts, parts_elements, JNI_ABORT);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_mesh_element = (jlong)out_mesh_value;
        (*environment)->SetLongArrayRegion(
            environment, out_mesh, 0, 1, &out_mesh_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelMeshDestroy(
    JNIEnv* environment, jclass declaring_class, jlong mesh)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.model_mesh_destroy((CNA_ModelMeshHandle)mesh);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelMeshGetBoundingSphere(
    JNIEnv* environment, jclass declaring_class, jlong mesh, jfloatArray out_valueFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_BoundingSphere out_value_value;
    memset(&out_value_value, 0, sizeof out_value_value);
    CNA_Result call_result = cna.model_mesh_get_bounding_sphere((CNA_ModelMeshHandle)mesh, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_valueFloating_values[] = {
            (jfloat)out_value_value.center.x,
            (jfloat)out_value_value.center.y,
            (jfloat)out_value_value.center.z,
            (jfloat)out_value_value.radius
        };
        (*environment)->SetFloatArrayRegion(environment, out_valueFloating, 0,
            (jsize)(sizeof out_valueFloating_values / sizeof out_valueFloating_values[0]),
            out_valueFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelMeshGetEffects(
    JNIEnv* environment, jclass declaring_class, jlong mesh, jlongArray out_effects)
{
    (void)environment;
    (void)declaring_class;
    CNA_ModelEffectCollectionHandle out_effects_value = 0;
    CNA_Result call_result = cna.model_mesh_get_effects((CNA_ModelMeshHandle)mesh, &out_effects_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_effects_element = (jlong)out_effects_value;
        (*environment)->SetLongArrayRegion(
            environment, out_effects, 0, 1, &out_effects_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelMeshGetMeshParts(
    JNIEnv* environment, jclass declaring_class, jlong mesh, jlongArray out_parts)
{
    (void)environment;
    (void)declaring_class;
    CNA_ModelMeshPartCollectionHandle out_parts_value = 0;
    CNA_Result call_result = cna.model_mesh_get_mesh_parts((CNA_ModelMeshHandle)mesh, &out_parts_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_parts_element = (jlong)out_parts_value;
        (*environment)->SetLongArrayRegion(
            environment, out_parts, 0, 1, &out_parts_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelMeshGetNameByteCount(
    JNIEnv* environment, jclass declaring_class, jlong mesh, jlongArray out_byte_count)
{
    (void)environment;
    (void)declaring_class;
    uint64_t out_byte_count_value = 0;
    CNA_Result call_result = cna.model_mesh_get_name_byte_count((CNA_ModelMeshHandle)mesh, &out_byte_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_byte_count_element = (jlong)out_byte_count_value;
        (*environment)->SetLongArrayRegion(
            environment, out_byte_count, 0, 1, &out_byte_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelMeshGetParentBone(
    JNIEnv* environment, jclass declaring_class, jlong mesh, jbooleanArray out_has_parent, jlongArray out_parent)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_has_parent_value = 0;
    CNA_ModelBoneHandle out_parent_value = 0;
    CNA_Result call_result = cna.model_mesh_get_parent_bone((CNA_ModelMeshHandle)mesh, &out_has_parent_value, &out_parent_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_has_parent_element = (jboolean)out_has_parent_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_has_parent, 0, 1, &out_has_parent_element);
        jlong out_parent_element = (jlong)out_parent_value;
        (*environment)->SetLongArrayRegion(
            environment, out_parent, 0, 1, &out_parent_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelMeshPartCollectionDestroy(
    JNIEnv* environment, jclass declaring_class, jlong collection)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.model_mesh_part_collection_destroy((CNA_ModelMeshPartCollectionHandle)collection);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelMeshPartCollectionGetAt(
    JNIEnv* environment, jclass declaring_class, jlong collection, jlong index, jlongArray out_part)
{
    (void)environment;
    (void)declaring_class;
    CNA_ModelMeshPartHandle out_part_value = 0;
    CNA_Result call_result = cna.model_mesh_part_collection_get_at((CNA_ModelMeshPartCollectionHandle)collection, (uint64_t)index, &out_part_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_part_element = (jlong)out_part_value;
        (*environment)->SetLongArrayRegion(
            environment, out_part, 0, 1, &out_part_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelMeshPartCollectionGetCount(
    JNIEnv* environment, jclass declaring_class, jlong collection, jlongArray out_count)
{
    (void)environment;
    (void)declaring_class;
    uint64_t out_count_value = 0;
    CNA_Result call_result = cna.model_mesh_part_collection_get_count((CNA_ModelMeshPartCollectionHandle)collection, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_count_element = (jlong)out_count_value;
        (*environment)->SetLongArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelMeshPartCreate(
    JNIEnv* environment, jclass declaring_class, jlong vertex_buffer, jlong index_buffer, jint num_vertices, jint primitive_count, jint start_index, jint vertex_offset, jlongArray out_part)
{
    (void)environment;
    (void)declaring_class;
    CNA_ModelMeshPartHandle out_part_value = 0;
    CNA_Result call_result = cna.model_mesh_part_create((CNA_VertexBufferHandle)vertex_buffer, (CNA_IndexBufferHandle)index_buffer, (int32_t)num_vertices, (int32_t)primitive_count, (int32_t)start_index, (int32_t)vertex_offset, &out_part_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_part_element = (jlong)out_part_value;
        (*environment)->SetLongArrayRegion(
            environment, out_part, 0, 1, &out_part_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelMeshPartDestroy(
    JNIEnv* environment, jclass declaring_class, jlong part)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.model_mesh_part_destroy((CNA_ModelMeshPartHandle)part);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelMeshPartGetEffect(
    JNIEnv* environment, jclass declaring_class, jlong part, jbooleanArray out_has_effect, jlongArray out_effect)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_has_effect_value = 0;
    CNA_EffectHandle out_effect_value = 0;
    CNA_Result call_result = cna.model_mesh_part_get_effect((CNA_ModelMeshPartHandle)part, &out_has_effect_value, &out_effect_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_has_effect_element = (jboolean)out_has_effect_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_has_effect, 0, 1, &out_has_effect_element);
        jlong out_effect_element = (jlong)out_effect_value;
        (*environment)->SetLongArrayRegion(
            environment, out_effect, 0, 1, &out_effect_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelMeshPartGetIndexBuffer(
    JNIEnv* environment, jclass declaring_class, jlong part, jbooleanArray out_has_buffer, jlongArray out_buffer)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_has_buffer_value = 0;
    CNA_IndexBufferHandle out_buffer_value = 0;
    CNA_Result call_result = cna.model_mesh_part_get_index_buffer((CNA_ModelMeshPartHandle)part, &out_has_buffer_value, &out_buffer_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_has_buffer_element = (jboolean)out_has_buffer_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_has_buffer, 0, 1, &out_has_buffer_element);
        jlong out_buffer_element = (jlong)out_buffer_value;
        (*environment)->SetLongArrayRegion(
            environment, out_buffer, 0, 1, &out_buffer_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelMeshPartGetNumVertices(
    JNIEnv* environment, jclass declaring_class, jlong part, jintArray out_value)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_value_value = 0;
    CNA_Result call_result = cna.model_mesh_part_get_num_vertices((CNA_ModelMeshPartHandle)part, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_value_element = (jint)out_value_value;
        (*environment)->SetIntArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelMeshPartGetPrimitiveCount(
    JNIEnv* environment, jclass declaring_class, jlong part, jintArray out_value)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_value_value = 0;
    CNA_Result call_result = cna.model_mesh_part_get_primitive_count((CNA_ModelMeshPartHandle)part, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_value_element = (jint)out_value_value;
        (*environment)->SetIntArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelMeshPartGetStartIndex(
    JNIEnv* environment, jclass declaring_class, jlong part, jintArray out_value)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_value_value = 0;
    CNA_Result call_result = cna.model_mesh_part_get_start_index((CNA_ModelMeshPartHandle)part, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_value_element = (jint)out_value_value;
        (*environment)->SetIntArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelMeshPartGetVertexBuffer(
    JNIEnv* environment, jclass declaring_class, jlong part, jbooleanArray out_has_buffer, jlongArray out_buffer)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_has_buffer_value = 0;
    CNA_VertexBufferHandle out_buffer_value = 0;
    CNA_Result call_result = cna.model_mesh_part_get_vertex_buffer((CNA_ModelMeshPartHandle)part, &out_has_buffer_value, &out_buffer_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_has_buffer_element = (jboolean)out_has_buffer_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_has_buffer, 0, 1, &out_has_buffer_element);
        jlong out_buffer_element = (jlong)out_buffer_value;
        (*environment)->SetLongArrayRegion(
            environment, out_buffer, 0, 1, &out_buffer_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelMeshPartGetVertexOffset(
    JNIEnv* environment, jclass declaring_class, jlong part, jintArray out_value)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_value_value = 0;
    CNA_Result call_result = cna.model_mesh_part_get_vertex_offset((CNA_ModelMeshPartHandle)part, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_value_element = (jint)out_value_value;
        (*environment)->SetIntArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelMeshPartSetEffect(
    JNIEnv* environment, jclass declaring_class, jlong part, jlong effect)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.model_mesh_part_set_effect((CNA_ModelMeshPartHandle)part, (CNA_EffectHandle)effect);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelMeshSetBoundingSphere(
    JNIEnv* environment, jclass declaring_class, jlong mesh, jfloatArray valueFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_BoundingSphere value_value;
    memset(&value_value, 0, sizeof value_value);
    {
        jfloat valueFloating_values[4];
        (*environment)->GetFloatArrayRegion(environment, valueFloating, 0, 4, valueFloating_values);
        value_value.center.x = (float)valueFloating_values[0];
        value_value.center.y = (float)valueFloating_values[1];
        value_value.center.z = (float)valueFloating_values[2];
        value_value.radius = (float)valueFloating_values[3];
    }
    CNA_Result call_result = cna.model_mesh_set_bounding_sphere((CNA_ModelMeshHandle)mesh, value_value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelMeshSetParentBone(
    JNIEnv* environment, jclass declaring_class, jlong mesh, jlong parent)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.model_mesh_set_parent_bone((CNA_ModelMeshHandle)mesh, (CNA_ModelBoneHandle)parent);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelSetBoneTransforms(
    JNIEnv* environment, jclass declaring_class, jlong model, jfloatArray sourceFloating)
{
    (void)environment;
    (void)declaring_class;
    jsize source_count = (*environment)->GetArrayLength(environment, sourceFloating) / 16;
    CNA_Matrix* source_values = (CNA_Matrix*)calloc(
        (size_t)source_count + 1U, sizeof(*source_values));
    if (source_values == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    {
        jsize sourceFloating_length = (*environment)->GetArrayLength(environment, sourceFloating);
        jfloat* sourceFloating_values = (jfloat*)malloc(
            ((size_t)sourceFloating_length + 1U) * sizeof(*sourceFloating_values));
        if (sourceFloating_values == NULL) {
            free(source_values);
            return (jint)CNA_RESULT_OUT_OF_MEMORY;
        }
        (*environment)->GetFloatArrayRegion(environment, sourceFloating, 0, sourceFloating_length, sourceFloating_values);
        for (jsize element = 0; element < source_count; ++element) {
            source_values[element].m11 = (float)sourceFloating_values[element * 16 + 0];
            source_values[element].m12 = (float)sourceFloating_values[element * 16 + 1];
            source_values[element].m13 = (float)sourceFloating_values[element * 16 + 2];
            source_values[element].m14 = (float)sourceFloating_values[element * 16 + 3];
            source_values[element].m21 = (float)sourceFloating_values[element * 16 + 4];
            source_values[element].m22 = (float)sourceFloating_values[element * 16 + 5];
            source_values[element].m23 = (float)sourceFloating_values[element * 16 + 6];
            source_values[element].m24 = (float)sourceFloating_values[element * 16 + 7];
            source_values[element].m31 = (float)sourceFloating_values[element * 16 + 8];
            source_values[element].m32 = (float)sourceFloating_values[element * 16 + 9];
            source_values[element].m33 = (float)sourceFloating_values[element * 16 + 10];
            source_values[element].m34 = (float)sourceFloating_values[element * 16 + 11];
            source_values[element].m41 = (float)sourceFloating_values[element * 16 + 12];
            source_values[element].m42 = (float)sourceFloating_values[element * 16 + 13];
            source_values[element].m43 = (float)sourceFloating_values[element * 16 + 14];
            source_values[element].m44 = (float)sourceFloating_values[element * 16 + 15];
        }
        free(sourceFloating_values);
    }
    CNA_Result call_result = cna.model_set_bone_transforms((CNA_ModelHandle)model, source_values, (uint64_t)source_count);
    free(source_values);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeModelExtensionRoutes_modelSetMaterialVariantExt(
    JNIEnv* environment, jclass declaring_class, jlong model, jint value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.model_set_material_variant_ext((CNA_ModelHandle)model, (int32_t)value);
    return (jint)call_result;
}
