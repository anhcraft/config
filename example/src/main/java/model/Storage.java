package model;

import dev.anhcraft.config.meta.Describe;
import dev.anhcraft.config.meta.Optional;

import java.util.List;

public class Storage<T> {
    @Describe("The type of the storage")
    public StorageType type;
    @Describe("The items in the storage")
    @Optional
    public List<T> items = List.of();
    @Describe("The location of the storage")
    public Location location;

    public Storage(StorageType type, List<T> items, Location location) {
        this.type = type;
        this.items = items;
        this.location = location;
    }
}
