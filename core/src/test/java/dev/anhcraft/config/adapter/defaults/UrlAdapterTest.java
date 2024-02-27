package dev.anhcraft.config.adapter.defaults;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.error.InvalidValueException;
import java.net.URL;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class UrlAdapterTest {
  private static Context context;
  private static UrlAdapter adapter;

  @BeforeAll
  public static void setUp() {
    context = ConfigFactory.create().build().createContext();
    adapter = new UrlAdapter();
  }

  @ParameterizedTest
  @ValueSource(strings = {"https://google.com", "ftp://example.com", "file://localhost"})
  public void testSimplify(String url) throws Exception {
    assertEquals(url, adapter.simplify(context, URL.class, new URL(url)));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "https://google.com",
        "ftp://example.com   ",
        "http://example.com/path/to/resource%zz"
      })
  public void testComplexifyIsUrl(String url) throws Exception {
    assertEquals(new URL(url.trim()), adapter.complexify(context, url, URL.class));
  }

  @ParameterizedTest
  @ValueSource(strings = {"localhost", "http ://1.2.333.43", "127.0.0.1"})
  public void testComplexifyNotUrl(String url) {
    assertThrows(InvalidValueException.class, () -> adapter.complexify(context, url, URL.class));
  }
}
