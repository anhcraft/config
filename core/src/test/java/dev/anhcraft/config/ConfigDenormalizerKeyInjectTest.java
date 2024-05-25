package dev.anhcraft.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.anhcraft.config.context.*;
import dev.anhcraft.config.context.injector.EntryKeyInjector;
import dev.anhcraft.config.meta.Fallback;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ConfigDenormalizerKeyInjectTest {
  public static class Config {
    @Fallback public Map<String, Item> items;
  }

  public static class Item {
    public String id;
    public String name;
  }

  private static ConfigFactory factory;

  @BeforeAll
  public static void setUp() {
    factory =
        ConfigFactory.create()
            .provideContext(
                new ContextProvider() {
                  @Override
                  public @NotNull Context provideDenormalizationContext(
                      @NotNull ConfigFactory factory) {
                    return new InjectableContext(factory).inject(new EntryKeyInjector());
                  }
                })
            .build();
  }

  @Test
  public void testDenormalizeInjectKey() throws Exception {
    SchemalessDictionary dict =
        SchemalessDictionary.create()
            .put("item1", SchemalessDictionary.create().put("name", "Laptop").build())
            .put("item2", SchemalessDictionary.create().put("name", "Smartphone").build())
            .put("item3", SchemalessDictionary.create().put("name", "Tablet").build())
            .put("item4", SchemalessDictionary.create().put("name", "Monitor").build())
            .put("item5", SchemalessDictionary.create().put("name", "Keyboard").build())
            .put("item6", SchemalessDictionary.create().put("name", "Mouse").build())
            .put("item7", SchemalessDictionary.create().put("name", "Printer").build())
            .put("item8", SchemalessDictionary.create().put("name", "Router").build())
            .put("item9", SchemalessDictionary.create().put("name", "Headphones").build())
            .put("item10", SchemalessDictionary.create().put("name", "Webcam").build())
            .build();

    Config cfg = (Config) factory.getDenormalizer().denormalize(dict, Config.class);

    assertTrue(cfg.items.containsKey("item1"));
    assertEquals("item1", cfg.items.get("item1").id);
    assertEquals("Laptop", cfg.items.get("item1").name);

    assertTrue(cfg.items.containsKey("item2"));
    assertEquals("item2", cfg.items.get("item2").id);
    assertEquals("Smartphone", cfg.items.get("item2").name);

    assertTrue(cfg.items.containsKey("item3"));
    assertEquals("item3", cfg.items.get("item3").id);
    assertEquals("Tablet", cfg.items.get("item3").name);

    assertTrue(cfg.items.containsKey("item4"));
    assertEquals("item4", cfg.items.get("item4").id);
    assertEquals("Monitor", cfg.items.get("item4").name);

    assertTrue(cfg.items.containsKey("item5"));
    assertEquals("item5", cfg.items.get("item5").id);
    assertEquals("Keyboard", cfg.items.get("item5").name);

    assertTrue(cfg.items.containsKey("item6"));
    assertEquals("item6", cfg.items.get("item6").id);
    assertEquals("Mouse", cfg.items.get("item6").name);

    assertTrue(cfg.items.containsKey("item7"));
    assertEquals("item7", cfg.items.get("item7").id);
    assertEquals("Printer", cfg.items.get("item7").name);

    assertTrue(cfg.items.containsKey("item8"));
    assertEquals("item8", cfg.items.get("item8").id);
    assertEquals("Router", cfg.items.get("item8").name);

    assertTrue(cfg.items.containsKey("item9"));
    assertEquals("item9", cfg.items.get("item9").id);
    assertEquals("Headphones", cfg.items.get("item9").name);

    assertTrue(cfg.items.containsKey("item10"));
    assertEquals("item10", cfg.items.get("item10").id);
    assertEquals("Webcam", cfg.items.get("item10").name);
  }
}
