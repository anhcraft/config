package dev.anhcraft.config;

import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.adapter.defaults.*;
import dev.anhcraft.config.blueprint.BlueprintScanner;
import dev.anhcraft.config.blueprint.Schema;
import dev.anhcraft.config.validate.ValidationRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.UnaryOperator;

public class ConfigFactory {
    final Map<Class<?>, TypeAdapter<?>> typeAdapters;
    private final ValidationRegistry validationRegistry;
    private final UnaryOperator<String> namingStrategy;
    private final BlueprintScanner blueprintScanner;
    private final ConfigNormalizer normalizer;
    private final ConfigDenormalizer denormalizer;
    private final Map<Class<?>, Schema> schemas;

    ConfigFactory(Builder builder) {
        this.typeAdapters = Map.copyOf(builder.typeAdapters);
        this.validationRegistry = builder.validationRegistry;
        this.namingStrategy = builder.namingStrategy;
        this.blueprintScanner = new BlueprintScanner(namingStrategy, validationRegistry);
        this.normalizer = new ConfigNormalizer(this);
        this.denormalizer = new ConfigDenormalizer(this);
        this.schemas = new LinkedHashMap<>() {
            protected boolean removeEldestEntry(Map.Entry eldest){
                return size() > builder.schemaCacheCapacity;
            }
        };
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

        public Builder() {
            typeAdapters.put(UUID.class, new UuidAdapter());
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
