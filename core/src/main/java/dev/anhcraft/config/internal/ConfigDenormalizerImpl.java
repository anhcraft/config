package dev.anhcraft.config.internal;

import dev.anhcraft.config.*;
import dev.anhcraft.config.Dictionary;
import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.adapter.TypeInferencer;
import dev.anhcraft.config.blueprint.ClassProperty;
import dev.anhcraft.config.blueprint.ClassSchema;
import dev.anhcraft.config.blueprint.Processor;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.context.ElementScope;
import dev.anhcraft.config.context.PropertyScope;
import dev.anhcraft.config.context.ValueScope;
import dev.anhcraft.config.error.DenormalizationException;
import dev.anhcraft.config.error.IllegalTypeException;
import dev.anhcraft.config.error.InvalidValueException;
import dev.anhcraft.config.meta.Denormalizer;
import dev.anhcraft.config.meta.Fallback;
import dev.anhcraft.config.type.ComplexTypes;
import dev.anhcraft.config.type.SimpleTypes;
import dev.anhcraft.config.type.TypeResolver;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class ConfigDenormalizerImpl implements ConfigDenormalizer {
  private final ConfigFactory configFactory;
  private final Set<SettingFlag.Denormalizer> settings;

  public ConfigDenormalizerImpl(
      ConfigFactory configFactory, Set<SettingFlag.Denormalizer> settings) {
    this.configFactory = configFactory;
    this.settings = Collections.unmodifiableSet(settings);
  }

  private Context createContext() {
    return configFactory.getContextProvider().provideDenormalizationContext(configFactory);
  }

  @Override
  public @NotNull Set<SettingFlag.Denormalizer> getSettings() {
    return settings;
  }

  @Override
  public <T> @Nullable Object denormalize(@Nullable T simple, @NotNull Type targetType) {
    return denormalize(createContext(), simple, targetType);
  }

  @Override
  public <T> @Nullable Object denormalize(
      @NotNull Context ctx, @Nullable T simple, @NotNull Type targetType) {
    validateSimpleType(ctx, simple);
    return _denormalize(ctx, simple, targetType);
  }

  @Override
  public void denormalizeToInstance(
      @NotNull Dictionary simple, @NotNull Type targetType, @NotNull Object instance) {
    denormalizeToInstance(createContext(), simple, targetType, instance);
  }

  @Override
  public void denormalizeToInstance(
      @NotNull Context ctx,
      @NotNull Dictionary simple,
      @NotNull Type targetType,
      @NotNull Object instance) {
    validateSimpleType(ctx, simple);
    validateComplexType(ctx, instance, targetType);
    _denormalizeToInstance(ctx, simple, targetType, instance, PropertyList.ALL);
  }

  // ======== Internal implementations ========

  private <T> void validateSimpleType(Context ctx, T simple) {
    if (!SimpleTypes.test(simple))
      throw new IllegalTypeException(ctx, "Supplied argument is not a simple object: " + simple);
  }

  private void validateComplexType(
      Context ctx, @NotNull Object instance, @NotNull Type targetType) {
    try {
      Class<?> erasureType = ComplexTypes.erasure(targetType);
      if (!erasureType.isAssignableFrom(instance.getClass()))
        throw new IllegalTypeException(
            ctx, "Supplied instance is not compatible to " + erasureType.getName());
    } catch (ClassNotFoundException e) {
      throw new IllegalTypeException(ctx, "Cannot perform type check", e);
    }
  }

  @SuppressWarnings("rawtypes")
  private <T> Object _denormalize(Context ctx, @Nullable T simple, @NotNull Type targetType) {
    if (simple == null) return null;
    if (targetType == Object.class) return simple;
    if (ComplexTypes.isArray(targetType)) return _denormalizeToArray(ctx, targetType, simple);
    Class<?> erasureType = erasureType(ctx, targetType);
    TypeAdapter adapter = configFactory.getTypeAdapter(erasureType);
    if (adapter != null && !(adapter instanceof TypeInferencer)) {
      Object result = adapter.complexify(ctx, simple, targetType);
      if (result != null
          && !ComplexTypes.wrapPrimitive(erasureType).isAssignableFrom(result.getClass())) {
        String msg =
            String.format(
                "Adapter returned incompatible type '%s' while the desire is '%s'",
                result.getClass().getName(), erasureType.getName());
        throw new IllegalTypeException(ctx, msg);
      }
      return result;
    }
    if (simple instanceof Dictionary)
      return _denormalizeInstance(ctx, (Dictionary) simple, targetType);
    return null;
  }

  private <T> Object _denormalizeToArray(Context ctx, Type targetType, T simple) {
    Type elemType = ComplexTypes.getComponentType(targetType);
    if (elemType == null) return null;
    Class<?> erasureElemType = erasureType(ctx, elemType);
    int len = SimpleTypes.getContainerSize(simple);
    Object object = Array.newInstance(erasureElemType, len);
    for (int i = 0; i < len; i++) {
      ctx.enterScope(new ElementScope(i));
      {
        Object value = _denormalize(ctx, SimpleTypes.getContainerElement(simple, i), elemType);
        ctx.enterScope(new ValueScope(value));
        Array.set(object, i, value);
        ctx.exitScope();
      }
      ctx.exitScope();
    }
    return object;
  }

  private Object _denormalizeInstance(Context ctx, Dictionary simple, Type targetType) {
    /*

      Built-in instance deserialization algorithm:

      1. If the type has no discriminators -> no shape could be linked
      - Deserialize all properties including "declared" and "inherited"
      2. If the type has at least one discriminator -> shape can be linked; however, their existence is unknown
      - Deserialize only "discriminators" properties (either "declared" or "inherited")
      - Look up a compatible shape given the partial instance
      - If a shape exists, deserialize the shape from scratch
      - If a shape does not exist, deserialize the instance again from scratch
       * The reason that we start from scratch is that processors, context and scopes could be different
    */

    Class<?> erasureType = erasureType(ctx, targetType);
    ClassSchema schema = configFactory.getSchema(erasureType);

    try {
      if (!schema.effectiveDiscriminators().isEmpty()) {
        Object base = configFactory.getInstanceFactory().newInstance(ctx, erasureType);
        _denormalizeToInstance(
            ctx, simple, targetType, base, PropertyList.EFFECTIVE_DISCRIMINATOR_ONLY);
        Class<?> shape = configFactory.getShapeRegistry().solve(ctx, base);

        if (shape != null && erasureType.isAssignableFrom(shape)) {
          // TODO shape is type-erasure (no generic parameters support)
          Object object = configFactory.getInstanceFactory().newInstance(ctx, shape);
          _denormalizeToInstance(ctx, simple, shape, object, PropertyList.ALL);
          return object;
        }
      }

      Object object = configFactory.getInstanceFactory().newInstance(ctx, erasureType);
      _denormalizeToInstance(ctx, simple, targetType, object, PropertyList.ALL);
      return object;

    } catch (InstantiationException e) {
      throw new DenormalizationException(
          ctx, "Cannot create instance of " + erasureType.getName(), e);
    }
  }

  private void _denormalizeToInstance(
      Context ctx, Dictionary simple, Type targetType, Object instance, PropertyList propertyList) {
    TypeResolver resolver = TypeResolver.of(targetType);
    ClassSchema schema = configFactory.getSchema(erasureType(ctx, targetType));
    Set<String> settingsProcessed = new HashSet<>();

    Collection<ClassProperty> classProperties =
        propertyList == PropertyList.EFFECTIVE_DISCRIMINATOR_ONLY
            ? schema.effectiveDiscriminators()
            : schema.properties();

    for (ClassProperty property : classProperties) {
      if (property.isConstant()) continue;

      String setting;
      Object value;

      // if there is a fallback property at the end, we collect all remaining settings including the
      // setting of the
      // fallback property
      if (property.isFallback()) {
        Fallback fallback = property.field().getAnnotation(Fallback.class);
        Set<String> exclusion;
        if (fallback.distinctBy() == Fallback.Distinct.NAME) {
          exclusion = settingsProcessed;
        } else {
          exclusion = new HashSet<>();
          for (String s : settingsProcessed) {
            ClassProperty cp = schema.property(s);
            if (cp != null) {
              exclusion.add(cp.name());
              exclusion.addAll(cp.aliases());
            }
          }
        }
        SchemalessDictionary trap = new SchemalessDictionary();
        for (Map.Entry<String, Object> entry : simple.entrySet()) {
          if (!exclusion.contains(entry.getKey())) trap.put(entry.getKey(), entry.getValue());
        }
        setting = "";
        value = trap;
      } else {
        Map.Entry<String, Object> entry = simple.search(property.name(), property.aliases());
        setting = entry == null ? "" : entry.getKey();
        value = entry == null ? null : entry.getValue();
      }

      ctx.enterScope(new PropertyScope(property, setting, simple));
      scope:
      {
        Processor processor = property.denormalizer();

        if (processor != null && processor.strategy() == Denormalizer.Strategy.REPLACE) {
          if (processor.invoker() instanceof Processor.VoidDenormalizationInvoker) {
            ((Processor.VoidDenormalizationInvoker) processor.invoker())
                .invoke(ctx, instance, value);
            break scope;
          }
          value =
              ((Processor.DenormalizationInvoker) processor.invoker()).invoke(ctx, instance, value);
        } else {
          if (value != null) {
            Type solvedType = resolver.resolve(property.type());
            value = _denormalize(ctx, value, solvedType);
          }

          if (processor != null && processor.strategy() == Denormalizer.Strategy.AFTER) {
            if (processor.invoker() instanceof Processor.VoidDenormalizationInvoker) {
              ((Processor.VoidDenormalizationInvoker) processor.invoker())
                  .invoke(ctx, instance, value);
              break scope;
            }
            value =
                ((Processor.DenormalizationInvoker) processor.invoker())
                    .invoke(ctx, instance, value);
          }
        }

        if (property.isOptional() && value == null) break scope;

        Class<?> propertyTypeErasure = erasureType(ctx, property.type());

        if (value == null && propertyTypeErasure.isPrimitive()) break scope;

        if (value != null
            && !ComplexTypes.wrapPrimitive(propertyTypeErasure).isAssignableFrom(value.getClass()))
          break scope;

        if (!settings.contains(SettingFlag.Denormalizer.DISABLE_VALIDATION)
            && !property.validator().check(value)) {
          if (property.validator().silent()) break scope;
          throw new InvalidValueException(
              ctx,
              String.format("Property '%s' %s", property.name(), property.validator().message()));
        }

        ctx.enterScope(new ValueScope(value));
        try {
          property.field().set(instance, value);
        } catch (IllegalAccessException e) {
          throw new DenormalizationException(
              ctx,
              "Cannot access field "
                  + property.field().getName()
                  + " (representing property "
                  + property.name()
                  + ") declared in "
                  + property.field().getDeclaringClass().getName(),
              e);
        }
        ctx.exitScope();
      }
      ctx.exitScope();

      settingsProcessed.add(setting);
    }
  }

  private static Class<?> erasureType(Context ctx, Type type) {
    try {
      return ComplexTypes.erasure(type);
    } catch (ClassNotFoundException e) {
      throw new DenormalizationException(
          ctx, "Cannot perform type-erasure on " + ComplexTypes.describe(type), e);
    }
  }

  private enum PropertyList {
    ALL,
    EFFECTIVE_DISCRIMINATOR_ONLY
  }
}
