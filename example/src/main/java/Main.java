import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.Dictionary;
import dev.anhcraft.config.NamingStrategy;
import model.*;

import java.util.List;
import java.util.UUID;

public class Main {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) throws Exception {
        Warehouse<Item<String>> item = new Warehouse<>();
        item.name = "test";
        item.storages = new Storage[]{
                new Storage<>(
                        StorageType.FRUIT,
                        List.of(
                                new Item<>("orange", 12, UUID.randomUUID()),
                                new Item<>("apple", 16, null)
                        ),
                        new Location(1, 2)
                ),
                new Storage<>(
                        StorageType.FOOD,
                        List.of(
                                new Item<>("bread", 10, UUID.randomUUID()),
                                new Item<>("cake", 6, UUID.randomUUID())
                        ),
                        new Location(0, 1)
                ),
                new Storage<>(
                        StorageType.VEGETABLE,
                        List.of(
                                new Item<>("carrot", 7, UUID.randomUUID()),
                                new Item<>("potato", 10, UUID.randomUUID()),
                                new Item<>("tomato", 5, UUID.randomUUID())
                        ),
                        new Location(2, 1)
                )
        };

        ConfigFactory factory = ConfigFactory.create()
                .ignoreDefaultValues(true)
                .ignoreEmptyDictionary(true)
                .useNamingStrategy(NamingStrategy.KEBAB_CASE)
                .build();
        Dictionary wrapper = (Dictionary) factory.getNormalizer().normalize(item);
        System.out.println(GSON.toJson(wrapper));
    }
}
