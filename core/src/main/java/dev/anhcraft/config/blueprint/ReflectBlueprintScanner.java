package dev.anhcraft.config.blueprint;

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

public class ReflectBlueprintScanner implements BlueprintScanner {
    private final UnaryOperator<String> namingStrategy;
    private final ValidationRegistry validationRegistry;

    public ReflectBlueprintScanner(@NotNull UnaryOperator<String> namingStrategy, @NotNull ValidationRegistry validationRegistry) {
        this.namingStrategy = namingStrategy;
        this.validationRegistry = validationRegistry;
    }

    @Override
    public @NotNull Schema scanSchema(@NotNull Class<?> type) {
        Map<String, Property> lookup = new LinkedHashMap<>();
        List<Property> properties = new ArrayList<>();

        for (Field field : type.getDeclaredFields()) {
            field.setAccessible(true);
            if (isExcluded(field))
                continue;

            PropertyNaming name = scanName(field, Set.copyOf(lookup.keySet()));
            List<String> description = scanDescription(field);
            byte modifier = scanModifier(field);
            Validator validator = scanValidation(field);
            Class<?> payloadType = scanPayloadType(field);

            Property property = new Property(name, description, modifier, validator, payloadType, field);
            lookup.put(name.primary(), property);
            for (String alias : name.aliases()) {
                lookup.put(alias, property);
            }
            properties.add(property);
        }

        return new Schema(type, properties, lookup);
    }

    private boolean isExcluded(Field field) {
        return Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers()) || field.getAnnotation(Exclude.class) != null;
    }

    private PropertyNaming scanName(Field field, Set<String> existing) {
        Set<String> aliases = Collections.emptySet(); // use empty singleton to reduce allocations
        String primary = null;

        Name nameMeta = field.getAnnotation(Name.class);
        if (nameMeta != null) {
            for (String name : nameMeta.value()) {
                if (name.isBlank() || existing.contains(name) || aliases.contains(name)) continue;
                if (primary == null) {
                    primary = name;
                    aliases = new LinkedHashSet<>();
                } else {
                    aliases.add(name);
                }
            }
        }

        Alias aliasMeta = field.getAnnotation(Alias.class);
        if (aliasMeta != null) {
            aliases = new LinkedHashSet<>();
            for (String alias : aliasMeta.value()) {
                if (alias.isBlank() || existing.contains(alias) || aliases.contains(alias)) continue;
                aliases.add(alias);
            }
        }

        if (primary == null) {
            primary = namingStrategy.apply(field.getName());
        }

        return new PropertyNaming(primary, aliases);
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
            return validationRegistry.parseString(validateMeta.value());
        }
        return DisabledValidator.INSTANCE;
    }

    private Class<?> scanPayloadType(Field field) {
        Payload payloadMeta = field.getAnnotation(Payload.class);
        if (payloadMeta != null) {
            return payloadMeta.value();
        }
        return null;
    }
}
