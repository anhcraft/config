package dev.anhcraft.config.schema;

import dev.anhcraft.config.annotations.Description;
import dev.anhcraft.config.annotations.Examples;
import dev.anhcraft.config.annotations.Path;
import dev.anhcraft.config.annotations.Validation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class EntrySchema {
    private final Field field;
    private final Path path;
    private final Description description;
    private final Validation validation;
    private final Examples examples;

    public EntrySchema(@NotNull Field field,
                       @Nullable Path path,
                       @Nullable Description description,
                       @Nullable Validation validation,
                       @Nullable Examples examples) {
        this.field = field;
        this.path = path;
        this.description = description;
        this.validation = validation;
        this.examples = examples;
    }

    @NotNull
    public Field getField() {
        return field;
    }

    @Nullable
    public Path getPath() {
        return path;
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
    public Examples getExamples() {
        return examples;
    }

    @NotNull
    public String getKey() {
        return path == null ? field.getName() : path.value();
    }
}
