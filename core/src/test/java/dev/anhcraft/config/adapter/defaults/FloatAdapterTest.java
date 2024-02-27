package dev.anhcraft.config.adapter.defaults;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.error.InvalidValueException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FloatAdapterTest {
  private static Context context;
  private static FloatAdapter adapter;

  @BeforeAll
  public static void setUp() {
    context = ConfigFactory.create().build().createContext();
    adapter = new FloatAdapter();
  }

  @Test
  public void throwWhenSimplify() {
    assertThrowsExactly(
        UnsupportedOperationException.class,
        () -> {
          adapter.simplify(context, Float.class, 1f);
        });
  }

  @SuppressWarnings("DataFlowIssue")
  @Test
  public void testComplexifyNumber() throws Exception {
    assertEquals(0.5f, adapter.complexify(context, 0.5f, Float.class), 1e-8);
    assertEquals(2.0000f, adapter.complexify(context, 2, Float.class), 1e-8);
    assertEquals(-0.0005f, adapter.complexify(context, -0.0005d, Float.class), 1e-8);
    assertEquals(3f, adapter.complexify(context, (byte) 3, Float.class), 1e-8);
  }

  @SuppressWarnings("DataFlowIssue")
  @Test
  public void testComplexifyString() throws Exception {
    assertEquals(3.005f, adapter.complexify(context, "3.005", Float.class), 1e-8);
    assertEquals(2f, adapter.complexify(context, "2 ", Float.class), 1e-8);
    assertEquals(12f, adapter.complexify(context, "0012", Float.class), 1e-8);
    assertEquals(-999.001009f, adapter.complexify(context, "-999.001009", Float.class), 1e-8);
    assertEquals(0.000001f, adapter.complexify(context, "0.000001", Float.class), 1e-8);
    assertEquals(Float.MAX_VALUE, adapter.complexify(context, "  3.4028235E38", Float.class), 1e-8);
    assertEquals(
        Float.POSITIVE_INFINITY, adapter.complexify(context, "  3.4028235E39", Float.class), 1e-8);
    assertThrowsExactly(
        InvalidValueException.class, () -> adapter.complexify(context, "", Float.class));
    assertThrowsExactly(
        InvalidValueException.class, () -> adapter.complexify(context, "--2.3", Float.class));
    assertThrowsExactly(
        InvalidValueException.class, () -> adapter.complexify(context, "0.000a", Float.class));
    assertThrowsExactly(
        InvalidValueException.class, () -> adapter.complexify(context, "1..02", Float.class));
  }

  @SuppressWarnings("DataFlowIssue")
  @Test
  public void testComplexifyStringStrictly() throws Exception {
    Context strict = ConfigFactory.create().strictNumberParsing(true).build().createContext();
    assertEquals(3.005f, adapter.complexify(strict, "3.005", Float.class), 1e-8);
    assertEquals(2f, adapter.complexify(strict, "2 ", Float.class), 1e-8);
    assertEquals(12f, adapter.complexify(strict, "0012", Float.class), 1e-8);
    assertEquals(-999.001009f, adapter.complexify(strict, "-999.001009", Float.class), 1e-8);
    assertEquals(0.000001f, adapter.complexify(strict, "0.000001", Float.class), 1e-8);
    assertEquals(Float.MAX_VALUE, adapter.complexify(context, "  3.4028235E38", Float.class), 1e-8);
    assertEquals(
        Float.POSITIVE_INFINITY, adapter.complexify(context, "  3.4028235E39", Float.class), 1e-8);
    assertThrowsExactly(
        InvalidValueException.class, () -> adapter.complexify(strict, "", Float.class));
    assertThrowsExactly(
        InvalidValueException.class, () -> adapter.complexify(strict, "--2.3", Float.class));
    assertThrowsExactly(
        InvalidValueException.class, () -> adapter.complexify(strict, "0.000a", Float.class));
    assertThrowsExactly(
        InvalidValueException.class, () -> adapter.complexify(strict, "1..02", Float.class));
  }

  @SuppressWarnings("DataFlowIssue")
  @Test
  public void testComplexifyCharacter() throws Exception {
    assertEquals('1', adapter.complexify(context, '1', Float.class), 1e-8);
    assertEquals('2', adapter.complexify(context, '2', Float.class), 1e-8);
  }

  @SuppressWarnings("DataFlowIssue")
  @Test
  public void testComplexifyBoolean() throws Exception {
    assertEquals(0f, adapter.complexify(context, false, Float.class), 1e-8);
    assertEquals(1f, adapter.complexify(context, true, Float.class), 1e-8);
  }
}
