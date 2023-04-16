package configs;

import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Path;
import dev.anhcraft.config.annotations.Validation;

import java.util.List;
import java.util.Map;

@Configurable
public class Market {
    @Path("items")
    public Item[] products;

    @Validation(notEmpty = true, silent = true)
    public List<Transaction> transactions;

    public Map<String, Integer> counter;

    @Configurable
    public static class Item {
        @Validation(notNull = true)
        public String label;

        public double price;

        public Item(String label, double price) {
            this.label = label;
            this.price = price;
        }
    }

    @Configurable
    public static class Transaction {
        public Item item;

        public long date;

        public Transaction(Item item, long date) {
            this.item = item;
            this.date = date;
        }
    }
}
