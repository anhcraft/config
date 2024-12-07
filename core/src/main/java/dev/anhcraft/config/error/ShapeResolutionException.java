package dev.anhcraft.config.error;

import dev.anhcraft.config.context.Context;
import org.jetbrains.annotations.NotNull;

/**
 * Thrown when a shape cannot be resolved.
 */
public class ShapeResolutionException extends ContextException {
  public ShapeResolutionException(@NotNull Context context, @NotNull String message) {
    super(context, message);
  }

  public ShapeResolutionException(
      @NotNull Context context, @NotNull String message, @NotNull Throwable cause) {
    super(context, message, cause);
  }
}
