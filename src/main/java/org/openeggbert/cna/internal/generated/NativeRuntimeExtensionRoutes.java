package org.openeggbert.cna.internal.generated;

/**
 * Generated CNA C ABI declarations for NativeRuntimeExtensionRoutes.
 *
 * <p>Produced by {@code tools/native-abi/generate_jni.py} from the live CNA C headers.
 * Do not edit: every signature here is the header's own declaration, and regenerating
 * is how a change upstream reaches Java. This class is not application API.
 */
public final class NativeRuntimeExtensionRoutes {

    private NativeRuntimeExtensionRoutes() {
    }

    /**
     * cna_assembly_copy_title_ext (core_ext.h).
     */
    public static native int assemblyCopyTitleExt(byte[] destination, long[] outBytes);

    /**
     * cna_assembly_set_title_ext (core_ext.h).
     */
    public static native int assemblySetTitleExt(byte[] title);

    /**
     * cna_desktop_os_get_current (core_ext.h).
     */
    public static native int desktopOsGetCurrent(int[] outOs);

    /**
     * cna_graphics_backend_category_copy_name (core_ext.h).
     */
    public static native int graphicsBackendCategoryCopyName(int category, byte[] destination, long[] outBytes);

    /**
     * cna_graphics_backend_category_get_name_size (core_ext.h).
     */
    public static native int graphicsBackendCategoryGetNameSize(int category, long[] outBytes);

    /**
     * cna_graphics_backend_get_current_category (core_ext.h).
     */
    public static native int graphicsBackendGetCurrentCategory(int[] outCategory);

    /**
     * cna_graphics_backend_get_current_maturity (core_ext.h).
     */
    public static native int graphicsBackendGetCurrentMaturity(int[] outMaturity);

    /**
     * cna_graphics_backend_maturity_copy_name (core_ext.h).
     */
    public static native int graphicsBackendMaturityCopyName(int maturity, byte[] destination, long[] outBytes);

    /**
     * cna_graphics_backend_maturity_get_name_size (core_ext.h).
     */
    public static native int graphicsBackendMaturityGetNameSize(int maturity, long[] outBytes);

    /**
     * cna_graphics_renderer_copy_current_name (core_ext.h).
     */
    public static native int graphicsRendererCopyCurrentName(byte[] destination, long[] outBytes);

    /**
     * cna_graphics_renderer_get_current_name_size (core_ext.h).
     */
    public static native int graphicsRendererGetCurrentNameSize(long[] outBytes);

    /**
     * cna_logger_get_minimum_level (core_ext.h).
     */
    public static native int loggerGetMinimumLevel(int[] outLevel);

    /**
     * cna_logger_log (core_ext.h).
     */
    public static native int loggerLog(int level, byte[] message, int category, boolean condition);

    /**
     * cna_logger_set_minimum_level (core_ext.h).
     */
    public static native int loggerSetMinimumLevel(int level);

    /**
     * cna_platform_copy_current_name_ext (core_ext.h).
     */
    public static native int platformCopyCurrentNameExt(byte[] destination, long[] outBytes);

    /**
     * cna_platform_get_current (core_ext.h).
     */
    public static native int platformGetCurrent(int[] outPlatform);

    /**
     * cna_platform_get_current_name_size_ext (core_ext.h).
     */
    public static native int platformGetCurrentNameSizeExt(long[] outBytes);

    /**
     * cna_platform_get_is_apple_ext (core_ext.h).
     */
    public static native int platformGetIsAppleExt(boolean[] outApple);

    /**
     * cna_platform_get_is_mobile_ext (core_ext.h).
     */
    public static native int platformGetIsMobileExt(boolean[] outMobile);
}
