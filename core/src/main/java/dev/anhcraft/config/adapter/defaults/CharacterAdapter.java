package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.adapter.ScalarAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public class CharacterAdapter implements ScalarAdapter<Character> {
    @Override
    public @Nullable Character complexify(@NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
        if (value instanceof Number)
            return (char) ((Number) value).intValue();
        else if (value instanceof String)
            return ((String) value).isEmpty() ? '\0' : ((String) value).charAt(0);
        else if (value instanceof Boolean)
            return complexify(ctx, ((Boolean) value) ? '1' : '0', targetType);
        return null;
    }
}
