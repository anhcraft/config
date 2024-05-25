package dev.anhcraft.config.error;

import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.context.injector.Injector;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an exception occurred within a {@link Injector}
 */
public class ContextInjectionException extends ContextException {
  public ContextInjectionException(@NotNull Context context, @NotNull String message) {
    super(context, message);
  }

  public ContextInjectionException(
      @NotNull Context context, @NotNull String message, @NotNull Throwable cause) {
    super(context, message, cause);
  }
}
