package dev.anhcraft.config.bungee;

import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigProvider;
import dev.anhcraft.config.ConfigSerializer;
import dev.anhcraft.config.bungee.struct.BungeeConfigSection;
import dev.anhcraft.config.struct.ConfigSection;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.JsonConfiguration;
import net.md_5.bungee.config.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public abstract class BungeeConfigProvider implements ConfigProvider {
    public static final BungeeConfigProvider YAML = new BungeeConfigProvider() {
        @Override
        public @NotNull ConfigSection createSection() {
            return new BungeeConfigSection(ConfigurationProvider.getProvider(YamlConfiguration.class));
        }
    };

    public static final BungeeConfigProvider JSON = new BungeeConfigProvider() {
        @Override
        public @NotNull ConfigSection createSection() {
            return new BungeeConfigSection(ConfigurationProvider.getProvider(JsonConfiguration.class));
        }
    };
}
