package dev.anhcraft.config.internal;

import dev.anhcraft.config.*;
import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.adapter.TypeAnnotator;
import dev.anhcraft.config.blueprint.ClassProperty;
import dev.anhcraft.config.blueprint.ClassSchema;
import dev.anhcraft.config.blueprint.Processor;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.context.ElementScope;
import dev.anhcraft.config.context.PropertyScope;
import dev.anhcraft.config.context.ValueScope;
import dev.anhcraft.config.error.IllegalTypeException;
import dev.anhcraft.config.error.NormalizationException;
import dev.anhcraft.config.meta.Normalizer;
import dev.anhcraft.config.type.ComplexTypes;
import dev.anhcraft.config.type.SimpleTypes;
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Set;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class ConfigNormalizerImpl implements ConfigNormalizer {
  private final ConfigFactory configFactory;
  private final Set<SettingFlag.Normalizer> settings;

  public ConfigNormalizerImpl(ConfigFactory configFactory, Set<SettingFlag.Normalizer> settings) {
    this.configFactory = configFactory;
    this.settings = Collections.unmodifiableSet(settings);
  }

  private Context createContext() {
    return configFactory.getContextProvider().provideNormalizationContext(configFactory);
  }

  @Override
  public @NotNull Set<SettingFlag.Normalizer> getSettings() {
    return settings;
  }

  @Override
  public <T> @Nullable Object normalize(@NotNull T complex) {
    return normalize(createContext(), complex);
  }

  @Override
  public <T> @Nullable Object normalize(@NotNull Context ctx, @NotNull T complex) {
    //noinspection unchecked
    return normalize(ctx, (Class<T>) complex.getClass(), complex);
  }

  @Override
  public <S, T extends S> @Nullable Object normalize(
      @NotNull Context ctx, @NotNull Class<S> type, @NotNull T complex) {
    validateType(ctx, type, complex);
    return _normalize(ctx, type, complex);
  }

  @Override
  public <T> void normalizeToDictionary(@NotNull T complex, @NotNull Dictionary dictionary) {
    //noinspection unchecked
    normalizeToDictionary(createContext(), (Class<T>) complex.getClass(), complex, dictionary);
  }

  @Override
  public <T> void normalizeToDictionary(
      @NotNull Context ctx, @NotNull T complex, @NotNull Dictionary dictionary) {
    //noinspection unchecked
    normalizeToDictionary(ctx, (Class<T>) complex.getClass(), complex, dictionary);
  }

  @Override
  public <S, T extends S> void normalizeToDictionary(
      @NotNull Context ctx,
      @NotNull Class<S> type,
      @NotNull T complex,
      @NotNull Dictionary dictionary) {
    validateType(ctx, type, complex);
    _dynamicNormalize(ctx, type, complex, dictionary);
  }

  // ======== Internal implementations ========

  private <S, T extends S> void validateType(Context ctx, Class<S> type, T complex) {
    if (!type.isAssignableFrom(complex.getClass()))
      throw new IllegalTypeException(
          ctx,
          String.format(
              "%s is not assignable from %s", type.getName(), complex.getClass().getName()));
  }

  @SuppressWarnings({"rawtypes", "unchecked"}) // generic sucks
  private Object _normalize(Context ctx, Class<?> type, Object complex) {
    if (SimpleTypes.test(complex)) {
      if (settings.contains(SettingFlag.Normalizer.DEEP_CLONE))
        return SimpleTypes.deepClone(complex);
      return complex;
    }
    if (type.isArray()) {
      return _normalizeArray(ctx, complex);
    }
    TypeAdapter adapter = configFactory.getTypeAdapter(type);
    if (adapter != null && !(adapter instanceof TypeAnnotator)) {
      Object result = adapter.simplify(ctx, type, complex);
      if (!SimpleTypes.test(result)) {
        String msg =
            String.format("Adapter returned invalid simple type '%s'", result.getClass().getName());
        throw new IllegalTypeException(ctx, msg);
      }
      return result;
    }
    Dictionary container = new SchemalessDictionary();
    _dynamicNormalize(ctx, type, complex, container);
    return container;
  }

  private Object _normalizeArray(Context ctx, Object complex) {
    int n = Array.getLength(complex);
    Object[] result = new Object[n];
    for (int i = 0; i < n; i++) {
      ctx.enterScope(new ElementScope(i));
      {
        Object elem = Array.get(complex, i);
        Class<?> clazz = elem == null ? Object.class : elem.getClass();
        Object value = _normalize(ctx, clazz, elem);
        ctx.enterScope(new ValueScope(value));
        result[i] = value;
        ctx.exitScope();
      }
      ctx.exitScope();
    }
    return result;
  }

  private void _dynamicNormalize(Context ctx, Class<?> type, Object complex, Dictionary container) {
    if (complex instanceof Dictionary) {
      if (settings.contains(SettingFlag.Normalizer.DEEP_CLONE)) { // TODO reduce allocations
        container.putAll(SimpleTypes.deepClone((Dictionary) complex));
      } else {
        container.putAll((Dictionary) complex);
      }
      return;
    }

    ClassSchema schema = ctx.getFactory().getSchema(type);
    for (ClassProperty property : schema.properties()) {
      if (property.isTransient()) continue;

      ctx.enterScope(new PropertyScope(property, property.name(), container));
      scope:
      {
        Object value;

        Processor processor = property.normalizer();
        if (processor != null && processor.strategy() == Normalizer.Strategy.REPLACE) {
          value = ((Processor.NormalizationInvoker) processor.invoker()).invoke(ctx, complex);
          if (!SimpleTypes.test(value)) {
            String msg =
                String.format(
                    "Processor returned invalid simple type '%s'", value.getClass().getName());
            throw new IllegalTypeException(ctx, msg);
          }
        } else {
          if (processor != null && processor.strategy() == Normalizer.Strategy.BEFORE)
            value = ((Processor.NormalizationInvoker) processor.invoker()).invoke(ctx, complex);
          else {
            try {
              value = property.field().get(complex);
            } catch (IllegalAccessException e) {
              throw new NormalizationException(
                  ctx,
                  "Cannot access field "
                      + property.field().getName()
                      + " (representing property "
                      + property.name()
                      + ") declared in "
                      + property.field().getDeclaringClass().getName(),
                  e);
            }
          }

          if (value != null) value = _normalize(ctx, value.getClass(), value);
        }

        if (settings.contains(SettingFlag.Normalizer.IGNORE_DEFAULT_VALUES)
            && value instanceof Number
            && Math.abs(((Number) value).floatValue()) < 1e-8) break scope;
        if (settings.contains(SettingFlag.Normalizer.IGNORE_DEFAULT_VALUES)
            && value instanceof Boolean
            && !((Boolean) value)) break scope;
        if (settings.contains(SettingFlag.Normalizer.IGNORE_EMPTY_ARRAY)
            && ComplexTypes.isArray(value)
            && Array.getLength(value) == 0) break scope;
        if (settings.contains(SettingFlag.Normalizer.IGNORE_EMPTY_DICTIONARY)
            && value instanceof Dictionary
            && ((Dictionary) value).isEmpty()) break scope;

        ctx.enterScope(new ValueScope(value));
        container.put(property.name(), value);
        ctx.exitScope();
      }
      ctx.exitScope();
    }
  }
}
