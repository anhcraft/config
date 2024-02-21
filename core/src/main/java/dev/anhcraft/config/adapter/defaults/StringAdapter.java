package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.adapter.AdapterContext;
import dev.anhcraft.config.adapter.ScalarAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public class StringAdapter implements ScalarAdapter<String> {
    @Override
    public @Nullable String complexify(@NotNull AdapterContext ctx, @NotNull Type targetType, @NotNull Object value) throws Exception {
        return String.valueOf(value);
    }
}
