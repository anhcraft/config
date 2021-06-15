package dev.anhcraft.config.bungee.struct;

import dev.anhcraft.config.struct.ConfigSection;
import dev.anhcraft.config.struct.SimpleForm;
import dev.anhcraft.config.utils.ObjectUtil;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.StringWriter;
import java.util.*;

public class BungeeConfigSection implements ConfigSection {
    private final ConfigurationProvider provider;
    private final Configuration backend;

    public BungeeConfigSection(@NotNull ConfigurationProvider provider) {
        this(provider, new Configuration());
    }

    public BungeeConfigSection(@NotNull ConfigurationProvider provider, @NotNull Configuration backend) {
        this.provider = provider;
        this.backend = backend;
    }

    @Override
    public boolean isEmpty() {
        return backend.getKeys().isEmpty();
    }

    @SuppressWarnings("unchecked")
    private <A, B> B wrap(A o) {
        if (o instanceof Configuration) {
            try {
                return (B) new BungeeConfigSection(provider, deepClone((Configuration) o));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else if (o instanceof List<?>) {
            ((List<?>) o).replaceAll(this::wrap);
            return (B) o;
        } else if (o != null && o.getClass().isArray()) {
            return (B) ObjectUtil.replaceAll(o, this::wrap);
        } else {
            return (B) o;
        }
    }

    @SuppressWarnings("unchecked")
    private <A, B> B unwrap(A o) {
        if (o instanceof BungeeConfigSection) {
            return (B) ((BungeeConfigSection) o).backend;
        } else if (o instanceof List<?>) {
            ((List<?>) o).replaceAll(this::unwrap);
            return (B) o;
        } else if (o != null && o.getClass().isArray()) {
            return (B) ObjectUtil.replaceAll(o, this::unwrap);
        } else {
            return (B) o;
        }
    }

    @Override
    public void set(@NotNull String path, @Nullable SimpleForm value) {
        Object object = value == null ? null : value.getObject();
        object = unwrap(object);
        backend.set(path, object);
    }

    @Override
    public @Nullable SimpleForm get(@NotNull String path) throws Exception {
        Object object = backend.get(path);
        object = wrap(object);
        return object == null ? null : SimpleForm.of(object);
    }

    @Override
    public @NotNull Set<String> getKeys(boolean deep) {
        return (LinkedHashSet<String>) backend.getKeys();
    }

    private Configuration deepClone(Configuration config) throws Exception {
        Configuration conf = new Configuration();
        for (String k : config.getKeys()) {
            Object v = config.get(k);
            //if(v instanceof Configuration) {
            //    conf.set(k, deepClone((Configuration) v));
            //} else {
                // TODO deep copy here :D
                conf.set(k, ObjectUtil.shallowCopy(v));
            //}
        }
        return conf;
    }

    @Override
    public @NotNull ConfigSection deepClone() throws Exception {
        return new BungeeConfigSection(provider, deepClone(backend));
    }

    @Override
    public @NotNull String stringify() {
        StringWriter sw = new StringWriter();
        provider.save(backend, sw);
        return sw.toString();
    }

    @NotNull
    public Configuration getBackend() {
        return backend;
    }
}
