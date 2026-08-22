package Microsoft.Xna.Framework;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

final class ValueTypeTests {

    @ParameterizedTest
    @CsvSource({"3,4,5", "0,0,0", "-6,8,10", "1.5,2.0,2.5"})
    void Vector2_Length(float x, float y, float expected) {
        assertEquals(expected, new Vector2(x, y).Length(), 0.00001f);
    }

    @Test
    void Vectors_PreserveClrFloatEqualityEdges() {
        assertEquals(new Vector2(Float.NaN, -0.0f), new Vector2(Float.NaN, 0.0f));
        assertEquals(new Vector2(-0.0f, 1.0f).hashCode(), new Vector2(0.0f, 1.0f).hashCode());
        assertEquals(new Vector3(0, 0, 1), Vector3.Cross(new Vector3(1, 0, 0), new Vector3(0, 1, 0)));
        assertEquals(4, new Vector4(1).LengthSquared());
    }

    @Test
    void Matrix_ComposesScaleAndTranslationUsingXnaLayout() {
        Matrix result = Matrix.Multiply(Matrix.CreateScale(2), Matrix.CreateTranslation(3, 4, 5));
        assertEquals(2, result.M11);
        assertEquals(2, result.M22);
        assertEquals(2, result.M33);
        assertEquals(new Vector3(3, 4, 5), result.getTranslation());
        assertEquals(1, Matrix.getIdentity().Determinant());
    }

    @Test
    void Quaternion_NormalizeAndIdentityAreValueOriented() {
        Quaternion identity = Quaternion.getIdentity();
        Quaternion normalized = Quaternion.Normalize(new Quaternion(0, 0, 0, 4));
        assertEquals(identity, normalized);
        identity.W = 3;
        assertEquals(1, Quaternion.getIdentity().W);
    }

    @Test
    void Color_ClampsConstructorsButValidatesPropertySetters() {
        Color color = new Color(-2, 300, 7, 260);
        assertEquals(0, color.getR());
        assertEquals(255, color.getG());
        assertEquals(7, color.getB());
        assertEquals(255, color.getA());
        assertThrows(IllegalArgumentException.class, () -> color.setR(256));
        assertThrows(UnsupportedOperationException.class, () -> Color.White.setA(0));
        assertEquals(new Color(100, 149, 237), Color.CornflowerBlue);
    }

    @Test
    void PointAndRectangle_UseXnaHalfOpenContainment() {
        Rectangle rectangle = new Rectangle(10, 20, 30, 40);
        assertTrue(rectangle.Contains(new Point(10, 20)));
        assertFalse(rectangle.Contains(new Point(40, 60)));
        assertTrue(rectangle.Intersects(new Rectangle(39, 59, 2, 2)));
        assertFalse(rectangle.Intersects(new Rectangle(40, 60, 2, 2)));
        assertEquals(new Rectangle(10, 20, 31, 41), Rectangle.Union(rectangle, new Rectangle(40, 60, 1, 1)));
    }

    @Test
    void Geometry_IntersectionsAndSnapshotting() {
        Vector3 min = new Vector3(-1, -1, -1);
        BoundingBox box = new BoundingBox(min, new Vector3(1, 1, 1));
        min.X = -100;
        assertEquals(-1, box.Min.X);

        BoundingSphere sphere = new BoundingSphere(Vector3.getZero(), 1);
        assertTrue(box.Intersects(sphere));
        assertEquals(ContainmentType.Contains, box.Contains(new Vector3(0, 0, 0)));
        assertEquals(ContainmentType.Disjoint, sphere.Contains(new Vector3(1, 0, 0)));

        Ray ray = new Ray(new Vector3(-3, 0, 0), Vector3.getUnitX());
        assertEquals(2.0f, ray.Intersects(box));
        assertEquals(2.0f, ray.Intersects(sphere));
        assertEquals(3.0f, ray.Intersects(new Plane(Vector3.getUnitX(), 0)));
    }

    @Test
    void Plane_NormalizeAndIntersection() {
        Plane plane = new Plane(new Vector3(0, 2, 0), -2);
        plane.Normalize();
        assertEquals(new Vector3(0, 1, 0), plane.Normal);
        assertEquals(-1, plane.D);
        assertEquals(PlaneIntersectionType.Intersecting,
                plane.Intersects(new BoundingSphere(new Vector3(0, 1, 0), 0.5f)));
    }

    @Test
    void GameTime_UsesDurationAndRejectsNull() {
        GameTime time = new GameTime(Duration.ofSeconds(3), Duration.ofMillis(16), true);
        assertEquals(Duration.ofSeconds(3), time.getTotalGameTime());
        assertEquals(Duration.ofMillis(16), time.getElapsedGameTime());
        assertTrue(time.getIsRunningSlowly());
        assertThrows(NullPointerException.class, () -> new GameTime(null, Duration.ZERO));
    }
}

