package dev.anhcraft.config.blueprint;

import static org.junit.jupiter.api.Assertions.*;

import dev.anhcraft.config.NamingPolicy;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.context.PathType;
import dev.anhcraft.config.error.UnsupportedSchemaException;
import dev.anhcraft.config.meta.*;
import dev.anhcraft.config.meta.Optional;
import dev.anhcraft.config.type.TypeToken;
import dev.anhcraft.config.validate.ValidationRegistry;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.UnaryOperator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ReflectSchemaScannerTest {
  private static ReflectSchemaScanner scanner;

  @BeforeAll
  public static void setUp() {
    scanner =
        new ReflectSchemaScanner(
            NamingPolicy.DEFAULT, ValidationRegistry.DEFAULT, LinkedHashMap::new);
  }

  @Test
  public void testScanInvalid() {
    assertThrows(UnsupportedSchemaException.class, () -> scanner.scanSchema(int.class));
    assertThrows(UnsupportedSchemaException.class, () -> scanner.scanSchema(int[].class));
    assertThrows(UnsupportedSchemaException.class, () -> scanner.scanSchema(List.class));
    assertThrows(UnsupportedSchemaException.class, () -> scanner.scanSchema(PathType.class));
    assertThrows(UnsupportedSchemaException.class, () -> scanner.scanSchema(Name.class));
    assertThrows(
        UnsupportedSchemaException.class,
        () -> scanner.scanSchema(new TypeToken<>() {}.getClass()));
  }

  @Nested
  public class ConflictNamingPolicyTest {
    @Test
    public void testConflictNamingPolicy() {
      ReflectSchemaScanner custom =
          new ReflectSchemaScanner(
              s -> String.valueOf(s.length()), ValidationRegistry.DEFAULT, LinkedHashMap::new);
      assertThrows(UnsupportedSchemaException.class, () -> custom.scanSchema(FooBar.class));
    }

    public class FooBar {
      public int foo;
      public int bar;
    }
  }

  @Nested
  public class ValidNamingPolicyTest {
    @Test
    public void testDefaultNamingPolicy() {
      ReflectSchemaScanner custom =
          new ReflectSchemaScanner(
              NamingPolicy.DEFAULT, ValidationRegistry.DEFAULT, LinkedHashMap::new);
      ClassSchema schema = custom.scanSchema(FooBar.class);
      assertEquals(Set.of("fooBar", "barFoo"), schema.propertyNames());
      assertNotNull(schema.property("fooBar"));
      assertEquals("fooBar", schema.property("fooBar").field().getName());
      assertNotNull(schema.property("barFoo"));
      assertEquals("barFoo", schema.property("barFoo").field().getName());
    }

    @Test
    public void testKebabCaseNamingPolicy() {
      ReflectSchemaScanner custom =
          new ReflectSchemaScanner(
              NamingPolicy.KEBAB_CASE, ValidationRegistry.DEFAULT, LinkedHashMap::new);
      ClassSchema schema = custom.scanSchema(FooBar.class);
      assertEquals(Set.of("foo-bar", "bar-foo"), schema.propertyNames());
      assertNotNull(schema.property("foo-bar"));
      assertEquals("fooBar", schema.property("foo-bar").field().getName());
      assertNotNull(schema.property("bar-foo"));
      assertEquals("barFoo", schema.property("bar-foo").field().getName());
    }

    @Test
    public void testSnakeCaseNamingPolicy() {
      ReflectSchemaScanner custom =
          new ReflectSchemaScanner(
              NamingPolicy.SNAKE_CASE, ValidationRegistry.DEFAULT, LinkedHashMap::new);
      ClassSchema schema = custom.scanSchema(FooBar.class);
      assertEquals(Set.of("foo_bar", "bar_foo"), schema.propertyNames());
      assertNotNull(schema.property("foo_bar"));
      assertEquals("fooBar", schema.property("foo_bar").field().getName());
      assertNotNull(schema.property("bar_foo"));
      assertEquals("barFoo", schema.property("bar_foo").field().getName());
    }

    @Test
    public void testPascalCaseNamingPolicy() {
      ReflectSchemaScanner custom =
          new ReflectSchemaScanner(
              NamingPolicy.PASCAL_CASE, ValidationRegistry.DEFAULT, LinkedHashMap::new);
      ClassSchema schema = custom.scanSchema(FooBar.class);
      assertEquals(Set.of("FooBar", "BarFoo"), schema.propertyNames());
      assertNotNull(schema.property("FooBar"));
      assertEquals("fooBar", schema.property("FooBar").field().getName());
      assertNotNull(schema.property("BarFoo"));
      assertEquals("barFoo", schema.property("BarFoo").field().getName());
    }

    public class FooBar {
      public int fooBar;
      public int barFoo;
    }
  }

  @Nested
  public class NamingResolutionTest {
    @Test
    public void testDefaultNamingPolicy() {
      ReflectSchemaScanner custom =
          new ReflectSchemaScanner(
              NamingPolicy.DEFAULT, ValidationRegistry.DEFAULT, LinkedHashMap::new);
      ClassSchema schema = custom.scanSchema(Container.class);
      assertEquals(
          Set.of(
              "cold",
              "upperStorage",
              "up",
              "upper",
              "lowerStorage",
              "lo",
              "lower",
              "backup",
              "coldStorage"),
          schema.propertyNames());
      assertEquals("upperStorage", schema.property("upperStorage").field().getName());
      assertEquals("upperStorage", schema.property("up").field().getName());
      assertEquals("upperStorage", schema.property("upper").field().getName());
      assertEquals("lowerStorage", schema.property("lowerStorage").field().getName());
      assertEquals("lowerStorage", schema.property("lo").field().getName());
      assertEquals("lowerStorage", schema.property("lower").field().getName());
      assertEquals("backupStorage", schema.property("backup").field().getName());
      assertEquals("backupStorage", schema.property("cold").field().getName());
      assertEquals("coldStorage", schema.property("coldStorage").field().getName());
    }

    @Test
    public void testDefaultCustomPolicy() {
      ReflectSchemaScanner custom =
          new ReflectSchemaScanner(
              s -> s.length() > 2 ? s.substring(0, 2) : s,
              ValidationRegistry.DEFAULT,
              LinkedHashMap::new);
      ClassSchema schema = custom.scanSchema(Container.class);
      assertEquals(
          Set.of("up", "lo", "lowerStorage", "co", "upper", "lower", "backup", "cold"),
          schema.propertyNames());
      assertEquals("upperStorage", schema.property("up").field().getName());
      assertEquals("upperStorage", schema.property("upper").field().getName());
      assertEquals("lowerStorage", schema.property("lo").field().getName());
      assertEquals("lowerStorage", schema.property("lower").field().getName());
      assertEquals("backupStorage", schema.property("lowerStorage").field().getName());
      assertEquals("backupStorage", schema.property("backup").field().getName());
      assertEquals("backupStorage", schema.property("cold").field().getName());
      assertEquals("coldStorage", schema.property("co").field().getName());
    }

    public class Container {
      @Alias({"", "up", " up ", "upper"})
      public int[] upperStorage;

      @Alias({"lo", "up", "lower"})
      public int[] lowerStorage;

      @Name({"  lowerStorage", "  backup ", "cold"})
      @Alias({" ", "lo", "  backup"})
      public int[] backupStorage;

      @Name("cold")
      public int[] coldStorage;
    }

    @Test
    public void testAliasDuplicateCustomPrimaryName() {
      ReflectSchemaScanner custom =
          new ReflectSchemaScanner(
              NamingPolicy.DEFAULT, ValidationRegistry.DEFAULT, LinkedHashMap::new);
      ClassSchema schema = custom.scanSchema(HelloGreet.class);
      assertEquals(Set.of("greet", "hiya"), schema.propertyNames());
      assertEquals("hello", schema.property("greet").field().getName());
      assertTrue(schema.property("greet").aliases().isEmpty());
      assertEquals("welcome", schema.property("hiya").field().getName());
      assertTrue(schema.property("hiya").aliases().isEmpty());
    }

    public class HelloGreet {
      @Name({"greet", "greet"})
      public String hello;

      @Name("hiya")
      @Alias("hiya")
      public String welcome;
    }
  }

  @Nested
  public class SimpleModelTest {
    private final ClassSchema ps = scanner.scanSchema(Profile.class);
    private final ClassSchema ops = scanner.scanSchema(OnlineProfile.class);

    @Test
    public void testSchemaIdentity() {
      assertNotEquals(
          new ClassSchema(
            new ReflectSchemaScanner(UnaryOperator.identity(), ValidationRegistry.DEFAULT, LinkedHashMap::new),
            ps.type(), ps.properties(), Map.of(), null), ps);
      assertEquals(
          new ClassSchema(scanner, ps.type(), ps.properties(), Map.of(), null), ps);
    }

    @Test
    public void testInit() {
      assertEquals(Profile.class, ps.type());
      assertNull(ps.parent());
      assertEquals("Profile", ps.name());
      assertEquals(4, ps.properties().size());
      assertEquals(Set.of("id", "email", "age", "birth", "bio"), ps.propertyNames());
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    public void testUser() {
      assertEquals("id", ps.property("id").name());
      assertEquals("java.lang.String", ps.property("id").describeType(false));
      assertEquals("user", ps.property("id").field().getName());
      assertTrue(ps.property("id").description().isEmpty());
      assertTrue(ps.property("id").aliases().isEmpty());
      assertTrue(ps.property("id").isConstant());
      assertFalse(ps.property("id").isTransient());
      assertFalse(ps.property("id").isOptional());
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    public void testEmail() {
      assertEquals("email", ps.property("email").field().getName());
      assertEquals("java.lang.String", ps.property("email").describeType(false));
      assertTrue(ps.property("email").description().isEmpty());
      assertTrue(ps.property("email").aliases().isEmpty());
      assertFalse(ps.property("email").isConstant());
      assertFalse(ps.property("email").isTransient());
      assertFalse(ps.property("email").isOptional());
      assertFalse(ps.property("email").validator().check(null));
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    public void testAge() {
      assertEquals("age", ps.property("age").name());
      assertEquals("age", ps.property("age").field().getName());
      assertEquals("int", ps.property("age").describeType(false));
      assertEquals(List.of("Age in years"), ps.property("age").description());
      assertEquals(Set.of("birth"), (ps.property("age").aliases()));
      assertFalse(ps.property("age").isConstant());
      assertFalse(ps.property("age").isTransient());
      assertFalse(ps.property("age").isOptional());

      assertEquals("age", ps.property("birth").name());
      assertEquals("age", ps.property("birth").field().getName());
      assertEquals(List.of("Age in years"), ps.property("birth").description());
      assertEquals(Set.of("birth"), (ps.property("birth").aliases()));
      assertFalse(ps.property("birth").isConstant());
      assertFalse(ps.property("birth").isTransient());
      assertFalse(ps.property("birth").isOptional());
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    public void testBio() {
      assertEquals("bio", ps.property("bio").field().getName());
      assertEquals("java.util.List<java.lang.String>", ps.property("bio").describeType(false));
      assertTrue(ps.property("bio").description().isEmpty());
      assertTrue(ps.property("bio").aliases().isEmpty());
      assertTrue(ps.property("bio").isTransient());
      assertTrue(ps.property("bio").isOptional());
      assertFalse(ps.property("bio").isConstant());
    }

    @Test
    public void testInitOnlineProfile() {
      assertEquals(OnlineProfile.class, ops.type());
      assertEquals(ps, ops.parent());
      assertEquals("OnlineProfile", ops.name());
      assertEquals(2, ops.properties().size());
      assertEquals(Set.of("lastOnline", "status"), ops.propertyNames());
    }

    public class Profile {
      @Alias("birth")
      @Describe("Age in years")
      public final int age = 18;

      @Name("id")
      @Constant public String user;

      @Validate("notNull")
      public String email;

      public transient double balance;

      @Exclude public List<String> education;

      @Optional @Transient public List<String> bio = List.of();
    }

    public class OnlineProfile extends Profile {
      public int lastOnline;
      public String status;
    }
  }

  @SuppressWarnings("DataFlowIssue")
  @Nested
  public class NormalizerProcessorTest {
    @Test
    public void testNormalizerReplaceStrategy() throws Exception {
      ClassSchema schema = scanner.scanSchema(Log.class);
      assertEquals(2, schema.properties().size());
      assertEquals("timestamp", schema.property("timestamp").field().getName());

      Processor processor = schema.property("timestamp").normalizer();
      assertNotNull(processor);
      assertEquals(Normalizer.Strategy.REPLACE, processor.strategy());
      assertEquals(
          "1970-01-01T00:00Z",
          ((Processor.NormalizationInvoker) processor.invoker()).invoke(null, new Log()));
    }

    @Test
    public void testNormalizerBeforeStrategy() throws Exception {
      ClassSchema schema = scanner.scanSchema(Log.class);
      assertEquals(2, schema.properties().size());

      Processor processor1 = schema.property("timestamp").normalizer();
      assertNotNull(processor1);
      assertEquals(Normalizer.Strategy.REPLACE, processor1.strategy());
      assertEquals(
          "1970-01-01T00:00Z",
          ((Processor.NormalizationInvoker) processor1.invoker()).invoke(null, new Log()));

      Processor processor2 = schema.property("details").normalizer();
      assertNotNull(processor2);
      assertEquals(Normalizer.Strategy.BEFORE, processor2.strategy());
      assertThrows(
          InvocationTargetException.class,
          () -> ((Processor.NormalizationInvoker) processor2.invoker()).invoke(null, new Log()));
    }

    public class Log {
      private long timestamp;
      private String details;

      @Normalizer("timestamp")
      private String processTimestamp() {
        return Instant.ofEpochMilli(timestamp).atOffset(ZoneOffset.UTC).toString();
      }

      @Normalizer(value = "details", strategy = Normalizer.Strategy.BEFORE)
      private String appendLogPrefix(Context ctx) {
        return String.format("[%s] %s", ctx.getPath(), details);
      }
    }
  }

  @SuppressWarnings("DataFlowIssue")
  @Nested
  public class DenormalizerProcessorTest {
    @Test
    public void testDenormalizerAfterStrategy() throws Exception {
      ClassSchema schema = scanner.scanSchema(Package.class);
      assertEquals(2, schema.properties().size());
      assertEquals("items", schema.property("items").field().getName());
      assertEquals("worth", schema.property("worth").field().getName());

      Processor processor = schema.property("items").denormalizer();
      assertNotNull(processor);
      assertEquals(Denormalizer.Strategy.AFTER, processor.strategy());
      Processor.VoidDenormalizationInvoker invoker =
          ((Processor.VoidDenormalizationInvoker) processor.invoker());
      Package pkg = new Package();
      invoker.invoke(
          null, pkg, new Item[] {new Item(UUID.randomUUID(), 10), new Item(UUID.randomUUID(), 20)});
      assertEquals(30, pkg.worth);
    }

    @Test
    public void testDenormalizerReplaceStrategy() throws Exception {
      ClassSchema schema = scanner.scanSchema(Item.class);
      assertEquals(2, schema.properties().size());
      assertEquals("id", schema.property("id").field().getName());
      assertEquals("worth", schema.property("worth").field().getName());

      Processor processor = schema.property("id").denormalizer();
      assertNotNull(processor);
      assertEquals(Denormalizer.Strategy.REPLACE, processor.strategy());
      Processor.DenormalizationInvoker invoker =
          ((Processor.DenormalizationInvoker) processor.invoker());
      Item item = new Item(UUID.randomUUID(), 0);
      assertEquals(
          UUID.fromString("fe777bfe-bc20-4871-9f99-f538a8803c78"),
          invoker.invoke(null, item, "fe777bfe-bc20-4871-9f99-f538a8803c78"));
      assertNull(invoker.invoke(null, item, "foo"));
    }

    public class Package {
      private Item[] items;
      private int worth;

      @Denormalizer(value = "items", strategy = Denormalizer.Strategy.AFTER)
      public void calculateWorth(Item[] items) {
        worth = Arrays.stream(items).mapToInt(item -> item.worth).sum();
      }
    }

    public class Item {
      private UUID id;
      private int worth;

      public Item(UUID id, int worth) {
        this.id = id;
        this.worth = worth;
      }

      @Denormalizer(value = "id")
      public UUID processId(Object simple, Context ctx) {
        if (simple
            .toString()
            .matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"))
          return UUID.fromString(simple.toString());
        return null;
      }
    }
  }

  @Nested
  public class FallbackTest {
    @Test
    public void testDuplicateFallbackProperty() {
      class ConfigWithDuplicateFallbackProperties {
        @Fallback public Map<String, Object> foo;
        @Fallback public Map<String, Object> bar;
      }
      assertThrows(
          UnsupportedSchemaException.class,
          () -> scanner.scanSchema(ConfigWithDuplicateFallbackProperties.class));
    }

    @Test
    public void testInvalidTypeFallbackProperty() {
      class ConfigWithInvalidTypeFallbackProperty {
        @Fallback public int foo;
      }
      assertThrows(
          UnsupportedSchemaException.class,
          () -> scanner.scanSchema(ConfigWithInvalidTypeFallbackProperty.class));
    }

    @Test
    public void testFallbackPropertyExists() {
      class Config {
        public int foo;

        @Fallback public Map<String, Object> _trap;
      }

      ClassSchema schema = scanner.scanSchema(Config.class);
      assertEquals(Set.of("foo", "_trap"), schema.propertyNames());
      assertNotNull(schema.fallback());
      assertEquals("_trap", schema.fallback().name());
    }
  }
}
