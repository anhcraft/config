package dev.anhcraft.config.bungee;

import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigProvider;

public class BungeeConfigDeserializer extends ConfigDeserializer {
    // package-protected constructor to prevent creating instances directly
    // to create a new instance, look at the implemented config provider
    BungeeConfigDeserializer(ConfigProvider configProvider) {
        super(configProvider);
        BungeeAdapters.registerFor(this);
    }
}
