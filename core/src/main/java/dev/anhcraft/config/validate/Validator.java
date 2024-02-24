package dev.anhcraft.config.validate;

import dev.anhcraft.config.validate.check.Validation;

public interface Validator extends Validation {
    boolean silent();
}
