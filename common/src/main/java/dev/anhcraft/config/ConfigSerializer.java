package dev.anhcraft.config;

import dev.anhcraft.config.adapters.TypeAdapter;
import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.schema.ConfigSchema;
import dev.anhcraft.config.schema.EntrySchema;
import dev.anhcraft.config.schema.SchemaScanner;
import dev.anhcraft.config.struct.ConfigSection;
import dev.anhcraft.config.struct.SimpleForm;
import dev.anhcraft.config.utils.ObjectUtil;
import dev.anhcraft.config.utils.TypeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class ConfigSerializer extends ConfigHandler {
    private Middleware middleware;

    // protected constructor to prevent creating instances directly
    // to create a new instance, look at the implemented config provider
    protected ConfigSerializer(ConfigProvider configProvider) {
        super(configProvider);
    }

    @Nullable
    public Middleware getMiddleware() {
        return middleware;
    }

    public void setMiddleware(@Nullable Middleware middleware) {
        this.middleware = middleware;
    }

    @Nullable
    public <T> SimpleForm transform(@NotNull Type sourceType, @Nullable T object) throws Exception {
        sourceType = TypeUtil.normalize(sourceType);
        if (object != null) {
            if (sourceType instanceof GenericArrayType) {
                if (!isCustomArrayAdapterPreferred()) {
                    return transformArray(Objects.requireNonNull(TypeUtil.getElementType(sourceType)), object);
                }
            }
            Class<?> rawType = Objects.requireNonNull(TypeUtil.getRawType(sourceType));
            if (rawType.isAnnotationPresent(Configurable.class)) {
                return SimpleForm.of(transformConfig(Objects.requireNonNull(SchemaScanner.scanConfig(rawType)), object));
            } else if (rawType.isArray()) {
                if (!isCustomArrayAdapterPreferred()) {
                    return transformArray(Objects.requireNonNull(TypeUtil.getElementType(sourceType)), object);
                }
            } else if (rawType.equals(List.class)) {
                if (!isCustomListAdapterPreferred()) {
                    Type t = TypeUtil.getElementType(sourceType);
                    // t may be null if the list is using raw type (no generic param specified)
                    return transformList(t == null ? Object.class : t, object);
                }
            }
            Class<?> type = rawType;
            while (true) {
                //noinspection unchecked
                TypeAdapter<T> typeAdapter = (TypeAdapter<T>) getTypeAdapter(type);
                if (typeAdapter != null) {
                    return typeAdapter.simplify(this, sourceType, object);
                } else {
                    type = type.getSuperclass();
                    if (!shouldCallSuperAdapter() || type == null || type.equals(Object.class)) {
                        break;
                    }
                }
            }
            return SimpleForm.of(object);
        }
        return null;
    }

    public <T> ConfigSection transformConfig(@NotNull ConfigSchema configSchema, @NotNull T object) throws Exception {
        return transformConfig(configSchema, getConfigProvider().createSection(), object);
    }

    public <T> ConfigSection transformConfig(@NotNull ConfigSchema configSchema,
                                             @NotNull ConfigSection configSection,
                                             @NotNull T object) throws Exception {
        for (EntrySchema entrySchema : configSchema.getEntrySchemas()) {
            Field field = entrySchema.getField();
            String key = entrySchema.getKey();
            Object val = field.get(object);
            if (middleware != null) {
                val = middleware.transform(this, entrySchema, val);
            }
            configSection.set(key, transform(field.getGenericType(), val));
        }
        return configSection;
    }

    @Nullable
    public <T> SimpleForm transformArray(@NotNull Type componentType, @NotNull T object) throws Exception {
        int length = Array.getLength(object);
        Object arr = Array.newInstance(Object.class, length);
        for (int i = 0; i < length; i++) {
            Object obj = Array.get(object, i);
            SimpleForm simplex = transform(componentType, obj);
            if (simplex != null) {
                Array.set(arr, i, simplex.getObject());
            }
        }
        return SimpleForm.of(arr);
    }

    @Nullable
    public <T> SimpleForm transformList(@NotNull Type componentType, @NotNull T object) throws Exception {
        List<?> list = (List<?>) ObjectUtil.shallowCopy(object);
        //noinspection unchecked
        ListIterator<T> li = (ListIterator<T>) list.listIterator();
        while (li.hasNext()) {
            //noinspection unchecked
            li.set((T) Objects.requireNonNull(transform(componentType, li.next())).getObject());
        }
        return SimpleForm.of(list);
    }

    public interface Middleware {
        @Nullable <ComplexT> ComplexT transform(@NotNull ConfigSerializer serializer, @NotNull EntrySchema entrySchema, @Nullable ComplexT value);
    }
}
