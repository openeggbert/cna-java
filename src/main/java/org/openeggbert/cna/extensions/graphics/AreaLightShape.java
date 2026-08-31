package org.openeggbert.cna.extensions.graphics;

/**
 * The shape an {@link AreaLight} emits from.
 *
 * <p>A CNA extension: XNA 4.0 has no counterpart. The constant names and their order are CNA's
 * own.
 */
public enum AreaLightShape {

    /** A flat rectangle, described by its centre and two half-axes. */
    Rectangle,

    /** A disc inscribed in the same rectangle. */
    Disc,

    /** A tube along the right axis, with the up axis as its radius. */
    Tube;

    static AreaLightShape fromValue(long value) {
        AreaLightShape[] values = values();
        if (value < 0 || value >= values.length) {
            throw new IllegalStateException("CNA reported area light shape " + value
                    + ", which this build has no constant for");
        }
        return values[(int) value];
    }
}
