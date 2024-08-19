package model;

import dev.anhcraft.config.meta.Describe;
import dev.anhcraft.config.meta.Validate;
import java.util.UUID;

public class Item<T> {
  public Item(T value, int stack, UUID owner) {
    this.value = value;
    this.stack = stack;
    this.owner = owner;
  }

  @Describe("The item value")
  public T value;

  @Describe("The number of items")
  @Validate("range=0|")
  public int stack;

  @Describe("The owner of the item")
  @Validate("not-null")
  public UUID owner;

  @Override
  public String toString() {
    return "Item{" + "value=" + value + ", stack=" + stack + ", owner=" + owner + '}';
  }
}
