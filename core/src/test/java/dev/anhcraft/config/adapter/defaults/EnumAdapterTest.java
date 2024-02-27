package dev.anhcraft.config.adapter.defaults;

import static org.junit.jupiter.api.Assertions.*;

import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.context.Context;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class EnumAdapterTest {
  private static Context context;
  private static EnumAdapter adapter;

  @BeforeAll
  public static void setUp() {
    context = ConfigFactory.create().build().createContext();
    adapter = new EnumAdapter();
  }

  @Test
  public void testSimplify() throws Exception {
    assertEquals("apple", adapter.simplify(context, Enum.class, Fruit.APPLE));
    assertEquals("banana", adapter.simplify(context, Enum.class, Fruit.BANANA));
    assertEquals("orange", adapter.simplify(context, Enum.class, Fruit.ORANGE));
    assertEquals("grapes", adapter.simplify(context, Enum.class, Fruit.GRAPES));
    assertEquals("strawberry", adapter.simplify(context, Enum.class, Fruit.STRAWBERRY));
  }

  @Test
  public void testComplexify() throws Exception {
    assertSame(Fruit.APPLE, adapter.complexify(context, "apple", Fruit.class));
    assertSame(Fruit.BANANA, adapter.complexify(context, " BANANA ", Fruit.class));
    assertSame(Fruit.ORANGE, adapter.complexify(context, "OrangE", Fruit.class));
    assertSame(Fruit.GRAPES, adapter.complexify(context, "gRAPes", Fruit.class));
    assertSame(Fruit.STRAWBERRY, adapter.complexify(context, "     STRAWBERRY", Fruit.class));
    assertNull(adapter.complexify(context, " STRAW BERRY", Fruit.class));
  }

  public enum Fruit {
    APPLE,
    BANANA,
    ORANGE,
    GRAPES,
    STRAWBERRY
  }
}
