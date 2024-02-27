package dev.anhcraft.config.blueprint;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Objects;

/**
 * Represents naming information of a {@link Property}.
 */
public final class PropertyNaming {
    private final String primary;
    private final LinkedHashSet<String> aliases;

    public PropertyNaming(@NotNull String primary, @NotNull LinkedHashSet<String> aliases) {
        this.primary = primary;
        this.aliases = aliases;
    }

    /**
     * Gets the primary name.
     * @return the primary name
     */
    @NotNull
    public String primary() {
        return primary;
    }

    /**
     * Gets all aliases.
     * @return the aliases
     */
    @NotNull
    public LinkedHashSet<String> aliases() {
        return aliases;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PropertyNaming)) return false;
        PropertyNaming that = (PropertyNaming) o;
        return Objects.equals(primary, that.primary) && Objects.equals(aliases, that.aliases);
    }

    @Override
    public int hashCode() {
        return Objects.hash(primary, aliases);
    }
}
