package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.SettingFlag;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.adapter.TypeAnnotator;
import dev.anhcraft.config.error.InvalidValueException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public class FloatAdapter implements TypeAnnotator<Float> {
    @Override
    public @Nullable Float complexify(@NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
        if (value instanceof Number)
            return ((Number) value).floatValue();
        else if (value instanceof String) {
            try {
                String str = ((String) value).trim();
                boolean strict = SettingFlag.has(ctx.getFactory().getDenormalizer().getSettings(), SettingFlag.Denormalizer.STRICT_NUMBER_PARSING);
                return strict ? Float.parseFloat(str) : (float) Double.parseDouble(str);
            } catch (NumberFormatException e) {
                throw new InvalidValueException(ctx, String.format("Cannot convert '%s' to float", value), e);
            }
        }
        else if (value instanceof Boolean)
            return complexify(ctx, ((Boolean) value) ? 1 : 0, targetType);
        else if (value instanceof Character)
            return (float) (Character) value;
        return null;
    }
}
