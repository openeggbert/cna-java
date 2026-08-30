/* SPDX-License-Identifier: MS-PL */
/*
 * Generated JNI adapter for NativeDeviceExtensionRoutes.
 *
 * Produced by tools/native-abi/generate_jni.py from the live CNA C headers, and
 * included by cna_java_jni.c so it shares the one dispatch table. Do not edit.
 */

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_devicesClipboardSetTextExt(
    JNIEnv* environment, jclass declaring_class, jlong game, jbyteArray text, jbooleanArray out_accepted)
{
    (void)environment;
    (void)declaring_class;
    jsize text_size = (*environment)->GetArrayLength(environment, text);
    jbyte* text_bytes = (*environment)->GetByteArrayElements(environment, text, NULL);
    if (text_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    CNA_StringView text_view = {(const char*)text_bytes, (uint64_t)text_size};
    CNA_Bool out_accepted_value = 0;
    CNA_Result result = cna.devices_clipboard_set_text_ext((CNA_Handle)game, text_view, &out_accepted_value);
    (*environment)->ReleaseByteArrayElements(environment, text, text_bytes, JNI_ABORT);
    if (result == CNA_RESULT_SUCCESS) {
        jboolean out_accepted_element = (jboolean)out_accepted_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_accepted, 0, 1, &out_accepted_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_devicesExtIsAvailable(
    JNIEnv* environment, jclass declaring_class, jbooleanArray out_available)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_available_value = 0;
    CNA_Result result = cna.devices_ext_is_available(&out_available_value);
    if (result == CNA_RESULT_SUCCESS) {
        jboolean out_available_element = (jboolean)out_available_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_available, 0, 1, &out_available_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_displayInfoGetContentScaleExt(
    JNIEnv* environment, jclass declaring_class, jlong game, jfloatArray out_scale)
{
    (void)environment;
    (void)declaring_class;
    float out_scale_value = 0;
    CNA_Result result = cna.display_info_get_content_scale_ext((CNA_Handle)game, &out_scale_value);
    if (result == CNA_RESULT_SUCCESS) {
        jfloat out_scale_element = (jfloat)out_scale_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_scale, 0, 1, &out_scale_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_displayInfoGetSafeAreaExt(
    JNIEnv* environment, jclass declaring_class, jlong game, jlongArray out_areaIntegral)
{
    (void)environment;
    (void)declaring_class;
    CNA_Rectangle out_area_value;
    memset(&out_area_value, 0, sizeof out_area_value);
    CNA_Result result = cna.display_info_get_safe_area_ext((CNA_Handle)game, &out_area_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_areaIntegral_values[] = {
            (jlong)out_area_value.x,
            (jlong)out_area_value.y,
            (jlong)out_area_value.width,
            (jlong)out_area_value.height
        };
        (*environment)->SetLongArrayRegion(environment, out_areaIntegral, 0,
            (jsize)(sizeof out_areaIntegral_values / sizeof out_areaIntegral_values[0]),
            out_areaIntegral_values);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_inputDevicesCopyKeyboardNameAt(
    JNIEnv* environment, jclass declaring_class, jlong game, jint index, jbyteArray destination, jlongArray out_bytes)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t destination_written = 0;
    CNA_Result result = cna.input_devices_copy_keyboard_name_at((CNA_Handle)game, (uint32_t)index, (char*)destination_bytes, (uint64_t)destination_size, &destination_written);
    (*environment)->ReleaseByteArrayElements(environment, destination, destination_bytes, 0);
    if (result == CNA_RESULT_SUCCESS) {
        jlong destination_element = (jlong)destination_written;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &destination_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_inputDevicesCopyMouseNameAt(
    JNIEnv* environment, jclass declaring_class, jlong game, jint index, jbyteArray destination, jlongArray out_bytes)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t destination_written = 0;
    CNA_Result result = cna.input_devices_copy_mouse_name_at((CNA_Handle)game, (uint32_t)index, (char*)destination_bytes, (uint64_t)destination_size, &destination_written);
    (*environment)->ReleaseByteArrayElements(environment, destination, destination_bytes, 0);
    if (result == CNA_RESULT_SUCCESS) {
        jlong destination_element = (jlong)destination_written;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &destination_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_inputDevicesCopyTouchDeviceNameAt(
    JNIEnv* environment, jclass declaring_class, jlong game, jint index, jbyteArray destination, jlongArray out_bytes)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t destination_written = 0;
    CNA_Result result = cna.input_devices_copy_touch_device_name_at((CNA_Handle)game, (uint32_t)index, (char*)destination_bytes, (uint64_t)destination_size, &destination_written);
    (*environment)->ReleaseByteArrayElements(environment, destination, destination_bytes, 0);
    if (result == CNA_RESULT_SUCCESS) {
        jlong destination_element = (jlong)destination_written;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &destination_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_inputDevicesGetKeyboardCount(
    JNIEnv* environment, jclass declaring_class, jlong game, jintArray out_count)
{
    (void)environment;
    (void)declaring_class;
    uint32_t out_count_value = 0;
    CNA_Result result = cna.input_devices_get_keyboard_count((CNA_Handle)game, &out_count_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_count_element = (jint)out_count_value;
        (*environment)->SetIntArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_inputDevicesGetKeyboardInfoAt(
    JNIEnv* environment, jclass declaring_class, jlong game, jint index, jlongArray out_infoIntegral)
{
    (void)environment;
    (void)declaring_class;
    CNA_InputDeviceInfo out_info_value;
    memset(&out_info_value, 0, sizeof out_info_value);
    out_info_value.struct_size = (uint32_t)(sizeof out_info_value);
    out_info_value.struct_version = UINT32_C(1);
    CNA_Result result = cna.input_devices_get_keyboard_info_at((CNA_Handle)game, (uint32_t)index, &out_info_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_infoIntegral_values[] = {
            (jlong)out_info_value.id
        };
        (*environment)->SetLongArrayRegion(environment, out_infoIntegral, 0,
            (jsize)(sizeof out_infoIntegral_values / sizeof out_infoIntegral_values[0]),
            out_infoIntegral_values);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_inputDevicesGetKeyboardNameSizeAt(
    JNIEnv* environment, jclass declaring_class, jlong game, jint index, jlongArray out_bytes)
{
    (void)environment;
    (void)declaring_class;
    uint64_t out_bytes_value = 0;
    CNA_Result result = cna.input_devices_get_keyboard_name_size_at((CNA_Handle)game, (uint32_t)index, &out_bytes_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_bytes_element = (jlong)out_bytes_value;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &out_bytes_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_inputDevicesGetMouseCount(
    JNIEnv* environment, jclass declaring_class, jlong game, jintArray out_count)
{
    (void)environment;
    (void)declaring_class;
    uint32_t out_count_value = 0;
    CNA_Result result = cna.input_devices_get_mouse_count((CNA_Handle)game, &out_count_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_count_element = (jint)out_count_value;
        (*environment)->SetIntArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_inputDevicesGetMouseInfoAt(
    JNIEnv* environment, jclass declaring_class, jlong game, jint index, jlongArray out_infoIntegral)
{
    (void)environment;
    (void)declaring_class;
    CNA_InputDeviceInfo out_info_value;
    memset(&out_info_value, 0, sizeof out_info_value);
    out_info_value.struct_size = (uint32_t)(sizeof out_info_value);
    out_info_value.struct_version = UINT32_C(1);
    CNA_Result result = cna.input_devices_get_mouse_info_at((CNA_Handle)game, (uint32_t)index, &out_info_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_infoIntegral_values[] = {
            (jlong)out_info_value.id
        };
        (*environment)->SetLongArrayRegion(environment, out_infoIntegral, 0,
            (jsize)(sizeof out_infoIntegral_values / sizeof out_infoIntegral_values[0]),
            out_infoIntegral_values);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_inputDevicesGetMouseNameSizeAt(
    JNIEnv* environment, jclass declaring_class, jlong game, jint index, jlongArray out_bytes)
{
    (void)environment;
    (void)declaring_class;
    uint64_t out_bytes_value = 0;
    CNA_Result result = cna.input_devices_get_mouse_name_size_at((CNA_Handle)game, (uint32_t)index, &out_bytes_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_bytes_element = (jlong)out_bytes_value;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &out_bytes_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_inputDevicesGetTouchDeviceCount(
    JNIEnv* environment, jclass declaring_class, jlong game, jintArray out_count)
{
    (void)environment;
    (void)declaring_class;
    uint32_t out_count_value = 0;
    CNA_Result result = cna.input_devices_get_touch_device_count((CNA_Handle)game, &out_count_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_count_element = (jint)out_count_value;
        (*environment)->SetIntArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_inputDevicesGetTouchDeviceInfoAt(
    JNIEnv* environment, jclass declaring_class, jlong game, jint index, jlongArray out_infoIntegral)
{
    (void)environment;
    (void)declaring_class;
    CNA_InputDeviceInfo out_info_value;
    memset(&out_info_value, 0, sizeof out_info_value);
    out_info_value.struct_size = (uint32_t)(sizeof out_info_value);
    out_info_value.struct_version = UINT32_C(1);
    CNA_Result result = cna.input_devices_get_touch_device_info_at((CNA_Handle)game, (uint32_t)index, &out_info_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_infoIntegral_values[] = {
            (jlong)out_info_value.id
        };
        (*environment)->SetLongArrayRegion(environment, out_infoIntegral, 0,
            (jsize)(sizeof out_infoIntegral_values / sizeof out_infoIntegral_values[0]),
            out_infoIntegral_values);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_inputDevicesGetTouchDeviceNameSizeAt(
    JNIEnv* environment, jclass declaring_class, jlong game, jint index, jlongArray out_bytes)
{
    (void)environment;
    (void)declaring_class;
    uint64_t out_bytes_value = 0;
    CNA_Result result = cna.input_devices_get_touch_device_name_size_at((CNA_Handle)game, (uint32_t)index, &out_bytes_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_bytes_element = (jlong)out_bytes_value;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &out_bytes_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_inputDevicesRaiseKeyboardConnectedExt(
    JNIEnv* environment, jclass declaring_class, jlong game, jint id)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result result = cna.input_devices_raise_keyboard_connected_ext((CNA_Handle)game, (uint32_t)id);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_inputDevicesRaiseKeyboardDisconnectedExt(
    JNIEnv* environment, jclass declaring_class, jlong game, jint id)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result result = cna.input_devices_raise_keyboard_disconnected_ext((CNA_Handle)game, (uint32_t)id);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_inputDevicesRaiseMouseConnectedExt(
    JNIEnv* environment, jclass declaring_class, jlong game, jint id)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result result = cna.input_devices_raise_mouse_connected_ext((CNA_Handle)game, (uint32_t)id);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_inputDevicesRaiseMouseDisconnectedExt(
    JNIEnv* environment, jclass declaring_class, jlong game, jint id)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result result = cna.input_devices_raise_mouse_disconnected_ext((CNA_Handle)game, (uint32_t)id);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_localeCopyCountryAtExt(
    JNIEnv* environment, jclass declaring_class, jlong game, jlong index, jbyteArray destination, jlongArray out_bytes)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t destination_written = 0;
    CNA_Result result = cna.locale_copy_country_at_ext((CNA_Handle)game, (uint64_t)index, (char*)destination_bytes, (uint64_t)destination_size, &destination_written);
    (*environment)->ReleaseByteArrayElements(environment, destination, destination_bytes, 0);
    if (result == CNA_RESULT_SUCCESS) {
        jlong destination_element = (jlong)destination_written;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &destination_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_localeCopyLanguageAtExt(
    JNIEnv* environment, jclass declaring_class, jlong game, jlong index, jbyteArray destination, jlongArray out_bytes)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t destination_written = 0;
    CNA_Result result = cna.locale_copy_language_at_ext((CNA_Handle)game, (uint64_t)index, (char*)destination_bytes, (uint64_t)destination_size, &destination_written);
    (*environment)->ReleaseByteArrayElements(environment, destination, destination_bytes, 0);
    if (result == CNA_RESULT_SUCCESS) {
        jlong destination_element = (jlong)destination_written;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &destination_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_localeGetCountrySizeAtExt(
    JNIEnv* environment, jclass declaring_class, jlong game, jlong index, jlongArray out_bytes)
{
    (void)environment;
    (void)declaring_class;
    uint64_t out_bytes_value = 0;
    CNA_Result result = cna.locale_get_country_size_at_ext((CNA_Handle)game, (uint64_t)index, &out_bytes_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_bytes_element = (jlong)out_bytes_value;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &out_bytes_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_localeGetLanguageSizeAtExt(
    JNIEnv* environment, jclass declaring_class, jlong game, jlong index, jlongArray out_bytes)
{
    (void)environment;
    (void)declaring_class;
    uint64_t out_bytes_value = 0;
    CNA_Result result = cna.locale_get_language_size_at_ext((CNA_Handle)game, (uint64_t)index, &out_bytes_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_bytes_element = (jlong)out_bytes_value;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &out_bytes_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_localeGetPreferredCountExt(
    JNIEnv* environment, jclass declaring_class, jlong game, jlongArray out_count)
{
    (void)environment;
    (void)declaring_class;
    uint64_t out_count_value = 0;
    CNA_Result result = cna.locale_get_preferred_count_ext((CNA_Handle)game, &out_count_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_count_element = (jlong)out_count_value;
        (*environment)->SetLongArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_powerGetBatteryPercentExt(
    JNIEnv* environment, jclass declaring_class, jlong game, jintArray out_percent)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_percent_value = 0;
    CNA_Result result = cna.power_get_battery_percent_ext((CNA_Handle)game, &out_percent_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_percent_element = (jint)out_percent_value;
        (*environment)->SetIntArrayRegion(
            environment, out_percent, 0, 1, &out_percent_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_powerGetSecondsRemainingExt(
    JNIEnv* environment, jclass declaring_class, jlong game, jintArray out_seconds)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_seconds_value = 0;
    CNA_Result result = cna.power_get_seconds_remaining_ext((CNA_Handle)game, &out_seconds_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_seconds_element = (jint)out_seconds_value;
        (*environment)->SetIntArrayRegion(
            environment, out_seconds, 0, 1, &out_seconds_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_powerGetStateExt(
    JNIEnv* environment, jclass declaring_class, jlong game, jintArray out_state)
{
    (void)environment;
    (void)declaring_class;
    CNA_PowerState out_state_value = 0;
    CNA_Result result = cna.power_get_state_ext((CNA_Handle)game, &out_state_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_state_element = (jint)out_state_value;
        (*environment)->SetIntArrayRegion(
            environment, out_state, 0, 1, &out_state_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_systemInfoGetLogicalCpuCoreCountExt(
    JNIEnv* environment, jclass declaring_class, jlong game, jintArray out_count)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_count_value = 0;
    CNA_Result result = cna.system_info_get_logical_cpu_core_count_ext((CNA_Handle)game, &out_count_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_count_element = (jint)out_count_value;
        (*environment)->SetIntArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_systemInfoGetSystemRamMegabytesExt(
    JNIEnv* environment, jclass declaring_class, jlong game, jintArray out_megabytes)
{
    (void)environment;
    (void)declaring_class;
    int32_t out_megabytes_value = 0;
    CNA_Result result = cna.system_info_get_system_ram_megabytes_ext((CNA_Handle)game, &out_megabytes_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_megabytes_element = (jint)out_megabytes_value;
        (*environment)->SetIntArrayRegion(
            environment, out_megabytes, 0, 1, &out_megabytes_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_urlLauncherOpenExt(
    JNIEnv* environment, jclass declaring_class, jlong game, jbyteArray url, jbooleanArray out_opened)
{
    (void)environment;
    (void)declaring_class;
    jsize url_size = (*environment)->GetArrayLength(environment, url);
    jbyte* url_bytes = (*environment)->GetByteArrayElements(environment, url, NULL);
    if (url_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    CNA_StringView url_view = {(const char*)url_bytes, (uint64_t)url_size};
    CNA_Bool out_opened_value = 0;
    CNA_Result result = cna.url_launcher_open_ext((CNA_Handle)game, url_view, &out_opened_value);
    (*environment)->ReleaseByteArrayElements(environment, url, url_bytes, JNI_ABORT);
    if (result == CNA_RESULT_SUCCESS) {
        jboolean out_opened_element = (jboolean)out_opened_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_opened, 0, 1, &out_opened_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_vibrateControllerCopyDeviceNameExt(
    JNIEnv* environment, jclass declaring_class, jlong game, jbyteArray destination, jlongArray out_bytes)
{
    (void)environment;
    (void)declaring_class;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t destination_written = 0;
    CNA_Result result = cna.vibrate_controller_copy_device_name_ext((CNA_Handle)game, (char*)destination_bytes, (uint64_t)destination_size, &destination_written);
    (*environment)->ReleaseByteArrayElements(environment, destination, destination_bytes, 0);
    if (result == CNA_RESULT_SUCCESS) {
        jlong destination_element = (jlong)destination_written;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &destination_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_vibrateControllerGetDeviceNameSizeExt(
    JNIEnv* environment, jclass declaring_class, jlong game, jlongArray out_bytes)
{
    (void)environment;
    (void)declaring_class;
    uint64_t out_bytes_value = 0;
    CNA_Result result = cna.vibrate_controller_get_device_name_size_ext((CNA_Handle)game, &out_bytes_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_bytes_element = (jlong)out_bytes_value;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &out_bytes_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_vibrateControllerGetIsSupportedExt(
    JNIEnv* environment, jclass declaring_class, jlong game, jbooleanArray out_supported)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_supported_value = 0;
    CNA_Result result = cna.vibrate_controller_get_is_supported_ext((CNA_Handle)game, &out_supported_value);
    if (result == CNA_RESULT_SUCCESS) {
        jboolean out_supported_element = (jboolean)out_supported_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_supported, 0, 1, &out_supported_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_vibrateControllerStart(
    JNIEnv* environment, jclass declaring_class, jlong game, jlong duration_ticks)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result result = cna.vibrate_controller_start((CNA_Handle)game, (int64_t)duration_ticks);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_vibrateControllerStartLeftRightExt(
    JNIEnv* environment, jclass declaring_class, jlong game, jfloat large_motor, jfloat small_motor, jlong duration_ticks)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result result = cna.vibrate_controller_start_left_right_ext((CNA_Handle)game, (float)large_motor, (float)small_motor, (int64_t)duration_ticks);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_vibrateControllerStartWithIntensityExt(
    JNIEnv* environment, jclass declaring_class, jlong game, jlong duration_ticks, jfloat intensity)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result result = cna.vibrate_controller_start_with_intensity_ext((CNA_Handle)game, (int64_t)duration_ticks, (float)intensity);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeDeviceExtensionRoutes_vibrateControllerStop(
    JNIEnv* environment, jclass declaring_class, jlong game)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result result = cna.vibrate_controller_stop((CNA_Handle)game);
    return (jint)result;
}
