package configs;

import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Setting;

@Configurable
public class Ingredient {
    @Setting
    public String name;

    @Setting
    public int amount;

    public Ingredient(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }
}
