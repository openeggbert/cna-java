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
    public static Vector4 Normalize(Vector4 vector) { Vector4 result = new Vector4(vector); result.Normalize(); return result; }
    public static Vector4 Add(Vector4 value1, Vector4 value2) { return new Vector4(value1.X + value2.X, value1.Y + value2.Y, value1.Z + value2.Z, value1.W + value2.W); }
    public static Vector4 Subtract(Vector4 value1, Vector4 value2) { return new Vector4(value1.X - value2.X, value1.Y - value2.Y, value1.Z - value2.Z, value1.W - value2.W); }
    public static Vector4 Multiply(Vector4 value1, Vector4 value2) { return new Vector4(value1.X * value2.X, value1.Y * value2.Y, value1.Z * value2.Z, value1.W * value2.W); }
    public static Vector4 Multiply(Vector4 value1, float scaleFactor) { return new Vector4(value1.X * scaleFactor, value1.Y * scaleFactor, value1.Z * scaleFactor, value1.W * scaleFactor); }
    public static Vector4 Divide(Vector4 value1, float divider) { return Multiply(value1, 1.0f / divider); }
    public static float Dot(Vector4 vector1, Vector4 vector2) { return (vector1.X * vector2.X) + (vector1.Y * vector2.Y) + (vector1.Z * vector2.Z) + (vector1.W * vector2.W); }
    public static Vector4 Lerp(Vector4 value1, Vector4 value2, float amount) { return new Vector4(MathHelper.Lerp(value1.X, value2.X, amount), MathHelper.Lerp(value1.Y, value2.Y, amount), MathHelper.Lerp(value1.Z, value2.Z, amount), MathHelper.Lerp(value1.W, value2.W, amount)); }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof Vector4 value
                && FloatSemantics.equals(X, value.X) && FloatSemantics.equals(Y, value.Y)
                && FloatSemantics.equals(Z, value.Z) && FloatSemantics.equals(W, value.W);
    }

    @Override
    public int hashCode() { return FloatSemantics.hash(X) + FloatSemantics.hash(Y) + FloatSemantics.hash(Z) + FloatSemantics.hash(W); }

    @Override
    public String toString() { return "{X:" + X + " Y:" + Y + " Z:" + Z + " W:" + W + '}'; }
}
