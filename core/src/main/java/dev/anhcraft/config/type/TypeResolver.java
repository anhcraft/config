package dev.anhcraft.config.type;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class TypeResolver implements Type {
    public static @NotNull TypeResolver of(@NotNull Type type) {
        if (type instanceof TypeResolver)
            return (TypeResolver) type;
        return new TypeResolver() {
            @Override
            public @NotNull Type provideType() {
                return type;
            }
        };
    }

    private Map<String, Type> typeMapping;

    public abstract @NotNull Type provideType();

    @NotNull
    public Map<String, Type> getTypeMapping() {
        if (typeMapping != null)
            return typeMapping;
        if (!(provideType() instanceof ParameterizedType))
            return Map.of();
        Type rawType = ((ParameterizedType) provideType()).getRawType();
        if (!(rawType instanceof Class))
            return Map.of();
        Type[] actualArgs = ((ParameterizedType) provideType()).getActualTypeArguments();
        TypeVariable<? extends Class<?>>[] typeVariables = ((Class<?>) rawType).getTypeParameters();
        Map<String, Type> map = new HashMap<>(typeVariables.length);
        for (int i = 0; i < typeVariables.length; i++) {
            map.put(typeVariables[i].getName(), actualArgs[i]);
        }
        map = Collections.unmodifiableMap(map);
        return typeMapping = map;
    }

    public @NotNull Type resolve(@NotNull Type type) {
        /*
            Resolve an unresolved type containing type variables using the type captured
            For example: public class Container<T> { public T[] items; }
            Using TypeResolver<Container<String>>, captures Container<String>
            The class is Container having one type variable named T
            Then, it is possible to resolve T[] to String[]
         */
        if (type instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) type;
            return new TypeImpl.ParameterizedTypeImpl(
                    paramType.getOwnerType(),
                    paramType.getRawType(),
                    Arrays.stream(paramType.getActualTypeArguments()).map(this::resolve).toArray(Type[]::new)
            );
        } else if (type instanceof GenericArrayType) {
            GenericArrayType arrayType = (GenericArrayType) type;
            return new TypeImpl.GenericArrayTypeImpl(
                    resolve(arrayType.getGenericComponentType())
            );
        } else if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) type;
            return new TypeImpl.WildcardTypeImpl(
                    Arrays.stream(wildcardType.getUpperBounds()).map(this::resolve).toArray(Type[]::new),
                    Arrays.stream(wildcardType.getLowerBounds()).map(this::resolve).toArray(Type[]::new)
            );
        } else if (type instanceof TypeVariable) {
            TypeVariable<?> typeVariable = (TypeVariable<?>) type;
            return getTypeMapping().getOrDefault(typeVariable.getName(), Object.class);
        } else {
            return type;
        }
    }
}
