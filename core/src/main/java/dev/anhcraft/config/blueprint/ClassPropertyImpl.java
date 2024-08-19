package dev.anhcraft.config.blueprint;

import dev.anhcraft.config.validate.Validator;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
class ClassPropertyImpl extends AbstractProperty implements ClassProperty {
  private final Field field;
  private final byte modifier; // optional, transient, constant
  private final Processor normalizer;
  private final Processor denormalizer;

  ClassPropertyImpl(
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

  public byte modifier() {
    return modifier;
  }

  public boolean isOptional() {
    return (modifier & MODIFIER_OPTIONAL) == MODIFIER_OPTIONAL;
  }

  public boolean isTransient() {
    return (modifier & MODIFIER_TRANSIENT) == MODIFIER_TRANSIENT;
  }

  public boolean isConstant() {
    return (modifier & MODIFIER_CONSTANT) == MODIFIER_CONSTANT;
  }

  public boolean isFallback() {
    return (modifier & MODIFIER_FALLBACK) == MODIFIER_FALLBACK;
  }

  @Nullable public Processor normalizer() {
    return normalizer;
  }

  @Nullable public Processor denormalizer() {
    return denormalizer;
  }

  @NotNull public Field field() {
    return field;
  }
}
