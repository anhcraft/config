package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.context.Context;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class CharacterAdapterTest {
    private static Context context;
    private static CharacterAdapter adapter;

    @BeforeAll
    public static void setUp() {
        context = ConfigFactory.create().build().createContext();
        adapter = new CharacterAdapter();
    }

    @Test
    public void throwWhenSimplify() {
        assertThrowsExactly(UnsupportedOperationException.class, () -> {
            adapter.simplify(context, Character.class, '1');
        });
    }

    @Test
    public void testComplexifyNumber() throws Exception {
        assertEquals('1', adapter.complexify(context, 49, Character.class));
        assertEquals('1', adapter.complexify(context, 49.001, Character.class));
        assertEquals('2', adapter.complexify(context, 50, Character.class));
        assertEquals('2', adapter.complexify(context, (short) 50, Character.class));
    }

    @Test
    public void testComplexifyString() throws Exception {
        assertEquals('H', adapter.complexify(context, "Hello World", Character.class));
        assertEquals('\0', adapter.complexify(context, "", Character.class));
    }

    @Test
    public void testComplexifyCharacter() throws Exception {
        assertEquals('1', adapter.complexify(context, '1', Character.class));
        assertEquals('0', adapter.complexify(context, '0', Character.class));
    }

    @Test
    public void testComplexifyBoolean() throws Exception {
        assertEquals('1', adapter.complexify(context, true, Character.class));
        assertEquals('0', adapter.complexify(context, false, Character.class));
    }
}
