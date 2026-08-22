// SPDX-License-Identifier: MS-PL

#include <jni.h>

#include <CNA/C/cna.h>

#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#if defined(_WIN32)
#include <windows.h>
typedef HMODULE DynamicLibrary;
#define CNA_DEFAULT_LIBRARY "cna_c_api.dll"
static DynamicLibrary open_library(const char* path) { return LoadLibraryA(path); }
static void close_library(DynamicLibrary library) { (void)FreeLibrary(library); }
static void* load_symbol(DynamicLibrary library, const char* name)
{
    return (void*)(uintptr_t)GetProcAddress(library, name);
}
static const char* loader_error(void) { return "LoadLibrary/GetProcAddress failed"; }
#else
#include <dlfcn.h>
typedef void* DynamicLibrary;
#if defined(__APPLE__)
#define CNA_DEFAULT_LIBRARY "libcna_c_api.dylib"
#else
#define CNA_DEFAULT_LIBRARY "libcna_c_api.so"
#endif
static DynamicLibrary open_library(const char* path) { return dlopen(path, RTLD_NOW | RTLD_LOCAL); }
static void close_library(DynamicLibrary library) { (void)dlclose(library); }
static void* load_symbol(DynamicLibrary library, const char* name) { return dlsym(library, name); }
static const char* loader_error(void)
{
    const char* error = dlerror();
    return error == NULL ? "dynamic loader failed without a diagnostic" : error;
}
#endif

typedef uint32_t (*GetAbiVersionFunction)(void);
typedef CNA_Result (*ErrorMessageSizeFunction)(uint64_t*);
typedef CNA_Result (*ErrorMessageCopyFunction)(char*, uint64_t, uint64_t*);
typedef CNA_Result (*GameCreateFunction)(const CNA_GameCreateInfo*, CNA_Handle*);
typedef CNA_Result (*GameSetHooksFunction)(CNA_Handle, const CNA_GameFrameHooks*);
typedef CNA_Result (*GameUnaryFunction)(CNA_Handle);
typedef CNA_Result (*GameClearFunction)(CNA_Handle, CNA_Color);
typedef CNA_Result (*GameSetBoolFunction)(CNA_Handle, CNA_Bool);
typedef CNA_Result (*GameGetBoolFunction)(CNA_Handle, CNA_Bool*);
typedef CNA_Result (*GameSetInt64Function)(CNA_Handle, int64_t);
typedef CNA_Result (*GameGetInt64Function)(CNA_Handle, int64_t*);
typedef CNA_Result (*GameGetRectangleFunction)(CNA_Handle, CNA_Rectangle*);
typedef CNA_Result (*GameGetUint32Function)(CNA_Handle, uint32_t*);
typedef CNA_Result (*GameGetUint64Function)(CNA_Handle, uint64_t*);
typedef CNA_Result (*GameGetSizeFunction)(CNA_Handle, uint64_t*);
typedef CNA_Result (*GameCopyStringFunction)(CNA_Handle, char*, uint64_t, uint64_t*);
typedef CNA_Result (*GameSetStringFunction)(CNA_Handle, CNA_StringView);
typedef CNA_Result (*GameEndScreenChangeFunction)(CNA_Handle, CNA_StringView, int32_t, int32_t);
typedef CNA_Result (*KeyboardGetStateFunction)(CNA_Handle, CNA_KeyboardState*);
typedef CNA_Result (*KeyboardGetStateForPlayerFunction)(
    CNA_Handle, CNA_PlayerIndex, CNA_KeyboardState*);
typedef CNA_Result (*MouseGetStateFunction)(CNA_Handle, CNA_MouseState*);
typedef CNA_Result (*MouseSetPositionFunction)(CNA_Handle, int32_t, int32_t);
typedef CNA_Result (*GameSetUint64Function)(CNA_Handle, uint64_t);

typedef struct CnaFunctions {
    DynamicLibrary library;
    GetAbiVersionFunction get_abi_version;
    ErrorMessageSizeFunction error_message_size;
    ErrorMessageCopyFunction error_message_copy;
    GameCreateFunction game_create;
    GameSetHooksFunction game_set_hooks;
    GameUnaryFunction game_run;
    GameUnaryFunction game_run_one_frame;
    GameUnaryFunction game_request_exit;
    GameUnaryFunction game_reset_elapsed_time;
    GameUnaryFunction game_suppress_draw;
    GameUnaryFunction game_tick;
    GameUnaryFunction game_destroy;
    GameClearFunction game_clear;
    GameSetBoolFunction game_set_mouse_visible;
    GameGetBoolFunction game_get_mouse_visible;
    GameGetBoolFunction game_get_is_active;
    GameSetBoolFunction game_set_fixed_time_step;
    GameGetBoolFunction game_get_fixed_time_step;
    GameSetInt64Function game_set_target_elapsed_time;
    GameGetInt64Function game_get_target_elapsed_time;
    GameSetInt64Function game_set_inactive_sleep_time;
    GameGetInt64Function game_get_inactive_sleep_time;
    GameGetBoolFunction game_window_get_allow_user_resizing;
    GameSetBoolFunction game_window_set_allow_user_resizing;
    GameGetRectangleFunction game_window_get_client_bounds;
    GameGetUint32Function game_window_get_current_orientation;
    GameGetUint64Function game_window_get_native_handle;
    GameGetSizeFunction game_window_get_screen_device_name_size;
    GameCopyStringFunction game_window_copy_screen_device_name;
    GameSetStringFunction game_set_window_title;
    GameSetBoolFunction game_window_begin_screen_device_change;
    GameEndScreenChangeFunction game_window_end_screen_device_change;
    KeyboardGetStateFunction keyboard_get_state;
    KeyboardGetStateForPlayerFunction keyboard_get_state_for_player;
    MouseGetStateFunction mouse_get_state;
    MouseSetPositionFunction mouse_set_position;
    GameGetUint64Function mouse_get_window_handle;
    GameSetUint64Function mouse_set_window_handle;
} CnaFunctions;

typedef struct JavaGameContext {
    jobject game;
    jmethodID initialize;
    jmethodID load_content;
    jmethodID begin_run;
    jmethodID update;
    jmethodID begin_draw;
    jmethodID draw;
    jmethodID end_draw;
    jmethodID end_run;
    jmethodID unload_content;
    jmethodID exiting;
    char callback_error[512];
} JavaGameContext;

typedef struct JavaGame {
    CNA_Handle cna_handle;
    JavaGameContext* context;
} JavaGame;

static JavaVM* java_vm;
static CnaFunctions cna;

static void throw_link_error(JNIEnv* environment, const char* message)
{
    jclass type = (*environment)->FindClass(environment, "java/lang/UnsatisfiedLinkError");
    if (type != NULL) {
        (*environment)->ThrowNew(environment, type, message);
    }
}

static int load_required(JNIEnv* environment, void** destination, const char* name)
{
    *destination = load_symbol(cna.library, name);
    if (*destination == NULL) {
        char message[768];
        (void)snprintf(message, sizeof(message), "Missing CNA C ABI symbol %s: %s", name, loader_error());
        throw_link_error(environment, message);
        return 0;
    }
    return 1;
}

static JNIEnv* callback_environment(int* attached)
{
    JNIEnv* environment = NULL;
    *attached = 0;
    if ((*java_vm)->GetEnv(java_vm, (void**)&environment, JNI_VERSION_1_8) == JNI_OK) {
        return environment;
    }
#if defined(__ANDROID__) || defined(ANDROID)
    if ((*java_vm)->AttachCurrentThread(java_vm, &environment, NULL) != JNI_OK) {
#else
    if ((*java_vm)->AttachCurrentThread(java_vm, (void**)&environment, NULL) != JNI_OK) {
#endif
        return NULL;
    }
    *attached = 1;
    return environment;
}

static void finish_callback_environment(int attached)
{
    if (attached != 0) {
        (void)(*java_vm)->DetachCurrentThread(java_vm);
    }
}

static CNA_Result capture_java_exception(
    JNIEnv* environment,
    JavaGameContext* context,
    CNA_CallbackError* out_error)
{
    jthrowable throwable = (*environment)->ExceptionOccurred(environment);
    if (throwable == NULL) {
        return CNA_RESULT_SUCCESS;
    }
    (*environment)->ExceptionClear(environment);
    (void)snprintf(context->callback_error, sizeof(context->callback_error),
        "Java lifecycle callback threw an exception");

    jclass throwable_class = (*environment)->GetObjectClass(environment, throwable);
    if (throwable_class != NULL) {
        jmethodID to_string = (*environment)->GetMethodID(
            environment, throwable_class, "toString", "()Ljava/lang/String;");
        if (to_string != NULL) {
            jstring text = (jstring)(*environment)->CallObjectMethod(environment, throwable, to_string);
            if (!(*environment)->ExceptionCheck(environment) && text != NULL) {
                const char* utf = (*environment)->GetStringUTFChars(environment, text, NULL);
                if (utf != NULL) {
                    (void)snprintf(context->callback_error, sizeof(context->callback_error), "%s", utf);
                    (*environment)->ReleaseStringUTFChars(environment, text, utf);
                }
                (*environment)->DeleteLocalRef(environment, text);
            } else if ((*environment)->ExceptionCheck(environment)) {
                (*environment)->ExceptionClear(environment);
            }
        }
        (*environment)->DeleteLocalRef(environment, throwable_class);
    }
    (*environment)->DeleteLocalRef(environment, throwable);

    if (out_error != NULL) {
        out_error->message.data = context->callback_error;
        out_error->message.byte_length = (uint64_t)strlen(context->callback_error);
    }
    return CNA_RESULT_CALLBACK;
}

static CNA_Result invoke_void(
    JavaGameContext* context,
    jmethodID method,
    const CNA_GameTime* game_time,
    CNA_CallbackError* out_error)
{
    int attached = 0;
    JNIEnv* environment = callback_environment(&attached);
    if (environment == NULL) {
        return CNA_RESULT_CALLBACK;
    }
    if (game_time == NULL) {
        (*environment)->CallVoidMethod(environment, context->game, method);
    } else {
        (*environment)->CallVoidMethod(environment, context->game, method,
            (jlong)game_time->total_game_time_ticks,
            (jlong)game_time->elapsed_game_time_ticks,
            game_time->is_running_slowly == CNA_TRUE ? JNI_TRUE : JNI_FALSE);
    }
    CNA_Result result = capture_java_exception(environment, context, out_error);
    finish_callback_environment(attached);
    return result;
}

static CNA_Result on_initialize(CNA_Handle game, const CNA_GameTime* game_time, void* value, CNA_CallbackError* error)
{
    (void)game;
    (void)game_time;
    JavaGameContext* context = (JavaGameContext*)value;
    return invoke_void(context, context->initialize, NULL, error);
}

static CNA_Result on_load(CNA_Handle game, const CNA_GameTime* game_time, void* value, CNA_CallbackError* error)
{
    (void)game;
    (void)game_time;
    JavaGameContext* context = (JavaGameContext*)value;
    return invoke_void(context, context->load_content, NULL, error);
}

static CNA_Result on_begin_run(CNA_Handle game, const CNA_GameTime* game_time, void* value, CNA_CallbackError* error)
{
    (void)game;
    (void)game_time;
    JavaGameContext* context = (JavaGameContext*)value;
    return invoke_void(context, context->begin_run, NULL, error);
}

static CNA_Result on_update(CNA_Handle game, const CNA_GameTime* game_time, void* value, CNA_CallbackError* error)
{
    (void)game;
    JavaGameContext* context = (JavaGameContext*)value;
    return invoke_void(context, context->update, game_time, error);
}

static CNA_Result on_draw(CNA_Handle game, const CNA_GameTime* game_time, void* value, CNA_CallbackError* error)
{
    (void)game;
    JavaGameContext* context = (JavaGameContext*)value;
    return invoke_void(context, context->draw, game_time, error);
}

static CNA_Result on_begin_draw(
    CNA_Handle game,
    const CNA_GameTime* game_time,
    void* value,
    CNA_Bool* out_should_draw,
    CNA_CallbackError* error)
{
    (void)game;
    (void)game_time;
    JavaGameContext* context = (JavaGameContext*)value;
    int attached = 0;
    JNIEnv* environment = callback_environment(&attached);
    if (environment == NULL) {
        return CNA_RESULT_CALLBACK;
    }
    jboolean should_draw = (*environment)->CallBooleanMethod(
        environment, context->game, context->begin_draw);
    CNA_Result result = capture_java_exception(environment, context, error);
    if (result == CNA_RESULT_SUCCESS) {
        *out_should_draw = should_draw == JNI_TRUE ? CNA_TRUE : CNA_FALSE;
    }
    finish_callback_environment(attached);
    return result;
}

static CNA_Result on_end_draw(CNA_Handle game, const CNA_GameTime* game_time, void* value, CNA_CallbackError* error)
{
    (void)game;
    (void)game_time;
    JavaGameContext* context = (JavaGameContext*)value;
    return invoke_void(context, context->end_draw, NULL, error);
}

static CNA_Result on_end_run(CNA_Handle game, const CNA_GameTime* game_time, void* value, CNA_CallbackError* error)
{
    (void)game;
    (void)game_time;
    JavaGameContext* context = (JavaGameContext*)value;
    return invoke_void(context, context->end_run, NULL, error);
}

static CNA_Result on_unload(CNA_Handle game, const CNA_GameTime* game_time, void* value, CNA_CallbackError* error)
{
    (void)game;
    (void)game_time;
    JavaGameContext* context = (JavaGameContext*)value;
    return invoke_void(context, context->unload_content, NULL, error);
}

static CNA_Result on_exiting(CNA_Handle game, const CNA_GameTime* game_time, void* value, CNA_CallbackError* error)
{
    (void)game;
    (void)game_time;
    JavaGameContext* context = (JavaGameContext*)value;
    return invoke_void(context, context->exiting, NULL, error);
}

static JavaGame* java_game(jlong value)
{
    return (JavaGame*)(uintptr_t)value;
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* virtual_machine, void* reserved)
{
    (void)reserved;
    java_vm = virtual_machine;
    return JNI_VERSION_1_8;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeLoadCna(
    JNIEnv* environment,
    jclass type,
    jstring path)
{
    (void)type;
    if (cna.library != NULL) {
        return (jint)cna.get_abi_version();
    }

    const char* selected = CNA_DEFAULT_LIBRARY;
    const char* explicit_path = NULL;
    if (path != NULL) {
        explicit_path = (*environment)->GetStringUTFChars(environment, path, NULL);
        if (explicit_path == NULL) {
            return 0;
        }
        selected = explicit_path;
    }
    cna.library = open_library(selected);
    if (cna.library == NULL) {
        char message[768];
        (void)snprintf(message, sizeof(message), "Unable to load CNA C ABI library %s: %s", selected, loader_error());
        if (explicit_path != NULL) {
            (*environment)->ReleaseStringUTFChars(environment, path, explicit_path);
        }
        throw_link_error(environment, message);
        return 0;
    }
    if (explicit_path != NULL) {
        (*environment)->ReleaseStringUTFChars(environment, path, explicit_path);
    }

#define LOAD(field, name) \
    if (!load_required(environment, (void**)&cna.field, name)) goto load_failed
    LOAD(get_abi_version, "cna_get_abi_version");
    LOAD(error_message_size, "cna_error_get_last_message_size");
    LOAD(error_message_copy, "cna_error_copy_last_message");
    LOAD(game_create, "cna_game_create");
    LOAD(game_set_hooks, "cna_game_set_frame_hooks_ext");
    LOAD(game_run, "cna_game_run");
    LOAD(game_run_one_frame, "cna_game_run_one_frame");
    LOAD(game_request_exit, "cna_game_request_exit");
    LOAD(game_reset_elapsed_time, "cna_game_reset_elapsed_time");
    LOAD(game_suppress_draw, "cna_game_suppress_draw");
    LOAD(game_tick, "cna_game_tick");
    LOAD(game_destroy, "cna_game_destroy");
    LOAD(game_clear, "cna_game_clear");
    LOAD(game_set_mouse_visible, "cna_game_set_is_mouse_visible");
    LOAD(game_get_mouse_visible, "cna_game_get_is_mouse_visible");
    LOAD(game_get_is_active, "cna_game_get_is_active");
    LOAD(game_set_fixed_time_step, "cna_game_set_is_fixed_time_step");
    LOAD(game_get_fixed_time_step, "cna_game_get_is_fixed_time_step");
    LOAD(game_set_target_elapsed_time, "cna_game_set_target_elapsed_time_ticks");
    LOAD(game_get_target_elapsed_time, "cna_game_get_target_elapsed_time_ticks");
    LOAD(game_set_inactive_sleep_time, "cna_game_set_inactive_sleep_time_ticks");
    LOAD(game_get_inactive_sleep_time, "cna_game_get_inactive_sleep_time_ticks");
    LOAD(game_window_get_allow_user_resizing, "cna_game_window_get_allow_user_resizing");
    LOAD(game_window_set_allow_user_resizing, "cna_game_window_set_allow_user_resizing");
    LOAD(game_window_get_client_bounds, "cna_game_window_get_client_bounds");
    LOAD(game_window_get_current_orientation, "cna_game_window_get_current_orientation");
    LOAD(game_window_get_native_handle, "cna_game_window_get_native_handle_ext");
    LOAD(game_window_get_screen_device_name_size, "cna_game_window_get_screen_device_name_size");
    LOAD(game_window_copy_screen_device_name, "cna_game_window_copy_screen_device_name");
    LOAD(game_set_window_title, "cna_game_set_window_title");
    LOAD(game_window_begin_screen_device_change, "cna_game_window_begin_screen_device_change");
    LOAD(game_window_end_screen_device_change, "cna_game_window_end_screen_device_change");
    LOAD(keyboard_get_state, "cna_keyboard_get_state");
    LOAD(keyboard_get_state_for_player, "cna_keyboard_get_state_for_player");
    LOAD(mouse_get_state, "cna_mouse_get_state");
    LOAD(mouse_set_position, "cna_mouse_set_position");
    LOAD(mouse_get_window_handle, "cna_mouse_get_window_handle");
    LOAD(mouse_set_window_handle, "cna_mouse_set_window_handle");
#undef LOAD

    return (jint)cna.get_abi_version();

load_failed:
    close_library(cna.library);
    (void)memset(&cna, 0, sizeof(cna));
    return 0;
}

JNIEXPORT jlong JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeCreateGame(
    JNIEnv* environment,
    jclass type,
    jobject game,
    jbyteArray title,
    jboolean fixed_time_step,
    jlong target_ticks)
{
    (void)type;
    JavaGame* wrapper = (JavaGame*)calloc(1U, sizeof(JavaGame));
    JavaGameContext* context = (JavaGameContext*)calloc(1U, sizeof(JavaGameContext));
    if (wrapper == NULL || context == NULL) {
        free(wrapper);
        free(context);
        return 0;
    }
    wrapper->context = context;
    context->game = (*environment)->NewGlobalRef(environment, game);
    if (context->game == NULL) {
        free(wrapper);
        free(context);
        return 0;
    }

    jclass game_class = (*environment)->GetObjectClass(environment, game);
#define METHOD(field, name, signature) \
    context->field = (*environment)->GetMethodID(environment, game_class, name, signature); \
    if (context->field == NULL) goto create_failed
    METHOD(initialize, "nativeInitialize", "()V");
    METHOD(load_content, "nativeLoadContent", "()V");
    METHOD(begin_run, "nativeBeginRun", "()V");
    METHOD(update, "nativeUpdate", "(JJZ)V");
    METHOD(begin_draw, "nativeBeginDraw", "()Z");
    METHOD(draw, "nativeDraw", "(JJZ)V");
    METHOD(end_draw, "nativeEndDraw", "()V");
    METHOD(end_run, "nativeEndRun", "()V");
    METHOD(unload_content, "nativeUnloadContent", "()V");
    METHOD(exiting, "nativeExiting", "()V");
#undef METHOD
    (*environment)->DeleteLocalRef(environment, game_class);

    jsize title_size = (*environment)->GetArrayLength(environment, title);
    jbyte* title_bytes = (*environment)->GetByteArrayElements(environment, title, NULL);
    if (title_bytes == NULL) {
        goto create_failed_without_class;
    }

    CNA_GameCallbacks callbacks;
    (void)memset(&callbacks, 0, sizeof(callbacks));
    callbacks.struct_size = (uint32_t)sizeof(callbacks);
    callbacks.struct_version = UINT32_C(1);
    callbacks.load_content = on_load;
    callbacks.update = on_update;
    callbacks.draw = on_draw;
    callbacks.unload_content = on_unload;
    callbacks.exiting = on_exiting;
    callbacks.context = context;

    CNA_GameCreateInfo create_info;
    (void)memset(&create_info, 0, sizeof(create_info));
    create_info.struct_size = (uint32_t)sizeof(create_info);
    create_info.struct_version = UINT32_C(1);
    create_info.is_fixed_time_step = fixed_time_step == JNI_TRUE ? CNA_TRUE : CNA_FALSE;
    create_info.target_elapsed_time_ticks = (int64_t)target_ticks;
    create_info.window_title.data = (const char*)title_bytes;
    create_info.window_title.byte_length = (uint64_t)title_size;
    create_info.callbacks = &callbacks;

    CNA_Result result = cna.game_create(&create_info, &wrapper->cna_handle);
    (*environment)->ReleaseByteArrayElements(environment, title, title_bytes, JNI_ABORT);
    if (result != CNA_RESULT_SUCCESS) {
        goto create_failed_without_class;
    }

    CNA_GameFrameHooks hooks;
    (void)memset(&hooks, 0, sizeof(hooks));
    hooks.struct_size = (uint32_t)sizeof(hooks);
    hooks.struct_version = UINT32_C(1);
    hooks.initialize = on_initialize;
    hooks.begin_run = on_begin_run;
    hooks.end_run = on_end_run;
    hooks.begin_draw = on_begin_draw;
    hooks.end_draw = on_end_draw;
    hooks.context = context;
    result = cna.game_set_hooks(wrapper->cna_handle, &hooks);
    if (result != CNA_RESULT_SUCCESS) {
        (void)cna.game_destroy(wrapper->cna_handle);
        goto create_failed_without_class;
    }
    return (jlong)(uintptr_t)wrapper;

create_failed:
    (*environment)->DeleteLocalRef(environment, game_class);
create_failed_without_class:
    (*environment)->DeleteGlobalRef(environment, context->game);
    free(context);
    free(wrapper);
    return 0;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeRun(
    JNIEnv* environment, jclass type, jlong game)
{
    (void)environment;
    (void)type;
    return (jint)cna.game_run(java_game(game)->cna_handle);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeRunOneFrame(
    JNIEnv* environment, jclass type, jlong game)
{
    (void)environment;
    (void)type;
    return (jint)cna.game_run_one_frame(java_game(game)->cna_handle);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeResetElapsedTime(
    JNIEnv* environment, jclass type, jlong game)
{
    (void)environment;
    (void)type;
    return (jint)cna.game_reset_elapsed_time(java_game(game)->cna_handle);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSuppressDraw(
    JNIEnv* environment, jclass type, jlong game)
{
    (void)environment;
    (void)type;
    return (jint)cna.game_suppress_draw(java_game(game)->cna_handle);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeTick(
    JNIEnv* environment, jclass type, jlong game)
{
    (void)environment;
    (void)type;
    return (jint)cna.game_tick(java_game(game)->cna_handle);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeRequestExit(
    JNIEnv* environment, jclass type, jlong game)
{
    (void)environment;
    (void)type;
    return (jint)cna.game_request_exit(java_game(game)->cna_handle);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeClear(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jint red,
    jint green,
    jint blue,
    jint alpha)
{
    (void)environment;
    (void)type;
    CNA_Color color;
    color.r = (uint8_t)red;
    color.g = (uint8_t)green;
    color.b = (uint8_t)blue;
    color.a = (uint8_t)alpha;
    return (jint)cna.game_clear(java_game(game)->cna_handle, color);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetMouseVisible(
    JNIEnv* environment, jclass type, jlong game, jboolean visible)
{
    (void)environment;
    (void)type;
    return (jint)cna.game_set_mouse_visible(
        java_game(game)->cna_handle, visible == JNI_TRUE ? CNA_TRUE : CNA_FALSE);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetMouseVisible(
    JNIEnv* environment, jclass type, jlong game)
{
    (void)environment;
    (void)type;
    CNA_Bool visible = CNA_FALSE;
    CNA_Result result = cna.game_get_mouse_visible(java_game(game)->cna_handle, &visible);
    if (result != CNA_RESULT_SUCCESS) {
        return -(jint)result;
    }
    return visible == CNA_TRUE ? 1 : 0;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetIsActive(
    JNIEnv* environment, jclass type, jlong game)
{
    (void)environment;
    (void)type;
    CNA_Bool value = CNA_FALSE;
    CNA_Result result = cna.game_get_is_active(java_game(game)->cna_handle, &value);
    return result == CNA_RESULT_SUCCESS ? (value == CNA_TRUE ? 1 : 0) : -(jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetFixedTimeStep(
    JNIEnv* environment, jclass type, jlong game, jboolean value)
{
    (void)environment;
    (void)type;
    return (jint)cna.game_set_fixed_time_step(
        java_game(game)->cna_handle, value == JNI_TRUE ? CNA_TRUE : CNA_FALSE);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetFixedTimeStep(
    JNIEnv* environment, jclass type, jlong game)
{
    (void)environment;
    (void)type;
    CNA_Bool value = CNA_FALSE;
    CNA_Result result = cna.game_get_fixed_time_step(java_game(game)->cna_handle, &value);
    return result == CNA_RESULT_SUCCESS ? (value == CNA_TRUE ? 1 : 0) : -(jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetTargetElapsedTime(
    JNIEnv* environment, jclass type, jlong game, jlong ticks)
{
    (void)environment;
    (void)type;
    return (jint)cna.game_set_target_elapsed_time(java_game(game)->cna_handle, (int64_t)ticks);
}

JNIEXPORT jlong JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetTargetElapsedTime(
    JNIEnv* environment, jclass type, jlong game)
{
    (void)environment;
    (void)type;
    int64_t ticks = 0;
    CNA_Result result = cna.game_get_target_elapsed_time(java_game(game)->cna_handle, &ticks);
    return result == CNA_RESULT_SUCCESS ? (jlong)ticks : -(jlong)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetInactiveSleepTime(
    JNIEnv* environment, jclass type, jlong game, jlong ticks)
{
    (void)environment;
    (void)type;
    return (jint)cna.game_set_inactive_sleep_time(java_game(game)->cna_handle, (int64_t)ticks);
}

JNIEXPORT jlong JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetInactiveSleepTime(
    JNIEnv* environment, jclass type, jlong game)
{
    (void)environment;
    (void)type;
    int64_t ticks = 0;
    CNA_Result result = cna.game_get_inactive_sleep_time(java_game(game)->cna_handle, &ticks);
    return result == CNA_RESULT_SUCCESS ? (jlong)ticks : -(jlong)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetWindowAllowUserResizing(
    JNIEnv* environment, jclass type, jlong game)
{
    (void)environment;
    (void)type;
    CNA_Bool value = CNA_FALSE;
    CNA_Result result = cna.game_window_get_allow_user_resizing(java_game(game)->cna_handle, &value);
    return result == CNA_RESULT_SUCCESS ? (value == CNA_TRUE ? 1 : 0) : -(jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetWindowAllowUserResizing(
    JNIEnv* environment, jclass type, jlong game, jboolean value)
{
    (void)environment;
    (void)type;
    return (jint)cna.game_window_set_allow_user_resizing(
        java_game(game)->cna_handle, value == JNI_TRUE ? CNA_TRUE : CNA_FALSE);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetWindowClientBounds(
    JNIEnv* environment, jclass type, jlong game, jintArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) < 4) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Rectangle value;
    CNA_Result result = cna.game_window_get_client_bounds(java_game(game)->cna_handle, &value);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    const jint fields[4] = {(jint)value.x, (jint)value.y, (jint)value.width, (jint)value.height};
    (*environment)->SetIntArrayRegion(environment, output, 0, 4, fields);
    return (*environment)->ExceptionCheck(environment) ? (jint)CNA_RESULT_INVALID_STATE : 0;
}

JNIEXPORT jlong JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetWindowCurrentOrientation(
    JNIEnv* environment, jclass type, jlong game)
{
    (void)environment;
    (void)type;
    CNA_DisplayOrientation value = CNA_DISPLAY_ORIENTATION_DEFAULT;
    CNA_Result result = cna.game_window_get_current_orientation(java_game(game)->cna_handle, &value);
    return result == CNA_RESULT_SUCCESS ? (jlong)value : -(jlong)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetWindowHandle(
    JNIEnv* environment, jclass type, jlong game, jlongArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) < 1) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    uint64_t value = 0U;
    CNA_Result result = cna.game_window_get_native_handle(java_game(game)->cna_handle, &value);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    const jlong projected = (jlong)value;
    (*environment)->SetLongArrayRegion(environment, output, 0, 1, &projected);
    return (*environment)->ExceptionCheck(environment) ? (jint)CNA_RESULT_INVALID_STATE : 0;
}

JNIEXPORT jlong JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetWindowScreenDeviceNameSize(
    JNIEnv* environment, jclass type, jlong game)
{
    (void)environment;
    (void)type;
    uint64_t size = 0U;
    CNA_Result result = cna.game_window_get_screen_device_name_size(java_game(game)->cna_handle, &size);
    if (result != CNA_RESULT_SUCCESS) {
        return -(jlong)result;
    }
    return size > (uint64_t)INT64_MAX ? -(jlong)CNA_RESULT_INVALID_STATE : (jlong)size;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeCopyWindowScreenDeviceName(
    JNIEnv* environment, jclass type, jlong game, jbyteArray destination)
{
    (void)type;
    if (destination == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    jsize capacity = (*environment)->GetArrayLength(environment, destination);
    jbyte* bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (bytes == NULL) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    uint64_t written = 0U;
    CNA_Result result = cna.game_window_copy_screen_device_name(
        java_game(game)->cna_handle, (char*)bytes, (uint64_t)capacity, &written);
    (*environment)->ReleaseByteArrayElements(environment, destination, bytes, 0);
    if (result == CNA_RESULT_SUCCESS && written != (uint64_t)capacity) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetWindowTitle(
    JNIEnv* environment, jclass type, jlong game, jbyteArray title)
{
    (void)type;
    if (title == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    jsize size = (*environment)->GetArrayLength(environment, title);
    jbyte* bytes = (*environment)->GetByteArrayElements(environment, title, NULL);
    if (bytes == NULL) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    CNA_StringView view = {(const char*)bytes, (uint64_t)size};
    CNA_Result result = cna.game_set_window_title(java_game(game)->cna_handle, view);
    (*environment)->ReleaseByteArrayElements(environment, title, bytes, JNI_ABORT);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeBeginWindowScreenDeviceChange(
    JNIEnv* environment, jclass type, jlong game, jboolean will_be_full_screen)
{
    (void)environment;
    (void)type;
    return (jint)cna.game_window_begin_screen_device_change(
        java_game(game)->cna_handle,
        will_be_full_screen == JNI_TRUE ? CNA_TRUE : CNA_FALSE);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeEndWindowScreenDeviceChange(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jbyteArray screen_device_name,
    jint client_width,
    jint client_height)
{
    (void)type;
    if (screen_device_name == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    jsize size = (*environment)->GetArrayLength(environment, screen_device_name);
    jbyte* bytes = (*environment)->GetByteArrayElements(environment, screen_device_name, NULL);
    if (bytes == NULL) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    CNA_StringView view = {(const char*)bytes, (uint64_t)size};
    CNA_Result result = cna.game_window_end_screen_device_change(
        java_game(game)->cna_handle, view, (int32_t)client_width, (int32_t)client_height);
    (*environment)->ReleaseByteArrayElements(
        environment, screen_device_name, bytes, JNI_ABORT);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetKeyboardState(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jint player_index,
    jlongArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) < 4) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }

    CNA_KeyboardState state;
    (void)memset(&state, 0, sizeof(state));
    state.struct_size = (uint32_t)sizeof(state);
    state.struct_version = UINT32_C(1);
    CNA_Result result = player_index < 0
        ? cna.keyboard_get_state(java_game(game)->cna_handle, &state)
        : cna.keyboard_get_state_for_player(
            java_game(game)->cna_handle, (CNA_PlayerIndex)player_index, &state);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }

    jlong words[4];
    for (size_t index = 0U; index < 4U; ++index) {
        (void)memcpy(&words[index], &state.pressed_key_words[index], sizeof(words[index]));
    }
    (*environment)->SetLongArrayRegion(environment, output, 0, 4, words);
    return (*environment)->ExceptionCheck(environment) ? (jint)CNA_RESULT_INVALID_STATE : 0;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetMouseState(
    JNIEnv* environment, jclass type, jlong game, jintArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) < 4) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_MouseState state;
    (void)memset(&state, 0, sizeof(state));
    state.struct_size = (uint32_t)sizeof(state);
    state.struct_version = UINT32_C(1);
    CNA_Result result = cna.mouse_get_state(java_game(game)->cna_handle, &state);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    const jint values[4] = {
        (jint)state.x,
        (jint)state.y,
        (jint)state.scroll_wheel,
        (jint)state.pressed_buttons
    };
    (*environment)->SetIntArrayRegion(environment, output, 0, 4, values);
    return (*environment)->ExceptionCheck(environment) ? (jint)CNA_RESULT_INVALID_STATE : 0;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetMousePosition(
    JNIEnv* environment, jclass type, jlong game, jint x, jint y)
{
    (void)environment;
    (void)type;
    return (jint)cna.mouse_set_position(
        java_game(game)->cna_handle, (int32_t)x, (int32_t)y);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetMouseWindowHandle(
    JNIEnv* environment, jclass type, jlong game, jlongArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) < 1) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    uint64_t value = 0U;
    CNA_Result result = cna.mouse_get_window_handle(java_game(game)->cna_handle, &value);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    jlong projected;
    (void)memcpy(&projected, &value, sizeof(projected));
    (*environment)->SetLongArrayRegion(environment, output, 0, 1, &projected);
    return (*environment)->ExceptionCheck(environment) ? (jint)CNA_RESULT_INVALID_STATE : 0;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetMouseWindowHandle(
    JNIEnv* environment, jclass type, jlong game, jlong window)
{
    (void)environment;
    (void)type;
    uint64_t value;
    (void)memcpy(&value, &window, sizeof(value));
    return (jint)cna.mouse_set_window_handle(java_game(game)->cna_handle, value);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeDestroyGame(
    JNIEnv* environment, jclass type, jlong game)
{
    (void)type;
    JavaGame* wrapper = java_game(game);
    CNA_Result result = cna.game_destroy(wrapper->cna_handle);
    if (result == CNA_RESULT_SUCCESS || result == CNA_RESULT_CALLBACK) {
        (*environment)->DeleteGlobalRef(environment, wrapper->context->game);
        free(wrapper->context);
        free(wrapper);
    }
    return (jint)result;
}

JNIEXPORT jstring JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeLastErrorMessage(
    JNIEnv* environment, jclass type)
{
    (void)type;
    uint64_t size = 0U;
    if (cna.error_message_size(&size) != CNA_RESULT_SUCCESS || size == 0U || size > (uint64_t)INT32_MAX) {
        return (*environment)->NewStringUTF(environment, "");
    }
    char* message = (char*)malloc((size_t)size + 1U);
    if (message == NULL) {
        return (*environment)->NewStringUTF(environment, "native diagnostic allocation failed");
    }
    uint64_t copied = 0U;
    CNA_Result result = cna.error_message_copy(message, size, &copied);
    if (result != CNA_RESULT_SUCCESS || copied != size) {
        free(message);
        return (*environment)->NewStringUTF(environment, "native diagnostic copy failed");
    }
    message[size] = '\0';
    jstring text = (*environment)->NewStringUTF(environment, message);
    free(message);
    return text;
}
