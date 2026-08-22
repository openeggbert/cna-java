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

    public static Quaternion Normalize(Quaternion quaternion) { Quaternion result = new Quaternion(quaternion); result.Normalize(); return result; }
    public static Quaternion Conjugate(Quaternion value) { return new Quaternion(-value.X, -value.Y, -value.Z, value.W); }

    public static Quaternion Inverse(Quaternion quaternion) {
        float inverse = 1.0f / quaternion.LengthSquared();
        return new Quaternion(-quaternion.X * inverse, -quaternion.Y * inverse, -quaternion.Z * inverse, quaternion.W * inverse);
    }

    public static Quaternion Add(Quaternion quaternion1, Quaternion quaternion2) {
        return new Quaternion(quaternion1.X + quaternion2.X, quaternion1.Y + quaternion2.Y, quaternion1.Z + quaternion2.Z, quaternion1.W + quaternion2.W);
    }

    public static Quaternion Subtract(Quaternion quaternion1, Quaternion quaternion2) {
        return new Quaternion(quaternion1.X - quaternion2.X, quaternion1.Y - quaternion2.Y, quaternion1.Z - quaternion2.Z, quaternion1.W - quaternion2.W);
    }

    public static Quaternion Negate(Quaternion quaternion) { return new Quaternion(-quaternion.X, -quaternion.Y, -quaternion.Z, -quaternion.W); }

    public static Quaternion Multiply(Quaternion quaternion1, Quaternion quaternion2) {
        return new Quaternion(
                (quaternion1.W * quaternion2.X) + (quaternion1.X * quaternion2.W) + (quaternion1.Y * quaternion2.Z) - (quaternion1.Z * quaternion2.Y),
                (quaternion1.W * quaternion2.Y) + (quaternion1.Y * quaternion2.W) + (quaternion1.Z * quaternion2.X) - (quaternion1.X * quaternion2.Z),
                (quaternion1.W * quaternion2.Z) + (quaternion1.Z * quaternion2.W) + (quaternion1.X * quaternion2.Y) - (quaternion1.Y * quaternion2.X),
                (quaternion1.W * quaternion2.W) - (quaternion1.X * quaternion2.X) - (quaternion1.Y * quaternion2.Y) - (quaternion1.Z * quaternion2.Z));
    }

    public static Quaternion Multiply(Quaternion quaternion1, float scaleFactor) {
        return new Quaternion(quaternion1.X * scaleFactor, quaternion1.Y * scaleFactor, quaternion1.Z * scaleFactor, quaternion1.W * scaleFactor);
    }

    public static Quaternion Divide(Quaternion quaternion1, Quaternion quaternion2) { return Multiply(quaternion1, Inverse(quaternion2)); }

    public static float Dot(Quaternion quaternion1, Quaternion quaternion2) {
        return (quaternion1.X * quaternion2.X) + (quaternion1.Y * quaternion2.Y) + (quaternion1.Z * quaternion2.Z) + (quaternion1.W * quaternion2.W);
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

    public static Quaternion Lerp(Quaternion quaternion1, Quaternion quaternion2, float amount) {
        float inverse = 1.0f - amount;
        Quaternion result = Dot(quaternion1, quaternion2) >= 0.0f
                ? new Quaternion(
                        (inverse * quaternion1.X) + (amount * quaternion2.X),
                        (inverse * quaternion1.Y) + (amount * quaternion2.Y),
                        (inverse * quaternion1.Z) + (amount * quaternion2.Z),
                        (inverse * quaternion1.W) + (amount * quaternion2.W))
                : new Quaternion(
                        (inverse * quaternion1.X) - (amount * quaternion2.X),
                        (inverse * quaternion1.Y) - (amount * quaternion2.Y),
                        (inverse * quaternion1.Z) - (amount * quaternion2.Z),
                        (inverse * quaternion1.W) - (amount * quaternion2.W));
        result.Normalize();
        return result;
    }

    public static Quaternion Slerp(Quaternion quaternion1, Quaternion quaternion2, float amount) {
        float dot = Dot(quaternion1, quaternion2);
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
                (leftScale * quaternion1.X) + (rightScale * quaternion2.X),
                (leftScale * quaternion1.Y) + (rightScale * quaternion2.Y),
                (leftScale * quaternion1.Z) + (rightScale * quaternion2.Z),
                (leftScale * quaternion1.W) + (rightScale * quaternion2.W));
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof Quaternion value
                && FloatSemantics.equals(X, value.X) && FloatSemantics.equals(Y, value.Y)
                && FloatSemantics.equals(Z, value.Z) && FloatSemantics.equals(W, value.W);
    }

    @Override
    public int hashCode() { return FloatSemantics.hash(X) + FloatSemantics.hash(Y) + FloatSemantics.hash(Z) + FloatSemantics.hash(W); }

    @Override
    public String toString() { return "{X:" + X + " Y:" + Y + " Z:" + Z + " W:" + W + '}'; }
}
