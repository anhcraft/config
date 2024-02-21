package dev.anhcraft.config.error;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InvalidValueException extends RuntimeException {
    public InvalidValueException(@NotNull String property, @NotNull String message) {
        this(property, message, null);
    }

    public InvalidValueException(@NotNull String property, @NotNull String message, @Nullable Throwable cause) {
        super(String.format("Property [%s] %s", property, message), cause);
    }
}
