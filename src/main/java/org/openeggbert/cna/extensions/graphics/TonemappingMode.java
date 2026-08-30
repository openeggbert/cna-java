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
}
