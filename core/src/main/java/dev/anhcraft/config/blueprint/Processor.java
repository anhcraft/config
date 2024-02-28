package dev.anhcraft.config.blueprint;

import dev.anhcraft.config.context.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a processor.
 */
public class Processor {
  private final Invoker invoker;
  private final Enum<?> strategy;

  public Processor(@NotNull Invoker invoker, @NotNull Enum<?> strategy) {
    this.invoker = invoker;
    this.strategy = strategy;
  }

  /**
   * Gets the invoker to execute this processor.
   * @return the invoker
   */
  public @NotNull Invoker invoker() {
    return invoker;
  }

  /**
   * Gets the strategy of this processor.
   * @return the strategy
   */
  public @NotNull Enum<?> strategy() {
    return strategy;
  }

  public interface Invoker {}

  public interface NormalizationInvoker extends Invoker {
    @Nullable Object invoke(@NotNull Context ctx, @NotNull Object instance) throws Exception;
  }

  public interface DenormalizationInvoker extends Invoker {
    @Nullable Object invoke(@NotNull Context ctx, @NotNull Object instance, @Nullable Object simple)
        throws Exception;
  }

  public interface VoidDenormalizationInvoker extends DenormalizationInvoker {}
}
