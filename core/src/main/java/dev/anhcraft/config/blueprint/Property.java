package dev.anhcraft.config.blueprint;

import dev.anhcraft.config.meta.Constant;
import dev.anhcraft.config.meta.Optional;
import dev.anhcraft.config.meta.Transient;
import dev.anhcraft.config.validate.Validator;
import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a property in a {@link Schema}.<br>
 * A property corresponds to a field in a class and vice versa (no matter if the property has aliases or not)<br>
 * The property is scanner-dependent and thus is factory-dependent even though in certain cases, it is possible
 * to compare two {@link Property} by comparing their associated {@link Field}
 */
public interface Property {
  /**
   * Modifier to mark optional properties.
   */
  byte MODIFIER_OPTIONAL = 1;

  /**
   * Modifier to mark transient properties.
   */
  byte MODIFIER_TRANSIENT = 2;

  /**
   * Modifier to mark constant properties.
   */
  byte MODIFIER_CONSTANT = 4;

  /**
   * Gets the primary name of this property.
   *
   * @return the primary name
   */
  @NotNull String name();

  /**
   * Describes the type of this property.
   * @param simple whether the type should be simplified
   * @return the type
   */
  String describeType(boolean simple);

  /**
   * Gets all aliases of this property.
   *
   * @return the aliases
   */
  @NotNull LinkedHashSet<String> aliases();

  /**
   * Gets the description of this property.
   *
   * @return the description
   */
  @NotNull List<String> description();

  /**
   * Gets the modifier of this property.
   *
   * @return the modifier
   */
  byte modifier();

  /**
   * Checks if this property is annotated as {@link Optional}
   *
   * @return whether the property is optional
   */
  boolean isOptional();

  /**
   * Checks if this property is annotated as {@link Transient}.<br>
   * Note: This is different from checking transient on the field.
   *
   * @return whether the property is transient
   */
  boolean isTransient();

  /**
   * Checks if this property is annotated as {@link Constant}
   * Note: This is different from checking final on the field.
   *
   * @return whether the property is constant
   */
  boolean isConstant();

  /**
   * Gets the validator of this property.
   *
   * @return the validator
   */
  @NotNull Validator validator();

  /**
   * Gets the normalization processor.
   *
   * @return the processor
   */
  @Nullable Processor normalizer();

  /**
   * Gets the denormalization processor.
   *
   * @return the processor
   */
  @Nullable Processor denormalizer();
}
