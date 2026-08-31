/* SPDX-License-Identifier: MS-PL */
/*
 * Does a post-process pass that reports itself unsupported actually decline to run?
 *
 * `cna_post_process_pass_is_supported` exists so a game can ask before it spends a frame, and the
 * whole value of that question is that the answer predicts the behaviour. On the renderers this
 * repository qualifies against it does not always: one of them answers `false` for a film-grain
 * pass and then grains the frame anyway.
 *
 * That is the third instance of one shape -- a capability query disagreeing with what the
 * renderer then does -- after the cube shadow map's face passes (`JAVA-UPSTREAM-007`) and the
 * clustered lighting routes' documented parameter (`JAVA-UPSTREAM-005`). Recorded in C because a
 * Java test can only show it on the renderers it happens to run on, and the point is the
 * disagreement rather than one renderer's answer.
 *
 *   CNA_GRAPHICS_RENDERER=OPENGL4 ./build-probe/pass_support_versus_behaviour
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "CNA/C/cna.h"

static int ran = 0;

static const char* name_of(const CNA_Result result)
{
    switch ((int)result) {
        case 0: return "SUCCESS";
        case 1: return "INVALID_ARGUMENT";
        case 2: return "INVALID_HANDLE";
        case 3: return "INVALID_STATE";
        case 6: return "NOT_SUPPORTED";
        case 12: return "INTERNAL";
        default: return "OTHER";
    }
}

static CNA_Result on_update(CNA_Handle game, const CNA_GameTime* game_time, void* context,
                            CNA_CallbackError* out_error)
{
    (void)game_time;
    (void)context;
    (void)out_error;
    if (ran) return CNA_RESULT_SUCCESS;
    ran = 1;

    CNA_Handle device = CNA_INVALID_HANDLE;
    if (cna_game_get_graphics_device(game, &device) != CNA_RESULT_SUCCESS) {
        printf("no device\n");
        return CNA_RESULT_SUCCESS;
    }

    enum { kSize = 16 };
    CNA_Color source_pixels[kSize * kSize];
    for (int index = 0; index < kSize * kSize; ++index) {
        source_pixels[index].r = 128U;
        source_pixels[index].g = 128U;
        source_pixels[index].b = 128U;
        source_pixels[index].a = 255U;
    }
    CNA_Handle source = CNA_INVALID_HANDLE;
    printf("source create           %s\n",
           name_of(cna_texture2d_create_from_rgba8(device, kSize, kSize, source_pixels,
                                                   (uint64_t)(kSize * kSize), &source)));

    CNA_RenderTarget2DCreateInfo target_info;
    memset(&target_info, 0, sizeof target_info);
    target_info.struct_size = (uint32_t)(sizeof target_info);
    target_info.struct_version = 1U;
    target_info.width = kSize;
    target_info.height = kSize;
    target_info.format = CNA_SURFACE_FORMAT_COLOR;
    target_info.depth_format = CNA_DEPTH_FORMAT_NONE;
    target_info.usage = CNA_RENDER_TARGET_USAGE_DISCARD_CONTENTS;
    CNA_Handle destination = CNA_INVALID_HANDLE;
    printf("target create           %s\n",
           name_of(cna_render_target2d_create(device, &target_info, &destination)));

    CNA_PostProcessPassHandle grain = 0;
    printf("film grain create       %s\n",
           name_of(cna_film_grain_pass_create(device, &grain)));
    CNA_Bool supported = CNA_FALSE;
    const CNA_Result asked = cna_post_process_pass_is_supported(grain, device, &supported);
    printf("is_supported            %s  %s\n", name_of(asked), supported ? "yes" : "no");
    printf("set intensity           %s\n",
           name_of(cna_film_grain_pass_set_intensity(grain, 1.0F)));

    CNA_PostProcessContext frame;
    memset(&frame, 0, sizeof frame);
    frame.struct_size = (uint32_t)(sizeof frame);
    frame.struct_version = 1U;
    frame.source = source;
    frame.destination = destination;
    frame.width = kSize;
    frame.height = kSize;
    frame.elapsed_seconds = 0.5F;
    printf("apply                   %s\n",
           name_of(cna_post_process_pass_apply(grain, &frame)));

    CNA_Color out[kSize * kSize];
    memset(out, 0, sizeof out);
    CNA_Texture2DTransfer transfer;
    memset(&transfer, 0, sizeof transfer);
    transfer.struct_size = (uint32_t)(sizeof transfer);
    transfer.struct_version = 1U;
    transfer.element_count = (uint64_t)(kSize * kSize);
    uint64_t written = 0;
    const CNA_Result read = cna_texture2d_get_data(destination, CNA_TEXTURE_DATA_COLOR, &transfer,
                                                   out, (uint64_t)(kSize * kSize), &written);
    if (read != CNA_RESULT_SUCCESS) {
        printf("readback                %s  (nothing can be said about the image)\n",
               name_of(read));
    } else {
        int distinct = 0;
        for (int index = 0; index < kSize * kSize; ++index) {
            int seen = 0;
            for (int earlier = 0; earlier < index; ++earlier) {
                if (out[earlier].r == out[index].r && out[earlier].g == out[index].g
                    && out[earlier].b == out[index].b) {
                    seen = 1;
                    break;
                }
            }
            if (!seen) distinct++;
        }
        printf("readback                %s  first %u,%u,%u  distinct %d\n", name_of(read),
               out[0].r, out[0].g, out[0].b, distinct);
        printf("VERDICT                 is_supported=%s, grain %s\n", supported ? "yes" : "no",
               distinct > 4 ? "RAN" : "did not run");
    }

    (void)cna_post_process_pass_destroy(grain);
    (void)cna_render_target_destroy(destination);
    (void)cna_texture2d_destroy(source);
    return CNA_RESULT_SUCCESS;
}

int main(void)
{
    const char* requested = getenv("CNA_GRAPHICS_RENDERER");
    printf("renderer                %s\n", requested != NULL ? requested : "<build default>");

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
    info.window_title.data = "pass support versus behaviour";
    info.window_title.byte_length = 28U;
    info.callbacks = &callbacks;

    CNA_Handle game = CNA_INVALID_HANDLE;
    if (cna_game_create(&info, &game) != CNA_RESULT_SUCCESS) {
        printf("game create failed\n");
        return 1;
    }
    CNA_GraphicsDeviceManagerHandle manager = 0;
    (void)cna_graphics_device_manager_create(game, &manager);
    (void)cna_game_run_one_frame(game);
    if (manager != 0) (void)cna_graphics_device_manager_destroy(manager);
    printf("game destroy            %s\n", name_of(cna_game_destroy(game)));
    printf("PROBE %s\n", ran ? "OK" : "INCOMPLETE");
    return ran ? 0 : 1;
}
