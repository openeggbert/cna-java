package Microsoft.Xna.Framework;

import java.util.Objects;

/** Mutable four-dimensional XNA value. */
public final class Vector4 {

    public float X;
    public float Y;
    public float Z;
    public float W;

    public Vector4() {
    }

    public Vector4(float value) { this(value, value, value, value); }
    public Vector4(float x, float y, float z, float w) { X = x; Y = y; Z = z; W = w; }
    public Vector4(Vector2 value, float z, float w) { this(value.X, value.Y, z, w); }
    public Vector4(Vector3 value, float w) { this(value.X, value.Y, value.Z, w); }
    public Vector4(Vector4 value) { this(value.X, value.Y, value.Z, value.W); }

    public static Vector4 getZero() { return new Vector4(); }
    public static Vector4 getOne() { return new Vector4(1.0f); }
    public static Vector4 getUnitX() { return new Vector4(1.0f, 0.0f, 0.0f, 0.0f); }
    public static Vector4 getUnitY() { return new Vector4(0.0f, 1.0f, 0.0f, 0.0f); }
    public static Vector4 getUnitZ() { return new Vector4(0.0f, 0.0f, 1.0f, 0.0f); }
    public static Vector4 getUnitW() { return new Vector4(0.0f, 0.0f, 0.0f, 1.0f); }

    public float Length() { return (float)Math.sqrt(LengthSquared()); }
    public float LengthSquared() { return (X * X) + (Y * Y) + (Z * Z) + (W * W); }
    public void Normalize() { float factor = 1.0f / Length(); X *= factor; Y *= factor; Z *= factor; W *= factor; }
    public static Vector4 Normalize(Vector4 value) { Vector4 result = new Vector4(value); result.Normalize(); return result; }
    public static Vector4 Add(Vector4 left, Vector4 right) { return new Vector4(left.X + right.X, left.Y + right.Y, left.Z + right.Z, left.W + right.W); }
    public static Vector4 Subtract(Vector4 left, Vector4 right) { return new Vector4(left.X - right.X, left.Y - right.Y, left.Z - right.Z, left.W - right.W); }
    public static Vector4 Multiply(Vector4 left, Vector4 right) { return new Vector4(left.X * right.X, left.Y * right.Y, left.Z * right.Z, left.W * right.W); }
    public static Vector4 Multiply(Vector4 value, float scale) { return new Vector4(value.X * scale, value.Y * scale, value.Z * scale, value.W * scale); }
    public static Vector4 Divide(Vector4 value, float divider) { return Multiply(value, 1.0f / divider); }
    public static float Dot(Vector4 left, Vector4 right) { return (left.X * right.X) + (left.Y * right.Y) + (left.Z * right.Z) + (left.W * right.W); }
    public static Vector4 Lerp(Vector4 left, Vector4 right, float amount) { return new Vector4(MathHelper.Lerp(left.X, right.X, amount), MathHelper.Lerp(left.Y, right.Y, amount), MathHelper.Lerp(left.Z, right.Z, amount), MathHelper.Lerp(left.W, right.W, amount)); }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof Vector4 value
                && FloatSemantics.equals(X, value.X) && FloatSemantics.equals(Y, value.Y)
                && FloatSemantics.equals(Z, value.Z) && FloatSemantics.equals(W, value.W);
    }

    @Override
    public int hashCode() { return FloatSemantics.hash(X) + FloatSemantics.hash(Y) + FloatSemantics.hash(Z) + FloatSemantics.hash(W); }

    @Override
    public String toString() { return "{X:" + X + " Y:" + Y + " Z:" + Z + " W:" + W + '}'; }
}
