package org.openeggbert.cna.extensions.content;

import org.openeggbert.cna.internal.generated.NativeCnbRoutes;

import java.nio.charset.StandardCharsets;

/**
 * How a compiled sound's samples are encoded inside a {@code .cnb} file.
 *
 * <p><strong>The numbers are wire format and frozen, and most of them have no codec.</strong>
 * CNA's v1 container reserves five identifiers and implements exactly one of them,
 * {@link #Pcm16}. The other four are names a future writer may use; a file declaring one today is
 * refused at encode time rather than silently mis-decoded, which is why {@link #hasCodec()} is a
 * question worth asking before building a sound around a format.
 */
public enum CnbAudioFormat {

    /** Not a valid format; a file declaring it is rejected. */
    Unknown,

    /** Signed 16-bit little-endian PCM: the portable baseline and CNA's native form. */
    Pcm16,

    /** Unsigned 8-bit PCM. Identifier reserved; no v1 codec. */
    Pcm8,

    /** 32-bit float PCM. Identifier reserved; no v1 codec. */
    PcmFloat32,

    /** IMA/MS ADPCM. Identifier reserved; no v1 codec. */
    Adpcm,

    /** Vorbis in an Ogg container. Identifier reserved; no v1 codec. */
    Vorbis;

    /**
     * Reports whether CNA's v1 container can actually encode and decode this format.
     *
     * <p>This is a statement about the container version, not about the host's audio hardware:
     * the four reserved identifiers have no codec in any v1 build, so the answer does not change
     * between machines.
     *
     * @return true only for {@link #Pcm16}
     */
    public boolean hasCodec() {
        return this == Pcm16;
    }

    /**
     * Returns how many bytes one sample frame occupies, or zero when the format has no v1 codec.
     *
     * @param channels the channel count the sound declares
     * @return the frame size in bytes
     */
    public int getFrameByteSize(int channels) {
        if (channels <= 0) {
            throw new IllegalArgumentException("channels must be positive");
        }
        return this == Pcm16 ? 2 * channels : 0;
    }

    /** Returns CNA's own diagnostic name for the format. */
    public String getName() {
        long[] bytes = new long[1];
        CnbExtension.check("CnbAudioFormat.getName",
                NativeCnbRoutes.cnbGetAudioFormatNameSize(ordinal(), bytes));
        byte[] destination = new byte[(int) bytes[0]];
        CnbExtension.check("CnbAudioFormat.getName",
                NativeCnbRoutes.cnbCopyAudioFormatName(ordinal(), destination, bytes));
        return new String(destination, 0, (int) bytes[0], StandardCharsets.UTF_8);
    }

    /** Returns the format one wire value names, refusing a value this build has no constant for. */
    static CnbAudioFormat fromValue(long value) {
        CnbAudioFormat[] values = values();
        if (value < 0 || value >= values.length) {
            throw new CnbFormatException(
                    "the file names audio format " + value + ", which this build has no constant "
                    + "for; the numbers are wire format, so this is a newer writer");
        }
        return values[(int) value];
    }
}
