package org.openeggbert.cna.internal;

import java.util.Objects;

/**
 * Delivers CNA's gamer-service and network-session events to the Java types that own them.
 *
 * <p>CNA may raise these from any thread, so the JNI callbacks record them rather than calling
 * into the JVM. Java drains the record immediately after pumping the dispatcher or the session,
 * which puts every event on the game thread during {@code Update} -- where XNA raises it too.
 *
 * <p>The two handlers are installed by the packages that own the listeners:
 * {@code Microsoft.Xna.Framework.GamerServices} and {@code Microsoft.Xna.Framework.Net}. CLR
 * reaches across those namespaces through assembly-internal access, which Java has no
 * equivalent for. Installing at class initialization is safe because a handler is only needed
 * once one of that package's types exists.
 *
 * <p>This class is not application API.
 */
public final class GamerEventPump {

    /** The lowest event kind that belongs to a network session rather than to gamer services. */
    private static final int FIRST_SESSION_KIND = 10;

    /** The lowest event kind that belongs to the input extensions. */
    private static final int FIRST_INPUT_KIND = 30;

    private static volatile Handler gamerHandler;
    private static volatile Handler sessionHandler;
    private static volatile Handler inputHandler;
    private static volatile boolean subscribed;
    private static long textInputRegistration;

    private GamerEventPump() {
    }

    /** Receives one drained event. */
    public interface Handler {
        void handle(long kind, long session, long first, long second, long flag);
    }

    public static void setGamerHandler(Handler handler) {
        gamerHandler = Objects.requireNonNull(handler, "handler");
    }

    public static void setSessionHandler(Handler handler) {
        sessionHandler = Objects.requireNonNull(handler, "handler");
    }

    public static void setInputHandler(Handler handler) {
        inputHandler = Objects.requireNonNull(handler, "handler");
    }

    /** Subscribes CNA's typed-character event once. */
    public static synchronized void ensureTextInputSubscribed() {
        if (textInputRegistration != 0L) {
            return;
        }
        long[] registration = new long[1];
        NativeGamerServices.check("TextInput events",
                NativeGamerServices.nativeSubscribeTextInput(registration));
        textInputRegistration = registration[0];
    }

    /**
     * Subscribes the process-wide gamer-service events once.
     *
     * <p>Subscribing twice would deliver every event twice, so the decision lives here rather
     * than at each call site.
     */
    public static synchronized void ensureSubscribed() {
        if (subscribed) {
            return;
        }
        long[] registrations = new long[3];
        NativeGamerServices.check("GamerServices events",
                NativeGamerServices.nativeSubscribeGamerEvents(registrations));
        subscribed = true;
    }

    /** Subscribes one session's events and returns its registrations. */
    public static long[] subscribeSession(long session) {
        long[] registrations = new long[10];
        NativeGamerServices.check("NetworkSession events",
                NativeGamerServices.nativeSubscribeSessionEvents(session, registrations));
        return registrations;
    }

    /** Releases one session's registrations. */
    public static void unsubscribeSession(long[] registrations) {
        NativeGamerServices.check("NetworkSession events",
                NativeGamerServices.nativeUnsubscribeSessionEvents(registrations));
    }

    /**
     * Delivers every event queued so far.
     *
     * <p>A listener that throws must not swallow the events behind it, so the first failure is
     * kept, the drain continues, and the failure is rethrown once the queue is empty.
     */
    public static void drain() {
        long[] record = new long[5];
        RuntimeException failure = null;
        while (NativeGamerServices.nativePollEvent(record)) {
            Handler handler;
            if (record[0] >= FIRST_INPUT_KIND) {
                handler = inputHandler;
            } else if (record[0] >= FIRST_SESSION_KIND) {
                handler = sessionHandler;
            } else {
                handler = gamerHandler;
            }
            if (handler == null) {
                continue;
            }
            try {
                handler.handle(record[0], record[1], record[2], record[3], record[4]);
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

    /** Returns how many events CNA raised that the queue could not hold. */
    public static long droppedEventCount() {
        return NativeGamerServices.nativeDroppedEventCount();
    }

    /** Discards every queued event, for a new game lifetime or a test. */
    public static void reset() {
        NativeGamerServices.nativeResetEvents();
    }
}
