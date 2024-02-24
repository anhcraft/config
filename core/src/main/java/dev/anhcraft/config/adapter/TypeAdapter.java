package dev.anhcraft.config.adapter;

import dev.anhcraft.config.context.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public interface TypeAdapter<ComplexT> {
    @Nullable
    Object simplify(@NotNull Context ctx, @NotNull Class<ComplexT> sourceType, @NotNull ComplexT value) throws Exception;

    @Nullable
    ComplexT complexify(@NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception;
}
