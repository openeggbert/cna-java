package org.openeggbert.cna.extensions.avatars;

import Microsoft.Xna.Framework.Color;

import org.openeggbert.cna.internal.NativeGamerServices;
import org.openeggbert.cna.internal.generated.NativeGamerServicesRoutes;

import java.util.Objects;

/**
 * The colours a real-rendered avatar is drawn in.
 *
 * <p>XNA has no such type: an Xbox avatar's appearance came from the player's profile and a title
 * never chose it. Off that runtime somebody has to, so CNA lets a title set the five colours its
 * renderer uses.
 *
 * @param SkinColor the skin colour
 * @param HairColor the hair colour
 * @param ShirtColor the shirt colour
 * @param PantsColor the trouser colour
 * @param ShoesColor the shoe colour
 */
public record AvatarAppearance(
        Color SkinColor,
        Color HairColor,
        Color ShirtColor,
        Color PantsColor,
        Color ShoesColor) {

    /**
     * Returns the appearance CNA starts from.
     *
     * <p>Asked of CNA rather than written down here. The five colours are the renderer's own
     * defaults, and a set chosen in Java would be a guess that drifted the first time CNA changed
     * one.
     *
     * @return the default appearance
     */
    public static AvatarAppearance Default() {
        NativeGamerServices.requireAvailable("AvatarAppearance.Default");
        long[] values = new long[20];
        NativeGamerServices.check("AvatarAppearance.Default",
                NativeGamerServicesRoutes.avatarAppearanceInitExt(values));
        return new AvatarAppearance(color(values, 0), color(values, 4), color(values, 8),
                color(values, 12), color(values, 16));
    }

    long[] toLeaves() {
        long[] values = new long[20];
        write(values, 0, Objects.requireNonNull(SkinColor, "SkinColor"));
        write(values, 4, Objects.requireNonNull(HairColor, "HairColor"));
        write(values, 8, Objects.requireNonNull(ShirtColor, "ShirtColor"));
        write(values, 12, Objects.requireNonNull(PantsColor, "PantsColor"));
        write(values, 16, Objects.requireNonNull(ShoesColor, "ShoesColor"));
        return values;
    }

    private static Color color(long[] values, int offset) {
        return new Color((int) values[offset], (int) values[offset + 1],
                (int) values[offset + 2], (int) values[offset + 3]);
    }

    private static void write(long[] values, int offset, Color color) {
        values[offset] = color.getR();
        values[offset + 1] = color.getG();
        values[offset + 2] = color.getB();
        values[offset + 3] = color.getA();
    }
}
