package dev.anhcraft.config.validate;

import dev.anhcraft.config.validate.check.Validation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Aggregates multiple validation and checks all of them.
 */
public class AggregatedValidator implements Validator {
  private final List<Validation> validations;
  private final ThreadLocal<String> lastMessage = new ThreadLocal<>();
  private final boolean silent;

  public AggregatedValidator(@NotNull Validation[] validators, boolean silent) {
    this.validations = Collections.unmodifiableList(Arrays.asList(validators));
    this.silent = silent;
  }

  /**
   * Checks the given value against all validators.<br>
   * Immediately returns {@code false} when a check fails.
   * @param value the value to check
   * @return {@code true} if all checks pass
   */
  @Override
  public boolean check(@Nullable Object value) {
    for (Validation validation : validations) {
      if (!validation.check(value)) {
        lastMessage.set(validation.message());
        return false;
      }
    }
    return true;
  }

  @Override
  public @NotNull List<Validation> validations() {
    return validations;
  }

  @Override
  public @NotNull String message() {
    return lastMessage.get();
  }

  @Override
  public boolean silent() {
    return silent;
  }
}
