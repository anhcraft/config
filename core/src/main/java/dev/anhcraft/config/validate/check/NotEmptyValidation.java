package dev.anhcraft.config.validate.check;

import java.util.Collection;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * Validates that the given object is not empty.<br>
 * Supports: {@link String}, {@link Collection}, {@link Iterable}, {@link Map} (including dictionary)
 */
public class NotEmptyValidation implements Validation {
  @Override
  public boolean check(Object value) {
    if (value instanceof String) return !((String) value).isEmpty();
    else if (value instanceof Collection) return !((Collection<?>) value).isEmpty();
    else if (value instanceof Iterable) return ((Iterable<?>) value).iterator().hasNext();
    else if (value instanceof Map) return !((Map<?, ?>) value).isEmpty();
    return true;
  }

  @Override
  public @NotNull String message() {
    return "must be not-empty";
  }
}
