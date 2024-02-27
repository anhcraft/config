package dev.anhcraft.config.validate.check;

import org.jetbrains.annotations.NotNull;

/**
 * Represents parameterized validation.<br>
 * This kind of validation requires initialization for every set of arguments.
 */
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
