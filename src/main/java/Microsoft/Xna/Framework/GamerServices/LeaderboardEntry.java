package Microsoft.Xna.Framework.GamerServices;

import org.openeggbert.cna.internal.NativeGamerServices;
import org.openeggbert.cna.internal.generated.NativeGamerServicesRoutes;

/** One gamer's row in a leaderboard, carrying a rating and the title's own columns. */
public final class LeaderboardEntry {

    private final long handle;
    private PropertyDictionary columns;

    LeaderboardEntry(long handle) {
        this.handle = handle;
    }

    long handle() {
        return handle;
    }

    /**
     * Returns the title-defined columns of this entry.
     *
     * <p>The dictionary is the entry's own, not a copy: writing to it is how a title records a
     * column, exactly as in XNA.
     */
    public synchronized PropertyDictionary getColumns() {
        if (columns == null) {
            long[] dictionary = new long[1];
            NativeGamerServices.check("LeaderboardEntry.Columns",
                    NativeGamerServicesRoutes.leaderboardEntryGetColumns(handle, dictionary));
            columns = new PropertyDictionary(dictionary[0]);
        }
        return columns;
    }

    /** Returns the gamer this row scores, or {@code null} when the row has no gamer yet. */
    public Gamer getGamer() {
        boolean[] present = new boolean[1];
        long[] gamer = new long[1];
        NativeGamerServices.check("LeaderboardEntry.Gamer",
                NativeGamerServicesRoutes.leaderboardEntryGetGamer(handle, present, gamer));
        return present[0] && gamer[0] != 0L ? new Gamer.RemoteGamer(gamer[0]) : null;
    }

    public long getRating() {
        long[] values = new long[3];
        NativeGamerServices.check("LeaderboardEntry.Rating",
                NativeGamerServicesRoutes.leaderboardEntryGetInfo(handle, new byte[3], values));
        return values[2];
    }

    public void setRating(long value) {
        NativeGamerServices.check("LeaderboardEntry.Rating",
                NativeGamerServicesRoutes.leaderboardEntrySetRating(handle, value));
    }
}
