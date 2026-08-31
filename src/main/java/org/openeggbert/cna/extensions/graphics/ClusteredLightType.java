package org.openeggbert.cna.extensions.graphics;

/**
 * Which kind of light a {@link ClusteredLight} is.
 *
 * <p>A CNA extension: XNA 4.0 has no counterpart. The constant names and their order are CNA's
 * own.
 */
public enum ClusteredLightType {

    /** Radiates in every direction from its position, out to its range. */
    Point,

    /** A point light restricted to the cone its direction and angles describe. */
    Spot;

    static ClusteredLightType fromValue(long value) {
        ClusteredLightType[] values = values();
        if (value < 0 || value >= values.length) {
            throw new IllegalStateException("CNA reported clustered light type " + value
                    + ", which this build has no constant for");
        }
        return values[(int) value];
    }
}
