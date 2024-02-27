package dev.anhcraft.config.adapter;

import dev.anhcraft.config.context.Context;
import java.lang.reflect.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link TypeAdapter} that is used in normalization only.
 * @param <ComplexT> the complex type
 */
public interface TypeInferencer<ComplexT> extends TypeAdapter<ComplexT> {

  @Nullable default ComplexT complexify(@NotNull Context ctx, @NotNull Object value, @NotNull Type targetType)
      throws Exception {
    throw new UnsupportedOperationException();
  }
}
