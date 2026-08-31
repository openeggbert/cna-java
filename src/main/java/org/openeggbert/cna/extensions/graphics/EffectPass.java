package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.Effect;
import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * A post-process pass that draws the frame through an effect a game supplies.
 *
 * <p>A CNA extension, and the open end of the post-process chain: the sixteen built-in passes are
 * the ones CNA ships, and this is the one a game writes itself. With
 * {@link ShaderEffectFactory} for the shader and {@link RenderPipeline#addUserPass} for where it
 * runs, a game can put its own screen-space effect in the frame without CNA knowing anything
 * about it.
 *
 * <p><strong>Two constructors, two ownerships, and the difference is not cosmetic.</strong>
 * {@link #create} borrows: the effect must outlive the pass and disposing the pass leaves it
 * alive. {@link #createOwning} <em>consumes</em>: on success the Java effect stops owning anything
 * and must not be disposed, and the pass releases it when the pass is closed. On failure the
 * transfer did not happen and the effect is still the caller's -- which is the branch that makes
 * a refused transfer recoverable rather than a lost resource.
 *
 * <p><strong>{@code cna_post_process_effect_pass_get_effect} is deliberately not bound.</strong>
 * It mints a <em>new</em> handle on every call and documents that the handle must not be
 * destroyed, so asking twice leaks twice and there is no way to give either back. Measured, in
 * {@code tools/native-abi/probes/effect_pass_ownership.c}: two calls in a row return two different
 * handles. {@link #getEffect()} answers from the effect this object retained instead, which is the
 * same answer and costs nothing.
 */
public final class EffectPass extends PostProcessPass {

    private final boolean owning;
    private Effect effect;

    private EffectPass(long handle, Effect effect, boolean owning) {
        super(handle);
        this.effect = effect;
        this.owning = owning;
    }

    /**
     * Creates a pass that draws through a borrowed effect.
     *
     * @param graphicsDevice the device to draw on
     * @param effect the effect to draw through, or {@code null} for none; borrowed, and it must
     *        outlive the pass
     * @param name the pass's name, which is what its timing is reported under
     * @return the pass, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static EffectPass create(GraphicsDevice graphicsDevice, Effect effect, String name) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        Objects.requireNonNull(name, "name");
        long[] created = new long[1];
        GraphicsExtension.check("EffectPass.create",
                NativeEngineLayerRoutes.postProcessEffectPassCreate(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice),
                        handleOf(effect), utf8(name), created));
        return new EffectPass(created[0], effect, false);
    }

    /**
     * Creates a pass that takes ownership of an effect.
     *
     * <p>On success the effect is <strong>consumed</strong>: the Java object stops owning the
     * native effect, disposing it does nothing, and the pass releases the effect when it is
     * closed. On failure nothing was transferred and the caller still owns the effect, exactly as
     * before the call -- so a refused transfer is a recoverable mistake rather than a resource
     * nobody can free.
     *
     * @param graphicsDevice the device to draw on
     * @param effect the effect to hand over; consumed on success
     * @param name the pass's name
     * @return the pass, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static EffectPass createOwning(GraphicsDevice graphicsDevice, Effect effect,
            String name) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        Objects.requireNonNull(effect, "effect");
        Objects.requireNonNull(name, "name");
        long[] created = new long[1];
        // Checked before anything is surrendered: a refused transfer must leave the caller owning
        // exactly what it owned before, and surrendering first would strand the effect if CNA
        // said no.
        GraphicsExtension.check("EffectPass.createOwning",
                NativeEngineLayerRoutes.postProcessEffectPassCreateOwning(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice),
                        NativeBindings.nativeResourceHandle(effect), utf8(name), created));
        NativeBindings.surrenderResource(effect);
        return new EffectPass(created[0], null, true);
    }

    /**
     * Returns the effect this pass draws through.
     *
     * <p>{@code null} for a pass that has none, and {@code null} for an owning pass: the effect
     * an owning pass was given belongs to the pass, and handing back a Java object that no longer
     * owns it would be handing back something a caller could only misuse.
     *
     * @return the effect, or {@code null}
     */
    public Effect getEffect() {
        return effect;
    }

    /**
     * Reports whether this pass owns the effect it was created with.
     *
     * @return whether the effect was consumed at construction
     */
    public boolean isOwningItsEffect() {
        return owning;
    }

    /**
     * Replaces the effect this pass draws through, borrowing the new one.
     *
     * <p>On a pass created by {@link #createOwning}, the effect it was given stays owned by the
     * pass and is <em>not</em> released here -- CNA's own behaviour, and the reason the old effect
     * remains unreachable afterwards. {@link #getEffect()} then answers with the new borrowed
     * effect, and {@link #isOwningItsEffect()} stays true, because the pass is still holding the
     * one it consumed.
     *
     * @param value the effect to draw through, or {@code null} to draw nothing; borrowed, and it
     *        must outlive the pass
     */
    public void setEffect(Effect value) {
        GraphicsExtension.check("EffectPass.setEffect",
                NativeEngineLayerRoutes.postProcessEffectPassSetEffect(open(), handleOf(value)));
        effect = value;
    }

    private static long handleOf(Effect effect) {
        return effect == null ? 0L : NativeBindings.nativeResourceHandle(effect);
    }

    private static byte[] utf8(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }
}
