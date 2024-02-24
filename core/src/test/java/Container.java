import dev.anhcraft.config.meta.Alias;
import dev.anhcraft.config.meta.Validate;

public class Container<T> {
    @Alias("id")
    @Validate("size=3|")
    public String name;
    public T[] items;
    public Container subContainer;
}
