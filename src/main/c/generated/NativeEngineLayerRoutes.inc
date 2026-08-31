/* SPDX-License-Identifier: MS-PL */
/*
 * Generated JNI adapter for NativeEngineLayerRoutes.
 *
 * Produced by tools/native-abi/generate_jni.py from the live CNA C headers, and
 * included by cna_java_jni.c so it shares the one dispatch table. Do not edit.
 */

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_engineLayerGetVersion(
    JNIEnv* environment, jclass declaring_class, jintArray out_version)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_version_value = 0;
    CNA_Result call_result = cna.engine_layer_get_version(&out_version_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_version_element = (jint)out_version_value;
        (*environment)->SetIntArrayRegion(
            environment, out_version, 0, 1, &out_version_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lodGroupExtAddLevel(
    JNIEnv* environment, jclass declaring_class, jlong group, jfloat max_distance, jlong part)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.lod_group_ext_add_level((CNA_LodGroupEXTHandle)group, (float)max_distance, (CNA_ModelMeshPartHandle)part);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lodGroupExtClear(
    JNIEnv* environment, jclass declaring_class, jlong group)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.lod_group_ext_clear((CNA_LodGroupEXTHandle)group);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lodGroupExtCopyLevels(
    JNIEnv* environment, jclass declaring_class, jlong group, jlongArray destinationIntegral, jfloatArray destinationFloating, jlongArray out_count)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_count = (*environment)->GetArrayLength(environment, destinationIntegral) / 2;
    CNA_LodLevelEXT* destination_values = (CNA_LodLevelEXT*)calloc(
        (size_t)destination_count + 1U, sizeof(*destination_values));
    if (destination_values == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    {
        jsize destinationIntegral_length = (*environment)->GetArrayLength(environment, destinationIntegral);
        jlong* destinationIntegral_values = (jlong*)malloc(
            ((size_t)destinationIntegral_length + 1U) * sizeof(*destinationIntegral_values));
        if (destinationIntegral_values == NULL) {
            free(destination_values);
            return (jint)CNA_RESULT_OUT_OF_MEMORY;
        }
        (*environment)->GetLongArrayRegion(environment, destinationIntegral, 0, destinationIntegral_length, destinationIntegral_values);
        for (jsize element = 0; element < destination_count; ++element) {
            destination_values[element].part = (CNA_ModelMeshPartHandle)destinationIntegral_values[element * 2 + 0];
            destination_values[element].reserved0 = (uint32_t)destinationIntegral_values[element * 2 + 1];
        }
        free(destinationIntegral_values);
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
            destination_values[element].max_distance = (float)destinationFloating_values[element * 1 + 0];
        }
        free(destinationFloating_values);
    }
    uint64_t out_count_value = 0;
    CNA_Result call_result = cna.lod_group_ext_copy_levels((CNA_LodGroupEXTHandle)group, destination_values, (uint64_t)destination_count, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS) {
        {
            jlong* destinationIntegral_out = (jlong*)malloc(
                ((size_t)destination_count * 2 + 1U) * sizeof(*destinationIntegral_out));
            if (destinationIntegral_out != NULL) {
                for (jsize element = 0; element < destination_count; ++element) {
                    destinationIntegral_out[element * 2 + 0] = (jlong)destination_values[element].part;
                    destinationIntegral_out[element * 2 + 1] = (jlong)destination_values[element].reserved0;
                }
                (*environment)->SetLongArrayRegion(
                    environment, destinationIntegral, 0,
                    (jsize)(destination_count * 2), destinationIntegral_out);
                free(destinationIntegral_out);
            }
        }
        {
            jfloat* destinationFloating_out = (jfloat*)malloc(
                ((size_t)destination_count * 1 + 1U) * sizeof(*destinationFloating_out));
            if (destinationFloating_out != NULL) {
                for (jsize element = 0; element < destination_count; ++element) {
                    destinationFloating_out[element * 1 + 0] = (jfloat)destination_values[element].max_distance;
                }
                (*environment)->SetFloatArrayRegion(
                    environment, destinationFloating, 0,
                    (jsize)(destination_count * 1), destinationFloating_out);
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
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lodGroupExtCreate(
    JNIEnv* environment, jclass declaring_class, jlongArray out_group)
{
    (void)environment;
    (void)declaring_class;
    CNA_LodGroupEXTHandle out_group_value = 0;
    CNA_Result call_result = cna.lod_group_ext_create(&out_group_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_group_element = (jlong)out_group_value;
        (*environment)->SetLongArrayRegion(
            environment, out_group, 0, 1, &out_group_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lodGroupExtDestroy(
    JNIEnv* environment, jclass declaring_class, jlong group)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.lod_group_ext_destroy((CNA_LodGroupEXTHandle)group);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lodGroupExtGetHysteresis(
    JNIEnv* environment, jclass declaring_class, jlong group, jfloatArray out_margin)
{
    (void)environment;
    (void)declaring_class;
    float out_margin_value = 0;
    CNA_Result call_result = cna.lod_group_ext_get_hysteresis((CNA_LodGroupEXTHandle)group, &out_margin_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_margin_element = (jfloat)out_margin_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_margin, 0, 1, &out_margin_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lodGroupExtGetSelectionMode(
    JNIEnv* environment, jclass declaring_class, jlong group, jintArray out_mode)
{
    (void)environment;
    (void)declaring_class;
    CNA_LodSelectionMode out_mode_value = 0;
    CNA_Result call_result = cna.lod_group_ext_get_selection_mode((CNA_LodGroupEXTHandle)group, &out_mode_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_mode_element = (jint)out_mode_value;
        (*environment)->SetIntArrayRegion(
            environment, out_mode, 0, 1, &out_mode_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lodGroupExtProjectedRadiusPixels(
    JNIEnv* environment, jclass declaring_class, jlong group, jfloat distance, jfloatArray out_pixels)
{
    (void)environment;
    (void)declaring_class;
    float out_pixels_value = 0;
    CNA_Result call_result = cna.lod_group_ext_projected_radius_pixels((CNA_LodGroupEXTHandle)group, (float)distance, &out_pixels_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_pixels_element = (jfloat)out_pixels_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_pixels, 0, 1, &out_pixels_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lodGroupExtResetHysteresis(
    JNIEnv* environment, jclass declaring_class, jlong group)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.lod_group_ext_reset_hysteresis((CNA_LodGroupEXTHandle)group);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lodGroupExtSelectIndex(
    JNIEnv* environment, jclass declaring_class, jlong group, jfloat distance, jintArray out_index)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_index_value = 0;
    CNA_Result call_result = cna.lod_group_ext_select_index((CNA_LodGroupEXTHandle)group, (float)distance, &out_index_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_index_element = (jint)out_index_value;
        (*environment)->SetIntArrayRegion(
            environment, out_index, 0, 1, &out_index_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lodGroupExtSetHysteresis(
    JNIEnv* environment, jclass declaring_class, jlong group, jfloat margin)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.lod_group_ext_set_hysteresis((CNA_LodGroupEXTHandle)group, (float)margin);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lodGroupExtSetScreenSpaceParameters(
    JNIEnv* environment, jclass declaring_class, jlong group, jfloat radius, jfloat vertical_fov, jfloat viewport_height)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.lod_group_ext_set_screen_space_parameters((CNA_LodGroupEXTHandle)group, (float)radius, (float)vertical_fov, (float)viewport_height);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lodGroupExtSetSelectionMode(
    JNIEnv* environment, jclass declaring_class, jlong group, jint mode)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.lod_group_ext_set_selection_mode((CNA_LodGroupEXTHandle)group, (CNA_LodSelectionMode)mode);
    return (jint)call_result;
}
