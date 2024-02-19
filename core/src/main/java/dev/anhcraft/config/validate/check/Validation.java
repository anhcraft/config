package dev.anhcraft.config.validate.check;

import org.jetbrains.annotations.NotNull;

public interface Validation {
    boolean check(Object value);

    @NotNull
    String message();
}
