package dev.anhcraft.config.bungee;

import dev.anhcraft.config.ConfigProvider;
import dev.anhcraft.config.ConfigSerializer;

public class BungeeConfigSerializer extends ConfigSerializer {
    // package-protected constructor to prevent creating instances directly
    // to create a new instance, look at the implemented config provider
    BungeeConfigSerializer(ConfigProvider configProvider) {
        super(configProvider);
        BungeeAdapters.registerFor(this);
    }
}
