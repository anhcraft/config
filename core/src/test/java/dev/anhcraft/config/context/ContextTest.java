package dev.anhcraft.config.context;

import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.blueprint.Schema;
import dev.anhcraft.config.meta.Name;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("DataFlowIssue")
public class ContextTest {
    private static ConfigFactory factory;
    private static Schema schema;
    private static PropertyScope foo;
    private static PropertyScope bar;
    private static PropertyScope baz;
    private static PropertyScope qux;
    private Context context;

    @BeforeAll
    public static void setUp() {
        factory = ConfigFactory.create().build();
        schema = factory.getSchema(Dummy.class);
        foo = new PropertyScope(schema.property("foo"), "foo");
        bar = new PropertyScope(schema.property("BAR"), "bar");
        baz = new PropertyScope(schema.property("baz"), "baz");
        qux = new PropertyScope(schema.property("QUX"), "qux");
    }

    @BeforeEach
    public void init() {
        context = factory.createContext();
    }

    @Test
    public void testEnterExit() {
        context.enterScope(foo);
        assertEquals(1, context.getDepth());
        context.enterScope(bar);
        assertEquals(2, context.getDepth());
        context.exitScope();
        assertEquals(1, context.getDepth());
        context.exitScope();
        assertEquals(0, context.getDepth());
    }

    @Test
    public void testExitOutOfBound() {
        context.enterScope(foo);
        context.exitScope();
        assertEquals(0, context.getDepth());
        context.exitScope();
        assertEquals(0, context.getDepth());
        context.exitScope();
        assertEquals(0, context.getDepth());
    }

    @Test
    public void testGetScope() {
        context.enterScope(foo);
        context.enterScope(bar);
        context.enterScope(baz);
        assertEquals(baz, context.getScope(0));
        assertEquals(bar, context.getScope(1));
        assertEquals(foo, context.getScope(2));
        context.enterScope(qux);
        assertEquals(qux, context.getScope(0));
        assertEquals(baz, context.getScope(1));
        assertEquals(bar, context.getScope(2));
        assertEquals(foo, context.getScope(3));
        assertThrows(IllegalArgumentException.class, () -> context.getScope(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> context.getScope(4));
    }

    @Test
    public void testGetPath() {
        assertEquals("", context.getPath());
        context.enterScope(foo);
        assertEquals("foo", context.getPath());
        context.enterScope(qux);
        assertEquals("foo.qux", context.getPath());
        context.enterScope(bar);
        assertEquals("foo.qux.bar", context.getPath());
        context.enterScope(new ElementScope(0));
        assertEquals("foo.qux.bar[0]", context.getPath());
        context.enterScope(new ElementScope(3));
        assertEquals("foo.qux.bar[0][3]", context.getPath());
        context.exitScope();
        context.exitScope();
        assertEquals("foo.qux.bar", context.getPath());
    }

    @Test
    public void testBuildPath() {
        context.enterScope(baz);
        context.enterScope(new ElementScope(0));
        context.enterScope(new ElementScope(1));
        context.enterScope(new ElementScope(2));
        assertEquals("baz[0][1][2]", context.buildPath(PathType.FIELD, "/"));
        context.enterScope(qux);
        assertEquals("baz[0][1][2].qux", context.buildPath(PathType.FIELD, "."));
        assertEquals("baz[0][1][2].qux", context.buildPath(PathType.SETTING, "."));
        assertEquals("baz[0][1][2].QUX", context.buildPath(PathType.PRIMARY, "."));
    }

    public static class Dummy {
        public String foo;
        @Name("BAR")
        public String bar;
        public String[] baz;
        @Name("QUX")
        public String qux;
    }
}
