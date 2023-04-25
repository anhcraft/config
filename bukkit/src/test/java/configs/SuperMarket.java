package configs;

import dev.anhcraft.config.annotations.*;

import java.util.ArrayList;
import java.util.List;

@Configurable
public class SuperMarket extends Market {
    public List<Shop> shops = new ArrayList<>();

    @Configurable
    public static class Shop {
        private final String name;
        private final String description;

        public Shop(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }
}
