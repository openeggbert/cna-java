package org.openeggbert.cna.internal.generated;

/**
 * Generated CNA C ABI declarations for NativeNetworkRoutes.
 *
 * <p>Produced by {@code tools/native-abi/generate_jni.py} from the live CNA C headers.
 * Do not edit: every signature here is the header's own declaration, and regenerating
 * is how a change upstream reaches Java. This class is not application API.
 */
public final class NativeNetworkRoutes {

    private NativeNetworkRoutes() {
    }

    /**
     * cna_available_network_session_collection_copy_session (net_sessions.h).
     */
    public static native int availableNetworkSessionCollectionCopySession(long collection, int index, long[] outSession);

    /**
     * cna_available_network_session_collection_create_ext (net_sessions.h).
     */
    public static native int availableNetworkSessionCollectionCreateExt(long[] sessions, long[] outCollection);

    /**
     * cna_available_network_session_collection_destroy (net_sessions.h).
     */
    public static native int availableNetworkSessionCollectionDestroy(long collection);

    /**
     * cna_available_network_session_collection_dispose (net_sessions.h).
     */
    public static native int availableNetworkSessionCollectionDispose(long collection);

    /**
     * cna_available_network_session_collection_get_count (net_sessions.h).
     */
    public static native int availableNetworkSessionCollectionGetCount(long collection, int[] outCount);

    /**
     * cna_available_network_session_collection_get_is_disposed (net_sessions.h).
     */
    public static native int availableNetworkSessionCollectionGetIsDisposed(long collection, boolean[] outIsDisposed);

    /**
     * cna_available_network_session_copy_connect_address_ext (net_sessions.h).
     */
    public static native int availableNetworkSessionCopyConnectAddressExt(long session, byte[] destination, long[] outBytes);

    /**
     * cna_available_network_session_copy_host_gamertag (net_sessions.h).
     */
    public static native int availableNetworkSessionCopyHostGamertag(long session, byte[] destination, long[] outBytes);

    /**
     * cna_available_network_session_copy_session_properties (net_sessions.h).
     */
    public static native int availableNetworkSessionCopySessionProperties(long session, long[] outProperties);

    /**
     * cna_available_network_session_destroy (net_sessions.h).
     */
    public static native int availableNetworkSessionDestroy(long session);

    /**
     * cna_available_network_session_equals (net_sessions.h).
     */
    public static native int availableNetworkSessionEquals(long left, long right, boolean[] outEqual);

    /**
     * cna_available_network_session_get_connect_address_size_ext (net_sessions.h).
     */
    public static native int availableNetworkSessionGetConnectAddressSizeExt(long session, long[] outBytes);

    /**
     * cna_available_network_session_get_connect_port_ext (net_sessions.h).
     */
    public static native int availableNetworkSessionGetConnectPortExt(long session, int[] outValue);

    /**
     * cna_available_network_session_get_current_gamer_count (net_sessions.h).
     */
    public static native int availableNetworkSessionGetCurrentGamerCount(long session, int[] outValue);

    /**
     * cna_available_network_session_get_host_gamertag_size (net_sessions.h).
     */
    public static native int availableNetworkSessionGetHostGamertagSize(long session, long[] outBytes);

    /**
     * cna_available_network_session_get_open_private_gamer_slots (net_sessions.h).
     */
    public static native int availableNetworkSessionGetOpenPrivateGamerSlots(long session, int[] outValue);

    /**
     * cna_available_network_session_get_open_public_gamer_slots (net_sessions.h).
     */
    public static native int availableNetworkSessionGetOpenPublicGamerSlots(long session, int[] outValue);

    /**
     * cna_available_network_session_get_quality_of_service (net_sessions.h).
     *
     * <p>outValueBytes carries CNA_QualityOfService in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     *   <li>{@code reserved[3]} (uint8_t)</li>
     *   <li>{@code reserved[4]} (uint8_t)</li>
     *   <li>{@code reserved[5]} (uint8_t)</li>
     *   <li>{@code reserved[6]} (uint8_t)</li>
     * </ol>
     *
     * <p>outValueIntegral carries CNA_QualityOfService in this order:
     * <ol start="0">
     *   <li>{@code is_available} (CNA_Bool)</li>
     *   <li>{@code average_roundtrip_ticks} (int64_t)</li>
     *   <li>{@code minimum_roundtrip_ticks} (int64_t)</li>
     *   <li>{@code bytes_per_second_downstream} (int32_t)</li>
     *   <li>{@code bytes_per_second_upstream} (int32_t)</li>
     * </ol>
     */
    public static native int availableNetworkSessionGetQualityOfService(long session, byte[] outValueBytes, long[] outValueIntegral);

    /**
     * cna_available_network_session_get_session_type_ext (net_sessions.h).
     */
    public static native int availableNetworkSessionGetSessionTypeExt(long session, int[] outValue);

    /**
     * cna_available_network_session_not_equals (net_sessions.h).
     */
    public static native int availableNetworkSessionNotEquals(long left, long right, boolean[] outNotEqual);

    /**
     * cna_game_ended_event_info_init (net_gamers.h).
     */
    public static native int gameEndedEventInfoInit();

    /**
     * cna_game_started_event_info_init (net_gamers.h).
     */
    public static native int gameStartedEventInfoInit();

    /**
     * cna_gamer_joined_event_info_init (net_gamers.h).
     *
     * <p>outInfoIntegral carries CNA_GamerJoinedEventInfo in this order:
     * <ol start="0">
     *   <li>{@code gamer} (CNA_NetworkGamerHandle)</li>
     * </ol>
     */
    public static native int gamerJoinedEventInfoInit(long gamer, long[] outInfoIntegral);

    /**
     * cna_gamer_left_event_info_init (net_gamers.h).
     *
     * <p>outInfoIntegral carries CNA_GamerLeftEventInfo in this order:
     * <ol start="0">
     *   <li>{@code gamer} (CNA_NetworkGamerHandle)</li>
     * </ol>
     */
    public static native int gamerLeftEventInfoInit(long gamer, long[] outInfoIntegral);

    /**
     * cna_host_changed_event_info_init (net_gamers.h).
     *
     * <p>outInfoIntegral carries CNA_HostChangedEventInfo in this order:
     * <ol start="0">
     *   <li>{@code old_host} (CNA_NetworkGamerHandle)</li>
     *   <li>{@code new_host} (CNA_NetworkGamerHandle)</li>
     * </ol>
     */
    public static native int hostChangedEventInfoInit(long oldHost, long newHost, long[] outInfoIntegral);

    /**
     * cna_local_network_gamer_clear_packet_queue_ext (net_sessions.h).
     */
    public static native int localNetworkGamerClearPacketQueueExt(long gamer);

    /**
     * cna_local_network_gamer_create_ext (net_sessions.h).
     */
    public static native int localNetworkGamerCreateExt(long signedInGamer, long session, long[] outGamer);

    /**
     * cna_local_network_gamer_enable_send_voice (net_sessions.h).
     */
    public static native int localNetworkGamerEnableSendVoice(long gamer, long remoteGamer, boolean enable);

    /**
     * cna_local_network_gamer_get_is_data_available (net_sessions.h).
     */
    public static native int localNetworkGamerGetIsDataAvailable(long gamer, boolean[] outValue);

    /**
     * cna_local_network_gamer_get_signed_in_gamer (net_sessions.h).
     */
    public static native int localNetworkGamerGetSignedInGamer(long gamer, long[] outSignedInGamer);

    /**
     * cna_local_network_gamer_receive_data (net_sessions.h).
     */
    public static native int localNetworkGamerReceiveData(long gamer, byte[] destination, long[] outSender, long[] outReceived);

    /**
     * cna_local_network_gamer_receive_data_at (net_sessions.h).
     */
    public static native int localNetworkGamerReceiveDataAt(long gamer, byte[] destination, int offset, long[] outSender, long[] outReceived);

    /**
     * cna_local_network_gamer_receive_data_into_packet_reader (net_sessions.h).
     */
    public static native int localNetworkGamerReceiveDataIntoPacketReader(long gamer, long reader, long[] outSender, long[] outReceived);

    /**
     * cna_local_network_gamer_send_data (net_sessions.h).
     */
    public static native int localNetworkGamerSendData(long gamer, byte[] data, int options);

    /**
     * cna_local_network_gamer_send_data_range (net_sessions.h).
     */
    public static native int localNetworkGamerSendDataRange(long gamer, byte[] data, int offset, int length, int options);

    /**
     * cna_local_network_gamer_send_data_range_to (net_sessions.h).
     */
    public static native int localNetworkGamerSendDataRangeTo(long gamer, byte[] data, int offset, int length, int options, long recipient);

    /**
     * cna_local_network_gamer_send_data_to (net_sessions.h).
     */
    public static native int localNetworkGamerSendDataTo(long gamer, byte[] data, int options, long recipient);

    /**
     * cna_local_network_gamer_send_packet_writer (net_sessions.h).
     */
    public static native int localNetworkGamerSendPacketWriter(long gamer, long writer, int options);

    /**
     * cna_local_network_gamer_send_packet_writer_to (net_sessions.h).
     */
    public static native int localNetworkGamerSendPacketWriterTo(long gamer, long writer, int options, long recipient);

    /**
     * cna_local_network_gamer_send_party_invites (net_sessions.h).
     */
    public static native int localNetworkGamerSendPartyInvites(long gamer);

    /**
     * cna_net_get_last_join_error (net.h).
     */
    public static native int netGetLastJoinError(int[] outJoinError, boolean[] outHasJoinError);

    /**
     * cna_network_gamer_copy_machine (net_gamers.h).
     */
    public static native int networkGamerCopyMachine(long gamer, long[] outMachine);

    /**
     * cna_network_gamer_create (net_gamers.h).
     */
    public static native int networkGamerCreate(long session, byte[] gamertag, long[] outGamer);

    /**
     * cna_network_gamer_destroy (net_gamers.h).
     */
    public static native int networkGamerDestroy(long gamer);

    /**
     * cna_network_gamer_get_has_left_session (net_gamers.h).
     */
    public static native int networkGamerGetHasLeftSession(long gamer, boolean[] outValue);

    /**
     * cna_network_gamer_get_has_voice (net_gamers.h).
     */
    public static native int networkGamerGetHasVoice(long gamer, boolean[] outValue);

    /**
     * cna_network_gamer_get_id (net_gamers.h).
     */
    public static native int networkGamerGetId(long gamer, byte[] outValue);

    /**
     * cna_network_gamer_get_is_guest (net_gamers.h).
     */
    public static native int networkGamerGetIsGuest(long gamer, boolean[] outValue);

    /**
     * cna_network_gamer_get_is_host (net_gamers.h).
     */
    public static native int networkGamerGetIsHost(long gamer, boolean[] outValue);

    /**
     * cna_network_gamer_get_is_local (net_gamers.h).
     */
    public static native int networkGamerGetIsLocal(long gamer, boolean[] outValue);

    /**
     * cna_network_gamer_get_is_muted_by_local_user (net_gamers.h).
     */
    public static native int networkGamerGetIsMutedByLocalUser(long gamer, boolean[] outValue);

    /**
     * cna_network_gamer_get_is_private_slot (net_gamers.h).
     */
    public static native int networkGamerGetIsPrivateSlot(long gamer, boolean[] outValue);

    /**
     * cna_network_gamer_get_is_ready (net_gamers.h).
     */
    public static native int networkGamerGetIsReady(long gamer, boolean[] outValue);

    /**
     * cna_network_gamer_get_is_talking (net_gamers.h).
     */
    public static native int networkGamerGetIsTalking(long gamer, boolean[] outValue);

    /**
     * cna_network_gamer_get_roundtrip_ticks (net_gamers.h).
     */
    public static native int networkGamerGetRoundtripTicks(long gamer, long[] outTicks);

    /**
     * cna_network_gamer_get_session (net_gamers.h).
     */
    public static native int networkGamerGetSession(long gamer, long[] outSession);

    /**
     * cna_network_gamer_set_has_left_session_ext (net_gamers.h).
     */
    public static native int networkGamerSetHasLeftSessionExt(long gamer, boolean value);

    /**
     * cna_network_gamer_set_id_ext (net_gamers.h).
     */
    public static native int networkGamerSetIdExt(long gamer, byte value);

    /**
     * cna_network_gamer_set_is_host_ext (net_gamers.h).
     */
    public static native int networkGamerSetIsHostExt(long gamer, boolean value);

    /**
     * cna_network_gamer_set_is_ready (net_gamers.h).
     */
    public static native int networkGamerSetIsReady(long gamer, boolean value);

    /**
     * cna_network_gamer_set_machine (net_gamers.h).
     */
    public static native int networkGamerSetMachine(long gamer, long machine);

    /**
     * cna_network_gamer_set_roundtrip_ticks_ext (net_gamers.h).
     */
    public static native int networkGamerSetRoundtripTicksExt(long gamer, long ticks);

    /**
     * cna_network_machine_create (net_gamers.h).
     */
    public static native int networkMachineCreate(long[] outMachine);

    /**
     * cna_network_machine_destroy (net_gamers.h).
     */
    public static native int networkMachineDestroy(long machine);

    /**
     * cna_network_machine_get_gamer (net_gamers.h).
     */
    public static native int networkMachineGetGamer(long machine, int index, long[] outGamer);

    /**
     * cna_network_machine_get_gamer_count (net_gamers.h).
     */
    public static native int networkMachineGetGamerCount(long machine, int[] outCount);

    /**
     * cna_network_machine_remove_from_session (net_gamers.h).
     */
    public static native int networkMachineRemoveFromSession(long machine);

    /**
     * cna_network_session_add_local_gamer (net_sessions.h).
     */
    public static native int networkSessionAddLocalGamer(long session, long signedInGamer);

    /**
     * cna_network_session_add_remote_gamer_ext (net_sessions.h).
     */
    public static native int networkSessionAddRemoteGamerExt(long session, long gamer);

    /**
     * cna_network_session_copy_session_properties (net_sessions.h).
     */
    public static native int networkSessionCopySessionProperties(long session, long[] outProperties);

    /**
     * cna_network_session_copy_type_name (net_sessions.h).
     */
    public static native int networkSessionCopyTypeName(long session, byte[] destination, long[] outBytes);

    /**
     * cna_network_session_create (net_sessions.h).
     */
    public static native int networkSessionCreate(int sessionType, int maxLocalGamers, int maxGamers, long[] outSession);

    /**
     * cna_network_session_create_with_local_gamers (net_sessions.h).
     */
    public static native int networkSessionCreateWithLocalGamers(int sessionType, long[] localGamers, int maxGamers, int privateGamerSlots, long sessionProperties, long[] outSession);

    /**
     * cna_network_session_create_with_properties (net_sessions.h).
     */
    public static native int networkSessionCreateWithProperties(int sessionType, int maxLocalGamers, int maxGamers, int privateGamerSlots, long sessionProperties, long[] outSession);

    /**
     * cna_network_session_destroy (net_sessions.h).
     */
    public static native int networkSessionDestroy(long session);

    /**
     * cna_network_session_dispose (net_sessions.h).
     */
    public static native int networkSessionDispose(long session);

    /**
     * cna_network_session_end_game (net_sessions.h).
     */
    public static native int networkSessionEndGame(long session);

    /**
     * cna_network_session_ended_event_info_init (net_gamers.h).
     *
     * <p>outInfoBytes carries CNA_NetworkSessionEndedEventInfo in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     *   <li>{@code reserved[3]} (uint8_t)</li>
     * </ol>
     *
     * <p>outInfoIntegral carries CNA_NetworkSessionEndedEventInfo in this order:
     * <ol start="0">
     *   <li>{@code end_reason} (CNA_NetworkSessionEndReason)</li>
     * </ol>
     */
    public static native int networkSessionEndedEventInfoInit(int endReason, byte[] outInfoBytes, long[] outInfoIntegral);

    /**
     * cna_network_session_find (net_sessions.h).
     */
    public static native int networkSessionFind(int sessionType, int maxLocalGamers, long searchProperties, long[] outCollection);

    /**
     * cna_network_session_find_gamer_by_id (net_sessions.h).
     */
    public static native int networkSessionFindGamerById(long session, byte gamerId, long[] outGamer);

    /**
     * cna_network_session_find_with_local_gamers (net_sessions.h).
     */
    public static native int networkSessionFindWithLocalGamers(int sessionType, long[] localGamers, long searchProperties, long[] outCollection);

    /**
     * cna_network_session_get_active_action_count_ext (net_sessions.h).
     */
    public static native int networkSessionGetActiveActionCountExt(int[] outCount);

    /**
     * cna_network_session_get_allow_host_migration (net_sessions.h).
     */
    public static native int networkSessionGetAllowHostMigration(long session, boolean[] outValue);

    /**
     * cna_network_session_get_allow_join_in_progress (net_sessions.h).
     */
    public static native int networkSessionGetAllowJoinInProgress(long session, boolean[] outValue);

    /**
     * cna_network_session_get_bytes_per_second_received (net_sessions.h).
     */
    public static native int networkSessionGetBytesPerSecondReceived(long session, int[] outValue);

    /**
     * cna_network_session_get_bytes_per_second_sent (net_sessions.h).
     */
    public static native int networkSessionGetBytesPerSecondSent(long session, int[] outValue);

    /**
     * cna_network_session_get_gamer (net_sessions.h).
     */
    public static native int networkSessionGetGamer(long session, int roster, int index, long[] outGamer);

    /**
     * cna_network_session_get_gamer_count (net_sessions.h).
     */
    public static native int networkSessionGetGamerCount(long session, int roster, int[] outCount);

    /**
     * cna_network_session_get_host (net_sessions.h).
     */
    public static native int networkSessionGetHost(long session, long[] outGamer);

    /**
     * cna_network_session_get_instance_count_ext (net_sessions.h).
     */
    public static native int networkSessionGetInstanceCountExt(int[] outCount);

    /**
     * cna_network_session_get_is_disposed (net_sessions.h).
     */
    public static native int networkSessionGetIsDisposed(long session, boolean[] outIsDisposed);

    /**
     * cna_network_session_get_is_everyone_ready (net_sessions.h).
     */
    public static native int networkSessionGetIsEveryoneReady(long session, boolean[] outValue);

    /**
     * cna_network_session_get_is_host (net_sessions.h).
     */
    public static native int networkSessionGetIsHost(long session, boolean[] outValue);

    /**
     * cna_network_session_get_max_gamers (net_sessions.h).
     */
    public static native int networkSessionGetMaxGamers(long session, int[] outValue);

    /**
     * cna_network_session_get_owned_gamer_count_ext (net_sessions.h).
     */
    public static native int networkSessionGetOwnedGamerCountExt(long session, long[] outCount);

    /**
     * cna_network_session_get_private_gamer_slots (net_sessions.h).
     */
    public static native int networkSessionGetPrivateGamerSlots(long session, int[] outValue);

    /**
     * cna_network_session_get_session_state (net_sessions.h).
     */
    public static native int networkSessionGetSessionState(long session, int[] outValue);

    /**
     * cna_network_session_get_session_type (net_sessions.h).
     */
    public static native int networkSessionGetSessionType(long session, int[] outValue);

    /**
     * cna_network_session_get_simulated_latency_ticks (net_sessions.h).
     */
    public static native int networkSessionGetSimulatedLatencyTicks(long session, long[] outTicks);

    /**
     * cna_network_session_get_simulated_packet_loss (net_sessions.h).
     */
    public static native int networkSessionGetSimulatedPacketLoss(long session, float[] outValue);

    /**
     * cna_network_session_get_type_name_size (net_sessions.h).
     */
    public static native int networkSessionGetTypeNameSize(long session, long[] outBytes);

    /**
     * cna_network_session_join (net_sessions.h).
     */
    public static native int networkSessionJoin(long availableSession, long[] outSession);

    /**
     * cna_network_session_join_invited (net_sessions.h).
     */
    public static native int networkSessionJoinInvited(int maxLocalGamers, long[] outSession);

    /**
     * cna_network_session_join_invited_with_local_gamers (net_sessions.h).
     */
    public static native int networkSessionJoinInvitedWithLocalGamers(long[] localGamers, long[] outSession);

    /**
     * cna_network_session_properties_add (net.h).
     */
    public static native int networkSessionPropertiesAdd(long properties, byte[] valueBytes, long[] valueIntegral);

    /**
     * cna_network_session_properties_clear (net.h).
     */
    public static native int networkSessionPropertiesClear(long properties);

    /**
     * cna_network_session_properties_contains (net.h).
     */
    public static native int networkSessionPropertiesContains(long properties, byte[] valueBytes, long[] valueIntegral, boolean[] outContains);

    /**
     * cna_network_session_properties_copy_to (net.h).
     */
    public static native int networkSessionPropertiesCopyTo(long properties, byte[] destinationBytes, long[] destinationIntegral, int index, long[] outCount);

    /**
     * cna_network_session_properties_create (net.h).
     */
    public static native int networkSessionPropertiesCreate(long[] outProperties);

    /**
     * cna_network_session_properties_create_enumerator (net.h).
     */
    public static native int networkSessionPropertiesCreateEnumerator(long properties, long[] outEnumerator);

    /**
     * cna_network_session_properties_destroy (net.h).
     */
    public static native int networkSessionPropertiesDestroy(long properties);

    /**
     * cna_network_session_properties_get_count (net.h).
     */
    public static native int networkSessionPropertiesGetCount(long properties, int[] outCount);

    /**
     * cna_network_session_properties_get_is_read_only (net.h).
     */
    public static native int networkSessionPropertiesGetIsReadOnly(long properties, boolean[] outIsReadOnly);

    /**
     * cna_network_session_properties_get_item (net.h).
     *
     * <p>outValueBytes carries CNA_OptionalInt32 in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>outValueIntegral carries CNA_OptionalInt32 in this order:
     * <ol start="0">
     *   <li>{@code has_value} (CNA_Bool)</li>
     *   <li>{@code value} (int32_t)</li>
     * </ol>
     */
    public static native int networkSessionPropertiesGetItem(long properties, int index, byte[] outValueBytes, long[] outValueIntegral);

    /**
     * cna_network_session_properties_index_of (net.h).
     */
    public static native int networkSessionPropertiesIndexOf(long properties, byte[] valueBytes, long[] valueIntegral, int[] outIndex);

    /**
     * cna_network_session_properties_insert (net.h).
     */
    public static native int networkSessionPropertiesInsert(long properties, int index, byte[] valueBytes, long[] valueIntegral);

    /**
     * cna_network_session_properties_remove (net.h).
     */
    public static native int networkSessionPropertiesRemove(long properties, byte[] valueBytes, long[] valueIntegral, boolean[] outRemoved);

    /**
     * cna_network_session_properties_remove_at (net.h).
     */
    public static native int networkSessionPropertiesRemoveAt(long properties, int index);

    /**
     * cna_network_session_properties_set_item (net.h).
     */
    public static native int networkSessionPropertiesSetItem(long properties, int index, byte[] valueBytes, long[] valueIntegral);

    /**
     * cna_network_session_property_enumerator_destroy (net.h).
     */
    public static native int networkSessionPropertyEnumeratorDestroy(long enumerator);

    /**
     * cna_network_session_property_enumerator_get_current (net.h).
     *
     * <p>outValueBytes carries CNA_OptionalInt32 in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     * </ol>
     *
     * <p>outValueIntegral carries CNA_OptionalInt32 in this order:
     * <ol start="0">
     *   <li>{@code has_value} (CNA_Bool)</li>
     *   <li>{@code value} (int32_t)</li>
     * </ol>
     */
    public static native int networkSessionPropertyEnumeratorGetCurrent(long enumerator, byte[] outValueBytes, long[] outValueIntegral);

    /**
     * cna_network_session_property_enumerator_move_next (net.h).
     */
    public static native int networkSessionPropertyEnumeratorMoveNext(long enumerator, boolean[] outHasCurrent);

    /**
     * cna_network_session_property_enumerator_reset (net.h).
     */
    public static native int networkSessionPropertyEnumeratorReset(long enumerator);

    /**
     * cna_network_session_remove_gamer_ext (net_sessions.h).
     */
    public static native int networkSessionRemoveGamerExt(long session, long gamer, int reason);

    /**
     * cna_network_session_reset_ready (net_sessions.h).
     */
    public static native int networkSessionResetReady(long session);

    /**
     * cna_network_session_set_allow_host_migration (net_sessions.h).
     */
    public static native int networkSessionSetAllowHostMigration(long session, boolean value);

    /**
     * cna_network_session_set_allow_join_in_progress (net_sessions.h).
     */
    public static native int networkSessionSetAllowJoinInProgress(long session, boolean value);

    /**
     * cna_network_session_set_max_gamers (net_sessions.h).
     */
    public static native int networkSessionSetMaxGamers(long session, int value);

    /**
     * cna_network_session_set_private_gamer_slots (net_sessions.h).
     */
    public static native int networkSessionSetPrivateGamerSlots(long session, int value);

    /**
     * cna_network_session_set_simulated_latency_ticks (net_sessions.h).
     */
    public static native int networkSessionSetSimulatedLatencyTicks(long session, long ticks);

    /**
     * cna_network_session_set_simulated_packet_loss (net_sessions.h).
     */
    public static native int networkSessionSetSimulatedPacketLoss(long session, float value);

    /**
     * cna_network_session_start_game (net_sessions.h).
     */
    public static native int networkSessionStartGame(long session);

    /**
     * cna_network_session_unsubscribe (net_sessions.h).
     */
    public static native int networkSessionUnsubscribe(long registration);

    /**
     * cna_network_session_update (net_sessions.h).
     */
    public static native int networkSessionUpdate(long session);

    /**
     * cna_packet_reader_create (net.h).
     */
    public static native int packetReaderCreate(int capacity, long[] outReader);

    /**
     * cna_packet_reader_destroy (net.h).
     */
    public static native int packetReaderDestroy(long reader);

    /**
     * cna_packet_reader_get_length (net.h).
     */
    public static native int packetReaderGetLength(long reader, int[] outLength);

    /**
     * cna_packet_reader_get_position (net.h).
     */
    public static native int packetReaderGetPosition(long reader, int[] outPosition);

    /**
     * cna_packet_reader_read_color (net.h).
     *
     * <p>outValueIntegral carries CNA_Color in this order:
     * <ol start="0">
     *   <li>{@code r} (uint8_t)</li>
     *   <li>{@code g} (uint8_t)</li>
     *   <li>{@code b} (uint8_t)</li>
     *   <li>{@code a} (uint8_t)</li>
     * </ol>
     */
    public static native int packetReaderReadColor(long reader, long[] outValueIntegral);

    /**
     * cna_packet_reader_read_double (net.h).
     */
    public static native int packetReaderReadDouble(long reader, double[] outValue);

    /**
     * cna_packet_reader_read_matrix (net.h).
     *
     * <p>outValueFloating carries CNA_Matrix in this order:
     * <ol start="0">
     *   <li>{@code m11} (float)</li>
     *   <li>{@code m12} (float)</li>
     *   <li>{@code m13} (float)</li>
     *   <li>{@code m14} (float)</li>
     *   <li>{@code m21} (float)</li>
     *   <li>{@code m22} (float)</li>
     *   <li>{@code m23} (float)</li>
     *   <li>{@code m24} (float)</li>
     *   <li>{@code m31} (float)</li>
     *   <li>{@code m32} (float)</li>
     *   <li>{@code m33} (float)</li>
     *   <li>{@code m34} (float)</li>
     *   <li>{@code m41} (float)</li>
     *   <li>{@code m42} (float)</li>
     *   <li>{@code m43} (float)</li>
     *   <li>{@code m44} (float)</li>
     * </ol>
     */
    public static native int packetReaderReadMatrix(long reader, float[] outValueFloating);

    /**
     * cna_packet_reader_read_quaternion (net.h).
     *
     * <p>outValueFloating carries CNA_Quaternion in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     *   <li>{@code w} (float)</li>
     * </ol>
     */
    public static native int packetReaderReadQuaternion(long reader, float[] outValueFloating);

    /**
     * cna_packet_reader_read_single (net.h).
     */
    public static native int packetReaderReadSingle(long reader, float[] outValue);

    /**
     * cna_packet_reader_read_vector2 (net.h).
     *
     * <p>outValueFloating carries CNA_Vector2 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     * </ol>
     */
    public static native int packetReaderReadVector2(long reader, float[] outValueFloating);

    /**
     * cna_packet_reader_read_vector3 (net.h).
     *
     * <p>outValueFloating carries CNA_Vector3 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     * </ol>
     */
    public static native int packetReaderReadVector3(long reader, float[] outValueFloating);

    /**
     * cna_packet_reader_read_vector4 (net.h).
     *
     * <p>outValueFloating carries CNA_Vector4 in this order:
     * <ol start="0">
     *   <li>{@code x} (float)</li>
     *   <li>{@code y} (float)</li>
     *   <li>{@code z} (float)</li>
     *   <li>{@code w} (float)</li>
     * </ol>
     */
    public static native int packetReaderReadVector4(long reader, float[] outValueFloating);

    /**
     * cna_packet_reader_set_data_ext (net.h).
     */
    public static native int packetReaderSetDataExt(long reader, byte[] data);

    /**
     * cna_packet_reader_set_position (net.h).
     */
    public static native int packetReaderSetPosition(long reader, int position);

    /**
     * cna_packet_writer_copy_data_ext (net.h).
     */
    public static native int packetWriterCopyDataExt(long writer, byte[] destination, long[] outBytes);

    /**
     * cna_packet_writer_create (net.h).
     */
    public static native int packetWriterCreate(int capacity, long[] outWriter);

    /**
     * cna_packet_writer_destroy (net.h).
     */
    public static native int packetWriterDestroy(long writer);

    /**
     * cna_packet_writer_get_length (net.h).
     */
    public static native int packetWriterGetLength(long writer, int[] outLength);

    /**
     * cna_packet_writer_get_position (net.h).
     */
    public static native int packetWriterGetPosition(long writer, int[] outPosition);

    /**
     * cna_packet_writer_set_position (net.h).
     */
    public static native int packetWriterSetPosition(long writer, int position);

    /**
     * cna_packet_writer_write_color (net.h).
     */
    public static native int packetWriterWriteColor(long writer, long[] valueIntegral);

    /**
     * cna_packet_writer_write_double (net.h).
     */
    public static native int packetWriterWriteDouble(long writer, double value);

    /**
     * cna_packet_writer_write_matrix (net.h).
     */
    public static native int packetWriterWriteMatrix(long writer, float[] valueFloating);

    /**
     * cna_packet_writer_write_quaternion (net.h).
     */
    public static native int packetWriterWriteQuaternion(long writer, float[] valueFloating);

    /**
     * cna_packet_writer_write_single (net.h).
     */
    public static native int packetWriterWriteSingle(long writer, float value);

    /**
     * cna_packet_writer_write_vector2 (net.h).
     */
    public static native int packetWriterWriteVector2(long writer, float[] valueFloating);

    /**
     * cna_packet_writer_write_vector3 (net.h).
     */
    public static native int packetWriterWriteVector3(long writer, float[] valueFloating);

    /**
     * cna_packet_writer_write_vector4 (net.h).
     */
    public static native int packetWriterWriteVector4(long writer, float[] valueFloating);

    /**
     * cna_quality_of_service_init (net.h).
     *
     * <p>outValueBytes carries CNA_QualityOfService in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     *   <li>{@code reserved[3]} (uint8_t)</li>
     *   <li>{@code reserved[4]} (uint8_t)</li>
     *   <li>{@code reserved[5]} (uint8_t)</li>
     *   <li>{@code reserved[6]} (uint8_t)</li>
     * </ol>
     *
     * <p>outValueIntegral carries CNA_QualityOfService in this order:
     * <ol start="0">
     *   <li>{@code is_available} (CNA_Bool)</li>
     *   <li>{@code average_roundtrip_ticks} (int64_t)</li>
     *   <li>{@code minimum_roundtrip_ticks} (int64_t)</li>
     *   <li>{@code bytes_per_second_downstream} (int32_t)</li>
     *   <li>{@code bytes_per_second_upstream} (int32_t)</li>
     * </ol>
     */
    public static native int qualityOfServiceInit(byte[] outValueBytes, long[] outValueIntegral);

    /**
     * cna_quality_of_service_init_measured (net.h).
     *
     * <p>outValueBytes carries CNA_QualityOfService in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     *   <li>{@code reserved[3]} (uint8_t)</li>
     *   <li>{@code reserved[4]} (uint8_t)</li>
     *   <li>{@code reserved[5]} (uint8_t)</li>
     *   <li>{@code reserved[6]} (uint8_t)</li>
     * </ol>
     *
     * <p>outValueIntegral carries CNA_QualityOfService in this order:
     * <ol start="0">
     *   <li>{@code is_available} (CNA_Bool)</li>
     *   <li>{@code average_roundtrip_ticks} (int64_t)</li>
     *   <li>{@code minimum_roundtrip_ticks} (int64_t)</li>
     *   <li>{@code bytes_per_second_downstream} (int32_t)</li>
     *   <li>{@code bytes_per_second_upstream} (int32_t)</li>
     * </ol>
     */
    public static native int qualityOfServiceInitMeasured(long roundtripTicks, byte[] outValueBytes, long[] outValueIntegral);

    /**
     * cna_write_leaderboards_event_info_init (net_gamers.h).
     *
     * <p>outInfoBytes carries CNA_WriteLeaderboardsEventInfo in this order:
     * <ol start="0">
     *   <li>{@code reserved[0]} (uint8_t)</li>
     *   <li>{@code reserved[1]} (uint8_t)</li>
     *   <li>{@code reserved[2]} (uint8_t)</li>
     *   <li>{@code reserved[3]} (uint8_t)</li>
     *   <li>{@code reserved[4]} (uint8_t)</li>
     *   <li>{@code reserved[5]} (uint8_t)</li>
     *   <li>{@code reserved[6]} (uint8_t)</li>
     * </ol>
     *
     * <p>outInfoIntegral carries CNA_WriteLeaderboardsEventInfo in this order:
     * <ol start="0">
     *   <li>{@code gamer} (CNA_NetworkGamerHandle)</li>
     *   <li>{@code is_leaving} (CNA_Bool)</li>
     * </ol>
     */
    public static native int writeLeaderboardsEventInfoInit(long gamer, boolean isLeaving, byte[] outInfoBytes, long[] outInfoIntegral);
}
