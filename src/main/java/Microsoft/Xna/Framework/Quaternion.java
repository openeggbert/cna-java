package Microsoft.Xna.Framework;

import java.util.Objects;

/** Mutable XNA quaternion value. */
public final class Quaternion {

    public float X;
    public float Y;
    public float Z;
    public float W;

    public Quaternion() {
    }

    public Quaternion(float x, float y, float z, float w) { X = x; Y = y; Z = z; W = w; }
    public Quaternion(Vector3 vectorPart, float scalarPart) { this(vectorPart.X, vectorPart.Y, vectorPart.Z, scalarPart); }
    public Quaternion(Quaternion value) { this(Objects.requireNonNull(value, "value").X, value.Y, value.Z, value.W); }

    public static Quaternion getIdentity() { return new Quaternion(0.0f, 0.0f, 0.0f, 1.0f); }

    public float Length() { return (float)Math.sqrt(LengthSquared()); }
    public float LengthSquared() { return (X * X) + (Y * Y) + (Z * Z) + (W * W); }

    public void Normalize() {
        float factor = 1.0f / (float)Math.sqrt(LengthSquared());
        X *= factor; Y *= factor; Z *= factor; W *= factor;
    }

    public void Conjugate() { X = -X; Y = -Y; Z = -Z; }

    public static Quaternion Normalize(Quaternion value) { Quaternion result = new Quaternion(value); result.Normalize(); return result; }
    public static Quaternion Conjugate(Quaternion value) { return new Quaternion(-value.X, -value.Y, -value.Z, value.W); }

    public static Quaternion Inverse(Quaternion value) {
        float inverse = 1.0f / value.LengthSquared();
        return new Quaternion(-value.X * inverse, -value.Y * inverse, -value.Z * inverse, value.W * inverse);
    }

    public static Quaternion Add(Quaternion left, Quaternion right) {
        return new Quaternion(left.X + right.X, left.Y + right.Y, left.Z + right.Z, left.W + right.W);
    }

    public static Quaternion Subtract(Quaternion left, Quaternion right) {
        return new Quaternion(left.X - right.X, left.Y - right.Y, left.Z - right.Z, left.W - right.W);
    }

    public static Quaternion Negate(Quaternion value) { return new Quaternion(-value.X, -value.Y, -value.Z, -value.W); }

    public static Quaternion Multiply(Quaternion left, Quaternion right) {
        return new Quaternion(
                (left.W * right.X) + (left.X * right.W) + (left.Y * right.Z) - (left.Z * right.Y),
                (left.W * right.Y) + (left.Y * right.W) + (left.Z * right.X) - (left.X * right.Z),
                (left.W * right.Z) + (left.Z * right.W) + (left.X * right.Y) - (left.Y * right.X),
                (left.W * right.W) - (left.X * right.X) - (left.Y * right.Y) - (left.Z * right.Z));
    }

    public static Quaternion Multiply(Quaternion value, float scale) {
        return new Quaternion(value.X * scale, value.Y * scale, value.Z * scale, value.W * scale);
    }

    public static Quaternion Divide(Quaternion left, Quaternion right) { return Multiply(left, Inverse(right)); }

    public static float Dot(Quaternion left, Quaternion right) {
        return (left.X * right.X) + (left.Y * right.Y) + (left.Z * right.Z) + (left.W * right.W);
    }

    public static Quaternion CreateFromAxisAngle(Vector3 axis, float angle) {
        float half = angle * 0.5f;
        float sine = (float)Math.sin(half);
        return new Quaternion(axis.X * sine, axis.Y * sine, axis.Z * sine, (float)Math.cos(half));
    }

    public static Quaternion CreateFromYawPitchRoll(float yaw, float pitch, float roll) {
        float halfRoll = roll * 0.5f;
        float sinRoll = (float)Math.sin(halfRoll);
        float cosRoll = (float)Math.cos(halfRoll);
        float halfPitch = pitch * 0.5f;
        float sinPitch = (float)Math.sin(halfPitch);
        float cosPitch = (float)Math.cos(halfPitch);
        float halfYaw = yaw * 0.5f;
        float sinYaw = (float)Math.sin(halfYaw);
        float cosYaw = (float)Math.cos(halfYaw);
        return new Quaternion(
                (cosYaw * sinPitch * cosRoll) + (sinYaw * cosPitch * sinRoll),
                (sinYaw * cosPitch * cosRoll) - (cosYaw * sinPitch * sinRoll),
                (cosYaw * cosPitch * sinRoll) - (sinYaw * sinPitch * cosRoll),
                (cosYaw * cosPitch * cosRoll) + (sinYaw * sinPitch * sinRoll));
    }

    public static Quaternion Lerp(Quaternion left, Quaternion right, float amount) {
        float inverse = 1.0f - amount;
        Quaternion result = Dot(left, right) >= 0.0f
                ? new Quaternion(
                        (inverse * left.X) + (amount * right.X),
                        (inverse * left.Y) + (amount * right.Y),
                        (inverse * left.Z) + (amount * right.Z),
                        (inverse * left.W) + (amount * right.W))
                : new Quaternion(
                        (inverse * left.X) - (amount * right.X),
                        (inverse * left.Y) - (amount * right.Y),
                        (inverse * left.Z) - (amount * right.Z),
                        (inverse * left.W) - (amount * right.W));
        result.Normalize();
        return result;
    }

    public static Quaternion Slerp(Quaternion left, Quaternion right, float amount) {
        float dot = Dot(left, right);
        boolean negate = dot < 0.0f;
        if (negate) {
            dot = -dot;
        }
        float leftScale;
        float rightScale;
        if (dot > 0.999999f) {
            leftScale = 1.0f - amount;
            rightScale = negate ? -amount : amount;
        } else {
            float angle = (float)Math.acos(dot);
            float inverseSin = 1.0f / (float)Math.sin(angle);
            leftScale = (float)Math.sin((1.0f - amount) * angle) * inverseSin;
            rightScale = (negate ? -1.0f : 1.0f) * (float)Math.sin(amount * angle) * inverseSin;
        }
        return new Quaternion(
                (leftScale * left.X) + (rightScale * right.X),
                (leftScale * left.Y) + (rightScale * right.Y),
                (leftScale * left.Z) + (rightScale * right.Z),
                (leftScale * left.W) + (rightScale * right.W));
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof Quaternion value
                && FloatSemantics.equals(X, value.X) && FloatSemantics.equals(Y, value.Y)
                && FloatSemantics.equals(Z, value.Z) && FloatSemantics.equals(W, value.W);
    }

    @Override
    public int hashCode() { return FloatSemantics.hash(X) + FloatSemantics.hash(Y) + FloatSemantics.hash(Z) + FloatSemantics.hash(W); }

    @Override
    public String toString() { return "{X:" + X + " Y:" + Y + " Z:" + Z + " W:" + W + '}'; }
}

