package dev.anhcraft.config.validate.check;

import org.jetbrains.annotations.NotNull;

public abstract class ParameterizedValidation implements Validation {
    private final String parameter;

    protected ParameterizedValidation(@NotNull String parameter) {
        this.parameter = parameter;
    }

    @NotNull
    public String getParameter() {
        return parameter;
    }
}
