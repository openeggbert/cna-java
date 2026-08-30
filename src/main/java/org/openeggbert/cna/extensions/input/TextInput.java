package org.openeggbert.cna.extensions.input;

import Microsoft.Xna.Framework.Rectangle;
import org.openeggbert.cna.internal.GamerEventPump;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeInputExtensionRoutes;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.IntConsumer;

/**
 * Receives the characters a keyboard layout actually produced.
 *
 * <p>A CNA extension. XNA's {@code Keyboard} reports which physical keys are down, which is the
 * wrong question for a name-entry field: it cannot tell an accented character from the dead key
 * that composed it, and it does not know the player's layout. This does.
 *
 * <p>Text input is off until {@link #Start()} is called, because a host with a screen keyboard
 * shows it while input is active. Characters arrive during {@code FrameworkDispatcher.Update},
 * which the game loop already pumps, so a listener runs on the game thread.
 *
 * <p>A character is delivered as an {@code int} code unit, not a {@code char}: a character
 * outside the basic plane arrives as two surrogate code units, exactly as CNA delivers it, so
 * nothing is lost or silently replaced.
 */
public final class TextInput {

    private static final int KIND_TEXT_INPUT = 30;

    private static final List<IntConsumer> LISTENERS = new CopyOnWriteArrayList<>();

    static {
        GamerEventPump.setInputHandler(TextInput::dispatch);
    }

    private TextInput() {
    }

    /** Adds a listener for each code unit the host produces. */
    public static void addTextInputListener(IntConsumer listener) {
        LISTENERS.add(Objects.requireNonNull(listener, "listener"));
        subscribe();
    }

    public static void removeTextInputListener(IntConsumer listener) {
        LISTENERS.remove(Objects.requireNonNull(listener, "listener"));
    }

    /** Starts plain text input. */
    public static void Start() {
        check("TextInput.Start",
                NativeInputExtensionRoutes.textInputStartExt(game()));
    }

    /** Starts text input of a stated kind, so a screen keyboard can match it. */
    public static void Start(TextInputType type) {
        check("TextInput.Start", NativeInputExtensionRoutes.textInputStartWithTypeExt(
                game(), Objects.requireNonNull(type, "type").ordinal()));
    }

    public static void Stop() {
        check("TextInput.Stop", NativeInputExtensionRoutes.textInputStopExt(game()));
    }

    public static boolean getIsActive() {
        boolean[] active = new boolean[1];
        check("TextInput.IsActive",
                NativeInputExtensionRoutes.textInputIsActiveExt(game(), active));
        return active[0];
    }

    public static boolean getIsScreenKeyboardShown() {
        boolean[] shown = new boolean[1];
        check("TextInput.IsScreenKeyboardShown",
                NativeInputExtensionRoutes.textInputIsScreenKeyboardShownExt(game(), shown));
        return shown[0];
    }

    /**
     * Tells the host where the text being edited is on screen.
     *
     * <p>A host that shows an input method window places it clear of this rectangle.
     */
    public static void setInputRectangle(Rectangle rectangle) {
        Objects.requireNonNull(rectangle, "rectangle");
        check("TextInput.setInputRectangle",
                NativeInputExtensionRoutes.textInputSetInputRectangleExt(game(),
                        new long[] {rectangle.X, rectangle.Y,
                            rectangle.Width, rectangle.Height}));
    }

    private static void subscribe() {
        NativeBindings.requireAvailable();
        GamerEventPump.ensureTextInputSubscribed();
    }

    private static void dispatch(long kind, long session, long first, long second, long flag) {
        if ((int) kind != KIND_TEXT_INPUT) {
            return;
        }
        for (IntConsumer listener : LISTENERS) {
            listener.accept((int) first);
        }
    }

    private static long game() {
        return InputExtension.game("TextInput");
    }

    private static void check(String operation, int result) {
        InputExtension.check(operation, result);
    }
}
