package org.openeggbert.cna.extensions.graphics;

/**
 * One entry of the area-light BRDF table.
 *
 * <p>The four numbers a shader needs to turn a lobe into a coverage weight: how much energy the
 * lobe carries, how much of it is Fresnel, and the average direction it points, split into its
 * tangent and normal components.
 *
 * @param magnitude total energy of the lobe
 * @param fresnel the Fresnel-weighted share of it
 * @param averageTangent tangent component of the average direction
 * @param averageNormal normal component of the average direction
 */
public record AreaLightBrdfTerms(float magnitude, float fresnel, float averageTangent,
        float averageNormal) {
}
