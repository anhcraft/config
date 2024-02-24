package dev.anhcraft.config;

public class SettingFlag {
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

    public static class Denormalizer {
        /**
         * See: {@link ConfigFactory.Builder#deepClone(boolean)}
         */
        public static final byte DEEP_CLONE = 1;

        /**
         * See: {@link ConfigFactory.Builder#strictNumberParsing(boolean)}
         */
        public static final byte STRICT_NUMBER_PARSING = 2;
    }

    public static byte set(byte settings, byte flag, boolean state) {
        if (state) settings |= flag;
        else settings &= (byte) ~flag;
        return settings;
    }

    public static boolean has(byte settings, byte flag) {
        return flag == (settings & flag);
    }
}
