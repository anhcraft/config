package dev.anhcraft.config.validate;

import dev.anhcraft.config.validate.check.Validation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A validator contains a set of {@link Validation}
 */
public interface Validator extends Validation {
  /**
   * Gets all validations used by this validator.
   * @return list of validations
   */
  @NotNull List<Validation> validations();

  /**
   * Whether this validator is silent or not.<br>
   * When it is silent, the implementation must suppress exceptions.
   * @return {@code true} if this validator is silent
   */
  boolean silent();
}
