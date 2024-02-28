package dev.anhcraft.config.blueprint;

import dev.anhcraft.config.meta.Constant;
import dev.anhcraft.config.meta.Optional;
import dev.anhcraft.config.meta.Transient;
import dev.anhcraft.config.validate.Validator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Represents a property in a {@link Schema}.<br>
 * A property corresponds to a field in a class and vice versa (no matter if the property has aliases or not)<br>
 * The property is scanner-dependent and thus is factory-dependent even though in certain cases, it is possible
 * to compare two {@link Property} by comparing their associated {@link Field}
 */
public final class Property {
  public static final byte MODIFIER_OPTIONAL = 1;
  public static final byte MODIFIER_TRANSIENT = 2;
  public static final byte MODIFIER_CONSTANT = 4;

  private final PropertyNaming naming;
  private final List<String> description;
  private final byte modifier; // optional, transient, constant
  private final Validator validator;
  private final Field field;
  private final Processor normalizer;
  private final Processor denormalizer;

  public Property(
    @NotNull PropertyNaming naming,
    @NotNull List<String> description,
    byte modifier,
    @NotNull Validator validator,
    @NotNull Field field,
    @Nullable Processor normalizer,
    @Nullable Processor denormalizer) {
    this.naming = naming;
    this.description = Collections.unmodifiableList(description);
    this.modifier = modifier;
    this.validator = validator;
    this.field = field;
    this.normalizer = normalizer;
    this.denormalizer = denormalizer;
  }

  /**
   * Gets the primary name of this property.
   * @return the primary name
   */
  @NotNull public String name() {
    return naming.primary();
  }

  /**
   * Gets all aliases of this property.
   * @return the aliases
   */
  @NotNull public LinkedHashSet<String> aliases() {
    return naming.aliases();
  }

  /**
   * Gets the description of this property.
   * @return the description
   */
  @NotNull public List<String> description() {
    return description;
  }

  /**
   * Gets the modifier of this property.
   * @return the modifier
   */
  public byte modifier() {
    return modifier;
  }

  /**
   * Checks if this property is annotated as {@link Optional}
   * @return whether the property is optional
   */
  public boolean isOptional() {
    return (modifier & MODIFIER_OPTIONAL) == MODIFIER_OPTIONAL;
  }

  /**
   * Checks if this property is annotated as {@link Transient}.<br>
   * Note: This is different from checking transient on the field.
   * @return whether the property is transient
   */
  public boolean isTransient() {
    return (modifier & MODIFIER_TRANSIENT) == MODIFIER_TRANSIENT;
  }

  /**
   * Checks if this property is annotated as {@link Constant}
   * Note: This is different from checking final on the field.
   * @return whether the property is constant
   */
  public boolean isConstant() {
    return (modifier & MODIFIER_CONSTANT) == MODIFIER_CONSTANT;
  }

  /**
   * Gets the validator of this property.
   * @return the validator
   */
  @NotNull public Validator validator() {
    return validator;
  }

  /**
   * Gets the type of this property.
   * @return the type
   */
  @NotNull public Type type() {
    return field.getGenericType();
  }

  /**
   * Gets the associated field.
   * @return the field
   */
  @NotNull public Field field() {
    return field;
  }

  /**
   * Gets the normalization processor.
   * @return the processor
   */
  @Nullable public Processor normalizer() {
    return normalizer;
  }

  /**
   * Gets the denormalization processor.
   * @return the processor
   */
  @Nullable public Processor denormalizer() {
    return denormalizer;
  }
}
