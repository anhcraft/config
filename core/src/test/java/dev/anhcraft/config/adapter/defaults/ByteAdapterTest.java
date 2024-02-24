package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.error.InvalidValueException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ByteAdapterTest {;
    private static Context context;
    private static ByteAdapter adapter;

    @BeforeAll
    public static void setUp() {
        context = ConfigFactory.create().build().createContext();
        adapter = new ByteAdapter();
    }

    @Test
    public void throwWhenSimplify() {
        assertThrowsExactly(UnsupportedOperationException.class, () -> {
            adapter.simplify(context, Byte.class, (byte) 1);
        });
    }

    @SuppressWarnings("ConstantValue")
    @Test
    public void testComplexifyNumber() throws Exception {
        assertEquals(Byte.MAX_VALUE, adapter.complexify(context, Byte.MAX_VALUE, Byte.class));
        assertEquals((byte) Short.MIN_VALUE, adapter.complexify(context, Short.MIN_VALUE, Byte.class));
        assertEquals((byte) Integer.MIN_VALUE, adapter.complexify(context, Integer.MIN_VALUE, Byte.class));
        assertEquals((byte) Long.MIN_VALUE, adapter.complexify(context, Long.MIN_VALUE, Byte.class));
        assertEquals((byte) Float.MIN_VALUE, adapter.complexify(context, Float.MIN_VALUE, Byte.class));
        assertEquals((byte) Double.MIN_VALUE, adapter.complexify(context, Double.MIN_VALUE, Byte.class));
        assertEquals((byte) Short.MAX_VALUE, adapter.complexify(context, Short.MAX_VALUE, Byte.class));
        assertEquals((byte) Integer.MAX_VALUE, adapter.complexify(context, Integer.MAX_VALUE, Byte.class));
        assertEquals((byte) Long.MAX_VALUE, adapter.complexify(context, Long.MAX_VALUE, Byte.class));
        assertEquals((byte) Float.MAX_VALUE, adapter.complexify(context, Float.MAX_VALUE, Byte.class));
        assertEquals((byte) Double.MAX_VALUE, adapter.complexify(context, Double.MAX_VALUE, Byte.class));
    }

    @Test
    public void testComplexifyString() throws Exception {
        assertEquals((byte) 0, adapter.complexify(context, "0", Byte.class));
        assertEquals((byte) 1, adapter.complexify(context, "1.001", Byte.class));
        assertEquals((byte) -2, adapter.complexify(context, "-2.1", Byte.class));
        assertEquals((byte) -1, adapter.complexify(context, " 255 ", Byte.class));
        assertThrowsExactly(InvalidValueException.class, () -> adapter.complexify(context, "", Byte.class));
        assertThrowsExactly(InvalidValueException.class, () -> adapter.complexify(context, "one", Byte.class));
    }

    @Test
    public void testComplexifyStringStrictly() throws Exception {
        Context strict = ConfigFactory.create().strictNumberParsing(true).build().createContext();
        assertEquals((byte) 0, adapter.complexify(strict, "0", Byte.class));
        assertEquals((byte) 127, adapter.complexify(strict, "127", Byte.class));
        assertEquals((byte) -128, adapter.complexify(strict, " -128 ", Byte.class));
        assertThrowsExactly(InvalidValueException.class, () -> adapter.complexify(strict, "1.001", Byte.class));
        assertThrowsExactly(InvalidValueException.class, () -> adapter.complexify(strict, "256", Byte.class));
        assertThrowsExactly(InvalidValueException.class, () -> adapter.complexify(strict, "-129", Byte.class));
        assertThrowsExactly(InvalidValueException.class, () -> adapter.complexify(context, "", Byte.class));
        assertThrowsExactly(InvalidValueException.class, () -> adapter.complexify(context, "one", Byte.class));
    }

    @Test
    public void testComplexifyCharacter() throws Exception {
        assertEquals((byte) '1', adapter.complexify(context, '1', Byte.class));
        assertEquals((byte) '2', adapter.complexify(context, '2', Byte.class));
        assertEquals((byte) 'à', adapter.complexify(context, 'à', Byte.class));
    }

    @Test
    public void testComplexifyBoolean() throws Exception {
        assertEquals((byte) 0, adapter.complexify(context, false, Byte.class));
        assertEquals((byte) 1, adapter.complexify(context, true, Byte.class));
    }
}
