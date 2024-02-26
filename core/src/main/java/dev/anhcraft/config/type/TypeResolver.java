package dev.anhcraft.config.type;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A type resolver is a thin abstract using an existing resolved type to resolve other types.<br>
 * A resolved type is a type that having actual type arguments and no type variables.<br>
 * For example: {@code List<Integer>} is a resolved type, however, {@code List<T>} is not.<br>
 * To construct a type resolver, it is recommended to use {@link #of(Type)}.
 * @see TypeToken
 */
public abstract class TypeResolver implements Type {
    /**
     * Constructs a type resolver.<br>
     * Note: A class is not a resolved type, use {@link TypeToken} instead.
     * @param type the type
     * @return a type resolver or the given type if it is already a {@link TypeResolver}
     */
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

    /**
     * Provides the resolved type.
     * @return the resolved type
     */
    public abstract @NotNull Type provideType();

    /**
     * Gets the type mapping for the resolved type.<br>
     * For example, if the given type is {@code Map<String, Integer>}
     * <ul>
     *     <li>{@code Map} has two type variables {@code K, V}</li>
     *     <li>The returned mapping is {@code (K -> String), (V -> Integer)}</li>
     * </ul>
     * @return the type mapping
     */
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

    /**
     * Resolves the given type.<br>
     * For example, if the given type is {@code Map<String, Integer>}
     * <ul>
     *     <li>{@code Map} has two type variables {@code K, V}</li>
     *     <li>The mapping is {@code (K -> String), (V -> Integer)}</li>
     * </ul>
     * If given {@code List<K>}, this returns {@code List<String>}.
     * @param type the type
     * @return the resolved type
     */
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
