package dev.anhcraft.config.adapters.defaults;

import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigSerializer;
import dev.anhcraft.config.adapters.TypeAdapter;
import dev.anhcraft.config.struct.ConfigSection;
import dev.anhcraft.config.struct.SimpleForm;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class MapAdapter implements TypeAdapter<Map<?, ?>> {

    // this method can be inherited to add more map types
    @NotNull
    protected Map<?, ?> createMapOf(@NotNull Type type) {
        if (type instanceof Hashtable) {
            return new Hashtable<>();
        } else if (type instanceof TreeMap) {
            return new TreeMap<>();
        } else if (type instanceof LinkedHashMap) {
            return new LinkedHashMap<>();
        } else if (type instanceof WeakHashMap) {
            return new WeakHashMap<>();
        } else if (type instanceof IdentityHashMap) {
            return new IdentityHashMap<>();
        } else if (type instanceof EnumMap) {
            throw new UnsupportedOperationException();
        } else {
            return new HashMap<>();
        }
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
