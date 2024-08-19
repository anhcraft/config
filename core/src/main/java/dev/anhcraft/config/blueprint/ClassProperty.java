package dev.anhcraft.config.blueprint;

import dev.anhcraft.config.meta.Constant;
import dev.anhcraft.config.meta.Fallback;
import dev.anhcraft.config.meta.Optional;
import dev.anhcraft.config.meta.Transient;
import java.lang.reflect.Field;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a property in a {@link ClassSchema}.
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
