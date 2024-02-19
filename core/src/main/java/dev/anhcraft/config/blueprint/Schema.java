package dev.anhcraft.config.blueprint;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class Schema {
    private final Class<?> type;
    private final Map<String, Property> properties;

    public Schema(@NotNull Class<?> type, @NotNull Map<String, Property> properties) {
        this.type = type;
        this.properties = Collections.unmodifiableMap(properties);
    }

    @NotNull
    public Class<?> type() {
        return type;
    }

    @NotNull
    public Set<String> properties() {
        return properties.keySet();
    }

    @Nullable
    public Property property(@Nullable String name) {
        return properties.get(name);
    }
}
