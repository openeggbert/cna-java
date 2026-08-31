package org.openeggbert.cna.extensions.graphics;

/**
 * How a compute shader may touch a texture bound as an image.
 *
 * <p>A CNA extension: XNA has no shader-writable texture at all. The constant order is CNA's own.
 */
public enum ImageAccess {

    /** The shader only reads the image. */
    ReadOnly,

    /** The shader only writes the image. */
    WriteOnly,

    /** The shader both reads and writes the image. */
    ReadWrite;

    int toValue() {
        return ordinal();
    }
}
