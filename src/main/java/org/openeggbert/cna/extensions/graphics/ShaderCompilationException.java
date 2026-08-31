package org.openeggbert.cna.extensions.graphics;

/**
 * Thrown when a renderer refuses shader source it was given.
 *
 * <p>A CNA extension. XNA has no counterpart because it has no runtime shader compiler at all: an
 * XNA effect is compiled by the Content Pipeline and arrives as bytecode.
 *
 * <p>An {@link IllegalArgumentException}, because the source is an argument the caller supplied
 * and a caller already catching that for bad input should not have to learn a new type. The
 * message carries the renderer's own compiler log, which is the only thing that says <em>where</em>
 * in the source the problem is.
 */
public class ShaderCompilationException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception.
     *
     * @param message the compiler's own diagnostics
     * @param cause the native failure this was derived from, or {@code null}
     */
    public ShaderCompilationException(String message, Throwable cause) {
        super(message, cause);
    }
}
