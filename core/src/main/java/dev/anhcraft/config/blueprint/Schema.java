package dev.anhcraft.config.blueprint;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the schema of a configuration.
 */
public interface Schema<T extends Property> {
  /**
   * Gets the name of the schema.
   * @return the name
   */
  @Nullable String name();

  /**
   * Gets all property names including primary names and aliases.<br>
   * <b>Note:</b> Using this method to iterate over the properties may result in duplication
   * of {@link Property} because a property may have more than one name.
   * @return all property names
   */
  @NotNull Set<String> propertyNames();

  /**
   * Returns all properties in the schema.
   * @return all properties
   */
  @NotNull List<T> properties();

  /**
   * Looks up a property by primary name or alias.
   * @param name property name
   * @return property
   */
  @Nullable T property(@Nullable String name);
}
