package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.adapter.TypeAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.net.URI;

public class UriAdapter implements TypeAdapter<URI> {
    @Override
    public @Nullable Object simplify(@NotNull Context ctx, @NotNull Class<URI> sourceType, @NotNull URI value) throws Exception {
        return value.toString();
    }

    @Override
    public @Nullable URI complexify(@NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
        return new URI(value.toString());
    }
}
