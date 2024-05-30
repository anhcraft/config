package dev.anhcraft.config;

/**
 * Flags to control the behavior of various Config components.
 */
public class SettingFlag {
  /**
   * Flags to control the behavior of normalization.
   */
  public static class Normalizer {
    /**
     * See: {@link ConfigFactory.Builder#deepClone(boolean)}
     */
    public static final byte DEEP_CLONE = 1;

    /**
     * See: {@link ConfigFactory.Builder#ignoreDefaultValues(boolean)}
     */
    public static final byte IGNORE_DEFAULT_VALUES = 2;

    /**
     * See: {@link ConfigFactory.Builder#ignoreEmptyArray(boolean)}
     */
    public static final byte IGNORE_EMPTY_ARRAY = 4;

    /**
     * See: {@link ConfigFactory.Builder#ignoreEmptyDictionary(boolean)}
     */
    public static final byte IGNORE_EMPTY_DICTIONARY = 8;
  }

  /**
   * Flags to control the behavior of denormalization.
   */
  public static class Denormalizer {
    /**
     * See: {@link ConfigFactory.Builder#deepClone(boolean)}
     */
    public static final byte DEEP_CLONE = 1;

    /**
     * See: {@link ConfigFactory.Builder#strictNumberParsing(boolean)}
     */
    public static final byte STRICT_NUMBER_PARSING = 2;

    /**
     * See: {@link ConfigFactory.Builder#disableValidation(boolean)}
     */
    public static final byte DISABLE_VALIDATION = 4;
  }

  /**
   * Sets the state of the given flag in the given settings.
   * @param settings the settings
   * @param flag the flag
   * @param state whether to set or clear the flag
   * @return the new settings
   */
  public static byte set(byte settings, byte flag, boolean state) {
    if (state) settings |= flag;
    else settings &= (byte) ~flag;
    return settings;
  }

  /**
   * Checks if the given flag is set in the given settings.
   * @param settings the settings
   * @param flag the flag
   * @return whether the flag is set
   */
  public static boolean has(byte settings, byte flag) {
    return flag == (settings & flag);
  }
}
