package dev.anhcraft.config;

import dev.anhcraft.config.blueprint.DictionarySchema;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a dictionary containing simple objects only.<br>
 * The dictionary does not allow {@code null} keys and values.
 */
public interface Dictionary extends Map<String, Object> {
  // ======== Helpers ========

  /**
   * Creates a mutable dictionary from the given map.<br>
   * If the schema is provided, a {@link ConstrainedDictionary} will be created, otherwise a
   * {@link SchemalessDictionary} will be created.
   * @param map the map
   * @param schema the schema
   * @return the dictionary
   */
  static @NotNull Dictionary of(
      @NotNull Map<String, Object> map, @Nullable DictionarySchema schema) {
    return schema == null
        ? SchemalessDictionary.copyOf(map)
        : ConstrainedDictionary.copyOf(map, schema);
  }

  /**
   * Creates a mutable {@link SchemalessDictionary} from the given map.
   * @param map the map
   * @return the dictionary
   * @see Dictionary#of(Map, DictionarySchema)
   */
  static @NotNull Dictionary of(@NotNull Map<String, Object> map) {
    return of(map, null);
  }

  // ======== Extra interfaces ========

  /**
   * Searches for an entry with the given name and aliases.
   *
   * @param name    the name
   * @param aliases the aliases
   * @return the entry or {@code null}
   */
  @Nullable Map.Entry<String, Object> search(@NotNull String name, @NotNull Iterable<String> aliases);

  /**
   * Gets the key at the given index.
   *
   * @param pos the index
   * @return the key
   */
  @Nullable String getKeyAt(int pos);

  /**
   * Gets the value at the given index.
   *
   * @param pos the index
   * @return the value
   */
  @Nullable Object getValueAt(int pos);

  /**
   * Renames an entry with a new key.<br>
   * Using the new key may override another existing entry.
   *
   * @param from the current key
   * @param to   the new key
   * @return the old value previously at the new key or {@code null}
   */
  @Nullable Object rename(@NotNull String from, @NotNull String to);

  /**
   * Unwraps the dictionary into a map.
   *
   * @return shallow-copied, mutable map
   */
  @NotNull LinkedHashMap<String, Object> unwrap();

  /**
   * Checks if this dictionary is compatible with the given dictionary schema.<br>
   * The check is performed recursively.
   * @param schema the dictionary schema
   * @return true if compatible
   */
  boolean isCompatibleWith(@Nullable DictionarySchema schema);

  /**
   * Creates an immutable view of this dictionary.<br>
   * If the dictionary is already immutable, it will return itself.
   * @return shallow-immutable dictionary
   */
  @NotNull Dictionary immutable();

  /**
   * Duplicates this dictionary.<br>
   * If the dictionary is immutable, it will return itself in shallow-copy mode.
   * Otherwise, a new immutable, deep copy dictionary will be created.
   * @param deepCopy if true, the dictionary will be deep-copied
   * @return duplicated dictionary
   */
  @NotNull Dictionary duplicate(boolean deepCopy);

  /**
   * Duplicates this dictionary.
   * @return shallow-copied dictionary
   */
  @NotNull default Dictionary duplicate() {
    return duplicate(false);
  }
}
