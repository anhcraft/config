package dev.anhcraft.config.blueprint;

import dev.anhcraft.config.validate.Validator;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/**
 * A generic implementation of {@link Property}.
 */
public abstract class AbstractProperty implements Property {
  private final PropertyNaming naming;
  private final List<String> description;
  private final Validator validator;

  protected AbstractProperty(
      @NotNull PropertyNaming naming,
      @NotNull List<String> description,
      @NotNull Validator validator) {
    this.naming = naming;
    this.description = Collections.unmodifiableList(description);
    this.validator = validator;
  }

  @Override
  @NotNull public String name() {
    return naming.primary();
  }

  @Override
  public abstract String describeType(boolean simple);

  @Override
  @NotNull public Set<String> aliases() {
    return naming.aliases();
  }

  @Override
  @NotNull public List<String> description() {
    return description;
  }

  @Override
  @NotNull public Validator validator() {
    return validator;
  }
}
