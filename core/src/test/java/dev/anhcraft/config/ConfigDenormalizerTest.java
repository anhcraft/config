package dev.anhcraft.config;

import static org.junit.jupiter.api.Assertions.*;

import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.adapter.TypeInferencer;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.error.IllegalTypeException;
import dev.anhcraft.config.error.InvalidValueException;
import dev.anhcraft.config.meta.Constant;
import dev.anhcraft.config.meta.Denormalizer;
import dev.anhcraft.config.meta.Optional;
import dev.anhcraft.config.meta.Validate;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.*;

public class ConfigDenormalizerTest {
  @Test
  public void testDenormalizeScalar() throws Exception {
    ConfigDenormalizer denormalizer = ConfigFactory.create().build().getDenormalizer();
    assertEquals("1.0001", denormalizer.denormalize("1.0001", String.class));
    assertEquals(1, denormalizer.denormalize("1.0001", Integer.class));
    assertEquals(1.0001d, denormalizer.denormalize("1.0001", Double.class));
    assertEquals(1.0001f, denormalizer.denormalize("1.0001", Float.class));
    assertEquals(1, denormalizer.denormalize("1.0001", int.class));
    assertEquals(1.0001d, denormalizer.denormalize("1.0001", double.class));
    assertEquals(1.0001f, denormalizer.denormalize("1.0001", float.class));
    assertEquals(1, denormalizer.denormalize("  1.0001  ", int.class));
    assertEquals(1.0001d, denormalizer.denormalize("  1.0001  ", double.class));
    assertEquals(1.0001f, denormalizer.denormalize("  1.0001  ", float.class));
  }

  @Test
  public void testDenormalizeArray() throws Exception {
    ConfigDenormalizer denormalizer = ConfigFactory.create().build().getDenormalizer();
    assertArrayEquals(
        new int[] {1, 2, 3},
        (int[]) denormalizer.denormalize(new String[] {"1", "2", "3"}, int[].class));
    assertArrayEquals(
        new int[] {49, 50, 51},
        (int[]) denormalizer.denormalize(new char[] {'1', '2', '3'}, int[].class));
    assertArrayEquals(
        new int[] {1, 2, 3},
        (int[]) denormalizer.denormalize(new double[] {1.0001, 2.21, 3.00042}, int[].class));
  }

  @Test
  public void testDenormalizeArray2D() throws Exception {
    ConfigDenormalizer denormalizer = ConfigFactory.create().build().getDenormalizer();
    assertArrayEquals(
        new int[][] {
          new int[] {1, 2, 3},
          new int[] {4, 5, 6},
          new int[] {7, 8, 9}
        },
        (int[][])
            denormalizer.denormalize(
                new String[][] {
                  new String[] {"1", "2", "3"},
                  new String[] {"4", "5", "6"},
                  new String[] {"7", "8", "9"}
                },
                int[][].class));
  }

  @Test
  public void testIgnoreTypeInferencer() throws Exception {
    ConfigDenormalizer denormalizer =
        ConfigFactory.create()
            .adaptType(
                DummyYummy.class,
                new TypeInferencer<>() {
                  @Override
                  public @Nullable Object simplify(
                      @NotNull Context ctx,
                      @NotNull Class<? extends DummyYummy> sourceType,
                      @NotNull DummyYummy value)
                      throws Exception {
                    return null;
                  }
                })
            .build()
            .getDenormalizer();
    assertInstanceOf(
        DummyYummy.class, denormalizer.denormalize(new SchemalessDictionary(), DummyYummy.class));
  }

  @Test
  public void testCheckTypeReturnedFromAdapter() {
    // try to trick the factory by putting an incompatible type adapter
    //noinspection unchecked
    ConfigDenormalizer denormalizer =
        ConfigFactory.create()
            .adaptType(
                DummyYummy.class,
                (TypeAdapter)
                    new TypeAdapter<Object>() {

                      @Override
                      public @Nullable Object simplify(
                          @NotNull Context ctx, @NotNull Class<?> sourceType, @NotNull Object value)
                          throws Exception {
                        return null;
                      }

                      @Override
                      public @Nullable Object complexify(
                          @NotNull Context ctx, @NotNull Object value, @NotNull Type targetType)
                          throws Exception {
                        return new ArrayList<>();
                      }
                    })
            .build()
            .getDenormalizer();
    assertThrows(
        IllegalTypeException.class,
        () -> denormalizer.denormalize(new DummyYummy(), DummyYummy.class));
  }

  public static class DummyYummy {}

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  public class TestDenormalizeDictionaryUsingSchema {
    private final Dictionary dict = new SchemalessDictionary();
    private ConfigFactory factory;

    @BeforeAll
    public void beforeAll() {
      dict.put("id", "32e123f7-e471-4403-bb4d-a3128841503f");
      dict.put(
          "items",
          new Dictionary[] {
            Dictionary.of(Map.of("id", "item1")),
            Dictionary.of(Map.of("id", "item2")),
            Dictionary.of(Map.of("id", "item3"))
          });
      dict.put("worth", "100.0001");
      factory = ConfigFactory.create().build();
    }

    @Test
    @Order(1)
    public void testSkipConstant() throws Exception {
      Transaction transaction = new Transaction();
      transaction.id = UUID.fromString("2733991b-6b66-4923-ac2e-76480f648cdc");
      factory.getDenormalizer().denormalizeToInstance(dict, Transaction.class, transaction);
      assertEquals(UUID.fromString("2733991b-6b66-4923-ac2e-76480f648cdc"), transaction.id);
    }

    @Test
    @Order(2)
    public void testDoNotOverrideOptional() throws Exception {
      dict.remove("items");
      Transaction transaction = new Transaction();
      factory.getDenormalizer().denormalizeToInstance(dict, Transaction.class, transaction);
      assertEquals(List.of(), transaction.items);
    }

    @Test
    @Order(3)
    public void testCheckNullPrimitive() throws Exception {
      dict.remove("worth");
      Transaction transaction = new Transaction();
      factory.getDenormalizer().denormalizeToInstance(dict, Transaction.class, transaction);
      assertEquals(0d, transaction.worth);
    }

    @Test
    @Order(4)
    public void testValidate() {
      dict.put(
          "items",
          new Dictionary[] {
            Dictionary.of(Map.of("id", "f")),
            Dictionary.of(Map.of("id", "fo")),
            Dictionary.of(Map.of("id", "foo"))
          });
      assertThrows(
          InvalidValueException.class,
          () ->
              factory
                  .getDenormalizer()
                  .denormalizeToInstance(dict, Transaction.class, new Transaction()));
    }
  }

  public static class Transaction {
    @Constant public UUID id;
    @Optional public List<Item> items = List.of();
    public double worth;

    public static class Item {
      @Validate("size=3|")
      public String id;
    }
  }

  @Nested
  public class TestDenormalizationProcessors {
    @Test
    public void testDefaultSyntax() throws Exception {
      ConfigFactory factory =
          ConfigFactory.create()
              .ignoreDefaultValues(true)
              .ignoreEmptyArray(true)
              .ignoreEmptyDictionary(true)
              .build();
      Dictionary dict = new SchemalessDictionary();
      dict.put(
          "reports",
          new Dictionary[] {
            Dictionary.of(Map.of("service", "auth", "ping", 1, "status", 1)),
            Dictionary.of(Map.of("service", "logging", "ping", 2, "status", 0)),
            Dictionary.of(Map.of("service", "noti", "ping", 1, "status", 1)),
            Dictionary.of(Map.of("service", "mail", "ping", 3, "status", 0)),
            Dictionary.of(Map.of("ping", 2, "status", 0)),
          });
      ServiceCenter serviceCenter = new ServiceCenter();
      factory.getDenormalizer().denormalizeToInstance(dict, ServiceCenter.class, serviceCenter);
      assertEquals(2, serviceCenter.deadServices.size());
    }

    public class ServiceCenter {
      public HealthReport[] reports;
      @Constant public Set<String> deadServices = Set.of();

      @Denormalizer(value = "reports", strategy = Denormalizer.Strategy.AFTER)
      private void filterReport(HealthReport[] reports) {
        reports =
            Arrays.stream(reports)
                .filter(report -> report.service != null)
                .toArray(HealthReport[]::new);
        deadServices =
            Arrays.stream(reports)
                .filter(report -> report.status == 0)
                .map(r -> r.service)
                .collect(Collectors.toSet());
      }
    }

    public class HealthReport {
      public String service;
      public int ping;
      public int status;

      @Denormalizer(value = {"ping", "status"})
      private int checkNegative(int n, Context ctx) {
        if (n < 0) throw new RuntimeException("Health report is malformed at " + ctx.getPath());
        return n;
      }

      @Override
      public String toString() {
        return String.format("HealthReport{service=%s, ping=%d, status=%d}", service, ping, status);
      }
    }
  }
}
