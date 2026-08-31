package org.openeggbert.cna.extensions.graphics;

/**
 * How a depth/normal prepass stores linear depth.
 *
 * <p>A CNA extension: XNA 4.0 has no counterpart. The constant names and their order are CNA's
 * own.
 */
public enum DepthEncoding {

    /** Let CNA choose whichever the renderer supports. */
    Automatic,

    /** Packed across four eight-bit channels, for a renderer with no float target. */
    Packed,

    /** A single half-float channel. */
    HalfFloat;

    static DepthEncoding fromValue(long value) {
        DepthEncoding[] values = values();
        if (value < 0 || value >= values.length) {
            throw new IllegalStateException("CNA reported depth encoding " + value
                    + ", which this build has no constant for");
        }
        return values[(int) value];
    }
}
