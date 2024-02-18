package dev.anhcraft.config;

import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.struct.Schema;

import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

public class ConfigFactory {
    private final Map<Class<?>, TypeAdapter<?>> typeAdapters;
    private final UnaryOperator<String> namingStrategy;
    private final Map<Class<?>, Schema> schemas = new HashMap<>();

    ConfigFactory(Builder builder) {
        this.typeAdapters = builder.typeAdapters;
        this.namingStrategy = builder.namingStrategy;
    }

    public Schema getSchema(Class<?> type) {
        if (!schemas.containsKey(type)) {
            schemas.put(type, Schema.from(type, typeAdapters));
        }
        return schemas.get(type);
    }

    public static class Builder {
        private Map<Class<?>, TypeAdapter<?>> typeAdapters = new HashMap<>();
        private UnaryOperator<String> namingStrategy = NamingStrategy.DEFAULT;

        public <T> Builder adaptType(Class<T> type, TypeAdapter<T> adapter) {
            typeAdapters.put(type, adapter);
            return this;
        }

        public Builder useNamingStrategy(UnaryOperator<String> strategy) {
            namingStrategy = strategy;
            return this;
        }

        public ConfigFactory build() {
            return new ConfigFactory(this);
        }
    }
}
