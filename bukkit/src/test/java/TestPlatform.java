import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigHandler;
import dev.anhcraft.config.ConfigSerializer;
import dev.anhcraft.config.bukkit.BukkitConfigProvider;
import dev.anhcraft.config.bukkit.struct.YamlConfigSection;
import dev.anhcraft.config.struct.SimpleForm;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.function.Consumer;

public class TestPlatform {
    protected void registerAdapters(ConfigHandler handler) {
        //handler.registerTypeAdapter(UUID.class, new UUIDAdapter());
    }

    protected <T> SimpleForm serialize(Class<? extends T> clazz, T object) throws Exception {
        return serialize(clazz, object, serializer -> {
        });
    }

    protected <T> SimpleForm serialize(Class<? extends T> clazz,
                                     T object,
                                     Consumer<ConfigSerializer> consumer) throws Exception {
        ConfigSerializer serializer = BukkitConfigProvider.YAML.createSerializer();
        registerAdapters(serializer);
        consumer.accept(serializer);
        return serializer.transform(clazz, object);
    }

    protected <T> T deserialize(Class<? extends T> clazz, YamlConfiguration config) throws Exception {
        return deserialize(clazz, config, configDeserializer -> {
        });
    }

    protected <T> T deserialize(Class<? extends T> clazz,
                              YamlConfiguration config,
                              Consumer<ConfigDeserializer> consumer) throws Exception {
        ConfigDeserializer deserializer = BukkitConfigProvider.YAML.createDeserializer();
        registerAdapters(deserializer);
        consumer.accept(deserializer);
        return deserializer.transform(clazz, SimpleForm.of(new YamlConfigSection(config)));
    }

    protected void debug(ConfigurationSection cs) {
        debug("", cs);
    }

    protected void debug(String prefix, ConfigurationSection cs) {
        for (String k : cs.getKeys(false)) {
            Object v = cs.get(k);
            System.out.println(prefix + "> " + k + ": " + v.getClass().getName());
            if (v instanceof ConfigurationSection) {
                debug(prefix + "  ", (ConfigurationSection) v);
            } else if (v instanceof Iterable) {
                ((Iterable<?>) v).forEach((Consumer<Object>) o -> {
                    System.out.println(prefix + " - " + o.getClass().getName());
                    if (o instanceof ConfigurationSection) {
                        debug(prefix + "   ", (ConfigurationSection) o);
                    }
                });
            }
        }
    }
}
