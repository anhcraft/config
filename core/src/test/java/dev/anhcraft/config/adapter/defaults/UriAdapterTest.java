package dev.anhcraft.config.adapter.defaults;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.error.InvalidValueException;
import java.net.URI;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class UriAdapterTest {
  private static Context context;
  private static UriAdapter adapter;

  @BeforeAll
  public static void setUp() {
    context = ConfigFactory.create().build().createContext();
    adapter = new UriAdapter();
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "https://google.com",
        "ftp://example.com",
        "file://localhost",
        "localhost",
        "127.0.0.1"
      })
  public void testSimplify(String uri) throws Exception {
    assertEquals(uri, adapter.simplify(context, URI.class, new URI(uri)));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "https://google.com",
        "ftp://example.com   ",
        "file://localhost",
        "localhost",
        "127.0.0.1"
      })
  public void testComplexifyIsUri(String uri) throws Exception {
    assertEquals(new URI(uri.trim()), adapter.complexify(context, uri, URI.class));
  }

  @ParameterizedTest
  @ValueSource(strings = {"http://example.com/path/to/resource%zz", "http ://1.2.333.43"})
  public void testComplexifyNotUri(String uri) {
    assertThrows(InvalidValueException.class, () -> adapter.complexify(context, uri, URI.class));
  }
}
