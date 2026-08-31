package org.openeggbert.cna.extensions.graphics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The GPU timer, against the live runtime.
 *
 * <p><strong>What this can and cannot say.</strong> The timer needs a real graphics device, so it
 * runs inside a game. On this configuration the HEADLESS renderer supplies <em>no timer query</em>
 * -- NOT_SUPPORTED_BY_RENDERER -- and that is the state these tests qualify: not by skipping, but
 * by checking that the unsupported timer behaves exactly the way CNA documents an unsupported
 * timer behaves. No timing value is invented anywhere here.
 *
 * <p>The tests are written to hold on a renderer that <em>does</em> supply one, so the same suite
 * measures a real GPU when it is run against one. Where the two paths differ they branch on
 * {@link GpuTimer#isSupported()} rather than assuming.
 */
@EnabledIfEnvironmentVariable(named = "CNA_NATIVE_LIBRARY", matches = ".+")
final class GpuTimerTests {

    @Test
    void anUnsupportedTimerStillExistsAndSaysWhy() {
        GameProbe.run(probe -> {
            try (GpuTimer timer = GpuTimer.create(probe.device())) {
                // Creation succeeding is not evidence of support, and CNA says so explicitly.
                // A profiling overlay branches on this, not on whether the object exists.
                boolean supported = timer.isSupported();
                String reason = timer.getUnsupportedReason();
                if (supported) {
                    assertEquals("", reason, "a supported timer reports no reason");
                } else {
                    assertFalse(reason.isBlank(),
                            "an unsupported timer must say why, and it said: " + reason);
                    // The reason is the renderer's own sentence, not a constant this projection
                    // made up, so the only thing worth asserting about its content is that it
                    // is a sentence rather than a code.
                    assertTrue(reason.length() > 8, reason);
                }
            }
        });
    }

    @Test
    void anUnsupportedTimerLetsTheCallsStayInTheFrame() {
        GameProbe.run(probe -> {
            try (GpuTimer timer = GpuTimer.create(probe.device())) {
                assertFalse(timer.isOpen(), "a new timer has no range open");

                timer.begin();
                if (timer.isSupported()) {
                    assertTrue(timer.isOpen(), "a supported timer opens the range");
                } else {
                    // The point of the whole design: on a renderer with no query, begin and end
                    // do nothing rather than failing, so a game leaves them in the frame and
                    // needs no build-time switch.
                    assertFalse(timer.isOpen(), "an unsupported timer opens nothing");
                }
                timer.end();
                assertFalse(timer.isOpen(), "end closes whatever begin opened");

                // And again, because "does nothing when already open" and "does nothing when not
                // open" are two separate rules and both have to survive being exercised.
                timer.end();
                timer.begin();
                timer.begin();
                timer.end();
                assertFalse(timer.isOpen());
            }
        });
    }

    @Test
    void nothingIsReportedBeforeAResultIsCollected() {
        GameProbe.run(probe -> {
            try (GpuTimer timer = GpuTimer.create(probe.device())) {
                assertEquals(0, timer.getSampleCount(), "no result has been collected");
                assertEquals(0.0, timer.getLastMilliseconds(),
                        "zero before the first result, not a made-up number");

                timer.begin();
                timer.end();
                if (!timer.isSupported()) {
                    // Nothing was measured, so nothing can be collected and nothing can be
                    // reported. A timer that answered otherwise here would be inventing a value.
                    assertFalse(timer.isResultAvailable());
                    assertFalse(timer.poll(), "there is nothing to collect");
                    assertEquals(0, timer.getSampleCount());
                    assertEquals(0.0, timer.getLastMilliseconds());
                } else {
                    // A real query answers whenever the GPU gets to it, which may be several
                    // frames later, so the only safe statement is that polling is consistent
                    // with availability and that a collected sample is counted.
                    boolean available = timer.isResultAvailable();
                    boolean collected = timer.poll();
                    if (collected) {
                        assertTrue(available || timer.getSampleCount() > 0);
                        assertEquals(1, timer.getSampleCount());
                        assertTrue(timer.getLastMilliseconds() >= 0.0);
                    }
                }
            }
        });
    }

    @Test
    void aClosedTimerRefusesEveryOperation() {
        GameProbe.run(probe -> {
            GpuTimer timer = GpuTimer.create(probe.device());
            timer.close();
            timer.close();
            assertThrows(IllegalStateException.class, timer::isSupported);
            assertThrows(IllegalStateException.class, timer::begin);
            assertThrows(IllegalStateException.class, timer::getUnsupportedReason);
            assertThrows(NullPointerException.class, () -> GpuTimer.create(null));
        });
    }
}
