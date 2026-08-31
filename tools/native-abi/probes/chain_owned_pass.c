/* SPDX-License-Identifier: MS-PL */
/*
 * What does `cna_post_process_chain_add_owned_pass` leave behind?
 *
 * It is the one route in the engine layer that invalidates a handle a caller still holds, and the
 * Java projection has to match it exactly or leave an object that releases a handle twice. What
 * needs measuring is not the handle -- the header is clear about that -- but the game's own count
 * of owned children, which every engine object is registered in and which `cna_game_destroy`
 * refuses on. A handover that releases the handle without decrementing that count makes the game
 * undestroyable, which is a leak a Java test sees only as a teardown failure three tests later.
 */
#include <stdio.h>
#include <string.h>

#include "CNA/C/engine_layer.h"
#include "CNA/C/cna.h"

static int ran = 0;

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

    CNA_PostProcessChainHandle chain = 0;
    printf("chain create            %d\n",
        (int)cna_post_process_chain_create(device, &chain));

    CNA_PostProcessPassHandle owned = 0;
    printf("pass create             %d\n", (int)cna_blit_pass_create(device, &owned));
    printf("add owned               %d\n",
        (int)cna_post_process_chain_add_owned_pass(chain, owned));
    int32_t count = 0;
    (void)cna_post_process_chain_get_pass_count(chain, &count);
    printf("pass count              %d\n", (int)count);
    /* The handle should now be invalid: using it must be refused, not honoured. */
    printf("destroy consumed handle %d\n", (int)cna_post_process_pass_destroy(owned));

    printf("chain clear             %d\n", (int)cna_post_process_chain_clear(chain));
    (void)cna_post_process_chain_get_pass_count(chain, &count);
    printf("pass count after clear  %d\n", (int)count);
    printf("chain destroy           %d\n", (int)cna_post_process_chain_destroy(chain));

    /* And the question the Java suite could only see as a teardown failure: is the game still
       destroyable, or did the handover leave an owned child nothing can release? */
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
    info.window_title.data = "chain owned pass probe";
    info.window_title.byte_length = 22U;
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
    printf("game destroy            %d\n", (int)cna_game_destroy(game));
    printf("PROBE %s\n", ran ? "OK" : "INCOMPLETE");
    return ran ? 0 : 1;
}
