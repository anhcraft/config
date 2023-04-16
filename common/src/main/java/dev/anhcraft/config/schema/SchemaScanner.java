package dev.anhcraft.config.schema;

import dev.anhcraft.config.annotations.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class SchemaScanner {
    private static final Map<String, ConfigSchema> CACHE = new HashMap<>();

    @Nullable
    public static ConfigSchema scanConfig(@NotNull Class<?> clazz) {
        return scanConfig(clazz, true);
    }

    @Nullable
    public static ConfigSchema scanConfig(@NotNull Class<?> clazz, boolean cache) {
        if (clazz.isAnnotationPresent(Configurable.class)) {
            if (cache) {
                ConfigSchema configSchema = CACHE.get(clazz.getName());
                if (configSchema != null) {
                    return configSchema;
                }
            }
            List<EntrySchema> entries = new ArrayList<>();
            List<Method> postHandlers = new ArrayList<>();
            List<String[]> ex = new ArrayList<>();
            Examples examples = clazz.getAnnotation(Examples.class);
            if (examples != null) {
                for (Example example : examples.value()) {
                    ex.add(example.value());
                }
            }
            Example example = clazz.getAnnotation(Example.class);
            if (example != null) {
                ex.add(example.value());
            }
            Description description = clazz.getAnnotation(Description.class);

            scanEntries(clazz, entries);
            scanHandlers(clazz, postHandlers);

            Class<?> parent = clazz;
            while (true) {
                parent = parent.getSuperclass();
                if (parent != null && !parent.equals(Object.class) && parent.isAnnotationPresent(Configurable.class)) {
                    scanEntries(parent, entries);
                    scanHandlers(parent, postHandlers);
                    continue;
                }
                break;
            }

            ConfigSchema configSchema = new ConfigSchema(clazz, Collections.unmodifiableList(entries), description, ex, postHandlers);
            if (cache) {
                CACHE.put(clazz.getName(), configSchema);
            }

            return configSchema;
        } else {
            return null;
        }
    }

    private static void scanEntries(Class<?> clazz, List<EntrySchema> entries) {
        Configurable configurable = clazz.getAnnotation(Configurable.class);

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Exclude.class)) {
                continue;
            }
            Description description = field.getAnnotation(Description.class);
            Validation validation = field.getAnnotation(Validation.class);
            List<String[]> ex = new ArrayList<>();
            Examples examples = field.getAnnotation(Examples.class);
            if (examples != null) {
                for (Example example : examples.value()) {
                    ex.add(example.value());
                }
            }
            Example example = field.getAnnotation(Example.class);
            if (example != null) {
                ex.add(example.value());
            }
            Consistent consistent = field.getAnnotation(Consistent.class);
            Virtual virtual = field.getAnnotation(Virtual.class);
            Path path = field.getAnnotation(Path.class);
            String key;
            if (path != null) {
                key = path.value();
            } else {
                key = field.getName();
                switch (configurable.keyNamingStyle()) {
                    case SNAKE_CASE: {
                        key = key.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
                        break;
                    }
                    case TRAIN_CASE: {
                        key = key.replaceAll("([a-z])([A-Z]+)", "$1-$2").toLowerCase();
                        break;
                    }
                }
            }
            entries.add(new EntrySchema(field, key, description, validation, ex, consistent != null, virtual != null));
        }
    }

    private static void scanHandlers(Class<?> clazz, List<Method> methods) {
        for (Method method : clazz.getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.isAnnotationPresent(PostHandler.class)) {
                methods.add(method);
            }
        }
    }
}
