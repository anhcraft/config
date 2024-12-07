package dev.anhcraft.config.blueprint;

import dev.anhcraft.config.meta.*;
import java.lang.reflect.Field;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a property in a {@link ClassSchema}.<br/>
 * A declared property is one declared in the class schema, not from inheritance or embedding.<br/>
 * Class property is read-only and identifiable. The identity of a class property depends on the {@link Field} it represents
 * and the {@link ClassSchemaScanner} creates it. If two class properties represent the same class but coming from different
 * {@link ClassSchemaScanner}, they are considered different.<br>
 * Note: Two {@link Field} might have the same class path but different identity due to being loaded by two different
 * class loaders. Inherently, {@link ClassProperty} also depends on the class loader.
 */
public interface ClassProperty extends Property {

  /**
   * Gets the modifier of this property.
   * @return the modifier
   */
  byte modifier();

  /**
   * Checks if this property is annotated as {@link Optional}
   * @return whether the property is optional
   */
  boolean isOptional();

  /**
   * Checks if this property is annotated as {@link Transient}.<br>
   * Note: This is different from checking transient on the field.
   * @return whether the property is transient
   */
  boolean isTransient();

  /**
   * Checks if this property is annotated as {@link Constant}
   * Note: This is different from checking final on the field.
   * @return whether the property is constant
   */
  boolean isConstant();

  /**
   * Checks if this property is annotated as {@link Fallback}
   * @return whether the property is fallback
   */
  boolean isFallback();

  /**
   * Checks if this property is annotated as {@link Discriminator}
   * @return whether the property is discriminator
   */
  boolean isDiscriminator();

  /**
   * Gets the normalization processor.
   * @return the processor
   */
  @Nullable Processor normalizer();

  /**
   * Gets the denormalization processor.
   * @return the processor
   */
  @Nullable Processor denormalizer();

  /**
   * Gets the associated field.
   * @return the field
   */
  @NotNull Field field();
}
