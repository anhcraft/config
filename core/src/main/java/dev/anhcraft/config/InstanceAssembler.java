package dev.anhcraft.config;

import dev.anhcraft.config.context.Context;
import org.jetbrains.annotations.NotNull;

/**
 * An instance assembler provides a fresh instance of a particular class.
 */
public interface InstanceAssembler {
  /**
   * Creates a new instance of the given class based on the context.
   * @param context the context
   * @param clazz the class
   * @return the new instance
   * @param <T> the type
   * @throws Exception if the instance could not be created
   */
  <T> @NotNull T newInstance(@NotNull Context context, @NotNull Class<T> clazz) throws Exception;
}
