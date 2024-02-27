package dev.anhcraft.config.adapter;

import dev.anhcraft.config.context.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link TypeAdapter} that is used in denormalization only.
 * @param <ComplexT> the complex type
 */
public interface TypeAnnotator<ComplexT> extends TypeAdapter<ComplexT> {
  @Nullable default Object simplify(
      @NotNull Context ctx,
      @NotNull Class<? extends ComplexT> sourceType,
      @NotNull ComplexT value) {
    throw new UnsupportedOperationException();
  }
}
