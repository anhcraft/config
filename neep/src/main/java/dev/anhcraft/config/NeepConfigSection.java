package dev.anhcraft.config;

import dev.anhcraft.config.struct.ConfigSection;
import dev.anhcraft.config.struct.SimpleForm;
import dev.anhcraft.config.utils.ObjectUtil;
import dev.anhcraft.neep.errors.NeepWriterException;
import dev.anhcraft.neep.struct.NeepComponent;
import dev.anhcraft.neep.struct.container.NeepContainer;
import dev.anhcraft.neep.struct.container.NeepList;
import dev.anhcraft.neep.struct.container.NeepSection;
import dev.anhcraft.neep.struct.primitive.*;
import dev.anhcraft.neep.writer.NeepWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

// see: https://github.com/anhcraft/Neep/blob/master/src/main/java/dev/anhcraft/neep/struct/NeepComponent.java

public class NeepConfigSection implements ConfigSection {
    private final NeepSection backend;

    public NeepConfigSection() {
        this(new NeepSection(null, "", null, new ArrayList<>()));
    }

    public NeepConfigSection(@NotNull NeepSection backend) {
        this.backend = backend;
    }

    public static NeepComponent unwrap(@NotNull NeepContainer<?> container, @NotNull String key, @NotNull Object object) {
        if (object instanceof NeepConfigSection) {
            NeepSection old = ((NeepConfigSection) object).backend;
            return new NeepSection(container, key, null, old.stream().collect(Collectors.toList()));
        } else if (object instanceof NeepComponent) {
            return (NeepComponent) object;
        } else if (object instanceof String) {
            return new NeepString(container, key, object.toString(), null);
        } else if (object instanceof Boolean) {
            return new NeepBoolean(container, key, object.toString(), null);
        } else if (object instanceof Double || object instanceof Float) {
            double val = ((Number) object).doubleValue();
            return new NeepDouble(container, key, String.valueOf(val), null);
        } else if (object instanceof Long) {
            long val = (Long) object;
            return new NeepLong(container, key, String.valueOf(val), null);
        } else if (object instanceof Number) {
            int val = ((Number) object).intValue();
            return new NeepInt(container, key, String.valueOf(val), null);
        } else if (object.getClass().isArray()) {
            NeepList<NeepComponent> list = new NeepList<>(container, key, null, new ArrayList<>());
            int i = 0;
            for (Object o : (Object[]) object) {
                list.add(unwrap(list, String.valueOf(i++), o));
            }
            return list;
        } else {
            throw new IllegalArgumentException("Cannot convert object to component");
        }
    }

    @Override
    public boolean isEmpty() {
        return backend.size() == 0;
    }

    public Object wrap(NeepComponent component) {
        if (component instanceof NeepInt) {
            return ((NeepInt) component).getValueAsInt();
        } else if (component instanceof NeepLong) {
            return ((NeepLong) component).getValueAsLong();
        } else if (component instanceof NeepDouble) {
            return ((NeepDouble) component).getValueAsDouble();
        } else if (component instanceof NeepBoolean) {
            return ((NeepBoolean) component).getValue();
        } else if (component.isDynamic()) {
            return component.asDynamic().stringifyValue();
        } else if (component.isSection()) {
            return new NeepConfigSection(component.asSection());
        } else if (component.isList()) {
            return component.asList().stream()
                    .filter(Objects::nonNull)
                    .map(o -> (NeepComponent) o)
                    .map(this::wrap)
                    .toArray();
        } else if (component.isComment()) {
            return component.asComment().getContent();
        } else {
            throw new IllegalStateException("Cannot get value as object");
        }
    }

    @Override
    public void set(@NotNull String path, @Nullable SimpleForm value) {
        Object object = value == null ? null : value.getObject();
        if (object != null) {
            backend.add(unwrap(backend, path, object));
        }
    }

    @Override
    public @Nullable SimpleForm get(@NotNull String path) throws Exception {
        NeepComponent component = backend.get(path);
        return component == null ? null : SimpleForm.of(wrap(component));
    }

    @Override
    public @NotNull Set<String> getKeys(boolean deep) {
        return backend.getKeys(deep);
    }

    @Override
    public @NotNull ConfigSection deepClone() throws Exception {
        NeepSection conf = new NeepSection(backend.getParent(), backend.getKey(), backend.getInlineComment(), new ArrayList<>());
        for (String e : backend.getKeys(false)) {
            Object v = backend.get(e);
            if (v == null) continue;
            // TODO deep copy here :D
            if (v instanceof NeepSection) {
                conf.add((NeepComponent) v);
            } else {
                v = ((NeepComponent) v).getValueAsObject();
                conf.add(NeepComponent.create(conf, e, ObjectUtil.shallowCopy(Objects.requireNonNull(v))));
            }
        }
        return new NeepConfigSection(conf);
    }

    @Override
    public @NotNull String stringify() {
        try {
            return NeepWriter.stringify(backend);
        } catch (NeepWriterException e) {
            e.printStackTrace();
        }
        return "";
    }

    @NotNull
    public NeepSection getBackend() {
        return backend;
    }
}
