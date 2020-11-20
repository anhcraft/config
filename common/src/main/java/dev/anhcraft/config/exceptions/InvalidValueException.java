package dev.anhcraft.config.exceptions;

import org.jetbrains.annotations.NotNull;

public class InvalidValueException extends Exception {
    private static final long serialVersionUID = 5168573547621147302L;

    public InvalidValueException(@NotNull String reason) {
        super(reason);
    }

    public InvalidValueException(@NotNull String key, @NotNull String reason) {
        super(String.format("%s (key = %s)", reason, key));
    }

    public InvalidValueException(@NotNull String key, @NotNull Reason reason) {
        super(String.format("Value must not be %s (key = %s)", reason.name().toLowerCase(), key));
    }

    public enum Reason {
        NULL,
        EMPTY
    }
}
