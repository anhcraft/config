package dev.anhcraft.config;

import dev.anhcraft.config.adapter.AdapterProvider;
import dev.anhcraft.config.adapter.SimpleAdapterProvider;
import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.adapter.defaults.*;
import dev.anhcraft.config.blueprint.ReflectBlueprintScanner;
import dev.anhcraft.config.blueprint.Schema;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.context.ContextProvider;
import dev.anhcraft.config.error.InvalidValueException;
import dev.anhcraft.config.validate.ValidationRegistry;
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
  private final ReflectBlueprintScanner blueprintScanner;
  private final ConfigNormalizer normalizer;
  private final ConfigDenormalizer denormalizer;
  private final Map<Class<?>, Schema> schemas;
  private final ContextProvider contextProvider;
  private final AdapterProvider adapterProvider;

  ConfigFactory(Builder builder) {
    this.blueprintScanner =
        new ReflectBlueprintScanner(builder.namingPolicy, builder.validationRegistry);
    this.normalizer = new ConfigNormalizer(this, builder.normalizerSettings);
    this.denormalizer = new ConfigDenormalizer(this, builder.denormalizerSettings);
    this.schemas = builder.schemaCacheProvider.get();
    this.contextProvider = builder.contextProvider;
    this.adapterProvider =
        builder.adapterProvider == null
            ? new SimpleAdapterProvider(builder.typeAdapters)
            : builder.adapterProvider;
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
   * Creates a new context.
   * @return a new context
   */
  @NotNull public Context createContext() {
    return contextProvider.apply(this);
  }

  /**
   * Gets the schema for the given type.<br>
   * The result will be cached for future calls.
   * @param type the type
   * @return the schema
   */
  @NotNull public Schema getSchema(@NotNull Class<?> type) {
    Schema schema = schemas.get(type);
    if (schema != null) return schema;
    schema = blueprintScanner.scanSchema(type);
    schemas.put(type, schema);
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

  public static class Builder {
    private final LinkedHashMap<Class<?>, TypeAdapter<?>> typeAdapters = new LinkedHashMap<>();
    private ValidationRegistry validationRegistry = ValidationRegistry.DEFAULT;
    private UnaryOperator<String> namingPolicy = NamingPolicy.DEFAULT;
    private ContextProvider contextProvider = Context::new;
    private Supplier<Map<Class<?>, Schema>> schemaCacheProvider =
        () ->
            new LinkedHashMap<>() {
              @Override
              protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > 100;
              }
            };
    private AdapterProvider adapterProvider = null;
    private byte normalizerSettings = SettingFlag.Normalizer.IGNORE_DEFAULT_VALUES;
    private byte denormalizerSettings = 0;

    public Builder() {
      // TODO cache, and move imports to local files
      typeAdapters.put(Byte.class, new ByteAdapter());
      typeAdapters.put(Short.class, new ShortAdapter());
      typeAdapters.put(Integer.class, new IntegerAdapter());
      typeAdapters.put(Long.class, new LongAdapter());
      typeAdapters.put(Float.class, new FloatAdapter());
      typeAdapters.put(Double.class, new DoubleAdapter());
      typeAdapters.put(Character.class, new CharacterAdapter());
      typeAdapters.put(Boolean.class, new BooleanAdapter());

      typeAdapters.put(byte.class, new ByteAdapter());
      typeAdapters.put(short.class, new ShortAdapter());
      typeAdapters.put(int.class, new IntegerAdapter());
      typeAdapters.put(long.class, new LongAdapter());
      typeAdapters.put(float.class, new FloatAdapter());
      typeAdapters.put(double.class, new DoubleAdapter());
      typeAdapters.put(char.class, new CharacterAdapter());
      typeAdapters.put(boolean.class, new BooleanAdapter());

      typeAdapters.put(String.class, new StringAdapter());
      typeAdapters.put(Dictionary.class, new DictionaryAdapter());
      typeAdapters.put(Iterable.class, new IterableAdapter());
      typeAdapters.put(List.class, new IterableAdapter());
      typeAdapters.put(Set.class, new IterableAdapter());
      typeAdapters.put(Map.class, new MapAdapter());
      typeAdapters.put(Enum.class, new EnumAdapter());
      typeAdapters.put(UUID.class, new UuidAdapter());
      typeAdapters.put(URL.class, new UrlAdapter());
      typeAdapters.put(URI.class, new UriAdapter());
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
    public @NotNull Builder useAdapterProvider(@NotNull AdapterProvider provider) {
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
     * By default, the schema cache is a linked hash map with a capacity of 100 classes.<br>
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
  public interface SchemaCacheProvider extends Supplier<Map<Class<?>, Schema>> {}
}
