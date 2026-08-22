package Microsoft.Xna.Framework;

import java.util.Objects;

/** Mutable row-major XNA 4x4 matrix value. */
public final class Matrix {

    public float M11; public float M12; public float M13; public float M14;
    public float M21; public float M22; public float M23; public float M24;
    public float M31; public float M32; public float M33; public float M34;
    public float M41; public float M42; public float M43; public float M44;

    public Matrix() {
    }

    public Matrix(
            float m11, float m12, float m13, float m14,
            float m21, float m22, float m23, float m24,
            float m31, float m32, float m33, float m34,
            float m41, float m42, float m43, float m44) {
        M11 = m11; M12 = m12; M13 = m13; M14 = m14;
        M21 = m21; M22 = m22; M23 = m23; M24 = m24;
        M31 = m31; M32 = m32; M33 = m33; M34 = m34;
        M41 = m41; M42 = m42; M43 = m43; M44 = m44;
    }

    public Matrix(Matrix value) {
        this(value.M11, value.M12, value.M13, value.M14,
                value.M21, value.M22, value.M23, value.M24,
                value.M31, value.M32, value.M33, value.M34,
                value.M41, value.M42, value.M43, value.M44);
    }

    public static Matrix getIdentity() {
        return new Matrix(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
    }

    public Vector3 getTranslation() { return new Vector3(M41, M42, M43); }
    public void setTranslation(Vector3 value) { M41 = value.X; M42 = value.Y; M43 = value.Z; }

    public float Determinant() {
        float a = (M33 * M44) - (M34 * M43);
        float b = (M32 * M44) - (M34 * M42);
        float c = (M32 * M43) - (M33 * M42);
        float d = (M31 * M44) - (M34 * M41);
        float e = (M31 * M43) - (M33 * M41);
        float f = (M31 * M42) - (M32 * M41);
        return (M11 * ((M22 * a) - (M23 * b) + (M24 * c)))
                - (M12 * ((M21 * a) - (M23 * d) + (M24 * e)))
                + (M13 * ((M21 * b) - (M22 * d) + (M24 * f)))
                - (M14 * ((M21 * c) - (M22 * e) + (M23 * f)));
    }

    public static Matrix Add(Matrix left, Matrix right) {
        return new Matrix(
                left.M11 + right.M11, left.M12 + right.M12, left.M13 + right.M13, left.M14 + right.M14,
                left.M21 + right.M21, left.M22 + right.M22, left.M23 + right.M23, left.M24 + right.M24,
                left.M31 + right.M31, left.M32 + right.M32, left.M33 + right.M33, left.M34 + right.M34,
                left.M41 + right.M41, left.M42 + right.M42, left.M43 + right.M43, left.M44 + right.M44);
    }

    public static Matrix Subtract(Matrix left, Matrix right) { return Add(left, Multiply(right, -1.0f)); }

    public static Matrix Multiply(Matrix a, Matrix b) {
        return new Matrix(
                (a.M11*b.M11)+(a.M12*b.M21)+(a.M13*b.M31)+(a.M14*b.M41),
                (a.M11*b.M12)+(a.M12*b.M22)+(a.M13*b.M32)+(a.M14*b.M42),
                (a.M11*b.M13)+(a.M12*b.M23)+(a.M13*b.M33)+(a.M14*b.M43),
                (a.M11*b.M14)+(a.M12*b.M24)+(a.M13*b.M34)+(a.M14*b.M44),
                (a.M21*b.M11)+(a.M22*b.M21)+(a.M23*b.M31)+(a.M24*b.M41),
                (a.M21*b.M12)+(a.M22*b.M22)+(a.M23*b.M32)+(a.M24*b.M42),
                (a.M21*b.M13)+(a.M22*b.M23)+(a.M23*b.M33)+(a.M24*b.M43),
                (a.M21*b.M14)+(a.M22*b.M24)+(a.M23*b.M34)+(a.M24*b.M44),
                (a.M31*b.M11)+(a.M32*b.M21)+(a.M33*b.M31)+(a.M34*b.M41),
                (a.M31*b.M12)+(a.M32*b.M22)+(a.M33*b.M32)+(a.M34*b.M42),
                (a.M31*b.M13)+(a.M32*b.M23)+(a.M33*b.M33)+(a.M34*b.M43),
                (a.M31*b.M14)+(a.M32*b.M24)+(a.M33*b.M34)+(a.M34*b.M44),
                (a.M41*b.M11)+(a.M42*b.M21)+(a.M43*b.M31)+(a.M44*b.M41),
                (a.M41*b.M12)+(a.M42*b.M22)+(a.M43*b.M32)+(a.M44*b.M42),
                (a.M41*b.M13)+(a.M42*b.M23)+(a.M43*b.M33)+(a.M44*b.M43),
                (a.M41*b.M14)+(a.M42*b.M24)+(a.M43*b.M34)+(a.M44*b.M44));
    }

    public static Matrix Multiply(Matrix value, float scale) {
        return new Matrix(
                value.M11*scale, value.M12*scale, value.M13*scale, value.M14*scale,
                value.M21*scale, value.M22*scale, value.M23*scale, value.M24*scale,
                value.M31*scale, value.M32*scale, value.M33*scale, value.M34*scale,
                value.M41*scale, value.M42*scale, value.M43*scale, value.M44*scale);
    }

    public static Matrix Transpose(Matrix value) {
        return new Matrix(
                value.M11, value.M21, value.M31, value.M41,
                value.M12, value.M22, value.M32, value.M42,
                value.M13, value.M23, value.M33, value.M43,
                value.M14, value.M24, value.M34, value.M44);
    }

    public static Matrix CreateScale(float scale) { return CreateScale(scale, scale, scale); }
    public static Matrix CreateScale(Vector3 scales) { return CreateScale(scales.X, scales.Y, scales.Z); }
    public static Matrix CreateScale(float x, float y, float z) {
        return new Matrix(x,0,0,0, 0,y,0,0, 0,0,z,0, 0,0,0,1);
    }

    public static Matrix CreateTranslation(float x, float y, float z) {
        return new Matrix(1,0,0,0, 0,1,0,0, 0,0,1,0, x,y,z,1);
    }

    public static Matrix CreateTranslation(Vector3 position) { return CreateTranslation(position.X, position.Y, position.Z); }

    public static Matrix CreateRotationX(float radians) {
        float cosine = (float)Math.cos(radians); float sine = (float)Math.sin(radians);
        return new Matrix(1,0,0,0, 0,cosine,sine,0, 0,-sine,cosine,0, 0,0,0,1);
    }

    public static Matrix CreateRotationY(float radians) {
        float cosine = (float)Math.cos(radians); float sine = (float)Math.sin(radians);
        return new Matrix(cosine,0,-sine,0, 0,1,0,0, sine,0,cosine,0, 0,0,0,1);
    }

    public static Matrix CreateRotationZ(float radians) {
        float cosine = (float)Math.cos(radians); float sine = (float)Math.sin(radians);
        return new Matrix(cosine,sine,0,0, -sine,cosine,0,0, 0,0,1,0, 0,0,0,1);
    }

    public static Matrix CreateFromQuaternion(Quaternion value) {
        float xx = value.X * value.X; float yy = value.Y * value.Y; float zz = value.Z * value.Z;
        float xy = value.X * value.Y; float zw = value.Z * value.W;
        float zx = value.Z * value.X; float yw = value.Y * value.W;
        float yz = value.Y * value.Z; float xw = value.X * value.W;
        return new Matrix(
                1-(2*(yy+zz)), 2*(xy+zw), 2*(zx-yw), 0,
                2*(xy-zw), 1-(2*(zz+xx)), 2*(yz+xw), 0,
                2*(zx+yw), 2*(yz-xw), 1-(2*(yy+xx)), 0,
                0,0,0,1);
    }

    public static Matrix CreateFromYawPitchRoll(float yaw, float pitch, float roll) {
        return CreateFromQuaternion(Quaternion.CreateFromYawPitchRoll(yaw, pitch, roll));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Matrix v)) return false;
        return FloatSemantics.equals(M11,v.M11)&&FloatSemantics.equals(M12,v.M12)&&FloatSemantics.equals(M13,v.M13)&&FloatSemantics.equals(M14,v.M14)
                &&FloatSemantics.equals(M21,v.M21)&&FloatSemantics.equals(M22,v.M22)&&FloatSemantics.equals(M23,v.M23)&&FloatSemantics.equals(M24,v.M24)
                &&FloatSemantics.equals(M31,v.M31)&&FloatSemantics.equals(M32,v.M32)&&FloatSemantics.equals(M33,v.M33)&&FloatSemantics.equals(M34,v.M34)
                &&FloatSemantics.equals(M41,v.M41)&&FloatSemantics.equals(M42,v.M42)&&FloatSemantics.equals(M43,v.M43)&&FloatSemantics.equals(M44,v.M44);
    }

    @Override
    public int hashCode() {
        return Objects.hash(FloatSemantics.hash(M11),FloatSemantics.hash(M12),FloatSemantics.hash(M13),FloatSemantics.hash(M14),
                FloatSemantics.hash(M21),FloatSemantics.hash(M22),FloatSemantics.hash(M23),FloatSemantics.hash(M24),
                FloatSemantics.hash(M31),FloatSemantics.hash(M32),FloatSemantics.hash(M33),FloatSemantics.hash(M34),
                FloatSemantics.hash(M41),FloatSemantics.hash(M42),FloatSemantics.hash(M43),FloatSemantics.hash(M44));
    }
}
