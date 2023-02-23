import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigHandler;
import dev.anhcraft.config.ConfigSerializer;
import dev.anhcraft.config.bungee.BungeeConfigProvider;
import dev.anhcraft.config.bungee.struct.BungeeConfigSection;
import dev.anhcraft.config.struct.SimpleForm;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

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
        ConfigSerializer serializer = BungeeConfigProvider.YAML.createSerializer();
        registerAdapters(serializer);
        consumer.accept(serializer);
        return serializer.transform(clazz, object);
    }

    protected <T> T deserialize(Class<? extends T> clazz, Configuration config) throws Exception {
        return deserialize(clazz, config, configDeserializer -> {
        });
    }

    protected <T> T deserialize(Class<? extends T> clazz,
                                Configuration config,
                                Consumer<ConfigDeserializer> consumer) throws Exception {
        ConfigDeserializer deserializer = BungeeConfigProvider.YAML.createDeserializer();
        registerAdapters(deserializer);
        consumer.accept(deserializer);
        return deserializer.transform(clazz, SimpleForm.of(new BungeeConfigSection(ConfigurationProvider.getProvider(YamlConfiguration.class), config)));
    }

    protected void debug(Configuration cs) {
        debug("", cs);
    }

    protected void debug(String prefix, Configuration cs) {
        for (String k : cs.getKeys()) {
            Object v = cs.get(k);
            System.out.println(prefix + "> " + k + ": " + v.getClass().getName());
            if (v instanceof Configuration) {
                debug(prefix + "  ", (Configuration) v);
            } else if (v instanceof Iterable) {
                ((Iterable<?>) v).forEach((Consumer<Object>) o -> {
                    System.out.println(prefix + " - " + o.getClass().getName());
                    if (o instanceof Configuration) {
                        debug(prefix + "   ", (Configuration) o);
                    }
                });
            }
        }
    }
}
