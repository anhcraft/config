package dev.anhcraft.config.blueprint;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/**
 * Represents naming information of a {@link Property}.
 */
public final class PropertyNaming {
  private final String primary;
  private final Set<String> aliases;

  public PropertyNaming(@NotNull String primary, @NotNull LinkedHashSet<String> aliases) {
    this(aliases, primary);
    if (primary.isEmpty()) throw new IllegalArgumentException("Primary name cannot be empty");
    for (String alias : aliases) {
      if (alias.isEmpty()) throw new IllegalArgumentException("Alias cannot be empty");
      if (alias.equals(primary))
        throw new IllegalArgumentException("Alias must be different from primary name");
    }
  }

  PropertyNaming(LinkedHashSet<String> aliases, String primary) { // faster construction
    this.primary = primary;
    this.aliases = Collections.unmodifiableSet(aliases);
  }

  /**
   * Gets the primary name.
   * @return the primary name
   */
  @NotNull public String primary() {
    return primary;
  }

  /**
   * Gets all aliases.
   * @return the aliases
   */
  @NotNull public Set<String> aliases() {
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
