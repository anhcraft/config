package dev.anhcraft.config.adapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a type adapter provider.<br>
 * The provider is responsible to provide the type adapter for a given type.
 */
public interface AdapterProvider {
  /**
   * Gets the type adapter for the given type.
   * @param type the type
   * @return the type adapter
   * @param <T> the type
   */
  <T> @Nullable TypeAdapter<T> getTypeAdapter(@NotNull Class<T> type);
}
