/* SPDX-License-Identifier: MS-PL */
/*
 * Does the owning effect-pass constructor really consume its effect, and what happens on the
 * failure path?
 *
 * `cna_post_process_effect_pass_create_owning` is the only consumed-ownership transfer in this part
 * of the engine layer, and the Java side of a transfer has to stop owning the object on success and
 * keep owning it on failure. Both branches have to be real before either can be projected: a
 * "failure" that cannot be provoked would make the second half untestable, and a "success" that
 * did not actually consume would make the first half a leak.
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

    /* The borrowing constructor first, with a stock effect the probe keeps owning. */
    CNA_EffectHandle borrowed_effect = 0;
    CNA_Result result = cna_basic_effect_create(device, &borrowed_effect);
    printf("basic effect create     %d\n", (int)result);

    CNA_PostProcessPassHandle pass = 0;
    result = cna_post_process_effect_pass_create(device, borrowed_effect, view_of("borrowing"),
        &pass);
    printf("create borrowing        %d\n", (int)result);

    CNA_EffectHandle read_back = 0;
    result = cna_post_process_effect_pass_get_effect(pass, &read_back);
    CNA_EffectHandle read_again = 0;
    (void)cna_post_process_effect_pass_get_effect(pass, &read_again);
    printf("get effect              %d  same as set %d  stable across calls %d\n", (int)result,
        read_back == borrowed_effect, read_back == read_again);

    result = cna_post_process_effect_pass_set_effect(pass, CNA_INVALID_HANDLE);
    printf("set effect to none      %d\n", (int)result);
    read_back = 1;
    (void)cna_post_process_effect_pass_get_effect(pass, &read_back);
    printf("get effect after none   %s\n", read_back == CNA_INVALID_HANDLE ? "invalid" : "valid");
    result = cna_post_process_effect_pass_set_effect(pass, borrowed_effect);
    printf("set effect back         %d\n", (int)result);

    printf("destroy borrowing pass  %d\n", (int)cna_post_process_pass_destroy(pass));
    /* The effect must have survived the pass, because the pass only borrowed it. */
    printf("effect alive after      %d\n", (int)cna_effect_destroy(borrowed_effect));

    /* Now the owning constructor. */
    CNA_EffectHandle owned_effect = 0;
    (void)cna_basic_effect_create(device, &owned_effect);
    CNA_PostProcessPassHandle owning = 0;
    result = cna_post_process_effect_pass_create_owning(device, owned_effect, view_of("owning"),
        &owning);
    printf("create owning           %d\n", (int)result);
    /* If the handle was consumed, destroying it now must be refused. */
    printf("destroy consumed handle %d\n", (int)cna_effect_destroy(owned_effect));
    printf("destroy owning pass     %d\n", (int)cna_post_process_pass_destroy(owning));

    /* The failure branch: is there one that leaves the effect alive? */
    CNA_EffectHandle kept = 0;
    (void)cna_basic_effect_create(device, &kept);
    CNA_PostProcessPassHandle refused = 0;
    result = cna_post_process_effect_pass_create_owning(CNA_INVALID_HANDLE, kept,
        view_of("refused"), &refused);
    printf("owning, bad device      %d\n", (int)result);
    printf("effect after refusal    %d\n", (int)cna_effect_destroy(kept));

    /* And an effect pass question asked of a pass that is not one. */
    CNA_PostProcessPassHandle blit = 0;
    (void)cna_blit_pass_create(device, &blit);
    CNA_EffectHandle wrong = 0;
    printf("get effect of blit      %d\n",
        (int)cna_post_process_effect_pass_get_effect(blit, &wrong));
    printf("set effect of blit      %d\n",
        (int)cna_post_process_effect_pass_set_effect(blit, CNA_INVALID_HANDLE));
    (void)cna_post_process_pass_destroy(blit);

    /* The full-screen pass, whose draw takes a sampler structure. */
    CNA_FullscreenPassHandle fullscreen = 0;
    result = cna_fullscreen_pass_create(device, &fullscreen);
    printf("fullscreen create       %d\n", (int)result);
    result = cna_fullscreen_pass_draw(fullscreen, CNA_INVALID_HANDLE, CNA_INVALID_HANDLE,
        CNA_INVALID_HANDLE, 64, 64, NULL);
    printf("fullscreen draw         %d\n", (int)result);
    result = cna_fullscreen_pass_draw_over_current_target(fullscreen, CNA_INVALID_HANDLE,
        CNA_INVALID_HANDLE, 64, 64, NULL);
    printf("fullscreen draw over    %d\n", (int)result);
    printf("fullscreen destroy      %d\n", (int)cna_fullscreen_pass_destroy(fullscreen));

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
    info.window_title.data = "effect pass ownership probe";
    info.window_title.byte_length = 27U;
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
