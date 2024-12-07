package dev.anhcraft.config.error;

import dev.anhcraft.config.context.Context;
import org.jetbrains.annotations.NotNull;

/**
 * An exception thrown during the normalization.
 */
public class NormalizationException extends ContextException {
  public NormalizationException(@NotNull Context context, @NotNull String message) {
    super(context, message);
  }

  public NormalizationException(
      @NotNull Context context, @NotNull String message, @NotNull Throwable cause) {
    super(context, message, cause);
  }
}
