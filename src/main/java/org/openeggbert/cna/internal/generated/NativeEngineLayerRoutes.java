package org.openeggbert.cna.internal.generated;

/**
 * Generated CNA C ABI declarations for NativeEngineLayerRoutes.
 *
 * <p>Produced by {@code tools/native-abi/generate_jni.py} from the live CNA C headers.
 * Do not edit: every signature here is the header's own declaration, and regenerating
 * is how a change upstream reaches Java. This class is not application API.
 */
public final class NativeEngineLayerRoutes {

    private NativeEngineLayerRoutes() {
    }

    /**
     * cna_aerial_perspective_pass_air_mass_for_distance (engine_layer.h).
     *
     * <p>viewDirectionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int aerialPerspectivePassAirMassForDistance(float[] viewDirectionFloating, float distance, float scaleHeight, float[] outAirMass);

    /**
     * cna_aerial_perspective_pass_copy_fallback_reason (engine_layer.h).
     */
    public static native int aerialPerspectivePassCopyFallbackReason(long pass, byte[] destination, long[] outBytes);

    /**
     * cna_aerial_perspective_pass_create (engine_layer.h).
     */
    public static native int aerialPerspectivePassCreate(long graphicsDevice, long[] outPass);

    /**
     * cna_aerial_perspective_pass_get_intensity (engine_layer.h).
     */
    public static native int aerialPerspectivePassGetIntensity(long pass, float[] outValue);

    /**
     * cna_aerial_perspective_pass_get_scale_height (engine_layer.h).
     */
    public static native int aerialPerspectivePassGetScaleHeight(long pass, float[] outValue);

    /**
     * cna_aerial_perspective_pass_get_sun_direction (engine_layer.h).
     *
     * <p>outValueFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int aerialPerspectivePassGetSunDirection(long pass, float[] outValueFloating);

    /**
     * cna_aerial_perspective_pass_get_turbidity (engine_layer.h).
     */
    public static native int aerialPerspectivePassGetTurbidity(long pass, float[] outValue);

    /**
     * cna_aerial_perspective_pass_set_intensity (engine_layer.h).
     */
    public static native int aerialPerspectivePassSetIntensity(long pass, float value);

    /**
     * cna_aerial_perspective_pass_set_scale_height (engine_layer.h).
     */
    public static native int aerialPerspectivePassSetScaleHeight(long pass, float value);

    /**
     * cna_aerial_perspective_pass_set_sun_direction (engine_layer.h).
     *
     * <p>valueFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int aerialPerspectivePassSetSunDirection(long pass, float[] valueFloating);

    /**
     * cna_aerial_perspective_pass_set_turbidity (engine_layer.h).
     */
    public static native int aerialPerspectivePassSetTurbidity(long pass, float value);

    /**
     * cna_aerial_perspective_pass_transmittance (engine_layer.h).
     *
     * <p>outTransmittanceFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int aerialPerspectivePassTransmittance(float turbidity, float airMass, float[] outTransmittanceFloating);

    /**
     * cna_area_light_brdf_table_copy_lookup_glsl (engine_layer.h).
     */
    public static native int areaLightBrdfTableCopyLookupGlsl(byte[] destination, long[] outBytes);

    /**
     * cna_area_light_brdf_table_create (engine_layer.h).
     */
    public static native int areaLightBrdfTableCreate(long graphicsDevice, long[] outTable);

    /**
     * cna_area_light_brdf_table_create_with_size (engine_layer.h).
     */
    public static native int areaLightBrdfTableCreateWithSize(long graphicsDevice, int size, int sampleCount, long[] outTable);

    /**
     * cna_area_light_brdf_table_destroy (engine_layer.h).
     */
    public static native int areaLightBrdfTableDestroy(long table);

    /**
     * cna_area_light_brdf_table_evaluate (engine_layer.h).
     *
     * <p>outTermsFloating carries CNA_AreaLightBrdfTerms in this order:
     * <ol start="0">
     *   <li>{@code magnitude} (float)</li>
     *   <li>{@code fresnel} (float)</li>
     *   <li>{@code average_tangent} (float)</li>
     *   <li>{@code average_normal} (float)</li>
     * </ol>
     */
    public static native int areaLightBrdfTableEvaluate(float roughness, float cosTheta, int sampleCount, float[] outTermsFloating);

    /**
     * cna_area_light_brdf_table_get_generation_milliseconds (engine_layer.h).
     */
    public static native int areaLightBrdfTableGetGenerationMilliseconds(long table, double[] outMilliseconds);

    /**
     * cna_area_light_brdf_table_get_sample_count (engine_layer.h).
     */
    public static native int areaLightBrdfTableGetSampleCount(long table, int[] outSampleCount);

    /**
     * cna_area_light_brdf_table_get_size (engine_layer.h).
     */
    public static native int areaLightBrdfTableGetSize(long table, int[] outSize);

    /**
     * cna_area_light_ext_init (engine_layer.h).
     *
     * <p>outLightBytes carries CNA_AreaLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved0[0]} (uint8_t)</li>
     *   <li>{@code reserved0[1]} (uint8_t)</li>
     *   <li>{@code reserved0[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>outLightIntegral carries CNA_AreaLightEXT in this order:
     * <ol start="0">
     *   <li>{@code shape} (CNA_AreaLightShapeEXT)</li>
     *   <li>{@code two_sided} (CNA_Bool)</li>
     * </ol>
     *
     * <p>outLightFloating carries CNA_AreaLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code right_axis.x} (float)</li>
     *   <li>{@code right_axis.y} (float)</li>
     *   <li>{@code right_axis.z} (float)</li>
     *   <li>{@code up_axis.x} (float)</li>
     *   <li>{@code up_axis.y} (float)</li>
     *   <li>{@code up_axis.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     * </ol>
     */
    public static native int areaLightExtInit(byte[] outLightBytes, long[] outLightIntegral, float[] outLightFloating);

    /**
     * cna_area_light_ext_is_valid (engine_layer.h).
     *
     * <p>lightBytes carries CNA_AreaLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved0[0]} (uint8_t)</li>
     *   <li>{@code reserved0[1]} (uint8_t)</li>
     *   <li>{@code reserved0[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>lightIntegral carries CNA_AreaLightEXT in this order:
     * <ol start="0">
     *   <li>{@code shape} (CNA_AreaLightShapeEXT)</li>
     *   <li>{@code two_sided} (CNA_Bool)</li>
     * </ol>
     *
     * <p>lightFloating carries CNA_AreaLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code right_axis.x} (float)</li>
     *   <li>{@code right_axis.y} (float)</li>
     *   <li>{@code right_axis.z} (float)</li>
     *   <li>{@code up_axis.x} (float)</li>
     *   <li>{@code up_axis.y} (float)</li>
     *   <li>{@code up_axis.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     * </ol>
     */
    public static native int areaLightExtIsValid(byte[] lightBytes, long[] lightIntegral, float[] lightFloating, boolean[] outValid);

    /**
     * cna_area_light_shading_contribution (engine_layer.h).
     *
     * <p>lightBytes carries CNA_AreaLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved0[0]} (uint8_t)</li>
     *   <li>{@code reserved0[1]} (uint8_t)</li>
     *   <li>{@code reserved0[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>lightIntegral carries CNA_AreaLightEXT in this order:
     * <ol start="0">
     *   <li>{@code shape} (CNA_AreaLightShapeEXT)</li>
     *   <li>{@code two_sided} (CNA_Bool)</li>
     * </ol>
     *
     * <p>lightFloating carries CNA_AreaLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code right_axis.x} (float)</li>
     *   <li>{@code right_axis.y} (float)</li>
     *   <li>{@code right_axis.z} (float)</li>
     *   <li>{@code up_axis.x} (float)</li>
     *   <li>{@code up_axis.y} (float)</li>
     *   <li>{@code up_axis.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     * </ol>
     *
     * <p>surfaceFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     *
     * <p>normalFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     *
     * <p>cameraPositionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     *
     * <p>baseColorFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     *
     * <p>outContributionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int areaLightShadingContribution(byte[] lightBytes, long[] lightIntegral, float[] lightFloating, float[] surfaceFloating, float[] normalFloating, float[] cameraPositionFloating, float[] baseColorFloating, float metallic, float roughness, float[] outContributionFloating);

    /**
     * cna_area_light_shading_copy_shading_glsl (engine_layer.h).
     */
    public static native int areaLightShadingCopyShadingGlsl(byte[] destination, long[] outBytes);

    /**
     * cna_area_light_shading_coverage (engine_layer.h).
     *
     * <p>surfaceFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     *
     * <p>lobeAxisFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int areaLightShadingCoverage(float[] quadFloating, float[] surfaceFloating, float[] lobeAxisFloating, float lobeScale, boolean twoSided, float[] outCoverage);

    /**
     * cna_area_light_shading_lobe_scale_for (engine_layer.h).
     */
    public static native int areaLightShadingLobeScaleFor(float roughness, float[] outScale);

    /**
     * cna_area_light_shading_quad_of (engine_layer.h).
     *
     * <p>lightBytes carries CNA_AreaLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved0[0]} (uint8_t)</li>
     *   <li>{@code reserved0[1]} (uint8_t)</li>
     *   <li>{@code reserved0[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>lightIntegral carries CNA_AreaLightEXT in this order:
     * <ol start="0">
     *   <li>{@code shape} (CNA_AreaLightShapeEXT)</li>
     *   <li>{@code two_sided} (CNA_Bool)</li>
     * </ol>
     *
     * <p>lightFloating carries CNA_AreaLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code right_axis.x} (float)</li>
     *   <li>{@code right_axis.y} (float)</li>
     *   <li>{@code right_axis.z} (float)</li>
     *   <li>{@code up_axis.x} (float)</li>
     *   <li>{@code up_axis.y} (float)</li>
     *   <li>{@code up_axis.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     * </ol>
     *
     * <p>surfaceFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int areaLightShadingQuadOf(byte[] lightBytes, long[] lightIntegral, float[] lightFloating, float[] surfaceFloating, float[] outQuadFloating);

    /**
     * cna_atmospheric_sky_copy_model_glsl (engine_layer.h).
     */
    public static native int atmosphericSkyCopyModelGlsl(byte[] destination, long[] outBytes);

    /**
     * cna_atmospheric_sky_create (engine_layer.h).
     */
    public static native int atmosphericSkyCreate(long graphicsDevice, long[] outSky);

    /**
     * cna_atmospheric_sky_destroy (engine_layer.h).
     */
    public static native int atmosphericSkyDestroy(long sky);

    /**
     * cna_atmospheric_sky_draw (engine_layer.h).
     *
     * <p>viewFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     *
     * <p>projectionFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     */
    public static native int atmosphericSkyDraw(long sky, float[] viewFloating, float[] projectionFloating, int width, int height);

    /**
     * cna_atmospheric_sky_get_intensity (engine_layer.h).
     */
    public static native int atmosphericSkyGetIntensity(long sky, float[] outIntensity);

    /**
     * cna_atmospheric_sky_get_sun_direction (engine_layer.h).
     *
     * <p>outDirectionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int atmosphericSkyGetSunDirection(long sky, float[] outDirectionFloating);

    /**
     * cna_atmospheric_sky_get_turbidity (engine_layer.h).
     */
    public static native int atmosphericSkyGetTurbidity(long sky, float[] outTurbidity);

    /**
     * cna_atmospheric_sky_is_supported (engine_layer.h).
     */
    public static native int atmosphericSkyIsSupported(long sky, boolean[] outSupported);

    /**
     * cna_atmospheric_sky_radiance (engine_layer.h).
     *
     * <p>viewDirectionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     *
     * <p>sunDirectionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     *
     * <p>outRadianceFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int atmosphericSkyRadiance(float[] viewDirectionFloating, float[] sunDirectionFloating, float turbidity, float[] outRadianceFloating);

    /**
     * cna_atmospheric_sky_set_intensity (engine_layer.h).
     */
    public static native int atmosphericSkySetIntensity(long sky, float intensity);

    /**
     * cna_atmospheric_sky_set_sun_direction (engine_layer.h).
     *
     * <p>directionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int atmosphericSkySetSunDirection(long sky, float[] directionFloating);

    /**
     * cna_atmospheric_sky_set_turbidity (engine_layer.h).
     */
    public static native int atmosphericSkySetTurbidity(long sky, float turbidity);

    /**
     * cna_blit_pass_create (engine_layer.h).
     */
    public static native int blitPassCreate(long graphicsDevice, long[] outPass);

    /**
     * cna_bloom_pass_create (engine_layer.h).
     */
    public static native int bloomPassCreate(long graphicsDevice, long[] outPass);

    /**
     * cna_bloom_pass_extract_channel (engine_layer.h).
     */
    public static native int bloomPassExtractChannel(float value, float threshold, float[] outExtracted);

    /**
     * cna_bloom_pass_get_intensity (engine_layer.h).
     */
    public static native int bloomPassGetIntensity(long pass, float[] outValue);

    /**
     * cna_bloom_pass_get_iterations (engine_layer.h).
     */
    public static native int bloomPassGetIterations(long pass, int[] outValue);

    /**
     * cna_bloom_pass_get_threshold (engine_layer.h).
     */
    public static native int bloomPassGetThreshold(long pass, float[] outValue);

    /**
     * cna_bloom_pass_iterations_for_quality (engine_layer.h).
     */
    public static native int bloomPassIterationsForQuality(int quality, int[] outIterations);

    /**
     * cna_bloom_pass_reset_targets (engine_layer.h).
     */
    public static native int bloomPassResetTargets(long pass);

    /**
     * cna_bloom_pass_set_intensity (engine_layer.h).
     */
    public static native int bloomPassSetIntensity(long pass, float value);

    /**
     * cna_bloom_pass_set_iterations (engine_layer.h).
     */
    public static native int bloomPassSetIterations(long pass, int value);

    /**
     * cna_bloom_pass_set_threshold (engine_layer.h).
     */
    public static native int bloomPassSetThreshold(long pass, float value);

    /**
     * cna_cascaded_shadow_map_apply_to_receiver (engine_layer.h).
     */
    public static native int cascadedShadowMapApplyToReceiver(long shadowMap, long effect);

    /**
     * cna_cascaded_shadow_map_begin (engine_layer.h).
     */
    public static native int cascadedShadowMapBegin(long shadowMap, int cascadeIndex);

    /**
     * cna_cascaded_shadow_map_compute_bounding_sphere (engine_layer.h).
     *
     * <p>outCentreFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int cascadedShadowMapComputeBoundingSphere(float[] cornersFloating, float[] outCentreFloating, float[] outRadius);

    /**
     * cna_cascaded_shadow_map_compute_frustum_corners (engine_layer.h).
     *
     * <p>viewFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     *
     * <p>projectionFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     */
    public static native int cascadedShadowMapComputeFrustumCorners(float[] viewFloating, float[] projectionFloating, float[] outCornersFloating);

    /**
     * cna_cascaded_shadow_map_compute_split_distances (engine_layer.h).
     */
    public static native int cascadedShadowMapComputeSplitDistances(float nearPlane, float farPlane, int cascadeCount, float lambda, float[] destination, long[] outCount);

    /**
     * cna_cascaded_shadow_map_create (engine_layer.h).
     */
    public static native int cascadedShadowMapCreate(long graphicsDevice, int quality, int cascadeCount, long[] outShadowMap);

    /**
     * cna_cascaded_shadow_map_destroy (engine_layer.h).
     */
    public static native int cascadedShadowMapDestroy(long shadowMap);

    /**
     * cna_cascaded_shadow_map_end (engine_layer.h).
     */
    public static native int cascadedShadowMapEnd(long shadowMap);

    /**
     * cna_cascaded_shadow_map_get_blend_band (engine_layer.h).
     */
    public static native int cascadedShadowMapGetBlendBand(long shadowMap, float[] outBand);

    /**
     * cna_cascaded_shadow_map_get_cascade_count (engine_layer.h).
     */
    public static native int cascadedShadowMapGetCascadeCount(long shadowMap, int[] outCount);

    /**
     * cna_cascaded_shadow_map_get_cascade_matrix (engine_layer.h).
     *
     * <p>outMatrixFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     */
    public static native int cascadedShadowMapGetCascadeMatrix(long shadowMap, int cascadeIndex, float[] outMatrixFloating);

    /**
     * cna_cascaded_shadow_map_get_cascade_size (engine_layer.h).
     */
    public static native int cascadedShadowMapGetCascadeSize(long shadowMap, int[] outSize);

    /**
     * cna_cascaded_shadow_map_get_shadow_texture (engine_layer.h).
     */
    public static native int cascadedShadowMapGetShadowTexture(long shadowMap, long[] outTexture);

    /**
     * cna_cascaded_shadow_map_get_split_distance (engine_layer.h).
     */
    public static native int cascadedShadowMapGetSplitDistance(long shadowMap, int cascadeIndex, float[] outDistance);

    /**
     * cna_cascaded_shadow_map_get_split_lambda (engine_layer.h).
     */
    public static native int cascadedShadowMapGetSplitLambda(long shadowMap, float[] outLambda);

    /**
     * cna_cascaded_shadow_map_is_debug_tint_enabled (engine_layer.h).
     */
    public static native int cascadedShadowMapIsDebugTintEnabled(long shadowMap, boolean[] outEnabled);

    /**
     * cna_cascaded_shadow_map_is_supported (engine_layer.h).
     */
    public static native int cascadedShadowMapIsSupported(long shadowMap, boolean[] outSupported);

    /**
     * cna_cascaded_shadow_map_select_cascade (engine_layer.h).
     */
    public static native int cascadedShadowMapSelectCascade(long shadowMap, float viewDepth, int[] outIndex);

    /**
     * cna_cascaded_shadow_map_set_blend_band (engine_layer.h).
     */
    public static native int cascadedShadowMapSetBlendBand(long shadowMap, float band);

    /**
     * cna_cascaded_shadow_map_set_debug_tint_enabled (engine_layer.h).
     */
    public static native int cascadedShadowMapSetDebugTintEnabled(long shadowMap, boolean enabled);

    /**
     * cna_cascaded_shadow_map_set_split_lambda (engine_layer.h).
     */
    public static native int cascadedShadowMapSetSplitLambda(long shadowMap, float lambda);

    /**
     * cna_cascaded_shadow_map_snap_to_texel_grid (engine_layer.h).
     *
     * <p>centreFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     *
     * <p>outCentreFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int cascadedShadowMapSnapToTexelGrid(float[] centreFloating, float radius, int cascadeSize, float[] outCentreFloating);

    /**
     * cna_cascaded_shadow_map_update (engine_layer.h).
     *
     * <p>lightBytes carries CNA_DirectionalLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>lightIntegral carries CNA_DirectionalLightEXT in this order:
     * <ol start="0">
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>lightFloating carries CNA_DirectionalLightEXT in this order:
     * <ol start="0">
     *   <li>{@code direction.x} (float)</li>
     *   <li>{@code direction.y} (float)</li>
     *   <li>{@code direction.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     * </ol>
     *
     * <p>cameraViewFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     *
     * <p>cameraProjectionFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     */
    public static native int cascadedShadowMapUpdate(long shadowMap, byte[] lightBytes, long[] lightIntegral, float[] lightFloating, float[] cameraViewFloating, float[] cameraProjectionFloating);

    /**
     * cna_chromatic_aberration_pass_create (engine_layer.h).
     */
    public static native int chromaticAberrationPassCreate(long graphicsDevice, long[] outPass);

    /**
     * cna_chromatic_aberration_pass_get_strength (engine_layer.h).
     */
    public static native int chromaticAberrationPassGetStrength(long pass, float[] outValue);

    /**
     * cna_chromatic_aberration_pass_set_strength (engine_layer.h).
     */
    public static native int chromaticAberrationPassSetStrength(long pass, float value);

    /**
     * cna_clustered_forward_effect_begin (engine_layer.h).
     *
     * <p>worldFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     *
     * <p>viewFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     *
     * <p>projectionFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     *
     * <p>cameraPositionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int clusteredForwardEffectBegin(long effect, float[] worldFloating, float[] viewFloating, float[] projectionFloating, float[] cameraPositionFloating, long lights);

    /**
     * cna_clustered_forward_effect_clear_area_light (engine_layer.h).
     */
    public static native int clusteredForwardEffectClearAreaLight(long effect);

    /**
     * cna_clustered_forward_effect_clear_light_probe (engine_layer.h).
     */
    public static native int clusteredForwardEffectClearLightProbe(long effect);

    /**
     * cna_clustered_forward_effect_contribution (engine_layer.h).
     *
     * <p>lightBytes carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>lightIntegral carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code type} (CNA_ClusteredLightType)</li>
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>lightFloating carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code direction.x} (float)</li>
     *   <li>{@code direction.y} (float)</li>
     *   <li>{@code direction.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     *   <li>{@code inner_angle} (float)</li>
     *   <li>{@code outer_angle} (float)</li>
     * </ol>
     *
     * <p>surfaceFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     *
     * <p>normalFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     *
     * <p>cameraPositionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     *
     * <p>baseColorFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     *
     * <p>sheenColorFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     *
     * <p>subsurfaceColorFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     *
     * <p>outContributionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int clusteredForwardEffectContribution(byte[] lightBytes, long[] lightIntegral, float[] lightFloating, float[] surfaceFloating, float[] normalFloating, float[] cameraPositionFloating, float[] baseColorFloating, float metallic, float roughness, float clearcoat, float clearcoatRoughness, float[] sheenColorFloating, float sheenRoughness, float iridescence, float iridescenceIor, float iridescenceThickness, float[] subsurfaceColorFloating, float subsurfaceWrap, float[] outContributionFloating);

    /**
     * cna_clustered_forward_effect_contribution_with_extensions (engine_layer.h).
     *
     * <p>lightBytes carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>lightIntegral carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code type} (CNA_ClusteredLightType)</li>
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>lightFloating carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code direction.x} (float)</li>
     *   <li>{@code direction.y} (float)</li>
     *   <li>{@code direction.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     *   <li>{@code inner_angle} (float)</li>
     *   <li>{@code outer_angle} (float)</li>
     * </ol>
     *
     * <p>surfaceFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     *
     * <p>normalFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     *
     * <p>cameraPositionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     *
     * <p>baseColorFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     *
     * <p>outContributionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int clusteredForwardEffectContributionWithExtensions(byte[] lightBytes, long[] lightIntegral, float[] lightFloating, float[] surfaceFloating, float[] normalFloating, float[] cameraPositionFloating, float[] baseColorFloating, float metallic, float roughness, long extensions, float[] outContributionFloating);

    /**
     * cna_clustered_forward_effect_create (engine_layer.h).
     */
    public static native int clusteredForwardEffectCreate(long graphicsDevice, long[] outEffect);

    /**
     * cna_clustered_forward_effect_destroy (engine_layer.h).
     */
    public static native int clusteredForwardEffectDestroy(long effect);

    /**
     * cna_clustered_forward_effect_get_ambient (engine_layer.h).
     *
     * <p>outAmbientFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int clusteredForwardEffectGetAmbient(long effect, float[] outAmbientFloating);

    /**
     * cna_clustered_forward_effect_get_base_color (engine_layer.h).
     *
     * <p>outColorFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int clusteredForwardEffectGetBaseColor(long effect, float[] outColorFloating);

    /**
     * cna_clustered_forward_effect_get_ior (engine_layer.h).
     */
    public static native int clusteredForwardEffectGetIor(long effect, float[] outIor);

    /**
     * cna_clustered_forward_effect_get_metallic (engine_layer.h).
     */
    public static native int clusteredForwardEffectGetMetallic(long effect, float[] outMetallic);

    /**
     * cna_clustered_forward_effect_get_roughness (engine_layer.h).
     */
    public static native int clusteredForwardEffectGetRoughness(long effect, float[] outRoughness);

    /**
     * cna_clustered_forward_effect_has_area_light (engine_layer.h).
     */
    public static native int clusteredForwardEffectHasAreaLight(long effect, boolean[] outHas);

    /**
     * cna_clustered_forward_effect_has_light_probe (engine_layer.h).
     */
    public static native int clusteredForwardEffectHasLightProbe(long effect, boolean[] outHas);

    /**
     * cna_clustered_forward_effect_is_supported (engine_layer.h).
     */
    public static native int clusteredForwardEffectIsSupported(long effect, boolean[] outSupported);

    /**
     * cna_clustered_forward_effect_set_ambient (engine_layer.h).
     *
     * <p>ambientFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int clusteredForwardEffectSetAmbient(long effect, float[] ambientFloating);

    /**
     * cna_clustered_forward_effect_set_area_light (engine_layer.h).
     *
     * <p>lightBytes carries CNA_AreaLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved0[0]} (uint8_t)</li>
     *   <li>{@code reserved0[1]} (uint8_t)</li>
     *   <li>{@code reserved0[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>lightIntegral carries CNA_AreaLightEXT in this order:
     * <ol start="0">
     *   <li>{@code shape} (CNA_AreaLightShapeEXT)</li>
     *   <li>{@code two_sided} (CNA_Bool)</li>
     * </ol>
     *
     * <p>lightFloating carries CNA_AreaLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code right_axis.x} (float)</li>
     *   <li>{@code right_axis.y} (float)</li>
     *   <li>{@code right_axis.z} (float)</li>
     *   <li>{@code up_axis.x} (float)</li>
     *   <li>{@code up_axis.y} (float)</li>
     *   <li>{@code up_axis.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     * </ol>
     */
    public static native int clusteredForwardEffectSetAreaLight(long effect, byte[] lightBytes, long[] lightIntegral, float[] lightFloating, long table);

    /**
     * cna_clustered_forward_effect_set_base_color (engine_layer.h).
     *
     * <p>colorFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int clusteredForwardEffectSetBaseColor(long effect, float[] colorFloating);

    /**
     * cna_clustered_forward_effect_set_ior (engine_layer.h).
     */
    public static native int clusteredForwardEffectSetIor(long effect, float ior);

    /**
     * cna_clustered_forward_effect_set_light_probe (engine_layer.h).
     */
    public static native int clusteredForwardEffectSetLightProbe(long effect, long probe);

    /**
     * cna_clustered_forward_effect_set_light_probe_volume (engine_layer.h).
     */
    public static native int clusteredForwardEffectSetLightProbeVolume(long effect, long volume);

    /**
     * cna_clustered_forward_effect_set_material_extensions (engine_layer.h).
     */
    public static native int clusteredForwardEffectSetMaterialExtensions(long effect, long extensions);

    /**
     * cna_clustered_forward_effect_set_metallic (engine_layer.h).
     */
    public static native int clusteredForwardEffectSetMetallic(long effect, float metallic);

    /**
     * cna_clustered_forward_effect_set_opaque_frame (engine_layer.h).
     */
    public static native int clusteredForwardEffectSetOpaqueFrame(long effect, long frame);

    /**
     * cna_clustered_forward_effect_set_roughness (engine_layer.h).
     */
    public static native int clusteredForwardEffectSetRoughness(long effect, float roughness);

    /**
     * cna_clustered_forward_effect_volume_attenuation (engine_layer.h).
     *
     * <p>attenuationColorFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     *
     * <p>outAttenuationFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int clusteredForwardEffectVolumeAttenuation(float[] attenuationColorFloating, float attenuationDistance, float thickness, float[] outAttenuationFloating);

    /**
     * cna_clustered_light_assignment_adopt (engine_layer.h).
     */
    public static native int clusteredLightAssignmentAdopt(long assignment, int lightCount, int[] offsets, int[] indices);

    /**
     * cna_clustered_light_assignment_assign (engine_layer.h).
     *
     * <p>viewFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     */
    public static native int clusteredLightAssignmentAssign(long assignment, long grid, float[] viewFloating, float[] boundsFloating);

    /**
     * cna_clustered_light_assignment_clear (engine_layer.h).
     */
    public static native int clusteredLightAssignmentClear(long assignment);

    /**
     * cna_clustered_light_assignment_copy_indices (engine_layer.h).
     */
    public static native int clusteredLightAssignmentCopyIndices(long assignment, int[] destination, long[] outCount);

    /**
     * cna_clustered_light_assignment_copy_lights_in_cluster (engine_layer.h).
     */
    public static native int clusteredLightAssignmentCopyLightsInCluster(long assignment, int clusterIndex, int[] destination, long[] outCount);

    /**
     * cna_clustered_light_assignment_copy_offsets (engine_layer.h).
     */
    public static native int clusteredLightAssignmentCopyOffsets(long assignment, int[] destination, long[] outCount);

    /**
     * cna_clustered_light_assignment_create (engine_layer.h).
     */
    public static native int clusteredLightAssignmentCreate(long game, long[] outAssignment);

    /**
     * cna_clustered_light_assignment_destroy (engine_layer.h).
     */
    public static native int clusteredLightAssignmentDestroy(long assignment);

    /**
     * cna_clustered_light_assignment_get_cluster_count (engine_layer.h).
     */
    public static native int clusteredLightAssignmentGetClusterCount(long assignment, int[] outCount);

    /**
     * cna_clustered_light_assignment_get_light_count (engine_layer.h).
     */
    public static native int clusteredLightAssignmentGetLightCount(long assignment, int[] outCount);

    /**
     * cna_clustered_light_assignment_get_max_lights_per_cluster (engine_layer.h).
     */
    public static native int clusteredLightAssignmentGetMaxLightsPerCluster(long assignment, int[] outCount);

    /**
     * cna_clustered_light_assignment_get_total_reference_count (engine_layer.h).
     */
    public static native int clusteredLightAssignmentGetTotalReferenceCount(long assignment, int[] outCount);

    /**
     * cna_clustered_light_buffer_bind (engine_layer.h).
     */
    public static native int clusteredLightBufferBind(long buffer, long effect, int firstUnit);

    /**
     * cna_clustered_light_buffer_copy_light_lookup_glsl (engine_layer.h).
     */
    public static native int clusteredLightBufferCopyLightLookupGlsl(byte[] destination, long[] outBytes);

    /**
     * cna_clustered_light_buffer_create (engine_layer.h).
     */
    public static native int clusteredLightBufferCreate(long graphicsDevice, long[] outBuffer);

    /**
     * cna_clustered_light_buffer_destroy (engine_layer.h).
     */
    public static native int clusteredLightBufferDestroy(long buffer);

    /**
     * cna_clustered_light_buffer_get_cluster_count (engine_layer.h).
     */
    public static native int clusteredLightBufferGetClusterCount(long buffer, int[] outCount);

    /**
     * cna_clustered_light_buffer_get_light_count (engine_layer.h).
     */
    public static native int clusteredLightBufferGetLightCount(long buffer, int[] outCount);

    /**
     * cna_clustered_light_buffer_get_reference_count (engine_layer.h).
     */
    public static native int clusteredLightBufferGetReferenceCount(long buffer, int[] outCount);

    /**
     * cna_clustered_light_buffer_is_uploaded (engine_layer.h).
     */
    public static native int clusteredLightBufferIsUploaded(long buffer, boolean[] outUploaded);

    /**
     * cna_clustered_light_buffer_upload (engine_layer.h).
     */
    public static native int clusteredLightBufferUpload(long buffer, long lights, long grid, long assignment);

    /**
     * cna_clustered_light_compute_assign (engine_layer.h).
     *
     * <p>viewFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     */
    public static native int clusteredLightComputeAssign(long compute, long grid, float[] viewFloating, float[] boundsFloating, long outAssignment);

    /**
     * cna_clustered_light_compute_copy_unsupported_reason (engine_layer.h).
     */
    public static native int clusteredLightComputeCopyUnsupportedReason(long compute, byte[] destination, long[] outBytes);

    /**
     * cna_clustered_light_compute_create (engine_layer.h).
     */
    public static native int clusteredLightComputeCreate(long graphicsDevice, int stride, long[] outCompute);

    /**
     * cna_clustered_light_compute_destroy (engine_layer.h).
     */
    public static native int clusteredLightComputeDestroy(long compute);

    /**
     * cna_clustered_light_compute_get_stride (engine_layer.h).
     */
    public static native int clusteredLightComputeGetStride(long compute, int[] outStride);

    /**
     * cna_clustered_light_compute_has_overflowed (engine_layer.h).
     */
    public static native int clusteredLightComputeHasOverflowed(long compute, boolean[] outOverflowed);

    /**
     * cna_clustered_light_compute_is_supported (engine_layer.h).
     */
    public static native int clusteredLightComputeIsSupported(long compute, boolean[] outSupported);

    /**
     * cna_clustered_light_compute_used_compute (engine_layer.h).
     */
    public static native int clusteredLightComputeUsedCompute(long compute, boolean[] outUsed);

    /**
     * cna_clustered_light_ext_init (engine_layer.h).
     *
     * <p>outLightBytes carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>outLightIntegral carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code type} (CNA_ClusteredLightType)</li>
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>outLightFloating carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code direction.x} (float)</li>
     *   <li>{@code direction.y} (float)</li>
     *   <li>{@code direction.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     *   <li>{@code inner_angle} (float)</li>
     *   <li>{@code outer_angle} (float)</li>
     * </ol>
     */
    public static native int clusteredLightExtInit(byte[] outLightBytes, long[] outLightIntegral, float[] outLightFloating);

    /**
     * cna_clustered_light_grid_cluster_bounds (engine_layer.h).
     *
     * <p>outBoundsFloating carries CNA_BoundingBox in this order:
     * <ol start="0">
     *   <li>{@code min.x} (float)</li>
     *   <li>{@code min.y} (float)</li>
     *   <li>{@code min.z} (float)</li>
     *   <li>{@code max.x} (float)</li>
     *   <li>{@code max.y} (float)</li>
     *   <li>{@code max.z} (float)</li>
     * </ol>
     */
    public static native int clusteredLightGridClusterBounds(long grid, int x, int y, int slice, float[] outBoundsFloating);

    /**
     * cna_clustered_light_grid_cluster_index (engine_layer.h).
     */
    public static native int clusteredLightGridClusterIndex(long grid, int x, int y, int slice, int[] outIndex);

    /**
     * cna_clustered_light_grid_create (engine_layer.h).
     */
    public static native int clusteredLightGridCreate(long game, int tilesX, int tilesY, int sliceCount, long[] outGrid);

    /**
     * cna_clustered_light_grid_destroy (engine_layer.h).
     */
    public static native int clusteredLightGridDestroy(long grid);

    /**
     * cna_clustered_light_grid_get_cluster_count (engine_layer.h).
     */
    public static native int clusteredLightGridGetClusterCount(long grid, int[] outCount);

    /**
     * cna_clustered_light_grid_get_far_plane (engine_layer.h).
     */
    public static native int clusteredLightGridGetFarPlane(long grid, float[] outFar);

    /**
     * cna_clustered_light_grid_get_inverse_projection (engine_layer.h).
     *
     * <p>outMatrixFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     */
    public static native int clusteredLightGridGetInverseProjection(long grid, float[] outMatrixFloating);

    /**
     * cna_clustered_light_grid_get_near_plane (engine_layer.h).
     */
    public static native int clusteredLightGridGetNearPlane(long grid, float[] outNear);

    /**
     * cna_clustered_light_grid_get_slice_count (engine_layer.h).
     */
    public static native int clusteredLightGridGetSliceCount(long grid, int[] outSlices);

    /**
     * cna_clustered_light_grid_get_tiles_x (engine_layer.h).
     */
    public static native int clusteredLightGridGetTilesX(long grid, int[] outTiles);

    /**
     * cna_clustered_light_grid_get_tiles_y (engine_layer.h).
     */
    public static native int clusteredLightGridGetTilesY(long grid, int[] outTiles);

    /**
     * cna_clustered_light_grid_has_projection (engine_layer.h).
     */
    public static native int clusteredLightGridHasProjection(long grid, boolean[] outHas);

    /**
     * cna_clustered_light_grid_set_projection (engine_layer.h).
     *
     * <p>projectionFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     */
    public static native int clusteredLightGridSetProjection(long grid, float[] projectionFloating, float nearPlane, float farPlane);

    /**
     * cna_clustered_light_grid_slice_distance (engine_layer.h).
     */
    public static native int clusteredLightGridSliceDistance(long grid, int slice, float[] outDistance);

    /**
     * cna_clustered_light_grid_slice_for_view_distance (engine_layer.h).
     */
    public static native int clusteredLightGridSliceForViewDistance(long grid, float viewDistance, int[] outSlice);

    /**
     * cna_clustered_light_set_add (engine_layer.h).
     *
     * <p>lightBytes carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>lightIntegral carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code type} (CNA_ClusteredLightType)</li>
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>lightFloating carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code direction.x} (float)</li>
     *   <li>{@code direction.y} (float)</li>
     *   <li>{@code direction.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     *   <li>{@code inner_angle} (float)</li>
     *   <li>{@code outer_angle} (float)</li>
     * </ol>
     */
    public static native int clusteredLightSetAdd(long set, byte[] lightBytes, long[] lightIntegral, float[] lightFloating, int[] outIndex);

    /**
     * cna_clustered_light_set_add_point (engine_layer.h).
     *
     * <p>lightBytes carries CNA_PointLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>lightIntegral carries CNA_PointLightEXT in this order:
     * <ol start="0">
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>lightFloating carries CNA_PointLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     * </ol>
     */
    public static native int clusteredLightSetAddPoint(long set, byte[] lightBytes, long[] lightIntegral, float[] lightFloating, int[] outIndex);

    /**
     * cna_clustered_light_set_add_spot (engine_layer.h).
     *
     * <p>lightBytes carries CNA_SpotLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>lightIntegral carries CNA_SpotLightEXT in this order:
     * <ol start="0">
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>lightFloating carries CNA_SpotLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code direction.x} (float)</li>
     *   <li>{@code direction.y} (float)</li>
     *   <li>{@code direction.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     *   <li>{@code inner_angle} (float)</li>
     *   <li>{@code outer_angle} (float)</li>
     * </ol>
     */
    public static native int clusteredLightSetAddSpot(long set, byte[] lightBytes, long[] lightIntegral, float[] lightFloating, int[] outIndex);

    /**
     * cna_clustered_light_set_clear (engine_layer.h).
     */
    public static native int clusteredLightSetClear(long set);

    /**
     * cna_clustered_light_set_copy_bounds (engine_layer.h).
     */
    public static native int clusteredLightSetCopyBounds(long set, float[] destinationFloating, long[] outCount);

    /**
     * cna_clustered_light_set_copy_lights (engine_layer.h).
     */
    public static native int clusteredLightSetCopyLights(long set, byte[] destinationBytes, long[] destinationIntegral, float[] destinationFloating, long[] outCount);

    /**
     * cna_clustered_light_set_create (engine_layer.h).
     */
    public static native int clusteredLightSetCreate(long game, long[] outSet);

    /**
     * cna_clustered_light_set_destroy (engine_layer.h).
     */
    public static native int clusteredLightSetDestroy(long set);

    /**
     * cna_clustered_light_set_get_at (engine_layer.h).
     *
     * <p>outLightBytes carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>outLightIntegral carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code type} (CNA_ClusteredLightType)</li>
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>outLightFloating carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code direction.x} (float)</li>
     *   <li>{@code direction.y} (float)</li>
     *   <li>{@code direction.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     *   <li>{@code inner_angle} (float)</li>
     *   <li>{@code outer_angle} (float)</li>
     * </ol>
     */
    public static native int clusteredLightSetGetAt(long set, int index, byte[] outLightBytes, long[] outLightIntegral, float[] outLightFloating);

    /**
     * cna_clustered_light_set_get_bounds_at (engine_layer.h).
     *
     * <p>outBoundsFloating carries CNA_BoundingSphere in this order:
     * <ol start="0">
     *   <li>{@code center.x} (float)</li>
     *   <li>{@code center.y} (float)</li>
     *   <li>{@code center.z} (float)</li>
     *   <li>{@code radius} (float)</li>
     * </ol>
     */
    public static native int clusteredLightSetGetBoundsAt(long set, int index, float[] outBoundsFloating);

    /**
     * cna_clustered_light_set_get_count (engine_layer.h).
     */
    public static native int clusteredLightSetGetCount(long set, int[] outCount);

    /**
     * cna_clustered_light_set_is_empty (engine_layer.h).
     */
    public static native int clusteredLightSetIsEmpty(long set, boolean[] outEmpty);

    /**
     * cna_clustered_light_set_is_usable (engine_layer.h).
     *
     * <p>lightBytes carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>lightIntegral carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code type} (CNA_ClusteredLightType)</li>
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>lightFloating carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code direction.x} (float)</li>
     *   <li>{@code direction.y} (float)</li>
     *   <li>{@code direction.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     *   <li>{@code inner_angle} (float)</li>
     *   <li>{@code outer_angle} (float)</li>
     * </ol>
     */
    public static native int clusteredLightSetIsUsable(byte[] lightBytes, long[] lightIntegral, float[] lightFloating, boolean[] outUsable);

    /**
     * cna_clustered_light_set_remove_at (engine_layer.h).
     */
    public static native int clusteredLightSetRemoveAt(long set, int index);

    /**
     * cna_clustered_light_set_replace_at (engine_layer.h).
     *
     * <p>lightBytes carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>lightIntegral carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code type} (CNA_ClusteredLightType)</li>
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>lightFloating carries CNA_ClusteredLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code direction.x} (float)</li>
     *   <li>{@code direction.y} (float)</li>
     *   <li>{@code direction.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     *   <li>{@code inner_angle} (float)</li>
     *   <li>{@code outer_angle} (float)</li>
     * </ol>
     */
    public static native int clusteredLightSetReplaceAt(long set, int index, byte[] lightBytes, long[] lightIntegral, float[] lightFloating);

    /**
     * cna_clustered_shadow_policy_copy_selected (engine_layer.h).
     */
    public static native int clusteredShadowPolicyCopySelected(long policy, int[] destination, long[] outCount);

    /**
     * cna_clustered_shadow_policy_create (engine_layer.h).
     */
    public static native int clusteredShadowPolicyCreate(long game, int budget, long[] outPolicy);

    /**
     * cna_clustered_shadow_policy_destroy (engine_layer.h).
     */
    public static native int clusteredShadowPolicyDestroy(long policy);

    /**
     * cna_clustered_shadow_policy_get_budget (engine_layer.h).
     */
    public static native int clusteredShadowPolicyGetBudget(long policy, int[] outBudget);

    /**
     * cna_clustered_shadow_policy_get_hysteresis (engine_layer.h).
     */
    public static native int clusteredShadowPolicyGetHysteresis(long policy, float[] outHysteresis);

    /**
     * cna_clustered_shadow_policy_get_refused_count (engine_layer.h).
     */
    public static native int clusteredShadowPolicyGetRefusedCount(long policy, int[] outCount);

    /**
     * cna_clustered_shadow_policy_get_request_count (engine_layer.h).
     */
    public static native int clusteredShadowPolicyGetRequestCount(long policy, int[] outCount);

    /**
     * cna_clustered_shadow_policy_get_score (engine_layer.h).
     */
    public static native int clusteredShadowPolicyGetScore(long policy, int lightIndex, float[] outScore);

    /**
     * cna_clustered_shadow_policy_is_selected (engine_layer.h).
     */
    public static native int clusteredShadowPolicyIsSelected(long policy, int lightIndex, boolean[] outSelected);

    /**
     * cna_clustered_shadow_policy_reset (engine_layer.h).
     */
    public static native int clusteredShadowPolicyReset(long policy);

    /**
     * cna_clustered_shadow_policy_select (engine_layer.h).
     *
     * <p>viewFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     *
     * <p>projectionFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     *
     * <p>cameraPositionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int clusteredShadowPolicySelect(long policy, long lights, float[] viewFloating, float[] projectionFloating, float[] cameraPositionFloating);

    /**
     * cna_clustered_shadow_policy_set_budget (engine_layer.h).
     */
    public static native int clusteredShadowPolicySetBudget(long policy, int budget);

    /**
     * cna_clustered_shadow_policy_set_hysteresis (engine_layer.h).
     */
    public static native int clusteredShadowPolicySetHysteresis(long policy, float hysteresis);

    /**
     * cna_contact_shadow_pass_combine_visibility (engine_layer.h).
     */
    public static native int contactShadowPassCombineVisibility(float shadowMapVisibility, float contactVisibility, float[] outVisibility);

    /**
     * cna_contact_shadow_pass_copy_fallback_reason (engine_layer.h).
     */
    public static native int contactShadowPassCopyFallbackReason(long pass, byte[] destination, long[] outBytes);

    /**
     * cna_contact_shadow_pass_copy_occlusion_test_glsl (engine_layer.h).
     */
    public static native int contactShadowPassCopyOcclusionTestGlsl(byte[] destination, long[] outBytes);

    /**
     * cna_contact_shadow_pass_create (engine_layer.h).
     */
    public static native int contactShadowPassCreate(long graphicsDevice, long[] outPass);

    /**
     * cna_contact_shadow_pass_get_bias (engine_layer.h).
     */
    public static native int contactShadowPassGetBias(long pass, float[] outBias);

    /**
     * cna_contact_shadow_pass_get_intensity (engine_layer.h).
     */
    public static native int contactShadowPassGetIntensity(long pass, float[] outIntensity);

    /**
     * cna_contact_shadow_pass_get_light_direction (engine_layer.h).
     *
     * <p>outDirectionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int contactShadowPassGetLightDirection(long pass, float[] outDirectionFloating);

    /**
     * cna_contact_shadow_pass_get_max_distance (engine_layer.h).
     */
    public static native int contactShadowPassGetMaxDistance(long pass, float[] outDistance);

    /**
     * cna_contact_shadow_pass_get_step_count (engine_layer.h).
     */
    public static native int contactShadowPassGetStepCount(long pass, int[] outCount);

    /**
     * cna_contact_shadow_pass_get_thickness (engine_layer.h).
     */
    public static native int contactShadowPassGetThickness(long pass, float[] outThickness);

    /**
     * cna_contact_shadow_pass_is_occluded (engine_layer.h).
     */
    public static native int contactShadowPassIsOccluded(float rayViewDepth, float sceneViewDepth, float bias, float thickness, boolean[] outOccluded);

    /**
     * cna_contact_shadow_pass_set_bias (engine_layer.h).
     */
    public static native int contactShadowPassSetBias(long pass, float bias);

    /**
     * cna_contact_shadow_pass_set_intensity (engine_layer.h).
     */
    public static native int contactShadowPassSetIntensity(long pass, float intensity);

    /**
     * cna_contact_shadow_pass_set_light_direction (engine_layer.h).
     *
     * <p>directionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int contactShadowPassSetLightDirection(long pass, float[] directionFloating);

    /**
     * cna_contact_shadow_pass_set_max_distance (engine_layer.h).
     */
    public static native int contactShadowPassSetMaxDistance(long pass, float distance);

    /**
     * cna_contact_shadow_pass_set_step_count (engine_layer.h).
     */
    public static native int contactShadowPassSetStepCount(long pass, int count);

    /**
     * cna_contact_shadow_pass_set_thickness (engine_layer.h).
     */
    public static native int contactShadowPassSetThickness(long pass, float thickness);

    /**
     * cna_cube_shadow_map_begin (engine_layer.h).
     */
    public static native int cubeShadowMapBegin(long shadowMap, int faceIndex);

    /**
     * cna_cube_shadow_map_compute_face_projection (engine_layer.h).
     *
     * <p>outMatrixFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     */
    public static native int cubeShadowMapComputeFaceProjection(float range, float[] outMatrixFloating);

    /**
     * cna_cube_shadow_map_compute_face_view (engine_layer.h).
     *
     * <p>positionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     *
     * <p>outMatrixFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     */
    public static native int cubeShadowMapComputeFaceView(int face, float[] positionFloating, float[] outMatrixFloating);

    /**
     * cna_cube_shadow_map_create (engine_layer.h).
     */
    public static native int cubeShadowMapCreate(long graphicsDevice, int quality, long[] outShadowMap);

    /**
     * cna_cube_shadow_map_destroy (engine_layer.h).
     */
    public static native int cubeShadowMapDestroy(long shadowMap);

    /**
     * cna_cube_shadow_map_end (engine_layer.h).
     */
    public static native int cubeShadowMapEnd(long shadowMap);

    /**
     * cna_cube_shadow_map_get_depth_bias (engine_layer.h).
     */
    public static native int cubeShadowMapGetDepthBias(long shadowMap, float[] outBias);

    /**
     * cna_cube_shadow_map_get_light_position (engine_layer.h).
     *
     * <p>outPositionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int cubeShadowMapGetLightPosition(long shadowMap, float[] outPositionFloating);

    /**
     * cna_cube_shadow_map_get_light_range (engine_layer.h).
     */
    public static native int cubeShadowMapGetLightRange(long shadowMap, float[] outRange);

    /**
     * cna_cube_shadow_map_get_quality (engine_layer.h).
     */
    public static native int cubeShadowMapGetQuality(long shadowMap, int[] outQuality);

    /**
     * cna_cube_shadow_map_get_shadow_texture (engine_layer.h).
     */
    public static native int cubeShadowMapGetShadowTexture(long shadowMap, long[] outTexture);

    /**
     * cna_cube_shadow_map_get_size (engine_layer.h).
     */
    public static native int cubeShadowMapGetSize(long shadowMap, int[] outSize);

    /**
     * cna_cube_shadow_map_is_supported (engine_layer.h).
     */
    public static native int cubeShadowMapIsSupported(long shadowMap, boolean[] outSupported);

    /**
     * cna_cube_shadow_map_set_depth_bias (engine_layer.h).
     */
    public static native int cubeShadowMapSetDepthBias(long shadowMap, float bias);

    /**
     * cna_cube_shadow_map_size_for_quality (engine_layer.h).
     */
    public static native int cubeShadowMapSizeForQuality(int quality, int[] outSize);

    /**
     * cna_cube_shadow_map_update (engine_layer.h).
     *
     * <p>lightBytes carries CNA_PointLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>lightIntegral carries CNA_PointLightEXT in this order:
     * <ol start="0">
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>lightFloating carries CNA_PointLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     * </ol>
     */
    public static native int cubeShadowMapUpdate(long shadowMap, byte[] lightBytes, long[] lightIntegral, float[] lightFloating);

    /**
     * cna_debug_draw_add_bounding_sphere (engine_layer.h).
     *
     * <p>sphereFloating carries CNA_BoundingSphere in this order:
     * <ol start="0">
     *   <li>{@code center.x} (float)</li>
     *   <li>{@code center.y} (float)</li>
     *   <li>{@code center.z} (float)</li>
     *   <li>{@code radius} (float)</li>
     * </ol>
     */
    public static native int debugDrawAddBoundingSphere(long debug, float[] sphereFloating, long[] colourIntegral, int segments);

    /**
     * cna_debug_draw_add_box (engine_layer.h).
     *
     * <p>boundsFloating carries CNA_BoundingBox in this order:
     * <ol start="0">
     *   <li>{@code min.x} (float)</li>
     *   <li>{@code min.y} (float)</li>
     *   <li>{@code min.z} (float)</li>
     *   <li>{@code max.x} (float)</li>
     *   <li>{@code max.y} (float)</li>
     *   <li>{@code max.z} (float)</li>
     * </ol>
     */
    public static native int debugDrawAddBox(long debug, float[] boundsFloating, long[] colourIntegral);

    /**
     * cna_debug_draw_add_cascade_gizmo (engine_layer.h).
     */
    public static native int debugDrawAddCascadeGizmo(long debug, long cascades, long[] colourIntegral);

    /**
     * cna_debug_draw_add_cluster_slice_gizmo (engine_layer.h).
     *
     * <p>inverseViewFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     */
    public static native int debugDrawAddClusterSliceGizmo(long debug, long grid, float[] inverseViewFloating, long[] colourIntegral);

    /**
     * cna_debug_draw_add_cross (engine_layer.h).
     *
     * <p>positionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int debugDrawAddCross(long debug, float[] positionFloating, float size, long[] colourIntegral);

    /**
     * cna_debug_draw_add_directional_light_gizmo (engine_layer.h).
     *
     * <p>lightBytes carries CNA_DirectionalLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>lightIntegral carries CNA_DirectionalLightEXT in this order:
     * <ol start="0">
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>lightFloating carries CNA_DirectionalLightEXT in this order:
     * <ol start="0">
     *   <li>{@code direction.x} (float)</li>
     *   <li>{@code direction.y} (float)</li>
     *   <li>{@code direction.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     * </ol>
     *
     * <p>atFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int debugDrawAddDirectionalLightGizmo(long debug, byte[] lightBytes, long[] lightIntegral, float[] lightFloating, float[] atFloating, float length, long[] colourIntegral);

    /**
     * cna_debug_draw_add_frustum (engine_layer.h).
     */
    public static native int debugDrawAddFrustum(long debug, float[] frustumFloating, long[] colourIntegral);

    /**
     * cna_debug_draw_add_line (engine_layer.h).
     *
     * <p>fromFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     *
     * <p>toFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int debugDrawAddLine(long debug, float[] fromFloating, float[] toFloating, long[] colourIntegral);

    /**
     * cna_debug_draw_add_point_light_gizmo (engine_layer.h).
     *
     * <p>lightBytes carries CNA_PointLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>lightIntegral carries CNA_PointLightEXT in this order:
     * <ol start="0">
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>lightFloating carries CNA_PointLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     * </ol>
     */
    public static native int debugDrawAddPointLightGizmo(long debug, byte[] lightBytes, long[] lightIntegral, float[] lightFloating, long[] colourIntegral);

    /**
     * cna_debug_draw_add_probe_volume_gizmo (engine_layer.h).
     */
    public static native int debugDrawAddProbeVolumeGizmo(long debug, long volume, long[] colourIntegral, float crossSize);

    /**
     * cna_debug_draw_add_sphere (engine_layer.h).
     *
     * <p>centreFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int debugDrawAddSphere(long debug, float[] centreFloating, float radius, long[] colourIntegral, int segments);

    /**
     * cna_debug_draw_add_spot_light_gizmo (engine_layer.h).
     *
     * <p>lightBytes carries CNA_SpotLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>lightIntegral carries CNA_SpotLightEXT in this order:
     * <ol start="0">
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>lightFloating carries CNA_SpotLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code direction.x} (float)</li>
     *   <li>{@code direction.y} (float)</li>
     *   <li>{@code direction.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     *   <li>{@code inner_angle} (float)</li>
     *   <li>{@code outer_angle} (float)</li>
     * </ol>
     */
    public static native int debugDrawAddSpotLightGizmo(long debug, byte[] lightBytes, long[] lightIntegral, float[] lightFloating, long[] colourIntegral, int segments);

    /**
     * cna_debug_draw_begin (engine_layer.h).
     *
     * <p>viewFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     *
     * <p>projectionFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     */
    public static native int debugDrawBegin(long debug, float[] viewFloating, float[] projectionFloating);

    /**
     * cna_debug_draw_clear (engine_layer.h).
     */
    public static native int debugDrawClear(long debug);

    /**
     * cna_debug_draw_copy_vertices (engine_layer.h).
     */
    public static native int debugDrawCopyVertices(long debug, boolean depthTested, long[] destinationIntegral, float[] destinationFloating, long[] outCount);

    /**
     * cna_debug_draw_create (engine_layer.h).
     */
    public static native int debugDrawCreate(long graphicsDevice, long[] outDebug);

    /**
     * cna_debug_draw_destroy (engine_layer.h).
     */
    public static native int debugDrawDestroy(long debug);

    /**
     * cna_debug_draw_end (engine_layer.h).
     */
    public static native int debugDrawEnd(long debug);

    /**
     * cna_debug_draw_get_line_count (engine_layer.h).
     */
    public static native int debugDrawGetLineCount(long debug, int[] outCount);

    /**
     * cna_debug_draw_is_depth_tested (engine_layer.h).
     */
    public static native int debugDrawIsDepthTested(long debug, boolean[] outDepthTested);

    /**
     * cna_debug_draw_set_depth_tested (engine_layer.h).
     */
    public static native int debugDrawSetDepthTested(long debug, boolean depthTested);

    /**
     * cna_decal_pass_create (engine_layer.h).
     */
    public static native int decalPassCreate(long graphicsDevice, long[] outPass);

    /**
     * cna_decal_pass_destroy (engine_layer.h).
     */
    public static native int decalPassDestroy(long pass);

    /**
     * cna_decal_pass_draw (engine_layer.h).
     *
     * <p>decalWorldFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     */
    public static native int decalPassDraw(long pass, long decal, float[] decalWorldFloating, int width, int height);

    /**
     * cna_decal_pass_get_max_slope_angle (engine_layer.h).
     */
    public static native int decalPassGetMaxSlopeAngle(long pass, float[] outValue);

    /**
     * cna_decal_pass_get_opacity (engine_layer.h).
     */
    public static native int decalPassGetOpacity(long pass, float[] outValue);

    /**
     * cna_decal_pass_get_tint (engine_layer.h).
     *
     * <p>outValueFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int decalPassGetTint(long pass, float[] outValueFloating);

    /**
     * cna_decal_pass_is_inside_decal_box (engine_layer.h).
     *
     * <p>decalLocalPositionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int decalPassIsInsideDecalBox(float[] decalLocalPositionFloating, boolean[] outInside);

    /**
     * cna_decal_pass_set_camera (engine_layer.h).
     *
     * <p>viewFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     *
     * <p>projectionFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     */
    public static native int decalPassSetCamera(long pass, float[] viewFloating, float[] projectionFloating, float farPlane);

    /**
     * cna_decal_pass_set_max_slope_angle (engine_layer.h).
     */
    public static native int decalPassSetMaxSlopeAngle(long pass, float value);

    /**
     * cna_decal_pass_set_opacity (engine_layer.h).
     */
    public static native int decalPassSetOpacity(long pass, float value);

    /**
     * cna_decal_pass_set_prepass_inputs (engine_layer.h).
     */
    public static native int decalPassSetPrepassInputs(long pass, long depth, long normals);

    /**
     * cna_decal_pass_set_tint (engine_layer.h).
     *
     * <p>valueFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int decalPassSetTint(long pass, float[] valueFloating);

    /**
     * cna_depth_normal_prepass_begin (engine_layer.h).
     *
     * <p>viewFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     *
     * <p>projectionFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     */
    public static native int depthNormalPrepassBegin(long prepass, int passIndex, float[] viewFloating, float[] projectionFloating, float nearPlane, float farPlane);

    /**
     * cna_depth_normal_prepass_copy_depth_decode_glsl (engine_layer.h).
     */
    public static native int depthNormalPrepassCopyDepthDecodeGlsl(boolean packed, byte[] destination, long[] outBytes);

    /**
     * cna_depth_normal_prepass_copy_velocity_decode_glsl (engine_layer.h).
     */
    public static native int depthNormalPrepassCopyVelocityDecodeGlsl(byte[] destination, long[] outBytes);

    /**
     * cna_depth_normal_prepass_create (engine_layer.h).
     */
    public static native int depthNormalPrepassCreate(long graphicsDevice, int width, int height, int encoding, long[] outPrepass);

    /**
     * cna_depth_normal_prepass_decode_velocity_ext (engine_layer.h).
     *
     * <p>outVelocityFloating carries CNA_Vector2 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     * </ol>
     */
    public static native int depthNormalPrepassDecodeVelocityExt(long[] texelIntegral, float[] outVelocityFloating);

    /**
     * cna_depth_normal_prepass_destroy (engine_layer.h).
     */
    public static native int depthNormalPrepassDestroy(long prepass);

    /**
     * cna_depth_normal_prepass_end (engine_layer.h).
     */
    public static native int depthNormalPrepassEnd(long prepass);

    /**
     * cna_depth_normal_prepass_get_depth_texture (engine_layer.h).
     */
    public static native int depthNormalPrepassGetDepthTexture(long prepass, long[] outTexture);

    /**
     * cna_depth_normal_prepass_get_normal_texture (engine_layer.h).
     */
    public static native int depthNormalPrepassGetNormalTexture(long prepass, long[] outTexture);

    /**
     * cna_depth_normal_prepass_get_pass_count (engine_layer.h).
     */
    public static native int depthNormalPrepassGetPassCount(long prepass, int[] outCount);

    /**
     * cna_depth_normal_prepass_get_roughness (engine_layer.h).
     */
    public static native int depthNormalPrepassGetRoughness(long prepass, float[] outRoughness);

    /**
     * cna_depth_normal_prepass_get_velocity_texture_ext (engine_layer.h).
     */
    public static native int depthNormalPrepassGetVelocityTextureExt(long prepass, long[] outTexture);

    /**
     * cna_depth_normal_prepass_has_velocity_ext (engine_layer.h).
     */
    public static native int depthNormalPrepassHasVelocityExt(long[] texelIntegral, boolean[] outHas);

    /**
     * cna_depth_normal_prepass_is_depth_packed (engine_layer.h).
     */
    public static native int depthNormalPrepassIsDepthPacked(long prepass, boolean[] outPacked);

    /**
     * cna_depth_normal_prepass_is_supported (engine_layer.h).
     */
    public static native int depthNormalPrepassIsSupported(long prepass, long graphicsDevice, boolean[] outSupported);

    /**
     * cna_depth_normal_prepass_is_using_multiple_render_targets (engine_layer.h).
     */
    public static native int depthNormalPrepassIsUsingMultipleRenderTargets(long prepass, boolean[] outUsing);

    /**
     * cna_depth_normal_prepass_is_velocity_enabled_ext (engine_layer.h).
     */
    public static native int depthNormalPrepassIsVelocityEnabledExt(long prepass, boolean[] outEnabled);

    /**
     * cna_depth_normal_prepass_pack_depth (engine_layer.h).
     */
    public static native int depthNormalPrepassPackDepth(float value, float[] outR, float[] outG, float[] outB, float[] outA);

    /**
     * cna_depth_normal_prepass_resize (engine_layer.h).
     */
    public static native int depthNormalPrepassResize(long prepass, int width, int height);

    /**
     * cna_depth_normal_prepass_set_previous_camera_ext (engine_layer.h).
     *
     * <p>previousViewFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     *
     * <p>previousProjectionFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     */
    public static native int depthNormalPrepassSetPreviousCameraExt(long prepass, float[] previousViewFloating, float[] previousProjectionFloating);

    /**
     * cna_depth_normal_prepass_set_previous_world_ext (engine_layer.h).
     *
     * <p>previousWorldFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     */
    public static native int depthNormalPrepassSetPreviousWorldExt(long prepass, float[] previousWorldFloating);

    /**
     * cna_depth_normal_prepass_set_roughness (engine_layer.h).
     */
    public static native int depthNormalPrepassSetRoughness(long prepass, float roughness);

    /**
     * cna_depth_normal_prepass_set_velocity_enabled_ext (engine_layer.h).
     */
    public static native int depthNormalPrepassSetVelocityEnabledExt(long prepass, boolean enabled);

    /**
     * cna_depth_normal_prepass_unpack_depth (engine_layer.h).
     */
    public static native int depthNormalPrepassUnpackDepth(float r, float g, float b, float a, float[] outValue);

    /**
     * cna_depth_normal_prepass_uses_packed_depth_ext (engine_layer.h).
     */
    public static native int depthNormalPrepassUsesPackedDepthExt(long graphicsDevice, boolean[] outPacked);

    /**
     * cna_depth_of_field_pass_circle_of_confusion_millimetres (engine_layer.h).
     */
    public static native int depthOfFieldPassCircleOfConfusionMillimetres(float depth, float focusDistance, float focalLength, float fNumber, float[] outMillimetres);

    /**
     * cna_depth_of_field_pass_create (engine_layer.h).
     */
    public static native int depthOfFieldPassCreate(long graphicsDevice, long[] outPass);

    /**
     * cna_depth_of_field_pass_get_f_number (engine_layer.h).
     */
    public static native int depthOfFieldPassGetFNumber(long pass, float[] outValue);

    /**
     * cna_depth_of_field_pass_get_focal_length (engine_layer.h).
     */
    public static native int depthOfFieldPassGetFocalLength(long pass, float[] outValue);

    /**
     * cna_depth_of_field_pass_get_focus_distance (engine_layer.h).
     */
    public static native int depthOfFieldPassGetFocusDistance(long pass, float[] outValue);

    /**
     * cna_depth_of_field_pass_get_max_radius (engine_layer.h).
     */
    public static native int depthOfFieldPassGetMaxRadius(long pass, float[] outValue);

    /**
     * cna_depth_of_field_pass_set_f_number (engine_layer.h).
     */
    public static native int depthOfFieldPassSetFNumber(long pass, float value);

    /**
     * cna_depth_of_field_pass_set_focal_length (engine_layer.h).
     */
    public static native int depthOfFieldPassSetFocalLength(long pass, float value);

    /**
     * cna_depth_of_field_pass_set_focus_distance (engine_layer.h).
     */
    public static native int depthOfFieldPassSetFocusDistance(long pass, float value);

    /**
     * cna_depth_of_field_pass_set_max_radius (engine_layer.h).
     */
    public static native int depthOfFieldPassSetMaxRadius(long pass, float value);

    /**
     * cna_directional_light_ext_init (engine_layer.h).
     *
     * <p>outLightBytes carries CNA_DirectionalLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>outLightIntegral carries CNA_DirectionalLightEXT in this order:
     * <ol start="0">
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>outLightFloating carries CNA_DirectionalLightEXT in this order:
     * <ol start="0">
     *   <li>{@code direction.x} (float)</li>
     *   <li>{@code direction.y} (float)</li>
     *   <li>{@code direction.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     * </ol>
     */
    public static native int directionalLightExtInit(byte[] outLightBytes, long[] outLightIntegral, float[] outLightFloating);

    /**
     * cna_engine_layer_copy_version_string (engine_layer.h).
     */
    public static native int engineLayerCopyVersionString(byte[] destination, long[] outBytes);

    /**
     * cna_engine_layer_get_version (engine_layer.h).
     */
    public static native int engineLayerGetVersion(int[] outVersion);

    /**
     * cna_environment_processor_convert_equirectangular (engine_layer.h).
     */
    public static native int environmentProcessorConvertEquirectangular(long processor, long panorama, int faceSize, long[] outEnvironment);

    /**
     * cna_environment_processor_create (engine_layer.h).
     */
    public static native int environmentProcessorCreate(long graphicsDevice, long[] outProcessor);

    /**
     * cna_environment_processor_destroy (engine_layer.h).
     */
    public static native int environmentProcessorDestroy(long processor);

    /**
     * cna_environment_processor_direction_to_equirectangular (engine_layer.h).
     *
     * <p>directionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int environmentProcessorDirectionToEquirectangular(float[] directionFloating, float[] outU, float[] outV);

    /**
     * cna_environment_processor_face_direction (engine_layer.h).
     *
     * <p>outDirectionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int environmentProcessorFaceDirection(int face, float u, float v, float[] outDirectionFloating);

    /**
     * cna_environment_processor_generate_brdf_lut (engine_layer.h).
     */
    public static native int environmentProcessorGenerateBrdfLut(long processor, int size, int sampleCount, long[] outLut);

    /**
     * cna_environment_processor_generate_irradiance (engine_layer.h).
     */
    public static native int environmentProcessorGenerateIrradiance(long processor, long environment, int size, int sampleCount, long[] outIrradiance);

    /**
     * cna_environment_processor_generate_prefiltered_specular (engine_layer.h).
     */
    public static native int environmentProcessorGeneratePrefilteredSpecular(long processor, long environment, int baseSize, int mipCount, int sampleCount, long[] outSpecular);

    /**
     * cna_environment_processor_generate_probe (engine_layer.h).
     *
     * <p>positionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int environmentProcessorGenerateProbe(long processor, long environment, float[] positionFloating, long[] outProbe);

    /**
     * cna_environment_processor_hammersley (engine_layer.h).
     */
    public static native int environmentProcessorHammersley(int index, int count, float[] outX, float[] outY);

    /**
     * cna_environment_processor_importance_sample_ggx (engine_layer.h).
     *
     * <p>normalFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     *
     * <p>outDirectionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int environmentProcessorImportanceSampleGgx(float x, float y, float[] normalFloating, float roughness, float[] outDirectionFloating);

    /**
     * cna_environment_processor_mip_for_roughness (engine_layer.h).
     */
    public static native int environmentProcessorMipForRoughness(float roughness, int mipCount, float[] outMip);

    /**
     * cna_environment_processor_roughness_for_mip (engine_layer.h).
     */
    public static native int environmentProcessorRoughnessForMip(float mip, int mipCount, float[] outRoughness);

    /**
     * cna_film_grain_pass_create (engine_layer.h).
     */
    public static native int filmGrainPassCreate(long graphicsDevice, long[] outPass);

    /**
     * cna_film_grain_pass_get_intensity (engine_layer.h).
     */
    public static native int filmGrainPassGetIntensity(long pass, float[] outValue);

    /**
     * cna_film_grain_pass_set_intensity (engine_layer.h).
     */
    public static native int filmGrainPassSetIntensity(long pass, float value);

    /**
     * cna_frustum_culler_ext_create (engine_layer.h).
     */
    public static native int frustumCullerExtCreate(long[] outCuller);

    /**
     * cna_frustum_culler_ext_cull_boxes (engine_layer.h).
     */
    public static native int frustumCullerExtCullBoxes(long culler, float[] boundsFloating, long[] destination, long[] outCount);

    /**
     * cna_frustum_culler_ext_cull_spheres (engine_layer.h).
     */
    public static native int frustumCullerExtCullSpheres(long culler, float[] boundsFloating, long[] destination, long[] outCount);

    /**
     * cna_frustum_culler_ext_cull_transforms (engine_layer.h).
     */
    public static native int frustumCullerExtCullTransforms(long culler, float[] transformsFloating, float[] boundsFloating, float[] destinationFloating, long[] outCount);

    /**
     * cna_frustum_culler_ext_destroy (engine_layer.h).
     */
    public static native int frustumCullerExtDestroy(long culler);

    /**
     * cna_frustum_culler_ext_get_frustum (engine_layer.h).
     *
     * <p>outFrustumFloating carries CNA_BoundingFrustum in this order:
     * <ol start="0">
     *   <li>{@code matrix.m11} (float)</li>
     *   <li>{@code matrix.m12} (float)</li>
     *   <li>{@code matrix.m13} (float)</li>
     *   <li>{@code matrix.m14} (float)</li>
     *   <li>{@code matrix.m21} (float)</li>
     *   <li>{@code matrix.m22} (float)</li>
     *   <li>{@code matrix.m23} (float)</li>
     *   <li>{@code matrix.m24} (float)</li>
     *   <li>{@code matrix.m31} (float)</li>
     *   <li>{@code matrix.m32} (float)</li>
     *   <li>{@code matrix.m33} (float)</li>
     *   <li>{@code matrix.m34} (float)</li>
     *   <li>{@code matrix.m41} (float)</li>
     *   <li>{@code matrix.m42} (float)</li>
     *   <li>{@code matrix.m43} (float)</li>
     *   <li>{@code matrix.m44} (float)</li>
     * </ol>
     */
    public static native int frustumCullerExtGetFrustum(long culler, float[] outFrustumFloating);

    /**
     * cna_frustum_culler_ext_is_box_visible (engine_layer.h).
     *
     * <p>boxFloating carries CNA_BoundingBox in this order:
     * <ol start="0">
     *   <li>{@code min.x} (float)</li>
     *   <li>{@code min.y} (float)</li>
     *   <li>{@code min.z} (float)</li>
     *   <li>{@code max.x} (float)</li>
     *   <li>{@code max.y} (float)</li>
     *   <li>{@code max.z} (float)</li>
     * </ol>
     */
    public static native int frustumCullerExtIsBoxVisible(long culler, float[] boxFloating, boolean[] outVisible);

    /**
     * cna_frustum_culler_ext_is_sphere_visible (engine_layer.h).
     *
     * <p>sphereFloating carries CNA_BoundingSphere in this order:
     * <ol start="0">
     *   <li>{@code center.x} (float)</li>
     *   <li>{@code center.y} (float)</li>
     *   <li>{@code center.z} (float)</li>
     *   <li>{@code radius} (float)</li>
     * </ol>
     */
    public static native int frustumCullerExtIsSphereVisible(long culler, float[] sphereFloating, boolean[] outVisible);

    /**
     * cna_frustum_culler_ext_set_camera (engine_layer.h).
     *
     * <p>viewFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     *
     * <p>projectionFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     */
    public static native int frustumCullerExtSetCamera(long culler, float[] viewFloating, float[] projectionFloating);

    /**
     * cna_frustum_culler_ext_set_view_projection (engine_layer.h).
     *
     * <p>viewProjectionFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     */
    public static native int frustumCullerExtSetViewProjection(long culler, float[] viewProjectionFloating);

    /**
     * cna_fxaa_pass_copy_fragment_glsl (engine_layer.h).
     */
    public static native int fxaaPassCopyFragmentGlsl(byte[] destination, long[] outBytes);

    /**
     * cna_fxaa_pass_create (engine_layer.h).
     */
    public static native int fxaaPassCreate(long graphicsDevice, long[] outPass);

    /**
     * cna_fxaa_pass_edge_threshold_for_quality (engine_layer.h).
     */
    public static native int fxaaPassEdgeThresholdForQuality(int quality, float[] outThreshold);

    /**
     * cna_fxaa_pass_get_edge_threshold (engine_layer.h).
     */
    public static native int fxaaPassGetEdgeThreshold(long pass, float[] outValue);

    /**
     * cna_fxaa_pass_set_edge_threshold (engine_layer.h).
     */
    public static native int fxaaPassSetEdgeThreshold(long pass, float value);

    /**
     * cna_gpu_cullable_instance_init (engine_layer.h).
     *
     * <p>outInstanceFloating carries CNA_GpuCullableInstance in this order:
     * <ol start="0">
     *   <li>{@code world.m11} (float)</li>
     *   <li>{@code world.m12} (float)</li>
     *   <li>{@code world.m13} (float)</li>
     *   <li>{@code world.m14} (float)</li>
     *   <li>{@code world.m21} (float)</li>
     *   <li>{@code world.m22} (float)</li>
     *   <li>{@code world.m23} (float)</li>
     *   <li>{@code world.m24} (float)</li>
     *   <li>{@code world.m31} (float)</li>
     *   <li>{@code world.m32} (float)</li>
     *   <li>{@code world.m33} (float)</li>
     *   <li>{@code world.m34} (float)</li>
     *   <li>{@code world.m41} (float)</li>
     *   <li>{@code world.m42} (float)</li>
     *   <li>{@code world.m43} (float)</li>
     *   <li>{@code world.m44} (float)</li>
     *   <li>{@code bounds.min.x} (float)</li>
     *   <li>{@code bounds.min.y} (float)</li>
     *   <li>{@code bounds.min.z} (float)</li>
     *   <li>{@code bounds.max.x} (float)</li>
     *   <li>{@code bounds.max.y} (float)</li>
     *   <li>{@code bounds.max.z} (float)</li>
     * </ol>
     */
    public static native int gpuCullableInstanceInit(float[] outInstanceFloating);

    /**
     * cna_gpu_instance_culler_copy_instance_lookup_glsl (engine_layer.h).
     */
    public static native int gpuInstanceCullerCopyInstanceLookupGlsl(byte[] destination, long[] outBytes);

    /**
     * cna_gpu_instance_culler_copy_unsupported_reason (engine_layer.h).
     */
    public static native int gpuInstanceCullerCopyUnsupportedReason(long culler, byte[] destination, long[] outBytes);

    /**
     * cna_gpu_instance_culler_create (engine_layer.h).
     */
    public static native int gpuInstanceCullerCreate(long graphicsDevice, long[] outCuller);

    /**
     * cna_gpu_instance_culler_cull (engine_layer.h).
     *
     * <p>viewFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     *
     * <p>projectionFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     */
    public static native int gpuInstanceCullerCull(long culler, float[] viewFloating, float[] projectionFloating, int indexCount, int firstIndex, int baseVertex);

    /**
     * cna_gpu_instance_culler_destroy (engine_layer.h).
     */
    public static native int gpuInstanceCullerDestroy(long culler);

    /**
     * cna_gpu_instance_culler_draw (engine_layer.h).
     */
    public static native int gpuInstanceCullerDraw(long culler, int primitiveType);

    /**
     * cna_gpu_instance_culler_get_instance_count (engine_layer.h).
     */
    public static native int gpuInstanceCullerGetInstanceCount(long culler, int[] outCount);

    /**
     * cna_gpu_instance_culler_is_supported (engine_layer.h).
     */
    public static native int gpuInstanceCullerIsSupported(long culler, boolean[] outSupported);

    /**
     * cna_gpu_instance_culler_read_visible_count_ext (engine_layer.h).
     */
    public static native int gpuInstanceCullerReadVisibleCountExt(long culler, int[] outCount);

    /**
     * cna_gpu_instance_culler_set_instances (engine_layer.h).
     */
    public static native int gpuInstanceCullerSetInstances(long culler, float[] instancesFloating);

    /**
     * cna_gpu_timer_begin (engine_layer.h).
     */
    public static native int gpuTimerBegin(long timer);

    /**
     * cna_gpu_timer_copy_unsupported_reason (engine_layer.h).
     */
    public static native int gpuTimerCopyUnsupportedReason(long timer, byte[] destination, long[] outBytes);

    /**
     * cna_gpu_timer_create (engine_layer.h).
     */
    public static native int gpuTimerCreate(long graphicsDevice, long[] outTimer);

    /**
     * cna_gpu_timer_destroy (engine_layer.h).
     */
    public static native int gpuTimerDestroy(long timer);

    /**
     * cna_gpu_timer_end (engine_layer.h).
     */
    public static native int gpuTimerEnd(long timer);

    /**
     * cna_gpu_timer_get_last_milliseconds (engine_layer.h).
     */
    public static native int gpuTimerGetLastMilliseconds(long timer, double[] outMilliseconds);

    /**
     * cna_gpu_timer_get_sample_count (engine_layer.h).
     */
    public static native int gpuTimerGetSampleCount(long timer, int[] outSampleCount);

    /**
     * cna_gpu_timer_is_open (engine_layer.h).
     */
    public static native int gpuTimerIsOpen(long timer, boolean[] outOpen);

    /**
     * cna_gpu_timer_is_result_available (engine_layer.h).
     */
    public static native int gpuTimerIsResultAvailable(long timer, boolean[] outAvailable);

    /**
     * cna_gpu_timer_is_supported (engine_layer.h).
     */
    public static native int gpuTimerIsSupported(long timer, boolean[] outSupported);

    /**
     * cna_gpu_timer_poll (engine_layer.h).
     */
    public static native int gpuTimerPoll(long timer, boolean[] outCollected);

    /**
     * cna_graphics_device_supports_shadow_sampling_ext (engine_layer.h).
     */
    public static native int graphicsDeviceSupportsShadowSamplingExt(long graphicsDevice, boolean[] outSupported);

    /**
     * cna_hdr_display_output_create (engine_layer.h).
     */
    public static native int hdrDisplayOutputCreate(long graphicsDevice, long[] outOutput);

    /**
     * cna_hdr_display_output_decode_pq (engine_layer.h).
     */
    public static native int hdrDisplayOutputDecodePq(float encoded, float[] outNits);

    /**
     * cna_hdr_display_output_destroy (engine_layer.h).
     */
    public static native int hdrDisplayOutputDestroy(long output);

    /**
     * cna_hdr_display_output_draw (engine_layer.h).
     */
    public static native int hdrDisplayOutputDraw(long output, long source, long destination, int width, int height);

    /**
     * cna_hdr_display_output_encode (engine_layer.h).
     *
     * <p>sceneLinearFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     *
     * <p>outColorFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int hdrDisplayOutputEncode(int space, float[] sceneLinearFloating, float paperWhiteNits, float peakNits, float[] outColorFloating);

    /**
     * cna_hdr_display_output_encode_pq (engine_layer.h).
     */
    public static native int hdrDisplayOutputEncodePq(float nits, float[] outEncoded);

    /**
     * cna_hdr_display_output_get_color_space (engine_layer.h).
     */
    public static native int hdrDisplayOutputGetColorSpace(long output, int[] outSpace);

    /**
     * cna_hdr_display_output_get_paper_white_nits (engine_layer.h).
     */
    public static native int hdrDisplayOutputGetPaperWhiteNits(long output, float[] outNits);

    /**
     * cna_hdr_display_output_get_peak_nits (engine_layer.h).
     */
    public static native int hdrDisplayOutputGetPeakNits(long output, float[] outNits);

    /**
     * cna_hdr_display_output_is_supported (engine_layer.h).
     */
    public static native int hdrDisplayOutputIsSupported(long output, boolean[] outSupported);

    /**
     * cna_hdr_display_output_rec709_to_rec2020 (engine_layer.h).
     *
     * <p>colorFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     *
     * <p>outColorFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int hdrDisplayOutputRec709ToRec2020(float[] colorFloating, float[] outColorFloating);

    /**
     * cna_hdr_display_output_roll_off (engine_layer.h).
     */
    public static native int hdrDisplayOutputRollOff(float nits, float peakNits, float[] outNits);

    /**
     * cna_hdr_display_output_set_color_space (engine_layer.h).
     */
    public static native int hdrDisplayOutputSetColorSpace(long output, int value);

    /**
     * cna_hdr_display_output_set_paper_white_nits (engine_layer.h).
     */
    public static native int hdrDisplayOutputSetPaperWhiteNits(long output, float value);

    /**
     * cna_hdr_display_output_set_peak_nits (engine_layer.h).
     */
    public static native int hdrDisplayOutputSetPeakNits(long output, float value);

    /**
     * cna_height_fog_pass_create (engine_layer.h).
     */
    public static native int heightFogPassCreate(long graphicsDevice, long[] outPass);

    /**
     * cna_height_fog_pass_get_base_height (engine_layer.h).
     */
    public static native int heightFogPassGetBaseHeight(long pass, float[] outValue);

    /**
     * cna_height_fog_pass_get_color (engine_layer.h).
     *
     * <p>outValueFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int heightFogPassGetColor(long pass, float[] outValueFloating);

    /**
     * cna_height_fog_pass_get_density (engine_layer.h).
     */
    public static native int heightFogPassGetDensity(long pass, float[] outValue);

    /**
     * cna_height_fog_pass_get_falloff (engine_layer.h).
     */
    public static native int heightFogPassGetFalloff(long pass, float[] outValue);

    /**
     * cna_height_fog_pass_optical_depth (engine_layer.h).
     */
    public static native int heightFogPassOpticalDepth(float cameraHeight, float rayHeightStep, float distance, float density, float falloff, float baseHeight, float[] outDepth);

    /**
     * cna_height_fog_pass_set_base_height (engine_layer.h).
     */
    public static native int heightFogPassSetBaseHeight(long pass, float value);

    /**
     * cna_height_fog_pass_set_color (engine_layer.h).
     *
     * <p>valueFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int heightFogPassSetColor(long pass, float[] valueFloating);

    /**
     * cna_height_fog_pass_set_density (engine_layer.h).
     */
    public static native int heightFogPassSetDensity(long pass, float value);

    /**
     * cna_height_fog_pass_set_falloff (engine_layer.h).
     */
    public static native int heightFogPassSetFalloff(long pass, float value);

    /**
     * cna_instanced_renderer_ext_copy_instance_elements (engine_layer.h).
     */
    public static native int instancedRendererExtCopyInstanceElements(long[] destinationIntegral, long[] outElementCount);

    /**
     * cna_instanced_renderer_ext_copy_tint_elements (engine_layer.h).
     */
    public static native int instancedRendererExtCopyTintElements(long[] destinationIntegral, long[] outElementCount);

    /**
     * cna_instanced_renderer_ext_create (engine_layer.h).
     */
    public static native int instancedRendererExtCreate(long graphicsDevice, long part, long[] outRenderer);

    /**
     * cna_instanced_renderer_ext_destroy (engine_layer.h).
     */
    public static native int instancedRendererExtDestroy(long renderer);

    /**
     * cna_instanced_renderer_ext_did_last_draw_instance (engine_layer.h).
     */
    public static native int instancedRendererExtDidLastDrawInstance(long renderer, boolean[] outInstanced);

    /**
     * cna_instanced_renderer_ext_draw (engine_layer.h).
     */
    public static native int instancedRendererExtDraw(long renderer, long effect);

    /**
     * cna_instanced_renderer_ext_get_instance_capacity (engine_layer.h).
     */
    public static native int instancedRendererExtGetInstanceCapacity(long renderer, int[] outCapacity);

    /**
     * cna_instanced_renderer_ext_get_instance_count (engine_layer.h).
     */
    public static native int instancedRendererExtGetInstanceCount(long renderer, int[] outCount);

    /**
     * cna_instanced_renderer_ext_get_instance_stride (engine_layer.h).
     */
    public static native int instancedRendererExtGetInstanceStride(int[] outStride);

    /**
     * cna_instanced_renderer_ext_get_last_draw_call_count (engine_layer.h).
     */
    public static native int instancedRendererExtGetLastDrawCallCount(long renderer, int[] outCount);

    /**
     * cna_instanced_renderer_ext_get_tint_stride (engine_layer.h).
     */
    public static native int instancedRendererExtGetTintStride(int[] outStride);

    /**
     * cna_instanced_renderer_ext_is_fallback_enabled (engine_layer.h).
     */
    public static native int instancedRendererExtIsFallbackEnabled(long renderer, boolean[] outEnabled);

    /**
     * cna_instanced_renderer_ext_is_instancing_supported (engine_layer.h).
     */
    public static native int instancedRendererExtIsInstancingSupported(long renderer, boolean[] outSupported);

    /**
     * cna_instanced_renderer_ext_is_tints_enabled (engine_layer.h).
     */
    public static native int instancedRendererExtIsTintsEnabled(long renderer, boolean[] outEnabled);

    /**
     * cna_instanced_renderer_ext_set_fallback_enabled (engine_layer.h).
     */
    public static native int instancedRendererExtSetFallbackEnabled(long renderer, boolean enabled);

    /**
     * cna_instanced_renderer_ext_set_instance_tints (engine_layer.h).
     */
    public static native int instancedRendererExtSetInstanceTints(long renderer, long[] tintsIntegral);

    /**
     * cna_instanced_renderer_ext_set_instances (engine_layer.h).
     */
    public static native int instancedRendererExtSetInstances(long renderer, float[] transformsFloating);

    /**
     * cna_instanced_renderer_ext_set_tints_enabled (engine_layer.h).
     */
    public static native int instancedRendererExtSetTintsEnabled(long renderer, boolean enabled);

    /**
     * cna_lens_flare_pass_create (engine_layer.h).
     */
    public static native int lensFlarePassCreate(long graphicsDevice, long[] outPass);

    /**
     * cna_lens_flare_pass_get_dispersal (engine_layer.h).
     */
    public static native int lensFlarePassGetDispersal(long pass, float[] outValue);

    /**
     * cna_lens_flare_pass_get_intensity (engine_layer.h).
     */
    public static native int lensFlarePassGetIntensity(long pass, float[] outValue);

    /**
     * cna_lens_flare_pass_get_threshold (engine_layer.h).
     */
    public static native int lensFlarePassGetThreshold(long pass, float[] outValue);

    /**
     * cna_lens_flare_pass_set_dispersal (engine_layer.h).
     */
    public static native int lensFlarePassSetDispersal(long pass, float value);

    /**
     * cna_lens_flare_pass_set_intensity (engine_layer.h).
     */
    public static native int lensFlarePassSetIntensity(long pass, float value);

    /**
     * cna_lens_flare_pass_set_threshold (engine_layer.h).
     */
    public static native int lensFlarePassSetThreshold(long pass, float value);

    /**
     * cna_light_probe_ext_copy_coefficients (engine_layer.h).
     */
    public static native int lightProbeExtCopyCoefficients(long probe, float[] destinationFloating, long[] outCount);

    /**
     * cna_light_probe_ext_copy_evaluation_glsl (engine_layer.h).
     */
    public static native int lightProbeExtCopyEvaluationGlsl(byte[] destination, long[] outBytes);

    /**
     * cna_light_probe_ext_copy_from (engine_layer.h).
     */
    public static native int lightProbeExtCopyFrom(long destination, long source);

    /**
     * cna_light_probe_ext_create (engine_layer.h).
     */
    public static native int lightProbeExtCreate(long[] outProbe);

    /**
     * cna_light_probe_ext_create_at (engine_layer.h).
     *
     * <p>positionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int lightProbeExtCreateAt(float[] positionFloating, long[] outProbe);

    /**
     * cna_light_probe_ext_destroy (engine_layer.h).
     */
    public static native int lightProbeExtDestroy(long probe);

    /**
     * cna_light_probe_ext_equals (engine_layer.h).
     */
    public static native int lightProbeExtEquals(long first, long second, boolean[] outEqual);

    /**
     * cna_light_probe_ext_get_coefficient (engine_layer.h).
     *
     * <p>outValueFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int lightProbeExtGetCoefficient(long probe, int index, float[] outValueFloating);

    /**
     * cna_light_probe_ext_get_position (engine_layer.h).
     *
     * <p>outPositionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int lightProbeExtGetPosition(long probe, float[] outPositionFloating);

    /**
     * cna_light_probe_ext_get_visibility_mean (engine_layer.h).
     */
    public static native int lightProbeExtGetVisibilityMean(long probe, int direction, float[] outValue);

    /**
     * cna_light_probe_ext_get_visibility_mean_squared (engine_layer.h).
     */
    public static native int lightProbeExtGetVisibilityMeanSquared(long probe, int direction, float[] outValue);

    /**
     * cna_light_probe_ext_has_visibility (engine_layer.h).
     */
    public static native int lightProbeExtHasVisibility(long probe, boolean[] outHas);

    /**
     * cna_light_probe_ext_irradiance (engine_layer.h).
     *
     * <p>normalFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     *
     * <p>outIrradianceFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int lightProbeExtIrradiance(long probe, float[] normalFloating, float[] outIrradianceFloating);

    /**
     * cna_light_probe_ext_is_zero (engine_layer.h).
     */
    public static native int lightProbeExtIsZero(long probe, boolean[] outZero);

    /**
     * cna_light_probe_ext_scale (engine_layer.h).
     */
    public static native int lightProbeExtScale(long probe, float factor);

    /**
     * cna_light_probe_ext_set_coefficient (engine_layer.h).
     *
     * <p>valueFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int lightProbeExtSetCoefficient(long probe, int index, float[] valueFloating);

    /**
     * cna_light_probe_ext_set_position (engine_layer.h).
     *
     * <p>positionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int lightProbeExtSetPosition(long probe, float[] positionFloating);

    /**
     * cna_light_probe_ext_set_visibility (engine_layer.h).
     */
    public static native int lightProbeExtSetVisibility(long probe, int direction, float meanDistance, float meanSquaredDistance);

    /**
     * cna_light_probe_ext_visibility_weight (engine_layer.h).
     *
     * <p>directionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int lightProbeExtVisibilityWeight(long probe, float[] directionFloating, float distance, float[] outWeight);

    /**
     * cna_light_probe_volume_ext_contains (engine_layer.h).
     *
     * <p>positionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int lightProbeVolumeExtContains(long volume, float[] positionFloating, boolean[] outContains);

    /**
     * cna_light_probe_volume_ext_create (engine_layer.h).
     *
     * <p>boundsFloating carries CNA_BoundingBox in this order:
     * <ol start="0">
     *   <li>{@code min.x} (float)</li>
     *   <li>{@code min.y} (float)</li>
     *   <li>{@code min.z} (float)</li>
     *   <li>{@code max.x} (float)</li>
     *   <li>{@code max.y} (float)</li>
     *   <li>{@code max.z} (float)</li>
     * </ol>
     */
    public static native int lightProbeVolumeExtCreate(float[] boundsFloating, int countX, int countY, int countZ, long[] outVolume);

    /**
     * cna_light_probe_volume_ext_destroy (engine_layer.h).
     */
    public static native int lightProbeVolumeExtDestroy(long volume);

    /**
     * cna_light_probe_volume_ext_get_bounds (engine_layer.h).
     *
     * <p>outBoundsFloating carries CNA_BoundingBox in this order:
     * <ol start="0">
     *   <li>{@code min.x} (float)</li>
     *   <li>{@code min.y} (float)</li>
     *   <li>{@code min.z} (float)</li>
     *   <li>{@code max.x} (float)</li>
     *   <li>{@code max.y} (float)</li>
     *   <li>{@code max.z} (float)</li>
     * </ol>
     */
    public static native int lightProbeVolumeExtGetBounds(long volume, float[] outBoundsFloating);

    /**
     * cna_light_probe_volume_ext_get_count_x (engine_layer.h).
     */
    public static native int lightProbeVolumeExtGetCountX(long volume, int[] outCount);

    /**
     * cna_light_probe_volume_ext_get_count_y (engine_layer.h).
     */
    public static native int lightProbeVolumeExtGetCountY(long volume, int[] outCount);

    /**
     * cna_light_probe_volume_ext_get_count_z (engine_layer.h).
     */
    public static native int lightProbeVolumeExtGetCountZ(long volume, int[] outCount);

    /**
     * cna_light_probe_volume_ext_get_probe (engine_layer.h).
     */
    public static native int lightProbeVolumeExtGetProbe(long volume, int x, int y, int z, long outProbe);

    /**
     * cna_light_probe_volume_ext_get_probe_count (engine_layer.h).
     */
    public static native int lightProbeVolumeExtGetProbeCount(long volume, int[] outCount);

    /**
     * cna_light_probe_volume_ext_get_probe_position (engine_layer.h).
     *
     * <p>outPositionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int lightProbeVolumeExtGetProbePosition(long volume, int x, int y, int z, float[] outPositionFloating);

    /**
     * cna_light_probe_volume_ext_irradiance (engine_layer.h).
     *
     * <p>positionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     *
     * <p>normalFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     *
     * <p>outIrradianceFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int lightProbeVolumeExtIrradiance(long volume, float[] positionFloating, float[] normalFloating, float[] outIrradianceFloating);

    /**
     * cna_light_probe_volume_ext_is_zero (engine_layer.h).
     */
    public static native int lightProbeVolumeExtIsZero(long volume, boolean[] outZero);

    /**
     * cna_light_probe_volume_ext_sample_probe (engine_layer.h).
     *
     * <p>positionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int lightProbeVolumeExtSampleProbe(long volume, float[] positionFloating, long outProbe);

    /**
     * cna_light_probe_volume_ext_set_probe (engine_layer.h).
     */
    public static native int lightProbeVolumeExtSetProbe(long volume, int x, int y, int z, long probe);

    /**
     * cna_light_shaft_pass_create (engine_layer.h).
     */
    public static native int lightShaftPassCreate(long graphicsDevice, long[] outPass);

    /**
     * cna_light_shaft_pass_get_decay (engine_layer.h).
     */
    public static native int lightShaftPassGetDecay(long pass, float[] outValue);

    /**
     * cna_light_shaft_pass_get_intensity (engine_layer.h).
     */
    public static native int lightShaftPassGetIntensity(long pass, float[] outValue);

    /**
     * cna_light_shaft_pass_get_light_screen_position (engine_layer.h).
     *
     * <p>outValueFloating carries CNA_Vector2 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     * </ol>
     */
    public static native int lightShaftPassGetLightScreenPosition(long pass, float[] outValueFloating);

    /**
     * cna_light_shaft_pass_get_threshold (engine_layer.h).
     */
    public static native int lightShaftPassGetThreshold(long pass, float[] outValue);

    /**
     * cna_light_shaft_pass_set_decay (engine_layer.h).
     */
    public static native int lightShaftPassSetDecay(long pass, float value);

    /**
     * cna_light_shaft_pass_set_intensity (engine_layer.h).
     */
    public static native int lightShaftPassSetIntensity(long pass, float value);

    /**
     * cna_light_shaft_pass_set_light_screen_position (engine_layer.h).
     *
     * <p>valueFloating carries CNA_Vector2 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     * </ol>
     */
    public static native int lightShaftPassSetLightScreenPosition(long pass, float[] valueFloating);

    /**
     * cna_light_shaft_pass_set_threshold (engine_layer.h).
     */
    public static native int lightShaftPassSetThreshold(long pass, float value);

    /**
     * cna_lod_group_ext_add_level (engine_layer.h).
     */
    public static native int lodGroupExtAddLevel(long group, float maxDistance, long part);

    /**
     * cna_lod_group_ext_clear (engine_layer.h).
     */
    public static native int lodGroupExtClear(long group);

    /**
     * cna_lod_group_ext_copy_levels (engine_layer.h).
     */
    public static native int lodGroupExtCopyLevels(long group, long[] destinationIntegral, float[] destinationFloating, long[] outCount);

    /**
     * cna_lod_group_ext_create (engine_layer.h).
     */
    public static native int lodGroupExtCreate(long[] outGroup);

    /**
     * cna_lod_group_ext_destroy (engine_layer.h).
     */
    public static native int lodGroupExtDestroy(long group);

    /**
     * cna_lod_group_ext_get_hysteresis (engine_layer.h).
     */
    public static native int lodGroupExtGetHysteresis(long group, float[] outMargin);

    /**
     * cna_lod_group_ext_get_selection_mode (engine_layer.h).
     */
    public static native int lodGroupExtGetSelectionMode(long group, int[] outMode);

    /**
     * cna_lod_group_ext_projected_radius_pixels (engine_layer.h).
     */
    public static native int lodGroupExtProjectedRadiusPixels(long group, float distance, float[] outPixels);

    /**
     * cna_lod_group_ext_reset_hysteresis (engine_layer.h).
     */
    public static native int lodGroupExtResetHysteresis(long group);

    /**
     * cna_lod_group_ext_select_index (engine_layer.h).
     */
    public static native int lodGroupExtSelectIndex(long group, float distance, int[] outIndex);

    /**
     * cna_lod_group_ext_set_hysteresis (engine_layer.h).
     */
    public static native int lodGroupExtSetHysteresis(long group, float margin);

    /**
     * cna_lod_group_ext_set_screen_space_parameters (engine_layer.h).
     */
    public static native int lodGroupExtSetScreenSpaceParameters(long group, float radius, float verticalFov, float viewportHeight);

    /**
     * cna_lod_group_ext_set_selection_mode (engine_layer.h).
     */
    public static native int lodGroupExtSetSelectionMode(long group, int mode);

    /**
     * cna_motion_blur_pass_create (engine_layer.h).
     */
    public static native int motionBlurPassCreate(long graphicsDevice, long[] outPass);

    /**
     * cna_motion_blur_pass_get_max_distance (engine_layer.h).
     */
    public static native int motionBlurPassGetMaxDistance(long pass, float[] outValue);

    /**
     * cna_motion_blur_pass_get_strength (engine_layer.h).
     */
    public static native int motionBlurPassGetStrength(long pass, float[] outValue);

    /**
     * cna_motion_blur_pass_set_max_distance (engine_layer.h).
     */
    public static native int motionBlurPassSetMaxDistance(long pass, float value);

    /**
     * cna_motion_blur_pass_set_strength (engine_layer.h).
     */
    public static native int motionBlurPassSetStrength(long pass, float value);

    /**
     * cna_particle_emitter_settings_init (engine_layer.h).
     *
     * <p>outSettingsFloating carries CNA_ParticleEmitterSettings in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code direction.x} (float)</li>
     *   <li>{@code direction.y} (float)</li>
     *   <li>{@code direction.z} (float)</li>
     *   <li>{@code gravity.x} (float)</li>
     *   <li>{@code gravity.y} (float)</li>
     *   <li>{@code gravity.z} (float)</li>
     *   <li>{@code start_color.x} (float)</li>
     *   <li>{@code start_color.y} (float)</li>
     *   <li>{@code start_color.z} (float)</li>
     *   <li>{@code start_color.w} (float)</li>
     *   <li>{@code end_color.x} (float)</li>
     *   <li>{@code end_color.y} (float)</li>
     *   <li>{@code end_color.z} (float)</li>
     *   <li>{@code end_color.w} (float)</li>
     *   <li>{@code cone_angle} (float)</li>
     *   <li>{@code speed} (float)</li>
     *   <li>{@code speed_variance} (float)</li>
     *   <li>{@code lifetime} (float)</li>
     *   <li>{@code lifetime_variance} (float)</li>
     *   <li>{@code drag} (float)</li>
     *   <li>{@code emission_rate} (float)</li>
     *   <li>{@code start_size} (float)</li>
     *   <li>{@code end_size} (float)</li>
     * </ol>
     */
    public static native int particleEmitterSettingsInit(float[] outSettingsFloating);

    /**
     * cna_particle_init (engine_layer.h).
     *
     * <p>outParticleFloating carries CNA_Particle in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code position.w} (float)</li>
     *   <li>{@code velocity.x} (float)</li>
     *   <li>{@code velocity.y} (float)</li>
     *   <li>{@code velocity.z} (float)</li>
     *   <li>{@code velocity.w} (float)</li>
     *   <li>{@code state.x} (float)</li>
     *   <li>{@code state.y} (float)</li>
     *   <li>{@code state.z} (float)</li>
     *   <li>{@code state.w} (float)</li>
     * </ol>
     */
    public static native int particleInit(float[] outParticleFloating);

    /**
     * cna_particle_system_copy_particle_lookup_glsl (engine_layer.h).
     */
    public static native int particleSystemCopyParticleLookupGlsl(byte[] destination, long[] outBytes);

    /**
     * cna_particle_system_copy_particles_ext (engine_layer.h).
     */
    public static native int particleSystemCopyParticlesExt(long system, float[] destinationFloating, long[] outCount);

    /**
     * cna_particle_system_copy_unsupported_reason (engine_layer.h).
     */
    public static native int particleSystemCopyUnsupportedReason(long system, byte[] destination, long[] outBytes);

    /**
     * cna_particle_system_create (engine_layer.h).
     */
    public static native int particleSystemCreate(long graphicsDevice, long[] outSystem);

    /**
     * cna_particle_system_create_with_capacity (engine_layer.h).
     */
    public static native int particleSystemCreateWithCapacity(long graphicsDevice, int capacity, long[] outSystem);

    /**
     * cna_particle_system_destroy (engine_layer.h).
     */
    public static native int particleSystemDestroy(long system);

    /**
     * cna_particle_system_draw (engine_layer.h).
     *
     * <p>viewFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     *
     * <p>projectionFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     */
    public static native int particleSystemDraw(long system, float[] viewFloating, float[] projectionFloating, long texture);

    /**
     * cna_particle_system_get_active_count (engine_layer.h).
     */
    public static native int particleSystemGetActiveCount(long system, int[] outCount);

    /**
     * cna_particle_system_get_capacity (engine_layer.h).
     */
    public static native int particleSystemGetCapacity(long system, int[] outCapacity);

    /**
     * cna_particle_system_get_settings (engine_layer.h).
     *
     * <p>outSettingsFloating carries CNA_ParticleEmitterSettings in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code direction.x} (float)</li>
     *   <li>{@code direction.y} (float)</li>
     *   <li>{@code direction.z} (float)</li>
     *   <li>{@code gravity.x} (float)</li>
     *   <li>{@code gravity.y} (float)</li>
     *   <li>{@code gravity.z} (float)</li>
     *   <li>{@code start_color.x} (float)</li>
     *   <li>{@code start_color.y} (float)</li>
     *   <li>{@code start_color.z} (float)</li>
     *   <li>{@code start_color.w} (float)</li>
     *   <li>{@code end_color.x} (float)</li>
     *   <li>{@code end_color.y} (float)</li>
     *   <li>{@code end_color.z} (float)</li>
     *   <li>{@code end_color.w} (float)</li>
     *   <li>{@code cone_angle} (float)</li>
     *   <li>{@code speed} (float)</li>
     *   <li>{@code speed_variance} (float)</li>
     *   <li>{@code lifetime} (float)</li>
     *   <li>{@code lifetime_variance} (float)</li>
     *   <li>{@code drag} (float)</li>
     *   <li>{@code emission_rate} (float)</li>
     *   <li>{@code start_size} (float)</li>
     *   <li>{@code end_size} (float)</li>
     * </ol>
     */
    public static native int particleSystemGetSettings(long system, float[] outSettingsFloating);

    /**
     * cna_particle_system_get_softness_ext (engine_layer.h).
     */
    public static native int particleSystemGetSoftnessExt(long system, float[] outSoftness);

    /**
     * cna_particle_system_is_emission_rate_clamped (engine_layer.h).
     */
    public static native int particleSystemIsEmissionRateClamped(long system, boolean[] outClamped);

    /**
     * cna_particle_system_is_simulation_on_cpu_ext (engine_layer.h).
     */
    public static native int particleSystemIsSimulationOnCpuExt(long system, boolean[] outForced);

    /**
     * cna_particle_system_random (engine_layer.h).
     */
    public static native int particleSystemRandom(int seed, float[] outValue);

    /**
     * cna_particle_system_reset (engine_layer.h).
     */
    public static native int particleSystemReset(long system);

    /**
     * cna_particle_system_set_depth_input_ext (engine_layer.h).
     */
    public static native int particleSystemSetDepthInputExt(long system, long depth, float farPlane);

    /**
     * cna_particle_system_set_settings (engine_layer.h).
     *
     * <p>settingsFloating carries CNA_ParticleEmitterSettings in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code direction.x} (float)</li>
     *   <li>{@code direction.y} (float)</li>
     *   <li>{@code direction.z} (float)</li>
     *   <li>{@code gravity.x} (float)</li>
     *   <li>{@code gravity.y} (float)</li>
     *   <li>{@code gravity.z} (float)</li>
     *   <li>{@code start_color.x} (float)</li>
     *   <li>{@code start_color.y} (float)</li>
     *   <li>{@code start_color.z} (float)</li>
     *   <li>{@code start_color.w} (float)</li>
     *   <li>{@code end_color.x} (float)</li>
     *   <li>{@code end_color.y} (float)</li>
     *   <li>{@code end_color.z} (float)</li>
     *   <li>{@code end_color.w} (float)</li>
     *   <li>{@code cone_angle} (float)</li>
     *   <li>{@code speed} (float)</li>
     *   <li>{@code speed_variance} (float)</li>
     *   <li>{@code lifetime} (float)</li>
     *   <li>{@code lifetime_variance} (float)</li>
     *   <li>{@code drag} (float)</li>
     *   <li>{@code emission_rate} (float)</li>
     *   <li>{@code start_size} (float)</li>
     *   <li>{@code end_size} (float)</li>
     * </ol>
     */
    public static native int particleSystemSetSettings(long system, float[] settingsFloating);

    /**
     * cna_particle_system_set_simulation_on_cpu_ext (engine_layer.h).
     */
    public static native int particleSystemSetSimulationOnCpuExt(long system, boolean forced);

    /**
     * cna_particle_system_set_softness_ext (engine_layer.h).
     */
    public static native int particleSystemSetSoftnessExt(long system, float softness);

    /**
     * cna_particle_system_step (engine_layer.h).
     *
     * <p>particleFloating carries CNA_Particle in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code position.w} (float)</li>
     *   <li>{@code velocity.x} (float)</li>
     *   <li>{@code velocity.y} (float)</li>
     *   <li>{@code velocity.z} (float)</li>
     *   <li>{@code velocity.w} (float)</li>
     *   <li>{@code state.x} (float)</li>
     *   <li>{@code state.y} (float)</li>
     *   <li>{@code state.z} (float)</li>
     *   <li>{@code state.w} (float)</li>
     * </ol>
     *
     * <p>settingsFloating carries CNA_ParticleEmitterSettings in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code direction.x} (float)</li>
     *   <li>{@code direction.y} (float)</li>
     *   <li>{@code direction.z} (float)</li>
     *   <li>{@code gravity.x} (float)</li>
     *   <li>{@code gravity.y} (float)</li>
     *   <li>{@code gravity.z} (float)</li>
     *   <li>{@code start_color.x} (float)</li>
     *   <li>{@code start_color.y} (float)</li>
     *   <li>{@code start_color.z} (float)</li>
     *   <li>{@code start_color.w} (float)</li>
     *   <li>{@code end_color.x} (float)</li>
     *   <li>{@code end_color.y} (float)</li>
     *   <li>{@code end_color.z} (float)</li>
     *   <li>{@code end_color.w} (float)</li>
     *   <li>{@code cone_angle} (float)</li>
     *   <li>{@code speed} (float)</li>
     *   <li>{@code speed_variance} (float)</li>
     *   <li>{@code lifetime} (float)</li>
     *   <li>{@code lifetime_variance} (float)</li>
     *   <li>{@code drag} (float)</li>
     *   <li>{@code emission_rate} (float)</li>
     *   <li>{@code start_size} (float)</li>
     *   <li>{@code end_size} (float)</li>
     * </ol>
     */
    public static native int particleSystemStep(float[] particleFloating, int index, float[] settingsFloating, float elapsedSeconds);

    /**
     * cna_particle_system_update (engine_layer.h).
     */
    public static native int particleSystemUpdate(long system, float elapsedSeconds);

    /**
     * cna_particle_system_uses_compute (engine_layer.h).
     */
    public static native int particleSystemUsesCompute(long system, boolean[] outUsesCompute);

    /**
     * cna_pbr_material_extensions_copy_from (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsCopyFrom(long destination, long source);

    /**
     * cna_pbr_material_extensions_copy_to_string (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsCopyToString(long extensions, byte[] destination, long[] outBytes);

    /**
     * cna_pbr_material_extensions_create (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsCreate(long[] outExtensions);

    /**
     * cna_pbr_material_extensions_destroy (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsDestroy(long extensions);

    /**
     * cna_pbr_material_extensions_equals (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsEquals(long first, long second, boolean[] outEqual);

    /**
     * cna_pbr_material_extensions_get_attenuation_color (engine_layer.h).
     *
     * <p>outValueFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int pbrMaterialExtensionsGetAttenuationColor(long extensions, float[] outValueFloating);

    /**
     * cna_pbr_material_extensions_get_attenuation_distance (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsGetAttenuationDistance(long extensions, float[] outValue);

    /**
     * cna_pbr_material_extensions_get_clearcoat_factor (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsGetClearcoatFactor(long extensions, float[] outValue);

    /**
     * cna_pbr_material_extensions_get_clearcoat_normal_scale (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsGetClearcoatNormalScale(long extensions, float[] outValue);

    /**
     * cna_pbr_material_extensions_get_clearcoat_roughness (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsGetClearcoatRoughness(long extensions, float[] outValue);

    /**
     * cna_pbr_material_extensions_get_hash_code (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsGetHashCode(long extensions, long[] outHash);

    /**
     * cna_pbr_material_extensions_get_iridescence_factor (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsGetIridescenceFactor(long extensions, float[] outValue);

    /**
     * cna_pbr_material_extensions_get_iridescence_ior (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsGetIridescenceIor(long extensions, float[] outValue);

    /**
     * cna_pbr_material_extensions_get_iridescence_thickness_maximum (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsGetIridescenceThicknessMaximum(long extensions, float[] outValue);

    /**
     * cna_pbr_material_extensions_get_iridescence_thickness_minimum (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsGetIridescenceThicknessMinimum(long extensions, float[] outValue);

    /**
     * cna_pbr_material_extensions_get_sheen_color_factor (engine_layer.h).
     *
     * <p>outValueFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int pbrMaterialExtensionsGetSheenColorFactor(long extensions, float[] outValueFloating);

    /**
     * cna_pbr_material_extensions_get_sheen_roughness (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsGetSheenRoughness(long extensions, float[] outValue);

    /**
     * cna_pbr_material_extensions_get_subsurface_color (engine_layer.h).
     *
     * <p>outValueFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int pbrMaterialExtensionsGetSubsurfaceColor(long extensions, float[] outValueFloating);

    /**
     * cna_pbr_material_extensions_get_subsurface_wrap (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsGetSubsurfaceWrap(long extensions, float[] outValue);

    /**
     * cna_pbr_material_extensions_get_thickness_factor (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsGetThicknessFactor(long extensions, float[] outValue);

    /**
     * cna_pbr_material_extensions_get_transmission_factor (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsGetTransmissionFactor(long extensions, float[] outValue);

    /**
     * cna_pbr_material_extensions_is_iridescence_enabled (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsIsIridescenceEnabled(long extensions, boolean[] outValue);

    /**
     * cna_pbr_material_extensions_is_neutral (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsIsNeutral(long extensions, boolean[] outValue);

    /**
     * cna_pbr_material_extensions_is_sheen_enabled (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsIsSheenEnabled(long extensions, boolean[] outValue);

    /**
     * cna_pbr_material_extensions_is_subsurface_enabled (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsIsSubsurfaceEnabled(long extensions, boolean[] outValue);

    /**
     * cna_pbr_material_extensions_is_transmission_enabled (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsIsTransmissionEnabled(long extensions, boolean[] outValue);

    /**
     * cna_pbr_material_extensions_set_attenuation_color (engine_layer.h).
     *
     * <p>valueFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int pbrMaterialExtensionsSetAttenuationColor(long extensions, float[] valueFloating);

    /**
     * cna_pbr_material_extensions_set_attenuation_distance (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsSetAttenuationDistance(long extensions, float value);

    /**
     * cna_pbr_material_extensions_set_clearcoat_factor (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsSetClearcoatFactor(long extensions, float value);

    /**
     * cna_pbr_material_extensions_set_clearcoat_normal_scale (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsSetClearcoatNormalScale(long extensions, float value);

    /**
     * cna_pbr_material_extensions_set_clearcoat_normal_texture (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsSetClearcoatNormalTexture(long extensions, long texture);

    /**
     * cna_pbr_material_extensions_set_clearcoat_roughness (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsSetClearcoatRoughness(long extensions, float value);

    /**
     * cna_pbr_material_extensions_set_clearcoat_roughness_texture (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsSetClearcoatRoughnessTexture(long extensions, long texture);

    /**
     * cna_pbr_material_extensions_set_clearcoat_texture (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsSetClearcoatTexture(long extensions, long texture);

    /**
     * cna_pbr_material_extensions_set_iridescence_factor (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsSetIridescenceFactor(long extensions, float value);

    /**
     * cna_pbr_material_extensions_set_iridescence_ior (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsSetIridescenceIor(long extensions, float value);

    /**
     * cna_pbr_material_extensions_set_iridescence_texture (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsSetIridescenceTexture(long extensions, long texture);

    /**
     * cna_pbr_material_extensions_set_iridescence_thickness_maximum (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsSetIridescenceThicknessMaximum(long extensions, float value);

    /**
     * cna_pbr_material_extensions_set_iridescence_thickness_minimum (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsSetIridescenceThicknessMinimum(long extensions, float value);

    /**
     * cna_pbr_material_extensions_set_iridescence_thickness_texture (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsSetIridescenceThicknessTexture(long extensions, long texture);

    /**
     * cna_pbr_material_extensions_set_sheen_color_factor (engine_layer.h).
     *
     * <p>valueFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int pbrMaterialExtensionsSetSheenColorFactor(long extensions, float[] valueFloating);

    /**
     * cna_pbr_material_extensions_set_sheen_color_texture (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsSetSheenColorTexture(long extensions, long texture);

    /**
     * cna_pbr_material_extensions_set_sheen_roughness (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsSetSheenRoughness(long extensions, float value);

    /**
     * cna_pbr_material_extensions_set_sheen_roughness_texture (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsSetSheenRoughnessTexture(long extensions, long texture);

    /**
     * cna_pbr_material_extensions_set_subsurface_color (engine_layer.h).
     *
     * <p>valueFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int pbrMaterialExtensionsSetSubsurfaceColor(long extensions, float[] valueFloating);

    /**
     * cna_pbr_material_extensions_set_subsurface_wrap (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsSetSubsurfaceWrap(long extensions, float value);

    /**
     * cna_pbr_material_extensions_set_thickness_factor (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsSetThicknessFactor(long extensions, float value);

    /**
     * cna_pbr_material_extensions_set_thickness_texture (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsSetThicknessTexture(long extensions, long texture);

    /**
     * cna_pbr_material_extensions_set_transmission_factor (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsSetTransmissionFactor(long extensions, float value);

    /**
     * cna_pbr_material_extensions_set_transmission_texture (engine_layer.h).
     */
    public static native int pbrMaterialExtensionsSetTransmissionTexture(long extensions, long texture);

    /**
     * cna_point_light_ext_init (engine_layer.h).
     *
     * <p>outLightBytes carries CNA_PointLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>outLightIntegral carries CNA_PointLightEXT in this order:
     * <ol start="0">
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>outLightFloating carries CNA_PointLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     * </ol>
     */
    public static native int pointLightExtInit(byte[] outLightBytes, long[] outLightIntegral, float[] outLightFloating);

    /**
     * cna_post_process_pass_copy_name (engine_layer.h).
     */
    public static native int postProcessPassCopyName(long pass, byte[] destination, long[] outBytes);

    /**
     * cna_post_process_pass_destroy (engine_layer.h).
     */
    public static native int postProcessPassDestroy(long pass);

    /**
     * cna_post_process_pass_is_supported (engine_layer.h).
     */
    public static native int postProcessPassIsSupported(long pass, long graphicsDevice, boolean[] outSupported);

    /**
     * cna_render_pipeline_add_user_pass (engine_layer.h).
     */
    public static native int renderPipelineAddUserPass(long pipeline, long pass);

    /**
     * cna_render_pipeline_begin (engine_layer.h).
     *
     * <p>clearColorIntegral carries CNA_Color in this order:
     * <ol start="0">
     *   <li>{@code r} (uint8_t)</li>
     *   <li>{@code g} (uint8_t)</li>
     *   <li>{@code b} (uint8_t)</li>
     *   <li>{@code a} (uint8_t)</li>
     * </ol>
     */
    public static native int renderPipelineBegin(long pipeline, long[] clearColorIntegral);

    /**
     * cna_render_pipeline_clear_user_passes (engine_layer.h).
     */
    public static native int renderPipelineClearUserPasses(long pipeline);

    /**
     * cna_render_pipeline_copy_pass_timing_name_ext (engine_layer.h).
     */
    public static native int renderPipelineCopyPassTimingNameExt(long pipeline, long index, byte[] destination, long[] outBytes);

    /**
     * cna_render_pipeline_copy_transparency_fallback_reason_ext (engine_layer.h).
     */
    public static native int renderPipelineCopyTransparencyFallbackReasonExt(long pipeline, byte[] destination, long[] outBytes);

    /**
     * cna_render_pipeline_create (engine_layer.h).
     */
    public static native int renderPipelineCreate(long graphicsDevice, long[] outPipeline);

    /**
     * cna_render_pipeline_destroy (engine_layer.h).
     */
    public static native int renderPipelineDestroy(long pipeline);

    /**
     * cna_render_pipeline_did_shadow_pass_run (engine_layer.h).
     */
    public static native int renderPipelineDidShadowPassRun(long pipeline, boolean[] outRan);

    /**
     * cna_render_pipeline_did_skybox_draw (engine_layer.h).
     */
    public static native int renderPipelineDidSkyboxDraw(long pipeline, boolean[] outDrew);

    /**
     * cna_render_pipeline_end (engine_layer.h).
     */
    public static native int renderPipelineEnd(long pipeline);

    /**
     * cna_render_pipeline_get_gpu_memory_estimate_bytes (engine_layer.h).
     */
    public static native int renderPipelineGetGpuMemoryEstimateBytes(long pipeline, long[] outBytes);

    /**
     * cna_render_pipeline_get_last_frame_pass_count (engine_layer.h).
     */
    public static native int renderPipelineGetLastFramePassCount(long pipeline, int[] outCount);

    /**
     * cna_render_pipeline_get_pass_timing_count_ext (engine_layer.h).
     */
    public static native int renderPipelineGetPassTimingCountExt(long pipeline, long[] outCount);

    /**
     * cna_render_pipeline_get_pass_timing_ext (engine_layer.h).
     *
     * <p>outTimingBytes carries CNA_PassTimingEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     *   <li>{@code reserved[3]} (uint8_t)</li>
     * </ol>
     *
     * <p>outTimingIntegral carries CNA_PassTimingEXT in this order:
     * <ol start="0">
     *   <li>{@code sample_count} (int32_t)</li>
     * </ol>
     *
     * <p>outTimingDoubles carries CNA_PassTimingEXT in this order:
     * <ol start="0">
     *   <li>{@code milliseconds} (double)</li>
     * </ol>
     */
    public static native int renderPipelineGetPassTimingExt(long pipeline, long index, byte[] outTimingBytes, long[] outTimingIntegral, double[] outTimingDoubles);

    /**
     * cna_render_pipeline_get_scene_target_format (engine_layer.h).
     */
    public static native int renderPipelineGetSceneTargetFormat(long pipeline, int[] outFormat);

    /**
     * cna_render_pipeline_get_settings (engine_layer.h).
     *
     * <p>outSettingsBytes carries CNA_RenderPipelineSettingsEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     *   <li>{@code reserved[3]} (uint8_t)</li>
     * </ol>
     *
     * <p>outSettingsIntegral carries CNA_RenderPipelineSettingsEXT in this order:
     * <ol start="0">
     *   <li>{@code hdr_enabled} (CNA_Bool)</li>
     *   <li>{@code tonemapping_mode} (CNA_TonemappingMode)</li>
     *   <li>{@code bloom_enabled} (CNA_Bool)</li>
     *   <li>{@code bloom_iterations} (int32_t)</li>
     *   <li>{@code ssao_enabled} (CNA_Bool)</li>
     *   <li>{@code transparency_mode} (CNA_TransparencyMode)</li>
     *   <li>{@code ssao_sample_count} (int32_t)</li>
     *   <li>{@code ssr_enabled} (CNA_Bool)</li>
     *   <li>{@code ssr_step_count} (int32_t)</li>
     *   <li>{@code color_grade_enabled} (CNA_Bool)</li>
     *   <li>{@code dof_enabled} (CNA_Bool)</li>
     *   <li>{@code fxaa_enabled} (CNA_Bool)</li>
     *   <li>{@code render_quality} (CNA_RenderQuality)</li>
     *   <li>{@code shadow_quality} (CNA_ShadowQuality)</li>
     *   <li>{@code shadows_enabled} (CNA_Bool)</li>
     * </ol>
     *
     * <p>outSettingsFloating carries CNA_RenderPipelineSettingsEXT in this order:
     * <ol start="0">
     *   <li>{@code exposure} (float)</li>
     *   <li>{@code gamma} (float)</li>
     *   <li>{@code bloom_intensity} (float)</li>
     *   <li>{@code bloom_threshold} (float)</li>
     *   <li>{@code ssao_radius} (float)</li>
     *   <li>{@code ssao_intensity} (float)</li>
     *   <li>{@code ssr_max_distance} (float)</li>
     *   <li>{@code ssr_thickness} (float)</li>
     *   <li>{@code ssr_depth_bias} (float)</li>
     *   <li>{@code ssr_edge_fade} (float)</li>
     *   <li>{@code volumetric_fog_density} (float)</li>
     *   <li>{@code light_shaft_threshold} (float)</li>
     *   <li>{@code light_shaft_intensity} (float)</li>
     *   <li>{@code light_shaft_decay} (float)</li>
     *   <li>{@code height_fog_density} (float)</li>
     *   <li>{@code height_fog_falloff} (float)</li>
     *   <li>{@code height_fog_base_height} (float)</li>
     *   <li>{@code motion_blur_strength} (float)</li>
     *   <li>{@code motion_blur_max_distance} (float)</li>
     *   <li>{@code chromatic_aberration_strength} (float)</li>
     *   <li>{@code film_grain_intensity} (float)</li>
     *   <li>{@code lens_flare_threshold} (float)</li>
     *   <li>{@code lens_flare_intensity} (float)</li>
     *   <li>{@code lens_flare_dispersal} (float)</li>
     *   <li>{@code color_grade_strength} (float)</li>
     *   <li>{@code dof_focus_distance} (float)</li>
     *   <li>{@code dof_focal_length} (float)</li>
     *   <li>{@code doff_number} (float)</li>
     *   <li>{@code dof_max_radius} (float)</li>
     *   <li>{@code ssr_roughness_blur} (float)</li>
     *   <li>{@code ssr_intensity} (float)</li>
     *   <li>{@code fxaa_edge_threshold_ext} (float)</li>
     * </ol>
     */
    public static native int renderPipelineGetSettings(long pipeline, byte[] outSettingsBytes, long[] outSettingsIntegral, float[] outSettingsFloating);

    /**
     * cna_render_pipeline_get_statistics (engine_layer.h).
     *
     * <p>outStatisticsBytes carries CNA_RenderPipelineFrameStatisticsEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     * </ol>
     *
     * <p>outStatisticsIntegral carries CNA_RenderPipelineFrameStatisticsEXT in this order:
     * <ol start="0">
     *   <li>{@code passes_run} (int32_t)</li>
     *   <li>{@code target_switches} (int32_t)</li>
     *   <li>{@code used_scene_target} (CNA_Bool)</li>
     *   <li>{@code drew_skybox} (CNA_Bool)</li>
     *   <li>{@code gpu_memory_estimate_bytes} (uint64_t)</li>
     * </ol>
     */
    public static native int renderPipelineGetStatistics(long pipeline, byte[] outStatisticsBytes, long[] outStatisticsIntegral);

    /**
     * cna_render_pipeline_is_gpu_timing_enabled_ext (engine_layer.h).
     */
    public static native int renderPipelineIsGpuTimingEnabledExt(long pipeline, boolean[] outEnabled);

    /**
     * cna_render_pipeline_is_using_scene_target (engine_layer.h).
     */
    public static native int renderPipelineIsUsingSceneTarget(long pipeline, boolean[] outUsing);

    /**
     * cna_render_pipeline_release_device_resources_ext (engine_layer.h).
     */
    public static native int renderPipelineReleaseDeviceResourcesExt(long pipeline);

    /**
     * cna_render_pipeline_resize (engine_layer.h).
     */
    public static native int renderPipelineResize(long pipeline, int width, int height);

    /**
     * cna_render_pipeline_set_camera (engine_layer.h).
     *
     * <p>viewFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     *
     * <p>projectionFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     */
    public static native int renderPipelineSetCamera(long pipeline, float[] viewFloating, float[] projectionFloating, float nearPlane, float farPlane);

    /**
     * cna_render_pipeline_set_depth_normal_inputs (engine_layer.h).
     */
    public static native int renderPipelineSetDepthNormalInputs(long pipeline, long depth, long normals);

    /**
     * cna_render_pipeline_set_gpu_timing_enabled_ext (engine_layer.h).
     */
    public static native int renderPipelineSetGpuTimingEnabledExt(long pipeline, boolean value);

    /**
     * cna_render_pipeline_set_settings (engine_layer.h).
     *
     * <p>settingsBytes carries CNA_RenderPipelineSettingsEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     *   <li>{@code reserved[3]} (uint8_t)</li>
     * </ol>
     *
     * <p>settingsIntegral carries CNA_RenderPipelineSettingsEXT in this order:
     * <ol start="0">
     *   <li>{@code hdr_enabled} (CNA_Bool)</li>
     *   <li>{@code tonemapping_mode} (CNA_TonemappingMode)</li>
     *   <li>{@code bloom_enabled} (CNA_Bool)</li>
     *   <li>{@code bloom_iterations} (int32_t)</li>
     *   <li>{@code ssao_enabled} (CNA_Bool)</li>
     *   <li>{@code transparency_mode} (CNA_TransparencyMode)</li>
     *   <li>{@code ssao_sample_count} (int32_t)</li>
     *   <li>{@code ssr_enabled} (CNA_Bool)</li>
     *   <li>{@code ssr_step_count} (int32_t)</li>
     *   <li>{@code color_grade_enabled} (CNA_Bool)</li>
     *   <li>{@code dof_enabled} (CNA_Bool)</li>
     *   <li>{@code fxaa_enabled} (CNA_Bool)</li>
     *   <li>{@code render_quality} (CNA_RenderQuality)</li>
     *   <li>{@code shadow_quality} (CNA_ShadowQuality)</li>
     *   <li>{@code shadows_enabled} (CNA_Bool)</li>
     * </ol>
     *
     * <p>settingsFloating carries CNA_RenderPipelineSettingsEXT in this order:
     * <ol start="0">
     *   <li>{@code exposure} (float)</li>
     *   <li>{@code gamma} (float)</li>
     *   <li>{@code bloom_intensity} (float)</li>
     *   <li>{@code bloom_threshold} (float)</li>
     *   <li>{@code ssao_radius} (float)</li>
     *   <li>{@code ssao_intensity} (float)</li>
     *   <li>{@code ssr_max_distance} (float)</li>
     *   <li>{@code ssr_thickness} (float)</li>
     *   <li>{@code ssr_depth_bias} (float)</li>
     *   <li>{@code ssr_edge_fade} (float)</li>
     *   <li>{@code volumetric_fog_density} (float)</li>
     *   <li>{@code light_shaft_threshold} (float)</li>
     *   <li>{@code light_shaft_intensity} (float)</li>
     *   <li>{@code light_shaft_decay} (float)</li>
     *   <li>{@code height_fog_density} (float)</li>
     *   <li>{@code height_fog_falloff} (float)</li>
     *   <li>{@code height_fog_base_height} (float)</li>
     *   <li>{@code motion_blur_strength} (float)</li>
     *   <li>{@code motion_blur_max_distance} (float)</li>
     *   <li>{@code chromatic_aberration_strength} (float)</li>
     *   <li>{@code film_grain_intensity} (float)</li>
     *   <li>{@code lens_flare_threshold} (float)</li>
     *   <li>{@code lens_flare_intensity} (float)</li>
     *   <li>{@code lens_flare_dispersal} (float)</li>
     *   <li>{@code color_grade_strength} (float)</li>
     *   <li>{@code dof_focus_distance} (float)</li>
     *   <li>{@code dof_focal_length} (float)</li>
     *   <li>{@code doff_number} (float)</li>
     *   <li>{@code dof_max_radius} (float)</li>
     *   <li>{@code ssr_roughness_blur} (float)</li>
     *   <li>{@code ssr_intensity} (float)</li>
     *   <li>{@code fxaa_edge_threshold_ext} (float)</li>
     * </ol>
     */
    public static native int renderPipelineSetSettings(long pipeline, byte[] settingsBytes, long[] settingsIntegral, float[] settingsFloating);

    /**
     * cna_render_pipeline_set_skybox (engine_layer.h).
     */
    public static native int renderPipelineSetSkybox(long pipeline, long skybox);

    /**
     * cna_render_pipeline_set_skybox_camera (engine_layer.h).
     *
     * <p>viewFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     *
     * <p>projectionFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     */
    public static native int renderPipelineSetSkyboxCamera(long pipeline, float[] viewFloating, float[] projectionFloating);

    /**
     * cna_render_pipeline_set_velocity_input_ext (engine_layer.h).
     */
    public static native int renderPipelineSetVelocityInputExt(long pipeline, long velocity);

    /**
     * cna_render_pipeline_settings_ext_apply_from_string (engine_layer.h).
     *
     * <p>settingsBytes carries CNA_RenderPipelineSettingsEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     *   <li>{@code reserved[3]} (uint8_t)</li>
     * </ol>
     *
     * <p>settingsIntegral carries CNA_RenderPipelineSettingsEXT in this order:
     * <ol start="0">
     *   <li>{@code hdr_enabled} (CNA_Bool)</li>
     *   <li>{@code tonemapping_mode} (CNA_TonemappingMode)</li>
     *   <li>{@code bloom_enabled} (CNA_Bool)</li>
     *   <li>{@code bloom_iterations} (int32_t)</li>
     *   <li>{@code ssao_enabled} (CNA_Bool)</li>
     *   <li>{@code transparency_mode} (CNA_TransparencyMode)</li>
     *   <li>{@code ssao_sample_count} (int32_t)</li>
     *   <li>{@code ssr_enabled} (CNA_Bool)</li>
     *   <li>{@code ssr_step_count} (int32_t)</li>
     *   <li>{@code color_grade_enabled} (CNA_Bool)</li>
     *   <li>{@code dof_enabled} (CNA_Bool)</li>
     *   <li>{@code fxaa_enabled} (CNA_Bool)</li>
     *   <li>{@code render_quality} (CNA_RenderQuality)</li>
     *   <li>{@code shadow_quality} (CNA_ShadowQuality)</li>
     *   <li>{@code shadows_enabled} (CNA_Bool)</li>
     * </ol>
     *
     * <p>settingsFloating carries CNA_RenderPipelineSettingsEXT in this order:
     * <ol start="0">
     *   <li>{@code exposure} (float)</li>
     *   <li>{@code gamma} (float)</li>
     *   <li>{@code bloom_intensity} (float)</li>
     *   <li>{@code bloom_threshold} (float)</li>
     *   <li>{@code ssao_radius} (float)</li>
     *   <li>{@code ssao_intensity} (float)</li>
     *   <li>{@code ssr_max_distance} (float)</li>
     *   <li>{@code ssr_thickness} (float)</li>
     *   <li>{@code ssr_depth_bias} (float)</li>
     *   <li>{@code ssr_edge_fade} (float)</li>
     *   <li>{@code volumetric_fog_density} (float)</li>
     *   <li>{@code light_shaft_threshold} (float)</li>
     *   <li>{@code light_shaft_intensity} (float)</li>
     *   <li>{@code light_shaft_decay} (float)</li>
     *   <li>{@code height_fog_density} (float)</li>
     *   <li>{@code height_fog_falloff} (float)</li>
     *   <li>{@code height_fog_base_height} (float)</li>
     *   <li>{@code motion_blur_strength} (float)</li>
     *   <li>{@code motion_blur_max_distance} (float)</li>
     *   <li>{@code chromatic_aberration_strength} (float)</li>
     *   <li>{@code film_grain_intensity} (float)</li>
     *   <li>{@code lens_flare_threshold} (float)</li>
     *   <li>{@code lens_flare_intensity} (float)</li>
     *   <li>{@code lens_flare_dispersal} (float)</li>
     *   <li>{@code color_grade_strength} (float)</li>
     *   <li>{@code dof_focus_distance} (float)</li>
     *   <li>{@code dof_focal_length} (float)</li>
     *   <li>{@code doff_number} (float)</li>
     *   <li>{@code dof_max_radius} (float)</li>
     *   <li>{@code ssr_roughness_blur} (float)</li>
     *   <li>{@code ssr_intensity} (float)</li>
     *   <li>{@code fxaa_edge_threshold_ext} (float)</li>
     * </ol>
     */
    public static native int renderPipelineSettingsExtApplyFromString(byte[] settingsBytes, long[] settingsIntegral, float[] settingsFloating, byte[] text, int[] outApplied);

    /**
     * cna_render_pipeline_settings_ext_apply_render_quality_preset (engine_layer.h).
     *
     * <p>settingsBytes carries CNA_RenderPipelineSettingsEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     *   <li>{@code reserved[3]} (uint8_t)</li>
     * </ol>
     *
     * <p>settingsIntegral carries CNA_RenderPipelineSettingsEXT in this order:
     * <ol start="0">
     *   <li>{@code hdr_enabled} (CNA_Bool)</li>
     *   <li>{@code tonemapping_mode} (CNA_TonemappingMode)</li>
     *   <li>{@code bloom_enabled} (CNA_Bool)</li>
     *   <li>{@code bloom_iterations} (int32_t)</li>
     *   <li>{@code ssao_enabled} (CNA_Bool)</li>
     *   <li>{@code transparency_mode} (CNA_TransparencyMode)</li>
     *   <li>{@code ssao_sample_count} (int32_t)</li>
     *   <li>{@code ssr_enabled} (CNA_Bool)</li>
     *   <li>{@code ssr_step_count} (int32_t)</li>
     *   <li>{@code color_grade_enabled} (CNA_Bool)</li>
     *   <li>{@code dof_enabled} (CNA_Bool)</li>
     *   <li>{@code fxaa_enabled} (CNA_Bool)</li>
     *   <li>{@code render_quality} (CNA_RenderQuality)</li>
     *   <li>{@code shadow_quality} (CNA_ShadowQuality)</li>
     *   <li>{@code shadows_enabled} (CNA_Bool)</li>
     * </ol>
     *
     * <p>settingsFloating carries CNA_RenderPipelineSettingsEXT in this order:
     * <ol start="0">
     *   <li>{@code exposure} (float)</li>
     *   <li>{@code gamma} (float)</li>
     *   <li>{@code bloom_intensity} (float)</li>
     *   <li>{@code bloom_threshold} (float)</li>
     *   <li>{@code ssao_radius} (float)</li>
     *   <li>{@code ssao_intensity} (float)</li>
     *   <li>{@code ssr_max_distance} (float)</li>
     *   <li>{@code ssr_thickness} (float)</li>
     *   <li>{@code ssr_depth_bias} (float)</li>
     *   <li>{@code ssr_edge_fade} (float)</li>
     *   <li>{@code volumetric_fog_density} (float)</li>
     *   <li>{@code light_shaft_threshold} (float)</li>
     *   <li>{@code light_shaft_intensity} (float)</li>
     *   <li>{@code light_shaft_decay} (float)</li>
     *   <li>{@code height_fog_density} (float)</li>
     *   <li>{@code height_fog_falloff} (float)</li>
     *   <li>{@code height_fog_base_height} (float)</li>
     *   <li>{@code motion_blur_strength} (float)</li>
     *   <li>{@code motion_blur_max_distance} (float)</li>
     *   <li>{@code chromatic_aberration_strength} (float)</li>
     *   <li>{@code film_grain_intensity} (float)</li>
     *   <li>{@code lens_flare_threshold} (float)</li>
     *   <li>{@code lens_flare_intensity} (float)</li>
     *   <li>{@code lens_flare_dispersal} (float)</li>
     *   <li>{@code color_grade_strength} (float)</li>
     *   <li>{@code dof_focus_distance} (float)</li>
     *   <li>{@code dof_focal_length} (float)</li>
     *   <li>{@code doff_number} (float)</li>
     *   <li>{@code dof_max_radius} (float)</li>
     *   <li>{@code ssr_roughness_blur} (float)</li>
     *   <li>{@code ssr_intensity} (float)</li>
     *   <li>{@code fxaa_edge_threshold_ext} (float)</li>
     * </ol>
     */
    public static native int renderPipelineSettingsExtApplyRenderQualityPreset(byte[] settingsBytes, long[] settingsIntegral, float[] settingsFloating);

    /**
     * cna_render_pipeline_settings_ext_init (engine_layer.h).
     *
     * <p>outSettingsBytes carries CNA_RenderPipelineSettingsEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     *   <li>{@code reserved[3]} (uint8_t)</li>
     * </ol>
     *
     * <p>outSettingsIntegral carries CNA_RenderPipelineSettingsEXT in this order:
     * <ol start="0">
     *   <li>{@code hdr_enabled} (CNA_Bool)</li>
     *   <li>{@code tonemapping_mode} (CNA_TonemappingMode)</li>
     *   <li>{@code bloom_enabled} (CNA_Bool)</li>
     *   <li>{@code bloom_iterations} (int32_t)</li>
     *   <li>{@code ssao_enabled} (CNA_Bool)</li>
     *   <li>{@code transparency_mode} (CNA_TransparencyMode)</li>
     *   <li>{@code ssao_sample_count} (int32_t)</li>
     *   <li>{@code ssr_enabled} (CNA_Bool)</li>
     *   <li>{@code ssr_step_count} (int32_t)</li>
     *   <li>{@code color_grade_enabled} (CNA_Bool)</li>
     *   <li>{@code dof_enabled} (CNA_Bool)</li>
     *   <li>{@code fxaa_enabled} (CNA_Bool)</li>
     *   <li>{@code render_quality} (CNA_RenderQuality)</li>
     *   <li>{@code shadow_quality} (CNA_ShadowQuality)</li>
     *   <li>{@code shadows_enabled} (CNA_Bool)</li>
     * </ol>
     *
     * <p>outSettingsFloating carries CNA_RenderPipelineSettingsEXT in this order:
     * <ol start="0">
     *   <li>{@code exposure} (float)</li>
     *   <li>{@code gamma} (float)</li>
     *   <li>{@code bloom_intensity} (float)</li>
     *   <li>{@code bloom_threshold} (float)</li>
     *   <li>{@code ssao_radius} (float)</li>
     *   <li>{@code ssao_intensity} (float)</li>
     *   <li>{@code ssr_max_distance} (float)</li>
     *   <li>{@code ssr_thickness} (float)</li>
     *   <li>{@code ssr_depth_bias} (float)</li>
     *   <li>{@code ssr_edge_fade} (float)</li>
     *   <li>{@code volumetric_fog_density} (float)</li>
     *   <li>{@code light_shaft_threshold} (float)</li>
     *   <li>{@code light_shaft_intensity} (float)</li>
     *   <li>{@code light_shaft_decay} (float)</li>
     *   <li>{@code height_fog_density} (float)</li>
     *   <li>{@code height_fog_falloff} (float)</li>
     *   <li>{@code height_fog_base_height} (float)</li>
     *   <li>{@code motion_blur_strength} (float)</li>
     *   <li>{@code motion_blur_max_distance} (float)</li>
     *   <li>{@code chromatic_aberration_strength} (float)</li>
     *   <li>{@code film_grain_intensity} (float)</li>
     *   <li>{@code lens_flare_threshold} (float)</li>
     *   <li>{@code lens_flare_intensity} (float)</li>
     *   <li>{@code lens_flare_dispersal} (float)</li>
     *   <li>{@code color_grade_strength} (float)</li>
     *   <li>{@code dof_focus_distance} (float)</li>
     *   <li>{@code dof_focal_length} (float)</li>
     *   <li>{@code doff_number} (float)</li>
     *   <li>{@code dof_max_radius} (float)</li>
     *   <li>{@code ssr_roughness_blur} (float)</li>
     *   <li>{@code ssr_intensity} (float)</li>
     *   <li>{@code fxaa_edge_threshold_ext} (float)</li>
     * </ol>
     */
    public static native int renderPipelineSettingsExtInit(byte[] outSettingsBytes, long[] outSettingsIntegral, float[] outSettingsFloating);

    /**
     * cna_render_pipeline_settings_ext_normalize (engine_layer.h).
     *
     * <p>settingsBytes carries CNA_RenderPipelineSettingsEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     *   <li>{@code reserved[3]} (uint8_t)</li>
     * </ol>
     *
     * <p>settingsIntegral carries CNA_RenderPipelineSettingsEXT in this order:
     * <ol start="0">
     *   <li>{@code hdr_enabled} (CNA_Bool)</li>
     *   <li>{@code tonemapping_mode} (CNA_TonemappingMode)</li>
     *   <li>{@code bloom_enabled} (CNA_Bool)</li>
     *   <li>{@code bloom_iterations} (int32_t)</li>
     *   <li>{@code ssao_enabled} (CNA_Bool)</li>
     *   <li>{@code transparency_mode} (CNA_TransparencyMode)</li>
     *   <li>{@code ssao_sample_count} (int32_t)</li>
     *   <li>{@code ssr_enabled} (CNA_Bool)</li>
     *   <li>{@code ssr_step_count} (int32_t)</li>
     *   <li>{@code color_grade_enabled} (CNA_Bool)</li>
     *   <li>{@code dof_enabled} (CNA_Bool)</li>
     *   <li>{@code fxaa_enabled} (CNA_Bool)</li>
     *   <li>{@code render_quality} (CNA_RenderQuality)</li>
     *   <li>{@code shadow_quality} (CNA_ShadowQuality)</li>
     *   <li>{@code shadows_enabled} (CNA_Bool)</li>
     * </ol>
     *
     * <p>settingsFloating carries CNA_RenderPipelineSettingsEXT in this order:
     * <ol start="0">
     *   <li>{@code exposure} (float)</li>
     *   <li>{@code gamma} (float)</li>
     *   <li>{@code bloom_intensity} (float)</li>
     *   <li>{@code bloom_threshold} (float)</li>
     *   <li>{@code ssao_radius} (float)</li>
     *   <li>{@code ssao_intensity} (float)</li>
     *   <li>{@code ssr_max_distance} (float)</li>
     *   <li>{@code ssr_thickness} (float)</li>
     *   <li>{@code ssr_depth_bias} (float)</li>
     *   <li>{@code ssr_edge_fade} (float)</li>
     *   <li>{@code volumetric_fog_density} (float)</li>
     *   <li>{@code light_shaft_threshold} (float)</li>
     *   <li>{@code light_shaft_intensity} (float)</li>
     *   <li>{@code light_shaft_decay} (float)</li>
     *   <li>{@code height_fog_density} (float)</li>
     *   <li>{@code height_fog_falloff} (float)</li>
     *   <li>{@code height_fog_base_height} (float)</li>
     *   <li>{@code motion_blur_strength} (float)</li>
     *   <li>{@code motion_blur_max_distance} (float)</li>
     *   <li>{@code chromatic_aberration_strength} (float)</li>
     *   <li>{@code film_grain_intensity} (float)</li>
     *   <li>{@code lens_flare_threshold} (float)</li>
     *   <li>{@code lens_flare_intensity} (float)</li>
     *   <li>{@code lens_flare_dispersal} (float)</li>
     *   <li>{@code color_grade_strength} (float)</li>
     *   <li>{@code dof_focus_distance} (float)</li>
     *   <li>{@code dof_focal_length} (float)</li>
     *   <li>{@code doff_number} (float)</li>
     *   <li>{@code dof_max_radius} (float)</li>
     *   <li>{@code ssr_roughness_blur} (float)</li>
     *   <li>{@code ssr_intensity} (float)</li>
     *   <li>{@code fxaa_edge_threshold_ext} (float)</li>
     * </ol>
     */
    public static native int renderPipelineSettingsExtNormalize(byte[] settingsBytes, long[] settingsIntegral, float[] settingsFloating);

    /**
     * cna_render_target_pool_acquire (engine_layer.h).
     */
    public static native int renderTargetPoolAcquire(long pool, int width, int height, int format, int depthFormat, int slot, long[] outRenderTarget);

    /**
     * cna_render_target_pool_create (engine_layer.h).
     */
    public static native int renderTargetPoolCreate(long graphicsDevice, long[] outPool);

    /**
     * cna_render_target_pool_destroy (engine_layer.h).
     */
    public static native int renderTargetPoolDestroy(long pool);

    /**
     * cna_render_target_pool_get_estimated_bytes (engine_layer.h).
     */
    public static native int renderTargetPoolGetEstimatedBytes(long pool, long[] outBytes);

    /**
     * cna_render_target_pool_get_target_count (engine_layer.h).
     */
    public static native int renderTargetPoolGetTargetCount(long pool, long[] outTargetCount);

    /**
     * cna_render_target_pool_reset (engine_layer.h).
     */
    public static native int renderTargetPoolReset(long pool);

    /**
     * cna_scoped_render_target_begin (engine_layer.h).
     */
    public static native int scopedRenderTargetBegin(long graphicsDevice, long destination, long[] outScope);

    /**
     * cna_scoped_render_target_end (engine_layer.h).
     */
    public static native int scopedRenderTargetEnd(long scope);

    /**
     * cna_scoped_render_target_get_has_recorded_previous (engine_layer.h).
     */
    public static native int scopedRenderTargetGetHasRecordedPrevious(long scope, boolean[] outRecorded);

    /**
     * cna_shadow_map_apply_caster (engine_layer.h).
     */
    public static native int shadowMapApplyCaster(long shadowMap);

    /**
     * cna_shadow_map_apply_skinned_caster (engine_layer.h).
     */
    public static native int shadowMapApplySkinnedCaster(long shadowMap, float[] boneTransformsFloating, int weightsPerVertex);

    /**
     * cna_shadow_map_begin (engine_layer.h).
     *
     * <p>lightBytes carries CNA_DirectionalLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>lightIntegral carries CNA_DirectionalLightEXT in this order:
     * <ol start="0">
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>lightFloating carries CNA_DirectionalLightEXT in this order:
     * <ol start="0">
     *   <li>{@code direction.x} (float)</li>
     *   <li>{@code direction.y} (float)</li>
     *   <li>{@code direction.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     * </ol>
     *
     * <p>sceneBoundsFloating carries CNA_BoundingBox in this order:
     * <ol start="0">
     *   <li>{@code min.x} (float)</li>
     *   <li>{@code min.y} (float)</li>
     *   <li>{@code min.z} (float)</li>
     *   <li>{@code max.x} (float)</li>
     *   <li>{@code max.y} (float)</li>
     *   <li>{@code max.z} (float)</li>
     * </ol>
     */
    public static native int shadowMapBegin(long shadowMap, byte[] lightBytes, long[] lightIntegral, float[] lightFloating, float[] sceneBoundsFloating);

    /**
     * cna_shadow_map_compute_light_projection (engine_layer.h).
     *
     * <p>lightViewFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     *
     * <p>sceneBoundsFloating carries CNA_BoundingBox in this order:
     * <ol start="0">
     *   <li>{@code min.x} (float)</li>
     *   <li>{@code min.y} (float)</li>
     *   <li>{@code min.z} (float)</li>
     *   <li>{@code max.x} (float)</li>
     *   <li>{@code max.y} (float)</li>
     *   <li>{@code max.z} (float)</li>
     * </ol>
     *
     * <p>outMatrixFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     */
    public static native int shadowMapComputeLightProjection(float[] lightViewFloating, float[] sceneBoundsFloating, float[] outMatrixFloating);

    /**
     * cna_shadow_map_compute_light_view (engine_layer.h).
     *
     * <p>lightBytes carries CNA_DirectionalLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>lightIntegral carries CNA_DirectionalLightEXT in this order:
     * <ol start="0">
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>lightFloating carries CNA_DirectionalLightEXT in this order:
     * <ol start="0">
     *   <li>{@code direction.x} (float)</li>
     *   <li>{@code direction.y} (float)</li>
     *   <li>{@code direction.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     * </ol>
     *
     * <p>sceneBoundsFloating carries CNA_BoundingBox in this order:
     * <ol start="0">
     *   <li>{@code min.x} (float)</li>
     *   <li>{@code min.y} (float)</li>
     *   <li>{@code min.z} (float)</li>
     *   <li>{@code max.x} (float)</li>
     *   <li>{@code max.y} (float)</li>
     *   <li>{@code max.z} (float)</li>
     * </ol>
     *
     * <p>outMatrixFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     */
    public static native int shadowMapComputeLightView(byte[] lightBytes, long[] lightIntegral, float[] lightFloating, float[] sceneBoundsFloating, float[] outMatrixFloating);

    /**
     * cna_shadow_map_create (engine_layer.h).
     */
    public static native int shadowMapCreate(long graphicsDevice, int quality, long[] outShadowMap);

    /**
     * cna_shadow_map_destroy (engine_layer.h).
     */
    public static native int shadowMapDestroy(long shadowMap);

    /**
     * cna_shadow_map_end (engine_layer.h).
     */
    public static native int shadowMapEnd(long shadowMap);

    /**
     * cna_shadow_map_filter_radius_for_quality (engine_layer.h).
     */
    public static native int shadowMapFilterRadiusForQuality(int quality, int[] outRadius);

    /**
     * cna_shadow_map_get_depth_bias (engine_layer.h).
     */
    public static native int shadowMapGetDepthBias(long shadowMap, float[] outBias);

    /**
     * cna_shadow_map_get_filter_radius (engine_layer.h).
     */
    public static native int shadowMapGetFilterRadius(long shadowMap, int[] outRadius);

    /**
     * cna_shadow_map_get_light_view_projection (engine_layer.h).
     *
     * <p>outMatrixFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     */
    public static native int shadowMapGetLightViewProjection(long shadowMap, float[] outMatrixFloating);

    /**
     * cna_shadow_map_get_quality (engine_layer.h).
     */
    public static native int shadowMapGetQuality(long shadowMap, int[] outQuality);

    /**
     * cna_shadow_map_get_shadow_texture (engine_layer.h).
     */
    public static native int shadowMapGetShadowTexture(long shadowMap, long[] outTexture);

    /**
     * cna_shadow_map_get_size (engine_layer.h).
     */
    public static native int shadowMapGetSize(long shadowMap, int[] outSize);

    /**
     * cna_shadow_map_is_supported (engine_layer.h).
     */
    public static native int shadowMapIsSupported(long shadowMap, boolean[] outSupported);

    /**
     * cna_shadow_map_set_depth_bias (engine_layer.h).
     */
    public static native int shadowMapSetDepthBias(long shadowMap, float bias);

    /**
     * cna_shadow_map_size_for_quality (engine_layer.h).
     */
    public static native int shadowMapSizeForQuality(int quality, int[] outSize);

    /**
     * cna_skybox_compute_view_ray (engine_layer.h).
     *
     * <p>viewFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     *
     * <p>projectionFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     *
     * <p>outDirectionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int skyboxComputeViewRay(float[] viewFloating, float[] projectionFloating, float ndcX, float ndcY, float yaw, float[] outDirectionFloating);

    /**
     * cna_skybox_create (engine_layer.h).
     */
    public static native int skyboxCreate(long graphicsDevice, long environment, long[] outSkybox);

    /**
     * cna_skybox_destroy (engine_layer.h).
     */
    public static native int skyboxDestroy(long skybox);

    /**
     * cna_skybox_draw (engine_layer.h).
     *
     * <p>viewFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     *
     * <p>projectionFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     */
    public static native int skyboxDraw(long skybox, float[] viewFloating, float[] projectionFloating, int width, int height);

    /**
     * cna_skybox_get_intensity (engine_layer.h).
     */
    public static native int skyboxGetIntensity(long skybox, float[] outIntensity);

    /**
     * cna_skybox_get_tint (engine_layer.h).
     *
     * <p>outTintFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int skyboxGetTint(long skybox, float[] outTintFloating);

    /**
     * cna_skybox_get_yaw (engine_layer.h).
     */
    public static native int skyboxGetYaw(long skybox, float[] outRadians);

    /**
     * cna_skybox_is_supported (engine_layer.h).
     */
    public static native int skyboxIsSupported(long skybox, boolean[] outSupported);

    /**
     * cna_skybox_set_environment (engine_layer.h).
     */
    public static native int skyboxSetEnvironment(long skybox, long environment);

    /**
     * cna_skybox_set_intensity (engine_layer.h).
     */
    public static native int skyboxSetIntensity(long skybox, float intensity);

    /**
     * cna_skybox_set_owned_environment (engine_layer.h).
     */
    public static native int skyboxSetOwnedEnvironment(long skybox, long environment);

    /**
     * cna_skybox_set_tint (engine_layer.h).
     *
     * <p>tintFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int skyboxSetTint(long skybox, float[] tintFloating);

    /**
     * cna_skybox_set_yaw (engine_layer.h).
     */
    public static native int skyboxSetYaw(long skybox, float radians);

    /**
     * cna_spatial_upscale_pass_create (engine_layer.h).
     */
    public static native int spatialUpscalePassCreate(long graphicsDevice, long[] outPass);

    /**
     * cna_spatial_upscale_pass_destroy (engine_layer.h).
     */
    public static native int spatialUpscalePassDestroy(long pass);

    /**
     * cna_spatial_upscale_pass_draw (engine_layer.h).
     */
    public static native int spatialUpscalePassDraw(long pass, long source, int sourceWidth, int sourceHeight, int targetWidth, int targetHeight);

    /**
     * cna_spatial_upscale_pass_get_edge_adaptive (engine_layer.h).
     */
    public static native int spatialUpscalePassGetEdgeAdaptive(long pass, boolean[] outValue);

    /**
     * cna_spatial_upscale_pass_get_sharpness (engine_layer.h).
     */
    public static native int spatialUpscalePassGetSharpness(long pass, float[] outValue);

    /**
     * cna_spatial_upscale_pass_is_identity_scale (engine_layer.h).
     */
    public static native int spatialUpscalePassIsIdentityScale(int sourceWidth, int sourceHeight, int targetWidth, int targetHeight, boolean[] outIdentity);

    /**
     * cna_spatial_upscale_pass_set_edge_adaptive (engine_layer.h).
     */
    public static native int spatialUpscalePassSetEdgeAdaptive(long pass, boolean value);

    /**
     * cna_spatial_upscale_pass_set_sharpness (engine_layer.h).
     */
    public static native int spatialUpscalePassSetSharpness(long pass, float value);

    /**
     * cna_spot_light_ext_init (engine_layer.h).
     *
     * <p>outLightBytes carries CNA_SpotLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>outLightIntegral carries CNA_SpotLightEXT in this order:
     * <ol start="0">
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>outLightFloating carries CNA_SpotLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code direction.x} (float)</li>
     *   <li>{@code direction.y} (float)</li>
     *   <li>{@code direction.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     *   <li>{@code inner_angle} (float)</li>
     *   <li>{@code outer_angle} (float)</li>
     * </ol>
     */
    public static native int spotLightExtInit(byte[] outLightBytes, long[] outLightIntegral, float[] outLightFloating);

    /**
     * cna_spot_shadow_map_begin (engine_layer.h).
     *
     * <p>lightBytes carries CNA_SpotLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>lightIntegral carries CNA_SpotLightEXT in this order:
     * <ol start="0">
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>lightFloating carries CNA_SpotLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code direction.x} (float)</li>
     *   <li>{@code direction.y} (float)</li>
     *   <li>{@code direction.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     *   <li>{@code inner_angle} (float)</li>
     *   <li>{@code outer_angle} (float)</li>
     * </ol>
     */
    public static native int spotShadowMapBegin(long shadowMap, byte[] lightBytes, long[] lightIntegral, float[] lightFloating);

    /**
     * cna_spot_shadow_map_compute_light_projection (engine_layer.h).
     *
     * <p>lightBytes carries CNA_SpotLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>lightIntegral carries CNA_SpotLightEXT in this order:
     * <ol start="0">
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>lightFloating carries CNA_SpotLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code direction.x} (float)</li>
     *   <li>{@code direction.y} (float)</li>
     *   <li>{@code direction.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     *   <li>{@code inner_angle} (float)</li>
     *   <li>{@code outer_angle} (float)</li>
     * </ol>
     *
     * <p>outMatrixFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     */
    public static native int spotShadowMapComputeLightProjection(byte[] lightBytes, long[] lightIntegral, float[] lightFloating, float[] outMatrixFloating);

    /**
     * cna_spot_shadow_map_compute_light_view (engine_layer.h).
     *
     * <p>lightBytes carries CNA_SpotLightEXT in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>lightIntegral carries CNA_SpotLightEXT in this order:
     * <ol start="0">
     *   <li>{@code casts_shadows} (CNA_Bool)</li>
     * </ol>
     *
     * <p>lightFloating carries CNA_SpotLightEXT in this order:
     * <ol start="0">
     *   <li>{@code position.x} (float)</li>
     *   <li>{@code position.y} (float)</li>
     *   <li>{@code position.z} (float)</li>
     *   <li>{@code direction.x} (float)</li>
     *   <li>{@code direction.y} (float)</li>
     *   <li>{@code direction.z} (float)</li>
     *   <li>{@code color.x} (float)</li>
     *   <li>{@code color.y} (float)</li>
     *   <li>{@code color.z} (float)</li>
     *   <li>{@code intensity} (float)</li>
     *   <li>{@code range} (float)</li>
     *   <li>{@code inner_angle} (float)</li>
     *   <li>{@code outer_angle} (float)</li>
     * </ol>
     *
     * <p>outMatrixFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     */
    public static native int spotShadowMapComputeLightView(byte[] lightBytes, long[] lightIntegral, float[] lightFloating, float[] outMatrixFloating);

    /**
     * cna_spot_shadow_map_create (engine_layer.h).
     */
    public static native int spotShadowMapCreate(long graphicsDevice, int quality, long[] outShadowMap);

    /**
     * cna_spot_shadow_map_destroy (engine_layer.h).
     */
    public static native int spotShadowMapDestroy(long shadowMap);

    /**
     * cna_spot_shadow_map_end (engine_layer.h).
     */
    public static native int spotShadowMapEnd(long shadowMap);

    /**
     * cna_spot_shadow_map_get_depth_bias (engine_layer.h).
     */
    public static native int spotShadowMapGetDepthBias(long shadowMap, float[] outBias);

    /**
     * cna_spot_shadow_map_get_light_position (engine_layer.h).
     *
     * <p>outPositionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int spotShadowMapGetLightPosition(long shadowMap, float[] outPositionFloating);

    /**
     * cna_spot_shadow_map_get_light_range (engine_layer.h).
     */
    public static native int spotShadowMapGetLightRange(long shadowMap, float[] outRange);

    /**
     * cna_spot_shadow_map_get_light_view_projection (engine_layer.h).
     *
     * <p>outMatrixFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     */
    public static native int spotShadowMapGetLightViewProjection(long shadowMap, float[] outMatrixFloating);

    /**
     * cna_spot_shadow_map_get_quality (engine_layer.h).
     */
    public static native int spotShadowMapGetQuality(long shadowMap, int[] outQuality);

    /**
     * cna_spot_shadow_map_get_shadow_texture (engine_layer.h).
     */
    public static native int spotShadowMapGetShadowTexture(long shadowMap, long[] outTexture);

    /**
     * cna_spot_shadow_map_get_size (engine_layer.h).
     */
    public static native int spotShadowMapGetSize(long shadowMap, int[] outSize);

    /**
     * cna_spot_shadow_map_is_supported (engine_layer.h).
     */
    public static native int spotShadowMapIsSupported(long shadowMap, boolean[] outSupported);

    /**
     * cna_spot_shadow_map_set_depth_bias (engine_layer.h).
     */
    public static native int spotShadowMapSetDepthBias(long shadowMap, float bias);

    /**
     * cna_ssao_pass_copy_kernel (engine_layer.h).
     */
    public static native int ssaoPassCopyKernel(long pass, float[] destinationFloating, long[] outCount);

    /**
     * cna_ssao_pass_copy_occlusion_glsl (engine_layer.h).
     */
    public static native int ssaoPassCopyOcclusionGlsl(boolean packed, byte[] destination, long[] outBytes);

    /**
     * cna_ssao_pass_create (engine_layer.h).
     */
    public static native int ssaoPassCreate(long graphicsDevice, long[] outPass);

    /**
     * cna_ssao_pass_get_half_resolution (engine_layer.h).
     */
    public static native int ssaoPassGetHalfResolution(long pass, boolean[] outValue);

    /**
     * cna_ssao_pass_get_intensity (engine_layer.h).
     */
    public static native int ssaoPassGetIntensity(long pass, float[] outValue);

    /**
     * cna_ssao_pass_get_radius (engine_layer.h).
     */
    public static native int ssaoPassGetRadius(long pass, float[] outValue);

    /**
     * cna_ssao_pass_get_sample_count (engine_layer.h).
     */
    public static native int ssaoPassGetSampleCount(long pass, int[] outValue);

    /**
     * cna_ssao_pass_reset_targets (engine_layer.h).
     */
    public static native int ssaoPassResetTargets(long pass);

    /**
     * cna_ssao_pass_sample_count_for_quality (engine_layer.h).
     */
    public static native int ssaoPassSampleCountForQuality(int quality, int[] outCount);

    /**
     * cna_ssao_pass_set_half_resolution (engine_layer.h).
     */
    public static native int ssaoPassSetHalfResolution(long pass, boolean value);

    /**
     * cna_ssao_pass_set_intensity (engine_layer.h).
     */
    public static native int ssaoPassSetIntensity(long pass, float value);

    /**
     * cna_ssao_pass_set_radius (engine_layer.h).
     */
    public static native int ssaoPassSetRadius(long pass, float value);

    /**
     * cna_ssao_pass_set_sample_count (engine_layer.h).
     */
    public static native int ssaoPassSetSampleCount(long pass, int value);

    /**
     * cna_ssr_pass_create (engine_layer.h).
     */
    public static native int ssrPassCreate(long graphicsDevice, long[] outPass);

    /**
     * cna_ssr_pass_get_depth_bias (engine_layer.h).
     */
    public static native int ssrPassGetDepthBias(long pass, float[] outValue);

    /**
     * cna_ssr_pass_get_edge_fade (engine_layer.h).
     */
    public static native int ssrPassGetEdgeFade(long pass, float[] outValue);

    /**
     * cna_ssr_pass_get_intensity (engine_layer.h).
     */
    public static native int ssrPassGetIntensity(long pass, float[] outValue);

    /**
     * cna_ssr_pass_get_max_distance (engine_layer.h).
     */
    public static native int ssrPassGetMaxDistance(long pass, float[] outValue);

    /**
     * cna_ssr_pass_get_roughness_blur (engine_layer.h).
     */
    public static native int ssrPassGetRoughnessBlur(long pass, float[] outValue);

    /**
     * cna_ssr_pass_get_step_count (engine_layer.h).
     */
    public static native int ssrPassGetStepCount(long pass, int[] outValue);

    /**
     * cna_ssr_pass_get_thickness (engine_layer.h).
     */
    public static native int ssrPassGetThickness(long pass, float[] outValue);

    /**
     * cna_ssr_pass_set_depth_bias (engine_layer.h).
     */
    public static native int ssrPassSetDepthBias(long pass, float value);

    /**
     * cna_ssr_pass_set_edge_fade (engine_layer.h).
     */
    public static native int ssrPassSetEdgeFade(long pass, float value);

    /**
     * cna_ssr_pass_set_intensity (engine_layer.h).
     */
    public static native int ssrPassSetIntensity(long pass, float value);

    /**
     * cna_ssr_pass_set_max_distance (engine_layer.h).
     */
    public static native int ssrPassSetMaxDistance(long pass, float value);

    /**
     * cna_ssr_pass_set_roughness_blur (engine_layer.h).
     */
    public static native int ssrPassSetRoughnessBlur(long pass, float value);

    /**
     * cna_ssr_pass_set_step_count (engine_layer.h).
     */
    public static native int ssrPassSetStepCount(long pass, int value);

    /**
     * cna_ssr_pass_set_thickness (engine_layer.h).
     */
    public static native int ssrPassSetThickness(long pass, float value);

    /**
     * cna_thin_film_iridescence_copy_glsl (engine_layer.h).
     */
    public static native int thinFilmIridescenceCopyGlsl(byte[] destination, long[] outBytes);

    /**
     * cna_thin_film_iridescence_evaluate (engine_layer.h).
     *
     * <p>baseF0Floating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     *
     * <p>outValueFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int thinFilmIridescenceEvaluate(float outsideIor, float filmIor, float cosTheta, float thicknessNm, float[] baseF0Floating, float[] outValueFloating);

    /**
     * cna_tonemap_pass_create (engine_layer.h).
     */
    public static native int tonemapPassCreate(long graphicsDevice, long[] outPass);

    /**
     * cna_tonemap_pass_get_deband_strength (engine_layer.h).
     */
    public static native int tonemapPassGetDebandStrength(long pass, float[] outValue);

    /**
     * cna_tonemap_pass_get_exposure (engine_layer.h).
     */
    public static native int tonemapPassGetExposure(long pass, float[] outValue);

    /**
     * cna_tonemap_pass_get_gamma (engine_layer.h).
     */
    public static native int tonemapPassGetGamma(long pass, float[] outValue);

    /**
     * cna_tonemap_pass_get_mode (engine_layer.h).
     */
    public static native int tonemapPassGetMode(long pass, int[] outMode);

    /**
     * cna_tonemap_pass_is_deband_enabled (engine_layer.h).
     */
    public static native int tonemapPassIsDebandEnabled(long pass, boolean[] outEnabled);

    /**
     * cna_tonemap_pass_set_deband_enabled (engine_layer.h).
     */
    public static native int tonemapPassSetDebandEnabled(long pass, boolean value);

    /**
     * cna_tonemap_pass_set_deband_strength (engine_layer.h).
     */
    public static native int tonemapPassSetDebandStrength(long pass, float value);

    /**
     * cna_tonemap_pass_set_exposure (engine_layer.h).
     */
    public static native int tonemapPassSetExposure(long pass, float value);

    /**
     * cna_tonemap_pass_set_gamma (engine_layer.h).
     */
    public static native int tonemapPassSetGamma(long pass, float value);

    /**
     * cna_tonemap_pass_set_mode (engine_layer.h).
     */
    public static native int tonemapPassSetMode(long pass, int mode);

    /**
     * cna_tonemap_pass_tonemap_channel (engine_layer.h).
     */
    public static native int tonemapPassTonemapChannel(int mode, float value, float exposure, float gamma, float[] outValue);

    /**
     * cna_volumetric_fog_pass_create (engine_layer.h).
     */
    public static native int volumetricFogPassCreate(long graphicsDevice, long[] outPass);

    /**
     * cna_volumetric_fog_pass_get_anisotropy (engine_layer.h).
     */
    public static native int volumetricFogPassGetAnisotropy(long pass, float[] outValue);

    /**
     * cna_volumetric_fog_pass_get_density (engine_layer.h).
     */
    public static native int volumetricFogPassGetDensity(long pass, float[] outValue);

    /**
     * cna_volumetric_fog_pass_get_range (engine_layer.h).
     */
    public static native int volumetricFogPassGetRange(long pass, float[] outValue);

    /**
     * cna_volumetric_fog_pass_set_anisotropy (engine_layer.h).
     */
    public static native int volumetricFogPassSetAnisotropy(long pass, float value);

    /**
     * cna_volumetric_fog_pass_set_density (engine_layer.h).
     */
    public static native int volumetricFogPassSetDensity(long pass, float value);

    /**
     * cna_volumetric_fog_pass_set_light (engine_layer.h).
     *
     * <p>lightDirectionFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     *
     * <p>lightColorFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int volumetricFogPassSetLight(long pass, long shadowMap, float[] lightDirectionFloating, float[] lightColorFloating);

    /**
     * cna_volumetric_fog_pass_set_range (engine_layer.h).
     */
    public static native int volumetricFogPassSetRange(long pass, float value);

    /**
     * cna_weighted_blended_transparency_begin (engine_layer.h).
     */
    public static native int weightedBlendedTransparencyBegin(long transparency, float farPlane);

    /**
     * cna_weighted_blended_transparency_copy_accumulation_glsl (engine_layer.h).
     */
    public static native int weightedBlendedTransparencyCopyAccumulationGlsl(byte[] destination, long[] outBytes);

    /**
     * cna_weighted_blended_transparency_copy_unsupported_reason (engine_layer.h).
     */
    public static native int weightedBlendedTransparencyCopyUnsupportedReason(long transparency, byte[] destination, long[] outBytes);

    /**
     * cna_weighted_blended_transparency_create (engine_layer.h).
     */
    public static native int weightedBlendedTransparencyCreate(long graphicsDevice, int width, int height, long[] outTransparency);

    /**
     * cna_weighted_blended_transparency_destroy (engine_layer.h).
     */
    public static native int weightedBlendedTransparencyDestroy(long transparency);

    /**
     * cna_weighted_blended_transparency_end (engine_layer.h).
     */
    public static native int weightedBlendedTransparencyEnd(long transparency);

    /**
     * cna_weighted_blended_transparency_is_accumulating (engine_layer.h).
     */
    public static native int weightedBlendedTransparencyIsAccumulating(long transparency, boolean[] outAccumulating);

    /**
     * cna_weighted_blended_transparency_is_supported (engine_layer.h).
     */
    public static native int weightedBlendedTransparencyIsSupported(long transparency, boolean[] outSupported);

    /**
     * cna_weighted_blended_transparency_resize (engine_layer.h).
     */
    public static native int weightedBlendedTransparencyResize(long transparency, int width, int height);

    /**
     * cna_weighted_blended_transparency_resolve (engine_layer.h).
     */
    public static native int weightedBlendedTransparencyResolve(long transparency, int width, int height);

    /**
     * cna_weighted_blended_transparency_weight (engine_layer.h).
     */
    public static native int weightedBlendedTransparencyWeight(float viewDepth, float alpha, float farPlane, float[] outWeight);
}
