package org.openeggbert.cna.extensions.content;

import org.openeggbert.cna.internal.generated.NativeCnbRoutes;

import java.util.Objects;

/** Encodes a whole asset as a {@code .cnb} file, using CNA's own writer for that asset. */
public final class Cnb {

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
        if (probe != CnbExtension.RESULT_BUFFER_TOO_SMALL) {
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

    /**
     * Encodes a sound as a complete SoundEffect {@code .cnb} file.
     *
     * <p>CNA's own encoder again, and it is stricter than the description: a loop region outside
     * the sound, a shape its samples do not fill, or a format the v1 container reserves but has
     * no codec for is refused here rather than written out for a reader to trip over.
     *
     * @param sound the sound to encode
     * @param contentName the source content name to record
     * @return the whole file
     * @throws CnbFormatException when the description and the samples disagree
     */
    public static byte[] encodeSoundEffect(CnbSoundEffectData sound, String contentName) {
        CnbExtension.requireAvailable();
        Objects.requireNonNull(sound, "sound");
        Objects.requireNonNull(contentName, "contentName");
        byte[] name = CnbExtension.utf8(contentName);
        long[] size = new long[1];
        int probe = NativeCnbRoutes.cnbEncodeSoundEffect(
                sound.handle(), name, new byte[0], size);
        if (probe != CnbExtension.RESULT_BUFFER_TOO_SMALL) {
            CnbExtension.check("Cnb.encodeSoundEffect", probe);
        }
        byte[] destination = new byte[Math.toIntExact(size[0])];
        long[] written = new long[1];
        CnbExtension.check("Cnb.encodeSoundEffect", NativeCnbRoutes
                .cnbEncodeSoundEffect(sound.handle(), name, destination, written));
        if (written[0] == destination.length) {
            return destination;
        }
        byte[] exact = new byte[Math.toIntExact(written[0])];
        System.arraycopy(destination, 0, exact, 0, exact.length);
        return exact;
    }
}
