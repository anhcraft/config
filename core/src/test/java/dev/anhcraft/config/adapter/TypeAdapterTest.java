package dev.anhcraft.config.adapter;

import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.context.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TypeAdapterTest {
    @Test
    public void test() throws Exception {
        ConfigFactory cf = ConfigFactory.create().adaptType(String.class, new CustomAdapter()).build();
        assertEquals("foo", cf.getTypeAdapter(String.class).simplify(cf.createContext(), String.class, " foo "));
    }

    public static class CustomAdapter implements TypeAdapter<String> {

        @Override
        public @Nullable Object simplify(@NotNull Context ctx, @NotNull Class<? extends String> sourceType, @NotNull String value) throws Exception {
            return value.trim();
        }

        @Override
        public @Nullable String complexify(@NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
            return String.valueOf(value);
        }
    }
}
