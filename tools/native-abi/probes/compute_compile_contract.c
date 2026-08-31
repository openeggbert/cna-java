/* What does `cna_compute_shader_create` do with source the renderer cannot compile?

   The header is explicit: *"Creation succeeds even when the source does not compile: ask
   @ref cna_compute_shader_is_valid and read @ref cna_compute_shader_copy_compile_error. That
   mirrors the canonical class, which records the failure rather than throwing."* That sentence is
   the whole basis on which a Java `ComputeShader` would be built -- a constructor that always
   produces an object, and a compile error a caller reads off it.

   It could not be measured on the HEADLESS renderer, which has no compiler and refuses before
   reaching the question. This probe asks it on a renderer that does compile, with four sources:
   one that compiles, one that is not GLSL at all, one that is valid GLSL for the other dialect,
   and one that compiles but cannot link.

   Run once per renderer:
     CNA_GRAPHICS_RENDERER=OPENGLES3 ./build-probe/compute_compile_contract
     CNA_GRAPHICS_RENDERER=OPENGL33  ./build-probe/compute_compile_contract */
#include <CNA/C/core.h>
#include <CNA/C/engine_layer.h>
#include <CNA/C/graphics.h>
#include <CNA/C/runtime.h>
#include <CNA/C/runtime_graphics_manager.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

static const char* name_of(const CNA_Result result)
{
    switch ((int)result) {
        case 0: return "SUCCESS";
        case 1: return "INVALID_ARGUMENT";
        case 2: return "INVALID_HANDLE";
        case 3: return "INVALID_STATE";
        case 6: return "NOT_SUPPORTED";
        case 12: return "INTERNAL";
        case 14: return "BUFFER_TOO_SMALL";
        default: return "OTHER";
    }
}

static CNA_StringView view_of(const char* text)
{
    CNA_StringView view;
    view.data = text;
    view.byte_length = (uint64_t)strlen(text);
    return view;
}

static void ask(CNA_Handle device, const char* label, const char* source)
{
    CNA_ComputeShaderHandle shader = 0;
    const CNA_Result made = cna_compute_shader_create(device, view_of(source), &shader);
    if (made != CNA_RESULT_SUCCESS) {
        /* No handle exists, so the documented compile-error route cannot be reached. The only
           text a caller can still see is the barrier's own last message, which is what decides
           whether a Java layer can surface the GLSL compiler's diagnostics at all. */
        char message[1024];
        uint64_t bytes = 0;
        message[0] = '\0';
        if (cna_error_get_last_message_size(&bytes) == CNA_RESULT_SUCCESS && bytes > 0 &&
            bytes < sizeof message) {
            uint64_t written = 0;
            if (cna_error_copy_last_message(message, sizeof message - 1, &written) ==
                CNA_RESULT_SUCCESS) {
                message[written] = '\0';
                for (uint64_t index = 0; index < written; ++index) {
                    if (message[index] == '\n' || message[index] == '\r') message[index] = ' ';
                }
            }
        }
        printf("  %-22s create %-16s handle %s\n", label, name_of(made),
               shader == 0 ? "invalid" : "SET DESPITE FAILURE");
        printf("  %-22s last message %llu bytes: %.220s\n", label, (unsigned long long)bytes,
               message);
        fflush(stdout);
        return;
    }
    CNA_Bool valid = CNA_FALSE;
    cna_compute_shader_is_valid(shader, &valid);
    uint64_t bytes = 0;
    const CNA_Result sized = cna_compute_shader_copy_compile_error(shader, NULL, 0, &bytes);
    char error[1024];
    error[0] = '\0';
    if (bytes > 0 && bytes < sizeof error) {
        uint64_t written = 0;
        if (cna_compute_shader_copy_compile_error(shader, error, sizeof error - 1, &written) ==
            CNA_RESULT_SUCCESS) {
            error[written] = '\0';
            for (uint64_t index = 0; index < written; ++index) {
                if (error[index] == '\n' || error[index] == '\r') error[index] = ' ';
            }
        }
    }
    printf("  %-22s create SUCCESS          valid %-3s error %llu bytes%s%s\n", label,
           valid ? "yes" : "no", (unsigned long long)bytes, error[0] != '\0' ? ": " : "",
           error);
    fflush(stdout);
    (void)sized;
    printf("  %-22s destroy %s\n", label, name_of(cna_compute_shader_destroy(shader)));
    fflush(stdout);
}

static CNA_Result on_update(CNA_Handle game, const CNA_GameTime* game_time, void* context,
                            CNA_CallbackError* out_error)
{
    (void)game_time;
    (void)out_error;
    int* ran = (int*)context;
    if (*ran) return CNA_RESULT_SUCCESS;
    *ran = 1;

    CNA_Handle device = CNA_INVALID_HANDLE;
    if (cna_game_get_graphics_device(game, &device) != CNA_RESULT_SUCCESS) return CNA_RESULT_SUCCESS;

    CNA_Bool compute = CNA_FALSE;
    cna_graphics_device_supports_capability(device, CNA_GRAPHICS_CAPABILITY_COMPUTE_SHADERS,
                                            &compute);
    printf("compute capability       %s\n", compute ? "yes" : "no");

    ask(device, "compiles", "#version 310 es\n"
                            "layout(local_size_x = 1) in;\n"
                            "layout(std430, binding = 0) buffer B { int v[]; };\n"
                            "void main() { v[0] = 1; }\n");

    ask(device, "not glsl at all", "#version 310 es\nthis is not glsl\n");

    ask(device, "no version directive", "void main() { }\n");

    ask(device, "compiles, cannot link",
        "#version 310 es\n"
        "layout(local_size_x = 1) in;\n"
        "void missing();\n"
        "void main() { missing(); }\n");

    ask(device, "empty source", "");

    return CNA_RESULT_SUCCESS;
}

int main(void)
{
    const char* requested = getenv("CNA_GRAPHICS_RENDERER");
    printf("renderer requested       %s\n", requested != NULL ? requested : "<build default>");
    fflush(stdout);

    int ran = 0;
    CNA_GameCallbacks callbacks;
    memset(&callbacks, 0, sizeof callbacks);
    callbacks.struct_size = (uint32_t)(sizeof callbacks);
    callbacks.struct_version = 1U;
    callbacks.update = on_update;
    callbacks.context = &ran;

    CNA_GameCreateInfo info;
    memset(&info, 0, sizeof info);
    info.struct_size = (uint32_t)(sizeof info);
    info.struct_version = 1U;
    info.is_fixed_time_step = CNA_TRUE;
    info.target_elapsed_time_ticks = 166667;
    info.window_title.data = "compute compile contract";
    info.window_title.byte_length = 24U;
    info.callbacks = &callbacks;

    CNA_Handle game = CNA_INVALID_HANDLE;
    if (cna_game_create(&info, &game) != CNA_RESULT_SUCCESS) {
        printf("PROBE INCOMPLETE: no game\n");
        return 1;
    }
    CNA_GraphicsDeviceManagerHandle manager = 0;
    cna_graphics_device_manager_create(game, &manager);
    cna_game_run_one_frame(game);
    if (manager != 0) cna_graphics_device_manager_destroy(manager);
    printf("game destroy             %s\n", name_of(cna_game_destroy(game)));
    printf("PROBE OK (ran=%d)\n", ran);
    return ran ? 0 : 1;
}
