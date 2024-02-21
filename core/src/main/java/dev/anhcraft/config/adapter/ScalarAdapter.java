package dev.anhcraft.config.adapter;

import dev.anhcraft.config.SimpleTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ScalarAdapter<ComplexT> extends TypeAdapter<ComplexT> {
    @Nullable
    default Object simplify(@NotNull AdapterContext ctx, @NotNull Class<ComplexT> sourceType, @NotNull ComplexT value) {
        if (SimpleTypes.validate(value))
            return value;
        throw new IllegalArgumentException("the given value is not a simple type");
    }
}
