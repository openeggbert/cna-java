package org.openeggbert.cna.extensions.graphics;

/**
 * The overall render-quality preset.
 *
 * <p>A CNA extension: XNA 4.0 has no counterpart. The constant names are CNA's own.
 */
public enum RenderQuality {
    Low,
    Medium,
    High,
    Ultra;

    static RenderQuality fromValue(long value) {
        RenderQuality[] values = values();
        if (value < 0 || value >= values.length) {
            throw new IllegalStateException("CNA reported RenderQuality " + value
                    + ", which this build has no constant for");
        }
        return values[(int) value];
    }
}
