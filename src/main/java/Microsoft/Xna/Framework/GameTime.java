package Microsoft.Xna.Framework;

import java.time.Duration;
import java.util.Objects;

/** XNA 4.0-compatible game-time facade. */
public final class GameTime {

    public final Duration TotalGameTime;
    public final Duration ElapsedGameTime;
    public final boolean IsRunningSlowly;

    /** Creates an immutable game-time snapshot. */
    public GameTime(Duration totalGameTime, Duration elapsedGameTime, boolean isRunningSlowly) {
        TotalGameTime = Objects.requireNonNull(totalGameTime, "totalGameTime");
        ElapsedGameTime = Objects.requireNonNull(elapsedGameTime, "elapsedGameTime");
        IsRunningSlowly = isRunningSlowly;
    }
}
