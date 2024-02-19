package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.adapter.AdapterContext;
import dev.anhcraft.config.adapter.TypeAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.UUID;

public class UuidAdapter implements TypeAdapter<UUID> {
    @Override
    public @Nullable Object simplify(@NotNull AdapterContext ctx, @NotNull Type sourceType, @NotNull UUID value) throws Exception {
        return value.toString();
    }

    @Override
    public @Nullable UUID complexify(@NotNull AdapterContext ctx, @NotNull Type targetType, @NotNull Object value) throws Exception {
        return UUID.fromString(value.toString());
    }
}
