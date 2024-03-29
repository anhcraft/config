package dev.anhcraft.config.error;

import org.jetbrains.annotations.NotNull;

/**
 * A schema is unsupported.
 */
public class UnsupportedSchemaException extends RuntimeException {

  public UnsupportedSchemaException(@NotNull String message) {
    super(message);
  }

  public UnsupportedSchemaException(@NotNull String message, @NotNull Throwable cause) {
    super(message, cause);
  }
}
