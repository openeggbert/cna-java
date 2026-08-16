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
        R = red;
        G = green;
        B = blue;
        A = alpha;
    }

    /** Converts this compatibility value to the CNA-native value. */
    public CNA.Framework.Color toCna() {
        return new CNA.Framework.Color(R, G, B, A);
    }
}
