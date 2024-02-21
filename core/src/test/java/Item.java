import dev.anhcraft.config.meta.Alias;
import dev.anhcraft.config.meta.Validate;

public class Item {
    @Alias("id")
    @Validate("size=3|")
    public String name;
    public int stock;
}
