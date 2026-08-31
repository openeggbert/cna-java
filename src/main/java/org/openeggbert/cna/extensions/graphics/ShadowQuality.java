package org.openeggbert.cna.extensions.graphics;

/**
 * The shadow-map quality preset.
 *
 * <p>A CNA extension: XNA 4.0 has no counterpart. The constant names are CNA's own.
 */
public enum ShadowQuality {
    Disabled,
    Low,
    Medium,
    High,
    Ultra;

    static ShadowQuality fromValue(long value) {
        ShadowQuality[] values = values();
        if (value < 0 || value >= values.length) {
            throw new IllegalStateException("CNA reported ShadowQuality " + value
                    + ", which this build has no constant for");
        }
        return values[(int) value];
    }
}
