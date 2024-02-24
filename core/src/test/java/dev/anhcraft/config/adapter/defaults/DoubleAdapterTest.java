package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.error.InvalidValueException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class DoubleAdapterTest {
    private static Context context;
    private static DoubleAdapter adapter;

    @BeforeAll
    public static void setUp() {
        context = ConfigFactory.create().build().createContext();
        adapter = new DoubleAdapter();
    }

    @Test
    public void throwWhenSimplify() {
        assertThrowsExactly(UnsupportedOperationException.class, () -> {
            adapter.simplify(context, Double.class, 1d);
        });
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    public void testComplexifyNumber() throws Exception {
        assertEquals(0.5d, adapter.complexify(context, 0.5d, Double.class), 1e-8);
        assertEquals(2.0000d, adapter.complexify(context, 2, Double.class), 1e-8);
        assertEquals(-0.0005d, adapter.complexify(context, -0.0005f, Double.class), 1e-8);
        assertEquals(3d, adapter.complexify(context, (byte) 3, Double.class), 1e-8);
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    public void testComplexifyString() throws Exception {
        assertEquals(3.005d, adapter.complexify(context, "3.005", Double.class), 1e-8);
        assertEquals(2d, adapter.complexify(context, "2 ", Double.class), 1e-8);
        assertEquals(12d, adapter.complexify(context, "0012", Double.class), 1e-8);
        assertEquals(-999.001009d, adapter.complexify(context, "-999.001009", Double.class), 1e-8);
        assertEquals(0.000001d, adapter.complexify(context, "0.000001", Double.class), 1e-8);
        assertEquals(9999999999.12345d, adapter.complexify(context, " 9999999999.12345", Double.class), 1e-8);
        assertThrowsExactly(InvalidValueException.class, () -> adapter.complexify(context, "", Double.class));
        assertThrowsExactly(InvalidValueException.class, () -> adapter.complexify(context, "--2.3", Double.class));
        assertThrowsExactly(InvalidValueException.class, () -> adapter.complexify(context, "0.000a", Double.class));
        assertThrowsExactly(InvalidValueException.class, () -> adapter.complexify(context, "1..02", Double.class));
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    public void testComplexifyStringStrictly() throws Exception {
        Context strict = ConfigFactory.create().strictNumberParsing(true).build().createContext();
        assertEquals(3.005d, adapter.complexify(strict, "3.005", Double.class), 1e-8);
        assertEquals(2d, adapter.complexify(strict, "2 ", Double.class), 1e-8);
        assertEquals(12d, adapter.complexify(strict, "0012", Double.class), 1e-8);
        assertEquals(-999.001009d, adapter.complexify(strict, "-999.001009", Double.class), 1e-8);
        assertEquals(0.000001d, adapter.complexify(strict, "0.000001", Double.class), 1e-8);
        assertEquals(9999999999.12345d, adapter.complexify(context, " 9999999999.12345", Double.class), 1e-8);
        assertThrowsExactly(InvalidValueException.class, () -> adapter.complexify(strict, "", Double.class));
        assertThrowsExactly(InvalidValueException.class, () -> adapter.complexify(strict, "--2.3", Double.class));
        assertThrowsExactly(InvalidValueException.class, () -> adapter.complexify(strict, "0.000a", Double.class));
        assertThrowsExactly(InvalidValueException.class, () -> adapter.complexify(strict, "1..02", Double.class));
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    public void testComplexifyCharacter() throws Exception {
        assertEquals('1', adapter.complexify(context, '1', Double.class), 1e-8);
        assertEquals('2', adapter.complexify(context, '2', Double.class), 1e-8);
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    public void testComplexifyBoolean() throws Exception {
        assertEquals(0d, adapter.complexify(context, false, Double.class), 1e-8);
        assertEquals(1d, adapter.complexify(context, true, Double.class), 1e-8);
    }
}
