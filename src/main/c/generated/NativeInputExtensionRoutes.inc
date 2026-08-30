/* SPDX-License-Identifier: MS-PL */
/*
 * Generated JNI adapter for NativeInputExtensionRoutes.
 *
 * Produced by tools/native-abi/generate_jni.py from the live CNA C headers, and
 * included by cna_java_jni.c so it shares the one dispatch table. Do not edit.
 */

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeInputExtensionRoutes_mouseCursorCreateExt(
    JNIEnv* environment, jclass declaring_class, jlongArray out_cursor)
{
    (void)environment;
    (void)declaring_class;
    CNA_MouseCursorHandle out_cursor_value = 0;
    CNA_Result result = cna.mouse_cursor_create_ext(&out_cursor_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_cursor_element = (jlong)out_cursor_value;
        (*environment)->SetLongArrayRegion(
            environment, out_cursor, 0, 1, &out_cursor_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeInputExtensionRoutes_mouseCursorCreateFromTexture2d(
    JNIEnv* environment, jclass declaring_class, jlong game, jlong texture, jint origin_x, jint origin_y, jlongArray out_cursor)
{
    (void)environment;
    (void)declaring_class;
    CNA_MouseCursorHandle out_cursor_value = 0;
    CNA_Result result = cna.mouse_cursor_create_from_texture2d((CNA_Handle)game, (CNA_Handle)texture, (int32_t)origin_x, (int32_t)origin_y, &out_cursor_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_cursor_element = (jlong)out_cursor_value;
        (*environment)->SetLongArrayRegion(
            environment, out_cursor, 0, 1, &out_cursor_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeInputExtensionRoutes_mouseCursorDestroy(
    JNIEnv* environment, jclass declaring_class, jlong cursor)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result result = cna.mouse_cursor_destroy((CNA_MouseCursorHandle)cursor);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeInputExtensionRoutes_mouseCursorDispose(
    JNIEnv* environment, jclass declaring_class, jlong cursor)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result result = cna.mouse_cursor_dispose((CNA_MouseCursorHandle)cursor);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeInputExtensionRoutes_mouseCursorGetStockExt(
    JNIEnv* environment, jclass declaring_class, jlong game, jint stock, jlongArray out_cursor)
{
    (void)environment;
    (void)declaring_class;
    CNA_MouseCursorHandle out_cursor_value = 0;
    CNA_Result result = cna.mouse_cursor_get_stock_ext((CNA_Handle)game, (CNA_MouseCursorStock)stock, &out_cursor_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_cursor_element = (jlong)out_cursor_value;
        (*environment)->SetLongArrayRegion(
            environment, out_cursor, 0, 1, &out_cursor_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeInputExtensionRoutes_mouseSetCursorExt(
    JNIEnv* environment, jclass declaring_class, jlong game, jlong cursor)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result result = cna.mouse_set_cursor_ext((CNA_Handle)game, (CNA_MouseCursorHandle)cursor);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeInputExtensionRoutes_textInputIsActiveExt(
    JNIEnv* environment, jclass declaring_class, jlong game, jbooleanArray out_active)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_active_value = 0;
    CNA_Result result = cna.text_input_is_active_ext((CNA_Handle)game, &out_active_value);
    if (result == CNA_RESULT_SUCCESS) {
        jboolean out_active_element = (jboolean)out_active_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_active, 0, 1, &out_active_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeInputExtensionRoutes_textInputIsScreenKeyboardShownExt(
    JNIEnv* environment, jclass declaring_class, jlong game, jbooleanArray out_shown)
{
    (void)environment;
    (void)declaring_class;
    CNA_Bool out_shown_value = 0;
    CNA_Result result = cna.text_input_is_screen_keyboard_shown_ext((CNA_Handle)game, &out_shown_value);
    if (result == CNA_RESULT_SUCCESS) {
        jboolean out_shown_element = (jboolean)out_shown_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_shown, 0, 1, &out_shown_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeInputExtensionRoutes_textInputSetInputRectangleExt(
    JNIEnv* environment, jclass declaring_class, jlong game, jlongArray rectangleIntegral)
{
    (void)environment;
    (void)declaring_class;
    CNA_Rectangle rectangle_value;
    memset(&rectangle_value, 0, sizeof rectangle_value);
    {
        jlong rectangleIntegral_values[4];
        (*environment)->GetLongArrayRegion(environment, rectangleIntegral, 0, 4, rectangleIntegral_values);
        rectangle_value.x = (int32_t)rectangleIntegral_values[0];
        rectangle_value.y = (int32_t)rectangleIntegral_values[1];
        rectangle_value.width = (int32_t)rectangleIntegral_values[2];
        rectangle_value.height = (int32_t)rectangleIntegral_values[3];
    }
    CNA_Result result = cna.text_input_set_input_rectangle_ext((CNA_Handle)game, rectangle_value);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeInputExtensionRoutes_textInputStartExt(
    JNIEnv* environment, jclass declaring_class, jlong game)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result result = cna.text_input_start_ext((CNA_Handle)game);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeInputExtensionRoutes_textInputStartWithTypeExt(
    JNIEnv* environment, jclass declaring_class, jlong game, jint type)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result result = cna.text_input_start_with_type_ext((CNA_Handle)game, (CNA_TextInputType)type);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeInputExtensionRoutes_textInputStopExt(
    JNIEnv* environment, jclass declaring_class, jlong game)
{
    (void)environment;
    (void)declaring_class;
    CNA_Result result = cna.text_input_stop_ext((CNA_Handle)game);
    return (jint)result;
}
