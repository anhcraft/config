package dev.anhcraft.config.type;

import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.blueprint.Schema;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static dev.anhcraft.config.type.ComplexTypes.describe;

public class TypeResolverTest {
    @Test
    public void testGetTypeMapping() {
        Assertions.assertEquals(Map.of("E", String.class), new TypeToken<List<String>>(){}.getTypeMapping());
        Assertions.assertEquals(Map.of("K", String.class, "V", String.class), new TypeToken<Map<String, String>>(){}.getTypeMapping());
        Assertions.assertEquals(Map.of(), new TypeToken<String>(){}.getTypeMapping());
    }

    @Nested
    public class TestResolve {
        private final TypeToken resolved = new TypeToken<Container<String, Integer>>(){};
        private final Schema schema = ConfigFactory.create().build().getSchema(Container.class);

        @Test
        public void testResolveFirstItem() {
            Type unresolved = schema.property("firstItem").type();
            String desc = describe(resolved.resolve(unresolved));
            Assertions.assertEquals("java.lang.String", desc);
        }

        @Test
        public void testResolveItems() {
            Type unresolved = schema.property("items").type();
            String desc = describe(resolved.resolve(unresolved));
            Assertions.assertEquals("java.util.List<java.lang.String>", desc);
        }

        @Test
        public void testResolveTrash() {
            Type unresolved = schema.property("trash").type();
            String desc = describe(resolved.resolve(unresolved));
            Assertions.assertEquals("java.lang.Integer[]", desc);
        }

        @Test
        public void testResolveFooBar() {
            Type unresolved = schema.property("fooBar").type();
            String desc = describe(resolved.resolve(unresolved));
            Assertions.assertEquals("java.util.Map<java.lang.Integer,java.util.List<java.util.Map<java.lang.Integer[],java.util.Map<java.lang.String,java.lang.Integer>[]>>[][]>", desc);
        }

        public class Container<T, S> {
            private T firstItem;
            private List<T> items;
            private S[] trash;
            private Map<S, List<Map<S[], Map<T, S>[]>>[][]> fooBar;
        }
    }
}
