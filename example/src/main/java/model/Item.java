package model;

import dev.anhcraft.config.meta.Describe;
import dev.anhcraft.config.meta.Discriminator;
import dev.anhcraft.config.meta.Validate;
import java.util.UUID;

public class Item<T> {
  @Describe("The item value")
  protected final T value;

  @Describe("The item type")
  @Discriminator
  protected final String type;

  @Describe("The number of items")
  @Validate("range=0|")
  protected final int stack;

  @Describe("The owner of the item")
  @Validate("not-null")
  protected final UUID owner;

  public Item(T value, int stack, UUID owner) {
    this(value, "generic", stack, owner);
  }

  public Item(T value, String type, int stack, UUID owner) {
    this.value = value;
    this.type = type;
    this.stack = stack;
    this.owner = owner;
  }

  public T getValue() {
    return value;
  }

  public String getType() {
    return type;
  }

  public int getStack() {
    return stack;
  }

  public UUID getOwner() {
    return owner;
  }

  @Override
  public String toString() {
    return "Item{"
        + "value="
        + value
        + ", type='"
        + type
        + '\''
        + ", stack="
        + stack
        + ", owner="
        + owner
        + '}';
  }
}
