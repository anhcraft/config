package dev.anhcraft.config.blueprint;

import dev.anhcraft.config.validate.Validator;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
class ClassPropertyImpl extends AbstractProperty implements ClassProperty {
  private final int scannerIdentity;
  private final Field field;
  private final byte modifier; // optional, transient, constant
  private final Processor normalizer;
  private final Processor denormalizer;

  ClassPropertyImpl(
      @NotNull ClassSchemaScanner scanner,
      @NotNull PropertyNaming naming,
      @NotNull List<String> description,
      @NotNull Validator validator,
      @NotNull Field field,
      byte modifier,
      @Nullable Processor normalizer,
      @Nullable Processor denormalizer) {
    super(naming, description, validator);
    this.scannerIdentity =
        System.identityHashCode(scanner); // avoid GC relocation and custom-defined #hashCode
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

  @Override
  public boolean isDiscriminator() {
    return (modifier & MODIFIER_DISCRIMINATOR) == MODIFIER_DISCRIMINATOR;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ClassPropertyImpl)) return false;
    ClassPropertyImpl that = (ClassPropertyImpl) o;
    return Objects.equals(scannerIdentity, that.scannerIdentity)
        && Objects.equals(field, that.field);
  }

  @Override
  public int hashCode() {
    return Objects.hash(scannerIdentity, field);
  }
}
