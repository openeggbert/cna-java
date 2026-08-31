package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.nio.charset.StandardCharsets;

/**
 * The colour a soap bubble is.
 *
 * <p>A CNA extension, and the arithmetic behind {@code KHR_materials_iridescence}: light
 * reflecting off the front and back of a very thin film interferes with itself, and which
 * wavelengths survive depends on the film's thickness and the angle it is seen at. That is why a
 * bubble's colour moves as you tilt your head, and why an oil slick is banded.
 *
 * <p>Pure, and exposed for the same reason every other shading helper here is: a game whose own
 * shader does this has to do it the way CNA does, and a tool that shows a material's response
 * needs the curve rather than a picture of it.
 */
public final class ThinFilmIridescence {

    /** CNA's own result for a buffer that could not hold the answer. */
    private static final int RESULT_BUFFER_TOO_SMALL = 14;

    private ThinFilmIridescence() {
    }

    /**
     * Evaluates thin-film iridescence for one viewing angle and film thickness.
     *
     * @param outsideIor the index of refraction of the medium the light comes from
     * @param filmIor the index of refraction of the film
     * @param cosTheta the cosine of the viewing angle; clamped to zero-to-one
     * @param thicknessNanometres the film thickness in nanometres
     * @param baseF0 the base reflectance at normal incidence
     * @return the result, whose channels are floored at zero
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static Vector3 evaluate(float outsideIor, float filmIor, float cosTheta,
            float thicknessNanometres, Vector3 baseF0) {
        GraphicsExtension.requireBackend();
        float[] value = new float[3];
        GraphicsExtension.check("ThinFilmIridescence.evaluate",
                NativeEngineLayerRoutes.thinFilmIridescenceEvaluate(outsideIor, filmIor,
                        cosTheta, thicknessNanometres,
                        EngineValues.floats(baseF0, "baseF0"), value));
        return new Vector3(value[0], value[1], value[2]);
    }

    /**
     * Returns the GLSL a shader evaluates the same film with.
     *
     * @return the GLSL
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static String getGlsl() {
        GraphicsExtension.requireBackend();
        long[] bytes = new long[1];
        int probe = NativeEngineLayerRoutes.thinFilmIridescenceCopyGlsl(new byte[0], bytes);
        if (probe != RESULT_BUFFER_TOO_SMALL) {
            GraphicsExtension.check("ThinFilmIridescence.getGlsl", probe);
        }
        int length = Math.toIntExact(bytes[0]);
        if (length == 0) {
            return "";
        }
        byte[] destination = new byte[length];
        GraphicsExtension.check("ThinFilmIridescence.getGlsl",
                NativeEngineLayerRoutes.thinFilmIridescenceCopyGlsl(destination, bytes));
        return new String(destination, 0, Math.toIntExact(bytes[0]), StandardCharsets.UTF_8);
    }
}
