package dev.anhcraft.config.internal.blueprint;

import dev.anhcraft.config.blueprint.AbstractSchema;
import dev.anhcraft.config.blueprint.ClassProperty;
import dev.anhcraft.config.blueprint.ClassSchema;
import dev.anhcraft.config.blueprint.ClassSchemaScanner;
import dev.anhcraft.config.type.ComplexTypes;
import java.util.*;
import java.util.stream.Collectors;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class ClassSchemaImpl extends AbstractSchema<ClassProperty> implements ClassSchema {
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

  public ClassSchemaImpl(
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

  @Override
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

  @Override
  @NotNull public Class<?> type() {
    return type;
  }

  @Override
  public @Nullable ClassProperty propertyByField(@Nullable String fieldName) {
    return fieldName2Property.get(fieldName);
  }

  @Override
  public @Nullable ClassProperty fallback() {
    return fallback;
  }

  @Override
  public String name() {
    return type.getSimpleName();
  }

  @Override
  public @NotNull Set<String> declaredPropertyNames() {
    return declaredPropertyLookup.keySet();
  }

  @Override
  public @NotNull List<ClassProperty> declaredProperties() {
    return declaredProperties;
  }

  @Override
  public @Nullable ClassProperty declaredProperty(@Nullable String name) {
    return declaredPropertyLookup.get(name);
  }

  @Override
  public @Nullable ClassProperty declaredPropertyByField(@Nullable String fieldName) {
    return declaredFieldName2Property.get(fieldName);
  }

  @Override
  public @Nullable ClassProperty declaredFallback() {
    return declaredFallback;
  }

  @Override
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

  @Override
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
    if (!(o instanceof ClassSchemaImpl)) return false;
    ClassSchemaImpl that = (ClassSchemaImpl) o;
    return Objects.equals(scannerIdentity, that.scannerIdentity) && Objects.equals(type, that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(scannerIdentity, type);
  }
}
