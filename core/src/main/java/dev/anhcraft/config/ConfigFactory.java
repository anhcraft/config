package dev.anhcraft.config;

import dev.anhcraft.config.adapter.AdapterProvider;
import dev.anhcraft.config.adapter.SimpleAdapterProvider;
import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.adapter.defaults.*;
import dev.anhcraft.config.blueprint.ClassSchema;
import dev.anhcraft.config.blueprint.ReflectSchemaScanner;
import dev.anhcraft.config.blueprint.Schema;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.context.ContextProvider;
import dev.anhcraft.config.error.InvalidValueException;
import dev.anhcraft.config.validate.ValidationRegistry;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The config factory centralizes facilities for normalization and denormalization.
 */
public final class ConfigFactory {
  private final ReflectSchemaScanner schemaScanner;
  private final ConfigNormalizer normalizer;
  private final ConfigDenormalizer denormalizer;
  private final Map<Class<?>, Schema<?>> classSchemas;
  private final ContextProvider contextProvider;
  private final AdapterProvider adapterProvider;

  ConfigFactory(Builder builder) {
    this.schemaScanner = new ReflectSchemaScanner(builder.namingPolicy, builder.validationRegistry);
    this.normalizer = new ConfigNormalizer(this, builder.normalizerSettings);
    this.denormalizer = new ConfigDenormalizer(this, builder.denormalizerSettings);
    this.classSchemas = builder.schemaCacheProvider.get();
    this.contextProvider = builder.contextProvider;
    try {
      this.adapterProvider =
          builder
              .adapterProvider
              .getDeclaredConstructor(LinkedHashMap.class)
              .newInstance(builder.typeAdapters);
    } catch (InstantiationException
        | IllegalAccessException
        | InvocationTargetException
        | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Gets the first type adapter which is compatible to the given type.<br>
   * A type adapter {@code T} is compatible to type {@code T} or subtype of {@code T}<br>
   * This method traverses from {@code T} up to {@code Object} including superclass and superinterfaces.
   * @param type the type
   * @return the type adapter or {@code null} if not found
   * @param <T> the type
   */
  public <T> @Nullable TypeAdapter<T> getTypeAdapter(@NotNull Class<T> type) {
    return adapterProvider.getTypeAdapter(type);
  }

  /**
   * Creates a new generic context.
   * @return a new context
   */
  @NotNull public Context createContext() {
    return contextProvider.provideGenericContext(this);
  }

  /**
   * Gets the schema for the given type.<br>
   * The result will be cached for future calls.<br>
   * This method is not thread-safe by default, unless the factory has a custom concurrent {@link SchemaCacheProvider}.
   * @param type the type
   * @return the schema
   */
  @NotNull public ClassSchema getSchema(@NotNull Class<?> type) {
    ClassSchema schema = (ClassSchema) classSchemas.get(type);
    if (schema != null) return schema;
    schema = schemaScanner.scanSchema(type);
    classSchemas.put(type, schema);
    return schema;
  }

  /**
   * Gets the normalizer.
   * @return the normalizer
   */
  @NotNull public ConfigNormalizer getNormalizer() {
    return normalizer;
  }

  /**
   * Gets the denormalizer.
   * @return the denormalizer
   */
  @NotNull public ConfigDenormalizer getDenormalizer() {
    return denormalizer;
  }

  /**
   * Gets the context provider.
   * @return the context provider
   */
  @NotNull public ContextProvider getContextProvider() {
    return contextProvider;
  }

  /**
   * A builder for {@link ConfigFactory}
   */
  public static class Builder {
    private final LinkedHashMap<Class<?>, TypeAdapter<?>> typeAdapters = new LinkedHashMap<>();
    private ValidationRegistry validationRegistry = ValidationRegistry.DEFAULT;
    private UnaryOperator<String> namingPolicy = NamingPolicy.DEFAULT;
    private ContextProvider contextProvider = new ContextProvider() {};
    private Supplier<Map<Class<?>, Schema<?>>> schemaCacheProvider =
        () ->
            new LinkedHashMap<>() {
              @Override
              protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > 100;
              }
            };
    private Class<? extends AdapterProvider> adapterProvider = SimpleAdapterProvider.class;
    private byte normalizerSettings = SettingFlag.Normalizer.IGNORE_DEFAULT_VALUES;
    private byte denormalizerSettings = 0;

    public Builder() {
      typeAdapters.put(Byte.class, ByteAdapter.INSTANCE);
      typeAdapters.put(Short.class, ShortAdapter.INSTANCE);
      typeAdapters.put(Integer.class, IntegerAdapter.INSTANCE);
      typeAdapters.put(Long.class, LongAdapter.INSTANCE);
      typeAdapters.put(Float.class, FloatAdapter.INSTANCE);
      typeAdapters.put(Double.class, DoubleAdapter.INSTANCE);
      typeAdapters.put(Character.class, CharacterAdapter.INSTANCE);
      typeAdapters.put(Boolean.class, BooleanAdapter.INSTANCE);

      typeAdapters.put(byte.class, ByteAdapter.INSTANCE);
      typeAdapters.put(short.class, ShortAdapter.INSTANCE);
      typeAdapters.put(int.class, IntegerAdapter.INSTANCE);
      typeAdapters.put(long.class, LongAdapter.INSTANCE);
      typeAdapters.put(float.class, FloatAdapter.INSTANCE);
      typeAdapters.put(double.class, DoubleAdapter.INSTANCE);
      typeAdapters.put(char.class, CharacterAdapter.INSTANCE);
      typeAdapters.put(boolean.class, BooleanAdapter.INSTANCE);

      typeAdapters.put(String.class, StringAdapter.INSTANCE);
      typeAdapters.put(Dictionary.class, DictionaryAdapter.INSTANCE);
      typeAdapters.put(Iterable.class, IterableAdapter.INSTANCE);
      typeAdapters.put(List.class, IterableAdapter.INSTANCE);
      typeAdapters.put(Set.class, IterableAdapter.INSTANCE);
      typeAdapters.put(Map.class, MapAdapter.INSTANCE);
      typeAdapters.put(Enum.class, EnumAdapter.INSTANCE);
      typeAdapters.put(UUID.class, UuidAdapter.INSTANCE);
      typeAdapters.put(URL.class, UrlAdapter.INSTANCE);
      typeAdapters.put(URI.class, UriAdapter.INSTANCE);
    }

    /**
     * Registers a type adapter.<br>
     * A type adapter {@code T} is compatible to type {@code T} or subtype of {@code T}<br>
     * By default, built-in type adapters are implicitly registered
     * @param type the type
     * @param adapter the type adapter
     * @return this
     * @param <T> the type
     * @see ConfigFactory#getTypeAdapter(Class)
     */
    public @NotNull <T> Builder adaptType(@NotNull Class<T> type, @NotNull TypeAdapter<T> adapter) {
      if (TypeAdapter.class.isAssignableFrom(type))
        throw new IllegalArgumentException("Wrong type?");
      typeAdapters.put(type, adapter);
      return this;
    }

    /**
     * Uses the given type adapter provider.<br>
     * By default, uses {@link SimpleAdapterProvider}.
     * @param provider the type adapter provider
     * @return this
     */
    public @NotNull Builder useAdapterProvider(@NotNull Class<? extends AdapterProvider> provider) {
      adapterProvider = provider;
      return this;
    }

    /**
     * Uses the given validation registry.<br>
     * By default, uses {@link ValidationRegistry#DEFAULT}.
     * @param registry the validation registry
     * @return this
     */
    public @NotNull Builder useValidationRegistry(@NotNull ValidationRegistry registry) {
      validationRegistry = registry;
      return this;
    }

    /**
     * Uses the given naming policy.<br>
     * By default, uses {@link NamingPolicy#DEFAULT}.
     * @param function the naming policy
     * @return this
     */
    public @NotNull Builder useNamingPolicy(@NotNull UnaryOperator<String> function) {
      namingPolicy = function;
      return this;
    }

    /**
     * Uses the given context provider.<br>
     * By default, uses the default context provider.
     * @param provider the context provider
     * @return this
     */
    public @NotNull Builder provideContext(@NotNull ContextProvider provider) {
      contextProvider = provider;
      return this;
    }

    /**
     * Sets the schema cache provider.<br>
     * By default, the schema cache is a {@link LinkedHashMap} with a capacity of 100 classes.<br>
     * It is possible to provide custom map implementation such as a map view from Guava Cache.
     * @param provider the schema cache
     * @return this
     */
    public @NotNull Builder provideSchemaCache(@NotNull SchemaCacheProvider provider) {
      schemaCacheProvider = provider;
      return this;
    }

    /**
     * Ignores setting default values when normalizing an instance into a {@link Dictionary}<br>
     * The default value including number and boolean.<br>
     * By default, sets to {@code true}
     * @param ignore if the default values should be ignored
     * @return this
     */
    public @NotNull Builder ignoreDefaultValues(boolean ignore) {
      normalizerSettings =
          SettingFlag.set(normalizerSettings, SettingFlag.Normalizer.IGNORE_DEFAULT_VALUES, ignore);
      return this;
    }

    /**
     * Ignores setting empty array when normalizing an instance into a {@link Dictionary}<br>
     * By default, sets to {@code false}
     * @param ignore if empty array should be ignored
     * @return this
     */
    public @NotNull Builder ignoreEmptyArray(boolean ignore) {
      normalizerSettings =
          SettingFlag.set(normalizerSettings, SettingFlag.Normalizer.IGNORE_EMPTY_ARRAY, ignore);
      return this;
    }

    /**
     * Ignores setting empty dictionary when normalizing an instance into a {@link Dictionary}<br>
     * By default, sets to {@code false}
     * @param ignore if empty dictionary should be ignored
     * @return this
     */
    public @NotNull Builder ignoreEmptyDictionary(boolean ignore) {
      normalizerSettings =
          SettingFlag.set(
              normalizerSettings, SettingFlag.Normalizer.IGNORE_EMPTY_DICTIONARY, ignore);
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
      normalizerSettings =
          SettingFlag.set(normalizerSettings, SettingFlag.Normalizer.DEEP_CLONE, deepClone);
      denormalizerSettings =
          SettingFlag.set(denormalizerSettings, SettingFlag.Denormalizer.DEEP_CLONE, deepClone);
      return this;
    }

    /**
     * When parsing a number, strictly checks the number range. For example:
     * <ul>
     *     <li>Without this flag: {@code adaptByte("255.001") == -1}</li>
     *     <li>With this flag: {@code adaptByte("255.001")} throws {@link InvalidValueException}</li>
     * </ul>
     * When this flag is off, the denormalizer parses the string as {@link Double} first, and then casts it to
     * the desired number type. When it is on, the denormalizer parses the string using the "parse" method of
     * the desired number type, e.g: {@link Integer#parseInt(String)} for the integer adapter.<br>
     * Note: the string is always trimmed before parsing no matter this flag is on or off.<br>
     * This setting is {@code false} by default to enhance user convenience.
     * @param strict should strictly parse numbers
     * @return this
     */
    public @NotNull Builder strictNumberParsing(boolean strict) {
      denormalizerSettings =
          SettingFlag.set(
              denormalizerSettings, SettingFlag.Denormalizer.STRICT_NUMBER_PARSING, strict);
      return this;
    }

    /**
     * Disables validation in denormalization.<br>
     * This setting is {@code false} by default.
     * @param disabled if validation should be disabled
     * @return this
     */
    public @NotNull Builder disableValidation(boolean disabled) {
      denormalizerSettings =
          SettingFlag.set(
              denormalizerSettings, SettingFlag.Denormalizer.DISABLE_VALIDATION, disabled);
      return this;
    }

    /**
     * Builds the config factory.
     * @return the config factory
     */
    @NotNull public ConfigFactory build() {
      return new ConfigFactory(this);
    }
  }

  /**
   * Creates a builder for {@link ConfigFactory}.
   * @return the builder
   */
  @NotNull public static Builder create() {
    return new Builder();
  }

  /**
   * Returns the schema cache.
   */
  public interface SchemaCacheProvider extends Supplier<Map<Class<?>, Schema<?>>> {}
}
