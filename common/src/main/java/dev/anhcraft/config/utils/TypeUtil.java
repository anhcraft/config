package dev.anhcraft.config.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

public class TypeUtil {
    // remove wildcard, converts it to normal type (class/array/generic)
    @NotNull
    public static Type normalize(Type type) {
        if (type instanceof WildcardType) {
            WildcardType t = (WildcardType) type;
            if (t.getLowerBounds().length > 0) {
                return normalize(t.getLowerBounds()[0]);
            }
            if (t.getUpperBounds().length > 0) {
                return normalize(t.getUpperBounds()[0]);
            }
            return Object.class;
        } else {
            return type;
        }
    }

    // get element part from parameterized type or generic array type
    @Nullable
    public static Type getElementType(Type type) {
        if (type instanceof ParameterizedType) {
            return normalize(((ParameterizedType) type).getActualTypeArguments()[0]);
        } else if (type instanceof GenericArrayType) {
            return normalize(((GenericArrayType) type).getGenericComponentType());
        } else if (type instanceof Class<?>) {
            return ((Class<?>) type).getComponentType();
        } else {
            return null;
        }
    }

    // get the raw type
    @Nullable
    public static Class<?> getRawType(Type type) {
        if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        } else if (type instanceof Class<?>) {
            return (Class<?>) type;
        } else {
            return null;
        }
    }
}
