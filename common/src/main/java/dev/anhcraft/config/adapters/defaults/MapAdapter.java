package dev.anhcraft.config.adapters.defaults;

import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigSerializer;
import dev.anhcraft.config.adapters.TypeAdapter;
import dev.anhcraft.config.struct.ConfigSection;
import dev.anhcraft.config.struct.SimpleForm;
import dev.anhcraft.config.utils.ObjectUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MapAdapter implements TypeAdapter<Map<?, ?>> {

    // this method can be inherited to add more map types
    @NotNull
    protected Map<?, ?> createMapOf(@NotNull Type type) {
        Class<?> mapType = null;
        if (type instanceof ParameterizedType) {
            mapType = (Class<?>) ((ParameterizedType) type).getRawType();
        }
        if (mapType == null || mapType.isAssignableFrom(Map.class)) return new LinkedHashMap<>();
        if (mapType.isAssignableFrom(Hashtable.class)) return new Hashtable<>();
        if (mapType.isAssignableFrom(TreeMap.class)) return new TreeMap<>();
        if (mapType.isAssignableFrom(LinkedHashMap.class)) return new LinkedHashMap<>();
        if (mapType.isAssignableFrom(WeakHashMap.class)) return new WeakHashMap<>();
        if (mapType.isAssignableFrom(IdentityHashMap.class)) return new IdentityHashMap<>();
        if (mapType.isAssignableFrom(ConcurrentHashMap.class)) return new ConcurrentHashMap<>();
        if (mapType.isAssignableFrom(EnumMap.class)) {
            if (type instanceof ParameterizedType) {
                Type keyType = ((ParameterizedType) type).getActualTypeArguments()[0];
                return new EnumMap<>((Class) keyType);
            }
        }
        if (mapType.isAssignableFrom(HashMap.class)) return new HashMap<>();
        try {
            Object o = ObjectUtil.newInstance(mapType);
            if (o instanceof Map) {
                return (Map<?, ?>) o;
            }
        } catch (InstantiationException ignored) {
        }
        return new LinkedHashMap<>();
    }

    @Override
    public @Nullable SimpleForm simplify(@NotNull ConfigSerializer serializer, @NotNull Type sourceType, @NotNull Map<?, ?> value) throws Exception {
        Type keyType, valueType;
        if (sourceType instanceof ParameterizedType) {
            keyType = ((ParameterizedType) sourceType).getActualTypeArguments()[0];
            valueType = ((ParameterizedType) sourceType).getActualTypeArguments()[1];
        } else {
            keyType = Object.class;
            valueType = Object.class;
        }
        ConfigSection section = serializer.getConfigProvider().createSection();
        for (Map.Entry<?, ?> entry : value.entrySet()) {
            Object key = entry.getKey();
            if (key instanceof String) {
                section.set((String) key, serializer.transform(valueType, entry.getValue()));
            } else {
                SimpleForm sp = serializer.transform(keyType, key);
                if (sp != null) {
                    section.set(Objects.requireNonNull(sp.getObject().toString()), serializer.transform(valueType, entry.getValue()));
                }
            }
        }
        return SimpleForm.of(section);
    }

    @Override
    public @Nullable Map<?, ?> complexify(@NotNull ConfigDeserializer deserializer, @NotNull Type targetType, @NotNull SimpleForm value) throws Exception {
        if (value.isSection()) {
            Type keyType, valueType;
            if (targetType instanceof ParameterizedType) {
                keyType = ((ParameterizedType) targetType).getActualTypeArguments()[0];
                valueType = ((ParameterizedType) targetType).getActualTypeArguments()[1];
            } else {
                keyType = Object.class;
                valueType = Object.class;
            }
            ConfigSection section = Objects.requireNonNull(value.asSection());
            Map<?, ?> map = createMapOf(targetType);
            for (String k : section.getKeys(false)) {
                map.put(deserializer.transform(keyType, SimpleForm.of(k)), deserializer.transform(valueType, section.get(k)));
            }
            return map;
        }
        return null;
    }
}
