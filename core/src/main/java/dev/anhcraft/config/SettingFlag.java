package dev.anhcraft.config;

import dev.anhcraft.config.error.InvalidValueException;

/**
 * Flags to control the behavior of various Config components.
 */
public class SettingFlag {
  /**
   * Flags to control the behavior of normalization.
   */
  public enum Normalizer {
    /**
     * Deep clones simple values. This applies to arrays, dictionaries and theirs descendants.
     */
    DEEP_CLONE,

    /**
     * Ignores default values when normalizing.<br>
     * The default value including number and boolean.
     */
    IGNORE_DEFAULT_VALUES,

    /**
     * Ignores empty array when normalizing.
     */
    IGNORE_EMPTY_ARRAY,

    /**
     * Ignores empty dictionary when normalizing.
     */
    IGNORE_EMPTY_DICTIONARY
  }

  /**
   * Flags to control the behavior of denormalization.
   */
  public enum Denormalizer {
    /**
     * Deep clones simple values. This applies to arrays, dictionaries and theirs descendants.
     */
    DEEP_CLONE,

    /**
     * When parsing a number, strictly checks the number range. For example:
     * <ul>
     *     <li>Without this flag: {@code adaptByte("255.001") == -1}</li>
     *     <li>With this flag: {@code adaptByte("255.001")} throws {@link InvalidValueException}</li>
     * </ul>
     * When this flag is unset, the denormalizer parses the string as {@link Double} first, and then casts it to
     * the desired number type. When it is set, the denormalizer parses the string using the "parse" method of
     * the desired number type, e.g: {@link Integer#parseInt(String)} for the integer adapter.<br>
     * Note: the string is always trimmed before parsing.
     */
    STRICT_NUMBER_PARSING,

    /**
     * Disables validation in denormalization.
     */
    DISABLE_VALIDATION
  }
}
