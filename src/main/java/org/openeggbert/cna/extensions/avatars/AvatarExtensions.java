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
 * <p><strong>Drawing a real skinned model is here now.</strong> CNA switches a renderer from its
 * placeholder to a model the game supplies -- there is no avatar asset service outside the Xbox
 * runtime, so the model is the game's -- and draws a named clip at a position. It waited on a
 * Java type for {@code CNA_SkinnedModelEXT} rather than on anything upstream, and
 * {@link org.openeggbert.cna.extensions.content.CnaSkinnedModel} is it.
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

    /**
     * Switches a renderer from CNA's placeholder to a real skinned model.
     *
     * <p>XNA's {@code AvatarRenderer} draws the Xbox runtime's own avatar and there is no such
     * service here, so the model is the game's own. The renderer keeps drawing it until it is
     * disposed.
     *
     * @param renderer the renderer to switch
     * @param graphicsDevice the device to draw on
     * @param model the skinned model to draw
     */
    public static void EnableRealRendering(AvatarRenderer renderer,
            Microsoft.Xna.Framework.Graphics.GraphicsDevice graphicsDevice,
            org.openeggbert.cna.extensions.content.CnaSkinnedModel model) {
        NativeGamerServices.requireAvailable("AvatarExtensions.EnableRealRendering");
        Objects.requireNonNull(renderer, "renderer");
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        Objects.requireNonNull(model, "model");
        NativeGamerServices.check("AvatarExtensions.EnableRealRendering",
                NativeGamerServicesRoutes.avatarRendererEnableRealRenderingExt(
                        FacadeFactory.avatarRendererHandle(renderer),
                        org.openeggbert.cna.internal.NativeBindings
                                .nativeGraphicsDeviceValue(graphicsDevice),
                        model.handle()));
    }

    /**
     * Draws the real skinned model at a point in a named clip.
     *
     * @param renderer the renderer to draw with
     * @param animationClipName the clip to draw
     * @param position where in the clip to draw
     * @param loop whether to wrap around at the end of the clip
     * @throws IllegalStateException when the renderer is disposed or has no real model, which
     *         CNA reports rather than drawing the placeholder instead
     */
    public static void DrawReal(AvatarRenderer renderer, String animationClipName,
            java.time.Duration position, boolean loop) {
        NativeGamerServices.requireAvailable("AvatarExtensions.DrawReal");
        Objects.requireNonNull(renderer, "renderer");
        Objects.requireNonNull(animationClipName, "animationClipName");
        Objects.requireNonNull(position, "position");
        NativeGamerServices.check("AvatarExtensions.DrawReal",
                NativeGamerServicesRoutes.avatarRendererDrawRealExt(
                        FacadeFactory.avatarRendererHandle(renderer),
                        NativeGamerServices.utf8(animationClipName),
                        position.toNanos() / 100L, loop));
    }
}
