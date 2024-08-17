package dev.anhcraft.config.adapter.defaults;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.SettingFlag;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.error.InvalidValueException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class IntegerAdapterTest {
  private static Context context;
  private static IntegerAdapter adapter;

  @BeforeAll
  public static void setUp() {
    context = ConfigFactory.create().build().createContext();
    adapter = new IntegerAdapter();
  }

  @Test
  public void throwWhenSimplify() {
    assertThrowsExactly(
        UnsupportedOperationException.class,
        () -> {
          adapter.simplify(context, Integer.class, 1);
        });
  }

  @SuppressWarnings("ConstantValue")
  @Test
  public void testComplexifyNumber() throws Exception {
    assertEquals(Integer.MAX_VALUE, adapter.complexify(context, Integer.MAX_VALUE, Integer.class));
    assertEquals(Short.MIN_VALUE, adapter.complexify(context, Short.MIN_VALUE, Integer.class));
    assertEquals(Integer.MIN_VALUE, adapter.complexify(context, Integer.MIN_VALUE, Integer.class));
    assertEquals((int) Long.MIN_VALUE, adapter.complexify(context, Long.MIN_VALUE, Integer.class));
    assertEquals(
        (int) Float.MIN_VALUE, adapter.complexify(context, Float.MIN_VALUE, Integer.class));
    assertEquals(
        (int) Double.MIN_VALUE, adapter.complexify(context, Double.MIN_VALUE, Integer.class));
    assertEquals(Short.MAX_VALUE, adapter.complexify(context, Short.MAX_VALUE, Integer.class));
    assertEquals(Integer.MAX_VALUE, adapter.complexify(context, Integer.MAX_VALUE, Integer.class));
    assertEquals((int) Long.MAX_VALUE, adapter.complexify(context, Long.MAX_VALUE, Integer.class));
    assertEquals(
        (int) Float.MAX_VALUE, adapter.complexify(context, Float.MAX_VALUE, Integer.class));
    assertEquals(
        (int) Double.MAX_VALUE, adapter.complexify(context, Double.MAX_VALUE, Integer.class));
  }

  @Test
  public void testComplexifyString() throws Exception {
    assertEquals(0, adapter.complexify(context, "0", Integer.class));
    assertEquals(1, adapter.complexify(context, "1.001", Integer.class));
    assertEquals(-2, adapter.complexify(context, "-2.1", Integer.class));
    assertEquals(255, adapter.complexify(context, " 255 ", Integer.class));
    assertThrowsExactly(
        InvalidValueException.class, () -> adapter.complexify(context, "", Integer.class));
    assertThrowsExactly(
        InvalidValueException.class, () -> adapter.complexify(context, "one", Integer.class));
  }

  @Test
  public void testComplexifyStringStrictly() throws Exception {
    Context strict =
        ConfigFactory.create()
            .enableDenormalizerSetting(SettingFlag.Denormalizer.STRICT_NUMBER_PARSING)
            .build()
            .createContext();
    assertEquals(0, adapter.complexify(strict, "0", Integer.class));
    assertEquals(255, adapter.complexify(strict, " 255 ", Integer.class));
    assertThrowsExactly(
        InvalidValueException.class, () -> adapter.complexify(strict, "", Integer.class));
    assertThrowsExactly(
        InvalidValueException.class, () -> adapter.complexify(strict, "one", Integer.class));
    assertThrowsExactly(
        InvalidValueException.class, () -> adapter.complexify(strict, "1.001", Integer.class));
    assertThrowsExactly(
        InvalidValueException.class, () -> adapter.complexify(strict, "-2.1", Integer.class));
  }

  @Test
  public void testComplexifyCharacter() throws Exception {
    assertEquals('1', adapter.complexify(context, '1', Integer.class));
    assertEquals('2', adapter.complexify(context, '2', Integer.class));
    assertEquals('à', adapter.complexify(context, 'à', Integer.class));
  }

  @Test
  public void testComplexifyBoolean() throws Exception {
    assertEquals(0, adapter.complexify(context, false, Integer.class));
    assertEquals(1, adapter.complexify(context, true, Integer.class));
  }
}
