package dev.anhcraft.config;

import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.adapter.defaults.*;
import dev.anhcraft.config.blueprint.ReflectBlueprintScanner;
import dev.anhcraft.config.blueprint.Schema;
import dev.anhcraft.config.validate.ValidationRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.UnaryOperator;

public class ConfigFactory {
    private final Map<Class<?>, TypeAdapter<?>> typeAdapters;
    private final ReflectBlueprintScanner blueprintScanner;
    private final ConfigNormalizer normalizer;
    private final ConfigDenormalizer denormalizer;
    private final Map<Class<?>, Schema> schemas;

    ConfigFactory(Builder builder) {
        this.typeAdapters = Map.copyOf(builder.typeAdapters);
        this.blueprintScanner = new ReflectBlueprintScanner(builder.namingStrategy, builder.validationRegistry);
        this.normalizer = new ConfigNormalizer(this, builder.contextDepthLimit, builder.normalizerSettings);
        this.denormalizer = new ConfigDenormalizer(this, builder.contextDepthLimit);
        this.schemas = new LinkedHashMap<>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry eldest){
                return size() > builder.schemaCacheCapacity;
            }
        };
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> TypeAdapter<T> getTypeAdapter(@NotNull Class<T> type) {
        // TODO cache the type adapter
        Class<?> clazz = type;
        do {
            TypeAdapter<?> adapter = typeAdapters.get(clazz);
            if (adapter != null) {
                return (TypeAdapter<T>) adapter;
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null);
        return null;
    }

    @NotNull
    public Schema getSchema(@NotNull Class<?> type) {
        return schemas.computeIfAbsent(type, blueprintScanner::scanSchema);
    }

    @NotNull
    public ConfigNormalizer getNormalizer() {
        return normalizer;
    }

    @NotNull
    public ConfigDenormalizer getDenormalizer() {
        return denormalizer;
    }

    public static class Builder {
        private final Map<Class<?>, TypeAdapter<?>> typeAdapters = new HashMap<>();
        private ValidationRegistry validationRegistry = ValidationRegistry.DEFAULT;
        private UnaryOperator<String> namingStrategy = NamingStrategy.DEFAULT;
        private int schemaCacheCapacity = 100;
        private int contextDepthLimit = 20;
        private byte normalizerSettings = 1;

        public Builder() {
            typeAdapters.put(byte.class, new ByteAdapter());
            typeAdapters.put(Byte.class, new ByteAdapter());
            typeAdapters.put(short.class, new ShortAdapter());
            typeAdapters.put(Short.class, new ShortAdapter());
            typeAdapters.put(int.class, new IntegerAdapter());
            typeAdapters.put(Integer.class, new IntegerAdapter());
            typeAdapters.put(long.class, new LongAdapter());
            typeAdapters.put(Long.class, new LongAdapter());
            typeAdapters.put(float.class, new FloatAdapter());
            typeAdapters.put(Float.class, new FloatAdapter());
            typeAdapters.put(double.class, new DoubleAdapter());
            typeAdapters.put(Double.class, new DoubleAdapter());
            typeAdapters.put(String.class, new StringAdapter());
            typeAdapters.put(Iterable.class, new IterableAdapter());
            typeAdapters.put(UUID.class, new UuidAdapter());
            typeAdapters.put(URL.class, new UrlAdapter());
            typeAdapters.put(URI.class, new UriAdapter());
        }

        @NotNull
        public <T> Builder adaptType(@NotNull Class<T> type, @NotNull TypeAdapter<T> adapter) {
            typeAdapters.put(type, adapter);
            return this;
        }

        @NotNull
        public Builder useValidationRegistry(@NotNull ValidationRegistry validation) {
            validationRegistry = validation;
            return this;
        }

        @NotNull
        public Builder useNamingStrategy(@NotNull UnaryOperator<String> function) {
            namingStrategy = function;
            return this;
        }

        @NotNull
        public Builder setSchemaCacheCapacity(int capacity) {
            schemaCacheCapacity = capacity;
            return this;
        }

        /**
         * Sets the limit of the context depth.<br>
         * The context depth is the number of recursive calls to different type-adapting. The limit exists
         * to prevent unnecessary recursions which can lead to stack overflows and performance issues.<br>
         * The context depth is set to 20 by default.
         * @param contextDepthLimit the limit
         * @return this
         */
        @NotNull
        public Builder setContextDepthLimit(int contextDepthLimit) {
            this.contextDepthLimit = contextDepthLimit;
            return this;
        }

        /**
         * Ignores the default values when normalizing into a {@link Dictionary}.<br>
         * Includes: {@code 0} and {@code false}
         * @param ignore if the default values should be ignored
         * @return this
         */
        @NotNull
        public Builder ignoreDefaultValues(boolean ignore) {
            if (ignore) normalizerSettings |= 1;
            else normalizerSettings &= ~1;
            return this;
        }

        @NotNull
        public Builder ignoreEmptyArray(boolean ignore) {
            if (ignore) normalizerSettings |= 2;
            else normalizerSettings &= ~2;
            return this;
        }

        @NotNull
        public Builder ignoreEmptyDictionary(boolean ignore) {
            if (ignore) normalizerSettings |= 4;
            else normalizerSettings &= ~4;
            return this;
        }

        @NotNull
        public ConfigFactory build() {
            return new ConfigFactory(this);
        }
    }

    @NotNull
    public static Builder create() {
        return new Builder();
    }
}
