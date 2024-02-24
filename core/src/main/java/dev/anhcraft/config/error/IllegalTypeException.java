package dev.anhcraft.config.error;

import dev.anhcraft.config.context.Context;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a mismatch type exception occurred within a {@link Context}
 */
public class IllegalTypeException extends ContextException {
    public IllegalTypeException(@NotNull Context context, @NotNull String message) {
        super(context, message);
    }

    public IllegalTypeException(@NotNull Context context, @NotNull String message, @NotNull Throwable cause) {
        super(context, message, cause);
    }
}
