package dev.anhcraft.config.context;

import dev.anhcraft.config.blueprint.Property;
import org.jetbrains.annotations.NotNull;

public class PropertyScope implements Scope {
    private final Property property;
    private final String setting;

    public PropertyScope(@NotNull Property property, @NotNull String setting) {
        this.property = property;
        this.setting = setting;
    }

    public @NotNull Property getProperty() {
        return property;
    }

    /**
     * Gets the setting involved in this scope.
     * @return the setting or <b>empty</b> if no setting exists
     */
    public @NotNull String getSetting() {
        return setting;
    }
}
