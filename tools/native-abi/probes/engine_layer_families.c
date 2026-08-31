/* Which engine-layer families are usable in this build, and which need a real graphics device?
   The layer reports itself available, but "available", "does anything without a GPU" and "does
   anything on a HEADLESS renderer" are three different questions, and binding a family that
   answers NOT_SUPPORTED to everything would be shipping an API nobody can call.

   Two passes. The first asks every family with no device at all, which separates the arithmetic
   families from the ones that want hardware. The second creates a real game, borrows its device
   inside a lifecycle callback -- the only scope the C API lends one in -- and asks the rest
   again, which is the answer that decides whether a family is worth projecting. */
#include <CNA/C/engine_layer.h>
#include <CNA/C/graphics_ext.h>
#include <CNA/C/runtime.h>
#include <CNA/C/graphics.h>
#include <CNA/C/geometry.h>
#include <CNA/C/runtime_graphics_manager.h>
#include <stdio.h>
#include <string.h>

static const char* name_of(const CNA_Result result)
{
    switch ((int)result) {
        case 0: return "SUCCESS";
        case 1: return "INVALID_ARGUMENT";
        case 2: return "INVALID_HANDLE";
        case 3: return "INVALID_STATE";
        case 4: return "NOT_FOUND";
        case 6: return "NOT_SUPPORTED";
        case 14: return "BUFFER_TOO_SMALL";
        default: return "OTHER";
    }
}

#define TRY(label, expression)                                                 \
    do {                                                                       \
        const CNA_Result probe_result = (expression);                          \
        printf("  %-42s %-17s (%d)\n", label, name_of(probe_result),           \
               (int)probe_result);                                             \
    } while (0)

/* Creates, reports, and destroys in one line, because "create succeeded" on its own is the
   weakest possible evidence and a family that cannot be released is not usable either. */
#define LIFECYCLE(label, create_expression, handle, destroy_expression)        \
    do {                                                                       \
        const CNA_Result created = (create_expression);                        \
        if (created != CNA_RESULT_SUCCESS) {                                   \
            printf("  %-42s %-17s (%d)\n", label, name_of(created),            \
                   (int)created);                                              \
        } else {                                                               \
            const CNA_Result released = (destroy_expression);                  \
            printf("  %-42s %-17s handle=%s destroy=%s\n", label, "SUCCESS",   \
                   (handle) != 0 ? "yes" : "ZERO", name_of(released));         \
        }                                                                      \
    } while (0)

static void no_device_families(void)
{
    printf("\n== no device at all ==\n");
    {
        CNA_LodGroupEXTHandle lod = 0;
        LIFECYCLE("lod_group_ext", cna_lod_group_ext_create(&lod), lod,
                  cna_lod_group_ext_destroy(lod));
    }
    {
        CNA_FrustumCullerEXTHandle culler = 0;
        LIFECYCLE("frustum_culler_ext", cna_frustum_culler_ext_create(&culler), culler,
                  cna_frustum_culler_ext_destroy(culler));
    }
    {
        CNA_LightProbeHandle probe = 0;
        LIFECYCLE("light_probe_ext", cna_light_probe_ext_create(&probe), probe,
                  cna_light_probe_ext_destroy(probe));
    }
    {
        CNA_BoundingBox bounds;
        memset(&bounds, 0, sizeof bounds);
        bounds.min.x = -1.0F; bounds.min.y = -1.0F; bounds.min.z = -1.0F;
        bounds.max.x =  1.0F; bounds.max.y =  1.0F; bounds.max.z =  1.0F;
        CNA_LightProbeVolumeHandle volume = 0;
        LIFECYCLE("light_probe_volume_ext",
                  cna_light_probe_volume_ext_create(&bounds, 2, 2, 2, &volume), volume,
                  cna_light_probe_volume_ext_destroy(volume));
    }
    {
        CNA_TransparentDrawListHandle list = 0;
        LIFECYCLE("transparent_draw_list", cna_transparent_draw_list_create(&list), list,
                  cna_transparent_draw_list_destroy(list));
    }
    {
        CNA_PbrMaterialExtensionsHandle extensions = 0;
        LIFECYCLE("pbr_material_extensions",
                  cna_pbr_material_extensions_create(&extensions), extensions,
                  cna_pbr_material_extensions_destroy(extensions));
    }
    {
        CNA_DebugDrawHandle debug = 0;
        LIFECYCLE("debug_draw (invalid device)",
                  cna_debug_draw_create(CNA_INVALID_HANDLE, &debug), debug,
                  cna_debug_draw_destroy(debug));
    }
    {
        CNA_ParticleSystemHandle particles = 0;
        LIFECYCLE("particle_system (invalid device)",
                  cna_particle_system_create(CNA_INVALID_HANDLE, &particles), particles,
                  cna_particle_system_destroy(particles));
    }
}

static void device_families(const CNA_Handle device)
{
    printf("\n== inside a game, with its own device ==\n");
    {
        CNA_GpuTimerHandle timer = 0;
        const CNA_Result created = cna_gpu_timer_create(device, &timer);
        if (created != CNA_RESULT_SUCCESS) {
            printf("  %-42s %-17s (%d)\n", "gpu_timer", name_of(created), (int)created);
        } else {
            CNA_Bool supported = CNA_FALSE;
            TRY("gpu_timer_is_supported", cna_gpu_timer_is_supported(timer, &supported));
            printf("  %-42s %s\n", "gpu_timer supported?", supported ? "yes" : "no");
            TRY("gpu_timer_begin", cna_gpu_timer_begin(timer));
            TRY("gpu_timer_end", cna_gpu_timer_end(timer));
            TRY("gpu_timer_destroy", cna_gpu_timer_destroy(timer));
        }
    }
    {
        CNA_StorageBufferHandle buffer = 0;
        LIFECYCLE("storage_buffer", cna_storage_buffer_create(device, 256, &buffer), buffer,
                  cna_storage_buffer_destroy(buffer));
    }
    {
        CNA_ComputeShaderHandle shader = 0;
        CNA_StringView source;
        static const char kSource[] = "#version 310 es\nvoid main() {}\n";
        source.data = kSource;
        source.byte_length = sizeof kSource - 1U;
        LIFECYCLE("compute_shader", cna_compute_shader_create(device, source, &shader), shader,
                  cna_compute_shader_destroy(shader));
    }
    {
        CNA_DebugDrawHandle debug = 0;
        LIFECYCLE("debug_draw", cna_debug_draw_create(device, &debug), debug,
                  cna_debug_draw_destroy(debug));
    }
    {
        CNA_ParticleSystemHandle particles = 0;
        LIFECYCLE("particle_system", cna_particle_system_create(device, &particles), particles,
                  cna_particle_system_destroy(particles));
    }
    {
        CNA_DecalPassHandle pass = 0;
        LIFECYCLE("decal_pass", cna_decal_pass_create(device, &pass), pass,
                  cna_decal_pass_destroy(pass));
    }
    {
        CNA_AtmosphericSkyHandle sky = 0;
        LIFECYCLE("atmospheric_sky", cna_atmospheric_sky_create(device, &sky), sky,
                  cna_atmospheric_sky_destroy(sky));
    }
    {
        CNA_AutoExposureHandle exposure = 0;
        LIFECYCLE("auto_exposure_ext", cna_auto_exposure_ext_create(device, &exposure), exposure,
                  cna_auto_exposure_ext_destroy(exposure));
    }
    {
        CNA_ShadowMapHandle map = 0;
        LIFECYCLE("shadow_map", cna_shadow_map_create(device, CNA_SHADOW_QUALITY_LOW, &map), map,
                  cna_shadow_map_destroy(map));
    }
    {
        CNA_CascadedShadowMapHandle map = 0;
        LIFECYCLE("cascaded_shadow_map",
                  cna_cascaded_shadow_map_create(device, CNA_SHADOW_QUALITY_LOW, 3, &map), map,
                  cna_cascaded_shadow_map_destroy(map));
    }
    {
        CNA_CubeShadowMapHandle map = 0;
        LIFECYCLE("cube_shadow_map",
                  cna_cube_shadow_map_create(device, CNA_SHADOW_QUALITY_LOW, &map), map,
                  cna_cube_shadow_map_destroy(map));
    }
    {
        CNA_SpotShadowMapHandle map = 0;
        LIFECYCLE("spot_shadow_map",
                  cna_spot_shadow_map_create(device, CNA_SHADOW_QUALITY_LOW, &map), map,
                  cna_spot_shadow_map_destroy(map));
    }
    {
        CNA_ClusteredLightBufferHandle buffer = 0;
        LIFECYCLE("clustered_light_buffer",
                  cna_clustered_light_buffer_create(device, &buffer), buffer,
                  cna_clustered_light_buffer_destroy(buffer));
    }
    {
        CNA_GpuInstanceCullerHandle culler = 0;
        const CNA_Result created = cna_gpu_instance_culler_create(device, &culler);
        if (created != CNA_RESULT_SUCCESS) {
            printf("  %-42s %-17s (%d)\n", "gpu_instance_culler", name_of(created),
                   (int)created);
        } else {
            CNA_Bool supported = CNA_FALSE;
            TRY("gpu_instance_culler_is_supported",
                cna_gpu_instance_culler_is_supported(culler, &supported));
            printf("  %-42s %s\n", "gpu_instance_culler supported?", supported ? "yes" : "no");
            char reason[256];
            uint64_t bytes = 0;
            if (cna_gpu_instance_culler_copy_unsupported_reason(
                    culler, reason, sizeof reason, &bytes) == CNA_RESULT_SUCCESS) {
                printf("  %-42s \"%s\"\n", "gpu_instance_culler reason", reason);
            }
            TRY("gpu_instance_culler_destroy", cna_gpu_instance_culler_destroy(culler));
        }
    }
    {
        CNA_RenderPipelineHandle pipeline = 0;
        LIFECYCLE("render_pipeline", cna_render_pipeline_create(device, &pipeline), pipeline,
                  cna_render_pipeline_destroy(pipeline));
    }
    {
        CNA_PostProcessChainHandle chain = 0;
        LIFECYCLE("post_process_chain", cna_post_process_chain_create(device, &chain), chain,
                  cna_post_process_chain_destroy(chain));
    }
    {
        CNA_PostProcessPassHandle pass = 0;
        LIFECYCLE("bloom_pass", cna_bloom_pass_create(device, &pass), pass,
                  cna_post_process_pass_destroy(pass));
    }
    {
        CNA_PostProcessPassHandle pass = 0;
        LIFECYCLE("tonemap_pass", cna_tonemap_pass_create(device, &pass), pass,
                  cna_post_process_pass_destroy(pass));
    }
    {
        CNA_LightProbeBakerHandle baker = 0;
        LIFECYCLE("light_probe_baker", cna_light_probe_baker_create(device, &baker), baker,
                  cna_light_probe_baker_destroy(baker));
    }
    {
        CNA_EnvironmentProcessorHandle processor = 0;
        LIFECYCLE("environment_processor",
                  cna_environment_processor_create(device, &processor), processor,
                  cna_environment_processor_destroy(processor));
    }
    {
        CNA_HdrDisplayOutputHandle output = 0;
        LIFECYCLE("hdr_display_output", cna_hdr_display_output_create(device, &output), output,
                  cna_hdr_display_output_destroy(output));
    }
    {
        CNA_AreaLightBrdfTableHandle table = 0;
        LIFECYCLE("area_light_brdf_table",
                  cna_area_light_brdf_table_create(device, &table), table,
                  cna_area_light_brdf_table_destroy(table));
    }
    {
        CNA_ClusteredForwardEffectHandle effect = 0;
        LIFECYCLE("clustered_forward_effect",
                  cna_clustered_forward_effect_create(device, &effect), effect,
                  cna_clustered_forward_effect_destroy(effect));
    }
    {
        CNA_DepthNormalPrepassHandle prepass = 0;
        LIFECYCLE("depth_normal_prepass",
                  cna_depth_normal_prepass_create(device, 64, 64,
                      CNA_DEPTH_ENCODING_AUTOMATIC, &prepass), prepass,
                  cna_depth_normal_prepass_destroy(prepass));
    }
    {
        CNA_InstancedRendererEXTHandle renderer = 0;
        /* No mesh part to hand it, so this is only expected to report which failure comes
           first: an invalid part is a different answer from a renderer that refuses outright. */
        LIFECYCLE("instanced_renderer_ext (no part)",
                  cna_instanced_renderer_ext_create(device, CNA_INVALID_HANDLE, &renderer),
                  renderer, cna_instanced_renderer_ext_destroy(renderer));
    }
}

/* The four clustered routes whose first parameter is *named* `game` and documented as "the
   owning game", and which the C API in fact resolves with GetBorrowedGraphicsDevice. Asked with
   all three handles, because guessing which one CNA means is what this probe exists to stop. */
static void clustered_families(const CNA_Handle owner, const char* which)
{
    printf("\n== clustered families, asked with the %s handle ==\n", which);
    {
        CNA_ClusteredLightSetHandle set = 0;
        LIFECYCLE("clustered_light_set", cna_clustered_light_set_create(owner, &set), set,
                  cna_clustered_light_set_destroy(set));
    }
    {
        CNA_ClusteredLightGridHandle grid = 0;
        LIFECYCLE("clustered_light_grid",
                  cna_clustered_light_grid_create(owner, 8, 8, 4, &grid), grid,
                  cna_clustered_light_grid_destroy(grid));
    }
    {
        CNA_ClusteredLightAssignmentHandle assignment = 0;
        LIFECYCLE("clustered_light_assignment",
                  cna_clustered_light_assignment_create(owner, &assignment), assignment,
                  cna_clustered_light_assignment_destroy(assignment));
    }
    {
        CNA_ClusteredShadowPolicyHandle policy = 0;
        LIFECYCLE("clustered_shadow_policy",
                  cna_clustered_shadow_policy_create(owner, 4, &policy), policy,
                  cna_clustered_shadow_policy_destroy(policy));
    }
}

static CNA_Result on_update(CNA_Handle game, const CNA_GameTime* game_time, void* context,
                            CNA_CallbackError* out_error)
{
    (void)game_time;
    (void)out_error;
    int* ran = (int*)context;
    if (*ran) {
        return CNA_RESULT_SUCCESS;
    }
    *ran = 1;
    CNA_Handle device = CNA_INVALID_HANDLE;
    const CNA_Result borrowed = cna_game_get_graphics_device(game, &device);
    printf("\ngame_get_graphics_device      %s device=%s\n", name_of(borrowed),
           device != CNA_INVALID_HANDLE ? "yes" : "INVALID");
    if (borrowed == CNA_RESULT_SUCCESS && device != CNA_INVALID_HANDLE) {
        device_families(device);
        clustered_families(device, "graphics device");
    }
    clustered_families(game, "callback-borrowed game");
    return CNA_RESULT_SUCCESS;
}

int main(void)
{
    CNA_Bool available = CNA_FALSE;
    int32_t version = 0;
    printf("graphics_ext_is_available    %d\n",
           (int)(cna_graphics_ext_is_available(&available) == CNA_RESULT_SUCCESS && available));
    TRY("engine_layer_get_version", cna_engine_layer_get_version(&version));
    printf("engine layer version         %d (header %d)\n",
           (int)version, (int)CNA_ENGINE_LAYER_VERSION);

    no_device_families();

    int ran = 0;
    CNA_GameCallbacks callbacks;
    memset(&callbacks, 0, sizeof callbacks);
    callbacks.struct_size = (uint32_t)(sizeof callbacks);
    callbacks.struct_version = 1U;
    callbacks.update = on_update;
    callbacks.context = &ran;

    CNA_GameCreateInfo info;
    memset(&info, 0, sizeof info);
    info.struct_size = (uint32_t)(sizeof info);
    info.struct_version = 1U;
    info.is_fixed_time_step = CNA_TRUE;
    info.target_elapsed_time_ticks = 166667;
    info.window_title.data = "engine layer probe";
    info.window_title.byte_length = 18U;
    info.callbacks = &callbacks;

    CNA_Handle game = CNA_INVALID_HANDLE;
    const CNA_Result created = cna_game_create(&info, &game);
    printf("\ncna_game_create              %s\n", name_of(created));
    if (created != CNA_RESULT_SUCCESS) {
        printf("PROBE INCOMPLETE: no game, so no device-backed answers\n");
        return 1;
    }
    CNA_GraphicsDeviceManagerHandle manager = 0;
    TRY("graphics_device_manager_create", cna_graphics_device_manager_create(game, &manager));
    TRY("game_run_one_frame", cna_game_run_one_frame(game));
    /* And once more with the owned game handle, outside any callback, so the answer is not
       confused with a callback-scope rule. */
    clustered_families(game, "owned game");
    if (manager != 0) {
        TRY("graphics_device_manager_destroy", cna_graphics_device_manager_destroy(manager));
    }
    TRY("game_destroy", cna_game_destroy(game));
    printf("\nPROBE OK (ran=%d)\n", ran);
    return ran ? 0 : 1;
}
