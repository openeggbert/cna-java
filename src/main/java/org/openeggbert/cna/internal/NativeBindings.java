package org.openeggbert.cna.internal;

import Microsoft.Xna.Framework.Game;
import Microsoft.Xna.Framework.WindowHandle;

import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

/** JNI entry point for CNA's stable C ABI. This class is not application API. */
public final class NativeBindings {

    /** CNA C headers used to compile this binding: ABI 0.7.0. */
    public static final int COMPILED_ABI_VERSION = encodeVersion(0, 7, 0);

    private static boolean bridgeLoaded;
    private static int runtimeAbiVersion;
    private static final Map<Game, NativeGameHandle> GAMES = new WeakHashMap<>();
    private static final Map<WindowHandle, Long> WINDOW_HANDLES = new WeakHashMap<>();
    private static Game currentGame;

    private NativeBindings() {
    }

    /** Loads the JNI adapter and the configured CNA C ABI library, then checks compatibility. */
    public static synchronized int requireAvailable() {
        if (runtimeAbiVersion != 0) {
            return runtimeAbiVersion;
        }
        loadBridge();

        String cnaLibrary = configuredCnaLibrary();
        int version = nativeLoadCna(cnaLibrary);
        int compiledMajor = versionMajor(COMPILED_ABI_VERSION);
        if (versionMajor(version) != compiledMajor || version < COMPILED_ABI_VERSION) {
            throw new UnsatisfiedLinkError(
                    "CNA C ABI mismatch: cna-java requires at least "
                            + formatVersion(COMPILED_ABI_VERSION) + " with major " + compiledMajor
                            + ", but loaded " + formatVersion(version));
        }
        runtimeAbiVersion = version;
        return version;
    }

    /** Returns whether a compatible native backend can be loaded without throwing. */
    public static boolean isAvailable() {
        try {
            requireAvailable();
            return true;
        } catch (LinkageError | RuntimeException unavailable) {
            return false;
        }
    }

    /** Returns a human-readable encoded ABI version. */
    public static String formatVersion(int version) {
        return versionMajor(version) + "." + ((version >>> 8) & 0xff) + "." + (version & 0xff);
    }

    public static NativeGameHandle createGame(
            Game game, String title, boolean fixedTimeStep, long targetTicks) {
        requireAvailable();
        long handle = nativeCreateGame(
                game, title.getBytes(StandardCharsets.UTF_8), fixedTimeStep, targetTicks);
        if (handle == 0L) {
            throw failure("cna_game_create", -1);
        }
        NativeGameHandle nativeGame = new NativeGameHandle(handle, game);
        synchronized (GAMES) {
            GAMES.put(game, nativeGame);
            currentGame = game;
        }
        return nativeGame;
    }

    public static void run(NativeGameHandle game) {
        check("cna_game_run", nativeRun(game.requireValue()));
    }

    public static void runOneFrame(NativeGameHandle game) {
        check("cna_game_run_one_frame", nativeRunOneFrame(game.requireValue()));
    }

    public static void resetElapsedTime(NativeGameHandle game) {
        check("cna_game_reset_elapsed_time", nativeResetElapsedTime(game.requireValue()));
    }

    public static void suppressDraw(NativeGameHandle game) {
        check("cna_game_suppress_draw", nativeSuppressDraw(game.requireValue()));
    }

    public static void tick(NativeGameHandle game) {
        check("cna_game_tick", nativeTick(game.requireValue()));
    }

    public static void requestExit(NativeGameHandle game) {
        check("cna_game_request_exit", nativeRequestExit(game.requireValue()));
    }

    public static void clear(NativeGameHandle game, int red, int green, int blue, int alpha) {
        check("cna_game_clear", nativeClear(game.requireValue(), red, green, blue, alpha));
    }

    /** Internal adapter used by strict facades without exposing a native handle. */
    public static void clear(Game game, int red, int green, int blue, int alpha) {
        clear(gameHandle(game, "GraphicsDevice"), red, green, blue, alpha);
    }

    public static void setMouseVisible(NativeGameHandle game, boolean visible) {
        check("cna_game_set_is_mouse_visible", nativeSetMouseVisible(game.requireValue(), visible));
    }

    public static boolean getMouseVisible(NativeGameHandle game) {
        return booleanResult(
                "cna_game_get_is_mouse_visible", nativeGetMouseVisible(game.requireValue()));
    }

    public static boolean getIsActive(NativeGameHandle game) {
        return booleanResult("cna_game_get_is_active", nativeGetIsActive(game.requireValue()));
    }

    public static void setFixedTimeStep(NativeGameHandle game, boolean value) {
        check("cna_game_set_is_fixed_time_step", nativeSetFixedTimeStep(game.requireValue(), value));
    }

    public static boolean getFixedTimeStep(NativeGameHandle game) {
        return booleanResult(
                "cna_game_get_is_fixed_time_step", nativeGetFixedTimeStep(game.requireValue()));
    }

    public static void setTargetElapsedTime(NativeGameHandle game, long ticks) {
        check("cna_game_set_target_elapsed_time_ticks",
                nativeSetTargetElapsedTime(game.requireValue(), ticks));
    }

    public static long getTargetElapsedTime(NativeGameHandle game) {
        return longResult("cna_game_get_target_elapsed_time_ticks",
                nativeGetTargetElapsedTime(game.requireValue()));
    }

    public static void setInactiveSleepTime(NativeGameHandle game, long ticks) {
        check("cna_game_set_inactive_sleep_time_ticks",
                nativeSetInactiveSleepTime(game.requireValue(), ticks));
    }

    public static long getInactiveSleepTime(NativeGameHandle game) {
        return longResult("cna_game_get_inactive_sleep_time_ticks",
                nativeGetInactiveSleepTime(game.requireValue()));
    }

    public static boolean getWindowAllowUserResizing(Game game) {
        return booleanResult("cna_game_window_get_allow_user_resizing",
                nativeGetWindowAllowUserResizing(gameHandle(game, "GameWindow").requireValue()));
    }

    public static void setWindowAllowUserResizing(Game game, boolean value) {
        check("cna_game_window_set_allow_user_resizing",
                nativeSetWindowAllowUserResizing(
                        gameHandle(game, "GameWindow").requireValue(), value));
    }

    public static int[] getWindowClientBounds(Game game) {
        int[] value = new int[4];
        check("cna_game_window_get_client_bounds",
                nativeGetWindowClientBounds(gameHandle(game, "GameWindow").requireValue(), value));
        return value;
    }

    public static int getWindowCurrentOrientation(Game game) {
        long value = longResult("cna_game_window_get_current_orientation",
                nativeGetWindowCurrentOrientation(gameHandle(game, "GameWindow").requireValue()));
        return Math.toIntExact(value);
    }

    public static long getWindowHandle(Game game) {
        long[] value = new long[1];
        check("cna_game_window_get_native_handle_ext",
                nativeGetWindowHandle(gameHandle(game, "GameWindow").requireValue(), value));
        return value[0];
    }

    public static String getWindowScreenDeviceName(Game game) {
        long gameValue = gameHandle(game, "GameWindow").requireValue();
        int size = Math.toIntExact(longResult("cna_game_window_get_screen_device_name_size",
                nativeGetWindowScreenDeviceNameSize(gameValue)));
        if (size == 0) {
            return "";
        }
        byte[] utf8 = new byte[size];
        check("cna_game_window_copy_screen_device_name",
                nativeCopyWindowScreenDeviceName(gameValue, utf8));
        return new String(utf8, StandardCharsets.UTF_8);
    }

    public static void setWindowTitle(Game game, String title) {
        check("cna_game_set_window_title",
                nativeSetWindowTitle(gameHandle(game, "GameWindow").requireValue(),
                        Objects.requireNonNull(title, "title").getBytes(StandardCharsets.UTF_8)));
    }

    public static void beginWindowScreenDeviceChange(Game game, boolean willBeFullScreen) {
        check("cna_game_window_begin_screen_device_change",
                nativeBeginWindowScreenDeviceChange(
                        gameHandle(game, "GameWindow").requireValue(), willBeFullScreen));
    }

    public static void endWindowScreenDeviceChange(
            Game game,
            String screenDeviceName,
            int clientWidth,
            int clientHeight) {
        check("cna_game_window_end_screen_device_change",
                nativeEndWindowScreenDeviceChange(
                        gameHandle(game, "GameWindow").requireValue(),
                        Objects.requireNonNull(screenDeviceName, "screenDeviceName")
                                .getBytes(StandardCharsets.UTF_8),
                        clientWidth, clientHeight));
    }

    /** Captures a copy of the current XNA keyboard bit set. A negative player selects no slot. */
    public static long[] getKeyboardState(int playerIndex) {
        NativeGameHandle game = currentGameHandle("Keyboard.GetState");
        long[] words = new long[4];
        check(playerIndex < 0 ? "cna_keyboard_get_state" : "cna_keyboard_get_state_for_player",
                nativeGetKeyboardState(game.requireValue(), playerIndex, words));
        return words;
    }

    public static int[] getMouseState() {
        int[] state = new int[4];
        check("cna_mouse_get_state",
                nativeGetMouseState(currentGameHandle("Mouse.GetState").requireValue(), state));
        return state;
    }

    public static void setMousePosition(int x, int y) {
        check("cna_mouse_set_position",
                nativeSetMousePosition(currentGameHandle("Mouse.SetPosition").requireValue(), x, y));
    }

    public static WindowHandle getMouseWindowHandle() {
        long[] output = new long[1];
        check("cna_mouse_get_window_handle", nativeGetMouseWindowHandle(
                currentGameHandle("Mouse.WindowHandle").requireValue(), output));
        if (output[0] == 0L) {
            return WindowHandle.Zero;
        }
        synchronized (GAMES) {
            WindowHandle known = findWindowHandle(output[0]);
            if (known != null) {
                return known;
            }
        }

        Game game;
        synchronized (GAMES) {
            game = currentGame;
        }
        WindowHandle gameWindow = game.getWindow().getHandle();
        synchronized (GAMES) {
            Long value = WINDOW_HANDLES.get(gameWindow);
            if (value != null && value == output[0]) {
                return gameWindow;
            }
        }
        throw new IllegalStateException("CNA returned an unrecognized opaque mouse window token");
    }

    public static void setMouseWindowHandle(WindowHandle window) {
        long value;
        synchronized (GAMES) {
            Long registered = WINDOW_HANDLES.get(Objects.requireNonNull(window, "window"));
            if (registered == null && !window.getIsZero()) {
                throw new IllegalArgumentException(
                        "Mouse.WindowHandle accepts only an opaque token issued by CNA-Java");
            }
            value = registered == null ? 0L : registered;
        }
        check("cna_mouse_set_window_handle", nativeSetMouseWindowHandle(
                currentGameHandle("Mouse.WindowHandle").requireValue(), value));
    }

    public static void registerWindowHandle(WindowHandle window, long value) {
        if (value != 0L) {
            synchronized (GAMES) {
                WINDOW_HANDLES.put(Objects.requireNonNull(window, "window"), value);
            }
        }
    }

    static void destroyGame(Game game, long handle) {
        int result = nativeDestroyGame(handle);
        // CNA documents CALLBACK as released: shutdown completed but a callback reported failure.
        if (result != 0 && result != 9) {
            throw failure("cna_game_destroy", result);
        }
        synchronized (GAMES) {
            GAMES.remove(game);
            if (currentGame == game) {
                currentGame = null;
            }
        }
    }

    private static void check(String operation, int result) {
        if (result != 0) {
            throw failure(operation, result);
        }
    }

    private static NativeGameHandle gameHandle(Game game, String owner) {
        NativeGameHandle handle;
        synchronized (GAMES) {
            handle = GAMES.get(Objects.requireNonNull(game, "game"));
        }
        if (handle == null || handle.isClosed()) {
            throw new IllegalStateException(owner + " is unavailable before native Game creation");
        }
        return handle;
    }

    private static NativeGameHandle currentGameHandle(String owner) {
        NativeGameHandle handle;
        synchronized (GAMES) {
            handle = currentGame == null ? null : GAMES.get(currentGame);
        }
        if (handle == null || handle.isClosed()) {
            throw new IllegalStateException(
                    owner + " requires a live CNA Game on the current process");
        }
        return handle;
    }

    private static WindowHandle findWindowHandle(long value) {
        for (Map.Entry<WindowHandle, Long> entry : WINDOW_HANDLES.entrySet()) {
            if (entry.getValue() == value) {
                return entry.getKey();
            }
        }
        return null;
    }

    private static boolean booleanResult(String operation, int result) {
        if (result < 0) {
            throw failure(operation, -result);
        }
        return result != 0;
    }

    private static long longResult(String operation, long result) {
        if (result < 0L) {
            throw failure(operation, Math.toIntExact(-result));
        }
        return result;
    }

    private static CnaNativeException failure(String operation, int result) {
        String diagnostic;
        try {
            diagnostic = nativeLastErrorMessage();
        } catch (LinkageError ignored) {
            diagnostic = "native diagnostic unavailable";
        }
        return new CnaNativeException(operation, result, diagnostic);
    }

    private static void loadBridge() {
        if (bridgeLoaded) {
            return;
        }
        String explicit = firstNonBlank(
                System.getProperty("cna.java.jniLibrary"),
                System.getenv("CNA_JNI_LIBRARY"));
        if (explicit != null) {
            System.load(Path.of(explicit).toAbsolutePath().normalize().toString());
        } else {
            System.loadLibrary("cna_java_jni");
        }
        bridgeLoaded = true;
    }

    private static String configuredCnaLibrary() {
        String explicit = firstNonBlank(
                System.getProperty("cna.native.library"),
                System.getenv("CNA_NATIVE_LIBRARY"));
        if (explicit != null) {
            return Path.of(explicit).toAbsolutePath().normalize().toString();
        }

        String directory = firstNonBlank(
                System.getProperty("cna.native.dir"),
                System.getenv("CNA_NATIVE_DIR"));
        if (directory == null) {
            return null;
        }
        return Path.of(directory, platformCnaLibraryName()).toAbsolutePath().normalize().toString();
    }

    private static String platformCnaLibraryName() {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (os.contains("win")) {
            return "cna_c_api.dll";
        }
        if (os.contains("mac") || os.contains("darwin")) {
            return "libcna_c_api.dylib";
        }
        return "libcna_c_api.so";
    }

    private static String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        return second != null && !second.isBlank() ? second : null;
    }

    private static int encodeVersion(int major, int minor, int patch) {
        return ((major & 0xffff) << 16) | ((minor & 0xff) << 8) | (patch & 0xff);
    }

    private static int versionMajor(int version) {
        return version >>> 16;
    }

    private static native int nativeLoadCna(String path);

    private static native long nativeCreateGame(
            Game game, byte[] titleUtf8, boolean fixedTimeStep, long targetElapsedTimeTicks);

    private static native int nativeRun(long game);

    private static native int nativeRunOneFrame(long game);

    private static native int nativeResetElapsedTime(long game);

    private static native int nativeSuppressDraw(long game);

    private static native int nativeTick(long game);

    private static native int nativeRequestExit(long game);

    private static native int nativeClear(long game, int red, int green, int blue, int alpha);

    private static native int nativeSetMouseVisible(long game, boolean visible);

    private static native int nativeGetMouseVisible(long game);

    private static native int nativeGetIsActive(long game);

    private static native int nativeSetFixedTimeStep(long game, boolean value);

    private static native int nativeGetFixedTimeStep(long game);

    private static native int nativeSetTargetElapsedTime(long game, long ticks);

    private static native long nativeGetTargetElapsedTime(long game);

    private static native int nativeSetInactiveSleepTime(long game, long ticks);

    private static native long nativeGetInactiveSleepTime(long game);

    private static native int nativeGetWindowAllowUserResizing(long game);

    private static native int nativeSetWindowAllowUserResizing(long game, boolean value);

    private static native int nativeGetWindowClientBounds(long game, int[] value);

    private static native long nativeGetWindowCurrentOrientation(long game);

    private static native int nativeGetWindowHandle(long game, long[] value);

    private static native long nativeGetWindowScreenDeviceNameSize(long game);

    private static native int nativeCopyWindowScreenDeviceName(long game, byte[] destination);

    private static native int nativeSetWindowTitle(long game, byte[] titleUtf8);

    private static native int nativeBeginWindowScreenDeviceChange(
            long game, boolean willBeFullScreen);

    private static native int nativeEndWindowScreenDeviceChange(
            long game, byte[] screenDeviceNameUtf8, int clientWidth, int clientHeight);

    private static native int nativeGetKeyboardState(
            long game, int playerIndex, long[] words);

    private static native int nativeGetMouseState(long game, int[] state);

    private static native int nativeSetMousePosition(long game, int x, int y);

    private static native int nativeGetMouseWindowHandle(long game, long[] window);

    private static native int nativeSetMouseWindowHandle(long game, long window);

    private static native int nativeDestroyGame(long game);

    private static native String nativeLastErrorMessage();
}
