package dev.anhcraft.config.adapter;

import java.util.LinkedHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A cacheable adapter provider can cache previous type adapter lookup.
 */
public class CacheableAdapterProvider extends IndexedAdapterProvider {
  public CacheableAdapterProvider(@NotNull LinkedHashMap<Class<?>, TypeAdapter<?>> typeAdapters) {
    super(new LinkedHashMap<>(typeAdapters)); // make the map modifiable
  }

  @SuppressWarnings("unchecked")
  @Override
  public @Nullable <T> TypeAdapter<T> getTypeAdapter(@NotNull Class<T> type) {
    TypeAdapter<?> adapter = typeAdapters.get(type);
    if (adapter != null) {
      return (TypeAdapter<T>) adapter;
    }
    synchronized (typeAdapters) {
      adapter = super.getTypeAdapter(type);
      typeAdapters.put(type, adapter);
      return (TypeAdapter<T>) adapter;
    }
  }
}
