package dev.anhcraft.config.blueprint;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A generic implementation of {@link Schema}.
 */
public abstract class AbstractSchema<T extends Property> implements Schema<T> {
  private final List<T> properties;
  private final Map<String, T> lookup;

  protected AbstractSchema(@NotNull List<T> properties, @NotNull Map<String, T> lookup) {
    this.properties = Collections.unmodifiableList(properties);
    this.lookup = Collections.unmodifiableMap(lookup);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @NotNull public Set<String> propertyNames() {
    return lookup.keySet();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @NotNull public List<T> properties() {
    return properties;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Nullable public T property(@Nullable String name) {
    return lookup.get(name);
  }
}
