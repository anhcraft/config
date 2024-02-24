package dev.anhcraft.config.adapter;

import dev.anhcraft.config.context.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TypeAnnotator<ComplexT> extends TypeAdapter<ComplexT> {
    @Nullable
    default Object simplify(@NotNull Context ctx, @NotNull Class<ComplexT> sourceType, @NotNull ComplexT value) {
        throw new UnsupportedOperationException();
    }
}
