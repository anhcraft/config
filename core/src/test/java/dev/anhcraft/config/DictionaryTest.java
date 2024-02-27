package dev.anhcraft.config;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class DictionaryTest {
  @Test
  public void testCopyOfShallow() {
    Dictionary foo = Dictionary.copyOf(new HashMap<>());
    foo.put("a", 1);
    foo.put("b", new String[1]);
    Dictionary bar = Dictionary.copyOf(foo);
    bar.put("a", 0);
    assertNotSame(foo, bar);
    assertNotEquals(foo.get("a"), bar.get("a"));
    assertSame(foo.get("b"), bar.get("b"));

    ((String[]) foo.get("b"))[0] = "foo";
    assertEquals(foo.get("b"), bar.get("b"));
  }

  @Test
  public void testCopyOfDeep() {
    Dictionary foo = Dictionary.copyOf(new HashMap<>());
    foo.put("a", 1);
    Dictionary bar = Dictionary.copyOf(foo);
    bar.put("a", 0);
    assertNotSame(foo, bar);
    assertNotEquals(foo.get("a"), bar.get("a"));
  }

  @Test
  public void testPutCheckType() {
    Dictionary dict = new Dictionary();
    assertDoesNotThrow(() -> dict.put("a", 3));
    assertDoesNotThrow(() -> dict.put("b", "99"));
    assertDoesNotThrow(() -> dict.put("c", new int[0]));
    assertThrows(IllegalArgumentException.class, () -> dict.put("d", new Object()));
  }

  @SuppressWarnings("OverwrittenKey")
  @Test
  public void testPut() {
    Dictionary dict = new Dictionary();
    dict.put("a", 3);
    dict.put("a", 5);
    assertEquals(5, dict.get("a"));
    dict.put("a", null);
    assertNull(dict.get("a"));
  }

  @Test
  public void testSearch() {
    Dictionary dict = new Dictionary();
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
    Dictionary dict = new Dictionary();
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
    Dictionary dict = new Dictionary();
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
    Dictionary dict = new Dictionary();
    dict.put("1", "a");
    dict.put("2", "b");
    assertNull(dict.rename("1", "3"));
    assertEquals("a", dict.get("3"));
    assertEquals("a", dict.rename("2", "3"));
  }
}
