package org.openeggbert.cna.framework;

import java.time.Duration;
import java.util.Objects;

/** Timing information supplied to one update or draw callback. */
public record GameTime(Duration totalGameTime, Duration elapsedGameTime, boolean runningSlowly) {

    /** Rejects missing duration values. */
    public GameTime {
        Objects.requireNonNull(totalGameTime, "totalGameTime");
        Objects.requireNonNull(elapsedGameTime, "elapsedGameTime");
    }
}
