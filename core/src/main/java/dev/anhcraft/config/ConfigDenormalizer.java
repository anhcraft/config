package dev.anhcraft.config;

import dev.anhcraft.config.adapter.AdapterContext;
import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.blueprint.Property;
import dev.anhcraft.config.blueprint.Schema;
import dev.anhcraft.config.error.InvalidValueException;
import dev.anhcraft.config.util.ObjectUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Map;

public class ConfigDenormalizer {
    private final ConfigFactory configFactory;
    private final int contextDepthLimit;

    public ConfigDenormalizer(ConfigFactory configFactory, int contextDepthLimit) {
        this.configFactory = configFactory;
        this.contextDepthLimit = contextDepthLimit;
    }

    public <T> Object denormalize(@NotNull Type targetType, @NotNull T simple) throws Exception {
        return denormalize(new AdapterContext(configFactory, 0), targetType, simple);
    }

    public <T> Object denormalize(AdapterContext ctx, @NotNull Type targetType, @NotNull T simple) throws Exception {
        return _denormalize(ctx, targetType, simple);
    }

    public <T> Object denormalizeToInstance(@NotNull Object instance, @NotNull T simple) throws Exception {
        return denormalizeToInstance(new AdapterContext(configFactory, 0), instance, simple);
    }

    public <T> Object denormalizeToInstance(AdapterContext ctx, @NotNull Object instance, @NotNull T simple) throws Exception {
        return _denormalizeToInstance(ctx, instance, instance.getClass(), simple);
    }

    @SuppressWarnings("rawtypes")
    private <T> Object _denormalize(AdapterContext ctx, @NotNull Type targetType, @NotNull T simple) throws Exception {
        if (ComplexTypes.isArray(targetType))
            return _denormalizeToArray(ctx, targetType, simple); // flat operation
        Class<?> erasureType = ComplexTypes.erasure(targetType);
        TypeAdapter adapter = configFactory.getTypeAdapter(erasureType);
        if (adapter == null)
            return _denormalizeToInstance(ctx, ObjectUtil.newInstance(erasureType), erasureType, simple); // flat operation
        if (ctx.commitDepth() > contextDepthLimit) // prevent overflow due to bad type-adapting
            return null;
        Object v = adapter.complexify(ctx, targetType, simple);
        ctx.releaseDepth();
        return v;
    }

    private <T> Object _denormalizeToArray(AdapterContext ctx, Type targetType, T simple) throws Exception {
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

    private <T> Object _denormalizeToInstance(AdapterContext ctx, Object instance, Class<?> targetType, T simple) throws Exception {
        if (!(simple instanceof Wrapper)) return null;
        Wrapper wrapper = (Wrapper) simple;
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
            if (!property.validator().check(value)) {
                String key = entry == null ? property.name() : entry.getKey();
                throw new InvalidValueException(key, property.validator().message());
            }
            property.field().set(instance, value);
        }
        return instance;
    }
}
