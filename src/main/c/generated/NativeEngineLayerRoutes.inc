/* SPDX-License-Identifier: MS-PL */
/*
 * Generated JNI adapter for NativeEngineLayerRoutes.
 *
 * Produced by tools/native-abi/generate_jni.py from the live CNA C headers, and
 * included by cna_java_jni.c so it shares the one dispatch table. Do not edit.
 */

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_aerialPerspectivePassAirMassForDistance(
    JNIEnv* environment, jclass declaring_class, jfloatArray view_directionFloating, jfloat distance, jfloat scale_height, jfloatArray out_air_mass)
{
    (void)environment;
    (void)declaring_class;
    CNA_Vector3 view_direction_value;
    memset(&view_direction_value, 0, sizeof view_direction_value);
    {
        jfloat view_directionFloating_values[3];
        (*environment)->GetFloatArrayRegion(environment, view_directionFloating, 0, 3, view_directionFloating_values);
        view_direction_value.x = (float)view_directionFloating_values[0];
        view_direction_value.y = (float)view_directionFloating_values[1];
        view_direction_value.z = (float)view_directionFloating_values[2];
    }
    float out_air_mass_value = 0;
    CNA_Result call_result = cna.aerial_perspective_pass_air_mass_for_distance(&view_direction_value, (float)distance, (float)scale_height, &out_air_mass_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_air_mass_element = (jfloat)out_air_mass_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_air_mass, 0, 1, &out_air_mass_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_aerialPerspectivePassCopyFallbackReason(
    JNIEnv* environment, jclass declaring_class, jlong pass, jbyteArray destination, jlongArray out_bytes)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t destination_written = 0;
    CNA_Result call_result = cna.aerial_perspective_pass_copy_fallback_reason((CNA_PostProcessPassHandle)pass, (char*)destination_bytes, (uint64_t)destination_size, &destination_written);
    (*environment)->ReleaseByteArrayElements(environment, destination, destination_bytes, 0);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong destination_element = (jlong)destination_written;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &destination_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_aerialPerspectivePassCreate(
    JNIEnv* environment, jclass declaring_class, jlong graphics_device, jlongArray out_pass)
{
    (void)environment;
    (void)declaring_class;
    CNA_PostProcessPassHandle out_pass_value = 0;
    CNA_Result call_result = cna.aerial_perspective_pass_create((CNA_Handle)graphics_device, &out_pass_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_pass_element = (jlong)out_pass_value;
        (*environment)->SetLongArrayRegion(
            environment, out_pass, 0, 1, &out_pass_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_aerialPerspectivePassGetIntensity(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.aerial_perspective_pass_get_intensity((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_aerialPerspectivePassGetScaleHeight(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.aerial_perspective_pass_get_scale_height((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_aerialPerspectivePassGetSunDirection(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_valueFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Vector3 out_value_value;
    memset(&out_value_value, 0, sizeof out_value_value);
    CNA_Result call_result = cna.aerial_perspective_pass_get_sun_direction((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_valueFloating_values[] = {
            (jfloat)out_value_value.x,
            (jfloat)out_value_value.y,
            (jfloat)out_value_value.z
        };
        (*environment)->SetFloatArrayRegion(environment, out_valueFloating, 0,
            (jsize)(sizeof out_valueFloating_values / sizeof out_valueFloating_values[0]),
            out_valueFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_aerialPerspectivePassGetTurbidity(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.aerial_perspective_pass_get_turbidity((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_aerialPerspectivePassSetIntensity(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.aerial_perspective_pass_set_intensity((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_aerialPerspectivePassSetScaleHeight(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.aerial_perspective_pass_set_scale_height((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_aerialPerspectivePassSetSunDirection(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray valueFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Vector3 value_value;
    memset(&value_value, 0, sizeof value_value);
    {
        jfloat valueFloating_values[3];
        (*environment)->GetFloatArrayRegion(environment, valueFloating, 0, 3, valueFloating_values);
        value_value.x = (float)valueFloating_values[0];
        value_value.y = (float)valueFloating_values[1];
        value_value.z = (float)valueFloating_values[2];
    }
    CNA_Result call_result = cna.aerial_perspective_pass_set_sun_direction((CNA_PostProcessPassHandle)pass, &value_value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_aerialPerspectivePassSetTurbidity(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.aerial_perspective_pass_set_turbidity((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_aerialPerspectivePassTransmittance(
    JNIEnv* environment, jclass declaring_class, jfloat turbidity, jfloat air_mass, jfloatArray out_transmittanceFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Vector3 out_transmittance_value;
    memset(&out_transmittance_value, 0, sizeof out_transmittance_value);
    CNA_Result call_result = cna.aerial_perspective_pass_transmittance((float)turbidity, (float)air_mass, &out_transmittance_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_transmittanceFloating_values[] = {
            (jfloat)out_transmittance_value.x,
            (jfloat)out_transmittance_value.y,
            (jfloat)out_transmittance_value.z
        };
        (*environment)->SetFloatArrayRegion(environment, out_transmittanceFloating, 0,
            (jsize)(sizeof out_transmittanceFloating_values / sizeof out_transmittanceFloating_values[0]),
            out_transmittanceFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_blitPassCreate(
    JNIEnv* environment, jclass declaring_class, jlong graphics_device, jlongArray out_pass)
{
    (void)environment;
    (void)declaring_class;
    CNA_PostProcessPassHandle out_pass_value = 0;
    CNA_Result call_result = cna.blit_pass_create((CNA_Handle)graphics_device, &out_pass_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_pass_element = (jlong)out_pass_value;
        (*environment)->SetLongArrayRegion(
            environment, out_pass, 0, 1, &out_pass_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_bloomPassCreate(
    JNIEnv* environment, jclass declaring_class, jlong graphics_device, jlongArray out_pass)
{
    (void)environment;
    (void)declaring_class;
    CNA_PostProcessPassHandle out_pass_value = 0;
    CNA_Result call_result = cna.bloom_pass_create((CNA_Handle)graphics_device, &out_pass_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_pass_element = (jlong)out_pass_value;
        (*environment)->SetLongArrayRegion(
            environment, out_pass, 0, 1, &out_pass_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_bloomPassExtractChannel(
    JNIEnv* environment, jclass declaring_class, jfloat value, jfloat threshold, jfloatArray out_extracted)
{
    (void)environment;
    (void)declaring_class;
    float out_extracted_value = 0;
    CNA_Result call_result = cna.bloom_pass_extract_channel((float)value, (float)threshold, &out_extracted_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_extracted_element = (jfloat)out_extracted_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_extracted, 0, 1, &out_extracted_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_bloomPassGetIntensity(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.bloom_pass_get_intensity((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_bloomPassGetIterations(
    JNIEnv* environment, jclass declaring_class, jlong pass, jintArray out_value)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_value_value = 0;
    CNA_Result call_result = cna.bloom_pass_get_iterations((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_value_element = (jint)out_value_value;
        (*environment)->SetIntArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_bloomPassGetThreshold(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.bloom_pass_get_threshold((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_bloomPassIterationsForQuality(
    JNIEnv* environment, jclass declaring_class, jint quality, jintArray out_iterations)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_iterations_value = 0;
    CNA_Result call_result = cna.bloom_pass_iterations_for_quality((CNA_RenderQuality)quality, &out_iterations_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_iterations_element = (jint)out_iterations_value;
        (*environment)->SetIntArrayRegion(
            environment, out_iterations, 0, 1, &out_iterations_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_bloomPassResetTargets(
    JNIEnv* environment, jclass declaring_class, jlong pass)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.bloom_pass_reset_targets((CNA_PostProcessPassHandle)pass);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_bloomPassSetIntensity(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.bloom_pass_set_intensity((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_bloomPassSetIterations(
    JNIEnv* environment, jclass declaring_class, jlong pass, jint value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.bloom_pass_set_iterations((CNA_PostProcessPassHandle)pass, (int32_t)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_bloomPassSetThreshold(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.bloom_pass_set_threshold((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cascadedShadowMapApplyToReceiver(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jlong effect)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.cascaded_shadow_map_apply_to_receiver((CNA_CascadedShadowMapHandle)shadow_map, (CNA_EffectHandle)effect);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cascadedShadowMapBegin(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jint cascade_index)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.cascaded_shadow_map_begin((CNA_CascadedShadowMapHandle)shadow_map, (int32_t)cascade_index);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cascadedShadowMapComputeBoundingSphere(
    JNIEnv* environment, jclass declaring_class, jfloatArray cornersFloating, jfloatArray out_centreFloating, jfloatArray out_radius)
{
    (void)environment;
    (void)declaring_class;
    const jsize corners_count = 8;
    if ((*environment)->GetArrayLength(environment, cornersFloating) != corners_count * 3) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Vector3* corners_values = (CNA_Vector3*)calloc(
        (size_t)corners_count + 1U, sizeof(*corners_values));
    if (corners_values == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    {
        jsize cornersFloating_length = (*environment)->GetArrayLength(environment, cornersFloating);
        jfloat* cornersFloating_values = (jfloat*)malloc(
            ((size_t)cornersFloating_length + 1U) * sizeof(*cornersFloating_values));
        if (cornersFloating_values == NULL) {
            free(corners_values);
            return (jint)CNA_RESULT_OUT_OF_MEMORY;
        }
        (*environment)->GetFloatArrayRegion(environment, cornersFloating, 0, cornersFloating_length, cornersFloating_values);
        for (jsize element = 0; element < corners_count; ++element) {
            corners_values[element].x = (float)cornersFloating_values[element * 3 + 0];
            corners_values[element].y = (float)cornersFloating_values[element * 3 + 1];
            corners_values[element].z = (float)cornersFloating_values[element * 3 + 2];
        }
        free(cornersFloating_values);
    }
    CNA_Vector3 out_centre_value;
    memset(&out_centre_value, 0, sizeof out_centre_value);
    float out_radius_value = 0;
    CNA_Result call_result = cna.cascaded_shadow_map_compute_bounding_sphere(corners_values, &out_centre_value, &out_radius_value);
    free(corners_values);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_centreFloating_values[] = {
            (jfloat)out_centre_value.x,
            (jfloat)out_centre_value.y,
            (jfloat)out_centre_value.z
        };
        (*environment)->SetFloatArrayRegion(environment, out_centreFloating, 0,
            (jsize)(sizeof out_centreFloating_values / sizeof out_centreFloating_values[0]),
            out_centreFloating_values);
        jfloat out_radius_element = (jfloat)out_radius_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_radius, 0, 1, &out_radius_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cascadedShadowMapComputeFrustumCorners(
    JNIEnv* environment, jclass declaring_class, jfloatArray viewFloating, jfloatArray projectionFloating, jfloatArray out_cornersFloating)
{
    (void)environment;
    (void)declaring_class;
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
    const jsize out_corners_count = 8;
    if ((*environment)->GetArrayLength(environment, out_cornersFloating) != out_corners_count * 3) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Vector3* out_corners_values = (CNA_Vector3*)calloc(
        (size_t)out_corners_count + 1U, sizeof(*out_corners_values));
    if (out_corners_values == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    {
        jsize out_cornersFloating_length = (*environment)->GetArrayLength(environment, out_cornersFloating);
        jfloat* out_cornersFloating_values = (jfloat*)malloc(
            ((size_t)out_cornersFloating_length + 1U) * sizeof(*out_cornersFloating_values));
        if (out_cornersFloating_values == NULL) {
            free(out_corners_values);
            return (jint)CNA_RESULT_OUT_OF_MEMORY;
        }
        (*environment)->GetFloatArrayRegion(environment, out_cornersFloating, 0, out_cornersFloating_length, out_cornersFloating_values);
        for (jsize element = 0; element < out_corners_count; ++element) {
            out_corners_values[element].x = (float)out_cornersFloating_values[element * 3 + 0];
            out_corners_values[element].y = (float)out_cornersFloating_values[element * 3 + 1];
            out_corners_values[element].z = (float)out_cornersFloating_values[element * 3 + 2];
        }
        free(out_cornersFloating_values);
    }
    CNA_Result call_result = cna.cascaded_shadow_map_compute_frustum_corners(&view_value, &projection_value, out_corners_values);
    if (call_result == CNA_RESULT_SUCCESS) {
        {
            jfloat* out_cornersFloating_out = (jfloat*)malloc(
                ((size_t)out_corners_count * 3 + 1U) * sizeof(*out_cornersFloating_out));
            if (out_cornersFloating_out != NULL) {
                for (jsize element = 0; element < out_corners_count; ++element) {
                    out_cornersFloating_out[element * 3 + 0] = (jfloat)out_corners_values[element].x;
                    out_cornersFloating_out[element * 3 + 1] = (jfloat)out_corners_values[element].y;
                    out_cornersFloating_out[element * 3 + 2] = (jfloat)out_corners_values[element].z;
                }
                (*environment)->SetFloatArrayRegion(
                    environment, out_cornersFloating, 0,
                    (jsize)(out_corners_count * 3), out_cornersFloating_out);
                free(out_cornersFloating_out);
            }
        }
    }
    free(out_corners_values);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cascadedShadowMapComputeSplitDistances(
    JNIEnv* environment, jclass declaring_class, jfloat near_plane, jfloat far_plane, jint cascade_count, jfloat lambda, jfloatArray destination, jlongArray out_count)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jfloat* destination_elements = (*environment)->GetFloatArrayElements(environment, destination, NULL);
    if (destination_elements == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    float* destination_values = (float*)malloc(
        ((size_t)destination_size + 1U) * sizeof(*destination_values));
    if (destination_values == NULL) {
        (*environment)->ReleaseFloatArrayElements(
            environment, destination, destination_elements, JNI_ABORT);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    for (jsize index = 0; index < destination_size; ++index) {
        destination_values[index] = (float)destination_elements[index];
    }
    uint64_t out_count_value = 0;
    CNA_Result call_result = cna.cascaded_shadow_map_compute_split_distances((float)near_plane, (float)far_plane, (int32_t)cascade_count, (float)lambda, destination_values, (uint64_t)destination_size, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS) {
        for (jsize index = 0; index < destination_size; ++index) {
            destination_elements[index] = (jfloat)destination_values[index];
        }
    }
    free(destination_values);
    (*environment)->ReleaseFloatArrayElements(
        environment, destination, destination_elements, 0);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_count_element = (jlong)out_count_value;
        (*environment)->SetLongArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cascadedShadowMapCreate(
    JNIEnv* environment, jclass declaring_class, jlong graphics_device, jint quality, jint cascade_count, jlongArray out_shadow_map)
{
    (void)environment;
    (void)declaring_class;
    CNA_CascadedShadowMapHandle out_shadow_map_value = 0;
    CNA_Result call_result = cna.cascaded_shadow_map_create((CNA_Handle)graphics_device, (CNA_ShadowQuality)quality, (int32_t)cascade_count, &out_shadow_map_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_shadow_map_element = (jlong)out_shadow_map_value;
        (*environment)->SetLongArrayRegion(
            environment, out_shadow_map, 0, 1, &out_shadow_map_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cascadedShadowMapDestroy(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.cascaded_shadow_map_destroy((CNA_CascadedShadowMapHandle)shadow_map);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cascadedShadowMapEnd(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.cascaded_shadow_map_end((CNA_CascadedShadowMapHandle)shadow_map);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cascadedShadowMapGetBlendBand(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jfloatArray out_band)
{
    (void)environment;
    (void)declaring_class;
    float out_band_value = 0;
    CNA_Result call_result = cna.cascaded_shadow_map_get_blend_band((CNA_CascadedShadowMapHandle)shadow_map, &out_band_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_band_element = (jfloat)out_band_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_band, 0, 1, &out_band_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cascadedShadowMapGetCascadeCount(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jintArray out_count)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_count_value = 0;
    CNA_Result call_result = cna.cascaded_shadow_map_get_cascade_count((CNA_CascadedShadowMapHandle)shadow_map, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_count_element = (jint)out_count_value;
        (*environment)->SetIntArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cascadedShadowMapGetCascadeMatrix(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jint cascade_index, jfloatArray out_matrixFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Matrix out_matrix_value;
    memset(&out_matrix_value, 0, sizeof out_matrix_value);
    CNA_Result call_result = cna.cascaded_shadow_map_get_cascade_matrix((CNA_CascadedShadowMapHandle)shadow_map, (int32_t)cascade_index, &out_matrix_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_matrixFloating_values[] = {
            (jfloat)out_matrix_value.m11,
            (jfloat)out_matrix_value.m12,
            (jfloat)out_matrix_value.m13,
            (jfloat)out_matrix_value.m14,
            (jfloat)out_matrix_value.m21,
            (jfloat)out_matrix_value.m22,
            (jfloat)out_matrix_value.m23,
            (jfloat)out_matrix_value.m24,
            (jfloat)out_matrix_value.m31,
            (jfloat)out_matrix_value.m32,
            (jfloat)out_matrix_value.m33,
            (jfloat)out_matrix_value.m34,
            (jfloat)out_matrix_value.m41,
            (jfloat)out_matrix_value.m42,
            (jfloat)out_matrix_value.m43,
            (jfloat)out_matrix_value.m44
        };
        (*environment)->SetFloatArrayRegion(environment, out_matrixFloating, 0,
            (jsize)(sizeof out_matrixFloating_values / sizeof out_matrixFloating_values[0]),
            out_matrixFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cascadedShadowMapGetCascadeSize(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jintArray out_size)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_size_value = 0;
    CNA_Result call_result = cna.cascaded_shadow_map_get_cascade_size((CNA_CascadedShadowMapHandle)shadow_map, &out_size_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_size_element = (jint)out_size_value;
        (*environment)->SetIntArrayRegion(
            environment, out_size, 0, 1, &out_size_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cascadedShadowMapGetShadowTexture(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jlongArray out_texture)
{
    (void)environment;
    (void)declaring_class;
    CNA_Handle out_texture_value = 0;
    CNA_Result call_result = cna.cascaded_shadow_map_get_shadow_texture((CNA_CascadedShadowMapHandle)shadow_map, &out_texture_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_texture_element = (jlong)out_texture_value;
        (*environment)->SetLongArrayRegion(
            environment, out_texture, 0, 1, &out_texture_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cascadedShadowMapGetSplitDistance(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jint cascade_index, jfloatArray out_distance)
{
    (void)environment;
    (void)declaring_class;
    float out_distance_value = 0;
    CNA_Result call_result = cna.cascaded_shadow_map_get_split_distance((CNA_CascadedShadowMapHandle)shadow_map, (int32_t)cascade_index, &out_distance_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_distance_element = (jfloat)out_distance_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_distance, 0, 1, &out_distance_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cascadedShadowMapGetSplitLambda(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jfloatArray out_lambda)
{
    (void)environment;
    (void)declaring_class;
    float out_lambda_value = 0;
    CNA_Result call_result = cna.cascaded_shadow_map_get_split_lambda((CNA_CascadedShadowMapHandle)shadow_map, &out_lambda_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_lambda_element = (jfloat)out_lambda_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_lambda, 0, 1, &out_lambda_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cascadedShadowMapIsDebugTintEnabled(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jbooleanArray out_enabled)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_enabled_value = 0;
    CNA_Result call_result = cna.cascaded_shadow_map_is_debug_tint_enabled((CNA_CascadedShadowMapHandle)shadow_map, &out_enabled_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_enabled_element = (jboolean)out_enabled_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_enabled, 0, 1, &out_enabled_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cascadedShadowMapIsSupported(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jbooleanArray out_supported)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_supported_value = 0;
    CNA_Result call_result = cna.cascaded_shadow_map_is_supported((CNA_CascadedShadowMapHandle)shadow_map, &out_supported_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_supported_element = (jboolean)out_supported_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_supported, 0, 1, &out_supported_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cascadedShadowMapSelectCascade(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jfloat view_depth, jintArray out_index)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_index_value = 0;
    CNA_Result call_result = cna.cascaded_shadow_map_select_cascade((CNA_CascadedShadowMapHandle)shadow_map, (float)view_depth, &out_index_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_index_element = (jint)out_index_value;
        (*environment)->SetIntArrayRegion(
            environment, out_index, 0, 1, &out_index_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cascadedShadowMapSetBlendBand(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jfloat band)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.cascaded_shadow_map_set_blend_band((CNA_CascadedShadowMapHandle)shadow_map, (float)band);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cascadedShadowMapSetDebugTintEnabled(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jboolean enabled)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.cascaded_shadow_map_set_debug_tint_enabled((CNA_CascadedShadowMapHandle)shadow_map, (CNA_Bool)enabled);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cascadedShadowMapSetSplitLambda(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jfloat lambda)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.cascaded_shadow_map_set_split_lambda((CNA_CascadedShadowMapHandle)shadow_map, (float)lambda);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cascadedShadowMapSnapToTexelGrid(
    JNIEnv* environment, jclass declaring_class, jfloatArray centreFloating, jfloat radius, jint cascade_size, jfloatArray out_centreFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Vector3 centre_value;
    memset(&centre_value, 0, sizeof centre_value);
    {
        jfloat centreFloating_values[3];
        (*environment)->GetFloatArrayRegion(environment, centreFloating, 0, 3, centreFloating_values);
        centre_value.x = (float)centreFloating_values[0];
        centre_value.y = (float)centreFloating_values[1];
        centre_value.z = (float)centreFloating_values[2];
    }
    CNA_Vector3 out_centre_value;
    memset(&out_centre_value, 0, sizeof out_centre_value);
    CNA_Result call_result = cna.cascaded_shadow_map_snap_to_texel_grid(&centre_value, (float)radius, (int32_t)cascade_size, &out_centre_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_centreFloating_values[] = {
            (jfloat)out_centre_value.x,
            (jfloat)out_centre_value.y,
            (jfloat)out_centre_value.z
        };
        (*environment)->SetFloatArrayRegion(environment, out_centreFloating, 0,
            (jsize)(sizeof out_centreFloating_values / sizeof out_centreFloating_values[0]),
            out_centreFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cascadedShadowMapUpdate(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jbyteArray lightBytes, jlongArray lightIntegral, jfloatArray lightFloating, jfloatArray camera_viewFloating, jfloatArray camera_projectionFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_DirectionalLightEXT light_value;
    memset(&light_value, 0, sizeof light_value);
    light_value.struct_size = (uint32_t)(sizeof light_value);
    light_value.struct_version = UINT32_C(1);
    {
        jbyte lightBytes_values[3];
        (*environment)->GetByteArrayRegion(environment, lightBytes, 0, 3, lightBytes_values);
        light_value.reserved[0] = (uint8_t)lightBytes_values[0];
        light_value.reserved[1] = (uint8_t)lightBytes_values[1];
        light_value.reserved[2] = (uint8_t)lightBytes_values[2];
    }
    {
        jlong lightIntegral_values[1];
        (*environment)->GetLongArrayRegion(environment, lightIntegral, 0, 1, lightIntegral_values);
        light_value.casts_shadows = (CNA_Bool)lightIntegral_values[0];
    }
    {
        jfloat lightFloating_values[7];
        (*environment)->GetFloatArrayRegion(environment, lightFloating, 0, 7, lightFloating_values);
        light_value.direction.x = (float)lightFloating_values[0];
        light_value.direction.y = (float)lightFloating_values[1];
        light_value.direction.z = (float)lightFloating_values[2];
        light_value.color.x = (float)lightFloating_values[3];
        light_value.color.y = (float)lightFloating_values[4];
        light_value.color.z = (float)lightFloating_values[5];
        light_value.intensity = (float)lightFloating_values[6];
    }
    CNA_Matrix camera_view_value;
    memset(&camera_view_value, 0, sizeof camera_view_value);
    {
        jfloat camera_viewFloating_values[16];
        (*environment)->GetFloatArrayRegion(environment, camera_viewFloating, 0, 16, camera_viewFloating_values);
        camera_view_value.m11 = (float)camera_viewFloating_values[0];
        camera_view_value.m12 = (float)camera_viewFloating_values[1];
        camera_view_value.m13 = (float)camera_viewFloating_values[2];
        camera_view_value.m14 = (float)camera_viewFloating_values[3];
        camera_view_value.m21 = (float)camera_viewFloating_values[4];
        camera_view_value.m22 = (float)camera_viewFloating_values[5];
        camera_view_value.m23 = (float)camera_viewFloating_values[6];
        camera_view_value.m24 = (float)camera_viewFloating_values[7];
        camera_view_value.m31 = (float)camera_viewFloating_values[8];
        camera_view_value.m32 = (float)camera_viewFloating_values[9];
        camera_view_value.m33 = (float)camera_viewFloating_values[10];
        camera_view_value.m34 = (float)camera_viewFloating_values[11];
        camera_view_value.m41 = (float)camera_viewFloating_values[12];
        camera_view_value.m42 = (float)camera_viewFloating_values[13];
        camera_view_value.m43 = (float)camera_viewFloating_values[14];
        camera_view_value.m44 = (float)camera_viewFloating_values[15];
    }
    CNA_Matrix camera_projection_value;
    memset(&camera_projection_value, 0, sizeof camera_projection_value);
    {
        jfloat camera_projectionFloating_values[16];
        (*environment)->GetFloatArrayRegion(environment, camera_projectionFloating, 0, 16, camera_projectionFloating_values);
        camera_projection_value.m11 = (float)camera_projectionFloating_values[0];
        camera_projection_value.m12 = (float)camera_projectionFloating_values[1];
        camera_projection_value.m13 = (float)camera_projectionFloating_values[2];
        camera_projection_value.m14 = (float)camera_projectionFloating_values[3];
        camera_projection_value.m21 = (float)camera_projectionFloating_values[4];
        camera_projection_value.m22 = (float)camera_projectionFloating_values[5];
        camera_projection_value.m23 = (float)camera_projectionFloating_values[6];
        camera_projection_value.m24 = (float)camera_projectionFloating_values[7];
        camera_projection_value.m31 = (float)camera_projectionFloating_values[8];
        camera_projection_value.m32 = (float)camera_projectionFloating_values[9];
        camera_projection_value.m33 = (float)camera_projectionFloating_values[10];
        camera_projection_value.m34 = (float)camera_projectionFloating_values[11];
        camera_projection_value.m41 = (float)camera_projectionFloating_values[12];
        camera_projection_value.m42 = (float)camera_projectionFloating_values[13];
        camera_projection_value.m43 = (float)camera_projectionFloating_values[14];
        camera_projection_value.m44 = (float)camera_projectionFloating_values[15];
    }
    CNA_Result call_result = cna.cascaded_shadow_map_update((CNA_CascadedShadowMapHandle)shadow_map, &light_value, &camera_view_value, &camera_projection_value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_chromaticAberrationPassCreate(
    JNIEnv* environment, jclass declaring_class, jlong graphics_device, jlongArray out_pass)
{
    (void)environment;
    (void)declaring_class;
    CNA_PostProcessPassHandle out_pass_value = 0;
    CNA_Result call_result = cna.chromatic_aberration_pass_create((CNA_Handle)graphics_device, &out_pass_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_pass_element = (jlong)out_pass_value;
        (*environment)->SetLongArrayRegion(
            environment, out_pass, 0, 1, &out_pass_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_chromaticAberrationPassGetStrength(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.chromatic_aberration_pass_get_strength((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_chromaticAberrationPassSetStrength(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.chromatic_aberration_pass_set_strength((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightAssignmentAdopt(
    JNIEnv* environment, jclass declaring_class, jlong assignment, jint light_count, jintArray offsets, jintArray indices)
{
    (void)environment;
    (void)declaring_class;
    jsize offsets_size = (*environment)->GetArrayLength(environment, offsets);
    jint* offsets_elements = (*environment)->GetIntArrayElements(environment, offsets, NULL);
    if (offsets_elements == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    int32_t* offsets_values = (int32_t*)malloc(
        ((size_t)offsets_size + 1U) * sizeof(*offsets_values));
    if (offsets_values == NULL) {
        (*environment)->ReleaseIntArrayElements(
            environment, offsets, offsets_elements, JNI_ABORT);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    for (jsize index = 0; index < offsets_size; ++index) {
        offsets_values[index] = (int32_t)offsets_elements[index];
    }
    jsize indices_size = (*environment)->GetArrayLength(environment, indices);
    jint* indices_elements = (*environment)->GetIntArrayElements(environment, indices, NULL);
    if (indices_elements == NULL) {
    free(offsets_values);
    (*environment)->ReleaseIntArrayElements(
        environment, offsets, offsets_elements, JNI_ABORT);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    int32_t* indices_values = (int32_t*)malloc(
        ((size_t)indices_size + 1U) * sizeof(*indices_values));
    if (indices_values == NULL) {
        (*environment)->ReleaseIntArrayElements(
            environment, indices, indices_elements, JNI_ABORT);
    free(offsets_values);
    (*environment)->ReleaseIntArrayElements(
        environment, offsets, offsets_elements, JNI_ABORT);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    for (jsize index = 0; index < indices_size; ++index) {
        indices_values[index] = (int32_t)indices_elements[index];
    }
    CNA_Result call_result = cna.clustered_light_assignment_adopt((CNA_ClusteredLightAssignmentHandle)assignment, (int32_t)light_count, offsets_values, (uint64_t)offsets_size, indices_values, (uint64_t)indices_size);
    free(offsets_values);
    (*environment)->ReleaseIntArrayElements(
        environment, offsets, offsets_elements, JNI_ABORT);
    free(indices_values);
    (*environment)->ReleaseIntArrayElements(
        environment, indices, indices_elements, JNI_ABORT);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightAssignmentAssign(
    JNIEnv* environment, jclass declaring_class, jlong assignment, jlong grid, jfloatArray viewFloating, jfloatArray boundsFloating)
{
    (void)environment;
    (void)declaring_class;
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
    jsize bounds_count = (*environment)->GetArrayLength(environment, boundsFloating) / 4;
    if ((*environment)->GetArrayLength(environment, boundsFloating) != bounds_count * 4) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_BoundingSphere* bounds_values = (CNA_BoundingSphere*)calloc(
        (size_t)bounds_count + 1U, sizeof(*bounds_values));
    if (bounds_values == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    {
        jsize boundsFloating_length = (*environment)->GetArrayLength(environment, boundsFloating);
        jfloat* boundsFloating_values = (jfloat*)malloc(
            ((size_t)boundsFloating_length + 1U) * sizeof(*boundsFloating_values));
        if (boundsFloating_values == NULL) {
            free(bounds_values);
            return (jint)CNA_RESULT_OUT_OF_MEMORY;
        }
        (*environment)->GetFloatArrayRegion(environment, boundsFloating, 0, boundsFloating_length, boundsFloating_values);
        for (jsize element = 0; element < bounds_count; ++element) {
            bounds_values[element].center.x = (float)boundsFloating_values[element * 4 + 0];
            bounds_values[element].center.y = (float)boundsFloating_values[element * 4 + 1];
            bounds_values[element].center.z = (float)boundsFloating_values[element * 4 + 2];
            bounds_values[element].radius = (float)boundsFloating_values[element * 4 + 3];
        }
        free(boundsFloating_values);
    }
    CNA_Result call_result = cna.clustered_light_assignment_assign((CNA_ClusteredLightAssignmentHandle)assignment, (CNA_ClusteredLightGridHandle)grid, &view_value, bounds_values, (uint64_t)bounds_count);
    free(bounds_values);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightAssignmentClear(
    JNIEnv* environment, jclass declaring_class, jlong assignment)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.clustered_light_assignment_clear((CNA_ClusteredLightAssignmentHandle)assignment);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightAssignmentCopyIndices(
    JNIEnv* environment, jclass declaring_class, jlong assignment, jintArray destination, jlongArray out_count)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jint* destination_elements = (*environment)->GetIntArrayElements(environment, destination, NULL);
    if (destination_elements == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    int32_t* destination_values = (int32_t*)malloc(
        ((size_t)destination_size + 1U) * sizeof(*destination_values));
    if (destination_values == NULL) {
        (*environment)->ReleaseIntArrayElements(
            environment, destination, destination_elements, JNI_ABORT);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    for (jsize index = 0; index < destination_size; ++index) {
        destination_values[index] = (int32_t)destination_elements[index];
    }
    uint64_t out_count_value = 0;
    CNA_Result call_result = cna.clustered_light_assignment_copy_indices((CNA_ClusteredLightAssignmentHandle)assignment, destination_values, (uint64_t)destination_size, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS) {
        for (jsize index = 0; index < destination_size; ++index) {
            destination_elements[index] = (jint)destination_values[index];
        }
    }
    free(destination_values);
    (*environment)->ReleaseIntArrayElements(
        environment, destination, destination_elements, 0);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_count_element = (jlong)out_count_value;
        (*environment)->SetLongArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightAssignmentCopyLightsInCluster(
    JNIEnv* environment, jclass declaring_class, jlong assignment, jint cluster_index, jintArray destination, jlongArray out_count)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jint* destination_elements = (*environment)->GetIntArrayElements(environment, destination, NULL);
    if (destination_elements == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    int32_t* destination_values = (int32_t*)malloc(
        ((size_t)destination_size + 1U) * sizeof(*destination_values));
    if (destination_values == NULL) {
        (*environment)->ReleaseIntArrayElements(
            environment, destination, destination_elements, JNI_ABORT);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    for (jsize index = 0; index < destination_size; ++index) {
        destination_values[index] = (int32_t)destination_elements[index];
    }
    uint64_t out_count_value = 0;
    CNA_Result call_result = cna.clustered_light_assignment_copy_lights_in_cluster((CNA_ClusteredLightAssignmentHandle)assignment, (int32_t)cluster_index, destination_values, (uint64_t)destination_size, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS) {
        for (jsize index = 0; index < destination_size; ++index) {
            destination_elements[index] = (jint)destination_values[index];
        }
    }
    free(destination_values);
    (*environment)->ReleaseIntArrayElements(
        environment, destination, destination_elements, 0);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_count_element = (jlong)out_count_value;
        (*environment)->SetLongArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightAssignmentCopyOffsets(
    JNIEnv* environment, jclass declaring_class, jlong assignment, jintArray destination, jlongArray out_count)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jint* destination_elements = (*environment)->GetIntArrayElements(environment, destination, NULL);
    if (destination_elements == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    int32_t* destination_values = (int32_t*)malloc(
        ((size_t)destination_size + 1U) * sizeof(*destination_values));
    if (destination_values == NULL) {
        (*environment)->ReleaseIntArrayElements(
            environment, destination, destination_elements, JNI_ABORT);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    for (jsize index = 0; index < destination_size; ++index) {
        destination_values[index] = (int32_t)destination_elements[index];
    }
    uint64_t out_count_value = 0;
    CNA_Result call_result = cna.clustered_light_assignment_copy_offsets((CNA_ClusteredLightAssignmentHandle)assignment, destination_values, (uint64_t)destination_size, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS) {
        for (jsize index = 0; index < destination_size; ++index) {
            destination_elements[index] = (jint)destination_values[index];
        }
    }
    free(destination_values);
    (*environment)->ReleaseIntArrayElements(
        environment, destination, destination_elements, 0);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_count_element = (jlong)out_count_value;
        (*environment)->SetLongArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightAssignmentCreate(
    JNIEnv* environment, jclass declaring_class, jlong game, jlongArray out_assignment)
{
    (void)environment;
    (void)declaring_class;
    CNA_ClusteredLightAssignmentHandle out_assignment_value = 0;
    CNA_Result call_result = cna.clustered_light_assignment_create((CNA_Handle)game, &out_assignment_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_assignment_element = (jlong)out_assignment_value;
        (*environment)->SetLongArrayRegion(
            environment, out_assignment, 0, 1, &out_assignment_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightAssignmentDestroy(
    JNIEnv* environment, jclass declaring_class, jlong assignment)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.clustered_light_assignment_destroy((CNA_ClusteredLightAssignmentHandle)assignment);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightAssignmentGetClusterCount(
    JNIEnv* environment, jclass declaring_class, jlong assignment, jintArray out_count)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_count_value = 0;
    CNA_Result call_result = cna.clustered_light_assignment_get_cluster_count((CNA_ClusteredLightAssignmentHandle)assignment, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_count_element = (jint)out_count_value;
        (*environment)->SetIntArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightAssignmentGetLightCount(
    JNIEnv* environment, jclass declaring_class, jlong assignment, jintArray out_count)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_count_value = 0;
    CNA_Result call_result = cna.clustered_light_assignment_get_light_count((CNA_ClusteredLightAssignmentHandle)assignment, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_count_element = (jint)out_count_value;
        (*environment)->SetIntArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightAssignmentGetMaxLightsPerCluster(
    JNIEnv* environment, jclass declaring_class, jlong assignment, jintArray out_count)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_count_value = 0;
    CNA_Result call_result = cna.clustered_light_assignment_get_max_lights_per_cluster((CNA_ClusteredLightAssignmentHandle)assignment, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_count_element = (jint)out_count_value;
        (*environment)->SetIntArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightAssignmentGetTotalReferenceCount(
    JNIEnv* environment, jclass declaring_class, jlong assignment, jintArray out_count)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_count_value = 0;
    CNA_Result call_result = cna.clustered_light_assignment_get_total_reference_count((CNA_ClusteredLightAssignmentHandle)assignment, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_count_element = (jint)out_count_value;
        (*environment)->SetIntArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightBufferBind(
    JNIEnv* environment, jclass declaring_class, jlong buffer, jlong effect, jint first_unit)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.clustered_light_buffer_bind((CNA_ClusteredLightBufferHandle)buffer, (CNA_EffectHandle)effect, (int32_t)first_unit);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightBufferCopyLightLookupGlsl(
    JNIEnv* environment, jclass declaring_class, jbyteArray destination, jlongArray out_bytes)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t destination_written = 0;
    CNA_Result call_result = cna.clustered_light_buffer_copy_light_lookup_glsl((char*)destination_bytes, (uint64_t)destination_size, &destination_written);
    (*environment)->ReleaseByteArrayElements(environment, destination, destination_bytes, 0);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong destination_element = (jlong)destination_written;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &destination_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightBufferCreate(
    JNIEnv* environment, jclass declaring_class, jlong graphics_device, jlongArray out_buffer)
{
    (void)environment;
    (void)declaring_class;
    CNA_ClusteredLightBufferHandle out_buffer_value = 0;
    CNA_Result call_result = cna.clustered_light_buffer_create((CNA_Handle)graphics_device, &out_buffer_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_buffer_element = (jlong)out_buffer_value;
        (*environment)->SetLongArrayRegion(
            environment, out_buffer, 0, 1, &out_buffer_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightBufferDestroy(
    JNIEnv* environment, jclass declaring_class, jlong buffer)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.clustered_light_buffer_destroy((CNA_ClusteredLightBufferHandle)buffer);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightBufferGetClusterCount(
    JNIEnv* environment, jclass declaring_class, jlong buffer, jintArray out_count)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_count_value = 0;
    CNA_Result call_result = cna.clustered_light_buffer_get_cluster_count((CNA_ClusteredLightBufferHandle)buffer, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_count_element = (jint)out_count_value;
        (*environment)->SetIntArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightBufferGetLightCount(
    JNIEnv* environment, jclass declaring_class, jlong buffer, jintArray out_count)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_count_value = 0;
    CNA_Result call_result = cna.clustered_light_buffer_get_light_count((CNA_ClusteredLightBufferHandle)buffer, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_count_element = (jint)out_count_value;
        (*environment)->SetIntArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightBufferGetReferenceCount(
    JNIEnv* environment, jclass declaring_class, jlong buffer, jintArray out_count)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_count_value = 0;
    CNA_Result call_result = cna.clustered_light_buffer_get_reference_count((CNA_ClusteredLightBufferHandle)buffer, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_count_element = (jint)out_count_value;
        (*environment)->SetIntArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightBufferIsUploaded(
    JNIEnv* environment, jclass declaring_class, jlong buffer, jbooleanArray out_uploaded)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_uploaded_value = 0;
    CNA_Result call_result = cna.clustered_light_buffer_is_uploaded((CNA_ClusteredLightBufferHandle)buffer, &out_uploaded_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_uploaded_element = (jboolean)out_uploaded_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_uploaded, 0, 1, &out_uploaded_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightBufferUpload(
    JNIEnv* environment, jclass declaring_class, jlong buffer, jlong lights, jlong grid, jlong assignment)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.clustered_light_buffer_upload((CNA_ClusteredLightBufferHandle)buffer, (CNA_ClusteredLightSetHandle)lights, (CNA_ClusteredLightGridHandle)grid, (CNA_ClusteredLightAssignmentHandle)assignment);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightComputeAssign(
    JNIEnv* environment, jclass declaring_class, jlong compute, jlong grid, jfloatArray viewFloating, jfloatArray boundsFloating, jlong out_assignment)
{
    (void)environment;
    (void)declaring_class;
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
    jsize bounds_count = (*environment)->GetArrayLength(environment, boundsFloating) / 4;
    if ((*environment)->GetArrayLength(environment, boundsFloating) != bounds_count * 4) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_BoundingSphere* bounds_values = (CNA_BoundingSphere*)calloc(
        (size_t)bounds_count + 1U, sizeof(*bounds_values));
    if (bounds_values == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    {
        jsize boundsFloating_length = (*environment)->GetArrayLength(environment, boundsFloating);
        jfloat* boundsFloating_values = (jfloat*)malloc(
            ((size_t)boundsFloating_length + 1U) * sizeof(*boundsFloating_values));
        if (boundsFloating_values == NULL) {
            free(bounds_values);
            return (jint)CNA_RESULT_OUT_OF_MEMORY;
        }
        (*environment)->GetFloatArrayRegion(environment, boundsFloating, 0, boundsFloating_length, boundsFloating_values);
        for (jsize element = 0; element < bounds_count; ++element) {
            bounds_values[element].center.x = (float)boundsFloating_values[element * 4 + 0];
            bounds_values[element].center.y = (float)boundsFloating_values[element * 4 + 1];
            bounds_values[element].center.z = (float)boundsFloating_values[element * 4 + 2];
            bounds_values[element].radius = (float)boundsFloating_values[element * 4 + 3];
        }
        free(boundsFloating_values);
    }
    CNA_Result call_result = cna.clustered_light_compute_assign((CNA_ClusteredLightComputeHandle)compute, (CNA_ClusteredLightGridHandle)grid, &view_value, bounds_values, (uint64_t)bounds_count, (CNA_ClusteredLightAssignmentHandle)out_assignment);
    free(bounds_values);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightComputeCopyUnsupportedReason(
    JNIEnv* environment, jclass declaring_class, jlong compute, jbyteArray destination, jlongArray out_bytes)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t destination_written = 0;
    CNA_Result call_result = cna.clustered_light_compute_copy_unsupported_reason((CNA_ClusteredLightComputeHandle)compute, (char*)destination_bytes, (uint64_t)destination_size, &destination_written);
    (*environment)->ReleaseByteArrayElements(environment, destination, destination_bytes, 0);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong destination_element = (jlong)destination_written;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &destination_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightComputeCreate(
    JNIEnv* environment, jclass declaring_class, jlong graphics_device, jint stride, jlongArray out_compute)
{
    (void)environment;
    (void)declaring_class;
    CNA_ClusteredLightComputeHandle out_compute_value = 0;
    CNA_Result call_result = cna.clustered_light_compute_create((CNA_Handle)graphics_device, (int32_t)stride, &out_compute_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_compute_element = (jlong)out_compute_value;
        (*environment)->SetLongArrayRegion(
            environment, out_compute, 0, 1, &out_compute_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightComputeDestroy(
    JNIEnv* environment, jclass declaring_class, jlong compute)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.clustered_light_compute_destroy((CNA_ClusteredLightComputeHandle)compute);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightComputeGetStride(
    JNIEnv* environment, jclass declaring_class, jlong compute, jintArray out_stride)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_stride_value = 0;
    CNA_Result call_result = cna.clustered_light_compute_get_stride((CNA_ClusteredLightComputeHandle)compute, &out_stride_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_stride_element = (jint)out_stride_value;
        (*environment)->SetIntArrayRegion(
            environment, out_stride, 0, 1, &out_stride_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightComputeHasOverflowed(
    JNIEnv* environment, jclass declaring_class, jlong compute, jbooleanArray out_overflowed)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_overflowed_value = 0;
    CNA_Result call_result = cna.clustered_light_compute_has_overflowed((CNA_ClusteredLightComputeHandle)compute, &out_overflowed_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_overflowed_element = (jboolean)out_overflowed_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_overflowed, 0, 1, &out_overflowed_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightComputeIsSupported(
    JNIEnv* environment, jclass declaring_class, jlong compute, jbooleanArray out_supported)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_supported_value = 0;
    CNA_Result call_result = cna.clustered_light_compute_is_supported((CNA_ClusteredLightComputeHandle)compute, &out_supported_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_supported_element = (jboolean)out_supported_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_supported, 0, 1, &out_supported_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightComputeUsedCompute(
    JNIEnv* environment, jclass declaring_class, jlong compute, jbooleanArray out_used)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_used_value = 0;
    CNA_Result call_result = cna.clustered_light_compute_used_compute((CNA_ClusteredLightComputeHandle)compute, &out_used_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_used_element = (jboolean)out_used_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_used, 0, 1, &out_used_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightExtInit(
    JNIEnv* environment, jclass declaring_class, jbyteArray out_lightBytes, jlongArray out_lightIntegral, jfloatArray out_lightFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_ClusteredLightEXT out_light_value;
    memset(&out_light_value, 0, sizeof out_light_value);
    out_light_value.struct_size = (uint32_t)(sizeof out_light_value);
    out_light_value.struct_version = UINT32_C(1);
    CNA_Result call_result = cna.clustered_light_ext_init(&out_light_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jbyte out_lightBytes_values[] = {
            (jbyte)out_light_value.reserved[0],
            (jbyte)out_light_value.reserved[1],
            (jbyte)out_light_value.reserved[2]
        };
        (*environment)->SetByteArrayRegion(environment, out_lightBytes, 0,
            (jsize)(sizeof out_lightBytes_values / sizeof out_lightBytes_values[0]),
            out_lightBytes_values);
        jlong out_lightIntegral_values[] = {
            (jlong)out_light_value.type,
            (jlong)out_light_value.casts_shadows
        };
        (*environment)->SetLongArrayRegion(environment, out_lightIntegral, 0,
            (jsize)(sizeof out_lightIntegral_values / sizeof out_lightIntegral_values[0]),
            out_lightIntegral_values);
        jfloat out_lightFloating_values[] = {
            (jfloat)out_light_value.position.x,
            (jfloat)out_light_value.position.y,
            (jfloat)out_light_value.position.z,
            (jfloat)out_light_value.direction.x,
            (jfloat)out_light_value.direction.y,
            (jfloat)out_light_value.direction.z,
            (jfloat)out_light_value.color.x,
            (jfloat)out_light_value.color.y,
            (jfloat)out_light_value.color.z,
            (jfloat)out_light_value.intensity,
            (jfloat)out_light_value.range,
            (jfloat)out_light_value.inner_angle,
            (jfloat)out_light_value.outer_angle
        };
        (*environment)->SetFloatArrayRegion(environment, out_lightFloating, 0,
            (jsize)(sizeof out_lightFloating_values / sizeof out_lightFloating_values[0]),
            out_lightFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightGridClusterBounds(
    JNIEnv* environment, jclass declaring_class, jlong grid, jint x, jint y, jint slice, jfloatArray out_boundsFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_BoundingBox out_bounds_value;
    memset(&out_bounds_value, 0, sizeof out_bounds_value);
    CNA_Result call_result = cna.clustered_light_grid_cluster_bounds((CNA_ClusteredLightGridHandle)grid, (int32_t)x, (int32_t)y, (int32_t)slice, &out_bounds_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_boundsFloating_values[] = {
            (jfloat)out_bounds_value.min.x,
            (jfloat)out_bounds_value.min.y,
            (jfloat)out_bounds_value.min.z,
            (jfloat)out_bounds_value.max.x,
            (jfloat)out_bounds_value.max.y,
            (jfloat)out_bounds_value.max.z
        };
        (*environment)->SetFloatArrayRegion(environment, out_boundsFloating, 0,
            (jsize)(sizeof out_boundsFloating_values / sizeof out_boundsFloating_values[0]),
            out_boundsFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightGridClusterIndex(
    JNIEnv* environment, jclass declaring_class, jlong grid, jint x, jint y, jint slice, jintArray out_index)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_index_value = 0;
    CNA_Result call_result = cna.clustered_light_grid_cluster_index((CNA_ClusteredLightGridHandle)grid, (int32_t)x, (int32_t)y, (int32_t)slice, &out_index_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_index_element = (jint)out_index_value;
        (*environment)->SetIntArrayRegion(
            environment, out_index, 0, 1, &out_index_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightGridCreate(
    JNIEnv* environment, jclass declaring_class, jlong game, jint tiles_x, jint tiles_y, jint slice_count, jlongArray out_grid)
{
    (void)environment;
    (void)declaring_class;
    CNA_ClusteredLightGridHandle out_grid_value = 0;
    CNA_Result call_result = cna.clustered_light_grid_create((CNA_Handle)game, (int32_t)tiles_x, (int32_t)tiles_y, (int32_t)slice_count, &out_grid_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_grid_element = (jlong)out_grid_value;
        (*environment)->SetLongArrayRegion(
            environment, out_grid, 0, 1, &out_grid_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightGridDestroy(
    JNIEnv* environment, jclass declaring_class, jlong grid)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.clustered_light_grid_destroy((CNA_ClusteredLightGridHandle)grid);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightGridGetClusterCount(
    JNIEnv* environment, jclass declaring_class, jlong grid, jintArray out_count)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_count_value = 0;
    CNA_Result call_result = cna.clustered_light_grid_get_cluster_count((CNA_ClusteredLightGridHandle)grid, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_count_element = (jint)out_count_value;
        (*environment)->SetIntArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightGridGetFarPlane(
    JNIEnv* environment, jclass declaring_class, jlong grid, jfloatArray out_far)
{
    (void)environment;
    (void)declaring_class;
    float out_far_value = 0;
    CNA_Result call_result = cna.clustered_light_grid_get_far_plane((CNA_ClusteredLightGridHandle)grid, &out_far_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_far_element = (jfloat)out_far_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_far, 0, 1, &out_far_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightGridGetInverseProjection(
    JNIEnv* environment, jclass declaring_class, jlong grid, jfloatArray out_matrixFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Matrix out_matrix_value;
    memset(&out_matrix_value, 0, sizeof out_matrix_value);
    CNA_Result call_result = cna.clustered_light_grid_get_inverse_projection((CNA_ClusteredLightGridHandle)grid, &out_matrix_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_matrixFloating_values[] = {
            (jfloat)out_matrix_value.m11,
            (jfloat)out_matrix_value.m12,
            (jfloat)out_matrix_value.m13,
            (jfloat)out_matrix_value.m14,
            (jfloat)out_matrix_value.m21,
            (jfloat)out_matrix_value.m22,
            (jfloat)out_matrix_value.m23,
            (jfloat)out_matrix_value.m24,
            (jfloat)out_matrix_value.m31,
            (jfloat)out_matrix_value.m32,
            (jfloat)out_matrix_value.m33,
            (jfloat)out_matrix_value.m34,
            (jfloat)out_matrix_value.m41,
            (jfloat)out_matrix_value.m42,
            (jfloat)out_matrix_value.m43,
            (jfloat)out_matrix_value.m44
        };
        (*environment)->SetFloatArrayRegion(environment, out_matrixFloating, 0,
            (jsize)(sizeof out_matrixFloating_values / sizeof out_matrixFloating_values[0]),
            out_matrixFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightGridGetNearPlane(
    JNIEnv* environment, jclass declaring_class, jlong grid, jfloatArray out_near)
{
    (void)environment;
    (void)declaring_class;
    float out_near_value = 0;
    CNA_Result call_result = cna.clustered_light_grid_get_near_plane((CNA_ClusteredLightGridHandle)grid, &out_near_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_near_element = (jfloat)out_near_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_near, 0, 1, &out_near_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightGridGetSliceCount(
    JNIEnv* environment, jclass declaring_class, jlong grid, jintArray out_slices)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_slices_value = 0;
    CNA_Result call_result = cna.clustered_light_grid_get_slice_count((CNA_ClusteredLightGridHandle)grid, &out_slices_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_slices_element = (jint)out_slices_value;
        (*environment)->SetIntArrayRegion(
            environment, out_slices, 0, 1, &out_slices_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightGridGetTilesX(
    JNIEnv* environment, jclass declaring_class, jlong grid, jintArray out_tiles)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_tiles_value = 0;
    CNA_Result call_result = cna.clustered_light_grid_get_tiles_x((CNA_ClusteredLightGridHandle)grid, &out_tiles_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_tiles_element = (jint)out_tiles_value;
        (*environment)->SetIntArrayRegion(
            environment, out_tiles, 0, 1, &out_tiles_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightGridGetTilesY(
    JNIEnv* environment, jclass declaring_class, jlong grid, jintArray out_tiles)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_tiles_value = 0;
    CNA_Result call_result = cna.clustered_light_grid_get_tiles_y((CNA_ClusteredLightGridHandle)grid, &out_tiles_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_tiles_element = (jint)out_tiles_value;
        (*environment)->SetIntArrayRegion(
            environment, out_tiles, 0, 1, &out_tiles_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightGridHasProjection(
    JNIEnv* environment, jclass declaring_class, jlong grid, jbooleanArray out_has)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_has_value = 0;
    CNA_Result call_result = cna.clustered_light_grid_has_projection((CNA_ClusteredLightGridHandle)grid, &out_has_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_has_element = (jboolean)out_has_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_has, 0, 1, &out_has_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightGridSetProjection(
    JNIEnv* environment, jclass declaring_class, jlong grid, jfloatArray projectionFloating, jfloat near_plane, jfloat far_plane)
{
    (void)environment;
    (void)declaring_class;
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
    CNA_Result call_result = cna.clustered_light_grid_set_projection((CNA_ClusteredLightGridHandle)grid, &projection_value, (float)near_plane, (float)far_plane);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightGridSliceDistance(
    JNIEnv* environment, jclass declaring_class, jlong grid, jint slice, jfloatArray out_distance)
{
    (void)environment;
    (void)declaring_class;
    float out_distance_value = 0;
    CNA_Result call_result = cna.clustered_light_grid_slice_distance((CNA_ClusteredLightGridHandle)grid, (int32_t)slice, &out_distance_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_distance_element = (jfloat)out_distance_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_distance, 0, 1, &out_distance_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightGridSliceForViewDistance(
    JNIEnv* environment, jclass declaring_class, jlong grid, jfloat view_distance, jintArray out_slice)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_slice_value = 0;
    CNA_Result call_result = cna.clustered_light_grid_slice_for_view_distance((CNA_ClusteredLightGridHandle)grid, (float)view_distance, &out_slice_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_slice_element = (jint)out_slice_value;
        (*environment)->SetIntArrayRegion(
            environment, out_slice, 0, 1, &out_slice_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightSetAdd(
    JNIEnv* environment, jclass declaring_class, jlong set, jbyteArray lightBytes, jlongArray lightIntegral, jfloatArray lightFloating, jintArray out_index)
{
    (void)environment;
    (void)declaring_class;
    CNA_ClusteredLightEXT light_value;
    memset(&light_value, 0, sizeof light_value);
    light_value.struct_size = (uint32_t)(sizeof light_value);
    light_value.struct_version = UINT32_C(1);
    {
        jbyte lightBytes_values[3];
        (*environment)->GetByteArrayRegion(environment, lightBytes, 0, 3, lightBytes_values);
        light_value.reserved[0] = (uint8_t)lightBytes_values[0];
        light_value.reserved[1] = (uint8_t)lightBytes_values[1];
        light_value.reserved[2] = (uint8_t)lightBytes_values[2];
    }
    {
        jlong lightIntegral_values[2];
        (*environment)->GetLongArrayRegion(environment, lightIntegral, 0, 2, lightIntegral_values);
        light_value.type = (CNA_ClusteredLightType)lightIntegral_values[0];
        light_value.casts_shadows = (CNA_Bool)lightIntegral_values[1];
    }
    {
        jfloat lightFloating_values[13];
        (*environment)->GetFloatArrayRegion(environment, lightFloating, 0, 13, lightFloating_values);
        light_value.position.x = (float)lightFloating_values[0];
        light_value.position.y = (float)lightFloating_values[1];
        light_value.position.z = (float)lightFloating_values[2];
        light_value.direction.x = (float)lightFloating_values[3];
        light_value.direction.y = (float)lightFloating_values[4];
        light_value.direction.z = (float)lightFloating_values[5];
        light_value.color.x = (float)lightFloating_values[6];
        light_value.color.y = (float)lightFloating_values[7];
        light_value.color.z = (float)lightFloating_values[8];
        light_value.intensity = (float)lightFloating_values[9];
        light_value.range = (float)lightFloating_values[10];
        light_value.inner_angle = (float)lightFloating_values[11];
        light_value.outer_angle = (float)lightFloating_values[12];
    }
    int32_t out_index_value = 0;
    CNA_Result call_result = cna.clustered_light_set_add((CNA_ClusteredLightSetHandle)set, &light_value, &out_index_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_index_element = (jint)out_index_value;
        (*environment)->SetIntArrayRegion(
            environment, out_index, 0, 1, &out_index_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightSetAddPoint(
    JNIEnv* environment, jclass declaring_class, jlong set, jbyteArray lightBytes, jlongArray lightIntegral, jfloatArray lightFloating, jintArray out_index)
{
    (void)environment;
    (void)declaring_class;
    CNA_PointLightEXT light_value;
    memset(&light_value, 0, sizeof light_value);
    light_value.struct_size = (uint32_t)(sizeof light_value);
    light_value.struct_version = UINT32_C(1);
    {
        jbyte lightBytes_values[3];
        (*environment)->GetByteArrayRegion(environment, lightBytes, 0, 3, lightBytes_values);
        light_value.reserved[0] = (uint8_t)lightBytes_values[0];
        light_value.reserved[1] = (uint8_t)lightBytes_values[1];
        light_value.reserved[2] = (uint8_t)lightBytes_values[2];
    }
    {
        jlong lightIntegral_values[1];
        (*environment)->GetLongArrayRegion(environment, lightIntegral, 0, 1, lightIntegral_values);
        light_value.casts_shadows = (CNA_Bool)lightIntegral_values[0];
    }
    {
        jfloat lightFloating_values[8];
        (*environment)->GetFloatArrayRegion(environment, lightFloating, 0, 8, lightFloating_values);
        light_value.position.x = (float)lightFloating_values[0];
        light_value.position.y = (float)lightFloating_values[1];
        light_value.position.z = (float)lightFloating_values[2];
        light_value.color.x = (float)lightFloating_values[3];
        light_value.color.y = (float)lightFloating_values[4];
        light_value.color.z = (float)lightFloating_values[5];
        light_value.intensity = (float)lightFloating_values[6];
        light_value.range = (float)lightFloating_values[7];
    }
    int32_t out_index_value = 0;
    CNA_Result call_result = cna.clustered_light_set_add_point((CNA_ClusteredLightSetHandle)set, &light_value, &out_index_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_index_element = (jint)out_index_value;
        (*environment)->SetIntArrayRegion(
            environment, out_index, 0, 1, &out_index_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightSetAddSpot(
    JNIEnv* environment, jclass declaring_class, jlong set, jbyteArray lightBytes, jlongArray lightIntegral, jfloatArray lightFloating, jintArray out_index)
{
    (void)environment;
    (void)declaring_class;
    CNA_SpotLightEXT light_value;
    memset(&light_value, 0, sizeof light_value);
    light_value.struct_size = (uint32_t)(sizeof light_value);
    light_value.struct_version = UINT32_C(1);
    {
        jbyte lightBytes_values[3];
        (*environment)->GetByteArrayRegion(environment, lightBytes, 0, 3, lightBytes_values);
        light_value.reserved[0] = (uint8_t)lightBytes_values[0];
        light_value.reserved[1] = (uint8_t)lightBytes_values[1];
        light_value.reserved[2] = (uint8_t)lightBytes_values[2];
    }
    {
        jlong lightIntegral_values[1];
        (*environment)->GetLongArrayRegion(environment, lightIntegral, 0, 1, lightIntegral_values);
        light_value.casts_shadows = (CNA_Bool)lightIntegral_values[0];
    }
    {
        jfloat lightFloating_values[13];
        (*environment)->GetFloatArrayRegion(environment, lightFloating, 0, 13, lightFloating_values);
        light_value.position.x = (float)lightFloating_values[0];
        light_value.position.y = (float)lightFloating_values[1];
        light_value.position.z = (float)lightFloating_values[2];
        light_value.direction.x = (float)lightFloating_values[3];
        light_value.direction.y = (float)lightFloating_values[4];
        light_value.direction.z = (float)lightFloating_values[5];
        light_value.color.x = (float)lightFloating_values[6];
        light_value.color.y = (float)lightFloating_values[7];
        light_value.color.z = (float)lightFloating_values[8];
        light_value.intensity = (float)lightFloating_values[9];
        light_value.range = (float)lightFloating_values[10];
        light_value.inner_angle = (float)lightFloating_values[11];
        light_value.outer_angle = (float)lightFloating_values[12];
    }
    int32_t out_index_value = 0;
    CNA_Result call_result = cna.clustered_light_set_add_spot((CNA_ClusteredLightSetHandle)set, &light_value, &out_index_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_index_element = (jint)out_index_value;
        (*environment)->SetIntArrayRegion(
            environment, out_index, 0, 1, &out_index_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightSetClear(
    JNIEnv* environment, jclass declaring_class, jlong set)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.clustered_light_set_clear((CNA_ClusteredLightSetHandle)set);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightSetCopyBounds(
    JNIEnv* environment, jclass declaring_class, jlong set, jfloatArray destinationFloating, jlongArray out_count)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_count = (*environment)->GetArrayLength(environment, destinationFloating) / 4;
    if ((*environment)->GetArrayLength(environment, destinationFloating) != destination_count * 4) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_BoundingSphere* destination_values = (CNA_BoundingSphere*)calloc(
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
            destination_values[element].center.x = (float)destinationFloating_values[element * 4 + 0];
            destination_values[element].center.y = (float)destinationFloating_values[element * 4 + 1];
            destination_values[element].center.z = (float)destinationFloating_values[element * 4 + 2];
            destination_values[element].radius = (float)destinationFloating_values[element * 4 + 3];
        }
        free(destinationFloating_values);
    }
    uint64_t out_count_value = 0;
    CNA_Result call_result = cna.clustered_light_set_copy_bounds((CNA_ClusteredLightSetHandle)set, destination_values, (uint64_t)destination_count, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS) {
        {
            jfloat* destinationFloating_out = (jfloat*)malloc(
                ((size_t)destination_count * 4 + 1U) * sizeof(*destinationFloating_out));
            if (destinationFloating_out != NULL) {
                for (jsize element = 0; element < destination_count; ++element) {
                    destinationFloating_out[element * 4 + 0] = (jfloat)destination_values[element].center.x;
                    destinationFloating_out[element * 4 + 1] = (jfloat)destination_values[element].center.y;
                    destinationFloating_out[element * 4 + 2] = (jfloat)destination_values[element].center.z;
                    destinationFloating_out[element * 4 + 3] = (jfloat)destination_values[element].radius;
                }
                (*environment)->SetFloatArrayRegion(
                    environment, destinationFloating, 0,
                    (jsize)(destination_count * 4), destinationFloating_out);
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
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightSetCopyLights(
    JNIEnv* environment, jclass declaring_class, jlong set, jbyteArray destinationBytes, jlongArray destinationIntegral, jfloatArray destinationFloating, jlongArray out_count)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_count = (*environment)->GetArrayLength(environment, destinationBytes) / 3;
    if ((*environment)->GetArrayLength(environment, destinationBytes) != destination_count * 3 ||
        (*environment)->GetArrayLength(environment, destinationIntegral) != destination_count * 2 ||
        (*environment)->GetArrayLength(environment, destinationFloating) != destination_count * 13) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_ClusteredLightEXT* destination_values = (CNA_ClusteredLightEXT*)calloc(
        (size_t)destination_count + 1U, sizeof(*destination_values));
    if (destination_values == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    {
        jsize destinationBytes_length = (*environment)->GetArrayLength(environment, destinationBytes);
        jbyte* destinationBytes_values = (jbyte*)malloc(
            ((size_t)destinationBytes_length + 1U) * sizeof(*destinationBytes_values));
        if (destinationBytes_values == NULL) {
            free(destination_values);
            return (jint)CNA_RESULT_OUT_OF_MEMORY;
        }
        (*environment)->GetByteArrayRegion(environment, destinationBytes, 0, destinationBytes_length, destinationBytes_values);
        for (jsize element = 0; element < destination_count; ++element) {
            destination_values[element].reserved[0] = (uint8_t)destinationBytes_values[element * 3 + 0];
            destination_values[element].reserved[1] = (uint8_t)destinationBytes_values[element * 3 + 1];
            destination_values[element].reserved[2] = (uint8_t)destinationBytes_values[element * 3 + 2];
        }
        free(destinationBytes_values);
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
            destination_values[element].type = (CNA_ClusteredLightType)destinationIntegral_values[element * 2 + 0];
            destination_values[element].casts_shadows = (CNA_Bool)destinationIntegral_values[element * 2 + 1];
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
            destination_values[element].position.x = (float)destinationFloating_values[element * 13 + 0];
            destination_values[element].position.y = (float)destinationFloating_values[element * 13 + 1];
            destination_values[element].position.z = (float)destinationFloating_values[element * 13 + 2];
            destination_values[element].direction.x = (float)destinationFloating_values[element * 13 + 3];
            destination_values[element].direction.y = (float)destinationFloating_values[element * 13 + 4];
            destination_values[element].direction.z = (float)destinationFloating_values[element * 13 + 5];
            destination_values[element].color.x = (float)destinationFloating_values[element * 13 + 6];
            destination_values[element].color.y = (float)destinationFloating_values[element * 13 + 7];
            destination_values[element].color.z = (float)destinationFloating_values[element * 13 + 8];
            destination_values[element].intensity = (float)destinationFloating_values[element * 13 + 9];
            destination_values[element].range = (float)destinationFloating_values[element * 13 + 10];
            destination_values[element].inner_angle = (float)destinationFloating_values[element * 13 + 11];
            destination_values[element].outer_angle = (float)destinationFloating_values[element * 13 + 12];
        }
        free(destinationFloating_values);
    }
    for (jsize element = 0; element < destination_count; ++element) {
        destination_values[element].struct_size = (uint32_t)(sizeof destination_values[element]);
        destination_values[element].struct_version = UINT32_C(1);
    }
    uint64_t out_count_value = 0;
    CNA_Result call_result = cna.clustered_light_set_copy_lights((CNA_ClusteredLightSetHandle)set, destination_values, (uint64_t)destination_count, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS) {
        {
            jbyte* destinationBytes_out = (jbyte*)malloc(
                ((size_t)destination_count * 3 + 1U) * sizeof(*destinationBytes_out));
            if (destinationBytes_out != NULL) {
                for (jsize element = 0; element < destination_count; ++element) {
                    destinationBytes_out[element * 3 + 0] = (jbyte)destination_values[element].reserved[0];
                    destinationBytes_out[element * 3 + 1] = (jbyte)destination_values[element].reserved[1];
                    destinationBytes_out[element * 3 + 2] = (jbyte)destination_values[element].reserved[2];
                }
                (*environment)->SetByteArrayRegion(
                    environment, destinationBytes, 0,
                    (jsize)(destination_count * 3), destinationBytes_out);
                free(destinationBytes_out);
            }
        }
        {
            jlong* destinationIntegral_out = (jlong*)malloc(
                ((size_t)destination_count * 2 + 1U) * sizeof(*destinationIntegral_out));
            if (destinationIntegral_out != NULL) {
                for (jsize element = 0; element < destination_count; ++element) {
                    destinationIntegral_out[element * 2 + 0] = (jlong)destination_values[element].type;
                    destinationIntegral_out[element * 2 + 1] = (jlong)destination_values[element].casts_shadows;
                }
                (*environment)->SetLongArrayRegion(
                    environment, destinationIntegral, 0,
                    (jsize)(destination_count * 2), destinationIntegral_out);
                free(destinationIntegral_out);
            }
        }
        {
            jfloat* destinationFloating_out = (jfloat*)malloc(
                ((size_t)destination_count * 13 + 1U) * sizeof(*destinationFloating_out));
            if (destinationFloating_out != NULL) {
                for (jsize element = 0; element < destination_count; ++element) {
                    destinationFloating_out[element * 13 + 0] = (jfloat)destination_values[element].position.x;
                    destinationFloating_out[element * 13 + 1] = (jfloat)destination_values[element].position.y;
                    destinationFloating_out[element * 13 + 2] = (jfloat)destination_values[element].position.z;
                    destinationFloating_out[element * 13 + 3] = (jfloat)destination_values[element].direction.x;
                    destinationFloating_out[element * 13 + 4] = (jfloat)destination_values[element].direction.y;
                    destinationFloating_out[element * 13 + 5] = (jfloat)destination_values[element].direction.z;
                    destinationFloating_out[element * 13 + 6] = (jfloat)destination_values[element].color.x;
                    destinationFloating_out[element * 13 + 7] = (jfloat)destination_values[element].color.y;
                    destinationFloating_out[element * 13 + 8] = (jfloat)destination_values[element].color.z;
                    destinationFloating_out[element * 13 + 9] = (jfloat)destination_values[element].intensity;
                    destinationFloating_out[element * 13 + 10] = (jfloat)destination_values[element].range;
                    destinationFloating_out[element * 13 + 11] = (jfloat)destination_values[element].inner_angle;
                    destinationFloating_out[element * 13 + 12] = (jfloat)destination_values[element].outer_angle;
                }
                (*environment)->SetFloatArrayRegion(
                    environment, destinationFloating, 0,
                    (jsize)(destination_count * 13), destinationFloating_out);
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
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightSetCreate(
    JNIEnv* environment, jclass declaring_class, jlong game, jlongArray out_set)
{
    (void)environment;
    (void)declaring_class;
    CNA_ClusteredLightSetHandle out_set_value = 0;
    CNA_Result call_result = cna.clustered_light_set_create((CNA_Handle)game, &out_set_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_set_element = (jlong)out_set_value;
        (*environment)->SetLongArrayRegion(
            environment, out_set, 0, 1, &out_set_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightSetDestroy(
    JNIEnv* environment, jclass declaring_class, jlong set)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.clustered_light_set_destroy((CNA_ClusteredLightSetHandle)set);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightSetGetAt(
    JNIEnv* environment, jclass declaring_class, jlong set, jint index, jbyteArray out_lightBytes, jlongArray out_lightIntegral, jfloatArray out_lightFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_ClusteredLightEXT out_light_value;
    memset(&out_light_value, 0, sizeof out_light_value);
    out_light_value.struct_size = (uint32_t)(sizeof out_light_value);
    out_light_value.struct_version = UINT32_C(1);
    CNA_Result call_result = cna.clustered_light_set_get_at((CNA_ClusteredLightSetHandle)set, (int32_t)index, &out_light_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jbyte out_lightBytes_values[] = {
            (jbyte)out_light_value.reserved[0],
            (jbyte)out_light_value.reserved[1],
            (jbyte)out_light_value.reserved[2]
        };
        (*environment)->SetByteArrayRegion(environment, out_lightBytes, 0,
            (jsize)(sizeof out_lightBytes_values / sizeof out_lightBytes_values[0]),
            out_lightBytes_values);
        jlong out_lightIntegral_values[] = {
            (jlong)out_light_value.type,
            (jlong)out_light_value.casts_shadows
        };
        (*environment)->SetLongArrayRegion(environment, out_lightIntegral, 0,
            (jsize)(sizeof out_lightIntegral_values / sizeof out_lightIntegral_values[0]),
            out_lightIntegral_values);
        jfloat out_lightFloating_values[] = {
            (jfloat)out_light_value.position.x,
            (jfloat)out_light_value.position.y,
            (jfloat)out_light_value.position.z,
            (jfloat)out_light_value.direction.x,
            (jfloat)out_light_value.direction.y,
            (jfloat)out_light_value.direction.z,
            (jfloat)out_light_value.color.x,
            (jfloat)out_light_value.color.y,
            (jfloat)out_light_value.color.z,
            (jfloat)out_light_value.intensity,
            (jfloat)out_light_value.range,
            (jfloat)out_light_value.inner_angle,
            (jfloat)out_light_value.outer_angle
        };
        (*environment)->SetFloatArrayRegion(environment, out_lightFloating, 0,
            (jsize)(sizeof out_lightFloating_values / sizeof out_lightFloating_values[0]),
            out_lightFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightSetGetBoundsAt(
    JNIEnv* environment, jclass declaring_class, jlong set, jint index, jfloatArray out_boundsFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_BoundingSphere out_bounds_value;
    memset(&out_bounds_value, 0, sizeof out_bounds_value);
    CNA_Result call_result = cna.clustered_light_set_get_bounds_at((CNA_ClusteredLightSetHandle)set, (int32_t)index, &out_bounds_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_boundsFloating_values[] = {
            (jfloat)out_bounds_value.center.x,
            (jfloat)out_bounds_value.center.y,
            (jfloat)out_bounds_value.center.z,
            (jfloat)out_bounds_value.radius
        };
        (*environment)->SetFloatArrayRegion(environment, out_boundsFloating, 0,
            (jsize)(sizeof out_boundsFloating_values / sizeof out_boundsFloating_values[0]),
            out_boundsFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightSetGetCount(
    JNIEnv* environment, jclass declaring_class, jlong set, jintArray out_count)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_count_value = 0;
    CNA_Result call_result = cna.clustered_light_set_get_count((CNA_ClusteredLightSetHandle)set, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_count_element = (jint)out_count_value;
        (*environment)->SetIntArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightSetIsEmpty(
    JNIEnv* environment, jclass declaring_class, jlong set, jbooleanArray out_empty)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_empty_value = 0;
    CNA_Result call_result = cna.clustered_light_set_is_empty((CNA_ClusteredLightSetHandle)set, &out_empty_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_empty_element = (jboolean)out_empty_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_empty, 0, 1, &out_empty_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightSetIsUsable(
    JNIEnv* environment, jclass declaring_class, jbyteArray lightBytes, jlongArray lightIntegral, jfloatArray lightFloating, jbooleanArray out_usable)
{
    (void)environment;
    (void)declaring_class;
    CNA_ClusteredLightEXT light_value;
    memset(&light_value, 0, sizeof light_value);
    light_value.struct_size = (uint32_t)(sizeof light_value);
    light_value.struct_version = UINT32_C(1);
    {
        jbyte lightBytes_values[3];
        (*environment)->GetByteArrayRegion(environment, lightBytes, 0, 3, lightBytes_values);
        light_value.reserved[0] = (uint8_t)lightBytes_values[0];
        light_value.reserved[1] = (uint8_t)lightBytes_values[1];
        light_value.reserved[2] = (uint8_t)lightBytes_values[2];
    }
    {
        jlong lightIntegral_values[2];
        (*environment)->GetLongArrayRegion(environment, lightIntegral, 0, 2, lightIntegral_values);
        light_value.type = (CNA_ClusteredLightType)lightIntegral_values[0];
        light_value.casts_shadows = (CNA_Bool)lightIntegral_values[1];
    }
    {
        jfloat lightFloating_values[13];
        (*environment)->GetFloatArrayRegion(environment, lightFloating, 0, 13, lightFloating_values);
        light_value.position.x = (float)lightFloating_values[0];
        light_value.position.y = (float)lightFloating_values[1];
        light_value.position.z = (float)lightFloating_values[2];
        light_value.direction.x = (float)lightFloating_values[3];
        light_value.direction.y = (float)lightFloating_values[4];
        light_value.direction.z = (float)lightFloating_values[5];
        light_value.color.x = (float)lightFloating_values[6];
        light_value.color.y = (float)lightFloating_values[7];
        light_value.color.z = (float)lightFloating_values[8];
        light_value.intensity = (float)lightFloating_values[9];
        light_value.range = (float)lightFloating_values[10];
        light_value.inner_angle = (float)lightFloating_values[11];
        light_value.outer_angle = (float)lightFloating_values[12];
    }
    CNA_Bool out_usable_value = 0;
    CNA_Result call_result = cna.clustered_light_set_is_usable(&light_value, &out_usable_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_usable_element = (jboolean)out_usable_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_usable, 0, 1, &out_usable_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightSetRemoveAt(
    JNIEnv* environment, jclass declaring_class, jlong set, jint index)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.clustered_light_set_remove_at((CNA_ClusteredLightSetHandle)set, (int32_t)index);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_clusteredLightSetReplaceAt(
    JNIEnv* environment, jclass declaring_class, jlong set, jint index, jbyteArray lightBytes, jlongArray lightIntegral, jfloatArray lightFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_ClusteredLightEXT light_value;
    memset(&light_value, 0, sizeof light_value);
    light_value.struct_size = (uint32_t)(sizeof light_value);
    light_value.struct_version = UINT32_C(1);
    {
        jbyte lightBytes_values[3];
        (*environment)->GetByteArrayRegion(environment, lightBytes, 0, 3, lightBytes_values);
        light_value.reserved[0] = (uint8_t)lightBytes_values[0];
        light_value.reserved[1] = (uint8_t)lightBytes_values[1];
        light_value.reserved[2] = (uint8_t)lightBytes_values[2];
    }
    {
        jlong lightIntegral_values[2];
        (*environment)->GetLongArrayRegion(environment, lightIntegral, 0, 2, lightIntegral_values);
        light_value.type = (CNA_ClusteredLightType)lightIntegral_values[0];
        light_value.casts_shadows = (CNA_Bool)lightIntegral_values[1];
    }
    {
        jfloat lightFloating_values[13];
        (*environment)->GetFloatArrayRegion(environment, lightFloating, 0, 13, lightFloating_values);
        light_value.position.x = (float)lightFloating_values[0];
        light_value.position.y = (float)lightFloating_values[1];
        light_value.position.z = (float)lightFloating_values[2];
        light_value.direction.x = (float)lightFloating_values[3];
        light_value.direction.y = (float)lightFloating_values[4];
        light_value.direction.z = (float)lightFloating_values[5];
        light_value.color.x = (float)lightFloating_values[6];
        light_value.color.y = (float)lightFloating_values[7];
        light_value.color.z = (float)lightFloating_values[8];
        light_value.intensity = (float)lightFloating_values[9];
        light_value.range = (float)lightFloating_values[10];
        light_value.inner_angle = (float)lightFloating_values[11];
        light_value.outer_angle = (float)lightFloating_values[12];
    }
    CNA_Result call_result = cna.clustered_light_set_replace_at((CNA_ClusteredLightSetHandle)set, (int32_t)index, &light_value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_contactShadowPassCombineVisibility(
    JNIEnv* environment, jclass declaring_class, jfloat shadow_map_visibility, jfloat contact_visibility, jfloatArray out_visibility)
{
    (void)environment;
    (void)declaring_class;
    float out_visibility_value = 0;
    CNA_Result call_result = cna.contact_shadow_pass_combine_visibility((float)shadow_map_visibility, (float)contact_visibility, &out_visibility_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_visibility_element = (jfloat)out_visibility_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_visibility, 0, 1, &out_visibility_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_contactShadowPassCopyFallbackReason(
    JNIEnv* environment, jclass declaring_class, jlong pass, jbyteArray destination, jlongArray out_bytes)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t destination_written = 0;
    CNA_Result call_result = cna.contact_shadow_pass_copy_fallback_reason((CNA_PostProcessPassHandle)pass, (char*)destination_bytes, (uint64_t)destination_size, &destination_written);
    (*environment)->ReleaseByteArrayElements(environment, destination, destination_bytes, 0);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong destination_element = (jlong)destination_written;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &destination_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_contactShadowPassCopyOcclusionTestGlsl(
    JNIEnv* environment, jclass declaring_class, jbyteArray destination, jlongArray out_bytes)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t destination_written = 0;
    CNA_Result call_result = cna.contact_shadow_pass_copy_occlusion_test_glsl((char*)destination_bytes, (uint64_t)destination_size, &destination_written);
    (*environment)->ReleaseByteArrayElements(environment, destination, destination_bytes, 0);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong destination_element = (jlong)destination_written;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &destination_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_contactShadowPassCreate(
    JNIEnv* environment, jclass declaring_class, jlong graphics_device, jlongArray out_pass)
{
    (void)environment;
    (void)declaring_class;
    CNA_PostProcessPassHandle out_pass_value = 0;
    CNA_Result call_result = cna.contact_shadow_pass_create((CNA_Handle)graphics_device, &out_pass_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_pass_element = (jlong)out_pass_value;
        (*environment)->SetLongArrayRegion(
            environment, out_pass, 0, 1, &out_pass_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_contactShadowPassGetBias(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_bias)
{
    (void)environment;
    (void)declaring_class;
    float out_bias_value = 0;
    CNA_Result call_result = cna.contact_shadow_pass_get_bias((CNA_PostProcessPassHandle)pass, &out_bias_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_bias_element = (jfloat)out_bias_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_bias, 0, 1, &out_bias_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_contactShadowPassGetIntensity(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_intensity)
{
    (void)environment;
    (void)declaring_class;
    float out_intensity_value = 0;
    CNA_Result call_result = cna.contact_shadow_pass_get_intensity((CNA_PostProcessPassHandle)pass, &out_intensity_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_intensity_element = (jfloat)out_intensity_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_intensity, 0, 1, &out_intensity_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_contactShadowPassGetLightDirection(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_directionFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Vector3 out_direction_value;
    memset(&out_direction_value, 0, sizeof out_direction_value);
    CNA_Result call_result = cna.contact_shadow_pass_get_light_direction((CNA_PostProcessPassHandle)pass, &out_direction_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_directionFloating_values[] = {
            (jfloat)out_direction_value.x,
            (jfloat)out_direction_value.y,
            (jfloat)out_direction_value.z
        };
        (*environment)->SetFloatArrayRegion(environment, out_directionFloating, 0,
            (jsize)(sizeof out_directionFloating_values / sizeof out_directionFloating_values[0]),
            out_directionFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_contactShadowPassGetMaxDistance(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_distance)
{
    (void)environment;
    (void)declaring_class;
    float out_distance_value = 0;
    CNA_Result call_result = cna.contact_shadow_pass_get_max_distance((CNA_PostProcessPassHandle)pass, &out_distance_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_distance_element = (jfloat)out_distance_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_distance, 0, 1, &out_distance_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_contactShadowPassGetStepCount(
    JNIEnv* environment, jclass declaring_class, jlong pass, jintArray out_count)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_count_value = 0;
    CNA_Result call_result = cna.contact_shadow_pass_get_step_count((CNA_PostProcessPassHandle)pass, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_count_element = (jint)out_count_value;
        (*environment)->SetIntArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_contactShadowPassGetThickness(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_thickness)
{
    (void)environment;
    (void)declaring_class;
    float out_thickness_value = 0;
    CNA_Result call_result = cna.contact_shadow_pass_get_thickness((CNA_PostProcessPassHandle)pass, &out_thickness_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_thickness_element = (jfloat)out_thickness_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_thickness, 0, 1, &out_thickness_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_contactShadowPassIsOccluded(
    JNIEnv* environment, jclass declaring_class, jfloat ray_view_depth, jfloat scene_view_depth, jfloat bias, jfloat thickness, jbooleanArray out_occluded)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_occluded_value = 0;
    CNA_Result call_result = cna.contact_shadow_pass_is_occluded((float)ray_view_depth, (float)scene_view_depth, (float)bias, (float)thickness, &out_occluded_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_occluded_element = (jboolean)out_occluded_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_occluded, 0, 1, &out_occluded_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_contactShadowPassSetBias(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat bias)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.contact_shadow_pass_set_bias((CNA_PostProcessPassHandle)pass, (float)bias);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_contactShadowPassSetIntensity(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat intensity)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.contact_shadow_pass_set_intensity((CNA_PostProcessPassHandle)pass, (float)intensity);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_contactShadowPassSetLightDirection(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray directionFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Vector3 direction_value;
    memset(&direction_value, 0, sizeof direction_value);
    {
        jfloat directionFloating_values[3];
        (*environment)->GetFloatArrayRegion(environment, directionFloating, 0, 3, directionFloating_values);
        direction_value.x = (float)directionFloating_values[0];
        direction_value.y = (float)directionFloating_values[1];
        direction_value.z = (float)directionFloating_values[2];
    }
    CNA_Result call_result = cna.contact_shadow_pass_set_light_direction((CNA_PostProcessPassHandle)pass, &direction_value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_contactShadowPassSetMaxDistance(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat distance)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.contact_shadow_pass_set_max_distance((CNA_PostProcessPassHandle)pass, (float)distance);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_contactShadowPassSetStepCount(
    JNIEnv* environment, jclass declaring_class, jlong pass, jint count)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.contact_shadow_pass_set_step_count((CNA_PostProcessPassHandle)pass, (int32_t)count);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_contactShadowPassSetThickness(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat thickness)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.contact_shadow_pass_set_thickness((CNA_PostProcessPassHandle)pass, (float)thickness);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cubeShadowMapBegin(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jint face_index)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.cube_shadow_map_begin((CNA_CubeShadowMapHandle)shadow_map, (int32_t)face_index);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cubeShadowMapComputeFaceProjection(
    JNIEnv* environment, jclass declaring_class, jfloat range, jfloatArray out_matrixFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Matrix out_matrix_value;
    memset(&out_matrix_value, 0, sizeof out_matrix_value);
    CNA_Result call_result = cna.cube_shadow_map_compute_face_projection((float)range, &out_matrix_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_matrixFloating_values[] = {
            (jfloat)out_matrix_value.m11,
            (jfloat)out_matrix_value.m12,
            (jfloat)out_matrix_value.m13,
            (jfloat)out_matrix_value.m14,
            (jfloat)out_matrix_value.m21,
            (jfloat)out_matrix_value.m22,
            (jfloat)out_matrix_value.m23,
            (jfloat)out_matrix_value.m24,
            (jfloat)out_matrix_value.m31,
            (jfloat)out_matrix_value.m32,
            (jfloat)out_matrix_value.m33,
            (jfloat)out_matrix_value.m34,
            (jfloat)out_matrix_value.m41,
            (jfloat)out_matrix_value.m42,
            (jfloat)out_matrix_value.m43,
            (jfloat)out_matrix_value.m44
        };
        (*environment)->SetFloatArrayRegion(environment, out_matrixFloating, 0,
            (jsize)(sizeof out_matrixFloating_values / sizeof out_matrixFloating_values[0]),
            out_matrixFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cubeShadowMapComputeFaceView(
    JNIEnv* environment, jclass declaring_class, jint face, jfloatArray positionFloating, jfloatArray out_matrixFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Vector3 position_value;
    memset(&position_value, 0, sizeof position_value);
    {
        jfloat positionFloating_values[3];
        (*environment)->GetFloatArrayRegion(environment, positionFloating, 0, 3, positionFloating_values);
        position_value.x = (float)positionFloating_values[0];
        position_value.y = (float)positionFloating_values[1];
        position_value.z = (float)positionFloating_values[2];
    }
    CNA_Matrix out_matrix_value;
    memset(&out_matrix_value, 0, sizeof out_matrix_value);
    CNA_Result call_result = cna.cube_shadow_map_compute_face_view((CNA_CubeMapFace)face, &position_value, &out_matrix_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_matrixFloating_values[] = {
            (jfloat)out_matrix_value.m11,
            (jfloat)out_matrix_value.m12,
            (jfloat)out_matrix_value.m13,
            (jfloat)out_matrix_value.m14,
            (jfloat)out_matrix_value.m21,
            (jfloat)out_matrix_value.m22,
            (jfloat)out_matrix_value.m23,
            (jfloat)out_matrix_value.m24,
            (jfloat)out_matrix_value.m31,
            (jfloat)out_matrix_value.m32,
            (jfloat)out_matrix_value.m33,
            (jfloat)out_matrix_value.m34,
            (jfloat)out_matrix_value.m41,
            (jfloat)out_matrix_value.m42,
            (jfloat)out_matrix_value.m43,
            (jfloat)out_matrix_value.m44
        };
        (*environment)->SetFloatArrayRegion(environment, out_matrixFloating, 0,
            (jsize)(sizeof out_matrixFloating_values / sizeof out_matrixFloating_values[0]),
            out_matrixFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cubeShadowMapCreate(
    JNIEnv* environment, jclass declaring_class, jlong graphics_device, jint quality, jlongArray out_shadow_map)
{
    (void)environment;
    (void)declaring_class;
    CNA_CubeShadowMapHandle out_shadow_map_value = 0;
    CNA_Result call_result = cna.cube_shadow_map_create((CNA_Handle)graphics_device, (CNA_ShadowQuality)quality, &out_shadow_map_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_shadow_map_element = (jlong)out_shadow_map_value;
        (*environment)->SetLongArrayRegion(
            environment, out_shadow_map, 0, 1, &out_shadow_map_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cubeShadowMapDestroy(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.cube_shadow_map_destroy((CNA_CubeShadowMapHandle)shadow_map);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cubeShadowMapEnd(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.cube_shadow_map_end((CNA_CubeShadowMapHandle)shadow_map);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cubeShadowMapGetDepthBias(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jfloatArray out_bias)
{
    (void)environment;
    (void)declaring_class;
    float out_bias_value = 0;
    CNA_Result call_result = cna.cube_shadow_map_get_depth_bias((CNA_CubeShadowMapHandle)shadow_map, &out_bias_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_bias_element = (jfloat)out_bias_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_bias, 0, 1, &out_bias_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cubeShadowMapGetLightPosition(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jfloatArray out_positionFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Vector3 out_position_value;
    memset(&out_position_value, 0, sizeof out_position_value);
    CNA_Result call_result = cna.cube_shadow_map_get_light_position((CNA_CubeShadowMapHandle)shadow_map, &out_position_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_positionFloating_values[] = {
            (jfloat)out_position_value.x,
            (jfloat)out_position_value.y,
            (jfloat)out_position_value.z
        };
        (*environment)->SetFloatArrayRegion(environment, out_positionFloating, 0,
            (jsize)(sizeof out_positionFloating_values / sizeof out_positionFloating_values[0]),
            out_positionFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cubeShadowMapGetLightRange(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jfloatArray out_range)
{
    (void)environment;
    (void)declaring_class;
    float out_range_value = 0;
    CNA_Result call_result = cna.cube_shadow_map_get_light_range((CNA_CubeShadowMapHandle)shadow_map, &out_range_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_range_element = (jfloat)out_range_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_range, 0, 1, &out_range_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cubeShadowMapGetQuality(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jintArray out_quality)
{
    (void)environment;
    (void)declaring_class;
    CNA_ShadowQuality out_quality_value = 0;
    CNA_Result call_result = cna.cube_shadow_map_get_quality((CNA_CubeShadowMapHandle)shadow_map, &out_quality_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_quality_element = (jint)out_quality_value;
        (*environment)->SetIntArrayRegion(
            environment, out_quality, 0, 1, &out_quality_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cubeShadowMapGetShadowTexture(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jlongArray out_texture)
{
    (void)environment;
    (void)declaring_class;
    CNA_Handle out_texture_value = 0;
    CNA_Result call_result = cna.cube_shadow_map_get_shadow_texture((CNA_CubeShadowMapHandle)shadow_map, &out_texture_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_texture_element = (jlong)out_texture_value;
        (*environment)->SetLongArrayRegion(
            environment, out_texture, 0, 1, &out_texture_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cubeShadowMapGetSize(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jintArray out_size)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_size_value = 0;
    CNA_Result call_result = cna.cube_shadow_map_get_size((CNA_CubeShadowMapHandle)shadow_map, &out_size_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_size_element = (jint)out_size_value;
        (*environment)->SetIntArrayRegion(
            environment, out_size, 0, 1, &out_size_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cubeShadowMapIsSupported(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jbooleanArray out_supported)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_supported_value = 0;
    CNA_Result call_result = cna.cube_shadow_map_is_supported((CNA_CubeShadowMapHandle)shadow_map, &out_supported_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_supported_element = (jboolean)out_supported_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_supported, 0, 1, &out_supported_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cubeShadowMapSetDepthBias(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jfloat bias)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.cube_shadow_map_set_depth_bias((CNA_CubeShadowMapHandle)shadow_map, (float)bias);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cubeShadowMapSizeForQuality(
    JNIEnv* environment, jclass declaring_class, jint quality, jintArray out_size)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_size_value = 0;
    CNA_Result call_result = cna.cube_shadow_map_size_for_quality((CNA_ShadowQuality)quality, &out_size_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_size_element = (jint)out_size_value;
        (*environment)->SetIntArrayRegion(
            environment, out_size, 0, 1, &out_size_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_cubeShadowMapUpdate(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jbyteArray lightBytes, jlongArray lightIntegral, jfloatArray lightFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_PointLightEXT light_value;
    memset(&light_value, 0, sizeof light_value);
    light_value.struct_size = (uint32_t)(sizeof light_value);
    light_value.struct_version = UINT32_C(1);
    {
        jbyte lightBytes_values[3];
        (*environment)->GetByteArrayRegion(environment, lightBytes, 0, 3, lightBytes_values);
        light_value.reserved[0] = (uint8_t)lightBytes_values[0];
        light_value.reserved[1] = (uint8_t)lightBytes_values[1];
        light_value.reserved[2] = (uint8_t)lightBytes_values[2];
    }
    {
        jlong lightIntegral_values[1];
        (*environment)->GetLongArrayRegion(environment, lightIntegral, 0, 1, lightIntegral_values);
        light_value.casts_shadows = (CNA_Bool)lightIntegral_values[0];
    }
    {
        jfloat lightFloating_values[8];
        (*environment)->GetFloatArrayRegion(environment, lightFloating, 0, 8, lightFloating_values);
        light_value.position.x = (float)lightFloating_values[0];
        light_value.position.y = (float)lightFloating_values[1];
        light_value.position.z = (float)lightFloating_values[2];
        light_value.color.x = (float)lightFloating_values[3];
        light_value.color.y = (float)lightFloating_values[4];
        light_value.color.z = (float)lightFloating_values[5];
        light_value.intensity = (float)lightFloating_values[6];
        light_value.range = (float)lightFloating_values[7];
    }
    CNA_Result call_result = cna.cube_shadow_map_update((CNA_CubeShadowMapHandle)shadow_map, &light_value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_debugDrawAddBoundingSphere(
    JNIEnv* environment, jclass declaring_class, jlong debug, jfloatArray sphereFloating, jlongArray colourIntegral, jint segments)
{
    (void)environment;
    (void)declaring_class;
    CNA_BoundingSphere sphere_value;
    memset(&sphere_value, 0, sizeof sphere_value);
    {
        jfloat sphereFloating_values[4];
        (*environment)->GetFloatArrayRegion(environment, sphereFloating, 0, 4, sphereFloating_values);
        sphere_value.center.x = (float)sphereFloating_values[0];
        sphere_value.center.y = (float)sphereFloating_values[1];
        sphere_value.center.z = (float)sphereFloating_values[2];
        sphere_value.radius = (float)sphereFloating_values[3];
    }
    CNA_Color colour_value;
    memset(&colour_value, 0, sizeof colour_value);
    {
        jlong colourIntegral_values[4];
        (*environment)->GetLongArrayRegion(environment, colourIntegral, 0, 4, colourIntegral_values);
        colour_value.r = (uint8_t)colourIntegral_values[0];
        colour_value.g = (uint8_t)colourIntegral_values[1];
        colour_value.b = (uint8_t)colourIntegral_values[2];
        colour_value.a = (uint8_t)colourIntegral_values[3];
    }
    CNA_Result call_result = cna.debug_draw_add_bounding_sphere((CNA_DebugDrawHandle)debug, &sphere_value, colour_value, (int32_t)segments);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_debugDrawAddBox(
    JNIEnv* environment, jclass declaring_class, jlong debug, jfloatArray boundsFloating, jlongArray colourIntegral)
{
    (void)environment;
    (void)declaring_class;
    CNA_BoundingBox bounds_value;
    memset(&bounds_value, 0, sizeof bounds_value);
    {
        jfloat boundsFloating_values[6];
        (*environment)->GetFloatArrayRegion(environment, boundsFloating, 0, 6, boundsFloating_values);
        bounds_value.min.x = (float)boundsFloating_values[0];
        bounds_value.min.y = (float)boundsFloating_values[1];
        bounds_value.min.z = (float)boundsFloating_values[2];
        bounds_value.max.x = (float)boundsFloating_values[3];
        bounds_value.max.y = (float)boundsFloating_values[4];
        bounds_value.max.z = (float)boundsFloating_values[5];
    }
    CNA_Color colour_value;
    memset(&colour_value, 0, sizeof colour_value);
    {
        jlong colourIntegral_values[4];
        (*environment)->GetLongArrayRegion(environment, colourIntegral, 0, 4, colourIntegral_values);
        colour_value.r = (uint8_t)colourIntegral_values[0];
        colour_value.g = (uint8_t)colourIntegral_values[1];
        colour_value.b = (uint8_t)colourIntegral_values[2];
        colour_value.a = (uint8_t)colourIntegral_values[3];
    }
    CNA_Result call_result = cna.debug_draw_add_box((CNA_DebugDrawHandle)debug, &bounds_value, colour_value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_debugDrawAddCascadeGizmo(
    JNIEnv* environment, jclass declaring_class, jlong debug, jlong cascades, jlongArray colourIntegral)
{
    (void)environment;
    (void)declaring_class;
    CNA_Color colour_value;
    memset(&colour_value, 0, sizeof colour_value);
    {
        jlong colourIntegral_values[4];
        (*environment)->GetLongArrayRegion(environment, colourIntegral, 0, 4, colourIntegral_values);
        colour_value.r = (uint8_t)colourIntegral_values[0];
        colour_value.g = (uint8_t)colourIntegral_values[1];
        colour_value.b = (uint8_t)colourIntegral_values[2];
        colour_value.a = (uint8_t)colourIntegral_values[3];
    }
    CNA_Result call_result = cna.debug_draw_add_cascade_gizmo((CNA_DebugDrawHandle)debug, (CNA_CascadedShadowMapHandle)cascades, colour_value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_debugDrawAddClusterSliceGizmo(
    JNIEnv* environment, jclass declaring_class, jlong debug, jlong grid, jfloatArray inverse_viewFloating, jlongArray colourIntegral)
{
    (void)environment;
    (void)declaring_class;
    CNA_Matrix inverse_view_value;
    memset(&inverse_view_value, 0, sizeof inverse_view_value);
    {
        jfloat inverse_viewFloating_values[16];
        (*environment)->GetFloatArrayRegion(environment, inverse_viewFloating, 0, 16, inverse_viewFloating_values);
        inverse_view_value.m11 = (float)inverse_viewFloating_values[0];
        inverse_view_value.m12 = (float)inverse_viewFloating_values[1];
        inverse_view_value.m13 = (float)inverse_viewFloating_values[2];
        inverse_view_value.m14 = (float)inverse_viewFloating_values[3];
        inverse_view_value.m21 = (float)inverse_viewFloating_values[4];
        inverse_view_value.m22 = (float)inverse_viewFloating_values[5];
        inverse_view_value.m23 = (float)inverse_viewFloating_values[6];
        inverse_view_value.m24 = (float)inverse_viewFloating_values[7];
        inverse_view_value.m31 = (float)inverse_viewFloating_values[8];
        inverse_view_value.m32 = (float)inverse_viewFloating_values[9];
        inverse_view_value.m33 = (float)inverse_viewFloating_values[10];
        inverse_view_value.m34 = (float)inverse_viewFloating_values[11];
        inverse_view_value.m41 = (float)inverse_viewFloating_values[12];
        inverse_view_value.m42 = (float)inverse_viewFloating_values[13];
        inverse_view_value.m43 = (float)inverse_viewFloating_values[14];
        inverse_view_value.m44 = (float)inverse_viewFloating_values[15];
    }
    CNA_Color colour_value;
    memset(&colour_value, 0, sizeof colour_value);
    {
        jlong colourIntegral_values[4];
        (*environment)->GetLongArrayRegion(environment, colourIntegral, 0, 4, colourIntegral_values);
        colour_value.r = (uint8_t)colourIntegral_values[0];
        colour_value.g = (uint8_t)colourIntegral_values[1];
        colour_value.b = (uint8_t)colourIntegral_values[2];
        colour_value.a = (uint8_t)colourIntegral_values[3];
    }
    CNA_Result call_result = cna.debug_draw_add_cluster_slice_gizmo((CNA_DebugDrawHandle)debug, (CNA_ClusteredLightGridHandle)grid, &inverse_view_value, colour_value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_debugDrawAddCross(
    JNIEnv* environment, jclass declaring_class, jlong debug, jfloatArray positionFloating, jfloat size, jlongArray colourIntegral)
{
    (void)environment;
    (void)declaring_class;
    CNA_Vector3 position_value;
    memset(&position_value, 0, sizeof position_value);
    {
        jfloat positionFloating_values[3];
        (*environment)->GetFloatArrayRegion(environment, positionFloating, 0, 3, positionFloating_values);
        position_value.x = (float)positionFloating_values[0];
        position_value.y = (float)positionFloating_values[1];
        position_value.z = (float)positionFloating_values[2];
    }
    CNA_Color colour_value;
    memset(&colour_value, 0, sizeof colour_value);
    {
        jlong colourIntegral_values[4];
        (*environment)->GetLongArrayRegion(environment, colourIntegral, 0, 4, colourIntegral_values);
        colour_value.r = (uint8_t)colourIntegral_values[0];
        colour_value.g = (uint8_t)colourIntegral_values[1];
        colour_value.b = (uint8_t)colourIntegral_values[2];
        colour_value.a = (uint8_t)colourIntegral_values[3];
    }
    CNA_Result call_result = cna.debug_draw_add_cross((CNA_DebugDrawHandle)debug, &position_value, (float)size, colour_value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_debugDrawAddDirectionalLightGizmo(
    JNIEnv* environment, jclass declaring_class, jlong debug, jbyteArray lightBytes, jlongArray lightIntegral, jfloatArray lightFloating, jfloatArray atFloating, jfloat length, jlongArray colourIntegral)
{
    (void)environment;
    (void)declaring_class;
    CNA_DirectionalLightEXT light_value;
    memset(&light_value, 0, sizeof light_value);
    light_value.struct_size = (uint32_t)(sizeof light_value);
    light_value.struct_version = UINT32_C(1);
    {
        jbyte lightBytes_values[3];
        (*environment)->GetByteArrayRegion(environment, lightBytes, 0, 3, lightBytes_values);
        light_value.reserved[0] = (uint8_t)lightBytes_values[0];
        light_value.reserved[1] = (uint8_t)lightBytes_values[1];
        light_value.reserved[2] = (uint8_t)lightBytes_values[2];
    }
    {
        jlong lightIntegral_values[1];
        (*environment)->GetLongArrayRegion(environment, lightIntegral, 0, 1, lightIntegral_values);
        light_value.casts_shadows = (CNA_Bool)lightIntegral_values[0];
    }
    {
        jfloat lightFloating_values[7];
        (*environment)->GetFloatArrayRegion(environment, lightFloating, 0, 7, lightFloating_values);
        light_value.direction.x = (float)lightFloating_values[0];
        light_value.direction.y = (float)lightFloating_values[1];
        light_value.direction.z = (float)lightFloating_values[2];
        light_value.color.x = (float)lightFloating_values[3];
        light_value.color.y = (float)lightFloating_values[4];
        light_value.color.z = (float)lightFloating_values[5];
        light_value.intensity = (float)lightFloating_values[6];
    }
    CNA_Vector3 at_value;
    memset(&at_value, 0, sizeof at_value);
    {
        jfloat atFloating_values[3];
        (*environment)->GetFloatArrayRegion(environment, atFloating, 0, 3, atFloating_values);
        at_value.x = (float)atFloating_values[0];
        at_value.y = (float)atFloating_values[1];
        at_value.z = (float)atFloating_values[2];
    }
    CNA_Color colour_value;
    memset(&colour_value, 0, sizeof colour_value);
    {
        jlong colourIntegral_values[4];
        (*environment)->GetLongArrayRegion(environment, colourIntegral, 0, 4, colourIntegral_values);
        colour_value.r = (uint8_t)colourIntegral_values[0];
        colour_value.g = (uint8_t)colourIntegral_values[1];
        colour_value.b = (uint8_t)colourIntegral_values[2];
        colour_value.a = (uint8_t)colourIntegral_values[3];
    }
    CNA_Result call_result = cna.debug_draw_add_directional_light_gizmo((CNA_DebugDrawHandle)debug, &light_value, &at_value, (float)length, colour_value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_debugDrawAddFrustum(
    JNIEnv* environment, jclass declaring_class, jlong debug, jfloatArray frustumFloating, jlongArray colourIntegral)
{
    (void)environment;
    (void)declaring_class;
    CNA_BoundingFrustum frustum_value;
    memset(&frustum_value, 0, sizeof frustum_value);
    {
        jfloat frustumFloating_values[16];
        (*environment)->GetFloatArrayRegion(environment, frustumFloating, 0, 16, frustumFloating_values);
        frustum_value.matrix.m11 = (float)frustumFloating_values[0];
        frustum_value.matrix.m12 = (float)frustumFloating_values[1];
        frustum_value.matrix.m13 = (float)frustumFloating_values[2];
        frustum_value.matrix.m14 = (float)frustumFloating_values[3];
        frustum_value.matrix.m21 = (float)frustumFloating_values[4];
        frustum_value.matrix.m22 = (float)frustumFloating_values[5];
        frustum_value.matrix.m23 = (float)frustumFloating_values[6];
        frustum_value.matrix.m24 = (float)frustumFloating_values[7];
        frustum_value.matrix.m31 = (float)frustumFloating_values[8];
        frustum_value.matrix.m32 = (float)frustumFloating_values[9];
        frustum_value.matrix.m33 = (float)frustumFloating_values[10];
        frustum_value.matrix.m34 = (float)frustumFloating_values[11];
        frustum_value.matrix.m41 = (float)frustumFloating_values[12];
        frustum_value.matrix.m42 = (float)frustumFloating_values[13];
        frustum_value.matrix.m43 = (float)frustumFloating_values[14];
        frustum_value.matrix.m44 = (float)frustumFloating_values[15];
    }
    CNA_Color colour_value;
    memset(&colour_value, 0, sizeof colour_value);
    {
        jlong colourIntegral_values[4];
        (*environment)->GetLongArrayRegion(environment, colourIntegral, 0, 4, colourIntegral_values);
        colour_value.r = (uint8_t)colourIntegral_values[0];
        colour_value.g = (uint8_t)colourIntegral_values[1];
        colour_value.b = (uint8_t)colourIntegral_values[2];
        colour_value.a = (uint8_t)colourIntegral_values[3];
    }
    CNA_Result call_result = cna.debug_draw_add_frustum((CNA_DebugDrawHandle)debug, frustum_value, colour_value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_debugDrawAddLine(
    JNIEnv* environment, jclass declaring_class, jlong debug, jfloatArray fromFloating, jfloatArray toFloating, jlongArray colourIntegral)
{
    (void)environment;
    (void)declaring_class;
    CNA_Vector3 from_value;
    memset(&from_value, 0, sizeof from_value);
    {
        jfloat fromFloating_values[3];
        (*environment)->GetFloatArrayRegion(environment, fromFloating, 0, 3, fromFloating_values);
        from_value.x = (float)fromFloating_values[0];
        from_value.y = (float)fromFloating_values[1];
        from_value.z = (float)fromFloating_values[2];
    }
    CNA_Vector3 to_value;
    memset(&to_value, 0, sizeof to_value);
    {
        jfloat toFloating_values[3];
        (*environment)->GetFloatArrayRegion(environment, toFloating, 0, 3, toFloating_values);
        to_value.x = (float)toFloating_values[0];
        to_value.y = (float)toFloating_values[1];
        to_value.z = (float)toFloating_values[2];
    }
    CNA_Color colour_value;
    memset(&colour_value, 0, sizeof colour_value);
    {
        jlong colourIntegral_values[4];
        (*environment)->GetLongArrayRegion(environment, colourIntegral, 0, 4, colourIntegral_values);
        colour_value.r = (uint8_t)colourIntegral_values[0];
        colour_value.g = (uint8_t)colourIntegral_values[1];
        colour_value.b = (uint8_t)colourIntegral_values[2];
        colour_value.a = (uint8_t)colourIntegral_values[3];
    }
    CNA_Result call_result = cna.debug_draw_add_line((CNA_DebugDrawHandle)debug, &from_value, &to_value, colour_value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_debugDrawAddPointLightGizmo(
    JNIEnv* environment, jclass declaring_class, jlong debug, jbyteArray lightBytes, jlongArray lightIntegral, jfloatArray lightFloating, jlongArray colourIntegral)
{
    (void)environment;
    (void)declaring_class;
    CNA_PointLightEXT light_value;
    memset(&light_value, 0, sizeof light_value);
    light_value.struct_size = (uint32_t)(sizeof light_value);
    light_value.struct_version = UINT32_C(1);
    {
        jbyte lightBytes_values[3];
        (*environment)->GetByteArrayRegion(environment, lightBytes, 0, 3, lightBytes_values);
        light_value.reserved[0] = (uint8_t)lightBytes_values[0];
        light_value.reserved[1] = (uint8_t)lightBytes_values[1];
        light_value.reserved[2] = (uint8_t)lightBytes_values[2];
    }
    {
        jlong lightIntegral_values[1];
        (*environment)->GetLongArrayRegion(environment, lightIntegral, 0, 1, lightIntegral_values);
        light_value.casts_shadows = (CNA_Bool)lightIntegral_values[0];
    }
    {
        jfloat lightFloating_values[8];
        (*environment)->GetFloatArrayRegion(environment, lightFloating, 0, 8, lightFloating_values);
        light_value.position.x = (float)lightFloating_values[0];
        light_value.position.y = (float)lightFloating_values[1];
        light_value.position.z = (float)lightFloating_values[2];
        light_value.color.x = (float)lightFloating_values[3];
        light_value.color.y = (float)lightFloating_values[4];
        light_value.color.z = (float)lightFloating_values[5];
        light_value.intensity = (float)lightFloating_values[6];
        light_value.range = (float)lightFloating_values[7];
    }
    CNA_Color colour_value;
    memset(&colour_value, 0, sizeof colour_value);
    {
        jlong colourIntegral_values[4];
        (*environment)->GetLongArrayRegion(environment, colourIntegral, 0, 4, colourIntegral_values);
        colour_value.r = (uint8_t)colourIntegral_values[0];
        colour_value.g = (uint8_t)colourIntegral_values[1];
        colour_value.b = (uint8_t)colourIntegral_values[2];
        colour_value.a = (uint8_t)colourIntegral_values[3];
    }
    CNA_Result call_result = cna.debug_draw_add_point_light_gizmo((CNA_DebugDrawHandle)debug, &light_value, colour_value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_debugDrawAddProbeVolumeGizmo(
    JNIEnv* environment, jclass declaring_class, jlong debug, jlong volume, jlongArray colourIntegral, jfloat cross_size)
{
    (void)environment;
    (void)declaring_class;
    CNA_Color colour_value;
    memset(&colour_value, 0, sizeof colour_value);
    {
        jlong colourIntegral_values[4];
        (*environment)->GetLongArrayRegion(environment, colourIntegral, 0, 4, colourIntegral_values);
        colour_value.r = (uint8_t)colourIntegral_values[0];
        colour_value.g = (uint8_t)colourIntegral_values[1];
        colour_value.b = (uint8_t)colourIntegral_values[2];
        colour_value.a = (uint8_t)colourIntegral_values[3];
    }
    CNA_Result call_result = cna.debug_draw_add_probe_volume_gizmo((CNA_DebugDrawHandle)debug, (CNA_LightProbeVolumeHandle)volume, colour_value, (float)cross_size);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_debugDrawAddSphere(
    JNIEnv* environment, jclass declaring_class, jlong debug, jfloatArray centreFloating, jfloat radius, jlongArray colourIntegral, jint segments)
{
    (void)environment;
    (void)declaring_class;
    CNA_Vector3 centre_value;
    memset(&centre_value, 0, sizeof centre_value);
    {
        jfloat centreFloating_values[3];
        (*environment)->GetFloatArrayRegion(environment, centreFloating, 0, 3, centreFloating_values);
        centre_value.x = (float)centreFloating_values[0];
        centre_value.y = (float)centreFloating_values[1];
        centre_value.z = (float)centreFloating_values[2];
    }
    CNA_Color colour_value;
    memset(&colour_value, 0, sizeof colour_value);
    {
        jlong colourIntegral_values[4];
        (*environment)->GetLongArrayRegion(environment, colourIntegral, 0, 4, colourIntegral_values);
        colour_value.r = (uint8_t)colourIntegral_values[0];
        colour_value.g = (uint8_t)colourIntegral_values[1];
        colour_value.b = (uint8_t)colourIntegral_values[2];
        colour_value.a = (uint8_t)colourIntegral_values[3];
    }
    CNA_Result call_result = cna.debug_draw_add_sphere((CNA_DebugDrawHandle)debug, &centre_value, (float)radius, colour_value, (int32_t)segments);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_debugDrawAddSpotLightGizmo(
    JNIEnv* environment, jclass declaring_class, jlong debug, jbyteArray lightBytes, jlongArray lightIntegral, jfloatArray lightFloating, jlongArray colourIntegral, jint segments)
{
    (void)environment;
    (void)declaring_class;
    CNA_SpotLightEXT light_value;
    memset(&light_value, 0, sizeof light_value);
    light_value.struct_size = (uint32_t)(sizeof light_value);
    light_value.struct_version = UINT32_C(1);
    {
        jbyte lightBytes_values[3];
        (*environment)->GetByteArrayRegion(environment, lightBytes, 0, 3, lightBytes_values);
        light_value.reserved[0] = (uint8_t)lightBytes_values[0];
        light_value.reserved[1] = (uint8_t)lightBytes_values[1];
        light_value.reserved[2] = (uint8_t)lightBytes_values[2];
    }
    {
        jlong lightIntegral_values[1];
        (*environment)->GetLongArrayRegion(environment, lightIntegral, 0, 1, lightIntegral_values);
        light_value.casts_shadows = (CNA_Bool)lightIntegral_values[0];
    }
    {
        jfloat lightFloating_values[13];
        (*environment)->GetFloatArrayRegion(environment, lightFloating, 0, 13, lightFloating_values);
        light_value.position.x = (float)lightFloating_values[0];
        light_value.position.y = (float)lightFloating_values[1];
        light_value.position.z = (float)lightFloating_values[2];
        light_value.direction.x = (float)lightFloating_values[3];
        light_value.direction.y = (float)lightFloating_values[4];
        light_value.direction.z = (float)lightFloating_values[5];
        light_value.color.x = (float)lightFloating_values[6];
        light_value.color.y = (float)lightFloating_values[7];
        light_value.color.z = (float)lightFloating_values[8];
        light_value.intensity = (float)lightFloating_values[9];
        light_value.range = (float)lightFloating_values[10];
        light_value.inner_angle = (float)lightFloating_values[11];
        light_value.outer_angle = (float)lightFloating_values[12];
    }
    CNA_Color colour_value;
    memset(&colour_value, 0, sizeof colour_value);
    {
        jlong colourIntegral_values[4];
        (*environment)->GetLongArrayRegion(environment, colourIntegral, 0, 4, colourIntegral_values);
        colour_value.r = (uint8_t)colourIntegral_values[0];
        colour_value.g = (uint8_t)colourIntegral_values[1];
        colour_value.b = (uint8_t)colourIntegral_values[2];
        colour_value.a = (uint8_t)colourIntegral_values[3];
    }
    CNA_Result call_result = cna.debug_draw_add_spot_light_gizmo((CNA_DebugDrawHandle)debug, &light_value, colour_value, (int32_t)segments);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_debugDrawBegin(
    JNIEnv* environment, jclass declaring_class, jlong debug, jfloatArray viewFloating, jfloatArray projectionFloating)
{
    (void)environment;
    (void)declaring_class;
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
    CNA_Result call_result = cna.debug_draw_begin((CNA_DebugDrawHandle)debug, &view_value, &projection_value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_debugDrawClear(
    JNIEnv* environment, jclass declaring_class, jlong debug)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.debug_draw_clear((CNA_DebugDrawHandle)debug);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_debugDrawCopyVertices(
    JNIEnv* environment, jclass declaring_class, jlong debug, jboolean depth_tested, jlongArray destinationIntegral, jfloatArray destinationFloating, jlongArray out_count)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_count = (*environment)->GetArrayLength(environment, destinationIntegral) / 4;
    if ((*environment)->GetArrayLength(environment, destinationIntegral) != destination_count * 4 ||
        (*environment)->GetArrayLength(environment, destinationFloating) != destination_count * 3) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_VertexPositionColor* destination_values = (CNA_VertexPositionColor*)calloc(
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
            destination_values[element].color.r = (uint8_t)destinationIntegral_values[element * 4 + 0];
            destination_values[element].color.g = (uint8_t)destinationIntegral_values[element * 4 + 1];
            destination_values[element].color.b = (uint8_t)destinationIntegral_values[element * 4 + 2];
            destination_values[element].color.a = (uint8_t)destinationIntegral_values[element * 4 + 3];
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
            destination_values[element].position.x = (float)destinationFloating_values[element * 3 + 0];
            destination_values[element].position.y = (float)destinationFloating_values[element * 3 + 1];
            destination_values[element].position.z = (float)destinationFloating_values[element * 3 + 2];
        }
        free(destinationFloating_values);
    }
    uint64_t out_count_value = 0;
    CNA_Result call_result = cna.debug_draw_copy_vertices((CNA_DebugDrawHandle)debug, (CNA_Bool)depth_tested, destination_values, (uint64_t)destination_count, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS) {
        {
            jlong* destinationIntegral_out = (jlong*)malloc(
                ((size_t)destination_count * 4 + 1U) * sizeof(*destinationIntegral_out));
            if (destinationIntegral_out != NULL) {
                for (jsize element = 0; element < destination_count; ++element) {
                    destinationIntegral_out[element * 4 + 0] = (jlong)destination_values[element].color.r;
                    destinationIntegral_out[element * 4 + 1] = (jlong)destination_values[element].color.g;
                    destinationIntegral_out[element * 4 + 2] = (jlong)destination_values[element].color.b;
                    destinationIntegral_out[element * 4 + 3] = (jlong)destination_values[element].color.a;
                }
                (*environment)->SetLongArrayRegion(
                    environment, destinationIntegral, 0,
                    (jsize)(destination_count * 4), destinationIntegral_out);
                free(destinationIntegral_out);
            }
        }
        {
            jfloat* destinationFloating_out = (jfloat*)malloc(
                ((size_t)destination_count * 3 + 1U) * sizeof(*destinationFloating_out));
            if (destinationFloating_out != NULL) {
                for (jsize element = 0; element < destination_count; ++element) {
                    destinationFloating_out[element * 3 + 0] = (jfloat)destination_values[element].position.x;
                    destinationFloating_out[element * 3 + 1] = (jfloat)destination_values[element].position.y;
                    destinationFloating_out[element * 3 + 2] = (jfloat)destination_values[element].position.z;
                }
                (*environment)->SetFloatArrayRegion(
                    environment, destinationFloating, 0,
                    (jsize)(destination_count * 3), destinationFloating_out);
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
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_debugDrawCreate(
    JNIEnv* environment, jclass declaring_class, jlong graphics_device, jlongArray out_debug)
{
    (void)environment;
    (void)declaring_class;
    CNA_DebugDrawHandle out_debug_value = 0;
    CNA_Result call_result = cna.debug_draw_create((CNA_Handle)graphics_device, &out_debug_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_debug_element = (jlong)out_debug_value;
        (*environment)->SetLongArrayRegion(
            environment, out_debug, 0, 1, &out_debug_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_debugDrawDestroy(
    JNIEnv* environment, jclass declaring_class, jlong debug)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.debug_draw_destroy((CNA_DebugDrawHandle)debug);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_debugDrawEnd(
    JNIEnv* environment, jclass declaring_class, jlong debug)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.debug_draw_end((CNA_DebugDrawHandle)debug);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_debugDrawGetLineCount(
    JNIEnv* environment, jclass declaring_class, jlong debug, jintArray out_count)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_count_value = 0;
    CNA_Result call_result = cna.debug_draw_get_line_count((CNA_DebugDrawHandle)debug, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_count_element = (jint)out_count_value;
        (*environment)->SetIntArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_debugDrawIsDepthTested(
    JNIEnv* environment, jclass declaring_class, jlong debug, jbooleanArray out_depth_tested)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_depth_tested_value = 0;
    CNA_Result call_result = cna.debug_draw_is_depth_tested((CNA_DebugDrawHandle)debug, &out_depth_tested_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_depth_tested_element = (jboolean)out_depth_tested_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_depth_tested, 0, 1, &out_depth_tested_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_debugDrawSetDepthTested(
    JNIEnv* environment, jclass declaring_class, jlong debug, jboolean depth_tested)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.debug_draw_set_depth_tested((CNA_DebugDrawHandle)debug, (CNA_Bool)depth_tested);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_decalPassCreate(
    JNIEnv* environment, jclass declaring_class, jlong graphics_device, jlongArray out_pass)
{
    (void)environment;
    (void)declaring_class;
    CNA_DecalPassHandle out_pass_value = 0;
    CNA_Result call_result = cna.decal_pass_create((CNA_Handle)graphics_device, &out_pass_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_pass_element = (jlong)out_pass_value;
        (*environment)->SetLongArrayRegion(
            environment, out_pass, 0, 1, &out_pass_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_decalPassDestroy(
    JNIEnv* environment, jclass declaring_class, jlong pass)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.decal_pass_destroy((CNA_DecalPassHandle)pass);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_decalPassDraw(
    JNIEnv* environment, jclass declaring_class, jlong pass, jlong decal, jfloatArray decal_worldFloating, jint width, jint height)
{
    (void)environment;
    (void)declaring_class;
    CNA_Matrix decal_world_value;
    memset(&decal_world_value, 0, sizeof decal_world_value);
    {
        jfloat decal_worldFloating_values[16];
        (*environment)->GetFloatArrayRegion(environment, decal_worldFloating, 0, 16, decal_worldFloating_values);
        decal_world_value.m11 = (float)decal_worldFloating_values[0];
        decal_world_value.m12 = (float)decal_worldFloating_values[1];
        decal_world_value.m13 = (float)decal_worldFloating_values[2];
        decal_world_value.m14 = (float)decal_worldFloating_values[3];
        decal_world_value.m21 = (float)decal_worldFloating_values[4];
        decal_world_value.m22 = (float)decal_worldFloating_values[5];
        decal_world_value.m23 = (float)decal_worldFloating_values[6];
        decal_world_value.m24 = (float)decal_worldFloating_values[7];
        decal_world_value.m31 = (float)decal_worldFloating_values[8];
        decal_world_value.m32 = (float)decal_worldFloating_values[9];
        decal_world_value.m33 = (float)decal_worldFloating_values[10];
        decal_world_value.m34 = (float)decal_worldFloating_values[11];
        decal_world_value.m41 = (float)decal_worldFloating_values[12];
        decal_world_value.m42 = (float)decal_worldFloating_values[13];
        decal_world_value.m43 = (float)decal_worldFloating_values[14];
        decal_world_value.m44 = (float)decal_worldFloating_values[15];
    }
    CNA_Result call_result = cna.decal_pass_draw((CNA_DecalPassHandle)pass, (CNA_Handle)decal, &decal_world_value, (int32_t)width, (int32_t)height);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_decalPassGetMaxSlopeAngle(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.decal_pass_get_max_slope_angle((CNA_DecalPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_decalPassGetOpacity(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.decal_pass_get_opacity((CNA_DecalPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_decalPassGetTint(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_valueFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Vector3 out_value_value;
    memset(&out_value_value, 0, sizeof out_value_value);
    CNA_Result call_result = cna.decal_pass_get_tint((CNA_DecalPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_valueFloating_values[] = {
            (jfloat)out_value_value.x,
            (jfloat)out_value_value.y,
            (jfloat)out_value_value.z
        };
        (*environment)->SetFloatArrayRegion(environment, out_valueFloating, 0,
            (jsize)(sizeof out_valueFloating_values / sizeof out_valueFloating_values[0]),
            out_valueFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_decalPassIsInsideDecalBox(
    JNIEnv* environment, jclass declaring_class, jfloatArray decal_local_positionFloating, jbooleanArray out_inside)
{
    (void)environment;
    (void)declaring_class;
    CNA_Vector3 decal_local_position_value;
    memset(&decal_local_position_value, 0, sizeof decal_local_position_value);
    {
        jfloat decal_local_positionFloating_values[3];
        (*environment)->GetFloatArrayRegion(environment, decal_local_positionFloating, 0, 3, decal_local_positionFloating_values);
        decal_local_position_value.x = (float)decal_local_positionFloating_values[0];
        decal_local_position_value.y = (float)decal_local_positionFloating_values[1];
        decal_local_position_value.z = (float)decal_local_positionFloating_values[2];
    }
    CNA_Bool out_inside_value = 0;
    CNA_Result call_result = cna.decal_pass_is_inside_decal_box(&decal_local_position_value, &out_inside_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_inside_element = (jboolean)out_inside_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_inside, 0, 1, &out_inside_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_decalPassSetCamera(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray viewFloating, jfloatArray projectionFloating, jfloat far_plane)
{
    (void)environment;
    (void)declaring_class;
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
    CNA_Result call_result = cna.decal_pass_set_camera((CNA_DecalPassHandle)pass, &view_value, &projection_value, (float)far_plane);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_decalPassSetMaxSlopeAngle(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.decal_pass_set_max_slope_angle((CNA_DecalPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_decalPassSetOpacity(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.decal_pass_set_opacity((CNA_DecalPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_decalPassSetPrepassInputs(
    JNIEnv* environment, jclass declaring_class, jlong pass, jlong depth, jlong normals)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.decal_pass_set_prepass_inputs((CNA_DecalPassHandle)pass, (CNA_Handle)depth, (CNA_Handle)normals);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_decalPassSetTint(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray valueFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Vector3 value_value;
    memset(&value_value, 0, sizeof value_value);
    {
        jfloat valueFloating_values[3];
        (*environment)->GetFloatArrayRegion(environment, valueFloating, 0, 3, valueFloating_values);
        value_value.x = (float)valueFloating_values[0];
        value_value.y = (float)valueFloating_values[1];
        value_value.z = (float)valueFloating_values[2];
    }
    CNA_Result call_result = cna.decal_pass_set_tint((CNA_DecalPassHandle)pass, &value_value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_depthOfFieldPassCircleOfConfusionMillimetres(
    JNIEnv* environment, jclass declaring_class, jfloat depth, jfloat focus_distance, jfloat focal_length, jfloat f_number, jfloatArray out_millimetres)
{
    (void)environment;
    (void)declaring_class;
    float out_millimetres_value = 0;
    CNA_Result call_result = cna.depth_of_field_pass_circle_of_confusion_millimetres((float)depth, (float)focus_distance, (float)focal_length, (float)f_number, &out_millimetres_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_millimetres_element = (jfloat)out_millimetres_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_millimetres, 0, 1, &out_millimetres_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_depthOfFieldPassCreate(
    JNIEnv* environment, jclass declaring_class, jlong graphics_device, jlongArray out_pass)
{
    (void)environment;
    (void)declaring_class;
    CNA_PostProcessPassHandle out_pass_value = 0;
    CNA_Result call_result = cna.depth_of_field_pass_create((CNA_Handle)graphics_device, &out_pass_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_pass_element = (jlong)out_pass_value;
        (*environment)->SetLongArrayRegion(
            environment, out_pass, 0, 1, &out_pass_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_depthOfFieldPassGetFNumber(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.depth_of_field_pass_get_f_number((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_depthOfFieldPassGetFocalLength(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.depth_of_field_pass_get_focal_length((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_depthOfFieldPassGetFocusDistance(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.depth_of_field_pass_get_focus_distance((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_depthOfFieldPassGetMaxRadius(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.depth_of_field_pass_get_max_radius((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_depthOfFieldPassSetFNumber(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.depth_of_field_pass_set_f_number((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_depthOfFieldPassSetFocalLength(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.depth_of_field_pass_set_focal_length((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_depthOfFieldPassSetFocusDistance(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.depth_of_field_pass_set_focus_distance((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_depthOfFieldPassSetMaxRadius(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.depth_of_field_pass_set_max_radius((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_directionalLightExtInit(
    JNIEnv* environment, jclass declaring_class, jbyteArray out_lightBytes, jlongArray out_lightIntegral, jfloatArray out_lightFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_DirectionalLightEXT out_light_value;
    memset(&out_light_value, 0, sizeof out_light_value);
    out_light_value.struct_size = (uint32_t)(sizeof out_light_value);
    out_light_value.struct_version = UINT32_C(1);
    CNA_Result call_result = cna.directional_light_ext_init(&out_light_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jbyte out_lightBytes_values[] = {
            (jbyte)out_light_value.reserved[0],
            (jbyte)out_light_value.reserved[1],
            (jbyte)out_light_value.reserved[2]
        };
        (*environment)->SetByteArrayRegion(environment, out_lightBytes, 0,
            (jsize)(sizeof out_lightBytes_values / sizeof out_lightBytes_values[0]),
            out_lightBytes_values);
        jlong out_lightIntegral_values[] = {
            (jlong)out_light_value.casts_shadows
        };
        (*environment)->SetLongArrayRegion(environment, out_lightIntegral, 0,
            (jsize)(sizeof out_lightIntegral_values / sizeof out_lightIntegral_values[0]),
            out_lightIntegral_values);
        jfloat out_lightFloating_values[] = {
            (jfloat)out_light_value.direction.x,
            (jfloat)out_light_value.direction.y,
            (jfloat)out_light_value.direction.z,
            (jfloat)out_light_value.color.x,
            (jfloat)out_light_value.color.y,
            (jfloat)out_light_value.color.z,
            (jfloat)out_light_value.intensity
        };
        (*environment)->SetFloatArrayRegion(environment, out_lightFloating, 0,
            (jsize)(sizeof out_lightFloating_values / sizeof out_lightFloating_values[0]),
            out_lightFloating_values);
    }
    return (jint)call_result;
}

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
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_filmGrainPassCreate(
    JNIEnv* environment, jclass declaring_class, jlong graphics_device, jlongArray out_pass)
{
    (void)environment;
    (void)declaring_class;
    CNA_PostProcessPassHandle out_pass_value = 0;
    CNA_Result call_result = cna.film_grain_pass_create((CNA_Handle)graphics_device, &out_pass_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_pass_element = (jlong)out_pass_value;
        (*environment)->SetLongArrayRegion(
            environment, out_pass, 0, 1, &out_pass_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_filmGrainPassGetIntensity(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.film_grain_pass_get_intensity((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_filmGrainPassSetIntensity(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.film_grain_pass_set_intensity((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_frustumCullerExtCreate(
    JNIEnv* environment, jclass declaring_class, jlongArray out_culler)
{
    (void)environment;
    (void)declaring_class;
    CNA_FrustumCullerEXTHandle out_culler_value = 0;
    CNA_Result call_result = cna.frustum_culler_ext_create(&out_culler_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_culler_element = (jlong)out_culler_value;
        (*environment)->SetLongArrayRegion(
            environment, out_culler, 0, 1, &out_culler_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_frustumCullerExtCullBoxes(
    JNIEnv* environment, jclass declaring_class, jlong culler, jfloatArray boundsFloating, jlongArray destination, jlongArray out_count)
{
    (void)environment;
    (void)declaring_class;
    jsize bounds_count = (*environment)->GetArrayLength(environment, boundsFloating) / 6;
    if ((*environment)->GetArrayLength(environment, boundsFloating) != bounds_count * 6) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_BoundingBox* bounds_values = (CNA_BoundingBox*)calloc(
        (size_t)bounds_count + 1U, sizeof(*bounds_values));
    if (bounds_values == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    {
        jsize boundsFloating_length = (*environment)->GetArrayLength(environment, boundsFloating);
        jfloat* boundsFloating_values = (jfloat*)malloc(
            ((size_t)boundsFloating_length + 1U) * sizeof(*boundsFloating_values));
        if (boundsFloating_values == NULL) {
            free(bounds_values);
            return (jint)CNA_RESULT_OUT_OF_MEMORY;
        }
        (*environment)->GetFloatArrayRegion(environment, boundsFloating, 0, boundsFloating_length, boundsFloating_values);
        for (jsize element = 0; element < bounds_count; ++element) {
            bounds_values[element].min.x = (float)boundsFloating_values[element * 6 + 0];
            bounds_values[element].min.y = (float)boundsFloating_values[element * 6 + 1];
            bounds_values[element].min.z = (float)boundsFloating_values[element * 6 + 2];
            bounds_values[element].max.x = (float)boundsFloating_values[element * 6 + 3];
            bounds_values[element].max.y = (float)boundsFloating_values[element * 6 + 4];
            bounds_values[element].max.z = (float)boundsFloating_values[element * 6 + 5];
        }
        free(boundsFloating_values);
    }
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jlong* destination_elements = (*environment)->GetLongArrayElements(environment, destination, NULL);
    if (destination_elements == NULL) {
    free(bounds_values);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t* destination_values = (uint64_t*)malloc(
        ((size_t)destination_size + 1U) * sizeof(*destination_values));
    if (destination_values == NULL) {
        (*environment)->ReleaseLongArrayElements(
            environment, destination, destination_elements, JNI_ABORT);
    free(bounds_values);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    for (jsize index = 0; index < destination_size; ++index) {
        destination_values[index] = (uint64_t)destination_elements[index];
    }
    uint64_t out_count_value = 0;
    CNA_Result call_result = cna.frustum_culler_ext_cull_boxes((CNA_FrustumCullerEXTHandle)culler, bounds_values, (uint64_t)bounds_count, destination_values, (uint64_t)destination_size, &out_count_value);
    free(bounds_values);
    if (call_result == CNA_RESULT_SUCCESS) {
        for (jsize index = 0; index < destination_size; ++index) {
            destination_elements[index] = (jlong)destination_values[index];
        }
    }
    free(destination_values);
    (*environment)->ReleaseLongArrayElements(
        environment, destination, destination_elements, 0);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_count_element = (jlong)out_count_value;
        (*environment)->SetLongArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_frustumCullerExtCullSpheres(
    JNIEnv* environment, jclass declaring_class, jlong culler, jfloatArray boundsFloating, jlongArray destination, jlongArray out_count)
{
    (void)environment;
    (void)declaring_class;
    jsize bounds_count = (*environment)->GetArrayLength(environment, boundsFloating) / 4;
    if ((*environment)->GetArrayLength(environment, boundsFloating) != bounds_count * 4) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_BoundingSphere* bounds_values = (CNA_BoundingSphere*)calloc(
        (size_t)bounds_count + 1U, sizeof(*bounds_values));
    if (bounds_values == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    {
        jsize boundsFloating_length = (*environment)->GetArrayLength(environment, boundsFloating);
        jfloat* boundsFloating_values = (jfloat*)malloc(
            ((size_t)boundsFloating_length + 1U) * sizeof(*boundsFloating_values));
        if (boundsFloating_values == NULL) {
            free(bounds_values);
            return (jint)CNA_RESULT_OUT_OF_MEMORY;
        }
        (*environment)->GetFloatArrayRegion(environment, boundsFloating, 0, boundsFloating_length, boundsFloating_values);
        for (jsize element = 0; element < bounds_count; ++element) {
            bounds_values[element].center.x = (float)boundsFloating_values[element * 4 + 0];
            bounds_values[element].center.y = (float)boundsFloating_values[element * 4 + 1];
            bounds_values[element].center.z = (float)boundsFloating_values[element * 4 + 2];
            bounds_values[element].radius = (float)boundsFloating_values[element * 4 + 3];
        }
        free(boundsFloating_values);
    }
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jlong* destination_elements = (*environment)->GetLongArrayElements(environment, destination, NULL);
    if (destination_elements == NULL) {
    free(bounds_values);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t* destination_values = (uint64_t*)malloc(
        ((size_t)destination_size + 1U) * sizeof(*destination_values));
    if (destination_values == NULL) {
        (*environment)->ReleaseLongArrayElements(
            environment, destination, destination_elements, JNI_ABORT);
    free(bounds_values);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    for (jsize index = 0; index < destination_size; ++index) {
        destination_values[index] = (uint64_t)destination_elements[index];
    }
    uint64_t out_count_value = 0;
    CNA_Result call_result = cna.frustum_culler_ext_cull_spheres((CNA_FrustumCullerEXTHandle)culler, bounds_values, (uint64_t)bounds_count, destination_values, (uint64_t)destination_size, &out_count_value);
    free(bounds_values);
    if (call_result == CNA_RESULT_SUCCESS) {
        for (jsize index = 0; index < destination_size; ++index) {
            destination_elements[index] = (jlong)destination_values[index];
        }
    }
    free(destination_values);
    (*environment)->ReleaseLongArrayElements(
        environment, destination, destination_elements, 0);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_count_element = (jlong)out_count_value;
        (*environment)->SetLongArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_frustumCullerExtCullTransforms(
    JNIEnv* environment, jclass declaring_class, jlong culler, jfloatArray transformsFloating, jfloatArray boundsFloating, jfloatArray destinationFloating, jlongArray out_count)
{
    (void)environment;
    (void)declaring_class;
    jsize transforms_count = (*environment)->GetArrayLength(environment, transformsFloating) / 16;
    if ((*environment)->GetArrayLength(environment, transformsFloating) != transforms_count * 16) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Matrix* transforms_values = (CNA_Matrix*)calloc(
        (size_t)transforms_count + 1U, sizeof(*transforms_values));
    if (transforms_values == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    {
        jsize transformsFloating_length = (*environment)->GetArrayLength(environment, transformsFloating);
        jfloat* transformsFloating_values = (jfloat*)malloc(
            ((size_t)transformsFloating_length + 1U) * sizeof(*transformsFloating_values));
        if (transformsFloating_values == NULL) {
            free(transforms_values);
            return (jint)CNA_RESULT_OUT_OF_MEMORY;
        }
        (*environment)->GetFloatArrayRegion(environment, transformsFloating, 0, transformsFloating_length, transformsFloating_values);
        for (jsize element = 0; element < transforms_count; ++element) {
            transforms_values[element].m11 = (float)transformsFloating_values[element * 16 + 0];
            transforms_values[element].m12 = (float)transformsFloating_values[element * 16 + 1];
            transforms_values[element].m13 = (float)transformsFloating_values[element * 16 + 2];
            transforms_values[element].m14 = (float)transformsFloating_values[element * 16 + 3];
            transforms_values[element].m21 = (float)transformsFloating_values[element * 16 + 4];
            transforms_values[element].m22 = (float)transformsFloating_values[element * 16 + 5];
            transforms_values[element].m23 = (float)transformsFloating_values[element * 16 + 6];
            transforms_values[element].m24 = (float)transformsFloating_values[element * 16 + 7];
            transforms_values[element].m31 = (float)transformsFloating_values[element * 16 + 8];
            transforms_values[element].m32 = (float)transformsFloating_values[element * 16 + 9];
            transforms_values[element].m33 = (float)transformsFloating_values[element * 16 + 10];
            transforms_values[element].m34 = (float)transformsFloating_values[element * 16 + 11];
            transforms_values[element].m41 = (float)transformsFloating_values[element * 16 + 12];
            transforms_values[element].m42 = (float)transformsFloating_values[element * 16 + 13];
            transforms_values[element].m43 = (float)transformsFloating_values[element * 16 + 14];
            transforms_values[element].m44 = (float)transformsFloating_values[element * 16 + 15];
        }
        free(transformsFloating_values);
    }
    jsize bounds_count = (*environment)->GetArrayLength(environment, boundsFloating) / 6;
    if ((*environment)->GetArrayLength(environment, boundsFloating) != bounds_count * 6) {
        free(transforms_values);
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_BoundingBox* bounds_values = (CNA_BoundingBox*)calloc(
        (size_t)bounds_count + 1U, sizeof(*bounds_values));
    if (bounds_values == NULL) {
    free(transforms_values);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    {
        jsize boundsFloating_length = (*environment)->GetArrayLength(environment, boundsFloating);
        jfloat* boundsFloating_values = (jfloat*)malloc(
            ((size_t)boundsFloating_length + 1U) * sizeof(*boundsFloating_values));
        if (boundsFloating_values == NULL) {
            free(bounds_values);
            free(transforms_values);
            return (jint)CNA_RESULT_OUT_OF_MEMORY;
        }
        (*environment)->GetFloatArrayRegion(environment, boundsFloating, 0, boundsFloating_length, boundsFloating_values);
        for (jsize element = 0; element < bounds_count; ++element) {
            bounds_values[element].min.x = (float)boundsFloating_values[element * 6 + 0];
            bounds_values[element].min.y = (float)boundsFloating_values[element * 6 + 1];
            bounds_values[element].min.z = (float)boundsFloating_values[element * 6 + 2];
            bounds_values[element].max.x = (float)boundsFloating_values[element * 6 + 3];
            bounds_values[element].max.y = (float)boundsFloating_values[element * 6 + 4];
            bounds_values[element].max.z = (float)boundsFloating_values[element * 6 + 5];
        }
        free(boundsFloating_values);
    }
    jsize destination_count = (*environment)->GetArrayLength(environment, destinationFloating) / 16;
    if ((*environment)->GetArrayLength(environment, destinationFloating) != destination_count * 16) {
        free(bounds_values);
        free(transforms_values);
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Matrix* destination_values = (CNA_Matrix*)calloc(
        (size_t)destination_count + 1U, sizeof(*destination_values));
    if (destination_values == NULL) {
    free(bounds_values);
    free(transforms_values);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    {
        jsize destinationFloating_length = (*environment)->GetArrayLength(environment, destinationFloating);
        jfloat* destinationFloating_values = (jfloat*)malloc(
            ((size_t)destinationFloating_length + 1U) * sizeof(*destinationFloating_values));
        if (destinationFloating_values == NULL) {
            free(destination_values);
            free(bounds_values);
            free(transforms_values);
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
    CNA_Result call_result = cna.frustum_culler_ext_cull_transforms((CNA_FrustumCullerEXTHandle)culler, transforms_values, (uint64_t)transforms_count, bounds_values, (uint64_t)bounds_count, destination_values, (uint64_t)destination_count, &out_count_value);
    free(transforms_values);
    free(bounds_values);
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
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_frustumCullerExtDestroy(
    JNIEnv* environment, jclass declaring_class, jlong culler)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.frustum_culler_ext_destroy((CNA_FrustumCullerEXTHandle)culler);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_frustumCullerExtGetFrustum(
    JNIEnv* environment, jclass declaring_class, jlong culler, jfloatArray out_frustumFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_BoundingFrustum out_frustum_value;
    memset(&out_frustum_value, 0, sizeof out_frustum_value);
    CNA_Result call_result = cna.frustum_culler_ext_get_frustum((CNA_FrustumCullerEXTHandle)culler, &out_frustum_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_frustumFloating_values[] = {
            (jfloat)out_frustum_value.matrix.m11,
            (jfloat)out_frustum_value.matrix.m12,
            (jfloat)out_frustum_value.matrix.m13,
            (jfloat)out_frustum_value.matrix.m14,
            (jfloat)out_frustum_value.matrix.m21,
            (jfloat)out_frustum_value.matrix.m22,
            (jfloat)out_frustum_value.matrix.m23,
            (jfloat)out_frustum_value.matrix.m24,
            (jfloat)out_frustum_value.matrix.m31,
            (jfloat)out_frustum_value.matrix.m32,
            (jfloat)out_frustum_value.matrix.m33,
            (jfloat)out_frustum_value.matrix.m34,
            (jfloat)out_frustum_value.matrix.m41,
            (jfloat)out_frustum_value.matrix.m42,
            (jfloat)out_frustum_value.matrix.m43,
            (jfloat)out_frustum_value.matrix.m44
        };
        (*environment)->SetFloatArrayRegion(environment, out_frustumFloating, 0,
            (jsize)(sizeof out_frustumFloating_values / sizeof out_frustumFloating_values[0]),
            out_frustumFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_frustumCullerExtIsBoxVisible(
    JNIEnv* environment, jclass declaring_class, jlong culler, jfloatArray boxFloating, jbooleanArray out_visible)
{
    (void)environment;
    (void)declaring_class;
    CNA_BoundingBox box_value;
    memset(&box_value, 0, sizeof box_value);
    {
        jfloat boxFloating_values[6];
        (*environment)->GetFloatArrayRegion(environment, boxFloating, 0, 6, boxFloating_values);
        box_value.min.x = (float)boxFloating_values[0];
        box_value.min.y = (float)boxFloating_values[1];
        box_value.min.z = (float)boxFloating_values[2];
        box_value.max.x = (float)boxFloating_values[3];
        box_value.max.y = (float)boxFloating_values[4];
        box_value.max.z = (float)boxFloating_values[5];
    }
    CNA_Bool out_visible_value = 0;
    CNA_Result call_result = cna.frustum_culler_ext_is_box_visible((CNA_FrustumCullerEXTHandle)culler, &box_value, &out_visible_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_visible_element = (jboolean)out_visible_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_visible, 0, 1, &out_visible_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_frustumCullerExtIsSphereVisible(
    JNIEnv* environment, jclass declaring_class, jlong culler, jfloatArray sphereFloating, jbooleanArray out_visible)
{
    (void)environment;
    (void)declaring_class;
    CNA_BoundingSphere sphere_value;
    memset(&sphere_value, 0, sizeof sphere_value);
    {
        jfloat sphereFloating_values[4];
        (*environment)->GetFloatArrayRegion(environment, sphereFloating, 0, 4, sphereFloating_values);
        sphere_value.center.x = (float)sphereFloating_values[0];
        sphere_value.center.y = (float)sphereFloating_values[1];
        sphere_value.center.z = (float)sphereFloating_values[2];
        sphere_value.radius = (float)sphereFloating_values[3];
    }
    CNA_Bool out_visible_value = 0;
    CNA_Result call_result = cna.frustum_culler_ext_is_sphere_visible((CNA_FrustumCullerEXTHandle)culler, &sphere_value, &out_visible_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_visible_element = (jboolean)out_visible_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_visible, 0, 1, &out_visible_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_frustumCullerExtSetCamera(
    JNIEnv* environment, jclass declaring_class, jlong culler, jfloatArray viewFloating, jfloatArray projectionFloating)
{
    (void)environment;
    (void)declaring_class;
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
    CNA_Result call_result = cna.frustum_culler_ext_set_camera((CNA_FrustumCullerEXTHandle)culler, &view_value, &projection_value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_frustumCullerExtSetViewProjection(
    JNIEnv* environment, jclass declaring_class, jlong culler, jfloatArray view_projectionFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Matrix view_projection_value;
    memset(&view_projection_value, 0, sizeof view_projection_value);
    {
        jfloat view_projectionFloating_values[16];
        (*environment)->GetFloatArrayRegion(environment, view_projectionFloating, 0, 16, view_projectionFloating_values);
        view_projection_value.m11 = (float)view_projectionFloating_values[0];
        view_projection_value.m12 = (float)view_projectionFloating_values[1];
        view_projection_value.m13 = (float)view_projectionFloating_values[2];
        view_projection_value.m14 = (float)view_projectionFloating_values[3];
        view_projection_value.m21 = (float)view_projectionFloating_values[4];
        view_projection_value.m22 = (float)view_projectionFloating_values[5];
        view_projection_value.m23 = (float)view_projectionFloating_values[6];
        view_projection_value.m24 = (float)view_projectionFloating_values[7];
        view_projection_value.m31 = (float)view_projectionFloating_values[8];
        view_projection_value.m32 = (float)view_projectionFloating_values[9];
        view_projection_value.m33 = (float)view_projectionFloating_values[10];
        view_projection_value.m34 = (float)view_projectionFloating_values[11];
        view_projection_value.m41 = (float)view_projectionFloating_values[12];
        view_projection_value.m42 = (float)view_projectionFloating_values[13];
        view_projection_value.m43 = (float)view_projectionFloating_values[14];
        view_projection_value.m44 = (float)view_projectionFloating_values[15];
    }
    CNA_Result call_result = cna.frustum_culler_ext_set_view_projection((CNA_FrustumCullerEXTHandle)culler, &view_projection_value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_fxaaPassCopyFragmentGlsl(
    JNIEnv* environment, jclass declaring_class, jbyteArray destination, jlongArray out_bytes)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t destination_written = 0;
    CNA_Result call_result = cna.fxaa_pass_copy_fragment_glsl((char*)destination_bytes, (uint64_t)destination_size, &destination_written);
    (*environment)->ReleaseByteArrayElements(environment, destination, destination_bytes, 0);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong destination_element = (jlong)destination_written;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &destination_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_fxaaPassCreate(
    JNIEnv* environment, jclass declaring_class, jlong graphics_device, jlongArray out_pass)
{
    (void)environment;
    (void)declaring_class;
    CNA_PostProcessPassHandle out_pass_value = 0;
    CNA_Result call_result = cna.fxaa_pass_create((CNA_Handle)graphics_device, &out_pass_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_pass_element = (jlong)out_pass_value;
        (*environment)->SetLongArrayRegion(
            environment, out_pass, 0, 1, &out_pass_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_fxaaPassEdgeThresholdForQuality(
    JNIEnv* environment, jclass declaring_class, jint quality, jfloatArray out_threshold)
{
    (void)environment;
    (void)declaring_class;
    float out_threshold_value = 0;
    CNA_Result call_result = cna.fxaa_pass_edge_threshold_for_quality((CNA_RenderQuality)quality, &out_threshold_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_threshold_element = (jfloat)out_threshold_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_threshold, 0, 1, &out_threshold_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_fxaaPassGetEdgeThreshold(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.fxaa_pass_get_edge_threshold((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_fxaaPassSetEdgeThreshold(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.fxaa_pass_set_edge_threshold((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_gpuTimerBegin(
    JNIEnv* environment, jclass declaring_class, jlong timer)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.gpu_timer_begin((CNA_GpuTimerHandle)timer);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_gpuTimerCopyUnsupportedReason(
    JNIEnv* environment, jclass declaring_class, jlong timer, jbyteArray destination, jlongArray out_bytes)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t destination_written = 0;
    CNA_Result call_result = cna.gpu_timer_copy_unsupported_reason((CNA_GpuTimerHandle)timer, (char*)destination_bytes, (uint64_t)destination_size, &destination_written);
    (*environment)->ReleaseByteArrayElements(environment, destination, destination_bytes, 0);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong destination_element = (jlong)destination_written;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &destination_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_gpuTimerCreate(
    JNIEnv* environment, jclass declaring_class, jlong graphics_device, jlongArray out_timer)
{
    (void)environment;
    (void)declaring_class;
    CNA_GpuTimerHandle out_timer_value = 0;
    CNA_Result call_result = cna.gpu_timer_create((CNA_Handle)graphics_device, &out_timer_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_timer_element = (jlong)out_timer_value;
        (*environment)->SetLongArrayRegion(
            environment, out_timer, 0, 1, &out_timer_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_gpuTimerDestroy(
    JNIEnv* environment, jclass declaring_class, jlong timer)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.gpu_timer_destroy((CNA_GpuTimerHandle)timer);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_gpuTimerEnd(
    JNIEnv* environment, jclass declaring_class, jlong timer)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.gpu_timer_end((CNA_GpuTimerHandle)timer);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_gpuTimerGetLastMilliseconds(
    JNIEnv* environment, jclass declaring_class, jlong timer, jdoubleArray out_milliseconds)
{
    (void)environment;
    (void)declaring_class;
    double out_milliseconds_value = 0;
    CNA_Result call_result = cna.gpu_timer_get_last_milliseconds((CNA_GpuTimerHandle)timer, &out_milliseconds_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jdouble out_milliseconds_element = (jdouble)out_milliseconds_value;
        (*environment)->SetDoubleArrayRegion(
            environment, out_milliseconds, 0, 1, &out_milliseconds_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_gpuTimerGetSampleCount(
    JNIEnv* environment, jclass declaring_class, jlong timer, jintArray out_sample_count)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_sample_count_value = 0;
    CNA_Result call_result = cna.gpu_timer_get_sample_count((CNA_GpuTimerHandle)timer, &out_sample_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_sample_count_element = (jint)out_sample_count_value;
        (*environment)->SetIntArrayRegion(
            environment, out_sample_count, 0, 1, &out_sample_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_gpuTimerIsOpen(
    JNIEnv* environment, jclass declaring_class, jlong timer, jbooleanArray out_open)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_open_value = 0;
    CNA_Result call_result = cna.gpu_timer_is_open((CNA_GpuTimerHandle)timer, &out_open_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_open_element = (jboolean)out_open_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_open, 0, 1, &out_open_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_gpuTimerIsResultAvailable(
    JNIEnv* environment, jclass declaring_class, jlong timer, jbooleanArray out_available)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_available_value = 0;
    CNA_Result call_result = cna.gpu_timer_is_result_available((CNA_GpuTimerHandle)timer, &out_available_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_available_element = (jboolean)out_available_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_available, 0, 1, &out_available_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_gpuTimerIsSupported(
    JNIEnv* environment, jclass declaring_class, jlong timer, jbooleanArray out_supported)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_supported_value = 0;
    CNA_Result call_result = cna.gpu_timer_is_supported((CNA_GpuTimerHandle)timer, &out_supported_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_supported_element = (jboolean)out_supported_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_supported, 0, 1, &out_supported_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_gpuTimerPoll(
    JNIEnv* environment, jclass declaring_class, jlong timer, jbooleanArray out_collected)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_collected_value = 0;
    CNA_Result call_result = cna.gpu_timer_poll((CNA_GpuTimerHandle)timer, &out_collected_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_collected_element = (jboolean)out_collected_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_collected, 0, 1, &out_collected_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_heightFogPassCreate(
    JNIEnv* environment, jclass declaring_class, jlong graphics_device, jlongArray out_pass)
{
    (void)environment;
    (void)declaring_class;
    CNA_PostProcessPassHandle out_pass_value = 0;
    CNA_Result call_result = cna.height_fog_pass_create((CNA_Handle)graphics_device, &out_pass_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_pass_element = (jlong)out_pass_value;
        (*environment)->SetLongArrayRegion(
            environment, out_pass, 0, 1, &out_pass_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_heightFogPassGetBaseHeight(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.height_fog_pass_get_base_height((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_heightFogPassGetColor(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_valueFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Vector3 out_value_value;
    memset(&out_value_value, 0, sizeof out_value_value);
    CNA_Result call_result = cna.height_fog_pass_get_color((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_valueFloating_values[] = {
            (jfloat)out_value_value.x,
            (jfloat)out_value_value.y,
            (jfloat)out_value_value.z
        };
        (*environment)->SetFloatArrayRegion(environment, out_valueFloating, 0,
            (jsize)(sizeof out_valueFloating_values / sizeof out_valueFloating_values[0]),
            out_valueFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_heightFogPassGetDensity(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.height_fog_pass_get_density((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_heightFogPassGetFalloff(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.height_fog_pass_get_falloff((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_heightFogPassOpticalDepth(
    JNIEnv* environment, jclass declaring_class, jfloat camera_height, jfloat ray_height_step, jfloat distance, jfloat density, jfloat falloff, jfloat base_height, jfloatArray out_depth)
{
    (void)environment;
    (void)declaring_class;
    float out_depth_value = 0;
    CNA_Result call_result = cna.height_fog_pass_optical_depth((float)camera_height, (float)ray_height_step, (float)distance, (float)density, (float)falloff, (float)base_height, &out_depth_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_depth_element = (jfloat)out_depth_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_depth, 0, 1, &out_depth_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_heightFogPassSetBaseHeight(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.height_fog_pass_set_base_height((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_heightFogPassSetColor(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray valueFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Vector3 value_value;
    memset(&value_value, 0, sizeof value_value);
    {
        jfloat valueFloating_values[3];
        (*environment)->GetFloatArrayRegion(environment, valueFloating, 0, 3, valueFloating_values);
        value_value.x = (float)valueFloating_values[0];
        value_value.y = (float)valueFloating_values[1];
        value_value.z = (float)valueFloating_values[2];
    }
    CNA_Result call_result = cna.height_fog_pass_set_color((CNA_PostProcessPassHandle)pass, &value_value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_heightFogPassSetDensity(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.height_fog_pass_set_density((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_heightFogPassSetFalloff(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.height_fog_pass_set_falloff((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_instancedRendererExtCopyInstanceElements(
    JNIEnv* environment, jclass declaring_class, jlongArray destinationIntegral, jlongArray out_element_count)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_count = (*environment)->GetArrayLength(environment, destinationIntegral) / 4;
    if ((*environment)->GetArrayLength(environment, destinationIntegral) != destination_count * 4) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_VertexElement* destination_values = (CNA_VertexElement*)calloc(
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
            destination_values[element].offset = (int32_t)destinationIntegral_values[element * 4 + 0];
            destination_values[element].format = (CNA_VertexElementFormat)destinationIntegral_values[element * 4 + 1];
            destination_values[element].usage = (CNA_VertexElementUsage)destinationIntegral_values[element * 4 + 2];
            destination_values[element].usage_index = (int32_t)destinationIntegral_values[element * 4 + 3];
        }
        free(destinationIntegral_values);
    }
    uint64_t out_element_count_value = 0;
    CNA_Result call_result = cna.instanced_renderer_ext_copy_instance_elements(destination_values, (uint64_t)destination_count, &out_element_count_value);
    if (call_result == CNA_RESULT_SUCCESS) {
        {
            jlong* destinationIntegral_out = (jlong*)malloc(
                ((size_t)destination_count * 4 + 1U) * sizeof(*destinationIntegral_out));
            if (destinationIntegral_out != NULL) {
                for (jsize element = 0; element < destination_count; ++element) {
                    destinationIntegral_out[element * 4 + 0] = (jlong)destination_values[element].offset;
                    destinationIntegral_out[element * 4 + 1] = (jlong)destination_values[element].format;
                    destinationIntegral_out[element * 4 + 2] = (jlong)destination_values[element].usage;
                    destinationIntegral_out[element * 4 + 3] = (jlong)destination_values[element].usage_index;
                }
                (*environment)->SetLongArrayRegion(
                    environment, destinationIntegral, 0,
                    (jsize)(destination_count * 4), destinationIntegral_out);
                free(destinationIntegral_out);
            }
        }
    }
    free(destination_values);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_element_count_element = (jlong)out_element_count_value;
        (*environment)->SetLongArrayRegion(
            environment, out_element_count, 0, 1, &out_element_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_instancedRendererExtCopyTintElements(
    JNIEnv* environment, jclass declaring_class, jlongArray destinationIntegral, jlongArray out_element_count)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_count = (*environment)->GetArrayLength(environment, destinationIntegral) / 4;
    if ((*environment)->GetArrayLength(environment, destinationIntegral) != destination_count * 4) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_VertexElement* destination_values = (CNA_VertexElement*)calloc(
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
            destination_values[element].offset = (int32_t)destinationIntegral_values[element * 4 + 0];
            destination_values[element].format = (CNA_VertexElementFormat)destinationIntegral_values[element * 4 + 1];
            destination_values[element].usage = (CNA_VertexElementUsage)destinationIntegral_values[element * 4 + 2];
            destination_values[element].usage_index = (int32_t)destinationIntegral_values[element * 4 + 3];
        }
        free(destinationIntegral_values);
    }
    uint64_t out_element_count_value = 0;
    CNA_Result call_result = cna.instanced_renderer_ext_copy_tint_elements(destination_values, (uint64_t)destination_count, &out_element_count_value);
    if (call_result == CNA_RESULT_SUCCESS) {
        {
            jlong* destinationIntegral_out = (jlong*)malloc(
                ((size_t)destination_count * 4 + 1U) * sizeof(*destinationIntegral_out));
            if (destinationIntegral_out != NULL) {
                for (jsize element = 0; element < destination_count; ++element) {
                    destinationIntegral_out[element * 4 + 0] = (jlong)destination_values[element].offset;
                    destinationIntegral_out[element * 4 + 1] = (jlong)destination_values[element].format;
                    destinationIntegral_out[element * 4 + 2] = (jlong)destination_values[element].usage;
                    destinationIntegral_out[element * 4 + 3] = (jlong)destination_values[element].usage_index;
                }
                (*environment)->SetLongArrayRegion(
                    environment, destinationIntegral, 0,
                    (jsize)(destination_count * 4), destinationIntegral_out);
                free(destinationIntegral_out);
            }
        }
    }
    free(destination_values);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_element_count_element = (jlong)out_element_count_value;
        (*environment)->SetLongArrayRegion(
            environment, out_element_count, 0, 1, &out_element_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_instancedRendererExtCreate(
    JNIEnv* environment, jclass declaring_class, jlong graphics_device, jlong part, jlongArray out_renderer)
{
    (void)environment;
    (void)declaring_class;
    CNA_InstancedRendererEXTHandle out_renderer_value = 0;
    CNA_Result call_result = cna.instanced_renderer_ext_create((CNA_Handle)graphics_device, (CNA_ModelMeshPartHandle)part, &out_renderer_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_renderer_element = (jlong)out_renderer_value;
        (*environment)->SetLongArrayRegion(
            environment, out_renderer, 0, 1, &out_renderer_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_instancedRendererExtDestroy(
    JNIEnv* environment, jclass declaring_class, jlong renderer)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.instanced_renderer_ext_destroy((CNA_InstancedRendererEXTHandle)renderer);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_instancedRendererExtDidLastDrawInstance(
    JNIEnv* environment, jclass declaring_class, jlong renderer, jbooleanArray out_instanced)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_instanced_value = 0;
    CNA_Result call_result = cna.instanced_renderer_ext_did_last_draw_instance((CNA_InstancedRendererEXTHandle)renderer, &out_instanced_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_instanced_element = (jboolean)out_instanced_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_instanced, 0, 1, &out_instanced_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_instancedRendererExtDraw(
    JNIEnv* environment, jclass declaring_class, jlong renderer, jlong effect)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.instanced_renderer_ext_draw((CNA_InstancedRendererEXTHandle)renderer, (CNA_EffectHandle)effect);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_instancedRendererExtGetInstanceCapacity(
    JNIEnv* environment, jclass declaring_class, jlong renderer, jintArray out_capacity)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_capacity_value = 0;
    CNA_Result call_result = cna.instanced_renderer_ext_get_instance_capacity((CNA_InstancedRendererEXTHandle)renderer, &out_capacity_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_capacity_element = (jint)out_capacity_value;
        (*environment)->SetIntArrayRegion(
            environment, out_capacity, 0, 1, &out_capacity_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_instancedRendererExtGetInstanceCount(
    JNIEnv* environment, jclass declaring_class, jlong renderer, jintArray out_count)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_count_value = 0;
    CNA_Result call_result = cna.instanced_renderer_ext_get_instance_count((CNA_InstancedRendererEXTHandle)renderer, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_count_element = (jint)out_count_value;
        (*environment)->SetIntArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_instancedRendererExtGetInstanceStride(
    JNIEnv* environment, jclass declaring_class, jintArray out_stride)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_stride_value = 0;
    CNA_Result call_result = cna.instanced_renderer_ext_get_instance_stride(&out_stride_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_stride_element = (jint)out_stride_value;
        (*environment)->SetIntArrayRegion(
            environment, out_stride, 0, 1, &out_stride_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_instancedRendererExtGetLastDrawCallCount(
    JNIEnv* environment, jclass declaring_class, jlong renderer, jintArray out_count)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_count_value = 0;
    CNA_Result call_result = cna.instanced_renderer_ext_get_last_draw_call_count((CNA_InstancedRendererEXTHandle)renderer, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_count_element = (jint)out_count_value;
        (*environment)->SetIntArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_instancedRendererExtGetTintStride(
    JNIEnv* environment, jclass declaring_class, jintArray out_stride)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_stride_value = 0;
    CNA_Result call_result = cna.instanced_renderer_ext_get_tint_stride(&out_stride_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_stride_element = (jint)out_stride_value;
        (*environment)->SetIntArrayRegion(
            environment, out_stride, 0, 1, &out_stride_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_instancedRendererExtIsFallbackEnabled(
    JNIEnv* environment, jclass declaring_class, jlong renderer, jbooleanArray out_enabled)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_enabled_value = 0;
    CNA_Result call_result = cna.instanced_renderer_ext_is_fallback_enabled((CNA_InstancedRendererEXTHandle)renderer, &out_enabled_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_enabled_element = (jboolean)out_enabled_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_enabled, 0, 1, &out_enabled_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_instancedRendererExtIsInstancingSupported(
    JNIEnv* environment, jclass declaring_class, jlong renderer, jbooleanArray out_supported)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_supported_value = 0;
    CNA_Result call_result = cna.instanced_renderer_ext_is_instancing_supported((CNA_InstancedRendererEXTHandle)renderer, &out_supported_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_supported_element = (jboolean)out_supported_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_supported, 0, 1, &out_supported_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_instancedRendererExtIsTintsEnabled(
    JNIEnv* environment, jclass declaring_class, jlong renderer, jbooleanArray out_enabled)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_enabled_value = 0;
    CNA_Result call_result = cna.instanced_renderer_ext_is_tints_enabled((CNA_InstancedRendererEXTHandle)renderer, &out_enabled_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_enabled_element = (jboolean)out_enabled_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_enabled, 0, 1, &out_enabled_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_instancedRendererExtSetFallbackEnabled(
    JNIEnv* environment, jclass declaring_class, jlong renderer, jboolean enabled)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.instanced_renderer_ext_set_fallback_enabled((CNA_InstancedRendererEXTHandle)renderer, (CNA_Bool)enabled);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_instancedRendererExtSetInstanceTints(
    JNIEnv* environment, jclass declaring_class, jlong renderer, jlongArray tintsIntegral)
{
    (void)environment;
    (void)declaring_class;
    jsize tints_count = (*environment)->GetArrayLength(environment, tintsIntegral) / 4;
    if ((*environment)->GetArrayLength(environment, tintsIntegral) != tints_count * 4) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Color* tints_values = (CNA_Color*)calloc(
        (size_t)tints_count + 1U, sizeof(*tints_values));
    if (tints_values == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    {
        jsize tintsIntegral_length = (*environment)->GetArrayLength(environment, tintsIntegral);
        jlong* tintsIntegral_values = (jlong*)malloc(
            ((size_t)tintsIntegral_length + 1U) * sizeof(*tintsIntegral_values));
        if (tintsIntegral_values == NULL) {
            free(tints_values);
            return (jint)CNA_RESULT_OUT_OF_MEMORY;
        }
        (*environment)->GetLongArrayRegion(environment, tintsIntegral, 0, tintsIntegral_length, tintsIntegral_values);
        for (jsize element = 0; element < tints_count; ++element) {
            tints_values[element].r = (uint8_t)tintsIntegral_values[element * 4 + 0];
            tints_values[element].g = (uint8_t)tintsIntegral_values[element * 4 + 1];
            tints_values[element].b = (uint8_t)tintsIntegral_values[element * 4 + 2];
            tints_values[element].a = (uint8_t)tintsIntegral_values[element * 4 + 3];
        }
        free(tintsIntegral_values);
    }
    CNA_Result call_result = cna.instanced_renderer_ext_set_instance_tints((CNA_InstancedRendererEXTHandle)renderer, tints_values, (uint64_t)tints_count);
    free(tints_values);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_instancedRendererExtSetInstances(
    JNIEnv* environment, jclass declaring_class, jlong renderer, jfloatArray transformsFloating)
{
    (void)environment;
    (void)declaring_class;
    jsize transforms_count = (*environment)->GetArrayLength(environment, transformsFloating) / 16;
    if ((*environment)->GetArrayLength(environment, transformsFloating) != transforms_count * 16) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Matrix* transforms_values = (CNA_Matrix*)calloc(
        (size_t)transforms_count + 1U, sizeof(*transforms_values));
    if (transforms_values == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    {
        jsize transformsFloating_length = (*environment)->GetArrayLength(environment, transformsFloating);
        jfloat* transformsFloating_values = (jfloat*)malloc(
            ((size_t)transformsFloating_length + 1U) * sizeof(*transformsFloating_values));
        if (transformsFloating_values == NULL) {
            free(transforms_values);
            return (jint)CNA_RESULT_OUT_OF_MEMORY;
        }
        (*environment)->GetFloatArrayRegion(environment, transformsFloating, 0, transformsFloating_length, transformsFloating_values);
        for (jsize element = 0; element < transforms_count; ++element) {
            transforms_values[element].m11 = (float)transformsFloating_values[element * 16 + 0];
            transforms_values[element].m12 = (float)transformsFloating_values[element * 16 + 1];
            transforms_values[element].m13 = (float)transformsFloating_values[element * 16 + 2];
            transforms_values[element].m14 = (float)transformsFloating_values[element * 16 + 3];
            transforms_values[element].m21 = (float)transformsFloating_values[element * 16 + 4];
            transforms_values[element].m22 = (float)transformsFloating_values[element * 16 + 5];
            transforms_values[element].m23 = (float)transformsFloating_values[element * 16 + 6];
            transforms_values[element].m24 = (float)transformsFloating_values[element * 16 + 7];
            transforms_values[element].m31 = (float)transformsFloating_values[element * 16 + 8];
            transforms_values[element].m32 = (float)transformsFloating_values[element * 16 + 9];
            transforms_values[element].m33 = (float)transformsFloating_values[element * 16 + 10];
            transforms_values[element].m34 = (float)transformsFloating_values[element * 16 + 11];
            transforms_values[element].m41 = (float)transformsFloating_values[element * 16 + 12];
            transforms_values[element].m42 = (float)transformsFloating_values[element * 16 + 13];
            transforms_values[element].m43 = (float)transformsFloating_values[element * 16 + 14];
            transforms_values[element].m44 = (float)transformsFloating_values[element * 16 + 15];
        }
        free(transformsFloating_values);
    }
    CNA_Result call_result = cna.instanced_renderer_ext_set_instances((CNA_InstancedRendererEXTHandle)renderer, transforms_values, (uint64_t)transforms_count);
    free(transforms_values);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_instancedRendererExtSetTintsEnabled(
    JNIEnv* environment, jclass declaring_class, jlong renderer, jboolean enabled)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.instanced_renderer_ext_set_tints_enabled((CNA_InstancedRendererEXTHandle)renderer, (CNA_Bool)enabled);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lensFlarePassCreate(
    JNIEnv* environment, jclass declaring_class, jlong graphics_device, jlongArray out_pass)
{
    (void)environment;
    (void)declaring_class;
    CNA_PostProcessPassHandle out_pass_value = 0;
    CNA_Result call_result = cna.lens_flare_pass_create((CNA_Handle)graphics_device, &out_pass_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_pass_element = (jlong)out_pass_value;
        (*environment)->SetLongArrayRegion(
            environment, out_pass, 0, 1, &out_pass_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lensFlarePassGetDispersal(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.lens_flare_pass_get_dispersal((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lensFlarePassGetIntensity(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.lens_flare_pass_get_intensity((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lensFlarePassGetThreshold(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.lens_flare_pass_get_threshold((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lensFlarePassSetDispersal(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.lens_flare_pass_set_dispersal((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lensFlarePassSetIntensity(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.lens_flare_pass_set_intensity((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lensFlarePassSetThreshold(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.lens_flare_pass_set_threshold((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeExtCopyCoefficients(
    JNIEnv* environment, jclass declaring_class, jlong probe, jfloatArray destinationFloating, jlongArray out_count)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_count = (*environment)->GetArrayLength(environment, destinationFloating) / 3;
    if ((*environment)->GetArrayLength(environment, destinationFloating) != destination_count * 3) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Vector3* destination_values = (CNA_Vector3*)calloc(
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
            destination_values[element].x = (float)destinationFloating_values[element * 3 + 0];
            destination_values[element].y = (float)destinationFloating_values[element * 3 + 1];
            destination_values[element].z = (float)destinationFloating_values[element * 3 + 2];
        }
        free(destinationFloating_values);
    }
    uint64_t out_count_value = 0;
    CNA_Result call_result = cna.light_probe_ext_copy_coefficients((CNA_LightProbeHandle)probe, destination_values, (uint64_t)destination_count, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS) {
        {
            jfloat* destinationFloating_out = (jfloat*)malloc(
                ((size_t)destination_count * 3 + 1U) * sizeof(*destinationFloating_out));
            if (destinationFloating_out != NULL) {
                for (jsize element = 0; element < destination_count; ++element) {
                    destinationFloating_out[element * 3 + 0] = (jfloat)destination_values[element].x;
                    destinationFloating_out[element * 3 + 1] = (jfloat)destination_values[element].y;
                    destinationFloating_out[element * 3 + 2] = (jfloat)destination_values[element].z;
                }
                (*environment)->SetFloatArrayRegion(
                    environment, destinationFloating, 0,
                    (jsize)(destination_count * 3), destinationFloating_out);
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
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeExtCopyEvaluationGlsl(
    JNIEnv* environment, jclass declaring_class, jbyteArray destination, jlongArray out_bytes)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t destination_written = 0;
    CNA_Result call_result = cna.light_probe_ext_copy_evaluation_glsl((char*)destination_bytes, (uint64_t)destination_size, &destination_written);
    (*environment)->ReleaseByteArrayElements(environment, destination, destination_bytes, 0);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong destination_element = (jlong)destination_written;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &destination_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeExtCopyFrom(
    JNIEnv* environment, jclass declaring_class, jlong destination, jlong source)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.light_probe_ext_copy_from((CNA_LightProbeHandle)destination, (CNA_LightProbeHandle)source);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeExtCreate(
    JNIEnv* environment, jclass declaring_class, jlongArray out_probe)
{
    (void)environment;
    (void)declaring_class;
    CNA_LightProbeHandle out_probe_value = 0;
    CNA_Result call_result = cna.light_probe_ext_create(&out_probe_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_probe_element = (jlong)out_probe_value;
        (*environment)->SetLongArrayRegion(
            environment, out_probe, 0, 1, &out_probe_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeExtCreateAt(
    JNIEnv* environment, jclass declaring_class, jfloatArray positionFloating, jlongArray out_probe)
{
    (void)environment;
    (void)declaring_class;
    CNA_Vector3 position_value;
    memset(&position_value, 0, sizeof position_value);
    {
        jfloat positionFloating_values[3];
        (*environment)->GetFloatArrayRegion(environment, positionFloating, 0, 3, positionFloating_values);
        position_value.x = (float)positionFloating_values[0];
        position_value.y = (float)positionFloating_values[1];
        position_value.z = (float)positionFloating_values[2];
    }
    CNA_LightProbeHandle out_probe_value = 0;
    CNA_Result call_result = cna.light_probe_ext_create_at(&position_value, &out_probe_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_probe_element = (jlong)out_probe_value;
        (*environment)->SetLongArrayRegion(
            environment, out_probe, 0, 1, &out_probe_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeExtDestroy(
    JNIEnv* environment, jclass declaring_class, jlong probe)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.light_probe_ext_destroy((CNA_LightProbeHandle)probe);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeExtEquals(
    JNIEnv* environment, jclass declaring_class, jlong first, jlong second, jbooleanArray out_equal)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_equal_value = 0;
    CNA_Result call_result = cna.light_probe_ext_equals((CNA_LightProbeHandle)first, (CNA_LightProbeHandle)second, &out_equal_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_equal_element = (jboolean)out_equal_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_equal, 0, 1, &out_equal_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeExtGetCoefficient(
    JNIEnv* environment, jclass declaring_class, jlong probe, jint index, jfloatArray out_valueFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Vector3 out_value_value;
    memset(&out_value_value, 0, sizeof out_value_value);
    CNA_Result call_result = cna.light_probe_ext_get_coefficient((CNA_LightProbeHandle)probe, (int32_t)index, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_valueFloating_values[] = {
            (jfloat)out_value_value.x,
            (jfloat)out_value_value.y,
            (jfloat)out_value_value.z
        };
        (*environment)->SetFloatArrayRegion(environment, out_valueFloating, 0,
            (jsize)(sizeof out_valueFloating_values / sizeof out_valueFloating_values[0]),
            out_valueFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeExtGetPosition(
    JNIEnv* environment, jclass declaring_class, jlong probe, jfloatArray out_positionFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Vector3 out_position_value;
    memset(&out_position_value, 0, sizeof out_position_value);
    CNA_Result call_result = cna.light_probe_ext_get_position((CNA_LightProbeHandle)probe, &out_position_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_positionFloating_values[] = {
            (jfloat)out_position_value.x,
            (jfloat)out_position_value.y,
            (jfloat)out_position_value.z
        };
        (*environment)->SetFloatArrayRegion(environment, out_positionFloating, 0,
            (jsize)(sizeof out_positionFloating_values / sizeof out_positionFloating_values[0]),
            out_positionFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeExtGetVisibilityMean(
    JNIEnv* environment, jclass declaring_class, jlong probe, jint direction, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.light_probe_ext_get_visibility_mean((CNA_LightProbeHandle)probe, (int32_t)direction, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeExtGetVisibilityMeanSquared(
    JNIEnv* environment, jclass declaring_class, jlong probe, jint direction, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.light_probe_ext_get_visibility_mean_squared((CNA_LightProbeHandle)probe, (int32_t)direction, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeExtHasVisibility(
    JNIEnv* environment, jclass declaring_class, jlong probe, jbooleanArray out_has)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_has_value = 0;
    CNA_Result call_result = cna.light_probe_ext_has_visibility((CNA_LightProbeHandle)probe, &out_has_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_has_element = (jboolean)out_has_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_has, 0, 1, &out_has_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeExtIrradiance(
    JNIEnv* environment, jclass declaring_class, jlong probe, jfloatArray normalFloating, jfloatArray out_irradianceFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Vector3 normal_value;
    memset(&normal_value, 0, sizeof normal_value);
    {
        jfloat normalFloating_values[3];
        (*environment)->GetFloatArrayRegion(environment, normalFloating, 0, 3, normalFloating_values);
        normal_value.x = (float)normalFloating_values[0];
        normal_value.y = (float)normalFloating_values[1];
        normal_value.z = (float)normalFloating_values[2];
    }
    CNA_Vector3 out_irradiance_value;
    memset(&out_irradiance_value, 0, sizeof out_irradiance_value);
    CNA_Result call_result = cna.light_probe_ext_irradiance((CNA_LightProbeHandle)probe, &normal_value, &out_irradiance_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_irradianceFloating_values[] = {
            (jfloat)out_irradiance_value.x,
            (jfloat)out_irradiance_value.y,
            (jfloat)out_irradiance_value.z
        };
        (*environment)->SetFloatArrayRegion(environment, out_irradianceFloating, 0,
            (jsize)(sizeof out_irradianceFloating_values / sizeof out_irradianceFloating_values[0]),
            out_irradianceFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeExtIsZero(
    JNIEnv* environment, jclass declaring_class, jlong probe, jbooleanArray out_zero)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_zero_value = 0;
    CNA_Result call_result = cna.light_probe_ext_is_zero((CNA_LightProbeHandle)probe, &out_zero_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_zero_element = (jboolean)out_zero_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_zero, 0, 1, &out_zero_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeExtScale(
    JNIEnv* environment, jclass declaring_class, jlong probe, jfloat factor)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.light_probe_ext_scale((CNA_LightProbeHandle)probe, (float)factor);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeExtSetCoefficient(
    JNIEnv* environment, jclass declaring_class, jlong probe, jint index, jfloatArray valueFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Vector3 value_value;
    memset(&value_value, 0, sizeof value_value);
    {
        jfloat valueFloating_values[3];
        (*environment)->GetFloatArrayRegion(environment, valueFloating, 0, 3, valueFloating_values);
        value_value.x = (float)valueFloating_values[0];
        value_value.y = (float)valueFloating_values[1];
        value_value.z = (float)valueFloating_values[2];
    }
    CNA_Result call_result = cna.light_probe_ext_set_coefficient((CNA_LightProbeHandle)probe, (int32_t)index, &value_value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeExtSetPosition(
    JNIEnv* environment, jclass declaring_class, jlong probe, jfloatArray positionFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Vector3 position_value;
    memset(&position_value, 0, sizeof position_value);
    {
        jfloat positionFloating_values[3];
        (*environment)->GetFloatArrayRegion(environment, positionFloating, 0, 3, positionFloating_values);
        position_value.x = (float)positionFloating_values[0];
        position_value.y = (float)positionFloating_values[1];
        position_value.z = (float)positionFloating_values[2];
    }
    CNA_Result call_result = cna.light_probe_ext_set_position((CNA_LightProbeHandle)probe, &position_value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeExtSetVisibility(
    JNIEnv* environment, jclass declaring_class, jlong probe, jint direction, jfloat mean_distance, jfloat mean_squared_distance)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.light_probe_ext_set_visibility((CNA_LightProbeHandle)probe, (int32_t)direction, (float)mean_distance, (float)mean_squared_distance);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeExtVisibilityWeight(
    JNIEnv* environment, jclass declaring_class, jlong probe, jfloatArray directionFloating, jfloat distance, jfloatArray out_weight)
{
    (void)environment;
    (void)declaring_class;
    CNA_Vector3 direction_value;
    memset(&direction_value, 0, sizeof direction_value);
    {
        jfloat directionFloating_values[3];
        (*environment)->GetFloatArrayRegion(environment, directionFloating, 0, 3, directionFloating_values);
        direction_value.x = (float)directionFloating_values[0];
        direction_value.y = (float)directionFloating_values[1];
        direction_value.z = (float)directionFloating_values[2];
    }
    float out_weight_value = 0;
    CNA_Result call_result = cna.light_probe_ext_visibility_weight((CNA_LightProbeHandle)probe, &direction_value, (float)distance, &out_weight_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_weight_element = (jfloat)out_weight_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_weight, 0, 1, &out_weight_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeVolumeExtContains(
    JNIEnv* environment, jclass declaring_class, jlong volume, jfloatArray positionFloating, jbooleanArray out_contains)
{
    (void)environment;
    (void)declaring_class;
    CNA_Vector3 position_value;
    memset(&position_value, 0, sizeof position_value);
    {
        jfloat positionFloating_values[3];
        (*environment)->GetFloatArrayRegion(environment, positionFloating, 0, 3, positionFloating_values);
        position_value.x = (float)positionFloating_values[0];
        position_value.y = (float)positionFloating_values[1];
        position_value.z = (float)positionFloating_values[2];
    }
    CNA_Bool out_contains_value = 0;
    CNA_Result call_result = cna.light_probe_volume_ext_contains((CNA_LightProbeVolumeHandle)volume, &position_value, &out_contains_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_contains_element = (jboolean)out_contains_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_contains, 0, 1, &out_contains_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeVolumeExtCreate(
    JNIEnv* environment, jclass declaring_class, jfloatArray boundsFloating, jint count_x, jint count_y, jint count_z, jlongArray out_volume)
{
    (void)environment;
    (void)declaring_class;
    CNA_BoundingBox bounds_value;
    memset(&bounds_value, 0, sizeof bounds_value);
    {
        jfloat boundsFloating_values[6];
        (*environment)->GetFloatArrayRegion(environment, boundsFloating, 0, 6, boundsFloating_values);
        bounds_value.min.x = (float)boundsFloating_values[0];
        bounds_value.min.y = (float)boundsFloating_values[1];
        bounds_value.min.z = (float)boundsFloating_values[2];
        bounds_value.max.x = (float)boundsFloating_values[3];
        bounds_value.max.y = (float)boundsFloating_values[4];
        bounds_value.max.z = (float)boundsFloating_values[5];
    }
    CNA_LightProbeVolumeHandle out_volume_value = 0;
    CNA_Result call_result = cna.light_probe_volume_ext_create(&bounds_value, (int32_t)count_x, (int32_t)count_y, (int32_t)count_z, &out_volume_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_volume_element = (jlong)out_volume_value;
        (*environment)->SetLongArrayRegion(
            environment, out_volume, 0, 1, &out_volume_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeVolumeExtDestroy(
    JNIEnv* environment, jclass declaring_class, jlong volume)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.light_probe_volume_ext_destroy((CNA_LightProbeVolumeHandle)volume);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeVolumeExtGetBounds(
    JNIEnv* environment, jclass declaring_class, jlong volume, jfloatArray out_boundsFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_BoundingBox out_bounds_value;
    memset(&out_bounds_value, 0, sizeof out_bounds_value);
    CNA_Result call_result = cna.light_probe_volume_ext_get_bounds((CNA_LightProbeVolumeHandle)volume, &out_bounds_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_boundsFloating_values[] = {
            (jfloat)out_bounds_value.min.x,
            (jfloat)out_bounds_value.min.y,
            (jfloat)out_bounds_value.min.z,
            (jfloat)out_bounds_value.max.x,
            (jfloat)out_bounds_value.max.y,
            (jfloat)out_bounds_value.max.z
        };
        (*environment)->SetFloatArrayRegion(environment, out_boundsFloating, 0,
            (jsize)(sizeof out_boundsFloating_values / sizeof out_boundsFloating_values[0]),
            out_boundsFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeVolumeExtGetCountX(
    JNIEnv* environment, jclass declaring_class, jlong volume, jintArray out_count)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_count_value = 0;
    CNA_Result call_result = cna.light_probe_volume_ext_get_count_x((CNA_LightProbeVolumeHandle)volume, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_count_element = (jint)out_count_value;
        (*environment)->SetIntArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeVolumeExtGetCountY(
    JNIEnv* environment, jclass declaring_class, jlong volume, jintArray out_count)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_count_value = 0;
    CNA_Result call_result = cna.light_probe_volume_ext_get_count_y((CNA_LightProbeVolumeHandle)volume, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_count_element = (jint)out_count_value;
        (*environment)->SetIntArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeVolumeExtGetCountZ(
    JNIEnv* environment, jclass declaring_class, jlong volume, jintArray out_count)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_count_value = 0;
    CNA_Result call_result = cna.light_probe_volume_ext_get_count_z((CNA_LightProbeVolumeHandle)volume, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_count_element = (jint)out_count_value;
        (*environment)->SetIntArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeVolumeExtGetProbe(
    JNIEnv* environment, jclass declaring_class, jlong volume, jint x, jint y, jint z, jlong out_probe)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.light_probe_volume_ext_get_probe((CNA_LightProbeVolumeHandle)volume, (int32_t)x, (int32_t)y, (int32_t)z, (CNA_LightProbeHandle)out_probe);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeVolumeExtGetProbeCount(
    JNIEnv* environment, jclass declaring_class, jlong volume, jintArray out_count)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_count_value = 0;
    CNA_Result call_result = cna.light_probe_volume_ext_get_probe_count((CNA_LightProbeVolumeHandle)volume, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_count_element = (jint)out_count_value;
        (*environment)->SetIntArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeVolumeExtGetProbePosition(
    JNIEnv* environment, jclass declaring_class, jlong volume, jint x, jint y, jint z, jfloatArray out_positionFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Vector3 out_position_value;
    memset(&out_position_value, 0, sizeof out_position_value);
    CNA_Result call_result = cna.light_probe_volume_ext_get_probe_position((CNA_LightProbeVolumeHandle)volume, (int32_t)x, (int32_t)y, (int32_t)z, &out_position_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_positionFloating_values[] = {
            (jfloat)out_position_value.x,
            (jfloat)out_position_value.y,
            (jfloat)out_position_value.z
        };
        (*environment)->SetFloatArrayRegion(environment, out_positionFloating, 0,
            (jsize)(sizeof out_positionFloating_values / sizeof out_positionFloating_values[0]),
            out_positionFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeVolumeExtIrradiance(
    JNIEnv* environment, jclass declaring_class, jlong volume, jfloatArray positionFloating, jfloatArray normalFloating, jfloatArray out_irradianceFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Vector3 position_value;
    memset(&position_value, 0, sizeof position_value);
    {
        jfloat positionFloating_values[3];
        (*environment)->GetFloatArrayRegion(environment, positionFloating, 0, 3, positionFloating_values);
        position_value.x = (float)positionFloating_values[0];
        position_value.y = (float)positionFloating_values[1];
        position_value.z = (float)positionFloating_values[2];
    }
    CNA_Vector3 normal_value;
    memset(&normal_value, 0, sizeof normal_value);
    {
        jfloat normalFloating_values[3];
        (*environment)->GetFloatArrayRegion(environment, normalFloating, 0, 3, normalFloating_values);
        normal_value.x = (float)normalFloating_values[0];
        normal_value.y = (float)normalFloating_values[1];
        normal_value.z = (float)normalFloating_values[2];
    }
    CNA_Vector3 out_irradiance_value;
    memset(&out_irradiance_value, 0, sizeof out_irradiance_value);
    CNA_Result call_result = cna.light_probe_volume_ext_irradiance((CNA_LightProbeVolumeHandle)volume, &position_value, &normal_value, &out_irradiance_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_irradianceFloating_values[] = {
            (jfloat)out_irradiance_value.x,
            (jfloat)out_irradiance_value.y,
            (jfloat)out_irradiance_value.z
        };
        (*environment)->SetFloatArrayRegion(environment, out_irradianceFloating, 0,
            (jsize)(sizeof out_irradianceFloating_values / sizeof out_irradianceFloating_values[0]),
            out_irradianceFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeVolumeExtIsZero(
    JNIEnv* environment, jclass declaring_class, jlong volume, jbooleanArray out_zero)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_zero_value = 0;
    CNA_Result call_result = cna.light_probe_volume_ext_is_zero((CNA_LightProbeVolumeHandle)volume, &out_zero_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_zero_element = (jboolean)out_zero_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_zero, 0, 1, &out_zero_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeVolumeExtSampleProbe(
    JNIEnv* environment, jclass declaring_class, jlong volume, jfloatArray positionFloating, jlong out_probe)
{
    (void)environment;
    (void)declaring_class;
    CNA_Vector3 position_value;
    memset(&position_value, 0, sizeof position_value);
    {
        jfloat positionFloating_values[3];
        (*environment)->GetFloatArrayRegion(environment, positionFloating, 0, 3, positionFloating_values);
        position_value.x = (float)positionFloating_values[0];
        position_value.y = (float)positionFloating_values[1];
        position_value.z = (float)positionFloating_values[2];
    }
    CNA_Result call_result = cna.light_probe_volume_ext_sample_probe((CNA_LightProbeVolumeHandle)volume, &position_value, (CNA_LightProbeHandle)out_probe);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightProbeVolumeExtSetProbe(
    JNIEnv* environment, jclass declaring_class, jlong volume, jint x, jint y, jint z, jlong probe)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.light_probe_volume_ext_set_probe((CNA_LightProbeVolumeHandle)volume, (int32_t)x, (int32_t)y, (int32_t)z, (CNA_LightProbeHandle)probe);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightShaftPassCreate(
    JNIEnv* environment, jclass declaring_class, jlong graphics_device, jlongArray out_pass)
{
    (void)environment;
    (void)declaring_class;
    CNA_PostProcessPassHandle out_pass_value = 0;
    CNA_Result call_result = cna.light_shaft_pass_create((CNA_Handle)graphics_device, &out_pass_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_pass_element = (jlong)out_pass_value;
        (*environment)->SetLongArrayRegion(
            environment, out_pass, 0, 1, &out_pass_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightShaftPassGetDecay(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.light_shaft_pass_get_decay((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightShaftPassGetIntensity(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.light_shaft_pass_get_intensity((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightShaftPassGetLightScreenPosition(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_valueFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Vector2 out_value_value;
    memset(&out_value_value, 0, sizeof out_value_value);
    CNA_Result call_result = cna.light_shaft_pass_get_light_screen_position((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_valueFloating_values[] = {
            (jfloat)out_value_value.x,
            (jfloat)out_value_value.y
        };
        (*environment)->SetFloatArrayRegion(environment, out_valueFloating, 0,
            (jsize)(sizeof out_valueFloating_values / sizeof out_valueFloating_values[0]),
            out_valueFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightShaftPassGetThreshold(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.light_shaft_pass_get_threshold((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightShaftPassSetDecay(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.light_shaft_pass_set_decay((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightShaftPassSetIntensity(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.light_shaft_pass_set_intensity((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightShaftPassSetLightScreenPosition(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray valueFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Vector2 value_value;
    memset(&value_value, 0, sizeof value_value);
    {
        jfloat valueFloating_values[2];
        (*environment)->GetFloatArrayRegion(environment, valueFloating, 0, 2, valueFloating_values);
        value_value.x = (float)valueFloating_values[0];
        value_value.y = (float)valueFloating_values[1];
    }
    CNA_Result call_result = cna.light_shaft_pass_set_light_screen_position((CNA_PostProcessPassHandle)pass, &value_value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_lightShaftPassSetThreshold(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.light_shaft_pass_set_threshold((CNA_PostProcessPassHandle)pass, (float)value);
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
    if ((*environment)->GetArrayLength(environment, destinationIntegral) != destination_count * 2 ||
        (*environment)->GetArrayLength(environment, destinationFloating) != destination_count * 1) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
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

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_motionBlurPassCreate(
    JNIEnv* environment, jclass declaring_class, jlong graphics_device, jlongArray out_pass)
{
    (void)environment;
    (void)declaring_class;
    CNA_PostProcessPassHandle out_pass_value = 0;
    CNA_Result call_result = cna.motion_blur_pass_create((CNA_Handle)graphics_device, &out_pass_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_pass_element = (jlong)out_pass_value;
        (*environment)->SetLongArrayRegion(
            environment, out_pass, 0, 1, &out_pass_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_motionBlurPassGetMaxDistance(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.motion_blur_pass_get_max_distance((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_motionBlurPassGetStrength(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.motion_blur_pass_get_strength((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_motionBlurPassSetMaxDistance(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.motion_blur_pass_set_max_distance((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_motionBlurPassSetStrength(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.motion_blur_pass_set_strength((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_pointLightExtInit(
    JNIEnv* environment, jclass declaring_class, jbyteArray out_lightBytes, jlongArray out_lightIntegral, jfloatArray out_lightFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_PointLightEXT out_light_value;
    memset(&out_light_value, 0, sizeof out_light_value);
    out_light_value.struct_size = (uint32_t)(sizeof out_light_value);
    out_light_value.struct_version = UINT32_C(1);
    CNA_Result call_result = cna.point_light_ext_init(&out_light_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jbyte out_lightBytes_values[] = {
            (jbyte)out_light_value.reserved[0],
            (jbyte)out_light_value.reserved[1],
            (jbyte)out_light_value.reserved[2]
        };
        (*environment)->SetByteArrayRegion(environment, out_lightBytes, 0,
            (jsize)(sizeof out_lightBytes_values / sizeof out_lightBytes_values[0]),
            out_lightBytes_values);
        jlong out_lightIntegral_values[] = {
            (jlong)out_light_value.casts_shadows
        };
        (*environment)->SetLongArrayRegion(environment, out_lightIntegral, 0,
            (jsize)(sizeof out_lightIntegral_values / sizeof out_lightIntegral_values[0]),
            out_lightIntegral_values);
        jfloat out_lightFloating_values[] = {
            (jfloat)out_light_value.position.x,
            (jfloat)out_light_value.position.y,
            (jfloat)out_light_value.position.z,
            (jfloat)out_light_value.color.x,
            (jfloat)out_light_value.color.y,
            (jfloat)out_light_value.color.z,
            (jfloat)out_light_value.intensity,
            (jfloat)out_light_value.range
        };
        (*environment)->SetFloatArrayRegion(environment, out_lightFloating, 0,
            (jsize)(sizeof out_lightFloating_values / sizeof out_lightFloating_values[0]),
            out_lightFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_postProcessPassCopyName(
    JNIEnv* environment, jclass declaring_class, jlong pass, jbyteArray destination, jlongArray out_bytes)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t destination_written = 0;
    CNA_Result call_result = cna.post_process_pass_copy_name((CNA_PostProcessPassHandle)pass, (char*)destination_bytes, (uint64_t)destination_size, &destination_written);
    (*environment)->ReleaseByteArrayElements(environment, destination, destination_bytes, 0);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong destination_element = (jlong)destination_written;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &destination_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_postProcessPassDestroy(
    JNIEnv* environment, jclass declaring_class, jlong pass)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.post_process_pass_destroy((CNA_PostProcessPassHandle)pass);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_postProcessPassIsSupported(
    JNIEnv* environment, jclass declaring_class, jlong pass, jlong graphics_device, jbooleanArray out_supported)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_supported_value = 0;
    CNA_Result call_result = cna.post_process_pass_is_supported((CNA_PostProcessPassHandle)pass, (CNA_Handle)graphics_device, &out_supported_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_supported_element = (jboolean)out_supported_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_supported, 0, 1, &out_supported_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_renderPipelineAddUserPass(
    JNIEnv* environment, jclass declaring_class, jlong pipeline, jlong pass)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.render_pipeline_add_user_pass((CNA_RenderPipelineHandle)pipeline, (CNA_PostProcessPassHandle)pass);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_renderPipelineBegin(
    JNIEnv* environment, jclass declaring_class, jlong pipeline, jlongArray clear_colorIntegral)
{
    (void)environment;
    (void)declaring_class;
    CNA_Color clear_color_value;
    memset(&clear_color_value, 0, sizeof clear_color_value);
    {
        jlong clear_colorIntegral_values[4];
        (*environment)->GetLongArrayRegion(environment, clear_colorIntegral, 0, 4, clear_colorIntegral_values);
        clear_color_value.r = (uint8_t)clear_colorIntegral_values[0];
        clear_color_value.g = (uint8_t)clear_colorIntegral_values[1];
        clear_color_value.b = (uint8_t)clear_colorIntegral_values[2];
        clear_color_value.a = (uint8_t)clear_colorIntegral_values[3];
    }
    CNA_Result call_result = cna.render_pipeline_begin((CNA_RenderPipelineHandle)pipeline, &clear_color_value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_renderPipelineClearUserPasses(
    JNIEnv* environment, jclass declaring_class, jlong pipeline)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.render_pipeline_clear_user_passes((CNA_RenderPipelineHandle)pipeline);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_renderPipelineCopyPassTimingNameExt(
    JNIEnv* environment, jclass declaring_class, jlong pipeline, jlong index, jbyteArray destination, jlongArray out_bytes)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t destination_written = 0;
    CNA_Result call_result = cna.render_pipeline_copy_pass_timing_name_ext((CNA_RenderPipelineHandle)pipeline, (uint64_t)index, (char*)destination_bytes, (uint64_t)destination_size, &destination_written);
    (*environment)->ReleaseByteArrayElements(environment, destination, destination_bytes, 0);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong destination_element = (jlong)destination_written;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &destination_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_renderPipelineCopyTransparencyFallbackReasonExt(
    JNIEnv* environment, jclass declaring_class, jlong pipeline, jbyteArray destination, jlongArray out_bytes)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t destination_written = 0;
    CNA_Result call_result = cna.render_pipeline_copy_transparency_fallback_reason_ext((CNA_RenderPipelineHandle)pipeline, (char*)destination_bytes, (uint64_t)destination_size, &destination_written);
    (*environment)->ReleaseByteArrayElements(environment, destination, destination_bytes, 0);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong destination_element = (jlong)destination_written;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &destination_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_renderPipelineCreate(
    JNIEnv* environment, jclass declaring_class, jlong graphics_device, jlongArray out_pipeline)
{
    (void)environment;
    (void)declaring_class;
    CNA_RenderPipelineHandle out_pipeline_value = 0;
    CNA_Result call_result = cna.render_pipeline_create((CNA_Handle)graphics_device, &out_pipeline_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_pipeline_element = (jlong)out_pipeline_value;
        (*environment)->SetLongArrayRegion(
            environment, out_pipeline, 0, 1, &out_pipeline_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_renderPipelineDestroy(
    JNIEnv* environment, jclass declaring_class, jlong pipeline)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.render_pipeline_destroy((CNA_RenderPipelineHandle)pipeline);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_renderPipelineDidShadowPassRun(
    JNIEnv* environment, jclass declaring_class, jlong pipeline, jbooleanArray out_ran)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_ran_value = 0;
    CNA_Result call_result = cna.render_pipeline_did_shadow_pass_run((CNA_RenderPipelineHandle)pipeline, &out_ran_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_ran_element = (jboolean)out_ran_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_ran, 0, 1, &out_ran_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_renderPipelineDidSkyboxDraw(
    JNIEnv* environment, jclass declaring_class, jlong pipeline, jbooleanArray out_drew)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_drew_value = 0;
    CNA_Result call_result = cna.render_pipeline_did_skybox_draw((CNA_RenderPipelineHandle)pipeline, &out_drew_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_drew_element = (jboolean)out_drew_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_drew, 0, 1, &out_drew_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_renderPipelineEnd(
    JNIEnv* environment, jclass declaring_class, jlong pipeline)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.render_pipeline_end((CNA_RenderPipelineHandle)pipeline);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_renderPipelineGetGpuMemoryEstimateBytes(
    JNIEnv* environment, jclass declaring_class, jlong pipeline, jlongArray out_bytes)
{
    (void)environment;
    (void)declaring_class;
    uint64_t out_bytes_value = 0;
    CNA_Result call_result = cna.render_pipeline_get_gpu_memory_estimate_bytes((CNA_RenderPipelineHandle)pipeline, &out_bytes_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_bytes_element = (jlong)out_bytes_value;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &out_bytes_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_renderPipelineGetLastFramePassCount(
    JNIEnv* environment, jclass declaring_class, jlong pipeline, jintArray out_count)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_count_value = 0;
    CNA_Result call_result = cna.render_pipeline_get_last_frame_pass_count((CNA_RenderPipelineHandle)pipeline, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_count_element = (jint)out_count_value;
        (*environment)->SetIntArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_renderPipelineGetPassTimingCountExt(
    JNIEnv* environment, jclass declaring_class, jlong pipeline, jlongArray out_count)
{
    (void)environment;
    (void)declaring_class;
    uint64_t out_count_value = 0;
    CNA_Result call_result = cna.render_pipeline_get_pass_timing_count_ext((CNA_RenderPipelineHandle)pipeline, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_count_element = (jlong)out_count_value;
        (*environment)->SetLongArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_renderPipelineGetPassTimingExt(
    JNIEnv* environment, jclass declaring_class, jlong pipeline, jlong index, jbyteArray out_timingBytes, jlongArray out_timingIntegral, jdoubleArray out_timingDoubles)
{
    (void)environment;
    (void)declaring_class;
    CNA_PassTimingEXT out_timing_value;
    memset(&out_timing_value, 0, sizeof out_timing_value);
    out_timing_value.struct_size = (uint32_t)(sizeof out_timing_value);
    out_timing_value.struct_version = UINT32_C(1);
    CNA_Result call_result = cna.render_pipeline_get_pass_timing_ext((CNA_RenderPipelineHandle)pipeline, (uint64_t)index, &out_timing_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jbyte out_timingBytes_values[] = {
            (jbyte)out_timing_value.reserved[0],
            (jbyte)out_timing_value.reserved[1],
            (jbyte)out_timing_value.reserved[2],
            (jbyte)out_timing_value.reserved[3]
        };
        (*environment)->SetByteArrayRegion(environment, out_timingBytes, 0,
            (jsize)(sizeof out_timingBytes_values / sizeof out_timingBytes_values[0]),
            out_timingBytes_values);
        jlong out_timingIntegral_values[] = {
            (jlong)out_timing_value.sample_count
        };
        (*environment)->SetLongArrayRegion(environment, out_timingIntegral, 0,
            (jsize)(sizeof out_timingIntegral_values / sizeof out_timingIntegral_values[0]),
            out_timingIntegral_values);
        jdouble out_timingDoubles_values[] = {
            (jdouble)out_timing_value.milliseconds
        };
        (*environment)->SetDoubleArrayRegion(environment, out_timingDoubles, 0,
            (jsize)(sizeof out_timingDoubles_values / sizeof out_timingDoubles_values[0]),
            out_timingDoubles_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_renderPipelineGetSceneTargetFormat(
    JNIEnv* environment, jclass declaring_class, jlong pipeline, jintArray out_format)
{
    (void)environment;
    (void)declaring_class;
    CNA_SurfaceFormat out_format_value = 0;
    CNA_Result call_result = cna.render_pipeline_get_scene_target_format((CNA_RenderPipelineHandle)pipeline, &out_format_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_format_element = (jint)out_format_value;
        (*environment)->SetIntArrayRegion(
            environment, out_format, 0, 1, &out_format_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_renderPipelineGetSettings(
    JNIEnv* environment, jclass declaring_class, jlong pipeline, jbyteArray out_settingsBytes, jlongArray out_settingsIntegral, jfloatArray out_settingsFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_RenderPipelineSettingsEXT out_settings_value;
    memset(&out_settings_value, 0, sizeof out_settings_value);
    out_settings_value.struct_size = (uint32_t)(sizeof out_settings_value);
    out_settings_value.struct_version = UINT32_C(1);
    CNA_Result call_result = cna.render_pipeline_get_settings((CNA_RenderPipelineHandle)pipeline, &out_settings_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jbyte out_settingsBytes_values[] = {
            (jbyte)out_settings_value.reserved[0],
            (jbyte)out_settings_value.reserved[1],
            (jbyte)out_settings_value.reserved[2],
            (jbyte)out_settings_value.reserved[3]
        };
        (*environment)->SetByteArrayRegion(environment, out_settingsBytes, 0,
            (jsize)(sizeof out_settingsBytes_values / sizeof out_settingsBytes_values[0]),
            out_settingsBytes_values);
        jlong out_settingsIntegral_values[] = {
            (jlong)out_settings_value.hdr_enabled,
            (jlong)out_settings_value.tonemapping_mode,
            (jlong)out_settings_value.bloom_enabled,
            (jlong)out_settings_value.bloom_iterations,
            (jlong)out_settings_value.ssao_enabled,
            (jlong)out_settings_value.transparency_mode,
            (jlong)out_settings_value.ssao_sample_count,
            (jlong)out_settings_value.ssr_enabled,
            (jlong)out_settings_value.ssr_step_count,
            (jlong)out_settings_value.color_grade_enabled,
            (jlong)out_settings_value.dof_enabled,
            (jlong)out_settings_value.fxaa_enabled,
            (jlong)out_settings_value.render_quality,
            (jlong)out_settings_value.shadow_quality,
            (jlong)out_settings_value.shadows_enabled
        };
        (*environment)->SetLongArrayRegion(environment, out_settingsIntegral, 0,
            (jsize)(sizeof out_settingsIntegral_values / sizeof out_settingsIntegral_values[0]),
            out_settingsIntegral_values);
        jfloat out_settingsFloating_values[] = {
            (jfloat)out_settings_value.exposure,
            (jfloat)out_settings_value.gamma,
            (jfloat)out_settings_value.bloom_intensity,
            (jfloat)out_settings_value.bloom_threshold,
            (jfloat)out_settings_value.ssao_radius,
            (jfloat)out_settings_value.ssao_intensity,
            (jfloat)out_settings_value.ssr_max_distance,
            (jfloat)out_settings_value.ssr_thickness,
            (jfloat)out_settings_value.ssr_depth_bias,
            (jfloat)out_settings_value.ssr_edge_fade,
            (jfloat)out_settings_value.volumetric_fog_density,
            (jfloat)out_settings_value.light_shaft_threshold,
            (jfloat)out_settings_value.light_shaft_intensity,
            (jfloat)out_settings_value.light_shaft_decay,
            (jfloat)out_settings_value.height_fog_density,
            (jfloat)out_settings_value.height_fog_falloff,
            (jfloat)out_settings_value.height_fog_base_height,
            (jfloat)out_settings_value.motion_blur_strength,
            (jfloat)out_settings_value.motion_blur_max_distance,
            (jfloat)out_settings_value.chromatic_aberration_strength,
            (jfloat)out_settings_value.film_grain_intensity,
            (jfloat)out_settings_value.lens_flare_threshold,
            (jfloat)out_settings_value.lens_flare_intensity,
            (jfloat)out_settings_value.lens_flare_dispersal,
            (jfloat)out_settings_value.color_grade_strength,
            (jfloat)out_settings_value.dof_focus_distance,
            (jfloat)out_settings_value.dof_focal_length,
            (jfloat)out_settings_value.doff_number,
            (jfloat)out_settings_value.dof_max_radius,
            (jfloat)out_settings_value.ssr_roughness_blur,
            (jfloat)out_settings_value.ssr_intensity,
            (jfloat)out_settings_value.fxaa_edge_threshold_ext
        };
        (*environment)->SetFloatArrayRegion(environment, out_settingsFloating, 0,
            (jsize)(sizeof out_settingsFloating_values / sizeof out_settingsFloating_values[0]),
            out_settingsFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_renderPipelineGetStatistics(
    JNIEnv* environment, jclass declaring_class, jlong pipeline, jbyteArray out_statisticsBytes, jlongArray out_statisticsIntegral)
{
    (void)environment;
    (void)declaring_class;
    CNA_RenderPipelineFrameStatisticsEXT out_statistics_value;
    memset(&out_statistics_value, 0, sizeof out_statistics_value);
    out_statistics_value.struct_size = (uint32_t)(sizeof out_statistics_value);
    out_statistics_value.struct_version = UINT32_C(1);
    CNA_Result call_result = cna.render_pipeline_get_statistics((CNA_RenderPipelineHandle)pipeline, &out_statistics_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jbyte out_statisticsBytes_values[] = {
            (jbyte)out_statistics_value.reserved[0],
            (jbyte)out_statistics_value.reserved[1]
        };
        (*environment)->SetByteArrayRegion(environment, out_statisticsBytes, 0,
            (jsize)(sizeof out_statisticsBytes_values / sizeof out_statisticsBytes_values[0]),
            out_statisticsBytes_values);
        jlong out_statisticsIntegral_values[] = {
            (jlong)out_statistics_value.passes_run,
            (jlong)out_statistics_value.target_switches,
            (jlong)out_statistics_value.used_scene_target,
            (jlong)out_statistics_value.drew_skybox,
            (jlong)out_statistics_value.gpu_memory_estimate_bytes
        };
        (*environment)->SetLongArrayRegion(environment, out_statisticsIntegral, 0,
            (jsize)(sizeof out_statisticsIntegral_values / sizeof out_statisticsIntegral_values[0]),
            out_statisticsIntegral_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_renderPipelineIsGpuTimingEnabledExt(
    JNIEnv* environment, jclass declaring_class, jlong pipeline, jbooleanArray out_enabled)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_enabled_value = 0;
    CNA_Result call_result = cna.render_pipeline_is_gpu_timing_enabled_ext((CNA_RenderPipelineHandle)pipeline, &out_enabled_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_enabled_element = (jboolean)out_enabled_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_enabled, 0, 1, &out_enabled_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_renderPipelineIsUsingSceneTarget(
    JNIEnv* environment, jclass declaring_class, jlong pipeline, jbooleanArray out_using)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_using_value = 0;
    CNA_Result call_result = cna.render_pipeline_is_using_scene_target((CNA_RenderPipelineHandle)pipeline, &out_using_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_using_element = (jboolean)out_using_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_using, 0, 1, &out_using_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_renderPipelineReleaseDeviceResourcesExt(
    JNIEnv* environment, jclass declaring_class, jlong pipeline)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.render_pipeline_release_device_resources_ext((CNA_RenderPipelineHandle)pipeline);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_renderPipelineResize(
    JNIEnv* environment, jclass declaring_class, jlong pipeline, jint width, jint height)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.render_pipeline_resize((CNA_RenderPipelineHandle)pipeline, (int32_t)width, (int32_t)height);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_renderPipelineSetCamera(
    JNIEnv* environment, jclass declaring_class, jlong pipeline, jfloatArray viewFloating, jfloatArray projectionFloating, jfloat near_plane, jfloat far_plane)
{
    (void)environment;
    (void)declaring_class;
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
    CNA_Result call_result = cna.render_pipeline_set_camera((CNA_RenderPipelineHandle)pipeline, &view_value, &projection_value, (float)near_plane, (float)far_plane);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_renderPipelineSetDepthNormalInputs(
    JNIEnv* environment, jclass declaring_class, jlong pipeline, jlong depth, jlong normals)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.render_pipeline_set_depth_normal_inputs((CNA_RenderPipelineHandle)pipeline, (CNA_Handle)depth, (CNA_Handle)normals);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_renderPipelineSetGpuTimingEnabledExt(
    JNIEnv* environment, jclass declaring_class, jlong pipeline, jboolean value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.render_pipeline_set_gpu_timing_enabled_ext((CNA_RenderPipelineHandle)pipeline, (CNA_Bool)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_renderPipelineSetSettings(
    JNIEnv* environment, jclass declaring_class, jlong pipeline, jbyteArray settingsBytes, jlongArray settingsIntegral, jfloatArray settingsFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_RenderPipelineSettingsEXT settings_value;
    memset(&settings_value, 0, sizeof settings_value);
    settings_value.struct_size = (uint32_t)(sizeof settings_value);
    settings_value.struct_version = UINT32_C(1);
    {
        jbyte settingsBytes_values[4];
        (*environment)->GetByteArrayRegion(environment, settingsBytes, 0, 4, settingsBytes_values);
        settings_value.reserved[0] = (uint8_t)settingsBytes_values[0];
        settings_value.reserved[1] = (uint8_t)settingsBytes_values[1];
        settings_value.reserved[2] = (uint8_t)settingsBytes_values[2];
        settings_value.reserved[3] = (uint8_t)settingsBytes_values[3];
    }
    {
        jlong settingsIntegral_values[15];
        (*environment)->GetLongArrayRegion(environment, settingsIntegral, 0, 15, settingsIntegral_values);
        settings_value.hdr_enabled = (CNA_Bool)settingsIntegral_values[0];
        settings_value.tonemapping_mode = (CNA_TonemappingMode)settingsIntegral_values[1];
        settings_value.bloom_enabled = (CNA_Bool)settingsIntegral_values[2];
        settings_value.bloom_iterations = (int32_t)settingsIntegral_values[3];
        settings_value.ssao_enabled = (CNA_Bool)settingsIntegral_values[4];
        settings_value.transparency_mode = (CNA_TransparencyMode)settingsIntegral_values[5];
        settings_value.ssao_sample_count = (int32_t)settingsIntegral_values[6];
        settings_value.ssr_enabled = (CNA_Bool)settingsIntegral_values[7];
        settings_value.ssr_step_count = (int32_t)settingsIntegral_values[8];
        settings_value.color_grade_enabled = (CNA_Bool)settingsIntegral_values[9];
        settings_value.dof_enabled = (CNA_Bool)settingsIntegral_values[10];
        settings_value.fxaa_enabled = (CNA_Bool)settingsIntegral_values[11];
        settings_value.render_quality = (CNA_RenderQuality)settingsIntegral_values[12];
        settings_value.shadow_quality = (CNA_ShadowQuality)settingsIntegral_values[13];
        settings_value.shadows_enabled = (CNA_Bool)settingsIntegral_values[14];
    }
    {
        jfloat settingsFloating_values[32];
        (*environment)->GetFloatArrayRegion(environment, settingsFloating, 0, 32, settingsFloating_values);
        settings_value.exposure = (float)settingsFloating_values[0];
        settings_value.gamma = (float)settingsFloating_values[1];
        settings_value.bloom_intensity = (float)settingsFloating_values[2];
        settings_value.bloom_threshold = (float)settingsFloating_values[3];
        settings_value.ssao_radius = (float)settingsFloating_values[4];
        settings_value.ssao_intensity = (float)settingsFloating_values[5];
        settings_value.ssr_max_distance = (float)settingsFloating_values[6];
        settings_value.ssr_thickness = (float)settingsFloating_values[7];
        settings_value.ssr_depth_bias = (float)settingsFloating_values[8];
        settings_value.ssr_edge_fade = (float)settingsFloating_values[9];
        settings_value.volumetric_fog_density = (float)settingsFloating_values[10];
        settings_value.light_shaft_threshold = (float)settingsFloating_values[11];
        settings_value.light_shaft_intensity = (float)settingsFloating_values[12];
        settings_value.light_shaft_decay = (float)settingsFloating_values[13];
        settings_value.height_fog_density = (float)settingsFloating_values[14];
        settings_value.height_fog_falloff = (float)settingsFloating_values[15];
        settings_value.height_fog_base_height = (float)settingsFloating_values[16];
        settings_value.motion_blur_strength = (float)settingsFloating_values[17];
        settings_value.motion_blur_max_distance = (float)settingsFloating_values[18];
        settings_value.chromatic_aberration_strength = (float)settingsFloating_values[19];
        settings_value.film_grain_intensity = (float)settingsFloating_values[20];
        settings_value.lens_flare_threshold = (float)settingsFloating_values[21];
        settings_value.lens_flare_intensity = (float)settingsFloating_values[22];
        settings_value.lens_flare_dispersal = (float)settingsFloating_values[23];
        settings_value.color_grade_strength = (float)settingsFloating_values[24];
        settings_value.dof_focus_distance = (float)settingsFloating_values[25];
        settings_value.dof_focal_length = (float)settingsFloating_values[26];
        settings_value.doff_number = (float)settingsFloating_values[27];
        settings_value.dof_max_radius = (float)settingsFloating_values[28];
        settings_value.ssr_roughness_blur = (float)settingsFloating_values[29];
        settings_value.ssr_intensity = (float)settingsFloating_values[30];
        settings_value.fxaa_edge_threshold_ext = (float)settingsFloating_values[31];
    }
    CNA_Result call_result = cna.render_pipeline_set_settings((CNA_RenderPipelineHandle)pipeline, &settings_value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_renderPipelineSetSkyboxCamera(
    JNIEnv* environment, jclass declaring_class, jlong pipeline, jfloatArray viewFloating, jfloatArray projectionFloating)
{
    (void)environment;
    (void)declaring_class;
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
    CNA_Result call_result = cna.render_pipeline_set_skybox_camera((CNA_RenderPipelineHandle)pipeline, &view_value, &projection_value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_renderPipelineSetVelocityInputExt(
    JNIEnv* environment, jclass declaring_class, jlong pipeline, jlong velocity)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.render_pipeline_set_velocity_input_ext((CNA_RenderPipelineHandle)pipeline, (CNA_Handle)velocity);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_renderPipelineSettingsExtApplyFromString(
    JNIEnv* environment, jclass declaring_class, jbyteArray settingsBytes, jlongArray settingsIntegral, jfloatArray settingsFloating, jbyteArray text, jintArray out_applied)
{
    (void)environment;
    (void)declaring_class;
    CNA_RenderPipelineSettingsEXT settings_value;
    memset(&settings_value, 0, sizeof settings_value);
    settings_value.struct_size = (uint32_t)(sizeof settings_value);
    settings_value.struct_version = UINT32_C(1);
    {
        jbyte settingsBytes_values[4];
        (*environment)->GetByteArrayRegion(environment, settingsBytes, 0, 4, settingsBytes_values);
        settings_value.reserved[0] = (uint8_t)settingsBytes_values[0];
        settings_value.reserved[1] = (uint8_t)settingsBytes_values[1];
        settings_value.reserved[2] = (uint8_t)settingsBytes_values[2];
        settings_value.reserved[3] = (uint8_t)settingsBytes_values[3];
    }
    {
        jlong settingsIntegral_values[15];
        (*environment)->GetLongArrayRegion(environment, settingsIntegral, 0, 15, settingsIntegral_values);
        settings_value.hdr_enabled = (CNA_Bool)settingsIntegral_values[0];
        settings_value.tonemapping_mode = (CNA_TonemappingMode)settingsIntegral_values[1];
        settings_value.bloom_enabled = (CNA_Bool)settingsIntegral_values[2];
        settings_value.bloom_iterations = (int32_t)settingsIntegral_values[3];
        settings_value.ssao_enabled = (CNA_Bool)settingsIntegral_values[4];
        settings_value.transparency_mode = (CNA_TransparencyMode)settingsIntegral_values[5];
        settings_value.ssao_sample_count = (int32_t)settingsIntegral_values[6];
        settings_value.ssr_enabled = (CNA_Bool)settingsIntegral_values[7];
        settings_value.ssr_step_count = (int32_t)settingsIntegral_values[8];
        settings_value.color_grade_enabled = (CNA_Bool)settingsIntegral_values[9];
        settings_value.dof_enabled = (CNA_Bool)settingsIntegral_values[10];
        settings_value.fxaa_enabled = (CNA_Bool)settingsIntegral_values[11];
        settings_value.render_quality = (CNA_RenderQuality)settingsIntegral_values[12];
        settings_value.shadow_quality = (CNA_ShadowQuality)settingsIntegral_values[13];
        settings_value.shadows_enabled = (CNA_Bool)settingsIntegral_values[14];
    }
    {
        jfloat settingsFloating_values[32];
        (*environment)->GetFloatArrayRegion(environment, settingsFloating, 0, 32, settingsFloating_values);
        settings_value.exposure = (float)settingsFloating_values[0];
        settings_value.gamma = (float)settingsFloating_values[1];
        settings_value.bloom_intensity = (float)settingsFloating_values[2];
        settings_value.bloom_threshold = (float)settingsFloating_values[3];
        settings_value.ssao_radius = (float)settingsFloating_values[4];
        settings_value.ssao_intensity = (float)settingsFloating_values[5];
        settings_value.ssr_max_distance = (float)settingsFloating_values[6];
        settings_value.ssr_thickness = (float)settingsFloating_values[7];
        settings_value.ssr_depth_bias = (float)settingsFloating_values[8];
        settings_value.ssr_edge_fade = (float)settingsFloating_values[9];
        settings_value.volumetric_fog_density = (float)settingsFloating_values[10];
        settings_value.light_shaft_threshold = (float)settingsFloating_values[11];
        settings_value.light_shaft_intensity = (float)settingsFloating_values[12];
        settings_value.light_shaft_decay = (float)settingsFloating_values[13];
        settings_value.height_fog_density = (float)settingsFloating_values[14];
        settings_value.height_fog_falloff = (float)settingsFloating_values[15];
        settings_value.height_fog_base_height = (float)settingsFloating_values[16];
        settings_value.motion_blur_strength = (float)settingsFloating_values[17];
        settings_value.motion_blur_max_distance = (float)settingsFloating_values[18];
        settings_value.chromatic_aberration_strength = (float)settingsFloating_values[19];
        settings_value.film_grain_intensity = (float)settingsFloating_values[20];
        settings_value.lens_flare_threshold = (float)settingsFloating_values[21];
        settings_value.lens_flare_intensity = (float)settingsFloating_values[22];
        settings_value.lens_flare_dispersal = (float)settingsFloating_values[23];
        settings_value.color_grade_strength = (float)settingsFloating_values[24];
        settings_value.dof_focus_distance = (float)settingsFloating_values[25];
        settings_value.dof_focal_length = (float)settingsFloating_values[26];
        settings_value.doff_number = (float)settingsFloating_values[27];
        settings_value.dof_max_radius = (float)settingsFloating_values[28];
        settings_value.ssr_roughness_blur = (float)settingsFloating_values[29];
        settings_value.ssr_intensity = (float)settingsFloating_values[30];
        settings_value.fxaa_edge_threshold_ext = (float)settingsFloating_values[31];
    }
    jsize text_size = (*environment)->GetArrayLength(environment, text);
    jbyte* text_bytes = (*environment)->GetByteArrayElements(environment, text, NULL);
    if (text_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    CNA_StringView text_view = {(const char*)text_bytes, (uint64_t)text_size};
    int32_t out_applied_value = 0;
    CNA_Result call_result = cna.render_pipeline_settings_ext_apply_from_string(&settings_value, text_view, &out_applied_value);
    (*environment)->ReleaseByteArrayElements(environment, text, text_bytes, JNI_ABORT);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jbyte settingsBytes_values[] = {
            (jbyte)settings_value.reserved[0],
            (jbyte)settings_value.reserved[1],
            (jbyte)settings_value.reserved[2],
            (jbyte)settings_value.reserved[3]
        };
        (*environment)->SetByteArrayRegion(environment, settingsBytes, 0,
            (jsize)(sizeof settingsBytes_values / sizeof settingsBytes_values[0]),
            settingsBytes_values);
        jlong settingsIntegral_values[] = {
            (jlong)settings_value.hdr_enabled,
            (jlong)settings_value.tonemapping_mode,
            (jlong)settings_value.bloom_enabled,
            (jlong)settings_value.bloom_iterations,
            (jlong)settings_value.ssao_enabled,
            (jlong)settings_value.transparency_mode,
            (jlong)settings_value.ssao_sample_count,
            (jlong)settings_value.ssr_enabled,
            (jlong)settings_value.ssr_step_count,
            (jlong)settings_value.color_grade_enabled,
            (jlong)settings_value.dof_enabled,
            (jlong)settings_value.fxaa_enabled,
            (jlong)settings_value.render_quality,
            (jlong)settings_value.shadow_quality,
            (jlong)settings_value.shadows_enabled
        };
        (*environment)->SetLongArrayRegion(environment, settingsIntegral, 0,
            (jsize)(sizeof settingsIntegral_values / sizeof settingsIntegral_values[0]),
            settingsIntegral_values);
        jfloat settingsFloating_values[] = {
            (jfloat)settings_value.exposure,
            (jfloat)settings_value.gamma,
            (jfloat)settings_value.bloom_intensity,
            (jfloat)settings_value.bloom_threshold,
            (jfloat)settings_value.ssao_radius,
            (jfloat)settings_value.ssao_intensity,
            (jfloat)settings_value.ssr_max_distance,
            (jfloat)settings_value.ssr_thickness,
            (jfloat)settings_value.ssr_depth_bias,
            (jfloat)settings_value.ssr_edge_fade,
            (jfloat)settings_value.volumetric_fog_density,
            (jfloat)settings_value.light_shaft_threshold,
            (jfloat)settings_value.light_shaft_intensity,
            (jfloat)settings_value.light_shaft_decay,
            (jfloat)settings_value.height_fog_density,
            (jfloat)settings_value.height_fog_falloff,
            (jfloat)settings_value.height_fog_base_height,
            (jfloat)settings_value.motion_blur_strength,
            (jfloat)settings_value.motion_blur_max_distance,
            (jfloat)settings_value.chromatic_aberration_strength,
            (jfloat)settings_value.film_grain_intensity,
            (jfloat)settings_value.lens_flare_threshold,
            (jfloat)settings_value.lens_flare_intensity,
            (jfloat)settings_value.lens_flare_dispersal,
            (jfloat)settings_value.color_grade_strength,
            (jfloat)settings_value.dof_focus_distance,
            (jfloat)settings_value.dof_focal_length,
            (jfloat)settings_value.doff_number,
            (jfloat)settings_value.dof_max_radius,
            (jfloat)settings_value.ssr_roughness_blur,
            (jfloat)settings_value.ssr_intensity,
            (jfloat)settings_value.fxaa_edge_threshold_ext
        };
        (*environment)->SetFloatArrayRegion(environment, settingsFloating, 0,
            (jsize)(sizeof settingsFloating_values / sizeof settingsFloating_values[0]),
            settingsFloating_values);
        jint out_applied_element = (jint)out_applied_value;
        (*environment)->SetIntArrayRegion(
            environment, out_applied, 0, 1, &out_applied_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_renderPipelineSettingsExtApplyRenderQualityPreset(
    JNIEnv* environment, jclass declaring_class, jbyteArray settingsBytes, jlongArray settingsIntegral, jfloatArray settingsFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_RenderPipelineSettingsEXT settings_value;
    memset(&settings_value, 0, sizeof settings_value);
    settings_value.struct_size = (uint32_t)(sizeof settings_value);
    settings_value.struct_version = UINT32_C(1);
    {
        jbyte settingsBytes_values[4];
        (*environment)->GetByteArrayRegion(environment, settingsBytes, 0, 4, settingsBytes_values);
        settings_value.reserved[0] = (uint8_t)settingsBytes_values[0];
        settings_value.reserved[1] = (uint8_t)settingsBytes_values[1];
        settings_value.reserved[2] = (uint8_t)settingsBytes_values[2];
        settings_value.reserved[3] = (uint8_t)settingsBytes_values[3];
    }
    {
        jlong settingsIntegral_values[15];
        (*environment)->GetLongArrayRegion(environment, settingsIntegral, 0, 15, settingsIntegral_values);
        settings_value.hdr_enabled = (CNA_Bool)settingsIntegral_values[0];
        settings_value.tonemapping_mode = (CNA_TonemappingMode)settingsIntegral_values[1];
        settings_value.bloom_enabled = (CNA_Bool)settingsIntegral_values[2];
        settings_value.bloom_iterations = (int32_t)settingsIntegral_values[3];
        settings_value.ssao_enabled = (CNA_Bool)settingsIntegral_values[4];
        settings_value.transparency_mode = (CNA_TransparencyMode)settingsIntegral_values[5];
        settings_value.ssao_sample_count = (int32_t)settingsIntegral_values[6];
        settings_value.ssr_enabled = (CNA_Bool)settingsIntegral_values[7];
        settings_value.ssr_step_count = (int32_t)settingsIntegral_values[8];
        settings_value.color_grade_enabled = (CNA_Bool)settingsIntegral_values[9];
        settings_value.dof_enabled = (CNA_Bool)settingsIntegral_values[10];
        settings_value.fxaa_enabled = (CNA_Bool)settingsIntegral_values[11];
        settings_value.render_quality = (CNA_RenderQuality)settingsIntegral_values[12];
        settings_value.shadow_quality = (CNA_ShadowQuality)settingsIntegral_values[13];
        settings_value.shadows_enabled = (CNA_Bool)settingsIntegral_values[14];
    }
    {
        jfloat settingsFloating_values[32];
        (*environment)->GetFloatArrayRegion(environment, settingsFloating, 0, 32, settingsFloating_values);
        settings_value.exposure = (float)settingsFloating_values[0];
        settings_value.gamma = (float)settingsFloating_values[1];
        settings_value.bloom_intensity = (float)settingsFloating_values[2];
        settings_value.bloom_threshold = (float)settingsFloating_values[3];
        settings_value.ssao_radius = (float)settingsFloating_values[4];
        settings_value.ssao_intensity = (float)settingsFloating_values[5];
        settings_value.ssr_max_distance = (float)settingsFloating_values[6];
        settings_value.ssr_thickness = (float)settingsFloating_values[7];
        settings_value.ssr_depth_bias = (float)settingsFloating_values[8];
        settings_value.ssr_edge_fade = (float)settingsFloating_values[9];
        settings_value.volumetric_fog_density = (float)settingsFloating_values[10];
        settings_value.light_shaft_threshold = (float)settingsFloating_values[11];
        settings_value.light_shaft_intensity = (float)settingsFloating_values[12];
        settings_value.light_shaft_decay = (float)settingsFloating_values[13];
        settings_value.height_fog_density = (float)settingsFloating_values[14];
        settings_value.height_fog_falloff = (float)settingsFloating_values[15];
        settings_value.height_fog_base_height = (float)settingsFloating_values[16];
        settings_value.motion_blur_strength = (float)settingsFloating_values[17];
        settings_value.motion_blur_max_distance = (float)settingsFloating_values[18];
        settings_value.chromatic_aberration_strength = (float)settingsFloating_values[19];
        settings_value.film_grain_intensity = (float)settingsFloating_values[20];
        settings_value.lens_flare_threshold = (float)settingsFloating_values[21];
        settings_value.lens_flare_intensity = (float)settingsFloating_values[22];
        settings_value.lens_flare_dispersal = (float)settingsFloating_values[23];
        settings_value.color_grade_strength = (float)settingsFloating_values[24];
        settings_value.dof_focus_distance = (float)settingsFloating_values[25];
        settings_value.dof_focal_length = (float)settingsFloating_values[26];
        settings_value.doff_number = (float)settingsFloating_values[27];
        settings_value.dof_max_radius = (float)settingsFloating_values[28];
        settings_value.ssr_roughness_blur = (float)settingsFloating_values[29];
        settings_value.ssr_intensity = (float)settingsFloating_values[30];
        settings_value.fxaa_edge_threshold_ext = (float)settingsFloating_values[31];
    }
    CNA_Result call_result = cna.render_pipeline_settings_ext_apply_render_quality_preset(&settings_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jbyte settingsBytes_values[] = {
            (jbyte)settings_value.reserved[0],
            (jbyte)settings_value.reserved[1],
            (jbyte)settings_value.reserved[2],
            (jbyte)settings_value.reserved[3]
        };
        (*environment)->SetByteArrayRegion(environment, settingsBytes, 0,
            (jsize)(sizeof settingsBytes_values / sizeof settingsBytes_values[0]),
            settingsBytes_values);
        jlong settingsIntegral_values[] = {
            (jlong)settings_value.hdr_enabled,
            (jlong)settings_value.tonemapping_mode,
            (jlong)settings_value.bloom_enabled,
            (jlong)settings_value.bloom_iterations,
            (jlong)settings_value.ssao_enabled,
            (jlong)settings_value.transparency_mode,
            (jlong)settings_value.ssao_sample_count,
            (jlong)settings_value.ssr_enabled,
            (jlong)settings_value.ssr_step_count,
            (jlong)settings_value.color_grade_enabled,
            (jlong)settings_value.dof_enabled,
            (jlong)settings_value.fxaa_enabled,
            (jlong)settings_value.render_quality,
            (jlong)settings_value.shadow_quality,
            (jlong)settings_value.shadows_enabled
        };
        (*environment)->SetLongArrayRegion(environment, settingsIntegral, 0,
            (jsize)(sizeof settingsIntegral_values / sizeof settingsIntegral_values[0]),
            settingsIntegral_values);
        jfloat settingsFloating_values[] = {
            (jfloat)settings_value.exposure,
            (jfloat)settings_value.gamma,
            (jfloat)settings_value.bloom_intensity,
            (jfloat)settings_value.bloom_threshold,
            (jfloat)settings_value.ssao_radius,
            (jfloat)settings_value.ssao_intensity,
            (jfloat)settings_value.ssr_max_distance,
            (jfloat)settings_value.ssr_thickness,
            (jfloat)settings_value.ssr_depth_bias,
            (jfloat)settings_value.ssr_edge_fade,
            (jfloat)settings_value.volumetric_fog_density,
            (jfloat)settings_value.light_shaft_threshold,
            (jfloat)settings_value.light_shaft_intensity,
            (jfloat)settings_value.light_shaft_decay,
            (jfloat)settings_value.height_fog_density,
            (jfloat)settings_value.height_fog_falloff,
            (jfloat)settings_value.height_fog_base_height,
            (jfloat)settings_value.motion_blur_strength,
            (jfloat)settings_value.motion_blur_max_distance,
            (jfloat)settings_value.chromatic_aberration_strength,
            (jfloat)settings_value.film_grain_intensity,
            (jfloat)settings_value.lens_flare_threshold,
            (jfloat)settings_value.lens_flare_intensity,
            (jfloat)settings_value.lens_flare_dispersal,
            (jfloat)settings_value.color_grade_strength,
            (jfloat)settings_value.dof_focus_distance,
            (jfloat)settings_value.dof_focal_length,
            (jfloat)settings_value.doff_number,
            (jfloat)settings_value.dof_max_radius,
            (jfloat)settings_value.ssr_roughness_blur,
            (jfloat)settings_value.ssr_intensity,
            (jfloat)settings_value.fxaa_edge_threshold_ext
        };
        (*environment)->SetFloatArrayRegion(environment, settingsFloating, 0,
            (jsize)(sizeof settingsFloating_values / sizeof settingsFloating_values[0]),
            settingsFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_renderPipelineSettingsExtInit(
    JNIEnv* environment, jclass declaring_class, jbyteArray out_settingsBytes, jlongArray out_settingsIntegral, jfloatArray out_settingsFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_RenderPipelineSettingsEXT out_settings_value;
    memset(&out_settings_value, 0, sizeof out_settings_value);
    out_settings_value.struct_size = (uint32_t)(sizeof out_settings_value);
    out_settings_value.struct_version = UINT32_C(1);
    CNA_Result call_result = cna.render_pipeline_settings_ext_init(&out_settings_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jbyte out_settingsBytes_values[] = {
            (jbyte)out_settings_value.reserved[0],
            (jbyte)out_settings_value.reserved[1],
            (jbyte)out_settings_value.reserved[2],
            (jbyte)out_settings_value.reserved[3]
        };
        (*environment)->SetByteArrayRegion(environment, out_settingsBytes, 0,
            (jsize)(sizeof out_settingsBytes_values / sizeof out_settingsBytes_values[0]),
            out_settingsBytes_values);
        jlong out_settingsIntegral_values[] = {
            (jlong)out_settings_value.hdr_enabled,
            (jlong)out_settings_value.tonemapping_mode,
            (jlong)out_settings_value.bloom_enabled,
            (jlong)out_settings_value.bloom_iterations,
            (jlong)out_settings_value.ssao_enabled,
            (jlong)out_settings_value.transparency_mode,
            (jlong)out_settings_value.ssao_sample_count,
            (jlong)out_settings_value.ssr_enabled,
            (jlong)out_settings_value.ssr_step_count,
            (jlong)out_settings_value.color_grade_enabled,
            (jlong)out_settings_value.dof_enabled,
            (jlong)out_settings_value.fxaa_enabled,
            (jlong)out_settings_value.render_quality,
            (jlong)out_settings_value.shadow_quality,
            (jlong)out_settings_value.shadows_enabled
        };
        (*environment)->SetLongArrayRegion(environment, out_settingsIntegral, 0,
            (jsize)(sizeof out_settingsIntegral_values / sizeof out_settingsIntegral_values[0]),
            out_settingsIntegral_values);
        jfloat out_settingsFloating_values[] = {
            (jfloat)out_settings_value.exposure,
            (jfloat)out_settings_value.gamma,
            (jfloat)out_settings_value.bloom_intensity,
            (jfloat)out_settings_value.bloom_threshold,
            (jfloat)out_settings_value.ssao_radius,
            (jfloat)out_settings_value.ssao_intensity,
            (jfloat)out_settings_value.ssr_max_distance,
            (jfloat)out_settings_value.ssr_thickness,
            (jfloat)out_settings_value.ssr_depth_bias,
            (jfloat)out_settings_value.ssr_edge_fade,
            (jfloat)out_settings_value.volumetric_fog_density,
            (jfloat)out_settings_value.light_shaft_threshold,
            (jfloat)out_settings_value.light_shaft_intensity,
            (jfloat)out_settings_value.light_shaft_decay,
            (jfloat)out_settings_value.height_fog_density,
            (jfloat)out_settings_value.height_fog_falloff,
            (jfloat)out_settings_value.height_fog_base_height,
            (jfloat)out_settings_value.motion_blur_strength,
            (jfloat)out_settings_value.motion_blur_max_distance,
            (jfloat)out_settings_value.chromatic_aberration_strength,
            (jfloat)out_settings_value.film_grain_intensity,
            (jfloat)out_settings_value.lens_flare_threshold,
            (jfloat)out_settings_value.lens_flare_intensity,
            (jfloat)out_settings_value.lens_flare_dispersal,
            (jfloat)out_settings_value.color_grade_strength,
            (jfloat)out_settings_value.dof_focus_distance,
            (jfloat)out_settings_value.dof_focal_length,
            (jfloat)out_settings_value.doff_number,
            (jfloat)out_settings_value.dof_max_radius,
            (jfloat)out_settings_value.ssr_roughness_blur,
            (jfloat)out_settings_value.ssr_intensity,
            (jfloat)out_settings_value.fxaa_edge_threshold_ext
        };
        (*environment)->SetFloatArrayRegion(environment, out_settingsFloating, 0,
            (jsize)(sizeof out_settingsFloating_values / sizeof out_settingsFloating_values[0]),
            out_settingsFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_renderPipelineSettingsExtNormalize(
    JNIEnv* environment, jclass declaring_class, jbyteArray settingsBytes, jlongArray settingsIntegral, jfloatArray settingsFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_RenderPipelineSettingsEXT settings_value;
    memset(&settings_value, 0, sizeof settings_value);
    settings_value.struct_size = (uint32_t)(sizeof settings_value);
    settings_value.struct_version = UINT32_C(1);
    {
        jbyte settingsBytes_values[4];
        (*environment)->GetByteArrayRegion(environment, settingsBytes, 0, 4, settingsBytes_values);
        settings_value.reserved[0] = (uint8_t)settingsBytes_values[0];
        settings_value.reserved[1] = (uint8_t)settingsBytes_values[1];
        settings_value.reserved[2] = (uint8_t)settingsBytes_values[2];
        settings_value.reserved[3] = (uint8_t)settingsBytes_values[3];
    }
    {
        jlong settingsIntegral_values[15];
        (*environment)->GetLongArrayRegion(environment, settingsIntegral, 0, 15, settingsIntegral_values);
        settings_value.hdr_enabled = (CNA_Bool)settingsIntegral_values[0];
        settings_value.tonemapping_mode = (CNA_TonemappingMode)settingsIntegral_values[1];
        settings_value.bloom_enabled = (CNA_Bool)settingsIntegral_values[2];
        settings_value.bloom_iterations = (int32_t)settingsIntegral_values[3];
        settings_value.ssao_enabled = (CNA_Bool)settingsIntegral_values[4];
        settings_value.transparency_mode = (CNA_TransparencyMode)settingsIntegral_values[5];
        settings_value.ssao_sample_count = (int32_t)settingsIntegral_values[6];
        settings_value.ssr_enabled = (CNA_Bool)settingsIntegral_values[7];
        settings_value.ssr_step_count = (int32_t)settingsIntegral_values[8];
        settings_value.color_grade_enabled = (CNA_Bool)settingsIntegral_values[9];
        settings_value.dof_enabled = (CNA_Bool)settingsIntegral_values[10];
        settings_value.fxaa_enabled = (CNA_Bool)settingsIntegral_values[11];
        settings_value.render_quality = (CNA_RenderQuality)settingsIntegral_values[12];
        settings_value.shadow_quality = (CNA_ShadowQuality)settingsIntegral_values[13];
        settings_value.shadows_enabled = (CNA_Bool)settingsIntegral_values[14];
    }
    {
        jfloat settingsFloating_values[32];
        (*environment)->GetFloatArrayRegion(environment, settingsFloating, 0, 32, settingsFloating_values);
        settings_value.exposure = (float)settingsFloating_values[0];
        settings_value.gamma = (float)settingsFloating_values[1];
        settings_value.bloom_intensity = (float)settingsFloating_values[2];
        settings_value.bloom_threshold = (float)settingsFloating_values[3];
        settings_value.ssao_radius = (float)settingsFloating_values[4];
        settings_value.ssao_intensity = (float)settingsFloating_values[5];
        settings_value.ssr_max_distance = (float)settingsFloating_values[6];
        settings_value.ssr_thickness = (float)settingsFloating_values[7];
        settings_value.ssr_depth_bias = (float)settingsFloating_values[8];
        settings_value.ssr_edge_fade = (float)settingsFloating_values[9];
        settings_value.volumetric_fog_density = (float)settingsFloating_values[10];
        settings_value.light_shaft_threshold = (float)settingsFloating_values[11];
        settings_value.light_shaft_intensity = (float)settingsFloating_values[12];
        settings_value.light_shaft_decay = (float)settingsFloating_values[13];
        settings_value.height_fog_density = (float)settingsFloating_values[14];
        settings_value.height_fog_falloff = (float)settingsFloating_values[15];
        settings_value.height_fog_base_height = (float)settingsFloating_values[16];
        settings_value.motion_blur_strength = (float)settingsFloating_values[17];
        settings_value.motion_blur_max_distance = (float)settingsFloating_values[18];
        settings_value.chromatic_aberration_strength = (float)settingsFloating_values[19];
        settings_value.film_grain_intensity = (float)settingsFloating_values[20];
        settings_value.lens_flare_threshold = (float)settingsFloating_values[21];
        settings_value.lens_flare_intensity = (float)settingsFloating_values[22];
        settings_value.lens_flare_dispersal = (float)settingsFloating_values[23];
        settings_value.color_grade_strength = (float)settingsFloating_values[24];
        settings_value.dof_focus_distance = (float)settingsFloating_values[25];
        settings_value.dof_focal_length = (float)settingsFloating_values[26];
        settings_value.doff_number = (float)settingsFloating_values[27];
        settings_value.dof_max_radius = (float)settingsFloating_values[28];
        settings_value.ssr_roughness_blur = (float)settingsFloating_values[29];
        settings_value.ssr_intensity = (float)settingsFloating_values[30];
        settings_value.fxaa_edge_threshold_ext = (float)settingsFloating_values[31];
    }
    CNA_Result call_result = cna.render_pipeline_settings_ext_normalize(&settings_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jbyte settingsBytes_values[] = {
            (jbyte)settings_value.reserved[0],
            (jbyte)settings_value.reserved[1],
            (jbyte)settings_value.reserved[2],
            (jbyte)settings_value.reserved[3]
        };
        (*environment)->SetByteArrayRegion(environment, settingsBytes, 0,
            (jsize)(sizeof settingsBytes_values / sizeof settingsBytes_values[0]),
            settingsBytes_values);
        jlong settingsIntegral_values[] = {
            (jlong)settings_value.hdr_enabled,
            (jlong)settings_value.tonemapping_mode,
            (jlong)settings_value.bloom_enabled,
            (jlong)settings_value.bloom_iterations,
            (jlong)settings_value.ssao_enabled,
            (jlong)settings_value.transparency_mode,
            (jlong)settings_value.ssao_sample_count,
            (jlong)settings_value.ssr_enabled,
            (jlong)settings_value.ssr_step_count,
            (jlong)settings_value.color_grade_enabled,
            (jlong)settings_value.dof_enabled,
            (jlong)settings_value.fxaa_enabled,
            (jlong)settings_value.render_quality,
            (jlong)settings_value.shadow_quality,
            (jlong)settings_value.shadows_enabled
        };
        (*environment)->SetLongArrayRegion(environment, settingsIntegral, 0,
            (jsize)(sizeof settingsIntegral_values / sizeof settingsIntegral_values[0]),
            settingsIntegral_values);
        jfloat settingsFloating_values[] = {
            (jfloat)settings_value.exposure,
            (jfloat)settings_value.gamma,
            (jfloat)settings_value.bloom_intensity,
            (jfloat)settings_value.bloom_threshold,
            (jfloat)settings_value.ssao_radius,
            (jfloat)settings_value.ssao_intensity,
            (jfloat)settings_value.ssr_max_distance,
            (jfloat)settings_value.ssr_thickness,
            (jfloat)settings_value.ssr_depth_bias,
            (jfloat)settings_value.ssr_edge_fade,
            (jfloat)settings_value.volumetric_fog_density,
            (jfloat)settings_value.light_shaft_threshold,
            (jfloat)settings_value.light_shaft_intensity,
            (jfloat)settings_value.light_shaft_decay,
            (jfloat)settings_value.height_fog_density,
            (jfloat)settings_value.height_fog_falloff,
            (jfloat)settings_value.height_fog_base_height,
            (jfloat)settings_value.motion_blur_strength,
            (jfloat)settings_value.motion_blur_max_distance,
            (jfloat)settings_value.chromatic_aberration_strength,
            (jfloat)settings_value.film_grain_intensity,
            (jfloat)settings_value.lens_flare_threshold,
            (jfloat)settings_value.lens_flare_intensity,
            (jfloat)settings_value.lens_flare_dispersal,
            (jfloat)settings_value.color_grade_strength,
            (jfloat)settings_value.dof_focus_distance,
            (jfloat)settings_value.dof_focal_length,
            (jfloat)settings_value.doff_number,
            (jfloat)settings_value.dof_max_radius,
            (jfloat)settings_value.ssr_roughness_blur,
            (jfloat)settings_value.ssr_intensity,
            (jfloat)settings_value.fxaa_edge_threshold_ext
        };
        (*environment)->SetFloatArrayRegion(environment, settingsFloating, 0,
            (jsize)(sizeof settingsFloating_values / sizeof settingsFloating_values[0]),
            settingsFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_shadowMapApplyCaster(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.shadow_map_apply_caster((CNA_ShadowMapHandle)shadow_map);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_shadowMapApplySkinnedCaster(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jfloatArray bone_transformsFloating, jint weights_per_vertex)
{
    (void)environment;
    (void)declaring_class;
    jsize bone_transforms_count = (*environment)->GetArrayLength(environment, bone_transformsFloating) / 16;
    if ((*environment)->GetArrayLength(environment, bone_transformsFloating) != bone_transforms_count * 16) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Matrix* bone_transforms_values = (CNA_Matrix*)calloc(
        (size_t)bone_transforms_count + 1U, sizeof(*bone_transforms_values));
    if (bone_transforms_values == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    {
        jsize bone_transformsFloating_length = (*environment)->GetArrayLength(environment, bone_transformsFloating);
        jfloat* bone_transformsFloating_values = (jfloat*)malloc(
            ((size_t)bone_transformsFloating_length + 1U) * sizeof(*bone_transformsFloating_values));
        if (bone_transformsFloating_values == NULL) {
            free(bone_transforms_values);
            return (jint)CNA_RESULT_OUT_OF_MEMORY;
        }
        (*environment)->GetFloatArrayRegion(environment, bone_transformsFloating, 0, bone_transformsFloating_length, bone_transformsFloating_values);
        for (jsize element = 0; element < bone_transforms_count; ++element) {
            bone_transforms_values[element].m11 = (float)bone_transformsFloating_values[element * 16 + 0];
            bone_transforms_values[element].m12 = (float)bone_transformsFloating_values[element * 16 + 1];
            bone_transforms_values[element].m13 = (float)bone_transformsFloating_values[element * 16 + 2];
            bone_transforms_values[element].m14 = (float)bone_transformsFloating_values[element * 16 + 3];
            bone_transforms_values[element].m21 = (float)bone_transformsFloating_values[element * 16 + 4];
            bone_transforms_values[element].m22 = (float)bone_transformsFloating_values[element * 16 + 5];
            bone_transforms_values[element].m23 = (float)bone_transformsFloating_values[element * 16 + 6];
            bone_transforms_values[element].m24 = (float)bone_transformsFloating_values[element * 16 + 7];
            bone_transforms_values[element].m31 = (float)bone_transformsFloating_values[element * 16 + 8];
            bone_transforms_values[element].m32 = (float)bone_transformsFloating_values[element * 16 + 9];
            bone_transforms_values[element].m33 = (float)bone_transformsFloating_values[element * 16 + 10];
            bone_transforms_values[element].m34 = (float)bone_transformsFloating_values[element * 16 + 11];
            bone_transforms_values[element].m41 = (float)bone_transformsFloating_values[element * 16 + 12];
            bone_transforms_values[element].m42 = (float)bone_transformsFloating_values[element * 16 + 13];
            bone_transforms_values[element].m43 = (float)bone_transformsFloating_values[element * 16 + 14];
            bone_transforms_values[element].m44 = (float)bone_transformsFloating_values[element * 16 + 15];
        }
        free(bone_transformsFloating_values);
    }
    CNA_Result call_result = cna.shadow_map_apply_skinned_caster((CNA_ShadowMapHandle)shadow_map, bone_transforms_values, (uint64_t)bone_transforms_count, (int32_t)weights_per_vertex);
    free(bone_transforms_values);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_shadowMapBegin(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jbyteArray lightBytes, jlongArray lightIntegral, jfloatArray lightFloating, jfloatArray scene_boundsFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_DirectionalLightEXT light_value;
    memset(&light_value, 0, sizeof light_value);
    light_value.struct_size = (uint32_t)(sizeof light_value);
    light_value.struct_version = UINT32_C(1);
    {
        jbyte lightBytes_values[3];
        (*environment)->GetByteArrayRegion(environment, lightBytes, 0, 3, lightBytes_values);
        light_value.reserved[0] = (uint8_t)lightBytes_values[0];
        light_value.reserved[1] = (uint8_t)lightBytes_values[1];
        light_value.reserved[2] = (uint8_t)lightBytes_values[2];
    }
    {
        jlong lightIntegral_values[1];
        (*environment)->GetLongArrayRegion(environment, lightIntegral, 0, 1, lightIntegral_values);
        light_value.casts_shadows = (CNA_Bool)lightIntegral_values[0];
    }
    {
        jfloat lightFloating_values[7];
        (*environment)->GetFloatArrayRegion(environment, lightFloating, 0, 7, lightFloating_values);
        light_value.direction.x = (float)lightFloating_values[0];
        light_value.direction.y = (float)lightFloating_values[1];
        light_value.direction.z = (float)lightFloating_values[2];
        light_value.color.x = (float)lightFloating_values[3];
        light_value.color.y = (float)lightFloating_values[4];
        light_value.color.z = (float)lightFloating_values[5];
        light_value.intensity = (float)lightFloating_values[6];
    }
    CNA_BoundingBox scene_bounds_value;
    memset(&scene_bounds_value, 0, sizeof scene_bounds_value);
    {
        jfloat scene_boundsFloating_values[6];
        (*environment)->GetFloatArrayRegion(environment, scene_boundsFloating, 0, 6, scene_boundsFloating_values);
        scene_bounds_value.min.x = (float)scene_boundsFloating_values[0];
        scene_bounds_value.min.y = (float)scene_boundsFloating_values[1];
        scene_bounds_value.min.z = (float)scene_boundsFloating_values[2];
        scene_bounds_value.max.x = (float)scene_boundsFloating_values[3];
        scene_bounds_value.max.y = (float)scene_boundsFloating_values[4];
        scene_bounds_value.max.z = (float)scene_boundsFloating_values[5];
    }
    CNA_Result call_result = cna.shadow_map_begin((CNA_ShadowMapHandle)shadow_map, &light_value, &scene_bounds_value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_shadowMapComputeLightProjection(
    JNIEnv* environment, jclass declaring_class, jfloatArray light_viewFloating, jfloatArray scene_boundsFloating, jfloatArray out_matrixFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Matrix light_view_value;
    memset(&light_view_value, 0, sizeof light_view_value);
    {
        jfloat light_viewFloating_values[16];
        (*environment)->GetFloatArrayRegion(environment, light_viewFloating, 0, 16, light_viewFloating_values);
        light_view_value.m11 = (float)light_viewFloating_values[0];
        light_view_value.m12 = (float)light_viewFloating_values[1];
        light_view_value.m13 = (float)light_viewFloating_values[2];
        light_view_value.m14 = (float)light_viewFloating_values[3];
        light_view_value.m21 = (float)light_viewFloating_values[4];
        light_view_value.m22 = (float)light_viewFloating_values[5];
        light_view_value.m23 = (float)light_viewFloating_values[6];
        light_view_value.m24 = (float)light_viewFloating_values[7];
        light_view_value.m31 = (float)light_viewFloating_values[8];
        light_view_value.m32 = (float)light_viewFloating_values[9];
        light_view_value.m33 = (float)light_viewFloating_values[10];
        light_view_value.m34 = (float)light_viewFloating_values[11];
        light_view_value.m41 = (float)light_viewFloating_values[12];
        light_view_value.m42 = (float)light_viewFloating_values[13];
        light_view_value.m43 = (float)light_viewFloating_values[14];
        light_view_value.m44 = (float)light_viewFloating_values[15];
    }
    CNA_BoundingBox scene_bounds_value;
    memset(&scene_bounds_value, 0, sizeof scene_bounds_value);
    {
        jfloat scene_boundsFloating_values[6];
        (*environment)->GetFloatArrayRegion(environment, scene_boundsFloating, 0, 6, scene_boundsFloating_values);
        scene_bounds_value.min.x = (float)scene_boundsFloating_values[0];
        scene_bounds_value.min.y = (float)scene_boundsFloating_values[1];
        scene_bounds_value.min.z = (float)scene_boundsFloating_values[2];
        scene_bounds_value.max.x = (float)scene_boundsFloating_values[3];
        scene_bounds_value.max.y = (float)scene_boundsFloating_values[4];
        scene_bounds_value.max.z = (float)scene_boundsFloating_values[5];
    }
    CNA_Matrix out_matrix_value;
    memset(&out_matrix_value, 0, sizeof out_matrix_value);
    CNA_Result call_result = cna.shadow_map_compute_light_projection(&light_view_value, &scene_bounds_value, &out_matrix_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_matrixFloating_values[] = {
            (jfloat)out_matrix_value.m11,
            (jfloat)out_matrix_value.m12,
            (jfloat)out_matrix_value.m13,
            (jfloat)out_matrix_value.m14,
            (jfloat)out_matrix_value.m21,
            (jfloat)out_matrix_value.m22,
            (jfloat)out_matrix_value.m23,
            (jfloat)out_matrix_value.m24,
            (jfloat)out_matrix_value.m31,
            (jfloat)out_matrix_value.m32,
            (jfloat)out_matrix_value.m33,
            (jfloat)out_matrix_value.m34,
            (jfloat)out_matrix_value.m41,
            (jfloat)out_matrix_value.m42,
            (jfloat)out_matrix_value.m43,
            (jfloat)out_matrix_value.m44
        };
        (*environment)->SetFloatArrayRegion(environment, out_matrixFloating, 0,
            (jsize)(sizeof out_matrixFloating_values / sizeof out_matrixFloating_values[0]),
            out_matrixFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_shadowMapComputeLightView(
    JNIEnv* environment, jclass declaring_class, jbyteArray lightBytes, jlongArray lightIntegral, jfloatArray lightFloating, jfloatArray scene_boundsFloating, jfloatArray out_matrixFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_DirectionalLightEXT light_value;
    memset(&light_value, 0, sizeof light_value);
    light_value.struct_size = (uint32_t)(sizeof light_value);
    light_value.struct_version = UINT32_C(1);
    {
        jbyte lightBytes_values[3];
        (*environment)->GetByteArrayRegion(environment, lightBytes, 0, 3, lightBytes_values);
        light_value.reserved[0] = (uint8_t)lightBytes_values[0];
        light_value.reserved[1] = (uint8_t)lightBytes_values[1];
        light_value.reserved[2] = (uint8_t)lightBytes_values[2];
    }
    {
        jlong lightIntegral_values[1];
        (*environment)->GetLongArrayRegion(environment, lightIntegral, 0, 1, lightIntegral_values);
        light_value.casts_shadows = (CNA_Bool)lightIntegral_values[0];
    }
    {
        jfloat lightFloating_values[7];
        (*environment)->GetFloatArrayRegion(environment, lightFloating, 0, 7, lightFloating_values);
        light_value.direction.x = (float)lightFloating_values[0];
        light_value.direction.y = (float)lightFloating_values[1];
        light_value.direction.z = (float)lightFloating_values[2];
        light_value.color.x = (float)lightFloating_values[3];
        light_value.color.y = (float)lightFloating_values[4];
        light_value.color.z = (float)lightFloating_values[5];
        light_value.intensity = (float)lightFloating_values[6];
    }
    CNA_BoundingBox scene_bounds_value;
    memset(&scene_bounds_value, 0, sizeof scene_bounds_value);
    {
        jfloat scene_boundsFloating_values[6];
        (*environment)->GetFloatArrayRegion(environment, scene_boundsFloating, 0, 6, scene_boundsFloating_values);
        scene_bounds_value.min.x = (float)scene_boundsFloating_values[0];
        scene_bounds_value.min.y = (float)scene_boundsFloating_values[1];
        scene_bounds_value.min.z = (float)scene_boundsFloating_values[2];
        scene_bounds_value.max.x = (float)scene_boundsFloating_values[3];
        scene_bounds_value.max.y = (float)scene_boundsFloating_values[4];
        scene_bounds_value.max.z = (float)scene_boundsFloating_values[5];
    }
    CNA_Matrix out_matrix_value;
    memset(&out_matrix_value, 0, sizeof out_matrix_value);
    CNA_Result call_result = cna.shadow_map_compute_light_view(&light_value, &scene_bounds_value, &out_matrix_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_matrixFloating_values[] = {
            (jfloat)out_matrix_value.m11,
            (jfloat)out_matrix_value.m12,
            (jfloat)out_matrix_value.m13,
            (jfloat)out_matrix_value.m14,
            (jfloat)out_matrix_value.m21,
            (jfloat)out_matrix_value.m22,
            (jfloat)out_matrix_value.m23,
            (jfloat)out_matrix_value.m24,
            (jfloat)out_matrix_value.m31,
            (jfloat)out_matrix_value.m32,
            (jfloat)out_matrix_value.m33,
            (jfloat)out_matrix_value.m34,
            (jfloat)out_matrix_value.m41,
            (jfloat)out_matrix_value.m42,
            (jfloat)out_matrix_value.m43,
            (jfloat)out_matrix_value.m44
        };
        (*environment)->SetFloatArrayRegion(environment, out_matrixFloating, 0,
            (jsize)(sizeof out_matrixFloating_values / sizeof out_matrixFloating_values[0]),
            out_matrixFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_shadowMapCreate(
    JNIEnv* environment, jclass declaring_class, jlong graphics_device, jint quality, jlongArray out_shadow_map)
{
    (void)environment;
    (void)declaring_class;
    CNA_ShadowMapHandle out_shadow_map_value = 0;
    CNA_Result call_result = cna.shadow_map_create((CNA_Handle)graphics_device, (CNA_ShadowQuality)quality, &out_shadow_map_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_shadow_map_element = (jlong)out_shadow_map_value;
        (*environment)->SetLongArrayRegion(
            environment, out_shadow_map, 0, 1, &out_shadow_map_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_shadowMapDestroy(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.shadow_map_destroy((CNA_ShadowMapHandle)shadow_map);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_shadowMapEnd(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.shadow_map_end((CNA_ShadowMapHandle)shadow_map);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_shadowMapFilterRadiusForQuality(
    JNIEnv* environment, jclass declaring_class, jint quality, jintArray out_radius)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_radius_value = 0;
    CNA_Result call_result = cna.shadow_map_filter_radius_for_quality((CNA_ShadowQuality)quality, &out_radius_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_radius_element = (jint)out_radius_value;
        (*environment)->SetIntArrayRegion(
            environment, out_radius, 0, 1, &out_radius_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_shadowMapGetDepthBias(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jfloatArray out_bias)
{
    (void)environment;
    (void)declaring_class;
    float out_bias_value = 0;
    CNA_Result call_result = cna.shadow_map_get_depth_bias((CNA_ShadowMapHandle)shadow_map, &out_bias_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_bias_element = (jfloat)out_bias_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_bias, 0, 1, &out_bias_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_shadowMapGetFilterRadius(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jintArray out_radius)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_radius_value = 0;
    CNA_Result call_result = cna.shadow_map_get_filter_radius((CNA_ShadowMapHandle)shadow_map, &out_radius_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_radius_element = (jint)out_radius_value;
        (*environment)->SetIntArrayRegion(
            environment, out_radius, 0, 1, &out_radius_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_shadowMapGetLightViewProjection(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jfloatArray out_matrixFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Matrix out_matrix_value;
    memset(&out_matrix_value, 0, sizeof out_matrix_value);
    CNA_Result call_result = cna.shadow_map_get_light_view_projection((CNA_ShadowMapHandle)shadow_map, &out_matrix_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_matrixFloating_values[] = {
            (jfloat)out_matrix_value.m11,
            (jfloat)out_matrix_value.m12,
            (jfloat)out_matrix_value.m13,
            (jfloat)out_matrix_value.m14,
            (jfloat)out_matrix_value.m21,
            (jfloat)out_matrix_value.m22,
            (jfloat)out_matrix_value.m23,
            (jfloat)out_matrix_value.m24,
            (jfloat)out_matrix_value.m31,
            (jfloat)out_matrix_value.m32,
            (jfloat)out_matrix_value.m33,
            (jfloat)out_matrix_value.m34,
            (jfloat)out_matrix_value.m41,
            (jfloat)out_matrix_value.m42,
            (jfloat)out_matrix_value.m43,
            (jfloat)out_matrix_value.m44
        };
        (*environment)->SetFloatArrayRegion(environment, out_matrixFloating, 0,
            (jsize)(sizeof out_matrixFloating_values / sizeof out_matrixFloating_values[0]),
            out_matrixFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_shadowMapGetQuality(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jintArray out_quality)
{
    (void)environment;
    (void)declaring_class;
    CNA_ShadowQuality out_quality_value = 0;
    CNA_Result call_result = cna.shadow_map_get_quality((CNA_ShadowMapHandle)shadow_map, &out_quality_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_quality_element = (jint)out_quality_value;
        (*environment)->SetIntArrayRegion(
            environment, out_quality, 0, 1, &out_quality_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_shadowMapGetShadowTexture(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jlongArray out_texture)
{
    (void)environment;
    (void)declaring_class;
    CNA_Handle out_texture_value = 0;
    CNA_Result call_result = cna.shadow_map_get_shadow_texture((CNA_ShadowMapHandle)shadow_map, &out_texture_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_texture_element = (jlong)out_texture_value;
        (*environment)->SetLongArrayRegion(
            environment, out_texture, 0, 1, &out_texture_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_shadowMapGetSize(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jintArray out_size)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_size_value = 0;
    CNA_Result call_result = cna.shadow_map_get_size((CNA_ShadowMapHandle)shadow_map, &out_size_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_size_element = (jint)out_size_value;
        (*environment)->SetIntArrayRegion(
            environment, out_size, 0, 1, &out_size_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_shadowMapIsSupported(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jbooleanArray out_supported)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_supported_value = 0;
    CNA_Result call_result = cna.shadow_map_is_supported((CNA_ShadowMapHandle)shadow_map, &out_supported_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_supported_element = (jboolean)out_supported_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_supported, 0, 1, &out_supported_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_shadowMapSetDepthBias(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jfloat bias)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.shadow_map_set_depth_bias((CNA_ShadowMapHandle)shadow_map, (float)bias);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_shadowMapSizeForQuality(
    JNIEnv* environment, jclass declaring_class, jint quality, jintArray out_size)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_size_value = 0;
    CNA_Result call_result = cna.shadow_map_size_for_quality((CNA_ShadowQuality)quality, &out_size_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_size_element = (jint)out_size_value;
        (*environment)->SetIntArrayRegion(
            environment, out_size, 0, 1, &out_size_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_spotLightExtInit(
    JNIEnv* environment, jclass declaring_class, jbyteArray out_lightBytes, jlongArray out_lightIntegral, jfloatArray out_lightFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_SpotLightEXT out_light_value;
    memset(&out_light_value, 0, sizeof out_light_value);
    out_light_value.struct_size = (uint32_t)(sizeof out_light_value);
    out_light_value.struct_version = UINT32_C(1);
    CNA_Result call_result = cna.spot_light_ext_init(&out_light_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jbyte out_lightBytes_values[] = {
            (jbyte)out_light_value.reserved[0],
            (jbyte)out_light_value.reserved[1],
            (jbyte)out_light_value.reserved[2]
        };
        (*environment)->SetByteArrayRegion(environment, out_lightBytes, 0,
            (jsize)(sizeof out_lightBytes_values / sizeof out_lightBytes_values[0]),
            out_lightBytes_values);
        jlong out_lightIntegral_values[] = {
            (jlong)out_light_value.casts_shadows
        };
        (*environment)->SetLongArrayRegion(environment, out_lightIntegral, 0,
            (jsize)(sizeof out_lightIntegral_values / sizeof out_lightIntegral_values[0]),
            out_lightIntegral_values);
        jfloat out_lightFloating_values[] = {
            (jfloat)out_light_value.position.x,
            (jfloat)out_light_value.position.y,
            (jfloat)out_light_value.position.z,
            (jfloat)out_light_value.direction.x,
            (jfloat)out_light_value.direction.y,
            (jfloat)out_light_value.direction.z,
            (jfloat)out_light_value.color.x,
            (jfloat)out_light_value.color.y,
            (jfloat)out_light_value.color.z,
            (jfloat)out_light_value.intensity,
            (jfloat)out_light_value.range,
            (jfloat)out_light_value.inner_angle,
            (jfloat)out_light_value.outer_angle
        };
        (*environment)->SetFloatArrayRegion(environment, out_lightFloating, 0,
            (jsize)(sizeof out_lightFloating_values / sizeof out_lightFloating_values[0]),
            out_lightFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_spotShadowMapBegin(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jbyteArray lightBytes, jlongArray lightIntegral, jfloatArray lightFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_SpotLightEXT light_value;
    memset(&light_value, 0, sizeof light_value);
    light_value.struct_size = (uint32_t)(sizeof light_value);
    light_value.struct_version = UINT32_C(1);
    {
        jbyte lightBytes_values[3];
        (*environment)->GetByteArrayRegion(environment, lightBytes, 0, 3, lightBytes_values);
        light_value.reserved[0] = (uint8_t)lightBytes_values[0];
        light_value.reserved[1] = (uint8_t)lightBytes_values[1];
        light_value.reserved[2] = (uint8_t)lightBytes_values[2];
    }
    {
        jlong lightIntegral_values[1];
        (*environment)->GetLongArrayRegion(environment, lightIntegral, 0, 1, lightIntegral_values);
        light_value.casts_shadows = (CNA_Bool)lightIntegral_values[0];
    }
    {
        jfloat lightFloating_values[13];
        (*environment)->GetFloatArrayRegion(environment, lightFloating, 0, 13, lightFloating_values);
        light_value.position.x = (float)lightFloating_values[0];
        light_value.position.y = (float)lightFloating_values[1];
        light_value.position.z = (float)lightFloating_values[2];
        light_value.direction.x = (float)lightFloating_values[3];
        light_value.direction.y = (float)lightFloating_values[4];
        light_value.direction.z = (float)lightFloating_values[5];
        light_value.color.x = (float)lightFloating_values[6];
        light_value.color.y = (float)lightFloating_values[7];
        light_value.color.z = (float)lightFloating_values[8];
        light_value.intensity = (float)lightFloating_values[9];
        light_value.range = (float)lightFloating_values[10];
        light_value.inner_angle = (float)lightFloating_values[11];
        light_value.outer_angle = (float)lightFloating_values[12];
    }
    CNA_Result call_result = cna.spot_shadow_map_begin((CNA_SpotShadowMapHandle)shadow_map, &light_value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_spotShadowMapComputeLightProjection(
    JNIEnv* environment, jclass declaring_class, jbyteArray lightBytes, jlongArray lightIntegral, jfloatArray lightFloating, jfloatArray out_matrixFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_SpotLightEXT light_value;
    memset(&light_value, 0, sizeof light_value);
    light_value.struct_size = (uint32_t)(sizeof light_value);
    light_value.struct_version = UINT32_C(1);
    {
        jbyte lightBytes_values[3];
        (*environment)->GetByteArrayRegion(environment, lightBytes, 0, 3, lightBytes_values);
        light_value.reserved[0] = (uint8_t)lightBytes_values[0];
        light_value.reserved[1] = (uint8_t)lightBytes_values[1];
        light_value.reserved[2] = (uint8_t)lightBytes_values[2];
    }
    {
        jlong lightIntegral_values[1];
        (*environment)->GetLongArrayRegion(environment, lightIntegral, 0, 1, lightIntegral_values);
        light_value.casts_shadows = (CNA_Bool)lightIntegral_values[0];
    }
    {
        jfloat lightFloating_values[13];
        (*environment)->GetFloatArrayRegion(environment, lightFloating, 0, 13, lightFloating_values);
        light_value.position.x = (float)lightFloating_values[0];
        light_value.position.y = (float)lightFloating_values[1];
        light_value.position.z = (float)lightFloating_values[2];
        light_value.direction.x = (float)lightFloating_values[3];
        light_value.direction.y = (float)lightFloating_values[4];
        light_value.direction.z = (float)lightFloating_values[5];
        light_value.color.x = (float)lightFloating_values[6];
        light_value.color.y = (float)lightFloating_values[7];
        light_value.color.z = (float)lightFloating_values[8];
        light_value.intensity = (float)lightFloating_values[9];
        light_value.range = (float)lightFloating_values[10];
        light_value.inner_angle = (float)lightFloating_values[11];
        light_value.outer_angle = (float)lightFloating_values[12];
    }
    CNA_Matrix out_matrix_value;
    memset(&out_matrix_value, 0, sizeof out_matrix_value);
    CNA_Result call_result = cna.spot_shadow_map_compute_light_projection(&light_value, &out_matrix_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_matrixFloating_values[] = {
            (jfloat)out_matrix_value.m11,
            (jfloat)out_matrix_value.m12,
            (jfloat)out_matrix_value.m13,
            (jfloat)out_matrix_value.m14,
            (jfloat)out_matrix_value.m21,
            (jfloat)out_matrix_value.m22,
            (jfloat)out_matrix_value.m23,
            (jfloat)out_matrix_value.m24,
            (jfloat)out_matrix_value.m31,
            (jfloat)out_matrix_value.m32,
            (jfloat)out_matrix_value.m33,
            (jfloat)out_matrix_value.m34,
            (jfloat)out_matrix_value.m41,
            (jfloat)out_matrix_value.m42,
            (jfloat)out_matrix_value.m43,
            (jfloat)out_matrix_value.m44
        };
        (*environment)->SetFloatArrayRegion(environment, out_matrixFloating, 0,
            (jsize)(sizeof out_matrixFloating_values / sizeof out_matrixFloating_values[0]),
            out_matrixFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_spotShadowMapComputeLightView(
    JNIEnv* environment, jclass declaring_class, jbyteArray lightBytes, jlongArray lightIntegral, jfloatArray lightFloating, jfloatArray out_matrixFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_SpotLightEXT light_value;
    memset(&light_value, 0, sizeof light_value);
    light_value.struct_size = (uint32_t)(sizeof light_value);
    light_value.struct_version = UINT32_C(1);
    {
        jbyte lightBytes_values[3];
        (*environment)->GetByteArrayRegion(environment, lightBytes, 0, 3, lightBytes_values);
        light_value.reserved[0] = (uint8_t)lightBytes_values[0];
        light_value.reserved[1] = (uint8_t)lightBytes_values[1];
        light_value.reserved[2] = (uint8_t)lightBytes_values[2];
    }
    {
        jlong lightIntegral_values[1];
        (*environment)->GetLongArrayRegion(environment, lightIntegral, 0, 1, lightIntegral_values);
        light_value.casts_shadows = (CNA_Bool)lightIntegral_values[0];
    }
    {
        jfloat lightFloating_values[13];
        (*environment)->GetFloatArrayRegion(environment, lightFloating, 0, 13, lightFloating_values);
        light_value.position.x = (float)lightFloating_values[0];
        light_value.position.y = (float)lightFloating_values[1];
        light_value.position.z = (float)lightFloating_values[2];
        light_value.direction.x = (float)lightFloating_values[3];
        light_value.direction.y = (float)lightFloating_values[4];
        light_value.direction.z = (float)lightFloating_values[5];
        light_value.color.x = (float)lightFloating_values[6];
        light_value.color.y = (float)lightFloating_values[7];
        light_value.color.z = (float)lightFloating_values[8];
        light_value.intensity = (float)lightFloating_values[9];
        light_value.range = (float)lightFloating_values[10];
        light_value.inner_angle = (float)lightFloating_values[11];
        light_value.outer_angle = (float)lightFloating_values[12];
    }
    CNA_Matrix out_matrix_value;
    memset(&out_matrix_value, 0, sizeof out_matrix_value);
    CNA_Result call_result = cna.spot_shadow_map_compute_light_view(&light_value, &out_matrix_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_matrixFloating_values[] = {
            (jfloat)out_matrix_value.m11,
            (jfloat)out_matrix_value.m12,
            (jfloat)out_matrix_value.m13,
            (jfloat)out_matrix_value.m14,
            (jfloat)out_matrix_value.m21,
            (jfloat)out_matrix_value.m22,
            (jfloat)out_matrix_value.m23,
            (jfloat)out_matrix_value.m24,
            (jfloat)out_matrix_value.m31,
            (jfloat)out_matrix_value.m32,
            (jfloat)out_matrix_value.m33,
            (jfloat)out_matrix_value.m34,
            (jfloat)out_matrix_value.m41,
            (jfloat)out_matrix_value.m42,
            (jfloat)out_matrix_value.m43,
            (jfloat)out_matrix_value.m44
        };
        (*environment)->SetFloatArrayRegion(environment, out_matrixFloating, 0,
            (jsize)(sizeof out_matrixFloating_values / sizeof out_matrixFloating_values[0]),
            out_matrixFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_spotShadowMapCreate(
    JNIEnv* environment, jclass declaring_class, jlong graphics_device, jint quality, jlongArray out_shadow_map)
{
    (void)environment;
    (void)declaring_class;
    CNA_SpotShadowMapHandle out_shadow_map_value = 0;
    CNA_Result call_result = cna.spot_shadow_map_create((CNA_Handle)graphics_device, (CNA_ShadowQuality)quality, &out_shadow_map_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_shadow_map_element = (jlong)out_shadow_map_value;
        (*environment)->SetLongArrayRegion(
            environment, out_shadow_map, 0, 1, &out_shadow_map_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_spotShadowMapDestroy(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.spot_shadow_map_destroy((CNA_SpotShadowMapHandle)shadow_map);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_spotShadowMapEnd(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.spot_shadow_map_end((CNA_SpotShadowMapHandle)shadow_map);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_spotShadowMapGetDepthBias(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jfloatArray out_bias)
{
    (void)environment;
    (void)declaring_class;
    float out_bias_value = 0;
    CNA_Result call_result = cna.spot_shadow_map_get_depth_bias((CNA_SpotShadowMapHandle)shadow_map, &out_bias_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_bias_element = (jfloat)out_bias_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_bias, 0, 1, &out_bias_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_spotShadowMapGetLightPosition(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jfloatArray out_positionFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Vector3 out_position_value;
    memset(&out_position_value, 0, sizeof out_position_value);
    CNA_Result call_result = cna.spot_shadow_map_get_light_position((CNA_SpotShadowMapHandle)shadow_map, &out_position_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_positionFloating_values[] = {
            (jfloat)out_position_value.x,
            (jfloat)out_position_value.y,
            (jfloat)out_position_value.z
        };
        (*environment)->SetFloatArrayRegion(environment, out_positionFloating, 0,
            (jsize)(sizeof out_positionFloating_values / sizeof out_positionFloating_values[0]),
            out_positionFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_spotShadowMapGetLightRange(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jfloatArray out_range)
{
    (void)environment;
    (void)declaring_class;
    float out_range_value = 0;
    CNA_Result call_result = cna.spot_shadow_map_get_light_range((CNA_SpotShadowMapHandle)shadow_map, &out_range_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_range_element = (jfloat)out_range_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_range, 0, 1, &out_range_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_spotShadowMapGetLightViewProjection(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jfloatArray out_matrixFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Matrix out_matrix_value;
    memset(&out_matrix_value, 0, sizeof out_matrix_value);
    CNA_Result call_result = cna.spot_shadow_map_get_light_view_projection((CNA_SpotShadowMapHandle)shadow_map, &out_matrix_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_matrixFloating_values[] = {
            (jfloat)out_matrix_value.m11,
            (jfloat)out_matrix_value.m12,
            (jfloat)out_matrix_value.m13,
            (jfloat)out_matrix_value.m14,
            (jfloat)out_matrix_value.m21,
            (jfloat)out_matrix_value.m22,
            (jfloat)out_matrix_value.m23,
            (jfloat)out_matrix_value.m24,
            (jfloat)out_matrix_value.m31,
            (jfloat)out_matrix_value.m32,
            (jfloat)out_matrix_value.m33,
            (jfloat)out_matrix_value.m34,
            (jfloat)out_matrix_value.m41,
            (jfloat)out_matrix_value.m42,
            (jfloat)out_matrix_value.m43,
            (jfloat)out_matrix_value.m44
        };
        (*environment)->SetFloatArrayRegion(environment, out_matrixFloating, 0,
            (jsize)(sizeof out_matrixFloating_values / sizeof out_matrixFloating_values[0]),
            out_matrixFloating_values);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_spotShadowMapGetQuality(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jintArray out_quality)
{
    (void)environment;
    (void)declaring_class;
    CNA_ShadowQuality out_quality_value = 0;
    CNA_Result call_result = cna.spot_shadow_map_get_quality((CNA_SpotShadowMapHandle)shadow_map, &out_quality_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_quality_element = (jint)out_quality_value;
        (*environment)->SetIntArrayRegion(
            environment, out_quality, 0, 1, &out_quality_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_spotShadowMapGetShadowTexture(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jlongArray out_texture)
{
    (void)environment;
    (void)declaring_class;
    CNA_Handle out_texture_value = 0;
    CNA_Result call_result = cna.spot_shadow_map_get_shadow_texture((CNA_SpotShadowMapHandle)shadow_map, &out_texture_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_texture_element = (jlong)out_texture_value;
        (*environment)->SetLongArrayRegion(
            environment, out_texture, 0, 1, &out_texture_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_spotShadowMapGetSize(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jintArray out_size)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_size_value = 0;
    CNA_Result call_result = cna.spot_shadow_map_get_size((CNA_SpotShadowMapHandle)shadow_map, &out_size_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_size_element = (jint)out_size_value;
        (*environment)->SetIntArrayRegion(
            environment, out_size, 0, 1, &out_size_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_spotShadowMapIsSupported(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jbooleanArray out_supported)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_supported_value = 0;
    CNA_Result call_result = cna.spot_shadow_map_is_supported((CNA_SpotShadowMapHandle)shadow_map, &out_supported_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_supported_element = (jboolean)out_supported_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_supported, 0, 1, &out_supported_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_spotShadowMapSetDepthBias(
    JNIEnv* environment, jclass declaring_class, jlong shadow_map, jfloat bias)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.spot_shadow_map_set_depth_bias((CNA_SpotShadowMapHandle)shadow_map, (float)bias);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_ssaoPassCopyKernel(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray destinationFloating, jlongArray out_count)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_count = (*environment)->GetArrayLength(environment, destinationFloating) / 3;
    if ((*environment)->GetArrayLength(environment, destinationFloating) != destination_count * 3) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Vector3* destination_values = (CNA_Vector3*)calloc(
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
            destination_values[element].x = (float)destinationFloating_values[element * 3 + 0];
            destination_values[element].y = (float)destinationFloating_values[element * 3 + 1];
            destination_values[element].z = (float)destinationFloating_values[element * 3 + 2];
        }
        free(destinationFloating_values);
    }
    uint64_t out_count_value = 0;
    CNA_Result call_result = cna.ssao_pass_copy_kernel((CNA_PostProcessPassHandle)pass, destination_values, (uint64_t)destination_count, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS) {
        {
            jfloat* destinationFloating_out = (jfloat*)malloc(
                ((size_t)destination_count * 3 + 1U) * sizeof(*destinationFloating_out));
            if (destinationFloating_out != NULL) {
                for (jsize element = 0; element < destination_count; ++element) {
                    destinationFloating_out[element * 3 + 0] = (jfloat)destination_values[element].x;
                    destinationFloating_out[element * 3 + 1] = (jfloat)destination_values[element].y;
                    destinationFloating_out[element * 3 + 2] = (jfloat)destination_values[element].z;
                }
                (*environment)->SetFloatArrayRegion(
                    environment, destinationFloating, 0,
                    (jsize)(destination_count * 3), destinationFloating_out);
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
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_ssaoPassCopyOcclusionGlsl(
    JNIEnv* environment, jclass declaring_class, jboolean packed, jbyteArray destination, jlongArray out_bytes)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t destination_written = 0;
    CNA_Result call_result = cna.ssao_pass_copy_occlusion_glsl((CNA_Bool)packed, (char*)destination_bytes, (uint64_t)destination_size, &destination_written);
    (*environment)->ReleaseByteArrayElements(environment, destination, destination_bytes, 0);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong destination_element = (jlong)destination_written;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &destination_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_ssaoPassCreate(
    JNIEnv* environment, jclass declaring_class, jlong graphics_device, jlongArray out_pass)
{
    (void)environment;
    (void)declaring_class;
    CNA_PostProcessPassHandle out_pass_value = 0;
    CNA_Result call_result = cna.ssao_pass_create((CNA_Handle)graphics_device, &out_pass_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_pass_element = (jlong)out_pass_value;
        (*environment)->SetLongArrayRegion(
            environment, out_pass, 0, 1, &out_pass_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_ssaoPassGetHalfResolution(
    JNIEnv* environment, jclass declaring_class, jlong pass, jbooleanArray out_value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_value_value = 0;
    CNA_Result call_result = cna.ssao_pass_get_half_resolution((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_value_element = (jboolean)out_value_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_ssaoPassGetIntensity(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.ssao_pass_get_intensity((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_ssaoPassGetRadius(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.ssao_pass_get_radius((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_ssaoPassGetSampleCount(
    JNIEnv* environment, jclass declaring_class, jlong pass, jintArray out_value)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_value_value = 0;
    CNA_Result call_result = cna.ssao_pass_get_sample_count((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_value_element = (jint)out_value_value;
        (*environment)->SetIntArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_ssaoPassResetTargets(
    JNIEnv* environment, jclass declaring_class, jlong pass)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.ssao_pass_reset_targets((CNA_PostProcessPassHandle)pass);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_ssaoPassSampleCountForQuality(
    JNIEnv* environment, jclass declaring_class, jint quality, jintArray out_count)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_count_value = 0;
    CNA_Result call_result = cna.ssao_pass_sample_count_for_quality((CNA_RenderQuality)quality, &out_count_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_count_element = (jint)out_count_value;
        (*environment)->SetIntArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_ssaoPassSetHalfResolution(
    JNIEnv* environment, jclass declaring_class, jlong pass, jboolean value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.ssao_pass_set_half_resolution((CNA_PostProcessPassHandle)pass, (CNA_Bool)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_ssaoPassSetIntensity(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.ssao_pass_set_intensity((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_ssaoPassSetRadius(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.ssao_pass_set_radius((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_ssaoPassSetSampleCount(
    JNIEnv* environment, jclass declaring_class, jlong pass, jint value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.ssao_pass_set_sample_count((CNA_PostProcessPassHandle)pass, (int32_t)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_ssrPassCreate(
    JNIEnv* environment, jclass declaring_class, jlong graphics_device, jlongArray out_pass)
{
    (void)environment;
    (void)declaring_class;
    CNA_PostProcessPassHandle out_pass_value = 0;
    CNA_Result call_result = cna.ssr_pass_create((CNA_Handle)graphics_device, &out_pass_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_pass_element = (jlong)out_pass_value;
        (*environment)->SetLongArrayRegion(
            environment, out_pass, 0, 1, &out_pass_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_ssrPassGetDepthBias(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.ssr_pass_get_depth_bias((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_ssrPassGetEdgeFade(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.ssr_pass_get_edge_fade((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_ssrPassGetIntensity(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.ssr_pass_get_intensity((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_ssrPassGetMaxDistance(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.ssr_pass_get_max_distance((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_ssrPassGetRoughnessBlur(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.ssr_pass_get_roughness_blur((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_ssrPassGetStepCount(
    JNIEnv* environment, jclass declaring_class, jlong pass, jintArray out_value)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_value_value = 0;
    CNA_Result call_result = cna.ssr_pass_get_step_count((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_value_element = (jint)out_value_value;
        (*environment)->SetIntArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_ssrPassGetThickness(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.ssr_pass_get_thickness((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_ssrPassSetDepthBias(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.ssr_pass_set_depth_bias((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_ssrPassSetEdgeFade(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.ssr_pass_set_edge_fade((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_ssrPassSetIntensity(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.ssr_pass_set_intensity((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_ssrPassSetMaxDistance(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.ssr_pass_set_max_distance((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_ssrPassSetRoughnessBlur(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.ssr_pass_set_roughness_blur((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_ssrPassSetStepCount(
    JNIEnv* environment, jclass declaring_class, jlong pass, jint value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.ssr_pass_set_step_count((CNA_PostProcessPassHandle)pass, (int32_t)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_ssrPassSetThickness(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.ssr_pass_set_thickness((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_tonemapPassCreate(
    JNIEnv* environment, jclass declaring_class, jlong graphics_device, jlongArray out_pass)
{
    (void)environment;
    (void)declaring_class;
    CNA_PostProcessPassHandle out_pass_value = 0;
    CNA_Result call_result = cna.tonemap_pass_create((CNA_Handle)graphics_device, &out_pass_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_pass_element = (jlong)out_pass_value;
        (*environment)->SetLongArrayRegion(
            environment, out_pass, 0, 1, &out_pass_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_tonemapPassGetDebandStrength(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.tonemap_pass_get_deband_strength((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_tonemapPassGetExposure(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.tonemap_pass_get_exposure((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_tonemapPassGetGamma(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.tonemap_pass_get_gamma((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_tonemapPassGetMode(
    JNIEnv* environment, jclass declaring_class, jlong pass, jintArray out_mode)
{
    (void)environment;
    (void)declaring_class;
    CNA_TonemappingMode out_mode_value = 0;
    CNA_Result call_result = cna.tonemap_pass_get_mode((CNA_PostProcessPassHandle)pass, &out_mode_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jint out_mode_element = (jint)out_mode_value;
        (*environment)->SetIntArrayRegion(
            environment, out_mode, 0, 1, &out_mode_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_tonemapPassIsDebandEnabled(
    JNIEnv* environment, jclass declaring_class, jlong pass, jbooleanArray out_enabled)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_enabled_value = 0;
    CNA_Result call_result = cna.tonemap_pass_is_deband_enabled((CNA_PostProcessPassHandle)pass, &out_enabled_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jboolean out_enabled_element = (jboolean)out_enabled_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_enabled, 0, 1, &out_enabled_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_tonemapPassSetDebandEnabled(
    JNIEnv* environment, jclass declaring_class, jlong pass, jboolean value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.tonemap_pass_set_deband_enabled((CNA_PostProcessPassHandle)pass, (CNA_Bool)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_tonemapPassSetDebandStrength(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.tonemap_pass_set_deband_strength((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_tonemapPassSetExposure(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.tonemap_pass_set_exposure((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_tonemapPassSetGamma(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.tonemap_pass_set_gamma((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_tonemapPassSetMode(
    JNIEnv* environment, jclass declaring_class, jlong pass, jint mode)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.tonemap_pass_set_mode((CNA_PostProcessPassHandle)pass, (CNA_TonemappingMode)mode);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_tonemapPassTonemapChannel(
    JNIEnv* environment, jclass declaring_class, jint mode, jfloat value, jfloat exposure, jfloat gamma, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.tonemap_pass_tonemap_channel((CNA_TonemappingMode)mode, (float)value, (float)exposure, (float)gamma, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_volumetricFogPassCreate(
    JNIEnv* environment, jclass declaring_class, jlong graphics_device, jlongArray out_pass)
{
    (void)environment;
    (void)declaring_class;
    CNA_PostProcessPassHandle out_pass_value = 0;
    CNA_Result call_result = cna.volumetric_fog_pass_create((CNA_Handle)graphics_device, &out_pass_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jlong out_pass_element = (jlong)out_pass_value;
        (*environment)->SetLongArrayRegion(
            environment, out_pass, 0, 1, &out_pass_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_volumetricFogPassGetAnisotropy(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.volumetric_fog_pass_get_anisotropy((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_volumetricFogPassGetDensity(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.volumetric_fog_pass_get_density((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_volumetricFogPassGetRange(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result call_result = cna.volumetric_fog_pass_get_range((CNA_PostProcessPassHandle)pass, &out_value_value);
    if (call_result == CNA_RESULT_SUCCESS || call_result == CNA_RESULT_BUFFER_TOO_SMALL) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_volumetricFogPassSetAnisotropy(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.volumetric_fog_pass_set_anisotropy((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_volumetricFogPassSetDensity(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.volumetric_fog_pass_set_density((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_volumetricFogPassSetLight(
    JNIEnv* environment, jclass declaring_class, jlong pass, jlong shadow_map, jfloatArray light_directionFloating, jfloatArray light_colorFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_Vector3 light_direction_value;
    memset(&light_direction_value, 0, sizeof light_direction_value);
    {
        jfloat light_directionFloating_values[3];
        (*environment)->GetFloatArrayRegion(environment, light_directionFloating, 0, 3, light_directionFloating_values);
        light_direction_value.x = (float)light_directionFloating_values[0];
        light_direction_value.y = (float)light_directionFloating_values[1];
        light_direction_value.z = (float)light_directionFloating_values[2];
    }
    CNA_Vector3 light_color_value;
    memset(&light_color_value, 0, sizeof light_color_value);
    {
        jfloat light_colorFloating_values[3];
        (*environment)->GetFloatArrayRegion(environment, light_colorFloating, 0, 3, light_colorFloating_values);
        light_color_value.x = (float)light_colorFloating_values[0];
        light_color_value.y = (float)light_colorFloating_values[1];
        light_color_value.z = (float)light_colorFloating_values[2];
    }
    CNA_Result call_result = cna.volumetric_fog_pass_set_light((CNA_PostProcessPassHandle)pass, (CNA_ShadowMapHandle)shadow_map, &light_direction_value, &light_color_value);
    return (jint)call_result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_volumetricFogPassSetRange(
    JNIEnv* environment, jclass declaring_class, jlong pass, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result call_result = cna.volumetric_fog_pass_set_range((CNA_PostProcessPassHandle)pass, (float)value);
    return (jint)call_result;
}
