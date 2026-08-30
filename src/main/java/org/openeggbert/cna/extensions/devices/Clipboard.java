package org.openeggbert.cna.extensions.devices;

import org.openeggbert.cna.internal.NativeGamerServices;
import org.openeggbert.cna.internal.generated.NativeDeviceExtensionRoutes;

import java.util.Objects;

/**
 * The host clipboard.
 *
 * <p>A CNA extension: XNA 4.0 has no clipboard at all. The clipboard is process-external state
 * another application can change at any moment, so a write reports that the request was made,
 * not that the clipboard now holds the text.
 */
public final class Clipboard {

    private Clipboard() {
    }

    /**
     * Asks the host to put this text on the clipboard.
     *
     * @return whether the host accepted the request
     */
    public static boolean SetText(String text) {
        Objects.requireNonNull(text, "text");
        boolean[] accepted = new boolean[1];
        DeviceExtension.check("Clipboard.SetText",
                NativeDeviceExtensionRoutes.devicesClipboardSetTextExt(
                        DeviceExtension.game("Clipboard"),
                        NativeGamerServices.utf8(text), accepted));
        return accepted[0];
    }
}
