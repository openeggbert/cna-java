/* SPDX-License-Identifier: MS-PL */
/*
 * Can the headless renderer compile a shader, and what does the factory's counted borrow actually
 * refuse?
 *
 * The factory's whole contract is a borrow discipline -- an acquired effect is a borrowed view, and
 * clear and destroy are refused while one is outstanding -- and a discipline is only worth
 * projecting into Java if this runtime actually enforces it. Whether a shader compiles here at all
 * is the question before that one.
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

    CNA_ShaderEffectFactoryHandle factory = 0;
    CNA_Result result = cna_shader_effect_factory_create(device, &factory);
    printf("create                 %d\n", (int)result);
    if (result != CNA_RESULT_SUCCESS) {
        return CNA_RESULT_SUCCESS;
    }

    uint64_t compiled = 99;
    (void)cna_shader_effect_factory_get_compile_count(factory, &compiled);
    printf("compile count, fresh   %llu\n", (unsigned long long)compiled);

    CNA_Bool contains = CNA_TRUE;
    (void)cna_shader_effect_factory_contains(factory, view_of("tint"), &contains);
    printf("contains before        %d\n", (int)contains);

    const char* vertex =
        "attribute vec4 a_position;\n"
        "void main() { gl_Position = a_position; }\n";
    const char* fragment =
        "void main() { gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0); }\n";

    CNA_EffectHandle effect = 0;
    result = cna_shader_effect_factory_acquire(factory, view_of("tint"), view_of(vertex),
        view_of(fragment), &effect);
    printf("acquire                %d  effect %s\n", (int)result,
        effect == 0 ? "invalid" : "valid");

    (void)cna_shader_effect_factory_contains(factory, view_of("tint"), &contains);
    printf("contains after         %d\n", (int)contains);
    (void)cna_shader_effect_factory_get_compile_count(factory, &compiled);
    printf("compile count after    %llu\n", (unsigned long long)compiled);

    if (effect != 0) {
        /* The borrow discipline: both refused while the view is out. */
        printf("clear while borrowed   %d\n", (int)cna_shader_effect_factory_clear(factory));
        printf("destroy while borrowed %d\n", (int)cna_shader_effect_factory_destroy(factory));

        /* Is a second acquire of the same name the same handle, and does it compile again? */
        CNA_EffectHandle again = 0;
        result = cna_shader_effect_factory_acquire(factory, view_of("tint"), view_of(vertex),
            view_of(fragment), &again);
        (void)cna_shader_effect_factory_get_compile_count(factory, &compiled);
        printf("acquire again          %d  same handle %d  compile count %llu\n", (int)result,
            again == effect, (unsigned long long)compiled);

        /* A different name with the same source: a second compile. */
        CNA_EffectHandle other = 0;
        result = cna_shader_effect_factory_acquire(factory, view_of("other"), view_of(vertex),
            view_of(fragment), &other);
        (void)cna_shader_effect_factory_get_compile_count(factory, &compiled);
        printf("acquire other name     %d  compile count %llu\n", (int)result,
            (unsigned long long)compiled);

        printf("dispose borrowed       %d\n", (int)cna_effect_destroy(effect));
        if (again != 0 && again != effect) {
            (void)cna_effect_destroy(again);
        }
        if (other != 0) {
            (void)cna_effect_destroy(other);
        }
        printf("clear after release    %d\n", (int)cna_shader_effect_factory_clear(factory));
        (void)cna_shader_effect_factory_get_compile_count(factory, &compiled);
        printf("compile count, cleared %llu\n", (unsigned long long)compiled);
    }

    CNA_EffectHandle empty = 0;
    printf("acquire empty name     %d\n", (int)cna_shader_effect_factory_acquire(
        factory, view_of(""), view_of(vertex), view_of(fragment), &empty));
    CNA_EffectHandle broken = 0;
    printf("acquire bad source     %d\n", (int)cna_shader_effect_factory_acquire(
        factory, view_of("broken"), view_of("this is not a shader"), view_of(fragment), &broken));

    printf("destroy                %d\n", (int)cna_shader_effect_factory_destroy(factory));
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
    info.window_title.data = "shader effect cache probe";
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
