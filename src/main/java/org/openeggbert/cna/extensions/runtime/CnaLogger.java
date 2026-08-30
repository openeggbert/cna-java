package org.openeggbert.cna.extensions.runtime;

import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.NativeGamerServices;
import org.openeggbert.cna.internal.generated.NativeRuntimeExtensionRoutes;

import java.util.Objects;

/**
 * CNA's own logger, so a game's diagnostics land in the same stream as the runtime's.
 *
 * <p>A CNA extension: XNA 4.0 has no logging API. The minimum level is process-wide state, so a
 * library that raises it should restore it.
 */
public final class CnaLogger {

    private CnaLogger() {
    }

    public static void Fatal(String message, LogCategory category) {
        Log(LogLevel.Fatal, message, category);
    }

    public static void Error(String message, LogCategory category) {
        Log(LogLevel.Error, message, category);
    }

    public static void Warn(String message, LogCategory category) {
        Log(LogLevel.Warn, message, category);
    }

    public static void Info(String message, LogCategory category) {
        Log(LogLevel.Info, message, category);
    }

    public static void Debug(String message, LogCategory category) {
        Log(LogLevel.Debug, message, category);
    }

    public static void Trace(String message, LogCategory category) {
        Log(LogLevel.Trace, message, category);
    }

    /** Writes one message at an explicit level. */
    public static void Log(LogLevel level, String message, LogCategory category) {
        Log(level, message, category, true);
    }

    /** Writes one message only when {@code condition} holds, as CNA's own conditional log does. */
    public static void Log(LogLevel level, String message, LogCategory category,
            boolean condition) {
        NativeBindings.requireAvailable();
        int result = NativeRuntimeExtensionRoutes.loggerLog(
                Objects.requireNonNull(level, "level").getValue(),
                NativeGamerServices.utf8(Objects.requireNonNull(message, "message")),
                Objects.requireNonNull(category, "category").ordinal(), condition);
        if (result != 0) {
            throw NativeBindings.failure("CnaLogger.Log", result);
        }
    }

    /** Returns the process-wide minimum level CNA is currently writing. */
    public static LogLevel getMinimumLevel() {
        NativeBindings.requireAvailable();
        int[] level = new int[1];
        int result = NativeRuntimeExtensionRoutes.loggerGetMinimumLevel(level);
        if (result != 0) {
            throw NativeBindings.failure("CnaLogger.getMinimumLevel", result);
        }
        return LogLevel.fromValue(level[0]);
    }

    /** Sets the process-wide minimum level. A library that raises it should restore it. */
    public static void setMinimumLevel(LogLevel value) {
        NativeBindings.requireAvailable();
        int result = NativeRuntimeExtensionRoutes.loggerSetMinimumLevel(
                Objects.requireNonNull(value, "value").getValue());
        if (result != 0) {
            throw NativeBindings.failure("CnaLogger.setMinimumLevel", result);
        }
    }
}
