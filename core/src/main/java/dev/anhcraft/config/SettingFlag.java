package dev.anhcraft.config;

public class SettingFlag {
    public static class Normalizer {
        public static final byte DEEP_CLONE = 1;
        public static final byte IGNORE_DEFAULT_VALUES = 2;
        public static final byte IGNORE_EMPTY_ARRAY = 4;
        public static final byte IGNORE_EMPTY_DICTIONARY = 8;
    }

    public static class Denormalizer {
        public static final byte DEEP_CLONE = 1;
    }

    public static byte set(byte settings, byte flag, boolean state) {
        if (state) settings |= flag;
        else settings &= (byte) ~flag;
        return settings;
    }

    public static boolean has(byte settings, byte flag) {
        return settings == (settings & flag);
    }
}
