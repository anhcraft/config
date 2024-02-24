package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.context.Context;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class StringAdapterTest {;
    private static Context context;
    private static StringAdapter adapter;

    @BeforeAll
    public static void setUp() {
        context = ConfigFactory.create().build().createContext();
        adapter = new StringAdapter();
    }

    @Test
    public void throwWhenSimplify() {
        assertThrowsExactly(UnsupportedOperationException.class, () -> {
            adapter.simplify(context, String.class, "foo");
        });
    }

    @Test
    public void testComplexify() throws Exception {
        assertEquals("2.001", adapter.complexify(context, 2.001f, String.class));
        assertEquals("-Infinity", adapter.complexify(context, Float.NEGATIVE_INFINITY, String.class));
        assertEquals("NaN", adapter.complexify(context, Double.NaN, String.class));
        assertEquals("true", adapter.complexify(context, true, String.class));
        assertEquals("a", adapter.complexify(context, 'a', String.class));
    }
}
