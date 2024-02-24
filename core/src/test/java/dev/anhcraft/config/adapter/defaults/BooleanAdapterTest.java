package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.context.Context;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BooleanAdapterTest {
    private static Context context;
    private static BooleanAdapter adapter;

    @BeforeAll
    public static void setUp() {
        context = ConfigFactory.create().build().createContext();
        adapter = new BooleanAdapter();
    }

    @Test
    public void throwWhenSimplify() {
        assertThrowsExactly(UnsupportedOperationException.class, () -> {
            adapter.simplify(context, Boolean.class, true);
        });
    }

    @Test
    public void testComplexifyNumber() throws Exception {
        assertEquals(true, adapter.complexify(context, 1, Boolean.class));
        assertEquals(false, adapter.complexify(context, 0, Boolean.class));
        assertEquals(false, adapter.complexify(context, -1, Boolean.class));
    }

    @Test
    public void testComplexifyString() throws Exception {
        assertEquals(true, adapter.complexify(context, "true", Boolean.class));
        assertEquals(true, adapter.complexify(context, "True", Boolean.class));
        assertEquals(true, adapter.complexify(context, "1", Boolean.class));
        assertEquals(false, adapter.complexify(context, "false", Boolean.class));
        assertEquals(false, adapter.complexify(context, "FaLSE", Boolean.class));
        assertEquals(false, adapter.complexify(context, "0", Boolean.class));
        assertNull(adapter.complexify(context, "-1", Boolean.class));
        assertNull(adapter.complexify(context, "yes", Boolean.class));
        assertNull(adapter.complexify(context, "no", Boolean.class));
        assertNull(adapter.complexify(context, "true ", Boolean.class));
        assertNull(adapter.complexify(context, " false ", Boolean.class));
    }

    @Test
    public void testComplexifyCharacter() throws Exception {
        assertEquals(true, adapter.complexify(context, '1', Boolean.class));
        assertEquals(false, adapter.complexify(context, '0', Boolean.class));
        assertNull(adapter.complexify(context, '2', Boolean.class));
    }

    @Test
    public void testComplexifyBoolean() throws Exception {
        assertEquals(true, adapter.complexify(context, true, Boolean.class));
        assertEquals(false, adapter.complexify(context, false, Boolean.class));
    }
}
