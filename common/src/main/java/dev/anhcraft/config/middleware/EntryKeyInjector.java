package dev.anhcraft.config.middleware;

import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.schema.EntrySchema;
import dev.anhcraft.config.struct.ConfigSection;
import dev.anhcraft.config.struct.SimpleForm;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

/**
 * This built-in middleware for config deserializer will perform the following action:
 * <pre>given       ConfigSection{key:value}</pre>
 * <pre>then set    value.(destination) := key</pre>
 * <code>(with key is String; value is ConfigSection)</code>
 */
public class EntryKeyInjector implements ConfigDeserializer.Middleware {
    private final Function<EntrySchema, String> filter;

    /**
     * Constructs a new instance of this injector
     * @param filter a filter that will choose the correct entry that contains the parent {@link ConfigSection}
     *              and returns the destination, otherwise it should return {@code null}.
     */
    public EntryKeyInjector(Function<EntrySchema, String> filter) {
        this.filter = filter;
    }

    @Override
    public @Nullable SimpleForm transform(@NotNull ConfigDeserializer deserializer, @NotNull EntrySchema entrySchema, @Nullable SimpleForm value) {
        String dest = filter.apply(entrySchema);
        if (value != null && value.isSection() && dest != null) {
            ConfigSection s = Objects.requireNonNull(value.asSection());
            try {
                for (String k : s.getKeys(false)) {
                    SimpleForm c = s.get(k);
                    if(c != null && c.isSection()) {
                        Objects.requireNonNull(c.asSection()).set(dest, SimpleForm.of(k));
                        s.set(k, c);
                    }
                }
                return SimpleForm.of(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return value;
    }
}
