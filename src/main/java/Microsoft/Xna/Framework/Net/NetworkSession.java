package Microsoft.Xna.Framework.Net;

import Microsoft.Xna.Framework.EventHandler;
import Microsoft.Xna.Framework.GamerServices.GamerCollection;
import Microsoft.Xna.Framework.GamerServices.InviteAcceptedEventArgs;
import Microsoft.Xna.Framework.GamerServices.SignedInGamer;
import org.openeggbert.cna.internal.CompletedAsyncResult;
import org.openeggbert.cna.internal.GamerHandles;
import org.openeggbert.cna.internal.NativeGamerServices;
import org.openeggbert.cna.internal.generated.NativeNetworkRoutes;
import System.AsyncCallback;
import System.IAsyncResult;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * One multiplayer session: its gamers, its state, and the events that report both changing.
 *
 * <p>Every asynchronous member follows XNA's {@code Begin}/{@code End} pair. CNA completes each
 * one synchronously -- its own asynchronous routes are one synchronous call that still invokes
 * the completion callback -- so the result reports {@code CompletedSynchronously} truthfully
 * and {@code End} returns immediately.
 *
 * <p>{@code StartGame} and {@code EndGame} only queue a state change; it takes effect when
 * {@link #Update()} runs, which is what XNA's session does too.
 */
public final class NetworkSession implements AutoCloseable {

    /** The most previous gamers a session remembers. */
    public static final int MaxPreviousGamers = 100;

    /** The most gamers one session can hold. */
    public static final int MaxSupportedGamers = 31;

    private static final int ROSTER_ALL = 0;
    private static final int ROSTER_LOCAL = 1;
    private static final int ROSTER_REMOTE = 2;
    private static final int ROSTER_PREVIOUS = 3;

    private static final List<EventHandler<InviteAcceptedEventArgs>> INVITE_ACCEPTED =
            new CopyOnWriteArrayList<>();

    private final List<EventHandler<GameEndedEventArgs>> gameEnded = new CopyOnWriteArrayList<>();
    private final List<EventHandler<GameStartedEventArgs>> gameStarted =
            new CopyOnWriteArrayList<>();
    private final List<EventHandler<GamerJoinedEventArgs>> gamerJoined =
            new CopyOnWriteArrayList<>();
    private final List<EventHandler<GamerLeftEventArgs>> gamerLeft = new CopyOnWriteArrayList<>();
    private final List<EventHandler<HostChangedEventArgs>> hostChanged =
            new CopyOnWriteArrayList<>();
    private final List<EventHandler<NetworkSessionEndedEventArgs>> sessionEnded =
            new CopyOnWriteArrayList<>();
    private final List<EventHandler<WriteLeaderboardsEventArgs>> writeArbitrated =
            new CopyOnWriteArrayList<>();
    private final List<EventHandler<WriteLeaderboardsEventArgs>> writeTrueSkill =
            new CopyOnWriteArrayList<>();
    private final List<EventHandler<WriteLeaderboardsEventArgs>> writeUnarbitrated =
            new CopyOnWriteArrayList<>();

    private final long handle;
    private final boolean owned;
    private boolean disposed;

    private NetworkSession(long handle, boolean owned) {
        this.handle = handle;
        this.owned = owned;
    }

    static NetworkSession borrowed(long handle) {
        return new NetworkSession(handle, false);
    }

    public void addGameEndedListener(EventHandler<GameEndedEventArgs> listener) {
        gameEnded.add(Objects.requireNonNull(listener, "listener"));
    }

    public void removeGameEndedListener(EventHandler<GameEndedEventArgs> listener) {
        gameEnded.remove(Objects.requireNonNull(listener, "listener"));
    }

    public void addGameStartedListener(EventHandler<GameStartedEventArgs> listener) {
        gameStarted.add(Objects.requireNonNull(listener, "listener"));
    }

    public void removeGameStartedListener(EventHandler<GameStartedEventArgs> listener) {
        gameStarted.remove(Objects.requireNonNull(listener, "listener"));
    }

    public void addGamerJoinedListener(EventHandler<GamerJoinedEventArgs> listener) {
        gamerJoined.add(Objects.requireNonNull(listener, "listener"));
    }

    public void removeGamerJoinedListener(EventHandler<GamerJoinedEventArgs> listener) {
        gamerJoined.remove(Objects.requireNonNull(listener, "listener"));
    }

    public void addGamerLeftListener(EventHandler<GamerLeftEventArgs> listener) {
        gamerLeft.add(Objects.requireNonNull(listener, "listener"));
    }

    public void removeGamerLeftListener(EventHandler<GamerLeftEventArgs> listener) {
        gamerLeft.remove(Objects.requireNonNull(listener, "listener"));
    }

    public void addHostChangedListener(EventHandler<HostChangedEventArgs> listener) {
        hostChanged.add(Objects.requireNonNull(listener, "listener"));
    }

    public void removeHostChangedListener(EventHandler<HostChangedEventArgs> listener) {
        hostChanged.remove(Objects.requireNonNull(listener, "listener"));
    }

    public static void addInviteAcceptedListener(
            EventHandler<InviteAcceptedEventArgs> listener) {
        INVITE_ACCEPTED.add(Objects.requireNonNull(listener, "listener"));
    }

    public static void removeInviteAcceptedListener(
            EventHandler<InviteAcceptedEventArgs> listener) {
        INVITE_ACCEPTED.remove(Objects.requireNonNull(listener, "listener"));
    }

    public void addSessionEndedListener(EventHandler<NetworkSessionEndedEventArgs> listener) {
        sessionEnded.add(Objects.requireNonNull(listener, "listener"));
    }

    public void removeSessionEndedListener(EventHandler<NetworkSessionEndedEventArgs> listener) {
        sessionEnded.remove(Objects.requireNonNull(listener, "listener"));
    }

    public void addWriteArbitratedLeaderboardListener(
            EventHandler<WriteLeaderboardsEventArgs> listener) {
        writeArbitrated.add(Objects.requireNonNull(listener, "listener"));
    }

    public void removeWriteArbitratedLeaderboardListener(
            EventHandler<WriteLeaderboardsEventArgs> listener) {
        writeArbitrated.remove(Objects.requireNonNull(listener, "listener"));
    }

    public void addWriteTrueSkillListener(EventHandler<WriteLeaderboardsEventArgs> listener) {
        writeTrueSkill.add(Objects.requireNonNull(listener, "listener"));
    }

    public void removeWriteTrueSkillListener(EventHandler<WriteLeaderboardsEventArgs> listener) {
        writeTrueSkill.remove(Objects.requireNonNull(listener, "listener"));
    }

    public void addWriteUnarbitratedLeaderboardListener(
            EventHandler<WriteLeaderboardsEventArgs> listener) {
        writeUnarbitrated.add(Objects.requireNonNull(listener, "listener"));
    }

    public void removeWriteUnarbitratedLeaderboardListener(
            EventHandler<WriteLeaderboardsEventArgs> listener) {
        writeUnarbitrated.remove(Objects.requireNonNull(listener, "listener"));
    }

    public void AddLocalGamer(SignedInGamer gamer) {
        NativeGamerServices.check("NetworkSession.AddLocalGamer",
                NativeNetworkRoutes.networkSessionAddLocalGamer(handle,
                        GamerHandles.of(Objects.requireNonNull(gamer, "gamer"))));
    }

    public static IAsyncResult BeginCreate(NetworkSessionType sessionType,
            Iterable<SignedInGamer> localGamers, int maxGamers, int privateGamerSlots,
            NetworkSessionProperties sessionProperties, AsyncCallback callback, Object asyncState) {
        return CompletedAsyncResult.begin(callback, asyncState, () -> Create(
                sessionType, localGamers, maxGamers, privateGamerSlots, sessionProperties));
    }

    public static IAsyncResult BeginCreate(NetworkSessionType sessionType, int maxLocalGamers,
            int maxGamers, AsyncCallback callback, Object asyncState) {
        return CompletedAsyncResult.begin(callback, asyncState,
                () -> Create(sessionType, maxLocalGamers, maxGamers));
    }

    public static IAsyncResult BeginCreate(NetworkSessionType sessionType, int maxLocalGamers,
            int maxGamers, int privateGamerSlots, NetworkSessionProperties sessionProperties,
            AsyncCallback callback, Object asyncState) {
        return CompletedAsyncResult.begin(callback, asyncState, () -> Create(
                sessionType, maxLocalGamers, maxGamers, privateGamerSlots, sessionProperties));
    }

    public static IAsyncResult BeginFind(NetworkSessionType sessionType,
            Iterable<SignedInGamer> localGamers, NetworkSessionProperties searchProperties,
            AsyncCallback callback, Object asyncState) {
        return CompletedAsyncResult.begin(callback, asyncState,
                () -> Find(sessionType, localGamers, searchProperties));
    }

    public static IAsyncResult BeginFind(NetworkSessionType sessionType, int maxLocalGamers,
            NetworkSessionProperties searchProperties, AsyncCallback callback, Object asyncState) {
        return CompletedAsyncResult.begin(callback, asyncState,
                () -> Find(sessionType, maxLocalGamers, searchProperties));
    }

    public static IAsyncResult BeginJoin(AvailableNetworkSession availableSession,
            AsyncCallback callback, Object asyncState) {
        return CompletedAsyncResult.begin(callback, asyncState, () -> Join(availableSession));
    }

    public static IAsyncResult BeginJoinInvited(Iterable<SignedInGamer> localGamers,
            AsyncCallback callback, Object asyncState) {
        return CompletedAsyncResult.begin(callback, asyncState, () -> JoinInvited(localGamers));
    }

    public static IAsyncResult BeginJoinInvited(int maxLocalGamers, AsyncCallback callback,
            Object asyncState) {
        return CompletedAsyncResult.begin(callback, asyncState, () -> JoinInvited(maxLocalGamers));
    }

    public static NetworkSession Create(NetworkSessionType sessionType,
            Iterable<SignedInGamer> localGamers, int maxGamers, int privateGamerSlots,
            NetworkSessionProperties sessionProperties) {
        long[] session = new long[1];
        NativeGamerServices.check("NetworkSession.Create",
                NativeNetworkRoutes.networkSessionCreateWithLocalGamers(
                        kind(sessionType), handles(localGamers), maxGamers, privateGamerSlots,
                        properties(sessionProperties), session));
        return new NetworkSession(session[0], true);
    }

    public static NetworkSession Create(NetworkSessionType sessionType, int maxLocalGamers,
            int maxGamers, int privateGamerSlots, NetworkSessionProperties sessionProperties) {
        long[] session = new long[1];
        NativeGamerServices.check("NetworkSession.Create",
                NativeNetworkRoutes.networkSessionCreateWithProperties(
                        kind(sessionType), maxLocalGamers, maxGamers, privateGamerSlots,
                        properties(sessionProperties), session));
        return new NetworkSession(session[0], true);
    }

    public static NetworkSession Create(NetworkSessionType sessionType, int maxLocalGamers,
            int maxGamers) {
        long[] session = new long[1];
        NativeGamerServices.check("NetworkSession.Create",
                NativeNetworkRoutes.networkSessionCreate(
                        kind(sessionType), maxLocalGamers, maxGamers, session));
        return new NetworkSession(session[0], true);
    }

    public void Dispose() {
        synchronized (this) {
            if (disposed) {
                return;
            }
            disposed = true;
        }
        NativeGamerServices.check("NetworkSession.Dispose",
                NativeNetworkRoutes.networkSessionDispose(handle));
        if (owned) {
            NativeGamerServices.check("NetworkSession.Dispose",
                    NativeNetworkRoutes.networkSessionDestroy(handle));
        }
    }

    @Override
    public void close() {
        Dispose();
    }

    public static NetworkSession EndCreate(IAsyncResult result) {
        return CompletedAsyncResult.end(result, NetworkSession.class);
    }

    public static AvailableNetworkSessionCollection EndFind(IAsyncResult result) {
        return CompletedAsyncResult.end(result, AvailableNetworkSessionCollection.class);
    }

    public void EndGame() {
        NativeGamerServices.check("NetworkSession.EndGame",
                NativeNetworkRoutes.networkSessionEndGame(handle));
    }

    public static NetworkSession EndJoin(IAsyncResult result) {
        return CompletedAsyncResult.end(result, NetworkSession.class);
    }

    public static NetworkSession EndJoinInvited(IAsyncResult result) {
        return CompletedAsyncResult.end(result, NetworkSession.class);
    }

    public static AvailableNetworkSessionCollection Find(NetworkSessionType sessionType,
            Iterable<SignedInGamer> localGamers, NetworkSessionProperties searchProperties) {
        long[] collection = new long[1];
        NativeGamerServices.check("NetworkSession.Find",
                NativeNetworkRoutes.networkSessionFindWithLocalGamers(kind(sessionType),
                        handles(localGamers), properties(searchProperties), collection));
        return new AvailableNetworkSessionCollection(collection[0]);
    }

    public static AvailableNetworkSessionCollection Find(NetworkSessionType sessionType,
            int maxLocalGamers, NetworkSessionProperties searchProperties) {
        long[] collection = new long[1];
        NativeGamerServices.check("NetworkSession.Find",
                NativeNetworkRoutes.networkSessionFind(kind(sessionType), maxLocalGamers,
                        properties(searchProperties), collection));
        return new AvailableNetworkSessionCollection(collection[0]);
    }

    /** Returns the gamer with this session-unique id, or {@code null} when there is none. */
    public NetworkGamer FindGamerById(int gamerId) {
        if (gamerId < 0 || gamerId > 255) {
            throw new IllegalArgumentException(
                    "gamerId is " + gamerId + ", outside the byte range 0..255");
        }
        long[] gamer = new long[1];
        NativeGamerServices.check("NetworkSession.FindGamerById",
                NativeNetworkRoutes.networkSessionFindGamerById(
                        handle, (byte) gamerId, gamer));
        return gamer[0] == 0L ? null : new NetworkGamer(gamer[0]);
    }

    public static NetworkSession Join(AvailableNetworkSession availableSession) {
        long[] session = new long[1];
        NativeGamerServices.check("NetworkSession.Join",
                NativeNetworkRoutes.networkSessionJoin(
                        Objects.requireNonNull(availableSession, "availableSession").handle(),
                        session));
        return new NetworkSession(session[0], true);
    }

    public static NetworkSession JoinInvited(Iterable<SignedInGamer> localGamers) {
        long[] session = new long[1];
        NativeGamerServices.check("NetworkSession.JoinInvited",
                NativeNetworkRoutes.networkSessionJoinInvitedWithLocalGamers(
                        handles(localGamers), session));
        return new NetworkSession(session[0], true);
    }

    public static NetworkSession JoinInvited(int maxLocalGamers) {
        long[] session = new long[1];
        NativeGamerServices.check("NetworkSession.JoinInvited",
                NativeNetworkRoutes.networkSessionJoinInvited(maxLocalGamers, session));
        return new NetworkSession(session[0], true);
    }

    public void ResetReady() {
        NativeGamerServices.check("NetworkSession.ResetReady",
                NativeNetworkRoutes.networkSessionResetReady(handle));
    }

    public void StartGame() {
        NativeGamerServices.check("NetworkSession.StartGame",
                NativeNetworkRoutes.networkSessionStartGame(handle));
    }

    /** Pumps the session: queued state changes take effect and queued events are raised. */
    public void Update() {
        NativeGamerServices.check("NetworkSession.Update",
                NativeNetworkRoutes.networkSessionUpdate(handle));
    }

    public GamerCollection<NetworkGamer> getAllGamers() {
        return roster(ROSTER_ALL);
    }

    public boolean getAllowHostMigration() {
        return flag("AllowHostMigration",
                value -> NativeNetworkRoutes.networkSessionGetAllowHostMigration(handle, value));
    }

    public void setAllowHostMigration(boolean value) {
        NativeGamerServices.check("NetworkSession.AllowHostMigration",
                NativeNetworkRoutes.networkSessionSetAllowHostMigration(handle, value));
    }

    public boolean getAllowJoinInProgress() {
        return flag("AllowJoinInProgress",
                value -> NativeNetworkRoutes.networkSessionGetAllowJoinInProgress(handle, value));
    }

    public void setAllowJoinInProgress(boolean value) {
        NativeGamerServices.check("NetworkSession.AllowJoinInProgress",
                NativeNetworkRoutes.networkSessionSetAllowJoinInProgress(handle, value));
    }

    public int getBytesPerSecondReceived() {
        return number("BytesPerSecondReceived",
                value -> NativeNetworkRoutes.networkSessionGetBytesPerSecondReceived(
                        handle, value));
    }

    public int getBytesPerSecondSent() {
        return number("BytesPerSecondSent",
                value -> NativeNetworkRoutes.networkSessionGetBytesPerSecondSent(handle, value));
    }

    public NetworkGamer getHost() {
        long[] gamer = new long[1];
        NativeGamerServices.check("NetworkSession.Host",
                NativeNetworkRoutes.networkSessionGetHost(handle, gamer));
        return gamer[0] == 0L ? null : new NetworkGamer(gamer[0]);
    }

    public boolean getIsDisposed() {
        if (disposed) {
            return true;
        }
        return flag("IsDisposed",
                value -> NativeNetworkRoutes.networkSessionGetIsDisposed(handle, value));
    }

    public boolean getIsEveryoneReady() {
        return flag("IsEveryoneReady",
                value -> NativeNetworkRoutes.networkSessionGetIsEveryoneReady(handle, value));
    }

    public boolean getIsHost() {
        return flag("IsHost",
                value -> NativeNetworkRoutes.networkSessionGetIsHost(handle, value));
    }

    public GamerCollection<LocalNetworkGamer> getLocalGamers() {
        return new LocalRoster(handle);
    }

    public int getMaxGamers() {
        return number("MaxGamers",
                value -> NativeNetworkRoutes.networkSessionGetMaxGamers(handle, value));
    }

    public void setMaxGamers(int value) {
        NativeGamerServices.check("NetworkSession.MaxGamers",
                NativeNetworkRoutes.networkSessionSetMaxGamers(handle, value));
    }

    public GamerCollection<NetworkGamer> getPreviousGamers() {
        return roster(ROSTER_PREVIOUS);
    }

    public int getPrivateGamerSlots() {
        return number("PrivateGamerSlots",
                value -> NativeNetworkRoutes.networkSessionGetPrivateGamerSlots(handle, value));
    }

    public void setPrivateGamerSlots(int value) {
        NativeGamerServices.check("NetworkSession.PrivateGamerSlots",
                NativeNetworkRoutes.networkSessionSetPrivateGamerSlots(handle, value));
    }

    public GamerCollection<NetworkGamer> getRemoteGamers() {
        return roster(ROSTER_REMOTE);
    }

    public NetworkSessionProperties getSessionProperties() {
        long[] properties = new long[1];
        NativeGamerServices.check("NetworkSession.SessionProperties",
                NativeNetworkRoutes.networkSessionCopySessionProperties(handle, properties));
        return new NetworkSessionProperties(properties[0]);
    }

    public NetworkSessionState getSessionState() {
        return NetworkSessionState.values()[number("SessionState",
                value -> NativeNetworkRoutes.networkSessionGetSessionState(handle, value))];
    }

    public NetworkSessionType getSessionType() {
        return NetworkSessionType.values()[number("SessionType",
                value -> NativeNetworkRoutes.networkSessionGetSessionType(handle, value))];
    }

    public Duration getSimulatedLatency() {
        long[] ticks = new long[1];
        NativeGamerServices.check("NetworkSession.SimulatedLatency",
                NativeNetworkRoutes.networkSessionGetSimulatedLatencyTicks(handle, ticks));
        return NativeGamerServices.duration(ticks[0]);
    }

    public void setSimulatedLatency(Duration value) {
        NativeGamerServices.check("NetworkSession.SimulatedLatency",
                NativeNetworkRoutes.networkSessionSetSimulatedLatencyTicks(handle,
                        NativeGamerServices.ticks(Objects.requireNonNull(value, "value"))));
    }

    public float getSimulatedPacketLoss() {
        float[] value = new float[1];
        NativeGamerServices.check("NetworkSession.SimulatedPacketLoss",
                NativeNetworkRoutes.networkSessionGetSimulatedPacketLoss(handle, value));
        return value[0];
    }

    public void setSimulatedPacketLoss(float value) {
        NativeGamerServices.check("NetworkSession.SimulatedPacketLoss",
                NativeNetworkRoutes.networkSessionSetSimulatedPacketLoss(handle, value));
    }

    private GamerCollection<NetworkGamer> roster(int roster) {
        return new Roster(handle, roster);
    }

    private static final class Roster extends GamerCollection<NetworkGamer> {

        Roster(long session, int roster) {
            super(index -> new NetworkGamer(gamerAt(session, roster, index)),
                    () -> gamerCount(session, roster));
        }
    }

    private static final class LocalRoster extends GamerCollection<LocalNetworkGamer> {

        LocalRoster(long session) {
            super(index -> new LocalNetworkGamer(gamerAt(session, ROSTER_LOCAL, index)),
                    () -> gamerCount(session, ROSTER_LOCAL));
        }
    }

    private static long gamerAt(long session, int roster, int index) {
        long[] gamer = new long[1];
        NativeGamerServices.check("NetworkSession gamer roster",
                NativeNetworkRoutes.networkSessionGetGamer(session, roster, index, gamer));
        return gamer[0];
    }

    private static int gamerCount(long session, int roster) {
        int[] count = new int[1];
        NativeGamerServices.check("NetworkSession gamer roster",
                NativeNetworkRoutes.networkSessionGetGamerCount(session, roster, count));
        return count[0];
    }

    private static int kind(NetworkSessionType sessionType) {
        return Objects.requireNonNull(sessionType, "sessionType").ordinal();
    }

    private static long properties(NetworkSessionProperties properties) {
        return properties == null ? 0L : properties.handle();
    }

    private static long[] handles(Iterable<SignedInGamer> gamers) {
        Objects.requireNonNull(gamers, "localGamers");
        List<Long> values = new ArrayList<>();
        for (SignedInGamer gamer : gamers) {
            values.add(GamerHandles.of(Objects.requireNonNull(gamer, "localGamers")));
        }
        long[] handles = new long[values.size()];
        for (int index = 0; index < handles.length; index++) {
            handles[index] = values.get(index);
        }
        return handles;
    }

    private interface Flag {
        int read(boolean[] value);
    }

    private interface Number {
        int read(int[] value);
    }

    private static boolean flag(String property, Flag reader) {
        boolean[] value = new boolean[1];
        NativeGamerServices.check("NetworkSession." + property, reader.read(value));
        return value[0];
    }

    private static int number(String property, Number reader) {
        int[] value = new int[1];
        NativeGamerServices.check("NetworkSession." + property, reader.read(value));
        return value[0];
    }
}
