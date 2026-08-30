package Microsoft.Xna.Framework.Net;

import org.openeggbert.cna.internal.NativeGamerServices;
import org.openeggbert.cna.internal.generated.NativeNetworkRoutes;

/** One session a search found, and what joining it would cost. */
public final class AvailableNetworkSession {

    private final long handle;

    AvailableNetworkSession(long handle) {
        this.handle = handle;
    }

    long handle() {
        return handle;
    }

    public int getCurrentGamerCount() {
        int[] value = new int[1];
        NativeGamerServices.check("AvailableNetworkSession.CurrentGamerCount",
                NativeNetworkRoutes.availableNetworkSessionGetCurrentGamerCount(handle, value));
        return value[0];
    }

    public String getHostGamertag() {
        return NativeGamerServices.text("AvailableNetworkSession.HostGamertag",
                out -> NativeNetworkRoutes.availableNetworkSessionGetHostGamertagSize(handle, out),
                (buffer, out) -> NativeNetworkRoutes.availableNetworkSessionCopyHostGamertag(
                        handle, buffer, out));
    }

    public int getOpenPrivateGamerSlots() {
        int[] value = new int[1];
        NativeGamerServices.check("AvailableNetworkSession.OpenPrivateGamerSlots",
                NativeNetworkRoutes.availableNetworkSessionGetOpenPrivateGamerSlots(handle, value));
        return value[0];
    }

    public int getOpenPublicGamerSlots() {
        int[] value = new int[1];
        NativeGamerServices.check("AvailableNetworkSession.OpenPublicGamerSlots",
                NativeNetworkRoutes.availableNetworkSessionGetOpenPublicGamerSlots(handle, value));
        return value[0];
    }

    public QualityOfService getQualityOfService() {
        long[] values = new long[5];
        NativeGamerServices.check("AvailableNetworkSession.QualityOfService",
                NativeNetworkRoutes.availableNetworkSessionGetQualityOfService(
                        handle, new byte[7], values));
        return new QualityOfService(values);
    }

    public NetworkSessionProperties getSessionProperties() {
        long[] properties = new long[1];
        NativeGamerServices.check("AvailableNetworkSession.SessionProperties",
                NativeNetworkRoutes.availableNetworkSessionCopySessionProperties(
                        handle, properties));
        return new NetworkSessionProperties(properties[0]);
    }
}
