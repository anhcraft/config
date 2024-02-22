package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.adapter.AdapterContext;
import dev.anhcraft.config.adapter.TypeAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.net.URI;

public class UriAdapter implements TypeAdapter<URI> {
    @Override
    public @Nullable Object simplify(@NotNull AdapterContext ctx, @NotNull Class<URI> sourceType, @NotNull URI value) throws Exception {
        return value.toString();
    }

    @Override
    public @Nullable URI complexify(@NotNull AdapterContext ctx, @NotNull Type targetType, @NotNull Object value) throws Exception {
        return new URI(value.toString());
    }
}
