/* SPDX-License-Identifier: MS-PL */
/*
 * Does the render pipeline ever enter the scene callbacks a game registers?
 *
 * Two routes register a callback that CNA is documented to run inside the frame -- one for
 * transparent geometry, one for shadow casters -- and the transparent draw list has already shown
 * that a callback invoked *during* a call can be projected with a JNI trampoline that leaks
 * nothing. These are registered once and invoked later, which is a harder shape, and the shape
 * only matters if the callback runs at all: the light-probe baker's does not on this renderer, and
 * a trampoline nobody can enter is code no test could execute.
 */
#include <stdio.h>
#include <string.h>

#include "CNA/C/engine_layer.h"
#include "CNA/C/cna.h"

static int ran = 0;
static int transparent_calls = 0;
static int shadow_calls = 0;

static CNA_Result on_transparent(void* context)
{
    (void)context;
    transparent_calls++;
    return CNA_RESULT_SUCCESS;
}

static CNA_Result on_shadow(void* context)
{
    (void)context;
    shadow_calls++;
    return CNA_RESULT_SUCCESS;
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

    CNA_RenderPipelineHandle pipeline = 0;
    printf("pipeline create        %d\n",
        (int)cna_render_pipeline_create(device, &pipeline));
    printf("set transparent scene  %d\n",
        (int)cna_render_pipeline_set_transparent_scene(pipeline, on_transparent, NULL));
    CNA_ShadowMapHandle map = 0;
    printf("shadow map create      %d\n",
        (int)cna_shadow_map_create(device, CNA_SHADOW_QUALITY_LOW, &map));
    CNA_DirectionalLightEXT light;
    memset(&light, 0, sizeof light);
    (void)cna_directional_light_ext_init(&light);
    CNA_BoundingBox bounds;
    bounds.min.x = -10.0f; bounds.min.y = -10.0f; bounds.min.z = -10.0f;
    bounds.max.x = 10.0f; bounds.max.y = 10.0f; bounds.max.z = 10.0f;
    printf("set shadow scene       %d\n",
        (int)cna_render_pipeline_set_shadow_scene(pipeline, map, &light, &bounds, on_shadow,
            NULL));

    CNA_Vector3 eye = {0.0f, 0.0f, 4.0f};
    CNA_Vector3 target = {0.0f, 0.0f, 0.0f};
    CNA_Vector3 up = {0.0f, 1.0f, 0.0f};
    CNA_Matrix view;
    CNA_Matrix projection;
    (void)cna_matrix_create_look_at(eye, target, up, &view);
    (void)cna_matrix_create_perspective_field_of_view(1.0f, 4.0f / 3.0f, 0.5f, 200.0f,
        &projection);
    printf("resize                 %d\n",
        (int)cna_render_pipeline_resize(pipeline, 320, 240));
    printf("set camera             %d\n",
        (int)cna_render_pipeline_set_camera(pipeline, &view, &projection, 0.5f, 200.0f));
    CNA_Color clear = {0, 0, 0, 255};
    printf("begin                  %d\n", (int)cna_render_pipeline_begin(pipeline, &clear));
    printf("end                    %d\n", (int)cna_render_pipeline_end(pipeline));
    printf("transparent callback   %d call(s)\n", transparent_calls);
    printf("shadow callback        %d call(s)\n", shadow_calls);

    /* Clearing them is the other half of the contract. */
    printf("clear transparent      %d\n",
        (int)cna_render_pipeline_set_transparent_scene(pipeline, NULL, NULL));
    printf("clear shadow           %d\n",
        (int)cna_render_pipeline_set_shadow_scene(pipeline, map, &light, &bounds, NULL, NULL));
    printf("pipeline destroy       %d\n", (int)cna_render_pipeline_destroy(pipeline));
    (void)cna_shadow_map_destroy(map);
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
    info.window_title.data = "pipeline scene callback probe";
    info.window_title.byte_length = 29U;
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
