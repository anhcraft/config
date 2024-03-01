package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.error.InvalidValueException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UrlAdapter implements TypeAdapter<URL> {
  public static final UrlAdapter INSTANCE = new UrlAdapter();

  @Override
  public @Nullable Object simplify(
      @NotNull Context ctx, @NotNull Class<? extends URL> sourceType, @NotNull URL value)
      throws Exception {
    return value.toString();
  }

  @Override
  public @Nullable URL complexify(
      @NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
    if (!(value instanceof String)) {
      return null;
    }
    try {
      return new URL(value.toString().trim());
    } catch (MalformedURLException e) {
      throw new InvalidValueException(ctx, String.format("'%s' is not a valid URL", value), e);
    }
  }
}
