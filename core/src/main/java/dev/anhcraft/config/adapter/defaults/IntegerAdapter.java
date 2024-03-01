package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.SettingFlag;
import dev.anhcraft.config.adapter.TypeAnnotator;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.error.InvalidValueException;
import java.lang.reflect.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IntegerAdapter implements TypeAnnotator<Integer> {
  public static final IntegerAdapter INSTANCE = new IntegerAdapter();

  @Override
  public @Nullable Integer complexify(
      @NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
    if (value instanceof Number) return ((Number) value).intValue();
    else if (value instanceof String) {
      try {
        String str = ((String) value).trim();
        boolean strict =
            SettingFlag.has(
                ctx.getFactory().getDenormalizer().getSettings(),
                SettingFlag.Denormalizer.STRICT_NUMBER_PARSING);
        return strict ? Integer.parseInt(str) : (int) Double.parseDouble(str);
      } catch (NumberFormatException e) {
        throw new InvalidValueException(
            ctx, String.format("Cannot convert '%s' to integer", value), e);
      }
    } else if (value instanceof Boolean)
      return complexify(ctx, ((Boolean) value) ? 1 : 0, targetType);
    else if (value instanceof Character) return (int) (Character) value;
    return null;
  }
}
