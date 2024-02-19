package dev.anhcraft.config.adapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public interface TypeAdapter<ComplexT> {
    @Nullable
    Object simplify(@NotNull AdapterContext ctx, @NotNull Type sourceType, @NotNull ComplexT value) throws Exception;

    @Nullable
    ComplexT complexify(@NotNull AdapterContext ctx, @NotNull Type targetType, @NotNull Object value) throws Exception;
}
