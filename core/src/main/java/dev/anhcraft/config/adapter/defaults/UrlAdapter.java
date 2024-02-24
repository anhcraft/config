package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.adapter.TypeAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.net.URL;

public class UrlAdapter implements TypeAdapter<URL> {
    @Override
    public @Nullable Object simplify(@NotNull Context ctx, @NotNull Class<URL> sourceType, @NotNull URL value) throws Exception {
        return value.toString();
    }

    @Override
    public @Nullable URL complexify(@NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
        return new URL(value.toString());
    }
}
