package configs;

import dev.anhcraft.config.annotations.Configurable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configurable
public class Menu {
    public EnumMap<Recipe, List<Ingredient>> recipes = new EnumMap<>(Recipe.class);
}
