package dev.anhcraft.config.blueprint;

import dev.anhcraft.config.meta.Constant;
import dev.anhcraft.config.meta.Fallback;
import dev.anhcraft.config.meta.Optional;
import dev.anhcraft.config.meta.Transient;
import dev.anhcraft.config.validate.Validator;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a property in a {@link ClassSchema}.
 */
public class ClassProperty extends AbstractProperty {
  private final Field field;
  private final byte modifier; // optional, transient, constant
  private final Processor normalizer;
  private final Processor denormalizer;

  public ClassProperty(
      @NotNull PropertyNaming naming,
      @NotNull List<String> description,
      @NotNull Validator validator,
      @NotNull Field field,
      byte modifier,
      @Nullable Processor normalizer,
      @Nullable Processor denormalizer) {
    super(naming, description, validator);
    this.field = field;
    this.modifier = modifier;
    this.normalizer = normalizer;
    this.denormalizer = denormalizer;
  }

  @Override
  public @NotNull Type type() {
    return field.getGenericType();
  }

  /**
   * Gets the modifier of this property.
   * @return the modifier
   */
  public byte modifier() {
    return modifier;
  }

  /**
   * Checks if this property is annotated as {@link Optional}
   * @return whether the property is optional
   */
  public boolean isOptional() {
    return (modifier & MODIFIER_OPTIONAL) == MODIFIER_OPTIONAL;
  }

  /**
   * Checks if this property is annotated as {@link Transient}.<br>
   * Note: This is different from checking transient on the field.
   * @return whether the property is transient
   */
  public boolean isTransient() {
    return (modifier & MODIFIER_TRANSIENT) == MODIFIER_TRANSIENT;
  }

  /**
   * Checks if this property is annotated as {@link Constant}
   * Note: This is different from checking final on the field.
   * @return whether the property is constant
   */
  public boolean isConstant() {
    return (modifier & MODIFIER_CONSTANT) == MODIFIER_CONSTANT;
  }

  /**
   * Checks if this property is annotated as {@link Fallback}
   * @return whether the property is constant
   */
  public boolean isFallback() {
    return field.getAnnotation(Fallback.class) != null;
  }

  /**
   * Gets the normalization processor.
   * @return the processor
   */
  @Nullable public Processor normalizer() {
    return normalizer;
  }

  /**
   * Gets the denormalization processor.
   * @return the processor
   */
  @Nullable public Processor denormalizer() {
    return denormalizer;
  }

  /**
   * Gets the associated field.
   * @return the field
   */
  @NotNull public Field field() {
    return field;
  }
}
