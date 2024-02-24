package model;

import java.util.List;

public class Storage<T> {
    public StorageType type;
    public List<T> items;
    public Location location;

    public Storage(StorageType type, List<T> items, Location location) {
        this.type = type;
        this.items = items;
        this.location = location;
    }
}
