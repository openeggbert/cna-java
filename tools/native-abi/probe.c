// SPDX-License-Identifier: MS-PL

#include <CNA/C/cna.h>

#include <stddef.h>
#include <stdint.h>

_Static_assert(sizeof(CNA_Result) == 4U, "CNA_Result must be uint32_t");
_Static_assert(sizeof(CNA_Bool) == 1U, "CNA_Bool must be uint8_t");
_Static_assert(sizeof(CNA_Handle) == 8U, "CNA_Handle must be uint64_t");
_Static_assert(sizeof(CNA_Color) == 4U, "CNA_Color must be four bytes");
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
static CNA_Result (*const game_exit_function)(CNA_Handle) = cna_game_request_exit;
static CNA_Result (*const game_destroy_function)(CNA_Handle) = cna_game_destroy;
static CNA_Result (*const game_clear_function)(CNA_Handle, CNA_Color) = cna_game_clear;
static CNA_Result (*const game_set_mouse_function)(CNA_Handle, CNA_Bool) = cna_game_set_is_mouse_visible;
static CNA_Result (*const game_get_mouse_function)(CNA_Handle, CNA_Bool*) = cna_game_get_is_mouse_visible;

int cna_java_abi_probe(void)
{
    return get_abi_version_function != NULL && error_size_function != NULL && error_copy_function != NULL &&
        game_create_function != NULL && game_hooks_function != NULL && game_run_function != NULL &&
        game_exit_function != NULL && game_destroy_function != NULL && game_clear_function != NULL &&
        game_set_mouse_function != NULL && game_get_mouse_function != NULL ? 0 : 1;
}

