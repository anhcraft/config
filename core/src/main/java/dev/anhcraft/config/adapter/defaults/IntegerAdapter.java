package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.adapter.AdapterContext;
import dev.anhcraft.config.adapter.ScalarAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public class IntegerAdapter implements ScalarAdapter<Integer> {
    @Override
    public @Nullable Integer complexify(@NotNull AdapterContext ctx, @NotNull Type targetType, @NotNull Object value) throws Exception {
        if (value instanceof Number)
            return ((Number) value).intValue();
        else if (value instanceof String)
            return Integer.parseInt((String) value);
        else if (value instanceof Boolean)
            return complexify(ctx, targetType, ((Boolean) value) ? 1 : 0);
        else if (value instanceof Character)
            return (int) value;
        return null;
    }
}
