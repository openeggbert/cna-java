package org.openeggbert.cna.internal.generated;

/**
 * Generated CNA C ABI declarations for NativeGamerServicesRoutes.
 *
 * <p>Produced by {@code tools/native-abi/generate_jni.py} from the live CNA C headers.
 * Do not edit: every signature here is the header's own declaration, and regenerating
 * is how a change upstream reaches Java. This class is not application API.
 */
public final class NativeGamerServicesRoutes {

    private NativeGamerServicesRoutes() {
    }

    /** cna_achievement_collection_add (gamer_services.h) */
    public static native int achievementCollectionAdd(long collection, long achievement);

    /** cna_achievement_collection_clear (gamer_services.h) */
    public static native int achievementCollectionClear(long collection);

    /** cna_achievement_collection_contains (gamer_services.h) */
    public static native int achievementCollectionContains(long collection, long achievement, boolean[] outContains);

    /** cna_achievement_collection_copy_to (gamer_services.h) */
    public static native int achievementCollectionCopyTo(long collection, long[] destination, int index, long[] outCount);

    /** cna_achievement_collection_create_ext (gamer_services.h) */
    public static native int achievementCollectionCreateExt(long[] achievements, long[] outCollection);

    /** cna_achievement_collection_destroy (gamer_services.h) */
    public static native int achievementCollectionDestroy(long collection);

    /** cna_achievement_collection_get_at (gamer_services.h) */
    public static native int achievementCollectionGetAt(long collection, int index, long[] outAchievement);

    /** cna_achievement_collection_get_by_key (gamer_services.h) */
    public static native int achievementCollectionGetByKey(long collection, byte[] key, long[] outAchievement);

    /** cna_achievement_collection_get_count (gamer_services.h) */
    public static native int achievementCollectionGetCount(long collection, int[] outCount);

    /** cna_achievement_collection_get_is_disposed (gamer_services.h) */
    public static native int achievementCollectionGetIsDisposed(long collection, boolean[] outIsDisposed);

    /** cna_achievement_collection_get_is_read_only (gamer_services.h) */
    public static native int achievementCollectionGetIsReadOnly(long collection, boolean[] outIsReadOnly);

    /** cna_achievement_collection_index_of (gamer_services.h) */
    public static native int achievementCollectionIndexOf(long collection, long achievement, int[] outIndex);

    /** cna_achievement_collection_insert (gamer_services.h) */
    public static native int achievementCollectionInsert(long collection, int index, long achievement);

    /** cna_achievement_collection_remove (gamer_services.h) */
    public static native int achievementCollectionRemove(long collection, long achievement, boolean[] outRemoved);

    /** cna_achievement_collection_remove_at (gamer_services.h) */
    public static native int achievementCollectionRemoveAt(long collection, int index);

    /** cna_achievement_copy_description (gamer_services.h) */
    public static native int achievementCopyDescription(long achievement, byte[] destination, long[] outBytes);

    /** cna_achievement_copy_how_to_earn (gamer_services.h) */
    public static native int achievementCopyHowToEarn(long achievement, byte[] destination, long[] outBytes);

    /** cna_achievement_copy_key (gamer_services.h) */
    public static native int achievementCopyKey(long achievement, byte[] destination, long[] outBytes);

    /** cna_achievement_copy_name (gamer_services.h) */
    public static native int achievementCopyName(long achievement, byte[] destination, long[] outBytes);

    /** cna_achievement_create_ext (gamer_services.h) */
    public static native int achievementCreateExt(byte[] key, byte[] name, byte[] description, boolean displayBeforeEarned, boolean isEarned, long earnedDateTimeTicks, long[] outAchievement);

    /** cna_achievement_destroy (gamer_services.h) */
    public static native int achievementDestroy(long achievement);

    /** cna_achievement_equals (gamer_services.h) */
    public static native int achievementEquals(long achievement, long other, boolean[] outEquals);

    /** cna_achievement_get_description_size (gamer_services.h) */
    public static native int achievementGetDescriptionSize(long achievement, long[] outBytes);

    /** cna_achievement_get_how_to_earn_size (gamer_services.h) */
    public static native int achievementGetHowToEarnSize(long achievement, long[] outBytes);

    /** cna_achievement_get_info (gamer_services.h) */
    public static native int achievementGetInfo(long achievement, long[] outInfoIntegral);

    /** cna_achievement_get_key_size (gamer_services.h) */
    public static native int achievementGetKeySize(long achievement, long[] outBytes);

    /** cna_achievement_get_name_size (gamer_services.h) */
    public static native int achievementGetNameSize(long achievement, long[] outBytes);

    /** cna_achievement_get_picture_size (gamer_services.h) */
    public static native int achievementGetPictureSize(long achievement, long[] outBytes);

    /** cna_avatar_animation_copy_real_clip_name_ext (gamer_services.h) */
    public static native int avatarAnimationCopyRealClipNameExt(long animation, byte[] destination, long[] outBytes);

    /** cna_avatar_animation_create (gamer_services.h) */
    public static native int avatarAnimationCreate(int preset, long[] outAnimation);

    /** cna_avatar_animation_destroy (gamer_services.h) */
    public static native int avatarAnimationDestroy(long animation);

    /** cna_avatar_animation_get_bone_transform_at (gamer_services.h) */
    public static native int avatarAnimationGetBoneTransformAt(long animation, int index, float[] outTransformFloating);

    /** cna_avatar_animation_get_expression (gamer_services.h) */
    public static native int avatarAnimationGetExpression(long animation, long[] outExpressionIntegral);

    /** cna_avatar_animation_get_info (gamer_services.h) */
    public static native int avatarAnimationGetInfo(long animation, byte[] outInfoBytes, long[] outInfoIntegral);

    /** cna_avatar_animation_get_real_clip_name_size_ext (gamer_services.h) */
    public static native int avatarAnimationGetRealClipNameSizeExt(long animation, long[] outBytes);

    /** cna_avatar_animation_preset_copy_clip_name_ext (gamer_services.h) */
    public static native int avatarAnimationPresetCopyClipNameExt(int preset, byte[] destination, long[] outBytes);

    /** cna_avatar_animation_preset_get_clip_name_size_ext (gamer_services.h) */
    public static native int avatarAnimationPresetGetClipNameSizeExt(int preset, long[] outBytes);

    /** cna_avatar_animation_set_current_position (gamer_services.h) */
    public static native int avatarAnimationSetCurrentPosition(long animation, long positionTicks);

    /** cna_avatar_animation_set_real_clip_name_ext (gamer_services.h) */
    public static native int avatarAnimationSetRealClipNameExt(long animation, byte[] clipName);

    /** cna_avatar_animation_update (gamer_services.h) */
    public static native int avatarAnimationUpdate(long animation, long elapsedTicks, boolean loop);

    /** cna_avatar_appearance_init_ext (gamer_services.h) */
    public static native int avatarAppearanceInitExt(long[] outAppearanceIntegral);

    /** cna_avatar_body_type_copy_content_name_ext (gamer_services.h) */
    public static native int avatarBodyTypeCopyContentNameExt(int bodyType, byte[] destination, long[] outBytes);

    /** cna_avatar_body_type_get_content_name_size_ext (gamer_services.h) */
    public static native int avatarBodyTypeGetContentNameSizeExt(int bodyType, long[] outBytes);

    /** cna_avatar_description_copy_description (gamer_services.h) */
    public static native int avatarDescriptionCopyDescription(long description, byte[] destination, long[] outBytes);

    /** cna_avatar_description_create (gamer_services.h) */
    public static native int avatarDescriptionCreate(byte[] description, long[] outDescription);

    /** cna_avatar_description_create_random (gamer_services.h) */
    public static native int avatarDescriptionCreateRandom(long[] outDescription);

    /** cna_avatar_description_create_random_for_body_type (gamer_services.h) */
    public static native int avatarDescriptionCreateRandomForBodyType(int bodyType, long[] outDescription);

    /** cna_avatar_description_destroy (gamer_services.h) */
    public static native int avatarDescriptionDestroy(long description);

    /** cna_avatar_description_get_info (gamer_services.h) */
    public static native int avatarDescriptionGetInfo(long description, byte[] outInfoBytes, long[] outInfoIntegral, float[] outInfoFloating);

    /** cna_avatar_expression_init (gamer_services.h) */
    public static native int avatarExpressionInit(long[] outExpressionIntegral);

    /** cna_avatar_renderer_create (gamer_services.h) */
    public static native int avatarRendererCreate(long description, boolean useLoadingEffect, long[] outRenderer);

    /** cna_avatar_renderer_destroy (gamer_services.h) */
    public static native int avatarRendererDestroy(long renderer);

    /** cna_avatar_renderer_draw_animation (gamer_services.h) */
    public static native int avatarRendererDrawAnimation(long renderer, long animation);

    /** cna_avatar_renderer_draw_bones (gamer_services.h) */
    public static native int avatarRendererDrawBones(long renderer, float[] bonesFloating, long boneCount, long[] expressionIntegral);

    /** cna_avatar_renderer_draw_real_ext (gamer_services.h) */
    public static native int avatarRendererDrawRealExt(long renderer, byte[] animationClipName, long positionTicks, boolean loop);

    /** cna_avatar_renderer_enable_real_rendering_ext (gamer_services.h) */
    public static native int avatarRendererEnableRealRenderingExt(long renderer, long device, long model);

    /** cna_avatar_renderer_get_bind_pose_at (gamer_services.h) */
    public static native int avatarRendererGetBindPoseAt(long renderer, int index, float[] outTransformFloating);

    /** cna_avatar_renderer_get_info (gamer_services.h) */
    public static native int avatarRendererGetInfo(long renderer, byte[] outInfoBytes, long[] outInfoIntegral);

    /** cna_avatar_renderer_get_lighting (gamer_services.h) */
    public static native int avatarRendererGetLighting(long renderer, float[] outLightColorFloating, float[] outLightDirectionFloating, float[] outAmbientLightColorFloating);

    /** cna_avatar_renderer_get_parent_bone_at (gamer_services.h) */
    public static native int avatarRendererGetParentBoneAt(long renderer, int index, int[] outParentIndex);

    /** cna_avatar_renderer_get_transforms (gamer_services.h) */
    public static native int avatarRendererGetTransforms(long renderer, float[] outWorldFloating, float[] outViewFloating, float[] outProjectionFloating);

    /** cna_avatar_renderer_set_appearance_ext (gamer_services.h) */
    public static native int avatarRendererSetAppearanceExt(long renderer, long[] appearanceIntegral);

    /** cna_avatar_renderer_set_lighting (gamer_services.h) */
    public static native int avatarRendererSetLighting(long renderer, float[] lightColorFloating, float[] lightDirectionFloating, float[] ambientLightColorFloating);

    /** cna_avatar_renderer_set_transforms (gamer_services.h) */
    public static native int avatarRendererSetTransforms(long renderer, float[] worldFloating, float[] viewFloating, float[] projectionFloating);

    /** cna_friend_collection_create_ext (gamer_services.h) */
    public static native int friendCollectionCreateExt(long[] friends, long[] outCollection);

    /** cna_friend_collection_get_is_disposed (gamer_services.h) */
    public static native int friendCollectionGetIsDisposed(long collection, boolean[] outIsDisposed);

    /** cna_friend_gamer_copy_presence (gamer_services.h) */
    public static native int friendGamerCopyPresence(long gamer, byte[] destination, long[] outBytes);

    /** cna_friend_gamer_create_ext (gamer_services.h) */
    public static native int friendGamerCreateExt(byte[] gamertag, byte[] displayName, boolean isOnline, boolean isPlaying, boolean isAway, boolean isBusy, boolean friendRequestSentTo, boolean friendRequestReceivedFrom, long[] outGamer);

    /** cna_friend_gamer_get_info (gamer_services.h) */
    public static native int friendGamerGetInfo(long gamer, byte[] outInfoBytes, long[] outInfoIntegral);

    /** cna_friend_gamer_get_presence_size (gamer_services.h) */
    public static native int friendGamerGetPresenceSize(long gamer, long[] outBytes);

    /** cna_game_defaults_init (gamer_services.h) */
    public static native int gameDefaultsInit(byte[] outDefaultsBytes, long[] outDefaultsIntegral);

    /** cna_gamer_collection_add (gamer_services.h) */
    public static native int gamerCollectionAdd(long collection, long gamer);

    /** cna_gamer_collection_clear (gamer_services.h) */
    public static native int gamerCollectionClear(long collection);

    /** cna_gamer_collection_contains (gamer_services.h) */
    public static native int gamerCollectionContains(long collection, long gamer, boolean[] outContains);

    /** cna_gamer_collection_copy_to (gamer_services.h) */
    public static native int gamerCollectionCopyTo(long collection, long[] destination, int index, long[] outCount);

    /** cna_gamer_collection_create_enumerator (gamer_services.h) */
    public static native int gamerCollectionCreateEnumerator(long collection, long[] outEnumerator);

    /** cna_gamer_collection_destroy (gamer_services.h) */
    public static native int gamerCollectionDestroy(long collection);

    /** cna_gamer_collection_get_at (gamer_services.h) */
    public static native int gamerCollectionGetAt(long collection, int index, long[] outGamer);

    /** cna_gamer_collection_get_count (gamer_services.h) */
    public static native int gamerCollectionGetCount(long collection, int[] outCount);

    /** cna_gamer_collection_index_of (gamer_services.h) */
    public static native int gamerCollectionIndexOf(long collection, long gamer, int[] outIndex);

    /** cna_gamer_collection_remove (gamer_services.h) */
    public static native int gamerCollectionRemove(long collection, long gamer);

    /** cna_gamer_copy_display_name (gamer_services.h) */
    public static native int gamerCopyDisplayName(long gamer, byte[] destination, long[] outBytes);

    /** cna_gamer_copy_gamertag (gamer_services.h) */
    public static native int gamerCopyGamertag(long gamer, byte[] destination, long[] outBytes);

    /** cna_gamer_copy_partner_token (gamer_services.h) */
    public static native int gamerCopyPartnerToken(byte[] audienceUri, byte[] destination, long[] outBytes);

    /** cna_gamer_copy_text (gamer_services.h) */
    public static native int gamerCopyText(long gamer, byte[] destination, long[] outBytes);

    /** cna_gamer_destroy (gamer_services.h) */
    public static native int gamerDestroy(long gamer);

    /** cna_gamer_enumerator_destroy (gamer_services.h) */
    public static native int gamerEnumeratorDestroy(long enumerator);

    /** cna_gamer_enumerator_get_current (gamer_services.h) */
    public static native int gamerEnumeratorGetCurrent(long enumerator, long[] outGamer);

    /** cna_gamer_enumerator_move_next (gamer_services.h) */
    public static native int gamerEnumeratorMoveNext(long enumerator, boolean[] outHasCurrent);

    /** cna_gamer_enumerator_reset (gamer_services.h) */
    public static native int gamerEnumeratorReset(long enumerator);

    /** cna_gamer_get_display_name_size (gamer_services.h) */
    public static native int gamerGetDisplayNameSize(long gamer, long[] outBytes);

    /** cna_gamer_get_from_gamertag (gamer_services.h) */
    public static native int gamerGetFromGamertag(byte[] gamertag, long[] outGamer);

    /** cna_gamer_get_gamertag_size (gamer_services.h) */
    public static native int gamerGetGamertagSize(long gamer, long[] outBytes);

    /** cna_gamer_get_is_disposed (gamer_services.h) */
    public static native int gamerGetIsDisposed(long gamer, boolean[] outIsDisposed);

    /** cna_gamer_get_partner_token_size (gamer_services.h) */
    public static native int gamerGetPartnerTokenSize(byte[] audienceUri, long[] outBytes);

    /** cna_gamer_get_profile (gamer_services.h) */
    public static native int gamerGetProfile(long gamer, long[] outProfile);

    /** cna_gamer_get_signed_in_gamer_at (gamer_services.h) */
    public static native int gamerGetSignedInGamerAt(int index, long[] outGamer);

    /** cna_gamer_get_signed_in_gamer_at_player_index (gamer_services.h) */
    public static native int gamerGetSignedInGamerAtPlayerIndex(int playerIndex, boolean[] outHasGamer, long[] outGamer);

    /** cna_gamer_get_signed_in_gamer_count (gamer_services.h) */
    public static native int gamerGetSignedInGamerCount(int[] outCount);

    /** cna_gamer_get_tag (gamer_services.h) */
    public static native int gamerGetTag(long gamer, long[] outTag);

    /** cna_gamer_get_text_size (gamer_services.h) */
    public static native int gamerGetTextSize(long gamer, long[] outBytes);

    /** cna_gamer_presence_init (gamer_services.h) */
    public static native int gamerPresenceInit(long[] outPresenceIntegral);

    /** cna_gamer_profile_copy_motto (gamer_services.h) */
    public static native int gamerProfileCopyMotto(long profile, byte[] destination, long[] outBytes);

    /** cna_gamer_profile_copy_region_name (gamer_services.h) */
    public static native int gamerProfileCopyRegionName(long profile, byte[] destination, long[] outBytes);

    /** cna_gamer_profile_destroy (gamer_services.h) */
    public static native int gamerProfileDestroy(long profile);

    /** cna_gamer_profile_get_info (gamer_services.h) */
    public static native int gamerProfileGetInfo(long profile, byte[] outInfoBytes, long[] outInfoIntegral, float[] outInfoFloating);

    /** cna_gamer_profile_get_motto_size (gamer_services.h) */
    public static native int gamerProfileGetMottoSize(long profile, long[] outBytes);

    /** cna_gamer_profile_get_picture_size (gamer_services.h) */
    public static native int gamerProfileGetPictureSize(long profile, boolean[] outHasPicture, long[] outBytes);

    /** cna_gamer_profile_get_region_name_size (gamer_services.h) */
    public static native int gamerProfileGetRegionNameSize(long profile, long[] outBytes);

    /** cna_gamer_services_component_create (gamer_services.h) */
    public static native int gamerServicesComponentCreate(long game, long[] outComponent);

    /** cna_gamer_services_dispatcher_get_freed_gamer_count_ext (gamer_services.h) */
    public static native int gamerServicesDispatcherGetFreedGamerCountExt(long[] outCount);

    /** cna_gamer_services_dispatcher_get_is_initialized (gamer_services.h) */
    public static native int gamerServicesDispatcherGetIsInitialized(boolean[] outIsInitialized);

    /** cna_gamer_services_dispatcher_get_window_handle (gamer_services.h) */
    public static native int gamerServicesDispatcherGetWindowHandle(long[] outWindowHandle);

    /** cna_gamer_services_dispatcher_update_async (gamer_services.h) */
    public static native int gamerServicesDispatcherUpdateAsync(boolean[] outDidWork);

    /** cna_gamer_set_display_name (gamer_services.h) */
    public static native int gamerSetDisplayName(long gamer, byte[] displayName);

    /** cna_gamer_set_signed_in_gamers_ext (gamer_services.h) */
    public static native int gamerSetSignedInGamersExt(long[] gamers);

    /** cna_gamer_set_tag (gamer_services.h) */
    public static native int gamerSetTag(long gamer, long tag);

    /** cna_gamer_signed_in_contains (gamer_services.h) */
    public static native int gamerSignedInContains(long gamer, boolean[] outContains);

    /** cna_gamer_signed_in_index_of (gamer_services.h) */
    public static native int gamerSignedInIndexOf(long gamer, int[] outIndex);

    /** cna_gamer_unsubscribe_ext (gamer_services.h) */
    public static native int gamerUnsubscribeExt(long registration);

    /** cna_guide_copy_pending_keyboard_input_description_ext (gamer_services.h) */
    public static native int guideCopyPendingKeyboardInputDescriptionExt(byte[] destination, long[] outBytes);

    /** cna_guide_copy_pending_keyboard_input_display_text_ext (gamer_services.h) */
    public static native int guideCopyPendingKeyboardInputDisplayTextExt(byte[] destination, long[] outBytes);

    /** cna_guide_copy_pending_keyboard_input_title_ext (gamer_services.h) */
    public static native int guideCopyPendingKeyboardInputTitleExt(byte[] destination, long[] outBytes);

    /** cna_guide_delay_notifications (gamer_services.h) */
    public static native int guideDelayNotifications(long delayTicks);

    /** cna_guide_end_show_keyboard_input (gamer_services.h) */
    public static native int guideEndShowKeyboardInput(byte[] destination, long[] outBytes);

    /** cna_guide_end_show_keyboard_input_size (gamer_services.h) */
    public static native int guideEndShowKeyboardInputSize(long[] outBytes);

    /** cna_guide_end_show_message_box (gamer_services.h) */
    public static native int guideEndShowMessageBox(boolean[] outHasChoice, int[] outButtonIndex);

    /** cna_guide_get_has_pending_keyboard_input_ext (gamer_services.h) */
    public static native int guideGetHasPendingKeyboardInputExt(boolean[] outHasPending);

    /** cna_guide_get_has_pending_message_box_ext (gamer_services.h) */
    public static native int guideGetHasPendingMessageBoxExt(boolean[] outHasPending);

    /** cna_guide_get_is_screen_saver_enabled (gamer_services.h) */
    public static native int guideGetIsScreenSaverEnabled(boolean[] outIsEnabled);

    /** cna_guide_get_is_trial_mode (gamer_services.h) */
    public static native int guideGetIsTrialMode(boolean[] outIsTrialMode);

    /** cna_guide_get_is_visible (gamer_services.h) */
    public static native int guideGetIsVisible(boolean[] outIsVisible);

    /** cna_guide_get_notification_position (gamer_services.h) */
    public static native int guideGetNotificationPosition(int[] outPosition);

    /** cna_guide_get_pending_keyboard_input_description_size_ext (gamer_services.h) */
    public static native int guideGetPendingKeyboardInputDescriptionSizeExt(long[] outBytes);

    /** cna_guide_get_pending_keyboard_input_display_text_size_ext (gamer_services.h) */
    public static native int guideGetPendingKeyboardInputDisplayTextSizeExt(long[] outBytes);

    /** cna_guide_get_pending_keyboard_input_title_size_ext (gamer_services.h) */
    public static native int guideGetPendingKeyboardInputTitleSizeExt(long[] outBytes);

    /** cna_guide_get_pending_message_box_focus_button_ext (gamer_services.h) */
    public static native int guideGetPendingMessageBoxFocusButtonExt(int[] outFocusButton);

    /** cna_guide_get_simulate_trial_mode (gamer_services.h) */
    public static native int guideGetSimulateTrialMode(boolean[] outSimulate);

    /** cna_guide_render_pending_keyboard_input_ext (gamer_services.h) */
    public static native int guideRenderPendingKeyboardInputExt(long device, long spriteBatch, long font, long whitePixel);

    /** cna_guide_render_pending_message_box_ext (gamer_services.h) */
    public static native int guideRenderPendingMessageBoxExt(long device, long spriteBatch, long font, long whitePixel);

    /** cna_guide_reset_pending_keyboard_input_ext (gamer_services.h) */
    public static native int guideResetPendingKeyboardInputExt();

    /** cna_guide_reset_pending_message_box_ext (gamer_services.h) */
    public static native int guideResetPendingMessageBoxExt();

    /** cna_guide_set_is_screen_saver_enabled (gamer_services.h) */
    public static native int guideSetIsScreenSaverEnabled(boolean isEnabled);

    /** cna_guide_set_is_trial_mode (gamer_services.h) */
    public static native int guideSetIsTrialMode(boolean isTrialMode);

    /** cna_guide_set_is_visible (gamer_services.h) */
    public static native int guideSetIsVisible(boolean isVisible);

    /** cna_guide_set_notification_position (gamer_services.h) */
    public static native int guideSetNotificationPosition(int position);

    /** cna_guide_set_simulate_trial_mode (gamer_services.h) */
    public static native int guideSetSimulateTrialMode(boolean simulate);

    /** cna_guide_show_achievements_ext (gamer_services.h) */
    public static native int guideShowAchievementsExt(int player);

    /** cna_guide_show_compose_message (gamer_services.h) */
    public static native int guideShowComposeMessage(int player, byte[] text, long[] recipients);

    /** cna_guide_show_friend_request (gamer_services.h) */
    public static native int guideShowFriendRequest(int player, long gamer);

    /** cna_guide_show_friends (gamer_services.h) */
    public static native int guideShowFriends(int player);

    /** cna_guide_show_game_invite (gamer_services.h) */
    public static native int guideShowGameInvite(int player, long[] recipients);

    /** cna_guide_show_game_invite_for_session (gamer_services.h) */
    public static native int guideShowGameInviteForSession(byte[] sessionId);

    /** cna_guide_show_gamer_card (gamer_services.h) */
    public static native int guideShowGamerCard(int player, long gamer);

    /** cna_guide_show_marketplace (gamer_services.h) */
    public static native int guideShowMarketplace(int player);

    /** cna_guide_show_messages (gamer_services.h) */
    public static native int guideShowMessages(int player);

    /** cna_guide_show_party (gamer_services.h) */
    public static native int guideShowParty(int player);

    /** cna_guide_show_party_sessions (gamer_services.h) */
    public static native int guideShowPartySessions(int player);

    /** cna_guide_show_player_review (gamer_services.h) */
    public static native int guideShowPlayerReview(int player, long gamer);

    /** cna_guide_show_players (gamer_services.h) */
    public static native int guideShowPlayers(int player);

    /** cna_guide_show_sign_in (gamer_services.h) */
    public static native int guideShowSignIn(int paneCount, boolean onlineOnly);

    /** cna_guide_simulate_keyboard_input_cancel_ext (gamer_services.h) */
    public static native int guideSimulateKeyboardInputCancelExt();

    /** cna_guide_simulate_message_box_click_ext (gamer_services.h) */
    public static native int guideSimulateMessageBoxClickExt(int buttonIndex);

    /** cna_guide_was_keyboard_input_canceled_ext (gamer_services.h) */
    public static native int guideWasKeyboardInputCanceledExt(boolean[] outWasCanceled);

    /** cna_invite_accepted_event_info_init (gamer_services.h) */
    public static native int inviteAcceptedEventInfoInit(long gamer, boolean isCurrentSession, byte[] outInfoBytes, long[] outInfoIntegral);

    /** cna_leaderboard_entry_create_ext (gamer_services.h) */
    public static native int leaderboardEntryCreateExt(long gamer, long rating, int ranking, long[] outEntry);

    /** cna_leaderboard_entry_destroy (gamer_services.h) */
    public static native int leaderboardEntryDestroy(long entry);

    /** cna_leaderboard_entry_equals (gamer_services.h) */
    public static native int leaderboardEntryEquals(long entry, long other, boolean[] outEquals);

    /** cna_leaderboard_entry_get_columns (gamer_services.h) */
    public static native int leaderboardEntryGetColumns(long entry, long[] outColumns);

    /** cna_leaderboard_entry_get_gamer (gamer_services.h) */
    public static native int leaderboardEntryGetGamer(long entry, boolean[] outHasGamer, long[] outGamer);

    /** cna_leaderboard_entry_get_info (gamer_services.h) */
    public static native int leaderboardEntryGetInfo(long entry, byte[] outInfoBytes, long[] outInfoIntegral);

    /** cna_leaderboard_entry_set_rating (gamer_services.h) */
    public static native int leaderboardEntrySetRating(long entry, long rating);

    /** cna_leaderboard_identity_init (gamer_services.h) */
    public static native int leaderboardIdentityInit(int key, int gameMode, byte[] outIdentityBytes, long[] outIdentityIntegral);

    /** cna_leaderboard_reader_destroy (gamer_services.h) */
    public static native int leaderboardReaderDestroy(long reader);

    /** cna_leaderboard_reader_get_entry_at (gamer_services.h) */
    public static native int leaderboardReaderGetEntryAt(long reader, int index, long[] outEntry);

    /** cna_leaderboard_reader_get_identity (gamer_services.h) */
    public static native int leaderboardReaderGetIdentity(long reader, byte[] outIdentityBytes, long[] outIdentityIntegral);

    /** cna_leaderboard_reader_get_info (gamer_services.h) */
    public static native int leaderboardReaderGetInfo(long reader, long[] outInfoIntegral);

    /** cna_leaderboard_reader_page_down (gamer_services.h) */
    public static native int leaderboardReaderPageDown(long reader);

    /** cna_leaderboard_reader_page_up (gamer_services.h) */
    public static native int leaderboardReaderPageUp(long reader);

    /** cna_leaderboard_reader_read (gamer_services.h) */
    public static native int leaderboardReaderRead(byte[] identityBytes, long[] identityIntegral, int pageStart, int pageSize, long[] outReader);

    /** cna_leaderboard_reader_read_from_gamers (gamer_services.h) */
    public static native int leaderboardReaderReadFromGamers(byte[] identityBytes, long[] identityIntegral, long[] gamers, long pivotGamer, int pageSize, long[] outReader);

    /** cna_leaderboard_reader_read_from_pivot (gamer_services.h) */
    public static native int leaderboardReaderReadFromPivot(byte[] identityBytes, long[] identityIntegral, long pivotGamer, int pageSize, long[] outReader);

    /** cna_property_dictionary_clear (gamer_services.h) */
    public static native int propertyDictionaryClear(long dictionary);

    /** cna_property_dictionary_contains_key (gamer_services.h) */
    public static native int propertyDictionaryContainsKey(long dictionary, byte[] key, boolean[] outContains);

    /** cna_property_dictionary_copy_key_at (gamer_services.h) */
    public static native int propertyDictionaryCopyKeyAt(long dictionary, int index, byte[] destination, long[] outBytes);

    /** cna_property_dictionary_copy_string (gamer_services.h) */
    public static native int propertyDictionaryCopyString(long dictionary, byte[] key, byte[] destination, long[] outBytes);

    /** cna_property_dictionary_create_ext (gamer_services.h) */
    public static native int propertyDictionaryCreateExt(long[] outDictionary);

    /** cna_property_dictionary_destroy (gamer_services.h) */
    public static native int propertyDictionaryDestroy(long dictionary);

    /** cna_property_dictionary_get_count (gamer_services.h) */
    public static native int propertyDictionaryGetCount(long dictionary, int[] outCount);

    /** cna_property_dictionary_get_date_time_ticks (gamer_services.h) */
    public static native int propertyDictionaryGetDateTimeTicks(long dictionary, byte[] key, long[] outTicks);

    /** cna_property_dictionary_get_double (gamer_services.h) */
    public static native int propertyDictionaryGetDouble(long dictionary, byte[] key, double[] outValue);

    /** cna_property_dictionary_get_int32 (gamer_services.h) */
    public static native int propertyDictionaryGetInt32(long dictionary, byte[] key, int[] outValue);

    /** cna_property_dictionary_get_int64 (gamer_services.h) */
    public static native int propertyDictionaryGetInt64(long dictionary, byte[] key, long[] outValue);

    /** cna_property_dictionary_get_is_read_only (gamer_services.h) */
    public static native int propertyDictionaryGetIsReadOnly(long dictionary, boolean[] outIsReadOnly);

    /** cna_property_dictionary_get_key_size_at (gamer_services.h) */
    public static native int propertyDictionaryGetKeySizeAt(long dictionary, int index, long[] outBytes);

    /** cna_property_dictionary_get_outcome (gamer_services.h) */
    public static native int propertyDictionaryGetOutcome(long dictionary, byte[] key, int[] outOutcome);

    /** cna_property_dictionary_get_single (gamer_services.h) */
    public static native int propertyDictionaryGetSingle(long dictionary, byte[] key, float[] outValue);

    /** cna_property_dictionary_get_stream_size_ext (gamer_services.h) */
    public static native int propertyDictionaryGetStreamSizeExt(long dictionary, byte[] key, boolean[] outHasStream, long[] outBytes);

    /** cna_property_dictionary_get_string_size (gamer_services.h) */
    public static native int propertyDictionaryGetStringSize(long dictionary, byte[] key, long[] outBytes);

    /** cna_property_dictionary_get_time_span_ticks (gamer_services.h) */
    public static native int propertyDictionaryGetTimeSpanTicks(long dictionary, byte[] key, long[] outTicks);

    /** cna_property_dictionary_remove (gamer_services.h) */
    public static native int propertyDictionaryRemove(long dictionary, byte[] key, boolean[] outRemoved);

    /** cna_property_dictionary_set_date_time_ticks (gamer_services.h) */
    public static native int propertyDictionarySetDateTimeTicks(long dictionary, byte[] key, long ticks);

    /** cna_property_dictionary_set_double (gamer_services.h) */
    public static native int propertyDictionarySetDouble(long dictionary, byte[] key, double value);

    /** cna_property_dictionary_set_int32 (gamer_services.h) */
    public static native int propertyDictionarySetInt32(long dictionary, byte[] key, int value);

    /** cna_property_dictionary_set_int64 (gamer_services.h) */
    public static native int propertyDictionarySetInt64(long dictionary, byte[] key, long value);

    /** cna_property_dictionary_set_outcome (gamer_services.h) */
    public static native int propertyDictionarySetOutcome(long dictionary, byte[] key, int outcome);

    /** cna_property_dictionary_set_single (gamer_services.h) */
    public static native int propertyDictionarySetSingle(long dictionary, byte[] key, float value);

    /** cna_property_dictionary_set_string (gamer_services.h) */
    public static native int propertyDictionarySetString(long dictionary, byte[] key, byte[] value);

    /** cna_property_dictionary_set_time_span_ticks (gamer_services.h) */
    public static native int propertyDictionarySetTimeSpanTicks(long dictionary, byte[] key, long ticks);

    /** cna_property_dictionary_try_get_value_kind_ext (gamer_services.h) */
    public static native int propertyDictionaryTryGetValueKindExt(long dictionary, byte[] key, boolean[] outFound, int[] outKind);

    /** cna_signed_in_gamer_award_achievement (gamer_services.h) */
    public static native int signedInGamerAwardAchievement(long gamer, byte[] achievementKey);

    /** cna_signed_in_gamer_copy_gamertag (gamer_services.h) */
    public static native int signedInGamerCopyGamertag(long gamer, byte[] destination, long[] outBytes);

    /** cna_signed_in_gamer_create_ext (gamer_services.h) */
    public static native int signedInGamerCreateExt(byte[] gamertag, boolean isSignedInToLive, boolean isGuest, int playerIndex, long[] outGamer);

    /** cna_signed_in_gamer_destroy (gamer_services.h) */
    public static native int signedInGamerDestroy(long gamer);

    /** cna_signed_in_gamer_get_achievements (gamer_services.h) */
    public static native int signedInGamerGetAchievements(long gamer, long[] outAchievements);

    /** cna_signed_in_gamer_get_friends (gamer_services.h) */
    public static native int signedInGamerGetFriends(long gamer, long[] outFriends);

    /** cna_signed_in_gamer_get_game_defaults (gamer_services.h) */
    public static native int signedInGamerGetGameDefaults(long gamer, byte[] outDefaultsBytes, long[] outDefaultsIntegral);

    /** cna_signed_in_gamer_get_gamertag_size (gamer_services.h) */
    public static native int signedInGamerGetGamertagSize(long gamer, long[] outBytes);

    /** cna_signed_in_gamer_get_is_guest (gamer_services.h) */
    public static native int signedInGamerGetIsGuest(long gamer, boolean[] outIsGuest);

    /** cna_signed_in_gamer_get_is_signed_in_to_live (gamer_services.h) */
    public static native int signedInGamerGetIsSignedInToLive(long gamer, boolean[] outIsSignedInToLive);

    /** cna_signed_in_gamer_get_party_size (gamer_services.h) */
    public static native int signedInGamerGetPartySize(long gamer, int[] outPartySize);

    /** cna_signed_in_gamer_get_player_index (gamer_services.h) */
    public static native int signedInGamerGetPlayerIndex(long gamer, int[] outPlayerIndex);

    /** cna_signed_in_gamer_get_presence (gamer_services.h) */
    public static native int signedInGamerGetPresence(long gamer, long[] outPresenceIntegral);

    /** cna_signed_in_gamer_get_privileges (gamer_services.h) */
    public static native int signedInGamerGetPrivileges(long gamer, byte[] outPrivilegesBytes, long[] outPrivilegesIntegral);

    /** cna_signed_in_gamer_is_friend (gamer_services.h) */
    public static native int signedInGamerIsFriend(long gamer, long other, boolean[] outIsFriend);

    /** cna_signed_in_gamer_is_headset (gamer_services.h) */
    public static native int signedInGamerIsHeadset(long gamer, long microphoneIndex, boolean[] outIsHeadset);

    /** cna_signed_in_gamer_set_party_size (gamer_services.h) */
    public static native int signedInGamerSetPartySize(long gamer, int partySize);

    /** cna_signed_in_gamer_set_presence (gamer_services.h) */
    public static native int signedInGamerSetPresence(long gamer, long[] presenceIntegral);

    /** cna_signed_in_gamer_set_presence_mode_string_ext (gamer_services.h) */
    public static native int signedInGamerSetPresenceModeStringExt(long gamer, byte[] mode);
}
