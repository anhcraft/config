package dev.anhcraft.config.bungee;

import dev.anhcraft.config.ConfigProvider;
import dev.anhcraft.config.ConfigSerializer;

public class BungeeConfigSerializer extends ConfigSerializer {
    public BungeeConfigSerializer(ConfigProvider configProvider) {
        super(configProvider);
        BungeeAdapters.registerFor(this);
    }
}
