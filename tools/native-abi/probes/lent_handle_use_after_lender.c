/* SPDX-License-Identifier: MS-PL */
/*
 * What is a lent handle worth after its lender has gone?
 *
 * `lent_effect_lifetime.c` sorted the engine layer's borrowed handles into two groups by asking
 * whether the lender's `destroy` is refused while one is outstanding. Seven refuse -- a *blocking*
 * borrow, where CNA itself makes a dangling handle impossible. Five do not, and for those the
 * refusal's absence is not by itself good news: a lender that lets go could be *retaining* (the
 * handle keeps the object alive, which the area-light BRDF table's texture is documented to do)
 * or simply *dangling* (the handle names memory that has been freed).
 *
 * Only using the handle afterwards tells the two apart, and one of the two answers is a crash. So
 * this probe does exactly one case per process, named on the command line, and the runner treats a
 * non-zero exit as part of the measurement rather than as a broken probe:
 *
 *   ./build-probe/lent_handle_use_after_lender spot_caster
 *   ./build-probe/lent_handle_use_after_lender clustered_extensions
 *   ./build-probe/lent_handle_use_after_lender oit_accumulation
 *   ./build-probe/lent_handle_use_after_lender ascii_effect
 *   ./build-probe/lent_handle_use_after_lender brdf_texture
 *
 * `brdf_texture` is the control: its header states the retaining contract outright, and an earlier
 * probe already measured it holding. A run in which the control crashes would mean the probe is
 * wrong rather than CNA.
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "CNA/C/engine_layer.h"
#include "CNA/C/cna.h"

static int ran = 0;
static const char* which = "";

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

/* A read that touches the borrowed object, chosen to be the cheapest one the family has. */
static void use_effect(const char* label, CNA_EffectHandle effect)
{
    uint64_t bytes = 0;
    const CNA_Result read = cna_effect_get_type_name_byte_count(effect, &bytes);
    printf("  %-22s use after lender gone   %-16s (%llu bytes)\n", label, name_of(read),
           (unsigned long long)bytes);
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

    if (strcmp(which, "spot_caster") == 0) {
        CNA_SpotShadowMapHandle map = 0;
        printf("  spot map create         %s\n",
               name_of(cna_spot_shadow_map_create(device, CNA_SHADOW_QUALITY_LOW, &map)));
        CNA_EffectHandle caster = 0;
        /* Sequenced: C leaves argument order unspecified, so reading `caster` in the same printf
           that fills it reports the value it held before the call. */
        const CNA_Result got = cna_spot_shadow_map_get_caster_effect(map, &caster);
        printf("  get caster effect       %s %s\n", name_of(got),
               caster == 0 ? "invalid" : "valid");
        if (caster == 0) {
            printf("  SKIPPED: no effect to lend on this renderer\n");
            (void)cna_spot_shadow_map_destroy(map);
            return CNA_RESULT_SUCCESS;
        }
        printf("  destroy map while lent  %s\n", name_of(cna_spot_shadow_map_destroy(map)));
        use_effect("spot_caster", caster);
        printf("  release afterwards      %s\n", name_of(cna_effect_destroy(caster)));
    } else if (strcmp(which, "clustered_extensions") == 0) {
        CNA_ClusteredForwardEffectHandle effect = 0;
        printf("  clustered create        %s\n",
               name_of(cna_clustered_forward_effect_create(device, &effect)));
        CNA_PbrMaterialExtensionsHandle extensions = 0;
        const CNA_Result got =
            cna_clustered_forward_effect_get_material_extensions(effect, &extensions);
        printf("  get extensions          %s %s\n", name_of(got),
               extensions == 0 ? "invalid" : "valid");
        if (extensions == 0) return CNA_RESULT_SUCCESS;
        printf("  destroy effect while lent %s\n",
               name_of(cna_clustered_forward_effect_destroy(effect)));
        float value = -1.0F;
        const CNA_Result read =
            cna_pbr_material_extensions_get_clearcoat_factor(extensions, &value);
        printf("  clustered_extensions   use after lender gone   %-16s (%.3f)\n", name_of(read),
               (double)value);
        printf("  release afterwards      %s\n",
               name_of(cna_pbr_material_extensions_destroy(extensions)));
    } else if (strcmp(which, "oit_accumulation") == 0) {
        CNA_WeightedBlendedTransparencyHandle oit = 0;
        printf("  oit create              %s\n",
               name_of(cna_weighted_blended_transparency_create(device, 64, 64, &oit)));
        CNA_Handle texture = 0;
        const CNA_Result got =
            cna_weighted_blended_transparency_get_accumulation_texture_ext(oit, &texture);
        printf("  get accumulation        %s %s\n", name_of(got),
               texture == 0 ? "invalid" : "valid");
        if (texture == 0) return CNA_RESULT_SUCCESS;
        printf("  destroy oit while lent  %s\n",
               name_of(cna_weighted_blended_transparency_destroy(oit)));
        CNA_TextureInfo info;
        memset(&info, 0, sizeof info);
        info.struct_size = (uint32_t)(sizeof info);
        info.struct_version = 1U;
        const CNA_Result read = cna_texture_get_info(texture, &info);
        printf("  oit_accumulation       use after lender gone   %-16s (levels %u, format %u)\n", name_of(read),
               (unsigned)info.level_count, (unsigned)info.format);
        printf("  release afterwards      %s\n", name_of(cna_texture2d_destroy(texture)));
    } else if (strcmp(which, "ascii_effect") == 0) {
        CNA_PostProcessPassHandle pass = 0;
        printf("  ascii create            %s\n", name_of(cna_ascii_pass_create(device, &pass)));
        CNA_EffectHandle effect = 0;
        const CNA_Result got = cna_ascii_pass_get_effect(pass, &effect);
        printf("  get effect              %s %s\n", name_of(got),
               effect == 0 ? "invalid" : "valid");
        if (effect == 0) return CNA_RESULT_SUCCESS;
        printf("  release attempt         %s (the header says a caller must NOT release it)\n",
               name_of(cna_effect_destroy(effect)));
        printf("  destroy pass while lent %s\n", name_of(cna_post_process_pass_destroy(pass)));
        use_effect("ascii_effect", effect);
    } else if (strcmp(which, "brdf_texture") == 0) {
        CNA_AreaLightBrdfTableHandle table = 0;
        printf("  brdf table create       %s\n",
               name_of(cna_area_light_brdf_table_create(device, &table)));
        CNA_Handle texture = 0;
        const CNA_Result got = cna_area_light_brdf_table_get_texture(table, &texture);
        printf("  get texture             %s %s\n", name_of(got),
               texture == 0 ? "invalid" : "valid");
        if (texture == 0) return CNA_RESULT_SUCCESS;
        printf("  destroy table while lent %s\n",
               name_of(cna_area_light_brdf_table_destroy(table)));
        CNA_TextureInfo info;
        memset(&info, 0, sizeof info);
        info.struct_size = (uint32_t)(sizeof info);
        info.struct_version = 1U;
        const CNA_Result read = cna_texture_get_info(texture, &info);
        printf("  brdf_texture           use after lender gone   %-16s (levels %u, format %u)\n", name_of(read),
               (unsigned)info.level_count, (unsigned)info.format);
        printf("  release afterwards      %s\n", name_of(cna_texture2d_destroy(texture)));
    } else {
        printf("unknown case '%s'\n", which);
        return CNA_RESULT_SUCCESS;
    }
    return CNA_RESULT_SUCCESS;
}

int main(int argc, char** argv)
{
    if (argc < 2) {
        printf("usage: lent_handle_use_after_lender "
               "<spot_caster|clustered_extensions|oit_accumulation|ascii_effect|brdf_texture>\n");
        return 2;
    }
    which = argv[1];
    const char* requested = getenv("CNA_GRAPHICS_RENDERER");
    printf("case %s on %s\n", which, requested != NULL ? requested : "<build default>");
    fflush(stdout);

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
    info.window_title.data = "lent handle use after lender";
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
    printf("  game destroy            %s\n", name_of(cna_game_destroy(game)));
    printf("PROBE %s\n", ran ? "OK" : "INCOMPLETE");
    return ran ? 0 : 1;
}
