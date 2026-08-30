/* SPDX-License-Identifier: MS-PL */
/*
 * Generated JNI adapter for NativeNetworkRoutes.
 *
 * Produced by tools/native-abi/generate_jni.py from the live CNA C headers, and
 * included by cna_java_jni.c so it shares the one dispatch table. Do not edit.
 */

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_availableNetworkSessionCollectionCopySession(
    JNIEnv* environment, jclass type, jlong collection, jint index, jlongArray out_session)
{
    (void)environment;
    (void)type;
    CNA_AvailableNetworkSessionHandle out_session_value = 0;
    CNA_Result result = cna.available_network_session_collection_copy_session((CNA_AvailableNetworkSessionCollectionHandle)collection, (int32_t)index, &out_session_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_session_element = (jlong)out_session_value;
        (*environment)->SetLongArrayRegion(
            environment, out_session, 0, 1, &out_session_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_availableNetworkSessionCollectionCreateExt(
    JNIEnv* environment, jclass type, jlongArray sessions, jlongArray out_collection)
{
    (void)environment;
    (void)type;
    jsize sessions_size = (*environment)->GetArrayLength(environment, sessions);
    jlong* sessions_elements = (*environment)->GetLongArrayElements(environment, sessions, NULL);
    if (sessions_elements == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    CNA_AvailableNetworkSessionHandle* sessions_values = (CNA_AvailableNetworkSessionHandle*)malloc(
        ((size_t)sessions_size + 1U) * sizeof(*sessions_values));
    if (sessions_values == NULL) {
        (*environment)->ReleaseLongArrayElements(
            environment, sessions, sessions_elements, JNI_ABORT);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    for (jsize index = 0; index < sessions_size; ++index) {
        sessions_values[index] = (CNA_AvailableNetworkSessionHandle)sessions_elements[index];
    }
    CNA_AvailableNetworkSessionCollectionHandle out_collection_value = 0;
    CNA_Result result = cna.available_network_session_collection_create_ext(sessions_values, (uint64_t)sessions_size, &out_collection_value);
    free(sessions_values);
    (*environment)->ReleaseLongArrayElements(
        environment, sessions, sessions_elements, JNI_ABORT);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_collection_element = (jlong)out_collection_value;
        (*environment)->SetLongArrayRegion(
            environment, out_collection, 0, 1, &out_collection_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_availableNetworkSessionCollectionDestroy(
    JNIEnv* environment, jclass type, jlong collection)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.available_network_session_collection_destroy((CNA_AvailableNetworkSessionCollectionHandle)collection);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_availableNetworkSessionCollectionDispose(
    JNIEnv* environment, jclass type, jlong collection)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.available_network_session_collection_dispose((CNA_AvailableNetworkSessionCollectionHandle)collection);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_availableNetworkSessionCollectionGetCount(
    JNIEnv* environment, jclass type, jlong collection, jintArray out_count)
{
    (void)environment;
    (void)type;
    int32_t out_count_value = 0;
    CNA_Result result = cna.available_network_session_collection_get_count((CNA_AvailableNetworkSessionCollectionHandle)collection, &out_count_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_count_element = (jint)out_count_value;
        (*environment)->SetIntArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_availableNetworkSessionCollectionGetIsDisposed(
    JNIEnv* environment, jclass type, jlong collection, jbooleanArray out_is_disposed)
{
    (void)environment;
    (void)type;
    CNA_Bool out_is_disposed_value = 0;
    CNA_Result result = cna.available_network_session_collection_get_is_disposed((CNA_AvailableNetworkSessionCollectionHandle)collection, &out_is_disposed_value);
    if (result == CNA_RESULT_SUCCESS) {
        jboolean out_is_disposed_element = (jboolean)out_is_disposed_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_is_disposed, 0, 1, &out_is_disposed_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_availableNetworkSessionCopyConnectAddressExt(
    JNIEnv* environment, jclass type, jlong session, jbyteArray destination, jlongArray out_bytes)
{
    (void)environment;
    (void)type;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t destination_written = 0;
    CNA_Result result = cna.available_network_session_copy_connect_address_ext((CNA_AvailableNetworkSessionHandle)session, (char*)destination_bytes, (uint64_t)destination_size, &destination_written);
    (*environment)->ReleaseByteArrayElements(environment, destination, destination_bytes, 0);
    if (result == CNA_RESULT_SUCCESS) {
        jlong destination_element = (jlong)destination_written;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &destination_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_availableNetworkSessionCopyHostGamertag(
    JNIEnv* environment, jclass type, jlong session, jbyteArray destination, jlongArray out_bytes)
{
    (void)environment;
    (void)type;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t destination_written = 0;
    CNA_Result result = cna.available_network_session_copy_host_gamertag((CNA_AvailableNetworkSessionHandle)session, (char*)destination_bytes, (uint64_t)destination_size, &destination_written);
    (*environment)->ReleaseByteArrayElements(environment, destination, destination_bytes, 0);
    if (result == CNA_RESULT_SUCCESS) {
        jlong destination_element = (jlong)destination_written;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &destination_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_availableNetworkSessionCopySessionProperties(
    JNIEnv* environment, jclass type, jlong session, jlongArray out_properties)
{
    (void)environment;
    (void)type;
    CNA_NetworkSessionPropertiesHandle out_properties_value = 0;
    CNA_Result result = cna.available_network_session_copy_session_properties((CNA_AvailableNetworkSessionHandle)session, &out_properties_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_properties_element = (jlong)out_properties_value;
        (*environment)->SetLongArrayRegion(
            environment, out_properties, 0, 1, &out_properties_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_availableNetworkSessionDestroy(
    JNIEnv* environment, jclass type, jlong session)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.available_network_session_destroy((CNA_AvailableNetworkSessionHandle)session);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_availableNetworkSessionEquals(
    JNIEnv* environment, jclass type, jlong left, jlong right, jbooleanArray out_equal)
{
    (void)environment;
    (void)type;
    CNA_Bool out_equal_value = 0;
    CNA_Result result = cna.available_network_session_equals((CNA_AvailableNetworkSessionHandle)left, (CNA_AvailableNetworkSessionHandle)right, &out_equal_value);
    if (result == CNA_RESULT_SUCCESS) {
        jboolean out_equal_element = (jboolean)out_equal_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_equal, 0, 1, &out_equal_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_availableNetworkSessionGetConnectAddressSizeExt(
    JNIEnv* environment, jclass type, jlong session, jlongArray out_bytes)
{
    (void)environment;
    (void)type;
    uint64_t out_bytes_value = 0;
    CNA_Result result = cna.available_network_session_get_connect_address_size_ext((CNA_AvailableNetworkSessionHandle)session, &out_bytes_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_bytes_element = (jlong)out_bytes_value;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &out_bytes_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_availableNetworkSessionGetConnectPortExt(
    JNIEnv* environment, jclass type, jlong session, jintArray out_value)
{
    (void)environment;
    (void)type;
    uint16_t out_value_value = 0;
    CNA_Result result = cna.available_network_session_get_connect_port_ext((CNA_AvailableNetworkSessionHandle)session, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_value_element = (jint)out_value_value;
        (*environment)->SetIntArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_availableNetworkSessionGetCurrentGamerCount(
    JNIEnv* environment, jclass type, jlong session, jintArray out_value)
{
    (void)environment;
    (void)type;
    int32_t out_value_value = 0;
    CNA_Result result = cna.available_network_session_get_current_gamer_count((CNA_AvailableNetworkSessionHandle)session, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_value_element = (jint)out_value_value;
        (*environment)->SetIntArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_availableNetworkSessionGetHostGamertagSize(
    JNIEnv* environment, jclass type, jlong session, jlongArray out_bytes)
{
    (void)environment;
    (void)type;
    uint64_t out_bytes_value = 0;
    CNA_Result result = cna.available_network_session_get_host_gamertag_size((CNA_AvailableNetworkSessionHandle)session, &out_bytes_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_bytes_element = (jlong)out_bytes_value;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &out_bytes_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_availableNetworkSessionGetOpenPrivateGamerSlots(
    JNIEnv* environment, jclass type, jlong session, jintArray out_value)
{
    (void)environment;
    (void)type;
    int32_t out_value_value = 0;
    CNA_Result result = cna.available_network_session_get_open_private_gamer_slots((CNA_AvailableNetworkSessionHandle)session, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_value_element = (jint)out_value_value;
        (*environment)->SetIntArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_availableNetworkSessionGetOpenPublicGamerSlots(
    JNIEnv* environment, jclass type, jlong session, jintArray out_value)
{
    (void)environment;
    (void)type;
    int32_t out_value_value = 0;
    CNA_Result result = cna.available_network_session_get_open_public_gamer_slots((CNA_AvailableNetworkSessionHandle)session, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_value_element = (jint)out_value_value;
        (*environment)->SetIntArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_availableNetworkSessionGetQualityOfService(
    JNIEnv* environment, jclass type, jlong session, jbyteArray out_valueBytes, jlongArray out_valueIntegral)
{
    (void)environment;
    (void)type;
    CNA_QualityOfService out_value_value;
    memset(&out_value_value, 0, sizeof out_value_value);
    out_value_value.struct_size = (uint32_t)(sizeof out_value_value);
    out_value_value.struct_version = UINT32_C(1);
    CNA_Result result = cna.available_network_session_get_quality_of_service((CNA_AvailableNetworkSessionHandle)session, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jbyte out_valueBytes_values[] = {
            (jbyte)out_value_value.reserved[0],
            (jbyte)out_value_value.reserved[1],
            (jbyte)out_value_value.reserved[2],
            (jbyte)out_value_value.reserved[3],
            (jbyte)out_value_value.reserved[4],
            (jbyte)out_value_value.reserved[5],
            (jbyte)out_value_value.reserved[6]
        };
        (*environment)->SetByteArrayRegion(environment, out_valueBytes, 0,
            (jsize)(sizeof out_valueBytes_values / sizeof out_valueBytes_values[0]),
            out_valueBytes_values);
        jlong out_valueIntegral_values[] = {
            (jlong)out_value_value.is_available,
            (jlong)out_value_value.average_roundtrip_ticks,
            (jlong)out_value_value.minimum_roundtrip_ticks,
            (jlong)out_value_value.bytes_per_second_downstream,
            (jlong)out_value_value.bytes_per_second_upstream
        };
        (*environment)->SetLongArrayRegion(environment, out_valueIntegral, 0,
            (jsize)(sizeof out_valueIntegral_values / sizeof out_valueIntegral_values[0]),
            out_valueIntegral_values);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_availableNetworkSessionGetSessionTypeExt(
    JNIEnv* environment, jclass type, jlong session, jintArray out_value)
{
    (void)environment;
    (void)type;
    CNA_NetworkSessionType out_value_value = 0;
    CNA_Result result = cna.available_network_session_get_session_type_ext((CNA_AvailableNetworkSessionHandle)session, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_value_element = (jint)out_value_value;
        (*environment)->SetIntArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_availableNetworkSessionNotEquals(
    JNIEnv* environment, jclass type, jlong left, jlong right, jbooleanArray out_not_equal)
{
    (void)environment;
    (void)type;
    CNA_Bool out_not_equal_value = 0;
    CNA_Result result = cna.available_network_session_not_equals((CNA_AvailableNetworkSessionHandle)left, (CNA_AvailableNetworkSessionHandle)right, &out_not_equal_value);
    if (result == CNA_RESULT_SUCCESS) {
        jboolean out_not_equal_element = (jboolean)out_not_equal_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_not_equal, 0, 1, &out_not_equal_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_gameEndedEventInfoInit(
    JNIEnv* environment, jclass type)
{
    (void)environment;
    (void)type;
    CNA_GameEndedEventInfo out_info_value;
    memset(&out_info_value, 0, sizeof out_info_value);
    out_info_value.struct_size = (uint32_t)(sizeof out_info_value);
    out_info_value.struct_version = UINT32_C(1);
    CNA_Result result = cna.game_ended_event_info_init(&out_info_value);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_gameStartedEventInfoInit(
    JNIEnv* environment, jclass type)
{
    (void)environment;
    (void)type;
    CNA_GameStartedEventInfo out_info_value;
    memset(&out_info_value, 0, sizeof out_info_value);
    out_info_value.struct_size = (uint32_t)(sizeof out_info_value);
    out_info_value.struct_version = UINT32_C(1);
    CNA_Result result = cna.game_started_event_info_init(&out_info_value);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_gamerJoinedEventInfoInit(
    JNIEnv* environment, jclass type, jlong gamer, jlongArray out_infoIntegral)
{
    (void)environment;
    (void)type;
    CNA_GamerJoinedEventInfo out_info_value;
    memset(&out_info_value, 0, sizeof out_info_value);
    out_info_value.struct_size = (uint32_t)(sizeof out_info_value);
    out_info_value.struct_version = UINT32_C(1);
    CNA_Result result = cna.gamer_joined_event_info_init((CNA_NetworkGamerHandle)gamer, &out_info_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_infoIntegral_values[] = {
            (jlong)out_info_value.gamer
        };
        (*environment)->SetLongArrayRegion(environment, out_infoIntegral, 0,
            (jsize)(sizeof out_infoIntegral_values / sizeof out_infoIntegral_values[0]),
            out_infoIntegral_values);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_gamerLeftEventInfoInit(
    JNIEnv* environment, jclass type, jlong gamer, jlongArray out_infoIntegral)
{
    (void)environment;
    (void)type;
    CNA_GamerLeftEventInfo out_info_value;
    memset(&out_info_value, 0, sizeof out_info_value);
    out_info_value.struct_size = (uint32_t)(sizeof out_info_value);
    out_info_value.struct_version = UINT32_C(1);
    CNA_Result result = cna.gamer_left_event_info_init((CNA_NetworkGamerHandle)gamer, &out_info_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_infoIntegral_values[] = {
            (jlong)out_info_value.gamer
        };
        (*environment)->SetLongArrayRegion(environment, out_infoIntegral, 0,
            (jsize)(sizeof out_infoIntegral_values / sizeof out_infoIntegral_values[0]),
            out_infoIntegral_values);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_hostChangedEventInfoInit(
    JNIEnv* environment, jclass type, jlong old_host, jlong new_host, jlongArray out_infoIntegral)
{
    (void)environment;
    (void)type;
    CNA_HostChangedEventInfo out_info_value;
    memset(&out_info_value, 0, sizeof out_info_value);
    out_info_value.struct_size = (uint32_t)(sizeof out_info_value);
    out_info_value.struct_version = UINT32_C(1);
    CNA_Result result = cna.host_changed_event_info_init((CNA_NetworkGamerHandle)old_host, (CNA_NetworkGamerHandle)new_host, &out_info_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_infoIntegral_values[] = {
            (jlong)out_info_value.old_host,
            (jlong)out_info_value.new_host
        };
        (*environment)->SetLongArrayRegion(environment, out_infoIntegral, 0,
            (jsize)(sizeof out_infoIntegral_values / sizeof out_infoIntegral_values[0]),
            out_infoIntegral_values);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_localNetworkGamerClearPacketQueueExt(
    JNIEnv* environment, jclass type, jlong gamer)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.local_network_gamer_clear_packet_queue_ext((CNA_NetworkGamerHandle)gamer);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_localNetworkGamerCreateExt(
    JNIEnv* environment, jclass type, jlong signed_in_gamer, jlong session, jlongArray out_gamer)
{
    (void)environment;
    (void)type;
    CNA_NetworkGamerHandle out_gamer_value = 0;
    CNA_Result result = cna.local_network_gamer_create_ext((CNA_SignedInGamerHandle)signed_in_gamer, (CNA_NetworkSessionHandle)session, &out_gamer_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_gamer_element = (jlong)out_gamer_value;
        (*environment)->SetLongArrayRegion(
            environment, out_gamer, 0, 1, &out_gamer_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_localNetworkGamerEnableSendVoice(
    JNIEnv* environment, jclass type, jlong gamer, jlong remote_gamer, jboolean enable)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.local_network_gamer_enable_send_voice((CNA_NetworkGamerHandle)gamer, (CNA_NetworkGamerHandle)remote_gamer, (CNA_Bool)enable);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_localNetworkGamerGetIsDataAvailable(
    JNIEnv* environment, jclass type, jlong gamer, jbooleanArray out_value)
{
    (void)environment;
    (void)type;
    CNA_Bool out_value_value = 0;
    CNA_Result result = cna.local_network_gamer_get_is_data_available((CNA_NetworkGamerHandle)gamer, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jboolean out_value_element = (jboolean)out_value_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_localNetworkGamerGetSignedInGamer(
    JNIEnv* environment, jclass type, jlong gamer, jlongArray out_signed_in_gamer)
{
    (void)environment;
    (void)type;
    CNA_SignedInGamerHandle out_signed_in_gamer_value = 0;
    CNA_Result result = cna.local_network_gamer_get_signed_in_gamer((CNA_NetworkGamerHandle)gamer, &out_signed_in_gamer_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_signed_in_gamer_element = (jlong)out_signed_in_gamer_value;
        (*environment)->SetLongArrayRegion(
            environment, out_signed_in_gamer, 0, 1, &out_signed_in_gamer_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_localNetworkGamerReceiveData(
    JNIEnv* environment, jclass type, jlong gamer, jbyteArray destination, jlongArray out_sender, jlongArray out_received)
{
    (void)environment;
    (void)type;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_elements = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_elements == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint8_t* destination_values = (uint8_t*)malloc(
        ((size_t)destination_size + 1U) * sizeof(*destination_values));
    if (destination_values == NULL) {
        (*environment)->ReleaseByteArrayElements(
            environment, destination, destination_elements, JNI_ABORT);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    for (jsize index = 0; index < destination_size; ++index) {
        destination_values[index] = (uint8_t)destination_elements[index];
    }
    CNA_NetworkGamerHandle out_sender_value = 0;
    uint64_t out_received_value = 0;
    CNA_Result result = cna.local_network_gamer_receive_data((CNA_NetworkGamerHandle)gamer, destination_values, (uint64_t)destination_size, &out_sender_value, &out_received_value);
    if (result == CNA_RESULT_SUCCESS) {
        for (jsize index = 0; index < destination_size; ++index) {
            destination_elements[index] = (jbyte)destination_values[index];
        }
    }
    free(destination_values);
    (*environment)->ReleaseByteArrayElements(
        environment, destination, destination_elements, 0);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_sender_element = (jlong)out_sender_value;
        (*environment)->SetLongArrayRegion(
            environment, out_sender, 0, 1, &out_sender_element);
        jlong out_received_element = (jlong)out_received_value;
        (*environment)->SetLongArrayRegion(
            environment, out_received, 0, 1, &out_received_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_localNetworkGamerReceiveDataAt(
    JNIEnv* environment, jclass type, jlong gamer, jbyteArray destination, jint offset, jlongArray out_sender, jlongArray out_received)
{
    (void)environment;
    (void)type;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_elements = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_elements == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint8_t* destination_values = (uint8_t*)malloc(
        ((size_t)destination_size + 1U) * sizeof(*destination_values));
    if (destination_values == NULL) {
        (*environment)->ReleaseByteArrayElements(
            environment, destination, destination_elements, JNI_ABORT);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    for (jsize index = 0; index < destination_size; ++index) {
        destination_values[index] = (uint8_t)destination_elements[index];
    }
    CNA_NetworkGamerHandle out_sender_value = 0;
    uint64_t out_received_value = 0;
    CNA_Result result = cna.local_network_gamer_receive_data_at((CNA_NetworkGamerHandle)gamer, destination_values, (uint64_t)destination_size, (int32_t)offset, &out_sender_value, &out_received_value);
    if (result == CNA_RESULT_SUCCESS) {
        for (jsize index = 0; index < destination_size; ++index) {
            destination_elements[index] = (jbyte)destination_values[index];
        }
    }
    free(destination_values);
    (*environment)->ReleaseByteArrayElements(
        environment, destination, destination_elements, 0);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_sender_element = (jlong)out_sender_value;
        (*environment)->SetLongArrayRegion(
            environment, out_sender, 0, 1, &out_sender_element);
        jlong out_received_element = (jlong)out_received_value;
        (*environment)->SetLongArrayRegion(
            environment, out_received, 0, 1, &out_received_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_localNetworkGamerReceiveDataIntoPacketReader(
    JNIEnv* environment, jclass type, jlong gamer, jlong reader, jlongArray out_sender, jlongArray out_received)
{
    (void)environment;
    (void)type;
    CNA_NetworkGamerHandle out_sender_value = 0;
    uint64_t out_received_value = 0;
    CNA_Result result = cna.local_network_gamer_receive_data_into_packet_reader((CNA_NetworkGamerHandle)gamer, (CNA_PacketReaderHandle)reader, &out_sender_value, &out_received_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_sender_element = (jlong)out_sender_value;
        (*environment)->SetLongArrayRegion(
            environment, out_sender, 0, 1, &out_sender_element);
        jlong out_received_element = (jlong)out_received_value;
        (*environment)->SetLongArrayRegion(
            environment, out_received, 0, 1, &out_received_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_localNetworkGamerSendData(
    JNIEnv* environment, jclass type, jlong gamer, jbyteArray data, jint options)
{
    (void)environment;
    (void)type;
    jsize data_size = (*environment)->GetArrayLength(environment, data);
    jbyte* data_elements = (*environment)->GetByteArrayElements(environment, data, NULL);
    if (data_elements == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint8_t* data_values = (uint8_t*)malloc(
        ((size_t)data_size + 1U) * sizeof(*data_values));
    if (data_values == NULL) {
        (*environment)->ReleaseByteArrayElements(
            environment, data, data_elements, JNI_ABORT);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    for (jsize index = 0; index < data_size; ++index) {
        data_values[index] = (uint8_t)data_elements[index];
    }
    CNA_Result result = cna.local_network_gamer_send_data((CNA_NetworkGamerHandle)gamer, data_values, (uint64_t)data_size, (CNA_SendDataOptions)options);
    free(data_values);
    (*environment)->ReleaseByteArrayElements(
        environment, data, data_elements, JNI_ABORT);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_localNetworkGamerSendDataRange(
    JNIEnv* environment, jclass type, jlong gamer, jbyteArray data, jint offset, jint length, jint options)
{
    (void)environment;
    (void)type;
    jsize data_size = (*environment)->GetArrayLength(environment, data);
    jbyte* data_elements = (*environment)->GetByteArrayElements(environment, data, NULL);
    if (data_elements == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint8_t* data_values = (uint8_t*)malloc(
        ((size_t)data_size + 1U) * sizeof(*data_values));
    if (data_values == NULL) {
        (*environment)->ReleaseByteArrayElements(
            environment, data, data_elements, JNI_ABORT);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    for (jsize index = 0; index < data_size; ++index) {
        data_values[index] = (uint8_t)data_elements[index];
    }
    CNA_Result result = cna.local_network_gamer_send_data_range((CNA_NetworkGamerHandle)gamer, data_values, (uint64_t)data_size, (int32_t)offset, (int32_t)length, (CNA_SendDataOptions)options);
    free(data_values);
    (*environment)->ReleaseByteArrayElements(
        environment, data, data_elements, JNI_ABORT);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_localNetworkGamerSendDataRangeTo(
    JNIEnv* environment, jclass type, jlong gamer, jbyteArray data, jint offset, jint length, jint options, jlong recipient)
{
    (void)environment;
    (void)type;
    jsize data_size = (*environment)->GetArrayLength(environment, data);
    jbyte* data_elements = (*environment)->GetByteArrayElements(environment, data, NULL);
    if (data_elements == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint8_t* data_values = (uint8_t*)malloc(
        ((size_t)data_size + 1U) * sizeof(*data_values));
    if (data_values == NULL) {
        (*environment)->ReleaseByteArrayElements(
            environment, data, data_elements, JNI_ABORT);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    for (jsize index = 0; index < data_size; ++index) {
        data_values[index] = (uint8_t)data_elements[index];
    }
    CNA_Result result = cna.local_network_gamer_send_data_range_to((CNA_NetworkGamerHandle)gamer, data_values, (uint64_t)data_size, (int32_t)offset, (int32_t)length, (CNA_SendDataOptions)options, (CNA_NetworkGamerHandle)recipient);
    free(data_values);
    (*environment)->ReleaseByteArrayElements(
        environment, data, data_elements, JNI_ABORT);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_localNetworkGamerSendDataTo(
    JNIEnv* environment, jclass type, jlong gamer, jbyteArray data, jint options, jlong recipient)
{
    (void)environment;
    (void)type;
    jsize data_size = (*environment)->GetArrayLength(environment, data);
    jbyte* data_elements = (*environment)->GetByteArrayElements(environment, data, NULL);
    if (data_elements == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint8_t* data_values = (uint8_t*)malloc(
        ((size_t)data_size + 1U) * sizeof(*data_values));
    if (data_values == NULL) {
        (*environment)->ReleaseByteArrayElements(
            environment, data, data_elements, JNI_ABORT);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    for (jsize index = 0; index < data_size; ++index) {
        data_values[index] = (uint8_t)data_elements[index];
    }
    CNA_Result result = cna.local_network_gamer_send_data_to((CNA_NetworkGamerHandle)gamer, data_values, (uint64_t)data_size, (CNA_SendDataOptions)options, (CNA_NetworkGamerHandle)recipient);
    free(data_values);
    (*environment)->ReleaseByteArrayElements(
        environment, data, data_elements, JNI_ABORT);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_localNetworkGamerSendPacketWriter(
    JNIEnv* environment, jclass type, jlong gamer, jlong writer, jint options)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.local_network_gamer_send_packet_writer((CNA_NetworkGamerHandle)gamer, (CNA_PacketWriterHandle)writer, (CNA_SendDataOptions)options);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_localNetworkGamerSendPacketWriterTo(
    JNIEnv* environment, jclass type, jlong gamer, jlong writer, jint options, jlong recipient)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.local_network_gamer_send_packet_writer_to((CNA_NetworkGamerHandle)gamer, (CNA_PacketWriterHandle)writer, (CNA_SendDataOptions)options, (CNA_NetworkGamerHandle)recipient);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_localNetworkGamerSendPartyInvites(
    JNIEnv* environment, jclass type, jlong gamer)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.local_network_gamer_send_party_invites((CNA_NetworkGamerHandle)gamer);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_netGetLastJoinError(
    JNIEnv* environment, jclass type, jintArray out_join_error, jbooleanArray out_has_join_error)
{
    (void)environment;
    (void)type;
    CNA_NetworkSessionJoinError out_join_error_value = 0;
    CNA_Bool out_has_join_error_value = 0;
    CNA_Result result = cna.net_get_last_join_error(&out_join_error_value, &out_has_join_error_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_join_error_element = (jint)out_join_error_value;
        (*environment)->SetIntArrayRegion(
            environment, out_join_error, 0, 1, &out_join_error_element);
        jboolean out_has_join_error_element = (jboolean)out_has_join_error_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_has_join_error, 0, 1, &out_has_join_error_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkGamerCopyMachine(
    JNIEnv* environment, jclass type, jlong gamer, jlongArray out_machine)
{
    (void)environment;
    (void)type;
    CNA_NetworkMachineHandle out_machine_value = 0;
    CNA_Result result = cna.network_gamer_copy_machine((CNA_NetworkGamerHandle)gamer, &out_machine_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_machine_element = (jlong)out_machine_value;
        (*environment)->SetLongArrayRegion(
            environment, out_machine, 0, 1, &out_machine_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkGamerCreate(
    JNIEnv* environment, jclass type, jlong session, jbyteArray gamertag, jlongArray out_gamer)
{
    (void)environment;
    (void)type;
    jsize gamertag_size = (*environment)->GetArrayLength(environment, gamertag);
    jbyte* gamertag_bytes = (*environment)->GetByteArrayElements(environment, gamertag, NULL);
    if (gamertag_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    CNA_StringView gamertag_view = {(const char*)gamertag_bytes, (uint64_t)gamertag_size};
    CNA_NetworkGamerHandle out_gamer_value = 0;
    CNA_Result result = cna.network_gamer_create((CNA_Handle)session, gamertag_view, &out_gamer_value);
    (*environment)->ReleaseByteArrayElements(environment, gamertag, gamertag_bytes, JNI_ABORT);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_gamer_element = (jlong)out_gamer_value;
        (*environment)->SetLongArrayRegion(
            environment, out_gamer, 0, 1, &out_gamer_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkGamerDestroy(
    JNIEnv* environment, jclass type, jlong gamer)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.network_gamer_destroy((CNA_NetworkGamerHandle)gamer);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkGamerGetHasLeftSession(
    JNIEnv* environment, jclass type, jlong gamer, jbooleanArray out_value)
{
    (void)environment;
    (void)type;
    CNA_Bool out_value_value = 0;
    CNA_Result result = cna.network_gamer_get_has_left_session((CNA_NetworkGamerHandle)gamer, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jboolean out_value_element = (jboolean)out_value_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkGamerGetHasVoice(
    JNIEnv* environment, jclass type, jlong gamer, jbooleanArray out_value)
{
    (void)environment;
    (void)type;
    CNA_Bool out_value_value = 0;
    CNA_Result result = cna.network_gamer_get_has_voice((CNA_NetworkGamerHandle)gamer, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jboolean out_value_element = (jboolean)out_value_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkGamerGetId(
    JNIEnv* environment, jclass type, jlong gamer, jbyteArray out_value)
{
    (void)environment;
    (void)type;
    uint8_t out_value_value = 0;
    CNA_Result result = cna.network_gamer_get_id((CNA_NetworkGamerHandle)gamer, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jbyte out_value_element = (jbyte)out_value_value;
        (*environment)->SetByteArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkGamerGetIsGuest(
    JNIEnv* environment, jclass type, jlong gamer, jbooleanArray out_value)
{
    (void)environment;
    (void)type;
    CNA_Bool out_value_value = 0;
    CNA_Result result = cna.network_gamer_get_is_guest((CNA_NetworkGamerHandle)gamer, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jboolean out_value_element = (jboolean)out_value_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkGamerGetIsHost(
    JNIEnv* environment, jclass type, jlong gamer, jbooleanArray out_value)
{
    (void)environment;
    (void)type;
    CNA_Bool out_value_value = 0;
    CNA_Result result = cna.network_gamer_get_is_host((CNA_NetworkGamerHandle)gamer, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jboolean out_value_element = (jboolean)out_value_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkGamerGetIsLocal(
    JNIEnv* environment, jclass type, jlong gamer, jbooleanArray out_value)
{
    (void)environment;
    (void)type;
    CNA_Bool out_value_value = 0;
    CNA_Result result = cna.network_gamer_get_is_local((CNA_NetworkGamerHandle)gamer, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jboolean out_value_element = (jboolean)out_value_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkGamerGetIsMutedByLocalUser(
    JNIEnv* environment, jclass type, jlong gamer, jbooleanArray out_value)
{
    (void)environment;
    (void)type;
    CNA_Bool out_value_value = 0;
    CNA_Result result = cna.network_gamer_get_is_muted_by_local_user((CNA_NetworkGamerHandle)gamer, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jboolean out_value_element = (jboolean)out_value_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkGamerGetIsPrivateSlot(
    JNIEnv* environment, jclass type, jlong gamer, jbooleanArray out_value)
{
    (void)environment;
    (void)type;
    CNA_Bool out_value_value = 0;
    CNA_Result result = cna.network_gamer_get_is_private_slot((CNA_NetworkGamerHandle)gamer, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jboolean out_value_element = (jboolean)out_value_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkGamerGetIsReady(
    JNIEnv* environment, jclass type, jlong gamer, jbooleanArray out_value)
{
    (void)environment;
    (void)type;
    CNA_Bool out_value_value = 0;
    CNA_Result result = cna.network_gamer_get_is_ready((CNA_NetworkGamerHandle)gamer, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jboolean out_value_element = (jboolean)out_value_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkGamerGetIsTalking(
    JNIEnv* environment, jclass type, jlong gamer, jbooleanArray out_value)
{
    (void)environment;
    (void)type;
    CNA_Bool out_value_value = 0;
    CNA_Result result = cna.network_gamer_get_is_talking((CNA_NetworkGamerHandle)gamer, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jboolean out_value_element = (jboolean)out_value_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkGamerGetRoundtripTicks(
    JNIEnv* environment, jclass type, jlong gamer, jlongArray out_ticks)
{
    (void)environment;
    (void)type;
    int64_t out_ticks_value = 0;
    CNA_Result result = cna.network_gamer_get_roundtrip_ticks((CNA_NetworkGamerHandle)gamer, &out_ticks_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_ticks_element = (jlong)out_ticks_value;
        (*environment)->SetLongArrayRegion(
            environment, out_ticks, 0, 1, &out_ticks_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkGamerGetSession(
    JNIEnv* environment, jclass type, jlong gamer, jlongArray out_session)
{
    (void)environment;
    (void)type;
    CNA_Handle out_session_value = 0;
    CNA_Result result = cna.network_gamer_get_session((CNA_NetworkGamerHandle)gamer, &out_session_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_session_element = (jlong)out_session_value;
        (*environment)->SetLongArrayRegion(
            environment, out_session, 0, 1, &out_session_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkGamerSetHasLeftSessionExt(
    JNIEnv* environment, jclass type, jlong gamer, jboolean value)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.network_gamer_set_has_left_session_ext((CNA_NetworkGamerHandle)gamer, (CNA_Bool)value);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkGamerSetIdExt(
    JNIEnv* environment, jclass type, jlong gamer, jbyte value)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.network_gamer_set_id_ext((CNA_NetworkGamerHandle)gamer, (uint8_t)value);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkGamerSetIsHostExt(
    JNIEnv* environment, jclass type, jlong gamer, jboolean value)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.network_gamer_set_is_host_ext((CNA_NetworkGamerHandle)gamer, (CNA_Bool)value);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkGamerSetIsReady(
    JNIEnv* environment, jclass type, jlong gamer, jboolean value)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.network_gamer_set_is_ready((CNA_NetworkGamerHandle)gamer, (CNA_Bool)value);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkGamerSetMachine(
    JNIEnv* environment, jclass type, jlong gamer, jlong machine)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.network_gamer_set_machine((CNA_NetworkGamerHandle)gamer, (CNA_NetworkMachineHandle)machine);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkGamerSetRoundtripTicksExt(
    JNIEnv* environment, jclass type, jlong gamer, jlong ticks)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.network_gamer_set_roundtrip_ticks_ext((CNA_NetworkGamerHandle)gamer, (int64_t)ticks);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkMachineCreate(
    JNIEnv* environment, jclass type, jlongArray out_machine)
{
    (void)environment;
    (void)type;
    CNA_NetworkMachineHandle out_machine_value = 0;
    CNA_Result result = cna.network_machine_create(&out_machine_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_machine_element = (jlong)out_machine_value;
        (*environment)->SetLongArrayRegion(
            environment, out_machine, 0, 1, &out_machine_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkMachineDestroy(
    JNIEnv* environment, jclass type, jlong machine)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.network_machine_destroy((CNA_NetworkMachineHandle)machine);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkMachineGetGamer(
    JNIEnv* environment, jclass type, jlong machine, jint index, jlongArray out_gamer)
{
    (void)environment;
    (void)type;
    CNA_NetworkGamerHandle out_gamer_value = 0;
    CNA_Result result = cna.network_machine_get_gamer((CNA_NetworkMachineHandle)machine, (int32_t)index, &out_gamer_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_gamer_element = (jlong)out_gamer_value;
        (*environment)->SetLongArrayRegion(
            environment, out_gamer, 0, 1, &out_gamer_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkMachineGetGamerCount(
    JNIEnv* environment, jclass type, jlong machine, jintArray out_count)
{
    (void)environment;
    (void)type;
    int32_t out_count_value = 0;
    CNA_Result result = cna.network_machine_get_gamer_count((CNA_NetworkMachineHandle)machine, &out_count_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_count_element = (jint)out_count_value;
        (*environment)->SetIntArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkMachineRemoveFromSession(
    JNIEnv* environment, jclass type, jlong machine)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.network_machine_remove_from_session((CNA_NetworkMachineHandle)machine);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionAddLocalGamer(
    JNIEnv* environment, jclass type, jlong session, jlong signed_in_gamer)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.network_session_add_local_gamer((CNA_NetworkSessionHandle)session, (CNA_Handle)signed_in_gamer);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionAddRemoteGamerExt(
    JNIEnv* environment, jclass type, jlong session, jlong gamer)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.network_session_add_remote_gamer_ext((CNA_NetworkSessionHandle)session, (CNA_NetworkGamerHandle)gamer);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionCopySessionProperties(
    JNIEnv* environment, jclass type, jlong session, jlongArray out_properties)
{
    (void)environment;
    (void)type;
    CNA_NetworkSessionPropertiesHandle out_properties_value = 0;
    CNA_Result result = cna.network_session_copy_session_properties((CNA_NetworkSessionHandle)session, &out_properties_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_properties_element = (jlong)out_properties_value;
        (*environment)->SetLongArrayRegion(
            environment, out_properties, 0, 1, &out_properties_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionCopyTypeName(
    JNIEnv* environment, jclass type, jlong session, jbyteArray destination, jlongArray out_bytes)
{
    (void)environment;
    (void)type;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_bytes = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_bytes == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint64_t destination_written = 0;
    CNA_Result result = cna.network_session_copy_type_name((CNA_NetworkSessionHandle)session, (char*)destination_bytes, (uint64_t)destination_size, &destination_written);
    (*environment)->ReleaseByteArrayElements(environment, destination, destination_bytes, 0);
    if (result == CNA_RESULT_SUCCESS) {
        jlong destination_element = (jlong)destination_written;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &destination_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionCreate(
    JNIEnv* environment, jclass type, jint session_type, jint max_local_gamers, jint max_gamers, jlongArray out_session)
{
    (void)environment;
    (void)type;
    CNA_NetworkSessionHandle out_session_value = 0;
    CNA_Result result = cna.network_session_create((CNA_NetworkSessionType)session_type, (int32_t)max_local_gamers, (int32_t)max_gamers, &out_session_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_session_element = (jlong)out_session_value;
        (*environment)->SetLongArrayRegion(
            environment, out_session, 0, 1, &out_session_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionCreateWithLocalGamers(
    JNIEnv* environment, jclass type, jint session_type, jlongArray local_gamers, jint max_gamers, jint private_gamer_slots, jlong session_properties, jlongArray out_session)
{
    (void)environment;
    (void)type;
    jsize local_gamers_size = (*environment)->GetArrayLength(environment, local_gamers);
    jlong* local_gamers_elements = (*environment)->GetLongArrayElements(environment, local_gamers, NULL);
    if (local_gamers_elements == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    CNA_Handle* local_gamers_values = (CNA_Handle*)malloc(
        ((size_t)local_gamers_size + 1U) * sizeof(*local_gamers_values));
    if (local_gamers_values == NULL) {
        (*environment)->ReleaseLongArrayElements(
            environment, local_gamers, local_gamers_elements, JNI_ABORT);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    for (jsize index = 0; index < local_gamers_size; ++index) {
        local_gamers_values[index] = (CNA_Handle)local_gamers_elements[index];
    }
    CNA_NetworkSessionHandle out_session_value = 0;
    CNA_Result result = cna.network_session_create_with_local_gamers((CNA_NetworkSessionType)session_type, local_gamers_values, (uint64_t)local_gamers_size, (int32_t)max_gamers, (int32_t)private_gamer_slots, (CNA_NetworkSessionPropertiesHandle)session_properties, &out_session_value);
    free(local_gamers_values);
    (*environment)->ReleaseLongArrayElements(
        environment, local_gamers, local_gamers_elements, JNI_ABORT);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_session_element = (jlong)out_session_value;
        (*environment)->SetLongArrayRegion(
            environment, out_session, 0, 1, &out_session_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionCreateWithProperties(
    JNIEnv* environment, jclass type, jint session_type, jint max_local_gamers, jint max_gamers, jint private_gamer_slots, jlong session_properties, jlongArray out_session)
{
    (void)environment;
    (void)type;
    CNA_NetworkSessionHandle out_session_value = 0;
    CNA_Result result = cna.network_session_create_with_properties((CNA_NetworkSessionType)session_type, (int32_t)max_local_gamers, (int32_t)max_gamers, (int32_t)private_gamer_slots, (CNA_NetworkSessionPropertiesHandle)session_properties, &out_session_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_session_element = (jlong)out_session_value;
        (*environment)->SetLongArrayRegion(
            environment, out_session, 0, 1, &out_session_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionDestroy(
    JNIEnv* environment, jclass type, jlong session)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.network_session_destroy((CNA_NetworkSessionHandle)session);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionDispose(
    JNIEnv* environment, jclass type, jlong session)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.network_session_dispose((CNA_NetworkSessionHandle)session);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionEndGame(
    JNIEnv* environment, jclass type, jlong session)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.network_session_end_game((CNA_NetworkSessionHandle)session);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionEndedEventInfoInit(
    JNIEnv* environment, jclass type, jint end_reason, jbyteArray out_infoBytes, jlongArray out_infoIntegral)
{
    (void)environment;
    (void)type;
    CNA_NetworkSessionEndedEventInfo out_info_value;
    memset(&out_info_value, 0, sizeof out_info_value);
    out_info_value.struct_size = (uint32_t)(sizeof out_info_value);
    out_info_value.struct_version = UINT32_C(1);
    CNA_Result result = cna.network_session_ended_event_info_init((CNA_NetworkSessionEndReason)end_reason, &out_info_value);
    if (result == CNA_RESULT_SUCCESS) {
        jbyte out_infoBytes_values[] = {
            (jbyte)out_info_value.reserved[0],
            (jbyte)out_info_value.reserved[1],
            (jbyte)out_info_value.reserved[2],
            (jbyte)out_info_value.reserved[3]
        };
        (*environment)->SetByteArrayRegion(environment, out_infoBytes, 0,
            (jsize)(sizeof out_infoBytes_values / sizeof out_infoBytes_values[0]),
            out_infoBytes_values);
        jlong out_infoIntegral_values[] = {
            (jlong)out_info_value.end_reason
        };
        (*environment)->SetLongArrayRegion(environment, out_infoIntegral, 0,
            (jsize)(sizeof out_infoIntegral_values / sizeof out_infoIntegral_values[0]),
            out_infoIntegral_values);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionFind(
    JNIEnv* environment, jclass type, jint session_type, jint max_local_gamers, jlong search_properties, jlongArray out_collection)
{
    (void)environment;
    (void)type;
    CNA_AvailableNetworkSessionCollectionHandle out_collection_value = 0;
    CNA_Result result = cna.network_session_find((CNA_NetworkSessionType)session_type, (int32_t)max_local_gamers, (CNA_NetworkSessionPropertiesHandle)search_properties, &out_collection_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_collection_element = (jlong)out_collection_value;
        (*environment)->SetLongArrayRegion(
            environment, out_collection, 0, 1, &out_collection_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionFindGamerById(
    JNIEnv* environment, jclass type, jlong session, jbyte gamer_id, jlongArray out_gamer)
{
    (void)environment;
    (void)type;
    CNA_NetworkGamerHandle out_gamer_value = 0;
    CNA_Result result = cna.network_session_find_gamer_by_id((CNA_NetworkSessionHandle)session, (uint8_t)gamer_id, &out_gamer_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_gamer_element = (jlong)out_gamer_value;
        (*environment)->SetLongArrayRegion(
            environment, out_gamer, 0, 1, &out_gamer_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionFindWithLocalGamers(
    JNIEnv* environment, jclass type, jint session_type, jlongArray local_gamers, jlong search_properties, jlongArray out_collection)
{
    (void)environment;
    (void)type;
    jsize local_gamers_size = (*environment)->GetArrayLength(environment, local_gamers);
    jlong* local_gamers_elements = (*environment)->GetLongArrayElements(environment, local_gamers, NULL);
    if (local_gamers_elements == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    CNA_Handle* local_gamers_values = (CNA_Handle*)malloc(
        ((size_t)local_gamers_size + 1U) * sizeof(*local_gamers_values));
    if (local_gamers_values == NULL) {
        (*environment)->ReleaseLongArrayElements(
            environment, local_gamers, local_gamers_elements, JNI_ABORT);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    for (jsize index = 0; index < local_gamers_size; ++index) {
        local_gamers_values[index] = (CNA_Handle)local_gamers_elements[index];
    }
    CNA_AvailableNetworkSessionCollectionHandle out_collection_value = 0;
    CNA_Result result = cna.network_session_find_with_local_gamers((CNA_NetworkSessionType)session_type, local_gamers_values, (uint64_t)local_gamers_size, (CNA_NetworkSessionPropertiesHandle)search_properties, &out_collection_value);
    free(local_gamers_values);
    (*environment)->ReleaseLongArrayElements(
        environment, local_gamers, local_gamers_elements, JNI_ABORT);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_collection_element = (jlong)out_collection_value;
        (*environment)->SetLongArrayRegion(
            environment, out_collection, 0, 1, &out_collection_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionGetActiveActionCountExt(
    JNIEnv* environment, jclass type, jintArray out_count)
{
    (void)environment;
    (void)type;
    int32_t out_count_value = 0;
    CNA_Result result = cna.network_session_get_active_action_count_ext(&out_count_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_count_element = (jint)out_count_value;
        (*environment)->SetIntArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionGetAllowHostMigration(
    JNIEnv* environment, jclass type, jlong session, jbooleanArray out_value)
{
    (void)environment;
    (void)type;
    CNA_Bool out_value_value = 0;
    CNA_Result result = cna.network_session_get_allow_host_migration((CNA_NetworkSessionHandle)session, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jboolean out_value_element = (jboolean)out_value_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionGetAllowJoinInProgress(
    JNIEnv* environment, jclass type, jlong session, jbooleanArray out_value)
{
    (void)environment;
    (void)type;
    CNA_Bool out_value_value = 0;
    CNA_Result result = cna.network_session_get_allow_join_in_progress((CNA_NetworkSessionHandle)session, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jboolean out_value_element = (jboolean)out_value_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionGetBytesPerSecondReceived(
    JNIEnv* environment, jclass type, jlong session, jintArray out_value)
{
    (void)environment;
    (void)type;
    int32_t out_value_value = 0;
    CNA_Result result = cna.network_session_get_bytes_per_second_received((CNA_NetworkSessionHandle)session, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_value_element = (jint)out_value_value;
        (*environment)->SetIntArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionGetBytesPerSecondSent(
    JNIEnv* environment, jclass type, jlong session, jintArray out_value)
{
    (void)environment;
    (void)type;
    int32_t out_value_value = 0;
    CNA_Result result = cna.network_session_get_bytes_per_second_sent((CNA_NetworkSessionHandle)session, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_value_element = (jint)out_value_value;
        (*environment)->SetIntArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionGetGamer(
    JNIEnv* environment, jclass type, jlong session, jint roster, jint index, jlongArray out_gamer)
{
    (void)environment;
    (void)type;
    CNA_NetworkGamerHandle out_gamer_value = 0;
    CNA_Result result = cna.network_session_get_gamer((CNA_NetworkSessionHandle)session, (uint32_t)roster, (int32_t)index, &out_gamer_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_gamer_element = (jlong)out_gamer_value;
        (*environment)->SetLongArrayRegion(
            environment, out_gamer, 0, 1, &out_gamer_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionGetGamerCount(
    JNIEnv* environment, jclass type, jlong session, jint roster, jintArray out_count)
{
    (void)environment;
    (void)type;
    int32_t out_count_value = 0;
    CNA_Result result = cna.network_session_get_gamer_count((CNA_NetworkSessionHandle)session, (uint32_t)roster, &out_count_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_count_element = (jint)out_count_value;
        (*environment)->SetIntArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionGetHost(
    JNIEnv* environment, jclass type, jlong session, jlongArray out_gamer)
{
    (void)environment;
    (void)type;
    CNA_NetworkGamerHandle out_gamer_value = 0;
    CNA_Result result = cna.network_session_get_host((CNA_NetworkSessionHandle)session, &out_gamer_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_gamer_element = (jlong)out_gamer_value;
        (*environment)->SetLongArrayRegion(
            environment, out_gamer, 0, 1, &out_gamer_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionGetInstanceCountExt(
    JNIEnv* environment, jclass type, jintArray out_count)
{
    (void)environment;
    (void)type;
    int32_t out_count_value = 0;
    CNA_Result result = cna.network_session_get_instance_count_ext(&out_count_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_count_element = (jint)out_count_value;
        (*environment)->SetIntArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionGetIsDisposed(
    JNIEnv* environment, jclass type, jlong session, jbooleanArray out_is_disposed)
{
    (void)environment;
    (void)type;
    CNA_Bool out_is_disposed_value = 0;
    CNA_Result result = cna.network_session_get_is_disposed((CNA_NetworkSessionHandle)session, &out_is_disposed_value);
    if (result == CNA_RESULT_SUCCESS) {
        jboolean out_is_disposed_element = (jboolean)out_is_disposed_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_is_disposed, 0, 1, &out_is_disposed_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionGetIsEveryoneReady(
    JNIEnv* environment, jclass type, jlong session, jbooleanArray out_value)
{
    (void)environment;
    (void)type;
    CNA_Bool out_value_value = 0;
    CNA_Result result = cna.network_session_get_is_everyone_ready((CNA_NetworkSessionHandle)session, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jboolean out_value_element = (jboolean)out_value_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionGetIsHost(
    JNIEnv* environment, jclass type, jlong session, jbooleanArray out_value)
{
    (void)environment;
    (void)type;
    CNA_Bool out_value_value = 0;
    CNA_Result result = cna.network_session_get_is_host((CNA_NetworkSessionHandle)session, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jboolean out_value_element = (jboolean)out_value_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionGetMaxGamers(
    JNIEnv* environment, jclass type, jlong session, jintArray out_value)
{
    (void)environment;
    (void)type;
    int32_t out_value_value = 0;
    CNA_Result result = cna.network_session_get_max_gamers((CNA_NetworkSessionHandle)session, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_value_element = (jint)out_value_value;
        (*environment)->SetIntArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionGetOwnedGamerCountExt(
    JNIEnv* environment, jclass type, jlong session, jlongArray out_count)
{
    (void)environment;
    (void)type;
    uint64_t out_count_value = 0;
    CNA_Result result = cna.network_session_get_owned_gamer_count_ext((CNA_NetworkSessionHandle)session, &out_count_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_count_element = (jlong)out_count_value;
        (*environment)->SetLongArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionGetPrivateGamerSlots(
    JNIEnv* environment, jclass type, jlong session, jintArray out_value)
{
    (void)environment;
    (void)type;
    int32_t out_value_value = 0;
    CNA_Result result = cna.network_session_get_private_gamer_slots((CNA_NetworkSessionHandle)session, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_value_element = (jint)out_value_value;
        (*environment)->SetIntArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionGetSessionState(
    JNIEnv* environment, jclass type, jlong session, jintArray out_value)
{
    (void)environment;
    (void)type;
    CNA_NetworkSessionState out_value_value = 0;
    CNA_Result result = cna.network_session_get_session_state((CNA_NetworkSessionHandle)session, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_value_element = (jint)out_value_value;
        (*environment)->SetIntArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionGetSessionType(
    JNIEnv* environment, jclass type, jlong session, jintArray out_value)
{
    (void)environment;
    (void)type;
    CNA_NetworkSessionType out_value_value = 0;
    CNA_Result result = cna.network_session_get_session_type((CNA_NetworkSessionHandle)session, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_value_element = (jint)out_value_value;
        (*environment)->SetIntArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionGetSimulatedLatencyTicks(
    JNIEnv* environment, jclass type, jlong session, jlongArray out_ticks)
{
    (void)environment;
    (void)type;
    int64_t out_ticks_value = 0;
    CNA_Result result = cna.network_session_get_simulated_latency_ticks((CNA_NetworkSessionHandle)session, &out_ticks_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_ticks_element = (jlong)out_ticks_value;
        (*environment)->SetLongArrayRegion(
            environment, out_ticks, 0, 1, &out_ticks_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionGetSimulatedPacketLoss(
    JNIEnv* environment, jclass type, jlong session, jfloatArray out_value)
{
    (void)environment;
    (void)type;
    float out_value_value = 0;
    CNA_Result result = cna.network_session_get_simulated_packet_loss((CNA_NetworkSessionHandle)session, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionGetTypeNameSize(
    JNIEnv* environment, jclass type, jlong session, jlongArray out_bytes)
{
    (void)environment;
    (void)type;
    uint64_t out_bytes_value = 0;
    CNA_Result result = cna.network_session_get_type_name_size((CNA_NetworkSessionHandle)session, &out_bytes_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_bytes_element = (jlong)out_bytes_value;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &out_bytes_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionJoin(
    JNIEnv* environment, jclass type, jlong available_session, jlongArray out_session)
{
    (void)environment;
    (void)type;
    CNA_NetworkSessionHandle out_session_value = 0;
    CNA_Result result = cna.network_session_join((CNA_AvailableNetworkSessionHandle)available_session, &out_session_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_session_element = (jlong)out_session_value;
        (*environment)->SetLongArrayRegion(
            environment, out_session, 0, 1, &out_session_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionJoinInvited(
    JNIEnv* environment, jclass type, jint max_local_gamers, jlongArray out_session)
{
    (void)environment;
    (void)type;
    CNA_NetworkSessionHandle out_session_value = 0;
    CNA_Result result = cna.network_session_join_invited((int32_t)max_local_gamers, &out_session_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_session_element = (jlong)out_session_value;
        (*environment)->SetLongArrayRegion(
            environment, out_session, 0, 1, &out_session_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionJoinInvitedWithLocalGamers(
    JNIEnv* environment, jclass type, jlongArray local_gamers, jlongArray out_session)
{
    (void)environment;
    (void)type;
    jsize local_gamers_size = (*environment)->GetArrayLength(environment, local_gamers);
    jlong* local_gamers_elements = (*environment)->GetLongArrayElements(environment, local_gamers, NULL);
    if (local_gamers_elements == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    CNA_Handle* local_gamers_values = (CNA_Handle*)malloc(
        ((size_t)local_gamers_size + 1U) * sizeof(*local_gamers_values));
    if (local_gamers_values == NULL) {
        (*environment)->ReleaseLongArrayElements(
            environment, local_gamers, local_gamers_elements, JNI_ABORT);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    for (jsize index = 0; index < local_gamers_size; ++index) {
        local_gamers_values[index] = (CNA_Handle)local_gamers_elements[index];
    }
    CNA_NetworkSessionHandle out_session_value = 0;
    CNA_Result result = cna.network_session_join_invited_with_local_gamers(local_gamers_values, (uint64_t)local_gamers_size, &out_session_value);
    free(local_gamers_values);
    (*environment)->ReleaseLongArrayElements(
        environment, local_gamers, local_gamers_elements, JNI_ABORT);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_session_element = (jlong)out_session_value;
        (*environment)->SetLongArrayRegion(
            environment, out_session, 0, 1, &out_session_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionPropertiesAdd(
    JNIEnv* environment, jclass type, jlong properties, jbyteArray valueBytes, jlongArray valueIntegral)
{
    (void)environment;
    (void)type;
    CNA_OptionalInt32 value_value;
    memset(&value_value, 0, sizeof value_value);
    {
        jbyte valueBytes_values[3];
        (*environment)->GetByteArrayRegion(environment, valueBytes, 0, 3, valueBytes_values);
        value_value.reserved[0] = (uint8_t)valueBytes_values[0];
        value_value.reserved[1] = (uint8_t)valueBytes_values[1];
        value_value.reserved[2] = (uint8_t)valueBytes_values[2];
    }
    {
        jlong valueIntegral_values[2];
        (*environment)->GetLongArrayRegion(environment, valueIntegral, 0, 2, valueIntegral_values);
        value_value.has_value = (CNA_Bool)valueIntegral_values[0];
        value_value.value = (int32_t)valueIntegral_values[1];
    }
    CNA_Result result = cna.network_session_properties_add((CNA_NetworkSessionPropertiesHandle)properties, value_value);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionPropertiesClear(
    JNIEnv* environment, jclass type, jlong properties)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.network_session_properties_clear((CNA_NetworkSessionPropertiesHandle)properties);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionPropertiesContains(
    JNIEnv* environment, jclass type, jlong properties, jbyteArray valueBytes, jlongArray valueIntegral, jbooleanArray out_contains)
{
    (void)environment;
    (void)type;
    CNA_OptionalInt32 value_value;
    memset(&value_value, 0, sizeof value_value);
    {
        jbyte valueBytes_values[3];
        (*environment)->GetByteArrayRegion(environment, valueBytes, 0, 3, valueBytes_values);
        value_value.reserved[0] = (uint8_t)valueBytes_values[0];
        value_value.reserved[1] = (uint8_t)valueBytes_values[1];
        value_value.reserved[2] = (uint8_t)valueBytes_values[2];
    }
    {
        jlong valueIntegral_values[2];
        (*environment)->GetLongArrayRegion(environment, valueIntegral, 0, 2, valueIntegral_values);
        value_value.has_value = (CNA_Bool)valueIntegral_values[0];
        value_value.value = (int32_t)valueIntegral_values[1];
    }
    CNA_Bool out_contains_value = 0;
    CNA_Result result = cna.network_session_properties_contains((CNA_NetworkSessionPropertiesHandle)properties, value_value, &out_contains_value);
    if (result == CNA_RESULT_SUCCESS) {
        jboolean out_contains_element = (jboolean)out_contains_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_contains, 0, 1, &out_contains_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionPropertiesCopyTo(
    JNIEnv* environment, jclass type, jlong properties, jbyteArray destinationBytes, jlongArray destinationIntegral, jint index, jlongArray out_count)
{
    (void)environment;
    (void)type;
    jsize destination_count = (*environment)->GetArrayLength(environment, destinationBytes) / 3;
    CNA_OptionalInt32* destination_values = (CNA_OptionalInt32*)calloc(
        (size_t)destination_count + 1U, sizeof(*destination_values));
    if (destination_values == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    {
        jsize destinationBytes_length = (*environment)->GetArrayLength(environment, destinationBytes);
        jbyte* destinationBytes_values = (jbyte*)malloc(
            ((size_t)destinationBytes_length + 1U) * sizeof(*destinationBytes_values));
        if (destinationBytes_values == NULL) {
            free(destination_values);
            return (jint)CNA_RESULT_OUT_OF_MEMORY;
        }
        (*environment)->GetByteArrayRegion(environment, destinationBytes, 0, destinationBytes_length, destinationBytes_values);
        for (jsize element = 0; element < destination_count; ++element) {
            destination_values[element].reserved[0] = (uint8_t)destinationBytes_values[element * 3 + 0];
            destination_values[element].reserved[1] = (uint8_t)destinationBytes_values[element * 3 + 1];
            destination_values[element].reserved[2] = (uint8_t)destinationBytes_values[element * 3 + 2];
        }
        free(destinationBytes_values);
    }
    {
        jsize destinationIntegral_length = (*environment)->GetArrayLength(environment, destinationIntegral);
        jlong* destinationIntegral_values = (jlong*)malloc(
            ((size_t)destinationIntegral_length + 1U) * sizeof(*destinationIntegral_values));
        if (destinationIntegral_values == NULL) {
            free(destination_values);
            return (jint)CNA_RESULT_OUT_OF_MEMORY;
        }
        (*environment)->GetLongArrayRegion(environment, destinationIntegral, 0, destinationIntegral_length, destinationIntegral_values);
        for (jsize element = 0; element < destination_count; ++element) {
            destination_values[element].has_value = (CNA_Bool)destinationIntegral_values[element * 2 + 0];
            destination_values[element].value = (int32_t)destinationIntegral_values[element * 2 + 1];
        }
        free(destinationIntegral_values);
    }
    uint64_t out_count_value = 0;
    CNA_Result result = cna.network_session_properties_copy_to((CNA_NetworkSessionPropertiesHandle)properties, destination_values, (uint64_t)destination_count, (int32_t)index, &out_count_value);
    free(destination_values);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_count_element = (jlong)out_count_value;
        (*environment)->SetLongArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionPropertiesCreate(
    JNIEnv* environment, jclass type, jlongArray out_properties)
{
    (void)environment;
    (void)type;
    CNA_NetworkSessionPropertiesHandle out_properties_value = 0;
    CNA_Result result = cna.network_session_properties_create(&out_properties_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_properties_element = (jlong)out_properties_value;
        (*environment)->SetLongArrayRegion(
            environment, out_properties, 0, 1, &out_properties_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionPropertiesCreateEnumerator(
    JNIEnv* environment, jclass type, jlong properties, jlongArray out_enumerator)
{
    (void)environment;
    (void)type;
    CNA_NetworkSessionPropertyEnumeratorHandle out_enumerator_value = 0;
    CNA_Result result = cna.network_session_properties_create_enumerator((CNA_NetworkSessionPropertiesHandle)properties, &out_enumerator_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_enumerator_element = (jlong)out_enumerator_value;
        (*environment)->SetLongArrayRegion(
            environment, out_enumerator, 0, 1, &out_enumerator_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionPropertiesDestroy(
    JNIEnv* environment, jclass type, jlong properties)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.network_session_properties_destroy((CNA_NetworkSessionPropertiesHandle)properties);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionPropertiesGetCount(
    JNIEnv* environment, jclass type, jlong properties, jintArray out_count)
{
    (void)environment;
    (void)type;
    int32_t out_count_value = 0;
    CNA_Result result = cna.network_session_properties_get_count((CNA_NetworkSessionPropertiesHandle)properties, &out_count_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_count_element = (jint)out_count_value;
        (*environment)->SetIntArrayRegion(
            environment, out_count, 0, 1, &out_count_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionPropertiesGetIsReadOnly(
    JNIEnv* environment, jclass type, jlong properties, jbooleanArray out_is_read_only)
{
    (void)environment;
    (void)type;
    CNA_Bool out_is_read_only_value = 0;
    CNA_Result result = cna.network_session_properties_get_is_read_only((CNA_NetworkSessionPropertiesHandle)properties, &out_is_read_only_value);
    if (result == CNA_RESULT_SUCCESS) {
        jboolean out_is_read_only_element = (jboolean)out_is_read_only_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_is_read_only, 0, 1, &out_is_read_only_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionPropertiesGetItem(
    JNIEnv* environment, jclass type, jlong properties, jint index, jbyteArray out_valueBytes, jlongArray out_valueIntegral)
{
    (void)environment;
    (void)type;
    CNA_OptionalInt32 out_value_value;
    memset(&out_value_value, 0, sizeof out_value_value);
    CNA_Result result = cna.network_session_properties_get_item((CNA_NetworkSessionPropertiesHandle)properties, (int32_t)index, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jbyte out_valueBytes_values[] = {
            (jbyte)out_value_value.reserved[0],
            (jbyte)out_value_value.reserved[1],
            (jbyte)out_value_value.reserved[2]
        };
        (*environment)->SetByteArrayRegion(environment, out_valueBytes, 0,
            (jsize)(sizeof out_valueBytes_values / sizeof out_valueBytes_values[0]),
            out_valueBytes_values);
        jlong out_valueIntegral_values[] = {
            (jlong)out_value_value.has_value,
            (jlong)out_value_value.value
        };
        (*environment)->SetLongArrayRegion(environment, out_valueIntegral, 0,
            (jsize)(sizeof out_valueIntegral_values / sizeof out_valueIntegral_values[0]),
            out_valueIntegral_values);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionPropertiesIndexOf(
    JNIEnv* environment, jclass type, jlong properties, jbyteArray valueBytes, jlongArray valueIntegral, jintArray out_index)
{
    (void)environment;
    (void)type;
    CNA_OptionalInt32 value_value;
    memset(&value_value, 0, sizeof value_value);
    {
        jbyte valueBytes_values[3];
        (*environment)->GetByteArrayRegion(environment, valueBytes, 0, 3, valueBytes_values);
        value_value.reserved[0] = (uint8_t)valueBytes_values[0];
        value_value.reserved[1] = (uint8_t)valueBytes_values[1];
        value_value.reserved[2] = (uint8_t)valueBytes_values[2];
    }
    {
        jlong valueIntegral_values[2];
        (*environment)->GetLongArrayRegion(environment, valueIntegral, 0, 2, valueIntegral_values);
        value_value.has_value = (CNA_Bool)valueIntegral_values[0];
        value_value.value = (int32_t)valueIntegral_values[1];
    }
    int32_t out_index_value = 0;
    CNA_Result result = cna.network_session_properties_index_of((CNA_NetworkSessionPropertiesHandle)properties, value_value, &out_index_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_index_element = (jint)out_index_value;
        (*environment)->SetIntArrayRegion(
            environment, out_index, 0, 1, &out_index_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionPropertiesInsert(
    JNIEnv* environment, jclass type, jlong properties, jint index, jbyteArray valueBytes, jlongArray valueIntegral)
{
    (void)environment;
    (void)type;
    CNA_OptionalInt32 value_value;
    memset(&value_value, 0, sizeof value_value);
    {
        jbyte valueBytes_values[3];
        (*environment)->GetByteArrayRegion(environment, valueBytes, 0, 3, valueBytes_values);
        value_value.reserved[0] = (uint8_t)valueBytes_values[0];
        value_value.reserved[1] = (uint8_t)valueBytes_values[1];
        value_value.reserved[2] = (uint8_t)valueBytes_values[2];
    }
    {
        jlong valueIntegral_values[2];
        (*environment)->GetLongArrayRegion(environment, valueIntegral, 0, 2, valueIntegral_values);
        value_value.has_value = (CNA_Bool)valueIntegral_values[0];
        value_value.value = (int32_t)valueIntegral_values[1];
    }
    CNA_Result result = cna.network_session_properties_insert((CNA_NetworkSessionPropertiesHandle)properties, (int32_t)index, value_value);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionPropertiesRemove(
    JNIEnv* environment, jclass type, jlong properties, jbyteArray valueBytes, jlongArray valueIntegral, jbooleanArray out_removed)
{
    (void)environment;
    (void)type;
    CNA_OptionalInt32 value_value;
    memset(&value_value, 0, sizeof value_value);
    {
        jbyte valueBytes_values[3];
        (*environment)->GetByteArrayRegion(environment, valueBytes, 0, 3, valueBytes_values);
        value_value.reserved[0] = (uint8_t)valueBytes_values[0];
        value_value.reserved[1] = (uint8_t)valueBytes_values[1];
        value_value.reserved[2] = (uint8_t)valueBytes_values[2];
    }
    {
        jlong valueIntegral_values[2];
        (*environment)->GetLongArrayRegion(environment, valueIntegral, 0, 2, valueIntegral_values);
        value_value.has_value = (CNA_Bool)valueIntegral_values[0];
        value_value.value = (int32_t)valueIntegral_values[1];
    }
    CNA_Bool out_removed_value = 0;
    CNA_Result result = cna.network_session_properties_remove((CNA_NetworkSessionPropertiesHandle)properties, value_value, &out_removed_value);
    if (result == CNA_RESULT_SUCCESS) {
        jboolean out_removed_element = (jboolean)out_removed_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_removed, 0, 1, &out_removed_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionPropertiesRemoveAt(
    JNIEnv* environment, jclass type, jlong properties, jint index)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.network_session_properties_remove_at((CNA_NetworkSessionPropertiesHandle)properties, (int32_t)index);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionPropertiesSetItem(
    JNIEnv* environment, jclass type, jlong properties, jint index, jbyteArray valueBytes, jlongArray valueIntegral)
{
    (void)environment;
    (void)type;
    CNA_OptionalInt32 value_value;
    memset(&value_value, 0, sizeof value_value);
    {
        jbyte valueBytes_values[3];
        (*environment)->GetByteArrayRegion(environment, valueBytes, 0, 3, valueBytes_values);
        value_value.reserved[0] = (uint8_t)valueBytes_values[0];
        value_value.reserved[1] = (uint8_t)valueBytes_values[1];
        value_value.reserved[2] = (uint8_t)valueBytes_values[2];
    }
    {
        jlong valueIntegral_values[2];
        (*environment)->GetLongArrayRegion(environment, valueIntegral, 0, 2, valueIntegral_values);
        value_value.has_value = (CNA_Bool)valueIntegral_values[0];
        value_value.value = (int32_t)valueIntegral_values[1];
    }
    CNA_Result result = cna.network_session_properties_set_item((CNA_NetworkSessionPropertiesHandle)properties, (int32_t)index, value_value);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionPropertyEnumeratorDestroy(
    JNIEnv* environment, jclass type, jlong enumerator)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.network_session_property_enumerator_destroy((CNA_NetworkSessionPropertyEnumeratorHandle)enumerator);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionPropertyEnumeratorGetCurrent(
    JNIEnv* environment, jclass type, jlong enumerator, jbyteArray out_valueBytes, jlongArray out_valueIntegral)
{
    (void)environment;
    (void)type;
    CNA_OptionalInt32 out_value_value;
    memset(&out_value_value, 0, sizeof out_value_value);
    CNA_Result result = cna.network_session_property_enumerator_get_current((CNA_NetworkSessionPropertyEnumeratorHandle)enumerator, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jbyte out_valueBytes_values[] = {
            (jbyte)out_value_value.reserved[0],
            (jbyte)out_value_value.reserved[1],
            (jbyte)out_value_value.reserved[2]
        };
        (*environment)->SetByteArrayRegion(environment, out_valueBytes, 0,
            (jsize)(sizeof out_valueBytes_values / sizeof out_valueBytes_values[0]),
            out_valueBytes_values);
        jlong out_valueIntegral_values[] = {
            (jlong)out_value_value.has_value,
            (jlong)out_value_value.value
        };
        (*environment)->SetLongArrayRegion(environment, out_valueIntegral, 0,
            (jsize)(sizeof out_valueIntegral_values / sizeof out_valueIntegral_values[0]),
            out_valueIntegral_values);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionPropertyEnumeratorMoveNext(
    JNIEnv* environment, jclass type, jlong enumerator, jbooleanArray out_has_current)
{
    (void)environment;
    (void)type;
    CNA_Bool out_has_current_value = 0;
    CNA_Result result = cna.network_session_property_enumerator_move_next((CNA_NetworkSessionPropertyEnumeratorHandle)enumerator, &out_has_current_value);
    if (result == CNA_RESULT_SUCCESS) {
        jboolean out_has_current_element = (jboolean)out_has_current_value;
        (*environment)->SetBooleanArrayRegion(
            environment, out_has_current, 0, 1, &out_has_current_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionPropertyEnumeratorReset(
    JNIEnv* environment, jclass type, jlong enumerator)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.network_session_property_enumerator_reset((CNA_NetworkSessionPropertyEnumeratorHandle)enumerator);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionRemoveGamerExt(
    JNIEnv* environment, jclass type, jlong session, jlong gamer, jint reason)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.network_session_remove_gamer_ext((CNA_NetworkSessionHandle)session, (CNA_NetworkGamerHandle)gamer, (CNA_NetworkSessionEndReason)reason);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionResetReady(
    JNIEnv* environment, jclass type, jlong session)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.network_session_reset_ready((CNA_NetworkSessionHandle)session);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionSetAllowHostMigration(
    JNIEnv* environment, jclass type, jlong session, jboolean value)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.network_session_set_allow_host_migration((CNA_NetworkSessionHandle)session, (CNA_Bool)value);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionSetAllowJoinInProgress(
    JNIEnv* environment, jclass type, jlong session, jboolean value)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.network_session_set_allow_join_in_progress((CNA_NetworkSessionHandle)session, (CNA_Bool)value);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionSetMaxGamers(
    JNIEnv* environment, jclass type, jlong session, jint value)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.network_session_set_max_gamers((CNA_NetworkSessionHandle)session, (int32_t)value);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionSetPrivateGamerSlots(
    JNIEnv* environment, jclass type, jlong session, jint value)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.network_session_set_private_gamer_slots((CNA_NetworkSessionHandle)session, (int32_t)value);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionSetSimulatedLatencyTicks(
    JNIEnv* environment, jclass type, jlong session, jlong ticks)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.network_session_set_simulated_latency_ticks((CNA_NetworkSessionHandle)session, (int64_t)ticks);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionSetSimulatedPacketLoss(
    JNIEnv* environment, jclass type, jlong session, jfloat value)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.network_session_set_simulated_packet_loss((CNA_NetworkSessionHandle)session, (float)value);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionStartGame(
    JNIEnv* environment, jclass type, jlong session)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.network_session_start_game((CNA_NetworkSessionHandle)session);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionUnsubscribe(
    JNIEnv* environment, jclass type, jlong registration)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.network_session_unsubscribe((CNA_NetworkSessionEventRegistrationHandle)registration);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_networkSessionUpdate(
    JNIEnv* environment, jclass type, jlong session)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.network_session_update((CNA_NetworkSessionHandle)session);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_packetReaderCreate(
    JNIEnv* environment, jclass type, jint capacity, jlongArray out_reader)
{
    (void)environment;
    (void)type;
    CNA_PacketReaderHandle out_reader_value = 0;
    CNA_Result result = cna.packet_reader_create((int32_t)capacity, &out_reader_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_reader_element = (jlong)out_reader_value;
        (*environment)->SetLongArrayRegion(
            environment, out_reader, 0, 1, &out_reader_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_packetReaderDestroy(
    JNIEnv* environment, jclass type, jlong reader)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.packet_reader_destroy((CNA_PacketReaderHandle)reader);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_packetReaderGetLength(
    JNIEnv* environment, jclass type, jlong reader, jintArray out_length)
{
    (void)environment;
    (void)type;
    int32_t out_length_value = 0;
    CNA_Result result = cna.packet_reader_get_length((CNA_PacketReaderHandle)reader, &out_length_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_length_element = (jint)out_length_value;
        (*environment)->SetIntArrayRegion(
            environment, out_length, 0, 1, &out_length_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_packetReaderGetPosition(
    JNIEnv* environment, jclass type, jlong reader, jintArray out_position)
{
    (void)environment;
    (void)type;
    int32_t out_position_value = 0;
    CNA_Result result = cna.packet_reader_get_position((CNA_PacketReaderHandle)reader, &out_position_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_position_element = (jint)out_position_value;
        (*environment)->SetIntArrayRegion(
            environment, out_position, 0, 1, &out_position_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_packetReaderReadColor(
    JNIEnv* environment, jclass type, jlong reader, jlongArray out_valueIntegral)
{
    (void)environment;
    (void)type;
    CNA_Color out_value_value;
    memset(&out_value_value, 0, sizeof out_value_value);
    CNA_Result result = cna.packet_reader_read_color((CNA_PacketReaderHandle)reader, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_valueIntegral_values[] = {
            (jlong)out_value_value.r,
            (jlong)out_value_value.g,
            (jlong)out_value_value.b,
            (jlong)out_value_value.a
        };
        (*environment)->SetLongArrayRegion(environment, out_valueIntegral, 0,
            (jsize)(sizeof out_valueIntegral_values / sizeof out_valueIntegral_values[0]),
            out_valueIntegral_values);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_packetReaderReadDouble(
    JNIEnv* environment, jclass type, jlong reader, jdoubleArray out_value)
{
    (void)environment;
    (void)type;
    double out_value_value = 0;
    CNA_Result result = cna.packet_reader_read_double((CNA_PacketReaderHandle)reader, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jdouble out_value_element = (jdouble)out_value_value;
        (*environment)->SetDoubleArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_packetReaderReadMatrix(
    JNIEnv* environment, jclass type, jlong reader, jfloatArray out_valueFloating)
{
    (void)environment;
    (void)type;
    CNA_Matrix out_value_value;
    memset(&out_value_value, 0, sizeof out_value_value);
    CNA_Result result = cna.packet_reader_read_matrix((CNA_PacketReaderHandle)reader, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jfloat out_valueFloating_values[] = {
            (jfloat)out_value_value.m11,
            (jfloat)out_value_value.m12,
            (jfloat)out_value_value.m13,
            (jfloat)out_value_value.m14,
            (jfloat)out_value_value.m21,
            (jfloat)out_value_value.m22,
            (jfloat)out_value_value.m23,
            (jfloat)out_value_value.m24,
            (jfloat)out_value_value.m31,
            (jfloat)out_value_value.m32,
            (jfloat)out_value_value.m33,
            (jfloat)out_value_value.m34,
            (jfloat)out_value_value.m41,
            (jfloat)out_value_value.m42,
            (jfloat)out_value_value.m43,
            (jfloat)out_value_value.m44
        };
        (*environment)->SetFloatArrayRegion(environment, out_valueFloating, 0,
            (jsize)(sizeof out_valueFloating_values / sizeof out_valueFloating_values[0]),
            out_valueFloating_values);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_packetReaderReadQuaternion(
    JNIEnv* environment, jclass type, jlong reader, jfloatArray out_valueFloating)
{
    (void)environment;
    (void)type;
    CNA_Quaternion out_value_value;
    memset(&out_value_value, 0, sizeof out_value_value);
    CNA_Result result = cna.packet_reader_read_quaternion((CNA_PacketReaderHandle)reader, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jfloat out_valueFloating_values[] = {
            (jfloat)out_value_value.x,
            (jfloat)out_value_value.y,
            (jfloat)out_value_value.z,
            (jfloat)out_value_value.w
        };
        (*environment)->SetFloatArrayRegion(environment, out_valueFloating, 0,
            (jsize)(sizeof out_valueFloating_values / sizeof out_valueFloating_values[0]),
            out_valueFloating_values);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_packetReaderReadSingle(
    JNIEnv* environment, jclass type, jlong reader, jfloatArray out_value)
{
    (void)environment;
    (void)type;
    float out_value_value = 0;
    CNA_Result result = cna.packet_reader_read_single((CNA_PacketReaderHandle)reader, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jfloat out_value_element = (jfloat)out_value_value;
        (*environment)->SetFloatArrayRegion(
            environment, out_value, 0, 1, &out_value_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_packetReaderReadVector2(
    JNIEnv* environment, jclass type, jlong reader, jfloatArray out_valueFloating)
{
    (void)environment;
    (void)type;
    CNA_Vector2 out_value_value;
    memset(&out_value_value, 0, sizeof out_value_value);
    CNA_Result result = cna.packet_reader_read_vector2((CNA_PacketReaderHandle)reader, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jfloat out_valueFloating_values[] = {
            (jfloat)out_value_value.x,
            (jfloat)out_value_value.y
        };
        (*environment)->SetFloatArrayRegion(environment, out_valueFloating, 0,
            (jsize)(sizeof out_valueFloating_values / sizeof out_valueFloating_values[0]),
            out_valueFloating_values);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_packetReaderReadVector3(
    JNIEnv* environment, jclass type, jlong reader, jfloatArray out_valueFloating)
{
    (void)environment;
    (void)type;
    CNA_Vector3 out_value_value;
    memset(&out_value_value, 0, sizeof out_value_value);
    CNA_Result result = cna.packet_reader_read_vector3((CNA_PacketReaderHandle)reader, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jfloat out_valueFloating_values[] = {
            (jfloat)out_value_value.x,
            (jfloat)out_value_value.y,
            (jfloat)out_value_value.z
        };
        (*environment)->SetFloatArrayRegion(environment, out_valueFloating, 0,
            (jsize)(sizeof out_valueFloating_values / sizeof out_valueFloating_values[0]),
            out_valueFloating_values);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_packetReaderReadVector4(
    JNIEnv* environment, jclass type, jlong reader, jfloatArray out_valueFloating)
{
    (void)environment;
    (void)type;
    CNA_Vector4 out_value_value;
    memset(&out_value_value, 0, sizeof out_value_value);
    CNA_Result result = cna.packet_reader_read_vector4((CNA_PacketReaderHandle)reader, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jfloat out_valueFloating_values[] = {
            (jfloat)out_value_value.x,
            (jfloat)out_value_value.y,
            (jfloat)out_value_value.z,
            (jfloat)out_value_value.w
        };
        (*environment)->SetFloatArrayRegion(environment, out_valueFloating, 0,
            (jsize)(sizeof out_valueFloating_values / sizeof out_valueFloating_values[0]),
            out_valueFloating_values);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_packetReaderSetDataExt(
    JNIEnv* environment, jclass type, jlong reader, jbyteArray data)
{
    (void)environment;
    (void)type;
    jsize data_size = (*environment)->GetArrayLength(environment, data);
    jbyte* data_elements = (*environment)->GetByteArrayElements(environment, data, NULL);
    if (data_elements == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint8_t* data_values = (uint8_t*)malloc(
        ((size_t)data_size + 1U) * sizeof(*data_values));
    if (data_values == NULL) {
        (*environment)->ReleaseByteArrayElements(
            environment, data, data_elements, JNI_ABORT);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    for (jsize index = 0; index < data_size; ++index) {
        data_values[index] = (uint8_t)data_elements[index];
    }
    CNA_Result result = cna.packet_reader_set_data_ext((CNA_PacketReaderHandle)reader, data_values, (uint64_t)data_size);
    free(data_values);
    (*environment)->ReleaseByteArrayElements(
        environment, data, data_elements, JNI_ABORT);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_packetReaderSetPosition(
    JNIEnv* environment, jclass type, jlong reader, jint position)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.packet_reader_set_position((CNA_PacketReaderHandle)reader, (int32_t)position);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_packetWriterCopyDataExt(
    JNIEnv* environment, jclass type, jlong writer, jbyteArray destination, jlongArray out_bytes)
{
    (void)environment;
    (void)type;
    jsize destination_size = (*environment)->GetArrayLength(environment, destination);
    jbyte* destination_elements = (*environment)->GetByteArrayElements(environment, destination, NULL);
    if (destination_elements == NULL) {
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    uint8_t* destination_values = (uint8_t*)malloc(
        ((size_t)destination_size + 1U) * sizeof(*destination_values));
    if (destination_values == NULL) {
        (*environment)->ReleaseByteArrayElements(
            environment, destination, destination_elements, JNI_ABORT);
        return (jint)CNA_RESULT_OUT_OF_MEMORY;
    }
    for (jsize index = 0; index < destination_size; ++index) {
        destination_values[index] = (uint8_t)destination_elements[index];
    }
    uint64_t out_bytes_value = 0;
    CNA_Result result = cna.packet_writer_copy_data_ext((CNA_PacketWriterHandle)writer, destination_values, (uint64_t)destination_size, &out_bytes_value);
    if (result == CNA_RESULT_SUCCESS) {
        for (jsize index = 0; index < destination_size; ++index) {
            destination_elements[index] = (jbyte)destination_values[index];
        }
    }
    free(destination_values);
    (*environment)->ReleaseByteArrayElements(
        environment, destination, destination_elements, 0);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_bytes_element = (jlong)out_bytes_value;
        (*environment)->SetLongArrayRegion(
            environment, out_bytes, 0, 1, &out_bytes_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_packetWriterCreate(
    JNIEnv* environment, jclass type, jint capacity, jlongArray out_writer)
{
    (void)environment;
    (void)type;
    CNA_PacketWriterHandle out_writer_value = 0;
    CNA_Result result = cna.packet_writer_create((int32_t)capacity, &out_writer_value);
    if (result == CNA_RESULT_SUCCESS) {
        jlong out_writer_element = (jlong)out_writer_value;
        (*environment)->SetLongArrayRegion(
            environment, out_writer, 0, 1, &out_writer_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_packetWriterDestroy(
    JNIEnv* environment, jclass type, jlong writer)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.packet_writer_destroy((CNA_PacketWriterHandle)writer);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_packetWriterGetLength(
    JNIEnv* environment, jclass type, jlong writer, jintArray out_length)
{
    (void)environment;
    (void)type;
    int32_t out_length_value = 0;
    CNA_Result result = cna.packet_writer_get_length((CNA_PacketWriterHandle)writer, &out_length_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_length_element = (jint)out_length_value;
        (*environment)->SetIntArrayRegion(
            environment, out_length, 0, 1, &out_length_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_packetWriterGetPosition(
    JNIEnv* environment, jclass type, jlong writer, jintArray out_position)
{
    (void)environment;
    (void)type;
    int32_t out_position_value = 0;
    CNA_Result result = cna.packet_writer_get_position((CNA_PacketWriterHandle)writer, &out_position_value);
    if (result == CNA_RESULT_SUCCESS) {
        jint out_position_element = (jint)out_position_value;
        (*environment)->SetIntArrayRegion(
            environment, out_position, 0, 1, &out_position_element);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_packetWriterSetPosition(
    JNIEnv* environment, jclass type, jlong writer, jint position)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.packet_writer_set_position((CNA_PacketWriterHandle)writer, (int32_t)position);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_packetWriterWriteColor(
    JNIEnv* environment, jclass type, jlong writer, jlongArray valueIntegral)
{
    (void)environment;
    (void)type;
    CNA_Color value_value;
    memset(&value_value, 0, sizeof value_value);
    {
        jlong valueIntegral_values[4];
        (*environment)->GetLongArrayRegion(environment, valueIntegral, 0, 4, valueIntegral_values);
        value_value.r = (uint8_t)valueIntegral_values[0];
        value_value.g = (uint8_t)valueIntegral_values[1];
        value_value.b = (uint8_t)valueIntegral_values[2];
        value_value.a = (uint8_t)valueIntegral_values[3];
    }
    CNA_Result result = cna.packet_writer_write_color((CNA_PacketWriterHandle)writer, value_value);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_packetWriterWriteDouble(
    JNIEnv* environment, jclass type, jlong writer, jdouble value)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.packet_writer_write_double((CNA_PacketWriterHandle)writer, (double)value);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_packetWriterWriteMatrix(
    JNIEnv* environment, jclass type, jlong writer, jfloatArray valueFloating)
{
    (void)environment;
    (void)type;
    CNA_Matrix value_value;
    memset(&value_value, 0, sizeof value_value);
    {
        jfloat valueFloating_values[16];
        (*environment)->GetFloatArrayRegion(environment, valueFloating, 0, 16, valueFloating_values);
        value_value.m11 = (float)valueFloating_values[0];
        value_value.m12 = (float)valueFloating_values[1];
        value_value.m13 = (float)valueFloating_values[2];
        value_value.m14 = (float)valueFloating_values[3];
        value_value.m21 = (float)valueFloating_values[4];
        value_value.m22 = (float)valueFloating_values[5];
        value_value.m23 = (float)valueFloating_values[6];
        value_value.m24 = (float)valueFloating_values[7];
        value_value.m31 = (float)valueFloating_values[8];
        value_value.m32 = (float)valueFloating_values[9];
        value_value.m33 = (float)valueFloating_values[10];
        value_value.m34 = (float)valueFloating_values[11];
        value_value.m41 = (float)valueFloating_values[12];
        value_value.m42 = (float)valueFloating_values[13];
        value_value.m43 = (float)valueFloating_values[14];
        value_value.m44 = (float)valueFloating_values[15];
    }
    CNA_Result result = cna.packet_writer_write_matrix((CNA_PacketWriterHandle)writer, value_value);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_packetWriterWriteQuaternion(
    JNIEnv* environment, jclass type, jlong writer, jfloatArray valueFloating)
{
    (void)environment;
    (void)type;
    CNA_Quaternion value_value;
    memset(&value_value, 0, sizeof value_value);
    {
        jfloat valueFloating_values[4];
        (*environment)->GetFloatArrayRegion(environment, valueFloating, 0, 4, valueFloating_values);
        value_value.x = (float)valueFloating_values[0];
        value_value.y = (float)valueFloating_values[1];
        value_value.z = (float)valueFloating_values[2];
        value_value.w = (float)valueFloating_values[3];
    }
    CNA_Result result = cna.packet_writer_write_quaternion((CNA_PacketWriterHandle)writer, value_value);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_packetWriterWriteSingle(
    JNIEnv* environment, jclass type, jlong writer, jfloat value)
{
    (void)environment;
    (void)type;
    CNA_Result result = cna.packet_writer_write_single((CNA_PacketWriterHandle)writer, (float)value);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_packetWriterWriteVector2(
    JNIEnv* environment, jclass type, jlong writer, jfloatArray valueFloating)
{
    (void)environment;
    (void)type;
    CNA_Vector2 value_value;
    memset(&value_value, 0, sizeof value_value);
    {
        jfloat valueFloating_values[2];
        (*environment)->GetFloatArrayRegion(environment, valueFloating, 0, 2, valueFloating_values);
        value_value.x = (float)valueFloating_values[0];
        value_value.y = (float)valueFloating_values[1];
    }
    CNA_Result result = cna.packet_writer_write_vector2((CNA_PacketWriterHandle)writer, value_value);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_packetWriterWriteVector3(
    JNIEnv* environment, jclass type, jlong writer, jfloatArray valueFloating)
{
    (void)environment;
    (void)type;
    CNA_Vector3 value_value;
    memset(&value_value, 0, sizeof value_value);
    {
        jfloat valueFloating_values[3];
        (*environment)->GetFloatArrayRegion(environment, valueFloating, 0, 3, valueFloating_values);
        value_value.x = (float)valueFloating_values[0];
        value_value.y = (float)valueFloating_values[1];
        value_value.z = (float)valueFloating_values[2];
    }
    CNA_Result result = cna.packet_writer_write_vector3((CNA_PacketWriterHandle)writer, value_value);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_packetWriterWriteVector4(
    JNIEnv* environment, jclass type, jlong writer, jfloatArray valueFloating)
{
    (void)environment;
    (void)type;
    CNA_Vector4 value_value;
    memset(&value_value, 0, sizeof value_value);
    {
        jfloat valueFloating_values[4];
        (*environment)->GetFloatArrayRegion(environment, valueFloating, 0, 4, valueFloating_values);
        value_value.x = (float)valueFloating_values[0];
        value_value.y = (float)valueFloating_values[1];
        value_value.z = (float)valueFloating_values[2];
        value_value.w = (float)valueFloating_values[3];
    }
    CNA_Result result = cna.packet_writer_write_vector4((CNA_PacketWriterHandle)writer, value_value);
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_qualityOfServiceInit(
    JNIEnv* environment, jclass type, jbyteArray out_valueBytes, jlongArray out_valueIntegral)
{
    (void)environment;
    (void)type;
    CNA_QualityOfService out_value_value;
    memset(&out_value_value, 0, sizeof out_value_value);
    out_value_value.struct_size = (uint32_t)(sizeof out_value_value);
    out_value_value.struct_version = UINT32_C(1);
    CNA_Result result = cna.quality_of_service_init(&out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jbyte out_valueBytes_values[] = {
            (jbyte)out_value_value.reserved[0],
            (jbyte)out_value_value.reserved[1],
            (jbyte)out_value_value.reserved[2],
            (jbyte)out_value_value.reserved[3],
            (jbyte)out_value_value.reserved[4],
            (jbyte)out_value_value.reserved[5],
            (jbyte)out_value_value.reserved[6]
        };
        (*environment)->SetByteArrayRegion(environment, out_valueBytes, 0,
            (jsize)(sizeof out_valueBytes_values / sizeof out_valueBytes_values[0]),
            out_valueBytes_values);
        jlong out_valueIntegral_values[] = {
            (jlong)out_value_value.is_available,
            (jlong)out_value_value.average_roundtrip_ticks,
            (jlong)out_value_value.minimum_roundtrip_ticks,
            (jlong)out_value_value.bytes_per_second_downstream,
            (jlong)out_value_value.bytes_per_second_upstream
        };
        (*environment)->SetLongArrayRegion(environment, out_valueIntegral, 0,
            (jsize)(sizeof out_valueIntegral_values / sizeof out_valueIntegral_values[0]),
            out_valueIntegral_values);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_qualityOfServiceInitMeasured(
    JNIEnv* environment, jclass type, jlong roundtrip_ticks, jbyteArray out_valueBytes, jlongArray out_valueIntegral)
{
    (void)environment;
    (void)type;
    CNA_QualityOfService out_value_value;
    memset(&out_value_value, 0, sizeof out_value_value);
    out_value_value.struct_size = (uint32_t)(sizeof out_value_value);
    out_value_value.struct_version = UINT32_C(1);
    CNA_Result result = cna.quality_of_service_init_measured((int64_t)roundtrip_ticks, &out_value_value);
    if (result == CNA_RESULT_SUCCESS) {
        jbyte out_valueBytes_values[] = {
            (jbyte)out_value_value.reserved[0],
            (jbyte)out_value_value.reserved[1],
            (jbyte)out_value_value.reserved[2],
            (jbyte)out_value_value.reserved[3],
            (jbyte)out_value_value.reserved[4],
            (jbyte)out_value_value.reserved[5],
            (jbyte)out_value_value.reserved[6]
        };
        (*environment)->SetByteArrayRegion(environment, out_valueBytes, 0,
            (jsize)(sizeof out_valueBytes_values / sizeof out_valueBytes_values[0]),
            out_valueBytes_values);
        jlong out_valueIntegral_values[] = {
            (jlong)out_value_value.is_available,
            (jlong)out_value_value.average_roundtrip_ticks,
            (jlong)out_value_value.minimum_roundtrip_ticks,
            (jlong)out_value_value.bytes_per_second_downstream,
            (jlong)out_value_value.bytes_per_second_upstream
        };
        (*environment)->SetLongArrayRegion(environment, out_valueIntegral, 0,
            (jsize)(sizeof out_valueIntegral_values / sizeof out_valueIntegral_values[0]),
            out_valueIntegral_values);
    }
    return (jint)result;
}

JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_generated_NativeNetworkRoutes_writeLeaderboardsEventInfoInit(
    JNIEnv* environment, jclass type, jlong gamer, jboolean is_leaving, jbyteArray out_infoBytes, jlongArray out_infoIntegral)
{
    (void)environment;
    (void)type;
    CNA_WriteLeaderboardsEventInfo out_info_value;
    memset(&out_info_value, 0, sizeof out_info_value);
    out_info_value.struct_size = (uint32_t)(sizeof out_info_value);
    out_info_value.struct_version = UINT32_C(1);
    CNA_Result result = cna.write_leaderboards_event_info_init((CNA_NetworkGamerHandle)gamer, (CNA_Bool)is_leaving, &out_info_value);
    if (result == CNA_RESULT_SUCCESS) {
        jbyte out_infoBytes_values[] = {
            (jbyte)out_info_value.reserved[0],
            (jbyte)out_info_value.reserved[1],
            (jbyte)out_info_value.reserved[2],
            (jbyte)out_info_value.reserved[3],
            (jbyte)out_info_value.reserved[4],
            (jbyte)out_info_value.reserved[5],
            (jbyte)out_info_value.reserved[6]
        };
        (*environment)->SetByteArrayRegion(environment, out_infoBytes, 0,
            (jsize)(sizeof out_infoBytes_values / sizeof out_infoBytes_values[0]),
            out_infoBytes_values);
        jlong out_infoIntegral_values[] = {
            (jlong)out_info_value.gamer,
            (jlong)out_info_value.is_leaving
        };
        (*environment)->SetLongArrayRegion(environment, out_infoIntegral, 0,
            (jsize)(sizeof out_infoIntegral_values / sizeof out_infoIntegral_values[0]),
            out_infoIntegral_values);
    }
    return (jint)result;
}
