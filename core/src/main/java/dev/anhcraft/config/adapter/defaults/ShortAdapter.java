package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.SettingFlag;
import dev.anhcraft.config.adapter.TypeAnnotator;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.error.InvalidValueException;
import java.lang.reflect.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShortAdapter implements TypeAnnotator<Short> {
  public static final ShortAdapter INSTANCE = new ShortAdapter();

  @Override
  public @Nullable Short complexify(
      @NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
    if (value instanceof Number) return ((Number) value).shortValue();
    else if (value instanceof String) {
      try {
        String str = ((String) value).trim();
        boolean strict =
            ctx.getFactory()
                .getDenormalizer()
                .getSettings()
                .contains(SettingFlag.Denormalizer.STRICT_NUMBER_PARSING);
        return strict ? Short.parseShort(str) : (short) Double.parseDouble(str);
      } catch (NumberFormatException e) {
        throw new InvalidValueException(
            ctx, String.format("Cannot convert '%s' to short", value), e);
      }
    } else if (value instanceof Boolean)
      return complexify(ctx, ((Boolean) value) ? 1 : 0, targetType);
    else if (value instanceof Character) return (short) ((Character) value).charValue();
    return null;
  }
}
