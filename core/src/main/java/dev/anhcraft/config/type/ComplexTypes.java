package dev.anhcraft.config.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

    public static boolean isNormalClassOrAbstract(@NotNull Class<?> clazz) {
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
        } else if (type instanceof TypeResolver) {
            return erasure(((TypeResolver) type).provideType());
        } else if (type instanceof Class<?>) {
            return (Class<?>) type;
        }
        throw new UnsupportedOperationException();
    }

    @NotNull
    public static String describe(@NotNull Type type) {
        if (type instanceof GenericArrayType) {
            GenericArrayType arrayType = (GenericArrayType) type;
            return String.format("%s[]", describe(arrayType.getGenericComponentType()));
        } else if (type instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) type;
            String args = Arrays.stream(paramType.getActualTypeArguments())
                    .map(ComplexTypes::describe)
                    .collect(Collectors.joining(","));
            if (paramType.getOwnerType() != null)
                return String.format("%s.%s<%s>", describe(paramType.getOwnerType()), describe(paramType.getRawType()), args);
            else
                return String.format("%s<%s>", describe(paramType.getRawType()), args);
        } else if (type instanceof TypeVariable) {
            return ((TypeVariable<?>) type).getName();
        } else if (type instanceof TypeResolver) {
            return describe(((TypeResolver) type).provideType());
        } else {
            return String.format("%s", type.getTypeName());
        }
    }

    @Nullable
    public static Type getActualTypeArgument(@NotNull Type container, int i) {
        if (container instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) container;
            Type[] args = parameterizedType.getActualTypeArguments();
            return i < args.length ? args[i] : null;
        } else if (container instanceof TypeResolver) {
            return getActualTypeArgument(((TypeResolver) container).provideType(), i);
        }
        return null;
    }
}
