/* SPDX-License-Identifier: MS-PL */
/*
 * Can this renderer bake a light probe, and what happens to the scene callback when it cannot?
 *
 * The header says the baker measures its own capability at construction by capturing one probe and
 * seeing whether the readback worked, and that the headless renderer is exactly the case that binds
 * and then refuses. That makes two things worth measuring before any Java is written: what
 * `is_supported` answers here, and whether a refused bake still runs the callback -- because a
 * trampoline that is entered on a renderer that cannot capture is a different design from one that
 * never is.
 */
#include <stdio.h>
#include <string.h>

#include "CNA/C/engine_layer.h"
#include "CNA/C/cna.h"

static int faces_drawn = 0;

static void count_face(const CNA_Matrix* view, const CNA_Matrix* projection, void* context)
{
    (void)view;
    (void)projection;
    (void)context;
    faces_drawn++;
}

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

    CNA_LightProbeBakerHandle baker = 0;
    CNA_Result result = cna_light_probe_baker_create(device, &baker);
    printf("create                  %d\n", (int)result);

    CNA_Bool supported = 0;
    result = cna_light_probe_baker_is_supported(baker, &supported);
    printf("is supported            %d  %d\n", (int)result, (int)supported);

    int32_t face_size = 0;
    (void)cna_light_probe_baker_get_face_size(baker, &face_size);
    int32_t face_count = 0;
    (void)cna_light_probe_baker_face_count(&face_count);
    float near_plane = 0.0f;
    float far_plane = 0.0f;
    (void)cna_light_probe_baker_get_near_plane(baker, &near_plane);
    (void)cna_light_probe_baker_get_far_plane(baker, &far_plane);
    printf("defaults                face %d, count %d, planes %.4f..%.4f\n",
        (int)face_size, (int)face_count, (double)near_plane, (double)far_plane);

    result = cna_light_probe_baker_set_planes(baker, 0.5f, 200.0f);
    printf("set planes              %d\n", (int)result);
    result = cna_light_probe_baker_set_planes(baker, 10.0f, 1.0f);
    printf("set planes reversed     %d\n", (int)result);
    result = cna_light_probe_baker_set_planes(baker, -1.0f, 100.0f);
    printf("set planes negative     %d\n", (int)result);
    (void)cna_light_probe_baker_get_near_plane(baker, &near_plane);
    (void)cna_light_probe_baker_get_far_plane(baker, &far_plane);
    printf("planes after refusals   %.4f..%.4f\n", (double)near_plane, (double)far_plane);

    /* The six face views from one position: are they six different matrices? */
    CNA_Vector3 position = {1.0f, 2.0f, 3.0f};
    for (int32_t face = 0; face < face_count; face++) {
        CNA_Matrix view;
        memset(&view, 0, sizeof(view));
        result = cna_light_probe_baker_face_view(baker, face, &position, &view);
        printf("face view %d             %d  m11=%.3f m31=%.3f m41=%.3f\n", (int)face,
            (int)result, (double)view.m11, (double)view.m31, (double)view.m41);
    }
    CNA_Matrix out_of_range;
    result = cna_light_probe_baker_face_view(baker, 6, &position, &out_of_range);
    printf("face view 6             %d\n", (int)result);

    faces_drawn = 0;
    CNA_LightProbeHandle probe = 0;
    result = cna_light_probe_baker_bake_probe(baker, &position, count_face, NULL, &probe);
    printf("bake probe              %d  faces drawn %d  probe %s\n", (int)result, faces_drawn,
        probe == 0 ? "invalid" : "valid");
    if (probe != 0) {
        (void)cna_light_probe_ext_destroy(probe);
    }

    result = cna_light_probe_baker_bake_probe(baker, &position, NULL, NULL, &probe);
    printf("bake null callback      %d\n", (int)result);

    CNA_LightProbeVolumeHandle volume = 0;
    CNA_BoundingBox bounds;
    bounds.min.x = -4.0f; bounds.min.y = -4.0f; bounds.min.z = -4.0f;
    bounds.max.x = 4.0f; bounds.max.y = 4.0f; bounds.max.z = 4.0f;
    result = cna_light_probe_volume_ext_create(&bounds, 2, 2, 2, &volume);
    printf("volume create           %d\n", (int)result);
    faces_drawn = 0;
    result = cna_light_probe_baker_bake_light(baker, volume, count_face, NULL);
    printf("bake light              %d  faces drawn %d\n", (int)result, faces_drawn);
    faces_drawn = 0;
    result = cna_light_probe_baker_bake_visibility(baker, volume, count_face, NULL);
    printf("bake visibility         %d  faces drawn %d\n", (int)result, faces_drawn);
    (void)cna_light_probe_volume_ext_destroy(volume);

    result = cna_light_probe_baker_destroy(baker);
    printf("destroy                 %d\n", (int)result);

    CNA_LightProbeBakerHandle sized = 0;
    result = cna_light_probe_baker_create_with_face_size(device, 64, &sized);
    printf("create with face size   %d\n", (int)result);
    int32_t chosen = 0;
    (void)cna_light_probe_baker_get_face_size(sized, &chosen);
    printf("chosen face size        %d\n", (int)chosen);
    (void)cna_light_probe_baker_destroy(sized);

    CNA_LightProbeBakerHandle rejected = 0;
    result = cna_light_probe_baker_create_with_face_size(device, 0, &rejected);
    printf("face size zero          %d\n", (int)result);

    return CNA_RESULT_SUCCESS;
}

int main(void)
{
    CNA_GameCallbacks callbacks;
    memset(&callbacks, 0, sizeof callbacks);
    callbacks.struct_size = (uint32_t)(sizeof callbacks);
    callbacks.struct_version = 1U;
    callbacks.update = on_update;
    callbacks.context = NULL;

    CNA_GameCreateInfo info;
    memset(&info, 0, sizeof info);
    info.struct_size = (uint32_t)(sizeof info);
    info.struct_version = 1U;
    info.is_fixed_time_step = CNA_TRUE;
    info.target_elapsed_time_ticks = 166667;
    info.window_title.data = "light probe bake probe";
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
    (void)cna_game_destroy(game);
    printf("PROBE %s\n", ran ? "OK" : "INCOMPLETE");
    return ran ? 0 : 1;
}
