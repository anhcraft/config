package dev.anhcraft.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class Dictionary extends AbstractMap<String, Object> {
    private LinkedHashMap<String, Object> backend;
    private List<String> sortedKeys;
    private Set<Map.Entry<String, Object>> entryView;

    public static @NotNull Dictionary wrap(@NotNull Map<String, Object> map) {
        Dictionary container = new Dictionary();
        container.backend = new LinkedHashMap<>(map);
        return container;
    }

    public static @NotNull Dictionary copyOf(@NotNull Dictionary dict) {
        return wrap(dict.backend);
    }

    public static @NotNull Dictionary copyOf(@NotNull Dictionary dict, boolean deep) {
        return deep ? SimpleTypes.deepClone(dict) : wrap(dict.backend);
    }

    public Dictionary() {
        backend = new LinkedHashMap<>();
    }

    // ======== Overrides ========
    @NotNull
    @Override
    public Set<Entry<String, Object>> entrySet() {
        if (entryView == null) entryView = new LinkedEntrySet();
        return entryView;
    }

    @Override
    public @Nullable Object put(@NotNull String key, @Nullable Object value) {
        if (!SimpleTypes.validate(value))
            throw new IllegalArgumentException(String.format("Object of type %s is not a simple type", value.getClass().getName()));
        // TODO deep clone array since the element can be mutated to be a non-simple type
        Object previous = backend.get(key);
        if ((previous == null && value != null) || (previous != null && value == null))
            sortedKeys = null; // invalidate the cache only when add/remove (not modify)
        if (value == null) backend.remove(key); // do not store null
        else backend.put(key, value);
        return previous;
    }

    // ======== Extra implementations ========

    @Nullable
    public Map.Entry<String, Object> tryGet(@NotNull String name, @NotNull Set<String> aliases) {
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

    public @Nullable String getKeyAt(int pos) {
        if (sortedKeys == null) // generate the cache if not exist
            sortedKeys = List.copyOf(keySet());
        return sortedKeys.get(pos);
    }

    public @Nullable Object getValueAt(int pos) {
        String key = getKeyAt(pos);
        return key == null ? null : get(key);
    }

    public @Nullable Object rename(@NotNull String from, @NotNull String to) {
        return put(to, remove(from));
    }

    @NotNull
    public LinkedHashMap<String, Object> unwrap() {
        return new LinkedHashMap<>(backend);
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
            return new Dictionary.EntryIterator();
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

        public void forEach(Consumer<? super Map.Entry<String, Object>> action) {
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
}
