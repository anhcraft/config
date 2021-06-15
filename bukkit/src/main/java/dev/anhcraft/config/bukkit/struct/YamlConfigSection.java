package dev.anhcraft.config.bukkit.struct;

import dev.anhcraft.config.struct.ConfigSection;
import dev.anhcraft.config.struct.SimpleForm;
import dev.anhcraft.config.utils.ObjectUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class YamlConfigSection implements ConfigSection {
    private final ConfigurationSection backend;

    public YamlConfigSection() {
        this(new YamlConfiguration());
    }

    public YamlConfigSection(@NotNull ConfigurationSection backend) {
        this.backend = backend;
    }

    @Override
    public boolean isEmpty() {
        return backend.getKeys(false).isEmpty();
    }

    private YamlConfiguration copy(ConfigurationSection section, YamlConfiguration conf){
        for(Map.Entry<String, Object> k : section.getValues(false).entrySet()){
            if(k.getValue() instanceof YamlConfiguration) {
                conf.set(k.getKey(), k.getValue());
            } else if(k.getValue() instanceof ConfigurationSection) {
                conf.set(k.getKey(), copy((ConfigurationSection) k.getValue(), new YamlConfiguration()));
            } else {
                conf.set(k.getKey(), k.getValue());
            }
        }
        return conf;
    }

    @SuppressWarnings("unchecked")
    private <A, B> B wrap(A o) {
        if (o instanceof YamlConfiguration) {
            return (B) new YamlConfigSection((YamlConfiguration) o);
        } else if (o instanceof MemorySection) {
            return (B) new YamlConfigSection(copy((MemorySection) o, new YamlConfiguration()));
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
        if (o instanceof YamlConfigSection) {
            return (B) ((YamlConfigSection) o).backend;
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
        return backend.getKeys(deep);
    }

    @Override
    public @NotNull ConfigSection deepClone() throws Exception {
        YamlConfiguration conf = new YamlConfiguration();
        for (Map.Entry<String, Object> e : backend.getValues(false).entrySet()) {
            // TODO deep copy here :D
            conf.set(e.getKey(), ObjectUtil.shallowCopy(e.getValue()));
        }
        return new YamlConfigSection(conf);
    }

    @Override
    public @NotNull String stringify() {
        if(backend instanceof YamlConfiguration) {
            return ((YamlConfiguration) backend).saveToString();
        } else {
            return copy(backend, new YamlConfiguration()).saveToString();
        }
    }

    @NotNull
    public ConfigurationSection getBackend() {
        return backend;
    }
}
