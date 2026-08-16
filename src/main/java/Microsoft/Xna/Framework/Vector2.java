package Microsoft.Xna.Framework;

/** XNA 4.0-compatible two-dimensional vector facade. */
public final class Vector2 {

    public float X;
    public float Y;

    /** Creates the zero vector. */
    public Vector2() {
    }

    /** Creates a vector from its components. */
    public Vector2(float x, float y) {
        X = x;
        Y = y;
    }

    /** Converts this compatibility value to the CNA-native value. */
    public CNA.Framework.Vector2 toCna() {
        return new CNA.Framework.Vector2(X, Y);
    }
}
