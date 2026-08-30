package org.openeggbert.cna.internal.generated;

/**
 * Generated CNA C ABI declarations for NativeGraphicsExtensionRoutes.
 *
 * <p>Produced by {@code tools/native-abi/generate_jni.py} from the live CNA C headers.
 * Do not edit: every signature here is the header's own declaration, and regenerating
 * is how a change upstream reaches Java. This class is not application API.
 */
public final class NativeGraphicsExtensionRoutes {

    private NativeGraphicsExtensionRoutes() {
    }

    /**
     * cna_ascii_post_process_effect_create (graphics_ext.h).
     */
    public static native int asciiPostProcessEffectCreate(long graphicsDevice, long[] outEffect);

    /**
     * cna_ascii_post_process_effect_destroy (graphics_ext.h).
     */
    public static native int asciiPostProcessEffectDestroy(long effect);

    /**
     * cna_ascii_post_process_effect_draw (graphics_ext.h).
     *
     * <p>destinationRectangleIntegral carries CNA_Rectangle in this order:
     * <ol start="0">
     *   <li>{@code x} (int32_t)</li>
     *   <li>{@code y} (int32_t)</li>
     *   <li>{@code width} (int32_t)</li>
     *   <li>{@code height} (int32_t)</li>
     * </ol>
     */
    public static native int asciiPostProcessEffectDraw(long effect, long source, long[] destinationRectangleIntegral);

    /**
     * cna_ascii_post_process_effect_get_cell_size (graphics_ext.h).
     */
    public static native int asciiPostProcessEffectGetCellSize(long effect, int[] outWidth, int[] outHeight);

    /**
     * cna_ascii_post_process_effect_get_last_grid_dimensions (graphics_ext.h).
     */
    public static native int asciiPostProcessEffectGetLastGridDimensions(long effect, int[] outColumns, int[] outRows);

    /**
     * cna_ascii_post_process_effect_get_quantize_mode (graphics_ext.h).
     */
    public static native int asciiPostProcessEffectGetQuantizeMode(long effect, int[] outMode);

    /**
     * cna_ascii_post_process_effect_set_cell_size (graphics_ext.h).
     */
    public static native int asciiPostProcessEffectSetCellSize(long effect, int width, int height);

    /**
     * cna_ascii_post_process_effect_set_quantize_mode (graphics_ext.h).
     */
    public static native int asciiPostProcessEffectSetQuantizeMode(long effect, int mode);

    /**
     * cna_crt_effect_create (graphics_ext.h).
     */
    public static native int crtEffectCreate(long graphicsDevice, long[] outEffect);

    /**
     * cna_crt_effect_get_curvature (graphics_ext.h).
     */
    public static native int crtEffectGetCurvature(long effect, float[] outValue);

    /**
     * cna_crt_effect_get_mask_intensity (graphics_ext.h).
     */
    public static native int crtEffectGetMaskIntensity(long effect, float[] outValue);

    /**
     * cna_crt_effect_get_mask_type (graphics_ext.h).
     */
    public static native int crtEffectGetMaskType(long effect, int[] outMaskType);

    /**
     * cna_crt_effect_get_scanline_intensity (graphics_ext.h).
     */
    public static native int crtEffectGetScanlineIntensity(long effect, float[] outValue);

    /**
     * cna_crt_effect_get_vignette_intensity (graphics_ext.h).
     */
    public static native int crtEffectGetVignetteIntensity(long effect, float[] outValue);

    /**
     * cna_crt_effect_set_curvature (graphics_ext.h).
     */
    public static native int crtEffectSetCurvature(long effect, float value);

    /**
     * cna_crt_effect_set_mask_intensity (graphics_ext.h).
     */
    public static native int crtEffectSetMaskIntensity(long effect, float value);

    /**
     * cna_crt_effect_set_mask_type (graphics_ext.h).
     */
    public static native int crtEffectSetMaskType(long effect, int maskType);

    /**
     * cna_crt_effect_set_scanline_intensity (graphics_ext.h).
     */
    public static native int crtEffectSetScanlineIntensity(long effect, float value);

    /**
     * cna_crt_effect_set_vignette_intensity (graphics_ext.h).
     */
    public static native int crtEffectSetVignetteIntensity(long effect, float value);

    /**
     * cna_depth_effect_create (graphics_ext.h).
     */
    public static native int depthEffectCreate(long graphicsDevice, long[] outEffect);

    /**
     * cna_depth_effect_get_dither_mode (graphics_ext.h).
     */
    public static native int depthEffectGetDitherMode(long effect, int[] outDitherMode);

    /**
     * cna_depth_effect_get_mode (graphics_ext.h).
     */
    public static native int depthEffectGetMode(long effect, int[] outMode);

    /**
     * cna_depth_effect_set_dither_mode (graphics_ext.h).
     */
    public static native int depthEffectSetDitherMode(long effect, int ditherMode);

    /**
     * cna_depth_effect_set_mode (graphics_ext.h).
     */
    public static native int depthEffectSetMode(long effect, int mode);

    /**
     * cna_graphics_ext_is_available (graphics_ext.h).
     */
    public static native int graphicsExtIsAvailable(boolean[] outAvailable);

    /**
     * cna_pbr_material_init (graphics_ext.h).
     *
     * <p>outMaterialBytes carries CNA_PbrMaterial in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>outMaterialIntegral carries CNA_PbrMaterial in this order:
     * <ol start="0">
     *   <li>{@code albedo_texture} (CNA_Handle)</li>
     *   <li>{@code normal_texture} (CNA_Handle)</li>
     *   <li>{@code metallic_roughness_texture} (CNA_Handle)</li>
     *   <li>{@code ambient_occlusion_texture} (CNA_Handle)</li>
     *   <li>{@code emissive_texture} (CNA_Handle)</li>
     *   <li>{@code albedo_color.r} (uint8_t)</li>
     *   <li>{@code albedo_color.g} (uint8_t)</li>
     *   <li>{@code albedo_color.b} (uint8_t)</li>
     *   <li>{@code albedo_color.a} (uint8_t)</li>
     *   <li>{@code emissive_color.r} (uint8_t)</li>
     *   <li>{@code emissive_color.g} (uint8_t)</li>
     *   <li>{@code emissive_color.b} (uint8_t)</li>
     *   <li>{@code emissive_color.a} (uint8_t)</li>
     *   <li>{@code alpha_blend_enabled} (CNA_Bool)</li>
     * </ol>
     *
     * <p>outMaterialFloating carries CNA_PbrMaterial in this order:
     * <ol start="0">
     *   <li>{@code metallic_factor} (float)</li>
     *   <li>{@code roughness_factor} (float)</li>
     *   <li>{@code normal_scale} (float)</li>
     *   <li>{@code occlusion_strength} (float)</li>
     *   <li>{@code alpha_cutoff} (float)</li>
     * </ol>
     */
    public static native int pbrMaterialInit(byte[] outMaterialBytes, long[] outMaterialIntegral, float[] outMaterialFloating);

    /**
     * cna_render_pipeline_settings_init (graphics_ext.h).
     *
     * <p>outSettingsIntegral carries CNA_RenderPipelineSettings in this order:
     * <ol start="0">
     *   <li>{@code tonemapping_mode} (CNA_TonemappingMode)</li>
     *   <li>{@code render_quality} (CNA_RenderQuality)</li>
     *   <li>{@code shadow_quality} (CNA_ShadowQuality)</li>
     *   <li>{@code hdr_enabled} (CNA_Bool)</li>
     *   <li>{@code bloom_enabled} (CNA_Bool)</li>
     *   <li>{@code ssao_enabled} (CNA_Bool)</li>
     *   <li>{@code shadows_enabled} (CNA_Bool)</li>
     * </ol>
     *
     * <p>outSettingsFloating carries CNA_RenderPipelineSettings in this order:
     * <ol start="0">
     *   <li>{@code exposure} (float)</li>
     *   <li>{@code gamma} (float)</li>
     *   <li>{@code bloom_intensity} (float)</li>
     * </ol>
     */
    public static native int renderPipelineSettingsInit(long[] outSettingsIntegral, float[] outSettingsFloating);
}
