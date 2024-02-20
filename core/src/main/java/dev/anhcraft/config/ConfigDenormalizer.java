package dev.anhcraft.config;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public class ConfigDenormalizer {
    private final ConfigFactory configFactory;
    private final int contextDepthLimit;

    public ConfigDenormalizer(ConfigFactory configFactory, int contextDepthLimit) {
        this.configFactory = configFactory;
        this.contextDepthLimit = contextDepthLimit;
    }

    public <T> Object denormalize(@NotNull Type desiredType, @NotNull T complex) throws Exception {

    }
}
