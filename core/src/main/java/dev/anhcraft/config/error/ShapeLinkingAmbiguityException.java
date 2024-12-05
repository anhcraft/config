package dev.anhcraft.config.error;

/**
 * Occurs there is ambiguity in shape linking.
 */
public class ShapeLinkingAmbiguityException extends RuntimeException {
  public ShapeLinkingAmbiguityException(String message) {
    super(message);
  }
}
