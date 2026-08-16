package Microsoft.Xna.Framework;

/** XNA 4.0-compatible RGBA color facade. */
public final class Color {

    public static final Color CornflowerBlue = new Color(100, 149, 237, 255);
    public static final Color White = new Color(255, 255, 255, 255);

    public final int R;
    public final int G;
    public final int B;
    public final int A;

    /** Creates an RGBA color. */
    public Color(int red, int green, int blue, int alpha) {
        R = requireChannel(red, "red");
        G = requireChannel(green, "green");
        B = requireChannel(blue, "blue");
        A = requireChannel(alpha, "alpha");
    }

    private static int requireChannel(int value, String name) {
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException(name + " must be between 0 and 255");
        }
        return value;
    }
}
