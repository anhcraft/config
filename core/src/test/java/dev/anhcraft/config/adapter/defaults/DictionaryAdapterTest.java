package dev.anhcraft.config.adapter.defaults;

import static org.junit.jupiter.api.Assertions.*;

import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.Dictionary;
import dev.anhcraft.config.SchemalessDictionary;
import dev.anhcraft.config.context.Context;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DictionaryAdapterTest {
  private static Context context;
  private static DictionaryAdapter adapter;

  @BeforeAll
  public static void setUp() {
    context = ConfigFactory.create().build().createContext();
    adapter = new DictionaryAdapter();
  }

  @Test
  public void throwWhenSimplify() {
    assertThrowsExactly(
        UnsupportedOperationException.class,
        () -> {
          adapter.simplify(context, Dictionary.class, new SchemalessDictionary());
        });
  }

  @Test
  public void testShallowComplexify() throws Exception {
    Dictionary dict = new SchemalessDictionary();
    dict.put("foo", "bar");
    assertSame(dict, adapter.complexify(context, dict, Dictionary.class));
  }

  @Test
  public void testDeepComplexify() throws Exception {
    Context deepContext = ConfigFactory.create().deepClone(true).build().createContext();
    Dictionary dict = new SchemalessDictionary();
    dict.put("foo", "bar");
    assertNotSame(dict, adapter.complexify(deepContext, dict, Dictionary.class));
    assertEquals("bar", adapter.complexify(deepContext, dict, Dictionary.class).get("foo"));
  }
}
