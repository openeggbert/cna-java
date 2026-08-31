/* SPDX-License-Identifier: MS-PL */
/*
 * Generated JNI adapter for NativeEngineLayerRoutes.
 *
 * Produced by tools/native-abi/generate_jni.py from the live CNA C headers, and
 * included by cna_java_jni.c so it shares the one dispatch table. Do not edit.
 */

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
