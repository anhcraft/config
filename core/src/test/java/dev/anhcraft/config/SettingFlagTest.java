package dev.anhcraft.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class SettingFlagTest {

  @Test
  public void testNormalizerFlags() {
    byte settings = 0;

    // Test setting and checking DEEP_CLONE flag
    settings = SettingFlag.set(settings, SettingFlag.Normalizer.DEEP_CLONE, true);
    assertTrue(SettingFlag.has(settings, SettingFlag.Normalizer.DEEP_CLONE));
    settings = SettingFlag.set(settings, SettingFlag.Normalizer.DEEP_CLONE, false);
    assertFalse(SettingFlag.has(settings, SettingFlag.Normalizer.DEEP_CLONE));

    // Test setting and checking IGNORE_DEFAULT_VALUES flag
    settings = SettingFlag.set(settings, SettingFlag.Normalizer.IGNORE_DEFAULT_VALUES, true);
    assertTrue(SettingFlag.has(settings, SettingFlag.Normalizer.IGNORE_DEFAULT_VALUES));
    settings = SettingFlag.set(settings, SettingFlag.Normalizer.IGNORE_DEFAULT_VALUES, false);
    assertFalse(SettingFlag.has(settings, SettingFlag.Normalizer.IGNORE_DEFAULT_VALUES));

    // Test setting and checking IGNORE_EMPTY_ARRAY flag
    settings = SettingFlag.set(settings, SettingFlag.Normalizer.IGNORE_EMPTY_ARRAY, true);
    assertTrue(SettingFlag.has(settings, SettingFlag.Normalizer.IGNORE_EMPTY_ARRAY));
    settings = SettingFlag.set(settings, SettingFlag.Normalizer.IGNORE_EMPTY_ARRAY, false);
    assertFalse(SettingFlag.has(settings, SettingFlag.Normalizer.IGNORE_EMPTY_ARRAY));

    // Test setting and checking IGNORE_EMPTY_DICTIONARY flag
    settings = SettingFlag.set(settings, SettingFlag.Normalizer.IGNORE_EMPTY_DICTIONARY, true);
    assertTrue(SettingFlag.has(settings, SettingFlag.Normalizer.IGNORE_EMPTY_DICTIONARY));
    settings = SettingFlag.set(settings, SettingFlag.Normalizer.IGNORE_EMPTY_DICTIONARY, false);
    assertFalse(SettingFlag.has(settings, SettingFlag.Normalizer.IGNORE_EMPTY_DICTIONARY));
  }

  @Test
  public void testDenormalizerFlags() {
    byte settings = 0;

    // Test setting and checking DEEP_CLONE flag
    settings = SettingFlag.set(settings, SettingFlag.Denormalizer.DEEP_CLONE, true);
    assertTrue(SettingFlag.has(settings, SettingFlag.Denormalizer.DEEP_CLONE));
    settings = SettingFlag.set(settings, SettingFlag.Denormalizer.DEEP_CLONE, false);
    assertFalse(SettingFlag.has(settings, SettingFlag.Denormalizer.DEEP_CLONE));

    // Test setting and checking STRICT_NUMBER_PARSING flag
    settings = SettingFlag.set(settings, SettingFlag.Denormalizer.STRICT_NUMBER_PARSING, true);
    assertTrue(SettingFlag.has(settings, SettingFlag.Denormalizer.STRICT_NUMBER_PARSING));
    settings = SettingFlag.set(settings, SettingFlag.Denormalizer.STRICT_NUMBER_PARSING, false);
    assertFalse(SettingFlag.has(settings, SettingFlag.Denormalizer.STRICT_NUMBER_PARSING));
  }

  @Test
  public void testCombinedFlags() {
    byte settings = 0;

    // Test setting multiple flags
    settings = SettingFlag.set(settings, SettingFlag.Normalizer.DEEP_CLONE, true);
    settings = SettingFlag.set(settings, SettingFlag.Normalizer.IGNORE_DEFAULT_VALUES, true);
    settings = SettingFlag.set(settings, SettingFlag.Normalizer.IGNORE_EMPTY_ARRAY, true);

    assertTrue(SettingFlag.has(settings, SettingFlag.Normalizer.DEEP_CLONE));
    assertTrue(SettingFlag.has(settings, SettingFlag.Normalizer.IGNORE_DEFAULT_VALUES));
    assertTrue(SettingFlag.has(settings, SettingFlag.Normalizer.IGNORE_EMPTY_ARRAY));

    // Test clearing one flag and checking the others
    settings = SettingFlag.set(settings, SettingFlag.Normalizer.DEEP_CLONE, false);
    assertFalse(SettingFlag.has(settings, SettingFlag.Normalizer.DEEP_CLONE));
    assertTrue(SettingFlag.has(settings, SettingFlag.Normalizer.IGNORE_DEFAULT_VALUES));
    assertTrue(SettingFlag.has(settings, SettingFlag.Normalizer.IGNORE_EMPTY_ARRAY));

    // Test clearing all flags
    settings = SettingFlag.set(settings, SettingFlag.Normalizer.IGNORE_DEFAULT_VALUES, false);
    settings = SettingFlag.set(settings, SettingFlag.Normalizer.IGNORE_EMPTY_ARRAY, false);

    assertFalse(SettingFlag.has(settings, SettingFlag.Normalizer.DEEP_CLONE));
    assertFalse(SettingFlag.has(settings, SettingFlag.Normalizer.IGNORE_DEFAULT_VALUES));
    assertFalse(SettingFlag.has(settings, SettingFlag.Normalizer.IGNORE_EMPTY_ARRAY));
  }
}
