// SPDX-License-Identifier: MS-PL

#include <CNA/C/cna.h>

#include <stddef.h>
#include <stdint.h>

_Static_assert(sizeof(CNA_Result) == 4U, "CNA_Result must be uint32_t");
_Static_assert(sizeof(CNA_Bool) == 1U, "CNA_Bool must be uint8_t");
_Static_assert(sizeof(CNA_Handle) == 8U, "CNA_Handle must be uint64_t");
_Static_assert(sizeof(CNA_Color) == 4U, "CNA_Color must be four bytes");
_Static_assert(sizeof(CNA_DisplayOrientation) == 4U, "CNA_DisplayOrientation must be uint32_t");
_Static_assert(sizeof(CNA_Rectangle) == 16U, "CNA_Rectangle must be four int32 values");
_Static_assert(offsetof(CNA_Rectangle, x) == 0U, "CNA_Rectangle.x offset");
_Static_assert(offsetof(CNA_Rectangle, y) == 4U, "CNA_Rectangle.y offset");
_Static_assert(offsetof(CNA_Rectangle, width) == 8U, "CNA_Rectangle.width offset");
_Static_assert(offsetof(CNA_Rectangle, height) == 12U, "CNA_Rectangle.height offset");
_Static_assert(sizeof(CNA_KeyboardState) == 40U, "CNA_KeyboardState layout changed");
_Static_assert(offsetof(CNA_KeyboardState, struct_size) == 0U, "CNA_KeyboardState.size offset");
_Static_assert(offsetof(CNA_KeyboardState, struct_version) == 4U, "CNA_KeyboardState.version offset");
_Static_assert(offsetof(CNA_KeyboardState, pressed_key_words) == 8U, "CNA_KeyboardState.words offset");
_Static_assert(sizeof(CNA_MouseState) == 32U, "CNA_MouseState layout changed");
_Static_assert(offsetof(CNA_MouseState, x) == 8U, "CNA_MouseState.x offset");
_Static_assert(offsetof(CNA_MouseState, y) == 12U, "CNA_MouseState.y offset");
_Static_assert(offsetof(CNA_MouseState, scroll_wheel) == 16U, "CNA_MouseState.wheel offset");
_Static_assert(offsetof(CNA_MouseState, pressed_buttons) == 24U, "CNA_MouseState.buttons offset");
_Static_assert(sizeof(CNA_GameTime) == 24U, "CNA_GameTime layout changed");
_Static_assert(offsetof(CNA_GameTime, total_game_time_ticks) == 0U, "CNA_GameTime.total offset");
_Static_assert(offsetof(CNA_GameTime, elapsed_game_time_ticks) == 8U, "CNA_GameTime.elapsed offset");
_Static_assert(offsetof(CNA_GameTime, is_running_slowly) == 16U, "CNA_GameTime.bool offset");
_Static_assert(offsetof(CNA_StringView, data) == 0U, "CNA_StringView.data offset");
_Static_assert(offsetof(CNA_StringView, byte_length) >= sizeof(void*), "CNA_StringView.length offset");
_Static_assert(CNA_ABI_VERSION == CNA_ABI_VERSION_ENCODE(0, 7, 0), "unexpected CNA header ABI");

static uint32_t (*const get_abi_version_function)(void) = cna_get_abi_version;
static CNA_Result (*const error_size_function)(uint64_t*) = cna_error_get_last_message_size;
static CNA_Result (*const error_copy_function)(char*, uint64_t, uint64_t*) = cna_error_copy_last_message;
static CNA_Result (*const game_create_function)(const CNA_GameCreateInfo*, CNA_Handle*) = cna_game_create;
static CNA_Result (*const game_hooks_function)(CNA_Handle, const CNA_GameFrameHooks*) = cna_game_set_frame_hooks_ext;
static CNA_Result (*const game_run_function)(CNA_Handle) = cna_game_run;
static CNA_Result (*const game_run_one_frame_function)(CNA_Handle) = cna_game_run_one_frame;
static CNA_Result (*const game_exit_function)(CNA_Handle) = cna_game_request_exit;
static CNA_Result (*const game_reset_elapsed_time_function)(CNA_Handle) = cna_game_reset_elapsed_time;
static CNA_Result (*const game_suppress_draw_function)(CNA_Handle) = cna_game_suppress_draw;
static CNA_Result (*const game_tick_function)(CNA_Handle) = cna_game_tick;
static CNA_Result (*const game_destroy_function)(CNA_Handle) = cna_game_destroy;
static CNA_Result (*const game_clear_function)(CNA_Handle, CNA_Color) = cna_game_clear;
static CNA_Result (*const game_set_mouse_function)(CNA_Handle, CNA_Bool) = cna_game_set_is_mouse_visible;
static CNA_Result (*const game_get_mouse_function)(CNA_Handle, CNA_Bool*) = cna_game_get_is_mouse_visible;
static CNA_Result (*const game_get_active_function)(CNA_Handle, CNA_Bool*) = cna_game_get_is_active;
static CNA_Result (*const game_set_fixed_function)(CNA_Handle, CNA_Bool) = cna_game_set_is_fixed_time_step;
static CNA_Result (*const game_get_fixed_function)(CNA_Handle, CNA_Bool*) = cna_game_get_is_fixed_time_step;
static CNA_Result (*const game_set_target_time_function)(CNA_Handle, int64_t) =
    cna_game_set_target_elapsed_time_ticks;
static CNA_Result (*const game_get_target_time_function)(CNA_Handle, int64_t*) =
    cna_game_get_target_elapsed_time_ticks;
static CNA_Result (*const game_set_inactive_time_function)(CNA_Handle, int64_t) =
    cna_game_set_inactive_sleep_time_ticks;
static CNA_Result (*const game_get_inactive_time_function)(CNA_Handle, int64_t*) =
    cna_game_get_inactive_sleep_time_ticks;
static CNA_Result (*const window_get_resizing_function)(CNA_Handle, CNA_Bool*) =
    cna_game_window_get_allow_user_resizing;
static CNA_Result (*const window_set_resizing_function)(CNA_Handle, CNA_Bool) =
    cna_game_window_set_allow_user_resizing;
static CNA_Result (*const window_get_bounds_function)(CNA_Handle, CNA_Rectangle*) =
    cna_game_window_get_client_bounds;
static CNA_Result (*const window_get_orientation_function)(CNA_Handle, CNA_DisplayOrientation*) =
    cna_game_window_get_current_orientation;
static CNA_Result (*const window_get_handle_function)(CNA_Handle, uint64_t*) =
    cna_game_window_get_native_handle_ext;
static CNA_Result (*const window_get_screen_size_function)(CNA_Handle, uint64_t*) =
    cna_game_window_get_screen_device_name_size;
static CNA_Result (*const window_copy_screen_function)(CNA_Handle, char*, uint64_t, uint64_t*) =
    cna_game_window_copy_screen_device_name;
static CNA_Result (*const window_set_title_function)(CNA_Handle, CNA_StringView) =
    cna_game_set_window_title;
static CNA_Result (*const window_begin_change_function)(CNA_Handle, CNA_Bool) =
    cna_game_window_begin_screen_device_change;
static CNA_Result (*const window_end_change_function)(CNA_Handle, CNA_StringView, int32_t, int32_t) =
    cna_game_window_end_screen_device_change;
static CNA_Result (*const keyboard_get_state_function)(CNA_Handle, CNA_KeyboardState*) =
    cna_keyboard_get_state;
static CNA_Result (*const keyboard_get_state_for_player_function)(
    CNA_Handle, CNA_PlayerIndex, CNA_KeyboardState*) = cna_keyboard_get_state_for_player;
static CNA_Result (*const mouse_get_state_function)(CNA_Handle, CNA_MouseState*) =
    cna_mouse_get_state;
static CNA_Result (*const mouse_set_position_function)(CNA_Handle, int32_t, int32_t) =
    cna_mouse_set_position;
static CNA_Result (*const mouse_get_window_handle_function)(CNA_Handle, uint64_t*) =
    cna_mouse_get_window_handle;
static CNA_Result (*const mouse_set_window_handle_function)(CNA_Handle, uint64_t) =
    cna_mouse_set_window_handle;

int cna_java_abi_probe(void)
{
    return get_abi_version_function != NULL && error_size_function != NULL && error_copy_function != NULL &&
        game_create_function != NULL && game_hooks_function != NULL && game_run_function != NULL &&
        game_run_one_frame_function != NULL && game_exit_function != NULL &&
        game_reset_elapsed_time_function != NULL && game_suppress_draw_function != NULL &&
        game_tick_function != NULL && game_destroy_function != NULL && game_clear_function != NULL &&
        game_set_mouse_function != NULL && game_get_mouse_function != NULL &&
        game_get_active_function != NULL && game_set_fixed_function != NULL &&
        game_get_fixed_function != NULL && game_set_target_time_function != NULL &&
        game_get_target_time_function != NULL && game_set_inactive_time_function != NULL &&
        game_get_inactive_time_function != NULL && window_get_resizing_function != NULL &&
        window_set_resizing_function != NULL && window_get_bounds_function != NULL &&
        window_get_orientation_function != NULL && window_get_handle_function != NULL &&
        window_get_screen_size_function != NULL && window_copy_screen_function != NULL &&
        window_set_title_function != NULL && window_begin_change_function != NULL &&
        window_end_change_function != NULL && keyboard_get_state_function != NULL &&
        keyboard_get_state_for_player_function != NULL && mouse_get_state_function != NULL &&
        mouse_set_position_function != NULL && mouse_get_window_handle_function != NULL &&
        mouse_set_window_handle_function != NULL ? 0 : 1;
}
