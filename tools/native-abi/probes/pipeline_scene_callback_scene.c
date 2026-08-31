/* SPDX-License-Identifier: MS-PL */
/*
 * Does a render pipeline enter its scene callbacks when the settings ask for the passes that
 * would call them?
 *
 * `pipeline_scene_callbacks.c` registered both callbacks, ran a whole frame and saw neither
 * entered. That was read at the time as a renderer boundary -- HEADLESS compiles no shaders, so
 * there is no transparent pass and no shadow pass to call anyone from -- and the two routes were
 * left unbound for the same reason the light-probe baker's bake routes were.
 *
 * Re-running it on a renderer that does compile shaders, bakes cube faces and reads pixels back
 * changed nothing at all: still zero calls. So the reading was wrong, and the real gate is
 * somewhere else. This probe finds it and pins it, because "the callback is never entered" and
 * "the callback is never entered with the settings the earlier probe used" are different facts,
 * and only the first would justify leaving the routes unbound.
 *
 * It also asks the four questions a JNI trampoline needs answered before it can be written: how
 * many times per frame each callback runs, in what order relative to each other, whether the
 * result a callback returns reaches the caller of `begin`, and whether a failing callback stops
 * the frame or is swallowed.
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "CNA/C/engine_layer.h"
#include "CNA/C/cna.h"

static int ran = 0;
static int transparent_calls = 0;
static int shadow_calls = 0;
static int sequence = 0;
static int transparent_at = 0;
static int shadow_at = 0;
static CNA_Result transparent_answer = CNA_RESULT_SUCCESS;

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

static const char* transparency_name(const CNA_TransparencyMode mode)
{
    switch ((unsigned)mode) {
        case 0U: return "NONE";
        case 1U: return "SORTED";
        case 2U: return "ORDER_INDEPENDENT";
        default: return "OTHER";
    }
}

static CNA_Result on_transparent(void* context)
{
    (void)context;
    transparent_calls++;
    transparent_at = ++sequence;
    return transparent_answer;
}

static CNA_Result on_shadow(void* context)
{
    (void)context;
    shadow_calls++;
    shadow_at = ++sequence;
    return CNA_RESULT_SUCCESS;
}

/* One frame with a stated configuration, reporting what ran. */
static void frame(CNA_RenderPipelineHandle pipeline, const char* label)
{
    transparent_calls = 0;
    shadow_calls = 0;
    sequence = 0;
    transparent_at = 0;
    shadow_at = 0;
    const CNA_Color clear = { 0U, 0U, 0U, 255U };
    const CNA_Result began = cna_render_pipeline_begin(pipeline, &clear);
    const CNA_Result ended = cna_render_pipeline_end(pipeline);
    CNA_Bool shadow_ran = CNA_FALSE;
    const CNA_Result asked = cna_render_pipeline_did_shadow_pass_run(pipeline, &shadow_ran);
    int32_t passes = 0;
    cna_render_pipeline_get_last_frame_pass_count(pipeline, &passes);
    printf("  %-30s begin=%-16s end=%-16s shadow=%d(at %d) transparent=%d(at %d) "
           "shadow_pass_ran=%s passes=%d\n",
           label, name_of(began), name_of(ended), shadow_calls, shadow_at, transparent_calls,
           transparent_at, asked == CNA_RESULT_SUCCESS && shadow_ran ? "yes" : "no", (int)passes);
    fflush(stdout);
}

static CNA_Result on_update(CNA_Handle game, const CNA_GameTime* game_time, void* context,
                            CNA_CallbackError* out_error)
{
    (void)game_time;
    (void)out_error;
    (void)context;
    if (ran) return CNA_RESULT_SUCCESS;
    ran = 1;

    CNA_Handle device = CNA_INVALID_HANDLE;
    if (cna_game_get_graphics_device(game, &device) != CNA_RESULT_SUCCESS) {
        printf("no device\n");
        return CNA_RESULT_SUCCESS;
    }

    CNA_RenderPipelineHandle pipeline = 0;
    printf("pipeline create                %s\n",
           name_of(cna_render_pipeline_create(device, &pipeline)));
    printf("set transparent scene          %s\n",
           name_of(cna_render_pipeline_set_transparent_scene(pipeline, on_transparent, NULL)));

    CNA_ShadowMapHandle map = 0;
    printf("shadow map create              %s\n",
           name_of(cna_shadow_map_create(device, CNA_SHADOW_QUALITY_LOW, &map)));
    CNA_Bool shadow_supported = CNA_FALSE;
    const CNA_Result asked_shadow = cna_shadow_map_is_supported(map, &shadow_supported);
    printf("shadow map supported           %s %s\n", name_of(asked_shadow),
           shadow_supported ? "yes" : "no");

    CNA_DirectionalLightEXT light;
    memset(&light, 0, sizeof light);
    (void)cna_directional_light_ext_init(&light);
    CNA_BoundingBox bounds;
    bounds.min.x = -10.0F; bounds.min.y = -10.0F; bounds.min.z = -10.0F;
    bounds.max.x = 10.0F;  bounds.max.y = 10.0F;  bounds.max.z = 10.0F;
    printf("set shadow scene               %s\n",
           name_of(cna_render_pipeline_set_shadow_scene(pipeline, map, &light, &bounds, on_shadow,
                                                        NULL)));

    CNA_Vector3 eye = { 0.0F, 0.0F, 4.0F };
    CNA_Vector3 target = { 0.0F, 0.0F, 0.0F };
    CNA_Vector3 up = { 0.0F, 1.0F, 0.0F };
    CNA_Matrix view;
    CNA_Matrix projection;
    (void)cna_matrix_create_look_at(eye, target, up, &view);
    (void)cna_matrix_create_perspective_field_of_view(1.0F, 4.0F / 3.0F, 0.5F, 200.0F, &projection);
    printf("resize                         %s\n",
           name_of(cna_render_pipeline_resize(pipeline, 320, 240)));
    printf("set camera                     %s\n",
           name_of(cna_render_pipeline_set_camera(pipeline, &view, &projection, 0.5F, 200.0F)));

    /* What the pipeline was actually configured as when the earlier probe ran. */
    CNA_RenderPipelineSettingsEXT settings;
    memset(&settings, 0, sizeof settings);
    settings.struct_size = (uint32_t)(sizeof settings);
    settings.struct_version = 1U;
    const CNA_Result read_settings = cna_render_pipeline_get_settings(pipeline, &settings);
    printf("default settings               %s shadows=%s transparency=%s\n",
           name_of(read_settings), settings.shadows_enabled ? "on" : "off",
           transparency_name(settings.transparency_mode));

    printf("\n== frames ==\n");
    frame(pipeline, "defaults");

    settings.shadows_enabled = CNA_TRUE;
    printf("  set shadows on                 %s\n",
           name_of(cna_render_pipeline_set_settings(pipeline, &settings)));
    frame(pipeline, "shadows on");

    settings.transparency_mode = CNA_TRANSPARENCY_MODE_SORTED;
    printf("  set transparency sorted        %s\n",
           name_of(cna_render_pipeline_set_settings(pipeline, &settings)));
    frame(pipeline, "shadows on, sorted");

    settings.transparency_mode = CNA_TRANSPARENCY_MODE_ORDER_INDEPENDENT;
    printf("  set transparency independent   %s\n",
           name_of(cna_render_pipeline_set_settings(pipeline, &settings)));
    frame(pipeline, "shadows on, order independent");
    char reason[512];
    uint64_t reason_bytes = 0;
    if (cna_render_pipeline_copy_transparency_fallback_reason_ext(pipeline, reason,
                                                                  sizeof reason - 1,
                                                                  &reason_bytes) ==
            CNA_RESULT_SUCCESS && reason_bytes > 0 && reason_bytes < sizeof reason) {
        reason[reason_bytes] = '\0';
        printf("  transparency fallback reason   %s\n", reason);
    } else {
        printf("  transparency fallback reason   <none>\n");
    }

    settings.transparency_mode = CNA_TRANSPARENCY_MODE_SORTED;
    (void)cna_render_pipeline_set_settings(pipeline, &settings);

    /* Does a failing callback reach the caller of begin, and does it stop the frame? */
    transparent_answer = CNA_RESULT_INVALID_STATE;
    frame(pipeline, "transparent callback fails");
    transparent_answer = CNA_RESULT_SUCCESS;

    /* Two frames in a row, so "once per frame" is a count rather than a coincidence. */
    frame(pipeline, "second frame");

    printf("\n== clearing ==\n");
    printf("  clear transparent              %s\n",
           name_of(cna_render_pipeline_set_transparent_scene(pipeline, NULL, NULL)));
    frame(pipeline, "transparent cleared");
    printf("  clear shadow                   %s\n",
           name_of(cna_render_pipeline_set_shadow_scene(pipeline, map, &light, &bounds, NULL,
                                                        NULL)));
    frame(pipeline, "both cleared");
    printf("  clear shadow map               %s\n",
           name_of(cna_render_pipeline_set_shadow_scene(pipeline, CNA_INVALID_HANDLE, &light,
                                                        &bounds, on_shadow, NULL)));
    frame(pipeline, "no shadow map");

    printf("\npipeline destroy               %s\n",
           name_of(cna_render_pipeline_destroy(pipeline)));
    printf("shadow map destroy             %s\n", name_of(cna_shadow_map_destroy(map)));
    return CNA_RESULT_SUCCESS;
}

int main(void)
{
    const char* requested = getenv("CNA_GRAPHICS_RENDERER");
    printf("renderer requested             %s\n",
           requested != NULL ? requested : "<build default>");

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
    info.window_title.data = "pipeline scene callbacks with a scene";
    info.window_title.byte_length = 36U;
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
    printf("game destroy                   %s\n", name_of(cna_game_destroy(game)));
    printf("PROBE %s\n", ran ? "OK" : "INCOMPLETE");
    return ran ? 0 : 1;
}
