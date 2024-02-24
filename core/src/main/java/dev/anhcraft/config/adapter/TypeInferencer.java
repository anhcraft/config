package dev.anhcraft.config.adapter;

import dev.anhcraft.config.context.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public interface TypeInferencer<ComplexT> extends TypeAdapter<ComplexT> {

    @Nullable
    default ComplexT complexify(@NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
        throw new UnsupportedOperationException();
    }
}
