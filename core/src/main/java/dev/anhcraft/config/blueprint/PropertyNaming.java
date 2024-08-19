package dev.anhcraft.config.blueprint;

import java.util.*;
import org.jetbrains.annotations.NotNull;

/**
 * Represents naming information of a {@link Property}.
 */
public final class PropertyNaming {
  private final String primary;
  private final Set<String> aliases;

  /**
   * Constructs {@link PropertyNaming} from a collection of names.
   * @param names the collection
   * @return {@link PropertyNaming}
   */
  public static @NotNull PropertyNaming of(@NotNull Collection<String> names) {
    if (names.isEmpty()) throw new IllegalArgumentException("No name provided");
    if (names.size() == 1)
      return new PropertyNaming(Collections.emptySet(), names.iterator().next());

    String primary = null;
    LinkedHashSet<String> aliases = new LinkedHashSet<>();

    for (String name : names) {
      if (primary == null) primary = name;
      else aliases.add(name);
    }

    return new PropertyNaming(aliases, primary);
  }

  public PropertyNaming(@NotNull String primary, @NotNull LinkedHashSet<String> aliases) {
    if (primary.isEmpty()) throw new IllegalArgumentException("Primary name cannot be empty");
    for (String alias : aliases) {
      if (alias.isEmpty()) throw new IllegalArgumentException("Alias cannot be empty");
      if (alias.equals(primary))
        throw new IllegalArgumentException("Alias must be different from primary name");
    }
    this.primary = primary;
    this.aliases = Collections.unmodifiableSet(aliases);
  }

  PropertyNaming(Set<String> aliases, String primary) { // faster construction
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
