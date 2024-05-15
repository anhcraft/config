package dev.anhcraft.config.blueprint;

import dev.anhcraft.config.meta.Constant;
import dev.anhcraft.config.meta.Optional;
import dev.anhcraft.config.meta.Transient;
import dev.anhcraft.config.validate.Validator;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A generic implementation of {@link Property}.
 */
public abstract class AbstractProperty implements Property {
  private final PropertyNaming naming;
  private final List<String> description;
  private final byte modifier; // optional, transient, constant
  private final Validator validator;
  private final Processor normalizer;
  private final Processor denormalizer;

  protected AbstractProperty(
      @NotNull PropertyNaming naming,
      @NotNull List<String> description,
      byte modifier,
      @NotNull Validator validator,
      @Nullable Processor normalizer,
      @Nullable Processor denormalizer) {
    this.naming = naming;
    this.description = Collections.unmodifiableList(description);
    this.modifier = modifier;
    this.validator = validator;
    this.normalizer = normalizer;
    this.denormalizer = denormalizer;
  }

  /**
   * Gets the primary name of this property.
   * @return the primary name
   */
  @Override
  @NotNull public String name() {
    return naming.primary();
  }

  @Override
  public abstract String describeType(boolean simple);

  /**
   * Gets all aliases of this property.
   * @return the aliases
   */
  @Override
  @NotNull public LinkedHashSet<String> aliases() {
    return naming.aliases();
  }

  /**
   * Gets the description of this property.
   * @return the description
   */
  @Override
  @NotNull public List<String> description() {
    return description;
  }

  /**
   * Gets the modifier of this property.
   * @return the modifier
   */
  @Override
  public byte modifier() {
    return modifier;
  }

  /**
   * Checks if this property is annotated as {@link Optional}
   * @return whether the property is optional
   */
  @Override
  public boolean isOptional() {
    return (modifier & MODIFIER_OPTIONAL) == MODIFIER_OPTIONAL;
  }

  /**
   * Checks if this property is annotated as {@link Transient}.<br>
   * Note: This is different from checking transient on the field.
   * @return whether the property is transient
   */
  @Override
  public boolean isTransient() {
    return (modifier & MODIFIER_TRANSIENT) == MODIFIER_TRANSIENT;
  }

  /**
   * Checks if this property is annotated as {@link Constant}
   * Note: This is different from checking final on the field.
   * @return whether the property is constant
   */
  @Override
  public boolean isConstant() {
    return (modifier & MODIFIER_CONSTANT) == MODIFIER_CONSTANT;
  }

  /**
   * Gets the validator of this property.
   * @return the validator
   */
  @Override
  @NotNull public Validator validator() {
    return validator;
  }

  /**
   * Gets the normalization processor.
   * @return the processor
   */
  @Override
  @Nullable public Processor normalizer() {
    return normalizer;
  }

  /**
   * Gets the denormalization processor.
   * @return the processor
   */
  @Override
  @Nullable public Processor denormalizer() {
    return denormalizer;
  }
}
