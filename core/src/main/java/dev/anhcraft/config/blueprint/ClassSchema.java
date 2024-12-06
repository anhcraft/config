package dev.anhcraft.config.blueprint;

import dev.anhcraft.config.type.ComplexTypes;
import java.util.*;
import java.util.stream.Collectors;
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
  private final Map<String, ClassProperty> fieldName2Property;
  private final ClassProperty fallback;
  private final List<ClassProperty> declaredProperties;
  private final Map<String, ClassProperty> declaredPropertyLookup;
  private final Map<String, ClassProperty> declaredFieldName2Property;
  private final ClassProperty declaredFallback;

  private volatile ClassSchema parent;
  private volatile List<ClassProperty> discriminatorProperties;
  private volatile Set<String> discriminatorPropertyNames;

  // 1st bit: whether the class has no parent

  private byte internalState;

  public ClassSchema(
      @NotNull ClassSchemaScanner scanner,
      @NotNull Class<?> type,
      @NotNull List<ClassProperty> properties,
      @NotNull Map<String, ClassProperty> lookup,
      @NotNull Map<String, ClassProperty> fieldName2Property,
      @Nullable ClassProperty fallback,
      @NotNull List<ClassProperty> declaredProperties,
      @NotNull Map<String, ClassProperty> declaredPropertyLookup,
      @NotNull Map<String, ClassProperty> declaredFieldName2Property,
      @Nullable ClassProperty declaredFallback) {
    super(properties, lookup);
    this.scanner = scanner;
    this.scannerIdentity =
        System.identityHashCode(scanner); // avoid GC relocation and custom-defined #hashCode
    this.type = type;
    this.fieldName2Property = Collections.unmodifiableMap(fieldName2Property);
    this.fallback = fallback;
    this.declaredProperties = Collections.unmodifiableList(declaredProperties);
    this.declaredPropertyLookup = Collections.unmodifiableMap(declaredPropertyLookup);
    this.declaredFieldName2Property = Collections.unmodifiableMap(declaredFieldName2Property);
    this.declaredFallback = declaredFallback;

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
   * Looks up a property by the field name.
   * @param fieldName field name
   * @return property
   * @see ClassProperty
   */
  public @Nullable ClassProperty propertyByField(@Nullable String fieldName) {
    return fieldName2Property.get(fieldName);
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
   * Gets all declared property names including primary names and aliases.<br>
   * <b>Note:</b> Using this method to iterate over the properties may result in duplication
   * of {@link Property} because a property may have more than one name.
   * @return all property names
   */
  public @NotNull Set<String> declaredPropertyNames() {
    return declaredPropertyLookup.keySet();
  }

  /**
   * Returns all declared properties in the schema.
   * @return all properties
   * @see ClassProperty
   */
  public @NotNull List<ClassProperty> declaredProperties() {
    return declaredProperties;
  }

  /**
   * Looks up a declared property by primary name or alias.
   * @param name property name
   * @return property
   * @see ClassProperty
   */
  public @Nullable ClassProperty declaredProperty(@Nullable String name) {
    return declaredPropertyLookup.get(name);
  }

  /**
   * Looks up a declared property by the field name.
   * @param fieldName declared field name
   * @return property
   * @see ClassProperty
   */
  public @Nullable ClassProperty declaredPropertyByField(@Nullable String fieldName) {
    return declaredFieldName2Property.get(fieldName);
  }

  /**
   * Gets the declared fallback property.
   * @return the fallback
   * @see ClassProperty
   */
  public @Nullable ClassProperty declaredFallback() {
    return declaredFallback;
  }

  /**
   * Gets all effective discriminator property names including primary names and aliases.<br>
   * <b>Note:</b> Using this method to iterate over the properties may result in duplication
   * of {@link Property} because a property may have more than one name.
   * @return all "declared" and "inherited" effective discriminator property names
   */
  public @NotNull Set<String> effectiveDiscriminatorNames() {
    if (discriminatorPropertyNames == null) {
      synchronized (this) {
        if (discriminatorPropertyNames == null) {
          discoverEffectiveDiscriminators();
        }
      }
    }
    return discriminatorPropertyNames;
  }

  /**
   * Returns all effective discriminator properties in the schema.
   * @return all "declared" and "inherited" effective discriminator properties
   */
  public @NotNull List<ClassProperty> effectiveDiscriminators() {
    if (discriminatorProperties == null) {
      synchronized (this) {
        if (discriminatorProperties == null) {
          discoverEffectiveDiscriminators();
        }
      }
    }
    return discriminatorProperties;
  }

  private void discoverEffectiveDiscriminators() {
    if (discriminatorProperties != null && discriminatorPropertyNames != null) return;

    discriminatorProperties =
        properties().stream()
            .filter(ClassProperty::isDiscriminator)
            .collect(Collectors.toUnmodifiableList());
    discriminatorPropertyNames =
        properties().stream()
            .filter(ClassProperty::isDiscriminator)
            .map(p -> p.field().getName())
            .collect(Collectors.toUnmodifiableSet());
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
