package dev.anhcraft.config.bukkit;

import dev.anhcraft.config.ConfigProvider;
import dev.anhcraft.config.ConfigSerializer;

public class BukkitConfigSerializer extends ConfigSerializer {
    public BukkitConfigSerializer(ConfigProvider configProvider) {
        super(configProvider);
        BukkitAdapters.registerFor(this);
    }
}
