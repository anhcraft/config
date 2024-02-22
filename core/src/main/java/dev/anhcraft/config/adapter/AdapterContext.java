package dev.anhcraft.config.adapter;

import dev.anhcraft.config.ConfigFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public final class AdapterContext implements TypeAdapter<Object> {
    private final ConfigFactory factory;
    private int depth;

    public AdapterContext(@NotNull ConfigFactory factory, int depth) {
        this.factory = factory;
        this.depth = depth;
    }

    @NotNull
    public ConfigFactory getFactory() {
        return factory;
    }

    public int getDepth() {
        return depth;
    }

    public int commitDepth() {
        return ++depth;
    }

    public int releaseDepth() {
        return --depth;
    }

    @Override
    public @Nullable Object simplify(@NotNull AdapterContext ctx, @NotNull Class<Object> sourceType, @NotNull Object value) throws Exception {
        return factory.getNormalizer().normalize(ctx, sourceType, value);
    }

    @Override
    public @Nullable Object complexify(@NotNull AdapterContext ctx, @NotNull Type targetType, @NotNull Object value) throws Exception {
        return factory.getDenormalizer().denormalize(ctx, targetType, value);
    }
}
