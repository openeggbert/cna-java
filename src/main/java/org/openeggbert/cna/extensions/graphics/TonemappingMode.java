package org.openeggbert.cna.extensions.graphics;

/**
 * The tonemapping operator applied to an HDR frame.
 *
 * <p>A CNA extension: XNA 4.0 has no counterpart. The constant names are CNA's own.
 */
public enum TonemappingMode {
    None,
    Reinhard,
    Filmic,
    Aces,
    Uncharted2;

    static TonemappingMode fromValue(long value) {
        TonemappingMode[] values = values();
        if (value < 0 || value >= values.length) {
            throw new IllegalStateException("CNA reported TonemappingMode " + value
                    + ", which this build has no constant for");
        }
        return values[(int) value];
    }
}
