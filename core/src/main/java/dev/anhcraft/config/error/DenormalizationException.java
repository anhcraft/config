package dev.anhcraft.config.error;

import dev.anhcraft.config.context.Context;
import org.jetbrains.annotations.NotNull;

/**
 * An exception thrown during the denormalization.
 */
public class DenormalizationException extends ContextException {
  public DenormalizationException(@NotNull Context context, @NotNull String message) {
    super(context, message);
  }

  public DenormalizationException(
      @NotNull Context context, @NotNull String message, @NotNull Throwable cause) {
    super(context, message, cause);
  }
}
