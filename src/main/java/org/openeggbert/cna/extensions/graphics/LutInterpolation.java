package org.openeggbert.cna.extensions.graphics;

/**
 * How a colour-grading lookup table is interpolated between its entries.
 *
 * <p>A CNA extension: XNA 4.0 has no counterpart. The constant names and their order are CNA's
 * own.
 */
public enum LutInterpolation {

    /** Eight-corner interpolation, the classic and the cheaper of the two. */
    Trilinear,

    /** Four-corner interpolation across the cube's tetrahedra, which colourists prefer. */
    Tetrahedral;

    static LutInterpolation fromValue(long value) {
        LutInterpolation[] values = values();
        if (value < 0 || value >= values.length) {
            throw new IllegalStateException("CNA reported LUT interpolation " + value
                    + ", which this build has no constant for");
        }
        return values[(int) value];
    }
}
