package org.openeggbert.cna.extensions.content;

import org.openeggbert.cna.internal.generated.NativeCnbRoutes;

import java.util.Objects;

/** Encodes a whole asset as a {@code .cnb} file, using CNA's own writer for that asset. */
public final class Cnb {

    /** CNA's own result for a buffer that could not hold the answer. */
    private static final int RESULT_BUFFER_TOO_SMALL = 14;

    private Cnb() {
    }

    /**
     * Encodes a texture as a complete Texture2D {@code .cnb} file.
     *
     * <p>This is CNA's own encoder, so the bytes carry the schema and the version CNA's reader
     * expects. A fixture built any other way would only prove that a hand-written writer and a
     * hand-written reader agree with each other.
     *
     * @param texture the texture to encode
     * @param contentName the source content name to record
     * @return the whole file
     */
    public static byte[] encodeTexture2D(CnbTextureData texture, String contentName) {
        CnbExtension.requireAvailable();
        Objects.requireNonNull(texture, "texture");
        Objects.requireNonNull(contentName, "contentName");
        byte[] name = CnbExtension.utf8(contentName);
        long[] size = new long[1];
        // Ask with no buffer: CNA reports the size it needs and writes nothing, which is the
        // format's own two-call protocol rather than a guess at how big the file will be.
        int probe = NativeCnbRoutes.cnbEncodeTexture2d(
                texture.handle(), name, new byte[0], size);
        if (probe != RESULT_BUFFER_TOO_SMALL) {
            CnbExtension.check("Cnb.encodeTexture2D", probe);
        }
        byte[] destination = new byte[Math.toIntExact(size[0])];
        long[] written = new long[1];
        CnbExtension.check("Cnb.encodeTexture2D", NativeCnbRoutes
                .cnbEncodeTexture2d(texture.handle(), name, destination, written));
        if (written[0] == destination.length) {
            return destination;
        }
        byte[] exact = new byte[Math.toIntExact(written[0])];
        System.arraycopy(destination, 0, exact, 0, exact.length);
        return exact;
    }
}
