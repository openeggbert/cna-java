package CNA.Framework;

/** A two-dimensional CNA vector implemented entirely in Java. */
public final class Vector2 {

    /** Horizontal component, matching the CNA/XNA public member name. */
    public float X;

    /** Vertical component, matching the CNA/XNA public member name. */
    public float Y;

    /** Creates the zero vector. */
    public Vector2() {
    }

    /** Creates a vector from its components. */
    public Vector2(float x, float y) {
        X = x;
        Y = y;
    }

    /** Returns the component-wise sum. */
    public Vector2 add(Vector2 other) {
        return new Vector2(X + other.X, Y + other.Y);
    }

    /** Returns the squared Euclidean length. */
    public float lengthSquared() {
        return X * X + Y * Y;
    }
}
