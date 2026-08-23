package org.openeggbert.cna.internal;

import Microsoft.Xna.Framework.Game;
import Microsoft.Xna.Framework.Color;
import Microsoft.Xna.Framework.GraphicsDeviceManager;
import Microsoft.Xna.Framework.Rectangle;
import Microsoft.Xna.Framework.Vector2;
import Microsoft.Xna.Framework.WindowHandle;
import Microsoft.Xna.Framework.Content.ContentManager;
import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.GraphicsResource;
import Microsoft.Xna.Framework.Graphics.CubeMapFace;
import Microsoft.Xna.Framework.Graphics.Texture;
import Microsoft.Xna.Framework.Graphics.PresentationParameters;
import Microsoft.Xna.Framework.Graphics.RenderTarget2D;
import Microsoft.Xna.Framework.Graphics.RenderTargetBinding;
import Microsoft.Xna.Framework.Graphics.RenderTargetCube;
import Microsoft.Xna.Framework.Graphics.SpriteBatch;
import Microsoft.Xna.Framework.Graphics.SpriteEffects;
import Microsoft.Xna.Framework.Graphics.SpriteFont;
import Microsoft.Xna.Framework.Graphics.SpriteSortMode;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import Microsoft.Xna.Framework.Graphics.TextureCube;
import Microsoft.Xna.Framework.Graphics.IndexBuffer;
import Microsoft.Xna.Framework.Graphics.VertexBuffer;
import Microsoft.Xna.Framework.Graphics.VertexBufferBinding;

import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
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
    private static final Map<GraphicsDevice, List<VertexBuffer>> DEVICE_VERTEX_BINDINGS =
            new WeakHashMap<>();
    private static final Map<GraphicsDevice, IndexBuffer> DEVICE_INDEX_BINDINGS =
            new WeakHashMap<>();
    private static final Map<SpriteFont, NativeResourceHandle> SPRITE_FONTS =
            new WeakHashMap<>();
    private static final Map<SpriteFont, Game> SPRITE_FONT_OWNERS = new WeakHashMap<>();
    private static final Map<Game, List<SpriteFont>> GAME_SPRITE_FONTS = new WeakHashMap<>();
    private static final Map<SpriteFont, Texture2D> SPRITE_FONT_ATLASES = new WeakHashMap<>();
    private static final Map<ContentManager, NativeResourceHandle> CONTENT_MANAGERS =
            new WeakHashMap<>();
    private static final Map<ContentManager, Game> CONTENT_MANAGER_OWNERS = new WeakHashMap<>();
    private static final Map<Game, List<ContentManager>> GAME_CONTENT_MANAGERS =
            new WeakHashMap<>();
    private static final Map<WindowHandle, Long> WINDOW_HANDLES = new WeakHashMap<>();
    private static final Map<GraphicsDevice, Throwable> DEVICE_CALLBACK_FAILURES =
            new WeakHashMap<>();
    private static final ThreadLocal<GraphicsResource> GRAPHICS_RESOURCE_EVENT =
            new ThreadLocal<>();
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

    public static void getGamePadState(
            int playerIndex, int deadZone, int[] discrete, float[] analog) {
        check(deadZone < 0 ? "cna_gamepad_get_state" : "cna_gamepad_get_state_with_dead_zone",
                nativeGetGamePadState(
                        currentGameHandle("GamePad.GetState").requireValue(),
                        playerIndex, deadZone, discrete, analog));
    }

    public static int[] getGamePadCapabilities(int playerIndex) {
        int[] capabilities = new int[26];
        check("cna_gamepad_get_capabilities", nativeGetGamePadCapabilities(
                currentGameHandle("GamePad.GetCapabilities").requireValue(),
                playerIndex, capabilities));
        return capabilities;
    }

    public static boolean setGamePadVibration(
            int playerIndex, float leftMotor, float rightMotor) {
        return booleanResult("cna_gamepad_set_vibration", nativeSetGamePadVibration(
                currentGameHandle("GamePad.SetVibration").requireValue(),
                playerIndex, leftMotor, rightMotor));
    }

    public static NativeGraphicsDeviceManagerHandle createGraphicsDeviceManager(
            NativeGameHandle game,
            GraphicsDeviceManager manager) {
        long[] output = new long[1];
        check("cna_graphics_device_manager_create", nativeCreateGraphicsDeviceManager(
                game.requireValue(), Objects.requireNonNull(manager, "manager"), output));
        return new NativeGraphicsDeviceManagerHandle(output[0], manager);
    }

    public static int getGraphicsDeviceManagerGraphicsProfile(
            NativeGraphicsDeviceManagerHandle manager) {
        return Math.toIntExact(longResult("cna_graphics_device_manager_get_graphics_profile",
                nativeGetGraphicsDeviceManagerGraphicsProfile(manager.requireValue())));
    }

    public static void setGraphicsDeviceManagerGraphicsProfile(
            NativeGraphicsDeviceManagerHandle manager,
            int value) {
        check("cna_graphics_device_manager_set_graphics_profile",
                nativeSetGraphicsDeviceManagerGraphicsProfile(manager.requireValue(), value));
    }

    public static boolean getGraphicsDeviceManagerIsFullScreen(
            NativeGraphicsDeviceManagerHandle manager) {
        return booleanResult("cna_graphics_device_manager_get_is_full_screen",
                nativeGetGraphicsDeviceManagerIsFullScreen(manager.requireValue()));
    }

    public static void setGraphicsDeviceManagerIsFullScreen(
            NativeGraphicsDeviceManagerHandle manager,
            boolean value) {
        check("cna_graphics_device_manager_set_is_full_screen",
                nativeSetGraphicsDeviceManagerIsFullScreen(manager.requireValue(), value));
    }

    public static boolean getGraphicsDeviceManagerPreferMultiSampling(
            NativeGraphicsDeviceManagerHandle manager) {
        return booleanResult("cna_graphics_device_manager_get_prefer_multi_sampling",
                nativeGetGraphicsDeviceManagerPreferMultiSampling(manager.requireValue()));
    }

    public static void setGraphicsDeviceManagerPreferMultiSampling(
            NativeGraphicsDeviceManagerHandle manager,
            boolean value) {
        check("cna_graphics_device_manager_set_prefer_multi_sampling",
                nativeSetGraphicsDeviceManagerPreferMultiSampling(manager.requireValue(), value));
    }

    public static int getGraphicsDeviceManagerPreferredBackBufferFormat(
            NativeGraphicsDeviceManagerHandle manager) {
        return Math.toIntExact(longResult(
                "cna_graphics_device_manager_get_preferred_back_buffer_format",
                nativeGetGraphicsDeviceManagerPreferredBackBufferFormat(manager.requireValue())));
    }

    public static void setGraphicsDeviceManagerPreferredBackBufferFormat(
            NativeGraphicsDeviceManagerHandle manager,
            int value) {
        check("cna_graphics_device_manager_set_preferred_back_buffer_format",
                nativeSetGraphicsDeviceManagerPreferredBackBufferFormat(
                        manager.requireValue(), value));
    }

    public static int getGraphicsDeviceManagerPreferredBackBufferWidth(
            NativeGraphicsDeviceManagerHandle manager) {
        return Math.toIntExact(longResult(
                "cna_graphics_device_manager_get_preferred_back_buffer_width",
                nativeGetGraphicsDeviceManagerPreferredBackBufferWidth(manager.requireValue())));
    }

    public static void setGraphicsDeviceManagerPreferredBackBufferWidth(
            NativeGraphicsDeviceManagerHandle manager,
            int value) {
        check("cna_graphics_device_manager_set_preferred_back_buffer_width",
                nativeSetGraphicsDeviceManagerPreferredBackBufferWidth(
                        manager.requireValue(), value));
    }

    public static int getGraphicsDeviceManagerPreferredBackBufferHeight(
            NativeGraphicsDeviceManagerHandle manager) {
        return Math.toIntExact(longResult(
                "cna_graphics_device_manager_get_preferred_back_buffer_height",
                nativeGetGraphicsDeviceManagerPreferredBackBufferHeight(manager.requireValue())));
    }

    public static void setGraphicsDeviceManagerPreferredBackBufferHeight(
            NativeGraphicsDeviceManagerHandle manager,
            int value) {
        check("cna_graphics_device_manager_set_preferred_back_buffer_height",
                nativeSetGraphicsDeviceManagerPreferredBackBufferHeight(
                        manager.requireValue(), value));
    }

    public static int getGraphicsDeviceManagerPreferredDepthStencilFormat(
            NativeGraphicsDeviceManagerHandle manager) {
        return Math.toIntExact(longResult(
                "cna_graphics_device_manager_get_preferred_depth_stencil_format",
                nativeGetGraphicsDeviceManagerPreferredDepthStencilFormat(manager.requireValue())));
    }

    public static void setGraphicsDeviceManagerPreferredDepthStencilFormat(
            NativeGraphicsDeviceManagerHandle manager,
            int value) {
        check("cna_graphics_device_manager_set_preferred_depth_stencil_format",
                nativeSetGraphicsDeviceManagerPreferredDepthStencilFormat(
                        manager.requireValue(), value));
    }

    public static boolean getGraphicsDeviceManagerSynchronizeWithVerticalRetrace(
            NativeGraphicsDeviceManagerHandle manager) {
        return booleanResult(
                "cna_graphics_device_manager_get_synchronize_with_vertical_retrace",
                nativeGetGraphicsDeviceManagerSynchronizeWithVerticalRetrace(
                        manager.requireValue()));
    }

    public static void setGraphicsDeviceManagerSynchronizeWithVerticalRetrace(
            NativeGraphicsDeviceManagerHandle manager,
            boolean value) {
        check("cna_graphics_device_manager_set_synchronize_with_vertical_retrace",
                nativeSetGraphicsDeviceManagerSynchronizeWithVerticalRetrace(
                        manager.requireValue(), value));
    }

    public static int getGraphicsDeviceManagerSupportedOrientations(
            NativeGraphicsDeviceManagerHandle manager) {
        return Math.toIntExact(longResult(
                "cna_graphics_device_manager_get_supported_orientations",
                nativeGetGraphicsDeviceManagerSupportedOrientations(manager.requireValue())));
    }

    public static void setGraphicsDeviceManagerSupportedOrientations(
            NativeGraphicsDeviceManagerHandle manager,
            int value) {
        check("cna_graphics_device_manager_set_supported_orientations",
                nativeSetGraphicsDeviceManagerSupportedOrientations(
                        manager.requireValue(), value));
    }

    public static void applyGraphicsDeviceManagerChanges(
            NativeGraphicsDeviceManagerHandle manager) {
        check("cna_graphics_device_manager_apply_changes",
                nativeApplyGraphicsDeviceManagerChanges(manager.requireValue()));
    }

    public static void toggleGraphicsDeviceManagerFullScreen(
            NativeGraphicsDeviceManagerHandle manager) {
        check("cna_graphics_device_manager_toggle_full_screen",
                nativeToggleGraphicsDeviceManagerFullScreen(manager.requireValue()));
    }

    public static void createGraphicsDeviceManagerDevice(
            NativeGraphicsDeviceManagerHandle manager) {
        check("cna_graphics_device_manager_create_device",
                nativeCreateGraphicsDeviceManagerDevice(manager.requireValue()));
    }

    public static boolean beginGraphicsDeviceManagerDraw(
            NativeGraphicsDeviceManagerHandle manager) {
        return booleanResult("cna_graphics_device_manager_begin_draw",
                nativeBeginGraphicsDeviceManagerDraw(manager.requireValue()));
    }

    public static void endGraphicsDeviceManagerDraw(
            NativeGraphicsDeviceManagerHandle manager) {
        check("cna_graphics_device_manager_end_draw",
                nativeEndGraphicsDeviceManagerDraw(manager.requireValue()));
    }

    public static void disposeGraphicsDeviceManager(
            NativeGraphicsDeviceManagerHandle manager) {
        check("cna_graphics_device_manager_dispose",
                nativeDisposeGraphicsDeviceManager(manager.requireValue()));
    }

    public static int getGraphicsAdapterCount() {
        return Math.toIntExact(longResult("cna_graphics_adapter_get_count",
                nativeGetGraphicsAdapterCount(
                        currentGameHandle("GraphicsAdapter.Adapters").requireValue())));
    }

    public static long[] getGraphicsAdapterInfo(int adapterIndex) {
        long[] output = new long[10];
        check("cna_graphics_adapter_get_info", nativeGetGraphicsAdapterInfo(
                currentGameHandle("GraphicsAdapter").requireValue(), adapterIndex, output));
        return output;
    }

    public static String getGraphicsAdapterDescription(int adapterIndex, long byteLength) {
        return getGraphicsAdapterString(adapterIndex, byteLength, true);
    }

    public static String getGraphicsAdapterDeviceName(int adapterIndex, long byteLength) {
        return getGraphicsAdapterString(adapterIndex, byteLength, false);
    }

    public static int[] getGraphicsAdapterCurrentDisplayMode(int adapterIndex) {
        int[] output = new int[4];
        check("cna_graphics_adapter_get_current_display_mode",
                nativeGetGraphicsAdapterCurrentDisplayMode(
                        currentGameHandle("GraphicsAdapter.CurrentDisplayMode").requireValue(),
                        adapterIndex, output));
        return output;
    }

    public static int[][] getGraphicsAdapterDisplayModes(int adapterIndex) {
        int count = Math.toIntExact(longResult("cna_graphics_adapter_get_display_mode_count",
                nativeGetGraphicsAdapterDisplayModeCount(
                        currentGameHandle("GraphicsAdapter.SupportedDisplayModes").requireValue(),
                        adapterIndex)));
        int[] packed = new int[Math.multiplyExact(count, 4)];
        check("cna_graphics_adapter_copy_display_modes", nativeCopyGraphicsAdapterDisplayModes(
                currentGameHandle("GraphicsAdapter.SupportedDisplayModes").requireValue(),
                adapterIndex, packed));
        int[][] output = new int[count][4];
        for (int index = 0; index < count; index++) {
            System.arraycopy(packed, index * 4, output[index], 0, 4);
        }
        return output;
    }

    public static void setGraphicsAdapterDevicePreferences(
            int adapterIndex,
            boolean useNullDevice,
            boolean useReferenceDevice) {
        check("cna_graphics_adapter_set_device_preferences",
                nativeSetGraphicsAdapterDevicePreferences(
                        currentGameHandle("GraphicsAdapter device preferences").requireValue(),
                        adapterIndex, useNullDevice, useReferenceDevice));
    }

    public static boolean isGraphicsAdapterProfileSupported(int adapterIndex, int profile) {
        return booleanResult("cna_graphics_adapter_is_profile_supported",
                nativeIsGraphicsAdapterProfileSupported(
                        currentGameHandle("GraphicsAdapter.IsProfileSupported").requireValue(),
                        adapterIndex, profile));
    }

    public static int[] queryGraphicsAdapterFormat(
            int adapterIndex,
            boolean backBuffer,
            int profile,
            int format,
            int depthFormat,
            int multiSampleCount) {
        int[] output = new int[4];
        check(backBuffer
                        ? "cna_graphics_adapter_query_backbuffer_format"
                        : "cna_graphics_adapter_query_render_target_format",
                nativeQueryGraphicsAdapterFormat(
                        currentGameHandle("GraphicsAdapter format query").requireValue(),
                        adapterIndex, backBuffer, profile, format, depthFormat,
                        multiSampleCount, output));
        return output;
    }

    public static WindowHandle getGraphicsAdapterMonitorHandle(int adapterIndex) {
        long[] output = new long[1];
        check("cna_graphics_adapter_get_native_monitor_handle",
                nativeGetGraphicsAdapterMonitorHandle(
                        currentGameHandle("GraphicsAdapter.MonitorHandle").requireValue(),
                        adapterIndex, output));
        if (output[0] == 0L) {
            return WindowHandle.Zero;
        }
        synchronized (GAMES) {
            WindowHandle known = findWindowHandle(output[0]);
            if (known != null) {
                return known;
            }
        }
        throw new IllegalStateException("CNA returned an unrecognized opaque monitor token");
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

    /** Returns whether this facade currently has a live native game parent. */
    public static boolean isGraphicsDeviceNative(GraphicsDevice device) {
        Game game = deviceGame(device);
        synchronized (GAMES) {
            NativeGameHandle handle = GAMES.get(game);
            return handle != null && !handle.isClosed();
        }
    }

    /** Installs all native device callbacks while called from a valid game lifecycle callback. */
    public static void ensureGraphicsDeviceEvents(GraphicsDevice device) {
        Game game = deviceGame(device);
        check("cna_graphics_device_subscribe_*", nativeEnsureGraphicsDeviceEvents(
                gameHandle(game, "GraphicsDevice events").requireValue(), device));
    }

    public static boolean getGraphicsDeviceIsDisposed(GraphicsDevice device) {
        int result = nativeGetGraphicsDeviceIsDisposed(deviceGameValue(device));
        boolean value = booleanResult("cna_graphics_device_get_is_disposed", result);
        rethrowGraphicsDeviceListenerFailure(device);
        return value;
    }

    public static int getGraphicsDeviceStatus(GraphicsDevice device) {
        return deviceIntResult(
                device, "cna_graphics_device_get_status",
                nativeGetGraphicsDeviceStatus(deviceGameValue(device)));
    }

    public static int getGraphicsDeviceAdapterIndex(GraphicsDevice device) {
        return deviceIntResult(
                device, "cna_graphics_device_get_adapter_index",
                nativeGetGraphicsDeviceAdapterIndex(deviceGameValue(device)));
    }

    public static int getGraphicsDeviceProfile(GraphicsDevice device) {
        return deviceIntResult(
                device, "cna_graphics_device_get_graphics_profile",
                nativeGetGraphicsDeviceProfile(deviceGameValue(device)));
    }

    public static void setGraphicsDeviceProfile(GraphicsDevice device, int profile) {
        checkDevice(
                device, "cna_graphics_device_set_graphics_profile_ext",
                nativeSetGraphicsDeviceProfile(deviceGameValue(device), profile));
    }

    public static int[] getGraphicsDeviceScissorRectangle(GraphicsDevice device) {
        int[] output = new int[4];
        checkDevice(
                device, "cna_graphics_device_get_scissor_rectangle",
                nativeGetGraphicsDeviceScissorRectangle(deviceGameValue(device), output));
        return output;
    }

    public static void setGraphicsDeviceScissorRectangle(
            GraphicsDevice device, int x, int y, int width, int height) {
        checkDevice(
                device, "cna_graphics_device_set_scissor_rectangle",
                nativeSetGraphicsDeviceScissorRectangle(
                        deviceGameValue(device), x, y, width, height));
    }

    public static void getGraphicsDeviceViewport(
            GraphicsDevice device, int[] bounds, float[] depth) {
        checkDevice(
                device, "cna_graphics_device_get_viewport",
                nativeGetGraphicsDeviceViewport(
                        deviceGameValue(device),
                        Objects.requireNonNull(bounds, "bounds"),
                        Objects.requireNonNull(depth, "depth")));
    }

    public static void setGraphicsDeviceViewport(
            GraphicsDevice device,
            int x,
            int y,
            int width,
            int height,
            float minDepth,
            float maxDepth) {
        checkDevice(
                device, "cna_graphics_device_set_viewport",
                nativeSetGraphicsDeviceViewport(
                        deviceGameValue(device), x, y, width, height, minDepth, maxDepth));
    }

    public static int getGraphicsDeviceBlendFactor(GraphicsDevice device) {
        long value = deviceLongResult(
                device, "cna_graphics_device_get_blend_factor",
                nativeGetGraphicsDeviceBlendFactor(deviceGameValue(device)));
        return (int)value;
    }

    public static void setGraphicsDeviceBlendFactor(GraphicsDevice device, int packedColor) {
        checkDevice(
                device, "cna_graphics_device_set_blend_factor",
                nativeSetGraphicsDeviceBlendFactor(deviceGameValue(device), packedColor));
    }

    public static int[] getGraphicsDeviceBlendState(GraphicsDevice device) {
        int[] output = new int[12];
        checkDevice(
                device, "cna_graphics_device_get_blend_state",
                nativeGetGraphicsDeviceBlendState(deviceGameValue(device), output));
        return output;
    }

    public static void setGraphicsDeviceBlendState(GraphicsDevice device, int[] values) {
        int[] snapshot = requireLength(values, 12, "BlendState");
        checkDevice(
                device, "cna_graphics_device_set_blend_state",
                nativeSetGraphicsDeviceBlendState(deviceGameValue(device), snapshot));
    }

    public static int[] getGraphicsDeviceDepthStencilState(GraphicsDevice device) {
        int[] output = new int[16];
        checkDevice(
                device, "cna_graphics_device_get_depth_stencil_state",
                nativeGetGraphicsDeviceDepthStencilState(deviceGameValue(device), output));
        return output;
    }

    public static void setGraphicsDeviceDepthStencilState(
            GraphicsDevice device, int[] values) {
        int[] snapshot = requireLength(values, 16, "DepthStencilState");
        checkDevice(
                device, "cna_graphics_device_set_depth_stencil_state",
                nativeSetGraphicsDeviceDepthStencilState(deviceGameValue(device), snapshot));
    }

    public static void getGraphicsDeviceRasterizerState(
            GraphicsDevice device, int[] integers, float[] floats) {
        requireOutputLength(integers, 4, "RasterizerState integer");
        requireOutputLength(floats, 2, "RasterizerState float");
        checkDevice(
                device, "cna_graphics_device_get_rasterizer_state",
                nativeGetGraphicsDeviceRasterizerState(
                        deviceGameValue(device), integers, floats));
    }

    public static void setGraphicsDeviceRasterizerState(
            GraphicsDevice device, int[] integers, float[] floats) {
        int[] integerSnapshot = requireLength(integers, 4, "RasterizerState integer");
        float[] floatSnapshot = requireLength(floats, 2, "RasterizerState float");
        checkDevice(
                device, "cna_graphics_device_set_rasterizer_state",
                nativeSetGraphicsDeviceRasterizerState(
                        deviceGameValue(device), integerSnapshot, floatSnapshot));
    }

    public static void getGraphicsDeviceSamplerState(
            GraphicsDevice device,
            int shaderStage,
            int slot,
            int[] integers,
            float[] bias) {
        requireOutputLength(integers, 6, "SamplerState integer");
        requireOutputLength(bias, 1, "SamplerState bias");
        checkDevice(
                device, "cna_graphics_device_get_sampler_state",
                nativeGetGraphicsDeviceSamplerState(
                        deviceGameValue(device), shaderStage, slot, integers, bias));
    }

    public static void setGraphicsDeviceSamplerState(
            GraphicsDevice device,
            int shaderStage,
            int slot,
            int[] integers,
            float bias) {
        int[] snapshot = requireLength(integers, 6, "SamplerState integer");
        checkDevice(
                device, "cna_graphics_device_set_sampler_state",
                nativeSetGraphicsDeviceSamplerState(
                        deviceGameValue(device), shaderStage, slot, snapshot, bias));
    }

    public static Texture getGraphicsDeviceTexture(
            GraphicsDevice device,
            int shaderStage,
            int slot,
            Texture cached) {
        long[] output = new long[2];
        checkDevice(
                device, "cna_graphics_device_get_texture",
                nativeGetGraphicsDeviceTexture(
                        deviceGameValue(device), shaderStage, slot, output));
        if (output[0] == 0L) {
            return null;
        }
        if (output[0] != 1L) {
            throw new IllegalStateException(
                    "CNA returned invalid texture-slot bound value " + output[0]);
        }
        if (output[1] == 0L) {
            throw new UnsupportedOperationException(
                    "The texture slot is occupied by canonical CNA code without a C resource handle");
        }
        Texture result = findTexture(output[1], cached);
        if (result == null) {
            throw new IllegalStateException(
                    "CNA returned a texture handle not owned by this Java binding");
        }
        return result;
    }

    public static void setGraphicsDeviceTexture(
            GraphicsDevice device,
            int shaderStage,
            int slot,
            Texture texture) {
        long textureHandle = texture == null ? 0L : resourceValue(texture);
        checkDevice(
                device, "cna_graphics_device_set_texture",
                nativeSetGraphicsDeviceTexture(
                        deviceGameValue(device), shaderStage, slot, textureHandle));
    }

    public static int getGraphicsDeviceMultiSampleMask(GraphicsDevice device) {
        return deviceIntResult(
                device, "cna_graphics_device_get_multi_sample_mask",
                nativeGetGraphicsDeviceMultiSampleMask(deviceGameValue(device)));
    }

    public static void setGraphicsDeviceMultiSampleMask(GraphicsDevice device, int value) {
        checkDevice(
                device, "cna_graphics_device_set_multi_sample_mask",
                nativeSetGraphicsDeviceMultiSampleMask(deviceGameValue(device), value));
    }

    public static int getGraphicsDeviceReferenceStencil(GraphicsDevice device) {
        return deviceIntResult(
                device, "cna_graphics_device_get_reference_stencil",
                nativeGetGraphicsDeviceReferenceStencil(deviceGameValue(device)));
    }

    public static void setGraphicsDeviceReferenceStencil(GraphicsDevice device, int value) {
        checkDevice(
                device, "cna_graphics_device_set_reference_stencil",
                nativeSetGraphicsDeviceReferenceStencil(deviceGameValue(device), value));
    }

    public static int[] getGraphicsDevicePresentationParameters(GraphicsDevice device) {
        int[] output = new int[10];
        checkDevice(
                device, "cna_graphics_device_get_presentation_parameters",
                nativeGetGraphicsDevicePresentationParameters(deviceGameValue(device), output));
        return output;
    }

    public static int[] getGraphicsDeviceDisplayMode(GraphicsDevice device) {
        int[] output = new int[4];
        checkDevice(
                device, "cna_graphics_device_get_display_mode",
                nativeGetGraphicsDeviceDisplayMode(deviceGameValue(device), output));
        return output;
    }

    public static int[] getGraphicsDeviceBackBufferInfo(GraphicsDevice device) {
        int[] output = new int[3];
        checkDevice(
                device, "cna_graphics_device_get_backbuffer_info",
                nativeGetGraphicsDeviceBackBufferInfo(deviceGameValue(device), output));
        return output;
    }

    public static int[] getGraphicsDeviceBackBufferData(
            GraphicsDevice device,
            Rectangle rectangle,
            int capacity,
            int startIndex,
            int elementCount) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Backbuffer capacity must not be negative");
        }
        int[] output = new int[capacity];
        boolean hasRectangle = rectangle != null;
        int x = hasRectangle ? rectangle.X : 0;
        int y = hasRectangle ? rectangle.Y : 0;
        int width = hasRectangle ? rectangle.Width : 0;
        int height = hasRectangle ? rectangle.Height : 0;
        checkDevice(
                device, "cna_graphics_device_get_backbuffer_data_window",
                nativeGetGraphicsDeviceBackBufferData(
                        deviceGameValue(device), hasRectangle,
                        x, y, width, height,
                        startIndex, elementCount, output));
        return output;
    }

    public static void clearGraphicsDevice(
            GraphicsDevice device, int options, int packedColor, float depth, int stencil) {
        checkDevice(
                device, "cna_graphics_device_clear_options",
                nativeClearGraphicsDevice(
                        deviceGameValue(device), options, packedColor, depth, stencil));
    }

    public static void presentGraphicsDevice(GraphicsDevice device) {
        checkDevice(
                device, "cna_graphics_device_present",
                nativePresentGraphicsDevice(deviceGameValue(device)));
    }

    public static void resetGraphicsDevice(GraphicsDevice device) {
        checkDevice(
                device, "cna_graphics_device_reset",
                nativeResetGraphicsDevice(deviceGameValue(device)));
    }

    public static void resetGraphicsDevice(
            GraphicsDevice device,
            PresentationParameters parameters,
            int adapterIndex) {
        PresentationParameters snapshot =
                Objects.requireNonNull(parameters, "parameters").Clone();
        Game game = deviceGame(device);
        WindowHandle requestedWindow = snapshot.getDeviceWindowHandle();
        if (!requestedWindow.getIsZero()
                && !requestedWindow.equals(game.getWindow().getHandle())) {
            throw new UnsupportedOperationException(
                    "CNA's C ABI cannot reset a GraphicsDevice onto a different native window");
        }
        int[] values = {
                snapshot.getBackBufferFormat().ordinal(),
                snapshot.getBackBufferWidth(),
                snapshot.getBackBufferHeight(),
                snapshot.getDepthStencilFormat().ordinal(),
                snapshot.getMultiSampleCount(),
                snapshot.getPresentationInterval().ordinal(),
                snapshot.getDisplayOrientation().getValue(),
                snapshot.getRenderTargetUsage().ordinal(),
                snapshot.getIsFullScreen() ? 1 : 0
        };
        checkDevice(
                device, "cna_graphics_device_reset_with_parameters",
                nativeResetGraphicsDeviceWithParameters(
                        gameHandle(game, "GraphicsDevice.Reset").requireValue(),
                        values,
                        adapterIndex));
    }

    public static void setGraphicsDeviceVertexBuffer(
            GraphicsDevice device, VertexBuffer vertexBuffer, int vertexOffset) {
        long handle = 0L;
        if (vertexBuffer != null) {
            requireResourceOwner(device, vertexBuffer);
            handle = resourceValue(vertexBuffer);
        }
        int result = nativeSetGraphicsDeviceVertexBuffer(
                deviceGameValue(device), handle, vertexOffset);
        try {
            checkDevice(device,
                    vertexOffset == 0
                            ? "cna_graphics_device_set_vertex_buffer"
                            : "cna_graphics_device_set_vertex_buffer_offset",
                    result);
        } finally {
            if (result == 0) {
                recordVertexBindings(
                        device, vertexBuffer == null ? List.of() : List.of(vertexBuffer));
            }
        }
    }

    public static void setGraphicsDeviceVertexBuffers(
            GraphicsDevice device, VertexBufferBinding[] bindings) {
        Objects.requireNonNull(bindings, "bindings");
        long[] handles = new long[bindings.length];
        int[] offsets = new int[bindings.length];
        int[] frequencies = new int[bindings.length];
        for (int index = 0; index < bindings.length; index++) {
            VertexBufferBinding binding = Objects.requireNonNull(
                    bindings[index], "bindings[" + index + "]");
            VertexBuffer buffer = Objects.requireNonNull(
                    binding.getVertexBuffer(), "bindings[" + index + "].vertexBuffer");
            requireResourceOwner(device, buffer);
            handles[index] = resourceValue(buffer);
            offsets[index] = binding.getVertexOffset();
            frequencies[index] = binding.getInstanceFrequency();
        }
        int result = nativeSetGraphicsDeviceVertexBuffers(
                deviceGameValue(device), handles, offsets, frequencies);
        try {
            checkDevice(device, "cna_graphics_device_set_vertex_buffers", result);
        } finally {
            if (result == 0) {
                List<VertexBuffer> buffers = new ArrayList<>(bindings.length);
                for (VertexBufferBinding binding : bindings) {
                    buffers.add(binding.getVertexBuffer());
                }
                recordVertexBindings(device, buffers);
            }
        }
    }

    public static VertexBufferBinding[] getGraphicsDeviceVertexBuffers(GraphicsDevice device) {
        int count = Math.toIntExact(longResult(
                "cna_graphics_device_get_vertex_buffer_count",
                nativeGetGraphicsDeviceVertexBufferCount(deviceGameValue(device))));
        long[] handles = new long[count];
        int[] offsets = new int[count];
        int[] frequencies = new int[count];
        checkDevice(device, "cna_graphics_device_copy_vertex_buffers",
                nativeCopyGraphicsDeviceVertexBuffers(
                        deviceGameValue(device), handles, offsets, frequencies));
        VertexBufferBinding[] result = new VertexBufferBinding[count];
        for (int index = 0; index < count; index++) {
            if (handles[index] == 0L) {
                throw new UnsupportedOperationException(
                        "A vertex buffer was bound by canonical CNA code without a C resource handle");
            }
            VertexBuffer buffer = findVertexBuffer(handles[index]);
            if (buffer == null) {
                throw new IllegalStateException(
                        "CNA returned a vertex-buffer handle not owned by CNA-Java");
            }
            result[index] = new VertexBufferBinding(
                    buffer, offsets[index], frequencies[index]);
        }
        return result;
    }

    public static void setGraphicsDeviceIndexBuffer(
            GraphicsDevice device, IndexBuffer indexBuffer) {
        long handle = 0L;
        if (indexBuffer != null) {
            requireResourceOwner(device, indexBuffer);
            handle = resourceValue(indexBuffer);
        }
        int result = nativeSetGraphicsDeviceIndexBuffer(deviceGameValue(device), handle);
        try {
            checkDevice(device, "cna_graphics_device_set_index_buffer", result);
        } finally {
            if (result == 0) {
                synchronized (GAMES) {
                    if (indexBuffer == null) {
                        DEVICE_INDEX_BINDINGS.remove(device);
                    } else {
                        DEVICE_INDEX_BINDINGS.put(device, indexBuffer);
                    }
                }
            }
        }
    }

    public static IndexBuffer getGraphicsDeviceIndexBuffer(GraphicsDevice device) {
        long[] output = new long[1];
        checkDevice(device, "cna_graphics_device_get_index_buffer",
                nativeGetGraphicsDeviceIndexBuffer(deviceGameValue(device), output));
        if (output[0] == 0L) {
            return null;
        }
        IndexBuffer result = findIndexBuffer(output[0]);
        if (result == null) {
            throw new UnsupportedOperationException(
                    "The index buffer was bound by canonical CNA code without a C resource handle");
        }
        return result;
    }

    public static void drawPrimitives(
            GraphicsDevice device, int primitiveType, int startVertex, int primitiveCount) {
        checkDevice(device, "cna_graphics_device_draw_primitives",
                nativeDrawPrimitives(
                        deviceGameValue(device), primitiveType, startVertex, primitiveCount));
    }

    public static void drawIndexedPrimitives(
            GraphicsDevice device,
            int primitiveType,
            int baseVertex,
            int minVertexIndex,
            int numVertices,
            int startIndex,
            int primitiveCount) {
        checkDevice(device, "cna_graphics_device_draw_indexed_primitives",
                nativeDrawIndexedPrimitives(
                        deviceGameValue(device), primitiveType, baseVertex,
                        minVertexIndex, numVertices, startIndex, primitiveCount));
    }

    public static void drawInstancedPrimitives(
            GraphicsDevice device,
            int primitiveType,
            int baseVertex,
            int minVertexIndex,
            int numVertices,
            int startIndex,
            int primitiveCount,
            int instanceCount) {
        checkDevice(device, "cna_graphics_device_draw_instanced_primitives",
                nativeDrawInstancedPrimitives(
                        deviceGameValue(device), primitiveType, baseVertex,
                        minVertexIndex, numVertices, startIndex,
                        primitiveCount, instanceCount));
    }

    public static void drawUserPrimitives(
            GraphicsDevice device,
            int primitiveType,
            int vertexSource,
            byte[] vertexData,
            int vertexStride,
            int vertexOffset,
            int numVertices,
            int primitiveCount,
            int[] declaration) {
        checkDevice(device, "cna_graphics_device_draw_user_primitives",
                nativeDrawUserPrimitives(
                        deviceGameValue(device), primitiveType, vertexSource,
                        Objects.requireNonNull(vertexData, "vertexData"), vertexStride,
                        vertexOffset, numVertices, primitiveCount,
                        Objects.requireNonNull(declaration, "declaration")));
    }

    public static void drawUserIndexedPrimitives(
            GraphicsDevice device,
            int primitiveType,
            int vertexSource,
            byte[] vertexData,
            int vertexStride,
            int vertexOffset,
            int numVertices,
            short[] indexData,
            int indexOffset,
            int primitiveCount,
            int[] declaration) {
        checkDevice(device, "cna_graphics_device_draw_user_indexed_primitives",
                nativeDrawUserIndexedPrimitives16(
                        deviceGameValue(device), primitiveType, vertexSource,
                        Objects.requireNonNull(vertexData, "vertexData"), vertexStride,
                        vertexOffset, numVertices,
                        Objects.requireNonNull(indexData, "indexData"), indexOffset,
                        primitiveCount, Objects.requireNonNull(declaration, "declaration")));
    }

    public static void drawUserIndexedPrimitives(
            GraphicsDevice device,
            int primitiveType,
            int vertexSource,
            byte[] vertexData,
            int vertexStride,
            int vertexOffset,
            int numVertices,
            int[] indexData,
            int indexOffset,
            int primitiveCount,
            int[] declaration) {
        checkDevice(device, "cna_graphics_device_draw_user_indexed_primitives",
                nativeDrawUserIndexedPrimitives32(
                        deviceGameValue(device), primitiveType, vertexSource,
                        Objects.requireNonNull(vertexData, "vertexData"), vertexStride,
                        vertexOffset, numVertices,
                        Objects.requireNonNull(indexData, "indexData"), indexOffset,
                        primitiveCount, Objects.requireNonNull(declaration, "declaration")));
    }

    /** Returns the Java resource currently crossing a synchronous native create/destroy event. */
    public static GraphicsResource currentGraphicsResourceEvent() {
        return GRAPHICS_RESOURCE_EVENT.get();
    }

    /** Records a Java listener failure without allowing it to unwind through native C/C++. */
    public static void recordGraphicsDeviceListenerFailure(
            GraphicsDevice device, Throwable failure) {
        synchronized (GAMES) {
            Throwable previous = DEVICE_CALLBACK_FAILURES.get(device);
            if (previous == null) {
                DEVICE_CALLBACK_FAILURES.put(device, failure);
            } else {
                previous.addSuppressed(failure);
            }
        }
    }

    /** Rethrows and clears a listener failure at the next Java/native call boundary. */
    public static void rethrowGraphicsDeviceListenerFailure(GraphicsDevice device) {
        Throwable failure;
        synchronized (GAMES) {
            failure = DEVICE_CALLBACK_FAILURES.remove(device);
        }
        if (failure instanceof RuntimeException runtime) {
            throw runtime;
        }
        if (failure instanceof Error error) {
            throw error;
        }
        if (failure != null) {
            throw new IllegalStateException("GraphicsDevice listener failed", failure);
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
        GraphicsResource previous = GRAPHICS_RESOURCE_EVENT.get();
        GRAPHICS_RESOURCE_EVENT.set(texture);
        try {
            check("cna_texture2d_create", nativeCreateTexture2D(
                    gameHandle(game, "Texture2D").requireValue(),
                    width, height, mipMap, format, output));
        } finally {
            restoreGraphicsResourceEvent(previous);
        }
        registerResource(game, texture, output[0], NativeBindings::destroyTexture2D);
        int[] info = textureInfoOrClose(texture);
        rethrowGraphicsDeviceListenerFailure(graphicsDevice);
        return info;
    }

    public static Texture2D loadContentTexture2D(
            ContentManager manager,
            GraphicsDevice graphicsDevice,
            String rootDirectory,
            String assetName) {
        Game game = deviceGame(graphicsDevice);
        long managerValue = contentManagerValue(
                manager, graphicsDevice, Objects.requireNonNull(rootDirectory, "rootDirectory"));
        Texture2D texture = FacadeFactory.createUninitializedTexture2D(graphicsDevice);
        long[] output = new long[1];
        GraphicsResource previous = GRAPHICS_RESOURCE_EVENT.get();
        GRAPHICS_RESOURCE_EVENT.set(texture);
        try {
            check("cna_content_manager_load_texture2d", nativeLoadContentTexture2D(
                    managerValue,
                    Objects.requireNonNull(assetName, "assetName")
                            .getBytes(StandardCharsets.UTF_8),
                    output));
        } finally {
            restoreGraphicsResourceEvent(previous);
        }
        registerResource(game, texture, output[0], NativeBindings::destroyTexture2D);
        try {
            FacadeFactory.initializeTexture2D(texture, textureInfoOrClose(texture));
            rethrowGraphicsDeviceListenerFailure(graphicsDevice);
            return texture;
        } catch (RuntimeException failure) {
            try {
                texture.close();
            } catch (RuntimeException closeFailure) {
                failure.addSuppressed(closeFailure);
            }
            throw failure;
        }
    }

    public static SpriteFont loadContentSpriteFont(
            ContentManager manager,
            GraphicsDevice graphicsDevice,
            String rootDirectory,
            String assetName) {
        Game game = deviceGame(graphicsDevice);
        long managerValue = contentManagerValue(
                manager, graphicsDevice, Objects.requireNonNull(rootDirectory, "rootDirectory"));
        SpriteFont font = FacadeFactory.createSpriteFont();
        Texture2D atlas = FacadeFactory.createUninitializedTexture2D(graphicsDevice);
        long[] output = new long[2];
        GraphicsResource previous = GRAPHICS_RESOURCE_EVENT.get();
        GRAPHICS_RESOURCE_EVENT.set(atlas);
        try {
            check("cna_content_manager_load_sprite_font", nativeLoadContentSpriteFont(
                    managerValue,
                    Objects.requireNonNull(assetName, "assetName")
                            .getBytes(StandardCharsets.UTF_8),
                    output));
        } finally {
            restoreGraphicsResourceEvent(previous);
        }
        registerResource(game, atlas, output[1], NativeBindings::destroyTexture2D);
        registerSpriteFont(game, font, output[0], atlas);
        try {
            FacadeFactory.initializeTexture2D(atlas, textureInfoOrClose(atlas));
            spriteFontInfo(font);
            rethrowGraphicsDeviceListenerFailure(graphicsDevice);
            return font;
        } catch (RuntimeException failure) {
            try {
                closeSpriteFont(font);
            } catch (RuntimeException closeFailure) {
                failure.addSuppressed(closeFailure);
            }
            throw failure;
        }
    }

    public static List<Character> getSpriteFontCharacters(SpriteFont font) {
        int count = spriteFontInfo(font).integers()[0];
        char[] characters = new char[count];
        check("cna_sprite_font_copy_characters",
                nativeCopySpriteFontCharacters(spriteFontValue(font), characters));
        List<Character> result = new ArrayList<>(count);
        for (char character : characters) {
            result.add(character);
        }
        return List.copyOf(result);
    }

    public static Character getSpriteFontDefaultCharacter(SpriteFont font) {
        int[] info = spriteFontInfo(font).integers();
        return info[3] == 0 ? null : (char)info[2];
    }

    public static void setSpriteFontDefaultCharacter(SpriteFont font, Character value) {
        check("cna_sprite_font_set_default_character",
                nativeSetSpriteFontDefaultCharacter(
                        spriteFontValue(font), value != null,
                        value == null ? 0 : value));
    }

    public static int getSpriteFontLineSpacing(SpriteFont font) {
        return spriteFontInfo(font).integers()[1];
    }

    public static void setSpriteFontLineSpacing(SpriteFont font, int value) {
        check("cna_sprite_font_set_line_spacing",
                nativeSetSpriteFontLineSpacing(spriteFontValue(font), value));
    }

    public static float getSpriteFontSpacing(SpriteFont font) {
        return spriteFontInfo(font).spacing();
    }

    public static void setSpriteFontSpacing(SpriteFont font, float value) {
        check("cna_sprite_font_set_spacing",
                nativeSetSpriteFontSpacing(spriteFontValue(font), value));
    }

    public static Vector2 measureSpriteFont(SpriteFont font, String text) {
        float[] output = new float[2];
        check("cna_sprite_font_measure_utf8", nativeMeasureSpriteFont(
                spriteFontValue(font),
                Objects.requireNonNull(text, "text").getBytes(StandardCharsets.UTF_8),
                output));
        return new Vector2(output[0], output[1]);
    }

    public static int[] createVertexBuffer(
            VertexBuffer buffer,
            GraphicsDevice graphicsDevice,
            int vertexStride,
            int[] declaration,
            int vertexCount,
            int usage) {
        Game game = deviceGame(graphicsDevice);
        long[] output = new long[1];
        GraphicsResource previous = GRAPHICS_RESOURCE_EVENT.get();
        GRAPHICS_RESOURCE_EVENT.set(buffer);
        try {
            check("cna_vertex_buffer_create", nativeCreateVertexBuffer(
                    gameHandle(game, "VertexBuffer").requireValue(),
                    vertexStride, Objects.requireNonNull(declaration, "declaration"),
                    vertexCount, usage, output));
        } finally {
            restoreGraphicsResourceEvent(previous);
        }
        registerResource(game, buffer, output[0], NativeBindings::destroyVertexBuffer);
        int[] info = vertexBufferInfoOrClose(buffer);
        rethrowGraphicsDeviceListenerFailure(graphicsDevice);
        return info;
    }

    public static void setVertexBufferData(
            VertexBuffer buffer,
            int offsetInBytes,
            byte[] payload,
            int vertexCount,
            int vertexStride) {
        check(offsetInBytes < 0
                        ? "cna_vertex_buffer_set_data_raw"
                        : "cna_vertex_buffer_set_data_raw_at",
                nativeSetVertexBufferData(
                        resourceValue(buffer), offsetInBytes,
                        Objects.requireNonNull(payload, "payload"),
                        vertexCount, vertexStride));
    }

    public static byte[] getVertexBufferData(
            VertexBuffer buffer,
            int offsetInBytes,
            int vertexCount,
            int vertexStride) {
        byte[] output = new byte[Math.multiplyExact(vertexCount, vertexStride)];
        check("cna_vertex_buffer_get_data_raw",
                nativeGetVertexBufferData(
                        resourceValue(buffer), offsetInBytes,
                        vertexCount, vertexStride, output));
        return output;
    }

    public static int[] createIndexBuffer(
            IndexBuffer buffer,
            GraphicsDevice graphicsDevice,
            int indexElementSize,
            int indexCount,
            int usage) {
        Game game = deviceGame(graphicsDevice);
        long[] output = new long[1];
        GraphicsResource previous = GRAPHICS_RESOURCE_EVENT.get();
        GRAPHICS_RESOURCE_EVENT.set(buffer);
        try {
            check("cna_index_buffer_create", nativeCreateIndexBuffer(
                    gameHandle(game, "IndexBuffer").requireValue(),
                    indexElementSize, indexCount, usage, output));
        } finally {
            restoreGraphicsResourceEvent(previous);
        }
        registerResource(game, buffer, output[0], NativeBindings::destroyIndexBuffer);
        int[] info = indexBufferInfoOrClose(buffer);
        rethrowGraphicsDeviceListenerFailure(graphicsDevice);
        return info;
    }

    public static void setIndexBufferData(
            IndexBuffer buffer,
            int offsetInBytes,
            int indexElementSize,
            int[] values) {
        check(offsetInBytes < 0
                        ? "cna_index_buffer_set_data"
                        : "cna_index_buffer_set_data_at",
                nativeSetIndexBufferData(
                        resourceValue(buffer), offsetInBytes, indexElementSize,
                        Objects.requireNonNull(values, "values")));
    }

    public static int[] getIndexBufferData(
            IndexBuffer buffer, int indexElementSize, int elementCount) {
        int[] output = new int[elementCount];
        check("cna_index_buffer_get_data",
                nativeGetIndexBufferData(
                        resourceValue(buffer), indexElementSize, output));
        return output;
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
        GraphicsResource previous = GRAPHICS_RESOURCE_EVENT.get();
        GRAPHICS_RESOURCE_EVENT.set(texture);
        try {
            check("cna_texture2d_create_from_encoded_memory", nativeCreateTexture2DFromEncoded(
                    gameHandle(game, "Texture2D.FromStream").requireValue(),
                    Objects.requireNonNull(encoded, "encoded"),
                    width, height, zoom, resize, output));
        } finally {
            restoreGraphicsResourceEvent(previous);
        }
        registerResource(game, texture, output[0], NativeBindings::destroyTexture2D);
        int[] info = textureInfoOrClose(texture);
        rethrowGraphicsDeviceListenerFailure(graphicsDevice);
        return info;
    }

    public static void setTexture2DData(Texture2D texture, Color[] data) {
        Objects.requireNonNull(data, "data");
        int[] packed = new int[data.length];
        for (int index = 0; index < data.length; index++) {
            packed[index] = Objects.requireNonNull(data[index], "data[" + index + "]")
                    .getPackedValue().intValue();
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

    public static void setTexture2DData(
            Texture2D texture,
            int dataType,
            int level,
            Rectangle rectangle,
            int startIndex,
            int elementCount,
            byte[] payload) {
        Objects.requireNonNull(payload, "payload");
        boolean hasRectangle = rectangle != null;
        int x = hasRectangle ? rectangle.X : 0;
        int y = hasRectangle ? rectangle.Y : 0;
        int width = hasRectangle ? rectangle.Width : 0;
        int height = hasRectangle ? rectangle.Height : 0;
        check("cna_texture2d_set_data", nativeSetTexture2DTypedData(
                resourceValue(texture), dataType, level,
                hasRectangle, x, y, width, height,
                startIndex, elementCount, payload));
    }

    public static byte[] getTexture2DData(
            Texture2D texture,
            int dataType,
            int level,
            Rectangle rectangle,
            int startIndex,
            int elementCount,
            int payloadBytes) {
        if (payloadBytes < 0) {
            throw new IllegalArgumentException("Texture payload capacity must not be negative");
        }
        byte[] payload = new byte[payloadBytes];
        boolean hasRectangle = rectangle != null;
        int x = hasRectangle ? rectangle.X : 0;
        int y = hasRectangle ? rectangle.Y : 0;
        int width = hasRectangle ? rectangle.Width : 0;
        int height = hasRectangle ? rectangle.Height : 0;
        check("cna_texture2d_get_data", nativeGetTexture2DTypedData(
                resourceValue(texture), dataType, level,
                hasRectangle, x, y, width, height,
                startIndex, elementCount, payload));
        return payload;
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

    public static int[] createTextureCube(
            TextureCube texture,
            GraphicsDevice graphicsDevice,
            int size,
            boolean mipMap,
            int format) {
        Game game = deviceGame(graphicsDevice);
        long[] output = new long[1];
        GraphicsResource previous = GRAPHICS_RESOURCE_EVENT.get();
        GRAPHICS_RESOURCE_EVENT.set(texture);
        try {
            check("cna_texturecube_create", nativeCreateTextureCube(
                    gameHandle(game, "TextureCube").requireValue(),
                    size, mipMap, format, output));
        } finally {
            restoreGraphicsResourceEvent(previous);
        }
        registerResource(game, texture, output[0], NativeBindings::destroyTextureCube);
        int[] info = textureCubeInfoOrClose(texture);
        rethrowGraphicsDeviceListenerFailure(graphicsDevice);
        return info;
    }

    public static void setTextureCubeData(
            TextureCube texture,
            int face,
            int level,
            Rectangle rectangle,
            Color[] data,
            int startIndex,
            int elementCount) {
        Objects.requireNonNull(data, "data");
        int[] packed = new int[data.length];
        for (int index = startIndex; index < startIndex + elementCount; index++) {
            packed[index] = Objects.requireNonNull(data[index], "data[" + index + "]")
                    .getPackedValue().intValue();
        }
        boolean hasRectangle = rectangle != null;
        check("cna_texturecube_set_data", nativeSetTextureCubeData(
                resourceValue(texture), face, level, hasRectangle,
                hasRectangle ? rectangle.X : 0,
                hasRectangle ? rectangle.Y : 0,
                hasRectangle ? rectangle.Width : 0,
                hasRectangle ? rectangle.Height : 0,
                startIndex, elementCount, packed));
    }

    public static Color[] getTextureCubeData(
            TextureCube texture,
            int face,
            int level,
            Rectangle rectangle,
            int capacity,
            int startIndex,
            int elementCount) {
        int[] packed = new int[capacity];
        boolean hasRectangle = rectangle != null;
        check("cna_texturecube_get_data", nativeGetTextureCubeData(
                resourceValue(texture), face, level, hasRectangle,
                hasRectangle ? rectangle.X : 0,
                hasRectangle ? rectangle.Y : 0,
                hasRectangle ? rectangle.Width : 0,
                hasRectangle ? rectangle.Height : 0,
                startIndex, elementCount, packed));
        Color[] result = new Color[capacity];
        for (int index = startIndex; index < startIndex + elementCount; index++) {
            Color color = new Color();
            color.setPackedValue(Integer.toUnsignedLong(packed[index]));
            result[index] = color;
        }
        return result;
    }

    public static int[] createRenderTarget2D(
            RenderTarget2D renderTarget,
            GraphicsDevice graphicsDevice,
            int width,
            int height,
            boolean mipMap,
            int format,
            int depthFormat,
            int multiSampleCount,
            int usage) {
        Game game = deviceGame(graphicsDevice);
        long[] output = new long[1];
        GraphicsResource previous = GRAPHICS_RESOURCE_EVENT.get();
        GRAPHICS_RESOURCE_EVENT.set(renderTarget);
        try {
            check("cna_render_target2d_create", nativeCreateRenderTarget2D(
                    gameHandle(game, "RenderTarget2D").requireValue(),
                    width, height, mipMap, format, depthFormat,
                    multiSampleCount, usage, output));
        } finally {
            restoreGraphicsResourceEvent(previous);
        }
        registerResource(game, renderTarget, output[0], NativeBindings::destroyRenderTarget);
        int[] info = renderTargetInfoOrClose(renderTarget);
        rethrowGraphicsDeviceListenerFailure(graphicsDevice);
        return info;
    }

    public static int[] createRenderTargetCube(
            RenderTargetCube renderTarget,
            GraphicsDevice graphicsDevice,
            int size,
            boolean mipMap,
            int format,
            int depthFormat,
            int multiSampleCount,
            int usage) {
        Game game = deviceGame(graphicsDevice);
        long[] output = new long[1];
        GraphicsResource previous = GRAPHICS_RESOURCE_EVENT.get();
        GRAPHICS_RESOURCE_EVENT.set(renderTarget);
        try {
            check("cna_render_target_cube_create", nativeCreateRenderTargetCube(
                    gameHandle(game, "RenderTargetCube").requireValue(),
                    size, mipMap, format, depthFormat,
                    multiSampleCount, usage, output));
        } finally {
            restoreGraphicsResourceEvent(previous);
        }
        registerResource(game, renderTarget, output[0], NativeBindings::destroyRenderTarget);
        int[] info = renderTargetInfoOrClose(renderTarget);
        rethrowGraphicsDeviceListenerFailure(graphicsDevice);
        return info;
    }

    public static boolean getRenderTargetIsContentLost(GraphicsResource renderTarget) {
        return renderTargetInfo(renderTarget)[7] != 0;
    }

    public static void setGraphicsDeviceRenderTarget2D(
            GraphicsDevice device, RenderTarget2D renderTarget) {
        checkDevice(device, "cna_graphics_device_set_render_target2d",
                nativeSetGraphicsDeviceRenderTarget2D(
                        deviceGameValue(device),
                        renderTarget == null ? 0L : resourceValue(renderTarget)));
    }

    public static void setGraphicsDeviceRenderTargetCube(
            GraphicsDevice device,
            RenderTargetCube renderTarget,
            CubeMapFace face) {
        checkDevice(device, "cna_graphics_device_set_render_target_cube",
                nativeSetGraphicsDeviceRenderTargetCube(
                        deviceGameValue(device),
                        renderTarget == null ? 0L : resourceValue(renderTarget),
                        Objects.requireNonNull(face, "face").ordinal()));
    }

    public static void setGraphicsDeviceRenderTargets(
            GraphicsDevice device, RenderTargetBinding[] bindings) {
        Objects.requireNonNull(bindings, "bindings");
        long[] handles = new long[bindings.length];
        int[] faces = new int[bindings.length];
        for (int index = 0; index < bindings.length; index++) {
            RenderTargetBinding binding = Objects.requireNonNull(
                    bindings[index], "bindings[" + index + "]");
            Texture target = binding.getRenderTarget();
            if (!(target instanceof RenderTarget2D)
                    && !(target instanceof RenderTargetCube)) {
                throw new IllegalArgumentException(
                        "A render-target binding must contain RenderTarget2D or RenderTargetCube");
            }
            if (target.getGraphicsDevice() != device) {
                throw new IllegalArgumentException(
                        "Every render target must belong to this GraphicsDevice");
            }
            handles[index] = resourceValue(target);
            faces[index] = binding.getCubeMapFace().ordinal();
        }
        checkDevice(device, "cna_graphics_device_set_render_targets",
                nativeSetGraphicsDeviceRenderTargets(
                        deviceGameValue(device), handles, faces));
    }

    public static RenderTargetBinding[] getGraphicsDeviceRenderTargets(GraphicsDevice device) {
        int count = Math.toIntExact(longResult(
                "cna_graphics_device_get_render_target_count",
                nativeGetGraphicsDeviceRenderTargetCount(deviceGameValue(device))));
        long[] handles = new long[count];
        int[] faces = new int[count];
        checkDevice(device, "cna_graphics_device_copy_render_targets",
                nativeCopyGraphicsDeviceRenderTargets(
                        deviceGameValue(device), handles, faces));
        RenderTargetBinding[] result = new RenderTargetBinding[count];
        for (int index = 0; index < count; index++) {
            Texture target = findTexture(handles[index], null);
            if (target instanceof RenderTarget2D renderTarget2D) {
                result[index] = new RenderTargetBinding(renderTarget2D);
            } else if (target instanceof RenderTargetCube renderTargetCube) {
                if (faces[index] < 0 || faces[index] >= CubeMapFace.values().length) {
                    throw new IllegalStateException(
                            "CNA returned an unknown CubeMapFace " + faces[index]);
                }
                result[index] = new RenderTargetBinding(
                        renderTargetCube,
                        CubeMapFace.values()[faces[index]]);
            } else {
                throw new IllegalStateException(
                        "CNA returned a render target not owned by this Java binding");
            }
        }
        return result;
    }

    public static void createSpriteBatch(SpriteBatch spriteBatch, GraphicsDevice graphicsDevice) {
        Game game = deviceGame(graphicsDevice);
        long[] output = new long[1];
        GraphicsResource previous = GRAPHICS_RESOURCE_EVENT.get();
        GRAPHICS_RESOURCE_EVENT.set(spriteBatch);
        try {
            check("cna_sprite_batch_create", nativeCreateSpriteBatch(
                    gameHandle(game, "SpriteBatch").requireValue(), output));
        } finally {
            restoreGraphicsResourceEvent(previous);
        }
        registerResource(game, spriteBatch, output[0], NativeBindings::destroySpriteBatch);
        rethrowGraphicsDeviceListenerFailure(graphicsDevice);
    }

    public static void beginSpriteBatch(SpriteBatch spriteBatch, SpriteSortMode sortMode) {
        check("cna_sprite_batch_begin", nativeBeginSpriteBatch(
                resourceValue(spriteBatch), Objects.requireNonNull(sortMode, "sortMode").ordinal()));
    }

    public static void beginSpriteBatchWithStates(
            SpriteBatch spriteBatch,
            SpriteSortMode sortMode,
            int[] blend,
            int[] sampler,
            float samplerBias,
            int[] depthStencil,
            int[] rasterizer,
            float[] rasterizerFloats) {
        check("cna_sprite_batch_begin_with_states", nativeBeginSpriteBatchWithStates(
                resourceValue(spriteBatch),
                Objects.requireNonNull(sortMode, "sortMode").ordinal(),
                requireLength(blend, 12, "BlendState"),
                requireLength(sampler, 6, "SamplerState integer"),
                samplerBias,
                requireLength(depthStencil, 16, "DepthStencilState"),
                requireLength(rasterizer, 4, "RasterizerState integer"),
                requireLength(rasterizerFloats, 2, "RasterizerState float")));
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
                colorValue.getPackedValue().intValue(), rotation, originValue.X, originValue.Y,
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
                colorValue.getPackedValue().intValue(), rotation, originValue.X, originValue.Y,
                scaleValue.X, scaleValue.Y, Objects.requireNonNull(effects, "effects").getValue(),
                layerDepth));
    }

    public static void drawSpriteString(
            SpriteBatch spriteBatch,
            SpriteFont spriteFont,
            String text,
            Vector2 position,
            Color color,
            float rotation,
            Vector2 origin,
            Vector2 scale,
            SpriteEffects effects,
            float layerDepth) {
        Vector2 positionValue = new Vector2(Objects.requireNonNull(position, "position"));
        Color colorValue = new Color(Objects.requireNonNull(color, "color"));
        Vector2 originValue = new Vector2(Objects.requireNonNull(origin, "origin"));
        Vector2 scaleValue = new Vector2(Objects.requireNonNull(scale, "scale"));
        check("cna_sprite_batch_draw_string", nativeDrawSpriteString(
                resourceValue(spriteBatch),
                spriteFontValue(Objects.requireNonNull(spriteFont, "spriteFont")),
                Objects.requireNonNull(text, "text").getBytes(StandardCharsets.UTF_8),
                positionValue.X,
                positionValue.Y,
                colorValue.getPackedValue().intValue(),
                rotation,
                originValue.X,
                originValue.Y,
                scaleValue.X,
                scaleValue.Y,
                Objects.requireNonNull(effects, "effects").getValue(),
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
        unbindBufferBeforeRelease(resource);
        GraphicsResource previous = GRAPHICS_RESOURCE_EVENT.get();
        GRAPHICS_RESOURCE_EVENT.set(resource);
        try {
            handle.close();
        } finally {
            restoreGraphicsResourceEvent(previous);
        }
        synchronized (GAMES) {
            RESOURCES.remove(resource);
            Game owner = RESOURCE_OWNERS.remove(resource);
            List<GraphicsResource> children = GAME_RESOURCES.get(owner);
            if (children != null) {
                children.remove(resource);
            }
        }
    }

    private static void unbindBufferBeforeRelease(GraphicsResource resource) {
        GraphicsDevice device = resource.getGraphicsDevice();
        if (resource instanceof VertexBuffer) {
            boolean bound;
            synchronized (GAMES) {
                List<VertexBuffer> bindings = DEVICE_VERTEX_BINDINGS.get(device);
                bound = bindings != null && bindings.stream()
                        .anyMatch(buffer -> buffer == resource);
            }
            if (bound) {
                int result = nativeSetGraphicsDeviceVertexBuffers(
                        deviceGameValue(device), new long[0], new int[0], new int[0]);
                try {
                    checkDevice(device, "cna_graphics_device_set_vertex_buffers", result);
                } finally {
                    if (result == 0) {
                        recordVertexBindings(device, List.of());
                    }
                }
            }
        } else if (resource instanceof IndexBuffer) {
            boolean bound;
            synchronized (GAMES) {
                bound = DEVICE_INDEX_BINDINGS.get(device) == resource;
            }
            if (bound) {
                int result = nativeSetGraphicsDeviceIndexBuffer(deviceGameValue(device), 0L);
                try {
                    checkDevice(device, "cna_graphics_device_set_index_buffer", result);
                } finally {
                    if (result == 0) {
                        synchronized (GAMES) {
                            if (DEVICE_INDEX_BINDINGS.get(device) == resource) {
                                DEVICE_INDEX_BINDINGS.remove(device);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void recordVertexBindings(
            GraphicsDevice device, List<VertexBuffer> bindings) {
        synchronized (GAMES) {
            if (bindings.isEmpty()) {
                DEVICE_VERTEX_BINDINGS.remove(device);
            } else {
                DEVICE_VERTEX_BINDINGS.put(device, List.copyOf(bindings));
            }
        }
    }

    public static void closeSpriteFont(SpriteFont font) {
        Objects.requireNonNull(font, "font");
        NativeResourceHandle handle;
        Texture2D atlas;
        synchronized (GAMES) {
            handle = SPRITE_FONTS.get(font);
            atlas = SPRITE_FONT_ATLASES.get(font);
        }
        if (handle != null) {
            handle.close();
            synchronized (GAMES) {
                SPRITE_FONTS.remove(font);
                Game owner = SPRITE_FONT_OWNERS.remove(font);
                List<SpriteFont> children = GAME_SPRITE_FONTS.get(owner);
                if (children != null) {
                    children.remove(font);
                }
            }
        }
        if (atlas != null) {
            atlas.close();
            synchronized (GAMES) {
                SPRITE_FONT_ATLASES.remove(font);
            }
        }
    }

    public static void unloadContentManager(ContentManager manager) {
        NativeResourceHandle handle;
        synchronized (GAMES) {
            handle = CONTENT_MANAGERS.get(Objects.requireNonNull(manager, "manager"));
        }
        if (handle != null) {
            check("cna_content_manager_unload",
                    nativeUnloadContentManager(handle.requireValue()));
        }
    }

    public static void closeContentManager(ContentManager manager) {
        Objects.requireNonNull(manager, "manager");
        NativeResourceHandle handle;
        synchronized (GAMES) {
            handle = CONTENT_MANAGERS.get(manager);
        }
        if (handle == null) {
            return;
        }
        handle.close();
        synchronized (GAMES) {
            CONTENT_MANAGERS.remove(manager);
            Game owner = CONTENT_MANAGER_OWNERS.remove(manager);
            List<ContentManager> children = GAME_CONTENT_MANAGERS.get(owner);
            if (children != null) {
                children.remove(manager);
            }
        }
    }

    public static void closeGraphicsResources(Game game) {
        RuntimeException failure = closeSpriteFonts(game, null);
        failure = closeContentManagers(game, failure);
        List<GraphicsResource> snapshot;
        synchronized (GAMES) {
            List<GraphicsResource> resources = GAME_RESOURCES.get(game);
            snapshot = resources == null ? List.of() : new ArrayList<>(resources);
        }
        Collections.reverse(snapshot);
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
            GAME_SPRITE_FONTS.remove(game);
            GAME_CONTENT_MANAGERS.remove(game);
            DEVICES.entrySet().removeIf(entry -> {
                if (entry.getValue() != game) {
                    return false;
                }
                DEVICE_CALLBACK_FAILURES.remove(entry.getKey());
                DEVICE_VERTEX_BINDINGS.remove(entry.getKey());
                DEVICE_INDEX_BINDINGS.remove(entry.getKey());
                return true;
            });
            if (currentGame == game) {
                currentGame = null;
            }
        }
    }

    static void destroyGraphicsDeviceManager(
            GraphicsDeviceManager manager,
            long handle) {
        Objects.requireNonNull(manager, "manager");
        check("cna_graphics_device_manager_destroy",
                nativeDestroyGraphicsDeviceManager(handle));
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

    private static long deviceGameValue(GraphicsDevice device) {
        return gameHandle(deviceGame(device), "GraphicsDevice").requireValue();
    }

    private static void checkDevice(GraphicsDevice device, String operation, int result) {
        try {
            check(operation, result);
        } catch (RuntimeException nativeFailure) {
            try {
                rethrowGraphicsDeviceListenerFailure(device);
            } catch (Throwable callbackFailure) {
                nativeFailure.addSuppressed(callbackFailure);
            }
            throw nativeFailure;
        }
        rethrowGraphicsDeviceListenerFailure(device);
    }

    private static long deviceLongResult(
            GraphicsDevice device, String operation, long result) {
        try {
            long value = longResult(operation, result);
            rethrowGraphicsDeviceListenerFailure(device);
            return value;
        } catch (RuntimeException nativeFailure) {
            try {
                rethrowGraphicsDeviceListenerFailure(device);
            } catch (Throwable callbackFailure) {
                nativeFailure.addSuppressed(callbackFailure);
            }
            throw nativeFailure;
        }
    }

    private static int deviceIntResult(
            GraphicsDevice device, String operation, long result) {
        return Math.toIntExact(deviceLongResult(device, operation, result));
    }

    private static void restoreGraphicsResourceEvent(GraphicsResource previous) {
        if (previous == null) {
            GRAPHICS_RESOURCE_EVENT.remove();
        } else {
            GRAPHICS_RESOURCE_EVENT.set(previous);
        }
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

    private static void registerSpriteFont(
            Game game, SpriteFont font, long value, Texture2D atlas) {
        NativeResourceHandle handle = new NativeResourceHandle(
                value, NativeBindings::destroySpriteFont);
        synchronized (GAMES) {
            SPRITE_FONTS.put(font, handle);
            SPRITE_FONT_OWNERS.put(font, game);
            SPRITE_FONT_ATLASES.put(font, atlas);
            GAME_SPRITE_FONTS.computeIfAbsent(game, ignored -> new ArrayList<>()).add(font);
        }
    }

    private static long contentManagerValue(
            ContentManager manager,
            GraphicsDevice graphicsDevice,
            String rootDirectory) {
        Objects.requireNonNull(manager, "manager");
        NativeResourceHandle existing;
        synchronized (GAMES) {
            existing = CONTENT_MANAGERS.get(manager);
        }
        if (existing != null) {
            check("cna_content_manager_set_root_directory", nativeSetContentManagerRootDirectory(
                    existing.requireValue(), rootDirectory.getBytes(StandardCharsets.UTF_8)));
            return existing.requireValue();
        }

        Game game = deviceGame(graphicsDevice);
        long[] output = new long[1];
        check("cna_content_manager_create", nativeCreateContentManager(
                gameHandle(game, "ContentManager").requireValue(),
                rootDirectory.getBytes(StandardCharsets.UTF_8),
                output));
        int registrationResult = nativeRegisterContentManagerBuiltinLoaders(output[0]);
        if (registrationResult != 0) {
            CnaNativeException registrationFailure = failure(
                    "cna_content_manager_register_builtin_loaders", registrationResult);
            int destroyResult = nativeDestroyContentManager(output[0]);
            if (destroyResult != 0) {
                registrationFailure.addSuppressed(
                        failure("cna_content_manager_destroy", destroyResult));
            }
            throw registrationFailure;
        }
        NativeResourceHandle created = new NativeResourceHandle(
                output[0], NativeBindings::destroyContentManager);
        synchronized (GAMES) {
            CONTENT_MANAGERS.put(manager, created);
            CONTENT_MANAGER_OWNERS.put(manager, game);
            GAME_CONTENT_MANAGERS.computeIfAbsent(game, ignored -> new ArrayList<>()).add(manager);
        }
        return created.requireValue();
    }

    private static long spriteFontValue(SpriteFont font) {
        NativeResourceHandle handle;
        synchronized (GAMES) {
            handle = SPRITE_FONTS.get(Objects.requireNonNull(font, "font"));
        }
        if (handle == null) {
            throw new IllegalStateException("SpriteFont is not backed by an open CNA resource");
        }
        return handle.requireValue();
    }

    private static SpriteFontInfo spriteFontInfo(SpriteFont font) {
        int[] integers = new int[4];
        float[] spacing = new float[1];
        check("cna_sprite_font_get_info",
                nativeGetSpriteFontInfo(spriteFontValue(font), integers, spacing));
        if (integers[0] < 0 || integers[3] < 0 || integers[3] > 1
                || integers[2] < Character.MIN_VALUE || integers[2] > Character.MAX_VALUE
                || !Float.isFinite(spacing[0])) {
            throw new IllegalStateException("CNA returned invalid SpriteFont metadata");
        }
        return new SpriteFontInfo(integers, spacing[0]);
    }

    private static RuntimeException closeSpriteFonts(Game game, RuntimeException failure) {
        List<SpriteFont> snapshot;
        synchronized (GAMES) {
            List<SpriteFont> resources = GAME_SPRITE_FONTS.get(game);
            snapshot = resources == null ? List.of() : new ArrayList<>(resources);
        }
        Collections.reverse(snapshot);
        for (SpriteFont font : snapshot) {
            try {
                closeSpriteFont(font);
            } catch (RuntimeException exception) {
                if (failure == null) {
                    failure = exception;
                } else {
                    failure.addSuppressed(exception);
                }
            }
        }
        return failure;
    }

    private static RuntimeException closeContentManagers(
            Game game, RuntimeException failure) {
        List<ContentManager> snapshot;
        synchronized (GAMES) {
            List<ContentManager> resources = GAME_CONTENT_MANAGERS.get(game);
            snapshot = resources == null ? List.of() : new ArrayList<>(resources);
        }
        Collections.reverse(snapshot);
        for (ContentManager manager : snapshot) {
            try {
                closeContentManager(manager);
            } catch (RuntimeException exception) {
                if (failure == null) {
                    failure = exception;
                } else {
                    failure.addSuppressed(exception);
                }
            }
        }
        return failure;
    }

    private record SpriteFontInfo(int[] integers, float spacing) {
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

    private static int[] vertexBufferInfoOrClose(VertexBuffer buffer) {
        int[] info = new int[4];
        try {
            check("cna_vertex_buffer_get_info",
                    nativeGetVertexBufferInfo(resourceValue(buffer), info));
            return info;
        } catch (RuntimeException failure) {
            try {
                closeGraphicsResource(buffer);
            } catch (RuntimeException closeFailure) {
                failure.addSuppressed(closeFailure);
            }
            throw failure;
        }
    }

    private static int[] indexBufferInfoOrClose(IndexBuffer buffer) {
        int[] info = new int[3];
        try {
            check("cna_index_buffer_get_info",
                    nativeGetIndexBufferInfo(resourceValue(buffer), info));
            return info;
        } catch (RuntimeException failure) {
            try {
                closeGraphicsResource(buffer);
            } catch (RuntimeException closeFailure) {
                failure.addSuppressed(closeFailure);
            }
            throw failure;
        }
    }

    private static int[] textureCubeInfoOrClose(TextureCube texture) {
        int[] info = new int[3];
        try {
            check("cna_texturecube_get_info",
                    nativeGetTextureCubeInfo(resourceValue(texture), info));
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

    private static int[] renderTargetInfoOrClose(GraphicsResource renderTarget) {
        try {
            return renderTargetInfo(renderTarget);
        } catch (RuntimeException failure) {
            try {
                closeGraphicsResource(renderTarget);
            } catch (RuntimeException closeFailure) {
                failure.addSuppressed(closeFailure);
            }
            throw failure;
        }
    }

    private static int[] renderTargetInfo(GraphicsResource renderTarget) {
        int[] info = new int[10];
        check("cna_render_target_get_info",
                nativeGetRenderTargetInfo(resourceValue(renderTarget), info));
        return info;
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

    private static Texture findTexture(long value, Texture cached) {
        synchronized (GAMES) {
            if (cached != null) {
                NativeResourceHandle cachedHandle = RESOURCES.get(cached);
                if (cachedHandle != null && !cachedHandle.isClosed()
                        && cachedHandle.requireValue() == value) {
                    return cached;
                }
            }
            for (Map.Entry<GraphicsResource, NativeResourceHandle> entry : RESOURCES.entrySet()) {
                if (entry.getKey() instanceof Texture texture
                        && !entry.getValue().isClosed()
                        && entry.getValue().requireValue() == value) {
                    return texture;
                }
            }
        }
        return null;
    }

    private static VertexBuffer findVertexBuffer(long value) {
        GraphicsResource result = findResource(value, VertexBuffer.class);
        return result instanceof VertexBuffer buffer ? buffer : null;
    }

    private static IndexBuffer findIndexBuffer(long value) {
        GraphicsResource result = findResource(value, IndexBuffer.class);
        return result instanceof IndexBuffer buffer ? buffer : null;
    }

    private static GraphicsResource findResource(
            long value, Class<? extends GraphicsResource> expectedType) {
        synchronized (GAMES) {
            for (Map.Entry<GraphicsResource, NativeResourceHandle> entry : RESOURCES.entrySet()) {
                if (expectedType.isInstance(entry.getKey())
                        && !entry.getValue().isClosed()
                        && entry.getValue().requireValue() == value) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    private static void requireResourceOwner(
            GraphicsDevice device, GraphicsResource resource) {
        if (resource.getGraphicsDevice() != device) {
            throw new IllegalArgumentException(
                    resource.getClass().getSimpleName()
                            + " belongs to a different GraphicsDevice");
        }
    }

    private static int[] requireLength(int[] values, int length, String name) {
        Objects.requireNonNull(values, "values");
        if (values.length != length) {
            throw new IllegalArgumentException(name + " descriptor must contain " + length + " values");
        }
        return Arrays.copyOf(values, values.length);
    }

    private static float[] requireLength(float[] values, int length, String name) {
        Objects.requireNonNull(values, "values");
        if (values.length != length) {
            throw new IllegalArgumentException(name + " descriptor must contain " + length + " values");
        }
        return Arrays.copyOf(values, values.length);
    }

    private static void requireOutputLength(int[] values, int length, String name) {
        Objects.requireNonNull(values, "values");
        if (values.length != length) {
            throw new IllegalArgumentException(name + " output must contain " + length + " values");
        }
    }

    private static void requireOutputLength(float[] values, int length, String name) {
        Objects.requireNonNull(values, "values");
        if (values.length != length) {
            throw new IllegalArgumentException(name + " output must contain " + length + " values");
        }
    }

    private static void destroyTexture2D(long handle) {
        check("cna_texture2d_destroy", nativeDestroyTexture2D(handle));
    }

    private static void destroyVertexBuffer(long handle) {
        check("cna_vertex_buffer_destroy", nativeDestroyVertexBuffer(handle));
    }

    private static void destroyIndexBuffer(long handle) {
        check("cna_index_buffer_destroy", nativeDestroyIndexBuffer(handle));
    }

    private static void destroyTextureCube(long handle) {
        check("cna_texturecube_destroy", nativeDestroyTextureCube(handle));
    }

    private static void destroyRenderTarget(long handle) {
        check("cna_render_target_destroy", nativeDestroyRenderTarget(handle));
    }

    private static void destroySpriteBatch(long handle) {
        check("cna_sprite_batch_destroy", nativeDestroySpriteBatch(handle));
    }

    private static void destroySpriteFont(long handle) {
        check("cna_sprite_font_destroy", nativeDestroySpriteFont(handle));
    }

    private static void destroyContentManager(long handle) {
        check("cna_content_manager_destroy", nativeDestroyContentManager(handle));
    }

    private static WindowHandle findWindowHandle(long value) {
        for (Map.Entry<WindowHandle, Long> entry : WINDOW_HANDLES.entrySet()) {
            if (entry.getValue() == value) {
                return entry.getKey();
            }
        }
        return null;
    }

    private static String getGraphicsAdapterString(
            int adapterIndex,
            long byteLength,
            boolean description) {
        int size = Math.toIntExact(byteLength);
        byte[] utf8 = new byte[size];
        check(description
                        ? "cna_graphics_adapter_copy_description"
                        : "cna_graphics_adapter_copy_device_name",
                nativeCopyGraphicsAdapterString(
                        currentGameHandle("GraphicsAdapter metadata").requireValue(),
                        adapterIndex, description, utf8));
        return new String(utf8, StandardCharsets.UTF_8);
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

    private static native int nativeGetGamePadState(
            long game, int playerIndex, int deadZone, int[] discrete, float[] analog);

    private static native int nativeGetGamePadCapabilities(
            long game, int playerIndex, int[] capabilities);

    private static native int nativeSetGamePadVibration(
            long game, int playerIndex, float leftMotor, float rightMotor);

    private static native int nativeCreateGraphicsDeviceManager(
            long game, GraphicsDeviceManager manager, long[] output);

    private static native long nativeGetGraphicsDeviceManagerGraphicsProfile(long manager);

    private static native int nativeSetGraphicsDeviceManagerGraphicsProfile(
            long manager, int value);

    private static native int nativeGetGraphicsDeviceManagerIsFullScreen(long manager);

    private static native int nativeSetGraphicsDeviceManagerIsFullScreen(
            long manager, boolean value);

    private static native int nativeGetGraphicsDeviceManagerPreferMultiSampling(long manager);

    private static native int nativeSetGraphicsDeviceManagerPreferMultiSampling(
            long manager, boolean value);

    private static native long nativeGetGraphicsDeviceManagerPreferredBackBufferFormat(
            long manager);

    private static native int nativeSetGraphicsDeviceManagerPreferredBackBufferFormat(
            long manager, int value);

    private static native long nativeGetGraphicsDeviceManagerPreferredBackBufferWidth(long manager);

    private static native int nativeSetGraphicsDeviceManagerPreferredBackBufferWidth(
            long manager, int value);

    private static native long nativeGetGraphicsDeviceManagerPreferredBackBufferHeight(long manager);

    private static native int nativeSetGraphicsDeviceManagerPreferredBackBufferHeight(
            long manager, int value);

    private static native long nativeGetGraphicsDeviceManagerPreferredDepthStencilFormat(
            long manager);

    private static native int nativeSetGraphicsDeviceManagerPreferredDepthStencilFormat(
            long manager, int value);

    private static native int nativeGetGraphicsDeviceManagerSynchronizeWithVerticalRetrace(
            long manager);

    private static native int nativeSetGraphicsDeviceManagerSynchronizeWithVerticalRetrace(
            long manager, boolean value);

    private static native long nativeGetGraphicsDeviceManagerSupportedOrientations(long manager);

    private static native int nativeSetGraphicsDeviceManagerSupportedOrientations(
            long manager, int value);

    private static native int nativeApplyGraphicsDeviceManagerChanges(long manager);

    private static native int nativeToggleGraphicsDeviceManagerFullScreen(long manager);

    private static native int nativeCreateGraphicsDeviceManagerDevice(long manager);

    private static native int nativeBeginGraphicsDeviceManagerDraw(long manager);

    private static native int nativeEndGraphicsDeviceManagerDraw(long manager);

    private static native int nativeDisposeGraphicsDeviceManager(long manager);

    private static native int nativeDestroyGraphicsDeviceManager(long manager);

    private static native long nativeGetGraphicsAdapterCount(long game);

    private static native int nativeGetGraphicsAdapterInfo(
            long game, int adapterIndex, long[] output);

    private static native int nativeCopyGraphicsAdapterString(
            long game, int adapterIndex, boolean description, byte[] output);

    private static native int nativeGetGraphicsAdapterCurrentDisplayMode(
            long game, int adapterIndex, int[] output);

    private static native long nativeGetGraphicsAdapterDisplayModeCount(
            long game, int adapterIndex);

    private static native int nativeCopyGraphicsAdapterDisplayModes(
            long game, int adapterIndex, int[] output);

    private static native int nativeSetGraphicsAdapterDevicePreferences(
            long game, int adapterIndex, boolean useNullDevice, boolean useReferenceDevice);

    private static native int nativeIsGraphicsAdapterProfileSupported(
            long game, int adapterIndex, int profile);

    private static native int nativeQueryGraphicsAdapterFormat(
            long game, int adapterIndex, boolean backBuffer, int profile,
            int format, int depthFormat, int multiSampleCount, int[] output);

    private static native int nativeGetGraphicsAdapterMonitorHandle(
            long game, int adapterIndex, long[] output);

    private static native int nativeEnsureGraphicsDeviceEvents(
            long game, GraphicsDevice device);

    private static native int nativeGetGraphicsDeviceIsDisposed(long game);

    private static native long nativeGetGraphicsDeviceStatus(long game);

    private static native long nativeGetGraphicsDeviceAdapterIndex(long game);

    private static native long nativeGetGraphicsDeviceProfile(long game);

    private static native int nativeSetGraphicsDeviceProfile(long game, int profile);

    private static native int nativeGetGraphicsDeviceScissorRectangle(
            long game, int[] output);

    private static native int nativeSetGraphicsDeviceScissorRectangle(
            long game, int x, int y, int width, int height);

    private static native int nativeGetGraphicsDeviceViewport(
            long game, int[] bounds, float[] depth);

    private static native int nativeSetGraphicsDeviceViewport(
            long game, int x, int y, int width, int height,
            float minDepth, float maxDepth);

    private static native long nativeGetGraphicsDeviceBlendFactor(long game);

    private static native int nativeSetGraphicsDeviceBlendFactor(
            long game, int packedColor);

    private static native int nativeGetGraphicsDeviceBlendState(
            long game, int[] output);

    private static native int nativeSetGraphicsDeviceBlendState(
            long game, int[] values);

    private static native int nativeGetGraphicsDeviceDepthStencilState(
            long game, int[] output);

    private static native int nativeSetGraphicsDeviceDepthStencilState(
            long game, int[] values);

    private static native int nativeGetGraphicsDeviceRasterizerState(
            long game, int[] integers, float[] floats);

    private static native int nativeSetGraphicsDeviceRasterizerState(
            long game, int[] integers, float[] floats);

    private static native int nativeGetGraphicsDeviceSamplerState(
            long game, int shaderStage, int slot, int[] integers, float[] bias);

    private static native int nativeSetGraphicsDeviceSamplerState(
            long game, int shaderStage, int slot, int[] integers, float bias);

    private static native int nativeGetGraphicsDeviceTexture(
            long game, int shaderStage, int slot, long[] output);

    private static native int nativeSetGraphicsDeviceTexture(
            long game, int shaderStage, int slot, long texture);

    private static native long nativeGetGraphicsDeviceMultiSampleMask(long game);

    private static native int nativeSetGraphicsDeviceMultiSampleMask(long game, int value);

    private static native long nativeGetGraphicsDeviceReferenceStencil(long game);

    private static native int nativeSetGraphicsDeviceReferenceStencil(long game, int value);

    private static native int nativeGetGraphicsDevicePresentationParameters(
            long game, int[] output);

    private static native int nativeGetGraphicsDeviceDisplayMode(long game, int[] output);

    private static native int nativeGetGraphicsDeviceBackBufferInfo(long game, int[] output);

    private static native int nativeGetGraphicsDeviceBackBufferData(
            long game,
            boolean hasRectangle,
            int x,
            int y,
            int width,
            int height,
            int startIndex,
            int elementCount,
            int[] output);

    private static native int nativeClearGraphicsDevice(
            long game, int options, int packedColor, float depth, int stencil);

    private static native int nativePresentGraphicsDevice(long game);

    private static native int nativeResetGraphicsDevice(long game);

    private static native int nativeResetGraphicsDeviceWithParameters(
            long game, int[] parameters, int adapterIndex);

    private static native int nativeSetGraphicsDeviceVertexBuffer(
            long game, long vertexBuffer, int vertexOffset);

    private static native int nativeSetGraphicsDeviceVertexBuffers(
            long game, long[] vertexBuffers, int[] vertexOffsets, int[] instanceFrequencies);

    private static native long nativeGetGraphicsDeviceVertexBufferCount(long game);

    private static native int nativeCopyGraphicsDeviceVertexBuffers(
            long game, long[] vertexBuffers, int[] vertexOffsets, int[] instanceFrequencies);

    private static native int nativeSetGraphicsDeviceIndexBuffer(long game, long indexBuffer);

    private static native int nativeGetGraphicsDeviceIndexBuffer(long game, long[] output);

    private static native int nativeDrawPrimitives(
            long game, int primitiveType, int startVertex, int primitiveCount);

    private static native int nativeDrawIndexedPrimitives(
            long game,
            int primitiveType,
            int baseVertex,
            int minVertexIndex,
            int numVertices,
            int startIndex,
            int primitiveCount);

    private static native int nativeDrawInstancedPrimitives(
            long game,
            int primitiveType,
            int baseVertex,
            int minVertexIndex,
            int numVertices,
            int startIndex,
            int primitiveCount,
            int instanceCount);

    private static native int nativeDrawUserPrimitives(
            long game,
            int primitiveType,
            int vertexSource,
            byte[] vertexData,
            int vertexStride,
            int vertexOffset,
            int numVertices,
            int primitiveCount,
            int[] declaration);

    private static native int nativeDrawUserIndexedPrimitives16(
            long game,
            int primitiveType,
            int vertexSource,
            byte[] vertexData,
            int vertexStride,
            int vertexOffset,
            int numVertices,
            short[] indexData,
            int indexOffset,
            int primitiveCount,
            int[] declaration);

    private static native int nativeDrawUserIndexedPrimitives32(
            long game,
            int primitiveType,
            int vertexSource,
            byte[] vertexData,
            int vertexStride,
            int vertexOffset,
            int numVertices,
            int[] indexData,
            int indexOffset,
            int primitiveCount,
            int[] declaration);

    private static native int nativeGetMouseState(long game, int[] state);

    private static native int nativeSetMousePosition(long game, int x, int y);

    private static native int nativeGetMouseWindowHandle(long game, long[] window);

    private static native int nativeSetMouseWindowHandle(long game, long window);

    private static native int nativeCreateVertexBuffer(
            long game,
            int vertexStride,
            int[] declaration,
            int vertexCount,
            int usage,
            long[] output);

    private static native int nativeGetVertexBufferInfo(long vertexBuffer, int[] output);

    private static native int nativeSetVertexBufferData(
            long vertexBuffer,
            int offsetInBytes,
            byte[] payload,
            int vertexCount,
            int vertexStride);

    private static native int nativeGetVertexBufferData(
            long vertexBuffer,
            int offsetInBytes,
            int vertexCount,
            int vertexStride,
            byte[] output);

    private static native int nativeDestroyVertexBuffer(long vertexBuffer);

    private static native int nativeCreateIndexBuffer(
            long game,
            int indexElementSize,
            int indexCount,
            int usage,
            long[] output);

    private static native int nativeGetIndexBufferInfo(long indexBuffer, int[] output);

    private static native int nativeSetIndexBufferData(
            long indexBuffer, int offsetInBytes, int indexElementSize, int[] values);

    private static native int nativeGetIndexBufferData(
            long indexBuffer, int indexElementSize, int[] output);

    private static native int nativeDestroyIndexBuffer(long indexBuffer);

    private static native int nativeCreateContentManager(
            long game, byte[] rootDirectory, long[] output);

    private static native int nativeSetContentManagerRootDirectory(
            long contentManager, byte[] rootDirectory);

    private static native int nativeLoadContentTexture2D(
            long contentManager, byte[] assetName, long[] output);

    private static native int nativeLoadContentSpriteFont(
            long contentManager, byte[] assetName, long[] output);

    private static native int nativeUnloadContentManager(long contentManager);

    private static native int nativeRegisterContentManagerBuiltinLoaders(long contentManager);

    private static native int nativeDestroyContentManager(long contentManager);

    private static native int nativeCreateTexture2D(
            long game, int width, int height, boolean mipMap, int format, long[] output);

    private static native int nativeCreateTexture2DFromEncoded(
            long game, byte[] encoded, int width, int height, boolean zoom, boolean resize,
            long[] output);

    private static native int nativeGetTexture2DInfo(long texture, int[] output);

    private static native int nativeSetTexture2DData(long texture, int[] packedColors);

    private static native int nativeGetTexture2DData(long texture, int[] packedColors);

    private static native int nativeSetTexture2DTypedData(
            long texture,
            int dataType,
            int level,
            boolean hasRectangle,
            int x,
            int y,
            int width,
            int height,
            int startIndex,
            int elementCount,
            byte[] payload);

    private static native int nativeGetTexture2DTypedData(
            long texture,
            int dataType,
            int level,
            boolean hasRectangle,
            int x,
            int y,
            int width,
            int height,
            int startIndex,
            int elementCount,
            byte[] payload);

    private static native long nativeGetTexture2DEncodedSize(
            long texture, int format, int width, int height);

    private static native int nativeCopyTexture2DEncoded(
            long texture, int format, int width, int height, byte[] output);

    private static native int nativeDestroyTexture2D(long texture);

    private static native int nativeGetSpriteFontInfo(
            long spriteFont, int[] integers, float[] spacing);

    private static native int nativeCopySpriteFontCharacters(
            long spriteFont, char[] characters);

    private static native int nativeSetSpriteFontDefaultCharacter(
            long spriteFont, boolean hasValue, int value);

    private static native int nativeSetSpriteFontLineSpacing(long spriteFont, int value);

    private static native int nativeSetSpriteFontSpacing(long spriteFont, float value);

    private static native int nativeMeasureSpriteFont(
            long spriteFont, byte[] text, float[] output);

    private static native int nativeDestroySpriteFont(long spriteFont);

    private static native int nativeCreateTextureCube(
            long game, int size, boolean mipMap, int format, long[] output);

    private static native int nativeGetTextureCubeInfo(long texture, int[] output);

    private static native int nativeSetTextureCubeData(
            long texture,
            int face,
            int level,
            boolean hasRectangle,
            int x,
            int y,
            int width,
            int height,
            int startIndex,
            int elementCount,
            int[] packedColors);

    private static native int nativeGetTextureCubeData(
            long texture,
            int face,
            int level,
            boolean hasRectangle,
            int x,
            int y,
            int width,
            int height,
            int startIndex,
            int elementCount,
            int[] packedColors);

    private static native int nativeDestroyTextureCube(long texture);

    private static native int nativeCreateRenderTarget2D(
            long game,
            int width,
            int height,
            boolean mipMap,
            int format,
            int depthFormat,
            int multiSampleCount,
            int usage,
            long[] output);

    private static native int nativeCreateRenderTargetCube(
            long game,
            int size,
            boolean mipMap,
            int format,
            int depthFormat,
            int multiSampleCount,
            int usage,
            long[] output);

    private static native int nativeGetRenderTargetInfo(long renderTarget, int[] output);

    private static native int nativeSetGraphicsDeviceRenderTarget2D(
            long game, long renderTarget);

    private static native int nativeSetGraphicsDeviceRenderTargetCube(
            long game, long renderTarget, int face);

    private static native int nativeSetGraphicsDeviceRenderTargets(
            long game, long[] renderTargets, int[] faces);

    private static native long nativeGetGraphicsDeviceRenderTargetCount(long game);

    private static native int nativeCopyGraphicsDeviceRenderTargets(
            long game, long[] renderTargets, int[] faces);

    private static native int nativeDestroyRenderTarget(long renderTarget);

    private static native int nativeCreateSpriteBatch(long game, long[] output);

    private static native int nativeBeginSpriteBatch(long spriteBatch, int sortMode);

    private static native int nativeBeginSpriteBatchWithStates(
            long spriteBatch,
            int sortMode,
            int[] blend,
            int[] sampler,
            float samplerBias,
            int[] depthStencil,
            int[] rasterizer,
            float[] rasterizerFloats);

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

    private static native int nativeDrawSpriteString(
            long spriteBatch,
            long spriteFont,
            byte[] text,
            float positionX,
            float positionY,
            int packedColor,
            float rotation,
            float originX,
            float originY,
            float scaleX,
            float scaleY,
            int effects,
            float layerDepth);

    private static native int nativeEndSpriteBatch(long spriteBatch);

    private static native int nativeDestroySpriteBatch(long spriteBatch);

    private static native int nativeDestroyGame(long game);

    private static native String nativeLastErrorMessage();
}
