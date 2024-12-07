package dev.anhcraft.config;

import org.jetbrains.annotations.NotNull;

/**
 * An instance factory provides facilities to assemble instances.
 */
public interface InstanceFactory extends InstanceAssembler {
  /**
   * Gets the instance assembler for the given class.<br>
   * The assembler could be registered when building the {@link ConfigFactory}. If it is not found, a new instance
   * assembler is created on demand and cached for later uses. The default assembler attempts to utilize the
   * default constructor (no args), otherwise, it allocates a new instance on the heap without invoking any constructor.
   * @param clazz the class
   * @return the instance assembler
   * @param <T> the type
   */
  <T> @NotNull InstanceAssembler getInstanceAssembler(@NotNull Class<T> clazz);
}
