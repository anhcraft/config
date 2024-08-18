package dev.anhcraft.config.blueprint;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a class schema associating with a class.<br>
 * Class schema is read-only and identifiable. The identity of a class schema depends on the {@link Class} it represents
 * and the {@link SchemaScanner} creates it. If two class schemas represent the same class but coming from different
 * {@link SchemaScanner}, they are considered different.<br>
 * Note: Two {@link Class} might have the same class path but different identity due to being loaded by two different
 * class loaders. Inherently, {@link ClassSchema} also depends on the class loader.
 */
public class ClassSchema extends AbstractSchema<ClassProperty> {
  private final int scannerIdentity;
  private final Class<?> type;
  private final ClassProperty fallback;

  public ClassSchema(
      @Nullable SchemaScanner<?> scanner,
      @NotNull Class<?> type,
      @NotNull List<ClassProperty> properties,
      @NotNull Map<String, ClassProperty> lookup,
      @Nullable ClassProperty fallback) {
    super(properties, lookup);
    // use identity-hash-code to avoid GC and custom #hashCode
    this.scannerIdentity = scanner == null ? 0 : System.identityHashCode(scanner);
    this.type = type;
    this.fallback = fallback;
  }

  /**
   * Gets the associated class.
   * @return the class
   */
  @NotNull public Class<?> type() {
    return type;
  }

  /**
   * Gets the fallback property.
   * @return the fallback
   */
  public @Nullable ClassProperty fallback() {
    return fallback;
  }

  @Override
  public String name() {
    return type.getSimpleName();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ClassSchema)) return false;
    ClassSchema that = (ClassSchema) o;
    return Objects.equals(scannerIdentity, that.scannerIdentity) && Objects.equals(type, that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(scannerIdentity, type);
  }
}
