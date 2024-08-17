package dev.anhcraft.config.adapter.defaults;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.SettingFlag;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.error.InvalidValueException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ShortAdapterTest {
  private static Context context;
  private static ShortAdapter adapter;

  @BeforeAll
  public static void setUp() {
    context = ConfigFactory.create().build().createContext();
    adapter = new ShortAdapter();
  }

  @Test
  public void throwWhenSimplify() {
    assertThrowsExactly(
        UnsupportedOperationException.class,
        () -> {
          adapter.simplify(context, Short.class, (short) 1);
        });
  }

  @SuppressWarnings("ConstantValue")
  @Test
  public void testComplexifyNumber() throws Exception {
    assertEquals(Short.MAX_VALUE, adapter.complexify(context, Short.MAX_VALUE, Short.class));
    assertEquals(
        (short) Integer.MIN_VALUE, adapter.complexify(context, Integer.MIN_VALUE, Short.class));
    assertEquals((short) Long.MIN_VALUE, adapter.complexify(context, Long.MIN_VALUE, Short.class));
    assertEquals(
        (short) Float.MIN_VALUE, adapter.complexify(context, Float.MIN_VALUE, Short.class));
    assertEquals(
        (short) Double.MIN_VALUE, adapter.complexify(context, Double.MIN_VALUE, Short.class));
    assertEquals(
        (short) Integer.MAX_VALUE, adapter.complexify(context, Integer.MAX_VALUE, Short.class));
    assertEquals((short) Long.MAX_VALUE, adapter.complexify(context, Long.MAX_VALUE, Short.class));
    assertEquals(
        (short) Float.MAX_VALUE, adapter.complexify(context, Float.MAX_VALUE, Short.class));
    assertEquals(
        (short) Double.MAX_VALUE, adapter.complexify(context, Double.MAX_VALUE, Short.class));
  }

  @Test
  public void testComplexifyString() throws Exception {
    assertEquals((short) 0, adapter.complexify(context, "0", Short.class));
    assertEquals((short) 1, adapter.complexify(context, "1.001", Short.class));
    assertEquals((short) -2, adapter.complexify(context, "-2.1", Short.class));
    assertEquals((short) 255, adapter.complexify(context, " 255 ", Short.class));
    assertThrowsExactly(
        InvalidValueException.class, () -> adapter.complexify(context, "", Short.class));
    assertThrowsExactly(
        InvalidValueException.class, () -> adapter.complexify(context, "one", Short.class));
  }

  @Test
  public void testComplexifyStringStrictly() throws Exception {
    Context strict =
        ConfigFactory.create()
            .enableDenormalizerSetting(SettingFlag.Denormalizer.STRICT_NUMBER_PARSING)
            .build()
            .createContext();
    assertEquals((short) 0, adapter.complexify(strict, "0", Short.class));
    assertEquals((short) 127, adapter.complexify(strict, "127", Short.class));
    assertEquals((short) -128, adapter.complexify(strict, " -128 ", Short.class));
    assertThrowsExactly(
        InvalidValueException.class, () -> adapter.complexify(strict, "1.001", Short.class));
    assertThrowsExactly(
        InvalidValueException.class, () -> adapter.complexify(context, "", Short.class));
    assertThrowsExactly(
        InvalidValueException.class, () -> adapter.complexify(context, "one", Short.class));
  }

  @Test
  public void testComplexifyCharacter() throws Exception {
    assertEquals((short) '1', adapter.complexify(context, '1', Short.class));
    assertEquals((short) '2', adapter.complexify(context, '2', Short.class));
    assertEquals((short) 'à', adapter.complexify(context, 'à', Short.class));
  }

  @Test
  public void testComplexifyBoolean() throws Exception {
    assertEquals((short) 0, adapter.complexify(context, false, Short.class));
    assertEquals((short) 1, adapter.complexify(context, true, Short.class));
  }
}
