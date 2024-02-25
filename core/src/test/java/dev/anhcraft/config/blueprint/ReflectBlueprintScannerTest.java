package dev.anhcraft.config.blueprint;

import dev.anhcraft.config.NamingStrategy;
import dev.anhcraft.config.context.PathType;
import dev.anhcraft.config.error.UnsupportedSchemaException;
import dev.anhcraft.config.meta.Alias;
import dev.anhcraft.config.meta.Describe;
import dev.anhcraft.config.meta.Exclude;
import dev.anhcraft.config.meta.Name;
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
        scanner = new ReflectBlueprintScanner(NamingStrategy.DEFAULT, ValidationRegistry.DEFAULT);
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
    public class ModelTest {
        @SuppressWarnings("DataFlowIssue")
        @Test
        public void testScan() {
            Schema schema = scanner.scanSchema(Profile.class);
            assertEquals(Profile.class, schema.type());
            assertEquals(2, schema.properties().size());
            assertEquals(Set.of("id", "age", "birth"), schema.propertyNames());

            assertEquals("id", schema.property("id").name());
            assertEquals("user", schema.property("id").field().getName());
            assertTrue(schema.property("id").description().isEmpty());
            assertTrue(schema.property("id").aliases().isEmpty());
            assertFalse(schema.property("id").isConstant());
            assertFalse(schema.property("id").isTransient());
            assertFalse(schema.property("id").isOptional());

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

        public class Profile {
            @Alias("birth")
            @Describe("Age in years")
            public final int age = 18;
            @Name("id")
            public String user;
            public transient double balance;

            @Exclude
            public List<String> education;
        }
    }
}
