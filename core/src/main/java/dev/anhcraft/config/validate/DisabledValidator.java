package dev.anhcraft.config.validate;

import dev.anhcraft.config.validate.check.Validation;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * A disabled validator always let the value pass.
 */
public class DisabledValidator implements Validator {
  public static final Validator INSTANCE = new DisabledValidator();

  @Override
  public boolean check(Object value) {
    return true;
  }

  @Override
  public @NotNull String message() {
    return "";
  }

  @Override
  public @NotNull List<Validation> validations() {
    return List.of();
  }

  @Override
  public boolean silent() {
    return true;
  }
}
