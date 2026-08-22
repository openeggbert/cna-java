package Microsoft.Xna.Framework;

import java.util.Objects;

/** XNA plane represented by a normal and distance. */
public final class Plane {

    public Vector3 Normal;
    public float D;

    public Plane(float a, float b, float c, float d) { Normal = new Vector3(a, b, c); D = d; }
    public Plane(Vector3 normal, float d) { Normal = new Vector3(Objects.requireNonNull(normal, "normal")); D = d; }
    public Plane(Vector4 value) { this(value.X, value.Y, value.Z, value.W); }

    public Plane(Vector3 point1, Vector3 point2, Vector3 point3) {
        Vector3 first = Vector3.Subtract(point2, point1);
        Vector3 second = Vector3.Subtract(point3, point1);
        Normal = Vector3.Normalize(Vector3.Cross(first, second));
        D = -Vector3.Dot(Normal, point1);
    }

    public float Dot(Vector4 value) {
        return (Normal.X * value.X) + (Normal.Y * value.Y) + (Normal.Z * value.Z) + (D * value.W);
    }

    public float DotCoordinate(Vector3 value) { return Vector3.Dot(Normal, value) + D; }
    public float DotNormal(Vector3 value) { return Vector3.Dot(Normal, value); }

    public void Normalize() {
        float factor = 1.0f / Normal.Length();
        Normal = Vector3.Multiply(Normal, factor);
        D *= factor;
    }

    public static Plane Normalize(Plane value) {
        Plane result = new Plane(value.Normal, value.D);
        result.Normalize();
        return result;
    }

    public PlaneIntersectionType Intersects(BoundingSphere sphere) {
        float distance = DotCoordinate(sphere.Center);
        if (distance > sphere.Radius) return PlaneIntersectionType.Front;
        if (distance < -sphere.Radius) return PlaneIntersectionType.Back;
        return PlaneIntersectionType.Intersecting;
    }

    public PlaneIntersectionType Intersects(BoundingBox box) {
        Vector3 negative = new Vector3(
                Normal.X >= 0.0f ? box.Min.X : box.Max.X,
                Normal.Y >= 0.0f ? box.Min.Y : box.Max.Y,
                Normal.Z >= 0.0f ? box.Min.Z : box.Max.Z);
        Vector3 positive = new Vector3(
                Normal.X >= 0.0f ? box.Max.X : box.Min.X,
                Normal.Y >= 0.0f ? box.Max.Y : box.Min.Y,
                Normal.Z >= 0.0f ? box.Max.Z : box.Min.Z);
        if (DotCoordinate(negative) > 0.0f) return PlaneIntersectionType.Front;
        if (DotCoordinate(positive) < 0.0f) return PlaneIntersectionType.Back;
        return PlaneIntersectionType.Intersecting;
    }

    @Override
    public boolean equals(Object other) { return this == other || other instanceof Plane value && Normal.equals(value.Normal) && FloatSemantics.equals(D, value.D); }
    @Override
    public int hashCode() { return Normal.hashCode() + FloatSemantics.hash(D); }
    @Override
    public String toString() { return "{Normal:" + Normal + " D:" + D + '}'; }
}

