package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.SettingFlag;
import dev.anhcraft.config.adapter.TypeAnnotator;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.error.InvalidValueException;
import java.lang.reflect.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LongAdapter implements TypeAnnotator<Long> {
  public static final LongAdapter INSTANCE = new LongAdapter();

  @Override
  public @Nullable Long complexify(
      @NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
    if (value instanceof Number) return ((Number) value).longValue();
    else if (value instanceof String) {
      try {
        String str = ((String) value).trim();
        boolean strict =
            ctx.getFactory()
                .getDenormalizer()
                .getSettings()
                .contains(SettingFlag.Denormalizer.STRICT_NUMBER_PARSING);
        return strict ? Long.parseLong(str) : (long) Double.parseDouble(str);
      } catch (NumberFormatException e) {
        throw new InvalidValueException(
            ctx, String.format("Cannot convert '%s' to long", value), e);
      }
    } else if (value instanceof Boolean)
      return complexify(ctx, ((Boolean) value) ? 1 : 0, targetType);
    else if (value instanceof Character) return (long) (Character) value;
    return null;
  }
}
