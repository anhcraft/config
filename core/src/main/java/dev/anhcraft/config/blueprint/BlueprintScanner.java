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

public class BlueprintScanner {
    private final UnaryOperator<String> namingStrategy;
    private final ValidationRegistry validationRegistry;

    public BlueprintScanner(@NotNull UnaryOperator<String> namingStrategy, @NotNull ValidationRegistry validationRegistry) {
        this.namingStrategy = namingStrategy;
        this.validationRegistry = validationRegistry;
    }

    @NotNull
    public Schema scanSchema(@NotNull Class<?> type) {
        Map<String, Property> properties = new LinkedHashMap<>();

        for (Field field : type.getDeclaredFields()) {
            if (isExcluded(field))
                continue;

            PropertyNaming name = scanName(field, Set.copyOf(properties.keySet()));
            List<String> description = scanDescription(field);
            byte modifier = scanModifier(field);
            Validator validator = scanValidation(field);
            Class<?> payloadType = scanPayloadType(field);

            properties.put(field.getName(), new Property(name, description, modifier, validator, payloadType, field));
        }

        return new Schema(type, properties);
    }

    private boolean isExcluded(Field field) {
        return Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers()) || field.getAnnotation(Exclude.class) != null;
    }

    private PropertyNaming scanName(Field field, Set<String> existing) {
        Set<String> aliases = new LinkedHashSet<>();
        String primary = null;

        Name nameMeta = field.getAnnotation(Name.class);
        if (nameMeta != null) {
            for (String name : nameMeta.value()) {
                if (name.isBlank() || existing.contains(name) || aliases.contains(name)) continue;
                if (primary == null) primary = name;
                else aliases.add(name);
            }
        }

        Alias aliasMeta = field.getAnnotation(Alias.class);
        if (aliasMeta != null) {
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
