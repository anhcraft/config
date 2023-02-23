import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigHandler;
import dev.anhcraft.config.ConfigSerializer;
import dev.anhcraft.config.NeepConfigSection;
import dev.anhcraft.config.struct.SimpleForm;
import dev.anhcraft.neep.struct.container.NeepSection;

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
        ConfigSerializer serializer = BukkitNeepConfigProvider.INSTANCE.createSerializer();
        registerAdapters(serializer);
        consumer.accept(serializer);
        return serializer.transform(clazz, object);
    }

    protected <T> T deserialize(Class<? extends T> clazz, NeepSection config) throws Exception {
        return deserialize(clazz, config, configDeserializer -> {
        });
    }

    protected <T> T deserialize(Class<? extends T> clazz,
                                NeepSection config,
                                Consumer<ConfigDeserializer> consumer) throws Exception {
        ConfigDeserializer deserializer = BukkitNeepConfigProvider.INSTANCE.createDeserializer();
        registerAdapters(deserializer);
        consumer.accept(deserializer);
        return deserializer.transform(clazz, SimpleForm.of(new NeepConfigSection(config)));
    }

    protected void debug(NeepSection cs) {
        debug("", cs);
    }

    protected void debug(String prefix, NeepSection cs) {
        for (String k : cs.getKeys(false)) {
            Object v = cs.get(k);
            System.out.println(prefix + "> " + k + ": " + v.getClass().getName());
            if (v instanceof NeepSection) {
                debug(prefix + "  ", (NeepSection) v);
            } else if (v instanceof Iterable) {
                ((Iterable<?>) v).forEach((Consumer<Object>) o -> {
                    System.out.println(prefix + " - " + o.getClass().getName());
                    if (o instanceof NeepSection) {
                        debug(prefix + "   ", (NeepSection) o);
                    }
                });
            }
        }
    }
}
