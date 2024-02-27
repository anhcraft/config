package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.error.InvalidValueException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UriAdapter implements TypeAdapter<URI> {
  @Override
  public @Nullable Object simplify(
      @NotNull Context ctx, @NotNull Class<? extends URI> sourceType, @NotNull URI value)
      throws Exception {
    return value.toString();
  }

  @Override
  public @Nullable URI complexify(
      @NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
    if (!(value instanceof String)) {
      return null;
    }
    try {
      return new URI(value.toString().trim());
    } catch (URISyntaxException e) {
      throw new InvalidValueException(ctx, String.format("'%s' is not a valid URI", value), e);
    }
  }
}
