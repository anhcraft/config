package dev.anhcraft.config.adapter;

import dev.anhcraft.config.ConfigFactory;
import org.jetbrains.annotations.NotNull;

public class AdapterContext {
    private final ConfigFactory factory;
    private int depth;

    public AdapterContext(@NotNull ConfigFactory factory, int depth) {
        this.factory = factory;
        this.depth = depth;
    }

    @NotNull
    public ConfigFactory getFactory() {
        return factory;
    }

    public int getDepth() {
        return depth;
    }

    public int incDepth() {
        return ++depth;
    }
}
