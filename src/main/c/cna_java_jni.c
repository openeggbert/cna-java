// SPDX-License-Identifier: MS-PL

#include <jni.h>

#include <CNA/C/cna.h>

#include <stdint.h>
#include <stdatomic.h>
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
typedef CNA_Result (*GameWindowSubscribeFunction)(
    CNA_Handle, CNA_GameWindowEvent, CNA_GameEventCallback, void*,
    CNA_GameEventRegistrationHandle*);
typedef CNA_Result (*KeyboardGetStateFunction)(CNA_Handle, CNA_KeyboardState*);
typedef CNA_Result (*KeyboardGetStateForPlayerFunction)(
    CNA_Handle, CNA_PlayerIndex, CNA_KeyboardState*);
typedef CNA_Result (*GamePadGetStateFunction)(CNA_Handle, CNA_PlayerIndex, CNA_GamePadState*);
typedef CNA_Result (*GamePadGetStateWithDeadZoneFunction)(
    CNA_Handle, CNA_PlayerIndex, CNA_GamePadDeadZone, CNA_GamePadState*);
typedef CNA_Result (*GamePadGetCapabilitiesFunction)(
    CNA_Handle, CNA_PlayerIndex, CNA_GamePadCapabilities*);
typedef CNA_Result (*GamePadSetVibrationFunction)(
    CNA_Handle, CNA_PlayerIndex, float, float, CNA_Bool*);
typedef CNA_Result (*TouchGetCapabilitiesFunction)(
    CNA_Handle, CNA_TouchCapabilities*);
typedef CNA_Result (*TouchGetStateFunction)(CNA_Handle, CNA_TouchState*);
typedef CNA_Result (*TouchReadGestureFunction)(CNA_Handle, CNA_GestureSample*);
typedef CNA_Result (*TouchEnqueueGestureFunction)(
    CNA_Handle, const CNA_GestureSample*);
typedef CNA_Result (*TouchSetFingerFunction)(
    CNA_Handle, int32_t, int32_t, CNA_Vector2);
typedef CNA_Result (*TouchRaiseEventFunction)(
    CNA_Handle, int32_t, CNA_TouchLocationState,
    float, float, float, float);
typedef CNA_Result (*MouseGetStateFunction)(CNA_Handle, CNA_MouseState*);
typedef CNA_Result (*MouseSetPositionFunction)(CNA_Handle, int32_t, int32_t);
typedef CNA_Result (*GameSetUint64Function)(CNA_Handle, uint64_t);
typedef CNA_Result (*GameGetHandleFunction)(CNA_Handle, CNA_Handle*);
typedef CNA_Result (*GameSetUint32Function)(CNA_Handle, uint32_t);
typedef CNA_Result (*GameGetInt32Function)(CNA_Handle, int32_t*);
typedef CNA_Result (*GameSetInt32Function)(CNA_Handle, int32_t);
typedef CNA_Result (*GraphicsDeviceManagerSubscribeFunction)(
    CNA_GraphicsDeviceManagerHandle, CNA_GraphicsDeviceManagerEvent,
    CNA_GameEventCallback, void*, CNA_GameEventRegistrationHandle*);
typedef CNA_Result (*GraphicsDeviceManagerPreparingSubscribeFunction)(
    CNA_GraphicsDeviceManagerHandle, CNA_PreparingDeviceSettingsMutatorEXT,
    void*, CNA_GameEventRegistrationHandle*);
typedef CNA_Result (*GraphicsAdapterGetInfoFunction)(
    CNA_Handle, uint32_t, CNA_GraphicsAdapterInfo*);
typedef CNA_Result (*GraphicsAdapterCopyStringFunction)(
    CNA_Handle, uint32_t, char*, uint64_t, uint64_t*);
typedef CNA_Result (*GraphicsAdapterGetDisplayModeFunction)(
    CNA_Handle, uint32_t, CNA_DisplayMode*);
typedef CNA_Result (*GraphicsAdapterGetDisplayModeCountFunction)(
    CNA_Handle, uint32_t, CNA_Bool, CNA_SurfaceFormat, uint64_t*);
typedef CNA_Result (*GraphicsAdapterCopyDisplayModesFunction)(
    CNA_Handle, uint32_t, CNA_Bool, CNA_SurfaceFormat,
    CNA_DisplayMode*, uint64_t, uint64_t*);
typedef CNA_Result (*GraphicsAdapterSetPreferencesFunction)(
    CNA_Handle, uint32_t, CNA_Bool, CNA_Bool);
typedef CNA_Result (*GraphicsAdapterIsProfileSupportedFunction)(
    CNA_Handle, uint32_t, CNA_GraphicsProfile, CNA_Bool*);
typedef CNA_Result (*GraphicsAdapterQueryFormatFunction)(
    CNA_Handle, uint32_t, CNA_GraphicsProfile, CNA_SurfaceFormat,
    CNA_DepthFormat, int32_t, CNA_GraphicsFormatSelection*);
typedef CNA_Result (*GraphicsAdapterGetMonitorHandleFunction)(
    CNA_Handle, uint32_t, CNA_NativeHandleValue*);
typedef CNA_Result (*GraphicsDeviceSetRectangleFunction)(CNA_Handle, CNA_Rectangle);
typedef CNA_Result (*GraphicsDeviceGetViewportFunction)(CNA_Handle, CNA_Viewport*);
typedef CNA_Result (*GraphicsDeviceSetViewportFunction)(CNA_Handle, CNA_Viewport);
typedef CNA_Result (*GraphicsDeviceGetColorFunction)(CNA_Handle, CNA_Color*);
typedef CNA_Result (*GraphicsDeviceSetColorFunction)(CNA_Handle, CNA_Color);
typedef CNA_Result (*GraphicsDeviceGetBlendStateFunction)(CNA_Handle, CNA_BlendState*);
typedef CNA_Result (*GraphicsDeviceSetBlendStateFunction)(CNA_Handle, const CNA_BlendState*);
typedef CNA_Result (*GraphicsDeviceGetDepthStencilStateFunction)(
    CNA_Handle, CNA_DepthStencilState*);
typedef CNA_Result (*GraphicsDeviceSetDepthStencilStateFunction)(
    CNA_Handle, const CNA_DepthStencilState*);
typedef CNA_Result (*GraphicsDeviceGetRasterizerStateFunction)(
    CNA_Handle, CNA_RasterizerState*);
typedef CNA_Result (*GraphicsDeviceSetRasterizerStateFunction)(
    CNA_Handle, const CNA_RasterizerState*);
typedef CNA_Result (*GraphicsDeviceGetSamplerStateFunction)(
    CNA_Handle, CNA_ShaderStage, uint32_t, CNA_SamplerState*);
typedef CNA_Result (*GraphicsDeviceSetSamplerStateFunction)(
    CNA_Handle, CNA_ShaderStage, uint32_t, const CNA_SamplerState*);
typedef CNA_Result (*GraphicsDeviceGetTextureFunction)(
    CNA_Handle, CNA_ShaderStage, uint32_t, CNA_TextureSlotInfo*);
typedef CNA_Result (*GraphicsDeviceSetTextureFunction)(
    CNA_Handle, CNA_ShaderStage, uint32_t, CNA_Handle);
typedef CNA_Result (*GraphicsDeviceGetPresentationParametersFunction)(
    CNA_Handle, CNA_PresentationParameters*);
typedef CNA_Result (*GraphicsDeviceResetWithParametersFunction)(
    CNA_Handle, const CNA_PresentationParameters*, const uint32_t*);
typedef CNA_Result (*GraphicsDeviceGetDisplayModeFunction)(CNA_Handle, CNA_DisplayMode*);
typedef CNA_Result (*GraphicsDeviceGetBackBufferInfoFunction)(CNA_Handle, CNA_BackBufferInfo*);
typedef CNA_Result (*GraphicsDeviceGetBackBufferDataFunction)(
    CNA_Handle, const CNA_BackBufferReadback*, CNA_Color*, uint64_t);
typedef CNA_Result (*GraphicsDeviceClearFunction)(
    CNA_Handle, CNA_ClearOptions, CNA_Color, float, int32_t);
typedef CNA_Result (*GraphicsDeviceSubscribeEventFunction)(
    CNA_Handle, CNA_GraphicsDeviceEvent, CNA_GraphicsDeviceEventCallback,
    void*, CNA_GraphicsDeviceEventRegistrationHandle*);
typedef CNA_Result (*GraphicsDeviceSubscribeResourceCreatedFunction)(
    CNA_Handle, CNA_GraphicsDeviceResourceCreatedCallback,
    void*, CNA_GraphicsDeviceEventRegistrationHandle*);
typedef CNA_Result (*GraphicsDeviceSubscribeResourceDestroyedFunction)(
    CNA_Handle, CNA_GraphicsDeviceResourceDestroyedCallback,
    void*, CNA_GraphicsDeviceEventRegistrationHandle*);
typedef CNA_Result (*TextureCreateFunction)(
    CNA_Handle, const CNA_Texture2DCreateInfo*, CNA_Handle*);
typedef CNA_Result (*TextureCreateEncodedFunction)(
    CNA_Handle, const uint8_t*, uint64_t, const CNA_Texture2DDecodeInfo*, CNA_Handle*);
typedef CNA_Result (*TextureGetInfoFunction)(CNA_Handle, CNA_Texture2DInfo*);
typedef CNA_Result (*TextureSetColorsFunction)(CNA_Handle, const CNA_Color*, uint64_t);
typedef CNA_Result (*TextureGetColorsFunction)(CNA_Handle, CNA_Color*, uint64_t, uint64_t*);
typedef CNA_Result (*TextureSetDataFunction)(
    CNA_Handle, CNA_TextureDataType, const CNA_Texture2DTransfer*, const void*, uint64_t);
typedef CNA_Result (*TextureGetDataFunction)(
    CNA_Handle, CNA_TextureDataType, const CNA_Texture2DTransfer*, void*, uint64_t, uint64_t*);
typedef CNA_Result (*TextureEncodedSizeFunction)(
    CNA_Handle, CNA_TextureImageFormat, uint32_t, uint32_t, uint64_t*);
typedef CNA_Result (*TextureCopyEncodedFunction)(
    CNA_Handle, CNA_TextureImageFormat, uint32_t, uint32_t, uint8_t*, uint64_t, uint64_t*);
typedef CNA_Result (*TextureCubeCreateFunction)(
    CNA_Handle, const CNA_TextureCubeCreateInfo*, CNA_Handle*);
typedef CNA_Result (*TextureCubeGetInfoFunction)(CNA_Handle, CNA_TextureCubeInfo*);
typedef CNA_Result (*TextureCubeSetDataFunction)(
    CNA_Handle, const CNA_TextureCubeTransfer*, const CNA_Color*, uint64_t);
typedef CNA_Result (*TextureCubeGetDataFunction)(
    CNA_Handle, const CNA_TextureCubeTransfer*, CNA_Color*, uint64_t, uint64_t*);
typedef CNA_Result (*Texture3DCreateFunction)(
    CNA_Handle, const CNA_Texture3DCreateInfo*, CNA_Handle*);
typedef CNA_Result (*Texture3DGetInfoFunction)(CNA_Handle, CNA_Texture3DInfo*);
typedef CNA_Result (*Texture3DSetDataFunction)(
    CNA_Handle, const CNA_Texture3DTransfer*, const CNA_Color*, uint64_t);
typedef CNA_Result (*Texture3DGetDataFunction)(
    CNA_Handle, const CNA_Texture3DTransfer*, CNA_Color*, uint64_t, uint64_t*);
typedef CNA_Result (*EffectCreateCompiledFunction)(
    CNA_Handle, const uint8_t*, uint64_t, CNA_EffectHandle*);
typedef CNA_Result (*HandleGetHandleFunction)(CNA_Handle, CNA_Handle*);
typedef CNA_Result (*HandleSetHandleFunction)(CNA_Handle, CNA_Handle);
typedef CNA_Result (*HandleIndexGetHandleFunction)(CNA_Handle, uint64_t, CNA_Handle*);
typedef CNA_Result (*HandleGetInfoFunction)(CNA_Handle, void*);
typedef CNA_Result (*HandleGetValueFunction)(CNA_Handle, void*);
typedef CNA_Result (*EffectParameterGetValuesFunction)(
    CNA_Handle, CNA_EffectValueType, uint64_t, void*, uint64_t, uint64_t*);
typedef CNA_Result (*EffectParameterGetValueFunction)(
    CNA_Handle, CNA_EffectValueType, void*);
typedef CNA_Result (*EffectParameterSetValuesFunction)(
    CNA_Handle, CNA_EffectValueType, const void*, uint64_t);
typedef CNA_Result (*EffectParameterSetValueFunction)(
    CNA_Handle, CNA_EffectValueType, const void*);
typedef CNA_Result (*EffectParameterGetTextureFunction)(
    CNA_Handle, CNA_EffectTextureType, CNA_Handle*);
typedef CNA_Result (*EffectParameterSetTextureFunction)(
    CNA_Handle, CNA_EffectTextureType, CNA_Handle);
typedef CNA_Result (*HandleGetFloatFunction)(CNA_Handle, float*);
typedef CNA_Result (*HandleSetFloatFunction)(CNA_Handle, float);
typedef CNA_Result (*HandleGetVector3Function)(CNA_Handle, CNA_Vector3*);
typedef CNA_Result (*HandleSetVector3Function)(CNA_Handle, CNA_Vector3);
typedef CNA_Result (*HandleGetMatrixFunction)(CNA_Handle, CNA_Matrix*);
typedef CNA_Result (*HandleSetMatrixFunction)(CNA_Handle, CNA_Matrix);
typedef CNA_Result (*HandleUint32GetHandleFunction)(CNA_Handle, uint32_t, CNA_Handle*);
typedef CNA_Result (*HandleGetCompareFunction)(CNA_Handle, CNA_CompareFunction*);
typedef CNA_Result (*HandleSetCompareFunction)(CNA_Handle, CNA_CompareFunction);
typedef CNA_Result (*HandleUint32SetHandleFunction)(CNA_Handle, uint32_t, CNA_Handle);
typedef CNA_Result (*SkinnedEffectSetBonesFunction)(
    CNA_Handle, const CNA_Matrix*, uint64_t);
typedef CNA_Result (*SkinnedEffectCopyBonesFunction)(
    CNA_Handle, uint64_t, CNA_Matrix*, uint64_t, uint64_t*);
typedef CNA_Result (*RenderTarget2DCreateFunction)(
    CNA_Handle, const CNA_RenderTarget2DCreateInfo*, CNA_Handle*);
typedef CNA_Result (*RenderTargetCubeCreateFunction)(
    CNA_Handle, const CNA_RenderTargetCubeCreateInfo*, CNA_Handle*);
typedef CNA_Result (*RenderTargetGetInfoFunction)(CNA_Handle, CNA_RenderTargetInfo*);
typedef CNA_Result (*GraphicsDeviceSetRenderTargetCubeFunction)(
    CNA_Handle, CNA_Handle, CNA_CubeMapFace);
typedef CNA_Result (*GraphicsDeviceSetRenderTargetsFunction)(
    CNA_Handle, const CNA_RenderTargetBinding*, uint64_t);
typedef CNA_Result (*GraphicsDeviceCopyRenderTargetsFunction)(
    CNA_Handle, CNA_RenderTargetBinding*, uint64_t, uint64_t*);
typedef CNA_Result (*VertexDeclarationCreateWithStrideFunction)(
    int32_t, const CNA_VertexElement*, uint64_t, CNA_VertexDeclarationHandle*);
typedef CNA_Result (*VertexBufferCreateFunction)(
    CNA_Handle, const CNA_VertexBufferCreateInfo*, CNA_VertexBufferHandle*);
typedef CNA_Result (*VertexBufferGetInfoFunction)(
    CNA_VertexBufferHandle, CNA_VertexBufferInfo*);
typedef CNA_Result (*VertexBufferSetFunction)(
    CNA_VertexBufferHandle, const CNA_VertexBufferTransfer*, const void*, uint64_t);
typedef CNA_Result (*VertexBufferSetRawFunction)(
    CNA_VertexBufferHandle, const void*, uint64_t, uint64_t, uint32_t);
typedef CNA_Result (*VertexBufferSetRawAtFunction)(
    CNA_VertexBufferHandle, uint64_t, const void*, uint64_t, uint64_t, uint32_t);
typedef CNA_Result (*VertexBufferGetRawFunction)(
    CNA_VertexBufferHandle, uint64_t, void*, uint64_t, uint64_t, uint32_t);
typedef CNA_Result (*VertexBufferSubscribeFunction)(
    CNA_VertexBufferHandle, CNA_VertexBufferContentLostCallback, void*,
    CNA_VertexBufferEventRegistrationHandle*);
typedef CNA_Result (*VertexBufferUnsubscribeFunction)(
    CNA_VertexBufferEventRegistrationHandle);
typedef CNA_Result (*IndexBufferCreateFunction)(
    CNA_Handle, const CNA_IndexBufferCreateInfo*, CNA_IndexBufferHandle*);
typedef CNA_Result (*IndexBufferGetInfoFunction)(
    CNA_IndexBufferHandle, CNA_IndexBufferInfo*);
typedef CNA_Result (*IndexBufferSetFunction)(
    CNA_IndexBufferHandle, const CNA_IndexBufferTransfer*, const void*, uint64_t);
typedef CNA_Result (*IndexBufferSetAtFunction)(
    CNA_IndexBufferHandle, uint64_t, const CNA_IndexBufferTransfer*, const void*, uint64_t);
typedef CNA_Result (*IndexBufferGetFunction)(
    CNA_IndexBufferHandle, const CNA_IndexBufferTransfer*, void*, uint64_t, uint64_t*);
typedef CNA_Result (*IndexBufferSubscribeFunction)(
    CNA_IndexBufferHandle, CNA_IndexBufferContentLostCallback, void*,
    CNA_IndexBufferEventRegistrationHandle*);
typedef CNA_Result (*IndexBufferUnsubscribeFunction)(
    CNA_IndexBufferEventRegistrationHandle);
typedef CNA_Result (*GraphicsDeviceSetVertexBufferFunction)(
    CNA_Handle, CNA_VertexBufferHandle);
typedef CNA_Result (*GraphicsDeviceSetVertexBufferOffsetFunction)(
    CNA_Handle, CNA_VertexBufferHandle, int32_t);
typedef CNA_Result (*GraphicsDeviceSetVertexBuffersFunction)(
    CNA_Handle, const CNA_VertexBufferBinding*, uint64_t);
typedef CNA_Result (*GraphicsDeviceCopyVertexBuffersFunction)(
    CNA_Handle, CNA_VertexBufferBinding*, uint64_t, uint64_t*);
typedef CNA_Result (*GraphicsDeviceSetIndexBufferFunction)(
    CNA_Handle, CNA_IndexBufferHandle);
typedef CNA_Result (*GraphicsDeviceGetIndexBufferFunction)(
    CNA_Handle, CNA_IndexBufferHandle*);
typedef CNA_Result (*GraphicsDeviceDrawPrimitivesFunction)(
    CNA_Handle, CNA_PrimitiveType, int32_t, int32_t);
typedef CNA_Result (*GraphicsDeviceDrawIndexedPrimitivesFunction)(
    CNA_Handle, CNA_PrimitiveType, int32_t, int32_t, int32_t, int32_t, int32_t);
typedef CNA_Result (*GraphicsDeviceDrawInstancedPrimitivesFunction)(
    CNA_Handle, CNA_PrimitiveType, int32_t, int32_t, int32_t, int32_t, int32_t, int32_t);
typedef CNA_Result (*GraphicsDeviceDrawUserPrimitivesFunction)(
    CNA_Handle, const CNA_UserPrimitives*);
typedef CNA_Result (*GraphicsDeviceDrawUserIndexedPrimitivesFunction)(
    CNA_Handle, const CNA_UserPrimitives*, const CNA_UserIndices*);
typedef CNA_Result (*SpriteBatchCreateFunction)(CNA_Handle, CNA_Handle*);
typedef CNA_Result (*SpriteBatchBeginFunction)(CNA_Handle, const CNA_SpriteBatchBeginInfo*);
typedef CNA_Result (*SpriteBatchBeginStatesFunction)(
    CNA_Handle, CNA_SpriteSortMode, const CNA_BlendState*, const CNA_SamplerState*,
    const CNA_DepthStencilState*, const CNA_RasterizerState*);
typedef CNA_Result (*SpriteBatchBeginEffectFunction)(
    CNA_Handle, CNA_SpriteSortMode, const CNA_BlendState*, const CNA_SamplerState*,
    const CNA_DepthStencilState*, const CNA_RasterizerState*, CNA_Handle,
    const CNA_Matrix*);
typedef CNA_Result (*SpriteBatchSubmitFunction)(CNA_Handle, const CNA_SpriteCommand*, uint64_t);
typedef CNA_Result (*SpriteBatchSubmitScaledFunction)(
    CNA_Handle, const CNA_SpriteScaledCommand*, uint64_t);
typedef CNA_Result (*ContentManagerCreateFunction)(
    CNA_Handle, const CNA_ContentManagerCreateInfo*, CNA_Handle*);
typedef CNA_Result (*ContentManagerLoadTextureFunction)(
    CNA_Handle, CNA_StringView, CNA_Handle*);
typedef CNA_Result (*ContentManagerLoadSpriteFontFunction)(
    CNA_Handle, CNA_StringView, CNA_Handle*, CNA_Handle*);
typedef CNA_Result (*SpriteFontCreateFunction)(
    const CNA_SpriteFontCreateInfo*, CNA_Handle*);
typedef CNA_Result (*SpriteFontGetInfoFunction)(CNA_Handle, CNA_SpriteFontInfo*);
typedef CNA_Result (*SpriteFontCopyCharactersFunction)(
    CNA_Handle, CNA_Char16*, uint64_t, uint64_t*);
typedef CNA_Result (*SpriteFontSetDefaultCharacterFunction)(
    CNA_Handle, CNA_Bool, CNA_Char16);
typedef CNA_Result (*SpriteFontSetLineSpacingFunction)(CNA_Handle, int32_t);
typedef CNA_Result (*SpriteFontSetSpacingFunction)(CNA_Handle, float);
typedef CNA_Result (*SpriteFontMeasureFunction)(CNA_Handle, CNA_StringView, CNA_Vector2*);
typedef CNA_Result (*SpriteBatchDrawStringFunction)(
    CNA_Handle, const CNA_SpriteTextCommand*);

typedef CNA_Result (*AudioSoundCreateRangeFunction)(
    CNA_Handle, const CNA_SoundEffectCreateInfo*, const uint8_t*, uint64_t,
    int32_t, int32_t, int32_t, int32_t, CNA_Handle*);
typedef CNA_Result (*AudioBytesCreateFunction)(
    CNA_Handle, const uint8_t*, uint64_t, CNA_Handle*);
typedef CNA_Result (*AudioPlaySettingsFunction)(
    CNA_Handle, float, float, float, CNA_Bool*);
typedef CNA_Result (*AudioInstanceStopFunction)(CNA_Handle, CNA_Bool);
typedef CNA_Result (*AudioInstanceInfoFunction)(
    CNA_Handle, CNA_SoundEffectInstanceInfo*);
typedef CNA_Result (*AudioInstanceSetBoolFunction)(CNA_Handle, CNA_Bool);
typedef CNA_Result (*AudioDynamicCreateFunction)(
    CNA_Handle, int32_t, CNA_AudioChannels, CNA_Handle*);
typedef CNA_Result (*AudioSubmitBufferFunction)(
    CNA_Handle, const uint8_t*, uint64_t, int32_t, int32_t);
typedef CNA_Result (*AudioSubscribeFunction)(
    CNA_Handle, CNA_AudioEventCallback, void*, CNA_AudioEventRegistrationHandle*);
typedef CNA_Result (*AudioApply3DFunction)(
    CNA_Handle, const CNA_AudioListener*, const CNA_AudioEmitter*);
typedef CNA_Result (*AudioApply3DMultiFunction)(
    CNA_Handle, const CNA_AudioListener*, uint64_t, const CNA_AudioEmitter*);
typedef CNA_Result (*MicrophoneIndexGetSizeFunction)(
    CNA_Handle, uint64_t, uint64_t*);
typedef CNA_Result (*MicrophoneIndexCopyStringFunction)(
    CNA_Handle, uint64_t, char*, uint64_t, uint64_t*);
typedef CNA_Result (*MicrophoneDefaultFunction)(
    CNA_Handle, uint64_t*, CNA_Bool*);
typedef CNA_Result (*MicrophoneIndexGetInt64Function)(
    CNA_Handle, uint64_t, int64_t*);
typedef CNA_Result (*MicrophoneIndexSetInt64Function)(
    CNA_Handle, uint64_t, int64_t);
typedef CNA_Result (*MicrophoneIndexGetBoolFunction)(
    CNA_Handle, uint64_t, CNA_Bool*);
typedef CNA_Result (*MicrophoneIndexGetInt32Function)(
    CNA_Handle, uint64_t, int32_t*);
typedef CNA_Result (*MicrophoneIndexGetStateFunction)(
    CNA_Handle, uint64_t, CNA_MicrophoneState*);
typedef CNA_Result (*MicrophoneIndexUnaryFunction)(CNA_Handle, uint64_t);
typedef CNA_Result (*MicrophoneGetDataFunction)(
    CNA_Handle, uint64_t, uint8_t*, uint64_t, uint64_t*);
typedef CNA_Result (*MicrophoneSubscribeFunction)(
    CNA_Handle, uint64_t, CNA_AudioEventCallback, void*,
    CNA_AudioEventRegistrationHandle*);
typedef CNA_Result (*AudioEngineCreateFunction)(
    CNA_Handle, CNA_StringView, int64_t, CNA_StringView, CNA_Handle*);
typedef CNA_Result (*HandleIndexGetSizeFunction)(CNA_Handle, uint64_t, uint64_t*);
typedef CNA_Result (*HandleIndexCopyStringFunction)(
    CNA_Handle, uint64_t, char*, uint64_t, uint64_t*);
typedef CNA_Result (*HandleStringGetFloatFunction)(CNA_Handle, CNA_StringView, float*);
typedef CNA_Result (*HandleStringSetFloatFunction)(CNA_Handle, CNA_StringView, float);
typedef CNA_Result (*HandleStringGetHandleFunction)(CNA_Handle, CNA_StringView, CNA_Handle*);
typedef CNA_Result (*CategoryStopFunction)(CNA_Handle, CNA_AudioStopOptions);
typedef CNA_Result (*CategoryEqualsFunction)(CNA_Handle, CNA_Handle, CNA_Bool*);
typedef CNA_Result (*WaveBankCreateFunction)(CNA_Handle, CNA_StringView, CNA_Handle*);
typedef CNA_Result (*WaveBankStreamingCreateFunction)(
    CNA_Handle, CNA_StringView, int32_t, int16_t, CNA_Handle*);
typedef CNA_Result (*SoundBankPlay3DFunction)(
    CNA_Handle, CNA_StringView, const CNA_AudioListener*, const CNA_AudioEmitter*);
typedef CNA_Result (*CueInfoFunction)(CNA_Handle, CNA_CueInfo*);
typedef CNA_Result (*CueStopFunction)(CNA_Handle, CNA_AudioStopOptions);

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
    GameUnaryFunction framework_dispatcher_update;
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
    GameWindowSubscribeFunction game_window_subscribe;
    GameUnaryFunction game_unsubscribe;
    KeyboardGetStateFunction keyboard_get_state;
    KeyboardGetStateForPlayerFunction keyboard_get_state_for_player;
    GamePadGetStateFunction gamepad_get_state;
    GamePadGetStateWithDeadZoneFunction gamepad_get_state_with_dead_zone;
    GamePadGetCapabilitiesFunction gamepad_get_capabilities;
    GamePadSetVibrationFunction gamepad_set_vibration;
    TouchGetCapabilitiesFunction touch_get_capabilities;
    TouchGetStateFunction touch_get_state;
    GameGetInt32Function touch_panel_get_display_width;
    GameSetInt32Function touch_panel_set_display_width;
    GameGetInt32Function touch_panel_get_display_height;
    GameSetInt32Function touch_panel_set_display_height;
    GameGetUint32Function touch_panel_get_display_orientation;
    GameSetUint32Function touch_panel_set_display_orientation;
    GameGetUint32Function touch_panel_get_enabled_gestures;
    GameSetUint32Function touch_panel_set_enabled_gestures;
    GameGetBoolFunction touch_panel_get_is_gesture_available;
    GameGetUint64Function touch_panel_get_window_handle;
    GameSetUint64Function touch_panel_set_window_handle;
    TouchReadGestureFunction touch_panel_read_gesture;
    TouchEnqueueGestureFunction touch_panel_enqueue_gesture_ext;
    GameSetBoolFunction touch_panel_set_touch_device_exists_ext;
    TouchSetFingerFunction touch_panel_set_finger_ext;
    TouchRaiseEventFunction touch_panel_raise_touch_event_ext;
    GameUnaryFunction touch_panel_update_ext;
    GameUnaryFunction touch_panel_reset_for_tests_ext;
    MouseGetStateFunction mouse_get_state;
    MouseSetPositionFunction mouse_set_position;
    GameGetUint64Function mouse_get_window_handle;
    GameSetUint64Function mouse_set_window_handle;
    GameGetHandleFunction game_get_graphics_device;
    SpriteBatchCreateFunction graphics_device_manager_create;
    GameGetUint32Function graphics_device_manager_get_graphics_profile;
    GameSetUint32Function graphics_device_manager_set_graphics_profile;
    GameGetBoolFunction graphics_device_manager_get_is_full_screen;
    GameSetBoolFunction graphics_device_manager_set_is_full_screen;
    GameGetBoolFunction graphics_device_manager_get_prefer_multi_sampling;
    GameSetBoolFunction graphics_device_manager_set_prefer_multi_sampling;
    GameGetUint32Function graphics_device_manager_get_preferred_back_buffer_format;
    GameSetUint32Function graphics_device_manager_set_preferred_back_buffer_format;
    GameGetInt32Function graphics_device_manager_get_preferred_back_buffer_width;
    GameSetInt32Function graphics_device_manager_set_preferred_back_buffer_width;
    GameGetInt32Function graphics_device_manager_get_preferred_back_buffer_height;
    GameSetInt32Function graphics_device_manager_set_preferred_back_buffer_height;
    GameGetUint32Function graphics_device_manager_get_preferred_depth_stencil_format;
    GameSetUint32Function graphics_device_manager_set_preferred_depth_stencil_format;
    GameGetBoolFunction graphics_device_manager_get_synchronize_with_vertical_retrace;
    GameSetBoolFunction graphics_device_manager_set_synchronize_with_vertical_retrace;
    GameGetUint32Function graphics_device_manager_get_supported_orientations;
    GameSetUint32Function graphics_device_manager_set_supported_orientations;
    GameUnaryFunction graphics_device_manager_apply_changes;
    GameUnaryFunction graphics_device_manager_toggle_full_screen;
    GameUnaryFunction graphics_device_manager_create_device;
    GameGetBoolFunction graphics_device_manager_begin_draw;
    GameUnaryFunction graphics_device_manager_end_draw;
    GameUnaryFunction graphics_device_manager_dispose;
    GraphicsDeviceManagerSubscribeFunction graphics_device_manager_subscribe;
    GraphicsDeviceManagerPreparingSubscribeFunction
        graphics_device_manager_subscribe_preparing_device_settings_ext;
    GameUnaryFunction graphics_device_manager_destroy;
    GameGetSizeFunction graphics_adapter_get_count;
    GraphicsAdapterGetInfoFunction graphics_adapter_get_info;
    GraphicsAdapterCopyStringFunction graphics_adapter_copy_description;
    GraphicsAdapterCopyStringFunction graphics_adapter_copy_device_name;
    GraphicsAdapterGetDisplayModeFunction graphics_adapter_get_current_display_mode;
    GraphicsAdapterGetDisplayModeCountFunction graphics_adapter_get_display_mode_count;
    GraphicsAdapterCopyDisplayModesFunction graphics_adapter_copy_display_modes;
    GraphicsAdapterSetPreferencesFunction graphics_adapter_set_device_preferences;
    GraphicsAdapterIsProfileSupportedFunction graphics_adapter_is_profile_supported;
    GraphicsAdapterQueryFormatFunction graphics_adapter_query_render_target_format;
    GraphicsAdapterQueryFormatFunction graphics_adapter_query_backbuffer_format;
    GraphicsAdapterGetMonitorHandleFunction graphics_adapter_get_native_monitor_handle;
    GameGetBoolFunction graphics_device_get_is_disposed;
    GameGetUint32Function graphics_device_get_status;
    GameGetUint32Function graphics_device_get_adapter_index;
    GameGetUint32Function graphics_device_get_graphics_profile;
    GameSetUint32Function graphics_device_set_graphics_profile_ext;
    GameGetRectangleFunction graphics_device_get_scissor_rectangle;
    GraphicsDeviceSetRectangleFunction graphics_device_set_scissor_rectangle;
    GraphicsDeviceGetViewportFunction graphics_device_get_viewport;
    GraphicsDeviceSetViewportFunction graphics_device_set_viewport;
    GraphicsDeviceGetColorFunction graphics_device_get_blend_factor;
    GraphicsDeviceSetColorFunction graphics_device_set_blend_factor;
    GraphicsDeviceGetBlendStateFunction graphics_device_get_blend_state;
    GraphicsDeviceSetBlendStateFunction graphics_device_set_blend_state;
    GraphicsDeviceGetDepthStencilStateFunction graphics_device_get_depth_stencil_state;
    GraphicsDeviceSetDepthStencilStateFunction graphics_device_set_depth_stencil_state;
    GraphicsDeviceGetRasterizerStateFunction graphics_device_get_rasterizer_state;
    GraphicsDeviceSetRasterizerStateFunction graphics_device_set_rasterizer_state;
    GraphicsDeviceGetSamplerStateFunction graphics_device_get_sampler_state;
    GraphicsDeviceSetSamplerStateFunction graphics_device_set_sampler_state;
    GraphicsDeviceGetTextureFunction graphics_device_get_texture;
    GraphicsDeviceSetTextureFunction graphics_device_set_texture;
    GameGetInt32Function graphics_device_get_multi_sample_mask;
    GameSetInt32Function graphics_device_set_multi_sample_mask;
    GameGetInt32Function graphics_device_get_reference_stencil;
    GameSetInt32Function graphics_device_set_reference_stencil;
    GraphicsDeviceGetPresentationParametersFunction
        graphics_device_get_presentation_parameters;
    GraphicsDeviceGetDisplayModeFunction graphics_device_get_display_mode;
    GraphicsDeviceGetBackBufferInfoFunction graphics_device_get_backbuffer_info;
    GraphicsDeviceGetBackBufferDataFunction graphics_device_get_backbuffer_data_window;
    GraphicsDeviceClearFunction graphics_device_clear_options;
    GameUnaryFunction graphics_device_present;
    GameUnaryFunction graphics_device_reset;
    GraphicsDeviceResetWithParametersFunction graphics_device_reset_with_parameters;
    GraphicsDeviceSubscribeEventFunction graphics_device_subscribe_event;
    GraphicsDeviceSubscribeResourceCreatedFunction
        graphics_device_subscribe_resource_created;
    GraphicsDeviceSubscribeResourceDestroyedFunction
        graphics_device_subscribe_resource_destroyed;
    GameUnaryFunction graphics_device_unsubscribe;
    TextureCreateFunction texture2d_create;
    TextureCreateEncodedFunction texture2d_create_from_encoded_memory;
    TextureGetInfoFunction texture2d_get_info;
    TextureSetColorsFunction texture2d_set_data_rgba8;
    TextureGetColorsFunction texture2d_get_data_rgba8;
    TextureSetDataFunction texture2d_set_data;
    TextureGetDataFunction texture2d_get_data;
    TextureEncodedSizeFunction texture2d_get_encoded_byte_count;
    TextureCopyEncodedFunction texture2d_copy_encoded;
    GameUnaryFunction texture2d_destroy;
    TextureCubeCreateFunction texturecube_create;
    TextureCubeGetInfoFunction texturecube_get_info;
    TextureCubeSetDataFunction texturecube_set_data;
    TextureCubeGetDataFunction texturecube_get_data;
    GameUnaryFunction texturecube_destroy;
    Texture3DCreateFunction texture3d_create;
    Texture3DGetInfoFunction texture3d_get_info;
    Texture3DSetDataFunction texture3d_set_data;
    Texture3DGetDataFunction texture3d_get_data;
    GameUnaryFunction texture3d_destroy;
    HandleGetHandleFunction effect_create_empty;
    EffectCreateCompiledFunction effect_create_compiled;
    GameUnaryFunction effect_destroy;
    HandleGetHandleFunction effect_clone;
    GameUnaryFunction effect_apply;
    HandleGetHandleFunction effect_get_parameters;
    HandleGetHandleFunction effect_get_techniques;
    HandleGetHandleFunction effect_get_current_technique;
    HandleSetHandleFunction effect_set_current_technique;
    GameGetUint32Function effect_technique_get_index_ext;
    GameGetSizeFunction effect_technique_get_name_byte_count;
    GameCopyStringFunction effect_technique_copy_name;
    HandleGetHandleFunction effect_technique_get_passes;
    HandleGetHandleFunction effect_technique_get_annotations;
    GameUnaryFunction effect_technique_destroy;
    GameGetSizeFunction effect_pass_get_name_byte_count;
    GameCopyStringFunction effect_pass_copy_name;
    HandleGetHandleFunction effect_pass_get_annotations;
    GameUnaryFunction effect_pass_apply;
    GameUnaryFunction effect_pass_destroy;
    HandleGetInfoFunction effect_parameter_get_info;
    GameGetSizeFunction effect_parameter_get_name_byte_count;
    GameCopyStringFunction effect_parameter_copy_name;
    GameGetSizeFunction effect_parameter_get_semantic_byte_count;
    GameCopyStringFunction effect_parameter_copy_semantic;
    HandleGetHandleFunction effect_parameter_get_elements;
    HandleGetHandleFunction effect_parameter_get_structure_members;
    HandleGetHandleFunction effect_parameter_get_annotations;
    EffectParameterGetValueFunction effect_parameter_get_value;
    EffectParameterGetValuesFunction effect_parameter_get_values;
    EffectParameterSetValueFunction effect_parameter_set_value;
    EffectParameterSetValuesFunction effect_parameter_set_values;
    GameGetSizeFunction effect_parameter_get_value_string_byte_count;
    GameCopyStringFunction effect_parameter_copy_value_string;
    GameSetStringFunction effect_parameter_set_value_string;
    EffectParameterGetTextureFunction effect_parameter_get_value_texture;
    EffectParameterSetTextureFunction effect_parameter_set_value_texture;
    GameUnaryFunction effect_parameter_destroy;
    GameGetSizeFunction effect_parameter_collection_get_count;
    HandleIndexGetHandleFunction effect_parameter_collection_get_at;
    GameUnaryFunction effect_parameter_collection_destroy;
    GameGetSizeFunction effect_technique_collection_get_count;
    HandleIndexGetHandleFunction effect_technique_collection_get_at;
    GameUnaryFunction effect_technique_collection_destroy;
    GameGetSizeFunction effect_pass_collection_get_count;
    HandleIndexGetHandleFunction effect_pass_collection_get_at;
    GameUnaryFunction effect_pass_collection_destroy;
    GameGetSizeFunction effect_annotation_collection_get_count;
    HandleIndexGetHandleFunction effect_annotation_collection_get_at;
    GameUnaryFunction effect_annotation_collection_destroy;
    HandleGetInfoFunction effect_annotation_get_info;
    GameGetSizeFunction effect_annotation_get_name_byte_count;
    GameCopyStringFunction effect_annotation_copy_name;
    GameGetSizeFunction effect_annotation_get_semantic_byte_count;
    GameCopyStringFunction effect_annotation_copy_semantic;
    HandleGetValueFunction effect_annotation_get_value_boolean;
    HandleGetValueFunction effect_annotation_get_value_int32;
    HandleGetValueFunction effect_annotation_get_value_single;
    HandleGetValueFunction effect_annotation_get_value_vector2;
    HandleGetValueFunction effect_annotation_get_value_vector3;
    HandleGetValueFunction effect_annotation_get_value_vector4;
    HandleGetValueFunction effect_annotation_get_value_matrix;
    GameGetSizeFunction effect_annotation_get_value_string_byte_count;
    GameCopyStringFunction effect_annotation_copy_value_string;
    GameUnaryFunction effect_annotation_destroy;
    HandleGetHandleFunction basic_effect_create;
    HandleGetHandleFunction effect_material_create;
    GameGetBoolFunction basic_effect_get_vertex_color_enabled;
    GameSetBoolFunction basic_effect_set_vertex_color_enabled;
    GameGetBoolFunction basic_effect_get_prefer_per_pixel_lighting;
    GameSetBoolFunction basic_effect_set_prefer_per_pixel_lighting;
    HandleGetVector3Function basic_effect_get_diffuse_color;
    HandleSetVector3Function basic_effect_set_diffuse_color;
    HandleGetVector3Function basic_effect_get_emissive_color;
    HandleSetVector3Function basic_effect_set_emissive_color;
    HandleGetVector3Function basic_effect_get_specular_color;
    HandleSetVector3Function basic_effect_set_specular_color;
    HandleGetFloatFunction basic_effect_get_specular_power;
    HandleSetFloatFunction basic_effect_set_specular_power;
    HandleGetFloatFunction basic_effect_get_alpha;
    HandleSetFloatFunction basic_effect_set_alpha;
    GameGetBoolFunction basic_effect_get_texture_enabled;
    GameSetBoolFunction basic_effect_set_texture_enabled;
    HandleSetHandleFunction basic_effect_set_texture;
    HandleGetHandleFunction alpha_test_effect_create;
    HandleGetVector3Function alpha_test_effect_get_diffuse_color;
    HandleSetVector3Function alpha_test_effect_set_diffuse_color;
    HandleGetFloatFunction alpha_test_effect_get_alpha;
    HandleSetFloatFunction alpha_test_effect_set_alpha;
    HandleSetHandleFunction alpha_test_effect_set_texture;
    GameGetBoolFunction alpha_test_effect_get_vertex_color_enabled;
    GameSetBoolFunction alpha_test_effect_set_vertex_color_enabled;
    HandleGetCompareFunction alpha_test_effect_get_alpha_function;
    HandleSetCompareFunction alpha_test_effect_set_alpha_function;
    GameGetInt32Function alpha_test_effect_get_reference_alpha;
    GameSetInt32Function alpha_test_effect_set_reference_alpha;
    HandleGetHandleFunction dual_texture_effect_create;
    HandleGetVector3Function dual_texture_effect_get_diffuse_color;
    HandleSetVector3Function dual_texture_effect_set_diffuse_color;
    HandleGetFloatFunction dual_texture_effect_get_alpha;
    HandleSetFloatFunction dual_texture_effect_set_alpha;
    HandleUint32SetHandleFunction dual_texture_effect_set_texture;
    GameGetBoolFunction dual_texture_effect_get_vertex_color_enabled;
    GameSetBoolFunction dual_texture_effect_set_vertex_color_enabled;
    HandleGetHandleFunction environment_map_effect_create;
    HandleGetVector3Function environment_map_effect_get_diffuse_color;
    HandleSetVector3Function environment_map_effect_set_diffuse_color;
    HandleGetVector3Function environment_map_effect_get_emissive_color;
    HandleSetVector3Function environment_map_effect_set_emissive_color;
    HandleGetFloatFunction environment_map_effect_get_alpha;
    HandleSetFloatFunction environment_map_effect_set_alpha;
    HandleSetHandleFunction environment_map_effect_set_texture;
    HandleSetHandleFunction environment_map_effect_set_environment_map;
    HandleGetFloatFunction environment_map_effect_get_amount;
    HandleSetFloatFunction environment_map_effect_set_amount;
    HandleGetVector3Function environment_map_effect_get_specular;
    HandleSetVector3Function environment_map_effect_set_specular;
    HandleGetFloatFunction environment_map_effect_get_fresnel_factor;
    HandleSetFloatFunction environment_map_effect_set_fresnel_factor;
    HandleGetHandleFunction skinned_effect_create;
    HandleGetVector3Function skinned_effect_get_diffuse_color;
    HandleSetVector3Function skinned_effect_set_diffuse_color;
    HandleGetVector3Function skinned_effect_get_emissive_color;
    HandleSetVector3Function skinned_effect_set_emissive_color;
    HandleGetVector3Function skinned_effect_get_specular_color;
    HandleSetVector3Function skinned_effect_set_specular_color;
    HandleGetFloatFunction skinned_effect_get_specular_power;
    HandleSetFloatFunction skinned_effect_set_specular_power;
    HandleGetFloatFunction skinned_effect_get_alpha;
    HandleSetFloatFunction skinned_effect_set_alpha;
    GameGetBoolFunction skinned_effect_get_prefer_per_pixel_lighting;
    GameSetBoolFunction skinned_effect_set_prefer_per_pixel_lighting;
    HandleSetHandleFunction skinned_effect_set_texture;
    GameGetInt32Function skinned_effect_get_weights_per_vertex;
    GameSetInt32Function skinned_effect_set_weights_per_vertex;
    SkinnedEffectSetBonesFunction skinned_effect_set_bone_transforms;
    SkinnedEffectCopyBonesFunction skinned_effect_copy_bone_transforms;
    HandleGetHandleFunction occlusion_query_create;
    GameUnaryFunction occlusion_query_begin;
    GameUnaryFunction occlusion_query_end;
    GameGetBoolFunction occlusion_query_get_is_complete;
    GameGetInt32Function occlusion_query_get_pixel_count;
    GameUnaryFunction occlusion_query_destroy;
    HandleGetMatrixFunction effect_matrices_get_world;
    HandleSetMatrixFunction effect_matrices_set_world;
    HandleGetMatrixFunction effect_matrices_get_view;
    HandleSetMatrixFunction effect_matrices_set_view;
    HandleGetMatrixFunction effect_matrices_get_projection;
    HandleSetMatrixFunction effect_matrices_set_projection;
    HandleGetVector3Function effect_fog_get_color;
    HandleSetVector3Function effect_fog_set_color;
    GameGetBoolFunction effect_fog_get_enabled;
    GameSetBoolFunction effect_fog_set_enabled;
    HandleGetFloatFunction effect_fog_get_start;
    HandleSetFloatFunction effect_fog_set_start;
    HandleGetFloatFunction effect_fog_get_end;
    HandleSetFloatFunction effect_fog_set_end;
    HandleGetVector3Function effect_lights_get_ambient_color;
    HandleSetVector3Function effect_lights_set_ambient_color;
    HandleUint32GetHandleFunction effect_lights_get_directional_light;
    GameGetBoolFunction effect_lights_get_enabled;
    GameSetBoolFunction effect_lights_set_enabled;
    GameUnaryFunction effect_lights_enable_default;
    GameUnaryFunction directional_light_destroy;
    HandleGetVector3Function directional_light_get_diffuse_color;
    HandleSetVector3Function directional_light_set_diffuse_color;
    HandleGetVector3Function directional_light_get_direction;
    HandleSetVector3Function directional_light_set_direction;
    HandleGetVector3Function directional_light_get_specular_color;
    HandleSetVector3Function directional_light_set_specular_color;
    GameGetBoolFunction directional_light_get_enabled;
    GameSetBoolFunction directional_light_set_enabled;
    RenderTarget2DCreateFunction render_target2d_create;
    RenderTargetCubeCreateFunction render_target_cube_create;
    RenderTargetGetInfoFunction render_target_get_info;
    GameSetUint64Function graphics_device_set_render_target2d;
    GraphicsDeviceSetRenderTargetCubeFunction graphics_device_set_render_target_cube;
    GraphicsDeviceSetRenderTargetsFunction graphics_device_set_render_targets;
    GameGetSizeFunction graphics_device_get_render_target_count;
    GraphicsDeviceCopyRenderTargetsFunction graphics_device_copy_render_targets;
    GameUnaryFunction render_target_destroy;
    VertexDeclarationCreateWithStrideFunction vertex_declaration_create_with_stride;
    GameUnaryFunction vertex_declaration_destroy;
    VertexBufferCreateFunction vertex_buffer_create;
    VertexBufferGetInfoFunction vertex_buffer_get_info;
    VertexBufferSetFunction vertex_buffer_set_data;
    VertexBufferSetRawFunction vertex_buffer_set_data_raw;
    VertexBufferSetRawAtFunction vertex_buffer_set_data_raw_at;
    VertexBufferGetRawFunction vertex_buffer_get_data_raw;
    VertexBufferSubscribeFunction vertex_buffer_subscribe_content_lost;
    VertexBufferUnsubscribeFunction vertex_buffer_unsubscribe_content_lost;
    GameUnaryFunction vertex_buffer_destroy;
    IndexBufferCreateFunction index_buffer_create;
    IndexBufferGetInfoFunction index_buffer_get_info;
    IndexBufferSetFunction index_buffer_set_data;
    IndexBufferSetAtFunction index_buffer_set_data_at;
    IndexBufferGetFunction index_buffer_get_data;
    IndexBufferSubscribeFunction index_buffer_subscribe_content_lost;
    IndexBufferUnsubscribeFunction index_buffer_unsubscribe_content_lost;
    GameUnaryFunction index_buffer_destroy;
    GraphicsDeviceSetVertexBufferFunction graphics_device_set_vertex_buffer;
    GraphicsDeviceSetVertexBufferOffsetFunction graphics_device_set_vertex_buffer_offset;
    GraphicsDeviceSetVertexBuffersFunction graphics_device_set_vertex_buffers;
    GameGetSizeFunction graphics_device_get_vertex_buffer_count;
    GraphicsDeviceCopyVertexBuffersFunction graphics_device_copy_vertex_buffers;
    GraphicsDeviceSetIndexBufferFunction graphics_device_set_index_buffer;
    GraphicsDeviceGetIndexBufferFunction graphics_device_get_index_buffer;
    GraphicsDeviceDrawPrimitivesFunction graphics_device_draw_primitives;
    GraphicsDeviceDrawIndexedPrimitivesFunction graphics_device_draw_indexed_primitives;
    GraphicsDeviceDrawInstancedPrimitivesFunction graphics_device_draw_instanced_primitives;
    GraphicsDeviceDrawUserPrimitivesFunction graphics_device_draw_user_primitives;
    GraphicsDeviceDrawUserIndexedPrimitivesFunction
        graphics_device_draw_user_indexed_primitives;
    SpriteBatchCreateFunction sprite_batch_create;
    SpriteBatchBeginFunction sprite_batch_begin;
    SpriteBatchBeginStatesFunction sprite_batch_begin_with_states;
    SpriteBatchBeginEffectFunction sprite_batch_begin_with_effect;
    SpriteBatchSubmitFunction sprite_batch_submit_many;
    SpriteBatchSubmitScaledFunction sprite_batch_submit_scaled_many;
    SpriteBatchDrawStringFunction sprite_batch_draw_string;
    GameUnaryFunction sprite_batch_end;
    GameUnaryFunction sprite_batch_destroy;
    ContentManagerCreateFunction content_manager_create;
    GameSetStringFunction content_manager_set_root_directory;
    ContentManagerLoadTextureFunction content_manager_load_texture2d;
    ContentManagerLoadSpriteFontFunction content_manager_load_sprite_font;
    GameUnaryFunction content_manager_unload;
    GameUnaryFunction content_manager_register_builtin_loaders;
    GameUnaryFunction content_manager_destroy;
    SpriteFontCreateFunction sprite_font_create;
    SpriteFontGetInfoFunction sprite_font_get_info;
    SpriteFontCopyCharactersFunction sprite_font_copy_characters;
    SpriteFontSetDefaultCharacterFunction sprite_font_set_default_character;
    SpriteFontSetLineSpacingFunction sprite_font_set_line_spacing;
    SpriteFontSetSpacingFunction sprite_font_set_spacing;
    SpriteFontMeasureFunction sprite_font_measure_utf8;
    GameUnaryFunction sprite_font_destroy;
    AudioSoundCreateRangeFunction sound_effect_create_pcm16_range_ext;
    AudioBytesCreateFunction sound_effect_create_from_encoded_ext;
    GameUnaryFunction sound_effect_destroy;
    HandleGetHandleFunction sound_effect_create_instance;
    GameGetBoolFunction sound_effect_play;
    AudioPlaySettingsFunction sound_effect_play_with_settings;
    GameGetInt64Function sound_effect_get_duration_ticks;
    GameGetSizeFunction sound_effect_get_name_size;
    GameCopyStringFunction sound_effect_copy_name;
    GameSetStringFunction sound_effect_set_name;
    HandleGetFloatFunction sound_effect_get_master_volume;
    HandleSetFloatFunction sound_effect_set_master_volume;
    HandleGetFloatFunction sound_effect_get_distance_scale;
    HandleSetFloatFunction sound_effect_set_distance_scale;
    HandleGetFloatFunction sound_effect_get_doppler_scale;
    HandleSetFloatFunction sound_effect_set_doppler_scale;
    HandleGetFloatFunction sound_effect_get_speed_of_sound;
    HandleSetFloatFunction sound_effect_set_speed_of_sound;
    GameUnaryFunction sound_effect_instance_play;
    GameUnaryFunction sound_effect_instance_pause;
    GameUnaryFunction sound_effect_instance_resume;
    AudioInstanceStopFunction sound_effect_instance_stop;
    AudioInstanceInfoFunction sound_effect_instance_get_info;
    HandleSetFloatFunction sound_effect_instance_set_volume;
    HandleSetFloatFunction sound_effect_instance_set_pitch;
    HandleSetFloatFunction sound_effect_instance_set_pan;
    AudioInstanceSetBoolFunction sound_effect_instance_set_is_looped;
    GameUnaryFunction sound_effect_instance_destroy;
    AudioApply3DFunction sound_effect_instance_apply_3d;
    AudioApply3DMultiFunction sound_effect_instance_apply_3d_multi_ext;
    AudioDynamicCreateFunction dynamic_sound_effect_instance_create;
    GameGetInt32Function dynamic_sound_effect_instance_get_pending_buffer_count;
    AudioSubmitBufferFunction dynamic_sound_effect_instance_submit_buffer;
    AudioSubscribeFunction dynamic_sound_effect_instance_subscribe_buffer_needed;
    GameUnaryFunction audio_unsubscribe_ext;
    GameGetSizeFunction microphone_get_count;
    MicrophoneDefaultFunction microphone_get_default_index_ext;
    MicrophoneIndexGetSizeFunction microphone_get_name_size_at;
    MicrophoneIndexCopyStringFunction microphone_copy_name_at;
    MicrophoneIndexGetInt64Function microphone_get_buffer_duration_ticks_at;
    MicrophoneIndexSetInt64Function microphone_set_buffer_duration_ticks_at;
    MicrophoneIndexGetBoolFunction microphone_get_is_headset_at;
    MicrophoneIndexGetInt32Function microphone_get_sample_rate_at;
    MicrophoneIndexGetStateFunction microphone_get_state_at;
    MicrophoneIndexUnaryFunction microphone_start_at;
    MicrophoneIndexUnaryFunction microphone_stop_at;
    MicrophoneGetDataFunction microphone_get_data_at;
    MicrophoneSubscribeFunction microphone_subscribe_buffer_ready_at;
    AudioEngineCreateFunction audio_engine_create_with_renderer;
    GameUnaryFunction audio_engine_destroy;
    GameGetSizeFunction audio_engine_get_renderer_count;
    HandleIndexGetSizeFunction audio_engine_get_renderer_friendly_name_size;
    HandleIndexCopyStringFunction audio_engine_copy_renderer_friendly_name;
    HandleIndexGetSizeFunction audio_engine_get_renderer_id_size;
    HandleIndexCopyStringFunction audio_engine_copy_renderer_id;
    HandleStringGetHandleFunction audio_engine_get_category;
    HandleStringGetFloatFunction audio_engine_get_global_variable;
    HandleStringSetFloatFunction audio_engine_set_global_variable;
    GameUnaryFunction audio_engine_update;
    GameUnaryFunction audio_category_destroy;
    GameGetSizeFunction audio_category_get_name_size;
    GameCopyStringFunction audio_category_copy_name;
    GameUnaryFunction audio_category_pause;
    GameUnaryFunction audio_category_resume;
    HandleSetFloatFunction audio_category_set_volume;
    CategoryStopFunction audio_category_stop;
    CategoryEqualsFunction audio_category_equals;
    GameGetInt32Function audio_category_get_hash_code;
    WaveBankCreateFunction wave_bank_create;
    WaveBankStreamingCreateFunction wave_bank_create_streaming;
    GameUnaryFunction wave_bank_destroy;
    GameGetBoolFunction wave_bank_get_is_prepared;
    GameGetBoolFunction wave_bank_get_is_in_use;
    WaveBankCreateFunction sound_bank_create;
    GameUnaryFunction sound_bank_destroy;
    GameGetBoolFunction sound_bank_get_is_in_use;
    HandleStringGetHandleFunction sound_bank_get_cue;
    GameSetStringFunction sound_bank_play_cue;
    SoundBankPlay3DFunction sound_bank_play_cue_3d;
    GameUnaryFunction cue_destroy;
    CueInfoFunction cue_get_info;
    AudioApply3DFunction cue_apply_3d;
    HandleStringGetFloatFunction cue_get_variable;
    HandleStringSetFloatFunction cue_set_variable;
    GameUnaryFunction cue_play;
    GameUnaryFunction cue_pause;
    GameUnaryFunction cue_resume;
    CueStopFunction cue_stop;
} CnaFunctions;

typedef struct JavaGameContext {
    jobject game;
    jobject graphics_device;
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
    jmethodID window_event;
    jmethodID graphics_device_event;
    atomic_int callbacks_enabled;
    char callback_error[512];
} JavaGameContext;

typedef struct JavaWindowEventContext {
    JavaGameContext* game;
    jint event;
} JavaWindowEventContext;

typedef struct JavaGraphicsDeviceEventContext {
    JavaGameContext* game;
    jint event;
} JavaGraphicsDeviceEventContext;

typedef struct JavaGame {
    CNA_Handle cna_handle;
    JavaGameContext* context;
    JavaWindowEventContext window_events[3];
    CNA_GameEventRegistrationHandle window_registrations[3];
    JavaGraphicsDeviceEventContext graphics_device_events[4];
    CNA_GraphicsDeviceEventRegistrationHandle graphics_device_registrations[6];
} JavaGame;

typedef struct JavaGraphicsDeviceManagerContext {
    jobject manager;
    jmethodID event;
    jmethodID preparing_device_settings;
    atomic_int callbacks_enabled;
} JavaGraphicsDeviceManagerContext;

typedef struct JavaGraphicsDeviceManagerEventContext {
    JavaGraphicsDeviceManagerContext* manager;
    jint event;
} JavaGraphicsDeviceManagerEventContext;

typedef struct JavaGraphicsDeviceManager {
    CNA_GraphicsDeviceManagerHandle cna_handle;
    JavaGraphicsDeviceManagerContext* context;
    JavaGraphicsDeviceManagerEventContext events[5];
    CNA_GameEventRegistrationHandle registrations[5];
    CNA_GameEventRegistrationHandle preparing_registration;
} JavaGraphicsDeviceManager;

typedef struct JavaBufferContentLostRegistration {
    jobject target;
    jmethodID event;
    CNA_Handle native_registration;
    atomic_int callbacks_enabled;
    int is_vertex;
} JavaBufferContentLostRegistration;

typedef struct JavaAudioEventRegistration {
    jobject target;
    jmethodID event;
    CNA_AudioEventRegistrationHandle native_registration;
    atomic_int callbacks_enabled;
} JavaAudioEventRegistration;

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

static void on_buffer_content_lost(CNA_Handle buffer, void* value)
{
    (void)buffer;
    JavaBufferContentLostRegistration* registration =
        (JavaBufferContentLostRegistration*)value;
    if (registration == NULL ||
        atomic_load_explicit(
            &registration->callbacks_enabled, memory_order_acquire) == 0) {
        return;
    }
    int attached = 0;
    JNIEnv* environment = callback_environment(&attached);
    if (environment == NULL) {
        return;
    }
    (*environment)->CallVoidMethod(
        environment, registration->target, registration->event);
    if ((*environment)->ExceptionCheck(environment)) {
        (*environment)->ExceptionClear(environment);
    }
    finish_callback_environment(attached);
}

static void on_audio_event(void* value)
{
    JavaAudioEventRegistration* registration = (JavaAudioEventRegistration*)value;
    if (registration == NULL || atomic_load_explicit(
            &registration->callbacks_enabled, memory_order_acquire) == 0) {
        return;
    }
    int attached = 0;
    JNIEnv* environment = callback_environment(&attached);
    if (environment == NULL) {
        return;
    }
    (*environment)->CallVoidMethod(environment, registration->target, registration->event);
    if ((*environment)->ExceptionCheck(environment)) {
        (*environment)->ExceptionClear(environment);
    }
    finish_callback_environment(attached);
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

static void on_window_event(void* value)
{
    JavaWindowEventContext* window = (JavaWindowEventContext*)value;
    JavaGameContext* context = window->game;
    if (atomic_load_explicit(&context->callbacks_enabled, memory_order_acquire) == 0) {
        return;
    }
    int attached = 0;
    JNIEnv* environment = callback_environment(&attached);
    if (environment == NULL) {
        return;
    }
    (*environment)->CallVoidMethod(
        environment, context->game, context->window_event, window->event);
    if ((*environment)->ExceptionCheck(environment)) {
        (void)capture_java_exception(environment, context, NULL);
    }
    finish_callback_environment(attached);
}

static void dispatch_graphics_device_event(
    JavaGameContext* context,
    jint event,
    jboolean payload_present,
    const char* name,
    uint64_t name_length,
    jboolean tag_present)
{
    if (context->graphics_device == NULL ||
        atomic_load_explicit(&context->callbacks_enabled, memory_order_acquire) == 0 ||
        name_length > (uint64_t)INT32_MAX) {
        return;
    }
    int attached = 0;
    JNIEnv* environment = callback_environment(&attached);
    if (environment == NULL) {
        return;
    }
    jbyteArray projected_name = NULL;
    if (name != NULL || name_length != 0U) {
        projected_name = (*environment)->NewByteArray(environment, (jsize)name_length);
        if (projected_name == NULL) {
            if ((*environment)->ExceptionCheck(environment)) {
                (*environment)->ExceptionClear(environment);
            }
            finish_callback_environment(attached);
            return;
        }
        if (name_length != 0U) {
            (*environment)->SetByteArrayRegion(
                environment, projected_name, 0, (jsize)name_length, (const jbyte*)name);
            if ((*environment)->ExceptionCheck(environment)) {
                (*environment)->ExceptionClear(environment);
                (*environment)->DeleteLocalRef(environment, projected_name);
                finish_callback_environment(attached);
                return;
            }
        }
    }
    (*environment)->CallVoidMethod(
        environment,
        context->graphics_device,
        context->graphics_device_event,
        event,
        payload_present,
        projected_name,
        tag_present);
    if ((*environment)->ExceptionCheck(environment)) {
        (*environment)->ExceptionClear(environment);
    }
    if (projected_name != NULL) {
        (*environment)->DeleteLocalRef(environment, projected_name);
    }
    finish_callback_environment(attached);
}

static void on_graphics_device_event(CNA_Handle graphics_device, void* value)
{
    (void)graphics_device;
    JavaGraphicsDeviceEventContext* event = (JavaGraphicsDeviceEventContext*)value;
    dispatch_graphics_device_event(
        event->game, event->event, JNI_FALSE, NULL, 0U, JNI_FALSE);
}

static void on_graphics_device_resource_created(
    CNA_Handle graphics_device,
    const CNA_ResourceCreatedEventInfo* info,
    void* value)
{
    (void)graphics_device;
    JavaGameContext* context = (JavaGameContext*)value;
    dispatch_graphics_device_event(
        context,
        4,
        info != NULL && info->has_resource == CNA_TRUE ? JNI_TRUE : JNI_FALSE,
        NULL,
        0U,
        JNI_FALSE);
}

static void on_graphics_device_resource_destroyed(
    CNA_Handle graphics_device,
    const CNA_ResourceDestroyedEventInfo* info,
    void* value)
{
    (void)graphics_device;
    JavaGameContext* context = (JavaGameContext*)value;
    dispatch_graphics_device_event(
        context,
        5,
        JNI_FALSE,
        info == NULL ? NULL : info->name.data,
        info == NULL ? 0U : info->name.byte_length,
        info != NULL && info->has_tag == CNA_TRUE ? JNI_TRUE : JNI_FALSE);
}

static void on_graphics_device_manager_event(void* value)
{
    JavaGraphicsDeviceManagerEventContext* event =
        (JavaGraphicsDeviceManagerEventContext*)value;
    JavaGraphicsDeviceManagerContext* context = event->manager;
    if (atomic_load_explicit(&context->callbacks_enabled, memory_order_acquire) == 0) {
        return;
    }
    int attached = 0;
    JNIEnv* environment = callback_environment(&attached);
    if (environment == NULL) {
        return;
    }
    (*environment)->CallVoidMethod(
        environment, context->manager, context->event, event->event);
    if ((*environment)->ExceptionCheck(environment)) {
        (*environment)->ExceptionClear(environment);
    }
    finish_callback_environment(attached);
}

static void on_graphics_device_manager_preparing_device_settings(
    CNA_GraphicsDeviceInformation* information,
    void* value)
{
    JavaGraphicsDeviceManagerContext* context =
        (JavaGraphicsDeviceManagerContext*)value;
    if (information == NULL ||
        atomic_load_explicit(&context->callbacks_enabled, memory_order_acquire) == 0) {
        return;
    }
    int attached = 0;
    JNIEnv* environment = callback_environment(&attached);
    if (environment == NULL) {
        return;
    }

    jint projected[12] = {
        (jint)information->adapter_index,
        (jint)information->graphics_profile,
        (jint)information->presentation_parameters.back_buffer_format,
        (jint)information->presentation_parameters.back_buffer_width,
        (jint)information->presentation_parameters.back_buffer_height,
        (jint)information->presentation_parameters.depth_stencil_format,
        (jint)information->presentation_parameters.multi_sample_count,
        (jint)information->presentation_parameters.presentation_interval,
        (jint)information->presentation_parameters.display_orientation,
        (jint)information->presentation_parameters.render_target_usage,
        information->presentation_parameters.is_full_screen == CNA_TRUE ? 1 : 0,
        information->presentation_parameters.headless_ext == CNA_TRUE ? 1 : 0
    };
    jintArray input = (*environment)->NewIntArray(environment, 12);
    if (input == NULL) {
        if ((*environment)->ExceptionCheck(environment)) {
            (*environment)->ExceptionClear(environment);
        }
        finish_callback_environment(attached);
        return;
    }
    (*environment)->SetIntArrayRegion(environment, input, 0, 12, projected);
    if ((*environment)->ExceptionCheck(environment)) {
        (*environment)->ExceptionClear(environment);
        (*environment)->DeleteLocalRef(environment, input);
        finish_callback_environment(attached);
        return;
    }
    jintArray output = (jintArray)(*environment)->CallObjectMethod(
        environment, context->manager, context->preparing_device_settings, input);
    if ((*environment)->ExceptionCheck(environment)) {
        (*environment)->ExceptionClear(environment);
    } else if (output != NULL && (*environment)->GetArrayLength(environment, output) == 12) {
        (*environment)->GetIntArrayRegion(environment, output, 0, 12, projected);
        if (!(*environment)->ExceptionCheck(environment)) {
            information->adapter_index = (int32_t)projected[0];
            information->graphics_profile = (CNA_GraphicsProfile)(uint32_t)projected[1];
            information->presentation_parameters.back_buffer_format =
                (CNA_SurfaceFormat)(uint32_t)projected[2];
            information->presentation_parameters.back_buffer_width = (int32_t)projected[3];
            information->presentation_parameters.back_buffer_height = (int32_t)projected[4];
            information->presentation_parameters.depth_stencil_format =
                (CNA_DepthFormat)(uint32_t)projected[5];
            information->presentation_parameters.multi_sample_count = (int32_t)projected[6];
            information->presentation_parameters.presentation_interval =
                (CNA_PresentInterval)(uint32_t)projected[7];
            information->presentation_parameters.display_orientation =
                (CNA_DisplayOrientation)(uint32_t)projected[8];
            information->presentation_parameters.render_target_usage =
                (CNA_RenderTargetUsage)(uint32_t)projected[9];
            information->presentation_parameters.is_full_screen =
                projected[10] == 0 ? CNA_FALSE : CNA_TRUE;
            information->presentation_parameters.headless_ext =
                projected[11] == 0 ? CNA_FALSE : CNA_TRUE;
        } else {
            (*environment)->ExceptionClear(environment);
        }
    }
    if (output != NULL) {
        (*environment)->DeleteLocalRef(environment, output);
    }
    (*environment)->DeleteLocalRef(environment, input);
    finish_callback_environment(attached);
}

static JavaGame* java_game(jlong value)
{
    return (JavaGame*)(uintptr_t)value;
}

static CNA_Result graphics_device_from_game(jlong game, CNA_Handle* out_device)
{
    return cna.game_get_graphics_device(java_game(game)->cna_handle, out_device);
}

static JavaGraphicsDeviceManager* java_graphics_device_manager(jlong value)
{
    return (JavaGraphicsDeviceManager*)(uintptr_t)value;
}

static CNA_Result set_handle_output(JNIEnv* environment, jlongArray output, CNA_Handle value)
{
    if (output == NULL || (*environment)->GetArrayLength(environment, output) < 1) {
        return CNA_RESULT_INVALID_ARGUMENT;
    }
    jlong projected;
    (void)memcpy(&projected, &value, sizeof(projected));
    (*environment)->SetLongArrayRegion(environment, output, 0, 1, &projected);
    return (*environment)->ExceptionCheck(environment)
        ? CNA_RESULT_INVALID_STATE : CNA_RESULT_SUCCESS;
}

static CNA_Color color_from_packed(jint packed)
{
    const uint32_t value = (uint32_t)packed;
    CNA_Color color;
    color.r = (uint8_t)(value & UINT32_C(0xff));
    color.g = (uint8_t)((value >> 8U) & UINT32_C(0xff));
    color.b = (uint8_t)((value >> 16U) & UINT32_C(0xff));
    color.a = (uint8_t)((value >> 24U) & UINT32_C(0xff));
    return color;
}

static jint packed_from_color(CNA_Color color)
{
    const uint32_t value = (uint32_t)color.r
        | ((uint32_t)color.g << 8U)
        | ((uint32_t)color.b << 16U)
        | ((uint32_t)color.a << 24U);
    return (jint)value;
}

/* XNA assigns Min=3 and Max=4; CNA's C enum intentionally uses Max=3 and Min=4. */
static jint java_blend_function_from_c(CNA_BlendFunction value)
{
    if (value == CNA_BLEND_FUNCTION_MAX) {
        return 4;
    }
    if (value == CNA_BLEND_FUNCTION_MIN) {
        return 3;
    }
    return (jint)value;
}

static CNA_BlendFunction c_blend_function_from_java(jint value)
{
    if (value == 3) {
        return CNA_BLEND_FUNCTION_MIN;
    }
    if (value == 4) {
        return CNA_BLEND_FUNCTION_MAX;
    }
    return (CNA_BlendFunction)(uint32_t)value;
}

static CNA_Result blend_state_from_java_array(
    JNIEnv* environment, jintArray input, CNA_BlendState* state)
{
    if (input == NULL || state == NULL ||
        (*environment)->GetArrayLength(environment, input) < 12) {
        return CNA_RESULT_INVALID_ARGUMENT;
    }
    jint values[12];
    (*environment)->GetIntArrayRegion(environment, input, 0, 12, values);
    if ((*environment)->ExceptionCheck(environment)) {
        return CNA_RESULT_INVALID_STATE;
    }
    (void)memset(state, 0, sizeof(*state));
    state->struct_size = (uint32_t)sizeof(*state);
    state->struct_version = UINT32_C(1);
    state->alpha_blend_function = c_blend_function_from_java(values[0]);
    state->alpha_destination_blend = (CNA_Blend)(uint32_t)values[1];
    state->alpha_source_blend = (CNA_Blend)(uint32_t)values[2];
    state->color_blend_function = c_blend_function_from_java(values[3]);
    state->color_destination_blend = (CNA_Blend)(uint32_t)values[4];
    state->color_source_blend = (CNA_Blend)(uint32_t)values[5];
    state->color_write_channels = (CNA_ColorWriteChannels)(uint32_t)values[6];
    state->color_write_channels1 = (CNA_ColorWriteChannels)(uint32_t)values[7];
    state->color_write_channels2 = (CNA_ColorWriteChannels)(uint32_t)values[8];
    state->color_write_channels3 = (CNA_ColorWriteChannels)(uint32_t)values[9];
    state->blend_factor = color_from_packed(values[10]);
    state->multi_sample_mask = (int32_t)values[11];
    return CNA_RESULT_SUCCESS;
}

static CNA_Result depth_state_from_java_array(
    JNIEnv* environment, jintArray input, CNA_DepthStencilState* state)
{
    if (input == NULL || state == NULL ||
        (*environment)->GetArrayLength(environment, input) < 16) {
        return CNA_RESULT_INVALID_ARGUMENT;
    }
    jint values[16];
    (*environment)->GetIntArrayRegion(environment, input, 0, 16, values);
    if ((*environment)->ExceptionCheck(environment)) {
        return CNA_RESULT_INVALID_STATE;
    }
    (void)memset(state, 0, sizeof(*state));
    state->struct_size = (uint32_t)sizeof(*state);
    state->struct_version = UINT32_C(1);
    state->depth_buffer_enable = (CNA_Bool)(uint8_t)values[0];
    state->depth_buffer_write_enable = (CNA_Bool)(uint8_t)values[1];
    state->stencil_enable = (CNA_Bool)(uint8_t)values[2];
    state->two_sided_stencil_mode = (CNA_Bool)(uint8_t)values[3];
    state->depth_buffer_function = (CNA_CompareFunction)(uint32_t)values[4];
    state->stencil_function = (CNA_CompareFunction)(uint32_t)values[5];
    state->stencil_mask = (int32_t)values[6];
    state->stencil_write_mask = (int32_t)values[7];
    state->reference_stencil = (int32_t)values[8];
    state->stencil_fail = (CNA_StencilOperation)(uint32_t)values[9];
    state->stencil_depth_buffer_fail = (CNA_StencilOperation)(uint32_t)values[10];
    state->stencil_pass = (CNA_StencilOperation)(uint32_t)values[11];
    state->counter_clockwise_stencil_function =
        (CNA_CompareFunction)(uint32_t)values[12];
    state->counter_clockwise_stencil_fail =
        (CNA_StencilOperation)(uint32_t)values[13];
    state->counter_clockwise_stencil_depth_buffer_fail =
        (CNA_StencilOperation)(uint32_t)values[14];
    state->counter_clockwise_stencil_pass =
        (CNA_StencilOperation)(uint32_t)values[15];
    return CNA_RESULT_SUCCESS;
}

static CNA_Result rasterizer_state_from_java_arrays(
    JNIEnv* environment,
    jintArray integer_input,
    jfloatArray float_input,
    CNA_RasterizerState* state)
{
    if (integer_input == NULL || float_input == NULL || state == NULL ||
        (*environment)->GetArrayLength(environment, integer_input) < 4 ||
        (*environment)->GetArrayLength(environment, float_input) < 2) {
        return CNA_RESULT_INVALID_ARGUMENT;
    }
    jint integers[4];
    jfloat floats[2];
    (*environment)->GetIntArrayRegion(environment, integer_input, 0, 4, integers);
    (*environment)->GetFloatArrayRegion(environment, float_input, 0, 2, floats);
    if ((*environment)->ExceptionCheck(environment)) {
        return CNA_RESULT_INVALID_STATE;
    }
    (void)memset(state, 0, sizeof(*state));
    state->struct_size = (uint32_t)sizeof(*state);
    state->struct_version = UINT32_C(1);
    state->cull_mode = (CNA_CullMode)(uint32_t)integers[0];
    state->fill_mode = (CNA_FillMode)(uint32_t)integers[1];
    state->depth_bias = (float)floats[0];
    state->slope_scale_depth_bias = (float)floats[1];
    state->multi_sample_anti_alias = (CNA_Bool)(uint8_t)integers[2];
    state->scissor_test_enable = (CNA_Bool)(uint8_t)integers[3];
    return CNA_RESULT_SUCCESS;
}

static CNA_Result sampler_state_from_java_array(
    JNIEnv* environment,
    jintArray integer_input,
    jfloat bias,
    CNA_SamplerState* state)
{
    if (integer_input == NULL || state == NULL ||
        (*environment)->GetArrayLength(environment, integer_input) < 6) {
        return CNA_RESULT_INVALID_ARGUMENT;
    }
    jint integers[6];
    (*environment)->GetIntArrayRegion(environment, integer_input, 0, 6, integers);
    if ((*environment)->ExceptionCheck(environment)) {
        return CNA_RESULT_INVALID_STATE;
    }
    (void)memset(state, 0, sizeof(*state));
    state->struct_size = (uint32_t)sizeof(*state);
    state->struct_version = UINT32_C(1);
    state->address_u = (CNA_TextureAddressMode)(uint32_t)integers[0];
    state->address_v = (CNA_TextureAddressMode)(uint32_t)integers[1];
    state->address_w = (CNA_TextureAddressMode)(uint32_t)integers[2];
    state->filter = (CNA_TextureFilter)(uint32_t)integers[3];
    state->max_anisotropy = (int32_t)integers[4];
    state->max_mip_level = (int32_t)integers[5];
    state->mip_map_level_of_detail_bias = (float)bias;
    return CNA_RESULT_SUCCESS;
}

static jlong uint32_result(CNA_Result result, uint32_t value)
{
    return result == CNA_RESULT_SUCCESS ? (jlong)value : -(jlong)result;
}

static jlong int32_result(CNA_Result result, int32_t value)
{
    return result == CNA_RESULT_SUCCESS ? (jlong)value : -(jlong)result;
}

static jint bool_result(CNA_Result result, CNA_Bool value)
{
    return result == CNA_RESULT_SUCCESS
        ? (value == CNA_TRUE ? 1 : 0)
        : -(jint)result;
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
    LOAD(framework_dispatcher_update, "cna_framework_dispatcher_update");
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
    LOAD(game_window_subscribe, "cna_game_window_subscribe");
    LOAD(game_unsubscribe, "cna_game_unsubscribe");
    LOAD(keyboard_get_state, "cna_keyboard_get_state");
    LOAD(keyboard_get_state_for_player, "cna_keyboard_get_state_for_player");
    LOAD(gamepad_get_state, "cna_gamepad_get_state");
    LOAD(gamepad_get_state_with_dead_zone, "cna_gamepad_get_state_with_dead_zone");
    LOAD(gamepad_get_capabilities, "cna_gamepad_get_capabilities");
    LOAD(gamepad_set_vibration, "cna_gamepad_set_vibration");
    LOAD(touch_get_capabilities, "cna_touch_get_capabilities");
    LOAD(touch_get_state, "cna_touch_get_state");
    LOAD(touch_panel_get_display_width, "cna_touch_panel_get_display_width");
    LOAD(touch_panel_set_display_width, "cna_touch_panel_set_display_width");
    LOAD(touch_panel_get_display_height, "cna_touch_panel_get_display_height");
    LOAD(touch_panel_set_display_height, "cna_touch_panel_set_display_height");
    LOAD(touch_panel_get_display_orientation,
        "cna_touch_panel_get_display_orientation");
    LOAD(touch_panel_set_display_orientation,
        "cna_touch_panel_set_display_orientation");
    LOAD(touch_panel_get_enabled_gestures,
        "cna_touch_panel_get_enabled_gestures");
    LOAD(touch_panel_set_enabled_gestures,
        "cna_touch_panel_set_enabled_gestures");
    LOAD(touch_panel_get_is_gesture_available,
        "cna_touch_panel_get_is_gesture_available");
    LOAD(touch_panel_get_window_handle, "cna_touch_panel_get_window_handle");
    LOAD(touch_panel_set_window_handle, "cna_touch_panel_set_window_handle");
    LOAD(touch_panel_read_gesture, "cna_touch_panel_read_gesture");
    LOAD(touch_panel_enqueue_gesture_ext, "cna_touch_panel_enqueue_gesture_ext");
    LOAD(touch_panel_set_touch_device_exists_ext,
        "cna_touch_panel_set_touch_device_exists_ext");
    LOAD(touch_panel_set_finger_ext, "cna_touch_panel_set_finger_ext");
    LOAD(touch_panel_raise_touch_event_ext,
        "cna_touch_panel_raise_touch_event_ext");
    LOAD(touch_panel_update_ext, "cna_touch_panel_update_ext");
    LOAD(touch_panel_reset_for_tests_ext, "cna_touch_panel_reset_for_tests_ext");
    LOAD(mouse_get_state, "cna_mouse_get_state");
    LOAD(mouse_set_position, "cna_mouse_set_position");
    LOAD(mouse_get_window_handle, "cna_mouse_get_window_handle");
    LOAD(mouse_set_window_handle, "cna_mouse_set_window_handle");
    LOAD(game_get_graphics_device, "cna_game_get_graphics_device");
    LOAD(graphics_device_manager_create, "cna_graphics_device_manager_create");
    LOAD(graphics_device_manager_get_graphics_profile,
        "cna_graphics_device_manager_get_graphics_profile");
    LOAD(graphics_device_manager_set_graphics_profile,
        "cna_graphics_device_manager_set_graphics_profile");
    LOAD(graphics_device_manager_get_is_full_screen,
        "cna_graphics_device_manager_get_is_full_screen");
    LOAD(graphics_device_manager_set_is_full_screen,
        "cna_graphics_device_manager_set_is_full_screen");
    LOAD(graphics_device_manager_get_prefer_multi_sampling,
        "cna_graphics_device_manager_get_prefer_multi_sampling");
    LOAD(graphics_device_manager_set_prefer_multi_sampling,
        "cna_graphics_device_manager_set_prefer_multi_sampling");
    LOAD(graphics_device_manager_get_preferred_back_buffer_format,
        "cna_graphics_device_manager_get_preferred_back_buffer_format");
    LOAD(graphics_device_manager_set_preferred_back_buffer_format,
        "cna_graphics_device_manager_set_preferred_back_buffer_format");
    LOAD(graphics_device_manager_get_preferred_back_buffer_width,
        "cna_graphics_device_manager_get_preferred_back_buffer_width");
    LOAD(graphics_device_manager_set_preferred_back_buffer_width,
        "cna_graphics_device_manager_set_preferred_back_buffer_width");
    LOAD(graphics_device_manager_get_preferred_back_buffer_height,
        "cna_graphics_device_manager_get_preferred_back_buffer_height");
    LOAD(graphics_device_manager_set_preferred_back_buffer_height,
        "cna_graphics_device_manager_set_preferred_back_buffer_height");
    LOAD(graphics_device_manager_get_preferred_depth_stencil_format,
        "cna_graphics_device_manager_get_preferred_depth_stencil_format");
    LOAD(graphics_device_manager_set_preferred_depth_stencil_format,
        "cna_graphics_device_manager_set_preferred_depth_stencil_format");
    LOAD(graphics_device_manager_get_synchronize_with_vertical_retrace,
        "cna_graphics_device_manager_get_synchronize_with_vertical_retrace");
    LOAD(graphics_device_manager_set_synchronize_with_vertical_retrace,
        "cna_graphics_device_manager_set_synchronize_with_vertical_retrace");
    LOAD(graphics_device_manager_get_supported_orientations,
        "cna_graphics_device_manager_get_supported_orientations");
    LOAD(graphics_device_manager_set_supported_orientations,
        "cna_graphics_device_manager_set_supported_orientations");
    LOAD(graphics_device_manager_apply_changes,
        "cna_graphics_device_manager_apply_changes");
    LOAD(graphics_device_manager_toggle_full_screen,
        "cna_graphics_device_manager_toggle_full_screen");
    LOAD(graphics_device_manager_create_device,
        "cna_graphics_device_manager_create_device");
    LOAD(graphics_device_manager_begin_draw,
        "cna_graphics_device_manager_begin_draw");
    LOAD(graphics_device_manager_end_draw,
        "cna_graphics_device_manager_end_draw");
    LOAD(graphics_device_manager_dispose,
        "cna_graphics_device_manager_dispose");
    LOAD(graphics_device_manager_subscribe,
        "cna_graphics_device_manager_subscribe");
    LOAD(graphics_device_manager_subscribe_preparing_device_settings_ext,
        "cna_graphics_device_manager_subscribe_preparing_device_settings_ext");
    LOAD(graphics_device_manager_destroy,
        "cna_graphics_device_manager_destroy");
    LOAD(graphics_adapter_get_count, "cna_graphics_adapter_get_count");
    LOAD(graphics_adapter_get_info, "cna_graphics_adapter_get_info");
    LOAD(graphics_adapter_copy_description, "cna_graphics_adapter_copy_description");
    LOAD(graphics_adapter_copy_device_name, "cna_graphics_adapter_copy_device_name");
    LOAD(graphics_adapter_get_current_display_mode,
        "cna_graphics_adapter_get_current_display_mode");
    LOAD(graphics_adapter_get_display_mode_count,
        "cna_graphics_adapter_get_display_mode_count");
    LOAD(graphics_adapter_copy_display_modes,
        "cna_graphics_adapter_copy_display_modes");
    LOAD(graphics_adapter_set_device_preferences,
        "cna_graphics_adapter_set_device_preferences");
    LOAD(graphics_adapter_is_profile_supported,
        "cna_graphics_adapter_is_profile_supported");
    LOAD(graphics_adapter_query_render_target_format,
        "cna_graphics_adapter_query_render_target_format");
    LOAD(graphics_adapter_query_backbuffer_format,
        "cna_graphics_adapter_query_backbuffer_format");
    LOAD(graphics_adapter_get_native_monitor_handle,
        "cna_graphics_adapter_get_native_monitor_handle");
    LOAD(graphics_device_get_is_disposed,
        "cna_graphics_device_get_is_disposed");
    LOAD(graphics_device_get_status,
        "cna_graphics_device_get_status");
    LOAD(graphics_device_get_adapter_index,
        "cna_graphics_device_get_adapter_index");
    LOAD(graphics_device_get_graphics_profile,
        "cna_graphics_device_get_graphics_profile");
    LOAD(graphics_device_set_graphics_profile_ext,
        "cna_graphics_device_set_graphics_profile_ext");
    LOAD(graphics_device_get_scissor_rectangle,
        "cna_graphics_device_get_scissor_rectangle");
    LOAD(graphics_device_set_scissor_rectangle,
        "cna_graphics_device_set_scissor_rectangle");
    LOAD(graphics_device_get_viewport,
        "cna_graphics_device_get_viewport");
    LOAD(graphics_device_set_viewport,
        "cna_graphics_device_set_viewport");
    LOAD(graphics_device_get_blend_factor,
        "cna_graphics_device_get_blend_factor");
    LOAD(graphics_device_set_blend_factor,
        "cna_graphics_device_set_blend_factor");
    LOAD(graphics_device_get_blend_state,
        "cna_graphics_device_get_blend_state");
    LOAD(graphics_device_set_blend_state,
        "cna_graphics_device_set_blend_state");
    LOAD(graphics_device_get_depth_stencil_state,
        "cna_graphics_device_get_depth_stencil_state");
    LOAD(graphics_device_set_depth_stencil_state,
        "cna_graphics_device_set_depth_stencil_state");
    LOAD(graphics_device_get_rasterizer_state,
        "cna_graphics_device_get_rasterizer_state");
    LOAD(graphics_device_set_rasterizer_state,
        "cna_graphics_device_set_rasterizer_state");
    LOAD(graphics_device_get_sampler_state,
        "cna_graphics_device_get_sampler_state");
    LOAD(graphics_device_set_sampler_state,
        "cna_graphics_device_set_sampler_state");
    LOAD(graphics_device_get_texture,
        "cna_graphics_device_get_texture");
    LOAD(graphics_device_set_texture,
        "cna_graphics_device_set_texture");
    LOAD(graphics_device_get_multi_sample_mask,
        "cna_graphics_device_get_multi_sample_mask");
    LOAD(graphics_device_set_multi_sample_mask,
        "cna_graphics_device_set_multi_sample_mask");
    LOAD(graphics_device_get_reference_stencil,
        "cna_graphics_device_get_reference_stencil");
    LOAD(graphics_device_set_reference_stencil,
        "cna_graphics_device_set_reference_stencil");
    LOAD(graphics_device_get_presentation_parameters,
        "cna_graphics_device_get_presentation_parameters");
    LOAD(graphics_device_get_display_mode,
        "cna_graphics_device_get_display_mode");
    LOAD(graphics_device_get_backbuffer_info,
        "cna_graphics_device_get_backbuffer_info");
    LOAD(graphics_device_get_backbuffer_data_window,
        "cna_graphics_device_get_backbuffer_data_window");
    LOAD(graphics_device_clear_options,
        "cna_graphics_device_clear_options");
    LOAD(graphics_device_present,
        "cna_graphics_device_present");
    LOAD(graphics_device_reset,
        "cna_graphics_device_reset");
    LOAD(graphics_device_reset_with_parameters,
        "cna_graphics_device_reset_with_parameters");
    LOAD(graphics_device_subscribe_event,
        "cna_graphics_device_subscribe_event");
    LOAD(graphics_device_subscribe_resource_created,
        "cna_graphics_device_subscribe_resource_created");
    LOAD(graphics_device_subscribe_resource_destroyed,
        "cna_graphics_device_subscribe_resource_destroyed");
    LOAD(graphics_device_unsubscribe,
        "cna_graphics_device_unsubscribe");
    LOAD(texture2d_create, "cna_texture2d_create");
    LOAD(texture2d_create_from_encoded_memory, "cna_texture2d_create_from_encoded_memory");
    LOAD(texture2d_get_info, "cna_texture2d_get_info");
    LOAD(texture2d_set_data_rgba8, "cna_texture2d_set_data_rgba8");
    LOAD(texture2d_get_data_rgba8, "cna_texture2d_get_data_rgba8");
    LOAD(texture2d_set_data, "cna_texture2d_set_data");
    LOAD(texture2d_get_data, "cna_texture2d_get_data");
    LOAD(texture2d_get_encoded_byte_count, "cna_texture2d_get_encoded_byte_count");
    LOAD(texture2d_copy_encoded, "cna_texture2d_copy_encoded");
    LOAD(texture2d_destroy, "cna_texture2d_destroy");
    LOAD(texturecube_create, "cna_texturecube_create");
    LOAD(texturecube_get_info, "cna_texturecube_get_info");
    LOAD(texturecube_set_data, "cna_texturecube_set_data");
    LOAD(texturecube_get_data, "cna_texturecube_get_data");
    LOAD(texturecube_destroy, "cna_texturecube_destroy");
    LOAD(texture3d_create, "cna_texture3d_create");
    LOAD(texture3d_get_info, "cna_texture3d_get_info");
    LOAD(texture3d_set_data, "cna_texture3d_set_data");
    LOAD(texture3d_get_data, "cna_texture3d_get_data");
    LOAD(texture3d_destroy, "cna_texture3d_destroy");
    LOAD(effect_create_empty, "cna_effect_create_empty");
    LOAD(effect_create_compiled, "cna_effect_create_compiled");
    LOAD(effect_destroy, "cna_effect_destroy");
    LOAD(effect_clone, "cna_effect_clone");
    LOAD(effect_apply, "cna_effect_apply");
    LOAD(effect_get_parameters, "cna_effect_get_parameters");
    LOAD(effect_get_techniques, "cna_effect_get_techniques");
    LOAD(effect_get_current_technique, "cna_effect_get_current_technique");
    LOAD(effect_set_current_technique, "cna_effect_set_current_technique");
    LOAD(effect_technique_get_index_ext, "cna_effect_technique_get_index_ext");
    LOAD(effect_technique_get_name_byte_count,
        "cna_effect_technique_get_name_byte_count");
    LOAD(effect_technique_copy_name, "cna_effect_technique_copy_name");
    LOAD(effect_technique_get_passes, "cna_effect_technique_get_passes");
    LOAD(effect_technique_get_annotations, "cna_effect_technique_get_annotations");
    LOAD(effect_technique_destroy, "cna_effect_technique_destroy");
    LOAD(effect_pass_get_name_byte_count, "cna_effect_pass_get_name_byte_count");
    LOAD(effect_pass_copy_name, "cna_effect_pass_copy_name");
    LOAD(effect_pass_get_annotations, "cna_effect_pass_get_annotations");
    LOAD(effect_pass_apply, "cna_effect_pass_apply");
    LOAD(effect_pass_destroy, "cna_effect_pass_destroy");
    LOAD(effect_parameter_get_info, "cna_effect_parameter_get_info");
    LOAD(effect_parameter_get_name_byte_count,
        "cna_effect_parameter_get_name_byte_count");
    LOAD(effect_parameter_copy_name, "cna_effect_parameter_copy_name");
    LOAD(effect_parameter_get_semantic_byte_count,
        "cna_effect_parameter_get_semantic_byte_count");
    LOAD(effect_parameter_copy_semantic, "cna_effect_parameter_copy_semantic");
    LOAD(effect_parameter_get_elements, "cna_effect_parameter_get_elements");
    LOAD(effect_parameter_get_structure_members,
        "cna_effect_parameter_get_structure_members");
    LOAD(effect_parameter_get_annotations, "cna_effect_parameter_get_annotations");
    LOAD(effect_parameter_get_value, "cna_effect_parameter_get_value");
    LOAD(effect_parameter_get_values, "cna_effect_parameter_get_values");
    LOAD(effect_parameter_set_value, "cna_effect_parameter_set_value");
    LOAD(effect_parameter_set_values, "cna_effect_parameter_set_values");
    LOAD(effect_parameter_get_value_string_byte_count,
        "cna_effect_parameter_get_value_string_byte_count");
    LOAD(effect_parameter_copy_value_string,
        "cna_effect_parameter_copy_value_string");
    LOAD(effect_parameter_set_value_string, "cna_effect_parameter_set_value_string");
    LOAD(effect_parameter_get_value_texture, "cna_effect_parameter_get_value_texture");
    LOAD(effect_parameter_set_value_texture, "cna_effect_parameter_set_value_texture");
    LOAD(effect_parameter_destroy, "cna_effect_parameter_destroy");
    LOAD(effect_parameter_collection_get_count,
        "cna_effect_parameter_collection_get_count");
    LOAD(effect_parameter_collection_get_at,
        "cna_effect_parameter_collection_get_at");
    LOAD(effect_parameter_collection_destroy,
        "cna_effect_parameter_collection_destroy");
    LOAD(effect_technique_collection_get_count,
        "cna_effect_technique_collection_get_count");
    LOAD(effect_technique_collection_get_at,
        "cna_effect_technique_collection_get_at");
    LOAD(effect_technique_collection_destroy,
        "cna_effect_technique_collection_destroy");
    LOAD(effect_pass_collection_get_count, "cna_effect_pass_collection_get_count");
    LOAD(effect_pass_collection_get_at, "cna_effect_pass_collection_get_at");
    LOAD(effect_pass_collection_destroy, "cna_effect_pass_collection_destroy");
    LOAD(effect_annotation_collection_get_count,
        "cna_effect_annotation_collection_get_count");
    LOAD(effect_annotation_collection_get_at,
        "cna_effect_annotation_collection_get_at");
    LOAD(effect_annotation_collection_destroy,
        "cna_effect_annotation_collection_destroy");
    LOAD(effect_annotation_get_info, "cna_effect_annotation_get_info");
    LOAD(effect_annotation_get_name_byte_count,
        "cna_effect_annotation_get_name_byte_count");
    LOAD(effect_annotation_copy_name, "cna_effect_annotation_copy_name");
    LOAD(effect_annotation_get_semantic_byte_count,
        "cna_effect_annotation_get_semantic_byte_count");
    LOAD(effect_annotation_copy_semantic, "cna_effect_annotation_copy_semantic");
    LOAD(effect_annotation_get_value_boolean,
        "cna_effect_annotation_get_value_boolean");
    LOAD(effect_annotation_get_value_int32, "cna_effect_annotation_get_value_int32");
    LOAD(effect_annotation_get_value_single, "cna_effect_annotation_get_value_single");
    LOAD(effect_annotation_get_value_vector2, "cna_effect_annotation_get_value_vector2");
    LOAD(effect_annotation_get_value_vector3, "cna_effect_annotation_get_value_vector3");
    LOAD(effect_annotation_get_value_vector4, "cna_effect_annotation_get_value_vector4");
    LOAD(effect_annotation_get_value_matrix, "cna_effect_annotation_get_value_matrix");
    LOAD(effect_annotation_get_value_string_byte_count,
        "cna_effect_annotation_get_value_string_byte_count");
    LOAD(effect_annotation_copy_value_string,
        "cna_effect_annotation_copy_value_string");
    LOAD(effect_annotation_destroy, "cna_effect_annotation_destroy");
    LOAD(basic_effect_create, "cna_basic_effect_create");
    LOAD(effect_material_create, "cna_effect_material_create");
    LOAD(basic_effect_get_vertex_color_enabled,
        "cna_basic_effect_get_vertex_color_enabled");
    LOAD(basic_effect_set_vertex_color_enabled,
        "cna_basic_effect_set_vertex_color_enabled");
    LOAD(basic_effect_get_prefer_per_pixel_lighting,
        "cna_basic_effect_get_prefer_per_pixel_lighting");
    LOAD(basic_effect_set_prefer_per_pixel_lighting,
        "cna_basic_effect_set_prefer_per_pixel_lighting");
    LOAD(basic_effect_get_diffuse_color, "cna_basic_effect_get_diffuse_color");
    LOAD(basic_effect_set_diffuse_color, "cna_basic_effect_set_diffuse_color");
    LOAD(basic_effect_get_emissive_color, "cna_basic_effect_get_emissive_color");
    LOAD(basic_effect_set_emissive_color, "cna_basic_effect_set_emissive_color");
    LOAD(basic_effect_get_specular_color, "cna_basic_effect_get_specular_color");
    LOAD(basic_effect_set_specular_color, "cna_basic_effect_set_specular_color");
    LOAD(basic_effect_get_specular_power, "cna_basic_effect_get_specular_power");
    LOAD(basic_effect_set_specular_power, "cna_basic_effect_set_specular_power");
    LOAD(basic_effect_get_alpha, "cna_basic_effect_get_alpha");
    LOAD(basic_effect_set_alpha, "cna_basic_effect_set_alpha");
    LOAD(basic_effect_get_texture_enabled, "cna_basic_effect_get_texture_enabled");
    LOAD(basic_effect_set_texture_enabled, "cna_basic_effect_set_texture_enabled");
    LOAD(basic_effect_set_texture, "cna_basic_effect_set_texture");
    LOAD(alpha_test_effect_create, "cna_alpha_test_effect_create");
    LOAD(alpha_test_effect_get_diffuse_color,
        "cna_alpha_test_effect_get_diffuse_color");
    LOAD(alpha_test_effect_set_diffuse_color,
        "cna_alpha_test_effect_set_diffuse_color");
    LOAD(alpha_test_effect_get_alpha, "cna_alpha_test_effect_get_alpha");
    LOAD(alpha_test_effect_set_alpha, "cna_alpha_test_effect_set_alpha");
    LOAD(alpha_test_effect_set_texture, "cna_alpha_test_effect_set_texture");
    LOAD(alpha_test_effect_get_vertex_color_enabled,
        "cna_alpha_test_effect_get_vertex_color_enabled");
    LOAD(alpha_test_effect_set_vertex_color_enabled,
        "cna_alpha_test_effect_set_vertex_color_enabled");
    LOAD(alpha_test_effect_get_alpha_function,
        "cna_alpha_test_effect_get_alpha_function");
    LOAD(alpha_test_effect_set_alpha_function,
        "cna_alpha_test_effect_set_alpha_function");
    LOAD(alpha_test_effect_get_reference_alpha,
        "cna_alpha_test_effect_get_reference_alpha");
    LOAD(alpha_test_effect_set_reference_alpha,
        "cna_alpha_test_effect_set_reference_alpha");
    LOAD(dual_texture_effect_create, "cna_dual_texture_effect_create");
    LOAD(dual_texture_effect_get_diffuse_color,
        "cna_dual_texture_effect_get_diffuse_color");
    LOAD(dual_texture_effect_set_diffuse_color,
        "cna_dual_texture_effect_set_diffuse_color");
    LOAD(dual_texture_effect_get_alpha, "cna_dual_texture_effect_get_alpha");
    LOAD(dual_texture_effect_set_alpha, "cna_dual_texture_effect_set_alpha");
    LOAD(dual_texture_effect_set_texture, "cna_dual_texture_effect_set_texture");
    LOAD(dual_texture_effect_get_vertex_color_enabled,
        "cna_dual_texture_effect_get_vertex_color_enabled");
    LOAD(dual_texture_effect_set_vertex_color_enabled,
        "cna_dual_texture_effect_set_vertex_color_enabled");
    LOAD(environment_map_effect_create, "cna_environment_map_effect_create");
    LOAD(environment_map_effect_get_diffuse_color,
        "cna_environment_map_effect_get_diffuse_color");
    LOAD(environment_map_effect_set_diffuse_color,
        "cna_environment_map_effect_set_diffuse_color");
    LOAD(environment_map_effect_get_emissive_color,
        "cna_environment_map_effect_get_emissive_color");
    LOAD(environment_map_effect_set_emissive_color,
        "cna_environment_map_effect_set_emissive_color");
    LOAD(environment_map_effect_get_alpha, "cna_environment_map_effect_get_alpha");
    LOAD(environment_map_effect_set_alpha, "cna_environment_map_effect_set_alpha");
    LOAD(environment_map_effect_set_texture, "cna_environment_map_effect_set_texture");
    LOAD(environment_map_effect_set_environment_map,
        "cna_environment_map_effect_set_environment_map");
    LOAD(environment_map_effect_get_amount, "cna_environment_map_effect_get_amount");
    LOAD(environment_map_effect_set_amount, "cna_environment_map_effect_set_amount");
    LOAD(environment_map_effect_get_specular,
        "cna_environment_map_effect_get_specular");
    LOAD(environment_map_effect_set_specular,
        "cna_environment_map_effect_set_specular");
    LOAD(environment_map_effect_get_fresnel_factor,
        "cna_environment_map_effect_get_fresnel_factor");
    LOAD(environment_map_effect_set_fresnel_factor,
        "cna_environment_map_effect_set_fresnel_factor");
    LOAD(skinned_effect_create, "cna_skinned_effect_create");
    LOAD(skinned_effect_get_diffuse_color, "cna_skinned_effect_get_diffuse_color");
    LOAD(skinned_effect_set_diffuse_color, "cna_skinned_effect_set_diffuse_color");
    LOAD(skinned_effect_get_emissive_color, "cna_skinned_effect_get_emissive_color");
    LOAD(skinned_effect_set_emissive_color, "cna_skinned_effect_set_emissive_color");
    LOAD(skinned_effect_get_specular_color, "cna_skinned_effect_get_specular_color");
    LOAD(skinned_effect_set_specular_color, "cna_skinned_effect_set_specular_color");
    LOAD(skinned_effect_get_specular_power, "cna_skinned_effect_get_specular_power");
    LOAD(skinned_effect_set_specular_power, "cna_skinned_effect_set_specular_power");
    LOAD(skinned_effect_get_alpha, "cna_skinned_effect_get_alpha");
    LOAD(skinned_effect_set_alpha, "cna_skinned_effect_set_alpha");
    LOAD(skinned_effect_get_prefer_per_pixel_lighting,
        "cna_skinned_effect_get_prefer_per_pixel_lighting");
    LOAD(skinned_effect_set_prefer_per_pixel_lighting,
        "cna_skinned_effect_set_prefer_per_pixel_lighting");
    LOAD(skinned_effect_set_texture, "cna_skinned_effect_set_texture");
    LOAD(skinned_effect_get_weights_per_vertex,
        "cna_skinned_effect_get_weights_per_vertex");
    LOAD(skinned_effect_set_weights_per_vertex,
        "cna_skinned_effect_set_weights_per_vertex");
    LOAD(skinned_effect_set_bone_transforms,
        "cna_skinned_effect_set_bone_transforms");
    LOAD(skinned_effect_copy_bone_transforms,
        "cna_skinned_effect_copy_bone_transforms");
    LOAD(occlusion_query_create, "cna_occlusion_query_create");
    LOAD(occlusion_query_begin, "cna_occlusion_query_begin");
    LOAD(occlusion_query_end, "cna_occlusion_query_end");
    LOAD(occlusion_query_get_is_complete,
        "cna_occlusion_query_get_is_complete");
    LOAD(occlusion_query_get_pixel_count,
        "cna_occlusion_query_get_pixel_count");
    LOAD(occlusion_query_destroy, "cna_occlusion_query_destroy");
    LOAD(effect_matrices_get_world, "cna_effect_matrices_get_world");
    LOAD(effect_matrices_set_world, "cna_effect_matrices_set_world");
    LOAD(effect_matrices_get_view, "cna_effect_matrices_get_view");
    LOAD(effect_matrices_set_view, "cna_effect_matrices_set_view");
    LOAD(effect_matrices_get_projection, "cna_effect_matrices_get_projection");
    LOAD(effect_matrices_set_projection, "cna_effect_matrices_set_projection");
    LOAD(effect_fog_get_color, "cna_effect_fog_get_color");
    LOAD(effect_fog_set_color, "cna_effect_fog_set_color");
    LOAD(effect_fog_get_enabled, "cna_effect_fog_get_enabled");
    LOAD(effect_fog_set_enabled, "cna_effect_fog_set_enabled");
    LOAD(effect_fog_get_start, "cna_effect_fog_get_start");
    LOAD(effect_fog_set_start, "cna_effect_fog_set_start");
    LOAD(effect_fog_get_end, "cna_effect_fog_get_end");
    LOAD(effect_fog_set_end, "cna_effect_fog_set_end");
    LOAD(effect_lights_get_ambient_color, "cna_effect_lights_get_ambient_color");
    LOAD(effect_lights_set_ambient_color, "cna_effect_lights_set_ambient_color");
    LOAD(effect_lights_get_directional_light,
        "cna_effect_lights_get_directional_light");
    LOAD(effect_lights_get_enabled, "cna_effect_lights_get_enabled");
    LOAD(effect_lights_set_enabled, "cna_effect_lights_set_enabled");
    LOAD(effect_lights_enable_default, "cna_effect_lights_enable_default");
    LOAD(directional_light_destroy, "cna_directional_light_destroy");
    LOAD(directional_light_get_diffuse_color,
        "cna_directional_light_get_diffuse_color");
    LOAD(directional_light_set_diffuse_color,
        "cna_directional_light_set_diffuse_color");
    LOAD(directional_light_get_direction, "cna_directional_light_get_direction");
    LOAD(directional_light_set_direction, "cna_directional_light_set_direction");
    LOAD(directional_light_get_specular_color,
        "cna_directional_light_get_specular_color");
    LOAD(directional_light_set_specular_color,
        "cna_directional_light_set_specular_color");
    LOAD(directional_light_get_enabled, "cna_directional_light_get_enabled");
    LOAD(directional_light_set_enabled, "cna_directional_light_set_enabled");
    LOAD(render_target2d_create, "cna_render_target2d_create");
    LOAD(render_target_cube_create, "cna_render_target_cube_create");
    LOAD(render_target_get_info, "cna_render_target_get_info");
    LOAD(graphics_device_set_render_target2d,
        "cna_graphics_device_set_render_target2d");
    LOAD(graphics_device_set_render_target_cube,
        "cna_graphics_device_set_render_target_cube");
    LOAD(graphics_device_set_render_targets,
        "cna_graphics_device_set_render_targets");
    LOAD(graphics_device_get_render_target_count,
        "cna_graphics_device_get_render_target_count");
    LOAD(graphics_device_copy_render_targets,
        "cna_graphics_device_copy_render_targets");
    LOAD(render_target_destroy, "cna_render_target_destroy");
    LOAD(vertex_declaration_create_with_stride,
        "cna_vertex_declaration_create_with_stride");
    LOAD(vertex_declaration_destroy, "cna_vertex_declaration_destroy");
    LOAD(vertex_buffer_create, "cna_vertex_buffer_create");
    LOAD(vertex_buffer_get_info, "cna_vertex_buffer_get_info");
    LOAD(vertex_buffer_set_data, "cna_vertex_buffer_set_data");
    LOAD(vertex_buffer_set_data_raw, "cna_vertex_buffer_set_data_raw");
    LOAD(vertex_buffer_set_data_raw_at, "cna_vertex_buffer_set_data_raw_at");
    LOAD(vertex_buffer_get_data_raw, "cna_vertex_buffer_get_data_raw");
    LOAD(vertex_buffer_subscribe_content_lost,
        "cna_vertex_buffer_subscribe_content_lost");
    LOAD(vertex_buffer_unsubscribe_content_lost,
        "cna_vertex_buffer_unsubscribe_content_lost");
    LOAD(vertex_buffer_destroy, "cna_vertex_buffer_destroy");
    LOAD(index_buffer_create, "cna_index_buffer_create");
    LOAD(index_buffer_get_info, "cna_index_buffer_get_info");
    LOAD(index_buffer_set_data, "cna_index_buffer_set_data");
    LOAD(index_buffer_set_data_at, "cna_index_buffer_set_data_at");
    LOAD(index_buffer_get_data, "cna_index_buffer_get_data");
    LOAD(index_buffer_subscribe_content_lost,
        "cna_index_buffer_subscribe_content_lost");
    LOAD(index_buffer_unsubscribe_content_lost,
        "cna_index_buffer_unsubscribe_content_lost");
    LOAD(index_buffer_destroy, "cna_index_buffer_destroy");
    LOAD(graphics_device_set_vertex_buffer,
        "cna_graphics_device_set_vertex_buffer");
    LOAD(graphics_device_set_vertex_buffer_offset,
        "cna_graphics_device_set_vertex_buffer_offset");
    LOAD(graphics_device_set_vertex_buffers,
        "cna_graphics_device_set_vertex_buffers");
    LOAD(graphics_device_get_vertex_buffer_count,
        "cna_graphics_device_get_vertex_buffer_count");
    LOAD(graphics_device_copy_vertex_buffers,
        "cna_graphics_device_copy_vertex_buffers");
    LOAD(graphics_device_set_index_buffer,
        "cna_graphics_device_set_index_buffer");
    LOAD(graphics_device_get_index_buffer,
        "cna_graphics_device_get_index_buffer");
    LOAD(graphics_device_draw_primitives,
        "cna_graphics_device_draw_primitives");
    LOAD(graphics_device_draw_indexed_primitives,
        "cna_graphics_device_draw_indexed_primitives");
    LOAD(graphics_device_draw_instanced_primitives,
        "cna_graphics_device_draw_instanced_primitives");
    LOAD(graphics_device_draw_user_primitives,
        "cna_graphics_device_draw_user_primitives");
    LOAD(graphics_device_draw_user_indexed_primitives,
        "cna_graphics_device_draw_user_indexed_primitives");
    LOAD(sprite_batch_create, "cna_sprite_batch_create");
    LOAD(sprite_batch_begin, "cna_sprite_batch_begin");
    LOAD(sprite_batch_begin_with_states, "cna_sprite_batch_begin_with_states");
    LOAD(sprite_batch_begin_with_effect, "cna_sprite_batch_begin_with_effect");
    LOAD(sprite_batch_submit_many, "cna_sprite_batch_submit_many");
    LOAD(sprite_batch_submit_scaled_many, "cna_sprite_batch_submit_scaled_many");
    LOAD(sprite_batch_draw_string, "cna_sprite_batch_draw_string");
    LOAD(sprite_batch_end, "cna_sprite_batch_end");
    LOAD(sprite_batch_destroy, "cna_sprite_batch_destroy");
    LOAD(content_manager_create, "cna_content_manager_create");
    LOAD(content_manager_set_root_directory, "cna_content_manager_set_root_directory");
    LOAD(content_manager_load_texture2d, "cna_content_manager_load_texture2d");
    LOAD(content_manager_load_sprite_font, "cna_content_manager_load_sprite_font");
    LOAD(content_manager_unload, "cna_content_manager_unload");
    LOAD(content_manager_register_builtin_loaders,
        "cna_content_manager_register_builtin_loaders");
    LOAD(content_manager_destroy, "cna_content_manager_destroy");
    LOAD(sprite_font_create, "cna_sprite_font_create");
    LOAD(sprite_font_get_info, "cna_sprite_font_get_info");
    LOAD(sprite_font_copy_characters, "cna_sprite_font_copy_characters");
    LOAD(sprite_font_set_default_character, "cna_sprite_font_set_default_character");
    LOAD(sprite_font_set_line_spacing, "cna_sprite_font_set_line_spacing");
    LOAD(sprite_font_set_spacing, "cna_sprite_font_set_spacing");
    LOAD(sprite_font_measure_utf8, "cna_sprite_font_measure_utf8");
    LOAD(sprite_font_destroy, "cna_sprite_font_destroy");
    LOAD(sound_effect_create_pcm16_range_ext,
        "cna_sound_effect_create_pcm16_range_ext");
    LOAD(sound_effect_create_from_encoded_ext,
        "cna_sound_effect_create_from_encoded_ext");
    LOAD(sound_effect_destroy, "cna_sound_effect_destroy");
    LOAD(sound_effect_create_instance, "cna_sound_effect_create_instance");
    LOAD(sound_effect_play, "cna_sound_effect_play");
    LOAD(sound_effect_play_with_settings, "cna_sound_effect_play_with_settings");
    LOAD(sound_effect_get_duration_ticks, "cna_sound_effect_get_duration_ticks");
    LOAD(sound_effect_get_name_size, "cna_sound_effect_get_name_size");
    LOAD(sound_effect_copy_name, "cna_sound_effect_copy_name");
    LOAD(sound_effect_set_name, "cna_sound_effect_set_name");
    LOAD(sound_effect_get_master_volume, "cna_sound_effect_get_master_volume");
    LOAD(sound_effect_set_master_volume, "cna_sound_effect_set_master_volume");
    LOAD(sound_effect_get_distance_scale, "cna_sound_effect_get_distance_scale");
    LOAD(sound_effect_set_distance_scale, "cna_sound_effect_set_distance_scale");
    LOAD(sound_effect_get_doppler_scale, "cna_sound_effect_get_doppler_scale");
    LOAD(sound_effect_set_doppler_scale, "cna_sound_effect_set_doppler_scale");
    LOAD(sound_effect_get_speed_of_sound, "cna_sound_effect_get_speed_of_sound");
    LOAD(sound_effect_set_speed_of_sound, "cna_sound_effect_set_speed_of_sound");
    LOAD(sound_effect_instance_play, "cna_sound_effect_instance_play");
    LOAD(sound_effect_instance_pause, "cna_sound_effect_instance_pause");
    LOAD(sound_effect_instance_resume, "cna_sound_effect_instance_resume");
    LOAD(sound_effect_instance_stop, "cna_sound_effect_instance_stop");
    LOAD(sound_effect_instance_get_info, "cna_sound_effect_instance_get_info");
    LOAD(sound_effect_instance_set_volume, "cna_sound_effect_instance_set_volume");
    LOAD(sound_effect_instance_set_pitch, "cna_sound_effect_instance_set_pitch");
    LOAD(sound_effect_instance_set_pan, "cna_sound_effect_instance_set_pan");
    LOAD(sound_effect_instance_set_is_looped,
        "cna_sound_effect_instance_set_is_looped");
    LOAD(sound_effect_instance_destroy, "cna_sound_effect_instance_destroy");
    LOAD(sound_effect_instance_apply_3d, "cna_sound_effect_instance_apply_3d");
    LOAD(sound_effect_instance_apply_3d_multi_ext,
        "cna_sound_effect_instance_apply_3d_multi_ext");
    LOAD(dynamic_sound_effect_instance_create,
        "cna_dynamic_sound_effect_instance_create");
    LOAD(dynamic_sound_effect_instance_get_pending_buffer_count,
        "cna_dynamic_sound_effect_instance_get_pending_buffer_count");
    LOAD(dynamic_sound_effect_instance_submit_buffer,
        "cna_dynamic_sound_effect_instance_submit_buffer");
    LOAD(dynamic_sound_effect_instance_subscribe_buffer_needed,
        "cna_dynamic_sound_effect_instance_subscribe_buffer_needed");
    LOAD(audio_unsubscribe_ext, "cna_audio_unsubscribe_ext");
    LOAD(microphone_get_count, "cna_microphone_get_count");
    LOAD(microphone_get_default_index_ext, "cna_microphone_get_default_index_ext");
    LOAD(microphone_get_name_size_at, "cna_microphone_get_name_size_at");
    LOAD(microphone_copy_name_at, "cna_microphone_copy_name_at");
    LOAD(microphone_get_buffer_duration_ticks_at,
        "cna_microphone_get_buffer_duration_ticks_at");
    LOAD(microphone_set_buffer_duration_ticks_at,
        "cna_microphone_set_buffer_duration_ticks_at");
    LOAD(microphone_get_is_headset_at, "cna_microphone_get_is_headset_at");
    LOAD(microphone_get_sample_rate_at, "cna_microphone_get_sample_rate_at");
    LOAD(microphone_get_state_at, "cna_microphone_get_state_at");
    LOAD(microphone_start_at, "cna_microphone_start_at");
    LOAD(microphone_stop_at, "cna_microphone_stop_at");
    LOAD(microphone_get_data_at, "cna_microphone_get_data_at");
    LOAD(microphone_subscribe_buffer_ready_at,
        "cna_microphone_subscribe_buffer_ready_at");
    LOAD(audio_engine_create_with_renderer, "cna_audio_engine_create_with_renderer");
    LOAD(audio_engine_destroy, "cna_audio_engine_destroy");
    LOAD(audio_engine_get_renderer_count, "cna_audio_engine_get_renderer_count");
    LOAD(audio_engine_get_renderer_friendly_name_size,
        "cna_audio_engine_get_renderer_friendly_name_size");
    LOAD(audio_engine_copy_renderer_friendly_name,
        "cna_audio_engine_copy_renderer_friendly_name");
    LOAD(audio_engine_get_renderer_id_size, "cna_audio_engine_get_renderer_id_size");
    LOAD(audio_engine_copy_renderer_id, "cna_audio_engine_copy_renderer_id");
    LOAD(audio_engine_get_category, "cna_audio_engine_get_category");
    LOAD(audio_engine_get_global_variable, "cna_audio_engine_get_global_variable");
    LOAD(audio_engine_set_global_variable, "cna_audio_engine_set_global_variable");
    LOAD(audio_engine_update, "cna_audio_engine_update");
    LOAD(audio_category_destroy, "cna_audio_category_destroy");
    LOAD(audio_category_get_name_size, "cna_audio_category_get_name_size");
    LOAD(audio_category_copy_name, "cna_audio_category_copy_name");
    LOAD(audio_category_pause, "cna_audio_category_pause");
    LOAD(audio_category_resume, "cna_audio_category_resume");
    LOAD(audio_category_set_volume, "cna_audio_category_set_volume");
    LOAD(audio_category_stop, "cna_audio_category_stop");
    LOAD(audio_category_equals, "cna_audio_category_equals");
    LOAD(audio_category_get_hash_code, "cna_audio_category_get_hash_code");
    LOAD(wave_bank_create, "cna_wave_bank_create");
    LOAD(wave_bank_create_streaming, "cna_wave_bank_create_streaming");
    LOAD(wave_bank_destroy, "cna_wave_bank_destroy");
    LOAD(wave_bank_get_is_prepared, "cna_wave_bank_get_is_prepared");
    LOAD(wave_bank_get_is_in_use, "cna_wave_bank_get_is_in_use");
    LOAD(sound_bank_create, "cna_sound_bank_create");
    LOAD(sound_bank_destroy, "cna_sound_bank_destroy");
    LOAD(sound_bank_get_is_in_use, "cna_sound_bank_get_is_in_use");
    LOAD(sound_bank_get_cue, "cna_sound_bank_get_cue");
    LOAD(sound_bank_play_cue, "cna_sound_bank_play_cue");
    LOAD(sound_bank_play_cue_3d, "cna_sound_bank_play_cue_3d");
    LOAD(cue_destroy, "cna_cue_destroy");
    LOAD(cue_get_info, "cna_cue_get_info");
    LOAD(cue_apply_3d, "cna_cue_apply_3d");
    LOAD(cue_get_variable, "cna_cue_get_variable");
    LOAD(cue_set_variable, "cna_cue_set_variable");
    LOAD(cue_play, "cna_cue_play");
    LOAD(cue_pause, "cna_cue_pause");
    LOAD(cue_resume, "cna_cue_resume");
    LOAD(cue_stop, "cna_cue_stop");
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
    atomic_init(&context->callbacks_enabled, 1);
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
    METHOD(window_event, "nativeWindowEvent", "(I)V");
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
    for (size_t index = 0U; index < 3U; ++index) {
        wrapper->window_events[index].game = context;
        wrapper->window_events[index].event = (jint)index;
        result = cna.game_window_subscribe(
            wrapper->cna_handle,
            (CNA_GameWindowEvent)index,
            on_window_event,
            &wrapper->window_events[index],
            &wrapper->window_registrations[index]);
        if (result != CNA_RESULT_SUCCESS) {
            atomic_store_explicit(
                &context->callbacks_enabled, 0, memory_order_release);
            for (size_t registered = 0U; registered < index; ++registered) {
                (void)cna.game_unsubscribe(wrapper->window_registrations[registered]);
                wrapper->window_registrations[registered] = CNA_INVALID_HANDLE;
            }
            (void)cna.game_destroy(wrapper->cna_handle);
            goto create_failed_without_class;
        }
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

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeUpdateFrameworkDispatcher(
    JNIEnv* environment, jclass type, jlong game)
{
    (void)environment;
    (void)type;
    return (jint)cna.framework_dispatcher_update(java_game(game)->cna_handle);
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

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGamePadState(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jint player_index,
    jint dead_zone,
    jintArray discrete_output,
    jfloatArray analog_output)
{
    (void)type;
    if (discrete_output == NULL || analog_output == NULL ||
        (*environment)->GetArrayLength(environment, discrete_output) < 3 ||
        (*environment)->GetArrayLength(environment, analog_output) < 6) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }

    CNA_GamePadState state;
    (void)memset(&state, 0, sizeof(state));
    state.struct_size = (uint32_t)sizeof(state);
    state.struct_version = UINT32_C(1);
    CNA_Result result = dead_zone < 0
        ? cna.gamepad_get_state(
            java_game(game)->cna_handle, (CNA_PlayerIndex)player_index, &state)
        : cna.gamepad_get_state_with_dead_zone(
            java_game(game)->cna_handle,
            (CNA_PlayerIndex)player_index,
            (CNA_GamePadDeadZone)dead_zone,
            &state);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }

    const jint discrete[3] = {
        state.is_connected == CNA_TRUE ? 1 : 0,
        (jint)state.packet_number,
        (jint)state.pressed_buttons
    };
    const jfloat analog[6] = {
        state.analog.left_thumb_stick.x,
        state.analog.left_thumb_stick.y,
        state.analog.right_thumb_stick.x,
        state.analog.right_thumb_stick.y,
        state.analog.left_trigger,
        state.analog.right_trigger
    };
    (*environment)->SetIntArrayRegion(environment, discrete_output, 0, 3, discrete);
    if ((*environment)->ExceptionCheck(environment)) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    (*environment)->SetFloatArrayRegion(environment, analog_output, 0, 6, analog);
    return (*environment)->ExceptionCheck(environment) ? (jint)CNA_RESULT_INVALID_STATE : 0;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGamePadCapabilities(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jint player_index,
    jintArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) < 26) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }

    CNA_GamePadCapabilities capabilities;
    (void)memset(&capabilities, 0, sizeof(capabilities));
    capabilities.struct_size = (uint32_t)sizeof(capabilities);
    capabilities.struct_version = UINT32_C(1);
    CNA_Result result = cna.gamepad_get_capabilities(
        java_game(game)->cna_handle, (CNA_PlayerIndex)player_index, &capabilities);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }

#define BOOLEAN_VALUE(field) (capabilities.field == CNA_TRUE ? 1 : 0)
    const jint values[26] = {
        BOOLEAN_VALUE(is_connected),
        (jint)capabilities.gamepad_type,
        BOOLEAN_VALUE(has_a_button),
        BOOLEAN_VALUE(has_b_button),
        BOOLEAN_VALUE(has_back_button),
        BOOLEAN_VALUE(has_big_button),
        BOOLEAN_VALUE(has_dpad_down_button),
        BOOLEAN_VALUE(has_dpad_left_button),
        BOOLEAN_VALUE(has_dpad_right_button),
        BOOLEAN_VALUE(has_dpad_up_button),
        BOOLEAN_VALUE(has_left_shoulder_button),
        BOOLEAN_VALUE(has_left_stick_button),
        BOOLEAN_VALUE(has_left_trigger),
        BOOLEAN_VALUE(has_left_vibration_motor),
        BOOLEAN_VALUE(has_left_x_thumb_stick),
        BOOLEAN_VALUE(has_left_y_thumb_stick),
        BOOLEAN_VALUE(has_right_shoulder_button),
        BOOLEAN_VALUE(has_right_stick_button),
        BOOLEAN_VALUE(has_right_trigger),
        BOOLEAN_VALUE(has_right_vibration_motor),
        BOOLEAN_VALUE(has_right_x_thumb_stick),
        BOOLEAN_VALUE(has_right_y_thumb_stick),
        BOOLEAN_VALUE(has_start_button),
        BOOLEAN_VALUE(has_voice_support),
        BOOLEAN_VALUE(has_x_button),
        BOOLEAN_VALUE(has_y_button)
    };
#undef BOOLEAN_VALUE
    (*environment)->SetIntArrayRegion(environment, output, 0, 26, values);
    return (*environment)->ExceptionCheck(environment) ? (jint)CNA_RESULT_INVALID_STATE : 0;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetGamePadVibration(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jint player_index,
    jfloat left_motor,
    jfloat right_motor)
{
    (void)environment;
    (void)type;
    CNA_Bool applied = CNA_FALSE;
    CNA_Result result = cna.gamepad_set_vibration(
        java_game(game)->cna_handle,
        (CNA_PlayerIndex)player_index,
        (float)left_motor,
        (float)right_motor,
        &applied);
    return result == CNA_RESULT_SUCCESS ? (applied == CNA_TRUE ? 1 : 0) : -(jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetTouchCapabilities(
    JNIEnv* environment, jclass type, jlong game, jintArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) < 2) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_TouchCapabilities capabilities;
    (void)memset(&capabilities, 0, sizeof(capabilities));
    capabilities.struct_size = (uint32_t)sizeof(capabilities);
    capabilities.struct_version = UINT32_C(1);
    CNA_Result result = cna.touch_get_capabilities(
        java_game(game)->cna_handle, &capabilities);
    if (result != CNA_RESULT_SUCCESS ||
        capabilities.maximum_touch_count > (uint32_t)INT32_MAX) {
        return (jint)(result == CNA_RESULT_SUCCESS
            ? CNA_RESULT_INVALID_STATE : result);
    }
    const jint values[2] = {
        capabilities.is_connected == CNA_TRUE ? 1 : 0,
        (jint)capabilities.maximum_touch_count
    };
    (*environment)->SetIntArrayRegion(environment, output, 0, 2, values);
    return (*environment)->ExceptionCheck(environment)
        ? (jint)CNA_RESULT_INVALID_STATE : (jint)CNA_RESULT_SUCCESS;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetTouchState(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jintArray discrete_output,
    jfloatArray position_output)
{
    (void)type;
    if (discrete_output == NULL || position_output == NULL ||
        (*environment)->GetArrayLength(environment, discrete_output) < 26 ||
        (*environment)->GetArrayLength(environment, position_output) < 32) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_TouchState state;
    (void)memset(&state, 0, sizeof(state));
    state.struct_size = (uint32_t)sizeof(state);
    state.struct_version = UINT32_C(1);
    CNA_Result result = cna.touch_get_state(java_game(game)->cna_handle, &state);
    if (result != CNA_RESULT_SUCCESS || state.touch_count > CNA_TOUCH_MAX_TOUCHES) {
        return (jint)(result == CNA_RESULT_SUCCESS
            ? CNA_RESULT_INVALID_STATE : result);
    }
    jint discrete[26] = {0};
    jfloat positions[32] = {0.0F};
    discrete[0] = state.is_connected == CNA_TRUE ? 1 : 0;
    discrete[1] = (jint)state.touch_count;
    for (uint32_t index = 0U; index < state.touch_count; ++index) {
        const CNA_TouchLocation* location = &state.touches[index];
        const size_t integer = 2U + (size_t)index * 3U;
        const size_t vector = (size_t)index * 4U;
        discrete[integer] = (jint)location->id;
        discrete[integer + 1U] = (jint)location->state;
        discrete[integer + 2U] = (jint)location->previous_state;
        positions[vector] = location->position.x;
        positions[vector + 1U] = location->position.y;
        positions[vector + 2U] = location->previous_position.x;
        positions[vector + 3U] = location->previous_position.y;
    }
    (*environment)->SetIntArrayRegion(environment, discrete_output, 0, 26, discrete);
    if ((*environment)->ExceptionCheck(environment)) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    (*environment)->SetFloatArrayRegion(environment, position_output, 0, 32, positions);
    return (*environment)->ExceptionCheck(environment)
        ? (jint)CNA_RESULT_INVALID_STATE : (jint)CNA_RESULT_SUCCESS;
}

static CNA_Result set_int_output(JNIEnv* environment, jintArray output, int32_t value)
{
    if (output == NULL || (*environment)->GetArrayLength(environment, output) < 1) {
        return CNA_RESULT_INVALID_ARGUMENT;
    }
    const jint projected = (jint)value;
    (*environment)->SetIntArrayRegion(environment, output, 0, 1, &projected);
    return (*environment)->ExceptionCheck(environment)
        ? CNA_RESULT_INVALID_STATE : CNA_RESULT_SUCCESS;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeTouchPanelGetDisplayWidth(
    JNIEnv* environment, jclass type, jlong game, jintArray output)
{
    (void)type;
    int32_t value = 0;
    CNA_Result result = cna.touch_panel_get_display_width(
        java_game(game)->cna_handle, &value);
    return (jint)(result == CNA_RESULT_SUCCESS
        ? set_int_output(environment, output, value) : result);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeTouchPanelSetDisplayWidth(
    JNIEnv* environment, jclass type, jlong game, jint value)
{
    (void)environment;
    (void)type;
    return (jint)cna.touch_panel_set_display_width(
        java_game(game)->cna_handle, (int32_t)value);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeTouchPanelGetDisplayHeight(
    JNIEnv* environment, jclass type, jlong game, jintArray output)
{
    (void)type;
    int32_t value = 0;
    CNA_Result result = cna.touch_panel_get_display_height(
        java_game(game)->cna_handle, &value);
    return (jint)(result == CNA_RESULT_SUCCESS
        ? set_int_output(environment, output, value) : result);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeTouchPanelSetDisplayHeight(
    JNIEnv* environment, jclass type, jlong game, jint value)
{
    (void)environment;
    (void)type;
    return (jint)cna.touch_panel_set_display_height(
        java_game(game)->cna_handle, (int32_t)value);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeTouchPanelGetDisplayOrientation(
    JNIEnv* environment, jclass type, jlong game, jintArray output)
{
    (void)type;
    CNA_DisplayOrientation value = CNA_DISPLAY_ORIENTATION_DEFAULT;
    CNA_Result result = cna.touch_panel_get_display_orientation(
        java_game(game)->cna_handle, &value);
    return (jint)(result == CNA_RESULT_SUCCESS
        ? set_int_output(environment, output, (int32_t)value) : result);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeTouchPanelSetDisplayOrientation(
    JNIEnv* environment, jclass type, jlong game, jint value)
{
    (void)environment;
    (void)type;
    return (jint)cna.touch_panel_set_display_orientation(
        java_game(game)->cna_handle, (CNA_DisplayOrientation)(uint32_t)value);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeTouchPanelGetEnabledGestures(
    JNIEnv* environment, jclass type, jlong game, jintArray output)
{
    (void)type;
    CNA_GestureType value = CNA_GESTURE_TYPE_NONE;
    CNA_Result result = cna.touch_panel_get_enabled_gestures(
        java_game(game)->cna_handle, &value);
    return (jint)(result == CNA_RESULT_SUCCESS
        ? set_int_output(environment, output, (int32_t)value) : result);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeTouchPanelSetEnabledGestures(
    JNIEnv* environment, jclass type, jlong game, jint value)
{
    (void)environment;
    (void)type;
    return (jint)cna.touch_panel_set_enabled_gestures(
        java_game(game)->cna_handle, (CNA_GestureType)(uint32_t)value);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeTouchPanelGetIsGestureAvailable(
    JNIEnv* environment, jclass type, jlong game)
{
    (void)environment;
    (void)type;
    CNA_Bool value = CNA_FALSE;
    CNA_Result result = cna.touch_panel_get_is_gesture_available(
        java_game(game)->cna_handle, &value);
    return result == CNA_RESULT_SUCCESS
        ? (value == CNA_TRUE ? 1 : 0) : -(jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeTouchPanelGetWindowHandle(
    JNIEnv* environment, jclass type, jlong game, jlongArray output)
{
    (void)type;
    uint64_t value = 0U;
    CNA_Result result = cna.touch_panel_get_window_handle(
        java_game(game)->cna_handle, &value);
    return (jint)(result == CNA_RESULT_SUCCESS
        ? set_handle_output(environment, output, (CNA_Handle)value) : result);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeTouchPanelSetWindowHandle(
    JNIEnv* environment, jclass type, jlong game, jlong value)
{
    (void)environment;
    (void)type;
    return (jint)cna.touch_panel_set_window_handle(
        java_game(game)->cna_handle, (uint64_t)value);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeReadTouchGesture(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jintArray type_output,
    jlongArray timestamp_output,
    jfloatArray vector_output)
{
    (void)type;
    if (type_output == NULL || timestamp_output == NULL || vector_output == NULL ||
        (*environment)->GetArrayLength(environment, type_output) < 1 ||
        (*environment)->GetArrayLength(environment, timestamp_output) < 1 ||
        (*environment)->GetArrayLength(environment, vector_output) < 8) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_GestureSample sample;
    (void)memset(&sample, 0, sizeof(sample));
    sample.struct_size = (uint32_t)sizeof(sample);
    sample.struct_version = UINT32_C(1);
    CNA_Result result = cna.touch_panel_read_gesture(
        java_game(game)->cna_handle, &sample);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    const jint gesture_type = (jint)sample.gesture_type;
    const jlong timestamp = (jlong)sample.timestamp_ticks;
    const jfloat vectors[8] = {
        sample.position.x, sample.position.y,
        sample.position2.x, sample.position2.y,
        sample.delta.x, sample.delta.y,
        sample.delta2.x, sample.delta2.y
    };
    (*environment)->SetIntArrayRegion(environment, type_output, 0, 1, &gesture_type);
    if ((*environment)->ExceptionCheck(environment)) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    (*environment)->SetLongArrayRegion(environment, timestamp_output, 0, 1, &timestamp);
    if ((*environment)->ExceptionCheck(environment)) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    (*environment)->SetFloatArrayRegion(environment, vector_output, 0, 8, vectors);
    return (*environment)->ExceptionCheck(environment)
        ? (jint)CNA_RESULT_INVALID_STATE : (jint)CNA_RESULT_SUCCESS;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetTouchDeviceExists(
    JNIEnv* environment, jclass type, jlong game, jboolean value)
{
    (void)environment;
    (void)type;
    return (jint)cna.touch_panel_set_touch_device_exists_ext(
        java_game(game)->cna_handle, value == JNI_TRUE ? CNA_TRUE : CNA_FALSE);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetTouchFinger(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jint index,
    jint finger_id,
    jfloat x,
    jfloat y)
{
    (void)environment;
    (void)type;
    return (jint)cna.touch_panel_set_finger_ext(
        java_game(game)->cna_handle,
        (int32_t)index, (int32_t)finger_id, (CNA_Vector2){x, y});
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeRaiseTouchEvent(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jint finger_id,
    jint state,
    jfloat x,
    jfloat y,
    jfloat delta_x,
    jfloat delta_y)
{
    (void)environment;
    (void)type;
    return (jint)cna.touch_panel_raise_touch_event_ext(
        java_game(game)->cna_handle,
        (int32_t)finger_id, (CNA_TouchLocationState)(uint32_t)state,
        x, y, delta_x, delta_y);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeEnqueueTouchGesture(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jint gesture_type,
    jlong timestamp_ticks,
    jfloatArray vector_values)
{
    (void)type;
    if (vector_values == NULL ||
        (*environment)->GetArrayLength(environment, vector_values) < 8) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    jfloat vectors[8];
    (*environment)->GetFloatArrayRegion(environment, vector_values, 0, 8, vectors);
    if ((*environment)->ExceptionCheck(environment)) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    CNA_GestureSample sample;
    (void)memset(&sample, 0, sizeof(sample));
    sample.struct_size = (uint32_t)sizeof(sample);
    sample.struct_version = UINT32_C(1);
    sample.gesture_type = (CNA_GestureType)(uint32_t)gesture_type;
    sample.finger_id_ext = CNA_TOUCH_NO_FINGER;
    sample.finger_id2_ext = CNA_TOUCH_NO_FINGER;
    sample.timestamp_ticks = (int64_t)timestamp_ticks;
    sample.position = (CNA_Vector2){vectors[0], vectors[1]};
    sample.position2 = (CNA_Vector2){vectors[2], vectors[3]};
    sample.delta = (CNA_Vector2){vectors[4], vectors[5]};
    sample.delta2 = (CNA_Vector2){vectors[6], vectors[7]};
    return (jint)cna.touch_panel_enqueue_gesture_ext(
        java_game(game)->cna_handle, &sample);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeUpdateTouchPanel(
    JNIEnv* environment, jclass type, jlong game)
{
    (void)environment;
    (void)type;
    return (jint)cna.touch_panel_update_ext(java_game(game)->cna_handle);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeResetTouchPanel(
    JNIEnv* environment, jclass type, jlong game)
{
    (void)environment;
    (void)type;
    return (jint)cna.touch_panel_reset_for_tests_ext(java_game(game)->cna_handle);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeCreateGraphicsDeviceManager(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jobject manager,
    jlongArray output)
{
    (void)type;
    if (manager == NULL || output == NULL ||
        (*environment)->GetArrayLength(environment, output) < 1) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    JavaGraphicsDeviceManager* wrapper =
        (JavaGraphicsDeviceManager*)calloc(1U, sizeof(JavaGraphicsDeviceManager));
    JavaGraphicsDeviceManagerContext* context =
        (JavaGraphicsDeviceManagerContext*)calloc(
            1U, sizeof(JavaGraphicsDeviceManagerContext));
    if (wrapper == NULL || context == NULL) {
        free(wrapper);
        free(context);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    wrapper->context = context;
    atomic_init(&context->callbacks_enabled, 1);
    context->manager = (*environment)->NewGlobalRef(environment, manager);
    if (context->manager == NULL) {
        free(context);
        free(wrapper);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }

    jclass manager_class = (*environment)->GetObjectClass(environment, manager);
    if (manager_class == NULL) {
        (*environment)->DeleteGlobalRef(environment, context->manager);
        free(context);
        free(wrapper);
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    context->event = (*environment)->GetMethodID(
        environment, manager_class, "nativeGraphicsDeviceManagerEvent", "(I)V");
    context->preparing_device_settings = (*environment)->GetMethodID(
        environment, manager_class, "nativePreparingDeviceSettings", "([I)[I");
    (*environment)->DeleteLocalRef(environment, manager_class);
    if (context->event == NULL || context->preparing_device_settings == NULL) {
        (*environment)->DeleteGlobalRef(environment, context->manager);
        free(context);
        free(wrapper);
        return (jint)CNA_RESULT_INVALID_STATE;
    }

    CNA_Result result = cna.graphics_device_manager_create(
        java_game(game)->cna_handle, &wrapper->cna_handle);
    if (result != CNA_RESULT_SUCCESS) {
        (*environment)->DeleteGlobalRef(environment, context->manager);
        free(context);
        free(wrapper);
        return (jint)result;
    }
    for (size_t index = 0U; index < 5U; ++index) {
        wrapper->events[index].manager = context;
        wrapper->events[index].event = (jint)index;
        result = cna.graphics_device_manager_subscribe(
            wrapper->cna_handle,
            (CNA_GraphicsDeviceManagerEvent)index,
            on_graphics_device_manager_event,
            &wrapper->events[index],
            &wrapper->registrations[index]);
        if (result != CNA_RESULT_SUCCESS) {
            goto manager_create_failed;
        }
    }
    result = cna.graphics_device_manager_subscribe_preparing_device_settings_ext(
        wrapper->cna_handle,
        on_graphics_device_manager_preparing_device_settings,
        context,
        &wrapper->preparing_registration);
    if (result != CNA_RESULT_SUCCESS) {
        goto manager_create_failed;
    }
    result = set_handle_output(
        environment, output, (CNA_Handle)(uintptr_t)wrapper);
    if (result != CNA_RESULT_SUCCESS) {
        goto manager_create_failed;
    }
    return 0;

manager_create_failed:
    atomic_store_explicit(&context->callbacks_enabled, 0, memory_order_release);
    if (wrapper->preparing_registration != CNA_INVALID_HANDLE) {
        (void)cna.game_unsubscribe(wrapper->preparing_registration);
    }
    for (size_t index = 0U; index < 5U; ++index) {
        if (wrapper->registrations[index] != CNA_INVALID_HANDLE) {
            (void)cna.game_unsubscribe(wrapper->registrations[index]);
        }
    }
    (void)cna.graphics_device_manager_destroy(wrapper->cna_handle);
    (*environment)->DeleteGlobalRef(environment, context->manager);
    free(context);
    free(wrapper);
    return (jint)result;
}

JNIEXPORT jlong JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsDeviceManagerGraphicsProfile(
    JNIEnv* environment, jclass type, jlong manager)
{
    (void)environment;
    (void)type;
    uint32_t value = 0U;
    CNA_Result result = cna.graphics_device_manager_get_graphics_profile(
        java_graphics_device_manager(manager)->cna_handle, &value);
    return uint32_result(result, value);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetGraphicsDeviceManagerGraphicsProfile(
    JNIEnv* environment, jclass type, jlong manager, jint value)
{
    (void)environment;
    (void)type;
    return (jint)cna.graphics_device_manager_set_graphics_profile(
        java_graphics_device_manager(manager)->cna_handle, (uint32_t)value);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsDeviceManagerIsFullScreen(
    JNIEnv* environment, jclass type, jlong manager)
{
    (void)environment;
    (void)type;
    CNA_Bool value = CNA_FALSE;
    CNA_Result result = cna.graphics_device_manager_get_is_full_screen(
        java_graphics_device_manager(manager)->cna_handle, &value);
    return bool_result(result, value);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetGraphicsDeviceManagerIsFullScreen(
    JNIEnv* environment, jclass type, jlong manager, jboolean value)
{
    (void)environment;
    (void)type;
    return (jint)cna.graphics_device_manager_set_is_full_screen(
        java_graphics_device_manager(manager)->cna_handle,
        value == JNI_TRUE ? CNA_TRUE : CNA_FALSE);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsDeviceManagerPreferMultiSampling(
    JNIEnv* environment, jclass type, jlong manager)
{
    (void)environment;
    (void)type;
    CNA_Bool value = CNA_FALSE;
    CNA_Result result = cna.graphics_device_manager_get_prefer_multi_sampling(
        java_graphics_device_manager(manager)->cna_handle, &value);
    return bool_result(result, value);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetGraphicsDeviceManagerPreferMultiSampling(
    JNIEnv* environment, jclass type, jlong manager, jboolean value)
{
    (void)environment;
    (void)type;
    return (jint)cna.graphics_device_manager_set_prefer_multi_sampling(
        java_graphics_device_manager(manager)->cna_handle,
        value == JNI_TRUE ? CNA_TRUE : CNA_FALSE);
}

JNIEXPORT jlong JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsDeviceManagerPreferredBackBufferFormat(
    JNIEnv* environment, jclass type, jlong manager)
{
    (void)environment;
    (void)type;
    uint32_t value = 0U;
    CNA_Result result = cna.graphics_device_manager_get_preferred_back_buffer_format(
        java_graphics_device_manager(manager)->cna_handle, &value);
    return uint32_result(result, value);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetGraphicsDeviceManagerPreferredBackBufferFormat(
    JNIEnv* environment, jclass type, jlong manager, jint value)
{
    (void)environment;
    (void)type;
    return (jint)cna.graphics_device_manager_set_preferred_back_buffer_format(
        java_graphics_device_manager(manager)->cna_handle, (uint32_t)value);
}

JNIEXPORT jlong JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsDeviceManagerPreferredBackBufferWidth(
    JNIEnv* environment, jclass type, jlong manager)
{
    (void)environment;
    (void)type;
    int32_t value = 0;
    CNA_Result result = cna.graphics_device_manager_get_preferred_back_buffer_width(
        java_graphics_device_manager(manager)->cna_handle, &value);
    return int32_result(result, value);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetGraphicsDeviceManagerPreferredBackBufferWidth(
    JNIEnv* environment, jclass type, jlong manager, jint value)
{
    (void)environment;
    (void)type;
    return (jint)cna.graphics_device_manager_set_preferred_back_buffer_width(
        java_graphics_device_manager(manager)->cna_handle, (int32_t)value);
}

JNIEXPORT jlong JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsDeviceManagerPreferredBackBufferHeight(
    JNIEnv* environment, jclass type, jlong manager)
{
    (void)environment;
    (void)type;
    int32_t value = 0;
    CNA_Result result = cna.graphics_device_manager_get_preferred_back_buffer_height(
        java_graphics_device_manager(manager)->cna_handle, &value);
    return int32_result(result, value);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetGraphicsDeviceManagerPreferredBackBufferHeight(
    JNIEnv* environment, jclass type, jlong manager, jint value)
{
    (void)environment;
    (void)type;
    return (jint)cna.graphics_device_manager_set_preferred_back_buffer_height(
        java_graphics_device_manager(manager)->cna_handle, (int32_t)value);
}

JNIEXPORT jlong JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsDeviceManagerPreferredDepthStencilFormat(
    JNIEnv* environment, jclass type, jlong manager)
{
    (void)environment;
    (void)type;
    uint32_t value = 0U;
    CNA_Result result = cna.graphics_device_manager_get_preferred_depth_stencil_format(
        java_graphics_device_manager(manager)->cna_handle, &value);
    return uint32_result(result, value);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetGraphicsDeviceManagerPreferredDepthStencilFormat(
    JNIEnv* environment, jclass type, jlong manager, jint value)
{
    (void)environment;
    (void)type;
    return (jint)cna.graphics_device_manager_set_preferred_depth_stencil_format(
        java_graphics_device_manager(manager)->cna_handle, (uint32_t)value);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsDeviceManagerSynchronizeWithVerticalRetrace(
    JNIEnv* environment, jclass type, jlong manager)
{
    (void)environment;
    (void)type;
    CNA_Bool value = CNA_FALSE;
    CNA_Result result =
        cna.graphics_device_manager_get_synchronize_with_vertical_retrace(
            java_graphics_device_manager(manager)->cna_handle, &value);
    return bool_result(result, value);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetGraphicsDeviceManagerSynchronizeWithVerticalRetrace(
    JNIEnv* environment, jclass type, jlong manager, jboolean value)
{
    (void)environment;
    (void)type;
    return (jint)cna.graphics_device_manager_set_synchronize_with_vertical_retrace(
        java_graphics_device_manager(manager)->cna_handle,
        value == JNI_TRUE ? CNA_TRUE : CNA_FALSE);
}

JNIEXPORT jlong JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsDeviceManagerSupportedOrientations(
    JNIEnv* environment, jclass type, jlong manager)
{
    (void)environment;
    (void)type;
    uint32_t value = 0U;
    CNA_Result result = cna.graphics_device_manager_get_supported_orientations(
        java_graphics_device_manager(manager)->cna_handle, &value);
    return uint32_result(result, value);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetGraphicsDeviceManagerSupportedOrientations(
    JNIEnv* environment, jclass type, jlong manager, jint value)
{
    (void)environment;
    (void)type;
    return (jint)cna.graphics_device_manager_set_supported_orientations(
        java_graphics_device_manager(manager)->cna_handle, (uint32_t)value);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeApplyGraphicsDeviceManagerChanges(
    JNIEnv* environment, jclass type, jlong manager)
{
    (void)environment;
    (void)type;
    return (jint)cna.graphics_device_manager_apply_changes(
        java_graphics_device_manager(manager)->cna_handle);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeToggleGraphicsDeviceManagerFullScreen(
    JNIEnv* environment, jclass type, jlong manager)
{
    (void)environment;
    (void)type;
    return (jint)cna.graphics_device_manager_toggle_full_screen(
        java_graphics_device_manager(manager)->cna_handle);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeCreateGraphicsDeviceManagerDevice(
    JNIEnv* environment, jclass type, jlong manager)
{
    (void)environment;
    (void)type;
    return (jint)cna.graphics_device_manager_create_device(
        java_graphics_device_manager(manager)->cna_handle);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeBeginGraphicsDeviceManagerDraw(
    JNIEnv* environment, jclass type, jlong manager)
{
    (void)environment;
    (void)type;
    CNA_Bool value = CNA_FALSE;
    CNA_Result result = cna.graphics_device_manager_begin_draw(
        java_graphics_device_manager(manager)->cna_handle, &value);
    return bool_result(result, value);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeEndGraphicsDeviceManagerDraw(
    JNIEnv* environment, jclass type, jlong manager)
{
    (void)environment;
    (void)type;
    return (jint)cna.graphics_device_manager_end_draw(
        java_graphics_device_manager(manager)->cna_handle);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeDisposeGraphicsDeviceManager(
    JNIEnv* environment, jclass type, jlong manager)
{
    (void)environment;
    (void)type;
    return (jint)cna.graphics_device_manager_dispose(
        java_graphics_device_manager(manager)->cna_handle);
}

JNIEXPORT jlong JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsAdapterCount(
    JNIEnv* environment, jclass type, jlong game)
{
    (void)environment;
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    if (result != CNA_RESULT_SUCCESS) {
        return -(jlong)result;
    }
    uint64_t count = 0U;
    result = cna.graphics_adapter_get_count(device, &count);
    if (result != CNA_RESULT_SUCCESS) {
        return -(jlong)result;
    }
    return count > (uint64_t)INT64_MAX ? -(jlong)CNA_RESULT_OVERFLOW : (jlong)count;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsAdapterInfo(
    JNIEnv* environment, jclass type, jlong game, jint adapter_index, jlongArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) < 10) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    CNA_GraphicsAdapterInfo info;
    (void)memset(&info, 0, sizeof(info));
    info.struct_size = (uint32_t)sizeof(info);
    info.struct_version = UINT32_C(1);
    result = cna.graphics_adapter_get_info(device, (uint32_t)adapter_index, &info);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    if (info.description_byte_length > (uint64_t)INT64_MAX ||
        info.device_name_byte_length > (uint64_t)INT64_MAX) {
        return (jint)CNA_RESULT_OVERFLOW;
    }
    const jlong values[10] = {
        info.is_default_adapter == CNA_TRUE ? 1 : 0,
        info.is_wide_screen == CNA_TRUE ? 1 : 0,
        info.use_null_device == CNA_TRUE ? 1 : 0,
        info.use_reference_device == CNA_TRUE ? 1 : 0,
        (jlong)info.vendor_id,
        (jlong)info.device_id,
        (jlong)info.revision,
        (jlong)info.subsystem_id,
        (jlong)info.description_byte_length,
        (jlong)info.device_name_byte_length
    };
    (*environment)->SetLongArrayRegion(environment, output, 0, 10, values);
    return (*environment)->ExceptionCheck(environment)
        ? (jint)CNA_RESULT_INVALID_STATE : 0;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeCopyGraphicsAdapterString(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jint adapter_index,
    jboolean description,
    jbyteArray output)
{
    (void)type;
    if (output == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    jsize capacity = (*environment)->GetArrayLength(environment, output);
    jbyte* bytes = capacity == 0
        ? NULL : (*environment)->GetByteArrayElements(environment, output, NULL);
    if (capacity != 0 && bytes == NULL) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    uint64_t written = 0U;
    GraphicsAdapterCopyStringFunction copy = description == JNI_TRUE
        ? cna.graphics_adapter_copy_description
        : cna.graphics_adapter_copy_device_name;
    result = copy(
        device, (uint32_t)adapter_index, (char*)bytes,
        (uint64_t)capacity, &written);
    if (bytes != NULL) {
        (*environment)->ReleaseByteArrayElements(environment, output, bytes, 0);
    }
    if (result == CNA_RESULT_SUCCESS && written != (uint64_t)capacity) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsAdapterCurrentDisplayMode(
    JNIEnv* environment, jclass type, jlong game, jint adapter_index, jintArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) < 4) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    CNA_DisplayMode mode;
    (void)memset(&mode, 0, sizeof(mode));
    mode.struct_size = (uint32_t)sizeof(mode);
    mode.struct_version = UINT32_C(1);
    result = cna.graphics_adapter_get_current_display_mode(
        device, (uint32_t)adapter_index, &mode);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    uint32_t aspect_bits = 0U;
    (void)memcpy(&aspect_bits, &mode.aspect_ratio, sizeof(aspect_bits));
    const jint values[4] = {
        (jint)mode.width,
        (jint)mode.height,
        (jint)aspect_bits,
        (jint)mode.format
    };
    (*environment)->SetIntArrayRegion(environment, output, 0, 4, values);
    return (*environment)->ExceptionCheck(environment)
        ? (jint)CNA_RESULT_INVALID_STATE : 0;
}

JNIEXPORT jlong JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsAdapterDisplayModeCount(
    JNIEnv* environment, jclass type, jlong game, jint adapter_index)
{
    (void)environment;
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    if (result != CNA_RESULT_SUCCESS) {
        return -(jlong)result;
    }
    uint64_t count = 0U;
    result = cna.graphics_adapter_get_display_mode_count(
        device, (uint32_t)adapter_index, CNA_FALSE, CNA_SURFACE_FORMAT_COLOR, &count);
    if (result != CNA_RESULT_SUCCESS) {
        return -(jlong)result;
    }
    return count > (uint64_t)INT64_MAX ? -(jlong)CNA_RESULT_OVERFLOW : (jlong)count;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeCopyGraphicsAdapterDisplayModes(
    JNIEnv* environment, jclass type, jlong game, jint adapter_index, jintArray output)
{
    (void)type;
    if (output == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    jsize packed_count = (*environment)->GetArrayLength(environment, output);
    if (packed_count % 4 != 0) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    uint64_t capacity = (uint64_t)(packed_count / 4);
    CNA_DisplayMode* modes = capacity == 0U
        ? NULL : (CNA_DisplayMode*)calloc((size_t)capacity, sizeof(CNA_DisplayMode));
    if (capacity != 0U && modes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    for (uint64_t index = 0U; index < capacity; ++index) {
        modes[index].struct_size = (uint32_t)sizeof(CNA_DisplayMode);
        modes[index].struct_version = UINT32_C(1);
    }
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    uint64_t count = 0U;
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.graphics_adapter_copy_display_modes(
            device, (uint32_t)adapter_index, CNA_FALSE, CNA_SURFACE_FORMAT_COLOR,
            modes, capacity, &count);
    }
    if (result == CNA_RESULT_SUCCESS && count == capacity) {
        jint* packed = packed_count == 0
            ? NULL : (jint*)malloc((size_t)packed_count * sizeof(jint));
        if (packed_count != 0 && packed == NULL) {
            result = CNA_RESULT_OUT_OF_MEMORY;
        } else {
            for (uint64_t index = 0U; index < capacity; ++index) {
                uint32_t aspect_bits = 0U;
                (void)memcpy(&aspect_bits, &modes[index].aspect_ratio, sizeof(aspect_bits));
                packed[index * 4U] = (jint)modes[index].width;
                packed[index * 4U + 1U] = (jint)modes[index].height;
                packed[index * 4U + 2U] = (jint)aspect_bits;
                packed[index * 4U + 3U] = (jint)modes[index].format;
            }
            if (packed_count != 0) {
                (*environment)->SetIntArrayRegion(
                    environment, output, 0, packed_count, packed);
                if ((*environment)->ExceptionCheck(environment)) {
                    result = CNA_RESULT_INVALID_STATE;
                }
            }
            free(packed);
        }
    } else if (result == CNA_RESULT_SUCCESS) {
        result = CNA_RESULT_INVALID_STATE;
    }
    free(modes);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetGraphicsAdapterDevicePreferences(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jint adapter_index,
    jboolean use_null_device,
    jboolean use_reference_device)
{
    (void)environment;
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    return (jint)cna.graphics_adapter_set_device_preferences(
        device, (uint32_t)adapter_index,
        use_null_device == JNI_TRUE ? CNA_TRUE : CNA_FALSE,
        use_reference_device == JNI_TRUE ? CNA_TRUE : CNA_FALSE);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeIsGraphicsAdapterProfileSupported(
    JNIEnv* environment, jclass type, jlong game, jint adapter_index, jint profile)
{
    (void)environment;
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    if (result != CNA_RESULT_SUCCESS) {
        return -(jint)result;
    }
    CNA_Bool supported = CNA_FALSE;
    result = cna.graphics_adapter_is_profile_supported(
        device, (uint32_t)adapter_index, (CNA_GraphicsProfile)(uint32_t)profile,
        &supported);
    return bool_result(result, supported);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeQueryGraphicsAdapterFormat(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jint adapter_index,
    jboolean back_buffer,
    jint profile,
    jint format,
    jint depth_format,
    jint multi_sample_count,
    jintArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) < 4) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    CNA_GraphicsFormatSelection selection;
    (void)memset(&selection, 0, sizeof(selection));
    selection.struct_size = (uint32_t)sizeof(selection);
    selection.struct_version = UINT32_C(1);
    GraphicsAdapterQueryFormatFunction query = back_buffer == JNI_TRUE
        ? cna.graphics_adapter_query_backbuffer_format
        : cna.graphics_adapter_query_render_target_format;
    result = query(
        device, (uint32_t)adapter_index,
        (CNA_GraphicsProfile)(uint32_t)profile,
        (CNA_SurfaceFormat)(uint32_t)format,
        (CNA_DepthFormat)(uint32_t)depth_format,
        (int32_t)multi_sample_count,
        &selection);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    const jint values[4] = {
        selection.exact_match == CNA_TRUE ? 1 : 0,
        (jint)selection.format,
        (jint)selection.depth_format,
        (jint)selection.multi_sample_count
    };
    (*environment)->SetIntArrayRegion(environment, output, 0, 4, values);
    return (*environment)->ExceptionCheck(environment)
        ? (jint)CNA_RESULT_INVALID_STATE : 0;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsAdapterMonitorHandle(
    JNIEnv* environment, jclass type, jlong game, jint adapter_index, jlongArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) < 1) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    CNA_NativeHandleValue value = 0U;
    result = cna.graphics_adapter_get_native_monitor_handle(
        device, (uint32_t)adapter_index, &value);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    jlong projected;
    (void)memcpy(&projected, &value, sizeof(projected));
    (*environment)->SetLongArrayRegion(environment, output, 0, 1, &projected);
    return (*environment)->ExceptionCheck(environment)
        ? (jint)CNA_RESULT_INVALID_STATE : 0;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeEnsureGraphicsDeviceEvents(
    JNIEnv* environment, jclass type, jlong game, jobject graphics_device)
{
    (void)type;
    if (graphics_device == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    JavaGame* wrapper = java_game(game);
    JavaGameContext* context = wrapper->context;
    if (context->graphics_device != NULL) {
        return (*environment)->IsSameObject(
            environment, context->graphics_device, graphics_device) == JNI_TRUE
            ? 0 : (jint)CNA_RESULT_INVALID_STATE;
    }

    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }

    jobject global_device = (*environment)->NewGlobalRef(environment, graphics_device);
    if (global_device == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    jclass device_class = (*environment)->GetObjectClass(environment, graphics_device);
    if (device_class == NULL) {
        (*environment)->DeleteGlobalRef(environment, global_device);
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    jmethodID event_method = (*environment)->GetMethodID(
        environment, device_class, "nativeGraphicsDeviceEvent", "(IZ[BZ)V");
    (*environment)->DeleteLocalRef(environment, device_class);
    if (event_method == NULL) {
        (*environment)->DeleteGlobalRef(environment, global_device);
        return (jint)CNA_RESULT_INVALID_STATE;
    }

    context->graphics_device = global_device;
    context->graphics_device_event = event_method;
    size_t registered = 0U;
    for (size_t index = 0U; index < 4U; ++index) {
        wrapper->graphics_device_events[index].game = context;
        wrapper->graphics_device_events[index].event = (jint)index;
        result = cna.graphics_device_subscribe_event(
            device,
            (CNA_GraphicsDeviceEvent)index,
            on_graphics_device_event,
            &wrapper->graphics_device_events[index],
            &wrapper->graphics_device_registrations[index]);
        if (result != CNA_RESULT_SUCCESS) {
            goto subscribe_failed;
        }
        ++registered;
    }
    result = cna.graphics_device_subscribe_resource_created(
        device,
        on_graphics_device_resource_created,
        context,
        &wrapper->graphics_device_registrations[4]);
    if (result != CNA_RESULT_SUCCESS) {
        goto subscribe_failed;
    }
    ++registered;
    result = cna.graphics_device_subscribe_resource_destroyed(
        device,
        on_graphics_device_resource_destroyed,
        context,
        &wrapper->graphics_device_registrations[5]);
    if (result != CNA_RESULT_SUCCESS) {
        goto subscribe_failed;
    }
    return 0;

subscribe_failed:
    for (size_t index = 0U; index < registered; ++index) {
        (void)cna.graphics_device_unsubscribe(
            wrapper->graphics_device_registrations[index]);
        wrapper->graphics_device_registrations[index] = CNA_INVALID_HANDLE;
    }
    context->graphics_device = NULL;
    context->graphics_device_event = NULL;
    (*environment)->DeleteGlobalRef(environment, global_device);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsDeviceIsDisposed(
    JNIEnv* environment, jclass type, jlong game)
{
    (void)environment;
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    CNA_Bool value = CNA_FALSE;
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.graphics_device_get_is_disposed(device, &value);
    }
    return bool_result(result, value);
}

JNIEXPORT jlong JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsDeviceStatus(
    JNIEnv* environment, jclass type, jlong game)
{
    (void)environment;
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    uint32_t value = 0U;
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.graphics_device_get_status(device, &value);
    }
    return uint32_result(result, value);
}

JNIEXPORT jlong JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsDeviceAdapterIndex(
    JNIEnv* environment, jclass type, jlong game)
{
    (void)environment;
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    uint32_t value = 0U;
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.graphics_device_get_adapter_index(device, &value);
    }
    return uint32_result(result, value);
}

JNIEXPORT jlong JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsDeviceProfile(
    JNIEnv* environment, jclass type, jlong game)
{
    (void)environment;
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    uint32_t value = 0U;
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.graphics_device_get_graphics_profile(device, &value);
    }
    return uint32_result(result, value);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetGraphicsDeviceProfile(
    JNIEnv* environment, jclass type, jlong game, jint profile)
{
    (void)environment;
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    return result == CNA_RESULT_SUCCESS
        ? (jint)cna.graphics_device_set_graphics_profile_ext(
            device, (uint32_t)profile)
        : (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsDeviceScissorRectangle(
    JNIEnv* environment, jclass type, jlong game, jintArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) < 4) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    CNA_Rectangle rectangle = {0, 0, 0, 0};
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.graphics_device_get_scissor_rectangle(device, &rectangle);
    }
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    const jint values[4] = {
        (jint)rectangle.x, (jint)rectangle.y,
        (jint)rectangle.width, (jint)rectangle.height
    };
    (*environment)->SetIntArrayRegion(environment, output, 0, 4, values);
    return (*environment)->ExceptionCheck(environment)
        ? (jint)CNA_RESULT_INVALID_STATE : 0;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetGraphicsDeviceScissorRectangle(
    JNIEnv* environment, jclass type, jlong game,
    jint x, jint y, jint width, jint height)
{
    (void)environment;
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    return result == CNA_RESULT_SUCCESS
        ? (jint)cna.graphics_device_set_scissor_rectangle(
            device,
            (CNA_Rectangle){
                (int32_t)x, (int32_t)y, (int32_t)width, (int32_t)height
            })
        : (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsDeviceViewport(
    JNIEnv* environment, jclass type, jlong game, jintArray bounds, jfloatArray depth)
{
    (void)type;
    if (bounds == NULL || depth == NULL ||
        (*environment)->GetArrayLength(environment, bounds) < 4 ||
        (*environment)->GetArrayLength(environment, depth) < 2) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    CNA_Viewport viewport = {0, 0, 0, 0, 0.0f, 1.0f};
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.graphics_device_get_viewport(device, &viewport);
    }
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    const jint projected_bounds[4] = {
        (jint)viewport.x, (jint)viewport.y,
        (jint)viewport.width, (jint)viewport.height
    };
    const jfloat projected_depth[2] = {
        (jfloat)viewport.min_depth, (jfloat)viewport.max_depth
    };
    (*environment)->SetIntArrayRegion(environment, bounds, 0, 4, projected_bounds);
    (*environment)->SetFloatArrayRegion(environment, depth, 0, 2, projected_depth);
    return (*environment)->ExceptionCheck(environment)
        ? (jint)CNA_RESULT_INVALID_STATE : 0;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetGraphicsDeviceViewport(
    JNIEnv* environment, jclass type, jlong game,
    jint x, jint y, jint width, jint height, jfloat min_depth, jfloat max_depth)
{
    (void)environment;
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    return result == CNA_RESULT_SUCCESS
        ? (jint)cna.graphics_device_set_viewport(
            device,
            (CNA_Viewport){
                (int32_t)x, (int32_t)y, (int32_t)width, (int32_t)height,
                (float)min_depth, (float)max_depth
            })
        : (jint)result;
}

JNIEXPORT jlong JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsDeviceBlendFactor(
    JNIEnv* environment, jclass type, jlong game)
{
    (void)environment;
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    CNA_Color value = {0, 0, 0, 0};
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.graphics_device_get_blend_factor(device, &value);
    }
    return result == CNA_RESULT_SUCCESS
        ? (jlong)(uint32_t)packed_from_color(value) : -(jlong)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetGraphicsDeviceBlendFactor(
    JNIEnv* environment, jclass type, jlong game, jint packed_color)
{
    (void)environment;
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    return result == CNA_RESULT_SUCCESS
        ? (jint)cna.graphics_device_set_blend_factor(
            device, color_from_packed(packed_color))
        : (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsDeviceBlendState(
    JNIEnv* environment, jclass type, jlong game, jintArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) < 12) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    CNA_BlendState state;
    (void)memset(&state, 0, sizeof(state));
    state.struct_size = (uint32_t)sizeof(state);
    state.struct_version = UINT32_C(1);
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.graphics_device_get_blend_state(device, &state);
    }
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    const jint values[12] = {
        java_blend_function_from_c(state.alpha_blend_function),
        (jint)state.alpha_destination_blend,
        (jint)state.alpha_source_blend,
        java_blend_function_from_c(state.color_blend_function),
        (jint)state.color_destination_blend,
        (jint)state.color_source_blend,
        (jint)state.color_write_channels,
        (jint)state.color_write_channels1,
        (jint)state.color_write_channels2,
        (jint)state.color_write_channels3,
        (jint)packed_from_color(state.blend_factor),
        (jint)state.multi_sample_mask
    };
    (*environment)->SetIntArrayRegion(environment, output, 0, 12, values);
    return (*environment)->ExceptionCheck(environment)
        ? (jint)CNA_RESULT_INVALID_STATE : 0;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetGraphicsDeviceBlendState(
    JNIEnv* environment, jclass type, jlong game, jintArray input)
{
    (void)type;
    if (input == NULL || (*environment)->GetArrayLength(environment, input) < 12) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    jint values[12];
    (*environment)->GetIntArrayRegion(environment, input, 0, 12, values);
    if ((*environment)->ExceptionCheck(environment)) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    CNA_BlendState state;
    (void)memset(&state, 0, sizeof(state));
    state.struct_size = (uint32_t)sizeof(state);
    state.struct_version = UINT32_C(1);
    state.alpha_blend_function = c_blend_function_from_java(values[0]);
    state.alpha_destination_blend = (CNA_Blend)(uint32_t)values[1];
    state.alpha_source_blend = (CNA_Blend)(uint32_t)values[2];
    state.color_blend_function = c_blend_function_from_java(values[3]);
    state.color_destination_blend = (CNA_Blend)(uint32_t)values[4];
    state.color_source_blend = (CNA_Blend)(uint32_t)values[5];
    state.color_write_channels = (CNA_ColorWriteChannels)(uint32_t)values[6];
    state.color_write_channels1 = (CNA_ColorWriteChannels)(uint32_t)values[7];
    state.color_write_channels2 = (CNA_ColorWriteChannels)(uint32_t)values[8];
    state.color_write_channels3 = (CNA_ColorWriteChannels)(uint32_t)values[9];
    state.blend_factor = color_from_packed(values[10]);
    state.multi_sample_mask = (int32_t)values[11];
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    return result == CNA_RESULT_SUCCESS
        ? (jint)cna.graphics_device_set_blend_state(device, &state)
        : (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsDeviceDepthStencilState(
    JNIEnv* environment, jclass type, jlong game, jintArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) < 16) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    CNA_DepthStencilState state;
    (void)memset(&state, 0, sizeof(state));
    state.struct_size = (uint32_t)sizeof(state);
    state.struct_version = UINT32_C(1);
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.graphics_device_get_depth_stencil_state(device, &state);
    }
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    const jint values[16] = {
        (jint)state.depth_buffer_enable,
        (jint)state.depth_buffer_write_enable,
        (jint)state.stencil_enable,
        (jint)state.two_sided_stencil_mode,
        (jint)state.depth_buffer_function,
        (jint)state.stencil_function,
        (jint)state.stencil_mask,
        (jint)state.stencil_write_mask,
        (jint)state.reference_stencil,
        (jint)state.stencil_fail,
        (jint)state.stencil_depth_buffer_fail,
        (jint)state.stencil_pass,
        (jint)state.counter_clockwise_stencil_function,
        (jint)state.counter_clockwise_stencil_fail,
        (jint)state.counter_clockwise_stencil_depth_buffer_fail,
        (jint)state.counter_clockwise_stencil_pass
    };
    (*environment)->SetIntArrayRegion(environment, output, 0, 16, values);
    return (*environment)->ExceptionCheck(environment)
        ? (jint)CNA_RESULT_INVALID_STATE : 0;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetGraphicsDeviceDepthStencilState(
    JNIEnv* environment, jclass type, jlong game, jintArray input)
{
    (void)type;
    if (input == NULL || (*environment)->GetArrayLength(environment, input) < 16) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    jint values[16];
    (*environment)->GetIntArrayRegion(environment, input, 0, 16, values);
    if ((*environment)->ExceptionCheck(environment)) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    CNA_DepthStencilState state;
    (void)memset(&state, 0, sizeof(state));
    state.struct_size = (uint32_t)sizeof(state);
    state.struct_version = UINT32_C(1);
    state.depth_buffer_enable = (CNA_Bool)(uint8_t)values[0];
    state.depth_buffer_write_enable = (CNA_Bool)(uint8_t)values[1];
    state.stencil_enable = (CNA_Bool)(uint8_t)values[2];
    state.two_sided_stencil_mode = (CNA_Bool)(uint8_t)values[3];
    state.depth_buffer_function = (CNA_CompareFunction)(uint32_t)values[4];
    state.stencil_function = (CNA_CompareFunction)(uint32_t)values[5];
    state.stencil_mask = (int32_t)values[6];
    state.stencil_write_mask = (int32_t)values[7];
    state.reference_stencil = (int32_t)values[8];
    state.stencil_fail = (CNA_StencilOperation)(uint32_t)values[9];
    state.stencil_depth_buffer_fail = (CNA_StencilOperation)(uint32_t)values[10];
    state.stencil_pass = (CNA_StencilOperation)(uint32_t)values[11];
    state.counter_clockwise_stencil_function =
        (CNA_CompareFunction)(uint32_t)values[12];
    state.counter_clockwise_stencil_fail =
        (CNA_StencilOperation)(uint32_t)values[13];
    state.counter_clockwise_stencil_depth_buffer_fail =
        (CNA_StencilOperation)(uint32_t)values[14];
    state.counter_clockwise_stencil_pass =
        (CNA_StencilOperation)(uint32_t)values[15];
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    return result == CNA_RESULT_SUCCESS
        ? (jint)cna.graphics_device_set_depth_stencil_state(device, &state)
        : (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsDeviceRasterizerState(
    JNIEnv* environment, jclass type, jlong game,
    jintArray integer_output, jfloatArray float_output)
{
    (void)type;
    if (integer_output == NULL || float_output == NULL ||
        (*environment)->GetArrayLength(environment, integer_output) < 4 ||
        (*environment)->GetArrayLength(environment, float_output) < 2) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    CNA_RasterizerState state;
    (void)memset(&state, 0, sizeof(state));
    state.struct_size = (uint32_t)sizeof(state);
    state.struct_version = UINT32_C(1);
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.graphics_device_get_rasterizer_state(device, &state);
    }
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    const jint integers[4] = {
        (jint)state.cull_mode,
        (jint)state.fill_mode,
        (jint)state.multi_sample_anti_alias,
        (jint)state.scissor_test_enable
    };
    const jfloat floats[2] = {
        (jfloat)state.depth_bias,
        (jfloat)state.slope_scale_depth_bias
    };
    (*environment)->SetIntArrayRegion(environment, integer_output, 0, 4, integers);
    (*environment)->SetFloatArrayRegion(environment, float_output, 0, 2, floats);
    return (*environment)->ExceptionCheck(environment)
        ? (jint)CNA_RESULT_INVALID_STATE : 0;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetGraphicsDeviceRasterizerState(
    JNIEnv* environment, jclass type, jlong game,
    jintArray integer_input, jfloatArray float_input)
{
    (void)type;
    if (integer_input == NULL || float_input == NULL ||
        (*environment)->GetArrayLength(environment, integer_input) < 4 ||
        (*environment)->GetArrayLength(environment, float_input) < 2) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    jint integers[4];
    jfloat floats[2];
    (*environment)->GetIntArrayRegion(environment, integer_input, 0, 4, integers);
    (*environment)->GetFloatArrayRegion(environment, float_input, 0, 2, floats);
    if ((*environment)->ExceptionCheck(environment)) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    CNA_RasterizerState state;
    (void)memset(&state, 0, sizeof(state));
    state.struct_size = (uint32_t)sizeof(state);
    state.struct_version = UINT32_C(1);
    state.cull_mode = (CNA_CullMode)(uint32_t)integers[0];
    state.fill_mode = (CNA_FillMode)(uint32_t)integers[1];
    state.depth_bias = (float)floats[0];
    state.slope_scale_depth_bias = (float)floats[1];
    state.multi_sample_anti_alias = (CNA_Bool)(uint8_t)integers[2];
    state.scissor_test_enable = (CNA_Bool)(uint8_t)integers[3];
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    return result == CNA_RESULT_SUCCESS
        ? (jint)cna.graphics_device_set_rasterizer_state(device, &state)
        : (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsDeviceSamplerState(
    JNIEnv* environment, jclass type, jlong game,
    jint shader_stage, jint slot, jintArray integer_output, jfloatArray bias_output)
{
    (void)type;
    if (integer_output == NULL || bias_output == NULL ||
        (*environment)->GetArrayLength(environment, integer_output) < 6 ||
        (*environment)->GetArrayLength(environment, bias_output) < 1) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    CNA_SamplerState state;
    (void)memset(&state, 0, sizeof(state));
    state.struct_size = (uint32_t)sizeof(state);
    state.struct_version = UINT32_C(1);
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.graphics_device_get_sampler_state(
            device, (CNA_ShaderStage)(uint32_t)shader_stage, (uint32_t)slot, &state);
    }
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    const jint integers[6] = {
        (jint)state.address_u,
        (jint)state.address_v,
        (jint)state.address_w,
        (jint)state.filter,
        (jint)state.max_anisotropy,
        (jint)state.max_mip_level
    };
    const jfloat bias[1] = {(jfloat)state.mip_map_level_of_detail_bias};
    (*environment)->SetIntArrayRegion(environment, integer_output, 0, 6, integers);
    (*environment)->SetFloatArrayRegion(environment, bias_output, 0, 1, bias);
    return (*environment)->ExceptionCheck(environment)
        ? (jint)CNA_RESULT_INVALID_STATE : 0;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetGraphicsDeviceSamplerState(
    JNIEnv* environment, jclass type, jlong game,
    jint shader_stage, jint slot, jintArray integer_input, jfloat bias)
{
    (void)type;
    if (integer_input == NULL ||
        (*environment)->GetArrayLength(environment, integer_input) < 6) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    jint integers[6];
    (*environment)->GetIntArrayRegion(environment, integer_input, 0, 6, integers);
    if ((*environment)->ExceptionCheck(environment)) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    CNA_SamplerState state;
    (void)memset(&state, 0, sizeof(state));
    state.struct_size = (uint32_t)sizeof(state);
    state.struct_version = UINT32_C(1);
    state.address_u = (CNA_TextureAddressMode)(uint32_t)integers[0];
    state.address_v = (CNA_TextureAddressMode)(uint32_t)integers[1];
    state.address_w = (CNA_TextureAddressMode)(uint32_t)integers[2];
    state.filter = (CNA_TextureFilter)(uint32_t)integers[3];
    state.max_anisotropy = (int32_t)integers[4];
    state.max_mip_level = (int32_t)integers[5];
    state.mip_map_level_of_detail_bias = (float)bias;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    return result == CNA_RESULT_SUCCESS
        ? (jint)cna.graphics_device_set_sampler_state(
            device, (CNA_ShaderStage)(uint32_t)shader_stage, (uint32_t)slot, &state)
        : (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsDeviceTexture(
    JNIEnv* environment, jclass type, jlong game,
    jint shader_stage, jint slot, jlongArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) < 2) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    CNA_TextureSlotInfo info;
    (void)memset(&info, 0, sizeof(info));
    info.struct_size = (uint32_t)sizeof(info);
    info.struct_version = UINT32_C(1);
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.graphics_device_get_texture(
            device, (CNA_ShaderStage)(uint32_t)shader_stage, (uint32_t)slot, &info);
    }
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    const jlong values[2] = {(jlong)info.bound, (jlong)info.texture};
    (*environment)->SetLongArrayRegion(environment, output, 0, 2, values);
    return (*environment)->ExceptionCheck(environment)
        ? (jint)CNA_RESULT_INVALID_STATE : 0;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetGraphicsDeviceTexture(
    JNIEnv* environment, jclass type, jlong game,
    jint shader_stage, jint slot, jlong texture)
{
    (void)environment;
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    return result == CNA_RESULT_SUCCESS
        ? (jint)cna.graphics_device_set_texture(
            device,
            (CNA_ShaderStage)(uint32_t)shader_stage,
            (uint32_t)slot,
            (CNA_Handle)texture)
        : (jint)result;
}

JNIEXPORT jlong JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsDeviceMultiSampleMask(
    JNIEnv* environment, jclass type, jlong game)
{
    (void)environment;
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    int32_t value = 0;
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.graphics_device_get_multi_sample_mask(device, &value);
    }
    return int32_result(result, value);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetGraphicsDeviceMultiSampleMask(
    JNIEnv* environment, jclass type, jlong game, jint value)
{
    (void)environment;
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    return result == CNA_RESULT_SUCCESS
        ? (jint)cna.graphics_device_set_multi_sample_mask(device, (int32_t)value)
        : (jint)result;
}

JNIEXPORT jlong JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsDeviceReferenceStencil(
    JNIEnv* environment, jclass type, jlong game)
{
    (void)environment;
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    int32_t value = 0;
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.graphics_device_get_reference_stencil(device, &value);
    }
    return int32_result(result, value);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetGraphicsDeviceReferenceStencil(
    JNIEnv* environment, jclass type, jlong game, jint value)
{
    (void)environment;
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    return result == CNA_RESULT_SUCCESS
        ? (jint)cna.graphics_device_set_reference_stencil(device, (int32_t)value)
        : (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsDevicePresentationParameters(
    JNIEnv* environment, jclass type, jlong game, jintArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) < 10) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    CNA_PresentationParameters parameters;
    (void)memset(&parameters, 0, sizeof(parameters));
    parameters.struct_size = (uint32_t)sizeof(parameters);
    parameters.struct_version = UINT32_C(1);
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.graphics_device_get_presentation_parameters(device, &parameters);
    }
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    const jint values[10] = {
        (jint)parameters.back_buffer_format,
        (jint)parameters.back_buffer_width,
        (jint)parameters.back_buffer_height,
        (jint)parameters.depth_stencil_format,
        (jint)parameters.multi_sample_count,
        (jint)parameters.presentation_interval,
        (jint)parameters.display_orientation,
        (jint)parameters.render_target_usage,
        parameters.is_full_screen == CNA_TRUE ? 1 : 0,
        parameters.headless_ext == CNA_TRUE ? 1 : 0
    };
    (*environment)->SetIntArrayRegion(environment, output, 0, 10, values);
    return (*environment)->ExceptionCheck(environment)
        ? (jint)CNA_RESULT_INVALID_STATE : 0;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsDeviceDisplayMode(
    JNIEnv* environment, jclass type, jlong game, jintArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) < 4) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    CNA_DisplayMode mode;
    (void)memset(&mode, 0, sizeof(mode));
    mode.struct_size = (uint32_t)sizeof(mode);
    mode.struct_version = UINT32_C(1);
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.graphics_device_get_display_mode(device, &mode);
    }
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    uint32_t aspect_bits = 0U;
    (void)memcpy(&aspect_bits, &mode.aspect_ratio, sizeof(aspect_bits));
    const jint values[4] = {
        (jint)mode.width, (jint)mode.height,
        (jint)aspect_bits, (jint)mode.format
    };
    (*environment)->SetIntArrayRegion(environment, output, 0, 4, values);
    return (*environment)->ExceptionCheck(environment)
        ? (jint)CNA_RESULT_INVALID_STATE : 0;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsDeviceBackBufferInfo(
    JNIEnv* environment, jclass type, jlong game, jintArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) < 3) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    CNA_BackBufferInfo info;
    (void)memset(&info, 0, sizeof(info));
    info.struct_size = (uint32_t)sizeof(info);
    info.struct_version = UINT32_C(1);
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.graphics_device_get_backbuffer_info(device, &info);
    }
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    if (info.width > (uint32_t)INT32_MAX || info.height > (uint32_t)INT32_MAX ||
        info.format > (uint32_t)INT32_MAX) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    const jint values[3] = {
        (jint)info.width, (jint)info.height, (jint)info.format};
    (*environment)->SetIntArrayRegion(environment, output, 0, 3, values);
    return (*environment)->ExceptionCheck(environment)
        ? (jint)CNA_RESULT_INVALID_STATE : 0;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsDeviceBackBufferData(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jboolean has_rectangle,
    jint x,
    jint y,
    jint width,
    jint height,
    jint start_index,
    jint element_count,
    jintArray output)
{
    (void)type;
    if (output == NULL || start_index < 0 || element_count < 0 ||
        (has_rectangle != JNI_FALSE && has_rectangle != JNI_TRUE)) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    const jsize capacity = (*environment)->GetArrayLength(environment, output);
    if ((uint64_t)(uint32_t)start_index + (uint64_t)(uint32_t)element_count >
        (uint64_t)(uint32_t)capacity) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Color* const colors = capacity == 0
        ? NULL : (CNA_Color*)calloc((size_t)capacity, sizeof(CNA_Color));
    if (capacity != 0 && colors == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    CNA_BackBufferReadback readback;
    (void)memset(&readback, 0, sizeof(readback));
    readback.struct_size = (uint32_t)sizeof(readback);
    readback.struct_version = UINT32_C(1);
    readback.has_source_rectangle = has_rectangle == JNI_TRUE ? CNA_TRUE : CNA_FALSE;
    readback.source_rectangle = (CNA_Rectangle){
        (int32_t)x, (int32_t)y, (int32_t)width, (int32_t)height};
    readback.start_index = (uint64_t)(uint32_t)start_index;
    readback.element_count = (uint64_t)(uint32_t)element_count;

    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.graphics_device_get_backbuffer_data_window(
            device, &readback, colors, (uint64_t)(uint32_t)capacity);
    }
    if (result == CNA_RESULT_SUCCESS && element_count != 0) {
        jint* const packed = (jint*)malloc((size_t)element_count * sizeof(jint));
        if (packed == NULL) {
            result = CNA_RESULT_OUT_OF_MEMORY;
        } else {
            for (jint index = 0; index < element_count; ++index) {
                packed[index] = packed_from_color(colors[start_index + index]);
            }
            (*environment)->SetIntArrayRegion(
                environment, output, start_index, element_count, packed);
            if ((*environment)->ExceptionCheck(environment)) {
                result = CNA_RESULT_INVALID_STATE;
            }
            free(packed);
        }
    }
    free(colors);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeClearGraphicsDevice(
    JNIEnv* environment, jclass type, jlong game,
    jint options, jint packed_color, jfloat depth, jint stencil)
{
    (void)environment;
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    return result == CNA_RESULT_SUCCESS
        ? (jint)cna.graphics_device_clear_options(
            device,
            (CNA_ClearOptions)(uint32_t)options,
            color_from_packed(packed_color),
            (float)depth,
            (int32_t)stencil)
        : (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativePresentGraphicsDevice(
    JNIEnv* environment, jclass type, jlong game)
{
    (void)environment;
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    return result == CNA_RESULT_SUCCESS
        ? (jint)cna.graphics_device_present(device) : (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeResetGraphicsDevice(
    JNIEnv* environment, jclass type, jlong game)
{
    (void)environment;
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    return result == CNA_RESULT_SUCCESS
        ? (jint)cna.graphics_device_reset(device) : (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeResetGraphicsDeviceWithParameters(
    JNIEnv* environment, jclass type, jlong game,
    jintArray projected_parameters, jint adapter_index)
{
    (void)type;
    if (projected_parameters == NULL ||
        (*environment)->GetArrayLength(environment, projected_parameters) < 9 ||
        adapter_index < -1) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    CNA_PresentationParameters parameters;
    (void)memset(&parameters, 0, sizeof(parameters));
    parameters.struct_size = (uint32_t)sizeof(parameters);
    parameters.struct_version = UINT32_C(1);
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.graphics_device_get_presentation_parameters(device, &parameters);
    }
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    jint values[9];
    (*environment)->GetIntArrayRegion(
        environment, projected_parameters, 0, 9, values);
    if ((*environment)->ExceptionCheck(environment)) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    parameters.back_buffer_format = (CNA_SurfaceFormat)(uint32_t)values[0];
    parameters.back_buffer_width = (int32_t)values[1];
    parameters.back_buffer_height = (int32_t)values[2];
    parameters.depth_stencil_format = (CNA_DepthFormat)(uint32_t)values[3];
    parameters.multi_sample_count = (int32_t)values[4];
    parameters.presentation_interval = (CNA_PresentInterval)(uint32_t)values[5];
    parameters.display_orientation = (CNA_DisplayOrientation)(uint32_t)values[6];
    parameters.render_target_usage = (CNA_RenderTargetUsage)(uint32_t)values[7];
    parameters.is_full_screen = values[8] == 0 ? CNA_FALSE : CNA_TRUE;
    uint32_t selected_adapter = (uint32_t)adapter_index;
    const uint32_t* selected = adapter_index < 0 ? NULL : &selected_adapter;
    return (jint)cna.graphics_device_reset_with_parameters(
        device, &parameters, selected);
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

static CNA_Result create_temporary_vertex_declaration(
    JNIEnv* environment,
    jint vertex_stride,
    jintArray descriptor,
    CNA_VertexDeclarationHandle* out_declaration)
{
    if (descriptor == NULL || out_declaration == NULL || vertex_stride <= 0) {
        return CNA_RESULT_INVALID_ARGUMENT;
    }
    *out_declaration = CNA_INVALID_HANDLE;
    const jsize value_count = (*environment)->GetArrayLength(environment, descriptor);
    if (value_count <= 0 || value_count % 4 != 0) {
        return CNA_RESULT_INVALID_ARGUMENT;
    }
    jint* values = (jint*)malloc((size_t)value_count * sizeof(jint));
    const size_t element_count = (size_t)value_count / 4U;
    CNA_VertexElement* elements = (CNA_VertexElement*)calloc(
        element_count, sizeof(CNA_VertexElement));
    if (values == NULL || elements == NULL) {
        free(values);
        free(elements);
        return CNA_RESULT_OUT_OF_MEMORY;
    }
    (*environment)->GetIntArrayRegion(
        environment, descriptor, 0, value_count, values);
    if ((*environment)->ExceptionCheck(environment)) {
        free(values);
        free(elements);
        return CNA_RESULT_INVALID_STATE;
    }
    for (size_t index = 0U; index < element_count; ++index) {
        const size_t base = index * 4U;
        elements[index].offset = (int32_t)values[base];
        elements[index].format =
            (CNA_VertexElementFormat)(uint32_t)values[base + 1U];
        elements[index].usage =
            (CNA_VertexElementUsage)(uint32_t)values[base + 2U];
        elements[index].usage_index = (int32_t)values[base + 3U];
    }
    CNA_Result result = cna.vertex_declaration_create_with_stride(
        (int32_t)vertex_stride, elements, (uint64_t)element_count, out_declaration);
    free(values);
    free(elements);
    return result;
}

static CNA_Result copy_byte_array(
    JNIEnv* environment,
    jbyteArray source,
    uint8_t** out_bytes,
    uint64_t* out_byte_count)
{
    if (source == NULL || out_bytes == NULL || out_byte_count == NULL) {
        return CNA_RESULT_INVALID_ARGUMENT;
    }
    *out_bytes = NULL;
    *out_byte_count = 0U;
    const jsize byte_count = (*environment)->GetArrayLength(environment, source);
    if (byte_count < 0) {
        return CNA_RESULT_INVALID_STATE;
    }
    uint8_t* bytes = byte_count == 0
        ? NULL : (uint8_t*)malloc((size_t)byte_count);
    if (byte_count != 0 && bytes == NULL) {
        return CNA_RESULT_OUT_OF_MEMORY;
    }
    if (byte_count != 0) {
        (*environment)->GetByteArrayRegion(
            environment, source, 0, byte_count, (jbyte*)bytes);
        if ((*environment)->ExceptionCheck(environment)) {
            free(bytes);
            return CNA_RESULT_INVALID_STATE;
        }
    }
    *out_bytes = bytes;
    *out_byte_count = (uint64_t)byte_count;
    return CNA_RESULT_SUCCESS;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeCreateVertexBuffer(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jint vertex_stride,
    jintArray declaration,
    jint vertex_count,
    jint usage,
    jboolean dynamic,
    jlongArray output)
{
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    CNA_VertexDeclarationHandle native_declaration = CNA_INVALID_HANDLE;
    result = create_temporary_vertex_declaration(
        environment, vertex_stride, declaration, &native_declaration);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    CNA_VertexBufferCreateInfo info;
    (void)memset(&info, 0, sizeof(info));
    info.struct_size = (uint32_t)sizeof(info);
    info.struct_version = UINT32_C(1);
    info.vertex_declaration = native_declaration;
    info.vertex_count = (int32_t)vertex_count;
    info.buffer_usage = (CNA_BufferUsage)(uint32_t)usage;
    info.dynamic = dynamic == JNI_TRUE ? CNA_TRUE : CNA_FALSE;
    CNA_VertexBufferHandle vertex_buffer = CNA_INVALID_HANDLE;
    result = cna.vertex_buffer_create(device, &info, &vertex_buffer);
    CNA_Result declaration_result = cna.vertex_declaration_destroy(native_declaration);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    if (declaration_result != CNA_RESULT_SUCCESS) {
        (void)cna.vertex_buffer_destroy(vertex_buffer);
        return (jint)declaration_result;
    }
    result = set_handle_output(environment, output, vertex_buffer);
    if (result != CNA_RESULT_SUCCESS) {
        (void)cna.vertex_buffer_destroy(vertex_buffer);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetVertexBufferInfo(
    JNIEnv* environment, jclass type, jlong vertex_buffer, jintArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) < 7) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_VertexBufferInfo info;
    (void)memset(&info, 0, sizeof(info));
    info.struct_size = (uint32_t)sizeof(info);
    info.struct_version = UINT32_C(1);
    CNA_Result result = cna.vertex_buffer_get_info(
        (CNA_VertexBufferHandle)vertex_buffer, &info);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    if (info.buffer_usage > (uint32_t)INT32_MAX ||
        info.vertex_element_count > (uint64_t)INT32_MAX) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    const jint values[7] = {
        (jint)info.vertex_count,
        (jint)info.buffer_usage,
        (jint)info.vertex_stride,
        (jint)info.vertex_element_count,
        info.dynamic == CNA_TRUE ? 1 : 0,
        info.is_content_lost == CNA_TRUE ? 1 : 0,
        info.has_renderer == CNA_TRUE ? 1 : 0
    };
    (*environment)->SetIntArrayRegion(environment, output, 0, 7, values);
    return (*environment)->ExceptionCheck(environment)
        ? (jint)CNA_RESULT_INVALID_STATE : (jint)CNA_RESULT_SUCCESS;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetVertexBufferData(
    JNIEnv* environment,
    jclass type,
    jlong vertex_buffer,
    jint offset_in_bytes,
    jint vertex_type,
    jbyteArray payload,
    jint vertex_count,
    jint vertex_stride,
    jint options)
{
    (void)type;
    if (vertex_count < 0 || vertex_stride <= 0 || offset_in_bytes < -1) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    uint8_t* bytes = NULL;
    uint64_t byte_count = 0U;
    CNA_Result result = copy_byte_array(
        environment, payload, &bytes, &byte_count);
    const uint64_t expected = (uint64_t)(uint32_t)vertex_count
        * (uint64_t)(uint32_t)vertex_stride;
    if (result == CNA_RESULT_SUCCESS && byte_count != expected) {
        result = CNA_RESULT_INVALID_ARGUMENT;
    }
    if (result == CNA_RESULT_SUCCESS) {
        if (offset_in_bytes < 0) {
            CNA_VertexBufferTransfer transfer;
            (void)memset(&transfer, 0, sizeof(transfer));
            transfer.struct_size = (uint32_t)sizeof(transfer);
            transfer.struct_version = UINT32_C(1);
            transfer.vertex_type = (CNA_VertexType)(uint32_t)vertex_type;
            transfer.options = (CNA_SetDataOptions)(uint32_t)options;
            transfer.start_index = 0U;
            transfer.element_count = (uint64_t)(uint32_t)vertex_count;
            result = cna.vertex_buffer_set_data(
                (CNA_VertexBufferHandle)vertex_buffer,
                &transfer, bytes, (uint64_t)(uint32_t)vertex_count);
        } else if (options != (jint)CNA_SET_DATA_NONE) {
            result = CNA_RESULT_NOT_SUPPORTED;
        } else {
            result = cna.vertex_buffer_set_data_raw_at(
                (CNA_VertexBufferHandle)vertex_buffer,
                (uint64_t)(uint32_t)offset_in_bytes,
                bytes, byte_count, (uint64_t)(uint32_t)vertex_count,
                (uint32_t)vertex_stride);
        }
    }
    free(bytes);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetVertexBufferData(
    JNIEnv* environment,
    jclass type,
    jlong vertex_buffer,
    jint offset_in_bytes,
    jint vertex_count,
    jint vertex_stride,
    jbyteArray output)
{
    (void)type;
    if (output == NULL || offset_in_bytes < 0 ||
        vertex_count < 0 || vertex_stride <= 0) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    const uint64_t expected = (uint64_t)(uint32_t)vertex_count
        * (uint64_t)(uint32_t)vertex_stride;
    const jsize capacity = (*environment)->GetArrayLength(environment, output);
    if (expected > (uint64_t)INT32_MAX || (uint64_t)capacity != expected) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    uint8_t* bytes = capacity == 0
        ? NULL : (uint8_t*)malloc((size_t)capacity);
    if (capacity != 0 && bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    CNA_Result result = cna.vertex_buffer_get_data_raw(
        (CNA_VertexBufferHandle)vertex_buffer,
        (uint64_t)(uint32_t)offset_in_bytes,
        bytes, (uint64_t)capacity, (uint64_t)(uint32_t)vertex_count,
        (uint32_t)vertex_stride);
    if (result == CNA_RESULT_SUCCESS && capacity != 0) {
        (*environment)->SetByteArrayRegion(
            environment, output, 0, capacity, (const jbyte*)bytes);
        if ((*environment)->ExceptionCheck(environment)) {
            result = CNA_RESULT_INVALID_STATE;
        }
    }
    free(bytes);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeDestroyVertexBuffer(
    JNIEnv* environment, jclass type, jlong vertex_buffer)
{
    (void)environment;
    (void)type;
    return (jint)cna.vertex_buffer_destroy((CNA_VertexBufferHandle)vertex_buffer);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSubscribeVertexBufferContentLost(
    JNIEnv* environment,
    jclass type,
    jlong vertex_buffer,
    jobject callback_target,
    jlongArray output)
{
    (void)type;
    if (callback_target == NULL || output == NULL ||
        (*environment)->GetArrayLength(environment, output) < 1) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    JavaBufferContentLostRegistration* registration =
        (JavaBufferContentLostRegistration*)calloc(1U, sizeof(*registration));
    if (registration == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    atomic_init(&registration->callbacks_enabled, 1);
    registration->is_vertex = 1;
    registration->target = (*environment)->NewGlobalRef(environment, callback_target);
    if (registration->target == NULL) {
        free(registration);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    jclass target_class = (*environment)->GetObjectClass(environment, callback_target);
    if (target_class != NULL) {
        registration->event = (*environment)->GetMethodID(
            environment, target_class, "nativeContentLost", "()V");
        (*environment)->DeleteLocalRef(environment, target_class);
    }
    if (registration->event == NULL) {
        (*environment)->DeleteGlobalRef(environment, registration->target);
        free(registration);
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    CNA_VertexBufferEventRegistrationHandle native_registration = CNA_INVALID_HANDLE;
    CNA_Result result = cna.vertex_buffer_subscribe_content_lost(
        (CNA_VertexBufferHandle)vertex_buffer,
        on_buffer_content_lost, registration, &native_registration);
    if (result != CNA_RESULT_SUCCESS) {
        (*environment)->DeleteGlobalRef(environment, registration->target);
        free(registration);
        return (jint)result;
    }
    registration->native_registration = native_registration;
    const jlong token = (jlong)(intptr_t)registration;
    (*environment)->SetLongArrayRegion(environment, output, 0, 1, &token);
    if ((*environment)->ExceptionCheck(environment)) {
        (void)cna.vertex_buffer_unsubscribe_content_lost(native_registration);
        (*environment)->DeleteGlobalRef(environment, registration->target);
        free(registration);
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    return (jint)CNA_RESULT_SUCCESS;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeUnsubscribeVertexBufferContentLost(
    JNIEnv* environment, jclass type, jlong registration_token)
{
    (void)type;
    JavaBufferContentLostRegistration* registration =
        (JavaBufferContentLostRegistration*)(intptr_t)registration_token;
    if (registration == NULL || registration->is_vertex == 0) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    atomic_store_explicit(
        &registration->callbacks_enabled, 0, memory_order_release);
    CNA_Result result = cna.vertex_buffer_unsubscribe_content_lost(
        (CNA_VertexBufferEventRegistrationHandle)registration->native_registration);
    if (result != CNA_RESULT_SUCCESS) {
        atomic_store_explicit(
            &registration->callbacks_enabled, 1, memory_order_release);
        return (jint)result;
    }
    (*environment)->DeleteGlobalRef(environment, registration->target);
    free(registration);
    return (jint)CNA_RESULT_SUCCESS;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeCreateIndexBuffer(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jint index_element_size,
    jint index_count,
    jint usage,
    jboolean dynamic,
    jlongArray output)
{
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    CNA_IndexBufferCreateInfo info;
    (void)memset(&info, 0, sizeof(info));
    info.struct_size = (uint32_t)sizeof(info);
    info.struct_version = UINT32_C(1);
    info.index_count = (int32_t)index_count;
    info.index_element_size = (CNA_IndexElementSize)(uint32_t)index_element_size;
    info.buffer_usage = (CNA_BufferUsage)(uint32_t)usage;
    info.dynamic = dynamic == JNI_TRUE ? CNA_TRUE : CNA_FALSE;
    CNA_IndexBufferHandle index_buffer = CNA_INVALID_HANDLE;
    result = cna.index_buffer_create(device, &info, &index_buffer);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    result = set_handle_output(environment, output, index_buffer);
    if (result != CNA_RESULT_SUCCESS) {
        (void)cna.index_buffer_destroy(index_buffer);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetIndexBufferInfo(
    JNIEnv* environment, jclass type, jlong index_buffer, jintArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) < 6) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_IndexBufferInfo info;
    (void)memset(&info, 0, sizeof(info));
    info.struct_size = (uint32_t)sizeof(info);
    info.struct_version = UINT32_C(1);
    CNA_Result result = cna.index_buffer_get_info(
        (CNA_IndexBufferHandle)index_buffer, &info);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    if (info.index_element_size > (uint32_t)INT32_MAX ||
        info.buffer_usage > (uint32_t)INT32_MAX) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    const jint values[6] = {
        (jint)info.index_count,
        (jint)info.index_element_size,
        (jint)info.buffer_usage,
        info.dynamic == CNA_TRUE ? 1 : 0,
        info.is_content_lost == CNA_TRUE ? 1 : 0,
        info.has_renderer == CNA_TRUE ? 1 : 0
    };
    (*environment)->SetIntArrayRegion(environment, output, 0, 6, values);
    return (*environment)->ExceptionCheck(environment)
        ? (jint)CNA_RESULT_INVALID_STATE : (jint)CNA_RESULT_SUCCESS;
}

static CNA_Result make_index_transfer(
    jint index_element_size,
    jsize element_count,
    jint options,
    CNA_IndexBufferTransfer* out_transfer)
{
    if (out_transfer == NULL || element_count < 0 ||
        (index_element_size != (jint)CNA_INDEX_ELEMENT_SIZE_SIXTEEN_BITS &&
         index_element_size != (jint)CNA_INDEX_ELEMENT_SIZE_THIRTY_TWO_BITS)) {
        return CNA_RESULT_INVALID_ARGUMENT;
    }
    (void)memset(out_transfer, 0, sizeof(*out_transfer));
    out_transfer->struct_size = (uint32_t)sizeof(*out_transfer);
    out_transfer->struct_version = UINT32_C(1);
    out_transfer->index_element_size =
        (CNA_IndexElementSize)(uint32_t)index_element_size;
    out_transfer->options = (CNA_SetDataOptions)(uint32_t)options;
    out_transfer->start_index = 0U;
    out_transfer->element_count = (uint64_t)element_count;
    return CNA_RESULT_SUCCESS;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetIndexBufferData(
    JNIEnv* environment,
    jclass type,
    jlong index_buffer,
    jint offset_in_bytes,
    jint index_element_size,
    jintArray values,
    jint options)
{
    (void)type;
    if (values == NULL || offset_in_bytes < -1) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    const jsize count = (*environment)->GetArrayLength(environment, values);
    CNA_IndexBufferTransfer transfer;
    CNA_Result result = make_index_transfer(
        index_element_size, count, options, &transfer);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    jint* projected = count == 0
        ? NULL : (jint*)malloc((size_t)count * sizeof(jint));
    const size_t element_size = index_element_size ==
        (jint)CNA_INDEX_ELEMENT_SIZE_SIXTEEN_BITS ? sizeof(uint16_t) : sizeof(uint32_t);
    void* native_values = count == 0
        ? NULL : malloc((size_t)count * element_size);
    if (count != 0 && (projected == NULL || native_values == NULL)) {
        free(projected);
        free(native_values);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    if (count != 0) {
        (*environment)->GetIntArrayRegion(environment, values, 0, count, projected);
        if ((*environment)->ExceptionCheck(environment)) {
            free(projected);
            free(native_values);
            return (jint)CNA_RESULT_INVALID_STATE;
        }
    }
    if (index_element_size == (jint)CNA_INDEX_ELEMENT_SIZE_SIXTEEN_BITS) {
        uint16_t* words = (uint16_t*)native_values;
        for (jsize index = 0; index < count; ++index) {
            words[index] = (uint16_t)(uint32_t)projected[index];
        }
    } else {
        uint32_t* words = (uint32_t*)native_values;
        for (jsize index = 0; index < count; ++index) {
            words[index] = (uint32_t)projected[index];
        }
    }
    result = offset_in_bytes < 0
        ? cna.index_buffer_set_data(
            (CNA_IndexBufferHandle)index_buffer,
            &transfer, native_values, (uint64_t)count)
        : cna.index_buffer_set_data_at(
            (CNA_IndexBufferHandle)index_buffer,
            (uint64_t)(uint32_t)offset_in_bytes,
            &transfer, native_values, (uint64_t)count);
    free(projected);
    free(native_values);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetIndexBufferData(
    JNIEnv* environment,
    jclass type,
    jlong index_buffer,
    jint index_element_size,
    jintArray output)
{
    (void)type;
    if (output == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    const jsize count = (*environment)->GetArrayLength(environment, output);
    CNA_IndexBufferTransfer transfer;
    CNA_Result result = make_index_transfer(
        index_element_size, count, (jint)CNA_SET_DATA_NONE, &transfer);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    const size_t element_size = index_element_size ==
        (jint)CNA_INDEX_ELEMENT_SIZE_SIXTEEN_BITS ? sizeof(uint16_t) : sizeof(uint32_t);
    void* native_values = count == 0
        ? NULL : calloc((size_t)count, element_size);
    jint* projected = count == 0
        ? NULL : (jint*)malloc((size_t)count * sizeof(jint));
    if (count != 0 && (native_values == NULL || projected == NULL)) {
        free(native_values);
        free(projected);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t written = 0U;
    result = cna.index_buffer_get_data(
        (CNA_IndexBufferHandle)index_buffer,
        &transfer, native_values, (uint64_t)count, &written);
    if (result == CNA_RESULT_SUCCESS && written != (uint64_t)count) {
        result = CNA_RESULT_INVALID_STATE;
    }
    if (result == CNA_RESULT_SUCCESS) {
        if (index_element_size == (jint)CNA_INDEX_ELEMENT_SIZE_SIXTEEN_BITS) {
            const uint16_t* words = (const uint16_t*)native_values;
            for (jsize index = 0; index < count; ++index) {
                projected[index] = (jint)words[index];
            }
        } else {
            const uint32_t* words = (const uint32_t*)native_values;
            for (jsize index = 0; index < count; ++index) {
                (void)memcpy(&projected[index], &words[index], sizeof(jint));
            }
        }
        if (count != 0) {
            (*environment)->SetIntArrayRegion(
                environment, output, 0, count, projected);
            if ((*environment)->ExceptionCheck(environment)) {
                result = CNA_RESULT_INVALID_STATE;
            }
        }
    }
    free(native_values);
    free(projected);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeDestroyIndexBuffer(
    JNIEnv* environment, jclass type, jlong index_buffer)
{
    (void)environment;
    (void)type;
    return (jint)cna.index_buffer_destroy((CNA_IndexBufferHandle)index_buffer);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSubscribeIndexBufferContentLost(
    JNIEnv* environment,
    jclass type,
    jlong index_buffer,
    jobject callback_target,
    jlongArray output)
{
    (void)type;
    if (callback_target == NULL || output == NULL ||
        (*environment)->GetArrayLength(environment, output) < 1) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    JavaBufferContentLostRegistration* registration =
        (JavaBufferContentLostRegistration*)calloc(1U, sizeof(*registration));
    if (registration == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    atomic_init(&registration->callbacks_enabled, 1);
    registration->target = (*environment)->NewGlobalRef(environment, callback_target);
    if (registration->target == NULL) {
        free(registration);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    jclass target_class = (*environment)->GetObjectClass(environment, callback_target);
    if (target_class != NULL) {
        registration->event = (*environment)->GetMethodID(
            environment, target_class, "nativeContentLost", "()V");
        (*environment)->DeleteLocalRef(environment, target_class);
    }
    if (registration->event == NULL) {
        (*environment)->DeleteGlobalRef(environment, registration->target);
        free(registration);
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    CNA_IndexBufferEventRegistrationHandle native_registration = CNA_INVALID_HANDLE;
    CNA_Result result = cna.index_buffer_subscribe_content_lost(
        (CNA_IndexBufferHandle)index_buffer,
        on_buffer_content_lost, registration, &native_registration);
    if (result != CNA_RESULT_SUCCESS) {
        (*environment)->DeleteGlobalRef(environment, registration->target);
        free(registration);
        return (jint)result;
    }
    registration->native_registration = native_registration;
    const jlong token = (jlong)(intptr_t)registration;
    (*environment)->SetLongArrayRegion(environment, output, 0, 1, &token);
    if ((*environment)->ExceptionCheck(environment)) {
        (void)cna.index_buffer_unsubscribe_content_lost(native_registration);
        (*environment)->DeleteGlobalRef(environment, registration->target);
        free(registration);
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    return (jint)CNA_RESULT_SUCCESS;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeUnsubscribeIndexBufferContentLost(
    JNIEnv* environment, jclass type, jlong registration_token)
{
    (void)type;
    JavaBufferContentLostRegistration* registration =
        (JavaBufferContentLostRegistration*)(intptr_t)registration_token;
    if (registration == NULL || registration->is_vertex != 0) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    atomic_store_explicit(
        &registration->callbacks_enabled, 0, memory_order_release);
    CNA_Result result = cna.index_buffer_unsubscribe_content_lost(
        (CNA_IndexBufferEventRegistrationHandle)registration->native_registration);
    if (result != CNA_RESULT_SUCCESS) {
        atomic_store_explicit(
            &registration->callbacks_enabled, 1, memory_order_release);
        return (jint)result;
    }
    (*environment)->DeleteGlobalRef(environment, registration->target);
    free(registration);
    return (jint)CNA_RESULT_SUCCESS;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetGraphicsDeviceVertexBuffer(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jlong vertex_buffer,
    jint vertex_offset)
{
    (void)environment;
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    return vertex_offset == 0
        ? (jint)cna.graphics_device_set_vertex_buffer(
            device, (CNA_VertexBufferHandle)vertex_buffer)
        : (jint)cna.graphics_device_set_vertex_buffer_offset(
            device, (CNA_VertexBufferHandle)vertex_buffer, (int32_t)vertex_offset);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetGraphicsDeviceVertexBuffers(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jlongArray vertex_buffers,
    jintArray vertex_offsets,
    jintArray instance_frequencies)
{
    (void)type;
    if (vertex_buffers == NULL || vertex_offsets == NULL ||
        instance_frequencies == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    const jsize count = (*environment)->GetArrayLength(environment, vertex_buffers);
    if ((*environment)->GetArrayLength(environment, vertex_offsets) != count ||
        (*environment)->GetArrayLength(environment, instance_frequencies) != count) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    jlong* handles = count == 0
        ? NULL : (jlong*)malloc((size_t)count * sizeof(jlong));
    jint* offsets = count == 0
        ? NULL : (jint*)malloc((size_t)count * sizeof(jint));
    jint* frequencies = count == 0
        ? NULL : (jint*)malloc((size_t)count * sizeof(jint));
    CNA_VertexBufferBinding* bindings = count == 0
        ? NULL : (CNA_VertexBufferBinding*)calloc(
            (size_t)count, sizeof(CNA_VertexBufferBinding));
    if (count != 0 && (handles == NULL || offsets == NULL ||
        frequencies == NULL || bindings == NULL)) {
        free(handles);
        free(offsets);
        free(frequencies);
        free(bindings);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    if (count != 0) {
        (*environment)->GetLongArrayRegion(
            environment, vertex_buffers, 0, count, handles);
        (*environment)->GetIntArrayRegion(
            environment, vertex_offsets, 0, count, offsets);
        (*environment)->GetIntArrayRegion(
            environment, instance_frequencies, 0, count, frequencies);
        if ((*environment)->ExceptionCheck(environment)) {
            free(handles);
            free(offsets);
            free(frequencies);
            free(bindings);
            return (jint)CNA_RESULT_INVALID_STATE;
        }
    }
    for (jsize index = 0; index < count; ++index) {
        bindings[index].vertex_buffer = (CNA_VertexBufferHandle)handles[index];
        bindings[index].vertex_offset = (int32_t)offsets[index];
        bindings[index].instance_frequency = (int32_t)frequencies[index];
    }
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.graphics_device_set_vertex_buffers(
            device, bindings, (uint64_t)count);
    }
    free(handles);
    free(offsets);
    free(frequencies);
    free(bindings);
    return (jint)result;
}

JNIEXPORT jlong JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsDeviceVertexBufferCount(
    JNIEnv* environment, jclass type, jlong game)
{
    (void)environment;
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    uint64_t count = 0U;
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.graphics_device_get_vertex_buffer_count(device, &count);
    }
    if (result != CNA_RESULT_SUCCESS) {
        return -(jlong)result;
    }
    return count > (uint64_t)INT64_MAX
        ? -(jlong)CNA_RESULT_INVALID_STATE : (jlong)count;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeCopyGraphicsDeviceVertexBuffers(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jlongArray vertex_buffers,
    jintArray vertex_offsets,
    jintArray instance_frequencies)
{
    (void)type;
    if (vertex_buffers == NULL || vertex_offsets == NULL ||
        instance_frequencies == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    const jsize capacity = (*environment)->GetArrayLength(environment, vertex_buffers);
    if ((*environment)->GetArrayLength(environment, vertex_offsets) != capacity ||
        (*environment)->GetArrayLength(environment, instance_frequencies) != capacity) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_VertexBufferBinding* bindings = capacity == 0
        ? NULL : (CNA_VertexBufferBinding*)calloc(
            (size_t)capacity, sizeof(CNA_VertexBufferBinding));
    if (capacity != 0 && bindings == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    uint64_t count = 0U;
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.graphics_device_copy_vertex_buffers(
            device, bindings, (uint64_t)capacity, &count);
    }
    if (result == CNA_RESULT_SUCCESS && count != (uint64_t)capacity) {
        result = CNA_RESULT_INVALID_STATE;
    }
    jlong* handles = NULL;
    jint* offsets = NULL;
    jint* frequencies = NULL;
    if (result == CNA_RESULT_SUCCESS && capacity != 0) {
        handles = (jlong*)malloc((size_t)capacity * sizeof(jlong));
        offsets = (jint*)malloc((size_t)capacity * sizeof(jint));
        frequencies = (jint*)malloc((size_t)capacity * sizeof(jint));
        if (handles == NULL || offsets == NULL || frequencies == NULL) {
            result = CNA_RESULT_OUT_OF_MEMORY;
        } else {
            for (jsize index = 0; index < capacity; ++index) {
                handles[index] = (jlong)bindings[index].vertex_buffer;
                offsets[index] = (jint)bindings[index].vertex_offset;
                frequencies[index] = (jint)bindings[index].instance_frequency;
            }
            (*environment)->SetLongArrayRegion(
                environment, vertex_buffers, 0, capacity, handles);
            (*environment)->SetIntArrayRegion(
                environment, vertex_offsets, 0, capacity, offsets);
            (*environment)->SetIntArrayRegion(
                environment, instance_frequencies, 0, capacity, frequencies);
            if ((*environment)->ExceptionCheck(environment)) {
                result = CNA_RESULT_INVALID_STATE;
            }
        }
    }
    free(handles);
    free(offsets);
    free(frequencies);
    free(bindings);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetGraphicsDeviceIndexBuffer(
    JNIEnv* environment, jclass type, jlong game, jlong index_buffer)
{
    (void)environment;
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    return result == CNA_RESULT_SUCCESS
        ? (jint)cna.graphics_device_set_index_buffer(
            device, (CNA_IndexBufferHandle)index_buffer)
        : (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsDeviceIndexBuffer(
    JNIEnv* environment, jclass type, jlong game, jlongArray output)
{
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    CNA_IndexBufferHandle index_buffer = CNA_INVALID_HANDLE;
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.graphics_device_get_index_buffer(device, &index_buffer);
    }
    return result == CNA_RESULT_SUCCESS
        ? (jint)set_handle_output(environment, output, index_buffer)
        : (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeDrawPrimitives(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jint primitive_type,
    jint start_vertex,
    jint primitive_count)
{
    (void)environment;
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    return result == CNA_RESULT_SUCCESS
        ? (jint)cna.graphics_device_draw_primitives(
            device, (CNA_PrimitiveType)(uint32_t)primitive_type,
            (int32_t)start_vertex, (int32_t)primitive_count)
        : (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeDrawIndexedPrimitives(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jint primitive_type,
    jint base_vertex,
    jint min_vertex_index,
    jint num_vertices,
    jint start_index,
    jint primitive_count)
{
    (void)environment;
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    return result == CNA_RESULT_SUCCESS
        ? (jint)cna.graphics_device_draw_indexed_primitives(
            device, (CNA_PrimitiveType)(uint32_t)primitive_type,
            (int32_t)base_vertex, (int32_t)min_vertex_index,
            (int32_t)num_vertices, (int32_t)start_index,
            (int32_t)primitive_count)
        : (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeDrawInstancedPrimitives(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jint primitive_type,
    jint base_vertex,
    jint min_vertex_index,
    jint num_vertices,
    jint start_index,
    jint primitive_count,
    jint instance_count)
{
    (void)environment;
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    return result == CNA_RESULT_SUCCESS
        ? (jint)cna.graphics_device_draw_instanced_primitives(
            device, (CNA_PrimitiveType)(uint32_t)primitive_type,
            (int32_t)base_vertex, (int32_t)min_vertex_index,
            (int32_t)num_vertices, (int32_t)start_index,
            (int32_t)primitive_count, (int32_t)instance_count)
        : (jint)result;
}

static CNA_Result user_vertex_stride(
    jint vertex_source, uint32_t* out_vertex_stride)
{
    if (out_vertex_stride == NULL) {
        return CNA_RESULT_INVALID_ARGUMENT;
    }
    switch ((uint32_t)vertex_source) {
        case CNA_USER_VERTEX_SOURCE_POSITION_COLOR:
            *out_vertex_stride = (uint32_t)sizeof(CNA_VertexPositionColor);
            break;
        case CNA_USER_VERTEX_SOURCE_POSITION_COLOR_TEXTURE:
            *out_vertex_stride = (uint32_t)sizeof(CNA_VertexPositionColorTexture);
            break;
        case CNA_USER_VERTEX_SOURCE_POSITION_TEXTURE:
            *out_vertex_stride = (uint32_t)sizeof(CNA_VertexPositionTexture);
            break;
        case CNA_USER_VERTEX_SOURCE_POSITION_NORMAL_TEXTURE:
            *out_vertex_stride = (uint32_t)sizeof(CNA_VertexPositionNormalTexture);
            break;
        default:
            return CNA_RESULT_INVALID_ARGUMENT;
    }
    return CNA_RESULT_SUCCESS;
}

static CNA_Result user_primitive_element_count(
    jint primitive_type, jint primitive_count, uint64_t* out_count)
{
    if (out_count == NULL || primitive_count <= 0) {
        return CNA_RESULT_INVALID_ARGUMENT;
    }
    const uint64_t count = (uint64_t)(uint32_t)primitive_count;
    switch ((uint32_t)primitive_type) {
        case CNA_PRIMITIVE_TRIANGLE_LIST: *out_count = count * 3U; break;
        case CNA_PRIMITIVE_TRIANGLE_STRIP: *out_count = count + 2U; break;
        case CNA_PRIMITIVE_LINE_LIST: *out_count = count * 2U; break;
        case CNA_PRIMITIVE_LINE_STRIP: *out_count = count + 1U; break;
        default: return CNA_RESULT_INVALID_ARGUMENT;
    }
    return CNA_RESULT_SUCCESS;
}

static CNA_Result draw_user_primitives(
    JNIEnv* environment,
    jlong game,
    jint primitive_type,
    jint vertex_source,
    jbyteArray vertex_data,
    jint vertex_stride,
    jint vertex_offset,
    jint num_vertices,
    jint primitive_count,
    jintArray declaration,
    const void* index_data,
    uint64_t index_count,
    CNA_IndexElementSize index_element_size,
    jint index_offset,
    CNA_Bool indexed)
{
    if (vertex_offset < 0 || num_vertices < 0 ||
        (indexed == CNA_TRUE && index_offset < 0)) {
        return CNA_RESULT_INVALID_ARGUMENT;
    }
    uint32_t expected_stride = 0U;
    CNA_Result result = user_vertex_stride(vertex_source, &expected_stride);
    if (result != CNA_RESULT_SUCCESS || vertex_stride <= 0 ||
        (uint32_t)vertex_stride != expected_stride) {
        return CNA_RESULT_INVALID_ARGUMENT;
    }
    uint64_t required_elements = 0U;
    result = user_primitive_element_count(
        primitive_type, primitive_count, &required_elements);
    if (result != CNA_RESULT_SUCCESS) {
        return result;
    }
    uint8_t* bytes = NULL;
    uint64_t byte_count = 0U;
    result = copy_byte_array(environment, vertex_data, &bytes, &byte_count);
    if (result != CNA_RESULT_SUCCESS) {
        return result;
    }
    if (byte_count == 0U || byte_count % expected_stride != 0U) {
        free(bytes);
        return CNA_RESULT_INVALID_ARGUMENT;
    }
    const uint64_t vertex_capacity = byte_count / expected_stride;
    const uint64_t requested_vertices = indexed == CNA_TRUE
        ? (uint64_t)(uint32_t)num_vertices : required_elements;
    if ((uint64_t)(uint32_t)vertex_offset + requested_vertices > vertex_capacity ||
        (indexed == CNA_TRUE &&
         ((uint64_t)(uint32_t)index_offset + required_elements > index_count))) {
        free(bytes);
        return CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_VertexDeclarationHandle native_declaration = CNA_INVALID_HANDLE;
    result = create_temporary_vertex_declaration(
        environment, vertex_stride, declaration, &native_declaration);
    if (result != CNA_RESULT_SUCCESS) {
        free(bytes);
        return result;
    }
    CNA_Handle device = CNA_INVALID_HANDLE;
    result = graphics_device_from_game(game, &device);
    CNA_UserPrimitives primitives;
    (void)memset(&primitives, 0, sizeof(primitives));
    primitives.struct_size = (uint32_t)sizeof(primitives);
    primitives.struct_version = UINT32_C(1);
    primitives.primitive_type = (CNA_PrimitiveType)(uint32_t)primitive_type;
    primitives.vertex_source = (CNA_UserVertexSource)(uint32_t)vertex_source;
    primitives.vertex_data = bytes;
    primitives.vertex_declaration = native_declaration;
    primitives.vertex_offset = (int32_t)vertex_offset;
    primitives.num_vertices = (int32_t)num_vertices;
    primitives.primitive_count = (int32_t)primitive_count;
    if (result == CNA_RESULT_SUCCESS && indexed == CNA_TRUE) {
        CNA_UserIndices indices;
        (void)memset(&indices, 0, sizeof(indices));
        indices.struct_size = (uint32_t)sizeof(indices);
        indices.struct_version = UINT32_C(1);
        indices.index_element_size = index_element_size;
        indices.index_offset = (int32_t)index_offset;
        indices.index_data = index_data;
        result = cna.graphics_device_draw_user_indexed_primitives(
            device, &primitives, &indices);
    } else if (result == CNA_RESULT_SUCCESS) {
        result = cna.graphics_device_draw_user_primitives(device, &primitives);
    }
    const CNA_Result declaration_result =
        cna.vertex_declaration_destroy(native_declaration);
    free(bytes);
    return result == CNA_RESULT_SUCCESS ? declaration_result : result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeDrawUserPrimitives(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jint primitive_type,
    jint vertex_source,
    jbyteArray vertex_data,
    jint vertex_stride,
    jint vertex_offset,
    jint num_vertices,
    jint primitive_count,
    jintArray declaration)
{
    (void)type;
    return (jint)draw_user_primitives(
        environment, game, primitive_type, vertex_source,
        vertex_data, vertex_stride, vertex_offset, num_vertices, primitive_count,
        declaration, NULL, 0U, CNA_INDEX_ELEMENT_SIZE_SIXTEEN_BITS, 0, CNA_FALSE);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeDrawUserIndexedPrimitives16(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jint primitive_type,
    jint vertex_source,
    jbyteArray vertex_data,
    jint vertex_stride,
    jint vertex_offset,
    jint num_vertices,
    jshortArray index_data,
    jint index_offset,
    jint primitive_count,
    jintArray declaration)
{
    (void)type;
    if (index_data == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    const jsize index_count = (*environment)->GetArrayLength(environment, index_data);
    jshort* indices = index_count == 0
        ? NULL : (jshort*)malloc((size_t)index_count * sizeof(jshort));
    if (index_count != 0 && indices == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    if (index_count != 0) {
        (*environment)->GetShortArrayRegion(
            environment, index_data, 0, index_count, indices);
        if ((*environment)->ExceptionCheck(environment)) {
            free(indices);
            return (jint)CNA_RESULT_INVALID_STATE;
        }
    }
    CNA_Result result = draw_user_primitives(
        environment, game, primitive_type, vertex_source,
        vertex_data, vertex_stride, vertex_offset, num_vertices, primitive_count,
        declaration, indices, (uint64_t)index_count,
        CNA_INDEX_ELEMENT_SIZE_SIXTEEN_BITS, index_offset, CNA_TRUE);
    free(indices);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeDrawUserIndexedPrimitives32(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jint primitive_type,
    jint vertex_source,
    jbyteArray vertex_data,
    jint vertex_stride,
    jint vertex_offset,
    jint num_vertices,
    jintArray index_data,
    jint index_offset,
    jint primitive_count,
    jintArray declaration)
{
    (void)type;
    if (index_data == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    const jsize index_count = (*environment)->GetArrayLength(environment, index_data);
    jint* indices = index_count == 0
        ? NULL : (jint*)malloc((size_t)index_count * sizeof(jint));
    if (index_count != 0 && indices == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    if (index_count != 0) {
        (*environment)->GetIntArrayRegion(
            environment, index_data, 0, index_count, indices);
        if ((*environment)->ExceptionCheck(environment)) {
            free(indices);
            return (jint)CNA_RESULT_INVALID_STATE;
        }
    }
    CNA_Result result = draw_user_primitives(
        environment, game, primitive_type, vertex_source,
        vertex_data, vertex_stride, vertex_offset, num_vertices, primitive_count,
        declaration, indices, (uint64_t)index_count,
        CNA_INDEX_ELEMENT_SIZE_THIRTY_TWO_BITS, index_offset, CNA_TRUE);
    free(indices);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeCreateContentManager(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jbyteArray root_directory,
    jlongArray output)
{
    (void)type;
    if (root_directory == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    const jsize byte_count = (*environment)->GetArrayLength(environment, root_directory);
    jbyte* bytes = (*environment)->GetByteArrayElements(environment, root_directory, NULL);
    if (bytes == NULL) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    CNA_ContentManagerCreateInfo info;
    (void)memset(&info, 0, sizeof(info));
    info.struct_size = (uint32_t)sizeof(info);
    info.struct_version = UINT32_C(1);
    info.root_directory = (CNA_StringView){(const char*)bytes, (uint64_t)byte_count};
    CNA_Handle manager = CNA_INVALID_HANDLE;
    result = cna.content_manager_create(device, &info, &manager);
    (*environment)->ReleaseByteArrayElements(
        environment, root_directory, bytes, JNI_ABORT);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    result = set_handle_output(environment, output, manager);
    if (result != CNA_RESULT_SUCCESS) {
        (void)cna.content_manager_destroy(manager);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetContentManagerRootDirectory(
    JNIEnv* environment,
    jclass type,
    jlong content_manager,
    jbyteArray root_directory)
{
    (void)type;
    if (root_directory == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    const jsize byte_count = (*environment)->GetArrayLength(environment, root_directory);
    jbyte* bytes = (*environment)->GetByteArrayElements(environment, root_directory, NULL);
    if (bytes == NULL) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    CNA_Result result = cna.content_manager_set_root_directory(
        (CNA_Handle)content_manager,
        (CNA_StringView){(const char*)bytes, (uint64_t)byte_count});
    (*environment)->ReleaseByteArrayElements(
        environment, root_directory, bytes, JNI_ABORT);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeLoadContentTexture2D(
    JNIEnv* environment,
    jclass type,
    jlong content_manager,
    jbyteArray asset_name,
    jlongArray output)
{
    (void)type;
    if (asset_name == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    const jsize byte_count = (*environment)->GetArrayLength(environment, asset_name);
    jbyte* bytes = (*environment)->GetByteArrayElements(environment, asset_name, NULL);
    if (bytes == NULL) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    CNA_Handle texture = CNA_INVALID_HANDLE;
    CNA_Result result = cna.content_manager_load_texture2d(
        (CNA_Handle)content_manager,
        (CNA_StringView){(const char*)bytes, (uint64_t)byte_count},
        &texture);
    (*environment)->ReleaseByteArrayElements(environment, asset_name, bytes, JNI_ABORT);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    result = set_handle_output(environment, output, texture);
    if (result != CNA_RESULT_SUCCESS) {
        (void)cna.texture2d_destroy(texture);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeLoadContentSpriteFont(
    JNIEnv* environment,
    jclass type,
    jlong content_manager,
    jbyteArray asset_name,
    jlongArray output)
{
    (void)type;
    if (asset_name == NULL || output == NULL ||
        (*environment)->GetArrayLength(environment, output) < 2) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    const jsize byte_count = (*environment)->GetArrayLength(environment, asset_name);
    jbyte* bytes = (*environment)->GetByteArrayElements(environment, asset_name, NULL);
    if (bytes == NULL) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    CNA_Handle font = CNA_INVALID_HANDLE;
    CNA_Handle texture = CNA_INVALID_HANDLE;
    CNA_Result result = cna.content_manager_load_sprite_font(
        (CNA_Handle)content_manager,
        (CNA_StringView){(const char*)bytes, (uint64_t)byte_count},
        &font,
        &texture);
    (*environment)->ReleaseByteArrayElements(environment, asset_name, bytes, JNI_ABORT);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    jlong projected[2];
    (void)memcpy(&projected[0], &font, sizeof(font));
    (void)memcpy(&projected[1], &texture, sizeof(texture));
    (*environment)->SetLongArrayRegion(environment, output, 0, 2, projected);
    if ((*environment)->ExceptionCheck(environment)) {
        (void)cna.sprite_font_destroy(font);
        (void)cna.texture2d_destroy(texture);
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    return (jint)CNA_RESULT_SUCCESS;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeUnloadContentManager(
    JNIEnv* environment, jclass type, jlong content_manager)
{
    (void)environment;
    (void)type;
    return (jint)cna.content_manager_unload((CNA_Handle)content_manager);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeRegisterContentManagerBuiltinLoaders(
    JNIEnv* environment, jclass type, jlong content_manager)
{
    (void)environment;
    (void)type;
    return (jint)cna.content_manager_register_builtin_loaders(
        (CNA_Handle)content_manager);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeDestroyContentManager(
    JNIEnv* environment, jclass type, jlong content_manager)
{
    (void)environment;
    (void)type;
    return (jint)cna.content_manager_destroy((CNA_Handle)content_manager);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeCreateSpriteFont(
    JNIEnv* environment,
    jclass type,
    jlong texture,
    jintArray rectangles,
    jcharArray characters,
    jfloatArray kerning,
    jint line_spacing,
    jfloat spacing,
    jboolean has_default_character,
    jint default_character,
    jlongArray output)
{
    (void)type;
    if (rectangles == NULL || characters == NULL || kerning == NULL || output == NULL ||
        (has_default_character != JNI_FALSE && has_default_character != JNI_TRUE) ||
        default_character < 0 || default_character > UINT16_MAX) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    const jsize count = (*environment)->GetArrayLength(environment, characters);
    const jsize rectangle_count = (*environment)->GetArrayLength(environment, rectangles);
    const jsize kerning_count = (*environment)->GetArrayLength(environment, kerning);
    if (count <= 0 || count > INT32_MAX / 8 ||
        rectangle_count != count * 8 || kerning_count != count * 3 ||
        (*environment)->GetArrayLength(environment, output) < 1) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }

    CNA_SpriteFontGlyph* glyphs =
        (CNA_SpriteFontGlyph*)calloc((size_t)count, sizeof(*glyphs));
    if (glyphs == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    jint* rectangle_values =
        (*environment)->GetIntArrayElements(environment, rectangles, NULL);
    jchar* character_values =
        (*environment)->GetCharArrayElements(environment, characters, NULL);
    jfloat* kerning_values =
        (*environment)->GetFloatArrayElements(environment, kerning, NULL);
    if (rectangle_values == NULL || character_values == NULL || kerning_values == NULL) {
        if (rectangle_values != NULL) {
            (*environment)->ReleaseIntArrayElements(
                environment, rectangles, rectangle_values, JNI_ABORT);
        }
        if (character_values != NULL) {
            (*environment)->ReleaseCharArrayElements(
                environment, characters, character_values, JNI_ABORT);
        }
        if (kerning_values != NULL) {
            (*environment)->ReleaseFloatArrayElements(
                environment, kerning, kerning_values, JNI_ABORT);
        }
        free(glyphs);
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    for (jsize index = 0; index < count; ++index) {
        CNA_SpriteFontGlyph* glyph = &glyphs[index];
        glyph->struct_size = (uint32_t)sizeof(*glyph);
        glyph->struct_version = UINT32_C(1);
        const jsize rectangle_offset = index * 8;
        glyph->glyph_bounds = (CNA_Rectangle){
            rectangle_values[rectangle_offset],
            rectangle_values[rectangle_offset + 1],
            rectangle_values[rectangle_offset + 2],
            rectangle_values[rectangle_offset + 3]};
        glyph->cropping = (CNA_Rectangle){
            rectangle_values[rectangle_offset + 4],
            rectangle_values[rectangle_offset + 5],
            rectangle_values[rectangle_offset + 6],
            rectangle_values[rectangle_offset + 7]};
        glyph->character = (CNA_Char16)character_values[index];
        const jsize kerning_offset = index * 3;
        glyph->kerning = (CNA_Vector3){
            kerning_values[kerning_offset],
            kerning_values[kerning_offset + 1],
            kerning_values[kerning_offset + 2]};
    }
    (*environment)->ReleaseIntArrayElements(
        environment, rectangles, rectangle_values, JNI_ABORT);
    (*environment)->ReleaseCharArrayElements(
        environment, characters, character_values, JNI_ABORT);
    (*environment)->ReleaseFloatArrayElements(
        environment, kerning, kerning_values, JNI_ABORT);

    CNA_SpriteFontCreateInfo info;
    (void)memset(&info, 0, sizeof(info));
    info.struct_size = (uint32_t)sizeof(info);
    info.struct_version = UINT32_C(1);
    info.texture = (CNA_Handle)texture;
    info.glyphs = glyphs;
    info.glyph_count = (uint64_t)count;
    info.line_spacing = (int32_t)line_spacing;
    info.spacing = spacing;
    info.has_default_character =
        has_default_character == JNI_TRUE ? CNA_TRUE : CNA_FALSE;
    info.default_character = (CNA_Char16)default_character;
    CNA_Handle sprite_font = CNA_INVALID_HANDLE;
    CNA_Result result = cna.sprite_font_create(&info, &sprite_font);
    free(glyphs);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    result = set_handle_output(environment, output, sprite_font);
    if (result != CNA_RESULT_SUCCESS) {
        (void)cna.sprite_font_destroy(sprite_font);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetSpriteFontInfo(
    JNIEnv* environment,
    jclass type,
    jlong sprite_font,
    jintArray integers,
    jfloatArray spacing)
{
    (void)type;
    if (integers == NULL || spacing == NULL ||
        (*environment)->GetArrayLength(environment, integers) < 4 ||
        (*environment)->GetArrayLength(environment, spacing) < 1) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_SpriteFontInfo info;
    (void)memset(&info, 0, sizeof(info));
    info.struct_size = (uint32_t)sizeof(info);
    info.struct_version = UINT32_C(1);
    CNA_Result result = cna.sprite_font_get_info((CNA_Handle)sprite_font, &info);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    if (info.character_count > (uint64_t)INT32_MAX) {
        return (jint)CNA_RESULT_OVERFLOW;
    }
    const jint projected_integers[4] = {
        (jint)info.character_count,
        (jint)info.line_spacing,
        (jint)info.default_character,
        info.has_default_character == CNA_TRUE ? 1 : 0
    };
    const jfloat projected_spacing[1] = {(jfloat)info.spacing};
    (*environment)->SetIntArrayRegion(environment, integers, 0, 4, projected_integers);
    if (!(*environment)->ExceptionCheck(environment)) {
        (*environment)->SetFloatArrayRegion(environment, spacing, 0, 1, projected_spacing);
    }
    return (*environment)->ExceptionCheck(environment)
        ? (jint)CNA_RESULT_INVALID_STATE : (jint)CNA_RESULT_SUCCESS;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeCopySpriteFontCharacters(
    JNIEnv* environment,
    jclass type,
    jlong sprite_font,
    jcharArray output)
{
    (void)type;
    if (output == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    const jsize capacity = (*environment)->GetArrayLength(environment, output);
    CNA_Char16* characters = capacity == 0
        ? NULL : (CNA_Char16*)malloc((size_t)capacity * sizeof(CNA_Char16));
    if (capacity != 0 && characters == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t count = 0U;
    CNA_Result result = cna.sprite_font_copy_characters(
        (CNA_Handle)sprite_font, characters, (uint64_t)capacity, &count);
    if (result == CNA_RESULT_SUCCESS && count != (uint64_t)capacity) {
        result = CNA_RESULT_INVALID_STATE;
    }
    if (result == CNA_RESULT_SUCCESS && capacity != 0) {
        (*environment)->SetCharArrayRegion(
            environment, output, 0, capacity, (const jchar*)characters);
        if ((*environment)->ExceptionCheck(environment)) {
            result = CNA_RESULT_INVALID_STATE;
        }
    }
    free(characters);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetSpriteFontDefaultCharacter(
    JNIEnv* environment,
    jclass type,
    jlong sprite_font,
    jboolean has_value,
    jint value)
{
    (void)environment;
    (void)type;
    if (value < 0 || value > (jint)UINT16_MAX) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    return (jint)cna.sprite_font_set_default_character(
        (CNA_Handle)sprite_font,
        has_value == JNI_TRUE ? CNA_TRUE : CNA_FALSE,
        (CNA_Char16)value);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetSpriteFontLineSpacing(
    JNIEnv* environment, jclass type, jlong sprite_font, jint value)
{
    (void)environment;
    (void)type;
    return (jint)cna.sprite_font_set_line_spacing(
        (CNA_Handle)sprite_font, (int32_t)value);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetSpriteFontSpacing(
    JNIEnv* environment, jclass type, jlong sprite_font, jfloat value)
{
    (void)environment;
    (void)type;
    return (jint)cna.sprite_font_set_spacing((CNA_Handle)sprite_font, (float)value);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeMeasureSpriteFont(
    JNIEnv* environment,
    jclass type,
    jlong sprite_font,
    jbyteArray text,
    jfloatArray output)
{
    (void)type;
    if (text == NULL || output == NULL ||
        (*environment)->GetArrayLength(environment, output) < 2) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    const jsize byte_count = (*environment)->GetArrayLength(environment, text);
    jbyte* bytes = (*environment)->GetByteArrayElements(environment, text, NULL);
    if (bytes == NULL) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    CNA_Vector2 size = {0.0f, 0.0f};
    CNA_Result result = cna.sprite_font_measure_utf8(
        (CNA_Handle)sprite_font,
        (CNA_StringView){(const char*)bytes, (uint64_t)byte_count},
        &size);
    (*environment)->ReleaseByteArrayElements(environment, text, bytes, JNI_ABORT);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    const jfloat projected[2] = {(jfloat)size.x, (jfloat)size.y};
    (*environment)->SetFloatArrayRegion(environment, output, 0, 2, projected);
    return (*environment)->ExceptionCheck(environment)
        ? (jint)CNA_RESULT_INVALID_STATE : (jint)CNA_RESULT_SUCCESS;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeDestroySpriteFont(
    JNIEnv* environment, jclass type, jlong sprite_font)
{
    (void)environment;
    (void)type;
    return (jint)cna.sprite_font_destroy((CNA_Handle)sprite_font);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeCreateEffect(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jbyteArray effect_code,
    jboolean empty,
    jlongArray output)
{
    (void)type;
    if (effect_code == NULL || (empty != JNI_FALSE && empty != JNI_TRUE)) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    CNA_Handle effect = CNA_INVALID_HANDLE;
    if (empty == JNI_TRUE) {
        result = cna.effect_create_empty(device, &effect);
    } else {
        const jsize byte_count = (*environment)->GetArrayLength(environment, effect_code);
        jbyte* bytes = (*environment)->GetByteArrayElements(environment, effect_code, NULL);
        if (bytes == NULL) {
            return (jint)CNA_RESULT_INVALID_STATE;
        }
        result = cna.effect_create_compiled(
            device, (const uint8_t*)bytes, (uint64_t)byte_count, &effect);
        (*environment)->ReleaseByteArrayElements(
            environment, effect_code, bytes, JNI_ABORT);
    }
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    result = set_handle_output(environment, output, effect);
    if (result != CNA_RESULT_SUCCESS) {
        (void)cna.effect_destroy(effect);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeCreateBasicEffect(
    JNIEnv* environment, jclass type, jlong game, jlongArray output)
{
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    CNA_Handle effect = CNA_INVALID_HANDLE;
    result = cna.basic_effect_create(device, &effect);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    result = set_handle_output(environment, output, effect);
    if (result != CNA_RESULT_SUCCESS) {
        (void)cna.effect_destroy(effect);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeCreateStockEffect(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jint effect_kind,
    jlongArray output)
{
    (void)type;
    HandleGetHandleFunction create_function = NULL;
    switch (effect_kind) {
        case 0: create_function = cna.alpha_test_effect_create; break;
        case 1: create_function = cna.dual_texture_effect_create; break;
        case 2: create_function = cna.environment_map_effect_create; break;
        case 3: create_function = cna.skinned_effect_create; break;
        default: return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    CNA_Handle effect = CNA_INVALID_HANDLE;
    result = create_function(device, &effect);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    result = set_handle_output(environment, output, effect);
    if (result != CNA_RESULT_SUCCESS) {
        (void)cna.effect_destroy(effect);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeCreateEffectMaterial(
    JNIEnv* environment, jclass type, jlong source, jlongArray output)
{
    (void)type;
    CNA_Handle effect = CNA_INVALID_HANDLE;
    CNA_Result result = cna.effect_material_create((CNA_Handle)source, &effect);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    result = set_handle_output(environment, output, effect);
    if (result != CNA_RESULT_SUCCESS) {
        (void)cna.effect_destroy(effect);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeCloneEffect(
    JNIEnv* environment, jclass type, jlong effect, jlongArray output)
{
    (void)type;
    CNA_Handle clone = CNA_INVALID_HANDLE;
    CNA_Result result = cna.effect_clone((CNA_Handle)effect, &clone);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    result = set_handle_output(environment, output, clone);
    if (result != CNA_RESULT_SUCCESS) {
        (void)cna.effect_destroy(clone);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeApplyEffect(
    JNIEnv* environment, jclass type, jlong effect)
{
    (void)environment;
    (void)type;
    return (jint)cna.effect_apply((CNA_Handle)effect);
}

static HandleGetHandleFunction effect_child_function(const jint kind)
{
    switch (kind) {
        case 0: return cna.effect_get_parameters;
        case 1: return cna.effect_get_techniques;
        case 2: return cna.effect_get_current_technique;
        case 3: return cna.effect_technique_get_passes;
        case 4: return cna.effect_technique_get_annotations;
        case 5: return cna.effect_pass_get_annotations;
        case 6: return cna.effect_parameter_get_elements;
        case 7: return cna.effect_parameter_get_structure_members;
        case 8: return cna.effect_parameter_get_annotations;
        default: return NULL;
    }
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetEffectChild(
    JNIEnv* environment,
    jclass type,
    jlong handle,
    jint kind,
    jlongArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) < 2) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    HandleGetHandleFunction function = effect_child_function(kind);
    if (function == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Handle child = CNA_INVALID_HANDLE;
    CNA_Result result = function((CNA_Handle)handle, &child);
    uint32_t index = 0U;
    if (result == CNA_RESULT_SUCCESS && kind == 2 && child != CNA_INVALID_HANDLE) {
        result = cna.effect_technique_get_index_ext(child, &index);
        if (result != CNA_RESULT_SUCCESS) {
            (void)cna.effect_technique_destroy(child);
            child = CNA_INVALID_HANDLE;
        }
    }
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    const jlong values[2] = {(jlong)child, (jlong)index};
    (*environment)->SetLongArrayRegion(environment, output, 0, 2, values);
    if ((*environment)->ExceptionCheck(environment)) {
        if (child != CNA_INVALID_HANDLE) {
            switch (kind) {
                case 0: (void)cna.effect_parameter_collection_destroy(child); break;
                case 1: (void)cna.effect_technique_collection_destroy(child); break;
                case 2: (void)cna.effect_technique_destroy(child); break;
                case 3: (void)cna.effect_pass_collection_destroy(child); break;
                case 4: case 5: case 8:
                    (void)cna.effect_annotation_collection_destroy(child); break;
                case 6: case 7:
                    (void)cna.effect_parameter_collection_destroy(child); break;
                default: break;
            }
        }
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    return (jint)CNA_RESULT_SUCCESS;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetEffectCurrentTechnique(
    JNIEnv* environment, jclass type, jlong effect, jlong technique)
{
    (void)environment;
    (void)type;
    return (jint)cna.effect_set_current_technique(
        (CNA_Handle)effect, (CNA_Handle)technique);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeDestroyEffectObject(
    JNIEnv* environment, jclass type, jlong handle, jint kind)
{
    (void)environment;
    (void)type;
    switch (kind) {
        case 0: return (jint)cna.effect_destroy((CNA_Handle)handle);
        case 1: return (jint)cna.effect_parameter_collection_destroy((CNA_Handle)handle);
        case 2: return (jint)cna.effect_technique_collection_destroy((CNA_Handle)handle);
        case 3: return (jint)cna.effect_pass_collection_destroy((CNA_Handle)handle);
        case 4: return (jint)cna.effect_annotation_collection_destroy((CNA_Handle)handle);
        case 5: return (jint)cna.effect_parameter_destroy((CNA_Handle)handle);
        case 6: return (jint)cna.effect_technique_destroy((CNA_Handle)handle);
        case 7: return (jint)cna.effect_pass_destroy((CNA_Handle)handle);
        case 8: return (jint)cna.effect_annotation_destroy((CNA_Handle)handle);
        case 9: return (jint)cna.directional_light_destroy((CNA_Handle)handle);
        default: return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
}

static GameGetSizeFunction effect_collection_count_function(const jint kind)
{
    switch (kind) {
        case 0: return cna.effect_parameter_collection_get_count;
        case 1: return cna.effect_technique_collection_get_count;
        case 2: return cna.effect_pass_collection_get_count;
        case 3: return cna.effect_annotation_collection_get_count;
        default: return NULL;
    }
}

static HandleIndexGetHandleFunction effect_collection_at_function(const jint kind)
{
    switch (kind) {
        case 0: return cna.effect_parameter_collection_get_at;
        case 1: return cna.effect_technique_collection_get_at;
        case 2: return cna.effect_pass_collection_get_at;
        case 3: return cna.effect_annotation_collection_get_at;
        default: return NULL;
    }
}

JNIEXPORT jlong JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetEffectCollectionCount(
    JNIEnv* environment, jclass type, jlong collection, jint kind)
{
    (void)environment;
    (void)type;
    GameGetSizeFunction function = effect_collection_count_function(kind);
    if (function == NULL) {
        return -(jlong)CNA_RESULT_INVALID_ARGUMENT;
    }
    uint64_t count = 0U;
    CNA_Result result = function((CNA_Handle)collection, &count);
    if (result != CNA_RESULT_SUCCESS) {
        return -(jlong)result;
    }
    return count > (uint64_t)INT64_MAX
        ? -(jlong)CNA_RESULT_INVALID_STATE : (jlong)count;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetEffectCollectionElement(
    JNIEnv* environment,
    jclass type,
    jlong collection,
    jint kind,
    jint index,
    jlongArray output)
{
    (void)type;
    HandleIndexGetHandleFunction function = effect_collection_at_function(kind);
    if (function == NULL || index < 0) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Handle element = CNA_INVALID_HANDLE;
    CNA_Result result = function((CNA_Handle)collection, (uint64_t)index, &element);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    result = set_handle_output(environment, output, element);
    if (result != CNA_RESULT_SUCCESS) {
        switch (kind) {
            case 0: (void)cna.effect_parameter_destroy(element); break;
            case 1: (void)cna.effect_technique_destroy(element); break;
            case 2: (void)cna.effect_pass_destroy(element); break;
            case 3: (void)cna.effect_annotation_destroy(element); break;
            default: break;
        }
    }
    return (jint)result;
}

static GameGetSizeFunction effect_string_size_function(const jint kind)
{
    switch (kind) {
        case 0: return cna.effect_technique_get_name_byte_count;
        case 1: return cna.effect_pass_get_name_byte_count;
        case 2: return cna.effect_parameter_get_name_byte_count;
        case 3: return cna.effect_parameter_get_semantic_byte_count;
        case 4: return cna.effect_annotation_get_name_byte_count;
        case 5: return cna.effect_annotation_get_semantic_byte_count;
        case 6: return cna.effect_parameter_get_value_string_byte_count;
        case 7: return cna.effect_annotation_get_value_string_byte_count;
        default: return NULL;
    }
}

static GameCopyStringFunction effect_string_copy_function(const jint kind)
{
    switch (kind) {
        case 0: return cna.effect_technique_copy_name;
        case 1: return cna.effect_pass_copy_name;
        case 2: return cna.effect_parameter_copy_name;
        case 3: return cna.effect_parameter_copy_semantic;
        case 4: return cna.effect_annotation_copy_name;
        case 5: return cna.effect_annotation_copy_semantic;
        case 6: return cna.effect_parameter_copy_value_string;
        case 7: return cna.effect_annotation_copy_value_string;
        default: return NULL;
    }
}

JNIEXPORT jlong JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetEffectStringSize(
    JNIEnv* environment, jclass type, jlong handle, jint kind)
{
    (void)environment;
    (void)type;
    GameGetSizeFunction function = effect_string_size_function(kind);
    if (function == NULL) {
        return -(jlong)CNA_RESULT_INVALID_ARGUMENT;
    }
    uint64_t size = 0U;
    CNA_Result result = function((CNA_Handle)handle, &size);
    if (result != CNA_RESULT_SUCCESS) {
        return -(jlong)result;
    }
    return size > (uint64_t)INT64_MAX
        ? -(jlong)CNA_RESULT_INVALID_STATE : (jlong)size;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeCopyEffectString(
    JNIEnv* environment, jclass type, jlong handle, jint kind, jbyteArray output)
{
    (void)type;
    GameCopyStringFunction function = effect_string_copy_function(kind);
    if (function == NULL || output == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    const jsize capacity = (*environment)->GetArrayLength(environment, output);
    jbyte* bytes = (*environment)->GetByteArrayElements(environment, output, NULL);
    if (bytes == NULL) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    uint64_t written = 0U;
    CNA_Result result = function(
        (CNA_Handle)handle, (char*)bytes, (uint64_t)capacity, &written);
    (*environment)->ReleaseByteArrayElements(environment, output, bytes, 0);
    if (result == CNA_RESULT_SUCCESS && written != (uint64_t)capacity) {
        result = CNA_RESULT_INVALID_STATE;
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetEffectInfo(
    JNIEnv* environment,
    jclass type,
    jlong handle,
    jint kind,
    jintArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) < 4) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    jint values[4];
    CNA_Result result;
    if (kind == 0) {
        CNA_EffectParameterInfo info;
        (void)memset(&info, 0, sizeof(info));
        info.struct_size = (uint32_t)sizeof(info);
        info.struct_version = UINT32_C(1);
        result = cna.effect_parameter_get_info((CNA_Handle)handle, &info);
        values[0] = (jint)info.row_count;
        values[1] = (jint)info.column_count;
        values[2] = (jint)info.parameter_class;
        values[3] = (jint)info.parameter_type;
    } else if (kind == 1) {
        CNA_EffectAnnotationInfo info;
        (void)memset(&info, 0, sizeof(info));
        info.struct_size = (uint32_t)sizeof(info);
        info.struct_version = UINT32_C(1);
        result = cna.effect_annotation_get_info((CNA_Handle)handle, &info);
        values[0] = (jint)info.row_count;
        values[1] = (jint)info.column_count;
        values[2] = (jint)info.parameter_class;
        values[3] = (jint)info.parameter_type;
    } else {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    (*environment)->SetIntArrayRegion(environment, output, 0, 4, values);
    return (*environment)->ExceptionCheck(environment)
        ? (jint)CNA_RESULT_INVALID_STATE : (jint)CNA_RESULT_SUCCESS;
}

static size_t effect_float_width(const jint value_type)
{
    switch (value_type) {
        case CNA_EFFECT_VALUE_SINGLE: return 1U;
        case CNA_EFFECT_VALUE_MATRIX:
        case CNA_EFFECT_VALUE_MATRIX_TRANSPOSE: return 16U;
        case CNA_EFFECT_VALUE_QUATERNION:
        case CNA_EFFECT_VALUE_VECTOR4: return 4U;
        case CNA_EFFECT_VALUE_VECTOR2: return 2U;
        case CNA_EFFECT_VALUE_VECTOR3: return 3U;
        default: return 0U;
    }
}

JNIEXPORT jlong JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetEffectInts(
    JNIEnv* environment,
    jclass type,
    jlong handle,
    jboolean annotation,
    jint value_type,
    jint count,
    jintArray output)
{
    (void)type;
    if (output == NULL || count < 0 || (*environment)->GetArrayLength(environment, output) < count ||
        (annotation != JNI_FALSE && annotation != JNI_TRUE) ||
        (value_type != CNA_EFFECT_VALUE_BOOLEAN && value_type != CNA_EFFECT_VALUE_INT32)) {
        return -(jlong)CNA_RESULT_INVALID_ARGUMENT;
    }
    if (annotation == JNI_TRUE) {
        if (count < 1) {
            return -(jlong)CNA_RESULT_INVALID_ARGUMENT;
        }
        jint value;
        CNA_Result result;
        if (value_type == CNA_EFFECT_VALUE_BOOLEAN) {
            CNA_Bool native = CNA_FALSE;
            result = cna.effect_annotation_get_value_boolean((CNA_Handle)handle, &native);
            value = native == CNA_TRUE ? 1 : 0;
        } else {
            int32_t native = 0;
            result = cna.effect_annotation_get_value_int32((CNA_Handle)handle, &native);
            value = (jint)native;
        }
        if (result != CNA_RESULT_SUCCESS) {
            return -(jlong)result;
        }
        (*environment)->SetIntArrayRegion(environment, output, 0, 1, &value);
        return (*environment)->ExceptionCheck(environment)
            ? -(jlong)CNA_RESULT_INVALID_STATE : (jlong)1;
    }
    void* native = NULL;
    if (count != 0) {
        const size_t element_size = value_type == CNA_EFFECT_VALUE_BOOLEAN
            ? sizeof(CNA_Bool) : sizeof(int32_t);
        native = calloc((size_t)count, element_size);
        if (native == NULL) {
            return -(jlong)CNA_RESULT_OUT_OF_MEMORY;
        }
    }
    uint64_t written = 0U;
    CNA_Result result = cna.effect_parameter_get_values(
        (CNA_Handle)handle, (CNA_EffectValueType)value_type,
        (uint64_t)count, native, (uint64_t)count, &written);
    if (result == CNA_RESULT_SUCCESS && written > (uint64_t)count) {
        result = CNA_RESULT_INVALID_STATE;
    }
    jint* projected = NULL;
    if (result == CNA_RESULT_SUCCESS && written != 0U) {
        projected = (jint*)malloc((size_t)written * sizeof(jint));
        if (projected == NULL) {
            result = CNA_RESULT_OUT_OF_MEMORY;
        } else {
            for (uint64_t index = 0U; index < written; ++index) {
                projected[index] = value_type == CNA_EFFECT_VALUE_BOOLEAN
                    ? (((CNA_Bool*)native)[index] == CNA_TRUE ? 1 : 0)
                    : (jint)((int32_t*)native)[index];
            }
            (*environment)->SetIntArrayRegion(
                environment, output, 0, (jsize)written, projected);
            if ((*environment)->ExceptionCheck(environment)) {
                result = CNA_RESULT_INVALID_STATE;
            }
        }
    }
    free(projected);
    free(native);
    return result == CNA_RESULT_SUCCESS ? (jlong)written : -(jlong)result;
}

JNIEXPORT jlong JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetEffectFloats(
    JNIEnv* environment,
    jclass type,
    jlong handle,
    jboolean annotation,
    jint value_type,
    jint count,
    jfloatArray output)
{
    (void)type;
    const size_t width = effect_float_width(value_type);
    if (width == 0U || output == NULL || count < 0 ||
        (annotation != JNI_FALSE && annotation != JNI_TRUE) ||
        (uint64_t)(*environment)->GetArrayLength(environment, output)
            < (uint64_t)(uint32_t)count * width) {
        return -(jlong)CNA_RESULT_INVALID_ARGUMENT;
    }
    float* values = count == 0 ? NULL
        : (float*)calloc((size_t)count * width, sizeof(float));
    if (count != 0 && values == NULL) {
        return -(jlong)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t written = 0U;
    CNA_Result result = CNA_RESULT_SUCCESS;
    if (annotation == JNI_TRUE) {
        if (count < 1) {
            result = CNA_RESULT_INVALID_ARGUMENT;
        } else {
            void* destination = values;
            switch (value_type) {
                case CNA_EFFECT_VALUE_SINGLE:
                    result = cna.effect_annotation_get_value_single(
                        (CNA_Handle)handle, destination); break;
                case CNA_EFFECT_VALUE_MATRIX:
                    result = cna.effect_annotation_get_value_matrix(
                        (CNA_Handle)handle, destination); break;
                case CNA_EFFECT_VALUE_VECTOR2:
                    result = cna.effect_annotation_get_value_vector2(
                        (CNA_Handle)handle, destination); break;
                case CNA_EFFECT_VALUE_VECTOR3:
                    result = cna.effect_annotation_get_value_vector3(
                        (CNA_Handle)handle, destination); break;
                case CNA_EFFECT_VALUE_VECTOR4:
                    result = cna.effect_annotation_get_value_vector4(
                        (CNA_Handle)handle, destination); break;
                default: result = CNA_RESULT_INVALID_ARGUMENT; break;
            }
            written = result == CNA_RESULT_SUCCESS ? UINT64_C(1) : UINT64_C(0);
        }
    } else {
        result = cna.effect_parameter_get_values(
            (CNA_Handle)handle, (CNA_EffectValueType)value_type,
            (uint64_t)count, values, (uint64_t)count, &written);
        if (result == CNA_RESULT_SUCCESS && written > (uint64_t)count) {
            result = CNA_RESULT_INVALID_STATE;
        }
    }
    if (result == CNA_RESULT_SUCCESS && written != 0U) {
        (*environment)->SetFloatArrayRegion(
            environment, output, 0, (jsize)(written * width), values);
        if ((*environment)->ExceptionCheck(environment)) {
            result = CNA_RESULT_INVALID_STATE;
        }
    }
    free(values);
    return result == CNA_RESULT_SUCCESS ? (jlong)written : -(jlong)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetEffectInts(
    JNIEnv* environment,
    jclass type,
    jlong parameter,
    jint value_type,
    jintArray values)
{
    (void)type;
    if (values == NULL ||
        (value_type != CNA_EFFECT_VALUE_BOOLEAN && value_type != CNA_EFFECT_VALUE_INT32)) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    const jsize count = (*environment)->GetArrayLength(environment, values);
    jint* projected = (*environment)->GetIntArrayElements(environment, values, NULL);
    if (projected == NULL && count != 0) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    const size_t element_size = value_type == CNA_EFFECT_VALUE_BOOLEAN
        ? sizeof(CNA_Bool) : sizeof(int32_t);
    void* native = count == 0 ? NULL : malloc((size_t)count * element_size);
    if (count != 0 && native == NULL) {
        (*environment)->ReleaseIntArrayElements(environment, values, projected, JNI_ABORT);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    for (jsize index = 0; index < count; ++index) {
        if (value_type == CNA_EFFECT_VALUE_BOOLEAN) {
            ((CNA_Bool*)native)[index] = projected[index] == 0 ? CNA_FALSE : CNA_TRUE;
        } else {
            ((int32_t*)native)[index] = (int32_t)projected[index];
        }
    }
    CNA_Result result = cna.effect_parameter_set_values(
        (CNA_Handle)parameter, (CNA_EffectValueType)value_type,
        native, (uint64_t)count);
    free(native);
    if (count != 0) {
        (*environment)->ReleaseIntArrayElements(environment, values, projected, JNI_ABORT);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetEffectIntValue(
    JNIEnv* environment,
    jclass type,
    jlong parameter,
    jint value_type,
    jintArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) != 1 ||
        (value_type != CNA_EFFECT_VALUE_BOOLEAN && value_type != CNA_EFFECT_VALUE_INT32)) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Result result;
    jint projected = 0;
    if (value_type == CNA_EFFECT_VALUE_BOOLEAN) {
        CNA_Bool value = CNA_FALSE;
        result = cna.effect_parameter_get_value(
            (CNA_Handle)parameter, CNA_EFFECT_VALUE_BOOLEAN, &value);
        projected = value == CNA_FALSE ? 0 : 1;
    } else {
        int32_t value = 0;
        result = cna.effect_parameter_get_value(
            (CNA_Handle)parameter, CNA_EFFECT_VALUE_INT32, &value);
        projected = (jint)value;
    }
    if (result == CNA_RESULT_SUCCESS) {
        (*environment)->SetIntArrayRegion(environment, output, 0, 1, &projected);
        if ((*environment)->ExceptionCheck(environment)) {
            return (jint)CNA_RESULT_INVALID_STATE;
        }
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetEffectIntValue(
    JNIEnv* environment,
    jclass type,
    jlong parameter,
    jint value_type,
    jint value)
{
    (void)environment;
    (void)type;
    if (value_type == CNA_EFFECT_VALUE_BOOLEAN) {
        const CNA_Bool native = value == 0 ? CNA_FALSE : CNA_TRUE;
        return (jint)cna.effect_parameter_set_value(
            (CNA_Handle)parameter, CNA_EFFECT_VALUE_BOOLEAN, &native);
    }
    if (value_type == CNA_EFFECT_VALUE_INT32) {
        const int32_t native = (int32_t)value;
        return (jint)cna.effect_parameter_set_value(
            (CNA_Handle)parameter, CNA_EFFECT_VALUE_INT32, &native);
    }
    return (jint)CNA_RESULT_INVALID_ARGUMENT;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetEffectFloatValue(
    JNIEnv* environment,
    jclass type,
    jlong parameter,
    jint value_type,
    jfloatArray output)
{
    (void)type;
    const size_t width = effect_float_width(value_type);
    if (width == 0U || output == NULL ||
        (size_t)(*environment)->GetArrayLength(environment, output) != width) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    float values[16] = {0};
    const CNA_Result result = cna.effect_parameter_get_value(
        (CNA_Handle)parameter, (CNA_EffectValueType)value_type, values);
    if (result == CNA_RESULT_SUCCESS) {
        (*environment)->SetFloatArrayRegion(
            environment, output, 0, (jsize)width, (const jfloat*)values);
        if ((*environment)->ExceptionCheck(environment)) {
            return (jint)CNA_RESULT_INVALID_STATE;
        }
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetEffectFloatValue(
    JNIEnv* environment,
    jclass type,
    jlong parameter,
    jint value_type,
    jfloatArray input)
{
    (void)type;
    const size_t width = effect_float_width(value_type);
    if (width == 0U || input == NULL ||
        (size_t)(*environment)->GetArrayLength(environment, input) != width) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    float values[16] = {0};
    (*environment)->GetFloatArrayRegion(
        environment, input, 0, (jsize)width, (jfloat*)values);
    if ((*environment)->ExceptionCheck(environment)) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    return (jint)cna.effect_parameter_set_value(
        (CNA_Handle)parameter, (CNA_EffectValueType)value_type, values);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetEffectFloats(
    JNIEnv* environment,
    jclass type,
    jlong parameter,
    jint value_type,
    jfloatArray values,
    jint count)
{
    (void)type;
    const size_t width = effect_float_width(value_type);
    if (width == 0U || values == NULL || count < 0 ||
        (uint64_t)(*environment)->GetArrayLength(environment, values)
            != (uint64_t)(uint32_t)count * width) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    jfloat* native = (*environment)->GetFloatArrayElements(environment, values, NULL);
    if (native == NULL && count != 0) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    CNA_Result result = cna.effect_parameter_set_values(
        (CNA_Handle)parameter, (CNA_EffectValueType)value_type,
        native, (uint64_t)count);
    if (count != 0) {
        (*environment)->ReleaseFloatArrayElements(environment, values, native, JNI_ABORT);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetEffectString(
    JNIEnv* environment, jclass type, jlong parameter, jbyteArray value)
{
    (void)type;
    if (value == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    const jsize byte_count = (*environment)->GetArrayLength(environment, value);
    jbyte* bytes = (*environment)->GetByteArrayElements(environment, value, NULL);
    if (bytes == NULL && byte_count != 0) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    CNA_Result result = cna.effect_parameter_set_value_string(
        (CNA_Handle)parameter,
        (CNA_StringView){(const char*)bytes, (uint64_t)byte_count});
    if (byte_count != 0) {
        (*environment)->ReleaseByteArrayElements(environment, value, bytes, JNI_ABORT);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetEffectTexture(
    JNIEnv* environment,
    jclass type,
    jlong parameter,
    jint texture_type,
    jlongArray output)
{
    (void)type;
    CNA_Handle texture = CNA_INVALID_HANDLE;
    CNA_Result result = cna.effect_parameter_get_value_texture(
        (CNA_Handle)parameter, (CNA_EffectTextureType)texture_type, &texture);
    return result == CNA_RESULT_SUCCESS
        ? (jint)set_handle_output(environment, output, texture) : (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetEffectTexture(
    JNIEnv* environment,
    jclass type,
    jlong parameter,
    jint texture_type,
    jlong texture)
{
    (void)environment;
    (void)type;
    return (jint)cna.effect_parameter_set_value_texture(
        (CNA_Handle)parameter, (CNA_EffectTextureType)texture_type,
        (CNA_Handle)texture);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeApplyEffectPass(
    JNIEnv* environment, jclass type, jlong pass)
{
    (void)environment;
    (void)type;
    return (jint)cna.effect_pass_apply((CNA_Handle)pass);
}

static GameGetBoolFunction basic_effect_bool_getter(const jint kind)
{
    switch (kind) {
        case 0: return cna.basic_effect_get_vertex_color_enabled;
        case 1: return cna.basic_effect_get_prefer_per_pixel_lighting;
        case 2: return cna.basic_effect_get_texture_enabled;
        case 3: return cna.effect_lights_get_enabled;
        case 4: return cna.effect_fog_get_enabled;
        default: return NULL;
    }
}

static GameSetBoolFunction basic_effect_bool_setter(const jint kind)
{
    switch (kind) {
        case 0: return cna.basic_effect_set_vertex_color_enabled;
        case 1: return cna.basic_effect_set_prefer_per_pixel_lighting;
        case 2: return cna.basic_effect_set_texture_enabled;
        case 3: return cna.effect_lights_set_enabled;
        case 4: return cna.effect_fog_set_enabled;
        default: return NULL;
    }
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetBasicEffectBoolean(
    JNIEnv* environment, jclass type, jlong effect, jint kind)
{
    (void)environment;
    (void)type;
    GameGetBoolFunction function = basic_effect_bool_getter(kind);
    if (function == NULL) {
        return -(jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Bool value = CNA_FALSE;
    const CNA_Result result = function((CNA_Handle)effect, &value);
    return result == CNA_RESULT_SUCCESS
        ? (value == CNA_TRUE ? 1 : 0) : -(jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetBasicEffectBoolean(
    JNIEnv* environment, jclass type, jlong effect, jint kind, jboolean value)
{
    (void)environment;
    (void)type;
    if (value != JNI_FALSE && value != JNI_TRUE) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    GameSetBoolFunction function = basic_effect_bool_setter(kind);
    return function == NULL ? (jint)CNA_RESULT_INVALID_ARGUMENT
        : (jint)function(
            (CNA_Handle)effect, value == JNI_TRUE ? CNA_TRUE : CNA_FALSE);
}

static HandleGetFloatFunction basic_effect_float_getter(const jint kind)
{
    switch (kind) {
        case 0: return cna.basic_effect_get_specular_power;
        case 1: return cna.basic_effect_get_alpha;
        case 2: return cna.effect_fog_get_start;
        case 3: return cna.effect_fog_get_end;
        default: return NULL;
    }
}

static HandleSetFloatFunction basic_effect_float_setter(const jint kind)
{
    switch (kind) {
        case 0: return cna.basic_effect_set_specular_power;
        case 1: return cna.basic_effect_set_alpha;
        case 2: return cna.effect_fog_set_start;
        case 3: return cna.effect_fog_set_end;
        default: return NULL;
    }
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetBasicEffectFloat(
    JNIEnv* environment, jclass type, jlong effect, jint kind, jfloatArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) != 1) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    HandleGetFloatFunction function = basic_effect_float_getter(kind);
    if (function == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    float value = 0.0f;
    const CNA_Result result = function((CNA_Handle)effect, &value);
    if (result == CNA_RESULT_SUCCESS) {
        const jfloat projected = (jfloat)value;
        (*environment)->SetFloatArrayRegion(environment, output, 0, 1, &projected);
        if ((*environment)->ExceptionCheck(environment)) {
            return (jint)CNA_RESULT_INVALID_STATE;
        }
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetBasicEffectFloat(
    JNIEnv* environment, jclass type, jlong effect, jint kind, jfloat value)
{
    (void)environment;
    (void)type;
    HandleSetFloatFunction function = basic_effect_float_setter(kind);
    return function == NULL ? (jint)CNA_RESULT_INVALID_ARGUMENT
        : (jint)function((CNA_Handle)effect, (float)value);
}

static HandleGetVector3Function basic_effect_vector_getter(const jint kind)
{
    switch (kind) {
        case 0: return cna.basic_effect_get_diffuse_color;
        case 1: return cna.basic_effect_get_emissive_color;
        case 2: return cna.basic_effect_get_specular_color;
        case 3: return cna.effect_lights_get_ambient_color;
        case 4: return cna.effect_fog_get_color;
        default: return NULL;
    }
}

static HandleSetVector3Function basic_effect_vector_setter(const jint kind)
{
    switch (kind) {
        case 0: return cna.basic_effect_set_diffuse_color;
        case 1: return cna.basic_effect_set_emissive_color;
        case 2: return cna.basic_effect_set_specular_color;
        case 3: return cna.effect_lights_set_ambient_color;
        case 4: return cna.effect_fog_set_color;
        default: return NULL;
    }
}

static CNA_Result set_vector3_output(
    JNIEnv* environment, jfloatArray output, const CNA_Vector3 value)
{
    if (output == NULL || (*environment)->GetArrayLength(environment, output) != 3) {
        return CNA_RESULT_INVALID_ARGUMENT;
    }
    const jfloat projected[3] = {(jfloat)value.x, (jfloat)value.y, (jfloat)value.z};
    (*environment)->SetFloatArrayRegion(environment, output, 0, 3, projected);
    return (*environment)->ExceptionCheck(environment)
        ? CNA_RESULT_INVALID_STATE : CNA_RESULT_SUCCESS;
}

static CNA_Result get_vector3_input(
    JNIEnv* environment, jfloatArray input, CNA_Vector3* output)
{
    if (input == NULL || output == NULL ||
        (*environment)->GetArrayLength(environment, input) != 3) {
        return CNA_RESULT_INVALID_ARGUMENT;
    }
    jfloat values[3] = {0.0f, 0.0f, 0.0f};
    (*environment)->GetFloatArrayRegion(environment, input, 0, 3, values);
    if ((*environment)->ExceptionCheck(environment)) {
        return CNA_RESULT_INVALID_STATE;
    }
    output->x = values[0];
    output->y = values[1];
    output->z = values[2];
    return CNA_RESULT_SUCCESS;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetBasicEffectVector(
    JNIEnv* environment, jclass type, jlong effect, jint kind, jfloatArray output)
{
    (void)type;
    HandleGetVector3Function function = basic_effect_vector_getter(kind);
    if (function == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Vector3 value = {0.0f, 0.0f, 0.0f};
    CNA_Result result = function((CNA_Handle)effect, &value);
    if (result == CNA_RESULT_SUCCESS) {
        result = set_vector3_output(environment, output, value);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetBasicEffectVector(
    JNIEnv* environment, jclass type, jlong effect, jint kind, jfloatArray input)
{
    (void)type;
    HandleSetVector3Function function = basic_effect_vector_setter(kind);
    if (function == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Vector3 value = {0.0f, 0.0f, 0.0f};
    const CNA_Result result = get_vector3_input(environment, input, &value);
    return result == CNA_RESULT_SUCCESS
        ? (jint)function((CNA_Handle)effect, value) : (jint)result;
}

static HandleGetMatrixFunction basic_effect_matrix_getter(const jint kind)
{
    switch (kind) {
        case 0: return cna.effect_matrices_get_world;
        case 1: return cna.effect_matrices_get_view;
        case 2: return cna.effect_matrices_get_projection;
        default: return NULL;
    }
}

static HandleSetMatrixFunction basic_effect_matrix_setter(const jint kind)
{
    switch (kind) {
        case 0: return cna.effect_matrices_set_world;
        case 1: return cna.effect_matrices_set_view;
        case 2: return cna.effect_matrices_set_projection;
        default: return NULL;
    }
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetBasicEffectMatrix(
    JNIEnv* environment, jclass type, jlong effect, jint kind, jfloatArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) != 16) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    HandleGetMatrixFunction function = basic_effect_matrix_getter(kind);
    if (function == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Matrix value = {0};
    const CNA_Result result = function((CNA_Handle)effect, &value);
    if (result == CNA_RESULT_SUCCESS) {
        (*environment)->SetFloatArrayRegion(
            environment, output, 0, 16, (const jfloat*)&value);
        if ((*environment)->ExceptionCheck(environment)) {
            return (jint)CNA_RESULT_INVALID_STATE;
        }
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetBasicEffectMatrix(
    JNIEnv* environment, jclass type, jlong effect, jint kind, jfloatArray input)
{
    (void)type;
    if (input == NULL || (*environment)->GetArrayLength(environment, input) != 16) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    HandleSetMatrixFunction function = basic_effect_matrix_setter(kind);
    if (function == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Matrix value = {0};
    (*environment)->GetFloatArrayRegion(environment, input, 0, 16, (jfloat*)&value);
    return (*environment)->ExceptionCheck(environment)
        ? (jint)CNA_RESULT_INVALID_STATE : (jint)function((CNA_Handle)effect, value);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetBasicEffectDirectionalLight(
    JNIEnv* environment,
    jclass type,
    jlong effect,
    jint index,
    jlongArray output)
{
    (void)type;
    if (index < 0) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Handle light = CNA_INVALID_HANDLE;
    CNA_Result result = cna.effect_lights_get_directional_light(
        (CNA_Handle)effect, (uint32_t)index, &light);
    if (result == CNA_RESULT_SUCCESS) {
        result = set_handle_output(environment, output, light);
        if (result != CNA_RESULT_SUCCESS && light != CNA_INVALID_HANDLE) {
            (void)cna.directional_light_destroy(light);
        }
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeEnableDefaultLighting(
    JNIEnv* environment, jclass type, jlong effect)
{
    (void)environment;
    (void)type;
    return (jint)cna.effect_lights_enable_default((CNA_Handle)effect);
}

static HandleGetVector3Function directional_light_vector_getter(const jint kind)
{
    switch (kind) {
        case 0: return cna.directional_light_get_diffuse_color;
        case 1: return cna.directional_light_get_direction;
        case 2: return cna.directional_light_get_specular_color;
        default: return NULL;
    }
}

static HandleSetVector3Function directional_light_vector_setter(const jint kind)
{
    switch (kind) {
        case 0: return cna.directional_light_set_diffuse_color;
        case 1: return cna.directional_light_set_direction;
        case 2: return cna.directional_light_set_specular_color;
        default: return NULL;
    }
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetDirectionalLightVector(
    JNIEnv* environment, jclass type, jlong light, jint kind, jfloatArray output)
{
    (void)type;
    HandleGetVector3Function function = directional_light_vector_getter(kind);
    if (function == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Vector3 value = {0.0f, 0.0f, 0.0f};
    CNA_Result result = function((CNA_Handle)light, &value);
    if (result == CNA_RESULT_SUCCESS) {
        result = set_vector3_output(environment, output, value);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetDirectionalLightVector(
    JNIEnv* environment, jclass type, jlong light, jint kind, jfloatArray input)
{
    (void)type;
    HandleSetVector3Function function = directional_light_vector_setter(kind);
    if (function == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Vector3 value = {0.0f, 0.0f, 0.0f};
    const CNA_Result result = get_vector3_input(environment, input, &value);
    return result == CNA_RESULT_SUCCESS
        ? (jint)function((CNA_Handle)light, value) : (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetDirectionalLightEnabled(
    JNIEnv* environment, jclass type, jlong light)
{
    (void)environment;
    (void)type;
    CNA_Bool value = CNA_FALSE;
    const CNA_Result result = cna.directional_light_get_enabled((CNA_Handle)light, &value);
    return result == CNA_RESULT_SUCCESS
        ? (value == CNA_TRUE ? 1 : 0) : -(jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetDirectionalLightEnabled(
    JNIEnv* environment, jclass type, jlong light, jboolean value)
{
    (void)environment;
    (void)type;
    if (value != JNI_FALSE && value != JNI_TRUE) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    return (jint)cna.directional_light_set_enabled(
        (CNA_Handle)light, value == JNI_TRUE ? CNA_TRUE : CNA_FALSE);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetBasicEffectTexture(
    JNIEnv* environment, jclass type, jlong effect, jlong texture)
{
    (void)environment;
    (void)type;
    return (jint)cna.basic_effect_set_texture(
        (CNA_Handle)effect, (CNA_Handle)texture);
}

static GameGetBoolFunction stock_effect_bool_getter(
    const jint effect_kind, const jint kind)
{
    if (kind != 0) {
        return NULL;
    }
    switch (effect_kind) {
        case 0: return cna.alpha_test_effect_get_vertex_color_enabled;
        case 1: return cna.dual_texture_effect_get_vertex_color_enabled;
        case 3: return cna.skinned_effect_get_prefer_per_pixel_lighting;
        default: return NULL;
    }
}

static GameSetBoolFunction stock_effect_bool_setter(
    const jint effect_kind, const jint kind)
{
    if (kind != 0) {
        return NULL;
    }
    switch (effect_kind) {
        case 0: return cna.alpha_test_effect_set_vertex_color_enabled;
        case 1: return cna.dual_texture_effect_set_vertex_color_enabled;
        case 3: return cna.skinned_effect_set_prefer_per_pixel_lighting;
        default: return NULL;
    }
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetStockEffectBoolean(
    JNIEnv* environment,
    jclass type,
    jlong effect,
    jint effect_kind,
    jint kind)
{
    (void)environment;
    (void)type;
    GameGetBoolFunction function = stock_effect_bool_getter(effect_kind, kind);
    if (function == NULL) {
        return -(jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Bool value = CNA_FALSE;
    const CNA_Result result = function((CNA_Handle)effect, &value);
    return result == CNA_RESULT_SUCCESS
        ? (value == CNA_TRUE ? 1 : 0) : -(jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetStockEffectBoolean(
    JNIEnv* environment,
    jclass type,
    jlong effect,
    jint effect_kind,
    jint kind,
    jboolean value)
{
    (void)environment;
    (void)type;
    if (value != JNI_FALSE && value != JNI_TRUE) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    GameSetBoolFunction function = stock_effect_bool_setter(effect_kind, kind);
    return function == NULL ? (jint)CNA_RESULT_INVALID_ARGUMENT
        : (jint)function(
            (CNA_Handle)effect, value == JNI_TRUE ? CNA_TRUE : CNA_FALSE);
}

static HandleGetFloatFunction stock_effect_float_getter(
    const jint effect_kind, const jint kind)
{
    switch (effect_kind) {
        case 0: return kind == 0 ? cna.alpha_test_effect_get_alpha : NULL;
        case 1: return kind == 0 ? cna.dual_texture_effect_get_alpha : NULL;
        case 2:
            switch (kind) {
                case 0: return cna.environment_map_effect_get_alpha;
                case 1: return cna.environment_map_effect_get_amount;
                case 2: return cna.environment_map_effect_get_fresnel_factor;
                default: return NULL;
            }
        case 3:
            switch (kind) {
                case 0: return cna.skinned_effect_get_specular_power;
                case 1: return cna.skinned_effect_get_alpha;
                default: return NULL;
            }
        default: return NULL;
    }
}

static HandleSetFloatFunction stock_effect_float_setter(
    const jint effect_kind, const jint kind)
{
    switch (effect_kind) {
        case 0: return kind == 0 ? cna.alpha_test_effect_set_alpha : NULL;
        case 1: return kind == 0 ? cna.dual_texture_effect_set_alpha : NULL;
        case 2:
            switch (kind) {
                case 0: return cna.environment_map_effect_set_alpha;
                case 1: return cna.environment_map_effect_set_amount;
                case 2: return cna.environment_map_effect_set_fresnel_factor;
                default: return NULL;
            }
        case 3:
            switch (kind) {
                case 0: return cna.skinned_effect_set_specular_power;
                case 1: return cna.skinned_effect_set_alpha;
                default: return NULL;
            }
        default: return NULL;
    }
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetStockEffectFloat(
    JNIEnv* environment,
    jclass type,
    jlong effect,
    jint effect_kind,
    jint kind,
    jfloatArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) != 1) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    HandleGetFloatFunction function = stock_effect_float_getter(effect_kind, kind);
    if (function == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    float value = 0.0f;
    const CNA_Result result = function((CNA_Handle)effect, &value);
    if (result == CNA_RESULT_SUCCESS) {
        const jfloat projected = (jfloat)value;
        (*environment)->SetFloatArrayRegion(environment, output, 0, 1, &projected);
        if ((*environment)->ExceptionCheck(environment)) {
            return (jint)CNA_RESULT_INVALID_STATE;
        }
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetStockEffectFloat(
    JNIEnv* environment,
    jclass type,
    jlong effect,
    jint effect_kind,
    jint kind,
    jfloat value)
{
    (void)environment;
    (void)type;
    HandleSetFloatFunction function = stock_effect_float_setter(effect_kind, kind);
    return function == NULL ? (jint)CNA_RESULT_INVALID_ARGUMENT
        : (jint)function((CNA_Handle)effect, (float)value);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetStockEffectInt(
    JNIEnv* environment,
    jclass type,
    jlong effect,
    jint effect_kind,
    jint kind,
    jintArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) != 1) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    int32_t value = 0;
    CNA_Result result = CNA_RESULT_INVALID_ARGUMENT;
    if (effect_kind == 0 && kind == 0) {
        CNA_CompareFunction compare = CNA_COMPARE_ALWAYS;
        result = cna.alpha_test_effect_get_alpha_function((CNA_Handle)effect, &compare);
        value = (int32_t)compare;
    } else if (effect_kind == 0 && kind == 1) {
        result = cna.alpha_test_effect_get_reference_alpha((CNA_Handle)effect, &value);
    } else if (effect_kind == 3 && kind == 0) {
        result = cna.skinned_effect_get_weights_per_vertex((CNA_Handle)effect, &value);
    }
    if (result == CNA_RESULT_SUCCESS) {
        const jint projected = (jint)value;
        (*environment)->SetIntArrayRegion(environment, output, 0, 1, &projected);
        if ((*environment)->ExceptionCheck(environment)) {
            return (jint)CNA_RESULT_INVALID_STATE;
        }
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetStockEffectInt(
    JNIEnv* environment,
    jclass type,
    jlong effect,
    jint effect_kind,
    jint kind,
    jint value)
{
    (void)environment;
    (void)type;
    if (effect_kind == 0 && kind == 0) {
        if (value < (jint)CNA_COMPARE_ALWAYS ||
            value > (jint)CNA_COMPARE_NOT_EQUAL) {
            return (jint)CNA_RESULT_INVALID_ARGUMENT;
        }
        return (jint)cna.alpha_test_effect_set_alpha_function(
            (CNA_Handle)effect, (CNA_CompareFunction)value);
    }
    if (effect_kind == 0 && kind == 1) {
        return (jint)cna.alpha_test_effect_set_reference_alpha(
            (CNA_Handle)effect, (int32_t)value);
    }
    if (effect_kind == 3 && kind == 0) {
        return (jint)cna.skinned_effect_set_weights_per_vertex(
            (CNA_Handle)effect, (int32_t)value);
    }
    return (jint)CNA_RESULT_INVALID_ARGUMENT;
}

static HandleGetVector3Function stock_effect_vector_getter(
    const jint effect_kind, const jint kind)
{
    switch (effect_kind) {
        case 0: return kind == 0 ? cna.alpha_test_effect_get_diffuse_color : NULL;
        case 1: return kind == 0 ? cna.dual_texture_effect_get_diffuse_color : NULL;
        case 2:
            switch (kind) {
                case 0: return cna.environment_map_effect_get_diffuse_color;
                case 1: return cna.environment_map_effect_get_emissive_color;
                case 2: return cna.environment_map_effect_get_specular;
                default: return NULL;
            }
        case 3:
            switch (kind) {
                case 0: return cna.skinned_effect_get_diffuse_color;
                case 1: return cna.skinned_effect_get_emissive_color;
                case 2: return cna.skinned_effect_get_specular_color;
                default: return NULL;
            }
        default: return NULL;
    }
}

static HandleSetVector3Function stock_effect_vector_setter(
    const jint effect_kind, const jint kind)
{
    switch (effect_kind) {
        case 0: return kind == 0 ? cna.alpha_test_effect_set_diffuse_color : NULL;
        case 1: return kind == 0 ? cna.dual_texture_effect_set_diffuse_color : NULL;
        case 2:
            switch (kind) {
                case 0: return cna.environment_map_effect_set_diffuse_color;
                case 1: return cna.environment_map_effect_set_emissive_color;
                case 2: return cna.environment_map_effect_set_specular;
                default: return NULL;
            }
        case 3:
            switch (kind) {
                case 0: return cna.skinned_effect_set_diffuse_color;
                case 1: return cna.skinned_effect_set_emissive_color;
                case 2: return cna.skinned_effect_set_specular_color;
                default: return NULL;
            }
        default: return NULL;
    }
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetStockEffectVector(
    JNIEnv* environment,
    jclass type,
    jlong effect,
    jint effect_kind,
    jint kind,
    jfloatArray output)
{
    (void)type;
    HandleGetVector3Function function = stock_effect_vector_getter(effect_kind, kind);
    if (function == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Vector3 value = {0.0f, 0.0f, 0.0f};
    CNA_Result result = function((CNA_Handle)effect, &value);
    if (result == CNA_RESULT_SUCCESS) {
        result = set_vector3_output(environment, output, value);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetStockEffectVector(
    JNIEnv* environment,
    jclass type,
    jlong effect,
    jint effect_kind,
    jint kind,
    jfloatArray input)
{
    (void)type;
    HandleSetVector3Function function = stock_effect_vector_setter(effect_kind, kind);
    if (function == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Vector3 value = {0.0f, 0.0f, 0.0f};
    const CNA_Result result = get_vector3_input(environment, input, &value);
    return result == CNA_RESULT_SUCCESS
        ? (jint)function((CNA_Handle)effect, value) : (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetStockEffectTexture(
    JNIEnv* environment,
    jclass type,
    jlong effect,
    jint effect_kind,
    jint slot,
    jlong texture)
{
    (void)environment;
    (void)type;
    if (effect_kind == 0 && slot == 0) {
        return (jint)cna.alpha_test_effect_set_texture(
            (CNA_Handle)effect, (CNA_Handle)texture);
    }
    if (effect_kind == 1 && (slot == 0 || slot == 1)) {
        return (jint)cna.dual_texture_effect_set_texture(
            (CNA_Handle)effect, (uint32_t)slot, (CNA_Handle)texture);
    }
    if (effect_kind == 2 && slot == 0) {
        return (jint)cna.environment_map_effect_set_texture(
            (CNA_Handle)effect, (CNA_Handle)texture);
    }
    if (effect_kind == 2 && slot == 1) {
        return (jint)cna.environment_map_effect_set_environment_map(
            (CNA_Handle)effect, (CNA_Handle)texture);
    }
    if (effect_kind == 3 && slot == 0) {
        return (jint)cna.skinned_effect_set_texture(
            (CNA_Handle)effect, (CNA_Handle)texture);
    }
    return (jint)CNA_RESULT_INVALID_ARGUMENT;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetSkinnedEffectBoneTransforms(
    JNIEnv* environment,
    jclass type,
    jlong effect,
    jfloatArray input)
{
    (void)type;
    if (input == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    const jsize float_count = (*environment)->GetArrayLength(environment, input);
    if (float_count < 16 || float_count > 72 * 16 || float_count % 16 != 0) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Matrix transforms[72];
    (void)memset(transforms, 0, sizeof(transforms));
    (*environment)->GetFloatArrayRegion(
        environment, input, 0, float_count, (jfloat*)transforms);
    if ((*environment)->ExceptionCheck(environment)) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    return (jint)cna.skinned_effect_set_bone_transforms(
        (CNA_Handle)effect, transforms, (uint64_t)(float_count / 16));
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetSkinnedEffectBoneTransforms(
    JNIEnv* environment,
    jclass type,
    jlong effect,
    jint count,
    jfloatArray output)
{
    (void)type;
    if (count < 1 || count > 72 || output == NULL ||
        (*environment)->GetArrayLength(environment, output) != count * 16) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Matrix transforms[72];
    (void)memset(transforms, 0, sizeof(transforms));
    uint64_t copied = 0U;
    CNA_Result result = cna.skinned_effect_copy_bone_transforms(
        (CNA_Handle)effect, (uint64_t)count, transforms, (uint64_t)count, &copied);
    if (result == CNA_RESULT_SUCCESS && copied != (uint64_t)count) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    if (result == CNA_RESULT_SUCCESS) {
        (*environment)->SetFloatArrayRegion(
            environment, output, 0, count * 16, (const jfloat*)transforms);
        if ((*environment)->ExceptionCheck(environment)) {
            return (jint)CNA_RESULT_INVALID_STATE;
        }
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeCreateOcclusionQuery(
    JNIEnv* environment, jclass type, jlong game, jlongArray output)
{
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    CNA_Handle query = CNA_INVALID_HANDLE;
    result = cna.occlusion_query_create(device, &query);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    result = set_handle_output(environment, output, query);
    if (result != CNA_RESULT_SUCCESS) {
        (void)cna.occlusion_query_destroy(query);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeBeginOcclusionQuery(
    JNIEnv* environment, jclass type, jlong query)
{
    (void)environment;
    (void)type;
    return (jint)cna.occlusion_query_begin((CNA_Handle)query);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeEndOcclusionQuery(
    JNIEnv* environment, jclass type, jlong query)
{
    (void)environment;
    (void)type;
    return (jint)cna.occlusion_query_end((CNA_Handle)query);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetOcclusionQueryComplete(
    JNIEnv* environment, jclass type, jlong query)
{
    (void)environment;
    (void)type;
    CNA_Bool complete = CNA_FALSE;
    const CNA_Result result = cna.occlusion_query_get_is_complete(
        (CNA_Handle)query, &complete);
    return result == CNA_RESULT_SUCCESS
        ? (complete == CNA_TRUE ? 1 : 0) : -(jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetOcclusionQueryPixelCount(
    JNIEnv* environment, jclass type, jlong query, jintArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) != 1) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    int32_t pixel_count = 0;
    const CNA_Result result = cna.occlusion_query_get_pixel_count(
        (CNA_Handle)query, &pixel_count);
    if (result == CNA_RESULT_SUCCESS) {
        const jint projected = (jint)pixel_count;
        (*environment)->SetIntArrayRegion(environment, output, 0, 1, &projected);
        if ((*environment)->ExceptionCheck(environment)) {
            return (jint)CNA_RESULT_INVALID_STATE;
        }
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeDestroyOcclusionQuery(
    JNIEnv* environment, jclass type, jlong query)
{
    (void)environment;
    (void)type;
    return (jint)cna.occlusion_query_destroy((CNA_Handle)query);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeCreateTexture2D(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jint width,
    jint height,
    jboolean mip_map,
    jint format,
    jlongArray output)
{
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = cna.game_get_graphics_device(java_game(game)->cna_handle, &device);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    CNA_Texture2DCreateInfo info;
    (void)memset(&info, 0, sizeof(info));
    info.struct_size = (uint32_t)sizeof(info);
    info.struct_version = UINT32_C(1);
    info.width = (uint32_t)width;
    info.height = (uint32_t)height;
    info.mip_map = mip_map == JNI_TRUE ? CNA_TRUE : CNA_FALSE;
    info.format = (CNA_SurfaceFormat)format;
    CNA_Handle texture = CNA_INVALID_HANDLE;
    result = cna.texture2d_create(device, &info, &texture);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    result = set_handle_output(environment, output, texture);
    if (result != CNA_RESULT_SUCCESS) {
        (void)cna.texture2d_destroy(texture);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeCreateTexture2DFromEncoded(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jbyteArray encoded,
    jint width,
    jint height,
    jboolean zoom,
    jboolean resize,
    jlongArray output)
{
    (void)type;
    if (encoded == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = cna.game_get_graphics_device(java_game(game)->cna_handle, &device);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    const jsize count = (*environment)->GetArrayLength(environment, encoded);
    jbyte* bytes = (*environment)->GetByteArrayElements(environment, encoded, NULL);
    if (bytes == NULL) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    CNA_Texture2DDecodeInfo info;
    const CNA_Texture2DDecodeInfo* selected = NULL;
    if (resize == JNI_TRUE) {
        (void)memset(&info, 0, sizeof(info));
        info.struct_size = (uint32_t)sizeof(info);
        info.struct_version = UINT32_C(1);
        info.width = (uint32_t)width;
        info.height = (uint32_t)height;
        info.zoom = zoom == JNI_TRUE ? CNA_TRUE : CNA_FALSE;
        selected = &info;
    }
    CNA_Handle texture = CNA_INVALID_HANDLE;
    result = cna.texture2d_create_from_encoded_memory(
        device, (const uint8_t*)bytes, (uint64_t)count, selected, &texture);
    (*environment)->ReleaseByteArrayElements(environment, encoded, bytes, JNI_ABORT);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    result = set_handle_output(environment, output, texture);
    if (result != CNA_RESULT_SUCCESS) {
        (void)cna.texture2d_destroy(texture);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetTexture2DInfo(
    JNIEnv* environment, jclass type, jlong texture, jintArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) < 4) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Texture2DInfo info;
    (void)memset(&info, 0, sizeof(info));
    info.struct_size = (uint32_t)sizeof(info);
    info.struct_version = UINT32_C(1);
    CNA_Result result = cna.texture2d_get_info((CNA_Handle)texture, &info);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    if (info.width > (uint32_t)INT32_MAX || info.height > (uint32_t)INT32_MAX
        || info.level_count > (uint32_t)INT32_MAX || info.format > (uint32_t)INT32_MAX) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    const jint values[4] = {
        (jint)info.width, (jint)info.height, (jint)info.level_count, (jint)info.format
    };
    (*environment)->SetIntArrayRegion(environment, output, 0, 4, values);
    return (*environment)->ExceptionCheck(environment) ? (jint)CNA_RESULT_INVALID_STATE : 0;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetTexture2DData(
    JNIEnv* environment, jclass type, jlong texture, jintArray packed_colors)
{
    (void)type;
    if (packed_colors == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    const jsize count = (*environment)->GetArrayLength(environment, packed_colors);
    jint* packed = (*environment)->GetIntArrayElements(environment, packed_colors, NULL);
    if (packed == NULL) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    CNA_Color* colors = count == 0 ? NULL : (CNA_Color*)malloc((size_t)count * sizeof(CNA_Color));
    if (count != 0 && colors == NULL) {
        (*environment)->ReleaseIntArrayElements(environment, packed_colors, packed, JNI_ABORT);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    for (jsize index = 0; index < count; ++index) {
        colors[index] = color_from_packed(packed[index]);
    }
    CNA_Result result = cna.texture2d_set_data_rgba8(
        (CNA_Handle)texture, colors, (uint64_t)count);
    free(colors);
    (*environment)->ReleaseIntArrayElements(environment, packed_colors, packed, JNI_ABORT);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetTexture2DData(
    JNIEnv* environment, jclass type, jlong texture, jintArray packed_colors)
{
    (void)type;
    if (packed_colors == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    const jsize capacity = (*environment)->GetArrayLength(environment, packed_colors);
    CNA_Color* colors = capacity == 0
        ? NULL : (CNA_Color*)malloc((size_t)capacity * sizeof(CNA_Color));
    if (capacity != 0 && colors == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t written = 0U;
    CNA_Result result = cna.texture2d_get_data_rgba8(
        (CNA_Handle)texture, colors, (uint64_t)capacity, &written);
    if (result == CNA_RESULT_SUCCESS && written == (uint64_t)capacity) {
        jint* packed = capacity == 0 ? NULL : (jint*)malloc((size_t)capacity * sizeof(jint));
        if (capacity != 0 && packed == NULL) {
            result = CNA_RESULT_OUT_OF_MEMORY;
        } else {
            for (jsize index = 0; index < capacity; ++index) {
                packed[index] = packed_from_color(colors[index]);
            }
            if (capacity != 0) {
                (*environment)->SetIntArrayRegion(environment, packed_colors, 0, capacity, packed);
            }
            if ((*environment)->ExceptionCheck(environment)) {
                result = CNA_RESULT_INVALID_STATE;
            }
            free(packed);
        }
    } else if (result == CNA_RESULT_SUCCESS) {
        result = CNA_RESULT_INVALID_STATE;
    }
    free(colors);
    return (jint)result;
}

static CNA_Result texture_data_element_size(const jint data_type, size_t* const out_size)
{
    if (out_size == NULL) {
        return CNA_RESULT_INVALID_ARGUMENT;
    }
    switch ((uint32_t)data_type) {
        case CNA_TEXTURE_DATA_COLOR: *out_size = sizeof(CNA_Color); break;
        case CNA_TEXTURE_DATA_BGR565: *out_size = sizeof(CNA_PackedBgr565); break;
        case CNA_TEXTURE_DATA_BGRA5551: *out_size = sizeof(CNA_PackedBgra5551); break;
        case CNA_TEXTURE_DATA_BGRA4444: *out_size = sizeof(CNA_PackedBgra4444); break;
        case CNA_TEXTURE_DATA_BYTE: *out_size = sizeof(uint8_t); break;
        case CNA_TEXTURE_DATA_NORMALIZED_BYTE2:
            *out_size = sizeof(CNA_PackedNormalizedByte2);
            break;
        case CNA_TEXTURE_DATA_NORMALIZED_BYTE4:
            *out_size = sizeof(CNA_PackedNormalizedByte4);
            break;
        case CNA_TEXTURE_DATA_RGBA1010102:
            *out_size = sizeof(CNA_PackedRgba1010102);
            break;
        case CNA_TEXTURE_DATA_RG32: *out_size = sizeof(CNA_PackedRg32); break;
        case CNA_TEXTURE_DATA_RGBA64: *out_size = sizeof(CNA_PackedRgba64); break;
        case CNA_TEXTURE_DATA_ALPHA8: *out_size = sizeof(CNA_PackedAlpha8); break;
        case CNA_TEXTURE_DATA_SINGLE: *out_size = sizeof(float); break;
        case CNA_TEXTURE_DATA_VECTOR2: *out_size = sizeof(CNA_Vector2); break;
        case CNA_TEXTURE_DATA_VECTOR4: *out_size = sizeof(CNA_Vector4); break;
        case CNA_TEXTURE_DATA_HALF_SINGLE:
            *out_size = sizeof(CNA_PackedHalfSingle);
            break;
        case CNA_TEXTURE_DATA_HALF_VECTOR2:
            *out_size = sizeof(CNA_PackedHalfVector2);
            break;
        case CNA_TEXTURE_DATA_HALF_VECTOR4:
            *out_size = sizeof(CNA_PackedHalfVector4);
            break;
        case CNA_TEXTURE_DATA_USHORT: *out_size = sizeof(uint16_t); break;
        default: return CNA_RESULT_INVALID_ARGUMENT;
    }
    return CNA_RESULT_SUCCESS;
}

static CNA_Result make_texture2d_transfer(
    const jint level,
    const jboolean has_rectangle,
    const jint x,
    const jint y,
    const jint width,
    const jint height,
    const jint start_index,
    const jint element_count,
    CNA_Texture2DTransfer* const out_transfer)
{
    if (out_transfer == NULL || level < 0 || start_index < 0 || element_count < 0 ||
        (has_rectangle != JNI_FALSE && has_rectangle != JNI_TRUE)) {
        return CNA_RESULT_INVALID_ARGUMENT;
    }
    (void)memset(out_transfer, 0, sizeof(*out_transfer));
    out_transfer->struct_size = (uint32_t)sizeof(*out_transfer);
    out_transfer->struct_version = UINT32_C(1);
    out_transfer->level = (int32_t)level;
    out_transfer->has_rectangle = has_rectangle == JNI_TRUE ? CNA_TRUE : CNA_FALSE;
    out_transfer->rectangle = (CNA_Rectangle){
        (int32_t)x, (int32_t)y, (int32_t)width, (int32_t)height};
    out_transfer->start_index = (uint64_t)start_index;
    out_transfer->element_count = (uint64_t)element_count;
    return CNA_RESULT_SUCCESS;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetTexture2DTypedData(
    JNIEnv* environment,
    jclass type,
    jlong texture,
    jint data_type,
    jint level,
    jboolean has_rectangle,
    jint x,
    jint y,
    jint width,
    jint height,
    jint start_index,
    jint element_count,
    jbyteArray payload)
{
    (void)type;
    if (payload == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    size_t element_size = 0U;
    CNA_Result result = texture_data_element_size(data_type, &element_size);
    CNA_Texture2DTransfer transfer;
    if (result == CNA_RESULT_SUCCESS) {
        result = make_texture2d_transfer(
            level, has_rectangle, x, y, width, height,
            start_index, element_count, &transfer);
    }
    const jsize byte_count = (*environment)->GetArrayLength(environment, payload);
    if (result != CNA_RESULT_SUCCESS || byte_count < 0 ||
        (size_t)byte_count % element_size != 0U) {
        return (jint)(result == CNA_RESULT_SUCCESS ? CNA_RESULT_INVALID_ARGUMENT : result);
    }
    const uint64_t capacity = (uint64_t)((size_t)byte_count / element_size);
    if (transfer.start_index + transfer.element_count > capacity) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    void* const buffer = byte_count == 0 ? NULL : malloc((size_t)byte_count);
    if (byte_count != 0 && buffer == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    if (byte_count != 0) {
        (*environment)->GetByteArrayRegion(
            environment, payload, 0, byte_count, (jbyte*)buffer);
        if ((*environment)->ExceptionCheck(environment)) {
            free(buffer);
            return (jint)CNA_RESULT_INVALID_STATE;
        }
    }
    result = cna.texture2d_set_data(
        (CNA_Handle)texture, (CNA_TextureDataType)data_type,
        &transfer, buffer, capacity);
    free(buffer);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetTexture2DTypedData(
    JNIEnv* environment,
    jclass type,
    jlong texture,
    jint data_type,
    jint level,
    jboolean has_rectangle,
    jint x,
    jint y,
    jint width,
    jint height,
    jint start_index,
    jint element_count,
    jbyteArray payload)
{
    (void)type;
    if (payload == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    size_t element_size = 0U;
    CNA_Result result = texture_data_element_size(data_type, &element_size);
    CNA_Texture2DTransfer transfer;
    if (result == CNA_RESULT_SUCCESS) {
        result = make_texture2d_transfer(
            level, has_rectangle, x, y, width, height,
            start_index, element_count, &transfer);
    }
    const jsize byte_count = (*environment)->GetArrayLength(environment, payload);
    if (result != CNA_RESULT_SUCCESS || byte_count < 0 ||
        (size_t)byte_count % element_size != 0U) {
        return (jint)(result == CNA_RESULT_SUCCESS ? CNA_RESULT_INVALID_ARGUMENT : result);
    }
    const uint64_t capacity = (uint64_t)((size_t)byte_count / element_size);
    if (transfer.start_index + transfer.element_count > capacity) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    void* const buffer = byte_count == 0 ? NULL : calloc(1U, (size_t)byte_count);
    if (byte_count != 0 && buffer == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t written = 0U;
    result = cna.texture2d_get_data(
        (CNA_Handle)texture, (CNA_TextureDataType)data_type,
        &transfer, buffer, capacity, &written);
    if (result == CNA_RESULT_SUCCESS && written != transfer.element_count) {
        result = CNA_RESULT_INVALID_STATE;
    }
    if (result == CNA_RESULT_SUCCESS && byte_count != 0) {
        (*environment)->SetByteArrayRegion(
            environment, payload, 0, byte_count, (const jbyte*)buffer);
        if ((*environment)->ExceptionCheck(environment)) {
            result = CNA_RESULT_INVALID_STATE;
        }
    }
    free(buffer);
    return (jint)result;
}

JNIEXPORT jlong JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetTexture2DEncodedSize(
    JNIEnv* environment, jclass type, jlong texture, jint format, jint width, jint height)
{
    (void)environment;
    (void)type;
    uint64_t size = 0U;
    CNA_Result result = cna.texture2d_get_encoded_byte_count(
        (CNA_Handle)texture, (CNA_TextureImageFormat)format,
        (uint32_t)width, (uint32_t)height, &size);
    if (result != CNA_RESULT_SUCCESS) {
        return -(jlong)result;
    }
    return size > (uint64_t)INT64_MAX ? -(jlong)CNA_RESULT_INVALID_STATE : (jlong)size;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeCopyTexture2DEncoded(
    JNIEnv* environment,
    jclass type,
    jlong texture,
    jint format,
    jint width,
    jint height,
    jbyteArray output)
{
    (void)type;
    if (output == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    const jsize capacity = (*environment)->GetArrayLength(environment, output);
    jbyte* bytes = (*environment)->GetByteArrayElements(environment, output, NULL);
    if (bytes == NULL) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    uint64_t written = 0U;
    CNA_Result result = cna.texture2d_copy_encoded(
        (CNA_Handle)texture, (CNA_TextureImageFormat)format,
        (uint32_t)width, (uint32_t)height, (uint8_t*)bytes,
        (uint64_t)capacity, &written);
    (*environment)->ReleaseByteArrayElements(environment, output, bytes, 0);
    if (result == CNA_RESULT_SUCCESS && written != (uint64_t)capacity) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeDestroyTexture2D(
    JNIEnv* environment, jclass type, jlong texture)
{
    (void)environment;
    (void)type;
    return (jint)cna.texture2d_destroy((CNA_Handle)texture);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeCreateTexture3D(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jint width,
    jint height,
    jint depth,
    jboolean mip_map,
    jint format,
    jlongArray output)
{
    (void)type;
    if (width <= 0 || height <= 0 || depth <= 0 ||
        (mip_map != JNI_FALSE && mip_map != JNI_TRUE)) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    CNA_Texture3DCreateInfo info;
    (void)memset(&info, 0, sizeof(info));
    info.struct_size = (uint32_t)sizeof(info);
    info.struct_version = UINT32_C(1);
    info.width = (uint32_t)width;
    info.height = (uint32_t)height;
    info.depth = (uint32_t)depth;
    info.mip_map = mip_map == JNI_TRUE ? CNA_TRUE : CNA_FALSE;
    info.format = (CNA_SurfaceFormat)(uint32_t)format;
    CNA_Handle texture = CNA_INVALID_HANDLE;
    result = cna.texture3d_create(device, &info, &texture);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    result = set_handle_output(environment, output, texture);
    if (result != CNA_RESULT_SUCCESS) {
        (void)cna.texture3d_destroy(texture);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetTexture3DInfo(
    JNIEnv* environment, jclass type, jlong texture, jintArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) < 5) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Texture3DInfo info;
    (void)memset(&info, 0, sizeof(info));
    info.struct_size = (uint32_t)sizeof(info);
    info.struct_version = UINT32_C(1);
    CNA_Result result = cna.texture3d_get_info((CNA_Handle)texture, &info);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    if (info.width > (uint32_t)INT32_MAX || info.height > (uint32_t)INT32_MAX ||
        info.depth > (uint32_t)INT32_MAX || info.level_count > (uint32_t)INT32_MAX ||
        info.format > (uint32_t)INT32_MAX) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    const jint values[5] = {
        (jint)info.width, (jint)info.height, (jint)info.depth,
        (jint)info.level_count, (jint)info.format
    };
    (*environment)->SetIntArrayRegion(environment, output, 0, 5, values);
    return (*environment)->ExceptionCheck(environment)
        ? (jint)CNA_RESULT_INVALID_STATE : (jint)CNA_RESULT_SUCCESS;
}

static CNA_Result make_texture3d_transfer(
    const jint level,
    const jint left,
    const jint top,
    const jint right,
    const jint bottom,
    const jint front,
    const jint back,
    const jint start_index,
    const jint element_count,
    CNA_Texture3DTransfer* const out_transfer)
{
    if (out_transfer == NULL || level < 0 || start_index < 0 || element_count < 0) {
        return CNA_RESULT_INVALID_ARGUMENT;
    }
    (void)memset(out_transfer, 0, sizeof(*out_transfer));
    out_transfer->struct_size = (uint32_t)sizeof(*out_transfer);
    out_transfer->struct_version = UINT32_C(1);
    out_transfer->level = (int32_t)level;
    out_transfer->left = (int32_t)left;
    out_transfer->top = (int32_t)top;
    out_transfer->right = (int32_t)right;
    out_transfer->bottom = (int32_t)bottom;
    out_transfer->front = (int32_t)front;
    out_transfer->back = (int32_t)back;
    out_transfer->start_index = (uint64_t)start_index;
    out_transfer->element_count = (uint64_t)element_count;
    return CNA_RESULT_SUCCESS;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetTexture3DData(
    JNIEnv* environment,
    jclass type,
    jlong texture,
    jint level,
    jint left,
    jint top,
    jint right,
    jint bottom,
    jint front,
    jint back,
    jint start_index,
    jint element_count,
    jintArray packed_colors)
{
    (void)type;
    if (packed_colors == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Texture3DTransfer transfer;
    CNA_Result result = make_texture3d_transfer(
        level, left, top, right, bottom, front, back,
        start_index, element_count, &transfer);
    const jsize capacity = (*environment)->GetArrayLength(environment, packed_colors);
    if (result != CNA_RESULT_SUCCESS ||
        transfer.start_index + transfer.element_count > (uint64_t)capacity) {
        return (jint)(result == CNA_RESULT_SUCCESS ? CNA_RESULT_INVALID_ARGUMENT : result);
    }
    jint* packed = (*environment)->GetIntArrayElements(environment, packed_colors, NULL);
    if (packed == NULL) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    CNA_Color* colors = capacity == 0 ? NULL
        : (CNA_Color*)malloc((size_t)capacity * sizeof(CNA_Color));
    if (capacity != 0 && colors == NULL) {
        (*environment)->ReleaseIntArrayElements(
            environment, packed_colors, packed, JNI_ABORT);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    for (jsize index = 0; index < capacity; ++index) {
        colors[index] = color_from_packed(packed[index]);
    }
    result = cna.texture3d_set_data(
        (CNA_Handle)texture, &transfer, colors, (uint64_t)capacity);
    free(colors);
    (*environment)->ReleaseIntArrayElements(
        environment, packed_colors, packed, JNI_ABORT);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetTexture3DData(
    JNIEnv* environment,
    jclass type,
    jlong texture,
    jint level,
    jint left,
    jint top,
    jint right,
    jint bottom,
    jint front,
    jint back,
    jint start_index,
    jint element_count,
    jintArray packed_colors)
{
    (void)type;
    if (packed_colors == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_Texture3DTransfer transfer;
    CNA_Result result = make_texture3d_transfer(
        level, left, top, right, bottom, front, back,
        start_index, element_count, &transfer);
    const jsize capacity = (*environment)->GetArrayLength(environment, packed_colors);
    if (result != CNA_RESULT_SUCCESS ||
        transfer.start_index + transfer.element_count > (uint64_t)capacity) {
        return (jint)(result == CNA_RESULT_SUCCESS ? CNA_RESULT_INVALID_ARGUMENT : result);
    }
    CNA_Color* colors = capacity == 0 ? NULL
        : (CNA_Color*)calloc((size_t)capacity, sizeof(CNA_Color));
    if (capacity != 0 && colors == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t written = 0U;
    result = cna.texture3d_get_data(
        (CNA_Handle)texture, &transfer, colors, (uint64_t)capacity, &written);
    if (result == CNA_RESULT_SUCCESS && written != transfer.element_count) {
        result = CNA_RESULT_INVALID_STATE;
    }
    jint* packed = NULL;
    if (result == CNA_RESULT_SUCCESS && capacity != 0) {
        packed = (jint*)malloc((size_t)capacity * sizeof(jint));
        if (packed == NULL) {
            result = CNA_RESULT_OUT_OF_MEMORY;
        } else {
            for (jsize index = 0; index < capacity; ++index) {
                packed[index] = packed_from_color(colors[index]);
            }
            (*environment)->SetIntArrayRegion(
                environment, packed_colors, 0, capacity, packed);
            if ((*environment)->ExceptionCheck(environment)) {
                result = CNA_RESULT_INVALID_STATE;
            }
        }
    }
    free(packed);
    free(colors);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeDestroyTexture3D(
    JNIEnv* environment, jclass type, jlong texture)
{
    (void)environment;
    (void)type;
    return (jint)cna.texture3d_destroy((CNA_Handle)texture);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeCreateTextureCube(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jint size,
    jboolean mip_map,
    jint format,
    jlongArray output)
{
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = cna.game_get_graphics_device(java_game(game)->cna_handle, &device);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    CNA_TextureCubeCreateInfo info;
    (void)memset(&info, 0, sizeof(info));
    info.struct_size = (uint32_t)sizeof(info);
    info.struct_version = UINT32_C(1);
    info.size = (uint32_t)size;
    info.mip_map = mip_map == JNI_TRUE ? CNA_TRUE : CNA_FALSE;
    info.format = (CNA_SurfaceFormat)(uint32_t)format;
    CNA_Handle texture = CNA_INVALID_HANDLE;
    result = cna.texturecube_create(device, &info, &texture);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    result = set_handle_output(environment, output, texture);
    if (result != CNA_RESULT_SUCCESS) {
        (void)cna.texturecube_destroy(texture);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetTextureCubeInfo(
    JNIEnv* environment, jclass type, jlong texture, jintArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) < 3) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_TextureCubeInfo info;
    (void)memset(&info, 0, sizeof(info));
    info.struct_size = (uint32_t)sizeof(info);
    info.struct_version = UINT32_C(1);
    CNA_Result result = cna.texturecube_get_info((CNA_Handle)texture, &info);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    if (info.size > (uint32_t)INT32_MAX || info.level_count > (uint32_t)INT32_MAX ||
        info.format > (uint32_t)INT32_MAX) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    const jint values[3] = {
        (jint)info.size, (jint)info.level_count, (jint)info.format
    };
    (*environment)->SetIntArrayRegion(environment, output, 0, 3, values);
    return (*environment)->ExceptionCheck(environment)
        ? (jint)CNA_RESULT_INVALID_STATE : (jint)CNA_RESULT_SUCCESS;
}

static CNA_Result make_texturecube_transfer(
    const jint face,
    const jint level,
    const jboolean has_rectangle,
    const jint x,
    const jint y,
    const jint width,
    const jint height,
    const jint start_index,
    const jint element_count,
    CNA_TextureCubeTransfer* const out_transfer)
{
    if (out_transfer == NULL || face < 0 || level < 0 || start_index < 0 ||
        element_count < 0 ||
        (has_rectangle != JNI_FALSE && has_rectangle != JNI_TRUE)) {
        return CNA_RESULT_INVALID_ARGUMENT;
    }
    (void)memset(out_transfer, 0, sizeof(*out_transfer));
    out_transfer->struct_size = (uint32_t)sizeof(*out_transfer);
    out_transfer->struct_version = UINT32_C(1);
    out_transfer->face = (CNA_CubeMapFace)(uint32_t)face;
    out_transfer->level = (int32_t)level;
    out_transfer->has_rectangle = has_rectangle == JNI_TRUE ? CNA_TRUE : CNA_FALSE;
    out_transfer->rectangle = (CNA_Rectangle){
        (int32_t)x, (int32_t)y, (int32_t)width, (int32_t)height};
    out_transfer->start_index = (uint64_t)start_index;
    out_transfer->element_count = (uint64_t)element_count;
    return CNA_RESULT_SUCCESS;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetTextureCubeData(
    JNIEnv* environment,
    jclass type,
    jlong texture,
    jint face,
    jint level,
    jboolean has_rectangle,
    jint x,
    jint y,
    jint width,
    jint height,
    jint start_index,
    jint element_count,
    jintArray packed_colors)
{
    (void)type;
    if (packed_colors == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_TextureCubeTransfer transfer;
    CNA_Result result = make_texturecube_transfer(
        face, level, has_rectangle, x, y, width, height,
        start_index, element_count, &transfer);
    const jsize capacity = (*environment)->GetArrayLength(environment, packed_colors);
    if (result != CNA_RESULT_SUCCESS ||
        transfer.start_index + transfer.element_count > (uint64_t)capacity) {
        return (jint)(result == CNA_RESULT_SUCCESS ? CNA_RESULT_INVALID_ARGUMENT : result);
    }
    jint* packed = (*environment)->GetIntArrayElements(environment, packed_colors, NULL);
    if (packed == NULL) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    CNA_Color* colors = capacity == 0
        ? NULL : (CNA_Color*)malloc((size_t)capacity * sizeof(CNA_Color));
    if (capacity != 0 && colors == NULL) {
        (*environment)->ReleaseIntArrayElements(
            environment, packed_colors, packed, JNI_ABORT);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    for (jsize index = 0; index < capacity; ++index) {
        colors[index] = color_from_packed(packed[index]);
    }
    result = cna.texturecube_set_data(
        (CNA_Handle)texture, &transfer, colors, (uint64_t)capacity);
    free(colors);
    (*environment)->ReleaseIntArrayElements(
        environment, packed_colors, packed, JNI_ABORT);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetTextureCubeData(
    JNIEnv* environment,
    jclass type,
    jlong texture,
    jint face,
    jint level,
    jboolean has_rectangle,
    jint x,
    jint y,
    jint width,
    jint height,
    jint start_index,
    jint element_count,
    jintArray packed_colors)
{
    (void)type;
    if (packed_colors == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_TextureCubeTransfer transfer;
    CNA_Result result = make_texturecube_transfer(
        face, level, has_rectangle, x, y, width, height,
        start_index, element_count, &transfer);
    const jsize capacity = (*environment)->GetArrayLength(environment, packed_colors);
    if (result != CNA_RESULT_SUCCESS ||
        transfer.start_index + transfer.element_count > (uint64_t)capacity) {
        return (jint)(result == CNA_RESULT_SUCCESS ? CNA_RESULT_INVALID_ARGUMENT : result);
    }
    CNA_Color* colors = capacity == 0
        ? NULL : (CNA_Color*)calloc((size_t)capacity, sizeof(CNA_Color));
    if (capacity != 0 && colors == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t written = 0U;
    result = cna.texturecube_get_data(
        (CNA_Handle)texture, &transfer, colors, (uint64_t)capacity, &written);
    if (result == CNA_RESULT_SUCCESS && written != transfer.element_count) {
        result = CNA_RESULT_INVALID_STATE;
    }
    jint* packed = NULL;
    if (result == CNA_RESULT_SUCCESS && capacity != 0) {
        packed = (jint*)malloc((size_t)capacity * sizeof(jint));
        if (packed == NULL) {
            result = CNA_RESULT_OUT_OF_MEMORY;
        } else {
            for (jsize index = 0; index < capacity; ++index) {
                packed[index] = packed_from_color(colors[index]);
            }
            (*environment)->SetIntArrayRegion(
                environment, packed_colors, 0, capacity, packed);
            if ((*environment)->ExceptionCheck(environment)) {
                result = CNA_RESULT_INVALID_STATE;
            }
        }
    }
    free(packed);
    free(colors);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeDestroyTextureCube(
    JNIEnv* environment, jclass type, jlong texture)
{
    (void)environment;
    (void)type;
    return (jint)cna.texturecube_destroy((CNA_Handle)texture);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeCreateRenderTarget2D(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jint width,
    jint height,
    jboolean mip_map,
    jint format,
    jint depth_format,
    jint multi_sample_count,
    jint usage,
    jlongArray output)
{
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = cna.game_get_graphics_device(java_game(game)->cna_handle, &device);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    CNA_RenderTarget2DCreateInfo info;
    (void)memset(&info, 0, sizeof(info));
    info.struct_size = (uint32_t)sizeof(info);
    info.struct_version = UINT32_C(1);
    info.width = (uint32_t)width;
    info.height = (uint32_t)height;
    info.mip_map = mip_map == JNI_TRUE ? CNA_TRUE : CNA_FALSE;
    info.format = (CNA_SurfaceFormat)(uint32_t)format;
    info.depth_format = (CNA_DepthFormat)(uint32_t)depth_format;
    info.multi_sample_count = (int32_t)multi_sample_count;
    info.usage = (CNA_RenderTargetUsage)(uint32_t)usage;
    CNA_Handle render_target = CNA_INVALID_HANDLE;
    result = cna.render_target2d_create(device, &info, &render_target);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    result = set_handle_output(environment, output, render_target);
    if (result != CNA_RESULT_SUCCESS) {
        (void)cna.render_target_destroy(render_target);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeCreateRenderTargetCube(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jint size,
    jboolean mip_map,
    jint format,
    jint depth_format,
    jint multi_sample_count,
    jint usage,
    jlongArray output)
{
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = cna.game_get_graphics_device(java_game(game)->cna_handle, &device);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    CNA_RenderTargetCubeCreateInfo info;
    (void)memset(&info, 0, sizeof(info));
    info.struct_size = (uint32_t)sizeof(info);
    info.struct_version = UINT32_C(1);
    info.size = (uint32_t)size;
    info.mip_map = mip_map == JNI_TRUE ? CNA_TRUE : CNA_FALSE;
    info.format = (CNA_SurfaceFormat)(uint32_t)format;
    info.depth_format = (CNA_DepthFormat)(uint32_t)depth_format;
    info.multi_sample_count = (int32_t)multi_sample_count;
    info.usage = (CNA_RenderTargetUsage)(uint32_t)usage;
    CNA_Handle render_target = CNA_INVALID_HANDLE;
    result = cna.render_target_cube_create(device, &info, &render_target);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    result = set_handle_output(environment, output, render_target);
    if (result != CNA_RESULT_SUCCESS) {
        (void)cna.render_target_destroy(render_target);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetRenderTargetInfo(
    JNIEnv* environment, jclass type, jlong render_target, jintArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) < 10) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_RenderTargetInfo info;
    (void)memset(&info, 0, sizeof(info));
    info.struct_size = (uint32_t)sizeof(info);
    info.struct_version = UINT32_C(1);
    CNA_Result result = cna.render_target_get_info((CNA_Handle)render_target, &info);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    if (info.width > (uint32_t)INT32_MAX || info.height > (uint32_t)INT32_MAX ||
        info.level_count > (uint32_t)INT32_MAX || info.format > (uint32_t)INT32_MAX ||
        info.depth_format > (uint32_t)INT32_MAX || info.usage > (uint32_t)INT32_MAX ||
        info.kind > (uint32_t)INT32_MAX) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    const jint values[10] = {
        (jint)info.width,
        (jint)info.height,
        (jint)info.level_count,
        (jint)info.format,
        (jint)info.depth_format,
        (jint)info.multi_sample_count,
        (jint)info.usage,
        info.is_content_lost == CNA_TRUE ? 1 : 0,
        info.renderer_available == CNA_TRUE ? 1 : 0,
        (jint)info.kind
    };
    (*environment)->SetIntArrayRegion(environment, output, 0, 10, values);
    return (*environment)->ExceptionCheck(environment)
        ? (jint)CNA_RESULT_INVALID_STATE : (jint)CNA_RESULT_SUCCESS;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetGraphicsDeviceRenderTarget2D(
    JNIEnv* environment, jclass type, jlong game, jlong render_target)
{
    (void)environment;
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    return result == CNA_RESULT_SUCCESS
        ? (jint)cna.graphics_device_set_render_target2d(
            device, (CNA_Handle)render_target)
        : (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetGraphicsDeviceRenderTargetCube(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jlong render_target,
    jint face)
{
    (void)environment;
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    return result == CNA_RESULT_SUCCESS
        ? (jint)cna.graphics_device_set_render_target_cube(
            device, (CNA_Handle)render_target, (CNA_CubeMapFace)(uint32_t)face)
        : (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeSetGraphicsDeviceRenderTargets(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jlongArray render_targets,
    jintArray faces)
{
    (void)type;
    if (render_targets == NULL || faces == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    const jsize count = (*environment)->GetArrayLength(environment, render_targets);
    if ((*environment)->GetArrayLength(environment, faces) != count) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    jlong* handles = (*environment)->GetLongArrayElements(environment, render_targets, NULL);
    jint* face_values = (*environment)->GetIntArrayElements(environment, faces, NULL);
    if ((count != 0 && handles == NULL) || (count != 0 && face_values == NULL)) {
        if (handles != NULL) {
            (*environment)->ReleaseLongArrayElements(
                environment, render_targets, handles, JNI_ABORT);
        }
        if (face_values != NULL) {
            (*environment)->ReleaseIntArrayElements(
                environment, faces, face_values, JNI_ABORT);
        }
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    CNA_RenderTargetBinding* bindings = count == 0 ? NULL :
        (CNA_RenderTargetBinding*)calloc((size_t)count, sizeof(CNA_RenderTargetBinding));
    if (count != 0 && bindings == NULL) {
        (*environment)->ReleaseLongArrayElements(
            environment, render_targets, handles, JNI_ABORT);
        (*environment)->ReleaseIntArrayElements(
            environment, faces, face_values, JNI_ABORT);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    for (jsize index = 0; index < count; ++index) {
        bindings[index].struct_size = (uint32_t)sizeof(CNA_RenderTargetBinding);
        bindings[index].struct_version = UINT32_C(1);
        bindings[index].render_target = (CNA_Handle)handles[index];
        bindings[index].array_slice = 0;
        bindings[index].cube_map_face =
            (CNA_CubeMapFace)(uint32_t)face_values[index];
    }
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.graphics_device_set_render_targets(
            device, bindings, (uint64_t)count);
    }
    free(bindings);
    if (handles != NULL) {
        (*environment)->ReleaseLongArrayElements(
            environment, render_targets, handles, JNI_ABORT);
    }
    if (face_values != NULL) {
        (*environment)->ReleaseIntArrayElements(
            environment, faces, face_values, JNI_ABORT);
    }
    return (jint)result;
}

JNIEXPORT jlong JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeGetGraphicsDeviceRenderTargetCount(
    JNIEnv* environment, jclass type, jlong game)
{
    (void)environment;
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    uint64_t count = 0U;
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.graphics_device_get_render_target_count(device, &count);
    }
    if (result != CNA_RESULT_SUCCESS) {
        return -(jlong)result;
    }
    return count > (uint64_t)INT64_MAX
        ? -(jlong)CNA_RESULT_INVALID_STATE : (jlong)count;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeCopyGraphicsDeviceRenderTargets(
    JNIEnv* environment,
    jclass type,
    jlong game,
    jlongArray render_targets,
    jintArray faces)
{
    (void)type;
    if (render_targets == NULL || faces == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    const jsize capacity = (*environment)->GetArrayLength(environment, render_targets);
    if ((*environment)->GetArrayLength(environment, faces) != capacity) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_RenderTargetBinding* bindings = capacity == 0 ? NULL :
        (CNA_RenderTargetBinding*)calloc(
            (size_t)capacity, sizeof(CNA_RenderTargetBinding));
    if (capacity != 0 && bindings == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    for (jsize index = 0; index < capacity; ++index) {
        bindings[index].struct_size = (uint32_t)sizeof(CNA_RenderTargetBinding);
        bindings[index].struct_version = UINT32_C(1);
    }
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = graphics_device_from_game(game, &device);
    uint64_t count = 0U;
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.graphics_device_copy_render_targets(
            device, bindings, (uint64_t)capacity, &count);
    }
    if (result == CNA_RESULT_SUCCESS && count != (uint64_t)capacity) {
        result = CNA_RESULT_INVALID_STATE;
    }
    jlong* handles = NULL;
    jint* face_values = NULL;
    if (result == CNA_RESULT_SUCCESS && capacity != 0) {
        handles = (jlong*)malloc((size_t)capacity * sizeof(jlong));
        face_values = (jint*)malloc((size_t)capacity * sizeof(jint));
        if (handles == NULL || face_values == NULL) {
            result = CNA_RESULT_OUT_OF_MEMORY;
        } else {
            for (jsize index = 0; index < capacity; ++index) {
                handles[index] = (jlong)bindings[index].render_target;
                face_values[index] = (jint)bindings[index].cube_map_face;
            }
            (*environment)->SetLongArrayRegion(
                environment, render_targets, 0, capacity, handles);
            (*environment)->SetIntArrayRegion(
                environment, faces, 0, capacity, face_values);
            if ((*environment)->ExceptionCheck(environment)) {
                result = CNA_RESULT_INVALID_STATE;
            }
        }
    }
    free(handles);
    free(face_values);
    free(bindings);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeDestroyRenderTarget(
    JNIEnv* environment, jclass type, jlong render_target)
{
    (void)environment;
    (void)type;
    return (jint)cna.render_target_destroy((CNA_Handle)render_target);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeCreateSpriteBatch(
    JNIEnv* environment, jclass type, jlong game, jlongArray output)
{
    (void)type;
    CNA_Handle device = CNA_INVALID_HANDLE;
    CNA_Result result = cna.game_get_graphics_device(java_game(game)->cna_handle, &device);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    CNA_Handle sprite_batch = CNA_INVALID_HANDLE;
    result = cna.sprite_batch_create(device, &sprite_batch);
    if (result != CNA_RESULT_SUCCESS) {
        return (jint)result;
    }
    result = set_handle_output(environment, output, sprite_batch);
    if (result != CNA_RESULT_SUCCESS) {
        (void)cna.sprite_batch_destroy(sprite_batch);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeBeginSpriteBatch(
    JNIEnv* environment, jclass type, jlong sprite_batch, jint sort_mode)
{
    (void)environment;
    (void)type;
    CNA_SpriteBatchBeginInfo info;
    (void)memset(&info, 0, sizeof(info));
    info.struct_size = (uint32_t)sizeof(info);
    info.struct_version = UINT32_C(1);
    info.sort_mode = (CNA_SpriteSortMode)sort_mode;
    return (jint)cna.sprite_batch_begin((CNA_Handle)sprite_batch, &info);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeBeginSpriteBatchWithStates(
    JNIEnv* environment,
    jclass type,
    jlong sprite_batch,
    jint sort_mode,
    jintArray blend_input,
    jintArray sampler_input,
    jfloat sampler_bias,
    jintArray depth_input,
    jintArray rasterizer_input,
    jfloatArray rasterizer_float_input)
{
    (void)type;
    CNA_BlendState blend;
    CNA_SamplerState sampler;
    CNA_DepthStencilState depth;
    CNA_RasterizerState rasterizer;
    CNA_Result result = blend_state_from_java_array(environment, blend_input, &blend);
    if (result == CNA_RESULT_SUCCESS) {
        result = sampler_state_from_java_array(
            environment, sampler_input, sampler_bias, &sampler);
    }
    if (result == CNA_RESULT_SUCCESS) {
        result = depth_state_from_java_array(environment, depth_input, &depth);
    }
    if (result == CNA_RESULT_SUCCESS) {
        result = rasterizer_state_from_java_arrays(
            environment, rasterizer_input, rasterizer_float_input, &rasterizer);
    }
    return result == CNA_RESULT_SUCCESS
        ? (jint)cna.sprite_batch_begin_with_states(
            (CNA_Handle)sprite_batch,
            (CNA_SpriteSortMode)(uint32_t)sort_mode,
            &blend,
            &sampler,
            &depth,
            &rasterizer)
        : (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeBeginSpriteBatchWithEffect(
    JNIEnv* environment,
    jclass type,
    jlong sprite_batch,
    jint sort_mode,
    jintArray blend_input,
    jintArray sampler_input,
    jfloat sampler_bias,
    jintArray depth_input,
    jintArray rasterizer_input,
    jfloatArray rasterizer_float_input,
    jlong effect,
    jfloatArray transform_input)
{
    (void)type;
    CNA_BlendState blend;
    CNA_SamplerState sampler;
    CNA_DepthStencilState depth;
    CNA_RasterizerState rasterizer;
    CNA_Result result = blend_state_from_java_array(environment, blend_input, &blend);
    if (result == CNA_RESULT_SUCCESS) {
        result = sampler_state_from_java_array(
            environment, sampler_input, sampler_bias, &sampler);
    }
    if (result == CNA_RESULT_SUCCESS) {
        result = depth_state_from_java_array(environment, depth_input, &depth);
    }
    if (result == CNA_RESULT_SUCCESS) {
        result = rasterizer_state_from_java_arrays(
            environment, rasterizer_input, rasterizer_float_input, &rasterizer);
    }
    CNA_Matrix transform;
    const CNA_Matrix* transform_pointer = NULL;
    if (result == CNA_RESULT_SUCCESS && transform_input != NULL) {
        if ((*environment)->GetArrayLength(environment, transform_input) != 16) {
            result = CNA_RESULT_INVALID_ARGUMENT;
        } else {
            jfloat values[16];
            (*environment)->GetFloatArrayRegion(
                environment, transform_input, 0, 16, values);
            if ((*environment)->ExceptionCheck(environment)) {
                result = CNA_RESULT_INVALID_STATE;
            } else {
                transform = (CNA_Matrix){
                    values[0], values[1], values[2], values[3],
                    values[4], values[5], values[6], values[7],
                    values[8], values[9], values[10], values[11],
                    values[12], values[13], values[14], values[15]};
                transform_pointer = &transform;
            }
        }
    }
    return result == CNA_RESULT_SUCCESS
        ? (jint)cna.sprite_batch_begin_with_effect(
            (CNA_Handle)sprite_batch,
            (CNA_SpriteSortMode)(uint32_t)sort_mode,
            &blend,
            &sampler,
            &depth,
            &rasterizer,
            (CNA_Handle)effect,
            transform_pointer)
        : (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeDrawSpriteRectangle(
    JNIEnv* environment,
    jclass type,
    jlong sprite_batch,
    jlong texture,
    jint destination_x,
    jint destination_y,
    jint destination_width,
    jint destination_height,
    jint source_x,
    jint source_y,
    jint source_width,
    jint source_height,
    jint packed_color,
    jfloat rotation,
    jfloat origin_x,
    jfloat origin_y,
    jint effects,
    jfloat layer_depth)
{
    (void)environment;
    (void)type;
    CNA_SpriteCommand command;
    (void)memset(&command, 0, sizeof(command));
    command.struct_size = (uint32_t)sizeof(command);
    command.struct_version = UINT32_C(1);
    command.texture = (CNA_Handle)texture;
    command.destination = (CNA_Rectangle){
        (int32_t)destination_x, (int32_t)destination_y,
        (int32_t)destination_width, (int32_t)destination_height
    };
    command.source = (CNA_Rectangle){
        (int32_t)source_x, (int32_t)source_y, (int32_t)source_width, (int32_t)source_height
    };
    command.color = color_from_packed(packed_color);
    command.rotation = (float)rotation;
    command.origin = (CNA_Vector2){(float)origin_x, (float)origin_y};
    command.effects = (CNA_SpriteEffects)effects;
    command.layer_depth = (float)layer_depth;
    return (jint)cna.sprite_batch_submit_many((CNA_Handle)sprite_batch, &command, UINT64_C(1));
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeDrawSpriteScaled(
    JNIEnv* environment,
    jclass type,
    jlong sprite_batch,
    jlong texture,
    jfloat position_x,
    jfloat position_y,
    jint source_x,
    jint source_y,
    jint source_width,
    jint source_height,
    jint packed_color,
    jfloat rotation,
    jfloat origin_x,
    jfloat origin_y,
    jfloat scale_x,
    jfloat scale_y,
    jint effects,
    jfloat layer_depth)
{
    (void)environment;
    (void)type;
    CNA_SpriteScaledCommand command;
    (void)memset(&command, 0, sizeof(command));
    command.struct_size = (uint32_t)sizeof(command);
    command.struct_version = UINT32_C(1);
    command.texture = (CNA_Handle)texture;
    command.position = (CNA_Vector2){(float)position_x, (float)position_y};
    command.source = (CNA_Rectangle){
        (int32_t)source_x, (int32_t)source_y, (int32_t)source_width, (int32_t)source_height
    };
    command.color = color_from_packed(packed_color);
    command.rotation = (float)rotation;
    command.origin = (CNA_Vector2){(float)origin_x, (float)origin_y};
    command.scale = (CNA_Vector2){(float)scale_x, (float)scale_y};
    command.effects = (CNA_SpriteEffects)effects;
    command.layer_depth = (float)layer_depth;
    return (jint)cna.sprite_batch_submit_scaled_many(
        (CNA_Handle)sprite_batch, &command, UINT64_C(1));
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeDrawSpriteString(
    JNIEnv* environment,
    jclass type,
    jlong sprite_batch,
    jlong sprite_font,
    jbyteArray text,
    jfloat position_x,
    jfloat position_y,
    jint packed_color,
    jfloat rotation,
    jfloat origin_x,
    jfloat origin_y,
    jfloat scale_x,
    jfloat scale_y,
    jint effects,
    jfloat layer_depth)
{
    (void)type;
    if (text == NULL) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    const jsize byte_count = (*environment)->GetArrayLength(environment, text);
    jbyte* bytes = (*environment)->GetByteArrayElements(environment, text, NULL);
    if (bytes == NULL) {
        return (jint)CNA_RESULT_INVALID_STATE;
    }
    CNA_SpriteTextCommand command;
    (void)memset(&command, 0, sizeof(command));
    command.struct_size = (uint32_t)sizeof(command);
    command.struct_version = UINT32_C(1);
    command.sprite_font = (CNA_Handle)sprite_font;
    command.text = (CNA_StringView){(const char*)bytes, (uint64_t)byte_count};
    command.position = (CNA_Vector2){(float)position_x, (float)position_y};
    command.color = color_from_packed(packed_color);
    command.rotation = (float)rotation;
    command.origin = (CNA_Vector2){(float)origin_x, (float)origin_y};
    command.scale = (CNA_Vector2){(float)scale_x, (float)scale_y};
    command.effects = (CNA_SpriteEffects)effects;
    command.layer_depth = (float)layer_depth;
    CNA_Result result = cna.sprite_batch_draw_string(
        (CNA_Handle)sprite_batch, &command);
    (*environment)->ReleaseByteArrayElements(environment, text, bytes, JNI_ABORT);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeEndSpriteBatch(
    JNIEnv* environment, jclass type, jlong sprite_batch)
{
    (void)environment;
    (void)type;
    return (jint)cna.sprite_batch_end((CNA_Handle)sprite_batch);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeDestroySpriteBatch(
    JNIEnv* environment, jclass type, jlong sprite_batch)
{
    (void)environment;
    (void)type;
    return (jint)cna.sprite_batch_destroy((CNA_Handle)sprite_batch);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeDestroyGraphicsDeviceManager(
    JNIEnv* environment, jclass type, jlong manager)
{
    (void)type;
    JavaGraphicsDeviceManager* wrapper = java_graphics_device_manager(manager);
    atomic_store_explicit(
        &wrapper->context->callbacks_enabled, 0, memory_order_release);
    if (wrapper->preparing_registration != CNA_INVALID_HANDLE) {
        CNA_Result unsubscribe_result =
            cna.game_unsubscribe(wrapper->preparing_registration);
        if (unsubscribe_result != CNA_RESULT_SUCCESS) {
            atomic_store_explicit(
                &wrapper->context->callbacks_enabled, 1, memory_order_release);
            return (jint)unsubscribe_result;
        }
        wrapper->preparing_registration = CNA_INVALID_HANDLE;
    }
    for (size_t index = 0U; index < 5U; ++index) {
        if (wrapper->registrations[index] == CNA_INVALID_HANDLE) {
            continue;
        }
        CNA_Result unsubscribe_result =
            cna.game_unsubscribe(wrapper->registrations[index]);
        if (unsubscribe_result != CNA_RESULT_SUCCESS) {
            atomic_store_explicit(
                &wrapper->context->callbacks_enabled, 1, memory_order_release);
            return (jint)unsubscribe_result;
        }
        wrapper->registrations[index] = CNA_INVALID_HANDLE;
    }
    CNA_Result result = cna.graphics_device_manager_destroy(wrapper->cna_handle);
    if (result == CNA_RESULT_SUCCESS) {
        (*environment)->DeleteGlobalRef(environment, wrapper->context->manager);
        free(wrapper->context);
        free(wrapper);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeBindings_nativeDestroyGame(
    JNIEnv* environment, jclass type, jlong game)
{
    (void)type;
    JavaGame* wrapper = java_game(game);
    atomic_store_explicit(
        &wrapper->context->callbacks_enabled, 0, memory_order_release);
    for (size_t index = 0U; index < 6U; ++index) {
        if (wrapper->graphics_device_registrations[index] == CNA_INVALID_HANDLE) {
            continue;
        }
        CNA_Result unsubscribe_result = cna.graphics_device_unsubscribe(
            wrapper->graphics_device_registrations[index]);
        if (unsubscribe_result != CNA_RESULT_SUCCESS) {
            atomic_store_explicit(
                &wrapper->context->callbacks_enabled, 1, memory_order_release);
            return (jint)unsubscribe_result;
        }
        wrapper->graphics_device_registrations[index] = CNA_INVALID_HANDLE;
    }
    for (size_t index = 0U; index < 3U; ++index) {
        if (wrapper->window_registrations[index] == CNA_INVALID_HANDLE) {
            continue;
        }
        CNA_Result unsubscribe_result =
            cna.game_unsubscribe(wrapper->window_registrations[index]);
        if (unsubscribe_result != CNA_RESULT_SUCCESS) {
            atomic_store_explicit(
                &wrapper->context->callbacks_enabled, 1, memory_order_release);
            return (jint)unsubscribe_result;
        }
        wrapper->window_registrations[index] = CNA_INVALID_HANDLE;
    }
    CNA_Result result = cna.game_destroy(wrapper->cna_handle);
    if (result == CNA_RESULT_SUCCESS || result == CNA_RESULT_CALLBACK) {
        if (wrapper->context->graphics_device != NULL) {
            (*environment)->DeleteGlobalRef(
                environment, wrapper->context->graphics_device);
        }
        (*environment)->DeleteGlobalRef(environment, wrapper->context->game);
        free(wrapper->context);
        free(wrapper);
    }
    return (jint)result;
}

static CNA_Result audio_set_long(JNIEnv* environment, jlongArray output, uint64_t value)
{
    if (output == NULL || (*environment)->GetArrayLength(environment, output) < 1) {
        return CNA_RESULT_INVALID_ARGUMENT;
    }
    const jlong projected = (jlong)value;
    (*environment)->SetLongArrayRegion(environment, output, 0, 1, &projected);
    return (*environment)->ExceptionCheck(environment)
        ? CNA_RESULT_INVALID_STATE : CNA_RESULT_SUCCESS;
}

static CNA_Result audio_set_float(JNIEnv* environment, jfloatArray output, float value)
{
    if (output == NULL || (*environment)->GetArrayLength(environment, output) < 1) {
        return CNA_RESULT_INVALID_ARGUMENT;
    }
    const jfloat projected = (jfloat)value;
    (*environment)->SetFloatArrayRegion(environment, output, 0, 1, &projected);
    return (*environment)->ExceptionCheck(environment)
        ? CNA_RESULT_INVALID_STATE : CNA_RESULT_SUCCESS;
}

static CNA_Result audio_string_view(
    JNIEnv* environment, jbyteArray input, jbyte** out_bytes, CNA_StringView* out_view)
{
    if (input == NULL || out_bytes == NULL || out_view == NULL) {
        return CNA_RESULT_INVALID_ARGUMENT;
    }
    const jsize length = (*environment)->GetArrayLength(environment, input);
    jbyte* bytes = (*environment)->GetByteArrayElements(environment, input, NULL);
    if (bytes == NULL && length != 0) {
        return CNA_RESULT_OUT_OF_MEMORY;
    }
    *out_bytes = bytes;
    *out_view = (CNA_StringView){(const char*)bytes, (uint64_t)length};
    return CNA_RESULT_SUCCESS;
}

static CNA_Result audio_listener_from_floats(
    JNIEnv* environment, jfloatArray input, jsize offset, CNA_AudioListener* output)
{
    if (input == NULL || output == NULL || offset < 0 ||
        (*environment)->GetArrayLength(environment, input) < offset + 12) {
        return CNA_RESULT_INVALID_ARGUMENT;
    }
    jfloat values[12];
    (*environment)->GetFloatArrayRegion(environment, input, offset, 12, values);
    if ((*environment)->ExceptionCheck(environment)) {
        return CNA_RESULT_INVALID_STATE;
    }
    (void)memset(output, 0, sizeof(*output));
    output->struct_size = (uint32_t)sizeof(*output);
    output->struct_version = UINT32_C(1);
    output->forward = (CNA_Vector3){values[0], values[1], values[2]};
    output->position = (CNA_Vector3){values[3], values[4], values[5]};
    output->up = (CNA_Vector3){values[6], values[7], values[8]};
    output->velocity = (CNA_Vector3){values[9], values[10], values[11]};
    return CNA_RESULT_SUCCESS;
}

static CNA_Result audio_emitter_from_floats(
    JNIEnv* environment, jfloatArray input, CNA_AudioEmitter* output)
{
    if (input == NULL || output == NULL ||
        (*environment)->GetArrayLength(environment, input) < 13) {
        return CNA_RESULT_INVALID_ARGUMENT;
    }
    jfloat values[13];
    (*environment)->GetFloatArrayRegion(environment, input, 0, 13, values);
    if ((*environment)->ExceptionCheck(environment)) {
        return CNA_RESULT_INVALID_STATE;
    }
    (void)memset(output, 0, sizeof(*output));
    output->struct_size = (uint32_t)sizeof(*output);
    output->struct_version = UINT32_C(1);
    output->doppler_scale = values[0];
    output->forward = (CNA_Vector3){values[1], values[2], values[3]};
    output->position = (CNA_Vector3){values[4], values[5], values[6]};
    output->up = (CNA_Vector3){values[7], values[8], values[9]};
    output->velocity = (CNA_Vector3){values[10], values[11], values[12]};
    return CNA_RESULT_SUCCESS;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeCreateSoundEffect(
    JNIEnv* environment, jclass type, jlong game, jbyteArray data, jint offset,
    jint count, jint sample_rate, jint channels, jint loop_start, jint loop_length,
    jlongArray output)
{
    (void)type;
    if (data == NULL) return (jint)CNA_RESULT_INVALID_ARGUMENT;
    const jsize byte_count = (*environment)->GetArrayLength(environment, data);
    jbyte* bytes = (*environment)->GetByteArrayElements(environment, data, NULL);
    if (bytes == NULL) return (jint)CNA_RESULT_OUT_OF_MEMORY;
    CNA_SoundEffectCreateInfo info;
    (void)memset(&info, 0, sizeof(info));
    info.struct_size = (uint32_t)sizeof(info);
    info.struct_version = UINT32_C(1);
    info.sample_rate = (uint32_t)sample_rate;
    info.channels = (CNA_AudioChannels)channels;
    CNA_Handle handle = CNA_INVALID_HANDLE;
    CNA_Result result = cna.sound_effect_create_pcm16_range_ext(
        java_game(game)->cna_handle, &info, (const uint8_t*)bytes, (uint64_t)byte_count,
        offset, count, loop_start, loop_length, &handle);
    (*environment)->ReleaseByteArrayElements(environment, data, bytes, JNI_ABORT);
    if (result == CNA_RESULT_SUCCESS) result = audio_set_long(environment, output, handle);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeCreateSoundEffectEncoded(
    JNIEnv* environment, jclass type, jlong game, jbyteArray data, jlongArray output)
{
    (void)type;
    if (data == NULL) return (jint)CNA_RESULT_INVALID_ARGUMENT;
    const jsize count = (*environment)->GetArrayLength(environment, data);
    jbyte* bytes = (*environment)->GetByteArrayElements(environment, data, NULL);
    if (bytes == NULL) return (jint)CNA_RESULT_OUT_OF_MEMORY;
    CNA_Handle handle = CNA_INVALID_HANDLE;
    CNA_Result result = cna.sound_effect_create_from_encoded_ext(
        java_game(game)->cna_handle, (const uint8_t*)bytes, (uint64_t)count, &handle);
    (*environment)->ReleaseByteArrayElements(environment, data, bytes, JNI_ABORT);
    if (result == CNA_RESULT_SUCCESS) result = audio_set_long(environment, output, handle);
    return (jint)result;
}

#define AUDIO_UNARY_JNI(java_name, field_name) \
JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_##java_name( \
    JNIEnv* environment, jclass type, jlong handle) \
{ \
    (void)environment; (void)type; \
    return (jint)cna.field_name((CNA_Handle)handle); \
}

AUDIO_UNARY_JNI(nativeDestroySoundEffect, sound_effect_destroy)
AUDIO_UNARY_JNI(nativeDestroySoundEffectInstance, sound_effect_instance_destroy)
AUDIO_UNARY_JNI(nativeDestroyAudioEngine, audio_engine_destroy)
AUDIO_UNARY_JNI(nativeDestroyCategory, audio_category_destroy)
AUDIO_UNARY_JNI(nativeUpdateAudioEngine, audio_engine_update)
AUDIO_UNARY_JNI(nativeDestroyWaveBank, wave_bank_destroy)
AUDIO_UNARY_JNI(nativeDestroySoundBank, sound_bank_destroy)
AUDIO_UNARY_JNI(nativeDestroyCue, cue_destroy)

#undef AUDIO_UNARY_JNI

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeCreateSoundEffectInstance(
    JNIEnv* environment, jclass type, jlong effect, jlongArray output)
{
    (void)type;
    CNA_Handle handle = CNA_INVALID_HANDLE;
    CNA_Result result = cna.sound_effect_create_instance((CNA_Handle)effect, &handle);
    if (result == CNA_RESULT_SUCCESS) result = audio_set_long(environment, output, handle);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativePlaySoundEffect(
    JNIEnv* environment, jclass type, jlong effect, jfloat volume, jfloat pitch,
    jfloat pan, jboolean settings)
{
    (void)environment; (void)type;
    CNA_Bool played = CNA_FALSE;
    CNA_Result result = settings == JNI_TRUE
        ? cna.sound_effect_play_with_settings(
            (CNA_Handle)effect, volume, pitch, pan, &played)
        : cna.sound_effect_play((CNA_Handle)effect, &played);
    return result == CNA_RESULT_SUCCESS
        ? (played == CNA_TRUE ? 1 : 0) : -(jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeGetSoundEffectDuration(
    JNIEnv* environment, jclass type, jlong effect, jlongArray output)
{
    (void)type;
    int64_t ticks = 0;
    CNA_Result result = cna.sound_effect_get_duration_ticks((CNA_Handle)effect, &ticks);
    if (result == CNA_RESULT_SUCCESS) result = audio_set_long(environment, output, (uint64_t)ticks);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeSetSoundEffectName(
    JNIEnv* environment, jclass type, jlong effect, jbyteArray name)
{
    (void)type;
    jbyte* bytes = NULL;
    CNA_StringView view;
    CNA_Result result = audio_string_view(environment, name, &bytes, &view);
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.sound_effect_set_name((CNA_Handle)effect, view);
        (*environment)->ReleaseByteArrayElements(environment, name, bytes, JNI_ABORT);
    }
    return (jint)result;
}

static HandleGetFloatFunction audio_setting_getter(jint kind)
{
    switch (kind) {
        case 0: return cna.sound_effect_get_master_volume;
        case 1: return cna.sound_effect_get_distance_scale;
        case 2: return cna.sound_effect_get_doppler_scale;
        case 3: return cna.sound_effect_get_speed_of_sound;
        default: return NULL;
    }
}

static HandleSetFloatFunction audio_setting_setter(jint kind)
{
    switch (kind) {
        case 0: return cna.sound_effect_set_master_volume;
        case 1: return cna.sound_effect_set_distance_scale;
        case 2: return cna.sound_effect_set_doppler_scale;
        case 3: return cna.sound_effect_set_speed_of_sound;
        default: return NULL;
    }
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeGetSoundSetting(
    JNIEnv* environment, jclass type, jlong game, jint kind, jfloatArray output)
{
    (void)type;
    HandleGetFloatFunction function = audio_setting_getter(kind);
    if (function == NULL) return (jint)CNA_RESULT_INVALID_ARGUMENT;
    float value = 0.0f;
    CNA_Result result = function(java_game(game)->cna_handle, &value);
    if (result == CNA_RESULT_SUCCESS) result = audio_set_float(environment, output, value);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeSetSoundSetting(
    JNIEnv* environment, jclass type, jlong game, jint kind, jfloat value)
{
    (void)environment; (void)type;
    HandleSetFloatFunction function = audio_setting_setter(kind);
    return function == NULL ? (jint)CNA_RESULT_INVALID_ARGUMENT
        : (jint)function(java_game(game)->cna_handle, value);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeInstanceTransport(
    JNIEnv* environment, jclass type, jlong instance, jint operation, jboolean immediate)
{
    (void)environment; (void)type;
    switch (operation) {
        case 0: return (jint)cna.sound_effect_instance_play((CNA_Handle)instance);
        case 1: return (jint)cna.sound_effect_instance_pause((CNA_Handle)instance);
        case 2: return (jint)cna.sound_effect_instance_resume((CNA_Handle)instance);
        case 3: return (jint)cna.sound_effect_instance_stop(
            (CNA_Handle)instance, immediate == JNI_TRUE ? CNA_TRUE : CNA_FALSE);
        default: return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeSetInstanceFloat(
    JNIEnv* environment, jclass type, jlong instance, jint kind, jfloat value)
{
    (void)environment; (void)type;
    HandleSetFloatFunction function = kind == 0 ? cna.sound_effect_instance_set_volume
        : kind == 1 ? cna.sound_effect_instance_set_pitch
        : kind == 2 ? cna.sound_effect_instance_set_pan : NULL;
    return function == NULL ? (jint)CNA_RESULT_INVALID_ARGUMENT
        : (jint)function((CNA_Handle)instance, value);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeSetInstanceBoolean(
    JNIEnv* environment, jclass type, jlong instance, jboolean value)
{
    (void)environment; (void)type;
    return (jint)cna.sound_effect_instance_set_is_looped(
        (CNA_Handle)instance, value == JNI_TRUE ? CNA_TRUE : CNA_FALSE);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeGetInstanceState(
    JNIEnv* environment, jclass type, jlong instance, jintArray output)
{
    (void)type;
    CNA_SoundEffectInstanceInfo info;
    (void)memset(&info, 0, sizeof(info));
    info.struct_size = (uint32_t)sizeof(info);
    info.struct_version = UINT32_C(1);
    CNA_Result result = cna.sound_effect_instance_get_info((CNA_Handle)instance, &info);
    if (result == CNA_RESULT_SUCCESS) result = set_int_output(environment, output, (int32_t)info.state);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeApply3D(
    JNIEnv* environment, jclass type, jlong instance,
    jfloatArray listeners, jfloatArray emitter_values)
{
    (void)type;
    if (listeners == NULL || (*environment)->GetArrayLength(environment, listeners) < 12 ||
        ((*environment)->GetArrayLength(environment, listeners) % 12) != 0) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_AudioEmitter emitter;
    CNA_Result result = audio_emitter_from_floats(environment, emitter_values, &emitter);
    const jsize count = (*environment)->GetArrayLength(environment, listeners) / 12;
    CNA_AudioListener* values = NULL;
    if (result == CNA_RESULT_SUCCESS) {
        values = (CNA_AudioListener*)calloc((size_t)count, sizeof(*values));
        if (values == NULL) result = CNA_RESULT_OUT_OF_MEMORY;
    }
    for (jsize i = 0; result == CNA_RESULT_SUCCESS && i < count; ++i) {
        result = audio_listener_from_floats(environment, listeners, i * 12, &values[i]);
    }
    if (result == CNA_RESULT_SUCCESS) {
        result = count == 1
            ? cna.sound_effect_instance_apply_3d((CNA_Handle)instance, &values[0], &emitter)
            : cna.sound_effect_instance_apply_3d_multi_ext(
                (CNA_Handle)instance, values, (uint64_t)count, &emitter);
    }
    free(values);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeCreateDynamicSoundEffect(
    JNIEnv* environment, jclass type, jlong game, jint sample_rate,
    jint channels, jlongArray output)
{
    (void)type;
    CNA_Handle handle = CNA_INVALID_HANDLE;
    CNA_Result result = cna.dynamic_sound_effect_instance_create(
        java_game(game)->cna_handle, sample_rate, (CNA_AudioChannels)channels, &handle);
    if (result == CNA_RESULT_SUCCESS) result = audio_set_long(environment, output, handle);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeGetPendingBufferCount(
    JNIEnv* environment, jclass type, jlong instance, jintArray output)
{
    (void)type;
    int32_t count = 0;
    CNA_Result result = cna.dynamic_sound_effect_instance_get_pending_buffer_count(
        (CNA_Handle)instance, &count);
    if (result == CNA_RESULT_SUCCESS) result = set_int_output(environment, output, count);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeSubmitDynamicBuffer(
    JNIEnv* environment, jclass type, jlong instance, jbyteArray buffer,
    jint offset, jint count)
{
    (void)type;
    if (buffer == NULL) return (jint)CNA_RESULT_INVALID_ARGUMENT;
    const jsize length = (*environment)->GetArrayLength(environment, buffer);
    jbyte* bytes = (*environment)->GetByteArrayElements(environment, buffer, NULL);
    if (bytes == NULL) return (jint)CNA_RESULT_OUT_OF_MEMORY;
    const CNA_Result result = cna.dynamic_sound_effect_instance_submit_buffer(
        (CNA_Handle)instance, (const uint8_t*)bytes, (uint64_t)length, offset, count);
    (*environment)->ReleaseByteArrayElements(environment, buffer, bytes, JNI_ABORT);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeSubscribeAudioEvent(
    JNIEnv* environment, jclass type, jlong handle, jint index,
    jobject target, jboolean microphone, jlongArray output)
{
    (void)type;
    if (target == NULL || output == NULL) return (jint)CNA_RESULT_INVALID_ARGUMENT;
    JavaAudioEventRegistration* registration =
        (JavaAudioEventRegistration*)calloc(1, sizeof(*registration));
    if (registration == NULL) return (jint)CNA_RESULT_OUT_OF_MEMORY;
    registration->target = (*environment)->NewGlobalRef(environment, target);
    if (registration->target == NULL) { free(registration); return (jint)CNA_RESULT_OUT_OF_MEMORY; }
    jclass target_class = (*environment)->GetObjectClass(environment, target);
    const char* method_name = microphone == JNI_TRUE ? "nativeBufferReady" : "nativeBufferNeeded";
    registration->event = (*environment)->GetMethodID(environment, target_class, method_name, "()V");
    (*environment)->DeleteLocalRef(environment, target_class);
    if (registration->event == NULL) {
        (*environment)->DeleteGlobalRef(environment, registration->target);
        free(registration);
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    atomic_init(&registration->callbacks_enabled, 1);
    CNA_Result result = microphone == JNI_TRUE
        ? cna.microphone_subscribe_buffer_ready_at(
            java_game(handle)->cna_handle, (uint64_t)index, on_audio_event, registration,
            &registration->native_registration)
        : cna.dynamic_sound_effect_instance_subscribe_buffer_needed(
            (CNA_Handle)handle, on_audio_event, registration,
            &registration->native_registration);
    if (result != CNA_RESULT_SUCCESS) {
        atomic_store_explicit(&registration->callbacks_enabled, 0, memory_order_release);
        (*environment)->DeleteGlobalRef(environment, registration->target);
        free(registration);
        return (jint)result;
    }
    result = audio_set_long(environment, output, (uint64_t)(uintptr_t)registration);
    if (result != CNA_RESULT_SUCCESS) {
        (void)cna.audio_unsubscribe_ext(registration->native_registration);
        (*environment)->DeleteGlobalRef(environment, registration->target);
        free(registration);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeUnsubscribeAudioEvent(
    JNIEnv* environment, jclass type, jlong token)
{
    (void)type;
    JavaAudioEventRegistration* registration =
        (JavaAudioEventRegistration*)(uintptr_t)token;
    if (registration == NULL) return (jint)CNA_RESULT_INVALID_ARGUMENT;
    atomic_store_explicit(&registration->callbacks_enabled, 0, memory_order_release);
    const CNA_Result result = cna.audio_unsubscribe_ext(registration->native_registration);
    if (result != CNA_RESULT_SUCCESS) {
        atomic_store_explicit(&registration->callbacks_enabled, 1, memory_order_release);
        return (jint)result;
    }
    (*environment)->DeleteGlobalRef(environment, registration->target);
    free(registration);
    return (jint)CNA_RESULT_SUCCESS;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeGetMicrophoneCount(
    JNIEnv* environment, jclass type, jlong game, jlongArray output)
{
    (void)type;
    uint64_t count = 0;
    CNA_Result result = cna.microphone_get_count(java_game(game)->cna_handle, &count);
    if (result == CNA_RESULT_SUCCESS) result = audio_set_long(environment, output, count);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeGetDefaultMicrophone(
    JNIEnv* environment, jclass type, jlong game, jlongArray index, jintArray present)
{
    (void)type;
    uint64_t value = 0;
    CNA_Bool has_value = CNA_FALSE;
    CNA_Result result = cna.microphone_get_default_index_ext(
        java_game(game)->cna_handle, &value, &has_value);
    if (result == CNA_RESULT_SUCCESS) result = audio_set_long(environment, index, value);
    if (result == CNA_RESULT_SUCCESS) result = set_int_output(
        environment, present, has_value == CNA_TRUE ? 1 : 0);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeGetMicrophoneInt(
    JNIEnv* environment, jclass type, jlong game, jint index, jint kind, jintArray output)
{
    (void)type;
    CNA_Result result;
    int32_t projected = 0;
    if (kind == 1) {
        CNA_Bool value = CNA_FALSE;
        result = cna.microphone_get_is_headset_at(
            java_game(game)->cna_handle, (uint64_t)index, &value);
        projected = value == CNA_TRUE ? 1 : 0;
    } else if (kind == 2) {
        result = cna.microphone_get_sample_rate_at(
            java_game(game)->cna_handle, (uint64_t)index, &projected);
    } else if (kind == 3) {
        CNA_MicrophoneState state = CNA_MICROPHONE_STATE_STOPPED;
        result = cna.microphone_get_state_at(
            java_game(game)->cna_handle, (uint64_t)index, &state);
        projected = (int32_t)state;
    } else {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    if (result == CNA_RESULT_SUCCESS) result = set_int_output(environment, output, projected);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeGetMicrophoneDuration(
    JNIEnv* environment, jclass type, jlong game, jint index, jlongArray output)
{
    (void)type;
    int64_t ticks = 0;
    CNA_Result result = cna.microphone_get_buffer_duration_ticks_at(
        java_game(game)->cna_handle, (uint64_t)index, &ticks);
    if (result == CNA_RESULT_SUCCESS) result = audio_set_long(environment, output, (uint64_t)ticks);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeSetMicrophoneDuration(
    JNIEnv* environment, jclass type, jlong game, jint index, jlong ticks)
{
    (void)environment; (void)type;
    return (jint)cna.microphone_set_buffer_duration_ticks_at(
        java_game(game)->cna_handle, (uint64_t)index, (int64_t)ticks);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeMicrophoneTransport(
    JNIEnv* environment, jclass type, jlong game, jint index, jboolean start)
{
    (void)environment; (void)type;
    return start == JNI_TRUE
        ? (jint)cna.microphone_start_at(java_game(game)->cna_handle, (uint64_t)index)
        : (jint)cna.microphone_stop_at(java_game(game)->cna_handle, (uint64_t)index);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeGetMicrophoneData(
    JNIEnv* environment, jclass type, jlong game, jint index, jbyteArray data,
    jint offset, jint count, jlongArray output)
{
    (void)type;
    if (data == NULL) return (jint)CNA_RESULT_INVALID_ARGUMENT;
    const jsize length = (*environment)->GetArrayLength(environment, data);
    if (offset < 0 || count < 0 || offset > length || count > length - offset) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    jbyte* bytes = (*environment)->GetByteArrayElements(environment, data, NULL);
    if (bytes == NULL) return (jint)CNA_RESULT_OUT_OF_MEMORY;
    uint64_t read = 0;
    CNA_Result result = cna.microphone_get_data_at(
        java_game(game)->cna_handle, (uint64_t)index, (uint8_t*)bytes + offset,
        (uint64_t)count, &read);
    (*environment)->ReleaseByteArrayElements(environment, data, bytes, 0);
    if (result == CNA_RESULT_SUCCESS) result = audio_set_long(environment, output, read);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeCreateAudioEngine(
    JNIEnv* environment, jclass type, jlong game, jbyteArray settings,
    jlong look_ahead, jbyteArray renderer, jlongArray output)
{
    (void)type;
    jbyte* settings_bytes = NULL;
    jbyte* renderer_bytes = NULL;
    CNA_StringView settings_view;
    CNA_StringView renderer_view;
    CNA_Result result = audio_string_view(
        environment, settings, &settings_bytes, &settings_view);
    if (result == CNA_RESULT_SUCCESS) result = audio_string_view(
        environment, renderer, &renderer_bytes, &renderer_view);
    CNA_Handle handle = CNA_INVALID_HANDLE;
    if (result == CNA_RESULT_SUCCESS) result = cna.audio_engine_create_with_renderer(
        java_game(game)->cna_handle, settings_view,
        (int64_t)look_ahead, renderer_view, &handle);
    if (renderer_bytes != NULL) (*environment)->ReleaseByteArrayElements(
        environment, renderer, renderer_bytes, JNI_ABORT);
    if (settings_bytes != NULL) (*environment)->ReleaseByteArrayElements(
        environment, settings, settings_bytes, JNI_ABORT);
    if (result == CNA_RESULT_SUCCESS) result = audio_set_long(environment, output, handle);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeGetRendererCount(
    JNIEnv* environment, jclass type, jlong engine, jlongArray output)
{
    (void)type;
    uint64_t count = 0;
    CNA_Result result = cna.audio_engine_get_renderer_count((CNA_Handle)engine, &count);
    if (result == CNA_RESULT_SUCCESS) result = audio_set_long(environment, output, count);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeGetAudioCategory(
    JNIEnv* environment, jclass type, jlong engine, jbyteArray name, jlongArray output)
{
    (void)type;
    jbyte* bytes = NULL;
    CNA_StringView view;
    CNA_Result result = audio_string_view(environment, name, &bytes, &view);
    CNA_Handle handle = CNA_INVALID_HANDLE;
    if (result == CNA_RESULT_SUCCESS) result = cna.audio_engine_get_category(
        (CNA_Handle)engine, view, &handle);
    if (bytes != NULL) (*environment)->ReleaseByteArrayElements(environment, name, bytes, JNI_ABORT);
    if (result == CNA_RESULT_SUCCESS) result = audio_set_long(environment, output, handle);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeCategoryOperation(
    JNIEnv* environment, jclass type, jlong category, jint operation, jfloat volume)
{
    (void)environment; (void)type;
    switch (operation) {
        case 0: return (jint)cna.audio_category_pause((CNA_Handle)category);
        case 1: return (jint)cna.audio_category_resume((CNA_Handle)category);
        case 2: return (jint)cna.audio_category_set_volume((CNA_Handle)category, volume);
        case 3: return (jint)cna.audio_category_stop(
            (CNA_Handle)category, CNA_AUDIO_STOP_OPTIONS_AS_AUTHORED);
        case 4: return (jint)cna.audio_category_stop(
            (CNA_Handle)category, CNA_AUDIO_STOP_OPTIONS_IMMEDIATE);
        default: return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeCategoryEquals(
    JNIEnv* environment, jclass type, jlong category, jlong other, jintArray output)
{
    (void)type;
    CNA_Bool equal = CNA_FALSE;
    CNA_Result result = cna.audio_category_equals(
        (CNA_Handle)category, (CNA_Handle)other, &equal);
    if (result == CNA_RESULT_SUCCESS) {
        result = set_int_output(environment, output, equal != CNA_FALSE ? 1 : 0);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeCategoryHashCode(
    JNIEnv* environment, jclass type, jlong category, jintArray output)
{
    (void)type;
    int32_t hash_code = 0;
    CNA_Result result = cna.audio_category_get_hash_code(
        (CNA_Handle)category, &hash_code);
    if (result == CNA_RESULT_SUCCESS) {
        result = set_int_output(environment, output, hash_code);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeXactVariable(
    JNIEnv* environment, jclass type, jlong engine, jlong cue, jbyteArray name,
    jboolean set, jfloat value, jfloatArray output)
{
    (void)type;
    jbyte* bytes = NULL;
    CNA_StringView view;
    CNA_Result result = audio_string_view(environment, name, &bytes, &view);
    float read = 0.0f;
    if (result == CNA_RESULT_SUCCESS) {
        if (cue != 0) result = set == JNI_TRUE
            ? cna.cue_set_variable((CNA_Handle)cue, view, value)
            : cna.cue_get_variable((CNA_Handle)cue, view, &read);
        else result = set == JNI_TRUE
            ? cna.audio_engine_set_global_variable((CNA_Handle)engine, view, value)
            : cna.audio_engine_get_global_variable((CNA_Handle)engine, view, &read);
    }
    if (bytes != NULL) (*environment)->ReleaseByteArrayElements(environment, name, bytes, JNI_ABORT);
    if (result == CNA_RESULT_SUCCESS) result = audio_set_float(environment, output, read);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeCreateWaveBank(
    JNIEnv* environment, jclass type, jlong engine, jbyteArray path, jint offset,
    jshort packet_size, jboolean streaming, jlongArray output)
{
    (void)type;
    jbyte* bytes = NULL;
    CNA_StringView view;
    CNA_Result result = audio_string_view(environment, path, &bytes, &view);
    CNA_Handle handle = CNA_INVALID_HANDLE;
    if (result == CNA_RESULT_SUCCESS) result = streaming == JNI_TRUE
        ? cna.wave_bank_create_streaming(
            (CNA_Handle)engine, view, offset, packet_size, &handle)
        : cna.wave_bank_create((CNA_Handle)engine, view, &handle);
    if (bytes != NULL) (*environment)->ReleaseByteArrayElements(environment, path, bytes, JNI_ABORT);
    if (result == CNA_RESULT_SUCCESS) result = audio_set_long(environment, output, handle);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeGetBankBoolean(
    JNIEnv* environment, jclass type, jlong bank, jint kind, jboolean sound_bank)
{
    (void)environment; (void)type;
    GameGetBoolFunction function = sound_bank == JNI_TRUE
        ? cna.sound_bank_get_is_in_use
        : kind == 1 ? cna.wave_bank_get_is_prepared
        : kind == 2 ? cna.wave_bank_get_is_in_use : NULL;
    if (function == NULL) return -(jint)CNA_RESULT_INVALID_ARGUMENT;
    CNA_Bool value = CNA_FALSE;
    const CNA_Result result = function((CNA_Handle)bank, &value);
    return result == CNA_RESULT_SUCCESS
        ? (value == CNA_TRUE ? 1 : 0) : -(jint)result;
}

static CNA_Result audio_handle_string_create(
    JNIEnv* environment, jlong parent, jbyteArray text,
    HandleStringGetHandleFunction function, jlongArray output)
{
    jbyte* bytes = NULL;
    CNA_StringView view;
    CNA_Result result = audio_string_view(environment, text, &bytes, &view);
    CNA_Handle handle = CNA_INVALID_HANDLE;
    if (result == CNA_RESULT_SUCCESS) result = function((CNA_Handle)parent, view, &handle);
    if (bytes != NULL) (*environment)->ReleaseByteArrayElements(environment, text, bytes, JNI_ABORT);
    if (result == CNA_RESULT_SUCCESS) result = audio_set_long(environment, output, handle);
    return result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeCreateSoundBank(
    JNIEnv* environment, jclass type, jlong engine, jbyteArray path, jlongArray output)
{
    (void)type;
    return (jint)audio_handle_string_create(
        environment, engine, path, cna.sound_bank_create, output);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeGetCue(
    JNIEnv* environment, jclass type, jlong bank, jbyteArray name, jlongArray output)
{
    (void)type;
    return (jint)audio_handle_string_create(
        environment, bank, name, cna.sound_bank_get_cue, output);
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativePlayCueFromBank(
    JNIEnv* environment, jclass type, jlong bank, jbyteArray name,
    jfloatArray listener_values, jfloatArray emitter_values)
{
    (void)type;
    jbyte* bytes = NULL;
    CNA_StringView view;
    CNA_Result result = audio_string_view(environment, name, &bytes, &view);
    if (result == CNA_RESULT_SUCCESS && listener_values == NULL && emitter_values == NULL) {
        result = cna.sound_bank_play_cue((CNA_Handle)bank, view);
    } else if (result == CNA_RESULT_SUCCESS) {
        CNA_AudioListener listener;
        CNA_AudioEmitter emitter;
        result = audio_listener_from_floats(environment, listener_values, 0, &listener);
        if (result == CNA_RESULT_SUCCESS) result = audio_emitter_from_floats(
            environment, emitter_values, &emitter);
        if (result == CNA_RESULT_SUCCESS) result = cna.sound_bank_play_cue_3d(
            (CNA_Handle)bank, view, &listener, &emitter);
    }
    if (bytes != NULL) (*environment)->ReleaseByteArrayElements(environment, name, bytes, JNI_ABORT);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeGetCueInfo(
    JNIEnv* environment, jclass type, jlong cue, jintArray output)
{
    (void)type;
    if (output == NULL || (*environment)->GetArrayLength(environment, output) < 8) {
        return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    CNA_CueInfo info;
    (void)memset(&info, 0, sizeof(info));
    info.struct_size = (uint32_t)sizeof(info);
    info.struct_version = UINT32_C(1);
    CNA_Result result = cna.cue_get_info((CNA_Handle)cue, &info);
    if (result == CNA_RESULT_SUCCESS) {
        const jint values[8] = {
            info.is_created == CNA_TRUE, info.is_disposed == CNA_TRUE,
            info.is_paused == CNA_TRUE, info.is_playing == CNA_TRUE,
            info.is_prepared == CNA_TRUE, info.is_preparing == CNA_TRUE,
            info.is_stopped == CNA_TRUE, info.is_stopping == CNA_TRUE};
        (*environment)->SetIntArrayRegion(environment, output, 0, 8, values);
        if ((*environment)->ExceptionCheck(environment)) result = CNA_RESULT_INVALID_STATE;
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeApplyCue3D(
    JNIEnv* environment, jclass type, jlong cue,
    jfloatArray listener_values, jfloatArray emitter_values)
{
    (void)type;
    CNA_AudioListener listener;
    CNA_AudioEmitter emitter;
    CNA_Result result = audio_listener_from_floats(environment, listener_values, 0, &listener);
    if (result == CNA_RESULT_SUCCESS) result = audio_emitter_from_floats(
        environment, emitter_values, &emitter);
    if (result == CNA_RESULT_SUCCESS) result = cna.cue_apply_3d(
        (CNA_Handle)cue, &listener, &emitter);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeCueTransport(
    JNIEnv* environment, jclass type, jlong cue, jint operation, jint option)
{
    (void)environment; (void)type;
    switch (operation) {
        case 0: return (jint)cna.cue_play((CNA_Handle)cue);
        case 1: return (jint)cna.cue_pause((CNA_Handle)cue);
        case 2: return (jint)cna.cue_resume((CNA_Handle)cue);
        case 3: return (jint)cna.cue_stop((CNA_Handle)cue, (CNA_AudioStopOptions)option);
        default: return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeGetStringSize(
    JNIEnv* environment, jclass type, jlong handle, jint kind, jint index, jlongArray output)
{
    (void)type;
    uint64_t size = 0;
    CNA_Result result;
    switch (kind) {
        case 0: result = cna.sound_effect_get_name_size((CNA_Handle)handle, &size); break;
        case 1: result = cna.microphone_get_name_size_at(
            java_game(handle)->cna_handle, (uint64_t)index, &size); break;
        case 2: result = cna.audio_engine_get_renderer_friendly_name_size(
            (CNA_Handle)handle, (uint64_t)index, &size); break;
        case 3: result = cna.audio_engine_get_renderer_id_size(
            (CNA_Handle)handle, (uint64_t)index, &size); break;
        case 4: result = cna.audio_category_get_name_size(
            (CNA_Handle)handle, &size); break;
        default: return (jint)CNA_RESULT_INVALID_ARGUMENT;
    }
    if (result == CNA_RESULT_SUCCESS) result = audio_set_long(environment, output, size);
    return (jint)result;
}

JNIEXPORT jint JNICALL Java_org_openeggbert_cna_internal_NativeAudio_nativeCopyString(
    JNIEnv* environment, jclass type, jlong handle, jint kind, jint index, jbyteArray output)
{
    (void)type;
    if (output == NULL) return (jint)CNA_RESULT_INVALID_ARGUMENT;
    const jsize capacity = (*environment)->GetArrayLength(environment, output);
    jbyte* bytes = (*environment)->GetByteArrayElements(environment, output, NULL);
    if (bytes == NULL && capacity != 0) return (jint)CNA_RESULT_OUT_OF_MEMORY;
    uint64_t required = 0;
    CNA_Result result;
    switch (kind) {
        case 0: result = cna.sound_effect_copy_name(
            (CNA_Handle)handle, (char*)bytes, (uint64_t)capacity, &required); break;
        case 1: result = cna.microphone_copy_name_at(
            java_game(handle)->cna_handle, (uint64_t)index, (char*)bytes,
            (uint64_t)capacity, &required); break;
        case 2: result = cna.audio_engine_copy_renderer_friendly_name(
            (CNA_Handle)handle, (uint64_t)index, (char*)bytes,
            (uint64_t)capacity, &required); break;
        case 3: result = cna.audio_engine_copy_renderer_id(
            (CNA_Handle)handle, (uint64_t)index, (char*)bytes,
            (uint64_t)capacity, &required); break;
        case 4: result = cna.audio_category_copy_name(
            (CNA_Handle)handle, (char*)bytes, (uint64_t)capacity, &required); break;
        default: result = CNA_RESULT_INVALID_ARGUMENT; break;
    }
    if (bytes != NULL) (*environment)->ReleaseByteArrayElements(environment, output, bytes, 0);
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
