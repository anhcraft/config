package dev.anhcraft.config;

import static org.junit.jupiter.api.Assertions.*;

import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.adapter.TypeAnnotator;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.error.IllegalTypeException;
import dev.anhcraft.config.meta.Normalizer;
import dev.anhcraft.config.meta.Normalizer.Strategy;
import dev.anhcraft.config.meta.Transient;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ConfigNormalizerTest {
  @Test
  public void testNormalizeSimpleValue() throws Exception {
    ConfigNormalizer normalizer = ConfigFactory.create().build().getNormalizer();
    assertEquals("abc", normalizer.normalize("abc"));

    int[] foo = new int[0];
    assertSame(foo, normalizer.normalize(foo));

    String[][] bar = new String[0][0];
    assertSame(bar, normalizer.normalize(bar));

    Dictionary buz = new Dictionary();
    assertSame(buz, normalizer.normalize(buz));
  }

  @Test
  public void testNormalizeSimpleValueDeep() throws Exception {
    ConfigNormalizer normalizer = ConfigFactory.create().deepClone(true).build().getNormalizer();
    assertSame("abc", normalizer.normalize("abc"));

    int[] foo = new int[0];
    assertNotSame(foo, normalizer.normalize(foo));

    String[][] bar = new String[0][0];
    assertNotSame(bar, normalizer.normalize(bar));

    Dictionary buz = new Dictionary();
    assertNotSame(buz, normalizer.normalize(buz));
  }

  @Test
  public void testNormalizeArray() throws Exception {
    ConfigNormalizer normalizer = ConfigFactory.create().build().getNormalizer();
    UUID foo = UUID.randomUUID();
    UUID[] bar = new UUID[] {foo, null, foo};
    Object result = normalizer.normalize(bar);
    assertEquals(foo.toString(), Array.get(result, 0));
    assertNull(Array.get(result, 1));
    assertEquals(foo.toString(), Array.get(result, 2));
  }

  @Test
  public void testIgnoreTypeAnnotator() throws Exception {
    ConfigNormalizer normalizer =
        ConfigFactory.create()
            .adaptType(
                DummyYummy.class,
                new TypeAnnotator<>() {
                  @Override
                  public @Nullable DummyYummy complexify(
                      @NotNull Context ctx, @NotNull Object value, @NotNull Type targetType)
                      throws Exception {
                    return null;
                  }
                })
            .build()
            .getNormalizer();
    assertInstanceOf(Dictionary.class, normalizer.normalize(new DummyYummy()));
  }

  @Test
  public void testCheckTypeReturnedFromAdapter() {
    ConfigNormalizer normalizer =
        ConfigFactory.create()
            .adaptType(
                DummyYummy.class,
                new TypeAdapter<>() {
                  @Override
                  public @Nullable Object simplify(
                      @NotNull Context ctx,
                      @NotNull Class<? extends DummyYummy> sourceType,
                      @NotNull DummyYummy value)
                      throws Exception {
                    return new ArrayList<>();
                  }

                  @Override
                  public @Nullable DummyYummy complexify(
                      @NotNull Context ctx, @NotNull Object value, @NotNull Type targetType)
                      throws Exception {
                    return null;
                  }
                })
            .build()
            .getNormalizer();
    assertThrows(IllegalTypeException.class, () -> normalizer.normalize(new DummyYummy()));
  }

  public static class DummyYummy {}

  @Nested
  public class TestNormalizeIntoDictionary {

    @Test
    public void testShallowCopyDictionary() throws Exception {
      ConfigFactory factory = ConfigFactory.create().build();
      String[] pet = new String[] {"dog"};
      Dictionary foo = new Dictionary();
      foo.put("pet", pet);
      Dictionary bar = new Dictionary();
      factory
          .getNormalizer()
          .normalizeToDictionary(factory.createContext(), Dictionary.class, foo, bar);
      pet[0] = "cat";
      assertEquals("cat", ((String[]) foo.get("pet"))[0]);
      assertEquals("cat", ((String[]) bar.get("pet"))[0]);
    }

    @Test
    public void testDeepCloneDictionary() throws Exception {
      ConfigFactory factory = ConfigFactory.create().deepClone(true).build();
      String[] pet = new String[] {"dog"};
      Dictionary foo = new Dictionary();
      foo.put("pet", pet);
      Dictionary bar = new Dictionary();
      factory
          .getNormalizer()
          .normalizeToDictionary(factory.createContext(), Dictionary.class, foo, bar);
      pet[0] = "cat";
      assertEquals("cat", ((String[]) foo.get("pet"))[0]);
      assertEquals("dog", ((String[]) bar.get("pet"))[0]);
    }
  }

  @Nested
  public class TestNormalizeInstanceUsingSchema {

    @Test
    public void testSkipTransient() throws Exception {
      ConfigFactory factory = ConfigFactory.create().build();
      Transaction transaction = new Transaction();
      transaction.id = UUID.randomUUID();
      Dictionary dict = new Dictionary();
      factory.getNormalizer().normalizeToDictionary(transaction, dict);
      assertNull(dict.get("id"));
    }

    @Test
    public void testIgnoreDefaultValue() throws Exception {
      ConfigFactory factory = ConfigFactory.create().ignoreDefaultValues(true).build();
      Transaction transaction = new Transaction();
      Dictionary dict = new Dictionary();
      factory.getNormalizer().normalizeToDictionary(transaction, dict);
      assertTrue(dict.isEmpty());
    }

    @Test
    public void testIgnoreEmptyArray() throws Exception {
      ConfigFactory factory =
          ConfigFactory.create().ignoreDefaultValues(true).ignoreEmptyArray(true).build();
      Transaction transaction = new Transaction();
      transaction.note = new String[0];
      Dictionary dict = new Dictionary();
      factory.getNormalizer().normalizeToDictionary(transaction, dict);
      assertTrue(dict.isEmpty());
    }

    @Test
    public void testIgnoreEmptyDictionary() throws Exception {
      ConfigFactory factory =
          ConfigFactory.create()
              .ignoreDefaultValues(true)
              .ignoreEmptyArray(true)
              .ignoreEmptyDictionary(true)
              .build();
      Transaction transaction = new Transaction();
      transaction.icon = new Transaction.Item();
      Dictionary dict = new Dictionary();
      factory.getNormalizer().normalizeToDictionary(transaction, dict);
      assertTrue(dict.isEmpty());
    }

    @Test
    public void testDoNotIgnoreEmptyDictionaryInArray() throws Exception {
      ConfigFactory factory =
          ConfigFactory.create()
              .ignoreDefaultValues(true)
              .ignoreEmptyArray(true)
              .ignoreEmptyDictionary(true)
              .build();
      Transaction transaction = new Transaction();
      transaction.items = List.of(new Transaction.Item());
      Dictionary dict = new Dictionary();
      factory.getNormalizer().normalizeToDictionary(transaction, dict);
      assertInstanceOf(Dictionary.class, Array.get(dict.get("items"), 0));
    }
  }

  public static class Transaction {
    @Transient public UUID id;
    public List<Item> items;
    public String[] note;
    public Item icon;

    public static class Item {
      public String id;
    }
  }

  @Nested
  public class TestNormalizationProcessors {
    @Test
    public void testDefaultSyntax() throws Exception {
      ConfigFactory factory =
          ConfigFactory.create()
              .ignoreDefaultValues(true)
              .ignoreEmptyArray(true)
              .ignoreEmptyDictionary(true)
              .build();
      ChatLog chatLog = new ChatLog();
      chatLog.sender = UUID.randomUUID();
      chatLog.message = "Hello";
      chatLog.timestamp = System.currentTimeMillis();
      Dictionary dict = new Dictionary();
      factory.getNormalizer().normalizeToDictionary(chatLog, dict);
      assertNotEquals(chatLog.sender.toString(), dict.get("sender"));
      assertEquals("[message] Hello", dict.get("message"));
      assertNotEquals(chatLog.timestamp, dict.get("timestamp"));
    }

    public class ChatLog {
      private UUID sender;
      private String message;
      private long timestamp;

      @Normalizer(value = "sender", strategy = Strategy.BEFORE)
      private UUID provideSender() {
        return UUID.randomUUID();
      }

      @Normalizer(value = "message")
      private String provideSender(Context ctx) {
        return String.format("[%s] %s", ctx.getPath(), message);
      }

      @Normalizer(value = "timestamp")
      private long provideTimestamp() {
        return System.currentTimeMillis();
      }
    }
  }
}
