package dev.anhcraft.config.blueprint;

import dev.anhcraft.config.validate.Validator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Represents a property in a {@link ClassSchema}.
 */
public class ClassProperty extends AbstractProperty {
  private final Field field;

  public ClassProperty(
    @NotNull PropertyNaming naming,
    @NotNull List<String> description,
    byte modifier,
    @NotNull Validator validator,
    @Nullable Processor normalizer,
    @Nullable Processor denormalizer,
    @NotNull Field field) {
    super(naming, description, modifier, validator, normalizer, denormalizer);
    this.field = field;
  }

  /**
   * Gets the type of this property.
   * @return the type
   */
  @NotNull
  public Type type() {
    return field.getGenericType();
  }

  /**
   * Gets the associated field.
   * @return the field
   */
  @NotNull public Field field() {
    return field;
  }
}
