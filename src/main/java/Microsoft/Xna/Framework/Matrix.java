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

    public static Matrix Add(Matrix matrix1, Matrix matrix2) {
        return new Matrix(
                matrix1.M11 + matrix2.M11, matrix1.M12 + matrix2.M12, matrix1.M13 + matrix2.M13, matrix1.M14 + matrix2.M14,
                matrix1.M21 + matrix2.M21, matrix1.M22 + matrix2.M22, matrix1.M23 + matrix2.M23, matrix1.M24 + matrix2.M24,
                matrix1.M31 + matrix2.M31, matrix1.M32 + matrix2.M32, matrix1.M33 + matrix2.M33, matrix1.M34 + matrix2.M34,
                matrix1.M41 + matrix2.M41, matrix1.M42 + matrix2.M42, matrix1.M43 + matrix2.M43, matrix1.M44 + matrix2.M44);
    }

    public static Matrix Subtract(Matrix matrix1, Matrix matrix2) { return Add(matrix1, Multiply(matrix2, -1.0f)); }

    public static Matrix Multiply(Matrix matrix1, Matrix matrix2) {
        return new Matrix(
                (matrix1.M11*matrix2.M11)+(matrix1.M12*matrix2.M21)+(matrix1.M13*matrix2.M31)+(matrix1.M14*matrix2.M41),
                (matrix1.M11*matrix2.M12)+(matrix1.M12*matrix2.M22)+(matrix1.M13*matrix2.M32)+(matrix1.M14*matrix2.M42),
                (matrix1.M11*matrix2.M13)+(matrix1.M12*matrix2.M23)+(matrix1.M13*matrix2.M33)+(matrix1.M14*matrix2.M43),
                (matrix1.M11*matrix2.M14)+(matrix1.M12*matrix2.M24)+(matrix1.M13*matrix2.M34)+(matrix1.M14*matrix2.M44),
                (matrix1.M21*matrix2.M11)+(matrix1.M22*matrix2.M21)+(matrix1.M23*matrix2.M31)+(matrix1.M24*matrix2.M41),
                (matrix1.M21*matrix2.M12)+(matrix1.M22*matrix2.M22)+(matrix1.M23*matrix2.M32)+(matrix1.M24*matrix2.M42),
                (matrix1.M21*matrix2.M13)+(matrix1.M22*matrix2.M23)+(matrix1.M23*matrix2.M33)+(matrix1.M24*matrix2.M43),
                (matrix1.M21*matrix2.M14)+(matrix1.M22*matrix2.M24)+(matrix1.M23*matrix2.M34)+(matrix1.M24*matrix2.M44),
                (matrix1.M31*matrix2.M11)+(matrix1.M32*matrix2.M21)+(matrix1.M33*matrix2.M31)+(matrix1.M34*matrix2.M41),
                (matrix1.M31*matrix2.M12)+(matrix1.M32*matrix2.M22)+(matrix1.M33*matrix2.M32)+(matrix1.M34*matrix2.M42),
                (matrix1.M31*matrix2.M13)+(matrix1.M32*matrix2.M23)+(matrix1.M33*matrix2.M33)+(matrix1.M34*matrix2.M43),
                (matrix1.M31*matrix2.M14)+(matrix1.M32*matrix2.M24)+(matrix1.M33*matrix2.M34)+(matrix1.M34*matrix2.M44),
                (matrix1.M41*matrix2.M11)+(matrix1.M42*matrix2.M21)+(matrix1.M43*matrix2.M31)+(matrix1.M44*matrix2.M41),
                (matrix1.M41*matrix2.M12)+(matrix1.M42*matrix2.M22)+(matrix1.M43*matrix2.M32)+(matrix1.M44*matrix2.M42),
                (matrix1.M41*matrix2.M13)+(matrix1.M42*matrix2.M23)+(matrix1.M43*matrix2.M33)+(matrix1.M44*matrix2.M43),
                (matrix1.M41*matrix2.M14)+(matrix1.M42*matrix2.M24)+(matrix1.M43*matrix2.M34)+(matrix1.M44*matrix2.M44));
    }

    public static Matrix Multiply(Matrix matrix, float scaleFactor) {
        return new Matrix(
                matrix.M11*scaleFactor, matrix.M12*scaleFactor, matrix.M13*scaleFactor, matrix.M14*scaleFactor,
                matrix.M21*scaleFactor, matrix.M22*scaleFactor, matrix.M23*scaleFactor, matrix.M24*scaleFactor,
                matrix.M31*scaleFactor, matrix.M32*scaleFactor, matrix.M33*scaleFactor, matrix.M34*scaleFactor,
                matrix.M41*scaleFactor, matrix.M42*scaleFactor, matrix.M43*scaleFactor, matrix.M44*scaleFactor);
    }

    public static Matrix Transpose(Matrix matrix) {
        return new Matrix(
                matrix.M11, matrix.M21, matrix.M31, matrix.M41,
                matrix.M12, matrix.M22, matrix.M32, matrix.M42,
                matrix.M13, matrix.M23, matrix.M33, matrix.M43,
                matrix.M14, matrix.M24, matrix.M34, matrix.M44);
    }

    public static Matrix CreateScale(float scale) { return CreateScale(scale, scale, scale); }
    public static Matrix CreateScale(Vector3 scales) { return CreateScale(scales.X, scales.Y, scales.Z); }
    public static Matrix CreateScale(float xScale, float yScale, float zScale) {
        return new Matrix(xScale,0,0,0, 0,yScale,0,0, 0,0,zScale,0, 0,0,0,1);
    }

    public static Matrix CreateTranslation(float xPosition, float yPosition, float zPosition) {
        return new Matrix(1,0,0,0, 0,1,0,0, 0,0,1,0, xPosition,yPosition,zPosition,1);
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

    public static Matrix CreateFromQuaternion(Quaternion quaternion) {
        float xx = quaternion.X * quaternion.X; float yy = quaternion.Y * quaternion.Y; float zz = quaternion.Z * quaternion.Z;
        float xy = quaternion.X * quaternion.Y; float zw = quaternion.Z * quaternion.W;
        float zx = quaternion.Z * quaternion.X; float yw = quaternion.Y * quaternion.W;
        float yz = quaternion.Y * quaternion.Z; float xw = quaternion.X * quaternion.W;
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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Matrix v)) return false;
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
