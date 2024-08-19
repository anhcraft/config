package dev.anhcraft.config.blueprint;

import dev.anhcraft.config.type.ComplexTypes;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a class schema associating with a class.<br>
 * Class schema is read-only and identifiable. The identity of a class schema depends on the {@link Class} it represents
 * and the {@link ClassSchemaScanner} creates it. If two class schemas represent the same class but coming from different
 * {@link ClassSchemaScanner}, they are considered different.<br>
 * Note: Two {@link Class} might have the same class path but different identity due to being loaded by two different
 * class loaders. Inherently, {@link ClassSchema} also depends on the class loader.
 */
public class ClassSchema extends AbstractSchema<ClassProperty> {
  private final ClassSchemaScanner scanner;
  private final int scannerIdentity;
  private final Class<?> type;
  private final ClassProperty fallback;
  private final List<ClassProperty> localProperties;
  private final Map<String, ClassProperty> localPropertyLookup;
  private final ClassProperty localFallback;

  private volatile ClassSchema parent;

  // 1st bit: whether the class has no parent
  private byte internalState;

  public ClassSchema(
      @NotNull ClassSchemaScanner scanner,
      @NotNull Class<?> type,
      @NotNull List<ClassProperty> properties,
      @NotNull Map<String, ClassProperty> lookup,
      @Nullable ClassProperty fallback,
      @NotNull List<ClassProperty> localProperties,
      @NotNull Map<String, ClassProperty> localPropertyLookup,
      @Nullable ClassProperty localFallback) {
    super(properties, lookup);
    this.scanner = scanner;
    this.scannerIdentity =
        System.identityHashCode(scanner); // avoid GC relocation and custom-defined #hashCode
    this.type = type;
    this.fallback = fallback;
    this.localProperties = Collections.unmodifiableList(localProperties);
    this.localPropertyLookup = Collections.unmodifiableMap(localPropertyLookup);
    this.localFallback = localFallback;

    // setup internal state
    this.internalState |=
        type.getSuperclass() != null
                && type.getSuperclass() != Object.class
                && ComplexTypes.isNormalClassOrAbstract(type.getSuperclass())
            ? 0
            : (byte) 1;
  }

  /**
   * Gets the parent class schema.<br>
   * A class schema has a parent if the represented class has a superclass that:
   * <ul>
   *   <li>Is not {@link Object}</li>
   *   <li>Is a normal class or an abstract class</li>
   * </ul>
   * @return the parent class schema or {@code null} if not exists
   */
  public @Nullable ClassSchema parent() {
    if ((internalState & 1) == 1) {
      return null;
    }

    ClassSchema parentRef = parent;
    if (parentRef == null) {
      synchronized (this) {
        parentRef = parent;
        if (parentRef == null) {
          parent = parentRef = scanner.getOrScanSchema(type.getSuperclass());
        }
      }
    }

    return parentRef;
  }

  /**
   * Gets the associated class.
   * @return the class
   */
  @NotNull public Class<?> type() {
    return type;
  }

  /**
   * Gets the effective fallback property.
   * @return the fallback
   */
  public @Nullable ClassProperty fallback() {
    return fallback;
  }

  @Override
  public String name() {
    return type.getSimpleName();
  }

  /**
   * Gets all local property names including primary names and aliases.<br>
   * A local property is one declared in the class schema, not from inheritance or embedding.
   * <b>Note:</b> Using this method to iterate over the properties may result in duplication
   * of {@link Property} because a property may have more than one name.
   * @return all property names
   */
  public @NotNull Set<String> localPropertyNames() {
    return localPropertyLookup.keySet();
  }

  /**
   * Returns all local properties in the schema.<br>
   * A local property is one declared in the class schema, not from inheritance or embedding.
   * @return all properties
   */
  public @NotNull List<ClassProperty> localProperties() {
    return localProperties;
  }

  /**
   * Looks up a local property by primary name or alias.<br>
   * A local property is one declared in the class schema, not from inheritance or embedding.
   * @param name property name
   * @return property
   */
  public @Nullable ClassProperty localProperty(@Nullable String name) {
    return localPropertyLookup.get(name);
  }

  /**
   * Gets the local fallback property.
   * @return the fallback
   */
  public @Nullable ClassProperty localFallback() {
    return localFallback;
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
