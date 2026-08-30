package Microsoft.Xna.Framework.GamerServices;

import java.util.Objects;

/** The presence string and numeric value a signed-in gamer publishes to other players. */
public final class GamerPresence {

    private GamerPresenceMode presenceMode = GamerPresenceMode.None;
    private int presenceValue;

    GamerPresence() {
    }

    public GamerPresenceMode getPresenceMode() {
        return presenceMode;
    }

    public void setPresenceMode(GamerPresenceMode value) {
        presenceMode = Objects.requireNonNull(value, "value");
    }

    public int getPresenceValue() {
        return presenceValue;
    }

    public void setPresenceValue(int value) {
        presenceValue = value;
    }
}
