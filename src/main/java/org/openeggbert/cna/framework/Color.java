package org.openeggbert.cna.framework;

/** A non-premultiplied RGBA color implemented entirely in Java. */
public record Color(int red, int green, int blue, int alpha) {

    /** The traditional XNA clear color. */
    public static final Color CORNFLOWER_BLUE = new Color(100, 149, 237, 255);

    /** Opaque white. */
    public static final Color WHITE = new Color(255, 255, 255, 255);

    /** Validates that all channels fit in an unsigned byte. */
    public Color {
        requireChannel(red, "red");
        requireChannel(green, "green");
        requireChannel(blue, "blue");
        requireChannel(alpha, "alpha");
    }

    private static void requireChannel(int value, String name) {
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException(name + " must be between 0 and 255");
        }
    }
}
