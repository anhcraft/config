package dev.anhcraft.config;

import dev.anhcraft.config.struct.ConfigSection;
import org.jetbrains.annotations.NotNull;

public interface ConfigProvider {
    @NotNull
    ConfigSection createSection();

    @NotNull
    ConfigSerializer createSerializer();

    @NotNull
    ConfigDeserializer createDeserializer();
}
