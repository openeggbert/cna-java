package org.openeggbert.cna.internal;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Delivers CNA's gamer-service and network-session events to the Java types that own them.
 *
 * <p>CNA may raise these from any thread, so the JNI callbacks record them rather than calling
 * into the JVM. Java drains the record immediately after pumping the dispatcher or the session,
 * which puts every event on the game thread during {@code Update} -- where XNA raises it too.
 *
 * <p>The gamer and session handlers are installed by the packages that own the listeners:
 * {@code Microsoft.Xna.Framework.GamerServices} and {@code Microsoft.Xna.Framework.Net}. CLR
 * reaches across those namespaces through assembly-internal access, which Java has no
 * equivalent for. Installing at class initialization is safe because a handler is only needed
 * once one of that package's types exists.
 *
 * <p>The input range is shared: several CNA extension families raise events there, so it takes a
 * list of handlers and each filters on the kinds it owns.
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
    // Several extension families share the input kind range, and each filters on the kind it
    // owns, so this is a list rather than the single handler the other two ranges use.
    private static final List<Handler> INPUT_HANDLERS = new CopyOnWriteArrayList<>();
    private static volatile boolean subscribed;
    private static long textInputRegistration;
    private static long[] inputDeviceRegistrations;
    private static long[] joystickRegistrations;
    private static long mouseClickedRegistration;

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

    /**
     * Adds a handler for the input kind range.
     *
     * <p>Registering the same handler twice would deliver every event twice, so a family
     * installs its handler once from its own class initializer.
     */
    public static void addInputHandler(Handler handler) {
        INPUT_HANDLERS.add(Objects.requireNonNull(handler, "handler"));
    }

    /**
     * Subscribes CNA's four mouse and keyboard hot-plug events once.
     *
     * <p>They are static CNA events, so the registrations belong to the process rather than to a
     * game and outlive any one game. Subscribing twice would deliver every event twice, so the
     * decision lives here rather than at each call site.
     */
    public static synchronized void ensureInputDevicesSubscribed() {
        if (inputDeviceRegistrations != null) {
            return;
        }
        long[] registrations = new long[4];
        NativeGamerServices.check("Input device hot-plug events",
                NativeGamerServices.nativeSubscribeInputDeviceEvents(registrations));
        inputDeviceRegistrations = registrations;
    }

    /** Releases the hot-plug registrations, for a test that needs to prove they can be released. */
    public static synchronized void releaseInputDevices() {
        if (inputDeviceRegistrations == null) {
            return;
        }
        long[] registrations = inputDeviceRegistrations;
        inputDeviceRegistrations = null;
        NativeGamerServices.check("Input device hot-plug events",
                NativeGamerServices.nativeUnsubscribeInputDeviceEvents(registrations));
    }

    /**
     * Subscribes CNA's two raw-joystick hot-plug events once.
     *
     * <p>Process-wide, for the same reason the mouse and keyboard ones are.
     */
    public static synchronized void ensureJoysticksSubscribed() {
        if (joystickRegistrations != null) {
            return;
        }
        long[] registrations = new long[2];
        NativeGamerServices.check("Joystick hot-plug events",
                NativeGamerServices.nativeSubscribeJoystickEvents(registrations));
        joystickRegistrations = registrations;
    }

    /** Releases the raw-joystick registrations. */
    public static synchronized void releaseJoysticks() {
        if (joystickRegistrations == null) {
            return;
        }
        long[] registrations = joystickRegistrations;
        joystickRegistrations = null;
        NativeGamerServices.check("Joystick hot-plug events",
                NativeGamerServices.nativeUnsubscribeJoystickEvents(registrations));
    }

    /** Subscribes CNA's mouse-click event once. Process-wide, like the hot-plug events. */
    public static synchronized void ensureMouseClickedSubscribed() {
        if (mouseClickedRegistration != 0L) {
            return;
        }
        long[] registration = new long[1];
        NativeGamerServices.check("Mouse click events",
                NativeGamerServices.nativeSubscribeMouseClicked(registration));
        mouseClickedRegistration = registration[0];
    }

    /** Releases the mouse-click registration. */
    public static synchronized void releaseMouseClicked() {
        if (mouseClickedRegistration == 0L) {
            return;
        }
        long registration = mouseClickedRegistration;
        mouseClickedRegistration = 0L;
        NativeGamerServices.check("Mouse click events",
                NativeGamerServices.nativeUnsubscribeMouseClicked(registration));
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
            Iterable<Handler> handlers;
            if (record[0] >= FIRST_INPUT_KIND) {
                handlers = INPUT_HANDLERS;
            } else if (record[0] >= FIRST_SESSION_KIND) {
                handlers = single(sessionHandler);
            } else {
                handlers = single(gamerHandler);
            }
            for (Handler handler : handlers) {
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
        }
        if (failure != null) {
            throw failure;
        }
    }

    private static Iterable<Handler> single(Handler handler) {
        return handler == null ? List.of() : List.of(handler);
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
