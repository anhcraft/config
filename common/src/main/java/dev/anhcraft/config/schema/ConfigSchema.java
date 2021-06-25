package dev.anhcraft.config.schema;

import dev.anhcraft.config.annotations.Description;
import dev.anhcraft.config.annotations.Examples;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.List;

public class ConfigSchema {
    private final Class<?> owner;
    private final List<EntrySchema> entrySchemas;
    private final Description description;
    private final List<String[]> examples;
    private final List<Method> postHandlers;

    public ConfigSchema(@NotNull Class<?> owner,
                        @NotNull List<EntrySchema> entrySchemas,
                        @Nullable Description description,
                        @Nullable List<String[]> examples, List<Method> postHandlers) {
        this.owner = owner;
        this.entrySchemas = entrySchemas;
        this.description = description;
        this.examples = examples;
        this.postHandlers = postHandlers;
    }

    @NotNull
    public Class<?> getOwner() {
        return owner;
    }

    @NotNull
    public List<EntrySchema> getEntrySchemas() {
        return entrySchemas;
    }

    @Nullable
    public Description getDescription() {
        return description;
    }

    @Nullable
    public List<String[]> getExamples() {
        return examples;
    }

    @NotNull
    public List<Method> getPostHandlers() {
        return postHandlers;
    }
}
