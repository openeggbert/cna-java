/* SPDX-License-Identifier: MS-PL */
/*
 * On a renderer that compiles shaders, what exactly are the engine layer's borrowed handles worth?
 *
 * `lent_handles.c` established the shape of the question on HEADLESS and could only half-answer
 * it: the caster and prepass effects came back `CNA_INVALID_HANDLE` because a renderer with no
 * shader compiler has no effect to lend, so there was nothing to measure a lifetime of. On
 * OPENGLES3 and OPENGL33 they come back valid, and the questions that decide whether a Java facade
 * can exist become answerable for all of them at once.
 *
 * Four questions per lender, none of which can be read off the declaration -- every one of these
 * routes says only "borrowed", and two routes that both say that turned out to require opposite
 * Java facades:
 *
 *   1. is the handle valid at all, or is this family still unsupported?
 *   2. is it the same handle every call, or minted fresh?
 *   3. does releasing it succeed?
 *   4. is destroying the LENDER refused while one is outstanding?
 *
 * Question four is the one that decides everything. A lender that refuses to die is a *blocking*
 * borrow: a Java facade must hold its parent and give the handle back, and a dangling pointer is
 * impossible because CNA will not let the parent go. A lender that dies happily and leaves the
 * handle valid is a *retaining* borrow: the facade may outlive its parent. A lender that dies and
 * leaves the handle dangling could not be projected at all.
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "CNA/C/engine_layer.h"
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

/* Reports one lent handle against all four questions.
 *
 * `first` and `second` are two separate calls to the same getter; `released` is the result of
 * releasing ONE of them; `lender_while_lent` is the lender's own destroy attempted while the
 * other is still outstanding; `lender_after` is the same destroy once nothing is out. A lender
 * whose destroy is refused at the first and succeeds at the second is counting. */
static void verdict(const char* label, CNA_Result got, CNA_Handle first, CNA_Handle second,
                    CNA_Result released, CNA_Result lender_while_lent, CNA_Result lender_after)
{
    const char* borrow;
    if (first == CNA_INVALID_HANDLE) {
        borrow = "unsupported";
    } else if (lender_while_lent == CNA_RESULT_INVALID_STATE &&
               lender_after == CNA_RESULT_SUCCESS) {
        borrow = "BLOCKING (lender counts, refuses while lent)";
    } else if (lender_while_lent == CNA_RESULT_SUCCESS) {
        borrow = "RETAINING (lender may go first)";
    } else {
        borrow = "see results";
    }
    printf("  %-34s %-8s %-7s %-16s release=%-16s lender_lent=%-16s lender_free=%-16s %s\n", label,
           name_of(got), first == CNA_INVALID_HANDLE ? "invalid" : "valid",
           first == second ? "same handle" : "fresh each call", name_of(released),
           name_of(lender_while_lent), name_of(lender_after), borrow);
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

    printf("\n== the four shadow maps' caster effects ==\n");
    {
        CNA_ShadowMapHandle map = 0;
        cna_shadow_map_create(device, CNA_SHADOW_QUALITY_LOW, &map);
        CNA_EffectHandle a = 0, b = 0;
        const CNA_Result got = cna_shadow_map_get_caster_effect(map, &a);
        cna_shadow_map_get_caster_effect(map, &b);
        const CNA_Result released = a != 0 ? cna_effect_destroy(a) : CNA_RESULT_INVALID_HANDLE;
        const CNA_Result blocked = cna_shadow_map_destroy(map);
        const CNA_Result freed = b != 0 ? cna_effect_destroy(b) : CNA_RESULT_INVALID_HANDLE;
        const CNA_Result after = blocked == CNA_RESULT_SUCCESS ? CNA_RESULT_SUCCESS
                                                               : cna_shadow_map_destroy(map);
        verdict("shadow_map caster", got, a, b, released, blocked, after);
        (void)freed;
    }
    {
        CNA_ShadowMapHandle map = 0;
        cna_shadow_map_create(device, CNA_SHADOW_QUALITY_LOW, &map);
        CNA_EffectHandle a = 0, b = 0;
        const CNA_Result got = cna_shadow_map_get_skinned_caster_effect(map, &a);
        cna_shadow_map_get_skinned_caster_effect(map, &b);
        const CNA_Result released = a != 0 ? cna_effect_destroy(a) : CNA_RESULT_INVALID_HANDLE;
        const CNA_Result blocked = cna_shadow_map_destroy(map);
        if (b != 0) cna_effect_destroy(b);
        const CNA_Result after = blocked == CNA_RESULT_SUCCESS ? CNA_RESULT_SUCCESS
                                                               : cna_shadow_map_destroy(map);
        verdict("shadow_map skinned caster", got, a, b, released, blocked, after);
    }
    {
        CNA_SpotShadowMapHandle map = 0;
        cna_spot_shadow_map_create(device, CNA_SHADOW_QUALITY_LOW, &map);
        CNA_EffectHandle a = 0, b = 0;
        const CNA_Result got = cna_spot_shadow_map_get_caster_effect(map, &a);
        cna_spot_shadow_map_get_caster_effect(map, &b);
        const CNA_Result released = a != 0 ? cna_effect_destroy(a) : CNA_RESULT_INVALID_HANDLE;
        const CNA_Result blocked = cna_spot_shadow_map_destroy(map);
        if (b != 0) cna_effect_destroy(b);
        const CNA_Result after = blocked == CNA_RESULT_SUCCESS ? CNA_RESULT_SUCCESS
                                                               : cna_spot_shadow_map_destroy(map);
        verdict("spot_shadow_map caster", got, a, b, released, blocked, after);
    }
    {
        CNA_CascadedShadowMapHandle map = 0;
        cna_cascaded_shadow_map_create(device, CNA_SHADOW_QUALITY_LOW, 3, &map);
        CNA_EffectHandle a = 0, b = 0;
        const CNA_Result got = cna_cascaded_shadow_map_get_caster_effect(map, &a);
        cna_cascaded_shadow_map_get_caster_effect(map, &b);
        const CNA_Result released = a != 0 ? cna_effect_destroy(a) : CNA_RESULT_INVALID_HANDLE;
        const CNA_Result blocked = cna_cascaded_shadow_map_destroy(map);
        if (b != 0) cna_effect_destroy(b);
        const CNA_Result after = blocked == CNA_RESULT_SUCCESS
                                     ? CNA_RESULT_SUCCESS
                                     : cna_cascaded_shadow_map_destroy(map);
        verdict("cascaded_shadow_map caster", got, a, b, released, blocked, after);
    }
    {
        CNA_CubeShadowMapHandle map = 0;
        cna_cube_shadow_map_create(device, CNA_SHADOW_QUALITY_LOW, &map);
        CNA_EffectHandle a = 0, b = 0;
        const CNA_Result got = cna_cube_shadow_map_get_caster_effect(map, &a);
        cna_cube_shadow_map_get_caster_effect(map, &b);
        const CNA_Result released = a != 0 ? cna_effect_destroy(a) : CNA_RESULT_INVALID_HANDLE;
        const CNA_Result blocked = cna_cube_shadow_map_destroy(map);
        if (b != 0) cna_effect_destroy(b);
        const CNA_Result after = blocked == CNA_RESULT_SUCCESS ? CNA_RESULT_SUCCESS
                                                               : cna_cube_shadow_map_destroy(map);
        verdict("cube_shadow_map caster", got, a, b, released, blocked, after);
    }

    printf("\n== the depth/normal prepass's two effects ==\n");
    {
        CNA_DepthNormalPrepassHandle prepass = 0;
        cna_depth_normal_prepass_create(device, 64, 64, CNA_DEPTH_ENCODING_AUTOMATIC, &prepass);
        CNA_EffectHandle a = 0, b = 0;
        const CNA_Result got = cna_depth_normal_prepass_get_prepass_effect(prepass, &a);
        cna_depth_normal_prepass_get_prepass_effect(prepass, &b);
        const CNA_Result released = a != 0 ? cna_effect_destroy(a) : CNA_RESULT_INVALID_HANDLE;
        const CNA_Result blocked = cna_depth_normal_prepass_destroy(prepass);
        if (b != 0) cna_effect_destroy(b);
        const CNA_Result after = blocked == CNA_RESULT_SUCCESS
                                     ? CNA_RESULT_SUCCESS
                                     : cna_depth_normal_prepass_destroy(prepass);
        verdict("depth_normal_prepass effect", got, a, b, released, blocked, after);
    }
    {
        CNA_DepthNormalPrepassHandle prepass = 0;
        cna_depth_normal_prepass_create(device, 64, 64, CNA_DEPTH_ENCODING_AUTOMATIC, &prepass);
        CNA_EffectHandle a = 0, b = 0;
        const CNA_Result got = cna_depth_normal_prepass_get_skinned_prepass_effect(prepass, &a);
        cna_depth_normal_prepass_get_skinned_prepass_effect(prepass, &b);
        const CNA_Result released = a != 0 ? cna_effect_destroy(a) : CNA_RESULT_INVALID_HANDLE;
        const CNA_Result blocked = cna_depth_normal_prepass_destroy(prepass);
        if (b != 0) cna_effect_destroy(b);
        const CNA_Result after = blocked == CNA_RESULT_SUCCESS
                                     ? CNA_RESULT_SUCCESS
                                     : cna_depth_normal_prepass_destroy(prepass);
        verdict("depth_normal_prepass skinned", got, a, b, released, blocked, after);
    }

    printf("\n== the clustered forward effect's three ==\n");
    {
        CNA_ClusteredForwardEffectHandle effect = 0;
        cna_clustered_forward_effect_create(device, &effect);
        CNA_Bool supported = CNA_FALSE;
        const CNA_Result asked = cna_clustered_forward_effect_is_supported(effect, &supported);
        printf("  clustered_forward is_supported     %s %s\n", name_of(asked),
               supported ? "yes" : "no");
        CNA_EffectHandle a = 0, b = 0;
        const CNA_Result got = cna_clustered_forward_effect_get_effect(effect, &a);
        cna_clustered_forward_effect_get_effect(effect, &b);
        const CNA_Result released = a != 0 ? cna_effect_destroy(a) : CNA_RESULT_INVALID_HANDLE;
        const CNA_Result blocked = cna_clustered_forward_effect_destroy(effect);
        if (b != 0) cna_effect_destroy(b);
        const CNA_Result after = blocked == CNA_RESULT_SUCCESS
                                     ? CNA_RESULT_SUCCESS
                                     : cna_clustered_forward_effect_destroy(effect);
        verdict("clustered_forward shader effect", got, a, b, released, blocked, after);
    }
    {
        CNA_ClusteredForwardEffectHandle effect = 0;
        cna_clustered_forward_effect_create(device, &effect);
        CNA_PbrMaterialExtensionsHandle a = 0, b = 0;
        const CNA_Result got = cna_clustered_forward_effect_get_material_extensions(effect, &a);
        cna_clustered_forward_effect_get_material_extensions(effect, &b);
        const CNA_Result released =
            a != 0 ? cna_pbr_material_extensions_destroy(a) : CNA_RESULT_INVALID_HANDLE;
        const CNA_Result blocked = cna_clustered_forward_effect_destroy(effect);
        if (b != 0) cna_pbr_material_extensions_destroy(b);
        const CNA_Result after = blocked == CNA_RESULT_SUCCESS
                                     ? CNA_RESULT_SUCCESS
                                     : cna_clustered_forward_effect_destroy(effect);
        verdict("clustered_forward extensions", got, a, b, released, blocked, after);
    }

    printf("\n== the weighted-blended transparency's two targets ==\n");
    {
        CNA_WeightedBlendedTransparencyHandle oit = 0;
        cna_weighted_blended_transparency_create(device, 64, 64, &oit);
        CNA_Bool supported = CNA_FALSE;
        const CNA_Result asked = cna_weighted_blended_transparency_is_supported(oit, &supported);
        printf("  oit is_supported                   %s %s\n", name_of(asked),
               supported ? "yes" : "no");
        CNA_Handle a = 0, b = 0;
        const CNA_Result got =
            cna_weighted_blended_transparency_get_accumulation_texture_ext(oit, &a);
        cna_weighted_blended_transparency_get_accumulation_texture_ext(oit, &b);
        const CNA_Result released = a != 0 ? cna_texture2d_destroy(a) : CNA_RESULT_INVALID_HANDLE;
        const CNA_Result blocked = cna_weighted_blended_transparency_destroy(oit);
        if (b != 0) cna_texture2d_destroy(b);
        const CNA_Result after = blocked == CNA_RESULT_SUCCESS
                                     ? CNA_RESULT_SUCCESS
                                     : cna_weighted_blended_transparency_destroy(oit);
        verdict("oit accumulation target", got, a, b, released, blocked, after);
    }
    {
        CNA_WeightedBlendedTransparencyHandle oit = 0;
        cna_weighted_blended_transparency_create(device, 64, 64, &oit);
        CNA_Handle a = 0, b = 0;
        const CNA_Result got = cna_weighted_blended_transparency_get_revealage_texture_ext(oit, &a);
        cna_weighted_blended_transparency_get_revealage_texture_ext(oit, &b);
        const CNA_Result released = a != 0 ? cna_texture2d_destroy(a) : CNA_RESULT_INVALID_HANDLE;
        const CNA_Result blocked = cna_weighted_blended_transparency_destroy(oit);
        if (b != 0) cna_texture2d_destroy(b);
        const CNA_Result after = blocked == CNA_RESULT_SUCCESS
                                     ? CNA_RESULT_SUCCESS
                                     : cna_weighted_blended_transparency_destroy(oit);
        verdict("oit revealage target", got, a, b, released, blocked, after);
    }

    printf("\n== the colour-grade pass's two LUTs, with nothing bound ==\n");
    {
        CNA_PostProcessPassHandle pass = 0;
        const CNA_Result made = cna_color_grade_pass_create(device, &pass);
        CNA_Handle strip = 0;
        CNA_Handle volume = 0;
        const CNA_Result got_strip = cna_color_grade_pass_get_lut(pass, &strip);
        const CNA_Result got_volume = cna_color_grade_pass_get_volume_lut(pass, &volume);
        printf("  color_grade create %s  strip=%s %s  volume=%s %s\n", name_of(made),
               name_of(got_strip), strip == 0 ? "invalid" : "valid", name_of(got_volume),
               volume == 0 ? "invalid" : "valid");
        printf("  color_grade destroy                %s\n",
               name_of(cna_post_process_pass_destroy(pass)));
    }

    printf("\n== the ASCII pass's effect ==\n");
    {
        CNA_PostProcessPassHandle pass = 0;
        cna_ascii_pass_create(device, &pass);
        CNA_EffectHandle a = 0, b = 0;
        const CNA_Result got = cna_ascii_pass_get_effect(pass, &a);
        cna_ascii_pass_get_effect(pass, &b);
        const CNA_Result released = a != 0 ? cna_effect_destroy(a) : CNA_RESULT_INVALID_HANDLE;
        const CNA_Result blocked = cna_post_process_pass_destroy(pass);
        if (b != 0) cna_effect_destroy(b);
        const CNA_Result after =
            blocked == CNA_RESULT_SUCCESS ? CNA_RESULT_SUCCESS : cna_post_process_pass_destroy(pass);
        verdict("ascii_pass effect", got, a, b, released, blocked, after);
    }

    printf("\n== the render pipeline's three ==\n");
    {
        CNA_RenderPipelineHandle pipeline = 0;
        cna_render_pipeline_create(device, &pipeline);
        cna_render_pipeline_resize(pipeline, 128, 128);
        CNA_ShadowMapHandle map = 0;
        cna_shadow_map_create(device, CNA_SHADOW_QUALITY_LOW, &map);
        CNA_DirectionalLightEXT light;
        memset(&light, 0, sizeof light);
        cna_directional_light_ext_init(&light);
        CNA_BoundingBox bounds;
        bounds.min.x = -1.0F; bounds.min.y = -1.0F; bounds.min.z = -1.0F;
        bounds.max.x = 1.0F;  bounds.max.y = 1.0F;  bounds.max.z = 1.0F;
        cna_render_pipeline_set_shadow_scene(pipeline, map, &light, &bounds, NULL, NULL);

        CNA_ShadowMapHandle lent_a = 0, lent_b = 0;
        const CNA_Result got_map = cna_render_pipeline_get_shadow_map(pipeline, &lent_a);
        cna_render_pipeline_get_shadow_map(pipeline, &lent_b);
        printf("  pipeline shadow map                %s %s %s  same as the one given: %s\n",
               name_of(got_map), lent_a == 0 ? "invalid" : "valid",
               lent_a == lent_b ? "same handle" : "fresh each call",
               lent_a == map ? "yes" : "no");

        /* The scene target exists only while the pipeline wants one, so ask before and after
           turning on something that makes it want one. */
        CNA_Handle target_off = 0;
        const CNA_Result got_off = cna_render_pipeline_get_scene_target(pipeline, &target_off);
        CNA_RenderPipelineSettingsEXT settings;
        memset(&settings, 0, sizeof settings);
        settings.struct_size = (uint32_t)(sizeof settings);
        settings.struct_version = 1U;
        cna_render_pipeline_get_settings(pipeline, &settings);
        settings.hdr_enabled = CNA_TRUE;
        cna_render_pipeline_set_settings(pipeline, &settings);
        const CNA_Color clear = { 0U, 0U, 0U, 255U };
        /* Asked from inside the frame as well as after it, because the pipeline builds the
           target when a frame opens and may not keep it afterwards -- and inside the frame is
           when a game would want it, to feed a pass of its own. */
        cna_render_pipeline_begin(pipeline, &clear);
        CNA_Handle inside_a = 0, inside_b = 0;
        const CNA_Result got_inside = cna_render_pipeline_get_scene_target(pipeline, &inside_a);
        cna_render_pipeline_get_scene_target(pipeline, &inside_b);
        cna_render_pipeline_end(pipeline);
        CNA_Handle target_a = 0, target_b = 0;
        const CNA_Result got_on = cna_render_pipeline_get_scene_target(pipeline, &target_a);
        cna_render_pipeline_get_scene_target(pipeline, &target_b);
        CNA_Bool using_target = CNA_FALSE;
        cna_render_pipeline_is_using_scene_target(pipeline, &using_target);
        printf("  pipeline scene target              off=%s %s  inside=%s %s %s  "
               "after=%s %s  using=%s\n",
               name_of(got_off), target_off == 0 ? "invalid" : "valid", name_of(got_inside),
               inside_a == 0 ? "invalid" : "valid",
               inside_a == inside_b ? "same handle" : "fresh each call", name_of(got_on),
               target_a == 0 ? "invalid" : "valid", using_target ? "yes" : "no");
        if (inside_a != 0) {
            printf("  scene target release               %s\n",
                   name_of(cna_texture2d_destroy(inside_a)));
        }
        if (inside_b != 0) cna_texture2d_destroy(inside_b);
        if (target_a != 0) cna_texture2d_destroy(target_a);
        if (target_b != 0) cna_texture2d_destroy(target_b);

        CNA_SkyboxHandle sky_a = 0, sky_b = 0;
        const CNA_Result got_sky = cna_render_pipeline_get_skybox(pipeline, &sky_a);
        cna_render_pipeline_get_skybox(pipeline, &sky_b);
        printf("  pipeline skybox, none set          %s %s\n", name_of(got_sky),
               sky_a == 0 ? "invalid" : "valid");

        printf("  pipeline destroy                   %s\n",
               name_of(cna_render_pipeline_destroy(pipeline)));
        printf("  shadow map destroy                 %s\n", name_of(cna_shadow_map_destroy(map)));
    }

    return CNA_RESULT_SUCCESS;
}

int main(void)
{
    const char* requested = getenv("CNA_GRAPHICS_RENDERER");
    printf("renderer requested %s\n", requested != NULL ? requested : "<build default>");

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
    info.window_title.data = "lent effect lifetime";
    info.window_title.byte_length = 20U;
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
    printf("\ngame destroy       %s\n", name_of(cna_game_destroy(game)));
    printf("PROBE %s\n", ran ? "OK" : "INCOMPLETE");
    return ran ? 0 : 1;
}
