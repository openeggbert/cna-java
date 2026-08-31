/* JAVA-UPSTREAM-006, reproduced with no Java anywhere in the picture.

   `cna_instanced_renderer_ext_draw` documents `CNA_RESULT_INVALID_STATE` for a renderer that
   cannot instance with the per-instance fallback disabled, and the implementation's own comment
   says the exception barrier maps the `std::logic_error` it throws to that result. The barrier
   has no `std::logic_error` arm; the throw reaches `catch (const std::exception&)` and the
   caller is told `CNA_RESULT_INTERNAL` instead -- which a game cannot tell from a defect inside
   CNA, and which is the one refusal here that a game can actually do something about.

   This builds the whole thing in C: a game, its device, a three-vertex buffer, a three-index
   buffer, a mesh part over them, an instanced renderer over that, a BasicEffect, four instances,
   and one draw. It prints the result CNA actually returns. */
#include <CNA/C/engine_layer.h>
#include <CNA/C/runtime.h>
#include <CNA/C/runtime_graphics_manager.h>
#include <CNA/C/graphics.h>
#include <CNA/C/graphics3d.h>
#include <CNA/C/vertex_resources.h>
#include <CNA/C/index_resources.h>
#include <CNA/C/effects.h>
#include <CNA/C/models.h>
#include <CNA/C/math_values.h>
#include <CNA/C/core.h>
#include <stdio.h>
#include <string.h>

static int failures = 0;

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

#define REQUIRE(label, expression)                                                  \
    do {                                                                            \
        const CNA_Result step = (expression);                                       \
        if (step != CNA_RESULT_SUCCESS) {                                           \
            printf("STEP FAILED %-36s %s (%d)\n", label, name_of(step), (int)step); \
            failures += 1;                                                          \
            return CNA_RESULT_SUCCESS;                                              \
        }                                                                           \
    } while (0)

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
    REQUIRE("game_get_graphics_device", cna_game_get_graphics_device(game, &device));

    /* One position element is enough: the draw never reaches the rasteriser here. */
    CNA_VertexElement position;
    memset(&position, 0, sizeof position);
    position.offset = 0;
    position.format = CNA_VERTEX_ELEMENT_FORMAT_VECTOR3;
    position.usage = CNA_VERTEX_ELEMENT_USAGE_POSITION;
    position.usage_index = 0;
    CNA_VertexDeclarationHandle declaration = 0;
    REQUIRE("vertex_declaration_create",
            cna_vertex_declaration_create(&position, 1U, &declaration));

    CNA_VertexBufferCreateInfo vertices;
    memset(&vertices, 0, sizeof vertices);
    vertices.struct_size = (uint32_t)(sizeof vertices);
    vertices.struct_version = 1U;
    vertices.vertex_declaration = declaration;
    vertices.vertex_count = 3;
    vertices.buffer_usage = CNA_BUFFER_USAGE_NONE;
    vertices.dynamic = CNA_FALSE;
    CNA_VertexBufferHandle vertexBuffer = 0;
    REQUIRE("vertex_buffer_create",
            cna_vertex_buffer_create(device, &vertices, &vertexBuffer));

    CNA_IndexBufferCreateInfo indices;
    memset(&indices, 0, sizeof indices);
    indices.struct_size = (uint32_t)(sizeof indices);
    indices.struct_version = 1U;
    indices.index_count = 3;
    indices.index_element_size = CNA_INDEX_ELEMENT_SIZE_SIXTEEN_BITS;
    indices.buffer_usage = CNA_BUFFER_USAGE_NONE;
    indices.dynamic = CNA_FALSE;
    CNA_IndexBufferHandle indexBuffer = 0;
    REQUIRE("index_buffer_create", cna_index_buffer_create(device, &indices, &indexBuffer));

    /* Both buffers need their data uploaded: the headless renderer validates a draw's primitive
       range against what the buffer actually holds, so a declared-but-never-filled index buffer
       refuses the draw before the interesting question is reached. */
    {
        const float positions[9] = {0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F};
        REQUIRE("vertex_buffer_set_data_raw",
                cna_vertex_buffer_set_data_raw(vertexBuffer, positions, sizeof positions, 3U,
                                               12U));
        const uint16_t triangle[3] = {0U, 1U, 2U};
        CNA_IndexBufferTransfer transfer;
        memset(&transfer, 0, sizeof transfer);
        transfer.struct_size = (uint32_t)(sizeof transfer);
        transfer.struct_version = 1U;
        transfer.index_element_size = CNA_INDEX_ELEMENT_SIZE_SIXTEEN_BITS;
        transfer.options = CNA_SET_DATA_NONE;
        transfer.start_index = 0U;
        transfer.element_count = 3U;
        REQUIRE("index_buffer_set_data",
                cna_index_buffer_set_data(indexBuffer, &transfer, triangle, 3U));
    }
    {
        CNA_IndexBufferInfo indexInfo;
        memset(&indexInfo, 0, sizeof indexInfo);
        indexInfo.struct_size = (uint32_t)(sizeof indexInfo);
        indexInfo.struct_version = 1U;
        const CNA_Result asked = cna_index_buffer_get_info(indexBuffer, &indexInfo);
        printf("index buffer count       %d (%s)\n", (int)indexInfo.index_count,
               name_of(asked));
    }
    CNA_ModelMeshPartHandle part = 0;
    REQUIRE("model_mesh_part_create",
            cna_model_mesh_part_create(vertexBuffer, indexBuffer, 3, 1, 0, 0, &part));

    {
        CNA_PrimitiveType topology = 99;
        (void)cna_model_mesh_part_get_primitive_type_ext(part, &topology);
        printf("part primitive type      %u\n", (unsigned)topology);
    }
    CNA_InstancedRendererEXTHandle renderer = 0;
    REQUIRE("instanced_renderer_ext_create",
            cna_instanced_renderer_ext_create(device, part, &renderer));

    CNA_Bool supported = CNA_TRUE;
    REQUIRE("is_instancing_supported",
            cna_instanced_renderer_ext_is_instancing_supported(renderer, &supported));
    CNA_Bool fallback = CNA_TRUE;
    REQUIRE("is_fallback_enabled",
            cna_instanced_renderer_ext_is_fallback_enabled(renderer, &fallback));
    printf("instancing supported     %s\n", supported ? "yes" : "no");
    printf("fallback enabled         %s\n", fallback ? "yes" : "no");

    CNA_Matrix transforms[4];
    memset(transforms, 0, sizeof transforms);
    for (int index = 0; index < 4; ++index) {
        transforms[index].m11 = 1.0F;
        transforms[index].m22 = 1.0F;
        transforms[index].m33 = 1.0F;
        transforms[index].m44 = 1.0F;
        transforms[index].m41 = (float)index;
    }
    REQUIRE("set_instances",
            cna_instanced_renderer_ext_set_instances(renderer, transforms, 4U));

    CNA_EffectHandle effect = 0;
    REQUIRE("basic_effect_create", cna_basic_effect_create(device, &effect));

    if (!supported && !fallback) {
        const CNA_Result refused = cna_instanced_renderer_ext_draw(renderer, effect);
        printf("\ndraw with fallback off   %s (%d)\n", name_of(refused), (int)refused);
        printf("header documents         INVALID_STATE (3)\n");
        if (refused == CNA_RESULT_INVALID_STATE) {
            printf("RESULT: the header and the library agree; JAVA-UPSTREAM-006 is fixed\n");
        } else if (refused == CNA_RESULT_INTERNAL) {
            printf("RESULT: JAVA-UPSTREAM-006 reproduced -- a documented, recoverable refusal\n"
                   "        arrives as INTERNAL, which a game cannot tell from a CNA defect\n");
        } else {
            printf("RESULT: neither -- the refusal changed to %s\n", name_of(refused));
            failures += 1;
        }
    } else {
        printf("\nThis renderer instances or already allows the fallback, so the refusal this\n"
               "probe exists for cannot be reached here.\n");
    }

    /* And the fallback path itself, which is what a game gets after acting on the refusal. */
    REQUIRE("set_fallback_enabled",
            cna_instanced_renderer_ext_set_fallback_enabled(renderer, CNA_TRUE));
    const CNA_Result drawn = cna_instanced_renderer_ext_draw(renderer, effect);
    if (drawn != CNA_RESULT_SUCCESS) {
        char message[512];
        uint64_t written = 0;
        if (cna_error_copy_last_message(message, sizeof message, &written)
            == CNA_RESULT_SUCCESS) {
            printf("fallback draw diagnostic \"%s\"\n", message);
        }
    }
    int32_t calls = 0;
    CNA_Bool instanced = CNA_TRUE;
    (void)cna_instanced_renderer_ext_get_last_draw_call_count(renderer, &calls);
    (void)cna_instanced_renderer_ext_did_last_draw_instance(renderer, &instanced);
    printf("draw with fallback on    %s, %d call(s), instanced=%s\n",
           name_of(drawn), (int)calls, instanced ? "yes" : "no");
    if (drawn != CNA_RESULT_SUCCESS || calls != 4 || instanced) {
        failures += 1;
    }

    (void)cna_effect_destroy(effect);
    (void)cna_instanced_renderer_ext_destroy(renderer);
    (void)cna_model_mesh_part_destroy(part);
    (void)cna_index_buffer_destroy(indexBuffer);
    (void)cna_vertex_buffer_destroy(vertexBuffer);
    (void)cna_vertex_declaration_destroy(declaration);
    return CNA_RESULT_SUCCESS;
}

int main(void)
{
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
    info.window_title.data = "instanced draw refusal";
    info.window_title.byte_length = 22U;
    info.callbacks = &callbacks;

    CNA_Handle game = CNA_INVALID_HANDLE;
    if (cna_game_create(&info, &game) != CNA_RESULT_SUCCESS) {
        printf("PROBE INCOMPLETE: no game\n");
        return 1;
    }
    CNA_GraphicsDeviceManagerHandle manager = 0;
    (void)cna_graphics_device_manager_create(game, &manager);
    (void)cna_game_run_one_frame(game);
    if (manager != 0) {
        (void)cna_graphics_device_manager_destroy(manager);
    }
    (void)cna_game_destroy(game);
    if (!ran || failures != 0) {
        printf("\nPROBE FAILED (ran=%d failures=%d)\n", ran, failures);
        return 1;
    }
    printf("\nPROBE OK\n");
    return 0;
}
