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
Java_org_openeggbert_cna_internal_generated_NativeEngineLayerRoutes_instancedRendererExtCopyInstanceElements(
    JNIEnv* environment, jclass declaring_class, jlongArray destinationIntegral, jlongArray out_element_count)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_count = (*environment)->GetArrayLength(environment, destinationIntegral) / 4;
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
