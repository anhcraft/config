package dev.anhcraft.config;

import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.adapter.defaults.*;
import dev.anhcraft.config.blueprint.ReflectBlueprintScanner;
import dev.anhcraft.config.blueprint.Schema;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.validate.ValidationRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class ConfigFactory {
    private final Map<Class<?>, TypeAdapter<?>> typeAdapters;
    private final ReflectBlueprintScanner blueprintScanner;
    private final ConfigNormalizer normalizer;
    private final ConfigDenormalizer denormalizer;
    private final Map<Class<?>, Schema> schemas;
    private final Function<ConfigFactory, Context> contextProvider;

    ConfigFactory(Builder builder) {
        this.typeAdapters = Map.copyOf(builder.typeAdapters);
        this.blueprintScanner = new ReflectBlueprintScanner(builder.namingStrategy, builder.validationRegistry);
        this.normalizer = new ConfigNormalizer(this, builder.normalizerSettings);
        this.denormalizer = new ConfigDenormalizer(this, builder.denormalizerSettings);
        this.schemas = new LinkedHashMap<>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry eldest){
                return size() > builder.schemaCacheCapacity;
            }
        };
        this.contextProvider = builder.contextProvider;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> TypeAdapter<T> getTypeAdapter(@NotNull Class<T> type) {
        // TODO cache the type adapter
        Class<?> clazz = ComplexTypes.wrapPrimitive(type);
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
    public Context createContext() {
        return contextProvider.apply(this);
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
        private Function<ConfigFactory, Context> contextProvider = Context::new;
        private int schemaCacheCapacity = 100;
        private byte normalizerSettings = SettingFlag.Normalizer.IGNORE_DEFAULT_VALUES;
        private byte denormalizerSettings = 0;

        public Builder() {
            typeAdapters.put(Byte.class, new ByteAdapter());
            typeAdapters.put(Short.class, new ShortAdapter());
            typeAdapters.put(Integer.class, new IntegerAdapter());
            typeAdapters.put(Long.class, new LongAdapter());
            typeAdapters.put(Float.class, new FloatAdapter());
            typeAdapters.put(Double.class, new DoubleAdapter());
            typeAdapters.put(Character.class, new CharacterAdapter());
            typeAdapters.put(Boolean.class, new BooleanAdapter());
            typeAdapters.put(String.class, new StringAdapter());
            typeAdapters.put(Dictionary.class, new DictionaryAdapter());
            typeAdapters.put(Iterable.class, new IterableAdapter());
            typeAdapters.put(Map.class, new MapAdapter());
            typeAdapters.put(Enum.class, new EnumAdapter());
            typeAdapters.put(UUID.class, new UuidAdapter());
            typeAdapters.put(URL.class, new UrlAdapter());
            typeAdapters.put(URI.class, new UriAdapter());
        }

        public @NotNull <T> Builder adaptType(@NotNull Class<T> type, @NotNull TypeAdapter<T> adapter) {
            if (TypeAdapter.class.isAssignableFrom(type))
                throw new IllegalArgumentException("Wrong type?");
            typeAdapters.put(ComplexTypes.wrapPrimitive(type), adapter);
            return this;
        }

        public @NotNull Builder useValidationRegistry(@NotNull ValidationRegistry validation) {
            validationRegistry = validation;
            return this;
        }

        public @NotNull Builder useNamingStrategy(@NotNull UnaryOperator<String> function) {
            namingStrategy = function;
            return this;
        }

        public @NotNull Builder provideContext(@NotNull Function<ConfigFactory, Context> provider) {
            contextProvider = provider;
            return this;
        }

        public @NotNull Builder setSchemaCacheCapacity(int capacity) {
            schemaCacheCapacity = capacity;
            return this;
        }

        /**
         * Ignores the default values when normalizing into a {@link Dictionary} ({@code 0} and {@code false})<br>
         * By default, sets to {@code true}
         * @param ignore if the default values should be ignored
         * @return this
         */
        public @NotNull Builder ignoreDefaultValues(boolean ignore) {
            normalizerSettings = SettingFlag.set(normalizerSettings, SettingFlag.Normalizer.IGNORE_DEFAULT_VALUES, ignore);
            return this;
        }

        public @NotNull Builder ignoreEmptyArray(boolean ignore) {
            normalizerSettings = SettingFlag.set(normalizerSettings, SettingFlag.Normalizer.IGNORE_EMPTY_ARRAY, ignore);
            return this;
        }

        public @NotNull Builder ignoreEmptyDictionary(boolean ignore) {
            normalizerSettings = SettingFlag.set(normalizerSettings, SettingFlag.Normalizer.IGNORE_EMPTY_DICTIONARY, ignore);
            return this;
        }

        /**
         * In normalization and denormalization, deep clones simple values. This applies to arrays, dictionaries and
         * theirs nested children.<br>
         * By defaults, sets to {@code false} to improve performance.
         * @param deepClone if simple values should be deep cloned
         * @return this
         */
        public @NotNull Builder deepClone(boolean deepClone) {
            normalizerSettings = SettingFlag.set(normalizerSettings, SettingFlag.Normalizer.DEEP_CLONE, deepClone);
            denormalizerSettings = SettingFlag.set(denormalizerSettings, SettingFlag.Denormalizer.DEEP_CLONE, deepClone);
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
