package configs;

import dev.anhcraft.config.annotations.Configurable;

@Configurable
public class Ingredient {
    public String name;

    public int amount;

    public Ingredient(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }
}
