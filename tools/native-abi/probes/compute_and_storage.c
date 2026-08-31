/* SPDX-License-Identifier: MS-PL */
/*
 * What does the compute family actually do on a renderer with no compute?
 *
 * The family census answered `NOT_SUPPORTED` for `storage_buffer` and `compute_shader`, but it
 * asked with no device. The header says something more interesting about the shader: creation
 * succeeds even when the source does not compile, and a caller asks `is_valid` and reads the
 * compile error -- the same shape as the GPU timer, which is a family worth projecting with its
 * refusal intact rather than leaving out. Which of the two answers is the real one, and how far
 * into the family a caller gets before it stops, is what this measures.
 */
#include <stdio.h>
#include <string.h>

#include "CNA/C/engine_layer.h"
#include "CNA/C/cna.h"

static int ran = 0;

static CNA_StringView view_of(const char* text)
{
    CNA_StringView view = {text, (uint64_t)strlen(text)};
    return view;
}

static CNA_Result on_update(CNA_Handle game, const CNA_GameTime* game_time, void* context,
    CNA_CallbackError* out_error)
{
    (void)game_time;
    (void)out_error;
    (void)context;
    if (ran) {
        return CNA_RESULT_SUCCESS;
    }
    ran = 1;
    CNA_Handle device = CNA_INVALID_HANDLE;
    if (cna_game_get_graphics_device(game, &device) != CNA_RESULT_SUCCESS) {
        printf("no device\n");
        return CNA_RESULT_SUCCESS;
    }

    /* The two pure routes first: they touch no device and should work anywhere. */
    CNA_Bool contains = CNA_FALSE;
    CNA_Result result = cna_graphics_memory_barrier_has(
        CNA_GRAPHICS_MEMORY_BARRIER_SHADER_STORAGE | CNA_GRAPHICS_MEMORY_BARRIER_TEXTURE_FETCH,
        CNA_GRAPHICS_MEMORY_BARRIER_SHADER_STORAGE, &contains);
    printf("barrier has, present    %d  %d\n", (int)result, (int)contains);
    result = cna_graphics_memory_barrier_has(CNA_GRAPHICS_MEMORY_BARRIER_TEXTURE_FETCH,
        CNA_GRAPHICS_MEMORY_BARRIER_SHADER_STORAGE, &contains);
    printf("barrier has, absent     %d  %d\n", (int)result, (int)contains);

    CNA_IndirectDrawArguments draw_arguments;
    memset(&draw_arguments, 0, sizeof draw_arguments);
    result = cna_indirect_draw_arguments_init(&draw_arguments);
    printf("indirect draw init      %d  count %u instances %u\n", (int)result,
        (unsigned)draw_arguments.vertex_count, (unsigned)draw_arguments.instance_count);
    CNA_IndirectDrawIndexedArguments indexed_arguments;
    memset(&indexed_arguments, 0, sizeof indexed_arguments);
    result = cna_indirect_draw_indexed_arguments_init(&indexed_arguments);
    printf("indirect indexed init   %d  count %u\n", (int)result,
        (unsigned)indexed_arguments.index_count);

    /* The storage buffer, asked with a real device this time. */
    CNA_StorageBufferHandle buffer = 0;
    result = cna_storage_buffer_create(device, 256, &buffer);
    printf("storage buffer create   %d\n", (int)result);
    if (result == CNA_RESULT_SUCCESS) {
        uint64_t size = 0;
        (void)cna_storage_buffer_get_byte_size(buffer, &size);
        printf("storage buffer size     %llu\n", (unsigned long long)size);
        (void)cna_storage_buffer_destroy(buffer);
    }
    CNA_StorageBufferHandle typed = 0;
    result = cna_storage_buffer_create_typed(device, 16, 16, &typed);
    printf("storage buffer typed    %d\n", (int)result);
    if (result == CNA_RESULT_SUCCESS) {
        (void)cna_storage_buffer_destroy(typed);
    }

    /* And the compute shader, whose header says creation succeeds even when compilation fails. */
    const char* source =
        "#version 310 es\n"
        "layout(local_size_x = 1) in;\n"
        "void main() {}\n";
    CNA_ComputeShaderHandle shader = 0;
    result = cna_compute_shader_create(device, view_of(source), &shader);
    printf("compute shader create   %d\n", (int)result);
    if (result == CNA_RESULT_SUCCESS) {
        /* Sequenced deliberately. C leaves the order of a call's arguments unspecified, so
           reading `valid` in the same printf that fills it prints whatever it held before the
           call -- which on a renderer with no compute is indistinguishable from the answer and
           on one with compute is simply wrong. */
        CNA_Bool valid = CNA_FALSE;
        const CNA_Result asked_valid = cna_compute_shader_is_valid(shader, &valid);
        printf("is valid                %d  %d\n", (int)asked_valid, (int)valid);
        uint64_t bytes = 0;
        CNA_Result text = cna_compute_shader_copy_compile_error(shader, NULL, 0, &bytes);
        char message[512];
        if (bytes > 0 && bytes < sizeof message) {
            (void)cna_compute_shader_copy_compile_error(shader, message, sizeof message, &bytes);
            message[bytes] = '\0';
            printf("compile error           %d  \"%s\"\n", (int)text, message);
        } else {
            printf("compile error           %d  %llu bytes\n", (int)text,
                (unsigned long long)bytes);
        }
        printf("dispatch                %d\n",
            (int)cna_compute_shader_dispatch(shader, 1, 1, 1));
        printf("set uniform int         %d\n",
            (int)cna_compute_shader_set_uniform_int(shader, view_of("value"), 3));
        printf("set uniform float       %d\n",
            (int)cna_compute_shader_set_uniform_float(shader, view_of("value"), 0.5f));
        CNA_Bool images = CNA_FALSE;
        const CNA_Result asked_images =
            cna_compute_shader_is_image_binding_supported(shader, &images);
        printf("image binding supported %d  %d\n", (int)asked_images, (int)images);
        printf("barrier                 %d\n", (int)cna_compute_shader_barrier(
            shader, CNA_GRAPHICS_MEMORY_BARRIER_SHADER_STORAGE));
        printf("destroy                 %d\n", (int)cna_compute_shader_destroy(shader));
    }

    return CNA_RESULT_SUCCESS;
}

int main(void)
{
    CNA_GameCallbacks callbacks;
    memset(&callbacks, 0, sizeof callbacks);
    callbacks.struct_size = (uint32_t)(sizeof callbacks);
    callbacks.struct_version = 1U;
    callbacks.update = on_update;

    CNA_GameCreateInfo info;
    memset(&info, 0, sizeof info);
    info.struct_size = (uint32_t)(sizeof info);
    info.struct_version = 1U;
    info.is_fixed_time_step = CNA_TRUE;
    info.target_elapsed_time_ticks = 166667;
    info.window_title.data = "compute and storage probe";
    info.window_title.byte_length = 25U;
    info.callbacks = &callbacks;

    CNA_Handle game = CNA_INVALID_HANDLE;
    if (cna_game_create(&info, &game) != CNA_RESULT_SUCCESS) {
        printf("game create failed\n");
        return 1;
    }
    CNA_GraphicsDeviceManagerHandle manager = 0;
    (void)cna_graphics_device_manager_create(game, &manager);
    (void)cna_game_run_one_frame(game);
    if (manager != 0) {
        (void)cna_graphics_device_manager_destroy(manager);
    }
    (void)cna_game_destroy(game);
    printf("PROBE %s\n", ran ? "OK" : "INCOMPLETE");
    return ran ? 0 : 1;
}
