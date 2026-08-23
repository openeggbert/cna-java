package Microsoft.Xna.Framework.Media;

/** Semantic role of a video's audio track. */
public enum VideoSoundtrackType {
    Music(0),
    Dialog(1),
    MusicAndDialog(2);

    private final int value;

    VideoSoundtrackType(int value) { this.value = value; }

    static VideoSoundtrackType fromValue(int value) {
        for (VideoSoundtrackType type : values()) if (type.value == value) return type;
        throw new IllegalStateException("CNA returned unknown VideoSoundtrackType " + value);
    }
}
