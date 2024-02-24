package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.error.InvalidValueException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.UUID;

public class UuidAdapter implements TypeAdapter<UUID> {
    @Override
    public @Nullable Object simplify(@NotNull Context ctx, @NotNull Class<? extends UUID> sourceType, @NotNull UUID value) throws Exception {
        return value.toString();
    }

    @Override
    public @Nullable UUID complexify(@NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
        if (!(value instanceof String)) {
            return null;
        }
        try {
            return UUID.fromString(value.toString().trim());
        } catch (IllegalArgumentException e) {
            throw new InvalidValueException(ctx, String.format("'%s' is not a valid UUID", value), e);
        }
    }
}
