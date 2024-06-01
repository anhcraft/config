package dev.anhcraft.config.blueprint;

import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.error.UnsupportedSchemaException;
import dev.anhcraft.config.meta.*;
import dev.anhcraft.config.meta.Optional;
import dev.anhcraft.config.type.ComplexTypes;
import dev.anhcraft.config.validate.DisabledValidator;
import dev.anhcraft.config.validate.ValidationRegistry;
import dev.anhcraft.config.validate.Validator;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.UnaryOperator;
import org.jetbrains.annotations.NotNull;

/**
 * A Reflection-based {@link SchemaScanner} that generates {@link ClassSchema}.
 */
public class ReflectSchemaScanner implements SchemaScanner<ClassSchema> {
  private final UnaryOperator<String> namingPolicy;
  private final ValidationRegistry validationRegistry;

  public ReflectSchemaScanner(
      @NotNull UnaryOperator<String> namingPolicy, @NotNull ValidationRegistry validationRegistry) {
    this.namingPolicy = namingPolicy;
    this.validationRegistry = validationRegistry;
  }

  @Override
  public @NotNull ClassSchema scanSchema(@NotNull Class<?> type) {
    if (!ComplexTypes.isNormalClassOrAbstract(type))
      throw new UnsupportedSchemaException(
          String.format("Cannot create schema for '%s'", type.getName()));

    Map<String, Processor> normalizers = scanNormalizers(type);
    Map<String, Processor> denormalizers = scanDenormalizers(type);
    Map<String, Field> fields = new LinkedHashMap<>();

    for (Field field : type.getDeclaredFields()) {
      try {
        field.setAccessible(true);
      } catch (Exception e) { // TODO is there better way to check accessibility?
        continue;
      }
      if (isExcluded(field)) continue;

      String primaryName = namingPolicy.apply(field.getName()).trim();
      if (primaryName.isBlank())
        throw new UnsupportedSchemaException(
            String.format(
                "Schema '%s' contains naming error due to naming policy", type.getName()));
      if (fields.containsKey(primaryName))
        throw new UnsupportedSchemaException(
            String.format(
                "Schema '%s' contains naming conflicts due to naming policy", type.getName()));
      fields.put(primaryName, field);
      // It is guaranteed that naming policy generates unique names at this point
    }

    // names claimed at a point in time; initially, they are names after naming policy applied
    Set<String> nameClaimed = new HashSet<>(fields.keySet());

    // a map of primary name and alias maps to ClassProperty
    // we do not initialize the map at this point because there is a possibility that a different
    // primary name will replace the original one
    Map<String, ClassProperty> lookup = new LinkedHashMap<>();

    List<ClassProperty> properties = new ArrayList<>();
    ClassProperty fallback = null;

    for (Map.Entry<String, Field> entry : fields.entrySet()) {
      String originalPrimaryName = entry.getKey();
      Field field = entry.getValue();

      PropertyNaming name = scanName(field, originalPrimaryName, nameClaimed);
      List<String> description = scanDescription(field);
      byte modifier = scanModifier(field);
      Validator validator = scanValidation(field);
      Processor normalizer = normalizers.get(field.getName());
      Processor denormalizer = denormalizers.get(field.getName());

      ClassProperty property =
          new ClassProperty(
              name, description, validator, field, modifier, normalizer, denormalizer);

      if (property.isFallback()) {
        if (fallback != null)
          throw new UnsupportedSchemaException(
              String.format(
                  "Schema '%s' contains more than one fallback property", type.getName()));
        try {
          if (!ComplexTypes.erasure(property.type()).isAssignableFrom(LinkedHashMap.class))
            throw new UnsupportedSchemaException(
                String.format("Schema '%s' contains invalid fallback property", type.getName()));
        } catch (ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
        fallback = property;
      }

      lookup.put(name.primary(), property);

      // discards the original primary name and uses the new one after scanned
      // the new one could be similar to the original one if no new primary name was provided
      nameClaimed.remove(originalPrimaryName);
      nameClaimed.add(name.primary());

      for (String alias : name.aliases()) {
        lookup.put(alias, property);
        nameClaimed.add(alias);
      }

      if (!property.isFallback()) { // fallback must be at the end
        properties.add(property);
      }
    }

    if (fallback != null) properties.add(fallback);

    return new ClassSchema(type, properties, lookup, fallback);
  }

  private Map<String, Processor> scanNormalizers(Class<?> type) {
    Map<String, Processor> lookup = new LinkedHashMap<>();

    for (Method method : type.getDeclaredMethods()) {
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

  private Map<String, Processor> scanDenormalizers(Class<?> type) {
    Map<String, Processor> lookup = new LinkedHashMap<>();

    for (Method method : type.getDeclaredMethods()) {
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

  private PropertyNaming scanName(Field field, String originalPrimaryName, Set<String> existing) {
    LinkedHashSet<String> aliases = new LinkedHashSet<>();
    String primary = null;

    Name nameMeta = field.getAnnotation(Name.class);
    if (nameMeta != null) {
      for (String name : nameMeta.value()) {
        name = name.trim();
        /*
          To be the new primary name:
          - It must not be empty
          - It must not collide with existing names

          To be an alias:
          - It must not be empty
          - It must not collide with existing names
          - It must not collide with to-be-added aliases
          - It must not collide with the new primary name
        */
        if (name.isEmpty()
            || existing.contains(name)
            || aliases.contains(name)
            || name.equals(primary)) continue;
        if (primary == null) {
          primary = name;
        } else {
          aliases.add(name);
        }
      }
    }

    Alias aliasMeta = field.getAnnotation(Alias.class);
    if (aliasMeta != null) {
      for (String alias : aliasMeta.value()) {
        alias = alias.trim();
        if (alias.isEmpty()
            || existing.contains(alias)
            || aliases.contains(alias)
            || alias.equals(primary)) continue;
        aliases.add(alias);
      }
    }

    return new PropertyNaming(aliases, primary == null ? originalPrimaryName : primary);
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
}
