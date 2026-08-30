package org.openeggbert.cna.extensions.graphics;

import org.openeggbert.cna.internal.CnaNativeException;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeGraphicsExtensionRoutes;

/**
 * Reports whether this CNA build carries the extended graphics layer.
 *
 * <p>The layer is an opt-in CNA build option. Its declarations exist in every build so the
 * exported ABI never changes shape, and the routes that need a native extension object answer
 * {@code CNA_RESULT_NOT_SUPPORTED} when it is absent. That distinction is preserved rather than
 * flattened: {@link #isAvailable()} answers truthfully, and a route on a build without the layer
 * raises {@link ExtensionNotSupportedException} instead of quietly doing something else.
 */
public final class GraphicsExtension {

    private static final int RESULT_SUCCESS = 0;
    private static final int RESULT_NOT_SUPPORTED = 6;

    private GraphicsExtension() {
    }

    /**
     * Reports whether the loaded CNA build contains the extended graphics layer.
     *
     * <p>Answers {@code false} rather than failing when no native backend is loaded at all, so a
     * game can ask before it does anything native.
     */
    public static boolean isAvailable() {
        if (!NativeBindings.isAvailable()) {
            return false;
        }
        boolean[] available = new boolean[1];
        check("GraphicsExtension.isAvailable",
                NativeGraphicsExtensionRoutes.graphicsExtIsAvailable(available));
        return available[0];
    }

    /**
     * Loads the native bridge, so an external consumer can reach this package first.
     *
     * <p>Nothing else in it has loaded the bridge: a game may ask CNA about its extended layer
     * before it creates a {@code Game}, and every route here would otherwise fail to link.
     */
    static void requireBackend() {
        NativeBindings.requireAvailable();
    }

    /**
     * Maps one CNA result for the extension surface.
     *
     * <p>{@code NOT_SUPPORTED} keeps its own identity: a build without the extended layer is a
     * different thing from a call that failed, and a game that catches the first can fall back
     * without swallowing the second.
     */
    static void check(String operation, int result) {
        if (result == RESULT_SUCCESS) {
            return;
        }
        if (result == RESULT_NOT_SUPPORTED) {
            throw new ExtensionNotSupportedException(operation
                    + " needs CNA's extended graphics layer, which this build does not contain");
        }
        CnaNativeException failure = NativeBindings.failure(operation, result);
        throw failure;
    }
}
