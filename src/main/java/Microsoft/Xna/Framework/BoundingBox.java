package Microsoft.Xna.Framework;

import java.util.Objects;

/** Axis-aligned XNA bounding box. Constructor inputs are snapshotted. */
public final class BoundingBox {

    public static final int CornerCount = 8;

    public Vector3 Min;
    public Vector3 Max;

    public BoundingBox(Vector3 min, Vector3 max) {
        Min = new Vector3(Objects.requireNonNull(min, "min"));
        Max = new Vector3(Objects.requireNonNull(max, "max"));
    }

    public Vector3[] GetCorners() {
        return new Vector3[] {
            new Vector3(Min.X, Max.Y, Max.Z), new Vector3(Max.X, Max.Y, Max.Z),
            new Vector3(Max.X, Min.Y, Max.Z), new Vector3(Min.X, Min.Y, Max.Z),
            new Vector3(Min.X, Max.Y, Min.Z), new Vector3(Max.X, Max.Y, Min.Z),
            new Vector3(Max.X, Min.Y, Min.Z), new Vector3(Min.X, Min.Y, Min.Z)
        };
    }

    public ContainmentType Contains(Vector3 point) {
        if (point.X < Min.X || point.X > Max.X || point.Y < Min.Y || point.Y > Max.Y
                || point.Z < Min.Z || point.Z > Max.Z) {
            return ContainmentType.Disjoint;
        }
        return ContainmentType.Contains;
    }

    public ContainmentType Contains(BoundingBox box) {
        if (Max.X < box.Min.X || Min.X > box.Max.X || Max.Y < box.Min.Y || Min.Y > box.Max.Y
                || Max.Z < box.Min.Z || Min.Z > box.Max.Z) {
            return ContainmentType.Disjoint;
        }
        if (Min.X <= box.Min.X && box.Max.X <= Max.X && Min.Y <= box.Min.Y && box.Max.Y <= Max.Y
                && Min.Z <= box.Min.Z && box.Max.Z <= Max.Z) {
            return ContainmentType.Contains;
        }
        return ContainmentType.Intersects;
    }

    public ContainmentType Contains(BoundingSphere sphere) {
        Vector3 center = sphere.Center;
        if (center.X - Min.X >= sphere.Radius && center.Y - Min.Y >= sphere.Radius
                && center.Z - Min.Z >= sphere.Radius && Max.X - center.X >= sphere.Radius
                && Max.Y - center.Y >= sphere.Radius && Max.Z - center.Z >= sphere.Radius) {
            return ContainmentType.Contains;
        }
        return Intersects(sphere) ? ContainmentType.Intersects : ContainmentType.Disjoint;
    }

    public boolean Intersects(BoundingBox box) { return Contains(box) != ContainmentType.Disjoint; }

    public boolean Intersects(BoundingSphere sphere) {
        float x = MathHelper.Clamp(sphere.Center.X, Min.X, Max.X);
        float y = MathHelper.Clamp(sphere.Center.Y, Min.Y, Max.Y);
        float z = MathHelper.Clamp(sphere.Center.Z, Min.Z, Max.Z);
        return Vector3.DistanceSquared(sphere.Center, new Vector3(x, y, z)) <= sphere.Radius * sphere.Radius;
    }

    public Float Intersects(Ray ray) { return ray.Intersects(this); }
    public PlaneIntersectionType Intersects(Plane plane) { return plane.Intersects(this); }

    public static BoundingBox CreateMerged(BoundingBox original, BoundingBox additional) {
        return new BoundingBox(Vector3.Min(original.Min, additional.Min), Vector3.Max(original.Max, additional.Max));
    }

    @Override
    public boolean equals(Object other) { return this == other || other instanceof BoundingBox value && Min.equals(value.Min) && Max.equals(value.Max); }
    @Override
    public int hashCode() { return Min.hashCode() + Max.hashCode(); }
    @Override
    public String toString() { return "{Min:" + Min + " Max:" + Max + '}'; }
}

