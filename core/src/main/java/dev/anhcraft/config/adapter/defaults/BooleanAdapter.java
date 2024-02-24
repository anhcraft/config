package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.adapter.TypeAnnotator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public class BooleanAdapter implements TypeAnnotator<Boolean> {
    @Override
    public @Nullable Boolean complexify(@NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
        if (value instanceof Number)
            return ((Number) value).intValue() > 0;
        else if (value instanceof String) {
            String s = (String) value;
            if (s.equalsIgnoreCase("true") || s.equals("1"))
                return true;
            else if (s.equalsIgnoreCase("false") || s.equals("0"))
                return false;
        } else if (value instanceof Character) {
            char c = (char) value;
            if (c == '1') return true;
            else if (c == '0') return false;
        }
        return null;
    }
}
