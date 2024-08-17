package dev.anhcraft.config;

import dev.anhcraft.config.adapter.AdapterProvider;
import dev.anhcraft.config.adapter.CacheableAdapterProvider;
import dev.anhcraft.config.adapter.SimpleAdapterProvider;
import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.adapter.defaults.*;
import dev.anhcraft.config.blueprint.ClassSchema;
import dev.anhcraft.config.blueprint.ReflectSchemaScanner;
import dev.anhcraft.config.blueprint.Schema;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.context.ContextProvider;
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
  private final InstanceFactory instanceFactory;

  ConfigFactory(Builder builder) {
    this.schemaScanner = new ReflectSchemaScanner(builder.namingPolicy, builder.validationRegistry);
    this.normalizer = new ConfigNormalizer(this, builder.normalizerSettings);
    this.denormalizer = new ConfigDenormalizer(this, builder.denormalizerSettings);
    this.classSchemas = builder.schemaCacheProvider.get();
    this.contextProvider = builder.contextProvider;
    this.instanceFactory = new InstanceFactory(builder.instanceAssemblers);
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
   * Gets the instance factory.
   * @return the instance factory
   */
  @NotNull public InstanceFactory getInstanceFactory() {
    return instanceFactory;
  }

  /**
   * A builder for {@link ConfigFactory}.<br>
   * Default settings:
   * <ul>
   *   <li>Type adapters: Java primitives and wrappers of primitives, String, Dictionary, Iterable, Map, Enum, UUID, URI, URL</li>
   *   <li>Default naming policy</li>
   *   <li>{@link CacheableAdapterProvider}</li>
   *   <li>Normalizer settings: {@link SettingFlag.Normalizer#IGNORE_DEFAULT_VALUES}</li>
   * </ul>
   */
  public static class Builder {
    private final LinkedHashMap<Class<?>, TypeAdapter<?>> typeAdapters = new LinkedHashMap<>();
    private final LinkedHashMap<Class<?>, InstanceAssembler> instanceAssemblers =
        new LinkedHashMap<>();
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
    private Class<? extends AdapterProvider> adapterProvider = CacheableAdapterProvider.class;
    private Set<SettingFlag.Normalizer> normalizerSettings =
        EnumSet.of(SettingFlag.Normalizer.IGNORE_DEFAULT_VALUES);
    private Set<SettingFlag.Denormalizer> denormalizerSettings =
        EnumSet.noneOf(SettingFlag.Denormalizer.class);

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
     * Registers the instance assembler for the given type.
     * @param type the type
     * @param instanceAssembler the instance assembler
     * @return this
     * @param <T> the type
     */
    public @NotNull <T> Builder useInstanceAssembler(
        @NotNull Class<T> type, @NotNull InstanceAssembler instanceAssembler) {
      instanceAssemblers.put(type, instanceAssembler);
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
     * Enables setting flags for the normalizer.
     * @param flags a list of setting flags
     * @return this
     */
    public @NotNull Builder enableNormalizerSetting(@NotNull SettingFlag.Normalizer... flags) {
      if (flags.length == 0) return this;
      if (flags.length == 1) normalizerSettings.add(flags[0]);
      else normalizerSettings.addAll(Arrays.asList(flags));
      return this;
    }

    /**
     * Enables setting flags for the denormalizer.
     * @param flags a list of setting flags
     * @return this
     */
    public @NotNull Builder enableDenormalizerSetting(@NotNull SettingFlag.Denormalizer... flags) {
      if (flags.length == 0) return this;
      if (flags.length == 1) denormalizerSettings.add(flags[0]);
      else denormalizerSettings.addAll(Arrays.asList(flags));
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
