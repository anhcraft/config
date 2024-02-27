package dev.anhcraft.config.context;

import dev.anhcraft.config.blueprint.Property;
import org.jetbrains.annotations.NotNull;

/**
 * A property scope involves a property and a setting.<br>
 * Both the normalization and the denormalization relies on the schema, so the property is guaranteed to exist.
 * The setting, however, may not exist in denormalization if no setting exists in the configuration.
 */
public class PropertyScope implements Scope {
  private final Property property;
  private final String setting;

  public PropertyScope(@NotNull Property property, @NotNull String setting) {
    this.property = property;
    this.setting = setting;
  }

  /**
   * Gets the property involved in this scope.
   * @return the property
   */
  public @NotNull Property getProperty() {
    return property;
  }

  /**
   * Gets the setting involved in this scope.<br>
   * During normalization, the setting is the primary name of the property.<br>
   * During denormalization, the setting is the first existing setting found in the configuration using the primary
   * name or aliases. However, it is possible that none exists in the configuration.
   * @return the setting or <b>empty</b> if no setting exists
   */
  public @NotNull String getSetting() {
    return setting;
  }
}
