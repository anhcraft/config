package dev.anhcraft.config.validate;

import dev.anhcraft.config.validate.check.Validation;
import org.jetbrains.annotations.NotNull;

public class AggeratedValidator implements Validator {
    private final Validation[] validators;
    private String lastMessage = "";

    public AggeratedValidator(@NotNull Validation[] validators) {
        this.validators = validators;
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
}
