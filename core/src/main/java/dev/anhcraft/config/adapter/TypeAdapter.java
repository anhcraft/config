package dev.anhcraft.config.adapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public interface TypeAdapter<ComplexT> {
    @Nullable
    <SimpleT> SimpleT simplify(@NotNull Type sourceType, @NotNull ComplexT value) throws Exception;

    @Nullable
    <SimpleT> ComplexT complexify(@NotNull Type targetType, @NotNull SimpleT value) throws Exception;
}
