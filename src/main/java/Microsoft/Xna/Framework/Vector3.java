package Microsoft.Xna.Framework;

import java.util.Objects;

/** Mutable three-dimensional XNA value. */
public final class Vector3 {

    public float X;
    public float Y;
    public float Z;

    public Vector3() {
    }

    public Vector3(float value) {
        this(value, value, value);
    }

    public Vector3(float x, float y, float z) {
        X = x;
        Y = y;
        Z = z;
    }

    public Vector3(Vector2 value, float z) {
        this(Objects.requireNonNull(value, "value").X, value.Y, z);
    }

    public Vector3(Vector3 value) {
        this(Objects.requireNonNull(value, "value").X, value.Y, value.Z);
    }

    public static Vector3 getZero() { return new Vector3(); }
    public static Vector3 getOne() { return new Vector3(1.0f); }
    public static Vector3 getUnitX() { return new Vector3(1.0f, 0.0f, 0.0f); }
    public static Vector3 getUnitY() { return new Vector3(0.0f, 1.0f, 0.0f); }
    public static Vector3 getUnitZ() { return new Vector3(0.0f, 0.0f, 1.0f); }
    public static Vector3 getUp() { return new Vector3(0.0f, 1.0f, 0.0f); }
    public static Vector3 getDown() { return new Vector3(0.0f, -1.0f, 0.0f); }
    public static Vector3 getRight() { return new Vector3(1.0f, 0.0f, 0.0f); }
    public static Vector3 getLeft() { return new Vector3(-1.0f, 0.0f, 0.0f); }
    public static Vector3 getForward() { return new Vector3(0.0f, 0.0f, -1.0f); }
    public static Vector3 getBackward() { return new Vector3(0.0f, 0.0f, 1.0f); }

    public float Length() { return (float)Math.sqrt(LengthSquared()); }
    public float LengthSquared() { return (X * X) + (Y * Y) + (Z * Z); }

    public void Normalize() {
        float factor = 1.0f / (float)Math.sqrt(LengthSquared());
        X *= factor;
        Y *= factor;
        Z *= factor;
    }

    public static Vector3 Normalize(Vector3 value) {
        Vector3 result = new Vector3(value);
        result.Normalize();
        return result;
    }

    public static Vector3 Add(Vector3 value1, Vector3 value2) {
        return new Vector3(value1.X + value2.X, value1.Y + value2.Y, value1.Z + value2.Z);
    }

    public static Vector3 Subtract(Vector3 value1, Vector3 value2) {
        return new Vector3(value1.X - value2.X, value1.Y - value2.Y, value1.Z - value2.Z);
    }

    public static Vector3 Multiply(Vector3 value1, Vector3 value2) {
        return new Vector3(value1.X * value2.X, value1.Y * value2.Y, value1.Z * value2.Z);
    }

    public static Vector3 Multiply(Vector3 value, float scaleFactor) {
        return new Vector3(value.X * scaleFactor, value.Y * scaleFactor, value.Z * scaleFactor);
    }

    public static Vector3 Divide(Vector3 value, float divider) {
        return new Vector3(value.X / divider, value.Y / divider, value.Z / divider);
    }

    public static Vector3 Negate(Vector3 value) {
        return new Vector3(-value.X, -value.Y, -value.Z);
    }

    public static float Dot(Vector3 vector1, Vector3 vector2) {
        return (vector1.X * vector2.X) + (vector1.Y * vector2.Y) + (vector1.Z * vector2.Z);
    }

    public static Vector3 Cross(Vector3 vector1, Vector3 vector2) {
        return new Vector3(
                (vector1.Y * vector2.Z) - (vector1.Z * vector2.Y),
                (vector1.Z * vector2.X) - (vector1.X * vector2.Z),
                (vector1.X * vector2.Y) - (vector1.Y * vector2.X));
    }

    public static float Distance(Vector3 value1, Vector3 value2) { return Subtract(value1, value2).Length(); }
    public static float DistanceSquared(Vector3 value1, Vector3 value2) { return Subtract(value1, value2).LengthSquared(); }

    public static Vector3 Lerp(Vector3 value1, Vector3 value2, float amount) {
        return new Vector3(
                MathHelper.Lerp(value1.X, value2.X, amount),
                MathHelper.Lerp(value1.Y, value2.Y, amount),
                MathHelper.Lerp(value1.Z, value2.Z, amount));
    }

    public static Vector3 Min(Vector3 value1, Vector3 value2) {
        return new Vector3(Math.min(value1.X, value2.X), Math.min(value1.Y, value2.Y), Math.min(value1.Z, value2.Z));
    }

    public static Vector3 Max(Vector3 value1, Vector3 value2) {
        return new Vector3(Math.max(value1.X, value2.X), Math.max(value1.Y, value2.Y), Math.max(value1.Z, value2.Z));
    }

    public static Vector3 Clamp(Vector3 value1, Vector3 min, Vector3 max) {
        return Min(Max(value1, min), max);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof Vector3 value
                && FloatSemantics.equals(X, value.X)
                && FloatSemantics.equals(Y, value.Y)
                && FloatSemantics.equals(Z, value.Z);
    }

    @Override
    public int hashCode() { return FloatSemantics.hash(X) + FloatSemantics.hash(Y) + FloatSemantics.hash(Z); }

    @Override
    public String toString() { return "{X:" + X + " Y:" + Y + " Z:" + Z + '}'; }
}
