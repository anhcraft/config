package model;

import dev.anhcraft.config.meta.Describe;
import dev.anhcraft.config.meta.Shape;
import dev.anhcraft.config.meta.Validate;
import java.util.UUID;

@Shape(discriminator = "type", value = "weapon")
public class Weapon<T> extends Item<T> {
  @Describe("The damage")
  @Validate("range=0|")
  private final double damage;

  public Weapon(T value, int stack, UUID owner, double damage) {
    super(value, "weapon", stack, owner);
    this.damage = damage;
  }

  public double getDamage() {
    return damage;
  }

  @Override
  public String toString() {
    return "Weapon{"
        + "damage="
        + damage
        + ", value="
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
