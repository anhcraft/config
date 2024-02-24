package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.error.InvalidValueException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class LongAdapterTest {;
    private static Context context;
    private static LongAdapter adapter;

    @BeforeAll
    public static void setUp() {
        context = ConfigFactory.create().build().createContext();
        adapter = new LongAdapter();
    }

    @Test
    public void throwWhenSimplify() {
        assertThrowsExactly(UnsupportedOperationException.class, () -> {
            adapter.simplify(context, Long.class, 1L);
        });
    }

    @SuppressWarnings("ConstantValue")
    @Test
    public void testComplexifyNumber() throws Exception {
        assertEquals(Long.MAX_VALUE, adapter.complexify(context, Long.MAX_VALUE, Long.class));
        assertEquals(Short.MIN_VALUE, adapter.complexify(context, Short.MIN_VALUE, Long.class));
        assertEquals(Long.MIN_VALUE, adapter.complexify(context, Long.MIN_VALUE, Long.class));
        assertEquals((long) Float.MIN_VALUE, adapter.complexify(context, Float.MIN_VALUE, Long.class));
        assertEquals((long) Double.MIN_VALUE, adapter.complexify(context, Double.MIN_VALUE, Long.class));
        assertEquals(Short.MAX_VALUE, adapter.complexify(context, Short.MAX_VALUE, Long.class));
        assertEquals(Long.MAX_VALUE, adapter.complexify(context, Long.MAX_VALUE, Long.class));
        assertEquals((long) Float.MAX_VALUE, adapter.complexify(context, Float.MAX_VALUE, Long.class));
        assertEquals((long) Double.MAX_VALUE, adapter.complexify(context, Double.MAX_VALUE, Long.class));
    }

    @Test
    public void testComplexifyString() throws Exception {
        assertEquals(0L, adapter.complexify(context, "0", Long.class));
        assertEquals(1L, adapter.complexify(context, "1.001", Long.class));
        assertEquals(-2L, adapter.complexify(context, "-2.1", Long.class));
        assertEquals(255L, adapter.complexify(context, " 255 ", Long.class));
        assertThrowsExactly(InvalidValueException.class, () -> adapter.complexify(context, "", Long.class));
        assertThrowsExactly(InvalidValueException.class, () -> adapter.complexify(context, "one", Long.class));
    }

    @Test
    public void testComplexifyStringStrictly() throws Exception {
        Context strict = ConfigFactory.create().strictNumberParsing(true).build().createContext();
        assertEquals(0L, adapter.complexify(strict, "0", Long.class));
        assertEquals(255L, adapter.complexify(strict, " 255 ", Long.class));
        assertThrowsExactly(InvalidValueException.class, () -> adapter.complexify(strict, "", Long.class));
        assertThrowsExactly(InvalidValueException.class, () -> adapter.complexify(strict, "one", Long.class));
        assertThrowsExactly(InvalidValueException.class, () -> adapter.complexify(strict, "1.001", Long.class));
        assertThrowsExactly(InvalidValueException.class, () -> adapter.complexify(strict, "-2.1", Long.class));
    }

    @Test
    public void testComplexifyCharacter() throws Exception {
        assertEquals('1', adapter.complexify(context, '1', Long.class));
        assertEquals('2', adapter.complexify(context, '2', Long.class));
        assertEquals('à', adapter.complexify(context, 'à', Long.class));
    }

    @Test
    public void testComplexifyBoolean() throws Exception {
        assertEquals(0L, adapter.complexify(context, false, Long.class));
        assertEquals(1L, adapter.complexify(context, true, Long.class));
    }
}
