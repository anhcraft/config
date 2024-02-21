package dev.anhcraft.config.blueprint;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Schema {
    private final Class<?> type;
    private final List<Property> properties;
    private final Map<String, Property> lookup;

    public Schema(@NotNull Class<?> type, @NotNull List<Property> properties, @NotNull Map<String, Property> lookup) {
        this.type = type;
        this.properties = Collections.unmodifiableList(properties);
        this.lookup = Collections.unmodifiableMap(lookup);
    }

    @NotNull
    public Class<?> type() {
        return type;
    }

    /**
     * Gets all property names including primary names and aliases.<br>
     * <b>Note:</b> Using this method to iterate over the properties may result in duplication
     * of {@link Property} because a property may have more than one name.
     * @return all property names
     */
    @NotNull
    public Set<String> propertyNames() {
        return lookup.keySet();
    }

    /**
     * Returns all properties in the schema.
     * @return all properties
     */
    @NotNull
    public Collection<Property> properties() {
        return properties;
    }

    /**
     * Looks up a property by primary name or alias.
     * @param name property name
     * @return property
     */
    @Nullable
    public Property property(@Nullable String name) {
        return lookup.get(name);
    }
}
