package dev.anhcraft.config.adapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a type adapter provider.<br>
 * The provider is responsible to provide the type adapter for a given type.
 */
public interface AdapterProvider {
  <T> @Nullable TypeAdapter<T> getTypeAdapter(@NotNull Class<T> type);
}
