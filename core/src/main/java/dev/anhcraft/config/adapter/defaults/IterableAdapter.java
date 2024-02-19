package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.adapter.AdapterContext;
import dev.anhcraft.config.adapter.TypeAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public class IterableAdapter implements TypeAdapter<Iterable> {
    @Override
    public @Nullable Object simplify(@NotNull AdapterContext ctx, @NotNull Type sourceType, @NotNull Iterable value) throws Exception {
        return null;
    }

    @Override
    public @Nullable Iterable complexify(@NotNull AdapterContext ctx, @NotNull Type targetType, @NotNull Object value) throws Exception {
        return null;
    }
}
