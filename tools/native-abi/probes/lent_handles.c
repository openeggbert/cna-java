/* SPDX-License-Identifier: MS-PL */
/*
 * On what terms does the engine layer lend the handles it says are "borrowed"?
 *
 * A dozen routes hand back an effect or a texture that belongs to something else, and their
 * documentation divides into two shapes. Some say exactly what a borrow is worth -- *"it keeps the
 * table alive while it exists, and releasing it releases only the handle"* -- which is a counted
 * borrow a Java facade can hold and give back. The rest say only "borrowed from the map", with no
 * release route and nothing about whether the lender may be destroyed underneath it.
 *
 * Three questions decide whether the second shape can be projected at all, and none of them can be
 * read off the declaration: is the handle stable across calls or minted fresh, does releasing it
 * succeed, and is destroying the lender refused while it is out. A Java facade over a handle that
 * is fresh every call and cannot be released is an unbounded leak; one over a handle whose lender
 * can vanish is a dangling pointer.
 */
#include <stdio.h>
#include <string.h>

#include "CNA/C/engine_layer.h"
#include "CNA/C/cna.h"

static int ran = 0;

static void report(const char* what, CNA_Result got, CNA_Handle first, CNA_Handle second)
{
    printf("%-26s %d  %s, %s\n", what, (int)got,
        first == 0 ? "invalid" : "valid",
        first == second ? "stable across calls" : "fresh each call");
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

    /* The shape whose prose says nothing: a shadow map's caster effect. */
    CNA_ShadowMapHandle map = 0;
    printf("shadow map create          %d\n",
        (int)cna_shadow_map_create(device, CNA_SHADOW_QUALITY_LOW, &map));
    CNA_EffectHandle caster = 0;
    CNA_EffectHandle caster_again = 0;
    CNA_Result got = cna_shadow_map_get_caster_effect(map, &caster);
    (void)cna_shadow_map_get_caster_effect(map, &caster_again);
    report("caster effect", got, caster, caster_again);
    if (caster != 0) {
        printf("release caster             %d\n", (int)cna_effect_destroy(caster));
        printf("destroy map while lent     %d\n", (int)cna_shadow_map_destroy(map));
    } else {
        printf("destroy map                %d\n", (int)cna_shadow_map_destroy(map));
    }

    /* The depth/normal prepass lends two of them. */
    CNA_DepthNormalPrepassHandle prepass = 0;
    printf("prepass create             %d\n",
        (int)cna_depth_normal_prepass_create(device, 64, 64, CNA_DEPTH_ENCODING_AUTOMATIC, &prepass));
    CNA_EffectHandle rigid = 0;
    CNA_EffectHandle rigid_again = 0;
    got = cna_depth_normal_prepass_get_prepass_effect(prepass, &rigid);
    (void)cna_depth_normal_prepass_get_prepass_effect(prepass, &rigid_again);
    report("prepass effect", got, rigid, rigid_again);
    if (rigid != 0) {
        printf("release prepass effect     %d\n", (int)cna_effect_destroy(rigid));
    }
    printf("destroy prepass            %d\n",
        (int)cna_depth_normal_prepass_destroy(prepass));

    /* And the shape whose prose is explicit: a BRDF table's texture. */
    CNA_AreaLightBrdfTableHandle table = 0;
    printf("brdf table create          %d\n",
        (int)cna_area_light_brdf_table_create(device, &table));
    CNA_Handle texture = 0;
    CNA_Handle texture_again = 0;
    got = cna_area_light_brdf_table_get_texture(table, &texture);
    (void)cna_area_light_brdf_table_get_texture(table, &texture_again);
    report("brdf texture", got, texture, texture_again);
    if (texture != 0) {
        printf("destroy table while lent   %d\n",
            (int)cna_area_light_brdf_table_destroy(table));
        printf("release brdf texture       %d\n", (int)cna_texture2d_destroy(texture));
        if (texture_again != 0 && texture_again != texture) {
            (void)cna_texture2d_destroy(texture_again);
        }
    }
    printf("destroy table              %d\n",
        (int)cna_area_light_brdf_table_destroy(table));

    /* The skybox makes the same promise about its environment, in the same words. */
    CNA_TextureCubeCreateInfo cube_info;
    memset(&cube_info, 0, sizeof cube_info);
    cube_info.struct_size = (uint32_t)(sizeof cube_info);
    cube_info.struct_version = 1U;
    cube_info.size = 4U;
    cube_info.format = CNA_SURFACE_FORMAT_COLOR;
    CNA_Handle environment = 0;
    printf("environment cube           %d\n",
        (int)cna_texturecube_create(device, &cube_info, &environment));
    CNA_SkyboxHandle skybox = 0;
    printf("skybox create              %d\n",
        (int)cna_skybox_create(device, environment, &skybox));
    CNA_Handle cube = 0;
    CNA_Handle cube_again = 0;
    got = cna_skybox_get_environment(skybox, &cube);
    (void)cna_skybox_get_environment(skybox, &cube_again);
    report("skybox environment", got, cube, cube_again);
    if (cube != 0) {
        printf("destroy skybox while lent  %d\n", (int)cna_skybox_destroy(skybox));
        printf("release environment        %d\n", (int)cna_texturecube_destroy(cube));
        if (cube_again != 0 && cube_again != cube) {
            (void)cna_texturecube_destroy(cube_again);
        }
    } else {
        printf("destroy skybox             %d\n", (int)cna_skybox_destroy(skybox));
    }
    (void)cna_texturecube_destroy(environment);

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
    info.window_title.data = "lent handles probe";
    info.window_title.byte_length = 18U;
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
    printf("game destroy               %d\n", (int)cna_game_destroy(game));
    printf("PROBE %s\n", ran ? "OK" : "INCOMPLETE");
    return ran ? 0 : 1;
}
