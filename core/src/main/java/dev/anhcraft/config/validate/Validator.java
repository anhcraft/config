package dev.anhcraft.config.validate;

import dev.anhcraft.config.validate.check.Validation;

/**
 * A validator contains a set of {@link Validation}
 */
public interface Validator extends Validation {
  /**
   * Whether this validator is silent or not.<br>
   * When it is silent, the implementation must suppress exceptions.
   * @return {@code true} if this validator is silent
   */
  boolean silent();
}
