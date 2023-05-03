package dev.anhcraft.config.bukkit;

import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigProvider;
import dev.anhcraft.config.ConfigSerializer;
import dev.anhcraft.config.bukkit.struct.YamlConfigSection;
import dev.anhcraft.config.struct.ConfigSection;
import org.jetbrains.annotations.NotNull;

public abstract class BukkitConfigProvider implements ConfigProvider {
    public static final BukkitConfigProvider YAML = new BukkitConfigProvider() {
        @Override
        public @NotNull ConfigSection createSection() {
            return new YamlConfigSection();
        }
    };
}
