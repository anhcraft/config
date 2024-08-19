package dev.anhcraft.config.blueprint;

import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.error.UnsupportedSchemaException;
import dev.anhcraft.config.meta.*;
import dev.anhcraft.config.meta.Optional;
import dev.anhcraft.config.type.ComplexTypes;
import dev.anhcraft.config.validate.DisabledValidator;
import dev.anhcraft.config.validate.ValidationRegistry;
import dev.anhcraft.config.validate.Validator;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import org.jetbrains.annotations.NotNull;

/**
 * A Reflection-based, thread-safe {@link ClassSchemaScanner} that generates {@link ClassSchema}.
 */
public class ReflectSchemaScanner implements ClassSchemaScanner {
  private final UnaryOperator<String> namingPolicy;
  private final ValidationRegistry validationRegistry;
  private final Map<Class<?>, Schema<?>> schemaCache;

  public ReflectSchemaScanner(
      @NotNull UnaryOperator<String> namingPolicy,
      @NotNull ValidationRegistry validationRegistry,
      @NotNull Supplier<Map<Class<?>, Schema<?>>> schemaCacheProvider) {
    this.namingPolicy = namingPolicy;
    this.validationRegistry = validationRegistry;
    this.schemaCache = schemaCacheProvider.get();
  }

  @Override
  public @NotNull ClassSchema getOrScanSchema(@NotNull Class<?> type) {
    ClassSchema schema = (ClassSchema) schemaCache.get(type);
    if (schema != null) return schema;
    synchronized (this) {
      schemaCache.put(type, schema = scanSchema(type));
    }
    return schema;
  }

  @Override
  public @NotNull ClassSchema scanSchema(@NotNull Class<?> type) {
    if (!ComplexTypes.isNormalClassOrAbstract(type))
      throw new UnsupportedSchemaException(
          String.format("Cannot create schema for '%s'", type.getName()));

    PropertyScanResult propertyListResult =
        scanPropertyList(
            Arrays.asList(type.getDeclaredFields()),
            () -> scanNormalizers(Arrays.asList(type.getDeclaredMethods())),
            () -> scanDenormalizers(Arrays.asList(type.getDeclaredMethods())));

    LazyEffectivePropertyResult effectivePropertyResult =
        new LazyEffectivePropertyResult(this, type);

    //noinspection unchecked
    List<ClassProperty> effectivePropertyList =
        (List<ClassProperty>)
            Proxy.newProxyInstance(
                ReflectSchemaScanner.class.getClassLoader(),
                new Class[] {List.class},
                (proxy, method, args) ->
                    method.invoke(
                        effectivePropertyResult.getPropertyListResult().properties, args));

    //noinspection unchecked
    Map<String, ClassProperty> effectivePropertyMap =
        (Map<String, ClassProperty>)
            Proxy.newProxyInstance(
                ReflectSchemaScanner.class.getClassLoader(),
                new Class[] {Map.class},
                (proxy, method, args) ->
                    method.invoke(
                        effectivePropertyResult.getPropertyListResult().propertyMap, args));

    ClassProperty effectiveFallback =
        (ClassProperty)
            Proxy.newProxyInstance(
                ReflectSchemaScanner.class.getClassLoader(),
                new Class[] {ClassProperty.class},
                (proxy, method, args) ->
                    method.invoke(effectivePropertyResult.getPropertyListResult().fallback, args));

    return new ClassSchema(
        this,
        type,
        effectivePropertyList,
        effectivePropertyMap,
        effectiveFallback,
        propertyListResult.properties,
        propertyListResult.propertyMap,
        propertyListResult.fallback);
  }

  @NotNull ReflectSchemaScanner.PropertyScanResult scanPropertyList(
      @NotNull Collection<Field> fields,
      @NotNull Supplier<Map<String, Processor>> normalizerSupplier,
      @NotNull Supplier<Map<String, Processor>> denormalizerSupplier) {

    /*
      SCHEMA RESOLUTION algorithm:

      1. Goals
      - Respect Property list resolution in Internal.md
      - Effective property list should be lazy-initialized
      - Integrates well with Java inheritance
        * Field hiding is unsupported (inherently unavailable from configuration-side)

      2. Schema creation initially contains local property list resolution
      - Init:
        + PN-F mapping: <String, Field> (Property name -> Field)
        + FN-PN mapping: <String, Set<String>> (Field name -> Property Names)
      - Filter and iterate a list of eligible fields (accessible and non-excluded)
        + Apply naming policy on the field to obtain the Initial primary name
        + Validate Initial primary name (non-blank)
          NOTE: Even if primary name is not used, this step ensures the custom naming policy is correct
        + Create a list of property names
          + If only @Name exists, use its list of names; discard Initial primary name
          + If only @Alias exists, use Initial primary name, then append the list of alias
          + If both @Name and @Alias, @Name first, then append @Alias; discard Initial primary name
        + Filter properties name and implicitly discard invalid ones
        + For each name:
          * put into PN-F
            NOTE: override mapping of the property name to a new field
          * (if overriding) delete PN from old F in FN-PN
          * add new F -> PN to FN-PN
      - Init:
        + P list: Property[]
        + PN-P mapping: <String, Property> (Property name -> Property)
      - Scan processors
      - Create P from FN-PN, add to PN-P and P list and assign processor to P based on F

      3. Effective property list resolution
      - Create proxied list and map; see below for the computation
    */

    Map<String, Field> propertyName2Field = new LinkedHashMap<>();
    Map<String, Set<String>> fieldName2PropertyNames = new LinkedHashMap<>();

    for (Field field : fields) {
      try {
        field.setAccessible(true);
      } catch (Exception e) { // TODO is there better way to check accessibility?
        continue;
      }
      if (isExcluded(field)) continue;

      String initPrimaryName = namingPolicy.apply(field.getName()).trim();
      if (initPrimaryName.isBlank())
        throw new UnsupportedSchemaException(
            String.format(
                "Field '%s' contains naming error due to naming policy", field.getName()));
      propertyName2Field.put(initPrimaryName, field);
      // It is guaranteed that naming policy generates unique names at this point
      fieldName2PropertyNames.put(field.getName(), new LinkedHashSet<>());

      Set<String> names = scanName(field, initPrimaryName);
      for (String name : names) {
        Field lastField = propertyName2Field.put(name, field);
        if (lastField != null) fieldName2PropertyNames.get(lastField.getName()).remove(name);
        fieldName2PropertyNames.get(field.getName()).add(name);
      }
    }

    List<ClassProperty> localPropertyList = new ArrayList<>();
    Map<String, ClassProperty> localPropertyMap = new LinkedHashMap<>();

    // normalizer and denormalizer bounds to fields
    Map<String, Processor> normalizers = normalizerSupplier.get();
    Map<String, Processor> denormalizers = denormalizerSupplier.get();
    ClassProperty fallback = null;

    for (Map.Entry<String, Set<String>> entry : fieldName2PropertyNames.entrySet()) {
      String fieldName = entry.getKey();
      if (entry.getValue().isEmpty()) continue;

      PropertyNaming propertyNaming = PropertyNaming.of(entry.getValue());
      Field field = propertyName2Field.get(propertyNaming.primary());

      List<String> description = scanDescription(field);
      byte modifier = scanModifier(field);
      Validator validator = scanValidation(field);
      Processor normalizer = normalizers.get(fieldName);
      Processor denormalizer = denormalizers.get(fieldName);

      ClassProperty property =
          new ClassPropertyImpl(
              propertyNaming, description, validator, field, modifier, normalizer, denormalizer);

      if (property.isFallback()) {
        if (fallback != null)
          throw new UnsupportedSchemaException(
              String.format(
                  "Field '%s' contains more than one fallback property", field.getName()));
        try {
          if (!ComplexTypes.erasure(property.type()).isAssignableFrom(LinkedHashMap.class))
            throw new UnsupportedSchemaException(
                String.format("Field '%s' contains invalid fallback property", field.getName()));
        } catch (ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
        fallback = property;
      }

      for (String name : entry.getValue()) {
        localPropertyMap.put(name, property);
      }

      if (!property.isFallback()) { // fallback must be at the end
        localPropertyList.add(property);
      }
    }

    if (fallback != null) localPropertyList.add(fallback);

    PropertyScanResult result = new PropertyScanResult();
    result.fallback = fallback;
    result.properties = localPropertyList;
    result.propertyMap = localPropertyMap;

    return result;
  }

  Map<String, Processor> scanNormalizers(Collection<Method> methods) {
    Map<String, Processor> lookup = new LinkedHashMap<>();

    for (Method method : methods) {
      try {
        method.setAccessible(true);
      } catch (Exception e) { // TODO is there better way to check accessibility?
        continue;
      }
      if (isExcluded(method) || !method.isAnnotationPresent(Normalizer.class)) continue;
      if (method.getReturnType() == Void.TYPE) continue;

      Processor.Invoker invoker;

      switch (method.getParameterCount()) {
        case 0:
          invoker = (Processor.NormalizationInvoker) (ctx, instance) -> method.invoke(instance);
          break;
        case 1:
          if (!Context.class.isAssignableFrom(method.getParameterTypes()[0])) {
            continue;
          }
          invoker =
              (Processor.NormalizationInvoker) (ctx, instance) -> method.invoke(instance, ctx);
          break;
        default:
          continue;
      }

      Normalizer normalizer = method.getAnnotation(Normalizer.class);
      Processor processor = new Processor(invoker, normalizer.strategy());
      for (String name : normalizer.value()) {
        if (name.isBlank()) continue;
        lookup.put(name.trim(), processor);
      }
    }

    return lookup;
  }

  Map<String, Processor> scanDenormalizers(Collection<Method> methods) {
    Map<String, Processor> lookup = new LinkedHashMap<>();

    for (Method method : methods) {
      try {
        method.setAccessible(true);
      } catch (Exception e) { // TODO is there better way to check accessibility?
        continue;
      }
      if (isExcluded(method) || !method.isAnnotationPresent(Denormalizer.class)) continue;

      Processor.Invoker invoker;

      switch (method.getParameterCount()) {
        case 1:
          if (method.getReturnType() == Void.TYPE)
            invoker =
                (Processor.VoidDenormalizationInvoker)
                    (ctx, instance, simple) -> {
                      method.invoke(instance, simple);
                      return null;
                    };
          else
            invoker =
                (Processor.DenormalizationInvoker)
                    (ctx, instance, simple) -> method.invoke(instance, simple);
          break;
        case 2:
          if (method.getReturnType() == Void.TYPE)
            invoker =
                (Processor.VoidDenormalizationInvoker)
                    (ctx, instance, simple) -> {
                      method.invoke(instance, simple, ctx);
                      return null;
                    };
          else
            invoker =
                (Processor.DenormalizationInvoker)
                    (ctx, instance, simple) -> method.invoke(instance, simple, ctx);
          break;
        default:
          continue;
      }

      Denormalizer denormalizer = method.getAnnotation(Denormalizer.class);
      Processor processor = new Processor(invoker, denormalizer.strategy());
      for (String name : denormalizer.value()) {
        if (name.isBlank()) continue;
        lookup.put(name.trim(), processor);
      }
    }

    return lookup;
  }

  private boolean isExcluded(Field field) {
    return Modifier.isStatic(field.getModifiers())
        || Modifier.isTransient(field.getModifiers())
        || Modifier.isNative(field.getModifiers())
        || field.isSynthetic()
        || field.getAnnotation(Exclude.class) != null;
  }

  private boolean isExcluded(Method method) {
    return Modifier.isStatic(method.getModifiers())
        || Modifier.isTransient(method.getModifiers())
        || Modifier.isNative(method.getModifiers())
        || method.isSynthetic();
  }

  private Set<String> scanName(Field field, String initPrimaryName) {
    LinkedHashSet<String> names = new LinkedHashSet<>();

    Name nameMeta = field.getAnnotation(Name.class);
    if (nameMeta != null) {
      for (String name : nameMeta.value()) {
        name = name.trim();
        if (!name.isEmpty()) names.add(name);
      }
    }

    if (names.isEmpty()) names.add(initPrimaryName);

    Alias aliasMeta = field.getAnnotation(Alias.class);
    if (aliasMeta != null) {
      for (String alias : aliasMeta.value()) {
        alias = alias.trim();
        if (!alias.isEmpty()) names.add(alias);
      }
    }

    return names;
  }

  private List<String> scanDescription(Field field) {
    List<String> description = Collections.emptyList();

    Describe describeMeta = field.getAnnotation(Describe.class);
    if (describeMeta != null) {
      description = Arrays.asList(describeMeta.value());
    }

    return description;
  }

  private byte scanModifier(Field field) {
    byte modifier = 0;
    modifier |= field.getAnnotation(Optional.class) != null ? Property.MODIFIER_OPTIONAL : 0;
    modifier |= field.getAnnotation(Transient.class) != null ? Property.MODIFIER_TRANSIENT : 0;
    modifier |= field.getAnnotation(Constant.class) != null ? Property.MODIFIER_CONSTANT : 0;
    modifier |= field.getAnnotation(Fallback.class) != null ? Property.MODIFIER_FALLBACK : 0;
    return modifier;
  }

  private Validator scanValidation(Field field) {
    Validate validateMeta = field.getAnnotation(Validate.class);
    if (validateMeta != null) {
      return validationRegistry.parseString(validateMeta.value(), validateMeta.silent());
    }
    return DisabledValidator.INSTANCE;
  }

  static class PropertyScanResult {
    List<ClassProperty> properties;
    Map<String, ClassProperty> propertyMap;
    ClassProperty fallback;
  }
}
