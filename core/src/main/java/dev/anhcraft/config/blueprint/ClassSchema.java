package dev.anhcraft.config.blueprint;

import java.util.List;
import java.util.Map;
import java.util.Objects;
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

  @Override
  public String getIdentifier() {
    return type.getName();
  }

  @Override
  public String getName() {
    return type.getSimpleName();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ClassSchema)) return false;
    ClassSchema that = (ClassSchema) o;
    return Objects.equals(type, that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(type);
  }
}
