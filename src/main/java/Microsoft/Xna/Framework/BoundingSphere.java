package Microsoft.Xna.Framework;

import java.util.Objects;

/** XNA bounding sphere. Constructor inputs are snapshotted. */
public final class BoundingSphere {

    public Vector3 Center;
    public float Radius;

    public BoundingSphere(Vector3 center, float radius) {
        Center = new Vector3(Objects.requireNonNull(center, "center"));
        Radius = radius;
    }

    public ContainmentType Contains(Vector3 point) {
        float distance = Vector3.DistanceSquared(point, Center);
        return distance < Radius * Radius ? ContainmentType.Contains : ContainmentType.Disjoint;
    }

    public ContainmentType Contains(BoundingSphere sphere) {
        float distance = Vector3.Distance(Center, sphere.Center);
        if (distance > Radius + sphere.Radius) return ContainmentType.Disjoint;
        if (distance <= Radius - sphere.Radius) return ContainmentType.Contains;
        return ContainmentType.Intersects;
    }

    public ContainmentType Contains(BoundingBox box) {
        boolean allInside = true;
        for (Vector3 corner : box.GetCorners()) {
            if (Contains(corner) == ContainmentType.Disjoint) { allInside = false; break; }
        }
        if (allInside) return ContainmentType.Contains;
        return Intersects(box) ? ContainmentType.Intersects : ContainmentType.Disjoint;
    }

    public boolean Intersects(BoundingSphere sphere) {
        float radius = Radius + sphere.Radius;
        return Vector3.DistanceSquared(Center, sphere.Center) <= radius * radius;
    }

    public boolean Intersects(BoundingBox box) { return box.Intersects(this); }
    public Float Intersects(Ray ray) { return ray.Intersects(this); }
    public PlaneIntersectionType Intersects(Plane plane) { return plane.Intersects(this); }

    public static BoundingSphere CreateMerged(BoundingSphere original, BoundingSphere additional) {
        Vector3 difference = Vector3.Subtract(additional.Center, original.Center);
        float distance = difference.Length();
        if (original.Radius >= distance + additional.Radius) return new BoundingSphere(original.Center, original.Radius);
        if (additional.Radius >= distance + original.Radius) return new BoundingSphere(additional.Center, additional.Radius);
        float left = Math.min(-original.Radius, distance - additional.Radius);
        float right = Math.max(original.Radius, distance + additional.Radius);
        float radius = (right - left) * 0.5f;
        Vector3 center = distance == 0.0f ? new Vector3(original.Center)
                : Vector3.Add(original.Center, Vector3.Multiply(difference, (left + radius) / distance));
        return new BoundingSphere(center, radius);
    }

    @Override
    public boolean equals(Object other) { return this == other || other instanceof BoundingSphere value && Center.equals(value.Center) && FloatSemantics.equals(Radius, value.Radius); }
    @Override
    public int hashCode() { return Center.hashCode() + FloatSemantics.hash(Radius); }
    @Override
    public String toString() { return "{Center:" + Center + " Radius:" + Radius + '}'; }
}
