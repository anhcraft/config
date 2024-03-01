package dev.anhcraft.config.adapter;

import java.util.LinkedHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A simple adapter stores registered type adapters only. To look up an adapter, it traverses the class hierarchy
 * until a compatible adapter exists.
 */
public class SimpleAdapterProvider implements AdapterProvider {
  protected final LinkedHashMap<Class<?>, TypeAdapter<?>> typeAdapters;

  public SimpleAdapterProvider(@NotNull LinkedHashMap<Class<?>, TypeAdapter<?>> typeAdapters) {
    this.typeAdapters = typeAdapters;
  }

  @Override
  @SuppressWarnings("unchecked")
  public @Nullable <T> TypeAdapter<T> getTypeAdapter(@NotNull Class<T> type) {
    Class<?> clazz = type;
    do {
      // Using containsKey instead of get-and-if check
      // e.g. IndexedAdapterProvider set null to mark adapter as unavailable
      if (typeAdapters.containsKey(clazz)) {
        return (TypeAdapter<T>) typeAdapters.get(clazz);
      }
      for (Class<?> inf : clazz.getInterfaces()) {
        if (typeAdapters.containsKey(inf)) {
          return (TypeAdapter<T>) typeAdapters.get(inf);
        }
      }
      clazz = clazz.getSuperclass();
    } while (clazz != null);
    return null;
  }
}
