package dev.anhcraft.config.bungee;

import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigProvider;

public class BungeeConfigDeserializer extends ConfigDeserializer {
    public BungeeConfigDeserializer(ConfigProvider configProvider) {
        super(configProvider);
        BungeeAdapters.registerFor(this);
    }
}
