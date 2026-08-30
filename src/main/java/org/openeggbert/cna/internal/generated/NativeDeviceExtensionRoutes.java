package org.openeggbert.cna.internal.generated;

/**
 * Generated CNA C ABI declarations for NativeDeviceExtensionRoutes.
 *
 * <p>Produced by {@code tools/native-abi/generate_jni.py} from the live CNA C headers.
 * Do not edit: every signature here is the header's own declaration, and regenerating
 * is how a change upstream reaches Java. This class is not application API.
 */
public final class NativeDeviceExtensionRoutes {

    private NativeDeviceExtensionRoutes() {
    }

    /**
     * cna_devices_clipboard_set_text_ext (devices.h).
     */
    public static native int devicesClipboardSetTextExt(long game, byte[] text, boolean[] outAccepted);

    /**
     * cna_devices_ext_is_available (devices.h).
     */
    public static native int devicesExtIsAvailable(boolean[] outAvailable);

    /**
     * cna_display_info_get_content_scale_ext (devices.h).
     */
    public static native int displayInfoGetContentScaleExt(long game, float[] outScale);

    /**
     * cna_display_info_get_safe_area_ext (devices.h).
     *
     * <p>outAreaIntegral carries CNA_Rectangle in this order:
     * <ol start="0">
     *   <li>{@code x} (int32_t)</li>
     *   <li>{@code y} (int32_t)</li>
     *   <li>{@code width} (int32_t)</li>
     *   <li>{@code height} (int32_t)</li>
     * </ol>
     */
    public static native int displayInfoGetSafeAreaExt(long game, long[] outAreaIntegral);

    /**
     * cna_locale_copy_country_at_ext (devices.h).
     */
    public static native int localeCopyCountryAtExt(long game, long index, byte[] destination, long[] outBytes);

    /**
     * cna_locale_copy_language_at_ext (devices.h).
     */
    public static native int localeCopyLanguageAtExt(long game, long index, byte[] destination, long[] outBytes);

    /**
     * cna_locale_get_country_size_at_ext (devices.h).
     */
    public static native int localeGetCountrySizeAtExt(long game, long index, long[] outBytes);

    /**
     * cna_locale_get_language_size_at_ext (devices.h).
     */
    public static native int localeGetLanguageSizeAtExt(long game, long index, long[] outBytes);

    /**
     * cna_locale_get_preferred_count_ext (devices.h).
     */
    public static native int localeGetPreferredCountExt(long game, long[] outCount);

    /**
     * cna_power_get_battery_percent_ext (devices.h).
     */
    public static native int powerGetBatteryPercentExt(long game, int[] outPercent);

    /**
     * cna_power_get_seconds_remaining_ext (devices.h).
     */
    public static native int powerGetSecondsRemainingExt(long game, int[] outSeconds);

    /**
     * cna_power_get_state_ext (devices.h).
     */
    public static native int powerGetStateExt(long game, int[] outState);

    /**
     * cna_system_info_get_logical_cpu_core_count_ext (devices.h).
     */
    public static native int systemInfoGetLogicalCpuCoreCountExt(long game, int[] outCount);

    /**
     * cna_system_info_get_system_ram_megabytes_ext (devices.h).
     */
    public static native int systemInfoGetSystemRamMegabytesExt(long game, int[] outMegabytes);

    /**
     * cna_url_launcher_open_ext (devices.h).
     */
    public static native int urlLauncherOpenExt(long game, byte[] url, boolean[] outOpened);

    /**
     * cna_vibrate_controller_copy_device_name_ext (devices.h).
     */
    public static native int vibrateControllerCopyDeviceNameExt(long game, byte[] destination, long[] outBytes);

    /**
     * cna_vibrate_controller_get_device_name_size_ext (devices.h).
     */
    public static native int vibrateControllerGetDeviceNameSizeExt(long game, long[] outBytes);

    /**
     * cna_vibrate_controller_get_is_supported_ext (devices.h).
     */
    public static native int vibrateControllerGetIsSupportedExt(long game, boolean[] outSupported);

    /**
     * cna_vibrate_controller_start (devices.h).
     */
    public static native int vibrateControllerStart(long game, long durationTicks);

    /**
     * cna_vibrate_controller_start_left_right_ext (devices.h).
     */
    public static native int vibrateControllerStartLeftRightExt(long game, float largeMotor, float smallMotor, long durationTicks);

    /**
     * cna_vibrate_controller_start_with_intensity_ext (devices.h).
     */
    public static native int vibrateControllerStartWithIntensityExt(long game, long durationTicks, float intensity);

    /**
     * cna_vibrate_controller_stop (devices.h).
     */
    public static native int vibrateControllerStop(long game);
}
