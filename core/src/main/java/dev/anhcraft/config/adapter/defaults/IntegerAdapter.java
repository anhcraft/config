package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.adapter.TypeAnnotator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public class IntegerAdapter implements TypeAnnotator<Integer> {
    @Override
    public @Nullable Integer complexify(@NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
        if (value instanceof Number)
            return ((Number) value).intValue();
        else if (value instanceof String)
            return Integer.parseInt((String) value);
        else if (value instanceof Boolean)
            return complexify(ctx, ((Boolean) value) ? 1 : 0, targetType);
        else if (value instanceof Character)
            return (int) value;
        return null;
    }
}
