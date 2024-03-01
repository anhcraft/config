package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.adapter.TypeAnnotator;
import dev.anhcraft.config.context.Context;
import java.lang.reflect.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringAdapter implements TypeAnnotator<String> {
  public static final StringAdapter INSTANCE = new StringAdapter();

  @Override
  public @Nullable String complexify(
      @NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
    return String.valueOf(value);
  }
}
