package dev.anhcraft.config.struct;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public class Schema {
    private final Class<?> type;
    private final Map<String, Property> properties;

    public Schema(Class<?> type, Map<String, Property> properties) {
        this.type = type;
        this.properties = Map.copyOf(properties);
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
