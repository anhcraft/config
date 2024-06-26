package dev.anhcraft.config;

import static org.junit.jupiter.api.Assertions.*;

import dev.anhcraft.config.blueprint.DictionarySchema;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ConstrainedDictionaryTest {
  @Nested
  class Schemaless {
    @Test
    public void testCopyOfShallow() {
      Dictionary foo = new ConstrainedDictionary(null);
      foo.put("a", 1);
      foo.put("b", new String[1]);
      Dictionary bar = Dictionary.of(foo);
      bar.put("a", 0);
      assertNotSame(foo, bar);
      assertNotEquals(foo.get("a"), bar.get("a"));
      assertSame(foo.get("b"), bar.get("b"));

      ((String[]) foo.get("b"))[0] = "foo";
      assertEquals(foo.get("b"), bar.get("b"));
    }

    @Test
    public void testDuplicateShallow() {
      Dictionary foo = new ConstrainedDictionary(null);
      foo.put("a", 1);
      foo.put("b", new String[1]);
      Dictionary bar = foo.duplicate();
      bar.put("a", 0);
      assertNotSame(foo, bar);
      assertNotEquals(foo.get("a"), bar.get("a"));
      assertSame(foo.get("b"), bar.get("b"));

      ((String[]) foo.get("b"))[0] = "foo";
      assertEquals(foo.get("b"), bar.get("b"));
    }

    @Test
    public void testDuplicateDeep() {
      Dictionary foo = new ConstrainedDictionary(null);
      foo.put("a", 1);
      foo.put("b", new String[1]);
      Dictionary bar = foo.duplicate(true);
      bar.put("a", 0);
      assertNotSame(foo, bar);
      assertNotEquals(foo.get("a"), bar.get("a"));
      assertNotSame(foo.get("b"), bar.get("b"));

      ((String[]) foo.get("b"))[0] = "foo";
      assertNotEquals(foo.get("b"), bar.get("b"));
    }

    @Test
    public void testPutCheckType() {
      Dictionary dict = new ConstrainedDictionary(null);
      assertDoesNotThrow(() -> dict.put("a", 3));
      assertDoesNotThrow(() -> dict.put("b", "99"));
      assertDoesNotThrow(() -> dict.put("c", new int[0]));
    }

    @SuppressWarnings("OverwrittenKey")
    @Test
    public void testPut() {
      Dictionary dict = new ConstrainedDictionary(null);
      dict.put("a", 3);
      dict.put("a", 5);
      assertEquals(5, dict.get("a"));
      dict.put("a", null);
      assertNull(dict.get("a"));
    }

    @Test
    public void testSearch() {
      Dictionary dict = new ConstrainedDictionary(null);
      dict.put("1", "a");
      dict.put("2", "b");
      dict.put("4", "c");
      dict.put("6", "d");
      assertEquals(Map.entry("1", "a"), dict.search("1", List.of("2")));
      assertEquals(Map.entry("4", "c"), dict.search("3", List.of("4", "1")));
      assertNull(dict.search("3", List.of("5")));
    }

    @Test
    public void testGetKeyAt() {
      Dictionary dict = new ConstrainedDictionary(null);
      dict.put("1", "a");
      dict.put("2", "b");
      dict.put("3", null);
      assertEquals("1", dict.getKeyAt(0));
      assertEquals("2", dict.getKeyAt(1));
      dict.put("4", "c");
      dict.put("1", null);
      dict.put("2", "e");
      dict.put("6", "d");
      assertEquals("4", dict.getKeyAt(1));
      assertEquals("6", dict.getKeyAt(2));
    }

    @Test
    public void testGetValueAt() {
      Dictionary dict = new ConstrainedDictionary(null);
      dict.put("1", "a");
      dict.put("2", "b");
      dict.put("3", null);
      assertEquals("a", dict.getValueAt(0));
      assertEquals("b", dict.getValueAt(1));
      dict.put("4", "c");
      dict.put("1", null);
      dict.put("2", "e");
      dict.put("6", "d");
      assertEquals("c", dict.getValueAt(1));
      assertEquals("d", dict.getValueAt(2));
    }

    @Test
    public void testRename() {
      Dictionary dict = new ConstrainedDictionary(null);
      dict.put("1", "a");
      dict.put("2", "b");
      assertNull(dict.rename("1", "3"));
      assertEquals("a", dict.get("3"));
      assertEquals("a", dict.rename("2", "3"));
    }

    @Test
    public void testIsCompatibleWith() {
      Dictionary dict = new ConstrainedDictionary(null);
      dict.put("1", "a");
      dict.put("2", true);
      dict.put("3", 2.3f);
      dict.put("4", new String[0]);
      dict.put("5", new ConstrainedDictionary(null));
      assertTrue(dict.isCompatibleWith(DictionarySchema.create().build()));
      assertTrue(dict.isCompatibleWith(null));
      assertTrue(
          dict.isCompatibleWith(
              DictionarySchema.create()
                  .addProperty("1", (p) -> p.withType(String.class))
                  .addProperty("2", (p) -> p.withType(Boolean.class))
                  .addProperty("3", (p) -> p.withType(Float.class))
                  .addProperty("4", (p) -> p.withType(String[].class))
                  .addProperty("5", (p) -> p.withType(Dictionary.class))
                  .build()));
      assertTrue(
          dict.isCompatibleWith(
              DictionarySchema.create()
                  .addProperty("2", (p) -> p.withType(boolean.class))
                  .addProperty("4", (p) -> p.withType(String[].class))
                  .build()));
      assertTrue(
          dict.isCompatibleWith(
              DictionarySchema.create()
                  .addProperty("1", (p) -> p.withType(String.class))
                  .addProperty("2", (p) -> p.withType(Boolean.class))
                  .addProperty("3", (p) -> p.withType(Float.class))
                  .addProperty("5", (p) -> p.withType(Dictionary.class))
                  .build()));
      assertFalse(
          dict.isCompatibleWith(
              DictionarySchema.create()
                  .addProperty("1", (p) -> p.withType(String.class))
                  .addProperty("2", (p) -> p.withType(Boolean.class))
                  .addProperty("3", (p) -> p.withType(Float.class))
                  .addProperty("4", (p) -> p.withType(String[].class))
                  .addProperty("5", (p) -> p.withType(SchemalessDictionary.class))
                  .build()));
      assertTrue(
          dict.isCompatibleWith(
              DictionarySchema.create()
                  .addProperty("1", (p) -> p.withType(String.class))
                  .addProperty("2", (p) -> p.withType(Boolean.class))
                  .addProperty("3", (p) -> p.withType(Float.class))
                  .addProperty("4", (p) -> p.withType(String[].class))
                  .addProperty("5", (p) -> p.withType(ConstrainedDictionary.class))
                  .build()));
      assertFalse(
          dict.isCompatibleWith(
              DictionarySchema.create()
                  .addProperty("1", (p) -> p.withType(String.class))
                  .addProperty("2", (p) -> p.withType(Boolean.class))
                  .addProperty("3", (p) -> p.withType(Double.class))
                  .addProperty("4", (p) -> p.withType(String[].class))
                  .addProperty("5", (p) -> p.withType(Dictionary.class))
                  .build()));
    }

    @Test
    public void testImmutable() {
      Dictionary dict = new ConstrainedDictionary(null).immutable();
      assertThrows(UnsupportedOperationException.class, () -> dict.put("1", "a"));
      assertThrows(UnsupportedOperationException.class, dict::clear);
      assertThrows(UnsupportedOperationException.class, () -> dict.replace("1", "a", "b"));
      assertThrows(UnsupportedOperationException.class, () -> dict.remove("1"));
      assertThrows(UnsupportedOperationException.class, () -> dict.putIfAbsent("1", "a"));
      assertThrows(UnsupportedOperationException.class, () -> dict.remove("1", "a"));
      assertThrows(UnsupportedOperationException.class, () -> dict.replace("1", "a"));
      assertThrows(UnsupportedOperationException.class, () -> dict.replaceAll((k, v) -> v));
      assertThrows(UnsupportedOperationException.class, () -> dict.compute("1", (k, v) -> v));
      assertThrows(
          UnsupportedOperationException.class, () -> dict.computeIfPresent("1", (k, v) -> v));
      assertThrows(UnsupportedOperationException.class, () -> dict.computeIfAbsent("1", k -> "a"));
      assertThrows(UnsupportedOperationException.class, () -> dict.merge("1", "a", (v1, v2) -> v1));
    }
  }

  private static DictionarySchema characterSchema;

  @BeforeAll
  public static void beforeAll() {
    characterSchema =
        DictionarySchema.create()
            .addProperty("name", p -> p.withType(String.class))
            .addProperty("level", p -> p.withType(int.class))
            .addProperty("health", p -> p.withType(double.class))
            .addProperty(
                "inventory",
                p ->
                    p.isDictionaryArray(
                        DictionarySchema.create()
                            .addProperty("item", bp -> bp.withType(String.class))
                            .addProperty("amount", bp -> bp.withType(int.class))
                            .build()))
            .build();
  }

  @Nested
  class Schema {
    @Test
    public void testPut() {
      assertDoesNotThrow(
          () -> {
            Dictionary dict = new ConstrainedDictionary(characterSchema);
            dict.put("name", "Bob");
            dict.put("level", 3);
            dict.put("health", 100.0);
            dict.put(
                "inventory",
                new Dictionary[] {
                  Dictionary.of(Map.of("item", "sword", "amount", 10)),
                  Dictionary.of(Map.of("item", "shield", "amount", 5))
                });
          });
      assertDoesNotThrow(
          () -> {
            Dictionary dict = new ConstrainedDictionary(characterSchema);
            dict.put("name", "Bob");
            dict.put("level", 3);
            dict.put(
                "inventory",
                new Dictionary[] {
                  Dictionary.of(Map.of("item", "sword", "amount", 10)),
                  Dictionary.of(Map.of("item", "shield", "amount", 5))
                });
          });
      assertThrows(
          IllegalArgumentException.class,
          () -> {
            Dictionary dict = new ConstrainedDictionary(characterSchema);
            dict.put("name", "Bob");
            dict.put("level", 3.0);
            dict.put("health", 100.0);
            dict.put(
                "inventory",
                new Dictionary[] {
                  Dictionary.of(Map.of("item", "sword", "amount", 10)),
                  Dictionary.of(Map.of("item", "shield", "amount", 5))
                });
          });
      assertThrows(
          IllegalArgumentException.class,
          () -> {
            Dictionary dict = new ConstrainedDictionary(characterSchema);
            dict.put("name", "Bob");
            dict.put("level", 3);
            dict.put("health", 100.0);
            dict.put(
                "inventory",
                new Dictionary[] {
                  Dictionary.of(Map.of("item", "sword", "amount", 10.0)),
                  Dictionary.of(Map.of("item", "shield", "amount", 5.0))
                });
          });
    }
  }
}
