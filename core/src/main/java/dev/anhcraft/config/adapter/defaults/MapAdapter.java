package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.type.ComplexTypes;
import dev.anhcraft.config.Dictionary;
import dev.anhcraft.config.type.SimpleTypes;
import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.context.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.*;

public class MapAdapter implements TypeAdapter<Map<?, ?>> {
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public @Nullable Object simplify(@NotNull Context ctx, @NotNull Class<Map<?, ?>> sourceType, @NotNull Map<?, ?> value) throws Exception {
        Dictionary dict = new Dictionary();
        for (Map.Entry<?, ?> object : value.entrySet()) {
            Object key = ctx.simplify(ctx, (Class) object.getKey().getClass(), object.getKey());
            if (key == null || SimpleTypes.getContainerSize(key) > 1)
                continue;
            Object val = ctx.simplify(ctx, (Class) object.getValue().getClass(), object.getValue());
            if (val == null)
                continue;
            dict.put(String.valueOf(key), val);
        }
        return dict;
    }

    @Override
    public @Nullable Map<?, ?> complexify(@NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
        if (value instanceof Dictionary) {
            Type keyType = ComplexTypes.getActualType(targetType, 0);
            Type valueType = ComplexTypes.getActualType(targetType, 1);
            if (keyType == null || valueType == null)
                return null;

            Class<?> targetClazz = ComplexTypes.erasure(targetType);
            Map<Object, Object> map;

            if (HashMap.class.isAssignableFrom(targetClazz)) {
                map = new HashMap<>();
            } else if (SortedMap.class.isAssignableFrom(targetClazz)) {
                map = new TreeMap<>();
            } else {
                map = new LinkedHashMap<>();
            }

            for (Map.Entry<String, Object> entry : ((Dictionary) value).entrySet()) {
                Object key = ctx.complexify(ctx, entry.getKey(), keyType);
                Object val = ctx.complexify(ctx, entry.getValue(), valueType);
                if (key == null || val == null)
                    continue;
                map.put(key, val);
            }
            return map;
        }
        return null;
    }
}
