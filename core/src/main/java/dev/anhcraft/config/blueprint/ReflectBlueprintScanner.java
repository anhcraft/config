package dev.anhcraft.config.blueprint;

import dev.anhcraft.config.type.ComplexTypes;
import dev.anhcraft.config.error.UnsupportedSchemaException;
import dev.anhcraft.config.meta.Optional;
import dev.anhcraft.config.meta.*;
import dev.anhcraft.config.validate.DisabledValidator;
import dev.anhcraft.config.validate.ValidationRegistry;
import dev.anhcraft.config.validate.Validator;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.UnaryOperator;

/**
 * A Reflection-based {@link BlueprintScanner}
 */
public class ReflectBlueprintScanner implements BlueprintScanner {
    private final UnaryOperator<String> namingPolicy;
    private final ValidationRegistry validationRegistry;

    public ReflectBlueprintScanner(@NotNull UnaryOperator<String> namingPolicy, @NotNull ValidationRegistry validationRegistry) {
        this.namingPolicy = namingPolicy;
        this.validationRegistry = validationRegistry;
    }

    @Override
    public @NotNull Schema scanSchema(@NotNull Class<?> type) {
        if (!ComplexTypes.isNormalClassOrAbstract(type))
            throw new UnsupportedSchemaException(String.format("Cannot create schema for '%s'", type.getName()));

        Map<String, Field> fields = new LinkedHashMap<>();

        for (Field field : type.getDeclaredFields()) {
            try {
                field.setAccessible(true);
            } catch (Exception e) { // TODO is there better way to check accessibility?
                continue;
            }
            if (isExcluded(field))
                continue;
            String primaryName = namingPolicy.apply(field.getName()).trim();
            if (primaryName.isBlank())
                throw new UnsupportedSchemaException(String.format("Schema '%s' contains naming error due to naming policy", type.getName()));
            if (fields.containsKey(primaryName))
                throw new UnsupportedSchemaException(String.format("Schema '%s' contains naming conflicts due to naming policy", type.getName()));
            fields.put(primaryName, field);
        }

        Map<String, Property> lookup = new LinkedHashMap<>();
        Set<String> nameClaimed = new HashSet<>(fields.keySet()); // initially, "lookup" cannot be used
        List<Property> properties = new ArrayList<>();

        for (Map.Entry<String, Field> entry : fields.entrySet()) {
            String primaryName = entry.getKey();
            Field field = entry.getValue();

            PropertyNaming name = scanName(field, primaryName, nameClaimed);
            List<String> description = scanDescription(field);
            byte modifier = scanModifier(field);
            Validator validator = scanValidation(field);

            Property property = new Property(name, description, modifier, validator, field);
            lookup.put(name.primary(), property);
            nameClaimed.remove(primaryName);
            nameClaimed.add(name.primary());
            for (String alias : name.aliases()) {
                lookup.put(alias, property);
                nameClaimed.add(alias);
            }
            properties.add(property);
        }

        return new Schema(type, properties, lookup);
    }

    private boolean isExcluded(Field field) {
        return Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers()) ||
                Modifier.isNative(field.getModifiers()) || field.isSynthetic() ||
                field.getAnnotation(Exclude.class) != null;
    }

    private PropertyNaming scanName(Field field, String originalPrimaryName, Set<String> existing) {
        LinkedHashSet<String> aliases = new LinkedHashSet<>();
        String primary = null;

        Name nameMeta = field.getAnnotation(Name.class);
        if (nameMeta != null) {
            for (String name : nameMeta.value()) {
                name = name.trim();
                if (name.isEmpty() || existing.contains(name) || aliases.contains(name)) continue;
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
                if (alias.isEmpty() || existing.contains(alias) || aliases.contains(alias)) continue;
                aliases.add(alias);
            }
        }

        return new PropertyNaming(primary == null ? originalPrimaryName : primary, aliases);
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
