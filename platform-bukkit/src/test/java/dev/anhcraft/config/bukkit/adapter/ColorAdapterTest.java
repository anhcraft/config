package dev.anhcraft.config.bukkit.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.SchemalessDictionary;
import dev.anhcraft.config.context.Context;
import org.bukkit.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ColorAdapterTest {
  private static Context context;
  private static ColorAdapter adapter;

  @BeforeAll
  public static void setUp() {
    context = ConfigFactory.create().build().createContext();
    adapter = ColorAdapter.INSTANCE;
  }

  @Test
  public void testSimplify() throws Exception {
    Color color1 = Color.fromRGB(255, 0, 0);
    Color color2 = Color.fromRGB(0, 255, 0);
    assertEquals(
        SchemalessDictionary.create()
            .put("alpha", 255)
            .put("red", 255)
            .put("green", 0)
            .put("blue", 0)
            .build(),
        adapter.simplify(context, Color.class, color1));
    assertEquals(
        SchemalessDictionary.create()
            .put("alpha", 255)
            .put("red", 0)
            .put("green", 255)
            .put("blue", 0)
            .build(),
        adapter.simplify(context, Color.class, color2));
  }

  @Test
  public void testComplexifyValid() throws Exception {
    assertEquals(
        Color.fromRGB(255, 0, 0),
        adapter.complexify(
            context,
            SchemalessDictionary.create()
                .put("alpha", 255)
                .put("red", 255)
                .put("green", 0)
                .put("blue", 0)
                .build(),
            Color.class));
    assertEquals(
        Color.fromRGB(0, 255, 0),
        adapter.complexify(
            context,
            SchemalessDictionary.create()
                .put("alpha", 255)
                .put("red", 0)
                .put("green", 255)
                .put("blue", 0)
                .build(),
            Color.class));
  }
}
