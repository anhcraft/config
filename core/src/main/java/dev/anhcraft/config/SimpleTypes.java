package dev.anhcraft.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;

public final class SimpleTypes {
    public static <T> boolean validate(@NotNull Class<T> value) {
        if (String.class.isAssignableFrom(value) ||
                Number.class.isAssignableFrom(value) ||
                Boolean.class.isAssignableFrom(value) ||
                Character.class.isAssignableFrom(value) ||
                Dictionary.class.isAssignableFrom(value))
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
                value instanceof Dictionary)
            return true;
        if (value.getClass().isArray()) {
            Class<?> componentType = value.getClass().getComponentType();
            // If the array is of Object type, we need to check each element
            if (componentType == Object.class) {
                int n = Array.getLength(value);
                for (int i = 0; i < n; i++) {
                    if (!validate(Array.get(value, i)))
                        return false;
                }
            } else { // Otherwise, we can do quick O(1) check
                return validate(componentType);
            }
        }
        return false;
    }

    public static <T> int getContainerSize(@NotNull T simpleValue) {
        if (simpleValue.getClass().isArray())
            return Array.getLength(simpleValue);
        else if (simpleValue instanceof Dictionary)
            return ((Dictionary) simpleValue).size();
        else
            return 1;
    }

    @Nullable
    public static <T> Object getContainerElement(T simple, int i) {
        if (simple.getClass().isArray())
            return Array.get(simple, i);
        else if (simple instanceof Dictionary)
            return ((Dictionary) simple).getValueAt(i);
        else
            return simple;
    }
}
