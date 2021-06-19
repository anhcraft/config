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
import java.util.ListIterator;
import java.util.Objects;

public class ConfigDeserializer extends ConfigHandler {
    private Middleware middleware;
    private Callback callback;
    private boolean wrapSingleElement = true;
    private boolean transformCollectionType = true;

    // protected constructor to prevent creating instances directly
    // to create a new instance, look at the implemented config provider
    protected ConfigDeserializer(ConfigProvider configProvider) {
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
    public Callback getCallback() {
        return callback;
    }

    public void setCallback(@Nullable Callback callback) {
        this.callback = callback;
    }

    /**
     * If the given {@link SimpleForm} contains a single object whose
     * type matches the element type, a new instance of the collection
     * will be created and the object becomes the only member.<br>
     * The "collection" here can be either {@link List} or array.
     *
     * @param value {@code true} or {@code false}
     */
    public void wrapSingleElement(boolean value) {
        this.wrapSingleElement = value;
    }

    /**
     * If the collection type is different from the target, the handler will
     * try to transform it to the appropriate type. All elements will be kept,
     * but the order may be changed due to the implementation of the target type.<br>
     * This option has no effect if {@link #preferCustomArrayAdapter(boolean)} or
     * {@link #preferCustomListAdapter(boolean)} is set to {@code true}.<br>
     * The "collection" here can be either {@link List} or array.
     *
     * @param value {@code true} or {@code false}
     */
    public void transformCollectionType(boolean value) {
        this.transformCollectionType = value;
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
            } else if (rawType.equals(List.class)) {
                if (!isCustomListAdapterPreferred()) {
                    Type t = TypeUtil.getElementType(targetType);
                    // t may be null if the list is using raw type (no generic param specified)
                    return transformList(t == null ? Object.class : t, simpleForm);
                }
            }
            Class<?> type = rawType;
            while (true) {
                TypeAdapter<?> typeAdapter = getTypeAdapter(type);
                if (typeAdapter != null) {
                    //noinspection unchecked
                    return (T) typeAdapter.complexify(this, targetType, simpleForm);
                } else {
                    type = type.getSuperclass();
                    if (!shouldCallSuperAdapter() || type == null || type.equals(Object.class)) {
                        break;
                    }
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
            if(entrySchema.isConsistent()) continue;
            Field field = entrySchema.getField();
            Validation validation = entrySchema.getValidation();
            String key = entrySchema.getKey();
            SimpleForm simpleForm = configSection.get(key);
            if (middleware != null) {
                simpleForm = middleware.transform(this, configSchema, entrySchema, simpleForm);
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
            field.set(object, complex);
        }
        for (Method m : configSchema.getPostHandlers()){
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
        } else if (simpleForm.isList()) {
            if (transformCollectionType) {
                List<?> list = Objects.requireNonNull(simpleForm.asList());
                Object arr = Array.newInstance(TypeUtil.getRawType(componentType), list.size());
                for (int i = 0; i < list.size(); i++) {
                    Array.set(arr, i, transform(componentType, SimpleForm.of(list.get(i))));
                }
                //noinspection unchecked
                return (T) arr;
            }
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

    @Nullable
    public <T> T transformList(@NotNull Type componentType, @NotNull SimpleForm simpleForm) throws Exception {
        if (simpleForm.isList()) {
            List<?> list = Objects.requireNonNull(simpleForm.asList());
            ListIterator<?> li = list.listIterator();
            while (li.hasNext()) {
                li.set(transform(componentType, SimpleForm.of(li.next())));
            }
            //noinspection unchecked
            return (T) list;
        } else if (simpleForm.isArray()) {
            if (transformCollectionType) {
                List<?> list = new ArrayList<>();
                Object array = simpleForm.getObject();
                int length = Array.getLength(array);
                for (int i = 0; i < length; i++) {
                    list.add(transform(componentType, SimpleForm.of(Array.get(array, i))));
                }
                //noinspection unchecked
                return (T) list;
            }
        } else {
            if (wrapSingleElement) {
                List<Object> list = new ArrayList<>();
                list.add(simpleForm.getObject());
                //noinspection unchecked
                return (T) list;
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
