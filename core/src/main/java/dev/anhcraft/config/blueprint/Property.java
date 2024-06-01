package dev.anhcraft.config.blueprint;

import dev.anhcraft.config.validate.Validator;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

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
   * Modifier to mark fallback properties.
   */
  byte MODIFIER_FALLBACK = 8;

  /**
   * Gets the primary name of this property.
   *
   * @return the primary name
   */
  @NotNull String name();

  /**
   * Gets the type of this property.
   * @return the type
   */
  @NotNull Type type();

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
  @NotNull Set<String> aliases();

  /**
   * Gets the description of this property.
   *
   * @return the description
   */
  @NotNull List<String> description();

  /**
   * Gets the validator of this property.
   *
   * @return the validator
   */
  @NotNull Validator validator();
}
