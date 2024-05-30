package dev.anhcraft.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class SettingFlagTest {

  @Test
  public void testNormalizerFlags() {
    byte settings = 0;

    settings = SettingFlag.set(settings, SettingFlag.Normalizer.DEEP_CLONE, true);
    assertTrue(SettingFlag.has(settings, SettingFlag.Normalizer.DEEP_CLONE));
    settings = SettingFlag.set(settings, SettingFlag.Normalizer.DEEP_CLONE, false);
    assertFalse(SettingFlag.has(settings, SettingFlag.Normalizer.DEEP_CLONE));

    settings = SettingFlag.set(settings, SettingFlag.Normalizer.IGNORE_DEFAULT_VALUES, true);
    assertTrue(SettingFlag.has(settings, SettingFlag.Normalizer.IGNORE_DEFAULT_VALUES));
    settings = SettingFlag.set(settings, SettingFlag.Normalizer.IGNORE_DEFAULT_VALUES, false);
    assertFalse(SettingFlag.has(settings, SettingFlag.Normalizer.IGNORE_DEFAULT_VALUES));

    settings = SettingFlag.set(settings, SettingFlag.Normalizer.IGNORE_EMPTY_ARRAY, true);
    assertTrue(SettingFlag.has(settings, SettingFlag.Normalizer.IGNORE_EMPTY_ARRAY));
    settings = SettingFlag.set(settings, SettingFlag.Normalizer.IGNORE_EMPTY_ARRAY, false);
    assertFalse(SettingFlag.has(settings, SettingFlag.Normalizer.IGNORE_EMPTY_ARRAY));

    settings = SettingFlag.set(settings, SettingFlag.Normalizer.IGNORE_EMPTY_DICTIONARY, true);
    assertTrue(SettingFlag.has(settings, SettingFlag.Normalizer.IGNORE_EMPTY_DICTIONARY));
    settings = SettingFlag.set(settings, SettingFlag.Normalizer.IGNORE_EMPTY_DICTIONARY, false);
    assertFalse(SettingFlag.has(settings, SettingFlag.Normalizer.IGNORE_EMPTY_DICTIONARY));
  }

  @Test
  public void testDenormalizerFlags() {
    byte settings = 0;

    settings = SettingFlag.set(settings, SettingFlag.Denormalizer.DEEP_CLONE, true);
    assertTrue(SettingFlag.has(settings, SettingFlag.Denormalizer.DEEP_CLONE));
    settings = SettingFlag.set(settings, SettingFlag.Denormalizer.DEEP_CLONE, false);
    assertFalse(SettingFlag.has(settings, SettingFlag.Denormalizer.DEEP_CLONE));

    settings = SettingFlag.set(settings, SettingFlag.Denormalizer.STRICT_NUMBER_PARSING, true);
    assertTrue(SettingFlag.has(settings, SettingFlag.Denormalizer.STRICT_NUMBER_PARSING));
    settings = SettingFlag.set(settings, SettingFlag.Denormalizer.STRICT_NUMBER_PARSING, false);
    assertFalse(SettingFlag.has(settings, SettingFlag.Denormalizer.STRICT_NUMBER_PARSING));

    settings = SettingFlag.set(settings, SettingFlag.Denormalizer.DISABLE_VALIDATION, true);
    assertTrue(SettingFlag.has(settings, SettingFlag.Denormalizer.DISABLE_VALIDATION));
    settings = SettingFlag.set(settings, SettingFlag.Denormalizer.DISABLE_VALIDATION, false);
    assertFalse(SettingFlag.has(settings, SettingFlag.Denormalizer.DISABLE_VALIDATION));
  }

  @Test
  public void testCombinedFlags() {
    byte settings = 0;

    settings = SettingFlag.set(settings, SettingFlag.Normalizer.DEEP_CLONE, true);
    settings = SettingFlag.set(settings, SettingFlag.Normalizer.IGNORE_DEFAULT_VALUES, true);
    settings = SettingFlag.set(settings, SettingFlag.Normalizer.IGNORE_EMPTY_ARRAY, true);

    assertTrue(SettingFlag.has(settings, SettingFlag.Normalizer.DEEP_CLONE));
    assertTrue(SettingFlag.has(settings, SettingFlag.Normalizer.IGNORE_DEFAULT_VALUES));
    assertTrue(SettingFlag.has(settings, SettingFlag.Normalizer.IGNORE_EMPTY_ARRAY));

    settings = SettingFlag.set(settings, SettingFlag.Normalizer.DEEP_CLONE, false);
    assertFalse(SettingFlag.has(settings, SettingFlag.Normalizer.DEEP_CLONE));
    assertTrue(SettingFlag.has(settings, SettingFlag.Normalizer.IGNORE_DEFAULT_VALUES));
    assertTrue(SettingFlag.has(settings, SettingFlag.Normalizer.IGNORE_EMPTY_ARRAY));

    settings = SettingFlag.set(settings, SettingFlag.Normalizer.IGNORE_DEFAULT_VALUES, false);
    settings = SettingFlag.set(settings, SettingFlag.Normalizer.IGNORE_EMPTY_ARRAY, false);

    assertFalse(SettingFlag.has(settings, SettingFlag.Normalizer.DEEP_CLONE));
    assertFalse(SettingFlag.has(settings, SettingFlag.Normalizer.IGNORE_DEFAULT_VALUES));
    assertFalse(SettingFlag.has(settings, SettingFlag.Normalizer.IGNORE_EMPTY_ARRAY));
  }
}
