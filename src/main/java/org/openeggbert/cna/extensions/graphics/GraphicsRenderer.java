package org.openeggbert.cna.extensions.graphics;

import org.openeggbert.cna.internal.generated.NativeRuntimeExtensionRoutes;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Which renderers this build has, and which one it should try first.
 *
 * <p>A CNA extension with no XNA counterpart. CNA can be built with several renderers compiled in
 * and one chosen when the first graphics device is created; this is how a game asks what the
 * choices are and makes one. {@link RendererCapabilities} answers the neighbouring question --
 * what the renderer a device already has can do -- and needs a device to ask it. These routes need
 * nothing, and are meant to be called before one exists.
 *
 * <p><strong>Process-wide, and latched.</strong> The choice is not a property of a device or a
 * game, because it must be made before either exists. Once a renderer has been created the
 * selection is fixed and every setter here answers {@link ExtensionNotSupportedException}'s
 * neighbour, an {@link IllegalStateException}, rather than quietly doing nothing.
 *
 * <p><strong>Never name a renderer this build does not have.</strong> The API path is safe:
 * {@link #setPreferred} refuses an absent renderer with {@link IllegalStateException} and
 * {@link #isAvailable} answers the question without side effects. The environment path is not.
 * Setting {@code CNA_GRAPHICS_RENDERER} to a renderer this build was compiled without
 * <strong>aborts the process while the native library is loading</strong> -- before {@code main},
 * before any Java code runs, inside {@code System.loadLibrary} -- with a C++ exception that never
 * becomes a result code. Nothing in this class, or anywhere in Java, can guard that; it is
 * recorded as JAVA-UPSTREAM-017. Read {@link #available()} and choose through {@link #setPreferred}
 * instead of letting a user's environment variable reach the loader unchecked.
 *
 * <p><strong>Five of CNA's query routes are not projected here</strong>, because measurement found
 * them write-only: {@code get_available_count_ext} answers zero for a build with five renderers,
 * {@code get_is_latched_ext} answers the exact opposite of what it documents,
 * {@code get_selected_ext} and {@code get_active_ext} answer {@code UNKNOWN} even directly after a
 * successful set, and {@code get_current_type} answers {@code UNKNOWN} once a renderer exists.
 * That is JAVA-UPSTREAM-018, reproduced in {@code tools/native-abi/probes/renderer_selection.c}
 * with no Java in the picture. {@link #available()} therefore sizes its buffer with the
 * zero-capacity probe that every count/copy pair in this API supports, and the active renderer's
 * name comes from {@link RendererCapabilities#getRendererName}, which is correct.
 */
public final class GraphicsRenderer {

    /** CNA's own result for a buffer that could not hold the answer. */
    private static final int RESULT_BUFFER_TOO_SMALL = 14;

    /** CNA's own result for an operation attempted at the wrong time. */
    private static final int RESULT_INVALID_STATE = 3;

    private GraphicsRenderer() {
    }

    /**
     * Returns the renderers compiled into this build, in CNA's own order.
     *
     * <p>The first is the build's default -- the one a run that names nothing gets. Being
     * enumerated is not a promise that it will start: a renderer can be compiled in and still fail
     * its own availability probe on a particular machine, which is what
     * {@link GraphicsRendererFallback.Reason#ProbeUnavailable} records.
     *
     * @return the identities, in order, never empty
     * @throws ExtensionNotSupportedException when this build has no extended runtime layer
     */
    public static List<GraphicsRendererType> available() {
        GraphicsExtension.requireBackend();
        long[] count = new long[1];
        // A zero-capacity probe reports the count and writes nothing, so BUFFER_TOO_SMALL is the
        // expected answer rather than a failure. CNA's own count route answers zero here
        // (JAVA-UPSTREAM-018), which is why the probe rather than the count is what sizes this.
        int probe = NativeRuntimeExtensionRoutes
                .graphicsRendererCopyAvailableExt(new int[0], count);
        if (probe != RESULT_BUFFER_TOO_SMALL) {
            GraphicsExtension.check("GraphicsRenderer.available", probe);
        }
        int length = Math.toIntExact(count[0]);
        if (length == 0) {
            return Collections.emptyList();
        }
        int[] destination = new int[length];
        GraphicsExtension.check("GraphicsRenderer.available",
                NativeRuntimeExtensionRoutes.graphicsRendererCopyAvailableExt(destination, count));
        List<GraphicsRendererType> answer = new ArrayList<>(length);
        for (int index = 0; index < Math.toIntExact(count[0]); index++) {
            answer.add(GraphicsRendererType.fromValue(destination[index]));
        }
        return Collections.unmodifiableList(answer);
    }

    /**
     * Reports whether one renderer is compiled into this build.
     *
     * @param type the identity to ask about
     * @return whether this build has it
     * @throws ExtensionNotSupportedException when this build has no extended runtime layer
     */
    public static boolean isAvailable(GraphicsRendererType type) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(type, "type");
        boolean[] there = new boolean[1];
        GraphicsExtension.check("GraphicsRenderer.isAvailable",
                NativeRuntimeExtensionRoutes.graphicsRendererGetIsAvailableExt(type.toValue(),
                        there));
        return there[0];
    }

    /**
     * Returns the identity CNA parses a name into, or {@code null} when it recognises none.
     *
     * <p>Case-insensitive, and matched by CNA against the same spellings its build option accepts.
     * An unrecognised name is an answer rather than a failure, which is what makes this the right
     * thing to hand a string that came from a user.
     *
     * @param name the name to parse
     * @return the identity, or {@code null}
     * @throws ExtensionNotSupportedException when this build has no extended runtime layer
     */
    public static GraphicsRendererType parse(String name) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(name, "name");
        int[] type = new int[1];
        boolean[] recognized = new boolean[1];
        GraphicsExtension.check("GraphicsRenderer.parse",
                NativeRuntimeExtensionRoutes.graphicsRendererTryParseNameExt(
                        name.getBytes(StandardCharsets.UTF_8), type, recognized));
        return recognized[0] ? GraphicsRendererType.fromValue(type[0]) : null;
    }

    /**
     * Requests the renderer CNA should try first.
     *
     * @param type the identity to prefer
     * @throws IllegalStateException when a renderer has already been created, or when this build
     *         does not have that renderer and no fallback chain is configured
     * @throws ExtensionNotSupportedException when this build has no extended runtime layer
     */
    public static void setPreferred(GraphicsRendererType type) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(type, "type");
        checkSelection("GraphicsRenderer.setPreferred",
                NativeRuntimeExtensionRoutes.graphicsRendererSetPreferredExt(type.toValue()), type);
    }

    /**
     * Requests the renderer CNA should try first, by name.
     *
     * @param name the renderer's name, matched case-insensitively
     * @throws IllegalArgumentException when the name is not a renderer identity at all
     * @throws IllegalStateException when a renderer has already been created, or when this build
     *         does not have that renderer and no fallback chain is configured
     * @throws ExtensionNotSupportedException when this build has no extended runtime layer
     */
    public static void setPreferred(String name) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(name, "name");
        int result = NativeRuntimeExtensionRoutes.graphicsRendererSetPreferredByNameExt(
                name.getBytes(StandardCharsets.UTF_8));
        if (result == RESULT_INVALID_STATE) {
            throw new IllegalStateException("the renderer " + name
                    + " cannot be preferred: either a renderer has already been created, or this"
                    + " build does not have it -- this build has " + available());
        }
        GraphicsExtension.check("GraphicsRenderer.setPreferred", result);
    }

    /**
     * Sets the order CNA tries renderers in when the preferred one cannot be used.
     *
     * <p>Only consulted when {@link #setAutomaticFallback automatic fallback} is on, which it is
     * not by default: CNA fails rather than substituting a renderer a game did not ask for.
     *
     * @param types the identities in attempt order; an empty list clears the chain
     * @throws IllegalStateException when a renderer has already been created
     * @throws ExtensionNotSupportedException when this build has no extended runtime layer
     */
    public static void setFallbackChain(List<GraphicsRendererType> types) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(types, "types");
        int[] values = new int[types.size()];
        for (int index = 0; index < values.length; index++) {
            values[index] = Objects.requireNonNull(types.get(index), "types[" + index + "]")
                    .toValue();
        }
        checkSelection("GraphicsRenderer.setFallbackChain",
                NativeRuntimeExtensionRoutes.graphicsRendererSetFallbackChainExt(values), null);
    }

    /**
     * Enables or disables automatic fallback.
     *
     * @param enabled whether CNA may work down the chain when the preferred renderer fails
     * @throws IllegalStateException when a renderer has already been created
     * @throws ExtensionNotSupportedException when this build has no extended runtime layer
     */
    public static void setAutomaticFallback(boolean enabled) {
        GraphicsExtension.requireBackend();
        checkSelection("GraphicsRenderer.setAutomaticFallback",
                NativeRuntimeExtensionRoutes.graphicsRendererSetAutomaticFallbackExt(enabled),
                null);
    }

    /**
     * Reports whether automatic fallback is enabled.
     *
     * @return whether it is
     * @throws ExtensionNotSupportedException when this build has no extended runtime layer
     */
    public static boolean isAutomaticFallback() {
        GraphicsExtension.requireBackend();
        boolean[] enabled = new boolean[1];
        GraphicsExtension.check("GraphicsRenderer.isAutomaticFallback",
                NativeRuntimeExtensionRoutes.graphicsRendererGetAutomaticFallbackExt(enabled));
        return enabled[0];
    }

    /**
     * Returns the renderers CNA tried and passed over, oldest first.
     *
     * <p>Empty on a build whose first choice worked, which is the ordinary case.
     *
     * @return the history, which may be empty
     * @throws ExtensionNotSupportedException when this build has no extended runtime layer
     */
    public static List<GraphicsRendererFallback> getFallbackHistory() {
        GraphicsExtension.requireBackend();
        long[] count = new long[1];
        GraphicsExtension.check("GraphicsRenderer.getFallbackHistory",
                NativeRuntimeExtensionRoutes.graphicsRendererGetFallbackCountExt(count));
        int length = Math.toIntExact(count[0]);
        List<GraphicsRendererFallback> answer = new ArrayList<>(length);
        for (int index = 0; index < length; index++) {
            long[] record = new long[2];
            GraphicsExtension.check("GraphicsRenderer.getFallbackHistory",
                    NativeRuntimeExtensionRoutes.graphicsRendererGetFallbackAtExt(index, record));
            answer.add(new GraphicsRendererFallback(GraphicsRendererType.fromValue(record[0]),
                    GraphicsRendererFallback.Reason.fromValue(record[1]),
                    fallbackMessage(index)));
        }
        return Collections.unmodifiableList(answer);
    }

    /**
     * Returns the set of renderers this build has, for a membership test.
     *
     * @return the identities
     * @throws ExtensionNotSupportedException when this build has no extended runtime layer
     */
    public static Set<GraphicsRendererType> availableSet() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(available()));
    }

    /**
     * Returns the selection to its initial state.
     *
     * <p>CNA names this route "for tests" and means it: it un-latches the process-wide selection so
     * a suite can drive the family more than once. It does not destroy or rebuild a renderer that
     * already exists, so calling it in a running game leaves the selection saying one thing and the
     * device doing another.
     *
     * @throws ExtensionNotSupportedException when this build has no extended runtime layer
     */
    public static void resetSelectionForTests() {
        GraphicsExtension.requireBackend();
        GraphicsExtension.check("GraphicsRenderer.resetSelectionForTests",
                NativeRuntimeExtensionRoutes.graphicsRendererResetSelectionForTestsExt());
    }

    private static String fallbackMessage(int index) {
        long[] bytes = new long[1];
        GraphicsExtension.check("GraphicsRenderer.getFallbackHistory",
                NativeRuntimeExtensionRoutes.graphicsRendererFallbackGetMessageSizeExt(index,
                        bytes));
        int length = Math.toIntExact(bytes[0]);
        if (length == 0) {
            return "";
        }
        byte[] destination = new byte[length];
        GraphicsExtension.check("GraphicsRenderer.getFallbackHistory",
                NativeRuntimeExtensionRoutes.graphicsRendererFallbackCopyMessageExt(index,
                        destination, bytes));
        return new String(destination, 0, Math.toIntExact(bytes[0]), StandardCharsets.UTF_8);
    }

    /**
     * Returns CNA's stable name for one fallback reason.
     *
     * <p>Asked of CNA rather than taken from the enum's own {@code name()}, so a diagnostic prints
     * the spelling CNA uses in its own logs.
     *
     * @param reason the reason
     * @return CNA's name for it
     * @throws ExtensionNotSupportedException when this build has no extended runtime layer
     */
    public static String getReasonName(GraphicsRendererFallback.Reason reason) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(reason, "reason");
        long[] bytes = new long[1];
        GraphicsExtension.check("GraphicsRenderer.getReasonName",
                NativeRuntimeExtensionRoutes.graphicsRendererFallbackReasonGetNameSizeExt(
                        reason.toValue(), bytes));
        int length = Math.toIntExact(bytes[0]);
        if (length == 0) {
            return "";
        }
        byte[] destination = new byte[length];
        GraphicsExtension.check("GraphicsRenderer.getReasonName",
                NativeRuntimeExtensionRoutes.graphicsRendererFallbackReasonCopyNameExt(
                        reason.toValue(), destination, bytes));
        return new String(destination, 0, Math.toIntExact(bytes[0]), StandardCharsets.UTF_8);
    }

    /* CNA answers INVALID_STATE to two different questions here -- a selection that is already
       latched, and a renderer this build does not have -- and the difference matters enough to a
       caller to be worth saying out loud, so the message names both and lists what there is. */
    private static void checkSelection(String operation, int result, GraphicsRendererType type) {
        if (result == RESULT_INVALID_STATE) {
            throw new IllegalStateException(operation + " was refused: either a renderer has"
                    + " already been created, or this build does not have "
                    + (type == null ? "one of those renderers" : type.toString())
                    + " -- this build has " + available());
        }
        GraphicsExtension.check(operation, result);
    }
}
