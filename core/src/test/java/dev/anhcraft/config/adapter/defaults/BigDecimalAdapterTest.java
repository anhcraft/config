package dev.anhcraft.config.adapter.defaults;

import static org.junit.jupiter.api.Assertions.*;

import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.context.Context;
import java.math.BigDecimal;
import java.math.MathContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BigDecimalAdapterTest {
  private static final double EPSILON = 1e-8;
  private static Context context;
  private static BigDecimalAdapter adapter;

  @BeforeAll
  public static void setUp() {
    context = ConfigFactory.create().build().createContext();
    adapter = new BigDecimalAdapter(MathContext.DECIMAL128);
  }

  private void assertBigDecimalEquals(BigDecimal expected, BigDecimal actual) {
    assertTrue(expected.subtract(actual).abs().compareTo(BigDecimal.valueOf(EPSILON)) <= 0);
  }

  @Test
  public void testSimplify() {
    assertEquals(
        "12345.6789", adapter.simplify(context, BigDecimal.class, new BigDecimal("12345.6789")));
    assertEquals(
        "-98765.4321", adapter.simplify(context, BigDecimal.class, new BigDecimal("-98765.4321")));
    assertEquals("0", adapter.simplify(context, BigDecimal.class, BigDecimal.ZERO));
  }

  @Test
  public void testComplexifyNumber() {
    assertBigDecimalEquals(
        new BigDecimal("12345.6789", MathContext.DECIMAL128),
        adapter.complexify(context, 12345.6789, BigDecimal.class));
    assertEquals(
        new BigDecimal(-67890L, MathContext.DECIMAL128),
        adapter.complexify(context, -67890L, BigDecimal.class));
    assertEquals(BigDecimal.ZERO, adapter.complexify(context, 0.0, BigDecimal.class));
  }

  @Test
  public void testComplexifyString() {
    assertBigDecimalEquals(
        new BigDecimal("12345.6789"), adapter.complexify(context, "12345.6789", BigDecimal.class));
    assertBigDecimalEquals(
        new BigDecimal("-98765.4321"),
        adapter.complexify(context, "-98765.4321", BigDecimal.class));
    assertBigDecimalEquals(BigDecimal.ZERO, adapter.complexify(context, "0.0", BigDecimal.class));
    assertThrows(
        NumberFormatException.class, () -> adapter.complexify(context, "NaN", BigDecimal.class));
    assertThrows(
        NumberFormatException.class,
        () -> adapter.complexify(context, "123.45.67", BigDecimal.class));
  }

  @Test
  public void testComplexifyUnsupportedType() {
    assertNull(adapter.complexify(context, new Object(), BigDecimal.class));
    assertNull(adapter.complexify(context, true, BigDecimal.class));
  }
}
