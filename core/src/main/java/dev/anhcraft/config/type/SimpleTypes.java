package dev.anhcraft.config.type;

import dev.anhcraft.config.Dictionary;
import java.lang.reflect.Array;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utilities for working with simple types.<br>
 * Simple types including:
 * <ul>
 *   <li>Primitives</li>
 *   <li>Primitive wrappers</li>
 *   <li>String</li>
 *   <li>Dictionary</li>
 *   <li>Array of simple values</li>
 * </ul>
 */
public final class SimpleTypes {
  /**
   * Checks whether the given class type is scalar.<br>
   * Scalar classes including String, Number, Boolean, Character and their primitive classes
   * @param type The class type
   * @return true if the class type is scalar
   */
  public static boolean isScalar(@NotNull Class<?> type) {
    if (type.isPrimitive()) return true;
    return String.class.isAssignableFrom(type)
        || Number.class.isAssignableFrom(type)
        || Boolean.class.isAssignableFrom(type)
        || Character.class.isAssignableFrom(type);
  }

  /**
   * Validates whether the given class type is a simple type.<br>
   * Simple types are:
   * <ul>
   *     <li>Number, Boolean, Character and their primitive classes</li>
   *     <li>String and Dictionary</li>
   *     <li>Array of simple types</li>
   * </ul>
   * @param type The class type
   * @return true if the class type is a simple type
   * @param <T> The class type
   */
  public static <T> boolean validate(@NotNull Class<T> type) {
    if (type.isPrimitive()) return true;
    if (String.class.isAssignableFrom(type)
        || Number.class.isAssignableFrom(type)
        || Boolean.class.isAssignableFrom(type)
        || Character.class.isAssignableFrom(type)
        || Dictionary.class.isAssignableFrom(type)) return true;
    if (type.isArray()) return validate(type.getComponentType());
    return false;
  }

  /**
   * Tests whether the given value is a simple value.<br>
   * Simple values are:
   * <ul>
   *     <li>Null</li>
   *     <li>Number, Boolean and Character</li>
   *     <li>String and Dictionary</li>
   *     <li>Array of simple values</li>
   * </ul>
   * @param value The value
   * @return true if the value is a simple value
   * @param <T> The value type
   */
  public static <T> boolean test(@Nullable T value) {
    if (value == null) return true;
    if (value instanceof String
        || value instanceof Number
        || value instanceof Boolean
        || value instanceof Character
        || value instanceof Dictionary) return true;
    if (ComplexTypes.isArray(value)) {
      Class<?> componentType = value.getClass().getComponentType();
      // If the array is of Object type, we need to check each element
      if (componentType == Object.class) {
        int n = Array.getLength(value);
        for (int i = 0; i < n; i++) {
          if (!test(Array.get(value, i))) return false;
        }
        return true;
      } else { // Otherwise, we can do quick O(1) check
        return validate(componentType);
      }
    }
    return false;
  }

  /**
   * Gets the size of the container, compatible to all simple types.
   * @param value The container
   * @return The size of the container (array, dictionary) or {@code 1} for scalar values
   * @param <T> The container type
   */
  public static <T> int getContainerSize(@NotNull T value) {
    if (ComplexTypes.isArray(value)) return Array.getLength(value);
    else if (value instanceof Dictionary) return ((Dictionary) value).size();
    else return 1;
  }

  /**
   * Gets the element of the container at the given index, compatible to all simple types.<br>
   * Throws {@link ArrayIndexOutOfBoundsException} if the index is out of bounds.<br>
   * For dictionary, this method gets the value using {@link Dictionary#getValueAt(int)}, not the entry.
   * @param value The container
   * @param i The index
   * @return The element of the container (array, dictionary) or the given value (scalar)
   * @param <T> The container type
   */
  @Nullable public static <T> Object getContainerElement(@Nullable T value, int i) {
    if (value == null) return null;
    if (ComplexTypes.isArray(value)) return Array.get(value, i);
    else if (value instanceof Dictionary) return ((Dictionary) value).getValueAt(i);
    else return value;
  }

  /**
   * Clones the given value recursively.
   * @param value The value
   * @return The cloned value
   * @param <T> The value type
   */
  @SuppressWarnings("unchecked")
  @Nullable public static <T> T deepClone(@Nullable T value) {
    if (value == null) return null;
    if (value instanceof String
        || value instanceof Number
        || value instanceof Boolean
        || value instanceof Character) return value; // immutable
    else if (value instanceof Dictionary) {
      return (T) ((Dictionary) value).duplicate(true);
    } else if (ComplexTypes.isArray(value)) {
      Class<?> componentType = value.getClass().getComponentType();
      int n = Array.getLength(value);
      Object result = Array.newInstance(componentType, n);
      for (int i = 0; i < n; i++) {
        Array.set(result, i, deepClone(Array.get(value, i)));
      }
      return (T) result;
    }
    throw new IllegalArgumentException(
        String.format("Object of type %s is not a simple type", value.getClass().getName()));
  }
}
