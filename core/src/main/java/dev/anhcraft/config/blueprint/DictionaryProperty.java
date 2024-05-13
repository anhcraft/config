package dev.anhcraft.config.blueprint;

import dev.anhcraft.config.validate.Validator;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a property in a {@link DictionarySchema}.
 */
public class DictionaryProperty extends AbstractProperty {
  public DictionaryProperty(
      @NotNull PropertyNaming naming,
      @NotNull List<String> description,
      byte modifier,
      @NotNull Validator validator,
      @Nullable Processor normalizer,
      @Nullable Processor denormalizer) {
    super(naming, description, modifier, validator, normalizer, denormalizer);
  }
}
