package dev.anhcraft.config;

import dev.anhcraft.config.adapters.TypeAdapter;
import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.schema.ConfigSchema;
import dev.anhcraft.config.schema.EntrySchema;
import dev.anhcraft.config.schema.SchemaScanner;
import dev.anhcraft.config.struct.ConfigSection;
import dev.anhcraft.config.struct.SimpleForm;
import dev.anhcraft.config.utils.TypeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
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
            }
            Class<?> type = rawType;
            while (true) {
                //noinspection unchecked
                TypeAdapter<T> typeAdapter = (TypeAdapter<T>) getTypeAdapter(type);
                if (typeAdapter != null) {
                    return typeAdapter.simplify(this, sourceType, object);
                }
                for (Class<?> clazz : type.getInterfaces()) {
                    typeAdapter = (TypeAdapter<T>) getTypeAdapter(clazz);
                    if (typeAdapter != null) {
                        return typeAdapter.simplify(this, sourceType, object);
                    }
                }
                type = type.getSuperclass();
                if (!shouldCallSuperAdapter() || type == null || type.equals(Object.class)) {
                    break;
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
            if (entrySchema.isVirtual()) continue;
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

    public interface Middleware {
        @Nullable <ComplexT> ComplexT transform(@NotNull ConfigSerializer serializer, @NotNull EntrySchema entrySchema, @Nullable ComplexT value);
    }
}
