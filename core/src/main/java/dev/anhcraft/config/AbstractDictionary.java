package dev.anhcraft.config;

import dev.anhcraft.config.blueprint.DictionaryProperty;
import dev.anhcraft.config.blueprint.DictionarySchema;
import dev.anhcraft.config.type.SimpleTypes;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A generic implementation of {@link Dictionary}.
 */
public abstract class AbstractDictionary extends AbstractMap<String, Object> implements Dictionary {
  private final LinkedHashMap<String, Object> backend;
  private List<String> sortedKeys;
  private Set<Entry<String, Object>> entryView;

  public AbstractDictionary() {
    backend = new LinkedHashMap<>();
  }

  // ======== Overrides ========
  @NotNull @Override
  public Set<Entry<String, Object>> entrySet() {
    if (entryView == null) entryView = new LinkedEntrySet();
    return entryView;
  }

  @Override
  public @Nullable Object put(@NotNull String key, @Nullable Object value) {
    if (!SimpleTypes.test(value))
      throw new IllegalArgumentException(
          String.format("Object of type %s is not a simple type", value.getClass().getName()));
    // TODO deep clone array since the element can be mutated to be a non-simple type
    Object previous = backend.get(key);
    if ((previous == null && value != null) || (previous != null && value == null))
      sortedKeys = null; // invalidate the cache only when add/remove (not modify)
    if (value == null) backend.remove(key); // do not store null
    else {
      onPut(key, value);
      backend.put(key, value);
    }
    return previous;
  }

  // ======== Extra implementations ========

  protected abstract void onPut(String name, Object value);

  @Override
  @Nullable public Map.Entry<String, Object> search(@NotNull String name, @NotNull Iterable<String> aliases) {
    Object value = get(name);
    if (value != null) {
      return Map.entry(name, value);
    }
    for (String alias : aliases) {
      value = get(alias);
      if (value != null) {
        return Map.entry(alias, value);
      }
    }
    return null;
  }

  @Override
  public @Nullable String getKeyAt(int pos) {
    if (sortedKeys == null) sortedKeys = List.copyOf(keySet());
    return sortedKeys.get(pos);
  }

  @Override
  public @Nullable Object getValueAt(int pos) {
    String key = getKeyAt(pos);
    return key == null ? null : get(key);
  }

  @Override
  public @Nullable Object rename(@NotNull String from, @NotNull String to) {
    return put(to, remove(from));
  }

  @Override
  @NotNull public LinkedHashMap<String, Object> unwrap() {
    return new LinkedHashMap<>(backend);
  }

  @Override
  public boolean isCompatibleWith(@Nullable DictionarySchema schema) {
    if (schema == null) return true;
    for (String key : backend.keySet()) {
      DictionaryProperty property = schema.property(key);
      if (property == null) continue;
      Object value = backend.get(key);
      if (!property.isCompatible(value)) return false;
    }
    return true;
  }

  @Override
  public @NotNull Dictionary immutable() {
    return new ImmutableView(this);
  }

  final class LinkedEntrySet extends AbstractSet<Map.Entry<String, Object>> {
    public int size() {
      return backend.entrySet().size();
    }

    public void clear() {
      backend.entrySet().clear();
      sortedKeys = null;
    }

    public Iterator<Map.Entry<String, Object>> iterator() {
      return new EntryIterator();
    }

    public boolean contains(Object o) {
      return backend.entrySet().contains(o);
    }

    public boolean remove(Object o) {
      if (backend.entrySet().remove(o)) {
        sortedKeys = null;
        return true;
      }
      return false;
    }

    public Spliterator<Map.Entry<String, Object>> spliterator() {
      throw new UnsupportedOperationException();
    }

    public void forEach(Consumer<? super Entry<String, Object>> action) {
      backend.entrySet().forEach(action);
    }
  }

  private class EntryIterator implements Iterator<Entry<String, Object>> {
    Iterator<Map.Entry<String, Object>> it = backend.entrySet().iterator();

    public boolean hasNext() {
      return it.hasNext();
    }

    public Entry<String, Object> next() {
      return it.next();
    }

    public void remove() {
      it.remove();
      sortedKeys = null;
    }
  }

  // ======== Immutable view ========

  static final class ImmutableView implements Dictionary {
    private final Dictionary delegate;
    private Set<String> keySet;
    private Set<Map.Entry<String, Object>> entrySet;
    private Collection<Object> values;

    public ImmutableView(Dictionary delegate) {
      if (delegate == this)
        throw new IllegalStateException("Cannot create an immutable view of itself");
      this.delegate = delegate;
    }

    @Override
    public @Nullable Map.Entry<String, Object> search(
        @NotNull String name, @NotNull Iterable<String> aliases) {
      return delegate.search(name, aliases);
    }

    @Override
    public @Nullable String getKeyAt(int pos) {
      return delegate.getKeyAt(pos);
    }

    @Override
    public @Nullable Object getValueAt(int pos) {
      return delegate.getValueAt(pos);
    }

    @Override
    public @Nullable Object rename(@NotNull String from, @NotNull String to) {
      throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull LinkedHashMap<String, Object> unwrap() {
      return delegate.unwrap();
    }

    @Override
    public boolean isCompatibleWith(@Nullable DictionarySchema schema) {
      return delegate.isCompatibleWith(schema);
    }

    @Override
    public @NotNull Dictionary immutable() {
      return this;
    }

    @Override
    public @NotNull Dictionary duplicate(boolean deepCopy) {
      if (deepCopy) return delegate.duplicate(true).immutable();
      return this;
    }

    @Override
    public int size() {
      return delegate.size();
    }

    @Override
    public boolean isEmpty() {
      return delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
      return delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
      return delegate.containsValue(value);
    }

    @Override
    public Object get(Object key) {
      return delegate.get(key);
    }

    @Override
    public Object getOrDefault(Object key, Object defaultValue) {
      return delegate.getOrDefault(key, defaultValue);
    }

    @Nullable @Override
    public Object put(String key, Object value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Object remove(Object key) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ?> m) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
      throw new UnsupportedOperationException();
    }

    @NotNull @Override
    public Set<String> keySet() {
      if (keySet == null) keySet = Collections.unmodifiableSet(delegate.keySet());
      return keySet;
    }

    @NotNull @Override
    public Collection<Object> values() {
      if (values == null) values = Collections.unmodifiableCollection(delegate.values());
      return values;
    }

    @NotNull @Override
    public Set<Entry<String, Object>> entrySet() {
      if (entrySet == null) entrySet = new UnmodifiableEntrySet(delegate.entrySet());
      return entrySet;
    }

    @Override
    public boolean equals(Object o) {
      return o == this || delegate.equals(o);
    }

    @Override
    public int hashCode() {
      return delegate.hashCode();
    }

    @Override
    public String toString() {
      return delegate.toString();
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super Object> action) {
      delegate.forEach(action);
    }

    @Nullable @Override
    public Object putIfAbsent(String key, Object value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object key, Object value) {
      throw new UnsupportedOperationException();
    }

    @Nullable @Override
    public Object replace(String key, Object value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean replace(String key, Object oldValue, Object newValue) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super Object, ?> function) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Object compute(
        String key, @NotNull BiFunction<? super String, ? super Object, ?> remappingFunction) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Object computeIfPresent(
        String key, @NotNull BiFunction<? super String, ? super Object, ?> remappingFunction) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Object computeIfAbsent(
        String key, @NotNull Function<? super String, ?> mappingFunction) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Object merge(
        String key,
        @NotNull Object value,
        @NotNull BiFunction<? super Object, ? super Object, ?> remappingFunction) {
      throw new UnsupportedOperationException();
    }

    static final class UnmodifiableEntrySet implements Set<Entry<String, Object>> {
      private final Set<Entry<String, Object>> delegate;

      public UnmodifiableEntrySet(Set<Entry<String, Object>> delegate) {
        this.delegate = Objects.requireNonNull(delegate);
      }

      @Override
      public int size() {
        return delegate.size();
      }

      @Override
      public boolean isEmpty() {
        return delegate.isEmpty();
      }

      @Override
      public boolean contains(Object o) {
        return delegate.contains(o);
      }

      @Override
      public @NotNull Iterator<Entry<String, Object>> iterator() {
        return new Iterator<>() {
          private final Iterator<Entry<String, Object>> delegateIterator = delegate.iterator();

          @Override
          public boolean hasNext() {
            return delegateIterator.hasNext();
          }

          @Override
          public Entry<String, Object> next() {
            Entry<String, Object> entry = delegateIterator.next();
            return new AbstractMap.SimpleImmutableEntry<>(entry.getKey(), entry.getValue());
          }

          @Override
          public void forEachRemaining(Consumer<? super Entry<String, Object>> action) {
            delegateIterator.forEachRemaining(action);
          }
        };
      }

      @Override
      public @NotNull Object[] toArray() {
        Object[] array = new Object[delegate.size()];
        int index = 0;
        for (Entry<String, Object> entry : delegate) {
          array[index++] = new AbstractMap.SimpleImmutableEntry<>(entry.getKey(), entry.getValue());
        }
        return array;
      }

      @Override
      @SuppressWarnings("unchecked")
      public <T> T[] toArray(T[] a) {
        Object[] arr = delegate.toArray(a.length == 0 ? a : Arrays.copyOf(a, 0));
        for (int i = 0; i < arr.length; i++) {
          Entry<String, Object> entry = (Entry<String, Object>) arr[i];
          arr[i] = new AbstractMap.SimpleImmutableEntry<>(entry.getKey(), entry.getValue());
        }
        if (arr.length > a.length) return (T[]) arr;
        System.arraycopy(arr, 0, a, 0, arr.length);
        if (a.length > arr.length) a[arr.length] = null;
        return a;
      }

      @Override
      public boolean containsAll(@NotNull Collection<?> c) {
        return delegate.containsAll(c);
      }

      @Override
      public boolean add(Entry<String, Object> e) {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(Object o) {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(Collection<? extends Entry<String, Object>> c) {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
        throw new UnsupportedOperationException();
      }
    }
  }
}
