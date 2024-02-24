package dev.anhcraft.config;

import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.adapter.TypeInferencer;
import dev.anhcraft.config.blueprint.Property;
import dev.anhcraft.config.blueprint.Schema;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.context.ElementScope;
import dev.anhcraft.config.context.PropertyScope;
import dev.anhcraft.config.error.IllegalTypeException;
import dev.anhcraft.config.error.InvalidValueException;
import dev.anhcraft.config.type.ComplexTypes;
import dev.anhcraft.config.type.SimpleTypes;
import dev.anhcraft.config.type.TypeResolver;
import dev.anhcraft.config.util.ObjectUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Map;

public class ConfigDenormalizer {
    private final ConfigFactory configFactory;
    private final byte settings;

    public ConfigDenormalizer(ConfigFactory configFactory, byte settings) {
        this.configFactory = configFactory;
        this.settings = settings;
    }

    public byte getSettings() {
        return settings;
    }

    public <T> @Nullable Object denormalize(@Nullable T simple, @NotNull Type targetType) throws Exception {
        return denormalize(configFactory.createContext(), simple, targetType);
    }

    public <T> @Nullable Object denormalize(@NotNull Context ctx, @Nullable T simple, @NotNull Type targetType) throws Exception {
        validateSimpleType(ctx, simple);
        return _denormalize(ctx, simple, targetType);
    }

    public <T> void denormalizeToInstance(@NotNull T simple, @NotNull Type targetType, @NotNull Object instance) throws Exception {
        denormalizeToInstance(configFactory.createContext(), simple, targetType, instance);
    }

    public <T> void denormalizeToInstance(@NotNull Context ctx, @NotNull T simple, @NotNull Type targetType, @NotNull Object instance) throws Exception {
        validateSimpleType(ctx, simple);
        validateComplexType(ctx, instance, targetType);
        _denormalizeToInstance(ctx, simple, targetType, instance);
    }

    private <T> void validateSimpleType(Context ctx, T simple) {
        if (!SimpleTypes.validate(simple))
            throw new IllegalTypeException(ctx, "Supplied argument is not a simple object");
    }

    private void validateComplexType(Context ctx, @NotNull Object instance, @NotNull Type targetType) {
        try {
            Class<?> erasureType = ComplexTypes.erasure(targetType);
            if (!erasureType.isAssignableFrom(instance.getClass()))
                throw new IllegalTypeException(ctx, "Supplied instance is not compatible to " + erasureType.getName());
        } catch (ClassNotFoundException e) {
            throw new IllegalTypeException(ctx, "Cannot perform type check");
        }
    }

    @SuppressWarnings("rawtypes")
    private <T> Object _denormalize(Context ctx, @Nullable T simple, @NotNull Type targetType) throws Exception {
        if (simple == null)
            return null;
        if (ComplexTypes.isArray(targetType))
            return _denormalizeToArray(ctx, targetType, simple);
        Class<?> erasureType = ComplexTypes.erasure(targetType);
        TypeAdapter adapter = configFactory.getTypeAdapter(erasureType);
        if (adapter != null && !(adapter instanceof TypeInferencer)) {
            return adapter.complexify(ctx, simple, targetType);
        }
        if (!(simple instanceof Dictionary))
            return null;
        Object object = ObjectUtil.newInstance(erasureType);
        _denormalizeToInstance(ctx, simple, targetType, object);
        return object;
    }

    private <T> Object _denormalizeToArray(Context ctx, Type targetType, T simple) throws Exception {
        Type elemType = ComplexTypes.getComponentType(targetType);
        if (elemType == null) return null;
        Class<?> erasureElemType = ComplexTypes.erasure(elemType);
        int len = SimpleTypes.getContainerSize(simple);
        Object object = Array.newInstance(erasureElemType, len);
        for (int i = 0; i < len; i++) {
            ctx.enterScope(new ElementScope(i));
            {
                Object elem = _denormalize(ctx, SimpleTypes.getContainerElement(simple, i), elemType);
                Array.set(object, i, elem);
            }
            ctx.exitScope();
        }
        return object;
    }

    private <T> void _denormalizeToInstance(Context ctx, T simple, Type targetType, Object instance) throws Exception {
        if (!(simple instanceof Dictionary)) return;

        TypeResolver resolver = TypeResolver.of(targetType);
        Dictionary dict = (Dictionary) simple;
        Schema schema = configFactory.getSchema(ComplexTypes.erasure(targetType));

        for (Property property : schema.properties()) {
            if (property.isConstant())
                continue;

            Map.Entry<String, Object> entry = dict.search(property.name(), property.aliases());
            Object value = entry == null ? null : entry.getValue();

            ctx.enterScope(new PropertyScope(property, entry == null ? "" : entry.getKey()));
            scope:
            {
                if (value != null) {
                    Type solvedType = resolver.resolve(property.type());
                    value = _denormalize(ctx, value, solvedType);
                }

                if (property.isOptional() && value == null)
                    break scope;

                Class<?> propertyTypeErasure = ComplexTypes.erasure(property.type());

                if (value == null && propertyTypeErasure.isPrimitive())
                    break scope;

                if (value != null && propertyTypeErasure.isAssignableFrom(value.getClass()))
                    break scope;

                if (!property.validator().check(value)) {
                    if (property.validator().silent())
                        break scope;
                    throw new InvalidValueException(ctx, property.validator().message());
                }

                property.field().set(instance, value);
            }
            ctx.exitScope();
        }
    }
}
