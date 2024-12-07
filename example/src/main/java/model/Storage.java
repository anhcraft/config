package model;

import dev.anhcraft.config.meta.Describe;
import dev.anhcraft.config.meta.Optional;
import java.util.Collections;
import java.util.List;

public class Storage<T> {
  @Describe("The type of the storage")
  private final StorageType type;

  @SuppressWarnings("UnusedAssignment")
  @Describe("The items in the storage")
  @Optional
  private List<T> items = Collections.emptyList();

  @Describe("The location of the storage")
  private final Location location;

  public Storage(StorageType type, List<T> items, Location location) {
    this.type = type;
    this.items = items;
    this.location = location;
  }

  public StorageType getType() {
    return type;
  }

  public List<T> getItems() {
    return items;
  }

  public Location getLocation() {
    return location;
  }
}
