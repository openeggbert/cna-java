/* SPDX-License-Identifier: MS-PL */
/*
 * Generated JNI adapter for NativeGraphicsExtensionRoutes.
 *
 * Produced by tools/native-abi/generate_jni.py from the live CNA C headers, and
 * included by cna_java_jni.c so it shares the one dispatch table. Do not edit.
 */

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeGraphicsExtensionRoutes_asciiPostProcessEffectCreate(
    JNIEnv* environment, jclass declaring_class, jlong graphics_device, jlongArray out_effect)
{
    (void)environment;
    (void)declaring_class;
    CNA_AsciiPostProcessEffectHandle out_effect_value = 0;
    CNA_Result result = cna.ascii_post_process_effect_create((CNA_Handle)graphics_device, &out_effect_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_effect_element = (jlong)out_effect_value;
        (*environment)->SetLongArrayRegion(
            environment, out_effect, 0, 1, &out_effect_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeGraphicsExtensionRoutes_asciiPostProcessEffectDestroy(
    JNIEnv* environment, jclass declaring_class, jlong effect)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result result = cna.ascii_post_process_effect_destroy((CNA_AsciiPostProcessEffectHandle)effect);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeGraphicsExtensionRoutes_asciiPostProcessEffectDraw(
    JNIEnv* environment, jclass declaring_class, jlong effect, jlong source, jlongArray destination_rectangleIntegral)
{
    (void)environment;
    (void)declaring_class;
    CNA_Rectangle destination_rectangle_value;
    memset(&destination_rectangle_value, 0, sizeof destination_rectangle_value);
    {
        jlong destination_rectangleIntegral_values[4];
        (*environment)->GetLongArrayRegion(environment, destination_rectangleIntegral, 0, 4, destination_rectangleIntegral_values);
        destination_rectangle_value.x = (int32_t)destination_rectangleIntegral_values[0];
        destination_rectangle_value.y = (int32_t)destination_rectangleIntegral_values[1];
        destination_rectangle_value.width = (int32_t)destination_rectangleIntegral_values[2];
        destination_rectangle_value.height = (int32_t)destination_rectangleIntegral_values[3];
    }
    CNA_Result result = cna.ascii_post_process_effect_draw((CNA_AsciiPostProcessEffectHandle)effect, (CNA_Handle)source, &destination_rectangle_value);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeGraphicsExtensionRoutes_asciiPostProcessEffectGetCellSize(
    JNIEnv* environment, jclass declaring_class, jlong effect, jintArray out_width, jintArray out_height)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_width_value = 0;
    int32_t out_height_value = 0;
    CNA_Result result = cna.ascii_post_process_effect_get_cell_size((CNA_AsciiPostProcessEffectHandle)effect, &out_width_value, &out_height_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_width_element = (jint)out_width_value;
        (*environment)->SetIntArrayRegion(
            environment, out_width, 0, 1, &out_width_element);
        jint out_height_element = (jint)out_height_value;
        (*environment)->SetIntArrayRegion(
            environment, out_height, 0, 1, &out_height_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeGraphicsExtensionRoutes_asciiPostProcessEffectGetLastGridDimensions(
    JNIEnv* environment, jclass declaring_class, jlong effect, jintArray out_columns, jintArray out_rows)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_columns_value = 0;
    int32_t out_rows_value = 0;
    CNA_Result result = cna.ascii_post_process_effect_get_last_grid_dimensions((CNA_AsciiPostProcessEffectHandle)effect, &out_columns_value, &out_rows_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_columns_element = (jint)out_columns_value;
        (*environment)->SetIntArrayRegion(
            environment, out_columns, 0, 1, &out_columns_element);
        jint out_rows_element = (jint)out_rows_value;
        (*environment)->SetIntArrayRegion(
            environment, out_rows, 0, 1, &out_rows_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeGraphicsExtensionRoutes_asciiPostProcessEffectGetQuantizeMode(
    JNIEnv* environment, jclass declaring_class, jlong effect, jintArray out_mode)
{
    (void)environment;
    (void)declaring_class;
    CNA_AsciiQuantizeMode out_mode_value = 0;
    CNA_Result result = cna.ascii_post_process_effect_get_quantize_mode((CNA_AsciiPostProcessEffectHandle)effect, &out_mode_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_mode_element = (jint)out_mode_value;
        (*environment)->SetIntArrayRegion(
            environment, out_mode, 0, 1, &out_mode_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeGraphicsExtensionRoutes_asciiPostProcessEffectSetCellSize(
    JNIEnv* environment, jclass declaring_class, jlong effect, jint width, jint height)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result result = cna.ascii_post_process_effect_set_cell_size((CNA_AsciiPostProcessEffectHandle)effect, (int32_t)width, (int32_t)height);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeGraphicsExtensionRoutes_asciiPostProcessEffectSetQuantizeMode(
    JNIEnv* environment, jclass declaring_class, jlong effect, jint mode)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result result = cna.ascii_post_process_effect_set_quantize_mode((CNA_AsciiPostProcessEffectHandle)effect, (CNA_AsciiQuantizeMode)mode);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeGraphicsExtensionRoutes_crtEffectCreate(
    JNIEnv* environment, jclass declaring_class, jlong graphics_device, jlongArray out_effect)
{
    (void)environment;
    (void)declaring_class;
    CNA_EffectHandle out_effect_value = 0;
    CNA_Result result = cna.crt_effect_create((CNA_Handle)graphics_device, &out_effect_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_effect_element = (jlong)out_effect_value;
        (*environment)->SetLongArrayRegion(
            environment, out_effect, 0, 1, &out_effect_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeGraphicsExtensionRoutes_crtEffectGetCurvature(
    JNIEnv* environment, jclass declaring_class, jlong effect, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result result = cna.crt_effect_get_curvature((CNA_EffectHandle)effect, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeGraphicsExtensionRoutes_crtEffectGetMaskIntensity(
    JNIEnv* environment, jclass declaring_class, jlong effect, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result result = cna.crt_effect_get_mask_intensity((CNA_EffectHandle)effect, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeGraphicsExtensionRoutes_crtEffectGetMaskType(
    JNIEnv* environment, jclass declaring_class, jlong effect, jintArray out_mask_type)
{
    (void)environment;
    (void)declaring_class;
    CNA_CRTMaskType out_mask_type_value = 0;
    CNA_Result result = cna.crt_effect_get_mask_type((CNA_EffectHandle)effect, &out_mask_type_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_mask_type_element = (jint)out_mask_type_value;
        (*environment)->SetIntArrayRegion(
            environment, out_mask_type, 0, 1, &out_mask_type_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeGraphicsExtensionRoutes_crtEffectGetScanlineIntensity(
    JNIEnv* environment, jclass declaring_class, jlong effect, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result result = cna.crt_effect_get_scanline_intensity((CNA_EffectHandle)effect, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeGraphicsExtensionRoutes_crtEffectGetVignetteIntensity(
    JNIEnv* environment, jclass declaring_class, jlong effect, jfloatArray out_value)
{
    (void)environment;
    (void)declaring_class;
    float out_value_value = 0;
    CNA_Result result = cna.crt_effect_get_vignette_intensity((CNA_EffectHandle)effect, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeGraphicsExtensionRoutes_crtEffectSetCurvature(
    JNIEnv* environment, jclass declaring_class, jlong effect, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result result = cna.crt_effect_set_curvature((CNA_EffectHandle)effect, (float)value);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeGraphicsExtensionRoutes_crtEffectSetMaskIntensity(
    JNIEnv* environment, jclass declaring_class, jlong effect, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result result = cna.crt_effect_set_mask_intensity((CNA_EffectHandle)effect, (float)value);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeGraphicsExtensionRoutes_crtEffectSetMaskType(
    JNIEnv* environment, jclass declaring_class, jlong effect, jint mask_type)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result result = cna.crt_effect_set_mask_type((CNA_EffectHandle)effect, (CNA_CRTMaskType)mask_type);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeGraphicsExtensionRoutes_crtEffectSetScanlineIntensity(
    JNIEnv* environment, jclass declaring_class, jlong effect, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result result = cna.crt_effect_set_scanline_intensity((CNA_EffectHandle)effect, (float)value);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeGraphicsExtensionRoutes_crtEffectSetVignetteIntensity(
    JNIEnv* environment, jclass declaring_class, jlong effect, jfloat value)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result result = cna.crt_effect_set_vignette_intensity((CNA_EffectHandle)effect, (float)value);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeGraphicsExtensionRoutes_depthEffectCreate(
    JNIEnv* environment, jclass declaring_class, jlong graphics_device, jlongArray out_effect)
{
    (void)environment;
    (void)declaring_class;
    CNA_EffectHandle out_effect_value = 0;
    CNA_Result result = cna.depth_effect_create((CNA_Handle)graphics_device, &out_effect_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_effect_element = (jlong)out_effect_value;
        (*environment)->SetLongArrayRegion(
            environment, out_effect, 0, 1, &out_effect_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeGraphicsExtensionRoutes_depthEffectGetDitherMode(
    JNIEnv* environment, jclass declaring_class, jlong effect, jintArray out_dither_mode)
{
    (void)environment;
    (void)declaring_class;
    CNA_DitherMode out_dither_mode_value = 0;
    CNA_Result result = cna.depth_effect_get_dither_mode((CNA_EffectHandle)effect, &out_dither_mode_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_dither_mode_element = (jint)out_dither_mode_value;
        (*environment)->SetIntArrayRegion(
            environment, out_dither_mode, 0, 1, &out_dither_mode_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeGraphicsExtensionRoutes_depthEffectGetMode(
    JNIEnv* environment, jclass declaring_class, jlong effect, jintArray out_mode)
{
    (void)environment;
    (void)declaring_class;
    CNA_DepthEffectMode out_mode_value = 0;
    CNA_Result result = cna.depth_effect_get_mode((CNA_EffectHandle)effect, &out_mode_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_mode_element = (jint)out_mode_value;
        (*environment)->SetIntArrayRegion(
            environment, out_mode, 0, 1, &out_mode_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeGraphicsExtensionRoutes_depthEffectSetDitherMode(
    JNIEnv* environment, jclass declaring_class, jlong effect, jint dither_mode)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result result = cna.depth_effect_set_dither_mode((CNA_EffectHandle)effect, (CNA_DitherMode)dither_mode);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeGraphicsExtensionRoutes_depthEffectSetMode(
    JNIEnv* environment, jclass declaring_class, jlong effect, jint mode)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result result = cna.depth_effect_set_mode((CNA_EffectHandle)effect, (CNA_DepthEffectMode)mode);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeGraphicsExtensionRoutes_graphicsExtIsAvailable(
    JNIEnv* environment, jclass declaring_class, jbooleanArray out_available)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_available_value = 0;
    CNA_Result result = cna.graphics_ext_is_available(&out_available_value);
    if (result == CNA_RESULT_SUCCESS) {
        jboolean out_available_element = (jboolean)out_available_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_available, 0, 1, &out_available_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeGraphicsExtensionRoutes_pbrMaterialInit(
    JNIEnv* environment, jclass declaring_class, jbyteArray out_materialBytes, jlongArray out_materialIntegral, jfloatArray out_materialFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_PbrMaterial out_material_value;
    memset(&out_material_value, 0, sizeof out_material_value);
    CNA_Result result = cna.pbr_material_init(&out_material_value);
    if (result == CNA_RESULT_SUCCESS) {
        jbyte out_materialBytes_values[] = {
            (jbyte)out_material_value.reserved[0],
            (jbyte)out_material_value.reserved[1],
            (jbyte)out_material_value.reserved[2]
        };
        (*environment)->SetByteArrayRegion(environment, out_materialBytes, 0,
            (jsize)(sizeof out_materialBytes_values / sizeof out_materialBytes_values[0]),
            out_materialBytes_values);
        jlong out_materialIntegral_values[] = {
            (jlong)out_material_value.albedo_texture,
            (jlong)out_material_value.normal_texture,
            (jlong)out_material_value.metallic_roughness_texture,
            (jlong)out_material_value.ambient_occlusion_texture,
            (jlong)out_material_value.emissive_texture,
            (jlong)out_material_value.albedo_color.r,
            (jlong)out_material_value.albedo_color.g,
            (jlong)out_material_value.albedo_color.b,
            (jlong)out_material_value.albedo_color.a,
            (jlong)out_material_value.emissive_color.r,
            (jlong)out_material_value.emissive_color.g,
            (jlong)out_material_value.emissive_color.b,
            (jlong)out_material_value.emissive_color.a,
            (jlong)out_material_value.alpha_blend_enabled
        };
        (*environment)->SetLongArrayRegion(environment, out_materialIntegral, 0,
            (jsize)(sizeof out_materialIntegral_values / sizeof out_materialIntegral_values[0]),
            out_materialIntegral_values);
        jfloat out_materialFloating_values[] = {
            (jfloat)out_material_value.metallic_factor,
            (jfloat)out_material_value.roughness_factor,
            (jfloat)out_material_value.normal_scale,
            (jfloat)out_material_value.occlusion_strength,
            (jfloat)out_material_value.alpha_cutoff
        };
        (*environment)->SetFloatArrayRegion(environment, out_materialFloating, 0,
            (jsize)(sizeof out_materialFloating_values / sizeof out_materialFloating_values[0]),
            out_materialFloating_values);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeGraphicsExtensionRoutes_renderPipelineSettingsInit(
    JNIEnv* environment, jclass declaring_class, jlongArray out_settingsIntegral, jfloatArray out_settingsFloating)
{
    (void)environment;
    (void)declaring_class;
    CNA_RenderPipelineSettings out_settings_value;
    memset(&out_settings_value, 0, sizeof out_settings_value);
    CNA_Result result = cna.render_pipeline_settings_init(&out_settings_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_settingsIntegral_values[] = {
            (jlong)out_settings_value.tonemapping_mode,
            (jlong)out_settings_value.render_quality,
            (jlong)out_settings_value.shadow_quality,
            (jlong)out_settings_value.hdr_enabled,
            (jlong)out_settings_value.bloom_enabled,
            (jlong)out_settings_value.ssao_enabled,
            (jlong)out_settings_value.shadows_enabled
        };
        (*environment)->SetLongArrayRegion(environment, out_settingsIntegral, 0,
            (jsize)(sizeof out_settingsIntegral_values / sizeof out_settingsIntegral_values[0]),
            out_settingsIntegral_values);
        jfloat out_settingsFloating_values[] = {
            (jfloat)out_settings_value.exposure,
            (jfloat)out_settings_value.gamma,
            (jfloat)out_settings_value.bloom_intensity
        };
        (*environment)->SetFloatArrayRegion(environment, out_settingsFloating, 0,
            (jsize)(sizeof out_settingsFloating_values / sizeof out_settingsFloating_values[0]),
            out_settingsFloating_values);
    }
    return (jint)result;
}
