package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.type.ComplexTypes;
import java.lang.reflect.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("rawtypes")
public class EnumAdapter implements TypeAdapter<Enum> {

  @Override
  public @Nullable Object simplify(
      @NotNull Context ctx, @NotNull Class<? extends Enum> sourceType, @NotNull Enum value)
      throws Exception {
    return value.name().toLowerCase();
  }

  @Override
  public @Nullable Enum complexify(
      @NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
    if (value instanceof String) {
      try {
        //noinspection rawtypes,unchecked
        return Enum.valueOf(
            (Class) ComplexTypes.erasure(targetType), ((String) value).trim().toUpperCase());
      } catch (IllegalArgumentException ignored) {
      } // TODO add strict enum parsing
    }
    return null;
  }
}
