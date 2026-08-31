package org.openeggbert.cna.extensions.avatars;

import Microsoft.Xna.Framework.GamerServices.AvatarAnimation;
import Microsoft.Xna.Framework.GamerServices.AvatarAnimationPreset;
import Microsoft.Xna.Framework.GamerServices.AvatarBodyType;
import Microsoft.Xna.Framework.GamerServices.AvatarRenderer;
import org.openeggbert.cna.internal.FacadeFactory;
import org.openeggbert.cna.internal.NativeGamerServices;
import org.openeggbert.cna.internal.generated.NativeGamerServicesRoutes;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * What CNA's avatar layer can do that XNA's {@code AvatarRenderer} has no member for.
 *
 * <p>XNA's renderer draws the Xbox runtime's own avatar and exposes nothing about it. CNA draws
 * one itself, which raises three questions XNA never had to answer: what does it look like, what
 * is it doing, and where does the content come from. This is where those live.
 *
 * <p><strong>Drawing a real skinned model is not here yet.</strong> CNA can switch a renderer
 * from its placeholder to a real model and draw a named clip, but the model it wants is a
 * {@code CNA_SkinnedModelEXT}, which this projection does not have a Java type for. Those two
 * routes stay unbound rather than being exposed as a raw handle: an API whose parameter is a
 * native pointer is not one a game can safely use, and half of a two-step contract is worse than
 * none. JAVA-EXT-007 is the slice that finishes it.
 */
public final class AvatarExtensions {

    private AvatarExtensions() {
    }

    /**
     * Sets the colours a real-rendered avatar is drawn in.
     *
     * @param renderer the renderer to colour
     * @param appearance the five colours to use
     */
    public static void SetAppearance(AvatarRenderer renderer, AvatarAppearance appearance) {
        NativeGamerServices.requireAvailable("AvatarExtensions.SetAppearance");
        Objects.requireNonNull(renderer, "renderer");
        Objects.requireNonNull(appearance, "appearance");
        NativeGamerServices.check("AvatarExtensions.SetAppearance",
                NativeGamerServicesRoutes.avatarRendererSetAppearanceExt(
                        FacadeFactory.avatarRendererHandle(renderer), appearance.toLeaves()));
    }

    /**
     * Returns the clip an animation is playing by name.
     *
     * @param animation the animation to ask
     * @return its clip name, empty when it is playing a preset with no real clip assigned
     */
    public static String GetRealClipName(AvatarAnimation animation) {
        Objects.requireNonNull(animation, "animation");
        NativeGamerServices.requireAvailable("AvatarExtensions.GetRealClipName");
        long handle = FacadeFactory.avatarAnimationHandle(animation);
        return NativeGamerServices.text("AvatarExtensions.GetRealClipName",
                out -> NativeGamerServicesRoutes
                        .avatarAnimationGetRealClipNameSizeExt(handle, out),
                (buffer, out) -> NativeGamerServicesRoutes
                        .avatarAnimationCopyRealClipNameExt(handle, buffer, out));
    }

    /**
     * Names the clip an animation should play.
     *
     * @param animation the animation to retarget
     * @param clipName the clip name, which CNA copies
     */
    public static void SetRealClipName(AvatarAnimation animation, String clipName) {
        NativeGamerServices.requireAvailable("AvatarExtensions.SetRealClipName");
        Objects.requireNonNull(animation, "animation");
        Objects.requireNonNull(clipName, "clipName");
        NativeGamerServices.check("AvatarExtensions.SetRealClipName",
                NativeGamerServicesRoutes.avatarAnimationSetRealClipNameExt(
                        FacadeFactory.avatarAnimationHandle(animation),
                        clipName.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Returns the clip name one of XNA's canonical presets maps to.
     *
     * <p>{@code AvatarAnimationPreset} is a closed enumeration in XNA and says nothing about what
     * it plays. A title supplying its own avatar content needs the name to author against.
     *
     * @param preset the preset to resolve
     * @return the clip name it maps to
     */
    public static String GetClipName(AvatarAnimationPreset preset) {
        NativeGamerServices.requireAvailable("AvatarExtensions.GetClipName");
        Objects.requireNonNull(preset, "preset");
        return NativeGamerServices.text("AvatarExtensions.GetClipName",
                out -> NativeGamerServicesRoutes
                        .avatarAnimationPresetGetClipNameSizeExt(preset.ordinal(), out),
                (buffer, out) -> NativeGamerServicesRoutes
                        .avatarAnimationPresetCopyClipNameExt(preset.ordinal(), buffer, out));
    }

    /**
     * Returns the content asset name a body type maps to.
     *
     * @param bodyType the body type to resolve
     * @return the asset name its model is loaded from
     */
    public static String GetContentName(AvatarBodyType bodyType) {
        NativeGamerServices.requireAvailable("AvatarExtensions.GetContentName");
        Objects.requireNonNull(bodyType, "bodyType");
        return NativeGamerServices.text("AvatarExtensions.GetContentName",
                out -> NativeGamerServicesRoutes
                        .avatarBodyTypeGetContentNameSizeExt(bodyType.ordinal(), out),
                (buffer, out) -> NativeGamerServicesRoutes
                        .avatarBodyTypeCopyContentNameExt(bodyType.ordinal(), buffer, out));
    }
}
