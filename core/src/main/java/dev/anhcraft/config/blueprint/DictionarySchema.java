package dev.anhcraft.config.blueprint;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Represents a dictionary schema.
 */
public class DictionarySchema extends AbstractSchema<DictionaryProperty> {
  public DictionarySchema(@NotNull List<DictionaryProperty> properties, @NotNull Map<String, DictionaryProperty> lookup) {
    super(properties, lookup);
  }
}
