package dev.anhcraft.config;

import dev.anhcraft.config.adapters.TypeAdapter;
import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Validation;
import dev.anhcraft.config.exceptions.InvalidValueException;
import dev.anhcraft.config.schema.ConfigSchema;
import dev.anhcraft.config.schema.EntrySchema;
import dev.anhcraft.config.schema.SchemaScanner;
import dev.anhcraft.config.struct.ConfigSection;
import dev.anhcraft.config.struct.SimpleForm;
import dev.anhcraft.config.utils.ClassUtil;
import dev.anhcraft.config.utils.ObjectUtil;
import dev.anhcraft.config.utils.TypeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfigDeserializer extends ConfigHandler {
    private final List<Middleware> middlewares = new ArrayList<>();
    private Callback callback;
    private boolean wrapSingleElement = true;

    // protected constructor to prevent creating instances directly
    // to create a new instance, look at the implemented config provider
    protected ConfigDeserializer(ConfigProvider configProvider) {
        super(configProvider);
    }

    public void addMiddleware(@NotNull Middleware middleware) {
        this.middlewares.add(middleware);
    }

    public void setCallback(@Nullable Callback callback) {
        this.callback = callback;
    }

    /**
     * If the given {@link SimpleForm} contains a single object whose
     * type matches the element type, a new instance of the array
     * will be created and the object becomes the only member.
     *
     * @param value {@code true} or {@code false}
     */
    public void wrapSingleElement(boolean value) {
        this.wrapSingleElement = value;
    }

    @Nullable
    public <T> T transform(@NotNull Type targetType, @Nullable SimpleForm simpleForm) throws Exception {
        targetType = TypeUtil.normalize(targetType);
        if (simpleForm != null) {
            if (targetType instanceof GenericArrayType) {
                if (!isCustomArrayAdapterPreferred()) {
                    return transformArray(Objects.requireNonNull(TypeUtil.getElementType(targetType)), simpleForm);
                }
            }
            Class<?> rawType = Objects.requireNonNull(TypeUtil.getRawType(targetType));
            if (rawType.isAnnotationPresent(Configurable.class) && simpleForm.isSection()) {
                return transformConfig(Objects.requireNonNull(SchemaScanner.scanConfig(rawType)), Objects.requireNonNull(simpleForm.asSection()));
            } else if (rawType.isArray()) {
                if (!isCustomArrayAdapterPreferred()) {
                    return transformArray(Objects.requireNonNull(TypeUtil.getElementType(targetType)), simpleForm);
                }
            }
            Class<?> type = rawType;
            while (true) {
                TypeAdapter<?> typeAdapter = getTypeAdapter(type);
                if (typeAdapter != null) {
                    //noinspection unchecked
                    return (T) typeAdapter.complexify(this, targetType, simpleForm);
                }
                for (Class<?> clazz : type.getInterfaces()) {
                    typeAdapter = getTypeAdapter(clazz);
                    if (typeAdapter != null) {
                        //noinspection unchecked
                        return (T) typeAdapter.complexify(this, targetType, simpleForm);
                    }
                }
                type = type.getSuperclass();
                if (!shouldCallSuperAdapter() || type == null || type.equals(Object.class)) {
                    break;
                }
            }
            //noinspection unchecked
            return (T) simpleForm.getObject();
        }
        return null;
    }

    @NotNull
    public <T> T transformConfig(@NotNull ConfigSchema configSchema, @NotNull ConfigSection configSection) throws Exception {
        //noinspection unchecked
        return (T) transformConfig(configSchema, configSection, ObjectUtil.newInstance(configSchema.getOwner()));
    }

    @NotNull
    public <T> T transformConfig(@NotNull ConfigSchema configSchema, @NotNull ConfigSection configSection, @NotNull T object) throws Exception {
        for (EntrySchema entrySchema : configSchema.getEntrySchemas()) {
            if (entrySchema.isConsistent()) continue;
            Field field = entrySchema.getField();
            Validation validation = entrySchema.getValidation();
            String key = entrySchema.getKey();
            SimpleForm simpleForm = configSection.get(key);
            for (Middleware m : middlewares) {
                simpleForm = m.transform(this, configSchema, entrySchema, simpleForm);
            }
            if (validation != null) {
                if (simpleForm == null && validation.notNull()) {
                    if (validation.silent()) {
                        continue;
                    } else {
                        throw new InvalidValueException(key, InvalidValueException.Reason.NULL);
                    }
                }
                if (simpleForm != null && validation.notEmpty() && simpleForm.isEmpty()) {
                    if (validation.silent()) {
                        continue;
                    } else {
                        throw new InvalidValueException(key, InvalidValueException.Reason.EMPTY);
                    }
                }
            }
            Object complex = transform(field.getGenericType(), simpleForm);
            if (complex == null) {
                // null can't be set to primitive fields
                if (field.getType().isPrimitive()) continue;
            } else {
                // check data type
                if (!ClassUtil.isAssignable(field.getType(), complex.getClass())) continue;
            }
            if (complex == null && validation != null && validation.notNull() && validation.silent()) {
                continue;
            }
            field.set(object, complex);
        }
        for (Method m : configSchema.getPostHandlers()) {
            try {
                if (m.getParameterCount() == 0) {
                    m.invoke(object);
                } else if (m.getParameterCount() == 1) {
                    m.invoke(object, this);
                } else if (m.getParameterCount() == 2) {
                    m.invoke(object, this, configSchema);
                } else if (m.getParameterCount() == 3) {
                    m.invoke(object, this, configSchema, configSection);
                }
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (callback != null) {
            callback.accept(this, configSchema, object);
        }
        return object;
    }

    @Nullable
    public <T> T transformArray(@NotNull Type componentType, @NotNull SimpleForm simpleForm) throws Exception {
        if (simpleForm.isArray()) {
            Object origin = simpleForm.getObject();
            int length = Array.getLength(origin);
            Object arr = Array.newInstance(TypeUtil.getRawType(componentType), length);
            for (int i = 0; i < length; i++) {
                Object obj = Array.get(origin, i);
                Array.set(arr, i, transform(componentType, SimpleForm.of(obj)));
            }
            //noinspection unchecked
            return (T) arr;
        } else {
            if (wrapSingleElement) {
                Object arr = Array.newInstance(TypeUtil.getRawType(componentType), 1);
                Array.set(arr, 0, simpleForm.getObject());
                //noinspection unchecked
                return (T) arr;
            }
        }
        return null;
    }

    public interface Middleware {
        @Nullable
        SimpleForm transform(@NotNull ConfigDeserializer deserializer, @NotNull ConfigSchema configSchema, @NotNull EntrySchema entrySchema, @Nullable SimpleForm value);
    }

    public interface Callback {
        void accept(@NotNull ConfigDeserializer deserializer, @NotNull ConfigSchema configSchema, @Nullable Object value);
    }
}
