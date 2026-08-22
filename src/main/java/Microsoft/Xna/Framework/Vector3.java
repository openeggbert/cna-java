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

    public static Vector3 Add(Vector3 left, Vector3 right) {
        return new Vector3(left.X + right.X, left.Y + right.Y, left.Z + right.Z);
    }

    public static Vector3 Subtract(Vector3 left, Vector3 right) {
        return new Vector3(left.X - right.X, left.Y - right.Y, left.Z - right.Z);
    }

    public static Vector3 Multiply(Vector3 left, Vector3 right) {
        return new Vector3(left.X * right.X, left.Y * right.Y, left.Z * right.Z);
    }

    public static Vector3 Multiply(Vector3 value, float scale) {
        return new Vector3(value.X * scale, value.Y * scale, value.Z * scale);
    }

    public static Vector3 Divide(Vector3 value, float divider) {
        return new Vector3(value.X / divider, value.Y / divider, value.Z / divider);
    }

    public static Vector3 Negate(Vector3 value) {
        return new Vector3(-value.X, -value.Y, -value.Z);
    }

    public static float Dot(Vector3 left, Vector3 right) {
        return (left.X * right.X) + (left.Y * right.Y) + (left.Z * right.Z);
    }

    public static Vector3 Cross(Vector3 left, Vector3 right) {
        return new Vector3(
                (left.Y * right.Z) - (left.Z * right.Y),
                (left.Z * right.X) - (left.X * right.Z),
                (left.X * right.Y) - (left.Y * right.X));
    }

    public static float Distance(Vector3 left, Vector3 right) { return Subtract(left, right).Length(); }
    public static float DistanceSquared(Vector3 left, Vector3 right) { return Subtract(left, right).LengthSquared(); }

    public static Vector3 Lerp(Vector3 left, Vector3 right, float amount) {
        return new Vector3(
                MathHelper.Lerp(left.X, right.X, amount),
                MathHelper.Lerp(left.Y, right.Y, amount),
                MathHelper.Lerp(left.Z, right.Z, amount));
    }

    public static Vector3 Min(Vector3 left, Vector3 right) {
        return new Vector3(Math.min(left.X, right.X), Math.min(left.Y, right.Y), Math.min(left.Z, right.Z));
    }

    public static Vector3 Max(Vector3 left, Vector3 right) {
        return new Vector3(Math.max(left.X, right.X), Math.max(left.Y, right.Y), Math.max(left.Z, right.Z));
    }

    public static Vector3 Clamp(Vector3 value, Vector3 min, Vector3 max) {
        return Min(Max(value, min), max);
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof Vector3 value
                && FloatSemantics.equals(X, value.X)
                && FloatSemantics.equals(Y, value.Y)
                && FloatSemantics.equals(Z, value.Z);
    }

    @Override
    public int hashCode() { return FloatSemantics.hash(X) + FloatSemantics.hash(Y) + FloatSemantics.hash(Z); }

    @Override
    public String toString() { return "{X:" + X + " Y:" + Y + " Z:" + Z + '}'; }
}

