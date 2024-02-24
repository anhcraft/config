package model;

import dev.anhcraft.config.meta.Alias;
import dev.anhcraft.config.meta.Validate;

public class Warehouse<T> {
    @Alias("id")
    @Validate("size=3|")
    public String name;
    public Storage<T>[] storages;
}
