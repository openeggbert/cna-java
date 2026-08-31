/* SPDX-License-Identifier: MS-PL */
/*
 * What order does CNA's transparent draw list actually run its callbacks in, and what does its
 * sort key measure?
 *
 * Asked in C because the answer decides the whole shape of the Java projection: a list whose
 * callbacks are C function pointers needs a JNI trampoline, and writing one against a guess about
 * the ordering would be building the hard part on top of an assumption. The header says
 * "farthest from the camera first" and "the distance is measured to the nearest point of the box",
 * and both of those are claims a probe can check rather than repeat.
 */
#include <stdio.h>
#include <string.h>

#include "CNA/C/engine_layer.h"
#include "CNA/C/cna.h"

static int order[8];
static int drawn = 0;

static CNA_Result record_draw(void* context)
{
    if (drawn < (int)(sizeof(order) / sizeof(order[0]))) {
        order[drawn++] = (int)(intptr_t)context;
    }
    return CNA_RESULT_SUCCESS;
}

static CNA_Result fail_draw(void* context)
{
    if (drawn < (int)(sizeof(order) / sizeof(order[0]))) {
        order[drawn++] = (int)(intptr_t)context;
    }
    return CNA_RESULT_INVALID_STATE;
}

static CNA_BoundingBox box_at(float x)
{
    CNA_BoundingBox box;
    memset(&box, 0, sizeof(box));
    box.min.x = x - 0.5f;
    box.min.y = -0.5f;
    box.min.z = -0.5f;
    box.max.x = x + 0.5f;
    box.max.y = 0.5f;
    box.max.z = 0.5f;
    return box;
}

int main(void)
{
    CNA_TransparentDrawListHandle list = 0;
    CNA_Result result = cna_transparent_draw_list_create(&list);
    printf("create                 %d\n", (int)result);
    if (result != CNA_RESULT_SUCCESS) {
        return 1;
    }

    /* A camera at the origin looking down -X, so the boxes below are 2, 10 and 30 away. */
    CNA_Vector3 eye = {0.0f, 0.0f, 0.0f};
    CNA_Vector3 target = {-1.0f, 0.0f, 0.0f};
    CNA_Vector3 up = {0.0f, 1.0f, 0.0f};
    CNA_Matrix view;
    memset(&view, 0, sizeof(view));
    result = cna_matrix_create_look_at(eye, target, up, &view);
    printf("look at                %d\n", (int)result);

    CNA_Vector3 derived;
    memset(&derived, 0, sizeof(derived));
    result = cna_transparent_draw_list_camera_position_of(&view, &derived);
    printf("camera position of     %d  (%.2f %.2f %.2f)\n", (int)result,
        (double)derived.x, (double)derived.y, (double)derived.z);

    /* Submitted nearest first, on purpose: a list that ran them in submission order would look
       identical to a correctly sorted one if they were submitted farthest first. */
    CNA_BoundingBox near_box = box_at(-2.0f);
    CNA_BoundingBox middle_box = box_at(-10.0f);
    CNA_BoundingBox far_box = box_at(-30.0f);
    result = cna_transparent_draw_list_submit(list, &near_box, record_draw, (void*)(intptr_t)0);
    printf("submit near            %d\n", (int)result);
    result = cna_transparent_draw_list_submit(list, &middle_box, record_draw, (void*)(intptr_t)1);
    printf("submit middle          %d\n", (int)result);
    result = cna_transparent_draw_list_submit(list, &far_box, record_draw, (void*)(intptr_t)2);
    printf("submit far             %d\n", (int)result);

    uint64_t count = 0;
    result = cna_transparent_draw_list_get_count(list, &count);
    printf("count                  %d  %llu\n", (int)result, (unsigned long long)count);

    result = cna_transparent_draw_list_submit(list, &near_box, NULL, NULL);
    printf("submit null callback   %d\n", (int)result);
    result = cna_transparent_draw_list_submit(list, NULL, record_draw, NULL);
    printf("submit null bounds     %d\n", (int)result);

    int32_t sorted[8];
    uint64_t needed = 0;
    result = cna_transparent_draw_list_copy_sorted_order_ext(list, &view, NULL, 0, &needed);
    printf("order probe            %d  needs %llu\n", (int)result, (unsigned long long)needed);
    result = cna_transparent_draw_list_copy_sorted_order_ext(list, &view, sorted, 8, &needed);
    printf("order                  %d  [", (int)result);
    for (uint64_t index = 0; index < needed; index++) {
        printf("%s%d", index ? " " : "", (int)sorted[index]);
    }
    printf("]\n");

    drawn = 0;
    result = cna_transparent_draw_list_draw_sorted(list, &view);
    printf("draw sorted            %d  [", (int)result);
    for (int index = 0; index < drawn; index++) {
        printf("%s%d", index ? " " : "", order[index]);
    }
    printf("]\n");

    /* Is the key the distance to the nearest point of the box, or to its centre? A camera inside
       the box separates the two answers: nearest point says zero, centre says half the width. */
    float key = -1.0f;
    CNA_Vector3 inside = {-10.0f, 0.0f, 0.0f};
    result = cna_transparent_draw_list_sort_key(&middle_box, &inside, &key);
    printf("key, camera inside     %d  %.4f\n", (int)result, (double)key);
    CNA_Vector3 origin = {0.0f, 0.0f, 0.0f};
    result = cna_transparent_draw_list_sort_key(&middle_box, &origin, &key);
    printf("key, camera at origin  %d  %.4f\n", (int)result, (double)key);

    /* A failing callback stops the draw, says the header. Which entries ran before it? */
    CNA_TransparentDrawListHandle failing = 0;
    (void)cna_transparent_draw_list_create(&failing);
    (void)cna_transparent_draw_list_submit(failing, &far_box, record_draw, (void*)(intptr_t)7);
    (void)cna_transparent_draw_list_submit(failing, &middle_box, fail_draw, (void*)(intptr_t)8);
    (void)cna_transparent_draw_list_submit(failing, &near_box, record_draw, (void*)(intptr_t)9);
    drawn = 0;
    result = cna_transparent_draw_list_draw_sorted(failing, &view);
    printf("failing draw           %d  [", (int)result);
    for (int index = 0; index < drawn; index++) {
        printf("%s%d", index ? " " : "", order[index]);
    }
    printf("]\n");
    (void)cna_transparent_draw_list_destroy(failing);

    result = cna_transparent_draw_list_clear(list);
    (void)cna_transparent_draw_list_get_count(list, &count);
    printf("clear                  %d  count %llu\n", (int)result, (unsigned long long)count);

    result = cna_transparent_draw_list_destroy(list);
    printf("destroy                %d\n", (int)result);
    result = cna_transparent_draw_list_destroy(list);
    printf("destroy again          %d\n", (int)result);
    return 0;
}
