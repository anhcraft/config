import configs.*;
import dev.anhcraft.config.bukkit.struct.YamlConfigSection;
import dev.anhcraft.config.struct.ConfigSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

public class CommonConfigTest extends TestPlatform {
    @Test
    public void playerInfo() throws Exception {
        YamlConfiguration conf = new YamlConfiguration();
        conf.set("player", "UUID:2c8d5050-eae7-438d-88c4-c29fbcebede9");
        conf.set("points", 100);
        conf.set("homes", Arrays.asList("20 15 -8", "-7 20 13", "47 19 51"));
        PlayerInfo obj = deserialize(PlayerInfo.class, conf);
        Assertions.assertEquals(obj.player, UUID.fromString("2c8d5050-eae7-438d-88c4-c29fbcebede9"));
        Assertions.assertEquals(obj.points, 100);
        Assertions.assertEquals(obj.homes, Arrays.asList("20 15 -8", "-7 20 13", "47 19 51"));
    }

    @Test
    public void userGroup() throws Exception {
        YamlConfiguration conf = new YamlConfiguration();
        conf.set("groups.a.name", "a");
        conf.set("groups.a.permissions", Arrays.asList("test.a", "test.b", "test.c"));
        conf.set("inheritable", true);
        RoleTable obj = deserialize(RoleTable.class, conf);
        Assertions.assertFalse(obj.groups.isEmpty());
        Assertions.assertNotNull(obj.groups.get("a"));
        Assertions.assertEquals(obj.groups.get("a").name, "a");
        Assertions.assertArrayEquals(obj.groups.get("a").permissions, new String[]{"test.a", "test.b", "test.c"});
        Assertions.assertTrue(obj.inheritable);
    }

    @Test
    public void market() throws Exception {
        Market m = new Market();
        m.products = new Market.Item[]{
                new Market.Item("Apple", 10),
                new Market.Item("Banana", 40),
                new Market.Item("Peach", 20),
                new Market.Item("Tomato", 30),
                new Market.Item("Kiwi", 50)
        };
        m.transactions = Arrays.asList(
                new Market.Transaction(m.products[0], 1598959383829L),
                new Market.Transaction(m.products[4], 1598959596515L),
                new Market.Transaction(m.products[2], 1598959804261L),
                new Market.Transaction(m.products[4], 1598960110693L)
        );
        m.counter = new HashMap<>();
        m.counter.put("Apple", 1);
        m.counter.put("Peach", 1);
        m.counter.put("Kiwi", 2);
        ConfigSection configSection = Objects.requireNonNull(serialize(Market.class, m).asSection());
        //debug(((YamlConfigSection) configSection).getBackend());
        m = deserialize(Market.class, ((YamlConfigSection) configSection).getBackend());
        ConfigSection configSection2 = Objects.requireNonNull(serialize(Market.class, m).asSection());
        Assertions.assertEquals(configSection.stringify(), configSection2.stringify());
    }

    @Test
    public void menu() throws Exception {
        Menu m = new Menu();
        m.recipes.put(Recipe.BEEF_STEW, Arrays.asList(
                new Ingredient("Steak", 5),
                new Ingredient("Onion", 1),
                new Ingredient("Carrot", 3),
                new Ingredient("Potato", 3),
                new Ingredient("Garlic", 1)
        ));
        m.recipes.put(Recipe.SALMON_SOUP, Arrays.asList(
                new Ingredient("Salmon", 2),
                new Ingredient("Onion", 1)
        ));
        m.recipes.put(Recipe.TOFU_CURRY, Arrays.asList(
                new Ingredient("Tofu", 10),
                new Ingredient("Ginger", 1),
                new Ingredient("Cayenne", 3),
                new Ingredient("Butter", 1)
        ));
        ConfigSection configSection = Objects.requireNonNull(serialize(Menu.class, m).asSection());
        m = deserialize(Menu.class, ((YamlConfigSection) configSection).getBackend());
        List<Ingredient> a = m.recipes.get(Recipe.TOFU_CURRY);
        Assertions.assertNotNull(a);
        Assertions.assertEquals(a.get(0).name, "Tofu");
        Assertions.assertEquals(a.get(2).amount, 3);
        ConfigSection configSection2 = Objects.requireNonNull(serialize(Menu.class, m).asSection());
        Assertions.assertEquals(configSection.stringify(), configSection2.stringify());
    }
}
