package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.adapter.TypeAnnotator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public class StringAdapter implements TypeAnnotator<String> {
    @Override
    public @Nullable String complexify(@NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
        return String.valueOf(value);
    }
}