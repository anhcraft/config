package dev.anhcraft.config;

import dev.anhcraft.config.type.SimpleTypes;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * A schemaless dictionary has no schema associated, as such, its content grows dynamically and fit any simple objects.<br>
 * This implementation is not thread-safe.
 * @see Dictionary
 */
public class SchemalessDictionary extends AbstractDictionary {
  /**
   * Makes a copy of the given map as a {@link SchemalessDictionary}.<br>
   * The map is shallow-copied. Changes to the dictionary does not reflect in the original map, however, indirect
   * changes made to the underlying values does reflect.<br>
   * The given map must contain simple values only, otherwise, {@link IllegalArgumentException} throws.
   * @param map the map
   * @return the dictionary
   */
  public static @NotNull SchemalessDictionary copyOf(@NotNull Map<String, Object> map) {
    SchemalessDictionary container = new SchemalessDictionary();
    container.putAll(map);
    return container;
  }

  @Override
  protected void onPut(String name, Object value) {}

  @Override
  public @NotNull Dictionary duplicate(boolean deepCopy) {
    if (deepCopy) {
      LinkedHashMap<String, Object> backend = unwrap();
      for (Map.Entry<String, Object> entry : backend.entrySet()) {
        entry.setValue(SimpleTypes.deepClone(entry.getValue()));
      }
      return copyOf(backend);
    }
    return copyOf(this);
  }

  /**
   * Creates a new {@link SchemalessDictionaryBuilder}.
   * @return the builder
   */
  public static @NotNull SchemalessDictionaryBuilder create() {
    return new SchemalessDictionaryBuilder();
  }

  public static class SchemalessDictionaryBuilder {
    private final LinkedHashMap<String, Object> backend = new LinkedHashMap<>();

    public @NotNull SchemalessDictionaryBuilder put(String key, Object value) {
      if (!SimpleTypes.test(value)) {
        throw new IllegalArgumentException("The given value is not a simple type");
      }
      backend.put(key, value);
      return this;
    }

    public @NotNull SchemalessDictionaryBuilder putAll(@NotNull Map<String, Object> map) {
      backend.putAll(map);
      return this;
    }

    public @NotNull SchemalessDictionaryBuilder putAll(@NotNull Dictionary dictionary) {
      return putAll(dictionary.unwrap());
    }

    public @NotNull SchemalessDictionary build() {
      return copyOf(backend);
    }
  }
}
