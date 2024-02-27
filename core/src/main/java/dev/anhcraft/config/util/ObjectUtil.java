package dev.anhcraft.config.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

public final class ObjectUtil {
  private static Unsafe unsafe;

  static {
    try {
      Field f = Unsafe.class.getDeclaredField("theUnsafe");
      f.setAccessible(true);
      unsafe = (Unsafe) f.get(null);
    } catch (IllegalAccessException | NoSuchFieldException ignored) {
    }
  }

  /**
   * Instantiates the given class using the default constructor. If fails, attempts to allocate an instance without
   * invoking any constructor.
   * @param clazz the class
   * @return an instance
   * @throws InstantiationException if two approaches fail
   */
  @NotNull public static Object newInstance(@NotNull Class<?> clazz) throws InstantiationException {
    try {
      return clazz.getDeclaredConstructor().newInstance();
    } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      return unsafe.allocateInstance(clazz);
    }
  }
}
