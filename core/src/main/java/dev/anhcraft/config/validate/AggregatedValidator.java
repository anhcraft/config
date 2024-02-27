package dev.anhcraft.config.validate;

import dev.anhcraft.config.validate.check.Validation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Aggregates multiple validation and checks all of them.
 */
public class AggregatedValidator implements Validator {
    private final Validation[] validators;
    private String lastMessage = "";
    private final boolean silent;

    public AggregatedValidator(@NotNull Validation[] validators, boolean silent) {
        this.validators = validators;
        this.silent = silent;
    }

    /**
     * Checks the given value against all validators.<br>
     * Immediately returns {@code false} when a check fails.
     * @param value the value to check
     * @return {@code true} if all checks pass
     */
    @Override
    public boolean check(@Nullable Object value) {
        for (Validation validation : validators) {
            if (!validation.check(value)) {
                lastMessage = validation.message();
                return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull String message() {
        return lastMessage;
    }

    @Override
    public boolean silent() {
        return silent;
    }
}
