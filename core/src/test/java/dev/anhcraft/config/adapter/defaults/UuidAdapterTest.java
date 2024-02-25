package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.error.InvalidValueException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UuidAdapterTest {
    private static Context context;
    private static UuidAdapter adapter;

    @BeforeAll
    public static void setUp() {
        context = ConfigFactory.create().build().createContext();
        adapter = new UuidAdapter();
    }

    @ParameterizedTest
    @ValueSource(strings = {"1a493939-49fe-457f-958f-69052a656d40   ", "00000000-0000-0000-0000-000000000000"})
    public void testSimplify(String uuid) throws Exception {
        assertEquals(uuid.trim(), adapter.simplify(context, UUID.class, UUID.fromString(uuid.trim())));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1a493939-49fe-457f-958f-69052a656d40  ", "00000000-0000-0000-0000-000000000000"})
    public void testComplexifyIsUri(String uuid) throws Exception {
        assertEquals(UUID.fromString(uuid.trim()), adapter.complexify(context, uuid, UUID.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1a49393949fe457f958f69052a656d40", "00000000-0000-0000-00000000000"})
    public void testComplexifyNotUrl(String uuid) {
        assertThrows(InvalidValueException.class, () -> adapter.complexify(context, uuid, UUID.class));
    }
}
