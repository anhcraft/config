package dev.anhcraft.config.blueprint;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;

public final class PropertyNaming {
    private final String primary;
    private final Set<String> aliases;

    public PropertyNaming(@NotNull String primary, @NotNull Set<String> aliases) {
        this.primary = primary;
        this.aliases = aliases;
    }

    @NotNull
    public String primary() {
        return primary;
    }

    @NotNull
    public Set<String> aliases() {
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
