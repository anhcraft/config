package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.adapter.TypeAnnotator;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.error.InvalidValueException;
import java.lang.reflect.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DoubleAdapter implements TypeAnnotator<Double> {
  @Override
  public @Nullable Double complexify(
      @NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
    if (value instanceof Number) return ((Number) value).doubleValue();
    else if (value instanceof String) {
      try {
        String str = ((String) value).trim();
        return Double.parseDouble(str);
      } catch (NumberFormatException e) {
        throw new InvalidValueException(
            ctx, String.format("Cannot convert '%s' to double", value), e);
      }
    } else if (value instanceof Boolean)
      return complexify(ctx, ((Boolean) value) ? 1 : 0, targetType);
    else if (value instanceof Character) return (double) (Character) value;
    return null;
  }
}
