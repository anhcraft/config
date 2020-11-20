package dev.anhcraft.config.bukkit;

import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigProvider;

public class BukkitConfigDeserializer extends ConfigDeserializer {
    // package-protected constructor to prevent creating instances directly
    // to create a new instance, look at the implemented config provider
    BukkitConfigDeserializer(ConfigProvider configProvider) {
        super(configProvider);
        BukkitAdapters.registerFor(this);
    }
}
