package dev.anhcraft.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ComplexTypes {
    public static boolean isArray(@Nullable Type type) {
        return type instanceof GenericArrayType;
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
        } else {
            return (Class<?>) type;
        }
    }
}
