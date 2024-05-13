package dev.anhcraft.config.blueprint;

import dev.anhcraft.config.validate.Validator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents a property in a {@link DictionarySchema}.
 */
public class DictionaryProperty extends AbstractProperty {
  public DictionaryProperty(@NotNull PropertyNaming naming, @NotNull List<String> description, byte modifier,
                            @NotNull Validator validator, @Nullable Processor normalizer, @Nullable Processor denormalizer) {
    super(naming, description, modifier, validator, normalizer, denormalizer);
  }
}
