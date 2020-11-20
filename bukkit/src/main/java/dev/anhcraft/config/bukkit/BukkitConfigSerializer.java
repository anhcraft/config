package dev.anhcraft.config.bukkit;

import dev.anhcraft.config.ConfigProvider;
import dev.anhcraft.config.ConfigSerializer;

public class BukkitConfigSerializer extends ConfigSerializer {
    // package-protected constructor to prevent creating instances directly
    // to create a new instance, look at the implemented config provider
    BukkitConfigSerializer(ConfigProvider configProvider) {
        super(configProvider);
        BukkitAdapters.registerFor(this);
    }
}
