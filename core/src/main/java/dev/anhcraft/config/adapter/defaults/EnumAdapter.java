package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.ComplexTypes;
import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.context.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public class EnumAdapter implements TypeAdapter<Enum<?>> {

    @Override
    public @Nullable Object simplify(@NotNull Context ctx, @NotNull Class<Enum<?>> sourceType, @NotNull Enum<?> value) throws Exception {
        return value.name().toLowerCase();
    }

    @Override
    public @Nullable Enum<?> complexify(@NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
        if (value instanceof String) {
            //noinspection rawtypes,unchecked
            return Enum.valueOf((Class) ComplexTypes.erasure(targetType), ((String) value).toUpperCase());
        }
        return null;
    }
}
