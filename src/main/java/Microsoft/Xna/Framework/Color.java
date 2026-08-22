package Microsoft.Xna.Framework;

import java.util.Objects;

/** Mutable XNA RGBA value. Named instances are frozen to prevent shared-state corruption. */
public final class Color {

    public static final Color Transparent = named(255, 255, 255, 0);
    public static final Color CornflowerBlue = named(100, 149, 237, 255);
    public static final Color White = named(255, 255, 255, 255);

    private int packedValue;
    private final boolean frozen;

    public Color(int red, int green, int blue) {
        this(red, green, blue, 255);
    }

    public Color(int red, int green, int blue, int alpha) {
        this(pack(clampByte(red), clampByte(green), clampByte(blue), clampByte(alpha)), false);
    }

    public Color(float red, float green, float blue) {
        this(red, green, blue, 1.0f);
    }

    public Color(float red, float green, float blue, float alpha) {
        this(pack(packUnit(red), packUnit(green), packUnit(blue), packUnit(alpha)), false);
    }

    /** Copies a color snapshot; copies of named colors are ordinary mutable values. */
    public Color(Color value) {
        this(Objects.requireNonNull(value, "value").packedValue, false);
    }

    private Color(int packedValue, boolean frozen) {
        this.packedValue = packedValue;
        this.frozen = frozen;
    }

    public int getR() {
        return packedValue & 0xff;
    }

    public void setR(int value) {
        ensureMutable();
        packedValue = (packedValue & 0xffffff00) | requireByte(value, "value");
    }

    public int getG() {
        return (packedValue >>> 8) & 0xff;
    }

    public void setG(int value) {
        ensureMutable();
        packedValue = (packedValue & 0xffff00ff) | (requireByte(value, "value") << 8);
    }

    public int getB() {
        return (packedValue >>> 16) & 0xff;
    }

    public void setB(int value) {
        ensureMutable();
        packedValue = (packedValue & 0xff00ffff) | (requireByte(value, "value") << 16);
    }

    public int getA() {
        return (packedValue >>> 24) & 0xff;
    }

    public void setA(int value) {
        ensureMutable();
        packedValue = (packedValue & 0x00ffffff) | (requireByte(value, "value") << 24);
    }

    /** Unsigned XNA packed value represented in Java's positive {@code long} range. */
    public long getPackedValue() {
        return Integer.toUnsignedLong(packedValue);
    }

    public void setPackedValue(long value) {
        ensureMutable();
        if (value < 0L || value > 0xffff_ffffL) {
            throw new IllegalArgumentException("value must fit an unsigned 32-bit integer");
        }
        packedValue = (int)value;
    }

    public Vector3 ToVector3() {
        return new Vector3(getR() / 255.0f, getG() / 255.0f, getB() / 255.0f);
    }

    public Vector4 ToVector4() {
        return new Vector4(getR() / 255.0f, getG() / 255.0f, getB() / 255.0f, getA() / 255.0f);
    }

    public static Color FromNonPremultiplied(int red, int green, int blue, int alpha) {
        int a = clampByte(alpha);
        return new Color(
                clampByte((int)((long)red * a / 255L)),
                clampByte((int)((long)green * a / 255L)),
                clampByte((int)((long)blue * a / 255L)),
                a);
    }

    public static Color FromNonPremultiplied(Vector4 vector) {
        Objects.requireNonNull(vector, "vector");
        return new Color(vector.X * vector.W, vector.Y * vector.W, vector.Z * vector.W, vector.W);
    }

    public static Color Lerp(Color value1, Color value2, float amount) {
        Objects.requireNonNull(value1, "value1");
        Objects.requireNonNull(value2, "value2");
        int fraction = packFixed16(amount);
        return new Color(
                value1.getR() + (((value2.getR() - value1.getR()) * fraction) >> 16),
                value1.getG() + (((value2.getG() - value1.getG()) * fraction) >> 16),
                value1.getB() + (((value2.getB() - value1.getB()) * fraction) >> 16),
                value1.getA() + (((value2.getA() - value1.getA()) * fraction) >> 16));
    }

    public static Color Multiply(Color value, float scale) {
        Objects.requireNonNull(value, "value");
        float scaled = scale * 65_536.0f;
        int fixedScale;
        if (Float.isNaN(scaled) || scaled <= 0.0f) {
            fixedScale = 0;
        } else if (scaled >= 16_777_215.0f) {
            fixedScale = 16_777_215;
        } else {
            fixedScale = (int)scaled;
        }
        return new Color(
                Math.min(255, (value.getR() * fixedScale) >> 16),
                Math.min(255, (value.getG() * fixedScale) >> 16),
                Math.min(255, (value.getB() * fixedScale) >> 16),
                Math.min(255, (value.getA() * fixedScale) >> 16));
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof Color color && packedValue == color.packedValue;
    }

    @Override
    public int hashCode() {
        return packedValue;
    }

    @Override
    public String toString() {
        return "{R:" + getR() + " G:" + getG() + " B:" + getB() + " A:" + getA() + '}';
    }

    private static Color named(int red, int green, int blue, int alpha) {
        return new Color(pack(red, green, blue, alpha), true);
    }

    private static int pack(int red, int green, int blue, int alpha) {
        return red | (green << 8) | (blue << 16) | (alpha << 24);
    }

    private static int packUnit(float value) {
        float scaled = value * 255.0f;
        if (Float.isNaN(scaled) || scaled <= 0.0f) {
            return 0;
        }
        if (scaled >= 255.0f) {
            return 255;
        }
        return (int)Math.rint(scaled);
    }

    private static int packFixed16(float amount) {
        float scaled = amount * 65_536.0f;
        if (Float.isNaN(scaled) || scaled <= 0.0f) {
            return 0;
        }
        if (scaled >= 16_777_215.0f) {
            return 16_777_215;
        }
        return (int)scaled;
    }

    private static int clampByte(int value) {
        return Math.max(0, Math.min(255, value));
    }

    private static int requireByte(int value, String name) {
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException(name + " must be between 0 and 255");
        }
        return value;
    }

    private void ensureMutable() {
        if (frozen) {
            throw new UnsupportedOperationException("Named Color values are immutable snapshots");
        }
    }
}
