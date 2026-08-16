package org.openeggbert.cna.internal;

/** Internal home for the future FFM/JNI mapping of CNA's stable C ABI. */
public final class NativeBindings {

    private NativeBindings() {
    }

    /** Fails explicitly while the canonical CNA C ABI is unavailable. */
    public static void requireAvailable() {
        throw new IllegalStateException("CNA native C ABI is not available yet");
    }
}
