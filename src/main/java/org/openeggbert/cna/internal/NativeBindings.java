package org.openeggbert.cna.internal;

import Microsoft.Xna.Framework.Game;
import Microsoft.Xna.Framework.Color;
import Microsoft.Xna.Framework.Rectangle;
import Microsoft.Xna.Framework.Vector2;
import Microsoft.Xna.Framework.WindowHandle;
import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.GraphicsResource;
import Microsoft.Xna.Framework.Graphics.SpriteBatch;
import Microsoft.Xna.Framework.Graphics.SpriteEffects;
import Microsoft.Xna.Framework.Graphics.SpriteSortMode;
import Microsoft.Xna.Framework.Graphics.Texture2D;

import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private static final Map<GraphicsDevice, Game> DEVICES = new WeakHashMap<>();
    private static final Map<GraphicsResource, NativeResourceHandle> RESOURCES =
            new WeakHashMap<>();
    private static final Map<GraphicsResource, Game> RESOURCE_OWNERS = new WeakHashMap<>();
    private static final Map<Game, List<GraphicsResource>> GAME_RESOURCES = new WeakHashMap<>();
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

    public static void registerGraphicsDevice(GraphicsDevice device, Game game) {
        synchronized (GAMES) {
            DEVICES.put(Objects.requireNonNull(device, "device"),
                    Objects.requireNonNull(game, "game"));
        }
    }

    public static int[] createTexture2D(
            Texture2D texture,
            GraphicsDevice graphicsDevice,
            int width,
            int height,
            boolean mipMap,
            int format) {
        Game game = deviceGame(graphicsDevice);
        long[] output = new long[1];
        check("cna_texture2d_create", nativeCreateTexture2D(
                gameHandle(game, "Texture2D").requireValue(), width, height, mipMap, format, output));
        registerResource(game, texture, output[0], NativeBindings::destroyTexture2D);
        return textureInfoOrClose(texture);
    }

    public static int[] createTexture2DFromEncoded(
            Texture2D texture,
            GraphicsDevice graphicsDevice,
            byte[] encoded,
            int width,
            int height,
            boolean zoom,
            boolean resize) {
        Game game = deviceGame(graphicsDevice);
        long[] output = new long[1];
        check("cna_texture2d_create_from_encoded_memory", nativeCreateTexture2DFromEncoded(
                gameHandle(game, "Texture2D.FromStream").requireValue(),
                Objects.requireNonNull(encoded, "encoded"), width, height, zoom, resize, output));
        registerResource(game, texture, output[0], NativeBindings::destroyTexture2D);
        return textureInfoOrClose(texture);
    }

    public static void setTexture2DData(Texture2D texture, Color[] data) {
        Objects.requireNonNull(data, "data");
        int[] packed = new int[data.length];
        for (int index = 0; index < data.length; index++) {
            packed[index] = (int)Objects.requireNonNull(data[index], "data[" + index + "]")
                    .getPackedValue();
        }
        check("cna_texture2d_set_data_rgba8",
                nativeSetTexture2DData(resourceValue(texture), packed));
    }

    public static Color[] getTexture2DData(Texture2D texture, int pixelCount) {
        int[] packed = new int[pixelCount];
        check("cna_texture2d_get_data_rgba8",
                nativeGetTexture2DData(resourceValue(texture), packed));
        Color[] result = new Color[pixelCount];
        for (int index = 0; index < packed.length; index++) {
            Color color = new Color(0, 0, 0, 0);
            color.setPackedValue(Integer.toUnsignedLong(packed[index]));
            result[index] = color;
        }
        return result;
    }

    public static byte[] encodeTexture2D(Texture2D texture, int format, int width, int height) {
        long handle = resourceValue(texture);
        int size = Math.toIntExact(longResult("cna_texture2d_get_encoded_byte_count",
                nativeGetTexture2DEncodedSize(handle, format, width, height)));
        byte[] output = new byte[size];
        check("cna_texture2d_copy_encoded",
                nativeCopyTexture2DEncoded(handle, format, width, height, output));
        return output;
    }

    public static void createSpriteBatch(SpriteBatch spriteBatch, GraphicsDevice graphicsDevice) {
        Game game = deviceGame(graphicsDevice);
        long[] output = new long[1];
        check("cna_sprite_batch_create", nativeCreateSpriteBatch(
                gameHandle(game, "SpriteBatch").requireValue(), output));
        registerResource(game, spriteBatch, output[0], NativeBindings::destroySpriteBatch);
    }

    public static void beginSpriteBatch(SpriteBatch spriteBatch, SpriteSortMode sortMode) {
        check("cna_sprite_batch_begin", nativeBeginSpriteBatch(
                resourceValue(spriteBatch), Objects.requireNonNull(sortMode, "sortMode").ordinal()));
    }

    public static void drawSpriteRectangle(
            SpriteBatch spriteBatch,
            Texture2D texture,
            Rectangle destination,
            Rectangle source,
            Color color,
            float rotation,
            Vector2 origin,
            SpriteEffects effects,
            float layerDepth) {
        Rectangle destinationValue = new Rectangle(Objects.requireNonNull(destination, "destination"));
        Rectangle sourceValue = source == null ? new Rectangle() : new Rectangle(source);
        Color colorValue = new Color(Objects.requireNonNull(color, "color"));
        Vector2 originValue = new Vector2(Objects.requireNonNull(origin, "origin"));
        check("cna_sprite_batch_submit_many", nativeDrawSpriteRectangle(
                resourceValue(spriteBatch), resourceValue(texture),
                destinationValue.X, destinationValue.Y, destinationValue.Width, destinationValue.Height,
                sourceValue.X, sourceValue.Y, sourceValue.Width, sourceValue.Height,
                (int)colorValue.getPackedValue(), rotation, originValue.X, originValue.Y,
                Objects.requireNonNull(effects, "effects").getValue(), layerDepth));
    }

    public static void drawSpriteScaled(
            SpriteBatch spriteBatch,
            Texture2D texture,
            Vector2 position,
            Rectangle source,
            Color color,
            float rotation,
            Vector2 origin,
            Vector2 scale,
            SpriteEffects effects,
            float layerDepth) {
        Vector2 positionValue = new Vector2(Objects.requireNonNull(position, "position"));
        Rectangle sourceValue = source == null ? new Rectangle() : new Rectangle(source);
        Color colorValue = new Color(Objects.requireNonNull(color, "color"));
        Vector2 originValue = new Vector2(Objects.requireNonNull(origin, "origin"));
        Vector2 scaleValue = new Vector2(Objects.requireNonNull(scale, "scale"));
        check("cna_sprite_batch_submit_scaled_many", nativeDrawSpriteScaled(
                resourceValue(spriteBatch), resourceValue(texture), positionValue.X, positionValue.Y,
                sourceValue.X, sourceValue.Y, sourceValue.Width, sourceValue.Height,
                (int)colorValue.getPackedValue(), rotation, originValue.X, originValue.Y,
                scaleValue.X, scaleValue.Y, Objects.requireNonNull(effects, "effects").getValue(),
                layerDepth));
    }

    public static void endSpriteBatch(SpriteBatch spriteBatch) {
        check("cna_sprite_batch_end", nativeEndSpriteBatch(resourceValue(spriteBatch)));
    }

    public static void closeGraphicsResource(GraphicsResource resource) {
        NativeResourceHandle handle;
        synchronized (GAMES) {
            handle = RESOURCES.get(resource);
        }
        if (handle == null) {
            return;
        }
        handle.close();
        synchronized (GAMES) {
            RESOURCES.remove(resource);
            Game owner = RESOURCE_OWNERS.remove(resource);
            List<GraphicsResource> children = GAME_RESOURCES.get(owner);
            if (children != null) {
                children.remove(resource);
            }
        }
    }

    public static void closeGraphicsResources(Game game) {
        List<GraphicsResource> snapshot;
        synchronized (GAMES) {
            List<GraphicsResource> resources = GAME_RESOURCES.get(game);
            snapshot = resources == null ? List.of() : new ArrayList<>(resources);
        }
        Collections.reverse(snapshot);
        RuntimeException failure = null;
        for (GraphicsResource resource : snapshot) {
            try {
                resource.close();
            } catch (RuntimeException exception) {
                if (failure == null) {
                    failure = exception;
                } else {
                    failure.addSuppressed(exception);
                }
            }
        }
        if (failure != null) {
            throw failure;
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
            GAME_RESOURCES.remove(game);
            DEVICES.values().removeIf(value -> value == game);
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

    private static Game deviceGame(GraphicsDevice device) {
        Game game;
        synchronized (GAMES) {
            game = DEVICES.get(Objects.requireNonNull(device, "graphicsDevice"));
        }
        if (game == null) {
            throw new IllegalArgumentException("GraphicsDevice was not created by CNA-Java");
        }
        return game;
    }

    private static void registerResource(
            Game game,
            GraphicsResource resource,
            long value,
            java.util.function.LongConsumer releaser) {
        NativeResourceHandle handle = new NativeResourceHandle(value, releaser);
        synchronized (GAMES) {
            RESOURCES.put(resource, handle);
            RESOURCE_OWNERS.put(resource, game);
            GAME_RESOURCES.computeIfAbsent(game, ignored -> new ArrayList<>()).add(resource);
        }
    }

    private static int[] textureInfoOrClose(Texture2D texture) {
        int[] info = new int[4];
        try {
            check("cna_texture2d_get_info", nativeGetTexture2DInfo(resourceValue(texture), info));
            return info;
        } catch (RuntimeException failure) {
            try {
                closeGraphicsResource(texture);
            } catch (RuntimeException closeFailure) {
                failure.addSuppressed(closeFailure);
            }
            throw failure;
        }
    }

    private static long resourceValue(GraphicsResource resource) {
        NativeResourceHandle handle;
        synchronized (GAMES) {
            handle = RESOURCES.get(Objects.requireNonNull(resource, "resource"));
        }
        if (handle == null) {
            throw new IllegalStateException(
                    resource.getClass().getSimpleName() + " has no live CNA resource");
        }
        return handle.requireValue();
    }

    private static void destroyTexture2D(long handle) {
        check("cna_texture2d_destroy", nativeDestroyTexture2D(handle));
    }

    private static void destroySpriteBatch(long handle) {
        check("cna_sprite_batch_destroy", nativeDestroySpriteBatch(handle));
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

    private static native int nativeCreateTexture2D(
            long game, int width, int height, boolean mipMap, int format, long[] output);

    private static native int nativeCreateTexture2DFromEncoded(
            long game, byte[] encoded, int width, int height, boolean zoom, boolean resize,
            long[] output);

    private static native int nativeGetTexture2DInfo(long texture, int[] output);

    private static native int nativeSetTexture2DData(long texture, int[] packedColors);

    private static native int nativeGetTexture2DData(long texture, int[] packedColors);

    private static native long nativeGetTexture2DEncodedSize(
            long texture, int format, int width, int height);

    private static native int nativeCopyTexture2DEncoded(
            long texture, int format, int width, int height, byte[] output);

    private static native int nativeDestroyTexture2D(long texture);

    private static native int nativeCreateSpriteBatch(long game, long[] output);

    private static native int nativeBeginSpriteBatch(long spriteBatch, int sortMode);

    private static native int nativeDrawSpriteRectangle(
            long spriteBatch, long texture,
            int destinationX, int destinationY, int destinationWidth, int destinationHeight,
            int sourceX, int sourceY, int sourceWidth, int sourceHeight,
            int packedColor, float rotation, float originX, float originY,
            int effects, float layerDepth);

    private static native int nativeDrawSpriteScaled(
            long spriteBatch, long texture, float positionX, float positionY,
            int sourceX, int sourceY, int sourceWidth, int sourceHeight,
            int packedColor, float rotation, float originX, float originY,
            float scaleX, float scaleY, int effects, float layerDepth);

    private static native int nativeEndSpriteBatch(long spriteBatch);

    private static native int nativeDestroySpriteBatch(long spriteBatch);

    private static native int nativeDestroyGame(long game);

    private static native String nativeLastErrorMessage();
}
