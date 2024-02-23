package dev.anhcraft.config;

import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.blueprint.Property;
import dev.anhcraft.config.blueprint.Schema;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.error.InvalidValueException;
import dev.anhcraft.config.util.ObjectUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Map;

public class ConfigDenormalizer {
    private final ConfigFactory configFactory;

    public ConfigDenormalizer(ConfigFactory configFactory) {
        this.configFactory = configFactory;
    }

    public <T> @Nullable Object denormalize(@Nullable T simple, @NotNull Type targetType) throws Exception {
        return denormalize(configFactory.createContext(), simple, targetType);
    }

    public <T> @Nullable Object denormalize(@NotNull Context ctx, @Nullable T simple, @NotNull Type targetType) throws Exception {
        return _denormalize(ctx, targetType, simple);
    }

    public <T> void denormalizeToInstance(@NotNull T simple, @NotNull Object instance) throws Exception {
        denormalizeToInstance(configFactory.createContext(), instance, simple);
    }

    public <T> void denormalizeToInstance(@NotNull Context ctx, @NotNull T simple, @NotNull Object instance) throws Exception {
        _denormalizeToInstance(ctx, instance, instance.getClass(), simple);
    }

    @SuppressWarnings("rawtypes")
    private <T> Object _denormalize(Context ctx, @NotNull Type targetType, @Nullable T simple) throws Exception {
        if (simple == null)
            return null;
        if (ComplexTypes.isArray(targetType))
            return _denormalizeToArray(ctx, targetType, simple); // flat operation
        Class<?> erasureType = ComplexTypes.erasure(targetType);
        TypeAdapter adapter = configFactory.getTypeAdapter(erasureType);
        if (adapter == null) {
            Object object = ObjectUtil.newInstance(erasureType);
            _denormalizeToInstance(ctx, object, erasureType, simple); // flat operation
            return object;
        } else {
            Object v = adapter.complexify(ctx, simple, targetType);
            return v;
        }
    }

    private <T> Object _denormalizeToArray(Context ctx, Type targetType, T simple) throws Exception {
        Type elemType = ComplexTypes.getComponentType(targetType);
        if (elemType == null) return null;
        Class<?> erasureElemType = ComplexTypes.erasure(elemType);
        int len = SimpleTypes.getContainerSize(simple);
        Object object = Array.newInstance(erasureElemType, len);
        for (int i = 0; i < len; i++) {
            Object elem = _denormalize(ctx, elemType, SimpleTypes.getContainerElement(simple, i));
            Array.set(object, i, elem);
        }
        return object;
    }

    private <T> void _denormalizeToInstance(Context ctx, Object instance, Class<?> targetType, T simple) throws Exception {
        if (!(simple instanceof Dictionary)) return;
        Dictionary wrapper = (Dictionary) simple;
        Schema schema = configFactory.getSchema(targetType);
        for (Property property : schema.properties()) {
            if (property.isConstant())
                continue;
            Map.Entry<String, Object> entry = wrapper.tryGet(property.name(), property.aliases());
            Object value = entry == null ? null : entry.getValue();
            if (property.isOptional() && value == null)
                continue;
            if (value != null)
                value = _denormalize(ctx, property.type(), value);
            if (!property.validator().check(value))
                throw new InvalidValueException(ctx, property.validator().message());
            if (ComplexTypes.erasure(property.type()).isPrimitive() && value == null)
                continue;
            property.field().set(instance, value);
        }
    }
}
