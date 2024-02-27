package dev.anhcraft.config.validate.check;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a validation.<br>
 * Unless the validation is {@link ParameterizedValidation}, it can be singleton.
 */
public interface Validation {
  /**
   * Checks the given value against the validation
   * @param value the value to check
   * @return {@code true} if the validation passes
   */
  boolean check(@Nullable Object value);

  /**
   * Returns the message, usually the latest error.
   * @return the message
   */
  @NotNull String message();
}
