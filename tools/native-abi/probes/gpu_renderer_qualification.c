/* What can this renderer actually do, and is any of it enough to make the engine layer's three
   refused families real?

   Every previous engine-layer measurement in this repository was taken on the HEADLESS renderer,
   which compiles no shader and reads back no pixel. Three families were recorded as blocked on
   that renderer rather than on CNA: compute and storage buffers (JAVA-EXT-012), automatic
   exposure (JAVA-EXT-011), and the borrowed effect handles a renderer with no compiled shaders
   has none of (JAVA-EXT-010).

   This probe asks the questions that decide whether those measurements still hold, in the order
   they depend on each other:

     1. which renderer is really active, by its own name rather than by what was asked for;
     2. what it says about itself through cna_graphics_device_supports_capability;
     3. whether a compute shader compiles, and in which GLSL dialect;
     4. whether a dispatch over a storage buffer produces the arithmetic it was asked for --
        the only evidence that separates "the API accepted the call" from "the GPU ran it";
     5. whether a frame can be rendered into a target and read back as pixels;
     6. whether automatic exposure constructs, and what it measures.

   Structured lines are prefixed `GPUQ ` and are also written to the file named by argv[1] when
   one is given, because a real renderer prints banners on stdout and a machine-readable probe
   must not have to be told apart from them. */
#include <CNA/C/engine_layer.h>
#include <CNA/C/core.h>
#include <CNA/C/graphics.h>
#include <CNA/C/graphics_device.h>
#include <CNA/C/graphics_ext.h>
#include <CNA/C/render_target.h>
#include <CNA/C/runtime.h>
#include <CNA/C/runtime_graphics_manager.h>
#include <CNA/C/texture.h>
#include <math.h>
#include <stdarg.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

static FILE* g_report = NULL;
static int g_failures = 0;

static const char* name_of(const CNA_Result result)
{
    switch ((int)result) {
        case 0: return "SUCCESS";
        case 1: return "INVALID_ARGUMENT";
        case 2: return "INVALID_HANDLE";
        case 3: return "INVALID_STATE";
        case 4: return "NOT_FOUND";
        case 6: return "NOT_SUPPORTED";
        case 12: return "INTERNAL";
        case 14: return "BUFFER_TOO_SMALL";
        default: return "OTHER";
    }
}

static void say(const char* format, ...)
{
    char line[1024];
    va_list arguments;
    va_start(arguments, format);
    vsnprintf(line, sizeof line, format, arguments);
    va_end(arguments);
    printf("GPUQ %s\n", line);
    fflush(stdout);
    if (g_report != NULL) {
        fprintf(g_report, "%s\n", line);
        fflush(g_report);
    }
}

/* GLSL ES 3.10, which is what every compute shader inside CNA's own engine layer is written in
   (AutoExposureEXT.cpp, GpuInstanceCuller.cpp and ClusteredLightCompute.cpp all open with
   "#version 310 es"). A renderer that cannot compile this cannot run the engine layer's own
   compute either, whatever else it supports. */
static const char* const kDoublerEs310 =
    "#version 310 es\n"
    "layout(local_size_x = 4) in;\n"
    "layout(std430, binding = 0) readonly buffer Source { int source_values[]; };\n"
    "layout(std430, binding = 1) writeonly buffer Result { int result_values[]; };\n"
    "uniform int addend;\n"
    "void main() {\n"
    "    uint index = gl_GlobalInvocationID.x;\n"
    "    result_values[index] = source_values[index] * 2 + addend;\n"
    "}\n";

/* The same program in desktop GLSL, asked separately so the answer says which dialect this
   renderer takes rather than only whether one of them worked. */
static const char* const kDoublerCore430 =
    "#version 430 core\n"
    "layout(local_size_x = 4) in;\n"
    "layout(std430, binding = 0) readonly buffer Source { int source_values[]; };\n"
    "layout(std430, binding = 1) writeonly buffer Result { int result_values[]; };\n"
    "uniform int addend;\n"
    "void main() {\n"
    "    uint index = gl_GlobalInvocationID.x;\n"
    "    result_values[index] = source_values[index] * 2 + addend;\n"
    "}\n";

static CNA_StringView view_of(const char* text)
{
    CNA_StringView view;
    view.data = text;
    view.byte_length = (uint64_t)strlen(text);
    return view;
}

static void report_renderer(CNA_Handle device)
{
    char buffer[256];
    uint64_t needed = 0;
    const CNA_Result sized = cna_graphics_device_get_renderer_name_size(device, &needed);
    if (sized != CNA_RESULT_SUCCESS || needed >= sizeof buffer) {
        say("renderer.name                 <%s, %llu bytes>", name_of(sized),
            (unsigned long long)needed);
        return;
    }
    uint64_t written = 0;
    const CNA_Result copied =
        cna_graphics_device_copy_renderer_name(device, buffer, sizeof buffer, &written);
    if (copied != CNA_RESULT_SUCCESS) {
        say("renderer.name                 <%s>", name_of(copied));
        return;
    }
    buffer[written] = '\0';
    say("renderer.name                 %s", buffer);
}

static CNA_Bool capability(CNA_Handle device, const CNA_GraphicsCapability which,
                           const char* label)
{
    CNA_Bool supported = CNA_FALSE;
    const CNA_Result asked =
        cna_graphics_device_supports_capability(device, which, &supported);
    say("capability.%-18s %s%s", label, supported ? "yes" : "no",
        asked == CNA_RESULT_SUCCESS ? "" : " (query failed)");
    return supported;
}

/* The whole point of the probe. A storage buffer holds four known integers; a compute shader
   doubles each and adds a uniform; the buffer is read back and compared against arithmetic done
   in C. Nothing short of the GPU having run the program produces this answer. */
static void compute_semantics(CNA_Handle device, CNA_ComputeShaderHandle shader)
{
    enum { kCount = 4 };
    const int32_t input[kCount] = { 3, 5, 11, 19 };
    int32_t output[kCount] = { -1, -1, -1, -1 };
    const int32_t addend = 7;

    CNA_StorageBufferHandle source = 0;
    CNA_StorageBufferHandle result = 0;
    const CNA_Result made_source =
        cna_storage_buffer_create_typed(device, kCount, sizeof(int32_t), &source);
    const CNA_Result made_result =
        cna_storage_buffer_create_typed(device, kCount, sizeof(int32_t), &result);
    say("compute.buffers               source=%s result=%s", name_of(made_source),
        name_of(made_result));
    if (made_source != CNA_RESULT_SUCCESS || made_result != CNA_RESULT_SUCCESS) {
        g_failures++;
        if (made_source == CNA_RESULT_SUCCESS) cna_storage_buffer_destroy(source);
        if (made_result == CNA_RESULT_SUCCESS) cna_storage_buffer_destroy(result);
        return;
    }

    const CNA_Result uploaded =
        cna_storage_buffer_set_elements(source, input, kCount, sizeof(int32_t));
    const CNA_Result bound_source = cna_compute_shader_bind_storage_buffer(shader, 0, source);
    const CNA_Result bound_result = cna_compute_shader_bind_storage_buffer(shader, 1, result);
    const CNA_Result uniform =
        cna_compute_shader_set_uniform_int(shader, view_of("addend"), addend);
    const CNA_Result dispatched = cna_compute_shader_dispatch(shader, 1, 1, 1);
    const CNA_Result ordered =
        cna_compute_shader_barrier(shader, CNA_GRAPHICS_MEMORY_BARRIER_SHADER_STORAGE |
                                               CNA_GRAPHICS_MEMORY_BARRIER_BUFFER_UPDATE);
    const CNA_Result read =
        cna_storage_buffer_get_elements(result, output, kCount, sizeof(int32_t));

    say("compute.upload                %s", name_of(uploaded));
    say("compute.bind                  source=%s result=%s uniform=%s", name_of(bound_source),
        name_of(bound_result), name_of(uniform));
    say("compute.dispatch              %s barrier=%s", name_of(dispatched), name_of(ordered));
    say("compute.readback              %s", name_of(read));

    int correct = 1;
    for (int index = 0; index < kCount; ++index) {
        if (output[index] != input[index] * 2 + addend) correct = 0;
    }
    say("compute.result                in [%d %d %d %d] out [%d %d %d %d] expected [%d %d %d %d]",
        input[0], input[1], input[2], input[3], output[0], output[1], output[2], output[3],
        input[0] * 2 + addend, input[1] * 2 + addend, input[2] * 2 + addend,
        input[3] * 2 + addend);
    say("compute.semantic              %s", correct && read == CNA_RESULT_SUCCESS ? "PASS" : "FAIL");
    if (!correct || read != CNA_RESULT_SUCCESS) g_failures++;

    /* Ordinary byte-shaped access to the same buffer, which is the other constructor's contract. */
    uint64_t element_count = 0;
    uint64_t element_size = 0;
    uint64_t byte_size = 0;
    cna_storage_buffer_get_element_count(result, &element_count);
    cna_storage_buffer_get_element_byte_size(result, &element_size);
    cna_storage_buffer_get_byte_size(result, &byte_size);
    say("storage.shape                 elements=%llu element_bytes=%llu bytes=%llu",
        (unsigned long long)element_count, (unsigned long long)element_size,
        (unsigned long long)byte_size);

    int32_t raw[kCount] = { 0, 0, 0, 0 };
    const CNA_Result raw_read = cna_storage_buffer_get_bytes(result, raw, sizeof raw);
    say("storage.get_bytes             %s [%d %d %d %d]", name_of(raw_read), raw[0], raw[1],
        raw[2], raw[3]);

    say("storage.destroy               source=%s result=%s",
        name_of(cna_storage_buffer_destroy(source)),
        name_of(cna_storage_buffer_destroy(result)));
}

static void compute_family(CNA_Handle device)
{
    CNA_ComputeShaderHandle es310 = 0;
    const CNA_Result made_es310 =
        cna_compute_shader_create(device, view_of(kDoublerEs310), &es310);
    CNA_Bool valid_es310 = CNA_FALSE;
    if (made_es310 == CNA_RESULT_SUCCESS) {
        cna_compute_shader_is_valid(es310, &valid_es310);
    }
    say("compute.create_es310          %s valid=%s", name_of(made_es310),
        valid_es310 ? "yes" : "no");
    if (made_es310 == CNA_RESULT_SUCCESS && !valid_es310) {
        char error[2048];
        uint64_t bytes = 0;
        const CNA_Result copied =
            cna_compute_shader_copy_compile_error(es310, error, sizeof error - 1, &bytes);
        if (copied == CNA_RESULT_SUCCESS && bytes < sizeof error) {
            error[bytes] = '\0';
            for (uint64_t index = 0; index < bytes; ++index) {
                if (error[index] == '\n') error[index] = ' ';
            }
            say("compute.es310_error           %s", error);
        }
    }

    CNA_ComputeShaderHandle core430 = 0;
    const CNA_Result made_core430 =
        cna_compute_shader_create(device, view_of(kDoublerCore430), &core430);
    CNA_Bool valid_core430 = CNA_FALSE;
    if (made_core430 == CNA_RESULT_SUCCESS) {
        cna_compute_shader_is_valid(core430, &valid_core430);
    }
    say("compute.create_core430        %s valid=%s", name_of(made_core430),
        valid_core430 ? "yes" : "no");

    /* Source that cannot compile in any dialect, because the header promises creation succeeds
       and records the failure rather than throwing -- a promise no renderer without a compiler
       could ever be measured against. */
    CNA_ComputeShaderHandle broken = 0;
    const CNA_Result made_broken =
        cna_compute_shader_create(device, view_of("#version 310 es\nthis is not glsl\n"), &broken);
    CNA_Bool valid_broken = CNA_TRUE;
    uint64_t error_bytes = 0;
    if (made_broken == CNA_RESULT_SUCCESS) {
        cna_compute_shader_is_valid(broken, &valid_broken);
        cna_compute_shader_copy_compile_error(broken, NULL, 0, &error_bytes);
    }
    say("compute.create_broken         %s valid=%s error_bytes=%llu", name_of(made_broken),
        valid_broken ? "yes" : "no", (unsigned long long)error_bytes);

    CNA_ComputeShaderHandle runner = 0;
    if (valid_es310) {
        runner = es310;
    } else if (valid_core430) {
        runner = core430;
    }
    if (runner != 0) {
        CNA_Bool image_binding = CNA_FALSE;
        cna_compute_shader_is_image_binding_supported(runner, &image_binding);
        say("compute.image_binding         %s", image_binding ? "yes" : "no");
        compute_semantics(device, runner);
    } else {
        say("compute.semantic              SKIPPED (no dialect compiled)");
        g_failures++;
    }

    if (made_es310 == CNA_RESULT_SUCCESS) {
        say("compute.destroy_es310         %s", name_of(cna_compute_shader_destroy(es310)));
    }
    if (made_core430 == CNA_RESULT_SUCCESS) {
        say("compute.destroy_core430       %s", name_of(cna_compute_shader_destroy(core430)));
    }
    if (made_broken == CNA_RESULT_SUCCESS) {
        say("compute.destroy_broken        %s", name_of(cna_compute_shader_destroy(broken)));
    }
}

/* A texture whose every texel is one known colour, uploaded and read straight back. This is the
   floor under every pixel claim: a renderer that cannot return what was just put in cannot be
   asked what a shader wrote. */
static CNA_Handle uniform_texture(CNA_Handle device, const int size, const uint8_t red,
                                  const uint8_t green, const uint8_t blue, const char* label)
{
    const size_t count = (size_t)size * (size_t)size;
    CNA_Color* pixels = (CNA_Color*)malloc(count * sizeof(CNA_Color));
    if (pixels == NULL) return CNA_INVALID_HANDLE;
    for (size_t index = 0; index < count; ++index) {
        pixels[index].r = red;
        pixels[index].g = green;
        pixels[index].b = blue;
        pixels[index].a = 255U;
    }
    CNA_Handle texture = CNA_INVALID_HANDLE;
    const CNA_Result made = cna_texture2d_create_from_rgba8(
        device, (uint32_t)size, (uint32_t)size, pixels, (uint64_t)count, &texture);
    say("texture.%-21s %s", label, name_of(made));
    free(pixels);
    return made == CNA_RESULT_SUCCESS ? texture : CNA_INVALID_HANDLE;
}

/* The versioned window every texture transfer takes: the whole level-zero image. */
static CNA_Texture2DTransfer whole_image(const uint64_t element_count)
{
    CNA_Texture2DTransfer transfer;
    memset(&transfer, 0, sizeof transfer);
    transfer.struct_size = (uint32_t)(sizeof transfer);
    transfer.struct_version = 1U;
    transfer.level = 0;
    transfer.has_rectangle = CNA_FALSE;
    transfer.start_index = 0U;
    transfer.element_count = element_count;
    return transfer;
}

static void readback_family(CNA_Handle device)
{
    const int size = 8;
    const uint64_t count = (uint64_t)size * (uint64_t)size;
    CNA_Handle texture = uniform_texture(device, size, 40U, 90U, 200U, "create_rgba8");
    if (texture == CNA_INVALID_HANDLE) {
        g_failures++;
        return;
    }
    CNA_Color* read = (CNA_Color*)calloc((size_t)count, sizeof(CNA_Color));
    uint64_t required = 0;
    const CNA_Texture2DTransfer transfer = whole_image(count);
    const CNA_Result got = cna_texture2d_get_data(texture, CNA_TEXTURE_DATA_COLOR, &transfer, read,
                                                  count, &required);
    say("texture.get_data              %s first=[%u %u %u %u]", name_of(got), read[0].r, read[0].g,
        read[0].b, read[0].a);
    const int matched = got == CNA_RESULT_SUCCESS && read[0].r == 40U && read[0].g == 90U &&
                        read[0].b == 200U && read[0].a == 255U;
    say("texture.readback_semantic     %s", matched ? "PASS" : "FAIL");
    if (!matched) g_failures++;
    free(read);

    CNA_RenderTarget2DCreateInfo target_info;
    memset(&target_info, 0, sizeof target_info);
    target_info.struct_size = (uint32_t)(sizeof target_info);
    target_info.struct_version = 1U;
    target_info.width = (uint32_t)size;
    target_info.height = (uint32_t)size;
    target_info.mip_map = CNA_FALSE;
    target_info.format = CNA_SURFACE_FORMAT_COLOR;
    target_info.depth_format = CNA_DEPTH_FORMAT_NONE;
    target_info.multi_sample_count = 0;
    target_info.usage = CNA_RENDER_TARGET_USAGE_DISCARD_CONTENTS;

    CNA_Handle target = CNA_INVALID_HANDLE;
    const CNA_Result made_target = cna_render_target2d_create(device, &target_info, &target);
    say("render_target.create          %s", name_of(made_target));
    if (made_target == CNA_RESULT_SUCCESS) {
        CNA_RenderTargetInfo info;
        memset(&info, 0, sizeof info);
        info.struct_size = (uint32_t)(sizeof info);
        info.struct_version = 1U;
        if (cna_render_target_get_info(target, &info) == CNA_RESULT_SUCCESS) {
            say("render_target.storage         renderer_available=%s",
                info.renderer_available ? "yes" : "no");
        }
        const CNA_Result bound = cna_graphics_device_set_render_target2d(device, target);
        /* 12/34/56 out of 255 as floats, which is what the float-channel clear takes. */
        const CNA_Result wiped = cna_graphics_device_clear_rgba(
            device, 12.0F / 255.0F, 34.0F / 255.0F, 56.0F / 255.0F, 1.0F);
        const CNA_Result unbound =
            cna_graphics_device_set_render_target2d(device, CNA_INVALID_HANDLE);
        CNA_Color* pixels = (CNA_Color*)calloc((size_t)count, sizeof(CNA_Color));
        uint64_t grabbed_count = 0;
        const CNA_Texture2DTransfer target_transfer = whole_image(count);
        const CNA_Result grabbed = cna_texture2d_get_data(
            target, CNA_TEXTURE_DATA_COLOR, &target_transfer, pixels, count, &grabbed_count);
        say("render_target.clear           bind=%s clear=%s unbind=%s", name_of(bound),
            name_of(wiped), name_of(unbound));
        say("render_target.read            %s first=[%u %u %u %u]", name_of(grabbed), pixels[0].r,
            pixels[0].g, pixels[0].b, pixels[0].a);
        /* One step of tolerance each way: a clear goes through a float channel and back. */
        const int cleared_ok = grabbed == CNA_RESULT_SUCCESS && abs((int)pixels[0].r - 12) <= 1 &&
                               abs((int)pixels[0].g - 34) <= 1 && abs((int)pixels[0].b - 56) <= 1;
        say("render_target.pixel_semantic  %s", cleared_ok ? "PASS" : "FAIL");
        if (!cleared_ok) g_failures++;
        free(pixels);
        say("render_target.destroy         %s", name_of(cna_render_target_destroy(target)));
    } else {
        g_failures++;
    }
    say("texture.destroy               %s", name_of(cna_texture2d_destroy(texture)));
}

static void auto_exposure_family(CNA_Handle device)
{
    CNA_AutoExposureHandle meter = 0;
    const CNA_Result made = cna_auto_exposure_ext_create(device, &meter);
    say("auto_exposure.create          %s", name_of(made));
    if (made != CNA_RESULT_SUCCESS) {
        g_failures++;
        return;
    }
    float exposure = 0.0F;
    float key = 0.0F;
    float brightening = 0.0F;
    float darkening = 0.0F;
    cna_auto_exposure_ext_get_exposure(meter, &exposure);
    cna_auto_exposure_ext_get_key_value(meter, &key);
    cna_auto_exposure_ext_get_brightening_speed(meter, &brightening);
    cna_auto_exposure_ext_get_darkening_speed(meter, &darkening);
    say("auto_exposure.defaults        exposure=%.4f key=%.4f brighten=%.4f darken=%.4f",
        (double)exposure, (double)key, (double)brightening, (double)darkening);

    CNA_Handle dark = uniform_texture(device, 16, 8U, 8U, 8U, "auto_exposure_dark");
    CNA_Handle bright = uniform_texture(device, 16, 240U, 240U, 240U, "auto_exposure_bright");
    float dark_luminance = -1.0F;
    float bright_luminance = -1.0F;
    const CNA_Result measured_dark =
        cna_auto_exposure_ext_measure_average_luminance(meter, dark, &dark_luminance);
    const CNA_Result measured_bright =
        cna_auto_exposure_ext_measure_average_luminance(meter, bright, &bright_luminance);
    say("auto_exposure.measure         dark=%s %.6f bright=%s %.6f", name_of(measured_dark),
        (double)dark_luminance, name_of(measured_bright), (double)bright_luminance);
    const int ordered = measured_dark == CNA_RESULT_SUCCESS &&
                        measured_bright == CNA_RESULT_SUCCESS &&
                        bright_luminance > dark_luminance;
    say("auto_exposure.luminance_order %s", ordered ? "PASS" : "FAIL");
    if (!ordered) g_failures++;

    float toward_bright = 0.0F;
    float toward_dark = 0.0F;
    cna_auto_exposure_ext_set_exposure(meter, 1.0F);
    const CNA_Result adapted_bright =
        cna_auto_exposure_ext_update(meter, bright, 1000.0F, &toward_bright);
    cna_auto_exposure_ext_set_exposure(meter, 1.0F);
    const CNA_Result adapted_dark =
        cna_auto_exposure_ext_update(meter, dark, 1000.0F, &toward_dark);
    say("auto_exposure.adapt           bright=%s %.6f dark=%s %.6f", name_of(adapted_bright),
        (double)toward_bright, name_of(adapted_dark), (double)toward_dark);
    const int direction = adapted_bright == CNA_RESULT_SUCCESS &&
                          adapted_dark == CNA_RESULT_SUCCESS && toward_bright < toward_dark;
    say("auto_exposure.adapt_direction %s", direction ? "PASS" : "FAIL");
    if (!direction) g_failures++;

    if (dark != CNA_INVALID_HANDLE) cna_texture2d_destroy(dark);
    if (bright != CNA_INVALID_HANDLE) cna_texture2d_destroy(bright);
    say("auto_exposure.destroy         %s", name_of(cna_auto_exposure_ext_destroy(meter)));
}

static void lent_effect_handles(CNA_Handle device)
{
    CNA_ShadowMapHandle map = 0;
    const CNA_Result made = cna_shadow_map_create(device, CNA_SHADOW_QUALITY_MEDIUM, &map);
    if (made != CNA_RESULT_SUCCESS) {
        say("lent.shadow_map               %s", name_of(made));
        return;
    }
    CNA_Handle caster = CNA_INVALID_HANDLE;
    CNA_Handle skinned = CNA_INVALID_HANDLE;
    const CNA_Result got_caster = cna_shadow_map_get_caster_effect(map, &caster);
    const CNA_Result got_skinned = cna_shadow_map_get_skinned_caster_effect(map, &skinned);
    say("lent.shadow_caster_effect     %s %s", name_of(got_caster),
        caster != CNA_INVALID_HANDLE ? "valid" : "invalid");
    say("lent.shadow_skinned_effect    %s %s", name_of(got_skinned),
        skinned != CNA_INVALID_HANDLE ? "valid" : "invalid");
    CNA_Handle again = CNA_INVALID_HANDLE;
    cna_shadow_map_get_caster_effect(map, &again);
    say("lent.shadow_caster_stable     %s", again == caster ? "same handle" : "fresh each call");
    say("lent.shadow_map_destroy       %s", name_of(cna_shadow_map_destroy(map)));

    CNA_DepthNormalPrepassHandle prepass = 0;
    if (cna_depth_normal_prepass_create(device, 64, 64, CNA_DEPTH_ENCODING_AUTOMATIC, &prepass) == CNA_RESULT_SUCCESS) {
        CNA_Handle prepass_effect = CNA_INVALID_HANDLE;
        CNA_Handle skinned_prepass = CNA_INVALID_HANDLE;
        const CNA_Result a = cna_depth_normal_prepass_get_prepass_effect(prepass, &prepass_effect);
        const CNA_Result b =
            cna_depth_normal_prepass_get_skinned_prepass_effect(prepass, &skinned_prepass);
        say("lent.prepass_effect           %s %s", name_of(a),
            prepass_effect != CNA_INVALID_HANDLE ? "valid" : "invalid");
        say("lent.prepass_skinned_effect   %s %s", name_of(b),
            skinned_prepass != CNA_INVALID_HANDLE ? "valid" : "invalid");
        CNA_Handle prepass_again = CNA_INVALID_HANDLE;
        cna_depth_normal_prepass_get_prepass_effect(prepass, &prepass_again);
        say("lent.prepass_effect_stable    %s",
            prepass_again == prepass_effect ? "same handle" : "fresh each call");
        cna_depth_normal_prepass_destroy(prepass);
    }
}

static CNA_Result on_update(CNA_Handle game, const CNA_GameTime* game_time, void* context,
                            CNA_CallbackError* out_error)
{
    (void)game_time;
    (void)out_error;
    int* ran = (int*)context;
    if (*ran) return CNA_RESULT_SUCCESS;
    *ran = 1;

    CNA_Handle device = CNA_INVALID_HANDLE;
    const CNA_Result borrowed = cna_game_get_graphics_device(game, &device);
    say("device.borrow                 %s", name_of(borrowed));
    if (borrowed != CNA_RESULT_SUCCESS || device == CNA_INVALID_HANDLE) {
        g_failures++;
        return CNA_RESULT_SUCCESS;
    }

    report_renderer(device);
    capability(device, CNA_GRAPHICS_CAPABILITY_COMPUTE_SHADERS, "compute_shaders");
    capability(device, CNA_GRAPHICS_CAPABILITY_INDIRECT_DRAW, "indirect_draw");
    capability(device, CNA_GRAPHICS_CAPABILITY_CUSTOM_EFFECTS, "custom_effects");
    capability(device, CNA_GRAPHICS_CAPABILITY_FLOAT_RENDER_TARGETS, "float_targets");
    capability(device, CNA_GRAPHICS_CAPABILITY_OCCLUSION_QUERY, "occlusion_query");
    capability(device, CNA_GRAPHICS_CAPABILITY_INSTANCING, "instancing");
    capability(device, CNA_GRAPHICS_CAPABILITY_MULTIPLE_RENDER_TARGETS, "mrt");

    CNA_GpuTimerHandle timer = 0;
    if (cna_gpu_timer_create(device, &timer) == CNA_RESULT_SUCCESS) {
        CNA_Bool supported = CNA_FALSE;
        cna_gpu_timer_is_supported(timer, &supported);
        say("gpu_timer.supported           %s", supported ? "yes" : "no");
        if (!supported) {
            char reason[512];
            uint64_t bytes = 0;
            if (cna_gpu_timer_copy_unsupported_reason(timer, reason, sizeof reason - 1, &bytes) ==
                    CNA_RESULT_SUCCESS && bytes < sizeof reason) {
                reason[bytes] = '\0';
                say("gpu_timer.reason              %s", reason);
            }
        }
        cna_gpu_timer_destroy(timer);
    }

    CNA_GpuInstanceCullerHandle culler = 0;
    if (cna_gpu_instance_culler_create(device, &culler) == CNA_RESULT_SUCCESS) {
        CNA_Bool supported = CNA_FALSE;
        cna_gpu_instance_culler_is_supported(culler, &supported);
        say("gpu_culler.supported          %s", supported ? "yes" : "no");
        if (!supported) {
            char reason[512];
            uint64_t bytes = 0;
            if (cna_gpu_instance_culler_copy_unsupported_reason(culler, reason, sizeof reason - 1,
                                                                &bytes) == CNA_RESULT_SUCCESS &&
                bytes < sizeof reason) {
                reason[bytes] = '\0';
                say("gpu_culler.reason             %s", reason);
            }
        }
        cna_gpu_instance_culler_destroy(culler);
    }

    compute_family(device);
    readback_family(device);
    auto_exposure_family(device);
    lent_effect_handles(device);
    return CNA_RESULT_SUCCESS;
}

int main(int argc, char** argv)
{
    if (argc > 1) {
        g_report = fopen(argv[1], "w");
        if (g_report == NULL) {
            fprintf(stderr, "cannot open report file %s\n", argv[1]);
            return 2;
        }
    }

    const char* requested = getenv("CNA_GRAPHICS_RENDERER");
    say("requested.renderer            %s", requested != NULL ? requested : "<build default>");

    int32_t engine_version = 0;
    cna_engine_layer_get_version(&engine_version);
    say("engine_layer.version          %d (header %d)", (int)engine_version,
        (int)CNA_ENGINE_LAYER_VERSION);

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
    info.window_title.data = "gpu qualification";
    info.window_title.byte_length = 17U;
    info.callbacks = &callbacks;

    CNA_Handle game = CNA_INVALID_HANDLE;
    const CNA_Result created = cna_game_create(&info, &game);
    say("game.create                   %s", name_of(created));
    if (created != CNA_RESULT_SUCCESS) {
        say("PROBE INCOMPLETE               no game, so no device-backed answers");
        if (g_report != NULL) fclose(g_report);
        return 1;
    }

    CNA_GraphicsDeviceManagerHandle manager = 0;
    const CNA_Result made_manager = cna_graphics_device_manager_create(game, &manager);
    const CNA_Result frame = cna_game_run_one_frame(game);
    say("game.frame                    manager=%s frame=%s", name_of(made_manager),
        name_of(frame));
    if (manager != 0) cna_graphics_device_manager_destroy(manager);
    say("game.destroy                  %s", name_of(cna_game_destroy(game)));
    say("probe.entered_update          %s", ran ? "yes" : "no");
    say("probe.failures                %d", g_failures);
    if (g_report != NULL) fclose(g_report);
    return (ran && g_failures == 0) ? 0 : 1;
}
