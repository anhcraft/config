package dev.anhcraft.config.context;

import org.jetbrains.annotations.Nullable;

/**
 * A value scope belongs to a container which holds the value, e.g. {@link PropertyScope}, {@link ElementScope}
 */
public class ValueScope implements Scope {
  private final Object value;

  public ValueScope(@Nullable Object value) {
    this.value = value;
  }

  /**
   * Gets the value
   * @return the value
   */
  @Nullable
  public Object getValue() {
    return value;
  }
}
