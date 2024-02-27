package dev.anhcraft.config.adapter;

import dev.anhcraft.config.context.Context;
import java.lang.reflect.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A type adapter is responsible for transforming between a complex type and arbitrary simple type.<br>
 * Specifically, a type adapter of type {@code ComplexT} is compatible to type {@code T} if {@code T} is a subtype
 * of {@code ComplexT} or if {@code T} is {@code ComplexT}.
 * @param <ComplexT> the complex type
 */
public interface TypeAdapter<ComplexT> {
  /**
   * Simplifies a complex object to a simple object.
   * @param ctx the context
   * @param sourceType the source type
   * @param value the complex object
   * @return the simple object
   * @throws Exception if something goes wrong
   */
  @Nullable Object simplify(
      @NotNull Context ctx, @NotNull Class<? extends ComplexT> sourceType, @NotNull ComplexT value)
      throws Exception;

  /**
   * Complexify a simple object to a complex object.
   * @param ctx the context
   * @param value the simple object
   * @param targetType the target type
   * @return the complex object
   * @throws Exception if something goes wrong
   */
  @Nullable ComplexT complexify(@NotNull Context ctx, @NotNull Object value, @NotNull Type targetType)
      throws Exception;
}
