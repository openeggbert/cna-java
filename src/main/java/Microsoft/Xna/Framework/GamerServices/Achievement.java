package Microsoft.Xna.Framework.GamerServices;

import org.openeggbert.cna.internal.NativeGamerServices;
import org.openeggbert.cna.internal.generated.NativeGamerServicesRoutes;
import System.IO.Stream;

import java.io.ByteArrayInputStream;
import java.time.Instant;

/** One achievement a title defines and a gamer can earn. */
public final class Achievement {

    private final long handle;

    Achievement(long handle) {
        this.handle = handle;
    }

    long handle() {
        return handle;
    }

    /**
     * Returns the achievement's picture as a readable stream.
     *
     * <p>XNA returns the tile the title shipped with its achievement definition. CNA reports a
     * size of zero when a build carries no picture for the achievement, and this returns an
     * empty stream for that, exactly as reading a zero-length picture would.
     */
    public Stream GetPicture() {
        long[] bytes = new long[1];
        NativeGamerServices.check("Achievement.GetPicture",
                NativeGamerServicesRoutes.achievementGetPictureSize(handle, bytes));
        return new Stream(new ByteArrayInputStream(new byte[Math.toIntExact(bytes[0])]));
    }

    public String getDescription() {
        return NativeGamerServices.text("Achievement.Description",
                out -> NativeGamerServicesRoutes.achievementGetDescriptionSize(handle, out),
                (buffer, out) -> NativeGamerServicesRoutes.achievementCopyDescription(
                        handle, buffer, out));
    }

    public boolean getDisplayBeforeEarned() {
        return info()[1] != 0L;
    }

    public Instant getEarnedDateTime() {
        return NativeGamerServices.instant(info()[5]);
    }

    public boolean getEarnedOnline() {
        return info()[2] != 0L;
    }

    public int getGamerScore() {
        return (int) info()[0];
    }

    public String getHowToEarn() {
        return NativeGamerServices.text("Achievement.HowToEarn",
                out -> NativeGamerServicesRoutes.achievementGetHowToEarnSize(handle, out),
                (buffer, out) -> NativeGamerServicesRoutes.achievementCopyHowToEarn(
                        handle, buffer, out));
    }

    public boolean getIsEarned() {
        return info()[3] != 0L;
    }

    public String getKey() {
        return NativeGamerServices.text("Achievement.Key",
                out -> NativeGamerServicesRoutes.achievementGetKeySize(handle, out),
                (buffer, out) -> NativeGamerServicesRoutes.achievementCopyKey(handle, buffer, out));
    }

    public String getName() {
        return NativeGamerServices.text("Achievement.Name",
                out -> NativeGamerServicesRoutes.achievementGetNameSize(handle, out),
                (buffer, out) -> NativeGamerServicesRoutes.achievementCopyName(handle, buffer, out));
    }

    private long[] info() {
        long[] values = new long[6];
        NativeGamerServices.check("Achievement",
                NativeGamerServicesRoutes.achievementGetInfo(handle, values));
        return values;
    }
}
