package dev.anhcraft.config.validate;

import org.jetbrains.annotations.NotNull;

public class DisabledValidator implements Validator {
    public static final Validator INSTANCE = new DisabledValidator();

    @Override
    public boolean check(Object value) {
        return true;
    }

    @Override
    public @NotNull String message() {
        return "";
    }
}
