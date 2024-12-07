package model;

import dev.anhcraft.config.meta.Alias;
import dev.anhcraft.config.meta.Describe;
import dev.anhcraft.config.meta.Validate;

public class Warehouse<T> {
  @Describe({
    "The name of the warehouse",
    "The name of the warehouse should be at least 3 characters long"
  })
  @Alias("id")
  @Validate("not-null, size=3|")
  private final String name;

  @Describe("The storages of the warehouse")
  private final Storage<T>[] storages;

  public Warehouse(String name, Storage<T>[] storages) {
    this.name = name;
    this.storages = storages;
  }

  public String getName() {
    return name;
  }

  public Storage<T>[] getStorages() {
    return storages;
  }
}
