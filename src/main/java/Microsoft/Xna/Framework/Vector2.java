package Microsoft.Xna.Framework;

import java.util.Objects;

/** Mutable two-dimensional XNA value; Java assignments alias, so APIs snapshot retained values. */
public final class Vector2 {

    public float X;
    public float Y;

    public Vector2() {
    }

    public Vector2(float value) {
        this(value, value);
    }

    public Vector2(float x, float y) {
        X = x;
        Y = y;
    }

    public Vector2(Vector2 value) {
        this(Objects.requireNonNull(value, "value").X, value.Y);
    }

    public static Vector2 getZero() {
        return new Vector2();
    }

    public static Vector2 getOne() {
        return new Vector2(1.0f);
    }

    public static Vector2 getUnitX() {
        return new Vector2(1.0f, 0.0f);
    }

    public static Vector2 getUnitY() {
        return new Vector2(0.0f, 1.0f);
    }

    public float Length() {
        return (float)Math.sqrt((X * X) + (Y * Y));
    }

    public float LengthSquared() {
        return (X * X) + (Y * Y);
    }

    public void Normalize() {
        float factor = 1.0f / (float)Math.sqrt((X * X) + (Y * Y));
        X *= factor;
        Y *= factor;
    }

    public static Vector2 Normalize(Vector2 value) {
        Vector2 result = new Vector2(value);
        result.Normalize();
        return result;
    }

    public static Vector2 Add(Vector2 value1, Vector2 value2) {
        return new Vector2(value1.X + value2.X, value1.Y + value2.Y);
    }

    public static Vector2 Subtract(Vector2 value1, Vector2 value2) {
        return new Vector2(value1.X - value2.X, value1.Y - value2.Y);
    }

    public static Vector2 Multiply(Vector2 value1, Vector2 value2) {
        return new Vector2(value1.X * value2.X, value1.Y * value2.Y);
    }

    public static Vector2 Multiply(Vector2 value, float scaleFactor) {
        return new Vector2(value.X * scaleFactor, value.Y * scaleFactor);
    }

    public static Vector2 Divide(Vector2 value1, Vector2 value2) {
        return new Vector2(value1.X / value2.X, value1.Y / value2.Y);
    }

    public static Vector2 Divide(Vector2 value1, float divider) {
        return new Vector2(value1.X / divider, value1.Y / divider);
    }

    public static Vector2 Negate(Vector2 value) {
        return new Vector2(-value.X, -value.Y);
    }

    public static float Distance(Vector2 value1, Vector2 value2) {
        return Subtract(value1, value2).Length();
    }

    public static float DistanceSquared(Vector2 value1, Vector2 value2) {
        return Subtract(value1, value2).LengthSquared();
    }

    public static float Dot(Vector2 value1, Vector2 value2) {
        return (value1.X * value2.X) + (value1.Y * value2.Y);
    }

    public static Vector2 Lerp(Vector2 value1, Vector2 value2, float amount) {
        return new Vector2(
                value1.X + ((value2.X - value1.X) * amount),
                value1.Y + ((value2.Y - value1.Y) * amount));
    }

    public static Vector2 Clamp(Vector2 value1, Vector2 min, Vector2 max) {
        return Min(Max(value1, min), max);
    }

    public static Vector2 Min(Vector2 value1, Vector2 value2) {
        return new Vector2(Math.min(value1.X, value2.X), Math.min(value1.Y, value2.Y));
    }

    public static Vector2 Max(Vector2 value1, Vector2 value2) {
        return new Vector2(Math.max(value1.X, value2.X), Math.max(value1.Y, value2.Y));
    }

    public static Vector2 Reflect(Vector2 vector, Vector2 normal) {
        float factor = 2.0f * Dot(vector, normal);
        return new Vector2(vector.X - (factor * normal.X), vector.Y - (factor * normal.Y));
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof Vector2 value
                && FloatSemantics.equals(X, value.X) && FloatSemantics.equals(Y, value.Y);
    }

    @Override
    public int hashCode() {
        return FloatSemantics.hash(X) + FloatSemantics.hash(Y);
    }

    @Override
    public String toString() {
        return "{X:" + X + " Y:" + Y + '}';
    }
}
