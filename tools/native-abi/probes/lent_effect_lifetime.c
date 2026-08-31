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
        /* The lent handle is an ASCII effect, not a shader Effect: graphics_ext.h says in as many
           words that it "is not accepted by the cna_effect_* routes". The first version of this
           block released it with cna_effect_destroy anyway and got INVALID_HANDLE, which reads
           exactly like a lender that refuses to give its borrow back. It was the wrong route. */
        CNA_PostProcessPassHandle pass = 0;
        cna_ascii_pass_create(device, &pass);
        CNA_AsciiPostProcessEffectHandle a = 0, b = 0;
        const CNA_Result got = cna_ascii_pass_get_effect(pass, &a);
        cna_ascii_pass_get_effect(pass, &b);
        const CNA_Result released =
            a != 0 ? cna_ascii_post_process_effect_destroy(a) : CNA_RESULT_INVALID_HANDLE;
        const CNA_Result blocked = cna_post_process_pass_destroy(pass);
        if (b != 0) cna_ascii_post_process_effect_destroy(b);
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

    /* Every case above asks a lender that is holding nothing, or holding something it made
       itself. Five getters lend a thing the CALLER gave the lender, and for those the question
       that decides the Java facade is a different one: is the handle that comes back the same
       name the caller handed in, or a fresh name for the same object? A same-name lend needs no
       Java facade at all -- the caller already has the object -- while a fresh-name lend must be
       released, and a facade that forgets is a leak on every read. */
    printf("\n== five getters, asked while the lender is actually holding something ==\n");
    {
        /* A skybox is created around an environment, so this one cannot be asked empty. */
        CNA_TextureCubeCreateInfo cube_info;
        memset(&cube_info, 0, sizeof cube_info);
        cube_info.struct_size = (uint32_t)(sizeof cube_info);
        cube_info.struct_version = 1U;
        cube_info.size = 16U;
        cube_info.mip_map = CNA_FALSE;
        cube_info.format = CNA_SURFACE_FORMAT_COLOR;
        CNA_Handle cube = 0;
        const CNA_Result made_cube = cna_texturecube_create(device, &cube_info, &cube);
        CNA_SkyboxHandle skybox = 0;
        const CNA_Result made_sky = cna_skybox_create(device, cube, &skybox);
        CNA_Handle env_a = 0, env_b = 0;
        const CNA_Result got = cna_skybox_get_environment(skybox, &env_a);
        cna_skybox_get_environment(skybox, &env_b);
        printf("  skybox environment                 cube=%s sky=%s get=%s %s %s  "
               "same as the one given: %s\n",
               name_of(made_cube), name_of(made_sky), name_of(got),
               env_a == 0 ? "invalid" : "valid",
               env_a == env_b ? "same handle" : "fresh each call", env_a == cube ? "yes" : "no");
        const CNA_Result released = env_a != 0 ? cna_texturecube_destroy(env_a)
                                               : CNA_RESULT_INVALID_HANDLE;
        const CNA_Result blocked = cna_skybox_destroy(skybox);
        if (env_b != 0 && env_b != env_a) cna_texturecube_destroy(env_b);
        const CNA_Result after =
            blocked == CNA_RESULT_SUCCESS ? CNA_RESULT_SUCCESS : cna_skybox_destroy(skybox);
        printf("  skybox environment lifetime        release=%s lender_lent=%s lender_free=%s\n",
               name_of(released), name_of(blocked), name_of(after));

        /* And the pipeline's skybox, which the earlier block could only ask with none set. */
        CNA_RenderPipelineHandle pipeline = 0;
        cna_render_pipeline_create(device, &pipeline);
        CNA_SkyboxHandle second = 0;
        cna_skybox_create(device, cube, &second);
        const CNA_Result set = cna_render_pipeline_set_skybox(pipeline, second);
        CNA_SkyboxHandle sky_a = 0, sky_b = 0;
        const CNA_Result got_sky = cna_render_pipeline_get_skybox(pipeline, &sky_a);
        cna_render_pipeline_get_skybox(pipeline, &sky_b);
        printf("  pipeline skybox, one set           set=%s get=%s %s %s  "
               "same as the one given: %s\n",
               name_of(set), name_of(got_sky), sky_a == 0 ? "invalid" : "valid",
               sky_a == sky_b ? "same handle" : "fresh each call", sky_a == second ? "yes" : "no");
        const CNA_Result sky_released = sky_a != 0 && sky_a != second
                                            ? cna_skybox_destroy(sky_a)
                                            : CNA_RESULT_INVALID_HANDLE;
        const CNA_Result sky_blocked = cna_render_pipeline_destroy(pipeline);
        printf("  pipeline skybox lifetime           release=%s pipeline_destroy=%s\n",
               name_of(sky_released), name_of(sky_blocked));
        cna_skybox_destroy(second);
        if (cube != 0) cna_texturecube_destroy(cube);
    }
    {
        /* The colour-grade pass's two LUTs, this time with LUTs bound. */
        CNA_PostProcessPassHandle pass = 0;
        cna_color_grade_pass_create(device, &pass);
        CNA_Handle strip = 0;
        const CNA_Result made_strip = cna_color_grade_pass_create_identity_lut(device, 8, &strip);
        const CNA_Result set_strip = cna_color_grade_pass_set_lut(pass, strip);
        CNA_Handle lut_a = 0, lut_b = 0;
        const CNA_Result got_strip = cna_color_grade_pass_get_lut(pass, &lut_a);
        cna_color_grade_pass_get_lut(pass, &lut_b);
        printf("  color_grade strip LUT, one set     make=%s set=%s get=%s %s %s  "
               "same as the one given: %s\n",
               name_of(made_strip), name_of(set_strip), name_of(got_strip),
               lut_a == 0 ? "invalid" : "valid",
               lut_a == lut_b ? "same handle" : "fresh each call", lut_a == strip ? "yes" : "no");

        CNA_Texture3DCreateInfo volume_info;
        memset(&volume_info, 0, sizeof volume_info);
        volume_info.struct_size = (uint32_t)(sizeof volume_info);
        volume_info.struct_version = 1U;
        volume_info.width = 8U;
        volume_info.height = 8U;
        volume_info.depth = 8U;
        volume_info.mip_map = CNA_FALSE;
        volume_info.format = CNA_SURFACE_FORMAT_COLOR;
        CNA_Handle volume = 0;
        const CNA_Result made_volume = cna_texture3d_create(device, &volume_info, &volume);
        const CNA_Result set_volume = cna_color_grade_pass_set_volume_lut(pass, volume);
        CNA_Handle vol_a = 0, vol_b = 0;
        const CNA_Result got_volume = cna_color_grade_pass_get_volume_lut(pass, &vol_a);
        cna_color_grade_pass_get_volume_lut(pass, &vol_b);
        printf("  color_grade volume LUT, one set    make=%s set=%s get=%s %s %s  "
               "same as the one given: %s\n",
               name_of(made_volume), name_of(set_volume), name_of(got_volume),
               vol_a == 0 ? "invalid" : "valid",
               vol_a == vol_b ? "same handle" : "fresh each call", vol_a == volume ? "yes" : "no");
        /* The declaration says the volume LUT must be "a cube with an edge between two and
           CNA_COLOR_GRADE_MAX_LUT_SIZE_EXT", which reads two ways: a cubical Texture3D, or a
           TextureCube. Java's ColorGradePass.setVolumeLut takes a TextureCube. Ask CNA which one
           it means, because if it accepts both then one of them is sampled wrong in silence. */
        CNA_TextureCubeCreateInfo cube_lut_info;
        memset(&cube_lut_info, 0, sizeof cube_lut_info);
        cube_lut_info.struct_size = (uint32_t)(sizeof cube_lut_info);
        cube_lut_info.struct_version = 1U;
        cube_lut_info.size = 8U;
        cube_lut_info.mip_map = CNA_FALSE;
        cube_lut_info.format = CNA_SURFACE_FORMAT_COLOR;
        CNA_Handle cube_lut = 0;
        const CNA_Result made_cube_lut = cna_texturecube_create(device, &cube_lut_info, &cube_lut);
        const CNA_Result set_cube_lut = cna_color_grade_pass_set_volume_lut(pass, cube_lut);
        printf("  color_grade volume LUT as a cube   make=%s set=%s\n", name_of(made_cube_lut),
               name_of(set_cube_lut));
        /* And the shape of a Texture3D it will take, so a Java facade can refuse the same
           things with the same message rather than forwarding them and reading a code back. */
        {
            const struct { uint32_t w, h, d; const char* what; } shapes[] = {
                { 8U, 8U, 4U, "not cubical" },
                { 1U, 1U, 1U, "edge 1" },
                { 2U, 2U, 2U, "edge 2" },
                { 64U, 64U, 64U, "edge 64" },
            };
            for (size_t i = 0; i < sizeof shapes / sizeof shapes[0]; i++) {
                CNA_Texture3DCreateInfo shape_info;
                memset(&shape_info, 0, sizeof shape_info);
                shape_info.struct_size = (uint32_t)(sizeof shape_info);
                shape_info.struct_version = 1U;
                shape_info.width = shapes[i].w;
                shape_info.height = shapes[i].h;
                shape_info.depth = shapes[i].d;
                shape_info.format = CNA_SURFACE_FORMAT_COLOR;
                CNA_Handle shaped = 0;
                const CNA_Result made_shape = cna_texture3d_create(device, &shape_info, &shaped);
                const CNA_Result set_shape = cna_color_grade_pass_set_volume_lut(pass, shaped);
                printf("  volume LUT %-14s %ux%ux%u  make=%s set=%s\n", shapes[i].what,
                       shapes[i].w, shapes[i].h, shapes[i].d, name_of(made_shape),
                       name_of(set_shape));
                if (set_shape == CNA_RESULT_SUCCESS) {
                    cna_color_grade_pass_set_volume_lut(pass, volume);
                }
                if (shaped != 0) cna_texture3d_destroy(shaped);
            }
        }

        if (cube_lut != 0) {
            /* Put the Texture3D back so the release bookkeeping below still describes the pass. */
            cna_color_grade_pass_set_volume_lut(pass, volume);
            cna_texturecube_destroy(cube_lut);
        }

        const CNA_Result blocked = cna_post_process_pass_destroy(pass);
        printf("  color_grade destroy while lent     %s\n", name_of(blocked));
        if (lut_a != 0 && lut_a != strip) cna_texture2d_destroy(lut_a);
        if (lut_b != 0 && lut_b != strip && lut_b != lut_a) cna_texture2d_destroy(lut_b);
        if (vol_a != 0 && vol_a != volume) cna_texture3d_destroy(vol_a);
        if (vol_b != 0 && vol_b != volume && vol_b != vol_a) cna_texture3d_destroy(vol_b);
        if (strip != 0) cna_texture2d_destroy(strip);
        if (volume != 0) cna_texture3d_destroy(volume);
    }
    {
        /* The effect pass lends the effect it was created around. */
        CNA_EffectHandle basic = 0;
        const CNA_Result made = cna_basic_effect_create(device, &basic);
        CNA_StringView name;
        name.data = "probe";
        name.byte_length = 5U;
        CNA_PostProcessPassHandle pass = 0;
        const CNA_Result made_pass =
            cna_post_process_effect_pass_create(device, basic, name, &pass);
        CNA_EffectHandle a = 0, b = 0;
        const CNA_Result got = cna_post_process_effect_pass_get_effect(pass, &a);
        cna_post_process_effect_pass_get_effect(pass, &b);
        printf("  effect_pass effect                 effect=%s pass=%s get=%s %s %s  "
               "same as the one given: %s\n",
               name_of(made), name_of(made_pass), name_of(got), a == 0 ? "invalid" : "valid",
               a == b ? "same handle" : "fresh each call", a == basic ? "yes" : "no");
        const CNA_Result released =
            a != 0 && a != basic ? cna_effect_destroy(a) : CNA_RESULT_INVALID_HANDLE;
        if (b != 0 && b != basic && b != a) cna_effect_destroy(b);
        const CNA_Result blocked = cna_post_process_pass_destroy(pass);
        const CNA_Result after = basic != 0 ? cna_effect_destroy(basic) : CNA_RESULT_INVALID_HANDLE;
        printf("  effect_pass lifetime               release=%s pass_destroy=%s effect_destroy=%s\n",
               name_of(released), name_of(blocked), name_of(after));
    }
    {
        /* And the clustered forward effect's opaque frame, which it refracts against. */
        CNA_ClusteredForwardEffectHandle effect = 0;
        cna_clustered_forward_effect_create(device, &effect);
        CNA_Handle empty = 0;
        const CNA_Result got_empty = cna_clustered_forward_effect_get_opaque_frame(effect, &empty);
        CNA_Color pixels[16 * 16];
        for (size_t i = 0; i < sizeof pixels / sizeof pixels[0]; i++) {
            pixels[i].r = 0U; pixels[i].g = 0U; pixels[i].b = 0U; pixels[i].a = 255U;
        }
        CNA_Handle frame = 0;
        const CNA_Result made = cna_texture2d_create_from_rgba8(
            device, 16U, 16U, pixels, (uint64_t)(sizeof pixels / sizeof pixels[0]), &frame);
        const CNA_Result set = cna_clustered_forward_effect_set_opaque_frame(effect, frame);
        CNA_Handle a = 0, b = 0;
        const CNA_Result got = cna_clustered_forward_effect_get_opaque_frame(effect, &a);
        cna_clustered_forward_effect_get_opaque_frame(effect, &b);
        printf("  clustered opaque frame             none=%s %s  make=%s set=%s get=%s %s %s  "
               "same as the one given: %s\n",
               name_of(got_empty), empty == 0 ? "invalid" : "valid", name_of(made), name_of(set),
               name_of(got), a == 0 ? "invalid" : "valid",
               a == b ? "same handle" : "fresh each call", a == frame ? "yes" : "no");
        const CNA_Result released =
            a != 0 && a != frame ? cna_texture2d_destroy(a) : CNA_RESULT_INVALID_HANDLE;
        if (b != 0 && b != frame && b != a) cna_texture2d_destroy(b);
        const CNA_Result blocked = cna_clustered_forward_effect_destroy(effect);
        const CNA_Result after = frame != 0 ? cna_texture2d_destroy(frame)
                                            : CNA_RESULT_INVALID_HANDLE;
        printf("  clustered frame lifetime           release=%s effect_destroy=%s frame_destroy=%s\n",
               name_of(released), name_of(blocked), name_of(after));
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
