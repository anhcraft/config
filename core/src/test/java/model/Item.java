package model;

import dev.anhcraft.config.meta.Validate;

import java.util.UUID;

public class Item<T> {
    public Item(T value, int stack, UUID owner) {
        this.value = value;
        this.stack = stack;
        this.owner = owner;
    }

    public T value;
    @Validate("range=0|")
    public int stack;
    public UUID owner;
}
