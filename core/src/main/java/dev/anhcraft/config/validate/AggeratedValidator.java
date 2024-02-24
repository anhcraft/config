package dev.anhcraft.config.validate;

import dev.anhcraft.config.validate.check.Validation;
import org.jetbrains.annotations.NotNull;

public class AggeratedValidator implements Validator {
    private final Validation[] validators;
    private String lastMessage = "";
    private boolean silent;

    public AggeratedValidator(@NotNull Validation[] validators, boolean silent) {
        this.validators = validators;
        this.silent = silent;
    }

    @Override
    public boolean check(Object value) {
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
