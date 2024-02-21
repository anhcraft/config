package dev.anhcraft.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Wrapper {
    private LinkedHashMap<String, Object> backend;
    private List<String> keys;

    @NotNull
    public static Wrapper from(@NotNull Map<String, Object> map) {
        Wrapper container = new Wrapper();
        container.backend = new LinkedHashMap<>(map);
        return container;
    }

    public Wrapper() {
        this.backend = new LinkedHashMap<>();
    }

    @Nullable
    public <T> Object set(@NotNull String key, @Nullable T value) {
        if (!SimpleTypes.validate(value))
            throw new IllegalArgumentException("invalid value");
        // TODO deep clone array since the element can be mutated to be a non-simple type
        Object previous = this.backend.put(key, value);
        if ((previous == null && value != null) || (previous != null && value == null)) {
            this.keys = null; // invalidate the cache only when add/remove (not modify)
        }
        return previous;
    }

    public Object rename(String from, String to) {
        return this.backend.put(to, this.backend.remove(from));
    }

    public int size() {
        return this.backend.size();
    }

    public boolean has(@NotNull String key) {
        return this.backend.containsKey(key);
    }

    @Nullable
    public Object locate(int pos) {
        if (this.keys == null) { // generate the cache if not exist
            this.keys = List.copyOf(this.backend.keySet());
        }
        return this.keys.get(pos);
    }

    @Nullable
    public Object get(@NotNull String key) {
        return this.backend.get(key);
    }

    @NotNull
    public Object getOrDefault(@NotNull String key, @NotNull Object defaultValue) {
        return this.backend.getOrDefault(key, defaultValue);
    }

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

    @NotNull
    public Map<String, Object> toMap() {
        return Map.copyOf(this.backend);
    }
}
