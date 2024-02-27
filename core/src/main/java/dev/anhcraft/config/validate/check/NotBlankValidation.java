package dev.anhcraft.config.validate.check;

import org.jetbrains.annotations.NotNull;

/**
 * Validates that the give string is not blank.
 */
public class NotBlankValidation implements Validation {
    @Override
    public boolean check(Object value) {
        if (value instanceof String) return !((String) value).isBlank();
        return true;
    }

    @Override
    public @NotNull String message() {
        return "must be not-blank";
    }
}
