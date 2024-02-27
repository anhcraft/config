package dev.anhcraft.config.error;

import org.jetbrains.annotations.NotNull;

/**
 * Invalid validation syntax.
 */
public class ValidationParseException extends RuntimeException {

    public ValidationParseException(@NotNull String message) {
        super(message);
    }

    public ValidationParseException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
}
