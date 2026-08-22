package Microsoft.Xna.Framework;

import java.util.Objects;

/** XNA ray. Constructor inputs are snapshotted. */
public final class Ray {

    public Vector3 Position;
    public Vector3 Direction;

    public Ray(Vector3 position, Vector3 direction) {
        Position = new Vector3(Objects.requireNonNull(position, "position"));
        Direction = new Vector3(Objects.requireNonNull(direction, "direction"));
    }

    public Float Intersects(BoundingBox box) {
        float tMin = 0.0f;
        float tMax = Float.MAX_VALUE;
        float[] origins = {Position.X, Position.Y, Position.Z};
        float[] directions = {Direction.X, Direction.Y, Direction.Z};
        float[] minima = {box.Min.X, box.Min.Y, box.Min.Z};
        float[] maxima = {box.Max.X, box.Max.Y, box.Max.Z};
        for (int index = 0; index < 3; index++) {
            if (Math.abs(directions[index]) < 1.0e-6f) {
                if (origins[index] < minima[index] || origins[index] > maxima[index]) return null;
            } else {
                float inverse = 1.0f / directions[index];
                float first = (minima[index] - origins[index]) * inverse;
                float second = (maxima[index] - origins[index]) * inverse;
                if (first > second) { float swap = first; first = second; second = swap; }
                tMin = Math.max(tMin, first);
                tMax = Math.min(tMax, second);
                if (tMin > tMax) return null;
            }
        }
        return tMin;
    }

    public Float Intersects(BoundingSphere sphere) {
        Vector3 difference = Vector3.Subtract(sphere.Center, Position);
        float distanceSquared = difference.LengthSquared();
        float radiusSquared = sphere.Radius * sphere.Radius;
        if (distanceSquared <= radiusSquared) return 0.0f;
        float along = Vector3.Dot(difference, Direction);
        if (along < 0.0f) return null;
        float perpendicularSquared = distanceSquared - (along * along);
        if (perpendicularSquared > radiusSquared) return null;
        return along - (float)Math.sqrt(radiusSquared - perpendicularSquared);
    }

    public Float Intersects(Plane plane) {
        float denominator = Vector3.Dot(Direction, plane.Normal);
        if (Math.abs(denominator) < 1.0e-5f) return null;
        float distance = (-plane.D - Vector3.Dot(plane.Normal, Position)) / denominator;
        if (distance < 0.0f) {
            if (distance < -1.0e-5f) return null;
            distance = 0.0f;
        }
        return distance;
    }

    @Override
    public boolean equals(Object other) { return this == other || other instanceof Ray value && Position.equals(value.Position) && Direction.equals(value.Direction); }
    @Override
    public int hashCode() { return Position.hashCode() + Direction.hashCode(); }
    @Override
    public String toString() { return "{Position:" + Position + " Direction:" + Direction + '}'; }
}
