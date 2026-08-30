package Microsoft.Xna.Framework.GamerServices;

import Microsoft.Xna.Framework.PlayerIndex;
import org.openeggbert.cna.internal.CompletedAsyncResult;
import org.openeggbert.cna.internal.NativeGamerServices;
import org.openeggbert.cna.internal.generated.NativeGamerServicesRoutes;
import System.AsyncCallback;
import System.IAsyncResult;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The platform's system UI: sign-in, gamer cards, messages, the on-screen keyboard and
 * message boxes.
 *
 * <p>The type is {@code abstract sealed} in XNA, the CLR spelling of a static class, so it is
 * a final class with a private constructor here and cannot be instantiated or extended. A Guide screen is modal: XNA raises
 * {@link GuideAlreadyVisibleException} when a second one is requested while the first is up,
 * and this projection raises the same exception for the same condition.
 */
public final class Guide {

    private Guide() {
    }

    public static IAsyncResult BeginShowKeyboardInput(
            PlayerIndex player, String title, String description, String defaultText,
            AsyncCallback callback, Object state, boolean usePasswordMode) {
        return CompletedAsyncResult.begin(callback, state, () -> {
            showKeyboardInput(player, title, description, defaultText, usePasswordMode);
            return keyboardInputResult();
        });
    }

    public static IAsyncResult BeginShowKeyboardInput(
            PlayerIndex player, String title, String description, String defaultText,
            AsyncCallback callback, Object state) {
        return BeginShowKeyboardInput(player, title, description, defaultText,
                callback, state, false);
    }

    public static IAsyncResult BeginShowMessageBox(
            PlayerIndex player, String title, String text, Iterable<String> buttons,
            int focusButton, MessageBoxIcon icon, AsyncCallback callback, Object state) {
        return CompletedAsyncResult.begin(callback, state, () -> {
            showMessageBox(player, title, text, buttons, focusButton, icon);
            return messageBoxResult();
        });
    }

    public static IAsyncResult BeginShowMessageBox(
            String title, String text, Iterable<String> buttons, int focusButton,
            MessageBoxIcon icon, AsyncCallback callback, Object state) {
        return BeginShowMessageBox(PlayerIndex.One, title, text, buttons, focusButton, icon,
                callback, state);
    }

    public static void DelayNotifications(Duration delay) {
        check("Guide.DelayNotifications", NativeGamerServicesRoutes.guideDelayNotifications(
                NativeGamerServices.ticks(Objects.requireNonNull(delay, "delay"))));
    }

    /** Returns the text the player entered, or {@code null} when they cancelled. */
    public static String EndShowKeyboardInput(IAsyncResult result) {
        return CompletedAsyncResult.end(result, String.class);
    }

    /** Returns the chosen button's index, or {@code null} when the player dismissed the box. */
    public static Integer EndShowMessageBox(IAsyncResult result) {
        return CompletedAsyncResult.end(result, Integer.class);
    }

    public static void ShowComposeMessage(PlayerIndex player, String text,
            Iterable<Gamer> recipients) {
        check("Guide.ShowComposeMessage", NativeGamerServicesRoutes.guideShowComposeMessage(
                slot(player), NativeGamerServices.utf8(text), handles(recipients)));
    }

    public static void ShowFriendRequest(PlayerIndex player, Gamer gamer) {
        check("Guide.ShowFriendRequest", NativeGamerServicesRoutes.guideShowFriendRequest(
                slot(player), Objects.requireNonNull(gamer, "gamer").handle()));
    }

    public static void ShowFriends(PlayerIndex player) {
        check("Guide.ShowFriends", NativeGamerServicesRoutes.guideShowFriends(slot(player)));
    }

    public static void ShowGameInvite(PlayerIndex player, Iterable<Gamer> recipients) {
        check("Guide.ShowGameInvite", NativeGamerServicesRoutes.guideShowGameInvite(
                slot(player), handles(recipients)));
    }

    public static void ShowGameInvite(String sessionId) {
        check("Guide.ShowGameInvite", NativeGamerServicesRoutes.guideShowGameInviteForSession(
                NativeGamerServices.utf8(Objects.requireNonNull(sessionId, "sessionId"))));
    }

    public static void ShowGamerCard(PlayerIndex player, Gamer gamer) {
        check("Guide.ShowGamerCard", NativeGamerServicesRoutes.guideShowGamerCard(
                slot(player), Objects.requireNonNull(gamer, "gamer").handle()));
    }

    public static void ShowMarketplace(PlayerIndex player) {
        check("Guide.ShowMarketplace",
                NativeGamerServicesRoutes.guideShowMarketplace(slot(player)));
    }

    public static void ShowMessages(PlayerIndex player) {
        check("Guide.ShowMessages", NativeGamerServicesRoutes.guideShowMessages(slot(player)));
    }

    public static void ShowParty(PlayerIndex player) {
        check("Guide.ShowParty", NativeGamerServicesRoutes.guideShowParty(slot(player)));
    }

    public static void ShowPartySessions(PlayerIndex player) {
        check("Guide.ShowPartySessions",
                NativeGamerServicesRoutes.guideShowPartySessions(slot(player)));
    }

    public static void ShowPlayerReview(PlayerIndex player, Gamer gamer) {
        check("Guide.ShowPlayerReview", NativeGamerServicesRoutes.guideShowPlayerReview(
                slot(player), Objects.requireNonNull(gamer, "gamer").handle()));
    }

    public static void ShowPlayers(PlayerIndex player) {
        check("Guide.ShowPlayers", NativeGamerServicesRoutes.guideShowPlayers(slot(player)));
    }

    public static void ShowSignIn(int paneCount, boolean onlineOnly) {
        check("Guide.ShowSignIn",
                NativeGamerServicesRoutes.guideShowSignIn(paneCount, onlineOnly));
    }

    public static boolean getIsScreenSaverEnabled() {
        boolean[] value = new boolean[1];
        check("Guide.IsScreenSaverEnabled",
                NativeGamerServicesRoutes.guideGetIsScreenSaverEnabled(value));
        return value[0];
    }

    public static void setIsScreenSaverEnabled(boolean value) {
        check("Guide.IsScreenSaverEnabled",
                NativeGamerServicesRoutes.guideSetIsScreenSaverEnabled(value));
    }

    public static boolean getIsTrialMode() {
        boolean[] value = new boolean[1];
        check("Guide.IsTrialMode", NativeGamerServicesRoutes.guideGetIsTrialMode(value));
        return value[0];
    }

    protected static void setIsTrialMode(boolean value) {
        check("Guide.IsTrialMode", NativeGamerServicesRoutes.guideSetIsTrialMode(value));
    }

    public static boolean getIsVisible() {
        boolean[] value = new boolean[1];
        check("Guide.IsVisible", NativeGamerServicesRoutes.guideGetIsVisible(value));
        return value[0];
    }

    protected static void setIsVisible(boolean value) {
        check("Guide.IsVisible", NativeGamerServicesRoutes.guideSetIsVisible(value));
    }

    public static NotificationPosition getNotificationPosition() {
        int[] value = new int[1];
        check("Guide.NotificationPosition",
                NativeGamerServicesRoutes.guideGetNotificationPosition(value));
        return NotificationPosition.values()[value[0]];
    }

    public static void setNotificationPosition(NotificationPosition value) {
        check("Guide.NotificationPosition",
                NativeGamerServicesRoutes.guideSetNotificationPosition(
                        Objects.requireNonNull(value, "value").ordinal()));
    }

    public static boolean getSimulateTrialMode() {
        boolean[] value = new boolean[1];
        check("Guide.SimulateTrialMode",
                NativeGamerServicesRoutes.guideGetSimulateTrialMode(value));
        return value[0];
    }

    public static void setSimulateTrialMode(boolean value) {
        check("Guide.SimulateTrialMode",
                NativeGamerServicesRoutes.guideSetSimulateTrialMode(value));
    }

    private static void showKeyboardInput(PlayerIndex player, String title, String description,
            String defaultText, boolean usePasswordMode) {
        check("Guide.BeginShowKeyboardInput",
                NativeGamerServicesRoutes.guideBeginShowKeyboardInput(slot(player),
                        NativeGamerServices.utf8(Objects.requireNonNull(title, "title")),
                        NativeGamerServices.utf8(
                                Objects.requireNonNull(description, "description")),
                        NativeGamerServices.utf8(defaultText), usePasswordMode));
    }

    private static String keyboardInputResult() {
        boolean[] cancelled = new boolean[1];
        check("Guide.EndShowKeyboardInput",
                NativeGamerServicesRoutes.guideWasKeyboardInputCanceledExt(cancelled));
        if (cancelled[0]) {
            return null;
        }
        return NativeGamerServices.text("Guide.EndShowKeyboardInput",
                NativeGamerServicesRoutes::guideEndShowKeyboardInputSize,
                NativeGamerServicesRoutes::guideEndShowKeyboardInput);
    }

    private static void showMessageBox(PlayerIndex player, String title, String text,
            Iterable<String> buttons, int focusButton, MessageBoxIcon icon) {
        Objects.requireNonNull(buttons, "buttons");
        List<String> captions = new ArrayList<>();
        for (String caption : buttons) {
            captions.add(Objects.requireNonNull(caption, "buttons"));
        }
        if (captions.isEmpty() || captions.size() > 2) {
            throw new IllegalArgumentException(
                    "A Guide message box takes one or two buttons; received " + captions.size());
        }
        check("Guide.BeginShowMessageBox", NativeGamerServices.nativeGuideShowMessageBox(
                slot(player), NativeGamerServices.utf8(Objects.requireNonNull(title, "title")),
                NativeGamerServices.utf8(Objects.requireNonNull(text, "text")),
                NativeGamerServices.utf8(captions.get(0)),
                NativeGamerServices.utf8(captions.size() > 1 ? captions.get(1) : ""),
                captions.size(), focusButton,
                Objects.requireNonNull(icon, "icon").ordinal()));
    }

    private static Integer messageBoxResult() {
        boolean[] chosen = new boolean[1];
        int[] button = new int[1];
        check("Guide.EndShowMessageBox",
                NativeGamerServicesRoutes.guideEndShowMessageBox(chosen, button));
        return chosen[0] ? button[0] : null;
    }

    private static long[] handles(Iterable<Gamer> recipients) {
        Objects.requireNonNull(recipients, "recipients");
        List<Long> values = new ArrayList<>();
        for (Gamer gamer : recipients) {
            values.add(Objects.requireNonNull(gamer, "recipients").handle());
        }
        long[] handles = new long[values.size()];
        for (int index = 0; index < handles.length; index++) {
            handles[index] = values.get(index);
        }
        return handles;
    }

    private static int slot(PlayerIndex player) {
        return Objects.requireNonNull(player, "player").ordinal();
    }

    /**
     * Maps a Guide failure to the XNA exception for the same condition.
     *
     * <p>A modal Guide screen requested while another is up is CNA's
     * {@code CNA_RESULT_INVALID_STATE} once the dispatcher is running, which XNA reports as
     * {@link GuideAlreadyVisibleException} rather than as unavailability.
     */
    private static void check(String operation, int result) {
        if (result == 3 && GamerServicesDispatcher.getIsInitialized() && isVisibleQuiet()) {
            throw new GuideAlreadyVisibleException(operation + " while the Guide is visible");
        }
        NativeGamerServices.check(operation, result);
    }

    private static boolean isVisibleQuiet() {
        boolean[] value = new boolean[1];
        return NativeGamerServicesRoutes.guideGetIsVisible(value) == 0 && value[0];
    }
}
