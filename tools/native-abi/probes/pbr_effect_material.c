/* SPDX-License-Identifier: MS-PL */
/*
 * Does the PBR effect exist on this renderer, and does a material really round-trip through it?
 *
 * The PBR effect is the largest unbound family left, and two things decide whether it is worth
 * projecting: whether it constructs at all where there is no shader compiler, and whether
 * `apply_material` followed by `extract_material` gives back what went in. The second is what makes
 * the whole family testable -- without it every setter would only be checkable against its own
 * getter, and a material that crossed as a structure could not be checked at all.
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

    CNA_EffectHandle effect = 0;
    CNA_Result result = cna_pbr_effect_create(device, &effect);
    printf("pbr effect create       %d\n", (int)result);
    if (result != CNA_RESULT_SUCCESS) {
        return CNA_RESULT_SUCCESS;
    }
    CNA_EffectHandle skinned = 0;
    printf("skinned create          %d\n", (int)cna_skinned_pbr_effect_create(device, &skinned));

    /* A handful of ordinary setters, to see whether they round-trip at all. */
    (void)cna_pbr_effect_set_metallic_factor(effect, 0.25f);
    float metallic = -1.0f;
    (void)cna_pbr_effect_get_metallic_factor(effect, &metallic);
    (void)cna_pbr_effect_set_ior_ext(effect, 1.75f);
    float ior = -1.0f;
    (void)cna_pbr_effect_get_ior_ext(effect, &ior);
    printf("metallic / ior          %.3f %.3f\n", (double)metallic, (double)ior);

    CNA_Bool double_sided = CNA_FALSE;
    (void)cna_pbr_effect_set_double_sided_ext(effect, CNA_TRUE);
    (void)cna_pbr_effect_get_double_sided_ext(effect, &double_sided);
    CNA_AlphaModeEXT mode = 0;
    (void)cna_pbr_effect_set_alpha_mode_ext(effect, 2);
    (void)cna_pbr_effect_get_alpha_mode_ext(effect, &mode);
    printf("double sided / mode     %d %d\n", (int)double_sided, (int)mode);

    /* The per-slot pair, which is where a fixed-array member becomes reachable. */
    (void)cna_pbr_effect_set_texture_coordinate_set_ext(effect, 3, 1);
    int32_t coordinate_set = -1;
    result = cna_pbr_effect_get_texture_coordinate_set_ext(effect, 3, &coordinate_set);
    printf("coordinate set 3        %d  %d\n", (int)result, (int)coordinate_set);
    result = cna_pbr_effect_get_texture_coordinate_set_ext(effect, 7, &coordinate_set);
    printf("coordinate set 7        %d\n", (int)result);

    CNA_TextureTransformEXT transform;
    memset(&transform, 0, sizeof transform);
    transform.struct_size = (uint32_t)(sizeof transform);
    transform.struct_version = 1U;
    transform.offset.x = 0.5f;
    transform.scale.x = 2.0f;
    transform.scale.y = 3.0f;
    transform.rotation = 0.25f;
    result = cna_pbr_effect_set_texture_transform_ext(effect, 2, &transform);
    CNA_TextureTransformEXT read_back;
    memset(&read_back, 0, sizeof read_back);
    /* A versioned out-parameter is still the caller's structure: CNA refuses one that says it is
       zero bytes long, so the stamp goes on before the call rather than being expected back. */
    read_back.struct_size = (uint32_t)(sizeof read_back);
    read_back.struct_version = 1U;
    CNA_Result got = cna_pbr_effect_get_texture_transform_ext(effect, 2, &read_back);
    printf("texture transform 2     %d %d  offset %.2f scale %.2f,%.2f rot %.2f\n", (int)result,
        (int)got, (double)read_back.offset.x, (double)read_back.scale.x,
        (double)read_back.scale.y, (double)read_back.rotation);

    /* And the whole material, out and back in. */
    CNA_PbrMaterialEXT material;
    memset(&material, 0, sizeof material);
    material.struct_size = (uint32_t)(sizeof material);
    material.struct_version = 1U;
    for (int slot = 0; slot < CNA_PBR_TEXTURE_SLOT_COUNT; slot++) {
        material.texture_transforms[slot].struct_size =
            (uint32_t)(sizeof material.texture_transforms[slot]);
        material.texture_transforms[slot].struct_version = 1U;
    }
    result = cna_pbr_effect_extract_material(effect, &material);
    printf("extract                 %d  metallic %.3f ior %.3f sided %d mode %d\n", (int)result,
        (double)material.metallic_factor, (double)material.ior, (int)material.double_sided,
        (int)material.alpha_mode);
    printf("extract slots           coord[3]=%d transform[2] scale %.2f,%.2f\n",
        (int)material.texture_coordinate_sets[3],
        (double)material.texture_transforms[2].scale.x,
        (double)material.texture_transforms[2].scale.y);

    material.roughness_factor = 0.125f;
    material.emissive_factor.y = 0.5f;
    material.texture_coordinate_sets[5] = 1;
    result = cna_pbr_effect_apply_material(effect, &material);
    float roughness = -1.0f;
    (void)cna_pbr_effect_get_roughness_factor(effect, &roughness);
    int32_t applied_set = -1;
    (void)cna_pbr_effect_get_texture_coordinate_set_ext(effect, 5, &applied_set);
    printf("apply                   %d  roughness %.3f coord[5] %d\n", (int)result,
        (double)roughness, (int)applied_set);

    /* Does a texture applied through a material become visible to get_texture? The two paths
       set the same effect property, but only one of them goes through the handle registry. */
    CNA_Handle texture = 0;
    CNA_Texture2DCreateInfo texture_info;
    memset(&texture_info, 0, sizeof texture_info);
    texture_info.struct_size = (uint32_t)(sizeof texture_info);
    texture_info.struct_version = 1U;
    texture_info.width = 4U;
    texture_info.height = 4U;
    texture_info.mip_map = CNA_FALSE;
    texture_info.format = CNA_SURFACE_FORMAT_COLOR;
    result = cna_texture2d_create(device, &texture_info, &texture);
    printf("texture create          %d\n", (int)result);
    CNA_Bool present = CNA_FALSE;
    CNA_Handle read_texture = 0;
    (void)cna_pbr_effect_set_texture(effect, CNA_PBR_TEXTURE_BASE_COLOR, texture);
    (void)cna_pbr_effect_get_texture(effect, CNA_PBR_TEXTURE_BASE_COLOR, &present, &read_texture);
    printf("set_texture then get    present %d  same handle %d\n", (int)present,
        read_texture == texture);
    (void)cna_pbr_effect_set_texture(effect, CNA_PBR_TEXTURE_BASE_COLOR, CNA_INVALID_HANDLE);
    material.albedo_texture = texture;
    (void)cna_pbr_effect_apply_material(effect, &material);
    present = CNA_FALSE;
    read_texture = 0;
    (void)cna_pbr_effect_get_texture(effect, CNA_PBR_TEXTURE_BASE_COLOR, &present, &read_texture);
    printf("apply_material then get present %d  same handle %d\n", (int)present,
        read_texture == texture);
    CNA_PbrMaterialEXT after;
    memset(&after, 0, sizeof after);
    after.struct_size = (uint32_t)(sizeof after);
    after.struct_version = 1U;
    for (int slot = 0; slot < CNA_PBR_TEXTURE_SLOT_COUNT; slot++) {
        after.texture_transforms[slot].struct_size =
            (uint32_t)(sizeof after.texture_transforms[slot]);
        after.texture_transforms[slot].struct_version = 1U;
    }
    (void)cna_pbr_effect_extract_material(effect, &after);
    printf("extract after apply     albedo handle %s\n",
        after.albedo_texture == 0 ? "invalid" : (after.albedo_texture == texture ? "same" : "other"));
    material.albedo_texture = 0;
    (void)cna_pbr_effect_apply_material(effect, &material);
    (void)cna_texture2d_destroy(texture);

    CNA_Bool equal = CNA_FALSE;
    (void)cna_pbr_material_ext_equals(&material, &material, &equal);
    uint64_t hash = 0;
    CNA_Result hashed = cna_pbr_material_ext_get_hash_code(&material, &hash);
    uint64_t bytes = 0;
    CNA_Result text = cna_pbr_material_ext_copy_to_string(&material, NULL, 0, &bytes);
    printf("equals / hash / string  %d  %d  %d needs %llu\n", (int)equal, (int)hashed, (int)text,
        (unsigned long long)bytes);

    CNA_PbrMaterialEXT other = material;
    other.roughness_factor = 0.9f;
    (void)cna_pbr_material_ext_equals(&material, &other, &equal);
    printf("equals, one field apart %d\n", (int)equal);

    printf("apply state             %d\n",
        (int)cna_pbr_material_apply_state(&material, device));

    printf("apply to wrong effect   %d\n",
        (int)cna_skinned_pbr_effect_apply_material(effect, &material));

    (void)cna_effect_destroy(skinned);
    (void)cna_effect_destroy(effect);
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
    info.window_title.data = "pbr effect material probe";
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
