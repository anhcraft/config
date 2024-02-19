package dev.anhcraft.config.wrapper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;

public class SimpleTypes {
    public static <T> boolean validate(@NotNull Class<T> value) {
        if (String.class.isAssignableFrom(value) ||
                Number.class.isAssignableFrom(value) ||
                Boolean.class.isAssignableFrom(value) ||
                Character.class.isAssignableFrom(value) ||
                Container.class.isAssignableFrom(value))
            return true;
        if (value.isArray())
            return validate(value.getComponentType());
        return false;
    }

    public static <T> boolean validate(@Nullable T value) {
        if (value == null)
            return true;
        if (value instanceof String ||
                value instanceof Number ||
                value instanceof Boolean ||
                value instanceof Character ||
                value instanceof Container)
            return true;
        if (value.getClass().isArray()) {
            Class<?> componentType = value.getClass().getComponentType();
            // If the array is of Object type, we need to check each element
            // Otherwise, we can do quick O(1) check
            if (componentType == Object.class) {
                int n = Array.getLength(value);
                for (int i = 0; i < n; i++) {
                    if (!validate(Array.get(value, i)))
                        return false;
                }
            } else {
                return validate(componentType);
            }
        }
        return false;
    }
}
