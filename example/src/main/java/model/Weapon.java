package model;

import dev.anhcraft.config.meta.Describe;
import dev.anhcraft.config.meta.Validate;
import java.util.UUID;

public class Weapon<T> extends Item<T> {

  public Weapon(T value, int stack, UUID owner, double damage) {
    super(value, stack, owner);
    this.damage = damage;
  }

  @Describe("The damage")
  @Validate("range=0|")
  public double damage;

  @Override
  public String toString() {
    return "Weapon{"
        + "damage="
        + damage
        + ", value="
        + value
        + ", stack="
        + stack
        + ", owner="
        + owner
        + '}';
  }
}
