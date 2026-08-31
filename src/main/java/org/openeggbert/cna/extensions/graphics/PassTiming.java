package org.openeggbert.cna.extensions.graphics;

import java.util.Objects;

/**
 * How long one named pass took on the GPU.
 *
 * <p>A CNA extension. {@link #sampleCount()} is zero when the pass has not been timed, which is
 * the honest answer on a renderer with no GPU timer -- see {@link GpuTimer} -- rather than a
 * fabricated zero milliseconds.
 *
 * @param name the pass's name
 * @param sampleCount how many samples the average is over; zero when the pass has not been timed
 * @param milliseconds mean milliseconds the pass took on the GPU
 */
public record PassTiming(String name, int sampleCount, double milliseconds) {

    /** @param name the pass's name, which is never null */
    public PassTiming {
        Objects.requireNonNull(name, "name");
    }

    /**
     * Reports whether this timing is a measurement rather than a placeholder.
     *
     * @return whether the pass has been timed at least once
     */
    public boolean isMeasured() {
        return sampleCount > 0;
    }
}
