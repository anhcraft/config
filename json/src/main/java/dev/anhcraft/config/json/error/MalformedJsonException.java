package dev.anhcraft.config.json.error;

import java.io.IOException;
import org.jetbrains.annotations.NotNull;

/**
 * Exception thrown to indicate that a malformed JSON has been encountered.
 */
public class MalformedJsonException extends IOException {

  /**
   * Constructs a new {@code MalformedJsonException} with the specified detail message.
   *
   * @param message the detail message
   */
  public MalformedJsonException(@NotNull String message) {
    super(message);
  }

  /**
   * Constructs a new {@code MalformedJsonException} with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the cause
   */
  public MalformedJsonException(@NotNull String message, @NotNull Throwable cause) {
    super(message, cause);
  }
}
