package dev.anhcraft.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class ComplexTypes {
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
}
