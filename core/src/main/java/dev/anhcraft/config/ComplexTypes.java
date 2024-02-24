package dev.anhcraft.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

public class ComplexTypes {
    public final static Map<Class<?>, Class<?>> map = new HashMap<>();

    static {
        map.put(byte.class, Byte.class);
        map.put(short.class, Short.class);
        map.put(int.class, Integer.class);
        map.put(long.class, Long.class);
        map.put(float.class, Float.class);
        map.put(double.class, Double.class);
        map.put(char.class, Character.class);
        map.put(boolean.class, Boolean.class);
    }

    public static @NotNull Class<?> wrapPrimitive(@NotNull Class<?> clazz) {
        return map.getOrDefault(clazz, clazz);
    }

    public static boolean isEligibleForSchema(@NotNull Class<?> clazz) {
        return !(clazz.isPrimitive() || clazz.isEnum() || clazz.isArray() || clazz.isInterface() ||
                clazz.isAnnotation() || clazz.isSynthetic() || clazz.isAnonymousClass());
    }

    public static boolean isArray(@Nullable Type type) {
        return (type instanceof GenericArrayType) || (type instanceof Class && ((Class<?>) type).isArray());
    }

    @Nullable
    public static Type getComponentType(@Nullable Type type) {
        if (type instanceof GenericArrayType) {
            GenericArrayType arrayType = (GenericArrayType) type;
            return arrayType.getGenericComponentType();
        }
        return null;
    }

    @NotNull
    public static Class<?> getArrayType(@NotNull Class<?> componentType) throws ClassNotFoundException {
        return Class.forName("[L" + componentType.getCanonicalName() + ";");
    }

    @NotNull
    public static Class<?> erasure(@NotNull Type type) throws ClassNotFoundException {
        if (type instanceof GenericArrayType) {
            Class<?> componentType = erasure(((GenericArrayType) type).getGenericComponentType());
            return getArrayType(componentType);
        } else if (type instanceof ParameterizedType) {
            return erasure(((ParameterizedType)type).getRawType());
        } else if (type instanceof TypeVariable) {
            return Object.class;
        } else {
            return (Class<?>) type;
        }
    }

    @Nullable
    public static Type getActualType(@NotNull Type container, int i) {
        if (container instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) container;
            Type[] args = parameterizedType.getActualTypeArguments();
            return i < args.length ? args[i] : null;
        }
        return null;
    }
}
