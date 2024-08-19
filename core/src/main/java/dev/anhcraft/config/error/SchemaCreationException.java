package dev.anhcraft.config.error;

import org.jetbrains.annotations.NotNull;

/**
 * Unable to create a schema.
 */
public class SchemaCreationException extends RuntimeException {

  public SchemaCreationException(@NotNull String message) {
    super(message);
  }

  public SchemaCreationException(@NotNull String message, @NotNull Throwable cause) {
    super(message, cause);
  }
}
