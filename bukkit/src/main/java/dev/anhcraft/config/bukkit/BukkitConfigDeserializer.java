package dev.anhcraft.config.bukkit;

import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigProvider;

public class BukkitConfigDeserializer extends ConfigDeserializer {
    public BukkitConfigDeserializer(ConfigProvider configProvider) {
        super(configProvider);
        BukkitAdapters.registerFor(this);
    }
}
