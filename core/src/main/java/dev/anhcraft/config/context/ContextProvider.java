package dev.anhcraft.config.context;

import dev.anhcraft.config.ConfigFactory;
import org.jetbrains.annotations.NotNull;

/**
 * A function that creates a new {@link Context} compatible to the given {@link ConfigFactory}.
 */
public interface ContextProvider {
  /**
   * Provides a new {@link Context} used for unspecified purposes.
   * @param factory the {@link ConfigFactory}
   * @return the {@link Context}
   */
  default @NotNull Context provideGenericContext(@NotNull ConfigFactory factory) {
    return new Context(factory);
  }

  /**
   * Provides a new {@link Context} used for normalization.
   * @param factory the {@link ConfigFactory}
   * @return the {@link Context}
   */
  default @NotNull Context provideNormalizationContext(@NotNull ConfigFactory factory) {
    return provideGenericContext(factory);
  }

  /**
   * Provides a new {@link Context} used for denormalization.
   * @param factory the {@link ConfigFactory}
   * @return the {@link Context}
   */
  default @NotNull Context provideDenormalizationContext(@NotNull ConfigFactory factory) {
    return provideGenericContext(factory);
  }
}
