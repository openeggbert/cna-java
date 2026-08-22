package Microsoft.Xna.Framework;

/** XNA 4.0 single-precision interpolation and angle helpers. */
public final class MathHelper {

    public static final float E = 2.71828175f;
    public static final float Log10E = 0.4342945f;
    public static final float Log2E = 1.442695f;
    public static final float Pi = 3.14159274f;
    public static final float PiOver2 = 1.57079637f;
    public static final float PiOver4 = 0.7853982f;
    public static final float TwoPi = 6.28318548f;

    private MathHelper() {
    }

    public static float Barycentric(float value1, float value2, float value3, float amount1, float amount2) {
        return value1 + (amount1 * (value2 - value1)) + (amount2 * (value3 - value1));
    }

    public static float CatmullRom(float value1, float value2, float value3, float value4, float amount) {
        float squared = amount * amount;
        float cubed = amount * squared;
        return 0.5f * ((2.0f * value2) + ((-value1 + value3) * amount)
                + (((2.0f * value1) - (5.0f * value2) + (4.0f * value3) - value4) * squared)
                + (((-value1 + (3.0f * value2) - (3.0f * value3) + value4) * cubed)));
    }

    public static float Clamp(float value, float min, float max) {
        value = value > max ? max : value;
        return value < min ? min : value;
    }

    public static float Distance(float value1, float value2) {
        return Math.abs(value1 - value2);
    }

    public static float Hermite(float value1, float tangent1, float value2, float tangent2, float amount) {
        if (amount == 0.0f) {
            return value1;
        }
        if (amount == 1.0f) {
            return value2;
        }
        float squared = amount * amount;
        float cubed = squared * amount;
        return (((2.0f * value1) - (2.0f * value2) + tangent2 + tangent1) * cubed)
                + (((3.0f * value2) - (3.0f * value1) - (2.0f * tangent1) - tangent2) * squared)
                + (tangent1 * amount) + value1;
    }

    public static float Lerp(float value1, float value2, float amount) {
        return value1 + ((value2 - value1) * amount);
    }

    public static float Max(float value1, float value2) {
        return Math.max(value1, value2);
    }

    public static float Min(float value1, float value2) {
        return Math.min(value1, value2);
    }

    public static float SmoothStep(float value1, float value2, float amount) {
        float bounded = Clamp(amount, 0.0f, 1.0f);
        return Hermite(value1, 0.0f, value2, 0.0f, bounded);
    }

    public static float ToDegrees(float radians) {
        return radians * 57.29578f;
    }

    public static float ToRadians(float degrees) {
        return degrees * 0.0174532924f;
    }

    public static float WrapAngle(float angle) {
        angle %= TwoPi;
        if (angle <= -Pi) {
            return angle + TwoPi;
        }
        if (angle > Pi) {
            return angle - TwoPi;
        }
        return angle;
    }
}

