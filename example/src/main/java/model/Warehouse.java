package model;

import dev.anhcraft.config.meta.Alias;
import dev.anhcraft.config.meta.Describe;
import dev.anhcraft.config.meta.Validate;

public class Warehouse<T> {
    @Describe(
      {
        "The name of the warehouse",
        "The name of the warehouse should be at least 3 characters long"
      }
    )
    @Alias("id")
    @Validate("not-null, size=3|")
    public String name;

    @Describe("The storages of the warehouse")
    public Storage<T>[] storages;
}
