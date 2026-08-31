package org.openeggbert.cna.extensions.graphics;

/**
 * The colour space a display expects a frame in.
 *
 * <p>A CNA extension: XNA presents sRGB and nothing else. The constant names and their order are
 * CNA's own.
 */
public enum DisplayColorSpace {

    /** Ordinary eight-bit sRGB, which is what XNA always presented. */
    Srgb,

    /** Extended-range linear, where values above one and below zero are meaningful. */
    Scrgb,

    /** Rec.2020 primaries with the PQ transfer function: what an HDR10 television takes. */
    Hdr10;

    static DisplayColorSpace fromValue(long value) {
        DisplayColorSpace[] values = values();
        if (value < 0 || value >= values.length) {
            throw new IllegalStateException("CNA reported display colour space " + value
                    + ", which this build has no constant for");
        }
        return values[(int) value];
    }
}
