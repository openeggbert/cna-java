package org.openeggbert.cna.internal;

import Microsoft.Xna.Framework.Game;

import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

/** JNI entry point for CNA's stable C ABI. This class is not application API. */
public final class NativeBindings {

    /** CNA C headers used to compile this binding: ABI 0.7.0. */
    public static final int COMPILED_ABI_VERSION = encodeVersion(0, 7, 0);

    private static boolean bridgeLoaded;
    private static int runtimeAbiVersion;
    private static final Map<Game, NativeGameHandle> GAMES = new WeakHashMap<>();

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
        NativeGameHandle handle;
        synchronized (GAMES) {
            handle = GAMES.get(game);
        }
        if (handle == null || handle.isClosed()) {
            throw new IllegalStateException("GraphicsDevice is unavailable before Game.Run");
        }
        clear(handle, red, green, blue, alpha);
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

    static void destroyGame(Game game, long handle) {
        int result = nativeDestroyGame(handle);
        // CNA documents CALLBACK as released: shutdown completed but a callback reported failure.
        if (result != 0 && result != 9) {
            throw failure("cna_game_destroy", result);
        }
        synchronized (GAMES) {
            GAMES.remove(game);
        }
    }

    private static void check(String operation, int result) {
        if (result != 0) {
            throw failure(operation, result);
        }
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

    private static native int nativeDestroyGame(long game);

    private static native String nativeLastErrorMessage();
}
