package org.openeggbert.cna.extensions.graphics;

import org.openeggbert.cna.extensions.runtime.GraphicsBackendCategory;
import org.openeggbert.cna.extensions.runtime.GraphicsBackendMaturity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Which renderers this build has, and what happens when a game asks for one it does not.
 *
 * <p>This suite exists because the qualification that produced it walked into the answer: a sweep
 * named a renderer the library was configured without, and the JVM died with SIGABRT inside
 * {@code System.loadLibrary} before a line of Java ran. Nothing here can test that -- a test that
 * kills its own JVM reports nothing -- so it is reproduced in
 * {@code tools/native-abi/probes/renderer_selection.c} and recorded as JAVA-UPSTREAM-017. What
 * this suite tests is the path that exists precisely so a game never has to find out that way:
 * ask what is here, and choose through the API, where a bad choice is a refusal.
 *
 * <p><strong>Every test here latches the selection first.</strong> The selection is process-wide
 * and fixed once a renderer exists, so a suite that touched a setter before some other suite had
 * created its device could change which renderer the whole run measured. Creating a device first
 * makes the refusals the assertions want, and makes them for the right reason.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class GraphicsRendererSelectionTests {

    /** Creates a device, which is what fixes the process-wide selection. */
    private static void latch() {
        GameProbe.run(probe -> assertNotNull(probe.device()));
    }

    @Test
    void thisBuildEnumeratesTheRenderersItHas() {
        latch();
        List<GraphicsRendererType> available = GraphicsRenderer.available();
        assertFalse(available.isEmpty(),
                "a build that produced a graphics device has at least one renderer");
        assertEquals(available.size(), Set.copyOf(available).size(),
                "the inventory names each renderer once");
        assertFalse(available.contains(GraphicsRendererType.UNKNOWN),
                "UNKNOWN names nothing and must not be enumerated");
    }

    @Test
    void theRendererTheRunAskedForIsOneThisBuildHas() {
        latch();
        String asked = System.getenv("CNA_GRAPHICS_RENDERER");
        if (asked == null || asked.isEmpty()) {
            return;
        }
        GraphicsRendererType type = GraphicsRenderer.parse(asked);
        assertNotNull(type, asked + " must be a renderer identity CNA recognises");
        assertTrue(GraphicsRenderer.available().contains(type),
                "the run asked for " + asked + ", so this build must have it -- and if it did not,"
                        + " this process would have aborted while loading rather than reaching"
                        + " this assertion");
    }

    @Test
    void isAvailableAgreesWithTheInventoryForEveryIdentity() {
        latch();
        Set<GraphicsRendererType> inventory = GraphicsRenderer.availableSet();
        for (GraphicsRendererType type : GraphicsRendererType.values()) {
            if (type == GraphicsRendererType.UNKNOWN) {
                // UNKNOWN is not an identity CNA will answer about; it refuses instead.
                assertThrows(RuntimeException.class, () -> GraphicsRenderer.isAvailable(type),
                        "UNKNOWN is not a renderer to ask about");
                continue;
            }
            assertEquals(inventory.contains(type), GraphicsRenderer.isAvailable(type),
                    type + " must be reported the same way one at a time as in the inventory");
        }
    }

    @Test
    void namesParseCaseInsensitivelyAndNonsenseIsAnAnswer() {
        latch();
        for (GraphicsRendererType type : GraphicsRenderer.available()) {
            String name = type.name();
            assertEquals(type, GraphicsRenderer.parse(name), name + " must parse to itself");
            assertEquals(type, GraphicsRenderer.parse(name.toLowerCase(Locale.ROOT)),
                    name + " must parse case-insensitively");
        }
        // A renderer this build does not have still parses: parsing a name and having the
        // renderer are different questions, and conflating them is how a caller ends up asking
        // for something that is not there.
        assertEquals(GraphicsRendererType.VULKAN, GraphicsRenderer.parse("VULKAN"));
        assertNull(GraphicsRenderer.parse("NOT_A_RENDERER"));
        assertNull(GraphicsRenderer.parse(""));
    }

    @Test
    void aRendererThisBuildDoesNotHaveIsRefusedRatherThanAborting() {
        latch();
        Set<GraphicsRendererType> inventory = GraphicsRenderer.availableSet();
        GraphicsRendererType absent = EnumSet.allOf(GraphicsRendererType.class).stream()
                .filter(type -> type != GraphicsRendererType.UNKNOWN)
                .filter(type -> !inventory.contains(type))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "this build has every renderer CNA defines, which cannot happen"));
        // The whole point of the API path: this returns, and the process is still alive to assert
        // that it did. The environment path with the same identity does not (JAVA-UPSTREAM-017).
        IllegalStateException refused =
                assertThrows(IllegalStateException.class, () -> GraphicsRenderer.setPreferred(absent));
        assertTrue(refused.getMessage().contains(absent.toString()),
                "the refusal names the renderer that was asked for: " + refused.getMessage());
        assertTrue(refused.getMessage().contains(inventory.iterator().next().toString()),
                "and lists what there is instead: " + refused.getMessage());
        assertThrows(IllegalStateException.class,
                () -> GraphicsRenderer.setPreferred(absent.name()));
    }

    @Test
    void aNameThatIsNoRendererAtAllIsRefusedDifferently() {
        latch();
        // INVALID_ARGUMENT, not INVALID_STATE: "not a renderer" and "not in this build" are
        // different answers and CNA keeps them apart.
        assertThrows(RuntimeException.class,
                () -> GraphicsRenderer.setPreferred("NOT_A_RENDERER"));
    }

    @Test
    void theSelectionIsFixedOnceARendererExists() {
        latch();
        GraphicsRendererType present = GraphicsRenderer.available().get(0);
        assertThrows(IllegalStateException.class, () -> GraphicsRenderer.setPreferred(present),
                "a renderer this build has is still refused once the selection is latched");
        assertThrows(IllegalStateException.class,
                () -> GraphicsRenderer.setFallbackChain(List.of(present)));
        assertThrows(IllegalStateException.class,
                () -> GraphicsRenderer.setAutomaticFallback(true));
    }

    @Test
    void theActiveRendererIsTheOneTheRunAskedFor() {
        latch();
        GraphicsRendererType active = GraphicsRenderer.getActive();
        assertNotNull(active);
        assertTrue(GraphicsRenderer.available().contains(active),
                "a renderer that was created is one this build has");
        String asked = System.getenv("CNA_GRAPHICS_RENDERER");
        if (asked != null && !asked.isEmpty()) {
            assertEquals(GraphicsRenderer.parse(asked), active,
                    "the run asked for " + asked + " and no fallback chain is configured, so that"
                            + " is what it must be running");
        }
        // The routes with "current" in their names answer about the build's compile-time default
        // instead, which on a multi-renderer build is a different renderer -- JAVA-UPSTREAM-018.
        // The device-scoped name is the other route that is right, and it agrees with this one.
        GameProbe.run(probe -> assertEquals(active.name(),
                RendererCapabilities.getRendererName(probe.device()),
                "the selection and the device name the same renderer"));
    }

    @Test
    void theFallbackHistoryIsReadableAndEveryReasonHasCnaSName() {
        latch();
        List<GraphicsRendererFallback> history = GraphicsRenderer.getFallbackHistory();
        assertNotNull(history);
        for (GraphicsRendererFallback record : history) {
            assertNotNull(record.getType());
            assertNotNull(record.getReason());
            assertFalse(record.getMessage().isEmpty(),
                    "CNA documents that a fallback message is never empty");
        }
        // The reason names come from CNA rather than from the enum, so they are worth checking
        // against the spellings CNA actually uses.
        assertEquals("NotCompiledIn",
                GraphicsRenderer.getReasonName(GraphicsRendererFallback.Reason.NotCompiledIn));
        assertEquals("ProbeUnavailable",
                GraphicsRenderer.getReasonName(GraphicsRendererFallback.Reason.ProbeUnavailable));
        assertEquals("InitializationFailed",
                GraphicsRenderer.getReasonName(
                        GraphicsRendererFallback.Reason.InitializationFailed));
        assertEquals("WindowKindConflict",
                GraphicsRenderer.getReasonName(
                        GraphicsRendererFallback.Reason.WindowKindConflict));
    }

    @Test
    void everyIdentityHasACategoryAndAMaturityWhetherOrNotThisBuildHasIt() {
        latch();
        // Classified for any identity CNA defines, not only the compiled-in ones -- which is what
        // makes this pair worth having next to available(): a settings screen can say what a
        // renderer is, and how far CNA recommends it, without the build containing it.
        for (GraphicsRendererType type : GraphicsRendererType.values()) {
            if (type == GraphicsRendererType.UNKNOWN) {
                assertThrows(RuntimeException.class, () -> GraphicsRenderer.getCategory(type));
                continue;
            }
            assertNotNull(GraphicsRenderer.getCategory(type), type + " has a category");
            assertNotNull(GraphicsRenderer.getMaturity(type), type + " has a maturity");
        }
        // The identities this projection cares most about, stated rather than assumed. A renderer
        // that turned out to be classified DIAGNOSTIC would be one to stop qualifying against.
        assertEquals(GraphicsBackendCategory.Diagnostic,
                GraphicsRenderer.getCategory(GraphicsRendererType.HEADLESS));
        assertEquals(GraphicsBackendCategory.Software,
                GraphicsRenderer.getCategory(GraphicsRendererType.SOFTWARE));
        assertEquals(GraphicsBackendCategory.Native,
                GraphicsRenderer.getCategory(GraphicsRendererType.OPENGL33));
        assertNotEquals(GraphicsBackendMaturity.Historical,
                GraphicsRenderer.getMaturity(GraphicsRendererType.OPENGL33),
                "the renderer this qualification leans on is not a historical one");

        // And the per-identity answer for the renderer actually running. Deliberately NOT
        // compared against CnaRuntime.getBackendCategory(), which classifies the compile-time
        // default rather than the running renderer -- writing that comparison is what found it.
        GraphicsRendererType active = GraphicsRenderer.getActive();
        assertEquals(GraphicsRenderer.getCategory(active),
                GraphicsRenderer.getCategory(GraphicsRenderer.parse(active.name())),
                "the identity and its name classify the same way");
    }

    @Test
    void automaticFallbackIsOffUnlessSomethingTurnedItOn() {
        latch();
        // Read-only: CNA fails rather than substituting a renderer a game did not ask for, and a
        // qualification run wants exactly the renderer it named.
        assertFalse(GraphicsRenderer.isAutomaticFallback(),
                "automatic fallback must not be on by default -- a run that silently got another"
                        + " renderer would be measuring the wrong thing and saying so nowhere");
    }
}
