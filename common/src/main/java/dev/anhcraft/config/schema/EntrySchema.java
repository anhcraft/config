package dev.anhcraft.config.schema;

import dev.anhcraft.config.annotations.Description;
import dev.anhcraft.config.annotations.Validation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;

public class EntrySchema {
    private final Field field;
    private final String key;
    private final Description description;
    private final Validation validation;
    private final List<String[]> examples;
    private final boolean constant;
    private final boolean virtual;
    private final boolean optional;

    public EntrySchema(@NotNull Field field,
                       @NotNull String key,
                       @Nullable Description description,
                       @Nullable Validation validation,
                       @Nullable List<String[]> examples,
                       boolean constant,
                       boolean virtual,
                       boolean optional) {
        this.field = field;
        this.key = key;
        this.description = description;
        this.validation = validation;
        this.examples = examples;
        this.constant = constant;
        this.virtual = virtual;
        this.optional = optional;
    }

    @NotNull
    public Field getField() {
        return field;
    }

    @Nullable
    public Description getDescription() {
        return description;
    }

    @Nullable
    public Validation getValidation() {
        return validation;
    }

    @Nullable
    public List<String[]> getExamples() {
        return examples;
    }

    @NotNull
    public String getKey() {
        return key;
    }

    public boolean isConstant() {
        return constant;
    }

    public boolean isVirtual() {
        return virtual;
    }

    public boolean isOptional() {
        return optional;
    }
}
