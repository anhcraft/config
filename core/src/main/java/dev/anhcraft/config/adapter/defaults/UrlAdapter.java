package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.adapter.AdapterContext;
import dev.anhcraft.config.adapter.TypeAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.net.URL;

public class UrlAdapter implements TypeAdapter<URL> {
    @Override
    public @Nullable Object simplify(@NotNull AdapterContext ctx, @NotNull Class<URL> sourceType, @NotNull URL value) throws Exception {
        return value.toString();
    }

    @Override
    public @Nullable URL complexify(@NotNull AdapterContext ctx, @NotNull Type targetType, @NotNull Object value) throws Exception {
        return new URL(value.toString());
    }
}
