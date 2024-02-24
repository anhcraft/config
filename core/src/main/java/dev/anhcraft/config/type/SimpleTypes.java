package dev.anhcraft.config.type;

import dev.anhcraft.config.Dictionary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.LinkedHashMap;
import java.util.Map;

public final class SimpleTypes {
    public static <T> boolean isScalar(@NotNull Class<T> value) {
        if (value.isPrimitive())
            return true;
        return String.class.isAssignableFrom(value) ||
                Number.class.isAssignableFrom(value) ||
                Boolean.class.isAssignableFrom(value) ||
                Character.class.isAssignableFrom(value);
    }

    public static <T> boolean validate(@NotNull Class<T> value) {
        if (value.isPrimitive())
            return true;
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
                return true;
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

    @SuppressWarnings("unchecked")
    public static <T> T deepClone(T value) {
        if (value instanceof String ||
                value instanceof Number ||
                value instanceof Boolean ||
                value instanceof Character)
            return value; // immutable
        else if (value instanceof Dictionary) {
            LinkedHashMap<String, Object> backend = ((Dictionary) value).unwrap();
            for (Map.Entry<String, Object> entry : backend.entrySet()) {
                entry.setValue(deepClone(entry.getValue()));
            }
            return (T) Dictionary.wrap(backend);
        }
        else if (value.getClass().isArray()) {
            Class<?> componentType = value.getClass().getComponentType();
            int n = Array.getLength(value);
            Object result = Array.newInstance(componentType, n);
            for (int i = 0; i < n; i++) {
                Array.set(result, i, deepClone(Array.get(value, i)));
            }
            return (T) result;
        }
        throw new IllegalArgumentException(String.format("Object of type %s is not a simple type", value.getClass().getName()));
    }
}
