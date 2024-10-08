package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.SettingFlag;
import dev.anhcraft.config.adapter.TypeAnnotator;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.error.InvalidValueException;
import java.lang.reflect.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ByteAdapter implements TypeAnnotator<Byte> {
  public static final ByteAdapter INSTANCE = new ByteAdapter();

  @Override
  public @Nullable Byte complexify(
      @NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
    if (value instanceof Number) return ((Number) value).byteValue();
    else if (value instanceof String) {
      try {
        String str = ((String) value).trim();
        boolean strict =
            ctx.getFactory()
                .getDenormalizer()
                .getSettings()
                .contains(SettingFlag.Denormalizer.STRICT_NUMBER_PARSING);
        return strict ? Byte.parseByte(str) : (byte) Double.parseDouble(str);
      } catch (NumberFormatException e) {
        throw new InvalidValueException(
            ctx, String.format("Cannot convert '%s' to byte", value), e);
      }
    } else if (value instanceof Boolean) return ((Boolean) value) ? (byte) 1 : (byte) 0;
    else if (value instanceof Character) return (byte) ((Character) value).charValue();
    return null;
  }
}
