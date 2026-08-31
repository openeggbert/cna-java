package org.openeggbert.cna.extensions.graphics;

/**
 * How CNA's extended pipeline draws transparent geometry.
 *
 * <p>The numbers are CNA's own. Sorting is the classic answer and is wrong whenever two
 * transparent surfaces interpenetrate; order-independent blending is right in more cases and
 * costs more, which is the trade a game makes here rather than one CNA makes for it.
 */
public enum TransparencyMode {

    /** Transparent geometry is not drawn as a separate pass. */
    None,

    /** Drawn back to front, sorted per object. */
    Sorted,

    /** Blended without sorting, so interpenetrating surfaces are correct. */
    OrderIndependent;

    static TransparencyMode fromValue(long value) {
        TransparencyMode[] values = values();
        if (value < 0 || value >= values.length) {
            throw new IllegalStateException("CNA reported transparency mode " + value
                    + ", which this build has no constant for");
        }
        return values[(int) value];
    }
}
