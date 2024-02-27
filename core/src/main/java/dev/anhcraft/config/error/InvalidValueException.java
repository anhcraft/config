package dev.anhcraft.config.error;

import dev.anhcraft.config.context.Context;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an invalid value exception occurred within a {@link Context}
 */
public class InvalidValueException extends ContextException {
  public InvalidValueException(@NotNull Context context, @NotNull String message) {
    super(context, message);
  }

  public InvalidValueException(
      @NotNull Context context, @NotNull String message, @NotNull Throwable cause) {
    super(context, message, cause);
  }
}
