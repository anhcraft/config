package dev.anhcraft.config.error;

/**
 * Thrown when the processor invocation fails.
 */
public class ProcessorInvocationException extends RuntimeException {
  public ProcessorInvocationException(String message) {
    super(message);
  }

  public ProcessorInvocationException(String message, Throwable cause) {
    super(message, cause);
  }
}
