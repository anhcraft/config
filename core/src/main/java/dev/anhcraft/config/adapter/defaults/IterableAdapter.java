package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.adapter.TypeAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public class IterableAdapter implements TypeAdapter<Iterable<?>> {
    @Override
    public @Nullable Object simplify(@NotNull Context ctx, @NotNull Class<Iterable<?>> sourceType, @NotNull Iterable<?> value) throws Exception {
        return null;
    }

    @Override
    public @Nullable Iterable<?> complexify(@NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
        return null;
    }
}
