package configs;

import dev.anhcraft.config.annotations.*;

import java.net.URL;
import java.util.List;
import java.util.Map;

@Configurable
public class Market {
    @Path("items")
    @Examples(
            {
                    @Example(
                            {
                                    "flags:",
                                    "  '1':",
                                    "    location: assault -345.41 43.00 -224.30 52.35 11.55",
                                    "    display_name:",
                                    "      valid: \"&a&l{__flag_team__} | &f&l{__flag_health__} &c&l❤\"",
                                    "      invalid: \"&7&l{__flag_team__} | &f&l{__flag_health__}/{__flag_max_health__} &c&l❤\"",
                                    "      neutral: \"&7Neutral\"",
                                    "    max_health: 10",
                                    "  '2':",
                                    "    location: assault -355.86 40.50 -285.70 176.70 90.00",
                                    "    display_name:",
                                    "      valid: \"&a&l{__flag_team__} | &f&l{__flag_health__} &c&l❤\"",
                                    "      invalid: \"&7&l{__flag_team__} | &f&l{__flag_health__}/{__flag_max_health__} &c&l❤\"",
                                    "      neutral: \"&7Neutral\"",
                                    "    max_health: 15"
                            }
                    )
            }
    )
    public Item[] products;

    @Validation(notEmpty = true, silent = true)
    public List<Transaction> transactions;

    public Map<String, Integer> counter;

    public URL website;

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
