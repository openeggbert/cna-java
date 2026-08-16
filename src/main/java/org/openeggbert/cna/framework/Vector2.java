package org.openeggbert.cna.framework;

/** A two-dimensional vector implemented entirely in Java. */
public record Vector2(float x, float y) {

    /** Returns the component-wise sum of this vector and {@code other}. */
    public Vector2 add(Vector2 other) {
        return new Vector2(x + other.x, y + other.y);
    }

    /** Returns this vector multiplied by {@code scale}. */
    public Vector2 scale(float scale) {
        return new Vector2(x * scale, y * scale);
    }

    /** Returns the squared Euclidean length. */
    public float lengthSquared() {
        return x * x + y * y;
    }
}
