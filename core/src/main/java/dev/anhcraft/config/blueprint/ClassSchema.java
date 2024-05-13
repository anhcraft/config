package dev.anhcraft.config.blueprint;

import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a class schema associating with a class.
 */
public class ClassSchema extends AbstractSchema<ClassProperty> {
  private final Class<?> type;

  public ClassSchema(
      @NotNull Class<?> type,
      @NotNull List<ClassProperty> properties,
      @NotNull Map<String, ClassProperty> lookup) {
    super(properties, lookup);
    this.type = type;
  }

  /**
   * Gets the associated class.
   * @return the class
   */
  @NotNull public Class<?> type() {
    return type;
  }
}
