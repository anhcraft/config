package configs;

import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Setting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configurable
public class Menu {
    @Setting
    public Map<Recipe, List<Ingredient>> recipes = new HashMap<>();
}
