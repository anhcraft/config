package dev.anhcraft.config.blueprint;

import java.util.List;
import java.util.Set;
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
public interface ClassSchema extends Schema<ClassProperty> {
  /**
   * Gets the parent class schema.<br>
   * A class schema has a parent if the represented class has a superclass that:
   * <ul>
   *   <li>Is not {@link Object}</li>
   *   <li>Is a normal class or an abstract class</li>
   * </ul>
   * @return the parent class schema or {@code null} if not exists
   */
  @Nullable ClassSchema parent();

  /**
   * Gets the associated class.
   * @return the class
   */
  @NotNull Class<?> type();

  /**
   * Looks up a property by the field name.
   * @param fieldName field name
   * @return property
   * @see ClassProperty
   */
  @Nullable ClassProperty propertyByField(@Nullable String fieldName);

  /**
   * Gets the effective fallback property.
   * @return the fallback
   */
  @Nullable ClassProperty fallback();

  /**
   * Gets all declared property names including primary names and aliases.<br>
   * <b>Note:</b> Using this method to iterate over the properties may result in duplication
   * of {@link Property} because a property may have more than one name.
   * @return all property names
   */
  @NotNull Set<String> declaredPropertyNames();

  /**
   * Returns all declared properties in the schema.
   * @return all properties
   * @see ClassProperty
   */
  @NotNull List<ClassProperty> declaredProperties();

  /**
   * Looks up a declared property by primary name or alias.
   * @param name property name
   * @return property
   * @see ClassProperty
   */
  @Nullable ClassProperty declaredProperty(@Nullable String name);

  /**
   * Looks up a declared property by the field name.
   * @param fieldName declared field name
   * @return property
   * @see ClassProperty
   */
  @Nullable ClassProperty declaredPropertyByField(@Nullable String fieldName);

  /**
   * Gets the declared fallback property.
   * @return the fallback
   * @see ClassProperty
   */
  @Nullable ClassProperty declaredFallback();

  /**
   * Gets all effective discriminator property names including primary names and aliases.<br>
   * <b>Note:</b> Using this method to iterate over the properties may result in duplication
   * of {@link Property} because a property may have more than one name.
   * @return all "declared" and "inherited" effective discriminator property names
   */
  @NotNull Set<String> effectiveDiscriminatorNames();

  /**
   * Returns all effective discriminator properties in the schema.
   * @return all "declared" and "inherited" effective discriminator properties
   */
  @NotNull List<ClassProperty> effectiveDiscriminators();
}
