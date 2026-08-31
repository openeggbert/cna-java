package org.openeggbert.cna.extensions.graphics;

/**
 * Which kind of light a {@link PunctualLight} slot holds.
 *
 * <p>A CNA extension: XNA 4.0 has no counterpart. The constant names and their order are CNA's
 * own.
 */
public enum PunctualLightKind {

    /** The slot is unused. */
    None,

    /** A point light, whose shadow is a cube. */
    Point,

    /** A spot light, whose shadow is a map. */
    Spot;

    static PunctualLightKind fromValue(long value) {
        PunctualLightKind[] values = values();
        if (value < 0 || value >= values.length) {
            throw new IllegalStateException("CNA reported punctual light kind " + value
                    + ", which this build has no constant for");
        }
        return values[(int) value];
    }
}
