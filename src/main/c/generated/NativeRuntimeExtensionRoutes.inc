/* SPDX-License-Identifier: MS-PL */
/*
 * Generated JNI adapter for NativeRuntimeExtensionRoutes.
 *
 * Produced by tools/native-abi/generate_jni.py from the live CNA C headers, and
 * included by cna_java_jni.c so it shares the one dispatch table. Do not edit.
 */

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeRuntimeExtensionRoutes_assemblyCopyTitleExt(
    JNIEnv* environment, jclass type, jbyteArray destination, jlongArray out_bytes)
{
    (void)environment;
    (void)type;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t destination_written = 0;
    CNA_Result result = cna.assembly_copy_title_ext((char*)destination_bytes, (uint64_t)destination_size, &destination_written);
    (*environment)->ReleaseByteArrayElements(environment, destination, destination_bytes, 0);
    if (result == CNA_RESULT_SUCCESS) {
        jlong destination_element = (jlong)destination_written;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &destination_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeRuntimeExtensionRoutes_assemblySetTitleExt(
    JNIEnv* environment, jclass type, jbyteArray title)
{
    (void)environment;
    (void)type;
    jsize title_size = (*environment)->GetArrayLength(environment, title);
    jbyte* title_bytes = (*environment)->GetByteArrayElements(environment, title, NULL);
    if (title_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    CNA_StringView title_view = {(const char*)title_bytes, (uint64_t)title_size};
    CNA_Result result = cna.assembly_set_title_ext(title_view);
    (*environment)->ReleaseByteArrayElements(environment, title, title_bytes, JNI_ABORT);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeRuntimeExtensionRoutes_desktopOsGetCurrent(
    JNIEnv* environment, jclass type, jintArray out_os)
{
    (void)environment;
    (void)type;
    CNA_DesktopOS out_os_value = 0;
    CNA_Result result = cna.desktop_os_get_current(&out_os_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_os_element = (jint)out_os_value;
        (*environment)->SetIntArrayRegion(
            environment, out_os, 0, 1, &out_os_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeRuntimeExtensionRoutes_graphicsBackendCategoryCopyName(
    JNIEnv* environment, jclass type, jint category, jbyteArray destination, jlongArray out_bytes)
{
    (void)environment;
    (void)type;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t destination_written = 0;
    CNA_Result result = cna.graphics_backend_category_copy_name((CNA_GraphicsBackendCategory)category, (char*)destination_bytes, (uint64_t)destination_size, &destination_written);
    (*environment)->ReleaseByteArrayElements(environment, destination, destination_bytes, 0);
    if (result == CNA_RESULT_SUCCESS) {
        jlong destination_element = (jlong)destination_written;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &destination_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeRuntimeExtensionRoutes_graphicsBackendCategoryGetNameSize(
    JNIEnv* environment, jclass type, jint category, jlongArray out_bytes)
{
    (void)environment;
    (void)type;
    uint64_t out_bytes_value = 0;
    CNA_Result result = cna.graphics_backend_category_get_name_size((CNA_GraphicsBackendCategory)category, &out_bytes_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_bytes_element = (jlong)out_bytes_value;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &out_bytes_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeRuntimeExtensionRoutes_graphicsBackendGetCurrentCategory(
    JNIEnv* environment, jclass type, jintArray out_category)
{
    (void)environment;
    (void)type;
    CNA_GraphicsBackendCategory out_category_value = 0;
    CNA_Result result = cna.graphics_backend_get_current_category(&out_category_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_category_element = (jint)out_category_value;
        (*environment)->SetIntArrayRegion(
            environment, out_category, 0, 1, &out_category_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeRuntimeExtensionRoutes_graphicsBackendGetCurrentMaturity(
    JNIEnv* environment, jclass type, jintArray out_maturity)
{
    (void)environment;
    (void)type;
    CNA_GraphicsBackendMaturity out_maturity_value = 0;
    CNA_Result result = cna.graphics_backend_get_current_maturity(&out_maturity_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_maturity_element = (jint)out_maturity_value;
        (*environment)->SetIntArrayRegion(
            environment, out_maturity, 0, 1, &out_maturity_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeRuntimeExtensionRoutes_graphicsBackendMaturityCopyName(
    JNIEnv* environment, jclass type, jint maturity, jbyteArray destination, jlongArray out_bytes)
{
    (void)environment;
    (void)type;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t destination_written = 0;
    CNA_Result result = cna.graphics_backend_maturity_copy_name((CNA_GraphicsBackendMaturity)maturity, (char*)destination_bytes, (uint64_t)destination_size, &destination_written);
    (*environment)->ReleaseByteArrayElements(environment, destination, destination_bytes, 0);
    if (result == CNA_RESULT_SUCCESS) {
        jlong destination_element = (jlong)destination_written;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &destination_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeRuntimeExtensionRoutes_graphicsBackendMaturityGetNameSize(
    JNIEnv* environment, jclass type, jint maturity, jlongArray out_bytes)
{
    (void)environment;
    (void)type;
    uint64_t out_bytes_value = 0;
    CNA_Result result = cna.graphics_backend_maturity_get_name_size((CNA_GraphicsBackendMaturity)maturity, &out_bytes_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_bytes_element = (jlong)out_bytes_value;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &out_bytes_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeRuntimeExtensionRoutes_graphicsRendererCopyCurrentName(
    JNIEnv* environment, jclass type, jbyteArray destination, jlongArray out_bytes)
{
    (void)environment;
    (void)type;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t destination_written = 0;
    CNA_Result result = cna.graphics_renderer_copy_current_name((char*)destination_bytes, (uint64_t)destination_size, &destination_written);
    (*environment)->ReleaseByteArrayElements(environment, destination, destination_bytes, 0);
    if (result == CNA_RESULT_SUCCESS) {
        jlong destination_element = (jlong)destination_written;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &destination_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeRuntimeExtensionRoutes_graphicsRendererGetCurrentNameSize(
    JNIEnv* environment, jclass type, jlongArray out_bytes)
{
    (void)environment;
    (void)type;
    uint64_t out_bytes_value = 0;
    CNA_Result result = cna.graphics_renderer_get_current_name_size(&out_bytes_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_bytes_element = (jlong)out_bytes_value;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &out_bytes_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeRuntimeExtensionRoutes_loggerGetMinimumLevel(
    JNIEnv* environment, jclass type, jintArray out_level)
{
    (void)environment;
    (void)type;
    CNA_LogLevel out_level_value = 0;
    CNA_Result result = cna.logger_get_minimum_level(&out_level_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_level_element = (jint)out_level_value;
        (*environment)->SetIntArrayRegion(
            environment, out_level, 0, 1, &out_level_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeRuntimeExtensionRoutes_loggerLog(
    JNIEnv* environment, jclass type, jint level, jbyteArray message, jint category, jboolean condition)
{
    (void)environment;
    (void)type;
    jsize message_size = (*environment)->GetArrayLength(environment, message);
    jbyte* message_bytes = (*environment)->GetByteArrayElements(environment, message, NULL);
    if (message_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    CNA_StringView message_view = {(const char*)message_bytes, (uint64_t)message_size};
    CNA_Result result = cna.logger_log((CNA_LogLevel)level, message_view, (CNA_LogCategory)category, (CNA_Bool)condition);
    (*environment)->ReleaseByteArrayElements(environment, message, message_bytes, JNI_ABORT);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeRuntimeExtensionRoutes_loggerSetMinimumLevel(
    JNIEnv* environment, jclass type, jint level)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.logger_set_minimum_level((CNA_LogLevel)level);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeRuntimeExtensionRoutes_platformCopyCurrentNameExt(
    JNIEnv* environment, jclass type, jbyteArray destination, jlongArray out_bytes)
{
    (void)environment;
    (void)type;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t destination_written = 0;
    CNA_Result result = cna.platform_copy_current_name_ext((char*)destination_bytes, (uint64_t)destination_size, &destination_written);
    (*environment)->ReleaseByteArrayElements(environment, destination, destination_bytes, 0);
    if (result == CNA_RESULT_SUCCESS) {
        jlong destination_element = (jlong)destination_written;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &destination_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeRuntimeExtensionRoutes_platformGetCurrent(
    JNIEnv* environment, jclass type, jintArray out_platform)
{
    (void)environment;
    (void)type;
    CNA_Platform out_platform_value = 0;
    CNA_Result result = cna.platform_get_current(&out_platform_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_platform_element = (jint)out_platform_value;
        (*environment)->SetIntArrayRegion(
            environment, out_platform, 0, 1, &out_platform_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeRuntimeExtensionRoutes_platformGetCurrentNameSizeExt(
    JNIEnv* environment, jclass type, jlongArray out_bytes)
{
    (void)environment;
    (void)type;
    uint64_t out_bytes_value = 0;
    CNA_Result result = cna.platform_get_current_name_size_ext(&out_bytes_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_bytes_element = (jlong)out_bytes_value;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &out_bytes_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeRuntimeExtensionRoutes_platformGetIsAppleExt(
    JNIEnv* environment, jclass type, jbooleanArray out_apple)
{
    (void)environment;
    (void)type;
    CNA_Bool out_apple_value = 0;
    CNA_Result result = cna.platform_get_is_apple_ext(&out_apple_value);
    if (result == CNA_RESULT_SUCCESS) {
        jboolean out_apple_element = (jboolean)out_apple_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_apple, 0, 1, &out_apple_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeRuntimeExtensionRoutes_platformGetIsMobileExt(
    JNIEnv* environment, jclass type, jbooleanArray out_mobile)
{
    (void)environment;
    (void)type;
    CNA_Bool out_mobile_value = 0;
    CNA_Result result = cna.platform_get_is_mobile_ext(&out_mobile_value);
    if (result == CNA_RESULT_SUCCESS) {
        jboolean out_mobile_element = (jboolean)out_mobile_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_mobile, 0, 1, &out_mobile_element);
    }
    return (jint)result;
}
