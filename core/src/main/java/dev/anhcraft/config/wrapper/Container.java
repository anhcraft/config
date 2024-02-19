package dev.anhcraft.config.wrapper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Container {
    private Map<String, Object> backend;

    @NotNull
    public static Container from(@NotNull Map<String, Object> map) {
        Container container = new Container();
        container.backend = map;
        return container;
    }

    public Container() {
        this.backend = new HashMap<>();
    }

    @Nullable
    public <T> Object set(@NotNull String key, @Nullable T value) {
        if (!SimpleTypes.validate(value))
            throw new IllegalArgumentException("invalid value");
        // TODO deep clone array since the element can be mutated to be a non-simple type
        return this.backend.put(key, value);
    }

    public boolean has(@NotNull String key) {
        return this.backend.containsKey(key);
    }

    @Nullable
    public Object get(@NotNull String key) {
        return this.backend.get(key);
    }

    @NotNull
    public Object getOrDefault(@NotNull String key, @NotNull Object defaultValue) {
        return this.backend.getOrDefault(key, defaultValue);
    }

    @NotNull
    public Map<String, Object> toMap() {
        return Map.copyOf(this.backend);
    }
}
