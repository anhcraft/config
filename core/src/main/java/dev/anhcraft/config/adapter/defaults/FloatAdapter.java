package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.adapter.ScalarAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public class FloatAdapter implements ScalarAdapter<Float> {
    @Override
    public @Nullable Float complexify(@NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
        if (value instanceof Number)
            return ((Number) value).floatValue();
        else if (value instanceof String)
            return Float.parseFloat((String) value);
        else if (value instanceof Boolean)
            return complexify(ctx, ((Boolean) value) ? 1 : 0, targetType);
        else if (value instanceof Character)
            return (float) value;
        return null;
    }
}
