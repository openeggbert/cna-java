package org.openeggbert.cna.internal.generated;

/**
 * Generated CNA C ABI declarations for NativeInputExtensionRoutes.
 *
 * <p>Produced by {@code tools/native-abi/generate_jni.py} from the live CNA C headers.
 * Do not edit: every signature here is the header's own declaration, and regenerating
 * is how a change upstream reaches Java. This class is not application API.
 */
public final class NativeInputExtensionRoutes {

    private NativeInputExtensionRoutes() {
    }

    /**
     * cna_mouse_cursor_create_ext (input_cursor.h).
     */
    public static native int mouseCursorCreateExt(long[] outCursor);

    /**
     * cna_mouse_cursor_create_from_texture2d (input_cursor.h).
     */
    public static native int mouseCursorCreateFromTexture2d(long game, long texture, int originX, int originY, long[] outCursor);

    /**
     * cna_mouse_cursor_destroy (input_cursor.h).
     */
    public static native int mouseCursorDestroy(long cursor);

    /**
     * cna_mouse_cursor_dispose (input_cursor.h).
     */
    public static native int mouseCursorDispose(long cursor);

    /**
     * cna_mouse_cursor_get_stock_ext (input_cursor.h).
     */
    public static native int mouseCursorGetStockExt(long game, int stock, long[] outCursor);

    /**
     * cna_mouse_set_cursor_ext (input_cursor.h).
     */
    public static native int mouseSetCursorExt(long game, long cursor);

    /**
     * cna_text_input_is_active_ext (input_text.h).
     */
    public static native int textInputIsActiveExt(long game, boolean[] outActive);

    /**
     * cna_text_input_is_screen_keyboard_shown_ext (input_text.h).
     */
    public static native int textInputIsScreenKeyboardShownExt(long game, boolean[] outShown);

    /**
     * cna_text_input_set_input_rectangle_ext (input_text.h).
     */
    public static native int textInputSetInputRectangleExt(long game, long[] rectangleIntegral);

    /**
     * cna_text_input_start_ext (input_text.h).
     */
    public static native int textInputStartExt(long game);

    /**
     * cna_text_input_start_with_type_ext (input_text.h).
     */
    public static native int textInputStartWithTypeExt(long game, int type);

    /**
     * cna_text_input_stop_ext (input_text.h).
     */
    public static native int textInputStopExt(long game);
}
