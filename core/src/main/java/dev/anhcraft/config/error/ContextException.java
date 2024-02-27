package dev.anhcraft.config.error;

import dev.anhcraft.config.context.Context;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an exception occurred within a {@link Context}
 */
public class ContextException extends RuntimeException {
  private final Context context;

  public ContextException(@NotNull Context context, @NotNull String message) {
    super(message);
    this.context = context;
  }

  public ContextException(
      @NotNull Context context, @NotNull String message, @NotNull Throwable cause) {
    super(message, cause);
    this.context = context;
  }

  @NotNull public Context getContext() {
    return context;
  }
}
