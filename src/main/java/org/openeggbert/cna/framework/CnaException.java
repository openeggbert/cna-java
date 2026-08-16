package org.openeggbert.cna.framework;

/** Represents a failure reported by CNA's native result/error model. */
public final class CnaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** Creates an exception with a descriptive message. */
    public CnaException(String message) {
        super(message);
    }

    /** Creates an exception with a descriptive message and cause. */
    public CnaException(String message, Throwable cause) {
        super(message, cause);
    }
}
