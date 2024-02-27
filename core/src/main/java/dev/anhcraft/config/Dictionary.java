package dev.anhcraft.config;

import dev.anhcraft.config.type.SimpleTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

/**
 * Represents a dictionary containing simple objects only.
 */
public class Dictionary extends AbstractMap<String, Object> {
    private final LinkedHashMap<String, Object> backend;
    private List<String> sortedKeys;
    private Set<Map.Entry<String, Object>> entryView;

    /**
     * Makes a copy of the given map as a {@link Dictionary}.<br>
     * The map is shallow-copied. Changes to the dictionary does not reflect in the original map, however, changes
     * made to their values does reflect.<br>
     * The given map must contain simple values only, otherwise, {@link IllegalArgumentException} throws.
     * @param map the map
     * @return the dictionary
     */
    public static @NotNull Dictionary copyOf(@NotNull Map<String, Object> map) {
        Dictionary container = new Dictionary();
        container.putAll(map);
        return container;
    }

    /**
     * Makes a copy of the given map as a {@link Dictionary}.<br>
     * If {@code deep} is true, the map will be deep-copied. Otherwise, it will be shallow-copied.
     * @param dict the dictionary
     * @param deep if true, the dictionary will be deep-copied
     * @return the copied dictionary
     */
    public static @NotNull Dictionary copyOf(@NotNull Dictionary dict, boolean deep) {
        return deep ? Objects.requireNonNull(SimpleTypes.deepClone(dict)) : copyOf(dict.backend);
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
        if (!SimpleTypes.test(value))
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

    /**
     * Searches for an entry with the given name and aliases.
     * @param name the name
     * @param aliases the aliases
     * @return the entry or {@code null}
     */
    @Nullable
    public Map.Entry<String, Object> search(@NotNull String name, @NotNull Iterable<String> aliases) {
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

    /**
     * Gets the key at the given index.
     * @param pos the index
     * @return the key
     */
    public @Nullable String getKeyAt(int pos) {
        if (sortedKeys == null) // generate the cache if not exist
            sortedKeys = List.copyOf(keySet());
        return sortedKeys.get(pos);
    }

    /**
     * Gets the value at the given index.
     * @param pos the index
     * @return the value
     */
    public @Nullable Object getValueAt(int pos) {
        String key = getKeyAt(pos);
        return key == null ? null : get(key);
    }

    /**
     * Renames an entry with a new key.<br>
     * Using the new key may override another existing entry.
     * @param from the current key
     * @param to the new key
     * @return the old value previously at the new key or {@code null}
     */
    public @Nullable Object rename(@NotNull String from, @NotNull String to) {
        return put(to, remove(from));
    }

    /**
     * Unwraps the dictionary into a map.
     * @return shallow-copied, mutable map
     */
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
