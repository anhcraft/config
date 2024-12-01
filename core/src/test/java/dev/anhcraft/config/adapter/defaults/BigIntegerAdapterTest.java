package dev.anhcraft.config.adapter.defaults;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.context.Context;
import java.math.BigInteger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BigIntegerAdapterTest {
  private static Context context;
  private static BigIntegerAdapter adapter;

  @BeforeAll
  public static void setUp() {
    context = ConfigFactory.create().build().createContext();
    adapter = new BigIntegerAdapter();
  }

  @Test
  public void testSimplify() throws Exception {
    assertEquals("12345", adapter.simplify(context, BigInteger.class, new BigInteger("12345")));
    assertEquals("-98765", adapter.simplify(context, BigInteger.class, new BigInteger("-98765")));
    assertEquals("0", adapter.simplify(context, BigInteger.class, BigInteger.ZERO));
  }

  @Test
  public void testComplexifyNumber() throws Exception {
    assertEquals(BigInteger.valueOf(12345), adapter.complexify(context, 12345, BigInteger.class));
    assertEquals(
        BigInteger.valueOf(-67890), adapter.complexify(context, -67890L, BigInteger.class));
    assertEquals(BigInteger.valueOf(0), adapter.complexify(context, 0.0, BigInteger.class));
  }

  @Test
  public void testComplexifyString() throws Exception {
    assertEquals(new BigInteger("12345"), adapter.complexify(context, "12345", BigInteger.class));
    assertEquals(new BigInteger("-98765"), adapter.complexify(context, "-98765", BigInteger.class));
    assertEquals(BigInteger.ZERO, adapter.complexify(context, "0", BigInteger.class));
    assertThrows(
        NumberFormatException.class,
        () -> adapter.complexify(context, "not_a_number", BigInteger.class));
    assertThrows(
        NumberFormatException.class, () -> adapter.complexify(context, "123.45", BigInteger.class));
  }

  @Test
  public void testComplexifyUnsupportedType() throws Exception {
    assertNull(adapter.complexify(context, new Object(), BigInteger.class));
    assertNull(adapter.complexify(context, true, BigInteger.class));
  }
}
