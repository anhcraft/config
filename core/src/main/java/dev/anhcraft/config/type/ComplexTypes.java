package dev.anhcraft.config.type;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utilities for working with complex types.
 */
public class ComplexTypes {
  private static final Map<Class<?>, Class<?>> map = new HashMap<>();

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

  /**
   * Wraps primitive types into their corresponding wrapper types
   * @param clazz the class
   * @return the wrapped class, if not found, return the given class
   */
  public static @NotNull Class<?> wrapPrimitive(@NotNull Class<?> clazz) {
    return map.getOrDefault(clazz, clazz);
  }

  /**
   * Checks whether the given class is a normal one or an abstract class.
   * @param clazz the class
   * @return {@code true} if the class is a normal one or an abstract class
   */
  public static boolean isNormalClassOrAbstract(@NotNull Class<?> clazz) {
    return !(clazz.isPrimitive()
        || clazz.isEnum()
        || clazz.isArray()
        || clazz.isInterface()
        || clazz.isAnnotation()
        || clazz.isSynthetic()
        || clazz.isAnonymousClass());
  }

  /**
   * Checks if the given object is an array.
   * @param obj the object
   * @return {@code true} if it is
   */
  public static boolean isArray(@Nullable Object obj) {
    return obj instanceof Object[]
        || obj instanceof byte[]
        || obj instanceof short[]
        || obj instanceof int[]
        || obj instanceof long[]
        || obj instanceof float[]
        || obj instanceof double[]
        || obj instanceof char[]
        || obj instanceof boolean[];
  }

  /**
   * Checks whether given class is instantiable.
   * @param clazz the class
   * @return {@code true} if the class is instantiable
   */
  public static boolean isInstantiable(@NotNull Class<?> clazz) {
    int mod = clazz.getModifiers();
    return !Modifier.isAbstract(mod)
        && !Modifier.isInterface(mod)
        && !clazz.isEnum()
        && !clazz.isArray();
  }

  /**
   * Checks whether the given type is an array.<br>
   * For example: {@code int[]} and {@code List<Integer>[]}
   * @param type the type
   * @return {@code true} if the type is an array
   */
  public static boolean isArray(@Nullable Type type) {
    if (type instanceof TypeResolver) return isArray(((TypeResolver) type).provideType());
    return (type instanceof GenericArrayType)
        || (type instanceof Class && ((Class<?>) type).isArray());
  }

  /**
   * Gets the component type of the given array type.<br>
   * For example: {@code int[]} and {@code List<Integer>[]}
   * @param type the array type
   * @return the component type or {@code null} if the type is not an array
   */
  @Nullable public static Type getComponentType(@Nullable Type type) {
    if (type instanceof GenericArrayType) {
      GenericArrayType arrayType = (GenericArrayType) type;
      return arrayType.getGenericComponentType();
    } else if (type instanceof Class && ((Class<?>) type).isArray())
      return ((Class<?>) type).getComponentType();
    else if (type instanceof TypeResolver)
      return getComponentType(((TypeResolver) type).provideType());
    return null;
  }

  /**
   * Gets the array type of the given component type.
   * @param componentType the component type
   * @return the array type
   * @throws ClassNotFoundException cannot initialize the array type
   */
  @NotNull public static Class<?> getArrayType(@NotNull Class<?> componentType)
      throws ClassNotFoundException {
    if (componentType.isPrimitive() || componentType.isArray())
      return Array.newInstance(componentType, 0).getClass(); // TODO optimize?
    return Class.forName("[L" + componentType.getCanonicalName() + ";");
  }

  /**
   * Erasures the given type into a pure class.
   * @param type the type
   * @return the pure class
   * @throws ClassNotFoundException cannot initialize the array type
   */
  @NotNull public static Class<?> erasure(@NotNull Type type) throws ClassNotFoundException {
    if (type instanceof GenericArrayType) {
      Class<?> componentType = erasure(((GenericArrayType) type).getGenericComponentType());
      return getArrayType(componentType);
    } else if (type instanceof ParameterizedType) {
      return erasure(((ParameterizedType) type).getRawType());
    } else if (type instanceof TypeVariable) {
      return Object.class;
    } else if (type instanceof TypeResolver) {
      return erasure(((TypeResolver) type).provideType());
    } else if (type instanceof Class<?>) {
      return (Class<?>) type;
    }
    throw new UnsupportedOperationException();
  }

  /**
   * Stringifies the given type.<br>
   * The class name is fully qualified similar to {@link Class#getName()}
   * @param type the type
   * @return the string
   */
  @NotNull public static String describe(@NotNull Type type) {
    return describe(type, false);
  }

  /**
   * Stringifies the given type.
   * @param type the type
   * @param simple if the type should be simplified
   * @return the string
   */
  @NotNull public static String describe(@NotNull Type type, boolean simple) {
    if (type instanceof GenericArrayType) {
      GenericArrayType arrayType = (GenericArrayType) type;
      return String.format("%s[]", describe(arrayType.getGenericComponentType(), simple));
    } else if (type instanceof ParameterizedType) {
      ParameterizedType paramType = (ParameterizedType) type;
      String args =
          Arrays.stream(paramType.getActualTypeArguments())
              .map(a -> describe(a, simple))
              .collect(Collectors.joining(","));
      if (paramType.getOwnerType() != null)
        return String.format(
            "%s.%s<%s>",
            describe(paramType.getOwnerType(), simple),
            describe(paramType.getRawType(), simple),
            args);
      else return String.format("%s<%s>", describe(paramType.getRawType(), simple), args);
    } else if (type instanceof TypeVariable) {
      return ((TypeVariable<?>) type).getName();
    } else if (type instanceof TypeResolver) {
      return describe(((TypeResolver) type).provideType(), simple);
    } else if (simple && type instanceof Class) {
      return ((Class<?>) type).getSimpleName();
    } else {
      return type.getTypeName();
    }
  }

  /**
   * Gets the actual type argument at the given index.
   * @param container the container
   * @param i the index
   * @return the actual type argument or {@code null} if not found
   */
  @Nullable public static Type getActualTypeArgument(@NotNull Type container, int i) {
    if (container instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) container;
      Type[] args = parameterizedType.getActualTypeArguments();
      return i < args.length ? args[i] : null;
    } else if (container instanceof TypeResolver) {
      return getActualTypeArgument(((TypeResolver) container).provideType(), i);
    }
    return null;
  }

  /**
   * Checks if a class is compatible with the given target class.
   * @param valueClass the class to check for compatibility
   * @param targetClass the target class
   * @return {@code true} if compatible
   */
  public static boolean isCompatible(@NotNull Class<?> valueClass, @NotNull Class<?> targetClass) {
    if (targetClass.isArray()) {
      if (!valueClass.isArray()) return false;
      Class<?> targetComponentType = targetClass.getComponentType();
      Class<?> valueComponentType = valueClass.getComponentType();
      return isCompatible(valueComponentType, targetComponentType);
    } else {
      Class<?> wrappedValueClass = wrapPrimitive(valueClass);
      Class<?> wrappedTargetClass = wrapPrimitive(targetClass);
      return wrappedTargetClass.isAssignableFrom(wrappedValueClass);
    }
  }
}
