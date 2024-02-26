package dev.anhcraft.config.blueprint;

import dev.anhcraft.config.NamingPolicy;
import dev.anhcraft.config.context.PathType;
import dev.anhcraft.config.error.UnsupportedSchemaException;
import dev.anhcraft.config.meta.*;
import dev.anhcraft.config.type.TypeToken;
import dev.anhcraft.config.validate.ValidationRegistry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ReflectBlueprintScannerTest {
    private static ReflectBlueprintScanner scanner;

    @BeforeAll
    public static void setUp() {
        scanner = new ReflectBlueprintScanner(NamingPolicy.DEFAULT, ValidationRegistry.DEFAULT);
    }

    @Test
    public void testScanInvalid() {
        assertThrows(UnsupportedSchemaException.class, () -> scanner.scanSchema(int.class));
        assertThrows(UnsupportedSchemaException.class, () -> scanner.scanSchema(int[].class));
        assertThrows(UnsupportedSchemaException.class, () -> scanner.scanSchema(List.class));
        assertThrows(UnsupportedSchemaException.class, () -> scanner.scanSchema(PathType.class));
        assertThrows(UnsupportedSchemaException.class, () -> scanner.scanSchema(Name.class));
        assertThrows(UnsupportedSchemaException.class, () -> scanner.scanSchema(new TypeToken<>() {
        }.getClass()));
    }

    @Nested
    public class ConflictNamingPolicyTest {
        @Test
        public void testConflictNamingPolicy() {
            ReflectBlueprintScanner custom = new ReflectBlueprintScanner(s -> String.valueOf(s.length()), ValidationRegistry.DEFAULT);
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
            ReflectBlueprintScanner custom = new ReflectBlueprintScanner(NamingPolicy.DEFAULT, ValidationRegistry.DEFAULT);
            Schema schema = custom.scanSchema(FooBar.class);
            assertEquals(Set.of("fooBar", "barFoo"), schema.propertyNames());
            assertNotNull(schema.property("fooBar"));
            assertEquals("fooBar", schema.property("fooBar").field().getName());
            assertNotNull(schema.property("barFoo"));
            assertEquals("barFoo", schema.property("barFoo").field().getName());
        }

        @Test
        public void testKebabCaseNamingPolicy() {
            ReflectBlueprintScanner custom = new ReflectBlueprintScanner(NamingPolicy.KEBAB_CASE, ValidationRegistry.DEFAULT);
            Schema schema = custom.scanSchema(FooBar.class);
            assertEquals(Set.of("foo-bar", "bar-foo"), schema.propertyNames());
            assertNotNull(schema.property("foo-bar"));
            assertEquals("fooBar", schema.property("foo-bar").field().getName());
            assertNotNull(schema.property("bar-foo"));
            assertEquals("barFoo", schema.property("bar-foo").field().getName());
        }

        @Test
        public void testSnakeCaseNamingPolicy() {
            ReflectBlueprintScanner custom = new ReflectBlueprintScanner(NamingPolicy.SNAKE_CASE, ValidationRegistry.DEFAULT);
            Schema schema = custom.scanSchema(FooBar.class);
            assertEquals(Set.of("foo_bar", "bar_foo"), schema.propertyNames());
            assertNotNull(schema.property("foo_bar"));
            assertEquals("fooBar", schema.property("foo_bar").field().getName());
            assertNotNull(schema.property("bar_foo"));
            assertEquals("barFoo", schema.property("bar_foo").field().getName());
        }

        @Test
        public void testPascalCaseNamingPolicy() {
            ReflectBlueprintScanner custom = new ReflectBlueprintScanner(NamingPolicy.PASCAL_CASE, ValidationRegistry.DEFAULT);
            Schema schema = custom.scanSchema(FooBar.class);
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
            ReflectBlueprintScanner custom = new ReflectBlueprintScanner(NamingPolicy.DEFAULT, ValidationRegistry.DEFAULT);
            Schema schema = custom.scanSchema(Container.class);
            assertEquals(Set.of("cold", "upperStorage", "up", "upper", "lowerStorage", "lo", "lower", "backup", "coldStorage"), schema.propertyNames());
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
            ReflectBlueprintScanner custom = new ReflectBlueprintScanner(s -> s.length() > 2 ? s.substring(0, 2) : s, ValidationRegistry.DEFAULT);
            Schema schema = custom.scanSchema(Container.class);
            assertEquals(Set.of("up","lo","lowerStorage","co","upper","lower","backup","cold"), schema.propertyNames());
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
    }

    @Nested
    public class SimpleModelTest {
        private final Schema schema = scanner.scanSchema(Profile.class);

        @Test
        public void testInit() {
            assertEquals(Profile.class, schema.type());
            assertEquals(4, schema.properties().size());
            assertEquals(Set.of("id", "email", "age", "birth", "bio"), schema.propertyNames());
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        public void testUser() {
            assertEquals("id", schema.property("id").name());
            assertEquals("user", schema.property("id").field().getName());
            assertTrue(schema.property("id").description().isEmpty());
            assertTrue(schema.property("id").aliases().isEmpty());
            assertTrue(schema.property("id").isConstant());
            assertFalse(schema.property("id").isTransient());
            assertFalse(schema.property("id").isOptional());
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        public void testEmail() {
            assertEquals("email", schema.property("email").field().getName());
            assertTrue(schema.property("email").description().isEmpty());
            assertTrue(schema.property("email").aliases().isEmpty());
            assertFalse(schema.property("email").isConstant());
            assertFalse(schema.property("email").isTransient());
            assertFalse(schema.property("email").isOptional());
            assertFalse(schema.property("email").validator().check(null));
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        public void testAge() {
            assertEquals("age", schema.property("age").name());
            assertEquals("age", schema.property("age").field().getName());
            assertEquals(List.of("Age in years"), schema.property("age").description());
            assertEquals(Set.of("birth"), (schema.property("age").aliases()));
            assertFalse(schema.property("age").isConstant());
            assertFalse(schema.property("age").isTransient());
            assertFalse(schema.property("age").isOptional());

            assertEquals("age", schema.property("birth").name());
            assertEquals("age", schema.property("birth").field().getName());
            assertEquals(List.of("Age in years"), schema.property("birth").description());
            assertEquals(Set.of("birth"), (schema.property("birth").aliases()));
            assertFalse(schema.property("birth").isConstant());
            assertFalse(schema.property("birth").isTransient());
            assertFalse(schema.property("birth").isOptional());
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        public void testBio() {
            assertEquals("bio", schema.property("bio").field().getName());
            assertTrue(schema.property("bio").description().isEmpty());
            assertTrue(schema.property("bio").aliases().isEmpty());
            assertTrue(schema.property("bio").isTransient());
            assertTrue(schema.property("bio").isOptional());
            assertFalse(schema.property("bio").isConstant());
        }

        public class Profile {
            @Alias("birth")
            @Describe("Age in years")
            public final int age = 18;

            @Name("id")
            @Constant
            public String user;

            @Validate("notNull")
            public String email;

            public transient double balance;

            @Exclude
            public List<String> education;

            @Optional
            @Transient
            public List<String> bio = List.of();
        }
    }
}
