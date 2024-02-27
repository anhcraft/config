package dev.anhcraft.config.validate;

import dev.anhcraft.config.validate.check.ParameterizedValidation;
import dev.anhcraft.config.validate.check.Validation;
import java.util.function.Function;

/**
 * A function to construct {@link Validation}.
 * The argument supplied is used to instantiate a {@link ParameterizedValidation}.<br>
 * Otherwise, the argument does not affect the result, and returned validation may be singleton.
 */
public interface ValidationConstructor extends Function<String, Validation> {}
