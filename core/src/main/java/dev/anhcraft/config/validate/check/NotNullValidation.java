package dev.anhcraft.config.validate.check;

import org.jetbrains.annotations.NotNull;

/**
 * Validates that the given object is not null.
 */
public class NotNullValidation implements Validation {
  @Override
  public boolean check(Object value) {
    return value != null;
  }

  @Override
  public @NotNull String message() {
    return "must be not-null";
  }
}
