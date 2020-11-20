package dev.anhcraft.config.schema;

import dev.anhcraft.config.annotations.Description;
import dev.anhcraft.config.annotations.Examples;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConfigSchema {
    private final Class<?> owner;
    private final List<EntrySchema> entrySchemas;
    private final Description description;
    private final Examples examples;

    public ConfigSchema(@NotNull Class<?> owner,
                        @NotNull List<EntrySchema> entrySchemas,
                        @Nullable Description description,
                        @Nullable Examples examples) {
        this.owner = owner;
        this.entrySchemas = entrySchemas;
        this.description = description;
        this.examples = examples;
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
    public Examples getExamples() {
        return examples;
    }
}
