package org.openeggbert.cna.extensions.content;

import org.openeggbert.cna.internal.NativeBindings;

import java.nio.charset.StandardCharsets;

/** Shared plumbing for the {@code .cnb} facade. This class is not application API. */
final class CnbExtension {

    private static final int RESULT_SUCCESS = 0;
    private static final int RESULT_INVALID_ARGUMENT = 1;
    private static final int RESULT_INVALID_STATE = 3;
    private static final int RESULT_IO = 5;
    private static final int RESULT_NOT_SUPPORTED = 6;
    private static final int RESULT_OVERFLOW = 10;
    private static final int RESULT_ENCODING = 11;
    static final int RESULT_BUFFER_TOO_SMALL = 14;

    private CnbExtension() {
    }

    /**
     * Loads the native bridge.
     *
     * <p>The {@code .cnb} family is pure functions and self-owned handles over caller bytes, so
     * unlike every other extension here it needs no running game -- a tool can read a file with
     * no window, no device and no frame.
     */
    static void requireAvailable() {
        NativeBindings.requireAvailable();
    }

    /**
     * Maps one CNA result, keeping the identities a {@code .cnb} caller can act on.
     *
     * <p>{@code IO}, {@code OVERFLOW} and {@code ENCODING} all mean the same thing to a reader --
     * this file is not one I can read -- so they become {@link CnbFormatException} with the
     * diagnostic CNA recorded. {@code BUFFER_TOO_SMALL} is the caller's mistake about a buffer
     * and stays its own failure, because CNA writes no partial answer and a retry with the
     * reported size is the fix.
     */
    static void check(String operation, int result) {
        if (result == RESULT_SUCCESS) {
            return;
        }
        if (result == RESULT_NOT_SUPPORTED) {
            throw new ContentNotSupportedException(operation
                    + " is not supported by this CNA build");
        }
        if (result == RESULT_IO || result == RESULT_OVERFLOW || result == RESULT_ENCODING) {
            throw new CnbFormatException(operation + " refused the file: "
                    + NativeBindings.failure(operation, result).getMessage());
        }
        if (result == RESULT_INVALID_STATE) {
            throw new IllegalStateException(operation + " was refused in this state");
        }
        if (result == RESULT_INVALID_ARGUMENT || result == RESULT_BUFFER_TOO_SMALL) {
            throw new IllegalArgumentException(
                    NativeBindings.failure(operation, result).getMessage());
        }
        throw NativeBindings.failure(operation, result);
    }

    static byte[] utf8(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }

    /** Reads one of CNA's size-then-copy string pairs. */
    static String text(String operation, SizeRoute size, CopyRoute copy) {
        long[] bytes = new long[1];
        check(operation, size.read(bytes));
        byte[] destination = new byte[(int) bytes[0]];
        check(operation, copy.read(destination, bytes));
        return new String(destination, 0, (int) bytes[0], StandardCharsets.UTF_8);
    }

    /** The size half of a count/copy string pair. */
    interface SizeRoute {
        int read(long[] outBytes);
    }

    /** The copy half of a count/copy string pair. */
    interface CopyRoute {
        int read(byte[] destination, long[] outBytes);
    }
}
