/* SPDX-License-Identifier: MS-PL */
/*
 * Hand-written JNI for CNA's gamer-service and network-session events.
 *
 * These are the routes the generator refuses on purpose: each takes a real C callback, and a
 * generated wrapper cannot decide what that callback should do. What it does here is the same
 * thing the media and storage families already do -- record the event and let Java drain it --
 * rather than calling into the JVM from whichever thread CNA happened to raise it on.
 *
 * The queue is process-wide because the sign-in and invite events are: CNA raises them without
 * a session. Java drains it immediately after pumping the dispatcher or the session, so an
 * event is delivered on the game thread, during Update, which is where XNA raises it too.
 *
 * A full queue drops the oldest record and counts the drop. Growing without bound in a callback
 * CNA may raise from any thread would be the wrong trade, and reporting the loss is better than
 * hiding it.
 */

#define CNA_JAVA_EVENT_CAPACITY 256

/* Event kinds. Java reads these from NativeGamerServices, which declares the same numbers. */
#define CNA_JAVA_EVENT_SIGNED_IN 0
#define CNA_JAVA_EVENT_SIGNED_OUT 1
#define CNA_JAVA_EVENT_INSTALLING_TITLE_UPDATE 2
#define CNA_JAVA_EVENT_GAME_ENDED 10
#define CNA_JAVA_EVENT_GAME_STARTED 11
#define CNA_JAVA_EVENT_GAMER_JOINED 12
#define CNA_JAVA_EVENT_GAMER_LEFT 13
#define CNA_JAVA_EVENT_HOST_CHANGED 14
#define CNA_JAVA_EVENT_SESSION_ENDED 15
#define CNA_JAVA_EVENT_WRITE_ARBITRATED 16
#define CNA_JAVA_EVENT_WRITE_TRUE_SKILL 17
#define CNA_JAVA_EVENT_WRITE_UNARBITRATED 18
#define CNA_JAVA_EVENT_INVITE_ACCEPTED 19

typedef struct JavaGamerEvent {
    int64_t kind;
    int64_t session;
    int64_t first;
    int64_t second;
    int64_t flag;
} JavaGamerEvent;

static JavaGamerEvent java_gamer_events[CNA_JAVA_EVENT_CAPACITY];
static int java_gamer_event_head;
static int java_gamer_event_count;
static int64_t java_gamer_events_dropped;
static atomic_flag java_gamer_event_lock = ATOMIC_FLAG_INIT;

static void java_gamer_event_enter(void)
{
    while (atomic_flag_test_and_set(&java_gamer_event_lock)) {
        /* The critical section is a handful of stores; spinning is cheaper than a mutex. */
    }
}

static void java_gamer_event_leave(void)
{
    atomic_flag_clear(&java_gamer_event_lock);
}

static void java_gamer_event_push(
    int64_t kind, int64_t session, int64_t first, int64_t second, int64_t flag)
{
    java_gamer_event_enter();
    if (java_gamer_event_count == CNA_JAVA_EVENT_CAPACITY) {
        java_gamer_event_head = (java_gamer_event_head + 1) % CNA_JAVA_EVENT_CAPACITY;
        java_gamer_event_count--;
        java_gamer_events_dropped++;
    }
    int index = (java_gamer_event_head + java_gamer_event_count) % CNA_JAVA_EVENT_CAPACITY;
    java_gamer_events[index].kind = kind;
    java_gamer_events[index].session = session;
    java_gamer_events[index].first = first;
    java_gamer_events[index].second = second;
    java_gamer_events[index].flag = flag;
    java_gamer_event_count++;
    java_gamer_event_leave();
}

static void java_signed_in_callback(void* context, const CNA_SignedInGamerEventInfo* info)
{
    (void)context;
    java_gamer_event_push(CNA_JAVA_EVENT_SIGNED_IN, 0,
        info == NULL ? 0 : (int64_t)info->gamer, 0, 0);
}

static void java_signed_out_callback(void* context, const CNA_SignedInGamerEventInfo* info)
{
    (void)context;
    java_gamer_event_push(CNA_JAVA_EVENT_SIGNED_OUT, 0,
        info == NULL ? 0 : (int64_t)info->gamer, 0, 0);
}

static void java_installing_title_update_callback(void* context)
{
    (void)context;
    java_gamer_event_push(CNA_JAVA_EVENT_INSTALLING_TITLE_UPDATE, 0, 0, 0, 0);
}

static void java_game_ended_callback(
    CNA_NetworkSessionHandle session, const CNA_GameEndedEventInfo* info, void* context)
{
    (void)info;
    (void)context;
    java_gamer_event_push(CNA_JAVA_EVENT_GAME_ENDED, (int64_t)session, 0, 0, 0);
}

static void java_game_started_callback(
    CNA_NetworkSessionHandle session, const CNA_GameStartedEventInfo* info, void* context)
{
    (void)info;
    (void)context;
    java_gamer_event_push(CNA_JAVA_EVENT_GAME_STARTED, (int64_t)session, 0, 0, 0);
}

static void java_gamer_joined_callback(
    CNA_NetworkSessionHandle session, const CNA_GamerJoinedEventInfo* info, void* context)
{
    (void)context;
    java_gamer_event_push(CNA_JAVA_EVENT_GAMER_JOINED, (int64_t)session,
        info == NULL ? 0 : (int64_t)info->gamer, 0, 0);
}

static void java_gamer_left_callback(
    CNA_NetworkSessionHandle session, const CNA_GamerLeftEventInfo* info, void* context)
{
    (void)context;
    java_gamer_event_push(CNA_JAVA_EVENT_GAMER_LEFT, (int64_t)session,
        info == NULL ? 0 : (int64_t)info->gamer, 0, 0);
}

static void java_host_changed_callback(
    CNA_NetworkSessionHandle session, const CNA_HostChangedEventInfo* info, void* context)
{
    (void)context;
    java_gamer_event_push(CNA_JAVA_EVENT_HOST_CHANGED, (int64_t)session,
        info == NULL ? 0 : (int64_t)info->old_host,
        info == NULL ? 0 : (int64_t)info->new_host, 0);
}

static void java_session_ended_callback(
    CNA_NetworkSessionHandle session, const CNA_NetworkSessionEndedEventInfo* info, void* context)
{
    (void)context;
    java_gamer_event_push(CNA_JAVA_EVENT_SESSION_ENDED, (int64_t)session, 0, 0,
        info == NULL ? 0 : (int64_t)info->end_reason);
}

static void java_write_leaderboards_push(
    int64_t kind, CNA_NetworkSessionHandle session, const CNA_WriteLeaderboardsEventInfo* info)
{
    java_gamer_event_push(kind, (int64_t)session,
        info == NULL ? 0 : (int64_t)info->gamer, 0,
        info == NULL ? 0 : (int64_t)info->is_leaving);
}

static void java_write_arbitrated_callback(
    CNA_NetworkSessionHandle session, const CNA_WriteLeaderboardsEventInfo* info, void* context)
{
    (void)context;
    java_write_leaderboards_push(CNA_JAVA_EVENT_WRITE_ARBITRATED, session, info);
}

static void java_write_true_skill_callback(
    CNA_NetworkSessionHandle session, const CNA_WriteLeaderboardsEventInfo* info, void* context)
{
    (void)context;
    java_write_leaderboards_push(CNA_JAVA_EVENT_WRITE_TRUE_SKILL, session, info);
}

static void java_write_unarbitrated_callback(
    CNA_NetworkSessionHandle session, const CNA_WriteLeaderboardsEventInfo* info, void* context)
{
    (void)context;
    java_write_leaderboards_push(CNA_JAVA_EVENT_WRITE_UNARBITRATED, session, info);
}

static void java_invite_accepted_callback(
    const CNA_InviteAcceptedEventInfo* info, void* context)
{
    (void)context;
    java_gamer_event_push(CNA_JAVA_EVENT_INVITE_ACCEPTED, 0,
        info == NULL ? 0 : (int64_t)info->gamer, 0,
        info == NULL ? 0 : (int64_t)info->is_current_session);
}

/*
 * Subscribes the three process-wide gamer-service events, returning their registrations.
 *
 * Java calls this once and keeps the registrations; a second call would deliver every event
 * twice, so the Java side, not this function, owns that decision.
 */
JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_NativeGamerServices_nativeSubscribeGamerEvents(
    JNIEnv* environment, jclass type, jlongArray out_registrations)
{
    (void)type;
    CNA_Handle signed_in = CNA_INVALID_HANDLE;
    CNA_Handle signed_out = CNA_INVALID_HANDLE;
    CNA_Handle title_update = CNA_INVALID_HANDLE;
    CNA_Result result = cna.signed_in_gamer_subscribe_signed_in_ext(
        java_signed_in_callback, NULL, &signed_in);
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.signed_in_gamer_subscribe_signed_out_ext(
            java_signed_out_callback, NULL, &signed_out);
    }
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.gamer_services_dispatcher_subscribe_installing_title_update_ext(
            java_installing_title_update_callback, NULL, &title_update);
    }
    if (result != CNA_RESULT_SUCCESS) {
        /* Roll back whatever did register, so a partial subscription never survives. */
        if (title_update != CNA_INVALID_HANDLE) {
            (void)cna.gamer_unsubscribe_ext(title_update);
        }
        if (signed_out != CNA_INVALID_HANDLE) {
            (void)cna.gamer_unsubscribe_ext(signed_out);
        }
        if (signed_in != CNA_INVALID_HANDLE) {
            (void)cna.gamer_unsubscribe_ext(signed_in);
        }
        return (jint)result;
    }
    jlong registrations[] = {(jlong)signed_in, (jlong)signed_out, (jlong)title_update};
    (*environment)->SetLongArrayRegion(environment, out_registrations, 0, 3, registrations);
    return (jint)CNA_RESULT_SUCCESS;
}

/** Releases the three process-wide gamer-service registrations. */
JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_NativeGamerServices_nativeUnsubscribeGamerEvents(
    JNIEnv* environment, jclass type, jlongArray registrations)
{
    (void)type;
    jlong values[3] = {0, 0, 0};
    (*environment)->GetLongArrayRegion(environment, registrations, 0, 3, values);
    CNA_Result first = CNA_RESULT_SUCCESS;
    for (int index = 2; index >= 0; --index) {
        if (values[index] == 0) {
            continue;
        }
        CNA_Result result = cna.gamer_unsubscribe_ext((CNA_Handle)values[index]);
        if (result != CNA_RESULT_SUCCESS && first == CNA_RESULT_SUCCESS) {
            first = result;
        }
    }
    return (jint)first;
}

/*
 * Subscribes all nine session events plus the static accepted-invite event.
 *
 * Every one of them or none: a session that raised only some of its events would be worse than
 * one that raised none, because a game cannot tell which it got.
 */
JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_NativeGamerServices_nativeSubscribeSessionEvents(
    JNIEnv* environment, jclass type, jlong session, jlongArray out_registrations)
{
    (void)type;
    CNA_NetworkSessionHandle handle = (CNA_NetworkSessionHandle)session;
    CNA_NetworkSessionEventRegistrationHandle registrations[10];
    for (int index = 0; index < 10; ++index) {
        registrations[index] = CNA_INVALID_HANDLE;
    }
    CNA_Result result = cna.network_session_subscribe_game_ended(
        handle, java_game_ended_callback, NULL, &registrations[0]);
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.network_session_subscribe_game_started(
            handle, java_game_started_callback, NULL, &registrations[1]);
    }
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.network_session_subscribe_gamer_joined(
            handle, java_gamer_joined_callback, NULL, &registrations[2]);
    }
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.network_session_subscribe_gamer_left(
            handle, java_gamer_left_callback, NULL, &registrations[3]);
    }
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.network_session_subscribe_host_changed(
            handle, java_host_changed_callback, NULL, &registrations[4]);
    }
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.network_session_subscribe_session_ended(
            handle, java_session_ended_callback, NULL, &registrations[5]);
    }
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.network_session_subscribe_write_arbitrated_leaderboard(
            handle, java_write_arbitrated_callback, NULL, &registrations[6]);
    }
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.network_session_subscribe_write_true_skill(
            handle, java_write_true_skill_callback, NULL, &registrations[7]);
    }
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.network_session_subscribe_write_unarbitrated_leaderboard(
            handle, java_write_unarbitrated_callback, NULL, &registrations[8]);
    }
    if (result == CNA_RESULT_SUCCESS) {
        result = cna.network_session_subscribe_invite_accepted(
            java_invite_accepted_callback, NULL, &registrations[9]);
    }
    if (result != CNA_RESULT_SUCCESS) {
        for (int index = 9; index >= 0; --index) {
            if (registrations[index] != CNA_INVALID_HANDLE) {
                (void)cna.network_session_unsubscribe(registrations[index]);
            }
        }
        return (jint)result;
    }
    jlong values[10];
    for (int index = 0; index < 10; ++index) {
        values[index] = (jlong)registrations[index];
    }
    (*environment)->SetLongArrayRegion(environment, out_registrations, 0, 10, values);
    return (jint)CNA_RESULT_SUCCESS;
}

/** Releases one session's ten registrations, in reverse subscription order. */
JNIEXPORT jint JNICALL
Java_org_openeggbert_cna_internal_NativeGamerServices_nativeUnsubscribeSessionEvents(
    JNIEnv* environment, jclass type, jlongArray registrations)
{
    (void)type;
    jlong values[10];
    (*environment)->GetLongArrayRegion(environment, registrations, 0, 10, values);
    CNA_Result first = CNA_RESULT_SUCCESS;
    for (int index = 9; index >= 0; --index) {
        if (values[index] == 0) {
            continue;
        }
        CNA_Result result = cna.network_session_unsubscribe(
            (CNA_NetworkSessionEventRegistrationHandle)values[index]);
        if (result != CNA_RESULT_SUCCESS && first == CNA_RESULT_SUCCESS) {
            first = result;
        }
    }
    return (jint)first;
}

/*
 * Drains one queued event.
 *
 * Returns JNI_TRUE when a record was written. Java drains in a loop right after pumping, so
 * every event CNA raised during that pump is delivered on the game thread before the game sees
 * the next frame.
 */
JNIEXPORT jboolean JNICALL
Java_org_openeggbert_cna_internal_NativeGamerServices_nativePollEvent(
    JNIEnv* environment, jclass type, jlongArray out_record)
{
    (void)type;
    JavaGamerEvent event;
    java_gamer_event_enter();
    if (java_gamer_event_count == 0) {
        java_gamer_event_leave();
        return JNI_FALSE;
    }
    event = java_gamer_events[java_gamer_event_head];
    java_gamer_event_head = (java_gamer_event_head + 1) % CNA_JAVA_EVENT_CAPACITY;
    java_gamer_event_count--;
    java_gamer_event_leave();

    jlong values[] = {event.kind, event.session, event.first, event.second, event.flag};
    (*environment)->SetLongArrayRegion(environment, out_record, 0, 5, values);
    return JNI_TRUE;
}

/** Returns how many events were dropped because the queue was full, for diagnostics. */
JNIEXPORT jlong JNICALL
Java_org_openeggbert_cna_internal_NativeGamerServices_nativeDroppedEventCount(
    JNIEnv* environment, jclass type)
{
    (void)environment;
    (void)type;
    java_gamer_event_enter();
    int64_t dropped = java_gamer_events_dropped;
    java_gamer_event_leave();
    return (jlong)dropped;
}

/** Discards every queued event. Used when a game lifetime ends. */
JNIEXPORT void JNICALL
Java_org_openeggbert_cna_internal_NativeGamerServices_nativeResetEvents(
    JNIEnv* environment, jclass type)
{
    (void)environment;
    (void)type;
    java_gamer_event_enter();
    java_gamer_event_head = 0;
    java_gamer_event_count = 0;
    java_gamer_events_dropped = 0;
    java_gamer_event_leave();
}
