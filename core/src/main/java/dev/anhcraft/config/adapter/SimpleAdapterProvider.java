package dev.anhcraft.config.adapter;

import java.util.LinkedHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A simple adapter stores registered type adapters only. To look up an adapter, it traverses the class hierarchy
 * until a compatible adapter exists.
 */
public class SimpleAdapterProvider implements AdapterProvider {
  private final LinkedHashMap<Class<?>, TypeAdapter<?>> typeAdapters;

  public SimpleAdapterProvider(@NotNull LinkedHashMap<Class<?>, TypeAdapter<?>> typeAdapters) {
    this.typeAdapters = typeAdapters;
  }

  @Override
  @SuppressWarnings("unchecked")
  public @Nullable <T> TypeAdapter<T> getTypeAdapter(@NotNull Class<T> type) {
    Class<?> clazz = type;
    do {
      TypeAdapter<?> adapter = typeAdapters.get(clazz);
      if (adapter != null) {
        return (TypeAdapter<T>) adapter;
      }
      for (Class<?> inf : clazz.getInterfaces()) {
        adapter = typeAdapters.get(inf);
        if (adapter != null) {
          return (TypeAdapter<T>) adapter;
        }
      }
      clazz = clazz.getSuperclass();
    } while (clazz != null);
    return null;
  }
}
